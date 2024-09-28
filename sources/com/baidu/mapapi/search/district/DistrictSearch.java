package com.baidu.mapapi.search.district;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.core.l;
import com.baidu.platform.core.a.d;
import com.baidu.platform.core.a.e;

public class DistrictSearch extends l {
    private e a;
    private boolean b;

    DistrictSearch() {
        this.a = null;
        this.b = false;
        this.a = new d();
    }

    public static DistrictSearch newInstance() {
        BMapManager.init();
        return new DistrictSearch();
    }

    public void destroy() {
        if (!this.b) {
            this.b = true;
            this.a.a();
            BMapManager.destroy();
        }
    }

    public boolean searchDistrict(DistrictSearchOption districtSearchOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: searcher is null, please call newInstance first.");
        } else if (districtSearchOption != null && districtSearchOption.mCityName != null && !districtSearchOption.mCityName.equals("")) {
            return this.a.a(districtSearchOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option or city name can not be null or empty.");
        }
    }

    public void setOnDistrictSearchListener(OnGetDistricSearchResultListener onGetDistricSearchResultListener) {
        e eVar = this.a;
        if (eVar == null) {
            throw new IllegalStateException("BDMapSDKException: searcher is null, please call newInstance first.");
        } else if (onGetDistricSearchResultListener != null) {
            eVar.a(onGetDistricSearchResultListener);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: listener can not be null");
        }
    }
}
