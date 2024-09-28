package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.util.AttributeSet;
import com.google.android.material.tabs.TabLayout;
import im.bclpbkiauv.ui.actionbar.Theme;

public class MryTabLayout extends TabLayout {
    public MryTabLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (Theme.getCurrentTheme() == null) {
            return;
        }
        if (Theme.getCurrentTheme().isDark()) {
            setTabTextColors(Theme.getColor(Theme.key_actionBarTabUnactiveText), Theme.getColor(Theme.key_actionBarTabActiveText));
            setSelectedTabIndicatorColor(Theme.getColor(Theme.key_actionBarTabActiveText));
            return;
        }
        setTabTextColors(Theme.getColor(Theme.key_actionBarTabUnactiveText), Theme.getColor(Theme.key_actionBarTabActiveText));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
