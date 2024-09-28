package com.baidu.platform.core.b;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.platform.base.e;

public class c extends e {
    public c(GeoCodeOption geoCodeOption) {
        a(geoCodeOption);
    }

    private void a(GeoCodeOption geoCodeOption) {
        this.a.a("city", geoCodeOption.mCity);
        this.a.a("address", geoCodeOption.mAddress);
        this.a.a("output", "json");
        this.a.a("ret_coordtype", "bd09ll");
        this.a.a("from", "android_map_sdk");
    }

    public String a(com.baidu.platform.domain.c cVar) {
        return cVar.f();
    }
}
