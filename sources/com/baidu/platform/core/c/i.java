package com.baidu.platform.core.c;

import android.text.TextUtils;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;
import com.google.android.gms.actions.SearchIntents;

public class i extends e {
    i(PoiBoundSearchOption poiBoundSearchOption) {
        a(poiBoundSearchOption);
    }

    i(PoiCitySearchOption poiCitySearchOption) {
        a(poiCitySearchOption);
    }

    i(PoiNearbySearchOption poiNearbySearchOption) {
        a(poiNearbySearchOption);
    }

    private void a(PoiBoundSearchOption poiBoundSearchOption) {
        this.a.a(SearchIntents.EXTRA_QUERY, poiBoundSearchOption.mKeyword);
        this.a.a("tag", poiBoundSearchOption.mTag);
        a aVar = this.a;
        aVar.a("bounds", poiBoundSearchOption.mBound.southwest.latitude + "," + poiBoundSearchOption.mBound.southwest.longitude + "," + poiBoundSearchOption.mBound.northeast.latitude + "," + poiBoundSearchOption.mBound.northeast.longitude);
        this.a.a("output", "json");
        a aVar2 = this.a;
        StringBuilder sb = new StringBuilder();
        sb.append(poiBoundSearchOption.mScope);
        sb.append("");
        aVar2.a("scope", sb.toString());
        a aVar3 = this.a;
        aVar3.a("page_num", poiBoundSearchOption.mPageNum + "");
        a aVar4 = this.a;
        aVar4.a("page_size", poiBoundSearchOption.mPageCapacity + "");
        if (poiBoundSearchOption.mScope == 2 && poiBoundSearchOption.mPoiFilter != null && !TextUtils.isEmpty(poiBoundSearchOption.mPoiFilter.toString())) {
            this.a.a("filter", poiBoundSearchOption.mPoiFilter.toString());
        }
    }

    private void a(PoiCitySearchOption poiCitySearchOption) {
        String str;
        a aVar;
        this.a.a(SearchIntents.EXTRA_QUERY, poiCitySearchOption.mKeyword);
        this.a.a(TtmlNode.TAG_REGION, poiCitySearchOption.mCity);
        this.a.a("output", "json");
        a aVar2 = this.a;
        aVar2.a("page_num", poiCitySearchOption.mPageNum + "");
        a aVar3 = this.a;
        aVar3.a("page_size", poiCitySearchOption.mPageCapacity + "");
        a aVar4 = this.a;
        aVar4.a("scope", poiCitySearchOption.mScope + "");
        this.a.a("tag", poiCitySearchOption.mTag);
        if (poiCitySearchOption.mIsCityLimit) {
            aVar = this.a;
            str = "true";
        } else {
            aVar = this.a;
            str = "false";
        }
        aVar.a("city_limit", str);
        if (poiCitySearchOption.mScope == 2 && poiCitySearchOption.mPoiFilter != null && !TextUtils.isEmpty(poiCitySearchOption.mPoiFilter.toString())) {
            this.a.a("filter", poiCitySearchOption.mPoiFilter.toString());
        }
    }

    private void a(PoiNearbySearchOption poiNearbySearchOption) {
        String str;
        a aVar;
        this.a.a(SearchIntents.EXTRA_QUERY, poiNearbySearchOption.mKeyword);
        a aVar2 = this.a;
        aVar2.a("location", poiNearbySearchOption.mLocation.latitude + "," + poiNearbySearchOption.mLocation.longitude);
        a aVar3 = this.a;
        aVar3.a("radius", poiNearbySearchOption.mRadius + "");
        this.a.a("output", "json");
        a aVar4 = this.a;
        aVar4.a("page_num", poiNearbySearchOption.mPageNum + "");
        a aVar5 = this.a;
        aVar5.a("page_size", poiNearbySearchOption.mPageCapacity + "");
        a aVar6 = this.a;
        aVar6.a("scope", poiNearbySearchOption.mScope + "");
        this.a.a("tag", poiNearbySearchOption.mTag);
        if (poiNearbySearchOption.mRadiusLimit) {
            aVar = this.a;
            str = "true";
        } else {
            aVar = this.a;
            str = "false";
        }
        aVar.a("radius_limit", str);
        if (poiNearbySearchOption.mScope == 2 && poiNearbySearchOption.mPoiFilter != null && !TextUtils.isEmpty(poiNearbySearchOption.mPoiFilter.toString())) {
            this.a.a("filter", poiNearbySearchOption.mPoiFilter.toString());
        }
    }

    public String a(c cVar) {
        return cVar.a();
    }
}
