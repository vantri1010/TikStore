package im.bclpbkiauv.ui.hui.friendscircle_v1.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.hui.adapter.pageAdapter.PageSelectionAdapter;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private boolean isShowBottom;
    private boolean isShowTop;
    private final Rect mBounds;
    private final ColorDrawable mDividerColorDrawable;
    private int space;

    public SpacesItemDecoration(int space2) {
        this.mBounds = new Rect();
        this.mDividerColorDrawable = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
        this.space = space2;
    }

    public SpacesItemDecoration(int space2, int spaceColor) {
        this.mBounds = new Rect();
        this.mDividerColorDrawable = new ColorDrawable(spaceColor);
        this.space = space2;
    }

    public SpacesItemDecoration isShowTop(boolean isShowTop2) {
        this.isShowTop = isShowTop2;
        return this;
    }

    public SpacesItemDecoration setShowBottom(boolean showBottom) {
        this.isShowBottom = showBottom;
        return this;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        boolean isHorizontal;
        if (parent.getAdapter() != null) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                isHorizontal = ((LinearLayoutManager) manager).getOrientation() == 0;
            } else if (manager instanceof StaggeredGridLayoutManager) {
                isHorizontal = ((StaggeredGridLayoutManager) manager).getOrientation() == 0;
            } else {
                isHorizontal = false;
            }
            int position = parent.getChildAdapterPosition(view);
            if (position != 0 || !this.isShowTop) {
                if (this.isShowBottom) {
                    if (parent.getAdapter() instanceof PageSelectionAdapter) {
                        if (((PageSelectionAdapter) parent.getAdapter()).isShowLoadMoreViewEnable() && position == ((PageSelectionAdapter) parent.getAdapter()).getDataCount() - 1) {
                            if (isHorizontal) {
                                outRect.bottom = this.space;
                            } else {
                                outRect.right = this.space;
                            }
                        }
                    } else if (parent.getAdapter().getItemCount() - 1 != position) {
                    } else {
                        if (isHorizontal) {
                            outRect.right = 0;
                        } else {
                            outRect.bottom = 0;
                        }
                    }
                } else if (isHorizontal) {
                    outRect.right = this.space;
                } else {
                    outRect.bottom = this.space;
                }
            } else if (isHorizontal) {
                outRect.right = this.space;
            } else {
                outRect.top = this.space;
                outRect.bottom = this.space;
            }
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null && this.mDividerColorDrawable != null) {
            drawVertical(c, parent);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int right;
        int left;
        canvas.save();
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int bottom = this.mBounds.bottom + Math.round(child.getTranslationY());
            this.mDividerColorDrawable.setBounds(left, this.mBounds.top, right, bottom);
            this.mDividerColorDrawable.draw(canvas);
        }
        canvas.restore();
    }
}
