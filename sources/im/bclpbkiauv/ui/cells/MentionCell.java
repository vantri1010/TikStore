package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class MentionCell extends LinearLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView imageView;
    private TextView nameTextView;
    private TextView usernameTextView;

    public MentionCell(Context context) {
        super(context);
        setOrientation(0);
        AvatarDrawable avatarDrawable2 = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable2;
        avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(14.0f));
        addView(this.imageView, LayoutHelper.createLinear(28, 28, 12.0f, 4.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 15.0f);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 0, 0));
        TextView textView2 = new TextView(context);
        this.usernameTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.usernameTextView.setTextSize(1, 15.0f);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(3);
        this.usernameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.usernameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 8, 0));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), 1073741824));
    }

    public void setUser(TLRPC.User user) {
        if (user == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable((Drawable) null);
            return;
        }
        this.avatarDrawable.setInfo(user);
        if (user.photo == null || user.photo.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
        }
        this.nameTextView.setText(UserObject.getName(user));
        this.usernameTextView.setText("");
        this.imageView.setVisibility(0);
        this.usernameTextView.setVisibility(0);
    }

    public void setText(String text) {
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        this.nameTextView.setText(text);
    }

    public void invalidate() {
        super.invalidate();
        this.nameTextView.invalidate();
    }

    public void setEmojiSuggestion(MediaDataController.KeywordResult suggestion) {
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        StringBuilder stringBuilder = new StringBuilder(suggestion.emoji.length() + suggestion.keyword.length() + 4);
        stringBuilder.append(suggestion.emoji);
        stringBuilder.append("   :");
        stringBuilder.append(suggestion.keyword);
        TextView textView = this.nameTextView;
        textView.setText(Emoji.replaceEmoji(stringBuilder, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
    }

    public void setBotCommand(String command, String help, TLRPC.User user) {
        if (user != null) {
            this.imageView.setVisibility(0);
            this.avatarDrawable.setInfo(user);
            if (user.photo == null || user.photo.photo_small == null) {
                this.imageView.setImageDrawable(this.avatarDrawable);
            } else {
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.avatarDrawable, (Object) user);
            }
        } else {
            this.imageView.setVisibility(4);
        }
        this.usernameTextView.setVisibility(0);
        this.nameTextView.setText(command);
        TextView textView = this.usernameTextView;
        textView.setText(Emoji.replaceEmoji(help, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        if (isDarkTheme) {
            this.nameTextView.setTextColor(-1);
            this.usernameTextView.setTextColor(-4473925);
            return;
        }
        this.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.usernameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
    }
}
