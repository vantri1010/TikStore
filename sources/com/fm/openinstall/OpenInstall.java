package com.fm.openinstall;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppInstallRetryAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.listener.ResultCallback;
import com.fm.openinstall.model.AppData;
import io.openinstall.sdk.b;
import io.openinstall.sdk.bf;
import io.openinstall.sdk.c;
import io.openinstall.sdk.cb;
import io.openinstall.sdk.cc;
import io.openinstall.sdk.cd;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

public final class OpenInstall {
    private static volatile boolean a = false;

    private OpenInstall() {
    }

    private static void a(Context context, Configuration configuration, Runnable runnable) {
        init(context, configuration);
        if (runnable != null) {
            runnable.run();
            c.a().a((Runnable) null);
        }
    }

    private static boolean a() {
        if (a) {
            return true;
        }
        if (cb.a) {
            cb.c("请先调用 init(Context) 初始化", new Object[0]);
        }
        return false;
    }

    public static void clipBoardEnabled(boolean z) {
        c.a().a(Boolean.valueOf(z));
    }

    public static void getInstall(AppInstallListener appInstallListener) {
        getInstall(appInstallListener, 10);
    }

    public static void getInstall(AppInstallListener appInstallListener, int i) {
        if (!a()) {
            appInstallListener.onInstallFinish((AppData) null, bf.a.NOT_INIT.a().c());
            return;
        }
        if (cb.a && i < 5) {
            cb.b("getInstall设置超时时间过小，易造成数据获取失败，请增大超时时间或调整调用时机", new Object[0]);
        }
        b.a().a(false, i, appInstallListener);
    }

    public static void getInstallCanRetry(AppInstallRetryAdapter appInstallRetryAdapter, int i) {
        if (!a()) {
            appInstallRetryAdapter.onInstallFinish((AppData) null, bf.a.NOT_INIT.a().c());
        } else {
            b.a().a(true, i, (AppInstallListener) appInstallRetryAdapter);
        }
    }

    public static String getOpid() {
        if (!a()) {
            return null;
        }
        return b.a().b();
    }

    @Deprecated
    public static void getUpdateApk(ResultCallback<File> resultCallback) {
        if (!a()) {
            resultCallback.onResult(null, bf.a.NOT_INIT.a().c());
        } else {
            b.a().a(resultCallback);
        }
    }

    public static String getVersion() {
        return "2.8.1";
    }

    public static boolean getWakeUp(Intent intent, AppWakeUpListener appWakeUpListener) {
        if (!a() || !OpenInstallHelper.isSchemeWakeup(intent)) {
            return false;
        }
        b.a().a(intent, appWakeUpListener);
        return true;
    }

    public static void getWakeUpAlwaysCallback(Intent intent, AppWakeUpListener appWakeUpListener) {
        if (!a()) {
            appWakeUpListener.onWakeUpFinish((AppData) null, bf.a.NOT_INIT.a().c());
        } else if (OpenInstallHelper.isSchemeWakeup(intent)) {
            b.a().a(intent, appWakeUpListener);
        } else {
            appWakeUpListener.onWakeUpFinish((AppData) null, bf.a.INVALID_DATA.a().c());
        }
    }

    public static boolean getWakeUpYYB(Activity activity, Intent intent, AppWakeUpListener appWakeUpListener) {
        if (!a()) {
            return false;
        }
        if (OpenInstallHelper.isSchemeWakeup(intent)) {
            b.a().a(intent, appWakeUpListener);
            return true;
        } else if (!OpenInstallHelper.isLauncherFromYYB(activity, intent)) {
            return false;
        } else {
            b.a().a(appWakeUpListener);
            return true;
        }
    }

