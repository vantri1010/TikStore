package com.baidu.mapapi.map;

import android.view.View;
import com.baidu.mapsdkplatform.comapi.map.ad;

class x implements View.OnClickListener {
    final /* synthetic */ TextureMapView a;

    x(TextureMapView textureMapView) {
        this.a = textureMapView;
    }

    public void onClick(View view) {
        float f = this.a.b.b().a;
        ad E = this.a.b.b().E();
        E.a += 1.0f;
        if (E.a <= f) {
            f = E.a;
        }
        E.a = f;
        BaiduMap.mapStatusReason |= 16;
        this.a.b.b().a(E, 300);
    }
}
