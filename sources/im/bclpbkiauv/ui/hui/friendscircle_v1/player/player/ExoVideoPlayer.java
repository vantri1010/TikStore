package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.TextureView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.text.TextRenderer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.logger.ExoPlayerLogger;
import java.util.List;

public class ExoVideoPlayer extends AbsBaseVideoPlayer implements SimpleExoPlayer.VideoListener, TextRenderer.Output, Player.EventListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "VideoExoPlayer";
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private ExoPlayerLogger mExoPlayerLogger;
    private Handler mMainHandler;
    private DataSource.Factory mMediaDataSourceFactory;
    private MappingTrackSelector mTrackSelector;

    public /* synthetic */ void onPositionDiscontinuity(int i) {
        Player.EventListener.CC.$default$onPositionDiscontinuity(this, i);
    }

    public /* synthetic */ void onRepeatModeChanged(int i) {
        Player.EventListener.CC.$default$onRepeatModeChanged(this, i);
    }

    public /* synthetic */ void onSeekProcessed() {
        Player.EventListener.CC.$default$onSeekProcessed(this);
    }

    public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
        Player.EventListener.CC.$default$onShuffleModeEnabledChanged(this, z);
    }

    public /* synthetic */ void onSurfaceSizeChanged(int i, int i2) {
        VideoListener.CC.$default$onSurfaceSizeChanged(this, i, i2);
    }

    public /* synthetic */ void onTimelineChanged(Timeline timeline, Object obj, int i) {
        Player.EventListener.CC.$default$onTimelineChanged(this, timeline, obj, i);
    }

    public ExoVideoPlayer(Context context) {
        this.mContext = context.getApplicationContext();
        initExoPlayer();
    }

    public ExoVideoPlayer(Context context, boolean enableLog) {
        this.mContext = context.getApplicationContext();
        initExoPlayer();
        this.mEnableLog = enableLog;
    }

    public void setTextureView(TextureView textureView) {
        if (textureView == null) {
            this.mExoPlayer.clearVideoTextureView(this.mTextureView);
        }
        super.setTextureView(textureView);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        SurfaceTexture surfaceTexture = this.mSurfaceTexture;
        this.mExoPlayer.setVideoTextureView(this.mTextureView);
        super.onSurfaceTextureAvailable(surface, width, height);
    }

    /* access modifiers changed from: protected */
    public void prepare() {
        MediaSource source;
        this.mExoPlayer.stop();
        this.mExoPlayer.setVideoTextureView(this.mTextureView);
        MediaSource source2 = buildMediaSource(Uri.parse(this.mUrl), (String) null);
        if (!this.blnLoop) {
            source = new LoopingMediaSource(source2, 1);
        } else {
            source = new LoopingMediaSource(source2);
        }
        this.mExoPlayer.prepare(source);
    }

    public void start(String url) {
        this.mUrl = url;
    }

    public void play() {
        if (this.mExoPlayer.getPlaybackState() == 3) {
            this.mExoPlayer.setPlayWhenReady(true);
        }
    }

    public void pause() {
        if (this.mExoPlayer.getPlaybackState() == 3) {
            this.mExoPlayer.setPlayWhenReady(false);
        }
    }

    public void resume() {
        if (this.mExoPlayer.getPlaybackState() == 3) {
            this.mExoPlayer.setPlayWhenReady(true);
        }
    }

    public void stop() {
        pause();
        this.mExoPlayer.stop();
    }

    public void reset() {
    }

    public void release() {
        pause();
        this.mExoPlayer.release(true);
    }

    public void setPlayerState(int state) {
        this.mState = state;
    }

    public int getPlayerState() {
        return this.mState;
    }

    public int getCurrentPosition() {
        return (int) this.mExoPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return (int) this.mExoPlayer.getDuration();
    }

    public void seekTo(int position) {
        this.mExoPlayer.seekTo((long) position);
    }

    public void setVolume(int volume) {
        this.mExoPlayer.setVolume((float) volume);
    }

    public int getVolume() {
        return Integer.valueOf((int) (this.mExoPlayer.getVolume() * 10.0f)).intValue();
    }

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == 4) {
            onCompletion();
        } else if (playbackState != 3) {
        } else {
            if (getPlayerState() == 1) {
                onPrepared();
            } else if (getPlayerState() == 3) {
                onSeekComplete();
            }
        }
    }

    public void onPrepared() {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onDurationChanged((int) this.mExoPlayer.getDuration());
            this.mPlayCallback.onPlayStateChanged(2);
        }
        play();
    }

    public void onCompletion() {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onComplete();
        }
    }

    public void onSeekComplete() {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onPlayStateChanged(2);
        }
        play();
    }

    public void onPlayerError(ExoPlaybackException error) {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onError(error.getCause().toString());
        }
    }

    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    public void onLoadingChanged(boolean isLoading) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    public void onCues(List<Cue> list) {
    }

    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        this.miVideoHeight = height;
        this.miVideoWidth = width;
    }

    public void onRenderedFirstFrame() {
    }

    public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        String str;
        if (!TextUtils.isEmpty(overrideExtension)) {
            str = "." + overrideExtension;
        } else {
            String str2 = overrideExtension;
            str = uri.getLastPathSegment();
        }
        int type = Util.inferContentType(str);
        if (type == 0) {
            Uri uri2 = uri;
            return new DashMediaSource(uri, buildDataSourceFactory(false), (DashChunkSource.Factory) new DefaultDashChunkSource.Factory(this.mMediaDataSourceFactory), this.mMainHandler, (MediaSourceEventListener) this.mExoPlayerLogger);
        } else if (type == 1) {
            Uri uri3 = uri;
            return new SsMediaSource(uri, buildDataSourceFactory(false), (SsChunkSource.Factory) new DefaultSsChunkSource.Factory(this.mMediaDataSourceFactory), this.mMainHandler, (MediaSourceEventListener) this.mExoPlayerLogger);
        } else if (type == 2) {
            Uri uri4 = uri;
            return new HlsMediaSource(uri, this.mMediaDataSourceFactory, this.mMainHandler, this.mExoPlayerLogger);
        } else if (type == 3) {
            return new ExtractorMediaSource(uri, this.mMediaDataSourceFactory, new DefaultExtractorsFactory(), this.mMainHandler, this.mExoPlayerLogger);
        } else {
            throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private void initExoPlayer() {
        this.mMediaDataSourceFactory = buildDataSourceFactory(true);
        DefaultTrackSelector defaultTrackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(BANDWIDTH_METER));
        this.mTrackSelector = defaultTrackSelector;
        this.mExoPlayerLogger = new ExoPlayerLogger(defaultTrackSelector);
        this.mMainHandler = new Handler(Looper.getMainLooper());
        new DefaultRenderersFactory(this.mContext, (DrmSessionManager<FrameworkMediaCrypto>) null, 0);
        SimpleExoPlayer newSimpleInstance = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, (TrackSelector) this.mTrackSelector, (LoadControl) new DefaultLoadControl(new DefaultAllocator(true, 65536), 15000, 50000, 100, 5000, -1, true), (DrmSessionManager<FrameworkMediaCrypto>) null, 2);
        this.mExoPlayer = newSimpleInstance;
        newSimpleInstance.addListener(this.mExoPlayerLogger);
        this.mExoPlayer.setAudioDebugListener(this.mExoPlayerLogger);
        this.mExoPlayer.setVideoDebugListener(this.mExoPlayerLogger);
        this.mExoPlayer.setMetadataOutput(this.mExoPlayerLogger);
        this.mExoPlayer.setTextOutput((TextOutput) null);
        this.mExoPlayer.setVideoListener((SimpleExoPlayer.VideoListener) null);
        this.mExoPlayer.removeListener(this);
        this.mExoPlayer.setVideoTextureView((TextureView) null);
        this.mExoPlayer.setVideoListener(this);
        this.mExoPlayer.addListener(this);
        this.mExoPlayer.setTextOutput(this);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this.mContext, (TransferListener) bandwidthMeter, (DataSource.Factory) buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(this.mContext, TAG), bandwidthMeter);
    }
}
