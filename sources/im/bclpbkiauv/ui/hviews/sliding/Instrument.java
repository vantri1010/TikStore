package im.bclpbkiauv.ui.hviews.sliding;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;

public class Instrument {
    private static Instrument mInstrument;

    public static Instrument getInstance() {
        if (mInstrument == null) {
            mInstrument = new Instrument();
        }
        return mInstrument;
    }

    public float getTranslationY(View view) {
        if (view == null) {
            return 0.0f;
        }
        if (Build.VERSION.SDK_INT >= 11) {
            return view.getTranslationY();
        }
        return view.getTranslationY();
    }

    public void slidingByDelta(View view, View followView, float delta) {
        if (view != null) {
            view.clearAnimation();
            if (Build.VERSION.SDK_INT >= 11) {
                view.setTranslationY(delta);
            } else {
                view.setTranslationY(delta);
            }
            if (followView != null) {
                followView.clearAnimation();
                if (Build.VERSION.SDK_INT >= 11) {
                    followView.setTranslationY(delta);
                } else {
                    followView.setTranslationY(delta);
                }
            }
        }
    }

    public void slidingToY(View view, float y) {
        if (view != null) {
            view.clearAnimation();
            if (Build.VERSION.SDK_INT >= 11) {
                view.setY(y);
            } else {
                view.setY(y);
            }
        }
    }

    public void reset(View view, View followView, long duration) {
        if (view != null) {
            view.clearAnimation();
            ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0f}).setDuration(duration).start();
            if (followView != null) {
                followView.clearAnimation();
                ObjectAnimator.ofFloat(followView, "translationY", new float[]{0.0f}).setDuration(duration).start();
            }
        }
    }

    public void smoothTo(View view, float y, long duration) {
        if (view != null) {
            view.clearAnimation();
            ObjectAnimator.ofFloat(view, "translationY", new float[]{y}).setDuration(duration).start();
        }
    }
}
