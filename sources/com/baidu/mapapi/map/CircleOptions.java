package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;
import java.util.List;

public final class CircleOptions extends OverlayOptions {
    private static final String d = CircleOptions.class.getSimpleName();
    int a;
    boolean b = true;
    Bundle c;
    private LatLng e;
    private int f = -16777216;
    private int g;
    private Stroke h;
    private boolean i = false;
    private int j = 0;
    private List<HoleOptions> k;
    private HoleOptions l;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Circle circle = new Circle();
        circle.B = this.b;
        circle.A = this.a;
        circle.C = this.c;
        circle.b = this.f;
        circle.a = this.e;
        circle.c = this.g;
        circle.d = this.h;
        circle.e = this.i;
        circle.f = this.j;
        circle.g = this.k;
        circle.h = this.l;
        return circle;
    }

    public CircleOptions addHoleOption(HoleOptions holeOptions) {
        this.l = holeOptions;
        return this;
    }

    public CircleOptions addHoleOptions(List<HoleOptions> list) {
        this.k = list;
        return this;
    }

    public CircleOptions center(LatLng latLng) {
        if (latLng != null) {
            this.e = latLng;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: circle center can not be null");
    }

    public CircleOptions dottedStroke(boolean z) {
        this.i = z;
        return this;
    }

    public CircleOptions dottedStrokeType(CircleDottedStrokeType circleDottedStrokeType) {
        this.j = circleDottedStrokeType.ordinal();
        return this;
    }

    public CircleOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public CircleOptions fillColor(int i2) {
        this.f = i2;
        return this;
    }

    public LatLng getCenter() {
        return this.e;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public int getFillColor() {
        return this.f;
    }

    public int getRadius() {
        return this.g;
    }

    public Stroke getStroke() {
        return this.h;
    }

    public int getZIndex() {
        return this.a;
    }

    public boolean isVisible() {
        return this.b;
    }

    public CircleOptions radius(int i2) {
        this.g = i2;
        return this;
    }

    public CircleOptions stroke(Stroke stroke) {
        this.h = stroke;
        return this;
    }

    public CircleOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public CircleOptions zIndex(int i2) {
        this.a = i2;
        return this;
    }
}
