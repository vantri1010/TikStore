package im.bclpbkiauv.ui.hviews.helper;

import android.view.View;
import im.bclpbkiauv.messenger.R;
import java.lang.ref.WeakReference;

public class MryAlphaViewHelper {
    private boolean mChangeAlphaWhenDisable = true;
    private boolean mChangeAlphaWhenPress = true;
    private float mDisabledAlpha = 0.5f;
    private float mNormalAlpha = 1.0f;
    private float mPressedAlpha = 0.5f;
    private WeakReference<View> mTarget;

    public MryAlphaViewHelper(View target) {
        this.mTarget = new WeakReference<>(target);
        this.mPressedAlpha = MryResHelper.getAttrFloatValue(target.getContext(), (int) R.attr.mryAlphaPressed);
        this.mDisabledAlpha = MryResHelper.getAttrFloatValue(target.getContext(), (int) R.attr.mryAlphaDisabled);
    }

    public MryAlphaViewHelper(View target, float pressedAlpha, float disabledAlpha) {
        this.mTarget = new WeakReference<>(target);
        this.mPressedAlpha = pressedAlpha;
        this.mDisabledAlpha = disabledAlpha;
    }

    public void onPressedChanged(View current, boolean pressed) {
        View target = (View) this.mTarget.get();
        if (target != null) {
            if (current.isEnabled()) {
                target.setAlpha((!this.mChangeAlphaWhenPress || !pressed || !current.isClickable()) ? this.mNormalAlpha : this.mPressedAlpha);
            } else if (this.mChangeAlphaWhenDisable) {
                target.setAlpha(this.mDisabledAlpha);
            }
        }
    }

    public void onEnabledChanged(View current, boolean enabled) {
        float alphaForIsEnable;
        View target = (View) this.mTarget.get();
        if (target != null) {
            if (this.mChangeAlphaWhenDisable) {
                alphaForIsEnable = enabled ? this.mNormalAlpha : this.mDisabledAlpha;
            } else {
                alphaForIsEnable = this.mNormalAlpha;
            }
            if (!(current == target || target.isEnabled() == enabled)) {
                target.setEnabled(enabled);
            }
            target.setAlpha(alphaForIsEnable);
        }
    }

    public void setChangeAlphaWhenPress(boolean changeAlphaWhenPress) {
        this.mChangeAlphaWhenPress = changeAlphaWhenPress;
    }

    public void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable) {
        this.mChangeAlphaWhenDisable = changeAlphaWhenDisable;
        View target = (View) this.mTarget.get();
        if (target != null) {
            onEnabledChanged(target, target.isEnabled());
        }
    }
}
