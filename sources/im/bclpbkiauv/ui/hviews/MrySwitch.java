package im.bclpbkiauv.ui.hviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.app.NotificationCompat;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class MrySwitch extends View {
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    /* access modifiers changed from: private */
    public ObjectAnimator checkAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    /* access modifiers changed from: private */
    public ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress = 1.0f;
    private boolean isChecked;
    private int lastIconColor;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private int overrideColorProgress;
    private Paint paint = new Paint(1);
    private Paint paint2;
    private int[] pressedState = {16842910, 16842919};
    private float progress;
    private RectF rectF = new RectF();
    private RippleDrawable rippleDrawable;
    /* access modifiers changed from: private */
    public Paint ripplePaint;
    private String thumbCheckedColorKey = Theme.key_windowBackgroundWhite;
    private String thumbColorKey = Theme.key_windowBackgroundWhite;
    private String trackCheckedColorKey = Theme.key_switch2TrackChecked;
    private String trackColorKey = Theme.key_switch2Track;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(MrySwitch mrySwitch, boolean z);
    }

    public MrySwitch(Context context) {
        super(context);
        Paint paint3 = new Paint(1);
        this.paint2 = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        this.paint2.setStrokeCap(Paint.Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
    }

    public MrySwitch(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Paint paint3 = new Paint(1);
        this.paint2 = paint3;
        paint3.setStyle(Paint.Style.STROKE);
        this.paint2.setStrokeCap(Paint.Cap.ROUND);
        this.paint2.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
    }

    public void setProgress(float value) {
        if (this.progress != value) {
            this.progress = value;
            invalidate();
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setIconProgress(float value) {
        if (this.iconProgress != value) {
            this.iconProgress = value;
            invalidate();
        }
    }

    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        ObjectAnimator objectAnimator = this.iconAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int type) {
        this.drawIconType = type;
    }

    public void setDrawRipple(boolean value) {
        Drawable maskDrawable;
        if (Build.VERSION.SDK_INT >= 21 && value != this.drawRipple) {
            this.drawRipple = value;
            int i = 1;
            if (this.rippleDrawable == null) {
                Paint paint3 = new Paint(1);
                this.ripplePaint = paint3;
                paint3.setColor(-1);
                if (Build.VERSION.SDK_INT >= 23) {
                    maskDrawable = null;
                } else {
                    maskDrawable = new Drawable() {
                        public void draw(Canvas canvas) {
                            Rect bounds = getBounds();
                            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), (float) AndroidUtilities.dp(18.0f), MrySwitch.this.ripplePaint);
                        }

                        public void setAlpha(int alpha) {
                        }

                        public void setColorFilter(ColorFilter colorFilter) {
                        }

                        public int getOpacity() {
                            return 0;
                        }
                    };
                }
                this.rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), (Drawable) null, maskDrawable);
                if (Build.VERSION.SDK_INT >= 23) {
                    this.rippleDrawable.setRadius(AndroidUtilities.dp(18.0f));
                }
                this.rippleDrawable.setCallback(this);
            }
            if ((this.isChecked && this.colorSet != 2) || (!this.isChecked && this.colorSet != 1)) {
                this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{Theme.getColor(this.isChecked ? Theme.key_switchTrackBlueSelectorChecked : Theme.key_switchTrackBlueSelector)}));
                if (this.isChecked) {
                    i = 2;
                }
                this.colorSet = i;
            }
            if (Build.VERSION.SDK_INT >= 28 && value) {
                this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : (float) AndroidUtilities.dp(100.0f), (float) AndroidUtilities.dp(18.0f));
            }
            this.rippleDrawable.setState(value ? this.pressedState : StateSet.NOTHING);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.rippleDrawable;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean verifyDrawable(android.graphics.drawable.Drawable r2) {
        /*
            r1 = this;
            boolean r0 = super.verifyDrawable(r2)
            if (r0 != 0) goto L_0x000f
            android.graphics.drawable.RippleDrawable r0 = r1.rippleDrawable
            if (r0 == 0) goto L_0x000d
            if (r2 != r0) goto L_0x000d
            goto L_0x000f
        L_0x000d:
            r0 = 0
            goto L_0x0010
        L_0x000f:
            r0 = 1
        L_0x0010:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.MrySwitch.verifyDrawable(android.graphics.drawable.Drawable):boolean");
    }

    public void setColors(String track, String trackChecked, String thumb, String thumbChecked) {
        this.trackColorKey = track;
        this.trackCheckedColorKey = trackChecked;
        this.thumbColorKey = thumb;
        this.thumbCheckedColorKey = thumbChecked;
    }

    public void settrackCheckedColors(String trackChecked) {
        this.trackCheckedColorKey = trackChecked;
        invalidate();
    }

    private void animateToCheckedState(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, NotificationCompat.CATEGORY_PROGRESS, fArr);
        this.checkAnimator = ofFloat;
        ofFloat.setDuration(250);
        this.checkAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator unused = MrySwitch.this.checkAnimator = null;
            }
        });
        this.checkAnimator.start();
    }

    private void animateIcon(boolean newCheckedState) {
        float[] fArr = new float[1];
        fArr[0] = newCheckedState ? 1.0f : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "iconProgress", fArr);
        this.iconAnimator = ofFloat;
        ofFloat.setDuration(250);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator unused = MrySwitch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setChecked(boolean checked, boolean animated) {
        setChecked(checked, this.drawIconType, animated);
    }

    public void setChecked(boolean checked, int iconType, boolean animated) {
        float f = 1.0f;
        if (checked != this.isChecked) {
            this.isChecked = checked;
            if (!this.attachedToWindow || !animated) {
                cancelCheckAnimator();
                setProgress(checked ? 1.0f : 0.0f);
            } else {
                animateToCheckedState(checked);
            }
            OnCheckedChangeListener onCheckedChangeListener2 = this.onCheckedChangeListener;
            if (onCheckedChangeListener2 != null) {
                onCheckedChangeListener2.onCheckedChanged(this, checked);
            }
        }
        if (this.drawIconType != iconType) {
            this.drawIconType = iconType;
            if (!this.attachedToWindow || !animated) {
                cancelIconAnimator();
                if (iconType != 0) {
                    f = 0.0f;
                }
                setIconProgress(f);
                return;
            }
            animateIcon(iconType == 0);
        }
    }

    public void setIcon(int icon) {
        if (icon != 0) {
            Drawable mutate = getResources().getDrawable(icon).mutate();
            this.iconDrawable = mutate;
            if (mutate != null) {
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey);
                this.lastIconColor = color;
                mutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                return;
            }
            return;
        }
        this.iconDrawable = null;
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int override) {
        if (this.overrideColorProgress != override) {
            if (this.overlayBitmap == null) {
                try {
                    this.overlayBitmap = new Bitmap[2];
                    this.overlayCanvas = new Canvas[2];
                    for (int a = 0; a < 2; a++) {
                        this.overlayBitmap[a] = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                        this.overlayCanvas[a] = new Canvas(this.overlayBitmap[a]);
                    }
                    this.overlayMaskBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                    this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
                    Paint paint3 = new Paint(1);
                    this.overlayEraserPaint = paint3;
                    paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    Paint paint4 = new Paint(1);
                    this.overlayMaskPaint = paint4;
                    paint4.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                    this.bitmapsCreated = true;
                } catch (Throwable th) {
                    return;
                }
            }
            if (this.bitmapsCreated) {
                this.overrideColorProgress = override;
                this.overlayCx = 0.0f;
                this.overlayCy = 0.0f;
                this.overlayRad = 0.0f;
                invalidate();
            }
        }
    }

    public void setOverrideColorProgress(float cx, float cy, float rad) {
        this.overlayCx = cx;
        this.overlayCy = cy;
        this.overlayRad = rad;
        invalidate();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x01cf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r53) {
        /*
            r52 = this;
            r0 = r52
            r1 = r53
            int r2 = r52.getVisibility()
            if (r2 == 0) goto L_0x000b
            return
        L_0x000b:
            android.content.res.Resources r2 = r52.getResources()
            r3 = 2131165460(0x7f070114, float:1.7945138E38)
            int r2 = r2.getDimensionPixelSize(r3)
            r3 = 1101004800(0x41a00000, float:20.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r3)
            int r4 = r52.getMeasuredWidth()
            int r4 = r4 - r2
            r5 = 2
            int r4 = r4 / r5
            int r6 = r52.getMeasuredHeight()
            float r6 = (float) r6
            r7 = 1106247680(0x41f00000, float:30.0)
            float r8 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r7)
            float r6 = r6 - r8
            r8 = 1073741824(0x40000000, float:2.0)
            float r6 = r6 / r8
            r9 = 1097859072(0x41700000, float:15.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r10 = r10 + r4
            int r11 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            int r11 = r2 - r11
            float r11 = (float) r11
            float r12 = r0.progress
            float r11 = r11 * r12
            int r11 = (int) r11
            int r10 = r10 + r11
            int r11 = r52.getMeasuredHeight()
            int r11 = r11 / r5
            r12 = 0
        L_0x004c:
            r14 = 0
            r13 = 1
            if (r12 >= r5) goto L_0x01f1
            if (r12 != r13) goto L_0x0061
            int r8 = r0.overrideColorProgress
            if (r8 != 0) goto L_0x0061
            r34 = r2
            r20 = r3
            r33 = r4
            r1 = r12
            r25 = 1106247680(0x41f00000, float:30.0)
            goto L_0x01de
        L_0x0061:
            if (r12 != 0) goto L_0x0065
            r8 = r1
            goto L_0x0069
        L_0x0065:
            android.graphics.Canvas[] r8 = r0.overlayCanvas
            r8 = r8[r14]
        L_0x0069:
            if (r12 != r13) goto L_0x00b1
            android.graphics.Bitmap[] r15 = r0.overlayBitmap
            r15 = r15[r14]
            r15.eraseColor(r14)
            android.graphics.Paint r14 = r0.paint
            r15 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r14.setColor(r15)
            android.graphics.Canvas r14 = r0.overlayMaskCanvas
            r19 = 0
            r20 = 0
            android.graphics.Bitmap r15 = r0.overlayMaskBitmap
            int r15 = r15.getWidth()
            float r15 = (float) r15
            android.graphics.Bitmap r9 = r0.overlayMaskBitmap
            int r9 = r9.getHeight()
            float r9 = (float) r9
            android.graphics.Paint r7 = r0.paint
            r18 = r14
            r21 = r15
            r22 = r9
            r23 = r7
            r18.drawRect(r19, r20, r21, r22, r23)
            android.graphics.Canvas r7 = r0.overlayMaskCanvas
            float r9 = r0.overlayCx
            float r14 = r52.getX()
            float r9 = r9 - r14
            float r14 = r0.overlayCy
            float r15 = r52.getY()
            float r14 = r14 - r15
            float r15 = r0.overlayRad
            android.graphics.Paint r5 = r0.overlayEraserPaint
            r7.drawCircle(r9, r14, r15, r5)
        L_0x00b1:
            int r5 = r0.overrideColorProgress
            if (r5 != r13) goto L_0x00bf
            if (r12 != 0) goto L_0x00ba
            r16 = 0
            goto L_0x00bc
        L_0x00ba:
            r16 = 1065353216(0x3f800000, float:1.0)
        L_0x00bc:
            r5 = r16
            goto L_0x00ce
        L_0x00bf:
            r7 = 2
            if (r5 != r7) goto L_0x00cc
            if (r12 != 0) goto L_0x00c7
            r16 = 1065353216(0x3f800000, float:1.0)
            goto L_0x00c9
        L_0x00c7:
            r16 = 0
        L_0x00c9:
            r5 = r16
            goto L_0x00ce
        L_0x00cc:
            float r5 = r0.progress
        L_0x00ce:
            java.lang.String r7 = r0.trackColorKey
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            java.lang.String r9 = r0.trackCheckedColorKey
            int r9 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r9)
            if (r12 != 0) goto L_0x0103
            android.graphics.drawable.Drawable r14 = r0.iconDrawable
            if (r14 == 0) goto L_0x0103
            int r14 = r0.lastIconColor
            boolean r15 = r0.isChecked
            if (r15 == 0) goto L_0x00e8
            r15 = r9
            goto L_0x00e9
        L_0x00e8:
            r15 = r7
        L_0x00e9:
            if (r14 == r15) goto L_0x0103
            android.graphics.drawable.Drawable r14 = r0.iconDrawable
            android.graphics.PorterDuffColorFilter r15 = new android.graphics.PorterDuffColorFilter
            boolean r13 = r0.isChecked
            if (r13 == 0) goto L_0x00f5
            r13 = r9
            goto L_0x00f6
        L_0x00f5:
            r13 = r7
        L_0x00f6:
            r0.lastIconColor = r13
            r20 = r3
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r15.<init>(r13, r3)
            r14.setColorFilter(r15)
            goto L_0x0105
        L_0x0103:
            r20 = r3
        L_0x0105:
            int r3 = android.graphics.Color.red(r7)
            int r13 = android.graphics.Color.red(r9)
            int r14 = android.graphics.Color.green(r7)
            int r15 = android.graphics.Color.green(r9)
            int r1 = android.graphics.Color.blue(r7)
            int r16 = android.graphics.Color.blue(r9)
            r21 = r12
            int r12 = android.graphics.Color.alpha(r7)
            int r22 = android.graphics.Color.alpha(r9)
            r23 = r7
            float r7 = (float) r3
            r26 = r9
            int r9 = r13 - r3
            float r9 = (float) r9
            float r9 = r9 * r5
            float r7 = r7 + r9
            int r7 = (int) r7
            float r9 = (float) r14
            r27 = r3
            int r3 = r15 - r14
            float r3 = (float) r3
            float r3 = r3 * r5
            float r9 = r9 + r3
            int r3 = (int) r9
            float r9 = (float) r1
            r28 = r13
            int r13 = r16 - r1
            float r13 = (float) r13
            float r13 = r13 * r5
            float r9 = r9 + r13
            int r9 = (int) r9
            float r13 = (float) r12
            r29 = r1
            int r1 = r22 - r12
            float r1 = (float) r1
            float r1 = r1 * r5
            float r13 = r13 + r1
            int r1 = (int) r13
            r13 = r1 & 255(0xff, float:3.57E-43)
            int r13 = r13 << 24
            r30 = r1
            r1 = r7 & 255(0xff, float:3.57E-43)
            int r1 = r1 << 16
            r1 = r1 | r13
            r13 = r3 & 255(0xff, float:3.57E-43)
            int r13 = r13 << 8
            r1 = r1 | r13
            r13 = r9 & 255(0xff, float:3.57E-43)
            r1 = r1 | r13
            android.graphics.Paint r13 = r0.paint
            r13.setColor(r1)
            android.graphics.Paint r13 = r0.paint2
            r13.setColor(r1)
            android.graphics.RectF r13 = r0.rectF
            r31 = r1
            float r1 = (float) r4
            r32 = r3
            int r3 = r4 + r2
            float r3 = (float) r3
            r25 = 1106247680(0x41f00000, float:30.0)
            float r33 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r25)
            r34 = r2
            float r2 = r6 + r33
            r13.set(r1, r6, r3, r2)
            android.graphics.RectF r1 = r0.rectF
            r2 = 1097859072(0x41700000, float:15.0)
            float r3 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r2)
            float r13 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r2)
            android.graphics.Paint r2 = r0.paint
            r8.drawRoundRect(r1, r3, r13, r2)
            float r1 = (float) r10
            float r2 = (float) r11
            r3 = 1097859072(0x41700000, float:15.0)
            float r13 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r3)
            android.graphics.Paint r3 = r0.paint
            r8.drawCircle(r1, r2, r13, r3)
            if (r21 != 0) goto L_0x01cf
            android.graphics.drawable.RippleDrawable r1 = r0.rippleDrawable
            if (r1 == 0) goto L_0x01cf
            r2 = 1099956224(0x41900000, float:18.0)
            int r3 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r3 = r10 - r3
            int r13 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r13 = r11 - r13
            int r17 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            r33 = r4
            int r4 = r10 + r17
            int r2 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 + r11
            r1.setBounds(r3, r13, r4, r2)
            android.graphics.drawable.RippleDrawable r1 = r0.rippleDrawable
            r1.draw(r8)
            r1 = r21
            goto L_0x01de
        L_0x01cf:
            r33 = r4
            r1 = r21
            r2 = 1
            if (r1 != r2) goto L_0x01de
            android.graphics.Bitmap r2 = r0.overlayMaskBitmap
            android.graphics.Paint r3 = r0.overlayMaskPaint
            r4 = 0
            r8.drawBitmap(r2, r4, r4, r3)
        L_0x01de:
            int r12 = r1 + 1
            r1 = r53
            r3 = r20
            r4 = r33
            r2 = r34
            r5 = 2
            r7 = 1106247680(0x41f00000, float:30.0)
            r8 = 1073741824(0x40000000, float:2.0)
            r9 = 1097859072(0x41700000, float:15.0)
            goto L_0x004c
        L_0x01f1:
            r34 = r2
            r20 = r3
            r33 = r4
            r1 = r12
            int r1 = r0.overrideColorProgress
            r2 = 0
            if (r1 == 0) goto L_0x0208
            android.graphics.Bitmap[] r1 = r0.overlayBitmap
            r1 = r1[r14]
            r3 = r53
            r4 = 0
            r3.drawBitmap(r1, r4, r4, r2)
            goto L_0x020a
        L_0x0208:
            r3 = r53
        L_0x020a:
            r1 = 0
        L_0x020b:
            r4 = 2
            if (r1 >= r4) goto L_0x047e
            r4 = 1
            if (r1 != r4) goto L_0x021c
            int r4 = r0.overrideColorProgress
            if (r4 != 0) goto L_0x021c
            r31 = r6
            r5 = 2
            r8 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0476
        L_0x021c:
            if (r1 != 0) goto L_0x0221
            r4 = r3
            r5 = 1
            goto L_0x0226
        L_0x0221:
            android.graphics.Canvas[] r4 = r0.overlayCanvas
            r5 = 1
            r4 = r4[r5]
        L_0x0226:
            if (r1 != r5) goto L_0x022f
            android.graphics.Bitmap[] r7 = r0.overlayBitmap
            r7 = r7[r5]
            r7.eraseColor(r14)
        L_0x022f:
            int r7 = r0.overrideColorProgress
            if (r7 != r5) goto L_0x023a
            if (r1 != 0) goto L_0x0237
            r5 = 0
            goto L_0x0239
        L_0x0237:
            r5 = 1065353216(0x3f800000, float:1.0)
        L_0x0239:
            goto L_0x0246
        L_0x023a:
            r5 = 2
            if (r7 != r5) goto L_0x0244
            if (r1 != 0) goto L_0x0242
            r5 = 1065353216(0x3f800000, float:1.0)
            goto L_0x0243
        L_0x0242:
            r5 = 0
        L_0x0243:
            goto L_0x0246
        L_0x0244:
            float r5 = r0.progress
        L_0x0246:
            java.lang.String r7 = r0.thumbColorKey
            int r7 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r7)
            java.lang.String r8 = r0.thumbCheckedColorKey
            int r8 = im.bclpbkiauv.ui.actionbar.Theme.getColor(r8)
            int r9 = android.graphics.Color.red(r7)
            int r12 = android.graphics.Color.red(r8)
            int r13 = android.graphics.Color.green(r7)
            int r15 = android.graphics.Color.green(r8)
            int r14 = android.graphics.Color.blue(r7)
            int r28 = android.graphics.Color.blue(r8)
            int r2 = android.graphics.Color.alpha(r7)
            int r30 = android.graphics.Color.alpha(r8)
            r31 = r6
            float r6 = (float) r9
            r32 = r7
            int r7 = r12 - r9
            float r7 = (float) r7
            float r7 = r7 * r5
            float r6 = r6 + r7
            int r6 = (int) r6
            float r7 = (float) r13
            r35 = r8
            int r8 = r15 - r13
            float r8 = (float) r8
            float r8 = r8 * r5
            float r7 = r7 + r8
            int r7 = (int) r7
            float r8 = (float) r14
            r36 = r9
            int r9 = r28 - r14
            float r9 = (float) r9
            float r9 = r9 * r5
            float r8 = r8 + r9
            int r8 = (int) r8
            float r9 = (float) r2
            r37 = r12
            int r12 = r30 - r2
            float r12 = (float) r12
            float r12 = r12 * r5
            float r9 = r9 + r12
            int r9 = (int) r9
            android.graphics.Paint r12 = r0.paint
            r38 = r2
            r2 = r9 & 255(0xff, float:3.57E-43)
            int r2 = r2 << 24
            r39 = r5
            r5 = r6 & 255(0xff, float:3.57E-43)
            int r5 = r5 << 16
            r2 = r2 | r5
            r5 = r7 & 255(0xff, float:3.57E-43)
            int r5 = r5 << 8
            r2 = r2 | r5
            r5 = r8 & 255(0xff, float:3.57E-43)
            r2 = r2 | r5
            r12.setColor(r2)
            float r2 = (float) r10
            float r5 = (float) r11
            r12 = 1095761920(0x41500000, float:13.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r40 = r6
            android.graphics.Paint r6 = r0.paint
            r4.drawCircle(r2, r5, r12, r6)
            if (r1 != 0) goto L_0x045c
            android.graphics.drawable.Drawable r2 = r0.iconDrawable
            if (r2 == 0) goto L_0x0306
            int r5 = r2.getIntrinsicWidth()
            r6 = 2
            int r5 = r5 / r6
            int r5 = r10 - r5
            android.graphics.drawable.Drawable r12 = r0.iconDrawable
            int r12 = r12.getIntrinsicHeight()
            int r12 = r12 / r6
            int r12 = r11 - r12
            r41 = r7
            android.graphics.drawable.Drawable r7 = r0.iconDrawable
            int r7 = r7.getIntrinsicWidth()
            int r7 = r7 / r6
            int r7 = r7 + r10
            r42 = r8
            android.graphics.drawable.Drawable r8 = r0.iconDrawable
            int r8 = r8.getIntrinsicHeight()
            int r8 = r8 / r6
            int r8 = r8 + r11
            r2.setBounds(r5, r12, r7, r8)
            android.graphics.drawable.Drawable r2 = r0.iconDrawable
            r2.draw(r4)
            r45 = r9
            r46 = r13
            r47 = r14
            r48 = r15
            r5 = 2
            r8 = 1065353216(0x3f800000, float:1.0)
            goto L_0x046b
        L_0x0306:
            r41 = r7
            r42 = r8
            int r2 = r0.drawIconType
            r5 = 1
            if (r2 != r5) goto L_0x03f4
            float r2 = (float) r10
            r5 = 1093455053(0x412ccccd, float:10.8)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r6 = 1067869798(0x3fa66666, float:1.3)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r7 = r0.progress
            float r6 = r6 * r7
            float r5 = r5 - r6
            float r2 = r2 - r5
            int r10 = (int) r2
            float r2 = (float) r11
            r5 = 1091043328(0x41080000, float:8.5)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r6 = 1056964608(0x3f000000, float:0.5)
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r7 = r0.progress
            float r6 = r6 * r7
            float r5 = r5 - r6
            float r2 = r2 - r5
            int r11 = (int) r2
            r2 = 1083388723(0x40933333, float:4.6)
            float r2 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r2)
            int r2 = (int) r2
            int r2 = r2 + r10
            r5 = 1092091904(0x41180000, float:9.5)
            float r5 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r5)
            float r6 = (float) r11
            float r5 = r5 + r6
            int r5 = (int) r5
            r6 = 1073741824(0x40000000, float:2.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r7 = r7 + r2
            int r8 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r6)
            int r8 = r8 + r5
            r12 = 1089470464(0x40f00000, float:7.5)
            float r6 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r12)
            int r6 = (int) r6
            int r6 = r6 + r10
            r21 = 1085066445(0x40accccd, float:5.4)
            float r12 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r21)
            int r12 = (int) r12
            int r12 = r12 + r11
            r44 = 1088421888(0x40e00000, float:7.0)
            int r21 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r44)
            r45 = r9
            int r9 = r6 + r21
            int r21 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r44)
            r46 = r13
            int r13 = r12 + r21
            r47 = r14
            float r14 = (float) r6
            r48 = r15
            int r15 = r2 - r6
            float r15 = (float) r15
            r49 = r2
            float r2 = r0.progress
            float r15 = r15 * r2
            float r14 = r14 + r15
            int r6 = (int) r14
            float r14 = (float) r12
            int r15 = r5 - r12
            float r15 = (float) r15
            float r15 = r15 * r2
            float r14 = r14 + r15
            int r12 = (int) r14
            float r14 = (float) r9
            int r15 = r7 - r9
            float r15 = (float) r15
            float r15 = r15 * r2
            float r14 = r14 + r15
            int r9 = (int) r14
            float r14 = (float) r13
            int r15 = r8 - r13
            float r15 = (float) r15
            float r15 = r15 * r2
            float r14 = r14 + r15
            int r2 = (int) r14
            float r13 = (float) r6
            float r14 = (float) r12
            float r15 = (float) r9
            r50 = r5
            float r5 = (float) r2
            r51 = r2
            android.graphics.Paint r2 = r0.paint2
            r21 = r4
            r22 = r13
            r23 = r14
            r24 = r15
            r25 = r5
            r26 = r2
            r21.drawLine(r22, r23, r24, r25, r26)
            r2 = 1089470464(0x40f00000, float:7.5)
            float r2 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r2)
            int r2 = (int) r2
            int r2 = r2 + r10
            r5 = 1095237632(0x41480000, float:12.5)
            float r5 = im.bclpbkiauv.messenger.AndroidUtilities.dpf2(r5)
            int r5 = (int) r5
            int r5 = r5 + r11
            int r6 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r44)
            int r6 = r6 + r2
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r44)
            int r9 = r5 - r9
            float r12 = (float) r2
            float r13 = (float) r5
            float r14 = (float) r6
            float r15 = (float) r9
            r43 = r2
            android.graphics.Paint r2 = r0.paint2
            r22 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r26 = r2
            r21.drawLine(r22, r23, r24, r25, r26)
            r5 = 2
        L_0x03f0:
            r8 = 1065353216(0x3f800000, float:1.0)
            goto L_0x046b
        L_0x03f4:
            r45 = r9
            r46 = r13
            r47 = r14
            r48 = r15
            r5 = 2
            if (r2 == r5) goto L_0x0403
            android.animation.ObjectAnimator r2 = r0.iconAnimator
            if (r2 == 0) goto L_0x03f0
        L_0x0403:
            android.graphics.Paint r2 = r0.paint2
            r6 = 1132396544(0x437f0000, float:255.0)
            float r7 = r0.iconProgress
            r8 = 1065353216(0x3f800000, float:1.0)
            float r13 = r8 - r7
            float r13 = r13 * r6
            int r6 = (int) r13
            r2.setAlpha(r6)
            float r2 = (float) r10
            float r6 = (float) r11
            float r7 = (float) r10
            r9 = 1084227584(0x40a00000, float:5.0)
            int r9 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r9)
            int r9 = r11 - r9
            float r9 = (float) r9
            android.graphics.Paint r12 = r0.paint2
            r21 = r4
            r22 = r2
            r23 = r6
            r24 = r7
            r25 = r9
            r26 = r12
            r21.drawLine(r22, r23, r24, r25, r26)
            r4.save()
            r2 = -1028390912(0xffffffffc2b40000, float:-90.0)
            float r6 = r0.iconProgress
            float r6 = r6 * r2
            float r2 = (float) r10
            float r7 = (float) r11
            r4.rotate(r6, r2, r7)
            float r2 = (float) r10
            float r6 = (float) r11
            r7 = 1082130432(0x40800000, float:4.0)
            int r7 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r7)
            int r7 = r7 + r10
            float r7 = (float) r7
            float r9 = (float) r11
            android.graphics.Paint r12 = r0.paint2
            r22 = r2
            r23 = r6
            r24 = r7
            r25 = r9
            r26 = r12
            r21.drawLine(r22, r23, r24, r25, r26)
            r4.restore()
            goto L_0x046b
        L_0x045c:
            r41 = r7
            r42 = r8
            r45 = r9
            r46 = r13
            r47 = r14
            r48 = r15
            r5 = 2
            r8 = 1065353216(0x3f800000, float:1.0)
        L_0x046b:
            r2 = 1
            if (r1 != r2) goto L_0x0476
            android.graphics.Bitmap r2 = r0.overlayMaskBitmap
            android.graphics.Paint r6 = r0.overlayMaskPaint
            r7 = 0
            r4.drawBitmap(r2, r7, r7, r6)
        L_0x0476:
            int r1 = r1 + 1
            r6 = r31
            r2 = 0
            r14 = 0
            goto L_0x020b
        L_0x047e:
            r31 = r6
            int r1 = r0.overrideColorProgress
            if (r1 == 0) goto L_0x048e
            android.graphics.Bitmap[] r1 = r0.overlayBitmap
            r2 = 1
            r1 = r1[r2]
            r2 = 0
            r4 = 0
            r3.drawBitmap(r1, r4, r4, r2)
        L_0x048e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.MrySwitch.onDraw(android.graphics.Canvas):void");
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
        info.setCheckable(true);
        info.setChecked(this.isChecked);
    }
}
