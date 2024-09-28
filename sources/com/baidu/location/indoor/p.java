package com.baidu.location.indoor;

import android.location.Location;
import android.os.Handler;
import com.baidu.location.BDLocation;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class p {
    /* access modifiers changed from: private */
    public a a;
    /* access modifiers changed from: private */
    public long b = 450;
    /* access modifiers changed from: private */
    public BDLocation c;
    private b d = null;
    /* access modifiers changed from: private */
    public b e = null;
    private b f = new b();
    private b g = new b();
    private b h = new b();
    private b i = new b();
    /* access modifiers changed from: private */
    public BDLocation j = null;
    /* access modifiers changed from: private */
    public long k = -1;
    private boolean l = false;
    /* access modifiers changed from: private */
    public Handler m = new Handler();
    private Runnable n = new q(this);
    /* access modifiers changed from: private */
    public Runnable o = new r(this);

    public interface a {
        void a(BDLocation bDLocation);
    }

    private class b {
        public double a;
        public double b;

        public b() {
            this.a = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            this.b = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }

        public b(double d, double d2) {
            this.a = d;
            this.b = d2;
        }

        public b(b bVar) {
            this.a = bVar.a;
            this.b = bVar.b;
        }

        public b a(double d) {
            return new b(this.a * d, this.b * d);
        }

        public b a(b bVar) {
            return new b(this.a - bVar.a, this.b - bVar.b);
        }

        public b b(b bVar) {
            return new b(this.a + bVar.a, this.b + bVar.b);
        }

        public boolean b(double d) {
            double abs = Math.abs(this.a);
            double abs2 = Math.abs(this.b);
            return abs > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && abs < d && abs2 > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && abs2 < d;
        }
    }

    /* access modifiers changed from: private */
    public b a(b bVar) {
        b bVar2 = this.d;
        if (bVar2 == null || bVar == null) {
            return null;
        }
        b a2 = bVar2.a(bVar);
        this.i = this.i.b(a2);
        b a3 = this.h.a(this.f);
        this.f = new b(this.h);
        this.h = new b(a2);
        b a4 = a2.a(0.2d);
        b a5 = this.i.a(0.01d);
        return a4.b(a5).b(a3.a(-0.02d));
    }

    public void a() {
        if (this.l) {
            this.l = false;
            this.m.removeCallbacks(this.o);
            b();
        }
    }

    public void a(long j2) {
        this.b = j2;
    }

    public synchronized void a(BDLocation bDLocation) {
        BDLocation bDLocation2 = bDLocation;
        synchronized (this) {
            double latitude = bDLocation.getLatitude();
            double longitude = bDLocation.getLongitude();
            this.c = bDLocation2;
            this.d = new b(latitude, longitude);
            if (this.e == null) {
                this.e = new b(latitude, longitude);
            }
            if (this.j == null) {
                this.j = new BDLocation(bDLocation2);
            } else {
                double latitude2 = this.j.getLatitude();
                double longitude2 = this.j.getLongitude();
                double latitude3 = bDLocation.getLatitude();
                double longitude3 = bDLocation.getLongitude();
                float[] fArr = new float[2];
                double d2 = longitude3;
                Location.distanceBetween(latitude2, longitude2, latitude3, longitude3, fArr);
                if (fArr[0] > 10.0f) {
                    this.j.setLatitude(latitude3);
                    this.j.setLongitude(d2);
                } else {
                    this.j.setLatitude((latitude2 + latitude3) / 2.0d);
                    this.j.setLongitude((longitude2 + d2) / 2.0d);
                }
            }
        }
    }

    public void b() {
        this.k = -1;
        this.e = null;
        this.d = null;
        this.f = new b();
        this.g = new b();
        this.h = new b();
        this.i = new b();
        this.j = null;
    }

    public boolean c() {
        return this.l;
    }
}
