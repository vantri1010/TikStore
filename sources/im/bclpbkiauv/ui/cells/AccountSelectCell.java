package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class AccountSelectCell extends FrameLayout {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private ImageView checkImageView;
    private BackupImageView imageView;
    private TextView textView;

    public AccountSelectCell(Context context) {
        super(context);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
        addView(this.imageView, LayoutHelper.createFrame(36.0f, 36.0f, 51, 10.0f, 10.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(19);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.textView, LayoutHelper.createFrame(-1.0f, -1.0f, 51, 61.0f, 0.0f, 56.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.checkImageView = imageView2;
        imageView2.setImageResource(R.drawable.account_check);
        this.checkImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.checkImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemCheck), PorterDuff.Mode.MULTIPLY));
        addView(this.checkImageView, LayoutHelper.createFrame(40.0f, -1.0f, 53, 0.0f, 0.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
    }

    public void setAccount(int account, boolean check) {
        this.accountNumber = account;
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        this.avatarDrawable.setInfo(user);
        this.textView.setText(ContactsController.formatName(user.first_name, user.last_name));
        this.imageView.getImageReceiver().setCurrentAccount(account);
        int i = 0;
        this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        ImageView imageView2 = this.checkImageView;
        if (!check || account != UserConfig.selectedAccount) {
            i = 4;
        }
        imageView2.setVisibility(i);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }
}
