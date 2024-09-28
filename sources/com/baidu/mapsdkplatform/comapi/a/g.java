package com.baidu.mapsdkplatform.comapi.a;

import android.animation.Animator;

class g implements Animator.AnimatorListener {
    final /* synthetic */ f a;

    g(f fVar) {
        this.a = fVar;
    }

    public void onAnimationCancel(Animator animator) {
        if (this.a.d != null) {
            this.a.d.onAnimationCancel();
        }
    }

    public void onAnimationEnd(Animator animator) {
        if (this.a.d != null) {
            this.a.d.onAnimationEnd();
        }
    }

    public void onAnimationRepeat(Animator animator) {
        if (this.a.d != null) {
            this.a.d.onAnimationRepeat();
        }
    }

    public void onAnimationStart(Animator animator) {
        if (this.a.d != null) {
            this.a.d.onAnimationStart();
        }
    }
}
