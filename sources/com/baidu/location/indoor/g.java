package com.baidu.location.indoor;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.PoiRegion;
import com.baidu.location.b.n;
import com.baidu.location.b.v;
import com.baidu.location.e.j;
import com.baidu.location.g.k;
import com.baidu.location.indoor.a;
import com.baidu.location.indoor.d;
import com.baidu.location.indoor.m;
import com.baidu.location.indoor.mapversion.a.a;
import com.baidu.location.indoor.mapversion.c.a;
import com.baidu.location.indoor.mapversion.c.c;
import com.baidu.location.indoor.p;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.boxes.dece.BaseLocationBox;
import com.king.zxing.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.json.JSONObject;

public class g {
    private static g g;
    /* access modifiers changed from: private */
    public String A;
    private String B;
    /* access modifiers changed from: private */
    public int C;
    private int D;
    /* access modifiers changed from: private */
    public c<String> E;
    private int F;
    private c<String> G;
    /* access modifiers changed from: private */
    public double H;
    /* access modifiers changed from: private */
    public double I;
    /* access modifiers changed from: private */
    public double J;
    /* access modifiers changed from: private */
    public boolean K;
    private boolean L;
    /* access modifiers changed from: private */
    public List<h> M;
    /* access modifiers changed from: private */
    public int N;
    /* access modifiers changed from: private */
    public int O;
    private int P;
    /* access modifiers changed from: private */
    public a Q;
    /* access modifiers changed from: private */
    public String R;
    /* access modifiers changed from: private */
    public d S;
    /* access modifiers changed from: private */
    public boolean T;
    /* access modifiers changed from: private */
    public p U;
    private p.a V;
    private com.baidu.location.indoor.mapversion.a.a W;
    /* access modifiers changed from: private */
    public int X;
    /* access modifiers changed from: private */
    public BDLocation Y;
    private boolean Z;
    public d a;
    private boolean aa;
    /* access modifiers changed from: private */
    public boolean ab;
    /* access modifiers changed from: private */
    public boolean ac;
    private c ad;
    /* access modifiers changed from: private */
    public e ae;
    /* access modifiers changed from: private */
    public f af;
    private b ag;
    public SimpleDateFormat b;
    /* access modifiers changed from: private */
    public int c;
    /* access modifiers changed from: private */
    public boolean d;
    /* access modifiers changed from: private */
    public int e;
    private boolean f;
    /* access modifiers changed from: private */
    public long h;
    /* access modifiers changed from: private */
    public volatile boolean i;
    /* access modifiers changed from: private */
    public m j;
    private C0011g k;
    private i l;
    private long m;
    /* access modifiers changed from: private */
    public boolean n;
    private boolean o;
    /* access modifiers changed from: private */
    public long p;
    /* access modifiers changed from: private */
    public long q;
    /* access modifiers changed from: private */
    public int r;
    private String s;
    private m.a t;
    /* access modifiers changed from: private */
    public int u;
    private int v;
    /* access modifiers changed from: private */
    public String w;
    /* access modifiers changed from: private */
    public String x;
    /* access modifiers changed from: private */
    public l y;
    /* access modifiers changed from: private */
    public String z;

    class a {
    }

    class b {
        public String a;
        public int b;
        public String c;
        private ArrayList<Double> e;
        private ArrayList<String> f;
        private Map<String, Double> g;
        private int h;
        private Map<String, Integer> i;

        public b() {
            this.a = null;
            this.e = null;
            this.f = null;
            this.g = null;
            this.h = 0;
            this.b = 0;
            this.c = null;
            this.i = null;
            this.e = new ArrayList<>();
            this.f = new ArrayList<>();
            this.i = new HashMap();
        }

