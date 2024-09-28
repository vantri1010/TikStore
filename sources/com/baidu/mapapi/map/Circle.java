package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.j;
import java.util.ArrayList;
import java.util.List;

public final class Circle extends Overlay {
    LatLng a;
    int b;
    int c;
    Stroke d;
    boolean e;
    int f = 0;
    List<HoleOptions> g;
    HoleOptions h;

    Circle() {
        this.type = j.circle;
    }

    private void b(Bundle bundle) {
        BitmapDescriptor fromAsset = BitmapDescriptorFactory.fromAsset(this.f == 1 ? "CircleDashTexture.png" : "lineDashTexture.png");
        if (fromAsset != null) {
            bundle.putBundle("image_info", fromAsset.b());
        }
    }

    private void c(List<HoleOptions> list, Bundle bundle) {
        Bundle bundle2 = new Bundle();
        boolean b2 = Overlay.b(list, bundle2);
        bundle.putInt("has_holes", b2 ? 1 : 0);
        if (b2) {
            bundle.putBundle("holes", bundle2);
        }
    }

    /* access modifiers changed from: package-private */
    public Bundle a(Bundle bundle) {
        List list;
        super.a(bundle);
        GeoPoint ll2mc = CoordUtil.ll2mc(this.a);
        bundle.putDouble("location_x", ll2mc.getLongitudeE6());
        bundle.putDouble("location_y", ll2mc.getLatitudeE6());
        if (this.e) {
            bundle.putDouble("dotted_stroke_location_x", ll2mc.getLongitudeE6());
            bundle.putDouble("dotted_stroke_location_y", ll2mc.getLatitudeE6());
            bundle.putInt("has_dotted_stroke", 1);
            b(bundle);
        } else {
            bundle.putInt("has_dotted_stroke", 0);
        }
        bundle.putInt("radius", CoordUtil.getMCDistanceByOneLatLngAndRadius(this.a, this.c));
        Overlay.a(this.b, bundle);
        if (this.d == null) {
            bundle.putInt("has_stroke", 0);
        } else {
            bundle.putInt("has_stroke", 1);
            bundle.putBundle("stroke", this.d.a(new Bundle()));
        }
        List<HoleOptions> list2 = this.g;
        if (list2 != null && list2.size() != 0) {
            list = this.g;
        } else if (this.h != null) {
            list = new ArrayList();
            list.add(this.h);
        } else {
            bundle.putInt("has_holes", 0);
            return bundle;
        }
        c(list, bundle);
        return bundle;
    }

    public LatLng getCenter() {
        return this.a;
    }

    public int getDottedStrokeType() {
        return this.f;
    }

    public int getFillColor() {
        return this.b;
    }

    public HoleOptions getHoleOption() {
        return this.h;
    }

    public List<HoleOptions> getHoleOptions() {
        return this.g;
    }

    public int getRadius() {
        return this.c;
    }

    public Stroke getStroke() {
        return this.d;
    }

    public boolean isDottedStroke() {
        return this.e;
    }

    public void setCenter(LatLng latLng) {
        if (latLng != null) {
            this.a = latLng;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: circle center can not be null");
    }

    public void setDottedStroke(boolean z) {
        this.e = z;
        this.listener.b(this);
    }

    public void setDottedStrokeType(CircleDottedStrokeType circleDottedStrokeType) {
        this.f = circleDottedStrokeType.ordinal();
        this.listener.b(this);
    }

    public void setFillColor(int i) {
        this.b = i;
        this.listener.b(this);
    }

    public void setHoleOption(HoleOptions holeOptions) {
        this.h = holeOptions;
        this.g = null;
        this.listener.b(this);
    }

    public void setHoleOptions(List<HoleOptions> list) {
        this.g = list;
        this.h = null;
        this.listener.b(this);
    }

    public void setRadius(int i) {
        this.c = i;
        this.listener.b(this);
    }

    public void setStroke(Stroke stroke) {
        this.d = stroke;
        this.listener.b(this);
    }
}
