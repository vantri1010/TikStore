package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public final class GranularRoundedCorners extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private final float bottomLeft;
    private final float bottomRight;
    private final float topLeft;
    private final float topRight;

    public GranularRoundedCorners(float topLeft2, float topRight2, float bottomRight2, float bottomLeft2) {
        this.topLeft = topLeft2;
        this.topRight = topRight2;
        this.bottomRight = bottomRight2;
        this.bottomLeft = bottomLeft2;
    }

    /* access modifiers changed from: protected */
    public Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.roundedCorners(pool, toTransform, this.topLeft, this.topRight, this.bottomRight, this.bottomLeft);
    }

    public boolean equals(Object o) {
        if (!(o instanceof GranularRoundedCorners)) {
            return false;
        }
        GranularRoundedCorners other = (GranularRoundedCorners) o;
        if (this.topLeft == other.topLeft && this.topRight == other.topRight && this.bottomRight == other.bottomRight && this.bottomLeft == other.bottomLeft) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Util.hashCode(this.bottomLeft, Util.hashCode(this.bottomRight, Util.hashCode(this.topRight, Util.hashCode(ID.hashCode(), Util.hashCode(this.topLeft)))));
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(ByteBuffer.allocate(16).putFloat(this.topLeft).putFloat(this.topRight).putFloat(this.bottomRight).putFloat(this.bottomLeft).array());
    }
}
