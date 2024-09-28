package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.DialogObject;
import im.bclpbkiauv.messenger.Emoji;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaDataController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.SharedConfig;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.DialogsActivity;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.CheckBoxBase;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import java.util.ArrayList;

public class DialogCell extends BaseCell {
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
    private boolean attachedToWindow;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private int avatarLeft;
    private int bottomClip;
    private TLRPC.Chat chat;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    private int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean clearingDialog;
    private float clipProgress;
    private int clockDrawLeft;
    private int clockDrawTop;
    private float cornerProgress;
    private boolean countIsBiggerThanTen;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop;
    private int countWidth;
    private int currentAccount;
    private int currentDialogFolderDialogsCount;
    private int currentDialogFolderId;
    private long currentDialogId;
    private int currentEditDate;
    private float currentRevealBounceProgress;
    private float currentRevealProgress;
    private CustomDialog customDialog;
    private boolean dialogMuted;
    private int dialogsType;
    private TLRPC.DraftMessage draftMessage;
    private boolean drawBotIcon;
    private boolean drawBroadcastIcon;
    private boolean drawCheck1;
    private boolean drawCheck2;
    private boolean drawClockIcon;
    private boolean drawCount;
    private boolean drawErrorIcon;
    private boolean drawGroupIcon;
    private boolean drawMentionIcon;
    private boolean drawPinBackground;
    private boolean drawPinIcon;
    private boolean drawReorder;
    private boolean drawRevealBackground;
    private boolean drawScam;
    private boolean drawSecretLockIcon;
    private boolean drawVerifiedIcon;
    private TLRPC.EncryptedChat encryptedChat;
    private int errorLeft;
    private int errorTop;
    private int folderId;
    public boolean fullSeparator;
    public boolean fullSeparator2;
    private int index;
    private BounceInterpolator interpolator;
    private boolean isDialogCell;
    private boolean isSelected;
    private boolean isSliding;
    private long lastCheckBoxAnimationTime;
    private int lastMessageDate;
    private CharSequence lastMessageString;
    private CharSequence lastPrintString;
    private int lastSendState;
    private boolean lastUnreadState;
    private long lastUpdateTime;
    private boolean markUnread;
    private int mentionCount;
    private StaticLayout mentionLayout;
    private int mentionLeft;
    private int mentionWidth;
    private MessageObject message;
    private int messageId;
    private StaticLayout messageLayout;
    private int messageLeft;
    private StaticLayout messageNameLayout;
    private int messageNameLeft;
    private int messageNameTop;
    private int messageTop;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameMuteLeft;
    private float onlineProgress;
    private int pinLeft;
    private int pinTop;
    private int position;
    private int recorderLeft;
    private int recorderTop;
    private RectF rect;
    private float reorderIconProgress;
    private StaticLayout timeLayout;
    private int timeLeft;
    private int timeTop;
    private int topClip;
    private float topOffset;
    private boolean translationAnimationStarted;
    private RLottieDrawable translationDrawable;
    private float translationX;
    private int unreadCount;
    public boolean useForceThreeLines;
    public boolean useSeparator;
    private TLRPC.User user;

    public static class CustomDialog {
        public int date;
        public int id;
        public boolean isMedia;
        public String message;
        public boolean muted;
        public String name;
        public boolean pinned;
        public boolean sent;
        public int type;
        public int unread_count;
        public boolean verified;
    }

    public class BounceInterpolator implements Interpolator {
        public BounceInterpolator() {
        }

        public float getInterpolation(float t) {
            if (t < 0.33f) {
                return (t / 0.33f) * 0.1f;
            }
            float t2 = t - 0.33f;
            if (t2 < 0.33f) {
                return 0.1f - ((t2 / 0.34f) * 0.15f);
            }
            return (((t2 - 0.34f) / 0.33f) * 0.05f) - 89.6f;
        }
    }

    public DialogCell(Context context, boolean forceThreeLines) {
        this(context, false, forceThreeLines);
    }

