package com.baidu.mapapi.utils;

import com.baidu.mapapi.utils.CoordinateConverter;

/* synthetic */ class a {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[CoordinateConverter.CoordType.values().length];
        a = iArr;
        try {
            iArr[CoordinateConverter.CoordType.COMMON.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[CoordinateConverter.CoordType.GPS.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[CoordinateConverter.CoordType.BD09LL.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            a[CoordinateConverter.CoordType.BD09MC.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
    }
}
