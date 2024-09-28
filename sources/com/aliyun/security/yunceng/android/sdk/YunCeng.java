package com.aliyun.security.yunceng.android.sdk;

import android.content.Context;
import android.os.Process;
import com.aliyun.security.yunceng.android.sdk.listener.YunCengGetSessionListener;
import com.aliyun.security.yunceng.android.sdk.listener.YunCengInitExListener;
import com.aliyun.security.yunceng.android.sdk.traceroute.YCNetDiagnosisListener;
import com.aliyun.security.yunceng.android.sdk.traceroute.a;
import com.aliyun.security.yunceng.android.sdk.traceroute.d;
import com.aliyun.security.yunceng.android.sdk.umid.UMID;
import com.king.zxing.util.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;

public class YunCeng {
    static int a = 1;
    static int b = 2;
    static final String c = "1";
    static final String d = "2";
    /* access modifiers changed from: private */
    public static YunCengUtil e = new YunCengUtil();
    private static int f = 0;
    private static boolean g = false;
    private static byte[] h = new byte[8];

    private static native String getIpInfo();

    private static native String getProxyRaw(int i, String str, String str2, String str3, String str4, String str5);

    private static native String getRealFetchedIp();

    private static native String getRealFetchedPort();

    private static native String getSessionRaw();

    private static native int initExRaw(String str, String str2, int i);

    public static native int reportInfo(int i, String str, String str2, int i2);

    private static native byte[] safeDecryptRaw(byte[] bArr);

    private static native byte[] safeEncryptRaw(byte[] bArr);

    private static native int securityInitRaw(byte[] bArr, int i);

    private static native byte[] whiteboxEncryptRaw(byte[] bArr);

    private static native byte[] whiteboxSignRaw(byte[] bArr);

    static {
        System.loadLibrary("yunceng");
    }

    public static int initEx(String appKey, String token) {
        if (g) {
            return 0;
        }
        if (e.n() != null) {
            e.o();
            e.p();
            new UMID(e.n()).a();
        }
        int ret = initExRaw(appKey, token, 0);
        if (ret == 0) {
            g = true;
            if (e.n() != null) {
                c();
            }
        }
        return ret;
    }

    public static void initExWithCallback(final String appKey, final String token, final YunCengInitExListener listener) {
        new Thread(new Runnable() {
            public void run() {
                listener.OnInitExFinished(YunCeng.initEx(appKey, token));
            }
        }).start();
    }

    private static void c() {
        new Thread(new Runnable() {
            public void run() {
                if (YunCeng.e.b() && new Random().nextInt(100) < YunCeng.e.r()) {
                    int unused = YunCeng.d();
                }
                Context ct = YunCeng.e.n();
                if (YunCeng.e.d()) {
                    YunCeng.reportInfo(13, "umid", new UMID(ct).b(), 1);
                }
            }
        }).start();
    }

    public static int getProxyTcpByIp(String token, String group_name, String dip, String dport, StringBuffer target_ip, StringBuffer target_port) {
        return a(a, token, group_name, "2", dip, dport, target_ip, target_port);
    }

    public static int getProxyTcpByDomain(String token, String group_name, String ddomain, String dport, StringBuffer target_ip, StringBuffer target_port) {
        return a(a, token, group_name, "1", ddomain, dport, target_ip, target_port);
    }

    public static int getProxyUdpByIP(String token, String group_name, String dip, String dport, StringBuffer target_ip, StringBuffer target_port) {
        return a(b, token, group_name, "2", dip, dport, target_ip, target_port);
    }

    public static int getProxyUdpByDomain(String token, String group_name, String ddomain, String dport, StringBuffer target_ip, StringBuffer target_port) {
        return a(b, token, group_name, "1", ddomain, dport, target_ip, target_port);
    }

    private static int a(int proto_type, String token, String group_name, String type, String dhost, String dport, StringBuffer target_ip, StringBuffer target_port) {
        if (token == null || group_name == null || type == null || dhost == null || dport == null || token.length() == 0 || group_name.length() == 0 || type.length() == 0 || dhost.length() == 0 || dport.length() == 0) {
            return -1;
        }
        String ret = getProxyRaw(proto_type, token, group_name, type, dhost, dport).trim();
        if (e.a(ret)) {
            return Integer.valueOf(ret).intValue();
        }
        int index = ret.indexOf(LogUtils.VERTICAL);
        if (index <= 0 || index >= ret.length()) {
            return -1;
        }
        target_ip.setLength(0);
        target_ip.append(ret.substring(0, index));
        target_port.setLength(0);
        target_port.append(ret.substring(index + 1, ret.length()));
        return 0;
    }