    public DialogCell(Context context, boolean needCheck, boolean forceThreeLines) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.interpolator = new BounceInterpolator();
        this.rect = new RectF();
        this.checkBoxVisible = needCheck;
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.useForceThreeLines = forceThreeLines;
        setClipChildren(false);
    }

    public void setDialog(CustomDialog dialog) {
        this.customDialog = dialog;
        this.messageId = 0;
        update(0);
        checkOnline();
    }

    public void setDialog(TLRPC.Dialog dialog, int type, int folder) {
        this.currentDialogId = dialog.id;
        this.isDialogCell = true;
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            this.currentDialogFolderId = ((TLRPC.TL_dialogFolder) dialog).folder.id;
        } else {
            this.currentDialogFolderId = 0;
        }
        this.dialogsType = type;
        this.folderId = folder;
        this.messageId = 0;
        update(0);
        checkOnline();
    }

    public void setDialog(long dialog_id, MessageObject messageObject, int date) {
        this.currentDialogId = dialog_id;
        this.message = messageObject;
        this.isDialogCell = false;
        this.lastMessageDate = date;
        this.currentEditDate = messageObject != null ? messageObject.messageOwner.edit_date : 0;
        this.unreadCount = 0;
        this.markUnread = false;
        this.messageId = messageObject != null ? messageObject.getId() : 0;
        this.mentionCount = 0;
        this.lastUnreadState = messageObject != null && messageObject.isUnread();
        MessageObject messageObject2 = this.message;
        if (messageObject2 != null) {
            this.lastSendState = messageObject2.messageOwner.send_state;
        }
        update(0);
    }

    public void setCheckBoxVisible(boolean visible, boolean animated, int position2) {
        this.position = position2;
        if (visible && this.checkBox == null) {
            CheckBoxBase checkBoxBase = new CheckBoxBase(this, 21);
            this.checkBox = checkBoxBase;
            if (this.attachedToWindow) {
                checkBoxBase.onAttachedToWindow();
            }
        }
        this.checkBoxVisible = visible;
        this.checkBoxAnimationInProgress = animated;
        if (animated) {
            this.lastCheckBoxAnimationTime = SystemClock.uptimeMillis();
        } else {
            this.checkBoxAnimationProgress = visible ? 1.0f : 0.0f;
        }
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.setChecked(checked, animated);
        }
    }

    public void setScrollX(int value) {
        super.setScrollX(value);
    }

    private void checkOnline() {
        TLRPC.User user2 = this.user;
        this.onlineProgress = user2 != null && !user2.self && ((this.user.status != null && this.user.status.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Integer.valueOf(this.user.id))) ? 1.0f : 0.0f;
    }

    public void setDialogIndex(int i) {
        this.index = i;
    }

    public int getDialogIndex() {
        return this.index;
    }

    public long getDialogId() {
        return this.currentDialogId;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public boolean isUnread() {
        return (this.unreadCount != 0 || this.markUnread) && !this.dialogMuted;
    }

    public boolean isPinned() {
        return this.drawPinIcon;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isSliding = false;
        this.drawRevealBackground = false;
        this.currentRevealProgress = 0.0f;
        this.attachedToWindow = false;
        this.reorderIconProgress = (!this.drawPinIcon || !this.drawReorder) ? 0.0f : 1.0f;
        this.avatarImage.onDetachedFromWindow();
        RLottieDrawable rLottieDrawable = this.translationDrawable;
        if (rLottieDrawable != null) {
            rLottieDrawable.stop();
            this.translationDrawable.setProgress(0.0f);
            this.translationDrawable.setCallback((Drawable.Callback) null);
            this.translationDrawable = null;
            this.translationAnimationStarted = false;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        boolean z = SharedConfig.archiveHidden;
        this.archiveHidden = z;
        float f = 1.0f;
        float f2 = z ? 0.0f : 1.0f;
        this.archiveBackgroundProgress = f2;
        this.avatarDrawable.setArchivedAvatarHiddenProgress(f2);
        this.clipProgress = 0.0f;
        this.isSliding = false;
        if (!this.drawPinIcon || !this.drawReorder) {
            f = 0.0f;
        }
        this.reorderIconProgress = f;
        this.attachedToWindow = true;
        this.cornerProgress = 0.0f;
        setTranslationX(0.0f);
        setTranslationY(0.0f);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp((this.useForceThreeLines || SharedConfig.useThreeLinesLayout) ? 77.0f : 71.0f) + (this.useSeparator ? 1 : 0));
        this.topClip = 0;
        this.bottomClip = getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!(this.currentDialogId == 0 && this.customDialog == null) && changed) {
            try {
                buildLayout();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private CharSequence formatArchivedDialogNames() {
        String title;
        ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(this.currentAccount).getDialogs(this.currentDialogFolderId);
        this.currentDialogFolderDialogsCount = dialogs.size();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int N = dialogs.size();
        for (int a = 0; a < N; a++) {
            TLRPC.Dialog dialog = dialogs.get(a);
            TLRPC.User currentUser = null;
            TLRPC.Chat currentChat = null;
            if (DialogObject.isSecretDialogId(dialog.id)) {
                TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog.id >> 32)));
                if (encryptedChat2 != null) {
                    currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat2.user_id));
                }
            } else {
                int lowerId = (int) dialog.id;
                if (lowerId > 0) {
                    currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lowerId));
                } else {
                    currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
                }
            }
            if (currentChat != null) {
                title = currentChat.title.replace(10, ' ');
            } else if (currentUser == null) {
                continue;
            } else if (UserObject.isDeleted(currentUser)) {
                title = LocaleController.getString("HiddenName", R.string.HiddenName);
            } else {
                title = ContactsController.formatName(currentUser.first_name, currentUser.last_name).replace(10, ' ');
            }
            if (builder.length() > 0) {
                builder.append(", ");
            }
            int boldStart = builder.length();
            int boldEnd = title.length() + boldStart;
            builder.append(title);
            if (dialog.unread_count > 0) {
                builder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 0, Theme.getColor(Theme.key_chats_nameArchived)), boldStart, boldEnd, 33);
            }
            if (builder.length() > 150) {
                break;
            }
        }
        return Emoji.replaceEmoji(builder, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0f), false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v197, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v199, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v210, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r8v51 */
    /* JADX WARNING: type inference failed for: r8v52 */
    /* JADX WARNING: type inference failed for: r8v54 */
    /* JADX WARNING: type inference failed for: r7v95 */
    /* JADX WARNING: type inference failed for: r7v96 */
    /* JADX WARNING: type inference failed for: r7v99 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0348  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0372  */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0cb3  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0cd0  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0d65  */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0dae  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x0dbf  */
    /* JADX WARNING: Removed duplicated region for block: B:600:0x0df0  */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0e48  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0e6c  */
    /* JADX WARNING: Removed duplicated region for block: B:622:0x0e9b A[SYNTHETIC, Splitter:B:622:0x0e9b] */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0f3b  */
    /* JADX WARNING: Removed duplicated region for block: B:641:0x0f40  */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0fd1  */
    /* JADX WARNING: Removed duplicated region for block: B:693:0x105f  */
    /* JADX WARNING: Removed duplicated region for block: B:694:0x106d  */
    /* JADX WARNING: Removed duplicated region for block: B:698:0x107e A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x1089 A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:707:0x1094 A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:717:0x10b4 A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x10e2 A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x10e5 A[Catch:{ Exception -> 0x10f8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:730:0x1100  */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x121b  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r43 = this;
            r1 = r43
            boolean r0 = r1.useForceThreeLines
            r2 = 1094713344(0x41400000, float:12.0)
            r3 = 1096810496(0x41600000, float:14.0)
            if (r0 != 0) goto L_0x004b
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x000f
            goto L_0x004b
        L_0x000f:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_nameEncryptedPaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r4 = 1095761920(0x41500000, float:13.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r5 = "chats_message"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r4.linkColor = r5
            r0.setColor(r5)
            goto L_0x0082
        L_0x004b:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_nameEncryptedPaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            float r4 = (float) r4
            r0.setTextSize(r4)
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r5 = "chats_message_threeLines"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)
            r4.linkColor = r5
            r0.setColor(r5)
        L_0x0082:
            r4 = 0
            r1.currentDialogFolderDialogsCount = r4
            java.lang.String r5 = ""
            java.lang.String r6 = ""
            r7 = 0
            r8 = 0
            java.lang.String r9 = ""
            r0 = 0
            r10 = 0
            boolean r11 = r1.isDialogCell
            if (r11 == 0) goto L_0x00a4
            int r11 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r11 = im.bclpbkiauv.messenger.MessagesController.getInstance(r11)
            android.util.LongSparseArray<java.lang.CharSequence> r11 = r11.printingStrings
            long r12 = r1.currentDialogId
            java.lang.Object r11 = r11.get(r12)
            r10 = r11
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
        L_0x00a4:
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            r12 = 1
            r1.drawGroupIcon = r4
            r1.drawBroadcastIcon = r4
            r1.drawSecretLockIcon = r4
            r1.drawBotIcon = r4
            r1.drawVerifiedIcon = r4
            r1.drawScam = r4
            r1.drawPinBackground = r4
            im.bclpbkiauv.tgnet.TLRPC$User r13 = r1.user
            boolean r13 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r13)
            r14 = 1
            r13 = r13 ^ r14
            r15 = 1
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r2 < r3) goto L_0x00da
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x00cc
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x00d0
        L_0x00cc:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x00d5
        L_0x00d0:
            java.lang.String r2 = "%2$s: ⁨%1$s⁩"
            r18 = 1
            goto L_0x00ef
        L_0x00d5:
            java.lang.String r2 = "⁨%s⁩"
            r18 = 0
            goto L_0x00ef
        L_0x00da:
            boolean r2 = r1.useForceThreeLines
            if (r2 != 0) goto L_0x00e2
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r2 == 0) goto L_0x00e6
        L_0x00e2:
            int r2 = r1.currentDialogFolderId
            if (r2 == 0) goto L_0x00eb
        L_0x00e6:
            java.lang.String r2 = "%2$s: %1$s"
            r18 = 1
            goto L_0x00ef
        L_0x00eb:
            java.lang.String r2 = "%1$s"
            r18 = 0
        L_0x00ef:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            if (r3 == 0) goto L_0x00f6
            java.lang.CharSequence r3 = r3.messageText
            goto L_0x00f7
        L_0x00f6:
            r3 = 0
        L_0x00f7:
            r1.lastMessageString = r3
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r4 = "%d"
            r23 = 1101004800(0x41a00000, float:20.0)
            r24 = 1102053376(0x41b00000, float:22.0)
            r25 = 1099956224(0x41900000, float:18.0)
            r14 = 2
            if (r3 == 0) goto L_0x0297
            int r3 = r3.type
            if (r3 != r14) goto L_0x0132
            r3 = 1
            r1.drawSecretLockIcon = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0124
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0116
            goto L_0x0124
        L_0x0116:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x011c
            goto L_0x0196
        L_0x011c:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x0124:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x012a
            goto L_0x0196
        L_0x012a:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x0132:
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            boolean r3 = r3.verified
            r1.drawVerifiedIcon = r3
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0166
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r14 = 1
            if (r3 != r14) goto L_0x0166
            r1.drawGroupIcon = r14
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x015a
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x014e
            goto L_0x015a
        L_0x014e:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0153
            goto L_0x0196
        L_0x0153:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x015a:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x015f
            goto L_0x0196
        L_0x015f:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x0166:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0183
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x016f
            goto L_0x0183
        L_0x016f:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x017c
            r3 = 1117257728(0x42980000, float:76.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x017c:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x0183:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0190
            r3 = 1117519872(0x429c0000, float:78.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x0196
        L_0x0190:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
        L_0x0196:
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            int r3 = r3.type
            r14 = 1
            if (r3 != r14) goto L_0x023c
            r3 = 2131691435(0x7f0f07ab, float:1.9011942E38)
            java.lang.String r14 = "FromYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r14, r3)
            r12 = 0
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            boolean r3 = r3.isMedia
            if (r3 == 0) goto L_0x01dd
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r3 = 1
            java.lang.Object[] r14 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
            r26 = r5
            r5 = 0
            r14[r5] = r3
            java.lang.String r3 = java.lang.String.format(r2, r14)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r3)
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan
            java.lang.String r19 = "chats_attachMessage"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            r14.<init>(r5)
            int r5 = r3.length()
            r27 = r6
            r28 = r7
            r6 = 33
            r7 = 0
            r3.setSpan(r14, r7, r5, r6)
            goto L_0x022d
        L_0x01dd:
            r26 = r5
            r27 = r6
            r28 = r7
            r7 = 0
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            int r5 = r3.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x01f4
            java.lang.String r3 = r3.substring(r7, r6)
        L_0x01f4:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x021b
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0200
            r5 = 2
            r7 = 0
            r14 = 1
            goto L_0x021e
        L_0x0200:
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r7 = 32
            r14 = 10
            java.lang.String r19 = r3.replace(r14, r7)
            r7 = 0
            r6[r7] = r19
            r14 = 1
            r6[r14] = r0
            java.lang.String r6 = java.lang.String.format(r2, r6)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)
            r3 = r6
            goto L_0x022d
        L_0x021b:
            r5 = 2
            r7 = 0
            r14 = 1
        L_0x021e:
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r6[r7] = r3
            r6[r14] = r0
            java.lang.String r5 = java.lang.String.format(r2, r6)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r5)
            r3 = r5
        L_0x022d:
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            java.lang.CharSequence r3 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r3, r5, r6, r7)
            goto L_0x024e
        L_0x023c:
            r26 = r5
            r27 = r6
            r28 = r7
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r3 = r1.customDialog
            java.lang.String r3 = r3.message
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r5 = r1.customDialog
            boolean r5 = r5.isMedia
            if (r5 == 0) goto L_0x024e
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
        L_0x024e:
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r5 = r1.customDialog
            int r5 = r5.date
            long r5 = (long) r5
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r6 = r1.customDialog
            int r6 = r6.unread_count
            if (r6 == 0) goto L_0x0272
            r6 = 1
            r1.drawCount = r6
            java.lang.Object[] r7 = new java.lang.Object[r6]
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r6 = r1.customDialog
            int r6 = r6.unread_count
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r9 = 0
            r7[r9] = r6
            java.lang.String r7 = java.lang.String.format(r4, r7)
            goto L_0x0277
        L_0x0272:
            r9 = 0
            r1.drawCount = r9
            r7 = r28
        L_0x0277:
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r4 = r1.customDialog
            boolean r4 = r4.sent
            if (r4 == 0) goto L_0x0287
            r4 = 1
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r1.drawClockIcon = r9
            r1.drawErrorIcon = r9
            goto L_0x028f
        L_0x0287:
            r1.drawCheck1 = r9
            r1.drawCheck2 = r9
            r1.drawClockIcon = r9
            r1.drawErrorIcon = r9
        L_0x028f:
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r4 = r1.customDialog
            java.lang.String r4 = r4.name
            r9 = r3
            r3 = r0
            goto L_0x0b81
        L_0x0297:
            r26 = r5
            r27 = r6
            r28 = r7
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x02ba
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x02a6
            goto L_0x02ba
        L_0x02a6:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02b3
            r3 = 1117257728(0x42980000, float:76.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x02cd
        L_0x02b3:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x02cd
        L_0x02ba:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02c7
            r3 = 1117519872(0x429c0000, float:78.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x02cd
        L_0x02c7:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
        L_0x02cd:
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x02fd
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x03bd
            r3 = 1
            r1.drawSecretLockIcon = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x02ef
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x02e1
            goto L_0x02ef
        L_0x02e1:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02e7
            goto L_0x03bd
        L_0x02e7:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x03bd
        L_0x02ef:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x02f5
            goto L_0x03bd
        L_0x02f5:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
            goto L_0x03bd
        L_0x02fd:
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x03bd
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0379
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x0312
            r3 = 1
            r1.drawScam = r3
            im.bclpbkiauv.ui.components.ScamDrawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x0318
        L_0x0312:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.verified
            r1.drawVerifiedIcon = r3
        L_0x0318:
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x03bd
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0350
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0325
            goto L_0x0350
        L_0x0325:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            int r3 = r3.id
            if (r3 < 0) goto L_0x033f
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x033b
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x033b
            r3 = 1
            goto L_0x0340
        L_0x033b:
            r3 = 1
            r1.drawGroupIcon = r3
            goto L_0x0342
        L_0x033f:
            r3 = 1
        L_0x0340:
            r1.drawBroadcastIcon = r3
        L_0x0342:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0348
            goto L_0x03bd
        L_0x0348:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x03bd
        L_0x0350:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            int r3 = r3.id
            if (r3 < 0) goto L_0x036a
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0366
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0366
            r3 = 1
            goto L_0x036b
        L_0x0366:
            r3 = 1
            r1.drawGroupIcon = r3
            goto L_0x036d
        L_0x036a:
            r3 = 1
        L_0x036b:
            r1.drawBroadcastIcon = r3
        L_0x036d:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0372
            goto L_0x03bd
        L_0x0372:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
            goto L_0x03bd
        L_0x0379:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x03bd
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x038a
            r3 = 1
            r1.drawScam = r3
            im.bclpbkiauv.ui.components.ScamDrawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x0390
        L_0x038a:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.verified
            r1.drawVerifiedIcon = r3
        L_0x0390:
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x03bd
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.bot
            if (r3 == 0) goto L_0x03bd
            r3 = 1
            r1.drawBotIcon = r3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x03b2
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x03a6
            goto L_0x03b2
        L_0x03a6:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03ab
            goto L_0x03bd
        L_0x03ab:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            r1.nameLeft = r3
            goto L_0x03bd
        L_0x03b2:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03b7
            goto L_0x03bd
        L_0x03b7:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.nameLeft = r3
        L_0x03bd:
            int r3 = r1.lastMessageDate
            int r5 = r1.lastMessageDate
            if (r5 != 0) goto L_0x03cb
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x03cb
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r3 = r5.date
        L_0x03cb:
            boolean r5 = r1.isDialogCell
            if (r5 == 0) goto L_0x0430
            int r5 = r1.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r5 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r5)
            long r6 = r1.currentDialogId
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r5.getDraft(r6)
            r1.draftMessage = r5
            if (r5 == 0) goto L_0x03f7
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x03ed
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            int r5 = r5.reply_to_msg_id
            if (r5 == 0) goto L_0x042a
        L_0x03ed:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            int r5 = r5.date
            if (r3 <= r5) goto L_0x03f7
            int r5 = r1.unreadCount
            if (r5 != 0) goto L_0x042a
        L_0x03f7:
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = im.bclpbkiauv.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x0419
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = r5.megagroup
            if (r5 != 0) goto L_0x0419
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = r5.creator
            if (r5 != 0) goto L_0x0419
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r5 = r5.admin_rights
            if (r5 == 0) goto L_0x042a
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r5 = r5.admin_rights
            boolean r5 = r5.post_messages
            if (r5 == 0) goto L_0x042a
        L_0x0419:
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            if (r5 == 0) goto L_0x042e
            boolean r5 = r5.left
            if (r5 != 0) goto L_0x042a
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r1.chat
            boolean r5 = r5.kicked
            if (r5 == 0) goto L_0x0428
            goto L_0x042a
        L_0x0428:
            r5 = 0
            goto L_0x0433
        L_0x042a:
            r5 = 0
            r1.draftMessage = r5
            goto L_0x0433
        L_0x042e:
            r5 = 0
            goto L_0x0433
        L_0x0430:
            r5 = 0
            r1.draftMessage = r5
        L_0x0433:
            if (r10 == 0) goto L_0x0441
            r9 = r10
            r1.lastPrintString = r10
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r29 = r3
            r30 = r8
            r3 = 2
            goto L_0x09cb
        L_0x0441:
            r1.lastPrintString = r5
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x050d
            r12 = 0
            r5 = 2131690977(0x7f0f05e1, float:1.9011013E38)
            java.lang.String r6 = "Draft"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r5)
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 == 0) goto L_0x0490
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0487
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0466
            r29 = r3
            goto L_0x0489
        L_0x0466:
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r6 = new android.text.style.ForegroundColorSpan
            java.lang.String r7 = "chats_draft"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.<init>(r7)
            int r7 = r0.length()
            r29 = r3
            r3 = 0
            r14 = 33
            r5.setSpan(r6, r3, r7, r14)
            r9 = r5
            r30 = r8
            r3 = 2
            goto L_0x09cb
        L_0x0487:
            r29 = r3
        L_0x0489:
            java.lang.String r9 = ""
            r30 = r8
            r3 = 2
            goto L_0x09cb
        L_0x0490:
            r29 = r3
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r5 = r3.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x04a3
            r5 = 0
            java.lang.String r3 = r3.substring(r5, r6)
        L_0x04a3:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x04e1
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x04af
            r30 = r8
            r8 = 0
            goto L_0x04e4
        L_0x04af:
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 32
            r7 = 10
            java.lang.String r14 = r3.replace(r7, r5)
            r5 = 0
            r6[r5] = r14
            r7 = 1
            r6[r7] = r0
            java.lang.String r6 = java.lang.String.format(r2, r6)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r6)
            android.text.style.ForegroundColorSpan r14 = new android.text.style.ForegroundColorSpan
            java.lang.String r19 = "chats_draft"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            r14.<init>(r5)
            int r5 = r0.length()
            int r5 = r5 + r7
            r30 = r8
            r7 = 33
            r8 = 0
            r6.setSpan(r14, r8, r5, r7)
            goto L_0x04fc
        L_0x04e1:
            r30 = r8
            r8 = 0
        L_0x04e4:
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 32
            r7 = 10
            java.lang.String r14 = r3.replace(r7, r5)
            r6[r8] = r14
            r5 = 1
            r6[r5] = r0
            java.lang.String r5 = java.lang.String.format(r2, r6)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r5)
        L_0x04fc:
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            java.lang.CharSequence r9 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r6, r5, r7, r8)
            r3 = 2
            goto L_0x09cb
        L_0x050d:
            r29 = r3
            r30 = r8
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0523
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r3 = 2131691583(0x7f0f083f, float:1.9012242E38)
            java.lang.String r5 = "HistoryCleared"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r3 = 2
            goto L_0x09cb
        L_0x0523:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.String r5 = ""
            if (r3 != 0) goto L_0x05db
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x05d6
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            boolean r6 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatRequested
            if (r6 == 0) goto L_0x0541
            r3 = 2131691115(0x7f0f066b, float:1.9011293E38)
            java.lang.String r5 = "EncryptionProcessing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r3 = 2
            goto L_0x09cb
        L_0x0541:
            boolean r6 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r6 == 0) goto L_0x0575
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x0563
            java.lang.String r3 = r3.first_name
            if (r3 == 0) goto L_0x0563
            r3 = 2131690056(0x7f0f0248, float:1.9009145E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r1.user
            java.lang.String r5 = r5.first_name
            r7 = 0
            r6[r7] = r5
            java.lang.String r5 = "AwaitingEncryption"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r6)
            r3 = 2
            goto L_0x09cb
        L_0x0563:
            r7 = 0
            r3 = 2131690056(0x7f0f0248, float:1.9009145E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r8[r7] = r5
            java.lang.String r5 = "AwaitingEncryption"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r8)
            r3 = 2
            goto L_0x09cb
        L_0x0575:
            boolean r6 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r6 == 0) goto L_0x0585
            r3 = 2131691116(0x7f0f066c, float:1.9011295E38)
            java.lang.String r5 = "EncryptionRejected"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r3 = 2
            goto L_0x09cb
        L_0x0585:
            boolean r6 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChat
            if (r6 == 0) goto L_0x05d3
            int r3 = r3.admin_id
            int r6 = r1.currentAccount
            im.bclpbkiauv.messenger.UserConfig r6 = im.bclpbkiauv.messenger.UserConfig.getInstance(r6)
            int r6 = r6.getClientUserId()
            if (r3 != r6) goto L_0x05c7
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x05b5
            java.lang.String r3 = r3.first_name
            if (r3 == 0) goto L_0x05b5
            r3 = 2131691104(0x7f0f0660, float:1.901127E38)
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r1.user
            java.lang.String r5 = r5.first_name
            r7 = 0
            r6[r7] = r5
            java.lang.String r5 = "EncryptedChatStartedOutgoing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r6)
            r3 = 2
            goto L_0x09cb
        L_0x05b5:
            r7 = 0
            r3 = 2131691104(0x7f0f0660, float:1.901127E38)
            r6 = 1
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r8[r7] = r5
            java.lang.String r5 = "EncryptedChatStartedOutgoing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r8)
            r3 = 2
            goto L_0x09cb
        L_0x05c7:
            r3 = 2131691103(0x7f0f065f, float:1.9011268E38)
            java.lang.String r5 = "EncryptedChatStartedIncoming"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r3 = 2
            goto L_0x09cb
        L_0x05d3:
            r3 = 2
            goto L_0x09cb
        L_0x05d6:
            java.lang.String r9 = ""
            r3 = 2
            goto L_0x09cb
        L_0x05db:
            r6 = 0
            r7 = 0
            boolean r3 = r3.isFromUser()
            if (r3 == 0) goto L_0x05f8
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.from_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r6 = r3.getUser(r8)
            goto L_0x060e
        L_0x05f8:
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r8 = r8.to_id
            int r8 = r8.channel_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r7 = r3.getChat(r8)
        L_0x060e:
            int r3 = r1.dialogsType
            r8 = 3
            if (r3 != r8) goto L_0x062f
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x062f
            r3 = 2131693694(0x7f0f107e, float:1.9016524E38)
            java.lang.String r5 = "SavedMessagesInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r5 = 0
            r15 = 0
            r9 = r3
            r13 = r5
            r31 = r6
            r32 = r7
            r3 = 2
            goto L_0x09c3
        L_0x062f:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0648
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x0648
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x0648
            r12 = 0
            java.lang.CharSequence r3 = r43.formatArchivedDialogNames()
            r9 = r3
            r31 = r6
            r32 = r7
            r3 = 2
            goto L_0x09c3
        L_0x0648:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService
            if (r3 == 0) goto L_0x067e
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0670
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r3 != 0) goto L_0x066c
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r3 == 0) goto L_0x0670
        L_0x066c:
            java.lang.String r3 = ""
            r13 = 0
            goto L_0x0674
        L_0x0670:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
        L_0x0674:
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r9 = r3
            r31 = r6
            r32 = r7
            r3 = 2
            goto L_0x09c3
        L_0x067e:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x08b5
            int r3 = r3.id
            if (r3 <= 0) goto L_0x08b5
            if (r7 != 0) goto L_0x08b5
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x069b
            r3 = 2131691435(0x7f0f07ab, float:1.9011942E38)
            java.lang.String r8 = "FromYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r3 = r0
            goto L_0x06e2
        L_0x069b:
            if (r6 == 0) goto L_0x06d3
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x06b2
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x06a6
            goto L_0x06b2
        L_0x06a6:
            java.lang.String r3 = im.bclpbkiauv.messenger.UserObject.getFirstName(r6)
            java.lang.String r8 = "\n"
            java.lang.String r0 = r3.replace(r8, r5)
            r3 = r0
            goto L_0x06e2
        L_0x06b2:
            boolean r3 = im.bclpbkiauv.messenger.UserObject.isDeleted(r6)
            if (r3 == 0) goto L_0x06c3
            r3 = 2131691577(0x7f0f0839, float:1.901223E38)
            java.lang.String r8 = "HiddenName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r3)
            r3 = r0
            goto L_0x06e2
        L_0x06c3:
            java.lang.String r3 = r6.first_name
            java.lang.String r8 = r6.last_name
            java.lang.String r3 = im.bclpbkiauv.messenger.ContactsController.formatName(r3, r8)
            java.lang.String r8 = "\n"
            java.lang.String r0 = r3.replace(r8, r5)
            r3 = r0
            goto L_0x06e2
        L_0x06d3:
            if (r7 == 0) goto L_0x06df
            java.lang.String r3 = r7.title
            java.lang.String r8 = "\n"
            java.lang.String r0 = r3.replace(r8, r5)
            r3 = r0
            goto L_0x06e2
        L_0x06df:
            java.lang.String r0 = "DELETED"
            r3 = r0
        L_0x06e2:
            r12 = 0
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            if (r0 == 0) goto L_0x075d
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            java.lang.String r0 = r0.toString()
            int r5 = r0.length()
            r8 = 150(0x96, float:2.1E-43)
            if (r5 <= r8) goto L_0x06fe
            r5 = 0
            java.lang.String r0 = r0.substring(r5, r8)
        L_0x06fe:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVideo()
            if (r5 == 0) goto L_0x0709
            java.lang.String r5 = "📹 "
            goto L_0x072c
        L_0x0709:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isVoice()
            if (r5 == 0) goto L_0x0714
            java.lang.String r5 = "🎤 "
            goto L_0x072c
        L_0x0714:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isMusic()
            if (r5 == 0) goto L_0x071f
            java.lang.String r5 = "🎧 "
            goto L_0x072c
        L_0x071f:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isPhoto()
            if (r5 == 0) goto L_0x072a
            java.lang.String r5 = "🖼 "
            goto L_0x072c
        L_0x072a:
            java.lang.String r5 = "📎 "
        L_0x072c:
            r8 = 2
            java.lang.Object[] r14 = new java.lang.Object[r8]
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            r19 = r5
            r31 = r6
            r32 = r7
            r5 = 32
            r6 = 10
            java.lang.String r7 = r0.replace(r6, r5)
            r8.append(r7)
            java.lang.String r5 = r8.toString()
            r6 = 0
            r14[r6] = r5
            r5 = 1
            r14[r5] = r3
            java.lang.String r5 = java.lang.String.format(r2, r14)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r5)
            r6 = r0
            goto L_0x0873
        L_0x075d:
            r31 = r6
            r32 = r7
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0837
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x0837
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x07b5
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r0 < r5) goto L_0x079c
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r7 = 0
            r0[r7] = r6
            java.lang.String r6 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            r5 = r0
            r8 = 1
            goto L_0x0803
        L_0x079c:
            r5 = 1
            r7 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r0[r7] = r5
            java.lang.String r5 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r5, r0)
            r5 = r0
            r8 = 1
            goto L_0x0803
        L_0x07b5:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            int r0 = r0.type
            r5 = 14
            if (r0 != r5) goto L_0x07fd
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 18
            if (r0 < r5) goto L_0x07e0
            r5 = 2
            java.lang.Object[] r0 = new java.lang.Object[r5]
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            java.lang.String r6 = r6.getMusicAuthor()
            r7 = 0
            r0[r7] = r6
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            java.lang.String r6 = r6.getMusicTitle()
            r8 = 1
            r0[r8] = r6
            java.lang.String r6 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            r5 = r0
            goto L_0x0803
        L_0x07e0:
            r5 = 2
            r7 = 0
            r8 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicAuthor()
            r0[r7] = r5
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicTitle()
            r0[r8] = r5
            java.lang.String r5 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r5, r0)
            r5 = r0
            goto L_0x0803
        L_0x07fd:
            r8 = 1
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
            r5 = r0
        L_0x0803:
            r6 = 2
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r6 = 0
            r0[r6] = r5
            r0[r8] = r3
            java.lang.String r0 = java.lang.String.format(r2, r0)
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0832 }
            java.lang.String r7 = "chats_attachMessage"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)     // Catch:{ Exception -> 0x0832 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0832 }
            if (r18 == 0) goto L_0x0827
            int r7 = r3.length()     // Catch:{ Exception -> 0x0832 }
            r8 = 2
            int r7 = r7 + r8
            goto L_0x0828
        L_0x0827:
            r7 = 0
        L_0x0828:
            int r8 = r6.length()     // Catch:{ Exception -> 0x0832 }
            r14 = 33
            r6.setSpan(r0, r7, r8, r14)     // Catch:{ Exception -> 0x0832 }
            goto L_0x0836
        L_0x0832:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0836:
            goto L_0x0873
        L_0x0837:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x086e
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0853
            r5 = 0
            java.lang.String r0 = r0.substring(r5, r6)
            goto L_0x0854
        L_0x0853:
            r5 = 0
        L_0x0854:
            r6 = 2
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r6 = 32
            r8 = 10
            java.lang.String r14 = r0.replace(r8, r6)
            r7[r5] = r14
            r5 = 1
            r7[r5] = r3
            java.lang.String r5 = java.lang.String.format(r2, r7)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r5)
            r6 = r0
            goto L_0x0873
        L_0x086e:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r5)
            r6 = r0
        L_0x0873:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x087b
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0885
        L_0x087b:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x08a1
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x08a1
        L_0x0885:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x089d }
            java.lang.String r5 = "chats_nameMessage"
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r5)     // Catch:{ Exception -> 0x089d }
            r0.<init>(r5)     // Catch:{ Exception -> 0x089d }
            int r5 = r3.length()     // Catch:{ Exception -> 0x089d }
            r7 = 1
            int r5 = r5 + r7
            r7 = 33
            r8 = 0
            r6.setSpan(r0, r8, r5, r7)     // Catch:{ Exception -> 0x089d }
            goto L_0x08a1
        L_0x089d:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08a1:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            r7 = 0
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r6, r0, r5, r7)
            r9 = r0
            r0 = r3
            r3 = 2
            goto L_0x09c3
        L_0x08b5:
            r31 = r6
            r32 = r7
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x08e6
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r3 = r3.photo
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoEmpty
            if (r3 == 0) goto L_0x08e6
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x08e6
            r3 = 2131689958(0x7f0f01e6, float:1.9008946E38)
            java.lang.String r5 = "AttachPhotoExpired"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r9 = r3
            r3 = 2
            goto L_0x09c3
        L_0x08e6:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r3 == 0) goto L_0x0913
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEmpty
            if (r3 == 0) goto L_0x0913
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x0913
            r3 = 2131689964(0x7f0f01ec, float:1.9008958E38)
            java.lang.String r5 = "AttachVideoExpired"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r5, r3)
            r9 = r3
            r3 = 2
            goto L_0x09c3
        L_0x0913:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.caption
            if (r3 == 0) goto L_0x095d
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x0924
            java.lang.String r3 = "📹 "
            goto L_0x0947
        L_0x0924:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x092f
            java.lang.String r3 = "🎤 "
            goto L_0x0947
        L_0x092f:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x093a
            java.lang.String r3 = "🎧 "
            goto L_0x0947
        L_0x093a:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x0945
            java.lang.String r3 = "🖼 "
            goto L_0x0947
        L_0x0945:
            java.lang.String r3 = "📎 "
        L_0x0947:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            java.lang.CharSequence r6 = r6.caption
            r5.append(r6)
            java.lang.String r3 = r5.toString()
            r9 = r3
            r3 = 2
            goto L_0x09c3
        L_0x095d:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0985
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "🎮 "
            r3.append(r5)
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r5 = r3
            r3 = 2
            goto L_0x09ae
        L_0x0985:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            int r3 = r3.type
            r5 = 14
            if (r3 != r5) goto L_0x09a9
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            java.lang.String r6 = r6.getMusicAuthor()
            r7 = 0
            r5[r7] = r6
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            java.lang.String r6 = r6.getMusicTitle()
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "🎧 %s - %s"
            java.lang.String r5 = java.lang.String.format(r6, r5)
            goto L_0x09ae
        L_0x09a9:
            r3 = 2
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.CharSequence r5 = r5.messageText
        L_0x09ae:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r6 = r6.media
            if (r6 == 0) goto L_0x09c2
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isMediaEmpty()
            if (r6 != 0) goto L_0x09c2
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r9 = r5
            goto L_0x09c3
        L_0x09c2:
            r9 = r5
        L_0x09c3:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x09cb
            java.lang.CharSequence r0 = r43.formatArchivedDialogNames()
        L_0x09cb:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x09d7
            int r5 = r5.date
            long r5 = (long) r5
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x09f1
        L_0x09d7:
            int r5 = r1.lastMessageDate
            if (r5 == 0) goto L_0x09e1
            long r5 = (long) r5
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x09f1
        L_0x09e1:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            if (r5 == 0) goto L_0x09ef
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x09f1
        L_0x09ef:
            r6 = r27
        L_0x09f1:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            if (r5 != 0) goto L_0x0a08
            r4 = 0
            r1.drawCheck1 = r4
            r1.drawCheck2 = r4
            r1.drawClockIcon = r4
            r1.drawCount = r4
            r1.drawMentionIcon = r4
            r1.drawErrorIcon = r4
            r7 = r28
            r8 = r30
            goto L_0x0b19
        L_0x0a08:
            int r7 = r1.currentDialogFolderId
            if (r7 == 0) goto L_0x0a4d
            int r5 = r1.unreadCount
            int r7 = r1.mentionCount
            int r8 = r5 + r7
            if (r8 <= 0) goto L_0x0a43
            if (r5 <= r7) goto L_0x0a2d
            r8 = 1
            r1.drawCount = r8
            r14 = 0
            r1.drawMentionIcon = r14
            java.lang.Object[] r3 = new java.lang.Object[r8]
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3[r14] = r5
            java.lang.String r7 = java.lang.String.format(r4, r3)
            r8 = r30
            goto L_0x0aa1
        L_0x0a2d:
            r8 = 1
            r14 = 0
            r1.drawCount = r14
            r1.drawMentionIcon = r8
            java.lang.Object[] r3 = new java.lang.Object[r8]
            int r5 = r5 + r7
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3[r14] = r5
            java.lang.String r8 = java.lang.String.format(r4, r3)
            r7 = r28
            goto L_0x0aa1
        L_0x0a43:
            r14 = 0
            r1.drawCount = r14
            r1.drawMentionIcon = r14
            r7 = r28
            r8 = r30
            goto L_0x0aa1
        L_0x0a4d:
            r14 = 0
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0a5b
            r1.drawCount = r14
            r3 = 0
            r13 = r3
            r7 = r28
            r3 = 1
            r4 = 0
            goto L_0x0a94
        L_0x0a5b:
            int r3 = r1.unreadCount
            if (r3 == 0) goto L_0x0a83
            r7 = 1
            if (r3 != r7) goto L_0x0a6e
            int r7 = r1.mentionCount
            if (r3 != r7) goto L_0x0a6e
            if (r5 == 0) goto L_0x0a6e
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r5.messageOwner
            boolean r3 = r3.mentioned
            if (r3 != 0) goto L_0x0a83
        L_0x0a6e:
            r3 = 1
            r1.drawCount = r3
            java.lang.Object[] r5 = new java.lang.Object[r3]
            int r3 = r1.unreadCount
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r7 = 0
            r5[r7] = r3
            java.lang.String r7 = java.lang.String.format(r4, r5)
            r3 = 1
            r4 = 0
            goto L_0x0a94
        L_0x0a83:
            boolean r3 = r1.markUnread
            if (r3 == 0) goto L_0x0a8e
            r3 = 1
            r1.drawCount = r3
            java.lang.String r7 = ""
            r4 = 0
            goto L_0x0a94
        L_0x0a8e:
            r3 = 1
            r4 = 0
            r1.drawCount = r4
            r7 = r28
        L_0x0a94:
            int r5 = r1.mentionCount
            if (r5 == 0) goto L_0x0a9d
            r1.drawMentionIcon = r3
            java.lang.String r8 = "@"
            goto L_0x0aa1
        L_0x0a9d:
            r1.drawMentionIcon = r4
            r8 = r30
        L_0x0aa1:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOut()
            if (r3 == 0) goto L_0x0b10
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            if (r3 != 0) goto L_0x0b10
            if (r13 == 0) goto L_0x0b10
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r3 != 0) goto L_0x0b10
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isSending()
            if (r3 == 0) goto L_0x0acc
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r4 = 1
            r1.drawClockIcon = r4
            r1.drawErrorIcon = r3
            goto L_0x0b19
        L_0x0acc:
            r3 = 0
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            boolean r4 = r4.isSendError()
            if (r4 == 0) goto L_0x0ae3
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClockIcon = r3
            r4 = 1
            r1.drawErrorIcon = r4
            r1.drawCount = r3
            r1.drawMentionIcon = r3
            goto L_0x0b19
        L_0x0ae3:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isSent()
            if (r3 == 0) goto L_0x0b19
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isUnread()
            if (r3 == 0) goto L_0x0b04
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0b02
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0b02
            goto L_0x0b04
        L_0x0b02:
            r3 = 0
            goto L_0x0b05
        L_0x0b04:
            r3 = 1
        L_0x0b05:
            r1.drawCheck1 = r3
            r3 = 1
            r1.drawCheck2 = r3
            r3 = 0
            r1.drawClockIcon = r3
            r1.drawErrorIcon = r3
            goto L_0x0b19
        L_0x0b10:
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClockIcon = r3
            r1.drawErrorIcon = r3
        L_0x0b19:
            int r3 = r1.dialogsType
            if (r3 != 0) goto L_0x0b39
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            long r4 = r1.currentDialogId
            r14 = 1
            boolean r3 = r3.isProxyDialog(r4, r14)
            if (r3 == 0) goto L_0x0b39
            r1.drawPinBackground = r14
            r3 = 2131694551(0x7f0f13d7, float:1.9018262E38)
            java.lang.String r4 = "UseProxySponsor"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r5 = r3
            goto L_0x0b3a
        L_0x0b39:
            r5 = r6
        L_0x0b3a:
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x0b49
            r3 = 2131689868(0x7f0f018c, float:1.9008764E38)
            java.lang.String r4 = "ArchivedChats"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r3 = r0
            goto L_0x0b81
        L_0x0b49:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0b50
            java.lang.String r3 = r3.title
            goto L_0x0b6e
        L_0x0b50:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x0b6c
            boolean r3 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x0b65
            r3 = 2131693693(0x7f0f107d, float:1.9016522E38)
            java.lang.String r4 = "SavedMessages"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            goto L_0x0b6e
        L_0x0b65:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            java.lang.String r3 = im.bclpbkiauv.messenger.UserObject.getName(r3)
            goto L_0x0b6e
        L_0x0b6c:
            r3 = r26
        L_0x0b6e:
            int r4 = r3.length()
            if (r4 != 0) goto L_0x0b7f
            r4 = 2131691577(0x7f0f0839, float:1.901223E38)
            java.lang.String r6 = "HiddenName"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r4)
            r3 = r0
            goto L_0x0b81
        L_0x0b7f:
            r4 = r3
            r3 = r0
        L_0x0b81:
            if (r15 == 0) goto L_0x0bc4
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r5)
            r6 = r13
            double r13 = (double) r0
            double r13 = java.lang.Math.ceil(r13)
            int r0 = (int) r13
            android.text.StaticLayout r13 = new android.text.StaticLayout
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r13
            r27 = r5
            r29 = r0
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.timeLayout = r13
            boolean r13 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r13 != 0) goto L_0x0bba
            int r13 = r43.getMeasuredWidth()
            r14 = 1097859072(0x41700000, float:15.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 - r14
            int r13 = r13 - r0
            r1.timeLeft = r13
            goto L_0x0bc2
        L_0x0bba:
            r13 = 1097859072(0x41700000, float:15.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            r1.timeLeft = r13
        L_0x0bc2:
            r13 = r0
            goto L_0x0bcd
        L_0x0bc4:
            r6 = r13
            r0 = 0
            r13 = 0
            r1.timeLayout = r13
            r13 = 0
            r1.timeLeft = r13
            r13 = r0
        L_0x0bcd:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0be2
            int r0 = r43.getMeasuredWidth()
            int r14 = r1.nameLeft
            int r0 = r0 - r14
            r14 = 1096810496(0x41600000, float:14.0)
            int r19 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r19
            int r0 = r0 - r13
            goto L_0x0bf6
        L_0x0be2:
            int r0 = r43.getMeasuredWidth()
            int r14 = r1.nameLeft
            int r0 = r0 - r14
            r14 = 1117388800(0x429a0000, float:77.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r14
            int r0 = r0 - r13
            int r14 = r1.nameLeft
            int r14 = r14 + r13
            r1.nameLeft = r14
        L_0x0bf6:
            boolean r14 = r1.dialogMuted
            r19 = 1086324736(0x40c00000, float:6.0)
            if (r14 == 0) goto L_0x0c22
            boolean r14 = r1.drawVerifiedIcon
            if (r14 != 0) goto L_0x0c22
            boolean r14 = r1.drawScam
            if (r14 != 0) goto L_0x0c22
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r22 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r22 = r22.getIntrinsicWidth()
            int r14 = r14 + r22
            int r0 = r0 - r14
            boolean r22 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r22 == 0) goto L_0x0c1d
            r22 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r14
            r1.nameLeft = r0
            goto L_0x0c1f
        L_0x0c1d:
            r22 = r0
        L_0x0c1f:
            r0 = r22
            goto L_0x0c65
        L_0x0c22:
            boolean r14 = r1.drawVerifiedIcon
            if (r14 == 0) goto L_0x0c44
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            android.graphics.drawable.Drawable r22 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r22 = r22.getIntrinsicWidth()
            int r14 = r14 + r22
            int r0 = r0 - r14
            boolean r22 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r22 == 0) goto L_0x0c3f
            r22 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r14
            r1.nameLeft = r0
            goto L_0x0c41
        L_0x0c3f:
            r22 = r0
        L_0x0c41:
            r0 = r22
            goto L_0x0c65
        L_0x0c44:
            boolean r14 = r1.drawScam
            if (r14 == 0) goto L_0x0c65
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            im.bclpbkiauv.ui.components.ScamDrawable r22 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r22 = r22.getIntrinsicWidth()
            int r14 = r14 + r22
            int r0 = r0 - r14
            boolean r22 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r22 == 0) goto L_0x0c61
            r22 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r14
            r1.nameLeft = r0
            goto L_0x0c63
        L_0x0c61:
            r22 = r0
        L_0x0c63:
            r0 = r22
        L_0x0c65:
            r22 = r2
            r14 = 1094713344(0x41400000, float:12.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r2 = java.lang.Math.max(r2, r0)
            r40 = r5
            r5 = 10
            r14 = 32
            java.lang.String r0 = r4.replace(r5, r14)     // Catch:{ Exception -> 0x0ca6 }
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0ca6 }
            r14 = 1094713344(0x41400000, float:12.0)
            int r26 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)     // Catch:{ Exception -> 0x0ca6 }
            int r14 = r2 - r26
            float r14 = (float) r14
            r41 = r4
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0ca4 }
            java.lang.CharSequence r27 = android.text.TextUtils.ellipsize(r0, r5, r14, r4)     // Catch:{ Exception -> 0x0ca4 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0ca4 }
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0ca4 }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0ca4 }
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r29 = r2
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x0ca4 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x0ca4 }
            goto L_0x0cac
        L_0x0ca4:
            r0 = move-exception
            goto L_0x0ca9
        L_0x0ca6:
            r0 = move-exception
            r41 = r4
        L_0x0ca9:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0cac:
            float r0 = r1.topOffset
            r4 = 0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x0cc4
            int r0 = r43.getMeasuredHeight()
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r5
            float r0 = (float) r0
            r5 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r5
            r1.topOffset = r0
        L_0x0cc4:
            boolean r0 = r1.useForceThreeLines
            r5 = 1111752704(0x42440000, float:49.0)
            r14 = 1106771968(0x41f80000, float:31.0)
            r34 = 1092616192(0x41200000, float:10.0)
            r26 = 1116733440(0x42900000, float:72.0)
            if (r0 != 0) goto L_0x0d65
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0cd8
            r42 = r6
            goto L_0x0d67
        L_0x0cd8:
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            float r0 = (float) r0
            float r4 = r1.topOffset
            float r0 = r0 + r4
            int r0 = (int) r0
            r1.messageNameTop = r0
            int r0 = (int) r4
            r1.timeTop = r0
            int r0 = r43.getMeasuredHeight()
            r4 = 1103626240(0x41c80000, float:25.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.errorTop = r0
            int r0 = r43.getMeasuredHeight()
            r4 = 1105199104(0x41e00000, float:28.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.recorderTop = r0
            int r0 = r43.getMeasuredHeight()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r4
            r1.countTop = r0
            int r0 = r43.getMeasuredHeight()
            r4 = 1103626240(0x41c80000, float:25.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.clockDrawTop = r0
            int r0 = r43.getMeasuredWidth()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            int r0 = r0 - r4
            boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0d36
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1094713344(0x41400000, float:12.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.avatarLeft = r14
            goto L_0x0d4b
        L_0x0d36:
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r24)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r43.getMeasuredWidth()
            r14 = 1115684864(0x42800000, float:64.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r4 = r4 - r14
            r1.avatarLeft = r4
        L_0x0d4b:
            im.bclpbkiauv.messenger.ImageReceiver r4 = r1.avatarImage
            int r14 = r1.avatarLeft
            r24 = r0
            float r0 = r1.topOffset
            int r0 = (int) r0
            r42 = r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r4.setImageCoords(r14, r0, r6, r5)
            r0 = r24
            goto L_0x0dec
        L_0x0d65:
            r42 = r6
        L_0x0d67:
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r0 = (float) r0
            float r4 = r1.topOffset
            float r0 = r0 + r4
            int r0 = (int) r0
            r1.messageNameTop = r0
            int r0 = (int) r4
            r1.timeTop = r0
            int r0 = r43.getMeasuredHeight()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r4
            r1.errorTop = r0
            r0 = 1105199104(0x41e00000, float:28.0)
            float r4 = r1.topOffset
            float r4 = r4 + r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.recorderTop = r0
            int r0 = r43.getMeasuredHeight()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r4
            r1.countTop = r0
            int r0 = r43.getMeasuredHeight()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r0 = r0 - r4
            r1.clockDrawTop = r0
            int r0 = r43.getMeasuredWidth()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            int r0 = r0 - r4
            boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0dbf
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.avatarLeft = r4
            goto L_0x0dd6
        L_0x0dbf:
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.messageNameLeft = r4
            r1.messageLeft = r4
            int r4 = r43.getMeasuredWidth()
            r6 = 1115947008(0x42840000, float:66.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r4 = r4 - r6
            r1.avatarLeft = r4
        L_0x0dd6:
            im.bclpbkiauv.messenger.ImageReceiver r4 = r1.avatarImage
            int r6 = r1.avatarLeft
            float r14 = r1.topOffset
            int r14 = (int) r14
            r24 = r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r4.setImageCoords(r6, r14, r0, r5)
            r0 = r24
        L_0x0dec:
            boolean r4 = r1.drawPinIcon
            if (r4 == 0) goto L_0x0e44
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r4 = r4.getIntrinsicWidth()
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r4 = r4 + r5
            int r0 = r0 - r4
            int r5 = r43.getMeasuredHeight()
            r6 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.pinTop = r5
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0e2f
            int r5 = r43.getMeasuredWidth()
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.recorderLeft = r5
            int r5 = r43.getMeasuredWidth()
            r6 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.pinLeft = r5
            goto L_0x0e44
        L_0x0e2f:
            r6 = 1096810496(0x41600000, float:14.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r1.recorderLeft = r5
            r5 = 0
            r1.pinLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r4
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r4
            r1.messageNameLeft = r5
        L_0x0e44:
            boolean r4 = r1.drawErrorIcon
            if (r4 == 0) goto L_0x0e6c
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r4
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0e58
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            r1.errorLeft = r5
            goto L_0x0e60
        L_0x0e58:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
        L_0x0e60:
            int r5 = r1.messageLeft
            int r5 = r5 + r4
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r4
            r1.messageNameLeft = r5
            r4 = r0
            goto L_0x0e95
        L_0x0e6c:
            boolean r4 = r1.drawClockIcon
            if (r4 == 0) goto L_0x0e94
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            int r0 = r0 - r4
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0e80
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r26)
            r1.clockDrawLeft = r5
            goto L_0x0e88
        L_0x0e80:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r1.clockDrawLeft = r5
        L_0x0e88:
            int r5 = r1.messageLeft
            int r5 = r5 + r4
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r4
            r1.messageNameLeft = r5
            r4 = r0
            goto L_0x0e95
        L_0x0e94:
            r4 = r0
        L_0x0e95:
            if (r7 != 0) goto L_0x0e99
            if (r8 == 0) goto L_0x0fcf
        L_0x0e99:
            if (r7 == 0) goto L_0x0f3b
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ Exception -> 0x0eb3 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0eb3 }
            java.math.BigDecimal r5 = new java.math.BigDecimal     // Catch:{ Exception -> 0x0eb3 }
            java.lang.String r6 = "10"
            r5.<init>(r6)     // Catch:{ Exception -> 0x0eb3 }
            int r0 = r0.compareTo(r5)     // Catch:{ Exception -> 0x0eb3 }
            if (r0 <= 0) goto L_0x0eaf
            r0 = 1
            goto L_0x0eb0
        L_0x0eaf:
            r0 = 0
        L_0x0eb0:
            r1.countIsBiggerThanTen = r0     // Catch:{ Exception -> 0x0eb3 }
            goto L_0x0ecc
        L_0x0eb3:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "FmtDialogCell ----> buildLayout countIsBiggerThanTen e: "
            r5.append(r6)
            java.lang.String r6 = r0.toString()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r5)
        L_0x0ecc:
            int r0 = r1.countTop
            boolean r5 = r1.countIsBiggerThanTen
            if (r5 == 0) goto L_0x0ed9
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            goto L_0x0eda
        L_0x0ed9:
            r5 = 0
        L_0x0eda:
            int r0 = r0 + r5
            r1.countTop = r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r7)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.countWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            int r5 = r1.countWidth
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_CENTER
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r27 = r7
            r29 = r5
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.countLayout = r0
            int r0 = r1.countWidth
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            int r0 = r0 + r5
            int r4 = r4 - r0
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0f27
            int r5 = r43.getMeasuredWidth()
            int r6 = r1.countWidth
            int r5 = r5 - r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            int r5 = r5 - r6
            r1.countLeft = r5
            goto L_0x0f37
        L_0x0f27:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            r1.countLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x0f37:
            r5 = 1
            r1.drawCount = r5
            goto L_0x0f3e
        L_0x0f3b:
            r5 = 0
            r1.countWidth = r5
        L_0x0f3e:
            if (r8 == 0) goto L_0x0fcf
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0f76
            r0 = 1092091904(0x41180000, float:9.5)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r5 = r5.measureText(r8)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r0 = java.lang.Math.max(r0, r5)
            r1.mentionWidth = r0
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            int r5 = r1.mentionWidth
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_CENTER
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r27 = r8
            r29 = r5
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.mentionLayout = r0
            goto L_0x0f7e
        L_0x0f76:
            r0 = 1092091904(0x41180000, float:9.5)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r1.mentionWidth = r0
        L_0x0f7e:
            int r0 = r1.mentionWidth
            if (r7 == 0) goto L_0x0f87
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            goto L_0x0f8b
        L_0x0f87:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
        L_0x0f8b:
            int r0 = r0 + r5
            int r4 = r4 - r0
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0fae
            int r5 = r43.getMeasuredWidth()
            int r6 = r1.mentionWidth
            int r5 = r5 - r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            int r5 = r5 - r6
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x0fa9
            r14 = 1104150528(0x41d00000, float:26.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r6 = r6 + r14
            goto L_0x0faa
        L_0x0fa9:
            r6 = 0
        L_0x0faa:
            int r5 = r5 - r6
            r1.mentionLeft = r5
            goto L_0x0fcc
        L_0x0fae:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x0fbe
            r14 = 1084227584(0x40a00000, float:5.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r6 = r6 + r14
            goto L_0x0fbf
        L_0x0fbe:
            r6 = 0
        L_0x0fbf:
            int r5 = r5 + r6
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x0fcc:
            r5 = 1
            r1.drawMentionIcon = r5
        L_0x0fcf:
            if (r12 == 0) goto L_0x1009
            if (r9 != 0) goto L_0x0fd5
            java.lang.String r9 = ""
        L_0x0fd5:
            java.lang.String r0 = r9.toString()
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0fe6
            r5 = 0
            java.lang.String r0 = r0.substring(r5, r6)
        L_0x0fe6:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0fee
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0ff0
        L_0x0fee:
            if (r3 == 0) goto L_0x0ff8
        L_0x0ff0:
            r5 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r5)
        L_0x0ff8:
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r14 = 0
            java.lang.CharSequence r9 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r0, r5, r6, r14)
        L_0x1009:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r4 = java.lang.Math.max(r0, r4)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x101b
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x1053
        L_0x101b:
            if (r3 == 0) goto L_0x1053
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x1026
            int r0 = r1.currentDialogFolderDialogsCount
            r5 = 1
            if (r0 != r5) goto L_0x1053
        L_0x1026:
            android.text.TextPaint r31 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x1041 }
            android.text.Layout$Alignment r33 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x1041 }
            r34 = 1065353216(0x3f800000, float:1.0)
            r35 = 0
            r36 = 0
            android.text.TextUtils$TruncateAt r37 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x1041 }
            r39 = 1
            r30 = r3
            r32 = r4
            r38 = r4
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r30, r31, r32, r33, r34, r35, r36, r37, r38, r39)     // Catch:{ Exception -> 0x1041 }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x1041 }
            goto L_0x1045
        L_0x1041:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1045:
            r0 = 1108344832(0x42100000, float:36.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
            goto L_0x107a
        L_0x1053:
            r5 = 0
            r1.messageNameLayout = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x106d
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x105f
            goto L_0x106d
        L_0x105f:
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
            goto L_0x107a
        L_0x106d:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
        L_0x107a:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x10f8 }
            if (r0 != 0) goto L_0x1085
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x10f8 }
            if (r0 == 0) goto L_0x1083
            goto L_0x1085
        L_0x1083:
            r5 = 1
            goto L_0x1095
        L_0x1085:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x10f8 }
            if (r0 == 0) goto L_0x1094
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x10f8 }
            r5 = 1
            if (r0 <= r5) goto L_0x1095
            r0 = r3
            r3 = 0
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x10f8 }
            r11 = r6
            goto L_0x10b0
        L_0x1094:
            r5 = 1
        L_0x1095:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x10f8 }
            if (r0 != 0) goto L_0x109d
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x10f8 }
            if (r0 == 0) goto L_0x109f
        L_0x109d:
            if (r3 == 0) goto L_0x10af
        L_0x109f:
            r6 = 1094713344(0x41400000, float:12.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x10f8 }
            int r0 = r4 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x10f8 }
            android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x10f8 }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r9, r11, r0, r6)     // Catch:{ Exception -> 0x10f8 }
            goto L_0x10b0
        L_0x10af:
            r0 = r9
        L_0x10b0:
            boolean r6 = r1.useForceThreeLines     // Catch:{ Exception -> 0x10f8 }
            if (r6 != 0) goto L_0x10d1
            boolean r6 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x10f8 }
            if (r6 == 0) goto L_0x10b9
            goto L_0x10d1
        L_0x10b9:
            android.text.StaticLayout r5 = new android.text.StaticLayout     // Catch:{ Exception -> 0x10f8 }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x10f8 }
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r5
            r27 = r0
            r28 = r11
            r29 = r4
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x10f8 }
            r1.messageLayout = r5     // Catch:{ Exception -> 0x10f8 }
            goto L_0x10f7
        L_0x10d1:
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x10f8 }
            r30 = 1065353216(0x3f800000, float:1.0)
            r6 = 1065353216(0x3f800000, float:1.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x10f8 }
            float r6 = (float) r6     // Catch:{ Exception -> 0x10f8 }
            r32 = 0
            android.text.TextUtils$TruncateAt r33 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x10f8 }
            if (r3 == 0) goto L_0x10e5
            r35 = 1
            goto L_0x10e7
        L_0x10e5:
            r35 = 2
        L_0x10e7:
            r26 = r0
            r27 = r11
            r28 = r4
            r31 = r6
            r34 = r4
            android.text.StaticLayout r5 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r26, r27, r28, r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x10f8 }
            r1.messageLayout = r5     // Catch:{ Exception -> 0x10f8 }
        L_0x10f7:
            goto L_0x10fc
        L_0x10f8:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x10fc:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x121b
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x1199
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1199
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            float r0 = r0.getLineLeft(r5)
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r5)
            double r5 = (double) r6
            double r5 = java.lang.Math.ceil(r5)
            boolean r14 = r1.dialogMuted
            if (r14 == 0) goto L_0x1147
            boolean r14 = r1.drawVerifiedIcon
            if (r14 != 0) goto L_0x1147
            boolean r14 = r1.drawScam
            if (r14 != 0) goto L_0x1147
            int r14 = r1.nameLeft
            r16 = r7
            r17 = r8
            double r7 = (double) r14
            r20 = r9
            r14 = r10
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r9
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x1185
        L_0x1147:
            r16 = r7
            r17 = r8
            r20 = r9
            r14 = r10
            boolean r7 = r1.drawVerifiedIcon
            if (r7 == 0) goto L_0x116a
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r9
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x1185
        L_0x116a:
            boolean r7 = r1.drawScam
            if (r7 == 0) goto L_0x1185
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            double r9 = (double) r9
            double r7 = r7 - r9
            im.bclpbkiauv.ui.components.ScamDrawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
        L_0x1185:
            r7 = 0
            int r8 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r8 != 0) goto L_0x11a0
            double r7 = (double) r2
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x11a0
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r7 = (int) r7
            r1.nameLeft = r7
            goto L_0x11a0
        L_0x1199:
            r16 = r7
            r17 = r8
            r20 = r9
            r14 = r10
        L_0x11a0:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x11e8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x11e5
            r5 = 2147483647(0x7fffffff, float:NaN)
            r6 = 0
        L_0x11ae:
            if (r6 >= r0) goto L_0x11d8
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r6)
            r8 = 0
            int r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r9 != 0) goto L_0x11d4
            android.text.StaticLayout r8 = r1.messageLayout
            float r8 = r8.getLineWidth(r6)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            r21 = r11
            double r10 = (double) r4
            double r10 = r10 - r8
            int r10 = (int) r10
            int r5 = java.lang.Math.min(r5, r10)
            int r6 = r6 + 1
            r11 = r21
            goto L_0x11ae
        L_0x11d4:
            r21 = r11
            r5 = 0
            goto L_0x11da
        L_0x11d8:
            r21 = r11
        L_0x11da:
            r6 = 2147483647(0x7fffffff, float:NaN)
            if (r5 == r6) goto L_0x11ea
            int r6 = r1.messageLeft
            int r6 = r6 + r5
            r1.messageLeft = r6
            goto L_0x11ea
        L_0x11e5:
            r21 = r11
            goto L_0x11ea
        L_0x11e8:
            r21 = r11
        L_0x11ea:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x12a7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12a7
            android.text.StaticLayout r0 = r1.messageNameLayout
            r5 = 0
            float r0 = r0.getLineLeft(r5)
            r6 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x12a7
            android.text.StaticLayout r6 = r1.messageNameLayout
            float r5 = r6.getLineWidth(r5)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r4
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x12a7
            int r7 = r1.messageNameLeft
            double r7 = (double) r7
            double r9 = (double) r4
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r7 = (int) r7
            r1.messageNameLeft = r7
            goto L_0x12a7
        L_0x121b:
            r16 = r7
            r17 = r8
            r20 = r9
            r14 = r10
            r21 = r11
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x126c
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x126c
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            float r0 = r0.getLineRight(r5)
            float r6 = (float) r2
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x1253
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r5)
            double r5 = (double) r6
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r2
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x1253
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameLeft = r7
        L_0x1253:
            boolean r5 = r1.dialogMuted
            if (r5 != 0) goto L_0x125f
            boolean r5 = r1.drawVerifiedIcon
            if (r5 != 0) goto L_0x125f
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x126c
        L_0x125f:
            int r5 = r1.nameLeft
            float r5 = (float) r5
            float r5 = r5 + r0
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r6 = (float) r6
            float r5 = r5 + r6
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x126c:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x128f
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x128f
            r5 = 1325400064(0x4f000000, float:2.14748365E9)
            r6 = 0
        L_0x1279:
            if (r6 >= r0) goto L_0x1288
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r6)
            float r5 = java.lang.Math.min(r5, r7)
            int r6 = r6 + 1
            goto L_0x1279
        L_0x1288:
            int r6 = r1.messageLeft
            float r6 = (float) r6
            float r6 = r6 - r5
            int r6 = (int) r6
            r1.messageLeft = r6
        L_0x128f:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x12a7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12a7
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r5 = r1.messageNameLayout
            r6 = 0
            float r5 = r5.getLineLeft(r6)
            float r0 = r0 - r5
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x12a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.DialogCell.buildLayout():void");
    }

    public boolean isPointInsideAvatar(float x, float y) {
        if (!LocaleController.isRTL) {
            if (x < 0.0f || x >= ((float) AndroidUtilities.dp(60.0f))) {
                return false;
            }
            return true;
        } else if (x < ((float) (getMeasuredWidth() - AndroidUtilities.dp(60.0f))) || x >= ((float) getMeasuredWidth())) {
            return false;
        } else {
            return true;
        }
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    public void checkCurrentDialogIndex(boolean frozen) {
        MessageObject newMessageObject;
        MessageObject messageObject;
        MessageObject messageObject2;
        ArrayList<TLRPC.Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, frozen);
        if (this.index < dialogsArray.size()) {
            TLRPC.Dialog dialog = dialogsArray.get(this.index);
            boolean z = true;
            TLRPC.Dialog nextDialog = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
            TLRPC.DraftMessage newDraftMessage = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            if (this.currentDialogFolderId != 0) {
                newMessageObject = findFolderTopMessage();
            } else {
                newMessageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
            }
            if (this.currentDialogId != dialog.id || (((messageObject = this.message) != null && messageObject.getId() != dialog.top_message) || ((newMessageObject != null && newMessageObject.messageOwner.edit_date != this.currentEditDate) || this.unreadCount != dialog.unread_count || this.mentionCount != dialog.unread_mentions_count || this.markUnread != dialog.unread_mark || (messageObject2 = this.message) != newMessageObject || ((messageObject2 == null && newMessageObject != null) || newDraftMessage != this.draftMessage || this.drawPinIcon != dialog.pinned)))) {
                boolean dialogChanged = this.currentDialogId != dialog.id;
                this.currentDialogId = dialog.id;
                if (dialog instanceof TLRPC.TL_dialogFolder) {
                    this.currentDialogFolderId = ((TLRPC.TL_dialogFolder) dialog).folder.id;
                } else {
                    this.currentDialogFolderId = 0;
                }
                this.fullSeparator = (dialog instanceof TLRPC.TL_dialog) && dialog.pinned && nextDialog != null && !nextDialog.pinned;
                if (!(dialog instanceof TLRPC.TL_dialogFolder) || nextDialog == null || nextDialog.pinned) {
                    z = false;
                }
                this.fullSeparator2 = z;
                update(0);
                if (dialogChanged) {
                    this.reorderIconProgress = (!this.drawPinIcon || !this.drawReorder) ? 0.0f : 1.0f;
                }
                checkOnline();
            }
        }
    }

    public void animateArchiveAvatar() {
        if (this.avatarDrawable.getAvatarType() == 3) {
            this.animatingArchiveAvatar = true;
            this.animatingArchiveAvatarProgress = 0.0f;
            Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
            Theme.dialogs_archiveAvatarDrawable.start();
            invalidate();
        }
    }

    private MessageObject findFolderTopMessage() {
        ArrayList<TLRPC.Dialog> dialogs = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject maxMessage = null;
        if (!dialogs.isEmpty()) {
            int N = dialogs.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Dialog dialog = dialogs.get(a);
                MessageObject object = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (object != null && (maxMessage == null || object.messageOwner.date > maxMessage.messageOwner.date)) {
                    maxMessage = object;
                }
                if (dialog.pinnedNum == 0) {
                    break;
                }
            }
        }
        return maxMessage;
    }

    public void update(int mask) {
        long dialogId;
        TLRPC.Chat chat2;
        MessageObject messageObject;
        TLRPC.Dialog dialog;
        MessageObject messageObject2;
        CharSequence charSequence;
        int i = mask;
        CustomDialog customDialog2 = this.customDialog;
        boolean z = true;
        if (customDialog2 != null) {
            this.lastMessageDate = customDialog2.date;
            if (this.customDialog.unread_count == 0) {
                z = false;
            }
            this.lastUnreadState = z;
            this.unreadCount = this.customDialog.unread_count;
            this.drawPinIcon = this.customDialog.pinned;
            this.dialogMuted = this.customDialog.muted;
            this.avatarDrawable.setInfo(this.customDialog.id, this.customDialog.name, (String) null);
            this.avatarImage.setImage((String) null, "50_50", this.avatarDrawable, (String) null, 0);
        } else {
            if (this.isDialogCell) {
                TLRPC.Dialog dialog2 = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId);
                if (dialog2 == null) {
                    this.unreadCount = 0;
                    this.mentionCount = 0;
                    this.currentEditDate = 0;
                    this.lastMessageDate = 0;
                    this.clearingDialog = false;
                } else if (i == 0) {
                    this.clearingDialog = MessagesController.getInstance(this.currentAccount).isClearingDialog(dialog2.id);
                    MessageObject messageObject3 = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog2.id);
                    this.message = messageObject3;
                    this.lastUnreadState = messageObject3 != null && messageObject3.isUnread();
                    this.unreadCount = dialog2.unread_count;
                    this.markUnread = dialog2.unread_mark;
                    this.mentionCount = dialog2.unread_mentions_count;
                    MessageObject messageObject4 = this.message;
                    this.currentEditDate = messageObject4 != null ? messageObject4.messageOwner.edit_date : 0;
                    this.lastMessageDate = dialog2.last_message_date;
                    this.drawPinIcon = this.currentDialogFolderId == 0 && dialog2.pinned;
                    MessageObject messageObject5 = this.message;
                    if (messageObject5 != null) {
                        this.lastSendState = messageObject5.messageOwner.send_state;
                    }
                }
            } else {
                this.drawPinIcon = false;
            }
            if (i != 0) {
                boolean continueUpdate = false;
                if (!(this.user == null || (i & 4) == 0)) {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user.id));
                    invalidate();
                }
                if (this.isDialogCell && (i & 64) != 0) {
                    CharSequence printString = MessagesController.getInstance(this.currentAccount).printingStrings.get(this.currentDialogId);
                    if ((this.lastPrintString != null && printString == null) || ((this.lastPrintString == null && printString != null) || !((charSequence = this.lastPrintString) == null || printString == null || charSequence.equals(printString)))) {
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || (32768 & i) == 0 || (messageObject2 = this.message) == null || messageObject2.messageText == this.lastMessageString)) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (i & 2) != 0 && this.chat == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (i & 1) != 0 && this.chat == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (i & 8) != 0 && this.user == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (i & 16) != 0 && this.user == null) {
                    continueUpdate = true;
                }
                if (!continueUpdate && (i & 256) != 0) {
                    MessageObject messageObject6 = this.message;
                    if (messageObject6 != null && this.lastUnreadState != messageObject6.isUnread()) {
                        this.lastUnreadState = this.message.isUnread();
                        continueUpdate = true;
                    } else if (!(!this.isDialogCell || (dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.currentDialogId)) == null || (this.unreadCount == dialog.unread_count && this.markUnread == dialog.unread_mark && this.mentionCount == dialog.unread_mentions_count))) {
                        this.unreadCount = dialog.unread_count;
                        this.mentionCount = dialog.unread_mentions_count;
                        this.markUnread = dialog.unread_mark;
                        continueUpdate = true;
                    }
                }
                if (!(continueUpdate || (i & 4096) == 0 || (messageObject = this.message) == null || this.lastSendState == messageObject.messageOwner.send_state)) {
                    this.lastSendState = this.message.messageOwner.send_state;
                    continueUpdate = true;
                }
                if (!continueUpdate) {
                    invalidate();
                    return;
                }
            }
            this.user = null;
            this.chat = null;
            this.encryptedChat = null;
            if (this.currentDialogFolderId != 0) {
                this.dialogMuted = false;
                MessageObject findFolderTopMessage = findFolderTopMessage();
                this.message = findFolderTopMessage;
                if (findFolderTopMessage != null) {
                    dialogId = findFolderTopMessage.getDialogId();
                } else {
                    dialogId = 0;
                }
            } else {
                this.dialogMuted = this.isDialogCell && MessagesController.getInstance(this.currentAccount).isDialogMuted(this.currentDialogId);
                dialogId = this.currentDialogId;
            }
            if (dialogId != 0) {
                int lower_id = (int) dialogId;
                int high_id = (int) (dialogId >> 32);
                if (lower_id == 0) {
                    TLRPC.EncryptedChat encryptedChat2 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                    this.encryptedChat = encryptedChat2;
                    if (encryptedChat2 != null) {
                        this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.encryptedChat.user_id));
                    }
                } else if (lower_id < 0) {
                    TLRPC.Chat chat3 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    this.chat = chat3;
                    if (!(this.isDialogCell || chat3 == null || chat3.migrated_to == null || (chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat.migrated_to.channel_id))) == null)) {
                        this.chat = chat2;
                    }
                } else {
                    this.user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                }
            }
            if (this.currentDialogFolderId != 0) {
                Theme.dialogs_archiveAvatarDrawable.setCallback(this);
                this.avatarDrawable.setAvatarType(3);
                this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.user, 0);
            } else {
                TLRPC.User user2 = this.user;
                if (user2 != null) {
                    this.avatarDrawable.setInfo(user2);
                    if (UserObject.isUserSelf(this.user)) {
                        this.avatarDrawable.setAvatarType(1);
                        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (String) null, this.user, 0);
                    } else {
                        this.avatarImage.setImage(ImageLocation.getForUser(this.user, false), "50_50", this.avatarDrawable, (String) null, this.user, 0);
                    }
                } else {
                    TLRPC.Chat chat4 = this.chat;
                    if (chat4 != null) {
                        this.avatarDrawable.setInfo(chat4);
                        this.avatarImage.setImage(ImageLocation.getForChat(this.chat, false), "50_50", this.avatarDrawable, (String) null, this.chat, 0);
                    }
                }
            }
        }
        if (getMeasuredWidth() == 0 && getMeasuredHeight() == 0) {
            requestLayout();
        } else {
            buildLayout();
        }
        invalidate();
    }

    public void drawCheckBox(Canvas canvas) {
        if (this.checkBox == null) {
            return;
        }
        if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
            canvas.save();
            canvas.translate(0.0f, (float) getTop());
            this.checkBox.draw(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x03b4  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0429  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x04ad  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x04d5  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x053a  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x06c8  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x06ef  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x06f6  */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x071d  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x07ed  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x07ef  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0808  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x080b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0816  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0883  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x089b  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x08ba  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x08c1  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x090e  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0964  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x097b  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0994  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x09bc  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x09e8  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x09fe  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0a29  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x0a4d  */
    /* JADX WARNING: Removed duplicated region for block: B:385:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x027a  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0294  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r24) {
        /*
            r23 = this;
            r1 = r23
            r8 = r24
            long r2 = r1.currentDialogId
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            r0 = 0
            long r9 = android.os.SystemClock.uptimeMillis()
            long r2 = r1.lastUpdateTime
            long r2 = r9 - r2
            r4 = 17
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x0020
            r2 = 17
            r11 = r2
            goto L_0x0021
        L_0x0020:
            r11 = r2
        L_0x0021:
            r1.lastUpdateTime = r9
            float r2 = r1.clipProgress
            r13 = 24
            r14 = 0
            int r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x0050
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 == r13) goto L_0x0050
            r24.save()
            int r2 = r1.topClip
            float r2 = (float) r2
            float r3 = r1.clipProgress
            float r2 = r2 * r3
            int r3 = r23.getMeasuredWidth()
            float r3 = (float) r3
            int r4 = r23.getMeasuredHeight()
            int r5 = r1.bottomClip
            float r5 = (float) r5
            float r6 = r1.clipProgress
            float r5 = r5 * r6
            int r5 = (int) r5
            int r4 = r4 - r5
            float r4 = (float) r4
            r8.clipRect(r14, r2, r3, r4)
        L_0x0050:
            im.bclpbkiauv.ui.components.RLottieDrawable r2 = r1.translationDrawable
            r15 = 0
            if (r2 == 0) goto L_0x0067
            r2.stop()
            im.bclpbkiauv.ui.components.RLottieDrawable r2 = r1.translationDrawable
            r2.setProgress(r14)
            im.bclpbkiauv.ui.components.RLottieDrawable r2 = r1.translationDrawable
            r3 = 0
            r2.setCallback(r3)
            r1.translationDrawable = r3
            r1.translationAnimationStarted = r15
        L_0x0067:
            float r2 = r1.translationX
            int r2 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x0075
            r24.save()
            float r2 = r1.translationX
            r8.translate(r2, r14)
        L_0x0075:
            im.bclpbkiauv.ui.components.CheckBoxBase r2 = r1.checkBox
            r7 = 2
            r6 = 1065353216(0x3f800000, float:1.0)
            if (r2 == 0) goto L_0x0159
            boolean r2 = r1.checkBoxVisible
            if (r2 != 0) goto L_0x0084
            boolean r2 = r1.checkBoxAnimationInProgress
            if (r2 == 0) goto L_0x0159
        L_0x0084:
            r24.save()
            int r2 = r23.getTop()
            float r2 = (float) r2
            r8.translate(r14, r2)
            im.bclpbkiauv.ui.components.CheckBoxBase r2 = r1.checkBox
            r2.draw(r8)
            r24.restore()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            boolean r3 = r1.checkBoxVisible
            if (r3 == 0) goto L_0x00a7
            float r3 = r1.checkBoxAnimationProgress
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 == 0) goto L_0x00b1
        L_0x00a7:
            boolean r3 = r1.checkBoxVisible
            if (r3 != 0) goto L_0x00b3
            float r3 = r1.checkBoxAnimationProgress
            int r3 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x00b3
        L_0x00b1:
            r1.checkBoxAnimationInProgress = r15
        L_0x00b3:
            boolean r3 = r1.checkBoxVisible
            if (r3 == 0) goto L_0x00ba
            im.bclpbkiauv.ui.components.CubicBezierInterpolator r3 = im.bclpbkiauv.ui.components.CubicBezierInterpolator.EASE_OUT
            goto L_0x00bc
        L_0x00ba:
            im.bclpbkiauv.ui.components.CubicBezierInterpolator r3 = im.bclpbkiauv.ui.components.CubicBezierInterpolator.EASE_IN
        L_0x00bc:
            float r4 = r1.checkBoxAnimationProgress
            float r4 = r3.getInterpolation(r4)
            r5 = 1103626240(0x41c80000, float:25.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r4 = r4 * r5
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            r1.checkBoxTranslation = r4
            float r4 = (float) r4
            r1.setTranslationX(r4)
            r4 = -1041235968(0xffffffffc1f00000, float:-30.0)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r5 = r1.checkBoxTranslation
            int r4 = r4 + r5
            int r5 = r1.position
            if (r5 != r7) goto L_0x010e
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = "checkBoxAnimationProgress = "
            r5.append(r13)
            float r13 = r1.checkBoxAnimationProgress
            r5.append(r13)
            java.lang.String r13 = " , checkBoxTranslation = "
            r5.append(r13)
            int r13 = r1.checkBoxTranslation
            r5.append(r13)
            java.lang.String r13 = " , x = "
            r5.append(r13)
            r5.append(r4)
            java.lang.String r5 = r5.toString()
            java.lang.String r13 = "mmm"
            com.google.android.exoplayer2.util.Log.i(r13, r5)
        L_0x010e:
            im.bclpbkiauv.ui.components.CheckBoxBase r5 = r1.checkBox
            r13 = -1038090240(0xffffffffc2200000, float:-40.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r15 = r1.checkBoxTranslation
            int r13 = r13 + r15
            int r15 = r23.getMeasuredHeight()
            int r15 = r15 / r7
            int r16 = r2 / 2
            int r15 = r15 - r16
            r5.setBounds(r13, r15, r2, r2)
            boolean r5 = r1.checkBoxAnimationInProgress
            if (r5 == 0) goto L_0x0159
            long r7 = android.os.SystemClock.uptimeMillis()
            long r13 = r1.lastCheckBoxAnimationTime
            long r13 = r7 - r13
            r1.lastCheckBoxAnimationTime = r7
            boolean r5 = r1.checkBoxVisible
            r17 = 1128792064(0x43480000, float:200.0)
            if (r5 == 0) goto L_0x0149
            float r5 = r1.checkBoxAnimationProgress
            float r15 = (float) r13
            float r15 = r15 / r17
            float r5 = r5 + r15
            r1.checkBoxAnimationProgress = r5
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 <= 0) goto L_0x0147
            r1.checkBoxAnimationProgress = r6
        L_0x0147:
            r0 = 1
            goto L_0x0159
        L_0x0149:
            float r5 = r1.checkBoxAnimationProgress
            float r15 = (float) r11
            float r15 = r15 / r17
            float r5 = r5 - r15
            r1.checkBoxAnimationProgress = r5
            r15 = 0
            int r5 = (r5 > r15 ? 1 : (r5 == r15 ? 0 : -1))
            if (r5 > 0) goto L_0x0158
            r1.checkBoxAnimationProgress = r15
        L_0x0158:
            r0 = 1
        L_0x0159:
            int r2 = r1.currentDialogFolderId
            java.lang.String r8 = "chats_pinnedOverlay"
            if (r2 == 0) goto L_0x0199
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x0170
            float r2 = r1.archiveBackgroundProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x016c
            goto L_0x0170
        L_0x016c:
            r14 = 1065353216(0x3f800000, float:1.0)
            r15 = 2
            goto L_0x019c
        L_0x0170:
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            float r4 = r1.archiveBackgroundProgress
            r5 = 0
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.getOffsetColor(r5, r3, r4, r6)
            r2.setColor(r3)
            r3 = 0
            r4 = 0
            int r2 = r23.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r23.getMeasuredHeight()
            float r7 = (float) r2
            android.graphics.Paint r13 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r2 = r24
            r14 = 1065353216(0x3f800000, float:1.0)
            r6 = r7
            r15 = 2
            r7 = r13
            r2.drawRect(r3, r4, r5, r6, r7)
            goto L_0x019e
        L_0x0199:
            r14 = 1065353216(0x3f800000, float:1.0)
            r15 = 2
        L_0x019c:
            boolean r2 = r1.drawPinIcon
        L_0x019e:
            float r2 = r1.translationX
            java.lang.String r13 = "windowBackgroundWhite"
            r3 = 1090519040(0x41000000, float:8.0)
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x01b4
            float r2 = r1.cornerProgress
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x01b0
            goto L_0x01b4
        L_0x01b0:
            r15 = r24
            goto L_0x025d
        L_0x01b4:
            r24.save()
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r2.setColor(r5)
            android.graphics.RectF r2 = r1.rect
            int r5 = r23.getMeasuredWidth()
            r6 = 1115684864(0x42800000, float:64.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            float r5 = (float) r5
            int r6 = r23.getMeasuredWidth()
            float r6 = (float) r6
            int r7 = r23.getMeasuredHeight()
            float r7 = (float) r7
            r4 = 0
            r2.set(r5, r4, r6, r7)
            android.graphics.RectF r4 = r1.rect
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r6 = r1.cornerProgress
            float r5 = r5 * r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            float r7 = r1.cornerProgress
            float r6 = r6 * r7
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r15 = r24
            r15.drawRoundRect(r4, r5, r6, r7)
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x0230
            boolean r4 = im.bclpbkiauv.messenger.SharedConfig.archiveHidden
            if (r4 == 0) goto L_0x0206
            float r4 = r1.archiveBackgroundProgress
            r2 = 0
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0230
        L_0x0206:
            android.graphics.Paint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            float r6 = r1.archiveBackgroundProgress
            r7 = 0
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.getOffsetColor(r7, r5, r6, r14)
            r4.setColor(r5)
            android.graphics.RectF r4 = r1.rect
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r6 = r1.cornerProgress
            float r5 = r5 * r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            float r7 = r1.cornerProgress
            float r6 = r6 * r7
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r15.drawRoundRect(r4, r5, r6, r7)
            goto L_0x025a
        L_0x0230:
            boolean r4 = r1.drawPinIcon
            if (r4 != 0) goto L_0x0238
            boolean r4 = r1.drawPinBackground
            if (r4 == 0) goto L_0x025a
        L_0x0238:
            android.graphics.Paint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            r4.setColor(r5)
            android.graphics.RectF r4 = r1.rect
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r6 = r1.cornerProgress
            float r5 = r5 * r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r6 = (float) r6
            float r7 = r1.cornerProgress
            float r6 = r6 * r7
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r15.drawRoundRect(r4, r5, r6, r7)
        L_0x025a:
            r24.restore()
        L_0x025d:
            float r4 = r1.translationX
            r5 = 1125515264(0x43160000, float:150.0)
            r2 = 0
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x027a
            float r4 = r1.cornerProgress
            int r6 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r6 >= 0) goto L_0x028f
            float r6 = (float) r11
            float r6 = r6 / r5
            float r4 = r4 + r6
            r1.cornerProgress = r4
            int r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r4 <= 0) goto L_0x0277
            r1.cornerProgress = r14
        L_0x0277:
            r0 = 1
            r4 = r0
            goto L_0x0290
        L_0x027a:
            float r4 = r1.cornerProgress
            r2 = 0
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x028f
            float r6 = (float) r11
            float r6 = r6 / r5
            float r4 = r4 - r6
            r1.cornerProgress = r4
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x028c
            r1.cornerProgress = r2
        L_0x028c:
            r0 = 1
            r4 = r0
            goto L_0x0290
        L_0x028f:
            r4 = r0
        L_0x0290:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x02e8
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x02a8
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_nameArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x02d5
        L_0x02a8:
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 != 0) goto L_0x02c6
            im.bclpbkiauv.ui.cells.DialogCell$CustomDialog r0 = r1.customDialog
            if (r0 == 0) goto L_0x02b6
            int r0 = r0.type
            r6 = 2
            if (r0 != r6) goto L_0x02b6
            goto L_0x02c6
        L_0x02b6:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_name"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x02d5
        L_0x02c6:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_secretName"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x02d5:
            r24.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            float r6 = r1.topOffset
            r15.translate(r0, r6)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r15)
            r24.restore()
        L_0x02e8:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x0304
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x0304
            r24.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r6 = r1.timeTop
            float r6 = (float) r6
            r15.translate(r0, r6)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r15)
            r24.restore()
        L_0x0304:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x0358
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x031c
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_nameMessageArchived_threeLines"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x033f
        L_0x031c:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x0330
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_draft"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x033f
        L_0x0330:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_nameMessage_threeLines"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x033f:
            r24.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r6 = r1.messageNameTop
            float r6 = (float) r6
            r15.translate(r0, r6)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x0351 }
            r0.draw(r15)     // Catch:{ Exception -> 0x0351 }
            goto L_0x0355
        L_0x0351:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0355:
            r24.restore()
        L_0x0358:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x03ac
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0384
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0374
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_nameMessageArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x0393
        L_0x0374:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_messageArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x0393
        L_0x0384:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_message"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x0393:
            r24.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r6 = r1.messageTop
            float r6 = (float) r6
            r15.translate(r0, r6)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x03a5 }
            r0.draw(r15)     // Catch:{ Exception -> 0x03a5 }
            goto L_0x03a9
        L_0x03a5:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03a9:
            r24.restore()
        L_0x03ac:
            int r0 = r1.currentDialogFolderId
            r6 = 1080033280(0x40600000, float:3.5)
            r7 = 1095761920(0x41500000, float:13.0)
            if (r0 != 0) goto L_0x0419
            boolean r0 = r1.drawClockIcon
            if (r0 == 0) goto L_0x03ca
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            android.graphics.drawable.Drawable r8 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_clockDrawable
            int r2 = r1.clockDrawLeft
            int r5 = r1.clockDrawTop
            setDrawableBounds(r8, r2, r5, r0, r0)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_clockDrawable
            r2.draw(r15)
        L_0x03ca:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x0419
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0419
            boolean r0 = r1.drawCheck1
            r2 = 1096810496(0x41600000, float:14.0)
            r5 = 1093140480(0x41280000, float:10.5)
            if (r0 == 0) goto L_0x03fa
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_checkReadDrawable1
            int r8 = r23.getMeasuredWidth()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r8 = r8 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            setDrawableBounds(r0, r8, r2, r3, r5)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_checkReadDrawable1
            r0.draw(r15)
            goto L_0x0419
        L_0x03fa:
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_halfCheckDrawable1
            int r3 = r23.getMeasuredWidth()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            setDrawableBounds(r0, r3, r2, r8, r5)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_halfCheckDrawable1
            r0.draw(r15)
        L_0x0419:
            boolean r0 = r1.dialogMuted
            r2 = 1092616192(0x41200000, float:10.0)
            r3 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x044b
            boolean r0 = r1.drawVerifiedIcon
            if (r0 != 0) goto L_0x044b
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x044b
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r5 = r23.getMeasuredWidth()
            r8 = 1096286208(0x41580000, float:13.5)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 - r8
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            setDrawableBounds(r0, r5, r6, r8, r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            r0.draw(r15)
            goto L_0x0484
        L_0x044b:
            boolean r0 = r1.drawVerifiedIcon
            if (r0 == 0) goto L_0x0470
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r5 = r1.nameMuteLeft
            float r5 = (float) r5
            float r6 = r1.topOffset
            float r6 = r6 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (float) r5, (float) r6)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedCheckDrawable
            int r5 = r1.nameMuteLeft
            float r5 = (float) r5
            float r6 = r1.topOffset
            float r6 = r6 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (float) r5, (float) r6)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            r0.draw(r15)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r15)
            goto L_0x0484
        L_0x0470:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x0484
            im.bclpbkiauv.ui.components.ScamDrawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            float r5 = (float) r5
            float r6 = r1.topOffset
            float r6 = r6 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (float) r5, (float) r6)
            im.bclpbkiauv.ui.components.ScamDrawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r0.draw(r15)
        L_0x0484:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437f0000, float:255.0)
            if (r0 != 0) goto L_0x0491
            float r0 = r1.reorderIconProgress
            r6 = 0
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x04a9
        L_0x0491:
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            float r7 = r1.reorderIconProgress
            float r7 = r7 * r5
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            int r7 = r1.recorderLeft
            int r8 = r1.recorderTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r7, (int) r8)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            r0.draw(r15)
        L_0x04a9:
            boolean r0 = r1.drawPinIcon
            if (r0 == 0) goto L_0x04d1
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            float r7 = r1.reorderIconProgress
            float r7 = r14 - r7
            float r7 = r7 * r5
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r7 = r1.pinLeft
            int r8 = r1.pinTop
            r18 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            setDrawableBounds(r0, r7, r8, r6, r2)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            r0.draw(r15)
        L_0x04d1:
            boolean r0 = r1.drawErrorIcon
            if (r0 == 0) goto L_0x053a
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorDrawable
            float r6 = r1.reorderIconProgress
            float r6 = r14 - r6
            float r6 = r6 * r5
            int r5 = (int) r6
            r0.setAlpha(r5)
            android.graphics.RectF r0 = r1.rect
            int r5 = r1.errorLeft
            float r6 = (float) r5
            int r7 = r1.errorTop
            float r7 = (float) r7
            r8 = 1095761920(0x41500000, float:13.0)
            int r18 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r5 = r5 + r18
            float r5 = (float) r5
            int r2 = r1.errorTop
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r2 = r2 + r8
            float r2 = (float) r2
            r0.set(r6, r7, r5, r2)
            android.graphics.RectF r0 = r1.rect
            r2 = 1087373312(0x40d00000, float:6.5)
            float r5 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 * r2
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r6 = r6 * r2
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorPaint
            r15.drawRoundRect(r0, r5, r6, r2)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorDrawable
            int r2 = r1.errorLeft
            r5 = 1084647014(0x40a66666, float:5.2)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r2 = r2 + r5
            int r5 = r1.errorTop
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r5 = r5 + r3
            r3 = 1076258406(0x40266666, float:2.6)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r6 = 1091567616(0x41100000, float:9.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            setDrawableBounds(r0, r2, r5, r3, r6)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorDrawable
            r0.draw(r15)
            r20 = 1092616192(0x41200000, float:10.0)
            goto L_0x06c2
        L_0x053a:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x0547
            boolean r0 = r1.drawMentionIcon
            if (r0 == 0) goto L_0x0543
            goto L_0x0547
        L_0x0543:
            r20 = 1092616192(0x41200000, float:10.0)
            goto L_0x06c2
        L_0x0547:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x060b
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0557
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0554
            goto L_0x0557
        L_0x0554:
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
            goto L_0x0559
        L_0x0557:
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countGrayPaint
        L_0x0559:
            float r2 = r1.reorderIconProgress
            float r6 = r14 - r2
            float r6 = r6 * r5
            int r2 = (int) r6
            r0.setAlpha(r2)
            android.text.TextPaint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r6 = r1.reorderIconProgress
            float r6 = r14 - r6
            float r6 = r6 * r5
            int r6 = (int) r6
            r2.setAlpha(r6)
            boolean r2 = r1.countIsBiggerThanTen
            if (r2 == 0) goto L_0x05a9
            int r2 = r1.countLeft
            r6 = 1086324736(0x40c00000, float:6.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 - r7
            r7 = 1090519040(0x41000000, float:8.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r8
            android.graphics.RectF r8 = r1.rect
            float r6 = (float) r2
            int r5 = r1.countTop
            float r5 = (float) r5
            int r14 = r1.countWidth
            int r14 = r14 + r2
            r20 = 1092616192(0x41200000, float:10.0)
            int r21 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            int r14 = r14 + r21
            float r14 = (float) r14
            int r3 = r1.countTop
            r22 = 1098907648(0x41800000, float:16.0)
            int r22 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            int r3 = r3 + r22
            float r3 = (float) r3
            r8.set(r6, r5, r14, r3)
            android.graphics.RectF r3 = r1.rect
            r15.drawRoundRect(r3, r7, r7, r0)
            goto L_0x05d6
        L_0x05a9:
            r20 = 1092616192(0x41200000, float:10.0)
            int r2 = r1.countLeft
            r3 = 1082130432(0x40800000, float:4.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r3 = r1.countWidth
            r5 = 1090519040(0x41000000, float:8.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r3 = r3 + r6
            float r3 = (float) r3
            android.graphics.RectF r5 = r1.rect
            float r6 = (float) r2
            int r7 = r1.countTop
            float r8 = (float) r7
            float r14 = (float) r2
            float r14 = r14 + r3
            float r7 = (float) r7
            float r7 = r7 + r3
            r5.set(r6, r8, r14, r7)
            android.graphics.RectF r5 = r1.rect
            r6 = 1073741824(0x40000000, float:2.0)
            float r7 = r3 / r6
            float r8 = r3 / r6
            r15.drawRoundRect(r5, r7, r8, r0)
        L_0x05d6:
            android.text.StaticLayout r3 = r1.countLayout
            if (r3 == 0) goto L_0x060d
            r24.save()
            int r3 = r1.countLeft
            boolean r5 = r1.countIsBiggerThanTen
            if (r5 == 0) goto L_0x05e6
            r6 = 1065353216(0x3f800000, float:1.0)
            goto L_0x05e8
        L_0x05e6:
            r6 = 1056964608(0x3f000000, float:0.5)
        L_0x05e8:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r5
            float r3 = (float) r3
            int r5 = r1.countTop
            boolean r6 = r1.countIsBiggerThanTen
            if (r6 == 0) goto L_0x05f7
            r6 = 1073741824(0x40000000, float:2.0)
            goto L_0x05f9
        L_0x05f7:
            r6 = 1077936128(0x40400000, float:3.0)
        L_0x05f9:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            float r5 = (float) r5
            r15.translate(r3, r5)
            android.text.StaticLayout r3 = r1.countLayout
            r3.draw(r15)
            r24.restore()
            goto L_0x060d
        L_0x060b:
            r20 = 1092616192(0x41200000, float:10.0)
        L_0x060d:
            boolean r0 = r1.drawMentionIcon
            if (r0 == 0) goto L_0x06c2
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
            float r2 = r1.reorderIconProgress
            r3 = 1065353216(0x3f800000, float:1.0)
            float r6 = r3 - r2
            r2 = 1132396544(0x437f0000, float:255.0)
            float r6 = r6 * r2
            int r2 = (int) r6
            r0.setAlpha(r2)
            int r0 = r1.mentionLeft
            r2 = 1085276160(0x40b00000, float:5.5)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            int r2 = r1.mentionWidth
            r3 = 1090519040(0x41000000, float:8.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r5
            float r2 = (float) r2
            android.graphics.RectF r5 = r1.rect
            float r6 = (float) r0
            int r7 = r1.countTop
            float r8 = (float) r7
            float r14 = (float) r0
            float r14 = r14 + r2
            float r7 = (float) r7
            float r7 = r7 + r2
            r5.set(r6, r8, r14, r7)
            boolean r5 = r1.dialogMuted
            if (r5 == 0) goto L_0x064c
            int r5 = r1.folderId
            if (r5 == 0) goto L_0x064c
            android.graphics.Paint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countGrayPaint
            goto L_0x064e
        L_0x064c:
            android.graphics.Paint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
        L_0x064e:
            android.graphics.RectF r6 = r1.rect
            r7 = 1073741824(0x40000000, float:2.0)
            float r8 = r2 / r7
            float r14 = r2 / r7
            r15.drawRoundRect(r6, r8, r14, r5)
            android.text.StaticLayout r6 = r1.mentionLayout
            if (r6 == 0) goto L_0x0689
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r7 = r1.reorderIconProgress
            r8 = 1065353216(0x3f800000, float:1.0)
            float r7 = r8 - r7
            r8 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 * r8
            int r7 = (int) r7
            r6.setAlpha(r7)
            r24.save()
            int r6 = r1.mentionLeft
            float r6 = (float) r6
            int r7 = r1.countTop
            r8 = 1082130432(0x40800000, float:4.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 + r8
            float r7 = (float) r7
            r15.translate(r6, r7)
            android.text.StaticLayout r6 = r1.mentionLayout
            r6.draw(r15)
            r24.restore()
            goto L_0x06c2
        L_0x0689:
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            float r7 = r1.reorderIconProgress
            r8 = 1065353216(0x3f800000, float:1.0)
            float r7 = r8 - r7
            r8 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 * r8
            int r7 = (int) r7
            r6.setAlpha(r7)
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            int r7 = r1.mentionLeft
            r8 = 1073741824(0x40000000, float:2.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            int r7 = r7 - r8
            int r8 = r1.countTop
            r14 = 1078774989(0x404ccccd, float:3.2)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r8 = r8 + r14
            r14 = 1093664768(0x41300000, float:11.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r17 = 1093664768(0x41300000, float:11.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            setDrawableBounds(r6, r7, r8, r14, r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            r3.draw(r15)
        L_0x06c2:
            boolean r0 = r1.animatingArchiveAvatar
            r8 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x06e6
            r24.save()
            im.bclpbkiauv.ui.cells.DialogCell$BounceInterpolator r0 = r1.interpolator
            float r2 = r1.animatingArchiveAvatarProgress
            float r2 = r2 / r8
            float r0 = r0.getInterpolation(r2)
            r2 = 1065353216(0x3f800000, float:1.0)
            float r0 = r0 + r2
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getCenterX()
            im.bclpbkiauv.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getCenterY()
            r15.scale(r0, r0, r2, r3)
        L_0x06e6:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r15)
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x06f2
            r24.restore()
        L_0x06f2:
            boolean r0 = r1.drawSecretLockIcon
            if (r0 == 0) goto L_0x071d
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_lockDrawable
            int r0 = r0.getIntrinsicHeight()
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_lockDrawable
            int r3 = r1.avatarLeft
            float r5 = r1.topOffset
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageHeight()
            float r6 = (float) r6
            float r5 = r5 + r6
            float r6 = (float) r0
            float r5 = r5 - r6
            int r5 = (int) r5
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
            setDrawableBounds(r2, r3, r5, r6, r0)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_lockDrawable
            r2.draw(r15)
            goto L_0x07a0
        L_0x071d:
            boolean r0 = r1.drawGroupIcon
            if (r0 == 0) goto L_0x074d
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            int r2 = r1.avatarLeft
            float r3 = r1.topOffset
            im.bclpbkiauv.messenger.ImageReceiver r5 = r1.avatarImage
            int r5 = r5.getImageHeight()
            float r5 = (float) r5
            float r3 = r3 + r5
            r5 = 1089470464(0x40f00000, float:7.5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r3 = r3 - r5
            int r3 = (int) r3
            r5 = 1100480512(0x41980000, float:19.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r6 = 1089470464(0x40f00000, float:7.5)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            setDrawableBounds(r0, r2, r3, r5, r6)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            r0.draw(r15)
            goto L_0x07a0
        L_0x074d:
            boolean r0 = r1.drawBroadcastIcon
            if (r0 == 0) goto L_0x0777
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
            int r0 = r0.getIntrinsicHeight()
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
            int r3 = r1.avatarLeft
            float r5 = r1.topOffset
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageHeight()
            float r6 = (float) r6
            float r5 = r5 + r6
            float r6 = (float) r0
            float r5 = r5 - r6
            int r5 = (int) r5
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
            int r6 = r6.getIntrinsicWidth()
            setDrawableBounds(r2, r3, r5, r6, r0)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
            r2.draw(r15)
            goto L_0x07a0
        L_0x0777:
            boolean r0 = r1.drawBotIcon
            if (r0 == 0) goto L_0x07a0
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r0 = r0.getIntrinsicHeight()
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r3 = r1.avatarLeft
            float r5 = r1.topOffset
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageHeight()
            float r6 = (float) r6
            float r5 = r5 + r6
            float r6 = (float) r0
            float r5 = r5 - r6
            int r5 = (int) r5
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            setDrawableBounds(r2, r3, r5, r6, r0)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            r2.draw(r15)
        L_0x07a0:
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            r5 = 1
            if (r0 == 0) goto L_0x08b2
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x08b2
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x08b2
            boolean r0 = im.bclpbkiauv.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x08b2
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x08b2
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = r0.self
            if (r0 != 0) goto L_0x07ef
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x07d7
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r0 = r0.status
            int r0 = r0.expires
            int r2 = r1.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x07ed
        L_0x07d7:
            int r0 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x07ef
        L_0x07ed:
            r0 = 1
            goto L_0x07f0
        L_0x07ef:
            r0 = 0
        L_0x07f0:
            if (r0 != 0) goto L_0x07f9
            float r2 = r1.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x08b2
        L_0x07f9:
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            int r2 = r2.getImageY2()
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x080b
            boolean r6 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x0808
            goto L_0x080b
        L_0x0808:
            r19 = 1090519040(0x41000000, float:8.0)
            goto L_0x080d
        L_0x080b:
            r19 = 1086324736(0x40c00000, float:6.0)
        L_0x080d:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 - r6
            boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0830
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageX()
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0828
            boolean r7 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x0825
            goto L_0x0828
        L_0x0825:
            r18 = 1086324736(0x40c00000, float:6.0)
            goto L_0x082a
        L_0x0828:
            r18 = 1092616192(0x41200000, float:10.0)
        L_0x082a:
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            int r6 = r6 + r7
            goto L_0x0849
        L_0x0830:
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageX2()
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0842
            boolean r7 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x083f
            goto L_0x0842
        L_0x083f:
            r18 = 1086324736(0x40c00000, float:6.0)
            goto L_0x0844
        L_0x0842:
            r18 = 1092616192(0x41200000, float:10.0)
        L_0x0844:
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            int r6 = r6 - r7
        L_0x0849:
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r7.setColor(r14)
            float r7 = (float) r6
            float r14 = (float) r2
            r17 = 1088421888(0x40e00000, float:7.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r3 = (float) r3
            float r8 = r1.onlineProgress
            float r3 = r3 * r8
            android.graphics.Paint r8 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            r15.drawCircle(r7, r14, r3, r8)
            android.graphics.Paint r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            java.lang.String r7 = "chats_onlineCircle"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r3.setColor(r7)
            float r3 = (float) r6
            float r7 = (float) r2
            r8 = 1084227584(0x40a00000, float:5.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            float r14 = r1.onlineProgress
            float r8 = r8 * r14
            android.graphics.Paint r14 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            r15.drawCircle(r3, r7, r8, r14)
            if (r0 == 0) goto L_0x089b
            float r3 = r1.onlineProgress
            r7 = 1065353216(0x3f800000, float:1.0)
            int r8 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r8 >= 0) goto L_0x08b2
            float r8 = (float) r11
            r14 = 1125515264(0x43160000, float:150.0)
            float r8 = r8 / r14
            float r3 = r3 + r8
            r1.onlineProgress = r3
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 <= 0) goto L_0x0898
            r1.onlineProgress = r7
        L_0x0898:
            r4 = 1
            r0 = r4
            goto L_0x08b3
        L_0x089b:
            float r3 = r1.onlineProgress
            r7 = 0
            int r8 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r8 <= 0) goto L_0x08b2
            float r8 = (float) r11
            r14 = 1125515264(0x43160000, float:150.0)
            float r8 = r8 / r14
            float r3 = r3 - r8
            r1.onlineProgress = r3
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x08af
            r1.onlineProgress = r7
        L_0x08af:
            r4 = 1
            r0 = r4
            goto L_0x08b3
        L_0x08b2:
            r0 = r4
        L_0x08b3:
            float r2 = r1.translationX
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x08bd
            r24.restore()
        L_0x08bd:
            boolean r2 = r1.useSeparator
            if (r2 == 0) goto L_0x0907
            r2 = 1116733440(0x42900000, float:72.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x08ea
            r3 = 0
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r4 = (float) r2
            int r2 = r23.getMeasuredWidth()
            int r2 = r2 - r14
            float r6 = (float) r2
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r7 = (float) r2
            android.graphics.Paint r16 = im.bclpbkiauv.ui.actionbar.Theme.dividerPaint
            r2 = r24
            r5 = r6
            r6 = r7
            r7 = r16
            r2.drawLine(r3, r4, r5, r6, r7)
            goto L_0x0907
        L_0x08ea:
            float r3 = (float) r14
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r4 = (float) r2
            int r2 = r23.getMeasuredWidth()
            float r6 = (float) r2
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r7 = (float) r2
            android.graphics.Paint r16 = im.bclpbkiauv.ui.actionbar.Theme.dividerPaint
            r2 = r24
            r5 = r6
            r6 = r7
            r7 = r16
            r2.drawLine(r3, r4, r5, r6, r7)
        L_0x0907:
            float r2 = r1.clipProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0955
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 == r3) goto L_0x0918
            r24.restore()
            goto L_0x0955
        L_0x0918:
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r13)
            r2.setColor(r3)
            r3 = 0
            r4 = 0
            int r2 = r23.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r1.topClip
            float r2 = (float) r2
            float r6 = r1.clipProgress
            float r6 = r6 * r2
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
            int r2 = r23.getMeasuredHeight()
            int r4 = r1.bottomClip
            float r4 = (float) r4
            float r5 = r1.clipProgress
            float r4 = r4 * r5
            int r4 = (int) r4
            int r2 = r2 - r4
            float r4 = (float) r2
            int r2 = r23.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r23.getMeasuredHeight()
            float r6 = (float) r2
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r2 = r24
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0955:
            boolean r2 = r1.drawReorder
            if (r2 != 0) goto L_0x0960
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0990
        L_0x0960:
            boolean r2 = r1.drawReorder
            if (r2 == 0) goto L_0x097b
            float r2 = r1.reorderIconProgress
            r4 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x0990
            float r5 = (float) r11
            r6 = 1126825984(0x432a0000, float:170.0)
            float r5 = r5 / r6
            float r2 = r2 + r5
            r1.reorderIconProgress = r2
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0979
            r1.reorderIconProgress = r4
        L_0x0979:
            r0 = 1
            goto L_0x0990
        L_0x097b:
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0990
            float r4 = (float) r11
            r5 = 1126825984(0x432a0000, float:170.0)
            float r4 = r4 / r5
            float r2 = r2 - r4
            r1.reorderIconProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x098f
            r1.reorderIconProgress = r3
        L_0x098f:
            r0 = 1
        L_0x0990:
            boolean r3 = r1.archiveHidden
            if (r3 == 0) goto L_0x09bc
            float r3 = r1.archiveBackgroundProgress
            r2 = 0
            int r4 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x09e4
            float r4 = (float) r11
            r5 = 1126825984(0x432a0000, float:170.0)
            float r4 = r4 / r5
            float r3 = r3 - r4
            r1.archiveBackgroundProgress = r3
            float r3 = r1.currentRevealBounceProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x09aa
            r1.currentRevealBounceProgress = r2
        L_0x09aa:
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            int r3 = r3.getAvatarType()
            r4 = 3
            if (r3 != r4) goto L_0x09ba
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            float r4 = r1.archiveBackgroundProgress
            r3.setArchivedAvatarHiddenProgress(r4)
        L_0x09ba:
            r0 = 1
            goto L_0x09e4
        L_0x09bc:
            float r3 = r1.archiveBackgroundProgress
            r4 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x09e4
            float r5 = (float) r11
            r6 = 1126825984(0x432a0000, float:170.0)
            float r5 = r5 / r6
            float r3 = r3 + r5
            r1.archiveBackgroundProgress = r3
            float r3 = r1.currentRevealBounceProgress
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x09d3
            r1.currentRevealBounceProgress = r4
        L_0x09d3:
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            int r3 = r3.getAvatarType()
            r4 = 3
            if (r3 != r4) goto L_0x09e3
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            float r4 = r1.archiveBackgroundProgress
            r3.setArchivedAvatarHiddenProgress(r4)
        L_0x09e3:
            r0 = 1
        L_0x09e4:
            boolean r3 = r1.animatingArchiveAvatar
            if (r3 == 0) goto L_0x09fa
            float r3 = r1.animatingArchiveAvatarProgress
            float r4 = (float) r11
            float r3 = r3 + r4
            r1.animatingArchiveAvatarProgress = r3
            r4 = 1126825984(0x432a0000, float:170.0)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x09f9
            r1.animatingArchiveAvatarProgress = r4
            r3 = 0
            r1.animatingArchiveAvatar = r3
        L_0x09f9:
            r0 = 1
        L_0x09fa:
            boolean r3 = r1.drawRevealBackground
            if (r3 == 0) goto L_0x0a29
            float r2 = r1.currentRevealBounceProgress
            r3 = 1065353216(0x3f800000, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0a14
            float r4 = (float) r11
            r5 = 1126825984(0x432a0000, float:170.0)
            float r4 = r4 / r5
            float r2 = r2 + r4
            r1.currentRevealBounceProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a14
            r1.currentRevealBounceProgress = r3
            r0 = 1
        L_0x0a14:
            float r2 = r1.currentRevealProgress
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0a4b
            float r4 = (float) r11
            r5 = 1133903872(0x43960000, float:300.0)
            float r4 = r4 / r5
            float r2 = r2 + r4
            r1.currentRevealProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x0a27
            r1.currentRevealProgress = r3
        L_0x0a27:
            r0 = 1
            goto L_0x0a4b
        L_0x0a29:
            r3 = 1065353216(0x3f800000, float:1.0)
            float r4 = r1.currentRevealBounceProgress
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x0a36
            r2 = 0
            r1.currentRevealBounceProgress = r2
            r0 = 1
            goto L_0x0a37
        L_0x0a36:
            r2 = 0
        L_0x0a37:
            float r3 = r1.currentRevealProgress
            int r4 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0a4b
            float r4 = (float) r11
            r5 = 1133903872(0x43960000, float:300.0)
            float r4 = r4 / r5
            float r3 = r3 - r4
            r1.currentRevealProgress = r3
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0a4a
            r1.currentRevealProgress = r2
        L_0x0a4a:
            r0 = 1
        L_0x0a4b:
            if (r0 == 0) goto L_0x0a50
            r23.invalidate()
        L_0x0a50:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.DialogCell.onDraw(android.graphics.Canvas):void");
    }

    public void onReorderStateChanged(boolean reordering, boolean animated) {
        if ((this.drawPinIcon || !reordering) && this.drawReorder != reordering) {
            this.drawReorder = reordering;
            float f = 1.0f;
            if (animated) {
                if (reordering) {
                    f = 0.0f;
                }
                this.reorderIconProgress = f;
            } else {
                if (!reordering) {
                    f = 0.0f;
                }
                this.reorderIconProgress = f;
            }
            invalidate();
        } else if (!this.drawPinIcon) {
            this.drawReorder = false;
        }
    }

    public void setSliding(boolean value) {
        this.isSliding = value;
    }

    public void invalidateDrawable(Drawable who) {
        if (who == this.translationDrawable || who == Theme.dialogs_archiveAvatarDrawable) {
            invalidate(who.getBounds());
        } else {
            super.invalidateDrawable(who);
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.addAction(16);
        info.addAction(32);
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        TLRPC.User fromUser;
        super.onPopulateAccessibilityEvent(event);
        StringBuilder sb = new StringBuilder();
        if (this.currentDialogFolderId == 1) {
            sb.append(LocaleController.getString("ArchivedChats", R.string.ArchivedChats));
            sb.append(". ");
        } else {
            if (this.encryptedChat != null) {
                sb.append(LocaleController.getString("AccDescrSecretChat", R.string.AccDescrSecretChat));
                sb.append(". ");
            }
            TLRPC.User user2 = this.user;
            if (user2 != null) {
                if (user2.bot) {
                    sb.append(LocaleController.getString("Bot", R.string.Bot));
                    sb.append(". ");
                }
                if (this.user.self) {
                    sb.append(LocaleController.getString("SavedMessages", R.string.SavedMessages));
                } else {
                    sb.append(ContactsController.formatName(this.user.first_name, this.user.last_name));
                }
                sb.append(". ");
            } else {
                TLRPC.Chat chat2 = this.chat;
                if (chat2 != null) {
                    if (chat2.broadcast) {
                        sb.append(LocaleController.getString("AccDescrChannel", R.string.AccDescrChannel));
                    } else {
                        sb.append(LocaleController.getString("AccDescrGroup", R.string.AccDescrGroup));
                    }
                    sb.append(". ");
                    sb.append(this.chat.title);
                    sb.append(". ");
                }
            }
        }
        int i = this.unreadCount;
        if (i > 0) {
            sb.append(LocaleController.formatPluralString("NewMessages", i));
            sb.append(". ");
        }
        MessageObject messageObject = this.message;
        if (messageObject == null || this.currentDialogFolderId != 0) {
            event.setContentDescription(sb.toString());
            return;
        }
        int lastDate = this.lastMessageDate;
        if (this.lastMessageDate == 0 && messageObject != null) {
            lastDate = messageObject.messageOwner.date;
        }
        String date = LocaleController.formatDateAudio((long) lastDate);
        if (this.message.isOut()) {
            sb.append(LocaleController.formatString("AccDescrSentDate", R.string.AccDescrSentDate, date));
        } else {
            sb.append(LocaleController.formatString("AccDescrReceivedDate", R.string.AccDescrReceivedDate, date));
        }
        sb.append(". ");
        if (this.chat != null && !this.message.isOut() && this.message.isFromUser() && this.message.messageOwner.action == null && (fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.message.messageOwner.from_id))) != null) {
            sb.append(ContactsController.formatName(fromUser.first_name, fromUser.last_name));
            sb.append(". ");
        }
        if (this.encryptedChat == null) {
            sb.append(this.message.messageText);
            if (!this.message.isMediaEmpty() && !TextUtils.isEmpty(this.message.caption)) {
                sb.append(". ");
                sb.append(this.message.caption);
            }
        }
        event.setContentDescription(sb.toString());
    }

    public void setClipProgress(float value) {
        this.clipProgress = value;
        invalidate();
    }

    public float getClipProgress() {
        return this.clipProgress;
    }

    public void setTopClip(int value) {
        this.topClip = value;
    }

    public void setBottomClip(int value) {
        this.bottomClip = value;
    }
}
