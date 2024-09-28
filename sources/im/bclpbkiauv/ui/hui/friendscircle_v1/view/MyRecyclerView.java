package im.bclpbkiauv.ui.hui.friendscircle_v1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.decoration.BaseDecoration;

public class MyRecyclerView extends RecyclerView {
    private BaseDecoration mDecoration;

    public MyRecyclerView(Context context) {
        super(context);
    }

    public MyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
            if (action == 0) {
                this.mDecoration.onEventDown(e);
            } else if (action == 1 && this.mDecoration.onEventUp(e)) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(e);
    }
}
