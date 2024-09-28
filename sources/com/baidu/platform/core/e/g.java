package com.baidu.platform.core.e;

import com.baidu.platform.base.SearchType;

/* synthetic */ class g {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[SearchType.values().length];
        a = iArr;
        try {
            iArr[SearchType.POI_DETAIL_SHARE.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[SearchType.LOCATION_SEARCH_SHARE.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
    }
}
