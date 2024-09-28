package io.openinstall.sdk;

import java.io.File;

public class u {
    public static void a(File file) {
        try {
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
        } catch (Exception e) {
        }
    }

    public static void a(File file, String str, boolean z) {
        a(file, str, false, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0030 A[SYNTHETIC, Splitter:B:23:0x0030] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0038 A[Catch:{ IOException -> 0x0034 }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0040 A[SYNTHETIC, Splitter:B:33:0x0040] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(java.io.File r2, java.lang.String r3, boolean r4, boolean r5) {
        /*
            a(r2)
            r0 = 0
            java.io.FileWriter r1 = new java.io.FileWriter     // Catch:{ IOException -> 0x003c, all -> 0x002c }
            r1.<init>(r2, r5)     // Catch:{ IOException -> 0x003c, all -> 0x002c }
            java.io.BufferedWriter r2 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x002a, all -> 0x0028 }
            r2.<init>(r1)     // Catch:{ IOException -> 0x002a, all -> 0x0028 }
            r2.write(r3)     // Catch:{ IOException -> 0x0025, all -> 0x0022 }
            if (r4 == 0) goto L_0x0016
            r2.newLine()     // Catch:{ IOException -> 0x0025, all -> 0x0022 }
        L_0x0016:
            r2.flush()     // Catch:{ IOException -> 0x0025, all -> 0x0022 }
            r2.close()     // Catch:{ IOException -> 0x0020 }
        L_0x001c:
            r1.close()     // Catch:{ IOException -> 0x0020 }
            goto L_0x0046
        L_0x0020:
            r2 = move-exception
            goto L_0x0046
        L_0x0022:
            r3 = move-exception
            r0 = r2
            goto L_0x002e
        L_0x0025:
            r3 = move-exception
            r0 = r2
            goto L_0x003e
        L_0x0028:
            r3 = move-exception
            goto L_0x002e
        L_0x002a:
            r2 = move-exception
            goto L_0x003e
        L_0x002c:
            r3 = move-exception
            r1 = r0
        L_0x002e:
            if (r0 == 0) goto L_0x0036
            r0.close()     // Catch:{ IOException -> 0x0034 }
            goto L_0x0036
        L_0x0034:
            r2 = move-exception
            goto L_0x003b
        L_0x0036:
            if (r1 == 0) goto L_0x003b
            r1.close()     // Catch:{ IOException -> 0x0034 }
        L_0x003b:
            throw r3
        L_0x003c:
            r2 = move-exception
            r1 = r0
        L_0x003e:
            if (r0 == 0) goto L_0x0043
            r0.close()     // Catch:{ IOException -> 0x0020 }
        L_0x0043:
            if (r1 == 0) goto L_0x0046
            goto L_0x001c
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.u.a(java.io.File, java.lang.String, boolean, boolean):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0046, code lost:
        if (r3 != null) goto L_0x001d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0033 A[SYNTHETIC, Splitter:B:24:0x0033] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x003b A[Catch:{ IOException -> 0x0037 }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0043 A[SYNTHETIC, Splitter:B:34:0x0043] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String b(java.io.File r3) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 0
            java.io.FileReader r2 = new java.io.FileReader     // Catch:{ IOException -> 0x003f, all -> 0x002f }
            r2.<init>(r3)     // Catch:{ IOException -> 0x003f, all -> 0x002f }
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ IOException -> 0x002b, all -> 0x0027 }
            r3.<init>(r2)     // Catch:{ IOException -> 0x002b, all -> 0x0027 }
        L_0x0010:
            java.lang.String r1 = r3.readLine()     // Catch:{ IOException -> 0x0025, all -> 0x0023 }
            if (r1 == 0) goto L_0x001a
            r0.append(r1)     // Catch:{ IOException -> 0x0025, all -> 0x0023 }
            goto L_0x0010
        L_0x001a:
            r2.close()     // Catch:{ IOException -> 0x0021 }
        L_0x001d:
            r3.close()     // Catch:{ IOException -> 0x0021 }
            goto L_0x0049
        L_0x0021:
            r3 = move-exception
            goto L_0x0049
        L_0x0023:
            r0 = move-exception
            goto L_0x0029
        L_0x0025:
            r1 = move-exception
            goto L_0x002d
        L_0x0027:
            r0 = move-exception
            r3 = r1
        L_0x0029:
            r1 = r2
            goto L_0x0031
        L_0x002b:
            r3 = move-exception
            r3 = r1
        L_0x002d:
            r1 = r2
            goto L_0x0041
        L_0x002f:
            r0 = move-exception
            r3 = r1
        L_0x0031:
            if (r1 == 0) goto L_0x0039
            r1.close()     // Catch:{ IOException -> 0x0037 }
            goto L_0x0039
        L_0x0037:
            r3 = move-exception
            goto L_0x003e
        L_0x0039:
            if (r3 == 0) goto L_0x003e
            r3.close()     // Catch:{ IOException -> 0x0037 }
        L_0x003e:
            throw r0
        L_0x003f:
            r3 = move-exception
            r3 = r1
        L_0x0041:
            if (r1 == 0) goto L_0x0046
            r1.close()     // Catch:{ IOException -> 0x0021 }
        L_0x0046:
            if (r3 == 0) goto L_0x0049
            goto L_0x001d
        L_0x0049:
            java.lang.String r3 = r0.toString()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.u.b(java.io.File):java.lang.String");
    }
}
