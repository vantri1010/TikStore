package ezy.assist.compat;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.google.android.exoplayer2.C;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import java.lang.reflect.Method;

public class SettingsCompat {
    private static final String HUAWEI_PACKAGE = "com.huawei.systemmanager";
    private static final int OP_SYSTEM_ALERT_WINDOW = 24;
    private static final int OP_WRITE_SETTINGS = 23;
    private static final String TAG = "ezy-settings-compat";

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        if (Build.VERSION.SDK_INT >= 18) {
            return checkOp(context, 24);
        }
        return true;
    }

    public static boolean canWriteSettings(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.System.canWrite(context);
        }
        if (Build.VERSION.SDK_INT >= 18) {
            return checkOp(context, 23);
        }
        return true;
    }

    public static boolean setDrawOverlays(Context context, boolean allowed) {
        return setMode(context, 24, allowed);
    }

    public static boolean setWriteSettings(Context context, boolean allowed) {
        return setMode(context, 23, allowed);
    }

    public static void manageDrawOverlays(Context context) {
        if ((Build.VERSION.SDK_INT < 18 || !manageDrawOverlaysForRom(context)) && Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    public static void manageWriteSettings(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    private static boolean manageDrawOverlaysForRom(Context context) {
        if (RomUtil.isMiui()) {
            return manageDrawOverlaysForMiui(context);
        }
        if (RomUtil.isEmui()) {
            return manageDrawOverlaysForEmui(context);
        }
        if (RomUtil.isFlyme()) {
            return manageDrawOverlaysForFlyme(context);
        }
        if (RomUtil.isOppo()) {
            return manageDrawOverlaysForOppo(context);
        }
        if (RomUtil.isVivo()) {
            return manageDrawOverlaysForVivo(context);
        }
        if (RomUtil.isQiku()) {
            return manageDrawOverlaysForQihu(context);
        }
        if (RomUtil.isSmartisan()) {
            return manageDrawOverlaysForSmartisan(context);
        }
        return false;
    }

    private static boolean checkOp(Context context, int op) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService("appops");
        Class<AppOpsManager> cls = AppOpsManager.class;
        try {
            if (((Integer) cls.getDeclaredMethod("checkOp", new Class[]{Integer.TYPE, Integer.TYPE, String.class}).invoke(manager, new Object[]{Integer.valueOf(op), Integer.valueOf(Binder.getCallingUid()), context.getPackageName()})).intValue() == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
    }

    private static boolean setMode(Context context, int op, boolean allowed) {
        if (Build.VERSION.SDK_INT < 18 || Build.VERSION.SDK_INT >= 21) {
            return false;
        }
        AppOpsManager manager = (AppOpsManager) context.getSystemService("appops");
        try {
            Method method = AppOpsManager.class.getDeclaredMethod("setMode", new Class[]{Integer.TYPE, Integer.TYPE, String.class, Integer.TYPE});
            Object[] objArr = new Object[4];
            objArr[0] = Integer.valueOf(op);
            objArr[1] = Integer.valueOf(Binder.getCallingUid());
            objArr[2] = context.getPackageName();
            objArr[3] = Integer.valueOf(allowed ? 0 : 1);
            method.invoke(manager, objArr);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return false;
        }
    }

    private static boolean startSafely(Context context, Intent intent) {
        if (context.getPackageManager().queryIntentActivities(intent, 65536).size() > 0) {
            intent.setFlags(C.ENCODING_PCM_MU_LAW);
            context.startActivity(intent);
            return true;
        }
        Log.e(TAG, "Intent is not available! " + intent);
        return false;
    }

    private static boolean manageDrawOverlaysForMiui(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return false;
        }
        Intent intent1 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent1.setData(Uri.fromParts("package", context.getPackageName(), (String) null));
        return startSafely(context, intent1);
    }

    private static boolean manageDrawOverlaysForEmui(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 21) {
            intent.setClassName(HUAWEI_PACKAGE, "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");
            if (startSafely(context, intent)) {
                return true;
            }
        }
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.notificationmanager.ui.NotificationManagmentActivity");
        intent.putExtra("showTabsNumber", 1);
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.permissionmanager.ui.MainActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        return false;
    }

    private static boolean manageDrawOverlaysForVivo(Context context) {
        Intent intent = new Intent("com.iqoo.secure");
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity");
        return startSafely(context, intent);
    }

    private static boolean manageDrawOverlaysForOppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra(RemoteConfigConstants.RequestFieldKey.PACKAGE_NAME, context.getPackageName());
        intent.setAction("com.oppo.safe");
        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setAction("com.color.safecenter");
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setAction("com.coloros.safecenter");
        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        return startSafely(context, intent);
    }

    private static boolean manageDrawOverlaysForFlyme(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        intent.putExtra(RemoteConfigConstants.RequestFieldKey.PACKAGE_NAME, context.getPackageName());
        return startSafely(context, intent);
    }

    private static boolean manageDrawOverlaysForQihu(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        if (startSafely(context, intent)) {
            return true;
        }
        intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        return startSafely(context, intent);
    }

    private static boolean manageDrawOverlaysForSmartisan(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Intent intent = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW");
            intent.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
            intent.putExtra("index", 17);
            return startSafely(context, intent);
        }
        Intent intent2 = new Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS");
        intent2.setClassName("com.smartisanos.security", "com.smartisanos.security.SwitchedPermissions");
        intent2.putExtra("permission", new String[]{"android.permission.SYSTEM_ALERT_WINDOW"});
        return startSafely(context, intent2);
    }
}
