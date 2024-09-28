package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class CommLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int margin = 0;

    public CommLayoutItemDecoration(int spacingMargin) {
        this.margin = spacingMargin;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, this.margin);
    }
}
