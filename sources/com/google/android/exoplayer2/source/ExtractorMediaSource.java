package com.google.android.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class ExtractorMediaSource extends BaseMediaSource implements ExtractorMediaPeriod.Listener {
    public static final int DEFAULT_LOADING_CHECK_INTERVAL_BYTES = 1048576;
    private final int continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final DataSource.Factory dataSourceFactory;
    private final ExtractorsFactory extractorsFactory;
    private final LoadErrorHandlingPolicy loadableLoadErrorHandlingPolicy;
    private final Object tag;
    private long timelineDurationUs;
    private boolean timelineIsSeekable;
    private TransferListener transferListener;
    private final Uri uri;

    @Deprecated
    public interface EventListener {
        void onLoadError(IOException iOException);
    }

    public static final class Factory implements AdsMediaSource.MediaSourceFactory {
        private int continueLoadingCheckIntervalBytes = 1048576;
        private String customCacheKey;
        private final DataSource.Factory dataSourceFactory;
        private ExtractorsFactory extractorsFactory;
        private boolean isCreateCalled;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
        private Object tag;

        public Factory(DataSource.Factory dataSourceFactory2) {
            this.dataSourceFactory = dataSourceFactory2;
        }

        public Factory setExtractorsFactory(ExtractorsFactory extractorsFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.extractorsFactory = extractorsFactory2;
            return this;
        }

        public Factory setCustomCacheKey(String customCacheKey2) {
            Assertions.checkState(!this.isCreateCalled);
            this.customCacheKey = customCacheKey2;
            return this;
        }

        public Factory setTag(Object tag2) {
            Assertions.checkState(!this.isCreateCalled);
            this.tag = tag2;
            return this;
        }

        @Deprecated
        public Factory setMinLoadableRetryCount(int minLoadableRetryCount) {
            return setLoadErrorHandlingPolicy(new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount));
        }

        public Factory setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy2) {
            Assertions.checkState(!this.isCreateCalled);
            this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
            return this;
        }

        public Factory setContinueLoadingCheckIntervalBytes(int continueLoadingCheckIntervalBytes2) {
            Assertions.checkState(!this.isCreateCalled);
            this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes2;
            return this;
        }

        public ExtractorMediaSource createMediaSource(Uri uri) {
            this.isCreateCalled = true;
            if (this.extractorsFactory == null) {
                this.extractorsFactory = new DefaultExtractorsFactory();
            }
            return new ExtractorMediaSource(uri, this.dataSourceFactory, this.extractorsFactory, this.loadErrorHandlingPolicy, this.customCacheKey, this.continueLoadingCheckIntervalBytes, this.tag);
        }

        @Deprecated
        public ExtractorMediaSource createMediaSource(Uri uri, Handler eventHandler, MediaSourceEventListener eventListener) {
            ExtractorMediaSource mediaSource = createMediaSource(uri);
            if (!(eventHandler == null || eventListener == null)) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{3};
        }
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri2, DataSource.Factory dataSourceFactory2, ExtractorsFactory extractorsFactory2, Handler eventHandler, EventListener eventListener) {
        this(uri2, dataSourceFactory2, extractorsFactory2, eventHandler, eventListener, (String) null);
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri2, DataSource.Factory dataSourceFactory2, ExtractorsFactory extractorsFactory2, Handler eventHandler, EventListener eventListener, String customCacheKey2) {
        this(uri2, dataSourceFactory2, extractorsFactory2, eventHandler, eventListener, customCacheKey2, 1048576);
    }

    @Deprecated
    public ExtractorMediaSource(Uri uri2, DataSource.Factory dataSourceFactory2, ExtractorsFactory extractorsFactory2, Handler eventHandler, EventListener eventListener, String customCacheKey2, int continueLoadingCheckIntervalBytes2) {
        this(uri2, dataSourceFactory2, extractorsFactory2, (LoadErrorHandlingPolicy) new DefaultLoadErrorHandlingPolicy(), customCacheKey2, continueLoadingCheckIntervalBytes2, (Object) null);
        if (eventListener != null && eventHandler != null) {
            addEventListener(eventHandler, new EventListenerWrapper(eventListener));
        }
    }

    private ExtractorMediaSource(Uri uri2, DataSource.Factory dataSourceFactory2, ExtractorsFactory extractorsFactory2, LoadErrorHandlingPolicy loadableLoadErrorHandlingPolicy2, String customCacheKey2, int continueLoadingCheckIntervalBytes2, Object tag2) {
        this.uri = uri2;
        this.dataSourceFactory = dataSourceFactory2;
        this.extractorsFactory = extractorsFactory2;
        this.loadableLoadErrorHandlingPolicy = loadableLoadErrorHandlingPolicy2;
        this.customCacheKey = customCacheKey2;
        this.continueLoadingCheckIntervalBytes = continueLoadingCheckIntervalBytes2;
        this.timelineDurationUs = C.TIME_UNSET;
        this.tag = tag2;
    }

    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(TransferListener mediaTransferListener) {
        this.transferListener = mediaTransferListener;
        notifySourceInfoRefreshed(this.timelineDurationUs, this.timelineIsSeekable);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        DataSource dataSource = this.dataSourceFactory.createDataSource();
        TransferListener transferListener2 = this.transferListener;
        if (transferListener2 != null) {
            dataSource.addTransferListener(transferListener2);
        }
        return new ExtractorMediaPeriod(this.uri, dataSource, this.extractorsFactory.createExtractors(), this.loadableLoadErrorHandlingPolicy, createEventDispatcher(id), this, allocator, this.customCacheKey, this.continueLoadingCheckIntervalBytes);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((ExtractorMediaPeriod) mediaPeriod).release();
    }

    public void releaseSourceInternal() {
    }

    public void onSourceInfoRefreshed(long durationUs, boolean isSeekable) {
        long durationUs2 = durationUs == C.TIME_UNSET ? this.timelineDurationUs : durationUs;
        if (this.timelineDurationUs != durationUs2 || this.timelineIsSeekable != isSeekable) {
            notifySourceInfoRefreshed(durationUs2, isSeekable);
        }
    }

    private void notifySourceInfoRefreshed(long durationUs, boolean isSeekable) {
        this.timelineDurationUs = durationUs;
        this.timelineIsSeekable = isSeekable;
        refreshSourceInfo(new SinglePeriodTimeline(this.timelineDurationUs, this.timelineIsSeekable, false, this.tag), (Object) null);
    }

    @Deprecated
    private static final class EventListenerWrapper extends DefaultMediaSourceEventListener {
        private final EventListener eventListener;

        public EventListenerWrapper(EventListener eventListener2) {
            this.eventListener = (EventListener) Assertions.checkNotNull(eventListener2);
        }

        public void onLoadError(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            this.eventListener.onLoadError(error);
        }
    }
}
