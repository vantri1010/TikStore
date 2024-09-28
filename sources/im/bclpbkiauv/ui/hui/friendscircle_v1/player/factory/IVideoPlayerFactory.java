package im.bclpbkiauv.ui.hui.friendscircle_v1.player.factory;

import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.AbsBaseVideoPlayer;

public interface IVideoPlayerFactory {
    AbsBaseVideoPlayer create();

    void logEnable(boolean z);
}
