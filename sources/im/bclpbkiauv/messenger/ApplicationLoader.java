package im.bclpbkiauv.messenger;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import androidx.core.app.NotificationCompat;
import com.bjz.comm.net.utils.AppPreferenceUtil;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.NetworkConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.ForegroundDetector;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.DatabaseInstance;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.toast.FcToastUtils;
import im.bclpbkiauv.ui.hui.visualcall.ThreadUtils;
import im.bclpbkiauv.ui.utils.ThirdPartSdkInitUtil;
import java.io.File;

public class ApplicationLoader extends Application implements Constants {
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;
    private static volatile boolean applicationInited = false;
    public static boolean blnShowAuth = false;
    /* access modifiers changed from: private */
    public static ConnectivityManager connectivityManager;
    public static volatile NetworkInfo currentNetworkInfo;
    public static volatile boolean externalInterfacePaused = true;
    public static boolean hasPlayServices;
    public static volatile boolean isScreenOn = false;
    public static volatile boolean mainInterfacePaused = true;
    public static volatile boolean mainInterfacePausedStageQueue = true;
    public static volatile long mainInterfacePausedStageQueueTime;
    public static volatile byte mbytAVideoCallBusy = 0;
    public static volatile byte mbytLiving = 0;
    public static byte mbytMessageReged = 0;
    public static String strDeviceKey = "";
    public static String thirdAppName;
    public static volatile boolean unableGetCurrentNetwork;
    private boolean mBlnSendUPushToken = false;

    public static File getFilesDirFixed() {
        for (int a = 0; a < 10; a++) {
            File path = applicationContext.getFilesDir();
            if (path != null) {
                return path;
            }
        }
        try {
            File path2 = new File(applicationContext.getApplicationInfo().dataDir, "files");
            path2.mkdirs();
            return path2;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return new File("/data/data/im.bclpbkiauv.messenger/files");
        }
    }

