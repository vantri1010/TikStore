package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;
import java.util.List;

public final class PolygonOptions extends OverlayOptions {
    int a;
    boolean b = true;
    Bundle c;
    private Stroke d;
    private int e = -16777216;
    private List<LatLng> f;
    private List<HoleOptions> g;
    private HoleOptions h;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Polygon polygon = new Polygon();
        polygon.B = this.b;
        polygon.A = this.a;
        polygon.C = this.c;
        List<LatLng> list = this.f;
        if (list == null || list.size() < 2) {
            throw new IllegalStateException("BDMapSDKException: when you add polyline, you must at least supply 2 points");
        }
        polygon.c = this.f;
        polygon.b = this.e;
        polygon.a = this.d;
        polygon.d = this.g;
        polygon.e = this.h;
        return polygon;
    }

    public PolygonOptions addHoleOption(HoleOptions holeOptions) {
        this.h = holeOptions;
        return this;
    }

    public PolygonOptions addHoleOptions(List<HoleOptions> list) {
        this.g = list;
        return this;
    }

    public PolygonOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public PolygonOptions fillColor(int i) {
        this.e = i;
        return this;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public int getFillColor() {
        return this.e;
    }

    public List<LatLng> getPoints() {
        return this.f;
    }

    public Stroke getStroke() {
        return this.d;
    }

    public int getZIndex() {
        return this.a;
    }

    public boolean isVisible() {
        return this.b;
    }

    public PolygonOptions points(List<LatLng> list) {
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
            this.f = list;
            return this;
        } else {
            throw new IllegalArgumentException("BDMapSDKException: points list can not contains null");
        }
    }

    public PolygonOptions stroke(Stroke stroke) {
        this.d = stroke;
        return this;
    }

    public PolygonOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public PolygonOptions zIndex(int i) {
        this.a = i;
        return this;
    }
}
