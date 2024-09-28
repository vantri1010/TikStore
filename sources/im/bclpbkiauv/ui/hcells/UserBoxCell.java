package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NotificationsSettingsActivity;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class UserBoxCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int currentAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currentStatus;
    private TLRPC.EncryptedChat encryptedChat;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private FrameLayout shadow;
    private boolean shadowIsVisible;
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;

    public UserBoxCell(Context context, int padding, int checkbox, boolean admin) {
        this(context, padding, checkbox, admin, false);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserBoxCell(Context context, int padding, int checkbox, boolean admin, boolean needAddButton) {
        super(context);
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        this.currentAccount = UserConfig.selectedAccount;
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        int i4 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(45.0f, 45.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 10.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 10.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(14);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i6 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28 + 0;
        } else {
            i = padding + 64;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 64;
        } else {
            i2 = (i3 != 2 ? 0 : i6) + 28 + 0;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1.0f, 20.0f, i5, f, 13.5f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(13);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (!LocaleController.isRTL ? 3 : i4) | 48, (float) (LocaleController.isRTL ? 0 + 28 : padding + 64), 35.5f, (float) (LocaleController.isRTL ? padding + 64 : 0 + 28), 0.0f));
        if (i3 == 1) {
            CheckBox2 checkBox2 = new CheckBox2(context2);
            this.checkBox = checkBox2;
            addView(checkBox2, LayoutHelper.createFrame(24.0f, 24.0f, 51, 16.0f, 18.0f, 0.0f, 0.0f));
        }
        FrameLayout frameLayout = new FrameLayout(context2);
        this.shadow = frameLayout;
        frameLayout.setBackgroundColor(-2130706433);
        this.shadow.setVisibility(8);
        this.shadow.setClickable(true);
        addView(this.shadow, LayoutHelper.createFrame(-1, -1.0f));
        setFocusable(true);
    }

    public void setAvatarPadding(int padding) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp((float) (padding + 7));
        layoutParams.rightMargin = AndroidUtilities.dp(0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams2.leftMargin = AndroidUtilities.dp((float) (padding + 64));
        layoutParams2.rightMargin = AndroidUtilities.dp(28.0f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        layoutParams3.leftMargin = AndroidUtilities.dp((float) (padding + 64));
        layoutParams3.rightMargin = AndroidUtilities.dp(28.0f);
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        setData(object, (TLRPC.EncryptedChat) null, name, status, resId, false);
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId, boolean divider) {
        setData(object, (TLRPC.EncryptedChat) null, name, status, resId, divider);
    }

    public void setData(TLObject object, TLRPC.EncryptedChat ec, CharSequence name, CharSequence status, int resId, boolean divider) {
        if (object == null && name == null && status == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.encryptedChat = ec;
        this.currentStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        this.needDivider = divider;
        setWillNotDraw(!divider);
        update(0);
    }

    public void setException(NotificationsSettingsActivity.NotificationException exception, CharSequence name, boolean divider) {
        String text;
        String text2;
        TLRPC.User user;
        boolean enabled;
        NotificationsSettingsActivity.NotificationException notificationException = exception;
        boolean custom = notificationException.hasCustom;
        int value = notificationException.notify;
        int delta = notificationException.muteUntil;
        if (value != 3 || delta == Integer.MAX_VALUE) {
            if (value == 0) {
                enabled = true;
            } else if (value == 1) {
                enabled = true;
            } else if (value == 2) {
                enabled = false;
            } else {
                enabled = false;
            }
            if (!enabled || !custom) {
                text = enabled ? LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted) : LocaleController.getString("NotificationsMuted", R.string.NotificationsMuted);
                int i = delta;
            } else {
                text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                int i2 = delta;
            }
        } else {
            int delta2 = delta - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (delta2 <= 0) {
                if (custom) {
                    text = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                    int i3 = delta2;
                } else {
                    text = LocaleController.getString("NotificationsUnmuted", R.string.NotificationsUnmuted);
                    int i4 = delta2;
                }
            } else if (delta2 < 3600) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta2 / 60));
                int i5 = delta2;
            } else if (delta2 < 86400) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta2) / 60.0f) / 60.0f))));
                int i6 = delta2;
            } else if (delta2 < 31536000) {
                text = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta2) / 60.0f) / 60.0f) / 24.0f))));
                int i7 = delta2;
            } else {
                text = null;
                int i8 = delta2;
            }
        }
        if (text == null) {
            text2 = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
        } else {
            text2 = text;
        }
        int lower_id = (int) notificationException.did;
        int high_id = (int) (notificationException.did >> 32);
        if (lower_id == 0) {
            TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
            if (encryptedChat2 != null && (user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id))) != null) {
                setData(user, encryptedChat2, name, text2, 0, false);
            }
        } else if (lower_id > 0) {
            TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
            if (user2 != null) {
                setData(user2, (TLRPC.EncryptedChat) null, name, text2, 0, divider);
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (chat != null) {
                setData(chat, (TLRPC.EncryptedChat) null, name, text2, 0, divider);
            }
        }
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setCurrentId(int id) {
        this.currentId = id;
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
        }
    }

    public void setShadow(boolean isVisible) {
        this.shadowIsVisible = isVisible;
        this.shadow.setVisibility(isVisible ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(65.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void setStatusColors(int color, int onlineColor) {
        this.statusColor = color;
        this.statusOnlineColor = onlineColor;
    }

    public void invalidate() {
        super.invalidate();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [im.bclpbkiauv.tgnet.TLObject] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void update(int r11) {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            im.bclpbkiauv.tgnet.TLObject r4 = r10.currentObject
            boolean r5 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r5 == 0) goto L_0x0016
            r2 = r4
            im.bclpbkiauv.tgnet.TLRPC$User r2 = (im.bclpbkiauv.tgnet.TLRPC.User) r2
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r4 = r2.photo
            if (r4 == 0) goto L_0x0025
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r4 = r2.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r0 = r4.photo_small
            goto L_0x0025
        L_0x0016:
            boolean r5 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.Chat
            if (r5 == 0) goto L_0x0025
            r3 = r4
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = (im.bclpbkiauv.tgnet.TLRPC.Chat) r3
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r4 = r3.photo
            if (r4 == 0) goto L_0x0025
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r4 = r3.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r0 = r4.photo_small
        L_0x0025:
            if (r11 == 0) goto L_0x0088
            r4 = 0
            r5 = r11 & 2
            if (r5 == 0) goto L_0x004f
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            if (r5 == 0) goto L_0x0032
            if (r0 == 0) goto L_0x004e
        L_0x0032:
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            if (r5 != 0) goto L_0x0038
            if (r0 != 0) goto L_0x004e
        L_0x0038:
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            if (r5 == 0) goto L_0x004f
            if (r0 == 0) goto L_0x004f
            long r5 = r5.volume_id
            long r7 = r0.volume_id
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x004e
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r10.lastAvatar
            int r5 = r5.local_id
            int r6 = r0.local_id
            if (r5 == r6) goto L_0x004f
        L_0x004e:
            r4 = 1
        L_0x004f:
            if (r2 == 0) goto L_0x0065
            if (r4 != 0) goto L_0x0065
            r5 = r11 & 4
            if (r5 == 0) goto L_0x0065
            r5 = 0
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x0060
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            int r5 = r6.expires
        L_0x0060:
            int r6 = r10.lastStatus
            if (r5 == r6) goto L_0x0065
            r4 = 1
        L_0x0065:
            if (r4 != 0) goto L_0x0085
            java.lang.CharSequence r5 = r10.currentName
            if (r5 != 0) goto L_0x0085
            java.lang.String r5 = r10.lastName
            if (r5 == 0) goto L_0x0085
            r5 = r11 & 1
            if (r5 == 0) goto L_0x0085
            if (r2 == 0) goto L_0x007a
            java.lang.String r1 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            goto L_0x007c
        L_0x007a:
            java.lang.String r1 = r3.title
        L_0x007c:
            java.lang.String r5 = r10.lastName
            boolean r5 = r1.equals(r5)
            if (r5 != 0) goto L_0x0085
            r4 = 1
        L_0x0085:
            if (r4 != 0) goto L_0x0088
            return
        L_0x0088:
            r4 = 0
            r5 = 0
            if (r2 == 0) goto L_0x009f
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((im.bclpbkiauv.tgnet.TLRPC.User) r2)
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x009c
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            int r6 = r6.expires
            r10.lastStatus = r6
            goto L_0x00c0
        L_0x009c:
            r10.lastStatus = r5
            goto L_0x00c0
        L_0x009f:
            if (r3 == 0) goto L_0x00a7
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r3)
            goto L_0x00c0
        L_0x00a7:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00b7
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            int r8 = r10.currentId
            java.lang.String r6 = r6.toString()
            r7.setInfo(r8, r6, r4)
            goto L_0x00c0
        L_0x00b7:
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            int r7 = r10.currentId
            java.lang.String r8 = "#"
            r6.setInfo(r7, r8, r4)
        L_0x00c0:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00cc
            r10.lastName = r4
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.nameTextView
            r4.setText(r6)
            goto L_0x00ef
        L_0x00cc:
            if (r2 == 0) goto L_0x00d9
            if (r1 != 0) goto L_0x00d5
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            goto L_0x00d6
        L_0x00d5:
            r4 = r1
        L_0x00d6:
            r10.lastName = r4
            goto L_0x00e8
        L_0x00d9:
            if (r3 == 0) goto L_0x00e4
            if (r1 != 0) goto L_0x00e0
            java.lang.String r4 = r3.title
            goto L_0x00e1
        L_0x00e0:
            r4 = r1
        L_0x00e1:
            r10.lastName = r4
            goto L_0x00e8
        L_0x00e4:
            java.lang.String r4 = ""
            r10.lastName = r4
        L_0x00e8:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.nameTextView
            java.lang.String r6 = r10.lastName
            r4.setText(r6)
        L_0x00ef:
            java.lang.CharSequence r4 = r10.currentStatus
            if (r4 == 0) goto L_0x0103
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            java.lang.CharSequence r6 = r10.currentStatus
            r4.setText(r6)
            goto L_0x017e
        L_0x0103:
            if (r2 == 0) goto L_0x017e
            boolean r4 = r2.bot
            if (r4 == 0) goto L_0x011f
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131690190(0x7f0f02ce, float:1.9009417E38)
            java.lang.String r7 = "BotStatusCantRead"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
            goto L_0x017e
        L_0x011f:
            int r4 = r2.id
            int r6 = r10.currentAccount
            im.bclpbkiauv.messenger.UserConfig r6 = im.bclpbkiauv.messenger.UserConfig.getInstance(r6)
            int r6 = r6.getClientUserId()
            if (r4 == r6) goto L_0x0169
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r4 = r2.status
            if (r4 == 0) goto L_0x0141
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r4 = r2.status
            int r4 = r4.expires
            int r6 = r10.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r6 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r6)
            int r6 = r6.getCurrentTime()
            if (r4 > r6) goto L_0x0169
        L_0x0141:
            int r4 = r10.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r4 = r4.onlinePrivacy
            int r6 = r2.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            boolean r4 = r4.containsKey(r6)
            if (r4 == 0) goto L_0x0156
            goto L_0x0169
        L_0x0156:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.currentAccount
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.formatUserStatus(r6, r2)
            r4.setText(r6)
            goto L_0x017e
        L_0x0169:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusOnlineColor
            r4.setTextColor(r6)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            r6 = 2131692475(0x7f0f0bbb, float:1.9014051E38)
            java.lang.String r7 = "Online"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r4.setText(r6)
        L_0x017e:
            r10.lastAvatar = r0
            java.lang.String r4 = "50_50"
            if (r2 == 0) goto L_0x0190
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r5 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r2, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r5, (java.lang.String) r4, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r2)
            goto L_0x01a5
        L_0x0190:
            if (r3 == 0) goto L_0x019e
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r5 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r3, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r5, (java.lang.String) r4, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r3)
            goto L_0x01a5
        L_0x019e:
            im.bclpbkiauv.ui.components.BackupImageView r4 = r10.avatarImageView
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r10.avatarDrawable
            r4.setImageDrawable(r5)
        L_0x01a5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hcells.UserBoxCell.update(int):void");
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(68.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null && checkBox2.getVisibility() == 0) {
            info.setCheckable(true);
            info.setChecked(this.checkBox.isChecked());
            info.setClassName("android.widget.CheckBox");
        }
    }
}
