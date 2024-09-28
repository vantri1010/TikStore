package im.bclpbkiauv.ui.hui.friendscircle_v1.player.factory;

import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.AbsBaseVideoPlayer;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.MediaVideoPlayer;

public class MediaPlayerFactory implements IVideoPlayerFactory {
    private boolean mEnableLog;

    public AbsBaseVideoPlayer create() {
        return new MediaVideoPlayer(this.mEnableLog);
    }

    public void logEnable(boolean enableLog) {
        this.mEnableLog = enableLog;
    }
}
