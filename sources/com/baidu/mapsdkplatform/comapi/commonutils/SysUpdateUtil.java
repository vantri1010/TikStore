package com.baidu.mapsdkplatform.comapi.commonutils;

import android.content.Context;
import com.baidu.mapapi.NetworkUtil;
import com.baidu.mapsdkplatform.comapi.util.SysUpdateObserver;
import com.baidu.mapsdkplatform.comjni.map.commonmemcache.a;

public class SysUpdateUtil implements SysUpdateObserver {
    static a a = new a();
    public static boolean b = false;
    public static String c = "";
    public static int d = 0;

    public void init() {
        a aVar = a;
        if (aVar != null) {
            aVar.a();
            a.b();
        }
    }

    public void updateNetworkInfo(Context context) {
        NetworkUtil.updateNetworkProxy(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00c2, code lost:
        if ("10.0.0.200".equals(r9.trim()) != false) goto L_0x00c4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateNetworkProxy(android.content.Context r9) {
        /*
            r8 = this;
            android.net.NetworkInfo r9 = com.baidu.mapapi.NetworkUtil.getActiveNetworkInfo(r9)
            if (r9 == 0) goto L_0x00d8
            boolean r0 = r9.isAvailable()
            if (r0 == 0) goto L_0x00d8
            java.lang.String r0 = r9.getTypeName()
            java.lang.String r0 = r0.toLowerCase()
            java.lang.String r1 = "wifi"
            boolean r2 = r0.equals(r1)
            r3 = 0
            r4 = 0
            if (r2 == 0) goto L_0x002a
            boolean r2 = r9.isConnected()
            if (r2 == 0) goto L_0x002a
            com.baidu.mapsdkplatform.comjni.engine.AppEngine.SetProxyInfo(r3, r4)
            b = r4
            return
        L_0x002a:
            java.lang.String r2 = "mobile"
            boolean r2 = r0.equals(r2)
            if (r2 != 0) goto L_0x003e
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x00d8
            boolean r0 = com.baidu.mapapi.NetworkUtil.isWifiConnected((android.net.NetworkInfo) r9)
            if (r0 != 0) goto L_0x00d8
        L_0x003e:
            java.lang.String r9 = r9.getExtraInfo()
            b = r4
            java.lang.String r0 = "10.0.0.200"
            r1 = 80
            java.lang.String r2 = "10.0.0.172"
            r5 = 1
            if (r9 == 0) goto L_0x0099
            java.lang.String r9 = r9.toLowerCase()
            java.lang.String r6 = "cmwap"
            boolean r6 = r9.startsWith(r6)
            if (r6 != 0) goto L_0x0096
            java.lang.String r6 = "uniwap"
            boolean r6 = r9.startsWith(r6)
            if (r6 != 0) goto L_0x0096
            java.lang.String r6 = "3gwap"
            boolean r6 = r9.startsWith(r6)
            if (r6 == 0) goto L_0x006a
            goto L_0x0096
        L_0x006a:
            java.lang.String r2 = "ctwap"
            boolean r2 = r9.startsWith(r2)
            if (r2 == 0) goto L_0x0073
            goto L_0x00c4
        L_0x0073:
            java.lang.String r0 = "cmnet"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0093
            java.lang.String r0 = "uninet"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0093
            java.lang.String r0 = "ctnet"
            boolean r0 = r9.startsWith(r0)
            if (r0 != 0) goto L_0x0093
            java.lang.String r0 = "3gnet"
            boolean r9 = r9.startsWith(r0)
            if (r9 == 0) goto L_0x00c9
        L_0x0093:
            b = r4
            goto L_0x00c9
        L_0x0096:
            c = r2
            goto L_0x00c6
        L_0x0099:
            java.lang.String r9 = android.net.Proxy.getDefaultHost()
            int r6 = android.net.Proxy.getDefaultPort()
            if (r9 == 0) goto L_0x00c9
            int r7 = r9.length()
            if (r7 <= 0) goto L_0x00c9
            java.lang.String r7 = r9.trim()
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x00ba
            c = r2
            d = r6
        L_0x00b7:
            b = r5
            goto L_0x00c9
        L_0x00ba:
            java.lang.String r9 = r9.trim()
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x00c9
        L_0x00c4:
            c = r0
        L_0x00c6:
            d = r1
            goto L_0x00b7
        L_0x00c9:
            boolean r9 = b
            if (r9 != r5) goto L_0x00d5
            java.lang.String r9 = c
            int r0 = d
            com.baidu.mapsdkplatform.comjni.engine.AppEngine.SetProxyInfo(r9, r0)
            goto L_0x00d8
        L_0x00d5:
            com.baidu.mapsdkplatform.comjni.engine.AppEngine.SetProxyInfo(r3, r4)
        L_0x00d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.commonutils.SysUpdateUtil.updateNetworkProxy(android.content.Context):void");
    }

    public void updatePhoneInfo() {
        a aVar = a;
        if (aVar != null) {
            aVar.b();
        }
    }
}
