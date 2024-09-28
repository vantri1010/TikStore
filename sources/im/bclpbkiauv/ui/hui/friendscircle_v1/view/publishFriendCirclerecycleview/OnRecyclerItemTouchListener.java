package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public abstract class OnRecyclerItemTouchListener<VH extends RecyclerView.ViewHolder> implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    /* access modifiers changed from: private */
    public RecyclerView recyclerView;

    public abstract void onItemClick(VH vh);

    public abstract void onItemLongClick(VH vh);

    public OnRecyclerItemTouchListener(RecyclerView recyclerView2) {
        this.recyclerView = recyclerView2;
        this.mGestureDetector = new GestureDetectorCompat(recyclerView2.getContext(), new ItemTouchHelperGestureListener());
    }

    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        this.mGestureDetector.onTouchEvent(e);
        return false;
    }

    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        this.mGestureDetector.onTouchEvent(e);
    }

    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        private ItemTouchHelperGestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            View child = OnRecyclerItemTouchListener.this.recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child == null) {
                return true;
            }
            OnRecyclerItemTouchListener.this.onItemClick(OnRecyclerItemTouchListener.this.recyclerView.getChildViewHolder(child));
            return true;
        }

        public void onLongPress(MotionEvent e) {
            View child = OnRecyclerItemTouchListener.this.recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                OnRecyclerItemTouchListener.this.onItemLongClick(OnRecyclerItemTouchListener.this.recyclerView.getChildViewHolder(child));
            }
        }
    }
}
