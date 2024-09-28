package com.baidu.mapapi.map.offline;

import com.baidu.mapsdkplatform.comapi.map.x;
import java.util.ArrayList;

class a implements x {
    final /* synthetic */ MKOfflineMap a;

    a(MKOfflineMap mKOfflineMap) {
        this.a = mKOfflineMap;
    }

    public void a(int i, int i2) {
        MKOfflineMapListener mKOfflineMapListener;
        if (i != 4) {
            int i3 = 6;
            if (i == 6) {
                mKOfflineMapListener = this.a.c;
            } else if (i == 8) {
                this.a.c.onGetOfflineMapState(0, i2 >> 8);
                return;
            } else if (i == 10) {
                mKOfflineMapListener = this.a.c;
                i3 = 2;
            } else if (i == 12) {
                this.a.b.a(true, false);
                return;
            } else {
                return;
            }
            mKOfflineMapListener.onGetOfflineMapState(i3, i2);
            return;
        }
        ArrayList<MKOLUpdateElement> allUpdateInfo = this.a.getAllUpdateInfo();
        if (allUpdateInfo != null) {
            for (MKOLUpdateElement next : allUpdateInfo) {
                if (next.update) {
                    this.a.c.onGetOfflineMapState(4, next.cityID);
                }
            }
        }
    }
}
