package im.bclpbkiauv.ui.utils;

import android.content.Context;
import android.util.AttributeSet;
import androidx.constraintlayout.motion.widget.MotionLayout;
import com.google.android.material.appbar.AppBarLayout;

public class CollapsibleToolbar extends MotionLayout implements AppBarLayout.OnOffsetChangedListener {
    public CollapsibleToolbar(Context context) {
        super(context);
    }

    public CollapsibleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsibleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof AppBarLayout) {
            ((AppBarLayout) getParent()).addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this);
        }
    }

    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        setProgress(((float) (-verticalOffset)) / ((float) appBarLayout.getTotalScrollRange()));
    }

    public boolean isInEditMode() {
        return true;
    }
}
