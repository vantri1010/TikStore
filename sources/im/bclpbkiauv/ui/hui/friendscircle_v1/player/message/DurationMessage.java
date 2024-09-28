package im.bclpbkiauv.ui.hui.friendscircle_v1.player.message;

public class DurationMessage extends Message {
    private int mDuration;

    public DurationMessage(int hash, String videoUrl, int duration) {
        super(hash, videoUrl);
        this.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }
}
