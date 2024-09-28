package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public GroupCreateUserCell(Context context, boolean needCheck, int padding) {
        super(context);
        Context context2 = context;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(45.0f, 45.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 13), 10.0f, LocaleController.isRTL ? (float) (padding + 13) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(14);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i2 = 28;
        addView(this.nameTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + padding), 10.0f, (float) ((LocaleController.isRTL ? 72 : 28) + padding), 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(13);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) ((LocaleController.isRTL ? 28 : 72) + padding), 39.0f, (float) ((LocaleController.isRTL ? 72 : i2) + padding), 0.0f));
        if (needCheck) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24.0f, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 0.0f : 40.0f, 36.5f, LocaleController.isRTL ? 39.0f : 0.0f, 0.0f));
        }
    }

    public void setObject(TLObject object, CharSequence name, CharSequence status) {
        this.currentObject = object;
        this.currentStatus = status;
        this.currentName = name;
        update(0);
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }

    public void setCheckBoxEnabled(boolean enabled) {
        this.checkBox.setEnabled(enabled);
    }

    public TLObject getObject() {
        return this.currentObject;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(65.0f), 1073741824));
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void update(int mask) {
        TLRPC.FileLocation fileLocation;
        TLRPC.FileLocation fileLocation2;
        TLObject tLObject = this.currentObject;
        if (tLObject != null) {
            TLRPC.FileLocation photo = null;
            String newName = null;
            if (tLObject instanceof TLRPC.User) {
                TLRPC.User currentUser = (TLRPC.User) tLObject;
                if (currentUser.photo != null) {
                    photo = currentUser.photo.photo_small;
                }
                if (mask != 0) {
                    boolean continueUpdate = false;
                    if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || ((this.lastAvatar == null && photo != null) || !((fileLocation2 = this.lastAvatar) == null || photo == null || (fileLocation2.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                        continueUpdate = true;
                    }
                    if (currentUser != null && this.currentStatus == null && !continueUpdate && (mask & 4) != 0) {
                        int newStatus = 0;
                        if (currentUser.status != null) {
                            newStatus = currentUser.status.expires;
                        }
                        if (newStatus != this.lastStatus) {
                            continueUpdate = true;
                        }
                    }
                    if (!continueUpdate && this.currentName == null && this.lastName != null && (mask & 1) != 0) {
                        newName = UserObject.getName(currentUser);
                        if (!newName.equals(this.lastName)) {
                            continueUpdate = true;
                        }
                    }
                    if (!continueUpdate) {
                        return;
                    }
                }
                this.avatarDrawable.setInfo(currentUser);
                this.lastStatus = currentUser.status != null ? currentUser.status.expires : 0;
                CharSequence charSequence = this.currentName;
                if (charSequence != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence, true);
                } else {
                    String name = newName == null ? UserObject.getName(currentUser) : newName;
                    this.lastName = name;
                    this.nameTextView.setText(name);
                }
                if (this.currentStatus == null) {
                    if (currentUser.bot) {
                        this.statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
                        this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                        this.statusTextView.setText(LocaleController.getString("Bot", R.string.Bot));
                    } else if (currentUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ((currentUser.status != null && currentUser.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(currentUser.id)))) {
                        this.statusTextView.setTag(Theme.key_windowBackgroundWhiteBlueText);
                        this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        this.statusTextView.setText(LocaleController.getString("Online", R.string.Online));
                    } else {
                        this.statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
                        this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                        this.statusTextView.setText(LocaleController.formatUserStatus(this.currentAccount, currentUser));
                    }
                }
                this.avatarImageView.setImage(ImageLocation.getForUser(currentUser, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentUser);
            } else {
                TLRPC.Chat currentChat = (TLRPC.Chat) tLObject;
                if (currentChat.photo != null) {
                    photo = currentChat.photo.photo_small;
                }
                if (mask != 0) {
                    boolean continueUpdate2 = false;
                    if ((mask & 2) != 0 && ((this.lastAvatar != null && photo == null) || ((this.lastAvatar == null && photo != null) || !((fileLocation = this.lastAvatar) == null || photo == null || (fileLocation.volume_id == photo.volume_id && this.lastAvatar.local_id == photo.local_id))))) {
                        continueUpdate2 = true;
                    }
                    if (!continueUpdate2 && this.currentName == null && this.lastName != null && (mask & 1) != 0) {
                        newName = currentChat.title;
                        if (!newName.equals(this.lastName)) {
                            continueUpdate2 = true;
                        }
                    }
                    if (!continueUpdate2) {
                        return;
                    }
                }
                this.avatarDrawable.setInfo(currentChat);
                CharSequence charSequence2 = this.currentName;
                if (charSequence2 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence2, true);
                } else {
                    String str = newName == null ? currentChat.title : newName;
                    this.lastName = str;
                    this.nameTextView.setText(str);
                }
                if (this.currentStatus == null) {
                    this.statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
                    this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                    if (currentChat.participants_count != 0) {
                        this.statusTextView.setText(LocaleController.formatPluralString("Members", currentChat.participants_count));
                    } else if (currentChat.has_geo) {
                        this.statusTextView.setText(LocaleController.getString("MegaLocation", R.string.MegaLocation));
                    } else if (TextUtils.isEmpty(currentChat.username)) {
                        this.statusTextView.setText(LocaleController.getString("MegaPrivate", R.string.MegaPrivate));
                    } else {
                        this.statusTextView.setText(LocaleController.getString("MegaPublic", R.string.MegaPublic));
                    }
                }
                this.avatarImageView.setImage(ImageLocation.getForChat(currentChat, false), "50_50", (Drawable) this.avatarDrawable, (Object) currentChat);
            }
            CharSequence charSequence3 = this.currentStatus;
            if (charSequence3 != null) {
                this.statusTextView.setText(charSequence3, true);
                this.statusTextView.setTag(Theme.key_windowBackgroundWhiteGrayText);
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
