package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.j;
import java.util.ArrayList;
import java.util.List;

public final class Polygon extends Overlay {
    Stroke a;
    int b;
    List<LatLng> c;
    List<HoleOptions> d;
    HoleOptions e;

    Polygon() {
        this.type = j.polygon;
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
        GeoPoint ll2mc = CoordUtil.ll2mc(this.c.get(0));
        bundle.putDouble("location_x", ll2mc.getLongitudeE6());
        bundle.putDouble("location_y", ll2mc.getLatitudeE6());
        Overlay.a(this.c, bundle);
        Overlay.a(this.b, bundle);
        if (this.a == null) {
            bundle.putInt("has_stroke", 0);
        } else {
            bundle.putInt("has_stroke", 1);
            bundle.putBundle("stroke", this.a.a(new Bundle()));
        }
        List<HoleOptions> list2 = this.d;
        if (list2 != null && list2.size() != 0) {
            list = this.d;
        } else if (this.e != null) {
            list = new ArrayList();
            list.add(this.e);
        } else {
            bundle.putInt("has_holes", 0);
            return bundle;
        }
        c(list, bundle);
        return bundle;
    }

    public int getFillColor() {
        return this.b;
    }

    public HoleOptions getHoleOption() {
        return this.e;
    }

    public List<HoleOptions> getHoleOptions() {
        return this.d;
    }

    public List<LatLng> getPoints() {
        return this.c;
    }

    public Stroke getStroke() {
        return this.a;
    }

    public void setFillColor(int i) {
        this.b = i;
        this.listener.b(this);
    }

    public void setHoleOption(HoleOptions holeOptions) {
        this.e = holeOptions;
        this.d = null;
        this.listener.b(this);
    }

    public void setHoleOptions(List<HoleOptions> list) {
        this.d = list;
        this.e = null;
        this.listener.b(this);
    }

    public void setPoints(List<LatLng> list) {
        if (list == null) {
            throw new IllegalArgumentException("BDMapSDKException: points list can not be null");
        } else if (list.size() <= 2) {
            throw new IllegalArgumentException("BDMapSDKException: points count can not less than three");
        } else if (!list.contains((Object) null)) {
            int i = 0;
            while (i < list.size()) {
                int i2 = i + 1;
                int i3 = i2;
                while (i3 < list.size()) {
                    if (list.get(i) != list.get(i3)) {
                        i3++;
                    } else {
                        throw new IllegalArgumentException("BDMapSDKException: points list can not has same points");
                    }
                }
                i = i2;
            }
            this.c = list;
            this.listener.b(this);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: points list can not contains null");
        }
    }

    public void setStroke(Stroke stroke) {
        this.a = stroke;
        this.listener.b(this);
    }
}
