package im.bclpbkiauv.ui.utils;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.util.Base64;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.utils.AppUpdater;
import org.webrtc.utils.RecvStatsReportCommon;

public class AppUpdater {
    public static final int Gur = 1;
    public static final int Token = 1;
    public static long dismissCheckUpdateTime;
    public static boolean hasChecked;
    private static AppUpdater instance;
    public static long lastUpdateCheckTime;
    private static int mAccount;
    public static TLRPC.TL_help_appUpdate pendingAppUpdate;
    public static int pendingAppUpdateBuildVersion;
    public static long pendingAppUpdateInstallTime;
    private int mRequestToken;

    public interface OnForceUpdateCallback {
        void onForce(TLRPC.TL_help_appUpdate tL_help_appUpdate);

        void onNoUpdate();

        void onNormal(TLRPC.TL_help_appUpdate tL_help_appUpdate);
    }

    private AppUpdater() {
    }

    public static AppUpdater getInstance(int account) {
        synchronized (AppUpdater.class) {
            mAccount = account;
            if (instance == null) {
                instance = new AppUpdater();
            }
        }
        return instance;
    }

    public void checkAppUpdate(OnForceUpdateCallback callback, boolean isClick) {
        TLRPC.TL_help_getAppUpdate req = new TLRPC.TL_help_getAppUpdate();
        req.source = RecvStatsReportCommon.sdk_platform;
        this.mRequestToken = ConnectionsManager.getInstance(mAccount).sendRequest(req, new RequestDelegate(callback, isClick) {
            private final /* synthetic */ AppUpdater.OnForceUpdateCallback f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AppUpdater.this.lambda$checkAppUpdate$1$AppUpdater(this.f$1, this.f$2, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkAppUpdate$1$AppUpdater(OnForceUpdateCallback callback, boolean isClick, TLObject response, TLRPC.TL_error error) {
        hasChecked = true;
        lastUpdateCheckTime = System.currentTimeMillis();
        if (response instanceof TLRPC.TL_help_appUpdate) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_help_appUpdate) response, callback, isClick) {
                private final /* synthetic */ TLRPC.TL_help_appUpdate f$1;
                private final /* synthetic */ AppUpdater.OnForceUpdateCallback f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    AppUpdater.this.lambda$null$0$AppUpdater(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            callback.onNoUpdate();
        }
    }

    public /* synthetic */ void lambda$null$0$AppUpdater(TLRPC.TL_help_appUpdate res, OnForceUpdateCallback callback, boolean isClick) {
        if (res.can_not_skip) {
            pendingAppUpdate = res;
            pendingAppUpdateBuildVersion = BuildVars.BUILD_VERSION;
            try {
                PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                pendingAppUpdateInstallTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                pendingAppUpdateInstallTime = 0;
            }
            lambda$loadUpdateConfig$2$AppUpdater();
            if (callback != null) {
                callback.onForce(res);
            }
        } else if ((isClick || Math.abs(System.currentTimeMillis() - dismissCheckUpdateTime) >= 43200000) && callback != null) {
            callback.onNormal(res);
        }
    }

    public void cancel() {
        if (this.mRequestToken != 0) {
            ConnectionsManager.getInstance(mAccount).cancelRequest(this.mRequestToken, true);
        }
    }

    /* renamed from: saveUpdateConfig */
    public void lambda$loadUpdateConfig$2$AppUpdater() {
        SharedPreferences.Editor editor = getPreferences().edit();
        TLRPC.TL_help_appUpdate tL_help_appUpdate = pendingAppUpdate;
        if (tL_help_appUpdate != null) {
            try {
                SerializedData data = new SerializedData(tL_help_appUpdate.getObjectSize());
                pendingAppUpdate.serializeToStream(data);
                editor.putString("appUpdate", Base64.encodeToString(data.toByteArray(), 0));
                editor.putInt("appUpdateBuild", pendingAppUpdateBuildVersion);
                editor.putLong("appUpdateTime", pendingAppUpdateInstallTime);
                data.cleanup();
            } catch (Exception e) {
            }
        } else {
            editor.remove("appUpdate");
        }
    }

    public void loadUpdateConfig() {
        SharedPreferences preferences = getPreferences();
        if (mAccount == 0) {
            try {
                String update = preferences.getString("appUpdate", (String) null);
                if (update != null) {
                    pendingAppUpdateBuildVersion = preferences.getInt("appUpdateBuild", BuildVars.BUILD_VERSION);
                    pendingAppUpdateInstallTime = preferences.getLong("appUpdateTime", System.currentTimeMillis());
                    byte[] arr = Base64.decode(update, 0);
                    if (arr != null) {
                        SerializedData data = new SerializedData(arr);
                        pendingAppUpdate = (TLRPC.TL_help_appUpdate) TLRPC.help_AppUpdate.TLdeserialize(data, data.readInt32(false), false);
                        data.cleanup();
                    }
                }
                if (pendingAppUpdate != null) {
                    long updateTime = 0;
                    try {
                        PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                        updateTime = Math.max(packageInfo.lastUpdateTime, packageInfo.firstInstallTime);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    if (pendingAppUpdateBuildVersion != BuildVars.BUILD_VERSION || pendingAppUpdateInstallTime < updateTime) {
                        pendingAppUpdate = null;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                AppUpdater.this.lambda$loadUpdateConfig$2$AppUpdater();
                            }
                        });
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    private SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("update_config", 0);
    }
}
