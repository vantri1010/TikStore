package im.bclpbkiauv.ui.hui.friendscircle_v1.adapter;

import android.view.View;
import android.widget.AdapterView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.adapter.SmartViewHolder;
import im.bclpbkiauv.ui.hui.friendscircle_v1.fcInterface.AutoPlayItemInterface;
import im.bclpbkiauv.ui.hui.friendscircle_v1.player.view.FcVideoPlayerView;

public class FcVideoViewHold extends SmartViewHolder implements AutoPlayItemInterface {
    private View itemView;
    private final FcVideoPlayerView rlFcDetailVideo;

    public FcVideoViewHold(View itemView2, AdapterView.OnItemClickListener mListener) {
        super(itemView2, mListener);
        this.itemView = itemView2;
        this.rlFcDetailVideo = (FcVideoPlayerView) itemView2.findViewById(R.id.view_video);
    }

    public void setActive() {
        FcVideoPlayerView fcVideoPlayerView = this.rlFcDetailVideo;
        if (fcVideoPlayerView == null) {
            return;
        }
        if (!fcVideoPlayerView.isViewPlaying()) {
            this.rlFcDetailVideo.newStartplay(this.itemView);
        } else if (!this.rlFcDetailVideo.getVideoPlayerMgr().isPlaying()) {
            this.rlFcDetailVideo.getVideoPlayerMgr().play();
        }
    }

    public void deactivate() {
        FcVideoPlayerView fcVideoPlayerView = this.rlFcDetailVideo;
        if (fcVideoPlayerView != null && fcVideoPlayerView.isViewPlaying()) {
            this.rlFcDetailVideo.getVideoPlayerMgr().stop();
        }
    }

    public View getAutoPlayView() {
        return this.rlFcDetailVideo;
    }
}
