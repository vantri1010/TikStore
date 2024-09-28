package im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

public class TagView extends FrameLayout implements Checkable {
    private static final int[] CHECK_STATE = {16842912};
    private boolean isChecked;

    public TagView(Context context) {
        super(context);
    }

    public View getTagView() {
        return getChildAt(0);
    }

    public int[] onCreateDrawableState(int extraSpace) {
        int[] states = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(states, CHECK_STATE);
        }
        return states;
    }

    public void setChecked(boolean checked) {
        if (this.isChecked != checked) {
            this.isChecked = checked;
            refreshDrawableState();
        }
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void toggle() {
        setChecked(!this.isChecked);
    }
}
