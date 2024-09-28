package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class EmptyCell extends FrameLayout {
    int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
    }

    public EmptyCell(Context context, int height) {
        super(context);
        this.cellHeight = height;
    }

    public EmptyCell(Context context, AttributeSet attrs) {
        this(context, (AttributeSet) null, 0);
    }

    public EmptyCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.cellHeight = 8;
    }

    public void setHeight(int height) {
        this.cellHeight = height;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(this.cellHeight, 1073741824));
    }
}
