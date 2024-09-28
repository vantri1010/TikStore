package com.baidu.location.b;

import android.location.Location;
import android.os.Build;
import com.baidu.location.e.f;
import com.baidu.location.e.i;
import com.baidu.location.g.e;
import com.baidu.location.g.j;
import com.baidu.location.g.k;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;

public class w {
    private static w A = null;
    private static long C = 0;
    private static ArrayList<String> b = new ArrayList<>();
    private static ArrayList<String> c = new ArrayList<>();
    private static ArrayList<String> d = new ArrayList<>();
    private static String e = (j.a + "/yo.dat");
    private static String f = (j.a + "/yoh.dat");
    private static String g = (j.a + "/yom.dat");
    private static String h = (j.a + "/yol.dat");
    private static String i = (j.a + "/yor.dat");
    private static File j = null;
    private static int k = 8;
    private static int l = 8;
    private static int m = 16;
    private static int n = 2048;
    private static double o = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    private static double p = 0.1d;
    private static double q = 30.0d;
    private static double r = 100.0d;
    private static int s = 0;
    private static int t = 64;
    private static int u = 128;
    private static Location v = null;
    private static Location w = null;
    private static Location x = null;
    private static i y = null;
    private int B;
    long a;
    private a z;

    private class a extends e {
        boolean a = false;
        int b = 0;
        int c = 0;
        private ArrayList<String> e = new ArrayList<>();
        private boolean f = true;

        public a() {
            this.k = new HashMap();
        }

        public void a() {
            String str;
            StringBuilder sb;
            Map map;
            this.h = k.e();
            if (this.b != 1) {
                this.h = k.g();
            }
            this.i = 2;
            if (this.e != null) {
                for (int i = 0; i < this.e.size(); i++) {
                    if (this.b == 1) {
                        map = this.k;
                        sb = new StringBuilder();
                        str = "cldc[";
                    } else {
                        map = this.k;
                        sb = new StringBuilder();
                        str = "cltr[";
                    }
                    sb.append(str);
                    sb.append(i);
                    sb.append("]");
                    map.put(sb.toString(), this.e.get(i));
                }
                this.k.put("trtm", String.format(Locale.CHINA, "%d", new Object[]{Long.valueOf(System.currentTimeMillis())}));
                if (this.b != 1) {
                    this.k.put("qt", "cltrg");
                }
            }
        }

