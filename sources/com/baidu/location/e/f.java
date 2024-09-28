package com.baidu.location.e;

import android.content.Context;
import android.location.GnssMeasurementsEvent;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.Jni;
import com.baidu.location.b.t;
import com.baidu.location.b.w;
import com.baidu.location.b.x;
import com.baidu.location.g.k;
import com.baidu.location.indoor.g;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.king.zxing.util.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class f {
    private static String B = null;
    private static double D = 100.0d;
    public static int a = 0;
    public static String b = "";
    public static float c = -1.0f;
    private static f d = null;
    /* access modifiers changed from: private */
    public static int p = 0;
    /* access modifiers changed from: private */
    public static int q = 0;
    /* access modifiers changed from: private */
    public static long r = 0;
    private double A = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
    /* access modifiers changed from: private */
    public Handler C = null;
    private long E = 0;
    /* access modifiers changed from: private */
    public long F = 0;
    private a G = null;
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> H = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> I = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> J = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> K = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> L = new ArrayList<>();
    private Context e;
    /* access modifiers changed from: private */
    public LocationManager f = null;
    private Location g;
    private d h = null;
    private e i = null;
    /* access modifiers changed from: private */
    public GpsStatus j;
    private b k;
    private boolean l = false;
    private c m = null;
    private boolean n = false;
    private OnNmeaMessageListener o = null;
    /* access modifiers changed from: private */
    public long s = 0;
    /* access modifiers changed from: private */
    public boolean t = false;
    /* access modifiers changed from: private */
    public boolean u = false;
    private String v = null;
    private boolean w = false;
    private long x = 0;
    private double y = -1.0d;
    private double z = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;

    private class a extends GnssMeasurementsEvent.Callback {
        public int a;
        public String b;

        public void onGnssMeasurementsReceived(GnssMeasurementsEvent gnssMeasurementsEvent) {
            if (this.a == 1 && gnssMeasurementsEvent != null) {
                this.b = gnssMeasurementsEvent.toString();
            }
        }

        public void onStatusChanged(int i) {
            this.a = i;
        }
    }

    private class b extends GnssStatus.Callback {
        private b() {
        }

        /* synthetic */ b(f fVar, g gVar) {
            this();
        }

        public void onFirstFix(int i) {
        }

        public void onSatelliteStatusChanged(GnssStatus gnssStatus) {
            ArrayList h;
            if (f.this.f != null) {
                long unused = f.this.F = System.currentTimeMillis();
                int satelliteCount = gnssStatus.getSatelliteCount();
                f.this.I.clear();
                f.this.J.clear();
                f.this.K.clear();
                f.this.L.clear();
                int i = 0;
                int i2 = 0;
                int i3 = 0;
                for (int i4 = 0; i4 < satelliteCount; i4++) {
                    i3++;
                    ArrayList arrayList = new ArrayList();
                    int constellationType = gnssStatus.getConstellationType(i4);
                    arrayList.add(Float.valueOf(gnssStatus.getAzimuthDegrees(i4)));
                    arrayList.add(Float.valueOf(gnssStatus.getElevationDegrees(i4)));
                    arrayList.add(Float.valueOf(gnssStatus.getCn0DbHz(i4)));
                    if (gnssStatus.usedInFix(i4)) {
                        i++;
                        arrayList.add(Float.valueOf(1.0f));
                        if (constellationType == 1) {
                            i2++;
                        }
                    } else {
                        arrayList.add(Float.valueOf(0.0f));
                    }
                    arrayList.add(Float.valueOf((float) gnssStatus.getSvid(i4)));
                    if (constellationType == 1) {
                        arrayList.add(Float.valueOf(1.0f));
                        h = f.this.I;
                    } else if (constellationType == 5) {
                        arrayList.add(Float.valueOf(2.0f));
                        h = f.this.J;
                    } else if (constellationType == 3) {
                        arrayList.add(Float.valueOf(3.0f));
                        h = f.this.K;
                    } else if (constellationType == 6) {
                        arrayList.add(Float.valueOf(4.0f));
                        h = f.this.L;
                    }
                    h.add(arrayList);
                }
                f fVar = f.this;
                ArrayList unused2 = fVar.H = fVar.a(true, false, false, false, true, -1.0f);
                f fVar2 = f.this;
                f.b = fVar2.a((ArrayList<ArrayList<Float>>) fVar2.H);
                f.a = i;
                int unused3 = f.p = i2;
                int unused4 = f.q = i3;
                long unused5 = f.r = System.currentTimeMillis();
            }
        }

        public void onStarted() {
        }

        public void onStopped() {
            f.this.d((Location) null);
            f.this.b(false);
            f.a = 0;
            int unused = f.p = 0;
        }
    }

    private class c implements GpsStatus.Listener {
        private long b;

        private c() {
            this.b = 0;
        }

        /* synthetic */ c(f fVar, g gVar) {
            this();
        }

        public void onGpsStatusChanged(int i) {
            ArrayList h;
            if (f.this.f != null) {
                int i2 = 0;
                if (i == 2) {
                    f.this.d((Location) null);
                    f.this.b(false);
                    f.a = 0;
                    int unused = f.p = 0;
                } else if (i == 4 && f.this.u) {
                    try {
                        if (f.this.j == null) {
                            GpsStatus unused2 = f.this.j = f.this.f.getGpsStatus((GpsStatus) null);
                        } else {
                            f.this.f.getGpsStatus(f.this.j);
                        }
                        long unused3 = f.this.F = System.currentTimeMillis();
                        f.this.I.clear();
                        f.this.J.clear();
                        f.this.K.clear();
                        f.this.L.clear();
                        int i3 = 0;
                        for (GpsSatellite next : f.this.j.getSatellites()) {
                            ArrayList arrayList = new ArrayList();
                            int prn = next.getPrn();
                            arrayList.add(Float.valueOf(next.getAzimuth()));
                            arrayList.add(Float.valueOf(next.getElevation()));
                            arrayList.add(Float.valueOf(next.getSnr()));
                            if (next.usedInFix()) {
                                i3++;
                                arrayList.add(Float.valueOf(1.0f));
                                if (prn >= 1 && prn <= 32) {
                                    i2++;
                                }
                            } else {
                                arrayList.add(Float.valueOf(0.0f));
                            }
                            arrayList.add(Float.valueOf((float) prn));
                            if (prn >= 1 && prn <= 32) {
                                arrayList.add(Float.valueOf(1.0f));
                                h = f.this.I;
                            } else if (prn >= 201 && prn <= 235) {
                                arrayList.add(Float.valueOf(2.0f));
                                h = f.this.J;
                            } else if (prn >= 65 && prn <= 96) {
                                arrayList.add(Float.valueOf(3.0f));
                                h = f.this.K;
                            } else if (prn >= 301 && prn <= 336) {
                                arrayList.add(Float.valueOf(4.0f));
                                h = f.this.L;
                            }
                            h.add(arrayList);
                        }
                        ArrayList unused4 = f.this.H = f.this.a(true, false, false, false, true, -1.0f);
                        f.b = f.this.a((ArrayList<ArrayList<Float>>) f.this.H);
                        if (i2 > 0) {
                            int unused5 = f.p = i2;
                        }
                        if (i3 > 0) {
                            this.b = System.currentTimeMillis();
                        } else {
                            if (System.currentTimeMillis() - this.b > 100) {
                                this.b = System.currentTimeMillis();
                            }
                            long unused6 = f.r = System.currentTimeMillis();
                        }
                        f.a = i3;
                        long unused7 = f.r = System.currentTimeMillis();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private class d implements LocationListener {
        private d() {
        }

        /* synthetic */ d(f fVar, g gVar) {
            this();
        }

        public void onLocationChanged(Location location) {
            if (location != null && Math.abs(location.getLatitude()) <= 360.0d && Math.abs(location.getLongitude()) <= 360.0d) {
                int i = f.a;
                if (i == 0) {
                    try {
                        i = location.getExtras().getInt("satellites");
                    } catch (Exception e) {
                    }
                }
                if (i == 0) {
                    System.currentTimeMillis();
                    long unused = f.this.F;
                    if (location.getAccuracy() > 50.0f && !k.m) {
                        return;
                    }
                }
                f.this.b(true);
                f.this.d(location);
                boolean unused2 = f.this.t = false;
            }
        }

        public void onProviderDisabled(String str) {
            f.this.d((Location) null);
            f.this.b(false);
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
            if (i == 0) {
                f.this.d((Location) null);
            } else if (i == 1) {
                long unused = f.this.s = System.currentTimeMillis();
                boolean unused2 = f.this.t = true;
            } else if (i == 2) {
                boolean unused3 = f.this.t = false;
                return;
            } else {
                return;
            }
            f.this.b(false);
        }
    }

    private class e implements LocationListener {
        private long b;

        private e() {
            this.b = 0;
        }

        /* synthetic */ e(f fVar, g gVar) {
            this();
        }

        public void onLocationChanged(Location location) {
            if (!f.this.u && location != null && location.getProvider() == "gps" && System.currentTimeMillis() - this.b >= OkHttpUtils.DEFAULT_MILLISECONDS && Math.abs(location.getLatitude()) <= 360.0d && Math.abs(location.getLongitude()) <= 360.0d && w.a(location, false)) {
                this.b = System.currentTimeMillis();
                f.this.C.sendMessage(f.this.C.obtainMessage(4, location));
            }
        }

        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }
    }

    private f() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Class.forName("android.location.GnssStatus");
                this.l = true;
            } catch (ClassNotFoundException e2) {
                this.l = false;
            }
        }
        this.n = false;
    }

    public static int a(String str, String str2) {
        char charAt = str2.charAt(0);
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            if (Character.valueOf(charAt).equals(Character.valueOf(str.charAt(i3)))) {
                i2++;
            }
        }
        return i2;
    }

    public static synchronized f a() {
        f fVar;
        synchronized (f.class) {
            if (d == null) {
                d = new f();
            }
            fVar = d;
        }
        return fVar;
    }

    public static String a(Location location) {
        if (location == null) {
            return null;
        }
        float speed = (float) (((double) location.getSpeed()) * 3.6d);
        float f2 = -1.0f;
        if (!location.hasSpeed()) {
            speed = -1.0f;
        }
        int accuracy = (int) (location.hasAccuracy() ? location.getAccuracy() : -1.0f);
        double altitude = location.hasAltitude() ? location.getAltitude() : 555.0d;
        if (location.hasBearing()) {
            f2 = location.getBearing();
        }
        if (c < -0.01f) {
            return String.format(Locale.CHINA, "&ll=%.5f|%.5f&s=%.1f&d=%.1f&ll_r=%d&ll_n=%d&ll_h=%.2f&ll_t=%d&ll_sn=%d|%d&ll_snr=%.1f", new Object[]{Double.valueOf(location.getLongitude()), Double.valueOf(location.getLatitude()), Float.valueOf(speed), Float.valueOf(f2), Integer.valueOf(accuracy), Integer.valueOf(a), Double.valueOf(altitude), Long.valueOf(location.getTime() / 1000), Integer.valueOf(a), Integer.valueOf(p), Double.valueOf(D)});
        }
        return String.format(Locale.CHINA, "&ll=%.5f|%.5f&s=%.1f&d=%.1f&ll_r=%d&ll_n=%d&ll_h=%.2f&ll_t=%d&ll_sn=%d|%d&ll_snr=%.1f&ll_bp=%.2f", new Object[]{Double.valueOf(location.getLongitude()), Double.valueOf(location.getLatitude()), Float.valueOf(speed), Float.valueOf(f2), Integer.valueOf(accuracy), Integer.valueOf(a), Double.valueOf(altitude), Long.valueOf(location.getTime() / 1000), Integer.valueOf(a), Integer.valueOf(p), Double.valueOf(D), Float.valueOf(c)});
    }

    /* access modifiers changed from: private */
    public String a(ArrayList<ArrayList<Float>> arrayList) {
        StringBuilder sb = new StringBuilder();
        if (arrayList.size() == 0) {
            return sb.toString();
        }
        Iterator<ArrayList<Float>> it = arrayList.iterator();
        boolean z2 = true;
        while (it.hasNext()) {
            ArrayList next = it.next();
            if (next.size() == 6) {
                if (z2) {
                    z2 = false;
                } else {
                    sb.append(LogUtils.VERTICAL);
                }
                sb.append(String.format("%.1f;", new Object[]{next.get(0)}));
                sb.append(String.format("%.1f;", new Object[]{next.get(1)}));
                sb.append(String.format("%.1f;", new Object[]{next.get(2)}));
                sb.append(String.format("%.0f;", new Object[]{next.get(3)}));
                sb.append(String.format("%.0f", new Object[]{next.get(4)}));
                sb.append(String.format("%.0f", new Object[]{next.get(5)}));
            }
        }
        return sb.toString();
    }

    private ArrayList<ArrayList<Float>> a(ArrayList<ArrayList<Float>> arrayList, boolean z2, float f2) {
        ArrayList<ArrayList<Float>> arrayList2 = new ArrayList<>();
        if (arrayList.size() <= 40 && arrayList.size() != 0) {
            Iterator<ArrayList<Float>> it = arrayList.iterator();
            while (it.hasNext()) {
                ArrayList next = it.next();
                if (next.size() == 6) {
                    float floatValue = ((Float) next.get(3)).floatValue();
                    float floatValue2 = ((Float) next.get(2)).floatValue();
                    if ((!z2 || floatValue >= 1.0f) && (f2 <= 0.0f || floatValue2 >= f2)) {
                        arrayList2.add(next);
                    }
                }
            }
        }
        return arrayList2;
    }

    /* access modifiers changed from: private */
    public ArrayList<ArrayList<Float>> a(boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, float f2) {
        ArrayList<ArrayList<Float>> arrayList = new ArrayList<>();
        if (z2) {
            arrayList.addAll(a(this.I, z6, f2));
        }
        if (z3) {
            arrayList.addAll(a(this.J, z6, f2));
        }
        if (z4) {
            arrayList.addAll(a(this.K, z6, f2));
        }
        if (z5) {
            arrayList.addAll(a(this.L, z6, f2));
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void a(String str, Location location) {
        if (location != null) {
            String str2 = str + com.baidu.location.b.a.a().d();
            boolean f2 = j.a().f();
            t.a(new a(b.a().f()));
            t.a(System.currentTimeMillis());
            t.a(new Location(location));
            t.a(str2);
            if (!f2) {
                w.a(t.c(), (i) null, t.d(), str2);
            }
        }
    }

    public static boolean a(Location location, Location location2, boolean z2) {
        if (location == location2) {
            return false;
        }
        if (location == null || location2 == null) {
            return true;
        }
        float speed = location2.getSpeed();
        if (z2 && ((k.v == 3 || !com.baidu.location.g.d.a().a(location2.getLongitude(), location2.getLatitude())) && speed < 5.0f)) {
            return true;
        }
        float distanceTo = location2.distanceTo(location);
        return speed > k.L ? distanceTo > k.N : speed > k.K ? distanceTo > k.M : distanceTo > 5.0f;
    }

    public static String b(Location location) {
        String a2 = a(location);
        if (a2 == null) {
            return a2;
        }
        return a2 + "&g_tp=0";
    }

    /* access modifiers changed from: private */
    public void b(boolean z2) {
        this.w = z2;
        c = -1.0f;
    }

    /* access modifiers changed from: private */
    public boolean b(String str) {
        int i2;
        if (str.indexOf("*") != -1 && str.indexOf("$") != -1 && str.indexOf("$") <= str.indexOf("*") && str.length() >= str.indexOf("*")) {
            byte[] bytes = str.substring(0, str.indexOf("*")).getBytes();
            byte b2 = bytes[1];
            for (int i3 = 2; i3 < bytes.length; i3++) {
                b2 ^= bytes[i3];
            }
            String format = String.format("%02x", new Object[]{Integer.valueOf(b2)});
            int indexOf = str.indexOf("*");
            if (indexOf != -1 && str.length() >= (i2 = indexOf + 3) && format.equalsIgnoreCase(str.substring(indexOf + 1, i2))) {
                return true;
            }
        }
        return false;
    }

    public static String c(Location location) {
        String a2 = a(location);
        if (a2 == null) {
            return a2;
        }
        return a2 + B;
    }

    /* access modifiers changed from: private */
    public void d(Location location) {
        this.C.sendMessage(this.C.obtainMessage(1, location));
    }

    /* access modifiers changed from: private */
    public void e(Location location) {
        if (location != null) {
            int i2 = a;
            if (i2 == 0) {
                try {
                    i2 = location.getExtras().getInt("satellites");
                } catch (Exception e2) {
                }
            }
            if (i2 != 0 || Math.abs(System.currentTimeMillis() - this.F) <= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS || k.m) {
                if (this.n && ((double) location.getSpeed()) == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && this.z != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && ((double) System.currentTimeMillis()) - this.A < 2000.0d) {
                    location.setSpeed((float) this.z);
                }
                Location location2 = new Location(location);
                this.x = System.currentTimeMillis();
                System.currentTimeMillis();
                this.g = location;
                int i3 = a;
                if (location == null) {
                    this.v = null;
                } else {
                    long currentTimeMillis = System.currentTimeMillis();
                    this.g.setTime(currentTimeMillis);
                    float speed = (float) (((double) this.g.getSpeed()) * 3.6d);
                    if (!this.g.hasSpeed()) {
                        speed = -1.0f;
                    }
                    if (i3 == 0) {
                        try {
                            i3 = this.g.getExtras().getInt("satellites");
                        } catch (Exception e3) {
                        }
                    }
                    this.v = String.format(Locale.CHINA, "&ll=%.5f|%.5f&s=%.1f&d=%.1f&ll_n=%d&ll_t=%d", new Object[]{Double.valueOf(this.g.getLongitude()), Double.valueOf(this.g.getLatitude()), Float.valueOf(speed), Float.valueOf(this.g.getBearing()), Integer.valueOf(i3), Long.valueOf(currentTimeMillis)});
                }
                if (this.g != null) {
                    if (g.a().e()) {
                        g.a().a(this.g, this.H);
                    }
                    if (!g.a().f()) {
                        boolean f2 = g.a().f();
                        com.baidu.location.b.a.a().a(g());
                    }
                    if (a > 2 && w.a(this.g, true)) {
                        boolean f3 = j.a().f();
                        t.a(new a(b.a().f()));
                        t.a(System.currentTimeMillis());
                        t.a(new Location(this.g));
                        t.a(com.baidu.location.b.a.a().d());
                        if (!f3) {
                            x.a().b();
                        }
                    }
                }
                x.a().a(location2, a);
                return;
            }
            return;
        }
        this.g = null;
    }

    public static String k() {
        long currentTimeMillis = System.currentTimeMillis() - r;
        if (currentTimeMillis < 0 || currentTimeMillis >= 3000) {
            return null;
        }
        return String.format(Locale.US, "&gsvn=%d&gsfn=%d", new Object[]{Integer.valueOf(q), Integer.valueOf(a)});
    }

    public void a(String str) {
        if (str.length() == 0 || !b(str)) {
            return;
        }
        if (str.startsWith("$GPPWR,") || str.startsWith("$GNGST,") || str.startsWith("$GPGST,") || str.startsWith("$GLGSV,") || str.startsWith("$GNGSV,") || str.startsWith("$BDGSV,") || str.startsWith("$GPZDA,") || str.startsWith("$GPGSA,") || str.startsWith("$GNVTG,") || str.startsWith("$GPVTG,") || str.startsWith("$GNGSA,") || str.startsWith("$GPNTR,") || str.startsWith("$GNGGA,") || str.startsWith("$GPGGA,") || str.startsWith("$GPRMC,") || str.startsWith("$GPGSV,") || str.startsWith("$BDGSA,")) {
            String[] split = str.split(",");
            a(str, ",");
            if (split == null || split.length <= 0) {
                return;
            }
            if ((split[0].equalsIgnoreCase("$GPRMC") || split[0].equalsIgnoreCase("$GNRMC") || split[0].equalsIgnoreCase("$GLRMC") || split[0].equalsIgnoreCase("$BDRMC")) && split.length > 7 && split[7].trim().length() > 0) {
                this.z = ((Double.valueOf(split[7]).doubleValue() * 1.852d) / 3600.0d) * 1000.0d;
                this.A = (double) System.currentTimeMillis();
            }
        }
    }

    public void a(boolean z2) {
        if (z2) {
            c();
        } else {
            d();
        }
    }

    public synchronized void b() {
        if (com.baidu.location.f.isServing) {
            Context serviceContext = com.baidu.location.f.getServiceContext();
            this.e = serviceContext;
            try {
                this.f = (LocationManager) serviceContext.getSystemService("location");
                if (!this.l) {
                    c cVar = new c(this, (g) null);
                    this.m = cVar;
                    this.f.addGpsStatusListener(cVar);
                } else {
                    b bVar = new b(this, (g) null);
                    this.k = bVar;
                    this.f.registerGnssStatusCallback(bVar);
                }
                if (this.n && Build.VERSION.SDK_INT >= 24) {
                    g gVar = new g(this);
                    this.o = gVar;
                    this.f.addNmeaListener(gVar);
                }
                e eVar = new e(this, (g) null);
                this.i = eVar;
                this.f.requestLocationUpdates("passive", 9000, 0.0f, eVar);
            } catch (Exception e2) {
            }
            this.C = new h(this);
        }
    }

    public void c() {
        Log.d(com.baidu.location.g.a.a, "start gps...");
        if (!this.u) {
            try {
                this.h = new d(this, (g) null);
                try {
                    this.f.sendExtraCommand("gps", "force_xtra_injection", new Bundle());
                } catch (Exception e2) {
                }
                this.f.requestLocationUpdates("gps", 1000, 0.0f, this.h);
                this.E = System.currentTimeMillis();
                this.u = true;
            } catch (Exception e3) {
            }
        }
    }

    public void d() {
        if (this.u) {
            LocationManager locationManager = this.f;
            if (locationManager != null) {
                try {
                    if (this.h != null) {
                        locationManager.removeUpdates(this.h);
                    }
                } catch (Exception e2) {
                }
            }
            k.d = 0;
            k.v = 0;
            this.h = null;
            this.u = false;
            b(false);
        }
    }

    public synchronized void e() {
        d();
        if (this.f != null) {
            try {
                if (this.m != null) {
                    this.f.removeGpsStatusListener(this.m);
                }
                if (this.l && this.k != null) {
                    this.f.unregisterGnssStatusCallback(this.k);
                }
                this.f.removeUpdates(this.i);
            } catch (Exception e2) {
            }
            this.m = null;
            this.f = null;
        }
    }

    public String f() {
        Location location;
        if (!j() || (location = this.g) == null) {
            return null;
        }
        return String.format("%s&idgps_tp=%s", new Object[]{a(location).replaceAll("ll", "idll").replaceAll("&d=", "&idd=").replaceAll("&s", "&ids="), this.g.getProvider()});
    }

    public String g() {
        boolean z2;
        StringBuilder sb;
        String str;
        if (this.g == null) {
            return null;
        }
        String str2 = "{\"result\":{\"time\":\"" + k.a() + "\",\"error\":\"61\"},\"content\":{\"point\":{\"x\":" + "\"%f\",\"y\":\"%f\"},\"radius\":\"%d\",\"d\":\"%f\"," + "\"s\":\"%f\",\"n\":\"%d\"";
        int accuracy = (int) (this.g.hasAccuracy() ? this.g.getAccuracy() : 10.0f);
        float speed = (float) (((double) this.g.getSpeed()) * 3.6d);
        if (!this.g.hasSpeed()) {
            speed = -1.0f;
        }
        double[] dArr = new double[2];
        if (com.baidu.location.g.d.a().a(this.g.getLongitude(), this.g.getLatitude())) {
            dArr = Jni.coorEncrypt(this.g.getLongitude(), this.g.getLatitude(), BDLocation.BDLOCATION_WGS84_TO_GCJ02);
            if (dArr[0] <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && dArr[1] <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                dArr[0] = this.g.getLongitude();
                dArr[1] = this.g.getLatitude();
            }
            z2 = true;
        } else {
            dArr[0] = this.g.getLongitude();
            dArr[1] = this.g.getLatitude();
            if (dArr[0] <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && dArr[1] <= FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                dArr[0] = this.g.getLongitude();
                dArr[1] = this.g.getLatitude();
            }
            z2 = false;
        }
        String format = String.format(Locale.CHINA, str2, new Object[]{Double.valueOf(dArr[0]), Double.valueOf(dArr[1]), Integer.valueOf(accuracy), Float.valueOf(this.g.getBearing()), Float.valueOf(speed), Integer.valueOf(a)});
        if (!z2) {
            format = format + ",\"in_cn\":\"0\"";
        }
        if (this.g.hasAltitude()) {
            sb = new StringBuilder();
            sb.append(format);
            str = String.format(Locale.CHINA, ",\"h\":%.2f}}", new Object[]{Double.valueOf(this.g.getAltitude())});
        } else {
            sb = new StringBuilder();
            sb.append(format);
            str = "}}";
        }
        sb.append(str);
        return sb.toString();
    }

    public Location h() {
        if (this.g != null && Math.abs(System.currentTimeMillis() - this.g.getTime()) <= DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
            return this.g;
        }
        return null;
    }

    public boolean i() {
        try {
            long currentTimeMillis = System.currentTimeMillis() - this.F;
            int i2 = a;
            if (i2 == 0) {
                try {
                    i2 = this.g.getExtras().getInt("satellites");
                } catch (Exception e2) {
                }
            }
            return (this.g == null || this.g.getLatitude() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || this.g.getLongitude() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || (i2 <= 2 && Math.abs(currentTimeMillis) >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS && !k.m)) ? false : true;
        } catch (Exception e3) {
            Location location = this.g;
            return (location == null || location.getLatitude() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE || this.g.getLongitude() == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) ? false : true;
        }
    }

    public boolean j() {
        if (!i() || System.currentTimeMillis() - this.x > OkHttpUtils.DEFAULT_MILLISECONDS) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (!this.t || currentTimeMillis - this.s >= 3000) {
            return this.w;
        }
        return true;
    }
}
