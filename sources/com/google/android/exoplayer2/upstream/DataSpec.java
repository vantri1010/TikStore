package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.zhy.http.okhttp.OkHttpUtils;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public final class DataSpec {
    public static final int FLAG_ALLOW_CACHE_FRAGMENTATION = 16;
    public static final int FLAG_ALLOW_GZIP = 1;
    public static final int FLAG_ALLOW_ICY_METADATA = 2;
    public static final int FLAG_DONT_CACHE_IF_LENGTH_UNKNOWN = 4;
    public static final int HTTP_METHOD_GET = 1;
    public static final int HTTP_METHOD_HEAD = 3;
    public static final int HTTP_METHOD_POST = 2;
    public final long absoluteStreamPosition;
    public final int flags;
    public final byte[] httpBody;
    public final int httpMethod;
    public final String key;
    public final long length;
    public final long position;
    @Deprecated
    public final byte[] postBody;
    public final Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface HttpMethod {
    }

    public DataSpec(Uri uri2) {
        this(uri2, 0);
    }

    public DataSpec(Uri uri2, int flags2) {
        this(uri2, 0, -1, (String) null, flags2);
    }

    public DataSpec(Uri uri2, long absoluteStreamPosition2, long length2, String key2) {
        this(uri2, absoluteStreamPosition2, absoluteStreamPosition2, length2, key2, 0);
    }

    public DataSpec(Uri uri2, long absoluteStreamPosition2, long length2, String key2, int flags2) {
        this(uri2, absoluteStreamPosition2, absoluteStreamPosition2, length2, key2, flags2);
    }

    public DataSpec(Uri uri2, long absoluteStreamPosition2, long position2, long length2, String key2, int flags2) {
        this(uri2, (byte[]) null, absoluteStreamPosition2, position2, length2, key2, flags2);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public DataSpec(Uri uri2, byte[] postBody2, long absoluteStreamPosition2, long position2, long length2, String key2, int flags2) {
        this(uri2, postBody2 != null ? 2 : 1, postBody2, absoluteStreamPosition2, position2, length2, key2, flags2);
    }

    public DataSpec(Uri uri2, int httpMethod2, byte[] httpBody2, long absoluteStreamPosition2, long position2, long length2, String key2, int flags2) {
        byte[] bArr = httpBody2;
        long j = absoluteStreamPosition2;
        long j2 = position2;
        long j3 = length2;
        boolean z = true;
        Assertions.checkArgument(j >= 0);
        Assertions.checkArgument(j2 >= 0);
        if (j3 <= 0 && j3 != -1) {
            z = false;
        }
        Assertions.checkArgument(z);
        this.uri = uri2;
        this.httpMethod = httpMethod2;
        byte[] bArr2 = (bArr == null || bArr.length == 0) ? null : bArr;
        this.httpBody = bArr2;
        this.postBody = bArr2;
        this.absoluteStreamPosition = j;
        this.position = j2;
        this.length = j3;
        this.key = key2;
        this.flags = flags2;
    }

    public boolean isFlagSet(int flag) {
        return (this.flags & flag) == flag;
    }

    public String toString() {
        return "DataSpec[" + getHttpMethodString() + " " + this.uri + ", " + Arrays.toString(this.httpBody) + ", " + this.absoluteStreamPosition + ", " + this.position + ", " + this.length + ", " + this.key + ", " + this.flags + "]";
    }

    public final String getHttpMethodString() {
        return getStringForHttpMethod(this.httpMethod);
    }

    public static String getStringForHttpMethod(int httpMethod2) {
        if (httpMethod2 == 1) {
            return "GET";
        }
        if (httpMethod2 == 2) {
            return "POST";
        }
        if (httpMethod2 == 3) {
            return OkHttpUtils.METHOD.HEAD;
        }
        throw new AssertionError(httpMethod2);
    }

    public DataSpec subrange(long offset) {
        long j = this.length;
        long j2 = -1;
        if (j != -1) {
            j2 = j - offset;
        }
        return subrange(offset, j2);
    }

    public DataSpec subrange(long offset, long length2) {
        if (offset == 0 && this.length == length2) {
            return this;
        }
        return new DataSpec(this.uri, this.httpMethod, this.httpBody, this.absoluteStreamPosition + offset, this.position + offset, length2, this.key, this.flags);
    }

    public DataSpec withUri(Uri uri2) {
        return new DataSpec(uri2, this.httpMethod, this.httpBody, this.absoluteStreamPosition, this.position, this.length, this.key, this.flags);
    }
}