        public void a(boolean z) {
            if (z && this.j != null) {
                ArrayList<String> arrayList = this.e;
                if (arrayList != null) {
                    arrayList.clear();
                }
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    if (jSONObject.has("ison") && jSONObject.getInt("ison") == 0) {
                        this.f = false;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
            this.a = false;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:65:0x00b2, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void b() {
            /*
                r7 = this;
                monitor-enter(r7)
                boolean r0 = r7.a     // Catch:{ all -> 0x00cc }
                if (r0 == 0) goto L_0x0007
                monitor-exit(r7)
                return
            L_0x0007:
                int r0 = p     // Catch:{ all -> 0x00cc }
                r1 = 4
                r2 = 1
                if (r0 <= r1) goto L_0x001a
                int r0 = r7.c     // Catch:{ all -> 0x00cc }
                int r1 = p     // Catch:{ all -> 0x00cc }
                if (r0 >= r1) goto L_0x001a
                int r0 = r7.c     // Catch:{ all -> 0x00cc }
                int r0 = r0 + r2
                r7.c = r0     // Catch:{ all -> 0x00cc }
                monitor-exit(r7)
                return
            L_0x001a:
                r0 = 0
                r7.c = r0     // Catch:{ all -> 0x00cc }
                r7.a = r2     // Catch:{ all -> 0x00cc }
                r7.b = r0     // Catch:{ all -> 0x00cc }
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                if (r1 == 0) goto L_0x002d
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                int r1 = r1.size()     // Catch:{ Exception -> 0x00c0 }
                if (r1 >= r2) goto L_0x0076
            L_0x002d:
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                if (r1 != 0) goto L_0x0038
                java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x00c0 }
                r1.<init>()     // Catch:{ Exception -> 0x00c0 }
                r7.e = r1     // Catch:{ Exception -> 0x00c0 }
            L_0x0038:
                r7.b = r0     // Catch:{ Exception -> 0x00c0 }
                r1 = 0
            L_0x003b:
                int r3 = r7.b     // Catch:{ Exception -> 0x00c0 }
                r4 = 2
                r5 = 0
                if (r3 >= r4) goto L_0x0046
                java.lang.String r3 = com.baidu.location.b.w.b()     // Catch:{ Exception -> 0x00c0 }
                goto L_0x0047
            L_0x0046:
                r3 = r5
            L_0x0047:
                if (r3 != 0) goto L_0x005a
                int r6 = r7.b     // Catch:{ Exception -> 0x00c0 }
                if (r6 == r2) goto L_0x005a
                boolean r6 = r7.f     // Catch:{ Exception -> 0x00c0 }
                if (r6 == 0) goto L_0x005a
                r7.b = r4     // Catch:{ Exception -> 0x00c0 }
                java.lang.String r5 = com.baidu.location.b.g.a()     // Catch:{ Exception -> 0x0058 }
                goto L_0x005d
            L_0x0058:
                r3 = move-exception
                goto L_0x005d
            L_0x005a:
                r7.b = r2     // Catch:{ Exception -> 0x00c0 }
                r5 = r3
            L_0x005d:
                if (r5 != 0) goto L_0x0060
                goto L_0x0076
            L_0x0060:
                java.lang.String r3 = "err!"
                boolean r3 = r5.contains(r3)     // Catch:{ Exception -> 0x00c0 }
                if (r3 != 0) goto L_0x003b
                java.util.ArrayList<java.lang.String> r3 = r7.e     // Catch:{ Exception -> 0x00c0 }
                r3.add(r5)     // Catch:{ Exception -> 0x00c0 }
                int r3 = r5.length()     // Catch:{ Exception -> 0x00c0 }
                int r1 = r1 + r3
                int r3 = com.baidu.location.g.a.i     // Catch:{ Exception -> 0x00c0 }
                if (r1 < r3) goto L_0x003b
            L_0x0076:
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                if (r1 == 0) goto L_0x00b3
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                int r1 = r1.size()     // Catch:{ Exception -> 0x00c0 }
                if (r1 >= r2) goto L_0x0083
                goto L_0x00b3
            L_0x0083:
                int r0 = r7.b     // Catch:{ all -> 0x00cc }
                if (r0 == r2) goto L_0x00a1
                com.baidu.location.b.v r0 = com.baidu.location.b.v.a()     // Catch:{ all -> 0x00cc }
                java.util.concurrent.ExecutorService r0 = r0.c()     // Catch:{ all -> 0x00cc }
                if (r0 == 0) goto L_0x0099
                java.lang.String r1 = com.baidu.location.g.k.g()     // Catch:{ all -> 0x00cc }
            L_0x0095:
                r7.a(r0, r1)     // Catch:{ all -> 0x00cc }
                goto L_0x00b1
            L_0x0099:
                java.lang.String r0 = com.baidu.location.g.k.g()     // Catch:{ all -> 0x00cc }
            L_0x009d:
                r7.c(r0)     // Catch:{ all -> 0x00cc }
                goto L_0x00b1
            L_0x00a1:
                com.baidu.location.b.v r0 = com.baidu.location.b.v.a()     // Catch:{ all -> 0x00cc }
                java.util.concurrent.ExecutorService r0 = r0.c()     // Catch:{ all -> 0x00cc }
                if (r0 == 0) goto L_0x00ae
                java.lang.String r1 = com.baidu.location.g.k.f     // Catch:{ all -> 0x00cc }
                goto L_0x0095
            L_0x00ae:
                java.lang.String r0 = com.baidu.location.g.k.f     // Catch:{ all -> 0x00cc }
                goto L_0x009d
            L_0x00b1:
                monitor-exit(r7)
                return
            L_0x00b3:
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                if (r1 == 0) goto L_0x00bc
                java.util.ArrayList<java.lang.String> r1 = r7.e     // Catch:{ Exception -> 0x00c0 }
                r1.clear()     // Catch:{ Exception -> 0x00c0 }
            L_0x00bc:
                r7.a = r0     // Catch:{ Exception -> 0x00c0 }
                monitor-exit(r7)
                return
            L_0x00c0:
                r0 = move-exception
                java.util.ArrayList<java.lang.String> r0 = r7.e     // Catch:{ all -> 0x00cc }
                if (r0 == 0) goto L_0x00ca
                java.util.ArrayList<java.lang.String> r0 = r7.e     // Catch:{ all -> 0x00cc }
                r0.clear()     // Catch:{ all -> 0x00cc }
            L_0x00ca:
                monitor-exit(r7)
                return
            L_0x00cc:
                r0 = move-exception
                monitor-exit(r7)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.w.a.b():void");
        }
    }

    private w() {
        String l2;
        this.z = null;
        this.B = 0;
        this.a = 0;
        this.z = new a();
        this.B = 0;
        if (Build.VERSION.SDK_INT > 28 && (l2 = k.l()) != null) {
            e = l2 + "/yo1.dat";
            f = l2 + "/yoh1.dat";
            g = l2 + "/yom1.dat";
            h = l2 + "/yol1.dat";
            i = l2 + "/yor1.dat";
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00d0, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static synchronized int a(java.util.List<java.lang.String> r17, int r18) {
        /*
            r0 = r17
            r1 = r18
            java.lang.Class<com.baidu.location.b.w> r2 = com.baidu.location.b.w.class
            monitor-enter(r2)
            if (r0 == 0) goto L_0x00cf
            r3 = 256(0x100, float:3.59E-43)
            if (r1 > r3) goto L_0x00cf
            if (r1 >= 0) goto L_0x0011
            goto L_0x00cf
        L_0x0011:
            java.io.File r3 = j     // Catch:{ Exception -> 0x00c6 }
            if (r3 != 0) goto L_0x002a
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00c6 }
            java.lang.String r4 = e     // Catch:{ Exception -> 0x00c6 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x00c6 }
            j = r3     // Catch:{ Exception -> 0x00c6 }
            boolean r3 = r3.exists()     // Catch:{ Exception -> 0x00c6 }
            if (r3 != 0) goto L_0x002a
            r0 = 0
            j = r0     // Catch:{ Exception -> 0x00c6 }
            r0 = -2
            monitor-exit(r2)
            return r0
        L_0x002a:
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x00c6 }
            java.io.File r4 = j     // Catch:{ Exception -> 0x00c6 }
            java.lang.String r5 = "rw"
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x00c6 }
            long r4 = r3.length()     // Catch:{ Exception -> 0x00c6 }
            r6 = 1
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x0043
            r3.close()     // Catch:{ Exception -> 0x00c6 }
            r0 = -3
            monitor-exit(r2)
            return r0
        L_0x0043:
            long r4 = (long) r1
            r3.seek(r4)     // Catch:{ Exception -> 0x00c6 }
            int r1 = r3.readInt()     // Catch:{ Exception -> 0x00c6 }
            int r12 = r3.readInt()     // Catch:{ Exception -> 0x00c6 }
            int r13 = r3.readInt()     // Catch:{ Exception -> 0x00c6 }
            int r14 = r3.readInt()     // Catch:{ Exception -> 0x00c6 }
            long r10 = r3.readLong()     // Catch:{ Exception -> 0x00c6 }
            r6 = r1
            r7 = r12
            r8 = r13
            r9 = r14
            r15 = r10
            boolean r6 = a(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x00c6 }
            if (r6 == 0) goto L_0x00be
            r6 = 1
            if (r12 >= r6) goto L_0x006a
            goto L_0x00be
        L_0x006a:
            int r7 = n     // Catch:{ Exception -> 0x00c6 }
            byte[] r7 = new byte[r7]     // Catch:{ Exception -> 0x00c6 }
            int r8 = k     // Catch:{ Exception -> 0x00c6 }
        L_0x0070:
            if (r8 <= 0) goto L_0x00a3
            if (r12 <= 0) goto L_0x00a3
            int r9 = r1 + r12
            int r9 = r9 - r6
            int r9 = r9 % r13
            int r9 = r9 * r14
            long r9 = (long) r9     // Catch:{ Exception -> 0x00c6 }
            r11 = r7
            r6 = r15
            long r9 = r9 + r6
            r3.seek(r9)     // Catch:{ Exception -> 0x00c6 }
            int r9 = r3.readInt()     // Catch:{ Exception -> 0x00c6 }
            if (r9 <= 0) goto L_0x009b
            if (r9 >= r14) goto L_0x009b
            r10 = 0
            r3.read(r11, r10, r9)     // Catch:{ Exception -> 0x00c6 }
            int r9 = r9 + -1
            byte r15 = r11[r9]     // Catch:{ Exception -> 0x00c6 }
            if (r15 != 0) goto L_0x009b
            java.lang.String r15 = new java.lang.String     // Catch:{ Exception -> 0x00c6 }
            r15.<init>(r11, r10, r9)     // Catch:{ Exception -> 0x00c6 }
            r0.add(r15)     // Catch:{ Exception -> 0x00c6 }
        L_0x009b:
            int r8 = r8 + -1
            int r12 = r12 + -1
            r15 = r6
            r7 = r11
            r6 = 1
            goto L_0x0070
        L_0x00a3:
            r6 = r15
            r3.seek(r4)     // Catch:{ Exception -> 0x00c6 }
            r3.writeInt(r1)     // Catch:{ Exception -> 0x00c6 }
            r3.writeInt(r12)     // Catch:{ Exception -> 0x00c6 }
            r3.writeInt(r13)     // Catch:{ Exception -> 0x00c6 }
            r3.writeInt(r14)     // Catch:{ Exception -> 0x00c6 }
            r3.writeLong(r6)     // Catch:{ Exception -> 0x00c6 }
            r3.close()     // Catch:{ Exception -> 0x00c6 }
            int r0 = k     // Catch:{ Exception -> 0x00c6 }
            int r0 = r0 - r8
            monitor-exit(r2)
            return r0
        L_0x00be:
            r3.close()     // Catch:{ Exception -> 0x00c6 }
            r0 = -4
            monitor-exit(r2)
            return r0
        L_0x00c4:
            r0 = move-exception
            goto L_0x00cd
        L_0x00c6:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x00c4 }
            r0 = -5
            monitor-exit(r2)
            return r0
        L_0x00cd:
            monitor-exit(r2)
            throw r0
        L_0x00cf:
            monitor-exit(r2)
            r0 = -1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.w.a(java.util.List, int):int");
    }

    public static synchronized w a() {
        w wVar;
        synchronized (w.class) {
            if (A == null) {
                A = new w();
            }
            wVar = A;
        }
        return wVar;
    }

    private static String a(int i2) {
        String str;
        ArrayList<String> arrayList;
        String str2 = null;
        if (i2 == 1) {
            str = f;
            arrayList = b;
        } else if (i2 == 2) {
            str = g;
            arrayList = c;
        } else {
            if (i2 == 3) {
                str = h;
            } else if (i2 != 4) {
                return null;
            } else {
                str = i;
            }
            arrayList = d;
        }
        if (arrayList == null) {
            return null;
        }
        if (arrayList.size() < 1) {
            a(str, (List<String>) arrayList);
        }
        synchronized (w.class) {
            int size = arrayList.size();
            if (size > 0) {
                int i3 = size - 1;
                try {
                    String str3 = arrayList.get(i3);
                    try {
                        arrayList.remove(i3);
                    } catch (Exception e2) {
                    }
                    str2 = str3;
                } catch (Exception e3) {
                }
            }
        }
        return str2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
        if (r15 != false) goto L_0x0014;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0011, code lost:
        if (r15 != false) goto L_0x0009;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0060 A[Catch:{ Exception -> 0x00e8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00e1 A[ADDED_TO_REGION, Catch:{ Exception -> 0x00e8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ce A[EDGE_INSN: B:44:0x00ce->B:38:0x00ce ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:50:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(int r14, boolean r15) {
        /*
            r0 = 4
            r1 = 1
            if (r14 != r1) goto L_0x000c
            java.lang.String r2 = f
            if (r15 == 0) goto L_0x0009
            return
        L_0x0009:
            java.util.ArrayList<java.lang.String> r3 = b
            goto L_0x0029
        L_0x000c:
            r2 = 2
            if (r14 != r2) goto L_0x0017
            java.lang.String r2 = g
            if (r15 == 0) goto L_0x0014
            goto L_0x0009
        L_0x0014:
            java.util.ArrayList<java.lang.String> r3 = c
            goto L_0x0029
        L_0x0017:
            r2 = 3
            if (r14 != r2) goto L_0x0022
            java.lang.String r2 = h
            if (r15 == 0) goto L_0x001f
            goto L_0x0014
        L_0x001f:
            java.util.ArrayList<java.lang.String> r3 = d
            goto L_0x0029
        L_0x0022:
            if (r14 != r0) goto L_0x00e9
            java.lang.String r2 = i
            if (r15 == 0) goto L_0x00e9
            goto L_0x001f
        L_0x0029:
            java.io.File r4 = new java.io.File
            r4.<init>(r2)
            boolean r5 = r4.exists()
            if (r5 != 0) goto L_0x0037
            d(r2)
        L_0x0037:
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x00e8 }
            java.lang.String r5 = "rw"
            r2.<init>(r4, r5)     // Catch:{ Exception -> 0x00e8 }
            r4 = 4
            r2.seek(r4)     // Catch:{ Exception -> 0x00e8 }
            int r4 = r2.readInt()     // Catch:{ Exception -> 0x00e8 }
            int r5 = r2.readInt()     // Catch:{ Exception -> 0x00e8 }
            int r6 = r2.readInt()     // Catch:{ Exception -> 0x00e8 }
            int r7 = r2.readInt()     // Catch:{ Exception -> 0x00e8 }
            int r8 = r2.readInt()     // Catch:{ Exception -> 0x00e8 }
            int r9 = r3.size()     // Catch:{ Exception -> 0x00e8 }
        L_0x005b:
            int r10 = l     // Catch:{ Exception -> 0x00e8 }
            r11 = 0
            if (r9 <= r10) goto L_0x00ce
            if (r15 == 0) goto L_0x0064
            int r8 = r8 + 1
        L_0x0064:
            if (r6 >= r4) goto L_0x0095
            int r10 = r5 * r6
            int r10 = r10 + 128
            long r12 = (long) r10     // Catch:{ Exception -> 0x00e8 }
            r2.seek(r12)     // Catch:{ Exception -> 0x00e8 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e8 }
            r10.<init>()     // Catch:{ Exception -> 0x00e8 }
            java.lang.Object r12 = r3.get(r11)     // Catch:{ Exception -> 0x00e8 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x00e8 }
            r10.append(r12)     // Catch:{ Exception -> 0x00e8 }
            r10.append(r11)     // Catch:{ Exception -> 0x00e8 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x00e8 }
            byte[] r10 = r10.getBytes()     // Catch:{ Exception -> 0x00e8 }
            int r12 = r10.length     // Catch:{ Exception -> 0x00e8 }
            r2.writeInt(r12)     // Catch:{ Exception -> 0x00e8 }
            int r12 = r10.length     // Catch:{ Exception -> 0x00e8 }
            r2.write(r10, r11, r12)     // Catch:{ Exception -> 0x00e8 }
            r3.remove(r11)     // Catch:{ Exception -> 0x00e8 }
            int r6 = r6 + 1
            goto L_0x00ca
        L_0x0095:
            if (r15 == 0) goto L_0x00cd
            int r10 = r7 * r5
            int r10 = r10 + 128
            long r12 = (long) r10     // Catch:{ Exception -> 0x00e8 }
            r2.seek(r12)     // Catch:{ Exception -> 0x00e8 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e8 }
            r10.<init>()     // Catch:{ Exception -> 0x00e8 }
            java.lang.Object r12 = r3.get(r11)     // Catch:{ Exception -> 0x00e8 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x00e8 }
            r10.append(r12)     // Catch:{ Exception -> 0x00e8 }
            r10.append(r11)     // Catch:{ Exception -> 0x00e8 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x00e8 }
            byte[] r10 = r10.getBytes()     // Catch:{ Exception -> 0x00e8 }
            int r12 = r10.length     // Catch:{ Exception -> 0x00e8 }
            r2.writeInt(r12)     // Catch:{ Exception -> 0x00e8 }
            int r12 = r10.length     // Catch:{ Exception -> 0x00e8 }
            r2.write(r10, r11, r12)     // Catch:{ Exception -> 0x00e8 }
            r3.remove(r11)     // Catch:{ Exception -> 0x00e8 }
            int r7 = r7 + 1
            if (r7 <= r6) goto L_0x00c8
            goto L_0x00c9
        L_0x00c8:
            r11 = r7
        L_0x00c9:
            r7 = r11
        L_0x00ca:
            int r9 = r9 + -1
            goto L_0x005b
        L_0x00cd:
            r11 = 1
        L_0x00ce:
            r3 = 12
            r2.seek(r3)     // Catch:{ Exception -> 0x00e8 }
            r2.writeInt(r6)     // Catch:{ Exception -> 0x00e8 }
            r2.writeInt(r7)     // Catch:{ Exception -> 0x00e8 }
            r2.writeInt(r8)     // Catch:{ Exception -> 0x00e8 }
            r2.close()     // Catch:{ Exception -> 0x00e8 }
            if (r11 == 0) goto L_0x00e9
            if (r14 >= r0) goto L_0x00e9
            int r14 = r14 + r1
            a((int) r14, (boolean) r1)     // Catch:{ Exception -> 0x00e8 }
            goto L_0x00e9
        L_0x00e8:
            r14 = move-exception
        L_0x00e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.w.a(int, boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0134 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0140  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:90:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:96:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(com.baidu.location.e.a r7, com.baidu.location.e.i r8, android.location.Location r9, java.lang.String r10) {
        /*
            com.baidu.location.c.e r0 = com.baidu.location.c.e.a()
            boolean r0 = r0.a
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            int r0 = com.baidu.location.g.k.v
            r1 = 3
            if (r0 != r1) goto L_0x001c
            boolean r0 = a((android.location.Location) r9, (com.baidu.location.e.i) r8)
            if (r0 != 0) goto L_0x001c
            r0 = 0
            boolean r0 = a((android.location.Location) r9, (boolean) r0)
            if (r0 != 0) goto L_0x001c
            return
        L_0x001c:
            if (r7 == 0) goto L_0x0152
            boolean r0 = r7.c()
            if (r0 == 0) goto L_0x0026
            goto L_0x0152
        L_0x0026:
            r0 = 0
            r2 = 28
            if (r7 == 0) goto L_0x0058
            boolean r3 = r7.a()
            if (r3 == 0) goto L_0x0058
            boolean r1 = a((android.location.Location) r9, (com.baidu.location.e.i) r8)
            if (r1 != 0) goto L_0x0038
            r8 = r0
        L_0x0038:
            r0 = 1
            java.lang.String r7 = com.baidu.location.g.k.a(r7, r8, r9, r10, r0)
            if (r7 == 0) goto L_0x0057
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 <= r2) goto L_0x0048
            java.lang.String r7 = com.baidu.location.Jni.encodeTp4(r7)
            goto L_0x004c
        L_0x0048:
            java.lang.String r7 = com.baidu.location.Jni.encode(r7)
        L_0x004c:
            a((java.lang.String) r7)
            w = r9
            v = r9
            if (r8 == 0) goto L_0x0057
            y = r8
        L_0x0057:
            return
        L_0x0058:
            java.lang.String r3 = "&cfr=3"
            java.lang.String r4 = "&cfr=2"
            java.lang.String r5 = "&cfr=1"
            if (r8 == 0) goto L_0x00d8
            boolean r6 = r8.l()
            if (r6 == 0) goto L_0x00d8
            boolean r6 = a((android.location.Location) r9, (com.baidu.location.e.i) r8)
            if (r6 == 0) goto L_0x00d8
            boolean r0 = a((android.location.Location) r9)
            if (r0 != 0) goto L_0x008c
            com.baidu.location.e.b r0 = com.baidu.location.e.b.a()
            boolean r0 = r0.d()
            if (r0 != 0) goto L_0x008c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r5)
        L_0x0084:
            r0.append(r10)
            java.lang.String r10 = r0.toString()
            goto L_0x00b8
        L_0x008c:
            boolean r0 = a((android.location.Location) r9)
            if (r0 != 0) goto L_0x00a5
            com.baidu.location.e.b r0 = com.baidu.location.e.b.a()
            boolean r0 = r0.d()
            if (r0 == 0) goto L_0x00a5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            goto L_0x0084
        L_0x00a5:
            com.baidu.location.e.b r0 = com.baidu.location.e.b.a()
            boolean r0 = r0.d()
            if (r0 == 0) goto L_0x00b8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            goto L_0x0084
        L_0x00b8:
            r0 = 2
            java.lang.String r7 = com.baidu.location.g.k.a(r7, r8, r9, r10, r0)
            if (r7 == 0) goto L_0x00d7
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 <= r2) goto L_0x00c8
            java.lang.String r7 = com.baidu.location.Jni.encodeTp4(r7)
            goto L_0x00cc
        L_0x00c8:
            java.lang.String r7 = com.baidu.location.Jni.encode(r7)
        L_0x00cc:
            b(r7)
            x = r9
            v = r9
            if (r8 == 0) goto L_0x00d7
            y = r8
        L_0x00d7:
            return
        L_0x00d8:
            boolean r6 = a((android.location.Location) r9)
            if (r6 != 0) goto L_0x00f8
            com.baidu.location.e.b r6 = com.baidu.location.e.b.a()
            boolean r6 = r6.d()
            if (r6 != 0) goto L_0x00f8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
        L_0x00f0:
            r3.append(r10)
            java.lang.String r10 = r3.toString()
            goto L_0x012b
        L_0x00f8:
            boolean r5 = a((android.location.Location) r9)
            if (r5 != 0) goto L_0x0118
            com.baidu.location.e.b r5 = com.baidu.location.e.b.a()
            boolean r5 = r5.d()
            if (r5 == 0) goto L_0x0118
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r10)
            java.lang.String r10 = r4.toString()
            goto L_0x012b
        L_0x0118:
            com.baidu.location.e.b r3 = com.baidu.location.e.b.a()
            boolean r3 = r3.d()
            if (r3 == 0) goto L_0x012b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            goto L_0x00f0
        L_0x012b:
            boolean r3 = a((android.location.Location) r9, (com.baidu.location.e.i) r8)
            if (r3 != 0) goto L_0x0132
            r8 = r0
        L_0x0132:
            if (r7 != 0) goto L_0x0136
            if (r8 == 0) goto L_0x0152
        L_0x0136:
            java.lang.String r7 = com.baidu.location.g.k.a(r7, r8, r9, r10, r1)
            if (r7 == 0) goto L_0x0152
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 <= r2) goto L_0x0145
            java.lang.String r7 = com.baidu.location.Jni.encodeTp4(r7)
            goto L_0x0149
        L_0x0145:
            java.lang.String r7 = com.baidu.location.Jni.encode(r7)
        L_0x0149:
            c(r7)
            v = r9
            if (r8 == 0) goto L_0x0152
            y = r8
        L_0x0152:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.w.a(com.baidu.location.e.a, com.baidu.location.e.i, android.location.Location, java.lang.String):void");
    }

    private static void a(String str) {
        e(str);
    }

    private static boolean a(int i2, int i3, int i4, int i5, long j2) {
        return i2 >= 0 && i2 < i4 && i3 >= 0 && i3 <= i4 && i4 >= 0 && i4 <= 1024 && i5 >= 128 && i5 <= 1024;
    }

    private static boolean a(Location location) {
        if (location == null) {
            return false;
        }
        Location location2 = w;
        if (location2 == null || v == null) {
            w = location;
            return true;
        }
        double distanceTo = (double) location.distanceTo(location2);
        return ((double) location.distanceTo(v)) > (((((double) k.T) * distanceTo) * distanceTo) + (((double) k.U) * distanceTo)) + ((double) k.V);
    }

    private static boolean a(Location location, i iVar) {
        boolean z2 = false;
        if (!(location == null || iVar == null || iVar.a == null || iVar.a.isEmpty())) {
            if (iVar.b(y)) {
                return false;
            }
            z2 = true;
            if (x == null) {
                x = location;
            }
        }
        return z2;
    }

    public static boolean a(Location location, boolean z2) {
        return f.a(v, location, z2);
    }

    private static boolean a(String str, List<String> list) {
        File file = new File(str);
        if (!file.exists()) {
            return false;
        }
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(8);
            int readInt = randomAccessFile.readInt();
            int readInt2 = randomAccessFile.readInt();
            int readInt3 = randomAccessFile.readInt();
            byte[] bArr = new byte[n];
            int i2 = l + 1;
            boolean z2 = false;
            while (i2 > 0 && readInt2 > 0) {
                if (readInt2 < readInt3) {
                    readInt3 = 0;
                }
                try {
                    randomAccessFile.seek((long) (((readInt2 - 1) * readInt) + 128));
                    int readInt4 = randomAccessFile.readInt();
                    if (readInt4 > 0 && readInt4 < readInt) {
                        randomAccessFile.read(bArr, 0, readInt4);
                        int i3 = readInt4 - 1;
                        if (bArr[i3] == 0) {
                            list.add(0, new String(bArr, 0, i3));
                            z2 = true;
                        }
                    }
                    i2--;
                    readInt2--;
                } catch (Exception e2) {
                    return z2;
                }
            }
            randomAccessFile.seek(12);
            randomAccessFile.writeInt(readInt2);
            randomAccessFile.writeInt(readInt3);
            randomAccessFile.close();
            return z2;
        } catch (Exception e3) {
            return false;
        }
    }

    public static String b() {
        return f();
    }

    private static void b(String str) {
        e(str);
    }

    private static void c(String str) {
        e(str);
    }

    public static void d() {
        l = 0;
        a(1, false);
        a(2, false);
        a(3, false);
        l = 8;
    }

    private static void d(String str) {
        try {
            File file = new File(str);
            if (!file.exists()) {
                File file2 = new File(j.a);
                if (!file2.exists()) {
                    file2.mkdirs();
                }
                if (!file.createNewFile()) {
                    file = null;
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(0);
                randomAccessFile.writeInt(32);
                randomAccessFile.writeInt(2048);
                randomAccessFile.writeInt(2060);
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(0);
                randomAccessFile.writeInt(0);
                randomAccessFile.close();
            }
        } catch (Exception e2) {
        }
    }

    public static String e() {
        File file = new File(g);
        String str = null;
        if (file.exists()) {
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(20);
                int readInt = randomAccessFile.readInt();
                if (readInt > 128) {
                    String str2 = "&p1=" + readInt;
                    try {
                        randomAccessFile.seek(20);
                        randomAccessFile.writeInt(0);
                        randomAccessFile.close();
                        return str2;
                    } catch (Exception e2) {
                        str = str2;
                    }
                } else {
                    randomAccessFile.close();
                }
            } catch (Exception e3) {
            }
        }
        File file2 = new File(h);
        if (file2.exists()) {
            try {
                RandomAccessFile randomAccessFile2 = new RandomAccessFile(file2, "rw");
                randomAccessFile2.seek(20);
                int readInt2 = randomAccessFile2.readInt();
                if (readInt2 > 256) {
                    String str3 = "&p2=" + readInt2;
                    try {
                        randomAccessFile2.seek(20);
                        randomAccessFile2.writeInt(0);
                        randomAccessFile2.close();
                        return str3;
                    } catch (Exception e4) {
                        str = str3;
                    }
                } else {
                    randomAccessFile2.close();
                }
            } catch (Exception e5) {
            }
        }
        File file3 = new File(i);
        if (!file3.exists()) {
            return str;
        }
        try {
            RandomAccessFile randomAccessFile3 = new RandomAccessFile(file3, "rw");
            randomAccessFile3.seek(20);
            int readInt3 = randomAccessFile3.readInt();
            if (readInt3 > 512) {
                String str4 = "&p3=" + readInt3;
                try {
                    randomAccessFile3.seek(20);
                    randomAccessFile3.writeInt(0);
                    randomAccessFile3.close();
                    return str4;
                } catch (Exception e6) {
                    return str4;
                }
            } else {
                randomAccessFile3.close();
                return str;
            }
        } catch (Exception e7) {
            return str;
        }
    }

    private static synchronized void e(String str) {
        ArrayList<String> arrayList;
        synchronized (w.class) {
            if (!str.contains("err!")) {
                int i2 = k.q;
                if (i2 == 1) {
                    arrayList = b;
                } else if (i2 == 2) {
                    arrayList = c;
                } else if (i2 == 3) {
                    arrayList = d;
                } else {
                    return;
                }
                if (arrayList != null) {
                    if (arrayList.size() <= m) {
                        arrayList.add(str);
                    }
                    if (arrayList.size() >= m) {
                        a(i2, false);
                    }
                    while (arrayList.size() > m) {
                        arrayList.remove(0);
                    }
                }
            }
        }
    }

    private static String f() {
        String str = null;
        for (int i2 = 1; i2 < 5; i2++) {
            str = a(i2);
            if (str != null) {
                return str;
            }
        }
        a((List<String>) d, t);
        if (d.size() > 0) {
            str = d.get(0);
            d.remove(0);
        }
        if (str != null) {
            return str;
        }
        a((List<String>) d, s);
        if (d.size() > 0) {
            str = d.get(0);
            d.remove(0);
        }
        if (str != null) {
            return str;
        }
        a((List<String>) d, u);
        if (d.size() <= 0) {
            return str;
        }
        String str2 = d.get(0);
        d.remove(0);
        return str2;
    }

    public void c() {
        if (com.baidu.location.e.j.j() && !k.b()) {
            this.z.b();
        }
    }
}
