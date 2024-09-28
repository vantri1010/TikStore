package com.baidu.platform.core.d;

import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.IndoorRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.platform.base.SearchType;
import com.baidu.platform.base.a;
import com.baidu.platform.base.d;
import com.baidu.platform.base.e;

public class j extends a implements e {
    private OnGetRoutePlanResultListener b = null;

    public void a() {
        this.a.lock();
        this.b = null;
        this.a.unlock();
    }

    public void a(OnGetRoutePlanResultListener onGetRoutePlanResultListener) {
        this.a.lock();
        this.b = onGetRoutePlanResultListener;
        this.a.unlock();
    }

    public boolean a(BikingRoutePlanOption bikingRoutePlanOption) {
        a aVar = new a();
        aVar.a(SearchType.BIKE_ROUTE);
        return a((e) new b(bikingRoutePlanOption), (Object) this.b, (d) aVar);
    }

    public boolean a(DrivingRoutePlanOption drivingRoutePlanOption) {
        c cVar = new c();
        cVar.a(SearchType.DRIVE_ROUTE);
        return a((e) new d(drivingRoutePlanOption), (Object) this.b, (d) cVar);
    }

    public boolean a(IndoorRoutePlanOption indoorRoutePlanOption) {
        f fVar = new f();
        fVar.a(SearchType.INDOOR_ROUTE);
        return a((e) new g(indoorRoutePlanOption), (Object) this.b, (d) fVar);
    }

    public boolean a(MassTransitRoutePlanOption massTransitRoutePlanOption) {
        h hVar = new h();
        hVar.a(SearchType.MASS_TRANSIT_ROUTE);
        return a((e) new i(massTransitRoutePlanOption), (Object) this.b, (d) hVar);
    }

    public boolean a(TransitRoutePlanOption transitRoutePlanOption) {
        m mVar = new m();
        mVar.a(SearchType.TRANSIT_ROUTE);
        return a((e) new n(transitRoutePlanOption), (Object) this.b, (d) mVar);
    }

    public boolean a(WalkingRoutePlanOption walkingRoutePlanOption) {
        o oVar = new o();
        oVar.a(SearchType.WALK_ROUTE);
        return a((e) new p(walkingRoutePlanOption), (Object) this.b, (d) oVar);
    }
}
