package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class AlphaPageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private float mMinAlpha = 0.5f;

    public AlphaPageTransformer() {
    }

    public AlphaPageTransformer(float minAlpha) {
        this.mMinAlpha = minAlpha;
    }

    public void transformPage(View view, float position) {
        view.setScaleX(0.999f);
        if (position < -1.0f) {
            view.setAlpha(this.mMinAlpha);
        } else if (position > 1.0f) {
            view.setAlpha(this.mMinAlpha);
        } else if (position < 0.0f) {
            float f = this.mMinAlpha;
            view.setAlpha(f + ((1.0f - f) * (1.0f + position)));
        } else {
            float f2 = this.mMinAlpha;
            view.setAlpha(f2 + ((1.0f - f2) * (1.0f - position)));
        }
    }
}
