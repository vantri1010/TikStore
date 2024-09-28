package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import im.bclpbkiauv.ui.hviews.helper.MryDisplayHelper;

public class DragFrameLayout extends FrameLayout {
    private Context context;
    private float downX;
    private float downY;
    private float dx;
    private float dy;
    private int height;
    private boolean isDrag = false;
    int left;
    private int maxHeight;
    private int maxWidth;
    int top;
    private int width;

    public boolean isDrag() {
        return this.isDrag;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        this.maxWidth = MryDisplayHelper.getScreenWidth(this.context);
        this.maxHeight = MryDisplayHelper.getScreenHeight(this.context);
    }

    public int getStatusBarHeight() {
        return getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
    }

    public int getNavigationBarHeight() {
        if (getResources().getIdentifier("config_showNavigationBar", "bool", "android") == 0) {
            return 0;
        }
        return this.context.getResources().getDimensionPixelSize(this.context.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!isEnabled()) {
            return false;
        }
        int action = event.getAction();
        if (action == 0) {
            this.isDrag = false;
            this.downX = event.getX();
            this.downY = event.getY();
            this.dx = (float) getLeft();
            this.dy = (float) getTop();
        } else if (action == 1) {
            float moveX1 = ((float) getLeft()) - this.dx;
            float moveY1 = ((float) getTop()) - this.dy;
            if (Math.abs(moveX1) > 3.0f || Math.abs(moveY1) > 3.0f) {
                this.isDrag = true;
            } else {
                this.isDrag = false;
            }
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
            if (this.left > this.maxWidth / 2) {
                lp.addRule(11);
            } else {
                lp.setMargins(0, this.top, 0, 0);
            }
            setPressed(false);
        } else if (action == 2) {
            float moveX = event.getX() - this.downX;
            float moveY = event.getY() - this.downY;
            if (Math.abs(moveX) > 3.0f || Math.abs(moveY) > 3.0f) {
                int l = (int) (((float) getLeft()) + moveX);
                int r = this.width + l;
                int t = (int) (((float) getTop()) + moveY);
                int b = this.height + t;
                if (l < 0) {
                    l = 0;
                    r = 0 + this.width;
                } else if (r > this.maxWidth) {
                    r = this.maxWidth;
                    l = r - this.width;
                }
                if (t < 0) {
                    t = 0;
                    b = 0 + this.height;
                } else if (b > this.maxHeight) {
                    b = this.maxHeight;
                    t = b - this.height;
                }
                this.left = l;
                this.top = t;
                layout(l, t, r, b);
                this.isDrag = true;
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) getLayoutParams();
                lp2.setMargins(this.left, this.top, 0, 0);
                lp2.removeRule(11);
                setLayoutParams(lp2);
            } else {
                this.isDrag = false;
            }
        } else if (action == 3) {
            setPressed(false);
        }
        return true;
    }

    public DragFrameLayout(Context context2) {
        super(context2);
        this.context = context2;
    }

    public DragFrameLayout(Context context2, AttributeSet attrs) {
        super(context2, attrs);
        this.context = context2;
    }
}
