package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatButton;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.helper.MryAlphaViewHelper;

public class MryAlphaButton extends AppCompatButton implements MryAlphaViewInf {
    protected boolean mAlphaEnable;
    private MryAlphaViewHelper mAlphaViewHelper;

    public MryAlphaButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryAlphaButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryAlphaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MryAlphaButton);
        int style = a.getInteger(2, 0);
        this.mAlphaEnable = a.getBoolean(1, true);
        boolean render = a.getBoolean(3, false);
        String textKey = a.getString(4);
        String hintKey = a.getString(0);
        a.recycle();
        if (style != 0) {
            if (style == 1) {
                setBold();
            } else if (style == 2) {
                setItalic();
            } else if (style == 3) {
                setBoldAndItalic();
            } else if (style == 4) {
                setMono();
            }
        }
        if (getTextColors() == null || render) {
            setHintColor(Theme.key_windowBackgroundWhiteHintText);
            setTextColor(Theme.key_windowBackgroundWhiteBlackText);
        }
        if (textKey != null) {
            setMryText(getResources().getIdentifier(textKey, "string", context.getPackageName()));
        }
        if (hintKey != null) {
            setMryHint(getResources().getIdentifier(hintKey, "string", context.getPackageName()));
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
        if (this.mAlphaEnable) {
            getAlphaViewHelper().onPressedChanged(this, pressed);
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.mAlphaEnable) {
            getAlphaViewHelper().onEnabledChanged(this, enabled);
        }
    }

    public void setChangeAlphaWhenPress(boolean changeAlphaWhenPress) {
        getAlphaViewHelper().setChangeAlphaWhenPress(changeAlphaWhenPress);
    }

    public void setChangeAlphaWhenDisable(boolean changeAlphaWhenDisable) {
        getAlphaViewHelper().setChangeAlphaWhenDisable(changeAlphaWhenDisable);
    }

    public void setMryText(int resId) {
        setText(LocaleController.getString(resId));
    }

    public void setMryHint(int resId) {
        setHint(LocaleController.getString(resId));
    }

    public void setTextColor(String colorThemeKey) {
        if (colorThemeKey != null) {
            super.setTextColor(Theme.getColor(colorThemeKey));
        }
    }

    public void setHintColor(String colorThemeKey) {
        if (colorThemeKey != null) {
            super.setHintTextColor(Theme.getColor(colorThemeKey));
        }
    }

    public void setBackgroundColor(String colorThemeKey) {
        if (colorThemeKey != null) {
            super.setBackgroundColor(Theme.getColor(colorThemeKey));
        }
    }

    public void setHighlightColor(String colorThemeKey) {
        if (colorThemeKey != null) {
            super.setHighlightColor(Theme.getColor(colorThemeKey));
        }
    }

    public void setBold() {
        setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    }

    public void setItalic() {
        setTypeface(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
    }

    public void setBoldAndItalic() {
        setTypeface(AndroidUtilities.getTypeface("fonts/rmediumitalic.ttf"));
    }

    public void setMono() {
        setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
    }
}
