package com.baidu.mapapi.map;

import android.graphics.Point;
import android.util.Log;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.e;

public final class MapStatusUpdate {
    private static final String o = MapStatusUpdate.class.getSimpleName();
    MapStatus a;
    LatLng b;
    LatLngBounds c;
    int d;
    int e;
    float f;
    int g;
    int h;
    float i;
    Point j;
    int k = 0;
    int l = 0;
    int m = 0;
    int n = 0;
    private int p;

    private MapStatusUpdate() {
    }

    MapStatusUpdate(int i2) {
        this.p = i2;
    }

    private float a(float f2) {
        return (float) (Math.pow(2.0d, (double) (18.0f - f2)) / ((double) (((float) SysOSUtil.getDensityDpi()) / 310.0f)));
    }

    private float a(LatLngBounds latLngBounds, e eVar, int i2, int i3) {
        GeoPoint ll2mc = CoordUtil.ll2mc(latLngBounds.southwest);
        GeoPoint ll2mc2 = CoordUtil.ll2mc(latLngBounds.northeast);
        int latitudeE6 = (int) ll2mc.getLatitudeE6();
        return eVar.a((int) ll2mc.getLongitudeE6(), (int) ll2mc2.getLatitudeE6(), (int) ll2mc2.getLongitudeE6(), latitudeE6, i2, i3);
    }

    private MapStatusUpdate a(MapStatus mapStatus) {
        MapStatusUpdate mapStatusUpdate = new MapStatusUpdate();
        synchronized (this) {
            mapStatusUpdate.a = mapStatus;
            mapStatusUpdate.c = this.c;
            mapStatusUpdate.k = this.k;
            mapStatusUpdate.l = this.l;
            mapStatusUpdate.m = this.m;
            mapStatusUpdate.n = this.n;
        }
        return mapStatusUpdate;
    }

    private LatLng a(LatLngBounds latLngBounds, e eVar, float f2) {
        double d2;
        double latitudeE6;
        if (latLngBounds == null || eVar == null) {
            return null;
        }
        GeoPoint ll2mc = CoordUtil.ll2mc(latLngBounds.getCenter());
        int i2 = this.k;
        double d3 = (double) (((float) i2) * f2);
        int i3 = this.m;
        double d4 = (double) (((float) i3) * f2);
        double d5 = (double) (((float) this.l) * f2);
        double d6 = (double) (((float) this.n) * f2);
        double longitudeE6 = i2 > i3 ? ll2mc.getLongitudeE6() - ((d3 - d4) / 2.0d) : i2 < i3 ? ll2mc.getLongitudeE6() + ((d4 - d3) / 2.0d) : ll2mc.getLongitudeE6();
        int i4 = this.l;
        int i5 = this.n;
        if (i4 < i5) {
            latitudeE6 = ll2mc.getLatitudeE6() - ((d6 - d5) / 2.0d);
        } else if (i4 > i5) {
            latitudeE6 = ll2mc.getLatitudeE6();
            d5 -= d6;
        } else {
            d2 = ll2mc.getLatitudeE6();
            return CoordUtil.mc2ll(new GeoPoint(d2, longitudeE6));
        }
        d2 = latitudeE6 + (d5 / 2.0d);
        return CoordUtil.mc2ll(new GeoPoint(d2, longitudeE6));
    }

    private boolean a(int i2, int i3, int i4, int i5, e eVar) {
        MapStatusUpdate G = eVar.G();
        return (G != null && i2 == G.k && i3 == G.l && i4 == G.m && i5 == G.n) ? false : true;
    }

