package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class ScrollSlidingTextTabStrip extends HorizontalScrollView {
    public static final int STYLE_BLOCK = 2;
    public static final int STYLE_NORMAL = 0;
    private String activeTextColorKey;
    private int allTextWidth;
    private int animateIndicatorStartWidth;
    private int animateIndicatorStartX;
    private int animateIndicatorToWidth;
    private int animateIndicatorToX;
    /* access modifiers changed from: private */
    public boolean animatingIndicator;
    private float animationIdicatorProgress;
    /* access modifiers changed from: private */
    public Runnable animationRunnable;
    private boolean animationRunning;
    /* access modifiers changed from: private */
    public float animationTime;
    private int currentPosition;
    /* access modifiers changed from: private */
    public ScrollSlidingTabStripDelegate delegate;
    private SparseIntArray idToPosition;
    private int indicatorWidth;
    private int indicatorX;
    /* access modifiers changed from: private */
    public CubicBezierInterpolator interpolator;
    /* access modifiers changed from: private */
    public long lastAnimationTime;
    private float mIndicatorCornerRadius;
    public int mIndicatorStyle;
    private SparseIntArray positionToId;
    private SparseIntArray positionToWidth;
    private int prevLayoutWidth;
    private int previousPosition;
    private int selectedTabId;
    private String selectorColorKey;
    private GradientDrawable selectorDrawable;
    private int tabCount;
    private String tabLineColorKey;
    private LinearLayout tabsContainer;
    private String unactiveTextColorKey;
    private boolean useSameWidth;

    public interface ScrollSlidingTabStripDelegate {
        void onPageScrolled(float f);

        void onPageSelected(int i, boolean z);
    }

    public ScrollSlidingTextTabStrip(Context context) {
        super(context);
        this.selectedTabId = -1;
        this.tabLineColorKey = Theme.key_actionBarTabLine;
        this.activeTextColorKey = Theme.key_actionBarTabUnactiveText;
        this.unactiveTextColorKey = Theme.key_actionBarTabActiveText;
        this.selectorColorKey = Theme.key_actionBarTabSelector;
        this.interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.positionToId = new SparseIntArray(5);
        this.idToPosition = new SparseIntArray(5);
        this.positionToWidth = new SparseIntArray(5);
        this.animationRunnable = new Runnable() {
            public void run() {
                if (ScrollSlidingTextTabStrip.this.animatingIndicator) {
                    long dt = SystemClock.elapsedRealtime() - ScrollSlidingTextTabStrip.this.lastAnimationTime;
                    if (dt > 17) {
                        dt = 17;
                    }
                    ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = ScrollSlidingTextTabStrip.this;
                    float unused = scrollSlidingTextTabStrip.animationTime = scrollSlidingTextTabStrip.animationTime + (((float) dt) / 200.0f);
                    ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = ScrollSlidingTextTabStrip.this;
                    scrollSlidingTextTabStrip2.setAnimationIdicatorProgress(scrollSlidingTextTabStrip2.interpolator.getInterpolation(ScrollSlidingTextTabStrip.this.animationTime));
                    if (ScrollSlidingTextTabStrip.this.animationTime > 1.0f) {
                        float unused2 = ScrollSlidingTextTabStrip.this.animationTime = 1.0f;
                    }
                    if (ScrollSlidingTextTabStrip.this.animationTime < 1.0f) {
                        AndroidUtilities.runOnUIThread(ScrollSlidingTextTabStrip.this.animationRunnable);
                        return;
                    }
                    boolean unused3 = ScrollSlidingTextTabStrip.this.animatingIndicator = false;
                    ScrollSlidingTextTabStrip.this.setEnabled(true);
                    if (ScrollSlidingTextTabStrip.this.delegate != null) {
                        ScrollSlidingTextTabStrip.this.delegate.onPageScrolled(1.0f);
                    }
                }
            }
        };
        this.mIndicatorStyle = 0;
        this.selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
        float dpf2 = AndroidUtilities.dpf2(3.0f);
        this.mIndicatorCornerRadius = dpf2;
        this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
        this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
    }

    public ScrollSlidingTextTabStrip(Context context, int mIndicatorStyle2) {
        super(context);
        this.selectedTabId = -1;
        this.tabLineColorKey = Theme.key_actionBarTabLine;
        this.activeTextColorKey = Theme.key_actionBarTabUnactiveText;
        this.unactiveTextColorKey = Theme.key_actionBarTabActiveText;
        this.selectorColorKey = Theme.key_actionBarTabSelector;
        this.interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.positionToId = new SparseIntArray(5);
        this.idToPosition = new SparseIntArray(5);
        this.positionToWidth = new SparseIntArray(5);
        this.animationRunnable = new Runnable() {
            public void run() {
                if (ScrollSlidingTextTabStrip.this.animatingIndicator) {
                    long dt = SystemClock.elapsedRealtime() - ScrollSlidingTextTabStrip.this.lastAnimationTime;
                    if (dt > 17) {
                        dt = 17;
                    }
                    ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = ScrollSlidingTextTabStrip.this;
                    float unused = scrollSlidingTextTabStrip.animationTime = scrollSlidingTextTabStrip.animationTime + (((float) dt) / 200.0f);
                    ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = ScrollSlidingTextTabStrip.this;
                    scrollSlidingTextTabStrip2.setAnimationIdicatorProgress(scrollSlidingTextTabStrip2.interpolator.getInterpolation(ScrollSlidingTextTabStrip.this.animationTime));
                    if (ScrollSlidingTextTabStrip.this.animationTime > 1.0f) {
                        float unused2 = ScrollSlidingTextTabStrip.this.animationTime = 1.0f;
                    }
                    if (ScrollSlidingTextTabStrip.this.animationTime < 1.0f) {
                        AndroidUtilities.runOnUIThread(ScrollSlidingTextTabStrip.this.animationRunnable);
                        return;
                    }
                    boolean unused3 = ScrollSlidingTextTabStrip.this.animatingIndicator = false;
                    ScrollSlidingTextTabStrip.this.setEnabled(true);
                    if (ScrollSlidingTextTabStrip.this.delegate != null) {
                        ScrollSlidingTextTabStrip.this.delegate.onPageScrolled(1.0f);
                    }
                }
            }
        };
        this.mIndicatorStyle = mIndicatorStyle2;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
        this.selectorDrawable = gradientDrawable;
        gradientDrawable.setColor(Theme.getColor(this.tabLineColorKey));
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public boolean isAnimatingIndicator() {
        return this.animatingIndicator;
    }

    private void setAnimationProgressInernal(TextView newTab, TextView prevTab, float value) {
        int newColor = Theme.getColor(this.activeTextColorKey);
        int prevColor = Theme.getColor(this.unactiveTextColorKey);
        int r1 = Color.red(newColor);
        int g1 = Color.green(newColor);
        int b1 = Color.blue(newColor);
        int a1 = Color.alpha(newColor);
        int r2 = Color.red(prevColor);
        int g2 = Color.green(prevColor);
        int b2 = Color.blue(prevColor);
        int a2 = Color.alpha(prevColor);
        prevTab.setTextColor(Color.argb((int) (((float) a1) + (((float) (a2 - a1)) * value)), (int) (((float) r1) + (((float) (r2 - r1)) * value)), (int) (((float) g1) + (((float) (g2 - g1)) * value)), (int) (((float) b1) + (((float) (b2 - b1)) * value))));
        int i = newColor;
        newTab.setTextColor(Color.argb((int) (((float) a2) + (((float) (a1 - a2)) * value)), (int) (((float) r2) + (((float) (r1 - r2)) * value)), (int) (((float) g2) + (((float) (g1 - g2)) * value)), (int) (((float) b2) + (((float) (b1 - b2)) * value))));
        int i2 = this.animateIndicatorStartX;
        this.indicatorX = (int) (((float) i2) + (((float) (this.animateIndicatorToX - i2)) * value));
        int i3 = this.animateIndicatorStartWidth;
        this.indicatorWidth = (int) (((float) i3) + (((float) (this.animateIndicatorToWidth - i3)) * value));
        invalidate();
    }

    public void setAnimationIdicatorProgress(float value) {
        this.animationIdicatorProgress = value;
        setAnimationProgressInernal((TextView) this.tabsContainer.getChildAt(this.currentPosition), (TextView) this.tabsContainer.getChildAt(this.previousPosition), value);
        ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
        if (scrollSlidingTabStripDelegate != null) {
            scrollSlidingTabStripDelegate.onPageScrolled(value);
        }
    }

    public void setUseSameWidth(boolean value) {
        this.useSameWidth = value;
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public View getTabsContainer() {
        return this.tabsContainer;
    }

    public float getAnimationIdicatorProgress() {
        return this.animationIdicatorProgress;
    }

    public int getNextPageId(boolean forward) {
        return this.positionToId.get(this.currentPosition + (forward ? 1 : -1), -1);
    }

    public void removeTabs() {
        this.positionToId.clear();
        this.idToPosition.clear();
        this.positionToWidth.clear();
        this.tabsContainer.removeAllViews();
        this.allTextWidth = 0;
        this.tabCount = 0;
    }

    public int getTabsCount() {
        return this.tabCount;
    }

    public boolean hasTab(int id) {
        return this.idToPosition.get(id, -1) != -1;
    }

    public void addTextTab(int id, CharSequence text) {
        int position = this.tabCount;
        this.tabCount = position + 1;
        if (position == 0 && this.selectedTabId == -1) {
            this.selectedTabId = id;
        }
        this.positionToId.put(position, id);
        this.idToPosition.put(id, position);
        int i = this.selectedTabId;
        if (i != -1 && i == id) {
            this.currentPosition = position;
            this.prevLayoutWidth = 0;
        }
        TextView tab = new TextView(getContext());
        tab.setGravity(17);
        tab.setText(text);
        String str = this.selectorColorKey;
        if (str != null) {
            tab.setBackground(Theme.createSelectorDrawable(Theme.getColor(str), 3));
        }
        tab.setTextSize(1, 14.0f);
        tab.setSingleLine(true);
        tab.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        tab.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        tab.setOnClickListener(new View.OnClickListener(id) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ScrollSlidingTextTabStrip.this.lambda$addTextTab$0$ScrollSlidingTextTabStrip(this.f$1, view);
            }
        });
        int tabWidth = ((int) Math.ceil((double) tab.getPaint().measureText(text, 0, text.length()))) + AndroidUtilities.dp(16.0f);
        this.allTextWidth += tabWidth;
        this.positionToWidth.put(position, tabWidth);
        this.tabsContainer.addView(tab, LayoutHelper.createLinear(0, -1));
    }

    public /* synthetic */ void lambda$addTextTab$0$ScrollSlidingTextTabStrip(int id, View v) {
        int i;
        int position1 = this.tabsContainer.indexOfChild(v);
        if (position1 >= 0 && position1 != (i = this.currentPosition)) {
            boolean scrollingForward = i < position1;
            this.previousPosition = this.currentPosition;
            this.currentPosition = position1;
            this.selectedTabId = id;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
            }
            this.animationTime = 0.0f;
            this.animatingIndicator = true;
            this.animateIndicatorStartX = this.indicatorX;
            this.animateIndicatorStartWidth = this.indicatorWidth;
            TextView nextChild = (TextView) v;
            this.animateIndicatorToWidth = getChildWidth(nextChild);
            this.animateIndicatorToX = nextChild.getLeft() + ((nextChild.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
            setEnabled(false);
            AndroidUtilities.runOnUIThread(this.animationRunnable, 16);
            ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
            if (scrollSlidingTabStripDelegate != null) {
                scrollSlidingTabStripDelegate.onPageSelected(id, scrollingForward);
            }
            scrollToChild(position1);
        }
    }

    public void finishAddingTabs() {
        int count = this.tabsContainer.getChildCount();
        int a = 0;
        while (a < count) {
            TextView tab = (TextView) this.tabsContainer.getChildAt(a);
            tab.setTag(this.currentPosition == a ? this.activeTextColorKey : this.unactiveTextColorKey);
            tab.setTextColor(Theme.getColor(this.currentPosition == a ? this.activeTextColorKey : this.unactiveTextColorKey));
            a++;
        }
    }

    public void setColors(String line, String active, String unactive, String selector) {
        this.tabLineColorKey = line;
        this.activeTextColorKey = active;
        this.unactiveTextColorKey = unactive;
        this.selectorColorKey = selector;
        this.selectorDrawable.setColor(Theme.getColor(line));
    }

    public int getCurrentTabId() {
        return this.selectedTabId;
    }

    public void setInitialTabId(int id) {
        this.selectedTabId = id;
    }

    public int getFirstTabId() {
        return this.positionToId.get(0, 0);
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int height = getMeasuredHeight();
        if (child == this.tabsContainer) {
            int i = this.mIndicatorStyle;
            if (i == 0) {
                this.selectorDrawable.setBounds(this.indicatorX, height - AndroidUtilities.dpr(4.0f), this.indicatorX + this.indicatorWidth, height);
                this.selectorDrawable.draw(canvas);
            } else if (i == 2) {
                this.mIndicatorCornerRadius = (float) (height / 2);
                this.selectorDrawable.setBounds(this.indicatorX - AndroidUtilities.dp(13.0f), AndroidUtilities.dpr(5.0f), this.indicatorX + this.indicatorWidth + AndroidUtilities.dp(13.0f), height - AndroidUtilities.dp(5.0f));
                this.selectorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                this.selectorDrawable.draw(canvas);
            }
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int count = this.tabsContainer.getChildCount();
        for (int a = 0; a < count; a++) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.tabsContainer.getChildAt(a).getLayoutParams();
            int i = this.allTextWidth;
            if (i > width) {
                layoutParams.weight = 0.0f;
                layoutParams.width = -2;
            } else if (this.useSameWidth) {
                layoutParams.weight = 1.0f / ((float) count);
                layoutParams.width = 0;
            } else {
                layoutParams.weight = (1.0f / ((float) i)) * ((float) this.positionToWidth.get(a));
                layoutParams.width = 0;
            }
        }
        if (this.allTextWidth > width) {
            this.tabsContainer.setWeightSum(0.0f);
        } else {
            this.tabsContainer.setWeightSum(1.0f);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void scrollToChild(int position) {
        TextView child;
        if (this.tabCount != 0 && (child = (TextView) this.tabsContainer.getChildAt(position)) != null) {
            int currentScrollX = getScrollX();
            int left = child.getLeft();
            int width = child.getMeasuredWidth();
            if (left < currentScrollX) {
                smoothScrollTo(left, 0);
            } else if (left + width > getWidth() + currentScrollX) {
                smoothScrollTo(left + width, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.prevLayoutWidth != r - l) {
            this.prevLayoutWidth = r - l;
            if (this.animatingIndicator) {
                AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                this.animatingIndicator = false;
                setEnabled(true);
                ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate = this.delegate;
                if (scrollSlidingTabStripDelegate != null) {
                    scrollSlidingTabStripDelegate.onPageScrolled(1.0f);
                }
            }
            TextView child = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            if (child != null) {
                this.indicatorWidth = getChildWidth(child);
                this.indicatorX = child.getLeft() + ((child.getMeasuredWidth() - this.indicatorWidth) / 2);
            }
        }
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        int count = this.tabsContainer.getChildCount();
        for (int a = 0; a < count; a++) {
            this.tabsContainer.getChildAt(a).setEnabled(enabled);
        }
    }

    public void selectTabWithId(int id, float progress) {
        int position = this.idToPosition.get(id, -1);
        if (position >= 0) {
            if (progress < 0.0f) {
                progress = 0.0f;
            } else if (progress > 1.0f) {
                progress = 1.0f;
            }
            TextView child = (TextView) this.tabsContainer.getChildAt(this.currentPosition);
            TextView nextChild = (TextView) this.tabsContainer.getChildAt(position);
            if (!(child == null || nextChild == null)) {
                this.animateIndicatorStartWidth = getChildWidth(child);
                this.animateIndicatorStartX = child.getLeft() + ((child.getMeasuredWidth() - this.animateIndicatorStartWidth) / 2);
                this.animateIndicatorToWidth = getChildWidth(nextChild);
                this.animateIndicatorToX = nextChild.getLeft() + ((nextChild.getMeasuredWidth() - this.animateIndicatorToWidth) / 2);
                setAnimationProgressInernal(nextChild, child, progress);
            }
            if (progress >= 1.0f) {
                this.currentPosition = position;
                this.selectedTabId = id;
            }
        }
    }

    private int getChildWidth(TextView child) {
        Layout layout = child.getLayout();
        if (layout != null) {
            return ((int) Math.ceil((double) layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
        }
        return child.getMeasuredWidth();
    }

    public void onPageScrolled(int position, int first) {
        if (this.currentPosition != position) {
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                int a = 0;
                while (true) {
                    boolean z = true;
                    if (a >= this.tabsContainer.getChildCount()) {
                        break;
                    }
                    View childAt = this.tabsContainer.getChildAt(a);
                    if (a != position) {
                        z = false;
                    }
                    childAt.setSelected(z);
                    a++;
                }
                if (first != position || position <= 1) {
                    scrollToChild(position);
                } else {
                    scrollToChild(position - 1);
                }
                invalidate();
            }
        }
    }
}
