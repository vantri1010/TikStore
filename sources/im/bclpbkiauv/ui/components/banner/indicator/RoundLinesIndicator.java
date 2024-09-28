package im.bclpbkiauv.ui.components.banner.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundLinesIndicator extends BaseIndicator {
    public RoundLinesIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public RoundLinesIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLinesIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            setMeasuredDimension((int) (this.config.getSelectedWidth() * ((float) count)), (int) this.config.getHeight());
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.config.getIndicatorSize() > 1) {
            this.mPaint.setColor(this.config.getNormalColor());
            canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) canvas.getWidth(), this.config.getHeight()), this.config.getRadius(), this.config.getRadius(), this.mPaint);
            this.mPaint.setColor(this.config.getSelectedColor());
            float left = ((float) this.config.getCurrentPosition()) * this.config.getSelectedWidth();
            canvas.drawRoundRect(new RectF(left, 0.0f, this.config.getSelectedWidth() + left, this.config.getHeight()), this.config.getRadius(), this.config.getRadius(), this.mPaint);
        }
    }
}
