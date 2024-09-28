package com.baidu.platform.core.a;

import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.district.OnGetDistricSearchResultListener;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.e;

public class d extends a implements e {
    private OnGetDistricSearchResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetDistricSearchResultListener onGetDistricSearchResultListener) {
        this.a.lock();
        this.b = onGetDistricSearchResultListener;
        this.a.unlock();
    }

    public boolean a(DistrictSearchOption districtSearchOption) {
        b bVar = new b();
        bVar.a(SearchType.DISTRICT_SEARCH);
        return a((e) new a(districtSearchOption), (Object) this.b, (com.baidu.platform.base.d) bVar);
    }
}
