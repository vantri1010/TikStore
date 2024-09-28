package com.google.android.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public final class DefaultHlsPlaylistTracker implements HlsPlaylistTracker, Loader.Callback<ParsingLoadable<HlsPlaylist>> {
    public static final HlsPlaylistTracker.Factory FACTORY = $$Lambda$lKTLOVxne0MoBOOliKH0gO2KDMM.INSTANCE;
    private static final double PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT = 3.5d;
    /* access modifiers changed from: private */
    public final HlsDataSourceFactory dataSourceFactory;
    /* access modifiers changed from: private */
    public MediaSourceEventListener.EventDispatcher eventDispatcher;
    private Loader initialPlaylistLoader;
    private long initialStartTimeUs;
    private boolean isLive;
    private final List<HlsPlaylistTracker.PlaylistEventListener> listeners;
    /* access modifiers changed from: private */
    public final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    /* access modifiers changed from: private */
    public HlsMasterPlaylist masterPlaylist;
    /* access modifiers changed from: private */
    public ParsingLoadable.Parser<HlsPlaylist> mediaPlaylistParser;
    private final IdentityHashMap<HlsMasterPlaylist.HlsUrl, MediaPlaylistBundle> playlistBundles;
    private final HlsPlaylistParserFactory playlistParserFactory;
    /* access modifiers changed from: private */
    public Handler playlistRefreshHandler;
    /* access modifiers changed from: private */
    public HlsMasterPlaylist.HlsUrl primaryHlsUrl;
    private HlsPlaylistTracker.PrimaryPlaylistListener primaryPlaylistListener;
    private HlsMediaPlaylist primaryUrlSnapshot;

    @Deprecated
    public DefaultHlsPlaylistTracker(HlsDataSourceFactory dataSourceFactory2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, ParsingLoadable.Parser<HlsPlaylist> playlistParser) {
        this(dataSourceFactory2, loadErrorHandlingPolicy2, createFixedFactory(playlistParser));
    }

    public DefaultHlsPlaylistTracker(HlsDataSourceFactory dataSourceFactory2, LoadErrorHandlingPolicy loadErrorHandlingPolicy2, HlsPlaylistParserFactory playlistParserFactory2) {
        this.dataSourceFactory = dataSourceFactory2;
        this.playlistParserFactory = playlistParserFactory2;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy2;
        this.listeners = new ArrayList();
        this.playlistBundles = new IdentityHashMap<>();
        this.initialStartTimeUs = C.TIME_UNSET;
    }

    public void start(Uri initialPlaylistUri, MediaSourceEventListener.EventDispatcher eventDispatcher2, HlsPlaylistTracker.PrimaryPlaylistListener primaryPlaylistListener2) {
        this.playlistRefreshHandler = new Handler();
        this.eventDispatcher = eventDispatcher2;
        this.primaryPlaylistListener = primaryPlaylistListener2;
        ParsingLoadable<HlsPlaylist> masterPlaylistLoadable = new ParsingLoadable<>(this.dataSourceFactory.createDataSource(4), initialPlaylistUri, 4, this.playlistParserFactory.createPlaylistParser());
        Assertions.checkState(this.initialPlaylistLoader == null);
        Loader loader = new Loader("DefaultHlsPlaylistTracker:MasterPlaylist");
        this.initialPlaylistLoader = loader;
        eventDispatcher2.loadStarted(masterPlaylistLoadable.dataSpec, masterPlaylistLoadable.type, loader.startLoading(masterPlaylistLoadable, this, this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(masterPlaylistLoadable.type)));
    }

    public void stop() {
        this.primaryHlsUrl = null;
        this.primaryUrlSnapshot = null;
        this.masterPlaylist = null;
        this.initialStartTimeUs = C.TIME_UNSET;
        this.initialPlaylistLoader.release();
        this.initialPlaylistLoader = null;
        for (MediaPlaylistBundle bundle : this.playlistBundles.values()) {
            bundle.release();
        }
        this.playlistRefreshHandler.removeCallbacksAndMessages((Object) null);
        this.playlistRefreshHandler = null;
        this.playlistBundles.clear();
    }

    public void addListener(HlsPlaylistTracker.PlaylistEventListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(HlsPlaylistTracker.PlaylistEventListener listener) {
        this.listeners.remove(listener);
    }

    public HlsMasterPlaylist getMasterPlaylist() {
        return this.masterPlaylist;
    }

    public HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl url, boolean isForPlayback) {
        HlsMediaPlaylist snapshot = this.playlistBundles.get(url).getPlaylistSnapshot();
        if (snapshot != null && isForPlayback) {
            maybeSetPrimaryUrl(url);
        }
        return snapshot;
    }

    public long getInitialStartTimeUs() {
        return this.initialStartTimeUs;
    }

    public boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl url) {
        return this.playlistBundles.get(url).isSnapshotValid();
    }

    public void maybeThrowPrimaryPlaylistRefreshError() throws IOException {
        Loader loader = this.initialPlaylistLoader;
        if (loader != null) {
            loader.maybeThrowError();
        }
        HlsMasterPlaylist.HlsUrl hlsUrl = this.primaryHlsUrl;
        if (hlsUrl != null) {
            maybeThrowPlaylistRefreshError(hlsUrl);
        }
    }

    public void maybeThrowPlaylistRefreshError(HlsMasterPlaylist.HlsUrl url) throws IOException {
        this.playlistBundles.get(url).maybeThrowPlaylistRefreshError();
    }

    public void refreshPlaylist(HlsMasterPlaylist.HlsUrl url) {
        this.playlistBundles.get(url).loadPlaylist();
    }

    public boolean isLive() {
        return this.isLive;
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        HlsMasterPlaylist masterPlaylist2;
        HlsPlaylist result = loadable.getResult();
        boolean isMediaPlaylist = result instanceof HlsMediaPlaylist;
        if (isMediaPlaylist) {
            masterPlaylist2 = HlsMasterPlaylist.createSingleVariantMasterPlaylist(result.baseUri);
        } else {
            masterPlaylist2 = (HlsMasterPlaylist) result;
        }
        this.masterPlaylist = masterPlaylist2;
        this.mediaPlaylistParser = this.playlistParserFactory.createPlaylistParser(masterPlaylist2);
        this.primaryHlsUrl = masterPlaylist2.variants.get(0);
        ArrayList<HlsMasterPlaylist.HlsUrl> urls = new ArrayList<>();
        urls.addAll(masterPlaylist2.variants);
        urls.addAll(masterPlaylist2.audios);
        urls.addAll(masterPlaylist2.subtitles);
        createBundles(urls);
        MediaPlaylistBundle primaryBundle = this.playlistBundles.get(this.primaryHlsUrl);
        if (isMediaPlaylist) {
            primaryBundle.processLoadedPlaylist((HlsMediaPlaylist) result, loadDurationMs);
        } else {
            long j = loadDurationMs;
            primaryBundle.loadPlaylist();
        }
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public Loader.LoadErrorAction onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
        ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
        long retryDelayMs = this.loadErrorHandlingPolicy.getRetryDelayMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
        boolean isFatal = retryDelayMs == C.TIME_UNSET;
        this.eventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        if (isFatal) {
            return Loader.DONT_RETRY_FATAL;
        }
        return Loader.createRetryAction(false, retryDelayMs);
    }

    /* access modifiers changed from: private */
    public boolean maybeSelectNewPrimaryUrl() {
        List<HlsMasterPlaylist.HlsUrl> variants = this.masterPlaylist.variants;
        int variantsSize = variants.size();
        long currentTimeMs = SystemClock.elapsedRealtime();
        for (int i = 0; i < variantsSize; i++) {
            MediaPlaylistBundle bundle = this.playlistBundles.get(variants.get(i));
            if (currentTimeMs > bundle.blacklistUntilMs) {
                this.primaryHlsUrl = bundle.playlistUrl;
                bundle.loadPlaylist();
                return true;
            }
        }
        return false;
    }

    private void maybeSetPrimaryUrl(HlsMasterPlaylist.HlsUrl url) {
        if (url != this.primaryHlsUrl && this.masterPlaylist.variants.contains(url)) {
            HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
            if (hlsMediaPlaylist == null || !hlsMediaPlaylist.hasEndTag) {
                this.primaryHlsUrl = url;
                this.playlistBundles.get(url).loadPlaylist();
            }
        }
    }

    private void createBundles(List<HlsMasterPlaylist.HlsUrl> urls) {
        int listSize = urls.size();
        for (int i = 0; i < listSize; i++) {
            HlsMasterPlaylist.HlsUrl url = urls.get(i);
            this.playlistBundles.put(url, new MediaPlaylistBundle(url));
        }
    }

    /* access modifiers changed from: private */
    public void onPlaylistUpdated(HlsMasterPlaylist.HlsUrl url, HlsMediaPlaylist newSnapshot) {
        if (url == this.primaryHlsUrl) {
            if (this.primaryUrlSnapshot == null) {
                this.isLive = !newSnapshot.hasEndTag;
                this.initialStartTimeUs = newSnapshot.startTimeUs;
            }
            this.primaryUrlSnapshot = newSnapshot;
            this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(newSnapshot);
        }
        int listenersSize = this.listeners.size();
        for (int i = 0; i < listenersSize; i++) {
            this.listeners.get(i).onPlaylistChanged();
        }
    }

    /* access modifiers changed from: private */
    public boolean notifyPlaylistError(HlsMasterPlaylist.HlsUrl playlistUrl, long blacklistDurationMs) {
        int listenersSize = this.listeners.size();
        boolean anyBlacklistingFailed = false;
        for (int i = 0; i < listenersSize; i++) {
            anyBlacklistingFailed |= !this.listeners.get(i).onPlaylistError(playlistUrl, blacklistDurationMs);
        }
        return anyBlacklistingFailed;
    }

    /* access modifiers changed from: private */
    public HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        if (loadedPlaylist.isNewerThan(oldPlaylist)) {
            return loadedPlaylist.copyWith(getLoadedPlaylistStartTimeUs(oldPlaylist, loadedPlaylist), getLoadedPlaylistDiscontinuitySequence(oldPlaylist, loadedPlaylist));
        }
        if (loadedPlaylist.hasEndTag) {
            return oldPlaylist.copyWithEndTag();
        }
        return oldPlaylist;
    }

    private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        if (loadedPlaylist.hasProgramDateTime) {
            return loadedPlaylist.startTimeUs;
        }
        HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
        long primarySnapshotStartTimeUs = hlsMediaPlaylist != null ? hlsMediaPlaylist.startTimeUs : 0;
        if (oldPlaylist == null) {
            return primarySnapshotStartTimeUs;
        }
        int oldPlaylistSize = oldPlaylist.segments.size();
        HlsMediaPlaylist.Segment firstOldOverlappingSegment = getFirstOldOverlappingSegment(oldPlaylist, loadedPlaylist);
        if (firstOldOverlappingSegment != null) {
            return oldPlaylist.startTimeUs + firstOldOverlappingSegment.relativeStartTimeUs;
        }
        if (((long) oldPlaylistSize) == loadedPlaylist.mediaSequence - oldPlaylist.mediaSequence) {
            return oldPlaylist.getEndTimeUs();
        }
        return primarySnapshotStartTimeUs;
    }

    private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        HlsMediaPlaylist.Segment firstOldOverlappingSegment;
        if (loadedPlaylist.hasDiscontinuitySequence) {
            return loadedPlaylist.discontinuitySequence;
        }
        HlsMediaPlaylist hlsMediaPlaylist = this.primaryUrlSnapshot;
        int primaryUrlDiscontinuitySequence = hlsMediaPlaylist != null ? hlsMediaPlaylist.discontinuitySequence : 0;
        if (oldPlaylist == null || (firstOldOverlappingSegment = getFirstOldOverlappingSegment(oldPlaylist, loadedPlaylist)) == null) {
            return primaryUrlDiscontinuitySequence;
        }
        return (oldPlaylist.discontinuitySequence + firstOldOverlappingSegment.relativeDiscontinuitySequence) - loadedPlaylist.segments.get(0).relativeDiscontinuitySequence;
    }

    private static HlsMediaPlaylist.Segment getFirstOldOverlappingSegment(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist loadedPlaylist) {
        int mediaSequenceOffset = (int) (loadedPlaylist.mediaSequence - oldPlaylist.mediaSequence);
        List<HlsMediaPlaylist.Segment> oldSegments = oldPlaylist.segments;
        if (mediaSequenceOffset < oldSegments.size()) {
            return oldSegments.get(mediaSequenceOffset);
        }
        return null;
    }

    private final class MediaPlaylistBundle implements Loader.Callback<ParsingLoadable<HlsPlaylist>>, Runnable {
        /* access modifiers changed from: private */
        public long blacklistUntilMs;
        private long earliestNextLoadTimeMs;
        private long lastSnapshotChangeMs;
        private long lastSnapshotLoadMs;
        private boolean loadPending;
        private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
        private final Loader mediaPlaylistLoader = new Loader("DefaultHlsPlaylistTracker:MediaPlaylist");
        private IOException playlistError;
        private HlsMediaPlaylist playlistSnapshot;
        /* access modifiers changed from: private */
        public final HlsMasterPlaylist.HlsUrl playlistUrl;

        public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl playlistUrl2) {
            this.playlistUrl = playlistUrl2;
            this.mediaPlaylistLoadable = new ParsingLoadable<>(DefaultHlsPlaylistTracker.this.dataSourceFactory.createDataSource(4), UriUtil.resolveToUri(DefaultHlsPlaylistTracker.this.masterPlaylist.baseUri, playlistUrl2.url), 4, DefaultHlsPlaylistTracker.this.mediaPlaylistParser);
        }

        public HlsMediaPlaylist getPlaylistSnapshot() {
            return this.playlistSnapshot;
        }

        public boolean isSnapshotValid() {
            if (this.playlistSnapshot == null) {
                return false;
            }
            long currentTimeMs = SystemClock.elapsedRealtime();
            long snapshotValidityDurationMs = Math.max(30000, C.usToMs(this.playlistSnapshot.durationUs));
            if (this.playlistSnapshot.hasEndTag || this.playlistSnapshot.playlistType == 2 || this.playlistSnapshot.playlistType == 1 || this.lastSnapshotLoadMs + snapshotValidityDurationMs > currentTimeMs) {
                return true;
            }
            return false;
        }

        public void release() {
            this.mediaPlaylistLoader.release();
        }

        public void loadPlaylist() {
            this.blacklistUntilMs = 0;
            if (!this.loadPending && !this.mediaPlaylistLoader.isLoading()) {
                long currentTimeMs = SystemClock.elapsedRealtime();
                if (currentTimeMs < this.earliestNextLoadTimeMs) {
                    this.loadPending = true;
                    DefaultHlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, this.earliestNextLoadTimeMs - currentTimeMs);
                    return;
                }
                loadPlaylistImmediately();
            }
        }

        public void maybeThrowPlaylistRefreshError() throws IOException {
            this.mediaPlaylistLoader.maybeThrowError();
            IOException iOException = this.playlistError;
            if (iOException != null) {
                throw iOException;
            }
        }

        public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            HlsPlaylist result = loadable.getResult();
            if (result instanceof HlsMediaPlaylist) {
                processLoadedPlaylist((HlsMediaPlaylist) result, loadDurationMs);
                DefaultHlsPlaylistTracker.this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
                return;
            }
            ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
            long j = loadDurationMs;
            this.playlistError = new ParserException("Loaded playlist has unexpected type.");
        }

        public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadCanceled(loadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        }

        public Loader.LoadErrorAction onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error, int errorCount) {
            Loader.LoadErrorAction loadErrorAction;
            ParsingLoadable<HlsPlaylist> parsingLoadable = loadable;
            long blacklistDurationMs = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
            boolean shouldBlacklist = blacklistDurationMs != C.TIME_UNSET;
            boolean blacklistingFailed = DefaultHlsPlaylistTracker.this.notifyPlaylistError(this.playlistUrl, blacklistDurationMs) || !shouldBlacklist;
            if (shouldBlacklist) {
                blacklistingFailed |= blacklistPlaylist(blacklistDurationMs);
            }
            if (blacklistingFailed) {
                long retryDelay = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getRetryDelayMsFor(parsingLoadable.type, loadDurationMs, error, errorCount);
                loadErrorAction = retryDelay != C.TIME_UNSET ? Loader.createRetryAction(false, retryDelay) : Loader.DONT_RETRY_FATAL;
            } else {
                loadErrorAction = Loader.DONT_RETRY;
            }
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadError(parsingLoadable.dataSpec, loadable.getUri(), loadable.getResponseHeaders(), 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, !loadErrorAction.isRetry());
            return loadErrorAction;
        }

        public void run() {
            this.loadPending = false;
            loadPlaylistImmediately();
        }

        private void loadPlaylistImmediately() {
            DefaultHlsPlaylistTracker.this.eventDispatcher.loadStarted(this.mediaPlaylistLoadable.dataSpec, this.mediaPlaylistLoadable.type, this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getMinimumLoadableRetryCount(this.mediaPlaylistLoadable.type)));
        }

        /* access modifiers changed from: private */
        public void processLoadedPlaylist(HlsMediaPlaylist loadedPlaylist, long loadDurationMs) {
            HlsMediaPlaylist hlsMediaPlaylist = loadedPlaylist;
            HlsMediaPlaylist oldPlaylist = this.playlistSnapshot;
            long currentTimeMs = SystemClock.elapsedRealtime();
            this.lastSnapshotLoadMs = currentTimeMs;
            HlsMediaPlaylist access$1000 = DefaultHlsPlaylistTracker.this.getLatestPlaylistSnapshot(oldPlaylist, hlsMediaPlaylist);
            this.playlistSnapshot = access$1000;
            if (access$1000 != oldPlaylist) {
                this.playlistError = null;
                this.lastSnapshotChangeMs = currentTimeMs;
                DefaultHlsPlaylistTracker.this.onPlaylistUpdated(this.playlistUrl, access$1000);
            } else if (!access$1000.hasEndTag) {
                if (hlsMediaPlaylist.mediaSequence + ((long) hlsMediaPlaylist.segments.size()) < this.playlistSnapshot.mediaSequence) {
                    this.playlistError = new HlsPlaylistTracker.PlaylistResetException(this.playlistUrl.url);
                    boolean unused = DefaultHlsPlaylistTracker.this.notifyPlaylistError(this.playlistUrl, C.TIME_UNSET);
                } else if (((double) (currentTimeMs - this.lastSnapshotChangeMs)) > ((double) C.usToMs(this.playlistSnapshot.targetDurationUs)) * DefaultHlsPlaylistTracker.PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT) {
                    this.playlistError = new HlsPlaylistTracker.PlaylistStuckException(this.playlistUrl.url);
                    long blacklistDurationMs = DefaultHlsPlaylistTracker.this.loadErrorHandlingPolicy.getBlacklistDurationMsFor(4, loadDurationMs, this.playlistError, 1);
                    boolean unused2 = DefaultHlsPlaylistTracker.this.notifyPlaylistError(this.playlistUrl, blacklistDurationMs);
                    if (blacklistDurationMs != C.TIME_UNSET) {
                        blacklistPlaylist(blacklistDurationMs);
                    }
                }
            }
            HlsMediaPlaylist hlsMediaPlaylist2 = this.playlistSnapshot;
            this.earliestNextLoadTimeMs = C.usToMs(hlsMediaPlaylist2 != oldPlaylist ? hlsMediaPlaylist2.targetDurationUs : hlsMediaPlaylist2.targetDurationUs / 2) + currentTimeMs;
            if (this.playlistUrl == DefaultHlsPlaylistTracker.this.primaryHlsUrl && !this.playlistSnapshot.hasEndTag) {
                loadPlaylist();
            }
        }

        private boolean blacklistPlaylist(long blacklistDurationMs) {
            this.blacklistUntilMs = SystemClock.elapsedRealtime() + blacklistDurationMs;
            return DefaultHlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl && !DefaultHlsPlaylistTracker.this.maybeSelectNewPrimaryUrl();
        }
    }

    private static HlsPlaylistParserFactory createFixedFactory(final ParsingLoadable.Parser<HlsPlaylist> playlistParser) {
        return new HlsPlaylistParserFactory() {
            public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser() {
                return playlistParser;
            }

            public ParsingLoadable.Parser<HlsPlaylist> createPlaylistParser(HlsMasterPlaylist masterPlaylist) {
                return playlistParser;
            }
        };
    }
}
