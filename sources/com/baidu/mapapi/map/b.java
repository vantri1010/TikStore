package com.baidu.mapapi.map;

import com.baidu.mapapi.map.InfoWindow;

class b implements InfoWindow.a {
    final /* synthetic */ BaiduMap a;

    b(BaiduMap baiduMap) {
        this.a = baiduMap;
    }

    public void a(InfoWindow infoWindow) {
        this.a.hideInfoWindow(infoWindow);
    }

    public void b(InfoWindow infoWindow) {
        this.a.a(infoWindow);
    }
}
