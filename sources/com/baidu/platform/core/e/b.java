package com.baidu.platform.core.e;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.Point;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.platform.base.e;
import com.baidu.platform.domain.c;
import com.baidu.platform.util.a;
import com.king.zxing.util.LogUtils;

public class b extends e {
    public b(LocationShareURLOption locationShareURLOption) {
        a(locationShareURLOption);
    }

    private void a(LocationShareURLOption locationShareURLOption) {
        this.a.a("qt", "cs");
        Point ll2point = CoordUtil.ll2point(locationShareURLOption.mLocation);
        a aVar = this.a;
        aVar.a("geo", ll2point.x + LogUtils.VERTICAL + ll2point.y);
        this.a.a("t", locationShareURLOption.mName);
        this.a.a("cnt", locationShareURLOption.mSnippet);
        b(false);
        a(false);
    }

    public String a(c cVar) {
        return cVar.q();
    }
}
