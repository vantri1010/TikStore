package im.bclpbkiauv.ui.hui.friendscircle_v1.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import java.security.MessageDigest;

public class RotateTransformation implements Transformation<Bitmap> {
    private boolean isLeftBottom;
    private boolean isLeftTop;
    private boolean isRightBotoom;
    private boolean isRightTop;
    private BitmapPool mBitmapPool;
    private float radius;

    public void setNeedCorner(boolean leftTop, boolean rightTop, boolean leftBottom, boolean rightBottom) {
        this.isLeftTop = leftTop;
        this.isRightTop = rightTop;
        this.isLeftBottom = leftBottom;
        this.isRightBotoom = rightBottom;
    }

    public RotateTransformation(Context context, float radius2) {
        this.mBitmapPool = Glide.get(context).getBitmapPool();
        this.radius = radius2;
    }

    public Resource<Bitmap> transform(Context context, Resource<Bitmap> resource, int outWidth, int outHeight) {
        int finalHeight;
        int finalWidth;
        int i = outWidth;
        int i2 = outHeight;
        Bitmap source = resource.get();
        if (i > i2) {
            finalWidth = source.getWidth();
            finalHeight = (int) (((float) source.getWidth()) * (((float) i2) / ((float) i)));
            if (finalHeight > source.getHeight()) {
                finalHeight = source.getHeight();
                finalWidth = (int) (((float) source.getHeight()) * (((float) i) / ((float) i2)));
            }
        } else if (i < i2) {
            finalHeight = source.getHeight();
            finalWidth = (int) (((float) source.getHeight()) * (((float) i) / ((float) i2)));
            if (finalWidth > source.getWidth()) {
                finalWidth = source.getWidth();
                finalHeight = (int) (((float) source.getWidth()) * (((float) i2) / ((float) i)));
            }
        } else {
            finalHeight = source.getHeight();
            finalWidth = finalHeight;
        }
        this.radius *= ((float) finalHeight) / ((float) i2);
        Bitmap outBitmap = this.mBitmapPool.get(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        if (outBitmap == null) {
            outBitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        int width = (source.getWidth() - finalWidth) / 2;
        int height = (source.getHeight() - finalHeight) / 2;
        if (!(width == 0 && height == 0)) {
            Matrix matrix = new Matrix();
            matrix.setTranslate((float) (-width), (float) (-height));
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight());
        float f = this.radius;
        canvas.drawRoundRect(rectF, f, f, paint);
        if (!this.isLeftTop) {
            float f2 = this.radius;
            RectF rectF2 = rectF;
            float f3 = f2;
            int i3 = height;
            float f4 = f2;
            int i4 = width;
            canvas.drawRect(0.0f, 0.0f, f3, f4, paint);
        } else {
            int i5 = height;
            int i6 = width;
        }
        if (!this.isRightTop) {
            canvas.drawRect(((float) canvas.getWidth()) - this.radius, 0.0f, (float) canvas.getWidth(), this.radius, paint);
        }
        if (!this.isLeftBottom) {
            float f5 = this.radius;
            canvas.drawRect(0.0f, ((float) canvas.getHeight()) - f5, f5, (float) canvas.getHeight(), paint);
        }
        if (!this.isRightBotoom) {
            canvas.drawRect(((float) canvas.getWidth()) - this.radius, ((float) canvas.getHeight()) - this.radius, (float) canvas.getWidth(), (float) canvas.getHeight(), paint);
        }
        return BitmapResource.obtain(outBitmap, this.mBitmapPool);
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}
