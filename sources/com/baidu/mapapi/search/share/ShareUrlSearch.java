package com.baidu.mapapi.search.share;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.core.l;
import com.baidu.mapapi.search.share.RouteShareURLOption;
import com.baidu.platform.core.e.a;
import com.baidu.platform.core.e.h;

public class ShareUrlSearch extends l {
    a a = new h();
    private boolean b = false;

    ShareUrlSearch() {
    }

    private boolean a(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ShareUrlSearch newInstance() {
        BMapManager.init();
        return new ShareUrlSearch();
    }

    public void destroy() {
        if (!this.b) {
            this.b = true;
            this.a.a();
            BMapManager.destroy();
        }
    }

    public boolean requestLocationShareUrl(LocationShareURLOption locationShareURLOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: searcher has been destroyed");
        } else if (locationShareURLOption != null && locationShareURLOption.mLocation != null && locationShareURLOption.mName != null && locationShareURLOption.mSnippet != null) {
            return this.a.a(locationShareURLOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option or name or snippet  can not be null");
        }
    }

    public boolean requestPoiDetailShareUrl(PoiDetailShareURLOption poiDetailShareURLOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: searcher has been destroyed");
        } else if (poiDetailShareURLOption != null && poiDetailShareURLOption.mUid != null) {
            return this.a.a(poiDetailShareURLOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option or uid can not be null");
        }
    }

    public boolean requestRouteShareUrl(RouteShareURLOption routeShareURLOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: searcher has been destroyed");
        } else if (routeShareURLOption == null) {
            throw new IllegalArgumentException("BDMapSDKException: option is null");
        } else if (routeShareURLOption.getmMode().ordinal() < 0) {
            return false;
        } else {
            if (routeShareURLOption.mFrom == null || routeShareURLOption.mTo == null) {
                throw new IllegalArgumentException("BDMapSDKException: start or end point can not be null");
            }
            if (routeShareURLOption.mMode == RouteShareURLOption.RouteShareMode.BUS_ROUTE_SHARE_MODE) {
                if ((routeShareURLOption.mFrom.getLocation() == null || routeShareURLOption.mTo.getLocation() == null) && routeShareURLOption.mCityCode < 0) {
                    throw new IllegalArgumentException("BDMapSDKException: city code can not be null if don't set start or end point");
                }
            } else if (routeShareURLOption.mFrom.getLocation() == null && !a(routeShareURLOption.mFrom.getCity())) {
                throw new IllegalArgumentException("BDMapSDKException: start cityCode must be set if not set start location");
            } else if (routeShareURLOption.mTo.getLocation() == null && !a(routeShareURLOption.mTo.getCity())) {
                throw new IllegalArgumentException("BDMapSDKException: end cityCode must be set if not set end location");
            }
            return this.a.a(routeShareURLOption);
        }
    }

    public void setOnGetShareUrlResultListener(OnGetShareUrlResultListener onGetShareUrlResultListener) {
        a aVar = this.a;
        if (aVar == null) {
            throw new IllegalStateException("BDMapSDKException: searcher has been destroyed");
        } else if (onGetShareUrlResultListener != null) {
            aVar.a(onGetShareUrlResultListener);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: listener can not be null");
        }
    }
}
