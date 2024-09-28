package com.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class MyRecyclerViewList extends RecyclerListView {
    private BaseDecoration mDecoration;

    public MyRecyclerViewList(Context context) {
        super(context);
    }

    public MyRecyclerViewList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerViewList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        if (decor != null && (decor instanceof BaseDecoration)) {
            this.mDecoration = (BaseDecoration) decor;
        }
        super.addItemDecoration(decor);
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.mDecoration != null) {
            int action = e.getAction();
            if (action != 0) {
                if (action == 1 && this.mDecoration.onEventUp(e)) {
                    return true;
                }
            } else if (this.mDecoration.onEventDown2(e)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(e);
    }
}
