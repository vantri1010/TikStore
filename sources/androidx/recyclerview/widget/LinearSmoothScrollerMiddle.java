package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import androidx.recyclerview.widget.RecyclerView;

public class LinearSmoothScrollerMiddle extends RecyclerView.SmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 25.0f;
    private static final float TARGET_SEEK_EXTRA_SCROLL_RATIO = 1.2f;
    private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
    private final float MILLISECONDS_PER_PX;
    protected final DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(1.5f);
    protected int mInterimTargetDx = 0;
    protected int mInterimTargetDy = 0;
    protected final LinearInterpolator mLinearInterpolator = new LinearInterpolator();
    protected PointF mTargetVector;

    public LinearSmoothScrollerMiddle(Context context) {
        this.MILLISECONDS_PER_PX = MILLISECONDS_PER_INCH / ((float) context.getResources().getDisplayMetrics().densityDpi);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
    }

    /* access modifiers changed from: protected */
    public void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        int dy = calculateDyToMakeVisible(targetView);
        int time = calculateTimeForDeceleration(dy);
        if (time > 0) {
            action.update(0, -dy, Math.max(400, time), this.mDecelerateInterpolator);
        }
    }

    /* access modifiers changed from: protected */
    public void onSeekTargetStep(int dx, int dy, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }
        this.mInterimTargetDx = clampApplyScroll(this.mInterimTargetDx, dx);
        int clampApplyScroll = clampApplyScroll(this.mInterimTargetDy, dy);
        this.mInterimTargetDy = clampApplyScroll;
        if (this.mInterimTargetDx == 0 && clampApplyScroll == 0) {
            updateActionForInterimTarget(action);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mInterimTargetDy = 0;
        this.mInterimTargetDx = 0;
        this.mTargetVector = null;
    }

    /* access modifiers changed from: protected */
    public int calculateTimeForDeceleration(int dx) {
        return (int) Math.ceil(((double) calculateTimeForScrolling(dx)) / 0.3356d);
    }

    /* access modifiers changed from: protected */
    public int calculateTimeForScrolling(int dx) {
        return (int) Math.ceil((double) (((float) Math.abs(dx)) * this.MILLISECONDS_PER_PX));
    }

    /* access modifiers changed from: protected */
    public void updateActionForInterimTarget(RecyclerView.SmoothScroller.Action action) {
        PointF scrollVector = computeScrollVectorForPosition(getTargetPosition());
        if (scrollVector == null || (scrollVector.x == 0.0f && scrollVector.y == 0.0f)) {
            action.jumpTo(getTargetPosition());
            stop();
            return;
        }
        normalize(scrollVector);
        this.mTargetVector = scrollVector;
        this.mInterimTargetDx = (int) (scrollVector.x * 10000.0f);
        this.mInterimTargetDy = (int) (scrollVector.y * 10000.0f);
        action.update((int) (((float) this.mInterimTargetDx) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) this.mInterimTargetDy) * TARGET_SEEK_EXTRA_SCROLL_RATIO), (int) (((float) calculateTimeForScrolling(10000)) * TARGET_SEEK_EXTRA_SCROLL_RATIO), this.mLinearInterpolator);
    }

    private int clampApplyScroll(int tmpDt, int dt) {
        int before = tmpDt;
        int tmpDt2 = tmpDt - dt;
        if (before * tmpDt2 <= 0) {
            return 0;
        }
        return tmpDt2;
    }

    public int calculateDyToMakeVisible(View view) {
        int start;
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0;
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        int top = layoutManager.getDecoratedTop(view) - params.topMargin;
        int bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin;
        int boxSize = (layoutManager.getHeight() - layoutManager.getPaddingBottom()) - layoutManager.getPaddingTop();
        int viewSize = bottom - top;
        if (viewSize > boxSize) {
            start = 0;
        } else {
            start = (boxSize - viewSize) / 2;
        }
        int end = start + viewSize;
        int dtStart = start - top;
        if (dtStart > 0) {
            return dtStart;
        }
        int dtEnd = end - bottom;
        if (dtEnd < 0) {
            return dtEnd;
        }
        return 0;
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return ((RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager).computeScrollVectorForPosition(targetPosition);
        }
        return null;
    }
}
