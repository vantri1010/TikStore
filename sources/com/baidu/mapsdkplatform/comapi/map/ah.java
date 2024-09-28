package com.baidu.mapsdkplatform.comapi.map;

import android.graphics.Point;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comjni.map.basemap.a;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class ah {
    private a a;

    public ah(a aVar) {
        this.a = aVar;
    }

    public Point a(GeoPoint geoPoint) {
        if (geoPoint == null) {
            return null;
        }
        Point point = new Point(0, 0);
        String b = this.a.b((int) geoPoint.getLongitudeE6(), (int) geoPoint.getLatitudeE6());
        if (b != null && !b.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(b);
                point.x = jSONObject.getInt("scrx");
                point.y = jSONObject.getInt("scry");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return point;
    }

    public GeoPoint a(int i, int i2) {
        GeoPoint geoPoint = new GeoPoint(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        String a2 = this.a.a(i, i2);
        if (a2 != null && !a2.isEmpty()) {
            try {
                JSONObject jSONObject = new JSONObject(a2);
                geoPoint.setLongitudeE6((double) jSONObject.getInt("geox"));
                geoPoint.setLatitudeE6((double) jSONObject.getInt("geoy"));
                return geoPoint;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
