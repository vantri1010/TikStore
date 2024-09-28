package com.baidu.mapsdkplatform.comapi;

import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class NativeLoader {
    private static final String a = NativeLoader.class.getSimpleName();
    private static Context b;
    private static final Set<String> c = new HashSet();
    private static final Set<String> d = new HashSet();
    private static NativeLoader e;
    private static a f = a.ARMEABI;
    private static boolean g = false;
    private static String h = null;

    private enum a {
        ARMEABI("armeabi"),
        ARMV7("armeabi-v7a"),
        ARM64("arm64-v8a"),
        X86("x86"),
        X86_64("x86_64");
        
        private String f;

        private a(String str) {
            this.f = str;
        }

        public String a() {
            return this.f;
        }
    }

    private NativeLoader() {
    }

    private String a() {
        return 8 <= Build.VERSION.SDK_INT ? b.getPackageCodePath() : "";
    }

    private String a(a aVar) {
        return "lib/" + aVar.a() + "/";
    }

    private void a(InputStream inputStream, FileOutputStream fileOutputStream) throws IOException {
        byte[] bArr = new byte[4096];
        while (true) {
            try {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    Log.e(a, "Close InputStream error", e2);
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e3) {
                    Log.e(a, "Close OutputStream error", e3);
                }
            }
        }
        fileOutputStream.flush();
    }

    private void a(Throwable th) {
        Log.e(a, "loadException", th);
        for (String str : d) {
            String str2 = a;
            Log.e(str2, str + " Failed to load.");
        }
    }

    static void a(boolean z, String str) {
        g = z;
        h = str;
    }

    private boolean a(String str) {
        try {
            synchronized (c) {
                if (c.contains(str)) {
                    return true;
                }
                System.loadLibrary(str);
                synchronized (c) {
                    c.add(str);
                }
                return true;
            }
        } catch (Throwable th) {
            return b(str);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x008e A[SYNTHETIC, Splitter:B:39:0x008e] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x009b A[SYNTHETIC, Splitter:B:45:0x009b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(java.lang.String r8, com.baidu.mapsdkplatform.comapi.NativeLoader.a r9) {
        /*
            r7 = this;
            java.lang.String r0 = "Release file failed"
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r7.b()
            r1.<init>(r2, r8)
            boolean r2 = r1.exists()
            r3 = 1
            if (r2 == 0) goto L_0x001d
            long r1 = r1.length()
            r4 = 0
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x001d
            return r3
        L_0x001d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r9 = r7.a((com.baidu.mapsdkplatform.comapi.NativeLoader.a) r9)
            r1.append(r9)
            r1.append(r8)
            java.lang.String r9 = r1.toString()
            r1 = 0
            boolean r2 = g
            if (r2 != 0) goto L_0x003a
            java.lang.String r2 = r7.a()
            goto L_0x003c
        L_0x003a:
            java.lang.String r2 = h
        L_0x003c:
            r4 = 0
            if (r2 == 0) goto L_0x00a6
            boolean r5 = r2.isEmpty()
            if (r5 == 0) goto L_0x0046
            goto L_0x00a6
        L_0x0046:
            java.util.zip.ZipFile r5 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x0084 }
            r5.<init>(r2)     // Catch:{ Exception -> 0x0084 }
            java.util.zip.ZipEntry r9 = r5.getEntry(r9)     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            if (r9 != 0) goto L_0x005c
            r5.close()     // Catch:{ IOException -> 0x0055 }
            goto L_0x005b
        L_0x0055:
            r8 = move-exception
            java.lang.String r9 = a
            android.util.Log.e(r9, r0, r8)
        L_0x005b:
            return r4
        L_0x005c:
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            java.lang.String r2 = r7.b()     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            r1.<init>(r2, r8)     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            java.io.InputStream r8 = r5.getInputStream(r9)     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            r9.<init>(r1)     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            r7.a((java.io.InputStream) r8, (java.io.FileOutputStream) r9)     // Catch:{ Exception -> 0x007f, all -> 0x007c }
            r5.close()     // Catch:{ IOException -> 0x0075 }
            goto L_0x007b
        L_0x0075:
            r8 = move-exception
            java.lang.String r9 = a
            android.util.Log.e(r9, r0, r8)
        L_0x007b:
            return r3
        L_0x007c:
            r8 = move-exception
            r1 = r5
            goto L_0x0099
        L_0x007f:
            r8 = move-exception
            r1 = r5
            goto L_0x0085
        L_0x0082:
            r8 = move-exception
            goto L_0x0099
        L_0x0084:
            r8 = move-exception
        L_0x0085:
            java.lang.String r9 = a     // Catch:{ all -> 0x0082 }
            java.lang.String r2 = "Copy library file error"
            android.util.Log.e(r9, r2, r8)     // Catch:{ all -> 0x0082 }
            if (r1 == 0) goto L_0x0098
            r1.close()     // Catch:{ IOException -> 0x0092 }
            goto L_0x0098
        L_0x0092:
            r8 = move-exception
            java.lang.String r9 = a
            android.util.Log.e(r9, r0, r8)
        L_0x0098:
            return r4
        L_0x0099:
            if (r1 == 0) goto L_0x00a5
            r1.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x00a5
        L_0x009f:
            r9 = move-exception
            java.lang.String r1 = a
            android.util.Log.e(r1, r0, r9)
        L_0x00a5:
            throw r8
        L_0x00a6:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.NativeLoader.a(java.lang.String, com.baidu.mapsdkplatform.comapi.NativeLoader$a):boolean");
    }

    private boolean a(String str, String str2) {
        return !a(str2, a.ARMV7) ? b(str, str2) : f(str2, str);
    }

    private String b() {
        File file = new File(b.getFilesDir(), "libs" + File.separator + f.a());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0020, code lost:
        if (r1 == 2) goto L_0x003c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        if (r1 == 3) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0026, code lost:
        if (r1 == 4) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0029, code lost:
        if (r1 == 5) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002d, code lost:
        r0 = d(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0032, code lost:
        r0 = e(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0037, code lost:
        r0 = b(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003c, code lost:
        r0 = a(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0041, code lost:
        r0 = c(r5, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0045, code lost:
        r2 = c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0047, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        c.add(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004d, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004e, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r1 = com.baidu.mapsdkplatform.comapi.e.a[f.ordinal()];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001d, code lost:
        if (r1 == 1) goto L_0x0041;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean b(java.lang.String r5) {
        /*
            r4 = this;
            java.lang.String r0 = java.lang.System.mapLibraryName(r5)
            java.util.Set<java.lang.String> r1 = c
            monitor-enter(r1)
            java.util.Set<java.lang.String> r2 = c     // Catch:{ all -> 0x0052 }
            boolean r2 = r2.contains(r5)     // Catch:{ all -> 0x0052 }
            r3 = 1
            if (r2 == 0) goto L_0x0012
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            return r3
        L_0x0012:
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            int[] r1 = com.baidu.mapsdkplatform.comapi.e.a
            com.baidu.mapsdkplatform.comapi.NativeLoader$a r2 = f
            int r2 = r2.ordinal()
            r1 = r1[r2]
            if (r1 == r3) goto L_0x0041
            r2 = 2
            if (r1 == r2) goto L_0x003c
            r2 = 3
            if (r1 == r2) goto L_0x0037
            r2 = 4
            if (r1 == r2) goto L_0x0032
            r2 = 5
            if (r1 == r2) goto L_0x002d
            r0 = 0
            goto L_0x0045
        L_0x002d:
            boolean r0 = r4.d(r5, r0)
            goto L_0x0045
        L_0x0032:
            boolean r0 = r4.e(r5, r0)
            goto L_0x0045
        L_0x0037:
            boolean r0 = r4.b(r5, r0)
            goto L_0x0045
        L_0x003c:
            boolean r0 = r4.a((java.lang.String) r5, (java.lang.String) r0)
            goto L_0x0045
        L_0x0041:
            boolean r0 = r4.c(r5, r0)
        L_0x0045:
            java.util.Set<java.lang.String> r2 = c
            monitor-enter(r2)
            java.util.Set<java.lang.String> r1 = c     // Catch:{ all -> 0x004f }
            r1.add(r5)     // Catch:{ all -> 0x004f }
            monitor-exit(r2)     // Catch:{ all -> 0x004f }
            return r0
        L_0x004f:
            r5 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004f }
            throw r5
        L_0x0052:
            r5 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0052 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.NativeLoader.b(java.lang.String):boolean");
    }

    private boolean b(String str, String str2) {
        if (a(str2, a.ARMEABI)) {
            return f(str2, str);
        }
        String str3 = a;
        Log.e(str3, "found lib " + a.ARMEABI.a() + "/" + str + ".so error");
        return false;
    }

    private static a c() {
        String str = Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0];
        if (str == null) {
            return a.ARMEABI;
        }
        if (str.contains("arm") && str.contains("v7")) {
            f = a.ARMV7;
        }
        if (str.contains("arm") && str.contains("64") && d()) {
            f = a.ARM64;
        }
        if (str.contains("x86")) {
            f = str.contains("64") ? a.X86_64 : a.X86;
        }
        return f;
    }

    private boolean c(String str, String str2) {
        return !a(str2, a.ARM64) ? a(str, str2) : f(str2, str);
    }

    private static boolean d() {
        if (Build.VERSION.SDK_INT >= 23) {
            return Process.is64Bit();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return Build.CPU_ABI.equals(Build.SUPPORTED_64_BIT_ABIS[0]);
        }
        return false;
    }

    private boolean d(String str, String str2) {
        return !a(str2, a.X86) ? a(str, str2) : f(str2, str);
    }

    private boolean e(String str, String str2) {
        return !a(str2, a.X86_64) ? d(str, str2) : f(str2, str);
    }

    private boolean f(String str, String str2) {
        try {
            System.load(new File(b(), str).getAbsolutePath());
            synchronized (c) {
                c.add(str2);
            }
            g(str, str2);
            return true;
        } catch (Throwable th) {
            synchronized (d) {
                d.add(str2);
                a(th);
                return false;
            }
        }
    }

    private void g(String str, String str2) {
        if (str != null && !str.isEmpty() && str.contains("libBaiduMapSDK_")) {
            try {
                String[] split = str.split("_v");
                if (split.length > 1) {
                    File[] listFiles = new File(b()).listFiles(new d(this, split[1]));
                    if (listFiles != null && listFiles.length != 0) {
                        for (File delete : listFiles) {
                            delete.delete();
                        }
                    }
                }
            } catch (Exception e2) {
            }
        }
    }

    public static synchronized NativeLoader getInstance() {
        NativeLoader nativeLoader;
        synchronized (NativeLoader.class) {
            if (e == null) {
                e = new NativeLoader();
                f = c();
            }
            nativeLoader = e;
        }
        return nativeLoader;
    }

    public static void setContext(Context context) {
        b = context;
    }

    public synchronized boolean loadLibrary(String str) {
        if (!g) {
            return a(str);
        } else if (h == null || h.isEmpty()) {
            Log.e(a, "Given custom so file path is null, please check!");
            return false;
        } else {
            return b(str);
        }
    }
}
