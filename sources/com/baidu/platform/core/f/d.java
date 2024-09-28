package com.baidu.platform.core.f;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;

public class d extends e {
    public d(SuggestionSearchOption suggestionSearchOption) {
        a(suggestionSearchOption);
    }

    private void a(SuggestionSearchOption suggestionSearchOption) {
        a aVar;
        String str;
        this.a.a("q", suggestionSearchOption.mKeyword);
        this.a.a(TtmlNode.TAG_REGION, suggestionSearchOption.mCity);
        if (suggestionSearchOption.mLocation != null) {
            LatLng latLng = new LatLng(suggestionSearchOption.mLocation.latitude, suggestionSearchOption.mLocation.longitude);
            if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                latLng = CoordTrans.gcjToBaidu(latLng);
            }
            a aVar2 = this.a;
            aVar2.a("location", latLng.latitude + "," + latLng.longitude);
        }
        if (suggestionSearchOption.mCityLimit.booleanValue()) {
            aVar = this.a;
            str = "true";
        } else {
            aVar = this.a;
            str = "false";
        }
        aVar.a("city_limit", str);
        this.a.a("from", "android_map_sdk");
        this.a.a("output", "json");
    }

    public String a(c cVar) {
        return cVar.d();
    }
}
