package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBoxSquare;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class CheckBoxUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private CheckBoxSquare checkBox;
    private TLRPC.User currentUser;
    private BackupImageView imageView;
    private boolean needDivider;
    private TextView textView;

    public CheckBoxUserCell(Context context, boolean alert) {
        super(context);
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(alert ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 14.0f);
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        int i = 5;
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        int i2 = 21;
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 21 : 94), 0.0f, (float) (!LocaleController.isRTL ? 21 : 94), 0.0f));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(36.0f));
        addView(this.imageView, LayoutHelper.createFrame(36.0f, 36.0f, (LocaleController.isRTL ? 5 : 3) | 48, 48.0f, 7.0f, 48.0f, 0.0f));
        CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context, alert);
        this.checkBox = checkBoxSquare;
        addView(checkBoxSquare, LayoutHelper.createFrame(18.0f, 18.0f, (!LocaleController.isRTL ? 3 : i) | 48, (float) (LocaleController.isRTL ? 0 : 21), 16.0f, (float) (!LocaleController.isRTL ? 0 : i2), 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setTextColor(int color) {
        this.textView.setTextColor(color);
    }

    public TLRPC.User getCurrentUser() {
        return this.currentUser;
    }

    public void setUser(TLRPC.User user, boolean checked, boolean divider) {
        this.currentUser = user;
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.checkBox.setChecked(checked, false);
        this.avatarDrawable.setInfo(user);
        this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        this.needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    public TextView getTextView() {
        return this.textView;
    }

    public CheckBoxSquare getCheckBox() {
        return this.checkBox;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
