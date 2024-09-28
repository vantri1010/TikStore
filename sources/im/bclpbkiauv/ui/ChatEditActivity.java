package im.bclpbkiauv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ChatObject;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SendMessagesHelper;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.NewLocationActivity;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.ThemeDescription;
import im.bclpbkiauv.ui.cells.HeaderCell;
import im.bclpbkiauv.ui.cells.RadioButtonCell;
import im.bclpbkiauv.ui.cells.TextCell;
import im.bclpbkiauv.ui.cells.TextCheckCell;
import im.bclpbkiauv.ui.cells.TextDetailCell;
import im.bclpbkiauv.ui.cells.TextInfoPrivacyCell;
import im.bclpbkiauv.ui.cells.TextSettingsCell;
import im.bclpbkiauv.ui.components.AlertsCreator;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.EditTextBoldCursor;
import im.bclpbkiauv.ui.components.EditTextEmoji;
import im.bclpbkiauv.ui.components.ImageUpdater;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextCell adminCell;
    private TLRPC.FileLocation avatar;
    /* access modifiers changed from: private */
    public AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private LinearLayout avatarContainer;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    /* access modifiers changed from: private */
    public ImageView avatarEditor;
    /* access modifiers changed from: private */
    public BackupImageView avatarImage;
    /* access modifiers changed from: private */
    public View avatarOverlay;
    /* access modifiers changed from: private */
    public RadialProgressView avatarProgressView;
    private TextCell blockCell;
    private int chatId;
    private boolean createAfterUpload;
    private TLRPC.Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextDetailCell historyCell;
    private boolean historyHidden;
    private ImageUpdater imageUpdater = new ImageUpdater();
    private TLRPC.ChatFull info;
    private LinearLayout infoContainer;
    private boolean isChannel;
    private TextDetailCell linkedCell;
    private TextDetailCell locationCell;
    private TextCell logCell;
    private TextCell membersCell;
    /* access modifiers changed from: private */
    public EditTextEmoji nameTextView;
    private AlertDialog progressDialog;
    private LinearLayout settingsContainer;
    private TextCheckCell signCell;
    private boolean signMessages;
    private TextSettingsCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell3;
    private TextDetailCell typeCell;
    private LinearLayout typeEditContainer;
    private TLRPC.InputFile uploadedAvatar;

    public /* synthetic */ void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
        ImageUpdater.ImageUpdaterDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public ChatEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getInt("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = true;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
            z = false;
        }
        this.isChannel = z;
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.delegate = this;
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
            this.nameTextView.getEditText().requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        updateFields(true);
    }

    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
    }

    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return checkDiscard();
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0410, code lost:
        if (im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r0.currentChat, 0) != false) goto L_0x0412;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View createView(android.content.Context r31) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            im.bclpbkiauv.ui.components.EditTextEmoji r2 = r0.nameTextView
            if (r2 == 0) goto L_0x000b
            r2.onDestroy()
        L_0x000b:
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r0.actionBar
            r3 = 2131558496(0x7f0d0060, float:1.874231E38)
            r2.setBackButtonImage(r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r0.actionBar
            r3 = 1
            r2.setAllowOverlayTitle(r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r2 = r0.actionBar
            im.bclpbkiauv.ui.ChatEditActivity$1 r4 = new im.bclpbkiauv.ui.ChatEditActivity$1
            r4.<init>()
            r2.setActionBarMenuOnItemClick(r4)
            im.bclpbkiauv.ui.ChatEditActivity$2 r2 = new im.bclpbkiauv.ui.ChatEditActivity$2
            r2.<init>(r1)
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$y8LnwS9nnKjOi9enbMZiXKhytwY r4 = im.bclpbkiauv.ui.$$Lambda$ChatEditActivity$y8LnwS9nnKjOi9enbMZiXKhytwY.INSTANCE
            r2.setOnTouchListener(r4)
            r0.fragmentView = r2
            android.view.View r4 = r0.fragmentView
            java.lang.String r5 = "windowBackgroundGray"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r4.setBackgroundColor(r6)
            android.widget.ScrollView r4 = new android.widget.ScrollView
            r4.<init>(r1)
            r6 = -1
            r7 = -1
            r8 = 1092616192(0x41200000, float:10.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r8 = r9
            r9 = r10
            r10 = r11
            r11 = r12
            android.widget.FrameLayout$LayoutParams r6 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11)
            r2.addView(r4, r6)
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            r6.<init>(r1)
            android.content.res.Resources r7 = r31.getResources()
            r8 = 2131231564(0x7f08034c, float:1.8079213E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)
            android.graphics.drawable.GradientDrawable r7 = (android.graphics.drawable.GradientDrawable) r7
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r7.setColor(r5)
            r6.setDividerDrawable(r7)
            r5 = 2
            r6.setShowDividers(r5)
            r5 = 1084227584(0x40a00000, float:5.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r8 = (float) r8
            java.lang.String r9 = "windowBackgroundWhite"
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            android.graphics.drawable.Drawable r8 = im.bclpbkiauv.ui.actionbar.Theme.createRoundRectDrawable(r8, r9)
            r6.setBackground(r8)
            android.widget.FrameLayout$LayoutParams r8 = new android.widget.FrameLayout$LayoutParams
            r9 = -2
            r10 = -1
            r8.<init>(r10, r9)
            r4.addView(r6, r8)
            r6.setOrientation(r3)
            im.bclpbkiauv.ui.actionbar.ActionBar r8 = r0.actionBar
            r11 = 2131690425(0x7f0f03b9, float:1.9009893E38)
            java.lang.String r12 = "ChannelEdit"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r8.setTitle(r11)
            android.widget.LinearLayout r8 = new android.widget.LinearLayout
            r8.<init>(r1)
            r0.avatarContainer = r8
            r8.setOrientation(r3)
            android.widget.LinearLayout r8 = r0.avatarContainer
            android.widget.LinearLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r9)
            r6.addView(r8, r11)
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            android.widget.LinearLayout r11 = r0.avatarContainer
            r12 = 65
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r12)
            r11.addView(r8, r12)
            im.bclpbkiauv.ui.ChatEditActivity$3 r11 = new im.bclpbkiauv.ui.ChatEditActivity$3
            r11.<init>(r1)
            r0.avatarImage = r11
            r12 = 1089470464(0x40f00000, float:7.5)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r11.setRoundRadius(r12)
            im.bclpbkiauv.ui.components.BackupImageView r11 = r0.avatarImage
            r12 = 1111752704(0x42440000, float:49.0)
            r13 = 1111752704(0x42440000, float:49.0)
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r19 = 3
            r15 = 5
            if (r14 == 0) goto L_0x00f1
            r14 = 5
            goto L_0x00f2
        L_0x00f1:
            r14 = 3
        L_0x00f2:
            r14 = r14 | 16
            boolean r16 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r20 = 0
            if (r16 == 0) goto L_0x00fd
            r16 = 0
            goto L_0x00ff
        L_0x00fd:
            r16 = 1098907648(0x41800000, float:16.0)
        L_0x00ff:
            r17 = 0
            boolean r18 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r18 == 0) goto L_0x0108
            r18 = 1098907648(0x41800000, float:16.0)
            goto L_0x010a
        L_0x0108:
            r18 = 0
        L_0x010a:
            r21 = 0
            r5 = 5
            r15 = r16
            r16 = r17
            r17 = r18
            r18 = r21
            android.widget.FrameLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r8.addView(r11, r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r0.currentChat
            boolean r11 = im.bclpbkiauv.messenger.ChatObject.canChangeChatInfo(r11)
            r12 = 0
            r13 = 0
            if (r11 == 0) goto L_0x020d
            im.bclpbkiauv.ui.components.AvatarDrawable r11 = r0.avatarDrawable
            r11.setInfo(r5, r12, r12)
            android.graphics.Paint r11 = new android.graphics.Paint
            r11.<init>(r3)
            r14 = 1426063360(0x55000000, float:8.796093E12)
            r11.setColor(r14)
            im.bclpbkiauv.ui.ChatEditActivity$4 r14 = new im.bclpbkiauv.ui.ChatEditActivity$4
            r14.<init>(r1, r11)
            r0.avatarOverlay = r14
            r23 = 1111752704(0x42440000, float:49.0)
            r24 = 1111752704(0x42440000, float:49.0)
            boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x0146
            r15 = 5
            goto L_0x0147
        L_0x0146:
            r15 = 3
        L_0x0147:
            r25 = r15 | 16
            boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x0150
            r26 = 0
            goto L_0x0152
        L_0x0150:
            r26 = 1098907648(0x41800000, float:16.0)
        L_0x0152:
            r27 = 0
            boolean r15 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r15 == 0) goto L_0x015b
            r28 = 1098907648(0x41800000, float:16.0)
            goto L_0x015d
        L_0x015b:
            r28 = 0
        L_0x015d:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r14, r15)
            android.view.View r14 = r0.avatarOverlay
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$HqVFjPTXWztuQXB0DW3f5SDNRoE r15 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$HqVFjPTXWztuQXB0DW3f5SDNRoE
            r15.<init>()
            r14.setOnClickListener(r15)
            android.view.View r14 = r0.avatarOverlay
            r15 = 2131690594(0x7f0f0462, float:1.9010236E38)
            java.lang.String r9 = "ChoosePhoto"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r15)
            r14.setContentDescription(r9)
            im.bclpbkiauv.ui.ChatEditActivity$5 r9 = new im.bclpbkiauv.ui.ChatEditActivity$5
            r9.<init>(r1)
            r0.avatarEditor = r9
            android.widget.ImageView$ScaleType r14 = android.widget.ImageView.ScaleType.CENTER
            r9.setScaleType(r14)
            android.widget.ImageView r9 = r0.avatarEditor
            r14 = 2131231248(0x7f080210, float:1.8078572E38)
            r9.setImageResource(r14)
            android.widget.ImageView r9 = r0.avatarEditor
            r9.setEnabled(r13)
            android.widget.ImageView r9 = r0.avatarEditor
            r9.setClickable(r13)
            android.widget.ImageView r9 = r0.avatarEditor
            r23 = 1111752704(0x42440000, float:49.0)
            r24 = 1111752704(0x42440000, float:49.0)
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01a8
            r15 = 5
            goto L_0x01a9
        L_0x01a8:
            r15 = 3
        L_0x01a9:
            r25 = r15 | 16
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01b2
            r26 = 0
            goto L_0x01b4
        L_0x01b2:
            r26 = 1098907648(0x41800000, float:16.0)
        L_0x01b4:
            r27 = 0
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01bd
            r28 = 1098907648(0x41800000, float:16.0)
            goto L_0x01bf
        L_0x01bd:
            r28 = 0
        L_0x01bf:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r9, r14)
            im.bclpbkiauv.ui.components.RadialProgressView r9 = new im.bclpbkiauv.ui.components.RadialProgressView
            r9.<init>(r1)
            r0.avatarProgressView = r9
            r14 = 1106247680(0x41f00000, float:30.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r9.setSize(r14)
            im.bclpbkiauv.ui.components.RadialProgressView r9 = r0.avatarProgressView
            r9.setProgressColor(r10)
            im.bclpbkiauv.ui.components.RadialProgressView r9 = r0.avatarProgressView
            r23 = 1111752704(0x42440000, float:49.0)
            r24 = 1111752704(0x42440000, float:49.0)
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01e9
            r15 = 5
            goto L_0x01ea
        L_0x01e9:
            r15 = 3
        L_0x01ea:
            r25 = r15 | 16
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01f3
            r26 = 0
            goto L_0x01f5
        L_0x01f3:
            r26 = 1098907648(0x41800000, float:16.0)
        L_0x01f5:
            r27 = 0
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x01fe
            r28 = 1098907648(0x41800000, float:16.0)
            goto L_0x0200
        L_0x01fe:
            r28 = 0
        L_0x0200:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r9, r14)
            r0.showAvatarProgress(r13, r13)
            goto L_0x0216
        L_0x020d:
            im.bclpbkiauv.ui.components.AvatarDrawable r9 = r0.avatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r0.currentChat
            java.lang.String r11 = r11.title
            r9.setInfo(r5, r11, r12)
        L_0x0216:
            im.bclpbkiauv.ui.components.EditTextEmoji r9 = new im.bclpbkiauv.ui.components.EditTextEmoji
            r9.<init>(r1, r2, r0, r13)
            r0.nameTextView = r9
            boolean r11 = r0.isChannel
            if (r11 == 0) goto L_0x022e
            r11 = 2131691124(0x7f0f0674, float:1.901131E38)
            java.lang.String r14 = "EnterChannelName"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r11)
            r9.setHint(r11)
            goto L_0x023a
        L_0x022e:
            r11 = 2131691516(0x7f0f07fc, float:1.9012106E38)
            java.lang.String r14 = "GroupName"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r11)
            r9.setHint(r11)
        L_0x023a:
            im.bclpbkiauv.ui.components.EditTextEmoji r9 = r0.nameTextView
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r0.currentChat
            boolean r11 = im.bclpbkiauv.messenger.ChatObject.canChangeChatInfo(r11)
            r9.setEnabled(r11)
            im.bclpbkiauv.ui.components.EditTextEmoji r9 = r0.nameTextView
            r9.hideEditBackgroup()
            im.bclpbkiauv.ui.components.EditTextEmoji r9 = r0.nameTextView
            boolean r11 = r9.isEnabled()
            r9.setFocusable(r11)
            android.text.InputFilter[] r9 = new android.text.InputFilter[r3]
            android.text.InputFilter$LengthFilter r11 = new android.text.InputFilter$LengthFilter
            r14 = 100
            r11.<init>(r14)
            r9[r13] = r11
            im.bclpbkiauv.ui.components.EditTextEmoji r11 = r0.nameTextView
            r11.setFilters(r9)
            im.bclpbkiauv.ui.components.EditTextEmoji r11 = r0.nameTextView
            r23 = -1082130432(0xffffffffbf800000, float:-1.0)
            r24 = -1073741824(0xffffffffc0000000, float:-2.0)
            r25 = 16
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            r15 = 1119879168(0x42c00000, float:96.0)
            if (r14 == 0) goto L_0x0274
            r26 = 1084227584(0x40a00000, float:5.0)
            goto L_0x0276
        L_0x0274:
            r26 = 1119879168(0x42c00000, float:96.0)
        L_0x0276:
            r27 = 0
            boolean r14 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x027f
            r28 = 1119879168(0x42c00000, float:96.0)
            goto L_0x0281
        L_0x027f:
            r28 = 1084227584(0x40a00000, float:5.0)
        L_0x0281:
            r29 = 0
            android.widget.FrameLayout$LayoutParams r14 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r11, r14)
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            r11.<init>(r1)
            r0.settingsContainer = r11
            r11.setOrientation(r3)
            android.widget.LinearLayout r11 = r0.settingsContainer
            r14 = -2
            android.widget.LinearLayout$LayoutParams r15 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r10, (int) r14)
            r6.addView(r11, r15)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = new im.bclpbkiauv.ui.components.EditTextBoldCursor
            r11.<init>(r1)
            r0.descriptionTextView = r11
            r14 = 1098907648(0x41800000, float:16.0)
            r11.setTextSize(r3, r14)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r0.descriptionTextView
            java.lang.String r14 = "windowBackgroundWhiteHintText"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r11.setHintTextColor(r14)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r0.descriptionTextView
            java.lang.String r14 = "windowBackgroundWhiteBlackText"
            int r15 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r11.setTextColor(r15)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r11 = r0.descriptionTextView
            r15 = 1102577664(0x41b80000, float:23.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            r18 = 1094713344(0x41400000, float:12.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            r11.setPadding(r5, r10, r15, r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            r5.setBackgroundDrawable(r12)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            boolean r10 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x02e8
            r10 = 5
            goto L_0x02e9
        L_0x02e8:
            r10 = 3
        L_0x02e9:
            r5.setGravity(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            r10 = 180225(0x2c001, float:2.52549E-40)
            r5.setInputType(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            r10 = 6
            r5.setImeOptions(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            im.bclpbkiauv.tgnet.TLRPC$Chat r10 = r0.currentChat
            boolean r10 = im.bclpbkiauv.messenger.ChatObject.canChangeChatInfo(r10)
            r5.setEnabled(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r5 = r0.descriptionTextView
            boolean r10 = r5.isEnabled()
            r5.setFocusable(r10)
            android.text.InputFilter[] r5 = new android.text.InputFilter[r3]
            android.text.InputFilter$LengthFilter r9 = new android.text.InputFilter$LengthFilter
            r10 = 255(0xff, float:3.57E-43)
            r9.<init>(r10)
            r10 = 0
            r5[r10] = r9
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            r9.setFilters(r5)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            r10 = 2131690879(0x7f0f057f, float:1.9010814E38)
            java.lang.String r11 = "DescriptionOptionalPlaceholder"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r9.setHint(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r9.setCursorColor(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            r10 = 1101004800(0x41a00000, float:20.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            r9.setCursorSize(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            r10 = 1069547520(0x3fc00000, float:1.5)
            r9.setCursorWidth(r10)
            android.widget.LinearLayout r9 = r0.settingsContainer
            im.bclpbkiauv.ui.components.EditTextBoldCursor r10 = r0.descriptionTextView
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r11)
            r9.addView(r10, r13)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$RoDwOxnu43gB5pFPKnVa2jCa8Uo r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$RoDwOxnu43gB5pFPKnVa2jCa8Uo
            r10.<init>()
            r9.setOnEditorActionListener(r10)
            im.bclpbkiauv.ui.components.EditTextBoldCursor r9 = r0.descriptionTextView
            im.bclpbkiauv.ui.ChatEditActivity$6 r10 = new im.bclpbkiauv.ui.ChatEditActivity$6
            r10.<init>()
            r9.addTextChangedListener(r10)
            android.widget.LinearLayout r9 = new android.widget.LinearLayout
            r9.<init>(r1)
            r0.typeEditContainer = r9
            r9.setOrientation(r3)
            android.widget.LinearLayout r9 = r0.typeEditContainer
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r10)
            r6.addView(r9, r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = r9.megagroup
            if (r9 == 0) goto L_0x03b2
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            if (r9 == 0) goto L_0x038c
            boolean r9 = r9.can_set_location
            if (r9 == 0) goto L_0x03b2
        L_0x038c:
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = new im.bclpbkiauv.ui.cells.TextDetailCell
            r9.<init>(r1)
            r0.locationCell = r9
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r9.setBackgroundDrawable(r11)
            android.widget.LinearLayout r9 = r0.typeEditContainer
            im.bclpbkiauv.ui.cells.TextDetailCell r10 = r0.locationCell
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r11)
            r9.addView(r10, r13)
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = r0.locationCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$UgPTPCM2Q13vdd6uJDbl1F_gg3w r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$UgPTPCM2Q13vdd6uJDbl1F_gg3w
            r10.<init>()
            r9.setOnClickListener(r10)
        L_0x03b2:
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = r9.creator
            if (r9 == 0) goto L_0x03e6
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            if (r9 == 0) goto L_0x03c0
            boolean r9 = r9.can_set_username
            if (r9 == 0) goto L_0x03e6
        L_0x03c0:
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = new im.bclpbkiauv.ui.cells.TextDetailCell
            r9.<init>(r1)
            r0.typeCell = r9
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r9.setBackgroundDrawable(r11)
            android.widget.LinearLayout r9 = r0.typeEditContainer
            im.bclpbkiauv.ui.cells.TextDetailCell r10 = r0.typeCell
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r11)
            r9.addView(r10, r13)
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = r0.typeCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$Fy56C7ze0f-gEaUFFxmhF4R-H1I r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$Fy56C7ze0f-gEaUFFxmhF4R-H1I
            r10.<init>()
            r9.setOnClickListener(r10)
        L_0x03e6:
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.isChannel(r9)
            if (r9 == 0) goto L_0x0437
            boolean r9 = r0.isChannel
            if (r9 == 0) goto L_0x03fd
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r9, r3)
            if (r9 != 0) goto L_0x03fb
            goto L_0x03fd
        L_0x03fb:
            r10 = 0
            goto L_0x0412
        L_0x03fd:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            if (r9 == 0) goto L_0x0437
            boolean r10 = r0.isChannel
            if (r10 != 0) goto L_0x0437
            int r9 = r9.linked_chat_id
            if (r9 == 0) goto L_0x0437
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            r10 = 0
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.canUserDoAdminAction(r9, r10)
            if (r9 == 0) goto L_0x0437
        L_0x0412:
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = new im.bclpbkiauv.ui.cells.TextDetailCell
            r9.<init>(r1)
            r0.linkedCell = r9
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r9.setBackgroundDrawable(r11)
            android.widget.LinearLayout r9 = r0.typeEditContainer
            im.bclpbkiauv.ui.cells.TextDetailCell r10 = r0.linkedCell
            r11 = -2
            r12 = -1
            android.widget.LinearLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r12, (int) r11)
            r9.addView(r10, r13)
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = r0.linkedCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$dLFrmDFrABtL01knbREeNwwB43k r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$dLFrmDFrABtL01knbREeNwwB43k
            r10.<init>()
            r9.setOnClickListener(r10)
        L_0x0437:
            boolean r9 = r0.isChannel
            if (r9 != 0) goto L_0x046a
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.canBlockUsers(r9)
            if (r9 == 0) goto L_0x046a
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.isChannel(r9)
            if (r9 != 0) goto L_0x0451
            im.bclpbkiauv.tgnet.TLRPC$Chat r9 = r0.currentChat
            boolean r9 = r9.creator
            if (r9 == 0) goto L_0x046a
        L_0x0451:
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = new im.bclpbkiauv.ui.cells.TextDetailCell
            r9.<init>(r1)
            r0.historyCell = r9
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r9.setBackgroundDrawable(r11)
            im.bclpbkiauv.ui.cells.TextDetailCell r9 = r0.historyCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$SnnEqpIyWaeWT2Dw7lXTfeDNcDc r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$SnnEqpIyWaeWT2Dw7lXTfeDNcDc
            r10.<init>(r1)
            r9.setOnClickListener(r10)
        L_0x046a:
            boolean r9 = r0.isChannel
            r10 = -1073741824(0xffffffffc0000000, float:-2.0)
            if (r9 == 0) goto L_0x04b6
            im.bclpbkiauv.ui.cells.TextCheckCell r9 = new im.bclpbkiauv.ui.cells.TextCheckCell
            r9.<init>(r1)
            r0.signCell = r9
            r11 = 0
            android.graphics.drawable.Drawable r12 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r11)
            r9.setBackgroundDrawable(r12)
            im.bclpbkiauv.ui.cells.TextCheckCell r9 = r0.signCell
            r11 = 2131690480(0x7f0f03f0, float:1.9010005E38)
            java.lang.String r12 = "ChannelSignMessages"
            java.lang.String r22 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r11 = 2131690481(0x7f0f03f1, float:1.9010007E38)
            java.lang.String r12 = "ChannelSignMessagesInfo"
            java.lang.String r23 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            boolean r11 = r0.signMessages
            r25 = 1
            r26 = 0
            r21 = r9
            r24 = r11
            r21.setTextAndValueAndCheck(r22, r23, r24, r25, r26)
            android.widget.LinearLayout r9 = r0.typeEditContainer
            im.bclpbkiauv.ui.cells.TextCheckCell r11 = r0.signCell
            r12 = -1
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r10)
            r9.addView(r11, r13)
            im.bclpbkiauv.ui.cells.TextCheckCell r9 = r0.signCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$x9tu4WThABt2YFvtl3jDwqsvHQ8 r11 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$x9tu4WThABt2YFvtl3jDwqsvHQ8
            r11.<init>()
            r9.setOnClickListener(r11)
        L_0x04b6:
            android.widget.LinearLayout r9 = r0.typeEditContainer
            int r9 = r9.getChildCount()
            r11 = 8
            if (r9 != 0) goto L_0x04c5
            android.widget.LinearLayout r9 = r0.typeEditContainer
            r9.setVisibility(r11)
        L_0x04c5:
            im.bclpbkiauv.ui.actionbar.ActionBar r9 = r0.actionBar
            im.bclpbkiauv.ui.actionbar.ActionBarMenu r9 = r9.createMenu()
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r0.currentChat
            boolean r12 = im.bclpbkiauv.messenger.ChatObject.canChangeChatInfo(r12)
            if (r12 != 0) goto L_0x04db
            im.bclpbkiauv.ui.cells.TextCheckCell r12 = r0.signCell
            if (r12 != 0) goto L_0x04db
            im.bclpbkiauv.ui.cells.TextDetailCell r12 = r0.historyCell
            if (r12 == 0) goto L_0x04f6
        L_0x04db:
            r12 = 2131231109(0x7f080185, float:1.807829E38)
            r13 = 1113587712(0x42600000, float:56.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            im.bclpbkiauv.ui.actionbar.ActionBarMenuItem r12 = r9.addItemWithWidth(r3, r12, r13)
            r0.doneButton = r12
            r13 = 2131690974(0x7f0f05de, float:1.9011007E38)
            java.lang.String r15 = "Done"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r13)
            r12.setContentDescription(r13)
        L_0x04f6:
            android.widget.LinearLayout r12 = new android.widget.LinearLayout
            r12.<init>(r1)
            r0.infoContainer = r12
            r12.setOrientation(r3)
            android.widget.LinearLayout r12 = r0.infoContainer
            r13 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r3 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r6.addView(r12, r3)
            im.bclpbkiauv.ui.cells.TextCell r3 = new im.bclpbkiauv.ui.cells.TextCell
            r3.<init>(r1)
            r0.blockCell = r3
            r12 = 0
            android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r12)
            r3.setBackgroundDrawable(r13)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.blockCell
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r0.currentChat
            boolean r12 = im.bclpbkiauv.messenger.ChatObject.isChannel(r12)
            if (r12 != 0) goto L_0x052e
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r0.currentChat
            boolean r12 = r12.creator
            if (r12 == 0) goto L_0x052b
            goto L_0x052e
        L_0x052b:
            r12 = 8
            goto L_0x052f
        L_0x052e:
            r12 = 0
        L_0x052f:
            r3.setVisibility(r12)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.blockCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$iNFC_NGVzu6XyJ5BJNR2KBJlRF0 r12 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$iNFC_NGVzu6XyJ5BJNR2KBJlRF0
            r12.<init>()
            r3.setOnClickListener(r12)
            im.bclpbkiauv.ui.cells.TextCell r3 = new im.bclpbkiauv.ui.cells.TextCell
            r3.<init>(r1)
            r0.adminCell = r3
            r12 = 0
            android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r12)
            r3.setBackgroundDrawable(r13)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.adminCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$z-UOt1dWP7fKWUAEpw4mjciT9bw r12 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$z-UOt1dWP7fKWUAEpw4mjciT9bw
            r12.<init>()
            r3.setOnClickListener(r12)
            im.bclpbkiauv.ui.cells.TextCell r3 = new im.bclpbkiauv.ui.cells.TextCell
            r3.<init>(r1)
            r0.membersCell = r3
            r12 = 0
            android.graphics.drawable.Drawable r13 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r12)
            r3.setBackgroundDrawable(r13)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.membersCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$NG0Syj2XKjiOBJzuZBdTUZ2cjYU r12 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$NG0Syj2XKjiOBJzuZBdTUZ2cjYU
            r12.<init>()
            r3.setOnClickListener(r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x05a0
            im.bclpbkiauv.ui.cells.TextCell r3 = new im.bclpbkiauv.ui.cells.TextCell
            r3.<init>(r1)
            r0.logCell = r3
            r12 = 2131691161(0x7f0f0699, float:1.9011386E38)
            java.lang.String r13 = "EventLog"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            r13 = 2131231066(0x7f08015a, float:1.8078203E38)
            r15 = 0
            r3.setTextAndIcon(r12, r13, r15)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.logCell
            android.graphics.drawable.Drawable r12 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r15)
            r3.setBackgroundDrawable(r12)
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.logCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$yotbO2A-0f-Bkcu1p1FB1h_iB2g r12 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$yotbO2A-0f-Bkcu1p1FB1h_iB2g
            r12.<init>()
            r3.setOnClickListener(r12)
        L_0x05a0:
            boolean r3 = r0.isChannel
            if (r3 != 0) goto L_0x05b2
            android.widget.LinearLayout r3 = r0.infoContainer
            im.bclpbkiauv.ui.cells.TextCell r12 = r0.blockCell
            r13 = -2
            r15 = -1
            android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r3.addView(r12, r10)
            goto L_0x05b4
        L_0x05b2:
            r13 = -2
            r15 = -1
        L_0x05b4:
            android.widget.LinearLayout r3 = r0.infoContainer
            im.bclpbkiauv.ui.cells.TextCell r10 = r0.adminCell
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r3.addView(r10, r12)
            android.widget.LinearLayout r3 = r0.infoContainer
            im.bclpbkiauv.ui.cells.TextCell r10 = r0.membersCell
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r3.addView(r10, r12)
            boolean r3 = r0.isChannel
            if (r3 == 0) goto L_0x05d9
            android.widget.LinearLayout r3 = r0.infoContainer
            im.bclpbkiauv.ui.cells.TextCell r10 = r0.blockCell
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r3.addView(r10, r12)
        L_0x05d9:
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.logCell
            if (r3 == 0) goto L_0x05e6
            android.widget.LinearLayout r10 = r0.infoContainer
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r15, (int) r13)
            r10.addView(r3, r12)
        L_0x05e6:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.hasAdminRights(r3)
            if (r3 != 0) goto L_0x05f3
            android.widget.LinearLayout r3 = r0.infoContainer
            r3.setVisibility(r11)
        L_0x05f3:
            boolean r3 = r0.isChannel
            if (r3 != 0) goto L_0x065d
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r3 = r0.info
            if (r3 == 0) goto L_0x065d
            boolean r3 = r3.can_set_stickers
            if (r3 == 0) goto L_0x065d
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.stickersContainer = r3
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r10)
            r6.addView(r3, r12)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = new im.bclpbkiauv.ui.cells.TextSettingsCell
            r3.<init>(r1)
            r0.stickersCell = r3
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r3.setTextColor(r10)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.stickersCell
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r3.setBackgroundDrawable(r11)
            android.widget.FrameLayout r3 = r0.stickersContainer
            im.bclpbkiauv.ui.cells.TextSettingsCell r10 = r0.stickersCell
            r11 = -1073741824(0xffffffffc0000000, float:-2.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r13 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r11)
            r3.addView(r10, r13)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.stickersCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$8ehn6bJaHo5adsYx6ixEOCDwAOI r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$8ehn6bJaHo5adsYx6ixEOCDwAOI
            r10.<init>()
            r3.setOnClickListener(r10)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = new im.bclpbkiauv.ui.cells.TextInfoPrivacyCell
            r3.<init>(r1)
            r0.stickersInfoCell3 = r3
            r10 = 2131691533(0x7f0f080d, float:1.901214E38)
            java.lang.String r11 = "GroupStickersInfo"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r3.setText(r10)
            im.bclpbkiauv.ui.cells.TextInfoPrivacyCell r3 = r0.stickersInfoCell3
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r12 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r10)
            r6.addView(r3, r12)
        L_0x065d:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = r3.creator
            if (r3 == 0) goto L_0x06dc
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.deleteContainer = r3
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r10 = im.bclpbkiauv.ui.components.LayoutHelper.createLinear((int) r11, (int) r10)
            r6.addView(r3, r10)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = new im.bclpbkiauv.ui.cells.TextSettingsCell
            r3.<init>(r1)
            r0.deleteCell = r3
            java.lang.String r10 = "windowBackgroundWhiteRedText5"
            int r10 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r10)
            r3.setTextColor(r10)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.deleteCell
            r10 = 0
            android.graphics.drawable.Drawable r11 = im.bclpbkiauv.ui.actionbar.Theme.getSelectorDrawable(r10)
            r3.setBackgroundDrawable(r11)
            boolean r3 = r0.isChannel
            if (r3 == 0) goto L_0x06a1
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.deleteCell
            r11 = 2131690418(0x7f0f03b2, float:1.900988E38)
            java.lang.String r12 = "ChannelDelete"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r3.setText(r11, r10)
            goto L_0x06c4
        L_0x06a1:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = r3.megagroup
            if (r3 == 0) goto L_0x06b6
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.deleteCell
            r11 = 2131690857(0x7f0f0569, float:1.901077E38)
            java.lang.String r12 = "DeleteMega"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r3.setText(r11, r10)
            goto L_0x06c4
        L_0x06b6:
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.deleteCell
            r11 = 2131690841(0x7f0f0559, float:1.9010737E38)
            java.lang.String r12 = "DeleteAndExitButton"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            r3.setText(r11, r10)
        L_0x06c4:
            android.widget.FrameLayout r3 = r0.deleteContainer
            im.bclpbkiauv.ui.cells.TextSettingsCell r10 = r0.deleteCell
            r11 = -1073741824(0xffffffffc0000000, float:-2.0)
            r12 = -1
            android.widget.FrameLayout$LayoutParams r11 = im.bclpbkiauv.ui.components.LayoutHelper.createFrame(r12, r11)
            r3.addView(r10, r11)
            im.bclpbkiauv.ui.cells.TextSettingsCell r3 = r0.deleteCell
            im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$RimyNhSZty0Y2HZRK5ndnDLkjrE r10 = new im.bclpbkiauv.ui.-$$Lambda$ChatEditActivity$RimyNhSZty0Y2HZRK5ndnDLkjrE
            r10.<init>()
            r3.setOnClickListener(r10)
        L_0x06dc:
            im.bclpbkiauv.ui.components.EditTextEmoji r3 = r0.nameTextView
            im.bclpbkiauv.tgnet.TLRPC$Chat r10 = r0.currentChat
            java.lang.String r10 = r10.title
            r3.setText(r10)
            im.bclpbkiauv.ui.components.EditTextEmoji r3 = r0.nameTextView
            int r10 = r3.length()
            r3.setSelection(r10)
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r3 = r0.info
            if (r3 == 0) goto L_0x06f9
            im.bclpbkiauv.ui.components.EditTextBoldCursor r10 = r0.descriptionTextView
            java.lang.String r3 = r3.about
            r10.setText(r3)
        L_0x06f9:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r3 = r3.photo
            if (r3 == 0) goto L_0x0722
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r3 = r3.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r3.photo_small
            r0.avatar = r3
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r3 = r3.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r3 = r3.photo_big
            r0.avatarBig = r3
            im.bclpbkiauv.ui.components.BackupImageView r3 = r0.avatarImage
            im.bclpbkiauv.tgnet.TLRPC$Chat r10 = r0.currentChat
            r11 = 0
            im.bclpbkiauv.messenger.ImageLocation r10 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r10, r11)
            im.bclpbkiauv.ui.components.AvatarDrawable r11 = r0.avatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r0.currentChat
            java.lang.String r13 = "50_50"
            r3.setImage((im.bclpbkiauv.messenger.ImageLocation) r10, (java.lang.String) r13, (android.graphics.drawable.Drawable) r11, (java.lang.Object) r12)
            goto L_0x0729
        L_0x0722:
            im.bclpbkiauv.ui.components.BackupImageView r3 = r0.avatarImage
            im.bclpbkiauv.ui.components.AvatarDrawable r10 = r0.avatarDrawable
            r3.setImageDrawable(r10)
        L_0x0729:
            r3 = 1
            r0.updateFields(r3)
            android.view.View r3 = r0.fragmentView
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatEditActivity.createView(android.content.Context):android.view.View");
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$createView$2$ChatEditActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            public final void run() {
                ChatEditActivity.this.lambda$null$1$ChatEditActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$1$ChatEditActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.uploadedAvatar = null;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.currentChat);
    }

    public /* synthetic */ boolean lambda$createView$3$ChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$5$ChatEditActivity(View v) {
        if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission(PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION) == 0) {
            NewLocationActivity fragment = new NewLocationActivity(2, (long) (-this.chatId));
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation)) {
                fragment.setInitialLocation((TLRPC.TL_channelLocation) this.info.location);
            }
            fragment.setDelegate(new NewLocationActivity.LocationActivityDelegate() {
                public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                    ChatEditActivity.this.lambda$null$4$ChatEditActivity(messageMedia, i, z, i2);
                }
            });
            presentFragment(fragment);
            return;
        }
        getParentActivity().requestPermissions(new String[]{PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION, "android.permission.ACCESS_FINE_LOCATION"}, 2);
    }

    public /* synthetic */ void lambda$null$4$ChatEditActivity(TLRPC.MessageMedia location, int locationType, boolean notify, int scheduleDate) {
        TLRPC.TL_channelLocation channelLocation = new TLRPC.TL_channelLocation();
        channelLocation.address = location.address;
        channelLocation.geo_point = location.geo;
        this.info.location = channelLocation;
        this.info.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    public /* synthetic */ void lambda$createView$6$ChatEditActivity(View v) {
        int i = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity fragment = new ChatEditTypeActivity(i, textDetailCell != null && textDetailCell.getVisibility() == 0);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$7$ChatEditActivity(View v) {
        ChatLinkActivity fragment = new ChatLinkActivity(this.chatId);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$9$ChatEditActivity(Context context, View v) {
        Context context2 = context;
        BottomSheet.Builder builder = new BottomSheet.Builder(context2);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
        linearLayout.addView(headerCell);
        LinearLayout linearLayoutInviteContainer = new LinearLayout(context2);
        linearLayoutInviteContainer.setOrientation(1);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        RadioButtonCell[] buttons = new RadioButtonCell[2];
        int a = 0;
        for (int i = 2; a < i; i = 2) {
            buttons[a] = new RadioButtonCell(context2, true);
            buttons[a].setTag(Integer.valueOf(a));
            buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (a == 0) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), false, this.historyHidden);
            } else {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo2", R.string.ChatHistoryHiddenInfo2), false, this.historyHidden);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new View.OnClickListener(buttons, builder) {
                private final /* synthetic */ RadioButtonCell[] f$1;
                private final /* synthetic */ BottomSheet.Builder f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    ChatEditActivity.this.lambda$null$8$ChatEditActivity(this.f$1, this.f$2, view);
                }
            });
            a++;
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$null$8$ChatEditActivity(RadioButtonCell[] buttons, BottomSheet.Builder builder, View v2) {
        Integer tag = (Integer) v2.getTag();
        boolean z = false;
        buttons[0].setChecked(tag.intValue() == 0, true);
        buttons[1].setChecked(tag.intValue() == 1, true);
        if (tag.intValue() == 1) {
            z = true;
        }
        this.historyHidden = z;
        builder.getDismissRunnable().run();
        updateFields(true);
    }

    public /* synthetic */ void lambda$createView$10$ChatEditActivity(View v) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) v).setChecked(z);
    }

    public /* synthetic */ void lambda$createView$11$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", !this.isChannel ? 3 : 0);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$12$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 1);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$13$ChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putInt("chat_id", this.chatId);
        args.putInt("type", 2);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    public /* synthetic */ void lambda$createView$14$ChatEditActivity(View v) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    public /* synthetic */ void lambda$createView$15$ChatEditActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    public /* synthetic */ void lambda$createView$17$ChatEditActivity(View v) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, (TLRPC.User) null, false, new MessagesStorage.BooleanCallback() {
            public final void run(boolean z) {
                ChatEditActivity.this.lambda$null$16$ChatEditActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$ChatEditActivity(boolean param) {
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chatId)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info, true, false);
        finishFragment();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        EditTextBoldCursor editTextBoldCursor;
        if (id == NotificationCenter.chatInfoDidLoad) {
            boolean z = false;
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                    editTextBoldCursor.setText(chatFull.about);
                }
                this.info = chatFull;
                if (!ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory) {
                    z = true;
                }
                this.historyHidden = z;
                updateFields(true);
            }
        }
    }

    public void didUploadPhoto(TLRPC.InputFile file, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable(file, smallSize, bigSize) {
            private final /* synthetic */ TLRPC.InputFile f$1;
            private final /* synthetic */ TLRPC.PhotoSize f$2;
            private final /* synthetic */ TLRPC.PhotoSize f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatEditActivity.this.lambda$didUploadPhoto$18$ChatEditActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$didUploadPhoto$18$ChatEditActivity(TLRPC.InputFile file, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (file != null) {
            this.uploadedAvatar = file;
            if (this.createAfterUpload) {
                try {
                    if (this.progressDialog != null && this.progressDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChat);
        showAvatarProgress(true, false);
    }

    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        EditTextBoldCursor editTextBoldCursor;
        TLRPC.ChatFull chatFull = this.info;
        String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
        if ((this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == this.historyHidden) && this.imageUpdater.uploadingImage == null && ((this.nameTextView == null || this.currentChat.title.equals(this.nameTextView.getText().toString())) && (((editTextBoldCursor = this.descriptionTextView) == null || about.equals(editTextBoldCursor.getText().toString())) && this.signMessages == this.currentChat.signatures && this.uploadedAvatar == null && (this.avatar != null || !(this.currentChat.photo instanceof TLRPC.TL_chatPhoto))))) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$19$ChatEditActivity(dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatEditActivity.this.lambda$checkDiscard$20$ChatEditActivity(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$19$ChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public /* synthetic */ void lambda$checkDiscard$20$ChatEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private int getAdminCount() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 1;
        }
        int count = 0;
        int N = chatFull.participants.participants.size();
        for (int a = 0; a < N; a++) {
            TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(a);
            if ((chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
                count++;
            }
        }
        return count;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        EditTextEmoji editTextEmoji;
        boolean z;
        if (!this.donePressed && (editTextEmoji = this.nameTextView) != null) {
            if (editTextEmoji.length() == 0) {
                Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
                if (v != null) {
                    v.vibrate(200);
                }
                AndroidUtilities.shakeView(this.nameTextView, 2.0f, 0);
                return;
            }
            this.donePressed = true;
            if (ChatObject.isChannel(this.currentChat) || this.historyHidden) {
                if (!(this.info == null || !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory == (z = this.historyHidden))) {
                    this.info.hidden_prehistory = z;
                    MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(this.chatId, this.historyHidden);
                }
                if (this.imageUpdater.uploadingImage != null) {
                    this.createAfterUpload = true;
                    AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                    this.progressDialog = alertDialog;
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public final void onCancel(DialogInterface dialogInterface) {
                            ChatEditActivity.this.lambda$processDone$22$ChatEditActivity(dialogInterface);
                        }
                    });
                    this.progressDialog.show();
                    return;
                }
                if (!this.currentChat.title.equals(this.nameTextView.getText().toString())) {
                    MessagesController.getInstance(this.currentAccount).changeChatTitle(this.chatId, this.nameTextView.getText().toString());
                }
                TLRPC.ChatFull chatFull = this.info;
                String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
                EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
                if (editTextBoldCursor != null && !about.equals(editTextBoldCursor.getText().toString())) {
                    MessagesController.getInstance(this.currentAccount).updateChatAbout(this.chatId, this.descriptionTextView.getText().toString(), this.info);
                }
                if (this.signMessages != this.currentChat.signatures) {
                    this.currentChat.signatures = true;
                    MessagesController.getInstance(this.currentAccount).toogleChannelSignatures(this.chatId, this.signMessages);
                }
                if (this.uploadedAvatar != null) {
                    MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, this.uploadedAvatar, this.avatar, this.avatarBig);
                } else if (this.avatar == null && (this.currentChat.photo instanceof TLRPC.TL_chatPhoto)) {
                    MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, (TLRPC.InputFile) null, (TLRPC.FileLocation) null, (TLRPC.FileLocation) null);
                }
                finishFragment();
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ChatEditActivity.this.lambda$processDone$21$ChatEditActivity(i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$processDone$21$ChatEditActivity(int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        this.donePressed = false;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            chatFull.hidden_prehistory = true;
        }
        processDone();
    }

    public /* synthetic */ void lambda$processDone$22$ChatEditActivity(DialogInterface dialog) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor != null) {
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{1.0f})});
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, new float[]{0.0f})});
                }
                this.avatarAnimation.setDuration(180);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (ChatEditActivity.this.avatarAnimation != null && ChatEditActivity.this.avatarEditor != null) {
                            if (show) {
                                ChatEditActivity.this.avatarEditor.setVisibility(4);
                            } else {
                                ChatEditActivity.this.avatarProgressView.setVisibility(4);
                            }
                            AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        AnimatorSet unused = ChatEditActivity.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        String text;
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (!(imageUpdater2 == null || imageUpdater2.currentPicturePath == null)) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null && (text = editTextEmoji.getText().toString()) != null && text.length() != 0) {
            args.putString("nameTextView", text);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater2 = this.imageUpdater;
        if (imageUpdater2 != null) {
            imageUpdater2.currentPicturePath = args.getString("path");
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0034, code lost:
        r5 = r0.info;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFields(boolean r17) {
        /*
            r16 = this;
            r0 = r16
            if (r17 == 0) goto L_0x0018
            int r1 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r1 = im.bclpbkiauv.messenger.MessagesController.getInstance(r1)
            int r2 = r0.chatId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0018
            r0.currentChat = r1
        L_0x0018:
            im.bclpbkiauv.tgnet.TLRPC$Chat r1 = r0.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.historyCell
            r3 = 8
            if (r2 == 0) goto L_0x0029
            r2.setVisibility(r3)
        L_0x0029:
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.logCell
            r4 = 0
            if (r2 == 0) goto L_0x0046
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r0.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x0042
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r5 = r0.info
            if (r5 == 0) goto L_0x003f
            int r5 = r5.participants_count
            r6 = 200(0xc8, float:2.8E-43)
            if (r5 <= r6) goto L_0x003f
            goto L_0x0042
        L_0x003f:
            r5 = 8
            goto L_0x0043
        L_0x0042:
            r5 = 0
        L_0x0043:
            r2.setVisibility(r5)
        L_0x0046:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.linkedCell
            r5 = 1
            if (r2 == 0) goto L_0x0103
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x00fe
            boolean r6 = r0.isChannel
            if (r6 != 0) goto L_0x0059
            int r2 = r2.linked_chat_id
            if (r2 != 0) goto L_0x0059
            goto L_0x00fe
        L_0x0059:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r4)
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            int r2 = r2.linked_chat_id
            r6 = 2131690943(0x7f0f05bf, float:1.9010944E38)
            java.lang.String r7 = "Discussion"
            if (r2 != 0) goto L_0x007d
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.linkedCell
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r7 = 2131690950(0x7f0f05c6, float:1.9010958E38)
            java.lang.String r8 = "DiscussionInfo"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r2.setTextAndValue(r6, r7, r5)
            goto L_0x0103
        L_0x007d:
            im.bclpbkiauv.messenger.MessagesController r2 = r16.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r0.info
            int r8 = r8.linked_chat_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.getChat(r8)
            if (r2 != 0) goto L_0x0095
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.linkedCell
            r6.setVisibility(r3)
            goto L_0x0103
        L_0x0095:
            boolean r8 = r0.isChannel
            java.lang.String r9 = "@"
            if (r8 == 0) goto L_0x00ca
            java.lang.String r8 = r2.username
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x00af
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.linkedCell
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = r2.title
            r8.setTextAndValue(r6, r7, r5)
            goto L_0x0103
        L_0x00af:
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.linkedCell
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            java.lang.String r9 = r2.username
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r8.setTextAndValue(r6, r7, r5)
            goto L_0x0103
        L_0x00ca:
            java.lang.String r6 = r2.username
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            r7 = 2131691826(0x7f0f0932, float:1.9012735E38)
            java.lang.String r8 = "LinkedChannel"
            if (r6 == 0) goto L_0x00e3
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.linkedCell
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            java.lang.String r8 = r2.title
            r6.setTextAndValue(r7, r8, r4)
            goto L_0x0103
        L_0x00e3:
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.linkedCell
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r9)
            java.lang.String r9 = r2.username
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r6.setTextAndValue(r7, r8, r4)
            goto L_0x0103
        L_0x00fe:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.linkedCell
            r2.setVisibility(r3)
        L_0x0103:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.locationCell
            if (r2 == 0) goto L_0x0144
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x013f
            boolean r2 = r2.can_set_location
            if (r2 == 0) goto L_0x013f
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.locationCell
            r2.setVisibility(r4)
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelLocation
            r6 = 2131689953(0x7f0f01e1, float:1.9008936E38)
            java.lang.String r7 = "AttachLocation"
            if (r2 == 0) goto L_0x0133
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$ChannelLocation r2 = r2.location
            im.bclpbkiauv.tgnet.TLRPC$TL_channelLocation r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_channelLocation) r2
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.locationCell
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = r2.address
            r8.setTextAndValue(r6, r7, r5)
            goto L_0x0144
        L_0x0133:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.locationCell
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "Unknown address"
            r2.setTextAndValue(r6, r7, r5)
            goto L_0x0144
        L_0x013f:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.locationCell
            r2.setVisibility(r3)
        L_0x0144:
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.typeCell
            if (r2 == 0) goto L_0x0225
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x01af
            im.bclpbkiauv.tgnet.TLRPC$ChannelLocation r2 = r2.location
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_channelLocation
            if (r2 == 0) goto L_0x01af
            if (r1 == 0) goto L_0x015e
            r2 = 2131694437(0x7f0f1365, float:1.901803E38)
            java.lang.String r6 = "TypeLocationGroupEdit"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r2)
            goto L_0x0188
        L_0x015e:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "https://"
            r2.append(r6)
            int r6 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r6 = im.bclpbkiauv.messenger.MessagesController.getInstance(r6)
            java.lang.String r6 = r6.linkPrefix
            r2.append(r6)
            java.lang.String r6 = "/%s"
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            java.lang.Object[] r6 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$Chat r7 = r0.currentChat
            java.lang.String r7 = r7.username
            r6[r4] = r7
            java.lang.String r2 = java.lang.String.format(r2, r6)
        L_0x0188:
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.typeCell
            r7 = 2131694436(0x7f0f1364, float:1.9018029E38)
            java.lang.String r8 = "TypeLocationGroup"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.historyCell
            if (r8 == 0) goto L_0x019d
            int r8 = r8.getVisibility()
            if (r8 == 0) goto L_0x01a7
        L_0x019d:
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.linkedCell
            if (r8 == 0) goto L_0x01a9
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x01a9
        L_0x01a7:
            r8 = 1
            goto L_0x01aa
        L_0x01a9:
            r8 = 0
        L_0x01aa:
            r6.setTextAndValue(r7, r2, r8)
            goto L_0x0225
        L_0x01af:
            boolean r2 = r0.isChannel
            if (r2 == 0) goto L_0x01c5
            if (r1 == 0) goto L_0x01bb
            r2 = 2131694439(0x7f0f1367, float:1.9018035E38)
            java.lang.String r6 = "TypePrivate"
            goto L_0x01c0
        L_0x01bb:
            r2 = 2131694442(0x7f0f136a, float:1.901804E38)
            java.lang.String r6 = "TypePublic"
        L_0x01c0:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r2)
            goto L_0x01d6
        L_0x01c5:
            if (r1 == 0) goto L_0x01cd
            r2 = 2131694441(0x7f0f1369, float:1.9018039E38)
            java.lang.String r6 = "TypePrivateGroup"
            goto L_0x01d2
        L_0x01cd:
            r2 = 2131694443(0x7f0f136b, float:1.9018043E38)
            java.lang.String r6 = "TypePublicGroup"
        L_0x01d2:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r2)
        L_0x01d6:
            boolean r6 = r0.isChannel
            if (r6 == 0) goto L_0x0200
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.typeCell
            r7 = 2131690487(0x7f0f03f7, float:1.901002E38)
            java.lang.String r8 = "ChannelType"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.historyCell
            if (r8 == 0) goto L_0x01ef
            int r8 = r8.getVisibility()
            if (r8 == 0) goto L_0x01f9
        L_0x01ef:
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.linkedCell
            if (r8 == 0) goto L_0x01fb
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x01fb
        L_0x01f9:
            r8 = 1
            goto L_0x01fc
        L_0x01fb:
            r8 = 0
        L_0x01fc:
            r6.setTextAndValue(r7, r2, r8)
            goto L_0x0225
        L_0x0200:
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.typeCell
            r7 = 2131691534(0x7f0f080e, float:1.9012143E38)
            java.lang.String r8 = "GroupType"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.historyCell
            if (r8 == 0) goto L_0x0215
            int r8 = r8.getVisibility()
            if (r8 == 0) goto L_0x021f
        L_0x0215:
            im.bclpbkiauv.ui.cells.TextDetailCell r8 = r0.linkedCell
            if (r8 == 0) goto L_0x0221
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0221
        L_0x021f:
            r8 = 1
            goto L_0x0222
        L_0x0221:
            r8 = 0
        L_0x0222:
            r6.setTextAndValue(r7, r2, r8)
        L_0x0225:
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x024e
            im.bclpbkiauv.ui.cells.TextDetailCell r2 = r0.historyCell
            if (r2 == 0) goto L_0x024e
            boolean r2 = r0.historyHidden
            if (r2 == 0) goto L_0x0237
            r2 = 2131690518(0x7f0f0416, float:1.9010082E38)
            java.lang.String r6 = "ChatHistoryHidden"
            goto L_0x023c
        L_0x0237:
            r2 = 2131690522(0x7f0f041a, float:1.901009E38)
            java.lang.String r6 = "ChatHistoryVisible"
        L_0x023c:
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r2)
            im.bclpbkiauv.ui.cells.TextDetailCell r6 = r0.historyCell
            r7 = 2131690517(0x7f0f0415, float:1.901008E38)
            java.lang.String r8 = "ChatHistory"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            r6.setTextAndValue(r7, r2, r4)
        L_0x024e:
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            r6 = 2131691532(0x7f0f080c, float:1.9012139E38)
            java.lang.String r7 = "GroupStickers"
            if (r2 == 0) goto L_0x0276
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x026d
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r9 = r9.stickerset
            java.lang.String r9 = r9.title
            r2.setTextAndValue(r8, r9, r4)
            goto L_0x0276
        L_0x026d:
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r8 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r6)
            r2.setText(r8, r4)
        L_0x0276:
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.membersCell
            if (r2 == 0) goto L_0x0448
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r0.info
            java.lang.String r9 = "ChannelPermissions"
            r10 = 2131230819(0x7f080063, float:1.8077702E38)
            r11 = 2131690407(0x7f0f03a7, float:1.9009857E38)
            java.lang.String r12 = "ChannelBlacklist"
            r13 = 2131690484(0x7f0f03f4, float:1.9010013E38)
            java.lang.String r14 = "ChannelSubscribers"
            java.lang.String r6 = "ChannelMembers"
            r3 = 2131230821(0x7f080065, float:1.8077706E38)
            if (r8 == 0) goto L_0x03ef
            boolean r8 = r0.isChannel
            java.lang.String r15 = "%d"
            if (r8 == 0) goto L_0x02df
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.participants_count
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r4] = r9
            java.lang.String r8 = java.lang.String.format(r15, r8)
            r2.setTextAndValueAndIcon(r6, r8, r3, r5)
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r0.info
            int r8 = r8.banned_count
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r9 = r0.info
            int r9 = r9.kicked_count
            int r8 = java.lang.Math.max(r8, r9)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6[r4] = r8
            java.lang.String r6 = java.lang.String.format(r15, r6)
            im.bclpbkiauv.ui.cells.TextCell r8 = r0.logCell
            if (r8 == 0) goto L_0x02d9
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x02d9
            r8 = 1
            goto L_0x02da
        L_0x02d9:
            r8 = 0
        L_0x02da:
            r2.setTextAndValueAndIcon(r3, r6, r10, r8)
            goto L_0x03c0
        L_0x02df:
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r0.currentChat
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x0311
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.membersCell
            r8 = 2131690438(0x7f0f03c6, float:1.900992E38)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r8)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r10 = r0.info
            int r10 = r10.participants_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r4] = r10
            java.lang.String r8 = java.lang.String.format(r15, r8)
            im.bclpbkiauv.ui.cells.TextCell r10 = r0.logCell
            if (r10 == 0) goto L_0x030c
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x030c
            r10 = 1
            goto L_0x030d
        L_0x030c:
            r10 = 0
        L_0x030d:
            r2.setTextAndValueAndIcon(r6, r8, r3, r10)
            goto L_0x0340
        L_0x0311:
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.membersCell
            r8 = 2131690438(0x7f0f03c6, float:1.900992E38)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r8)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r10 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$ChatParticipants r10 = r10.participants
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$ChatParticipant> r10 = r10.participants
            int r10 = r10.size()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r4] = r10
            java.lang.String r8 = java.lang.String.format(r15, r8)
            im.bclpbkiauv.ui.cells.TextCell r10 = r0.logCell
            if (r10 == 0) goto L_0x033c
            int r10 = r10.getVisibility()
            if (r10 != 0) goto L_0x033c
            r10 = 1
            goto L_0x033d
        L_0x033c:
            r10 = 0
        L_0x033d:
            r2.setTextAndValueAndIcon(r6, r8, r3, r10)
        L_0x0340:
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            if (r3 == 0) goto L_0x0398
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_stickers
            if (r3 != 0) goto L_0x0351
            int r2 = r2 + 1
        L_0x0351:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_media
            if (r3 != 0) goto L_0x035b
            int r2 = r2 + 1
        L_0x035b:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.embed_links
            if (r3 != 0) goto L_0x0365
            int r2 = r2 + 1
        L_0x0365:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_messages
            if (r3 != 0) goto L_0x036f
            int r2 = r2 + 1
        L_0x036f:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.pin_messages
            if (r3 != 0) goto L_0x0379
            int r2 = r2 + 1
        L_0x0379:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.send_polls
            if (r3 != 0) goto L_0x0383
            int r2 = r2 + 1
        L_0x0383:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.invite_users
            if (r3 != 0) goto L_0x038d
            int r2 = r2 + 1
        L_0x038d:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r0.currentChat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatBannedRights r3 = r3.default_banned_rights
            boolean r3 = r3.change_info
            if (r3 != 0) goto L_0x039a
            int r2 = r2 + 1
            goto L_0x039a
        L_0x0398:
            r2 = 8
        L_0x039a:
            im.bclpbkiauv.ui.cells.TextCell r3 = r0.blockCell
            r6 = 2131690464(0x7f0f03e0, float:1.9009972E38)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r6)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r2)
            r8[r4] = r9
            r9 = 8
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r8[r5] = r9
            java.lang.String r9 = "%d/%d"
            java.lang.String r8 = java.lang.String.format(r9, r8)
            r9 = 2131230817(0x7f080061, float:1.8077697E38)
            r3.setTextAndValueAndIcon(r6, r8, r9, r5)
        L_0x03c0:
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.adminCell
            r3 = 2131690401(0x7f0f03a1, float:1.9009845E38)
            java.lang.String r6 = "ChannelAdministrators"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r3)
            java.lang.Object[] r6 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$Chat r8 = r0.currentChat
            boolean r8 = im.bclpbkiauv.messenger.ChatObject.isChannel(r8)
            if (r8 == 0) goto L_0x03da
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r8 = r0.info
            int r8 = r8.admins_count
            goto L_0x03de
        L_0x03da:
            int r8 = r16.getAdminCount()
        L_0x03de:
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6[r4] = r8
            java.lang.String r6 = java.lang.String.format(r15, r6)
            r8 = 2131230815(0x7f08005f, float:1.8077693E38)
            r2.setTextAndValueAndIcon(r3, r6, r8, r5)
            goto L_0x0448
        L_0x03ef:
            boolean r8 = r0.isChannel
            if (r8 == 0) goto L_0x0411
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r13)
            r2.setTextAndIcon(r6, r3, r5)
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.blockCell
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            im.bclpbkiauv.ui.cells.TextCell r6 = r0.logCell
            if (r6 == 0) goto L_0x040c
            int r6 = r6.getVisibility()
            if (r6 != 0) goto L_0x040c
            r6 = 1
            goto L_0x040d
        L_0x040c:
            r6 = 0
        L_0x040d:
            r2.setTextAndIcon(r3, r10, r6)
            goto L_0x0437
        L_0x0411:
            r8 = 2131690438(0x7f0f03c6, float:1.900992E38)
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r8)
            im.bclpbkiauv.ui.cells.TextCell r8 = r0.logCell
            if (r8 == 0) goto L_0x0424
            int r8 = r8.getVisibility()
            if (r8 != 0) goto L_0x0424
            r8 = 1
            goto L_0x0425
        L_0x0424:
            r8 = 0
        L_0x0425:
            r2.setTextAndIcon(r6, r3, r8)
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.blockCell
            r3 = 2131690464(0x7f0f03e0, float:1.9009972E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r3)
            r6 = 2131230817(0x7f080061, float:1.8077697E38)
            r2.setTextAndIcon(r3, r6, r5)
        L_0x0437:
            im.bclpbkiauv.ui.cells.TextCell r2 = r0.adminCell
            r3 = 2131690401(0x7f0f03a1, float:1.9009845E38)
            java.lang.String r6 = "ChannelAdministrators"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r3)
            r6 = 2131230815(0x7f08005f, float:1.8077693E38)
            r2.setTextAndIcon(r3, r6, r5)
        L_0x0448:
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            if (r2 == 0) goto L_0x0473
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r2 = r0.info
            if (r2 == 0) goto L_0x0473
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x0467
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            r3 = 2131691532(0x7f0f080c, float:1.9012139E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r3)
            im.bclpbkiauv.tgnet.TLRPC$ChatFull r5 = r0.info
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r5 = r5.stickerset
            java.lang.String r5 = r5.title
            r2.setTextAndValue(r3, r5, r4)
            goto L_0x0473
        L_0x0467:
            r3 = 2131691532(0x7f0f080c, float:1.9012139E38)
            im.bclpbkiauv.ui.cells.TextSettingsCell r2 = r0.stickersCell
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r3)
            r2.setText(r3, r4)
        L_0x0473:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.ChatEditActivity.updateFields(boolean):void");
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatEditActivity.this.lambda$getThemeDescriptions$23$ChatEditActivity();
            }
        };
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGray), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayIcon), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText2), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrack), new ThemeDescription((View) this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_switchTrackChecked), new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteRedText5), new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_listSelector), new ThemeDescription((View) this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.stickersInfoCell3, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundGrayShadow), new ThemeDescription((View) this.stickersInfoCell3, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, (String) Theme.key_windowBackgroundWhiteGrayText4), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, themeDescriptionDelegate, Theme.key_avatar_text), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink)};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$23$ChatEditActivity() {
        if (this.avatarImage != null) {
            this.avatarDrawable.setInfo(5, (String) null, (String) null);
            this.avatarImage.invalidate();
        }
    }
}