    public static void getWakeUpYYBAlwaysCallback(Activity activity, Intent intent, AppWakeUpListener appWakeUpListener) {
        if (!a()) {
            appWakeUpListener.onWakeUpFinish((AppData) null, bf.a.NOT_INIT.a().c());
        } else if (OpenInstallHelper.isSchemeWakeup(intent)) {
            b.a().a(intent, appWakeUpListener);
        } else if (OpenInstallHelper.isLauncherFromYYB(activity, intent)) {
            b.a().a(appWakeUpListener);
        } else {
            appWakeUpListener.onWakeUpFinish((AppData) null, bf.a.INVALID_DATA.a().c());
        }
    }

    public static void init(Context context) {
        init(context, Configuration.getDefault());
    }

    public static void init(Context context, Configuration configuration) {
        String a2 = cc.a(context);
        if (!TextUtils.isEmpty(a2)) {
            init(context, a2, configuration);
            return;
        }
        throw new IllegalArgumentException("请在AndroidManifest.xml中配置OpenInstall提供的AppKey");
    }

    public static void init(Context context, String str) {
        init(context, str, Configuration.getDefault());
    }

    public static void init(Context context, String str, Configuration configuration) {
        long currentTimeMillis = System.currentTimeMillis();
        if (context == null) {
            throw new IllegalArgumentException("context不能为空");
        } else if (!TextUtils.isEmpty(str)) {
            if (cb.a) {
                cb.a("SDK Version : " + getVersion(), new Object[0]);
            }
            c.a().a(context.getApplicationContext());
            c.a().a(str);
            c.a().a(configuration);
            WeakReference weakReference = null;
            if (context instanceof Activity) {
                weakReference = new WeakReference((Activity) context);
            }
            synchronized (OpenInstall.class) {
                if (!a) {
                    b.a().a((WeakReference<Activity>) weakReference, currentTimeMillis);
                    a = true;
                }
            }
        } else {
            throw new IllegalArgumentException("请前往OpenInstall控制台的 “Android集成” -> “Android应用配置” 中获取AppKey");
        }
    }

    @Deprecated
    public static void initWithPermission(Activity activity, Configuration configuration) {
        initWithPermission(activity, configuration, (Runnable) null);
    }

    @Deprecated
    public static void initWithPermission(Activity activity, Configuration configuration, Runnable runnable) {
        if (cb.a) {
            cb.b("initWithPermission 方法在后续版本中将被移除，请自行进行权限申请", new Object[0]);
        }
        if (!cd.a(activity)) {
            cd.a(activity, new String[]{"android.permission.READ_PHONE_STATE"}, 987);
            c.a().a(activity.getApplicationContext());
            c.a().a(runnable);
            c.a().a(configuration);
            return;
        }
        a(activity.getApplicationContext(), configuration, runnable);
    }

    public static void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        Context b = c.a().b();
        if (b != null && i == 987) {
            a(b, c.a().e(), c.a().j());
        }
    }

    public static void reportEffectPoint(String str, long j) {
        if (a()) {
            b.a().a(str, j);
        }
    }

    public static void reportEffectPoint(String str, long j, Map<String, String> map) {
        if (a()) {
            b.a().a(str, j, map);
        }
    }

    public static void reportRegister() {
        if (a()) {
            b.a().c();
        }
    }

    public static void reportShare(String str, SharePlatform sharePlatform, ResultCallback<Void> resultCallback) {
        reportShare(str, sharePlatform.name(), resultCallback);
    }

    public static void reportShare(String str, String str2, ResultCallback<Void> resultCallback) {
        if (!a()) {
            resultCallback.onResult(null, bf.a.NOT_INIT.a().c());
        } else {
            b.a().a(str, str2, resultCallback);
        }
    }

    @Deprecated
    public static void serialEnabled(boolean z) {
        c.a().b(Boolean.valueOf(z));
    }

    public static void setChannel(String str) {
        c.a().b(str);
    }

    public static void setDebug(boolean z) {
        cb.a = z;
    }

    public static void setTrackData(ClipData clipData) {
        c.a().a(clipData);
        c.a().a((Boolean) false);
    }
}
