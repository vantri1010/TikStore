package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;

public final class DotOptions extends OverlayOptions {
    int a;
    boolean b = true;
    Bundle c;
    private LatLng d;
    private int e = -16777216;
    private int f = 5;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Dot dot = new Dot();
        dot.B = this.b;
        dot.A = this.a;
        dot.C = this.c;
        dot.b = this.e;
        dot.a = this.d;
        dot.c = this.f;
        return dot;
    }

    public DotOptions center(LatLng latLng) {
        if (latLng != null) {
            this.d = latLng;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: dot center can not be null");
    }

    public DotOptions color(int i) {
        this.e = i;
        return this;
    }

    public DotOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public LatLng getCenter() {
        return this.d;
    }

    public int getColor() {
        return this.e;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public int getRadius() {
        return this.f;
    }

    public int getZIndex() {
        return this.a;
    }

    public boolean isVisible() {
        return this.b;
    }

    public DotOptions radius(int i) {
        if (i > 0) {
            this.f = i;
        }
        return this;
    }

    public DotOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public DotOptions zIndex(int i) {
        this.a = i;
        return this;
    }
}
