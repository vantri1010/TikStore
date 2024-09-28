package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class GridLayoutManagerFixed extends GridLayoutManager {
    private ArrayList<View> additionalViews = new ArrayList<>(4);
    private boolean canScrollVertically = true;

    public GridLayoutManagerFixed(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerFixed(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    /* access modifiers changed from: protected */
    public boolean hasSiblingChild(int position) {
        return false;
    }

    public void setCanScrollVertically(boolean value) {
        this.canScrollVertically = value;
    }

    public boolean canScrollVertically() {
        return this.canScrollVertically;
    }

    /* access modifiers changed from: protected */
    public void recycleViewsFromStart(RecyclerView.Recycler recycler, int scrollingOffset, int noRecycleSpace) {
        if (scrollingOffset >= 0) {
            int childCount = getChildCount();
            if (this.mShouldReverseLayout) {
                for (int i = childCount - 1; i >= 0; i--) {
                    View child = getChildAt(i);
                    if (child.getBottom() + ((RecyclerView.LayoutParams) child.getLayoutParams()).bottomMargin > scrollingOffset || child.getTop() + child.getHeight() > scrollingOffset) {
                        recycleChildren(recycler, childCount - 1, i);
                        return;
                    }
                }
                return;
            }
            for (int i2 = 0; i2 < childCount; i2++) {
                View child2 = getChildAt(i2);
                if (this.mOrientationHelper.getDecoratedEnd(child2) > scrollingOffset || this.mOrientationHelper.getTransformedEndWithDecoration(child2) > scrollingOffset) {
                    recycleChildren(recycler, 0, i2);
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int[] calculateItemBorders(int[] cachedBorders, int spanCount, int totalSpace) {
        if (!(cachedBorders != null && cachedBorders.length == spanCount + 1 && cachedBorders[cachedBorders.length - 1] == totalSpace)) {
            cachedBorders = new int[(spanCount + 1)];
        }
        cachedBorders[0] = 0;
        for (int i = 1; i <= spanCount; i++) {
            cachedBorders[i] = (int) Math.ceil((double) ((((float) i) / ((float) spanCount)) * ((float) totalSpace)));
        }
        return cachedBorders;
    }

    public boolean shouldLayoutChildFromOpositeSide(View child) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void measureChild(View view, int otherDirParentSpecMode, boolean alreadyMeasured) {
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        Rect decorInsets = lp.mDecorInsets;
        int verticalInsets = decorInsets.top + decorInsets.bottom + lp.topMargin + lp.bottomMargin;
        measureChildWithDecorationsAndMargin(view, getChildMeasureSpec(this.mCachedBorders[lp.mSpanSize], otherDirParentSpecMode, decorInsets.left + decorInsets.right + lp.leftMargin + lp.rightMargin, lp.width, false), getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), verticalInsets, lp.height, true), alreadyMeasured);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x009d, code lost:
        r0 = r9.mCurrentPosition;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void layoutChunk(androidx.recyclerview.widget.RecyclerView.Recycler r30, androidx.recyclerview.widget.RecyclerView.State r31, androidx.recyclerview.widget.LinearLayoutManager.LayoutState r32, androidx.recyclerview.widget.LinearLayoutManager.LayoutChunkResult r33) {
        /*
            r29 = this;
            r6 = r29
            r7 = r30
            r8 = r31
            r9 = r32
            r10 = r33
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mOrientationHelper
            int r11 = r0.getModeInOther()
            int r0 = r9.mItemDirection
            r12 = 0
            r13 = 1
            if (r0 != r13) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            r14 = r0
            r0 = 1
            r10.mConsumed = r12
            r15 = 0
            int r5 = r9.mCurrentPosition
            int r1 = r9.mLayoutDirection
            r4 = -1
            if (r1 == r4) goto L_0x007c
            int r1 = r9.mCurrentPosition
            boolean r1 = r6.hasSiblingChild(r1)
            if (r1 == 0) goto L_0x007c
            int r1 = r9.mCurrentPosition
            int r1 = r1 + r13
            android.view.View r1 = r6.findViewByPosition(r1)
            if (r1 != 0) goto L_0x007c
            int r1 = r9.mCurrentPosition
            int r1 = r1 + r13
            boolean r1 = r6.hasSiblingChild(r1)
            if (r1 == 0) goto L_0x0046
            int r1 = r9.mCurrentPosition
            int r1 = r1 + 3
            r9.mCurrentPosition = r1
            goto L_0x004c
        L_0x0046:
            int r1 = r9.mCurrentPosition
            int r1 = r1 + 2
            r9.mCurrentPosition = r1
        L_0x004c:
            int r1 = r9.mCurrentPosition
            int r2 = r9.mCurrentPosition
        L_0x0050:
            if (r2 <= r5) goto L_0x007a
            android.view.View r3 = r9.next(r7)
            java.util.ArrayList<android.view.View> r4 = r6.additionalViews
            r4.add(r3)
            if (r2 == r1) goto L_0x0075
            android.graphics.Rect r4 = r6.mDecorInsets
            r6.calculateItemDecorationsForChild(r3, r4)
            r6.measureChild(r3, r11, r12)
            androidx.recyclerview.widget.OrientationHelper r4 = r6.mOrientationHelper
            int r4 = r4.getDecoratedMeasurement(r3)
            int r12 = r9.mOffset
            int r12 = r12 - r4
            r9.mOffset = r12
            int r12 = r9.mAvailable
            int r12 = r12 + r4
            r9.mAvailable = r12
        L_0x0075:
            int r2 = r2 + -1
            r4 = -1
            r12 = 0
            goto L_0x0050
        L_0x007a:
            r9.mCurrentPosition = r1
        L_0x007c:
            if (r0 == 0) goto L_0x02fb
            r1 = 0
            r2 = 0
            int r3 = r6.mSpanCount
            java.util.ArrayList<android.view.View> r4 = r6.additionalViews
            boolean r4 = r4.isEmpty()
            r4 = r4 ^ r13
            r0 = r4
            int r12 = r9.mCurrentPosition
            r18 = r0
            r4 = r1
            r19 = r2
        L_0x0091:
            int r0 = r6.mSpanCount
            if (r4 >= r0) goto L_0x00ea
            boolean r0 = r9.hasMore(r8)
            if (r0 == 0) goto L_0x00ea
            if (r3 <= 0) goto L_0x00ea
            int r0 = r9.mCurrentPosition
            int r1 = r6.getSpanSize(r7, r8, r0)
            int r3 = r3 - r1
            if (r3 >= 0) goto L_0x00a7
            goto L_0x00ea
        L_0x00a7:
            java.util.ArrayList<android.view.View> r2 = r6.additionalViews
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x00c8
            java.util.ArrayList<android.view.View> r2 = r6.additionalViews
            r13 = 0
            java.lang.Object r2 = r2.get(r13)
            android.view.View r2 = (android.view.View) r2
            r20 = r2
            java.util.ArrayList<android.view.View> r2 = r6.additionalViews
            r2.remove(r13)
            int r2 = r9.mCurrentPosition
            r13 = 1
            int r2 = r2 - r13
            r9.mCurrentPosition = r2
            r2 = r20
            goto L_0x00cc
        L_0x00c8:
            android.view.View r2 = r9.next(r7)
        L_0x00cc:
            if (r2 != 0) goto L_0x00cf
            goto L_0x00ea
        L_0x00cf:
            int r19 = r19 + r1
            android.view.View[] r13 = r6.mSet
            r13[r4] = r2
            int r4 = r4 + 1
            int r13 = r9.mLayoutDirection
            r20 = r1
            r1 = -1
            if (r13 != r1) goto L_0x00e8
            if (r3 > 0) goto L_0x00e8
            boolean r1 = r6.hasSiblingChild(r0)
            if (r1 == 0) goto L_0x00e8
            r18 = 1
        L_0x00e8:
            r13 = 1
            goto L_0x0091
        L_0x00ea:
            r13 = r3
            if (r4 != 0) goto L_0x00f1
            r0 = 1
            r10.mFinished = r0
            return
        L_0x00f1:
            r0 = 0
            r1 = 0
            r6.assignSpans(r7, r8, r4, r14)
            r2 = 0
            r3 = r0
            r20 = r1
        L_0x00fa:
            if (r2 >= r4) goto L_0x0155
            android.view.View[] r0 = r6.mSet
            r0 = r0[r2]
            java.util.List<androidx.recyclerview.widget.RecyclerView$ViewHolder> r1 = r9.mScrapList
            if (r1 != 0) goto L_0x0110
            if (r14 == 0) goto L_0x010b
            r6.addView(r0)
            r1 = 0
            goto L_0x011a
        L_0x010b:
            r1 = 0
            r6.addView(r0, r1)
            goto L_0x011a
        L_0x0110:
            r1 = 0
            if (r14 == 0) goto L_0x0117
            r6.addDisappearingView(r0)
            goto L_0x011a
        L_0x0117:
            r6.addDisappearingView(r0, r1)
        L_0x011a:
            android.graphics.Rect r1 = r6.mDecorInsets
            r6.calculateItemDecorationsForChild(r0, r1)
            r1 = 0
            r6.measureChild(r0, r11, r1)
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mOrientationHelper
            int r1 = r1.getDecoratedMeasurement(r0)
            if (r1 <= r3) goto L_0x012c
            r3 = r1
        L_0x012c:
            android.view.ViewGroup$LayoutParams r21 = r0.getLayoutParams()
            r22 = r1
            r1 = r21
            androidx.recyclerview.widget.GridLayoutManager$LayoutParams r1 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r1
            r21 = 1065353216(0x3f800000, float:1.0)
            r23 = r3
            androidx.recyclerview.widget.OrientationHelper r3 = r6.mOrientationHelper
            int r3 = r3.getDecoratedMeasurementInOther(r0)
            float r3 = (float) r3
            float r3 = r3 * r21
            r21 = r0
            int r0 = r1.mSpanSize
            float r0 = (float) r0
            float r3 = r3 / r0
            int r0 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r0 <= 0) goto L_0x0150
            r0 = r3
            r20 = r0
        L_0x0150:
            int r2 = r2 + 1
            r3 = r23
            goto L_0x00fa
        L_0x0155:
            r0 = 0
        L_0x0156:
            if (r0 >= r4) goto L_0x01b9
            android.view.View[] r1 = r6.mSet
            r1 = r1[r0]
            androidx.recyclerview.widget.OrientationHelper r2 = r6.mOrientationHelper
            int r2 = r2.getDecoratedMeasurement(r1)
            if (r2 == r3) goto L_0x01a6
            android.view.ViewGroup$LayoutParams r2 = r1.getLayoutParams()
            androidx.recyclerview.widget.GridLayoutManager$LayoutParams r2 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r2
            r21 = r5
            android.graphics.Rect r5 = r2.mDecorInsets
            int r7 = r5.top
            int r8 = r5.bottom
            int r7 = r7 + r8
            int r8 = r2.topMargin
            int r7 = r7 + r8
            int r8 = r2.bottomMargin
            int r7 = r7 + r8
            int r8 = r5.left
            r22 = r11
            int r11 = r5.right
            int r8 = r8 + r11
            int r11 = r2.leftMargin
            int r8 = r8 + r11
            int r11 = r2.rightMargin
            int r8 = r8 + r11
            int[] r11 = r6.mCachedBorders
            r23 = r5
            int r5 = r2.mSpanSize
            r5 = r11[r5]
            int r11 = r2.width
            r24 = r2
            r2 = 1073741824(0x40000000, float:2.0)
            r25 = r12
            r12 = 0
            int r11 = getChildMeasureSpec(r5, r2, r8, r11, r12)
            int r12 = r3 - r7
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r2)
            r12 = 1
            r6.measureChildWithDecorationsAndMargin(r1, r11, r2, r12)
            goto L_0x01ac
        L_0x01a6:
            r21 = r5
            r22 = r11
            r25 = r12
        L_0x01ac:
            int r0 = r0 + 1
            r7 = r30
            r8 = r31
            r5 = r21
            r11 = r22
            r12 = r25
            goto L_0x0156
        L_0x01b9:
            r21 = r5
            r22 = r11
            r25 = r12
            android.view.View[] r0 = r6.mSet
            r7 = 0
            r0 = r0[r7]
            boolean r8 = r6.shouldLayoutChildFromOpositeSide(r0)
            if (r8 == 0) goto L_0x01cf
            int r0 = r9.mLayoutDirection
            r1 = -1
            if (r0 == r1) goto L_0x01d6
        L_0x01cf:
            if (r8 != 0) goto L_0x0261
            int r0 = r9.mLayoutDirection
            r1 = 1
            if (r0 != r1) goto L_0x0261
        L_0x01d6:
            int r0 = r9.mLayoutDirection
            r5 = -1
            if (r0 != r5) goto L_0x01e6
            int r0 = r9.mOffset
            int r1 = r10.mConsumed
            int r0 = r0 - r1
            int r1 = r0 - r3
            r2 = 0
            r11 = r0
            r12 = r1
            goto L_0x01f3
        L_0x01e6:
            int r0 = r9.mOffset
            int r1 = r10.mConsumed
            int r1 = r1 + r0
            int r0 = r1 + r3
            int r2 = r29.getWidth()
            r11 = r0
            r12 = r1
        L_0x01f3:
            int r0 = r4 + -1
            r16 = r0
        L_0x01f7:
            if (r16 < 0) goto L_0x0258
            android.view.View[] r0 = r6.mSet
            r1 = r0[r16]
            android.view.ViewGroup$LayoutParams r0 = r1.getLayoutParams()
            r17 = r0
            androidx.recyclerview.widget.GridLayoutManager$LayoutParams r17 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r17
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mOrientationHelper
            int r23 = r0.getDecoratedMeasurementInOther(r1)
            int r0 = r9.mLayoutDirection
            r5 = 1
            if (r0 != r5) goto L_0x0215
            int r2 = r2 - r23
            r26 = r2
            goto L_0x0217
        L_0x0215:
            r26 = r2
        L_0x0217:
            int r5 = r26 + r23
            r0 = r29
            r27 = r1
            r2 = r26
            r28 = r3
            r3 = r12
            r7 = r4
            r24 = r8
            r8 = -1
            r4 = r5
            r5 = r11
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
            int r0 = r9.mLayoutDirection
            if (r0 != r8) goto L_0x0234
            int r26 = r26 + r23
            r2 = r26
            goto L_0x0236
        L_0x0234:
            r2 = r26
        L_0x0236:
            boolean r0 = r17.isItemRemoved()
            if (r0 != 0) goto L_0x0242
            boolean r0 = r17.isItemChanged()
            if (r0 == 0) goto L_0x0245
        L_0x0242:
            r0 = 1
            r10.mIgnoreConsumed = r0
        L_0x0245:
            boolean r0 = r10.mFocusable
            boolean r1 = r27.hasFocusable()
            r0 = r0 | r1
            r10.mFocusable = r0
            int r16 = r16 + -1
            r4 = r7
            r8 = r24
            r3 = r28
            r5 = -1
            r7 = 0
            goto L_0x01f7
        L_0x0258:
            r28 = r3
            r7 = r4
            r24 = r8
            r8 = -1
            r1 = 1
            goto L_0x02e2
        L_0x0261:
            r28 = r3
            r7 = r4
            r24 = r8
            r8 = -1
            int r0 = r9.mLayoutDirection
            if (r0 != r8) goto L_0x0279
            int r0 = r9.mOffset
            int r1 = r10.mConsumed
            int r0 = r0 - r1
            int r1 = r0 - r28
            int r2 = r29.getWidth()
            r11 = r0
            r12 = r1
            goto L_0x0283
        L_0x0279:
            int r0 = r9.mOffset
            int r1 = r10.mConsumed
            int r0 = r0 + r1
            int r3 = r0 + r28
            r2 = 0
            r12 = r0
            r11 = r3
        L_0x0283:
            r0 = 0
            r5 = r0
        L_0x0285:
            if (r5 >= r7) goto L_0x02df
            android.view.View[] r0 = r6.mSet
            r4 = r0[r5]
            android.view.ViewGroup$LayoutParams r0 = r4.getLayoutParams()
            r16 = r0
            androidx.recyclerview.widget.GridLayoutManager$LayoutParams r16 = (androidx.recyclerview.widget.GridLayoutManager.LayoutParams) r16
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mOrientationHelper
            int r17 = r0.getDecoratedMeasurementInOther(r4)
            int r0 = r9.mLayoutDirection
            if (r0 != r8) goto L_0x02a2
            int r2 = r2 - r17
            r23 = r2
            goto L_0x02a4
        L_0x02a2:
            r23 = r2
        L_0x02a4:
            int r26 = r23 + r17
            r0 = r29
            r1 = r4
            r2 = r23
            r3 = r12
            r27 = r4
            r4 = r26
            r26 = r5
            r5 = r11
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
            int r0 = r9.mLayoutDirection
            if (r0 == r8) goto L_0x02bf
            int r23 = r23 + r17
            r2 = r23
            goto L_0x02c1
        L_0x02bf:
            r2 = r23
        L_0x02c1:
            boolean r0 = r16.isItemRemoved()
            if (r0 != 0) goto L_0x02d0
            boolean r0 = r16.isItemChanged()
            if (r0 == 0) goto L_0x02ce
            goto L_0x02d0
        L_0x02ce:
            r1 = 1
            goto L_0x02d3
        L_0x02d0:
            r1 = 1
            r10.mIgnoreConsumed = r1
        L_0x02d3:
            boolean r0 = r10.mFocusable
            boolean r3 = r27.hasFocusable()
            r0 = r0 | r3
            r10.mFocusable = r0
            int r5 = r26 + 1
            goto L_0x0285
        L_0x02df:
            r26 = r5
            r1 = 1
        L_0x02e2:
            int r0 = r10.mConsumed
            int r0 = r0 + r28
            r10.mConsumed = r0
            android.view.View[] r0 = r6.mSet
            r3 = 0
            java.util.Arrays.fill(r0, r3)
            r7 = r30
            r8 = r31
            r0 = r18
            r5 = r21
            r11 = r22
            r13 = 1
            goto L_0x007c
        L_0x02fb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.GridLayoutManagerFixed.layoutChunk(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, androidx.recyclerview.widget.LinearLayoutManager$LayoutState, androidx.recyclerview.widget.LinearLayoutManager$LayoutChunkResult):void");
    }
}
