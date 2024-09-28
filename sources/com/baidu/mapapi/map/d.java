package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapsdkplatform.comapi.map.q;

class d implements q {
    final /* synthetic */ BaiduMap a;

    d(BaiduMap baiduMap) {
        this.a = baiduMap;
    }

    public Bundle a(int i, int i2, int i3) {
        Tile a2;
        this.a.I.lock();
        try {
            if (this.a.H != null && (a2 = this.a.H.a(i, i2, i3)) != null) {
                return a2.toBundle();
            }
            this.a.I.unlock();
            return null;
        } finally {
            this.a.I.unlock();
        }
    }
}
