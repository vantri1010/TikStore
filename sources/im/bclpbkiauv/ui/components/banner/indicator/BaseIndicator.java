package im.bclpbkiauv.ui.components.banner.indicator;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.ui.components.banner.config.IndicatorConfig;

public class BaseIndicator extends View implements Indicator {
    protected IndicatorConfig config;
    protected Paint mPaint;
    protected float offset;

    public BaseIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public BaseIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.config = new IndicatorConfig();
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
        this.mPaint.setColor(0);
        this.mPaint.setColor(this.config.getNormalColor());
    }

    public View getIndicatorView() {
        if (this.config.isAttachToBanner()) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            int gravity = this.config.getGravity();
            if (gravity == 0) {
                layoutParams.gravity = 8388691;
            } else if (gravity == 1) {
                layoutParams.gravity = 81;
            } else if (gravity == 2) {
                layoutParams.gravity = 8388693;
            }
            layoutParams.leftMargin = this.config.getMargins().leftMargin;
            layoutParams.rightMargin = this.config.getMargins().rightMargin;
            layoutParams.topMargin = this.config.getMargins().topMargin;
            layoutParams.bottomMargin = this.config.getMargins().bottomMargin;
            setLayoutParams(layoutParams);
        }
        return this;
    }

    public IndicatorConfig getIndicatorConfig() {
        return this.config;
    }

    public void onPageChanged(int count, int currentPosition) {
        this.config.setIndicatorSize(count);
        this.config.setCurrentPosition(currentPosition);
        requestLayout();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.offset = positionOffset;
        invalidate();
    }

    public void onPageSelected(int position) {
        this.config.setCurrentPosition(position);
        invalidate();
    }

    public void onPageScrollStateChanged(int state) {
    }
}
