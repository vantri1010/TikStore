package im.bclpbkiauv.ui.hui.decoration;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class TopDecorationWithSearch extends RecyclerView.ItemDecoration {
    private boolean isGridLayoutManager;
    private int top;

    public TopDecorationWithSearch() {
        this.isGridLayoutManager = false;
        this.top = AndroidUtilities.dp(55.0f);
    }

    public TopDecorationWithSearch(int top2, boolean isGridLayoutManager2) {
        this.isGridLayoutManager = false;
        this.top = AndroidUtilities.dp((float) top2);
        this.isGridLayoutManager = isGridLayoutManager2;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.top = this.top;
        }
        if (this.isGridLayoutManager && position == 1) {
            outRect.top = this.top;
        }
    }
}
