package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private StaticLayout countLayout;
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC.User currentUser;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;
    private RectF rect = new RectF();

    public HintDialogCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        addView(this.imageView, LayoutHelper.createFrame(54.0f, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), 1073741824));
    }

    public void update(int mask) {
        if (!((mask & 4) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
        if (mask == 0 || (mask & 256) != 0 || (mask & 2048) != 0) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (dialog == null || dialog.unread_count == 0) {
                if (this.countLayout != null) {
                    if (mask != 0) {
                        invalidate();
                    }
                    this.lastUnreadCount = 0;
                    this.countLayout = null;
                }
            } else if (this.lastUnreadCount != dialog.unread_count) {
                this.lastUnreadCount = dialog.unread_count;
                String countString = String.format("%d", new Object[]{Integer.valueOf(dialog.unread_count)});
                this.countWidth = Math.max(AndroidUtilities.dp(12.0f), (int) Math.ceil((double) Theme.dialogs_countTextPaint.measureText(countString)));
                this.countLayout = new StaticLayout(countString, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                if (mask != 0) {
                    invalidate();
                }
            }
        }
    }

    public void update() {
        int uid = (int) this.dialog_id;
        if (uid > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            this.currentUser = user;
            this.avatarDrawable.setInfo(user);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid)));
        this.currentUser = null;
    }

    public void setDialog(int uid, boolean counter, CharSequence name) {
        this.dialog_id = (long) uid;
        if (uid > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            this.currentUser = user;
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (user != null) {
                this.nameTextView.setText(UserObject.getFirstName(user));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.imageView.setImage(ImageLocation.getForUser(this.currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.currentUser = null;
            this.imageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
        if (counter) {
            update(0);
        } else {
            this.countLayout = null;
        }
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView) {
            if (this.countLayout != null) {
                int top = AndroidUtilities.dp(6.0f);
                int left = AndroidUtilities.dp(54.0f);
                int x = left - AndroidUtilities.dp(5.5f);
                this.rect.set((float) x, (float) top, (float) (this.countWidth + x + AndroidUtilities.dp(11.0f)), (float) (AndroidUtilities.dp(23.0f) + top));
                canvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) left, (float) (AndroidUtilities.dp(4.0f) + top));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            TLRPC.User user = this.currentUser;
            if (user != null && !user.bot && ((this.currentUser.status != null && this.currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id)))) {
                int top2 = AndroidUtilities.dp(53.0f);
                int left2 = AndroidUtilities.dp(59.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas.drawCircle((float) left2, (float) top2, (float) AndroidUtilities.dp(7.0f), Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_chats_onlineCircle));
                canvas.drawCircle((float) left2, (float) top2, (float) AndroidUtilities.dp(5.0f), Theme.dialogs_onlineCirclePaint);
            }
        }
        return result;
    }
}
