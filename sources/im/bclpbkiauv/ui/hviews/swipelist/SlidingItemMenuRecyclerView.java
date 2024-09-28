package im.bclpbkiauv.ui.hviews.swipelist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.collection.SimpleArrayMap;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import java.util.LinkedList;
import java.util.List;

public class SlidingItemMenuRecyclerView extends RecyclerListView {
    public static final int DEFAULT_ITEM_SCROLL_DURATION = 500;
    private static final String TAG = "SlidingItemMenuRecyclerView";
    private static final int TAG_ITEM_ANIMATOR = 2131297364;
    private static final int TAG_ITEM_MENU_WIDTH = 2131297365;
    private static final int TAG_MENU_ITEM_WIDTHS = 2131297366;
    private static final Interpolator sOvershootInterpolator = new OvershootInterpolator(1.0f);
    private static final Interpolator sViscousFluidInterpolator = new ViscousFluidInterpolator(6.66f);
    private ViewGroup mActiveItem;
    private final Rect mActiveItemBounds;
    private final Rect mActiveItemMenuBounds;
    private int mDownX;
    private int mDownY;
    private ViewGroup mFullyOpenedItem;
    private boolean mHasItemFullyOpenOnActionDown;
    private boolean mIsItemBeingDragged;
    private boolean mIsItemDraggable;
    private boolean mIsVerticalScrollBarEnabled;
    private final float mItemMinimumFlingVelocity;
    private int mItemScrollDuration;
    private final List<ViewGroup> mOpenedItems;
    protected final float mTouchSlop;
    private final float[] mTouchX;
    private final float[] mTouchY;
    private VelocityTracker mVelocityTracker;

    public boolean isItemScrollingEnabled() {
        return isItemDraggable();
    }

    public boolean isItemDraggable() {
        return this.mIsItemDraggable;
    }

    public void setItemScrollingEnabled(boolean enabled) {
        setItemDraggable(enabled);
    }

    public void setItemDraggable(boolean draggable) {
        this.mIsItemDraggable = draggable;
    }

    public int getItemScrollDuration() {
        return this.mItemScrollDuration;
    }

    public void setItemScrollDuration(int duration) {
        if (duration >= 0) {
            this.mItemScrollDuration = duration;
            return;
        }
        throw new IllegalArgumentException("The animators for opening/closing the item views cannot have negative duration: " + duration);
    }

