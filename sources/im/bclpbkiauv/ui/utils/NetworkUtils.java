package im.bclpbkiauv.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context c) {
        NetworkInfo[] networkInfo;
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getApplicationContext().getSystemService("connectivity");
        if (!(connectivityManager == null || (networkInfo = connectivityManager.getAllNetworkInfo()) == null || networkInfo.length <= 0)) {
            for (NetworkInfo aNetworkInfo : networkInfo) {
                if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService("connectivity");
        return (mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || ((TelephonyManager) context.getSystemService("phone")).getNetworkType() == 3;
    }

    public static boolean isMobileConnected(Context context) {
        NetworkInfo mMobileNetworkInfo;
        if (context == null || (mMobileNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(0)) == null) {
            return false;
        }
        return mMobileNetworkInfo.isAvailable();
    }

    public static boolean hasSimCard(Context context) {
        int simState = ((TelephonyManager) context.getSystemService("phone")).getSimState();
        if (simState == 0 || simState == 1) {
            return false;
        }
        return true;
    }

    public static boolean is3rd(Context context) {
        NetworkInfo networkINfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkINfo == null || networkINfo.getType() != 0) {
            return false;
        }
        return true;
    }

    public static boolean isWifi(Context context) {
        NetworkInfo networkINfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkINfo == null || networkINfo.getType() != 1) {
            return false;
        }
        return true;
    }
}
