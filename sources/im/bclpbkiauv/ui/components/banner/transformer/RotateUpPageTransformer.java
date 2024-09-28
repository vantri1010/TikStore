package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class RotateUpPageTransformer extends BasePageTransformer {
    private static final float DEFAULT_MAX_ROTATE = 15.0f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;

    public RotateUpPageTransformer() {
    }

    public RotateUpPageTransformer(float maxRotate) {
        this.mMaxRotate = maxRotate;
    }

    public void transformPage(View view, float position) {
        if (position < -1.0f) {
            view.setRotation(this.mMaxRotate);
            view.setPivotX((float) view.getWidth());
            view.setPivotY(0.0f);
        } else if (position > 1.0f) {
            view.setRotation(-this.mMaxRotate);
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
        } else if (position < 0.0f) {
            view.setPivotX(((float) view.getWidth()) * (((-position) * 0.5f) + 0.5f));
            view.setPivotY(0.0f);
            view.setRotation((-this.mMaxRotate) * position);
        } else {
            view.setPivotX(((float) view.getWidth()) * 0.5f * (1.0f - position));
            view.setPivotY(0.0f);
            view.setRotation((-this.mMaxRotate) * position);
        }
    }
}
