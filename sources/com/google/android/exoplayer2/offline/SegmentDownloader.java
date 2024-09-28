package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.offline.FilterableManifest;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SegmentDownloader<M extends FilterableManifest<M>> implements Downloader {
    private static final int BUFFER_SIZE_BYTES = 131072;
    private final Cache cache;
    private final CacheKeyFactory cacheKeyFactory;
    private final CacheDataSource dataSource;
    private volatile long downloadedBytes;
    private volatile int downloadedSegments;
    private final AtomicBoolean isCanceled = new AtomicBoolean();
    private final DataSpec manifestDataSpec;
    private final CacheDataSource offlineDataSource;
    private final PriorityTaskManager priorityTaskManager;
    private final ArrayList<StreamKey> streamKeys;
    private volatile long totalBytes = -1;
    private volatile int totalSegments = -1;

    /* access modifiers changed from: protected */
    public abstract M getManifest(DataSource dataSource2, DataSpec dataSpec) throws IOException;

    /* access modifiers changed from: protected */
    public abstract List<Segment> getSegments(DataSource dataSource2, M m, boolean z) throws InterruptedException, IOException;

    protected static class Segment implements Comparable<Segment> {
        public final DataSpec dataSpec;
        public final long startTimeUs;

        public Segment(long startTimeUs2, DataSpec dataSpec2) {
            this.startTimeUs = startTimeUs2;
            this.dataSpec = dataSpec2;
        }

        public int compareTo(Segment other) {
            return Util.compareLong(this.startTimeUs, other.startTimeUs);
        }
    }

    public SegmentDownloader(Uri manifestUri, List<StreamKey> streamKeys2, DownloaderConstructorHelper constructorHelper) {
        this.manifestDataSpec = getCompressibleDataSpec(manifestUri);
        this.streamKeys = new ArrayList<>(streamKeys2);
        this.cache = constructorHelper.getCache();
        this.dataSource = constructorHelper.createCacheDataSource();
        this.offlineDataSource = constructorHelper.createOfflineCacheDataSource();
        this.cacheKeyFactory = constructorHelper.getCacheKeyFactory();
        this.priorityTaskManager = constructorHelper.getPriorityTaskManager();
    }

    public final void download() throws IOException, InterruptedException {
        CacheUtil.CachingCounters cachingCounters;
        this.priorityTaskManager.add(-1000);
        try {
            List<Segment> segments = initDownload();
            Collections.sort(segments);
            byte[] buffer = new byte[131072];
            cachingCounters = new CacheUtil.CachingCounters();
            for (int i = 0; i < segments.size(); i++) {
                CacheUtil.cache(segments.get(i).dataSpec, this.cache, this.cacheKeyFactory, this.dataSource, buffer, this.priorityTaskManager, -1000, cachingCounters, this.isCanceled, true);
                this.downloadedSegments++;
                this.downloadedBytes += cachingCounters.newlyCachedBytes;
            }
            this.priorityTaskManager.remove(-1000);
        } catch (Throwable th) {
            this.priorityTaskManager.remove(-1000);
            throw th;
        }
    }

    public void cancel() {
        this.isCanceled.set(true);
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public long getTotalBytes() {
        return this.totalBytes;
    }

    public final float getDownloadPercentage() {
        long totalBytes2 = this.totalBytes;
        if (totalBytes2 == -1) {
            int totalSegments2 = this.totalSegments;
            int downloadedSegments2 = this.downloadedSegments;
            if (totalSegments2 == -1 || downloadedSegments2 == -1) {
                return -1.0f;
            }
            if (totalSegments2 == 0) {
                return 100.0f;
            }
            return (((float) downloadedSegments2) * 100.0f) / ((float) totalSegments2);
        } else if (totalBytes2 == 0) {
            return 100.0f;
        } else {
            return (((float) this.downloadedBytes) * 100.0f) / ((float) totalBytes2);
        }
    }

    public final void remove() throws InterruptedException {
        try {
            List<Segment> segments = getSegments(this.offlineDataSource, getManifest(this.offlineDataSource, this.manifestDataSpec), true);
            for (int i = 0; i < segments.size(); i++) {
                removeDataSpec(segments.get(i).dataSpec);
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            removeDataSpec(this.manifestDataSpec);
            throw th;
        }
        removeDataSpec(this.manifestDataSpec);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: com.google.android.exoplayer2.offline.FilterableManifest} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.google.android.exoplayer2.offline.SegmentDownloader.Segment> initDownload() throws java.io.IOException, java.lang.InterruptedException {
        /*
            r14 = this;
            com.google.android.exoplayer2.upstream.cache.CacheDataSource r0 = r14.dataSource
            com.google.android.exoplayer2.upstream.DataSpec r1 = r14.manifestDataSpec
            com.google.android.exoplayer2.offline.FilterableManifest r0 = r14.getManifest(r0, r1)
            java.util.ArrayList<com.google.android.exoplayer2.offline.StreamKey> r1 = r14.streamKeys
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0019
            java.util.ArrayList<com.google.android.exoplayer2.offline.StreamKey> r1 = r14.streamKeys
            java.lang.Object r1 = r0.copy(r1)
            r0 = r1
            com.google.android.exoplayer2.offline.FilterableManifest r0 = (com.google.android.exoplayer2.offline.FilterableManifest) r0
        L_0x0019:
            com.google.android.exoplayer2.upstream.cache.CacheDataSource r1 = r14.dataSource
            r2 = 0
            java.util.List r1 = r14.getSegments(r1, r0, r2)
            com.google.android.exoplayer2.upstream.cache.CacheUtil$CachingCounters r3 = new com.google.android.exoplayer2.upstream.cache.CacheUtil$CachingCounters
            r3.<init>()
            int r4 = r1.size()
            r14.totalSegments = r4
            r14.downloadedSegments = r2
            r4 = 0
            r14.downloadedBytes = r4
            r4 = 0
            int r2 = r1.size()
            int r2 = r2 + -1
        L_0x0039:
            if (r2 < 0) goto L_0x0077
            java.lang.Object r6 = r1.get(r2)
            com.google.android.exoplayer2.offline.SegmentDownloader$Segment r6 = (com.google.android.exoplayer2.offline.SegmentDownloader.Segment) r6
            com.google.android.exoplayer2.upstream.DataSpec r7 = r6.dataSpec
            com.google.android.exoplayer2.upstream.cache.Cache r8 = r14.cache
            com.google.android.exoplayer2.upstream.cache.CacheKeyFactory r9 = r14.cacheKeyFactory
            com.google.android.exoplayer2.upstream.cache.CacheUtil.getCached(r7, r8, r9, r3)
            long r7 = r14.downloadedBytes
            long r9 = r3.alreadyCachedBytes
            long r7 = r7 + r9
            r14.downloadedBytes = r7
            long r7 = r3.contentLength
            r9 = -1
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 == 0) goto L_0x0072
            long r7 = r3.alreadyCachedBytes
            long r11 = r3.contentLength
            int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x006a
            int r7 = r14.downloadedSegments
            int r7 = r7 + 1
            r14.downloadedSegments = r7
            r1.remove(r2)
        L_0x006a:
            int r7 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r7 == 0) goto L_0x0074
            long r7 = r3.contentLength
            long r4 = r4 + r7
            goto L_0x0074
        L_0x0072:
            r4 = -1
        L_0x0074:
            int r2 = r2 + -1
            goto L_0x0039
        L_0x0077:
            r14.totalBytes = r4
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.SegmentDownloader.initDownload():java.util.List");
    }

    private void removeDataSpec(DataSpec dataSpec) {
        CacheUtil.remove(dataSpec, this.cache, this.cacheKeyFactory);
    }

    protected static DataSpec getCompressibleDataSpec(Uri uri) {
        return new DataSpec(uri, 0, -1, (String) null, 1);
    }
}
