package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class GroupCreateSectionCell extends FrameLayout {
    private Drawable drawable;
    private TextView textView;

    public GroupCreateSectionCell(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_graySection));
        Drawable drawable2 = getResources().getDrawable(R.drawable.shadowdown);
        this.drawable = drawable2;
        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_groupcreate_sectionShadow), PorterDuff.Mode.MULTIPLY));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor(Theme.key_groupcreate_sectionText));
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (!LocaleController.isRTL ? 3 : i) | 48, 16.0f, 0.0f, 16.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
        this.drawable.draw(canvas);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }
}
