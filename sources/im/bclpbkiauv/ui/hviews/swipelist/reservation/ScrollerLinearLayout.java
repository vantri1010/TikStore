package im.bclpbkiauv.ui.hviews.swipelist.reservation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollerLinearLayout extends LinearLayout implements ScrollerView {
    private final Scroller mScroller;

    public ScrollerLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScrollerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScroller = new Scroller(context);
    }

    public Scroller getScroller() {
        return this.mScroller;
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        if (dx == 0 && dy == 0) {
            this.mScroller.abortAnimation();
            return;
        }
        this.mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
        invalidate();
    }

    public void smoothScrollTo(int x, int y, int duration) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        boolean finished = this.mScroller.isFinished();
        if (!finished || (scrollX == x && scrollY == y)) {
            if (finished) {
                return;
            }
            if (this.mScroller.getFinalX() == x && this.mScroller.getFinalY() == y) {
                return;
            }
        }
        smoothScrollBy(x - scrollX, y - scrollY, duration);
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            invalidate();
        }
    }
}
