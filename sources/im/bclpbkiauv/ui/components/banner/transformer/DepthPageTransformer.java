package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class DepthPageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MIN_SCALE = 0.75f;
    private float mMinScale = 0.75f;

    public DepthPageTransformer() {
    }

    public DepthPageTransformer(float minScale) {
        this.mMinScale = minScale;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1.0f) {
            view.setAlpha(0.0f);
        } else if (position <= 0.0f) {
            view.setAlpha(1.0f);
            view.setTranslationX(0.0f);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
        } else if (position <= 1.0f) {
            view.setVisibility(0);
            view.setAlpha(1.0f - position);
            view.setTranslationX(((float) pageWidth) * (-position));
            float f = this.mMinScale;
            float scaleFactor = f + ((1.0f - f) * (1.0f - Math.abs(position)));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            if (position == 1.0f) {
                view.setVisibility(4);
            }
        } else {
            view.setAlpha(0.0f);
        }
    }
}
