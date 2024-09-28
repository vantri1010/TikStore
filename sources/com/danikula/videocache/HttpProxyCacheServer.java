package com.danikula.videocache;

import android.content.Context;
import android.net.Uri;
import com.danikula.videocache.file.DiskUsage;
import com.danikula.videocache.file.FileNameGenerator;
import com.danikula.videocache.file.Md5FileNameGenerator;
import com.danikula.videocache.file.TotalCountLruDiskUsage;
import com.danikula.videocache.file.TotalSizeLruDiskUsage;
import com.danikula.videocache.headers.EmptyHeadersInjector;
import com.danikula.videocache.headers.HeaderInjector;
import com.danikula.videocache.sourcestorage.SourceInfoStorage;
import com.danikula.videocache.sourcestorage.SourceInfoStorageFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyCacheServer {
    private static final Logger LOG = LoggerFactory.getLogger("HttpProxyCacheServer");
    private static final String PROXY_HOST = "127.0.0.1";
    private final Object clientsLock;
    private final Map<String, HttpProxyCacheServerClients> clientsMap;
    private final Config config;
    private final Pinger pinger;
    private final int port;
    private final ServerSocket serverSocket;
    private final ExecutorService socketProcessor;
    private final Thread waitConnectionThread;

    public HttpProxyCacheServer(Context context) {
        this(new Builder(context).buildConfig());
    }

    private HttpProxyCacheServer(Config config2) {
        this.clientsLock = new Object();
        this.socketProcessor = Executors.newFixedThreadPool(8);
        this.clientsMap = new ConcurrentHashMap();
        this.config = (Config) Preconditions.checkNotNull(config2);
        try {
            ServerSocket serverSocket2 = new ServerSocket(0, 8, InetAddress.getByName(PROXY_HOST));
            this.serverSocket = serverSocket2;
            int localPort = serverSocket2.getLocalPort();
            this.port = localPort;
            IgnoreHostProxySelector.install(PROXY_HOST, localPort);
            CountDownLatch startSignal = new CountDownLatch(1);
            Thread thread = new Thread(new WaitRequestsRunnable(startSignal));
            this.waitConnectionThread = thread;
            thread.start();
            startSignal.await();
            this.pinger = new Pinger(PROXY_HOST, this.port);
            Logger logger = LOG;
            logger.info("Proxy cache server started. Is it alive? " + isAlive());
        } catch (IOException | InterruptedException e) {
            this.socketProcessor.shutdown();
            throw new IllegalStateException("Error starting local proxy server", e);
        }
    }

    public String getProxyUrl(String url) {
        return getProxyUrl(url, true);
    }

    public String getProxyUrl(String url, boolean allowCachedFileUri) {
        if (!allowCachedFileUri || !isCached(url)) {
            return isAlive() ? appendToProxyUrl(url) : url;
        }
        File cacheFile = getCacheFile(url);
        touchFileSafely(cacheFile);
        return Uri.fromFile(cacheFile).toString();
    }

    public void registerCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(cacheListener, url);
        synchronized (this.clientsLock) {
            try {
                getClients(url).registerCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                LOG.warn("Error registering cache listener", (Throwable) e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener, String url) {
        Preconditions.checkAllNotNull(cacheListener, url);
        synchronized (this.clientsLock) {
            try {
                getClients(url).unregisterCacheListener(cacheListener);
            } catch (ProxyCacheException e) {
                LOG.warn("Error registering cache listener", (Throwable) e);
            }
        }
    }

    public void unregisterCacheListener(CacheListener cacheListener) {
        Preconditions.checkNotNull(cacheListener);
        synchronized (this.clientsLock) {
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                clients.unregisterCacheListener(cacheListener);
            }
        }
    }

    public boolean isCached(String url) {
        Preconditions.checkNotNull(url, "Url can't be null!");
        return getCacheFile(url).exists();
    }

    public void shutdown() {
        LOG.info("Shutdown proxy server");
        shutdownClients();
        this.config.sourceInfoStorage.release();
        this.waitConnectionThread.interrupt();
        try {
            if (!this.serverSocket.isClosed()) {
                this.serverSocket.close();
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error shutting down proxy server", e));
        }
    }

    private boolean isAlive() {
        return this.pinger.ping(3, 70);
    }

    private String appendToProxyUrl(String url) {
        return String.format(Locale.US, "http://%s:%d/%s", new Object[]{PROXY_HOST, Integer.valueOf(this.port), ProxyCacheUtils.encode(url)});
    }

    private File getCacheFile(String url) {
        return new File(this.config.cacheRoot, this.config.fileNameGenerator.generate(url));
    }

    private void touchFileSafely(File cacheFile) {
        try {
            this.config.diskUsage.touch(cacheFile);
        } catch (IOException e) {
            Logger logger = LOG;
            logger.error("Error touching file " + cacheFile, (Throwable) e);
        }
    }

    private void shutdownClients() {
        synchronized (this.clientsLock) {
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                clients.shutdown();
            }
            this.clientsMap.clear();
        }
    }

    /* access modifiers changed from: private */
    public void waitForRequest() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = this.serverSocket.accept();
                Logger logger = LOG;
                logger.debug("Accept new socket " + socket);
                this.socketProcessor.submit(new SocketProcessorRunnable(socket));
            } catch (IOException e) {
                onError(new ProxyCacheException("Error during waiting connection", e));
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void processSocket(Socket socket) {
        StringBuilder sb;
        Logger logger;
        try {
            GetRequest request = GetRequest.read(socket.getInputStream());
            Logger logger2 = LOG;
            logger2.debug("Request to cache proxy:" + request);
            String url = ProxyCacheUtils.decode(request.uri);
            if (this.pinger.isPingRequest(url)) {
                this.pinger.responseToPing(socket);
            } else {
                getClients(url).processRequest(request, socket);
            }
            releaseSocket(socket);
            logger = LOG;
            sb = new StringBuilder();
        } catch (SocketException e) {
            LOG.debug("Closing socket… Socket is closed by client.");
            releaseSocket(socket);
            logger = LOG;
            sb = new StringBuilder();
        } catch (ProxyCacheException | IOException e2) {
            onError(new ProxyCacheException("Error processing request", e2));
            releaseSocket(socket);
            logger = LOG;
            sb = new StringBuilder();
        } catch (Throwable th) {
            releaseSocket(socket);
            Logger logger3 = LOG;
            logger3.debug("Opened connections: " + getClientsCount());
            throw th;
        }
        sb.append("Opened connections: ");
        sb.append(getClientsCount());
        logger.debug(sb.toString());
    }

    private HttpProxyCacheServerClients getClients(String url) throws ProxyCacheException {
        HttpProxyCacheServerClients clients;
        synchronized (this.clientsLock) {
            clients = this.clientsMap.get(url);
            if (clients == null) {
                clients = new HttpProxyCacheServerClients(url, this.config);
                this.clientsMap.put(url, clients);
            }
        }
        return clients;
    }

    private int getClientsCount() {
        int count;
        synchronized (this.clientsLock) {
            count = 0;
            for (HttpProxyCacheServerClients clients : this.clientsMap.values()) {
                count += clients.getClientsCount();
            }
        }
        return count;
    }

    private void releaseSocket(Socket socket) {
        closeSocketInput(socket);
        closeSocketOutput(socket);
        closeSocket(socket);
    }

    private void closeSocketInput(Socket socket) {
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }
        } catch (SocketException e) {
            LOG.debug("Releasing input stream… Socket is closed by client.");
        } catch (IOException e2) {
            onError(new ProxyCacheException("Error closing socket input stream", e2));
        }
    }

    private void closeSocketOutput(Socket socket) {
        try {
            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        } catch (IOException e) {
            LOG.warn("Failed to close socket on proxy side: {}. It seems client have already closed connection.", (Object) e.getMessage());
        }
    }

    private void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            onError(new ProxyCacheException("Error closing socket", e));
        }
    }

    private void onError(Throwable e) {
        LOG.error("HttpProxyCacheServer error", e);
    }

    private final class WaitRequestsRunnable implements Runnable {
        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal2) {
            this.startSignal = startSignal2;
        }

        public void run() {
            this.startSignal.countDown();
            HttpProxyCacheServer.this.waitForRequest();
        }
    }

    private final class SocketProcessorRunnable implements Runnable {
        private final Socket socket;

        public SocketProcessorRunnable(Socket socket2) {
            this.socket = socket2;
        }

        public void run() {
            HttpProxyCacheServer.this.processSocket(this.socket);
        }
    }

    public static final class Builder {
        private static final long DEFAULT_MAX_SIZE = 536870912;
        private File cacheRoot;
        private DiskUsage diskUsage = new TotalSizeLruDiskUsage(DEFAULT_MAX_SIZE);
        private FileNameGenerator fileNameGenerator = new Md5FileNameGenerator();
        private HeaderInjector headerInjector = new EmptyHeadersInjector();
        private SourceInfoStorage sourceInfoStorage;

        public Builder(Context context) {
            this.sourceInfoStorage = SourceInfoStorageFactory.newSourceInfoStorage(context);
            this.cacheRoot = StorageUtils.getIndividualCacheDirectory(context);
        }

        public Builder cacheDirectory(File file) {
            this.cacheRoot = (File) Preconditions.checkNotNull(file);
            return this;
        }

        public Builder fileNameGenerator(FileNameGenerator fileNameGenerator2) {
            this.fileNameGenerator = (FileNameGenerator) Preconditions.checkNotNull(fileNameGenerator2);
            return this;
        }

        public Builder maxCacheSize(long maxSize) {
            this.diskUsage = new TotalSizeLruDiskUsage(maxSize);
            return this;
        }

        public Builder maxCacheFilesCount(int count) {
            this.diskUsage = new TotalCountLruDiskUsage(count);
            return this;
        }

        public Builder diskUsage(DiskUsage diskUsage2) {
            this.diskUsage = (DiskUsage) Preconditions.checkNotNull(diskUsage2);
            return this;
        }

        public Builder headerInjector(HeaderInjector headerInjector2) {
            this.headerInjector = (HeaderInjector) Preconditions.checkNotNull(headerInjector2);
            return this;
        }

        public HttpProxyCacheServer build() {
            return new HttpProxyCacheServer(buildConfig());
        }

        /* access modifiers changed from: private */
        public Config buildConfig() {
            return new Config(this.cacheRoot, this.fileNameGenerator, this.diskUsage, this.sourceInfoStorage, this.headerInjector);
        }
    }
}