        /* access modifiers changed from: private */
        public int a(BDLocation bDLocation) {
            if (!bDLocation.getBuildingID().equals(this.c)) {
                this.c = bDLocation.getBuildingID();
                a();
            }
            if (b(bDLocation.getRetFields("p_floor")) != 0) {
                this.b = 0;
                return 1;
            }
            try {
                int size = this.f.size();
                double d2 = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
                if (size == 0) {
                    for (Map.Entry next : this.g.entrySet()) {
                        this.f.add(next.getKey());
                        this.e.add(next.getValue());
                    }
                } else {
                    ArrayList<String> arrayList = new ArrayList<>();
                    ArrayList arrayList2 = new ArrayList();
                    Iterator<String> it = this.f.iterator();
                    while (it.hasNext()) {
                        arrayList.add(it.next());
                        arrayList2.add(Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    }
                    HashMap hashMap = new HashMap();
                    for (Map.Entry next2 : this.g.entrySet()) {
                        String str = (String) next2.getKey();
                        Double d3 = (Double) next2.getValue();
                        hashMap.put(str, d3);
                        if (!this.f.contains(str)) {
                            arrayList.add(str);
                            arrayList2.add(d3);
                        }
                    }
                    double d4 = 0.0d;
                    for (Double doubleValue : this.g.values()) {
                        d4 += doubleValue.doubleValue();
                    }
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        arrayList2.set(i2, hashMap.containsKey(arrayList.get(i2)) ? hashMap.get(arrayList.get(i2)) : Double.valueOf((1.0d - d4) / ((double) (this.h - hashMap.size()))));
                    }
                    ArrayList arrayList3 = new ArrayList();
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        arrayList3.add(Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE));
                    }
                    for (int i4 = 0; i4 < this.f.size(); i4++) {
                        Double d5 = this.e.get(i4);
                        ArrayList<Double> a2 = a(arrayList, this.f.get(i4));
                        for (int i5 = 0; i5 < arrayList.size(); i5++) {
                            arrayList3.set(i5, Double.valueOf(((Double) arrayList3.get(i5)).doubleValue() + (d5.doubleValue() * a2.get(i5).doubleValue() * ((Double) arrayList2.get(i5)).doubleValue())));
                        }
                    }
                    this.f = arrayList;
                    this.e = a((ArrayList<Double>) arrayList3);
                }
                String str2 = null;
                for (int i6 = 0; i6 < this.f.size(); i6++) {
                    if (this.e.get(i6).doubleValue() > d2) {
                        d2 = this.e.get(i6).doubleValue();
                        str2 = this.f.get(i6);
                    }
                }
                this.a = str2;
            } catch (Exception e2) {
                this.b = 0;
            }
            this.b = 1;
            return 0;
        }

        private int a(String str) {
            if (this.i.containsKey(str)) {
                return this.i.get(str).intValue();
            }
            int i2 = 1000;
            try {
                if (!str.startsWith("F")) {
                    if (!str.startsWith("f")) {
                        if (str.startsWith("B") || str.startsWith("b")) {
                            i2 = -Integer.parseInt(str.substring(1));
                        }
                        this.i.put(str, Integer.valueOf(i2));
                        return i2;
                    }
                }
                i2 = Integer.parseInt(str.substring(1)) - 1;
            } catch (Exception e2) {
            }
            this.i.put(str, Integer.valueOf(i2));
            return i2;
        }

        private ArrayList<Double> a(ArrayList<Double> arrayList) {
            ArrayList<Double> arrayList2 = new ArrayList<>();
            Double valueOf = Double.valueOf(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            Iterator<Double> it = arrayList.iterator();
            while (it.hasNext()) {
                valueOf = Double.valueOf(valueOf.doubleValue() + it.next().doubleValue());
            }
            Iterator<Double> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                arrayList2.add(Double.valueOf(it2.next().doubleValue() / valueOf.doubleValue()));
            }
            return arrayList2;
        }

        private ArrayList<Double> a(ArrayList<String> arrayList, String str) {
            ArrayList<Double> arrayList2 = new ArrayList<>();
            double[] dArr = {180.0d, 10.0d, 1.0d};
            int a2 = a(str);
            Iterator<String> it = arrayList.iterator();
            if (a2 == 1000) {
                while (it.hasNext()) {
                    arrayList2.add(Double.valueOf(it.next().equals(str) ? dArr[0] : dArr[2]));
                }
                return arrayList2;
            }
            while (it.hasNext()) {
                int a3 = a(it.next());
                int i2 = a3 == 1000 ? 2 : a2 > a3 ? a2 - a3 : a3 - a2;
                if (i2 > 2) {
                    i2 = 2;
                }
                arrayList2.add(Double.valueOf(dArr[i2]));
            }
            return arrayList2;
        }

        private void a() {
            this.e.clear();
            this.f.clear();
            this.i.clear();
        }

        private int b(String str) {
            try {
                String[] split = str.split(";");
                if (split.length <= 1) {
                    return 1;
                }
                this.h = Integer.parseInt(split[0]);
                this.g = new HashMap();
                for (int i2 = 1; i2 < split.length; i2++) {
                    String[] split2 = split[i2].split(LogUtils.COLON);
                    this.g.put(split2[0], Double.valueOf(Double.parseDouble(split2[1])));
                }
                return 0;
            } catch (Exception e2) {
                return 1;
            }
        }

        /* access modifiers changed from: private */
        public String b() {
            return this.a;
        }
    }

    class c {
        public ArrayList<ArrayList<Float>> a = null;
        public double[] b = null;
        private float d = -0.18181887f;
        private float e = -0.90904963f;
        private float f = -0.55321634f;
        private float g = -0.05259979f;
        private float h = 24.0f;
        private float i = 8.61f;
        private float j = 4.25f;
        private float k = 60.39f;
        private float l = 15.6f;
        private float m = 68.07f;
        private float n = 11.61f;

        public c() {
        }

        public double a(double d2, double d3, double d4, double d5) {
            double[] a2 = a(d3, d4);
            double abs = Math.abs(d5 - a2[0]);
            return abs > a2[1] * 2.0d ? d2 + abs : d2;
        }

        public double[] a(double d2, double d3) {
            return com.baidu.location.c.a.a().a(d2, d3);
        }
    }

    public class d extends Handler {
        public d() {
        }

        public void handleMessage(Message message) {
            if (com.baidu.location.f.isServing) {
                int i = message.what;
                if (i == 21) {
                    g.this.a(message);
                } else if (i == 41) {
                    g.this.k();
                } else if (i != 801) {
                    super.dispatchMessage(message);
                } else {
                    g.this.a((BDLocation) message.obj);
                }
            }
        }
    }

    class e {
        /* access modifiers changed from: private */
        public double b = -1.0d;
        /* access modifiers changed from: private */
        public long c = 0;
        private long d = 0;
        /* access modifiers changed from: private */
        public long e = 0;
        /* access modifiers changed from: private */
        public long f = 0;
        private long g = 0;
        private long h = 0;
        private long i = 0;
        private long j = 0;
        private long k = 0;
        private double l = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private double m = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private double n = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private double o = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private int p = 0;
        private int q = 0;
        private com.baidu.location.e.i r = null;
        private long s = 0;
        private int t = 0;
        private int u = 0;

        public e() {
        }

        /* access modifiers changed from: private */
        public void a() {
            this.b = -1.0d;
            this.c = 0;
            this.d = 0;
            this.f = 0;
            this.g = 0;
            this.h = 0;
            this.i = 0;
            this.j = 0;
            this.k = 0;
            this.l = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.m = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.p = 0;
            this.q = 0;
            this.r = null;
            this.s = 0;
            this.t = 0;
            this.u = 0;
            this.e = 0;
        }

        /* access modifiers changed from: private */
        public void a(double d2, double d3, double d4, long j2) {
            this.j = j2;
            this.u = 0;
        }

        /* access modifiers changed from: private */
        public void a(Location location, boolean z) {
            this.k = System.currentTimeMillis();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double d2 = this.l;
            if (d2 != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                float[] fArr = new float[2];
                Location.distanceBetween(this.m, d2, latitude, longitude, fArr);
                if (fArr[0] < 20.0f) {
                    this.p++;
                } else {
                    this.p = 0;
                }
                if (fArr[0] < 5.0f) {
                    this.q++;
                } else {
                    this.q = 0;
                }
            }
            this.l = longitude;
            this.m = latitude;
            if (location.hasSpeed() && location.getSpeed() > 3.0f) {
                this.h = System.currentTimeMillis();
            }
            if (location.getAccuracy() >= 50.0f || z) {
                this.t = 0;
            } else {
                this.t++;
            }
            if (this.t > 10 && System.currentTimeMillis() - this.c > 30000) {
                g.this.d();
            }
        }

        /* access modifiers changed from: private */
        public boolean a(double d2, double d3, double d4) {
            if (!g.this.ae.c()) {
                return true;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = this.f;
            if (j2 != 0 && currentTimeMillis - j2 > OkHttpUtils.DEFAULT_MILLISECONDS) {
                return true;
            }
            if (this.q >= 5 && d4 < 15.0d && currentTimeMillis - this.c > 20000) {
                float[] fArr = new float[2];
                Location.distanceBetween(this.o, this.n, d3, d2, fArr);
                if (fArr[0] > 30.0f) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        public boolean a(BDLocation bDLocation, double d2, String str) {
            long currentTimeMillis = System.currentTimeMillis();
            this.i = currentTimeMillis;
            this.b = d2;
            this.n = bDLocation.getLongitude();
            this.o = bDLocation.getLatitude();
            if (str.equals("wifi")) {
                this.c = currentTimeMillis;
            }
            if (str.equals("gps")) {
                this.e = currentTimeMillis;
            }
            if (e()) {
                this.f = currentTimeMillis;
            }
            g gVar = g.this;
            boolean unused = gVar.d = gVar.a(bDLocation.getLongitude(), bDLocation.getLatitude());
            if (g.this.d || g.this.c == 1) {
                this.g = currentTimeMillis;
            }
            long j2 = this.s;
            if (j2 != 0 && currentTimeMillis - j2 > 30000 && currentTimeMillis - this.j < OkHttpUtils.DEFAULT_MILLISECONDS && currentTimeMillis - this.k < OkHttpUtils.DEFAULT_MILLISECONDS) {
                return false;
            }
            if (this.t > 10 && currentTimeMillis - this.c > 30000) {
                return false;
            }
            if (currentTimeMillis - this.g > OkHttpUtils.DEFAULT_MILLISECONDS && currentTimeMillis - this.c > 30000) {
                return false;
            }
            long j3 = this.f;
            return j3 == 0 || currentTimeMillis - j3 <= DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS;
        }

        /* access modifiers changed from: private */
        public boolean b() {
            System.currentTimeMillis();
            if (g.this.n || this.p < 3) {
                return false;
            }
            if (!j.a().h().contains("&wifio") && g.this.c != 1) {
                return false;
            }
            this.u = 1;
            return true;
        }

        private boolean c() {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - this.h < OkHttpUtils.DEFAULT_MILLISECONDS && currentTimeMillis - this.c > 30000) {
                return false;
            }
            if (currentTimeMillis - this.k >= OkHttpUtils.DEFAULT_MILLISECONDS) {
                return true;
            }
            long j2 = this.j;
            return j2 == 0 || currentTimeMillis - j2 <= 16000 || currentTimeMillis - this.c <= 30000;
        }

        /* access modifiers changed from: private */
        public void d() {
            com.baidu.location.e.i q2 = j.a().q();
            if (q2.a != null) {
                long currentTimeMillis = System.currentTimeMillis();
                com.baidu.location.e.i iVar = this.r;
                if (iVar == null || !q2.b(iVar)) {
                    if (currentTimeMillis - this.s < OkHttpUtils.DEFAULT_MILLISECONDS) {
                        this.d = currentTimeMillis;
                    }
                    this.s = currentTimeMillis;
                    this.r = q2;
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean e() {
            if (this.u == 1 || !c() || this.b > 25.0d || System.currentTimeMillis() - this.i > 30000) {
                return false;
            }
            this.f = System.currentTimeMillis();
            return true;
        }
    }

    private class f {
        public int a = 10;
        /* access modifiers changed from: private */
        public List<a> c = Collections.synchronizedList(new ArrayList());

        private class a {
            public double a;
            public double b;
            public double c;

            public a(double d2, double d3, double d4) {
                this.a = d2;
                this.b = d3;
                this.c = d4;
            }
        }

        public f() {
        }

        public void a(BDLocation bDLocation) {
            this.c.add(new a(bDLocation.getLongitude(), bDLocation.getLatitude(), g.this.ae.b));
        }

        public String toString() {
            if (this.c.size() == 0) {
                return "";
            }
            StringBuffer stringBuffer = new StringBuffer();
            double d = this.c.get(0).a;
            double d2 = this.c.get(0).b;
            stringBuffer.append(String.format("%.6f:%.6f:%.1f", new Object[]{Double.valueOf(d), Double.valueOf(d2), Double.valueOf(this.c.get(0).c)}));
            int size = (this.c.size() > this.a ? this.c.size() - this.a : 0) + 1;
            while (size < this.c.size()) {
                stringBuffer.append(String.format(";%.0f:%.0f:%.1f", new Object[]{Double.valueOf((this.c.get(size).a - d) * 1000000.0d), Double.valueOf((this.c.get(size).b - d2) * 1000000.0d), Double.valueOf(this.c.get(size).c)}));
                size++;
                d = d;
            }
            return stringBuffer.toString();
        }
    }

    /* renamed from: com.baidu.location.indoor.g$g  reason: collision with other inner class name */
    class C0011g extends Thread {
        /* access modifiers changed from: private */
        public volatile boolean b = true;
        private long c = 0;
        private long d = 0;
        private long e = 0;

        C0011g() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:21:0x006f, code lost:
            if ((java.lang.System.currentTimeMillis() - r9.c) > com.baidu.location.indoor.g.p(r9.a)) goto L_0x0073;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r9 = this;
            L_0x0000:
                boolean r0 = r9.b
                if (r0 == 0) goto L_0x00f9
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                int r0 = r0.c
                r1 = 5000(0x1388, double:2.4703E-320)
                r3 = 1
                if (r0 != r3) goto L_0x001d
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                boolean r0 = r0.d
                if (r0 != 0) goto L_0x001d
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                long unused = r0.h = r1
                goto L_0x0024
            L_0x001d:
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                r4 = 3000(0xbb8, double:1.482E-320)
                long unused = r0.h = r4
            L_0x0024:
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                com.baidu.location.indoor.m r0 = r0.j
                int r0 = r0.c()
                if (r0 != r3) goto L_0x0036
                long r4 = java.lang.System.currentTimeMillis()
                r9.d = r4
            L_0x0036:
                long r4 = java.lang.System.currentTimeMillis()
                long r6 = r9.c
                long r4 = r4 - r6
                r6 = 17500(0x445c, double:8.646E-320)
                r0 = 0
                int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r8 <= 0) goto L_0x0046
                r4 = 1
                goto L_0x0047
            L_0x0046:
                r4 = 0
            L_0x0047:
                long r5 = java.lang.System.currentTimeMillis()
                long r7 = r9.d
                long r5 = r5 - r7
                int r7 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
                if (r7 >= 0) goto L_0x0072
                long r1 = java.lang.System.currentTimeMillis()
                long r5 = r9.c
                long r1 = r1 - r5
                r5 = 10000(0x2710, double:4.9407E-320)
                int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r7 <= 0) goto L_0x0060
                r4 = 1
            L_0x0060:
                long r1 = java.lang.System.currentTimeMillis()
                long r5 = r9.c
                long r1 = r1 - r5
                com.baidu.location.indoor.g r5 = com.baidu.location.indoor.g.this
                long r5 = r5.h
                int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r7 <= 0) goto L_0x0072
                goto L_0x0073
            L_0x0072:
                r3 = r4
            L_0x0073:
                if (r3 == 0) goto L_0x0090
                com.baidu.location.e.j r1 = com.baidu.location.e.j.a()
                r1.i()
                com.baidu.location.indoor.g r1 = com.baidu.location.indoor.g.this
                com.baidu.location.indoor.m r1 = r1.j
                r1.f()
                long r1 = java.lang.System.currentTimeMillis()
                r9.c = r1
                com.baidu.location.indoor.g r1 = com.baidu.location.indoor.g.this
                boolean unused = r1.i = r0
            L_0x0090:
                com.baidu.location.e.j r1 = com.baidu.location.e.j.a()
                boolean r1 = r1.r()
                if (r1 == 0) goto L_0x009f
                r1 = 0
                r9.e = r1
                goto L_0x00b4
            L_0x009f:
                long r1 = r9.e
                r3 = 1
                long r1 = r1 + r3
                r9.e = r1
                r3 = 10
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 < 0) goto L_0x00b4
                r9.b = r0
                com.baidu.location.indoor.g r0 = com.baidu.location.indoor.g.this
                r0.d()
                return
            L_0x00b4:
                com.baidu.location.indoor.g r1 = com.baidu.location.indoor.g.this
                boolean r1 = r1.n
                if (r1 == 0) goto L_0x00ef
                com.baidu.location.indoor.g r1 = com.baidu.location.indoor.g.this
                com.baidu.location.indoor.g$e r1 = r1.ae
                if (r1 == 0) goto L_0x00ef
                long r1 = java.lang.System.currentTimeMillis()
                com.baidu.location.indoor.g r3 = com.baidu.location.indoor.g.this
                long r3 = r3.q
                long r1 = r1 - r3
                r3 = 30000(0x7530, double:1.4822E-319)
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 <= 0) goto L_0x00ef
                long r1 = java.lang.System.currentTimeMillis()
                com.baidu.location.indoor.g r5 = com.baidu.location.indoor.g.this
                com.baidu.location.indoor.g$e r5 = r5.ae
                long r5 = r5.f
                long r1 = r1 - r5
                int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
                if (r5 <= 0) goto L_0x00ef
                com.baidu.location.indoor.g r1 = com.baidu.location.indoor.g.a()
                r1.d()
            L_0x00ef:
                r1 = 2000(0x7d0, double:9.88E-321)
                java.lang.Thread.sleep(r1)     // Catch:{ InterruptedException -> 0x00f6 }
                goto L_0x0000
            L_0x00f6:
                r1 = move-exception
                r9.b = r0
            L_0x00f9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.g.C0011g.run():void");
        }
    }

    private class h {
        public int a;
        public double b;
        public double c;
        public int d = 1;
        public double e;

        public h(int i, double d2, double d3, double d4) {
            this.a = i;
            this.b = d2;
            this.c = d3;
            this.e = d4;
        }

        public String toString() {
            if (this.c == this.e) {
                return String.format("%d:%.1f:%.2f", new Object[]{Integer.valueOf(this.d), Double.valueOf(this.c), Double.valueOf(this.b)});
            }
            return String.format("%d:%.1f:%.2f:%.1f", new Object[]{Integer.valueOf(this.d), Double.valueOf(this.c), Double.valueOf(this.b), Double.valueOf(this.e)});
        }
    }

    class i extends com.baidu.location.g.e {
        private boolean b = false;
        private boolean c = false;
        private String d = null;
        private String e = null;
        private long f = 0;
        private a q = null;
        private long r = 0;
        private long s = 0;

        public i() {
            this.k = new HashMap();
        }

        public void a() {
            this.h = k.e();
            if (g.this.x == null || g.this.y == null || !g.this.x.equals(g.this.y.a())) {
                this.d = "&nd_idf=1&indoor_polygon=1" + this.d;
            }
            this.i = 1;
            if (com.baidu.location.b.j.a().b()) {
                this.d += "&enc=2";
            }
            String encodeTp4 = Jni.encodeTp4(this.d);
            this.d = null;
            this.k.put(BaseLocationBox.TYPE, encodeTp4);
            this.r = System.currentTimeMillis();
        }

        public void a(boolean z) {
            if (!z || this.j == null) {
                g.v(g.this);
                int unused = g.this.X = 0;
                this.b = false;
                if (g.this.r > 40) {
                    g.this.d();
                } else {
                    return;
                }
            } else {
                try {
                    String str = this.j;
                    if (str.contains("enc") && com.baidu.location.b.j.a().b()) {
                        try {
                            JSONObject jSONObject = new JSONObject(str);
                            if (jSONObject.has("enc")) {
                                str = com.baidu.location.b.j.a().a(jSONObject.getString("enc"));
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (!g.this.n) {
                        this.b = false;
                        return;
                    }
                    BDLocation bDLocation = new BDLocation(str);
                    if (bDLocation.getLocType() == 161 && bDLocation.getBuildingID() != null) {
                        BDLocation unused2 = g.this.Y = new BDLocation(bDLocation);
                    }
                    String indoorLocationSurpportBuidlingName = bDLocation.getIndoorLocationSurpportBuidlingName();
                    if (indoorLocationSurpportBuidlingName != null && !g.this.Q.a(indoorLocationSurpportBuidlingName)) {
                        g.this.Q.a(indoorLocationSurpportBuidlingName, (a.C0010a) null);
                    }
                    if (g.this.S != null) {
                        g.this.S.a((d.b) new k(this));
                    }
                    n.a().b(true);
                    if (bDLocation.getBuildingName() != null) {
                        String unused3 = g.this.A = bDLocation.getBuildingName();
                    }
                    if (bDLocation.getFloor() != null) {
                        long unused4 = g.this.p = System.currentTimeMillis();
                        long currentTimeMillis = System.currentTimeMillis();
                        this.s = currentTimeMillis;
                        int i = (int) (currentTimeMillis - this.r);
                        if (i > 10000) {
                            int unused5 = g.this.X = 0;
                        } else if (i < 3000) {
                            int unused6 = g.this.X = 2;
                        } else {
                            int unused7 = g.this.X = 1;
                        }
                        if (bDLocation.getFloor().contains("-a")) {
                            boolean unused8 = g.this.K = true;
                            bDLocation.setFloor(bDLocation.getFloor().split("-")[0]);
                        } else {
                            boolean unused9 = g.this.K = false;
                        }
                        g.this.E.add(bDLocation.getFloor());
                    }
                    Message obtainMessage = g.this.a.obtainMessage(21);
                    obtainMessage.obj = bDLocation;
                    obtainMessage.sendToTarget();
                } catch (Exception e3) {
                }
            }
            if (this.k != null) {
                this.k.clear();
            }
            this.b = false;
        }

        public void b() {
            if (this.b) {
                this.c = true;
            } else if (g.this.c != 1 || g.this.d || System.currentTimeMillis() - this.f >= 30000 || System.currentTimeMillis() - g.this.ae.c <= 30000) {
                StringBuffer stringBuffer = new StringBuffer(1024);
                String h = com.baidu.location.e.b.a().f().h();
                String f2 = com.baidu.location.e.f.a().f();
                double unused = g.this.J = 0.5d;
                com.baidu.location.e.i q2 = j.a().q();
                String a2 = g.this.a(q2);
                if (a2 == null) {
                    a2 = q2.a(g.this.e, true, false);
                }
                if (a2 != null && a2.length() >= 10) {
                    String str = this.e;
                    if (str == null || !str.equals(a2)) {
                        this.e = a2;
                        this.b = true;
                        stringBuffer.append(h);
                        if (f2 != null) {
                            stringBuffer.append(f2);
                        }
                        stringBuffer.append("&coor=gcj02");
                        stringBuffer.append("&lt=1");
                        stringBuffer.append(a2);
                        if (!(g.this.j == null || g.this.O > 2 || g.this.j.h() == null)) {
                            stringBuffer.append("&idsl=" + g.this.j.h());
                        }
                        int size = g.this.M.size();
                        stringBuffer.append(g.this.a(size));
                        int unused2 = g.this.N = size;
                        g.z(g.this);
                        stringBuffer.append("&drsi=" + g.this.O);
                        stringBuffer.append("&drc=" + g.this.u);
                        if (!(g.this.H == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || g.this.I == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)) {
                            stringBuffer.append("&lst_idl=" + String.format(Locale.CHINA, "%.5f:%.5f", new Object[]{Double.valueOf(g.this.H), Double.valueOf(g.this.I)}));
                        }
                        int unused3 = g.this.u = 0;
                        stringBuffer.append("&idpfv=1");
                        stringBuffer.append("&iflxy=" + g.this.af.toString());
                        g.this.af.c.clear();
                        if (g.this.j != null && g.this.j.g()) {
                            stringBuffer.append("&pdr2=1");
                        }
                        if (!(g.this.S == null || g.this.S.e() == null || !g.this.S.g())) {
                            stringBuffer.append("&bleand=");
                            stringBuffer.append(g.this.S.e());
                            stringBuffer.append("&bleand_et=");
                            stringBuffer.append(g.this.S.f());
                        }
                        g.D(g.this);
                        if (g.this.R != null) {
                            stringBuffer.append(g.this.R);
                            String unused4 = g.this.R = null;
                        }
                        String d2 = com.baidu.location.b.a.a().d();
                        if (d2 != null) {
                            stringBuffer.append(d2);
                        }
                        stringBuffer.append(com.baidu.location.g.b.a().a(true));
                        this.d = stringBuffer.toString();
                        ExecutorService b2 = v.a().b();
                        if (b2 != null) {
                            a(b2, k.f);
                        } else {
                            c(k.f);
                        }
                        this.f = System.currentTimeMillis();
                    }
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0012, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized void c() {
            /*
                r1 = this;
                monitor-enter(r1)
                boolean r0 = r1.b     // Catch:{ all -> 0x0013 }
                if (r0 == 0) goto L_0x0007
                monitor-exit(r1)
                return
            L_0x0007:
                boolean r0 = r1.c     // Catch:{ all -> 0x0013 }
                if (r0 == 0) goto L_0x0011
                r0 = 0
                r1.c = r0     // Catch:{ all -> 0x0013 }
                r1.b()     // Catch:{ all -> 0x0013 }
            L_0x0011:
                monitor-exit(r1)
                return
            L_0x0013:
                r0 = move-exception
                monitor-exit(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.indoor.g.i.c():void");
        }
    }

    private g() {
        this.c = 0;
        this.d = false;
        this.e = 32;
        this.h = 3000;
        this.i = true;
        this.a = null;
        this.j = null;
        this.k = null;
        this.l = null;
        this.m = 0;
        this.n = false;
        this.o = false;
        this.p = 0;
        this.q = 0;
        this.r = 0;
        this.s = null;
        this.u = 0;
        this.v = 0;
        this.w = null;
        this.x = null;
        this.y = null;
        this.z = null;
        this.A = null;
        this.B = null;
        this.C = 0;
        this.D = 3;
        this.E = null;
        this.F = 20;
        this.G = null;
        this.H = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.I = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.J = 0.4d;
        this.K = false;
        this.L = true;
        this.M = Collections.synchronizedList(new ArrayList());
        this.N = -1;
        this.O = 0;
        this.P = 0;
        this.R = null;
        this.S = null;
        this.T = false;
        this.b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.X = 2;
        this.Y = null;
        this.Z = false;
        this.aa = false;
        this.ab = false;
        this.ac = false;
        this.ad = null;
        this.ae = null;
        this.af = null;
        this.ag = null;
        this.f = false;
        this.a = new d();
        try {
            com.baidu.location.indoor.mapversion.c.a.a(com.baidu.location.f.getServiceContext());
        } catch (Exception e2) {
        }
        try {
            com.baidu.location.indoor.mapversion.c.c.a(com.baidu.location.f.getServiceContext());
        } catch (Exception e3) {
        }
        p pVar = new p();
        this.U = pVar;
        pVar.a(1000);
        this.V = new h(this);
        this.t = new i(this);
        this.j = new m(com.baidu.location.f.getServiceContext(), this.t);
        this.l = new i();
        this.E = new c<>(this.D);
        this.G = new c<>(this.F);
        this.Q = new a(com.baidu.location.f.getServiceContext());
        this.ad = new c();
        i();
        this.ae = new e();
        this.af = new f();
        this.ag = new b();
    }

    static /* synthetic */ int D(g gVar) {
        int i2 = gVar.P;
        gVar.P = i2 + 1;
        return i2;
    }

    public static synchronized g a() {
        g gVar;
        synchronized (g.class) {
            if (g == null) {
                g = new g();
            }
            gVar = g;
        }
        return gVar;
    }

    /* access modifiers changed from: private */
    public String a(int i2) {
        if (this.M.size() == 0) {
            return "&dr=0:0";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("&dr=");
        int i3 = 1;
        this.M.get(0).d = 1;
        sb.append(this.M.get(0).toString());
        int i4 = this.M.get(0).a;
        while (i3 < this.M.size() && i3 <= i2) {
            this.M.get(i3).d = this.M.get(i3).a - i4;
            sb.append(";");
            sb.append(this.M.get(i3).toString());
            i4 = this.M.get(i3).a;
            i3++;
        }
        return sb.toString();
    }

    /* access modifiers changed from: private */
    public String a(com.baidu.location.e.i iVar) {
        int a2 = iVar.a();
        if (a2 <= this.e) {
            return iVar.a(this.e, true, true) + "&aprk=0";
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < a2; i2++) {
            String lowerCase = iVar.a.get(i2).BSSID.replaceAll(LogUtils.COLON, "").toLowerCase();
            a aVar = this.Q;
            if (aVar == null || !aVar.b(lowerCase)) {
                arrayList2.add(iVar.a.get(i2));
            } else {
                arrayList.add(iVar.a.get(i2));
            }
        }
        String str = arrayList.size() > 0 ? "&aprk=3" : "";
        if (str.equals("")) {
            a aVar2 = this.Q;
            str = (aVar2 == null || !aVar2.b()) ? "&aprk=1" : "&aprk=2";
        }
        arrayList.addAll(arrayList2);
        iVar.a = arrayList;
        String a3 = iVar.a(this.e, true, true);
        return a3 + str;
    }

    /* access modifiers changed from: private */
    public void a(Message message) {
        m mVar;
        l lVar;
        if (this.n) {
            this.o = false;
            BDLocation bDLocation = (BDLocation) message.obj;
            if (bDLocation.getLocType() == 161) {
                m();
                if (!(bDLocation.getIndoorSurpportPolygon() == null || bDLocation.getIndoorLocationSurpportBuidlingID() == null || ((lVar = this.y) != null && lVar.a().equals(bDLocation.getBuildingID())))) {
                    String[] split = bDLocation.getIndoorSurpportPolygon().split("\\|");
                    Location[] locationArr = new Location[split.length];
                    for (int i2 = 0; i2 < split.length; i2++) {
                        String[] split2 = split[i2].split(",");
                        Location location = new Location("gps");
                        location.setLatitude(Double.valueOf(split2[1]).doubleValue());
                        location.setLongitude(Double.valueOf(split2[0]).doubleValue());
                        locationArr[i2] = location;
                    }
                    this.y = new l(bDLocation.getIndoorLocationSurpportBuidlingID(), locationArr);
                }
                if (this.L && this.S != null) {
                    if ((((bDLocation.getIndoorLocationSource() >> 2) & 1) == 1) && this.S.a()) {
                        this.L = false;
                        this.S.b();
                    }
                }
                this.r = 0;
                if (bDLocation.getBuildingID() != null) {
                    this.o = true;
                    bDLocation.setIndoorLocMode(true);
                    if (bDLocation.getRetFields("tp") == null || !bDLocation.getRetFields("tp").equalsIgnoreCase("ble")) {
                        this.T = false;
                    } else {
                        bDLocation.setRadius(8.0f);
                        bDLocation.setNetworkLocationType("ble");
                        this.T = true;
                    }
                    String retFields = bDLocation.getRetFields("pdr2");
                    if (!(retFields == null || !retFields.equals("1") || (mVar = this.j) == null)) {
                        mVar.a(true);
                    }
                    this.x = bDLocation.getBuildingID();
                    this.z = bDLocation.getBuildingName();
                    this.B = bDLocation.getNetworkLocationType();
                    this.C = bDLocation.isParkAvailable();
                    int unused = this.ag.a(bDLocation);
                    if (bDLocation.getFloor().equals(l())) {
                        if (this.w == null) {
                            this.w = bDLocation.getFloor();
                        }
                        com.baidu.location.indoor.mapversion.c.a.a().a(bDLocation.getLongitude(), bDLocation.getLatitude());
                        a(bDLocation.getBuildingName(), bDLocation.getFloor());
                        if (bDLocation.getFloor().equals(l())) {
                            if (!bDLocation.getFloor().equalsIgnoreCase(this.w) && this.ab) {
                                this.ae.a();
                                com.baidu.location.indoor.mapversion.b.a.c();
                                this.ac = com.baidu.location.indoor.mapversion.b.a.a(bDLocation.getFloor());
                            }
                            this.w = bDLocation.getFloor();
                            m mVar2 = this.j;
                            if (mVar2 != null && mVar2.e() >= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && bDLocation.getDirection() <= 0.0f) {
                                bDLocation.setDirection((float) this.j.e());
                            }
                            double[] a2 = com.baidu.location.indoor.mapversion.b.a.a(bDLocation);
                            if (!(a2 == null || a2[0] == -1.0d || a2[0] != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE)) {
                                bDLocation.setLongitude(a2[1]);
                                bDLocation.setLatitude(a2[2]);
                                bDLocation.setFusionLocInfo("res", a2);
                                bDLocation.setRadius((float) a2[5]);
                                bDLocation.setDirection((float) a2[6]);
                                bDLocation.setSpeed((float) a2[8]);
                                if (!this.ae.a(bDLocation, a2[5], "wifi")) {
                                    d();
                                    return;
                                }
                            }
                            this.I = bDLocation.getLatitude();
                            this.H = bDLocation.getLongitude();
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            } else if (bDLocation.getLocType() == 63) {
                int i3 = this.r + 1;
                this.r = i3;
                if (i3 > 10) {
                    d();
                } else {
                    return;
                }
            } else {
                this.r = 0;
            }
            if (this.o) {
                if (bDLocation.getTime() == null) {
                    bDLocation.setTime(this.b.format(new Date()));
                }
                BDLocation bDLocation2 = new BDLocation(bDLocation);
                String networkLocationType = bDLocation2.getNetworkLocationType();
                bDLocation2.setNetworkLocationType(networkLocationType + "2");
                p pVar = this.U;
                if (pVar == null || !pVar.c()) {
                    a(bDLocation2, 21);
                } else {
                    this.U.a(bDLocation2);
                }
            }
            this.l.c();
        }
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation) {
    }

    /* access modifiers changed from: private */
    public void a(BDLocation bDLocation, int i2) {
        if (bDLocation != null) {
            if (bDLocation.getNetworkLocationType().startsWith("vps")) {
                if (bDLocation.getLongitude() == -1.0d && bDLocation.getLatitude() == -1.0d) {
                    bDLocation.setUserIndoorState(-1);
                } else {
                    bDLocation.setUserIndoorState(1);
                }
                bDLocation.setIndoorNetworkState(this.X);
                com.baidu.location.b.a.a().a(bDLocation);
                return;
            }
            if (this.Y != null) {
                if (bDLocation.getAddrStr() == null && this.Y.getAddrStr() != null) {
                    bDLocation.setAddr(this.Y.getAddress());
                    bDLocation.setAddrStr(this.Y.getAddrStr());
                }
                if (bDLocation.getPoiList() == null && this.Y.getPoiList() != null) {
                    bDLocation.setPoiList(this.Y.getPoiList());
                }
                if (bDLocation.getPoiRegion() == null && this.Y.getPoiRegion() != null) {
                    bDLocation.setPoiRegion(new PoiRegion(this.Y.getPoiRegion()));
                }
                if (bDLocation.getLocationDescribe() == null && this.Y.getLocationDescribe() != null) {
                    bDLocation.setLocationDescribe(this.Y.getLocationDescribe());
                }
                if (bDLocation.getNrlResult() == null) {
                    bDLocation.setNrlData(this.Y.getNrlResult());
                }
            }
            if (bDLocation != null) {
                bDLocation.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis())));
                if (bDLocation.getNetworkLocationType().contains("2")) {
                    String networkLocationType = bDLocation.getNetworkLocationType();
                    bDLocation.setNetworkLocationType(networkLocationType.substring(0, networkLocationType.length() - 1));
                    bDLocation.setUserIndoorState(1);
                    bDLocation.setIndoorNetworkState(this.X);
                    com.baidu.location.b.a.a().a(bDLocation);
                    BDLocation bDLocation2 = new BDLocation(bDLocation);
                    bDLocation2.setRadius(this.T ? 8.0f : 15.0f);
                    Message obtainMessage = this.a.obtainMessage(801);
                    obtainMessage.obj = bDLocation2;
                    obtainMessage.sendToTarget();
                }
            }
        }
    }

    private void a(String str, String str2) {
        String str3 = this.z;
        if (str3 == null || !str3.equals(str) || !this.ab) {
            com.baidu.location.indoor.mapversion.c.a a2 = com.baidu.location.indoor.mapversion.c.a.a();
            a2.a(CoordinateType.GCJ02);
            a2.a(str, (a.c) new j(this, str, str2));
        }
    }

    static /* synthetic */ int g(g gVar) {
        int i2 = gVar.u;
        gVar.u = i2 + 1;
        return i2;
    }

    private void i() {
    }

    private void j() {
        this.E.clear();
        this.G.clear();
        this.p = 0;
        this.r = 0;
        this.C = 0;
        this.v = 0;
        this.w = null;
        this.x = null;
        this.z = null;
        this.A = null;
        this.B = null;
        this.L = true;
        this.J = 0.4d;
        this.T = false;
        this.H = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.I = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        this.K = false;
        this.O = 0;
        this.u = 0;
        this.s = null;
        this.q = 0;
        this.ae.a();
        com.baidu.location.indoor.mapversion.b.a.c();
        if (this.ab) {
            com.baidu.location.indoor.mapversion.c.a.a().b();
        }
        this.ac = false;
        this.ab = false;
        n.a().b(false);
        d dVar = this.S;
        if (dVar != null) {
            dVar.c();
        }
    }

    /* access modifiers changed from: private */
    public void k() {
        if (this.n) {
            this.i = true;
            this.ae.d();
            this.l.b();
            this.m = System.currentTimeMillis();
        }
    }

    private String l() {
        if (this.ag.b == 1 && this.ag.a != null) {
            return this.ag.b();
        }
        HashMap hashMap = new HashMap();
        int size = this.E.size();
        String str = null;
        int i2 = -1;
        int i3 = 0;
        String str2 = "";
        while (i3 < size) {
            try {
                String str3 = (String) this.E.get(i3);
                str2 = str2 + str3 + LogUtils.VERTICAL;
                hashMap.put(str3, hashMap.containsKey(str3) ? Integer.valueOf(((Integer) hashMap.get(str3)).intValue() + 1) : 1);
                i3++;
            } catch (Exception e2) {
                return this.w;
            }
        }
        for (String str4 : hashMap.keySet()) {
            if (((Integer) hashMap.get(str4)).intValue() > i2) {
                i2 = ((Integer) hashMap.get(str4)).intValue();
                str = str4;
            }
        }
        return str;
    }

    private void m() {
        for (int i2 = this.N; i2 >= 0 && this.M.size() > 0; i2--) {
            this.M.remove(0);
        }
        this.N = -1;
    }

    static /* synthetic */ int v(g gVar) {
        int i2 = gVar.r;
        gVar.r = i2 + 1;
        return i2;
    }

    static /* synthetic */ int z(g gVar) {
        int i2 = gVar.O;
        gVar.O = i2 + 1;
        return i2;
    }

    public boolean a(double d2, double d3) {
        Map<String, c.b> d4;
        com.baidu.location.indoor.mapversion.c.c a2 = com.baidu.location.indoor.mapversion.c.c.a();
        if (!a2.c() || !a2.b() || (d4 = a2.d()) == null) {
            return false;
        }
        String str = null;
        Iterator<String> it = d4.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            c.b bVar = d4.get(it.next());
            if (d2 > bVar.e && d2 < bVar.c && d3 > bVar.f && d3 < bVar.d) {
                String str2 = bVar.b;
                str = bVar.a;
                String str3 = bVar.g;
                break;
            }
        }
        return str != null;
    }

    public boolean a(Location location, ArrayList<ArrayList<Float>> arrayList) {
        String str;
        if (arrayList.size() == 0 || !com.baidu.location.e.f.a().j()) {
            return false;
        }
        if (!this.n && location.getSpeed() > 3.0f) {
            return false;
        }
        double[] coorEncrypt = Jni.coorEncrypt(location.getLongitude(), location.getLatitude(), BDLocation.BDLOCATION_WGS84_TO_GCJ02);
        double d2 = coorEncrypt[0];
        double d3 = coorEncrypt[1];
        double accuracy = (double) location.getAccuracy();
        double bearing = (double) location.getBearing();
        double altitude = location.getAltitude();
        double speed = (double) location.getSpeed();
        boolean z2 = a(d2, d3) || this.c == 1;
        if (!this.n && !z2) {
            return false;
        }
        try {
            double d4 = speed;
            this.ae.a(location, z2);
            if (this.ae.b()) {
                c();
                return true;
            } else if (!e()) {
                return false;
            } else {
                double d5 = d4;
                double d6 = altitude;
                double d7 = bearing;
                double d8 = accuracy;
                if (this.ae.a(d2, d3, accuracy)) {
                    com.baidu.location.indoor.mapversion.b.a.c();
                }
                double[] a2 = com.baidu.location.indoor.mapversion.b.a.a(d2, d3, this.ad.a(d8, d2, d3, d6), d7, d5);
                if (a2 == null) {
                    return false;
                }
                if (a2[0] == -1.0d) {
                    return false;
                }
                if (a2[0] != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                    return false;
                }
                BDLocation bDLocation = new BDLocation();
                bDLocation.setAltitude(d6);
                bDLocation.setLatitude(a2[2]);
                bDLocation.setLongitude(a2[1]);
                if (this.T) {
                    bDLocation.setRadius(8.0f);
                } else {
                    bDLocation.setRadius(15.0f);
                }
                bDLocation.setDirection((float) d7);
                bDLocation.setSpeed((float) d5);
                bDLocation.setLocType(BDLocation.TypeNetWorkLocation);
                bDLocation.setNetworkLocationType("gps");
                if (System.currentTimeMillis() - this.ae.c < 20000) {
                    bDLocation.setFloor(this.w);
                    bDLocation.setBuildingName(this.z);
                    str = this.x;
                } else {
                    str = null;
                    bDLocation.setFloor((String) null);
                    bDLocation.setBuildingName((String) null);
                }
                bDLocation.setBuildingID(str);
                bDLocation.setIndoorLocMode(true);
                this.I = bDLocation.getLatitude();
                this.H = bDLocation.getLongitude();
                bDLocation.setFusionLocInfo("res", a2);
                bDLocation.setRadius((float) a2[5]);
                bDLocation.setDirection((float) a2[6]);
                bDLocation.setSpeed((float) a2[8]);
                bDLocation.setTime(this.b.format(new Date()));
                BDLocation bDLocation2 = new BDLocation(bDLocation);
                String networkLocationType = bDLocation2.getNetworkLocationType();
                bDLocation2.setNetworkLocationType(networkLocationType + "2");
                if (this.U == null || !this.U.c()) {
                    a(bDLocation2, 21);
                } else {
                    this.U.a(bDLocation2);
                }
                if (this.ae.a(bDLocation, a2[5], "gps")) {
                    return true;
                }
                d();
                return true;
            }
        } catch (Exception e2) {
            return false;
        }
    }

    public boolean a(Bundle bundle) {
        if (bundle == null || this.W == null) {
            return false;
        }
        a.d dVar = new a.d();
        dVar.b(bundle.getString("bid")).c(bundle.getString("code"));
        dVar.a(bundle.getDouble("fov")).a(bundle.getFloatArray("gravity"));
        dVar.a(bundle.getString(TtmlNode.TAG_IMAGE));
        dVar.a(bundle.getBoolean("force_online"));
        this.W.a(dVar);
        return true;
    }

    public synchronized void b() {
        if (this.n) {
            this.E.clear();
        }
    }

    public boolean b(Bundle bundle) {
        if (bundle == null) {
            return false;
        }
        this.c = bundle.getInt("mode");
        return true;
    }

    public synchronized void c() {
        if (!this.n) {
            com.baidu.location.indoor.mapversion.b.a.b();
            this.p = System.currentTimeMillis();
            this.q = System.currentTimeMillis();
            this.j.a();
            C0011g gVar = new C0011g();
            this.k = gVar;
            gVar.start();
            this.o = false;
            this.n = true;
            this.S = d.a(com.baidu.location.f.getServiceContext());
            this.O = 0;
            this.u = 0;
            n.a().b(true);
        }
    }

    public synchronized void d() {
        if (this.n) {
            this.n = false;
            this.j.b();
            if (this.U != null && this.U.c()) {
                this.U.a();
            }
            if (this.Q != null) {
                this.Q.c();
            }
            if (this.S != null) {
                this.S.d();
            }
            if (this.k != null) {
                boolean unused = this.k.b = false;
                this.k.interrupt();
                this.k = null;
            }
            j();
            this.o = false;
            com.baidu.location.b.a.a().c();
        }
    }

    public boolean e() {
        return this.n;
    }

    public boolean f() {
        return this.n && this.ae.e();
    }

    public String g() {
        return this.w;
    }

    public String h() {
        return this.x;
    }
}
