package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.graphics.Canvas;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public interface DragListener<T, VH extends RecyclerView.ViewHolder> {
    boolean canDrag(VH vh, int i, T t);

    boolean checkIsInSpecialArea(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z);

    void clearView();

    void doBothInAreaAndFingerUp(boolean z, boolean z2);

    void onDraging();

    void onPreDrag();

    void onReleasedDrag();

    boolean stateIsInSpecialArea(boolean z, boolean z2, int i);
}
