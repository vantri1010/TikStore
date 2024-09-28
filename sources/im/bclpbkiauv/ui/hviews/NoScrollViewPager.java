package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;
import im.bclpbkiauv.ui.actionbar.Theme;

public class NoScrollViewPager extends ViewPager {
    private boolean enScroll = false;

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return this.enScroll && super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (this.enScroll) {
                return super.onTouchEvent(ev);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setEnScroll(boolean enScroll2) {
        this.enScroll = enScroll2;
    }
}
