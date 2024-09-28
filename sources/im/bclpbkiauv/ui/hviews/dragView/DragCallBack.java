package im.bclpbkiauv.ui.hviews.dragView;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.customview.widget.ViewDragHelper;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class DragCallBack extends ViewDragHelper.Callback {
    public static final int SIDE_BOTTOM = 4;
    public static final int SIDE_LEFT = 1;
    public static final int SIDE_NONE = 0;
    public static final int SIDE_RIGHT = 3;
    public static final int SIDE_TOP = 2;
    protected boolean mAllowDragOutParentBorder = false;
    protected boolean mAllowSideToTopOrBottom;
    protected boolean mAllowTopToStatusBar;
    protected boolean mAutoBackBorderAfterRelease = true;
    protected View mCapturedView;
    protected int mCapturedViewLastBottom;
    protected int mCapturedViewLastLeft;
    protected int mCapturedViewLastRight;
    protected int mCapturedViewLastTop;
    protected boolean mCapturedViewPositionHasChanged;
    protected int mCloseToSideWhenViewRealeased = 0;
    protected ViewDragHelper mHelper;
    protected boolean mIsDraging;
    protected List<Rect> mNotchRects;
    protected View mParent;

    @Retention(RetentionPolicy.SOURCE)
    @interface ColseToSide {
    }

    public DragCallBack(View parent, View capturedView) {
        this.mParent = parent;
        this.mCapturedView = capturedView;
    }

    public boolean tryCaptureView(View child, int pointerId) {
        boolean result = false;
        this.mCloseToSideWhenViewRealeased = 0;
        if (this.mCapturedView == child) {
            result = true;
        }
        if (result) {
            this.mIsDraging = result;
        }
        return result;
    }

    public int clampViewPositionHorizontal(View child, int left, int dx) {
        if (this.mParent == null || this.mAllowDragOutParentBorder || !(child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            return left;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        int leftBorder = this.mParent.getPaddingLeft() + lp.leftMargin;
        return Math.min(Math.max(leftBorder, left), ((this.mParent.getMeasuredWidth() - this.mParent.getPaddingRight()) - child.getMeasuredWidth()) - lp.rightMargin);
    }

    public int clampViewPositionVertical(View child, int top, int dy) {
        if (this.mParent == null || this.mAllowDragOutParentBorder || !(child.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            return top;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        int topBorder = this.mParent.getPaddingTop() + lp.topMargin;
        return Math.min(Math.max(topBorder, top), ((this.mParent.getMeasuredHeight() - this.mParent.getPaddingBottom()) - child.getMeasuredHeight()) - lp.bottomMargin);
    }

    public int getViewHorizontalDragRange(View child) {
        View view = this.mParent;
        if (view != null) {
            return view.getMeasuredWidth() - child.getMeasuredWidth();
        }
        return 0;
    }

    public int getViewVerticalDragRange(View child) {
        View view = this.mParent;
        if (view != null) {
            return view.getMeasuredHeight() - child.getMeasuredHeight();
        }
        return 0;
    }

    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        super.onViewPositionChanged(changedView, left, top, dx, dy);
        this.mCapturedViewPositionHasChanged = true;
        this.mCapturedViewLastLeft = left;
        this.mCapturedViewLastTop = top;
        this.mCapturedViewLastRight = changedView.getRight();
        int bottom = changedView.getBottom();
        this.mCapturedViewLastBottom = bottom;
        changedView.layout(this.mCapturedViewLastLeft, this.mCapturedViewLastTop, this.mCapturedViewLastRight, bottom);
        log("onViewPositionChanged", "child  = " + changedView + " , left = " + left + " , top = " + top + " , right = " + this.mCapturedViewLastRight + " , bottom = " + this.mCapturedViewLastBottom);
    }

    public void onViewReleased(View releasedChild, float xvel, float yvel) {
        int top;
        int left;
        boolean toTB = false;
        this.mIsDraging = false;
        super.onViewReleased(releasedChild, xvel, yvel);
        if (this.mAutoBackBorderAfterRelease && releasedChild == this.mCapturedView && this.mParent != null) {
            if (Math.min(releasedChild.getTop(), this.mParent.getBottom() - releasedChild.getBottom()) <= Math.min(releasedChild.getLeft(), this.mParent.getRight() - releasedChild.getRight()) && this.mAllowSideToTopOrBottom) {
                toTB = true;
            }
            if (toTB) {
                left = releasedChild.getLeft();
                if (releasedChild.getTop() <= this.mParent.getBottom() - releasedChild.getBottom()) {
                    if (this.mAllowTopToStatusBar) {
                        top = 0;
                    } else {
                        top = AndroidUtilities.statusBarHeight;
                    }
                    this.mCloseToSideWhenViewRealeased = 2;
                } else {
                    this.mCloseToSideWhenViewRealeased = 4;
                    top = this.mParent.getBottom() - releasedChild.getMeasuredHeight();
                }
            } else {
                int top2 = releasedChild.getTop();
                if (releasedChild.getLeft() <= this.mParent.getRight() - releasedChild.getRight()) {
                    this.mCloseToSideWhenViewRealeased = 1;
                    left = 0;
                    top = top2;
                } else {
                    left = this.mParent.getRight() - releasedChild.getMeasuredWidth();
                    this.mCloseToSideWhenViewRealeased = 4;
                    top = top2;
                }
            }
            if (this.mHelper != null) {
                List<Rect> list = this.mNotchRects;
                if (list != null && list.size() > 0) {
                    calculateForNotchRects(toTB, left, top);
                } else if (this.mNotchRects == null) {
                    this.mNotchRects = getNotchRectList();
                }
                this.mHelper.settleCapturedViewAt(left, top);
                this.mParent.invalidate();
                log("onViewReleased ", "releasedChild  = " + releasedChild + " , left = " + releasedChild.getLeft() + " , top = " + releasedChild.getTop() + " , right = " + releasedChild.getRight() + " , bottom = " + releasedChild.getBottom());
            }
        }
    }

    public void onEdgeDragStarted(int edgeFlags, int pointerId) {
        super.onEdgeDragStarted(edgeFlags, pointerId);
    }

    public List<Rect> getNotchRectList() {
        return new ArrayList();
    }

    public void calculateForNotchRects(boolean toTB, int left, int top) {
        if (this.mNotchRects.size() > 0) {
            for (Rect rect : this.mNotchRects) {
                if (left >= rect.left && left <= rect.right && top >= rect.top && top <= rect.bottom) {
                    left = rect.left;
                    top = rect.top;
                }
            }
        }
    }

    public DragCallBack setHelper(ViewDragHelper mHelper2) {
        this.mHelper = mHelper2;
        return this;
    }

    public DragCallBack setAllowSideToTopOrBottom(boolean allowSideToTopOrBottom) {
        this.mAllowSideToTopOrBottom = allowSideToTopOrBottom;
        return this;
    }

    public DragCallBack setAllowDragOutParentBorder(boolean allowDragOutParentBorder) {
        this.mAllowDragOutParentBorder = allowDragOutParentBorder;
        return this;
    }

    public DragCallBack setAutoBackBorderAfterRelease(boolean autoBackBorderAfterRelease) {
        this.mAutoBackBorderAfterRelease = autoBackBorderAfterRelease;
        return this;
    }

    public DragCallBack setAllowTopToStatusBar(boolean mAllowTopToStatusBar2) {
        this.mAllowTopToStatusBar = mAllowTopToStatusBar2;
        return this;
    }

    public boolean isAllowDragOutParentBorder() {
        return this.mAllowDragOutParentBorder;
    }

    public boolean isAutoBackBorderAfterRelease() {
        return this.mAutoBackBorderAfterRelease;
    }

    public boolean isCapturedViewPositionHasChanged() {
        return this.mCapturedViewPositionHasChanged;
    }

    public boolean isDraging() {
        return this.mIsDraging;
    }

    public View getCapturedView() {
        return this.mCapturedView;
    }

    public int getCapturedViewLastLeft() {
        return this.mCapturedViewLastLeft;
    }

    public int getCapturedViewLastTop() {
        return this.mCapturedViewLastTop;
    }

    public int getCapturedViewLastRight() {
        return this.mCapturedViewLastRight;
    }

    public int getCapturedViewLastBottom() {
        return this.mCapturedViewLastBottom;
    }

    public int getCloseToSideWhenViewRealeased() {
        return this.mCloseToSideWhenViewRealeased;
    }

    public void log(String desc, String msg) {
        if (BuildVars.DEBUG_VERSION) {
            Log.i("DragHelperFrame", "DragCallBack ===> " + desc + msg);
        }
    }
}
