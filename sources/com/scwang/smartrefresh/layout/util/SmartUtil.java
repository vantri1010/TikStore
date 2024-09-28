package com.scwang.smartrefresh.layout.util;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ScrollView;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ScrollingView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class SmartUtil implements Interpolator {
    public static int INTERPOLATOR_DECELERATE = 1;
    public static int INTERPOLATOR_VISCOUS_FLUID = 0;
    private static final float VISCOUS_FLUID_NORMALIZE;
    private static final float VISCOUS_FLUID_OFFSET;
    private static final float VISCOUS_FLUID_SCALE = 8.0f;
    private static float density = Resources.getSystem().getDisplayMetrics().density;
    private int type;

    static {
        float viscousFluid = 1.0f / viscousFluid(1.0f);
        VISCOUS_FLUID_NORMALIZE = viscousFluid;
        VISCOUS_FLUID_OFFSET = 1.0f - (viscousFluid * viscousFluid(1.0f));
    }

    public SmartUtil(int type2) {
        this.type = type2;
    }

    public static int measureViewHeight(View view) {
        int childHeightSpec;
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(-1, -2);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        if (p.height > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(p.height, 1073741824);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }

    public static void scrollListBy(AbsListView listView, int y) {
        View firstView;
        if (Build.VERSION.SDK_INT >= 19) {
            listView.scrollListBy(y);
        } else if (listView instanceof ListView) {
            int firstPosition = listView.getFirstVisiblePosition();
            if (firstPosition != -1 && (firstView = listView.getChildAt(0)) != null) {
                ((ListView) listView).setSelectionFromTop(firstPosition, firstView.getTop() - y);
            }
        } else {
            listView.smoothScrollBy(y, 0);
        }
    }

    public static boolean isScrollableView(View view) {
        return (view instanceof AbsListView) || (view instanceof ScrollView) || (view instanceof ScrollingView) || (view instanceof WebView) || (view instanceof NestedScrollingChild);
    }

    public static boolean isContentView(View view) {
        return isScrollableView(view) || (view instanceof ViewPager) || (view instanceof NestedScrollingParent);
    }

    public static void fling(View scrollableView, int velocity) {
        if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof AbsListView) {
            if (Build.VERSION.SDK_INT >= 21) {
                ((AbsListView) scrollableView).fling(velocity);
            }
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocity);
        } else if (scrollableView instanceof NestedScrollView) {
            ((NestedScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocity);
        }
    }

    public static boolean canRefresh(View targetView, PointF touch) {
        if (canScrollVertically(targetView, -1) && targetView.getVisibility() == 0) {
            return false;
        }
        if (!(targetView instanceof ViewGroup) || touch == null) {
            return true;
        }
        ViewGroup viewGroup = (ViewGroup) targetView;
        int childCount = viewGroup.getChildCount();
        PointF point = new PointF();
        int i = childCount;
        while (i > 0) {
            View child = viewGroup.getChildAt(i - 1);
            if (!isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                i--;
            } else if ("fixed".equals(child.getTag()) || "fixed-bottom".equals(child.getTag())) {
                return false;
            } else {
                touch.offset(point.x, point.y);
                boolean can = canRefresh(child, touch);
                touch.offset(-point.x, -point.y);
                return can;
            }
        }
        return true;
    }

    public static boolean canLoadMore(View targetView, PointF touch, boolean contentFull) {
        if (canScrollVertically(targetView, 1) && targetView.getVisibility() == 0) {
            return false;
        }
        if ((targetView instanceof ViewGroup) && touch != null && !isScrollableView(targetView)) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            int i = childCount;
            while (i > 0) {
                View child = viewGroup.getChildAt(i - 1);
                if (!isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                    i--;
                } else if ("fixed".equals(child.getTag()) || "fixed-top".equals(child.getTag())) {
                    return false;
                } else {
                    touch.offset(point.x, point.y);
                    boolean can = canLoadMore(child, touch, contentFull);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        if (contentFull || canScrollVertically(targetView, -1)) {
            return true;
        }
        return false;
    }

    public static boolean canScrollVertically(View targetView, int direction) {
        if (Build.VERSION.SDK_INT >= 14) {
            return targetView.canScrollVertically(direction);
        }
        if (targetView instanceof AbsListView) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            AbsListView absListView = (AbsListView) targetView;
            int childCount = viewGroup.getChildCount();
            if (direction > 0) {
                if (childCount <= 0) {
                    return false;
                }
                if (absListView.getLastVisiblePosition() < childCount - 1 || viewGroup.getChildAt(childCount - 1).getBottom() > targetView.getPaddingBottom()) {
                    return true;
                }
                return false;
            } else if (childCount <= 0) {
                return false;
            } else {
                if (absListView.getFirstVisiblePosition() > 0 || viewGroup.getChildAt(0).getTop() < targetView.getPaddingTop()) {
                    return true;
                }
                return false;
            }
        } else if (direction > 0) {
            if (targetView.getScrollY() < 0) {
                return true;
            }
            return false;
        } else if (targetView.getScrollY() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTransformedTouchPointInView(View group, View child, float x, float y, PointF outLocalPoint) {
        if (child.getVisibility() != 0) {
            return false;
        }
        float[] point = {x, y};
        point[0] = point[0] + ((float) (group.getScrollX() - child.getLeft()));
        point[1] = point[1] + ((float) (group.getScrollY() - child.getTop()));
        boolean isInView = point[0] >= 0.0f && point[1] >= 0.0f && point[0] < ((float) child.getWidth()) && point[1] < ((float) child.getHeight());
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0] - x, point[1] - y);
        }
        return isInView;
    }

    public static int dp2px(float dpValue) {
        return (int) ((density * dpValue) + 0.5f);
    }

    public static float px2dp(int pxValue) {
        return ((float) pxValue) / density;
    }

    private static float viscousFluid(float x) {
        float x2 = x * 8.0f;
        if (x2 < 1.0f) {
            return x2 - (1.0f - ((float) Math.exp((double) (-x2))));
        }
        return 0.36787945f + ((1.0f - 0.36787945f) * (1.0f - ((float) Math.exp((double) (1.0f - x2)))));
    }

    public float getInterpolation(float input) {
        if (this.type == INTERPOLATOR_DECELERATE) {
            return 1.0f - ((1.0f - input) * (1.0f - input));
        }
        float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
        if (interpolated > 0.0f) {
            return VISCOUS_FLUID_OFFSET + interpolated;
        }
        return interpolated;
    }
}
