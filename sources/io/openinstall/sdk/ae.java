package io.openinstall.sdk;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Collections;

public class ae {
    private final Context a;
    private String b;
    private String c;

    public ae(Context context) {
        this.a = context;
    }

    private boolean a(String str) {
        return TextUtils.isEmpty(str) || str.equals(ca.j) || str.equals(ca.k);
    }

    private boolean b(String str) {
        return TextUtils.isEmpty(str) || str.equals(ca.l);
    }

    public String a() {
        byte[] hardwareAddress;
        WifiInfo connectionInfo;
        String str = this.b;
        if (str != null) {
            return str;
        }
        String str2 = null;
        try {
            WifiManager wifiManager = (WifiManager) this.a.getSystemService(ca.f);
            if (!(wifiManager == null || (connectionInfo = wifiManager.getConnectionInfo()) == null)) {
                str2 = connectionInfo.getMacAddress();
            }
        } catch (SecurityException e) {
        }
        if (!b(str2)) {
            this.b = str2;
            return str2;
        }
        try {
            str2 = new BufferedReader(new FileReader(new File(ca.h))).readLine();
        } catch (IOException e2) {
        }
        if (!b(str2)) {
            this.b = str2;
            return str2;
        }
        try {
            for (T t : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ca.g.equalsIgnoreCase(t.getName()) && (hardwareAddress = t.getHardwareAddress()) != null) {
                    StringBuilder sb = new StringBuilder();
                    int length = hardwareAddress.length;
                    for (int i = 0; i < length; i++) {
                        sb.append(String.format("%02X:", new Object[]{Byte.valueOf(hardwareAddress[i])}));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    str2 = sb.toString();
                }
            }
        } catch (Throwable th) {
        }
        if (b(str2)) {
            str2 = "";
        }
        this.b = str2;
        return this.b;
    }

    public String b() {
        String str = this.c;
        if (str != null) {
            return str;
        }
        String str2 = null;
        if (cd.a(this.a)) {
            TelephonyManager telephonyManager = (TelephonyManager) this.a.getSystemService("phone");
            try {
                str2 = Build.VERSION.SDK_INT >= 26 ? telephonyManager.getImei() : telephonyManager.getDeviceId();
            } catch (SecurityException e) {
            }
        }
        if (a(str2)) {
            str2 = "";
        }
        this.c = str2;
        return this.c;
    }
}
