package com.baidu.location.b;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.baidu.location.b.i;
import com.baidu.location.e.j;
import com.baidu.location.f;
import com.baidu.location.g.k;
import com.baidu.location.indoor.g;
import java.util.List;

public class l extends i {
    public static boolean g = false;
    private static l h = null;
    private double A;
    private boolean B;
    private long C;
    private long D;
    private a E;
    /* access modifiers changed from: private */
    public boolean F;
    /* access modifiers changed from: private */
    public boolean G;
    private boolean H;
    private boolean I;
    private boolean J;
    private b K;
    /* access modifiers changed from: private */
    public boolean L;
    private int M;
    private long N;
    private boolean O;
    private boolean P;
    public i.b e;
    public final Handler f;
    private boolean i;
    private String j;
    private BDLocation k;
    private BDLocation l;
    private com.baidu.location.e.i m;
    private com.baidu.location.e.a n;
    private com.baidu.location.e.i o;
    private com.baidu.location.e.a p;
    private boolean q;
    private volatile boolean r;
    /* access modifiers changed from: private */
    public boolean s;
    private long t;
    private long u;
    private Address v;
    private String w;
    private List<Poi> x;
    private PoiRegion y;
    private double z;

    private class a implements Runnable {
        final /* synthetic */ l a;

        public void run() {
            if (this.a.F) {
                boolean unused = this.a.F = false;
                boolean unused2 = this.a.G;
            }
        }
    }

    private class b implements Runnable {
        private b() {
        }

        /* synthetic */ b(l lVar, m mVar) {
            this();
        }

        public void run() {
            if (l.this.L) {
                boolean unused = l.this.L = false;
            }
            if (l.this.s) {
                boolean unused2 = l.this.s = false;
                l.this.h((Message) null);
            }
        }
    }

    private l() {
        this.i = true;
        this.e = null;
        this.j = null;
        this.k = null;
        this.l = null;
        this.m = null;
        this.n = null;
        this.o = null;
        this.p = null;
        this.q = true;
        this.r = false;
        this.s = false;
        this.t = 0;
        this.u = 0;
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = null;
        this.B = false;
        this.C = 0;
        this.D = 0;
        this.E = null;
        this.F = false;
        this.G = false;
        this.H = true;
        this.f = new i.a();
        this.I = false;
        this.J = false;
        this.K = null;
        this.L = false;
        this.M = 0;
        this.N = 0;
        this.O = false;
        this.P = true;
        this.e = new i.b();
    }

    private boolean a(com.baidu.location.e.a aVar) {
        this.b = com.baidu.location.e.b.a().f();
        if (this.b == aVar) {
            return false;
        }
        if (this.b == null || aVar == null) {
            return true;
        }
        return !aVar.a(this.b);
    }

    private boolean a(com.baidu.location.e.i iVar) {
        this.a = j.a().p();
        if (iVar == this.a) {
            return false;
        }
        if (this.a == null || iVar == null) {
            return true;
        }
        return !iVar.c(this.a);
    }

    public static synchronized l c() {
        l lVar;
        synchronized (l.class) {
            if (h == null) {
                h = new l();
            }
            lVar = h;
        }
        return lVar;
    }

    private void c(Message message) {
        if (!k.ay || k.c(f.getServiceContext())) {
            if (k.b()) {
                Log.d(com.baidu.location.g.a.a, "isInforbiddenTime on request location ...");
            }
            if (message.getData().getBoolean("isWaitingLocTag", false)) {
                g = true;
            }
            if (!g.a().f()) {
                int d = a.a().d(message);
                if (d == 1) {
                    d(message);
                } else if (d == 2) {
                    g(message);
                } else if (d != 3) {
                    throw new IllegalArgumentException(String.format("this type %d is illegal", new Object[]{Integer.valueOf(d)}));
                } else if (com.baidu.location.e.f.a().j()) {
                    e(message);
                }
            }
        } else {
            BDLocation bDLocation = new BDLocation();
            bDLocation.setLocType(62);
            a.a().a(bDLocation);
        }
    }

