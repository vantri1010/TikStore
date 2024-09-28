package com.baidu.platform.core.b;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;

public class f extends e {
    public f(ReverseGeoCodeOption reverseGeoCodeOption) {
        a(reverseGeoCodeOption);
    }

    private void a(ReverseGeoCodeOption reverseGeoCodeOption) {
        if (reverseGeoCodeOption.getLocation() != null) {
            LatLng latLng = new LatLng(reverseGeoCodeOption.getLocation().latitude, reverseGeoCodeOption.getLocation().longitude);
            if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                latLng = CoordTrans.gcjToBaidu(latLng);
            }
            a aVar = this.a;
            aVar.a("location", latLng.latitude + "," + latLng.longitude);
        }
        this.a.a("coordtype", "bd09ll");
        this.a.a("page_index", String.valueOf(reverseGeoCodeOption.getPageNum()));
        this.a.a("page_size", String.valueOf(reverseGeoCodeOption.getPageSize()));
        this.a.a("pois", "1");
        this.a.a("output", "jsonaes");
        this.a.a("from", "android_map_sdk");
        this.a.a("latest_admin", String.valueOf(reverseGeoCodeOption.getLatestAdmin()));
        this.a.a("radius", String.valueOf(reverseGeoCodeOption.getRadius()));
    }

    public String a(c cVar) {
        return cVar.e();
    }
}
