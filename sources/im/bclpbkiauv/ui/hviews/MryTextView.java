package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hviews.helper.MryAlphaViewHelper;

public class MryTextView extends AppCompatTextView {
    private boolean mAlphaEnable;
    private MryAlphaViewHelper mAlphaViewHelper;

    public MryTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MryTextView);
        this.mAlphaEnable = a.getBoolean(1, true);
        int style = a.getInteger(2, 0);
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

    public final void setPressed(boolean pressed) {
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

    public void setAlphaEnable(boolean mAlphaEnable2) {
        this.mAlphaEnable = mAlphaEnable2;
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
