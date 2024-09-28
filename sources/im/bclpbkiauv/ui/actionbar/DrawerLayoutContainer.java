package im.bclpbkiauv.ui.actionbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.R;

public class DrawerLayoutContainer extends FrameLayout {
    private static final int MIN_DRAWER_MARGIN = 64;
    private boolean allowDrawContent = true;
    private boolean allowOpenDrawer;
    private Paint backgroundPaint = new Paint();
    private boolean beginTrackingSent;
    private int behindKeyboardColor;
    private AnimatorSet currentAnimation;
    private ViewGroup drawerLayout;
    private boolean drawerOpened;
    private float drawerPosition;
    private boolean hasCutout;
    private boolean inLayout;
    private Object lastInsets;
    private boolean maybeStartTracking;
    private int minDrawerMargin = ((int) ((AndroidUtilities.density * 64.0f) + 0.5f));
    private int paddingTop;
    private ActionBarLayout parentActionBarLayout;
    private Rect rect = new Rect();
    private float scrimOpacity;
    private Paint scrimPaint = new Paint();
    private Drawable shadowLeft;
    private boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;

    public DrawerLayoutContainer(Context context) {
        super(context);
        setDescendantFocusability(262144);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            setFitsSystemWindows(true);
            setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return DrawerLayoutContainer.this.lambda$new$0$DrawerLayoutContainer(view, windowInsets);
                }
            });
            setSystemUiVisibility(1280);
        }
        this.shadowLeft = getResources().getDrawable(R.drawable.menu_shadow);
    }

    public /* synthetic */ WindowInsets lambda$new$0$DrawerLayoutContainer(View v, WindowInsets insets) {
        DrawerLayoutContainer drawerLayout2 = (DrawerLayoutContainer) v;
        if (AndroidUtilities.statusBarHeight != insets.getSystemWindowInsetTop()) {
            drawerLayout2.requestLayout();
        }
        AndroidUtilities.statusBarHeight = insets.getSystemWindowInsetTop();
        this.lastInsets = insets;
        boolean z = true;
        drawerLayout2.setWillNotDraw(insets.getSystemWindowInsetTop() <= 0 && getBackground() == null);
        if (Build.VERSION.SDK_INT >= 28) {
            DisplayCutout cutout = insets.getDisplayCutout();
            if (cutout == null || cutout.getBoundingRects().size() == 0) {
                z = false;
            }
            this.hasCutout = z;
        }
        invalidate();
        return insets.consumeSystemWindowInsets();
    }

    private void dispatchChildInsets(View child, Object insets, int drawerGravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    private void applyMarginInsets(ViewGroup.MarginLayoutParams lp, Object insets, int drawerGravity, boolean topOnly) {
        WindowInsets wi = (WindowInsets) insets;
        int i = 0;
        if (drawerGravity == 3) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(), wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (drawerGravity == 5) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(), wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        if (!topOnly) {
            i = wi.getSystemWindowInsetTop();
        }
        lp.topMargin = i;
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    private int getTopInset(Object insets) {
        if (Build.VERSION.SDK_INT < 21 || insets == null) {
            return 0;
        }
        return ((WindowInsets) insets).getSystemWindowInsetTop();
    }

    public void setDrawerLayout(ViewGroup layout) {
        this.drawerLayout = layout;
        addView(layout);
        if (Build.VERSION.SDK_INT >= 21) {
            this.drawerLayout.setFitsSystemWindows(true);
        }
    }

    public void moveDrawerByX(float dx) {
        setDrawerPosition(this.drawerPosition + dx);
    }

    public void setDrawerPosition(float value) {
        this.drawerPosition = value;
        if (value > ((float) this.drawerLayout.getMeasuredWidth())) {
            this.drawerPosition = (float) this.drawerLayout.getMeasuredWidth();
        } else if (this.drawerPosition < 0.0f) {
            this.drawerPosition = 0.0f;
        }
        this.drawerLayout.setTranslationX(this.drawerPosition);
        int newVisibility = this.drawerPosition > 0.0f ? 0 : 8;
        if (this.drawerLayout.getVisibility() != newVisibility) {
            this.drawerLayout.setVisibility(newVisibility);
        }
        setScrimOpacity(this.drawerPosition / ((float) this.drawerLayout.getMeasuredWidth()));
    }

    public float getDrawerPosition() {
        return this.drawerPosition;
    }

    public void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    public void openDrawer(boolean fast) {
        ActionBarLayout actionBarLayout;
        if (this.allowOpenDrawer) {
            if (!(!AndroidUtilities.isTablet() || (actionBarLayout = this.parentActionBarLayout) == null || actionBarLayout.parentActivity == null)) {
                AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
            }
            cancelCurrentAnimation();
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{(float) this.drawerLayout.getMeasuredWidth()})});
            animatorSet.setInterpolator(new DecelerateInterpolator());
            if (fast) {
                animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * (((float) this.drawerLayout.getMeasuredWidth()) - this.drawerPosition)), 50));
            } else {
                animatorSet.setDuration(300);
            }
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
                }
            });
            animatorSet.start();
            this.currentAnimation = animatorSet;
        }
    }

    public void closeDrawer(boolean fast) {
        cancelCurrentAnimation();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, "drawerPosition", new float[]{0.0f})});
        animatorSet.setInterpolator(new DecelerateInterpolator());
        if (fast) {
            animatorSet.setDuration((long) Math.max((int) ((200.0f / ((float) this.drawerLayout.getMeasuredWidth())) * this.drawerPosition), 50));
        } else {
            animatorSet.setDuration(300);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
            }
        });
        animatorSet.start();
    }

    /* access modifiers changed from: private */
    public void onDrawerAnimationEnd(boolean opened) {
        this.startedTracking = false;
        this.currentAnimation = null;
        this.drawerOpened = opened;
        if (!opened) {
            ViewGroup viewGroup = this.drawerLayout;
            if (viewGroup instanceof ListView) {
                ((ListView) viewGroup).setSelectionFromTop(0, 0);
            }
        }
        if (Build.VERSION.SDK_INT >= 19) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != this.drawerLayout) {
                    child.setImportantForAccessibility(opened ? 4 : 0);
                }
            }
        }
        sendAccessibilityEvent(32);
    }

    private void setScrimOpacity(float value) {
        this.scrimOpacity = value;
        invalidate();
    }

    private float getScrimOpacity() {
        return this.scrimOpacity;
    }

    public View getDrawerLayout() {
        return this.drawerLayout;
    }

    public void setParentActionBarLayout(ActionBarLayout layout) {
        this.parentActionBarLayout = layout;
    }

    public void setAllowOpenDrawer(boolean value, boolean animated) {
        this.allowOpenDrawer = value;
        if (!value && this.drawerPosition != 0.0f) {
            if (!animated) {
                setDrawerPosition(0.0f);
                onDrawerAnimationEnd(false);
                return;
            }
            closeDrawer(true);
        }
    }

    private void prepareForDrawerOpen(MotionEvent ev) {
        this.maybeStartTracking = false;
        this.startedTracking = true;
        if (ev != null) {
            this.startedTrackingX = (int) ev.getX();
        }
        this.beginTrackingSent = false;
    }

    public boolean isDrawerOpened() {
        return this.drawerOpened;
    }

    public void setAllowDrawContent(boolean value) {
        if (this.allowDrawContent != value) {
            this.allowDrawContent = value;
            invalidate();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0184, code lost:
        if (r0 != ((float) r8.drawerLayout.getMeasuredWidth())) goto L_0x0186;
     */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01f1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r8.parentActionBarLayout
            boolean r0 = r0.checkTransitionAnimation()
            r1 = 0
            if (r0 != 0) goto L_0x01fa
            boolean r0 = r8.drawerOpened
            r2 = 1
            if (r0 == 0) goto L_0x0028
            if (r9 == 0) goto L_0x0028
            float r0 = r9.getX()
            float r3 = r8.drawerPosition
            int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x0028
            boolean r0 = r8.startedTracking
            if (r0 != 0) goto L_0x0028
            int r0 = r9.getAction()
            if (r0 != r2) goto L_0x0027
            r8.closeDrawer(r1)
        L_0x0027:
            return r2
        L_0x0028:
            boolean r0 = r8.allowOpenDrawer
            if (r0 == 0) goto L_0x01f7
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r8.parentActionBarLayout
            java.util.ArrayList<im.bclpbkiauv.ui.actionbar.BaseFragment> r0 = r0.fragmentsStack
            int r0 = r0.size()
            if (r0 != r2) goto L_0x01f7
            r0 = 2
            if (r9 == 0) goto L_0x0080
            int r3 = r9.getAction()
            if (r3 == 0) goto L_0x0045
            int r3 = r9.getAction()
            if (r3 != r0) goto L_0x0080
        L_0x0045:
            boolean r3 = r8.startedTracking
            if (r3 != 0) goto L_0x0080
            boolean r3 = r8.maybeStartTracking
            if (r3 != 0) goto L_0x0080
            im.bclpbkiauv.ui.actionbar.ActionBarLayout r0 = r8.parentActionBarLayout
            android.graphics.Rect r3 = r8.rect
            r0.getHitRect(r3)
            float r0 = r9.getX()
            int r0 = (int) r0
            r8.startedTrackingX = r0
            float r0 = r9.getY()
            int r0 = (int) r0
            r8.startedTrackingY = r0
            android.graphics.Rect r3 = r8.rect
            int r4 = r8.startedTrackingX
            boolean r0 = r3.contains(r4, r0)
            if (r0 == 0) goto L_0x01f7
            int r0 = r9.getPointerId(r1)
            r8.startedTrackingPointerId = r0
            r8.maybeStartTracking = r2
            r8.cancelCurrentAnimation()
            android.view.VelocityTracker r0 = r8.velocityTracker
            if (r0 == 0) goto L_0x01f7
            r0.clear()
            goto L_0x01f7
        L_0x0080:
            r3 = 0
            if (r9 == 0) goto L_0x013d
            int r4 = r9.getAction()
            if (r4 != r0) goto L_0x013d
            int r0 = r9.getPointerId(r1)
            int r4 = r8.startedTrackingPointerId
            if (r0 != r4) goto L_0x013d
            android.view.VelocityTracker r0 = r8.velocityTracker
            if (r0 != 0) goto L_0x009b
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r0
        L_0x009b:
            float r0 = r9.getX()
            int r1 = r8.startedTrackingX
            float r1 = (float) r1
            float r0 = r0 - r1
            int r0 = (int) r0
            float r0 = (float) r0
            float r1 = r9.getY()
            int r1 = (int) r1
            int r4 = r8.startedTrackingY
            int r1 = r1 - r4
            int r1 = java.lang.Math.abs(r1)
            float r1 = (float) r1
            android.view.VelocityTracker r4 = r8.velocityTracker
            r4.addMovement(r9)
            boolean r4 = r8.maybeStartTracking
            if (r4 == 0) goto L_0x010f
            boolean r4 = r8.startedTracking
            if (r4 != 0) goto L_0x010f
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x00de
            r4 = 1077936128(0x40400000, float:3.0)
            float r4 = r0 / r4
            float r5 = java.lang.Math.abs(r1)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x00de
            float r4 = java.lang.Math.abs(r0)
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            float r5 = im.bclpbkiauv.messenger.AndroidUtilities.getPixelsInCM(r5, r2)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 >= 0) goto L_0x0101
        L_0x00de:
            boolean r4 = r8.drawerOpened
            if (r4 == 0) goto L_0x010f
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x010f
            float r3 = java.lang.Math.abs(r0)
            float r4 = java.lang.Math.abs(r1)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x010f
            float r3 = java.lang.Math.abs(r0)
            r4 = 1053609165(0x3ecccccd, float:0.4)
            float r4 = im.bclpbkiauv.messenger.AndroidUtilities.getPixelsInCM(r4, r2)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 < 0) goto L_0x010f
        L_0x0101:
            r8.prepareForDrawerOpen(r9)
            float r3 = r9.getX()
            int r3 = (int) r3
            r8.startedTrackingX = r3
            r8.requestDisallowInterceptTouchEvent(r2)
            goto L_0x015e
        L_0x010f:
            boolean r3 = r8.startedTracking
            if (r3 == 0) goto L_0x015e
            boolean r3 = r8.beginTrackingSent
            if (r3 != 0) goto L_0x0132
            android.content.Context r3 = r8.getContext()
            android.app.Activity r3 = (android.app.Activity) r3
            android.view.View r3 = r3.getCurrentFocus()
            if (r3 == 0) goto L_0x0130
            android.content.Context r3 = r8.getContext()
            android.app.Activity r3 = (android.app.Activity) r3
            android.view.View r3 = r3.getCurrentFocus()
            im.bclpbkiauv.messenger.AndroidUtilities.hideKeyboard(r3)
        L_0x0130:
            r8.beginTrackingSent = r2
        L_0x0132:
            r8.moveDrawerByX(r0)
            float r2 = r9.getX()
            int r2 = (int) r2
            r8.startedTrackingX = r2
            goto L_0x015e
        L_0x013d:
            if (r9 == 0) goto L_0x0160
            if (r9 == 0) goto L_0x015e
            int r0 = r9.getPointerId(r1)
            int r4 = r8.startedTrackingPointerId
            if (r0 != r4) goto L_0x015e
            int r0 = r9.getAction()
            r4 = 3
            if (r0 == r4) goto L_0x0160
            int r0 = r9.getAction()
            if (r0 == r2) goto L_0x0160
            int r0 = r9.getAction()
            r4 = 6
            if (r0 != r4) goto L_0x015e
            goto L_0x0160
        L_0x015e:
            goto L_0x01f7
        L_0x0160:
            android.view.VelocityTracker r0 = r8.velocityTracker
            if (r0 != 0) goto L_0x016a
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r8.velocityTracker = r0
        L_0x016a:
            android.view.VelocityTracker r0 = r8.velocityTracker
            r4 = 1000(0x3e8, float:1.401E-42)
            r0.computeCurrentVelocity(r4)
            boolean r0 = r8.startedTracking
            if (r0 != 0) goto L_0x0186
            float r0 = r8.drawerPosition
            int r4 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r4 == 0) goto L_0x01e9
            android.view.ViewGroup r4 = r8.drawerLayout
            int r4 = r4.getMeasuredWidth()
            float r4 = (float) r4
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x01e9
        L_0x0186:
            android.view.VelocityTracker r0 = r8.velocityTracker
            float r0 = r0.getXVelocity()
            android.view.VelocityTracker r4 = r8.velocityTracker
            float r4 = r4.getYVelocity()
            float r5 = r8.drawerPosition
            android.view.ViewGroup r6 = r8.drawerLayout
            int r6 = r6.getMeasuredWidth()
            float r6 = (float) r6
            r7 = 1073741824(0x40000000, float:2.0)
            float r6 = r6 / r7
            r7 = 1163575296(0x455ac000, float:3500.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 >= 0) goto L_0x01b5
            int r5 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r5 < 0) goto L_0x01c1
            float r5 = java.lang.Math.abs(r0)
            float r6 = java.lang.Math.abs(r4)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x01c1
        L_0x01b5:
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 >= 0) goto L_0x01c3
            float r3 = java.lang.Math.abs(r0)
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 < 0) goto L_0x01c3
        L_0x01c1:
            r3 = 1
            goto L_0x01c4
        L_0x01c3:
            r3 = 0
        L_0x01c4:
            if (r3 != 0) goto L_0x01d8
            boolean r5 = r8.drawerOpened
            if (r5 != 0) goto L_0x01d3
            float r5 = java.lang.Math.abs(r0)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 < 0) goto L_0x01d3
            goto L_0x01d4
        L_0x01d3:
            r2 = 0
        L_0x01d4:
            r8.openDrawer(r2)
            goto L_0x01e9
        L_0x01d8:
            boolean r5 = r8.drawerOpened
            if (r5 == 0) goto L_0x01e5
            float r5 = java.lang.Math.abs(r0)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 < 0) goto L_0x01e5
            goto L_0x01e6
        L_0x01e5:
            r2 = 0
        L_0x01e6:
            r8.closeDrawer(r2)
        L_0x01e9:
            r8.startedTracking = r1
            r8.maybeStartTracking = r1
            android.view.VelocityTracker r0 = r8.velocityTracker
            if (r0 == 0) goto L_0x01f7
            r0.recycle()
            r0 = 0
            r8.velocityTracker = r0
        L_0x01f7:
            boolean r0 = r8.startedTracking
            return r0
        L_0x01fa:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.actionbar.DrawerLayoutContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.parentActionBarLayout.checkTransitionAnimation() || onTouchEvent(ev);
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        this.inLayout = true;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (!BuildVars.DEBUG_VERSION) {
                    try {
                        if (this.drawerLayout != child) {
                            child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        } else {
                            child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else if (this.drawerLayout != child) {
                    child.layout(lp.leftMargin, lp.topMargin + getPaddingTop(), lp.leftMargin + child.getMeasuredWidth(), lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                } else {
                    child.layout(-child.getMeasuredWidth(), lp.topMargin + getPaddingTop(), 0, lp.topMargin + child.getMeasuredHeight() + getPaddingTop());
                }
            }
        }
        this.inLayout = false;
    }

    public void requestLayout() {
        if (!this.inLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        if (Build.VERSION.SDK_INT < 21) {
            this.inLayout = true;
            if (heightSize == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight) {
                if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
                }
                heightSize = AndroidUtilities.displaySize.y;
            } else if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                setPadding(0, 0, 0, 0);
            }
            this.inLayout = false;
        }
        boolean applyInsets = this.lastInsets != null && Build.VERSION.SDK_INT >= 21;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 8) {
                int i2 = widthMeasureSpec;
                int i3 = heightMeasureSpec;
            } else {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                if (applyInsets) {
                    if (child.getFitsSystemWindows()) {
                        dispatchChildInsets(child, this.lastInsets, lp.gravity);
                    } else if (child.getTag() == null) {
                        applyMarginInsets(lp, this.lastInsets, lp.gravity, Build.VERSION.SDK_INT >= 21);
                    }
                }
                if (this.drawerLayout != child) {
                    child.measure(View.MeasureSpec.makeMeasureSpec((widthSize - lp.leftMargin) - lp.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec((heightSize - lp.topMargin) - lp.bottomMargin, 1073741824));
                    int i4 = widthMeasureSpec;
                    int i5 = heightMeasureSpec;
                } else {
                    child.setPadding(0, 0, 0, 0);
                    child.measure(getChildMeasureSpec(widthMeasureSpec, this.minDrawerMargin + lp.leftMargin + lp.rightMargin, lp.width), getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height));
                }
            }
        }
        int i6 = widthMeasureSpec;
        int i7 = heightMeasureSpec;
    }

    public void setBehindKeyboardColor(int color) {
        this.behindKeyboardColor = color;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int clipLeft;
        int lastVisibleChild;
        int vright;
        Canvas canvas2 = canvas;
        View view = child;
        if (!this.allowDrawContent) {
            return false;
        }
        int height = getHeight();
        boolean drawingContent = view != this.drawerLayout;
        int lastVisibleChild2 = 0;
        int clipLeft2 = 0;
        int clipRight = getWidth();
        int restoreCount = canvas.save();
        if (drawingContent) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (v.getVisibility() == 0 && v != this.drawerLayout) {
                    lastVisibleChild2 = i;
                }
                if (v != view && v.getVisibility() == 0 && v == this.drawerLayout && v.getHeight() >= height && (vright = ((int) v.getX()) + v.getMeasuredWidth()) > clipLeft2) {
                    clipLeft2 = vright;
                }
            }
            if (clipLeft2 != 0) {
                canvas2.clipRect(clipLeft2, 0, clipRight, getHeight());
            }
            lastVisibleChild = lastVisibleChild2;
            clipLeft = clipLeft2;
        } else {
            lastVisibleChild = 0;
            clipLeft = 0;
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas2.restoreToCount(restoreCount);
        if (this.scrimOpacity <= 0.0f || !drawingContent) {
            if (this.shadowLeft != null) {
                float alpha = Math.max(0.0f, Math.min(this.drawerPosition / ((float) AndroidUtilities.dp(20.0f)), 1.0f));
                if (alpha != 0.0f) {
                    this.shadowLeft.setBounds((int) this.drawerPosition, child.getTop(), ((int) this.drawerPosition) + this.shadowLeft.getIntrinsicWidth(), child.getBottom());
                    this.shadowLeft.setAlpha((int) (255.0f * alpha));
                    this.shadowLeft.draw(canvas2);
                }
            }
        } else if (indexOfChild(view) == lastVisibleChild) {
            this.scrimPaint.setColor(((int) (this.scrimOpacity * 153.0f)) << 24);
            canvas.drawRect((float) clipLeft, 0.0f, (float) clipRight, (float) getHeight(), this.scrimPaint);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Object obj;
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null) {
            WindowInsets insets = (WindowInsets) obj;
            int bottomInset = insets.getSystemWindowInsetBottom();
            if (bottomInset > 0) {
                this.backgroundPaint.setColor(this.behindKeyboardColor);
                canvas.drawRect(0.0f, (float) (getMeasuredHeight() - bottomInset), (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
            }
            if (this.hasCutout) {
                this.backgroundPaint.setColor(-16777216);
                int left = insets.getSystemWindowInsetLeft();
                if (left != 0) {
                    canvas.drawRect(0.0f, 0.0f, (float) left, (float) getMeasuredHeight(), this.backgroundPaint);
                }
                int right = insets.getSystemWindowInsetRight();
                if (right != 0) {
                    canvas.drawRect((float) right, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.backgroundPaint);
                }
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean onRequestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (!this.drawerOpened || child == this.drawerLayout) {
            return super.onRequestSendAccessibilityEvent(child, event);
        }
        return false;
    }
}
