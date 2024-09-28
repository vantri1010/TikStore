package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DownloadState {
    public static final int FAILURE_REASON_NONE = 0;
    public static final int FAILURE_REASON_UNKNOWN = 1;
    public static final int STATE_COMPLETED = 3;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_FAILED = 4;
    public static final int STATE_QUEUED = 0;
    public static final int STATE_REMOVED = 6;
    public static final int STATE_REMOVING = 5;
    public static final int STATE_RESTARTING = 7;
    public static final int STATE_STOPPED = 1;
    public static final int STOP_FLAG_DOWNLOAD_MANAGER_NOT_READY = 1;
    public static final int STOP_FLAG_STOPPED = 2;
    public final String cacheKey;
    public final byte[] customMetadata;
    public final float downloadPercentage;
    public final long downloadedBytes;
    public final int failureReason;
    public final String id;
    public final long startTimeMs;
    public final int state;
    public final int stopFlags;
    public final StreamKey[] streamKeys;
    public final long totalBytes;
    public final String type;
    public final long updateTimeMs;
    public final Uri uri;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface FailureReason {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface StopFlags {
    }

    public static String getStateString(int state2) {
        switch (state2) {
            case 0:
                return "QUEUED";
            case 1:
                return "STOPPED";
            case 2:
                return "DOWNLOADING";
            case 3:
                return "COMPLETED";
            case 4:
                return "FAILED";
            case 5:
                return "REMOVING";
            case 6:
                return "REMOVED";
            case 7:
                return "RESTARTING";
            default:
                throw new IllegalStateException();
        }
    }

    public static String getFailureString(int failureReason2) {
        if (failureReason2 == 0) {
            return "NO_REASON";
        }
        if (failureReason2 == 1) {
            return "UNKNOWN_REASON";
        }
        throw new IllegalStateException();
    }

    DownloadState(String id2, String type2, Uri uri2, String cacheKey2, int state2, float downloadPercentage2, long downloadedBytes2, long totalBytes2, int failureReason2, int stopFlags2, long startTimeMs2, long updateTimeMs2, StreamKey[] streamKeys2, byte[] customMetadata2) {
        int i = state2;
        int i2 = failureReason2;
        this.stopFlags = stopFlags2;
        boolean z = true;
        if (i2 != 0 ? i != 4 : i == 4) {
            z = false;
        }
        Assertions.checkState(z);
        this.id = id2;
        this.type = type2;
        this.uri = uri2;
        this.cacheKey = cacheKey2;
        this.streamKeys = streamKeys2;
        this.customMetadata = customMetadata2;
        this.state = i;
        this.downloadPercentage = downloadPercentage2;
        this.downloadedBytes = downloadedBytes2;
        this.totalBytes = totalBytes2;
        this.failureReason = i2;
        this.startTimeMs = startTimeMs2;
        this.updateTimeMs = updateTimeMs2;
    }
}
