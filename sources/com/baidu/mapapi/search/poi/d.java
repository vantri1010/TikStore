package com.baidu.mapapi.search.poi;

import com.baidu.mapapi.search.poi.PoiFilter;

/* synthetic */ class d {
    static final /* synthetic */ int[] a;

    static {
        int[] iArr = new int[PoiFilter.IndustryType.values().length];
        a = iArr;
        try {
            iArr[PoiFilter.IndustryType.HOTEL.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            a[PoiFilter.IndustryType.CATER.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            a[PoiFilter.IndustryType.LIFE.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
    }
}
