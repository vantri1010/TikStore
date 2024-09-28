package com.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class NoTouchExceptionViewPager extends ViewPager {
    public NoTouchExceptionViewPager(Context context) {
        super(context);
    }

    public NoTouchExceptionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }
}
