package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.net.Uri;

public class PictureCurrentModel {
    private String filePath;
    private String type;
    private Uri uri;
    private boolean useSource;

    public Uri getUri() {
        return this.uri;
    }

    public PictureCurrentModel setUri(Uri uri2) {
        this.uri = uri2;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public PictureCurrentModel setType(String type2) {
        this.type = type2;
        return this;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public PictureCurrentModel setFilePath(String filePath2) {
        this.filePath = filePath2;
        return this;
    }

    public boolean isUseSource() {
        return this.useSource;
    }

    public PictureCurrentModel setUseSource(boolean useSource2) {
        this.useSource = useSource2;
        return this;
    }
}
