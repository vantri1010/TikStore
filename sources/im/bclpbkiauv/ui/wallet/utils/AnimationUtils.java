package im.bclpbkiauv.ui.wallet.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

    public static void animationShow(View view) {
        view.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
        animatorSet.setDuration(280);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "alpha", new float[]{0.2f, 1.0f}), ObjectAnimator.ofFloat(view, "scaleX", new float[]{0.95f, 1.0f}), ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.95f, 1.0f})});
        animatorSet.start();
    }

    public static void animationHide(final View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
        animatorSet.setDuration(280);
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0f, 0.2f}), ObjectAnimator.ofFloat(view, "scaleX", new float[]{1.0f, 0.95f}), ObjectAnimator.ofFloat(view, "scaleY", new float[]{1.0f, 0.95f})});
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(8);
            }
        });
    }
}
