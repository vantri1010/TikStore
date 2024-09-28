package im.bclpbkiauv.ui.hui.friendscircle_v1.player.player;

import android.view.TextureView;

public interface IVideoPlayer {

    public interface PlayCallback {
        void onComplete();

        void onDurationChanged(int i);

        void onError(String str);

        void onPlayStateChanged(int i);
    }

    int getCurrentPosition();

    int getDuration();

    int getPlayerState();

    int getVolume();

    boolean isPlaying();

    void pause();

    void play();

    void release();

    void reset();

    void resume();

    void seekTo(int i);

    void setPlayCallback(PlayCallback playCallback);

    void setPlayerState(int i);

    void setTextureView(TextureView textureView);

    void setVolume(int i);

    void start(String str);

    void stop();
}
