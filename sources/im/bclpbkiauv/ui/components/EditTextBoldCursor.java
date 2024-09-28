package im.bclpbkiauv.ui.components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatEditText;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.FloatingActionMode;
import im.bclpbkiauv.ui.actionbar.FloatingToolbar;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EditTextBoldCursor extends AppCompatEditText {
    private static Class editorClass;
    private static Method getVerticalOffsetMethod;
    private static Field mCursorDrawableResField;
    private static Field mEditor;
    private static Field mScrollYField;
    private static Field mShowCursorField;
    private int activeLineColor;
    private boolean allowDrawCursor = true;
    /* access modifiers changed from: private */
    public View attachedToWindow;
    private boolean currentDrawHintAsHeader;
    private int cursorSize;
    private float cursorWidth = 2.0f;
    private Object editor;
    private StaticLayout errorLayout;
    private int errorLineColor;
    private TextPaint errorPaint;
    private CharSequence errorText;
    private boolean fixed;
    /* access modifiers changed from: private */
    public FloatingActionMode floatingActionMode;
    private FloatingToolbar floatingToolbar;
    private ViewTreeObserver.OnPreDrawListener floatingToolbarPreDrawListener;
    private GradientDrawable gradientDrawable;
    private float headerAnimationProgress;
    private int headerHintColor;
    private AnimatorSet headerTransformAnimation;
    private float hintAlpha = 1.0f;
    private int hintColor;
    private StaticLayout hintLayout;
    private boolean hintVisible = true;
    private int ignoreBottomCount;
    private int ignoreTopCount;
    private Runnable invalidateRunnable = new Runnable() {
        public void run() {
            EditTextBoldCursor.this.invalidate();
            if (EditTextBoldCursor.this.attachedToWindow != null) {
                AndroidUtilities.runOnUIThread(this, 500);
            }
        }
    };
    private long lastUpdateTime;
    private int lineColor;
    private Paint linePaint;
    private float lineSpacingExtra;
    private float lineY;
    private ViewTreeObserver.OnPreDrawListener listenerFixer;
    private Drawable mCursorDrawable;
    private Rect mTempRect;
    private boolean nextSetTextAnimated;
    private Rect rect = new Rect();
    private int scrollY;
    private boolean supportRtlHint;
    private boolean transformHintToHeader;
    private View windowView;

    private class ActionModeCallback2Wrapper extends ActionMode.Callback2 {
        private final ActionMode.Callback mWrapped;

        public ActionModeCallback2Wrapper(ActionMode.Callback wrapped) {
            this.mWrapped = wrapped;
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onCreateActionMode(mode, menu);
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(mode, menu);
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mWrapped.onActionItemClicked(mode, item);
        }

        public void onDestroyActionMode(ActionMode mode) {
            this.mWrapped.onDestroyActionMode(mode);
            EditTextBoldCursor.this.cleanupFloatingActionModeViews();
            FloatingActionMode unused = EditTextBoldCursor.this.floatingActionMode = null;
        }

        public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
            ActionMode.Callback callback = this.mWrapped;
            if (callback instanceof ActionMode.Callback2) {
                ((ActionMode.Callback2) callback).onGetContentRect(mode, view, outRect);
            } else {
                super.onGetContentRect(mode, view, outRect);
            }
        }
    }

    public EditTextBoldCursor(Context context) {
        super(context);
        if (Build.VERSION.SDK_INT >= 26) {
            setImportantForAutofill(2);
        }
        init();
    }

    public int getAutofillType() {
        return 0;
    }

    private void init() {
        this.linePaint = new Paint();
        TextPaint textPaint = new TextPaint(1);
        this.errorPaint = textPaint;
        textPaint.setTextSize((float) AndroidUtilities.dp(11.0f));
        if (Build.VERSION.SDK_INT >= 26) {
            setImportantForAutofill(2);
        }
        try {
            if (mScrollYField == null) {
                Field declaredField = View.class.getDeclaredField("mScrollY");
                mScrollYField = declaredField;
                declaredField.setAccessible(true);
            }
        } catch (Throwable th) {
        }
        try {
            if (editorClass == null) {
                Field declaredField2 = TextView.class.getDeclaredField("mEditor");
                mEditor = declaredField2;
                declaredField2.setAccessible(true);
                Class<?> cls = Class.forName("android.widget.Editor");
                editorClass = cls;
                Field declaredField3 = cls.getDeclaredField("mShowCursor");
                mShowCursorField = declaredField3;
                declaredField3.setAccessible(true);
                Method declaredMethod = TextView.class.getDeclaredMethod("getVerticalOffset", new Class[]{Boolean.TYPE});
                getVerticalOffsetMethod = declaredMethod;
                declaredMethod.setAccessible(true);
                Field declaredField4 = editorClass.getDeclaredField("mShowCursor");
                mShowCursorField = declaredField4;
                declaredField4.setAccessible(true);
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
        try {
            this.gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{-11230757, -11230757});
            if (Build.VERSION.SDK_INT >= 29) {
                setTextCursorDrawable(this.gradientDrawable);
            }
            this.editor = mEditor.get(this);
        } catch (Throwable th2) {
        }
        try {
            if (mCursorDrawableResField == null) {
                Field declaredField5 = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableResField = declaredField5;
                declaredField5.setAccessible(true);
            }
            if (mCursorDrawableResField != null) {
                mCursorDrawableResField.set(this, Integer.valueOf(R.drawable.field_carret_empty));
            }
        } catch (Throwable th3) {
        }
        this.cursorSize = AndroidUtilities.dp(24.0f);
    }

    public void fixHandleView(boolean reset) {
        if (reset) {
            this.fixed = false;
        } else if (!this.fixed) {
            try {
                if (editorClass == null) {
                    editorClass = Class.forName("android.widget.Editor");
                    Field declaredField = TextView.class.getDeclaredField("mEditor");
                    mEditor = declaredField;
                    declaredField.setAccessible(true);
                    this.editor = mEditor.get(this);
                }
                if (this.listenerFixer == null) {
                    Method initDrawablesMethod = editorClass.getDeclaredMethod("getPositionListener", new Class[0]);
                    initDrawablesMethod.setAccessible(true);
                    this.listenerFixer = (ViewTreeObserver.OnPreDrawListener) initDrawablesMethod.invoke(this.editor, new Object[0]);
                }
                ViewTreeObserver.OnPreDrawListener onPreDrawListener = this.listenerFixer;
                onPreDrawListener.getClass();
                AndroidUtilities.runOnUIThread(new Runnable(onPreDrawListener) {
                    private final /* synthetic */ ViewTreeObserver.OnPreDrawListener f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.onPreDraw();
                    }
                }, 500);
            } catch (Throwable th) {
            }
            this.fixed = true;
        }
    }

    public void setTransformHintToHeader(boolean value) {
        if (this.transformHintToHeader != value) {
            this.transformHintToHeader = value;
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
        }
    }

    public void setAllowDrawCursor(boolean value) {
        this.allowDrawCursor = value;
        invalidate();
    }

    public void setCursorWidth(float width) {
        this.cursorWidth = width;
    }

    public void setCursorColor(int color) {
        this.gradientDrawable.setColor(color);
        invalidate();
    }

    public void setCursorSize(int value) {
        this.cursorSize = value;
    }

    public void setErrorLineColor(int error) {
        this.errorLineColor = error;
        this.errorPaint.setColor(error);
        invalidate();
    }

    public void setLineColors(int color, int active, int error) {
        this.lineColor = color;
        this.activeLineColor = active;
        this.errorLineColor = error;
        this.errorPaint.setColor(error);
        invalidate();
    }

    public void setHintVisible(boolean value) {
        if (this.hintVisible != value) {
            this.lastUpdateTime = System.currentTimeMillis();
            this.hintVisible = value;
            invalidate();
        }
    }

    public void setHintColor(int value) {
        this.hintColor = value;
        invalidate();
    }

    public void setHeaderHintColor(int value) {
        this.headerHintColor = value;
        invalidate();
    }

    public void setNextSetTextAnimated(boolean value) {
        this.nextSetTextAnimated = value;
    }

    public void setErrorText(CharSequence text) {
        if (!TextUtils.equals(text, this.errorText)) {
            this.errorText = text;
            requestLayout();
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        try {
            super.onWindowFocusChanged(hasWindowFocus);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    public boolean hasErrorText() {
        return !TextUtils.isEmpty(this.errorText);
    }

    public StaticLayout getErrorLayout(int width) {
        if (TextUtils.isEmpty(this.errorText)) {
            return null;
        }
        return new StaticLayout(this.errorText, this.errorPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public float getLineY() {
        return this.lineY;
    }

    public void setSupportRtlHint(boolean value) {
        this.supportRtlHint = value;
    }

    public void setText(CharSequence text, TextView.BufferType type) {
        super.setText(text, type);
        checkHeaderVisibility(this.nextSetTextAnimated);
        this.nextSetTextAnimated = false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.hintLayout != null) {
            this.lineY = (((float) (getMeasuredHeight() - this.hintLayout.getHeight())) / 2.0f) + ((float) this.hintLayout.getHeight()) + ((float) AndroidUtilities.dp(6.0f));
        }
    }

    public void setHintText(CharSequence text) {
        if (text == null) {
            text = "";
        }
        this.hintLayout = new StaticLayout(text, getPaint(), AndroidUtilities.dp(1000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
    }

    public Layout getHintLayoutEx() {
        return this.hintLayout;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        checkHeaderVisibility(true);
    }

    private void checkHeaderVisibility(boolean animated) {
        boolean newHintHeader = this.transformHintToHeader && (isFocused() || getText().length() > 0);
        if (this.currentDrawHintAsHeader != newHintHeader) {
            AnimatorSet animatorSet = this.headerTransformAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.headerTransformAnimation = null;
            }
            this.currentDrawHintAsHeader = newHintHeader;
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.headerTransformAnimation = animatorSet2;
                Animator[] animatorArr = new Animator[1];
                float[] fArr = new float[1];
                if (!newHintHeader) {
                    f = 0.0f;
                }
                fArr[0] = f;
                animatorArr[0] = ObjectAnimator.ofFloat(this, "headerAnimationProgress", fArr);
                animatorSet2.playTogether(animatorArr);
                this.headerTransformAnimation.setDuration(200);
                this.headerTransformAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.headerTransformAnimation.start();
            } else {
                if (!newHintHeader) {
                    f = 0.0f;
                }
                this.headerAnimationProgress = f;
            }
            invalidate();
        }
    }

    public void setHeaderAnimationProgress(float value) {
        this.headerAnimationProgress = value;
        invalidate();
    }

    public float getHeaderAnimationProgress() {
        return this.headerAnimationProgress;
    }

    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        this.lineSpacingExtra = add;
    }

    public int getExtendedPaddingTop() {
        int i = this.ignoreTopCount;
        if (i == 0) {
            return super.getExtendedPaddingTop();
        }
        this.ignoreTopCount = i - 1;
        return 0;
    }

    public int getExtendedPaddingBottom() {
        int i = this.ignoreBottomCount;
        if (i == 0) {
            return super.getExtendedPaddingBottom();
        }
        this.ignoreBottomCount = i - 1;
        int i2 = this.scrollY;
        if (i2 != Integer.MAX_VALUE) {
            return -i2;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0303  */
    /* JADX WARNING: Removed duplicated region for block: B:110:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0233 A[Catch:{ all -> 0x02fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0235 A[Catch:{ all -> 0x02fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0238 A[Catch:{ all -> 0x02fa }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r26) {
        /*
            r25 = this;
            r1 = r25
            r8 = r26
            int r9 = r25.getExtendedPaddingTop()
            r2 = 2147483647(0x7fffffff, float:NaN)
            r1.scrollY = r2
            r3 = 0
            java.lang.reflect.Field r0 = mScrollYField     // Catch:{ Exception -> 0x0020 }
            int r0 = r0.getInt(r1)     // Catch:{ Exception -> 0x0020 }
            r1.scrollY = r0     // Catch:{ Exception -> 0x0020 }
            java.lang.reflect.Field r0 = mScrollYField     // Catch:{ Exception -> 0x0020 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0020 }
            r0.set(r1, r4)     // Catch:{ Exception -> 0x0020 }
            goto L_0x0021
        L_0x0020:
            r0 = move-exception
        L_0x0021:
            r4 = 1
            r1.ignoreTopCount = r4
            r1.ignoreBottomCount = r4
            r26.save()
            float r0 = (float) r9
            r5 = 0
            r8.translate(r5, r0)
            super.onDraw(r26)     // Catch:{ Exception -> 0x0032 }
            goto L_0x0033
        L_0x0032:
            r0 = move-exception
        L_0x0033:
            int r0 = r1.scrollY
            if (r0 == r2) goto L_0x0042
            java.lang.reflect.Field r2 = mScrollYField     // Catch:{ Exception -> 0x0041 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0041 }
            r2.set(r1, r0)     // Catch:{ Exception -> 0x0041 }
            goto L_0x0042
        L_0x0041:
            r0 = move-exception
        L_0x0042:
            r26.restore()
            int r0 = r25.length()
            r2 = 1065353216(0x3f800000, float:1.0)
            if (r0 == 0) goto L_0x0056
            boolean r0 = r1.transformHintToHeader
            if (r0 == 0) goto L_0x0052
            goto L_0x0056
        L_0x0052:
            r18 = r9
            goto L_0x020f
        L_0x0056:
            android.text.StaticLayout r0 = r1.hintLayout
            if (r0 == 0) goto L_0x020d
            boolean r0 = r1.hintVisible
            if (r0 != 0) goto L_0x0064
            float r0 = r1.hintAlpha
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0052
        L_0x0064:
            boolean r0 = r1.hintVisible
            if (r0 == 0) goto L_0x006e
            float r0 = r1.hintAlpha
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0078
        L_0x006e:
            boolean r0 = r1.hintVisible
            if (r0 != 0) goto L_0x00b4
            float r0 = r1.hintAlpha
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x00b4
        L_0x0078:
            long r6 = java.lang.System.currentTimeMillis()
            long r10 = r1.lastUpdateTime
            long r10 = r6 - r10
            r12 = 0
            int r0 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r0 < 0) goto L_0x008c
            r12 = 17
            int r0 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x008e
        L_0x008c:
            r10 = 17
        L_0x008e:
            r1.lastUpdateTime = r6
            boolean r0 = r1.hintVisible
            r12 = 1125515264(0x43160000, float:150.0)
            if (r0 == 0) goto L_0x00a4
            float r0 = r1.hintAlpha
            float r13 = (float) r10
            float r13 = r13 / r12
            float r0 = r0 + r13
            r1.hintAlpha = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x00b1
            r1.hintAlpha = r2
            goto L_0x00b1
        L_0x00a4:
            float r0 = r1.hintAlpha
            float r13 = (float) r10
            float r13 = r13 / r12
            float r0 = r0 - r13
            r1.hintAlpha = r0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 >= 0) goto L_0x00b1
            r1.hintAlpha = r5
        L_0x00b1:
            r25.invalidate()
        L_0x00b4:
            android.text.TextPaint r0 = r25.getPaint()
            int r0 = r0.getColor()
            r26.save()
            r6 = 0
            android.text.StaticLayout r7 = r1.hintLayout
            float r7 = r7.getLineLeft(r3)
            android.text.StaticLayout r10 = r1.hintLayout
            float r10 = r10.getLineWidth(r3)
            int r11 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r11 == 0) goto L_0x00d3
            float r11 = (float) r6
            float r11 = r11 - r7
            int r6 = (int) r11
        L_0x00d3:
            boolean r11 = r1.supportRtlHint
            r12 = 1086324736(0x40c00000, float:6.0)
            if (r11 == 0) goto L_0x0104
            boolean r11 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x0104
            int r11 = r25.getMeasuredWidth()
            float r11 = (float) r11
            float r11 = r11 - r10
            int r13 = r25.getScrollX()
            int r13 = r13 + r6
            float r13 = (float) r13
            float r13 = r13 + r11
            int r14 = r25.getPaddingRight()
            float r14 = (float) r14
            float r13 = r13 + r14
            float r14 = r1.lineY
            android.text.StaticLayout r15 = r1.hintLayout
            int r15 = r15.getHeight()
            float r15 = (float) r15
            float r14 = r14 - r15
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r14 = r14 - r12
            r8.translate(r13, r14)
            goto L_0x0122
        L_0x0104:
            int r11 = r25.getScrollX()
            int r11 = r11 + r6
            int r13 = r25.getPaddingLeft()
            int r11 = r11 + r13
            float r11 = (float) r11
            float r13 = r1.lineY
            android.text.StaticLayout r14 = r1.hintLayout
            int r14 = r14.getHeight()
            float r14 = (float) r14
            float r13 = r13 - r14
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            float r13 = r13 - r12
            r8.translate(r11, r13)
        L_0x0122:
            boolean r11 = r1.transformHintToHeader
            if (r11 == 0) goto L_0x01d4
            r11 = 1050253722(0x3e99999a, float:0.3)
            float r12 = r1.headerAnimationProgress
            float r12 = r12 * r11
            float r11 = r2 - r12
            r12 = 1102053376(0x41b00000, float:22.0)
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)
            int r12 = -r12
            float r12 = (float) r12
            float r13 = r1.headerAnimationProgress
            float r12 = r12 * r13
            int r13 = r1.headerHintColor
            int r13 = android.graphics.Color.red(r13)
            int r14 = r1.headerHintColor
            int r14 = android.graphics.Color.green(r14)
            int r15 = r1.headerHintColor
            int r15 = android.graphics.Color.blue(r15)
            int r3 = r1.headerHintColor
            int r3 = android.graphics.Color.alpha(r3)
            int r4 = r1.hintColor
            int r4 = android.graphics.Color.red(r4)
            int r2 = r1.hintColor
            int r2 = android.graphics.Color.green(r2)
            int r5 = r1.hintColor
            int r5 = android.graphics.Color.blue(r5)
            r17 = r6
            int r6 = r1.hintColor
            int r6 = android.graphics.Color.alpha(r6)
            r18 = r9
            boolean r9 = r1.supportRtlHint
            if (r9 == 0) goto L_0x0186
            boolean r9 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0186
            float r9 = r10 + r7
            float r19 = r10 + r7
            float r19 = r19 * r11
            float r9 = r9 - r19
            r19 = r10
            r10 = 0
            r8.translate(r9, r10)
            goto L_0x0196
        L_0x0186:
            r19 = r10
            r10 = 0
            int r9 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x0196
            r9 = 1065353216(0x3f800000, float:1.0)
            float r16 = r9 - r11
            float r9 = r7 * r16
            r8.translate(r9, r10)
        L_0x0196:
            r8.scale(r11, r11)
            r8.translate(r10, r12)
            android.text.TextPaint r9 = r25.getPaint()
            float r10 = (float) r6
            r20 = r7
            int r7 = r3 - r6
            float r7 = (float) r7
            r21 = r3
            float r3 = r1.headerAnimationProgress
            float r7 = r7 * r3
            float r10 = r10 + r7
            int r7 = (int) r10
            float r10 = (float) r4
            r22 = r6
            int r6 = r13 - r4
            float r6 = (float) r6
            float r6 = r6 * r3
            float r10 = r10 + r6
            int r6 = (int) r10
            float r10 = (float) r2
            r23 = r4
            int r4 = r14 - r2
            float r4 = (float) r4
            float r4 = r4 * r3
            float r10 = r10 + r4
            int r4 = (int) r10
            float r10 = (float) r5
            r24 = r2
            int r2 = r15 - r5
            float r2 = (float) r2
            float r2 = r2 * r3
            float r10 = r10 + r2
            int r2 = (int) r10
            int r2 = android.graphics.Color.argb(r7, r6, r4, r2)
            r9.setColor(r2)
            goto L_0x01fd
        L_0x01d4:
            r17 = r6
            r20 = r7
            r18 = r9
            r19 = r10
            android.text.TextPaint r2 = r25.getPaint()
            int r3 = r1.hintColor
            r2.setColor(r3)
            android.text.TextPaint r2 = r25.getPaint()
            float r3 = r1.hintAlpha
            r4 = 1132396544(0x437f0000, float:255.0)
            float r3 = r3 * r4
            int r5 = r1.hintColor
            int r5 = android.graphics.Color.alpha(r5)
            float r5 = (float) r5
            float r5 = r5 / r4
            float r3 = r3 * r5
            int r3 = (int) r3
            r2.setAlpha(r3)
        L_0x01fd:
            android.text.StaticLayout r2 = r1.hintLayout
            r2.draw(r8)
            android.text.TextPaint r2 = r25.getPaint()
            r2.setColor(r0)
            r26.restore()
            goto L_0x020f
        L_0x020d:
            r18 = r9
        L_0x020f:
            boolean r0 = r1.allowDrawCursor     // Catch:{ all -> 0x02fa }
            if (r0 == 0) goto L_0x02f9
            java.lang.reflect.Field r0 = mShowCursorField     // Catch:{ all -> 0x02fa }
            if (r0 == 0) goto L_0x02f9
            java.lang.reflect.Field r0 = mShowCursorField     // Catch:{ all -> 0x02fa }
            java.lang.Object r2 = r1.editor     // Catch:{ all -> 0x02fa }
            long r2 = r0.getLong(r2)     // Catch:{ all -> 0x02fa }
            long r4 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x02fa }
            long r4 = r4 - r2
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 % r6
            r6 = 500(0x1f4, double:2.47E-321)
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 >= 0) goto L_0x0235
            boolean r0 = r25.isFocused()     // Catch:{ all -> 0x02fa }
            if (r0 == 0) goto L_0x0235
            r0 = 1
            goto L_0x0236
        L_0x0235:
            r0 = 0
        L_0x0236:
            if (r0 == 0) goto L_0x02f9
            r26.save()     // Catch:{ all -> 0x02fa }
            r4 = 0
            java.lang.reflect.Method r5 = getVerticalOffsetMethod     // Catch:{ all -> 0x02fa }
            r6 = 48
            if (r5 == 0) goto L_0x0262
            int r5 = r25.getGravity()     // Catch:{ all -> 0x02fa }
            r5 = r5 & 112(0x70, float:1.57E-43)
            if (r5 == r6) goto L_0x0274
            java.lang.reflect.Method r5 = getVerticalOffsetMethod     // Catch:{ all -> 0x02fa }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x02fa }
            java.lang.Boolean r6 = java.lang.Boolean.valueOf(r6)     // Catch:{ all -> 0x02fa }
            r9 = 0
            r7[r9] = r6     // Catch:{ all -> 0x02fa }
            java.lang.Object r5 = r5.invoke(r1, r7)     // Catch:{ all -> 0x02fa }
            java.lang.Integer r5 = (java.lang.Integer) r5     // Catch:{ all -> 0x02fa }
            int r5 = r5.intValue()     // Catch:{ all -> 0x02fa }
            r4 = r5
            goto L_0x0274
        L_0x0262:
            int r5 = r25.getGravity()     // Catch:{ all -> 0x02fa }
            r5 = r5 & 112(0x70, float:1.57E-43)
            if (r5 == r6) goto L_0x0274
            int r5 = r25.getTotalPaddingTop()     // Catch:{ all -> 0x02fa }
            int r6 = r25.getExtendedPaddingTop()     // Catch:{ all -> 0x02fa }
            int r4 = r5 - r6
        L_0x0274:
            int r5 = r25.getPaddingLeft()     // Catch:{ all -> 0x02fa }
            float r5 = (float) r5     // Catch:{ all -> 0x02fa }
            int r6 = r25.getExtendedPaddingTop()     // Catch:{ all -> 0x02fa }
            int r6 = r6 + r4
            float r6 = (float) r6     // Catch:{ all -> 0x02fa }
            r8.translate(r5, r6)     // Catch:{ all -> 0x02fa }
            android.text.Layout r5 = r25.getLayout()     // Catch:{ all -> 0x02fa }
            int r6 = r25.getSelectionStart()     // Catch:{ all -> 0x02fa }
            int r6 = r5.getLineForOffset(r6)     // Catch:{ all -> 0x02fa }
            int r7 = r5.getLineCount()     // Catch:{ all -> 0x02fa }
            r25.updateCursorPosition()     // Catch:{ all -> 0x02fa }
            android.graphics.drawable.GradientDrawable r9 = r1.gradientDrawable     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r9 = r9.getBounds()     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r9.left     // Catch:{ all -> 0x02fa }
            r10.left = r11     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r9.left     // Catch:{ all -> 0x02fa }
            float r12 = r1.cursorWidth     // Catch:{ all -> 0x02fa }
            int r12 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x02fa }
            int r11 = r11 + r12
            r10.right = r11     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r9.bottom     // Catch:{ all -> 0x02fa }
            r10.bottom = r11     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r9.top     // Catch:{ all -> 0x02fa }
            r10.top = r11     // Catch:{ all -> 0x02fa }
            float r10 = r1.lineSpacingExtra     // Catch:{ all -> 0x02fa }
            r11 = 0
            int r10 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1))
            if (r10 == 0) goto L_0x02d0
            int r10 = r7 + -1
            if (r6 >= r10) goto L_0x02d0
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r10.bottom     // Catch:{ all -> 0x02fa }
            float r11 = (float) r11     // Catch:{ all -> 0x02fa }
            float r12 = r1.lineSpacingExtra     // Catch:{ all -> 0x02fa }
            float r11 = r11 - r12
            int r11 = (int) r11     // Catch:{ all -> 0x02fa }
            r10.bottom = r11     // Catch:{ all -> 0x02fa }
        L_0x02d0:
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r11 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r11.centerY()     // Catch:{ all -> 0x02fa }
            int r12 = r1.cursorSize     // Catch:{ all -> 0x02fa }
            int r12 = r12 / 2
            int r11 = r11 - r12
            r10.top = r11     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r10 = r1.rect     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r11 = r1.rect     // Catch:{ all -> 0x02fa }
            int r11 = r11.top     // Catch:{ all -> 0x02fa }
            int r12 = r1.cursorSize     // Catch:{ all -> 0x02fa }
            int r11 = r11 + r12
            r10.bottom = r11     // Catch:{ all -> 0x02fa }
            android.graphics.drawable.GradientDrawable r10 = r1.gradientDrawable     // Catch:{ all -> 0x02fa }
            android.graphics.Rect r11 = r1.rect     // Catch:{ all -> 0x02fa }
            r10.setBounds(r11)     // Catch:{ all -> 0x02fa }
            android.graphics.drawable.GradientDrawable r10 = r1.gradientDrawable     // Catch:{ all -> 0x02fa }
            r10.draw(r8)     // Catch:{ all -> 0x02fa }
            r26.restore()     // Catch:{ all -> 0x02fa }
        L_0x02f9:
            goto L_0x02fb
        L_0x02fa:
            r0 = move-exception
        L_0x02fb:
            int r0 = r1.lineColor
            if (r0 == 0) goto L_0x0356
            android.text.StaticLayout r0 = r1.hintLayout
            if (r0 == 0) goto L_0x0356
            java.lang.CharSequence r0 = r1.errorText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r2 = 1073741824(0x40000000, float:2.0)
            if (r0 != 0) goto L_0x0319
            android.graphics.Paint r0 = r1.linePaint
            int r3 = r1.errorLineColor
            r0.setColor(r3)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            goto L_0x0338
        L_0x0319:
            boolean r0 = r25.isFocused()
            if (r0 == 0) goto L_0x032b
            android.graphics.Paint r0 = r1.linePaint
            int r3 = r1.activeLineColor
            r0.setColor(r3)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
            goto L_0x0338
        L_0x032b:
            android.graphics.Paint r0 = r1.linePaint
            int r2 = r1.lineColor
            r0.setColor(r2)
            r2 = 1065353216(0x3f800000, float:1.0)
            int r0 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r2)
        L_0x0338:
            int r2 = r25.getScrollX()
            float r3 = (float) r2
            float r2 = r1.lineY
            int r2 = (int) r2
            float r4 = (float) r2
            int r2 = r25.getScrollX()
            int r5 = r25.getMeasuredWidth()
            int r2 = r2 + r5
            float r5 = (float) r2
            float r2 = r1.lineY
            float r6 = (float) r0
            float r6 = r6 + r2
            android.graphics.Paint r7 = r1.linePaint
            r2 = r26
            r2.drawRect(r3, r4, r5, r6, r7)
        L_0x0356:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.EditTextBoldCursor.onDraw(android.graphics.Canvas):void");
    }

    public void setWindowView(View view) {
        this.windowView = view;
    }

    private boolean updateCursorPosition() {
        Layout layout = getLayout();
        int offset = getSelectionStart();
        int line = layout.getLineForOffset(offset);
        updateCursorPosition(layout.getLineTop(line), layout.getLineTop(line + 1), layout.getPrimaryHorizontal(offset));
        return true;
    }

    private int clampHorizontalPosition(Drawable drawable, float horizontal) {
        float horizontal2 = Math.max(0.5f, horizontal - 0.5f);
        if (this.mTempRect == null) {
            this.mTempRect = new Rect();
        }
        int drawableWidth = 0;
        if (drawable != null) {
            drawable.getPadding(this.mTempRect);
            drawableWidth = drawable.getIntrinsicWidth();
        } else {
            this.mTempRect.setEmpty();
        }
        int scrollX = getScrollX();
        float horizontalDiff = horizontal2 - ((float) scrollX);
        int viewClippedWidth = (getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (horizontalDiff >= ((float) viewClippedWidth) - 1.0f) {
            return (viewClippedWidth + scrollX) - (drawableWidth - this.mTempRect.right);
        }
        if (Math.abs(horizontalDiff) <= 1.0f || (TextUtils.isEmpty(getText()) && ((float) (1048576 - scrollX)) <= ((float) viewClippedWidth) + 1.0f && horizontal2 <= 1.0f)) {
            return scrollX - this.mTempRect.left;
        }
        return ((int) horizontal2) - this.mTempRect.left;
    }

    private void updateCursorPosition(int top, int bottom, float horizontal) {
        int left = clampHorizontalPosition(this.gradientDrawable, horizontal);
        this.gradientDrawable.setBounds(left, top - this.mTempRect.top, left + AndroidUtilities.dp(this.cursorWidth), this.mTempRect.bottom + bottom);
    }

    public float getLineSpacingExtra() {
        return super.getLineSpacingExtra();
    }

    /* access modifiers changed from: private */
    public void cleanupFloatingActionModeViews() {
        FloatingToolbar floatingToolbar2 = this.floatingToolbar;
        if (floatingToolbar2 != null) {
            floatingToolbar2.dismiss();
            this.floatingToolbar = null;
        }
        if (this.floatingToolbarPreDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(this.floatingToolbarPreDrawListener);
            this.floatingToolbarPreDrawListener = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = getRootView();
        AndroidUtilities.runOnUIThread(this.invalidateRunnable);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = null;
        AndroidUtilities.cancelRunOnUIThread(this.invalidateRunnable);
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        if (Build.VERSION.SDK_INT < 23 || (this.windowView == null && this.attachedToWindow == null)) {
            return super.startActionMode(callback);
        }
        FloatingActionMode floatingActionMode2 = this.floatingActionMode;
        if (floatingActionMode2 != null) {
            floatingActionMode2.finish();
        }
        cleanupFloatingActionModeViews();
        Context context = getContext();
        View view = this.windowView;
        if (view == null) {
            view = this.attachedToWindow;
        }
        this.floatingToolbar = new FloatingToolbar(context, view, getActionModeStyle());
        this.floatingActionMode = new FloatingActionMode(getContext(), new ActionModeCallback2Wrapper(callback), this, this.floatingToolbar);
        this.floatingToolbarPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public final boolean onPreDraw() {
                return EditTextBoldCursor.this.lambda$startActionMode$0$EditTextBoldCursor();
            }
        };
        FloatingActionMode floatingActionMode3 = this.floatingActionMode;
        callback.onCreateActionMode(floatingActionMode3, floatingActionMode3.getMenu());
        FloatingActionMode floatingActionMode4 = this.floatingActionMode;
        extendActionMode(floatingActionMode4, floatingActionMode4.getMenu());
        this.floatingActionMode.invalidate();
        getViewTreeObserver().addOnPreDrawListener(this.floatingToolbarPreDrawListener);
        invalidate();
        return this.floatingActionMode;
    }

    public /* synthetic */ boolean lambda$startActionMode$0$EditTextBoldCursor() {
        FloatingActionMode floatingActionMode2 = this.floatingActionMode;
        if (floatingActionMode2 == null) {
            return true;
        }
        floatingActionMode2.updateViewLocationInWindow();
        return true;
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        if (Build.VERSION.SDK_INT < 23 || (this.windowView == null && this.attachedToWindow == null)) {
            return super.startActionMode(callback, type);
        }
        return startActionMode(callback);
    }

    /* access modifiers changed from: protected */
    public void extendActionMode(ActionMode actionMode, Menu menu) {
    }

    /* access modifiers changed from: protected */
    public int getActionModeStyle() {
        return 1;
    }

    public void setSelection(int start, int stop) {
        try {
            super.setSelection(start, stop);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void setSelection(int index) {
        try {
            super.setSelection(index);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.EditText");
        StaticLayout staticLayout = this.hintLayout;
        if (staticLayout != null) {
            info.setContentDescription(staticLayout.getText());
        }
    }
}
