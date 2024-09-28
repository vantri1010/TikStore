package io.openinstall.sdk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import com.king.zxing.util.LogUtils;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import kotlin.UByte;

public class j {
    private static j k;
    private static final Object l = new Object();
    private final Context a;
    private final f b;
    private final String c;
    private final String d = Build.VERSION.RELEASE;
    private final Integer e;
    private final String f;
    private final String g;
    private final String h;
    private final String i;
    private final String j;
    private String m;
    private String n;
    private String o;

    private j(Context context, f fVar) {
        Integer num;
        this.a = context;
        this.b = fVar;
        this.c = context.getPackageName();
        String str = null;
        try {
            PackageInfo packageInfo = this.a.getPackageManager().getPackageInfo(this.a.getPackageName(), 0);
            num = Integer.valueOf(packageInfo.versionCode);
            try {
                str = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e2) {
            }
        } catch (PackageManager.NameNotFoundException e3) {
            num = null;
        }
        this.e = num;
        this.f = str;
        this.g = Build.MODEL;
        this.h = Build.ID;
        this.i = Build.DISPLAY;
        this.j = Build.BRAND;
    }

    public static j a(Context context, f fVar) {
        synchronized (l) {
            if (k == null) {
                k = new j(context.getApplicationContext(), fVar);
            }
        }
        return k;
    }

    private boolean a(String str) {
        return TextUtils.isEmpty(str) || str.equalsIgnoreCase(ca.m);
    }

    private boolean b(String str) {
        return TextUtils.isEmpty(str) || str.equalsIgnoreCase(ca.i) || str.equalsIgnoreCase(ca.m);
    }

    public String a() {
        String str = this.m;
        if (str != null) {
            return str;
        }
        String a2 = this.b.a("FM_android_id");
        if (TextUtils.isEmpty(a2)) {
            try {
                a2 = Settings.Secure.getString(this.a.getContentResolver(), "android_id");
            } catch (Throwable th) {
            }
        }
        if (b(a2)) {
            this.b.a("FM_android_id", ca.m);
            a2 = "";
        } else {
            this.b.a("FM_android_id", a2);
        }
        this.m = a2;
        return this.m;
    }

    public String b() {
        String str = this.n;
        if (str != null) {
            return str;
        }
        String a2 = this.b.a("FM_serial_number");
        if (TextUtils.isEmpty(a2)) {
            if (Build.VERSION.SDK_INT < 26) {
                a2 = Build.SERIAL;
            } else {
                try {
                    a2 = Build.getSerial();
                } catch (SecurityException e2) {
                }
            }
        }
        if (a(a2)) {
            this.b.a("FM_serial_number", ca.m);
            a2 = "";
        } else {
            this.b.a("FM_serial_number", a2);
        }
        this.n = a2;
        return this.n;
    }

    public String c() {
        String str = this.o;
        if (str != null) {
            return str;
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(this.a.getPackageManager().getPackageInfo(this.a.getPackageName(), 64).signatures[0].toByteArray());
            StringBuilder sb = new StringBuilder();
            for (byte b2 : digest) {
                String upperCase = Integer.toHexString(b2 & UByte.MAX_VALUE).toUpperCase(Locale.US);
                if (upperCase.length() == 1) {
                    sb.append("0");
                }
                sb.append(upperCase);
                sb.append(LogUtils.COLON);
            }
            String sb2 = sb.toString();
            this.o = sb2.substring(0, sb2.length() - 1);
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e2) {
        }
        return this.o;
    }

    public String d() {
        return this.c;
    }

    public String e() {
        return this.d;
    }

    public Integer f() {
        return this.e;
    }

    public String g() {
        return this.g;
    }

    public String h() {
        return this.h;
    }

    public String i() {
        return this.i;
    }

    public String j() {
        return this.j;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String k() {
        /*
            r5 = this;
            android.content.Context r0 = r5.a
            android.content.pm.ApplicationInfo r0 = r0.getApplicationInfo()
            java.lang.String r0 = r0.sourceDir
            r1 = 0
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x0056, all -> 0x004c }
            java.lang.String r3 = "r"
            r2.<init>(r0, r3)     // Catch:{ IOException -> 0x0056, all -> 0x004c }
            java.nio.channels.FileChannel r0 = r2.getChannel()     // Catch:{ IOException -> 0x0056, all -> 0x004c }
            io.openinstall.sdk.ao r2 = io.openinstall.sdk.ap.a(r0)     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            java.lang.String r3 = ""
            if (r2 != 0) goto L_0x0024
            if (r0 == 0) goto L_0x0023
            r0.close()     // Catch:{ IOException -> 0x0022 }
            goto L_0x0023
        L_0x0022:
            r0 = move-exception
        L_0x0023:
            return r3
        L_0x0024:
            byte[] r2 = r2.c()     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            if (r2 != 0) goto L_0x0032
            if (r0 == 0) goto L_0x0031
            r0.close()     // Catch:{ IOException -> 0x0030 }
            goto L_0x0031
        L_0x0030:
            r0 = move-exception
        L_0x0031:
            return r3
        L_0x0032:
            r3 = 10
            byte[] r2 = android.util.Base64.encode(r2, r3)     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            java.lang.String r3 = new java.lang.String     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            java.nio.charset.Charset r4 = io.openinstall.sdk.ac.c     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            r3.<init>(r2, r4)     // Catch:{ IOException -> 0x004a, all -> 0x0048 }
            if (r0 == 0) goto L_0x0046
            r0.close()     // Catch:{ IOException -> 0x0045 }
            goto L_0x0046
        L_0x0045:
            r0 = move-exception
        L_0x0046:
            r1 = r3
            goto L_0x005b
        L_0x0048:
            r2 = move-exception
            goto L_0x004e
        L_0x004a:
            r2 = move-exception
            goto L_0x0058
        L_0x004c:
            r0 = move-exception
            r0 = r1
        L_0x004e:
            if (r0 == 0) goto L_0x005b
        L_0x0050:
            r0.close()     // Catch:{ IOException -> 0x0054 }
            goto L_0x005b
        L_0x0054:
            r0 = move-exception
            goto L_0x005b
        L_0x0056:
            r0 = move-exception
            r0 = r1
        L_0x0058:
            if (r0 == 0) goto L_0x005b
            goto L_0x0050
        L_0x005b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.j.k():java.lang.String");
    }

    public List<String> l() {
        ArrayList arrayList = new ArrayList();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface nextElement = networkInterfaces.nextElement();
                    if (!nextElement.isLoopback()) {
                        if (nextElement.isUp()) {
                            Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
                            while (inetAddresses.hasMoreElements()) {
                                arrayList.add(inetAddresses.nextElement().getHostAddress());
                            }
                        }
                    }
                }
            }
        } catch (SocketException e2) {
        }
        return arrayList;
    }
}
