package com.baidu.location.b;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import com.baidu.location.Jni;
import com.baidu.location.e.b;
import com.baidu.location.f;
import com.baidu.location.g.c;
import com.baidu.location.g.e;
import com.baidu.location.g.j;
import com.baidu.location.g.k;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

public class o extends e {
    private static o q = null;
    String a;
    String b;
    String c;
    String d;
    int e;
    Handler f;

    private o() {
        this.a = null;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = 1;
        this.f = null;
        this.f = new Handler();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0041  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(java.io.File r4, java.io.File r5) throws java.io.IOException {
        /*
            r0 = 0
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch:{ all -> 0x0038 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x0038 }
            r2.<init>(r4)     // Catch:{ all -> 0x0038 }
            r1.<init>(r2)     // Catch:{ all -> 0x0038 }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ all -> 0x0034 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ all -> 0x0034 }
            r3.<init>(r5)     // Catch:{ all -> 0x0034 }
            r2.<init>(r3)     // Catch:{ all -> 0x0034 }
            r5 = 5120(0x1400, float:7.175E-42)
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x0032 }
        L_0x0019:
            int r0 = r1.read(r5)     // Catch:{ all -> 0x0032 }
            r3 = -1
            if (r0 == r3) goto L_0x0025
            r3 = 0
            r2.write(r5, r3, r0)     // Catch:{ all -> 0x0032 }
            goto L_0x0019
        L_0x0025:
            r2.flush()     // Catch:{ all -> 0x0032 }
            r4.delete()     // Catch:{ all -> 0x0032 }
            r1.close()
            r2.close()
            return
        L_0x0032:
            r4 = move-exception
            goto L_0x0036
        L_0x0034:
            r4 = move-exception
            r2 = r0
        L_0x0036:
            r0 = r1
            goto L_0x003a
        L_0x0038:
            r4 = move-exception
            r2 = r0
        L_0x003a:
            if (r0 == 0) goto L_0x003f
            r0.close()
        L_0x003f:
            if (r2 == 0) goto L_0x0044
            r2.close()
        L_0x0044:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.o.a(java.io.File, java.io.File):void");
    }

    /* access modifiers changed from: private */
    public boolean a(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo == null || networkInfo.getType() != 0) {
                return false;
            }
            String a2 = com.baidu.location.e.e.a(b.a().e());
            return a2.equals("3G") || a2.equals("4G");
        } catch (Throwable th) {
            return false;
        }
    }

    public static boolean a(String str, String str2) {
        File file = new File(k.j() + File.separator + "tmp");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[4096];
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(str).openConnection();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
            while (true) {
                int read = bufferedInputStream.read(bArr);
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            httpsURLConnection.disconnect();
            fileOutputStream.close();
            bufferedInputStream.close();
            if (file.length() < 10240) {
                file.delete();
                return false;
            }
            file.renameTo(new File(k.j() + File.separator + str2));
            return true;
        } catch (Exception e2) {
            file.delete();
            return false;
        }
    }

    public static o b() {
        if (q == null) {
            q = new o();
        }
        return q;
    }

    private Handler d() {
        return this.f;
    }

