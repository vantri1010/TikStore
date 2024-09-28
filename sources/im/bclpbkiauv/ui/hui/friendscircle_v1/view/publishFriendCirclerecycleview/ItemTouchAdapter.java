package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ItemTouchAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements ItemTouchHelperAdapter<T> {
    protected List<Integer> mCannotMovePositionList;
    protected List<T> mData;
    protected RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        public void onChanged() {
            super.onChanged();
            ItemTouchAdapter.this.notifyChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            onChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            onChanged();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            onChanged();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            onChanged();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            onChanged();
        }
    };
    private WeakReference<Context> mWeakContext;

    public ItemTouchAdapter(Context context) {
        this.mWeakContext = new WeakReference<>(context);
        registerAdapterDataObserver(this.mObserver);
    }

    public Context getContext() {
        WeakReference<Context> weakReference = this.mWeakContext;
        if (weakReference != null) {
            return (Context) weakReference.get();
        }
        return null;
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(getData(), i, i + 1);
            }
        } else {
            for (int i2 = fromPosition; i2 > toPosition; i2--) {
                Collections.swap(getData(), i2, i2 - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public boolean onItemRemove(int position) {
        List<T> list = this.mData;
        if (list == null || position < 0 || position >= list.size()) {
            return true;
        }
        this.mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
        return true;
    }

    public List<T> getData() {
        List<T> list = this.mData;
        if (list != null) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        this.mData = arrayList;
        return arrayList;
    }

    public List<Integer> getCannotDragIndexList() {
        return this.mCannotMovePositionList;
    }

    public int getDataCount() {
        List<T> list = this.mData;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
    }

    public T getItem(int position) {
        List<T> list = this.mData;
        if (list == null || position < 0 || position >= list.size()) {
            return null;
        }
        return this.mData.get(position);
    }

    public void setData(List<T> data) {
        this.mData = data;
    }

    public void addItem(T item) {
        if (item != null) {
            if (this.mData == null) {
                this.mData = new ArrayList();
            }
            this.mData.add(item);
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getDataCount()) {
            this.mData.remove(position);
        }
    }

    public void removeItem(T item) {
        this.mData.remove(item);
        notifyDataSetChanged();
    }

    public void destroy() {
        this.mData = null;
        unregisterAdapterDataObserver(this.mObserver);
        this.mObserver = null;
        this.mWeakContext = null;
    }
}
