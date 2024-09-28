package com.baidu.platform.core.c;

import com.baidu.mapapi.search.poi.PoiIndoorOption;
import com.baidu.platform.base.e;
import com.baidu.platform.util.a;

public class c extends e {
    public c(PoiIndoorOption poiIndoorOption) {
        a(poiIndoorOption);
    }

    private void a(PoiIndoorOption poiIndoorOption) {
        this.a.a("qt", "indoor_s");
        this.a.a("x", "0");
        this.a.a("y", "0");
        this.a.a("from", "android_map_sdk");
        String str = poiIndoorOption.bid;
        if (str != null && !str.equals("")) {
            this.a.a("bid", str);
        }
        String str2 = poiIndoorOption.wd;
        if (str2 != null && !str2.equals("")) {
            this.a.a("wd", str2);
        }
        String str3 = poiIndoorOption.floor;
        if (str3 != null && !str3.equals("")) {
            this.a.a("floor", str3);
        }
        a aVar = this.a;
        aVar.a("current", poiIndoorOption.currentPage + "");
        a aVar2 = this.a;
        aVar2.a("pageSize", poiIndoorOption.pageSize + "");
    }

    public String a(com.baidu.platform.domain.c cVar) {
        return cVar.c();
    }
}
