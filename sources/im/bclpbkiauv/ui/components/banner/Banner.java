package im.bclpbkiauv.ui.components.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.banner.adapter.BannerAdapter;
import im.bclpbkiauv.ui.components.banner.config.BannerConfig;
import im.bclpbkiauv.ui.components.banner.config.IndicatorConfig;
import im.bclpbkiauv.ui.components.banner.indicator.Indicator;
import im.bclpbkiauv.ui.components.banner.listener.OnBannerListener;
import im.bclpbkiauv.ui.components.banner.listener.OnPageChangeListener;
import im.bclpbkiauv.ui.components.banner.transformer.MZScaleInTransformer;
import im.bclpbkiauv.ui.components.banner.transformer.ScaleInTransformer;
import im.bclpbkiauv.ui.components.banner.util.BannerLifecycleObserver;
import im.bclpbkiauv.ui.components.banner.util.BannerLifecycleObserverAdapter;
import im.bclpbkiauv.ui.components.banner.util.BannerUtils;
import im.bclpbkiauv.ui.components.banner.util.ScrollSpeedManger;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;

public class Banner<T, BA extends BannerAdapter<T, ? extends RecyclerView.ViewHolder>> extends FrameLayout implements BannerLifecycleObserver {
    public static final int HORIZONTAL = 0;
    public static final int INVALID_VALUE = -1;
    public static final int VERTICAL = 1;
    private int indicatorGravity;
    private float indicatorHeight;
    private int indicatorMargin;
    private int indicatorMarginBottom;
    private int indicatorMarginLeft;
    private int indicatorMarginRight;
    private int indicatorMarginTop;
    private float indicatorRadius;
    private float indicatorSpace;
    private boolean isIntercept;
    private BA mAdapter;
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private float mBannerRadius;
    private CompositePageTransformer mCompositePageTransformer;
    private Paint mImagePaint;
    private Indicator mIndicator;
    /* access modifiers changed from: private */
    public boolean mIsAutoLoop;
    /* access modifiers changed from: private */
    public boolean mIsInfiniteLoop;
    private boolean mIsViewPager2Drag;
    /* access modifiers changed from: private */
    public AutoLoopTask mLoopTask;
    /* access modifiers changed from: private */
    public long mLoopTime;
    /* access modifiers changed from: private */
    public OnPageChangeListener mOnPageChangeListener;
    private int mOrientation;
    private Banner<T, BA>.BannerOnPageChangeCallback mPageChangeCallback;
    private boolean mRoundBottomLeft;
    private boolean mRoundBottomRight;
    private Paint mRoundPaint;
    private boolean mRoundTopLeft;
    private boolean mRoundTopRight;
    private int mScrollTime;
    private int mStartPosition;
    private float mStartX;
    private float mStartY;
    private int mTouchSlop;
    private ViewPager2 mViewPager2;
    private int normalColor;
    private float normalWidth;
    private int selectedColor;
    private float selectedWidth;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    public Banner(Context context) {
        this(context, (AttributeSet) null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIsInfiniteLoop = true;
        this.mIsAutoLoop = true;
        this.mLoopTime = 3000;
        this.mScrollTime = BannerConfig.SCROLL_TIME;
        this.mStartPosition = 1;
        this.mBannerRadius = 0.0f;
        this.normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH;
        this.selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH;
        this.normalColor = BannerConfig.INDICATOR_NORMAL_COLOR;
        this.selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR;
        this.indicatorGravity = 1;
        this.indicatorHeight = BannerConfig.INDICATOR_HEIGHT;
        this.indicatorRadius = BannerConfig.INDICATOR_RADIUS;
        this.mOrientation = 0;
        this.isIntercept = true;
        this.mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
            public void onChanged() {
                if (Banner.this.getItemCount() <= 1) {
                    Banner.this.stop();
                } else {
                    Banner.this.start();
                }
                Banner.this.setIndicatorPageChange();
            }
        };
        init(context);
        initTypedArray(context, attrs);
    }

