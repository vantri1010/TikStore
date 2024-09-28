package com.google.android.exoplayer2.offline;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

public final class DownloadIndexUtil {

    public interface DownloadIdProvider {
        String getId(DownloadAction downloadAction);
    }

    private DownloadIndexUtil() {
    }

    public static void upgradeActionFile(ActionFile actionFile, DownloadIndex downloadIndex, DownloadIdProvider downloadIdProvider) throws IOException {
        if (downloadIdProvider == null) {
            downloadIdProvider = $$Lambda$DownloadIndexUtil$Py1lIUrgVBjWZAWCVofL31wM7XI.INSTANCE;
        }
        for (DownloadAction action : actionFile.load()) {
            addAction(downloadIndex, downloadIdProvider.getId(action), action);
        }
    }

    public static void addAction(DownloadIndex downloadIndex, String id, DownloadAction action) {
        DownloadState downloadState;
        DownloadState downloadState2 = downloadIndex.getDownloadState(id != null ? id : action.id);
        if (downloadState2 != null) {
            downloadState = merge(downloadState2, action);
        } else {
            downloadState = convert(action);
        }
        downloadIndex.putDownloadState(downloadState);
    }

    private static DownloadState merge(DownloadState downloadState, DownloadAction action) {
        int newState;
        DownloadState downloadState2 = downloadState;
        DownloadAction downloadAction = action;
        Assertions.checkArgument(downloadAction.type.equals(downloadState2.type));
        if (downloadAction.isRemoveAction) {
            newState = 5;
        } else if (downloadState2.state == 5 || downloadState2.state == 7) {
            newState = 7;
        } else if (downloadState2.state == 1) {
            newState = 1;
        } else {
            newState = 0;
        }
        HashSet hashSet = new HashSet(downloadAction.keys);
        Collections.addAll(hashSet, downloadState2.streamKeys);
        String str = downloadState2.id;
        String str2 = downloadState2.type;
        Uri uri = downloadAction.uri;
        String str3 = downloadAction.customCacheKey;
        long j = downloadState2.downloadedBytes;
        HashSet hashSet2 = hashSet;
        return new DownloadState(str, str2, uri, str3, newState, -1.0f, j, -1, downloadState2.failureReason, downloadState2.stopFlags, downloadState2.startTimeMs, downloadState2.updateTimeMs, (StreamKey[]) hashSet.toArray(new StreamKey[0]), downloadAction.data);
    }

    private static DownloadState convert(DownloadAction action) {
        DownloadAction downloadAction = action;
        long currentTimeMs = System.currentTimeMillis();
        return new DownloadState(downloadAction.id, downloadAction.type, downloadAction.uri, downloadAction.customCacheKey, downloadAction.isRemoveAction ? 5 : 0, -1.0f, 0, -1, 0, 0, currentTimeMs, currentTimeMs, (StreamKey[]) downloadAction.keys.toArray(new StreamKey[0]), downloadAction.data);
    }
}
