package im.bclpbkiauv.ui.components.recyclerview.manager;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.LogUtils;
import java.lang.reflect.Field;

public class FullyLinearLayoutManager extends LinearLayoutManager {
    private static final int CHILD_HEIGHT = 1;
    private static final int CHILD_WIDTH = 0;
    private static final int DEFAULT_CHILD_SIZE = 100;
    private static boolean canMakeInsetsDirty = true;
    private static Field insetsDirtyField = null;
    private final int[] childDimensions;
    private int childSize;
    private boolean hasChildSize;
    private int overScrollMode;
    private final Rect tmpRect;
    private final RecyclerView view;

    public FullyLinearLayoutManager(Context context) {
        super(context);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.view = null;
    }

    public FullyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.view = null;
    }

    public FullyLinearLayoutManager(RecyclerView view2) {
        super(view2.getContext());
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.view = view2;
        this.overScrollMode = ViewCompat.getOverScrollMode(view2);
    }

    public FullyLinearLayoutManager(RecyclerView view2, int orientation, boolean reverseLayout) {
        super(view2.getContext(), orientation, reverseLayout);
        this.childDimensions = new int[2];
        this.childSize = 100;
        this.overScrollMode = 0;
        this.tmpRect = new Rect();
        this.view = view2;
        this.overScrollMode = ViewCompat.getOverScrollMode(view2);
    }

    public void setOverScrollMode(int overScrollMode2) {
        if (overScrollMode2 < 0 || overScrollMode2 > 2) {
            throw new IllegalArgumentException("Unknown overscroll mode: " + overScrollMode2);
        }
        RecyclerView recyclerView = this.view;
        if (recyclerView != null) {
            this.overScrollMode = overScrollMode2;
            ViewCompat.setOverScrollMode(recyclerView, overScrollMode2);
            return;
        }
        throw new IllegalStateException("view == null");
    }

    public static int makeUnspecifiedSpec() {
        return View.MeasureSpec.makeMeasureSpec(0, 0);
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        boolean vertical;
        int width;
        int height;
        int adapterItemCount;
        int stateItemCount;
        int i;
        int stateItemCount2;
        int i2;
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);
        int i3 = 0;
        int i4 = 1;
        boolean hasWidthSize = widthMode != 0;
        boolean hasHeightSize = heightMode != 0;
        boolean exactWidth = widthMode == 1073741824;
        boolean exactHeight = heightMode == 1073741824;
        int unspecified = makeUnspecifiedSpec();
        if (!exactWidth || !exactHeight) {
            boolean vertical2 = getOrientation() == 1;
            initChildDimensions(widthSize, heightSize, vertical2);
            recycler.clear();
            int stateItemCount3 = state.getItemCount();
            int adapterItemCount2 = getItemCount();
            int i5 = 0;
            int width2 = 0;
            int height2 = 0;
            while (true) {
                if (i5 >= adapterItemCount2) {
                    int i6 = i5;
                    int i7 = adapterItemCount2;
                    int i8 = stateItemCount3;
                    vertical = vertical2;
                    break;
                }
                if (!vertical2) {
                    adapterItemCount = adapterItemCount2;
                    int stateItemCount4 = stateItemCount3;
                    vertical = vertical2;
                    int i9 = i5;
                    if (!this.hasChildSize) {
                        int stateItemCount5 = stateItemCount4;
                        if (i9 < stateItemCount5) {
                            stateItemCount = stateItemCount5;
                            i = i9;
                            measureChild(recycler, i9, unspecified, heightSize, this.childDimensions);
                        } else {
                            stateItemCount = stateItemCount5;
                            i = i9;
                            logMeasureWarning(i);
                        }
                    } else {
                        i = i9;
                        stateItemCount = stateItemCount4;
                    }
                    int[] iArr = this.childDimensions;
                    int width3 = width2 + iArr[0];
                    if (i == 0) {
                        height2 = iArr[1];
                    }
                    if (hasWidthSize && width3 >= widthSize) {
                        width2 = width3;
                        break;
                    }
                    width2 = width3;
                } else {
                    if (this.hasChildSize) {
                        adapterItemCount = adapterItemCount2;
                        stateItemCount2 = stateItemCount3;
                        vertical = vertical2;
                        i2 = i5;
                    } else if (i5 < stateItemCount3) {
                        adapterItemCount = adapterItemCount2;
                        stateItemCount2 = stateItemCount3;
                        vertical = vertical2;
                        measureChild(recycler, i5, widthSize, unspecified, this.childDimensions);
                        i2 = i5;
                    } else {
                        adapterItemCount = adapterItemCount2;
                        stateItemCount2 = stateItemCount3;
                        vertical = vertical2;
                        i2 = i5;
                        logMeasureWarning(i2);
                    }
                    int[] iArr2 = this.childDimensions;
                    int height3 = height2 + iArr2[i4];
                    if (i2 == 0) {
                        width2 = iArr2[0];
                    }
                    if (hasHeightSize && height3 >= heightSize) {
                        height2 = height3;
                        int i10 = stateItemCount2;
                        break;
                    }
                    height2 = height3;
                    i = i2;
                    stateItemCount = stateItemCount2;
                }
                i5 = i + 1;
                stateItemCount3 = stateItemCount;
                adapterItemCount2 = adapterItemCount;
                vertical2 = vertical;
                i4 = 1;
            }
            if (exactWidth) {
                width = widthSize;
            } else {
                width = width2 + getPaddingLeft() + getPaddingRight();
                if (hasWidthSize) {
                    width = Math.min(width, widthSize);
                }
            }
            if (exactHeight) {
                height = heightSize;
            } else {
                height = height2 + getPaddingTop() + getPaddingBottom();
                if (hasHeightSize) {
                    height = Math.min(height, heightSize);
                }
            }
            setMeasuredDimension(width, height);
            if (this.view != null && this.overScrollMode == 1) {
                boolean fit = (vertical && (!hasHeightSize || height < heightSize)) || (!vertical && (!hasWidthSize || width < widthSize));
                RecyclerView recyclerView = this.view;
                if (fit) {
                    i3 = 2;
                }
                ViewCompat.setOverScrollMode(recyclerView, i3);
                return;
            }
            return;
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    private void logMeasureWarning(int child) {
        LogUtils.dTag("LinearLayoutManager", "Can't measure child #" + child + ", previously used dimensions will be reused.To remove this message either use #setChildSize() method or don't run RecyclerView animations");
    }

    private void initChildDimensions(int width, int height, boolean vertical) {
        int[] iArr = this.childDimensions;
        if (iArr[0] != 0 || iArr[1] != 0) {
            return;
        }
        if (vertical) {
            iArr[0] = width;
            iArr[1] = this.childSize;
            return;
        }
        iArr[0] = this.childSize;
        iArr[1] = height;
    }

    public void setOrientation(int orientation) {
        if (!(this.childDimensions == null || getOrientation() == orientation)) {
            int[] iArr = this.childDimensions;
            iArr[0] = 0;
            iArr[1] = 0;
        }
        super.setOrientation(orientation);
    }

    public void clearChildSize() {
        this.hasChildSize = false;
        setChildSize(100);
    }

    public void setChildSize(int childSize2) {
        this.hasChildSize = true;
        if (this.childSize != childSize2) {
            this.childSize = childSize2;
            requestLayout();
        }
    }

    private void measureChild(RecyclerView.Recycler recycler, int position, int widthSize, int heightSize, int[] dimensions) {
        try {
            View child = recycler.getViewForPosition(position);
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();
            int hMargin = p.leftMargin + p.rightMargin;
            int vMargin = p.topMargin + p.bottomMargin;
            makeInsetsDirty(p);
            calculateItemDecorationsForChild(child, this.tmpRect);
            int i = widthSize;
            int i2 = heightSize;
            child.measure(getChildMeasureSpec(i, hPadding + hMargin + getRightDecorationWidth(child) + getLeftDecorationWidth(child), p.width, canScrollHorizontally()), getChildMeasureSpec(i2, vPadding + vMargin + getTopDecorationHeight(child) + getBottomDecorationHeight(child), p.height, canScrollVertically()));
            dimensions[0] = getDecoratedMeasuredWidth(child) + p.leftMargin + p.rightMargin;
            dimensions[1] = getDecoratedMeasuredHeight(child) + p.bottomMargin + p.topMargin;
            makeInsetsDirty(p);
            recycler.recycleView(child);
        } catch (IndexOutOfBoundsException e) {
            RecyclerView.Recycler recycler2 = recycler;
            int i3 = widthSize;
            int i4 = heightSize;
            LogUtils.dTag("LinearLayoutManager doesn't work well with animations. Consider switching them off", e);
        }
    }

    private static void makeInsetsDirty(RecyclerView.LayoutParams p) {
        if (canMakeInsetsDirty) {
            try {
                if (insetsDirtyField == null) {
                    Field declaredField = RecyclerView.LayoutParams.class.getDeclaredField("mInsetsDirty");
                    insetsDirtyField = declaredField;
                    declaredField.setAccessible(true);
                }
                insetsDirtyField.set(p, true);
            } catch (NoSuchFieldException e) {
                onMakeInsertDirtyFailed();
            } catch (IllegalAccessException e2) {
                onMakeInsertDirtyFailed();
            }
        }
    }

    private static void onMakeInsertDirtyFailed() {
        canMakeInsetsDirty = false;
        LogUtils.dTag("LinearLayoutManager", "Can't make LayoutParams insets dirty, decorations measurements might be incorrect");
    }
}
