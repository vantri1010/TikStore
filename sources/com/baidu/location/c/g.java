package com.baidu.location.c;

import android.os.Environment;
import android.text.TextUtils;
import com.baidu.location.e.j;
import com.baidu.location.g.a;
import com.baidu.location.g.k;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.lang.Thread;
import java.net.HttpURLConnection;
import java.net.URL;

public class g implements Thread.UncaughtExceptionHandler {
    private static g a = null;
    private int b = 0;

    private g() {
    }

    public static g a() {
        if (a == null) {
            a = new g();
        }
        return a;
    }

    private String a(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    private void a(File file, String str, String str2) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(280);
            randomAccessFile.writeInt(12346);
            randomAccessFile.seek(300);
            randomAccessFile.writeLong(System.currentTimeMillis());
            byte[] bytes = str.getBytes();
            randomAccessFile.writeInt(bytes.length);
            randomAccessFile.write(bytes, 0, bytes.length);
            randomAccessFile.seek(600);
            byte[] bytes2 = str2.getBytes();
            randomAccessFile.writeInt(bytes2.length);
            randomAccessFile.write(bytes2, 0, bytes2.length);
            if (!a(str, str2)) {
                randomAccessFile.seek(280);
                randomAccessFile.writeInt(1326);
            }
            randomAccessFile.close();
        } catch (Exception e) {
        }
    }

    private boolean a(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || !j.j()) {
            return false;
        }
        try {
            URL url = new URL(k.e);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("e0");
            stringBuffer.append("=");
            stringBuffer.append(str);
            stringBuffer.append("&");
            stringBuffer.append("e1");
            stringBuffer.append("=");
            stringBuffer.append(str2);
            stringBuffer.append("&");
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(a.b);
            httpURLConnection.setReadTimeout(a.b);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(stringBuffer.toString().getBytes());
            outputStream.flush();
            outputStream.close();
            return httpURLConnection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public void b() {
        String str;
        try {
            File file = new File((Environment.getExternalStorageDirectory().getPath() + "/traces") + "/error_fs2.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(280);
                if (1326 == randomAccessFile.readInt()) {
                    randomAccessFile.seek(308);
                    int readInt = randomAccessFile.readInt();
                    String str2 = null;
                    if (readInt <= 0 || readInt >= 2048) {
                        str = null;
                    } else {
                        byte[] bArr = new byte[readInt];
                        randomAccessFile.read(bArr, 0, readInt);
                        str = new String(bArr, 0, readInt);
                    }
                    randomAccessFile.seek(600);
                    int readInt2 = randomAccessFile.readInt();
                    if (readInt2 > 0 && readInt2 < 2048) {
                        byte[] bArr2 = new byte[readInt2];
                        randomAccessFile.read(bArr2, 0, readInt2);
                        str2 = new String(bArr2, 0, readInt2);
                    }
                    if (a(str, str2)) {
                        randomAccessFile.seek(280);
                        randomAccessFile.writeInt(12346);
                    }
                }
                randomAccessFile.close();
            }
        } catch (Exception e) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x00a8 A[Catch:{ Exception -> 0x00b3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00bb A[SYNTHETIC, Splitter:B:34:0x00bb] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void uncaughtException(java.lang.Thread r9, java.lang.Throwable r10) {
        /*
            r8 = this;
            int r9 = r8.b
            r0 = 1
            int r9 = r9 + r0
            r8.b = r9
            r1 = 2
            if (r9 <= r1) goto L_0x0011
        L_0x0009:
            int r9 = android.os.Process.myPid()
            android.os.Process.killProcess(r9)
            return
        L_0x0011:
            long r1 = java.lang.System.currentTimeMillis()
            long r3 = com.baidu.location.f.a.b()
            long r1 = r1 - r3
            r3 = 10000(0x2710, double:4.9407E-320)
            int r9 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r9 >= 0) goto L_0x0075
            r9 = 1090749727(0x4103851f, float:8.22)
            float r1 = com.baidu.location.f.getFrameVersion()
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 <= 0) goto L_0x0075
            com.baidu.location.g.c r9 = com.baidu.location.g.c.a()
            long r1 = r9.c()
            long r3 = java.lang.System.currentTimeMillis()
            long r3 = r3 - r1
            r1 = 40000(0x9c40, double:1.97626E-319)
            int r9 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r9 >= 0) goto L_0x006a
            java.io.File r9 = new java.io.File
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = com.baidu.location.g.k.j()
            r1.append(r2)
            java.lang.String r2 = java.io.File.separator
            r1.append(r2)
            java.lang.String r2 = com.baidu.location.f.getJarFileName()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r9.<init>(r1)
            boolean r1 = r9.exists()
            if (r1 == 0) goto L_0x0075
            r9.delete()
            goto L_0x0075
        L_0x006a:
            com.baidu.location.g.c r9 = com.baidu.location.g.c.a()
            long r1 = java.lang.System.currentTimeMillis()
            r9.b(r1)
        L_0x0075:
            r9 = 0
            r1 = 0
            java.lang.String r10 = r8.a(r10)     // Catch:{ Exception -> 0x00b5 }
            if (r10 == 0) goto L_0x0086
            java.lang.String r2 = "com.baidu.location"
            boolean r2 = r10.contains(r2)     // Catch:{ Exception -> 0x00b3 }
            if (r2 == 0) goto L_0x0086
            goto L_0x0087
        L_0x0086:
            r0 = 0
        L_0x0087:
            com.baidu.location.g.b r2 = com.baidu.location.g.b.a()     // Catch:{ Exception -> 0x00b3 }
            java.lang.String r2 = r2.a((boolean) r9)     // Catch:{ Exception -> 0x00b3 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b3 }
            r3.<init>()     // Catch:{ Exception -> 0x00b3 }
            r3.append(r2)     // Catch:{ Exception -> 0x00b3 }
            com.baidu.location.b.a r2 = com.baidu.location.b.a.a()     // Catch:{ Exception -> 0x00b3 }
            java.lang.String r2 = r2.d()     // Catch:{ Exception -> 0x00b3 }
            r3.append(r2)     // Catch:{ Exception -> 0x00b3 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x00b3 }
            if (r2 == 0) goto L_0x00ad
            java.lang.String r9 = com.baidu.location.Jni.encode(r2)     // Catch:{ Exception -> 0x00b3 }
            goto L_0x00ae
        L_0x00ad:
            r9 = r1
        L_0x00ae:
            r7 = r10
            r10 = r9
            r9 = r0
            r0 = r7
            goto L_0x00b9
        L_0x00b3:
            r0 = move-exception
            goto L_0x00b7
        L_0x00b5:
            r10 = move-exception
            r10 = r1
        L_0x00b7:
            r0 = r10
            r10 = r1
        L_0x00b9:
            if (r9 == 0) goto L_0x0009
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012f }
            r9.<init>()     // Catch:{ Exception -> 0x012f }
            java.io.File r2 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x012f }
            java.lang.String r2 = r2.getPath()     // Catch:{ Exception -> 0x012f }
            r9.append(r2)     // Catch:{ Exception -> 0x012f }
            java.lang.String r2 = "/traces"
            r9.append(r2)     // Catch:{ Exception -> 0x012f }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x012f }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012f }
            r2.<init>()     // Catch:{ Exception -> 0x012f }
            r2.append(r9)     // Catch:{ Exception -> 0x012f }
            java.lang.String r3 = "/error_fs2.dat"
            r2.append(r3)     // Catch:{ Exception -> 0x012f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x012f }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x012f }
            r3.<init>(r2)     // Catch:{ Exception -> 0x012f }
            boolean r2 = r3.exists()     // Catch:{ Exception -> 0x012f }
            if (r2 != 0) goto L_0x010b
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x012f }
            r2.<init>(r9)     // Catch:{ Exception -> 0x012f }
            boolean r9 = r2.exists()     // Catch:{ Exception -> 0x012f }
            if (r9 != 0) goto L_0x00fe
            r2.mkdirs()     // Catch:{ Exception -> 0x012f }
        L_0x00fe:
            boolean r9 = r3.createNewFile()     // Catch:{ Exception -> 0x012f }
            if (r9 != 0) goto L_0x0105
            goto L_0x0106
        L_0x0105:
            r1 = r3
        L_0x0106:
            r8.a(r1, r10, r0)     // Catch:{ Exception -> 0x012f }
            goto L_0x0009
        L_0x010b:
            java.io.RandomAccessFile r9 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x012f }
            java.lang.String r1 = "rw"
            r9.<init>(r3, r1)     // Catch:{ Exception -> 0x012f }
            r1 = 300(0x12c, double:1.48E-321)
            r9.seek(r1)     // Catch:{ Exception -> 0x012f }
            long r1 = r9.readLong()     // Catch:{ Exception -> 0x012f }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x012f }
            long r4 = r4 - r1
            r1 = 86400000(0x5265c00, double:4.2687272E-316)
            int r6 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r6 <= 0) goto L_0x012a
            r8.a(r3, r10, r0)     // Catch:{ Exception -> 0x012f }
        L_0x012a:
            r9.close()     // Catch:{ Exception -> 0x012f }
            goto L_0x0009
        L_0x012f:
            r9 = move-exception
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.c.g.uncaughtException(java.lang.Thread, java.lang.Throwable):void");
    }
}
