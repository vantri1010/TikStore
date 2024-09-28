package com.google.android.exoplayer2.offline;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.scheduler.Requirements;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.HashMap;

public abstract class DownloadService extends Service {
    public static final String ACTION_ADD = "com.google.android.exoplayer.downloadService.action.ADD";
    public static final String ACTION_INIT = "com.google.android.exoplayer.downloadService.action.INIT";
    private static final String ACTION_RESTART = "com.google.android.exoplayer.downloadService.action.RESTART";
    private static final boolean DEBUG = false;
    public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
    public static final int FOREGROUND_NOTIFICATION_ID_NONE = 0;
    public static final String KEY_DOWNLOAD_ACTION = "download_action";
    public static final String KEY_FOREGROUND = "foreground";
    private static final String TAG = "DownloadService";
    private static final HashMap<Class<? extends DownloadService>, DownloadManagerHelper> downloadManagerListeners = new HashMap<>();
    private final String channelId;
    private final int channelName;
    /* access modifiers changed from: private */
    public DownloadManager downloadManager;
    private final ForegroundNotificationUpdater foregroundNotificationUpdater;
    private int lastStartId;
    private boolean startedInForeground;
    private boolean taskRemoved;

    /* access modifiers changed from: protected */
    public abstract DownloadManager getDownloadManager();

    /* access modifiers changed from: protected */
    public abstract Scheduler getScheduler();

    protected DownloadService(int foregroundNotificationId) {
        this(foregroundNotificationId, 1000);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        this(foregroundNotificationId, foregroundNotificationUpdateInterval, (String) null, 0);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, String channelId2, int channelName2) {
        this.foregroundNotificationUpdater = foregroundNotificationId == 0 ? null : new ForegroundNotificationUpdater(foregroundNotificationId, foregroundNotificationUpdateInterval);
        this.channelId = channelId2;
        this.channelName = channelName2;
    }

    public static Intent buildAddActionIntent(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        return getIntent(context, clazz, ACTION_ADD).putExtra(KEY_DOWNLOAD_ACTION, downloadAction.toByteArray()).putExtra(KEY_FOREGROUND, foreground);
    }

