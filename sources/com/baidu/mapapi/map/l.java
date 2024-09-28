package com.baidu.mapapi.map;

import android.text.TextUtils;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapsdkplatform.comapi.map.h;

class l implements h.a {
    final /* synthetic */ MapView.CustomMapStyleCallBack a;
    final /* synthetic */ MapCustomStyleOptions b;
    final /* synthetic */ MapView c;

    l(MapView mapView, MapView.CustomMapStyleCallBack customMapStyleCallBack, MapCustomStyleOptions mapCustomStyleOptions) {
        this.c = mapView;
        this.a = customMapStyleCallBack;
        this.b = mapCustomStyleOptions;
    }

    public void a(int i, String str, String str2) {
        MapView.CustomMapStyleCallBack customMapStyleCallBack = this.a;
        if (customMapStyleCallBack == null || !customMapStyleCallBack.onCustomMapStyleLoadFailed(i, str, str2)) {
            if (!TextUtils.isEmpty(str2)) {
                this.c.a(str2, 1);
            } else {
                String localCustomStyleFilePath = this.b.getLocalCustomStyleFilePath();
                if (!TextUtils.isEmpty(localCustomStyleFilePath)) {
                    this.c.a(localCustomStyleFilePath, 0);
                } else {
                    return;
                }
            }
            this.c.setMapCustomStyleEnable(true);
        }
    }

    public void a(String str) {
        MapView.CustomMapStyleCallBack customMapStyleCallBack = this.a;
        if ((customMapStyleCallBack == null || !customMapStyleCallBack.onPreLoadLastCustomMapStyle(str)) && !TextUtils.isEmpty(str)) {
            this.c.a(str, 1);
            this.c.setMapCustomStyleEnable(true);
        }
    }

    public void a(boolean z, String str) {
        MapView.CustomMapStyleCallBack customMapStyleCallBack = this.a;
        if ((customMapStyleCallBack == null || !customMapStyleCallBack.onCustomMapStyleLoadSuccess(z, str)) && z && !TextUtils.isEmpty(str)) {
            this.c.a(str, 1);
            this.c.setMapCustomStyleEnable(true);
        }
    }
}
