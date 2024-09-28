package com.bjz.comm.net.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;
import com.bjz.comm.net.BuildVars;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkConnectChangedReceiver.class.getSimpleName();
    private OnNetWorkStateListener listener;

    public interface OnNetWorkStateListener {
        void onNetWorkStateChange(boolean z);
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (((action.hashCode() == -1172645946 && action.equals("android.net.conn.CONNECTIVITY_CHANGE")) ? (char) 0 : 65535) == 0) {
            if (BuildVars.DEBUG_VERSION) {
                Log.e(TAG, "action: ConnectivityManager.CONNECTIVITY_ACTION");
            }
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (info == null) {
                return;
            }
            if (NetworkInfo.State.CONNECTED != info.getState() || !info.isAvailable()) {
                if (BuildVars.DEBUG_VERSION) {
                    Log.e(TAG, "网络断开");
                }
                OnNetWorkStateListener onNetWorkStateListener = this.listener;
                if (onNetWorkStateListener != null) {
                    onNetWorkStateListener.onNetWorkStateChange(false);
                    return;
                }
                return;
            }
            if (BuildVars.DEBUG_VERSION) {
                Log.e(TAG, "网络连接上");
            }
            OnNetWorkStateListener onNetWorkStateListener2 = this.listener;
            if (onNetWorkStateListener2 != null) {
                onNetWorkStateListener2.onNetWorkStateChange(true);
            }
        }
    }

    public void setNetWorkStateListener(OnNetWorkStateListener listener2) {
        this.listener = listener2;
    }
}
