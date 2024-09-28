package com.baidu.mapapi.map;

import android.graphics.Typeface;
import android.os.Bundle;
import com.baidu.mapapi.model.LatLng;

public final class TextOptions extends OverlayOptions {
    public static final int ALIGN_BOTTOM = 16;
    public static final int ALIGN_CENTER_HORIZONTAL = 4;
    public static final int ALIGN_CENTER_VERTICAL = 32;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int ALIGN_TOP = 8;
    int a;
    boolean b = true;
    Bundle c;
    private String d;
    private LatLng e;
    private int f;
    private int g = -16777216;
    private int h = 12;
    private Typeface i;
    private int j = 4;
    private int k = 32;
    private float l;

    /* access modifiers changed from: package-private */
    public Overlay a() {
        Text text = new Text();
        text.B = this.b;
        text.A = this.a;
        text.C = this.c;
        text.a = this.d;
        text.b = this.e;
        text.c = this.f;
        text.d = this.g;
        text.e = this.h;
        text.f = this.i;
        text.g = this.j;
        text.h = this.k;
        text.i = this.l;
        return text;
    }

    public TextOptions align(int i2, int i3) {
        this.j = i2;
        this.k = i3;
        return this;
    }

    public TextOptions bgColor(int i2) {
        this.f = i2;
        return this;
    }

    public TextOptions extraInfo(Bundle bundle) {
        this.c = bundle;
        return this;
    }

    public TextOptions fontColor(int i2) {
        this.g = i2;
        return this;
    }

    public TextOptions fontSize(int i2) {
        this.h = i2;
        return this;
    }

    public float getAlignX() {
        return (float) this.j;
    }

    public float getAlignY() {
        return (float) this.k;
    }

    public int getBgColor() {
        return this.f;
    }

    public Bundle getExtraInfo() {
        return this.c;
    }

    public int getFontColor() {
        return this.g;
    }

    public int getFontSize() {
        return this.h;
    }

    public LatLng getPosition() {
        return this.e;
    }

    public float getRotate() {
        return this.l;
    }

    public String getText() {
        return this.d;
    }

    public Typeface getTypeface() {
        return this.i;
    }

    public int getZIndex() {
        return this.a;
    }

    public boolean isVisible() {
        return this.b;
    }

    public TextOptions position(LatLng latLng) {
        if (latLng != null) {
            this.e = latLng;
            return this;
        }
        throw new IllegalArgumentException("BDMapSDKException: position can not be null");
    }

    public TextOptions rotate(float f2) {
        this.l = f2;
        return this;
    }

    public TextOptions text(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("BDMapSDKException: text can not be null or empty");
        }
        this.d = str;
        return this;
    }

    public TextOptions typeface(Typeface typeface) {
        this.i = typeface;
        return this;
    }

    public TextOptions visible(boolean z) {
        this.b = z;
        return this;
    }

    public TextOptions zIndex(int i2) {
        this.a = i2;
        return this;
    }
}
