package com.blankj.utilcode.util;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ProcessUtils {
    private ProcessUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getForegroundProcessName() {
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
                return "";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Set<String> getAllBackgroundProcesses() {
        List<ActivityManager.RunningAppProcessInfo> info = ((ActivityManager) Utils.getApp().getSystemService("activity")).getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        if (info != null) {
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                Collections.addAll(set, aInfo.pkgList);
            }
        }
        return set;
    }

    public static Set<String> killAllBackgroundProcesses() {
        ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        Set<String> set = new HashSet<>();
        if (info == null) {
            return set;
        }
        Iterator<ActivityManager.RunningAppProcessInfo> it = info.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            for (String pkg : it.next().pkgList) {
                am.killBackgroundProcesses(pkg);
                set.add(pkg);
            }
        }
        for (ActivityManager.RunningAppProcessInfo aInfo : am.getRunningAppProcesses()) {
            for (String pkg2 : aInfo.pkgList) {
                set.remove(pkg2);
            }
        }
        return set;
    }

    public static boolean killBackgroundProcesses(String packageName) {
        if (packageName != null) {
            ActivityManager am = (ActivityManager) Utils.getApp().getSystemService("activity");
            List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
            if (info == null || info.size() == 0) {
                return true;
            }
            for (ActivityManager.RunningAppProcessInfo aInfo : info) {
                if (Arrays.asList(aInfo.pkgList).contains(packageName)) {
                    am.killBackgroundProcesses(packageName);
                }
            }
            List<ActivityManager.RunningAppProcessInfo> info2 = am.getRunningAppProcesses();
            if (info2 == null || info2.size() == 0) {
                return true;
            }
            for (ActivityManager.RunningAppProcessInfo aInfo2 : info2) {
                if (Arrays.asList(aInfo2.pkgList).contains(packageName)) {
                    return false;
                }
            }
            return true;
        }
        throw new NullPointerException("Argument 'packageName' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isMainProcess() {
        return Utils.getApp().getPackageName().equals(Utils.getCurrentProcessName());
    }

    public static String getCurrentProcessName() {
        return Utils.getCurrentProcessName();
    }
}
