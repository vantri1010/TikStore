package com.google.android.exoplayer2.offline;

import android.content.Context;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public final class DownloadManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_MAX_SIMULTANEOUS_DOWNLOADS = 1;
    public static final int DEFAULT_MIN_RETRY_COUNT = 5;
    public static final Requirements DEFAULT_REQUIREMENTS = new Requirements(1, false, false);
    private static final String TAG = "DownloadManager";
    private final ActionFile actionFile;
    private final ArrayDeque<DownloadAction> actionQueue;
    private final ArrayList<Download> activeDownloads;
    private final Context context;
    private final DownloaderFactory downloaderFactory;
    private final ArrayList<Download> downloads;
    private final Handler fileIOHandler;
    private final HandlerThread fileIOThread;
    /* access modifiers changed from: private */
    public final Handler handler;
    private boolean initialized;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final int maxActiveDownloads;
    private final int minRetryCount;
    /* access modifiers changed from: private */
    public boolean released;
    private RequirementsWatcher requirementsWatcher;
    private int stickyStopFlags;

    public interface Listener {
        void onDownloadStateChanged(DownloadManager downloadManager, DownloadState downloadState);

        void onIdle(DownloadManager downloadManager);

        void onInitialized(DownloadManager downloadManager);

        void onRequirementsStateChanged(DownloadManager downloadManager, Requirements requirements, int i);
    }

    public DownloadManager(Context context2, File actionFile2, DownloaderFactory downloaderFactory2) {
        this(context2, actionFile2, downloaderFactory2, 1, 5, DEFAULT_REQUIREMENTS);
    }

    public DownloadManager(Context context2, File actionFile2, DownloaderFactory downloaderFactory2, int maxSimultaneousDownloads, int minRetryCount2, Requirements requirements) {
        this.context = context2.getApplicationContext();
        this.actionFile = new ActionFile(actionFile2);
        this.downloaderFactory = downloaderFactory2;
        this.maxActiveDownloads = maxSimultaneousDownloads;
        this.minRetryCount = minRetryCount2;
        this.stickyStopFlags = 3;
        this.downloads = new ArrayList<>();
        this.activeDownloads = new ArrayList<>();
        Looper looper = Looper.myLooper();
        this.handler = new Handler(looper == null ? Looper.getMainLooper() : looper);
        HandlerThread handlerThread = new HandlerThread("DownloadManager file i/o");
        this.fileIOThread = handlerThread;
        handlerThread.start();
        this.fileIOHandler = new Handler(this.fileIOThread.getLooper());
        this.listeners = new CopyOnWriteArraySet<>();
        this.actionQueue = new ArrayDeque<>();
        watchRequirements(requirements);
        loadActions();
        logd("Created");
    }

    public void setRequirements(Requirements requirements) {
        Assertions.checkState(!this.released);
        if (!requirements.equals(this.requirementsWatcher.getRequirements())) {
            this.requirementsWatcher.stop();
            notifyListenersRequirementsStateChange(watchRequirements(requirements));
        }
    }

    public Requirements getRequirements() {
        return this.requirementsWatcher.getRequirements();
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void startDownloads() {
        clearStopFlags(2);
    }

    public void stopDownloads() {
        setStopFlags(2);
    }

    private void setStopFlags(int flags) {
        updateStopFlags(flags, flags);
    }

    private void clearStopFlags(int flags) {
        updateStopFlags(flags, 0);
    }

    private void updateStopFlags(int flags, int values) {
        Assertions.checkState(!this.released);
        int i = this.stickyStopFlags;
        int updatedStickyStopFlags = (values & flags) | ((~flags) & i);
        if (i != updatedStickyStopFlags) {
            this.stickyStopFlags = updatedStickyStopFlags;
            for (int i2 = 0; i2 < this.downloads.size(); i2++) {
                this.downloads.get(i2).updateStopFlags(flags, values);
            }
            logdFlags("Sticky stop flags are updated", updatedStickyStopFlags);
        }
    }

    public void handleAction(DownloadAction action) {
        Assertions.checkState(!this.released);
        if (this.initialized) {
            addDownloadForAction(action);
            saveActions();
            return;
        }
        this.actionQueue.add(action);
    }

    public int getDownloadCount() {
        Assertions.checkState(!this.released);
        return this.downloads.size();
    }

    public DownloadState getDownloadState(String id) {
        Assertions.checkState(!this.released);
        for (int i = 0; i < this.downloads.size(); i++) {
            Download download = this.downloads.get(i);
            if (download.id.equals(id)) {
                return download.getDownloadState();
            }
        }
        return null;
    }

    public DownloadState[] getAllDownloadStates() {
        Assertions.checkState(!this.released);
        DownloadState[] states = new DownloadState[this.downloads.size()];
        for (int i = 0; i < states.length; i++) {
            states[i] = this.downloads.get(i).getDownloadState();
        }
        return states;
    }

    public boolean isInitialized() {
        Assertions.checkState(!this.released);
        return this.initialized;
    }

    public boolean isIdle() {
        Assertions.checkState(!this.released);
        if (!this.initialized) {
            return false;
        }
        for (int i = 0; i < this.downloads.size(); i++) {
            if (!this.downloads.get(i).isIdle()) {
                return false;
            }
        }
        return true;
    }

    public void release() {
        if (!this.released) {
            setStopFlags(1);
            this.released = true;
            RequirementsWatcher requirementsWatcher2 = this.requirementsWatcher;
            if (requirementsWatcher2 != null) {
                requirementsWatcher2.stop();
            }
            ConditionVariable fileIOFinishedCondition = new ConditionVariable();
            Handler handler2 = this.fileIOHandler;
            fileIOFinishedCondition.getClass();
            handler2.post(new Runnable(fileIOFinishedCondition) {
                private final /* synthetic */ ConditionVariable f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    this.f$0.open();
                }
            });
            fileIOFinishedCondition.block();
            this.fileIOThread.quit();
            logd("Released");
        }
    }

    private void addDownloadForAction(DownloadAction action) {
        for (int i = 0; i < this.downloads.size(); i++) {
            Download download = this.downloads.get(i);
            if (download.addAction(action)) {
                logd("Action is added to existing download", download);
                return;
            }
        }
        Download download2 = new Download(this.downloaderFactory, action, this.minRetryCount, this.stickyStopFlags);
        this.downloads.add(download2);
        logd("Download is added", download2);
    }

    /* access modifiers changed from: private */
    public void maybeStartDownload(Download download) {
        if (this.activeDownloads.size() < this.maxActiveDownloads && download.start()) {
            this.activeDownloads.add(download);
        }
    }

    private void maybeNotifyListenersIdle() {
        if (isIdle()) {
            logd("Notify idle state");
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                it.next().onIdle(this);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onDownloadStateChange(Download download) {
        if (!this.released) {
            boolean idle = download.isIdle();
            if (idle) {
                this.activeDownloads.remove(download);
            }
            notifyListenersDownloadStateChange(download);
            if (download.isFinished()) {
                this.downloads.remove(download);
                saveActions();
            }
            if (idle) {
                for (int i = 0; i < this.downloads.size(); i++) {
                    maybeStartDownload(this.downloads.get(i));
                }
                maybeNotifyListenersIdle();
            }
        }
    }

    private void notifyListenersDownloadStateChange(Download download) {
        logd("Download state is changed", download);
        DownloadState downloadState = download.getDownloadState();
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onDownloadStateChanged(this, downloadState);
        }
    }

    /* access modifiers changed from: private */
    public void notifyListenersRequirementsStateChange(int notMetRequirements) {
        logdFlags("Not met requirements are changed", notMetRequirements);
        Iterator<Listener> it = this.listeners.iterator();
        while (it.hasNext()) {
            it.next().onRequirementsStateChanged(this, this.requirementsWatcher.getRequirements(), notMetRequirements);
        }
    }

    private void loadActions() {
        this.fileIOHandler.post(new Runnable() {
            public final void run() {
                DownloadManager.this.lambda$loadActions$1$DownloadManager();
            }
        });
    }

    public /* synthetic */ void lambda$loadActions$1$DownloadManager() {
        DownloadAction[] loadedActions;
        try {
            loadedActions = this.actionFile.load();
            logd("Action file is loaded.");
        } catch (Throwable e) {
            Log.e(TAG, "Action file loading failed.", e);
            loadedActions = new DownloadAction[0];
        }
        this.handler.post(new Runnable(loadedActions) {
            private final /* synthetic */ DownloadAction[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                DownloadManager.this.lambda$null$0$DownloadManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$DownloadManager(DownloadAction[] actions) {
        if (!this.released) {
            for (DownloadAction action : actions) {
                addDownloadForAction(action);
            }
            if (!this.actionQueue.isEmpty()) {
                while (!this.actionQueue.isEmpty()) {
                    addDownloadForAction(this.actionQueue.remove());
                }
                saveActions();
            }
            logd("Downloads are created.");
            this.initialized = true;
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                it.next().onInitialized(this);
            }
            clearStopFlags(1);
        }
    }

    private void saveActions() {
        if (!this.released) {
            ArrayList<DownloadAction> actions = new ArrayList<>(this.downloads.size());
            for (int i = 0; i < this.downloads.size(); i++) {
                actions.addAll(this.downloads.get(i).actionQueue);
            }
            this.fileIOHandler.post(new Runnable((DownloadAction[]) actions.toArray(new DownloadAction[0])) {
                private final /* synthetic */ DownloadAction[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DownloadManager.this.lambda$saveActions$2$DownloadManager(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$saveActions$2$DownloadManager(DownloadAction[] actionsArray) {
        try {
            this.actionFile.store(actionsArray);
            logd("Actions persisted.");
        } catch (IOException e) {
            Log.e(TAG, "Persisting actions failed.", e);
        }
    }

    private static void logd(String message) {
    }

    /* access modifiers changed from: private */
    public static void logd(String message, Download download) {
    }

    private static void logdFlags(String message, int flags) {
    }

    private int watchRequirements(Requirements requirements) {
        RequirementsWatcher requirementsWatcher2 = new RequirementsWatcher(this.context, new RequirementListener(), requirements);
        this.requirementsWatcher = requirementsWatcher2;
        int notMetRequirements = requirementsWatcher2.start();
        if (notMetRequirements == 0) {
            startDownloads();
        } else {
            stopDownloads();
        }
        return notMetRequirements;
    }

    private static final class Download {
        /* access modifiers changed from: private */
        public final ArrayDeque<DownloadAction> actionQueue;
        private final DownloadManager downloadManager;
        private DownloadThread downloadThread;
        private Downloader downloader;
        private final DownloaderFactory downloaderFactory;
        private int failureReason;
        /* access modifiers changed from: private */
        public final String id;
        private final int minRetryCount;
        private final long startTimeMs;
        private int state;
        private int stopFlags;

        private Download(DownloadManager downloadManager2, DownloaderFactory downloaderFactory2, DownloadAction action, int minRetryCount2, int stopFlags2) {
            this.id = action.id;
            this.downloadManager = downloadManager2;
            this.downloaderFactory = downloaderFactory2;
            this.minRetryCount = minRetryCount2;
            this.stopFlags = stopFlags2;
            this.startTimeMs = System.currentTimeMillis();
            ArrayDeque<DownloadAction> arrayDeque = new ArrayDeque<>();
            this.actionQueue = arrayDeque;
            arrayDeque.add(action);
            initialize(false);
        }

        public boolean addAction(DownloadAction newAction) {
            DownloadAction action = this.actionQueue.peek();
            if (!action.isSameMedia(newAction)) {
                return false;
            }
            Assertions.checkState(action.type.equals(newAction.type));
            this.actionQueue.add(newAction);
            DownloadAction updatedAction = DownloadActionUtil.mergeActions(this.actionQueue);
            int i = this.state;
            if (i == 5) {
                Assertions.checkState(updatedAction.isRemoveAction);
                if (this.actionQueue.size() > 1) {
                    setState(7);
                }
            } else if (i == 7) {
                Assertions.checkState(updatedAction.isRemoveAction);
                if (this.actionQueue.size() == 1) {
                    setState(5);
                }
            } else if (!action.equals(updatedAction)) {
                int i2 = this.state;
                if (i2 == 2) {
                    stopDownloadThread();
                } else {
                    Assertions.checkState(i2 == 0 || i2 == 1);
                    initialize(false);
                }
            }
            return true;
        }

        public DownloadState getDownloadState() {
            float downloadPercentage = -1.0f;
            long downloadedBytes = 0;
            long totalBytes = -1;
            Downloader downloader2 = this.downloader;
            if (downloader2 != null) {
                downloadPercentage = downloader2.getDownloadPercentage();
                downloadedBytes = this.downloader.getDownloadedBytes();
                totalBytes = this.downloader.getTotalBytes();
            }
            DownloadAction action = this.actionQueue.peek();
            byte[] bArr = action.data;
            DownloadAction downloadAction = action;
            return new DownloadState(action.id, action.type, action.uri, action.customCacheKey, this.state, downloadPercentage, downloadedBytes, totalBytes, this.failureReason, this.stopFlags, this.startTimeMs, System.currentTimeMillis(), (StreamKey[]) action.keys.toArray(new StreamKey[0]), bArr);
        }

        public boolean isFinished() {
            int i = this.state;
            return i == 4 || i == 3 || i == 6;
        }

        public boolean isIdle() {
            int i = this.state;
            return (i == 2 || i == 5 || i == 7) ? false : true;
        }

        public String toString() {
            return this.id + ' ' + DownloadState.getStateString(this.state);
        }

        public boolean start() {
            if (this.state != 0) {
                return false;
            }
            startDownloadThread(this.actionQueue.peek());
            setState(2);
            return true;
        }

        public void setStopFlags(int flags) {
            updateStopFlags(flags, flags);
        }

        public void clearStopFlags(int flags) {
            updateStopFlags(flags, 0);
        }

        public void updateStopFlags(int flags, int values) {
            int i = (values & flags) | (this.stopFlags & (~flags));
            this.stopFlags = i;
            if (i != 0) {
                int i2 = this.state;
                if (i2 == 2) {
                    stopDownloadThread();
                } else if (i2 == 0) {
                    setState(1);
                }
            } else if (this.state == 1) {
                startOrQueue(false);
            }
        }

        private void initialize(boolean restart) {
            DownloadAction action = this.actionQueue.peek();
            if (action.isRemoveAction) {
                if (!this.downloadManager.released) {
                    startDownloadThread(action);
                }
                setState(this.actionQueue.size() == 1 ? 5 : 7);
            } else if (this.stopFlags != 0) {
                setState(1);
            } else {
                startOrQueue(restart);
            }
        }

        private void startOrQueue(boolean restart) {
            this.state = 0;
            if (restart) {
                start();
            } else {
                this.downloadManager.maybeStartDownload(this);
            }
            if (this.state == 0) {
                this.downloadManager.onDownloadStateChange(this);
            }
        }

        private void setState(int newState) {
            this.state = newState;
            this.downloadManager.onDownloadStateChange(this);
        }

        private void startDownloadThread(DownloadAction action) {
            Downloader createDownloader = this.downloaderFactory.createDownloader(action);
            this.downloader = createDownloader;
            this.downloadThread = new DownloadThread(this, createDownloader, action.isRemoveAction, this.minRetryCount, this.downloadManager.handler);
        }

        private void stopDownloadThread() {
            ((DownloadThread) Assertions.checkNotNull(this.downloadThread)).cancel();
        }

        /* access modifiers changed from: private */
        public void onDownloadThreadStopped(Throwable finalError) {
            int i;
            boolean z = false;
            this.failureReason = 0;
            if (!this.downloadThread.isCanceled) {
                if (finalError != null && (i = this.state) != 5 && i != 7) {
                    this.failureReason = 1;
                    setState(4);
                    return;
                } else if (this.actionQueue.size() == 1) {
                    int i2 = this.state;
                    if (i2 == 5) {
                        setState(6);
                        return;
                    }
                    if (i2 == 2) {
                        z = true;
                    }
                    Assertions.checkState(z);
                    setState(3);
                    return;
                } else {
                    this.actionQueue.remove();
                }
            }
            if (this.state == 2) {
                z = true;
            }
            initialize(z);
        }
    }

    private static class DownloadThread implements Runnable {
        private final Handler callbackHandler;
        private final Download download;
        private final Downloader downloader;
        /* access modifiers changed from: private */
        public volatile boolean isCanceled;
        private final int minRetryCount;
        private final boolean remove;
        private final Thread thread;

        private DownloadThread(Download download2, Downloader downloader2, boolean remove2, int minRetryCount2, Handler callbackHandler2) {
            this.download = download2;
            this.downloader = downloader2;
            this.remove = remove2;
            this.minRetryCount = minRetryCount2;
            this.callbackHandler = callbackHandler2;
            Thread thread2 = new Thread(this);
            this.thread = thread2;
            thread2.start();
        }

        public void cancel() {
            this.isCanceled = true;
            this.downloader.cancel();
            this.thread.interrupt();
        }

        public void run() {
            int errorCount;
            long errorPosition;
            DownloadManager.logd("Download is started", this.download);
            Throwable error = null;
            try {
                if (this.remove) {
                    this.downloader.remove();
                } else {
                    errorCount = 0;
                    errorPosition = -1;
                    while (!this.isCanceled) {
                        this.downloader.download();
                    }
                }
            } catch (IOException e) {
                if (!this.isCanceled) {
                    long downloadedBytes = this.downloader.getDownloadedBytes();
                    if (downloadedBytes != errorPosition) {
                        DownloadManager.logd("Reset error count. downloadedBytes = " + downloadedBytes, this.download);
                        errorPosition = downloadedBytes;
                        errorCount = 0;
                    }
                    errorCount++;
                    if (errorCount <= this.minRetryCount) {
                        DownloadManager.logd("Download error. Retry " + errorCount, this.download);
                        Thread.sleep((long) getRetryDelayMillis(errorCount));
                    } else {
                        throw e;
                    }
                }
            } catch (Throwable e2) {
                error = e2;
            }
            this.callbackHandler.post(new Runnable(error) {
                private final /* synthetic */ Throwable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    DownloadManager.DownloadThread.this.lambda$run$0$DownloadManager$DownloadThread(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$run$0$DownloadManager$DownloadThread(Throwable finalError) {
            this.download.onDownloadThreadStopped(this.isCanceled ? null : finalError);
        }

        private int getRetryDelayMillis(int errorCount) {
            return Math.min((errorCount - 1) * 1000, 5000);
        }
    }

    private class RequirementListener implements RequirementsWatcher.Listener {
        private RequirementListener() {
        }

        public void requirementsMet(RequirementsWatcher requirementsWatcher) {
            DownloadManager.this.startDownloads();
            DownloadManager.this.notifyListenersRequirementsStateChange(0);
        }

        public void requirementsNotMet(RequirementsWatcher requirementsWatcher, int notMetRequirements) {
            DownloadManager.this.stopDownloads();
            DownloadManager.this.notifyListenersRequirementsStateChange(notMetRequirements);
        }
    }
}
