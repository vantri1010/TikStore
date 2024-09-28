package im.bclpbkiauv.ui.hui.friendscircle_v1.player.factory;

import android.content.Context;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.AbsBaseVideoPlayer;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.player.ExoVideoPlayer;

public class ExoPlayerFactory implements IVideoPlayerFactory {
    private Context mContext;
    private boolean mEnableLog;

    public ExoPlayerFactory(Context context) {
        this.mContext = context;
    }

    public AbsBaseVideoPlayer create() {
        return new ExoVideoPlayer(this.mContext, this.mEnableLog);
    }

    public void logEnable(boolean enableLog) {
        this.mEnableLog = enableLog;
    }
}
