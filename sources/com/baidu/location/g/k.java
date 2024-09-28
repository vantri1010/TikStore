package com.baidu.location.g;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.c.d;
import com.baidu.location.e.a;
import com.baidu.location.e.b;
import com.baidu.location.e.f;
import com.baidu.location.e.i;
import com.baidu.mapapi.UIMsg;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import kotlin.UByte;

public class k {
    public static float A = 2.2f;
    public static float B = 2.3f;
    public static float C = 3.8f;
    public static int D = 3;
    public static int E = 10;
    public static int F = 2;
    public static int G = 7;
    public static int H = 20;
    public static int I = 70;
    public static int J = 120;
    public static float K = 2.0f;
    public static float L = 10.0f;
    public static float M = 50.0f;
    public static float N = 200.0f;
    public static int O = 16;
    public static int P = 32;
    public static float Q = 0.9f;
    public static int R = 10000;
    public static float S = 0.5f;
    public static float T = 0.0f;
    public static float U = 0.1f;
    public static int V = 30;
    public static int W = 100;
    public static int X = 0;
    public static int Y = 0;
    public static int Z = 0;
    public static boolean a = false;
    private static String aA = "http://loc.map.baidu.com/user_err.php";
    private static String aB = "http://loc.map.baidu.com/oqur.php";
    private static String aC = "https://loc.map.baidu.com/tcu.php";
    private static String aD = "http://loc.map.baidu.com/rtbu.php";
    private static String aE = "http://loc.map.baidu.com/iofd.php";
    private static String aF = "http://loc.map.baidu.com/wloc";
    public static int aa = 420000;
    public static boolean ab = true;
    public static boolean ac = true;
    public static int ad = 20;
    public static int ae = 300;
    public static int af = 1000;
    public static int ag = Integer.MAX_VALUE;
    public static long ah = 900000;
    public static long ai = 420000;
    public static long aj = 180000;
    public static long ak = 0;
    public static long al = 15;
    public static long am = 300000;
    public static int an = 1000;
    public static int ao = 0;
    public static int ap = UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT;
    public static int aq = UIMsg.m_AppUI.MSG_RADAR_SEARCH_RETURN_RESULT;
    public static float ar = 10.0f;
    public static float as = 6.0f;
    public static float at = 10.0f;
    public static int au = 60;
    public static int av = 70;
    public static int aw = 6;
    public static String ax = null;
    public static boolean ay = false;
    private static String az = "http://loc.map.baidu.com/sdk.php";
    public static boolean b = false;
    public static boolean c = false;
    public static int d = 0;
    public static String e = "http://loc.map.baidu.com/sdk_ep.php";
    public static String f = "https://loc.map.baidu.com/sdk.php";
    public static String g = "no";
    public static boolean h = false;
    public static boolean i = false;
    public static boolean j = false;
    public static boolean k = false;
    public static boolean l = false;
    public static boolean m = false;
    public static String n = CoordinateType.GCJ02;
    public static String o = "";
    public static boolean p = true;
    public static int q = 3;
    public static double r = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    public static double s = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    public static double t = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    public static double u = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    public static int v = 0;
    public static byte[] w = null;
    public static boolean x = false;
    public static int y = 0;
    public static float z = 1.1f;

