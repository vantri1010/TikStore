package im.bclpbkiauv.ui.components.recyclerview.manager;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ExStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    private int[] dimension;
    private int[] measuredDimension = new int[2];

    public ExStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int width;
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width2 = 0;
        int height = 0;
        int count = getItemCount();
        this.dimension = new int[getSpanCount()];
        int i = 0;
        while (i < count) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i, 0);
            int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i, 0);
            int width3 = width2;
            measureScrapChild(recycler, i, makeMeasureSpec, makeMeasureSpec2, this.measuredDimension);
            if (getOrientation() == 1) {
                int[] iArr = this.dimension;
                int findMinIndex = findMinIndex(iArr);
                iArr[findMinIndex] = iArr[findMinIndex] + this.measuredDimension[1];
            } else {
                int[] iArr2 = this.dimension;
                int findMinIndex2 = findMinIndex(iArr2);
                iArr2[findMinIndex2] = iArr2[findMinIndex2] + this.measuredDimension[0];
            }
            i++;
            width2 = width3;
        }
        int width4 = width2;
        if (getOrientation() == 1) {
            height = findMax(this.dimension);
            width = width4;
        } else {
            width = findMax(this.dimension);
        }
        if (widthMode == 1073741824) {
            width = widthSize;
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension2) {
        if (position < getItemCount()) {
            try {
                View view = recycler.getViewForPosition(position);
                if (view != null) {
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                    view.measure(ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), lp.width), ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), lp.height));
                    measuredDimension2[0] = view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    measuredDimension2[1] = view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int findMinIndex(int[] array) {
        int index = 0;
        int min = array[0];
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                index = i;
            }
        }
        return index;
    }
}
