package im.bclpbkiauv.ui.hui.friendscircle_v1.view.flowLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.text.TextUtilsCompat;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FlowLayout extends ViewGroup {
    private static final int CENTER = 0;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final String TAG = "FlowLayout";
    private List<View> lineViews;
    protected List<List<View>> mAllViews;
    private int mGravity;
    protected List<Integer> mLineHeight;
    protected List<Integer> mLineWidth;

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
        this.mLineWidth = new ArrayList();
        this.lineViews = new ArrayList();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        this.mGravity = ta.getInt(1, -1);
        if (TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
            if (this.mGravity == -1) {
                this.mGravity = 1;
            } else {
                this.mGravity = -1;
            }
        }
        ta.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2;
        int sizeWidth;
        int sizeHeight;
        int lineHeight;
        int lineWidth;
        int sizeWidth2 = View.MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = View.MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight2 = View.MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = View.MeasureSpec.getMode(heightMeasureSpec);
        int width = 0;
        int height = 0;
        int lineHeight2 = 0;
        int lineHeight3 = 0;
        int cCount = getChildCount();
        int i3 = 0;
        while (i3 < cCount) {
            View child = getChildAt(i3);
            if (child.getVisibility() != 8) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                sizeHeight = sizeHeight2;
                int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                View view = child;
                int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                sizeWidth = sizeWidth2;
                if (lineHeight2 + childWidth > (sizeWidth2 - getPaddingLeft()) - getPaddingRight()) {
                    width = Math.max(width, lineHeight2);
                    lineWidth = childWidth;
                    height += lineHeight3;
                    lineHeight = childHeight;
                } else {
                    lineWidth = lineHeight2 + childWidth;
                    lineHeight = Math.max(lineHeight3, childHeight);
                }
                if (i3 == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                    lineHeight3 = lineHeight;
                    lineHeight2 = lineWidth;
                } else {
                    lineHeight3 = lineHeight;
                    lineHeight2 = lineWidth;
                }
            } else if (i3 == cCount - 1) {
                width = Math.max(lineHeight2, width);
                height += lineHeight3;
                int i4 = widthMeasureSpec;
                int i5 = heightMeasureSpec;
                sizeWidth = sizeWidth2;
                sizeHeight = sizeHeight2;
            } else {
                int i6 = widthMeasureSpec;
                int i7 = heightMeasureSpec;
                sizeWidth = sizeWidth2;
                sizeHeight = sizeHeight2;
            }
            i3++;
            sizeHeight2 = sizeHeight;
            sizeWidth2 = sizeWidth;
        }
        int i8 = widthMeasureSpec;
        int i9 = heightMeasureSpec;
        int sizeWidth3 = sizeWidth2;
        int sizeHeight3 = sizeHeight2;
        if (modeWidth == 1073741824) {
            i = sizeWidth3;
        } else {
            i = getPaddingLeft() + width + getPaddingRight();
        }
        if (modeHeight == 1073741824) {
            i2 = sizeHeight3;
        } else {
            i2 = getPaddingTop() + height + getPaddingBottom();
        }
        setMeasuredDimension(i, i2);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int i;
        int width;
        int lc;
        int tc;
        FlowLayout flowLayout = this;
        flowLayout.mAllViews.clear();
        flowLayout.mLineHeight.clear();
        flowLayout.mLineWidth.clear();
        flowLayout.lineViews.clear();
        int width2 = getWidth();
        int lineWidth = 0;
        int lineHeight = 0;
        int cCount = getChildCount();
        int i2 = 0;
        while (true) {
            i = 8;
            if (i2 >= cCount) {
                break;
            }
            View child = flowLayout.getChildAt(i2);
            if (child.getVisibility() != 8) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > (width2 - getPaddingLeft()) - getPaddingRight()) {
                    flowLayout.mLineHeight.add(Integer.valueOf(lineHeight));
                    flowLayout.mAllViews.add(flowLayout.lineViews);
                    flowLayout.mLineWidth.add(Integer.valueOf(lineWidth));
                    lineWidth = 0;
                    lineHeight = lp.topMargin + childHeight + lp.bottomMargin;
                    flowLayout.lineViews = new ArrayList();
                }
                lineWidth += lp.leftMargin + childWidth + lp.rightMargin;
                lineHeight = Math.max(lineHeight, lp.topMargin + childHeight + lp.bottomMargin);
                flowLayout.lineViews.add(child);
            }
            i2++;
        }
        flowLayout.mLineHeight.add(Integer.valueOf(lineHeight));
        flowLayout.mLineWidth.add(Integer.valueOf(lineWidth));
        flowLayout.mAllViews.add(flowLayout.lineViews);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNum = flowLayout.mAllViews.size();
        int lastLayoutLineNum = -1;
        int i3 = 0;
        while (i3 < lineNum) {
            flowLayout.lineViews = flowLayout.mAllViews.get(i3);
            int lineHeight2 = flowLayout.mLineHeight.get(i3).intValue();
            int currentLineWidth = flowLayout.mLineWidth.get(i3).intValue();
            int i4 = flowLayout.mGravity;
            if (i4 == -1) {
                left = getPaddingLeft();
            } else if (i4 == 0) {
                left = ((width2 - currentLineWidth) / 2) + getPaddingLeft();
            } else if (i4 == 1) {
                left = (width2 - (getPaddingLeft() + currentLineWidth)) - getPaddingRight();
                Collections.reverse(flowLayout.lineViews);
            }
            int j = 0;
            while (j < flowLayout.lineViews.size()) {
                View child2 = flowLayout.lineViews.get(j);
                if (child2.getVisibility() == i) {
                    width = width2;
                } else {
                    ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) child2.getLayoutParams();
                    if (lastLayoutLineNum != i3) {
                        lc = left;
                    } else {
                        lc = left + lp2.leftMargin;
                    }
                    if (i3 == 0) {
                        tc = top;
                    } else {
                        tc = top + lp2.topMargin;
                    }
                    int rc = child2.getMeasuredWidth() + lc;
                    width = width2;
                    child2.layout(lc, tc, rc, tc + child2.getMeasuredHeight());
                    int i5 = rc;
                    left += child2.getMeasuredWidth() + lp2.leftMargin + lp2.rightMargin;
                }
                j++;
                i = 8;
                flowLayout = this;
                width2 = width;
            }
            int width3 = width2;
            top += lineHeight2;
            if (lastLayoutLineNum != i3) {
                lastLayoutLineNum = i3;
            }
            i3++;
            i = 8;
            flowLayout = this;
            width2 = width3;
        }
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.MarginLayoutParams(p);
    }
}
