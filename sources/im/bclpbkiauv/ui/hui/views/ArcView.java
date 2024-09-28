package im.bclpbkiauv.ui.hui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class ArcView extends View {
    private int lgColor;
    private LinearGradient linearGradient;
    private int mArcHeight;
    private int mBgColor;
    private int mHeight;
    private Paint mPaint;
    private int mWidth;
    private Path path;
    private Rect rect;

    public ArcView(Context context) {
        this(context, (AttributeSet) null);
    }

    public ArcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.rect = new Rect(0, 0, 0, 0);
        this.path = new Path();
        this.mArcHeight = AndroidUtilities.dp(50.0f);
        this.mBgColor = -104343;
        this.lgColor = -96917;
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setAntiAlias(true);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, (float) getMeasuredWidth(), 0.0f, this.mBgColor, this.lgColor, Shader.TileMode.CLAMP);
        this.linearGradient = linearGradient2;
        this.mPaint.setShader(linearGradient2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mBgColor);
        this.rect.set(0, 0, this.mWidth, (this.mHeight - this.mArcHeight) + 1);
        canvas.drawRect(this.rect, this.mPaint);
        this.path.moveTo(0.0f, (float) (this.mHeight - this.mArcHeight));
        Path path2 = this.path;
        int i = this.mWidth;
        int i2 = this.mHeight;
        path2.quadTo((float) (i >> 1), (float) i2, (float) i, (float) (i2 - this.mArcHeight));
        canvas.drawPath(this.path, this.mPaint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == 1073741824) {
            this.mWidth = widthSize;
        }
        if (heightMode == 1073741824) {
            this.mHeight = heightSize;
        }
        setMeasuredDimension(this.mWidth, this.mHeight);
    }
}
