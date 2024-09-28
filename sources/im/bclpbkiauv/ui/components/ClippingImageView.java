package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;

public class ClippingImageView extends View {
    private float animationProgress;
    private float[][] animationValues;
    private RectF bitmapRect = new RectF();
    private BitmapShader bitmapShader;
    private ImageReceiver.BitmapHolder bmp;
    private int clipBottom;
    private int clipLeft;
    private int clipRight;
    private int clipTop;
    private RectF drawRect = new RectF();
    private int imageX;
    private int imageY;
    private Matrix matrix = new Matrix();
    private boolean needRadius;
    private int orientation;
    private Paint paint;
    private int radius;
    private Paint roundPaint = new Paint(3);
    private RectF roundRect = new RectF();
    private Matrix shaderMatrix = new Matrix();

    public ClippingImageView(Context context) {
        super(context);
        Paint paint2 = new Paint(2);
        this.paint = paint2;
        paint2.setFilterBitmap(true);
    }

    public void setAnimationValues(float[][] values) {
        this.animationValues = values;
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    public void setAnimationProgress(float progress) {
        this.animationProgress = progress;
        try {
            setScaleX(this.animationValues[0][0] + ((this.animationValues[1][0] - this.animationValues[0][0]) * progress));
            setScaleY(this.animationValues[0][1] + ((this.animationValues[1][1] - this.animationValues[0][1]) * this.animationProgress));
            setTranslationX(this.animationValues[0][2] + ((this.animationValues[1][2] - this.animationValues[0][2]) * this.animationProgress));
            setTranslationY(this.animationValues[0][3] + ((this.animationValues[1][3] - this.animationValues[0][3]) * this.animationProgress));
            setClipHorizontal((int) (this.animationValues[0][4] + ((this.animationValues[1][4] - this.animationValues[0][4]) * this.animationProgress)));
            setClipTop((int) (this.animationValues[0][5] + ((this.animationValues[1][5] - this.animationValues[0][5]) * this.animationProgress)));
            setClipBottom((int) (this.animationValues[0][6] + ((this.animationValues[1][6] - this.animationValues[0][6]) * this.animationProgress)));
            setRadius((int) (this.animationValues[0][7] + ((this.animationValues[1][7] - this.animationValues[0][7]) * this.animationProgress)));
            if (this.animationValues[0].length > 8) {
                setImageY((int) (this.animationValues[0][8] + ((this.animationValues[1][8] - this.animationValues[0][8]) * this.animationProgress)));
                setImageX((int) (this.animationValues[0][9] + ((this.animationValues[1][9] - this.animationValues[0][9]) * this.animationProgress)));
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        invalidate();
    }

    public void getClippedVisibleRect(RectF rect) {
        rect.left = getTranslationX();
        rect.top = getTranslationY();
        rect.right = rect.left + (((float) getMeasuredWidth()) * getScaleX());
        rect.bottom = rect.top + (((float) getMeasuredHeight()) * getScaleY());
        rect.left += (float) this.clipLeft;
        rect.top += (float) this.clipTop;
        rect.right -= (float) this.clipRight;
        rect.bottom -= (float) this.clipBottom;
    }

    public int getClipBottom() {
        return this.clipBottom;
    }

    public int getClipHorizontal() {
        return this.clipRight;
    }

    public int getClipLeft() {
        return this.clipLeft;
    }

    public int getClipRight() {
        return this.clipRight;
    }

    public int getClipTop() {
        return this.clipTop;
    }

    public int getRadius() {
        return this.radius;
    }

    public void onDraw(Canvas canvas) {
        ImageReceiver.BitmapHolder bitmapHolder;
        if (getVisibility() == 0 && (bitmapHolder = this.bmp) != null && !bitmapHolder.isRecycled()) {
            float scaleY = getScaleY();
            canvas.save();
            if (this.needRadius) {
                this.shaderMatrix.reset();
                this.roundRect.set(((float) this.imageX) / scaleY, ((float) this.imageY) / scaleY, ((float) getWidth()) - (((float) this.imageX) / scaleY), ((float) getHeight()) - (((float) this.imageY) / scaleY));
                this.bitmapRect.set(0.0f, 0.0f, (float) this.bmp.getWidth(), (float) this.bmp.getHeight());
                AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.orientation, false);
                this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                RectF rectF = this.roundRect;
                int i = this.radius;
                canvas.drawRoundRect(rectF, (float) i, (float) i, this.roundPaint);
            } else {
                int i2 = this.orientation;
                if (i2 == 90 || i2 == 270) {
                    this.drawRect.set((float) ((-getHeight()) / 2), (float) ((-getWidth()) / 2), (float) (getHeight() / 2), (float) (getWidth() / 2));
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                    this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                    this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                } else if (i2 == 180) {
                    this.drawRect.set((float) ((-getWidth()) / 2), (float) ((-getHeight()) / 2), (float) (getWidth() / 2), (float) (getHeight() / 2));
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                    this.matrix.postRotate((float) this.orientation, 0.0f, 0.0f);
                    this.matrix.postTranslate((float) (getWidth() / 2), (float) (getHeight() / 2));
                } else {
                    this.drawRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
                    this.matrix.setRectToRect(this.bitmapRect, this.drawRect, Matrix.ScaleToFit.FILL);
                }
                canvas.clipRect(((float) this.clipLeft) / scaleY, ((float) this.clipTop) / scaleY, ((float) getWidth()) - (((float) this.clipRight) / scaleY), ((float) getHeight()) - (((float) this.clipBottom) / scaleY));
                try {
                    canvas.drawBitmap(this.bmp.bitmap, this.matrix, this.paint);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            canvas.restore();
        }
    }

    public void setClipBottom(int value) {
        this.clipBottom = value;
        invalidate();
    }

    public void setClipHorizontal(int value) {
        this.clipRight = value;
        this.clipLeft = value;
        invalidate();
    }

    public void setClipLeft(int value) {
        this.clipLeft = value;
        invalidate();
    }

    public void setClipRight(int value) {
        this.clipRight = value;
        invalidate();
    }

    public void setClipTop(int value) {
        this.clipTop = value;
        invalidate();
    }

    public void setClipVertical(int value) {
        this.clipBottom = value;
        this.clipTop = value;
        invalidate();
    }

    public void setImageY(int value) {
        this.imageY = value;
    }

    public void setImageX(int value) {
        this.imageX = value;
    }

    public void setOrientation(int angle) {
        this.orientation = angle;
    }

    public void setImageBitmap(ImageReceiver.BitmapHolder bitmap) {
        ImageReceiver.BitmapHolder bitmapHolder = this.bmp;
        if (bitmapHolder != null) {
            bitmapHolder.release();
            this.bitmapShader = null;
        }
        this.bmp = bitmap;
        if (!(bitmap == null || bitmap.bitmap == null)) {
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            if (this.needRadius) {
                BitmapShader bitmapShader2 = new BitmapShader(this.bmp.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                this.bitmapShader = bitmapShader2;
                this.roundPaint.setShader(bitmapShader2);
            }
        }
        invalidate();
    }

    public Bitmap getBitmap() {
        ImageReceiver.BitmapHolder bitmapHolder = this.bmp;
        if (bitmapHolder != null) {
            return bitmapHolder.bitmap;
        }
        return null;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setNeedRadius(boolean value) {
        this.needRadius = value;
    }

    public void setRadius(int value) {
        this.radius = value;
    }
}
