package com.baidu.mapapi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import com.baidu.mapsdkplatform.comapi.util.SysUpdateObservable;

public class NetworkUtil {
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            return ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCurrentNetMode(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        int i = 1;
        if (activeNetworkInfo != null) {
            if (activeNetworkInfo.getType() != 1) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (telephonyManager != null) {
                    switch (telephonyManager.getNetworkType()) {
                        case 1:
                        case 2:
                            i = 6;
                            break;
                        case 3:
                        case 9:
                        case 10:
                        case 15:
                            i = 9;
                            break;
                        case 4:
                            i = 5;
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 12:
                            i = 7;
                            break;
                        case 8:
                            i = 8;
                            break;
                        case 11:
                            i = 2;
                            break;
                        case 13:
                            i = 4;
                            break;
                        case 14:
                            i = 10;
                            break;
                    }
                } else {
                    return Integer.toString(0);
                }
            }
            return Integer.toString(i);
        }
        i = 0;
        return Integer.toString(i);
    }

    public static boolean initConnectState() {
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            if (isWifiConnected(context)) {
                return true;
            }
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null) {
            return false;
        }
        try {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && 1 == activeNetworkInfo.getType() && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000e, code lost:
        if (r3.isConnected() != false) goto L_0x0017;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isWifiConnected(android.net.NetworkInfo r3) {
        /*
            r0 = 1
            r1 = 0
            if (r3 == 0) goto L_0x0016
            int r2 = r3.getType()     // Catch:{ Exception -> 0x0011 }
            if (r0 != r2) goto L_0x0016
            boolean r3 = r3.isConnected()     // Catch:{ Exception -> 0x0011 }
            if (r3 == 0) goto L_0x0016
            goto L_0x0017
        L_0x0011:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x0018
        L_0x0016:
            r0 = 0
        L_0x0017:
            r1 = r0
        L_0x0018:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapapi.NetworkUtil.isWifiConnected(android.net.NetworkInfo):boolean");
    }

    public static void updateNetworkProxy(Context context) {
        SysUpdateObservable.getInstance().updateNetworkProxy(context);
    }
}
