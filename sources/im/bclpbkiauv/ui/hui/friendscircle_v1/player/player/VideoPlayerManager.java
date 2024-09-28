package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.config.VideoPlayerConfig;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.BackPressedMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.DurationMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.Message;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.message.UIStateMessage;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.IVideoPlayer;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.state.ScreenViewState;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.utils.Utils;
import java.util.Observable;
import java.util.Observer;

public final class VideoPlayerManager implements IVideoPlayer.PlayCallback {
    private static final String TAG = "PlayerManager";
    private static volatile VideoPlayerManager sVideoPlayerManager;
    private int iPlayMessageId;
    private int mObserverHash = -1;
    private PlayStateObservable mPlayStateObservable;
    private AbsBaseVideoPlayer mPlayer;
    private View mPlayingView = null;
    private int mScreenState = 1;
    private VideoPlayerConfig mVideoPlayerConfig;
    private String mVideoUrl;
    private View vLast = null;
    private View vVideoView = null;

    private VideoPlayerManager(VideoPlayerConfig videoPlayerConfig) {
        this.mVideoPlayerConfig = videoPlayerConfig;
        createPlayer();
        this.mPlayStateObservable = new PlayStateObservable();
    }

    public void setvLast(View vLast2) {
        this.vLast = vLast2;
    }

    public int getPlayMessageId() {
        return this.iPlayMessageId;
    }

    public void setPlayMessageId(int iPlayMessageId2, View view) {
        this.iPlayMessageId = iPlayMessageId2;
        this.vVideoView = view;
    }

    public View getVideoView() {
        return this.vVideoView;
    }

    public View getvLast() {
        return this.vLast;
    }

    public void setVLastVisiable(boolean blnVis) {
        View view = this.vLast;
        if (view != null) {
            view.setTag("stop");
            this.vLast.setVisibility(blnVis ? 0 : 8);
        }
    }

    public static VideoPlayerManager getInstance() {
        Class<VideoPlayerManager> cls = VideoPlayerManager.class;
        if (sVideoPlayerManager == null) {
            synchronized (cls) {
                if (sVideoPlayerManager == null) {
                    loadConfig(new VideoPlayerConfig.Builder().build());
                }
            }
        }
        if (sVideoPlayerManager.mPlayer == null) {
            synchronized (cls) {
                if (sVideoPlayerManager.mPlayer == null) {
                    sVideoPlayerManager.createPlayer();
                }
            }
        }
        return sVideoPlayerManager;
    }

    private void createPlayer() {
        AbsBaseVideoPlayer create = this.mVideoPlayerConfig.getPlayerFactory().create();
        this.mPlayer = create;
        create.setPlayCallback(this);
    }

    public AbsBaseVideoPlayer getPlayer() {
        return this.mPlayer;
    }

