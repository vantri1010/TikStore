package com.blankj.utilcode.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.ViewCompat;

public class ClickUtils {
    private static final long DEBOUNCING_DEFAULT_VALUE = 200;
    private static final int DEBOUNCING_TAG = -7;
    private static final float PRESSED_BG_ALPHA_DEFAULT_VALUE = 0.9f;
    private static final int PRESSED_BG_ALPHA_STYLE = 4;
    private static final float PRESSED_BG_DARK_DEFAULT_VALUE = 0.9f;
    private static final int PRESSED_BG_DARK_STYLE = 5;
    private static final float PRESSED_VIEW_ALPHA_DEFAULT_VALUE = 0.8f;
    private static final int PRESSED_VIEW_ALPHA_SRC_TAG = -3;
    private static final int PRESSED_VIEW_ALPHA_TAG = -2;
    private static final float PRESSED_VIEW_SCALE_DEFAULT_VALUE = -0.06f;
    private static final int PRESSED_VIEW_SCALE_TAG = -1;

    private ClickUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void applyPressedViewScale(View... views) {
        applyPressedViewScale(views, (float[]) null);
    }

    public static void applyPressedViewScale(View[] views, float[] scaleFactors) {
        if (views != null && views.length != 0) {
            for (int i = 0; i < views.length; i++) {
                if (scaleFactors == null || i >= scaleFactors.length) {
                    applyPressedViewScale(views[i], (float) PRESSED_VIEW_SCALE_DEFAULT_VALUE);
                } else {
                    applyPressedViewScale(views[i], scaleFactors[i]);
                }
            }
        }
    }

    public static void applyPressedViewScale(View view, float scaleFactor) {
        if (view != null) {
            view.setTag(-1, Float.valueOf(scaleFactor));
            view.setClickable(true);
            view.setOnTouchListener(OnUtilsTouchListener.getInstance());
        }
    }

    public static void applyPressedViewAlpha(View... views) {
        applyPressedViewAlpha(views, (float[]) null);
    }

    public static void applyPressedViewAlpha(View[] views, float[] alphas) {
        if (views != null && views.length != 0) {
            for (int i = 0; i < views.length; i++) {
                if (alphas == null || i >= alphas.length) {
                    applyPressedViewAlpha(views[i], (float) PRESSED_VIEW_ALPHA_DEFAULT_VALUE);
                } else {
                    applyPressedViewAlpha(views[i], alphas[i]);
                }
            }
        }
    }

    public static void applyPressedViewAlpha(View view, float alpha) {
        if (view != null) {
            view.setTag(-2, Float.valueOf(alpha));
            view.setTag(-3, Float.valueOf(view.getAlpha()));
            view.setClickable(true);
            view.setOnTouchListener(OnUtilsTouchListener.getInstance());
        }
    }

    public static void applyPressedBgAlpha(View view) {
        applyPressedBgAlpha(view, 0.9f);
    }

    public static void applyPressedBgAlpha(View view, float alpha) {
        applyPressedBgStyle(view, 4, alpha);
    }

    public static void applyPressedBgDark(View view) {
        applyPressedBgDark(view, 0.9f);
    }

    public static void applyPressedBgDark(View view, float darkAlpha) {
        applyPressedBgStyle(view, 5, darkAlpha);
    }

    private static void applyPressedBgStyle(View view, int style, float value) {
        if (view != null) {
            Drawable background = view.getBackground();
            Object tag = view.getTag(-style);
            if (tag instanceof Drawable) {
                ViewCompat.setBackground(view, (Drawable) tag);
                return;
            }
            Drawable background2 = createStyleDrawable(background, style, value);
            ViewCompat.setBackground(view, background2);
            view.setTag(-style, background2);
        }
    }

