package com.google.android.exoplayer2.upstream.cache;

import com.google.android.exoplayer2.C;
import java.io.File;

public class CacheSpan implements Comparable<CacheSpan> {
    public final File file;
    public final boolean isCached;
    public final String key;
    public final long lastAccessTimestamp;
    public final long length;
    public final long position;

    public CacheSpan(String key2, long position2, long length2) {
        this(key2, position2, length2, C.TIME_UNSET, (File) null);
    }

    public CacheSpan(String key2, long position2, long length2, long lastAccessTimestamp2, File file2) {
        this.key = key2;
        this.position = position2;
        this.length = length2;
        this.isCached = file2 != null;
        this.file = file2;
        this.lastAccessTimestamp = lastAccessTimestamp2;
    }

    public boolean isOpenEnded() {
        return this.length == -1;
    }

    public boolean isHoleSpan() {
        return !this.isCached;
    }

    public int compareTo(CacheSpan another) {
        if (!this.key.equals(another.key)) {
            return this.key.compareTo(another.key);
        }
        long startOffsetDiff = this.position - another.position;
        if (startOffsetDiff == 0) {
            return 0;
        }
        return startOffsetDiff < 0 ? -1 : 1;
    }
}
