package im.bclpbkiauv.ui.hui.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class SilderRelativeLayout extends RelativeLayout {
    private boolean LeftSilder;
    private int downX;
    private int downY;
    private int iTouchDownViewX;
    private boolean isFinish;
    private boolean isSilding;
    private long lDownTime;
    private long lMoveTime;
    private ViewGroup mParentView;
    private Scroller mScroller;
    private int mTouchSlop;
    private ObjectAnimator objectAnimatorX;
    private OnSildingFinishListener onSildingFinishListener;
    private int tempX;
    private int viewWidth;

    public interface OnSildingFinishListener {
        void onLeftScroll();

        void onMove(int i, int i2);

        void onSildingFinish();
    }

    public SilderRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SilderRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.LeftSilder = false;
        this.lMoveTime = 0;
        this.lDownTime = 0;
        this.objectAnimatorX = null;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mScroller = new Scroller(context);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0) {
            int rawX = (int) ev.getRawX();
            this.tempX = rawX;
            this.downX = rawX;
            this.downY = (int) ev.getRawY();
            this.lDownTime = System.currentTimeMillis();
            if (getChildAt(0) != null) {
                this.LeftSilder = ((float) this.downX) < getChildAt(0).getX();
                this.iTouchDownViewX = (int) getChildAt(0).getX();
            }
        } else if (action == 2 && Math.abs(((int) ev.getRawX()) - this.downX) > this.mTouchSlop && Math.abs(((int) ev.getRawY()) - this.downY) < this.mTouchSlop) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 1) {
            int upX = (int) event.getRawX();
            this.isSilding = false;
            this.lMoveTime = System.currentTimeMillis() - this.lDownTime;
            if (upX - this.downX >= AndroidUtilities.getRealScreenSize().x / 3) {
                this.isFinish = true;
                if (getChildAt(0) != null) {
                    viewTranslateAnimation(getChildAt(0), (float) AndroidUtilities.getRealScreenSize().x);
                }
                OnSildingFinishListener onSildingFinishListener2 = this.onSildingFinishListener;
                if (onSildingFinishListener2 != null) {
                    onSildingFinishListener2.onSildingFinish();
                }
            } else if (this.downX - upX >= AndroidUtilities.getRealScreenSize().x / 3) {
                KLog.d("66666666 = 1/3");
                if (getChildAt(0) != null) {
                    viewTranslateAnimation(getChildAt(0), 0.0f);
                }
                OnSildingFinishListener onSildingFinishListener3 = this.onSildingFinishListener;
                if (onSildingFinishListener3 != null) {
                    onSildingFinishListener3.onLeftScroll();
                }
                this.isFinish = true;
            } else if (this.lMoveTime >= 100 || Math.abs(upX - this.downX) <= 150) {
                if (getChildAt(0) != null) {
                    if (this.iTouchDownViewX < AndroidUtilities.getRealScreenSize().x / 2) {
                        viewTranslateAnimation(getChildAt(0), 0.0f);
                    } else {
                        viewTranslateAnimation(getChildAt(0), (float) AndroidUtilities.getRealScreenSize().x);
                    }
                }
                this.isFinish = false;
            } else {
                KLog.d("66666666 = short time");
                if (getChildAt(0) != null) {
                    if (this.LeftSilder) {
                        viewTranslateAnimation(getChildAt(0), 0.0f);
                    } else {
                        viewTranslateAnimation(getChildAt(0), (float) AndroidUtilities.getRealScreenSize().x);
                    }
                }
                this.isFinish = true;
            }
        } else if (action == 2) {
            int moveX = (int) event.getRawX();
            int deltaX = this.tempX - moveX;
            this.tempX = moveX;
            if (Math.abs(moveX - this.downX) > this.mTouchSlop && Math.abs(((int) event.getRawY()) - this.downY) < this.mTouchSlop) {
                this.isSilding = true;
            }
            this.LeftSilder = deltaX > 0;
            if (getChildAt(0) != null) {
                int X = ((int) getChildAt(0).getX()) + (deltaX * -1);
                if (X < 0) {
                    X = 0;
                }
                if (X > AndroidUtilities.getRealScreenSize().x) {
                    X = AndroidUtilities.getRealScreenSize().x;
                }
                getChildAt(0).setX((float) X);
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.mParentView = (ViewGroup) getParent();
            this.viewWidth = getWidth();
        }
    }

    public void setOnSildingFinishListener(OnSildingFinishListener onSildingFinishListener2) {
        this.onSildingFinishListener = onSildingFinishListener2;
    }

    private void scrollOrigin() {
        int delta = this.mParentView.getScrollX();
        this.mScroller.startScroll(this.mParentView.getScrollX(), 0, -delta, 0, Math.abs(delta));
        postInvalidate();
    }

    public void computeScroll() {
        if (this.mScroller.computeScrollOffset()) {
            this.mParentView.scrollTo(this.mScroller.getCurrX(), this.mScroller.getCurrY());
            postInvalidate();
            if (this.mScroller.isFinished() && this.isFinish) {
                OnSildingFinishListener onSildingFinishListener2 = this.onSildingFinishListener;
                if (onSildingFinishListener2 != null) {
                    onSildingFinishListener2.onSildingFinish();
                } else {
                    this.isFinish = false;
                }
            }
        }
    }

    private void viewTranslateAnimation(View view, float endX) {
        float[] x = {endX};
        if (this.objectAnimatorX == null) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, "translationX", x);
            this.objectAnimatorX = ofFloat;
            ofFloat.setDuration(300);
        }
        this.objectAnimatorX.setFloatValues(x);
        this.objectAnimatorX.start();
    }
}
