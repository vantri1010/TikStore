package im.bclpbkiauv.ui.components.banner.transformer;

import android.view.View;

public class RotateYTransformer extends BasePageTransformer {
    private static final float DEFAULT_MAX_ROTATE = 35.0f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;

    public RotateYTransformer() {
    }

    public RotateYTransformer(float maxRotate) {
        this.mMaxRotate = maxRotate;
    }

    public void transformPage(View view, float position) {
        view.setPivotY((float) (view.getHeight() / 2));
        if (position < -1.0f) {
            view.setRotationY(this.mMaxRotate * -1.0f);
            view.setPivotX((float) view.getWidth());
        } else if (position <= 1.0f) {
            view.setRotationY(this.mMaxRotate * position);
            if (position < 0.0f) {
                view.setPivotX(((float) view.getWidth()) * (((-position) * 0.5f) + 0.5f));
                view.setPivotX((float) view.getWidth());
                return;
            }
            view.setPivotX(((float) view.getWidth()) * 0.5f * (1.0f - position));
            view.setPivotX(0.0f);
        } else {
            view.setRotationY(this.mMaxRotate * 1.0f);
            view.setPivotX(0.0f);
        }
    }
}
