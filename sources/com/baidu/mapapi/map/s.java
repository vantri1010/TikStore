package com.baidu.mapapi.map;

import android.animation.ValueAnimator;
import android.view.ViewGroup;

class s implements ValueAnimator.AnimatorUpdateListener {
    final /* synthetic */ ViewGroup.LayoutParams a;
    final /* synthetic */ SwipeDismissTouchListener b;

    s(SwipeDismissTouchListener swipeDismissTouchListener, ViewGroup.LayoutParams layoutParams) {
        this.b = swipeDismissTouchListener;
        this.a = layoutParams;
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.a.height = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.b.e.setLayoutParams(this.a);
    }
}
