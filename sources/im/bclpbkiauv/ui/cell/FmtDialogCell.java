package im.bclpbkiauv.ui.cell;

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
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
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
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.cells.BaseCell;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.CheckBoxBase;
import im.bclpbkiauv.ui.components.RLottieDrawable;
import im.bclpbkiauv.ui.components.TypefaceSpan;
import im.bclpbkiauv.ui.fragments.DialogsFragment;
import java.util.ArrayList;

public class FmtDialogCell extends BaseCell {
    private boolean animatingArchiveAvatar;
    private float animatingArchiveAvatarProgress;
    private float archiveBackgroundProgress;
    private boolean archiveHidden;
    private boolean attachedToWindow;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
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
    private int currentAccount = UserConfig.selectedAccount;
    private int currentDialogFolderDialogsCount;
    private int currentDialogFolderId;
    private long currentDialogId;
    private int currentEditDate;
    private float currentRevealBounceProgress;
    private float currentRevealProgress;
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
    private BounceInterpolator interpolator = new BounceInterpolator();
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
    private final ViewDragHelper mDragHelper;
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
    private RectF rect = new RectF();
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

    public FmtDialogCell(Context context, boolean forceThreeLines) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(7.5f));
        this.useForceThreeLines = forceThreeLines;
        this.mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            public boolean tryCaptureView(View child, int pointerId) {
                return false;
            }

            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            }
        });
        setClipChildren(false);
    }

    public void computeScroll() {
        invalidate();
        super.computeScroll();
        if (this.mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void openLeft() {
        this.mDragHelper.smoothSlideViewTo(this, AndroidUtilities.dp(50.0f), 0);
        invalidate();
    }

    public void close() {
        this.mDragHelper.smoothSlideViewTo(this, 0, 0);
        invalidate();
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
        if (this.currentDialogId != 0 && changed) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r31v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v88, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v106, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v108, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v111, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v118, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v120, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r8v24 */
    /* JADX WARNING: type inference failed for: r8v25 */
    /* JADX WARNING: type inference failed for: r8v27 */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x027b, code lost:
        if (r1.chat.kicked == false) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0f5c, code lost:
        if (im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x0f64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0f7c, code lost:
        if (im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout != false) goto L_0x0f7e;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0b8f  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0ba6  */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0c3b  */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0c86  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0c97  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0cc8  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0d20  */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0d44  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0d73 A[SYNTHETIC, Splitter:B:564:0x0d73] */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0e15  */
    /* JADX WARNING: Removed duplicated region for block: B:583:0x0e1a  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0ead  */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0f3b  */
    /* JADX WARNING: Removed duplicated region for block: B:636:0x0f49  */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0f5a A[SYNTHETIC, Splitter:B:640:0x0f5a] */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0f68 A[SYNTHETIC, Splitter:B:648:0x0f68] */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0f7a A[SYNTHETIC, Splitter:B:656:0x0f7a] */
    /* JADX WARNING: Removed duplicated region for block: B:663:0x0f88 A[Catch:{ Exception -> 0x114c }] */
    /* JADX WARNING: Removed duplicated region for block: B:687:0x10a7 A[Catch:{ Exception -> 0x10bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:691:0x10c2  */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x10e7 A[Catch:{ Exception -> 0x114a }] */
    /* JADX WARNING: Removed duplicated region for block: B:700:0x10e8 A[Catch:{ Exception -> 0x114a }] */
    /* JADX WARNING: Removed duplicated region for block: B:704:0x1106 A[Catch:{ Exception -> 0x114a }] */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x1134 A[Catch:{ Exception -> 0x114a }] */
    /* JADX WARNING: Removed duplicated region for block: B:711:0x1137 A[Catch:{ Exception -> 0x114a }] */
    /* JADX WARNING: Removed duplicated region for block: B:719:0x1158  */
    /* JADX WARNING: Removed duplicated region for block: B:767:0x1268  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r44 = this;
            r1 = r44
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
            boolean r3 = r1.useForceThreeLines
            r20 = 1102053376(0x41b00000, float:22.0)
            r21 = 1099956224(0x41900000, float:18.0)
            r22 = 1116733440(0x42900000, float:72.0)
            if (r3 != 0) goto L_0x011a
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0108
            goto L_0x011a
        L_0x0108:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0113
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r3
            goto L_0x012b
        L_0x0113:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r3
            goto L_0x012b
        L_0x011a:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0125
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.nameLeft = r3
            goto L_0x012b
        L_0x0125:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r3
        L_0x012b:
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x015a
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0211
            r1.drawSecretLockIcon = r14
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x014c
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x013e
            goto L_0x014c
        L_0x013e:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0144
            goto L_0x0211
        L_0x0144:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r3
            goto L_0x0211
        L_0x014c:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0152
            goto L_0x0211
        L_0x0152:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r3
            goto L_0x0211
        L_0x015a:
            int r3 = r1.currentDialogFolderId
            if (r3 != 0) goto L_0x0211
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x01cf
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x016e
            r1.drawScam = r14
            im.bclpbkiauv.ui.components.ScamDrawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x0174
        L_0x016e:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.verified
            r1.drawVerifiedIcon = r3
        L_0x0174:
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0211
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x01a9
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0181
            goto L_0x01a9
        L_0x0181:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            int r3 = r3.id
            if (r3 < 0) goto L_0x0199
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x0196
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0196
            goto L_0x0199
        L_0x0196:
            r1.drawGroupIcon = r14
            goto L_0x019b
        L_0x0199:
            r1.drawBroadcastIcon = r14
        L_0x019b:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01a1
            goto L_0x0211
        L_0x01a1:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r3
            goto L_0x0211
        L_0x01a9:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            int r3 = r3.id
            if (r3 < 0) goto L_0x01c1
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x01be
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x01be
            goto L_0x01c1
        L_0x01be:
            r1.drawGroupIcon = r14
            goto L_0x01c3
        L_0x01c1:
            r1.drawBroadcastIcon = r14
        L_0x01c3:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01c8
            goto L_0x0211
        L_0x01c8:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r3
            goto L_0x0211
        L_0x01cf:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x0211
            boolean r3 = r3.scam
            if (r3 == 0) goto L_0x01df
            r1.drawScam = r14
            im.bclpbkiauv.ui.components.ScamDrawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r3.checkText()
            goto L_0x01e5
        L_0x01df:
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.verified
            r1.drawVerifiedIcon = r3
        L_0x01e5:
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.drawDialogIcons
            if (r3 == 0) goto L_0x0211
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = r3.bot
            if (r3 == 0) goto L_0x0211
            r1.drawBotIcon = r14
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0206
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x01fa
            goto L_0x0206
        L_0x01fa:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x01ff
            goto L_0x0211
        L_0x01ff:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            r1.nameLeft = r3
            goto L_0x0211
        L_0x0206:
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x020b
            goto L_0x0211
        L_0x020b:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            r1.nameLeft = r3
        L_0x0211:
            int r3 = r1.lastMessageDate
            int r14 = r1.lastMessageDate
            if (r14 != 0) goto L_0x021f
            im.bclpbkiauv.messenger.MessageObject r14 = r1.message
            if (r14 == 0) goto L_0x021f
            im.bclpbkiauv.tgnet.TLRPC$Message r14 = r14.messageOwner
            int r3 = r14.date
        L_0x021f:
            boolean r14 = r1.isDialogCell
            if (r14 == 0) goto L_0x0283
            int r14 = r1.currentAccount
            im.bclpbkiauv.messenger.MediaDataController r14 = im.bclpbkiauv.messenger.MediaDataController.getInstance(r14)
            r23 = r5
            long r4 = r1.currentDialogId
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r4 = r14.getDraft(r4)
            r1.draftMessage = r4
            if (r4 == 0) goto L_0x024d
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0243
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.reply_to_msg_id
            if (r4 == 0) goto L_0x027d
        L_0x0243:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            int r4 = r4.date
            if (r3 <= r4) goto L_0x024d
            int r4 = r1.unreadCount
            if (r4 != 0) goto L_0x027d
        L_0x024d:
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x026f
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x026f
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.creator
            if (r4 != 0) goto L_0x026f
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            if (r4 == 0) goto L_0x027d
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            im.bclpbkiauv.tgnet.TLRPC$TL_chatAdminRights r4 = r4.admin_rights
            boolean r4 = r4.post_messages
            if (r4 == 0) goto L_0x027d
        L_0x026f:
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            if (r4 == 0) goto L_0x0281
            boolean r4 = r4.left
            if (r4 != 0) goto L_0x027d
            im.bclpbkiauv.tgnet.TLRPC$Chat r4 = r1.chat
            boolean r4 = r4.kicked
            if (r4 == 0) goto L_0x0288
        L_0x027d:
            r4 = 0
            r1.draftMessage = r4
            goto L_0x0288
        L_0x0281:
            r4 = 0
            goto L_0x0288
        L_0x0283:
            r23 = r5
            r4 = 0
            r1.draftMessage = r4
        L_0x0288:
            java.lang.String r14 = ""
            if (r10 == 0) goto L_0x029d
            r9 = r10
            r1.lastPrintString = r10
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r4 = r0
            r25 = r3
            r26 = r6
            r27 = r7
            r28 = r8
            r3 = 2
            goto L_0x089f
        L_0x029d:
            r5 = 0
            r1.lastPrintString = r5
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r5 = r1.draftMessage
            if (r5 == 0) goto L_0x037f
            r12 = 0
            r5 = 2131690977(0x7f0f05e1, float:1.9011013E38)
            java.lang.String r4 = "Draft"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r5)
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r4 = r1.draftMessage
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x02fb
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x02ed
            boolean r4 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x02c7
            r25 = r3
            r26 = r6
            r27 = r7
            goto L_0x02f3
        L_0x02c7:
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r0)
            android.text.style.ForegroundColorSpan r5 = new android.text.style.ForegroundColorSpan
            java.lang.String r19 = "chats_draft"
            r25 = r3
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            r5.<init>(r3)
            int r3 = r0.length()
            r26 = r6
            r27 = r7
            r6 = 33
            r7 = 0
            r4.setSpan(r5, r7, r3, r6)
            r9 = r4
            r4 = r0
            r28 = r8
            r3 = 2
            goto L_0x089f
        L_0x02ed:
            r25 = r3
            r26 = r6
            r27 = r7
        L_0x02f3:
            java.lang.String r9 = ""
            r4 = r0
            r28 = r8
            r3 = 2
            goto L_0x089f
        L_0x02fb:
            r25 = r3
            r26 = r6
            r27 = r7
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r3 = r1.draftMessage
            java.lang.String r3 = r3.message
            int r4 = r3.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x0312
            r4 = 0
            java.lang.String r3 = r3.substring(r4, r5)
        L_0x0312:
            boolean r4 = r1.useForceThreeLines
            if (r4 != 0) goto L_0x0350
            boolean r4 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r4 == 0) goto L_0x031e
            r28 = r8
            r8 = 0
            goto L_0x0353
        L_0x031e:
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 32
            r6 = 10
            java.lang.String r7 = r3.replace(r6, r4)
            r4 = 0
            r5[r4] = r7
            r6 = 1
            r5[r6] = r0
            java.lang.String r5 = java.lang.String.format(r2, r5)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r5)
            android.text.style.ForegroundColorSpan r7 = new android.text.style.ForegroundColorSpan
            java.lang.String r19 = "chats_draft"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r19)
            r7.<init>(r4)
            int r4 = r0.length()
            int r4 = r4 + r6
            r28 = r8
            r6 = 33
            r8 = 0
            r5.setSpan(r7, r8, r4, r6)
            goto L_0x036b
        L_0x0350:
            r28 = r8
            r8 = 0
        L_0x0353:
            r4 = 2
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 32
            r6 = 10
            java.lang.String r7 = r3.replace(r6, r4)
            r5[r8] = r7
            r4 = 1
            r5[r4] = r0
            java.lang.String r4 = java.lang.String.format(r2, r5)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r4)
        L_0x036b:
            android.text.TextPaint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r4 = r4.getFontMetricsInt()
            r6 = 1101004800(0x41a00000, float:20.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            java.lang.CharSequence r9 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r5, r4, r7, r8)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x037f:
            r25 = r3
            r26 = r6
            r27 = r7
            r28 = r8
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x039a
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r3 = 2131691583(0x7f0f083f, float:1.9012242E38)
            java.lang.String r4 = "HistoryCleared"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x039a:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            if (r3 != 0) goto L_0x0459
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            if (r3 == 0) goto L_0x0453
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r3 = r1.encryptedChat
            boolean r4 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatRequested
            if (r4 == 0) goto L_0x03b7
            r3 = 2131691115(0x7f0f066b, float:1.9011293E38)
            java.lang.String r4 = "EncryptionProcessing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x03b7:
            boolean r4 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatWaiting
            if (r4 == 0) goto L_0x03ed
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x03da
            java.lang.String r3 = r3.first_name
            if (r3 == 0) goto L_0x03da
            r3 = 2131690056(0x7f0f0248, float:1.9009145E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = r4.first_name
            r6 = 0
            r5[r6] = r4
            java.lang.String r4 = "AwaitingEncryption"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r5)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x03da:
            r6 = 0
            r3 = 2131690056(0x7f0f0248, float:1.9009145E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r6] = r14
            java.lang.String r4 = "AwaitingEncryption"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r5)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x03ed:
            boolean r4 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChatDiscarded
            if (r4 == 0) goto L_0x03fe
            r3 = 2131691116(0x7f0f066c, float:1.9011295E38)
            java.lang.String r4 = "EncryptionRejected"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x03fe:
            boolean r4 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_encryptedChat
            if (r4 == 0) goto L_0x044f
            int r3 = r3.admin_id
            int r4 = r1.currentAccount
            im.bclpbkiauv.messenger.UserConfig r4 = im.bclpbkiauv.messenger.UserConfig.getInstance(r4)
            int r4 = r4.getClientUserId()
            if (r3 != r4) goto L_0x0442
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            if (r3 == 0) goto L_0x042f
            java.lang.String r3 = r3.first_name
            if (r3 == 0) goto L_0x042f
            r3 = 2131691104(0x7f0f0660, float:1.901127E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r1.user
            java.lang.String r4 = r4.first_name
            r6 = 0
            r5[r6] = r4
            java.lang.String r4 = "EncryptedChatStartedOutgoing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r5)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x042f:
            r6 = 0
            r3 = 2131691104(0x7f0f0660, float:1.901127E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r6] = r14
            java.lang.String r4 = "EncryptedChatStartedOutgoing"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r5)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x0442:
            r3 = 2131691103(0x7f0f065f, float:1.9011268E38)
            java.lang.String r4 = "EncryptedChatStartedIncoming"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x044f:
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x0453:
            java.lang.String r9 = ""
            r4 = r0
            r3 = 2
            goto L_0x089f
        L_0x0459:
            r4 = 0
            r5 = 0
            boolean r3 = r3.isFromUser()
            if (r3 == 0) goto L_0x0476
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.from_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r3.getUser(r6)
            goto L_0x048c
        L_0x0476:
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r6.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.channel_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$Chat r5 = r3.getChat(r6)
        L_0x048c:
            int r3 = r1.dialogsType
            r6 = 3
            if (r3 != r6) goto L_0x04ae
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r1.user
            boolean r3 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r3)
            if (r3 == 0) goto L_0x04ae
            r3 = 2131693694(0x7f0f107e, float:1.9016524E38)
            java.lang.String r6 = "SavedMessagesInfo"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r3)
            r6 = 0
            r7 = 0
            r9 = r3
            r29 = r4
            r30 = r5
            r13 = r6
            r15 = r7
            r3 = 2
            goto L_0x0894
        L_0x04ae:
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x04c7
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 != 0) goto L_0x04c7
            int r3 = r1.currentDialogFolderId
            if (r3 == 0) goto L_0x04c7
            r12 = 0
            java.lang.CharSequence r3 = r44.formatArchivedDialogNames()
            r9 = r3
            r29 = r4
            r30 = r5
            r3 = 2
            goto L_0x0894
        L_0x04c7:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService
            if (r3 == 0) goto L_0x04fd
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)
            if (r3 == 0) goto L_0x04ef
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r3 != 0) goto L_0x04eb
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r3 == 0) goto L_0x04ef
        L_0x04eb:
            java.lang.String r3 = ""
            r13 = 0
            goto L_0x04f3
        L_0x04ef:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.messageText
        L_0x04f3:
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r9 = r3
            r29 = r4
            r30 = r5
            r3 = 2
            goto L_0x0894
        L_0x04fd:
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r1.chat
            if (r3 == 0) goto L_0x0786
            int r3 = r3.id
            if (r3 <= 0) goto L_0x0786
            if (r5 != 0) goto L_0x0786
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isOutOwner()
            if (r3 == 0) goto L_0x051a
            r3 = 2131691435(0x7f0f07ab, float:1.9011942E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r3)
            r3 = r0
            goto L_0x0561
        L_0x051a:
            if (r4 == 0) goto L_0x0552
            boolean r3 = r1.useForceThreeLines
            if (r3 != 0) goto L_0x0531
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r3 == 0) goto L_0x0525
            goto L_0x0531
        L_0x0525:
            java.lang.String r3 = im.bclpbkiauv.messenger.UserObject.getFirstName(r4)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r3.replace(r6, r14)
            r3 = r0
            goto L_0x0561
        L_0x0531:
            boolean r3 = im.bclpbkiauv.messenger.UserObject.isDeleted(r4)
            if (r3 == 0) goto L_0x0542
            r3 = 2131691577(0x7f0f0839, float:1.901223E38)
            java.lang.String r6 = "HiddenName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r6, r3)
            r3 = r0
            goto L_0x0561
        L_0x0542:
            java.lang.String r3 = r4.first_name
            java.lang.String r6 = r4.last_name
            java.lang.String r3 = im.bclpbkiauv.messenger.ContactsController.formatName(r3, r6)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r3.replace(r6, r14)
            r3 = r0
            goto L_0x0561
        L_0x0552:
            if (r5 == 0) goto L_0x055e
            java.lang.String r3 = r5.title
            java.lang.String r6 = "\n"
            java.lang.String r0 = r3.replace(r6, r14)
            r3 = r0
            goto L_0x0561
        L_0x055e:
            java.lang.String r0 = "DELETED"
            r3 = r0
        L_0x0561:
            r12 = 0
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            if (r0 == 0) goto L_0x05dc
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.caption
            java.lang.String r0 = r0.toString()
            int r6 = r0.length()
            r7 = 150(0x96, float:2.1E-43)
            if (r6 <= r7) goto L_0x057d
            r6 = 0
            java.lang.String r0 = r0.substring(r6, r7)
        L_0x057d:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isVideo()
            if (r6 == 0) goto L_0x0588
            java.lang.String r6 = "📹 "
            goto L_0x05ab
        L_0x0588:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isVoice()
            if (r6 == 0) goto L_0x0593
            java.lang.String r6 = "🎤 "
            goto L_0x05ab
        L_0x0593:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isMusic()
            if (r6 == 0) goto L_0x059e
            java.lang.String r6 = "🎧 "
            goto L_0x05ab
        L_0x059e:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            boolean r6 = r6.isPhoto()
            if (r6 == 0) goto L_0x05a9
            java.lang.String r6 = "🖼 "
            goto L_0x05ab
        L_0x05a9:
            java.lang.String r6 = "📎 "
        L_0x05ab:
            r7 = 2
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r6)
            r29 = r4
            r30 = r5
            r19 = r6
            r4 = 32
            r5 = 10
            java.lang.String r6 = r0.replace(r5, r4)
            r7.append(r6)
            java.lang.String r4 = r7.toString()
            r5 = 0
            r8[r5] = r4
            r4 = 1
            r8[r4] = r3
            java.lang.String r4 = java.lang.String.format(r2, r8)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            r5 = r0
            goto L_0x0742
        L_0x05dc:
            r29 = r4
            r30 = r5
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x06ea
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isMediaEmpty()
            if (r0 != 0) goto L_0x06ea
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0634
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x061b
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r5 = r5.game
            java.lang.String r5 = r5.title
            r6 = 0
            r0[r6] = r5
            java.lang.String r5 = "🎮 ⁨%s⁩"
            java.lang.String r0 = java.lang.String.format(r5, r0)
            r4 = r0
            r7 = 1
            goto L_0x0682
        L_0x061b:
            r4 = 1
            r6 = 0
            java.lang.Object[] r0 = new java.lang.Object[r4]
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r4.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r0[r6] = r4
            java.lang.String r4 = "🎮 %s"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            r4 = r0
            r7 = 1
            goto L_0x0682
        L_0x0634:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            int r0 = r0.type
            r4 = 14
            if (r0 != r4) goto L_0x067c
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 18
            if (r0 < r4) goto L_0x065f
            r4 = 2
            java.lang.Object[] r0 = new java.lang.Object[r4]
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicAuthor()
            r6 = 0
            r0[r6] = r5
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicTitle()
            r7 = 1
            r0[r7] = r5
            java.lang.String r5 = "🎧 ⁨%s - %s⁩"
            java.lang.String r0 = java.lang.String.format(r5, r0)
            r4 = r0
            goto L_0x0682
        L_0x065f:
            r4 = 2
            r6 = 0
            r7 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.String r4 = r4.getMusicAuthor()
            r0[r6] = r4
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.String r4 = r4.getMusicTitle()
            r0[r7] = r4
            java.lang.String r4 = "🎧 %s - %s"
            java.lang.String r0 = java.lang.String.format(r4, r0)
            r4 = r0
            goto L_0x0682
        L_0x067c:
            r7 = 1
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r0 = r0.messageText
            r4 = r0
        L_0x0682:
            r5 = 2
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r5 = 0
            r0[r5] = r4
            r0[r7] = r3
            java.lang.String r0 = java.lang.String.format(r2, r0)
            android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r0)
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x06e5 }
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x06e5 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x06e5 }
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShareContact     // Catch:{ Exception -> 0x06e5 }
            if (r0 != 0) goto L_0x06c6
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x06e5 }
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x06e5 }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x06e5 }
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShare     // Catch:{ Exception -> 0x06e5 }
            if (r0 == 0) goto L_0x06a7
            goto L_0x06c6
        L_0x06a7:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x06e5 }
            java.lang.String r6 = "chats_attachMessage"
            int r6 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r6)     // Catch:{ Exception -> 0x06e5 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x06e5 }
            if (r18 == 0) goto L_0x06bb
            int r6 = r3.length()     // Catch:{ Exception -> 0x06e5 }
            r7 = 2
            int r6 = r6 + r7
            goto L_0x06bc
        L_0x06bb:
            r6 = 0
        L_0x06bc:
            int r7 = r5.length()     // Catch:{ Exception -> 0x06e5 }
            r8 = 33
            r5.setSpan(r0, r6, r7, r8)     // Catch:{ Exception -> 0x06e5 }
            goto L_0x06e4
        L_0x06c6:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x06e5 }
            java.lang.String r6 = "#999999"
            int r6 = android.graphics.Color.parseColor(r6)     // Catch:{ Exception -> 0x06e5 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x06e5 }
            if (r18 == 0) goto L_0x06da
            int r6 = r3.length()     // Catch:{ Exception -> 0x06e5 }
            r7 = 2
            int r6 = r6 + r7
            goto L_0x06db
        L_0x06da:
            r6 = 0
        L_0x06db:
            int r7 = r5.length()     // Catch:{ Exception -> 0x06e5 }
            r8 = 33
            r5.setSpan(r0, r6, r7, r8)     // Catch:{ Exception -> 0x06e5 }
        L_0x06e4:
            goto L_0x06e9
        L_0x06e5:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x06e9:
            goto L_0x0742
        L_0x06ea:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x073d
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.messageText
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0714
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.messageText
            boolean r4 = r4.equals(r0)
            if (r4 != 0) goto L_0x0714
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.messageText
            java.lang.String r0 = r4.toString()
        L_0x0714:
            int r4 = r0.length()
            r5 = 150(0x96, float:2.1E-43)
            if (r4 <= r5) goto L_0x0722
            r4 = 0
            java.lang.String r0 = r0.substring(r4, r5)
            goto L_0x0723
        L_0x0722:
            r4 = 0
        L_0x0723:
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 32
            r7 = 10
            java.lang.String r8 = r0.replace(r7, r5)
            r6[r4] = r8
            r4 = 1
            r6[r4] = r3
            java.lang.String r4 = java.lang.String.format(r2, r6)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r4)
            r5 = r0
            goto L_0x0742
        L_0x073d:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r14)
            r5 = r0
        L_0x0742:
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x074a
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0754
        L_0x074a:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0770
            int r0 = r5.length()
            if (r0 <= 0) goto L_0x0770
        L_0x0754:
            android.text.style.ForegroundColorSpan r0 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x076c }
            java.lang.String r4 = "chats_nameMessage"
            int r4 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r4)     // Catch:{ Exception -> 0x076c }
            r0.<init>(r4)     // Catch:{ Exception -> 0x076c }
            int r4 = r3.length()     // Catch:{ Exception -> 0x076c }
            r6 = 1
            int r4 = r4 + r6
            r6 = 33
            r7 = 0
            r5.setSpan(r0, r7, r4, r6)     // Catch:{ Exception -> 0x076c }
            goto L_0x0770
        L_0x076c:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0770:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            r4 = 1101004800(0x41a00000, float:20.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r4 = 0
            java.lang.CharSequence r0 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r5, r0, r6, r4)
            r9 = r0
            r0 = r3
            r3 = 2
            goto L_0x0894
        L_0x0786:
            r29 = r4
            r30 = r5
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x07b7
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Photo r3 = r3.photo
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_photoEmpty
            if (r3 == 0) goto L_0x07b7
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x07b7
            r3 = 2131689958(0x7f0f01e6, float:1.9008946E38)
            java.lang.String r4 = "AttachPhotoExpired"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r9 = r3
            r3 = 2
            goto L_0x0894
        L_0x07b7:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r3 == 0) goto L_0x07e4
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentEmpty
            if (r3 == 0) goto L_0x07e4
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x07e4
            r3 = 2131689964(0x7f0f01ec, float:1.9008958E38)
            java.lang.String r4 = "AttachVideoExpired"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r9 = r3
            r3 = 2
            goto L_0x0894
        L_0x07e4:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            java.lang.CharSequence r3 = r3.caption
            if (r3 == 0) goto L_0x082e
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x07f5
            java.lang.String r3 = "📹 "
            goto L_0x0818
        L_0x07f5:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x0800
            java.lang.String r3 = "🎤 "
            goto L_0x0818
        L_0x0800:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isMusic()
            if (r3 == 0) goto L_0x080b
            java.lang.String r3 = "🎧 "
            goto L_0x0818
        L_0x080b:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            boolean r3 = r3.isPhoto()
            if (r3 == 0) goto L_0x0816
            java.lang.String r3 = "🖼 "
            goto L_0x0818
        L_0x0816:
            java.lang.String r3 = "📎 "
        L_0x0818:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.CharSequence r5 = r5.caption
            r4.append(r5)
            java.lang.String r3 = r4.toString()
            r9 = r3
            r3 = 2
            goto L_0x0894
        L_0x082e:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0856
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "🎮 "
            r3.append(r4)
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r4.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = r3
            r3 = 2
            goto L_0x087f
        L_0x0856:
            im.bclpbkiauv.messenger.MessageObject r3 = r1.message
            int r3 = r3.type
            r4 = 14
            if (r3 != r4) goto L_0x087a
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicAuthor()
            r6 = 0
            r4[r6] = r5
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.lang.String r5 = r5.getMusicTitle()
            r6 = 1
            r4[r6] = r5
            java.lang.String r5 = "🎧 %s - %s"
            java.lang.String r4 = java.lang.String.format(r5, r4)
            goto L_0x087f
        L_0x087a:
            r3 = 2
            im.bclpbkiauv.messenger.MessageObject r4 = r1.message
            java.lang.CharSequence r4 = r4.messageText
        L_0x087f:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media
            if (r5 == 0) goto L_0x0893
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            boolean r5 = r5.isMediaEmpty()
            if (r5 != 0) goto L_0x0893
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePrintingPaint
            r9 = r4
            goto L_0x0894
        L_0x0893:
            r9 = r4
        L_0x0894:
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x089e
            java.lang.CharSequence r0 = r44.formatArchivedDialogNames()
            r4 = r0
            goto L_0x089f
        L_0x089e:
            r4 = r0
        L_0x089f:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x08ab
            int r0 = r0.date
            long r5 = (long) r0
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x08c5
        L_0x08ab:
            int r0 = r1.lastMessageDate
            if (r0 == 0) goto L_0x08b5
            long r5 = (long) r0
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x08c5
        L_0x08b5:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x08c3
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.stringForMessageListDate(r5)
            goto L_0x08c5
        L_0x08c3:
            r6 = r26
        L_0x08c5:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            if (r0 != 0) goto L_0x08dc
            r5 = 0
            r1.drawCheck1 = r5
            r1.drawCheck2 = r5
            r1.drawClockIcon = r5
            r1.drawCount = r5
            r1.drawMentionIcon = r5
            r1.drawErrorIcon = r5
            r7 = r27
            r8 = r28
            goto L_0x09f5
        L_0x08dc:
            int r5 = r1.currentDialogFolderId
            if (r5 == 0) goto L_0x0927
            int r0 = r1.unreadCount
            int r5 = r1.mentionCount
            int r7 = r0 + r5
            if (r7 <= 0) goto L_0x091d
            if (r0 <= r5) goto L_0x0904
            r7 = 1
            r1.drawCount = r7
            r8 = 0
            r1.drawMentionIcon = r8
            java.lang.Object[] r3 = new java.lang.Object[r7]
            int r0 = r0 + r5
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3[r8] = r0
            java.lang.String r0 = "%d"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            r7 = r0
            r8 = r28
            goto L_0x097d
        L_0x0904:
            r7 = 1
            r8 = 0
            r1.drawCount = r8
            r1.drawMentionIcon = r7
            java.lang.Object[] r3 = new java.lang.Object[r7]
            int r0 = r0 + r5
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3[r8] = r0
            java.lang.String r0 = "%d"
            java.lang.String r0 = java.lang.String.format(r0, r3)
            r8 = r0
            r7 = r27
            goto L_0x097d
        L_0x091d:
            r8 = 0
            r1.drawCount = r8
            r1.drawMentionIcon = r8
            r7 = r27
            r8 = r28
            goto L_0x097d
        L_0x0927:
            r8 = 0
            boolean r3 = r1.clearingDialog
            if (r3 == 0) goto L_0x0935
            r1.drawCount = r8
            r0 = 0
            r13 = r0
            r7 = r27
            r3 = 1
            r5 = 0
            goto L_0x0970
        L_0x0935:
            int r3 = r1.unreadCount
            if (r3 == 0) goto L_0x095f
            r5 = 1
            if (r3 != r5) goto L_0x0948
            int r5 = r1.mentionCount
            if (r3 != r5) goto L_0x0948
            if (r0 == 0) goto L_0x0948
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.mentioned
            if (r0 != 0) goto L_0x095f
        L_0x0948:
            r3 = 1
            r1.drawCount = r3
            java.lang.Object[] r0 = new java.lang.Object[r3]
            int r3 = r1.unreadCount
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5 = 0
            r0[r5] = r3
            java.lang.String r3 = "%d"
            java.lang.String r7 = java.lang.String.format(r3, r0)
            r3 = 1
            r5 = 0
            goto L_0x0970
        L_0x095f:
            boolean r0 = r1.markUnread
            if (r0 == 0) goto L_0x096a
            r3 = 1
            r1.drawCount = r3
            java.lang.String r7 = ""
            r5 = 0
            goto L_0x0970
        L_0x096a:
            r3 = 1
            r5 = 0
            r1.drawCount = r5
            r7 = r27
        L_0x0970:
            int r0 = r1.mentionCount
            if (r0 == 0) goto L_0x0979
            r1.drawMentionIcon = r3
            java.lang.String r8 = "@"
            goto L_0x097d
        L_0x0979:
            r1.drawMentionIcon = r5
            r8 = r28
        L_0x097d:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isOut()
            if (r0 == 0) goto L_0x09ec
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 != 0) goto L_0x09ec
            if (r13 == 0) goto L_0x09ec
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 != 0) goto L_0x09ec
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSending()
            if (r0 == 0) goto L_0x09a8
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r5 = 1
            r1.drawClockIcon = r5
            r1.drawErrorIcon = r3
            goto L_0x09f5
        L_0x09a8:
            r3 = 0
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSendError()
            if (r0 == 0) goto L_0x09bf
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClockIcon = r3
            r5 = 1
            r1.drawErrorIcon = r5
            r1.drawCount = r3
            r1.drawMentionIcon = r3
            goto L_0x09f5
        L_0x09bf:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isSent()
            if (r0 == 0) goto L_0x09f5
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message
            boolean r0 = r0.isUnread()
            if (r0 == 0) goto L_0x09e0
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x09de
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r1.chat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x09de
            goto L_0x09e0
        L_0x09de:
            r0 = 0
            goto L_0x09e1
        L_0x09e0:
            r0 = 1
        L_0x09e1:
            r1.drawCheck1 = r0
            r3 = 1
            r1.drawCheck2 = r3
            r3 = 0
            r1.drawClockIcon = r3
            r1.drawErrorIcon = r3
            goto L_0x09f5
        L_0x09ec:
            r3 = 0
            r1.drawCheck1 = r3
            r1.drawCheck2 = r3
            r1.drawClockIcon = r3
            r1.drawErrorIcon = r3
        L_0x09f5:
            int r0 = r1.dialogsType
            if (r0 != 0) goto L_0x0a17
            int r0 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            r5 = r2
            long r2 = r1.currentDialogId
            r19 = r5
            r5 = 1
            boolean r0 = r0.isProxyDialog(r2, r5)
            if (r0 == 0) goto L_0x0a19
            r1.drawPinBackground = r5
            r0 = 2131694551(0x7f0f13d7, float:1.9018262E38)
            java.lang.String r2 = "UseProxySponsor"
            java.lang.String r6 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            goto L_0x0a19
        L_0x0a17:
            r19 = r2
        L_0x0a19:
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0a28
            r0 = 2131689868(0x7f0f018c, float:1.9008764E38)
            java.lang.String r2 = "ArchivedChats"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            r5 = r0
            goto L_0x0a5d
        L_0x0a28:
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0a2f
            java.lang.String r5 = r0.title
            goto L_0x0a4d
        L_0x0a2f:
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            if (r0 == 0) goto L_0x0a4b
            boolean r0 = im.bclpbkiauv.messenger.UserObject.isUserSelf(r0)
            if (r0 == 0) goto L_0x0a44
            r0 = 2131693693(0x7f0f107d, float:1.9016522E38)
            java.lang.String r2 = "SavedMessages"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            goto L_0x0a4d
        L_0x0a44:
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            java.lang.String r5 = im.bclpbkiauv.messenger.UserObject.getName(r0)
            goto L_0x0a4d
        L_0x0a4b:
            r5 = r23
        L_0x0a4d:
            int r0 = r5.length()
            if (r0 != 0) goto L_0x0a5d
            r0 = 2131691577(0x7f0f0839, float:1.901223E38)
            java.lang.String r2 = "HiddenName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            r5 = r0
        L_0x0a5d:
            if (r15 == 0) goto L_0x0a9f
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_timePaint
            float r0 = r0.measureText(r6)
            double r2 = (double) r0
            double r2 = java.lang.Math.ceil(r2)
            int r0 = (int) r2
            android.text.StaticLayout r2 = new android.text.StaticLayout
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_timePaint
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r2
            r27 = r6
            r29 = r0
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)
            r1.timeLayout = r2
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0a95
            int r2 = r44.getMeasuredWidth()
            r3 = 1097859072(0x41700000, float:15.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            int r2 = r2 - r0
            r1.timeLeft = r2
            goto L_0x0a9d
        L_0x0a95:
            r2 = 1097859072(0x41700000, float:15.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.timeLeft = r2
        L_0x0a9d:
            r2 = r0
            goto L_0x0aa7
        L_0x0a9f:
            r0 = 0
            r2 = 0
            r1.timeLayout = r2
            r2 = 0
            r1.timeLeft = r2
            r2 = r0
        L_0x0aa7:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0abc
            int r0 = r44.getMeasuredWidth()
            int r3 = r1.nameLeft
            int r0 = r0 - r3
            r3 = 1096810496(0x41600000, float:14.0)
            int r23 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r23
            int r0 = r0 - r2
            goto L_0x0ad0
        L_0x0abc:
            int r0 = r44.getMeasuredWidth()
            int r3 = r1.nameLeft
            int r0 = r0 - r3
            r3 = 1117388800(0x429a0000, float:77.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            int r0 = r0 - r2
            int r3 = r1.nameLeft
            int r3 = r3 + r2
            r1.nameLeft = r3
        L_0x0ad0:
            boolean r3 = r1.dialogMuted
            r23 = 1086324736(0x40c00000, float:6.0)
            if (r3 == 0) goto L_0x0afc
            boolean r3 = r1.drawVerifiedIcon
            if (r3 != 0) goto L_0x0afc
            boolean r3 = r1.drawScam
            if (r3 != 0) goto L_0x0afc
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            android.graphics.drawable.Drawable r24 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r24 = r24.getIntrinsicWidth()
            int r3 = r3 + r24
            int r0 = r0 - r3
            boolean r24 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r24 == 0) goto L_0x0af7
            r24 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r3
            r1.nameLeft = r0
            goto L_0x0af9
        L_0x0af7:
            r24 = r0
        L_0x0af9:
            r0 = r24
            goto L_0x0b3f
        L_0x0afc:
            boolean r3 = r1.drawVerifiedIcon
            if (r3 == 0) goto L_0x0b1e
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            android.graphics.drawable.Drawable r24 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r24 = r24.getIntrinsicWidth()
            int r3 = r3 + r24
            int r0 = r0 - r3
            boolean r24 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r24 == 0) goto L_0x0b19
            r24 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r3
            r1.nameLeft = r0
            goto L_0x0b1b
        L_0x0b19:
            r24 = r0
        L_0x0b1b:
            r0 = r24
            goto L_0x0b3f
        L_0x0b1e:
            boolean r3 = r1.drawScam
            if (r3 == 0) goto L_0x0b3f
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            im.bclpbkiauv.ui.components.ScamDrawable r24 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r24 = r24.getIntrinsicWidth()
            int r3 = r3 + r24
            int r0 = r0 - r3
            boolean r24 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r24 == 0) goto L_0x0b3b
            r24 = r0
            int r0 = r1.nameLeft
            int r0 = r0 + r3
            r1.nameLeft = r0
            goto L_0x0b3d
        L_0x0b3b:
            r24 = r0
        L_0x0b3d:
            r0 = r24
        L_0x0b3f:
            r24 = r2
            r3 = 1094713344(0x41400000, float:12.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = java.lang.Math.max(r2, r0)
            r41 = r6
            r3 = 32
            r6 = 10
            java.lang.String r0 = r5.replace(r6, r3)     // Catch:{ Exception -> 0x0b80 }
            android.text.TextPaint r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0b80 }
            r6 = 1094713344(0x41400000, float:12.0)
            int r26 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)     // Catch:{ Exception -> 0x0b80 }
            int r6 = r2 - r26
            float r6 = (float) r6
            r42 = r5
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0b7e }
            java.lang.CharSequence r27 = android.text.TextUtils.ellipsize(r0, r3, r6, r5)     // Catch:{ Exception -> 0x0b7e }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0b7e }
            android.text.TextPaint r28 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint     // Catch:{ Exception -> 0x0b7e }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0b7e }
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r0
            r29 = r2
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x0b7e }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x0b7e }
            goto L_0x0b86
        L_0x0b7e:
            r0 = move-exception
            goto L_0x0b83
        L_0x0b80:
            r0 = move-exception
            r42 = r5
        L_0x0b83:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0b86:
            float r0 = r1.topOffset
            r3 = 0
            r5 = 1111752704(0x42440000, float:49.0)
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0b9e
            int r0 = r44.getMeasuredHeight()
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r6
            float r0 = (float) r0
            r6 = 1073741824(0x40000000, float:2.0)
            float r0 = r0 / r6
            r1.topOffset = r0
        L_0x0b9e:
            boolean r0 = r1.useForceThreeLines
            r6 = 1106771968(0x41f80000, float:31.0)
            r34 = 1092616192(0x41200000, float:10.0)
            if (r0 != 0) goto L_0x0c3b
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0bae
            r43 = r10
            goto L_0x0c3d
        L_0x0bae:
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            float r0 = (float) r0
            float r3 = r1.topOffset
            float r0 = r0 + r3
            int r0 = (int) r0
            r1.messageNameTop = r0
            int r0 = (int) r3
            r1.timeTop = r0
            int r0 = r44.getMeasuredHeight()
            r3 = 1103626240(0x41c80000, float:25.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            r1.errorTop = r0
            int r0 = r44.getMeasuredHeight()
            r3 = 1105199104(0x41e00000, float:28.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            r1.recorderTop = r0
            int r0 = r44.getMeasuredHeight()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r3
            r1.countTop = r0
            int r0 = r44.getMeasuredHeight()
            r3 = 1103626240(0x41c80000, float:25.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            r1.clockDrawTop = r0
            int r0 = r44.getMeasuredWidth()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r3
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0c0c
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            r3 = 1094713344(0x41400000, float:12.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.avatarLeft = r6
            goto L_0x0c21
        L_0x0c0c:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            int r3 = r44.getMeasuredWidth()
            r6 = 1115684864(0x42800000, float:64.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            r1.avatarLeft = r3
        L_0x0c21:
            im.bclpbkiauv.messenger.ImageReceiver r3 = r1.avatarImage
            int r6 = r1.avatarLeft
            r20 = r0
            float r0 = r1.topOffset
            int r0 = (int) r0
            r43 = r10
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r3.setImageCoords(r6, r0, r10, r5)
            r0 = r20
            goto L_0x0cc4
        L_0x0c3b:
            r43 = r10
        L_0x0c3d:
            r3 = 1101004800(0x41a00000, float:20.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r0 = (float) r0
            float r3 = r1.topOffset
            float r0 = r0 + r3
            int r0 = (int) r0
            r1.messageNameTop = r0
            int r0 = (int) r3
            r1.timeTop = r0
            int r0 = r44.getMeasuredHeight()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r3
            r1.errorTop = r0
            r0 = 1105199104(0x41e00000, float:28.0)
            float r3 = r1.topOffset
            float r3 = r3 + r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.recorderTop = r0
            int r0 = r44.getMeasuredHeight()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r3
            r1.countTop = r0
            int r0 = r44.getMeasuredHeight()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r0 = r0 - r3
            r1.clockDrawTop = r0
            int r0 = r44.getMeasuredWidth()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            int r0 = r0 - r3
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0c97
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.avatarLeft = r3
            goto L_0x0cae
        L_0x0c97:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.messageNameLeft = r3
            r1.messageLeft = r3
            int r3 = r44.getMeasuredWidth()
            r6 = 1115947008(0x42840000, float:66.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r3 = r3 - r6
            r1.avatarLeft = r3
        L_0x0cae:
            im.bclpbkiauv.messenger.ImageReceiver r3 = r1.avatarImage
            int r6 = r1.avatarLeft
            float r10 = r1.topOffset
            int r10 = (int) r10
            r20 = r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r3.setImageCoords(r6, r10, r0, r5)
            r0 = r20
        L_0x0cc4:
            boolean r3 = r1.drawPinIcon
            if (r3 == 0) goto L_0x0d1c
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r3 = r3.getIntrinsicWidth()
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            int r3 = r3 + r5
            int r0 = r0 - r3
            int r5 = r44.getMeasuredHeight()
            r6 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.pinTop = r5
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0d07
            int r5 = r44.getMeasuredWidth()
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 - r6
            r6 = 1096810496(0x41600000, float:14.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.recorderLeft = r5
            int r5 = r44.getMeasuredWidth()
            r6 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.pinLeft = r5
            goto L_0x0d1c
        L_0x0d07:
            r6 = 1096810496(0x41600000, float:14.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r1.recorderLeft = r5
            r5 = 0
            r1.pinLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r3
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r3
            r1.messageNameLeft = r5
        L_0x0d1c:
            boolean r3 = r1.drawErrorIcon
            if (r3 == 0) goto L_0x0d44
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r3
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0d30
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.errorLeft = r5
            goto L_0x0d38
        L_0x0d30:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r1.errorLeft = r5
        L_0x0d38:
            int r5 = r1.messageLeft
            int r5 = r5 + r3
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r3
            r1.messageNameLeft = r5
            r3 = r0
            goto L_0x0d6d
        L_0x0d44:
            boolean r3 = r1.drawClockIcon
            if (r3 == 0) goto L_0x0d6c
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            int r0 = r0 - r3
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0d58
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r1.clockDrawLeft = r5
            goto L_0x0d60
        L_0x0d58:
            r5 = 1093664768(0x41300000, float:11.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r1.clockDrawLeft = r5
        L_0x0d60:
            int r5 = r1.messageLeft
            int r5 = r5 + r3
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r3
            r1.messageNameLeft = r5
            r3 = r0
            goto L_0x0d6d
        L_0x0d6c:
            r3 = r0
        L_0x0d6d:
            if (r7 != 0) goto L_0x0d71
            if (r8 == 0) goto L_0x0eab
        L_0x0d71:
            if (r7 == 0) goto L_0x0e15
            java.math.BigDecimal r0 = new java.math.BigDecimal     // Catch:{ Exception -> 0x0d8b }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0d8b }
            java.math.BigDecimal r5 = new java.math.BigDecimal     // Catch:{ Exception -> 0x0d8b }
            java.lang.String r6 = "10"
            r5.<init>(r6)     // Catch:{ Exception -> 0x0d8b }
            int r0 = r0.compareTo(r5)     // Catch:{ Exception -> 0x0d8b }
            if (r0 <= 0) goto L_0x0d87
            r0 = 1
            goto L_0x0d88
        L_0x0d87:
            r0 = 0
        L_0x0d88:
            r1.countIsBiggerThanTen = r0     // Catch:{ Exception -> 0x0d8b }
            goto L_0x0da4
        L_0x0d8b:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "FmtDialogCell ----> buildLayout countIsBiggerThanTen e: "
            r5.append(r6)
            java.lang.String r6 = r0.toString()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            im.bclpbkiauv.messenger.FileLog.e((java.lang.String) r5)
        L_0x0da4:
            int r0 = r1.countTop
            boolean r5 = r1.countIsBiggerThanTen
            if (r5 == 0) goto L_0x0db1
            r5 = 1077936128(0x40400000, float:3.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            goto L_0x0db2
        L_0x0db1:
            r5 = 0
        L_0x0db2:
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
            r5 = 1101004800(0x41a00000, float:20.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 + r6
            int r3 = r3 - r0
            boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r6 != 0) goto L_0x0e01
            int r6 = r44.getMeasuredWidth()
            int r10 = r1.countWidth
            int r6 = r6 - r10
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r6 = r6 - r10
            r1.countLeft = r6
            goto L_0x0e11
        L_0x0e01:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            r1.countLeft = r6
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x0e11:
            r5 = 1
            r1.drawCount = r5
            goto L_0x0e18
        L_0x0e15:
            r5 = 0
            r1.countWidth = r5
        L_0x0e18:
            if (r8 == 0) goto L_0x0eab
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0e50
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
            goto L_0x0e58
        L_0x0e50:
            r0 = 1092091904(0x41180000, float:9.5)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r1.mentionWidth = r0
        L_0x0e58:
            int r0 = r1.mentionWidth
            if (r7 == 0) goto L_0x0e61
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            goto L_0x0e67
        L_0x0e61:
            r5 = 1101004800(0x41a00000, float:20.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
        L_0x0e67:
            int r0 = r0 + r5
            int r3 = r3 - r0
            boolean r5 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0e8a
            int r5 = r44.getMeasuredWidth()
            int r6 = r1.mentionWidth
            int r5 = r5 - r6
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            int r5 = r5 - r6
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x0e85
            r10 = 1104150528(0x41d00000, float:26.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r6 = r6 + r10
            goto L_0x0e86
        L_0x0e85:
            r6 = 0
        L_0x0e86:
            int r5 = r5 - r6
            r1.mentionLeft = r5
            goto L_0x0ea8
        L_0x0e8a:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r34)
            int r6 = r1.countWidth
            if (r6 == 0) goto L_0x0e9a
            r10 = 1084227584(0x40a00000, float:5.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r6 = r6 + r10
            goto L_0x0e9b
        L_0x0e9a:
            r6 = 0
        L_0x0e9b:
            int r5 = r5 + r6
            r1.mentionLeft = r5
            int r5 = r1.messageLeft
            int r5 = r5 + r0
            r1.messageLeft = r5
            int r5 = r1.messageNameLeft
            int r5 = r5 + r0
            r1.messageNameLeft = r5
        L_0x0ea8:
            r5 = 1
            r1.drawMentionIcon = r5
        L_0x0eab:
            if (r12 == 0) goto L_0x0ee5
            if (r9 != 0) goto L_0x0eb1
            java.lang.String r9 = ""
        L_0x0eb1:
            java.lang.String r0 = r9.toString()
            int r5 = r0.length()
            r6 = 150(0x96, float:2.1E-43)
            if (r5 <= r6) goto L_0x0ec2
            r5 = 0
            java.lang.String r0 = r0.substring(r5, r6)
        L_0x0ec2:
            boolean r5 = r1.useForceThreeLines
            if (r5 != 0) goto L_0x0eca
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r5 == 0) goto L_0x0ecc
        L_0x0eca:
            if (r4 == 0) goto L_0x0ed4
        L_0x0ecc:
            r5 = 32
            r6 = 10
            java.lang.String r0 = r0.replace(r6, r5)
        L_0x0ed4:
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.graphics.Paint$FontMetricsInt r5 = r5.getFontMetricsInt()
            r6 = 1099431936(0x41880000, float:17.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r10 = 0
            java.lang.CharSequence r9 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r0, r5, r6, r10)
        L_0x0ee5:
            r5 = 1094713344(0x41400000, float:12.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r3 = java.lang.Math.max(r0, r3)
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0ef7
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0f2f
        L_0x0ef7:
            if (r4 == 0) goto L_0x0f2f
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0f02
            int r0 = r1.currentDialogFolderDialogsCount
            r5 = 1
            if (r0 != r5) goto L_0x0f2f
        L_0x0f02:
            android.text.TextPaint r32 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint     // Catch:{ Exception -> 0x0f1d }
            android.text.Layout$Alignment r34 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0f1d }
            r35 = 1065353216(0x3f800000, float:1.0)
            r36 = 0
            r37 = 0
            android.text.TextUtils$TruncateAt r38 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x0f1d }
            r40 = 1
            r31 = r4
            r33 = r3
            r39 = r3
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r31, r32, r33, r34, r35, r36, r37, r38, r39, r40)     // Catch:{ Exception -> 0x0f1d }
            r1.messageNameLayout = r0     // Catch:{ Exception -> 0x0f1d }
            goto L_0x0f21
        L_0x0f1d:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0f21:
            r0 = 1108344832(0x42100000, float:36.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
            goto L_0x0f56
        L_0x0f2f:
            r5 = 0
            r1.messageNameLayout = r5
            boolean r0 = r1.useForceThreeLines
            if (r0 != 0) goto L_0x0f49
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r0 == 0) goto L_0x0f3b
            goto L_0x0f49
        L_0x0f3b:
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
            goto L_0x0f56
        L_0x0f49:
            r0 = 1107296256(0x42000000, float:32.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            float r5 = r1.topOffset
            float r0 = r0 + r5
            int r0 = (int) r0
            r1.messageTop = r0
        L_0x0f56:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x114c }
            if (r0 != 0) goto L_0x0f64
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x0f5f }
            if (r0 == 0) goto L_0x0f76
            goto L_0x0f64
        L_0x0f5f:
            r0 = move-exception
            r22 = r7
            goto L_0x1151
        L_0x0f64:
            int r0 = r1.currentDialogFolderId     // Catch:{ Exception -> 0x114c }
            if (r0 == 0) goto L_0x0f76
            int r0 = r1.currentDialogFolderDialogsCount     // Catch:{ Exception -> 0x0f5f }
            r5 = 1
            if (r0 <= r5) goto L_0x0f76
            r0 = r4
            r4 = 0
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint     // Catch:{ Exception -> 0x0f5f }
            r11 = r5
            r22 = r7
            goto L_0x10c9
        L_0x0f76:
            boolean r0 = r1.useForceThreeLines     // Catch:{ Exception -> 0x114c }
            if (r0 != 0) goto L_0x0f7e
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x0f5f }
            if (r0 == 0) goto L_0x0f80
        L_0x0f7e:
            if (r4 == 0) goto L_0x10c2
        L_0x0f80:
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x114c }
            int r0 = r0.type     // Catch:{ Exception -> 0x114c }
            r5 = 105(0x69, float:1.47E-43)
            if (r0 != r5) goto L_0x10a7
            im.bclpbkiauv.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x114c }
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x114c }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x114c }
            im.bclpbkiauv.tgnet.TLRPCContacts$TL_messageMediaSysNotify r0 = (im.bclpbkiauv.tgnet.TLRPCContacts.TL_messageMediaSysNotify) r0     // Catch:{ Exception -> 0x114c }
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r5 = r0.data     // Catch:{ Exception -> 0x114c }
            java.lang.String r5 = im.bclpbkiauv.tgnet.TLJsonResolve.getData(r5)     // Catch:{ Exception -> 0x114c }
            java.lang.Class<im.bclpbkiauv.javaBean.ChatFCAttentionBean> r6 = im.bclpbkiauv.javaBean.ChatFCAttentionBean.class
            java.lang.Object r6 = com.blankj.utilcode.util.GsonUtils.fromJson((java.lang.String) r5, r6)     // Catch:{ Exception -> 0x114c }
            im.bclpbkiauv.javaBean.ChatFCAttentionBean r6 = (im.bclpbkiauv.javaBean.ChatFCAttentionBean) r6     // Catch:{ Exception -> 0x114c }
            r10 = r14
            int r16 = im.bclpbkiauv.messenger.UserConfig.selectedAccount     // Catch:{ Exception -> 0x114c }
            r17 = r4
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r16)     // Catch:{ Exception -> 0x10a0 }
            r16 = r5
            im.bclpbkiauv.javaBean.ChatFCAttentionBean$MsgDataBean r5 = r6.interact_msg     // Catch:{ Exception -> 0x10a0 }
            int r5 = r5.with_id     // Catch:{ Exception -> 0x10a0 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x10a0 }
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)     // Catch:{ Exception -> 0x10a0 }
            java.lang.String r5 = im.bclpbkiauv.messenger.UserObject.getName(r4)     // Catch:{ Exception -> 0x10a0 }
            r20 = r4
            int r4 = r5.length()     // Catch:{ Exception -> 0x10a0 }
            r21 = r6
            r6 = 13
            if (r4 <= r6) goto L_0x0fe1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x10a0 }
            r4.<init>()     // Catch:{ Exception -> 0x10a0 }
            r6 = 12
            r22 = r7
            r7 = 0
            java.lang.String r6 = r5.substring(r7, r6)     // Catch:{ Exception -> 0x10bd }
            r4.append(r6)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r6 = "..."
            r4.append(r6)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x10bd }
            goto L_0x0fe4
        L_0x0fe1:
            r22 = r7
            r4 = r5
        L_0x0fe4:
            int r6 = r0.business_code     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "#90C6FF"
            switch(r6) {
                case 4: goto L_0x1081;
                case 5: goto L_0x1064;
                case 6: goto L_0x1047;
                case 7: goto L_0x102a;
                case 8: goto L_0x100c;
                case 9: goto L_0x0fee;
                default: goto L_0x0feb;
            }
        L_0x0feb:
            r0 = r10
            goto L_0x109d
        L_0x0fee:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "提醒了你"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
            goto L_0x109d
        L_0x100c:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "关注了你"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
            goto L_0x109d
        L_0x102a:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "@了你"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
            goto L_0x109d
        L_0x1047:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "给你点了个赞"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
            goto L_0x109d
        L_0x1064:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "回复了你"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
            goto L_0x109d
        L_0x1081:
            com.blankj.utilcode.util.SpanUtils r6 = new com.blankj.utilcode.util.SpanUtils     // Catch:{ Exception -> 0x10bd }
            r6.<init>()     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r4)     // Catch:{ Exception -> 0x10bd }
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x10bd }
            com.blankj.utilcode.util.SpanUtils r6 = r6.setForegroundColor(r7)     // Catch:{ Exception -> 0x10bd }
            java.lang.String r7 = "评论了你"
            com.blankj.utilcode.util.SpanUtils r6 = r6.append(r7)     // Catch:{ Exception -> 0x10bd }
            android.text.SpannableStringBuilder r6 = r6.create()     // Catch:{ Exception -> 0x10bd }
            r0 = r6
        L_0x109d:
            r4 = r17
            goto L_0x10c9
        L_0x10a0:
            r0 = move-exception
            r22 = r7
            r4 = r17
            goto L_0x1151
        L_0x10a7:
            r17 = r4
            r22 = r7
            r4 = 1094713344(0x41400000, float:12.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x10bd }
            int r0 = r3 - r0
            float r0 = (float) r0     // Catch:{ Exception -> 0x10bd }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x10bd }
            java.lang.CharSequence r0 = android.text.TextUtils.ellipsize(r9, r11, r0, r4)     // Catch:{ Exception -> 0x10bd }
            r4 = r17
            goto L_0x10c9
        L_0x10bd:
            r0 = move-exception
            r4 = r17
            goto L_0x1151
        L_0x10c2:
            r17 = r4
            r22 = r7
            r0 = r9
            r4 = r17
        L_0x10c9:
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media     // Catch:{ Exception -> 0x114a }
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaShareContact     // Catch:{ Exception -> 0x114a }
            if (r5 != 0) goto L_0x10ea
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media     // Catch:{ Exception -> 0x114a }
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesPayBillOverMedia     // Catch:{ Exception -> 0x114a }
            if (r5 != 0) goto L_0x10ea
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r5.messageOwner     // Catch:{ Exception -> 0x114a }
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r5 = r5.media     // Catch:{ Exception -> 0x114a }
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPCContacts.TL_messageMediaSysNotify     // Catch:{ Exception -> 0x114a }
            if (r5 == 0) goto L_0x10e8
            goto L_0x10ea
        L_0x10e8:
            r6 = 1
            goto L_0x1102
        L_0x10ea:
            android.text.TextPaint r5 = new android.text.TextPaint     // Catch:{ Exception -> 0x114a }
            r6 = 1
            r5.<init>(r6)     // Catch:{ Exception -> 0x114a }
            float r7 = r11.getTextSize()     // Catch:{ Exception -> 0x114a }
            r5.setTextSize(r7)     // Catch:{ Exception -> 0x114a }
            java.lang.String r7 = "#999999"
            int r7 = android.graphics.Color.parseColor(r7)     // Catch:{ Exception -> 0x114a }
            r5.setColor(r7)     // Catch:{ Exception -> 0x114a }
            r7 = r5
            r11 = r7
        L_0x1102:
            boolean r5 = r1.useForceThreeLines     // Catch:{ Exception -> 0x114a }
            if (r5 != 0) goto L_0x1123
            boolean r5 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout     // Catch:{ Exception -> 0x114a }
            if (r5 == 0) goto L_0x110b
            goto L_0x1123
        L_0x110b:
            android.text.StaticLayout r5 = new android.text.StaticLayout     // Catch:{ Exception -> 0x114a }
            android.text.Layout$Alignment r30 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x114a }
            r31 = 1065353216(0x3f800000, float:1.0)
            r32 = 0
            r33 = 0
            r26 = r5
            r27 = r0
            r28 = r11
            r29 = r3
            r26.<init>(r27, r28, r29, r30, r31, r32, r33)     // Catch:{ Exception -> 0x114a }
            r1.messageLayout = r5     // Catch:{ Exception -> 0x114a }
            goto L_0x1149
        L_0x1123:
            android.text.Layout$Alignment r29 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x114a }
            r30 = 1065353216(0x3f800000, float:1.0)
            r5 = 1065353216(0x3f800000, float:1.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x114a }
            float r5 = (float) r5     // Catch:{ Exception -> 0x114a }
            r32 = 0
            android.text.TextUtils$TruncateAt r33 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x114a }
            if (r4 == 0) goto L_0x1137
            r35 = 1
            goto L_0x1139
        L_0x1137:
            r35 = 2
        L_0x1139:
            r26 = r0
            r27 = r11
            r28 = r3
            r31 = r5
            r34 = r3
            android.text.StaticLayout r5 = im.bclpbkiauv.ui.components.StaticLayoutEx.createStaticLayout(r26, r27, r28, r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x114a }
            r1.messageLayout = r5     // Catch:{ Exception -> 0x114a }
        L_0x1149:
            goto L_0x1154
        L_0x114a:
            r0 = move-exception
            goto L_0x1151
        L_0x114c:
            r0 = move-exception
            r17 = r4
            r22 = r7
        L_0x1151:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x1154:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x1268
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x11ea
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x11ea
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            float r0 = r0.getLineLeft(r5)
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r5)
            double r5 = (double) r6
            double r5 = java.lang.Math.ceil(r5)
            boolean r7 = r1.dialogMuted
            if (r7 == 0) goto L_0x119c
            boolean r7 = r1.drawVerifiedIcon
            if (r7 != 0) goto L_0x119c
            boolean r7 = r1.drawScam
            if (r7 != 0) goto L_0x119c
            int r7 = r1.nameLeft
            r10 = r8
            double r7 = (double) r7
            r14 = r9
            r16 = r10
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            double r9 = (double) r9
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x11d6
        L_0x119c:
            r16 = r8
            r14 = r9
            boolean r7 = r1.drawVerifiedIcon
            if (r7 == 0) goto L_0x11bb
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            double r9 = (double) r9
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
            goto L_0x11d6
        L_0x11bb:
            boolean r7 = r1.drawScam
            if (r7 == 0) goto L_0x11d6
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            double r9 = (double) r9
            double r7 = r7 - r9
            im.bclpbkiauv.ui.components.ScamDrawable r9 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
        L_0x11d6:
            r7 = 0
            int r8 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r8 != 0) goto L_0x11ed
            double r7 = (double) r2
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x11ed
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r7 = (int) r7
            r1.nameLeft = r7
            goto L_0x11ed
        L_0x11ea:
            r16 = r8
            r14 = r9
        L_0x11ed:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x1235
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x1232
            r5 = 2147483647(0x7fffffff, float:NaN)
            r6 = 0
        L_0x11fb:
            if (r6 >= r0) goto L_0x1225
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r6)
            r8 = 0
            int r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r9 != 0) goto L_0x1221
            android.text.StaticLayout r8 = r1.messageLayout
            float r8 = r8.getLineWidth(r6)
            double r8 = (double) r8
            double r8 = java.lang.Math.ceil(r8)
            r17 = r11
            double r10 = (double) r3
            double r10 = r10 - r8
            int r10 = (int) r10
            int r5 = java.lang.Math.min(r5, r10)
            int r6 = r6 + 1
            r11 = r17
            goto L_0x11fb
        L_0x1221:
            r17 = r11
            r5 = 0
            goto L_0x1227
        L_0x1225:
            r17 = r11
        L_0x1227:
            r6 = 2147483647(0x7fffffff, float:NaN)
            if (r5 == r6) goto L_0x1237
            int r6 = r1.messageLeft
            int r6 = r6 + r5
            r1.messageLeft = r6
            goto L_0x1237
        L_0x1232:
            r17 = r11
            goto L_0x1237
        L_0x1235:
            r17 = r11
        L_0x1237:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x12f0
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12f0
            android.text.StaticLayout r0 = r1.messageNameLayout
            r5 = 0
            float r0 = r0.getLineLeft(r5)
            r6 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x12f0
            android.text.StaticLayout r6 = r1.messageNameLayout
            float r5 = r6.getLineWidth(r5)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r3
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x12f0
            int r7 = r1.messageNameLeft
            double r7 = (double) r7
            double r9 = (double) r3
            double r9 = r9 - r5
            double r7 = r7 + r9
            int r7 = (int) r7
            r1.messageNameLeft = r7
            goto L_0x12f0
        L_0x1268:
            r16 = r8
            r14 = r9
            r17 = r11
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x12b5
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12b5
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            float r0 = r0.getLineRight(r5)
            float r6 = (float) r2
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x129c
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r5)
            double r5 = (double) r6
            double r5 = java.lang.Math.ceil(r5)
            double r7 = (double) r2
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x129c
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r2
            double r9 = r9 - r5
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameLeft = r7
        L_0x129c:
            boolean r5 = r1.dialogMuted
            if (r5 != 0) goto L_0x12a8
            boolean r5 = r1.drawVerifiedIcon
            if (r5 != 0) goto L_0x12a8
            boolean r5 = r1.drawScam
            if (r5 == 0) goto L_0x12b5
        L_0x12a8:
            int r5 = r1.nameLeft
            float r5 = (float) r5
            float r5 = r5 + r0
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r23)
            float r6 = (float) r6
            float r5 = r5 + r6
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x12b5:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x12d8
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12d8
            r5 = 1325400064(0x4f000000, float:2.14748365E9)
            r6 = 0
        L_0x12c2:
            if (r6 >= r0) goto L_0x12d1
            android.text.StaticLayout r7 = r1.messageLayout
            float r7 = r7.getLineLeft(r6)
            float r5 = java.lang.Math.min(r5, r7)
            int r6 = r6 + 1
            goto L_0x12c2
        L_0x12d1:
            int r6 = r1.messageLeft
            float r6 = (float) r6
            float r6 = r6 - r5
            int r6 = (int) r6
            r1.messageLeft = r6
        L_0x12d8:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x12f0
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x12f0
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            android.text.StaticLayout r5 = r1.messageNameLayout
            r6 = 0
            float r5 = r5.getLineLeft(r6)
            float r0 = r0 - r5
            int r0 = (int) r0
            r1.messageNameLeft = r0
        L_0x12f0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cell.FmtDialogCell.buildLayout():void");
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
        ArrayList<TLRPC.Dialog> dialogsArray = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, frozen);
        if (this.index < dialogsArray.size()) {
            TLRPC.Dialog dialog = dialogsArray.get(this.index);
            boolean z = true;
            TLRPC.Dialog nextDialog = this.index + 1 < dialogsArray.size() ? dialogsArray.get(this.index + 1) : null;
            TLRPC.DraftMessage newDraftMessage = MediaDataController.getInstance(this.currentAccount).getDraft(this.currentDialogId);
            if (this.currentDialogFolderId != 0) {
                newMessageObject = findFolderTopMessage();
            } else {
                newMessageObject = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (newMessageObject != null) {
                    newMessageObject.generateCaption();
                }
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
        ArrayList<TLRPC.Dialog> dialogs = DialogsFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.currentDialogFolderId, false);
        MessageObject maxMessage = null;
        if (!dialogs.isEmpty()) {
            int N = dialogs.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Dialog dialog = dialogs.get(a);
                MessageObject object = MessagesController.getInstance(this.currentAccount).dialogMessage.get(dialog.id);
                if (object != null && (maxMessage == null || object.messageOwner.date > maxMessage.messageOwner.date)) {
                    object.generateCaption();
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
                if (messageObject3 != null) {
                    messageObject3.generateCaption();
                }
                MessageObject messageObject4 = this.message;
                this.lastUnreadState = messageObject4 != null && messageObject4.isUnread();
                this.unreadCount = dialog2.unread_count;
                this.markUnread = dialog2.unread_mark;
                this.mentionCount = dialog2.unread_mentions_count;
                MessageObject messageObject5 = this.message;
                this.currentEditDate = messageObject5 != null ? messageObject5.messageOwner.edit_date : 0;
                this.lastMessageDate = dialog2.last_message_date;
                this.drawPinIcon = this.currentDialogFolderId == 0 && dialog2.pinned;
                MessageObject messageObject6 = this.message;
                if (messageObject6 != null) {
                    this.lastSendState = messageObject6.messageOwner.send_state;
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
                MessageObject messageObject7 = this.message;
                if (messageObject7 != null && this.lastUnreadState != messageObject7.isUnread()) {
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
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02f6  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x034a  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x03a2  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0417  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0439  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x049b  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x04c3  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0528  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x06b6  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x06dd  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x06e4  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x07db  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x07dd  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x07f6  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x081e  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x084d  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0866  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x08df  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0935  */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x094a  */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0961  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0987  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x09b1  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x09c5  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x09ee  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0a12  */
    /* JADX WARNING: Removed duplicated region for block: B:380:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01fe  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0228  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0272  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x028c  */
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
            r7 = 1065353216(0x3f800000, float:1.0)
            if (r2 == 0) goto L_0x015a
            boolean r2 = r1.checkBoxVisible
            if (r2 != 0) goto L_0x0083
            boolean r2 = r1.checkBoxAnimationInProgress
            if (r2 == 0) goto L_0x015a
        L_0x0083:
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
            if (r3 == 0) goto L_0x00a6
            float r3 = r1.checkBoxAnimationProgress
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 == 0) goto L_0x00b0
        L_0x00a6:
            boolean r3 = r1.checkBoxVisible
            if (r3 != 0) goto L_0x00b2
            float r3 = r1.checkBoxAnimationProgress
            int r3 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x00b2
        L_0x00b0:
            r1.checkBoxAnimationInProgress = r15
        L_0x00b2:
            boolean r3 = r1.checkBoxVisible
            if (r3 == 0) goto L_0x00b9
            im.bclpbkiauv.ui.components.CubicBezierInterpolator r3 = im.bclpbkiauv.ui.components.CubicBezierInterpolator.EASE_OUT
            goto L_0x00bb
        L_0x00b9:
            im.bclpbkiauv.ui.components.CubicBezierInterpolator r3 = im.bclpbkiauv.ui.components.CubicBezierInterpolator.EASE_IN
        L_0x00bb:
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
            r6 = 2
            if (r5 != r6) goto L_0x010e
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
            int r15 = r15 / r6
            int r6 = r2 / 2
            int r15 = r15 - r6
            r5.setBounds(r13, r15, r2, r2)
            boolean r5 = r1.checkBoxAnimationInProgress
            if (r5 == 0) goto L_0x015a
            long r5 = android.os.SystemClock.uptimeMillis()
            long r14 = r1.lastCheckBoxAnimationTime
            long r14 = r5 - r14
            r1.lastCheckBoxAnimationTime = r5
            boolean r13 = r1.checkBoxVisible
            r17 = 1128792064(0x43480000, float:200.0)
            if (r13 == 0) goto L_0x014a
            float r13 = r1.checkBoxAnimationProgress
            float r7 = (float) r14
            float r7 = r7 / r17
            float r13 = r13 + r7
            r1.checkBoxAnimationProgress = r13
            r7 = 1065353216(0x3f800000, float:1.0)
            int r13 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r13 <= 0) goto L_0x0148
            r1.checkBoxAnimationProgress = r7
        L_0x0148:
            r0 = 1
            goto L_0x015a
        L_0x014a:
            float r7 = r1.checkBoxAnimationProgress
            float r13 = (float) r11
            float r13 = r13 / r17
            float r7 = r7 - r13
            r1.checkBoxAnimationProgress = r7
            r13 = 0
            int r7 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r7 > 0) goto L_0x0159
            r1.checkBoxAnimationProgress = r13
        L_0x0159:
            r0 = 1
        L_0x015a:
            int r2 = r1.currentDialogFolderId
            java.lang.String r14 = "chats_pinnedOverlay"
            if (r2 == 0) goto L_0x0199
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.archiveHidden
            if (r2 == 0) goto L_0x0170
            float r2 = r1.archiveBackgroundProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x016d
            goto L_0x0170
        L_0x016d:
            r13 = 1065353216(0x3f800000, float:1.0)
            goto L_0x019b
        L_0x0170:
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            float r4 = r1.archiveBackgroundProgress
            r5 = 0
            r7 = 1065353216(0x3f800000, float:1.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.getOffsetColor(r5, r3, r4, r7)
            r2.setColor(r3)
            r3 = 0
            r4 = 0
            int r2 = r23.getMeasuredWidth()
            float r5 = (float) r2
            int r2 = r23.getMeasuredHeight()
            float r6 = (float) r2
            android.graphics.Paint r15 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            r2 = r24
            r13 = 1065353216(0x3f800000, float:1.0)
            r7 = r15
            r2.drawRect(r3, r4, r5, r6, r7)
            goto L_0x019d
        L_0x0199:
            r13 = 1065353216(0x3f800000, float:1.0)
        L_0x019b:
            boolean r2 = r1.drawPinIcon
        L_0x019d:
            float r2 = r1.translationX
            java.lang.String r15 = "windowBackgroundWhite"
            r3 = 1090519040(0x41000000, float:8.0)
            r4 = 0
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 != 0) goto L_0x01ae
            float r2 = r1.cornerProgress
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x0255
        L_0x01ae:
            r24.save()
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
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
            r8.drawRoundRect(r4, r5, r6, r7)
            int r4 = r1.currentDialogFolderId
            if (r4 == 0) goto L_0x0228
            boolean r4 = im.bclpbkiauv.messenger.SharedConfig.archiveHidden
            if (r4 == 0) goto L_0x01fe
            float r4 = r1.archiveBackgroundProgress
            r2 = 0
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0228
        L_0x01fe:
            android.graphics.Paint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            float r6 = r1.archiveBackgroundProgress
            r7 = 0
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.getOffsetColor(r7, r5, r6, r13)
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
            r8.drawRoundRect(r4, r5, r6, r7)
            goto L_0x0252
        L_0x0228:
            boolean r4 = r1.drawPinIcon
            if (r4 != 0) goto L_0x0230
            boolean r4 = r1.drawPinBackground
            if (r4 == 0) goto L_0x0252
        L_0x0230:
            android.graphics.Paint r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r5 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
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
            r8.drawRoundRect(r4, r5, r6, r7)
        L_0x0252:
            r24.restore()
        L_0x0255:
            float r4 = r1.translationX
            r5 = 1125515264(0x43160000, float:150.0)
            r2 = 0
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0272
            float r4 = r1.cornerProgress
            int r6 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r6 >= 0) goto L_0x0287
            float r6 = (float) r11
            float r6 = r6 / r5
            float r4 = r4 + r6
            r1.cornerProgress = r4
            int r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r4 <= 0) goto L_0x026f
            r1.cornerProgress = r13
        L_0x026f:
            r0 = 1
            r4 = r0
            goto L_0x0288
        L_0x0272:
            float r4 = r1.cornerProgress
            r2 = 0
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0287
            float r6 = (float) r11
            float r6 = r6 / r5
            float r4 = r4 - r6
            r1.cornerProgress = r4
            int r4 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x0284
            r1.cornerProgress = r2
        L_0x0284:
            r0 = 1
            r4 = r0
            goto L_0x0288
        L_0x0287:
            r4 = r0
        L_0x0288:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x02d6
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x02a0
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_nameArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x02c3
        L_0x02a0:
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r0 = r1.encryptedChat
            if (r0 == 0) goto L_0x02b4
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_secretName"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x02c3
        L_0x02b4:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            java.lang.String r7 = "chats_name"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x02c3:
            r24.save()
            int r0 = r1.nameLeft
            float r0 = (float) r0
            float r6 = r1.topOffset
            r8.translate(r0, r6)
            android.text.StaticLayout r0 = r1.nameLayout
            r0.draw(r8)
            r24.restore()
        L_0x02d6:
            android.text.StaticLayout r0 = r1.timeLayout
            if (r0 == 0) goto L_0x02f2
            int r0 = r1.currentDialogFolderId
            if (r0 != 0) goto L_0x02f2
            r24.save()
            int r0 = r1.timeLeft
            float r0 = (float) r0
            int r6 = r1.timeTop
            float r6 = (float) r6
            r8.translate(r0, r6)
            android.text.StaticLayout r0 = r1.timeLayout
            r0.draw(r8)
            r24.restore()
        L_0x02f2:
            android.text.StaticLayout r0 = r1.messageNameLayout
            if (r0 == 0) goto L_0x0346
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x030a
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_nameMessageArchived_threeLines"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x032d
        L_0x030a:
            im.bclpbkiauv.tgnet.TLRPC$DraftMessage r0 = r1.draftMessage
            if (r0 == 0) goto L_0x031e
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_draft"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x032d
        L_0x031e:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messageNamePaint
            java.lang.String r7 = "chats_nameMessage_threeLines"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x032d:
            r24.save()
            int r0 = r1.messageNameLeft
            float r0 = (float) r0
            int r6 = r1.messageNameTop
            float r6 = (float) r6
            r8.translate(r0, r6)
            android.text.StaticLayout r0 = r1.messageNameLayout     // Catch:{ Exception -> 0x033f }
            r0.draw(r8)     // Catch:{ Exception -> 0x033f }
            goto L_0x0343
        L_0x033f:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0343:
            r24.restore()
        L_0x0346:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x039a
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0372
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r1.chat
            if (r0 == 0) goto L_0x0362
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_nameMessageArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x0381
        L_0x0362:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_messageArchived"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
            goto L_0x0381
        L_0x0372:
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            java.lang.String r7 = "chats_message"
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            r6.linkColor = r7
            r0.setColor(r7)
        L_0x0381:
            r24.save()
            int r0 = r1.messageLeft
            float r0 = (float) r0
            int r6 = r1.messageTop
            float r6 = (float) r6
            r8.translate(r0, r6)
            android.text.StaticLayout r0 = r1.messageLayout     // Catch:{ Exception -> 0x0393 }
            r0.draw(r8)     // Catch:{ Exception -> 0x0393 }
            goto L_0x0397
        L_0x0393:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0397:
            r24.restore()
        L_0x039a:
            int r0 = r1.currentDialogFolderId
            r6 = 1080033280(0x40600000, float:3.5)
            r7 = 1095761920(0x41500000, float:13.0)
            if (r0 != 0) goto L_0x0407
            boolean r0 = r1.drawClockIcon
            if (r0 == 0) goto L_0x03b8
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            android.graphics.drawable.Drawable r14 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_clockDrawable
            int r2 = r1.clockDrawLeft
            int r5 = r1.clockDrawTop
            setDrawableBounds(r14, r2, r5, r0, r0)
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_clockDrawable
            r2.draw(r8)
        L_0x03b8:
            boolean r0 = r1.drawCheck2
            if (r0 == 0) goto L_0x0407
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0407
            boolean r0 = r1.drawCheck1
            r2 = 1096810496(0x41600000, float:14.0)
            r5 = 1093140480(0x41280000, float:10.5)
            if (r0 == 0) goto L_0x03e8
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_checkReadDrawable1
            int r14 = r23.getMeasuredWidth()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r14 = r14 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            setDrawableBounds(r0, r14, r2, r3, r5)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_checkReadDrawable1
            r0.draw(r8)
            goto L_0x0407
        L_0x03e8:
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_halfCheckDrawable1
            int r3 = r23.getMeasuredWidth()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r3 = r3 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            setDrawableBounds(r0, r3, r2, r14, r5)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_halfCheckDrawable1
            r0.draw(r8)
        L_0x0407:
            boolean r0 = r1.dialogMuted
            r2 = 1092616192(0x41200000, float:10.0)
            r3 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x0439
            boolean r0 = r1.drawVerifiedIcon
            if (r0 != 0) goto L_0x0439
            boolean r0 = r1.drawScam
            if (r0 != 0) goto L_0x0439
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            int r5 = r23.getMeasuredWidth()
            r14 = 1096286208(0x41580000, float:13.5)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r5 = r5 - r14
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            setDrawableBounds(r0, r5, r6, r14, r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_muteDrawable
            r0.draw(r8)
            goto L_0x0472
        L_0x0439:
            boolean r0 = r1.drawVerifiedIcon
            if (r0 == 0) goto L_0x045e
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
            r0.draw(r8)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedCheckDrawable
            r0.draw(r8)
            goto L_0x0472
        L_0x045e:
            boolean r0 = r1.drawScam
            if (r0 == 0) goto L_0x0472
            im.bclpbkiauv.ui.components.ScamDrawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            int r5 = r1.nameMuteLeft
            float r5 = (float) r5
            float r6 = r1.topOffset
            float r6 = r6 + r3
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (float) r5, (float) r6)
            im.bclpbkiauv.ui.components.ScamDrawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_scamDrawable
            r0.draw(r8)
        L_0x0472:
            boolean r0 = r1.drawReorder
            r5 = 1132396544(0x437f0000, float:255.0)
            if (r0 != 0) goto L_0x047f
            float r0 = r1.reorderIconProgress
            r6 = 0
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0497
        L_0x047f:
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            float r7 = r1.reorderIconProgress
            float r7 = r7 * r5
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            int r7 = r1.recorderLeft
            int r14 = r1.recorderTop
            setDrawableBounds((android.graphics.drawable.Drawable) r0, (int) r7, (int) r14)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_reorderDrawable
            r0.draw(r8)
        L_0x0497:
            boolean r0 = r1.drawPinIcon
            if (r0 == 0) goto L_0x04bf
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            float r7 = r1.reorderIconProgress
            float r7 = r13 - r7
            float r7 = r7 * r5
            int r7 = (int) r7
            r0.setAlpha(r7)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            int r7 = r1.pinLeft
            int r14 = r1.pinTop
            r16 = 1106247680(0x41f00000, float:30.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            setDrawableBounds(r0, r7, r14, r6, r2)
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedDrawable
            r0.draw(r8)
        L_0x04bf:
            boolean r0 = r1.drawErrorIcon
            if (r0 == 0) goto L_0x0528
            android.graphics.drawable.Drawable r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorDrawable
            float r6 = r1.reorderIconProgress
            float r7 = r13 - r6
            float r7 = r7 * r5
            int r5 = (int) r7
            r0.setAlpha(r5)
            android.graphics.RectF r0 = r1.rect
            int r5 = r1.errorLeft
            float r6 = (float) r5
            int r7 = r1.errorTop
            float r7 = (float) r7
            r14 = 1095761920(0x41500000, float:13.0)
            int r16 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r5 = r5 + r16
            float r5 = (float) r5
            int r2 = r1.errorTop
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r2 = r2 + r14
            float r2 = (float) r2
            r0.set(r6, r7, r5, r2)
            android.graphics.RectF r0 = r1.rect
            r2 = 1087373312(0x40d00000, float:6.5)
            float r5 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r5 = r5 * r2
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r6 = r6 * r2
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_errorPaint
            r8.drawRoundRect(r0, r5, r6, r2)
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
            r0.draw(r8)
            r20 = 1092616192(0x41200000, float:10.0)
            goto L_0x06b0
        L_0x0528:
            boolean r0 = r1.drawCount
            if (r0 != 0) goto L_0x0535
            boolean r0 = r1.drawMentionIcon
            if (r0 == 0) goto L_0x0531
            goto L_0x0535
        L_0x0531:
            r20 = 1092616192(0x41200000, float:10.0)
            goto L_0x06b0
        L_0x0535:
            boolean r0 = r1.drawCount
            if (r0 == 0) goto L_0x05f9
            boolean r0 = r1.dialogMuted
            if (r0 != 0) goto L_0x0545
            int r0 = r1.currentDialogFolderId
            if (r0 == 0) goto L_0x0542
            goto L_0x0545
        L_0x0542:
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
            goto L_0x0547
        L_0x0545:
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countGrayPaint
        L_0x0547:
            float r2 = r1.reorderIconProgress
            float r7 = r13 - r2
            float r7 = r7 * r5
            int r2 = (int) r7
            r0.setAlpha(r2)
            android.text.TextPaint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r6 = r1.reorderIconProgress
            float r7 = r13 - r6
            float r7 = r7 * r5
            int r6 = (int) r7
            r2.setAlpha(r6)
            boolean r2 = r1.countIsBiggerThanTen
            if (r2 == 0) goto L_0x0597
            int r2 = r1.countLeft
            r6 = 1086324736(0x40c00000, float:6.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r2 = r2 - r7
            r7 = 1090519040(0x41000000, float:8.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r14
            android.graphics.RectF r14 = r1.rect
            float r6 = (float) r2
            int r5 = r1.countTop
            float r5 = (float) r5
            int r13 = r1.countWidth
            int r13 = r13 + r2
            r20 = 1092616192(0x41200000, float:10.0)
            int r21 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            int r13 = r13 + r21
            float r13 = (float) r13
            int r3 = r1.countTop
            r22 = 1098907648(0x41800000, float:16.0)
            int r22 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            int r3 = r3 + r22
            float r3 = (float) r3
            r14.set(r6, r5, r13, r3)
            android.graphics.RectF r3 = r1.rect
            r8.drawRoundRect(r3, r7, r7, r0)
            goto L_0x05c4
        L_0x0597:
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
            float r13 = (float) r7
            float r14 = (float) r2
            float r14 = r14 + r3
            float r7 = (float) r7
            float r7 = r7 + r3
            r5.set(r6, r13, r14, r7)
            android.graphics.RectF r5 = r1.rect
            r6 = 1073741824(0x40000000, float:2.0)
            float r7 = r3 / r6
            float r13 = r3 / r6
            r8.drawRoundRect(r5, r7, r13, r0)
        L_0x05c4:
            android.text.StaticLayout r3 = r1.countLayout
            if (r3 == 0) goto L_0x05fb
            r24.save()
            int r3 = r1.countLeft
            boolean r5 = r1.countIsBiggerThanTen
            if (r5 == 0) goto L_0x05d4
            r7 = 1065353216(0x3f800000, float:1.0)
            goto L_0x05d6
        L_0x05d4:
            r7 = 1056964608(0x3f000000, float:0.5)
        L_0x05d6:
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            int r3 = r3 - r5
            float r3 = (float) r3
            int r5 = r1.countTop
            boolean r6 = r1.countIsBiggerThanTen
            if (r6 == 0) goto L_0x05e5
            r6 = 1073741824(0x40000000, float:2.0)
            goto L_0x05e7
        L_0x05e5:
            r6 = 1077936128(0x40400000, float:3.0)
        L_0x05e7:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 + r6
            float r5 = (float) r5
            r8.translate(r3, r5)
            android.text.StaticLayout r3 = r1.countLayout
            r3.draw(r8)
            r24.restore()
            goto L_0x05fb
        L_0x05f9:
            r20 = 1092616192(0x41200000, float:10.0)
        L_0x05fb:
            boolean r0 = r1.drawMentionIcon
            if (r0 == 0) goto L_0x06b0
            android.graphics.Paint r0 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
            float r2 = r1.reorderIconProgress
            r3 = 1065353216(0x3f800000, float:1.0)
            float r7 = r3 - r2
            r2 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 * r2
            int r2 = (int) r7
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
            float r13 = (float) r7
            float r14 = (float) r0
            float r14 = r14 + r2
            float r7 = (float) r7
            float r7 = r7 + r2
            r5.set(r6, r13, r14, r7)
            boolean r5 = r1.dialogMuted
            if (r5 == 0) goto L_0x063a
            int r5 = r1.folderId
            if (r5 == 0) goto L_0x063a
            android.graphics.Paint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countGrayPaint
            goto L_0x063c
        L_0x063a:
            android.graphics.Paint r5 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countPaint
        L_0x063c:
            android.graphics.RectF r6 = r1.rect
            r7 = 1073741824(0x40000000, float:2.0)
            float r13 = r2 / r7
            float r14 = r2 / r7
            r8.drawRoundRect(r6, r13, r14, r5)
            android.text.StaticLayout r6 = r1.mentionLayout
            if (r6 == 0) goto L_0x0677
            android.text.TextPaint r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_countTextPaint
            float r7 = r1.reorderIconProgress
            r13 = 1065353216(0x3f800000, float:1.0)
            float r7 = r13 - r7
            r13 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 * r13
            int r7 = (int) r7
            r6.setAlpha(r7)
            r24.save()
            int r6 = r1.mentionLeft
            float r6 = (float) r6
            int r7 = r1.countTop
            r13 = 1082130432(0x40800000, float:4.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r7 = r7 + r13
            float r7 = (float) r7
            r8.translate(r6, r7)
            android.text.StaticLayout r6 = r1.mentionLayout
            r6.draw(r8)
            r24.restore()
            goto L_0x06b0
        L_0x0677:
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            float r7 = r1.reorderIconProgress
            r13 = 1065353216(0x3f800000, float:1.0)
            float r7 = r13 - r7
            r13 = 1132396544(0x437f0000, float:255.0)
            float r7 = r7 * r13
            int r7 = (int) r7
            r6.setAlpha(r7)
            android.graphics.drawable.Drawable r6 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            int r7 = r1.mentionLeft
            r13 = 1073741824(0x40000000, float:2.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r7 = r7 - r13
            int r13 = r1.countTop
            r14 = 1078774989(0x404ccccd, float:3.2)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            int r13 = r13 + r14
            r14 = 1093664768(0x41300000, float:11.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r18 = 1093664768(0x41300000, float:11.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            setDrawableBounds(r6, r7, r13, r14, r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_mentionDrawable
            r3.draw(r8)
        L_0x06b0:
            boolean r0 = r1.animatingArchiveAvatar
            r13 = 1126825984(0x432a0000, float:170.0)
            if (r0 == 0) goto L_0x06d4
            r24.save()
            im.bclpbkiauv.ui.cell.FmtDialogCell$BounceInterpolator r0 = r1.interpolator
            float r2 = r1.animatingArchiveAvatarProgress
            float r2 = r2 / r13
            float r0 = r0.getInterpolation(r2)
            r2 = 1065353216(0x3f800000, float:1.0)
            float r0 = r0 + r2
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            float r2 = r2.getCenterX()
            im.bclpbkiauv.messenger.ImageReceiver r3 = r1.avatarImage
            float r3 = r3.getCenterY()
            r8.scale(r0, r0, r2, r3)
        L_0x06d4:
            im.bclpbkiauv.messenger.ImageReceiver r0 = r1.avatarImage
            r0.draw(r8)
            boolean r0 = r1.animatingArchiveAvatar
            if (r0 == 0) goto L_0x06e0
            r24.restore()
        L_0x06e0:
            boolean r0 = r1.drawSecretLockIcon
            if (r0 == 0) goto L_0x070b
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
            r2.draw(r8)
            goto L_0x078e
        L_0x070b:
            boolean r0 = r1.drawGroupIcon
            if (r0 == 0) goto L_0x073b
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
            r0.draw(r8)
            goto L_0x078e
        L_0x073b:
            boolean r0 = r1.drawBroadcastIcon
            if (r0 == 0) goto L_0x0765
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
            r2.draw(r8)
            goto L_0x078e
        L_0x0765:
            boolean r0 = r1.drawBotIcon
            if (r0 == 0) goto L_0x078e
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
            r2.draw(r8)
        L_0x078e:
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            r5 = 1
            if (r0 == 0) goto L_0x087e
            boolean r2 = r1.isDialogCell
            if (r2 == 0) goto L_0x087e
            int r2 = r1.currentDialogFolderId
            if (r2 != 0) goto L_0x087e
            boolean r0 = im.bclpbkiauv.messenger.MessagesController.isSupportUser(r0)
            if (r0 != 0) goto L_0x087e
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = r0.bot
            if (r0 != 0) goto L_0x087e
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            boolean r0 = r0.self
            if (r0 != 0) goto L_0x07dd
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r0 = r0.status
            if (r0 == 0) goto L_0x07c5
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r1.user
            im.bclpbkiauv.tgnet.TLRPC$UserStatus r0 = r0.status
            int r0 = r0.expires
            int r2 = r1.currentAccount
            im.bclpbkiauv.tgnet.ConnectionsManager r2 = im.bclpbkiauv.tgnet.ConnectionsManager.getInstance(r2)
            int r2 = r2.getCurrentTime()
            if (r0 > r2) goto L_0x07db
        L_0x07c5:
            int r0 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r0 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0)
            java.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r0 = r0.onlinePrivacy
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r1.user
            int r2 = r2.id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            boolean r0 = r0.containsKey(r2)
            if (r0 == 0) goto L_0x07dd
        L_0x07db:
            r0 = 1
            goto L_0x07de
        L_0x07dd:
            r0 = 0
        L_0x07de:
            if (r0 != 0) goto L_0x07e7
            float r2 = r1.onlineProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x087e
        L_0x07e7:
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            int r2 = r2.getImageY2()
            boolean r6 = r1.useForceThreeLines
            if (r6 != 0) goto L_0x07f9
            boolean r6 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r6 == 0) goto L_0x07f6
            goto L_0x07f9
        L_0x07f6:
            r19 = 1090519040(0x41000000, float:8.0)
            goto L_0x07fb
        L_0x07f9:
            r19 = 1086324736(0x40c00000, float:6.0)
        L_0x07fb:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r2 = r2 - r6
            boolean r6 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x081e
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageX()
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0816
            boolean r7 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x0813
            goto L_0x0816
        L_0x0813:
            r16 = 1086324736(0x40c00000, float:6.0)
            goto L_0x0818
        L_0x0816:
            r16 = 1092616192(0x41200000, float:10.0)
        L_0x0818:
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r6 = r6 + r7
            goto L_0x0837
        L_0x081e:
            im.bclpbkiauv.messenger.ImageReceiver r6 = r1.avatarImage
            int r6 = r6.getImageX2()
            boolean r7 = r1.useForceThreeLines
            if (r7 != 0) goto L_0x0830
            boolean r7 = im.bclpbkiauv.messenger.SharedConfig.useThreeLinesLayout
            if (r7 == 0) goto L_0x082d
            goto L_0x0830
        L_0x082d:
            r16 = 1086324736(0x40c00000, float:6.0)
            goto L_0x0832
        L_0x0830:
            r16 = 1092616192(0x41200000, float:10.0)
        L_0x0832:
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r6 = r6 - r7
        L_0x0837:
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
            r7.setColor(r14)
            android.graphics.Paint r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_onlineCirclePaint
            java.lang.String r14 = "chats_onlineCircle"
            int r14 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r14)
            r7.setColor(r14)
            if (r0 == 0) goto L_0x0866
            float r7 = r1.onlineProgress
            r14 = 1065353216(0x3f800000, float:1.0)
            int r16 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r16 >= 0) goto L_0x087e
            float r3 = (float) r11
            r17 = 1125515264(0x43160000, float:150.0)
            float r3 = r3 / r17
            float r7 = r7 + r3
            r1.onlineProgress = r7
            int r3 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x0863
            r1.onlineProgress = r14
        L_0x0863:
            r4 = 1
            r0 = r4
            goto L_0x087f
        L_0x0866:
            float r3 = r1.onlineProgress
            r7 = 0
            int r14 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r14 <= 0) goto L_0x087e
            float r14 = (float) r11
            r16 = 1125515264(0x43160000, float:150.0)
            float r14 = r14 / r16
            float r3 = r3 - r14
            r1.onlineProgress = r3
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x087b
            r1.onlineProgress = r7
        L_0x087b:
            r4 = 1
            r0 = r4
            goto L_0x087f
        L_0x087e:
            r0 = r4
        L_0x087f:
            float r2 = r1.translationX
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0889
            r24.restore()
        L_0x0889:
            boolean r2 = r1.useSeparator
            if (r2 == 0) goto L_0x08d8
            r2 = 1116733440(0x42900000, float:72.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x08ba
            r3 = 0
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r4 = (float) r2
            int r2 = r23.getMeasuredWidth()
            int r2 = r2 - r7
            float r6 = (float) r2
            int r2 = r23.getMeasuredHeight()
            int r2 = r2 - r5
            float r5 = (float) r2
            android.graphics.Paint r16 = im.bclpbkiauv.ui.actionbar.Theme.dividerPaint
            r2 = r24
            r17 = r5
            r5 = r6
            r6 = r17
            r14 = r7
            r7 = r16
            r2.drawLine(r3, r4, r5, r6, r7)
            goto L_0x08d8
        L_0x08ba:
            r14 = r7
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
        L_0x08d8:
            float r2 = r1.clipProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0926
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 24
            if (r2 == r3) goto L_0x08e9
            r24.restore()
            goto L_0x0926
        L_0x08e9:
            android.graphics.Paint r2 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_pinnedPaint
            int r3 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r15)
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
        L_0x0926:
            boolean r2 = r1.drawReorder
            if (r2 != 0) goto L_0x0931
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x095d
        L_0x0931:
            boolean r2 = r1.drawReorder
            if (r2 == 0) goto L_0x094a
            float r2 = r1.reorderIconProgress
            r4 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x095d
            float r5 = (float) r11
            float r5 = r5 / r13
            float r2 = r2 + r5
            r1.reorderIconProgress = r2
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0948
            r1.reorderIconProgress = r4
        L_0x0948:
            r0 = 1
            goto L_0x095d
        L_0x094a:
            float r2 = r1.reorderIconProgress
            r3 = 0
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x095d
            float r4 = (float) r11
            float r4 = r4 / r13
            float r2 = r2 - r4
            r1.reorderIconProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 >= 0) goto L_0x095c
            r1.reorderIconProgress = r3
        L_0x095c:
            r0 = 1
        L_0x095d:
            boolean r3 = r1.archiveHidden
            if (r3 == 0) goto L_0x0987
            float r3 = r1.archiveBackgroundProgress
            r2 = 0
            int r4 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x09ad
            float r4 = (float) r11
            float r4 = r4 / r13
            float r3 = r3 - r4
            r1.archiveBackgroundProgress = r3
            float r3 = r1.currentRevealBounceProgress
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0975
            r1.currentRevealBounceProgress = r2
        L_0x0975:
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            int r3 = r3.getAvatarType()
            r4 = 3
            if (r3 != r4) goto L_0x0985
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            float r4 = r1.archiveBackgroundProgress
            r3.setArchivedAvatarHiddenProgress(r4)
        L_0x0985:
            r0 = 1
            goto L_0x09ad
        L_0x0987:
            float r3 = r1.archiveBackgroundProgress
            r4 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r5 >= 0) goto L_0x09ad
            float r5 = (float) r11
            float r5 = r5 / r13
            float r3 = r3 + r5
            r1.archiveBackgroundProgress = r3
            float r3 = r1.currentRevealBounceProgress
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x099c
            r1.currentRevealBounceProgress = r4
        L_0x099c:
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            int r3 = r3.getAvatarType()
            r4 = 3
            if (r3 != r4) goto L_0x09ac
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            float r4 = r1.archiveBackgroundProgress
            r3.setArchivedAvatarHiddenProgress(r4)
        L_0x09ac:
            r0 = 1
        L_0x09ad:
            boolean r3 = r1.animatingArchiveAvatar
            if (r3 == 0) goto L_0x09c1
            float r3 = r1.animatingArchiveAvatarProgress
            float r4 = (float) r11
            float r3 = r3 + r4
            r1.animatingArchiveAvatarProgress = r3
            int r3 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r3 < 0) goto L_0x09c0
            r1.animatingArchiveAvatarProgress = r13
            r3 = 0
            r1.animatingArchiveAvatar = r3
        L_0x09c0:
            r0 = 1
        L_0x09c1:
            boolean r3 = r1.drawRevealBackground
            if (r3 == 0) goto L_0x09ee
            float r2 = r1.currentRevealBounceProgress
            r3 = 1065353216(0x3f800000, float:1.0)
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x09d9
            float r4 = (float) r11
            float r4 = r4 / r13
            float r2 = r2 + r4
            r1.currentRevealBounceProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x09d9
            r1.currentRevealBounceProgress = r3
            r0 = 1
        L_0x09d9:
            float r2 = r1.currentRevealProgress
            int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r4 >= 0) goto L_0x0a10
            float r4 = (float) r11
            r5 = 1133903872(0x43960000, float:300.0)
            float r4 = r4 / r5
            float r2 = r2 + r4
            r1.currentRevealProgress = r2
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 <= 0) goto L_0x09ec
            r1.currentRevealProgress = r3
        L_0x09ec:
            r0 = 1
            goto L_0x0a10
        L_0x09ee:
            r3 = 1065353216(0x3f800000, float:1.0)
            float r4 = r1.currentRevealBounceProgress
            int r3 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x09fb
            r2 = 0
            r1.currentRevealBounceProgress = r2
            r0 = 1
            goto L_0x09fc
        L_0x09fb:
            r2 = 0
        L_0x09fc:
            float r3 = r1.currentRevealProgress
            int r4 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0a10
            float r4 = (float) r11
            r5 = 1133903872(0x43960000, float:300.0)
            float r4 = r4 / r5
            float r3 = r3 - r4
            r1.currentRevealProgress = r3
            int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
            if (r3 >= 0) goto L_0x0a0f
            r1.currentRevealProgress = r2
        L_0x0a0f:
            r0 = 1
        L_0x0a10:
            if (r0 == 0) goto L_0x0a15
            r23.invalidate()
        L_0x0a15:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cell.FmtDialogCell.onDraw(android.graphics.Canvas):void");
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
