package com.baidu.platform.core.d;

import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.king.zxing.util.LogUtils;
import java.util.List;

public class d extends e {
    d(DrivingRoutePlanOption drivingRoutePlanOption) {
        a(drivingRoutePlanOption);
    }

    private void a(DrivingRoutePlanOption drivingRoutePlanOption) {
        this.a.a("qt", "cars");
        this.a.a("sy", drivingRoutePlanOption.mPolicy.getInt() + "");
        this.a.a("ie", "utf-8");
        this.a.a("lrn", "20");
        this.a.a("version", "6");
        this.a.a("extinfo", "32");
        this.a.a("mrs", "1");
        this.a.a("rp_format", "json");
        this.a.a("rp_filter", "mobile");
        this.a.a("route_traffic", drivingRoutePlanOption.mtrafficPolicy.getInt() + "");
        this.a.a("sn", a(drivingRoutePlanOption.mFrom));
        this.a.a("en", a(drivingRoutePlanOption.mTo));
        if (drivingRoutePlanOption.mCityName != null) {
            this.a.a("c", drivingRoutePlanOption.mCityName);
        }
        if (drivingRoutePlanOption.mFrom != null) {
            this.a.a("sc", drivingRoutePlanOption.mFrom.getCity());
        }
        if (drivingRoutePlanOption.mTo != null) {
            this.a.a("ec", drivingRoutePlanOption.mTo.getCity());
        }
        List<PlanNode> list = drivingRoutePlanOption.mWayPoints;
        String str = new String();
        String str2 = new String();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                PlanNode planNode = list.get(i);
                if (planNode != null) {
                    str = str + a(planNode);
                    str2 = str2 + planNode.getCity();
                    if (i != list.size() - 1) {
                        str2 = str2 + LogUtils.VERTICAL;
                        str = str + LogUtils.VERTICAL;
                    }
                }
            }
            this.a.a("wp", str);
            this.a.a("wpc", str2);
        }
    }

    public String a(c cVar) {
        return cVar.i();
    }
}
