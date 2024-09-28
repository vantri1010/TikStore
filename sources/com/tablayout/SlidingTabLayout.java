package com.tablayout;

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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.tablayout.listener.OnTabReleaseListener;
import com.tablayout.listener.OnTabSelectListener;
import com.tablayout.utils.UnreadMsgUtils;
import com.tablayout.widget.MsgView;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.Collections;

public class SlidingTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private static final int STYLE_BLOCK = 2;
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int TEXT_BOLD_BOTH = 2;
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private Context mContext;
    private float mCurrentPositionOffset;
    private int mCurrentTab;
    private int mDividerColor;
    private float mDividerPadding;
    private Paint mDividerPaint;
    private float mDividerWidth;
    private int mEndIndicatorColor;
    private int mHeight;
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
    private boolean mIndicatorWidthEqualTitle;
    private SparseArray<Boolean> mInitSetMap;
    private int mLastScrollX;
    /* access modifiers changed from: private */
    public OnTabSelectListener mListener;
    private int mMsgViewBackgroundColor;
    private int mMsgViewWidth;
    private Paint mRectPaint;
    /* access modifiers changed from: private */
    public OnTabReleaseListener mReleaseListener;
    /* access modifiers changed from: private */
    public boolean mSnapOnTabClick;
    private int mTabCount;
    private float mTabPadding;
    private float mTabPaddingBottom;
    private float mTabPaddingTop;
    private Rect mTabRect;
    private boolean mTabSpaceEqual;
    private float mTabWidth;
    /* access modifiers changed from: private */
    public LinearLayout mTabsContainer;
    private boolean mTextAllCaps;
    private int mTextBold;
    private Paint mTextPaint;
    private int mTextSelectColor;
    private float mTextSelectSize;
    private int mTextUnselectColor;
    private float mTextsize;
    private ArrayList<String> mTitles;
    private Paint mTrianglePaint;
    private Path mTrianglePath;
    private int mUnderlineColor;
    private int mUnderlineGravity;
    private float mUnderlineHeight;
    /* access modifiers changed from: private */
    public ViewPager mViewPager;
    private float margin;
    private int prevTab;

    public SlidingTabLayout(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mIndicatorRect = new Rect();
        this.mTabRect = new Rect();
        this.mIndicatorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
        this.mRectPaint = new Paint(1);
        this.mDividerPaint = new Paint(1);
        this.mTrianglePaint = new Paint(1);
        this.mTrianglePath = new Path();
        this.mIndicatorStyle = 0;
        this.mTabWidth = -1.0f;
        this.prevTab = -1;
        this.mTextPaint = new Paint(1);
        this.mInitSetMap = new SparseArray<>();
        setFillViewport(true);
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.mContext = context;
        LinearLayout linearLayout = new LinearLayout(context);
        this.mTabsContainer = linearLayout;
        addView(linearLayout);
        if (attrs != null) {
            obtainAttributes(context, attrs);
            String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
            if (!height.equals("-1") && !height.equals("-2")) {
                TypedArray a = context.obtainStyledAttributes(attrs, new int[]{16842997});
                this.mHeight = a.getDimensionPixelSize(0, -2);
                a.recycle();
            }
        }
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        float f;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingTabLayout);
        int i = ta.getInt(12, 0);
        this.mIndicatorStyle = i;
        this.mIndicatorColor = ta.getColor(3, Color.parseColor(i == 2 ? "#4B6A87" : "#ffffff"));
        this.mEndIndicatorColor = ta.getColor(5, this.mIndicatorStyle == 2 ? Color.parseColor("#4B6A87") : 0);
        int i2 = this.mIndicatorStyle;
        if (i2 == 1) {
            f = 4.0f;
        } else {
            f = (float) (i2 == 2 ? -1 : 2);
        }
        this.mIndicatorHeight = ta.getDimension(7, (float) dp2px(f));
        this.mIndicatorWidth = ta.getDimension(13, (float) dp2px(this.mIndicatorStyle == 1 ? 10.0f : -1.0f));
        this.mIndicatorCornerRadius = ta.getDimension(4, (float) dp2px(this.mIndicatorStyle == 2 ? -1.0f : 0.0f));
        this.mIndicatorMarginLeft = ta.getDimension(9, (float) dp2px(0.0f));
        float f2 = 7.0f;
        this.mIndicatorMarginTop = ta.getDimension(11, (float) dp2px(this.mIndicatorStyle == 2 ? 7.0f : 0.0f));
        this.mIndicatorMarginRight = ta.getDimension(10, (float) dp2px(0.0f));
        if (this.mIndicatorStyle != 2) {
            f2 = 0.0f;
        }
        this.mIndicatorMarginBottom = ta.getDimension(8, (float) dp2px(f2));
        this.mIndicatorGravity = ta.getInt(6, 80);
        this.mIndicatorWidthEqualTitle = ta.getBoolean(14, false);
        this.mUnderlineColor = ta.getColor(24, Color.parseColor("#ffffff"));
        this.mUnderlineHeight = ta.getDimension(26, (float) dp2px(0.0f));
        this.mUnderlineGravity = ta.getInt(25, 80);
        this.mDividerColor = ta.getColor(0, Color.parseColor("#ffffff"));
        this.mDividerWidth = ta.getDimension(2, (float) dp2px(0.0f));
        this.mDividerPadding = ta.getDimension(1, (float) dp2px(12.0f));
        this.mTextsize = ta.getDimension(20, (float) sp2px(14.0f));
        this.mTextSelectSize = ta.getDimension(22, 0.0f);
        this.mTextSelectColor = ta.getColor(21, Color.parseColor("#ffffff"));
        this.mTextUnselectColor = ta.getColor(23, Color.parseColor("#AAffffff"));
        this.mTextBold = ta.getInt(19, 0);
        this.mTextAllCaps = ta.getBoolean(18, false);
        this.mTabSpaceEqual = ta.getBoolean(16, false);
        float dimension = ta.getDimension(17, (float) dp2px(-1.0f));
        this.mTabWidth = dimension;
        this.mTabPadding = ta.getDimension(15, (float) ((this.mTabSpaceEqual || dimension > 0.0f) ? dp2px(0.0f) : dp2px(20.0f)));
        ta.recycle();
    }

    public void setViewPager(ViewPager vp) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        }
        this.mViewPager = vp;
        vp.removeOnPageChangeListener(this);
        this.mViewPager.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void setViewPager(ViewPager vp, String[] titles) {
        if (vp == null || vp.getAdapter() == null) {
            throw new IllegalStateException("ViewPager or ViewPager adapter can not be NULL !");
        } else if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        } else if (titles.length == vp.getAdapter().getCount()) {
            this.mViewPager = vp;
            ArrayList<String> arrayList = new ArrayList<>();
            this.mTitles = arrayList;
            Collections.addAll(arrayList, titles);
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("Titles length must be the same as the page count !");
        }
    }

    public void setViewPager(ViewPager vp, String[] titles, FragmentActivity fa, ArrayList<Fragment> fragments) {
        if (vp == null) {
            throw new IllegalStateException("ViewPager can not be NULL !");
        } else if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        } else {
            this.mViewPager = vp;
            vp.setAdapter(new InnerPagerAdapter(fa.getSupportFragmentManager(), fragments, titles));
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        this.mTabsContainer.removeAllViews();
        ArrayList<String> arrayList = this.mTitles;
        this.mTabCount = arrayList == null ? this.mViewPager.getAdapter().getCount() : arrayList.size();
        for (int i = 0; i < this.mTabCount; i++) {
            View tabView = View.inflate(this.mContext, R.layout.layout_tab, (ViewGroup) null);
            ArrayList<String> arrayList2 = this.mTitles;
            addTab(i, (arrayList2 == null ? this.mViewPager.getAdapter().getPageTitle(i) : arrayList2.get(i)).toString(), tabView);
        }
        updateTabStyles();
    }

    public void addNewTab(String title) {
        View tabView = View.inflate(this.mContext, R.layout.layout_tab, (ViewGroup) null);
        ArrayList<String> arrayList = this.mTitles;
        if (arrayList != null) {
            arrayList.add(title);
        }
        ArrayList<String> arrayList2 = this.mTitles;
        addTab(this.mTabCount, (arrayList2 == null ? this.mViewPager.getAdapter().getPageTitle(this.mTabCount) : arrayList2.get(this.mTabCount)).toString(), tabView);
        ArrayList<String> arrayList3 = this.mTitles;
        this.mTabCount = arrayList3 == null ? this.mViewPager.getAdapter().getCount() : arrayList3.size();
        updateTabStyles();
    }

    private void addTab(int position, String title, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        if (!(tv_tab_title == null || title == null)) {
            tv_tab_title.setText(title);
        }
        tabView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int position = SlidingTabLayout.this.mTabsContainer.indexOfChild(v);
                if (position == -1) {
                    return;
                }
                if (SlidingTabLayout.this.mViewPager.getCurrentItem() != position) {
                    if (SlidingTabLayout.this.mReleaseListener != null) {
                        SlidingTabLayout.this.mReleaseListener.onTabRelease(SlidingTabLayout.this.mViewPager.getCurrentItem(), false);
                    }
                    if (SlidingTabLayout.this.mSnapOnTabClick) {
                        SlidingTabLayout.this.mViewPager.setCurrentItem(position, false);
                    } else {
                        SlidingTabLayout.this.mViewPager.setCurrentItem(position);
                    }
                    if (SlidingTabLayout.this.mListener != null) {
                        SlidingTabLayout.this.mListener.onTabSelect(position);
                    }
                } else if (SlidingTabLayout.this.mListener != null) {
                    SlidingTabLayout.this.mListener.onTabReselect(position);
                }
            }
        });
        MsgView msgView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (msgView != null) {
            int i = this.mMsgViewBackgroundColor;
            if (i != 0) {
                msgView.setBackgroundColor(i);
            }
            int i2 = this.mMsgViewWidth;
            if (i2 != 0) {
                msgView.setWidth(i2);
            }
        }
        LinearLayout.LayoutParams lp_tab = this.mTabSpaceEqual ? new LinearLayout.LayoutParams(0, -1, 1.0f) : new LinearLayout.LayoutParams(-2, -1);
        if (this.mTabWidth > 0.0f) {
            lp_tab = new LinearLayout.LayoutParams((int) this.mTabWidth, -1);
        }
        this.mTabsContainer.addView(tabView, position, lp_tab);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002b, code lost:
        if (r3 == 0.0f) goto L_0x002d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabStyles() {
        /*
            r8 = this;
            r0 = 0
        L_0x0001:
            int r1 = r8.mTabCount
            if (r0 >= r1) goto L_0x007d
            android.widget.LinearLayout r1 = r8.mTabsContainer
            android.view.View r1 = r1.getChildAt(r0)
            r2 = 2131297853(0x7f09063d, float:1.8213663E38)
            android.view.View r2 = r1.findViewById(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x007a
            int r3 = r8.mCurrentTab
            if (r0 != r3) goto L_0x001d
            int r3 = r8.mTextSelectColor
            goto L_0x001f
        L_0x001d:
            int r3 = r8.mTextUnselectColor
        L_0x001f:
            r2.setTextColor(r3)
            int r3 = r8.mCurrentTab
            if (r0 != r3) goto L_0x002d
            float r3 = r8.mTextSelectSize
            r4 = 0
            int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x002f
        L_0x002d:
            float r3 = r8.mTextsize
        L_0x002f:
            r4 = 0
            r2.setTextSize(r4, r3)
            float r3 = r8.mTabPadding
            int r5 = (int) r3
            float r6 = r8.mTabPaddingTop
            int r6 = (int) r6
            int r3 = (int) r3
            float r7 = r8.mTabPaddingBottom
            int r7 = (int) r7
            r2.setPadding(r5, r6, r3, r7)
            boolean r3 = r8.mTextAllCaps
            if (r3 == 0) goto L_0x0053
            java.lang.CharSequence r3 = r2.getText()
            java.lang.String r3 = r3.toString()
            java.lang.String r3 = r3.toUpperCase()
            r2.setText(r3)
        L_0x0053:
            int r3 = r8.mTextBold
            r5 = 2
            r6 = 1
            if (r3 != r5) goto L_0x0061
            android.text.TextPaint r3 = r2.getPaint()
            r3.setFakeBoldText(r6)
            goto L_0x007a
        L_0x0061:
            if (r3 != r6) goto L_0x006f
            int r3 = r8.mCurrentTab
            if (r0 != r3) goto L_0x006f
            android.text.TextPaint r3 = r2.getPaint()
            r3.setFakeBoldText(r6)
            goto L_0x007a
        L_0x006f:
            int r3 = r8.mTextBold
            if (r3 != 0) goto L_0x007a
            android.text.TextPaint r3 = r2.getPaint()
            r3.setFakeBoldText(r4)
        L_0x007a:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x007d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tablayout.SlidingTabLayout.updateTabStyles():void");
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mCurrentTab = position;
        this.mCurrentPositionOffset = positionOffset;
        scrollToCurrentTab();
        invalidate();
    }

    public void onPageSelected(int position) {
        updateTabSelection(position);
    }

    public void onPageScrollStateChanged(int state) {
    }

    private void scrollToCurrentTab() {
        if (this.mTabCount > 0) {
            int offset = (int) (this.mCurrentPositionOffset * ((float) this.mTabsContainer.getChildAt(this.mCurrentTab).getWidth()));
            int newScrollX = this.mTabsContainer.getChildAt(this.mCurrentTab).getLeft() + offset;
            if (this.mCurrentTab > 0 || offset > 0) {
                calcIndicatorRect();
                newScrollX = (newScrollX - ((getWidth() / 2) - getPaddingLeft())) + ((this.mTabRect.right - this.mTabRect.left) / 2);
            }
            if (newScrollX != this.mLastScrollX) {
                this.mLastScrollX = newScrollX;
                scrollTo(newScrollX, 0);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0024, code lost:
        if (r7 == 0.0f) goto L_0x0026;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateTabSelection(int r12) {
        /*
            r11 = this;
            r0 = 0
        L_0x0001:
            int r1 = r11.mTabCount
            if (r0 >= r1) goto L_0x00af
            android.widget.LinearLayout r1 = r11.mTabsContainer
            android.view.View r1 = r1.getChildAt(r0)
            r2 = 1
            r3 = 0
            if (r0 != r12) goto L_0x0011
            r4 = 1
            goto L_0x0012
        L_0x0011:
            r4 = 0
        L_0x0012:
            r5 = 2131297853(0x7f09063d, float:1.8213663E38)
            android.view.View r5 = r1.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            if (r5 == 0) goto L_0x00ab
            r6 = 0
            if (r4 == 0) goto L_0x0026
            float r7 = r11.mTextSelectSize
            int r8 = (r7 > r6 ? 1 : (r7 == r6 ? 0 : -1))
            if (r8 != 0) goto L_0x0028
        L_0x0026:
            float r7 = r11.mTextsize
        L_0x0028:
            r5.setTextSize(r3, r7)
            if (r4 == 0) goto L_0x0030
            int r7 = r11.mTextSelectColor
            goto L_0x0032
        L_0x0030:
            int r7 = r11.mTextUnselectColor
        L_0x0032:
            r5.setTextColor(r7)
            int r7 = r11.mTextBold
            if (r7 != r2) goto L_0x0040
            android.text.TextPaint r2 = r5.getPaint()
            r2.setFakeBoldText(r4)
        L_0x0040:
            com.tablayout.listener.OnTabReleaseListener r2 = r11.mReleaseListener
            if (r2 == 0) goto L_0x0047
            r2.onTabRelease(r0, r4)
        L_0x0047:
            r2 = 2131297231(0x7f0903cf, float:1.8212401E38)
            android.view.View r2 = r1.findViewById(r2)
            com.tablayout.widget.MsgView r2 = (com.tablayout.widget.MsgView) r2
            if (r2 == 0) goto L_0x00ab
            int r7 = r2.getVisibility()
            if (r7 != 0) goto L_0x00ab
            android.graphics.Paint r7 = r11.mTextPaint
            float r8 = r5.getTextSize()
            r7.setTextSize(r8)
            android.graphics.Paint r7 = r11.mTextPaint
            java.lang.CharSequence r8 = r5.getText()
            java.lang.String r8 = r8.toString()
            float r7 = r7.measureText(r8)
            android.graphics.Paint r8 = r11.mTextPaint
            float r8 = r8.descent()
            android.graphics.Paint r9 = r11.mTextPaint
            float r9 = r9.ascent()
            float r8 = r8 - r9
            android.view.ViewGroup$LayoutParams r9 = r2.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r9 = (android.view.ViewGroup.MarginLayoutParams) r9
            float r10 = r11.mTabWidth
            int r6 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r6 < 0) goto L_0x0090
            r6 = 1073741824(0x40000000, float:2.0)
            float r10 = r10 / r6
            float r6 = r7 / r6
            float r10 = r10 + r6
            int r6 = (int) r10
            goto L_0x0094
        L_0x0090:
            float r6 = r11.mTabPadding
            float r6 = r6 + r7
            int r6 = (int) r6
        L_0x0094:
            r9.leftMargin = r6
            int r6 = r11.mHeight
            if (r6 <= 0) goto L_0x00a6
            float r3 = (float) r6
            float r3 = r3 - r8
            int r3 = (int) r3
            int r3 = r3 / 2
            r6 = 1065353216(0x3f800000, float:1.0)
            int r6 = r11.dp2px(r6)
            int r3 = r3 - r6
        L_0x00a6:
            r9.topMargin = r3
            r2.setLayoutParams(r9)
        L_0x00ab:
            int r0 = r0 + 1
            goto L_0x0001
        L_0x00af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tablayout.SlidingTabLayout.updateTabSelection(int):void");
    }

    private void calcIndicatorRect() {
        View currentTabView = this.mTabsContainer.getChildAt(this.mCurrentTab);
        float left = (float) currentTabView.getLeft();
        float right = (float) currentTabView.getRight();
        if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
            this.mTextPaint.setTextSize(this.mTextsize);
            this.margin = ((right - left) - this.mTextPaint.measureText(((TextView) currentTabView.findViewById(R.id.tv_tab_title)).getText().toString())) / 2.0f;
        }
        int i = this.mCurrentTab;
        if (i < this.mTabCount - 1) {
            View nextTabView = this.mTabsContainer.getChildAt(i + 1);
            float nextTabLeft = (float) nextTabView.getLeft();
            float nextTabRight = (float) nextTabView.getRight();
            float f = this.mCurrentPositionOffset;
            left += (nextTabLeft - left) * f;
            right += f * (nextTabRight - right);
            if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
                this.mTextPaint.setTextSize(this.mTextsize);
                float nextTextWidth = this.mTextPaint.measureText(((TextView) nextTabView.findViewById(R.id.tv_tab_title)).getText().toString());
                float f2 = this.margin;
                this.margin = f2 + (this.mCurrentPositionOffset * ((((nextTabRight - nextTabLeft) - nextTextWidth) / 2.0f) - f2));
            }
        }
        this.mIndicatorRect.left = (int) left;
        this.mIndicatorRect.right = (int) right;
        if (this.mIndicatorStyle == 0 && this.mIndicatorWidthEqualTitle) {
            this.mIndicatorRect.left = (int) ((this.margin + left) - 1.0f);
            this.mIndicatorRect.right = (int) ((right - this.margin) - 1.0f);
        }
        this.mTabRect.left = (int) left;
        this.mTabRect.right = (int) right;
        if (this.mIndicatorWidth >= 0.0f) {
            float indicatorLeft = ((float) currentTabView.getLeft()) + ((((float) currentTabView.getWidth()) - this.mIndicatorWidth) / 2.0f);
            int i2 = this.mCurrentTab;
            if (i2 < this.mTabCount - 1) {
                indicatorLeft += this.mCurrentPositionOffset * ((float) ((currentTabView.getWidth() / 2) + (this.mTabsContainer.getChildAt(i2 + 1).getWidth() / 2)));
            }
            this.mIndicatorRect.left = (int) indicatorLeft;
            Rect rect = this.mIndicatorRect;
            rect.right = (int) (((float) rect.left) + this.mIndicatorWidth);
        }
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
            calcIndicatorRect();
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
                    if (this.mEndIndicatorColor != 0) {
                        this.mIndicatorDrawable.setGradientType(0);
                        this.mIndicatorDrawable.setColors(new int[]{this.mIndicatorColor, this.mEndIndicatorColor});
                    } else {
                        this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                    }
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (int) (((float) (this.mIndicatorRect.right + paddingLeft)) - this.mIndicatorMarginRight), (int) (this.mIndicatorMarginTop + this.mIndicatorHeight));
                    this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                    this.mIndicatorDrawable.draw(canvas);
                }
            } else if (this.mIndicatorHeight > 0.0f) {
                this.mIndicatorDrawable.setCornerRadius(this.mIndicatorCornerRadius);
                if (this.mEndIndicatorColor != 0) {
                    this.mIndicatorDrawable.setGradientType(0);
                    this.mIndicatorDrawable.setColors(new int[]{this.mIndicatorColor, this.mEndIndicatorColor});
                } else {
                    this.mIndicatorDrawable.setColor(this.mIndicatorColor);
                }
                if (this.mIndicatorGravity == 80) {
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (height - ((int) this.mIndicatorHeight)) - ((int) this.mIndicatorMarginBottom), (this.mIndicatorRect.right + paddingLeft) - ((int) this.mIndicatorMarginRight), height - ((int) this.mIndicatorMarginBottom));
                } else {
                    this.mIndicatorDrawable.setBounds(((int) this.mIndicatorMarginLeft) + paddingLeft + this.mIndicatorRect.left, (int) this.mIndicatorMarginTop, (this.mIndicatorRect.right + paddingLeft) - ((int) this.mIndicatorMarginRight), ((int) this.mIndicatorHeight) + ((int) this.mIndicatorMarginTop));
                }
                this.mIndicatorDrawable.draw(canvas);
            }
        }
    }

    public void setCurrentTab(int currentTab) {
        this.mCurrentTab = currentTab;
        this.mViewPager.setCurrentItem(currentTab);
    }

    public void setCurrentTab(int currentTab, boolean smoothScroll) {
        this.mCurrentTab = currentTab;
        this.mViewPager.setCurrentItem(currentTab, smoothScroll);
    }

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = (float) dp2px(tabPadding);
        updateTabStyles();
    }

    public void setmTabPaddingBottom(float tabPadding) {
        this.mTabPaddingBottom = (float) dp2px(tabPadding);
        updateTabStyles();
    }

    public void setmTabPaddingTop(float tabPadding) {
        this.mTabPaddingTop = (float) dp2px(tabPadding);
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

    public void setIndicatorEndColor(int indicatorColor) {
        this.mEndIndicatorColor = indicatorColor;
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

    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
        invalidate();
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

    public void setTextSelectSize(float textSize) {
        this.mTextSelectSize = (float) sp2px(textSize);
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

    public void setSnapOnTabClick(boolean snapOnTabClick) {
        this.mSnapOnTabClick = snapOnTabClick;
    }

    public void setMsgViewBackgroundColor(int mMsgViewBackgroundColor2) {
        this.mMsgViewBackgroundColor = mMsgViewBackgroundColor2;
    }

    public void setMsgViewWidth(int mMsgViewWidth2) {
        if (mMsgViewWidth2 > 0) {
            this.mMsgViewWidth = dp2px((float) mMsgViewWidth2);
        }
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

    public int getIndicatorEndColor() {
        return this.mEndIndicatorColor;
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

    public float getTextSize() {
        return this.mTextsize;
    }

    public float getTextSelectSize() {
        return this.mTextSelectSize;
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
                setMsgMargin(position, 0.0f, 1.0f);
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
        if (tipView != null && tipView.getVisibility() == 0) {
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
            float textWidth = this.mTextPaint.measureText(((TextView) tabView.findViewById(R.id.tv_tab_title)).getText().toString());
            float textHeight = this.mTextPaint.descent() - this.mTextPaint.ascent();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tipView.getLayoutParams();
            float f = this.mTabWidth;
            lp.leftMargin = (int) ((f >= 0.0f ? (f / 2.0f) + (textWidth / 2.0f) : this.mTabPadding + textWidth) + ((float) dp2px(leftPadding)));
            int i2 = this.mHeight;
            lp.topMargin = i2 > 0 ? (((int) (((float) i2) - textHeight)) / 2) - dp2px(bottomPadding) : 0;
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

    public void setOnTabReleaseListener(OnTabReleaseListener listener) {
        this.mReleaseListener = listener;
    }

    class InnerPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();
        private String[] titles;

        public InnerPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments2, String[] titles2) {
            super(fm);
            this.fragments = fragments2;
            this.titles = titles2;
        }

        public int getCount() {
            return this.fragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return this.titles[position];
        }

        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        public int getItemPosition(Object object) {
            return -2;
        }
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
                scrollToCurrentTab();
            }
        }
        super.onRestoreInstanceState(state);
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
