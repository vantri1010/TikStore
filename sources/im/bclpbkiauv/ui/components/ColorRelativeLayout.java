package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ColorRelativeLayout extends RelativeLayout {
    public ColorRelativeLayout(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    public ColorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    public ColorRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }
}
