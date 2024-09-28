package im.bclpbkiauv.ui.hui.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private Context context;
    private int space;

    public SpacesItemDecoration(int space2, Context context2) {
        this.space = space2;
        this.context = context2;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildPosition(view) != 0) {
            outRect.top = this.space;
        }
    }
}
