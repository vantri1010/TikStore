package im.bclpbkiauv.ui.components.banner.itemdecoration;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MarginDecoration extends RecyclerView.ItemDecoration {
    private int mMarginPx;

    public MarginDecoration(int margin) {
        this.mMarginPx = margin;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (requireLinearLayoutManager(parent).getOrientation() == 1) {
            outRect.top = this.mMarginPx;
            outRect.bottom = this.mMarginPx;
            return;
        }
        outRect.left = this.mMarginPx;
        outRect.right = this.mMarginPx;
    }

    private LinearLayoutManager requireLinearLayoutManager(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return (LinearLayoutManager) layoutManager;
        }
        throw new IllegalStateException("The layoutManager must be LinearLayoutManager");
    }
}
