package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;

public final class CacheDataSourceFactory implements DataSource.Factory {
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final DataSource.Factory cacheReadDataSourceFactory;
    private final DataSink.Factory cacheWriteDataSinkFactory;
    private final CacheDataSource.EventListener eventListener;
    private final int flags;
    private final DataSource.Factory upstreamFactory;

    public CacheDataSourceFactory(Cache cache2, DataSource.Factory upstreamFactory2) {
        this(cache2, upstreamFactory2, 0);
    }

    public CacheDataSourceFactory(Cache cache2, DataSource.Factory upstreamFactory2, int flags2) {
        this(cache2, upstreamFactory2, new FileDataSourceFactory(), new CacheDataSinkFactory(cache2, CacheDataSink.DEFAULT_FRAGMENT_SIZE), flags2, (CacheDataSource.EventListener) null);
    }

    public CacheDataSourceFactory(Cache cache2, DataSource.Factory upstreamFactory2, DataSource.Factory cacheReadDataSourceFactory2, DataSink.Factory cacheWriteDataSinkFactory2, int flags2, CacheDataSource.EventListener eventListener2) {
        this(cache2, upstreamFactory2, cacheReadDataSourceFactory2, cacheWriteDataSinkFactory2, flags2, eventListener2, (CacheKeyFactory) null);
    }

    public CacheDataSourceFactory(Cache cache2, DataSource.Factory upstreamFactory2, DataSource.Factory cacheReadDataSourceFactory2, DataSink.Factory cacheWriteDataSinkFactory2, int flags2, CacheDataSource.EventListener eventListener2, CacheKeyFactory cacheKeyFactory2) {
        this.cache = cache2;
        this.upstreamFactory = upstreamFactory2;
        this.cacheReadDataSourceFactory = cacheReadDataSourceFactory2;
        this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory2;
        this.flags = flags2;
        this.eventListener = eventListener2;
        this.cacheKeyFactory = cacheKeyFactory2;
    }

    public CacheDataSource createDataSource() {
        DataSink dataSink;
        Cache cache2 = this.cache;
        DataSource createDataSource = this.upstreamFactory.createDataSource();
        DataSource createDataSource2 = this.cacheReadDataSourceFactory.createDataSource();
        DataSink.Factory factory = this.cacheWriteDataSinkFactory;
        if (factory == null) {
            dataSink = null;
        } else {
            dataSink = factory.createDataSink();
        }
        return new CacheDataSource(cache2, createDataSource, createDataSource2, dataSink, this.flags, this.eventListener, this.cacheKeyFactory);
    }
}
