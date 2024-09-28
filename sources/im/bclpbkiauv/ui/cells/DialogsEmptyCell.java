package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class DialogsEmptyCell extends LinearLayout {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentType;
    private ImageView emptyImageView;
    private TextView emptyTextView2;

    public DialogsEmptyCell(Context context) {
        super(context);
        setGravity(17);
        setOrientation(1);
        setOnTouchListener($$Lambda$DialogsEmptyCell$xbM5hg8IIJQOG7PbXNm1U3FYJZY.INSTANCE);
        ImageView imageView = new ImageView(context);
        this.emptyImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.emptyImageView.setImageResource(R.mipmap.img_empty_default);
        addView(this.emptyImageView, LayoutHelper.createLinear(175, 165, 1, 52, 4, 52, 0));
        this.emptyTextView2 = new TextView(context);
        String help = LocaleController.getString("NoDialogsInCurrent", R.string.NoDialogsInCurrent);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace(10, ' ');
        }
        this.emptyTextView2.setText(help);
        this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_chats_message));
        this.emptyTextView2.setTextSize(1, 14.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        addView(this.emptyTextView2, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 52.0f, 7.0f, 52.0f, 0.0f));
    }

    static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    public void setType(int value) {
        String help;
        this.currentType = value;
        if (value == 0) {
            help = LocaleController.getString("NoDialogsInCurrent", R.string.NoDialogsInCurrent);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
        } else {
            help = LocaleController.getString("NoDialogsInCurrent", R.string.NoDialogsInCurrent);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
        }
        this.emptyTextView2.setText(help);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (totalHeight == 0) {
            totalHeight = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
        }
        if (this.currentType == 0) {
            ArrayList<TLRPC.RecentMeUrl> arrayList = MessagesController.getInstance(this.currentAccount).hintDialogs;
            if (!arrayList.isEmpty()) {
                totalHeight -= (((AndroidUtilities.dp(72.0f) * arrayList.size()) + arrayList.size()) - 1) + AndroidUtilities.dp(50.0f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(totalHeight, 1073741824));
            return;
        }
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(166.0f), 1073741824));
    }
}
