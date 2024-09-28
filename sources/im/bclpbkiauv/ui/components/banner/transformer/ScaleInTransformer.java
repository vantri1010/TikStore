package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class ScaleInTransformer extends BasePageTransformer {
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public ScaleInTransformer() {
    }

    public ScaleInTransformer(float minScale) {
        this.mMinScale = minScale;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        view.setPivotY((float) (view.getHeight() / 2));
        view.setPivotX((float) (pageWidth / 2));
        if (position < -1.0f) {
            view.setScaleX(this.mMinScale);
            view.setScaleY(this.mMinScale);
            view.setPivotX((float) pageWidth);
        } else if (position > 1.0f) {
            view.setPivotX(0.0f);
            view.setScaleX(this.mMinScale);
            view.setScaleY(this.mMinScale);
        } else if (position < 0.0f) {
            float f = this.mMinScale;
            float scaleFactor = ((position + 1.0f) * (1.0f - f)) + f;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setPivotX(((float) pageWidth) * (((-position) * 0.5f) + 0.5f));
        } else {
            float f2 = this.mMinScale;
            float scaleFactor2 = ((1.0f - position) * (1.0f - f2)) + f2;
            view.setScaleX(scaleFactor2);
            view.setScaleY(scaleFactor2);
            view.setPivotX(((float) pageWidth) * (1.0f - position) * 0.5f);
        }
    }
}
