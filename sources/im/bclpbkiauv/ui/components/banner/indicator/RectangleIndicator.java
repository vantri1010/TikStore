package im.bclpbkiauv.ui.components.banner.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RectangleIndicator extends BaseIndicator {
    RectF rectF;

    public RectangleIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public RectangleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.rectF = new RectF();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            setMeasuredDimension(((int) (this.config.getIndicatorSpace() * ((float) (count - 1)))) + ((int) (this.config.getNormalWidth() * ((float) (count - 1)))) + ((int) this.config.getSelectedWidth()) + getPaddingLeft() + getPaddingRight(), ((int) this.config.getHeight()) + getPaddingTop() + getPaddingBottom());
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            float left = (float) getPaddingLeft();
            int i = 0;
            while (i < count) {
                this.mPaint.setColor(this.config.getCurrentPosition() == i ? this.config.getSelectedColor() : this.config.getNormalColor());
                float indicatorWidth = this.config.getCurrentPosition() == i ? this.config.getSelectedWidth() : this.config.getNormalWidth();
                this.rectF.set(left, (float) getPaddingTop(), left + indicatorWidth, ((float) getPaddingTop()) + this.config.getHeight());
                left += this.config.getIndicatorSpace() + indicatorWidth;
                canvas.drawRoundRect(this.rectF, this.config.getRadius(), this.config.getRadius(), this.mPaint);
                i++;
            }
        }
    }
}
