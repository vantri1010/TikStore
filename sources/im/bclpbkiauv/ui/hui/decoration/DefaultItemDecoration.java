package im.bclpbkiauv.ui.hui.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class DefaultItemDecoration extends BaseItemDecoration<DefaultItemDecoration> {
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerWidth;
    private boolean mDrawFirst;
    private boolean mDrawLast;

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Rect rect = outRect;
        RecyclerView recyclerView = parent;
        int position = recyclerView.getChildAdapterPosition(view);
        if (position >= 0) {
            if (this.mExcludeViewTypeList.contains(Integer.valueOf(parent.getAdapter().getItemViewType(position)))) {
                rect.set(0, 0, 0, 0);
                return;
            }
            int columnCount = getSpanCount(recyclerView);
            int childCount = parent.getAdapter().getItemCount();
            boolean firstRow = isFirstRow(position, columnCount);
            boolean lastRow = isLastRow(position, columnCount, childCount);
            boolean firstColumn = isFirstColumn(position, columnCount);
            boolean lastColumn = isLastColumn(position, columnCount);
            boolean isHorizontal = isHorizontal(recyclerView);
            if (columnCount == 1) {
                if (firstRow) {
                    if (this.mDrawFirst) {
                        if (isHorizontal) {
                            int i = this.mDividerWidth;
                            rect.set(i, 0, i / 2, 0);
                            return;
                        }
                        int i2 = this.mDividerHeight;
                        rect.set(0, i2, 0, i2 / 2);
                    } else if (isHorizontal) {
                        rect.set(0, 0, this.mDividerWidth / 2, 0);
                    } else {
                        rect.set(0, 0, 0, this.mDividerHeight / 2);
                    }
                } else if (lastRow) {
                    if (this.mDrawLast) {
                        if (isHorizontal) {
                            int i3 = this.mDividerWidth;
                            rect.set(i3 / 2, 0, i3, 0);
                            return;
                        }
                        int i4 = this.mDividerHeight;
                        rect.set(0, i4 / 2, 0, i4);
                    } else if (isHorizontal) {
                        rect.set(this.mDividerWidth / 2, 0, 0, 0);
                    } else {
                        rect.set(0, this.mDividerHeight / 2, 0, 0);
                    }
                } else if (isHorizontal) {
                    int i5 = this.mDividerWidth;
                    rect.set(i5 / 2, 0, i5 / 2, 0);
                } else {
                    int i6 = this.mDividerHeight;
                    rect.set(0, i6 / 2, 0, i6 / 2);
                }
            } else if (firstRow && firstColumn) {
                rect.set(0, 0, this.mDividerWidth / 2, this.mDividerHeight / 2);
            } else if (firstRow && lastColumn) {
                rect.set(this.mDividerWidth / 2, 0, 0, this.mDividerHeight / 2);
            } else if (firstRow) {
                int i7 = this.mDividerWidth;
                rect.set(i7 / 2, 0, i7 / 2, this.mDividerHeight / 2);
            } else if (lastRow && firstColumn) {
                rect.set(0, this.mDividerHeight / 2, this.mDividerWidth / 2, 0);
            } else if (lastRow && lastColumn) {
                rect.set(this.mDividerWidth / 2, this.mDividerHeight / 2, 0, 0);
            } else if (lastRow) {
                int i8 = this.mDividerWidth;
                rect.set(i8 / 2, this.mDividerHeight / 2, i8 / 2, 0);
            } else if (firstColumn) {
                int i9 = this.mDividerHeight;
                rect.set(0, i9 / 2, this.mDividerWidth / 2, i9 / 2);
            } else if (lastColumn) {
                int i10 = this.mDividerHeight;
                rect.set(this.mDividerWidth / 2, i10 / 2, 0, i10 / 2);
            } else {
                int i11 = this.mDividerWidth;
                int i12 = this.mDividerHeight;
                rect.set(i11 / 2, i12 / 2, i11 / 2, i12 / 2);
            }
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition >= 0 && !this.mExcludeViewTypeList.contains(Integer.valueOf(parent.getAdapter().getItemViewType(childPosition)))) {
                int left = child.getLeft();
                int top = child.getBottom();
                this.mDivider.setBounds(left, top, child.getRight(), this.mDividerHeight + top);
                this.mDivider.draw(c);
            }
        }
        c.restore();
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        c.save();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);
            if (childPosition >= 0 && !this.mExcludeViewTypeList.contains(Integer.valueOf(parent.getAdapter().getItemViewType(childPosition)))) {
                int left = child.getRight();
                int bottom = child.getBottom();
                this.mDivider.setBounds(left, child.getTop(), this.mDividerWidth + left, bottom);
                this.mDivider.draw(c);
            }
        }
        c.restore();
    }

    public DefaultItemDecoration setDividerHeight(int dividerHeight) {
        this.mDividerHeight = dividerHeight;
        return this;
    }

    public DefaultItemDecoration setDividerWidth(int dividerWidth) {
        this.mDividerWidth = dividerWidth;
        return this;
    }

    public DefaultItemDecoration setDividerColor(int dividerColor) {
        return setDivider(new ColorDrawable(dividerColor));
    }

    public DefaultItemDecoration setDivider(Drawable divider) {
        this.mDivider = divider;
        return this;
    }

    public DefaultItemDecoration setDrawFirst(boolean drawFirst) {
        this.mDrawFirst = drawFirst;
        return this;
    }

    public DefaultItemDecoration setDrawLast(boolean drawLast) {
        this.mDrawLast = drawLast;
        return this;
    }
}
