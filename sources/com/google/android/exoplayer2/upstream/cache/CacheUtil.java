package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CacheUtil {
    public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
    public static final CacheKeyFactory DEFAULT_CACHE_KEY_FACTORY = $$Lambda$CacheUtil$uQzD0N2Max0h6DuMDYcCbN2peIo.INSTANCE;

    public static class CachingCounters {
        public volatile long alreadyCachedBytes;
        public volatile long contentLength = -1;
        public volatile long newlyCachedBytes;

        public long totalCachedBytes() {
            return this.alreadyCachedBytes + this.newlyCachedBytes;
        }
    }

    static /* synthetic */ String lambda$static$0(DataSpec dataSpec) {
        return dataSpec.key != null ? dataSpec.key : generateKey(dataSpec.uri);
    }

    public static String generateKey(Uri uri) {
        return uri.toString();
    }

    public static void getCached(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, CachingCounters counters) {
        long left;
        DataSpec dataSpec2 = dataSpec;
        CachingCounters cachingCounters = counters;
        String key = buildCacheKey(dataSpec2, cacheKeyFactory);
        long start = dataSpec2.absoluteStreamPosition;
        if (dataSpec2.length != -1) {
            left = dataSpec2.length;
            Cache cache2 = cache;
        } else {
            left = ContentMetadata.CC.getContentLength(cache.getContentMetadata(key));
        }
        cachingCounters.contentLength = left;
        cachingCounters.alreadyCachedBytes = 0;
        cachingCounters.newlyCachedBytes = 0;
        long start2 = start;
        long left2 = left;
        while (left2 != 0) {
            long blockLength = cache.getCachedLength(key, start2, left2 != -1 ? left2 : Long.MAX_VALUE);
            if (blockLength > 0) {
                cachingCounters.alreadyCachedBytes += blockLength;
            } else {
                blockLength = -blockLength;
                if (blockLength == Long.MAX_VALUE) {
                    return;
                }
            }
            start2 += blockLength;
            left2 -= left2 == -1 ? 0 : blockLength;
        }
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, DataSource upstream, CachingCounters counters, AtomicBoolean isCanceled) throws IOException, InterruptedException {
        cache(dataSpec, cache, cacheKeyFactory, new CacheDataSource(cache, upstream), new byte[131072], (PriorityTaskManager) null, 0, counters, isCanceled, false);
    }

    public static void cache(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory, CacheDataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters, AtomicBoolean isCanceled, boolean enableEOFException) throws IOException, InterruptedException {
        CachingCounters counters2;
        long j;
        DataSpec dataSpec2 = dataSpec;
        Cache cache2 = cache;
        CacheKeyFactory cacheKeyFactory2 = cacheKeyFactory;
        CachingCounters cachingCounters = counters;
        Assertions.checkNotNull(dataSource);
        Assertions.checkNotNull(buffer);
        if (cachingCounters != null) {
            getCached(dataSpec2, cache2, cacheKeyFactory2, cachingCounters);
            counters2 = cachingCounters;
        } else {
            counters2 = new CachingCounters();
        }
        String key = buildCacheKey(dataSpec2, cacheKeyFactory2);
        long start = dataSpec2.absoluteStreamPosition;
        if (dataSpec2.length != -1) {
            j = dataSpec2.length;
        } else {
            j = ContentMetadata.CC.getContentLength(cache2.getContentMetadata(key));
        }
        long start2 = start;
        long left = j;
        while (true) {
            long j2 = 0;
            if (left != 0) {
                throwExceptionIfInterruptedOrCancelled(isCanceled);
                long read = cache.getCachedLength(key, start2, left != -1 ? left : Long.MAX_VALUE);
                if (read <= 0) {
                    long blockLength = -read;
                    long blockLength2 = blockLength;
                    if (readAndDiscard(dataSpec, start2, blockLength, dataSource, buffer, priorityTaskManager, priority, counters2, isCanceled) >= blockLength2) {
                        read = blockLength2;
                    } else if (enableEOFException && left != -1) {
                        throw new EOFException();
                    } else {
                        return;
                    }
                }
                start2 += read;
                if (left != -1) {
                    j2 = read;
                }
                left -= j2;
            } else {
                return;
            }
        }
    }

    private static long readAndDiscard(DataSpec dataSpec, long absoluteStreamPosition, long length, DataSource dataSource, byte[] buffer, PriorityTaskManager priorityTaskManager, int priority, CachingCounters counters, AtomicBoolean isCanceled) throws IOException, InterruptedException {
        DataSpec dataSpec2;
        long resolvedLength;
        DataSource dataSource2 = dataSource;
        byte[] bArr = buffer;
        CachingCounters cachingCounters = counters;
        DataSpec dataSpec3 = dataSpec;
        while (true) {
            if (priorityTaskManager != null) {
                priorityTaskManager.proceed(priority);
            }
            try {
                throwExceptionIfInterruptedOrCancelled(isCanceled);
                dataSpec2 = new DataSpec(dataSpec3.uri, dataSpec3.httpMethod, dataSpec3.httpBody, absoluteStreamPosition, (dataSpec3.position + absoluteStreamPosition) - dataSpec3.absoluteStreamPosition, -1, dataSpec3.key, dataSpec3.flags);
                resolvedLength = dataSource2.open(dataSpec2);
                break;
            } catch (PriorityTaskManager.PriorityTooLowException e) {
            } finally {
                Util.closeQuietly(dataSource);
            }
        }
        if (cachingCounters.contentLength == -1 && resolvedLength != -1) {
            cachingCounters.contentLength = dataSpec2.absoluteStreamPosition + resolvedLength;
        }
        long totalRead = 0;
        while (true) {
            if (totalRead == length) {
                break;
            }
            throwExceptionIfInterruptedOrCancelled(isCanceled);
            int read = dataSource2.read(bArr, 0, length != -1 ? (int) Math.min((long) bArr.length, length - totalRead) : bArr.length);
            if (read != -1) {
                totalRead += (long) read;
                cachingCounters.newlyCachedBytes += (long) read;
            } else if (cachingCounters.contentLength == -1) {
                cachingCounters.contentLength = dataSpec2.absoluteStreamPosition + totalRead;
            }
        }
        return totalRead;
    }

    public static void remove(DataSpec dataSpec, Cache cache, CacheKeyFactory cacheKeyFactory) {
        remove(cache, buildCacheKey(dataSpec, cacheKeyFactory));
    }

    public static void remove(Cache cache, String key) {
        for (CacheSpan cachedSpan : cache.getCachedSpans(key)) {
            try {
                cache.removeSpan(cachedSpan);
            } catch (Cache.CacheException e) {
            }
        }
    }

    private static String buildCacheKey(DataSpec dataSpec, CacheKeyFactory cacheKeyFactory) {
        return (cacheKeyFactory != null ? cacheKeyFactory : DEFAULT_CACHE_KEY_FACTORY).buildCacheKey(dataSpec);
    }

    private static void throwExceptionIfInterruptedOrCancelled(AtomicBoolean isCanceled) throws InterruptedException {
        if (Thread.interrupted() || (isCanceled != null && isCanceled.get())) {
            throw new InterruptedException();
        }
    }

    private CacheUtil() {
    }
}
