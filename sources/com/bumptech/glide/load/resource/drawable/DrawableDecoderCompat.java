package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public final class DrawableDecoderCompat {
    private static volatile boolean shouldCallAppCompatResources = true;

    private DrawableDecoderCompat() {
    }

    public static Drawable getDrawable(Context ourContext, Context targetContext, int id) {
        return getDrawable(ourContext, targetContext, id, (Resources.Theme) null);
    }

    public static Drawable getDrawable(Context ourContext, int id, Resources.Theme theme) {
        return getDrawable(ourContext, ourContext, id, theme);
    }

    private static Drawable getDrawable(Context ourContext, Context targetContext, int id, Resources.Theme theme) {
        try {
            if (shouldCallAppCompatResources) {
                return loadDrawableV7(targetContext, id, theme);
            }
        } catch (NoClassDefFoundError e) {
            shouldCallAppCompatResources = false;
        } catch (IllegalStateException e2) {
            if (!ourContext.getPackageName().equals(targetContext.getPackageName())) {
                return ContextCompat.getDrawable(targetContext, id);
            }
            throw e2;
        } catch (Resources.NotFoundException e3) {
        }
        return loadDrawableV4(targetContext, id, theme != null ? theme : targetContext.getTheme());
    }

    private static Drawable loadDrawableV7(Context context, int id, Resources.Theme theme) {
        return AppCompatResources.getDrawable(theme != null ? new ContextThemeWrapper(context, theme) : context, id);
    }

    private static Drawable loadDrawableV4(Context context, int id, Resources.Theme theme) {
        return ResourcesCompat.getDrawable(context.getResources(), id, theme);
    }
}
