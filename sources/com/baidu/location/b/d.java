package com.baidu.location.b;

import android.app.ActivityManager;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import com.baidu.location.Jni;
import com.baidu.location.f;
import com.baidu.location.g.b;
import com.baidu.location.g.e;
import com.baidu.location.g.j;
import com.baidu.location.g.k;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.BuildConfig;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import kotlin.jvm.internal.ByteCompanionObject;
import org.json.JSONException;
import org.json.JSONObject;

public class d {
    public static String f = "0";
    private static d j = null;
    private a A;
    private boolean B;
    private boolean C;
    private int D;
    private float E;
    private float F;
    private long G;
    private int H;
    private Handler I;
    private byte[] J;
    private byte[] K;
    private int L;
    private List<Byte> M;
    private boolean N;
    long a;
    Location b;
    Location c;
    StringBuilder d;
    long e;
    int g;
    double h;
    double i;
    private int k;
    private double l;
    private String m;
    private int n;
    private int o;
    private int p;
    private int q;
    private double r;
    private double s;
    private double t;
    private int u;
    private int v;
    private int w;
    private int x;
    private int y;
    private long z;

    class a extends e {
        String a = null;

        public a() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = "http://loc.map.baidu.com/cc.php";
            String encode = Jni.encode(this.a);
            this.a = null;
            this.k.put("q", encode);
        }

        public void a(String str) {
            this.a = str;
            b(v.a().c());
        }

        public void a(boolean z) {
            if (z && this.j != null) {
                try {
                    JSONObject jSONObject = new JSONObject(this.j);
                    jSONObject.put(BuildConfig.FLAVOR, b.e);
                    jSONObject.put("uptime", System.currentTimeMillis());
                    d.this.e(jSONObject.toString());
                } catch (Exception e) {
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
        }
    }

    private d() {
        this.k = 1;
        this.l = 0.699999988079071d;
        this.m = "3G|4G";
        this.n = 1;
        this.o = 307200;
        this.p = 15;
        this.q = 1;
        this.r = 3.5d;
        this.s = 3.0d;
        this.t = 0.5d;
        this.u = 300;
        this.v = 60;
        this.w = 0;
        this.x = 60;
        this.y = 0;
        this.z = 0;
        this.A = null;
        this.B = false;
        this.C = false;
        this.D = 0;
        this.E = 0.0f;
        this.F = 0.0f;
        this.G = 0;
        this.H = 500;
        this.a = 0;
        this.b = null;
        this.c = null;
        this.d = null;
        this.e = 0;
        this.I = null;
        this.J = new byte[4];
        this.K = null;
        this.L = 0;
        this.M = null;
        this.N = false;
        this.g = 0;
        this.h = 116.22345545d;
        this.i = 40.245667323d;
        this.I = new Handler();
    }

    public static d a() {
        if (j == null) {
            j = new d();
        }
        return j;
    }

    /* access modifiers changed from: private */
    public String a(File file, String str) {
        String uuid = UUID.randomUUID().toString();
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(str).openConnection();
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setConnectTimeout(10000);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setRequestProperty("Charset", "utf-8");
            httpsURLConnection.setRequestProperty("connection", "close");
            httpsURLConnection.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" + uuid);
            if (file == null || !file.exists()) {
                return "0";
            }
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("--");
            stringBuffer.append(uuid);
            stringBuffer.append("\r\n");
            stringBuffer.append("Content-Disposition: form-data; name=\"location_dat\"; filename=\"" + file.getName() + "\"" + "\r\n");
            StringBuilder sb = new StringBuilder();
            sb.append("Content-Type: application/octet-stream; charset=utf-8");
            sb.append("\r\n");
            stringBuffer.append(sb.toString());
            stringBuffer.append("\r\n");
            dataOutputStream.write(stringBuffer.toString().getBytes());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                dataOutputStream.write(bArr, 0, read);
            }
            fileInputStream.close();
            dataOutputStream.write("\r\n".getBytes());
            dataOutputStream.write(("--" + uuid + "--" + "\r\n").getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = httpsURLConnection.getResponseCode();
            outputStream.close();
            httpsURLConnection.disconnect();
            int i2 = this.y + 400;
            this.y = i2;
            c(i2);
            return responseCode == 200 ? "1" : "0";
        } catch (IOException | MalformedURLException e2) {
            return "0";
        }
    }

