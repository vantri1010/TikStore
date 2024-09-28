package com.baidu.mapapi.utils.poi;

import com.baidu.mapapi.http.HttpClient;
import com.baidu.platform.comapi.pano.PanoStateError;

/* synthetic */ class b {
    static final /* synthetic */ int[] a;
    static final /* synthetic */ int[] b;

    static {
        int[] iArr = new int[HttpClient.HttpStateError.values().length];
        b = iArr;
        try {
            iArr[HttpClient.HttpStateError.NETWORK_ERROR.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            b[HttpClient.HttpStateError.INNER_ERROR.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        int[] iArr2 = new int[PanoStateError.values().length];
        a = iArr2;
        try {
            iArr2[PanoStateError.PANO_UID_ERROR.ordinal()] = 1;
        } catch (NoSuchFieldError e3) {
        }
        try {
            a[PanoStateError.PANO_NOT_FOUND.ordinal()] = 2;
        } catch (NoSuchFieldError e4) {
        }
        try {
            a[PanoStateError.PANO_NO_TOKEN.ordinal()] = 3;
        } catch (NoSuchFieldError e5) {
        }
        try {
            a[PanoStateError.PANO_NO_ERROR.ordinal()] = 4;
        } catch (NoSuchFieldError e6) {
        }
    }
}