    public static int a(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0);
        } catch (Exception e2) {
            return 2;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0017 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0018 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(android.content.Context r4, java.lang.String r5) {
        /*
            r0 = 0
            r1 = 1
            int r2 = android.os.Process.myPid()     // Catch:{ Exception -> 0x0013 }
            int r3 = android.os.Process.myUid()     // Catch:{ Exception -> 0x0013 }
            int r4 = r4.checkPermission(r5, r2, r3)     // Catch:{ Exception -> 0x0013 }
            if (r4 != 0) goto L_0x0011
            goto L_0x0014
        L_0x0011:
            r4 = 0
            goto L_0x0015
        L_0x0013:
            r4 = move-exception
        L_0x0014:
            r4 = 1
        L_0x0015:
            if (r4 != 0) goto L_0x0018
            return r0
        L_0x0018:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.k.a(android.content.Context, java.lang.String):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0015, code lost:
        r2 = r2 + r5.length();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(java.lang.String r4, java.lang.String r5, java.lang.String r6) {
        /*
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r4 == 0) goto L_0x0034
            java.lang.String r1 = ""
            boolean r2 = r4.equals(r1)
            if (r2 == 0) goto L_0x000d
            goto L_0x0034
        L_0x000d:
            int r2 = r4.indexOf(r5)
            r3 = -1
            if (r2 != r3) goto L_0x0015
            return r0
        L_0x0015:
            int r5 = r5.length()
            int r2 = r2 + r5
            int r5 = r4.indexOf(r6, r2)
            if (r5 != r3) goto L_0x0021
            return r0
        L_0x0021:
            java.lang.String r4 = r4.substring(r2, r5)
            if (r4 == 0) goto L_0x0034
            boolean r5 = r4.equals(r1)
            if (r5 == 0) goto L_0x002e
            goto L_0x0034
        L_0x002e:
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x0033 }
            return r4
        L_0x0033:
            r4 = move-exception
        L_0x0034:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.k.a(java.lang.String, java.lang.String, java.lang.String):int");
    }

    public static String a() {
        Calendar instance = Calendar.getInstance();
        int i2 = instance.get(5);
        int i3 = instance.get(1);
        int i4 = instance.get(11);
        int i5 = instance.get(12);
        int i6 = instance.get(13);
        return String.format(Locale.CHINA, "%d-%02d-%02d %02d:%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(i2), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6)});
    }

    public static String a(a aVar, i iVar, Location location, String str, int i2) {
        return a(aVar, iVar, location, str, i2, false);
    }

    public static String a(a aVar, i iVar, Location location, String str, int i2, boolean z2) {
        String a2;
        String b2;
        StringBuffer stringBuffer = new StringBuffer(2048);
        if (!(aVar == null || (b2 = b.a().b(aVar)) == null)) {
            stringBuffer.append(b2);
        }
        if (iVar != null) {
            String b3 = i2 == 0 ? z2 ? iVar.b() : iVar.c() : iVar.d();
            if (b3 != null) {
                stringBuffer.append(b3);
            }
        }
        if (location != null) {
            String b4 = (d == 0 || i2 == 0) ? f.b(location) : f.c(location);
            if (b4 != null) {
                stringBuffer.append(b4);
            }
        }
        boolean z3 = false;
        if (i2 == 0) {
            z3 = true;
        }
        String a3 = b.a().a(z3);
        if (a3 != null) {
            stringBuffer.append(a3);
        }
        if (str != null) {
            stringBuffer.append(str);
        }
        String d2 = d.a().d();
        if (!TextUtils.isEmpty(d2)) {
            stringBuffer.append("&bc=");
            stringBuffer.append(d2);
        }
        if (!(aVar == null || (a2 = b.a().a(aVar)) == null || a2.length() + stringBuffer.length() >= 2000)) {
            stringBuffer.append(a2);
        }
        String stringBuffer2 = stringBuffer.toString();
        if (!(location == null || iVar == null)) {
            try {
                float speed = location.getSpeed();
                int i3 = d;
                int h2 = iVar.h();
                int a4 = iVar.a();
                boolean i4 = iVar.i();
                if (speed < as && ((i3 == 1 || i3 == 0) && (h2 < au || i4))) {
                    q = 1;
                    return stringBuffer2;
                } else if (speed < at && ((i3 == 1 || i3 == 0 || i3 == 3) && (h2 < av || a4 > aw))) {
                    q = 2;
                    return stringBuffer2;
                }
            } catch (Exception e2) {
                q = 3;
            }
        }
        q = 3;
        return stringBuffer2;
    }

    public static String a(File file, String str) {
        if (!file.isFile()) {
            return null;
        }
        byte[] bArr = new byte[1024];
        try {
            MessageDigest instance = MessageDigest.getInstance(str);
            FileInputStream fileInputStream = new FileInputStream(file);
            while (true) {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read != -1) {
                    instance.update(bArr, 0, read);
                } else {
                    fileInputStream.close();
                    return new BigInteger(1, instance.digest()).toString(16);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static String a(String str) {
        return Jni.en1(o + ";" + str);
    }

    public static String a(byte[] bArr, String str, boolean z2) {
        StringBuilder sb = new StringBuilder();
        for (byte b2 : bArr) {
            String hexString = Integer.toHexString(b2 & UByte.MAX_VALUE);
            if (z2) {
                hexString = hexString.toUpperCase();
            }
            if (hexString.length() == 1) {
                sb.append("0");
            }
            sb.append(hexString);
            sb.append(str);
        }
        return sb.toString();
    }

    public static String a(byte[] bArr, boolean z2) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.reset();
            instance.update(bArr);
            return a(instance.digest(), "", z2);
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static boolean a(BDLocation bDLocation) {
        int locType = bDLocation.getLocType();
        return (locType > 100 && locType < 200) || locType == 62;
    }

    public static int b(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return -2;
        }
        try {
            return Settings.Secure.getInt(context.getContentResolver(), "location_mode", -1);
        } catch (Exception e2) {
            return -1;
        }
    }

    public static boolean b() {
        return false;
    }

    public static boolean b(String str, String str2, String str3) {
        try {
            PublicKey generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str3.getBytes(), 0)));
            Signature instance = Signature.getInstance("SHA1WithRSA");
            instance.initVerify(generatePublic);
            instance.update(str.getBytes());
            return instance.verify(Base64.decode(str2.getBytes(), 0));
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }

    public static String c() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (true) {
                    if (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement = inetAddresses.nextElement();
                        if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet4Address)) {
                            byte[] address = nextElement.getAddress();
                            String str = "";
                            for (byte b2 : address) {
                                String hexString = Integer.toHexString(b2 & UByte.MAX_VALUE);
                                if (hexString.length() == 1) {
                                    hexString = '0' + hexString;
                                }
                                str = str + hexString;
                            }
                            return str;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e2) {
            return null;
        }
    }

    public static boolean c(Context context) {
        int i2;
        if (context == null) {
            return true;
        }
        try {
            i2 = context.checkCallingOrSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION);
        } catch (Exception e2) {
            e2.printStackTrace();
            i2 = 0;
        }
        boolean z2 = i2 == 0;
        if (z2 && Build.VERSION.SDK_INT >= 23) {
            try {
                if (Settings.Secure.getInt(context.getContentResolver(), "location_mode", 1) == 0) {
                    return false;
                }
            } catch (Exception e3) {
            }
        }
        return z2;
    }

    public static String d() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                while (true) {
                    if (inetAddresses.hasMoreElements()) {
                        InetAddress nextElement = inetAddresses.nextElement();
                        if (!nextElement.isLoopbackAddress() && (nextElement instanceof Inet6Address) && nextElement.getHostAddress() != null && !nextElement.getHostAddress().startsWith("fe80:")) {
                            return nextElement.getHostAddress();
                        }
                    }
                }
            }
            return "";
        } catch (SocketException e2) {
            return "";
        } catch (Throwable th) {
            return "";
        }
    }

    public static String d(Context context) {
        int a2 = a(context, PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION);
        int a3 = a(context, "android.permission.ACCESS_FINE_LOCATION");
        int a4 = a(context, "android.permission.READ_PHONE_STATE");
        return "&per=" + a2 + LogUtils.VERTICAL + a3 + LogUtils.VERTICAL + a4;
    }

    public static String e() {
        return az;
    }

    public static String e(Context context) {
        int i2 = -1;
        if (context != null) {
            try {
                NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
                    i2 = activeNetworkInfo.getType();
                }
            } catch (Throwable th) {
            }
        }
        return "&netc=" + i2;
    }

    public static String f() {
        return aC;
    }

    public static String g() {
        return "https://daup.map.baidu.com/cltr/rcvr";
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0040 A[SYNTHETIC, Splitter:B:19:0x0040] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String h() {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 28
            r2 = 0
            if (r0 > r1) goto L_0x0020
            java.lang.String r0 = android.os.Environment.getExternalStorageState()     // Catch:{ Exception -> 0x001c }
            java.lang.String r3 = "mounted"
            boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x001c }
            if (r0 == 0) goto L_0x0020
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x001c }
            java.lang.String r0 = r0.getPath()     // Catch:{ Exception -> 0x001c }
            goto L_0x0021
        L_0x001c:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0020:
            r0 = r2
        L_0x0021:
            if (r0 != 0) goto L_0x003e
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 <= r1) goto L_0x003e
            android.content.Context r1 = com.baidu.location.f.getServiceContext()
            if (r1 == 0) goto L_0x003e
            android.content.Context r0 = com.baidu.location.f.getServiceContext()     // Catch:{ Exception -> 0x003c }
            java.lang.String r1 = android.os.Environment.DIRECTORY_MOVIES     // Catch:{ Exception -> 0x003c }
            java.io.File r0 = r0.getExternalFilesDir(r1)     // Catch:{ Exception -> 0x003c }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x003c }
            goto L_0x003e
        L_0x003c:
            r0 = move-exception
            r0 = r2
        L_0x003e:
            if (r0 == 0) goto L_0x0065
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0060 }
            r1.<init>()     // Catch:{ Exception -> 0x0060 }
            r1.append(r0)     // Catch:{ Exception -> 0x0060 }
            java.lang.String r3 = "/baidu/tempdata"
            r1.append(r3)     // Catch:{ Exception -> 0x0060 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0060 }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0060 }
            r3.<init>(r1)     // Catch:{ Exception -> 0x0060 }
            boolean r1 = r3.exists()     // Catch:{ Exception -> 0x0060 }
            if (r1 != 0) goto L_0x0065
            r3.mkdirs()     // Catch:{ Exception -> 0x0060 }
            goto L_0x0065
        L_0x0060:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0066
        L_0x0065:
            r2 = r0
        L_0x0066:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.k.h():java.lang.String");
    }

    public static String i() {
        String h2 = h();
        if (h2 == null) {
            return null;
        }
        return h2 + "/baidu/tempdata";
    }

    public static String j() {
        try {
            File file = new File(com.baidu.location.f.getServiceContext().getFilesDir() + File.separator + "lldt");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e2) {
            return null;
        }
    }

    public static String k() {
        try {
            File file = new File(com.baidu.location.f.getServiceContext().getFilesDir() + File.separator + "/baidu/tempdata");
            if (!file.exists()) {
                file.mkdirs();
            }
            return com.baidu.location.f.getServiceContext().getFilesDir().getPath();
        } catch (Exception e2) {
            return null;
        }
    }

    public static String l() {
        try {
            File file = new File(com.baidu.location.f.getServiceContext().getFilesDir() + File.separator + "/baidu/tempdata");
            if (!file.exists()) {
                file.mkdirs();
            }
            return com.baidu.location.f.getServiceContext().getFilesDir().getPath() + File.separator + "/baidu/tempdata";
        } catch (Exception e2) {
            return null;
        }
    }
}
