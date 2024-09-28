package com.tablayout;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.tablayout.listener.CustomTabEntity;
import com.tablayout.listener.OnTabSelectListener;
import com.tablayout.utils.FragmentChangeManager;
import com.tablayout.utils.UnreadMsgUtils;
import com.tablayout.widget.MsgView;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;

public class CommonTabLayout extends FrameLayout implements ValueAnimator.AnimatorUpdateListener {
    private static final int STYLE_BLOCK = 2;
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int TEXT_BOLD_BOTH = 2;
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
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
    private int mIconGravity;
    private float mIconHeight;
    private float mIconMargin;
    private boolean mIconVisible;
    private float mIconWidth;
    private long mIndicatorAnimDuration;
    private boolean mIndicatorAnimEnable;
    private boolean mIndicatorBounceEnable;
    private int mIndicatorColor;
    private float mIndicatorCornerRadius;
    private GradientDrawable mIndicatorDrawable;
    private int mIndicatorGravity;
    private float mIndicatorHeight;
    private float mIndicatorMarginBottom;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginTop;
    private Rect mIndicatorRect;
    private int mIndicatorStyle;
    private float mIndicatorWidth;
    private SparseArray<Boolean> mInitSetMap;
    private OvershootInterpolator mInterpolator;
    private boolean mIsFirstDraw;
    private IndicatorPoint mLastP;
    private int mLastTab;
    /* access modifiers changed from: private */
    public OnTabSelectListener mListener;
    private Paint mRectPaint;
    private int mTabCount;
    private ArrayList<CustomTabEntity> mTabEntitys;
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
    private Paint mTrianglePaint;
    private Path mTrianglePath;
    private int mUnderlineColor;
    private int mUnderlineGravity;
    private float mUnderlineHeight;
    private ValueAnimator mValueAnimator;

