package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;

public class LocationLoadingCell extends FrameLayout {
    private RadialProgressView progressBar;
    private TextView textView;

    public LocationLoadingCell(Context context) {
        super(context);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (((float) AndroidUtilities.dp(56.0f)) * 2.5f), 1073741824));
    }

    public void setLoading(boolean value) {
        int i = 0;
        this.progressBar.setVisibility(value ? 0 : 4);
        TextView textView2 = this.textView;
        if (value) {
            i = 4;
        }
        textView2.setVisibility(i);
    }
}
