package com.tablayout;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.tablayout.listener.OnTabSelectListener;
import com.tablayout.utils.FragmentChangeManager;
import com.tablayout.utils.UnreadMsgUtils;
import com.tablayout.widget.MsgView;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;

public class SegmentTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final int TEXT_BOLD_BOTH = 2;
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private int mBarColor;
    private int mBarStrokeColor;
    private float mBarStrokeWidth;
    private Context mContext;
    private IndicatorPoint mCurrentP;
    /* access modifiers changed from: private */
    public int mCurrentTab;
    private int mDividerColor;
    private float mDividerPadding;
    private Paint mDividerPaint;
    private float mDividerWidth;
    private FragmentChangeManager mFragmentChangeManager;
    private int mHeight;
    private long mIndicatorAnimDuration;
    private boolean mIndicatorAnimEnable;
    private boolean mIndicatorBounceEnable;
    private int mIndicatorColor;
    private float mIndicatorCornerRadius;
    private GradientDrawable mIndicatorDrawable;
    private float mIndicatorHeight;
    private float mIndicatorMarginBottom;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginTop;
    private Rect mIndicatorRect;
    private SparseArray<Boolean> mInitSetMap;
    private OvershootInterpolator mInterpolator;
    private boolean mIsFirstDraw;
    private IndicatorPoint mLastP;
    private int mLastTab;
    /* access modifiers changed from: private */
    public OnTabSelectListener mListener;
    private float[] mRadiusArr;
    private GradientDrawable mRectDrawable;
    private int mTabCount;
    private float mTabPadding;
    private boolean mTabSpaceEqual;
    private float mTabWidth;
    private LinearLayout mTabsContainer;
    private boolean mTextAllCaps;
    private int mTextBold;
    private Paint mTextPaint;
    private int mTextSelectColor;
    private int mTextUnselectColor;
    private float mTextsize;
    private String[] mTitles;
    private ValueAnimator mValueAnimator;

    public SegmentTabLayout(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegmentTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIndicatorRect = new Rect();
        this.mIndicatorDrawable = new GradientDrawable();
        this.mRectDrawable = new GradientDrawable();
        this.mDividerPaint = new Paint(1);
        this.mInterpolator = new OvershootInterpolator(0.8f);
        this.mRadiusArr = new float[8];
        this.mIsFirstDraw = true;
        this.mTextPaint = new Paint(1);
        this.mInitSetMap = new SparseArray<>();
        this.mCurrentP = new IndicatorPoint();
        this.mLastP = new IndicatorPoint();
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.mContext = context;
        LinearLayout linearLayout = new LinearLayout(context);
        this.mTabsContainer = linearLayout;
        addView(linearLayout);
        obtainAttributes(context, attrs);
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        if (!height.equals("-1") && !height.equals("-2")) {
            TypedArray a = context.obtainStyledAttributes(attrs, new int[]{16842997});
            this.mHeight = a.getDimensionPixelSize(0, -2);
            a.recycle();
        }
        ValueAnimator ofObject = ValueAnimator.ofObject(new PointEvaluator(), new Object[]{this.mLastP, this.mCurrentP});
        this.mValueAnimator = ofObject;
        ofObject.addUpdateListener(this);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SegmentTabLayout);
        this.mIndicatorColor = ta.getColor(9, Color.parseColor("#222831"));
        this.mIndicatorHeight = ta.getDimension(12, -1.0f);
        this.mIndicatorCornerRadius = ta.getDimension(10, -1.0f);
        this.mIndicatorMarginLeft = ta.getDimension(14, (float) dp2px(0.0f));
        this.mIndicatorMarginTop = ta.getDimension(16, 0.0f);
        this.mIndicatorMarginRight = ta.getDimension(15, (float) dp2px(0.0f));
        this.mIndicatorMarginBottom = ta.getDimension(13, 0.0f);
        this.mIndicatorAnimEnable = ta.getBoolean(7, false);
        this.mIndicatorBounceEnable = ta.getBoolean(8, true);
        this.mIndicatorAnimDuration = (long) ta.getInt(6, -1);
        this.mDividerColor = ta.getColor(3, this.mIndicatorColor);
        this.mDividerWidth = ta.getDimension(5, (float) dp2px(1.0f));
        this.mDividerPadding = ta.getDimension(4, 0.0f);
        this.mTextsize = ta.getDimension(22, (float) sp2px(13.0f));
        this.mTextSelectColor = ta.getColor(23, Color.parseColor("#ffffff"));
        this.mTextUnselectColor = ta.getColor(25, this.mIndicatorColor);
        this.mTextBold = ta.getInt(21, 0);
        this.mTextAllCaps = ta.getBoolean(20, false);
        this.mTabSpaceEqual = ta.getBoolean(18, true);
        float dimension = ta.getDimension(19, (float) dp2px(-1.0f));
        this.mTabWidth = dimension;
        this.mTabPadding = ta.getDimension(17, (float) ((this.mTabSpaceEqual || dimension > 0.0f) ? dp2px(0.0f) : dp2px(10.0f)));
        this.mBarColor = ta.getColor(0, 0);
        this.mBarStrokeColor = ta.getColor(1, this.mIndicatorColor);
        this.mBarStrokeWidth = ta.getDimension(2, (float) dp2px(1.0f));
        ta.recycle();
    }

    public void setTabData(String[] titles) {
        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be NULL or EMPTY !");
        }
        this.mTitles = titles;
        notifyDataSetChanged();
    }

    public void setTabData(String[] titles, FragmentActivity fa, int containerViewId, ArrayList<Fragment> fragments) {
        this.mFragmentChangeManager = new FragmentChangeManager(fa.getSupportFragmentManager(), containerViewId, fragments);
        setTabData(titles);
    }

    public void notifyDataSetChanged() {
        this.mTabsContainer.removeAllViews();
        this.mTabCount = this.mTitles.length;
        for (int i = 0; i < this.mTabCount; i++) {
            View tabView = View.inflate(this.mContext, R.layout.layout_tab_segment, (ViewGroup) null);
            tabView.setTag(Integer.valueOf(i));
            addTab(i, tabView);
        }
        updateTabStyles();
    }

    private void addTab(int position, View tabView) {
        ((TextView) tabView.findViewById(R.id.tv_tab_title)).setText(this.mTitles[position]);
        tabView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int position = ((Integer) v.getTag()).intValue();
                if (SegmentTabLayout.this.mCurrentTab != position) {
                    SegmentTabLayout.this.setCurrentTab(position);
                    if (SegmentTabLayout.this.mListener != null) {
                        SegmentTabLayout.this.mListener.onTabSelect(position);
                    }
                } else if (SegmentTabLayout.this.mListener != null) {
                    SegmentTabLayout.this.mListener.onTabReselect(position);
                }
            }
        });
        LinearLayout.LayoutParams lp_tab = this.mTabSpaceEqual ? new LinearLayout.LayoutParams(0, -1, 1.0f) : new LinearLayout.LayoutParams(-2, -1);
        if (this.mTabWidth > 0.0f) {
            lp_tab = new LinearLayout.LayoutParams((int) this.mTabWidth, -1);
        }
        this.mTabsContainer.addView(tabView, position, lp_tab);
    }

    private void updateTabStyles() {
        int i = 0;
        while (i < this.mTabCount) {
            View tabView = this.mTabsContainer.getChildAt(i);
            float f = this.mTabPadding;
            tabView.setPadding((int) f, 0, (int) f, 0);
            TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tv_tab_title.setTextColor(i == this.mCurrentTab ? this.mTextSelectColor : this.mTextUnselectColor);
            tv_tab_title.setTextSize(0, this.mTextsize);
            if (this.mTextAllCaps) {
                tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
            }
            int i2 = this.mTextBold;
            if (i2 == 2) {
                tv_tab_title.getPaint().setFakeBoldText(true);
            } else if (i2 == 0) {
                tv_tab_title.getPaint().setFakeBoldText(false);
            }
            i++;
        }
    }

    private void updateTabSelection(int position) {
        int i = 0;
        while (i < this.mTabCount) {
            View tabView = this.mTabsContainer.getChildAt(i);
            boolean isSelect = i == position;
            TextView tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tab_title.setTextColor(isSelect ? this.mTextSelectColor : this.mTextUnselectColor);
            if (this.mTextBold == 1) {
                tab_title.getPaint().setFakeBoldText(isSelect);
            }
            i++;
        }
    }

    private void calcOffset() {
        View currentTabView = this.mTabsContainer.getChildAt(this.mCurrentTab);
        this.mCurrentP.left = (float) currentTabView.getLeft();
        this.mCurrentP.right = (float) currentTabView.getRight();
        View lastTabView = this.mTabsContainer.getChildAt(this.mLastTab);
        this.mLastP.left = (float) lastTabView.getLeft();
        this.mLastP.right = (float) lastTabView.getRight();
        if (this.mLastP.left == this.mCurrentP.left && this.mLastP.right == this.mCurrentP.right) {
            invalidate();
            return;
        }
        this.mValueAnimator.setObjectValues(new Object[]{this.mLastP, this.mCurrentP});
        if (this.mIndicatorBounceEnable) {
            this.mValueAnimator.setInterpolator(this.mInterpolator);
        }
        if (this.mIndicatorAnimDuration < 0) {
            this.mIndicatorAnimDuration = this.mIndicatorBounceEnable ? 500 : 250;
        }
        this.mValueAnimator.setDuration(this.mIndicatorAnimDuration);
        this.mValueAnimator.start();
    }

    private void calcIndicatorRect() {
        View currentTabView = this.mTabsContainer.getChildAt(this.mCurrentTab);
        this.mIndicatorRect.left = (int) ((float) currentTabView.getLeft());
        this.mIndicatorRect.right = (int) ((float) currentTabView.getRight());
        if (!this.mIndicatorAnimEnable) {
            int i = this.mCurrentTab;
            if (i == 0) {
                float[] fArr = this.mRadiusArr;
                float f = this.mIndicatorCornerRadius;
                fArr[0] = f;
                fArr[1] = f;
                fArr[2] = 0.0f;
                fArr[3] = 0.0f;
                fArr[4] = 0.0f;
                fArr[5] = 0.0f;
                fArr[6] = f;
                fArr[7] = f;
            } else if (i == this.mTabCount - 1) {
                float[] fArr2 = this.mRadiusArr;
                fArr2[0] = 0.0f;
                fArr2[1] = 0.0f;
                float f2 = this.mIndicatorCornerRadius;
                fArr2[2] = f2;
                fArr2[3] = f2;
                fArr2[4] = f2;
                fArr2[5] = f2;
                fArr2[6] = 0.0f;
                fArr2[7] = 0.0f;
            } else {
                float[] fArr3 = this.mRadiusArr;
                fArr3[0] = 0.0f;
                fArr3[1] = 0.0f;
                fArr3[2] = 0.0f;
                fArr3[3] = 0.0f;
                fArr3[4] = 0.0f;
                fArr3[5] = 0.0f;
                fArr3[6] = 0.0f;
                fArr3[7] = 0.0f;
            }
        } else {
            float[] fArr4 = this.mRadiusArr;
            float f3 = this.mIndicatorCornerRadius;
            fArr4[0] = f3;
            fArr4[1] = f3;
            fArr4[2] = f3;
            fArr4[3] = f3;
            fArr4[4] = f3;
            fArr4[5] = f3;
            fArr4[6] = f3;
            fArr4[7] = f3;
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        this.mIndicatorRect.left = (int) p.left;
        this.mIndicatorRect.right = (int) p.right;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.mTabCount > 0) {
            int height = getHeight();
            int paddingLeft = getPaddingLeft();
            if (this.mIndicatorHeight < 0.0f) {
                this.mIndicatorHeight = (((float) height) - this.mIndicatorMarginTop) - this.mIndicatorMarginBottom;
            }
            float f = this.mIndicatorCornerRadius;
            if (f < 0.0f || f > this.mIndicatorHeight / 2.0f) {
                this.mIndicatorCornerRadius = this.mIndicatorHeight / 2.0f;
            }
            this.mRectDrawable.setColor(this.mBarColor);
            this.mRectDrawable.setStroke((int) this.mBarStrokeWidth, this.mBarStrokeColor);
            this.mRectDrawable.setCornerRadius(this.mIndicatorCornerRadius);
            this.mRectDrawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            this.mRectDrawable.draw(canvas);
            if (!this.mIndicatorAnimEnable) {
                float f2 = this.mDividerWidth;
                if (f2 > 0.0f) {
                    this.mDividerPaint.setStrokeWidth(f2);
                    this.mDividerPaint.setColor(this.mDividerColor);
                    for (int i = 0; i < this.mTabCount - 1; i++) {
                        View tab = this.mTabsContainer.getChildAt(i);
                        canvas.drawLine((float) (tab.getRight() + paddingLeft), this.mDividerPadding, (float) (tab.getRight() + paddingLeft), ((float) height) - this.mDividerPadding, this.mDividerPaint);
                    }
                }
            }
            if (this.mIndicatorAnimEnable == 0) {
                calcIndicatorRect();
            } else if (this.mIsFirstDraw) {
                this.mIsFirstDraw = false;
                calcIndicatorRect();
            }
            this.mIndicatorDrawable.setColor(this.mIndicatorColor);
            this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (int) (((float) (this.mIndicatorRect.right + paddingLeft)) - this.mIndicatorMarginRight), (int) (this.mIndicatorMarginTop + this.mIndicatorHeight));
            this.mIndicatorDrawable.setCornerRadii(this.mRadiusArr);
            this.mIndicatorDrawable.draw(canvas);
        }
    }

    public void setCurrentTab(int currentTab) {
        this.mLastTab = this.mCurrentTab;
        this.mCurrentTab = currentTab;
        updateTabSelection(currentTab);
        FragmentChangeManager fragmentChangeManager = this.mFragmentChangeManager;
        if (fragmentChangeManager != null) {
            fragmentChangeManager.setFragments(currentTab);
        }
        if (this.mIndicatorAnimEnable) {
            calcOffset();
        } else {
            invalidate();
        }
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = (float) dp2px(tabPadding);
        updateTabStyles();
    }

    public void setTabSpaceEqual(boolean tabSpaceEqual) {
        this.mTabSpaceEqual = tabSpaceEqual;
        updateTabStyles();
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = (float) dp2px(tabWidth);
        updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = (float) dp2px(indicatorHeight);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = (float) dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop, float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicatorMarginLeft = (float) dp2px(indicatorMarginLeft);
        this.mIndicatorMarginTop = (float) dp2px(indicatorMarginTop);
        this.mIndicatorMarginRight = (float) dp2px(indicatorMarginRight);
        this.mIndicatorMarginBottom = (float) dp2px(indicatorMarginBottom);
        invalidate();
    }

    public void setIndicatorAnimDuration(long indicatorAnimDuration) {
        this.mIndicatorAnimDuration = indicatorAnimDuration;
    }

    public void setIndicatorAnimEnable(boolean indicatorAnimEnable) {
        this.mIndicatorAnimEnable = indicatorAnimEnable;
    }

    public void setIndicatorBounceEnable(boolean indicatorBounceEnable) {
        this.mIndicatorBounceEnable = indicatorBounceEnable;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerWidth(float dividerWidth) {
        this.mDividerWidth = (float) dp2px(dividerWidth);
        invalidate();
    }

    public void setDividerPadding(float dividerPadding) {
        this.mDividerPadding = (float) dp2px(dividerPadding);
        invalidate();
    }

    public void setTextsize(float textsize) {
        this.mTextsize = (float) sp2px(textsize);
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnselectColor) {
        this.mTextUnselectColor = textUnselectColor;
        updateTabStyles();
    }

    public void setTextBold(int textBold) {
        this.mTextBold = textBold;
        updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        updateTabStyles();
    }

    public int getTabCount() {
        return this.mTabCount;
    }

    public int getCurrentTab() {
        return this.mCurrentTab;
    }

    public float getTabPadding() {
        return this.mTabPadding;
    }

    public boolean isTabSpaceEqual() {
        return this.mTabSpaceEqual;
    }

    public float getTabWidth() {
        return this.mTabWidth;
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public float getIndicatorHeight() {
        return this.mIndicatorHeight;
    }

    public float getIndicatorCornerRadius() {
        return this.mIndicatorCornerRadius;
    }

    public float getIndicatorMarginLeft() {
        return this.mIndicatorMarginLeft;
    }

    public float getIndicatorMarginTop() {
        return this.mIndicatorMarginTop;
    }

    public float getIndicatorMarginRight() {
        return this.mIndicatorMarginRight;
    }

    public float getIndicatorMarginBottom() {
        return this.mIndicatorMarginBottom;
    }

    public long getIndicatorAnimDuration() {
        return this.mIndicatorAnimDuration;
    }

    public boolean isIndicatorAnimEnable() {
        return this.mIndicatorAnimEnable;
    }

    public boolean isIndicatorBounceEnable() {
        return this.mIndicatorBounceEnable;
    }

    public int getDividerColor() {
        return this.mDividerColor;
    }

    public float getDividerWidth() {
        return this.mDividerWidth;
    }

    public float getDividerPadding() {
        return this.mDividerPadding;
    }

    public float getTextsize() {
        return this.mTextsize;
    }

    public int getTextSelectColor() {
        return this.mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return this.mTextUnselectColor;
    }

    public int getTextBold() {
        return this.mTextBold;
    }

    public boolean isTextAllCaps() {
        return this.mTextAllCaps;
    }

    public TextView getTitleView(int tab) {
        return (TextView) this.mTabsContainer.getChildAt(tab).findViewById(R.id.tv_tab_title);
    }

    public void showMsg(int position, int num) {
        int i = this.mTabCount;
        if (position >= i) {
            position = i - 1;
        }
        MsgView tipView = (MsgView) this.mTabsContainer.getChildAt(position).findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            UnreadMsgUtils.show(tipView, num);
            if (this.mInitSetMap.get(position) == null || !this.mInitSetMap.get(position).booleanValue()) {
                setMsgMargin(position, 2.0f, 2.0f);
                this.mInitSetMap.put(position, true);
            }
        }
    }

    public void showDot(int position) {
        int i = this.mTabCount;
        if (position >= i) {
            position = i - 1;
        }
        showMsg(position, 0);
    }

    public void hideMsg(int position) {
        int i = this.mTabCount;
        if (position >= i) {
            position = i - 1;
        }
        MsgView tipView = (MsgView) this.mTabsContainer.getChildAt(position).findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(8);
        }
    }

    public void setMsgMargin(int position, float leftPadding, float bottomPadding) {
        int i = this.mTabCount;
        if (position >= i) {
            position = i - 1;
        }
        View tabView = this.mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            this.mTextPaint.setTextSize(this.mTextsize);
            float measureText = this.mTextPaint.measureText(((TextView) tabView.findViewById(R.id.tv_tab_title)).getText().toString());
            float textHeight = this.mTextPaint.descent() - this.mTextPaint.ascent();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tipView.getLayoutParams();
            lp.leftMargin = dp2px(leftPadding);
            int i2 = this.mHeight;
            lp.topMargin = i2 > 0 ? (((int) (((float) i2) - textHeight)) / 2) - dp2px(bottomPadding) : dp2px(bottomPadding);
            tipView.setLayoutParams(lp);
        }
    }

    public MsgView getMsgView(int position) {
        int i = this.mTabCount;
        if (position >= i) {
            position = i - 1;
        }
        return (MsgView) this.mTabsContainer.getChildAt(position).findViewById(R.id.rtv_msg_tip);
    }

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", this.mCurrentTab);
        return bundle;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (this.mCurrentTab != 0 && this.mTabsContainer.getChildCount() > 0) {
                updateTabSelection(this.mCurrentTab);
            }
        }
        super.onRestoreInstanceState(state);
    }

    class IndicatorPoint {
        public float left;
        public float right;

        IndicatorPoint() {
        }
    }

    class PointEvaluator implements TypeEvaluator<IndicatorPoint> {
        PointEvaluator() {
        }

        public IndicatorPoint evaluate(float fraction, IndicatorPoint startValue, IndicatorPoint endValue) {
            float left = startValue.left + ((endValue.left - startValue.left) * fraction);
            float right = startValue.right + ((endValue.right - startValue.right) * fraction);
            IndicatorPoint point = new IndicatorPoint();
            point.left = left;
            point.right = right;
            return point;
        }
    }

    /* access modifiers changed from: protected */
    public int dp2px(float dp) {
        return (int) ((dp * this.mContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    /* access modifiers changed from: protected */
    public int sp2px(float sp) {
        return (int) ((sp * this.mContext.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }
}
