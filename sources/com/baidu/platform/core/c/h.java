package com.baidu.platform.core.c;

import com.baidu.platform.base.SearchType;

/* synthetic */ class h {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[SearchType.values().length];
        a = iArr;
        try {
            iArr[SearchType.POI_NEAR_BY_SEARCH.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[SearchType.POI_IN_CITY_SEARCH.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[SearchType.POI_IN_BOUND_SEARCH.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
    }
}
