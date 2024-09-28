package com.baidu.platform.core.e;

import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.PoiDetailShareURLOption;
import com.baidu.mapapi.search.share.RouteShareURLOption;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class h extends a implements a {
    OnGetShareUrlResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetShareUrlResultListener onGetShareUrlResultListener) {
        this.a.lock();
        this.b = onGetShareUrlResultListener;
        this.a.unlock();
    }

    public boolean a(LocationShareURLOption locationShareURLOption) {
        f fVar = new f();
        fVar.a(SearchType.LOCATION_SEARCH_SHARE);
        return a((e) new b(locationShareURLOption), (Object) this.b, (d) fVar);
    }

    public boolean a(PoiDetailShareURLOption poiDetailShareURLOption) {
        f fVar = new f();
        fVar.a(SearchType.POI_DETAIL_SHARE);
        return a((e) new c(poiDetailShareURLOption), (Object) this.b, (d) fVar);
    }

    public boolean a(RouteShareURLOption routeShareURLOption) {
        d dVar = new d();
        dVar.a(SearchType.ROUTE_PLAN_SHARE);
        return a((e) new e(routeShareURLOption), (Object) this.b, (d) dVar);
    }
}
