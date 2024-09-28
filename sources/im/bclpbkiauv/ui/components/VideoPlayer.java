package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.secretmedia.ExtendedDefaultDataSourceFactory;

public class VideoPlayer implements Player.EventListener, SimpleExoPlayer.VideoListener, NotificationCenter.NotificationCenterDelegate {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;
    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private SimpleExoPlayer audioPlayer;
    /* access modifiers changed from: private */
    public boolean audioPlayerReady;
    private boolean autoplay;
    private Uri currentUri;
    private VideoPlayerDelegate delegate;
    private boolean isStreaming;
    private boolean lastReportedPlayWhenReady;
    private int lastReportedPlaybackState = 1;
    private Handler mainHandler = new Handler();
    private DataSource.Factory mediaDataSourceFactory;
    private boolean mixedAudio;
    private boolean mixedPlayWhenReady;
    private SimpleExoPlayer player;
    private TextureView textureView;
    private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
    private boolean videoPlayerReady;

    public interface RendererBuilder {
        void buildRenderers(VideoPlayer videoPlayer);

        void cancel();
    }

    public interface VideoPlayerDelegate {
        void onError(Exception exc);

        void onRenderedFirstFrame();

        void onStateChanged(boolean z, int i);

        boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture);

        void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture);

        void onVideoSizeChanged(int i, int i2, int i3, float f);
    }

    public VideoPlayer() {
        Context context = ApplicationLoader.applicationContext;
        DefaultBandwidthMeter defaultBandwidthMeter = BANDWIDTH_METER;
        this.mediaDataSourceFactory = new ExtendedDefaultDataSourceFactory(context, (TransferListener) defaultBandwidthMeter, (DataSource.Factory) new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", defaultBandwidthMeter));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.playerDidStartPlaying && ((VideoPlayer) args[0]) != this && isPlaying()) {
            pause();
        }
    }

    private void ensurePleyaerCreated() {
        DefaultLoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true);
        if (this.player == null) {
            SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.trackSelector, (LoadControl) loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.player = newSimpleInstance;
            newSimpleInstance.addListener(this);
            this.player.setVideoListener(this);
            this.player.setVideoTextureView(this.textureView);
            this.player.setPlayWhenReady(this.autoplay);
        }
        if (this.mixedAudio && this.audioPlayer == null) {
            SimpleExoPlayer newSimpleInstance2 = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.trackSelector, (LoadControl) loadControl, (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
            this.audioPlayer = newSimpleInstance2;
            newSimpleInstance2.addListener(new Player.EventListener() {
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                public void onLoadingChanged(boolean isLoading) {
                }

                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                public void onPositionDiscontinuity(int reason) {
                }

                public void onSeekProcessed() {
                }

                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (!VideoPlayer.this.audioPlayerReady && playbackState == 3) {
                        boolean unused = VideoPlayer.this.audioPlayerReady = true;
                        VideoPlayer.this.checkPlayersReady();
                    }
                }

                public void onRepeatModeChanged(int repeatMode) {
                }

                public void onPlayerError(ExoPlaybackException error) {
                }

                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }
            });
            this.audioPlayer.setPlayWhenReady(this.autoplay);
        }
    }

    /* JADX WARNING: type inference failed for: r6v1, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r8v12, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r6v5 */
    /* JADX WARNING: type inference failed for: r8v13, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r8v14, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayerLoop(android.net.Uri r16, java.lang.String r17, android.net.Uri r18, java.lang.String r19) {
        /*
            r15 = this;
            r0 = r15
            r1 = 1
            r0.mixedAudio = r1
            r2 = 0
            r0.audioPlayerReady = r2
            r0.videoPlayerReady = r2
            r15.ensurePleyaerCreated()
            r3 = 0
            r4 = 0
            r5 = 0
        L_0x000f:
            r6 = 2
            if (r5 >= r6) goto L_0x00a6
            if (r5 != 0) goto L_0x001a
            r7 = r17
            r8 = r16
            r14 = r8
            goto L_0x001f
        L_0x001a:
            r7 = r19
            r8 = r18
            r14 = r8
        L_0x001f:
            r8 = -1
            int r9 = r7.hashCode()
            r10 = 3680(0xe60, float:5.157E-42)
            if (r9 == r10) goto L_0x0047
            r10 = 103407(0x193ef, float:1.44904E-40)
            if (r9 == r10) goto L_0x003d
            r10 = 3075986(0x2eef92, float:4.310374E-39)
            if (r9 == r10) goto L_0x0033
        L_0x0032:
            goto L_0x0050
        L_0x0033:
            java.lang.String r9 = "dash"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0032
            r8 = 0
            goto L_0x0050
        L_0x003d:
            java.lang.String r9 = "hls"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0032
            r8 = 1
            goto L_0x0050
        L_0x0047:
            java.lang.String r9 = "ss"
            boolean r9 = r7.equals(r9)
            if (r9 == 0) goto L_0x0032
            r8 = 2
        L_0x0050:
            if (r8 == 0) goto L_0x0085
            if (r8 == r1) goto L_0x007a
            if (r8 == r6) goto L_0x0068
            com.google.android.exoplayer2.source.ExtractorMediaSource r6 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r11 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r11.<init>()
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r9 = r14
            r8.<init>(r9, r10, r11, r12, r13)
            goto L_0x0097
        L_0x0068:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r6 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r11 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r11.<init>(r10)
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r9 = r14
            r8.<init>((android.net.Uri) r9, (com.google.android.exoplayer2.upstream.DataSource.Factory) r10, (com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) r11, (android.os.Handler) r12, (com.google.android.exoplayer2.source.MediaSourceEventListener) r13)
            goto L_0x0097
        L_0x007a:
            com.google.android.exoplayer2.source.hls.HlsMediaSource r6 = new com.google.android.exoplayer2.source.hls.HlsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r8 = r0.mediaDataSourceFactory
            android.os.Handler r9 = r0.mainHandler
            r10 = 0
            r6.<init>(r14, r8, r9, r10)
            goto L_0x0097
        L_0x0085:
            com.google.android.exoplayer2.source.dash.DashMediaSource r6 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r10 = r0.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r11 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r11.<init>(r10)
            android.os.Handler r12 = r0.mainHandler
            r13 = 0
            r8 = r6
            r9 = r14
            r8.<init>((android.net.Uri) r9, (com.google.android.exoplayer2.upstream.DataSource.Factory) r10, (com.google.android.exoplayer2.source.dash.DashChunkSource.Factory) r11, (android.os.Handler) r12, (com.google.android.exoplayer2.source.MediaSourceEventListener) r13)
        L_0x0097:
            com.google.android.exoplayer2.source.LoopingMediaSource r8 = new com.google.android.exoplayer2.source.LoopingMediaSource
            r8.<init>(r6)
            r6 = r8
            if (r5 != 0) goto L_0x00a1
            r3 = r6
            goto L_0x00a2
        L_0x00a1:
            r4 = r6
        L_0x00a2:
            int r5 = r5 + 1
            goto L_0x000f
        L_0x00a6:
            com.google.android.exoplayer2.SimpleExoPlayer r2 = r0.player
            r2.prepare(r3, r1, r1)
            com.google.android.exoplayer2.SimpleExoPlayer r2 = r0.audioPlayer
            r2.prepare(r4, r1, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.VideoPlayer.preparePlayerLoop(android.net.Uri, java.lang.String, android.net.Uri, java.lang.String):void");
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [com.google.android.exoplayer2.source.MediaSource] */
    /* JADX WARNING: type inference failed for: r3v10, types: [com.google.android.exoplayer2.source.dash.DashMediaSource] */
    /* JADX WARNING: type inference failed for: r0v14 */
    /* JADX WARNING: type inference failed for: r3v11, types: [com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource] */
    /* JADX WARNING: type inference failed for: r7v3, types: [com.google.android.exoplayer2.source.ExtractorMediaSource] */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0039, code lost:
        if (r15.equals(com.google.android.exoplayer2.offline.DownloadAction.TYPE_DASH) == false) goto L_0x0050;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0086  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void preparePlayer(android.net.Uri r14, java.lang.String r15) {
        /*
            r13 = this;
            r0 = 0
            r13.videoPlayerReady = r0
            r13.mixedAudio = r0
            r13.currentUri = r14
            java.lang.String r1 = r14.getScheme()
            r2 = 1
            if (r1 == 0) goto L_0x0018
            java.lang.String r3 = "file"
            boolean r3 = r1.startsWith(r3)
            if (r3 != 0) goto L_0x0018
            r3 = 1
            goto L_0x0019
        L_0x0018:
            r3 = 0
        L_0x0019:
            r13.isStreaming = r3
            r13.ensurePleyaerCreated()
            r3 = -1
            int r4 = r15.hashCode()
            r5 = 3680(0xe60, float:5.157E-42)
            r6 = 2
            if (r4 == r5) goto L_0x0046
            r5 = 103407(0x193ef, float:1.44904E-40)
            if (r4 == r5) goto L_0x003c
            r5 = 3075986(0x2eef92, float:4.310374E-39)
            if (r4 == r5) goto L_0x0033
        L_0x0032:
            goto L_0x0050
        L_0x0033:
            java.lang.String r4 = "dash"
            boolean r4 = r15.equals(r4)
            if (r4 == 0) goto L_0x0032
            goto L_0x0051
        L_0x003c:
            java.lang.String r0 = "hls"
            boolean r0 = r15.equals(r0)
            if (r0 == 0) goto L_0x0032
            r0 = 1
            goto L_0x0051
        L_0x0046:
            java.lang.String r0 = "ss"
            boolean r0 = r15.equals(r0)
            if (r0 == 0) goto L_0x0032
            r0 = 2
            goto L_0x0051
        L_0x0050:
            r0 = -1
        L_0x0051:
            if (r0 == 0) goto L_0x0086
            if (r0 == r2) goto L_0x007b
            if (r0 == r6) goto L_0x0069
            com.google.android.exoplayer2.source.ExtractorMediaSource r0 = new com.google.android.exoplayer2.source.ExtractorMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r9 = r13.mediaDataSourceFactory
            com.google.android.exoplayer2.extractor.DefaultExtractorsFactory r10 = new com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
            r10.<init>()
            android.os.Handler r11 = r13.mainHandler
            r12 = 0
            r7 = r0
            r8 = r14
            r7.<init>(r8, r9, r10, r11, r12)
            goto L_0x0098
        L_0x0069:
            com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource r0 = new com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r13.mediaDataSourceFactory
            com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory r6 = new com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource$Factory
            r6.<init>(r5)
            android.os.Handler r7 = r13.mainHandler
            r8 = 0
            r3 = r0
            r4 = r14
            r3.<init>((android.net.Uri) r4, (com.google.android.exoplayer2.upstream.DataSource.Factory) r5, (com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource.Factory) r6, (android.os.Handler) r7, (com.google.android.exoplayer2.source.MediaSourceEventListener) r8)
            goto L_0x0098
        L_0x007b:
            com.google.android.exoplayer2.source.hls.HlsMediaSource r0 = new com.google.android.exoplayer2.source.hls.HlsMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r3 = r13.mediaDataSourceFactory
            android.os.Handler r4 = r13.mainHandler
            r5 = 0
            r0.<init>(r14, r3, r4, r5)
            goto L_0x0098
        L_0x0086:
            com.google.android.exoplayer2.source.dash.DashMediaSource r0 = new com.google.android.exoplayer2.source.dash.DashMediaSource
            com.google.android.exoplayer2.upstream.DataSource$Factory r5 = r13.mediaDataSourceFactory
            com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory r6 = new com.google.android.exoplayer2.source.dash.DefaultDashChunkSource$Factory
            r6.<init>(r5)
            android.os.Handler r7 = r13.mainHandler
            r8 = 0
            r3 = r0
            r4 = r14
            r3.<init>((android.net.Uri) r4, (com.google.android.exoplayer2.upstream.DataSource.Factory) r5, (com.google.android.exoplayer2.source.dash.DashChunkSource.Factory) r6, (android.os.Handler) r7, (com.google.android.exoplayer2.source.MediaSourceEventListener) r8)
        L_0x0098:
            com.google.android.exoplayer2.SimpleExoPlayer r3 = r13.player
            r3.prepare(r0, r2, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.VideoPlayer.preparePlayer(android.net.Uri, java.lang.String):void");
    }

    public boolean isPlayerPrepared() {
        return this.player != null;
    }

    public void releasePlayer(boolean async) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release(async);
            this.player = null;
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.release(async);
            this.audioPlayer = null;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.playerDidStartPlaying);
    }

    public void setTextureView(TextureView texture) {
        if (this.textureView != texture) {
            this.textureView = texture;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setVideoTextureView(texture);
            }
        }
    }

    public boolean getPlayWhenReady() {
        return this.player.getPlayWhenReady();
    }

    public int getPlaybackState() {
        return this.player.getPlaybackState();
    }

    public Uri getCurrentUri() {
        return this.currentUri;
    }

    public void play() {
        this.mixedPlayWhenReady = true;
        if (!this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(true);
                return;
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(false);
        }
    }

    public void pause() {
        this.mixedPlayWhenReady = false;
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setPlayWhenReady(false);
        }
    }

    public void setPlaybackSpeed(float speed) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            float f = 1.0f;
            if (speed > 1.0f) {
                f = 0.98f;
            }
            simpleExoPlayer.setPlaybackParameters(new PlaybackParameters(speed, f));
        }
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.mixedPlayWhenReady = playWhenReady;
        if (!playWhenReady || !this.mixedAudio || (this.audioPlayerReady && this.videoPlayerReady)) {
            this.autoplay = playWhenReady;
            SimpleExoPlayer simpleExoPlayer = this.player;
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(playWhenReady);
            }
            SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
            if (simpleExoPlayer2 != null) {
                simpleExoPlayer2.setPlayWhenReady(playWhenReady);
                return;
            }
            return;
        }
        SimpleExoPlayer simpleExoPlayer3 = this.player;
        if (simpleExoPlayer3 != null) {
            simpleExoPlayer3.setPlayWhenReady(false);
        }
        SimpleExoPlayer simpleExoPlayer4 = this.audioPlayer;
        if (simpleExoPlayer4 != null) {
            simpleExoPlayer4.setPlayWhenReady(false);
        }
    }

    public long getDuration() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getDuration();
        }
        return 0;
    }

    public long getCurrentPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isMuted() {
        return this.player.getVolume() == 0.0f;
    }

    public void setMute(boolean value) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        float f = 0.0f;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(value ? 0.0f : 1.0f);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            if (!value) {
                f = 1.0f;
            }
            simpleExoPlayer2.setVolume(f);
        }
    }

    public void onRepeatModeChanged(int repeatMode) {
    }

    public void onSurfaceSizeChanged(int width, int height) {
    }

    public void setVolume(float volume) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setVolume(volume);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setVolume(volume);
        }
    }

    public void seekTo(long positionMs) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.seekTo(positionMs);
        }
    }

    public void setDelegate(VideoPlayerDelegate videoPlayerDelegate) {
        this.delegate = videoPlayerDelegate;
    }

    public int getBufferedPercentage() {
        if (!this.isStreaming) {
            return 100;
        }
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return simpleExoPlayer.getBufferedPercentage();
        }
        return 0;
    }

    public long getBufferedPosition() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            return this.isStreaming ? simpleExoPlayer.getBufferedPosition() : simpleExoPlayer.getDuration();
        }
        return 0;
    }

    public boolean isStreaming() {
        return this.isStreaming;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0008, code lost:
        r0 = r1.player;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isPlaying() {
        /*
            r1 = this;
            boolean r0 = r1.mixedAudio
            if (r0 == 0) goto L_0x0008
            boolean r0 = r1.mixedPlayWhenReady
            if (r0 != 0) goto L_0x0012
        L_0x0008:
            com.google.android.exoplayer2.SimpleExoPlayer r0 = r1.player
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.getPlayWhenReady()
            if (r0 == 0) goto L_0x0014
        L_0x0012:
            r0 = 1
            goto L_0x0015
        L_0x0014:
            r0 = 0
        L_0x0015:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.VideoPlayer.isPlaying():boolean");
    }

    public boolean isBuffering() {
        return this.player != null && this.lastReportedPlaybackState == 2;
    }

    public void setStreamType(int type) {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setAudioStreamType(type);
        }
        SimpleExoPlayer simpleExoPlayer2 = this.audioPlayer;
        if (simpleExoPlayer2 != null) {
            simpleExoPlayer2.setAudioStreamType(type);
        }
    }

    /* access modifiers changed from: private */
    public void checkPlayersReady() {
        if (this.audioPlayerReady && this.videoPlayerReady && this.mixedPlayWhenReady) {
            play();
        }
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        maybeReportPlayerState();
        if (playWhenReady && playbackState == 3) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.playerDidStartPlaying, this);
        }
        if (!this.videoPlayerReady && playbackState == 3) {
            this.videoPlayerReady = true;
            checkPlayersReady();
        }
    }

    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    public void onPositionDiscontinuity(int reason) {
    }

    public void onSeekProcessed() {
    }

    public void onPlayerError(ExoPlaybackException error) {
        this.delegate.onError(error);
    }

    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.delegate.onVideoSizeChanged(width, height, unappliedRotationDegrees, pixelWidthHeightRatio);
    }

    public void onRenderedFirstFrame() {
        this.delegate.onRenderedFirstFrame();
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return this.delegate.onSurfaceDestroyed(surfaceTexture);
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        this.delegate.onSurfaceTextureUpdated(surfaceTexture);
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    private void maybeReportPlayerState() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            boolean playWhenReady = simpleExoPlayer.getPlayWhenReady();
            int playbackState = this.player.getPlaybackState();
            if (this.lastReportedPlayWhenReady != playWhenReady || this.lastReportedPlaybackState != playbackState) {
                this.delegate.onStateChanged(playWhenReady, playbackState);
                this.lastReportedPlayWhenReady = playWhenReady;
                this.lastReportedPlaybackState = playbackState;
            }
        }
    }
}
