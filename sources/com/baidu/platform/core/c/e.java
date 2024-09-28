package com.baidu.platform.core.c;

import android.util.Log;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.platform.domain.c;

public class e extends com.baidu.platform.base.e {
    e(PoiDetailSearchOption poiDetailSearchOption) {
        a(poiDetailSearchOption);
    }

    private void a(PoiDetailSearchOption poiDetailSearchOption) {
        if (poiDetailSearchOption == null) {
            Log.e(e.class.getSimpleName(), "Option is null");
            return;
        }
        if (!poiDetailSearchOption.isSearchByUids()) {
            poiDetailSearchOption.poiUids(poiDetailSearchOption.getUid());
        }
        this.a.a("uids", poiDetailSearchOption.getUids());
        this.a.a("output", "json");
        this.a.a("scope", "2");
    }

    public String a(c cVar) {
        return cVar.b();
    }
}
