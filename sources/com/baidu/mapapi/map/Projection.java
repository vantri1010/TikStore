package com.baidu.mapapi.map;

import android.graphics.Point;
import android.graphics.PointF;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.baidu.mapsdkplatform.comapi.map.e;

public final class Projection {
    private e a;

    Projection(e eVar) {
        this.a = eVar;
    }

    public LatLng fromScreenLocation(Point point) {
        e eVar;
        if (point == null || (eVar = this.a) == null) {
            return null;
        }
        return CoordUtil.mc2ll(eVar.b(point.x, point.y));
    }

    public float metersToEquatorPixels(float f) {
        if (f <= 0.0f) {
            return 0.0f;
        }
        return (float) (((double) f) / this.a.K());
    }

    public PointF toOpenGLLocation(LatLng latLng, MapStatus mapStatus) {
        if (latLng == null || mapStatus == null) {
            return null;
        }
        GeoPoint ll2mc = CoordUtil.ll2mc(latLng);
        ad adVar = mapStatus.a;
        return new PointF((float) (ll2mc.getLongitudeE6() - adVar.d), (float) (ll2mc.getLatitudeE6() - adVar.e));
    }

    public PointF toOpenGLNormalization(LatLng latLng, MapStatus mapStatus) {
        if (latLng == null || mapStatus == null) {
            return null;
        }
        GeoPoint ll2mc = CoordUtil.ll2mc(latLng);
        ad.a aVar = mapStatus.a.k;
        return new PointF((float) ((((ll2mc.getLongitudeE6() - ((double) aVar.a)) * 2.0d) / ((double) Math.abs(aVar.b - aVar.a))) - 1.0d), (float) ((((ll2mc.getLatitudeE6() - ((double) aVar.d)) * 2.0d) / ((double) Math.abs(aVar.c - aVar.d))) - 1.0d));
    }

    public Point toScreenLocation(LatLng latLng) {
        if (latLng == null || this.a == null) {
            return null;
        }
        return this.a.a(CoordUtil.ll2mc(latLng));
    }
}