    private void init(Context context) {
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
        this.mCompositePageTransformer = new CompositePageTransformer();
        this.mPageChangeCallback = new BannerOnPageChangeCallback();
        this.mLoopTask = new AutoLoopTask(this);
        ViewPager2 viewPager2 = new ViewPager2(context);
        this.mViewPager2 = viewPager2;
        viewPager2.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.mViewPager2.setOffscreenPageLimit(2);
        this.mViewPager2.registerOnPageChangeCallback(this.mPageChangeCallback);
        this.mViewPager2.setPageTransformer(this.mCompositePageTransformer);
        ScrollSpeedManger.reflectLayoutManager(this);
        addView(this.mViewPager2);
        Paint paint = new Paint();
        this.mRoundPaint = paint;
        paint.setColor(-1);
        this.mRoundPaint.setAntiAlias(true);
        this.mRoundPaint.setStyle(Paint.Style.FILL);
        this.mRoundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint paint2 = new Paint();
        this.mImagePaint = paint2;
        paint2.setXfermode((Xfermode) null);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Banner);
            this.mBannerRadius = (float) a.getDimensionPixelSize(17, 0);
            this.mLoopTime = (long) a.getInt(15, 3000);
            this.mIsAutoLoop = a.getBoolean(0, true);
            this.mIsInfiniteLoop = a.getBoolean(14, true);
            this.normalWidth = a.getDimension(9, BannerConfig.INDICATOR_NORMAL_WIDTH);
            this.selectedWidth = a.getDimension(12, BannerConfig.INDICATOR_SELECTED_WIDTH);
            this.normalColor = a.getColor(8, BannerConfig.INDICATOR_NORMAL_COLOR);
            this.selectedColor = a.getColor(11, BannerConfig.INDICATOR_SELECTED_COLOR);
            this.indicatorGravity = a.getInt(1, 1);
            this.indicatorSpace = a.getDimension(13, 0.0f);
            this.indicatorMargin = a.getDimensionPixelSize(3, 0);
            this.indicatorMarginLeft = a.getDimensionPixelSize(5, 0);
            this.indicatorMarginTop = a.getDimensionPixelSize(7, 0);
            this.indicatorMarginRight = a.getDimensionPixelSize(6, 0);
            this.indicatorMarginBottom = a.getDimensionPixelSize(4, 0);
            this.indicatorHeight = a.getDimension(2, BannerConfig.INDICATOR_HEIGHT);
            this.indicatorRadius = a.getDimension(10, BannerConfig.INDICATOR_RADIUS);
            this.mOrientation = a.getInt(16, 0);
            this.mRoundTopLeft = a.getBoolean(20, false);
            this.mRoundTopRight = a.getBoolean(21, false);
            this.mRoundBottomLeft = a.getBoolean(18, false);
            this.mRoundBottomRight = a.getBoolean(19, false);
            a.recycle();
        }
        setOrientation(this.mOrientation);
        setInfiniteLoop();
    }

    private void initIndicatorAttr() {
        int i = this.indicatorMargin;
        if (i != 0) {
            setIndicatorMargins(new IndicatorConfig.Margins(i));
        } else if (!(this.indicatorMarginLeft == 0 && this.indicatorMarginTop == 0 && this.indicatorMarginRight == 0 && this.indicatorMarginBottom == 0)) {
            setIndicatorMargins(new IndicatorConfig.Margins(this.indicatorMarginLeft, this.indicatorMarginTop, this.indicatorMarginRight, this.indicatorMarginBottom));
        }
        float f = this.indicatorSpace;
        if (f > 0.0f) {
            setIndicatorSpace(f);
        }
        int i2 = this.indicatorGravity;
        if (i2 != 1) {
            setIndicatorGravity(i2);
        }
        float f2 = this.normalWidth;
        if (f2 > 0.0f) {
            setIndicatorNormalWidth(f2);
        }
        float f3 = this.selectedWidth;
        if (f3 > 0.0f) {
            setIndicatorSelectedWidth(f3);
        }
        float f4 = this.indicatorHeight;
        if (f4 > 0.0f) {
            setIndicatorHeight(f4);
        }
        float f5 = this.indicatorRadius;
        if (f5 > 0.0f) {
            setIndicatorRadius(f5);
        }
        setIndicatorNormalColor(this.normalColor);
        setIndicatorSelectedColor(this.selectedColor);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!getViewPager2().isUserInputEnabled()) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getActionMasked();
        if (action == 1 || action == 3 || action == 4) {
            start();
        } else if (action == 0) {
            stop();
        }
        return super.dispatchTouchEvent(ev);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001e, code lost:
        if (r0 != 3) goto L_0x0088;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            androidx.viewpager2.widget.ViewPager2 r0 = r7.getViewPager2()
            boolean r0 = r0.isUserInputEnabled()
            if (r0 == 0) goto L_0x008d
            boolean r0 = r7.isIntercept
            if (r0 != 0) goto L_0x0010
            goto L_0x008d
        L_0x0010:
            int r0 = r8.getAction()
            r1 = 1
            if (r0 == 0) goto L_0x0074
            r2 = 0
            if (r0 == r1) goto L_0x006c
            r3 = 2
            if (r0 == r3) goto L_0x0021
            r1 = 3
            if (r0 == r1) goto L_0x006c
            goto L_0x0088
        L_0x0021:
            float r0 = r8.getX()
            float r3 = r8.getY()
            float r4 = r7.mStartX
            float r4 = r0 - r4
            float r4 = java.lang.Math.abs(r4)
            float r5 = r7.mStartY
            float r5 = r3 - r5
            float r5 = java.lang.Math.abs(r5)
            androidx.viewpager2.widget.ViewPager2 r6 = r7.getViewPager2()
            int r6 = r6.getOrientation()
            if (r6 != 0) goto L_0x0053
            int r6 = r7.mTouchSlop
            float r6 = (float) r6
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x004f
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 <= 0) goto L_0x004f
            goto L_0x0050
        L_0x004f:
            r1 = 0
        L_0x0050:
            r7.mIsViewPager2Drag = r1
            goto L_0x0062
        L_0x0053:
            int r6 = r7.mTouchSlop
            float r6 = (float) r6
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x005f
            int r6 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x005f
            goto L_0x0060
        L_0x005f:
            r1 = 0
        L_0x0060:
            r7.mIsViewPager2Drag = r1
        L_0x0062:
            android.view.ViewParent r1 = r7.getParent()
            boolean r2 = r7.mIsViewPager2Drag
            r1.requestDisallowInterceptTouchEvent(r2)
            goto L_0x0088
        L_0x006c:
            android.view.ViewParent r0 = r7.getParent()
            r0.requestDisallowInterceptTouchEvent(r2)
            goto L_0x0088
        L_0x0074:
            float r0 = r8.getX()
            r7.mStartX = r0
            float r0 = r8.getY()
            r7.mStartY = r0
            android.view.ViewParent r0 = r7.getParent()
            r0.requestDisallowInterceptTouchEvent(r1)
        L_0x0088:
            boolean r0 = super.onInterceptTouchEvent(r8)
            return r0
        L_0x008d:
            boolean r0 = super.onInterceptTouchEvent(r8)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.components.banner.Banner.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mBannerRadius > 0.0f) {
            canvas.saveLayer(new RectF(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight()), this.mImagePaint, 31);
            super.dispatchDraw(canvas);
            if (this.mRoundTopRight || this.mRoundTopLeft || this.mRoundBottomRight || this.mRoundBottomLeft) {
                if (this.mRoundTopLeft) {
                    drawTopLeft(canvas);
                }
                if (this.mRoundTopRight) {
                    drawTopRight(canvas);
                }
                if (this.mRoundBottomLeft) {
                    drawBottomLeft(canvas);
                }
                if (this.mRoundBottomRight) {
                    drawBottomRight(canvas);
                }
                canvas.restore();
                return;
            }
            drawTopLeft(canvas);
            drawTopRight(canvas);
            drawBottomLeft(canvas);
            drawBottomRight(canvas);
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    private void drawTopLeft(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0.0f, this.mBannerRadius);
        path.lineTo(0.0f, 0.0f);
        path.lineTo(this.mBannerRadius, 0.0f);
        float f = this.mBannerRadius;
        path.arcTo(new RectF(0.0f, 0.0f, f * 2.0f, f * 2.0f), -90.0f, -90.0f);
        path.close();
        canvas.drawPath(path, this.mRoundPaint);
    }

    private void drawTopRight(Canvas canvas) {
        int width = getWidth();
        Path path = new Path();
        path.moveTo(((float) width) - this.mBannerRadius, 0.0f);
        path.lineTo((float) width, 0.0f);
        path.lineTo((float) width, this.mBannerRadius);
        float f = this.mBannerRadius;
        path.arcTo(new RectF(((float) width) - (f * 2.0f), 0.0f, (float) width, f * 2.0f), 0.0f, -90.0f);
        path.close();
        canvas.drawPath(path, this.mRoundPaint);
    }

    private void drawBottomLeft(Canvas canvas) {
        int height = getHeight();
        Path path = new Path();
        path.moveTo(0.0f, ((float) height) - this.mBannerRadius);
        path.lineTo(0.0f, (float) height);
        path.lineTo(this.mBannerRadius, (float) height);
        float f = this.mBannerRadius;
        path.arcTo(new RectF(0.0f, ((float) height) - (f * 2.0f), f * 2.0f, (float) height), 90.0f, 90.0f);
        path.close();
        canvas.drawPath(path, this.mRoundPaint);
    }

    private void drawBottomRight(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        Path path = new Path();
        path.moveTo(((float) width) - this.mBannerRadius, (float) height);
        path.lineTo((float) width, (float) height);
        path.lineTo((float) width, ((float) height) - this.mBannerRadius);
        float f = this.mBannerRadius;
        path.arcTo(new RectF(((float) width) - (f * 2.0f), ((float) height) - (f * 2.0f), (float) width, (float) height), 0.0f, 90.0f);
        path.close();
        canvas.drawPath(path, this.mRoundPaint);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    class BannerOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        private boolean isScrolled;
        private int mTempPosition = -1;

        BannerOnPageChangeCallback() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = BannerUtils.getRealPosition(Banner.this.isInfiniteLoop(), position, Banner.this.getRealCount());
            if (Banner.this.mOnPageChangeListener != null && realPosition == Banner.this.getCurrentItem() - 1) {
                Banner.this.mOnPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
            if (Banner.this.getIndicator() != null && realPosition == Banner.this.getCurrentItem() - 1) {
                Banner.this.getIndicator().onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
            }
        }

        public void onPageSelected(int position) {
            if (this.isScrolled) {
                this.mTempPosition = position;
                int realPosition = BannerUtils.getRealPosition(Banner.this.isInfiniteLoop(), position, Banner.this.getRealCount());
                if (Banner.this.mOnPageChangeListener != null) {
                    Banner.this.mOnPageChangeListener.onPageSelected(realPosition);
                }
                if (Banner.this.getIndicator() != null) {
                    Banner.this.getIndicator().onPageSelected(realPosition);
                }
            }
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 1 || state == 2) {
                this.isScrolled = true;
            } else if (state == 0) {
                this.isScrolled = false;
                if (this.mTempPosition != -1 && Banner.this.mIsInfiniteLoop) {
                    int i = this.mTempPosition;
                    if (i == 0) {
                        Banner banner = Banner.this;
                        banner.setCurrentItem(banner.getRealCount(), false);
                    } else if (i == Banner.this.getItemCount() - 1) {
                        Banner.this.setCurrentItem(1, false);
                    }
                }
            }
            if (Banner.this.mOnPageChangeListener != null) {
                Banner.this.mOnPageChangeListener.onPageScrollStateChanged(state);
            }
            if (Banner.this.getIndicator() != null) {
                Banner.this.getIndicator().onPageScrollStateChanged(state);
            }
        }
    }

    static class AutoLoopTask implements Runnable {
        private final WeakReference<Banner> reference;

        AutoLoopTask(Banner banner) {
            this.reference = new WeakReference<>(banner);
        }

        public void run() {
            int count;
            Banner banner = (Banner) this.reference.get();
            if (banner != null && banner.mIsAutoLoop && (count = banner.getItemCount()) != 0) {
                banner.setCurrentItem((banner.getCurrentItem() + 1) % count);
                banner.postDelayed(banner.mLoopTask, banner.mLoopTime);
            }
        }
    }

    private void initIndicator() {
        if (getIndicator() != null) {
            if (getIndicator().getIndicatorConfig().isAttachToBanner()) {
                removeIndicator();
                addView(getIndicator().getIndicatorView());
            }
            initIndicatorAttr();
            setIndicatorPageChange();
        }
    }

    private void setInfiniteLoop() {
        int i = 0;
        if (!isInfiniteLoop()) {
            isAutoLoop(false);
        }
        if (isInfiniteLoop()) {
            i = this.mStartPosition;
        }
        setStartPosition(i);
    }

    private void setRecyclerViewPadding(int itemPadding) {
        setRecyclerViewPadding(itemPadding, itemPadding);
    }

    private void setRecyclerViewPadding(int leftItemPadding, int rightItemPadding) {
        RecyclerView recyclerView = (RecyclerView) getViewPager2().getChildAt(0);
        if (getViewPager2().getOrientation() == 1) {
            recyclerView.setPadding(this.mViewPager2.getPaddingLeft(), leftItemPadding, this.mViewPager2.getPaddingRight(), rightItemPadding);
        } else {
            recyclerView.setPadding(leftItemPadding, this.mViewPager2.getPaddingTop(), rightItemPadding, this.mViewPager2.getPaddingBottom());
        }
        recyclerView.setClipToPadding(false);
    }

    public int getCurrentItem() {
        return getViewPager2().getCurrentItem();
    }

    public int getItemCount() {
        if (getAdapter() != null) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    public int getScrollTime() {
        return this.mScrollTime;
    }

    public boolean isInfiniteLoop() {
        return this.mIsInfiniteLoop;
    }

    public BannerAdapter getAdapter() {
        return this.mAdapter;
    }

    public ViewPager2 getViewPager2() {
        return this.mViewPager2;
    }

    public Indicator getIndicator() {
        return this.mIndicator;
    }

    public IndicatorConfig getIndicatorConfig() {
        if (getIndicator() != null) {
            return getIndicator().getIndicatorConfig();
        }
        return null;
    }

    public int getRealCount() {
        if (getAdapter() != null) {
            return getAdapter().getRealCount();
        }
        return 0;
    }

    public Banner setIntercept(boolean intercept) {
        this.isIntercept = intercept;
        return this;
    }

    public Banner setCurrentItem(int position) {
        return setCurrentItem(position, true);
    }

    public Banner setCurrentItem(int position, boolean smoothScroll) {
        getViewPager2().setCurrentItem(position, smoothScroll);
        return this;
    }

    public Banner setIndicatorPageChange() {
        if (getIndicator() != null) {
            getIndicator().onPageChanged(getRealCount(), BannerUtils.getRealPosition(isInfiniteLoop(), getCurrentItem(), getRealCount()));
        }
        return this;
    }

    public Banner removeIndicator() {
        if (getIndicator() != null) {
            removeView(getIndicator().getIndicatorView());
        }
        return this;
    }

    public Banner setStartPosition(int mStartPosition2) {
        this.mStartPosition = mStartPosition2;
        return this;
    }

    public int getStartPosition() {
        return this.mStartPosition;
    }

    public Banner setUserInputEnabled(boolean enabled) {
        getViewPager2().setUserInputEnabled(enabled);
        return this;
    }

    public Banner addPageTransformer(ViewPager2.PageTransformer transformer) {
        this.mCompositePageTransformer.addTransformer(transformer);
        return this;
    }

    public Banner setPageTransformer(ViewPager2.PageTransformer transformer) {
        getViewPager2().setPageTransformer(transformer);
        return this;
    }

    public Banner removeTransformer(ViewPager2.PageTransformer transformer) {
        this.mCompositePageTransformer.removeTransformer(transformer);
        return this;
    }

    public Banner addItemDecoration(RecyclerView.ItemDecoration decor) {
        getViewPager2().addItemDecoration(decor);
        return this;
    }

    public Banner addItemDecoration(RecyclerView.ItemDecoration decor, int index) {
        getViewPager2().addItemDecoration(decor, index);
        return this;
    }

    public Banner isAutoLoop(boolean isAutoLoop) {
        this.mIsAutoLoop = isAutoLoop;
        return this;
    }

    public Banner setLoopTime(long loopTime) {
        this.mLoopTime = loopTime;
        return this;
    }

    public Banner setScrollTime(int scrollTime) {
        this.mScrollTime = scrollTime;
        return this;
    }

    public Banner start() {
        if (this.mIsAutoLoop) {
            stop();
            postDelayed(this.mLoopTask, this.mLoopTime);
        }
        return this;
    }

    public Banner stop() {
        if (this.mIsAutoLoop) {
            removeCallbacks(this.mLoopTask);
        }
        return this;
    }

    public void destroy() {
        if (!(getViewPager2() == null || this.mPageChangeCallback == null)) {
            getViewPager2().unregisterOnPageChangeCallback(this.mPageChangeCallback);
            this.mPageChangeCallback = null;
        }
        stop();
    }

    public Banner setAdapter(BA adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            if (!isInfiniteLoop()) {
                getAdapter().setIncreaseCount(0);
            }
            getAdapter().registerAdapterDataObserver(this.mAdapterDataObserver);
            this.mViewPager2.setAdapter(adapter);
            setCurrentItem(this.mStartPosition, false);
            initIndicator();
            return this;
        }
        throw new NullPointerException("Adapter is null, please check it.");
    }

    public Banner setAdapter(BA adapter, boolean isInfiniteLoop) {
        this.mIsInfiniteLoop = isInfiniteLoop;
        setInfiniteLoop();
        setAdapter(adapter);
        return this;
    }

    public Banner setDatas(List<T> datas) {
        if (getAdapter() != null) {
            getAdapter().setDatas(datas);
            setCurrentItem(this.mStartPosition, false);
            setIndicatorPageChange();
            start();
        }
        return this;
    }

    public Banner setOrientation(int orientation) {
        getViewPager2().setOrientation(orientation);
        return this;
    }

    public Banner setTouchSlop(int mTouchSlop2) {
        this.mTouchSlop = mTouchSlop2;
        return this;
    }

    public Banner setOnBannerListener(OnBannerListener<T> listener) {
        if (getAdapter() != null) {
            getAdapter().setOnBannerListener(listener);
        }
        return this;
    }

    public Banner addOnPageChangeListener(OnPageChangeListener pageListener) {
        this.mOnPageChangeListener = pageListener;
        return this;
    }

    public Banner setBannerRound(float radius) {
        this.mBannerRadius = radius;
        return this;
    }

    public Banner setBannerRound2(float radius) {
        BannerUtils.setBannerRound(this, radius);
        return this;
    }

    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin) {
        return setBannerGalleryEffect(itemWidth, pageMargin, 0.85f);
    }

    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin) {
        return setBannerGalleryEffect(leftItemWidth, rightItemWidth, pageMargin, 0.85f);
    }

    public Banner setBannerGalleryEffect(int itemWidth, int pageMargin, float scale) {
        return setBannerGalleryEffect(itemWidth, itemWidth, pageMargin, scale);
    }

    public Banner setBannerGalleryEffect(int leftItemWidth, int rightItemWidth, int pageMargin, float scale) {
        if (pageMargin > 0) {
            addPageTransformer(new MarginPageTransformer((int) BannerUtils.dp2px((float) pageMargin)));
        }
        if (scale < 1.0f && scale > 0.0f) {
            addPageTransformer(new ScaleInTransformer(scale));
        }
        int i = 0;
        int dp2px = leftItemWidth > 0 ? (int) BannerUtils.dp2px((float) (leftItemWidth + pageMargin)) : 0;
        if (rightItemWidth > 0) {
            i = (int) BannerUtils.dp2px((float) (rightItemWidth + pageMargin));
        }
        setRecyclerViewPadding(dp2px, i);
        return this;
    }

    public Banner setBannerGalleryMZ(int itemWidth) {
        return setBannerGalleryMZ(itemWidth, 0.88f);
    }

    public Banner setBannerGalleryMZ(int itemWidth, float scale) {
        if (scale < 1.0f && scale > 0.0f) {
            addPageTransformer(new MZScaleInTransformer(scale));
        }
        setRecyclerViewPadding((int) BannerUtils.dp2px((float) itemWidth));
        return this;
    }

    public Banner setIndicator(Indicator indicator) {
        return setIndicator(indicator, true);
    }

    public Banner setIndicator(Indicator indicator, boolean attachToBanner) {
        removeIndicator();
        indicator.getIndicatorConfig().setAttachToBanner(attachToBanner);
        this.mIndicator = indicator;
        initIndicator();
        return this;
    }

    public Banner setIndicatorSelectedColor(int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedColor(color);
        }
        return this;
    }

    public Banner setIndicatorSelectedColorRes(int color) {
        setIndicatorSelectedColor(ContextCompat.getColor(getContext(), color));
        return this;
    }

    public Banner setIndicatorNormalColor(int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalColor(color);
        }
        return this;
    }

    public Banner setIndicatorNormalColorRes(int color) {
        setIndicatorNormalColor(ContextCompat.getColor(getContext(), color));
        return this;
    }

    public Banner setIndicatorGravity(int gravity) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setGravity(gravity);
            getIndicator().getIndicatorView().postInvalidate();
        }
        return this;
    }

    public Banner setIndicatorSpace(float indicatorSpace2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setIndicatorSpace(indicatorSpace2);
        }
        return this;
    }

    public Banner setIndicatorMargins(IndicatorConfig.Margins margins) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setMargins(margins);
            getIndicator().getIndicatorView().requestLayout();
        }
        return this;
    }

    public Banner setIndicatorWidth(float normalWidth2, float selectedWidth2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth2);
            getIndicatorConfig().setSelectedWidth(selectedWidth2);
        }
        return this;
    }

    public Banner setIndicatorNormalWidth(float normalWidth2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth2);
        }
        return this;
    }

    public Banner setIndicatorSelectedWidth(float selectedWidth2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedWidth(selectedWidth2);
        }
        return this;
    }

    public Banner setIndicatorRadius(float indicatorRadius2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setRadius(indicatorRadius2);
        }
        return this;
    }

    public Banner setIndicatorHeight(float indicatorHeight2) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setHeight(indicatorHeight2);
        }
        return this;
    }

    public Banner addBannerLifecycleObserver(LifecycleOwner owner) {
        if (owner != null) {
            owner.getLifecycle().addObserver(new BannerLifecycleObserverAdapter(owner, this));
        }
        return this;
    }

    public void onStart(LifecycleOwner owner) {
        start();
    }

    public void onStop(LifecycleOwner owner) {
        stop();
    }

    public void onDestroy(LifecycleOwner owner) {
        destroy();
    }
}
