package im.bclpbkiauv.ui.hui.visualcall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public class CallNetWorkReceiver extends BroadcastReceiver {
    private NetworkInfo dataNetworkInfo;
    private NetWorkStateCallBack mCallBack;
    private NetworkInfo wifiNetworkInfo;

    public interface NetWorkStateCallBack {
        void onNetWorkConnected();

        void onNetWorkDisconnected();
    }

    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo;
        if (Build.VERSION.SDK_INT < 23) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService("connectivity");
            this.wifiNetworkInfo = connMgr.getNetworkInfo(1);
            this.dataNetworkInfo = connMgr.getNetworkInfo(0);
        } else {
            ConnectivityManager connMgr2 = (ConnectivityManager) context.getSystemService("connectivity");
            Network[] networks = connMgr2.getAllNetworks();
            for (Network networkInfo2 : networks) {
                NetworkInfo networkInfo3 = connMgr2.getNetworkInfo(networkInfo2);
                if (networkInfo3.getType() == 1) {
                    this.wifiNetworkInfo = networkInfo3;
                } else if (networkInfo3.getType() == 0) {
                    this.dataNetworkInfo = networkInfo3;
                }
            }
        }
        NetworkInfo networkInfo4 = this.wifiNetworkInfo;
        if ((networkInfo4 == null || !networkInfo4.isConnected()) && ((networkInfo = this.dataNetworkInfo) == null || !networkInfo.isConnected())) {
            NetWorkStateCallBack netWorkStateCallBack = this.mCallBack;
            if (netWorkStateCallBack != null) {
                netWorkStateCallBack.onNetWorkDisconnected();
                return;
            }
            return;
        }
        NetWorkStateCallBack netWorkStateCallBack2 = this.mCallBack;
        if (netWorkStateCallBack2 != null) {
            netWorkStateCallBack2.onNetWorkConnected();
        }
    }

    public void setCallBack(NetWorkStateCallBack mCallBack2) {
        this.mCallBack = mCallBack2;
    }
}
