package com.baidu.mapapi.map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewGroup;

class r extends AnimatorListenerAdapter {
    final /* synthetic */ ViewGroup.LayoutParams a;
    final /* synthetic */ int b;
    final /* synthetic */ SwipeDismissTouchListener c;

    r(SwipeDismissTouchListener swipeDismissTouchListener, ViewGroup.LayoutParams layoutParams, int i) {
        this.c = swipeDismissTouchListener;
        this.a = layoutParams;
        this.b = i;
    }

    public void onAnimationEnd(Animator animator) {
        this.c.f.onDismiss(this.c.e, this.c.l);
        this.c.e.setTranslationX(0.0f);
        this.a.height = this.b;
        this.c.e.setLayoutParams(this.a);
    }
}
