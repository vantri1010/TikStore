package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.view.View;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;

public class DialogMeUrlCell extends BaseCell {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private int currentAccount = UserConfig.selectedAccount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawVerified;
    private boolean isSelected;
    private StaticLayout messageLayout;
    private int messageLeft;
    private int messageTop = AndroidUtilities.dp(40.0f);
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private TLRPC.RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(7.5f));
    }

    public void setRecentMeUrl(TLRPC.RecentMeUrl url) {
        this.recentMeUrl = url;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(72.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            buildLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:126:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0494  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0517  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r21 = this;
            r1 = r21
            java.lang.String r0 = ""
            android.text.TextPaint r10 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_namePaint
            android.text.TextPaint r11 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_messagePaint
            r12 = 0
            r1.drawNameGroup = r12
            r1.drawNameBroadcast = r12
            r1.drawNameLock = r12
            r1.drawNameBot = r12
            r1.drawVerified = r12
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_recentMeUrlChat
            r3 = 1099694080(0x418c0000, float:17.5)
            r4 = 1099169792(0x41840000, float:16.5)
            r5 = 1
            r9 = 1096810496(0x41600000, float:14.0)
            if (r2 == 0) goto L_0x00bf
            int r2 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r6 = r1.recentMeUrl
            int r6 = r6.chat_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            int r6 = r2.id
            if (r6 < 0) goto L_0x004a
            boolean r6 = im.bclpbkiauv.messenger.ChatObject.isChannel(r2)
            if (r6 == 0) goto L_0x0041
            boolean r6 = r2.megagroup
            if (r6 != 0) goto L_0x0041
            goto L_0x004a
        L_0x0041:
            r1.drawNameGroup = r5
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r3
            goto L_0x0052
        L_0x004a:
            r1.drawNameBroadcast = r5
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r3
        L_0x0052:
            boolean r3 = r2.verified
            r1.drawVerified = r3
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x007d
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLockLeft = r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            int r3 = r3 + 4
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0073
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            goto L_0x0075
        L_0x0073:
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
        L_0x0075:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x009f
        L_0x007d:
            int r3 = r21.getMeasuredWidth()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0090
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            goto L_0x0092
        L_0x0090:
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
        L_0x0092:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r3
        L_0x009f:
            java.lang.String r0 = r2.title
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            r3.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r2)
            im.bclpbkiauv.messenger.ImageReceiver r13 = r1.avatarImage
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r2, r12)
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            r19 = 0
            java.lang.String r15 = "50_50"
            r16 = r3
            r18 = r4
            r13.setImage(r14, r15, r16, r17, r18, r19)
            goto L_0x030d
        L_0x00bf:
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_recentMeUrlUser
            if (r2 == 0) goto L_0x0158
            int r2 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r2 = im.bclpbkiauv.messenger.MessagesController.getInstance(r2)
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r2.getUser(r3)
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x00e3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x00e9
        L_0x00e3:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r3
        L_0x00e9:
            if (r2 == 0) goto L_0x0136
            boolean r3 = r2.bot
            if (r3 == 0) goto L_0x0132
            r1.drawNameBot = r5
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r3
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0117
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLockLeft = r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            int r3 = r3 + 4
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0132
        L_0x0117:
            int r3 = r21.getMeasuredWidth()
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r3
        L_0x0132:
            boolean r3 = r2.verified
            r1.drawVerified = r3
        L_0x0136:
            java.lang.String r0 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            r3.setInfo((im.bclpbkiauv.tgnet.TLRPC.User) r2)
            im.bclpbkiauv.messenger.ImageReceiver r13 = r1.avatarImage
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForUser(r2, r12)
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            r19 = 0
            java.lang.String r15 = "50_50"
            r16 = r3
            r18 = r4
            r13.setImage(r14, r15, r16, r17, r18, r19)
            goto L_0x030d
        L_0x0158:
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_recentMeUrlStickerSet
            r6 = 0
            r8 = 5
            if (r2 == 0) goto L_0x01a5
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x016c
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.nameLeft = r2
            goto L_0x0172
        L_0x016c:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r2
        L_0x0172:
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r2 = r2.set
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r2 = r2.set
            java.lang.String r0 = r2.title
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = r1.avatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r3 = r3.set
            im.bclpbkiauv.tgnet.TLRPC$StickerSet r3 = r3.set
            java.lang.String r3 = r3.title
            r2.setInfo(r8, r3, r6)
            im.bclpbkiauv.messenger.ImageReceiver r13 = r1.avatarImage
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$StickerSetCovered r2 = r2.set
            im.bclpbkiauv.tgnet.TLRPC$Document r2 = r2.cover
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r2)
            r15 = 0
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = r1.avatarDrawable
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            r19 = 0
            r16 = r2
            r18 = r3
            r13.setImage(r14, r15, r16, r17, r18, r19)
            goto L_0x030d
        L_0x01a5:
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_recentMeUrlChatInvite
            if (r2 == 0) goto L_0x02da
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x01b7
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.nameLeft = r2
            goto L_0x01bd
        L_0x01b7:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r2
        L_0x01bd:
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            if (r2 == 0) goto L_0x0232
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = r1.avatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r6 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r6 = r6.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r6 = r6.chat
            r2.setInfo((im.bclpbkiauv.tgnet.TLRPC.Chat) r6)
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            java.lang.String r0 = r2.title
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            int r2 = r2.id
            if (r2 < 0) goto L_0x0202
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            boolean r2 = im.bclpbkiauv.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x01f9
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x01f9
            goto L_0x0202
        L_0x01f9:
            r1.drawNameGroup = r5
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r2
            goto L_0x020a
        L_0x0202:
            r1.drawNameBroadcast = r5
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r2
        L_0x020a:
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            boolean r2 = r2.verified
            r1.drawVerified = r2
            im.bclpbkiauv.messenger.ImageReceiver r13 = r1.avatarImage
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Chat r2 = r2.chat
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForChat(r2, r12)
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = r1.avatarDrawable
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            r19 = 0
            java.lang.String r15 = "50_50"
            r16 = r2
            r18 = r3
            r13.setImage(r14, r15, r16, r17, r18, r19)
            goto L_0x0290
        L_0x0232:
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            java.lang.String r0 = r2.title
            im.bclpbkiauv.ui.components.AvatarDrawable r2 = r1.avatarDrawable
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r7 = r7.chat_invite
            java.lang.String r7 = r7.title
            r2.setInfo(r8, r7, r6)
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            boolean r2 = r2.broadcast
            if (r2 != 0) goto L_0x025d
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            boolean r2 = r2.channel
            if (r2 == 0) goto L_0x0254
            goto L_0x025d
        L_0x0254:
            r1.drawNameGroup = r5
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.nameLockTop = r2
            goto L_0x0265
        L_0x025d:
            r1.drawNameBroadcast = r5
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r2
        L_0x0265:
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r2 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r2 = r2.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            r3 = 50
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r2 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            im.bclpbkiauv.messenger.ImageReceiver r13 = r1.avatarImage
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            im.bclpbkiauv.tgnet.TLRPC$ChatInvite r3 = r3.chat_invite
            im.bclpbkiauv.tgnet.TLRPC$Photo r3 = r3.photo
            im.bclpbkiauv.messenger.ImageLocation r14 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r2, r3)
            im.bclpbkiauv.ui.components.AvatarDrawable r3 = r1.avatarDrawable
            r17 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            r19 = 0
            java.lang.String r15 = "50_50"
            r16 = r3
            r18 = r4
            r13.setImage(r14, r15, r16, r17, r18, r19)
        L_0x0290:
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x02b7
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.nameLockLeft = r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            int r2 = r2 + 4
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x02ad
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            goto L_0x02af
        L_0x02ad:
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
        L_0x02af:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            r1.nameLeft = r2
            goto L_0x030d
        L_0x02b7:
            int r2 = r21.getMeasuredWidth()
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            boolean r3 = r1.drawNameGroup
            if (r3 == 0) goto L_0x02ca
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            goto L_0x02cc
        L_0x02ca:
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
        L_0x02cc:
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 - r3
            r1.nameLockLeft = r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r2
            goto L_0x030d
        L_0x02da:
            boolean r2 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_recentMeUrlUnknown
            if (r2 == 0) goto L_0x0302
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x02ec
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.nameLeft = r2
            goto L_0x02f2
        L_0x02ec:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r1.nameLeft = r2
        L_0x02f2:
            java.lang.String r0 = "Url"
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            r3 = 0
            r4 = 0
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r1.avatarDrawable
            r6 = 0
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r8 = 0
            r2.setImage(r3, r4, r5, r6, r7, r8)
            goto L_0x030d
        L_0x0302:
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            r3 = 0
            r4 = 0
            im.bclpbkiauv.ui.components.AvatarDrawable r5 = r1.avatarDrawable
            r6 = 0
            r8 = 0
            r2.setImage(r3, r4, r5, r6, r7, r8)
        L_0x030d:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            java.lang.String r3 = r3.linkPrefix
            r2.append(r3)
            java.lang.String r3 = "/"
            r2.append(r3)
            im.bclpbkiauv.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            java.lang.String r3 = r3.url
            r2.append(r3)
            java.lang.String r13 = r2.toString()
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x033e
            r2 = 2131691577(0x7f0f0839, float:1.901223E38)
            java.lang.String r3 = "HiddenName"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            r14 = r0
            goto L_0x033f
        L_0x033e:
            r14 = r0
        L_0x033f:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0350
            int r0 = r21.getMeasuredWidth()
            int r2 = r1.nameLeft
            int r0 = r0 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r0 = r0 - r2
            goto L_0x035f
        L_0x0350:
            int r0 = r21.getMeasuredWidth()
            int r2 = r1.nameLeft
            int r0 = r0 - r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
        L_0x035f:
            boolean r2 = r1.drawNameLock
            r3 = 1082130432(0x40800000, float:4.0)
            if (r2 == 0) goto L_0x0372
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_lockDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            int r0 = r0 - r2
            goto L_0x03a4
        L_0x0372:
            boolean r2 = r1.drawNameGroup
            if (r2 == 0) goto L_0x0383
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_groupDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            int r0 = r0 - r2
            goto L_0x03a4
        L_0x0383:
            boolean r2 = r1.drawNameBroadcast
            if (r2 == 0) goto L_0x0394
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_broadcastDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            int r0 = r0 - r2
            goto L_0x03a4
        L_0x0394:
            boolean r2 = r1.drawNameBot
            if (r2 == 0) goto L_0x03a4
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_botDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            int r0 = r0 - r2
        L_0x03a4:
            boolean r2 = r1.drawVerified
            r15 = 1086324736(0x40c00000, float:6.0)
            if (r2 == 0) goto L_0x03bf
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r15)
            android.graphics.drawable.Drawable r3 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r3 = r3.getIntrinsicWidth()
            int r2 = r2 + r3
            int r0 = r0 - r2
            boolean r3 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x03bf
            int r3 = r1.nameLeft
            int r3 = r3 + r2
            r1.nameLeft = r3
        L_0x03bf:
            r16 = 1094713344(0x41400000, float:12.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r9 = java.lang.Math.max(r2, r0)
            r0 = 10
            r2 = 32
            java.lang.String r0 = r14.replace(r0, r2)     // Catch:{ Exception -> 0x03f5 }
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x03f5 }
            int r2 = r9 - r2
            float r2 = (float) r2     // Catch:{ Exception -> 0x03f5 }
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x03f5 }
            java.lang.CharSequence r3 = android.text.TextUtils.ellipsize(r0, r10, r2, r3)     // Catch:{ Exception -> 0x03f5 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x03f5 }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x03f5 }
            r7 = 1065353216(0x3f800000, float:1.0)
            r8 = 0
            r17 = 0
            r2 = r0
            r4 = r10
            r5 = r9
            r15 = r9
            r9 = r17
            r2.<init>(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x03f3 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x03f3 }
            goto L_0x03fa
        L_0x03f3:
            r0 = move-exception
            goto L_0x03f7
        L_0x03f5:
            r0 = move-exception
            r15 = r9
        L_0x03f7:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03fa:
            int r0 = r21.getMeasuredWidth()
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            int r2 = r2 + 16
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            boolean r2 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0426
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r2 = (float) r2
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.messageLeft = r2
            boolean r2 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x041e
            r2 = 1095761920(0x41500000, float:13.0)
            goto L_0x0420
        L_0x041e:
            r2 = 1091567616(0x41100000, float:9.0)
        L_0x0420:
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r9 = r2
            goto L_0x0443
        L_0x0426:
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r1.messageLeft = r2
            int r2 = r21.getMeasuredWidth()
            boolean r3 = im.bclpbkiauv.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x043b
            r3 = 1115815936(0x42820000, float:65.0)
            goto L_0x043d
        L_0x043b:
            r3 = 1114898432(0x42740000, float:61.0)
        L_0x043d:
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            r9 = r2
        L_0x0443:
            im.bclpbkiauv.messenger.ImageReceiver r2 = r1.avatarImage
            int r3 = r1.avatarTop
            r4 = 1112539136(0x42500000, float:52.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r4)
            r2.setImageCoords(r9, r3, r5, r4)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r8 = java.lang.Math.max(r2, r0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r0 = r8 - r0
            float r0 = (float) r0
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r16 = android.text.TextUtils.ellipsize(r13, r11, r0, r2)
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0488 }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0488 }
            r7 = 1065353216(0x3f800000, float:1.0)
            r17 = 0
            r19 = 0
            r2 = r0
            r3 = r16
            r4 = r11
            r5 = r8
            r20 = r8
            r8 = r17
            r17 = r9
            r9 = r19
            r2.<init>(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0486 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x0486 }
            goto L_0x0490
        L_0x0486:
            r0 = move-exception
            goto L_0x048d
        L_0x0488:
            r0 = move-exception
            r20 = r8
            r17 = r9
        L_0x048d:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0490:
            boolean r0 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0517
            android.text.StaticLayout r0 = r1.nameLayout
            r2 = 0
            if (r0 == 0) goto L_0x04df
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x04df
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r12)
            android.text.StaticLayout r3 = r1.nameLayout
            float r3 = r3.getLineWidth(r12)
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x04cd
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r15
            double r7 = r7 - r3
            double r5 = r5 + r7
            r7 = 1086324736(0x40c00000, float:6.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            double r7 = (double) r7
            double r5 = r5 - r7
            android.graphics.drawable.Drawable r7 = im.bclpbkiauv.ui.actionbar.Theme.dialogs_verifiedDrawable
            int r7 = r7.getIntrinsicWidth()
            double r7 = (double) r7
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x04cd:
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x04df
            double r5 = (double) r15
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x04df
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r15
            double r7 = r7 - r3
            double r5 = r5 + r7
            int r5 = (int) r5
            r1.nameLeft = r5
        L_0x04df:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0514
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0514
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineLeft(r12)
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x0510
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getLineWidth(r12)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            r4 = r20
            double r5 = (double) r4
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0588
            int r5 = r1.messageLeft
            double r5 = (double) r5
            double r7 = (double) r4
            double r7 = r7 - r2
            double r5 = r5 + r7
            int r5 = (int) r5
            r1.messageLeft = r5
            goto L_0x0588
        L_0x0510:
            r4 = r20
            goto L_0x0588
        L_0x0514:
            r4 = r20
            goto L_0x0588
        L_0x0517:
            r4 = r20
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x055a
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x055a
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r12)
            float r2 = (float) r15
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x0547
            android.text.StaticLayout r2 = r1.nameLayout
            float r2 = r2.getLineWidth(r12)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            double r5 = (double) r15
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0547
            int r5 = r1.nameLeft
            double r5 = (double) r5
            double r7 = (double) r15
            double r7 = r7 - r2
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.nameLeft = r5
        L_0x0547:
            boolean r2 = r1.drawVerified
            if (r2 == 0) goto L_0x055a
            int r2 = r1.nameLeft
            float r2 = (float) r2
            float r2 = r2 + r0
            r3 = 1086324736(0x40c00000, float:6.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 + r3
            int r2 = (int) r2
            r1.nameMuteLeft = r2
        L_0x055a:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0588
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0588
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineRight(r12)
            float r2 = (float) r4
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x0588
            android.text.StaticLayout r2 = r1.messageLayout
            float r2 = r2.getLineWidth(r12)
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            double r5 = (double) r4
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 >= 0) goto L_0x0588
            int r5 = r1.messageLeft
            double r5 = (double) r5
            double r7 = (double) r4
            double r7 = r7 - r2
            double r5 = r5 - r7
            int r5 = (int) r5
            r1.messageLeft = r5
        L_0x0588:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.DialogMeUrlCell.buildLayout():void");
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isSelected) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
        }
        if (this.drawNameLock) {
            setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas);
        } else if (this.drawNameGroup) {
            setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(canvas);
        } else if (this.drawNameBroadcast) {
            setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(canvas);
        } else if (this.drawNameBot) {
            setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_botDrawable.draw(canvas);
        }
        if (this.nameLayout != null) {
            canvas.save();
            canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
            this.nameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.messageLayout != null) {
            canvas.save();
            canvas.translate((float) this.messageLeft, (float) this.messageTop);
            try {
                this.messageLayout.draw(canvas);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            canvas.restore();
        }
        if (this.drawVerified) {
            setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            Theme.dialogs_verifiedDrawable.draw(canvas);
            Theme.dialogs_verifiedCheckDrawable.draw(canvas);
        }
        if (this.useSeparator) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        this.avatarImage.draw(canvas);
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
