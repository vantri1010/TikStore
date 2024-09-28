package im.bclpbkiauv.ui.components.paint.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.EditText;

public class EditTextOutline extends EditText {
    private Bitmap mCache;
    private final Canvas mCanvas = new Canvas();
    private final TextPaint mPaint;
    private int mStrokeColor;
    private float mStrokeWidth;
    private boolean mUpdateCachedBitmap;

    public EditTextOutline(Context context) {
        super(context);
        TextPaint textPaint = new TextPaint();
        this.mPaint = textPaint;
        this.mStrokeColor = 0;
        this.mUpdateCachedBitmap = true;
        textPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /* access modifiers changed from: protected */
    public void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        this.mUpdateCachedBitmap = true;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) {
            this.mCache = null;
            return;
        }
        this.mUpdateCachedBitmap = true;
        this.mCache = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    public void setStrokeColor(int strokeColor) {
        this.mStrokeColor = strokeColor;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mStrokeWidth = strokeWidth;
        this.mUpdateCachedBitmap = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mCache == null || this.mStrokeColor == 0) {
            Canvas canvas2 = canvas;
        } else {
            if (this.mUpdateCachedBitmap) {
                int w = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                int h = getMeasuredHeight();
                String text = getText().toString();
                this.mCanvas.setBitmap(this.mCache);
                this.mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                float strokeWidth = this.mStrokeWidth;
                if (strokeWidth <= 0.0f) {
                    strokeWidth = (float) Math.ceil((double) (getTextSize() / 11.5f));
                }
                this.mPaint.setStrokeWidth(strokeWidth);
                this.mPaint.setColor(this.mStrokeColor);
                this.mPaint.setTextSize(getTextSize());
                this.mPaint.setTypeface(getTypeface());
                this.mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                StaticLayout sl = new StaticLayout(text, this.mPaint, w, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
                this.mCanvas.save();
                this.mCanvas.translate((float) getPaddingLeft(), ((float) getPaddingTop()) + (((float) (((h - getPaddingTop()) - getPaddingBottom()) - sl.getHeight())) / 2.0f));
                sl.draw(this.mCanvas);
                this.mCanvas.restore();
                this.mUpdateCachedBitmap = false;
            }
            canvas.drawBitmap(this.mCache, 0.0f, 0.0f, this.mPaint);
        }
        super.onDraw(canvas);
    }
}
