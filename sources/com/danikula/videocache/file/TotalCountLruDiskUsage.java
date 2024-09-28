package com.danikula.videocache.file;

import java.io.File;

public class TotalCountLruDiskUsage extends LruDiskUsage {
    private final int maxCount;

    public TotalCountLruDiskUsage(int maxCount2) {
        if (maxCount2 > 0) {
            this.maxCount = maxCount2;
            return;
        }
        throw new IllegalArgumentException("Max count must be positive number!");
    }

    /* access modifiers changed from: protected */
    public boolean accept(File file, long totalSize, int totalCount) {
        return totalCount <= this.maxCount;
    }
}
