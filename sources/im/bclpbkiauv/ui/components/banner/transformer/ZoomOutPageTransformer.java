package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class ZoomOutPageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private float mMinAlpha = 0.5f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public ZoomOutPageTransformer() {
    }

    public ZoomOutPageTransformer(float minScale, float minAlpha) {
        this.mMinScale = minScale;
        this.mMinAlpha = minAlpha;
    }

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        if (position < -1.0f) {
            view.setAlpha(0.0f);
        } else if (position <= 1.0f) {
            float scaleFactor = Math.max(this.mMinScale, 1.0f - Math.abs(position));
            float vertMargin = (((float) pageHeight) * (1.0f - scaleFactor)) / 2.0f;
            float horzMargin = (((float) pageWidth) * (1.0f - scaleFactor)) / 2.0f;
            if (position < 0.0f) {
                view.setTranslationX(horzMargin - (vertMargin / 2.0f));
            } else {
                view.setTranslationX((-horzMargin) + (vertMargin / 2.0f));
            }
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            float f = this.mMinAlpha;
            float f2 = this.mMinScale;
            view.setAlpha(f + (((scaleFactor - f2) / (1.0f - f2)) * (1.0f - f)));
        } else {
            view.setAlpha(0.0f);
        }
    }
}