    private boolean a(LatLngBounds latLngBounds, e eVar) {
        LatLngBounds latLngBounds2 = latLngBounds;
        MapStatusUpdate G = eVar.G();
        if (G == null) {
            return true;
        }
        return (latLngBounds2.southwest.latitude == G.c.southwest.latitude && latLngBounds2.southwest.longitude == G.c.southwest.longitude && latLngBounds2.northeast.latitude == G.c.northeast.latitude && latLngBounds2.northeast.longitude == G.c.northeast.longitude) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public MapStatus a(e eVar, MapStatus mapStatus) {
        e eVar2 = eVar;
        MapStatus mapStatus2 = mapStatus;
        if (eVar2 == null || mapStatus2 == null) {
            return null;
        }
        switch (this.p) {
            case 1:
                return this.a;
            case 2:
                return new MapStatus(mapStatus2.rotate, this.b, mapStatus2.overlook, mapStatus2.zoom, mapStatus2.targetScreen, (LatLngBounds) null);
            case 3:
                LatLngBounds latLngBounds = this.c;
                if (latLngBounds == null) {
                    return null;
                }
                GeoPoint ll2mc = CoordUtil.ll2mc(latLngBounds.southwest);
                GeoPoint ll2mc2 = CoordUtil.ll2mc(this.c.northeast);
                double longitudeE6 = ll2mc.getLongitudeE6();
                double latitudeE6 = ll2mc2.getLatitudeE6();
                e eVar3 = eVar;
                int i2 = (int) longitudeE6;
                int i3 = (int) latitudeE6;
                float a2 = eVar3.a(i2, i3, (int) ll2mc2.getLongitudeE6(), (int) ll2mc.getLatitudeE6(), mapStatus2.a.j.right - mapStatus2.a.j.left, mapStatus2.a.j.bottom - mapStatus2.a.j.top);
                LatLng center = this.c.getCenter();
                return new MapStatus(mapStatus2.rotate, center, mapStatus2.overlook, a2, mapStatus2.targetScreen, (LatLngBounds) null);
            case 4:
                return new MapStatus(mapStatus2.rotate, this.b, mapStatus2.overlook, this.f, mapStatus2.targetScreen, (LatLngBounds) null);
            case 5:
                GeoPoint b2 = eVar2.b((eVar.H() / 2) + this.g, (eVar.I() / 2) + this.h);
                return new MapStatus(mapStatus2.rotate, CoordUtil.mc2ll(b2), mapStatus2.overlook, mapStatus2.zoom, mapStatus2.targetScreen, b2.getLongitudeE6(), b2.getLatitudeE6(), (LatLngBounds) null);
            case 6:
                float f2 = mapStatus2.rotate;
                LatLng latLng = mapStatus2.target;
                float f3 = mapStatus2.overlook;
                return new MapStatus(f2, latLng, f3, mapStatus2.zoom + this.i, mapStatus2.targetScreen, mapStatus.a(), mapStatus.b(), (LatLngBounds) null);
            case 7:
                return new MapStatus(mapStatus2.rotate, CoordUtil.mc2ll(eVar2.b(this.j.x, this.j.y)), mapStatus2.overlook, mapStatus2.zoom + this.i, this.j, (LatLngBounds) null);
            case 8:
                float f4 = mapStatus2.rotate;
                LatLng latLng2 = mapStatus2.target;
                float f5 = mapStatus2.overlook;
                float f6 = this.f;
                return new MapStatus(f4, latLng2, f5, f6, mapStatus2.targetScreen, mapStatus.a(), mapStatus.b(), (LatLngBounds) null);
            case 9:
                LatLngBounds latLngBounds2 = this.c;
                if (latLngBounds2 == null) {
                    return null;
                }
                GeoPoint ll2mc3 = CoordUtil.ll2mc(latLngBounds2.southwest);
                GeoPoint ll2mc4 = CoordUtil.ll2mc(this.c.northeast);
                double longitudeE62 = ll2mc3.getLongitudeE6();
                double latitudeE62 = ll2mc4.getLatitudeE6();
                e eVar4 = eVar;
                int i4 = (int) longitudeE62;
                int i5 = (int) latitudeE62;
                float a3 = eVar4.a(i4, i5, (int) ll2mc4.getLongitudeE6(), (int) ll2mc3.getLatitudeE6(), this.d, this.e);
                LatLng center2 = this.c.getCenter();
                return new MapStatus(mapStatus2.rotate, center2, mapStatus2.overlook, a3, mapStatus2.targetScreen, (LatLngBounds) null);
            case 10:
                if (this.c == null) {
                    return null;
                }
                int H = (eVar.H() - this.k) - this.m;
                if (H < 0) {
                    H = eVar.H();
                    Log.e(o, "Bound paddingLeft or paddingRight too larger, please check");
                }
                int I = (eVar.I() - this.l) - this.n;
                if (I < 0) {
                    I = eVar.I();
                    Log.e(o, "Bound paddingTop or paddingBottom too larger, please check");
                }
                float a4 = a(this.c, eVar2, H, I);
                LatLng a5 = a(this.c, eVar2, a(a4));
                if (a5 == null) {
                    Log.e(o, "Bound center error");
                    return null;
                }
                boolean a6 = a(this.c, eVar2);
                boolean a7 = a(this.k, this.l, this.m, this.n, eVar);
                if (a6 || a7) {
                    MapStatus mapStatus3 = new MapStatus(mapStatus2.rotate, a5, mapStatus2.overlook, a4, (Point) null, (LatLngBounds) null);
                    eVar2.a(a(mapStatus3));
                    return mapStatus3;
                } else if (eVar.G() != null) {
                    return eVar.G().a;
                } else {
                    return null;
                }
            case 11:
                if (this.c == null) {
                    return null;
                }
                int H2 = (eVar.H() - this.k) - this.m;
                if (H2 < 0) {
                    H2 = eVar.H();
                    Log.e(o, "Bound paddingLeft or paddingRight too larger, please check");
                }
                int I2 = (eVar.I() - this.l) - this.n;
                if (I2 < 0) {
                    I2 = eVar.I();
                    Log.e(o, "Bound paddingTop or paddingBottom too larger, please check");
                }
                GeoPoint ll2mc5 = CoordUtil.ll2mc(this.c.southwest);
                GeoPoint ll2mc6 = CoordUtil.ll2mc(this.c.northeast);
                double longitudeE63 = ll2mc5.getLongitudeE6();
                e eVar5 = eVar;
                int i6 = (int) longitudeE63;
                float a8 = eVar5.a(i6, (int) ll2mc6.getLatitudeE6(), (int) ll2mc6.getLongitudeE6(), (int) ll2mc5.getLatitudeE6(), H2, I2);
                Point point = new Point(this.k + (H2 / 2), this.l + (I2 / 2));
                LatLng center3 = this.c.getCenter();
                return new MapStatus(mapStatus2.rotate, center3, mapStatus2.overlook, a8, point, (LatLngBounds) null);
            default:
                return null;
        }
    }
}
