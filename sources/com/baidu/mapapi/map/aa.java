package com.baidu.mapapi.map;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comapi.map.ad;
import com.baidu.mapsdkplatform.comapi.map.n;
import javax.microedition.khronos.opengles.GL10;

class aa implements n {
    final /* synthetic */ WearMapView a;

    aa(WearMapView wearMapView) {
        this.a = wearMapView;
    }

    public void a() {
        String str;
        if (this.a.f != null && this.a.f.a() != null) {
            float f = this.a.f.a().E().a;
            if (this.a.A != f) {
                int intValue = ((Integer) WearMapView.x.get((int) f)).intValue();
                int i = ((int) (((double) intValue) / this.a.f.a().E().m)) / 2;
                this.a.r.setPadding(i, 0, i, 0);
                Object[] objArr = new Object[1];
                if (intValue >= 1000) {
                    objArr[0] = Integer.valueOf(intValue / 1000);
                    str = String.format(" %d公里 ", objArr);
                } else {
                    objArr[0] = Integer.valueOf(intValue);
                    str = String.format(" %d米 ", objArr);
                }
                this.a.p.setText(str);
                this.a.q.setText(str);
                float unused = this.a.A = f;
            }
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
