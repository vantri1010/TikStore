package im.bclpbkiauv.ui.hcells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.CheckBoxSquare;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class ContactUserCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
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
    private int statusColor;
    private int statusOnlineColor;
    private SimpleTextView statusTextView;
    private TLRPC.UserFull userFull;

    public ContactUserCell(Context context) {
        this(context, (AttributeSet) null);
    }

    public ContactUserCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactUserCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.currentAccount = UserConfig.selectedAccount;
        init(context, 6, 1, false, false);
    }

    public void init(Context context, int padding, int checkbox, boolean admin) {
        init(context, padding, checkbox, admin, false);
    }

    public void init(Context context, int padding, int checkbox, boolean admin, boolean needAddButton) {
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        this.statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
        this.statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(7.5f));
        addView(this.avatarImageView, LayoutHelper.createFrame(45.0f, 45.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 10.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(14);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i5 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28 + 0;
        } else {
            i = padding + 64;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 64;
        } else {
            if (i3 != 2) {
                i5 = 0;
            }
            i2 = i5 + 28 + 0;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1.0f, 20.0f, i4, f, 10.0f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(13);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 0 + 28 : padding + 64), 39.0f, (float) (LocaleController.isRTL ? padding + 64 : 0 + 28), 0.0f));
        if (i3 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18.0f, 18.0f, (LocaleController.isRTL ? 3 : 5) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i3 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, R.drawable.round_check2);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22.0f, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 43.5f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
        setFocusable(true);
    }

    public void setAvatarPadding(int padding) {
        int i;
        float f;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        float f2 = 0.0f;
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 7));
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? (float) (padding + 7) : 0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        int i2 = 18;
        if (LocaleController.isRTL) {
            i = (this.checkBoxBig != null ? 18 : 0) + 28;
        } else {
            i = padding + 64;
        }
        layoutParams2.leftMargin = AndroidUtilities.dp((float) i);
        if (LocaleController.isRTL) {
            f = (float) (padding + 64);
        } else {
            if (this.checkBoxBig == null) {
                i2 = 0;
            }
            f = (float) (i2 + 28);
        }
        layoutParams2.rightMargin = AndroidUtilities.dp(f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        float f3 = 28.0f;
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : (float) (padding + 64));
        if (LocaleController.isRTL) {
            f3 = (float) (padding + 64);
        }
        layoutParams3.rightMargin = AndroidUtilities.dp(f3);
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) checkBox2.getLayoutParams();
            layoutParams4.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : (float) (padding + 37));
            if (LocaleController.isRTL) {
                f2 = (float) (padding + 37);
            }
            layoutParams4.rightMargin = AndroidUtilities.dp(f2);
        }
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
        CheckBox checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(checked, animated);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(checked, animated);
        }
    }

    public void setCheckDisabled(boolean disabled) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(disabled);
        }
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
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
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
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            im.bclpbkiauv.tgnet.TLRPC$UserFull r4 = r10.userFull
            if (r4 == 0) goto L_0x0101
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            java.lang.String r4 = r4.about
            r6.setText(r4)
        L_0x0101:
            r10.lastAvatar = r0
            java.lang.String r4 = "50_50"
            if (r2 == 0) goto L_0x0113
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r5 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r2, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r5, (java.lang.String) r4, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r2)
            goto L_0x0128
        L_0x0113:
            if (r3 == 0) goto L_0x0121
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r5 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r3, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r5, (java.lang.String) r4, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r3)
            goto L_0x0128
        L_0x0121:
            im.bclpbkiauv.ui.components.BackupImageView r4 = r10.avatarImageView
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r10.avatarDrawable
            r4.setImageDrawable(r5)
        L_0x0128:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hcells.ContactUserCell.update(int):void");
    }

    public void setUserFull(TLRPC.UserFull userFull2) {
        this.userFull = userFull2;
        if (userFull2 != null) {
            this.statusTextView.setText(userFull2.about);
        }
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
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare == null || checkBoxSquare.getVisibility() != 0) {
            CheckBox checkBox2 = this.checkBox;
            if (checkBox2 != null && checkBox2.getVisibility() == 0) {
                info.setCheckable(true);
                info.setChecked(this.checkBox.isChecked());
                info.setClassName("android.widget.CheckBox");
                return;
            }
            return;
        }
        info.setCheckable(true);
        info.setChecked(this.checkBoxBig.isChecked());
        info.setClassName("android.widget.CheckBox");
    }
}