    private void d(Message message) {
        if (com.baidu.location.e.f.a().j()) {
            e(message);
            n.a().c();
            return;
        }
        g(message);
        n.a().b();
    }

    private void e(Message message) {
        BDLocation bDLocation = new BDLocation(com.baidu.location.e.f.a().g());
        if (k.g.equals("all") || k.h || k.j) {
            float[] fArr = new float[2];
            Location.distanceBetween(this.A, this.z, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
            if (fArr[0] < 100.0f) {
                Address address = this.v;
                if (address != null) {
                    bDLocation.setAddr(address);
                }
                String str = this.w;
                if (str != null) {
                    bDLocation.setLocationDescribe(str);
                }
                List<Poi> list = this.x;
                if (list != null) {
                    bDLocation.setPoiList(list);
                }
                PoiRegion poiRegion = this.y;
                if (poiRegion != null) {
                    bDLocation.setPoiRegion(poiRegion);
                }
            } else {
                this.B = true;
                g((Message) null);
            }
        }
        this.k = bDLocation;
        this.l = null;
        a.a().a(bDLocation);
    }

    private void f(Message message) {
        b bVar;
        if (j.a().g()) {
            this.s = true;
            if (this.K == null) {
                this.K = new b(this, (m) null);
            }
            if (this.L && (bVar = this.K) != null) {
                this.f.removeCallbacks(bVar);
            }
            this.f.postDelayed(this.K, 3500);
            this.L = true;
            return;
        }
        h(message);
    }

    /* access modifiers changed from: private */
    public void g(Message message) {
        this.M = 0;
        if (this.q) {
            this.M = 1;
            this.D = SystemClock.uptimeMillis();
            if (j.a().k()) {
                f(message);
            } else {
                h(message);
            }
        } else {
            f(message);
            this.D = SystemClock.uptimeMillis();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009e, code lost:
        if (r6 <= 0) goto L_0x00a0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void h(android.os.Message r14) {
        /*
            r13 = this;
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r13.t
            long r0 = r0 - r2
            boolean r2 = r13.r
            if (r2 == 0) goto L_0x0012
            r2 = 12000(0x2ee0, double:5.929E-320)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x0012
            return
        L_0x0012:
            long r0 = java.lang.System.currentTimeMillis()
            long r2 = r13.t
            long r0 = r0 - r2
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x003d
            long r0 = java.lang.System.currentTimeMillis()
            long r4 = r13.t
            long r0 = r0 - r4
            r4 = 1000(0x3e8, double:4.94E-321)
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x003d
            com.baidu.location.BDLocation r14 = r13.k
            if (r14 == 0) goto L_0x0039
            com.baidu.location.b.a r14 = com.baidu.location.b.a.a()
            com.baidu.location.BDLocation r0 = r13.k
            r14.a((com.baidu.location.BDLocation) r0)
        L_0x0039:
            r13.l()
            return
        L_0x003d:
            r0 = 1
            r13.r = r0
            com.baidu.location.e.a r1 = r13.n
            boolean r1 = r13.a((com.baidu.location.e.a) r1)
            r13.i = r1
            com.baidu.location.e.i r1 = r13.m
            boolean r1 = r13.a((com.baidu.location.e.i) r1)
            r4 = 0
            if (r1 != 0) goto L_0x00d0
            boolean r1 = r13.i
            if (r1 != 0) goto L_0x00d0
            com.baidu.location.BDLocation r1 = r13.k
            if (r1 == 0) goto L_0x00d0
            boolean r1 = r13.B
            if (r1 != 0) goto L_0x00d0
            com.baidu.location.BDLocation r1 = r13.l
            if (r1 == 0) goto L_0x0074
            long r5 = java.lang.System.currentTimeMillis()
            long r7 = r13.u
            long r5 = r5 - r7
            r7 = 30000(0x7530, double:1.4822E-319)
            int r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r1 <= 0) goto L_0x0074
            com.baidu.location.BDLocation r1 = r13.l
            r13.k = r1
            r13.l = r4
        L_0x0074:
            com.baidu.location.b.n r1 = com.baidu.location.b.n.a()
            boolean r1 = r1.d()
            if (r1 == 0) goto L_0x008b
            com.baidu.location.BDLocation r1 = r13.k
            com.baidu.location.b.n r5 = com.baidu.location.b.n.a()
            float r5 = r5.e()
            r1.setDirection(r5)
        L_0x008b:
            com.baidu.location.BDLocation r1 = r13.k
            int r1 = r1.getLocType()
            r5 = 62
            if (r1 != r5) goto L_0x00a0
            long r6 = java.lang.System.currentTimeMillis()
            long r8 = r13.N
            long r6 = r6 - r8
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 > 0) goto L_0x00a1
        L_0x00a0:
            r6 = r2
        L_0x00a1:
            com.baidu.location.BDLocation r1 = r13.k
            int r1 = r1.getLocType()
            r8 = 61
            if (r1 == r8) goto L_0x00c3
            com.baidu.location.BDLocation r1 = r13.k
            int r1 = r1.getLocType()
            r8 = 161(0xa1, float:2.26E-43)
            if (r1 == r8) goto L_0x00c3
            com.baidu.location.BDLocation r1 = r13.k
            int r1 = r1.getLocType()
            if (r1 != r5) goto L_0x00d0
            r8 = 15000(0x3a98, double:7.411E-320)
            int r1 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r1 >= 0) goto L_0x00d0
        L_0x00c3:
            com.baidu.location.b.a r14 = com.baidu.location.b.a.a()
            com.baidu.location.BDLocation r0 = r13.k
            r14.a((com.baidu.location.BDLocation) r0)
            r13.l()
            return
        L_0x00d0:
            long r5 = java.lang.System.currentTimeMillis()
            r13.t = r5
            java.lang.String r1 = r13.a((java.lang.String) r4)
            r5 = 0
            r13.J = r5
            if (r1 != 0) goto L_0x0173
            r13.J = r0
            long r6 = java.lang.System.currentTimeMillis()
            r13.N = r6
            java.lang.String[] r1 = r13.k()
            long r6 = java.lang.System.currentTimeMillis()
            long r8 = r13.C
            long r8 = r6 - r8
            r10 = 60000(0xea60, double:2.9644E-319)
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x00fc
            r13.C = r6
        L_0x00fc:
            com.baidu.location.e.j r6 = com.baidu.location.e.j.a()
            java.lang.String r6 = r6.m()
            if (r6 == 0) goto L_0x011f
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r6)
            java.lang.String r6 = r13.b()
            r7.append(r6)
            r1 = r1[r5]
            r7.append(r1)
            java.lang.String r1 = r7.toString()
            goto L_0x0139
        L_0x011f:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = ""
            r6.append(r7)
            java.lang.String r7 = r13.b()
            r6.append(r7)
            r1 = r1[r5]
            r6.append(r1)
            java.lang.String r1 = r6.toString()
        L_0x0139:
            com.baidu.location.e.a r6 = r13.b
            if (r6 == 0) goto L_0x015a
            com.baidu.location.e.a r6 = r13.b
            java.lang.String r6 = r6.h()
            if (r6 == 0) goto L_0x015a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            com.baidu.location.e.a r7 = r13.b
            java.lang.String r7 = r7.h()
            r6.append(r7)
            r6.append(r1)
            java.lang.String r1 = r6.toString()
        L_0x015a:
            com.baidu.location.g.b r6 = com.baidu.location.g.b.a()
            java.lang.String r6 = r6.a((boolean) r0)
            if (r6 == 0) goto L_0x0173
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            r7.append(r6)
            java.lang.String r1 = r7.toString()
        L_0x0173:
            java.lang.String r6 = r13.j
            if (r6 == 0) goto L_0x018a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r1)
            java.lang.String r1 = r13.j
            r6.append(r1)
            java.lang.String r1 = r6.toString()
            r13.j = r4
        L_0x018a:
            com.baidu.location.e.i r4 = r13.a
            if (r4 == 0) goto L_0x0194
            com.baidu.location.e.i r2 = r13.a
            long r2 = r2.f()
        L_0x0194:
            com.baidu.location.b.i$b r4 = r13.e
            r4.a(r1, r2)
            com.baidu.location.e.a r1 = r13.b
            r13.n = r1
            com.baidu.location.e.i r1 = r13.a
            r13.m = r1
            boolean r1 = r13.q
            if (r1 != r0) goto L_0x01bd
            r13.q = r5
            boolean r0 = com.baidu.location.e.j.j()
            if (r0 == 0) goto L_0x01b6
            if (r14 == 0) goto L_0x01b6
            com.baidu.location.b.a r0 = com.baidu.location.b.a.a()
            r0.e(r14)
        L_0x01b6:
            com.baidu.location.c.b r14 = com.baidu.location.c.b.a()
            r14.b()
        L_0x01bd:
            int r14 = r13.M
            if (r14 <= 0) goto L_0x01cd
            r0 = 2
            if (r14 != r0) goto L_0x01cb
            com.baidu.location.e.j r14 = com.baidu.location.e.j.a()
            r14.g()
        L_0x01cb:
            r13.M = r5
        L_0x01cd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.l.h(android.os.Message):void");
    }

    private String[] k() {
        boolean z2;
        b a2;
        int i2;
        String[] strArr = {"", "Location failed beacuse we can not get any loc information!"};
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("&apl=");
        int a3 = k.a(f.getServiceContext());
        String str = "Location failed beacuse we can not get any loc information in airplane mode, you can turn it off and try again!!";
        if (a3 == 1) {
            strArr[1] = str;
        }
        stringBuffer.append(a3);
        String d = k.d(f.getServiceContext());
        if (d.contains("0|0|")) {
            strArr[1] = "Location failed beacuse we can not get any loc information without any location permission!";
        }
        stringBuffer.append(d);
        if (Build.VERSION.SDK_INT >= 23) {
            stringBuffer.append("&loc=");
            int b2 = k.b(f.getServiceContext());
            if (b2 == 0) {
                strArr[1] = "Location failed beacuse we can not get any loc information with the phone loc mode is off, you can turn it on and try again!";
                z2 = true;
            } else {
                z2 = false;
            }
            stringBuffer.append(b2);
        } else {
            z2 = false;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            stringBuffer.append("&lmd=");
            int b3 = k.b(f.getServiceContext());
            if (b3 >= 0) {
                stringBuffer.append(b3);
            }
        }
        String g2 = com.baidu.location.e.b.a().g();
        String h2 = j.a().h();
        stringBuffer.append(h2);
        stringBuffer.append(g2);
        stringBuffer.append(k.e(f.getServiceContext()));
        if (a3 == 1) {
            a2 = b.a();
            i2 = 7;
        } else {
            if (d.contains("0|0|")) {
                b.a().a(62, 4, "Location failed beacuse we can not get any loc information without any location permission!");
            } else if (z2) {
                b.a().a(62, 5, "Location failed beacuse we can not get any loc information with the phone loc mode is off, you can turn it on and try again!");
            } else if (g2 == null || h2 == null || !g2.equals("&sim=1") || h2.equals("&wifio=1")) {
                b.a().a(62, 9, "Location failed beacuse we can not get any loc information!");
            } else {
                a2 = b.a();
                i2 = 6;
                str = "Location failed beacuse we can not get any loc information , you can insert a sim card or open wifi and try again!";
            }
            strArr[0] = stringBuffer.toString();
            return strArr;
        }
        a2.a(62, i2, str);
        strArr[0] = stringBuffer.toString();
        return strArr;
    }

    private void l() {
        this.r = false;
        this.G = false;
        this.H = false;
        this.B = false;
        m();
        if (this.P) {
            this.P = false;
        }
    }

    private void m() {
        if (this.k != null && j.j()) {
            x.a().d();
        }
    }

    public Address a(BDLocation bDLocation) {
        if (k.g.equals("all") || k.h || k.j) {
            float[] fArr = new float[2];
            Location.distanceBetween(this.A, this.z, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
            if (fArr[0] < 100.0f) {
                Address address = this.v;
                if (address != null) {
                    return address;
                }
            } else {
                this.w = null;
                this.x = null;
                this.y = null;
                this.B = true;
                this.f.post(new m(this));
            }
        }
        return null;
    }

    public void a() {
        a aVar = this.E;
        if (aVar != null && this.F) {
            this.F = false;
            this.f.removeCallbacks(aVar);
        }
        if (com.baidu.location.e.f.a().j()) {
            BDLocation bDLocation = new BDLocation(com.baidu.location.e.f.a().g());
            if (k.g.equals("all") || k.h || k.j) {
                float[] fArr = new float[2];
                Location.distanceBetween(this.A, this.z, bDLocation.getLatitude(), bDLocation.getLongitude(), fArr);
                if (fArr[0] < 100.0f) {
                    Address address = this.v;
                    if (address != null) {
                        bDLocation.setAddr(address);
                    }
                    String str = this.w;
                    if (str != null) {
                        bDLocation.setLocationDescribe(str);
                    }
                    List<Poi> list = this.x;
                    if (list != null) {
                        bDLocation.setPoiList(list);
                    }
                    PoiRegion poiRegion = this.y;
                    if (poiRegion != null) {
                        bDLocation.setPoiRegion(poiRegion);
                    }
                }
            }
            a.a().a(bDLocation);
        } else if (this.G) {
            l();
            return;
        } else {
            if (this.i || this.k == null) {
                BDLocation bDLocation2 = new BDLocation();
                bDLocation2.setLocType(63);
                this.k = null;
                a.a().a(bDLocation2);
            } else {
                a.a().a(this.k);
            }
            this.l = null;
        }
        l();
    }

    public void a(Message message) {
        a aVar = this.E;
        if (aVar != null && this.F) {
            this.F = false;
            this.f.removeCallbacks(aVar);
        }
        BDLocation bDLocation = (BDLocation) message.obj;
        if (bDLocation != null && bDLocation.getLocType() == 167 && this.J) {
            bDLocation.setLocType(62);
        }
        b(bDLocation);
    }

    public void b(Message message) {
        if (this.I) {
            c(message);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:59:0x013b, code lost:
        r0 = com.baidu.location.g.k.b(com.baidu.location.f.getServiceContext());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void b(com.baidu.location.BDLocation r14) {
        /*
            r13 = this;
            com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation
            r0.<init>((com.baidu.location.BDLocation) r14)
            boolean r0 = r14.hasAddr()
            if (r0 == 0) goto L_0x001d
            com.baidu.location.Address r0 = r14.getAddress()
            r13.v = r0
            double r0 = r14.getLongitude()
            r13.z = r0
            double r0 = r14.getLatitude()
            r13.A = r0
        L_0x001d:
            java.lang.String r0 = r14.getLocationDescribe()
            if (r0 == 0) goto L_0x0035
            java.lang.String r0 = r14.getLocationDescribe()
            r13.w = r0
            double r0 = r14.getLongitude()
            r13.z = r0
            double r0 = r14.getLatitude()
            r13.A = r0
        L_0x0035:
            java.util.List r0 = r14.getPoiList()
            if (r0 == 0) goto L_0x004d
            java.util.List r0 = r14.getPoiList()
            r13.x = r0
            double r0 = r14.getLongitude()
            r13.z = r0
            double r0 = r14.getLatitude()
            r13.A = r0
        L_0x004d:
            com.baidu.location.PoiRegion r0 = r14.getPoiRegion()
            if (r0 == 0) goto L_0x0065
            com.baidu.location.PoiRegion r0 = r14.getPoiRegion()
            r13.y = r0
            double r0 = r14.getLongitude()
            r13.z = r0
            double r0 = r14.getLatitude()
            r13.A = r0
        L_0x0065:
            com.baidu.location.e.f r0 = com.baidu.location.e.f.a()
            boolean r0 = r0.j()
            r1 = 1120403456(0x42c80000, float:100.0)
            r2 = 2
            r3 = 0
            if (r0 == 0) goto L_0x00d1
            com.baidu.location.e.f r14 = com.baidu.location.e.f.a()
            java.lang.String r14 = r14.g()
            com.baidu.location.BDLocation r0 = new com.baidu.location.BDLocation
            r0.<init>((java.lang.String) r14)
            java.lang.String r14 = com.baidu.location.g.k.g
            java.lang.String r4 = "all"
            boolean r14 = r14.equals(r4)
            if (r14 != 0) goto L_0x0092
            boolean r14 = com.baidu.location.g.k.h
            if (r14 != 0) goto L_0x0092
            boolean r14 = com.baidu.location.g.k.j
            if (r14 == 0) goto L_0x00c6
        L_0x0092:
            float[] r14 = new float[r2]
            double r4 = r13.A
            double r6 = r13.z
            double r8 = r0.getLatitude()
            double r10 = r0.getLongitude()
            r12 = r14
            android.location.Location.distanceBetween(r4, r6, r8, r10, r12)
            r14 = r14[r3]
            int r14 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r14 >= 0) goto L_0x00c6
            com.baidu.location.Address r14 = r13.v
            if (r14 == 0) goto L_0x00b1
            r0.setAddr(r14)
        L_0x00b1:
            java.lang.String r14 = r13.w
            if (r14 == 0) goto L_0x00b8
            r0.setLocationDescribe(r14)
        L_0x00b8:
            java.util.List<com.baidu.location.Poi> r14 = r13.x
            if (r14 == 0) goto L_0x00bf
            r0.setPoiList(r14)
        L_0x00bf:
            com.baidu.location.PoiRegion r14 = r13.y
            if (r14 == 0) goto L_0x00c6
            r0.setPoiRegion(r14)
        L_0x00c6:
            com.baidu.location.b.a r14 = com.baidu.location.b.a.a()
            r14.a((com.baidu.location.BDLocation) r0)
            r13.l()
            return
        L_0x00d1:
            boolean r0 = r13.G
            if (r0 == 0) goto L_0x0116
            float[] r0 = new float[r2]
            com.baidu.location.BDLocation r1 = r13.k
            if (r1 == 0) goto L_0x00f1
            double r4 = r1.getLatitude()
            com.baidu.location.BDLocation r1 = r13.k
            double r6 = r1.getLongitude()
            double r8 = r14.getLatitude()
            double r10 = r14.getLongitude()
            r12 = r0
            android.location.Location.distanceBetween(r4, r6, r8, r10, r12)
        L_0x00f1:
            r0 = r0[r3]
            r1 = 1092616192(0x41200000, float:10.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x0102
            r13.k = r14
            boolean r0 = r13.H
            if (r0 != 0) goto L_0x0112
            r13.H = r3
            goto L_0x010b
        L_0x0102:
            int r0 = r14.getUserIndoorState()
            r1 = -1
            if (r0 <= r1) goto L_0x0112
            r13.k = r14
        L_0x010b:
            com.baidu.location.b.a r0 = com.baidu.location.b.a.a()
            r0.a((com.baidu.location.BDLocation) r14)
        L_0x0112:
            r13.l()
            return
        L_0x0116:
            int r0 = r14.getLocType()
            java.lang.String r4 = "cl"
            r5 = 167(0xa7, float:2.34E-43)
            r6 = 1
            r7 = 161(0xa1, float:2.26E-43)
            if (r0 != r5) goto L_0x012f
            com.baidu.location.b.b r0 = com.baidu.location.b.b.a()
            r1 = 8
            java.lang.String r2 = "NetWork location failed because baidu location service can not caculate the location!"
            r0.a(r5, r1, r2)
            goto L_0x0189
        L_0x012f:
            int r0 = r14.getLocType()
            if (r0 != r7) goto L_0x0189
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 < r5) goto L_0x0149
            android.content.Context r0 = com.baidu.location.f.getServiceContext()
            int r0 = com.baidu.location.g.k.b(r0)
            if (r0 == 0) goto L_0x0147
            if (r0 != r2) goto L_0x0149
        L_0x0147:
            r0 = 1
            goto L_0x014a
        L_0x0149:
            r0 = 0
        L_0x014a:
            if (r0 == 0) goto L_0x0156
            com.baidu.location.b.b r0 = com.baidu.location.b.b.a()
            java.lang.String r1 = "NetWork location successful, open gps will be better!"
            r0.a(r7, r6, r1)
            goto L_0x0189
        L_0x0156:
            float r0 = r14.getRadius()
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x0189
            java.lang.String r0 = r14.getNetworkLocationType()
            if (r0 == 0) goto L_0x0189
            java.lang.String r0 = r14.getNetworkLocationType()
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0189
            com.baidu.location.e.j r0 = com.baidu.location.e.j.a()
            java.lang.String r0 = r0.h()
            if (r0 == 0) goto L_0x0189
            java.lang.String r1 = "&wifio=1"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0189
            com.baidu.location.b.b r0 = com.baidu.location.b.b.a()
            java.lang.String r1 = "NetWork location successful, open wifi will be better!"
            r0.a(r7, r2, r1)
        L_0x0189:
            r0 = 0
            r13.l = r0
            int r1 = r14.getLocType()
            if (r1 != r7) goto L_0x01c4
            java.lang.String r1 = r14.getNetworkLocationType()
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x01c4
            com.baidu.location.BDLocation r1 = r13.k
            if (r1 == 0) goto L_0x01c4
            int r1 = r1.getLocType()
            if (r1 != r7) goto L_0x01c4
            com.baidu.location.BDLocation r1 = r13.k
            java.lang.String r1 = r1.getNetworkLocationType()
            java.lang.String r2 = "wf"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x01c4
            long r1 = java.lang.System.currentTimeMillis()
            long r4 = r13.u
            long r1 = r1 - r4
            r4 = 30000(0x7530, double:1.4822E-319)
            int r7 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r7 >= 0) goto L_0x01c4
            r13.l = r14
            r3 = 1
        L_0x01c4:
            com.baidu.location.b.a r1 = com.baidu.location.b.a.a()
            if (r3 == 0) goto L_0x01d0
            com.baidu.location.BDLocation r2 = r13.k
            r1.a((com.baidu.location.BDLocation) r2)
            goto L_0x01d9
        L_0x01d0:
            r1.a((com.baidu.location.BDLocation) r14)
            long r1 = java.lang.System.currentTimeMillis()
            r13.u = r1
        L_0x01d9:
            boolean r1 = com.baidu.location.g.k.a((com.baidu.location.BDLocation) r14)
            if (r1 == 0) goto L_0x01e4
            if (r3 != 0) goto L_0x01e6
            r13.k = r14
            goto L_0x01e6
        L_0x01e4:
            r13.k = r0
        L_0x01e6:
            java.lang.String r14 = c
            java.lang.String r1 = "ssid\":\""
            java.lang.String r2 = "\""
            int r14 = com.baidu.location.g.k.a((java.lang.String) r14, (java.lang.String) r1, (java.lang.String) r2)
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r14 == r1) goto L_0x01ff
            com.baidu.location.e.i r1 = r13.m
            if (r1 == 0) goto L_0x01ff
            java.lang.String r14 = r1.b((int) r14)
            r13.j = r14
            goto L_0x0201
        L_0x01ff:
            r13.j = r0
        L_0x0201:
            com.baidu.location.e.j.j()
            r13.l()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.b.l.b(com.baidu.location.BDLocation):void");
    }

    public void c(BDLocation bDLocation) {
        this.k = new BDLocation(bDLocation);
    }

    public void d() {
        this.q = true;
        this.r = false;
        this.I = true;
    }

    public void e() {
        this.r = false;
        this.s = false;
        this.G = false;
        this.H = true;
        j();
        this.I = false;
    }

    public String f() {
        return this.w;
    }

    public List<Poi> g() {
        return this.x;
    }

    public PoiRegion h() {
        return this.y;
    }

    public void i() {
        if (this.s) {
            h((Message) null);
            this.s = false;
            return;
        }
        com.baidu.location.c.b.a().d();
    }

    public void j() {
        this.k = null;
    }
}