    public static void postInitApplication() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("SDKINIT  ===> ApplicationLoader postInitApplication app init ===> start , iid = " + applicationInited + " , preparedId = " + true);
        }
        if (!applicationInited) {
            applicationInited = true;
            AndroidUtilities.runOnUIThread($$Lambda$eLJweN9aG4MXt6IWul3wimO8py8.INSTANCE);
            try {
                LocaleController.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                connectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
                applicationContext.registerReceiver(new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        try {
                            ApplicationLoader.currentNetworkInfo = ApplicationLoader.connectivityManager.getActiveNetworkInfo();
                        } catch (Throwable th) {
                        }
                        boolean isSlow = ApplicationLoader.isConnectionSlow();
                        for (int a = 0; a < 3; a++) {
                            ConnectionsManager.getInstance(a).checkConnection();
                            FileLoader.getInstance(a).onNetworkChanged(isSlow);
                        }
                    }
                }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
                applicationContext.registerReceiver(new ScreenReceiver(), filter);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            try {
                isScreenOn = ((PowerManager) applicationContext.getSystemService("power")).isScreenOn();
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ApplicationLoader ---> postInitApplication screen state = " + isScreenOn);
                }
            } catch (Exception e4) {
                FileLog.e((Throwable) e4);
            }
            SharedConfig.loadConfig();
            for (int a = 0; a < 3; a++) {
                UserConfig.getInstance(a).loadConfig();
                MessagesController.getInstance(a);
                if (a == 0) {
                    SharedConfig.pushStringStatus = "__FIREBASE_GENERATING_SINCE_" + ConnectionsManager.getInstance(a).getCurrentTime() + "__";
                } else {
                    ConnectionsManager.getInstance(a);
                }
                TLRPC.User user = UserConfig.getInstance(a).getCurrentUser();
                if (user != null) {
                    MessagesController.getInstance(a).putUser(user, true);
                    SendMessagesHelper.getInstance(a).checkUnsentMessages();
                }
            }
            ((ApplicationLoader) applicationContext).initPlayServices();
            MediaController.getInstance();
            for (int a2 = 0; a2 < 3; a2++) {
                ContactsController.getInstance(a2).checkAppAccount();
                DownloadController.getInstance(a2);
            }
            ThreadUtils.runOnSubThread(new Runnable() {
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        for (int a = 0; a < 3; a++) {
                            NetworkConfig.getInstance().applyNetconfig(a);
                        }
                    }
                }
            });
            WearDataLayerListenerService.updateWatchConnectionState();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("SDKINIT  ===> ApplicationLoader postInitApplication app init end");
            }
            ThirdPartSdkInitUtil.initOtherSdk(applicationContext);
        }
    }

    public void onCreate() {
        try {
            applicationContext = getApplicationContext();
        } catch (Throwable th) {
        }
        super.onCreate();
        if (applicationContext == null) {
            applicationContext = getApplicationContext();
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("SDKINIT  ===> ApplicationLoader onCreate init start");
        }
        NativeLoader.initNativeLibs(applicationContext);
        ConnectionsManager.native_setJava(false);
        new ForegroundDetector(this);
        applicationHandler = new Handler(applicationContext.getMainLooper());
        AppPreferenceUtil.initSharedPreferences(applicationContext);
        DatabaseInstance.getInstance(applicationContext);
        ToastUtils.init(applicationContext);
        FcToastUtils.init(applicationContext);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("SDKINIT  ===> ApplicationLoader onCreate init end");
        }
    }

    private void sendUPushTokenToServer() {
        AndroidUtilities.runOnUIThread($$Lambda$ApplicationLoader$xqe1VkDKmr805DhxM27KSNrskp4.INSTANCE);
    }

    public static void startPushService() {
        Log.d(Constants.SDK_INIT_TAG, applicationContext.toString() + " startPushService ===> start");
        if (MessagesController.getGlobalNotificationsSettings().getBoolean("pushService", true)) {
            try {
                applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
            } catch (Throwable th) {
            }
        } else {
            stopPushService();
        }
        Log.d(Constants.SDK_INIT_TAG, applicationContext.toString() + " startPushService ===> end");
    }

    public static void stopPushService() {
        applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
        ((AlarmManager) applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM)).cancel(PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0));
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPlayServices() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                ApplicationLoader.this.lambda$initPlayServices$4$ApplicationLoader();
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$initPlayServices$4$ApplicationLoader() {
        boolean checkPlayServices = checkPlayServices();
        hasPlayServices = checkPlayServices;
        if (checkPlayServices) {
            String currentPushString = SharedConfig.pushString;
            if (!TextUtils.isEmpty(currentPushString)) {
                if (BuildVars.DEBUG_PRIVATE_VERSION && BuildVars.LOGS_ENABLED) {
                    FileLog.d("ApplicationLoader ---> initPlayServices GCM regId = " + currentPushString);
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ApplicationLoFader ---> initPlayServices GCM Registration not found.");
            }
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ApplicationLoader.this.lambda$null$3$ApplicationLoader();
                }
            });
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("ApplicationLoader ---> No valid Google Play Services APK found.");
        }
        SharedConfig.pushStringStatus = "__NO_GOOGLE_PLAY_SERVICES__";
        FileLog.d("ApplicationLoader ---> umeng strDeviceKey = " + strDeviceKey);
        if (!TextUtils.isEmpty(strDeviceKey)) {
            GcmPushListenerService.sendUPushRegistrationToServer(strDeviceKey);
        } else {
            this.mBlnSendUPushToken = true;
        }
    }

    public /* synthetic */ void lambda$null$3$ApplicationLoader() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener($$Lambda$ApplicationLoader$LjZQbPLOMEeJMBlW8XZz4DLWNs.INSTANCE).addOnFailureListener(new OnFailureListener() {
                public final void onFailure(Exception exc) {
                    ApplicationLoader.this.lambda$null$2$ApplicationLoader(exc);
                }
            });
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static /* synthetic */ void lambda$null$1(InstanceIdResult instanceIdResult) {
        String token = instanceIdResult.getToken();
        if (!TextUtils.isEmpty(token)) {
            GcmPushListenerService.sendRegistrationToServer(token);
        }
    }

    public /* synthetic */ void lambda$null$2$ApplicationLoader(Exception e) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("ApplicationLoader ---> initPlayServices Failed to get regid");
        }
        SharedConfig.pushStringStatus = "__FIREBASE_FAILED__";
        if (!TextUtils.isEmpty(strDeviceKey)) {
            GcmPushListenerService.sendUPushRegistrationToServer(strDeviceKey);
        } else {
            this.mBlnSendUPushToken = true;
        }
    }

    private boolean checkPlayServices() {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    public static boolean isRoaming() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo != null) {
                return netInfo.isRoaming();
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean isConnectedOrConnectingToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            NetworkInfo.State state = netInfo.getState();
            if (netInfo == null) {
                return false;
            }
            if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING || state == NetworkInfo.State.SUSPENDED) {
                return true;
            }
            return false;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static boolean isConnectedToWiFi() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) applicationContext.getSystemService("connectivity")).getNetworkInfo(1);
            if (netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return false;
        }
    }

    public static int getCurrentNetworkType() {
        if (isConnectedOrConnectingToWiFi()) {
            return 1;
        }
        if (isRoaming()) {
            return 2;
        }
        return 0;
    }

    public static boolean isConnectionSlow() {
        try {
            NetworkInfo netInfo = ((ConnectivityManager) applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if (netInfo.getType() != 0) {
                return false;
            }
            int subtype = netInfo.getSubtype();
            if (subtype == 1 || subtype == 2 || subtype == 4 || subtype == 7 || subtype == 11) {
                return true;
            }
            return false;
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean isNetworkOnline() {
        try {
            ConnectivityManager connectivityManager2 = (ConnectivityManager) applicationContext.getSystemService("connectivity");
            NetworkInfo netInfo = connectivityManager2.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }
            NetworkInfo netInfo2 = connectivityManager2.getNetworkInfo(0);
            if (netInfo2 != null && netInfo2.isConnectedOrConnecting()) {
                return true;
            }
            NetworkInfo netInfo3 = connectivityManager2.getNetworkInfo(1);
            if (netInfo3 == null || !netInfo3.isConnectedOrConnecting()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return true;
        }
    }

    public void onTerminate() {
        super.onTerminate();
    }
}
