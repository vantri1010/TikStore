package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class LocationPoweredCell extends FrameLayout {
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;

    public LocationPoweredCell(Context context) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        addView(linearLayout, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView3 = new TextView(context);
        this.textView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.textView.setText("Powered by");
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2));
        ImageView imageView2 = new ImageView(context);
        this.imageView = imageView2;
        imageView2.setImageResource(R.drawable.foursquare);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), PorterDuff.Mode.MULTIPLY));
        this.imageView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(35, -2));
        TextView textView4 = new TextView(context);
        this.textView2 = textView4;
        textView4.setTextSize(1, 14.0f);
        this.textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.textView2.setText("Foursquare");
        linearLayout.addView(this.textView2, LayoutHelper.createLinear(-2, -2));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
    }
}
