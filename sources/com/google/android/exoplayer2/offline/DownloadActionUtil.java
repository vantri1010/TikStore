package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class DownloadActionUtil {
    private DownloadActionUtil() {
    }

    public static DownloadAction mergeActions(ArrayDeque<DownloadAction> actionQueue) {
        DownloadAction removeAction = null;
        DownloadAction downloadAction = null;
        HashSet<StreamKey> keys = new HashSet<>();
        boolean downloadAllTracks = false;
        DownloadAction firstAction = (DownloadAction) Assertions.checkNotNull(actionQueue.peek());
        while (!actionQueue.isEmpty()) {
            DownloadAction action = actionQueue.remove();
            Assertions.checkState(action.type.equals(firstAction.type));
            Assertions.checkState(action.isSameMedia(firstAction));
            if (action.isRemoveAction) {
                removeAction = action;
                downloadAction = null;
                keys.clear();
                downloadAllTracks = false;
            } else {
                if (!downloadAllTracks) {
                    if (action.keys.isEmpty()) {
                        downloadAllTracks = true;
                        keys.clear();
                    } else {
                        keys.addAll(action.keys);
                    }
                }
                downloadAction = action;
            }
        }
        if (removeAction != null) {
            actionQueue.add(removeAction);
        }
        if (downloadAction != null) {
            actionQueue.add(DownloadAction.createDownloadAction(downloadAction.type, downloadAction.uri, new ArrayList(keys), downloadAction.customCacheKey, downloadAction.data));
        }
        return (DownloadAction) Assertions.checkNotNull(actionQueue.peek());
    }
}
