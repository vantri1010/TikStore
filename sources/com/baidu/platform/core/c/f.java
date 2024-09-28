package com.baidu.platform.core.c;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class f extends a implements a {
    private OnGetPoiSearchResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetPoiSearchResultListener onGetPoiSearchResultListener) {
        this.a.lock();
        this.b = onGetPoiSearchResultListener;
        this.a.unlock();
    }

    public boolean a(PoiBoundSearchOption poiBoundSearchOption) {
        g gVar = new g(poiBoundSearchOption.mPageNum, poiBoundSearchOption.mPageCapacity);
        gVar.a(SearchType.POI_IN_BOUND_SEARCH);
        return a((e) new i(poiBoundSearchOption), (Object) this.b, (d) gVar);
    }

    public boolean a(PoiCitySearchOption poiCitySearchOption) {
        g gVar = new g(poiCitySearchOption.mPageNum, poiCitySearchOption.mPageCapacity);
        gVar.a(SearchType.POI_IN_CITY_SEARCH);
        return a((e) new i(poiCitySearchOption), (Object) this.b, (d) gVar);
    }

    public boolean a(PoiDetailSearchOption poiDetailSearchOption) {
        d dVar = new d();
        if (poiDetailSearchOption != null) {
            dVar.a(poiDetailSearchOption.isSearchByUids());
        }
        dVar.a(SearchType.POI_DETAIL_SEARCH);
        return a((e) new e(poiDetailSearchOption), (Object) this.b, (d) dVar);
    }

    public boolean a(PoiIndoorOption poiIndoorOption) {
        b bVar = new b();
        bVar.a(SearchType.INDOOR_POI_SEARCH);
        return a((e) new c(poiIndoorOption), (Object) this.b, (d) bVar);
    }

    public boolean a(PoiNearbySearchOption poiNearbySearchOption) {
        g gVar = new g(poiNearbySearchOption.mPageNum, poiNearbySearchOption.mPageCapacity);
        gVar.a(SearchType.POI_NEAR_BY_SEARCH);
        return a((e) new i(poiNearbySearchOption), (Object) this.b, (d) gVar);
    }
}
