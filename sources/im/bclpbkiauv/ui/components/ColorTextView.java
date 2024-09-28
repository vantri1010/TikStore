package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ColorTextView extends TextView {
    public ColorTextView(Context context) {
        super(context);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTextColor(context, attrs);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomTextColor(context, attrs);
    }

    private void setCustomTextColor(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorTextView);
        int indexCount = a.getIndexCount();
        String strType = "";
        String strKey = "";
        for (int i = 0; i < indexCount; i++) {
            int attr = a.getIndex(i);
            if (attr == 0) {
                strKey = a.getString(attr);
            } else if (attr == 1) {
                strType = a.getString(attr);
            }
        }
        if (strType.equals("title")) {
            setTextColor(Theme.getColor(Theme.key_graySectionText));
        } else if (strType.equals("label")) {
            setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        } else if (strType.equals("value")) {
            setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        }
        if (!"".equals(strKey)) {
            setText(LocaleController.getString(strKey, getResources().getIdentifier(strKey, "string", context.getPackageName())));
        }
        a.recycle();
    }
}
