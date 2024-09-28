package im.bclpbkiauv.ui.actionbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.components.AnimationProperties;
import im.bclpbkiauv.ui.components.CubicBezierInterpolator;
import im.bclpbkiauv.ui.components.LayoutHelper;
import java.util.ArrayList;

public class BottomSheet extends Dialog {
    /* access modifiers changed from: private */
    public boolean allowCustomAnimation = true;
    /* access modifiers changed from: private */
    public boolean allowDrawContent = true;
    /* access modifiers changed from: private */
    public boolean allowNestedScroll = true;
    protected boolean applyBottomPadding = true;
    protected boolean applyTopPadding = true;
    protected ColorDrawable backDrawable = new ColorDrawable(-16777216);
    protected int backgroundPaddingLeft;
    protected int backgroundPaddingTop;
    protected ContainerView container;
    protected ViewGroup containerView;
    protected int currentAccount = UserConfig.selectedAccount;
    protected AnimatorSet currentSheetAnimation;
    protected int currentSheetAnimationType;
    /* access modifiers changed from: private */
    public View customView;
    /* access modifiers changed from: private */
    public BottomSheetDelegateInterface delegate;
    /* access modifiers changed from: private */
    public boolean dimBehind = true;
    /* access modifiers changed from: private */
    public Runnable dismissRunnable = new Runnable() {
        public final void run() {
            BottomSheet.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public boolean dismissed;
    private boolean focusable;
    protected boolean fullWidth;
    public boolean isAnimationed;
    protected boolean isFullscreen;
    /* access modifiers changed from: private */
    public int[] itemIcons;
    private ArrayList<BottomSheetCell> itemViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public CharSequence[] items;
    /* access modifiers changed from: private */
    public WindowInsets lastInsets;
    /* access modifiers changed from: private */
    public int layoutCount;
    protected boolean mblnCanScroll = true;
    protected View nestedScrollChild;
    /* access modifiers changed from: private */
    public DialogInterface.OnClickListener onClickListener;
    protected Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    protected Drawable shadowDrawable;
    private boolean showWithoutAnimation;
    /* access modifiers changed from: private */
    public Runnable startAnimationRunnable;
    /* access modifiers changed from: private */
    public int tag;
    /* access modifiers changed from: private */
    public CharSequence title;
    private TextView titleView;
    /* access modifiers changed from: private */
    public int touchSlop;
    /* access modifiers changed from: private */
    public boolean useFastDismiss;
    /* access modifiers changed from: private */
    public boolean useHardwareLayer = true;

    public interface BottomSheetDelegateInterface {
        boolean canDismiss();

        void onOpenAnimationEnd();

        void onOpenAnimationStart();
    }

    static /* synthetic */ int access$710(BottomSheet x0) {
        int i = x0.layoutCount;
        x0.layoutCount = i - 1;
        return i;
    }

    public Runnable getDismissRunnable() {
        return this.dismissRunnable;
    }

    protected class ContainerView extends FrameLayout implements NestedScrollingParent {
        /* access modifiers changed from: private */
        public AnimatorSet currentAnimation = null;
        private boolean maybeStartTracking = false;
        private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        private boolean startedTracking = false;
        private int startedTrackingPointerId = -1;
        private int startedTrackingX;
        private int startedTrackingY;
        private VelocityTracker velocityTracker = null;

        public ContainerView(Context context) {
            super(context);
        }

        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (BottomSheet.this.nestedScrollChild == null || child == BottomSheet.this.nestedScrollChild) && !BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll && nestedScrollAxes == 2 && !BottomSheet.this.canDismissWithSwipe();
        }

        public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
            }
        }

