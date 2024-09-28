package com.baidu.mapapi.map;

import android.view.View;
import com.baidu.mapsdkplatform.comapi.map.ad;

class ac implements View.OnClickListener {
    final /* synthetic */ WearMapView a;

    ac(WearMapView wearMapView) {
        this.a = wearMapView;
    }

    public void onClick(View view) {
        ad E = this.a.f.a().E();
        E.a += 1.0f;
        this.a.f.a().a(E, 300);
    }
}
