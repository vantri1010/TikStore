package com.baidu.location.e;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import com.baidu.location.f;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.king.zxing.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.List;

public class j {
    public static long a = 0;
    private static j b = null;
    private WifiManager c = null;
    private a d = null;
    private i e = null;
    private long f = 0;
    private long g = 0;
    private boolean h = false;
    /* access modifiers changed from: private */
    public Handler i = new Handler();
    /* access modifiers changed from: private */
    public boolean j = false;
    private long k = 0;
    private long l = 0;

    private class a extends BroadcastReceiver {
        private long b;
        private boolean c;

        private a() {
            this.b = 0;
            this.c = false;
        }

        public void onReceive(Context context, Intent intent) {
            if (context != null) {
                String action = intent.getAction();
                if (action.equals("android.net.wifi.SCAN_RESULTS")) {
                    j.a = System.currentTimeMillis() / 1000;
                    j.this.i.post(new k(this, intent.getBooleanExtra("resultsUpdated", true)));
                } else if (action.equals("android.net.wifi.STATE_CHANGE") && ((NetworkInfo) intent.getParcelableExtra("networkInfo")).getState().equals(NetworkInfo.State.CONNECTED) && System.currentTimeMillis() - this.b >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                    this.b = System.currentTimeMillis();
                    if (!this.c) {
                        this.c = true;
                    }
                }
            }
        }
    }

    private j() {
    }

    public static synchronized j a() {
        j jVar;
        synchronized (j.class) {
            if (b == null) {
                b = new j();
            }
            jVar = b;
        }
        return jVar;
    }

