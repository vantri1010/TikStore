package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import im.bclpbkiauv.ui.hviews.helper.MryAlphaViewHelper;

public class MryAlphaLinearLayout extends LinearLayout implements MryAlphaViewInf {
    private MryAlphaViewHelper mAlphaViewHelper;

    public MryAlphaLinearLayout(Context context) {
        super(context);
    }

    public MryAlphaLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MryAlphaLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private MryAlphaViewHelper getAlphaViewHelper() {
        if (this.mAlphaViewHelper == null) {
            this.mAlphaViewHelper = new MryAlphaViewHelper(this);
        }
        return this.mAlphaViewHelper;
    }

    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        getAlphaViewHelper().onPressedChanged(this, pressed);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getAlphaViewHelper().onEnabledChanged(this, enabled);
    }

    public void setChangeAlphaWhenPress(boolean changeAlphaWhenPress) {
        getAlphaViewHelper().setChangeAlphaWhenPress(changeAlphaWhenPress);
    }

    public void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable) {
        getAlphaViewHelper().setChangeAlphaWhenDisable(changeAlphaWhenDisable);
    }
}