    public CommonTabLayout(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public CommonTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTabEntitys = new ArrayList<>();
        this.mIndicatorRect = new Rect();
        this.mIndicatorDrawable = new GradientDrawable();
        this.mRectPaint = new Paint(1);
        this.mDividerPaint = new Paint(1);
        this.mTrianglePaint = new Paint(1);
        this.mTrianglePath = new Path();
        this.mIndicatorStyle = 0;
        this.mInterpolator = new OvershootInterpolator(1.5f);
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
        float f;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTabLayout);
        int i = ta.getInt(20, 0);
        this.mIndicatorStyle = i;
        this.mIndicatorColor = ta.getColor(11, Color.parseColor(i == 2 ? "#4B6A87" : "#ffffff"));
        int i2 = this.mIndicatorStyle;
        if (i2 == 1) {
            f = 4.0f;
        } else {
            f = (float) (i2 == 2 ? -1 : 2);
        }
        this.mIndicatorHeight = ta.getDimension(15, (float) dp2px(f));
        this.mIndicatorWidth = ta.getDimension(21, (float) dp2px(this.mIndicatorStyle == 1 ? 10.0f : -1.0f));
        this.mIndicatorCornerRadius = ta.getDimension(12, (float) dp2px(this.mIndicatorStyle == 2 ? -1.0f : 0.0f));
        this.mIndicatorMarginLeft = ta.getDimension(17, (float) dp2px(0.0f));
        float f2 = 7.0f;
        this.mIndicatorMarginTop = ta.getDimension(19, (float) dp2px(this.mIndicatorStyle == 2 ? 7.0f : 0.0f));
        this.mIndicatorMarginRight = ta.getDimension(18, (float) dp2px(0.0f));
        if (this.mIndicatorStyle != 2) {
            f2 = 0.0f;
        }
        this.mIndicatorMarginBottom = ta.getDimension(16, (float) dp2px(f2));
        this.mIndicatorAnimEnable = ta.getBoolean(9, true);
        this.mIndicatorBounceEnable = ta.getBoolean(10, true);
        this.mIndicatorAnimDuration = (long) ta.getInt(8, -1);
        this.mIndicatorGravity = ta.getInt(14, 80);
        this.mUnderlineColor = ta.getColor(33, Color.parseColor("#ffffff"));
        this.mUnderlineHeight = ta.getDimension(35, (float) dp2px(0.0f));
        this.mUnderlineGravity = ta.getInt(34, 80);
        this.mDividerColor = ta.getColor(0, Color.parseColor("#ffffff"));
        this.mDividerWidth = ta.getDimension(2, (float) dp2px(0.0f));
        this.mDividerPadding = ta.getDimension(1, (float) dp2px(12.0f));
        this.mTextsize = ta.getDimension(29, (float) sp2px(13.0f));
        this.mTextSelectColor = ta.getColor(30, Color.parseColor("#ffffff"));
        this.mTextUnselectColor = ta.getColor(32, Color.parseColor("#AAffffff"));
        this.mTextBold = ta.getInt(28, 0);
        this.mTextAllCaps = ta.getBoolean(27, false);
        this.mIconVisible = ta.getBoolean(6, true);
        this.mIconGravity = ta.getInt(3, 48);
        this.mIconWidth = ta.getDimension(7, (float) dp2px(0.0f));
        this.mIconHeight = ta.getDimension(4, (float) dp2px(0.0f));
        this.mIconMargin = ta.getDimension(5, (float) dp2px(2.5f));
        this.mTabSpaceEqual = ta.getBoolean(25, true);
        float dimension = ta.getDimension(26, (float) dp2px(-1.0f));
        this.mTabWidth = dimension;
        this.mTabPadding = ta.getDimension(24, (float) ((this.mTabSpaceEqual || dimension > 0.0f) ? dp2px(0.0f) : dp2px(10.0f)));
        ta.recycle();
    }

    public void setTabData(ArrayList<CustomTabEntity> tabEntitys) {
        if (tabEntitys == null || tabEntitys.size() == 0) {
            throw new IllegalStateException("TabEntitys can not be NULL or EMPTY !");
        }
        this.mTabEntitys.clear();
        this.mTabEntitys.addAll(tabEntitys);
        notifyDataSetChanged();
    }

    public void setTabData(ArrayList<CustomTabEntity> tabEntitys, FragmentActivity fa, int containerViewId, ArrayList<Fragment> fragments) {
        this.mFragmentChangeManager = new FragmentChangeManager(fa.getSupportFragmentManager(), containerViewId, fragments);
        setTabData(tabEntitys);
    }

    public void notifyDataSetChanged() {
        View tabView;
        this.mTabsContainer.removeAllViews();
        this.mTabCount = this.mTabEntitys.size();
        for (int i = 0; i < this.mTabCount; i++) {
            int i2 = this.mIconGravity;
            if (i2 == 3) {
                tabView = View.inflate(this.mContext, R.layout.layout_tab_left, (ViewGroup) null);
            } else if (i2 == 5) {
                tabView = View.inflate(this.mContext, R.layout.layout_tab_right, (ViewGroup) null);
            } else if (i2 == 80) {
                tabView = View.inflate(this.mContext, R.layout.layout_tab_bottom, (ViewGroup) null);
            } else {
                tabView = View.inflate(this.mContext, R.layout.layout_tab_top, (ViewGroup) null);
            }
            tabView.setTag(Integer.valueOf(i));
            addTab(i, tabView);
        }
        updateTabStyles();
    }

    private void addTab(int position, View tabView) {
        ((TextView) tabView.findViewById(R.id.tv_tab_title)).setText(this.mTabEntitys.get(position).getTabTitle());
        ((ImageView) tabView.findViewById(R.id.iv_tab_icon)).setImageResource(this.mTabEntitys.get(position).getTabUnselectedIcon());
        tabView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int position = ((Integer) v.getTag()).intValue();
                if (CommonTabLayout.this.mCurrentTab != position) {
                    CommonTabLayout.this.setCurrentTab(position);
                    if (CommonTabLayout.this.mListener != null) {
                        CommonTabLayout.this.mListener.onTabSelect(position);
                    }
                } else if (CommonTabLayout.this.mListener != null) {
                    CommonTabLayout.this.mListener.onTabReselect(position);
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
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            if (this.mIconVisible) {
                iv_tab_icon.setVisibility(0);
                CustomTabEntity tabEntity = this.mTabEntitys.get(i);
                iv_tab_icon.setImageResource(i == this.mCurrentTab ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
                float f2 = this.mIconWidth;
                int i3 = -2;
                int i4 = f2 <= 0.0f ? -2 : (int) f2;
                float f3 = this.mIconHeight;
                if (f3 > 0.0f) {
                    i3 = (int) f3;
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(i4, i3);
                int i5 = this.mIconGravity;
                if (i5 == 3) {
                    lp.rightMargin = (int) this.mIconMargin;
                } else if (i5 == 5) {
                    lp.leftMargin = (int) this.mIconMargin;
                } else if (i5 == 80) {
                    lp.topMargin = (int) this.mIconMargin;
                } else {
                    lp.bottomMargin = (int) this.mIconMargin;
                }
                iv_tab_icon.setLayoutParams(lp);
            } else {
                iv_tab_icon.setVisibility(8);
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
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            CustomTabEntity tabEntity = this.mTabEntitys.get(i);
            iv_tab_icon.setImageResource(isSelect ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
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
        if (this.mIndicatorWidth >= 0.0f) {
            this.mIndicatorRect.left = (int) (((float) currentTabView.getLeft()) + ((((float) currentTabView.getWidth()) - this.mIndicatorWidth) / 2.0f));
            Rect rect = this.mIndicatorRect;
            rect.right = (int) (((float) rect.left) + this.mIndicatorWidth);
        }
    }

    public void onAnimationUpdate(ValueAnimator animation) {
        View currentTabView = this.mTabsContainer.getChildAt(this.mCurrentTab);
        IndicatorPoint p = (IndicatorPoint) animation.getAnimatedValue();
        this.mIndicatorRect.left = (int) p.left;
        this.mIndicatorRect.right = (int) p.right;
        if (this.mIndicatorWidth >= 0.0f) {
            this.mIndicatorRect.left = (int) (p.left + ((((float) currentTabView.getWidth()) - this.mIndicatorWidth) / 2.0f));
            Rect rect = this.mIndicatorRect;
            rect.right = (int) (((float) rect.left) + this.mIndicatorWidth);
        }
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode() && this.mTabCount > 0) {
            int height = getHeight();
            int paddingLeft = getPaddingLeft();
            float f = this.mDividerWidth;
            if (f > 0.0f) {
                this.mDividerPaint.setStrokeWidth(f);
                this.mDividerPaint.setColor(this.mDividerColor);
                for (int i = 0; i < this.mTabCount - 1; i++) {
                    View tab = this.mTabsContainer.getChildAt(i);
                    canvas.drawLine((float) (tab.getRight() + paddingLeft), this.mDividerPadding, (float) (tab.getRight() + paddingLeft), ((float) height) - this.mDividerPadding, this.mDividerPaint);
                }
            }
            if (this.mUnderlineHeight > 0.0f) {
                this.mRectPaint.setColor(this.mUnderlineColor);
                if (this.mUnderlineGravity == 80) {
                    canvas.drawRect((float) paddingLeft, ((float) height) - this.mUnderlineHeight, (float) (this.mTabsContainer.getWidth() + paddingLeft), (float) height, this.mRectPaint);
                } else {
                    canvas.drawRect((float) paddingLeft, 0.0f, (float) (this.mTabsContainer.getWidth() + paddingLeft), this.mUnderlineHeight, this.mRectPaint);
                }
            }
            if (!this.mIndicatorAnimEnable) {
                calcIndicatorRect();
            } else if (this.mIsFirstDraw) {
                this.mIsFirstDraw = false;
                calcIndicatorRect();
            }
            int i2 = this.mIndicatorStyle;
            if (i2 == 1) {
                if (this.mIndicatorHeight > 0.0f) {
                    this.mTrianglePaint.setColor(this.mIndicatorColor);
                    this.mTrianglePath.reset();
                    this.mTrianglePath.moveTo((float) (this.mIndicatorRect.left + paddingLeft), (float) height);
                    this.mTrianglePath.lineTo((float) ((this.mIndicatorRect.left / 2) + paddingLeft + (this.mIndicatorRect.right / 2)), ((float) height) - this.mIndicatorHeight);
                    this.mTrianglePath.lineTo((float) (this.mIndicatorRect.right + paddingLeft), (float) height);
                    this.mTrianglePath.close();
                    canvas.drawPath(this.mTrianglePath, this.mTrianglePaint);
                }
            } else if (i2 == 2) {
                if (this.mIndicatorHeight < 0.0f) {
                    this.mIndicatorHeight = (((float) height) - this.mIndicatorMarginTop) - this.mIndicatorMarginBottom;
                }
                float f2 = this.mIndicatorHeight;
                if (f2 > 0.0f) {
                    float f3 = this.mIndicatorCornerRadius;
                    if (f3 < 0.0f || f3 > f2 / 2.0f) {
                        this.mIndicatorCornerRadius = this.mIndicatorHeight / 2.0f;
                    }
                    this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (int) (((float) (this.mIndicatorRect.right + paddingLeft)) - this.mIndicatorMarginRight), (int) (this.mIndicatorMarginTop + this.mIndicatorHeight));
                    this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                    this.mIndicatorDrawable.draw(canvas);
                }
            } else if (this.mIndicatorHeight > 0.0f) {
                this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                if (this.mIndicatorGravity == 80) {
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (height - ((int) this.mIndicatorHeight)) - ((int) this.mIndicatorMarginBottom), (this.mIndicatorRect.right + paddingLeft) - ((int) this.mIndicatorMarginRight), height - ((int) this.mIndicatorMarginBottom));
                } else {
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (this.mIndicatorRect.right + paddingLeft) - ((int) this.mIndicatorMarginRight), ((int) this.mIndicatorHeight) + ((int) this.mIndicatorMarginTop));
                }
                this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                this.mIndicatorDrawable.draw(canvas);
            }
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

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        invalidate();
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

    public void setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = (float) dp2px(indicatorWidth);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = (float) dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
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

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = (float) dp2px(underlineHeight);
        invalidate();
    }

    public void setUnderlineGravity(int underlineGravity) {
        this.mUnderlineGravity = underlineGravity;
        invalidate();
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

    public void setIconVisible(boolean iconVisible) {
        this.mIconVisible = iconVisible;
        updateTabStyles();
    }

    public void setIconGravity(int iconGravity) {
        this.mIconGravity = iconGravity;
        notifyDataSetChanged();
    }

    public void setIconWidth(float iconWidth) {
        this.mIconWidth = (float) dp2px(iconWidth);
        updateTabStyles();
    }

    public void setIconHeight(float iconHeight) {
        this.mIconHeight = (float) dp2px(iconHeight);
        updateTabStyles();
    }

    public void setIconMargin(float iconMargin) {
        this.mIconMargin = (float) dp2px(iconMargin);
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

    public int getIndicatorStyle() {
        return this.mIndicatorStyle;
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

    public float getIndicatorWidth() {
        return this.mIndicatorWidth;
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

    public int getUnderlineColor() {
        return this.mUnderlineColor;
    }

    public float getUnderlineHeight() {
        return this.mUnderlineHeight;
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

    public int getIconGravity() {
        return this.mIconGravity;
    }

    public float getIconWidth() {
        return this.mIconWidth;
    }

    public float getIconHeight() {
        return this.mIconHeight;
    }

    public float getIconMargin() {
        return this.mIconMargin;
    }

    public boolean isIconVisible() {
        return this.mIconVisible;
    }

    public ImageView getIconView(int tab) {
        return (ImageView) this.mTabsContainer.getChildAt(tab).findViewById(R.id.iv_tab_icon);
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
                if (!this.mIconVisible) {
                    setMsgMargin(position, 2.0f, 2.0f);
                } else {
                    int i2 = this.mIconGravity;
                    setMsgMargin(position, 0.0f, (i2 == 3 || i2 == 5) ? 4.0f : 0.0f);
                }
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
            float iconH = this.mIconHeight;
            float margin = 0.0f;
            if (this.mIconVisible) {
                if (iconH <= 0.0f) {
                    iconH = (float) this.mContext.getResources().getDrawable(this.mTabEntitys.get(position).getTabSelectedIcon()).getIntrinsicHeight();
                }
                margin = this.mIconMargin;
            }
            int i2 = this.mIconGravity;
            if (i2 == 48 || i2 == 80) {
                lp.leftMargin = dp2px(leftPadding);
                int i3 = this.mHeight;
                lp.topMargin = i3 > 0 ? (((int) (((((float) i3) - textHeight) - iconH) - margin)) / 2) - dp2px(bottomPadding) : dp2px(bottomPadding);
            } else {
                lp.leftMargin = dp2px(leftPadding);
                int i4 = this.mHeight;
                lp.topMargin = i4 > 0 ? (((int) (((float) i4) - Math.max(textHeight, iconH))) / 2) - dp2px(bottomPadding) : dp2px(bottomPadding);
            }
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
