package im.bclpbkiauv.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class AnimationUtils {
    public static void executeAlphaScaleDisplayAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
        animatorSet.setDuration(280);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "alpha", new float[]{0.2f, 1.0f}), ObjectAnimator.ofFloat(view, "scaleX", new float[]{0.95f, 1.0f}), ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.95f, 1.0f})});
        animatorSet.start();
    }
}
