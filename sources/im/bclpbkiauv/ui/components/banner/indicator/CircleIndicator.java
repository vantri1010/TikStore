package im.bclpbkiauv.ui.components.banner.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class CircleIndicator extends BaseIndicator {
    private float mNormalRadius;
    private float mSelectedRadius;
    private float maxRadius;

    public CircleIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mNormalRadius = this.config.getNormalWidth() / 2.0f;
        this.mSelectedRadius = this.config.getSelectedWidth() / 2.0f;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            this.mNormalRadius = this.config.getNormalWidth() / 2.0f;
            float selectedWidth = this.config.getSelectedWidth() / 2.0f;
            this.mSelectedRadius = selectedWidth;
            this.maxRadius = Math.max(selectedWidth, this.mNormalRadius);
            setMeasuredDimension((int) ((((float) (count - 1)) * this.config.getIndicatorSpace()) + this.config.getSelectedWidth() + (this.config.getNormalWidth() * ((float) (count - 1)))), (int) Math.max(this.config.getNormalWidth(), this.config.getSelectedWidth()));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            float left = 0.0f;
            int i = 0;
            while (i < count) {
                this.mPaint.setColor(this.config.getCurrentPosition() == i ? this.config.getSelectedColor() : this.config.getNormalColor());
                float indicatorWidth = this.config.getCurrentPosition() == i ? this.config.getSelectedWidth() : this.config.getNormalWidth();
                float radius = this.config.getCurrentPosition() == i ? this.mSelectedRadius : this.mNormalRadius;
                canvas.drawCircle(left + radius, this.maxRadius, radius, this.mPaint);
                left += this.config.getIndicatorSpace() + indicatorWidth;
                i++;
            }
        }
    }
}
