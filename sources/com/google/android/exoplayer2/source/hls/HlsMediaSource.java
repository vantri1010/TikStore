package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.FilteringHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.List;

public final class HlsMediaSource extends BaseMediaSource implements HlsPlaylistTracker.PrimaryPlaylistListener {
    private final boolean allowChunklessPreparation;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private final Uri manifestUri;
    private TransferListener mediaTransferListener;
    private final HlsPlaylistTracker playlistTracker;
    private final Object tag;

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
    }

    public static final class Factory implements AdsMediaSource.MediaSourceFactory {
        private boolean allowChunklessPreparation;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private HlsExtractorFactory extractorFactory;
        private final HlsDataSourceFactory hlsDataSourceFactory;
        private boolean isCreateCalled;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private HlsPlaylistParserFactory playlistParserFactory;
        private HlsPlaylistTracker.Factory playlistTrackerFactory;
        private List<StreamKey> streamKeys;
        private Object tag;

        public Factory(DataSource.Factory dataSourceFactory) {
            this((HlsDataSourceFactory) new DefaultHlsDataSourceFactory(dataSourceFactory));
        }

        public Factory(HlsDataSourceFactory hlsDataSourceFactory2) {
            this.hlsDataSourceFactory = (HlsDataSourceFactory) Assertions.checkNotNull(hlsDataSourceFactory2);
            this.playlistParserFactory = new DefaultHlsPlaylistParserFactory();
            this.playlistTrackerFactory = DefaultHlsPlaylistTracker.FACTORY;
            this.extractorFactory = HlsExtractorFactory.DEFAULT;
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory setTag(Object tag2) {
            Assertions.checkState(!this.isCreateCalled);
            this.tag = tag2;
            return this;
        }

        public Factory setExtractorFactory(HlsExtractorFactory extractorFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.extractorFactory = (HlsExtractorFactory) Assertions.checkNotNull(extractorFactory2);
            return this;
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy2) {
            Assertions.checkState(!this.isCreateCalled);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
            return this;
        }

        @Deprecated
        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            Assertions.checkState(!this.isCreateCalled);
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount);
            return this;
        }

        public Factory setPlaylistParserFactory(HlsPlaylistParserFactory playlistParserFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.playlistParserFactory = (HlsPlaylistParserFactory) Assertions.checkNotNull(playlistParserFactory2);
            return this;
        }

        public Factory setStreamKeys(List<StreamKey> streamKeys2) {
            Assertions.checkState(!this.isCreateCalled);
            this.streamKeys = streamKeys2;
            return this;
        }

        public Factory setPlaylistTrackerFactory(HlsPlaylistTracker.Factory playlistTrackerFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.playlistTrackerFactory = (HlsPlaylistTracker.Factory) Assertions.checkNotNull(playlistTrackerFactory2);
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory2);
            return this;
        }

        public Factory setAllowChunklessPreparation(boolean allowChunklessPreparation2) {
            Assertions.checkState(!this.isCreateCalled);
            this.allowChunklessPreparation = allowChunklessPreparation2;
            return this;
        }

        public HlsMediaSource createMediaSource(Uri playlistUri) {
            this.isCreateCalled = true;
            List<StreamKey> list = this.streamKeys;
            if (list != null) {
                this.playlistParserFactory = new FilteringHlsPlaylistParserFactory(this.playlistParserFactory, list);
            }
            HlsDataSourceFactory hlsDataSourceFactory2 = this.hlsDataSourceFactory;
            HlsExtractorFactory hlsExtractorFactory = this.extractorFactory;
            CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2 = this.compositeSequenceableLoaderFactory;
            LoadErrorHandlingPolicy loadErrorHandlingPolicy2 = this.loadErrorHandlingPolicy;
            return new HlsMediaSource(playlistUri, hlsDataSourceFactory2, hlsExtractorFactory, compositeSequenceableLoaderFactory2, loadErrorHandlingPolicy2, this.playlistTrackerFactory.createTracker(hlsDataSourceFactory2, loadErrorHandlingPolicy2, this.playlistParserFactory), this.allowChunklessPreparation, this.tag);
        }

        @Deprecated
        public HlsMediaSource createMediaSource(Uri playlistUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            HlsMediaSource mediaSource = createMediaSource(playlistUri);
            if (!(eventHandler == null || eventListener == null)) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{2};
        }
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri2, DataSource.Factory dataSourceFactory2, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri2, dataSourceFactory2, 3, eventHandler, eventListener);
    }

    @Deprecated
    public HlsMediaSource(Uri manifestUri2, DataSource.Factory dataSourceFactory2, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri2, new DefaultHlsDataSourceFactory(dataSourceFactory2), HlsExtractorFactory.DEFAULT, minLoadableRetryCount, eventHandler, eventListener, new HlsPlaylistParser());
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @java.lang.Deprecated
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsMediaSource(android.net.Uri r15, com.google.android.exoplayer2.source.hls.HlsDataSourceFactory r16, com.google.android.exoplayer2.source.hls.HlsExtractorFactory r17, int r18, android.os.Handler r19, com.google.android.exoplayer2.source.MediaSourceEventListener r20, com.google.android.exoplayer2.upstream.ParsingLoadable.Parser<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist> r21) {
        /*
            r14 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory r7 = new com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory
            r7.<init>()
            com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy r8 = new com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
            r8.<init>(r0)
            com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker r9 = new com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker
            com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy r3 = new com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
            r3.<init>(r0)
            r12 = r16
            r13 = r21
            r9.<init>((com.google.android.exoplayer2.source.hls.HlsDataSourceFactory) r12, (com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy) r3, (com.google.android.exoplayer2.upstream.ParsingLoadable.Parser<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>) r13)
            r10 = 0
            r11 = 0
            r3 = r14
            r4 = r15
            r5 = r16
            r6 = r17
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11)
            if (r1 == 0) goto L_0x0032
            if (r2 == 0) goto L_0x0032
            r3 = r14
            r14.addEventListener(r1, r2)
            goto L_0x0033
        L_0x0032:
            r3 = r14
        L_0x0033:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaSource.<init>(android.net.Uri, com.google.android.exoplayer2.source.hls.HlsDataSourceFactory, com.google.android.exoplayer2.source.hls.HlsExtractorFactory, int, android.os.Handler, com.google.android.exoplayer2.source.MediaSourceEventListener, com.google.android.exoplayer2.upstream.ParsingLoadable$Parser):void");
    }

    private HlsMediaSource(Uri manifestUri2, HlsDataSourceFactory dataSourceFactory2, HlsExtractorFactory extractorFactory2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, HlsPlaylistTracker playlistTracker2, boolean allowChunklessPreparation2, Object tag2) {
        this.manifestUri = manifestUri2;
        this.dataSourceFactory = dataSourceFactory2;
        this.extractorFactory = extractorFactory2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
        this.playlistTracker = playlistTracker2;
        this.allowChunklessPreparation = allowChunklessPreparation2;
        this.tag = tag2;
    }

    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(TransferListener mediaTransferListener2) {
        this.mediaTransferListener = mediaTransferListener2;
        this.playlistTracker.start(this.manifestUri, createEventDispatcher((MediaSource.MediaPeriodId) null), this);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        return new HlsMediaPeriod(this.extractorFactory, this.playlistTracker, this.dataSourceFactory, this.mediaTransferListener, this.loadErrorHandlingPolicy, createEventDispatcher(id), allocator, this.compositeSequenceableLoaderFactory, this.allowChunklessPreparation);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((HlsMediaPeriod) mediaPeriod).release();
    }

    public void releaseSourceInternal() {
        this.playlistTracker.stop();
    }

    public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist playlist) {
        SinglePeriodTimeline timeline;
        long windowDefaultStartPositionUs;
        HlsMediaPlaylist hlsMediaPlaylist = playlist;
        long windowStartTimeMs = hlsMediaPlaylist.hasProgramDateTime ? C.usToMs(hlsMediaPlaylist.startTimeUs) : -9223372036854775807L;
        long presentationStartTimeMs = (hlsMediaPlaylist.playlistType == 2 || hlsMediaPlaylist.playlistType == 1) ? windowStartTimeMs : -9223372036854775807L;
        long windowDefaultStartPositionUs2 = hlsMediaPlaylist.startOffsetUs;
        if (this.playlistTracker.isLive()) {
            long offsetFromInitialStartTimeUs = hlsMediaPlaylist.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
            long periodDurationUs = hlsMediaPlaylist.hasEndTag ? offsetFromInitialStartTimeUs + hlsMediaPlaylist.durationUs : -9223372036854775807L;
            List<HlsMediaPlaylist.Segment> segments = hlsMediaPlaylist.segments;
            if (windowDefaultStartPositionUs2 == C.TIME_UNSET) {
                if (segments.isEmpty()) {
                    windowDefaultStartPositionUs = 0;
                } else {
                    windowDefaultStartPositionUs = segments.get(Math.max(0, segments.size() - 3)).relativeStartTimeUs;
                }
            } else {
                windowDefaultStartPositionUs = windowDefaultStartPositionUs2;
            }
            timeline = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, periodDurationUs, hlsMediaPlaylist.durationUs, offsetFromInitialStartTimeUs, windowDefaultStartPositionUs, true, !hlsMediaPlaylist.hasEndTag, this.tag);
        } else {
            if (windowDefaultStartPositionUs2 == C.TIME_UNSET) {
                windowDefaultStartPositionUs2 = 0;
            }
            timeline = new SinglePeriodTimeline(presentationStartTimeMs, windowStartTimeMs, hlsMediaPlaylist.durationUs, hlsMediaPlaylist.durationUs, 0, windowDefaultStartPositionUs2, true, false, this.tag);
            long j = windowDefaultStartPositionUs2;
        }
        refreshSourceInfo(timeline, new HlsManifest(this.playlistTracker.getMasterPlaylist(), hlsMediaPlaylist));
    }
}
