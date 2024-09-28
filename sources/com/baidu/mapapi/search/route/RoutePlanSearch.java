package com.baidu.mapapi.search.route;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.core.l;
import com.baidu.platform.core.d.e;
import com.baidu.platform.core.d.j;

public class RoutePlanSearch extends l {
    private e a = new j();
    private boolean b = false;

    RoutePlanSearch() {
    }

    public static RoutePlanSearch newInstance() {
        BMapManager.init();
        return new RoutePlanSearch();
    }

    public boolean bikingSearch(BikingRoutePlanOption bikingRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (bikingRoutePlanOption == null || bikingRoutePlanOption.mTo == null || bikingRoutePlanOption.mFrom == null) {
            throw new IllegalArgumentException("BDMapSDKException: route plan option , origin or destination can not be null");
        } else if (bikingRoutePlanOption.mFrom.getLocation() == null && (bikingRoutePlanOption.mFrom.getName() == null || bikingRoutePlanOption.mFrom.getName() == "")) {
            throw new IllegalArgumentException("BDMapSDKException: route plan option , origin is illegal");
        } else if (bikingRoutePlanOption.mTo.getLocation() != null || (bikingRoutePlanOption.mTo.getName() != null && bikingRoutePlanOption.mTo.getName() != "")) {
            return this.a.a(bikingRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: route plan option , destination is illegal");
        }
    }

    public void destroy() {
        if (!this.b) {
            this.b = true;
            this.a.a();
            BMapManager.destroy();
        }
    }

    public boolean drivingSearch(DrivingRoutePlanOption drivingRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (drivingRoutePlanOption != null && drivingRoutePlanOption.mTo != null && drivingRoutePlanOption.mFrom != null) {
            return this.a.a(drivingRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: route plan option , origin or destination can not be null");
        }
    }

    public boolean masstransitSearch(MassTransitRoutePlanOption massTransitRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (massTransitRoutePlanOption == null || massTransitRoutePlanOption.mTo == null || massTransitRoutePlanOption.mFrom == null) {
            throw new IllegalArgumentException("BDMapSDKException: route plan option,origin or destination can not be null");
        } else if (massTransitRoutePlanOption.mFrom.getLocation() == null && (massTransitRoutePlanOption.mFrom.getName() == null || massTransitRoutePlanOption.mFrom.getCity() == null)) {
            throw new IllegalArgumentException("BDMapSDKException: route plan option,origin is illegal");
        } else if (massTransitRoutePlanOption.mTo.getLocation() != null || (massTransitRoutePlanOption.mTo.getName() != null && massTransitRoutePlanOption.mTo.getCity() != null)) {
            return this.a.a(massTransitRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: route plan option,destination is illegal");
        }
    }

    public void setOnGetRoutePlanResultListener(OnGetRoutePlanResultListener onGetRoutePlanResultListener) {
        e eVar = this.a;
        if (eVar == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (onGetRoutePlanResultListener != null) {
            eVar.a(onGetRoutePlanResultListener);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: listener can not be null");
        }
    }

    public boolean transitSearch(TransitRoutePlanOption transitRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (transitRoutePlanOption != null && transitRoutePlanOption.mCityName != null && transitRoutePlanOption.mTo != null && transitRoutePlanOption.mFrom != null) {
            return this.a.a(transitRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: route plan option,origin or destination or city can not be null");
        }
    }

    public boolean walkingIndoorSearch(IndoorRoutePlanOption indoorRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (indoorRoutePlanOption != null && indoorRoutePlanOption.mTo != null && indoorRoutePlanOption.mFrom != null) {
            return this.a.a(indoorRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option , origin or destination can not be null");
        }
    }

    public boolean walkingSearch(WalkingRoutePlanOption walkingRoutePlanOption) {
        if (this.a == null) {
            throw new IllegalStateException("BDMapSDKException: RoutePlanSearch is null, please call newInstance() first.");
        } else if (walkingRoutePlanOption != null && walkingRoutePlanOption.mTo != null && walkingRoutePlanOption.mFrom != null) {
            return this.a.a(walkingRoutePlanOption);
        } else {
            throw new IllegalArgumentException("BDMapSDKException: option , origin or destination can not be null");
        }
    }
}
