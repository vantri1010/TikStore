package im.bclpbkiauv.ui.hui.friendscircle_v1.player.message;

public class BackPressedMessage extends Message {
    private int mScreenState;

    public BackPressedMessage(int screenState, int hash, String videoUrl) {
        super(hash, videoUrl);
        this.mScreenState = screenState;
    }

    public int getScreenState() {
        return this.mScreenState;
    }
}
