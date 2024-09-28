package com.blankj.utilcode.util;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.ServiceConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ServiceUtils {
    private ServiceUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Set getAllRunningServices() {
        List<ActivityManager.RunningServiceInfo> info = ((ActivityManager) Utils.getApp().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        Set<String> names = new HashSet<>();
        if (info == null || info.size() == 0) {
            return null;
        }
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            names.add(aInfo.service.getClassName());
        }
        return names;
    }

    public static void startService(String className) {
        try {
            startService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startService(Class<?> cls) {
        Utils.getApp().startService(new Intent(Utils.getApp(), cls));
    }

    public static boolean stopService(String className) {
        try {
            return stopService(Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean stopService(Class<?> cls) {
        return Utils.getApp().stopService(new Intent(Utils.getApp(), cls));
    }

    public static void bindService(String className, ServiceConnection conn, int flags) {
        try {
            bindService(Class.forName(className), conn, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bindService(Class<?> cls, ServiceConnection conn, int flags) {
        Utils.getApp().bindService(new Intent(Utils.getApp(), cls), conn, flags);
    }

    public static void unbindService(ServiceConnection conn) {
        Utils.getApp().unbindService(conn);
    }

    public static boolean isServiceRunning(Class<?> cls) {
        return isServiceRunning(cls.getName());
    }

    public static boolean isServiceRunning(String className) {
        List<ActivityManager.RunningServiceInfo> info = ((ActivityManager) Utils.getApp().getSystemService("activity")).getRunningServices(Integer.MAX_VALUE);
        if (info == null || info.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo aInfo : info) {
            if (className.equals(aInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
