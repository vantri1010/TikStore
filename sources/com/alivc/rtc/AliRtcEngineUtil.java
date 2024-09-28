package com.alivc.rtc;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.aliyun.security.yunceng.android.sdk.traceroute.d;

public class AliRtcEngineUtil {
    public static final int NETWORK_CLASS_2_G = 2;
    public static final int NETWORK_CLASS_3_G = 3;
    public static final int NETWORK_CLASS_4_G = 4;
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1;

    public static float getRunningAppProcessInfo(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService("activity");
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mActivityManager.getRunningAppProcesses()) {
            int pid = runningAppProcessInfo.pid;
            int i = runningAppProcessInfo.uid;
            String str = runningAppProcessInfo.processName;
            int[] pids = {pid};
            if (pid == Process.myPid()) {
                return (float) mActivityManager.getProcessMemoryInfo(pids)[0].getTotalPss();
            }
        }
        return 0.0f;
    }

    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static String getNetWorkClass(Context context) {
        switch (((TelephonyManager) context.getSystemService("phone")).getNetworkType()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return "3G";
            case 13:
                return "4G";
            default:
                return d.a;
        }
    }

    public static String getNetWorkStatus(Context context) {
        NetworkInfo networkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null || (networkInfo = connectivityManager.getActiveNetworkInfo()) == null || !networkInfo.isConnected()) {
            return d.a;
        }
        int type = networkInfo.getType();
        if (type == 1) {
            return "WiFi";
        }
        if (type == 0) {
            return getNetWorkClass(context);
        }
        return d.a;
    }

    public static String getOperators(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return d.a;
        }
        String imsi = telephonyManager.getSimOperator();
        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
            return "China Mobile";
        }
        if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
            return "China Unicom";
        }
        if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
            return "China Telecom";
        }
        return d.a;
    }
}
