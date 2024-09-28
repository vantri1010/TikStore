package im.bclpbkiauv.ui.hui.friendscircle_v1.decoration;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class DetailPageSpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int commSpace = AndroidUtilities.dp(1.0f);
    private int topSpace = AndroidUtilities.dp(7.0f);

    public DetailPageSpacesItemDecoration(int space) {
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getAdapter() != null) {
            int childAdapterPosition = parent.getChildAdapterPosition(view);
            if (childAdapterPosition == 0 || childAdapterPosition == parent.getAdapter().getItemCount() - 1) {
                outRect.bottom = 0;
                return;
            }
            return;
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}
