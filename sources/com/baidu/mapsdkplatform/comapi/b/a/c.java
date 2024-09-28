package com.baidu.mapsdkplatform.comapi.b.a;

import android.content.Context;
import android.os.Build;
import com.baidu.mapapi.NetworkUtil;
import com.baidu.mapsdkplatform.comapi.util.SyncSysInfo;
import com.baidu.mapsdkplatform.comapi.util.g;
import com.baidu.mapsdkplatform.comapi.util.h;
import com.baidu.mapsdkplatform.comjni.util.JNIHandler;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.zip.GZIPOutputStream;

public class c {
    /* access modifiers changed from: private */
    public static String a = "";
    /* access modifiers changed from: private */
    public static String b = "";
    private static String c = "";
    private Context d;

    private static final class a {
        /* access modifiers changed from: private */
        public static final c a = new c();
    }

    public static c a() {
        return a.a;
    }

    private void a(InputStream inputStream, OutputStream outputStream) throws Exception {
        GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(outputStream);
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr, 0, 1024);
            if (read != -1) {
                gZIPOutputStream.write(bArr, 0, read);
            } else {
                gZIPOutputStream.flush();
                gZIPOutputStream.close();
                try {
                    outputStream.close();
                    inputStream.close();
                    return;
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(File[] fileArr) {
        int length = fileArr.length;
        for (int i = 0; i < length - 10; i++) {
            int i2 = i + 10;
            if (fileArr[i2] != null && fileArr[i2].exists()) {
                fileArr[i2].delete();
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: java.io.InputStream} */
    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r4v0, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r1v1, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r4v3, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r1v2, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r1v5, types: [java.io.BufferedReader] */
    /* JADX WARNING: type inference failed for: r1v7 */
    /* JADX WARNING: type inference failed for: r4v28 */
    /* JADX WARNING: type inference failed for: r4v29 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00e4, code lost:
        r10 = th;
        r4 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00e6, code lost:
        r4 = r4;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0156 A[SYNTHETIC, Splitter:B:104:0x0156] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:23:0x00ae] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0122 A[SYNTHETIC, Splitter:B:76:0x0122] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0135 A[SYNTHETIC, Splitter:B:85:0x0135] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0141 A[SYNTHETIC, Splitter:B:94:0x0141] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean a(java.io.File r10) {
        /*
            r9 = this;
            monitor-enter(r9)
            r0 = 0
            r1 = 0
            java.net.HttpURLConnection r2 = r9.g()     // Catch:{ Exception -> 0x013b, all -> 0x011c }
            if (r2 != 0) goto L_0x0012
            if (r2 == 0) goto L_0x0010
            r2.disconnect()     // Catch:{ Exception -> 0x000f }
            goto L_0x0010
        L_0x000f:
            r10 = move-exception
        L_0x0010:
            monitor-exit(r9)
            return r0
        L_0x0012:
            r2.connect()     // Catch:{ Exception -> 0x0119, all -> 0x0116 }
            java.io.OutputStream r3 = r2.getOutputStream()     // Catch:{ Exception -> 0x0119, all -> 0x0116 }
            java.lang.StringBuilder r4 = r9.b(r10)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r4 = r4.getBytes()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r4 = r9.a((byte[]) r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r3.write(r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r4.<init>()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r5 = "--bd_map_sdk_cc"
            r4.append(r5)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r5 = "\r\n"
            r4.append(r5)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r5 = "Content-Disposition: form-data; name=\"file\"; filename=\"c.txt\"\r\n"
            r4.append(r5)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r5 = "\r\n"
            r4.append(r5)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r4 = r4.getBytes()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r4 = r9.a((byte[]) r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r3.write(r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.io.ByteArrayOutputStream r4 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            long r5 = r10.length()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            int r6 = (int) r5     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r4.<init>(r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.io.DataInputStream r5 = new java.io.DataInputStream     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r6.<init>(r10)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r5.<init>(r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r6]     // Catch:{ Exception -> 0x0111, all -> 0x010c }
        L_0x006c:
            int r7 = r5.read(r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r8 = -1
            if (r7 == r8) goto L_0x0077
            r4.write(r6, r0, r7)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            goto L_0x006c
        L_0x0077:
            byte[] r6 = r4.toByteArray()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r6 = r9.a((byte[]) r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r3.write(r6)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r5.close()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r4.close()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.lang.String r4 = "\r\n--bd_map_sdk_cc--\r\n"
            byte[] r4 = r4.getBytes()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            byte[] r4 = r9.a((byte[]) r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r3.write(r4)     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r3.flush()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            int r4 = r2.getResponseCode()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            r5 = 200(0xc8, float:2.8E-43)
            if (r4 != r5) goto L_0x00ee
            java.io.InputStream r4 = r2.getInputStream()     // Catch:{ Exception -> 0x0111, all -> 0x010c }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.io.InputStreamReader r6 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r6.<init>(r4)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x00eb, all -> 0x00e8 }
            java.lang.StringBuffer r1 = new java.lang.StringBuffer     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
            r1.<init>()     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
        L_0x00b3:
            int r6 = r5.read()     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
            if (r6 == r8) goto L_0x00be
            char r6 = (char) r6     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
            r1.append(r6)     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
            goto L_0x00b3
        L_0x00be:
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x00e6, all -> 0x00e4 }
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            r1.<init>(r0)     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            java.lang.String r0 = "status"
            boolean r0 = r1.has(r0)     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            if (r0 == 0) goto L_0x00e2
            java.lang.String r0 = "status"
            int r0 = r1.getInt(r0)     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            if (r0 != 0) goto L_0x00e2
            boolean r0 = r10.exists()     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            if (r0 == 0) goto L_0x00e2
            r10.delete()     // Catch:{ Exception -> 0x00e1, all -> 0x00e4 }
            goto L_0x00e2
        L_0x00e1:
            r10 = move-exception
        L_0x00e2:
            r1 = r5
            goto L_0x00ef
        L_0x00e4:
            r10 = move-exception
            goto L_0x010f
        L_0x00e6:
            r10 = move-exception
            goto L_0x0114
        L_0x00e8:
            r10 = move-exception
            r5 = r1
            goto L_0x010f
        L_0x00eb:
            r10 = move-exception
            r5 = r1
            goto L_0x0114
        L_0x00ee:
            r4 = r1
        L_0x00ef:
            if (r3 == 0) goto L_0x00f6
            r3.close()     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00f6
        L_0x00f5:
            r10 = move-exception
        L_0x00f6:
            if (r4 == 0) goto L_0x0102
            if (r1 == 0) goto L_0x0102
            r4.close()     // Catch:{ Exception -> 0x0101 }
            r1.close()     // Catch:{ Exception -> 0x0101 }
            goto L_0x0102
        L_0x0101:
            r10 = move-exception
        L_0x0102:
            if (r2 == 0) goto L_0x0109
            r2.disconnect()     // Catch:{ Exception -> 0x0108 }
            goto L_0x0109
        L_0x0108:
            r10 = move-exception
        L_0x0109:
            r10 = 1
            monitor-exit(r9)
            return r10
        L_0x010c:
            r10 = move-exception
            r4 = r1
            r5 = r4
        L_0x010f:
            r1 = r3
            goto L_0x0120
        L_0x0111:
            r10 = move-exception
            r4 = r1
            r5 = r4
        L_0x0114:
            r1 = r3
            goto L_0x013f
        L_0x0116:
            r10 = move-exception
            r4 = r1
            goto L_0x011f
        L_0x0119:
            r10 = move-exception
            r4 = r1
            goto L_0x013e
        L_0x011c:
            r10 = move-exception
            r2 = r1
            r4 = r2
        L_0x011f:
            r5 = r4
        L_0x0120:
            if (r1 == 0) goto L_0x0127
            r1.close()     // Catch:{ Exception -> 0x0126 }
            goto L_0x0127
        L_0x0126:
            r0 = move-exception
        L_0x0127:
            if (r4 == 0) goto L_0x0133
            if (r5 == 0) goto L_0x0133
            r4.close()     // Catch:{ Exception -> 0x0132 }
            r5.close()     // Catch:{ Exception -> 0x0132 }
            goto L_0x0133
        L_0x0132:
            r0 = move-exception
        L_0x0133:
            if (r2 == 0) goto L_0x013a
            r2.disconnect()     // Catch:{ Exception -> 0x0139 }
            goto L_0x013a
        L_0x0139:
            r0 = move-exception
        L_0x013a:
            throw r10     // Catch:{ all -> 0x0145 }
        L_0x013b:
            r10 = move-exception
            r2 = r1
            r4 = r2
        L_0x013e:
            r5 = r4
        L_0x013f:
            if (r1 == 0) goto L_0x0148
            r1.close()     // Catch:{ Exception -> 0x0147 }
            goto L_0x0148
        L_0x0145:
            r10 = move-exception
            goto L_0x015a
        L_0x0147:
            r10 = move-exception
        L_0x0148:
            if (r4 == 0) goto L_0x0154
            if (r5 == 0) goto L_0x0154
            r4.close()     // Catch:{ Exception -> 0x0153 }
            r5.close()     // Catch:{ Exception -> 0x0153 }
            goto L_0x0154
        L_0x0153:
            r10 = move-exception
        L_0x0154:
            if (r2 == 0) goto L_0x015d
            r2.disconnect()     // Catch:{ Exception -> 0x015c }
            goto L_0x015d
        L_0x015a:
            monitor-exit(r9)
            throw r10
        L_0x015c:
            r10 = move-exception
        L_0x015d:
            monitor-exit(r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.b.a.c.a(java.io.File):boolean");
    }

    private byte[] a(byte[] bArr) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
        a((InputStream) byteArrayInputStream, (OutputStream) byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        byteArrayInputStream.close();
        return byteArray;
    }

    private StringBuilder b(File file) {
        String[] split = file.getName().substring(0, file.getName().length() - 4).split("_");
        StringBuilder sb = new StringBuilder();
        sb.append("--bd_map_sdk_cc");
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"phoneinfo\"\r\n");
        sb.append("\r\n");
        sb.append(URLDecoder.decode(SyncSysInfo.getPhoneInfo() + "&abi=" + c));
        sb.append("\r\n");
        sb.append("--bd_map_sdk_cc");
        sb.append("\r\n");
        if (split[0] != null && !split[0].isEmpty()) {
            sb.append("Content-Disposition: form-data; name=\"packname\"\r\n");
            sb.append("\r\n");
            sb.append(split[0]);
            sb.append("\r\n");
            sb.append("--bd_map_sdk_cc");
            sb.append("\r\n");
        }
        if (split[1] != null && !split[1].isEmpty()) {
            sb.append("Content-Disposition: form-data; name=\"version\"\r\n");
            sb.append("\r\n");
            sb.append(split[1]);
            sb.append("\r\n");
            sb.append("--bd_map_sdk_cc");
            sb.append("\r\n");
        }
        if (split[2] != null && !split[2].isEmpty()) {
            sb.append("Content-Disposition: form-data; name=\"timestamp\"\r\n");
            sb.append("\r\n");
            sb.append(split[2]);
            sb.append("\r\n");
            sb.append("--bd_map_sdk_cc");
            sb.append("\r\n");
        }
        sb.append("Content-Disposition: form-data; name=\"os\"\r\n");
        sb.append("\r\n");
        sb.append("android");
        sb.append("\r\n");
        sb.append("--bd_map_sdk_cc");
        sb.append("\r\n");
        return sb;
    }

    private void d() {
        if (g.a().b() != null) {
            String b2 = g.a().b().b();
            if (!b2.isEmpty()) {
                String str = b2 + File.separator + "crash";
                File file = new File(str);
                if (!file.exists() && !file.mkdir()) {
                    a = b2;
                } else {
                    a = str;
                }
            }
        }
    }

    private void e() {
        String str;
        String str2 = a;
        if (str2 != null && !str2.isEmpty() && (str = b) != null && !str.isEmpty()) {
            String str3 = a + File.separator + b;
            a.a().a(str3);
            JNIHandler.registerNativeHandler(str3);
        }
    }

    private void f() {
        if (NetworkUtil.isNetworkAvailable(this.d)) {
            new Thread(new d(this)).start();
        }
    }

    private HttpURLConnection g() {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://api.map.baidu.com/lbs_sdkcc/report").openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "keep-alive");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=bd_map_sdk_cc");
            httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
            httpURLConnection.setRequestProperty("Content-Encoding", "gzip");
            httpURLConnection.setConnectTimeout(10000);
            return httpURLConnection;
        } catch (Exception e) {
            return null;
        }
    }

    public void a(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.SUPPORTED_ABIS.length > 0) {
                c = Build.SUPPORTED_ABIS[0];
            }
            this.d = context;
            String n = h.n();
            if (!n.isEmpty()) {
                if (n.contains("_")) {
                    n = n.replaceAll("_", "");
                }
                b = n + "_" + h.i() + "_";
                d();
                e();
                f();
            }
        }
    }
}
