package com.scwang.smartrefresh.layout.internal;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class ArrowDrawable extends PaintDrawable {
    private int mHeight = 0;
    private Path mPath = new Path();
    private int mWidth = 0;

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int width = bounds.width();
        int height = bounds.height();
        if (!(this.mWidth == width && this.mHeight == height)) {
            int lineWidth = (width * 30) / 225;
            this.mPath.reset();
            float vector1 = ((float) lineWidth) * 0.70710677f;
            float vector2 = ((float) lineWidth) / 0.70710677f;
            this.mPath.moveTo(((float) width) / 2.0f, (float) height);
            this.mPath.lineTo(0.0f, ((float) height) / 2.0f);
            this.mPath.lineTo(vector1, (((float) height) / 2.0f) - vector1);
            this.mPath.lineTo((((float) width) / 2.0f) - (((float) lineWidth) / 2.0f), (((float) height) - vector2) - (((float) lineWidth) / 2.0f));
            this.mPath.lineTo((((float) width) / 2.0f) - (((float) lineWidth) / 2.0f), 0.0f);
            this.mPath.lineTo((((float) width) / 2.0f) + (((float) lineWidth) / 2.0f), 0.0f);
            this.mPath.lineTo((((float) width) / 2.0f) + (((float) lineWidth) / 2.0f), (((float) height) - vector2) - (((float) lineWidth) / 2.0f));
            this.mPath.lineTo(((float) width) - vector1, (((float) height) / 2.0f) - vector1);
            this.mPath.lineTo((float) width, ((float) height) / 2.0f);
            this.mPath.close();
            this.mWidth = width;
            this.mHeight = height;
        }
        canvas.drawPath(this.mPath, this.mPaint);
    }
}
