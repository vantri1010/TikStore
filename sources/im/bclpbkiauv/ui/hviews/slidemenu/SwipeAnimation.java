package im.bclpbkiauv.ui.hviews.slidemenu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import androidx.core.view.ViewCompat;

class SwipeAnimation extends Animation {
    private View changeXView;
    private boolean left;
    private View resizeView;
    private int startWidth = -1;
    private int width;

    SwipeAnimation(View resizeView2, int width2, View changeXView2, boolean left2) {
        this.resizeView = resizeView2;
        this.width = width2;
        this.changeXView = changeXView2;
        this.left = left2;
        setDuration(300);
        setInterpolator(new DecelerateInterpolator());
    }

    /* access modifiers changed from: protected */
    public void applyTransformation(float interpolatedTime, Transformation t) {
        if (this.startWidth < 0) {
            this.startWidth = this.resizeView.getWidth();
        }
        View view = this.resizeView;
        int i = this.startWidth;
        Utils.setViewWidth(view, i + ((int) ((((float) this.width) - ((float) i)) * interpolatedTime)));
        if (this.left) {
            ViewCompat.setTranslationX(this.changeXView, (float) this.resizeView.getWidth());
        } else {
            ViewCompat.setTranslationX(this.changeXView, (float) (-this.resizeView.getWidth()));
        }
    }

    public boolean willChangeBounds() {
        return true;
    }
}
