package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
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

public class AvatarCell extends FrameLayout {
    private int accountNumber;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private ImageView ivArrow;
    private ImageView ivFlag;
    private ImageView ivQrCode;
    private TextView tvId;
    private TextView tvName;

    public AvatarCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public AvatarCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
        initDefault();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(176.0f), 1073741824));
    }

    private void initLayout(Context context) {
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(16.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImage, LayoutHelper.createFrame(64.0f, 64.0f, 51, 30.0f, 70.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.tvName = textView;
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.tvName.setTextSize(1, 15.0f);
        this.tvName.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.tvName.setLines(1);
        this.tvName.setMaxLines(1);
        this.tvName.setSingleLine(true);
        this.tvName.setGravity(19);
        this.tvName.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.tvName, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 108.0f, 80.0f, 16.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.tvId = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        this.tvId.setTextSize(1, 15.0f);
        this.tvId.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.tvId.setLines(1);
        this.tvId.setMaxLines(1);
        this.tvId.setSingleLine(true);
        this.tvId.setGravity(19);
        this.tvId.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.tvId, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 108.0f, 113.0f, 16.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.ivFlag = imageView;
        imageView.setImageResource(R.mipmap.fmt_me_vip_icon);
        addView(this.ivFlag, LayoutHelper.createFrame(16.0f, 16.0f, 51, 75.0f, 118.0f, 0.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.ivQrCode = imageView2;
        imageView2.setImageResource(R.mipmap.fmt_me_qr_code_icon);
        addView(this.ivQrCode, LayoutHelper.createFrame(12.0f, 12.0f, 51, 224.0f, 113.0f, 0.0f, 0.0f));
        ImageView imageView3 = new ImageView(context);
        this.ivArrow = imageView3;
        imageView3.setImageResource(R.mipmap.icon_arrow_right);
        addView(this.ivArrow, LayoutHelper.createFrame(12.0f, 12.0f, 53, 0.0f, 100.0f, 20.0f, 0.0f));
        setWillNotDraw(false);
    }

    private void initDefault() {
        setAccount(UserConfig.selectedAccount);
    }

    public void setAccount(int account) {
        this.accountNumber = account;
        TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
        if (user != null) {
            this.avatarDrawable.setInfo(user);
            this.tvName.setText(ContactsController.formatName(user.first_name, user.last_name));
            TextView textView = this.tvId;
            textView.setText("ID: " + user.id);
            this.avatarImage.getImageReceiver().setCurrentAccount(account);
            this.avatarImage.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        }
    }
}
