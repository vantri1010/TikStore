package com.baidu.mapsdkplatform.comapi.a;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Point;
import android.view.animation.Interpolator;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

public class l extends c {
    private Animator a = null;
    private long b = 0;
    private Interpolator c = null;
    /* access modifiers changed from: private */
    public Animation.AnimationListener d = null;
    private int e = 1;
    private int f = 0;
    private Object[] g;

    public class a implements TypeEvaluator {
        public a() {
        }

        public Object evaluate(float f, Object obj, Object obj2) {
            LatLng latLng = (LatLng) obj;
            LatLng latLng2 = (LatLng) obj2;
            double d = (double) f;
            return new LatLng(latLng.latitude + (d * (latLng2.latitude - latLng.latitude)), latLng.longitude + ((latLng2.longitude - latLng.longitude) * d));
        }
    }

    public class b implements TypeEvaluator {
        public b() {
        }

        public Object evaluate(float f, Object obj, Object obj2) {
            Point point = (Point) obj;
            Point point2 = (Point) obj2;
            return new Point((int) (((float) point.x) + (((float) (point2.x - point.x)) * f)), (int) (((float) point.y) + (f * ((float) (point2.y - point.y)))));
        }
    }

    public l(Point... pointArr) {
        this.g = pointArr;
    }

    public l(LatLng... latLngArr) {
        this.g = latLngArr;
    }

    /* access modifiers changed from: package-private */
    public ObjectAnimator a(Marker marker) {
        ObjectAnimator objectAnimator;
        if (marker.isFixed()) {
            if (this.g[0] instanceof Point) {
                objectAnimator = ObjectAnimator.ofObject(marker, "fixedScreenPosition", new b(), this.g);
            } else {
                throw new ClassCastException("BDMapSDKException: if the marker is fixed on screen, the parameters of Transformation must be android.graphics.Point");
            }
        } else if (this.g[0] instanceof LatLng) {
            objectAnimator = ObjectAnimator.ofObject(marker, "position", new a(), this.g);
        } else {
            throw new ClassCastException("BDMapSDKException: if the marker isn't fixed on screen, the parameters of Transformation must be Latlng");
        }
        if (objectAnimator != null) {
            objectAnimator.setRepeatCount(this.f);
            objectAnimator.setRepeatMode(c());
            objectAnimator.setDuration(this.b);
            Interpolator interpolator = this.c;
            if (interpolator != null) {
                objectAnimator.setInterpolator(interpolator);
            }
        }
        return objectAnimator;
    }

    public void a() {
        Animator animator = this.a;
        if (animator != null) {
            animator.start();
        }
    }

    public void a(int i) {
        this.e = i;
    }

    public void a(long j) {
        if (j < 0) {
            j = 0;
        }
        this.b = j;
    }

    /* access modifiers changed from: protected */
    public void a(Animator animator) {
        if (animator != null) {
            animator.addListener(new m(this));
        }
    }

    public void a(Interpolator interpolator) {
        this.c = interpolator;
    }

    public void a(Animation.AnimationListener animationListener) {
        this.d = animationListener;
    }

    public void a(Marker marker, Animation animation) {
        ObjectAnimator a2 = a(marker);
        this.a = a2;
        a((Animator) a2);
    }

    public void b() {
        Animator animator = this.a;
        if (animator != null) {
            animator.cancel();
            this.a = null;
        }
    }

    public void b(int i) {
        if (i > 0 || i == -1) {
            this.f = i;
        }
    }

    public int c() {
        return this.e;
    }

    public void c(int i) {
    }
}
