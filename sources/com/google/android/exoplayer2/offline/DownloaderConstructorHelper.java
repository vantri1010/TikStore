package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DummyDataSource;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.PriorityDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;

public final class DownloaderConstructorHelper {
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheDataSourceFactory offlineCacheDataSourceFactory;
    private final CacheDataSourceFactory onlineCacheDataSourceFactory;
    private final PriorityTaskManager priorityTaskManager;

    public DownloaderConstructorHelper(Cache cache2, DataSource.Factory upstreamFactory) {
        this(cache2, upstreamFactory, (DataSource.Factory) null, (DataSink.Factory) null, (PriorityTaskManager) null);
    }

    public DownloaderConstructorHelper(Cache cache2, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, PriorityTaskManager priorityTaskManager2) {
        this(cache2, upstreamFactory, cacheReadDataSourceFactory, cacheWriteDataSinkFactory, priorityTaskManager2, (CacheKeyFactory) null);
    }

    public DownloaderConstructorHelper(Cache cache2, DataSource.Factory upstreamFactory, DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory, PriorityTaskManager priorityTaskManager2, CacheKeyFactory cacheKeyFactory2) {
        DataSource.Factory upstreamFactory2;
        DataSink.Factory cacheWriteDataSinkFactory2;
        Cache cache3 = cache2;
        PriorityTaskManager priorityTaskManager3 = priorityTaskManager2;
        if (priorityTaskManager3 != null) {
            upstreamFactory2 = new PriorityDataSourceFactory(upstreamFactory, priorityTaskManager3, -1000);
        } else {
            upstreamFactory2 = upstreamFactory;
        }
        DataSource.Factory readDataSourceFactory = cacheReadDataSourceFactory != null ? cacheReadDataSourceFactory : new FileDataSourceFactory();
        if (cacheWriteDataSinkFactory == null) {
            CacheDataSinkFactory factory = new CacheDataSinkFactory(cache3, CacheDataSink.DEFAULT_FRAGMENT_SIZE);
            factory.experimental_setRespectCacheFragmentationFlag(true);
            cacheWriteDataSinkFactory2 = factory;
        } else {
            cacheWriteDataSinkFactory2 = cacheWriteDataSinkFactory;
        }
        this.onlineCacheDataSourceFactory = new CacheDataSourceFactory(cache2, upstreamFactory2, readDataSourceFactory, cacheWriteDataSinkFactory2, 1, (CacheDataSource.EventListener) null, cacheKeyFactory2);
        this.offlineCacheDataSourceFactory = new CacheDataSourceFactory(cache2, DummyDataSource.FACTORY, readDataSourceFactory, (DataSink.Factory) null, 1, (CacheDataSource.EventListener) null, cacheKeyFactory2);
        this.cache = cache3;
        this.priorityTaskManager = priorityTaskManager3;
        this.cacheKeyFactory = cacheKeyFactory2;
    }

    public Cache getCache() {
        return this.cache;
    }

    public CacheKeyFactory getCacheKeyFactory() {
        CacheKeyFactory cacheKeyFactory2 = this.cacheKeyFactory;
        return cacheKeyFactory2 != null ? cacheKeyFactory2 : CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
    }

    public PriorityTaskManager getPriorityTaskManager() {
        PriorityTaskManager priorityTaskManager2 = this.priorityTaskManager;
        return priorityTaskManager2 != null ? priorityTaskManager2 : new PriorityTaskManager();
    }

    public CacheDataSource createCacheDataSource() {
        return this.onlineCacheDataSourceFactory.createDataSource();
    }

    public CacheDataSource createOfflineCacheDataSource() {
        return this.offlineCacheDataSourceFactory.createDataSource();
    }
}
