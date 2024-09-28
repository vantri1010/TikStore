package im.bclpbkiauv.ui.hviews.helper;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;

public class MryResHelper {
    private static TypedValue sTmpValue;

    public static float getAttrFloatValue(Context context, int attr) {
        return getAttrFloatValue(context.getTheme(), attr);
    }

    public static float getAttrFloatValue(Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        theme.resolveAttribute(attr, sTmpValue, true);
        return sTmpValue.getFloat();
    }

    public static int getAttrColor(Context context, int attrRes) {
        return getAttrColor(context.getTheme(), attrRes);
    }

    public static int getAttrColor(Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        theme.resolveAttribute(attr, sTmpValue, true);
        if (sTmpValue.type == 2) {
            return getAttrColor(theme, sTmpValue.data);
        }
        return sTmpValue.data;
    }

    public static ColorStateList getAttrColorStateList(Context context, int attrRes) {
        return getAttrColorStateList(context, context.getTheme(), attrRes);
    }

    public static ColorStateList getAttrColorStateList(Context context, Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        theme.resolveAttribute(attr, sTmpValue, true);
        if (sTmpValue.type >= 28 && sTmpValue.type <= 31) {
            return ColorStateList.valueOf(sTmpValue.data);
        }
        if (sTmpValue.type == 2) {
            return getAttrColorStateList(context, theme, sTmpValue.data);
        }
        return ContextCompat.getColorStateList(context, sTmpValue.resourceId);
    }

    public static Drawable getAttrDrawable(Context context, int attr) {
        return getAttrDrawable(context, context.getTheme(), attr);
    }

    public static Drawable getAttrDrawable(Context context, Resources.Theme theme, int attr) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        theme.resolveAttribute(attr, sTmpValue, true);
        if (sTmpValue.type >= 28 && sTmpValue.type <= 31) {
            return new ColorDrawable(sTmpValue.data);
        }
        if (sTmpValue.type == 2) {
            return getAttrDrawable(context, theme, sTmpValue.data);
        }
        if (sTmpValue.resourceId != 0) {
            return MryDrawableHelper.getVectorDrawable(context, sTmpValue.resourceId);
        }
        return null;
    }

    public static Drawable getAttrDrawable(Context context, TypedArray typedArray, int index) {
        TypedValue value = typedArray.peekValue(index);
        if (value == null || value.type == 2 || value.resourceId == 0) {
            return null;
        }
        return MryDrawableHelper.getVectorDrawable(context, value.resourceId);
    }

    public static int getAttrDimen(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        context.getTheme().resolveAttribute(attrRes, sTmpValue, true);
        return TypedValue.complexToDimensionPixelSize(sTmpValue.data, MryDisplayHelper.getDisplayMetrics(context));
    }

    public static String getAttrString(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        context.getTheme().resolveAttribute(attrRes, sTmpValue, true);
        CharSequence str = sTmpValue.string;
        if (str == null) {
            return null;
        }
        return str.toString();
    }

    public static int getAttrInt(Context context, int attrRes) {
        if (sTmpValue == null) {
            sTmpValue = new TypedValue();
        }
        context.getTheme().resolveAttribute(attrRes, sTmpValue, true);
        return sTmpValue.data;
    }
}
