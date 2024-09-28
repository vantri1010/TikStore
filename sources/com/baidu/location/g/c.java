package com.baidu.location.g;

import java.io.File;
import java.io.RandomAccessFile;

public class c {
    static c c;
    String a = "firll.dat";
    int b = 3164;
    int d = 0;
    int e = 20;
    int f = 40;
    int g = 60;
    int h = 80;
    int i = 100;

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004e A[SYNTHETIC, Splitter:B:23:0x004e] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0057 A[SYNTHETIC, Splitter:B:29:0x0057] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long a(int r8) {
        /*
            r7 = this;
            java.lang.String r0 = com.baidu.location.g.k.j()
            r1 = -1
            if (r0 != 0) goto L_0x0009
            return r1
        L_0x0009:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            java.lang.String r0 = java.io.File.separator
            r3.append(r0)
            java.lang.String r0 = r7.a
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r3 = 0
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0054, all -> 0x004b }
            java.lang.String r5 = "rw"
            r4.<init>(r0, r5)     // Catch:{ Exception -> 0x0054, all -> 0x004b }
            long r5 = (long) r8
            r4.seek(r5)     // Catch:{ Exception -> 0x0048, all -> 0x0045 }
            int r8 = r4.readInt()     // Catch:{ Exception -> 0x0048, all -> 0x0045 }
            long r5 = r4.readLong()     // Catch:{ Exception -> 0x0048, all -> 0x0045 }
            int r0 = r4.readInt()     // Catch:{ Exception -> 0x0048, all -> 0x0045 }
            if (r8 != r0) goto L_0x003f
            r4.close()     // Catch:{ IOException -> 0x003d }
            goto L_0x003e
        L_0x003d:
            r8 = move-exception
        L_0x003e:
            return r5
        L_0x003f:
            r4.close()     // Catch:{ IOException -> 0x0043 }
            goto L_0x005a
        L_0x0043:
            r8 = move-exception
            goto L_0x005a
        L_0x0045:
            r8 = move-exception
            r3 = r4
            goto L_0x004c
        L_0x0048:
            r8 = move-exception
            r3 = r4
            goto L_0x0055
        L_0x004b:
            r8 = move-exception
        L_0x004c:
            if (r3 == 0) goto L_0x0053
            r3.close()     // Catch:{ IOException -> 0x0052 }
            goto L_0x0053
        L_0x0052:
            r0 = move-exception
        L_0x0053:
            throw r8
        L_0x0054:
            r8 = move-exception
        L_0x0055:
            if (r3 == 0) goto L_0x005a
            r3.close()     // Catch:{ IOException -> 0x0043 }
        L_0x005a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.c.a(int):long");
    }

    public static c a() {
        if (c == null) {
            c = new c();
        }
        return c;
    }

    private void a(int i2, long j) {
        String j2 = k.j();
        if (j2 != null) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(j2 + File.separator + this.a, "rw");
                randomAccessFile.seek((long) i2);
                randomAccessFile.writeInt(this.b);
                randomAccessFile.writeLong(j);
                randomAccessFile.writeInt(this.b);
                randomAccessFile.close();
            } catch (Exception e2) {
            }
        }
    }

    public void a(long j) {
        a(this.d, j);
    }

    public long b() {
        return a(this.d);
    }

    public void b(long j) {
        a(this.e, j);
    }

    public long c() {
        return a(this.e);
    }

    public void c(long j) {
        a(this.g, j);
    }

    public long d() {
        return a(this.g);
    }
}
