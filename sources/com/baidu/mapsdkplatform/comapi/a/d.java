package com.baidu.mapsdkplatform.comapi.a;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.Interpolator;
import com.baidu.mapapi.animation.AlphaAnimation;
import com.baidu.mapapi.animation.Animation;
import com.baidu.mapapi.animation.RotateAnimation;
import com.baidu.mapapi.animation.ScaleAnimation;
import com.baidu.mapapi.animation.SingleScaleAnimation;
import com.baidu.mapapi.animation.Transformation;
import com.baidu.mapapi.map.Marker;
import java.util.ArrayList;

public class d extends c {
    private Animator a = null;
    private long b = 0;
    private Interpolator c = null;
    /* access modifiers changed from: private */
    public Animation.AnimationListener d = null;
    private int e = 0;
    private ArrayList<Animation> f = new ArrayList<>();

    private ObjectAnimator b(Marker marker, Animation animation) {
        if (animation instanceof AlphaAnimation) {
            return ((a) animation.bdAnimation).a(marker);
        }
        if (animation instanceof RotateAnimation) {
            return ((f) animation.bdAnimation).a(marker);
        }
        if (animation instanceof Transformation) {
            return ((l) animation.bdAnimation).a(marker);
        }
        if (animation instanceof ScaleAnimation) {
            return ((h) animation.bdAnimation).a(marker);
        }
        if (animation instanceof SingleScaleAnimation) {
            return ((j) animation.bdAnimation).a(marker);
        }
        return null;
    }

    public void a() {
        Animator animator = this.a;
        if (animator != null) {
            animator.start();
        }
    }

    public void a(int i) {
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
            animator.addListener(new e(this));
        }
    }

    public void a(Interpolator interpolator) {
        this.c = interpolator;
    }

    public void a(Animation.AnimationListener animationListener) {
        this.d = animationListener;
    }

    public void a(Animation animation) {
        if (!this.f.contains(animation)) {
            this.f.add(animation);
        }
    }

    public void a(Marker marker, Animation animation) {
        ObjectAnimator b2;
        this.a = new AnimatorSet();
        ArrayList<Animation> arrayList = this.f;
        ArrayList arrayList2 = new ArrayList();
        arrayList2.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            Animation animation2 = arrayList.get(i);
            if (!(animation2 == null || (b2 = b(marker, animation2)) == null)) {
                arrayList2.add(b2);
            }
        }
        long j = this.b;
        if (j != 0) {
            this.a.setDuration(j);
        }
        Interpolator interpolator = this.c;
        if (interpolator != null) {
            this.a.setInterpolator(interpolator);
        }
        if (arrayList2.size() != 0) {
            int i2 = this.e;
            if (i2 == 0) {
                ((AnimatorSet) this.a).playTogether(arrayList2);
            } else if (i2 == 1) {
                ((AnimatorSet) this.a).playSequentially(arrayList2);
            }
        }
        a(this.a);
    }

    public void b() {
        Animator animator = this.a;
        if (animator != null) {
            animator.cancel();
            this.a = null;
        }
    }

    public void b(int i) {
    }

    public void c(int i) {
        this.e = i;
    }
}
