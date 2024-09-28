package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.util.List;

public class MyScrollView extends FrameLayout implements NestedScrollingParent, NestedScrollingChild2, ScrollingView {
    private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
    static final int ANIMATED_SCROLL_GAP = 250;
    private static final float INIT_RATE = 0.8f;
    private static final int INVALID_POINTER = -1;
    static final float MAX_SCROLL_FACTOR = 0.5f;
    private static final int[] SCROLLVIEW_STYLEABLE = {16843130};
    private static final String TAG = "NestedScrollView";
    private Interpolator interpolator;
    private int mActivePointerId;
    private final NestedScrollingChildHelper mChildHelper;
    private View mChildToScrollTo;
    private boolean mFillViewport;
    private boolean mIsBeingDragged;
    private boolean mIsLaidOut;
    private boolean mIsLayoutDirty;
    private int mLastMotionY;
    private long mLastScroll;
    private int mLastScrollerY;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mNestedYOffset;
    private OnScrollChangeListener mOnScrollChangeListener;
    private int mOverFlingDistance;
    private int mOverScrollDistance;
    private final NestedScrollingParentHelper mParentHelper;
    private SavedState mSavedState;
    private final int[] mScrollConsumed;
    private final int[] mScrollOffset;
    private OverScroller mScroller;
    private boolean mSmoothScrollingEnabled;
    private final Rect mTempRect;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;

    public interface OnScrollChangeListener {
        void onScrollChange(MyScrollView myScrollView, int i, int i2, int i3, int i4);
    }

