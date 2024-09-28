package com.baidu.mapsdkplatform.comapi.a;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.animation.Interpolator;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.map.Marker;

public class j extends c {
    private Animator a = null;
    private long b = 0;
    private Interpolator c = null;
    /* access modifiers changed from: private */
    public Animation.AnimationListener d = null;
    private int e = 1;
    private int f = 0;
    private float[] g;
    private int h = 1;

    public j(int i, float... fArr) {
        this.g = fArr;
        this.h = i;
    }

    /* access modifiers changed from: package-private */
    public ObjectAnimator a(Marker marker) {
        int i = this.h;
        ObjectAnimator ofFloat = i == 1 ? ObjectAnimator.ofFloat(marker, "scaleX", this.g) : i == 2 ? ObjectAnimator.ofFloat(marker, "scaleY", this.g) : null;
        if (ofFloat != null) {
            ofFloat.setRepeatCount(this.f);
            ofFloat.setRepeatMode(c());
            ofFloat.setDuration(this.b);
            Interpolator interpolator = this.c;
            if (interpolator != null) {
                ofFloat.setInterpolator(interpolator);
            }
        }
        return ofFloat;
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
            animator.addListener(new k(this));
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
