package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.helper.MryAlphaViewHelper;

public class MryAlphaImageView extends AppCompatImageView implements MryAlphaViewInf {
    private MryAlphaViewHelper mAlphaViewHelper;
    private boolean mChangedAlphaWhenPressedEnable = true;
    private int mNightThemeColor;
    private boolean mUseDefaultColorFilter;

    public MryAlphaImageView(Context context) {
        super(context);
    }

    public MryAlphaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MryAlphaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.MryAlphaImageView);
        this.mUseDefaultColorFilter = t.getBoolean(0, false);
        t.recycle();
        if (this.mUseDefaultColorFilter) {
            setColor(PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setColor(PorterDuff.Mode mode) {
        if (Theme.getCurrentTheme() == null || Theme.getCurrentTheme().isDark() || this.mNightThemeColor == 0) {
            setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueButton), mode));
        } else {
            setColorFilter(new PorterDuffColorFilter(this.mNightThemeColor, mode));
        }
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

    public void setNightThemeColor(int nightThemeColor) {
        setNightThemeColor(nightThemeColor, PorterDuff.Mode.SRC_IN);
    }

    public void setNightThemeColor(int nightThemeColor, PorterDuff.Mode mode) {
        this.mNightThemeColor = nightThemeColor;
        setColor(mode);
    }

    public void setBackgroundColorMultiply(String colorThemeKey) {
        setBackgroundColor(colorThemeKey, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackgroundColorSrcIn(String colorThemeKey) {
        setBackgroundColor(colorThemeKey, PorterDuff.Mode.SRC_IN);
    }

    public void setBackgroundColor(String colorThemeKey, PorterDuff.Mode mode) {
        if (colorThemeKey != null) {
            super.setBackgroundColor(Theme.getColor(colorThemeKey));
            if (Build.VERSION.SDK_INT >= 21) {
                setBackgroundTintMode(mode);
            }
        }
    }

    public void setBackgroundResourceMultiply(int resId, String colorThemeKey) {
        setBackgroundResource(resId, colorThemeKey, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackgroundResourceSrcIn(int resId, String colorThemeKey) {
        setBackgroundResource(resId, colorThemeKey, PorterDuff.Mode.SRC_IN);
    }

    public void setBackgroundResource(int resId, String colorThemeKey, PorterDuff.Mode mode) {
        super.setBackgroundResource(resId);
        setColorFilter(colorThemeKey, mode);
    }

    public void setImageResourceMultiply(int resId, String colorThemeKey) {
        setImageResource(resId, colorThemeKey, PorterDuff.Mode.MULTIPLY);
    }

    public void setImageResourceSrcIn(int resId, String colorThemeKey) {
        setImageResource(resId, colorThemeKey, PorterDuff.Mode.SRC_IN);
    }

    public void setImageResource(int resId, String colorThemeKey, PorterDuff.Mode mode) {
        super.setImageResource(resId);
        setColorFilter(colorThemeKey, mode);
    }

    public void setColorFilterMultiply(String colorThemeKey) {
        setColorFilter(colorThemeKey, PorterDuff.Mode.MULTIPLY);
    }

    public void setColorFilterSrcIn(String colorThemeKey) {
        setColorFilter(colorThemeKey, PorterDuff.Mode.SRC_IN);
    }

    public void setColorFilter(String colorThemeKey, PorterDuff.Mode mode) {
        if (colorThemeKey != null) {
            super.setColorFilter(Theme.getColor(colorThemeKey), mode);
        }
    }
}
