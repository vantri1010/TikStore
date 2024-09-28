package com.baidu.mapapi.search.geocode;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.core.l;
import com.baidu.platform.core.b.a;
import com.baidu.platform.core.b.d;

public class GeoCoder extends l {
    private d a = new a();
    private boolean b;

    private GeoCoder() {
    }

    public static GeoCoder newInstance() {
        BMapManager.init();
        return new GeoCoder();
    }

    public void destroy() {
        if (!this.b) {
            this.b = true;
            this.a.a();
            BMapManager.destroy();
        }
    }

    public boolean geocode(GeoCodeOption geoCodeOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: GeoCoder is null, please call newInstance() first.");
        } else if (geoCodeOption != null && geoCodeOption.mAddress != null && geoCodeOption.mCity != null) {
            return this.a.a(geoCodeOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option or address or city can not be null");
        }
    }

    public boolean reverseGeoCode(ReverseGeoCodeOption reverseGeoCodeOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: GeoCoder is null, please call newInstance() first.");
        } else if (reverseGeoCodeOption != null && reverseGeoCodeOption.getLocation() != null) {
            return this.a.a(reverseGeoCodeOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option or mLocation can not be null");
        }
    }

    public void setOnGetGeoCodeResultListener(OnGetGeoCoderResultListener onGetGeoCoderResultListener) {
        d dVar = this.a;
        if (dVar == null) {
            throw new IllegalStateException("BDMapSDKException: GeoCoder is null, please call newInstance() first.");
        } else if (onGetGeoCoderResultListener != null) {
            dVar.a(onGetGeoCoderResultListener);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: listener can not be null");
        }
    }
}