    private void e() {
        try {
            File file = new File(k.j() + "/grtcfrsa.dat");
            if (!file.exists()) {
                File file2 = new File(j.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (file.createNewFile()) {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(2);
                    randomAccessFile.writeInt(0);
                    randomAccessFile.seek(8);
                    byte[] bytes = "1980_01_01:0".getBytes();
                    randomAccessFile.writeInt(bytes.length);
                    randomAccessFile.write(bytes);
                    randomAccessFile.seek(200);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.seek(800);
                    randomAccessFile.writeBoolean(false);
                    randomAccessFile.close();
                } else {
                    return;
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, "rw");
            randomAccessFile2.seek(200);
            randomAccessFile2.writeBoolean(true);
            if (this.e == 1) {
                randomAccessFile2.writeBoolean(true);
            } else {
                randomAccessFile2.writeBoolean(false);
            }
            if (this.d != null) {
                byte[] bytes2 = this.d.getBytes();
                randomAccessFile2.writeInt(bytes2.length);
                randomAccessFile2.write(bytes2);
            } else if (Math.abs(f.getFrameVersion() - 8.22f) < 1.0E-8f) {
                randomAccessFile2.writeInt(0);
            }
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    /* access modifiers changed from: private */
    public void f() {
        if (this.a != null) {
            new s(this).start();
        }
    }

    /* access modifiers changed from: private */
    public boolean g() {
        String str = this.c;
        if (str == null) {
            return true;
        }
        if (str.contains("../")) {
            return false;
        }
        if (new File(k.j() + File.separator + this.c).exists()) {
            return true;
        }
        return a("https://" + this.a + "/" + this.c, this.c);
    }

    /* access modifiers changed from: private */
    public void h() {
        String str = this.b;
        if (str != null && !str.contains("../")) {
            File file = new File(k.j() + File.separator + this.b);
            if (!file.exists()) {
                if (a("https://" + this.a + "/" + this.b, this.b)) {
                    String a2 = k.a(file, "SHA-256");
                    String str2 = this.d;
                    if (str2 != null && a2 != null && k.b(a2, str2, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiP7BS5IjEOzrKGR9/Ww9oSDhdX1ir26VOsYjT1T6tk2XumRpkHRwZbrucDcNnvSB4QsqiEJnvTSRi7YMbh2H9sLMkcvHlMV5jAErNvnuskWfcvf7T2mq7EUZI/Hf4oVZhHV0hQJRFVdTcjWI6q2uaaKM3VMh+roDesiE7CR2biQIDAQAB")) {
                        File file2 = new File(k.j() + File.separator + f.replaceFileName);
                        if (file2.exists()) {
                            file2.delete();
                        }
                        try {
                            a(file, file2);
                        } catch (Exception e2) {
                            file2.delete();
                        }
                    }
                }
            }
        }
    }

    public void a() {
        String str;
        StringBuffer stringBuffer = new StringBuffer(128);
        stringBuffer.append("&sdk=");
        stringBuffer.append(8.22f);
        stringBuffer.append("&fw=");
        stringBuffer.append(f.getFrameVersion());
        stringBuffer.append("&suit=");
        stringBuffer.append(2);
        if (com.baidu.location.g.b.a().c == null) {
            stringBuffer.append("&im=");
            str = com.baidu.location.g.b.a().a;
        } else {
            stringBuffer.append("&cu=");
            str = com.baidu.location.g.b.a().c;
        }
        stringBuffer.append(str);
        stringBuffer.append("&mb=");
        stringBuffer.append(Build.MODEL);
        stringBuffer.append("&sv=");
        String str2 = Build.VERSION.RELEASE;
        if (str2 != null && str2.length() > 10) {
            str2 = str2.substring(0, 10);
        }
        stringBuffer.append(str2);
        String str3 = null;
        try {
            if (Build.VERSION.SDK_INT > 20) {
                String[] strArr = Build.SUPPORTED_ABIS;
                String str4 = null;
                for (int i = 0; i < strArr.length; i++) {
                    if (i == 0) {
                        str4 = strArr[i] + ";";
                    } else {
                        str4 = str4 + strArr[i] + ";";
                    }
                }
                str3 = str4;
            } else {
                str3 = Build.CPU_ABI2;
            }
        } catch (Error | Exception e2) {
        }
        if (str3 != null) {
            stringBuffer.append("&cpuabi=");
            stringBuffer.append(str3);
        }
        stringBuffer.append("&pack=");
        stringBuffer.append(com.baidu.location.g.b.e);
        this.h = k.f() + "?&it=" + Jni.en1(stringBuffer.toString());
    }

    public void a(boolean z) {
        if (z) {
            try {
                JSONObject jSONObject = new JSONObject(this.j);
                if ("up".equals(jSONObject.getString("res"))) {
                    if (jSONObject.has("upath")) {
                        this.a = jSONObject.getString("upath");
                    }
                    if (jSONObject.has("u1")) {
                        this.b = jSONObject.getString("u1");
                    }
                    if (jSONObject.has("u2")) {
                        this.c = jSONObject.getString("u2");
                    }
                    if (jSONObject.has("u1_rsa")) {
                        this.d = jSONObject.getString("u1_rsa");
                    }
                    d().post(new r(this));
                }
                if (jSONObject.has("ison")) {
                    this.e = jSONObject.getInt("ison");
                }
                e();
            } catch (Exception e2) {
            }
        }
    }

    public void c() {
        if (System.currentTimeMillis() - c.a().b() > 86400000) {
            c.a().a(System.currentTimeMillis());
            d().postDelayed(new p(this), OkHttpUtils.DEFAULT_MILLISECONDS);
            d().postDelayed(new q(this), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        }
    }
}
