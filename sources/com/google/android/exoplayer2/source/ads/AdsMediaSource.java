package com.google.android.exoplayer2.source.ads;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.CompositeMediaSource;
import com.google.android.exoplayer2.source.DeferredMediaPeriod;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdsMediaSource extends CompositeMediaSource<MediaSource.MediaPeriodId> {
    private static final MediaSource.MediaPeriodId DUMMY_CONTENT_MEDIA_PERIOD_ID = new MediaSource.MediaPeriodId(new Object());
    private MediaSource[][] adGroupMediaSources;
    private Timeline[][] adGroupTimelines;
    private final MediaSourceFactory adMediaSourceFactory;
    private AdPlaybackState adPlaybackState;
    private final ViewGroup adUiViewGroup;
    /* access modifiers changed from: private */
    public final AdsLoader adsLoader;
    private ComponentListener componentListener;
    private Object contentManifest;
    private final MediaSource contentMediaSource;
    private Timeline contentTimeline;
    private final Map<MediaSource, List<DeferredMediaPeriod>> deferredMediaPeriodByAdMediaSource;
    /* access modifiers changed from: private */
    public final Handler eventHandler;
    /* access modifiers changed from: private */
    public final EventListener eventListener;
    /* access modifiers changed from: private */
    public final Handler mainHandler;
    private final Timeline.Period period;

    @Deprecated
    public interface EventListener {
        void onAdClicked();

        void onAdLoadError(IOException iOException);

        void onAdTapped();

        void onInternalAdLoadError(RuntimeException runtimeException);
    }

    public interface MediaSourceFactory {
        MediaSource createMediaSource(Uri uri);

        int[] getSupportedTypes();
    }

    public static final class AdLoadException extends IOException {
        public static final int TYPE_AD = 0;
        public static final int TYPE_AD_GROUP = 1;
        public static final int TYPE_ALL_ADS = 2;
        public static final int TYPE_UNEXPECTED = 3;
        public final int type;

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        public @interface Type {
        }

        public static AdLoadException createForAd(Exception error) {
            return new AdLoadException(0, error);
        }

        public static AdLoadException createForAdGroup(Exception error, int adGroupIndex) {
            return new AdLoadException(1, new IOException("Failed to load ad group " + adGroupIndex, error));
        }

        public static AdLoadException createForAllAds(Exception error) {
            return new AdLoadException(2, error);
        }

        public static AdLoadException createForUnexpected(RuntimeException error) {
            return new AdLoadException(3, error);
        }

        private AdLoadException(int type2, Exception cause) {
            super(cause);
            this.type = type2;
        }

        public RuntimeException getRuntimeExceptionForUnexpected() {
            Assertions.checkState(this.type == 3);
            return (RuntimeException) getCause();
        }
    }

    public AdsMediaSource(MediaSource contentMediaSource2, DataSource.Factory dataSourceFactory, AdsLoader adsLoader2, ViewGroup adUiViewGroup2) {
        this(contentMediaSource2, (MediaSourceFactory) new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader2, adUiViewGroup2, (Handler) null, (EventListener) null);
    }

    public AdsMediaSource(MediaSource contentMediaSource2, MediaSourceFactory adMediaSourceFactory2, AdsLoader adsLoader2, ViewGroup adUiViewGroup2) {
        this(contentMediaSource2, adMediaSourceFactory2, adsLoader2, adUiViewGroup2, (Handler) null, (EventListener) null);
    }

    @Deprecated
    public AdsMediaSource(MediaSource contentMediaSource2, DataSource.Factory dataSourceFactory, AdsLoader adsLoader2, ViewGroup adUiViewGroup2, Handler eventHandler2, EventListener eventListener2) {
        this(contentMediaSource2, (MediaSourceFactory) new ExtractorMediaSource.Factory(dataSourceFactory), adsLoader2, adUiViewGroup2, eventHandler2, eventListener2);
    }

    @Deprecated
    public AdsMediaSource(MediaSource contentMediaSource2, MediaSourceFactory adMediaSourceFactory2, AdsLoader adsLoader2, ViewGroup adUiViewGroup2, Handler eventHandler2, EventListener eventListener2) {
        this.contentMediaSource = contentMediaSource2;
        this.adMediaSourceFactory = adMediaSourceFactory2;
        this.adsLoader = adsLoader2;
        this.adUiViewGroup = adUiViewGroup2;
        this.eventHandler = eventHandler2;
        this.eventListener = eventListener2;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.deferredMediaPeriodByAdMediaSource = new HashMap();
        this.period = new Timeline.Period();
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        adsLoader2.setSupportedContentTypes(adMediaSourceFactory2.getSupportedTypes());
    }

    public Object getTag() {
        return this.contentMediaSource.getTag();
    }

    public void prepareSourceInternal(TransferListener mediaTransferListener) {
        super.prepareSourceInternal(mediaTransferListener);
        ComponentListener componentListener2 = new ComponentListener();
        this.componentListener = componentListener2;
        prepareChildSource(DUMMY_CONTENT_MEDIA_PERIOD_ID, this.contentMediaSource);
        this.mainHandler.post(new Runnable(componentListener2) {
            private final /* synthetic */ AdsMediaSource.ComponentListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                AdsMediaSource.this.lambda$prepareSourceInternal$0$AdsMediaSource(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$prepareSourceInternal$0$AdsMediaSource(ComponentListener componentListener2) {
        this.adsLoader.start(componentListener2, this.adUiViewGroup);
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        MediaSource.MediaPeriodId mediaPeriodId = id;
        if (this.adPlaybackState.adGroupCount <= 0 || !id.isAd()) {
            DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(this.contentMediaSource, id, allocator, startPositionUs);
            mediaPeriod.createPeriod(id);
            return mediaPeriod;
        }
        int adGroupIndex = mediaPeriodId.adGroupIndex;
        int adIndexInAdGroup = mediaPeriodId.adIndexInAdGroup;
        Uri adUri = this.adPlaybackState.adGroups[adGroupIndex].uris[adIndexInAdGroup];
        if (this.adGroupMediaSources[adGroupIndex].length <= adIndexInAdGroup) {
            MediaSource adMediaSource = this.adMediaSourceFactory.createMediaSource(adUri);
            MediaSource[][] mediaSourceArr = this.adGroupMediaSources;
            if (adIndexInAdGroup >= mediaSourceArr[adGroupIndex].length) {
                int adCount = adIndexInAdGroup + 1;
                mediaSourceArr[adGroupIndex] = (MediaSource[]) Arrays.copyOf(mediaSourceArr[adGroupIndex], adCount);
                Timeline[][] timelineArr = this.adGroupTimelines;
                timelineArr[adGroupIndex] = (Timeline[]) Arrays.copyOf(timelineArr[adGroupIndex], adCount);
            }
            this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup] = adMediaSource;
            this.deferredMediaPeriodByAdMediaSource.put(adMediaSource, new ArrayList());
            prepareChildSource(id, adMediaSource);
        }
        MediaSource mediaSource = this.adGroupMediaSources[adGroupIndex][adIndexInAdGroup];
        DeferredMediaPeriod deferredMediaPeriod = new DeferredMediaPeriod(mediaSource, id, allocator, startPositionUs);
        deferredMediaPeriod.setPrepareErrorListener(new AdPrepareErrorListener(adUri, adGroupIndex, adIndexInAdGroup));
        List<DeferredMediaPeriod> mediaPeriods = this.deferredMediaPeriodByAdMediaSource.get(mediaSource);
        if (mediaPeriods == null) {
            deferredMediaPeriod.createPeriod(new MediaSource.MediaPeriodId(this.adGroupTimelines[adGroupIndex][adIndexInAdGroup].getUidOfPeriod(0), mediaPeriodId.windowSequenceNumber));
        } else {
            mediaPeriods.add(deferredMediaPeriod);
        }
        return deferredMediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DeferredMediaPeriod deferredMediaPeriod = (DeferredMediaPeriod) mediaPeriod;
        List<DeferredMediaPeriod> mediaPeriods = this.deferredMediaPeriodByAdMediaSource.get(deferredMediaPeriod.mediaSource);
        if (mediaPeriods != null) {
            mediaPeriods.remove(deferredMediaPeriod);
        }
        deferredMediaPeriod.releasePeriod();
    }

    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.componentListener.release();
        this.componentListener = null;
        this.deferredMediaPeriodByAdMediaSource.clear();
        this.contentTimeline = null;
        this.contentManifest = null;
        this.adPlaybackState = null;
        this.adGroupMediaSources = new MediaSource[0][];
        this.adGroupTimelines = new Timeline[0][];
        Handler handler = this.mainHandler;
        AdsLoader adsLoader2 = this.adsLoader;
        adsLoader2.getClass();
        handler.post(new Runnable() {
            public final void run() {
                AdsLoader.this.stop();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onChildSourceInfoRefreshed(MediaSource.MediaPeriodId mediaPeriodId, MediaSource mediaSource, Timeline timeline, Object manifest) {
        if (mediaPeriodId.isAd()) {
            onAdSourceInfoRefreshed(mediaSource, mediaPeriodId.adGroupIndex, mediaPeriodId.adIndexInAdGroup, timeline);
        } else {
            onContentSourceInfoRefreshed(timeline, manifest);
        }
    }

    /* access modifiers changed from: protected */
    public MediaSource.MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSource.MediaPeriodId childId, MediaSource.MediaPeriodId mediaPeriodId) {
        return childId.isAd() ? childId : mediaPeriodId;
    }

    /* access modifiers changed from: private */
    public void onAdPlaybackState(AdPlaybackState adPlaybackState2) {
        if (this.adPlaybackState == null) {
            MediaSource[][] mediaSourceArr = new MediaSource[adPlaybackState2.adGroupCount][];
            this.adGroupMediaSources = mediaSourceArr;
            Arrays.fill(mediaSourceArr, new MediaSource[0]);
            Timeline[][] timelineArr = new Timeline[adPlaybackState2.adGroupCount][];
            this.adGroupTimelines = timelineArr;
            Arrays.fill(timelineArr, new Timeline[0]);
        }
        this.adPlaybackState = adPlaybackState2;
        maybeUpdateSourceInfo();
    }

    private void onContentSourceInfoRefreshed(Timeline timeline, Object manifest) {
        boolean z = true;
        if (timeline.getPeriodCount() != 1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.contentTimeline = timeline;
        this.contentManifest = manifest;
        maybeUpdateSourceInfo();
    }

    private void onAdSourceInfoRefreshed(MediaSource mediaSource, int adGroupIndex, int adIndexInAdGroup, Timeline timeline) {
        boolean z = true;
        if (timeline.getPeriodCount() != 1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.adGroupTimelines[adGroupIndex][adIndexInAdGroup] = timeline;
        List<DeferredMediaPeriod> mediaPeriods = this.deferredMediaPeriodByAdMediaSource.remove(mediaSource);
        if (mediaPeriods != null) {
            Object periodUid = timeline.getUidOfPeriod(0);
            for (int i = 0; i < mediaPeriods.size(); i++) {
                DeferredMediaPeriod mediaPeriod = mediaPeriods.get(i);
                mediaPeriod.createPeriod(new MediaSource.MediaPeriodId(periodUid, mediaPeriod.id.windowSequenceNumber));
            }
        }
        maybeUpdateSourceInfo();
    }

    private void maybeUpdateSourceInfo() {
        AdPlaybackState adPlaybackState2 = this.adPlaybackState;
        if (adPlaybackState2 != null && this.contentTimeline != null) {
            AdPlaybackState withAdDurationsUs = adPlaybackState2.withAdDurationsUs(getAdDurations(this.adGroupTimelines, this.period));
            this.adPlaybackState = withAdDurationsUs;
            refreshSourceInfo(withAdDurationsUs.adGroupCount == 0 ? this.contentTimeline : new SinglePeriodAdTimeline(this.contentTimeline, this.adPlaybackState), this.contentManifest);
        }
    }

    private static long[][] getAdDurations(Timeline[][] adTimelines, Timeline.Period period2) {
        long j;
        long[][] adDurations = new long[adTimelines.length][];
        for (int i = 0; i < adTimelines.length; i++) {
            adDurations[i] = new long[adTimelines[i].length];
            for (int j2 = 0; j2 < adTimelines[i].length; j2++) {
                long[] jArr = adDurations[i];
                if (adTimelines[i][j2] == null) {
                    j = C.TIME_UNSET;
                } else {
                    j = adTimelines[i][j2].getPeriod(0, period2).getDurationUs();
                }
                jArr[j2] = j;
            }
        }
        return adDurations;
    }

    private final class ComponentListener implements AdsLoader.EventListener {
        private final Handler playerHandler = new Handler();
        private volatile boolean released;

        public ComponentListener() {
        }

        public void release() {
            this.released = true;
            this.playerHandler.removeCallbacksAndMessages((Object) null);
        }

        public void onAdPlaybackState(AdPlaybackState adPlaybackState) {
            if (!this.released) {
                this.playerHandler.post(new Runnable(adPlaybackState) {
                    private final /* synthetic */ AdPlaybackState f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        AdsMediaSource.ComponentListener.this.lambda$onAdPlaybackState$0$AdsMediaSource$ComponentListener(this.f$1);
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onAdPlaybackState$0$AdsMediaSource$ComponentListener(AdPlaybackState adPlaybackState) {
            if (!this.released) {
                AdsMediaSource.this.onAdPlaybackState(adPlaybackState);
            }
        }

        public void onAdClicked() {
            if (!this.released && AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public final void run() {
                        AdsMediaSource.ComponentListener.this.lambda$onAdClicked$1$AdsMediaSource$ComponentListener();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onAdClicked$1$AdsMediaSource$ComponentListener() {
            if (!this.released) {
                AdsMediaSource.this.eventListener.onAdClicked();
            }
        }

        public void onAdTapped() {
            if (!this.released && AdsMediaSource.this.eventHandler != null && AdsMediaSource.this.eventListener != null) {
                AdsMediaSource.this.eventHandler.post(new Runnable() {
                    public final void run() {
                        AdsMediaSource.ComponentListener.this.lambda$onAdTapped$2$AdsMediaSource$ComponentListener();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onAdTapped$2$AdsMediaSource$ComponentListener() {
            if (!this.released) {
                AdsMediaSource.this.eventListener.onAdTapped();
            }
        }

        public void onAdLoadError(AdLoadException error, DataSpec dataSpec) {
            if (!this.released) {
                AdsMediaSource.this.createEventDispatcher((MediaSource.MediaPeriodId) null).loadError(dataSpec, dataSpec.uri, Collections.emptyMap(), 6, -1, 0, 0, error, true);
                if (AdsMediaSource.this.eventHandler == null || AdsMediaSource.this.eventListener == null) {
                    AdLoadException adLoadException = error;
                } else {
                    AdsMediaSource.this.eventHandler.post(new Runnable(error) {
                        private final /* synthetic */ AdsMediaSource.AdLoadException f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            AdsMediaSource.ComponentListener.this.lambda$onAdLoadError$3$AdsMediaSource$ComponentListener(this.f$1);
                        }
                    });
                }
            }
        }

        public /* synthetic */ void lambda$onAdLoadError$3$AdsMediaSource$ComponentListener(AdLoadException error) {
            if (this.released) {
                return;
            }
            if (error.type == 3) {
                AdsMediaSource.this.eventListener.onInternalAdLoadError(error.getRuntimeExceptionForUnexpected());
            } else {
                AdsMediaSource.this.eventListener.onAdLoadError(error);
            }
        }
    }

    private final class AdPrepareErrorListener implements DeferredMediaPeriod.PrepareErrorListener {
        private final int adGroupIndex;
        private final int adIndexInAdGroup;
        private final Uri adUri;

        public AdPrepareErrorListener(Uri adUri2, int adGroupIndex2, int adIndexInAdGroup2) {
            this.adUri = adUri2;
            this.adGroupIndex = adGroupIndex2;
            this.adIndexInAdGroup = adIndexInAdGroup2;
        }

        public void onPrepareError(MediaSource.MediaPeriodId mediaPeriodId, IOException exception) {
            AdsMediaSource.this.createEventDispatcher(mediaPeriodId).loadError(new DataSpec(this.adUri), this.adUri, Collections.emptyMap(), 6, -1, 0, 0, AdLoadException.createForAd(exception), true);
            AdsMediaSource.this.mainHandler.post(new Runnable(exception) {
                private final /* synthetic */ IOException f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AdsMediaSource.AdPrepareErrorListener.this.lambda$onPrepareError$0$AdsMediaSource$AdPrepareErrorListener(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onPrepareError$0$AdsMediaSource$AdPrepareErrorListener(IOException exception) {
            AdsMediaSource.this.adsLoader.handlePrepareError(this.adGroupIndex, this.adIndexInAdGroup, exception);
        }
    }
}
