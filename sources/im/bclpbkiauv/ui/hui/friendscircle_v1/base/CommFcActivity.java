package im.bclpbkiauv.ui.hui.friendscircle_v1.base;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.actionbar.ActionBar;

public abstract class CommFcActivity extends BaseFcActivity {
    protected RecyclerView.LayoutManager layoutManager;

    public View createView(Context context) {
        super.createView(context);
        return this.fragmentView;
    }

    public void hideTitle(View rootView) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rootView, "translationY", new float[]{0.0f, (float) (-ActionBar.getCurrentActionBarHeight())});
        animator.setDuration(300);
        animator.start();
        this.actionBar.setVisibility(4);
    }

    public void showTitle(View rootView) {
        ObjectAnimator.ofFloat(rootView, "translationY", new float[]{(float) (-ActionBar.getCurrentActionBarHeight()), 0.0f}).start();
        this.actionBar.setVisibility(0);
    }
}
