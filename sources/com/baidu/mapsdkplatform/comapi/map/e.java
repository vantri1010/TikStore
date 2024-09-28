package com.baidu.mapsdkplatform.comapi.map;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.baidu.mapapi.common.EnvironmentUtilities;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapLayer;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.ParcelItem;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.l;
import com.baidu.mapsdkplatform.comjni.map.basemap.BaseMapCallback;
import com.baidu.mapsdkplatform.comjni.map.basemap.JNIBaseMap;
import com.baidu.mapsdkplatform.comjni.map.basemap.b;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class e implements b {
    private static int O;
    private static int P;
    private static List<JNIBaseMap> as;
    public static float d = 1096.0f;
    static long m = 0;
    private static final String r = l.class.getSimpleName();
    private boolean A = false;
    private al B;
    private ak C;
    private Context D;
    private List<d> E;
    private HashMap<MapLayer, d> F;
    private z G;
    private g H;
    private ag I;
    private aj J;
    private p K;
    private a L;
    private q M;
    private ah N;
    private int Q;
    private int R;
    private int S;
    private l.a T = new l.a();
    private VelocityTracker U;
    private long V;
    private long W;
    private long X;
    private long Y;
    private int Z;
    public float a = 21.0f;
    private float aa;
    private float ab;
    private boolean ac;
    private long ad;
    private long ae;
    private boolean af = false;
    private boolean ag = false;
    private float ah;
    private float ai;
    private float aj;
    private float ak;
    private long al = 0;
    private long am = 0;
    private f an;
    private String ao;
    private int ap;
    private b aq;
    private c ar;
    private boolean at = false;
    private Queue<a> au = new LinkedList();
    private boolean av = false;
    private boolean aw = false;
    public float b = 4.0f;
    public float c = 21.0f;
    boolean e = true;
    boolean f = true;
    boolean g = false;
    List<n> h;
    com.baidu.mapsdkplatform.comjni.map.basemap.a i;
    long j;
    boolean k;
    int l;
    boolean n;
    boolean o;
    boolean p;
    public MapStatusUpdate q = null;
    private boolean s;
    private boolean t;
    private boolean u = true;
    private boolean v = false;
    private boolean w = false;
    private boolean x = false;
    private boolean y = true;
    private boolean z = true;

    public static class a {
        public long a;
        public int b;
        public int c;
        public int d;
        public Bundle e;

        public a(long j, int i, int i2, int i3) {
            this.a = j;
            this.b = i;
            this.c = i2;
            this.d = i3;
        }

        public a(Bundle bundle) {
            this.e = bundle;
        }
    }

    public e(Context context, String str, int i2) {
        this.D = context;
        this.h = new ArrayList();
        this.ao = str;
        this.ap = i2;
    }

    private void R() {
        if (this.w || this.t || this.s || this.x) {
            if (this.a > 20.0f) {
                this.a = 20.0f;
            }
            if (E().a > 20.0f) {
                ad E2 = E();
                E2.a = 20.0f;
                a(E2);
                return;
            }
            return;
        }
        this.a = this.c;
    }

    private void S() {
        if (!this.n) {
            this.n = true;
            this.o = false;
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    if (next != null) {
                        next.a(E());
                    }
                }
            }
        }
    }

    private boolean T() {
        if (this.i == null || !this.k) {
            return true;
        }
        this.ag = false;
        if (!this.e) {
            return false;
        }
        float f2 = (float) (this.am - this.al);
        float abs = (Math.abs(this.aj - this.ah) * 1000.0f) / f2;
        float abs2 = (Math.abs(this.ak - this.ai) * 1000.0f) / f2;
        float sqrt = (float) Math.sqrt((double) ((abs * abs) + (abs2 * abs2)));
        if (sqrt <= 500.0f) {
            return false;
        }
        A();
        a(34, (int) (sqrt * 0.6f), (((int) this.ak) << 16) | ((int) this.aj));
        M();
        return true;
    }

    private Activity a(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return a(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    private void a(d dVar) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            dVar.a = aVar.a(dVar.c, dVar.d, dVar.b);
            this.E.add(dVar);
        }
    }

    private void a(String str, String str2, long j2) {
        try {
            Class<?> cls = Class.forName(str);
            Object newInstance = cls.newInstance();
            cls.getMethod(str2, new Class[]{Long.TYPE}).invoke(newInstance, new Object[]{Long.valueOf(j2)});
        } catch (Exception e2) {
        }
    }

    private void b(MotionEvent motionEvent) {
        if (!this.T.e) {
            long downTime = motionEvent.getDownTime();
            this.ae = downTime;
            if (downTime - this.ad < 400) {
                downTime = (Math.abs(motionEvent.getX() - this.aa) >= 120.0f || Math.abs(motionEvent.getY() - this.ab) >= 120.0f) ? this.ae : 0;
            }
            this.ad = downTime;
            this.aa = motionEvent.getX();
            this.ab = motionEvent.getY();
            a(4, 0, (((int) motionEvent.getY()) << 16) | ((int) motionEvent.getX()));
            this.ac = true;
        }
    }

    private void b(String str, Bundle bundle) {
        if (this.i != null) {
            this.H.a(str);
            this.H.a(bundle);
            this.i.b(this.H.a);
        }
    }

    private boolean c(MotionEvent motionEvent) {
        if (this.T.e || System.currentTimeMillis() - m < 300) {
            return true;
        }
        if (this.p) {
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    GeoPoint b2 = b((int) motionEvent.getX(), (int) motionEvent.getY());
                    if (next != null) {
                        next.d(b2);
                    }
                }
            }
            return true;
        }
        float abs = Math.abs(motionEvent.getX() - this.aa);
        float abs2 = Math.abs(motionEvent.getY() - this.ab);
        int i2 = (((double) SysOSUtil.getDensity()) > 1.5d ? 1 : (((double) SysOSUtil.getDensity()) == 1.5d ? 0 : -1));
        double density = (double) SysOSUtil.getDensity();
        if (i2 > 0) {
            density *= 1.5d;
        }
        float f2 = (float) density;
        if (this.ac && abs / f2 <= 3.0f && abs2 / f2 <= 3.0f) {
            return true;
        }
        this.ac = false;
        int x2 = (int) motionEvent.getX();
        int y2 = (int) motionEvent.getY();
        if (x2 < 0) {
            x2 = 0;
        }
        if (y2 < 0) {
            y2 = 0;
        }
        if (this.e) {
            BaiduMap.mapStatusReason = 1 | BaiduMap.mapStatusReason;
            S();
            a(3, 0, (y2 << 16) | x2);
        }
        return false;
    }

    private boolean d(MotionEvent motionEvent) {
        if (this.p) {
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    GeoPoint b2 = b((int) motionEvent.getX(), (int) motionEvent.getY());
                    if (next != null) {
                        next.e(b2);
                    }
                }
            }
            this.p = false;
            return true;
        }
        boolean z2 = !this.T.e && motionEvent.getEventTime() - this.ae < 400 && Math.abs(motionEvent.getX() - this.aa) < 10.0f && Math.abs(motionEvent.getY() - this.ab) < 10.0f;
        M();
        int x2 = (int) motionEvent.getX();
        int y2 = (int) motionEvent.getY();
        if (z2) {
            return false;
        }
        if (x2 < 0) {
            x2 = 0;
        }
        if (y2 < 0) {
            y2 = 0;
        }
        a(5, 0, (y2 << 16) | x2);
        return true;
    }

    private boolean e(float f2, float f3) {
        if (this.i == null || !this.k) {
            return true;
        }
        this.af = false;
        GeoPoint b2 = b((int) f2, (int) f3);
        if (b2 != null) {
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    if (next != null) {
                        next.b(b2);
                    }
                }
            }
            if (this.f) {
                ad E2 = E();
                E2.a += 1.0f;
                E2.d = b2.getLongitudeE6();
                E2.e = b2.getLatitudeE6();
                a(E2, 300);
                m = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    private boolean e(Bundle bundle) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.d(bundle);
    }

    private boolean f(Bundle bundle) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar;
        if (bundle == null || (aVar = this.i) == null) {
            return false;
        }
        boolean c2 = aVar.c(bundle);
        if (c2) {
            f(c2);
            this.i.b(this.B.a);
        }
        return c2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000c, code lost:
        r5 = (android.os.Bundle) r5.get("param");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void g(android.os.Bundle r5) {
        /*
            r4 = this;
            java.lang.String r0 = "param"
            java.lang.Object r1 = r5.get(r0)
            java.lang.String r2 = "type"
            java.lang.String r3 = "layer_addr"
            if (r1 == 0) goto L_0x002f
            java.lang.Object r5 = r5.get(r0)
            android.os.Bundle r5 = (android.os.Bundle) r5
            int r0 = r5.getInt(r2)
            com.baidu.mapsdkplatform.comapi.map.j r1 = com.baidu.mapsdkplatform.comapi.map.j.ground
            int r1 = r1.ordinal()
            if (r0 != r1) goto L_0x001f
            goto L_0x003b
        L_0x001f:
            com.baidu.mapsdkplatform.comapi.map.j r1 = com.baidu.mapsdkplatform.comapi.map.j.arc
            int r1 = r1.ordinal()
            if (r0 < r1) goto L_0x0028
            goto L_0x003b
        L_0x0028:
            com.baidu.mapsdkplatform.comapi.map.j r1 = com.baidu.mapsdkplatform.comapi.map.j.popup
            int r1 = r1.ordinal()
            goto L_0x003b
        L_0x002f:
            int r0 = r5.getInt(r2)
            com.baidu.mapsdkplatform.comapi.map.j r1 = com.baidu.mapsdkplatform.comapi.map.j.ground
            int r1 = r1.ordinal()
            if (r0 != r1) goto L_0x0043
        L_0x003b:
            com.baidu.mapsdkplatform.comapi.map.aj r0 = r4.J
            long r0 = r0.a
            r5.putLong(r3, r0)
            goto L_0x004c
        L_0x0043:
            com.baidu.mapsdkplatform.comapi.map.j r1 = com.baidu.mapsdkplatform.comapi.map.j.arc
            int r1 = r1.ordinal()
            if (r0 < r1) goto L_0x0028
            goto L_0x003b
        L_0x004c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.e.g(android.os.Bundle):void");
    }

    static void m(boolean z2) {
        List<JNIBaseMap> b2 = com.baidu.mapsdkplatform.comjni.map.basemap.a.b();
        as = b2;
        if (b2 == null || b2.size() == 0) {
            com.baidu.mapsdkplatform.comjni.map.basemap.a.c(0, z2);
            return;
        }
        com.baidu.mapsdkplatform.comjni.map.basemap.a.c(as.get(0).a, z2);
        for (JNIBaseMap next : as) {
            if (next != null) {
                next.ClearLayer(next.a, -1);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void A() {
        if (!this.n && !this.o) {
            this.o = true;
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    if (next != null) {
                        next.a(E());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void B() {
        this.o = false;
        this.n = false;
        List<n> list = this.h;
        if (list != null) {
            for (n next : list) {
                if (next != null) {
                    next.c(E());
                }
            }
        }
    }

    public boolean C() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            return aVar.a(this.I.a);
        }
        return false;
    }

    public boolean D() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            return aVar.a(this.ar.a);
        }
        return false;
    }

    public ad E() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        Bundle h2 = aVar.h();
        ad adVar = new ad();
        adVar.a(h2);
        return adVar;
    }

    public LatLngBounds F() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        Bundle i2 = aVar.i();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int i3 = i2.getInt("maxCoorx");
        int i4 = i2.getInt("minCoorx");
        builder.include(CoordUtil.mc2ll(new GeoPoint((double) i2.getInt("minCoory"), (double) i3))).include(CoordUtil.mc2ll(new GeoPoint((double) i2.getInt("maxCoory"), (double) i4)));
        return builder.build();
    }

    public MapStatusUpdate G() {
        return this.q;
    }

    public int H() {
        return this.Q;
    }

    public int I() {
        return this.R;
    }

    /* access modifiers changed from: package-private */
    public ad J() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        Bundle j2 = aVar.j();
        ad adVar = new ad();
        adVar.a(j2);
        return adVar;
    }

    public double K() {
        return E().m;
    }

    /* access modifiers changed from: package-private */
    public void L() {
        List<n> list;
        this.n = false;
        if (!this.o && (list = this.h) != null) {
            for (n next : list) {
                if (next != null) {
                    next.c(E());
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void M() {
        this.S = 0;
        this.T.e = false;
        this.T.h = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    }

    public float[] N() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        return aVar.u();
    }

    public float[] O() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        return aVar.v();
    }

    public Queue<a> P() {
        return this.au;
    }

    public void Q() {
        if (!this.au.isEmpty()) {
            a poll = this.au.poll();
            if (poll.e == null) {
                com.baidu.mapsdkplatform.comjni.map.basemap.a.a(poll.a, poll.b, poll.c, poll.d);
            } else if (this.i != null) {
                A();
                this.i.a(poll.e, true);
            }
        }
    }

    public float a(int i2, int i3, int i4, int i5, int i6, int i7) {
        if (!this.k) {
            return 12.0f;
        }
        if (this.i == null) {
            return 0.0f;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(TtmlNode.LEFT, i2);
        bundle.putInt(TtmlNode.RIGHT, i4);
        bundle.putInt("bottom", i5);
        bundle.putInt("top", i3);
        bundle.putInt("hasHW", 1);
        bundle.putInt("width", i6);
        bundle.putInt("height", i7);
        return this.i.b(bundle);
    }

    /* access modifiers changed from: package-private */
    public int a(int i2, int i3, int i4) {
        if (!this.at) {
            return com.baidu.mapsdkplatform.comjni.map.basemap.a.a(this.j, i2, i3, i4);
        }
        this.au.add(new a(this.j, i2, i3, i4));
        return 0;
    }

    public int a(Bundle bundle, long j2, int i2, Bundle bundle2) {
        if (j2 == this.H.a) {
            bundle.putString("jsondata", this.H.a());
            bundle.putBundle("param", this.H.b());
            return this.H.g;
        } else if (j2 == this.G.a) {
            bundle.putString("jsondata", this.G.a());
            bundle.putBundle("param", this.G.b());
            return this.G.g;
        } else if (j2 == this.K.a) {
            bundle.putBundle("param", this.M.a(bundle2.getInt("x"), bundle2.getInt("y"), bundle2.getInt("zoom")));
            return this.K.g;
        } else if (j2 != this.B.a) {
            return 0;
        } else {
            bundle.putBundle("param", this.C.a(bundle2.getInt("x"), bundle2.getInt("y"), bundle2.getInt("zoom"), this.D));
            return this.B.g;
        }
    }

    public Point a(GeoPoint geoPoint) {
        return this.N.a(geoPoint);
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.E = new ArrayList();
        this.F = new HashMap<>();
        f fVar = new f();
        this.an = fVar;
        a((d) fVar);
        b bVar = new b();
        this.aq = bVar;
        a((d) bVar);
        p pVar = new p();
        this.K = pVar;
        a((d) pVar);
        a aVar = new a();
        this.L = aVar;
        a((d) aVar);
        a((d) new r());
        ag agVar = new ag();
        this.I = agVar;
        a((d) agVar);
        this.F.put(MapLayer.MAP_LAYER_POI_MARKER, this.I);
        c cVar = new c();
        this.ar = cVar;
        a((d) cVar);
        this.F.put(MapLayer.MAP_LAYER_INDOOR_POI, this.ar);
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar2 = this.i;
        if (aVar2 != null) {
            aVar2.e(false);
        }
        aj ajVar = new aj();
        this.J = ajVar;
        a((d) ajVar);
        this.F.put(MapLayer.MAP_LAYER_OVERLAY, this.J);
        g gVar = new g();
        this.H = gVar;
        a((d) gVar);
        z zVar = new z();
        this.G = zVar;
        a((d) zVar);
        this.F.put(MapLayer.MAP_LAYER_LOCATION, this.G);
    }

    public void a(float f2, float f3) {
        this.a = f2;
        this.c = f2;
        this.b = f3;
    }

    /* access modifiers changed from: package-private */
    public void a(int i2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = new com.baidu.mapsdkplatform.comjni.map.basemap.a();
        this.i = aVar;
        aVar.a(i2);
        long a2 = this.i.a();
        this.j = a2;
        a("com.baidu.platform.comapi.wnplatform.walkmap.WNaviBaiduMap", "setId", a2);
        this.l = SysOSUtil.getDensityDpi() < 180 ? 18 : SysOSUtil.getDensityDpi() < 240 ? 25 : SysOSUtil.getDensityDpi() < 320 ? 37 : 50;
        String moduleFileName = SysOSUtil.getModuleFileName();
        String appSDCardPath = EnvironmentUtilities.getAppSDCardPath();
        String appCachePath = EnvironmentUtilities.getAppCachePath();
        String appSecondCachePath = EnvironmentUtilities.getAppSecondCachePath();
        int mapTmpStgMax = EnvironmentUtilities.getMapTmpStgMax();
        int domTmpStgMax = EnvironmentUtilities.getDomTmpStgMax();
        int itsTmpStgMax = EnvironmentUtilities.getItsTmpStgMax();
        String str = SysOSUtil.getDensityDpi() >= 180 ? "/h/" : "/l/";
        String str2 = moduleFileName + "/cfg";
        String str3 = appSDCardPath + "/vmp";
        String str4 = str2 + "/a/";
        String str5 = str2 + "/a/";
        String str6 = str2 + "/idrres/";
        String str7 = str3 + str;
        String str8 = str3 + str;
        String str9 = appCachePath + "/tmp/";
        String str10 = appSecondCachePath + "/tmp/";
        Activity a3 = a(this.D);
        if (a3 != null) {
            Display defaultDisplay = a3.getWindowManager().getDefaultDisplay();
            this.i.a(str4, str7, str9, str10, str8, str5, this.ao, this.ap, str6, defaultDisplay.getWidth(), defaultDisplay.getHeight(), SysOSUtil.getDensityDpi(), mapTmpStgMax, domTmpStgMax, itsTmpStgMax, 0);
            return;
        }
        throw new RuntimeException("BDMapSDKException: Please give the right context.");
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3) {
        this.Q = i2;
        this.R = i3;
    }

    public void a(long j2, long j3, long j4, long j5, boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(j2, j3, j4, j5, z2);
        }
    }

    public void a(Bitmap bitmap) {
        Bundle bundle;
        if (this.i != null) {
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            try {
                jSONObject.put("type", 0);
                jSONObject2.put("x", O);
                jSONObject2.put("y", P);
                jSONObject2.put("hidetime", 1000);
                jSONArray.put(jSONObject2);
                jSONObject.put("data", jSONArray);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            if (bitmap == null) {
                bundle = null;
            } else {
                Bundle bundle2 = new Bundle();
                ArrayList arrayList = new ArrayList();
                ParcelItem parcelItem = new ParcelItem();
                Bundle bundle3 = new Bundle();
                ByteBuffer allocate = ByteBuffer.allocate(bitmap.getWidth() * bitmap.getHeight() * 4);
                bitmap.copyPixelsToBuffer(allocate);
                bundle3.putByteArray("imgdata", allocate.array());
                bundle3.putInt("imgindex", bitmap.hashCode());
                bundle3.putInt("imgH", bitmap.getHeight());
                bundle3.putInt("imgW", bitmap.getWidth());
                bundle3.putInt("hasIcon", 1);
                parcelItem.setBundle(bundle3);
                arrayList.add(parcelItem);
                if (arrayList.size() > 0) {
                    ParcelItem[] parcelItemArr = new ParcelItem[arrayList.size()];
                    for (int i2 = 0; i2 < arrayList.size(); i2++) {
                        parcelItemArr[i2] = (ParcelItem) arrayList.get(i2);
                    }
                    bundle2.putParcelableArray("icondata", parcelItemArr);
                }
                bundle = bundle2;
            }
            b(jSONObject.toString(), bundle);
            this.i.b(this.H.a);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(Handler handler) {
        MessageCenter.registMessage(4000, handler);
        MessageCenter.registMessage(39, handler);
        MessageCenter.registMessage(41, handler);
        MessageCenter.registMessage(49, handler);
        MessageCenter.registMessage(65289, handler);
        MessageCenter.registMessage(50, handler);
        MessageCenter.registMessage(999, handler);
        BaseMapCallback.addLayerDataInterface(this.j, this);
    }

    public void a(MapLayer mapLayer, MapLayer mapLayer2) {
        if (this.i != null) {
            d dVar = this.F.get(mapLayer);
            d dVar2 = this.F.get(mapLayer2);
            if (dVar != null && dVar2 != null) {
                this.i.a(dVar.a, dVar2.a);
            }
        }
    }

    public void a(MapLayer mapLayer, boolean z2) {
        d dVar;
        if (this.i != null && (dVar = this.F.get(mapLayer)) != null) {
            this.i.b(dVar.a, z2);
        }
    }

    public void a(MapStatusUpdate mapStatusUpdate) {
        this.q = mapStatusUpdate;
    }

    public void a(LatLngBounds latLngBounds) {
        if (latLngBounds != null && this.i != null) {
            LatLng latLng = latLngBounds.northeast;
            LatLng latLng2 = latLngBounds.southwest;
            GeoPoint ll2mc = CoordUtil.ll2mc(latLng);
            GeoPoint ll2mc2 = CoordUtil.ll2mc(latLng2);
            int longitudeE6 = (int) ll2mc2.getLongitudeE6();
            int latitudeE6 = (int) ll2mc.getLatitudeE6();
            Bundle bundle = new Bundle();
            bundle.putInt("maxCoorx", (int) ll2mc.getLongitudeE6());
            bundle.putInt("minCoory", (int) ll2mc2.getLatitudeE6());
            bundle.putInt("minCoorx", longitudeE6);
            bundle.putInt("maxCoory", latitudeE6);
            this.i.a(bundle);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(ab abVar) {
        new ad();
        if (abVar == null) {
            abVar = new ab();
        }
        ad adVar = abVar.a;
        this.y = abVar.f;
        this.z = abVar.d;
        this.e = abVar.e;
        this.f = abVar.g;
        this.i.a(adVar.a(this), false);
        this.i.c(aa.DEFAULT.ordinal());
        this.u = abVar.b;
        if (!abVar.b) {
            this.i.a(this.H.a, false);
        } else {
            O = (int) (SysOSUtil.getDensity() * 40.0f);
            P = (int) (SysOSUtil.getDensity() * 40.0f);
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject2 = new JSONObject();
            try {
                jSONObject2.put("x", O);
                jSONObject2.put("y", P);
                jSONObject2.put("hidetime", 1000);
                jSONArray.put(jSONObject2);
                jSONObject.put("data", jSONArray);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            this.H.a(jSONObject.toString());
            this.i.a(this.H.a, true);
        }
        int i2 = abVar.c;
        if (i2 == 2) {
            a(true);
        }
        if (i2 == 3) {
            this.i.a(this.an.a, false);
            this.i.a(this.ar.a, false);
            this.i.a(this.I.a, false);
            this.i.e(false);
        }
    }

    public void a(ad adVar) {
        if (this.i != null && adVar != null) {
            Bundle a2 = adVar.a(this);
            a2.putInt("animation", 0);
            a2.putInt("animatime", 0);
            this.i.a(a2, true);
        }
    }

    public void a(ad adVar, int i2) {
        if (this.i != null && adVar != null) {
            Bundle a2 = adVar.a(this);
            a2.putInt("animation", 1);
            a2.putInt("animatime", i2);
            if (this.at) {
                this.au.add(new a(a2));
                return;
            }
            A();
            this.i.a(a2, false);
        }
    }

    public void a(ak akVar) {
        this.C = akVar;
    }

    public void a(n nVar) {
        List<n> list;
        if (nVar != null && (list = this.h) != null) {
            list.add(nVar);
        }
    }

    public void a(q qVar) {
        this.M = qVar;
    }

    public void a(String str, int i2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(str, i2);
        }
    }

    public void a(String str, Bundle bundle) {
        if (this.i != null) {
            this.G.a(str);
            this.G.a(bundle);
            this.i.b(this.G.a);
        }
    }

    public void a(List<Bundle> list) {
        if (this.i != null && list != null) {
            int size = list.size();
            Bundle[] bundleArr = new Bundle[list.size()];
            for (int i2 = 0; i2 < size; i2++) {
                g(list.get(i2));
                bundleArr[i2] = list.get(i2);
            }
            this.i.a(bundleArr);
        }
    }

    public void a(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            if (!aVar.a(this.an.a)) {
                this.i.a(this.an.a, true);
            }
            this.t = z2;
            R();
            this.i.a(this.t);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:81:0x0230  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:88:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(float r23, float r24, float r25, float r26) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r25
            int r3 = r0.R
            float r4 = (float) r3
            float r4 = r4 - r24
            float r3 = (float) r3
            float r3 = r3 - r26
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            boolean r5 = r5.e
            r9 = 1
            if (r5 != r9) goto L_0x0229
            int r5 = r0.S
            r13 = 4640537203540230144(0x4066800000000000, double:180.0)
            r15 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            r17 = 4611686018427387904(0x4000000000000000, double:2.0)
            r19 = 0
            if (r5 != 0) goto L_0x00bc
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.c
            float r5 = r5 - r4
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 <= 0) goto L_0x0039
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.d
            float r5 = r5 - r3
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 > 0) goto L_0x004b
        L_0x0039:
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.c
            float r5 = r5 - r4
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 >= 0) goto L_0x00b3
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.d
            float r5 = r5 - r3
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 >= 0) goto L_0x00b3
        L_0x004b:
            float r5 = r3 - r4
            double r8 = (double) r5
            float r10 = r2 - r1
            double r6 = (double) r10
            double r6 = java.lang.Math.atan2(r8, r6)
            com.baidu.mapsdkplatform.comapi.map.l$a r8 = r0.T
            float r8 = r8.d
            com.baidu.mapsdkplatform.comapi.map.l$a r9 = r0.T
            float r9 = r9.c
            float r8 = r8 - r9
            double r8 = (double) r8
            com.baidu.mapsdkplatform.comapi.map.l$a r11 = r0.T
            float r11 = r11.b
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.a
            float r11 = r11 - r12
            double r11 = (double) r11
            double r8 = java.lang.Math.atan2(r8, r11)
            double r6 = r6 - r8
            float r10 = r10 * r10
            float r5 = r5 * r5
            float r10 = r10 + r5
            double r8 = (double) r10
            double r8 = java.lang.Math.sqrt(r8)
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            double r10 = r5.h
            double r8 = r8 / r10
            double r10 = java.lang.Math.log(r8)
            double r20 = java.lang.Math.log(r17)
            double r10 = r10 / r20
            double r10 = r10 * r15
            int r5 = (int) r10
            double r6 = r6 * r13
            r10 = 4614256673094690983(0x400921ff2e48e8a7, double:3.1416)
            double r6 = r6 / r10
            int r6 = (int) r6
            r10 = 0
            int r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r7 <= 0) goto L_0x00a1
            r7 = 3000(0xbb8, float:4.204E-42)
            if (r5 > r7) goto L_0x00a9
            r7 = -3000(0xfffffffffffff448, float:NaN)
            if (r5 < r7) goto L_0x00a9
        L_0x00a1:
            int r5 = java.lang.Math.abs(r6)
            r6 = 10
            if (r5 < r6) goto L_0x00ae
        L_0x00a9:
            r5 = 2
            r0.S = r5
            r6 = 1
            goto L_0x00b7
        L_0x00ae:
            r5 = 2
            r6 = 1
            r0.S = r6
            goto L_0x00b7
        L_0x00b3:
            r5 = 2
            r6 = 1
            r0.S = r5
        L_0x00b7:
            int r5 = r0.S
            if (r5 != 0) goto L_0x00bd
            return r6
        L_0x00bc:
            r6 = 1
        L_0x00bd:
            int r5 = r0.S
            r7 = 0
            if (r5 != r6) goto L_0x00fb
            boolean r5 = r0.y
            if (r5 == 0) goto L_0x00fb
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.c
            float r5 = r5 - r4
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 <= 0) goto L_0x00e3
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.d
            float r5 = r5 - r3
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 <= 0) goto L_0x00e3
            r22.S()
            r5 = 83
        L_0x00dd:
            r6 = 1
            r0.a((int) r6, (int) r5, (int) r7)
            goto L_0x0229
        L_0x00e3:
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.c
            float r5 = r5 - r4
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 >= 0) goto L_0x0229
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.d
            float r5 = r5 - r3
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 >= 0) goto L_0x0229
            r22.S()
            r5 = 87
            goto L_0x00dd
        L_0x00fb:
            int r5 = r0.S
            r6 = 4
            r8 = 3
            r9 = 2
            if (r5 == r9) goto L_0x0106
            if (r5 == r6) goto L_0x0106
            if (r5 != r8) goto L_0x0229
        L_0x0106:
            float r5 = r3 - r4
            double r9 = (double) r5
            float r11 = r2 - r1
            double r6 = (double) r11
            double r6 = java.lang.Math.atan2(r9, r6)
            com.baidu.mapsdkplatform.comapi.map.l$a r9 = r0.T
            float r9 = r9.d
            com.baidu.mapsdkplatform.comapi.map.l$a r10 = r0.T
            float r10 = r10.c
            float r9 = r9 - r10
            double r9 = (double) r9
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.b
            com.baidu.mapsdkplatform.comapi.map.l$a r8 = r0.T
            float r8 = r8.a
            float r12 = r12 - r8
            double r13 = (double) r12
            double r8 = java.lang.Math.atan2(r9, r13)
            double r6 = r6 - r8
            float r11 = r11 * r11
            float r5 = r5 * r5
            float r11 = r11 + r5
            double r8 = (double) r11
            double r8 = java.lang.Math.sqrt(r8)
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            double r10 = r5.h
            double r8 = r8 / r10
            double r10 = java.lang.Math.log(r8)
            double r12 = java.lang.Math.log(r17)
            double r10 = r10 / r12
            double r10 = r10 * r15
            int r5 = (int) r10
            com.baidu.mapsdkplatform.comapi.map.l$a r10 = r0.T
            float r10 = r10.g
            com.baidu.mapsdkplatform.comapi.map.l$a r11 = r0.T
            float r11 = r11.c
            float r10 = r10 - r11
            double r10 = (double) r10
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.f
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.a
            float r12 = r12 - r13
            double r12 = (double) r12
            double r10 = java.lang.Math.atan2(r10, r12)
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.f
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.a
            float r12 = r12 - r13
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.f
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.a
            float r13 = r13 - r14
            float r12 = r12 * r13
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.g
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.c
            float r13 = r13 - r14
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.g
            com.baidu.mapsdkplatform.comapi.map.l$a r15 = r0.T
            float r15 = r15.c
            float r14 = r14 - r15
            float r13 = r13 * r14
            float r12 = r12 + r13
            double r12 = (double) r12
            double r12 = java.lang.Math.sqrt(r12)
            double r10 = r10 + r6
            double r14 = java.lang.Math.cos(r10)
            double r14 = r14 * r12
            double r14 = r14 * r8
            r16 = r3
            double r2 = (double) r1
            double r14 = r14 + r2
            float r2 = (float) r14
            double r10 = java.lang.Math.sin(r10)
            double r12 = r12 * r10
            double r12 = r12 * r8
            double r10 = (double) r4
            double r12 = r12 + r10
            float r3 = (float) r12
            r10 = 4640537203540230144(0x4066800000000000, double:180.0)
            double r6 = r6 * r10
            r10 = 4614256673094690983(0x400921ff2e48e8a7, double:3.1416)
            double r6 = r6 / r10
            int r6 = (int) r6
            r7 = 8193(0x2001, float:1.1481E-41)
            r10 = 0
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01fa
            int r10 = r0.S
            r11 = 3
            if (r11 == r10) goto L_0x01cb
            int r10 = java.lang.Math.abs(r5)
            r12 = 2000(0x7d0, float:2.803E-42)
            if (r10 <= r12) goto L_0x01fa
            int r10 = r0.S
            r12 = 2
            if (r12 != r10) goto L_0x01fa
        L_0x01cb:
            r0.S = r11
            com.baidu.mapsdkplatform.comapi.map.ad r6 = r22.E()
            float r6 = r6.a
            boolean r10 = r0.f
            if (r10 == 0) goto L_0x0220
            r10 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01ea
            float r8 = r0.a
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01e5
            r8 = 0
            return r8
        L_0x01e5:
            r22.S()
            r9 = 3
            goto L_0x01f6
        L_0x01ea:
            r8 = 0
            r9 = 3
            float r10 = r0.b
            int r6 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r6 > 0) goto L_0x01f3
            return r8
        L_0x01f3:
            r22.S()
        L_0x01f6:
            r0.a((int) r7, (int) r9, (int) r5)
            goto L_0x0220
        L_0x01fa:
            if (r6 == 0) goto L_0x0220
            int r5 = r0.S
            r8 = 4
            if (r8 == r5) goto L_0x020e
            int r5 = java.lang.Math.abs(r6)
            r9 = 10
            if (r5 <= r9) goto L_0x0220
            int r5 = r0.S
            r9 = 2
            if (r9 != r5) goto L_0x0220
        L_0x020e:
            r0.S = r8
            boolean r5 = r0.z
            if (r5 == 0) goto L_0x0220
            int r5 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r8 = 1
            r5 = r5 | r8
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r5
            r22.S()
            r0.a((int) r7, (int) r8, (int) r6)
        L_0x0220:
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            r5.f = r2
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            r2.g = r3
            goto L_0x022b
        L_0x0229:
            r16 = r3
        L_0x022b:
            int r2 = r0.S
            r3 = 2
            if (r3 == r2) goto L_0x0244
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            r2.c = r4
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            r3 = r16
            r2.d = r3
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            r2.a = r1
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            r2 = r25
            r1.b = r2
        L_0x0244:
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            boolean r1 = r1.e
            if (r1 != 0) goto L_0x029c
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            int r2 = r0.Q
            r3 = 2
            int r2 = r2 / r3
            float r2 = (float) r2
            r1.f = r2
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            int r2 = r0.R
            int r2 = r2 / r3
            float r2 = (float) r2
            r1.g = r2
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            r2 = 1
            r1.e = r2
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            double r1 = r1.h
            r3 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 != 0) goto L_0x029c
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.b
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            float r2 = r2.a
            float r1 = r1 - r2
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            float r2 = r2.b
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r0.T
            float r3 = r3.a
            float r2 = r2 - r3
            float r1 = r1 * r2
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r0.T
            float r2 = r2.d
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r0.T
            float r3 = r3.c
            float r2 = r2 - r3
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r0.T
            float r3 = r3.d
            com.baidu.mapsdkplatform.comapi.map.l$a r4 = r0.T
            float r4 = r4.c
            float r3 = r3 - r4
            float r2 = r2 * r3
            float r1 = r1 + r2
            double r1 = (double) r1
            double r1 = java.lang.Math.sqrt(r1)
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r0.T
            r3.h = r1
        L_0x029c:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.e.a(float, float, float, float):boolean");
    }

    public boolean a(long j2) {
        for (d dVar : this.E) {
            if (dVar.a == j2) {
                return true;
            }
        }
        return false;
    }

    public boolean a(Point point) {
        if (point == null || this.i == null || point.x < 0 || point.y < 0) {
            return false;
        }
        O = point.x;
        P = point.y;
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("x", O);
            jSONObject2.put("y", P);
            jSONObject2.put("hidetime", 1000);
            jSONArray.put(jSONObject2);
            jSONObject.put("data", jSONArray);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        this.H.a(jSONObject.toString());
        this.i.b(this.H.a);
        return true;
    }

    public boolean a(Bundle bundle) {
        if (this.i == null) {
            return false;
        }
        al alVar = new al();
        this.B = alVar;
        long a2 = this.i.a(alVar.c, this.B.d, this.B.b);
        if (a2 != 0) {
            this.B.a = a2;
            this.E.add(this.B);
            bundle.putLong("sdktileaddr", a2);
            return e(bundle) && f(bundle);
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0375  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x038d  */
    /* JADX WARNING: Removed duplicated region for block: B:155:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0134  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(android.view.MotionEvent r24) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            int r2 = r24.getPointerCount()
            r3 = 0
            r4 = 2
            r5 = 1
            if (r2 != r4) goto L_0x002e
            float r6 = r1.getX(r3)
            int r6 = (int) r6
            float r7 = r1.getY(r3)
            int r7 = (int) r7
            boolean r6 = r0.c((int) r6, (int) r7)
            if (r6 == 0) goto L_0x002d
            float r6 = r1.getX(r5)
            int r6 = (int) r6
            float r7 = r1.getY(r5)
            int r7 = (int) r7
            boolean r6 = r0.c((int) r6, (int) r7)
            if (r6 != 0) goto L_0x002e
        L_0x002d:
            r2 = 1
        L_0x002e:
            if (r2 != r4) goto L_0x03e1
            int r2 = r0.R
            float r2 = (float) r2
            float r6 = r1.getY(r3)
            float r2 = r2 - r6
            int r6 = r0.R
            float r6 = (float) r6
            float r7 = r1.getY(r5)
            float r6 = r6 - r7
            float r7 = r1.getX(r3)
            float r8 = r1.getX(r5)
            int r9 = r24.getAction()
            r10 = 5
            if (r9 == r10) goto L_0x0073
            r10 = 6
            if (r9 == r10) goto L_0x0069
            r10 = 261(0x105, float:3.66E-43)
            if (r9 == r10) goto L_0x0062
            r10 = 262(0x106, float:3.67E-43)
            if (r9 == r10) goto L_0x005b
            goto L_0x007e
        L_0x005b:
            long r9 = r24.getEventTime()
            r0.X = r9
            goto L_0x006f
        L_0x0062:
            long r9 = r24.getEventTime()
            r0.V = r9
            goto L_0x0079
        L_0x0069:
            long r9 = r24.getEventTime()
            r0.Y = r9
        L_0x006f:
            int r9 = r0.Z
            int r9 = r9 + r5
            goto L_0x007c
        L_0x0073:
            long r9 = r24.getEventTime()
            r0.W = r9
        L_0x0079:
            int r9 = r0.Z
            int r9 = r9 - r5
        L_0x007c:
            r0.Z = r9
        L_0x007e:
            android.view.VelocityTracker r9 = r0.U
            if (r9 != 0) goto L_0x0088
            android.view.VelocityTracker r9 = android.view.VelocityTracker.obtain()
            r0.U = r9
        L_0x0088:
            android.view.VelocityTracker r9 = r0.U
            r9.addMovement(r1)
            android.content.Context r1 = com.baidu.mapapi.JNIInitializer.getCachedContext()
            android.view.ViewConfiguration r1 = android.view.ViewConfiguration.get(r1)
            if (r1 != 0) goto L_0x00a0
            int r1 = android.view.ViewConfiguration.getMinimumFlingVelocity()
            int r9 = android.view.ViewConfiguration.getMaximumFlingVelocity()
            goto L_0x00ad
        L_0x00a0:
            int r9 = r1.getScaledMinimumFlingVelocity()
            int r1 = r1.getScaledMaximumFlingVelocity()
            r22 = r9
            r9 = r1
            r1 = r22
        L_0x00ad:
            android.view.VelocityTracker r10 = r0.U
            r11 = 1000(0x3e8, float:1.401E-42)
            float r9 = (float) r9
            r10.computeCurrentVelocity(r11, r9)
            android.view.VelocityTracker r9 = r0.U
            float r9 = r9.getXVelocity(r5)
            android.view.VelocityTracker r10 = r0.U
            float r10 = r10.getYVelocity(r5)
            android.view.VelocityTracker r11 = r0.U
            float r11 = r11.getXVelocity(r4)
            android.view.VelocityTracker r12 = r0.U
            float r12 = r12.getYVelocity(r4)
            float r9 = java.lang.Math.abs(r9)
            float r1 = (float) r1
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 > 0) goto L_0x0134
            float r9 = java.lang.Math.abs(r10)
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 > 0) goto L_0x0134
            float r9 = java.lang.Math.abs(r11)
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 > 0) goto L_0x0134
            float r9 = java.lang.Math.abs(r12)
            int r1 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x00ef
            goto L_0x0134
        L_0x00ef:
            int r1 = r0.S
            if (r1 != 0) goto L_0x036d
            int r1 = r0.Z
            if (r1 != 0) goto L_0x036d
            long r9 = r0.X
            long r11 = r0.Y
            int r1 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x0100
            goto L_0x0101
        L_0x0100:
            r9 = r11
        L_0x0101:
            r0.X = r9
            long r9 = r0.V
            long r11 = r0.W
            int r1 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r1 >= 0) goto L_0x010c
            r9 = r11
        L_0x010c:
            r0.V = r9
            long r11 = r0.X
            long r11 = r11 - r9
            r9 = 200(0xc8, double:9.9E-322)
            int r1 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r1 >= 0) goto L_0x036d
            boolean r1 = r0.f
            if (r1 == 0) goto L_0x036d
            com.baidu.mapsdkplatform.comapi.map.ad r1 = r23.E()
            if (r1 == 0) goto L_0x036d
            float r3 = r1.a
            r9 = 1065353216(0x3f800000, float:1.0)
            float r3 = r3 - r9
            r1.a = r3
            int r3 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r3 = r3 | r5
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r3
            r3 = 300(0x12c, float:4.2E-43)
            r0.a((com.baidu.mapsdkplatform.comapi.map.ad) r1, (int) r3)
            goto L_0x036d
        L_0x0134:
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            boolean r1 = r1.e
            if (r1 == 0) goto L_0x036d
            int r1 = r0.S
            r15 = 4640537203540230144(0x4066800000000000, double:180.0)
            r17 = 4666723172467343360(0x40c3880000000000, double:10000.0)
            r19 = 4611686018427387904(0x4000000000000000, double:2.0)
            r12 = 0
            if (r1 != 0) goto L_0x01e3
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.c
            float r1 = r1 - r2
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 <= 0) goto L_0x015d
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.d
            float r1 = r1 - r6
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 > 0) goto L_0x016f
        L_0x015d:
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.c
            float r1 = r1 - r2
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x01d8
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.d
            float r1 = r1 - r6
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 >= 0) goto L_0x01d8
        L_0x016f:
            float r1 = r6 - r2
            r21 = r6
            double r5 = (double) r1
            float r3 = r8 - r7
            double r12 = (double) r3
            double r5 = java.lang.Math.atan2(r5, r12)
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.d
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.c
            float r12 = r12 - r13
            double r12 = (double) r12
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.b
            com.baidu.mapsdkplatform.comapi.map.l$a r4 = r0.T
            float r4 = r4.a
            float r14 = r14 - r4
            double r9 = (double) r14
            double r9 = java.lang.Math.atan2(r12, r9)
            double r5 = r5 - r9
            float r3 = r3 * r3
            float r1 = r1 * r1
            float r3 = r3 + r1
            double r9 = (double) r3
            double r9 = java.lang.Math.sqrt(r9)
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            double r11 = r1.h
            double r9 = r9 / r11
            double r11 = java.lang.Math.log(r9)
            double r13 = java.lang.Math.log(r19)
            double r11 = r11 / r13
            double r11 = r11 * r17
            int r1 = (int) r11
            double r5 = r5 * r15
            r11 = 4614256673094690983(0x400921ff2e48e8a7, double:3.1416)
            double r5 = r5 / r11
            int r3 = (int) r5
            r5 = 0
            int r11 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r11 <= 0) goto L_0x01c6
            r5 = 3000(0xbb8, float:4.204E-42)
            if (r1 > r5) goto L_0x01ce
            r5 = -3000(0xfffffffffffff448, float:NaN)
            if (r1 < r5) goto L_0x01ce
        L_0x01c6:
            int r1 = java.lang.Math.abs(r3)
            r3 = 10
            if (r1 < r3) goto L_0x01d3
        L_0x01ce:
            r1 = 2
            r0.S = r1
            r3 = 1
            goto L_0x01de
        L_0x01d3:
            r1 = 2
            r3 = 1
            r0.S = r3
            goto L_0x01de
        L_0x01d8:
            r21 = r6
            r1 = 2
            r3 = 1
            r0.S = r1
        L_0x01de:
            int r1 = r0.S
            if (r1 != 0) goto L_0x01e6
            return r3
        L_0x01e3:
            r21 = r6
            r3 = 1
        L_0x01e6:
            int r1 = r0.S
            if (r1 != r3) goto L_0x0232
            boolean r1 = r0.y
            if (r1 == 0) goto L_0x0232
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.c
            float r1 = r1 - r2
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x0212
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.d
            float r1 = r1 - r21
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x0212
            int r1 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r3 = 1
            r1 = r1 | r3
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r1
            r23.S()
            r1 = 83
        L_0x020d:
            r4 = 0
            r0.a((int) r3, (int) r1, (int) r4)
            goto L_0x023e
        L_0x0212:
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.c
            float r1 = r1 - r2
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x023e
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            float r1 = r1.d
            float r1 = r1 - r21
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r1 >= 0) goto L_0x023e
            int r1 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r3 = 1
            r1 = r1 | r3
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r1
            r23.S()
            r1 = 87
            goto L_0x020d
        L_0x0232:
            int r1 = r0.S
            r3 = 4
            r5 = 3
            r6 = 2
            if (r1 == r6) goto L_0x0241
            if (r1 == r3) goto L_0x0241
            if (r1 != r5) goto L_0x023e
            goto L_0x0241
        L_0x023e:
            r5 = r0
            goto L_0x0370
        L_0x0241:
            float r6 = r21 - r2
            double r9 = (double) r6
            float r1 = r8 - r7
            double r11 = (double) r1
            double r9 = java.lang.Math.atan2(r9, r11)
            com.baidu.mapsdkplatform.comapi.map.l$a r11 = r0.T
            float r11 = r11.d
            com.baidu.mapsdkplatform.comapi.map.l$a r12 = r0.T
            float r12 = r12.c
            float r11 = r11 - r12
            double r11 = (double) r11
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.b
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.a
            float r13 = r13 - r14
            double r13 = (double) r13
            double r11 = java.lang.Math.atan2(r11, r13)
            double r9 = r9 - r11
            float r1 = r1 * r1
            float r6 = r6 * r6
            float r1 = r1 + r6
            double r11 = (double) r1
            double r11 = java.lang.Math.sqrt(r11)
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r0.T
            double r13 = r1.h
            double r11 = r11 / r13
            double r13 = java.lang.Math.log(r11)
            double r19 = java.lang.Math.log(r19)
            double r13 = r13 / r19
            double r13 = r13 * r17
            int r1 = (int) r13
            com.baidu.mapsdkplatform.comapi.map.l$a r6 = r0.T
            float r6 = r6.g
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.c
            float r6 = r6 - r13
            double r13 = (double) r6
            com.baidu.mapsdkplatform.comapi.map.l$a r6 = r0.T
            float r6 = r6.f
            com.baidu.mapsdkplatform.comapi.map.l$a r4 = r0.T
            float r4 = r4.a
            float r6 = r6 - r4
            double r3 = (double) r6
            double r3 = java.lang.Math.atan2(r13, r3)
            com.baidu.mapsdkplatform.comapi.map.l$a r6 = r0.T
            float r6 = r6.f
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.a
            float r6 = r6 - r13
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.f
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.a
            float r13 = r13 - r14
            float r6 = r6 * r13
            com.baidu.mapsdkplatform.comapi.map.l$a r13 = r0.T
            float r13 = r13.g
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.c
            float r13 = r13 - r14
            com.baidu.mapsdkplatform.comapi.map.l$a r14 = r0.T
            float r14 = r14.g
            com.baidu.mapsdkplatform.comapi.map.l$a r5 = r0.T
            float r5 = r5.c
            float r14 = r14 - r5
            float r13 = r13 * r14
            float r6 = r6 + r13
            double r5 = (double) r6
            double r5 = java.lang.Math.sqrt(r5)
            double r3 = r3 + r9
            double r13 = java.lang.Math.cos(r3)
            double r13 = r13 * r5
            double r13 = r13 * r11
            r19 = r1
            double r0 = (double) r7
            double r13 = r13 + r0
            float r0 = (float) r13
            double r3 = java.lang.Math.sin(r3)
            double r5 = r5 * r3
            double r5 = r5 * r11
            double r3 = (double) r2
            double r5 = r5 + r3
            float r1 = (float) r5
            double r9 = r9 * r15
            r3 = 4614256673094690983(0x400921ff2e48e8a7, double:3.1416)
            double r9 = r9 / r3
            int r3 = (int) r9
            r4 = 8193(0x2001, float:1.1481E-41)
            r5 = 0
            int r9 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            r5 = r23
            if (r9 <= 0) goto L_0x0343
            int r6 = r5.S
            r9 = 3
            if (r9 == r6) goto L_0x0304
            int r6 = java.lang.Math.abs(r19)
            r10 = 2000(0x7d0, float:2.803E-42)
            if (r6 <= r10) goto L_0x0343
            int r6 = r5.S
            r10 = 2
            if (r10 != r6) goto L_0x0343
        L_0x0304:
            r5.S = r9
            com.baidu.mapsdkplatform.comapi.map.ad r3 = r23.E()
            float r3 = r3.a
            boolean r6 = r5.f
            if (r6 == 0) goto L_0x0364
            r9 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r6 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x032b
            float r6 = r5.a
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 < 0) goto L_0x031e
            r6 = 0
            return r6
        L_0x031e:
            int r3 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r9 = 1
            r3 = r3 | r9
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r3
            r23.S()
            r10 = r19
            r11 = 3
            goto L_0x033f
        L_0x032b:
            r10 = r19
            r6 = 0
            r9 = 1
            r11 = 3
            float r12 = r5.b
            int r3 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r3 > 0) goto L_0x0337
            return r6
        L_0x0337:
            int r3 = com.baidu.mapapi.map.BaiduMap.mapStatusReason
            r3 = r3 | r9
            com.baidu.mapapi.map.BaiduMap.mapStatusReason = r3
            r23.S()
        L_0x033f:
            r5.a((int) r4, (int) r11, (int) r10)
            goto L_0x0364
        L_0x0343:
            if (r3 == 0) goto L_0x0364
            int r6 = r5.S
            r9 = 4
            if (r9 == r6) goto L_0x0357
            int r6 = java.lang.Math.abs(r3)
            r10 = 10
            if (r6 <= r10) goto L_0x0364
            int r6 = r5.S
            r10 = 2
            if (r10 != r6) goto L_0x0364
        L_0x0357:
            r5.S = r9
            boolean r6 = r5.z
            if (r6 == 0) goto L_0x0364
            r23.S()
            r6 = 1
            r5.a((int) r4, (int) r6, (int) r3)
        L_0x0364:
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r5.T
            r3.f = r0
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r0.g = r1
            goto L_0x0370
        L_0x036d:
            r5 = r0
            r21 = r6
        L_0x0370:
            int r0 = r5.S
            r1 = 2
            if (r1 == r0) goto L_0x0387
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r0.c = r2
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r6 = r21
            r0.d = r6
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r0.a = r7
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r0.b = r8
        L_0x0387:
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            boolean r0 = r0.e
            if (r0 != 0) goto L_0x03df
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            int r1 = r5.Q
            r2 = 2
            int r1 = r1 / r2
            float r1 = (float) r1
            r0.f = r1
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            int r1 = r5.R
            int r1 = r1 / r2
            float r1 = (float) r1
            r0.g = r1
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            r1 = 1
            r0.e = r1
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            double r0 = r0.h
            r2 = 0
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 != 0) goto L_0x03df
            com.baidu.mapsdkplatform.comapi.map.l$a r0 = r5.T
            float r0 = r0.b
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r5.T
            float r1 = r1.a
            float r0 = r0 - r1
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r5.T
            float r1 = r1.b
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r5.T
            float r2 = r2.a
            float r1 = r1 - r2
            float r0 = r0 * r1
            com.baidu.mapsdkplatform.comapi.map.l$a r1 = r5.T
            float r1 = r1.d
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r5.T
            float r2 = r2.c
            float r1 = r1 - r2
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r5.T
            float r2 = r2.d
            com.baidu.mapsdkplatform.comapi.map.l$a r3 = r5.T
            float r3 = r3.c
            float r2 = r2 - r3
            float r1 = r1 * r2
            float r0 = r0 + r1
            double r0 = (double) r0
            double r0 = java.lang.Math.sqrt(r0)
            com.baidu.mapsdkplatform.comapi.map.l$a r2 = r5.T
            r2.h = r0
        L_0x03df:
            r0 = 1
            return r0
        L_0x03e1:
            r5 = r0
            r0 = 1
            int r2 = r24.getAction()
            if (r2 == 0) goto L_0x03f9
            if (r2 == r0) goto L_0x03f4
            r3 = 2
            if (r2 == r3) goto L_0x03f0
            r2 = 0
            return r2
        L_0x03f0:
            r23.c((android.view.MotionEvent) r24)
            goto L_0x03fc
        L_0x03f4:
            boolean r0 = r23.d((android.view.MotionEvent) r24)
            return r0
        L_0x03f9:
            r23.b((android.view.MotionEvent) r24)
        L_0x03fc:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.e.a(android.view.MotionEvent):boolean");
    }

    public boolean a(String str, String str2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.a(str, str2);
    }

    public GeoPoint b(int i2, int i3) {
        return this.N.a(i2, i3);
    }

    /* access modifiers changed from: package-private */
    public void b(float f2, float f3) {
        if (!this.T.e) {
            long currentTimeMillis = System.currentTimeMillis();
            this.ae = currentTimeMillis;
            if (currentTimeMillis - this.ad < 400) {
                if (Math.abs(f2 - this.aa) >= 120.0f || Math.abs(f3 - this.ab) >= 120.0f) {
                    currentTimeMillis = this.ae;
                } else {
                    this.ad = 0;
                    this.af = true;
                    this.aa = f2;
                    this.ab = f3;
                    a(4, 0, ((int) f2) | (((int) f3) << 16));
                    this.ac = true;
                }
            }
            this.ad = currentTimeMillis;
            this.aa = f2;
            this.ab = f3;
            a(4, 0, ((int) f2) | (((int) f3) << 16));
            this.ac = true;
        }
    }

    public void b(int i2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.f(i2);
        }
    }

    public void b(Bundle bundle) {
        if (this.i != null) {
            g(bundle);
            this.i.e(bundle);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(Handler handler) {
        MessageCenter.unregistMessage(4000, handler);
        MessageCenter.unregistMessage(41, handler);
        MessageCenter.unregistMessage(49, handler);
        MessageCenter.unregistMessage(39, handler);
        MessageCenter.unregistMessage(65289, handler);
        MessageCenter.unregistMessage(50, handler);
        MessageCenter.unregistMessage(999, handler);
        BaseMapCallback.removeLayerDataInterface(this.j);
    }

    public void b(boolean z2) {
        this.A = z2;
    }

    public boolean b() {
        return this.A;
    }

    public void c() {
        if (this.i != null) {
            for (d dVar : this.E) {
                this.i.a(dVar.a, false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void c(int i2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.b(i2);
            this.i = null;
        }
    }

    public void c(Bundle bundle) {
        if (this.i != null) {
            g(bundle);
            this.i.f(bundle);
        }
    }

    public void c(boolean z2) {
        boolean z3;
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            if (z2) {
                if (!this.av) {
                    aVar.a(this.aq.a, this.J.a);
                    z3 = true;
                } else {
                    return;
                }
            } else if (this.av) {
                aVar.a(this.J.a, this.aq.a);
                z3 = false;
            } else {
                return;
            }
            this.av = z3;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean c(float f2, float f3) {
        if (this.T.e || System.currentTimeMillis() - m < 300) {
            return true;
        }
        if (this.p) {
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    GeoPoint b2 = b((int) f2, (int) f3);
                    if (next != null) {
                        next.d(b2);
                    }
                }
            }
            return true;
        }
        float abs = Math.abs(f2 - this.aa);
        float abs2 = Math.abs(f3 - this.ab);
        int i2 = (((double) SysOSUtil.getDensity()) > 1.5d ? 1 : (((double) SysOSUtil.getDensity()) == 1.5d ? 0 : -1));
        double density = (double) SysOSUtil.getDensity();
        if (i2 > 0) {
            density *= 1.5d;
        }
        float f4 = (float) density;
        if (this.ac && abs / f4 <= 3.0f && abs2 / f4 <= 3.0f) {
            return true;
        }
        this.ac = false;
        int i3 = (int) f2;
        int i4 = (int) f3;
        if (i3 < 0) {
            i3 = 0;
        }
        if (i4 < 0) {
            i4 = 0;
        }
        if (this.e) {
            this.ah = this.aj;
            this.ai = this.ak;
            this.aj = f2;
            this.ak = f3;
            this.al = this.am;
            this.am = System.currentTimeMillis();
            this.ag = true;
            S();
            a(3, 0, (i4 << 16) | i3);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean c(int i2, int i3) {
        return i2 >= 0 && i2 <= this.Q + 0 && i3 >= 0 && i3 <= this.R + 0;
    }

    public void d() {
        if (this.i != null) {
            for (d next : this.E) {
                if ((next instanceof z) || (next instanceof a) || (next instanceof p)) {
                    this.i.a(next.a, false);
                } else {
                    this.i.a(next.a, true);
                }
            }
            this.i.c(false);
        }
    }

    public void d(Bundle bundle) {
        if (this.i != null) {
            g(bundle);
            this.i.g(bundle);
        }
    }

    public void d(boolean z2) {
        boolean z3;
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            if (z2) {
                if (!this.aw) {
                    aVar.a(this.J.a, this.G.a);
                    z3 = true;
                } else {
                    return;
                }
            } else if (this.aw) {
                aVar.a(this.G.a, this.J.a);
                z3 = false;
            } else {
                return;
            }
            this.aw = z3;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean d(float f2, float f3) {
        if (this.p) {
            List<n> list = this.h;
            if (list != null) {
                for (n next : list) {
                    GeoPoint b2 = b((int) f2, (int) f3);
                    if (next != null) {
                        next.e(b2);
                    }
                }
            }
            this.p = false;
            return true;
        }
        if (!this.T.e) {
            if (this.af) {
                return e(f2, f3);
            }
            if (this.ag) {
                return T();
            }
            if (System.currentTimeMillis() - this.ae < 400 && Math.abs(f2 - this.aa) < 10.0f && Math.abs(f3 - this.ab) < 10.0f) {
                M();
                return true;
            }
        }
        M();
        int i2 = (int) f2;
        int i3 = (int) f3;
        if (i2 < 0) {
            i2 = 0;
        }
        if (i3 < 0) {
            i3 = 0;
        }
        a(5, 0, i2 | (i3 << 16));
        return true;
    }

    public void e(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(this.H.a, z2);
        }
    }

    public boolean e() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar;
        al alVar = this.B;
        if (alVar == null || (aVar = this.i) == null) {
            return false;
        }
        return aVar.c(alVar.a);
    }

    /* access modifiers changed from: package-private */
    public void f() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.N = new ah(aVar);
        }
    }

    public void f(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(this.B.a, z2);
        }
    }

    public void g(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(this.an.a, z2);
        }
    }

    public boolean g() {
        return this.s;
    }

    public String h() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        return aVar.e(this.H.a);
    }

    public void h(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.x = z2;
            aVar.b(z2);
        }
    }

    public void i(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.s = z2;
            aVar.c(z2);
        }
    }

    public boolean i() {
        return this.x;
    }

    public void j(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.d(z2);
        }
    }

    public boolean j() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.k();
    }

    public void k(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.u = z2;
            aVar.a(this.H.a, z2);
        }
    }

    public boolean k() {
        return this.t;
    }

    public void l(boolean z2) {
        if (this.i != null) {
            float f2 = z2 ? 22.0f : 21.0f;
            this.a = f2;
            this.c = f2;
            this.i.e(z2);
            this.i.d(this.aq.a);
            this.i.d(this.ar.a);
        }
    }

    public boolean l() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.a(this.an.a);
    }

    public boolean m() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.o();
    }

    public void n() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.d(this.J.a);
        }
    }

    public void n(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.f(z2);
        }
    }

    public void o() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.p();
            this.i.b(this.K.a);
        }
    }

    public void o(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.v = z2;
            aVar.a(this.G.a, z2);
        }
    }

    public MapBaseIndoorMapInfo p() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return null;
        }
        return aVar.q();
    }

    public void p(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            this.w = z2;
            aVar.a(this.K.a, z2);
        }
    }

    public void q(boolean z2) {
        this.e = z2;
    }

    public boolean q() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar == null) {
            return false;
        }
        return aVar.r();
    }

    public void r(boolean z2) {
        this.f = z2;
    }

    public boolean r() {
        return this.u;
    }

    public void s(boolean z2) {
        this.g = z2;
    }

    public boolean s() {
        return this.v;
    }

    public void t() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.b(this.K.a);
        }
    }

    public void t(boolean z2) {
        this.z = z2;
    }

    /* access modifiers changed from: package-private */
    public void u() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.e();
        }
    }

    public void u(boolean z2) {
        this.y = z2;
    }

    /* access modifiers changed from: package-private */
    public void v() {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.f();
        }
    }

    public void v(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(this.I.a, z2);
        }
    }

    public void w(boolean z2) {
        com.baidu.mapsdkplatform.comjni.map.basemap.a aVar = this.i;
        if (aVar != null) {
            aVar.a(this.ar.a, z2);
        }
    }

    public boolean w() {
        return this.e;
    }

    public void x(boolean z2) {
        this.at = z2;
    }

    public boolean x() {
        return this.f;
    }

    public boolean y() {
        return this.z;
    }

    public boolean z() {
        return this.y;
    }
}
