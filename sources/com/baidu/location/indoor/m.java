package com.baidu.location.indoor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

public class m {
    private int A;
    private long B;
    private int C;
    private int D;
    private double E;
    private double F;
    private double G;
    private double H;
    private double I;
    private double J;
    private double K;
    private int L;
    private float M;
    /* access modifiers changed from: private */
    public int N;
    /* access modifiers changed from: private */
    public int O;
    /* access modifiers changed from: private */
    public double[] P;
    /* access modifiers changed from: private */
    public boolean Q;
    private double R;
    private String S;
    Timer a;
    public SensorEventListener b;
    private a c;
    /* access modifiers changed from: private */
    public SensorManager d;
    private boolean e;
    private int f;
    private Sensor g;
    /* access modifiers changed from: private */
    public Sensor h;
    private Sensor i;
    private final long j;
    /* access modifiers changed from: private */
    public boolean k;
    private boolean l;
    private boolean m;
    /* access modifiers changed from: private */
    public volatile int n;
    private int o;
    private float[] p;
    /* access modifiers changed from: private */
    public float[] q;
    /* access modifiers changed from: private */
    public double[] r;
    private int s;
    private double[] t;
    private int u;
    private int v;
    private int w;
    private double[] x;
    private int y;
    private double z;

    public interface a {
        void a(double d, double d2, double d3, long j);
    }

    private m(Context context, int i2) {
        this.j = 30;
        this.k = true;
        this.l = false;
        this.m = false;
        this.n = 1;
        this.o = 1;
        this.p = new float[3];
        this.q = new float[]{0.0f, 0.0f, 0.0f};
        this.r = new double[]{FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE};
        this.s = 31;
        this.t = new double[31];
        this.u = 0;
        this.x = new double[6];
        this.y = 0;
        this.B = 0;
        this.C = 0;
        this.D = 0;
        this.E = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.F = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.G = 100.0d;
        this.H = 0.5d;
        this.I = 0.5d;
        this.J = 0.85d;
        this.K = 0.42d;
        this.L = -1;
        this.M = 0.0f;
        this.N = 20;
        this.O = 0;
        this.P = new double[20];
        this.Q = false;
        this.R = -1.0d;
        this.S = null;
        this.b = new n(this);
        this.z = 1.6d;
        this.A = 440;
        try {
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            this.d = sensorManager;
            this.f = i2;
            this.g = sensorManager.getDefaultSensor(1);
            this.h = this.d.getDefaultSensor(3);
            if (com.baidu.location.indoor.mapversion.a.b()) {
                this.i = this.d.getDefaultSensor(4);
            }
            j();
        } catch (Exception e2) {
        }
    }

    public m(Context context, a aVar) {
        this(context, 1);
        this.c = aVar;
    }

    /* access modifiers changed from: private */
    public double a(double d2, double d3, double d4) {
        double d5 = d3 - d2;
        if (d5 < -180.0d) {
            d5 += 360.0d;
        } else if (d5 > 180.0d) {
            d5 -= 360.0d;
        }
        return d2 + (d4 * d5);
    }

    private double a(double[] dArr) {
        double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        double d3 = 0.0d;
        for (double d4 : dArr) {
            d3 += d4;
        }
        double d5 = d3 / ((double) r0);
        for (int i2 = 0; i2 < r0; i2++) {
            d2 += (dArr[i2] - d5) * (dArr[i2] - d5);
        }
        return d2 / ((double) (r0 - 1));
    }

    private void a(double d2) {
        double[] dArr = this.x;
        int i2 = this.y;
        dArr[i2 % 6] = d2;
        int i3 = i2 + 1;
        this.y = i3;
        this.y = i3 % 6;
    }

    private synchronized void a(int i2) {
        this.o = i2 | this.o;
    }

    /* access modifiers changed from: private */
    public float[] a(float f2, float f3, float f4) {
        float[] fArr = this.p;
        fArr[0] = (fArr[0] * 0.8f) + (f2 * 0.19999999f);
        fArr[1] = (fArr[1] * 0.8f) + (f3 * 0.19999999f);
        fArr[2] = (fArr[2] * 0.8f) + (0.19999999f * f4);
        return new float[]{f2 - fArr[0], f3 - fArr[1], f4 - fArr[2]};
    }

    static /* synthetic */ int b(m mVar) {
        int i2 = mVar.v + 1;
        mVar.v = i2;
        return i2;
    }

