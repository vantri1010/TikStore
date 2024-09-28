package im.bclpbkiauv.ui.hui.friendscircle_v1.player.view;

public interface IVideoPlayerView {
    void changeScreenMode(int i);

    void destroy();

    int getBufferPercentage();

    int getBufferingPosition();

    int getScreenBrightness();

    int getScreenMode();

    void setAutoPlay(boolean z);

    void setControlBarCanShow(boolean z);

    void setCoverData(Object obj);

    void setScreenBrightness(int i);

    void setTitleBarCanShow(boolean z);
}
