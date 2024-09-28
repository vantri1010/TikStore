package com.baidu.platform.core.b;

import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class a extends com.baidu.platform.base.a implements d {
    OnGetGeoCoderResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetGeoCoderResultListener onGetGeoCoderResultListener) {
        this.a.lock();
        this.b = onGetGeoCoderResultListener;
        this.a.unlock();
    }

    public boolean a(GeoCodeOption geoCodeOption) {
        b bVar = new b();
        c cVar = new c(geoCodeOption);
        bVar.a(SearchType.GEO_CODER);
        if (geoCodeOption != null) {
            bVar.b(geoCodeOption.getAddress());
        }
        return a((e) cVar, (Object) this.b, (d) bVar);
    }

    public boolean a(ReverseGeoCodeOption reverseGeoCodeOption) {
        e eVar = new e();
        f fVar = new f(reverseGeoCodeOption);
        eVar.a(SearchType.REVERSE_GEO_CODER);
        return a((e) fVar, (Object) this.b, (d) eVar);
    }
}
