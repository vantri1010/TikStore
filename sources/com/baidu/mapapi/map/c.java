package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.baidu.mapsdkplatform.comapi.map.n;
import java.util.Iterator;
import java.util.Set;
import javax.microedition.khronos.opengles.GL10;
import org.json.JSONException;
import org.json.JSONObject;

class c implements n {
    final /* synthetic */ BaiduMap a;

    c(BaiduMap baiduMap) {
        this.a = baiduMap;
    }

    public void a() {
    }

    public void a(Bitmap bitmap) {
        if (this.a.B != null) {
            this.a.B.onSnapshotReady(bitmap);
        }
    }

    public void a(MotionEvent motionEvent) {
        if (this.a.r != null) {
            this.a.r.onTouch(motionEvent);
        }
    }

    public void a(GeoPoint geoPoint) {
        if (this.a.s != null) {
            this.a.s.onMapClick(CoordUtil.mc2ll(geoPoint));
        }
    }

    public void a(ad adVar) {
        if (!this.a.K.values().isEmpty()) {
            for (InfoWindow infoWindow : this.a.K.values()) {
                if (infoWindow.b != null && !infoWindow.i) {
                    infoWindow.b.setVisibility(4);
                }
            }
        }
        int i = (BaiduMap.mapStatusReason & 256) == 256 ? 3 : (BaiduMap.mapStatusReason & 16) == 16 ? 2 : 1;
        if (this.a.q != null) {
            MapStatus a2 = MapStatus.a(adVar);
            this.a.q.onMapStatusChangeStart(a2);
            this.a.q.onMapStatusChangeStart(a2, i);
        }
        if (this.a.F != null) {
            this.a.F.onMapStatusChangeReason(i);
        }
        BaiduMap.mapStatusReason = 0;
    }

