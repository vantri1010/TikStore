package im.bclpbkiauv.ui.hui.friendscircle_v1.player.message;

public class UIStateMessage extends Message {
    private int mState;

    public UIStateMessage(int hash, String videoUrl, int state) {
        super(hash, videoUrl);
        this.mState = state;
    }

    public int getState() {
        return this.mState;
    }
}
