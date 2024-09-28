package com.baidu.platform.core.d;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapsdkplatform.comapi.util.CoordTrans;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;

public class i extends e {
    public i(MassTransitRoutePlanOption massTransitRoutePlanOption) {
        a(massTransitRoutePlanOption);
    }

    private void a(MassTransitRoutePlanOption massTransitRoutePlanOption) {
        LatLng location = massTransitRoutePlanOption.mFrom.getLocation();
        if (location != null) {
            if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                location = CoordTrans.gcjToBaidu(location);
            }
            a aVar = this.a;
            aVar.a(TtmlNode.ATTR_TTS_ORIGIN, location.latitude + "," + location.longitude);
        } else {
            this.a.a(TtmlNode.ATTR_TTS_ORIGIN, massTransitRoutePlanOption.mFrom.getName());
        }
        if (massTransitRoutePlanOption.mFrom.getCity() != null) {
            this.a.a("origin_region", massTransitRoutePlanOption.mFrom.getCity());
        }
        LatLng location2 = massTransitRoutePlanOption.mTo.getLocation();
        if (location2 != null) {
            if (SDKInitializer.getCoordType() == CoordType.GCJ02) {
                location2 = CoordTrans.gcjToBaidu(location2);
            }
            a aVar2 = this.a;
            aVar2.a("destination", location2.latitude + "," + location2.longitude);
        } else {
            this.a.a("destination", massTransitRoutePlanOption.mTo.getName());
        }
        if (massTransitRoutePlanOption.mTo.getCity() != null) {
            this.a.a("destination_region", massTransitRoutePlanOption.mTo.getCity());
        }
        a aVar3 = this.a;
        aVar3.a("tactics_incity", massTransitRoutePlanOption.mTacticsIncity.getInt() + "");
        a aVar4 = this.a;
        aVar4.a("tactics_intercity", massTransitRoutePlanOption.mTacticsIntercity.getInt() + "");
        a aVar5 = this.a;
        aVar5.a("trans_type_intercity", massTransitRoutePlanOption.mTransTypeIntercity.getInt() + "");
        a aVar6 = this.a;
        aVar6.a("page_index", massTransitRoutePlanOption.mPageIndex + "");
        a aVar7 = this.a;
        aVar7.a("page_size", massTransitRoutePlanOption.mPageSize + "");
        this.a.a("coord_type", massTransitRoutePlanOption.mCoordType);
        this.a.a("output", "json");
        this.a.a("from", "android_map_sdk");
    }

    public String a(c cVar) {
        return cVar.g();
    }
}
