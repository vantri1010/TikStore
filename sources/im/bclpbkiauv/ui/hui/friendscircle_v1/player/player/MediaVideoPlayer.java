package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import android.view.TextureView;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.IVideoPlayer;

public class MediaVideoPlayer extends AbsBaseVideoPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener {
    private static final int MSG_PREPARE = 1;
    private static final int MSG_RELEASE = 2;
    private static final String TAG = "VideoMediaPlayer";
    private MediaHandler mMediaHandler;
    private HandlerThread mMediaHandlerThread;
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer;

    public MediaVideoPlayer() {
        this(false);
    }

    public MediaVideoPlayer(boolean enableLog) {
        this.mMediaPlayer = new MediaPlayer();
        HandlerThread handlerThread = new HandlerThread(TAG);
        this.mMediaHandlerThread = handlerThread;
        handlerThread.start();
        this.mMediaHandler = new MediaHandler(this.mMediaHandlerThread.getLooper());
        this.mEnableLog = enableLog;
    }

    public void setTextureView(TextureView textureView) {
        if (textureView == null && this.mSurfaceTexture != null) {
            this.mSurfaceTexture.release();
        }
        super.setTextureView(textureView);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            this.mMediaPlayer.setSurface(new Surface(surface));
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSurfaceTextureAvailable(surface, width, height);
    }

    class MediaHandler extends Handler {
        public MediaHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 1) {
                try {
                    MediaVideoPlayer.this.mMediaPlayer.release();
                    MediaPlayer unused = MediaVideoPlayer.this.mMediaPlayer = new MediaPlayer();
                    MediaVideoPlayer.this.mMediaPlayer.setAudioStreamType(3);
                    MediaVideoPlayer.this.mMediaPlayer.setOnPreparedListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setOnCompletionListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setOnBufferingUpdateListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setScreenOnWhilePlaying(true);
                    MediaVideoPlayer.this.mMediaPlayer.setOnSeekCompleteListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setOnErrorListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setOnInfoListener(MediaVideoPlayer.this);
                    MediaVideoPlayer.this.mMediaPlayer.setDataSource(MediaVideoPlayer.this.mUrl);
                    MediaVideoPlayer.this.mMediaPlayer.prepareAsync();
                    MediaVideoPlayer.this.mMediaPlayer.setSurface(new Surface(MediaVideoPlayer.this.mSurfaceTexture));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (i == 2) {
                MediaVideoPlayer.this.mMediaPlayer.release();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void prepare() {
        this.mMediaHandler.obtainMessage(1).sendToTarget();
    }

    public void start(String url) {
        this.mUrl = url;
    }

    public void play() {
        this.mMediaPlayer.start();
    }

    public void pause() {
        if (getPlayerState() == 2) {
            this.mMediaPlayer.pause();
        }
    }

    public void resume() {
        this.mMediaPlayer.start();
    }

    public void stop() {
        this.mMediaHandler.obtainMessage(2).sendToTarget();
    }

    public void reset() {
    }

    public void release() {
        this.mMediaHandler.obtainMessage(2).sendToTarget();
    }

    public void setPlayerState(int state) {
        this.mState = state;
    }

    public int getPlayerState() {
        return this.mState;
    }

    public int getCurrentPosition() {
        return this.mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return this.mMediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        this.mMediaPlayer.seekTo(position);
    }

    public void setVolume(int volume) {
        this.mMediaPlayer.setVolume((float) volume, (float) volume);
    }

    public int getVolume() {
        return 0;
    }

    public void onSeekComplete(MediaPlayer mp) {
        if (this.mPlayCallback != null && isPlaying()) {
            this.mPlayCallback.onPlayStateChanged(2);
        }
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }

    public void onCompletion(MediaPlayer mp) {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onComplete();
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (this.mPlayCallback == null) {
            return false;
        }
        IVideoPlayer.PlayCallback playCallback = this.mPlayCallback;
        playCallback.onError("Play error, what=" + what + ", extra=" + extra);
        return false;
    }

    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void onPrepared(MediaPlayer mp) {
        if (this.mPlayCallback != null) {
            this.mPlayCallback.onDurationChanged(mp.getDuration());
            this.mPlayCallback.onPlayStateChanged(2);
        }
        play();
    }
}
