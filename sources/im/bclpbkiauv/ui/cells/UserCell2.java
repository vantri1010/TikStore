package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.SimpleTextView;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.CheckBox;
import im.bclpbkiauv.ui.components.CheckBoxSquare;
import im.bclpbkiauv.ui.components.LayoutHelper;

public class UserCell2 extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private CheckBox checkBox;
    private CheckBoxSquare checkBoxBig;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private TLObject currentObject;
    private CharSequence currrntStatus;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private SimpleTextView nameTextView;
    private int statusColor = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText);
    private int statusOnlineColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
    private SimpleTextView statusTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public UserCell2(Context context, int padding, int checkbox) {
        super(context);
        int i;
        int i2;
        Context context2 = context;
        int i3 = checkbox;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i4 = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48.0f, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 7), 11.0f, LocaleController.isRTL ? (float) (padding + 7) : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i6 = 18;
        if (LocaleController.isRTL) {
            i = (i3 == 2 ? 18 : 0) + 28;
        } else {
            i = padding + 68;
        }
        float f = (float) i;
        if (LocaleController.isRTL) {
            i2 = padding + 68;
        } else {
            i2 = (i3 != 2 ? 0 : i6) + 28;
        }
        addView(simpleTextView2, LayoutHelper.createFrame(-1.0f, 20.0f, i5, f, 14.5f, (float) i2, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1.0f, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : (float) (padding + 68), 37.5f, LocaleController.isRTL ? (float) (padding + 68) : 28.0f, 0.0f));
        ImageView imageView2 = new ImageView(context2);
        this.imageView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayIcon), PorterDuff.Mode.MULTIPLY));
        this.imageView.setVisibility(8);
        addView(this.imageView, LayoutHelper.createFrame(-2.0f, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0.0f : 16.0f, 0.0f, LocaleController.isRTL ? 16.0f : 0.0f, 0.0f));
        if (i3 == 2) {
            CheckBoxSquare checkBoxSquare = new CheckBoxSquare(context2, false);
            this.checkBoxBig = checkBoxSquare;
            addView(checkBoxSquare, LayoutHelper.createFrame(18.0f, 18.0f, (LocaleController.isRTL ? 3 : i4) | 16, LocaleController.isRTL ? 19.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 19.0f, 0.0f));
        } else if (i3 == 1) {
            CheckBox checkBox2 = new CheckBox(context2, R.drawable.round_check2);
            this.checkBox = checkBox2;
            checkBox2.setVisibility(4);
            this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
            addView(this.checkBox, LayoutHelper.createFrame(22.0f, 22.0f, (!LocaleController.isRTL ? 3 : i4) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 37), 41.0f, LocaleController.isRTL ? (float) (padding + 37) : 0.0f, 0.0f));
        }
    }

    public void setData(TLObject object, CharSequence name, CharSequence status, int resId) {
        if (object == null && name == null && status == null) {
            this.currrntStatus = null;
            this.currentName = null;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable((Drawable) null);
            return;
        }
        this.currrntStatus = status;
        this.currentName = name;
        this.currentObject = object;
        this.currentDrawable = resId;
        update(0);
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
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(70.0f), 1073741824));
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
            r10.lastAvatar = r0
            r4 = 0
            r5 = 0
            if (r2 == 0) goto L_0x00a1
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((im.bclpbkiauv.tgnet.TLRPC.User) r2)
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x009e
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            int r6 = r6.expires
            r10.lastStatus = r6
            goto L_0x00c2
        L_0x009e:
            r10.lastStatus = r5
            goto L_0x00c2
        L_0x00a1:
            if (r3 == 0) goto L_0x00a9
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            r6.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r3)
            goto L_0x00c2
        L_0x00a9:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00b9
            im.bclpbkiauv.ui.components.AvatarDrawable r7 = r10.avatarDrawable
            int r8 = r10.currentId
            java.lang.String r6 = r6.toString()
            r7.setInfo(r8, r6, r4)
            goto L_0x00c2
        L_0x00b9:
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            int r7 = r10.currentId
            java.lang.String r8 = "#"
            r6.setInfo(r7, r8, r4)
        L_0x00c2:
            java.lang.CharSequence r6 = r10.currentName
            if (r6 == 0) goto L_0x00ce
            r10.lastName = r4
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.nameTextView
            r4.setText(r6)
            goto L_0x00ea
        L_0x00ce:
            if (r2 == 0) goto L_0x00db
            if (r1 != 0) goto L_0x00d7
            java.lang.String r4 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            goto L_0x00d8
        L_0x00d7:
            r4 = r1
        L_0x00d8:
            r10.lastName = r4
            goto L_0x00e3
        L_0x00db:
            if (r1 != 0) goto L_0x00e0
            java.lang.String r4 = r3.title
            goto L_0x00e1
        L_0x00e0:
            r4 = r1
        L_0x00e1:
            r10.lastName = r4
        L_0x00e3:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.nameTextView
            java.lang.String r6 = r10.lastName
            r4.setText(r6)
        L_0x00ea:
            java.lang.CharSequence r4 = r10.currrntStatus
            if (r4 == 0) goto L_0x00fe
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            int r6 = r10.statusColor
            r4.setTextColor(r6)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r4 = r10.statusTextView
            java.lang.CharSequence r6 = r10.currrntStatus
            r4.setText(r6)
            goto L_0x0246
        L_0x00fe:
            java.lang.String r4 = "50_50"
            if (r2 == 0) goto L_0x019b
            boolean r6 = r2.bot
            if (r6 == 0) goto L_0x012f
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r10.statusColor
            r6.setTextColor(r7)
            boolean r6 = r2.bot_chat_history
            if (r6 == 0) goto L_0x0120
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131690191(0x7f0f02cf, float:1.9009419E38)
            java.lang.String r8 = "BotStatusRead"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x018e
        L_0x0120:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131690190(0x7f0f02ce, float:1.9009417E38)
            java.lang.String r8 = "BotStatusCantRead"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x018e
        L_0x012f:
            int r6 = r2.id
            int r7 = r10.currentAccount
            im.bclpbkiauv.messenger.UserConfig r7 = im.bclpbkiauv.messenger.UserConfig.getInstance(r7)
            int r7 = r7.getClientUserId()
            if (r6 == r7) goto L_0x0179
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            if (r6 == 0) goto L_0x0151
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r6 = r2.status
            int r6 = r6.expires
            int r7 = r10.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r7 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r7)
            int r7 = r7.getCurrentTime()
            if (r6 > r7) goto L_0x0179
        L_0x0151:
            int r6 = r10.currentAccount
            im.bclpbkiauv.messenger.MessagesController r6 = im.bclpbkiauv.messenger.MessagesController.getInstance(r6)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r6 = r6.onlinePrivacy
            int r7 = r2.id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            boolean r6 = r6.containsKey(r7)
            if (r6 == 0) goto L_0x0166
            goto L_0x0179
        L_0x0166:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r10.statusColor
            r6.setTextColor(r7)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r10.currentAccount
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatUserStatus(r7, r2)
            r6.setText(r7)
            goto L_0x018e
        L_0x0179:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r10.statusOnlineColor
            r6.setTextColor(r7)
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131692475(0x7f0f0bbb, float:1.9014051E38)
            java.lang.String r8 = "Online"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
        L_0x018e:
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r2, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r8 = r10.avatarDrawable
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r7, (java.lang.String) r4, (android.graphics.drawable.Drawable) r8, (java.lang.Object) r2)
            goto L_0x0246
        L_0x019b:
            if (r3 == 0) goto L_0x023f
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r10.statusColor
            r6.setTextColor(r7)
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r6 == 0) goto L_0x01e7
            boolean r6 = r3.megagroup
            if (r6 != 0) goto L_0x01e7
            int r6 = r3.participants_count
            if (r6 == 0) goto L_0x01c1
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r3.participants_count
            java.lang.String r8 = "Subscribers"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x01c1:
            java.lang.String r6 = r3.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x01d8
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131690467(0x7f0f03e3, float:1.9009978E38)
            java.lang.String r8 = "ChannelPrivate"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x01d8:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131690470(0x7f0f03e6, float:1.9009985E38)
            java.lang.String r8 = "ChannelPublic"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x01e7:
            int r6 = r3.participants_count
            if (r6 == 0) goto L_0x01f9
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            int r7 = r3.participants_count
            java.lang.String r8 = "Members"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x01f9:
            boolean r6 = r3.has_geo
            if (r6 == 0) goto L_0x020c
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131691954(0x7f0f09b2, float:1.9012994E38)
            java.lang.String r8 = "MegaLocation"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x020c:
            java.lang.String r6 = r3.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 == 0) goto L_0x0223
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131691955(0x7f0f09b3, float:1.9012996E38)
            java.lang.String r8 = "MegaPrivate"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
            goto L_0x0231
        L_0x0223:
            im.bclpbkiauv.ui.actionbar.SimpleTextView r6 = r10.statusTextView
            r7 = 2131691958(0x7f0f09b6, float:1.9013003E38)
            java.lang.String r8 = "MegaPublic"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setText(r7)
        L_0x0231:
            im.bclpbkiauv.ui.components.BackupImageView r6 = r10.avatarImageView
            im.bclpbkiauv.messenger.ImageLocation r7 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r3, r5)
            im.bclpbkiauv.ui.components.AvatarDrawable r8 = r10.avatarDrawable
            im.bclpbkiauv.tgnet.TLObject r9 = r10.currentObject
            r6.setImage((im.bclpbkiauv.messenger.ImageLocation) r7, (java.lang.String) r4, (android.graphics.drawable.Drawable) r8, (java.lang.Object) r9)
            goto L_0x0246
        L_0x023f:
            im.bclpbkiauv.ui.components.BackupImageView r4 = r10.avatarImageView
            im.bclpbkiauv.ui.components.AvatarDrawable r6 = r10.avatarDrawable
            r4.setImageDrawable(r6)
        L_0x0246:
            android.widget.ImageView r4 = r10.imageView
            int r4 = r4.getVisibility()
            r6 = 8
            if (r4 != 0) goto L_0x0254
            int r4 = r10.currentDrawable
            if (r4 == 0) goto L_0x0260
        L_0x0254:
            android.widget.ImageView r4 = r10.imageView
            int r4 = r4.getVisibility()
            if (r4 != r6) goto L_0x0272
            int r4 = r10.currentDrawable
            if (r4 == 0) goto L_0x0272
        L_0x0260:
            android.widget.ImageView r4 = r10.imageView
            int r7 = r10.currentDrawable
            if (r7 != 0) goto L_0x0268
            r5 = 8
        L_0x0268:
            r4.setVisibility(r5)
            android.widget.ImageView r4 = r10.imageView
            int r5 = r10.currentDrawable
            r4.setImageResource(r5)
        L_0x0272:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.UserCell2.update(int):void");
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