    public MyScrollView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempRect = new Rect();
        this.mIsLayoutDirty = true;
        this.mIsLaidOut = false;
        this.mChildToScrollTo = null;
        this.mIsBeingDragged = false;
        this.mSmoothScrollingEnabled = true;
        this.mActivePointerId = -1;
        this.mScrollOffset = new int[2];
        this.mScrollConsumed = new int[2];
        this.interpolator = new DecelerateInterpolator();
        setOverScrollMode(0);
        initScrollView();
        TypedArray a = context.obtainStyledAttributes(attrs, SCROLLVIEW_STYLEABLE, defStyleAttr, 0);
        setFillViewport(a.getBoolean(0, false));
        a.recycle();
        this.mParentHelper = new NestedScrollingParentHelper(this);
        this.mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
        this.mOverScrollDistance = (int) (context.getResources().getDisplayMetrics().density * 300.0f);
        this.mOverFlingDistance = (int) (context.getResources().getDisplayMetrics().density * 200.0f);
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        this.mChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return this.mChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return this.mChildHelper.startNestedScroll(axes);
    }

    public boolean startNestedScroll(int axes, int type) {
        return this.mChildHelper.startNestedScroll(axes, type);
    }

    public void stopNestedScroll() {
        this.mChildHelper.stopNestedScroll();
    }

    public void stopNestedScroll(int type) {
        this.mChildHelper.stopNestedScroll(type);
    }

    public boolean hasNestedScrollingParent() {
        return this.mChildHelper.hasNestedScrollingParent();
    }

    public boolean hasNestedScrollingParent(int type) {
        return this.mChildHelper.hasNestedScrollingParent(type);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        return this.mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return this.mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        return this.mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return this.mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return this.mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(2);
    }

    public void onStopNestedScroll(View target) {
        this.mParentHelper.onStopNestedScroll(target);
        stopNestedScroll();
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int oldScrollY = getScrollY();
        scrollBy(0, dyUnconsumed);
        int myConsumed = getScrollY() - oldScrollY;
        dispatchNestedScroll(0, myConsumed, 0, dyUnconsumed - myConsumed, (int[]) null);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(dx, dy, consumed, (int[]) null);
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (consumed) {
            return false;
        }
        flingWithNestedDispatch((int) velocityY);
        return true;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    public int getNestedScrollAxes() {
        return this.mParentHelper.getNestedScrollAxes();
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    /* access modifiers changed from: protected */
    public float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int length = getVerticalFadingEdgeLength();
        int scrollY = getScrollY();
        if (scrollY < length) {
            return ((float) scrollY) / ((float) length);
        }
        return 1.0f;
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int length = getVerticalFadingEdgeLength();
        int span = (getChildAt(0).getBottom() - getScrollY()) - (getHeight() - getPaddingBottom());
        if (span < length) {
            return ((float) span) / ((float) length);
        }
        return 1.0f;
    }

    public int getMaxScrollAmount() {
        return (int) (((float) getHeight()) * 0.5f);
    }

    private void initScrollView() {
        this.mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void addView(View child) {
        if (getChildCount() <= 0) {
            super.addView(child);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, int index) {
        if (getChildCount() <= 0) {
            super.addView(child, index);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, params);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() <= 0) {
            super.addView(child, index, params);
            return;
        }
        throw new IllegalStateException("ScrollView can host only one direct child");
    }

    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        this.mOnScrollChangeListener = l;
    }

    private boolean canScroll() {
        View child = getChildAt(0);
        if (child == null) {
            return false;
        }
        if (getHeight() < getPaddingTop() + child.getHeight() + getPaddingBottom()) {
            return true;
        }
        return false;
    }

    public boolean isFillViewport() {
        return this.mFillViewport;
    }

    public void setFillViewport(boolean fillViewport) {
        if (fillViewport != this.mFillViewport) {
            this.mFillViewport = fillViewport;
            requestLayout();
        }
    }

    public boolean isSmoothScrollingEnabled() {
        return this.mSmoothScrollingEnabled;
    }

    public void setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        this.mSmoothScrollingEnabled = smoothScrollingEnabled;
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        OnScrollChangeListener onScrollChangeListener = this.mOnScrollChangeListener;
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mFillViewport && View.MeasureSpec.getMode(heightMeasureSpec) != 0 && getChildCount() > 0) {
            View child = getChildAt(0);
            int height = getMeasuredHeight();
            if (child.getMeasuredHeight() < height) {
                child.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), ((FrameLayout.LayoutParams) child.getLayoutParams()).width), View.MeasureSpec.makeMeasureSpec((height - getPaddingTop()) - getPaddingBottom(), 1073741824));
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    public boolean executeKeyEvent(KeyEvent event) {
        this.mTempRect.setEmpty();
        boolean canScroll = canScroll();
        int i = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
        if (!canScroll) {
            if (!isFocused() || event.getKeyCode() == 4) {
                return false;
            }
            View currentFocused = findFocus();
            if (currentFocused == this) {
                currentFocused = null;
            }
            View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
            if (nextFocused == null || nextFocused == this || !nextFocused.requestFocus(TsExtractor.TS_STREAM_TYPE_HDMV_DTS)) {
                return false;
            }
            return true;
        } else if (event.getAction() != 0) {
            return false;
        } else {
            int keyCode = event.getKeyCode();
            if (keyCode != 19) {
                if (keyCode != 20) {
                    if (keyCode != 62) {
                        return false;
                    }
                    if (event.isShiftPressed()) {
                        i = 33;
                    }
                    pageScroll(i);
                    return false;
                } else if (!event.isAltPressed()) {
                    return arrowScroll(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                } else {
                    return fullScroll(TsExtractor.TS_STREAM_TYPE_HDMV_DTS);
                }
            } else if (!event.isAltPressed()) {
                return arrowScroll(33);
            } else {
                return fullScroll(33);
            }
        }
    }

    private boolean inChild(int x, int y) {
        if (getChildCount() <= 0) {
            return false;
        }
        int scrollY = getScrollY();
        View child = getChildAt(0);
        if (y < child.getTop() - scrollY || y >= child.getBottom() - scrollY || x < child.getLeft() || x >= child.getRight()) {
            return false;
        }
        return true;
    }

    private void initOrResetVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MotionEvent motionEvent = ev;
        int action = ev.getAction();
        if (action == 2 && this.mIsBeingDragged) {
            return true;
        }
        int i = action & 255;
        if (i != 0) {
            if (i != 1) {
                if (i == 2) {
                    int activePointerId = this.mActivePointerId;
                    if (activePointerId != -1) {
                        int pointerIndex = motionEvent.findPointerIndex(activePointerId);
                        if (pointerIndex == -1) {
                            Log.e(TAG, "Invalid pointerId=" + activePointerId + " in onInterceptTouchEvent");
                        } else {
                            int y = (int) motionEvent.getY(pointerIndex);
                            if (Math.abs(y - this.mLastMotionY) > this.mTouchSlop && (2 & getNestedScrollAxes()) == 0) {
                                this.mIsBeingDragged = true;
                                this.mLastMotionY = y;
                                initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(motionEvent);
                                this.mNestedYOffset = 0;
                                ViewParent parent = getParent();
                                if (parent != null) {
                                    parent.requestDisallowInterceptTouchEvent(true);
                                }
                            }
                        }
                    }
                } else if (i != 3) {
                    if (i == 6) {
                        onSecondaryPointerUp(ev);
                    }
                }
            }
            this.mIsBeingDragged = false;
            this.mActivePointerId = -1;
            recycleVelocityTracker();
            if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            stopNestedScroll(0);
        } else {
            int y2 = (int) ev.getY();
            if (!inChild((int) ev.getX(), y2)) {
                this.mIsBeingDragged = false;
                recycleVelocityTracker();
            } else {
                this.mLastMotionY = y2;
                this.mActivePointerId = motionEvent.getPointerId(0);
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(motionEvent);
                this.mScroller.computeScrollOffset();
                this.mIsBeingDragged = true ^ this.mScroller.isFinished();
                startNestedScroll(2, 0);
            }
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean z;
        ViewParent parent;
        int deltaY;
        MotionEvent motionEvent = ev;
        initVelocityTrackerIfNotExists();
        MotionEvent vtev = MotionEvent.obtain(ev);
        int actionMasked = ev.getActionMasked();
        if (actionMasked == 0) {
            this.mNestedYOffset = 0;
        }
        vtev.offsetLocation(0.0f, (float) this.mNestedYOffset);
        if (actionMasked == 0) {
            z = true;
            if (getChildCount() == 0) {
                return false;
            }
            boolean z2 = !this.mScroller.isFinished();
            this.mIsBeingDragged = z2;
            if (z2 && (parent = getParent()) != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            if (!this.mScroller.isFinished()) {
                this.mScroller.abortAnimation();
            }
            this.mLastMotionY = (int) ev.getY();
            this.mActivePointerId = motionEvent.getPointerId(0);
            startNestedScroll(2, 0);
        } else if (actionMasked == 1) {
            z = true;
            if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity(this.mActivePointerId);
                if (Math.abs(initialVelocity) > this.mMinimumVelocity) {
                    flingWithNestedDispatch(-initialVelocity);
                }
            }
            this.mActivePointerId = -1;
            endDrag();
        } else if (actionMasked == 2) {
            int activePointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
            if (activePointerIndex == -1) {
                Log.e(TAG, "Invalid pointerId=" + this.mActivePointerId + " in onTouchEvent");
                z = true;
            } else {
                int y = (int) motionEvent.getY(activePointerIndex);
                int deltaY2 = this.mLastMotionY - y;
                if (dispatchNestedPreScroll(0, deltaY2, this.mScrollConsumed, this.mScrollOffset, 0)) {
                    deltaY2 -= this.mScrollConsumed[1];
                    vtev.offsetLocation(0.0f, (float) this.mScrollOffset[1]);
                    this.mNestedYOffset += this.mScrollOffset[1];
                }
                if (!this.mIsBeingDragged && Math.abs(deltaY2) > this.mTouchSlop) {
                    ViewParent parent2 = getParent();
                    if (parent2 != null) {
                        parent2.requestDisallowInterceptTouchEvent(true);
                    }
                    this.mIsBeingDragged = true;
                    if (deltaY2 > 0) {
                        deltaY2 -= this.mTouchSlop;
                    } else {
                        deltaY2 += this.mTouchSlop;
                    }
                }
                if (this.mIsBeingDragged) {
                    this.mLastMotionY = y - this.mScrollOffset[1];
                    int oldY = getScrollY();
                    int range = getScrollRange();
                    if (oldY <= 0 && deltaY2 < 0) {
                        deltaY2 = (int) (((float) deltaY2) * getScrollRate(oldY, this.mOverScrollDistance));
                    }
                    if (oldY < range || deltaY2 <= 0) {
                        deltaY = deltaY2;
                    } else {
                        deltaY = (int) (((float) deltaY2) * getScrollRate(oldY - range, this.mOverScrollDistance));
                    }
                    int oldY2 = oldY;
                    int i = y;
                    int i2 = activePointerIndex;
                    z = true;
                    if (overScrollByCompat(0, deltaY, 0, getScrollY(), 0, range, 0, this.mOverScrollDistance, true) && !hasNestedScrollingParent(0)) {
                        this.mVelocityTracker.clear();
                    }
                    int scrolledDeltaY = getScrollY() - oldY2;
                    if (dispatchNestedScroll(0, scrolledDeltaY, 0, deltaY - scrolledDeltaY, this.mScrollOffset, 0)) {
                        int i3 = this.mLastMotionY;
                        int[] iArr = this.mScrollOffset;
                        this.mLastMotionY = i3 - iArr[1];
                        vtev.offsetLocation(0.0f, (float) iArr[1]);
                        this.mNestedYOffset += this.mScrollOffset[1];
                    }
                } else {
                    int i4 = activePointerIndex;
                    z = true;
                }
            }
        } else if (actionMasked == 3) {
            if (this.mIsBeingDragged != 0 && getChildCount() > 0 && this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
            this.mActivePointerId = -1;
            endDrag();
            z = true;
        } else if (actionMasked == 5) {
            int index = ev.getActionIndex();
            this.mLastMotionY = (int) motionEvent.getY(index);
            this.mActivePointerId = motionEvent.getPointerId(index);
            z = true;
        } else if (actionMasked != 6) {
            z = true;
        } else {
            onSecondaryPointerUp(ev);
            this.mLastMotionY = (int) motionEvent.getY(motionEvent.findPointerIndex(this.mActivePointerId));
            z = true;
        }
        VelocityTracker velocityTracker2 = this.mVelocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.addMovement(vtev);
        }
        vtev.recycle();
        return z;
    }

    private float getScrollRate(int scrollY, int totalDistance) {
        return INIT_RATE * (1.0f - this.interpolator.getInterpolation((((float) Math.abs(scrollY)) * 1.0f) / ((float) Math.abs(totalDistance))));
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = ev.getActionIndex();
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionY = (int) ev.getY(newPointerIndex);
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
            VelocityTracker velocityTracker = this.mVelocityTracker;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & 2) == 0 || event.getAction() != 8 || this.mIsBeingDragged) {
            return false;
        }
        float vscroll = event.getAxisValue(9);
        if (vscroll == 0.0f) {
            return false;
        }
        int range = getScrollRange();
        int oldScrollY = getScrollY();
        int newScrollY = oldScrollY - ((int) (getVerticalScrollFactorCompat() * vscroll));
        if (newScrollY < 0) {
            newScrollY = 0;
        } else if (newScrollY > range) {
            newScrollY = range;
        }
        if (newScrollY == oldScrollY) {
            return false;
        }
        super.scrollTo(getScrollX(), newScrollY);
        return true;
    }

    private float getVerticalScrollFactorCompat() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            Context context = getContext();
            if (context.getTheme().resolveAttribute(16842829, outValue, true)) {
                this.mVerticalScrollFactor = outValue.getDimension(context.getResources().getDisplayMetrics());
            } else {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
        }
        return this.mVerticalScrollFactor;
    }

    /* access modifiers changed from: protected */
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (!this.mScroller.isFinished()) {
            super.scrollTo(scrollX, scrollY);
            if (clampedY) {
                this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange());
                return;
            }
            return;
        }
        super.scrollTo(scrollX, scrollY);
    }

    /* access modifiers changed from: package-private */
    public boolean overScrollByCompat(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int maxOverScrollX2;
        int maxOverScrollY2;
        boolean clampedY;
        int overScrollMode = getOverScrollMode();
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || (overScrollMode == 1 && canScrollHorizontal);
        boolean overScrollVertical = overScrollMode == 0 || (overScrollMode == 1 && canScrollVertical);
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX2 = 0;
        } else {
            maxOverScrollX2 = maxOverScrollX;
        }
        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY2 = 0;
        } else {
            maxOverScrollY2 = maxOverScrollY;
        }
        int left = -maxOverScrollX2;
        int right = maxOverScrollX2 + scrollRangeX;
        int top = -maxOverScrollY2;
        int bottom = maxOverScrollY2 + scrollRangeY;
        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        } else {
            clampedY = false;
        }
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);
        return clampedX || clampedY;
    }

    /* access modifiers changed from: package-private */
    public int getScrollRange() {
        if (getChildCount() > 0) {
            return Math.max(0, getChildAt(0).getHeight() - ((getHeight() - getPaddingBottom()) - getPaddingTop()));
        }
        return 0;
    }

    private View findFocusableViewInBounds(boolean topFocus, int top, int bottom) {
        List<View> focusables = getFocusables(2);
        View focusCandidate = null;
        boolean foundFullyContainedFocusable = false;
        int count = focusables.size();
        for (int i = 0; i < count; i++) {
            View view = focusables.get(i);
            int viewTop = view.getTop();
            int viewBottom = view.getBottom();
            if (top < viewBottom && viewTop < bottom) {
                boolean viewIsCloserToBoundary = false;
                boolean viewIsFullyContained = top < viewTop && viewBottom < bottom;
                if (focusCandidate == null) {
                    focusCandidate = view;
                    foundFullyContainedFocusable = viewIsFullyContained;
                } else {
                    if ((topFocus && viewTop < focusCandidate.getTop()) || (!topFocus && viewBottom > focusCandidate.getBottom())) {
                        viewIsCloserToBoundary = true;
                    }
                    if (foundFullyContainedFocusable) {
                        if (viewIsFullyContained && viewIsCloserToBoundary) {
                            focusCandidate = view;
                        }
                    } else if (viewIsFullyContained) {
                        focusCandidate = view;
                        foundFullyContainedFocusable = true;
                    } else if (viewIsCloserToBoundary) {
                        focusCandidate = view;
                    }
                }
            }
        }
        return focusCandidate;
    }

    public boolean pageScroll(int direction) {
        boolean down = direction == 130;
        int height = getHeight();
        if (down) {
            this.mTempRect.top = getScrollY() + height;
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                if (this.mTempRect.top + height > view.getBottom()) {
                    this.mTempRect.top = view.getBottom() - height;
                }
            }
        } else {
            this.mTempRect.top = getScrollY() - height;
            if (this.mTempRect.top < 0) {
                this.mTempRect.top = 0;
            }
        }
        Rect rect = this.mTempRect;
        rect.bottom = rect.top + height;
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    public boolean fullScroll(int direction) {
        int count;
        boolean down = direction == 130;
        int height = getHeight();
        this.mTempRect.top = 0;
        this.mTempRect.bottom = height;
        if (down && (count = getChildCount()) > 0) {
            this.mTempRect.bottom = getChildAt(count - 1).getBottom() + getPaddingBottom();
            Rect rect = this.mTempRect;
            rect.top = rect.bottom - height;
        }
        return scrollAndFocus(direction, this.mTempRect.top, this.mTempRect.bottom);
    }

    private boolean scrollAndFocus(int direction, int top, int bottom) {
        boolean handled = true;
        int height = getHeight();
        int containerTop = getScrollY();
        int containerBottom = containerTop + height;
        boolean up = direction == 33;
        View newFocused = findFocusableViewInBounds(up, top, bottom);
        if (newFocused == null) {
            newFocused = this;
        }
        if (top < containerTop || bottom > containerBottom) {
            doScrollY(up ? top - containerTop : bottom - containerBottom);
        } else {
            handled = false;
        }
        if (newFocused != findFocus()) {
            newFocused.requestFocus(direction);
        }
        return handled;
    }

    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this) {
            currentFocused = null;
        }
        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
        int maxJump = getMaxScrollAmount();
        if (nextFocused == null || !isWithinDeltaOfScreen(nextFocused, maxJump, getHeight())) {
            int scrollDelta = maxJump;
            if (direction == 33 && getScrollY() < scrollDelta) {
                scrollDelta = getScrollY();
            } else if (direction == 130 && getChildCount() > 0) {
                int daBottom = getChildAt(0).getBottom();
                int screenBottom = (getScrollY() + getHeight()) - getPaddingBottom();
                if (daBottom - screenBottom < maxJump) {
                    scrollDelta = daBottom - screenBottom;
                }
            }
            if (scrollDelta == 0) {
                return false;
            }
            doScrollY(direction == 130 ? scrollDelta : -scrollDelta);
        } else {
            nextFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(nextFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
            nextFocused.requestFocus(direction);
        }
        if (currentFocused == null || !currentFocused.isFocused() || !isOffScreen(currentFocused)) {
            return true;
        }
        int descendantFocusability = getDescendantFocusability();
        setDescendantFocusability(131072);
        requestFocus();
        setDescendantFocusability(descendantFocusability);
        return true;
    }

    private boolean isOffScreen(View descendant) {
        return !isWithinDeltaOfScreen(descendant, 0, getHeight());
    }

    private boolean isWithinDeltaOfScreen(View descendant, int delta, int height) {
        descendant.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        return this.mTempRect.bottom + delta >= getScrollY() && this.mTempRect.top - delta <= getScrollY() + height;
    }

    private void doScrollY(int delta) {
        if (delta == 0) {
            return;
        }
        if (this.mSmoothScrollingEnabled) {
            smoothScrollBy(0, delta);
        } else {
            scrollBy(0, delta);
        }
    }

    public final void smoothScrollBy(int dx, int dy) {
        if (getChildCount() != 0) {
            if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
                int maxY = Math.max(0, getChildAt(0).getHeight() - ((getHeight() - getPaddingBottom()) - getPaddingTop()));
                int scrollY = getScrollY();
                this.mScroller.startScroll(getScrollX(), scrollY, 0, Math.max(0, Math.min(scrollY + dy, maxY)) - scrollY);
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                scrollBy(dx, dy);
            }
            this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
        }
    }

    public final void smoothScrollTo(int x, int y) {
        smoothScrollBy(x - getScrollX(), y - getScrollY());
    }

    public int computeVerticalScrollRange() {
        int count = getChildCount();
        int contentHeight = (getHeight() - getPaddingBottom()) - getPaddingTop();
        if (count == 0) {
            return contentHeight;
        }
        int scrollRange = getChildAt(0).getBottom();
        int scrollY = getScrollY();
        int overscrollBottom = Math.max(0, scrollRange - contentHeight);
        if (scrollY < 0) {
            return scrollRange - scrollY;
        }
        if (scrollY > overscrollBottom) {
            return scrollRange + (scrollY - overscrollBottom);
        }
        return scrollRange;
    }

    public int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    /* access modifiers changed from: protected */
    public void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), child.getLayoutParams().width), View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    /* access modifiers changed from: protected */
    public void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        child.measure(getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed, lp.width), View.MeasureSpec.makeMeasureSpec(lp.topMargin + lp.bottomMargin, 0));
    }

    public void computeScroll() {
        int dy;
        if (this.mScroller.computeScrollOffset()) {
            int oldY = getScrollY();
            int y = this.mScroller.getCurrY();
            int dy2 = y - oldY;
            if (dispatchNestedPreScroll(0, dy2, this.mScrollConsumed, (int[]) null, 1)) {
                dy = dy2 - this.mScrollConsumed[1];
            } else {
                dy = dy2;
            }
            if (dy != 0) {
                int range = getScrollRange();
                int oldScrollY = getScrollY();
                overScrollByCompat(0, dy, getScrollX(), oldScrollY, 0, range, 0, this.mOverScrollDistance, false);
                int scrolledDeltaY = getScrollY() - oldScrollY;
                dispatchNestedScroll(0, scrolledDeltaY, 0, dy - scrolledDeltaY, (int[]) null, 1);
            }
            this.mLastScrollerY = y;
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }
        if (hasNestedScrollingParent(1)) {
            stopNestedScroll(1);
        }
        this.mLastScrollerY = 0;
    }

    private void scrollToChild(View child) {
        child.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(child, this.mTempRect);
        int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
        if (scrollDelta != 0) {
            scrollBy(0, scrollDelta);
        }
    }

    private boolean scrollToChildRect(Rect rect, boolean immediate) {
        int delta = computeScrollDeltaToGetChildRectOnScreen(rect);
        boolean scroll = delta != 0;
        if (scroll) {
            if (immediate) {
                scrollBy(0, delta);
            } else {
                smoothScrollBy(0, delta);
            }
        }
        return scroll;
    }

    /* access modifiers changed from: protected */
    public int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        int scrollYDelta;
        int scrollYDelta2;
        if (getChildCount() == 0) {
            return 0;
        }
        int height = getHeight();
        int screenTop = getScrollY();
        int screenBottom = screenTop + height;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (rect.top > 0) {
            screenTop += fadingEdge;
        }
        if (rect.bottom < getChildAt(0).getHeight()) {
            screenBottom -= fadingEdge;
        }
        if (rect.bottom > screenBottom && rect.top > screenTop) {
            if (rect.height() > height) {
                scrollYDelta2 = 0 + (rect.top - screenTop);
            } else {
                scrollYDelta2 = 0 + (rect.bottom - screenBottom);
            }
            return Math.min(scrollYDelta2, getChildAt(0).getBottom() - screenBottom);
        } else if (rect.top >= screenTop || rect.bottom >= screenBottom) {
            return 0;
        } else {
            if (rect.height() > height) {
                scrollYDelta = 0 - (screenBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (screenTop - rect.top);
            }
            return Math.max(scrollYDelta, -getScrollY());
        }
    }

    public void requestChildFocus(View child, View focused) {
        if (!this.mIsLayoutDirty) {
            scrollToChild(focused);
        } else {
            this.mChildToScrollTo = focused;
        }
        super.requestChildFocus(child, focused);
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View nextFocus;
        if (direction == 2) {
            direction = TsExtractor.TS_STREAM_TYPE_HDMV_DTS;
        } else if (direction == 1) {
            direction = 33;
        }
        if (previouslyFocusedRect == null) {
            nextFocus = FocusFinder.getInstance().findNextFocus(this, (View) null, direction);
        } else {
            nextFocus = FocusFinder.getInstance().findNextFocusFromRect(this, previouslyFocusedRect, direction);
        }
        if (nextFocus != null && !isOffScreen(nextFocus)) {
            return nextFocus.requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
        return scrollToChildRect(rectangle, immediate);
    }

    public void requestLayout() {
        this.mIsLayoutDirty = true;
        super.requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mIsLayoutDirty = false;
        View view = this.mChildToScrollTo;
        if (view != null && isViewDescendantOf(view, this)) {
            scrollToChild(this.mChildToScrollTo);
        }
        this.mChildToScrollTo = null;
        if (!this.mIsLaidOut) {
            if (this.mSavedState != null) {
                scrollTo(getScrollX(), this.mSavedState.scrollPosition);
                this.mSavedState = null;
            }
            int scrollRange = Math.max(0, (getChildCount() > 0 ? getChildAt(0).getMeasuredHeight() : 0) - (((b - t) - getPaddingBottom()) - getPaddingTop()));
            if (getScrollY() > scrollRange) {
                scrollTo(getScrollX(), scrollRange);
            } else if (getScrollY() < 0) {
                scrollTo(getScrollX(), 0);
            }
        }
        scrollTo(getScrollX(), getScrollY());
        this.mIsLaidOut = true;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsLaidOut = false;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View currentFocused = findFocus();
        if (currentFocused != null && this != currentFocused && isWithinDeltaOfScreen(currentFocused, 0, oldh)) {
            currentFocused.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(currentFocused, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
        }
    }

    private static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        if (!(theParent instanceof ViewGroup) || !isViewDescendantOf((View) theParent, parent)) {
            return false;
        }
        return true;
    }

    public void fling(int velocityY) {
        if (getChildCount() > 0) {
            startNestedScroll(2, 1);
            this.mScroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, getScrollRange(), 0, this.mOverFlingDistance);
            this.mLastScrollerY = getScrollY();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void flingWithNestedDispatch(int velocityY) {
        int scrollY = getScrollY();
        boolean canFling = (scrollY > 0 || velocityY > 0) && (scrollY < getScrollRange() || velocityY < 0);
        if (!dispatchNestedPreFling(0.0f, (float) velocityY)) {
            dispatchNestedFling(0.0f, (float) velocityY, canFling);
            fling(velocityY);
        }
    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
        stopNestedScroll(0);
    }

    public void scrollTo(int x, int y) {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            int x2 = clamp(x, (getWidth() - getPaddingRight()) - getPaddingLeft(), child.getWidth());
            int y2 = clamp(y, (getHeight() - getPaddingBottom()) - getPaddingTop(), child.getHeight());
            if (x2 != getScrollX() || y2 != getScrollY()) {
                super.scrollTo(x2, y2);
            }
        }
    }

    private static int clamp(int n, int my, int child) {
        if (my >= child || n < 0) {
            return 0;
        }
        if (my + n > child) {
            return child - my;
        }
        return n;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mSavedState = ss;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.scrollPosition = getScrollY();
        return ss;
    }

    static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public int scrollPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source) {
            super(source);
            this.scrollPosition = source.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.scrollPosition);
        }

        public String toString() {
            return "HorizontalScrollView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " scrollPosition=" + this.scrollPosition + "}";
        }
    }

    static class AccessibilityDelegate extends AccessibilityDelegateCompat {
        AccessibilityDelegate() {
        }

        public boolean performAccessibilityAction(View host, int action, Bundle arguments) {
            if (super.performAccessibilityAction(host, action, arguments)) {
                return true;
            }
            MyScrollView nsvHost = (MyScrollView) host;
            if (!nsvHost.isEnabled()) {
                return false;
            }
            if (action == 4096) {
                int targetScrollY = Math.min(nsvHost.getScrollY() + ((nsvHost.getHeight() - nsvHost.getPaddingBottom()) - nsvHost.getPaddingTop()), nsvHost.getScrollRange());
                if (targetScrollY == nsvHost.getScrollY()) {
                    return false;
                }
                nsvHost.smoothScrollTo(0, targetScrollY);
                return true;
            } else if (action != 8192) {
                return false;
            } else {
                int targetScrollY2 = Math.max(nsvHost.getScrollY() - ((nsvHost.getHeight() - nsvHost.getPaddingBottom()) - nsvHost.getPaddingTop()), 0);
                if (targetScrollY2 == nsvHost.getScrollY()) {
                    return false;
                }
                nsvHost.smoothScrollTo(0, targetScrollY2);
                return true;
            }
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            int scrollRange;
            super.onInitializeAccessibilityNodeInfo(host, info);
            MyScrollView nsvHost = (MyScrollView) host;
            info.setClassName(ScrollView.class.getName());
            if (nsvHost.isEnabled() && (scrollRange = nsvHost.getScrollRange()) > 0) {
                info.setScrollable(true);
                if (nsvHost.getScrollY() > 0) {
                    info.addAction(8192);
                }
                if (nsvHost.getScrollY() < scrollRange) {
                    info.addAction(4096);
                }
            }
        }

        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            MyScrollView nsvHost = (MyScrollView) host;
            event.setClassName(ScrollView.class.getName());
            event.setScrollable(nsvHost.getScrollRange() > 0);
            event.setScrollX(nsvHost.getScrollX());
            event.setScrollY(nsvHost.getScrollY());
            AccessibilityRecordCompat.setMaxScrollX(event, nsvHost.getScrollX());
            AccessibilityRecordCompat.setMaxScrollY(event, nsvHost.getScrollRange());
        }
    }

    public void setOverScrollDistance(int overScrollDistance) {
        this.mOverScrollDistance = overScrollDistance;
    }

    public void setOverFlingDistance(int overFlingDistance) {
        this.mOverFlingDistance = overFlingDistance;
    }
}
