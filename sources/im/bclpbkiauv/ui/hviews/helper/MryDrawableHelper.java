package im.bclpbkiauv.ui.hviews.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.content.res.AppCompatResources;
import im.bclpbkiauv.messenger.FileLog;

public class MryDrawableHelper {
    private static final String TAG = MryDrawableHelper.class.getSimpleName();
    private static final Canvas sCanvas = new Canvas();

    public static Bitmap createBitmapFromView(View view, float scale) {
        Drawable drawable;
        if ((view instanceof ImageView) && (drawable = ((ImageView) view).getDrawable()) != null && (drawable instanceof BitmapDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely((int) (((float) view.getWidth()) * scale), (int) (((float) view.getHeight()) * scale), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            synchronized (sCanvas) {
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                canvas.save();
                canvas.drawColor(-1);
                canvas.scale(scale, scale);
                view.draw(canvas);
                canvas.restore();
                canvas.setBitmap((Bitmap) null);
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapFromView(View view) {
        return createBitmapFromView(view, 1.0f);
    }

    public static Bitmap createBitmapFromView(View view, int leftCrop, int topCrop, int rightCrop, int bottomCrop) {
        Bitmap cutBitmap;
        Bitmap originBitmap = createBitmapFromView(view);
        if (originBitmap == null || (cutBitmap = createBitmapSafely((view.getWidth() - rightCrop) - leftCrop, (view.getHeight() - topCrop) - bottomCrop, Bitmap.Config.ARGB_8888, 1)) == null) {
            return null;
        }
        Canvas canvas = new Canvas(cutBitmap);
        Rect src = new Rect(leftCrop, topCrop, view.getWidth() - rightCrop, view.getHeight() - bottomCrop);
        Rect dest = new Rect(0, 0, (view.getWidth() - rightCrop) - leftCrop, (view.getHeight() - topCrop) - bottomCrop);
        canvas.drawColor(-1);
        canvas.drawBitmap(originBitmap, src, dest, (Paint) null);
        originBitmap.recycle();
        return cutBitmap;
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount <= 0) {
                return null;
            }
            System.gc();
            return createBitmapSafely(width, height, config, retryCount - 1);
        }
    }

    public static BitmapDrawable createDrawableWithSize(Resources resources, int width, int height, int cornerRadius, int filledColor) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        if (filledColor == 0) {
            filledColor = 0;
        }
        if (cornerRadius > 0) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(filledColor);
            canvas.drawRoundRect(new RectF(0.0f, 0.0f, (float) width, (float) height), (float) cornerRadius, (float) cornerRadius, paint);
        } else {
            canvas.drawColor(filledColor);
        }
        return new BitmapDrawable(resources, output);
    }

    public static ColorFilter setDrawableTintColor(Drawable drawable, int tintColor) {
        LightingColorFilter colorFilter = new LightingColorFilter(Color.argb(255, 0, 0, 0), tintColor);
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        return colorFilter;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return null;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, drawable.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GradientDrawable createCircleGradientDrawable(int startColor, int endColor, int radius, float centerX, float centerY) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{startColor, endColor});
        gradientDrawable.setGradientType(1);
        gradientDrawable.setGradientRadius((float) radius);
        gradientDrawable.setGradientCenter(centerX, centerY);
        return gradientDrawable;
    }

    public static LayerDrawable createItemSeparatorBg(int separatorColor, int bgColor, int separatorHeight, boolean top) {
        ShapeDrawable separator = new ShapeDrawable();
        separator.getPaint().setStyle(Paint.Style.FILL);
        separator.getPaint().setColor(separatorColor);
        ShapeDrawable bg = new ShapeDrawable();
        bg.getPaint().setStyle(Paint.Style.FILL);
        bg.getPaint().setColor(bgColor);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{separator, bg});
        layerDrawable.setLayerInset(1, 0, top ? separatorHeight : 0, 0, top ? 0 : separatorHeight);
        return layerDrawable;
    }

    public static Drawable getVectorDrawable(Context context, int resVector) {
        try {
            return AppCompatResources.getDrawable(context, resVector);
        } catch (Exception e) {
            FileLog.d(TAG + " =====> Error in getVectorDrawable. resVector=" + resVector + ", resName=" + context.getResources().getResourceName(resVector) + e.getMessage());
            return null;
        }
    }

    public static Bitmap vectorDrawableToBitmap(Context context, int resVector) {
        Drawable drawable = getVectorDrawable(context, resVector);
        if (drawable == null) {
            return null;
        }
        Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
        drawable.draw(c);
        return b;
    }
}
