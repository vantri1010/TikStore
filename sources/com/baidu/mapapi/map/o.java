package com.baidu.mapapi.map;

import android.view.View;
import com.baidu.mapsdkplatform.comapi.map.ad;

class o implements View.OnClickListener {
    final /* synthetic */ MapView a;

    o(MapView mapView) {
        this.a = mapView;
    }

    public void onClick(View view) {
        float f = this.a.e.a().a;
        ad E = this.a.e.a().E();
        E.a += 1.0f;
        if (E.a <= f) {
            f = E.a;
        }
        E.a = f;
        BaiduMap.mapStatusReason |= 16;
        this.a.e.a().a(E, 300);
    }
}
