package com.baidu.mapsdkvi;

import android.net.NetworkInfo;

/* synthetic */ class d {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[NetworkInfo.State.values().length];
        a = iArr;
        try {
            iArr[NetworkInfo.State.CONNECTED.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[NetworkInfo.State.CONNECTING.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[NetworkInfo.State.DISCONNECTED.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            a[NetworkInfo.State.DISCONNECTING.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            a[NetworkInfo.State.SUSPENDED.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
    }
}
