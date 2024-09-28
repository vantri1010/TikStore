package com.baidu.mapapi.search.share;

import com.baidu.mapapi.search.route.PlanNode;

public class RouteShareURLOption {
    public int mCityCode = -1;
    public PlanNode mFrom = null;
    public RouteShareMode mMode;
    public int mPn = 0;
    public PlanNode mTo = null;

    public enum RouteShareMode {
        CAR_ROUTE_SHARE_MODE(0),
        FOOT_ROUTE_SHARE_MODE(1),
        CYCLE_ROUTE_SHARE_MODE(2),
        BUS_ROUTE_SHARE_MODE(3);
        
        private int a;

        private RouteShareMode(int i) {
            this.a = -1;
            this.a = i;
        }

        public int getRouteShareMode() {
            return this.a;
        }
    }

    public RouteShareURLOption cityCode(int i) {
        this.mCityCode = i;
        return this;
    }

    public RouteShareURLOption from(PlanNode planNode) {
        this.mFrom = planNode;
        return this;
    }

    public RouteShareMode getmMode() {
        return this.mMode;
    }

    public RouteShareURLOption pn(int i) {
        this.mPn = i;
        return this;
    }

    public RouteShareURLOption routMode(RouteShareMode routeShareMode) {
        this.mMode = routeShareMode;
        return this;
    }

    public RouteShareURLOption to(PlanNode planNode) {
        this.mTo = planNode;
        return this;
    }
}
