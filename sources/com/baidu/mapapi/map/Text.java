package com.baidu.mapapi.map;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.j;
import mapsdkvi.com.gdi.bgl.android.java.EnvDrawText;

public final class Text extends Overlay {
    private static final String k = Text.class.getSimpleName();
    String a;
    LatLng b;
    int c;
    int d;
    int e;
    Typeface f;
    int g;
    int h;
    float i;
    int j;

    Text() {
        this.type = j.text;
    }

    /* access modifiers changed from: package-private */
    public Bundle a() {
        Typeface typeface = this.f;
        if (typeface != null) {
            EnvDrawText.removeFontCache(typeface.hashCode());
        }
        return super.a();
    }

    /* access modifiers changed from: package-private */
    public Bundle a(Bundle bundle) {
        super.a(bundle);
        if (this.b != null) {
            bundle.putString("text", this.a);
            GeoPoint ll2mc = CoordUtil.ll2mc(this.b);
            bundle.putDouble("location_x", ll2mc.getLongitudeE6());
            bundle.putDouble("location_y", ll2mc.getLatitudeE6());
            int i2 = this.d;
            bundle.putInt("font_color", Color.argb(i2 >>> 24, i2 & 255, (i2 >> 8) & 255, (i2 >> 16) & 255));
            int i3 = this.c;
            bundle.putInt("bg_color", Color.argb(i3 >>> 24, i3 & 255, (i3 >> 8) & 255, (i3 >> 16) & 255));
            bundle.putInt("font_size", this.e);
            Typeface typeface = this.f;
            if (typeface != null) {
                EnvDrawText.registFontCache(typeface.hashCode(), this.f);
                bundle.putInt("type_face", this.f.hashCode());
            }
            int i4 = this.g;
            float f2 = 1.0f;
            bundle.putFloat("align_x", i4 != 1 ? i4 != 2 ? 0.5f : 1.0f : 0.0f);
            int i5 = this.h;
            if (i5 == 8) {
                f2 = 0.0f;
            } else if (i5 != 16) {
                f2 = 0.5f;
            }
            bundle.putFloat("align_y", f2);
            bundle.putFloat("rotate", this.i);
            bundle.putInt("update", this.j);
            return bundle;
        }
        throw new IllegalStateException("BDMapSDKException: when you add a text overlay, you must provide text and the position info.");
    }

    public float getAlignX() {
        return (float) this.g;
    }

    public float getAlignY() {
        return (float) this.h;
    }

    public int getBgColor() {
        return this.c;
    }

    public int getFontColor() {
        return this.d;
    }

    public int getFontSize() {
        return this.e;
    }

    public LatLng getPosition() {
        return this.b;
    }

    public float getRotate() {
        return this.i;
    }

    public String getText() {
        return this.a;
    }

    public Typeface getTypeface() {
        return this.f;
    }

    public void setAlign(int i2, int i3) {
        this.g = i2;
        this.h = i3;
        this.j = 1;
        this.listener.b(this);
    }

    public void setBgColor(int i2) {
        this.c = i2;
        this.j = 1;
        this.listener.b(this);
    }

    public void setFontColor(int i2) {
        this.d = i2;
        this.j = 1;
        this.listener.b(this);
    }

    public void setFontSize(int i2) {
        this.e = i2;
        this.j = 1;
        this.listener.b(this);
    }

    public void setPosition(LatLng latLng) {
        if (latLng != null) {
            this.b = latLng;
            this.j = 1;
            this.listener.b(this);
            return;
        }
        throw new IllegalArgumentException("BDMapSDKException: position can not be null");
    }

    public void setRotate(float f2) {
        this.i = f2;
        this.j = 1;
        this.listener.b(this);
    }

    public void setText(String str) {
        if (str == null || str.equals("")) {
            throw new IllegalArgumentException("BDMapSDKException: text can not be null or empty");
        }
        this.a = str;
        this.j = 1;
        this.listener.b(this);
    }

    public void setTypeface(Typeface typeface) {
        this.f = typeface;
        this.j = 1;
        this.listener.b(this);
    }
}
