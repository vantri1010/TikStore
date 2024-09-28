package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.URLSpanNoUnderline;

public class AdminedChannelCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC.Chat currentChannel;
    private ImageView deleteButton;
    private boolean isLast;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AdminedChannelCell(Context context, View.OnClickListener onClickListener) {
        super(context);
        Context context2 = context;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 12.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 15.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        this.statusTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 38.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.deleteButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.deleteButton.setImageResource(R.drawable.msg_panel_clear);
        this.deleteButton.setOnClickListener(onClickListener);
        this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), PorterDuff.Mode.MULTIPLY));
        addView(this.deleteButton, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 12.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
    }

    public void setChannel(TLRPC.Chat channel, boolean last) {
        String url = MessagesController.getInstance(this.currentAccount).linkPrefix + "/";
        this.currentChannel = channel;
        this.avatarDrawable.setInfo(channel);
        this.nameTextView.setText(channel.title);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(url + channel.username);
        stringBuilder.setSpan(new URLSpanNoUnderline(""), url.length(), stringBuilder.length(), 33);
        this.statusTextView.setText(stringBuilder);
        this.avatarImageView.setImage(ImageLocation.getForChat(channel, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChannel);
        this.isLast = last;
    }

    public void update() {
        this.avatarDrawable.setInfo(this.currentChannel);
        this.avatarImageView.invalidate();
    }

    public TLRPC.Chat getCurrentChannel() {
        return this.currentChannel;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.isLast ? 12 : 0) + 60)), 1073741824));
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public SimpleTextView getNameTextView() {
        return this.nameTextView;
    }

    public SimpleTextView getStatusTextView() {
        return this.statusTextView;
    }

    public ImageView getDeleteButton() {
        return this.deleteButton;
    }
}
