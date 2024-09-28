package im.bclpbkiauv.ui.hui.friendscircle_v1.player.message;

public class Message {
    private int mHash;
    private String mVideoUrl;

    public Message(int hash, String videoUrl) {
        this.mHash = hash;
        this.mVideoUrl = videoUrl;
    }

    public int getHash() {
        return this.mHash;
    }

    public String getVideoUrl() {
        return this.mVideoUrl;
    }
}
