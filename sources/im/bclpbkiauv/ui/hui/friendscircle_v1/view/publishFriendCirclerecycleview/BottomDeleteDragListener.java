package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.graphics.Canvas;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;

public abstract class BottomDeleteDragListener<T, VH extends RecyclerView.ViewHolder> implements DragListener<T, VH> {
    public boolean stateIsInSpecialArea(boolean isInArea, boolean isFingerUp, int position) {
        return false;
    }

    public void doBothInAreaAndFingerUp(boolean isInArea, boolean isFingerUp) {
    }

    public boolean checkIsInSpecialArea(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        return SizeUtils.dp2px(150.0f) >= ScreenUtils.getScreenHeight() - getViewYLocationInScreen(viewHolder.itemView);
    }

    public void clearView() {
    }

    public static int getViewYLocationInScreen(View v) {
        if (v == null) {
            return 0;
        }
        int[] a = new int[2];
        v.getLocationOnScreen(a);
        return a[1];
    }
}
