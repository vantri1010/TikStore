package com.qiniu.android.dns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import com.qiniu.android.dns.NetworkInfo;
import java.util.Locale;

public final class NetworkReceiver extends BroadcastReceiver {
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private static DnsManager mdnsManager;

    public static NetworkInfo createNetInfo(NetworkInfo info, Context context) {
        NetworkInfo.NetSatus net;
        String netMode;
        if (info == null) {
            return NetworkInfo.noNetwork;
        }
        int provider = 0;
        if (info.getType() == 1) {
            net = NetworkInfo.NetSatus.WIFI;
            provider = 0;
        } else {
            NetworkInfo.NetSatus net2 = NetworkInfo.NetSatus.MOBILE;
            Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, (String[]) null, (String) null, (String[]) null, (String) null);
            if (c != null) {
                c.moveToFirst();
                String user = c.getString(c.getColumnIndex("user"));
                if (!TextUtils.isEmpty(user) && (user.startsWith("ctwap") || user.startsWith("ctnet"))) {
                    provider = 1;
                }
            }
            c.close();
            if (!(provider == 1 || (netMode = info.getExtraInfo()) == null)) {
                String netMode2 = netMode.toLowerCase(Locale.getDefault());
                if (netMode2.equals("cmwap") || netMode2.equals("cmnet")) {
                    provider = 3;
                    net = net2;
                } else if (netMode2.equals("3gnet") || netMode2.equals("uninet") || netMode2.equals("3gwap") || netMode2.equals("uniwap")) {
                    provider = 2;
                    net = net2;
                }
            }
            net = net2;
        }
        return new NetworkInfo(net, provider);
    }

    public static void setDnsManager(DnsManager dnsManager) {
        mdnsManager = dnsManager;
    }

    public void onReceive(Context context, Intent intent) {
        if (mdnsManager != null) {
            mdnsManager.onNetworkChange(createNetInfo(((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo(), context));
        }
    }
}
