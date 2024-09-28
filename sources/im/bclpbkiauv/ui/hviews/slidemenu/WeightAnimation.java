package im.bclpbkiauv.ui.hviews.slidemenu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class WeightAnimation extends Animation {
    private float deltaWeight = -1.0f;
    private final float endWeight;
    private float startWeight = -1.0f;
    private View view;

    WeightAnimation(float endWeight2, View view2) {
        this.endWeight = endWeight2;
        this.view = view2;
        setDuration(200);
    }

    public View getView() {
        return this.view;
    }

    /* access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
        if (this.startWeight < 0.0f) {
            float viewWeight = Utils.getViewWeight(this.view);
            this.startWeight = viewWeight;
            this.deltaWeight = this.endWeight - viewWeight;
        }
        Utils.setViewWeight(this.view, this.startWeight + (this.deltaWeight * interpolatedTime));
    }

    public boolean willChangeBounds() {
        return true;
    }
}
