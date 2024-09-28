package com.baidu.mapsdkplatform.comapi;

import com.baidu.mapsdkplatform.comapi.NativeLoader;

/* synthetic */ class e {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[NativeLoader.a.values().length];
        a = iArr;
        try {
            iArr[NativeLoader.a.ARM64.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[NativeLoader.a.ARMV7.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[NativeLoader.a.ARMEABI.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            a[NativeLoader.a.X86_64.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            a[NativeLoader.a.X86.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
    }
}
