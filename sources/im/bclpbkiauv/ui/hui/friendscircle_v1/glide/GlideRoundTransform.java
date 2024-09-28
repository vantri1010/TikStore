package im.bclpbkiauv.ui.hui.friendscircle_v1.glide;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.security.MessageDigest;

public class GlideRoundTransform extends BitmapTransformation {
    private float radius = 0.0f;

    public GlideRoundTransform(int dp) {
        this.radius = (float) dp;
    }

    /* access modifiers changed from: protected */
    public Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight));
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) {
            return null;
        }
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0.0f, 0.0f, (float) source.getWidth(), (float) source.getHeight());
        float f = this.radius;
        canvas.drawRoundRect(rectF, f, f, paint);
        return result;
    }

    public String getId() {
        return getClass().getName() + Math.round(this.radius);
    }

    public void updateDiskCacheKey(MessageDigest messageDigest) {
    }
}
