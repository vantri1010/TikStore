package im.bclpbkiauv.ui.hui.adapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SmartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final AdapterView.OnItemClickListener mListener;
    private int mPosition = -1;

    public SmartViewHolder(View itemView, AdapterView.OnItemClickListener mListener2) {
        super(itemView);
        this.mListener = mListener2;
        itemView.setOnClickListener(this);
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void onClick(View v) {
        if (this.mListener != null) {
            int position = getAdapterPosition();
            if (position >= 0) {
                this.mListener.onItemClick((AdapterView) null, v, position, getItemId());
                return;
            }
            int i = this.mPosition;
            if (i > -1) {
                this.mListener.onItemClick((AdapterView) null, v, i, getItemId());
            }
        }
    }

    private View findViewById(int id) {
        View view = this.itemView;
        return id == 0 ? view : view.findViewById(id);
    }

    public SmartViewHolder text(int id, CharSequence sequence) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(sequence);
        }
        return this;
    }

    public SmartViewHolder text(int id, int stringRes) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(stringRes);
        }
        return this;
    }

    public SmartViewHolder textColorId(int id, int colorId) {
        View view = findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(ContextCompat.getColor(view.getContext(), colorId));
        }
        return this;
    }

    public SmartViewHolder image(int id, int imageId) {
        View view = findViewById(id);
        if (view instanceof ImageView) {
            ((ImageView) view).setImageResource(imageId);
        }
        return this;
    }

    public SmartViewHolder gone(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(8);
        }
        return this;
    }

    public SmartViewHolder visible(int id) {
        View view = findViewById(id);
        if (view != null) {
            view.setVisibility(0);
        }
        return this;
    }
}
