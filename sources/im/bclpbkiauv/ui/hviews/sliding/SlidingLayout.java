package im.bclpbkiauv.ui.hviews.sliding;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import im.bclpbkiauv.messenger.R;

public class SlidingLayout extends FrameLayout {
    private static final int INVALID_POINTER = -1;
    private static final int RESET_DURATION = 200;
    public static final int SLIDING_DISTANCE_UNDEFINED = -1;
    public static final int SLIDING_MODE_BOTH = 0;
    public static final int SLIDING_MODE_BOTTOM = 2;
    public static final int SLIDING_MODE_TOP = 1;
    public static final int SLIDING_POINTER_MODE_MORE = 1;
    public static final int SLIDING_POINTER_MODE_ONE = 0;
    private static final int SMOOTH_DURATION = 1000;
    public static final int STATE_IDLE = 1;
    public static final int STATE_SLIDING = 2;
    private int mActivePointerId;
    private View mBackgroundView;
    private int mBackgroundViewLayoutId;
    private View.OnTouchListener mDelegateTouchListener;
    private View mFollowView;
    private float mInitialDownY;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private float mLastMotionY;
    private SlidingListener mSlidingListener;
    private int mSlidingMode;
    private float mSlidingOffset;
    private int mSlidingPointerMode;
    private int mSlidingTopMaxDistance;
    private View mTargetView;
    private int mTouchSlop;

    public interface SlidingListener {
        void onSlidingChangePointer(View view, int i);

        void onSlidingOffset(View view, float f);

        void onSlidingStateChange(View view, int i);
    }

