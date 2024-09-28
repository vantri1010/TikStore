package im.bclpbkiauv.ui.hviews.behavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.AbsSavedState;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPagerUtils;
import im.bclpbkiauv.messenger.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class ViewPagerBottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    int mActivePointerId;
    private BottomSheetCallback mCallback;
    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        public boolean tryCaptureView(View child, int pointerId) {
            View scroll;
            if (ViewPagerBottomSheetBehavior.this.mState == 1 || ViewPagerBottomSheetBehavior.this.mTouchingScrollingChild) {
                return false;
            }
            if ((ViewPagerBottomSheetBehavior.this.mState != 3 || ViewPagerBottomSheetBehavior.this.mActivePointerId != pointerId || (scroll = (View) ViewPagerBottomSheetBehavior.this.mNestedScrollingChildRef.get()) == null || !scroll.canScrollVertically(-1)) && ViewPagerBottomSheetBehavior.this.mViewRef != null && ViewPagerBottomSheetBehavior.this.mViewRef.get() == child) {
                return true;
            }
            return false;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            ViewPagerBottomSheetBehavior.this.dispatchOnSlide(top);
        }

        public void onViewDragStateChanged(int state) {
            if (state == 1) {
                ViewPagerBottomSheetBehavior.this.setStateInternal(1);
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;
            int currentTop;
            if (yvel < 0.0f && Math.abs(yvel) > ViewPagerBottomSheetBehavior.this.mMinimumVelocity && Math.abs(yvel) > Math.abs(xvel)) {
                currentTop = ViewPagerBottomSheetBehavior.this.mMinOffset;
                top = 3;
            } else if (ViewPagerBottomSheetBehavior.this.mHideable && ViewPagerBottomSheetBehavior.this.shouldHide(releasedChild, yvel)) {
                currentTop = ViewPagerBottomSheetBehavior.this.mParentHeight;
                top = 5;
            } else if (yvel <= 0.0f || Math.abs(yvel) <= ViewPagerBottomSheetBehavior.this.mMinimumVelocity || Math.abs(yvel) <= Math.abs(xvel)) {
                int currentTop2 = releasedChild.getTop();
                if (Math.abs(currentTop2 - ViewPagerBottomSheetBehavior.this.mMinOffset) < Math.abs(currentTop2 - ViewPagerBottomSheetBehavior.this.mMaxOffset)) {
                    currentTop = ViewPagerBottomSheetBehavior.this.mMinOffset;
                    top = 3;
                } else {
                    currentTop = ViewPagerBottomSheetBehavior.this.mMaxOffset;
                    top = 4;
                }
            } else {
                currentTop = ViewPagerBottomSheetBehavior.this.mMaxOffset;
                top = 4;
            }
            if (ViewPagerBottomSheetBehavior.this.mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), currentTop)) {
                ViewPagerBottomSheetBehavior.this.setStateInternal(2);
                ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, top));
                return;
            }
            ViewPagerBottomSheetBehavior.this.setStateInternal(top);
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return MathUtils.clamp(top, ViewPagerBottomSheetBehavior.this.mMinOffset, ViewPagerBottomSheetBehavior.this.mHideable ? ViewPagerBottomSheetBehavior.this.mParentHeight : ViewPagerBottomSheetBehavior.this.mMaxOffset);
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(View child) {
            if (ViewPagerBottomSheetBehavior.this.mHideable) {
                return ViewPagerBottomSheetBehavior.this.mParentHeight - ViewPagerBottomSheetBehavior.this.mMinOffset;
            }
            return ViewPagerBottomSheetBehavior.this.mMaxOffset - ViewPagerBottomSheetBehavior.this.mMinOffset;
        }
    };
    boolean mHideable;
    private boolean mIgnoreEvents;
    private int mInitialY;
    int mMaxOffset;
    private float mMaximumVelocity;
    int mMinOffset;
    /* access modifiers changed from: private */
    public float mMinimumVelocity;
    private boolean mNestedScrolled;
    WeakReference<View> mNestedScrollingChildRef;
    int mParentHeight;
    private int mPeekHeight;
    private boolean mPeekHeightAuto;
    private int mPeekHeightMin;
    private boolean mSkipCollapsed;
    int mState = 4;
    boolean mTouchingScrollingChild;
    private VelocityTracker mVelocityTracker;
    ViewDragHelper mViewDragHelper;
    WeakReference<V> mViewRef;

    public static abstract class BottomSheetCallback {
        public abstract void onSlide(View view, float f);

        public abstract void onStateChanged(View view, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public ViewPagerBottomSheetBehavior() {
    }

    public ViewPagerBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(2);
        if (value == null || value.data != -1) {
            setPeekHeight(a.getDimensionPixelSize(2, -1));
        } else {
            setPeekHeight(value.data);
        }
        setHideable(a.getBoolean(1, false));
        setSkipCollapsed(a.getBoolean(3, false));
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mMaximumVelocity = (float) configuration.getScaledMaximumFlingVelocity();
        this.mMinimumVelocity = (float) configuration.getScaledMinimumFlingVelocity();
    }

    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), this.mState);
    }

    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state == 1 || ss.state == 2) {
            this.mState = 4;
        } else {
            this.mState = ss.state;
        }
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int peekHeight;
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            ViewCompat.setFitsSystemWindows(child, true);
        }
        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.mParentHeight = parent.getHeight();
        if (this.mPeekHeightAuto) {
            if (this.mPeekHeightMin == 0) {
                this.mPeekHeightMin = parent.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            }
            peekHeight = Math.max(this.mPeekHeightMin, this.mParentHeight - ((parent.getWidth() * 9) / 16));
        } else {
            peekHeight = this.mPeekHeight;
        }
        int max = Math.max(0, this.mParentHeight - child.getHeight());
        this.mMinOffset = max;
        this.mMaxOffset = Math.max(this.mParentHeight - peekHeight, max);
        int i = this.mState;
        if (i == 3) {
            ViewCompat.offsetTopAndBottom(child, this.mMinOffset);
        } else if (!this.mHideable || i != 5) {
            int i2 = this.mState;
            if (i2 == 4) {
                ViewCompat.offsetTopAndBottom(child, this.mMaxOffset);
            } else if (i2 == 1 || i2 == 2) {
                ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
            }
        } else {
            ViewCompat.offsetTopAndBottom(child, this.mParentHeight);
        }
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = ViewDragHelper.create(parent, this.mDragCallback);
        }
        this.mViewRef = new WeakReference<>(child);
        this.mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        return true;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            this.mIgnoreEvents = true;
            return false;
        }
        int action = event.getActionMasked();
        if (action == 0) {
            reset();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        if (action == 0) {
            int initialX = (int) event.getX();
            this.mInitialY = (int) event.getY();
            WeakReference<View> weakReference = this.mNestedScrollingChildRef;
            View scroll = weakReference != null ? (View) weakReference.get() : null;
            if (scroll != null && parent.isPointInChildBounds(scroll, initialX, this.mInitialY)) {
                this.mActivePointerId = event.getPointerId(event.getActionIndex());
                this.mTouchingScrollingChild = true;
            }
            this.mIgnoreEvents = this.mActivePointerId == -1 && !parent.isPointInChildBounds(child, initialX, this.mInitialY);
        } else if (action == 1 || action == 3) {
            this.mTouchingScrollingChild = false;
            this.mActivePointerId = -1;
            if (this.mIgnoreEvents) {
                this.mIgnoreEvents = false;
                return false;
            }
        }
        if (!this.mIgnoreEvents && this.mViewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        View scroll2 = (View) this.mNestedScrollingChildRef.get();
        if (action != 2 || scroll2 == null || this.mIgnoreEvents || this.mState == 1 || parent.isPointInChildBounds(scroll2, (int) event.getX(), (int) event.getY()) || Math.abs(((float) this.mInitialY) - event.getY()) <= ((float) this.mViewDragHelper.getTouchSlop())) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = event.getActionMasked();
        if (this.mState == 1 && action == 0) {
            return true;
        }
        ViewDragHelper viewDragHelper = this.mViewDragHelper;
        if (viewDragHelper != null) {
            viewDragHelper.processTouchEvent(event);
        }
        if (action == 0) {
            reset();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        if (action == 2 && !this.mIgnoreEvents && Math.abs(((float) this.mInitialY) - event.getY()) > ((float) this.mViewDragHelper.getTouchSlop())) {
            this.mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
        }
        return !this.mIgnoreEvents;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View directTargetChild, View target, int nestedScrollAxes) {
        this.mNestedScrolled = false;
        if ((nestedScrollAxes & 2) != 0) {
            return true;
        }
        return false;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        if (target == ((View) this.mNestedScrollingChildRef.get())) {
            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            if (dy > 0) {
                int i = this.mMinOffset;
                if (newTop < i) {
                    consumed[1] = currentTop - i;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(3);
                } else {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                }
            } else if (dy < 0 && !target.canScrollVertically(-1)) {
                int i2 = this.mMaxOffset;
                if (newTop <= i2 || this.mHideable) {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(1);
                } else {
                    consumed[1] = currentTop - i2;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(4);
                }
            }
            dispatchOnSlide(child.getTop());
            this.mNestedScrolled = true;
        }
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        int top;
        int currentTop;
        if (child.getTop() == this.mMinOffset) {
            setStateInternal(3);
            return;
        }
        WeakReference<View> weakReference = this.mNestedScrollingChildRef;
        if (weakReference != null && target == weakReference.get() && this.mNestedScrolled) {
            this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
            float xVel = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
            float yVel = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
            if (yVel < 0.0f && Math.abs(yVel) > this.mMinimumVelocity && Math.abs(yVel) > Math.abs(xVel)) {
                currentTop = this.mMinOffset;
                top = 3;
            } else if (this.mHideable != 0 && shouldHide(child, yVel)) {
                currentTop = this.mParentHeight;
                top = 5;
            } else if (yVel <= 0.0f || Math.abs(yVel) <= this.mMinimumVelocity || Math.abs(yVel) <= Math.abs(xVel)) {
                int currentTop2 = child.getTop();
                if (Math.abs(currentTop2 - this.mMinOffset) < Math.abs(currentTop2 - this.mMaxOffset)) {
                    currentTop = this.mMinOffset;
                    top = 3;
                } else {
                    currentTop = this.mMaxOffset;
                    top = 4;
                }
            } else {
                currentTop = this.mMaxOffset;
                top = 4;
            }
            if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), currentTop)) {
                setStateInternal(2);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, top));
            } else {
                setStateInternal(top);
            }
            this.mNestedScrolled = false;
        }
    }

    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return target == this.mNestedScrollingChildRef.get() && (this.mState != 3 || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    /* access modifiers changed from: package-private */
    public void invalidateScrollingChild() {
        this.mNestedScrollingChildRef = new WeakReference<>(findScrollingChild((View) this.mViewRef.get()));
    }

    public final void setPeekHeight(int peekHeight) {
        WeakReference<V> weakReference;
        V view;
        boolean layout = false;
        if (peekHeight == -1) {
            if (!this.mPeekHeightAuto) {
                this.mPeekHeightAuto = true;
                layout = true;
            }
        } else if (this.mPeekHeightAuto || this.mPeekHeight != peekHeight) {
            this.mPeekHeightAuto = false;
            this.mPeekHeight = Math.max(0, peekHeight);
            this.mMaxOffset = this.mParentHeight - peekHeight;
            layout = true;
        }
        if (layout && this.mState == 4 && (weakReference = this.mViewRef) != null && (view = (View) weakReference.get()) != null) {
            view.requestLayout();
        }
    }

    public final int getPeekHeight() {
        if (this.mPeekHeightAuto) {
            return -1;
        }
        return this.mPeekHeight;
    }

    public void setHideable(boolean hideable) {
        this.mHideable = hideable;
    }

    public boolean isHideable() {
        return this.mHideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.mSkipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return this.mSkipCollapsed;
    }

    public void setBottomSheetCallback(BottomSheetCallback callback) {
        this.mCallback = callback;
    }

    public final void setState(final int state) {
        if (state != this.mState) {
            WeakReference<V> weakReference = this.mViewRef;
            if (weakReference != null) {
                final V child = (View) weakReference.get();
                if (child != null) {
                    ViewParent parent = child.getParent();
                    if (parent == null || !parent.isLayoutRequested() || !ViewCompat.isAttachedToWindow(child)) {
                        startSettlingAnimation(child, state);
                    } else {
                        child.post(new Runnable() {
                            public void run() {
                                ViewPagerBottomSheetBehavior.this.startSettlingAnimation(child, state);
                            }
                        });
                    }
                }
            } else if (state == 4 || state == 3 || (this.mHideable && state == 5)) {
                this.mState = state;
            }
        }
    }

    public final int getState() {
        return this.mState;
    }

    /* access modifiers changed from: package-private */
    public void setStateInternal(int state) {
        BottomSheetCallback bottomSheetCallback;
        if (this.mState != state) {
            this.mState = state;
            View bottomSheet = (View) this.mViewRef.get();
            if (bottomSheet != null && (bottomSheetCallback = this.mCallback) != null) {
                bottomSheetCallback.onStateChanged(bottomSheet, state);
            }
        }
    }

    private void reset() {
        this.mActivePointerId = -1;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldHide(View child, float yvel) {
        if (this.mSkipCollapsed) {
            return true;
        }
        if (child.getTop() >= this.mMaxOffset && Math.abs((((float) child.getTop()) + (0.1f * yvel)) - ((float) this.mMaxOffset)) / ((float) this.mPeekHeight) > 0.5f) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public View findScrollingChild(View view) {
        View scrollingChild;
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (view instanceof ViewPager) {
            View currentViewPagerChild = ViewPagerUtils.getCurrentView((ViewPager) view);
            if (!(currentViewPagerChild == null || (scrollingChild = findScrollingChild(currentViewPagerChild)) == null)) {
                return scrollingChild;
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                View scrollingChild2 = findScrollingChild(group.getChildAt(i));
                if (scrollingChild2 != null) {
                    return scrollingChild2;
                }
            }
        }
        return null;
    }

    private float getYVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        return this.mVelocityTracker.getYVelocity(this.mActivePointerId);
    }

    /* access modifiers changed from: package-private */
    public void startSettlingAnimation(View child, int state) {
        int top;
        if (state == 4) {
            top = this.mMaxOffset;
        } else if (state == 3) {
            top = this.mMinOffset;
        } else if (this.mHideable == 0 || state != 5) {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        } else {
            top = this.mParentHeight;
        }
        if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            setStateInternal(2);
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
            return;
        }
        setStateInternal(state);
    }

    /* access modifiers changed from: package-private */
    public void dispatchOnSlide(int top) {
        BottomSheetCallback bottomSheetCallback;
        View bottomSheet = (View) this.mViewRef.get();
        if (bottomSheet != null && (bottomSheetCallback = this.mCallback) != null) {
            int i = this.mMaxOffset;
            if (top > i) {
                bottomSheetCallback.onSlide(bottomSheet, ((float) (i - top)) / ((float) (this.mParentHeight - i)));
            } else {
                bottomSheetCallback.onSlide(bottomSheet, ((float) (i - top)) / ((float) (i - this.mMinOffset)));
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getPeekHeightMin() {
        return this.mPeekHeightMin;
    }

    private class SettleRunnable implements Runnable {
        private final int mTargetState;
        private final View mView;

        SettleRunnable(View view, int targetState) {
            this.mView = view;
            this.mTargetState = targetState;
        }

        public void run() {
            if (ViewPagerBottomSheetBehavior.this.mViewDragHelper == null || !ViewPagerBottomSheetBehavior.this.mViewDragHelper.continueSettling(true)) {
                ViewPagerBottomSheetBehavior.this.setStateInternal(this.mTargetState);
            } else {
                ViewCompat.postOnAnimation(this.mView, this);
            }
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, (ClassLoader) null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        final int state;

        public SavedState(Parcel source) {
            this(source, (ClassLoader) null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state2) {
            super(superState);
            this.state = state2;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }
    }

    public static <V extends View> ViewPagerBottomSheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof ViewPagerBottomSheetBehavior) {
                return (ViewPagerBottomSheetBehavior) behavior;
            }
            throw new IllegalArgumentException("The view is not associated with ViewPagerBottomSheetBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
}
