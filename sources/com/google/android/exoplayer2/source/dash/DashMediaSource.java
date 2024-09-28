package com.google.android.exoplayer2.source.dash;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.PlayerEmsgHandler;
import com.google.android.exoplayer2.source.dash.manifest.AdaptationSet;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.dash.manifest.Period;
import com.google.android.exoplayer2.source.dash.manifest.UtcTimingElement;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Marker;

public final class DashMediaSource extends BaseMediaSource {
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_FIXED_MS = 30000;
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000;
    @Deprecated
    public static final long DEFAULT_LIVE_PRESENTATION_DELAY_PREFER_MANIFEST_MS = -1;
    private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000;
    private static final int NOTIFY_MANIFEST_INTERVAL_MS = 5000;
    private static final String TAG = "DashMediaSource";
    private final DashChunkSource.Factory chunkSourceFactory;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private DataSource dataSource;
    private long elapsedRealtimeOffsetMs;
    private long expiredManifestPublishTimeUs;
    private int firstPeriodId;
    private Handler handler;
    private Uri initialManifestUri;
    private final long livePresentationDelayMs;
    private final boolean livePresentationDelayOverridesManifest;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    /* access modifiers changed from: private */
    public Loader loader;
    private DashManifest manifest;
    private final ManifestCallback manifestCallback;
    private final DataSource.Factory manifestDataSourceFactory;
    private final MediaSourceEventListener.EventDispatcher manifestEventDispatcher;
    /* access modifiers changed from: private */
    public IOException manifestFatalError;
    private long manifestLoadEndTimestampMs;
    private final LoaderErrorThrower manifestLoadErrorThrower;
    private boolean manifestLoadPending;
    private long manifestLoadStartTimestampMs;
    private final ParsingLoadable.Parser<? extends DashManifest> manifestParser;
    private Uri manifestUri;
    private final Object manifestUriLock;
    private TransferListener mediaTransferListener;
    private final SparseArray<DashMediaPeriod> periodsById;
    private final PlayerEmsgHandler.PlayerEmsgCallback playerEmsgCallback;
    private final Runnable refreshManifestRunnable;
    private final boolean sideloadedManifest;
    private final Runnable simulateManifestRefreshRunnable;
    private int staleManifestReloadAttempt;
    private final Object tag;

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.dash");
    }

    public static final class Factory implements AdsMediaSource.MediaSourceFactory {
        private final DashChunkSource.Factory chunkSourceFactory;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private boolean isCreateCalled;
        private long livePresentationDelayMs;
        private boolean livePresentationDelayOverridesManifest;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy;
        private final DataSource.Factory manifestDataSourceFactory;
        private ParsingLoadable.Parser<? extends DashManifest> manifestParser;
        private List<StreamKey> streamKeys;
        private Object tag;

        public Factory(DataSource.Factory dataSourceFactory) {
            this(new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory);
        }

        public Factory(DashChunkSource.Factory chunkSourceFactory2, DataSource.Factory manifestDataSourceFactory2) {
            this.chunkSourceFactory = (DashChunkSource.Factory) Assertions.checkNotNull(chunkSourceFactory2);
            this.manifestDataSourceFactory = manifestDataSourceFactory2;
            this.loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
            this.livePresentationDelayMs = 30000;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
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

        @Deprecated
        public Factory setLivePresentationDelayMs(long livePresentationDelayMs2) {
            if (livePresentationDelayMs2 == -1) {
                return setLivePresentationDelayMs(30000, false);
            }
            return setLivePresentationDelayMs(livePresentationDelayMs2, true);
        }

        public Factory setLivePresentationDelayMs(long livePresentationDelayMs2, boolean overridesManifest) {
            Assertions.checkState(!this.isCreateCalled);
            this.livePresentationDelayMs = livePresentationDelayMs2;
            this.livePresentationDelayOverridesManifest = overridesManifest;
            return this;
        }

        public Factory setManifestParser(ParsingLoadable.Parser<? extends DashManifest> manifestParser2) {
            Assertions.checkState(!this.isCreateCalled);
            this.manifestParser = (ParsingLoadable.Parser) Assertions.checkNotNull(manifestParser2);
            return this;
        }

        public Factory setStreamKeys(List<StreamKey> streamKeys2) {
            Assertions.checkState(!this.isCreateCalled);
            this.streamKeys = streamKeys2;
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            this.compositeSequenceableLoaderFactory = (CompositeSequenceableLoaderFactory) Assertions.checkNotNull(compositeSequenceableLoaderFactory2);
            return this;
        }

        public DashMediaSource createMediaSource(DashManifest manifest) {
            Assertions.checkArgument(!manifest.dynamic);
            this.isCreateCalled = true;
            List<StreamKey> list = this.streamKeys;
            if (list != null && !list.isEmpty()) {
                manifest = manifest.copy(this.streamKeys);
            }
            return new DashMediaSource(manifest, (Uri) null, (DataSource.Factory) null, (ParsingLoadable.Parser) null, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Deprecated
        public DashMediaSource createMediaSource(DashManifest manifest, Handler eventHandler, MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifest);
            if (!(eventHandler == null || eventListener == null)) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public DashMediaSource createMediaSource(Uri manifestUri) {
            this.isCreateCalled = true;
            if (this.manifestParser == null) {
                this.manifestParser = new DashManifestParser();
            }
            List<StreamKey> list = this.streamKeys;
            if (list != null) {
                this.manifestParser = new FilteringManifestParser(this.manifestParser, list);
            }
            return new DashMediaSource((DashManifest) null, (Uri) Assertions.checkNotNull(manifestUri), this.manifestDataSourceFactory, this.manifestParser, this.chunkSourceFactory, this.compositeSequenceableLoaderFactory, this.loadErrorHandlingPolicy, this.livePresentationDelayMs, this.livePresentationDelayOverridesManifest, this.tag);
        }

        @Deprecated
        public DashMediaSource createMediaSource(Uri manifestUri, Handler eventHandler, MediaSourceEventListener eventListener) {
            DashMediaSource mediaSource = createMediaSource(manifestUri);
            if (!(eventHandler == null || eventListener == null)) {
                mediaSource.addEventListener(eventHandler, eventListener);
            }
            return mediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{0};
        }
    }

    @Deprecated
    public DashMediaSource(DashManifest manifest2, DashChunkSource.Factory chunkSourceFactory2, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest2, chunkSourceFactory2, 3, eventHandler, eventListener);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    @Deprecated
    public DashMediaSource(DashManifest manifest2, DashChunkSource.Factory chunkSourceFactory2, int minLoadableRetryCount, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifest2, (Uri) null, (DataSource.Factory) null, (ParsingLoadable.Parser<? extends DashManifest>) null, chunkSourceFactory2, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), 30000, false, (Object) null);
        Handler handler2 = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        if (handler2 == null || mediaSourceEventListener == null) {
            return;
        }
        addEventListener(handler2, mediaSourceEventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri2, DataSource.Factory manifestDataSourceFactory2, DashChunkSource.Factory chunkSourceFactory2, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri2, manifestDataSourceFactory2, chunkSourceFactory2, 3, -1, eventHandler, eventListener);
    }

    @Deprecated
    public DashMediaSource(Uri manifestUri2, DataSource.Factory manifestDataSourceFactory2, DashChunkSource.Factory chunkSourceFactory2, int minLoadableRetryCount, long livePresentationDelayMs2, Handler eventHandler, MediaSourceEventListener eventListener) {
        this(manifestUri2, manifestDataSourceFactory2, new DashManifestParser(), chunkSourceFactory2, minLoadableRetryCount, livePresentationDelayMs2, eventHandler, eventListener);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    @Deprecated
    public DashMediaSource(Uri manifestUri2, DataSource.Factory manifestDataSourceFactory2, ParsingLoadable.Parser<? extends DashManifest> manifestParser2, DashChunkSource.Factory chunkSourceFactory2, int minLoadableRetryCount, long livePresentationDelayMs2, Handler eventHandler, MediaSourceEventListener eventListener) {
        this((DashManifest) null, manifestUri2, manifestDataSourceFactory2, manifestParser2, chunkSourceFactory2, new DefaultCompositeSequenceableLoaderFactory(), new DefaultLoadErrorHandlingPolicy(minLoadableRetryCount), livePresentationDelayMs2 == -1 ? 30000 : livePresentationDelayMs2, livePresentationDelayMs2 != -1, (Object) null);
        Handler handler2 = eventHandler;
        MediaSourceEventListener mediaSourceEventListener = eventListener;
        if (handler2 == null || mediaSourceEventListener == null) {
            return;
        }
        addEventListener(handler2, mediaSourceEventListener);
    }

    private DashMediaSource(DashManifest manifest2, Uri manifestUri2, DataSource.Factory manifestDataSourceFactory2, ParsingLoadable.Parser<? extends DashManifest> manifestParser2, DashChunkSource.Factory chunkSourceFactory2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, long livePresentationDelayMs2, boolean livePresentationDelayOverridesManifest2, Object tag2) {
        this.initialManifestUri = manifestUri2;
        this.manifest = manifest2;
        this.manifestUri = manifestUri2;
        this.manifestDataSourceFactory = manifestDataSourceFactory2;
        this.manifestParser = manifestParser2;
        this.chunkSourceFactory = chunkSourceFactory2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
        this.livePresentationDelayMs = livePresentationDelayMs2;
        this.livePresentationDelayOverridesManifest = livePresentationDelayOverridesManifest2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.tag = tag2;
        this.sideloadedManifest = manifest2 != null;
        this.manifestEventDispatcher = createEventDispatcher((MediaSource.MediaPeriodId) null);
        this.manifestUriLock = new Object();
        this.periodsById = new SparseArray<>();
        this.playerEmsgCallback = new DefaultPlayerEmsgCallback();
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        if (this.sideloadedManifest) {
            Assertions.checkState(true ^ manifest2.dynamic);
            this.manifestCallback = null;
            this.refreshManifestRunnable = null;
            this.simulateManifestRefreshRunnable = null;
            this.manifestLoadErrorThrower = new LoaderErrorThrower.Dummy();
            return;
        }
        this.manifestCallback = new ManifestCallback();
        this.manifestLoadErrorThrower = new ManifestLoadErrorThrower();
        this.refreshManifestRunnable = new Runnable() {
            public final void run() {
                DashMediaSource.this.startLoadingManifest();
            }
        };
        this.simulateManifestRefreshRunnable = new Runnable() {
            public final void run() {
                DashMediaSource.this.lambda$new$0$DashMediaSource();
            }
        };
    }

    public /* synthetic */ void lambda$new$0$DashMediaSource() {
        processManifest(false);
    }

    public void replaceManifestUri(Uri manifestUri2) {
        synchronized (this.manifestUriLock) {
            this.manifestUri = manifestUri2;
            this.initialManifestUri = manifestUri2;
        }
    }

    public Object getTag() {
        return this.tag;
    }

    public void prepareSourceInternal(TransferListener mediaTransferListener2) {
        this.mediaTransferListener = mediaTransferListener2;
        if (this.sideloadedManifest) {
            processManifest(false);
            return;
        }
        this.dataSource = this.manifestDataSourceFactory.createDataSource();
        this.loader = new Loader("Loader:DashMediaSource");
        this.handler = new Handler();
        startLoadingManifest();
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        this.manifestLoadErrorThrower.maybeThrowError();
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId periodId, Allocator allocator, long startPositionUs) {
        MediaSource.MediaPeriodId mediaPeriodId = periodId;
        int periodIndex = ((Integer) mediaPeriodId.periodUid).intValue() - this.firstPeriodId;
        MediaSourceEventListener.EventDispatcher periodEventDispatcher = createEventDispatcher(mediaPeriodId, this.manifest.getPeriod(periodIndex).startMs);
        DashManifest dashManifest = this.manifest;
        DashChunkSource.Factory factory = this.chunkSourceFactory;
        TransferListener transferListener = this.mediaTransferListener;
        LoadErrorHandlingPolicy loadErrorHandlingPolicy2 = this.loadErrorHandlingPolicy;
        long j = this.elapsedRealtimeOffsetMs;
        LoaderErrorThrower loaderErrorThrower = this.manifestLoadErrorThrower;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2 = this.compositeSequenceableLoaderFactory;
        CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory3 = compositeSequenceableLoaderFactory2;
        DashMediaPeriod mediaPeriod = new DashMediaPeriod(this.firstPeriodId + periodIndex, dashManifest, periodIndex, factory, transferListener, loadErrorHandlingPolicy2, periodEventDispatcher, j, loaderErrorThrower, allocator, compositeSequenceableLoaderFactory3, this.playerEmsgCallback);
        this.periodsById.put(mediaPeriod.id, mediaPeriod);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        DashMediaPeriod dashMediaPeriod = (DashMediaPeriod) mediaPeriod;
        dashMediaPeriod.release();
        this.periodsById.remove(dashMediaPeriod.id);
    }

    public void releaseSourceInternal() {
        this.manifestLoadPending = false;
        this.dataSource = null;
        Loader loader2 = this.loader;
        if (loader2 != null) {
            loader2.release();
            this.loader = null;
        }
        this.manifestLoadStartTimestampMs = 0;
        this.manifestLoadEndTimestampMs = 0;
        this.manifest = this.sideloadedManifest ? this.manifest : null;
        this.manifestUri = this.initialManifestUri;
        this.manifestFatalError = null;
        Handler handler2 = this.handler;
        if (handler2 != null) {
            handler2.removeCallbacksAndMessages((Object) null);
            this.handler = null;
        }
        this.elapsedRealtimeOffsetMs = 0;
        this.staleManifestReloadAttempt = 0;
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        this.firstPeriodId = 0;
        this.periodsById.clear();
    }

    /* access modifiers changed from: package-private */
    public void onDashManifestRefreshRequested() {
        this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
        startLoadingManifest();
    }

    /* access modifiers changed from: package-private */
    public void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs2) {
        long j = this.expiredManifestPublishTimeUs;
        if (j == C.TIME_UNSET || j < expiredManifestPublishTimeUs2) {
            this.expiredManifestPublishTimeUs = expiredManifestPublishTimeUs2;
        }
    }

    /* access modifiers changed from: package-private */
    public void onManifestLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        long j = elapsedRealtimeMs;
        this.manifestEventDispatcher.loadCompleted(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        DashManifest newManifest = loadable.getResult();
        DashManifest dashManifest = this.manifest;
        boolean isSameUriInstance = false;
        int oldPeriodCount = dashManifest == null ? 0 : dashManifest.getPeriodCount();
        long newFirstPeriodStartTimeMs = newManifest.getPeriod(0).startMs;
        int removedPeriodCount = 0;
        while (removedPeriodCount < oldPeriodCount && this.manifest.getPeriod(removedPeriodCount).startMs < newFirstPeriodStartTimeMs) {
            removedPeriodCount++;
        }
        if (newManifest.dynamic) {
            boolean isManifestStale = false;
            if (oldPeriodCount - removedPeriodCount > newManifest.getPeriodCount()) {
                Log.w(TAG, "Loaded out of sync manifest");
                isManifestStale = true;
            } else if (this.expiredManifestPublishTimeUs != C.TIME_UNSET && newManifest.publishTimeMs * 1000 <= this.expiredManifestPublishTimeUs) {
                Log.w(TAG, "Loaded stale dynamic manifest: " + newManifest.publishTimeMs + ", " + this.expiredManifestPublishTimeUs);
                isManifestStale = true;
            }
            if (isManifestStale) {
                int i = this.staleManifestReloadAttempt;
                this.staleManifestReloadAttempt = i + 1;
                if (i < this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(parsingLoadable.type)) {
                    scheduleManifestRefresh(getManifestLoadRetryDelayMillis());
                    return;
                } else {
                    this.manifestFatalError = new DashManifestStaleException();
                    return;
                }
            } else {
                this.staleManifestReloadAttempt = 0;
            }
        }
        this.manifest = newManifest;
        this.manifestLoadPending &= newManifest.dynamic;
        this.manifestLoadStartTimestampMs = j - loadDurationMs;
        this.manifestLoadEndTimestampMs = j;
        if (this.manifest.location != null) {
            synchronized (this.manifestUriLock) {
                if (parsingLoadable.dataSpec.uri == this.manifestUri) {
                    isSameUriInstance = true;
                }
                if (isSameUriInstance) {
                    this.manifestUri = this.manifest.location;
                }
            }
        }
        if (oldPeriodCount != 0) {
            this.firstPeriodId += removedPeriodCount;
            processManifest(true);
        } else if (!this.manifest.dynamic || this.manifest.utcTiming == null) {
            processManifest(true);
        } else {
            resolveUtcTimingElement(this.manifest.utcTiming);
        }
    }

    /* access modifiers changed from: package-private */
    public Loader.LoadErrorAction onManifestLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<DashManifest> parsingLoadable = loadable;
        boolean isFatal = error instanceof ParserException;
        this.manifestEventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? Loader.DONT_RETRY_FATAL : Loader.RETRY;
    }

    /* access modifiers changed from: package-private */
    public void onUtcTimestampLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<Long> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCompleted(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        onUtcTimestampResolved(loadable.getResult().longValue() - elapsedRealtimeMs);
    }

    /* access modifiers changed from: package-private */
    public Loader.LoadErrorAction onUtcTimestampLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        ParsingLoadable<Long> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, true);
        onUtcTimestampResolutionError(error);
        return Loader.DONT_RETRY;
    }

    /* access modifiers changed from: package-private */
    public void onLoadCanceled(ParsingLoadable<?> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        ParsingLoadable<?> parsingLoadable = loadable;
        this.manifestEventDispatcher.loadCanceled(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), parsingLoadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    private void resolveUtcTimingElement(UtcTimingElement timingElement) {
        String scheme = timingElement.schemeIdUri;
        if (Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:direct:2012")) {
            resolveUtcTimingElementDirect(timingElement);
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-iso:2012")) {
            resolveUtcTimingElementHttp(timingElement, new Iso8601Parser());
        } else if (Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2014") || Util.areEqual(scheme, "urn:mpeg:dash:utc:http-xsdate:2012")) {
            resolveUtcTimingElementHttp(timingElement, new XsDateTimeParser());
        } else {
            onUtcTimestampResolutionError(new IOException("Unsupported UTC timing scheme"));
        }
    }

    private void resolveUtcTimingElementDirect(UtcTimingElement timingElement) {
        try {
            onUtcTimestampResolved(Util.parseXsDateTime(timingElement.value) - this.manifestLoadEndTimestampMs);
        } catch (ParserException e) {
            onUtcTimestampResolutionError(e);
        }
    }

    private void resolveUtcTimingElementHttp(UtcTimingElement timingElement, ParsingLoadable.Parser<Long> parser) {
        startLoading(new ParsingLoadable(this.dataSource, Uri.parse(timingElement.value), 5, parser), new UtcTimestampCallback(), 1);
    }

    private void onUtcTimestampResolved(long elapsedRealtimeOffsetMs2) {
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs2;
        processManifest(true);
    }

    private void onUtcTimestampResolutionError(IOException error) {
        Log.e(TAG, "Failed to resolve UtcTiming element.", error);
        processManifest(true);
    }

    private void processManifest(boolean scheduleRefresh) {
        boolean windowChangingImplicitly;
        for (int i = 0; i < this.periodsById.size(); i++) {
            int id = this.periodsById.keyAt(i);
            if (id >= this.firstPeriodId) {
                this.periodsById.valueAt(i).updateManifest(this.manifest, id - this.firstPeriodId);
            }
        }
        boolean windowChangingImplicitly2 = false;
        int lastPeriodIndex = this.manifest.getPeriodCount() - 1;
        PeriodSeekInfo firstPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(0), this.manifest.getPeriodDurationUs(0));
        PeriodSeekInfo lastPeriodSeekInfo = PeriodSeekInfo.createPeriodSeekInfo(this.manifest.getPeriod(lastPeriodIndex), this.manifest.getPeriodDurationUs(lastPeriodIndex));
        long currentStartTimeUs = firstPeriodSeekInfo.availableStartTimeUs;
        long currentEndTimeUs = lastPeriodSeekInfo.availableEndTimeUs;
        if (!this.manifest.dynamic || lastPeriodSeekInfo.isIndexExplicit) {
            int i2 = lastPeriodIndex;
            PeriodSeekInfo periodSeekInfo = lastPeriodSeekInfo;
            windowChangingImplicitly = false;
        } else {
            PeriodSeekInfo periodSeekInfo2 = lastPeriodSeekInfo;
            currentEndTimeUs = Math.min((getNowUnixTimeUs() - C.msToUs(this.manifest.availabilityStartTimeMs)) - C.msToUs(this.manifest.getPeriod(lastPeriodIndex).startMs), currentEndTimeUs);
            if (this.manifest.timeShiftBufferDepthMs != C.TIME_UNSET) {
                int periodIndex = lastPeriodIndex;
                long offsetInPeriodUs = currentEndTimeUs - C.msToUs(this.manifest.timeShiftBufferDepthMs);
                while (offsetInPeriodUs < 0 && periodIndex > 0) {
                    periodIndex--;
                    offsetInPeriodUs += this.manifest.getPeriodDurationUs(periodIndex);
                    windowChangingImplicitly2 = windowChangingImplicitly2;
                }
                if (periodIndex == 0) {
                    currentStartTimeUs = Math.max(currentStartTimeUs, offsetInPeriodUs);
                    int i3 = lastPeriodIndex;
                } else {
                    int i4 = lastPeriodIndex;
                    currentStartTimeUs = this.manifest.getPeriodDurationUs(0);
                }
            } else {
                int i5 = lastPeriodIndex;
            }
            windowChangingImplicitly = true;
        }
        long windowDurationUs = currentEndTimeUs - currentStartTimeUs;
        for (int i6 = 0; i6 < this.manifest.getPeriodCount() - 1; i6++) {
            windowDurationUs += this.manifest.getPeriodDurationUs(i6);
        }
        long windowDefaultStartPositionUs = 0;
        if (this.manifest.dynamic) {
            long presentationDelayForManifestMs = this.livePresentationDelayMs;
            if (!this.livePresentationDelayOverridesManifest && this.manifest.suggestedPresentationDelayMs != C.TIME_UNSET) {
                presentationDelayForManifestMs = this.manifest.suggestedPresentationDelayMs;
            }
            windowDefaultStartPositionUs = windowDurationUs - C.msToUs(presentationDelayForManifestMs);
            if (windowDefaultStartPositionUs < MIN_LIVE_DEFAULT_START_POSITION_US) {
                PeriodSeekInfo periodSeekInfo3 = firstPeriodSeekInfo;
                windowDefaultStartPositionUs = Math.min(MIN_LIVE_DEFAULT_START_POSITION_US, windowDurationUs / 2);
            }
        }
        long windowStartTimeMs = this.manifest.availabilityStartTimeMs + this.manifest.getPeriod(0).startMs + C.usToMs(currentStartTimeUs);
        long j = currentEndTimeUs;
        refreshSourceInfo(new DashTimeline(this.manifest.availabilityStartTimeMs, windowStartTimeMs, this.firstPeriodId, currentStartTimeUs, windowDurationUs, windowDefaultStartPositionUs, this.manifest, this.tag), this.manifest);
        if (!this.sideloadedManifest) {
            this.handler.removeCallbacks(this.simulateManifestRefreshRunnable);
            if (windowChangingImplicitly) {
                this.handler.postDelayed(this.simulateManifestRefreshRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
            if (this.manifestLoadPending) {
                startLoadingManifest();
                boolean z = windowChangingImplicitly;
                long j2 = windowStartTimeMs;
                long j3 = windowDurationUs;
            } else if (!scheduleRefresh || !this.manifest.dynamic || this.manifest.minUpdatePeriodMs == C.TIME_UNSET) {
                long j4 = windowStartTimeMs;
                long j5 = windowDurationUs;
            } else {
                long minUpdatePeriodMs = this.manifest.minUpdatePeriodMs;
                if (minUpdatePeriodMs == 0) {
                    minUpdatePeriodMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                }
                boolean z2 = windowChangingImplicitly;
                long j6 = windowStartTimeMs;
                long j7 = windowDurationUs;
                scheduleManifestRefresh(Math.max(0, (this.manifestLoadStartTimestampMs + minUpdatePeriodMs) - SystemClock.elapsedRealtime()));
            }
        } else {
            long j8 = windowStartTimeMs;
            long j9 = windowDurationUs;
        }
    }

    private void scheduleManifestRefresh(long delayUntilNextLoadMs) {
        this.handler.postDelayed(this.refreshManifestRunnable, delayUntilNextLoadMs);
    }

    /* access modifiers changed from: private */
    public void startLoadingManifest() {
        Uri manifestUri2;
        this.handler.removeCallbacks(this.refreshManifestRunnable);
        if (this.loader.isLoading()) {
            this.manifestLoadPending = true;
            return;
        }
        synchronized (this.manifestUriLock) {
            manifestUri2 = this.manifestUri;
        }
        this.manifestLoadPending = false;
        startLoading(new ParsingLoadable(this.dataSource, manifestUri2, 4, this.manifestParser), this.manifestCallback, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(4));
    }

    private long getManifestLoadRetryDelayMillis() {
        return (long) Math.min((this.staleManifestReloadAttempt - 1) * 1000, 5000);
    }

    private <T> void startLoading(ParsingLoadable<T> loadable, Loader.Callback<ParsingLoadable<T>> callback, int minRetryCount) {
        this.manifestEventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.loader.startLoading(loadable, callback, minRetryCount));
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return C.msToUs(SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs);
        }
        return C.msToUs(System.currentTimeMillis());
    }

    private static final class PeriodSeekInfo {
        public final long availableEndTimeUs;
        public final long availableStartTimeUs;
        public final boolean isIndexExplicit;

        public static PeriodSeekInfo createPeriodSeekInfo(Period period, long durationUs) {
            Period period2 = period;
            long j = durationUs;
            int adaptationSetCount = period2.adaptationSets.size();
            boolean haveAudioVideoAdaptationSets = false;
            int i = 0;
            while (true) {
                if (i >= adaptationSetCount) {
                    break;
                }
                int type = period2.adaptationSets.get(i).type;
                if (type == 1 || type == 2) {
                    haveAudioVideoAdaptationSets = true;
                } else {
                    i++;
                }
            }
            haveAudioVideoAdaptationSets = true;
            long availableStartTimeUs2 = 0;
            boolean isIndexExplicit2 = false;
            boolean seenEmptyIndex = false;
            int i2 = 0;
            long availableEndTimeUs2 = Long.MAX_VALUE;
            while (i2 < adaptationSetCount) {
                AdaptationSet adaptationSet = period2.adaptationSets.get(i2);
                if (!haveAudioVideoAdaptationSets || adaptationSet.type != 3) {
                    DashSegmentIndex index = adaptationSet.representations.get(0).getIndex();
                    if (index == null) {
                        AdaptationSet adaptationSet2 = adaptationSet;
                        DashSegmentIndex dashSegmentIndex = index;
                        return new PeriodSeekInfo(true, 0, durationUs);
                    }
                    DashSegmentIndex index2 = index;
                    boolean isIndexExplicit3 = isIndexExplicit2 | index2.isExplicit();
                    DashSegmentIndex index3 = index2;
                    int segmentCount = index3.getSegmentCount(j);
                    if (segmentCount == 0) {
                        availableEndTimeUs2 = 0;
                        isIndexExplicit2 = isIndexExplicit3;
                        seenEmptyIndex = true;
                        availableStartTimeUs2 = 0;
                    } else if (!seenEmptyIndex) {
                        long firstSegmentNum = index3.getFirstSegmentNum();
                        boolean isIndexExplicit4 = isIndexExplicit3;
                        long adaptationSetAvailableStartTimeUs = index3.getTimeUs(firstSegmentNum);
                        availableStartTimeUs2 = Math.max(availableStartTimeUs2, adaptationSetAvailableStartTimeUs);
                        long j2 = adaptationSetAvailableStartTimeUs;
                        if (segmentCount != -1) {
                            long lastSegmentNum = (((long) segmentCount) + firstSegmentNum) - 1;
                            long j3 = lastSegmentNum;
                            availableEndTimeUs2 = Math.min(availableEndTimeUs2, index3.getTimeUs(lastSegmentNum) + index3.getDurationUs(lastSegmentNum, j));
                            isIndexExplicit2 = isIndexExplicit4;
                        } else {
                            isIndexExplicit2 = isIndexExplicit4;
                        }
                    } else {
                        isIndexExplicit2 = isIndexExplicit3;
                    }
                }
                i2++;
                period2 = period;
            }
            long j4 = availableStartTimeUs2;
            return new PeriodSeekInfo(isIndexExplicit2, availableStartTimeUs2, availableEndTimeUs2);
        }

        private PeriodSeekInfo(boolean isIndexExplicit2, long availableStartTimeUs2, long availableEndTimeUs2) {
            this.isIndexExplicit = isIndexExplicit2;
            this.availableStartTimeUs = availableStartTimeUs2;
            this.availableEndTimeUs = availableEndTimeUs2;
        }
    }

    private static final class DashTimeline extends Timeline {
        private final int firstPeriodId;
        private final DashManifest manifest;
        private final long offsetInFirstPeriodUs;
        private final long presentationStartTimeMs;
        private final long windowDefaultStartPositionUs;
        private final long windowDurationUs;
        private final long windowStartTimeMs;
        private final Object windowTag;

        public DashTimeline(long presentationStartTimeMs2, long windowStartTimeMs2, int firstPeriodId2, long offsetInFirstPeriodUs2, long windowDurationUs2, long windowDefaultStartPositionUs2, DashManifest manifest2, Object windowTag2) {
            this.presentationStartTimeMs = presentationStartTimeMs2;
            this.windowStartTimeMs = windowStartTimeMs2;
            this.firstPeriodId = firstPeriodId2;
            this.offsetInFirstPeriodUs = offsetInFirstPeriodUs2;
            this.windowDurationUs = windowDurationUs2;
            this.windowDefaultStartPositionUs = windowDefaultStartPositionUs2;
            this.manifest = manifest2;
            this.windowTag = windowTag2;
        }

        public int getPeriodCount() {
            return this.manifest.getPeriodCount();
        }

        public Timeline.Period getPeriod(int periodIndex, Timeline.Period period, boolean setIdentifiers) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            Integer uid = null;
            Object id = setIdentifiers ? this.manifest.getPeriod(periodIndex).id : null;
            if (setIdentifiers) {
                uid = Integer.valueOf(this.firstPeriodId + periodIndex);
            }
            return period.set(id, uid, 0, this.manifest.getPeriodDurationUs(periodIndex), C.msToUs(this.manifest.getPeriod(periodIndex).startMs - this.manifest.getPeriod(0).startMs) - this.offsetInFirstPeriodUs);
        }

        public int getWindowCount() {
            return 1;
        }

        public Timeline.Window getWindow(int windowIndex, Timeline.Window window, boolean setTag, long defaultPositionProjectionUs) {
            Assertions.checkIndex(windowIndex, 0, 1);
            return window.set(setTag ? this.windowTag : null, this.presentationStartTimeMs, this.windowStartTimeMs, true, this.manifest.dynamic && this.manifest.minUpdatePeriodMs != C.TIME_UNSET && this.manifest.durationMs == C.TIME_UNSET, getAdjustedWindowDefaultStartPositionUs(defaultPositionProjectionUs), this.windowDurationUs, 0, getPeriodCount() - 1, this.offsetInFirstPeriodUs);
        }

        public int getIndexOfPeriod(Object uid) {
            int periodIndex;
            if ((uid instanceof Integer) && (periodIndex = ((Integer) uid).intValue() - this.firstPeriodId) >= 0 && periodIndex < getPeriodCount()) {
                return periodIndex;
            }
            return -1;
        }

        private long getAdjustedWindowDefaultStartPositionUs(long defaultPositionProjectionUs) {
            DashSegmentIndex snapIndex;
            long windowDefaultStartPositionUs2 = this.windowDefaultStartPositionUs;
            if (!this.manifest.dynamic) {
                return windowDefaultStartPositionUs2;
            }
            if (defaultPositionProjectionUs > 0) {
                windowDefaultStartPositionUs2 += defaultPositionProjectionUs;
                if (windowDefaultStartPositionUs2 > this.windowDurationUs) {
                    return C.TIME_UNSET;
                }
            }
            int periodIndex = 0;
            long defaultStartPositionInPeriodUs = this.offsetInFirstPeriodUs + windowDefaultStartPositionUs2;
            long periodDurationUs = this.manifest.getPeriodDurationUs(0);
            while (periodIndex < this.manifest.getPeriodCount() - 1 && defaultStartPositionInPeriodUs >= periodDurationUs) {
                defaultStartPositionInPeriodUs -= periodDurationUs;
                periodIndex++;
                periodDurationUs = this.manifest.getPeriodDurationUs(periodIndex);
            }
            Period period = this.manifest.getPeriod(periodIndex);
            int videoAdaptationSetIndex = period.getAdaptationSetIndex(2);
            if (videoAdaptationSetIndex == -1 || (snapIndex = period.adaptationSets.get(videoAdaptationSetIndex).representations.get(0).getIndex()) == null || snapIndex.getSegmentCount(periodDurationUs) == 0) {
                return windowDefaultStartPositionUs2;
            }
            return (snapIndex.getTimeUs(snapIndex.getSegmentNum(defaultStartPositionInPeriodUs, periodDurationUs)) + windowDefaultStartPositionUs2) - defaultStartPositionInPeriodUs;
        }

        public Object getUidOfPeriod(int periodIndex) {
            Assertions.checkIndex(periodIndex, 0, getPeriodCount());
            return Integer.valueOf(this.firstPeriodId + periodIndex);
        }
    }

    private final class DefaultPlayerEmsgCallback implements PlayerEmsgHandler.PlayerEmsgCallback {
        private DefaultPlayerEmsgCallback() {
        }

        public void onDashManifestRefreshRequested() {
            DashMediaSource.this.onDashManifestRefreshRequested();
        }

        public void onDashManifestPublishTimeExpired(long expiredManifestPublishTimeUs) {
            DashMediaSource.this.onDashManifestPublishTimeExpired(expiredManifestPublishTimeUs);
        }
    }

    private final class ManifestCallback implements Loader.Callback<ParsingLoadable<DashManifest>> {
        private ManifestCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onManifestLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public Loader.LoadErrorAction onLoadError(ParsingLoadable<DashManifest> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onManifestLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    private final class UtcTimestampCallback implements Loader.Callback<ParsingLoadable<Long>> {
        private UtcTimestampCallback() {
        }

        public void onLoadCompleted(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            DashMediaSource.this.onUtcTimestampLoadCompleted(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public void onLoadCanceled(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DashMediaSource.this.onLoadCanceled(loadable, elapsedRealtimeMs, loadDurationMs);
        }

        public Loader.LoadErrorAction onLoadError(ParsingLoadable<Long> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            return DashMediaSource.this.onUtcTimestampLoadError(loadable, elapsedRealtimeMs, loadDurationMs, error);
        }
    }

    private static final class XsDateTimeParser implements ParsingLoadable.Parser<Long> {
        private XsDateTimeParser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            return Long.valueOf(Util.parseXsDateTime(new BufferedReader(new InputStreamReader(inputStream)).readLine()));
        }
    }

    static final class Iso8601Parser implements ParsingLoadable.Parser<Long> {
        private static final Pattern TIMESTAMP_WITH_TIMEZONE_PATTERN = Pattern.compile("(.+?)(Z|((\\+|-|−)(\\d\\d)(:?(\\d\\d))?))");

        Iso8601Parser() {
        }

        public Long parse(Uri uri, InputStream inputStream) throws IOException {
            String firstLine = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8"))).readLine();
            try {
                Matcher matcher = TIMESTAMP_WITH_TIMEZONE_PATTERN.matcher(firstLine);
                if (matcher.matches()) {
                    String timestampWithoutTimezone = matcher.group(1);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    long timestampMs = format.parse(timestampWithoutTimezone).getTime();
                    if (!"Z".equals(matcher.group(2))) {
                        long sign = Marker.ANY_NON_NULL_MARKER.equals(matcher.group(4)) ? 1 : -1;
                        long hours = Long.parseLong(matcher.group(5));
                        String minutesString = matcher.group(7);
                        timestampMs -= ((((hours * 60) + (TextUtils.isEmpty(minutesString) ? 0 : Long.parseLong(minutesString))) * 60) * 1000) * sign;
                    }
                    return Long.valueOf(timestampMs);
                }
                throw new ParserException("Couldn't parse timestamp: " + firstLine);
            } catch (ParseException e) {
                throw new ParserException((Throwable) e);
            }
        }
    }

    final class ManifestLoadErrorThrower implements LoaderErrorThrower {
        ManifestLoadErrorThrower() {
        }

        public void maybeThrowError() throws IOException {
            DashMediaSource.this.loader.maybeThrowError();
            maybeThrowManifestError();
        }

        public void maybeThrowError(int minRetryCount) throws IOException {
            DashMediaSource.this.loader.maybeThrowError(minRetryCount);
            maybeThrowManifestError();
        }

        private void maybeThrowManifestError() throws IOException {
            if (DashMediaSource.this.manifestFatalError != null) {
                throw DashMediaSource.this.manifestFatalError;
            }
        }
    }
}
