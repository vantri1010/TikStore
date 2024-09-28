package com.baidu.mapapi.map;

import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapsdkplatform.comapi.map.ac;

/* synthetic */ class f {
    static final /* synthetic */ int[] a;
    static final /* synthetic */ int[] b;

    static {
        int[] iArr = new int[ac.values().length];
        b = iArr;
        try {
            iArr[ac.TextureView.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            b[ac.GLSurfaceView.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        int[] iArr2 = new int[MyLocationConfiguration.LocationMode.values().length];
        a = iArr2;
        try {
            iArr2[MyLocationConfiguration.LocationMode.COMPASS.ordinal()] = 1;
        } catch (NoSuchFieldError e3) {
        }
        try {
            a[MyLocationConfiguration.LocationMode.FOLLOWING.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            a[MyLocationConfiguration.LocationMode.NORMAL.ordinal()] = 3;
        } catch (NoSuchFieldError e5) {
        }
    }
}
