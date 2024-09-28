package com.baidu.mapapi.map;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public final class MapStatus implements Parcelable {
    public static final Parcelable.Creator<MapStatus> CREATOR = new k();
    ad a;
    private double b;
    public final LatLngBounds bound;
    private double c;
    public final float overlook;
    public final float rotate;
    public final LatLng target;
    public final Point targetScreen;
    public WinRound winRound;
    public final float zoom;

    public static final class Builder {
        private float a = -2.14748365E9f;
        private LatLng b = null;
        private float c = -2.14748365E9f;
        private float d = -2.14748365E9f;
        private Point e = null;
        private LatLngBounds f = null;
        private double g = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private double h = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        private final float i = 15.0f;

        public Builder() {
        }

        public Builder(MapStatus mapStatus) {
            this.a = mapStatus.rotate;
            this.b = mapStatus.target;
            this.c = mapStatus.overlook;
            this.d = mapStatus.zoom;
            this.e = mapStatus.targetScreen;
            this.g = mapStatus.a();
            this.h = mapStatus.b();
        }

        private float a(float f2) {
            if (15.0f == f2) {
                return 15.5f;
            }
            return f2;
        }

        public MapStatus build() {
            return new MapStatus(this.a, this.b, this.c, this.d, this.e, this.f);
        }

        public Builder overlook(float f2) {
            this.c = f2;
            return this;
        }

        public Builder rotate(float f2) {
            this.a = f2;
            return this;
        }

        public Builder target(LatLng latLng) {
            this.b = latLng;
            return this;
        }

        public Builder targetScreen(Point point) {
            this.e = point;
            return this;
        }

        public Builder zoom(float f2) {
            this.d = a(f2);
            return this;
        }
    }

    MapStatus(float f, LatLng latLng, float f2, float f3, Point point, double d, double d2, LatLngBounds latLngBounds) {
        this.rotate = f;
        this.target = latLng;
        this.overlook = f2;
        this.zoom = f3;
        this.targetScreen = point;
        this.b = d;
        this.c = d2;
        this.bound = latLngBounds;
    }

    MapStatus(float f, LatLng latLng, float f2, float f3, Point point, LatLngBounds latLngBounds) {
        this.rotate = f;
        this.target = latLng;
        this.overlook = f2;
        this.zoom = f3;
        this.targetScreen = point;
        if (latLng != null) {
            this.b = CoordUtil.ll2mc(latLng).getLongitudeE6();
            this.c = CoordUtil.ll2mc(this.target).getLatitudeE6();
        }
        this.bound = latLngBounds;
    }

    MapStatus(float f, LatLng latLng, float f2, float f3, Point point, ad adVar, double d, double d2, LatLngBounds latLngBounds, WinRound winRound2) {
        this.rotate = f;
        this.target = latLng;
        this.overlook = f2;
        this.zoom = f3;
        this.targetScreen = point;
        this.a = adVar;
        this.b = d;
        this.c = d2;
        this.bound = latLngBounds;
        this.winRound = winRound2;
    }

    protected MapStatus(Parcel parcel) {
        this.rotate = parcel.readFloat();
        this.target = (LatLng) parcel.readParcelable(LatLng.class.getClassLoader());
        this.overlook = parcel.readFloat();
        this.zoom = parcel.readFloat();
        this.targetScreen = (Point) parcel.readParcelable(Point.class.getClassLoader());
        this.bound = (LatLngBounds) parcel.readParcelable(LatLngBounds.class.getClassLoader());
        this.b = parcel.readDouble();
        this.c = parcel.readDouble();
    }

    static MapStatus a(ad adVar) {
        ad adVar2 = adVar;
        if (adVar2 == null) {
            return null;
        }
        float f = (float) adVar2.b;
        double d = adVar2.e;
        double d2 = adVar2.d;
        LatLng mc2ll = CoordUtil.mc2ll(new GeoPoint(d, d2));
        float f2 = (float) adVar2.c;
        float f3 = adVar2.a;
        Point point = new Point(adVar2.f, adVar2.g);
        LatLng mc2ll2 = CoordUtil.mc2ll(new GeoPoint((double) adVar2.k.e.y, (double) adVar2.k.e.x));
        LatLng mc2ll3 = CoordUtil.mc2ll(new GeoPoint((double) adVar2.k.f.y, (double) adVar2.k.f.x));
        double d3 = d;
        LatLng mc2ll4 = CoordUtil.mc2ll(new GeoPoint((double) adVar2.k.h.y, (double) adVar2.k.h.x));
        LatLng mc2ll5 = CoordUtil.mc2ll(new GeoPoint((double) adVar2.k.g.y, (double) adVar2.k.g.x));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mc2ll2);
        builder.include(mc2ll3);
        builder.include(mc2ll4);
        builder.include(mc2ll5);
        WinRound winRound2 = adVar2.j;
        return new MapStatus(f, mc2ll, f2, f3, point, adVar, d2, d3, builder.build(), winRound2);
    }

    /* access modifiers changed from: package-private */
    public double a() {
        return this.b;
    }

    /* access modifiers changed from: package-private */
    public double b() {
        return this.c;
    }

    /* access modifiers changed from: package-private */
    public ad b(ad adVar) {
        if (adVar == null) {
            return null;
        }
        float f = this.rotate;
        if (f != -2.14748365E9f) {
            adVar.b = (int) f;
        }
        float f2 = this.zoom;
        if (f2 != -2.14748365E9f) {
            adVar.a = f2;
        }
        float f3 = this.overlook;
        if (f3 != -2.14748365E9f) {
            adVar.c = (int) f3;
        }
        if (this.target != null) {
            adVar.d = this.b;
            adVar.e = this.c;
        }
        Point point = this.targetScreen;
        if (point != null) {
            adVar.f = point.x;
            adVar.g = this.targetScreen.y;
        }
        return adVar;
    }

    /* access modifiers changed from: package-private */
    public ad c() {
        return b(new ad());
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.target != null) {
            sb.append("target lat: " + this.target.latitude + "\n");
            sb.append("target lng: " + this.target.longitude + "\n");
        }
        if (this.targetScreen != null) {
            sb.append("target screen x: " + this.targetScreen.x + "\n");
            sb.append("target screen y: " + this.targetScreen.y + "\n");
        }
        sb.append("zoom: " + this.zoom + "\n");
        sb.append("rotate: " + this.rotate + "\n");
        sb.append("overlook: " + this.overlook + "\n");
        return sb.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.rotate);
        parcel.writeParcelable(this.target, i);
        parcel.writeFloat(this.overlook);
        parcel.writeFloat(this.zoom);
        parcel.writeParcelable(this.targetScreen, i);
        parcel.writeParcelable(this.bound, i);
        parcel.writeDouble(this.b);
        parcel.writeDouble(this.c);
    }
}