        public void onStopNestedScroll(View target) {
            this.nestedScrollingParentHelper.onStopNestedScroll(target);
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                float translationY = BottomSheet.this.containerView.getTranslationY();
                if (BottomSheet.this.canDismissWithSwipe()) {
                    checkDismiss(0.0f, 0.0f);
                }
            }
        }

        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                if (dyUnconsumed != 0) {
                    float currentTranslation = BottomSheet.this.containerView.getTranslationY() - ((float) dyUnconsumed);
                    if (currentTranslation < 0.0f) {
                        currentTranslation = 0.0f;
                    }
                    if (BottomSheet.this.mblnCanScroll) {
                        BottomSheet.this.containerView.setTranslationY(currentTranslation);
                    }
                }
            }
        }

        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            if (!BottomSheet.this.dismissed && BottomSheet.this.allowNestedScroll) {
                cancelCurrentAnimation();
                float currentTranslation = BottomSheet.this.containerView.getTranslationY();
                if (currentTranslation > 0.0f && dy > 0) {
                    float currentTranslation2 = currentTranslation - ((float) dy);
                    consumed[1] = dy;
                    if (currentTranslation2 < 0.0f) {
                        currentTranslation2 = 0.0f;
                    }
                    if (BottomSheet.this.mblnCanScroll) {
                        BottomSheet.this.containerView.setTranslationY(currentTranslation2);
                    }
                }
            }
        }

        public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
            return false;
        }

        public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
            return false;
        }

        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        private void checkDismiss(float velX, float velY) {
            float translationY = BottomSheet.this.containerView.getTranslationY();
            if (!((translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f))) {
                boolean allowOld = BottomSheet.this.allowCustomAnimation;
                boolean unused = BottomSheet.this.allowCustomAnimation = false;
                boolean unused2 = BottomSheet.this.useFastDismiss = true;
                BottomSheet.this.dismiss();
                boolean unused3 = BottomSheet.this.allowCustomAnimation = allowOld;
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentAnimation = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[]{0.0f})});
            this.currentAnimation.setDuration((long) ((int) ((translationY / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f)));
            this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ContainerView.this.currentAnimation != null && ContainerView.this.currentAnimation.equals(animation)) {
                        AnimatorSet unused = ContainerView.this.currentAnimation = null;
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentAnimation.start();
        }

        private void cancelCurrentAnimation() {
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.currentAnimation = null;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean processTouchEvent(MotionEvent ev, boolean intercept) {
            if (BottomSheet.this.dismissed) {
                return false;
            }
            if (BottomSheet.this.onContainerTouchEvent(ev)) {
                return true;
            }
            if (BottomSheet.this.canDismissWithTouchOutside() && ev != null && ((ev.getAction() == 0 || ev.getAction() == 2) && !this.startedTracking && !this.maybeStartTracking && ev.getPointerCount() == 1)) {
                this.startedTrackingX = (int) ev.getX();
                int y = (int) ev.getY();
                this.startedTrackingY = y;
                if (y < BottomSheet.this.containerView.getTop() || this.startedTrackingX < BottomSheet.this.containerView.getLeft() || this.startedTrackingX > BottomSheet.this.containerView.getRight()) {
                    BottomSheet.this.dismiss();
                    return true;
                }
                this.startedTrackingPointerId = ev.getPointerId(0);
                this.maybeStartTracking = true;
                cancelCurrentAnimation();
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.clear();
                }
            } else if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId && BottomSheet.this.canDismissWithSwipe()) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float dx = (float) Math.abs((int) (ev.getX() - ((float) this.startedTrackingX)));
                float dy = (float) (((int) ev.getY()) - this.startedTrackingY);
                this.velocityTracker.addMovement(ev);
                if (this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= ((float) BottomSheet.this.touchSlop)) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float translationY = BottomSheet.this.containerView.getTranslationY() + dy;
                    if (translationY < 0.0f) {
                        translationY = 0.0f;
                    }
                    BottomSheet.this.containerView.setTranslationY(translationY);
                    this.startedTrackingY = (int) ev.getY();
                }
            } else if (ev == null || (ev != null && ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float translationY2 = BottomSheet.this.containerView.getTranslationY();
                if (this.startedTracking || translationY2 != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
            if ((intercept || !this.maybeStartTracking) && !this.startedTracking && BottomSheet.this.canDismissWithSwipe()) {
                return false;
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent ev) {
            return processTouchEvent(ev, false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthSpec;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                height -= BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
            }
            setMeasuredDimension(width, height);
            if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                width -= BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            }
            boolean isPortrait = width < height;
            if (BottomSheet.this.containerView != null) {
                if (!BottomSheet.this.fullWidth) {
                    if (AndroidUtilities.isTablet()) {
                        widthSpec = View.MeasureSpec.makeMeasureSpec(((int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.8f)) + (BottomSheet.this.backgroundPaddingLeft * 2), 1073741824);
                    } else {
                        widthSpec = View.MeasureSpec.makeMeasureSpec(isPortrait ? (BottomSheet.this.backgroundPaddingLeft * 2) + width : ((int) Math.max(((float) width) * 0.8f, (float) Math.min(AndroidUtilities.dp(480.0f), width))) + (BottomSheet.this.backgroundPaddingLeft * 2), 1073741824);
                    }
                    BottomSheet.this.containerView.measure(widthSpec, View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                } else {
                    BottomSheet.this.containerView.measure(View.MeasureSpec.makeMeasureSpec((BottomSheet.this.backgroundPaddingLeft * 2) + width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                }
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (!(child.getVisibility() == 8 || child == BottomSheet.this.containerView || BottomSheet.this.onCustomMeasure(child, width, height))) {
                    measureChildWithMargins(child, View.MeasureSpec.makeMeasureSpec(width, 1073741824), 0, View.MeasureSpec.makeMeasureSpec(height, 1073741824), 0);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int right2;
            int left2;
            int childLeft;
            int childTop;
            int right3;
            int left3;
            BottomSheet.access$710(BottomSheet.this);
            int i = 21;
            if (BottomSheet.this.containerView != null) {
                if (BottomSheet.this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
                    left3 = left;
                    right3 = right;
                } else {
                    left3 = left + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                    right3 = right - BottomSheet.this.lastInsets.getSystemWindowInsetRight();
                }
                int t = (bottom - top) - BottomSheet.this.containerView.getMeasuredHeight();
                int l = ((right3 - left3) - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
                if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= 21) {
                    l += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                }
                BottomSheet.this.containerView.layout(l, t, BottomSheet.this.containerView.getMeasuredWidth() + l, BottomSheet.this.containerView.getMeasuredHeight() + t);
                left2 = left3;
                right2 = right3;
            } else {
                left2 = left;
                right2 = right;
            }
            int count = getChildCount();
            int i2 = 0;
            while (i2 < count) {
                View child = getChildAt(i2);
                if (!(child.getVisibility() == 8 || child == BottomSheet.this.containerView || BottomSheet.this.onCustomLayout(child, left2, top, right2, bottom))) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int verticalGravity = gravity & 112;
                    int i3 = gravity & 7 & 7;
                    if (i3 == 1) {
                        childLeft = ((((right2 - left2) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                    } else if (i3 != 5) {
                        childLeft = lp.leftMargin;
                    } else {
                        childLeft = (right2 - width) - lp.rightMargin;
                    }
                    if (verticalGravity == 16) {
                        childTop = ((((bottom - top) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                    } else if (verticalGravity == 48) {
                        childTop = lp.topMargin;
                    } else if (verticalGravity != 80) {
                        childTop = lp.topMargin;
                    } else {
                        childTop = ((bottom - top) - height) - lp.bottomMargin;
                    }
                    if (BottomSheet.this.lastInsets != null && Build.VERSION.SDK_INT >= i) {
                        childLeft += BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
                i2++;
                i = 21;
            }
            if (BottomSheet.this.layoutCount == 0 && BottomSheet.this.startAnimationRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
                BottomSheet.this.startAnimationRunnable.run();
                Runnable unused = BottomSheet.this.startAnimationRunnable = null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (BottomSheet.this.canDismissWithSwipe()) {
                return processTouchEvent(event, true);
            }
            return super.onInterceptTouchEvent(event);
        }

        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            if (this.maybeStartTracking && !this.startedTracking) {
                onTouchEvent((MotionEvent) null);
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            BottomSheet.this.onContainerDraw(canvas);
        }
    }

    public static class BottomSheetDelegate implements BottomSheetDelegateInterface {
        public void onOpenAnimationStart() {
        }

        public void onOpenAnimationEnd() {
        }

        public boolean canDismiss() {
            return true;
        }
    }

    public static class BottomSheetCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public BottomSheetCell(Context context, int type) {
            super(context);
            setBackground((Drawable) null);
            setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(56, 48, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            if (type == 0) {
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 16.0f);
                addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
            } else if (type == 1) {
                this.textView.setGravity(17);
                this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                this.textView.setTextSize(1, 14.0f);
                this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                addView(this.textView, LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            float f = 16.0f;
            if (icon != 0) {
                this.imageView.setImageResource(icon);
                this.imageView.setVisibility(0);
                TextView textView2 = this.textView;
                int dp = AndroidUtilities.dp(LocaleController.isRTL ? 16.0f : 72.0f);
                if (LocaleController.isRTL) {
                    f = 72.0f;
                }
                textView2.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setAllowNestedScroll(boolean value) {
        this.allowNestedScroll = value;
        if (!value) {
            this.containerView.setTranslationY(0.0f);
        }
    }

    public BottomSheet(Context context, int themeResId) {
        super(context, themeResId);
    }

    public BottomSheet(Context context, boolean needFocus, int backgroundType) {
        super(context, R.style.TransparentDialog);
        init(context, needFocus, backgroundType);
    }

    /* access modifiers changed from: protected */
    public void init(Context context, boolean needFocus, int backgroundType) {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(-2147417856);
        }
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Rect padding = new Rect();
        if (backgroundType == 0) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
            this.shadowDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        } else if (backgroundType == 1) {
            Drawable mutate2 = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
            this.shadowDrawable = mutate2;
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        } else if (backgroundType == 2) {
            this.shadowDrawable = new ColorDrawable(0);
        }
        this.shadowDrawable.getPadding(padding);
        this.backgroundPaddingLeft = padding.left;
        this.backgroundPaddingTop = padding.top;
        AnonymousClass1 r4 = new ContainerView(getContext()) {
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                try {
                    return BottomSheet.this.allowDrawContent && super.drawChild(canvas, child, drawingTime);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    return true;
                }
            }
        };
        this.container = r4;
        r4.setBackgroundDrawable(this.backDrawable);
        this.focusable = needFocus;
        if (Build.VERSION.SDK_INT >= 21) {
            this.container.setFitsSystemWindows(true);
            this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return BottomSheet.this.lambda$init$0$BottomSheet(view, windowInsets);
                }
            });
            this.container.setSystemUiVisibility(1280);
        }
        this.backDrawable.setAlpha(0);
    }

    public /* synthetic */ WindowInsets lambda$init$0$BottomSheet(View v, WindowInsets insets) {
        this.lastInsets = insets;
        v.requestLayout();
        return insets.consumeSystemWindowInsets();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogNoAnimation);
        setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
        int i = 0;
        if (this.containerView == null) {
            AnonymousClass2 r2 = new FrameLayout(getContext()) {
                public boolean hasOverlappingRendering() {
                    return false;
                }

                public void setTranslationY(float translationY) {
                    super.setTranslationY(translationY);
                    BottomSheet.this.onContainerTranslationYChanged(translationY);
                }
            };
            this.containerView = r2;
            r2.setBackgroundDrawable(this.shadowDrawable);
            this.containerView.setPadding(this.backgroundPaddingLeft, ((this.applyTopPadding ? AndroidUtilities.dp(8.0f) : 0) + this.backgroundPaddingTop) - 1, this.backgroundPaddingLeft, this.applyBottomPadding ? AndroidUtilities.dp(8.0f) : 0);
        }
        this.containerView.setVisibility(4);
        this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        int topOffset = 0;
        if (this.title != null) {
            TextView textView = new TextView(getContext());
            this.titleView = textView;
            textView.setLines(1);
            this.titleView.setSingleLine(true);
            this.titleView.setText(this.title);
            this.titleView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.titleView.setTextSize(1, 16.0f);
            this.titleView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            this.titleView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
            this.titleView.setGravity(16);
            this.containerView.addView(this.titleView, LayoutHelper.createFrame(-1, 48.0f));
            this.titleView.setOnTouchListener($$Lambda$BottomSheet$Q_xv8ktcsKmvv9BLDQoaaaxnpi0.INSTANCE);
            topOffset = 0 + 48;
        }
        View view = this.customView;
        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup) this.customView.getParent()).removeView(this.customView);
            }
            this.containerView.addView(this.customView, LayoutHelper.createFrame(-1.0f, -2.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
        } else if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    BottomSheetCell cell = new BottomSheetCell(getContext(), i);
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0);
                    this.containerView.addView(cell, LayoutHelper.createFrame(-1.0f, 48.0f, 51, 0.0f, (float) topOffset, 0.0f, 0.0f));
                    topOffset += 48;
                    cell.setTag(Integer.valueOf(a));
                    cell.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            BottomSheet.this.lambda$onCreate$2$BottomSheet(view);
                        }
                    });
                    this.itemViews.add(cell);
                }
                a++;
                i = 0;
            }
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = -1;
        params.gravity = 51;
        params.dimAmount = 0.0f;
        params.flags &= -3;
        if (this.focusable) {
            params.softInputMode = 16;
        } else {
            params.flags |= 131072;
        }
        if (this.isFullscreen) {
            if (Build.VERSION.SDK_INT >= 21) {
                params.flags |= -2147417856;
            }
            params.flags |= 1024;
            this.container.setSystemUiVisibility(1284);
        }
        params.height = -1;
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(params);
    }

    static /* synthetic */ boolean lambda$onCreate$1(View v, MotionEvent event) {
        return true;
    }

    public /* synthetic */ void lambda$onCreate$2$BottomSheet(View v) {
        dismissWithButtonClick(((Integer) v.getTag()).intValue());
    }

    public boolean isFocusable() {
        return this.focusable;
    }

    public void setFocusable(boolean value) {
        if (this.focusable != value) {
            this.focusable = value;
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            if (this.focusable) {
                params.softInputMode = 16;
                params.flags &= -131073;
            } else {
                params.softInputMode = 48;
                params.flags |= 131072;
            }
            window.setAttributes(params);
        }
    }

    public void setShowWithoutAnimation(boolean value) {
        this.showWithoutAnimation = value;
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void show() {
        super.show();
        if (this.focusable) {
            getWindow().setSoftInputMode(16);
        }
        int i = 0;
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        if (this.showWithoutAnimation) {
            ColorDrawable colorDrawable = this.backDrawable;
            if (this.dimBehind) {
                i = 51;
            }
            colorDrawable.setAlpha(i);
            this.containerView.setTranslationY(0.0f);
            return;
        }
        this.backDrawable.setAlpha(0);
        if (Build.VERSION.SDK_INT >= 18) {
            this.layoutCount = 2;
            ViewGroup viewGroup = this.containerView;
            viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
            AnonymousClass3 r0 = new Runnable() {
                public void run() {
                    if (BottomSheet.this.startAnimationRunnable == this && !BottomSheet.this.dismissed) {
                        Runnable unused = BottomSheet.this.startAnimationRunnable = null;
                        BottomSheet.this.startOpenAnimation();
                    }
                }
            };
            this.startAnimationRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 150);
            return;
        }
        startOpenAnimation();
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            this.container.setBackgroundDrawable(value ? this.backDrawable : null);
            this.container.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent event) {
        return false;
    }

    public void setCustomView(View view) {
        this.customView = view;
    }

    public void setTitle(CharSequence value) {
        this.title = value;
    }

    public void setApplyTopPadding(boolean value) {
        this.applyTopPadding = value;
    }

    public void setApplyBottomPadding(boolean value) {
        this.applyBottomPadding = value;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int width, int height) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return true;
    }

    public TextView getTitleView() {
        return this.titleView;
    }

    /* access modifiers changed from: protected */
    public void onContainerTranslationYChanged(float translationY) {
    }

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    /* access modifiers changed from: private */
    public void startOpenAnimation() {
        if (!this.dismissed) {
            this.containerView.setVisibility(0);
            if (!onCustomOpenAnimation()) {
                if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
                    this.container.setLayerType(2, (Paint) null);
                }
                ViewGroup viewGroup = this.containerView;
                viewGroup.setTranslationY((float) viewGroup.getMeasuredHeight());
                this.currentSheetAnimationType = 1;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, new float[]{0.0f});
                ColorDrawable colorDrawable = this.backDrawable;
                Property<ColorDrawable, Integer> property = AnimationProperties.COLOR_DRAWABLE_ALPHA;
                int[] iArr = new int[1];
                iArr[0] = this.dimBehind ? 51 : 0;
                animatorArr[1] = ObjectAnimator.ofInt(colorDrawable, property, iArr);
                animatorSet.playTogether(animatorArr);
                this.currentSheetAnimation.setDuration(400);
                this.currentSheetAnimation.setStartDelay(20);
                this.currentSheetAnimation.setInterpolator(this.openInterpolator);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                            BottomSheet.this.isAnimationed = true;
                            if (BottomSheet.this.delegate != null) {
                                BottomSheet.this.delegate.onOpenAnimationEnd();
                            }
                            if (BottomSheet.this.useHardwareLayer) {
                                BottomSheet.this.container.setLayerType(0, (Paint) null);
                            }
                            if (BottomSheet.this.isFullscreen) {
                                WindowManager.LayoutParams params = BottomSheet.this.getWindow().getAttributes();
                                params.flags &= -1025;
                                BottomSheet.this.getWindow().setAttributes(params);
                            }
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            }
        }
    }

    public void setDelegate(BottomSheetDelegateInterface bottomSheetDelegate) {
        this.delegate = bottomSheetDelegate;
    }

    public FrameLayout getContainer() {
        return this.container;
    }

    public ViewGroup getSheetContainer() {
        return this.containerView;
    }

    public int getTag() {
        return this.tag;
    }

    public void setDimBehind(boolean value) {
        this.dimBehind = value;
    }

    public void setItemText(int item, CharSequence text) {
        if (item >= 0 && item < this.itemViews.size()) {
            this.itemViews.get(item).textView.setText(text);
        }
    }

    public void setItemColor(int item, int color, int icon) {
        if (item >= 0 && item < this.itemViews.size()) {
            BottomSheetCell cell = this.itemViews.get(item);
            cell.textView.setTextColor(color);
            cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
        }
    }

    public void setItems(CharSequence[] i, int[] icons, DialogInterface.OnClickListener listener) {
        this.items = i;
        this.itemIcons = icons;
        this.onClickListener = listener;
    }

    public void setTitleColor(int color) {
        TextView textView = this.titleView;
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    public boolean isDismissed() {
        return this.dismissed;
    }

    public void dismissWithButtonClick(final int item) {
        if (!this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            this.currentSheetAnimationType = 2;
            AnimatorSet animatorSet = new AnimatorSet();
            this.currentSheetAnimation = animatorSet;
            ViewGroup viewGroup = this.containerView;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, "translationY", new float[]{(float) (viewGroup.getMeasuredHeight() + AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0})});
            this.currentSheetAnimation.setDuration(180);
            this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                        if (BottomSheet.this.onClickListener != null) {
                            BottomSheet.this.onClickListener.onClick(BottomSheet.this, item);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                BottomSheet.AnonymousClass5.this.lambda$onAnimationEnd$0$BottomSheet$5();
                            }
                        });
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                }

                public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$5() {
                    try {
                        BottomSheet.super.dismiss();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                        BottomSheet.this.currentSheetAnimation = null;
                        BottomSheet.this.currentSheetAnimationType = 0;
                    }
                }
            });
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            this.currentSheetAnimation.start();
        }
    }

    public void dismiss() {
        BottomSheetDelegateInterface bottomSheetDelegateInterface = this.delegate;
        if ((bottomSheetDelegateInterface == null || bottomSheetDelegateInterface.canDismiss()) && !this.dismissed) {
            this.dismissed = true;
            cancelSheetAnimation();
            if (!this.allowCustomAnimation || !onCustomCloseAnimation()) {
                this.currentSheetAnimationType = 2;
                AnimatorSet animatorSet = new AnimatorSet();
                this.currentSheetAnimation = animatorSet;
                ViewGroup viewGroup = this.containerView;
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, "translationY", new float[]{(float) (viewGroup.getMeasuredHeight() + AndroidUtilities.dp(10.0f))}), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[]{0})});
                if (this.useFastDismiss) {
                    int height = this.containerView.getMeasuredHeight();
                    this.currentSheetAnimation.setDuration((long) Math.max(60, (int) (((((float) height) - this.containerView.getTranslationY()) * 180.0f) / ((float) height))));
                    this.useFastDismiss = false;
                } else {
                    this.currentSheetAnimation.setDuration(180);
                }
                this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    BottomSheet.AnonymousClass6.this.lambda$onAnimationEnd$0$BottomSheet$6();
                                }
                            });
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    }

                    public /* synthetic */ void lambda$onAnimationEnd$0$BottomSheet$6() {
                        try {
                            BottomSheet.this.dismissInternal();
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }

                    public void onAnimationCancel(Animator animation) {
                        if (BottomSheet.this.currentSheetAnimation != null && BottomSheet.this.currentSheetAnimation.equals(animation)) {
                            BottomSheet.this.currentSheetAnimation = null;
                            BottomSheet.this.currentSheetAnimationType = 0;
                        }
                    }
                });
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
                this.currentSheetAnimation.start();
            }
        }
    }

    public void dismissInternal() {
        try {
            super.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomCloseAnimation() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        return false;
    }

    public static class Builder {
        private BottomSheet bottomSheet;

        public Builder(Context context, boolean needFocus, int backgroundType) {
            this.bottomSheet = new BottomSheet(context, needFocus, backgroundType);
        }

        public Builder(Context context) {
            this.bottomSheet = new BottomSheet(context, false, 1);
        }

        public Builder(Context context, boolean needFocus) {
            this.bottomSheet = new BottomSheet(context, false, 1);
        }

        public Builder(Context context, int type) {
            this.bottomSheet = new BottomSheet(context, false, type);
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = items;
            DialogInterface.OnClickListener unused2 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            CharSequence[] unused = this.bottomSheet.items = items;
            int[] unused2 = this.bottomSheet.itemIcons = icons;
            DialogInterface.OnClickListener unused3 = this.bottomSheet.onClickListener = onClickListener;
            return this;
        }

        public Builder setCustomView(View view) {
            View unused = this.bottomSheet.customView = view;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            CharSequence unused = this.bottomSheet.title = title;
            return this;
        }

        public BottomSheet create() {
            return this.bottomSheet;
        }

        public BottomSheet setDimBehind(boolean value) {
            boolean unused = this.bottomSheet.dimBehind = value;
            return this.bottomSheet;
        }

        public BottomSheet show() {
            this.bottomSheet.show();
            return this.bottomSheet;
        }

        public Builder setTag(int tag) {
            int unused = this.bottomSheet.tag = tag;
            return this;
        }

        public Builder setUseHardwareLayer(boolean value) {
            boolean unused = this.bottomSheet.useHardwareLayer = value;
            return this;
        }

        public Builder setDelegate(BottomSheetDelegate delegate) {
            this.bottomSheet.setDelegate(delegate);
            return this;
        }

        public Builder setApplyTopPadding(boolean value) {
            this.bottomSheet.applyTopPadding = value;
            return this;
        }

        public Builder setApplyBottomPadding(boolean value) {
            this.bottomSheet.applyBottomPadding = value;
            return this;
        }

        public Runnable getDismissRunnable() {
            return this.bottomSheet.dismissRunnable;
        }

        public BottomSheet setUseFullWidth(boolean value) {
            this.bottomSheet.fullWidth = value;
            return this.bottomSheet;
        }

        public BottomSheet setUseFullscreen(boolean value) {
            this.bottomSheet.isFullscreen = value;
            return this.bottomSheet;
        }
    }

    /* access modifiers changed from: protected */
    public int getLeftInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return this.lastInsets.getSystemWindowInsetLeft();
    }

    /* access modifiers changed from: protected */
    public int getRightInset() {
        if (this.lastInsets == null || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        return this.lastInsets.getSystemWindowInsetRight();
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onContainerDraw(Canvas canvas) {
    }
}