    private boolean b(double d2) {
        for (int i2 = 1; i2 <= 5; i2++) {
            double[] dArr = this.x;
            int i3 = this.y;
            if (dArr[((((i3 - 1) - i2) + 6) + 6) % 6] - dArr[((i3 - 1) + 6) % 6] > d2) {
                return true;
            }
        }
        return false;
    }

    static /* synthetic */ int f(m mVar) {
        int i2 = mVar.O;
        mVar.O = i2 + 1;
        return i2;
    }

    static /* synthetic */ int h(m mVar) {
        int i2 = mVar.w + 1;
        mVar.w = i2;
        return i2;
    }

    /* access modifiers changed from: private */
    public boolean i() {
        for (int i2 = 0; i2 < this.N; i2++) {
            if (this.P[i2] > 1.0E-7d) {
                return true;
            }
        }
        return false;
    }

    private void j() {
        try {
            List<Sensor> sensorList = this.d.getSensorList(-1);
            HashMap hashMap = new HashMap();
            hashMap.put(1, 0);
            hashMap.put(10, 1);
            hashMap.put(9, 2);
            hashMap.put(4, 3);
            hashMap.put(2, 4);
            hashMap.put(11, 5);
            hashMap.put(6, 6);
            if (Build.VERSION.SDK_INT >= 18) {
                hashMap.put(14, 7);
                hashMap.put(16, 8);
            }
            int size = hashMap.size();
            char[] cArr = new char[size];
            for (int i2 = 0; i2 < size; i2++) {
                cArr[i2] = '0';
            }
            for (Sensor type : sensorList) {
                int type2 = type.getType();
                if (hashMap.get(Integer.valueOf(type2)) != null) {
                    int intValue = ((Integer) hashMap.get(Integer.valueOf(type2))).intValue();
                    if (intValue < size) {
                        cArr[intValue] = '1';
                    }
                }
            }
            this.S = new String(cArr);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void k() {
        this.k = false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00f1, code lost:
        if (r5 < r7) goto L_0x00ea;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void l() {
        /*
            r19 = this;
            r0 = r19
            int r1 = r0.v
            r2 = 20
            if (r1 >= r2) goto L_0x0009
            return
        L_0x0009:
            boolean r1 = r0.e
            if (r1 != 0) goto L_0x000e
            return
        L_0x000e:
            long r9 = java.lang.System.currentTimeMillis()
            r1 = 3
            float[] r2 = new float[r1]
            float[] r3 = r0.q
            r4 = 0
            java.lang.System.arraycopy(r3, r4, r2, r4, r1)
            double[] r3 = new double[r1]
            double[] r5 = r0.r
            java.lang.System.arraycopy(r5, r4, r3, r4, r1)
            r1 = r2[r4]
            r5 = r2[r4]
            float r1 = r1 * r5
            r5 = 1
            r6 = r2[r5]
            r7 = r2[r5]
            float r6 = r6 * r7
            float r1 = r1 + r6
            r6 = 2
            r7 = r2[r6]
            r2 = r2[r6]
            float r7 = r7 * r2
            float r1 = r1 + r7
            double r1 = (double) r1
            double r1 = java.lang.Math.sqrt(r1)
            double[] r6 = r0.t
            int r7 = r0.u
            r6[r7] = r1
            r0.a((double) r1)
            int r6 = r0.D
            int r6 = r6 + r5
            r0.D = r6
            double r6 = r0.F
            int r8 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x0054
            r0.F = r1
            goto L_0x005c
        L_0x0054:
            double r6 = r0.G
            int r8 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x005c
            r0.G = r1
        L_0x005c:
            int r6 = r0.u
            int r6 = r6 + r5
            r0.u = r6
            int r7 = r0.s
            if (r6 != r7) goto L_0x0085
            r0.u = r4
            double[] r6 = r0.t
            double r6 = r0.a((double[]) r6)
            int r8 = r0.n
            if (r8 != 0) goto L_0x0080
            r11 = 4599075939470750515(0x3fd3333333333333, double:0.3)
            int r8 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r8 >= 0) goto L_0x0080
            r0.a((int) r4)
            r0.n = r4
            goto L_0x0085
        L_0x0080:
            r0.a((int) r5)
            r0.n = r5
        L_0x0085:
            long r6 = r0.B
            long r6 = r9 - r6
            int r8 = r0.A
            long r11 = (long) r8
            int r8 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r8 <= 0) goto L_0x0128
            double r6 = r0.z
            boolean r6 = r0.b((double) r6)
            if (r6 == 0) goto L_0x0128
            int r6 = r0.C
            int r6 = r6 + r5
            r0.C = r6
            r0.B = r9
            r6 = r3[r4]
            r11 = r3[r4]
            boolean r8 = r0.k
            r13 = 0
            if (r8 == 0) goto L_0x00c7
            boolean r8 = r0.l
            if (r8 == 0) goto L_0x00c7
            boolean r8 = com.baidu.location.indoor.mapversion.a.b()
            if (r8 == 0) goto L_0x00c7
            float[] r6 = com.baidu.location.indoor.mapversion.a.c()
            r6 = r6[r4]
            double r6 = (double) r6
            boolean r8 = java.lang.Double.isNaN(r6)
            if (r8 != 0) goto L_0x00c4
            int r8 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r8 >= 0) goto L_0x00c6
        L_0x00c4:
            r6 = r3[r4]
        L_0x00c6:
            r4 = 1
        L_0x00c7:
            int r3 = r0.D
            r8 = 40
            if (r3 >= r8) goto L_0x00f4
            if (r3 <= 0) goto L_0x00f4
            double r13 = r0.F
            r17 = r6
            double r5 = r0.G
            double r13 = r13 - r5
            double r5 = java.lang.Math.sqrt(r13)
            double r5 = java.lang.Math.sqrt(r5)
            double r7 = r0.K
            double r5 = r5 * r7
            r0.I = r5
            double r7 = r0.J
            int r13 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r13 <= 0) goto L_0x00ed
        L_0x00ea:
            r0.I = r7
            goto L_0x00fa
        L_0x00ed:
            double r7 = r0.H
            int r13 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r13 >= 0) goto L_0x00fa
            goto L_0x00ea
        L_0x00f4:
            r17 = r6
            double r5 = r0.H
            r0.I = r5
        L_0x00fa:
            float r5 = r0.M
            double r5 = (double) r5
            double r6 = r17 + r5
            r13 = 4645040803167600640(0x4076800000000000, double:360.0)
            int r5 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r5 <= 0) goto L_0x0109
            double r6 = r6 - r13
        L_0x0109:
            r15 = 0
            int r5 = (r6 > r15 ? 1 : (r6 == r15 ? 0 : -1))
            if (r5 >= 0) goto L_0x0110
            double r6 = r6 + r13
        L_0x0110:
            r7 = r6
            r3 = 1
            r0.D = r3
            r0.F = r1
            r0.G = r1
            r0.R = r7
            boolean r1 = r0.Q
            if (r1 != 0) goto L_0x0120
            if (r4 == 0) goto L_0x0128
        L_0x0120:
            com.baidu.location.indoor.m$a r2 = r0.c
            double r3 = r0.I
            r5 = r11
            r2.a(r3, r5, r7, r9)
        L_0x0128:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.m.l():void");
    }

    public void a() {
        if (!this.e) {
            Sensor sensor = this.g;
            if (sensor != null) {
                try {
                    this.d.registerListener(this.b, sensor, this.f);
                } catch (Exception e2) {
                    this.k = false;
                }
                this.a = new Timer("UpdateData", false);
                this.a.schedule(new o(this), 500, 30);
                this.e = true;
            }
            Sensor sensor2 = this.h;
            if (sensor2 != null) {
                try {
                    this.d.registerListener(this.b, sensor2, this.f);
                } catch (Exception e3) {
                    this.k = false;
                }
            }
        }
    }

    public void a(boolean z2) {
        this.l = z2;
        if (z2 && !this.m) {
            k();
            this.m = true;
        }
    }

    public void b() {
        if (this.e) {
            this.e = false;
            try {
                this.d.unregisterListener(this.b);
            } catch (Exception e2) {
            }
            this.a.cancel();
            this.a.purge();
            this.a = null;
            this.m = false;
            if (com.baidu.location.indoor.mapversion.a.b()) {
                com.baidu.location.indoor.mapversion.a.a();
            }
        }
    }

    public synchronized int c() {
        if (this.v < 20) {
            return 1;
        }
        return this.o;
    }

    public synchronized int d() {
        if (this.v < 20) {
            return -1;
        }
        return this.C;
    }

    public double e() {
        return this.R;
    }

    public synchronized void f() {
        this.o = 0;
    }

    public boolean g() {
        return this.l;
    }

    /* access modifiers changed from: protected */
    public String h() {
        return this.S;
    }
}
