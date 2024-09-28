package com.scwang.smartrefresh.layout.internal;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public abstract class PaintDrawable extends Drawable {
    protected Paint mPaint;

    protected PaintDrawable() {
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-5592406);
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }
}
