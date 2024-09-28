package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceException;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TeeDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CacheDataSource implements DataSource {
    public static final int CACHE_IGNORED_REASON_ERROR = 0;
    public static final int CACHE_IGNORED_REASON_UNSET_LENGTH = 1;
    private static final int CACHE_NOT_IGNORED = -1;
    public static final int FLAG_BLOCK_ON_CACHE = 1;
    public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
    public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
    private static final long MIN_READ_BEFORE_CHECKING_CACHE = 102400;
    private Uri actualUri;
    private final boolean blockOnCache;
    private long bytesRemaining;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final DataSource cacheReadDataSource;
    private final DataSource cacheWriteDataSource;
    private long checkCachePosition;
    private DataSource currentDataSource;
    private boolean currentDataSpecLengthUnset;
    private CacheSpan currentHoleSpan;
    private boolean currentRequestIgnoresCache;
    private final EventListener eventListener;
    private int flags;
    private int httpMethod;
    private final boolean ignoreCacheForUnsetLengthRequests;
    private final boolean ignoreCacheOnError;
    private String key;
    private long readPosition;
    private boolean seenCacheError;
    private long totalCachedBytesRead;
    private final DataSource upstreamDataSource;
    private Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheIgnoredReason {
    }

    public interface EventListener {
        void onCacheIgnored(int i);

        void onCachedBytesRead(long j, long j2);
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public CacheDataSource(Cache cache2, DataSource upstream) {
        this(cache2, upstream, 0);
    }

    public CacheDataSource(Cache cache2, DataSource upstream, int flags2) {
        this(cache2, upstream, new FileDataSource(), new CacheDataSink(cache2, CacheDataSink.DEFAULT_FRAGMENT_SIZE), flags2, (EventListener) null);
    }

    public CacheDataSource(Cache cache2, DataSource upstream, DataSource cacheReadDataSource2, DataSink cacheWriteDataSink, int flags2, EventListener eventListener2) {
        this(cache2, upstream, cacheReadDataSource2, cacheWriteDataSink, flags2, eventListener2, (CacheKeyFactory) null);
    }

    public CacheDataSource(Cache cache2, DataSource upstream, DataSource cacheReadDataSource2, DataSink cacheWriteDataSink, int flags2, EventListener eventListener2, CacheKeyFactory cacheKeyFactory2) {
        this.cache = cache2;
        this.cacheReadDataSource = cacheReadDataSource2;
        this.cacheKeyFactory = cacheKeyFactory2 != null ? cacheKeyFactory2 : CacheUtil.DEFAULT_CACHE_KEY_FACTORY;
        boolean z = false;
        this.blockOnCache = (flags2 & 1) != 0;
        this.ignoreCacheOnError = (flags2 & 2) != 0;
        this.ignoreCacheForUnsetLengthRequests = (flags2 & 4) != 0 ? true : z;
        this.upstreamDataSource = upstream;
        if (cacheWriteDataSink != null) {
            this.cacheWriteDataSource = new TeeDataSource(upstream, cacheWriteDataSink);
        } else {
            this.cacheWriteDataSource = null;
        }
        this.eventListener = eventListener2;
    }

    public void addTransferListener(TransferListener transferListener) {
        this.cacheReadDataSource.addTransferListener(transferListener);
        this.upstreamDataSource.addTransferListener(transferListener);
    }

    public long open(DataSpec dataSpec) throws IOException {
        try {
            this.key = this.cacheKeyFactory.buildCacheKey(dataSpec);
            Uri uri2 = dataSpec.uri;
            this.uri = uri2;
            this.actualUri = getRedirectedUriOrDefault(this.cache, this.key, uri2);
            this.httpMethod = dataSpec.httpMethod;
            this.flags = dataSpec.flags;
            this.readPosition = dataSpec.position;
            int reason = shouldIgnoreCacheForRequest(dataSpec);
            boolean z = reason != -1;
            this.currentRequestIgnoresCache = z;
            if (z) {
                notifyCacheIgnored(reason);
            }
            if (dataSpec.length == -1) {
                if (!this.currentRequestIgnoresCache) {
                    long contentLength = ContentMetadata.CC.getContentLength(this.cache.getContentMetadata(this.key));
                    this.bytesRemaining = contentLength;
                    if (contentLength != -1) {
                        long j = contentLength - dataSpec.position;
                        this.bytesRemaining = j;
                        if (j <= 0) {
                            throw new DataSourceException(0);
                        }
                    }
                    openNextSource(false);
                    return this.bytesRemaining;
                }
            }
            this.bytesRemaining = dataSpec.length;
            openNextSource(false);
            return this.bytesRemaining;
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            if (this.readPosition >= this.checkCachePosition) {
                openNextSource(true);
            }
            int bytesRead = this.currentDataSource.read(buffer, offset, readLength);
            if (bytesRead != -1) {
                if (isReadingFromCache()) {
                    this.totalCachedBytesRead += (long) bytesRead;
                }
                this.readPosition += (long) bytesRead;
                if (this.bytesRemaining != -1) {
                    this.bytesRemaining -= (long) bytesRead;
                }
            } else if (this.currentDataSpecLengthUnset) {
                setNoBytesRemainingAndMaybeStoreLength();
            } else {
                if (this.bytesRemaining <= 0) {
                    if (this.bytesRemaining == -1) {
                    }
                }
                closeCurrentSource();
                openNextSource(false);
                return read(buffer, offset, readLength);
            }
            return bytesRead;
        } catch (IOException e) {
            if (!this.currentDataSpecLengthUnset || !isCausedByPositionOutOfRange(e)) {
                handleBeforeThrow(e);
                throw e;
            }
            setNoBytesRemainingAndMaybeStoreLength();
            return -1;
        }
    }

    public Uri getUri() {
        return this.actualUri;
    }

    public Map<String, List<String>> getResponseHeaders() {
        if (isReadingFromUpstream()) {
            return this.upstreamDataSource.getResponseHeaders();
        }
        return Collections.emptyMap();
    }

    public void close() throws IOException {
        this.uri = null;
        this.actualUri = null;
        this.httpMethod = 1;
        notifyBytesRead();
        try {
            closeCurrentSource();
        } catch (IOException e) {
            handleBeforeThrow(e);
            throw e;
        }
    }

    private void openNextSource(boolean checkCache) throws IOException {
        CacheSpan nextSpan;
        DataSource nextDataSource;
        DataSpec nextDataSpec;
        CacheSpan nextSpan2;
        long length;
        long length2;
        if (this.currentRequestIgnoresCache) {
            nextSpan = null;
        } else if (this.blockOnCache) {
            try {
                nextSpan = this.cache.startReadWrite(this.key, this.readPosition);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
        } else {
            nextSpan = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        }
        if (nextSpan == null) {
            DataSource nextDataSource2 = this.upstreamDataSource;
            Uri uri2 = this.uri;
            int i = this.httpMethod;
            long j = this.readPosition;
            nextDataSpec = new DataSpec(uri2, i, (byte[]) null, j, j, this.bytesRemaining, this.key, this.flags);
            nextDataSource = nextDataSource2;
            nextSpan2 = nextSpan;
        } else if (nextSpan.isCached) {
            Uri fileUri = Uri.fromFile(nextSpan.file);
            long filePosition = this.readPosition - nextSpan.position;
            long length3 = nextSpan.length - filePosition;
            long j2 = this.bytesRemaining;
            if (j2 != -1) {
                length2 = Math.min(length3, j2);
            } else {
                length2 = length3;
            }
            nextDataSpec = new DataSpec(fileUri, this.readPosition, filePosition, length2, this.key, this.flags);
            nextDataSource = this.cacheReadDataSource;
            nextSpan2 = nextSpan;
        } else {
            if (nextSpan.isOpenEnded()) {
                length = this.bytesRemaining;
            } else {
                length = nextSpan.length;
                long j3 = this.bytesRemaining;
                if (j3 != -1) {
                    length = Math.min(length, j3);
                }
            }
            Uri uri3 = this.uri;
            int i2 = this.httpMethod;
            long j4 = this.readPosition;
            DataSpec nextDataSpec2 = new DataSpec(uri3, i2, (byte[]) null, j4, j4, length, this.key, this.flags);
            if (this.cacheWriteDataSource != null) {
                nextDataSource = this.cacheWriteDataSource;
                nextSpan2 = nextSpan;
                nextDataSpec = nextDataSpec2;
            } else {
                nextDataSource = this.upstreamDataSource;
                this.cache.releaseHoleSpan(nextSpan);
                nextSpan2 = null;
                nextDataSpec = nextDataSpec2;
            }
        }
        this.checkCachePosition = (this.currentRequestIgnoresCache || nextDataSource != this.upstreamDataSource) ? Long.MAX_VALUE : this.readPosition + MIN_READ_BEFORE_CHECKING_CACHE;
        if (checkCache) {
            Assertions.checkState(isBypassingCache());
            if (nextDataSource != this.upstreamDataSource) {
                try {
                    closeCurrentSource();
                } catch (Throwable th) {
                    Throwable e2 = th;
                    if (nextSpan2.isHoleSpan()) {
                        this.cache.releaseHoleSpan(nextSpan2);
                    }
                    throw e2;
                }
            } else {
                return;
            }
        }
        if (nextSpan2 != null && nextSpan2.isHoleSpan()) {
            this.currentHoleSpan = nextSpan2;
        }
        this.currentDataSource = nextDataSource;
        this.currentDataSpecLengthUnset = nextDataSpec.length == -1;
        long resolvedLength = nextDataSource.open(nextDataSpec);
        ContentMetadataMutations mutations = new ContentMetadataMutations();
        if (this.currentDataSpecLengthUnset && resolvedLength != -1) {
            this.bytesRemaining = resolvedLength;
            ContentMetadataMutations.setContentLength(mutations, this.readPosition + resolvedLength);
        }
        if (isReadingFromUpstream()) {
            Uri uri4 = this.currentDataSource.getUri();
            this.actualUri = uri4;
            ContentMetadataMutations.setRedirectedUri(mutations, true ^ this.uri.equals(uri4) ? this.actualUri : null);
        }
        if (isWritingToCache()) {
            this.cache.applyContentMetadataMutations(this.key, mutations);
        }
    }

    private void setNoBytesRemainingAndMaybeStoreLength() throws IOException {
        this.bytesRemaining = 0;
        if (isWritingToCache()) {
            ContentMetadataMutations mutations = new ContentMetadataMutations();
            ContentMetadataMutations.setContentLength(mutations, this.readPosition);
            this.cache.applyContentMetadataMutations(this.key, mutations);
        }
    }

    private static Uri getRedirectedUriOrDefault(Cache cache2, String key2, Uri defaultUri) {
        Uri redirectedUri = ContentMetadata.CC.getRedirectedUri(cache2.getContentMetadata(key2));
        return redirectedUri != null ? redirectedUri : defaultUri;
    }

    private static boolean isCausedByPositionOutOfRange(IOException e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if ((cause instanceof DataSourceException) && ((DataSourceException) cause).reason == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isReadingFromUpstream() {
        return !isReadingFromCache();
    }

    private boolean isBypassingCache() {
        return this.currentDataSource == this.upstreamDataSource;
    }

    private boolean isReadingFromCache() {
        return this.currentDataSource == this.cacheReadDataSource;
    }

    private boolean isWritingToCache() {
        return this.currentDataSource == this.cacheWriteDataSource;
    }

    private void closeCurrentSource() throws IOException {
        DataSource dataSource = this.currentDataSource;
        if (dataSource != null) {
            try {
                dataSource.close();
            } finally {
                this.currentDataSource = null;
                this.currentDataSpecLengthUnset = false;
                CacheSpan cacheSpan = this.currentHoleSpan;
                if (cacheSpan != null) {
                    this.cache.releaseHoleSpan(cacheSpan);
                    this.currentHoleSpan = null;
                }
            }
        }
    }

    private void handleBeforeThrow(IOException exception) {
        if (isReadingFromCache() || (exception instanceof Cache.CacheException)) {
            this.seenCacheError = true;
        }
    }

    private int shouldIgnoreCacheForRequest(DataSpec dataSpec) {
        if (this.ignoreCacheOnError && this.seenCacheError) {
            return 0;
        }
        if (!this.ignoreCacheForUnsetLengthRequests || dataSpec.length != -1) {
            return -1;
        }
        return 1;
    }

    private void notifyCacheIgnored(int reason) {
        EventListener eventListener2 = this.eventListener;
        if (eventListener2 != null) {
            eventListener2.onCacheIgnored(reason);
        }
    }

    private void notifyBytesRead() {
        EventListener eventListener2 = this.eventListener;
        if (eventListener2 != null && this.totalCachedBytesRead > 0) {
            eventListener2.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
            this.totalCachedBytesRead = 0;
        }
    }
}