    public SlidingItemMenuRecyclerView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SlidingItemMenuRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingItemMenuRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTouchX = new float[2];
        this.mTouchY = new float[2];
        this.mActiveItemBounds = new Rect();
        this.mActiveItemMenuBounds = new Rect();
        this.mOpenedItems = new LinkedList();
        float dp = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = ((float) ViewConfiguration.getTouchSlop()) * dp;
        this.mItemMinimumFlingVelocity = 200.0f * dp;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingItemMenuRecyclerView, defStyle, 0);
        setItemDraggable(ta.getBoolean(1, true));
        setItemScrollDuration(ta.getInteger(0, 500));
        ta.recycle();
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        this.mIsVerticalScrollBarEnabled = verticalScrollBarEnabled;
        super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    private boolean childHasMenu(ViewGroup itemView) {
        if (itemView.getVisibility() != 0) {
            return false;
        }
        int itemChildCount = itemView.getChildCount();
        View itemLastChild = itemView.getChildAt(itemChildCount >= 2 ? itemChildCount - 1 : 1);
        if (!(itemLastChild instanceof FrameLayout)) {
            return false;
        }
        FrameLayout itemMenu = (FrameLayout) itemLastChild;
        int menuItemCount = itemMenu.getChildCount();
        int[] menuItemWidths = new int[menuItemCount];
        int itemMenuWidth = 0;
        for (int i = 0; i < menuItemCount; i++) {
            menuItemWidths[i] = ((FrameLayout) itemMenu.getChildAt(i)).getChildAt(0).getWidth();
            itemMenuWidth += menuItemWidths[i];
        }
        if (itemMenuWidth <= 0) {
            return false;
        }
        itemView.setTag(R.id.tag_itemMenuWidth, Integer.valueOf(itemMenuWidth));
        itemView.setTag(R.id.tag_menuItemWidths, menuItemWidths);
        return true;
    }

    private void resolveActiveItemMenuBounds() {
        int left;
        int itemMenuWidth = ((Integer) this.mActiveItem.getTag(R.id.tag_itemMenuWidth)).intValue();
        if (Utils.isLayoutRtl(this.mActiveItem)) {
            left = 0;
        } else {
            left = this.mActiveItem.getRight() - itemMenuWidth;
        }
        this.mActiveItemMenuBounds.set(left, this.mActiveItemBounds.top, left + itemMenuWidth, this.mActiveItemBounds.bottom);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0027, code lost:
        if (r2 != 3) goto L_0x00ae;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r11) {
        /*
            r10 = this;
            int r0 = r11.getAction()
            if (r0 != 0) goto L_0x0009
            r10.resetTouch()
        L_0x0009:
            android.view.VelocityTracker r1 = r10.mVelocityTracker
            if (r1 != 0) goto L_0x0013
            android.view.VelocityTracker r1 = android.view.VelocityTracker.obtain()
            r10.mVelocityTracker = r1
        L_0x0013:
            android.view.VelocityTracker r1 = r10.mVelocityTracker
            r1.addMovement(r11)
            r1 = 0
            int r2 = r11.getAction()
            r3 = 0
            r4 = 1
            if (r2 == 0) goto L_0x0062
            if (r2 == r4) goto L_0x004b
            r5 = 2
            if (r2 == r5) goto L_0x002b
            r5 = 3
            if (r2 == r5) goto L_0x004b
            goto L_0x00ae
        L_0x002b:
            float r2 = r11.getX()
            float r5 = r11.getY()
            r10.markCurrTouchPoint(r2, r5)
            boolean r1 = r10.tryHandleItemScrollingEvent()
            boolean r2 = r10.mHasItemFullyOpenOnActionDown
            if (r2 == 0) goto L_0x00ae
            android.graphics.Rect r2 = r10.mActiveItemMenuBounds
            int r5 = r10.mDownX
            int r6 = r10.mDownY
            boolean r2 = r2.contains(r5, r6)
            if (r2 == 0) goto L_0x00ae
            return r1
        L_0x004b:
            boolean r2 = r10.mHasItemFullyOpenOnActionDown
            if (r2 == 0) goto L_0x005e
            android.graphics.Rect r2 = r10.mActiveItemMenuBounds
            int r5 = r10.mDownX
            int r6 = r10.mDownY
            boolean r2 = r2.contains(r5, r6)
            if (r2 == 0) goto L_0x005e
            r10.releaseItemView(r4)
        L_0x005e:
            r10.clearTouch()
            return r3
        L_0x0062:
            float r2 = r11.getX()
            int r2 = (int) r2
            r10.mDownX = r2
            float r2 = r11.getY()
            int r2 = (int) r2
            r10.mDownY = r2
            int r5 = r10.mDownX
            float r5 = (float) r5
            float r2 = (float) r2
            r10.markCurrTouchPoint(r5, r2)
            int r2 = r10.getChildCount()
            int r2 = r2 - r4
        L_0x007c:
            if (r2 < 0) goto L_0x00a6
            android.view.View r5 = r10.getChildAt(r2)
            boolean r6 = r5 instanceof android.view.ViewGroup
            if (r6 != 0) goto L_0x0087
            goto L_0x009b
        L_0x0087:
            r6 = r5
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            android.graphics.Rect r7 = r10.mActiveItemBounds
            r6.getHitRect(r7)
            android.graphics.Rect r7 = r10.mActiveItemBounds
            int r8 = r10.mDownX
            int r9 = r10.mDownY
            boolean r7 = r7.contains(r8, r9)
            if (r7 != 0) goto L_0x009e
        L_0x009b:
            int r2 = r2 + -1
            goto L_0x007c
        L_0x009e:
            boolean r7 = r10.childHasMenu(r6)
            if (r7 == 0) goto L_0x00a6
            r10.mActiveItem = r6
        L_0x00a6:
            java.util.List<android.view.ViewGroup> r2 = r10.mOpenedItems
            int r2 = r2.size()
            if (r2 != 0) goto L_0x00b8
        L_0x00ae:
            if (r1 != 0) goto L_0x00b6
            boolean r2 = super.onInterceptTouchEvent(r11)
            if (r2 == 0) goto L_0x00b7
        L_0x00b6:
            r3 = 1
        L_0x00b7:
            return r3
        L_0x00b8:
            r10.requestParentDisallowInterceptTouchEvent()
            android.view.ViewGroup r2 = r10.mFullyOpenedItem
            if (r2 == 0) goto L_0x00e9
            r10.mHasItemFullyOpenOnActionDown = r4
            android.view.ViewGroup r5 = r10.mActiveItem
            if (r5 != r2) goto L_0x00e2
            r10.resolveActiveItemMenuBounds()
            android.graphics.Rect r2 = r10.mActiveItemMenuBounds
            int r5 = r10.mDownX
            int r6 = r10.mDownY
            boolean r2 = r2.contains(r5, r6)
            if (r2 == 0) goto L_0x00d5
            return r3
        L_0x00d5:
            android.graphics.Rect r2 = r10.mActiveItemBounds
            int r3 = r10.mDownX
            int r5 = r10.mDownY
            boolean r2 = r2.contains(r3, r5)
            if (r2 == 0) goto L_0x00e2
            return r4
        L_0x00e2:
            android.view.ViewGroup r2 = r10.mFullyOpenedItem
            int r3 = r10.mItemScrollDuration
            r10.releaseItemViewInternal(r2, r3)
        L_0x00e9:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hviews.swipelist.SlidingItemMenuRecyclerView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent e) {
        int finalXFromEndToStart;
        if (this.mIsVerticalScrollBarEnabled) {
            super.setVerticalScrollBarEnabled(!this.mIsItemBeingDragged);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(e);
        int action = e.getAction() & 255;
        if (action != 1) {
            if (action == 2) {
                markCurrTouchPoint(e.getX(), e.getY());
                if (!this.mIsItemDraggable && cancelTouch()) {
                    return true;
                }
                if (this.mIsItemBeingDragged) {
                    float[] fArr = this.mTouchX;
                    float dx = fArr[fArr.length - 1] - fArr[fArr.length - 2];
                    float translationX = this.mActiveItem.getChildAt(0).getTranslationX();
                    boolean rtl = Utils.isLayoutRtl(this.mActiveItem);
                    if (rtl) {
                        finalXFromEndToStart = ((Integer) this.mActiveItem.getTag(R.id.tag_itemMenuWidth)).intValue();
                    } else {
                        finalXFromEndToStart = -((Integer) this.mActiveItem.getTag(R.id.tag_itemMenuWidth)).intValue();
                    }
                    if ((!rtl && dx + translationX < ((float) finalXFromEndToStart)) || (rtl && dx + translationX > ((float) finalXFromEndToStart))) {
                        dx /= 3.0f;
                    } else if ((!rtl && dx + translationX > 0.0f) || (rtl && dx + translationX < 0.0f)) {
                        dx = 0.0f - translationX;
                    }
                    translateItemViewXBy(this.mActiveItem, dx);
                    return true;
                } else if ((this.mHasItemFullyOpenOnActionDown || tryHandleItemScrollingEvent()) || this.mOpenedItems.size() > 0) {
                    return true;
                }
            } else if (action != 3) {
                if ((action == 5 || action == 6) && (this.mIsItemBeingDragged || this.mHasItemFullyOpenOnActionDown || this.mOpenedItems.size() > 0)) {
                    return true;
                }
            }
            return super.onTouchEvent(e);
        } else if (this.mIsItemDraggable && this.mIsItemBeingDragged) {
            boolean rtl2 = Utils.isLayoutRtl(this.mActiveItem);
            float translationX2 = this.mActiveItem.getChildAt(0).getTranslationX();
            int itemMenuWidth = ((Integer) this.mActiveItem.getTag(R.id.tag_itemMenuWidth)).intValue();
            if (translationX2 != 0.0f) {
                if ((rtl2 || translationX2 != ((float) (-itemMenuWidth))) && (!rtl2 || translationX2 != ((float) itemMenuWidth))) {
                    float[] fArr2 = this.mTouchX;
                    float dx2 = rtl2 ? fArr2[fArr2.length - 2] - fArr2[fArr2.length - 1] : fArr2[fArr2.length - 1] - fArr2[fArr2.length - 2];
                    this.mVelocityTracker.computeCurrentVelocity(1000);
                    float velocityX = Math.abs(this.mVelocityTracker.getXVelocity());
                    if (dx2 < 0.0f && velocityX >= this.mItemMinimumFlingVelocity) {
                        smoothTranslateItemViewXTo(this.mActiveItem, rtl2 ? (float) itemMenuWidth : (float) (-itemMenuWidth), this.mItemScrollDuration);
                        this.mFullyOpenedItem = this.mActiveItem;
                        clearTouch();
                        cancelParentTouch(e);
                        return true;
                    } else if (dx2 <= 0.0f || velocityX < this.mItemMinimumFlingVelocity) {
                        if (Math.abs(translationX2) < ((float) itemMenuWidth) / 2.0f) {
                            releaseItemView(true);
                        } else {
                            smoothTranslateItemViewXTo(this.mActiveItem, rtl2 ? (float) itemMenuWidth : (float) (-itemMenuWidth), this.mItemScrollDuration);
                            this.mFullyOpenedItem = this.mActiveItem;
                        }
                    } else {
                        releaseItemView(true);
                        clearTouch();
                        cancelParentTouch(e);
                        return true;
                    }
                } else {
                    this.mFullyOpenedItem = this.mActiveItem;
                }
            }
            clearTouch();
            cancelParentTouch(e);
            return true;
        }
        cancelTouch();
        return super.onTouchEvent(e);
    }

    private void markCurrTouchPoint(float x, float y) {
        float[] fArr = this.mTouchX;
        System.arraycopy(fArr, 1, fArr, 0, fArr.length - 1);
        float[] fArr2 = this.mTouchX;
        fArr2[fArr2.length - 1] = x;
        float[] fArr3 = this.mTouchY;
        System.arraycopy(fArr3, 1, fArr3, 0, fArr3.length - 1);
        float[] fArr4 = this.mTouchY;
        fArr4[fArr4.length - 1] = y;
    }

    private boolean tryHandleItemScrollingEvent() {
        if (this.mActiveItem == null || !this.mIsItemDraggable || getScrollState() != 0 || getLayoutManager().canScrollHorizontally()) {
            return false;
        }
        float[] fArr = this.mTouchY;
        if (Math.abs(fArr[fArr.length - 1] - ((float) this.mDownY)) <= this.mTouchSlop) {
            float[] fArr2 = this.mTouchX;
            float dx = fArr2[fArr2.length - 1] - ((float) this.mDownX);
            if (this.mOpenedItems.size() == 0) {
                boolean rtl = Utils.isLayoutRtl(this.mActiveItem);
                this.mIsItemBeingDragged = (rtl && dx > this.mTouchSlop) || (!rtl && dx < (-this.mTouchSlop));
            } else {
                this.mIsItemBeingDragged = Math.abs(dx) > this.mTouchSlop;
            }
            if (this.mIsItemBeingDragged) {
                requestParentDisallowInterceptTouchEvent();
                return true;
            }
        }
        return false;
    }

    private void requestParentDisallowInterceptTouchEvent() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private boolean cancelTouch() {
        if (this.mIsItemBeingDragged) {
            releaseItemView(true);
            clearTouch();
            return true;
        } else if (!this.mHasItemFullyOpenOnActionDown) {
            return false;
        } else {
            if (this.mActiveItem == this.mFullyOpenedItem) {
                releaseItemView(true);
            }
            clearTouch();
            return true;
        }
    }

    private void clearTouch() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        resetTouch();
    }

    private void resetTouch() {
        this.mActiveItem = null;
        this.mHasItemFullyOpenOnActionDown = false;
        this.mActiveItemBounds.setEmpty();
        this.mActiveItemMenuBounds.setEmpty();
        this.mIsItemBeingDragged = false;
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
    }

    private void cancelParentTouch(MotionEvent e) {
        int action = e.getAction();
        e.setAction(3);
        super.onTouchEvent(e);
        e.setAction(action);
    }

    public void releaseItemView() {
        releaseItemView(true);
    }

    public void releaseItemView(boolean animate) {
        releaseItemViewInternal(this.mIsItemBeingDragged ? this.mActiveItem : this.mFullyOpenedItem, animate ? this.mItemScrollDuration : 0);
    }

    private void releaseItemViewInternal(ViewGroup itemView, int duration) {
        if (itemView != null) {
            if (duration > 0) {
                smoothTranslateItemViewXTo(itemView, 0.0f, duration);
            } else {
                translateItemViewXTo(itemView, 0.0f);
            }
            if (this.mFullyOpenedItem == itemView) {
                this.mFullyOpenedItem = null;
            }
        }
    }

    public boolean openItemAtPosition(int position) {
        return openItemAtPosition(position, true);
    }

    public boolean openItemAtPosition(int position, boolean animate) {
        ViewGroup itemView;
        float f;
        RecyclerView.LayoutManager lm = getLayoutManager();
        int i = 0;
        if (lm == null) {
            return false;
        }
        View view = lm.findViewByPosition(position);
        if (!(view instanceof ViewGroup) || this.mFullyOpenedItem == (itemView = (ViewGroup) view) || !childHasMenu(itemView)) {
            return false;
        }
        if (!cancelTouch()) {
            releaseItemView(animate);
        }
        if (Utils.isLayoutRtl(itemView)) {
            f = (float) ((Integer) itemView.getTag(R.id.tag_itemMenuWidth)).intValue();
        } else {
            f = (float) (-((Integer) itemView.getTag(R.id.tag_itemMenuWidth)).intValue());
        }
        if (animate) {
            i = this.mItemScrollDuration;
        }
        smoothTranslateItemViewXTo(itemView, f, i);
        this.mFullyOpenedItem = itemView;
        return true;
    }

    private void smoothTranslateItemViewXTo(ViewGroup itemView, float x, int duration) {
        smoothTranslateItemViewXBy(itemView, x - itemView.getChildAt(0).getTranslationX(), duration);
    }

    private void smoothTranslateItemViewXBy(ViewGroup itemView, float dx, int duration) {
        TranslateItemViewXAnimator animator = (TranslateItemViewXAnimator) itemView.getTag(R.id.tag_itemAnimator);
        if (dx == 0.0f || duration <= 0) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
            baseTranslateItemViewXBy(itemView, dx);
            return;
        }
        boolean canceled = false;
        if (animator == null) {
            animator = new TranslateItemViewXAnimator(this, itemView);
            itemView.setTag(R.id.tag_itemAnimator, animator);
        } else if (animator.isRunning()) {
            animator.removeListener(animator.listener);
            animator.cancel();
            canceled = true;
        }
        animator.setFloatValues(new float[]{0.0f, dx});
        boolean rtl = Utils.isLayoutRtl(itemView);
        animator.setInterpolator(((rtl || dx >= 0.0f) && (!rtl || dx <= 0.0f)) ? sViscousFluidInterpolator : sOvershootInterpolator);
        animator.setDuration((long) duration);
        animator.start();
        if (canceled) {
            animator.addListener(animator.listener);
        }
    }

    private void translateItemViewXTo(ViewGroup itemView, float x) {
        translateItemViewXBy(itemView, x - itemView.getChildAt(0).getTranslationX());
    }

    private void translateItemViewXBy(ViewGroup itemView, float dx) {
        TranslateItemViewXAnimator animator = (TranslateItemViewXAnimator) itemView.getTag(R.id.tag_itemAnimator);
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        baseTranslateItemViewXBy(itemView, dx);
    }

    /* access modifiers changed from: private */
    public void baseTranslateItemViewXBy(ViewGroup itemView, float dx) {
        if (dx != 0.0f) {
            float translationX = itemView.getChildAt(0).getTranslationX() + dx;
            int itemMenuWidth = ((Integer) itemView.getTag(R.id.tag_itemMenuWidth)).intValue();
            boolean rtl = Utils.isLayoutRtl(itemView);
            if ((!rtl && translationX > ((float) (-itemMenuWidth)) * 0.05f) || (rtl && translationX < ((float) itemMenuWidth) * 0.05f)) {
                this.mOpenedItems.remove(itemView);
            } else if (!this.mOpenedItems.contains(itemView)) {
                this.mOpenedItems.add(itemView);
            }
            int itemChildCount = itemView.getChildCount();
            for (int i = 0; i < itemChildCount; i++) {
                itemView.getChildAt(i).setTranslationX(translationX);
            }
            FrameLayout itemMenu = (FrameLayout) itemView.getChildAt(itemChildCount - 1);
            int[] menuItemWidths = (int[]) itemView.getTag(R.id.tag_menuItemWidths);
            float menuItemFrameDx = 0.0f;
            int menuItemCount = itemMenu.getChildCount();
            for (int i2 = 1; i2 < menuItemCount; i2++) {
                FrameLayout menuItemFrame = (FrameLayout) itemMenu.getChildAt(i2);
                menuItemFrameDx -= (((float) menuItemWidths[i2 - 1]) * dx) / ((float) itemMenuWidth);
                menuItemFrame.setTranslationX(menuItemFrame.getTranslationX() + menuItemFrameDx);
            }
        }
    }

    private static final class TranslateItemViewXAnimator extends ValueAnimator {
        float cachedDeltaTransX;
        final Animator.AnimatorListener listener;

        TranslateItemViewXAnimator(final SlidingItemMenuRecyclerView parent, final ViewGroup itemView) {
            AnonymousClass1 r0 = new AnimatorListenerAdapter() {
                final SimpleArrayMap<View, Integer> childrenLayerTypes = new SimpleArrayMap<>(0);

                /* access modifiers changed from: package-private */
                public void ensureChildrenLayerTypes() {
                    int itemChildCount = itemView.getChildCount();
                    ViewGroup itemMenu = (ViewGroup) itemView.getChildAt(itemChildCount - 1);
                    int menuItemCount = itemMenu.getChildCount();
                    this.childrenLayerTypes.clear();
                    this.childrenLayerTypes.ensureCapacity((itemChildCount - 1) + menuItemCount);
                    for (int i = 0; i < itemChildCount - 1; i++) {
                        View itemChild = itemView.getChildAt(i);
                        this.childrenLayerTypes.put(itemChild, Integer.valueOf(itemChild.getLayerType()));
                    }
                    for (int i2 = 0; i2 < menuItemCount; i2++) {
                        View menuItemFrame = itemMenu.getChildAt(i2);
                        this.childrenLayerTypes.put(menuItemFrame, Integer.valueOf(menuItemFrame.getLayerType()));
                    }
                }

                public void onAnimationStart(Animator animation) {
                    ensureChildrenLayerTypes();
                    for (int i = this.childrenLayerTypes.size() - 1; i >= 0; i--) {
                        View child = this.childrenLayerTypes.keyAt(i);
                        child.setLayerType(2, (Paint) null);
                        if (Build.VERSION.SDK_INT >= 12 && ViewCompat.isAttachedToWindow(child)) {
                            child.buildLayer();
                        }
                    }
                }

                public void onAnimationEnd(Animator animation) {
                    for (int i = this.childrenLayerTypes.size() - 1; i >= 0; i--) {
                        this.childrenLayerTypes.keyAt(i).setLayerType(this.childrenLayerTypes.valueAt(i).intValue(), (Paint) null);
                    }
                }
            };
            this.listener = r0;
            addListener(r0);
            addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float deltaTransX = ((Float) animation.getAnimatedValue()).floatValue();
                    parent.baseTranslateItemViewXBy(itemView, deltaTransX - TranslateItemViewXAnimator.this.cachedDeltaTransX);
                    TranslateItemViewXAnimator.this.cachedDeltaTransX = deltaTransX;
                }
            });
        }

        public void start() {
            this.cachedDeltaTransX = 0.0f;
            super.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseItemViewInternal(this.mFullyOpenedItem, 0);
        if (this.mOpenedItems.size() > 0) {
            for (ViewGroup openedItem : (ViewGroup[]) this.mOpenedItems.toArray(new ViewGroup[0])) {
                Animator animator = (Animator) openedItem.getTag(R.id.tag_itemAnimator);
                if (animator != null && animator.isRunning()) {
                    animator.end();
                }
            }
            this.mOpenedItems.clear();
        }
    }
}
