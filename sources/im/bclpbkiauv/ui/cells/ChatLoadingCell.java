package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;

public class ChatLoadingCell extends FrameLayout {
    private FrameLayout frameLayout;
    private RadialProgressView progressBar;

    public ChatLoadingCell(Context context) {
        super(context);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout = frameLayout2;
        frameLayout2.setBackgroundResource(R.drawable.system_loader);
        this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
        addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), 1073741824));
    }

    public void setProgressVisible(boolean value) {
        this.frameLayout.setVisibility(value ? 0 : 4);
    }
}
