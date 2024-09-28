package com.baidu.location.indoor;

import android.location.Location;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.List;

public class l {
    private List<Location> a;
    private String b;
    private Location c = null;

    l(String str, Location[] locationArr) {
        if (locationArr != null && locationArr.length > 0) {
            a(locationArr);
            this.b = str;
        }
    }

    private void a(Location[] locationArr) {
        if (locationArr != null && locationArr.length > 0) {
            if (this.a == null) {
                this.a = new ArrayList();
            }
            double d = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            double d2 = 0.0d;
            for (int i = 0; i < locationArr.length; i++) {
                d += locationArr[i].getLatitude();
                d2 += locationArr[i].getLongitude();
                this.a.add(locationArr[i]);
            }
            if (this.c == null) {
                Location location = new Location("gps");
                this.c = location;
                location.setLatitude(d / ((double) locationArr.length));
                this.c.setLongitude(d2 / ((double) locationArr.length));
            }
        }
    }

    public String a() {
        return this.b;
    }
}
