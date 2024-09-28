package com.baidu.platform.core.e;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.Point;
import com.baidu.mapapi.search.share.RouteShareURLOption;
import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;
import com.google.android.gms.common.internal.ImagesContract;

public class e extends com.baidu.platform.base.e {
    public e(RouteShareURLOption routeShareURLOption) {
        a(routeShareURLOption);
    }

    private int a(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void a(RouteShareURLOption routeShareURLOption) {
        String str;
        String str2;
        a aVar = new a();
        Point ll2point = CoordUtil.ll2point(routeShareURLOption.mFrom.getLocation());
        Point ll2point2 = CoordUtil.ll2point(routeShareURLOption.mTo.getLocation());
        String str3 = "2$$$$$$";
        if (ll2point != null) {
            str = "1$$$$" + ll2point.x + "," + ll2point.y + "$$";
        } else {
            str = str3;
        }
        String name = routeShareURLOption.mFrom.getName();
        String str4 = "";
        if (name == null || name.equals(str4)) {
            name = "起点";
        }
        String str5 = str + name + "$$0$$$$";
        if (ll2point2 != null) {
            str3 = "1$$$$" + ll2point2.x + "," + ll2point2.y + "$$";
        }
        String name2 = routeShareURLOption.mTo.getName();
        if (name2 == null || name2.equals(str4)) {
            name2 = "终点";
        }
        String str6 = str3 + name2 + "$$0$$$$";
        int ordinal = routeShareURLOption.mMode.ordinal();
        if (ordinal == 0) {
            aVar.a("sc", a(routeShareURLOption.mFrom.getCity()) + str4);
            aVar.a("ec", a(routeShareURLOption.mTo.getCity()) + str4);
            str4 = "&sharecallbackflag=carRoute";
            str2 = "nav";
        } else if (ordinal == 1) {
            aVar.a("sc", a(routeShareURLOption.mFrom.getCity()) + str4);
            aVar.a("ec", a(routeShareURLOption.mTo.getCity()) + str4);
            str4 = "&sharecallbackflag=footRoute";
            str2 = "walk";
        } else if (ordinal == 2) {
            aVar.a("sc", a(routeShareURLOption.mFrom.getCity()) + str4);
            aVar.a("ec", a(routeShareURLOption.mTo.getCity()) + str4);
            str4 = "&sharecallbackflag=cycleRoute";
            str2 = "cycle";
        } else if (ordinal != 3) {
            str2 = str4;
        } else {
            String str7 = "&i=" + routeShareURLOption.mPn + ",1,1&sharecallbackflag=busRoute";
            aVar.a("c", routeShareURLOption.mCityCode + str4);
            str2 = "bt";
            str4 = str7;
        }
        aVar.a("sn", str5);
        aVar.a("en", str6);
        String str8 = "&" + aVar.a() + ("&start=" + name + "&end=" + name2);
        this.a.a(ImagesContract.URL, "http://map.baidu.com/?newmap=1&s=" + str2 + (AppMD5.encodeUrlParamsValue(str8) + str4));
        this.a.a("from", "android_map_sdk");
    }

    public String a(c cVar) {
        return cVar.r();
    }
}