    private boolean a(String str, Context context) {
        boolean z2 = false;
        try {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                    if (next.processName.equals(str)) {
                        int i2 = next.importance;
                        if (i2 == 200 || i2 == 100) {
                            z2 = true;
                        }
                    }
                }
            }
        } catch (Exception e2) {
        }
        return z2;
    }

    private byte[] a(int i2) {
        return new byte[]{(byte) (i2 & 255), (byte) ((65280 & i2) >> 8), (byte) ((16711680 & i2) >> 16), (byte) ((i2 & -16777216) >> 24)};
    }

    private byte[] a(String str) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        byte nextInt = (byte) new Random().nextInt(255);
        byte nextInt2 = (byte) new Random().nextInt(255);
        byte[] bArr = new byte[(bytes.length + 2)];
        int length = bytes.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            bArr[i3] = (byte) (bytes[i2] ^ nextInt);
            i2++;
            i3++;
        }
        bArr[i3] = nextInt;
        bArr[i3 + 1] = nextInt2;
        return bArr;
    }

    private String b(String str) {
        Calendar instance = Calendar.getInstance();
        return String.format(str, new Object[]{Integer.valueOf(instance.get(1)), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(5))});
    }

    private void b(int i2) {
        byte[] a2 = a(i2);
        for (int i3 = 0; i3 < 4; i3++) {
            this.M.add(Byte.valueOf(a2[i3]));
        }
    }

    /* access modifiers changed from: private */
    public void b(Location location) {
        c(location);
        h();
    }

    private void c() {
        if (!this.N) {
            this.N = true;
            d(b.e);
            j();
            d();
        }
    }

    private void c(int i2) {
        if (i2 != 0) {
            try {
                File file = new File(j.a + "/grtcf.dat");
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
                randomAccessFile2.seek(8);
                byte[] bytes2 = (b("%d_%02d_%02d") + LogUtils.COLON + i2).getBytes();
                randomAccessFile2.writeInt(bytes2.length);
                randomAccessFile2.write(bytes2);
                randomAccessFile2.close();
            } catch (Exception e2) {
            }
        }
    }

    private void c(Location location) {
        if (System.currentTimeMillis() - this.a >= ((long) this.H) && location != null) {
            if (location != null && location.hasSpeed() && location.getSpeed() > this.E) {
                this.E = location.getSpeed();
            }
            try {
                if (this.M == null) {
                    this.M = new ArrayList();
                    i();
                    d(location);
                } else {
                    e(location);
                }
            } catch (Exception e2) {
            }
            this.L++;
        }
    }

    private void c(String str) {
        String str2;
        String str3;
        String str4 = str;
        String str5 = "uptime";
        if (str4 != null) {
            String str6 = "nondrper";
            try {
                JSONObject jSONObject = new JSONObject(str4);
                if (jSONObject.has("on")) {
                    this.k = jSONObject.getInt("on");
                }
                if (jSONObject.has("bash")) {
                    str3 = "nondron";
                    str2 = "stper";
                    this.l = jSONObject.getDouble("bash");
                } else {
                    str3 = "nondron";
                    str2 = "stper";
                }
                if (jSONObject.has("net")) {
                    this.m = jSONObject.getString("net");
                }
                if (jSONObject.has("tcon")) {
                    this.n = jSONObject.getInt("tcon");
                }
                if (jSONObject.has("tcsh")) {
                    this.o = jSONObject.getInt("tcsh");
                }
                if (jSONObject.has("per")) {
                    this.p = jSONObject.getInt("per");
                }
                if (jSONObject.has("chdron")) {
                    this.q = jSONObject.getInt("chdron");
                }
                if (jSONObject.has("spsh")) {
                    this.r = jSONObject.getDouble("spsh");
                }
                if (jSONObject.has("acsh")) {
                    this.s = jSONObject.getDouble("acsh");
                }
                if (jSONObject.has("stspsh")) {
                    this.t = jSONObject.getDouble("stspsh");
                }
                if (jSONObject.has("drstsh")) {
                    this.u = jSONObject.getInt("drstsh");
                }
                if (jSONObject.has(str2)) {
                    this.v = jSONObject.getInt(str2);
                }
                if (jSONObject.has(str3)) {
                    this.w = jSONObject.getInt(str3);
                }
                String str7 = str6;
                if (jSONObject.has(str7)) {
                    this.x = jSONObject.getInt(str7);
                }
                String str8 = str5;
                if (jSONObject.has(str8)) {
                    this.z = jSONObject.getLong(str8);
                }
                k();
            } catch (JSONException e2) {
            }
        }
    }

    private void d() {
        String[] split = "8.2.2".split("\\.");
        int length = split.length;
        byte[] bArr = this.J;
        int i2 = 0;
        bArr[0] = 0;
        bArr[1] = 0;
        bArr[2] = 0;
        bArr[3] = 0;
        if (length >= 4) {
            length = 4;
        }
        while (i2 < length) {
            try {
                this.J[i2] = (byte) (Integer.valueOf(split[i2]).intValue() & 255);
                i2++;
            } catch (Exception e2) {
            }
        }
        this.K = a(b.e + LogUtils.COLON + b.a().c);
    }

    private void d(Location location) {
        this.e = System.currentTimeMillis();
        b((int) (location.getTime() / 1000));
        b((int) (location.getLongitude() * 1000000.0d));
        b((int) (location.getLatitude() * 1000000.0d));
        char c2 = !location.hasBearing();
        char c3 = !location.hasSpeed();
        this.M.add(Byte.valueOf(c2 > 0 ? 32 : (byte) (((byte) (((int) (location.getBearing() / 15.0f)) & 255)) & -33)));
        this.M.add(Byte.valueOf(c3 > 0 ? ByteCompanionObject.MIN_VALUE : (byte) (((byte) (((int) ((((double) location.getSpeed()) * 3.6d) / 4.0d)) & 255)) & ByteCompanionObject.MAX_VALUE)));
        this.b = location;
    }

    private void d(String str) {
        try {
            File file = new File(j.a + "/grtcf.dat");
            if (file.exists()) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(2);
                int readInt = randomAccessFile.readInt();
                randomAccessFile.seek(8);
                int readInt2 = randomAccessFile.readInt();
                byte[] bArr = new byte[readInt2];
                randomAccessFile.read(bArr, 0, readInt2);
                String str2 = new String(bArr);
                int i2 = 1;
                if (str2.contains(b("%d_%02d_%02d")) && str2.contains(LogUtils.COLON)) {
                    try {
                        String[] split = str2.split(LogUtils.COLON);
                        if (split.length > 1) {
                            this.y = Integer.valueOf(split[1]).intValue();
                        }
                    } catch (Exception e2) {
                    }
                }
                while (true) {
                    if (i2 > readInt) {
                        break;
                    }
                    randomAccessFile.seek((long) (i2 * 2048));
                    int readInt3 = randomAccessFile.readInt();
                    byte[] bArr2 = new byte[readInt3];
                    randomAccessFile.read(bArr2, 0, readInt3);
                    String str3 = new String(bArr2);
                    if (str != null && str3.contains(str)) {
                        c(str3);
                        break;
                    }
                    i2++;
                }
                randomAccessFile.close();
            }
        } catch (Exception e3) {
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0104, code lost:
        if (r8 > 0) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x011a, code lost:
        if (r8 > 0) goto L_0x011c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void e(android.location.Location r22) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            double r2 = r22.getLongitude()
            android.location.Location r4 = r0.b
            double r4 = r4.getLongitude()
            double r2 = r2 - r4
            r4 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            double r2 = r2 * r4
            int r2 = (int) r2
            double r6 = r22.getLatitude()
            android.location.Location r3 = r0.b
            double r8 = r3.getLatitude()
            double r6 = r6 - r8
            double r6 = r6 * r4
            int r3 = (int) r6
            boolean r4 = r22.hasBearing()
            r5 = 1
            r4 = r4 ^ r5
            boolean r6 = r22.hasSpeed()
            r6 = r6 ^ r5
            if (r2 <= 0) goto L_0x0037
            r8 = 0
            goto L_0x0038
        L_0x0037:
            r8 = 1
        L_0x0038:
            int r2 = java.lang.Math.abs(r2)
            if (r3 <= 0) goto L_0x0040
            r9 = 0
            goto L_0x0041
        L_0x0040:
            r9 = 1
        L_0x0041:
            int r3 = java.lang.Math.abs(r3)
            int r10 = r0.L
            if (r10 <= r5) goto L_0x0050
            r5 = 0
            r0.c = r5
            android.location.Location r5 = r0.b
            r0.c = r5
        L_0x0050:
            r0.b = r1
            if (r1 == 0) goto L_0x00c6
            android.location.Location r5 = r0.c
            if (r5 == 0) goto L_0x00c6
            long r10 = r22.getTime()
            android.location.Location r5 = r0.c
            long r12 = r5.getTime()
            int r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r5 <= 0) goto L_0x00c6
            android.location.Location r5 = r0.b
            long r10 = r5.getTime()
            android.location.Location r5 = r0.c
            long r12 = r5.getTime()
            long r10 = r10 - r12
            r12 = 5000(0x1388, double:2.4703E-320)
            int r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r5 >= 0) goto L_0x00c6
            android.location.Location r5 = r0.b
            long r10 = r5.getTime()
            android.location.Location r5 = r0.c
            long r12 = r5.getTime()
            long r10 = r10 - r12
            r5 = 2
            float[] r5 = new float[r5]
            android.location.Location r12 = r0.b
            double r12 = r12.getAltitude()
            android.location.Location r14 = r0.b
            double r14 = r14.getLongitude()
            android.location.Location r7 = r0.c
            double r16 = r7.getLatitude()
            android.location.Location r7 = r0.c
            double r18 = r7.getLongitude()
            r20 = r5
            android.location.Location.distanceBetween(r12, r14, r16, r18, r20)
            r7 = 1073741824(0x40000000, float:2.0)
            r12 = 0
            r5 = r5[r12]
            android.location.Location r12 = r0.c
            float r12 = r12.getSpeed()
            float r13 = (float) r10
            float r12 = r12 * r13
            float r5 = r5 - r12
            float r5 = r5 * r7
            long r10 = r10 * r10
            float r7 = (float) r10
            float r5 = r5 / r7
            double r10 = (double) r5
            float r5 = r0.F
            double r12 = (double) r5
            int r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r5 <= 0) goto L_0x00c6
            float r5 = (float) r10
            r0.F = r5
        L_0x00c6:
            java.util.List<java.lang.Byte> r5 = r0.M
            r7 = r2 & 255(0xff, float:3.57E-43)
            byte r7 = (byte) r7
            java.lang.Byte r7 = java.lang.Byte.valueOf(r7)
            r5.add(r7)
            java.util.List<java.lang.Byte> r5 = r0.M
            r7 = 65280(0xff00, float:9.1477E-41)
            r2 = r2 & r7
            int r2 = r2 >> 8
            byte r2 = (byte) r2
            java.lang.Byte r2 = java.lang.Byte.valueOf(r2)
            r5.add(r2)
            java.util.List<java.lang.Byte> r2 = r0.M
            r5 = r3 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5
            java.lang.Byte r5 = java.lang.Byte.valueOf(r5)
            r2.add(r5)
            java.util.List<java.lang.Byte> r2 = r0.M
            r3 = r3 & r7
            int r3 = r3 >> 8
            byte r3 = (byte) r3
            java.lang.Byte r3 = java.lang.Byte.valueOf(r3)
            r2.add(r3)
            if (r4 <= 0) goto L_0x0107
            r2 = 32
            if (r9 <= 0) goto L_0x0104
            r2 = 96
            byte r2 = (byte) r2
        L_0x0104:
            if (r8 <= 0) goto L_0x011f
            goto L_0x011c
        L_0x0107:
            float r2 = r22.getBearing()
            r3 = 1097859072(0x41700000, float:15.0)
            float r2 = r2 / r3
            int r2 = (int) r2
            r2 = r2 & 255(0xff, float:3.57E-43)
            byte r2 = (byte) r2
            r2 = r2 & 31
            byte r2 = (byte) r2
            if (r9 <= 0) goto L_0x011a
            r2 = r2 | 64
            byte r2 = (byte) r2
        L_0x011a:
            if (r8 <= 0) goto L_0x011f
        L_0x011c:
            r2 = r2 | -128(0xffffffffffffff80, float:NaN)
            byte r2 = (byte) r2
        L_0x011f:
            java.util.List<java.lang.Byte> r3 = r0.M
            java.lang.Byte r2 = java.lang.Byte.valueOf(r2)
            r3.add(r2)
            if (r6 <= 0) goto L_0x0136
            java.util.List<java.lang.Byte> r1 = r0.M
            r2 = -128(0xffffffffffffff80, float:NaN)
            java.lang.Byte r2 = java.lang.Byte.valueOf(r2)
            r1.add(r2)
            goto L_0x0155
        L_0x0136:
            float r1 = r22.getSpeed()
            double r1 = (double) r1
            r3 = 4615288898129284301(0x400ccccccccccccd, double:3.6)
            double r1 = r1 * r3
            r3 = 4616189618054758400(0x4010000000000000, double:4.0)
            double r1 = r1 / r3
            int r1 = (int) r1
            r1 = r1 & 255(0xff, float:3.57E-43)
            byte r1 = (byte) r1
            r1 = r1 & 127(0x7f, float:1.78E-43)
            byte r1 = (byte) r1
            java.util.List<java.lang.Byte> r2 = r0.M
            java.lang.Byte r1 = java.lang.Byte.valueOf(r1)
            r2.add(r1)
        L_0x0155:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.d.e(android.location.Location):void");
    }

    /* access modifiers changed from: private */
    public void e(String str) {
        try {
            File file = new File(j.a + "/grtcf.dat");
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
            randomAccessFile2.seek(2);
            int readInt = randomAccessFile2.readInt();
            int i2 = 1;
            while (true) {
                if (i2 > readInt) {
                    break;
                }
                randomAccessFile2.seek((long) (i2 * 2048));
                int readInt2 = randomAccessFile2.readInt();
                byte[] bArr = new byte[readInt2];
                randomAccessFile2.read(bArr, 0, readInt2);
                if (new String(bArr).contains(b.e)) {
                    break;
                }
                i2++;
            }
            if (i2 >= readInt) {
                randomAccessFile2.seek(2);
                randomAccessFile2.writeInt(i2);
            }
            randomAccessFile2.seek((long) (i2 * 2048));
            byte[] bytes2 = str.getBytes();
            randomAccessFile2.writeInt(bytes2.length);
            randomAccessFile2.write(bytes2);
            randomAccessFile2.close();
        } catch (Exception e2) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.io.RandomAccessFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: java.nio.channels.FileChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: java.nio.channels.FileLock} */
    /* JADX WARNING: type inference failed for: r0v0 */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r0v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x005b A[SYNTHETIC, Splitter:B:29:0x005b] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0063 A[Catch:{ Exception -> 0x005f }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x006a A[SYNTHETIC, Splitter:B:38:0x006a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean e() {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            r3.<init>()     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.String r4 = com.baidu.location.g.k.i()     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            r3.append(r4)     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.String r4 = java.io.File.separator     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            r3.append(r4)     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.String r4 = "gflk.dat"
            r3.append(r4)     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            r2.<init>(r3)     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            boolean r3 = r2.exists()     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            if (r3 != 0) goto L_0x002a
            r2.createNewFile()     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
        L_0x002a:
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.lang.String r4 = "rw"
            r3.<init>(r2, r4)     // Catch:{ Exception -> 0x0067, all -> 0x0057 }
            java.nio.channels.FileChannel r2 = r3.getChannel()     // Catch:{ Exception -> 0x0054, all -> 0x0052 }
            java.nio.channels.FileLock r0 = r2.tryLock()     // Catch:{ Exception -> 0x003d, all -> 0x003a }
            goto L_0x003f
        L_0x003a:
            r1 = move-exception
            r0 = r2
            goto L_0x0059
        L_0x003d:
            r1 = move-exception
            r1 = 1
        L_0x003f:
            if (r0 == 0) goto L_0x0047
            r0.release()     // Catch:{ Exception -> 0x0045 }
            goto L_0x0047
        L_0x0045:
            r0 = move-exception
            goto L_0x006d
        L_0x0047:
            if (r2 == 0) goto L_0x004c
            r2.close()     // Catch:{ Exception -> 0x0045 }
        L_0x004c:
            if (r3 == 0) goto L_0x006d
            r3.close()     // Catch:{ Exception -> 0x0045 }
            goto L_0x006d
        L_0x0052:
            r1 = move-exception
            goto L_0x0059
        L_0x0054:
            r0 = move-exception
            r0 = r3
            goto L_0x0068
        L_0x0057:
            r1 = move-exception
            r3 = r0
        L_0x0059:
            if (r0 == 0) goto L_0x0061
            r0.close()     // Catch:{ Exception -> 0x005f }
            goto L_0x0061
        L_0x005f:
            r0 = move-exception
            goto L_0x0066
        L_0x0061:
            if (r3 == 0) goto L_0x0066
            r3.close()     // Catch:{ Exception -> 0x005f }
        L_0x0066:
            throw r1
        L_0x0067:
            r2 = move-exception
        L_0x0068:
            if (r0 == 0) goto L_0x006d
            r0.close()     // Catch:{ Exception -> 0x0045 }
        L_0x006d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.d.e():boolean");
    }

    private boolean f() {
        if (this.B) {
            if (this.C) {
                if (((double) this.E) < this.t) {
                    int i2 = this.D + this.p;
                    this.D = i2;
                    return i2 <= this.u || System.currentTimeMillis() - this.G > ((long) (this.v * 1000));
                }
                this.D = 0;
                this.C = false;
            } else if (((double) this.E) < this.t) {
                this.C = true;
                this.D = 0;
                this.D = 0 + this.p;
            }
        } else if (((double) this.E) >= this.r || ((double) this.F) >= this.s) {
            this.B = true;
        } else if (this.w != 1 || System.currentTimeMillis() - this.G <= ((long) (this.x * 1000))) {
            return false;
        }
    }

    private void g() {
        this.M = null;
        this.e = 0;
        this.L = 0;
        this.b = null;
        this.c = null;
        this.E = 0.0f;
        this.F = 0.0f;
    }

    private void h() {
        if (this.e != 0 && System.currentTimeMillis() - this.e >= ((long) (this.p * 1000))) {
            if (f.getServiceContext().getSharedPreferences("loc_navi_mode", 4).getBoolean("is_navi_on", false)) {
                g();
            } else if (this.n != 1 || f()) {
                if (!b.e.equals("com.ubercab.driver")) {
                    if (!a(b.e, f.getServiceContext())) {
                        g();
                        return;
                    }
                } else if (e()) {
                    g();
                    return;
                }
                List<Byte> list = this.M;
                if (list != null) {
                    int size = list.size();
                    this.M.set(0, Byte.valueOf((byte) (size & 255)));
                    this.M.set(1, Byte.valueOf((byte) ((65280 & size) >> 8)));
                    this.M.set(3, Byte.valueOf((byte) (this.L & 255)));
                    byte[] bArr = new byte[size];
                    for (int i2 = 0; i2 < size; i2++) {
                        bArr[i2] = this.M.get(i2).byteValue();
                    }
                    File file = new File(k.k(), "baidu/tempdata");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    if (file.exists()) {
                        File file2 = new File(file, "intime.dat");
                        if (file2.exists()) {
                            file2.delete();
                        }
                        try {
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file2));
                            bufferedOutputStream.write(bArr);
                            bufferedOutputStream.flush();
                            bufferedOutputStream.close();
                            new f(this).start();
                        } catch (Exception e2) {
                        }
                    }
                    g();
                    this.G = System.currentTimeMillis();
                }
            } else {
                g();
            }
        }
    }

    private void i() {
        byte b2;
        List<Byte> list;
        this.M.add((byte) 0);
        this.M.add((byte) 0);
        if (f.equals("0")) {
            list = this.M;
            b2 = -82;
        } else {
            list = this.M;
            b2 = -66;
        }
        list.add(Byte.valueOf(b2));
        this.M.add((byte) 0);
        this.M.add(Byte.valueOf(this.J[0]));
        this.M.add(Byte.valueOf(this.J[1]));
        this.M.add(Byte.valueOf(this.J[2]));
        this.M.add(Byte.valueOf(this.J[3]));
        this.M.add(Byte.valueOf((byte) ((r0 + 1) & 255)));
        for (byte valueOf : this.K) {
            this.M.add(Byte.valueOf(valueOf));
        }
    }

    private void j() {
        if (System.currentTimeMillis() - this.z > 86400000) {
            if (this.A == null) {
                this.A = new a();
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(b.a().a(false));
            stringBuffer.append(a.a().d());
            this.A.a(stringBuffer.toString());
        }
        k();
    }

    private void k() {
    }

    public void a(Location location) {
        if (!this.N) {
            c();
        }
        boolean z2 = ((double) com.baidu.location.c.d.a().f()) < this.l * 100.0d;
        if (this.k == 1 && z2 && this.m.contains(com.baidu.location.e.e.a(com.baidu.location.e.b.a().e()))) {
            if (this.n != 1 || this.y <= this.o) {
                this.I.post(new e(this, location));
            }
        }
    }

    public void b() {
        if (this.N) {
            this.N = false;
            g();
        }
    }
}
