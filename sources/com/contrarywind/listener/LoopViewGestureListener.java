package com.contrarywind.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.contrarywind.view.WheelView;

public final class LoopViewGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final WheelView wheelView;

    public LoopViewGestureListener(WheelView wheelView2) {
        this.wheelView = wheelView2;
    }

    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.wheelView.scrollBy(velocityY);
        return true;
    }
}
