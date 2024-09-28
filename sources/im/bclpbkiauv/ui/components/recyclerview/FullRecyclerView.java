package im.bclpbkiauv.ui.components.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class FullRecyclerView extends RecyclerView {
    public FullRecyclerView(Context context) {
        super(context, (AttributeSet) null);
    }

    public FullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FullRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE));
    }
}
