package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.CheckBox2;
import im.bclpbkiauv.ui.components.LayoutHelper;
import im.bclpbkiauv.ui.components.LetterDrawable;
import im.bclpbkiauv.ui.components.LinkPath;
import java.util.ArrayList;

public class SharedLinkCell extends FrameLayout {
    private CheckBox2 checkBox;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress = false;
    /* access modifiers changed from: private */
    public SharedLinkCellDelegate delegate;
    private int description2Y = AndroidUtilities.dp(30.0f);
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY = AndroidUtilities.dp(30.0f);
    private boolean drawLinkImageView;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout = new ArrayList<>();
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links = new ArrayList<>();
    private MessageObject message;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    /* access modifiers changed from: private */
    public int pressCount = 0;
    /* access modifiers changed from: private */
    public int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY = AndroidUtilities.dp(10.0f);
    private LinkPath urlPath;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC.WebPage webPage);

        void onLinkLongPress(String str);
    }

    static /* synthetic */ int access$104(SharedLinkCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                CheckForLongPress unused = sharedLinkCell.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell2 = SharedLinkCell.this;
            sharedLinkCell2.postDelayed(sharedLinkCell2.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                boolean unused = SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCell.this.delegate.onLinkLongPress(SharedLinkCell.this.links.get(SharedLinkCell.this.pressedLink));
                }
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    public SharedLinkCell(Context context) {
        super(context);
        setFocusable(true);
        LinkPath linkPath = new LinkPath();
        this.urlPath = linkPath;
        linkPath.setUseRoundRect(true);
        TextPaint textPaint = new TextPaint(1);
        this.titleTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24.0f, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0119 A[Catch:{ Exception -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x017e A[Catch:{ Exception -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0191 A[Catch:{ Exception -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01a8 A[Catch:{ Exception -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01ab A[Catch:{ Exception -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0200 A[Catch:{ Exception -> 0x0236 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r33, int r34) {
        /*
            r32 = this;
            r1 = r32
            r2 = 0
            r1.drawLinkImageView = r2
            r0 = 0
            r1.descriptionLayout = r0
            r1.titleLayout = r0
            r1.descriptionLayout2 = r0
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r33)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r3 = r0 - r3
            r0 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaWebPage
            r12 = 1
            if (r8 == 0) goto L_0x0078
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r8 = r8.webpage
            boolean r8 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webPage
            if (r8 == 0) goto L_0x0078
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r8 = r8.media
            im.bclpbkiauv.tgnet.TLRPC$WebPage r8 = r8.webpage
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            if (r9 != 0) goto L_0x005d
            im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r8.photo
            if (r9 == 0) goto L_0x005d
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            r9.generateThumbs(r12)
        L_0x005d:
            im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r8.photo
            if (r9 == 0) goto L_0x0069
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r9 = r9.photoThumbs
            if (r9 == 0) goto L_0x0069
            r9 = 1
            goto L_0x006a
        L_0x0069:
            r9 = 0
        L_0x006a:
            r7 = r9
            java.lang.String r0 = r8.title
            if (r0 != 0) goto L_0x0071
            java.lang.String r0 = r8.site_name
        L_0x0071:
            java.lang.String r4 = r8.description
            java.lang.String r6 = r8.url
            r13 = r6
            r14 = r7
            goto L_0x007a
        L_0x0078:
            r13 = r6
            r14 = r7
        L_0x007a:
            im.bclpbkiauv.messenger.MessageObject r6 = r1.message
            if (r6 == 0) goto L_0x0245
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r6.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r6 = r6.entities
            boolean r6 = r6.isEmpty()
            if (r6 != 0) goto L_0x0245
            r6 = 0
            r31 = r4
            r4 = r0
            r0 = r5
            r5 = r31
        L_0x008f:
            im.bclpbkiauv.messenger.MessageObject r7 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r7.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r7 = r7.entities
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x0240
            im.bclpbkiauv.messenger.MessageObject r7 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r7.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r7 = r7.entities
            java.lang.Object r7 = r7.get(r6)
            im.bclpbkiauv.tgnet.TLRPC$MessageEntity r7 = (im.bclpbkiauv.tgnet.TLRPC.MessageEntity) r7
            int r8 = r7.length
            if (r8 <= 0) goto L_0x023b
            int r8 = r7.offset
            if (r8 < 0) goto L_0x023b
            int r8 = r7.offset
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            int r9 = r9.length()
            if (r8 < r9) goto L_0x00bf
            goto L_0x023b
        L_0x00bf:
            int r8 = r7.offset
            int r9 = r7.length
            int r8 = r8 + r9
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            int r9 = r9.length()
            if (r8 <= r9) goto L_0x00df
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.lang.String r8 = r8.message
            int r8 = r8.length()
            int r9 = r7.offset
            int r8 = r8 - r9
            r7.length = r8
        L_0x00df:
            if (r6 != 0) goto L_0x0113
            if (r13 == 0) goto L_0x0113
            int r8 = r7.offset
            if (r8 != 0) goto L_0x00f5
            int r8 = r7.length
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.lang.String r9 = r9.message
            int r9 = r9.length()
            if (r8 == r9) goto L_0x0113
        L_0x00f5:
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            int r8 = r8.size()
            if (r8 != r12) goto L_0x010b
            if (r5 != 0) goto L_0x0113
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.lang.String r0 = r8.message
            r8 = r0
            goto L_0x0114
        L_0x010b:
            im.bclpbkiauv.messenger.MessageObject r8 = r1.message
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r8.messageOwner
            java.lang.String r0 = r8.message
            r8 = r0
            goto L_0x0114
        L_0x0113:
            r8 = r0
        L_0x0114:
            r0 = 0
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEntityTextUrl     // Catch:{ Exception -> 0x0236 }
            if (r9 != 0) goto L_0x017a
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0236 }
            if (r9 == 0) goto L_0x011e
            goto L_0x017a
        L_0x011e:
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEntityEmail     // Catch:{ Exception -> 0x0236 }
            if (r9 == 0) goto L_0x01fe
            if (r4 == 0) goto L_0x012a
            int r9 = r4.length()     // Catch:{ Exception -> 0x0236 }
            if (r9 != 0) goto L_0x01fe
        L_0x012a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0236 }
            r9.<init>()     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = "mailto:"
            r9.append(r10)     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r10.message     // Catch:{ Exception -> 0x0236 }
            int r11 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r15 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r12 = r7.length     // Catch:{ Exception -> 0x0236 }
            int r15 = r15 + r12
            java.lang.String r10 = r10.substring(r11, r15)     // Catch:{ Exception -> 0x0236 }
            r9.append(r10)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0236 }
            r0 = r9
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r9 = r9.message     // Catch:{ Exception -> 0x0236 }
            int r10 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r11 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r12 = r7.length     // Catch:{ Exception -> 0x0236 }
            int r11 = r11 + r12
            java.lang.String r9 = r9.substring(r10, r11)     // Catch:{ Exception -> 0x0236 }
            r4 = r9
            int r9 = r7.offset     // Catch:{ Exception -> 0x0236 }
            if (r9 != 0) goto L_0x0171
            int r9 = r7.length     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r10.message     // Catch:{ Exception -> 0x0236 }
            int r10 = r10.length()     // Catch:{ Exception -> 0x0236 }
            if (r9 == r10) goto L_0x01fe
        L_0x0171:
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r9 = r9.message     // Catch:{ Exception -> 0x0236 }
            r5 = r9
            goto L_0x01fe
        L_0x017a:
            boolean r9 = r7 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageEntityUrl     // Catch:{ Exception -> 0x0236 }
            if (r9 == 0) goto L_0x0191
            im.bclpbkiauv.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r9.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r9 = r9.message     // Catch:{ Exception -> 0x0236 }
            int r10 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r11 = r7.offset     // Catch:{ Exception -> 0x0236 }
            int r12 = r7.length     // Catch:{ Exception -> 0x0236 }
            int r11 = r11 + r12
            java.lang.String r9 = r9.substring(r10, r11)     // Catch:{ Exception -> 0x0236 }
            r0 = r9
            goto L_0x0194
        L_0x0191:
            java.lang.String r9 = r7.url     // Catch:{ Exception -> 0x0236 }
            r0 = r9
        L_0x0194:
            if (r4 == 0) goto L_0x019c
            int r9 = r4.length()     // Catch:{ Exception -> 0x0236 }
            if (r9 != 0) goto L_0x01fe
        L_0x019c:
            r4 = r0
            android.net.Uri r9 = android.net.Uri.parse(r4)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r9.getHost()     // Catch:{ Exception -> 0x0236 }
            r4 = r10
            if (r4 != 0) goto L_0x01a9
            r4 = r0
        L_0x01a9:
            if (r4 == 0) goto L_0x01e4
            r10 = 46
            int r11 = r4.lastIndexOf(r10)     // Catch:{ Exception -> 0x0236 }
            r12 = r11
            if (r11 < 0) goto L_0x01e4
            java.lang.String r11 = r4.substring(r2, r12)     // Catch:{ Exception -> 0x0236 }
            r4 = r11
            int r10 = r4.lastIndexOf(r10)     // Catch:{ Exception -> 0x0236 }
            r11 = r10
            if (r10 < 0) goto L_0x01c7
            int r10 = r11 + 1
            java.lang.String r10 = r4.substring(r10)     // Catch:{ Exception -> 0x0236 }
            r4 = r10
        L_0x01c7:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0236 }
            r10.<init>()     // Catch:{ Exception -> 0x0236 }
            r12 = 1
            java.lang.String r15 = r4.substring(r2, r12)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r15 = r15.toUpperCase()     // Catch:{ Exception -> 0x0236 }
            r10.append(r15)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r15 = r4.substring(r12)     // Catch:{ Exception -> 0x0236 }
            r10.append(r15)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0236 }
            r4 = r10
        L_0x01e4:
            int r10 = r7.offset     // Catch:{ Exception -> 0x0236 }
            if (r10 != 0) goto L_0x01f6
            int r10 = r7.length     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r11.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r11 = r11.message     // Catch:{ Exception -> 0x0236 }
            int r11 = r11.length()     // Catch:{ Exception -> 0x0236 }
            if (r10 == r11) goto L_0x01fd
        L_0x01f6:
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message     // Catch:{ Exception -> 0x0236 }
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r10.messageOwner     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r10.message     // Catch:{ Exception -> 0x0236 }
            r5 = r10
        L_0x01fd:
        L_0x01fe:
            if (r0 == 0) goto L_0x0234
            java.lang.String r9 = r0.toLowerCase()     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = "http"
            int r9 = r9.indexOf(r10)     // Catch:{ Exception -> 0x0236 }
            if (r9 == 0) goto L_0x022f
            java.lang.String r9 = r0.toLowerCase()     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = "mailto"
            int r9 = r9.indexOf(r10)     // Catch:{ Exception -> 0x0236 }
            if (r9 == 0) goto L_0x022f
            java.util.ArrayList<java.lang.String> r9 = r1.links     // Catch:{ Exception -> 0x0236 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0236 }
            r10.<init>()     // Catch:{ Exception -> 0x0236 }
            java.lang.String r11 = "http://"
            r10.append(r11)     // Catch:{ Exception -> 0x0236 }
            r10.append(r0)     // Catch:{ Exception -> 0x0236 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0236 }
            r9.add(r10)     // Catch:{ Exception -> 0x0236 }
            goto L_0x0234
        L_0x022f:
            java.util.ArrayList<java.lang.String> r9 = r1.links     // Catch:{ Exception -> 0x0236 }
            r9.add(r0)     // Catch:{ Exception -> 0x0236 }
        L_0x0234:
            r0 = r8
            goto L_0x023b
        L_0x0236:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r8
        L_0x023b:
            int r6 = r6 + 1
            r12 = 1
            goto L_0x008f
        L_0x0240:
            r17 = r0
            r12 = r4
            r15 = r5
            goto L_0x0249
        L_0x0245:
            r12 = r0
            r15 = r4
            r17 = r5
        L_0x0249:
            if (r13 == 0) goto L_0x0258
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0258
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.add(r13)
        L_0x0258:
            r18 = 1082130432(0x40800000, float:4.0)
            if (r12 == 0) goto L_0x0291
            android.text.TextPaint r5 = r1.titleTextPaint     // Catch:{ Exception -> 0x0288 }
            r8 = 0
            r9 = 3
            r4 = r12
            r6 = r3
            r7 = r3
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.cells.ChatMessageCell.generateStaticLayout(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0288 }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x0288 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0288 }
            if (r0 <= 0) goto L_0x0287
            int r0 = r1.titleY     // Catch:{ Exception -> 0x0288 }
            android.text.StaticLayout r4 = r1.titleLayout     // Catch:{ Exception -> 0x0288 }
            android.text.StaticLayout r5 = r1.titleLayout     // Catch:{ Exception -> 0x0288 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0288 }
            r6 = 1
            int r5 = r5 - r6
            int r4 = r4.getLineBottom(r5)     // Catch:{ Exception -> 0x0288 }
            int r0 = r0 + r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x0288 }
            int r0 = r0 + r4
            r1.descriptionY = r0     // Catch:{ Exception -> 0x0288 }
        L_0x0287:
            goto L_0x028c
        L_0x0288:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x028c:
            im.bclpbkiauv.ui.components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r12)
        L_0x0291:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x029e
            int r0 = r0.getLineCount()
            goto L_0x029f
        L_0x029e:
            r0 = 0
        L_0x029f:
            int r0 = 4 - r0
            r4 = 1
            int r19 = java.lang.Math.max(r4, r0)
            r20 = 1084227584(0x40a00000, float:5.0)
            if (r15 == 0) goto L_0x02db
            android.text.TextPaint r5 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x02d7 }
            r8 = 0
            r4 = r15
            r6 = r3
            r7 = r3
            r9 = r19
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.cells.ChatMessageCell.generateStaticLayout(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x02d7 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x02d7 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x02d7 }
            if (r0 <= 0) goto L_0x02d6
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x02d7 }
            android.text.StaticLayout r4 = r1.descriptionLayout     // Catch:{ Exception -> 0x02d7 }
            android.text.StaticLayout r5 = r1.descriptionLayout     // Catch:{ Exception -> 0x02d7 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x02d7 }
            r6 = 1
            int r5 = r5 - r6
            int r4 = r4.getLineBottom(r5)     // Catch:{ Exception -> 0x02d7 }
            int r0 = r0 + r4
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x02d7 }
            int r0 = r0 + r4
            r1.description2Y = r0     // Catch:{ Exception -> 0x02d7 }
        L_0x02d6:
            goto L_0x02db
        L_0x02d7:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02db:
            r21 = 1092616192(0x41200000, float:10.0)
            if (r17 == 0) goto L_0x0300
            android.text.TextPaint r5 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x02fc }
            r8 = 0
            r4 = r17
            r6 = r3
            r7 = r3
            r9 = r19
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.cells.ChatMessageCell.generateStaticLayout(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x02fc }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x02fc }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x02fc }
            if (r0 == 0) goto L_0x02fb
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x02fc }
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)     // Catch:{ Exception -> 0x02fc }
            int r0 = r0 + r4
            r1.description2Y = r0     // Catch:{ Exception -> 0x02fc }
        L_0x02fb:
            goto L_0x0300
        L_0x02fc:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0300:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0394
            r0 = 0
            r11 = r0
        L_0x030a:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            int r0 = r0.size()
            if (r11 >= r0) goto L_0x0392
            java.util.ArrayList<java.lang.String> r0 = r1.links     // Catch:{ Exception -> 0x0388 }
            java.lang.Object r0 = r0.get(r11)     // Catch:{ Exception -> 0x0388 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0388 }
            android.text.TextPaint r4 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0388 }
            float r4 = r4.measureText(r0)     // Catch:{ Exception -> 0x0388 }
            double r4 = (double) r4     // Catch:{ Exception -> 0x0388 }
            double r4 = java.lang.Math.ceil(r4)     // Catch:{ Exception -> 0x0388 }
            int r10 = (int) r4     // Catch:{ Exception -> 0x0388 }
            r4 = 10
            r5 = 32
            java.lang.String r4 = r0.replace(r4, r5)     // Catch:{ Exception -> 0x0388 }
            android.text.TextPaint r5 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0388 }
            int r6 = java.lang.Math.min(r10, r3)     // Catch:{ Exception -> 0x0388 }
            float r6 = (float) r6     // Catch:{ Exception -> 0x0388 }
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0388 }
            java.lang.CharSequence r5 = android.text.TextUtils.ellipsize(r4, r5, r6, r7)     // Catch:{ Exception -> 0x0388 }
            android.text.StaticLayout r22 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0388 }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0388 }
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0388 }
            r9 = 1065353216(0x3f800000, float:1.0)
            r23 = 0
            r24 = 0
            r4 = r22
            r7 = r3
            r25 = r10
            r10 = r23
            r23 = r11
            r11 = r24
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0386 }
            r4 = r22
            int r6 = r1.description2Y     // Catch:{ Exception -> 0x0386 }
            r1.linkY = r6     // Catch:{ Exception -> 0x0386 }
            android.text.StaticLayout r6 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0386 }
            if (r6 == 0) goto L_0x037f
            android.text.StaticLayout r6 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0386 }
            int r6 = r6.getLineCount()     // Catch:{ Exception -> 0x0386 }
            if (r6 == 0) goto L_0x037f
            int r6 = r1.linkY     // Catch:{ Exception -> 0x0386 }
            android.text.StaticLayout r7 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0386 }
            android.text.StaticLayout r8 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0386 }
            int r8 = r8.getLineCount()     // Catch:{ Exception -> 0x0386 }
            r9 = 1
            int r8 = r8 - r9
            int r7 = r7.getLineBottom(r8)     // Catch:{ Exception -> 0x0386 }
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)     // Catch:{ Exception -> 0x0386 }
            int r7 = r7 + r8
            int r6 = r6 + r7
            r1.linkY = r6     // Catch:{ Exception -> 0x0386 }
        L_0x037f:
            java.util.ArrayList<android.text.StaticLayout> r6 = r1.linkLayout     // Catch:{ Exception -> 0x0386 }
            r6.add(r4)     // Catch:{ Exception -> 0x0386 }
            goto L_0x038e
        L_0x0386:
            r0 = move-exception
            goto L_0x038b
        L_0x0388:
            r0 = move-exception
            r23 = r11
        L_0x038b:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x038e:
            int r11 = r23 + 1
            goto L_0x030a
        L_0x0392:
            r23 = r11
        L_0x0394:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            boolean r4 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x03a9
            int r4 = android.view.View.MeasureSpec.getSize(r33)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            int r4 = r4 - r5
            int r4 = r4 - r0
            goto L_0x03ad
        L_0x03a9:
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
        L_0x03ad:
            im.bclpbkiauv.ui.components.LetterDrawable r5 = r1.letterDrawable
            r6 = 1093664768(0x41300000, float:11.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r8 = r4 + r0
            r9 = 1115422720(0x427c0000, float:63.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r5.setBounds(r4, r7, r8, r9)
            if (r14 == 0) goto L_0x0443
            im.bclpbkiauv.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r5 = r5.photoThumbs
            r7 = 1
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r5 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r0, r7)
            im.bclpbkiauv.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 80
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            if (r7 != r5) goto L_0x03d8
            r7 = 0
        L_0x03d8:
            r8 = -1
            r5.size = r8
            if (r7 == 0) goto L_0x03df
            r7.size = r8
        L_0x03df:
            im.bclpbkiauv.messenger.ImageReceiver r8 = r1.linkImageView
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r8.setImageCoords(r4, r6, r0, r0)
            java.lang.String r6 = im.bclpbkiauv.messenger.FileLoader.getAttachFileName(r5)
            java.util.Locale r8 = java.util.Locale.US
            r9 = 2
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r10[r2] = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r16 = 1
            r10[r16] = r11
            java.lang.String r11 = "%d_%d"
            java.lang.String r8 = java.lang.String.format(r8, r11, r10)
            java.util.Locale r10 = java.util.Locale.US
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            r9[r2] = r11
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            r9[r16] = r2
            java.lang.String r2 = "%d_%d_b"
            java.lang.String r2 = java.lang.String.format(r10, r2, r9)
            im.bclpbkiauv.messenger.ImageReceiver r9 = r1.linkImageView
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message
            im.bclpbkiauv.tgnet.TLObject r10 = r10.photoThumbsObject
            im.bclpbkiauv.messenger.ImageLocation r23 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r5, r10)
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message
            im.bclpbkiauv.tgnet.TLObject r10 = r10.photoThumbsObject
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForObject(r7, r10)
            r27 = 0
            r28 = 0
            im.bclpbkiauv.messenger.MessageObject r10 = r1.message
            r30 = 0
            r22 = r9
            r24 = r8
            r26 = r2
            r29 = r10
            r22.setImage(r23, r24, r25, r26, r27, r28, r29, r30)
            r9 = 1
            r1.drawLinkImageView = r9
        L_0x0443:
            r2 = 0
            android.text.StaticLayout r5 = r1.titleLayout
            if (r5 == 0) goto L_0x0460
            int r5 = r5.getLineCount()
            if (r5 == 0) goto L_0x0460
            android.text.StaticLayout r5 = r1.titleLayout
            int r6 = r5.getLineCount()
            r7 = 1
            int r6 = r6 - r7
            int r5 = r5.getLineBottom(r6)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            int r5 = r5 + r6
            int r2 = r2 + r5
        L_0x0460:
            android.text.StaticLayout r5 = r1.descriptionLayout
            if (r5 == 0) goto L_0x047c
            int r5 = r5.getLineCount()
            if (r5 == 0) goto L_0x047c
            android.text.StaticLayout r5 = r1.descriptionLayout
            int r6 = r5.getLineCount()
            r7 = 1
            int r6 = r6 - r7
            int r5 = r5.getLineBottom(r6)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 + r6
            int r2 = r2 + r5
        L_0x047c:
            android.text.StaticLayout r5 = r1.descriptionLayout2
            if (r5 == 0) goto L_0x04a1
            int r5 = r5.getLineCount()
            if (r5 == 0) goto L_0x04a1
            android.text.StaticLayout r5 = r1.descriptionLayout2
            int r6 = r5.getLineCount()
            r7 = 1
            int r6 = r6 - r7
            int r5 = r5.getLineBottom(r6)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r20)
            int r5 = r5 + r6
            int r2 = r2 + r5
            android.text.StaticLayout r5 = r1.descriptionLayout
            if (r5 == 0) goto L_0x04a1
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r21)
            int r2 = r2 + r5
        L_0x04a1:
            r5 = 0
        L_0x04a2:
            java.util.ArrayList<android.text.StaticLayout> r6 = r1.linkLayout
            int r6 = r6.size()
            if (r5 >= r6) goto L_0x04c8
            java.util.ArrayList<android.text.StaticLayout> r6 = r1.linkLayout
            java.lang.Object r6 = r6.get(r5)
            android.text.StaticLayout r6 = (android.text.StaticLayout) r6
            int r7 = r6.getLineCount()
            if (r7 <= 0) goto L_0x04c4
            int r7 = r6.getLineCount()
            r8 = 1
            int r7 = r7 - r8
            int r7 = r6.getLineBottom(r7)
            int r2 = r2 + r7
            goto L_0x04c5
        L_0x04c4:
            r8 = 1
        L_0x04c5:
            int r5 = r5 + 1
            goto L_0x04a2
        L_0x04c8:
            im.bclpbkiauv.ui.components.CheckBox2 r5 = r1.checkBox
            r6 = 1103101952(0x41c00000, float:24.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r8 = 1073741824(0x40000000, float:2.0)
            int r7 = android.view.View.MeasureSpec.makeMeasureSpec(r7, r8)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r6 = android.view.View.MeasureSpec.makeMeasureSpec(r6, r8)
            r5.measure(r7, r6)
            int r5 = android.view.View.MeasureSpec.getSize(r33)
            r6 = 1117257728(0x42980000, float:76.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            r7 = 1099431936(0x41880000, float:17.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r2
            int r6 = java.lang.Math.max(r6, r7)
            boolean r7 = r1.needDivider
            int r6 = r6 + r7
            r1.setMeasuredDimension(r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        SharedLinkCellDelegate sharedLinkCellDelegate;
        boolean result = false;
        if (this.message == null || this.linkLayout.isEmpty() || (sharedLinkCellDelegate = this.delegate) == null || !sharedLinkCellDelegate.canPerformActions()) {
            resetPressedLink();
        } else if (event.getAction() == 0 || (this.linkPreviewPressed && event.getAction() == 1)) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean ok = false;
            int a = 0;
            int offset = 0;
            while (true) {
                if (a >= this.linkLayout.size()) {
                    break;
                }
                StaticLayout layout = this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    int height = layout.getLineBottom(layout.getLineCount() - 1);
                    int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                    if (((float) x) >= ((float) linkPosX) + layout.getLineLeft(0) && ((float) x) <= ((float) linkPosX) + layout.getLineWidth(0)) {
                        int i = this.linkY;
                        if (y >= i + offset && y <= i + offset + height) {
                            ok = true;
                            if (event.getAction() == 0) {
                                resetPressedLink();
                                this.pressedLink = a;
                                this.linkPreviewPressed = true;
                                startCheckLongPress();
                                try {
                                    this.urlPath.setCurrentLayout(layout, 0, 0.0f);
                                    layout.getSelectionPath(0, layout.getText().length(), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                result = true;
                            } else if (this.linkPreviewPressed) {
                                try {
                                    TLRPC.WebPage webPage = (this.pressedLink != 0 || this.message.messageOwner.media == null) ? null : this.message.messageOwner.media.webpage;
                                    if (webPage == null || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                        Browser.openUrl(getContext(), this.links.get(this.pressedLink));
                                    } else {
                                        this.delegate.needOpenWebView(webPage);
                                    }
                                } catch (Exception e2) {
                                    FileLog.e((Throwable) e2);
                                }
                                resetPressedLink();
                                result = true;
                            }
                        }
                    }
                    offset += height;
                }
                a++;
            }
            if (!ok) {
                resetPressedLink();
            }
        } else if (event.getAction() == 3) {
            resetPressedLink();
        }
        if (result || super.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public String getLink(int num) {
        if (num < 0 || num >= this.links.size()) {
            return null;
        }
        return this.links.get(num);
    }

    /* access modifiers changed from: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + offset));
                    if (this.pressedLink == a) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    layout.draw(canvas);
                    canvas.restore();
                    offset += layout.getLineBottom(layout.getLineCount() - 1);
                }
            }
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        if (this.checkBox.isChecked()) {
            info.setChecked(true);
            info.setCheckable(true);
        }
    }
}
