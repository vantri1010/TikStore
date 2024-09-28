package com.bumptech.glide.signature;

import android.content.Context;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public final class AndroidResourceSignature implements Key {
    private final Key applicationVersion;
    private final int nightMode;

    public static Key obtain(Context context) {
        return new AndroidResourceSignature(context.getResources().getConfiguration().uiMode & 48, ApplicationVersionSignature.obtain(context));
    }

    private AndroidResourceSignature(int nightMode2, Key applicationVersion2) {
        this.nightMode = nightMode2;
        this.applicationVersion = applicationVersion2;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AndroidResourceSignature)) {
            return false;
        }
        AndroidResourceSignature that = (AndroidResourceSignature) o;
        if (this.nightMode != that.nightMode || !this.applicationVersion.equals(that.applicationVersion)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Util.hashCode((Object) this.applicationVersion, this.nightMode);
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
        this.applicationVersion.updateDiskCacheKey(messageDigest);
        messageDigest.update(ByteBuffer.allocate(4).putInt(this.nightMode).array());
    }
}
