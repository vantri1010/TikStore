package im.bclpbkiauv.ui.components;

import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class MediaActionDrawable extends Drawable {
    private static final float CANCEL_TO_CHECK_STAGE1 = 0.5f;
    private static final float CANCEL_TO_CHECK_STAGE2 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE1 = 0.5f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE2 = 0.2f;
    private static final float DOWNLOAD_TO_CANCEL_STAGE3 = 0.3f;
    private static final float EPS = 0.001f;
    public static final int ICON_CANCEL = 3;
    public static final int ICON_CANCEL_FILL = 14;
    public static final int ICON_CANCEL_NOPROFRESS = 12;
    public static final int ICON_CANCEL_PERCENT = 13;
    public static final int ICON_CHECK = 6;
    public static final int ICON_DOWNLOAD = 2;
    public static final int ICON_EMPTY = 10;
    public static final int ICON_EMPTY_NOPROGRESS = 11;
    public static final int ICON_FILE = 5;
    public static final int ICON_FILE_APK = 20;
    public static final int ICON_FILE_COMPRESS = 15;
    public static final int ICON_FILE_DOC = 16;
    public static final int ICON_FILE_IPA = 21;
    public static final int ICON_FILE_PDF = 19;
    public static final int ICON_FILE_TXT = 18;
    public static final int ICON_FILE_XLS = 17;
    public static final int ICON_FIRE = 7;
    public static final int ICON_GIF = 8;
    public static final int ICON_NONE = 4;
    public static final int ICON_PAUSE = 1;
    public static final int ICON_PLAY = 0;
    public static final int ICON_SECRETCHECK = 9;
    private static final float[] pausePath1 = {16.0f, 17.0f, 32.0f, 17.0f, 32.0f, 22.0f, 16.0f, 22.0f, 16.0f, 19.5f};
    private static final float[] pausePath2 = {16.0f, 31.0f, 32.0f, 31.0f, 32.0f, 26.0f, 16.0f, 26.0f, 16.0f, 28.5f};
    private static final int pauseRotation = 90;
    private static final float[] playFinalPath = {18.0f, 15.0f, 34.0f, 24.0f, 18.0f, 33.0f};
    private static final float[] playPath1 = {18.0f, 15.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final float[] playPath2 = {18.0f, 33.0f, 34.0f, 24.0f, 34.0f, 24.0f, 18.0f, 24.0f, 18.0f, 24.0f};
    private static final int playRotation = 0;
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private boolean isMini;
    private long lastAnimationTime;
    private int lastPercent = -1;
    private int nextIcon;
    private float overrideAlpha = 1.0f;
    private Paint paint = new Paint(1);
    private Paint paint2 = new Paint(1);
    private Paint paint3 = new Paint(1);
    private Path path1 = new Path();
    private Path path2 = new Path();
    private String percentString;
    private int percentStringWidth;
    private RectF rect = new RectF();
    private float savedTransitionProgress;
    private float scale = 1.0f;
    private TextPaint textPaint = new TextPaint(1);
    private float transitionAnimationTime = 400.0f;
    private float transitionProgress = 1.0f;

    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint3.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
        this.paint2.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(2.0f)));
    }

    public void setAlpha(int alpha) {
    }

    public void setOverrideAlpha(float alpha) {
        this.overrideAlpha = alpha;
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.paint.setColorFilter(colorFilter2);
        this.paint2.setColorFilter(colorFilter2);
        this.paint3.setColorFilter(colorFilter2);
        this.textPaint.setColorFilter(colorFilter2);
    }

    public void setColor(int value) {
        this.paint.setColor(value | -16777216);
        this.paint2.setColor(value | -16777216);
        this.paint3.setColor(value | -16777216);
        this.textPaint.setColor(-16777216 | value);
        this.colorFilter = new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setMini(boolean value) {
        this.isMini = value;
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(value ? 2.0f : 3.0f));
    }

    public int getOpacity() {
        return -2;
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int icon, boolean animated) {
        int i;
        if (this.currentIcon == icon && (i = this.nextIcon) != icon) {
            this.currentIcon = i;
            this.transitionProgress = 1.0f;
        }
        if (animated) {
            int i2 = this.currentIcon;
            if (i2 == icon || this.nextIcon == icon) {
                return false;
            }
            if (i2 == 2 && (icon == 3 || icon == 14)) {
                this.transitionAnimationTime = 400.0f;
            } else if (this.currentIcon != 4 && icon == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((this.currentIcon == 4 && icon == 14) || (this.currentIcon == 14 && icon == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = this.nextIcon;
            }
            this.animatingTransition = true;
            this.nextIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else if (this.currentIcon == icon) {
            return false;
        } else {
            this.animatingTransition = false;
            this.nextIcon = icon;
            this.currentIcon = icon;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (icon == 3 || icon == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float value, boolean animated) {
        if (!animated) {
            this.animatedDownloadProgress = value;
            this.downloadProgressAnimationStart = value;
        } else {
            if (this.animatedDownloadProgress > value) {
                this.animatedDownloadProgress = value;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = value;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    private float getCircleValue(float value) {
        while (value > 360.0f) {
            value -= 360.0f;
        }
        return value;
    }

    public float getProgressAlpha() {
        return 1.0f - this.transitionProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        float intrinsicWidth = ((float) (right - left)) / ((float) getIntrinsicWidth());
        this.scale = intrinsicWidth;
        if (intrinsicWidth < 0.7f) {
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        MediaActionDrawableDelegate mediaActionDrawableDelegate = this.delegate;
        if (mediaActionDrawableDelegate != null) {
            mediaActionDrawableDelegate.invalidate();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:343:0x08a9, code lost:
        if (r0.nextIcon != 1) goto L_0x08ad;
     */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0619  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0648  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x064b  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0656  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0659  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0666  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0669  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0677  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x067a  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0686  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0689  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0697  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x069a  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x06a8  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x06ab  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06b9  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x06bc  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x06ca  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x06cd  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x06db  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x06e0  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x06fc  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0702  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0789  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x078d  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x07a4  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x07a7  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x07c1  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x07fe  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0811  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0814  */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x083c  */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x08a6  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x08ac  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x08d5  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x08e3  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x08fd  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x0905  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x090f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x09d0  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x0a8b  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0bd3  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0be3  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0c03  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0c29  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0c46  */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0c74  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0c92  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0cec  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0d51  */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0d88  */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0dbc  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x0dec  */
    /* JADX WARNING: Removed duplicated region for block: B:526:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(android.graphics.Canvas r40) {
        /*
            r39 = this;
            r0 = r39
            r7 = r40
            android.graphics.Rect r8 = r39.getBounds()
            int r9 = r8.centerX()
            int r10 = r8.centerY()
            int r1 = r0.nextIcon
            r11 = 10
            r12 = 6
            r13 = 4
            r14 = 1065353216(0x3f800000, float:1.0)
            if (r1 != r13) goto L_0x0027
            float r1 = r0.transitionProgress
            float r1 = r14 - r1
            r40.save()
            float r2 = (float) r9
            float r3 = (float) r10
            r7.scale(r1, r1, r2, r3)
        L_0x0026:
            goto L_0x0039
        L_0x0027:
            if (r1 == r12) goto L_0x002b
            if (r1 != r11) goto L_0x0026
        L_0x002b:
            int r1 = r0.currentIcon
            if (r1 != r13) goto L_0x0026
            r40.save()
            float r1 = r0.transitionProgress
            float r2 = (float) r9
            float r3 = (float) r10
            r7.scale(r1, r1, r2, r3)
        L_0x0039:
            r1 = 1077936128(0x40400000, float:3.0)
            int r15 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            int r1 = r0.currentIcon
            r16 = 1094713344(0x41400000, float:12.0)
            r17 = 1080033280(0x40600000, float:3.5)
            r5 = 255(0xff, float:3.57E-43)
            r18 = 1073741824(0x40000000, float:2.0)
            r19 = 1088421888(0x40e00000, float:7.0)
            r4 = 3
            r20 = 1132396544(0x437f0000, float:255.0)
            r2 = 14
            r21 = 1056964608(0x3f000000, float:0.5)
            r12 = 2
            if (r1 == r12) goto L_0x0059
            int r1 = r0.nextIcon
            if (r1 != r12) goto L_0x0306
        L_0x0059:
            float r1 = (float) r10
            r22 = 1091567616(0x41100000, float:9.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r22)
            float r3 = (float) r3
            float r6 = r0.scale
            float r3 = r3 * r6
            float r24 = r1 - r3
            float r1 = (float) r10
            r3 = 1091567616(0x41100000, float:9.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r6 = r0.scale
            float r3 = r3 * r6
            float r25 = r1 + r3
            float r1 = (float) r10
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            float r3 = (float) r3
            float r6 = r0.scale
            float r3 = r3 * r6
            float r26 = r1 + r3
            int r1 = r0.currentIcon
            if (r1 == r4) goto L_0x0087
            if (r1 != r2) goto L_0x00ac
        L_0x0087:
            int r1 = r0.nextIcon
            if (r1 != r12) goto L_0x00ac
            android.graphics.Paint r1 = r0.paint
            float r3 = r0.transitionProgress
            float r3 = r3 / r21
            float r3 = java.lang.Math.min(r14, r3)
            float r3 = r3 * r20
            int r3 = (int) r3
            r1.setAlpha(r3)
            float r1 = r0.transitionProgress
            float r3 = (float) r10
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r11 = r0.scale
            float r6 = r6 * r11
            float r3 = r3 + r6
            r11 = r1
            r27 = r3
            goto L_0x00e2
        L_0x00ac:
            int r1 = r0.nextIcon
            if (r1 == r4) goto L_0x00cd
            if (r1 == r2) goto L_0x00cd
            if (r1 == r12) goto L_0x00cd
            android.graphics.Paint r1 = r0.paint
            float r3 = r0.savedTransitionProgress
            float r3 = r3 / r21
            float r3 = java.lang.Math.min(r14, r3)
            float r3 = r3 * r20
            float r6 = r0.transitionProgress
            float r6 = r14 - r6
            float r3 = r3 * r6
            int r3 = (int) r3
            r1.setAlpha(r3)
            float r1 = r0.savedTransitionProgress
            goto L_0x00d4
        L_0x00cd:
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r5)
            float r1 = r0.transitionProgress
        L_0x00d4:
            float r3 = (float) r10
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            float r6 = (float) r6
            float r11 = r0.scale
            float r6 = r6 * r11
            float r3 = r3 + r6
            r11 = r1
            r27 = r3
        L_0x00e2:
            boolean r1 = r0.animatingTransition
            r3 = 1090519040(0x41000000, float:8.0)
            if (r1 == 0) goto L_0x02b3
            r1 = r11
            int r6 = r0.nextIcon
            if (r6 == r12) goto L_0x026b
            int r6 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r6 > 0) goto L_0x00f3
            goto L_0x026b
        L_0x00f3:
            r3 = 1095761920(0x41500000, float:13.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r6 = r0.scale
            float r28 = r3 * r6
            float r1 = r1 - r21
            float r29 = r1 / r21
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x011b
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            float r1 = r1 - r3
            r3 = 1065353216(0x3f800000, float:1.0)
            r6 = 1050253722(0x3e99999a, float:0.3)
            float r6 = r1 / r6
            r30 = r1
            r31 = r3
            r32 = r6
            goto L_0x0127
        L_0x011b:
            r3 = 1045220557(0x3e4ccccd, float:0.2)
            float r3 = r1 / r3
            r6 = 0
            r30 = r1
            r31 = r3
            r32 = r6
        L_0x0127:
            android.graphics.RectF r1 = r0.rect
            float r3 = (float) r9
            float r3 = r3 - r28
            float r6 = r28 / r18
            float r6 = r26 - r6
            float r2 = (float) r9
            float r33 = r28 / r18
            float r4 = r26 + r33
            r1.set(r3, r6, r2, r4)
            r1 = 1120403456(0x42c80000, float:100.0)
            float r33 = r32 * r1
            android.graphics.RectF r2 = r0.rect
            r1 = 1120927744(0x42d00000, float:104.0)
            float r1 = r1 * r29
            float r4 = r1 - r33
            r6 = 0
            android.graphics.Paint r3 = r0.paint
            r1 = r40
            r13 = 14
            r23 = r3
            r12 = 0
            r3 = r33
            r5 = r6
            r6 = r23
            r1.drawArc(r2, r3, r4, r5, r6)
            float r1 = r26 - r27
            float r1 = r1 * r31
            float r22 = r27 + r1
            r23 = r26
            r34 = r26
            float r1 = (float) r9
            r36 = r1
            r37 = r1
            int r1 = (r32 > r12 ? 1 : (r32 == r12 ? 0 : -1))
            if (r1 <= 0) goto L_0x02b2
            int r1 = r0.nextIcon
            if (r1 != r13) goto L_0x0170
            r1 = 0
            r6 = r1
            goto L_0x0177
        L_0x0170:
            r1 = -1036779520(0xffffffffc2340000, float:-45.0)
            float r2 = r14 - r32
            float r1 = r1 * r2
            r6 = r1
        L_0x0177:
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r1 = r1 * r32
            float r2 = r0.scale
            float r28 = r1 * r2
            float r1 = r32 * r20
            int r1 = (int) r1
            int r2 = r0.nextIcon
            r5 = 3
            if (r2 == r5) goto L_0x019f
            if (r2 == r13) goto L_0x019f
            r3 = 2
            if (r2 == r3) goto L_0x019f
            float r2 = r0.transitionProgress
            float r2 = r2 / r21
            float r2 = java.lang.Math.min(r14, r2)
            float r2 = r14 - r2
            float r3 = (float) r1
            float r3 = r3 * r2
            int r1 = (int) r3
            r4 = r1
            goto L_0x01a0
        L_0x019f:
            r4 = r1
        L_0x01a0:
            int r1 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x01ac
            r40.save()
            float r1 = (float) r9
            float r2 = (float) r10
            r7.rotate(r6, r1, r2)
        L_0x01ac:
            if (r4 == 0) goto L_0x0260
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r4)
            int r1 = r0.nextIcon
            if (r1 != r13) goto L_0x0237
            android.graphics.Paint r1 = r0.paint3
            r1.setAlpha(r4)
            android.graphics.RectF r1 = r0.rect
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r2 = r9 - r2
            float r2 = (float) r2
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r3 = r10 - r3
            float r3 = (float) r3
            int r35 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r5 = r9 + r35
            float r5 = (float) r5
            int r35 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r13 = r10 + r35
            float r13 = (float) r13
            r1.set(r2, r3, r5, r13)
            android.graphics.RectF r1 = r0.rect
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            android.graphics.Paint r5 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r5)
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r4
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x0201
            r1 = 1073741824(0x40000000, float:2.0)
            goto L_0x0203
        L_0x0201:
            r1 = 1082130432(0x40800000, float:4.0)
        L_0x0203:
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r13
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r13
            float r3 = (float) r3
            int r5 = r8.right
            int r5 = r5 - r13
            float r5 = (float) r5
            int r14 = r8.bottom
            int r14 = r14 - r13
            float r14 = (float) r14
            r1.set(r2, r3, r5, r14)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r5 = 1135869952(0x43b40000, float:360.0)
            r14 = 0
            android.graphics.Paint r1 = r0.paint
            r38 = r1
            r1 = r40
            r12 = r4
            r4 = r5
            r5 = r14
            r14 = r6
            r6 = r38
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r12)
            goto L_0x0262
        L_0x0237:
            r12 = r4
            r14 = r6
            float r1 = (float) r9
            float r2 = r1 - r28
            float r1 = (float) r10
            float r3 = r1 - r28
            float r1 = (float) r9
            float r4 = r1 + r28
            float r1 = (float) r10
            float r5 = r1 + r28
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r9
            float r2 = r1 + r28
            float r1 = (float) r10
            float r3 = r1 - r28
            float r1 = (float) r9
            float r4 = r1 - r28
            float r1 = (float) r10
            float r5 = r1 + r28
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x0262
        L_0x0260:
            r12 = r4
            r14 = r6
        L_0x0262:
            r1 = 0
            int r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x02b2
            r40.restore()
            goto L_0x02b2
        L_0x026b:
            int r2 = r0.nextIcon
            r4 = 2
            if (r2 != r4) goto L_0x0276
            r2 = r11
            r4 = 1065353216(0x3f800000, float:1.0)
            float r14 = r4 - r2
            goto L_0x027c
        L_0x0276:
            r4 = 1065353216(0x3f800000, float:1.0)
            float r14 = r11 / r21
            float r2 = r4 - r14
        L_0x027c:
            float r4 = r27 - r24
            float r4 = r4 * r14
            float r22 = r24 + r4
            float r4 = r26 - r25
            float r4 = r4 * r14
            float r34 = r25 + r4
            float r4 = (float) r9
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r5 = r5 * r2
            float r6 = r0.scale
            float r5 = r5 * r6
            float r37 = r4 - r5
            float r4 = (float) r9
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            float r5 = r5 * r2
            float r6 = r0.scale
            float r5 = r5 * r6
            float r36 = r4 + r5
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r3 = r3 * r2
            float r4 = r0.scale
            float r3 = r3 * r4
            float r23 = r34 - r3
        L_0x02b2:
            goto L_0x02da
        L_0x02b3:
            r22 = r24
            r34 = r25
            float r1 = (float) r9
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            float r4 = r0.scale
            float r2 = r2 * r4
            float r37 = r1 - r2
            float r1 = (float) r9
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            float r4 = r0.scale
            float r2 = r2 * r4
            float r36 = r1 + r2
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            float r1 = (float) r1
            float r2 = r0.scale
            float r1 = r1 * r2
            float r23 = r34 - r1
        L_0x02da:
            int r1 = (r22 > r34 ? 1 : (r22 == r34 ? 0 : -1))
            if (r1 == 0) goto L_0x02eb
            float r2 = (float) r9
            float r4 = (float) r9
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r3 = r22
            r5 = r34
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x02eb:
            float r1 = (float) r9
            int r1 = (r37 > r1 ? 1 : (r37 == r1 ? 0 : -1))
            if (r1 == 0) goto L_0x0306
            float r4 = (float) r9
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r2 = r37
            r3 = r23
            r5 = r34
            r1.drawLine(r2, r3, r4, r5, r6)
            float r4 = (float) r9
            android.graphics.Paint r6 = r0.paint
            r2 = r36
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0306:
            int r1 = r0.currentIcon
            r11 = 15
            r12 = 5
            r13 = 13
            r14 = 1
            r6 = 3
            if (r1 == r6) goto L_0x03a3
            r2 = 14
            if (r1 == r2) goto L_0x03a3
            r3 = 4
            if (r1 != r3) goto L_0x0323
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x031e
            if (r1 != r6) goto L_0x0323
        L_0x031e:
            r4 = 1082130432(0x40800000, float:4.0)
            r13 = 3
            goto L_0x03a6
        L_0x0323:
            int r1 = r0.currentIcon
            r2 = 10
            if (r1 == r2) goto L_0x0332
            int r3 = r0.nextIcon
            if (r3 == r2) goto L_0x0332
            if (r1 != r13) goto L_0x0330
            goto L_0x0332
        L_0x0330:
            goto L_0x0611
        L_0x0332:
            int r1 = r0.nextIcon
            r2 = 4
            if (r1 == r2) goto L_0x033f
            r2 = 6
            if (r1 != r2) goto L_0x033b
            goto L_0x033f
        L_0x033b:
            r1 = 255(0xff, float:3.57E-43)
            r5 = r1
            goto L_0x0349
        L_0x033f:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3f800000, float:1.0)
            float r3 = r2 - r1
            float r2 = r3 * r20
            int r1 = (int) r2
            r5 = r1
        L_0x0349:
            if (r5 == 0) goto L_0x039e
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r5
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            r4 = 1082130432(0x40800000, float:4.0)
            float r17 = java.lang.Math.max(r4, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x0367
            goto L_0x0369
        L_0x0367:
            r18 = 1082130432(0x40800000, float:4.0)
        L_0x0369:
            int r18 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r18
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r18
            float r3 = (float) r3
            int r4 = r8.right
            int r4 = r4 - r18
            float r4 = (float) r4
            int r6 = r8.bottom
            int r6 = r6 - r18
            float r6 = (float) r6
            r1.set(r2, r3, r4, r6)
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r6 = 0
            android.graphics.Paint r4 = r0.paint
            r1 = r40
            r22 = r4
            r4 = r17
            r23 = r5
            r5 = r6
            r13 = 3
            r6 = r22
            r1.drawArc(r2, r3, r4, r5, r6)
            goto L_0x0611
        L_0x039e:
            r23 = r5
            r13 = 3
            goto L_0x0611
        L_0x03a3:
            r4 = 1082130432(0x40800000, float:4.0)
            r13 = 3
        L_0x03a6:
            r1 = 1065353216(0x3f800000, float:1.0)
            r2 = 0
            r3 = 0
            int r5 = r0.nextIcon
            r6 = 2
            if (r5 != r6) goto L_0x03d7
            float r5 = r0.transitionProgress
            int r6 = (r5 > r21 ? 1 : (r5 == r21 ? 0 : -1))
            if (r6 > 0) goto L_0x03ca
            float r5 = r5 / r21
            r6 = 1065353216(0x3f800000, float:1.0)
            float r22 = r6 - r5
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r6 = (float) r6
            float r6 = r6 * r22
            float r4 = r0.scale
            float r6 = r6 * r4
            float r4 = r22 * r20
            int r4 = (int) r4
            goto L_0x03cc
        L_0x03ca:
            r6 = 0
            r4 = 0
        L_0x03cc:
            r5 = 0
            r11 = r1
            r13 = r2
            r14 = r3
            r26 = r6
            r12 = 1065353216(0x3f800000, float:1.0)
            r6 = r4
            goto L_0x04cb
        L_0x03d7:
            if (r5 == 0) goto L_0x0493
            if (r5 == r14) goto L_0x0493
            if (r5 == r12) goto L_0x0493
            r4 = 8
            if (r5 == r4) goto L_0x0493
            r4 = 9
            if (r5 == r4) goto L_0x0493
            r4 = 7
            if (r5 == r4) goto L_0x0493
            r4 = 6
            if (r5 == r4) goto L_0x0493
            if (r5 == r11) goto L_0x0493
            r4 = 16
            if (r5 == r4) goto L_0x0493
            r4 = 17
            if (r5 == r4) goto L_0x0493
            r4 = 19
            if (r5 == r4) goto L_0x0493
            r4 = 18
            if (r5 == r4) goto L_0x0493
            r4 = 20
            if (r5 == r4) goto L_0x0493
            r4 = 21
            if (r5 != r4) goto L_0x0407
            goto L_0x0493
        L_0x0407:
            r4 = 4
            if (r5 != r4) goto L_0x0436
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3f800000, float:1.0)
            float r6 = r5 - r4
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r5 = (float) r5
            float r14 = r0.scale
            float r5 = r5 * r14
            float r14 = r6 * r20
            int r14 = (int) r14
            int r11 = r0.currentIcon
            r12 = 14
            if (r11 != r12) goto L_0x0425
            r11 = 0
            r1 = r6
            goto L_0x042b
        L_0x0425:
            r11 = 1110704128(0x42340000, float:45.0)
            float r11 = r11 * r4
            r1 = 1065353216(0x3f800000, float:1.0)
        L_0x042b:
            r13 = r2
            r26 = r5
            r5 = r11
            r6 = r14
            r12 = 1065353216(0x3f800000, float:1.0)
            r11 = r1
            r14 = r3
            goto L_0x04cb
        L_0x0436:
            r4 = 14
            if (r5 == r4) goto L_0x0453
            if (r5 != r13) goto L_0x043d
            goto L_0x0453
        L_0x043d:
            r5 = 0
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r4 = (float) r4
            float r6 = r0.scale
            float r6 = r6 * r4
            r4 = 255(0xff, float:3.57E-43)
            r11 = r1
            r13 = r2
            r14 = r3
            r26 = r6
            r12 = 1065353216(0x3f800000, float:1.0)
            r6 = r4
            goto L_0x04cb
        L_0x0453:
            float r4 = r0.transitionProgress
            r5 = 1065353216(0x3f800000, float:1.0)
            float r14 = r5 - r4
            int r5 = r0.currentIcon
            r6 = 4
            if (r5 != r6) goto L_0x0461
            r5 = 0
            r1 = r4
            goto L_0x0467
        L_0x0461:
            r5 = 1110704128(0x42340000, float:45.0)
            float r5 = r5 * r14
            r1 = 1065353216(0x3f800000, float:1.0)
        L_0x0467:
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r6 = (float) r6
            float r11 = r0.scale
            float r6 = r6 * r11
            float r11 = r4 * r20
            int r11 = (int) r11
            int r12 = r0.nextIcon
            r13 = 14
            if (r12 != r13) goto L_0x0480
            int r12 = r8.left
            float r2 = (float) r12
            int r12 = r8.top
            float r3 = (float) r12
            goto L_0x048a
        L_0x0480:
            int r12 = r8.centerX()
            float r2 = (float) r12
            int r12 = r8.centerY()
            float r3 = (float) r12
        L_0x048a:
            r13 = r2
            r14 = r3
            r26 = r6
            r6 = r11
            r12 = 1065353216(0x3f800000, float:1.0)
            r11 = r1
            goto L_0x04cb
        L_0x0493:
            int r4 = r0.nextIcon
            r5 = 6
            if (r4 != r5) goto L_0x04a5
            float r4 = r0.transitionProgress
            float r4 = r4 / r21
            r5 = 1065353216(0x3f800000, float:1.0)
            float r4 = java.lang.Math.min(r5, r4)
            float r14 = r5 - r4
            goto L_0x04ab
        L_0x04a5:
            r5 = 1065353216(0x3f800000, float:1.0)
            float r4 = r0.transitionProgress
            float r14 = r5 - r4
        L_0x04ab:
            r5 = 1110704128(0x42340000, float:45.0)
            float r5 = r5 * r4
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r6 = (float) r6
            float r6 = r6 * r14
            float r11 = r0.scale
            float r6 = r6 * r11
            float r11 = r14 * r18
            r12 = 1065353216(0x3f800000, float:1.0)
            float r11 = java.lang.Math.min(r12, r11)
            float r11 = r11 * r20
            int r4 = (int) r11
            r11 = r1
            r13 = r2
            r14 = r3
            r26 = r6
            r6 = r4
        L_0x04cb:
            int r1 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r1 == 0) goto L_0x04d5
            r40.save()
            r7.scale(r11, r11, r13, r14)
        L_0x04d5:
            r1 = 0
            int r2 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x04e2
            r40.save()
            float r1 = (float) r9
            float r2 = (float) r10
            r7.rotate(r5, r1, r2)
        L_0x04e2:
            if (r6 == 0) goto L_0x0577
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r6
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x0534
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0501
            r28 = r5
            r12 = r6
            r27 = r13
            r13 = 1082130432(0x40800000, float:4.0)
            goto L_0x053b
        L_0x0501:
            float r1 = (float) r9
            float r2 = r1 - r26
            float r1 = (float) r10
            float r3 = r1 - r26
            float r1 = (float) r9
            float r4 = r1 + r26
            float r1 = (float) r10
            float r12 = r1 + r26
            android.graphics.Paint r1 = r0.paint
            r17 = r1
            r1 = r40
            r27 = r13
            r13 = 1082130432(0x40800000, float:4.0)
            r28 = r5
            r5 = r12
            r12 = r6
            r6 = r17
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r9
            float r2 = r1 + r26
            float r1 = (float) r10
            float r3 = r1 - r26
            float r1 = (float) r9
            float r4 = r1 - r26
            float r1 = (float) r10
            float r5 = r1 + r26
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            goto L_0x057e
        L_0x0534:
            r28 = r5
            r12 = r6
            r27 = r13
            r13 = 1082130432(0x40800000, float:4.0)
        L_0x053b:
            android.graphics.Paint r1 = r0.paint3
            float r2 = (float) r12
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.RectF r1 = r0.rect
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r2 = r9 - r2
            float r2 = (float) r2
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r3 = r10 - r3
            float r3 = (float) r3
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r4 = r4 + r9
            float r4 = (float) r4
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r5 = r5 + r10
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            android.graphics.RectF r1 = r0.rect
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            float r2 = (float) r2
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r18)
            float r3 = (float) r3
            android.graphics.Paint r4 = r0.paint3
            r7.drawRoundRect(r1, r2, r3, r4)
            goto L_0x057e
        L_0x0577:
            r28 = r5
            r12 = r6
            r27 = r13
            r13 = 1082130432(0x40800000, float:4.0)
        L_0x057e:
            r1 = 0
            int r2 = (r28 > r1 ? 1 : (r28 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0586
            r40.restore()
        L_0x0586:
            int r1 = r0.currentIcon
            r2 = 3
            if (r1 == r2) goto L_0x0598
            r3 = 14
            if (r1 == r3) goto L_0x0598
            r4 = 4
            if (r1 != r4) goto L_0x0606
            int r1 = r0.nextIcon
            if (r1 == r3) goto L_0x0598
            if (r1 != r2) goto L_0x0606
        L_0x0598:
            if (r12 == 0) goto L_0x0606
            r1 = 1135869952(0x43b40000, float:360.0)
            float r2 = r0.animatedDownloadProgress
            float r2 = r2 * r1
            float r17 = java.lang.Math.max(r13, r2)
            boolean r1 = r0.isMini
            if (r1 == 0) goto L_0x05ab
            r6 = 1073741824(0x40000000, float:2.0)
            goto L_0x05ad
        L_0x05ab:
            r6 = 1082130432(0x40800000, float:4.0)
        L_0x05ad:
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            android.graphics.RectF r1 = r0.rect
            int r2 = r8.left
            int r2 = r2 + r13
            float r2 = (float) r2
            int r3 = r8.top
            int r3 = r3 + r13
            float r3 = (float) r3
            int r4 = r8.right
            int r4 = r4 - r13
            float r4 = (float) r4
            int r5 = r8.bottom
            int r5 = r5 - r13
            float r5 = (float) r5
            r1.set(r2, r3, r4, r5)
            int r1 = r0.currentIcon
            r2 = 14
            if (r1 == r2) goto L_0x05d6
            r3 = 4
            if (r1 != r3) goto L_0x05f8
            int r1 = r0.nextIcon
            if (r1 == r2) goto L_0x05d6
            r2 = 3
            if (r1 != r2) goto L_0x05f8
        L_0x05d6:
            android.graphics.Paint r1 = r0.paint
            float r2 = (float) r12
            r3 = 1041865114(0x3e19999a, float:0.15)
            float r2 = r2 * r3
            float r3 = r0.overrideAlpha
            float r2 = r2 * r3
            int r2 = (int) r2
            r1.setAlpha(r2)
            android.graphics.RectF r2 = r0.rect
            r3 = 0
            r4 = 1135869952(0x43b40000, float:360.0)
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawArc(r2, r3, r4, r5, r6)
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r12)
        L_0x05f8:
            android.graphics.RectF r2 = r0.rect
            float r3 = r0.downloadRadOffset
            r5 = 0
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r4 = r17
            r1.drawArc(r2, r3, r4, r5, r6)
        L_0x0606:
            r1 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r2 == 0) goto L_0x0330
            r40.restore()
            goto L_0x0330
        L_0x0611:
            r1 = 0
            r2 = 0
            int r3 = r0.currentIcon
            int r4 = r0.nextIcon
            if (r3 != r4) goto L_0x0620
            r4 = 1065353216(0x3f800000, float:1.0)
            r3 = r4
            r5 = r4
            r11 = r3
            r12 = r5
            goto L_0x0643
        L_0x0620:
            r4 = 1065353216(0x3f800000, float:1.0)
            r5 = 4
            if (r3 != r5) goto L_0x062e
            float r5 = r0.transitionProgress
            float r3 = r0.transitionProgress
            float r3 = r4 - r3
            r11 = r3
            r12 = r5
            goto L_0x0643
        L_0x062e:
            float r3 = r0.transitionProgress
            float r3 = r3 / r21
            float r5 = java.lang.Math.min(r4, r3)
            float r3 = r0.transitionProgress
            float r3 = r3 / r21
            float r14 = r4 - r3
            r3 = 0
            float r4 = java.lang.Math.max(r3, r14)
            r11 = r4
            r12 = r5
        L_0x0643:
            int r3 = r0.nextIcon
            r4 = 5
            if (r3 != r4) goto L_0x064b
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileNoneIcon
            goto L_0x0651
        L_0x064b:
            int r3 = r0.currentIcon
            if (r3 != r4) goto L_0x0651
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileNoneIcon
        L_0x0651:
            int r3 = r0.nextIcon
            r4 = 7
            if (r3 != r4) goto L_0x0659
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_flameIcon
            goto L_0x0660
        L_0x0659:
            int r3 = r0.currentIcon
            r4 = 7
            if (r3 != r4) goto L_0x0660
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_flameIcon
        L_0x0660:
            int r3 = r0.nextIcon
            r4 = 8
            if (r3 != r4) goto L_0x0669
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_gifIcon
            goto L_0x0671
        L_0x0669:
            int r3 = r0.currentIcon
            r4 = 8
            if (r3 != r4) goto L_0x0671
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_gifIcon
        L_0x0671:
            int r3 = r0.nextIcon
            r4 = 15
            if (r3 != r4) goto L_0x067a
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileCompressIcon
            goto L_0x0680
        L_0x067a:
            int r3 = r0.currentIcon
            if (r3 != r4) goto L_0x0680
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileCompressIcon
        L_0x0680:
            int r3 = r0.nextIcon
            r4 = 16
            if (r3 != r4) goto L_0x0689
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileDocIcon
            goto L_0x0691
        L_0x0689:
            int r3 = r0.currentIcon
            r4 = 16
            if (r3 != r4) goto L_0x0691
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileDocIcon
        L_0x0691:
            int r3 = r0.nextIcon
            r4 = 17
            if (r3 != r4) goto L_0x069a
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileXlsIcon
            goto L_0x06a2
        L_0x069a:
            int r3 = r0.currentIcon
            r4 = 17
            if (r3 != r4) goto L_0x06a2
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileXlsIcon
        L_0x06a2:
            int r3 = r0.nextIcon
            r4 = 19
            if (r3 != r4) goto L_0x06ab
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_filePdfIcon
            goto L_0x06b3
        L_0x06ab:
            int r3 = r0.currentIcon
            r4 = 19
            if (r3 != r4) goto L_0x06b3
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_filePdfIcon
        L_0x06b3:
            int r3 = r0.nextIcon
            r4 = 18
            if (r3 != r4) goto L_0x06bc
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileTxtIcon
            goto L_0x06c4
        L_0x06bc:
            int r3 = r0.currentIcon
            r4 = 18
            if (r3 != r4) goto L_0x06c4
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileTxtIcon
        L_0x06c4:
            int r3 = r0.nextIcon
            r4 = 20
            if (r3 != r4) goto L_0x06cd
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileApkIcon
            goto L_0x06d5
        L_0x06cd:
            int r3 = r0.currentIcon
            r4 = 20
            if (r3 != r4) goto L_0x06d5
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileApkIcon
        L_0x06d5:
            int r3 = r0.nextIcon
            r4 = 21
            if (r3 != r4) goto L_0x06e0
            android.graphics.drawable.Drawable r1 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileIpaIcon
            r13 = r1
            r14 = r2
            goto L_0x06ed
        L_0x06e0:
            int r3 = r0.currentIcon
            r4 = 21
            if (r3 != r4) goto L_0x06eb
            android.graphics.drawable.Drawable r2 = im.bclpbkiauv.ui.actionbar.Theme.chat_fileIpaIcon
            r13 = r1
            r14 = r2
            goto L_0x06ed
        L_0x06eb:
            r13 = r1
            r14 = r2
        L_0x06ed:
            int r1 = r0.currentIcon
            r2 = 9
            r17 = 1086324736(0x40c00000, float:6.0)
            if (r1 == r2) goto L_0x0702
            int r1 = r0.nextIcon
            r2 = 9
            if (r1 != r2) goto L_0x06fc
            goto L_0x0702
        L_0x06fc:
            r26 = r11
            r27 = r15
            goto L_0x0777
        L_0x0702:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x070d
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0712
        L_0x070d:
            float r2 = r0.transitionProgress
            float r2 = r2 * r20
            int r5 = (int) r2
        L_0x0712:
            r1.setAlpha(r5)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r6 = r10 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            int r5 = r9 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0733
            r40.save()
            float r1 = r0.transitionProgress
            float r2 = (float) r9
            float r3 = (float) r10
            r7.scale(r1, r1, r2, r3)
        L_0x0733:
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r1 = r5 - r1
            float r2 = (float) r1
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r1 = r6 - r1
            float r3 = (float) r1
            float r4 = (float) r5
            float r1 = (float) r6
            r18 = r6
            android.graphics.Paint r6 = r0.paint
            r26 = r1
            r1 = r40
            r27 = r15
            r15 = r5
            r5 = r26
            r26 = r11
            r11 = r18
            r1.drawLine(r2, r3, r4, r5, r6)
            float r2 = (float) r15
            float r3 = (float) r11
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r5 = r15 + r1
            float r4 = (float) r5
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            int r6 = r11 - r1
            float r5 = (float) r6
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0777
            r40.restore()
        L_0x0777:
            int r1 = r0.currentIcon
            r2 = 12
            if (r1 == r2) goto L_0x0783
            int r1 = r0.nextIcon
            r2 = 12
            if (r1 != r2) goto L_0x0801
        L_0x0783:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x078d
            r1 = 1065353216(0x3f800000, float:1.0)
            r11 = r1
            goto L_0x079c
        L_0x078d:
            r1 = 13
            if (r2 != r1) goto L_0x0795
            float r1 = r0.transitionProgress
            r11 = r1
            goto L_0x079c
        L_0x0795:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3f800000, float:1.0)
            float r1 = r2 - r1
            r11 = r1
        L_0x079c:
            android.graphics.Paint r1 = r0.paint
            int r2 = r0.currentIcon
            int r3 = r0.nextIcon
            if (r2 != r3) goto L_0x07a7
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x07aa
        L_0x07a7:
            float r2 = r11 * r20
            int r5 = (int) r2
        L_0x07aa:
            r1.setAlpha(r5)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r15 = r10 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            int r18 = r9 - r1
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x07c9
            r40.save()
            float r1 = (float) r9
            float r2 = (float) r10
            r7.scale(r11, r11, r1, r2)
        L_0x07c9:
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            float r1 = (float) r1
            float r2 = r0.scale
            float r28 = r1 * r2
            float r1 = (float) r9
            float r2 = r1 - r28
            float r1 = (float) r10
            float r3 = r1 - r28
            float r1 = (float) r9
            float r4 = r1 + r28
            float r1 = (float) r10
            float r5 = r1 + r28
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            float r1 = (float) r9
            float r2 = r1 + r28
            float r1 = (float) r10
            float r3 = r1 - r28
            float r1 = (float) r9
            float r4 = r1 - r28
            float r1 = (float) r10
            float r5 = r1 + r28
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 == r2) goto L_0x0801
            r40.restore()
        L_0x0801:
            int r1 = r0.currentIcon
            r2 = 13
            if (r1 == r2) goto L_0x080b
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0887
        L_0x080b:
            int r1 = r0.currentIcon
            int r2 = r0.nextIcon
            if (r1 != r2) goto L_0x0814
            r1 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0821
        L_0x0814:
            r1 = 13
            if (r2 != r1) goto L_0x081b
            float r1 = r0.transitionProgress
            goto L_0x0821
        L_0x081b:
            float r1 = r0.transitionProgress
            r2 = 1065353216(0x3f800000, float:1.0)
            float r1 = r2 - r1
        L_0x0821:
            android.text.TextPaint r2 = r0.textPaint
            float r3 = r1 * r20
            int r3 = (int) r3
            r2.setAlpha(r3)
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r10
            int r3 = r0.percentStringWidth
            r4 = 2
            int r3 = r3 / r4
            int r3 = r9 - r3
            int r4 = r0.currentIcon
            int r5 = r0.nextIcon
            if (r4 == r5) goto L_0x0844
            r40.save()
            float r4 = (float) r9
            float r5 = (float) r10
            r7.scale(r1, r1, r4, r5)
        L_0x0844:
            float r4 = r0.animatedDownloadProgress
            r5 = 1120403456(0x42c80000, float:100.0)
            float r4 = r4 * r5
            int r4 = (int) r4
            java.lang.String r5 = r0.percentString
            if (r5 == 0) goto L_0x0853
            int r5 = r0.lastPercent
            if (r4 == r5) goto L_0x0875
        L_0x0853:
            r0.lastPercent = r4
            r5 = 1
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 0
            java.lang.Integer r11 = java.lang.Integer.valueOf(r4)
            r6[r5] = r11
            java.lang.String r5 = "%d%%"
            java.lang.String r5 = java.lang.String.format(r5, r6)
            r0.percentString = r5
            android.text.TextPaint r6 = r0.textPaint
            float r5 = r6.measureText(r5)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            r0.percentStringWidth = r5
        L_0x0875:
            java.lang.String r5 = r0.percentString
            float r6 = (float) r3
            float r11 = (float) r2
            android.text.TextPaint r15 = r0.textPaint
            r7.drawText(r5, r6, r11, r15)
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 == r6) goto L_0x0887
            r40.restore()
        L_0x0887:
            int r1 = r0.currentIcon
            if (r1 == 0) goto L_0x08a2
            r2 = 1
            if (r1 == r2) goto L_0x08a2
            int r1 = r0.nextIcon
            if (r1 == 0) goto L_0x08a2
            if (r1 != r2) goto L_0x0895
            goto L_0x08a2
        L_0x0895:
            r31 = r9
            r30 = r10
            r18 = r13
            r28 = r14
            r10 = 255(0xff, float:3.57E-43)
            r13 = r12
            goto L_0x0bf6
        L_0x08a2:
            int r1 = r0.currentIcon
            if (r1 != 0) goto L_0x08ac
            int r1 = r0.nextIcon
            r2 = 1
            if (r1 == r2) goto L_0x08b5
            goto L_0x08ad
        L_0x08ac:
            r2 = 1
        L_0x08ad:
            int r1 = r0.currentIcon
            if (r1 != r2) goto L_0x08c5
            int r1 = r0.nextIcon
            if (r1 != 0) goto L_0x08c5
        L_0x08b5:
            boolean r1 = r0.animatingTransition
            if (r1 == 0) goto L_0x08c2
            android.view.animation.DecelerateInterpolator r1 = r0.interpolator
            float r2 = r0.transitionProgress
            float r3 = r1.getInterpolation(r2)
            goto L_0x08c3
        L_0x08c2:
            r3 = 0
        L_0x08c3:
            r1 = r3
            goto L_0x08c6
        L_0x08c5:
            r1 = 0
        L_0x08c6:
            android.graphics.Path r2 = r0.path1
            r2.reset()
            android.graphics.Path r2 = r0.path2
            r2.reset()
            r2 = 0
            int r3 = r0.currentIcon
            if (r3 == 0) goto L_0x08e3
            r4 = 1
            if (r3 == r4) goto L_0x08dc
            r3 = 0
            r4 = 0
            r5 = r4
            goto L_0x08eb
        L_0x08dc:
            float[] r4 = pausePath1
            float[] r5 = pausePath2
            r3 = 90
            goto L_0x08eb
        L_0x08e3:
            float[] r4 = playPath1
            float[] r5 = playPath2
            float[] r2 = playFinalPath
            r3 = 0
        L_0x08eb:
            int r6 = r0.nextIcon
            if (r6 == 0) goto L_0x08fd
            r11 = 1
            if (r6 == r11) goto L_0x08f6
            r6 = 0
            r11 = 0
            r15 = r11
            goto L_0x0903
        L_0x08f6:
            float[] r11 = pausePath1
            float[] r15 = pausePath2
            r6 = 90
            goto L_0x0903
        L_0x08fd:
            float[] r11 = playPath1
            float[] r15 = playPath2
            r6 = 0
        L_0x0903:
            if (r4 != 0) goto L_0x0909
            r4 = r11
            r5 = r15
            r11 = 0
            r15 = 0
        L_0x0909:
            r18 = r13
            boolean r13 = r0.animatingTransition
            if (r13 != 0) goto L_0x09c6
            if (r2 == 0) goto L_0x09c6
            r13 = 0
        L_0x0912:
            r28 = r14
            int r14 = r2.length
            r25 = 2
            int r14 = r14 / 2
            if (r13 >= r14) goto L_0x09bc
            if (r13 != 0) goto L_0x0968
            android.graphics.Path r14 = r0.path1
            int r25 = r13 * 2
            r25 = r2[r25]
            r29 = r12
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r25)
            float r12 = (float) r12
            r30 = r10
            float r10 = r0.scale
            float r12 = r12 * r10
            int r10 = r13 * 2
            r22 = 1
            int r10 = r10 + 1
            r10 = r2[r10]
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            r31 = r9
            float r9 = r0.scale
            float r10 = r10 * r9
            r14.moveTo(r12, r10)
            android.graphics.Path r9 = r0.path2
            int r10 = r13 * 2
            r10 = r2[r10]
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            int r12 = r13 * 2
            r14 = 1
            int r12 = r12 + r14
            r12 = r2[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            r9.moveTo(r10, r12)
            goto L_0x09b0
        L_0x0968:
            r31 = r9
            r30 = r10
            r29 = r12
            android.graphics.Path r9 = r0.path1
            int r10 = r13 * 2
            r10 = r2[r10]
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            int r12 = r13 * 2
            r14 = 1
            int r12 = r12 + r14
            r12 = r2[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            r9.lineTo(r10, r12)
            android.graphics.Path r9 = r0.path2
            int r10 = r13 * 2
            r10 = r2[r10]
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)
            float r10 = (float) r10
            float r12 = r0.scale
            float r10 = r10 * r12
            int r12 = r13 * 2
            r14 = 1
            int r12 = r12 + r14
            r12 = r2[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r0.scale
            float r12 = r12 * r14
            r9.lineTo(r10, r12)
        L_0x09b0:
            int r13 = r13 + 1
            r14 = r28
            r12 = r29
            r10 = r30
            r9 = r31
            goto L_0x0912
        L_0x09bc:
            r31 = r9
            r30 = r10
            r29 = r12
            r10 = 255(0xff, float:3.57E-43)
            goto L_0x0b9d
        L_0x09c6:
            r31 = r9
            r30 = r10
            r29 = r12
            r28 = r14
            if (r11 != 0) goto L_0x0a8b
            r9 = 0
        L_0x09d1:
            r10 = 5
            if (r9 >= r10) goto L_0x0a5f
            if (r9 != 0) goto L_0x0a19
            android.graphics.Path r10 = r0.path1
            int r12 = r9 * 2
            r12 = r4[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r4[r13]
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.moveTo(r12, r13)
            android.graphics.Path r10 = r0.path2
            int r12 = r9 * 2
            r12 = r5[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r5[r13]
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.moveTo(r12, r13)
            goto L_0x0a5b
        L_0x0a19:
            android.graphics.Path r10 = r0.path1
            int r12 = r9 * 2
            r12 = r4[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r4[r13]
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.lineTo(r12, r13)
            android.graphics.Path r10 = r0.path2
            int r12 = r9 * 2
            r12 = r5[r12]
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r5[r13]
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.lineTo(r12, r13)
        L_0x0a5b:
            int r9 = r9 + 1
            goto L_0x09d1
        L_0x0a5f:
            int r9 = r0.nextIcon
            r10 = 4
            if (r9 != r10) goto L_0x0a76
            android.graphics.Paint r9 = r0.paint2
            float r10 = r0.transitionProgress
            r12 = 1065353216(0x3f800000, float:1.0)
            float r14 = r12 - r10
            float r14 = r14 * r20
            int r10 = (int) r14
            r9.setAlpha(r10)
            r10 = 255(0xff, float:3.57E-43)
            goto L_0x0b9d
        L_0x0a76:
            android.graphics.Paint r10 = r0.paint2
            int r12 = r0.currentIcon
            if (r12 != r9) goto L_0x0a7f
            r9 = 255(0xff, float:3.57E-43)
            goto L_0x0a84
        L_0x0a7f:
            float r9 = r0.transitionProgress
            float r9 = r9 * r20
            int r9 = (int) r9
        L_0x0a84:
            r10.setAlpha(r9)
            r10 = 255(0xff, float:3.57E-43)
            goto L_0x0b9d
        L_0x0a8b:
            r9 = 0
        L_0x0a8c:
            r10 = 5
            if (r9 >= r10) goto L_0x0b96
            if (r9 != 0) goto L_0x0b14
            android.graphics.Path r12 = r0.path1
            int r13 = r9 * 2
            r13 = r4[r13]
            int r14 = r9 * 2
            r14 = r11[r14]
            int r25 = r9 * 2
            r25 = r4[r25]
            float r14 = r14 - r25
            float r14 = r14 * r1
            float r13 = r13 + r14
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            int r14 = r9 * 2
            r22 = 1
            int r14 = r14 + 1
            r14 = r4[r14]
            int r25 = r9 * 2
            int r25 = r25 + 1
            r25 = r11[r25]
            int r32 = r9 * 2
            int r32 = r32 + 1
            r32 = r4[r32]
            float r25 = r25 - r32
            float r25 = r25 * r1
            float r14 = r14 + r25
            int r14 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            float r10 = r0.scale
            float r14 = r14 * r10
            r12.moveTo(r13, r14)
            android.graphics.Path r10 = r0.path2
            int r12 = r9 * 2
            r12 = r5[r12]
            int r13 = r9 * 2
            r13 = r15[r13]
            int r14 = r9 * 2
            r14 = r5[r14]
            float r13 = r13 - r14
            float r13 = r13 * r1
            float r12 = r12 + r13
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r5[r13]
            int r22 = r9 * 2
            int r22 = r22 + 1
            r32 = r15[r22]
            int r22 = r9 * 2
            int r33 = r22 + 1
            r14 = r5[r33]
            float r32 = r32 - r14
            float r32 = r32 * r1
            float r13 = r13 + r32
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.moveTo(r12, r13)
            goto L_0x0b92
        L_0x0b14:
            android.graphics.Path r10 = r0.path1
            int r12 = r9 * 2
            r12 = r4[r12]
            int r13 = r9 * 2
            r13 = r11[r13]
            int r14 = r9 * 2
            r14 = r4[r14]
            float r13 = r13 - r14
            float r13 = r13 * r1
            float r12 = r12 + r13
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r4[r13]
            int r22 = r9 * 2
            int r22 = r22 + 1
            r32 = r11[r22]
            int r22 = r9 * 2
            int r33 = r22 + 1
            r14 = r4[r33]
            float r32 = r32 - r14
            float r32 = r32 * r1
            float r13 = r13 + r32
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.lineTo(r12, r13)
            android.graphics.Path r10 = r0.path2
            int r12 = r9 * 2
            r12 = r5[r12]
            int r13 = r9 * 2
            r13 = r15[r13]
            int r14 = r9 * 2
            r14 = r5[r14]
            float r13 = r13 - r14
            float r13 = r13 * r1
            float r12 = r12 + r13
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r0.scale
            float r12 = r12 * r13
            int r13 = r9 * 2
            r14 = 1
            int r13 = r13 + r14
            r13 = r5[r13]
            int r22 = r9 * 2
            int r22 = r22 + 1
            r32 = r15[r22]
            int r22 = r9 * 2
            int r33 = r22 + 1
            r14 = r5[r33]
            float r32 = r32 - r14
            float r32 = r32 * r1
            float r13 = r13 + r32
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            float r14 = r0.scale
            float r13 = r13 * r14
            r10.lineTo(r12, r13)
        L_0x0b92:
            int r9 = r9 + 1
            goto L_0x0a8c
        L_0x0b96:
            android.graphics.Paint r9 = r0.paint2
            r10 = 255(0xff, float:3.57E-43)
            r9.setAlpha(r10)
        L_0x0b9d:
            android.graphics.Path r9 = r0.path1
            r9.close()
            android.graphics.Path r9 = r0.path2
            r9.close()
            r40.save()
            int r9 = r8.left
            float r9 = (float) r9
            int r12 = r8.top
            float r12 = (float) r12
            r7.translate(r9, r12)
            float r9 = (float) r3
            int r12 = r6 - r3
            float r12 = (float) r12
            float r12 = r12 * r1
            float r9 = r9 + r12
            int r12 = r8.left
            int r12 = r31 - r12
            float r12 = (float) r12
            int r13 = r8.top
            int r13 = r30 - r13
            float r13 = (float) r13
            r7.rotate(r9, r12, r13)
            int r9 = r0.currentIcon
            if (r9 == 0) goto L_0x0bce
            r12 = 1
            if (r9 != r12) goto L_0x0bd3
        L_0x0bce:
            int r9 = r0.currentIcon
            r12 = 4
            if (r9 != r12) goto L_0x0be3
        L_0x0bd3:
            int r9 = r8.left
            int r9 = r31 - r9
            float r9 = (float) r9
            int r12 = r8.top
            int r12 = r30 - r12
            float r12 = (float) r12
            r13 = r29
            r7.scale(r13, r13, r9, r12)
            goto L_0x0be5
        L_0x0be3:
            r13 = r29
        L_0x0be5:
            android.graphics.Path r9 = r0.path1
            android.graphics.Paint r12 = r0.paint2
            r7.drawPath(r9, r12)
            android.graphics.Path r9 = r0.path2
            android.graphics.Paint r12 = r0.paint2
            r7.drawPath(r9, r12)
            r40.restore()
        L_0x0bf6:
            int r1 = r0.currentIcon
            r2 = 6
            if (r1 == r2) goto L_0x0bff
            int r1 = r0.nextIcon
            if (r1 != r2) goto L_0x0c90
        L_0x0bff:
            int r1 = r0.currentIcon
            if (r1 == r2) goto L_0x0c29
            float r1 = r0.transitionProgress
            int r2 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c24
            float r1 = r1 - r21
            float r1 = r1 / r21
            float r2 = r1 / r21
            r3 = 1065353216(0x3f800000, float:1.0)
            float r2 = java.lang.Math.min(r3, r2)
            float r14 = r3 - r2
            int r2 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c20
            float r2 = r1 - r21
            float r3 = r2 / r21
            goto L_0x0c21
        L_0x0c20:
            r3 = 0
        L_0x0c21:
            r1 = r3
            r9 = r1
            goto L_0x0c2d
        L_0x0c24:
            r14 = 1065353216(0x3f800000, float:1.0)
            r1 = 0
            r9 = r1
            goto L_0x0c2d
        L_0x0c29:
            r14 = 0
            r1 = 1065353216(0x3f800000, float:1.0)
            r9 = r1
        L_0x0c2d:
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r19)
            int r11 = r30 + r1
            r1 = 1077936128(0x40400000, float:3.0)
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r1)
            int r12 = r31 - r1
            android.graphics.Paint r1 = r0.paint
            r1.setAlpha(r10)
            r1 = 1065353216(0x3f800000, float:1.0)
            int r2 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r2 >= 0) goto L_0x0c6f
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r1 = r12 - r1
            float r2 = (float) r1
            int r1 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            int r1 = r11 - r1
            float r3 = (float) r1
            float r1 = (float) r12
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r4 = (float) r4
            float r4 = r4 * r14
            float r4 = r1 - r4
            float r1 = (float) r11
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r17)
            float r5 = (float) r5
            float r5 = r5 * r14
            float r5 = r1 - r5
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0c6f:
            r1 = 0
            int r2 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c90
            float r2 = (float) r12
            float r3 = (float) r11
            float r1 = (float) r12
            int r4 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            float r4 = (float) r4
            float r4 = r4 * r9
            float r4 = r4 + r1
            float r1 = (float) r11
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r16)
            float r5 = (float) r5
            float r5 = r5 * r9
            float r5 = r1 - r5
            android.graphics.Paint r6 = r0.paint
            r1 = r40
            r1.drawLine(r2, r3, r4, r5, r6)
        L_0x0c90:
            if (r28 == 0) goto L_0x0cec
            r1 = r18
            r2 = r28
            if (r2 == r1) goto L_0x0cf0
            int r3 = r2.getIntrinsicWidth()
            float r3 = (float) r3
            float r3 = r3 * r26
            int r3 = (int) r3
            int r4 = r2.getIntrinsicHeight()
            float r4 = (float) r4
            float r4 = r4 * r26
            int r4 = (int) r4
            int r5 = r0.currentIcon
            r6 = 15
            if (r5 == r6) goto L_0x0cc0
            r6 = 16
            if (r5 == r6) goto L_0x0cc0
            r6 = 17
            if (r5 == r6) goto L_0x0cc0
            r6 = 19
            if (r5 == r6) goto L_0x0cc0
            r6 = 18
            if (r5 == r6) goto L_0x0cc0
            r6 = 20
        L_0x0cc0:
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0cc9
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0cd2
        L_0x0cc9:
            float r5 = r0.transitionProgress
            r6 = 1065353216(0x3f800000, float:1.0)
            float r14 = r6 - r5
            float r14 = r14 * r20
            int r5 = (int) r14
        L_0x0cd2:
            r2.setAlpha(r5)
            int r5 = r3 / 2
            int r9 = r31 - r5
            int r5 = r4 / 2
            int r5 = r30 - r5
            int r6 = r3 / 2
            int r6 = r31 + r6
            int r11 = r4 / 2
            int r11 = r30 + r11
            r2.setBounds(r9, r5, r6, r11)
            r2.draw(r7)
            goto L_0x0cf0
        L_0x0cec:
            r1 = r18
            r2 = r28
        L_0x0cf0:
            if (r1 == 0) goto L_0x0d43
            int r3 = r1.getIntrinsicWidth()
            float r3 = (float) r3
            float r3 = r3 * r13
            int r3 = (int) r3
            int r4 = r1.getIntrinsicHeight()
            float r4 = (float) r4
            float r4 = r4 * r13
            int r4 = (int) r4
            int r5 = r0.nextIcon
            r6 = 15
            if (r5 == r6) goto L_0x0d1c
            int r5 = r0.currentIcon
            r6 = 16
            if (r5 == r6) goto L_0x0d1c
            r6 = 17
            if (r5 == r6) goto L_0x0d1c
            r6 = 19
            if (r5 == r6) goto L_0x0d1c
            r6 = 18
            if (r5 == r6) goto L_0x0d1c
            r6 = 20
        L_0x0d1c:
            int r5 = r0.currentIcon
            int r6 = r0.nextIcon
            if (r5 != r6) goto L_0x0d25
            r5 = 255(0xff, float:3.57E-43)
            goto L_0x0d2a
        L_0x0d25:
            float r5 = r0.transitionProgress
            float r5 = r5 * r20
            int r5 = (int) r5
        L_0x0d2a:
            r1.setAlpha(r5)
            int r5 = r3 / 2
            int r9 = r31 - r5
            int r5 = r4 / 2
            int r10 = r30 - r5
            int r5 = r3 / 2
            int r5 = r31 + r5
            int r6 = r4 / 2
            int r6 = r30 + r6
            r1.setBounds(r9, r10, r5, r6)
            r1.draw(r7)
        L_0x0d43:
            long r3 = java.lang.System.currentTimeMillis()
            long r5 = r0.lastAnimationTime
            long r5 = r3 - r5
            r9 = 17
            int r11 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x0d53
            r5 = 17
        L_0x0d53:
            r0.lastAnimationTime = r3
            int r9 = r0.currentIcon
            r10 = 3
            if (r9 == r10) goto L_0x0d6f
            r10 = 14
            if (r9 == r10) goto L_0x0d6f
            r11 = 4
            if (r9 != r11) goto L_0x0d65
            int r9 = r0.nextIcon
            if (r9 == r10) goto L_0x0d6f
        L_0x0d65:
            int r9 = r0.currentIcon
            r10 = 10
            if (r9 == r10) goto L_0x0d6f
            r10 = 13
            if (r9 != r10) goto L_0x0db8
        L_0x0d6f:
            float r9 = r0.downloadRadOffset
            r10 = 360(0x168, double:1.78E-321)
            long r10 = r10 * r5
            float r10 = (float) r10
            r11 = 1159479296(0x451c4000, float:2500.0)
            float r10 = r10 / r11
            float r9 = r9 + r10
            r0.downloadRadOffset = r9
            float r9 = r0.getCircleValue(r9)
            r0.downloadRadOffset = r9
            int r9 = r0.nextIcon
            r10 = 2
            if (r9 == r10) goto L_0x0db5
            float r9 = r0.downloadProgress
            float r10 = r0.downloadProgressAnimationStart
            float r11 = r9 - r10
            r12 = 0
            int r14 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1))
            if (r14 <= 0) goto L_0x0db5
            float r12 = r0.downloadProgressTime
            float r14 = (float) r5
            float r12 = r12 + r14
            r0.downloadProgressTime = r12
            r14 = 1128792064(0x43480000, float:200.0)
            int r14 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r14 < 0) goto L_0x0da7
            r0.animatedDownloadProgress = r9
            r0.downloadProgressAnimationStart = r9
            r9 = 0
            r0.downloadProgressTime = r9
            goto L_0x0db5
        L_0x0da7:
            android.view.animation.DecelerateInterpolator r9 = r0.interpolator
            r14 = 1128792064(0x43480000, float:200.0)
            float r12 = r12 / r14
            float r9 = r9.getInterpolation(r12)
            float r9 = r9 * r11
            float r10 = r10 + r9
            r0.animatedDownloadProgress = r10
        L_0x0db5:
            r39.invalidateSelf()
        L_0x0db8:
            boolean r9 = r0.animatingTransition
            if (r9 == 0) goto L_0x0ddb
            float r9 = r0.transitionProgress
            r10 = 1065353216(0x3f800000, float:1.0)
            int r11 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r11 >= 0) goto L_0x0ddb
            float r11 = (float) r5
            float r12 = r0.transitionAnimationTime
            float r11 = r11 / r12
            float r9 = r9 + r11
            r0.transitionProgress = r9
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 < 0) goto L_0x0dd8
            int r9 = r0.nextIcon
            r0.currentIcon = r9
            r0.transitionProgress = r10
            r9 = 0
            r0.animatingTransition = r9
        L_0x0dd8:
            r39.invalidateSelf()
        L_0x0ddb:
            int r9 = r0.nextIcon
            r10 = 4
            if (r9 == r10) goto L_0x0dec
            r10 = 6
            if (r9 == r10) goto L_0x0de7
            r10 = 10
            if (r9 != r10) goto L_0x0def
        L_0x0de7:
            int r9 = r0.currentIcon
            r10 = 4
            if (r9 != r10) goto L_0x0def
        L_0x0dec:
            r40.restore()
        L_0x0def:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.MediaActionDrawable.draw(android.graphics.Canvas):void");
    }

    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
