package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.baidu.mapsdkplatform.comapi.map.n;
import javax.microedition.khronos.opengles.GL10;

class m implements n {
    final /* synthetic */ MapView a;

    m(MapView mapView) {
        this.a = mapView;
    }

    public void a() {
        String str;
        if (this.a.e != null && this.a.e.a() != null) {
            float f = this.a.e.a().E().a;
            if (f < this.a.e.a().b) {
                f = this.a.e.a().b;
            } else if (f > this.a.e.a().a) {
                f = this.a.e.a().a;
            }
            if (Math.abs(this.a.u - f) > 0.0f) {
                int i = MapView.q.get(Math.round(f));
                int i2 = ((int) (((double) i) / this.a.e.a().E().m)) / 2;
                this.a.o.setPadding(i2, 0, i2, 0);
                Object[] objArr = new Object[1];
                if (i >= 1000) {
                    objArr[0] = Integer.valueOf(i / 1000);
                    str = String.format(" %d公里 ", objArr);
                } else {
                    objArr[0] = Integer.valueOf(i);
                    str = String.format(" %d米 ", objArr);
                }
                this.a.m.setText(str);
                this.a.n.setText(str);
                float unused = this.a.u = f;
            }
            this.a.b();
            this.a.requestLayout();
        }
    }

    public void a(Bitmap bitmap) {
    }

    public void a(MotionEvent motionEvent) {
    }

    public void a(GeoPoint geoPoint) {
    }

    public void a(ad adVar) {
    }

    public void a(String str) {
    }

    public void a(GL10 gl10, ad adVar) {
    }

    public void a(boolean z) {
    }

    public void a(boolean z, int i) {
    }

    public void b() {
    }

    public void b(GeoPoint geoPoint) {
    }

    public void b(ad adVar) {
    }

    public boolean b(String str) {
        return false;
    }

    public void c() {
    }

    public void c(GeoPoint geoPoint) {
    }

    public void c(ad adVar) {
    }

    public void d() {
    }

    public void d(GeoPoint geoPoint) {
    }

    public void e() {
    }

    public void e(GeoPoint geoPoint) {
    }

    public void f() {
    }
}