    private String a(long j2) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.valueOf((int) (j2 & 255)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int) ((j2 >> 8) & 255)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int) ((j2 >> 16) & 255)));
        stringBuffer.append('.');
        stringBuffer.append(String.valueOf((int) ((j2 >> 24) & 255)));
        return stringBuffer.toString();
    }

    public static boolean a(i iVar, i iVar2) {
        boolean a2 = a(iVar, iVar2, 0.7f);
        long currentTimeMillis = System.currentTimeMillis() - com.baidu.location.b.a.c;
        if (currentTimeMillis <= 0 || currentTimeMillis >= 30000 || !a2 || iVar2.g() - iVar.g() <= 30) {
            return a2;
        }
        return false;
    }

    public static boolean a(i iVar, i iVar2, float f2) {
        if (!(iVar == null || iVar2 == null)) {
            List<ScanResult> list = iVar.a;
            List<ScanResult> list2 = iVar2.a;
            if (list == list2) {
                return true;
            }
            if (!(list == null || list2 == null)) {
                int size = list.size();
                int size2 = list2.size();
                if (size == 0 && size2 == 0) {
                    return true;
                }
                if (!(size == 0 || size2 == 0)) {
                    int i2 = 0;
                    for (int i3 = 0; i3 < size; i3++) {
                        String str = list.get(i3) != null ? list.get(i3).BSSID : null;
                        if (str != null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= size2) {
                                    break;
                                }
                                String str2 = list2.get(i4) != null ? list2.get(i4).BSSID : null;
                                if (str2 != null && str.equals(str2)) {
                                    i2++;
                                    break;
                                }
                                i4++;
                            }
                        }
                    }
                    if (((float) i2) >= ((float) size) * f2) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean j() {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) f.getServiceContext().getSystemService("connectivity")).getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.getType() == 1;
        } catch (Throwable th) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void s() {
        WifiManager wifiManager = this.c;
        if (wifiManager != null) {
            try {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults != null) {
                    i iVar = new i(scanResults, System.currentTimeMillis());
                    i iVar2 = this.e;
                    if (iVar2 == null || !iVar.a(iVar2)) {
                        this.e = iVar;
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public void b() {
        this.k = 0;
    }

    public synchronized void c() {
        if (!this.h) {
            if (f.isServing) {
                this.c = (WifiManager) f.getServiceContext().getApplicationContext().getSystemService("wifi");
                this.d = new a();
                try {
                    f.getServiceContext().registerReceiver(this.d, new IntentFilter("android.net.wifi.SCAN_RESULTS"));
                } catch (Exception e2) {
                }
                this.h = true;
            }
        }
    }

    public List<WifiConfiguration> d() {
        try {
            if (this.c != null) {
                return this.c.getConfiguredNetworks();
            }
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public synchronized void e() {
        if (this.h) {
            try {
                f.getServiceContext().unregisterReceiver(this.d);
                a = 0;
            } catch (Exception e2) {
            }
            this.d = null;
            this.c = null;
            this.h = false;
        }
    }

    public boolean f() {
        long currentTimeMillis = System.currentTimeMillis();
        long j2 = this.g;
        if (currentTimeMillis - j2 > 0 && currentTimeMillis - j2 <= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            return false;
        }
        this.g = currentTimeMillis;
        b();
        return g();
    }

    public boolean g() {
        if (this.c == null) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j2 = this.f;
        if (currentTimeMillis - j2 > 0) {
            long j3 = this.k;
            if (currentTimeMillis - j2 <= j3 + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS || currentTimeMillis - (a * 1000) <= j3 + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                return false;
            }
            if (j() && currentTimeMillis - this.f <= this.k + OkHttpUtils.DEFAULT_MILLISECONDS) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= 28 && currentTimeMillis - this.f < 25000) {
                return false;
            }
        }
        return i();
    }

    public String h() {
        WifiManager wifiManager = this.c;
        if (wifiManager == null) {
            return "";
        }
        try {
            return (wifiManager.isWifiEnabled() || (Build.VERSION.SDK_INT > 17 && this.c.isScanAlwaysAvailable())) ? "&wifio=1" : "";
        } catch (NoSuchMethodError e2) {
            return "";
        } catch (Exception e3) {
            return "";
        }
    }

    public boolean i() {
        long currentTimeMillis = System.currentTimeMillis() - this.l;
        if (currentTimeMillis >= 0 && currentTimeMillis <= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            return false;
        }
        this.l = System.currentTimeMillis();
        try {
            if (!this.c.isWifiEnabled()) {
                if (Build.VERSION.SDK_INT <= 17 || !this.c.isScanAlwaysAvailable()) {
                    return false;
                }
            }
            this.c.startScan();
            this.f = System.currentTimeMillis();
            return true;
        } catch (NoSuchMethodError e2) {
            return false;
        } catch (Exception e3) {
            return false;
        }
    }

    public boolean k() {
        try {
            return (this.c.isWifiEnabled() || (Build.VERSION.SDK_INT > 17 && this.c.isScanAlwaysAvailable())) && !j() && new i(this.c.getScanResults(), 0).e();
        } catch (NoSuchMethodError e2) {
            return false;
        } catch (Exception e3) {
            return false;
        }
    }

    public WifiInfo l() {
        WifiManager wifiManager = this.c;
        if (wifiManager == null) {
            return null;
        }
        try {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (!(connectionInfo == null || connectionInfo.getBSSID() == null)) {
                if (connectionInfo.getRssi() > -100) {
                    String bssid = connectionInfo.getBSSID();
                    if (bssid != null) {
                        String replace = bssid.replace(LogUtils.COLON, "");
                        if ("000000000000".equals(replace) || "".equals(replace) || replace.equals("020000000000")) {
                            return null;
                        }
                    }
                    return connectionInfo;
                }
            }
            return null;
        } catch (Exception e2) {
            return null;
        } catch (Error e3) {
            return null;
        }
    }

    public String m() {
        StringBuffer stringBuffer = new StringBuffer();
        WifiInfo l2 = a().l();
        if (!(l2 == null || l2.getBSSID() == null)) {
            String replace = l2.getBSSID().replace(LogUtils.COLON, "");
            int rssi = l2.getRssi();
            String n = a().n();
            if (rssi < 0) {
                rssi = -rssi;
            }
            if (replace != null && rssi < 100 && !replace.equals("020000000000")) {
                stringBuffer.append("&wf=");
                stringBuffer.append(replace);
                stringBuffer.append(";");
                stringBuffer.append("" + rssi + ";");
                String ssid = l2.getSSID();
                if (ssid != null && (ssid.contains("&") || ssid.contains(";"))) {
                    ssid = ssid.replace("&", "_");
                }
                stringBuffer.append(ssid);
                stringBuffer.append("&wf_n=1");
                if (n != null) {
                    stringBuffer.append("&wf_gw=");
                    stringBuffer.append(n);
                }
                return stringBuffer.toString();
            }
        }
        return null;
    }

    public String n() {
        DhcpInfo dhcpInfo;
        WifiManager wifiManager = this.c;
        if (wifiManager == null || (dhcpInfo = wifiManager.getDhcpInfo()) == null) {
            return null;
        }
        return a((long) dhcpInfo.gateway);
    }

    public i o() {
        i iVar = this.e;
        return (iVar == null || !iVar.j()) ? q() : this.e;
    }

    public i p() {
        i iVar = this.e;
        return (iVar == null || !iVar.k()) ? q() : this.e;
    }

    public i q() {
        WifiManager wifiManager = this.c;
        if (wifiManager != null) {
            try {
                return new i(wifiManager.getScanResults(), this.f);
            } catch (Exception e2) {
            }
        }
        return new i((List<ScanResult>) null, 0);
    }

    public boolean r() {
        try {
            if (!this.c.isWifiEnabled()) {
                return Build.VERSION.SDK_INT > 17 && this.c.isScanAlwaysAvailable();
            }
            return true;
        } catch (NoSuchMethodError e2) {
            return false;
        } catch (Exception e3) {
            return false;
        }
    }
}
