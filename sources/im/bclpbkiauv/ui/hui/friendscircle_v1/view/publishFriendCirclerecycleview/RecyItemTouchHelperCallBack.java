package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.graphics.Canvas;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import im.bclpbkiauv.messenger.R;

public class RecyItemTouchHelperCallBack<T, VH extends RecyclerView.ViewHolder> extends ItemTouchHelper.Callback {
    public static final float ALPHA_FULL = 1.0f;
    private boolean actionUp;
    private DragListener<T, VH> dragListener;
    private boolean isSwipeEnable = false;
    private ItemTouchAdapter<T, VH> mAdapter;

    public RecyItemTouchHelperCallBack(ItemTouchAdapter<T, VH> adapter) {
        this.mAdapter = adapter;
    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return makeMovementFlags(15, 0);
        }
        return makeMovementFlags(3, 48);
    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (!checkCanLongDrag(fromPosition, toPosition)) {
            return false;
        }
        DragListener<T, VH> dragListener2 = this.dragListener;
        if (dragListener2 != null) {
            dragListener2.onDraging();
        }
        ItemTouchAdapter<T, VH> itemTouchAdapter = this.mAdapter;
        if (itemTouchAdapter == null) {
            return true;
        }
        itemTouchAdapter.onItemMove(fromPosition, toPosition);
        return true;
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        ItemTouchAdapter<T, VH> itemTouchAdapter = this.mAdapter;
        if (itemTouchAdapter != null) {
            itemTouchAdapter.onItemRemove(viewHolder.getAdapterPosition());
        }
    }

    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        RecyclerView.ViewHolder viewHolder2 = viewHolder;
        DragListener<T, VH> dragListener2 = this.dragListener;
        if (dragListener2 != null) {
            boolean isInArea = dragListener2.checkIsInSpecialArea(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (this.dragListener.stateIsInSpecialArea(isInArea, this.actionUp, viewHolder.getAdapterPosition()) || !isInArea || !this.actionUp) {
                this.dragListener.stateIsInSpecialArea(isInArea, this.actionUp, viewHolder.getAdapterPosition());
            } else {
                viewHolder2.itemView.setVisibility(4);
                ItemTouchAdapter<T, VH> itemTouchAdapter = this.mAdapter;
                if (itemTouchAdapter != null) {
                    itemTouchAdapter.onItemRemove(viewHolder.getAdapterPosition());
                }
                this.dragListener.doBothInAreaAndFingerUp(true, this.actionUp);
                this.actionUp = false;
                return;
            }
        }
        if (actionState == 1) {
            View itemView = viewHolder2.itemView;
            itemView.setAlpha(1.0f - (Math.abs(dX) / ((float) itemView.getWidth())));
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        this.actionUp = true;
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Vibrator vibrator;
        if (!(viewHolder == null || !checkCanLongDrag(viewHolder.getAdapterPosition(), -1) || actionState == 0)) {
            ItemTouchAdapter<T, VH> itemTouchAdapter = this.mAdapter;
            if (!(itemTouchAdapter == null || itemTouchAdapter.getContext() == null || (vibrator = (Vibrator) this.mAdapter.getContext().getSystemService("vibrator")) == null)) {
                vibrator.vibrate(50);
            }
            DragListener<T, VH> dragListener2 = this.dragListener;
            if (dragListener2 != null) {
                dragListener2.onPreDrag();
            }
            startItemScaleAni(viewHolder, true);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        startItemScaleAni(viewHolder, false);
        viewHolder.itemView.setAlpha(1.0f);
        viewHolder.itemView.setBackgroundColor(-1);
        resetDragListenerBoth();
        DragListener<T, VH> dragListener2 = this.dragListener;
        if (dragListener2 != null) {
            dragListener2.onReleasedDrag();
            this.dragListener.clearView();
        }
    }

    public boolean isLongPressDragEnabled() {
        return false;
    }

    public boolean isItemViewSwipeEnabled() {
        return this.isSwipeEnable;
    }

    private void resetDragListenerBoth() {
        DragListener<T, VH> dragListener2 = this.dragListener;
        if (dragListener2 != null) {
            dragListener2.stateIsInSpecialArea(false, false, -1);
        }
        this.actionUp = false;
    }

    public void startItemScaleAni(RecyclerView.ViewHolder viewHolder, boolean bigger) {
        if (viewHolder == null) {
            return;
        }
        if (bigger) {
            doScaleAni(viewHolder.itemView, R.anim.scale_from_100_to_110, false);
        } else {
            doScaleAni(viewHolder.itemView, R.anim.scale_from_110_to_100, true);
        }
    }

    public boolean checkCanLongDrag(int fromOrLongClickPosition, int toPosition) {
        ItemTouchAdapter<T, VH> itemTouchAdapter = this.mAdapter;
        return itemTouchAdapter == null || itemTouchAdapter.getCannotDragIndexList() == null || (!this.mAdapter.getCannotDragIndexList().contains(Integer.valueOf(fromOrLongClickPosition)) && (-1 == toPosition || !this.mAdapter.getCannotDragIndexList().contains(Integer.valueOf(toPosition))));
    }

    public void setSwipeEnable(boolean swipeEnable) {
        this.isSwipeEnable = swipeEnable;
    }

    public void setDragListener(DragListener<T, VH> dragListener2) {
        this.dragListener = dragListener2;
    }

    public void doScaleAni(final View view, int animResId, boolean clearAnimation) {
        if (view != null) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), animResId);
            if (clearAnimation) {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        view.clearAnimation();
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
            view.startAnimation(animation);
        }
    }
}