    public int getDuration() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer == null) {
            return 0;
        }
        return absBaseVideoPlayer.getDuration();
    }

    public VideoPlayerConfig getConfig() {
        return this.mVideoPlayerConfig;
    }

    public static void loadConfig(VideoPlayerConfig videoPlayerConfig) {
        if (sVideoPlayerManager == null) {
            sVideoPlayerManager = new VideoPlayerManager(videoPlayerConfig);
        }
    }

    public static VideoPlayerManager createMgrWithCfg(VideoPlayerConfig videoPlayerConfig) {
        return new VideoPlayerManager(videoPlayerConfig);
    }

    public void removeTextureView() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null && absBaseVideoPlayer.mTextureView != null && this.mPlayer.mTextureView.getParent() != null) {
            ((ViewGroup) this.mPlayer.mTextureView.getParent()).removeView(this.mPlayer.mTextureView);
            setTextureView((TextureView) null);
            if (this.mPlayer.mTextureView != null) {
                Utils.log("remove TextureView:" + this.mPlayer.mTextureView.toString());
            }
        }
    }

    public void setTextureView(TextureView textureView) {
        if (textureView != null) {
            Utils.log("set TextureView:" + textureView.toString());
        }
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.setTextureView(textureView);
        }
    }

    public TextureView getTextureView() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer == null) {
            return null;
        }
        return absBaseVideoPlayer.mTextureView;
    }

    public float getVideoRatio() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer == null) {
            return 0.0f;
        }
        return ((float) absBaseVideoPlayer.getMiVideoWidth()) / ((float) this.mPlayer.getMiVideoHeight());
    }

    public void setLoopPlay(boolean blnLoop) {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.setLoopPlay(blnLoop);
        }
    }

    public boolean isCached(String videoUrl) {
        if (!this.mVideoPlayerConfig.isCacheEnable() || !this.mVideoPlayerConfig.getCacheProxy().isCached(videoUrl)) {
            return false;
        }
        return true;
    }

    public String getVideoUrl() {
        return this.mVideoUrl;
    }

    public void start(String url, int observerHash) {
        bindPlayerView(url, observerHash);
        onPlayStateChanged(1);
        Utils.log(String.format("start loading video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
        String wrapperUrl = url;
        if (this.mVideoPlayerConfig.isCacheEnable()) {
            wrapperUrl = this.mVideoPlayerConfig.getCacheProxy().getProxyUrl(url);
        }
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.start(wrapperUrl);
        }
    }

    public View getPlayingView() {
        return this.mPlayingView;
    }

    public void setPlayingView(View vLast2) {
        this.mPlayingView = vLast2;
    }

    public void start(String url, int observerHash, View view) {
        bindPlayerView(url, observerHash);
        onPlayStateChanged(1);
        Utils.log(String.format("start loading video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
        String wrapperUrl = url;
        if (this.mVideoPlayerConfig.isCacheEnable()) {
            wrapperUrl = this.mVideoPlayerConfig.getCacheProxy().getProxyUrl(url);
        }
        if (view != null) {
            this.mPlayingView = view;
        }
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.start(wrapperUrl);
        }
    }

    /* access modifiers changed from: package-private */
    public void bindPlayerView(String url, int observerHash) {
        this.mVideoUrl = url;
        this.mObserverHash = observerHash;
    }

    public void play() {
        Utils.log(String.format("play video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.play();
        }
        onPlayStateChanged(2);
    }

    public void resume() {
        if (getState() == 4) {
            Utils.log(String.format("resume video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
            play();
        }
    }

    public void pause() {
        if (getState() == 2) {
            Utils.log(String.format("pause video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
            AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
            if (absBaseVideoPlayer != null) {
                absBaseVideoPlayer.pause();
            }
            onPlayStateChanged(4);
            return;
        }
        Utils.log(String.format("pause video for state: %d, hash=%d, url=%s", new Object[]{Integer.valueOf(getState()), Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
    }

    public void stop() {
        Utils.log(String.format("stop video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
        onPlayStateChanged(0);
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.stop();
        }
        removeTextureView();
        this.mObserverHash = -1;
        this.mVideoUrl = null;
        this.mScreenState = 1;
    }

    public void stopWithKeepView() {
        Utils.log(String.format("stop video, hash=%d, url=%s", new Object[]{Integer.valueOf(this.mObserverHash), this.mVideoUrl}));
        onPlayStateChanged(0);
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.stop();
        }
        this.mObserverHash = -1;
        this.mVideoUrl = null;
        this.mScreenState = 1;
    }

    public void release() {
        Utils.log("release player");
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.setPlayerState(0);
        }
        removeTextureView();
        AbsBaseVideoPlayer absBaseVideoPlayer2 = this.mPlayer;
        if (absBaseVideoPlayer2 != null) {
            absBaseVideoPlayer2.release();
            this.mPlayer = null;
        }
        this.mObserverHash = -1;
        this.mVideoUrl = null;
        this.mScreenState = 1;
        this.vLast = null;
    }

    public void setAutoPlay(boolean auto) {
    }

    public void setVolume(int i) {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.setVolume(i);
        }
    }

    public int getVolume() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer == null) {
            return 0;
        }
        return absBaseVideoPlayer.getVolume();
    }

    public void setRepeat(boolean auto) {
    }

    public void setPlaySpeed(float speed) {
    }

    public void setPlayingCache(boolean enable, String saveDir, int maxDuration, long maxSize) {
    }

    public void setNetworkTimeout(int mstimeout) {
    }

    public boolean hasViewPlaying() {
        return this.mObserverHash != -1;
    }

    public int getmObserverHash() {
        return this.mObserverHash;
    }

    public boolean onBackPressed() {
        if (ScreenViewState.isNormal(this.mScreenState)) {
            return false;
        }
        this.mPlayStateObservable.notify(new BackPressedMessage(this.mScreenState, this.mObserverHash, this.mVideoUrl));
        return true;
    }

    public boolean isPlaying() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        return absBaseVideoPlayer != null && absBaseVideoPlayer.isPlaying();
    }

    public boolean isViewPlaying(int viewHash) {
        return this.mObserverHash == viewHash;
    }

    public int getCurrentPosition() {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer == null) {
            return 0;
        }
        return absBaseVideoPlayer.getCurrentPosition();
    }

    public void seekTo(int position) {
        if (isPlaying()) {
            onPlayStateChanged(3);
        }
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.seekTo(position);
        }
    }

    public int getState() {
        if (sVideoPlayerManager == null || sVideoPlayerManager.mPlayer == null) {
            return 0;
        }
        return sVideoPlayerManager.mPlayer.getPlayerState();
    }

    public void setState(int state) {
        if (sVideoPlayerManager != null && sVideoPlayerManager.mPlayer != null) {
            sVideoPlayerManager.mPlayer.setPlayerState(state);
        }
    }

    public void onError(String error) {
        String str;
        if (("error video, error= " + error) == null) {
            str = "null";
        } else {
            str = error + ", url=" + this.mVideoUrl;
        }
        Utils.log(str);
        if (!TextUtils.isEmpty(error)) {
            Log.d(TAG, error);
        }
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.stop();
        }
        changeUIState(6);
    }

    public void clearmObserverHash() {
        this.mObserverHash = -1;
    }

    public void onComplete() {
        changeUIState(5);
    }

    public void onPlayStateChanged(int state) {
        changeUIState(state);
    }

    public void onDurationChanged(int duration) {
        this.mPlayStateObservable.notify(new DurationMessage(this.mObserverHash, this.mVideoUrl, duration));
    }

    public void addObserver(Observer observer) {
        this.mPlayStateObservable.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        this.mPlayStateObservable.deleteObserver(observer);
    }

    private void changeUIState(int state) {
        AbsBaseVideoPlayer absBaseVideoPlayer = this.mPlayer;
        if (absBaseVideoPlayer != null) {
            absBaseVideoPlayer.setPlayerState(state);
        }
        PlayStateObservable playStateObservable = this.mPlayStateObservable;
        if (playStateObservable != null) {
            playStateObservable.notify(new UIStateMessage(this.mObserverHash, this.mVideoUrl, state));
        }
    }

    public void setScreenState(int screenState) {
        this.mScreenState = screenState;
    }

    public int getmScreenState() {
        return this.mScreenState;
    }

    class PlayStateObservable extends Observable {
        PlayStateObservable() {
        }

        private void setObservableChanged() {
            setChanged();
        }

        public void notify(Message message) {
            setObservableChanged();
            notifyObservers(message);
        }
    }
}
