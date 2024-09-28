package im.bclpbkiauv.ui.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.DownloadController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.ImageReceiver;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MediaController;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.messenger.WebFile;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.PhotoViewer;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.LetterDrawable;
import im.bclpbkiauv.ui.components.RadialProgress2;
import java.io.File;

public class ContextLinkCell extends View implements DownloadController.FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private boolean canPreviewGif;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private TLRPC.PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private TLRPC.BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private TLRPC.Photo photoAttach;
    private RadialProgress2 radialProgress;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        super(context);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
    }

    /* JADX WARNING: type inference failed for: r3v51, types: [im.bclpbkiauv.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r3v53, types: [im.bclpbkiauv.tgnet.TLRPC$WebDocument] */
    /* JADX WARNING: type inference failed for: r3v55, types: [im.bclpbkiauv.tgnet.TLRPC$WebDocument] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x02c5  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0300  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x032c  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0389  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0392  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x039c  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x040f  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x04e3  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0521  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0142 A[SYNTHETIC, Splitter:B:42:0x0142] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r34, int r35) {
        /*
            r33 = this;
            r1 = r33
            r2 = 0
            r1.drawLinkImageView = r2
            r0 = 0
            r1.descriptionLayout = r0
            r1.titleLayout = r0
            r1.linkLayout = r0
            r1.currentPhotoObject = r0
            r0 = 1104674816(0x41d80000, float:27.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            r1.linkY = r0
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            r3 = 1120403456(0x42c80000, float:100.0)
            if (r0 != 0) goto L_0x002c
            im.bclpbkiauv.tgnet.TLRPC$Document r0 = r1.documentAttach
            if (r0 != 0) goto L_0x002c
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            r1.setMeasuredDimension(r0, r2)
            return
        L_0x002c:
            int r4 = android.view.View.MeasureSpec.getSize(r34)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r0)
            int r0 = r4 - r0
            r5 = 1090519040(0x41000000, float:8.0)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            int r6 = r0 - r6
            r15 = 0
            r0 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            if (r7 == 0) goto L_0x0059
            java.util.ArrayList r7 = new java.util.ArrayList
            im.bclpbkiauv.tgnet.TLRPC$Document r8 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r8 = r8.thumbs
            r7.<init>(r8)
            r0 = r7
            r14 = r0
            goto L_0x0070
        L_0x0059:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult
            if (r7 == 0) goto L_0x006f
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r7.photo
            if (r7 == 0) goto L_0x006f
            java.util.ArrayList r7 = new java.util.ArrayList
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r8 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            r7.<init>(r8)
            r0 = r7
            r14 = r0
            goto L_0x0070
        L_0x006f:
            r14 = r0
        L_0x0070:
            boolean r0 = r1.mediaWebpage
            r19 = 1082130432(0x40800000, float:4.0)
            r13 = 1
            if (r0 != 0) goto L_0x018b
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            if (r0 == 0) goto L_0x018b
            java.lang.String r0 = r0.title
            r12 = 32
            r11 = 10
            if (r0 == 0) goto L_0x00df
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d2 }
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r7 = r7.title     // Catch:{ Exception -> 0x00d2 }
            float r0 = r0.measureText(r7)     // Catch:{ Exception -> 0x00d2 }
            double r7 = (double) r0     // Catch:{ Exception -> 0x00d2 }
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x00d2 }
            int r0 = (int) r7     // Catch:{ Exception -> 0x00d2 }
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r7 = r7.title     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r7 = r7.replace(r11, r12)     // Catch:{ Exception -> 0x00d2 }
            android.text.TextPaint r8 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d2 }
            android.graphics.Paint$FontMetricsInt r8 = r8.getFontMetricsInt()     // Catch:{ Exception -> 0x00d2 }
            r9 = 1097859072(0x41700000, float:15.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)     // Catch:{ Exception -> 0x00d2 }
            java.lang.CharSequence r7 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r7, r8, r9, r2)     // Catch:{ Exception -> 0x00d2 }
            android.text.TextPaint r8 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d2 }
            int r9 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x00d2 }
            float r9 = (float) r9     // Catch:{ Exception -> 0x00d2 }
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x00d2 }
            java.lang.CharSequence r21 = android.text.TextUtils.ellipsize(r7, r8, r9, r10)     // Catch:{ Exception -> 0x00d2 }
            android.text.StaticLayout r7 = new android.text.StaticLayout     // Catch:{ Exception -> 0x00d2 }
            android.text.TextPaint r22 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_titleTextPaint     // Catch:{ Exception -> 0x00d2 }
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)     // Catch:{ Exception -> 0x00d2 }
            int r23 = r6 + r8
            android.text.Layout$Alignment r24 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x00d2 }
            r25 = 1065353216(0x3f800000, float:1.0)
            r26 = 0
            r27 = 0
            r20 = r7
            r20.<init>(r21, r22, r23, r24, r25, r26, r27)     // Catch:{ Exception -> 0x00d2 }
            r1.titleLayout = r7     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x00d2:
            r0 = move-exception
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00d6:
            im.bclpbkiauv.ui.components.LetterDrawable r0 = r1.letterDrawable
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult
            java.lang.String r7 = r7.title
            r0.setTitle(r7)
        L_0x00df:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0138
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult     // Catch:{ Exception -> 0x012f }
            java.lang.String r0 = r0.description     // Catch:{ Exception -> 0x012f }
            android.text.TextPaint r7 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x012f }
            android.graphics.Paint$FontMetricsInt r7 = r7.getFontMetricsInt()     // Catch:{ Exception -> 0x012f }
            r8 = 1095761920(0x41500000, float:13.0)
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)     // Catch:{ Exception -> 0x012f }
            java.lang.CharSequence r7 = im.bclpbkiauv.messenger.Emoji.replaceEmoji(r0, r7, r8, r2)     // Catch:{ Exception -> 0x012f }
            android.text.TextPaint r8 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x012f }
            r0 = 0
            r20 = 3
            r9 = r6
            r10 = r6
            r5 = 10
            r11 = r0
            r3 = 32
            r12 = r20
            android.text.StaticLayout r0 = im.bclpbkiauv.ui.cells.ChatMessageCell.generateStaticLayout(r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x012d }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x012d }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x012d }
            if (r0 <= 0) goto L_0x012c
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x012d }
            android.text.StaticLayout r7 = r1.descriptionLayout     // Catch:{ Exception -> 0x012d }
            android.text.StaticLayout r8 = r1.descriptionLayout     // Catch:{ Exception -> 0x012d }
            int r8 = r8.getLineCount()     // Catch:{ Exception -> 0x012d }
            int r8 = r8 - r13
            int r7 = r7.getLineBottom(r8)     // Catch:{ Exception -> 0x012d }
            int r0 = r0 + r7
            r7 = 1065353216(0x3f800000, float:1.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)     // Catch:{ Exception -> 0x012d }
            int r0 = r0 + r7
            r1.linkY = r0     // Catch:{ Exception -> 0x012d }
        L_0x012c:
            goto L_0x013c
        L_0x012d:
            r0 = move-exception
            goto L_0x0134
        L_0x012f:
            r0 = move-exception
            r3 = 32
            r5 = 10
        L_0x0134:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x013c
        L_0x0138:
            r3 = 32
            r5 = 10
        L_0x013c:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r0 = r1.inlineResult
            java.lang.String r0 = r0.url
            if (r0 == 0) goto L_0x0188
            android.text.TextPaint r0 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0181 }
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult     // Catch:{ Exception -> 0x0181 }
            java.lang.String r7 = r7.url     // Catch:{ Exception -> 0x0181 }
            float r0 = r0.measureText(r7)     // Catch:{ Exception -> 0x0181 }
            double r7 = (double) r0     // Catch:{ Exception -> 0x0181 }
            double r7 = java.lang.Math.ceil(r7)     // Catch:{ Exception -> 0x0181 }
            int r0 = (int) r7     // Catch:{ Exception -> 0x0181 }
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult     // Catch:{ Exception -> 0x0181 }
            java.lang.String r7 = r7.url     // Catch:{ Exception -> 0x0181 }
            java.lang.String r3 = r7.replace(r5, r3)     // Catch:{ Exception -> 0x0181 }
            android.text.TextPaint r5 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0181 }
            int r7 = java.lang.Math.min(r0, r6)     // Catch:{ Exception -> 0x0181 }
            float r7 = (float) r7     // Catch:{ Exception -> 0x0181 }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0181 }
            java.lang.CharSequence r8 = android.text.TextUtils.ellipsize(r3, r5, r7, r8)     // Catch:{ Exception -> 0x0181 }
            android.text.StaticLayout r3 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0181 }
            android.text.TextPaint r9 = im.bclpbkiauv.ui.actionbar.Theme.chat_contextResult_descriptionTextPaint     // Catch:{ Exception -> 0x0181 }
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0181 }
            r12 = 1065353216(0x3f800000, float:1.0)
            r5 = 0
            r20 = 0
            r7 = r3
            r10 = r6
            r2 = 1
            r13 = r5
            r5 = r14
            r14 = r20
            r7.<init>(r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x017f }
            r1.linkLayout = r3     // Catch:{ Exception -> 0x017f }
            goto L_0x018d
        L_0x017f:
            r0 = move-exception
            goto L_0x0184
        L_0x0181:
            r0 = move-exception
            r5 = r14
            r2 = 1
        L_0x0184:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x018d
        L_0x0188:
            r5 = r14
            r2 = 1
            goto L_0x018d
        L_0x018b:
            r5 = r14
            r2 = 1
        L_0x018d:
            r0 = 0
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            r7 = 3
            r8 = 5
            r9 = 80
            if (r3 == 0) goto L_0x01d8
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isGifDocument((im.bclpbkiauv.tgnet.TLRPC.Document) r3)
            r10 = 90
            if (r3 == 0) goto L_0x01a9
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r10)
            r1.currentPhotoObject = r3
            goto L_0x01f3
        L_0x01a9:
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isStickerDocument(r3)
            if (r3 != 0) goto L_0x01cb
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            boolean r3 = im.bclpbkiauv.messenger.MessageObject.isAnimatedStickerDocument(r3)
            if (r3 == 0) goto L_0x01ba
            goto L_0x01cb
        L_0x01ba:
            int r3 = r1.documentAttachType
            if (r3 == r8) goto L_0x01f3
            if (r3 == r7) goto L_0x01f3
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r10)
            r1.currentPhotoObject = r3
            goto L_0x01f3
        L_0x01cb:
            im.bclpbkiauv.tgnet.TLRPC$Document r3 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r10)
            r1.currentPhotoObject = r3
            java.lang.String r0 = "webp"
            goto L_0x01f3
        L_0x01d8:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            if (r3 == 0) goto L_0x01f3
            im.bclpbkiauv.tgnet.TLRPC$Photo r3 = r3.photo
            if (r3 == 0) goto L_0x01f3
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.getPhotoSize()
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3, r2)
            r1.currentPhotoObject = r3
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r15 = im.bclpbkiauv.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r9)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r15 != r3) goto L_0x01f3
            r15 = 0
        L_0x01f3:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            r10 = 2
            if (r3 == 0) goto L_0x02bf
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.content
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webDocument
            if (r3 == 0) goto L_0x0240
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            java.lang.String r3 = r3.type
            if (r3 == 0) goto L_0x0240
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            java.lang.String r3 = r3.type
            java.lang.String r11 = "gif"
            boolean r3 = r3.startsWith(r11)
            if (r3 == 0) goto L_0x021b
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.content
            r17 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_webDocument r17 = (im.bclpbkiauv.tgnet.TLRPC.TL_webDocument) r17
            r1.documentAttachType = r10
            goto L_0x0240
        L_0x021b:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            java.lang.String r3 = r3.type
            java.lang.String r11 = "photo"
            boolean r3 = r3.equals(r11)
            if (r3 == 0) goto L_0x0240
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.thumb
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webDocument
            if (r3 == 0) goto L_0x0238
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.thumb
            r17 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_webDocument r17 = (im.bclpbkiauv.tgnet.TLRPC.TL_webDocument) r17
            goto L_0x0240
        L_0x0238:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.content
            r17 = r3
            im.bclpbkiauv.tgnet.TLRPC$TL_webDocument r17 = (im.bclpbkiauv.tgnet.TLRPC.TL_webDocument) r17
        L_0x0240:
            if (r17 != 0) goto L_0x0252
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.thumb
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_webDocument
            if (r3 == 0) goto L_0x0252
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$WebDocument r3 = r3.thumb
            im.bclpbkiauv.tgnet.TLRPC$TL_webDocument r3 = (im.bclpbkiauv.tgnet.TLRPC.TL_webDocument) r3
            r17 = r3
        L_0x0252:
            if (r17 != 0) goto L_0x02b9
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r3 = r1.currentPhotoObject
            if (r3 != 0) goto L_0x02b9
            if (r15 != 0) goto L_0x02b9
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$BotInlineMessage r3 = r3.send_message
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_botInlineMessageMediaVenue
            if (r3 != 0) goto L_0x026a
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$BotInlineMessage r3 = r3.send_message
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_botInlineMessageMediaGeo
            if (r3 == 0) goto L_0x02b9
        L_0x026a:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$BotInlineMessage r3 = r3.send_message
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r3 = r3.geo
            double r11 = r3.lat
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$BotInlineMessage r3 = r3.send_message
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r3 = r3.geo
            double r13 = r3._long
            int r3 = r1.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            int r3 = r3.mapProvider
            if (r3 != r10) goto L_0x02a5
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r3 = r1.inlineResult
            im.bclpbkiauv.tgnet.TLRPC$BotInlineMessage r3 = r3.send_message
            im.bclpbkiauv.tgnet.TLRPC$GeoPoint r3 = r3.geo
            r8 = 15
            float r7 = im.bclpbkiauv.messenger.AndroidUtilities.density
            r24 = r3
            double r2 = (double) r7
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r2 = java.lang.Math.min(r10, r2)
            r3 = 72
            r7 = r24
            im.bclpbkiauv.messenger.WebFile r2 = im.bclpbkiauv.messenger.WebFile.createWithGeoPoint(r7, r3, r3, r8, r2)
            r16 = r2
            goto L_0x02b9
        L_0x02a5:
            int r2 = r1.currentAccount
            r29 = 72
            r30 = 72
            r31 = 1
            r32 = 15
            r24 = r2
            r25 = r11
            r27 = r13
            java.lang.String r18 = im.bclpbkiauv.messenger.AndroidUtilities.formapMapUrl(r24, r25, r27, r29, r30, r31, r32)
        L_0x02b9:
            if (r17 == 0) goto L_0x02bf
            im.bclpbkiauv.messenger.WebFile r16 = im.bclpbkiauv.messenger.WebFile.createWithWebDocument(r17)
        L_0x02bf:
            r2 = 0
            r3 = 0
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            if (r7 == 0) goto L_0x02ea
            r7 = 0
        L_0x02c6:
            im.bclpbkiauv.tgnet.TLRPC$Document r8 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x02ea
            im.bclpbkiauv.tgnet.TLRPC$Document r8 = r1.documentAttach
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes
            java.lang.Object r8 = r8.get(r7)
            im.bclpbkiauv.tgnet.TLRPC$DocumentAttribute r8 = (im.bclpbkiauv.tgnet.TLRPC.DocumentAttribute) r8
            boolean r11 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeImageSize
            if (r11 != 0) goto L_0x02e6
            boolean r11 = r8 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_documentAttributeVideo
            if (r11 == 0) goto L_0x02e3
            goto L_0x02e6
        L_0x02e3:
            int r7 = r7 + 1
            goto L_0x02c6
        L_0x02e6:
            int r2 = r8.w
            int r3 = r8.h
        L_0x02ea:
            if (r2 == 0) goto L_0x02ee
            if (r3 != 0) goto L_0x030e
        L_0x02ee:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            if (r7 == 0) goto L_0x0300
            if (r15 == 0) goto L_0x02f7
            r7 = -1
            r15.size = r7
        L_0x02f7:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            int r2 = r7.w
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            int r3 = r7.h
            goto L_0x030e
        L_0x0300:
            im.bclpbkiauv.tgnet.TLRPC$BotInlineResult r7 = r1.inlineResult
            if (r7 == 0) goto L_0x030e
            int[] r7 = im.bclpbkiauv.messenger.MessageObject.getInlineResultWidthAndHeight(r7)
            r8 = 0
            r2 = r7[r8]
            r8 = 1
            r3 = r7[r8]
        L_0x030e:
            r7 = 1117782016(0x42a00000, float:80.0)
            if (r2 == 0) goto L_0x0314
            if (r3 != 0) goto L_0x031a
        L_0x0314:
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            r3 = r8
            r2 = r8
        L_0x031a:
            im.bclpbkiauv.tgnet.TLRPC$Document r8 = r1.documentAttach
            if (r8 != 0) goto L_0x0326
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r8 = r1.currentPhotoObject
            if (r8 != 0) goto L_0x0326
            if (r16 != 0) goto L_0x0326
            if (r18 == 0) goto L_0x04db
        L_0x0326:
            java.lang.String r8 = "52_52_b"
            boolean r11 = r1.mediaWebpage
            if (r11 == 0) goto L_0x0389
            float r11 = (float) r2
            float r12 = (float) r3
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r12 = r12 / r7
            float r11 = r11 / r12
            int r7 = (int) r11
            int r11 = r1.documentAttachType
            if (r11 != r10) goto L_0x035a
            java.util.Locale r11 = java.util.Locale.US
            java.lang.Object[] r12 = new java.lang.Object[r10]
            float r13 = (float) r7
            float r14 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r13 = r13 / r14
            int r13 = (int) r13
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r14 = 0
            r12[r14] = r13
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r13 = 1
            r12[r13] = r9
            java.lang.String r9 = "%d_%d_b"
            java.lang.String r9 = java.lang.String.format(r11, r9, r12)
            r11 = r9
            r8 = r9
            goto L_0x038b
        L_0x035a:
            java.util.Locale r11 = java.util.Locale.US
            java.lang.Object[] r12 = new java.lang.Object[r10]
            float r13 = (float) r7
            float r14 = im.bclpbkiauv.messenger.AndroidUtilities.density
            float r13 = r13 / r14
            int r13 = (int) r13
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r14 = 0
            r12[r14] = r13
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r13 = 1
            r12[r13] = r9
            java.lang.String r9 = "%d_%d"
            java.lang.String r11 = java.lang.String.format(r11, r9, r12)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r11)
            java.lang.String r12 = "_b"
            r9.append(r12)
            java.lang.String r8 = r9.toString()
            goto L_0x038b
        L_0x0389:
            java.lang.String r11 = "52_52"
        L_0x038b:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            int r9 = r1.documentAttachType
            r12 = 6
            if (r9 != r12) goto L_0x0394
            r13 = 1
            goto L_0x0395
        L_0x0394:
            r13 = 0
        L_0x0395:
            r7.setAspectFit(r13)
            int r7 = r1.documentAttachType
            if (r7 != r10) goto L_0x040f
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            if (r7 == 0) goto L_0x03c7
            im.bclpbkiauv.messenger.ImageReceiver r9 = r1.linkImageView
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r7)
            r26 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            im.bclpbkiauv.tgnet.TLRPC$Document r12 = r1.documentAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r7, r12)
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            int r7 = r7.size
            java.lang.Object r12 = r1.parentObject
            r32 = 0
            r24 = r9
            r28 = r11
            r29 = r7
            r30 = r0
            r31 = r12
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x03c7:
            if (r16 == 0) goto L_0x03ec
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForWebFile(r16)
            r26 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            im.bclpbkiauv.tgnet.TLRPC$Photo r12 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r9, r12)
            r29 = -1
            java.lang.Object r9 = r1.parentObject
            r32 = 1
            r24 = r7
            r28 = r11
            r30 = r0
            r31 = r9
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x03ec:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForPath(r18)
            r26 = 0
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            im.bclpbkiauv.tgnet.TLRPC$Photo r12 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r9, r12)
            r29 = -1
            java.lang.Object r9 = r1.parentObject
            r32 = 1
            r24 = r7
            r28 = r11
            r30 = r0
            r31 = r9
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x040f:
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            if (r7 == 0) goto L_0x0497
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            boolean r7 = im.bclpbkiauv.messenger.MessageObject.canAutoplayAnimatedSticker(r7)
            if (r7 == 0) goto L_0x0444
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.tgnet.TLRPC$Document r9 = r1.documentAttach
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r9)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            im.bclpbkiauv.tgnet.TLRPC$Document r12 = r1.documentAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r9, r12)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            int r9 = r9.size
            r30 = 0
            java.lang.Object r12 = r1.parentObject
            r32 = 0
            java.lang.String r26 = "80_80"
            r24 = r7
            r28 = r8
            r29 = r9
            r31 = r12
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x0444:
            im.bclpbkiauv.tgnet.TLRPC$Document r7 = r1.documentAttach
            if (r7 == 0) goto L_0x046f
            im.bclpbkiauv.messenger.ImageReceiver r9 = r1.linkImageView
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r12 = r1.currentPhotoObject
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForDocument(r12, r7)
            im.bclpbkiauv.tgnet.TLRPC$Photo r7 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r15, r7)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r7 = r1.currentPhotoObject
            int r7 = r7.size
            java.lang.Object r12 = r1.parentObject
            r32 = 0
            r24 = r9
            r26 = r11
            r28 = r8
            r29 = r7
            r30 = r0
            r31 = r12
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x046f:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            im.bclpbkiauv.tgnet.TLRPC$Photo r12 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r9, r12)
            im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r15, r9)
            im.bclpbkiauv.tgnet.TLRPC$PhotoSize r9 = r1.currentPhotoObject
            int r9 = r9.size
            java.lang.Object r12 = r1.parentObject
            r32 = 0
            r24 = r7
            r26 = r11
            r28 = r8
            r29 = r9
            r30 = r0
            r31 = r12
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x0497:
            if (r16 == 0) goto L_0x04b9
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForWebFile(r16)
            im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r15, r9)
            r29 = -1
            java.lang.Object r9 = r1.parentObject
            r32 = 1
            r24 = r7
            r26 = r11
            r28 = r8
            r30 = r0
            r31 = r9
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
            goto L_0x04d8
        L_0x04b9:
            im.bclpbkiauv.messenger.ImageReceiver r7 = r1.linkImageView
            im.bclpbkiauv.messenger.ImageLocation r25 = im.bclpbkiauv.messenger.ImageLocation.getForPath(r18)
            im.bclpbkiauv.tgnet.TLRPC$Photo r9 = r1.photoAttach
            im.bclpbkiauv.messenger.ImageLocation r27 = im.bclpbkiauv.messenger.ImageLocation.getForPhoto(r15, r9)
            r29 = -1
            java.lang.Object r9 = r1.parentObject
            r32 = 1
            r24 = r7
            r26 = r11
            r28 = r8
            r30 = r0
            r31 = r9
            r24.setImage(r25, r26, r27, r28, r29, r30, r31, r32)
        L_0x04d8:
            r7 = 1
            r1.drawLinkImageView = r7
        L_0x04db:
            boolean r7 = r1.mediaWebpage
            r8 = 1094713344(0x41400000, float:12.0)
            r9 = 1103101952(0x41c00000, float:24.0)
            if (r7 == 0) goto L_0x0521
            r7 = r4
            int r11 = android.view.View.MeasureSpec.getSize(r35)
            if (r11 != 0) goto L_0x04f0
            r12 = 1120403456(0x42c80000, float:100.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
        L_0x04f0:
            r1.setMeasuredDimension(r7, r11)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r12 = r7 - r12
            int r12 = r12 / r10
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r13 = r11 - r13
            int r13 = r13 / r10
            im.bclpbkiauv.ui.components.RadialProgress2 r10 = r1.radialProgress
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r14 = r14 + r12
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r9 = r9 + r13
            r10.setProgressRect(r12, r13, r14, r9)
            im.bclpbkiauv.ui.components.RadialProgress2 r9 = r1.radialProgress
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r8)
            r9.setCircleRadius(r8)
            im.bclpbkiauv.messenger.ImageReceiver r8 = r1.linkImageView
            r9 = 0
            r8.setImageCoords(r9, r9, r7, r11)
            goto L_0x05f1
        L_0x0521:
            r7 = 0
            android.text.StaticLayout r10 = r1.titleLayout
            if (r10 == 0) goto L_0x0539
            int r10 = r10.getLineCount()
            if (r10 == 0) goto L_0x0539
            android.text.StaticLayout r10 = r1.titleLayout
            int r11 = r10.getLineCount()
            r12 = 1
            int r11 = r11 - r12
            int r10 = r10.getLineBottom(r11)
            int r7 = r7 + r10
        L_0x0539:
            android.text.StaticLayout r10 = r1.descriptionLayout
            if (r10 == 0) goto L_0x0550
            int r10 = r10.getLineCount()
            if (r10 == 0) goto L_0x0550
            android.text.StaticLayout r10 = r1.descriptionLayout
            int r11 = r10.getLineCount()
            r12 = 1
            int r11 = r11 - r12
            int r10 = r10.getLineBottom(r11)
            int r7 = r7 + r10
        L_0x0550:
            android.text.StaticLayout r10 = r1.linkLayout
            if (r10 == 0) goto L_0x0567
            int r10 = r10.getLineCount()
            if (r10 <= 0) goto L_0x0567
            android.text.StaticLayout r10 = r1.linkLayout
            int r11 = r10.getLineCount()
            r12 = 1
            int r11 = r11 - r12
            int r10 = r10.getLineBottom(r11)
            int r7 = r7 + r10
        L_0x0567:
            r10 = 1112539136(0x42500000, float:52.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            int r7 = java.lang.Math.max(r11, r7)
            int r11 = android.view.View.MeasureSpec.getSize(r34)
            r12 = 1116209152(0x42880000, float:68.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r13 = 1098907648(0x41800000, float:16.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r7
            int r12 = java.lang.Math.max(r12, r13)
            boolean r13 = r1.needDivider
            int r12 = r12 + r13
            r1.setMeasuredDimension(r11, r12)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x05a1
            int r11 = android.view.View.MeasureSpec.getSize(r34)
            r12 = 1090519040(0x41000000, float:8.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            int r11 = r11 - r13
            int r11 = r11 - r10
            goto L_0x05a7
        L_0x05a1:
            r12 = 1090519040(0x41000000, float:8.0)
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
        L_0x05a7:
            im.bclpbkiauv.ui.components.LetterDrawable r13 = r1.letterDrawable
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            int r8 = r11 + r10
            r22 = 1114636288(0x42700000, float:60.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            r13.setBounds(r11, r14, r8, r9)
            im.bclpbkiauv.messenger.ImageReceiver r8 = r1.linkImageView
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r8.setImageCoords(r11, r9, r10, r10)
            int r8 = r1.documentAttachType
            r9 = 3
            if (r8 == r9) goto L_0x05c9
            r9 = 5
            if (r8 != r9) goto L_0x05f1
        L_0x05c9:
            im.bclpbkiauv.ui.components.RadialProgress2 r8 = r1.radialProgress
            r9 = 1103101952(0x41c00000, float:24.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            r8.setCircleRadius(r9)
            im.bclpbkiauv.ui.components.RadialProgress2 r8 = r1.radialProgress
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r9 = r9 + r11
            r12 = 1094713344(0x41400000, float:12.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            r13 = 1111490560(0x42400000, float:48.0)
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            int r13 = r13 + r11
            r14 = 1113587712(0x42600000, float:56.0)
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            r8.setProgressRect(r9, r12, r13, r14)
        L_0x05f1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        String str;
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        TLRPC.Document document = this.documentAttach;
        if (document == null) {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                if (botInlineResult.photo != null) {
                    this.documentAttachType = 7;
                } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                    this.documentAttachType = 5;
                } else if (this.inlineResult.type.equals("voice")) {
                    this.documentAttachType = 3;
                }
            }
        } else if (MessageObject.isGifDocument(document)) {
            this.documentAttachType = 2;
        } else if (MessageObject.isStickerDocument(this.documentAttach) || MessageObject.isAnimatedStickerDocument(this.documentAttach)) {
            this.documentAttachType = 6;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
        } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = 3;
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            TLRPC.TL_message message = new TLRPC.TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.to_id = new TLRPC.TL_peerUser();
            TLRPC.Peer peer = message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            String str2 = "";
            message.message = str2;
            message.media = new TLRPC.TL_messageMediaDocument();
            message.media.flags |= 3;
            message.media.document = new TLRPC.TL_document();
            message.media.document.file_reference = new byte[0];
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = str2;
            } else {
                String str3 = "mp3";
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str3 : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.dc_id = 0;
                TLRPC.TL_documentAttributeAudio attributeAudio = new TLRPC.TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : str2;
                if (this.inlineResult.description != null) {
                    str2 = this.inlineResult.description;
                }
                attributeAudio.performer = str2;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TLRPC.TL_documentAttributeFilename fileName = new TLRPC.TL_documentAttributeFilename();
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                String str4 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = str3;
                } else {
                    str = "ogg";
                }
                sb.append(ImageLoader.getHttpUrlExtension(str4, str));
                fileName.file_name = sb.toString();
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(Utilities.MD5(this.inlineResult.content.url));
                sb2.append(".");
                String str5 = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str3 = "ogg";
                }
                sb2.append(ImageLoader.getHttpUrlExtension(str5, str3));
                message.attachPath = new File(directory, sb2.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false);
        }
    }

    public void setLink(TLRPC.BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineResult = contextResult;
        this.parentObject = contextResult;
        if (contextResult != null) {
            this.documentAttach = contextResult.document;
            this.photoAttach = this.inlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public void setGif(TLRPC.Document document, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.inlineResult = null;
        this.parentObject = "gif" + document;
        this.documentAttach = document;
        this.photoAttach = null;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean isGif() {
        return this.documentAttachType == 2 && this.canPreviewGif;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public TLRPC.Document getDocument() {
        return this.documentAttach;
    }

    public TLRPC.BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setCanPreviewGif(boolean value) {
        this.canPreviewGif = value;
    }

    public boolean isCanPreviewGif() {
        return this.canPreviewGif;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int dp = AndroidUtilities.dp(48.0f);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    this.radialProgress.setPressed(true, false);
                    invalidate();
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.setPressed(this.buttonPressed, false);
            }
        } else {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (!(botInlineResult == null || botInlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
                if (event.getAction() == 0) {
                    if (this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = true;
                        result = true;
                    }
                } else if (this.buttonPressed) {
                    if (event.getAction() == 1) {
                        this.buttonPressed = false;
                        playSoundEffect(0);
                        this.delegate.didPressedImage(this);
                    } else if (event.getAction() == 3) {
                        this.buttonPressed = false;
                    } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                        this.buttonPressed = false;
                    }
                }
            }
        }
        if (!result) {
            return super.onTouchEvent(event);
        }
        return result;
    }

    private void didPressedButton() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            int i2 = this.buttonState;
            if (i2 == 0) {
                if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i2 == 2) {
                this.radialProgress.setProgress(0.0f, false);
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
                }
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (i2 == 4) {
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
                }
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        if (!this.mediaWebpage) {
            if (!this.drawLinkImageView || PhotoViewer.isShowingImage(this.inlineResult)) {
                this.letterDrawable.setAlpha(255);
            } else {
                this.letterDrawable.setAlpha((int) ((1.0f - this.linkImageView.getCurrentAlpha()) * 255.0f));
            }
            int i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
                this.radialProgress.draw(canvas);
            } else {
                TLRPC.BotInlineResult botInlineResult = this.inlineResult;
                if (botInlineResult == null || !botInlineResult.type.equals("file")) {
                    TLRPC.BotInlineResult botInlineResult2 = this.inlineResult;
                    if (botInlineResult2 == null || (!botInlineResult2.type.equals(MimeTypes.BASE_TYPE_AUDIO) && !this.inlineResult.type.equals("voice"))) {
                        TLRPC.BotInlineResult botInlineResult3 = this.inlineResult;
                        if (botInlineResult3 == null || (!botInlineResult3.type.equals("venue") && !this.inlineResult.type.equals("geo"))) {
                            this.letterDrawable.draw(canvas);
                        } else {
                            int w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                            int h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                            int x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
                            int y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
                            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        }
                    } else {
                        int w2 = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        int h2 = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        int x2 = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w2) / 2);
                        int y2 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h2) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(x2, y2, x2 + w2, y2 + h2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    int w3 = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    int h3 = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    int x3 = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w3) / 2);
                    int y3 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h3) / 2);
                    canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                    Theme.chat_inlineResultFile.setBounds(x3, y3, x3 + w3, y3 + h3);
                    Theme.chat_inlineResultFile.draw(canvas);
                }
            }
        } else {
            TLRPC.BotInlineResult botInlineResult4 = this.inlineResult;
            if (botInlineResult4 != null && ((botInlineResult4.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))) {
                int w4 = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                int h4 = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                int x4 = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w4) / 2);
                int y4 = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h4) / 2);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x4, y4, x4 + w4, y4 + h4);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        }
        if (this.drawLinkImageView != 0) {
            TLRPC.BotInlineResult botInlineResult5 = this.inlineResult;
            if (botInlineResult5 != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(botInlineResult5), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || (!this.scaled && this.scale != 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (this.scaled) {
                    float f2 = this.scale;
                    if (f2 != 0.8f) {
                        float f3 = f2 - (((float) dt) / 400.0f);
                        this.scale = f3;
                        if (f3 < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                float f4 = this.scale + (((float) dt) / 400.0f);
                this.scale = f4;
                if (f4 > 1.0f) {
                    this.scale = 1.0f;
                }
                invalidate();
            }
            float f5 = this.scale;
            canvas.scale(f5, f5, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && ((i = this.documentAttachType) == 7 || i == 2)) {
            this.radialProgress.draw(canvas);
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private int getIconForCurrentState() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            this.radialProgress.setColors(Theme.key_chat_inLoader, Theme.key_chat_inLoaderSelected, Theme.key_chat_inMediaIcon, Theme.key_chat_inMediaIconSelected);
            int i2 = this.buttonState;
            if (i2 == 1) {
                return 1;
            }
            if (i2 == 2) {
                return 2;
            }
            if (i2 == 4) {
                return 3;
            }
            return 0;
        }
        this.radialProgress.setColors(Theme.key_chat_mediaLoaderPhoto, Theme.key_chat_mediaLoaderPhotoSelected, Theme.key_chat_mediaLoaderPhotoIcon, Theme.key_chat_mediaLoaderPhotoIconSelected);
        if (this.buttonState == 1) {
            return 10;
        }
        return 4;
    }

    public void updateButtonState(boolean ifSame, boolean animated) {
        boolean isLoading;
        String fileName = null;
        File cacheFile = null;
        int i = this.documentAttachType;
        if (i == 5 || i == 3) {
            TLRPC.Document document = this.documentAttach;
            if (document != null) {
                fileName = FileLoader.getAttachFileName(document);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                StringBuilder sb = new StringBuilder();
                sb.append(Utilities.MD5(this.inlineResult.content.url));
                sb.append(".");
                sb.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg"));
                fileName = sb.toString();
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        } else if (this.mediaWebpage) {
            TLRPC.BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult == null) {
                TLRPC.Document document2 = this.documentAttach;
                if (document2 != null) {
                    fileName = FileLoader.getAttachFileName(document2);
                    cacheFile = FileLoader.getPathToAttach(this.documentAttach);
                }
            } else if (botInlineResult.document instanceof TLRPC.TL_document) {
                fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
            } else if (this.inlineResult.photo instanceof TLRPC.TL_photo) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                this.currentPhotoObject = closestPhotoSizeWithSize;
                fileName = FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
            } else if (this.inlineResult.content instanceof TLRPC.TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg");
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            } else if (this.inlineResult.thumb instanceof TLRPC.TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg");
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        }
        if (!TextUtils.isEmpty(fileName)) {
            if (!cacheFile.exists()) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
                int i2 = this.documentAttachType;
                float setProgress = 0.0f;
                if (i2 == 5 || i2 == 3) {
                    if (this.documentAttach != null) {
                        isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                    } else {
                        isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                    }
                    if (!isLoading) {
                        this.buttonState = 2;
                        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    } else {
                        this.buttonState = 4;
                        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
                        if (progress != null) {
                            this.radialProgress.setProgress(progress.floatValue(), animated);
                        } else {
                            this.radialProgress.setProgress(0.0f, animated);
                        }
                        this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                    }
                } else {
                    this.buttonState = 1;
                    Float progress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress2 != null) {
                        setProgress = progress2.floatValue();
                    }
                    this.radialProgress.setProgress(setProgress, false);
                    this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
                }
                invalidate();
                return;
            }
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            int i3 = this.documentAttachType;
            if (i3 == 5 || i3 == 3) {
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, animated);
            } else {
                this.buttonState = -1;
            }
            this.radialProgress.setIcon(getIconForCurrentState(), ifSame, animated);
            invalidate();
        }
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public TLRPC.BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName, boolean canceled) {
        updateButtonState(true, canceled);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sbuf = new StringBuilder();
        switch (this.documentAttachType) {
            case 1:
                sbuf.append(LocaleController.getString("AttachDocument", R.string.AttachDocument));
                break;
            case 2:
                sbuf.append(LocaleController.getString("AttachGif", R.string.AttachGif));
                break;
            case 3:
                sbuf.append(LocaleController.getString("AttachAudio", R.string.AttachAudio));
                break;
            case 4:
                sbuf.append(LocaleController.getString("AttachVideo", R.string.AttachVideo));
                break;
            case 5:
                sbuf.append(LocaleController.getString("AttachMusic", R.string.AttachMusic));
                if (!(this.descriptionLayout == null || this.titleLayout == null)) {
                    sbuf.append(", ");
                    sbuf.append(LocaleController.formatString("AccDescrMusicInfo", R.string.AccDescrMusicInfo, this.descriptionLayout.getText(), this.titleLayout.getText()));
                    break;
                }
            case 6:
                sbuf.append(LocaleController.getString("AttachSticker", R.string.AttachSticker));
                break;
            case 7:
                sbuf.append(LocaleController.getString("AttachPhoto", R.string.AttachPhoto));
                break;
            case 8:
                sbuf.append(LocaleController.getString("AttachLocation", R.string.AttachLocation));
                break;
            default:
                StaticLayout staticLayout = this.titleLayout;
                if (staticLayout != null && !TextUtils.isEmpty(staticLayout.getText())) {
                    sbuf.append(this.titleLayout.getText());
                }
                StaticLayout staticLayout2 = this.descriptionLayout;
                if (staticLayout2 != null && !TextUtils.isEmpty(staticLayout2.getText())) {
                    if (sbuf.length() > 0) {
                        sbuf.append(", ");
                    }
                    sbuf.append(this.descriptionLayout.getText());
                    break;
                }
        }
        info.setText(sbuf);
    }
}
