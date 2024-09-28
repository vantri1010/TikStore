package com.danikula.videocache.file;

import java.io.File;

public class TotalSizeLruDiskUsage extends LruDiskUsage {
    private final long maxSize;

    public TotalSizeLruDiskUsage(long maxSize2) {
        if (maxSize2 > 0) {
            this.maxSize = maxSize2;
            return;
        }
        throw new IllegalArgumentException("Max size must be positive number!");
    }

    /* access modifiers changed from: protected */
    public boolean accept(File file, long totalSize, int totalCount) {
        return totalSize <= this.maxSize;
    }
}