    public static void startWithAction(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        Intent intent = buildAddActionIntent(context, clazz, downloadAction, foreground);
        if (foreground) {
            Util.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    public static void start(Context context, Class<? extends DownloadService> clazz) {
        context.startService(getIntent(context, clazz, ACTION_INIT));
    }

    public static void startForeground(Context context, Class<? extends DownloadService> clazz) {
        Util.startForegroundService(context, getIntent(context, clazz, ACTION_INIT).putExtra(KEY_FOREGROUND, true));
    }

    public void onCreate() {
        logd("onCreate");
        String str = this.channelId;
        if (str != null) {
            NotificationUtil.createNotificationChannel(this, str, this.channelName, 2);
        }
        Class<?> cls = getClass();
        DownloadManagerHelper downloadManagerHelper = downloadManagerListeners.get(cls);
        if (downloadManagerHelper == null) {
            downloadManagerHelper = new DownloadManagerHelper(getApplicationContext(), getDownloadManager(), getScheduler(), cls);
            downloadManagerListeners.put(cls, downloadManagerHelper);
        }
        this.downloadManager = downloadManagerHelper.downloadManager;
        downloadManagerHelper.attachService(this);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0062, code lost:
        if (r1.equals(ACTION_INIT) == false) goto L_0x0077;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00bd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(android.content.Intent r9, int r10, int r11) {
        /*
            r8 = this;
            r8.lastStartId = r11
            r0 = 0
            r8.taskRemoved = r0
            r1 = 0
            java.lang.String r2 = "com.google.android.exoplayer.downloadService.action.RESTART"
            r3 = 1
            if (r9 == 0) goto L_0x0026
            java.lang.String r1 = r9.getAction()
            boolean r4 = r8.startedInForeground
            java.lang.String r5 = "foreground"
            boolean r5 = r9.getBooleanExtra(r5, r0)
            if (r5 != 0) goto L_0x0022
            boolean r5 = r2.equals(r1)
            if (r5 == 0) goto L_0x0020
            goto L_0x0022
        L_0x0020:
            r5 = 0
            goto L_0x0023
        L_0x0022:
            r5 = 1
        L_0x0023:
            r4 = r4 | r5
            r8.startedInForeground = r4
        L_0x0026:
            if (r1 != 0) goto L_0x002a
            java.lang.String r1 = "com.google.android.exoplayer.downloadService.action.INIT"
        L_0x002a:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "onStartCommand action: "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = " startId: "
            r4.append(r5)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            r8.logd(r4)
            r4 = -1
            int r5 = r1.hashCode()
            r6 = -871181424(0xffffffffcc12d390, float:-3.8489664E7)
            r7 = 2
            if (r5 == r6) goto L_0x006f
            r2 = -382886238(0xffffffffe92d9ea2, float:-1.311833E25)
            if (r5 == r2) goto L_0x0065
            r2 = 1015676687(0x3c89ff0f, float:0.016845254)
            if (r5 == r2) goto L_0x005c
        L_0x005b:
            goto L_0x0077
        L_0x005c:
            java.lang.String r2 = "com.google.android.exoplayer.downloadService.action.INIT"
            boolean r2 = r1.equals(r2)
            if (r2 == 0) goto L_0x005b
            goto L_0x0078
        L_0x0065:
            java.lang.String r0 = "com.google.android.exoplayer.downloadService.action.ADD"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x005b
            r0 = 2
            goto L_0x0078
        L_0x006f:
            boolean r0 = r1.equals(r2)
            if (r0 == 0) goto L_0x005b
            r0 = 1
            goto L_0x0078
        L_0x0077:
            r0 = -1
        L_0x0078:
            if (r0 == 0) goto L_0x00b4
            if (r0 == r3) goto L_0x00b4
            java.lang.String r2 = "DownloadService"
            if (r0 == r7) goto L_0x0095
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Ignoring unrecognized action: "
            r0.append(r4)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.google.android.exoplayer2.util.Log.e(r2, r0)
            goto L_0x00b5
        L_0x0095:
            java.lang.String r0 = "download_action"
            byte[] r0 = r9.getByteArrayExtra(r0)
            if (r0 != 0) goto L_0x00a3
            java.lang.String r4 = "Ignoring ADD action with no action data"
            com.google.android.exoplayer2.util.Log.e(r2, r4)
            goto L_0x00b5
        L_0x00a3:
            com.google.android.exoplayer2.offline.DownloadManager r4 = r8.downloadManager     // Catch:{ IOException -> 0x00ad }
            com.google.android.exoplayer2.offline.DownloadAction r5 = com.google.android.exoplayer2.offline.DownloadAction.fromByteArray(r0)     // Catch:{ IOException -> 0x00ad }
            r4.handleAction(r5)     // Catch:{ IOException -> 0x00ad }
            goto L_0x00b5
        L_0x00ad:
            r4 = move-exception
            java.lang.String r5 = "Failed to handle ADD action"
            com.google.android.exoplayer2.util.Log.e(r2, r5, r4)
            goto L_0x00b5
        L_0x00b4:
        L_0x00b5:
            com.google.android.exoplayer2.offline.DownloadManager r0 = r8.downloadManager
            boolean r0 = r0.isIdle()
            if (r0 == 0) goto L_0x00c0
            r8.stop()
        L_0x00c0:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.offline.DownloadService.onStartCommand(android.content.Intent, int, int):int");
    }

    public void onTaskRemoved(Intent rootIntent) {
        logd("onTaskRemoved rootIntent: " + rootIntent);
        this.taskRemoved = true;
    }

    public void onDestroy() {
        logd("onDestroy");
        downloadManagerListeners.get(getClass()).detachService(this, this.downloadManager.getDownloadCount() <= 0);
        ForegroundNotificationUpdater foregroundNotificationUpdater2 = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater2 != null) {
            foregroundNotificationUpdater2.stopPeriodicUpdates();
        }
    }

    public final IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: protected */
    public Notification getForegroundNotification(DownloadState[] downloadStates) {
        throw new IllegalStateException(getClass().getName() + " is started in the foreground but getForegroundNotification() is not implemented.");
    }

    /* access modifiers changed from: protected */
    public void onDownloadStateChanged(DownloadState downloadState) {
    }

    /* access modifiers changed from: private */
    public void notifyDownloadStateChange(DownloadState downloadState) {
        onDownloadStateChanged(downloadState);
        if (this.foregroundNotificationUpdater == null) {
            return;
        }
        if (downloadState.state == 2 || downloadState.state == 5 || downloadState.state == 7) {
            this.foregroundNotificationUpdater.startPeriodicUpdates();
        } else {
            this.foregroundNotificationUpdater.update();
        }
    }

    /* access modifiers changed from: private */
    public void stop() {
        ForegroundNotificationUpdater foregroundNotificationUpdater2 = this.foregroundNotificationUpdater;
        if (foregroundNotificationUpdater2 != null) {
            foregroundNotificationUpdater2.stopPeriodicUpdates();
            if (this.startedInForeground && Util.SDK_INT >= 26) {
                this.foregroundNotificationUpdater.showNotificationIfNotAlready();
            }
        }
        if (Util.SDK_INT >= 28 || !this.taskRemoved) {
            boolean stopSelfResult = stopSelfResult(this.lastStartId);
            logd("stopSelf(" + this.lastStartId + ") result: " + stopSelfResult);
            return;
        }
        stopSelf();
        logd("stopSelf()");
    }

    private void logd(String message) {
    }

    /* access modifiers changed from: private */
    public static Intent getIntent(Context context, Class<? extends DownloadService> clazz, String action) {
        return new Intent(context, clazz).setAction(action);
    }

    private final class ForegroundNotificationUpdater implements Runnable {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean notificationDisplayed;
        private final int notificationId;
        private boolean periodicUpdatesStarted;
        private final long updateInterval;

        public ForegroundNotificationUpdater(int notificationId2, long updateInterval2) {
            this.notificationId = notificationId2;
            this.updateInterval = updateInterval2;
        }

        public void startPeriodicUpdates() {
            this.periodicUpdatesStarted = true;
            update();
        }

        public void stopPeriodicUpdates() {
            this.periodicUpdatesStarted = false;
            this.handler.removeCallbacks(this);
        }

        public void update() {
            DownloadState[] downloadStates = DownloadService.this.downloadManager.getAllDownloadStates();
            DownloadService downloadService = DownloadService.this;
            downloadService.startForeground(this.notificationId, downloadService.getForegroundNotification(downloadStates));
            this.notificationDisplayed = true;
            if (this.periodicUpdatesStarted) {
                this.handler.removeCallbacks(this);
                this.handler.postDelayed(this, this.updateInterval);
            }
        }

        public void showNotificationIfNotAlready() {
            if (!this.notificationDisplayed) {
                update();
            }
        }

        public void run() {
            update();
        }
    }

    private static final class DownloadManagerHelper implements DownloadManager.Listener {
        private final Context context;
        /* access modifiers changed from: private */
        public final DownloadManager downloadManager;
        private DownloadService downloadService;
        private final Scheduler scheduler;
        private final Class<? extends DownloadService> serviceClass;

        private DownloadManagerHelper(Context context2, DownloadManager downloadManager2, Scheduler scheduler2, Class<? extends DownloadService> serviceClass2) {
            this.context = context2;
            this.downloadManager = downloadManager2;
            this.scheduler = scheduler2;
            this.serviceClass = serviceClass2;
            downloadManager2.addListener(this);
            if (scheduler2 != null) {
                Requirements requirements = downloadManager2.getRequirements();
                setSchedulerEnabled(!requirements.checkRequirements(context2), requirements);
            }
        }

        public void attachService(DownloadService downloadService2) {
            Assertions.checkState(this.downloadService == null);
            this.downloadService = downloadService2;
        }

        public void detachService(DownloadService downloadService2, boolean unschedule) {
            Assertions.checkState(this.downloadService == downloadService2);
            this.downloadService = null;
            if (unschedule) {
                this.scheduler.cancel();
            }
        }

        public void onInitialized(DownloadManager downloadManager2) {
        }

        public void onDownloadStateChanged(DownloadManager downloadManager2, DownloadState downloadState) {
            DownloadService downloadService2 = this.downloadService;
            if (downloadService2 != null) {
                downloadService2.notifyDownloadStateChange(downloadState);
            }
        }

        public final void onIdle(DownloadManager downloadManager2) {
            DownloadService downloadService2 = this.downloadService;
            if (downloadService2 != null) {
                downloadService2.stop();
            }
        }

        public void onRequirementsStateChanged(DownloadManager downloadManager2, Requirements requirements, int notMetRequirements) {
            boolean z = true;
            boolean requirementsMet = notMetRequirements == 0;
            if (this.downloadService == null && requirementsMet) {
                try {
                    this.context.startService(DownloadService.getIntent(this.context, this.serviceClass, DownloadService.ACTION_INIT));
                } catch (IllegalStateException e) {
                    return;
                }
            }
            if (this.scheduler != null) {
                if (requirementsMet) {
                    z = false;
                }
                setSchedulerEnabled(z, requirements);
            }
        }

        private void setSchedulerEnabled(boolean enabled, Requirements requirements) {
            if (!enabled) {
                this.scheduler.cancel();
                return;
            }
            if (!this.scheduler.schedule(requirements, this.context.getPackageName(), DownloadService.ACTION_RESTART)) {
                Log.e(DownloadService.TAG, "Scheduling downloads failed.");
            }
        }
    }
}
