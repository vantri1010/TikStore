package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public final class AppUtils {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void registerAppStatusChangedListener(Utils.OnAppStatusChangedListener listener) {
        if (listener != null) {
            Utils.getActivityLifecycle().addOnAppStatusChangedListener(listener);
            return;
        }
        throw new NullPointerException("Argument 'listener' of type Utils.OnAppStatusChangedListener (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void unregisterAppStatusChangedListener(Utils.OnAppStatusChangedListener listener) {
        if (listener != null) {
            Utils.getActivityLifecycle().removeOnAppStatusChangedListener(listener);
            return;
        }
        throw new NullPointerException("Argument 'listener' of type Utils.OnAppStatusChangedListener (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void installApp(String filePath) {
        installApp(getFileByPath(filePath));
    }

    public static void installApp(File file) {
        if (isFileExists(file)) {
            Utils.getApp().startActivity(getInstallAppIntent(file, true));
        }
    }

    public static void installApp(Activity activity, String filePath, int requestCode) {
        installApp(activity, getFileByPath(filePath), requestCode);
    }

    public static void installApp(Activity activity, File file, int requestCode) {
        if (isFileExists(file)) {
            activity.startActivityForResult(getInstallAppIntent(file), requestCode);
        }
    }

    public static void uninstallApp(String packageName) {
        if (!isSpace(packageName)) {
            Utils.getApp().startActivity(getUninstallAppIntent(packageName, true));
        }
    }

    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
        if (!isSpace(packageName)) {
            activity.startActivityForResult(getUninstallAppIntent(packageName), requestCode);
        }
    }

    public static boolean isAppInstalled(String pkgName) {
        if (pkgName != null) {
            try {
                return Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0) != null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new NullPointerException("Argument 'pkgName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static boolean isAppRoot() {
        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
        if (result.result == 0) {
            return true;
        }
        if (result.errorMsg == null) {
            return false;
        }
        Log.d("AppUtils", "isAppRoot() called" + result.errorMsg);
        return false;
    }

    public static boolean isAppDebug() {
        return isAppDebug(Utils.getApp().getPackageName());
    }

    public static boolean isAppDebug(String packageName) {
        if (isSpace(packageName)) {
            return false;
        }
        try {
            ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(packageName, 0);
            if (ai == null || (ai.flags & 2) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAppSystem() {
        return isAppSystem(Utils.getApp().getPackageName());
    }

    public static boolean isAppSystem(String packageName) {
        if (isSpace(packageName)) {
            return false;
        }
        try {
            ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(packageName, 0);
            if (ai == null || (ai.flags & 1) == 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAppForeground() {
        return Utils.isAppForeground();
    }

    public static boolean isAppForeground(String packageName) {
        if (packageName != null) {
            return !isSpace(packageName) && packageName.equals(getForegroundProcessName());
        }
        throw new NullPointerException("Argument 'packageName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isAppRunning(String pkgName) {
        if (pkgName != null) {
            try {
                ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0);
                if (ai == null) {
                    return false;
                }
                int uid = ai.uid;
                ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
                if (am != null) {
                    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
                    if (taskInfo != null && taskInfo.size() > 0) {
                        for (ActivityManager.RunningTaskInfo aInfo : taskInfo) {
                            if (pkgName.equals(aInfo.baseActivity.getPackageName())) {
                                return true;
                            }
                        }
                    }
                    List<ActivityManager.RunningServiceInfo> serviceInfo = am.getRunningServices(Integer.MAX_VALUE);
                    if (serviceInfo != null && serviceInfo.size() > 0) {
                        for (ActivityManager.RunningServiceInfo aInfo2 : serviceInfo) {
                            if (uid == aInfo2.uid) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new NullPointerException("Argument 'pkgName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static void launchApp(String packageName) {
        if (!isSpace(packageName)) {
            Intent launchAppIntent = getLaunchAppIntent(packageName, true);
            if (launchAppIntent == null) {
                Log.e("AppUtils", "Didn't exist launcher activity.");
            } else {
                Utils.getApp().startActivity(launchAppIntent);
            }
        }
    }

    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (!isSpace(packageName)) {
            Intent launchAppIntent = getLaunchAppIntent(packageName);
            if (launchAppIntent == null) {
                Log.e("AppUtils", "Didn't exist launcher activity.");
            } else {
                activity.startActivityForResult(launchAppIntent, requestCode);
            }
        }
    }

    public static void relaunchApp() {
        relaunchApp(false);
    }

    public static void relaunchApp(boolean isKillProcess) {
        Intent intent = getLaunchAppIntent(Utils.getApp().getPackageName(), true);
        if (intent == null) {
            Log.e("AppUtils", "Didn't exist launcher activity.");
            return;
        }
        intent.addFlags(335577088);
        Utils.getApp().startActivity(intent);
        if (isKillProcess) {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
    }

    public static void launchAppDetailsSettings() {
        launchAppDetailsSettings(Utils.getApp().getPackageName());
    }

    public static void launchAppDetailsSettings(String packageName) {
        if (!isSpace(packageName)) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + packageName));
            Utils.getApp().startActivity(intent.addFlags(C.ENCODING_PCM_MU_LAW));
        }
    }

    public static void exitApp() {
        List<Activity> activityList = Utils.getActivityList();
        for (int i = activityList.size() - 1; i >= 0; i--) {
            activityList.get(i).finish();
        }
        System.exit(0);
    }

    public static Drawable getAppIcon() {
        return getAppIcon(Utils.getApp().getPackageName());
    }

    public static Drawable getAppIcon(String packageName) {
        Drawable drawable = null;
        if (isSpace(packageName)) {
            return null;
        }
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi != null) {
                drawable = pi.applicationInfo.loadIcon(pm);
            }
            return drawable;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAppIconId() {
        return getAppIconId(Utils.getApp().getPackageName());
    }

    public static int getAppIconId(String packageName) {
        int i = 0;
        if (isSpace(packageName)) {
            return 0;
        }
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(packageName, 0);
            if (pi != null) {
                i = pi.applicationInfo.icon;
            }
            return i;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getAppPackageName() {
        return Utils.getApp().getPackageName();
    }

    public static String getAppName() {
        return getAppName(Utils.getApp().getPackageName());
    }

    public static String getAppName(String packageName) {
        if (isSpace(packageName)) {
            return "";
        }
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            if (pi == null) {
                return null;
            }
            return pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAppPath() {
        return getAppPath(Utils.getApp().getPackageName());
    }

    public static String getAppPath(String packageName) {
        if (isSpace(packageName)) {
            return "";
        }
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(packageName, 0);
            if (pi == null) {
                return null;
            }
            return pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getAppVersionName() {
        return getAppVersionName(Utils.getApp().getPackageName());
    }

    public static String getAppVersionName(String packageName) {
        if (isSpace(packageName)) {
            return "";
        }
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(packageName, 0);
            if (pi == null) {
                return null;
            }
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getAppVersionCode() {
        return getAppVersionCode(Utils.getApp().getPackageName());
    }

    public static int getAppVersionCode(String packageName) {
        if (isSpace(packageName)) {
            return -1;
        }
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(packageName, 0);
            if (pi == null) {
                return -1;
            }
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Signature[] getAppSignature() {
        return getAppSignature(Utils.getApp().getPackageName());
    }

    public static Signature[] getAppSignature(String packageName) {
        if (isSpace(packageName)) {
            return null;
        }
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(packageName, 64);
            if (pi == null) {
                return null;
            }
            return pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppSignatureSHA1() {
        return getAppSignatureSHA1(Utils.getApp().getPackageName());
    }

    public static String getAppSignatureSHA1(String packageName) {
        return getAppSignatureHash(packageName, "SHA1");
    }

    public static String getAppSignatureSHA256() {
        return getAppSignatureSHA256(Utils.getApp().getPackageName());
    }

    public static String getAppSignatureSHA256(String packageName) {
        return getAppSignatureHash(packageName, "SHA256");
    }

    public static String getAppSignatureMD5() {
        return getAppSignatureMD5(Utils.getApp().getPackageName());
    }

    public static String getAppSignatureMD5(String packageName) {
        return getAppSignatureHash(packageName, "MD5");
    }

    public static int getAppUid() {
        return getAppUid(Utils.getApp().getPackageName());
    }

    public static int getAppUid(String pkgName) {
        try {
            ApplicationInfo ai = Utils.getApp().getPackageManager().getApplicationInfo(pkgName, 0);
            if (ai != null) {
                return ai.uid;
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static String getAppSignatureHash(String packageName, String algorithm) {
        Signature[] signature;
        if (!isSpace(packageName) && (signature = getAppSignature(packageName)) != null && signature.length > 0) {
            return bytes2HexString(hashTemplate(signature[0].toByteArray(), algorithm)).replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
        }
        return "";
    }

    public static AppInfo getAppInfo() {
        return getAppInfo(Utils.getApp().getPackageName());
    }

    public static AppInfo getAppInfo(String packageName) {
        try {
            PackageManager pm = Utils.getApp().getPackageManager();
            if (pm == null) {
                return null;
            }
            return getBean(pm, pm.getPackageInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<AppInfo> getAppsInfo() {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = Utils.getApp().getPackageManager();
        if (pm == null) {
            return list;
        }
        for (PackageInfo pi : pm.getInstalledPackages(0)) {
            AppInfo ai = getBean(pm, pi);
            if (ai != null) {
                list.add(ai);
            }
        }
        return list;
    }

    public static AppInfo getApkInfo(File apkFile) {
        if (apkFile == null || !apkFile.isFile() || !apkFile.exists()) {
            return null;
        }
        return getApkInfo(apkFile.getAbsolutePath());
    }

    public static AppInfo getApkInfo(String apkFilePath) {
        PackageManager pm;
        PackageInfo pi;
        if (isSpace(apkFilePath) || (pm = Utils.getApp().getPackageManager()) == null || (pi = pm.getPackageArchiveInfo(apkFilePath, 0)) == null) {
            return null;
        }
        ApplicationInfo appInfo = pi.applicationInfo;
        appInfo.sourceDir = apkFilePath;
        appInfo.publicSourceDir = apkFilePath;
        return getBean(pm, pi);
    }

    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
        PackageManager packageManager = pm;
        PackageInfo packageInfo = pi;
        if (packageInfo == null) {
            return null;
        }
        ApplicationInfo ai = packageInfo.applicationInfo;
        String packageName = packageInfo.packageName;
        String name = ai.loadLabel(packageManager).toString();
        Drawable icon = ai.loadIcon(packageManager);
        String packagePath = ai.sourceDir;
        String versionName = packageInfo.versionName;
        int versionCode = packageInfo.versionCode;
        int i = versionCode;
        return new AppInfo(packageName, name, icon, packagePath, versionName, versionCode, (ai.flags & 1) != 0);
    }

    public static class AppInfo {
        private Drawable icon;
        private boolean isSystem;
        private String name;
        private String packageName;
        private String packagePath;
        private int versionCode;
        private String versionName;

        public Drawable getIcon() {
            return this.icon;
        }

        public void setIcon(Drawable icon2) {
            this.icon = icon2;
        }

        public boolean isSystem() {
            return this.isSystem;
        }

        public void setSystem(boolean isSystem2) {
            this.isSystem = isSystem2;
        }

        public String getPackageName() {
            return this.packageName;
        }

        public void setPackageName(String packageName2) {
            this.packageName = packageName2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }

        public String getPackagePath() {
            return this.packagePath;
        }

        public void setPackagePath(String packagePath2) {
            this.packagePath = packagePath2;
        }

        public int getVersionCode() {
            return this.versionCode;
        }

        public void setVersionCode(int versionCode2) {
            this.versionCode = versionCode2;
        }

        public String getVersionName() {
            return this.versionName;
        }

        public void setVersionName(String versionName2) {
            this.versionName = versionName2;
        }

        public AppInfo(String packageName2, String name2, Drawable icon2, String packagePath2, String versionName2, int versionCode2, boolean isSystem2) {
            setName(name2);
            setIcon(icon2);
            setPackageName(packageName2);
            setPackagePath(packagePath2);
            setVersionName(versionName2);
            setVersionCode(versionCode2);
            setSystem(isSystem2);
        }

        public String toString() {
            return "{\n    pkg name: " + getPackageName() + "\n    app icon: " + getIcon() + "\n    app name: " + getName() + "\n    app path: " + getPackagePath() + "\n    app v name: " + getVersionName() + "\n    app v code: " + getVersionCode() + "\n    is system: " + isSystem() + "\n}";
        }
    }

    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static byte[] hashTemplate(byte[] data, String algorithm) {
        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytes2HexString(byte[] bytes) {
        int len;
        if (bytes == null || (len = bytes.length) <= 0) {
            return "";
        }
        char[] ret = new char[(len << 1)];
        int j = 0;
        for (int i = 0; i < len; i++) {
            int j2 = j + 1;
            char[] cArr = HEX_DIGITS;
            ret[j] = cArr[(bytes[i] >> 4) & 15];
            j = j2 + 1;
            ret[j2] = cArr[bytes[i] & 15];
        }
        return new String(ret);
    }

    private static Intent getInstallAppIntent(File file) {
        return getInstallAppIntent(file, false);
    }

    private static Intent getInstallAppIntent(File file, boolean isNewTask) {
        Uri data;
        Intent intent = new Intent("android.intent.action.VIEW");
        if (Build.VERSION.SDK_INT < 24) {
            data = Uri.fromFile(file);
        } else {
            Uri data2 = FileProvider.getUriForFile(Utils.getApp(), Utils.getApp().getPackageName() + ".utilcode.provider", file);
            intent.setFlags(1);
            data = data2;
        }
        Utils.getApp().grantUriPermission(Utils.getApp().getPackageName(), data, 1);
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        return isNewTask ? intent.addFlags(C.ENCODING_PCM_MU_LAW) : intent;
    }

    private static Intent getUninstallAppIntent(String packageName) {
        return getUninstallAppIntent(packageName, false);
    }

    private static Intent getUninstallAppIntent(String packageName, boolean isNewTask) {
        Intent intent = new Intent("android.intent.action.DELETE");
        intent.setData(Uri.parse("package:" + packageName));
        return isNewTask ? intent.addFlags(C.ENCODING_PCM_MU_LAW) : intent;
    }

    private static Intent getLaunchAppIntent(String packageName) {
        return getLaunchAppIntent(packageName, false);
    }

    private static Intent getLaunchAppIntent(String packageName, boolean isNewTask) {
        String launcherActivity = getLauncherActivity(packageName);
        if (launcherActivity.isEmpty()) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName(packageName, launcherActivity));
        return isNewTask ? intent.addFlags(C.ENCODING_PCM_MU_LAW) : intent;
    }

    private static String getLauncherActivity(String pkg) {
        if (pkg != null) {
            Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(pkg);
            List<ResolveInfo> info = Utils.getApp().getPackageManager().queryIntentActivities(intent, 0);
            int size = info.size();
            if (size == 0) {
                return "";
            }
            for (int i = 0; i < size; i++) {
                ResolveInfo ri = info.get(i);
                if (ri.activityInfo.processName.equals(pkg)) {
                    return ri.activityInfo.name;
                }
            }
            return info.get(0).activityInfo.name;
        }
        throw new NullPointerException("Argument 'pkg' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static String getForegroundProcessName() {
        List<ActivityManager.RunningAppProcessInfo> pInfo = ((ActivityManager) Utils.getApp().getSystemService("activity")).getRunningAppProcesses();
        if (pInfo != null && pInfo.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo aInfo : pInfo) {
                if (aInfo.importance == 100) {
                    return aInfo.processName;
                }
            }
        }
        if (Build.VERSION.SDK_INT > 21) {
            PackageManager pm = Utils.getApp().getPackageManager();
            Intent intent = new Intent("android.settings.USAGE_ACCESS_SETTINGS");
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 65536);
            Log.i("ProcessUtils", list.toString());
            if (list.size() <= 0) {
                Log.i("ProcessUtils", "getForegroundProcessName: noun of access to usage information.");
                return "";
            }
            try {
                ApplicationInfo info = pm.getApplicationInfo(Utils.getApp().getPackageName(), 0);
                AppOpsManager aom = (AppOpsManager) Utils.getApp().getSystemService("appops");
                if (aom.checkOpNoThrow("android:get_usage_stats", info.uid, info.packageName) != 0) {
                    intent.addFlags(C.ENCODING_PCM_MU_LAW);
                    Utils.getApp().startActivity(intent);
                }
                if (aom.checkOpNoThrow("android:get_usage_stats", info.uid, info.packageName) != 0) {
                    Log.i("ProcessUtils", "getForegroundProcessName: refuse to device usage stats.");
                    return "";
                }
                UsageStatsManager usageStatsManager = (UsageStatsManager) Utils.getApp().getSystemService("usagestats");
                List<UsageStats> usageStatsList = null;
                if (usageStatsManager != null) {
                    long endTime = System.currentTimeMillis();
                    usageStatsList = usageStatsManager.queryUsageStats(4, endTime - 604800000, endTime);
                }
                if (usageStatsList != null) {
                    if (!usageStatsList.isEmpty()) {
                        UsageStats recentStats = null;
                        for (UsageStats usageStats : usageStatsList) {
                            if (recentStats == null || usageStats.getLastTimeUsed() > recentStats.getLastTimeUsed()) {
                                recentStats = usageStats;
                            }
                        }
                        if (recentStats == null) {
                            return null;
                        }
                        return recentStats.getPackageName();
                    }
                }
                return null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