    private static Drawable createStyleDrawable(Drawable src, int style, float value) {
        if (src == null) {
            src = new ColorDrawable(0);
        }
        if (src.getConstantState() == null) {
            return src;
        }
        Drawable pressed = src.getConstantState().newDrawable().mutate();
        if (style == 4) {
            pressed = createAlphaDrawable(pressed, value);
        } else if (style == 5) {
            pressed = createDarkDrawable(pressed, value);
        }
        Drawable mutate = src.getConstantState().newDrawable().mutate();
        Drawable disable = createAlphaDrawable(pressed, 0.5f);
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{16842919}, pressed);
        drawable.addState(new int[]{-16842910}, disable);
        drawable.addState(StateSet.WILD_CARD, src);
        return drawable;
    }

    private static Drawable createAlphaDrawable(Drawable drawable, float alpha) {
        if (Build.VERSION.SDK_INT > 19 || (drawable instanceof ColorDrawable)) {
            drawable.setAlpha((int) (255.0f * alpha));
            return drawable;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(bitmap);
        drawable.setAlpha((int) (255.0f * alpha));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(myCanvas);
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    private static Drawable createDarkDrawable(Drawable drawable, float alpha) {
        if (Build.VERSION.SDK_INT > 19 || (drawable instanceof ColorDrawable)) {
            drawable.setColorFilter(getDarkColorFilter(alpha));
            return drawable;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(bitmap);
        drawable.setColorFilter(getDarkColorFilter(alpha));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(myCanvas);
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    private static ColorMatrixColorFilter getDarkColorFilter(float darkAlpha) {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, darkAlpha, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 0.0f}));
    }

    public static void applySingleDebouncing(View view, View.OnClickListener listener) {
        applySingleDebouncing(new View[]{view}, listener);
    }

    public static void applySingleDebouncing(View view, long duration, View.OnClickListener listener) {
        applySingleDebouncing(new View[]{view}, duration, listener);
    }

    public static void applySingleDebouncing(View[] views, View.OnClickListener listener) {
        applySingleDebouncing(views, (long) DEBOUNCING_DEFAULT_VALUE, listener);
    }

    public static void applySingleDebouncing(View[] views, long duration, View.OnClickListener listener) {
        applyDebouncing(views, false, duration, listener);
    }

    public static void applyGlobalDebouncing(View view, View.OnClickListener listener) {
        applyGlobalDebouncing(new View[]{view}, listener);
    }

    public static void applyGlobalDebouncing(View view, long duration, View.OnClickListener listener) {
        applyGlobalDebouncing(new View[]{view}, duration, listener);
    }

    public static void applyGlobalDebouncing(View[] views, View.OnClickListener listener) {
        applyGlobalDebouncing(views, (long) DEBOUNCING_DEFAULT_VALUE, listener);
    }

    public static void applyGlobalDebouncing(View[] views, long duration, View.OnClickListener listener) {
        applyDebouncing(views, true, duration, listener);
    }

    private static void applyDebouncing(View[] views, boolean isGlobal, long duration, final View.OnClickListener listener) {
        if (views != null && views.length != 0 && listener != null) {
            for (View view : views) {
                if (view != null) {
                    view.setOnClickListener(new OnDebouncingClickListener(isGlobal, duration) {
                        public void onDebouncingClick(View v) {
                            listener.onClick(v);
                        }
                    });
                }
            }
        }
    }

    private static int dp2px(float dpValue) {
        return (int) ((dpValue * Resources.getSystem().getDisplayMetrics().density) + 0.5f);
    }

    public static abstract class OnDebouncingClickListener implements View.OnClickListener {
        private static final Runnable ENABLE_AGAIN = new Runnable() {
            public void run() {
                boolean unused = OnDebouncingClickListener.mEnabled = true;
            }
        };
        /* access modifiers changed from: private */
        public static boolean mEnabled = true;
        private long mDuration;
        private boolean mIsGlobal;

        public abstract void onDebouncingClick(View view);

        private static boolean isValid(View view, long duration) {
            if (view != null) {
                long curTime = System.currentTimeMillis();
                Object tag = view.getTag(-7);
                if (!(tag instanceof Long)) {
                    view.setTag(-7, Long.valueOf(curTime));
                    return true;
                } else if (curTime - ((Long) tag).longValue() <= duration) {
                    return false;
                } else {
                    view.setTag(-7, Long.valueOf(curTime));
                    return true;
                }
            } else {
                throw new NullPointerException("Argument 'view' of type View (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
            }
        }

        public OnDebouncingClickListener() {
            this(true, ClickUtils.DEBOUNCING_DEFAULT_VALUE);
        }

        public OnDebouncingClickListener(boolean isGlobal) {
            this(isGlobal, ClickUtils.DEBOUNCING_DEFAULT_VALUE);
        }

        public OnDebouncingClickListener(long duration) {
            this(true, duration);
        }

        public OnDebouncingClickListener(boolean isGlobal, long duration) {
            this.mIsGlobal = isGlobal;
            this.mDuration = duration;
        }

        public final void onClick(View v) {
            if (this.mIsGlobal) {
                if (mEnabled) {
                    mEnabled = false;
                    v.postDelayed(ENABLE_AGAIN, this.mDuration);
                    onDebouncingClick(v);
                }
            } else if (isValid(v, this.mDuration)) {
                onDebouncingClick(v);
            }
        }
    }

    public static abstract class OnMultiClickListener implements View.OnClickListener {
        private static final long INTERVAL_DEFAULT_VALUE = 666;
        private int mClickCount;
        private final long mClickInterval;
        private long mLastClickTime;
        private final int mTriggerClickCount;

        public abstract void onBeforeTriggerClick(View view, int i);

        public abstract void onTriggerClick(View view);

        public OnMultiClickListener(int triggerClickCount) {
            this(triggerClickCount, INTERVAL_DEFAULT_VALUE);
        }

        public OnMultiClickListener(int triggerClickCount, long clickInterval) {
            this.mTriggerClickCount = triggerClickCount;
            this.mClickInterval = clickInterval;
        }

        public void onClick(View v) {
            if (this.mTriggerClickCount <= 1) {
                onTriggerClick(v);
                return;
            }
            long curTime = System.currentTimeMillis();
            if (curTime - this.mLastClickTime < this.mClickInterval) {
                int i = this.mClickCount + 1;
                this.mClickCount = i;
                int i2 = this.mTriggerClickCount;
                if (i == i2) {
                    onTriggerClick(v);
                } else if (i < i2) {
                    onBeforeTriggerClick(v, i);
                } else {
                    this.mClickCount = 1;
                    onBeforeTriggerClick(v, 1);
                }
            } else {
                this.mClickCount = 1;
                onBeforeTriggerClick(v, 1);
            }
            this.mLastClickTime = curTime;
        }
    }

    private static class OnUtilsTouchListener implements View.OnTouchListener {
        public static OnUtilsTouchListener getInstance() {
            return LazyHolder.INSTANCE;
        }

        private OnUtilsTouchListener() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == 0) {
                processScale(v, true);
                processAlpha(v, true);
            } else if (action == 1 || action == 3) {
                processScale(v, false);
                processAlpha(v, false);
            }
            return false;
        }

        private void processScale(View view, boolean isDown) {
            Object tag = view.getTag(-1);
            if (tag instanceof Float) {
                float value = 1.0f;
                if (isDown) {
                    value = 1.0f + ((Float) tag).floatValue();
                }
                view.animate().scaleX(value).scaleY(value).setDuration(ClickUtils.DEBOUNCING_DEFAULT_VALUE).start();
            }
        }

        private void processAlpha(View view, boolean isDown) {
            Object tag = view.getTag(isDown ? -2 : -3);
            if (tag instanceof Float) {
                view.setAlpha(((Float) tag).floatValue());
            }
        }

        private static class LazyHolder {
            /* access modifiers changed from: private */
            public static final OnUtilsTouchListener INSTANCE = new OnUtilsTouchListener();

            private LazyHolder() {
            }
        }
    }
}
