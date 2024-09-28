package im.bclpbkiauv.ui.hui.friendscircle_v1.view.publishFriendCirclerecycleview;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;

public abstract class AddPictureTouchAdapter<T, VH extends RecyclerView.ViewHolder> extends ItemTouchAdapter<T, VH> {
    public static final int VIEW_TYPE_ADD_ITEM = 1;
    public static final int VIEW_TYPE_NORMAL = 0;
    private int maxCount;
    /* access modifiers changed from: private */
    public AddPictureOnItemClickListener<T> onItemClickListener;

    public interface AddPictureOnItemClickListener<T> {
        void onItemClick(T t, boolean z);
    }

    /* access modifiers changed from: protected */
    public abstract void onBindViewHolder(VH vh, int i, T t, boolean z);

    public AddPictureTouchAdapter(Context context) {
        super(context);
    }

    public final void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (AddPictureTouchAdapter.this.onItemClickListener != null) {
                    AddPictureOnItemClickListener access$000 = AddPictureTouchAdapter.this.onItemClickListener;
                    boolean z = true;
                    Object item = holder.getItemViewType() == 1 ? null : AddPictureTouchAdapter.this.getItem(position);
                    if (holder.getItemViewType() != 1) {
                        z = false;
                    }
                    access$000.onItemClick(item, z);
                }
            }
        });
        Object item = getItem(position);
        boolean z = true;
        if (holder.getItemViewType() != 1) {
            z = false;
        }
        onBindViewHolder(holder, position, item, z);
    }

    public int getItemViewType(int position) {
        if (checkIsFull() || position != getItemCount() - 1) {
            return 0;
        }
        return 1;
    }

    public int getItemCount() {
        return checkIsFull() ? this.maxCount : getDataCount() + 1;
    }

    /* access modifiers changed from: protected */
    public void notifyChanged() {
        super.notifyChanged();
        if (checkIsFull()) {
            this.mCannotMovePositionList = null;
            return;
        }
        this.mCannotMovePositionList = new ArrayList();
        this.mCannotMovePositionList.add(Integer.valueOf(getItemCount() - 1));
    }

    /* access modifiers changed from: protected */
    public boolean checkIsFull() {
        return getDataCount() == this.maxCount;
    }

    public void setMaxCount(int maxCount2) {
        this.maxCount = maxCount2;
    }

    public int getMaxSelectCount() {
        return this.maxCount;
    }

    public void setOnItemClickListener(AddPictureOnItemClickListener<T> onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }
}
