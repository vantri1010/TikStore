package com.baidu.platform.core.busline;

import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class c extends a implements IBusLineSearch {
    OnGetBusLineSearchResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetBusLineSearchResultListener onGetBusLineSearchResultListener) {
        this.a.lock();
        this.b = onGetBusLineSearchResultListener;
        this.a.unlock();
    }

    public boolean a(BusLineSearchOption busLineSearchOption) {
        a aVar = new a();
        aVar.a(SearchType.BUS_LINE_DETAIL);
        return a((e) new b(busLineSearchOption), (Object) this.b, (d) aVar);
    }
}
