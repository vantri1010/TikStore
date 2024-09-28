package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class LetterSectionCell extends FrameLayout {
    private TextView textView;

    public LetterSectionCell(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0f), AndroidUtilities.dp(64.0f)));
        TextView textView2 = new TextView(getContext());
        this.textView = textView2;
        textView2.setTextSize(1, 22.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        this.textView.setGravity(17);
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
    }

    public void setLetter(String letter) {
        this.textView.setText(letter.toUpperCase());
    }

    public void setCellHeight(int height) {
        setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0f), height));
    }
}
