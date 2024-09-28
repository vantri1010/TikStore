package com.baidu.platform.core.d;

import com.baidu.platform.base.SearchType;

/* synthetic */ class l {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[SearchType.values().length];
        a = iArr;
        try {
            iArr[SearchType.TRANSIT_ROUTE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[SearchType.DRIVE_ROUTE.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[SearchType.WALK_ROUTE.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
    }
}
