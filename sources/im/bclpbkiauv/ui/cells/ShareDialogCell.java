package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.CheckBoxBase;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private RectF checkBgRectF;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float onlineProgress;
    private TLRPC.User user;

    public ShareDialogCell(Context context) {
        super(context);
        setWillNotDraw(false);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        addView(this.imageView, LayoutHelper.createFrame(56.0f, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_dialogRoundCheckBox, Theme.key_dialogBackground, Theme.key_dialogRoundCheckBoxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(4);
        this.checkBox.setProgressDelegate(new CheckBoxBase.ProgressDelegate() {
            public final void setProgress(float f) {
                ShareDialogCell.this.lambda$new$0$ShareDialogCell(f);
            }
        });
        addView(this.checkBox, LayoutHelper.createFrame(24.0f, 24.0f, 49, 19.0f, 42.0f, 0.0f, 0.0f));
        this.checkBgRectF = new RectF();
    }

    public /* synthetic */ void lambda$new$0$ShareDialogCell(float progress) {
        float scale = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(scale);
        this.imageView.setScaleY(scale);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(103.0f), 1073741824));
    }

    public void setDialog(int uid, boolean checked, CharSequence name) {
        if (uid > 0) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
            this.user = user2;
            this.avatarDrawable.setInfo(user2);
            if (UserObject.isUserSelf(this.user)) {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                this.avatarDrawable.setAvatarType(1);
                this.imageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.user);
            } else {
                if (name != null) {
                    this.nameTextView.setText(name);
                } else {
                    TLRPC.User user3 = this.user;
                    if (user3 != null) {
                        this.nameTextView.setText(ContactsController.formatName(user3.first_name, this.user.last_name));
                    } else {
                        this.nameTextView.setText("");
                    }
                }
                this.imageView.setImage(ImageLocation.getForUser(this.user, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.user);
            }
        } else {
            this.user = null;
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.imageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
        this.checkBox.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        TLRPC.User user2;
        Canvas canvas2 = canvas;
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (child == this.imageView && (user2 = this.user) != null && !MessagesController.isSupportUser(user2)) {
            long newTime = SystemClock.uptimeMillis();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            boolean isOnline = !this.user.self && !this.user.bot && ((this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.user.id)));
            if (isOnline || this.onlineProgress != 0.0f) {
                int top = this.imageView.getBottom() - AndroidUtilities.dp(6.0f);
                int left = this.imageView.getRight() - AndroidUtilities.dp(7.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas2.drawCircle((float) left, (float) top, ((float) AndroidUtilities.dp(7.0f)) * this.onlineProgress, Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_chats_onlineCircle));
                canvas2.drawCircle((float) left, (float) top, ((float) AndroidUtilities.dp(5.0f)) * this.onlineProgress, Theme.dialogs_onlineCirclePaint);
                if (isOnline) {
                    float f = this.onlineProgress;
                    if (f < 1.0f) {
                        float f2 = f + (((float) dt) / 150.0f);
                        this.onlineProgress = f2;
                        if (f2 > 1.0f) {
                            this.onlineProgress = 1.0f;
                        }
                        this.imageView.invalidate();
                        invalidate();
                    }
                } else {
                    float f3 = this.onlineProgress;
                    if (f3 > 0.0f) {
                        float f4 = f3 - (((float) dt) / 150.0f);
                        this.onlineProgress = f4;
                        if (f4 < 0.0f) {
                            this.onlineProgress = 0.0f;
                        }
                        this.imageView.invalidate();
                        invalidate();
                    }
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Theme.checkboxSquare_checkPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        this.checkBgRectF.set((float) this.imageView.getLeft(), (float) this.imageView.getTop(), (float) this.imageView.getRight(), (float) this.imageView.getBottom());
        float radius = (float) AndroidUtilities.dp(7.5f);
        canvas.drawRoundRect(this.checkBgRectF, radius, radius, Theme.checkboxSquare_checkPaint);
    }
}
