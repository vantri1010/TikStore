package im.bclpbkiauv.ui.expand.viewholders;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.expand.listeners.OnGroupClickListener;

public abstract class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private OnGroupClickListener listener;

    public GroupViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
    }

    public void onClick(View v) {
        OnGroupClickListener onGroupClickListener = this.listener;
        if (onGroupClickListener != null) {
            onGroupClickListener.onGroupClick(getAdapterPosition());
        }
    }

    public void setOnGroupClickListener(OnGroupClickListener listener2) {
        this.listener = listener2;
    }

    public void expand() {
    }

    public void collapse() {
    }
}
