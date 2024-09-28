package com.danikula.videocache;

import android.text.TextUtils;
import com.danikula.videocache.headers.EmptyHeadersInjector;
import com.danikula.videocache.headers.HeaderInjector;
import com.danikula.videocache.sourcestorage.SourceInfoStorage;
import com.danikula.videocache.sourcestorage.SourceInfoStorageFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUrlSource implements Source {
    private static final Logger LOG = LoggerFactory.getLogger("HttpUrlSource");
    private static final int MAX_REDIRECTS = 5;
    private HttpURLConnection connection;
    private final HeaderInjector headerInjector;
    private InputStream inputStream;
    private SourceInfo sourceInfo;
    private final SourceInfoStorage sourceInfoStorage;

    public HttpUrlSource(String url) {
        this(url, SourceInfoStorageFactory.newEmptySourceInfoStorage());
    }

    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage2) {
        this(url, sourceInfoStorage2, new EmptyHeadersInjector());
    }

    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage2, HeaderInjector headerInjector2) {
        SourceInfo sourceInfo2;
        this.sourceInfoStorage = (SourceInfoStorage) Preconditions.checkNotNull(sourceInfoStorage2);
        this.headerInjector = (HeaderInjector) Preconditions.checkNotNull(headerInjector2);
        SourceInfo sourceInfo3 = sourceInfoStorage2.get(url);
        if (sourceInfo3 != null) {
            sourceInfo2 = sourceInfo3;
        } else {
            sourceInfo2 = new SourceInfo(url, -2147483648L, ProxyCacheUtils.getSupposablyMime(url));
        }
        this.sourceInfo = sourceInfo2;
    }

    public HttpUrlSource(HttpUrlSource source) {
        this.sourceInfo = source.sourceInfo;
        this.sourceInfoStorage = source.sourceInfoStorage;
        this.headerInjector = source.headerInjector;
    }

    public synchronized long length() throws ProxyCacheException {
        if (this.sourceInfo.length == -2147483648L) {
            fetchContentInfo();
        }
        return this.sourceInfo.length;
    }

    public void open(long offset) throws ProxyCacheException {
        try {
            HttpURLConnection openConnection = openConnection(offset, -1);
            this.connection = openConnection;
            String mime = openConnection.getContentType();
            this.inputStream = new BufferedInputStream(this.connection.getInputStream(), 8192);
            SourceInfo sourceInfo2 = new SourceInfo(this.sourceInfo.url, readSourceAvailableBytes(this.connection, offset, this.connection.getResponseCode()), mime);
            this.sourceInfo = sourceInfo2;
            this.sourceInfoStorage.put(sourceInfo2.url, this.sourceInfo);
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening connection for " + this.sourceInfo.url + " with offset " + offset, e);
        }
    }

    private long readSourceAvailableBytes(HttpURLConnection connection2, long offset, int responseCode) throws IOException {
        long contentLength = getContentLength(connection2);
        if (responseCode == 200) {
            return contentLength;
        }
        return responseCode == 206 ? contentLength + offset : this.sourceInfo.length;
    }

    private long getContentLength(HttpURLConnection connection2) {
        String contentLengthValue = connection2.getHeaderField("Content-Length");
        if (contentLengthValue == null) {
            return -1;
        }
        return Long.parseLong(contentLengthValue);
    }

    public void close() throws ProxyCacheException {
        HttpURLConnection httpURLConnection = this.connection;
        if (httpURLConnection != null) {
            try {
                httpURLConnection.disconnect();
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new RuntimeException("Wait... but why? WTF!? Really shouldn't happen any more after fixing https://github.com/danikula/AndroidVideoCache/issues/43. If you read it on your device log, please, notify me danikula@gmail.com or create issue here https://github.com/danikula/AndroidVideoCache/issues.", e);
            } catch (ArrayIndexOutOfBoundsException e2) {
                LOG.error("Error closing connection correctly. Should happen only on Android L. If anybody know how to fix it, please visit https://github.com/danikula/AndroidVideoCache/issues/88. Until good solution is not know, just ignore this issue :(", (Throwable) e2);
            }
        }
    }

    public int read(byte[] buffer) throws ProxyCacheException {
        InputStream inputStream2 = this.inputStream;
        if (inputStream2 != null) {
            try {
                return inputStream2.read(buffer, 0, buffer.length);
            } catch (InterruptedIOException e) {
                throw new InterruptedProxyCacheException("Reading source " + this.sourceInfo.url + " is interrupted", e);
            } catch (IOException e2) {
                throw new ProxyCacheException("Error reading data from " + this.sourceInfo.url, e2);
            }
        } else {
            throw new ProxyCacheException("Error reading data from " + this.sourceInfo.url + ": connection is absent!");
        }
    }

    private void fetchContentInfo() throws ProxyCacheException {
        Logger logger = LOG;
        logger.debug("Read content info from " + this.sourceInfo.url);
        HttpURLConnection urlConnection = null;
        InputStream inputStream2 = null;
        try {
            urlConnection = openConnection(0, 10000);
            long length = getContentLength(urlConnection);
            String mime = urlConnection.getContentType();
            inputStream2 = urlConnection.getInputStream();
            SourceInfo sourceInfo2 = new SourceInfo(this.sourceInfo.url, length, mime);
            this.sourceInfo = sourceInfo2;
            this.sourceInfoStorage.put(sourceInfo2.url, this.sourceInfo);
            Logger logger2 = LOG;
            logger2.debug("Source info fetched: " + this.sourceInfo);
            ProxyCacheUtils.close(inputStream2);
            if (urlConnection == null) {
                return;
            }
        } catch (IOException e) {
            Logger logger3 = LOG;
            logger3.error("Error fetching info from " + this.sourceInfo.url, (Throwable) e);
            ProxyCacheUtils.close(inputStream2);
            if (urlConnection == null) {
                return;
            }
        } catch (Throwable th) {
            ProxyCacheUtils.close(inputStream2);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            throw th;
        }
        urlConnection.disconnect();
    }

    private HttpURLConnection openConnection(long offset, int timeout) throws IOException, ProxyCacheException {
        String str;
        HttpURLConnection connection2;
        boolean redirected;
        int redirectCount = 0;
        String url = this.sourceInfo.url;
        do {
            Logger logger = LOG;
            StringBuilder sb = new StringBuilder();
            sb.append("Open connection ");
            if (offset > 0) {
                str = " with offset " + offset;
            } else {
                str = "";
            }
            sb.append(str);
            sb.append(" to ");
            sb.append(url);
            logger.debug(sb.toString());
            connection2 = (HttpURLConnection) new URL(url).openConnection();
            injectCustomHeaders(connection2, url);
            if (offset > 0) {
                connection2.setRequestProperty("Range", "bytes=" + offset + "-");
            }
            if (timeout > 0) {
                connection2.setConnectTimeout(timeout);
                connection2.setReadTimeout(timeout);
            }
            int code = connection2.getResponseCode();
            redirected = code == 301 || code == 302 || code == 303;
            if (redirected) {
                url = connection2.getHeaderField("Location");
                redirectCount++;
                connection2.disconnect();
            }
            if (redirectCount > 5) {
                throw new ProxyCacheException("Too many redirects: " + redirectCount);
            }
        } while (redirected);
        return connection2;
    }

    private void injectCustomHeaders(HttpURLConnection connection2, String url) {
        for (Map.Entry<String, String> header : this.headerInjector.addHeaders(url).entrySet()) {
            connection2.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    public synchronized String getMime() throws ProxyCacheException {
        if (TextUtils.isEmpty(this.sourceInfo.mime)) {
            fetchContentInfo();
        }
        return this.sourceInfo.mime;
    }

    public String getUrl() {
        return this.sourceInfo.url;
    }

    public String toString() {
        return "HttpUrlSource{sourceInfo='" + this.sourceInfo + "}";
    }
}
