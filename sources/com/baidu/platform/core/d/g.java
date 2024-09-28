package com.baidu.platform.core.d;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.king.zxing.util.LogUtils;

public class g extends e {
    g(IndoorRoutePlanOption indoorRoutePlanOption) {
        a(indoorRoutePlanOption);
    }

    private void a(IndoorRoutePlanOption indoorRoutePlanOption) {
        this.a.a("qt", "indoornavi");
        this.a.a("rp_format", "json");
        this.a.a("version", "1");
        GeoPoint ll2mc = CoordUtil.ll2mc(indoorRoutePlanOption.mFrom.getLocation());
        if (ll2mc != null) {
            String format = String.format("%f,%f", new Object[]{Double.valueOf(ll2mc.getLongitudeE6()), Double.valueOf(ll2mc.getLatitudeE6())});
            this.a.a("sn", (format + LogUtils.VERTICAL + indoorRoutePlanOption.mFrom.getFloor()).replaceAll(" ", ""));
        }
        GeoPoint ll2mc2 = CoordUtil.ll2mc(indoorRoutePlanOption.mTo.getLocation());
        if (ll2mc2 != null) {
            String format2 = String.format("%f,%f", new Object[]{Double.valueOf(ll2mc2.getLongitudeE6()), Double.valueOf(ll2mc2.getLatitudeE6())});
            this.a.a("en", (format2 + LogUtils.VERTICAL + indoorRoutePlanOption.mTo.getFloor()).replaceAll(" ", ""));
        }
    }

    public String a(c cVar) {
        return cVar.l();
    }
}
