package com.baidu.platform.core.a;

import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;

public class a extends e {
    a(DistrictSearchOption districtSearchOption) {
        a(districtSearchOption);
    }

    private void a(DistrictSearchOption districtSearchOption) {
        String str;
        com.baidu.platform.util.a aVar;
        if (districtSearchOption != null) {
            this.a.a("qt", "con");
            this.a.a("rp_format", "json");
            this.a.a("rp_filter", "mobile");
            this.a.a("area_res", "true");
            this.a.a("addr_identify", "1");
            this.a.a("ie", "utf-8");
            this.a.a("pn", "0");
            this.a.a("rn", "10");
            this.a.a("c", districtSearchOption.mCityName);
            if (districtSearchOption.mDistrictName == null || districtSearchOption.mDistrictName.equals("")) {
                aVar = this.a;
                str = districtSearchOption.mCityName;
            } else {
                aVar = this.a;
                str = districtSearchOption.mDistrictName;
            }
            aVar.a("wd", str);
        }
    }

    public String a(c cVar) {
        return cVar.n();
    }
}
