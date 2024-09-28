package com.danikula.videocache;

import java.lang.Thread;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProxyCache {
    private static final Logger LOG = LoggerFactory.getLogger("ProxyCache");
    private static final int MAX_READ_SOURCE_ATTEMPTS = 1;
    private final Cache cache;
    private volatile int percentsAvailable = -1;
    private final AtomicInteger readSourceErrorsCount;
    private final Source source;
    private volatile Thread sourceReaderThread;
    private final Object stopLock = new Object();
    private volatile boolean stopped;
    private final Object wc = new Object();

    public ProxyCache(Source source2, Cache cache2) {
        this.source = (Source) Preconditions.checkNotNull(source2);
        this.cache = (Cache) Preconditions.checkNotNull(cache2);
        this.readSourceErrorsCount = new AtomicInteger();
    }

    public int read(byte[] buffer, long offset, int length) throws ProxyCacheException {
        ProxyCacheUtils.assertBuffer(buffer, offset, length);
        while (!this.cache.isCompleted() && this.cache.available() < ((long) length) + offset && !this.stopped) {
            readSourceAsync();
            waitForSourceData();
            checkReadSourceErrorsCount();
        }
        int read = this.cache.read(buffer, offset, length);
        if (this.cache.isCompleted() && this.percentsAvailable != 100) {
            this.percentsAvailable = 100;
            onCachePercentsAvailableChanged(100);
        }
        return read;
    }

    private void checkReadSourceErrorsCount() throws ProxyCacheException {
        int errorsCount = this.readSourceErrorsCount.get();
        if (errorsCount >= 1) {
            this.readSourceErrorsCount.set(0);
            throw new ProxyCacheException("Error reading source " + errorsCount + " times");
        }
    }

    public void shutdown() {
        synchronized (this.stopLock) {
            Logger logger = LOG;
            logger.debug("Shutdown proxy for " + this.source);
            try {
                this.stopped = true;
                if (this.sourceReaderThread != null) {
                    this.sourceReaderThread.interrupt();
                }
                this.cache.close();
            } catch (ProxyCacheException e) {
                onError(e);
            }
        }
    }

    private synchronized void readSourceAsync() throws ProxyCacheException {
        boolean readingInProgress = (this.sourceReaderThread == null || this.sourceReaderThread.getState() == Thread.State.TERMINATED) ? false : true;
        if (!this.stopped && !this.cache.isCompleted() && !readingInProgress) {
            SourceReaderRunnable sourceReaderRunnable = new SourceReaderRunnable();
            this.sourceReaderThread = new Thread(sourceReaderRunnable, "Source reader for " + this.source);
            this.sourceReaderThread.start();
        }
    }

    private void waitForSourceData() throws ProxyCacheException {
        synchronized (this.wc) {
            try {
                this.wc.wait(1000);
            } catch (InterruptedException e) {
                throw new ProxyCacheException("Waiting source data is interrupted!", e);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void notifyNewCacheDataAvailable(long cacheAvailable, long sourceAvailable) {
        onCacheAvailable(cacheAvailable, sourceAvailable);
        synchronized (this.wc) {
            this.wc.notifyAll();
        }
    }

    /* access modifiers changed from: protected */
    public void onCacheAvailable(long cacheAvailable, long sourceLength) {
        boolean sourceLengthKnown = true;
        int percents = (sourceLength > 0 ? 1 : (sourceLength == 0 ? 0 : -1)) == 0 ? 100 : (int) ((((float) cacheAvailable) / ((float) sourceLength)) * 100.0f);
        boolean percentsChanged = percents != this.percentsAvailable;
        if (sourceLength < 0) {
            sourceLengthKnown = false;
        }
        if (sourceLengthKnown && percentsChanged) {
            onCachePercentsAvailableChanged(percents);
        }
        this.percentsAvailable = percents;
    }

    /* access modifiers changed from: protected */
    public void onCachePercentsAvailableChanged(int percentsAvailable2) {
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003c, code lost:
        r2 = r2 + ((long) r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readSource() {
        /*
            r9 = this;
            r0 = -1
            r2 = 0
            com.danikula.videocache.Cache r4 = r9.cache     // Catch:{ all -> 0x004c }
            long r4 = r4.available()     // Catch:{ all -> 0x004c }
            r2 = r4
            com.danikula.videocache.Source r4 = r9.source     // Catch:{ all -> 0x004c }
            r4.open(r2)     // Catch:{ all -> 0x004c }
            com.danikula.videocache.Source r4 = r9.source     // Catch:{ all -> 0x004c }
            long r4 = r4.length()     // Catch:{ all -> 0x004c }
            r0 = r4
            r4 = 8192(0x2000, float:1.14794E-41)
            byte[] r4 = new byte[r4]     // Catch:{ all -> 0x004c }
        L_0x001b:
            com.danikula.videocache.Source r5 = r9.source     // Catch:{ all -> 0x004c }
            int r5 = r5.read(r4)     // Catch:{ all -> 0x004c }
            r6 = r5
            r7 = -1
            if (r5 == r7) goto L_0x0045
            java.lang.Object r5 = r9.stopLock     // Catch:{ all -> 0x004c }
            monitor-enter(r5)     // Catch:{ all -> 0x004c }
            boolean r7 = r9.isStopped()     // Catch:{ all -> 0x0042 }
            if (r7 == 0) goto L_0x0036
            monitor-exit(r5)     // Catch:{ all -> 0x0042 }
            r9.closeSource()
            r9.notifyNewCacheDataAvailable(r2, r0)
            return
        L_0x0036:
            com.danikula.videocache.Cache r7 = r9.cache     // Catch:{ all -> 0x0042 }
            r7.append(r4, r6)     // Catch:{ all -> 0x0042 }
            monitor-exit(r5)     // Catch:{ all -> 0x0042 }
            long r7 = (long) r6
            long r2 = r2 + r7
            r9.notifyNewCacheDataAvailable(r2, r0)     // Catch:{ all -> 0x004c }
            goto L_0x001b
        L_0x0042:
            r7 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0042 }
            throw r7     // Catch:{ all -> 0x004c }
        L_0x0045:
            r9.tryComplete()     // Catch:{ all -> 0x004c }
            r9.onSourceRead()     // Catch:{ all -> 0x004c }
            goto L_0x0055
        L_0x004c:
            r4 = move-exception
            java.util.concurrent.atomic.AtomicInteger r5 = r9.readSourceErrorsCount     // Catch:{ all -> 0x005d }
            r5.incrementAndGet()     // Catch:{ all -> 0x005d }
            r9.onError(r4)     // Catch:{ all -> 0x005d }
        L_0x0055:
            r9.closeSource()
            r9.notifyNewCacheDataAvailable(r2, r0)
            return
        L_0x005d:
            r4 = move-exception
            r9.closeSource()
            r9.notifyNewCacheDataAvailable(r2, r0)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.danikula.videocache.ProxyCache.readSource():void");
    }

    private void onSourceRead() {
        this.percentsAvailable = 100;
        onCachePercentsAvailableChanged(this.percentsAvailable);
    }

    private void tryComplete() throws ProxyCacheException {
        synchronized (this.stopLock) {
            if (!isStopped() && this.cache.available() == this.source.length()) {
                this.cache.complete();
            }
        }
    }

    private boolean isStopped() {
        return Thread.currentThread().isInterrupted() || this.stopped;
    }

    private void closeSource() {
        try {
            this.source.close();
        } catch (ProxyCacheException e) {
            onError(new ProxyCacheException("Error closing source " + this.source, e));
        }
    }

    /* access modifiers changed from: protected */
    public final void onError(Throwable e) {
        if (e instanceof InterruptedProxyCacheException) {
            LOG.debug("ProxyCache is interrupted");
        } else {
            LOG.error("ProxyCache error", e);
        }
    }

    private class SourceReaderRunnable implements Runnable {
        private SourceReaderRunnable() {
        }

        public void run() {
            ProxyCache.this.readSource();
        }
    }
}
