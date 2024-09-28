package com.baidu.location.b;

import com.baidu.location.g.j;
import com.baidu.location.g.k;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class g {
    private static g a = null;
    private static String b = "Temp_in.dat";
    private static File c = new File(j.a, b);
    private static StringBuffer d = null;
    private static boolean e = true;
    private static int f = 0;
    private static int g = 0;
    private static long h = 0;
    private static long i = 0;
    private static long j = 0;
    private static double k = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    private static double l = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    private static int m = 0;
    private static int n = 0;
    private static int o = 0;

    public static String a() {
        int i2;
        File file = c;
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(c, "rw");
            randomAccessFile.seek(0);
            int readInt = randomAccessFile.readInt();
            int readInt2 = randomAccessFile.readInt();
            int readInt3 = randomAccessFile.readInt();
            if (!a(readInt, readInt2, readInt3)) {
                randomAccessFile.close();
                c();
                return null;
            }
            if (readInt2 != 0) {
                if (readInt2 != readInt3) {
                    long j2 = ((long) (((readInt2 - 1) * 1024) + 12)) + 0;
                    randomAccessFile.seek(j2);
                    int readInt4 = randomAccessFile.readInt();
                    byte[] bArr = new byte[readInt4];
                    randomAccessFile.seek(j2 + 4);
                    for (int i3 = 0; i3 < readInt4; i3++) {
                        bArr[i3] = randomAccessFile.readByte();
                    }
                    String str = new String(bArr);
                    int i4 = 1;
                    if (readInt < k.af) {
                        i2 = readInt2 + 1;
                    } else {
                        if (readInt2 != k.af) {
                            i4 = 1 + readInt2;
                        }
                        i2 = i4;
                    }
                    randomAccessFile.seek(4);
                    randomAccessFile.writeInt(i2);
                    randomAccessFile.close();
                    return str;
                }
            }
            randomAccessFile.close();
            return null;
        } catch (IOException e2) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x000a, code lost:
        r2 = r2 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean a(int r2, int r3, int r4) {
        /*
            r0 = 0
            if (r2 < 0) goto L_0x0019
            int r1 = com.baidu.location.g.k.af
            if (r2 <= r1) goto L_0x0008
            goto L_0x0019
        L_0x0008:
            if (r3 < 0) goto L_0x0019
            r1 = 1
            int r2 = r2 + r1
            if (r3 <= r2) goto L_0x000f
            goto L_0x0019
        L_0x000f:
            if (r4 < r1) goto L_0x0019
            if (r4 > r2) goto L_0x0019
            int r2 = com.baidu.location.g.k.af
            if (r4 <= r2) goto L_0x0018
            goto L_0x0019
        L_0x0018:
            return r1
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.g.a(int, int, int):boolean");
    }

    private static void b() {
        e = true;
        d = null;
        f = 0;
        g = 0;
        h = 0;
        i = 0;
        j = 0;
        k = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        l = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        m = 0;
        n = 0;
        o = 0;
    }

    private static boolean c() {
        if (c.exists()) {
            c.delete();
        }
        if (!c.getParentFile().exists()) {
            c.getParentFile().mkdirs();
        }
        try {
            c.createNewFile();
            RandomAccessFile randomAccessFile = new RandomAccessFile(c, "rw");
            randomAccessFile.seek(0);
            randomAccessFile.writeInt(0);
            randomAccessFile.writeInt(0);
            randomAccessFile.writeInt(1);
            randomAccessFile.close();
            b();
            return c.exists();
        } catch (IOException e2) {
            return false;
        }
    }
}
