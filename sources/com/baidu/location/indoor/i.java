package com.baidu.location.indoor;

import com.baidu.location.BDLocation;
import com.baidu.location.indoor.g;
import com.baidu.location.indoor.m;
import com.baidu.location.indoor.mapversion.b.a;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.Date;

class i implements m.a {
    final /* synthetic */ g a;

    i(g gVar) {
        this.a = gVar;
    }

    public synchronized void a(double d, double d2, double d3, long j) {
        synchronized (this) {
            if (this.a.n) {
                double unused = this.a.J = 0.4d;
                this.a.ae.a(d, d2, d3, j);
                double[] a2 = a.a(this.a.w, d, d2, d3);
                if (a2 != null) {
                    if (a2[0] != -1.0d) {
                        if (a2[0] == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                            double unused2 = this.a.I = a2[2];
                            double unused3 = this.a.H = a2[1];
                            if (this.a.M.size() > 50) {
                                this.a.M.clear();
                            }
                            this.a.M.add(new g.h(this.a.j.d(), d, d3, d2));
                            g.g(this.a);
                            try {
                                BDLocation bDLocation = new BDLocation();
                                bDLocation.setLocType(BDLocation.TypeNetWorkLocation);
                                bDLocation.setLatitude(a2[2]);
                                bDLocation.setLongitude(a2[1]);
                                bDLocation.setDirection((float) d3);
                                bDLocation.setTime(this.a.b.format(new Date()));
                                bDLocation.setFloor(this.a.w);
                                bDLocation.setBuildingID(this.a.x);
                                bDLocation.setBuildingName(this.a.z);
                                bDLocation.setParkAvailable(this.a.C);
                                bDLocation.setIndoorLocMode(true);
                                if (this.a.T) {
                                    bDLocation.setRadius(8.0f);
                                } else {
                                    bDLocation.setRadius(15.0f);
                                }
                                bDLocation.setFusionLocInfo("res", a2);
                                bDLocation.setRadius((float) a2[5]);
                                bDLocation.setDirection((float) a2[6]);
                                bDLocation.setSpeed((float) a2[8]);
                                bDLocation.setNetworkLocationType("dr");
                                BDLocation bDLocation2 = new BDLocation(bDLocation);
                                bDLocation2.setNetworkLocationType("dr2");
                                if (this.a.U == null || !this.a.U.c()) {
                                    this.a.a(bDLocation2, 21);
                                } else {
                                    this.a.U.a(bDLocation2);
                                }
                                if (!this.a.ae.a(bDLocation, a2[5], "dr")) {
                                    this.a.d();
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        }
    }
}
