package im.bclpbkiauv.messenger.utils;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import androidx.core.graphics.drawable.DrawableCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class DrawableUtils {
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes == null) {
            return null;
        }
        if (opts != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static Drawable tintListDrawable(Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public static Drawable createLayerDrawable(int innerColor, int strokeColor, float conrnerRadius) {
        return createLayerDrawable(innerColor, strokeColor, conrnerRadius, 0, 0, 0, 0);
    }

    public static Drawable createLayerDrawable(int innerColor, int strokeColor, float conrnerRadius, int insetLeft, int insetTop, int insetRight, int insetBottom) {
        GradientDrawable roundRect = new GradientDrawable();
        roundRect.setShape(0);
        roundRect.setColor(0);
        GradientDrawable innerRect = new GradientDrawable();
        innerRect.setShape(0);
        float f = conrnerRadius;
        innerRect.setCornerRadius(conrnerRadius);
        int i = innerColor;
        innerRect.setColor(innerColor);
        int i2 = strokeColor;
        innerRect.setStroke(AndroidUtilities.dp(0.5f), strokeColor);
        return new LayerDrawable(new Drawable[]{roundRect, new InsetDrawable(innerRect, insetLeft, insetTop, insetRight, insetBottom)});
    }

    public static Drawable getGradientDrawable(float[] radii, int... colors) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadii(radii);
        return gradientDrawable;
    }
}
