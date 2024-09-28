package com.baidu.mapapi.map;

import android.content.Context;
import android.os.Bundle;
import com.baidu.mapsdkplatform.comapi.map.ak;

class e implements ak {
    final /* synthetic */ BaiduMap a;

    e(BaiduMap baiduMap) {
        this.a = baiduMap;
    }

    public Bundle a(int i, int i2, int i3, Context context) {
        Tile a2;
        this.a.J.lock();
        try {
            if (this.a.G != null && (a2 = this.a.G.a(i, i2, i3)) != null) {
                return a2.toBundle();
            }
            this.a.J.unlock();
            return null;
        } finally {
            this.a.J.unlock();
        }
    }
}
