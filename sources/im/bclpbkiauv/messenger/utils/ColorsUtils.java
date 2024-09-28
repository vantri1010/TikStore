package im.bclpbkiauv.messenger.utils;

import android.graphics.Color;

public class ColorsUtils {
    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] - 0.1f;
        return Color.HSVToColor(hsv);
    }

    public int getLrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] - 0.1f;
        hsv[2] = hsv[2] + 0.1f;
        return Color.HSVToColor(hsv);
    }
}