    public void a(String str) {
        ad E;
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject optJSONObject = jSONObject.optJSONArray("dataset").optJSONObject(0);
            GeoPoint b = this.a.i.b(jSONObject.optInt("px"), jSONObject.optInt("py"));
            int optInt = optJSONObject.optInt("ty");
            if (optInt != 17) {
                if (optInt == 18) {
                    if (this.a.A != null) {
                        this.a.A.onMyLocationClick();
                        return;
                    }
                } else if (optInt == 19) {
                    if (this.a.i != null && (E = this.a.i.E()) != null) {
                        E.c = 0;
                        E.b = 0;
                        BaiduMap.mapStatusReason |= 16;
                        this.a.i.a(E, 300);
                        return;
                    }
                    return;
                } else if (optInt == 90909) {
                    String optString = optJSONObject.optString("marker_id");
                    Set<String> keySet = this.a.K.keySet();
                    if (keySet.isEmpty() || !keySet.contains(optString)) {
                        for (Overlay overlay : this.a.k) {
                            if ((overlay instanceof Marker) && overlay.z.equals(optString)) {
                                if (!this.a.x.isEmpty()) {
                                    Iterator it = this.a.x.iterator();
                                    while (it.hasNext()) {
                                        ((BaiduMap.OnMarkerClickListener) it.next()).onMarkerClick((Marker) overlay);
                                    }
                                    return;
                                }
                                a(b);
                            }
                        }
                        return;
                    }
                    for (String str2 : keySet) {
                        if (str2 != null && str2.equals(optString)) {
                            InfoWindow infoWindow = (InfoWindow) this.a.K.get(str2);
                            if (!(infoWindow == null || infoWindow.d == null)) {
                                infoWindow.d.onInfoWindowClick();
                                return;
                            }
                        }
                    }
                    return;
                } else if (optInt == 90910) {
                    String optString2 = optJSONObject.optString("polyline_id");
                    for (Overlay overlay2 : this.a.k) {
                        if ((overlay2 instanceof Polyline) && overlay2.z.equals(optString2)) {
                            if (!this.a.y.isEmpty()) {
                                Iterator it2 = this.a.y.iterator();
                                while (it2.hasNext()) {
                                    ((BaiduMap.OnPolylineClickListener) it2.next()).onPolylineClick((Polyline) overlay2);
                                }
                            } else {
                                a(b);
                            }
                        }
                    }
                    return;
                } else {
                    return;
                }
                a(b);
            } else if (this.a.s != null) {
                MapPoi mapPoi = new MapPoi();
                mapPoi.a(optJSONObject);
                this.a.s.onMapPoiClick(mapPoi);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void a(GL10 gl10, ad adVar) {
        if (this.a.C != null) {
            this.a.C.onMapDrawFrame(MapStatus.a(adVar));
        }
    }

    public void a(boolean z) {
        if (this.a.D != null) {
            this.a.D.onBaseIndoorMapMode(z, this.a.getFocusedBaseIndoorMapInfo());
        }
    }

    public void a(boolean z, int i) {
        if (this.a.E != null) {
            this.a.E.onMapRenderValidData(z, i, this.a.a(i));
        }
    }

    public void b() {
        BaiduMap baiduMap = this.a;
        Projection unused = baiduMap.f = new Projection(baiduMap.i);
        boolean unused2 = this.a.S = true;
        if (this.a.t != null) {
            this.a.t.onMapLoaded();
        }
    }

    public void b(GeoPoint geoPoint) {
        if (this.a.v != null) {
            this.a.v.onMapDoubleClick(CoordUtil.mc2ll(geoPoint));
        }
    }

    public void b(ad adVar) {
        if (this.a.q != null) {
            this.a.q.onMapStatusChange(MapStatus.a(adVar));
        }
    }

    public boolean b(String str) {
        try {
            JSONObject optJSONObject = new JSONObject(str).optJSONArray("dataset").optJSONObject(0);
            if (optJSONObject.optInt("ty") != 90909) {
                return false;
            }
            String optString = optJSONObject.optString("marker_id");
            Set keySet = this.a.K.keySet();
            if (!keySet.isEmpty() && keySet.contains(optString)) {
                return false;
            }
            for (Overlay overlay : this.a.k) {
                if ((overlay instanceof Marker) && overlay.z.equals(optString)) {
                    Marker marker = (Marker) overlay;
                    if (!marker.f) {
                        return false;
                    }
                    Marker unused = this.a.M = marker;
                    Point screenLocation = this.a.f.toScreenLocation(this.a.M.a);
                    this.a.M.setPosition(this.a.f.fromScreenLocation(new Point(screenLocation.x, screenLocation.y - 60)));
                    if (this.a.z != null) {
                        this.a.z.onMarkerDragStart(this.a.M);
                    }
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void c() {
        if (this.a.u != null) {
            this.a.u.onMapRenderFinished();
        }
    }

    public void c(GeoPoint geoPoint) {
        if (this.a.w != null) {
            this.a.w.onMapLongClick(CoordUtil.mc2ll(geoPoint));
        }
    }

    public void c(ad adVar) {
        if (!this.a.K.values().isEmpty()) {
            for (InfoWindow infoWindow : this.a.K.values()) {
                View view = infoWindow.b;
                if (view != null) {
                    view.setVisibility(0);
                }
            }
        }
        if (this.a.q != null) {
            this.a.q.onMapStatusChangeFinish(MapStatus.a(adVar));
        }
    }

    public void d() {
        this.a.I.lock();
        try {
            if (this.a.H != null) {
                this.a.H.a();
            }
        } finally {
            this.a.I.unlock();
        }
    }

    public void d(GeoPoint geoPoint) {
        if (this.a.M != null && this.a.M.f) {
            Point screenLocation = this.a.f.toScreenLocation(CoordUtil.mc2ll(geoPoint));
            this.a.M.setPosition(this.a.f.fromScreenLocation(new Point(screenLocation.x, screenLocation.y - 60)));
            if (this.a.z != null && this.a.M.f) {
                this.a.z.onMarkerDrag(this.a.M);
            }
        }
    }

    public void e() {
        this.a.I.lock();
        try {
            if (!(this.a.H == null || this.a.i == null)) {
                this.a.H.a();
                this.a.i.o();
            }
        } finally {
            this.a.I.unlock();
        }
    }

    public void e(GeoPoint geoPoint) {
        if (this.a.M != null && this.a.M.f) {
            Point screenLocation = this.a.f.toScreenLocation(CoordUtil.mc2ll(geoPoint));
            this.a.M.setPosition(this.a.f.fromScreenLocation(new Point(screenLocation.x, screenLocation.y - 60)));
            if (this.a.z != null && this.a.M.f) {
                this.a.z.onMarkerDragEnd(this.a.M);
            }
            Marker unused = this.a.M = null;
        }
    }

    public void f() {
        if (this.a.i != null) {
            this.a.i.b(false);
        }
        this.a.I.lock();
        try {
            if (this.a.H != null) {
                this.a.a(this.a.H);
            }
        } finally {
            this.a.I.unlock();
        }
    }
}
