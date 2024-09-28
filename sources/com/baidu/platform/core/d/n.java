package com.baidu.platform.core.d;

import androidx.exifinterface.media.ExifInterface;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;

public class n extends e {
    public n(TransitRoutePlanOption transitRoutePlanOption) {
        a(transitRoutePlanOption);
    }

    private void a(TransitRoutePlanOption transitRoutePlanOption) {
        this.a.a("qt", "bus");
        a aVar = this.a;
        aVar.a("sy", transitRoutePlanOption.mPolicy.getInt() + "");
        this.a.a("ie", "utf-8");
        this.a.a("lrn", "20");
        this.a.a("version", ExifInterface.GPS_MEASUREMENT_3D);
        this.a.a("rp_format", "json");
        this.a.a("rp_filter", "mobile");
        this.a.a("ic_info", "2");
        this.a.a("exptype", "depall");
        this.a.a("sn", a(transitRoutePlanOption.mFrom));
        this.a.a("en", a(transitRoutePlanOption.mTo));
        if (transitRoutePlanOption.mCityName != null) {
            this.a.a("c", transitRoutePlanOption.mCityName);
        }
        if (TransitRoutePlanOption.TransitPolicy.EBUS_NO_SUBWAY == transitRoutePlanOption.mPolicy) {
            this.a.a("f", "[0,2,4,7,5,8,9,10,11]");
        }
    }

    public String a(c cVar) {
        return cVar.h();
    }
}
