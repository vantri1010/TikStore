package im.bclpbkiauv.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import im.bclpbkiauv.messenger.AndroidUtilities;

public class PagerSlidingTabStrip extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    protected int currentPosition = 0;
    protected float currentPositionOffset = 0.0f;
    protected LinearLayout.LayoutParams defaultTabLayoutParams;
    public ViewPager.OnPageChangeListener delegatePageListener;
    protected int dividerPadding = AndroidUtilities.dp(12.0f);
    protected int indicatorColor = -10066330;
    protected int indicatorHeight = AndroidUtilities.dp(8.0f);
    protected int lastScrollX = 0;
    protected ViewPager pager;
    protected Paint rectPaint;
    protected int scrollOffset = AndroidUtilities.dp(52.0f);
    protected boolean shouldExpand = false;
    protected int tabCount;
    protected int tabPadding = AndroidUtilities.dp(24.0f);
    protected LinearLayout tabsContainer;
    protected int underlineColor = 436207616;
    protected int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface IconTabProvider {
        boolean canScrollToTab(int i);

        void customOnDraw(Canvas canvas, int i);

        Drawable getPageIconDrawable(int i);
    }

    public PagerSlidingTabStrip(Context context) {
        super(context);
        setFillViewport(true);
        setWillNotDraw(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.tabsContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(-2, -1);
    }

    public void setViewPager(ViewPager pager2) {
        this.pager = pager2;
        if (pager2.getAdapter() != null) {
            pager2.setOnPageChangeListener(this);
            notifyDataSetChanged();
            return;
        }
        throw new IllegalStateException("ViewPager does not have adapter instance.");
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        this.tabsContainer.removeAllViews();
        this.tabCount = this.pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconDrawable(i), this.pager.getAdapter().getPageTitle(i));
            }
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                PagerSlidingTabStrip.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                PagerSlidingTabStrip pagerSlidingTabStrip = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip.currentPosition = pagerSlidingTabStrip.pager.getCurrentItem();
                PagerSlidingTabStrip pagerSlidingTabStrip2 = PagerSlidingTabStrip.this;
                pagerSlidingTabStrip2.scrollToChild(pagerSlidingTabStrip2.currentPosition, 0);
            }
        });
    }

    public View getTab(int position) {
        if (position < 0 || position >= this.tabsContainer.getChildCount()) {
            return null;
        }
        return this.tabsContainer.getChildAt(position);
    }

    /* access modifiers changed from: protected */
    public void addIconTab(final int position, Drawable drawable, CharSequence contentDescription) {
        ImageView tab = new ImageView(getContext()) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (PagerSlidingTabStrip.this.pager.getAdapter() instanceof IconTabProvider) {
                    ((IconTabProvider) PagerSlidingTabStrip.this.pager.getAdapter()).customOnDraw(canvas, position);
                }
            }
        };
        boolean z = true;
        tab.setFocusable(true);
        tab.setImageDrawable(drawable);
        tab.setScaleType(ImageView.ScaleType.CENTER);
        tab.setOnClickListener(new View.OnClickListener(position) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                PagerSlidingTabStrip.this.lambda$addIconTab$0$PagerSlidingTabStrip(this.f$1, view);
            }
        });
        this.tabsContainer.addView(tab);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        tab.setContentDescription(contentDescription);
    }

    public /* synthetic */ void lambda$addIconTab$0$PagerSlidingTabStrip(int position, View v) {
        if (!(this.pager.getAdapter() instanceof IconTabProvider) || ((IconTabProvider) this.pager.getAdapter()).canScrollToTab(position)) {
            this.pager.setCurrentItem(position, false);
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            v.setLayoutParams(this.defaultTabLayoutParams);
            if (this.shouldExpand) {
                v.setPadding(0, 0, 0, 0);
                v.setLayoutParams(new LinearLayout.LayoutParams(-1, -1, 1.0f));
            } else {
                int i2 = this.tabPadding;
                v.setPadding(i2, 0, i2, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.shouldExpand && View.MeasureSpec.getMode(widthMeasureSpec) != 0) {
            this.tabsContainer.measure(1073741824 | getMeasuredWidth(), heightMeasureSpec);
        }
    }

    public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        if (!this.shouldExpand) {
            post(new Runnable() {
                public final void run() {
                    PagerSlidingTabStrip.this.notifyDataSetChanged();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void scrollToChild(int position, int offset) {
        if (this.tabCount != 0) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
            if (position > 0 || offset > 0) {
                newScrollX -= this.scrollOffset;
            }
            if (newScrollX != this.lastScrollX) {
                this.lastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            int height = getHeight();
            if (this.underlineHeight != 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, (float) (height - this.underlineHeight), (float) this.tabsContainer.getWidth(), (float) height, this.rectPaint);
            }
            View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
            float lineLeft = (float) currentTab.getLeft();
            float lineRight = (float) currentTab.getRight();
            if (this.currentPositionOffset > 0.0f && (i = this.currentPosition) < this.tabCount - 1) {
                View nextTab = this.tabsContainer.getChildAt(i + 1);
                float f = this.currentPositionOffset;
                lineLeft = (f * ((float) nextTab.getLeft())) + ((1.0f - f) * lineLeft);
                lineRight = (f * ((float) nextTab.getRight())) + ((1.0f - f) * lineRight);
            }
            if (this.indicatorHeight != 0) {
                this.rectPaint.setColor(this.indicatorColor);
                canvas.drawRect(lineLeft, (float) (height - this.indicatorHeight), lineRight, (float) height, this.rectPaint);
            }
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.currentPosition = position;
        this.currentPositionOffset = positionOffset;
        scrollToChild(position, (int) (((float) this.tabsContainer.getChildAt(position).getWidth()) * positionOffset));
        invalidate();
        ViewPager.OnPageChangeListener onPageChangeListener = this.delegatePageListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        ViewPager.OnPageChangeListener onPageChangeListener = this.delegatePageListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(position);
        }
        int a = 0;
        while (a < this.tabsContainer.getChildCount()) {
            this.tabsContainer.getChildAt(a).setSelected(a == position);
            a++;
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            scrollToChild(this.pager.getCurrentItem(), 0);
        }
        ViewPager.OnPageChangeListener onPageChangeListener = this.delegatePageListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void setIndicatorColor(int indicatorColor2) {
        this.indicatorColor = indicatorColor2;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor2) {
        this.underlineColor = underlineColor2;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return this.underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return this.dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand2) {
        this.shouldExpand = shouldExpand2;
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        updateTabStyles();
        requestLayout();
    }

    public boolean getShouldExpand() {
        return this.shouldExpand;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }
}
