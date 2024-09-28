package im.bclpbkiauv.ui.hviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;

public class MryImageView extends MryAlphaImageView {
    private boolean render;

    public MryImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MryImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MryImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.render = false;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MryImageView);
            this.render = ta.getBoolean(0, false);
            ta.recycle();
        }
        if (this.render) {
            setColorFilterMultiply(Theme.key_actionBarDefaultIcon);
        }
    }
}
