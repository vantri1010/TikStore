package com.baidu.location.indoor;

import com.baidu.location.BDLocation;
import com.baidu.location.e.f;
import com.baidu.location.indoor.p;
import com.zhy.http.okhttp.OkHttpUtils;

class h implements p.a {
    final /* synthetic */ g a;

    h(g gVar) {
        this.a = gVar;
    }

    public void a(BDLocation bDLocation) {
        String g;
        if (this.a.f()) {
            if (this.a.ae != null && System.currentTimeMillis() - this.a.ae.c > 20000 && System.currentTimeMillis() - this.a.ae.e < OkHttpUtils.DEFAULT_MILLISECONDS) {
                bDLocation.setLocType(61);
                bDLocation.setFloor((String) null);
                bDLocation.setBuildingID((String) null);
                bDLocation.setBuildingName((String) null);
            }
            BDLocation bDLocation2 = new BDLocation(bDLocation);
            if (f.a().j() && (g = f.a().g()) != null) {
                BDLocation bDLocation3 = new BDLocation(g);
                bDLocation2.setLocType(61);
                bDLocation2.setSatelliteNumber(bDLocation3.getSatelliteNumber());
                bDLocation2.setSpeed(bDLocation3.getSpeed());
                bDLocation2.setAltitude(bDLocation3.getAltitude());
                bDLocation2.setDirection(bDLocation3.getDirection());
            }
            this.a.a(bDLocation2, 29);
            this.a.af.a(bDLocation);
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (this.a.ae != null && currentTimeMillis - this.a.ae.c > 30000 && currentTimeMillis - this.a.ae.e > 30000) {
            this.a.d();
        }
    }
}
