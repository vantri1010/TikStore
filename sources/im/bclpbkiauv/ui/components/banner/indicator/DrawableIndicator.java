package im.bclpbkiauv.ui.components.banner.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import im.bclpbkiauv.messenger.R;

public class DrawableIndicator extends BaseIndicator {
    private Bitmap normalBitmap;
    private Bitmap selectedBitmap;

    public DrawableIndicator(Context context, int normalResId, int selectedResId) {
        super(context);
        this.normalBitmap = BitmapFactory.decodeResource(getResources(), normalResId);
        this.selectedBitmap = BitmapFactory.decodeResource(getResources(), selectedResId);
    }

    public DrawableIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public DrawableIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableIndicator);
        if (a != null) {
            this.normalBitmap = ((BitmapDrawable) a.getDrawable(0)).getBitmap();
            this.selectedBitmap = ((BitmapDrawable) a.getDrawable(1)).getBitmap();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.config.getIndicatorSize();
        if (count > 1) {
            setMeasuredDimension((int) (((float) ((this.selectedBitmap.getWidth() * (count - 1)) + this.selectedBitmap.getWidth())) + (this.config.getIndicatorSpace() * ((float) (count - 1)))), Math.max(this.normalBitmap.getHeight(), this.selectedBitmap.getHeight()));
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = this.config.getIndicatorSize();
        if (count > 1 && this.normalBitmap != null && this.selectedBitmap != null) {
            float left = 0.0f;
            int i = 0;
            while (i < count) {
                canvas.drawBitmap(this.config.getCurrentPosition() == i ? this.selectedBitmap : this.normalBitmap, left, 0.0f, this.mPaint);
                left += ((float) this.normalBitmap.getWidth()) + this.config.getIndicatorSpace();
                i++;
            }
        }
    }
}
