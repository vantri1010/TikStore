package im.bclpbkiauv.ui.components.recyclerview;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View view, int i, T t);
}
