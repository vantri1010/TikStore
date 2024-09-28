package im.bclpbkiauv.ui.components;

import android.graphics.drawable.GradientDrawable;

public class BackgroundGradientDrawable extends GradientDrawable {
    private int[] colors;

    public BackgroundGradientDrawable(GradientDrawable.Orientation orientation, int[] colors2) {
        super(orientation, colors2);
        this.colors = colors2;
    }

    public int[] getColorsList() {
        return this.colors;
    }
}
