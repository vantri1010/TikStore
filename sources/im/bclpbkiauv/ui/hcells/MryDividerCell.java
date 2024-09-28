package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import im.bclpbkiauv.ui.actionbar.Theme;

public class MryDividerCell extends View {
    private float padding;

    public MryDividerCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryDividerCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryDividerCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(0, 0, 0, 0);
    }

    public void setPadding(float padding2) {
        this.padding = padding2;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), getPaddingTop() + getPaddingBottom() + 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        canvas.drawLine((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) getPaddingTop(), Theme.dividerPaint);
    }
}