    public static int getLocalIpInfo(StringBuffer ip_buf, StringBuffer ip_info) {
        int index;
        String ret = getIpInfo().trim();
        if (e.a(ret) || (index = ret.indexOf(LogUtils.VERTICAL)) <= 0 || index >= ret.length()) {
            return -1;
        }
        ip_buf.setLength(0);
        ip_buf.append(ret.substring(0, index));
        ip_info.setLength(0);
        ip_info.append(ret.substring(index + 1, ret.length()));
        return 0;
    }

    /* access modifiers changed from: private */
    public static int d() {
        new Thread(new Runnable() {
            public void run() {
                YCNetDiagnosisListener listener = new YCNetDiagnosisListener() {
                    public void OnNetDiagnosisFinished(String log) {
                        if (YunCeng.e.c()) {
                            YunCeng.reportInfo(14, "diag", log, 0);
                        }
                    }
                };
                String[] public_targets = {"www.baidu.com", "www.qq.com", "www.163.com", "www.taobao.com"};
                for (String dstDomain : public_targets) {
                    if (d.b(dstDomain) == null) {
                        break;
                    }
                    YunCeng.b((Context) null, dstDomain, "80", listener);
                }
                int i = 1;
                while (i < YunCeng.e.q() + 1) {
                    String dstDomain2 = String.format("a%06d.excaliburdx.com", new Object[]{Integer.valueOf(i)});
                    if (d.b(dstDomain2) != null) {
                        YunCeng.b((Context) null, dstDomain2, "10800", listener);
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }).start();
        return 0;
    }

    /* access modifiers changed from: private */
    public static void b(Context context, String domain, String port, YCNetDiagnosisListener listener) {
        try {
            new a(context, domain, port, listener).a();
        } catch (RuntimeException e2) {
        }
    }

    public static void startNetworkDiagnosis(String domain, int port, YCNetDiagnosisListener listener) {
        Context ct = e.n();
        if (ct.checkPermission("android.permission.ACCESS_NETWORK_STATE", Process.myPid(), Process.myUid()) == -1 || ct.checkPermission("android.permission.ACCESS_WIFI_STATE", Process.myPid(), Process.myUid()) == -1) {
            ct = null;
        }
        final Context theContext = ct;
        final String theDomain = domain;
        final String thePort = "" + port;
        final YCNetDiagnosisListener theListener = listener;
        new Thread(new Runnable() {
            public void run() {
                YunCeng.b(theContext, theDomain, thePort, theListener);
            }
        }).start();
    }

    public static int reportUserData(int type, String msg, int sync) {
        if (type < -1) {
            return -1;
        }
        reportInfo(type + 10000, "user_data", msg, sync);
        return 0;
    }

    public static int securityInit(String fileName) {
        try {
            if (f == 1) {
                return 5002;
            }
            FileInputStream in = new FileInputStream(fileName);
            int length = in.available();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (true) {
                int read = in.read(buffer);
                int len = read;
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
            in.close();
            byte[] data = outputStream.toByteArray();
            if (data[0] != 75 || data[1] != 78) {
                return 5004;
            }
            if (data[2] != 84) {
                return 5004;
            }
            int ret = securityInitRaw(data, length);
            if (ret != 0) {
                return ret;
            }
            h = Arrays.copyOfRange(data, 8, 16);
            f = 1;
            return ret;
        } catch (Exception e2) {
            e2.printStackTrace();
            return 5005;
        }
    }

    public static byte[] whiteboxSign(byte[] data) {
        if (f == 0 || data == null || data.length == 0) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(h);
            bos.write(whiteboxSignRaw(data));
            return bos.toByteArray();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static byte[] safeEncrypt(byte[] data) {
        if (f == 0 || data == null || data.length == 0) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(h);
            bos.write(safeEncryptRaw(data));
            return bos.toByteArray();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static byte[] safeDecrypt(byte[] data) {
        if (f == 0 || data == null || data.length <= 8) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(safeDecryptRaw(Arrays.copyOfRange(data, 8, data.length)));
            return bos.toByteArray();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static byte[] whiteboxEncrypt(byte[] data) {
        if (f == 0 || data == null || data.length == 0) {
            return null;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(h);
            bos.write(whiteboxEncryptRaw(data));
            return bos.toByteArray();
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static int GetSession(StringBuffer session) {
        session.setLength(0);
        String ret = getSessionRaw();
        int index = ret.indexOf(LogUtils.VERTICAL);
        if (index == -1 || index >= ret.length()) {
            return -1;
        }
        session.append(ret.substring(index + 1, ret.length()));
        return Integer.valueOf(ret.substring(0, index)).intValue();
    }

    public static void GetSessionWithCallback(final YunCengGetSessionListener listener) {
        new Thread(new Runnable() {
            public void run() {
                StringBuffer session = new StringBuffer();
                for (int ret = YunCeng.GetSession(session); ret != 0; ret = YunCeng.GetSession(session)) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                listener.OnGetSessionFinished(session.toString());
            }
        }).start();
    }

    public static URL getYunCengURL(URL url) {
        if (g) {
            return e.a(url);
        }
        return url;
    }
}