    public SlidingLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mBackgroundViewLayoutId = 0;
        this.mActivePointerId = -1;
        this.mSlidingOffset = 0.5f;
        this.mSlidingMode = 0;
        this.mSlidingPointerMode = 1;
        this.mSlidingTopMaxDistance = -1;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingLayout);
        this.mBackgroundViewLayoutId = a.getResourceId(0, this.mBackgroundViewLayoutId);
        this.mSlidingMode = a.getInteger(1, 0);
        this.mSlidingPointerMode = a.getInteger(2, 1);
        this.mSlidingTopMaxDistance = a.getDimensionPixelSize(3, -1);
        a.recycle();
        if (this.mBackgroundViewLayoutId != 0) {
            setBackgroundView(View.inflate(getContext(), this.mBackgroundViewLayoutId, (ViewGroup) null));
        }
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setBackgroundView(View view) {
        View view2 = this.mBackgroundView;
        if (view2 != null) {
            removeView(view2);
        }
        this.mBackgroundView = view;
        addView(view, 0);
    }

    public View getBackgroundView() {
        return this.mBackgroundView;
    }

    public void setSlidingDistance(int distance) {
        this.mSlidingTopMaxDistance = distance;
    }

    public int setSlidingDistance() {
        return this.mSlidingTopMaxDistance;
    }

    public float getSlidingOffset() {
        return this.mSlidingOffset;
    }

    public void setSlidingOffset(float slidingOffset) {
        this.mSlidingOffset = slidingOffset;
    }

    public void setSlidingListener(SlidingListener slidingListener) {
        this.mSlidingListener = slidingListener;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() != 0) {
            if (this.mTargetView == null) {
                ensureTarget();
            }
            if (this.mTargetView != null) {
            }
        }
    }

    private void ensureTarget() {
        if (this.mTargetView == null) {
            this.mTargetView = getChildAt(getChildCount() - 1);
        }
    }

    public void setTargetView(View view) {
        View view2 = this.mTargetView;
        if (view2 != null) {
            removeView(view2);
        }
        this.mTargetView = view;
        addView(view);
    }

    public void setFollowView(View view) {
        this.mFollowView = view;
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        this.mDelegateTouchListener = l;
    }

    public View getTargetView() {
        return this.mTargetView;
    }

    public float getSlidingDistance() {
        return getInstrument().getTranslationY(getTargetView());
    }

    public Instrument getInstrument() {
        return Instrument.getInstance();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0014, code lost:
        if (r0 != 3) goto L_0x0089;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            r6.ensureTarget()
            int r0 = androidx.core.view.MotionEventCompat.getActionMasked(r7)
            r1 = -1082130432(0xffffffffbf800000, float:-1.0)
            r2 = 0
            if (r0 == 0) goto L_0x0075
            r3 = -1
            r4 = 1
            if (r0 == r4) goto L_0x0070
            r5 = 2
            if (r0 == r5) goto L_0x0018
            r1 = 3
            if (r0 == r1) goto L_0x0070
            goto L_0x0089
        L_0x0018:
            int r5 = r6.mActivePointerId
            if (r5 != r3) goto L_0x001d
            return r2
        L_0x001d:
            float r3 = r6.getMotionEventY(r7, r5)
            int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r1 != 0) goto L_0x0026
            return r2
        L_0x0026:
            float r1 = r6.mInitialDownY
            int r2 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x004c
            float r1 = r3 - r1
            int r2 = r6.mTouchSlop
            float r2 = (float) r2
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x006f
            boolean r2 = r6.mIsBeingDragged
            if (r2 != 0) goto L_0x006f
            boolean r2 = r6.canChildScrollUp()
            if (r2 != 0) goto L_0x006f
            float r2 = r6.mInitialDownY
            int r5 = r6.mTouchSlop
            float r5 = (float) r5
            float r2 = r2 + r5
            r6.mInitialMotionY = r2
            r6.mLastMotionY = r2
            r6.mIsBeingDragged = r4
            goto L_0x006f
        L_0x004c:
            int r2 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x006f
            float r1 = r1 - r3
            int r2 = r6.mTouchSlop
            float r2 = (float) r2
            int r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x006e
            boolean r2 = r6.mIsBeingDragged
            if (r2 != 0) goto L_0x006e
            boolean r2 = r6.canChildScrollDown()
            if (r2 != 0) goto L_0x006e
            float r2 = r6.mInitialDownY
            int r5 = r6.mTouchSlop
            float r5 = (float) r5
            float r2 = r2 + r5
            r6.mInitialMotionY = r2
            r6.mLastMotionY = r2
            r6.mIsBeingDragged = r4
        L_0x006e:
            goto L_0x0089
        L_0x006f:
            goto L_0x0089
        L_0x0070:
            r6.mIsBeingDragged = r2
            r6.mActivePointerId = r3
            goto L_0x0089
        L_0x0075:
            int r3 = androidx.core.view.MotionEventCompat.getPointerId(r7, r2)
            r6.mActivePointerId = r3
            r6.mIsBeingDragged = r2
            float r3 = r6.getMotionEventY(r7, r3)
            int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r1 != 0) goto L_0x0086
            return r2
        L_0x0086:
            r6.mInitialDownY = r3
        L_0x0089:
            boolean r1 = r6.mIsBeingDragged
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.sliding.SlidingLayout.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public boolean canChildScrollUp() {
        if (Build.VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTargetView, -1);
        }
        View view = this.mTargetView;
        if (view instanceof AbsListView) {
            AbsListView absListView = (AbsListView) view;
            if (absListView.getChildCount() <= 0 || (absListView.getFirstVisiblePosition() <= 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop())) {
                return false;
            }
            return true;
        } else if (ViewCompat.canScrollVertically(view, -1) || this.mTargetView.getScrollY() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canChildScrollDown() {
        if (Build.VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTargetView, 1);
        }
        View view = this.mTargetView;
        if (view instanceof AbsListView) {
            AbsListView absListView = (AbsListView) view;
            if (absListView.getChildCount() <= 0 || absListView.getAdapter() == null || (absListView.getLastVisiblePosition() >= ((ListAdapter) absListView.getAdapter()).getCount() - 1 && absListView.getChildAt(absListView.getChildCount() - 1).getBottom() >= absListView.getPaddingBottom())) {
                return false;
            }
            return true;
        } else if (ViewCompat.canScrollVertically(view, 1) || this.mTargetView.getScrollY() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
        if (r0 != 3) goto L_0x0131;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            android.view.View$OnTouchListener r0 = r8.mDelegateTouchListener
            r1 = 1
            if (r0 == 0) goto L_0x000c
            boolean r0 = r0.onTouch(r8, r9)
            if (r0 == 0) goto L_0x000c
            return r1
        L_0x000c:
            int r0 = r9.getAction()
            if (r0 == r1) goto L_0x011d
            r2 = 2
            if (r0 == r2) goto L_0x001a
            r2 = 3
            if (r0 == r2) goto L_0x011d
            goto L_0x0131
        L_0x001a:
            r0 = 0
            r3 = 0
            int r4 = r8.mSlidingPointerMode
            r5 = 1065353216(0x3f800000, float:1.0)
            if (r4 != r1) goto L_0x008c
            int r4 = r9.getPointerCount()
            int r4 = r4 - r1
            int r4 = androidx.core.view.MotionEventCompat.getPointerId(r9, r4)
            int r6 = r8.mActivePointerId
            if (r6 == r4) goto L_0x0048
            r8.mActivePointerId = r4
            float r6 = r8.getMotionEventY(r9, r4)
            r8.mInitialDownY = r6
            int r7 = r8.mTouchSlop
            float r7 = (float) r7
            float r6 = r6 + r7
            r8.mInitialMotionY = r6
            r8.mLastMotionY = r6
            im.bclpbkiauv.ui.hviews.sliding.SlidingLayout$SlidingListener r6 = r8.mSlidingListener
            if (r6 == 0) goto L_0x0048
            android.view.View r7 = r8.mTargetView
            r6.onSlidingChangePointer(r7, r4)
        L_0x0048:
            int r6 = r8.mActivePointerId
            float r6 = r8.getMotionEventY(r9, r6)
            float r7 = r8.mLastMotionY
            float r6 = r6 - r7
            im.bclpbkiauv.ui.hviews.sliding.Instrument r0 = r8.getInstrument()
            android.view.View r7 = r8.mTargetView
            float r0 = r0.getTranslationY(r7)
            float r0 = r0 + r6
            float r0 = java.lang.Math.abs(r0)
            android.view.View r7 = r8.mTargetView
            int r7 = r7.getMeasuredHeight()
            float r7 = (float) r7
            float r0 = r0 / r7
            float r5 = r5 - r0
            im.bclpbkiauv.ui.hviews.sliding.Instrument r0 = r8.getInstrument()
            android.view.View r7 = r8.mTargetView
            float r0 = r0.getTranslationY(r7)
            float r7 = r8.mSlidingOffset
            float r7 = r7 * r6
            float r7 = r7 * r5
            float r0 = r0 + r7
            int r6 = r8.mActivePointerId
            float r6 = r8.getMotionEventY(r9, r6)
            r8.mLastMotionY = r6
            int r6 = r8.mActivePointerId
            float r6 = r8.getMotionEventY(r9, r6)
            float r7 = r8.mInitialMotionY
            float r6 = r6 - r7
            goto L_0x00b8
        L_0x008c:
            im.bclpbkiauv.ui.hviews.sliding.Instrument r4 = r8.getInstrument()
            android.view.View r6 = r8.mTargetView
            float r4 = r4.getTranslationY(r6)
            android.view.View r6 = r8.mTargetView
            int r6 = r6.getMeasuredHeight()
            float r6 = (float) r6
            float r4 = r4 / r6
            float r4 = java.lang.Math.abs(r4)
            float r5 = r5 - r4
            float r4 = r9.getY()
            float r6 = r8.mInitialMotionY
            float r4 = r4 - r6
            float r6 = r8.mSlidingOffset
            float r4 = r4 * r6
            float r0 = r4 * r5
            float r4 = r9.getY()
            float r6 = r8.mInitialMotionY
            float r6 = r4 - r6
        L_0x00b8:
            float r3 = r8.getSlidingDistance()
            int r4 = r8.mSlidingMode
            if (r4 == 0) goto L_0x0104
            r5 = 0
            if (r4 == r1) goto L_0x00df
            if (r4 == r2) goto L_0x00c6
            goto L_0x0110
        L_0x00c6:
            int r4 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x00ce
            int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0110
        L_0x00ce:
            int r4 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x00d3
            r0 = 0
        L_0x00d3:
            im.bclpbkiauv.ui.hviews.sliding.Instrument r4 = r8.getInstrument()
            android.view.View r5 = r8.mTargetView
            android.view.View r7 = r8.mFollowView
            r4.slidingByDelta(r5, r7, r0)
            goto L_0x0110
        L_0x00df:
            int r4 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x00e7
            int r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x0110
        L_0x00e7:
            int r4 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x00ec
            r0 = 0
        L_0x00ec:
            int r4 = r8.mSlidingTopMaxDistance
            r5 = -1
            if (r4 == r5) goto L_0x00f8
            float r5 = (float) r4
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 >= 0) goto L_0x00f7
            goto L_0x00f8
        L_0x00f7:
            float r0 = (float) r4
        L_0x00f8:
            im.bclpbkiauv.ui.hviews.sliding.Instrument r4 = r8.getInstrument()
            android.view.View r5 = r8.mTargetView
            android.view.View r7 = r8.mFollowView
            r4.slidingByDelta(r5, r7, r0)
            goto L_0x0110
        L_0x0104:
            im.bclpbkiauv.ui.hviews.sliding.Instrument r4 = r8.getInstrument()
            android.view.View r5 = r8.mTargetView
            android.view.View r7 = r8.mFollowView
            r4.slidingByDelta(r5, r7, r0)
        L_0x0110:
            im.bclpbkiauv.ui.hviews.sliding.SlidingLayout$SlidingListener r4 = r8.mSlidingListener
            if (r4 == 0) goto L_0x0131
            r4.onSlidingStateChange(r8, r2)
            im.bclpbkiauv.ui.hviews.sliding.SlidingLayout$SlidingListener r2 = r8.mSlidingListener
            r2.onSlidingOffset(r8, r0)
            goto L_0x0131
        L_0x011d:
            im.bclpbkiauv.ui.hviews.sliding.SlidingLayout$SlidingListener r0 = r8.mSlidingListener
            if (r0 == 0) goto L_0x0124
            r0.onSlidingStateChange(r8, r1)
        L_0x0124:
            im.bclpbkiauv.ui.hviews.sliding.Instrument r0 = r8.getInstrument()
            android.view.View r2 = r8.mTargetView
            android.view.View r3 = r8.mFollowView
            r4 = 200(0xc8, double:9.9E-322)
            r0.reset(r2, r3, r4)
        L_0x0131:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.sliding.SlidingLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setSlidingMode(int mode) {
        this.mSlidingMode = mode;
    }

    public int getSlidingMode() {
        return this.mSlidingMode;
    }

    public void smoothScrollTo(float y) {
        getInstrument().smoothTo(this.mTargetView, y, 1000);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        View view = this.mTargetView;
        if (view != null) {
            view.clearAnimation();
        }
        this.mSlidingMode = 0;
        this.mTargetView = null;
        this.mBackgroundView = null;
        this.mSlidingListener = null;
    }
}
