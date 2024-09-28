package im.bclpbkiauv.ui.hviews.page;

import android.util.DisplayMetrics;
import android.view.View;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class PagerGridSmoothScroller extends LinearSmoothScroller {
    private RecyclerView mRecyclerView;

    public PagerGridSmoothScroller(RecyclerView recyclerView) {
        super(recyclerView.getContext());
        this.mRecyclerView = recyclerView;
    }

    /* access modifiers changed from: protected */
    public void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
        RecyclerView.LayoutManager manager = this.mRecyclerView.getLayoutManager();
        if (manager != null && (manager instanceof PagerGridLayoutManager)) {
            int[] snapDistances = ((PagerGridLayoutManager) manager).getSnapOffset(this.mRecyclerView.getChildAdapterPosition(targetView));
            int dx = snapDistances[0];
            int dy = snapDistances[1];
            PagerConfig.Logi("dx = " + dx);
            PagerConfig.Logi("dy = " + dy);
            int time = calculateTimeForScrolling(Math.max(Math.abs(dx), Math.abs(dy)));
            if (time > 0) {
                action.update(dx, dy, time, this.mDecelerateInterpolator);
            }
        }
    }

    /* access modifiers changed from: protected */
    public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return PagerConfig.getMillisecondsPreInch() / ((float) displayMetrics.densityDpi);
    }
}
