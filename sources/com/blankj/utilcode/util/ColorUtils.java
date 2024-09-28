package com.blankj.utilcode.util;

import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.ViewCompat;

public final class ColorUtils {
    private ColorUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static int getColor(int id) {
        return ContextCompat.getColor(Utils.getApp(), id);
    }

    public static int setAlphaComponent(int color, int alpha) {
        return (16777215 & color) | (alpha << 24);
    }

    public static int setAlphaComponent(int color, float alpha) {
        return (16777215 & color) | (((int) ((255.0f * alpha) + 0.5f)) << 24);
    }

    public static int setRedComponent(int color, int red) {
        return (-16711681 & color) | (red << 16);
    }

    public static int setRedComponent(int color, float red) {
        return (-16711681 & color) | (((int) ((255.0f * red) + 0.5f)) << 16);
    }

    public static int setGreenComponent(int color, int green) {
        return (-65281 & color) | (green << 8);
    }

    public static int setGreenComponent(int color, float green) {
        return (-65281 & color) | (((int) ((255.0f * green) + 0.5f)) << 8);
    }

    public static int setBlueComponent(int color, int blue) {
        return (color & InputDeviceCompat.SOURCE_ANY) | blue;
    }

    public static int setBlueComponent(int color, float blue) {
        return (color & InputDeviceCompat.SOURCE_ANY) | ((int) ((255.0f * blue) + 0.5f));
    }

    public static int string2Int(String colorString) {
        if (colorString != null) {
            return Color.parseColor(colorString);
        }
        throw new NullPointerException("Argument 'colorString' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String int2RgbString(int colorInt) {
        String color = Integer.toHexString(colorInt & ViewCompat.MEASURED_SIZE_MASK);
        while (color.length() < 6) {
            color = "0" + color;
        }
        return "#" + color;
    }

    public static String int2ArgbString(int colorInt) {
        String color = Integer.toHexString(colorInt);
        while (color.length() < 6) {
            color = "0" + color;
        }
        while (color.length() < 8) {
            color = "f" + color;
        }
        return "#" + color;
    }

    public static int getRandomColor() {
        return getRandomColor(true);
    }

    public static int getRandomColor(boolean supportAlpha) {
        return ((int) (Math.random() * 1.6777216E7d)) | (supportAlpha ? ((int) (Math.random() * 256.0d)) << 24 : -16777216);
    }
}
