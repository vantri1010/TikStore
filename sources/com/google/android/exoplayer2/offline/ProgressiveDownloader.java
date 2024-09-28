package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ProgressiveDownloader implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheUtil.CachingCounters cachingCounters = new CacheUtil.CachingCounters();
    private final CacheDataSource dataSource;
    private final DataSpec dataSpec;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final PriorityTaskManager priorityTaskManager;

    public ProgressiveDownloader(Uri uri, String customCacheKey, DownloaderConstructorHelper constructorHelper) {
        this.dataSpec = new DataSpec(uri, 0, -1, customCacheKey, 16);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.createCacheDataSource();
        this.cacheKeyFactory = constructorHelper.getCacheKeyFactory();
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
    }

    public void download() throws InterruptedException, IOException {
        this.priorityTaskManager.add(-1000);
        try {
            CacheUtil.cache(this.dataSpec, this.cache, this.cacheKeyFactory, this.dataSource, new byte[131072], this.priorityTaskManager, -1000, this.cachingCounters, this.isCanceled, true);
        } finally {
            this.priorityTaskManager.remove(-1000);
        }
    }

    public void cancel() {
        this.isCanceled.set(true);
    }

    public long getDownloadedBytes() {
        return this.cachingCounters.totalCachedBytes();
    }

    public long getTotalBytes() {
        return this.cachingCounters.contentLength;
    }

    public float getDownloadPercentage() {
        long contentLength = this.cachingCounters.contentLength;
        if (contentLength == -1) {
            return -1.0f;
        }
        return (((float) this.cachingCounters.totalCachedBytes()) * 100.0f) / ((float) contentLength);
    }

    public void remove() {
        CacheUtil.remove(this.dataSpec, this.cache, this.cacheKeyFactory);
    }
}
