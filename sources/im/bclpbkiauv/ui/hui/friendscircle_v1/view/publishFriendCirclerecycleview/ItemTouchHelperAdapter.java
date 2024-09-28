package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.content.Context;
import java.util.List;

public interface ItemTouchHelperAdapter<T> {
    List<Integer> getCannotDragIndexList();

    Context getContext();

    List<T> getData();

    boolean onItemMove(int i, int i2);

    boolean onItemRemove(int i);
}
