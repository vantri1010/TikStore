package im.bclpbkiauv.ui.hui.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.ui.actionbar.Theme;

public class TopBottomDecoration extends BaseItemDecoration<TopBottomDecoration> {
    private int mBottomOffset;
    private int mColor;
    private float mCornerRadius;
    private float[] mCornerRadiusArr;
    private Drawable mDrawable;
    private int mFirstRowPosition;
    private boolean mIsDpValue;
    private int mItemBgColor;
    private int mLastRowPosition;
    private boolean mRoundCornerBottom;
    private boolean mRoundCornerTop;
    private int mTopOffset;

    public TopBottomDecoration() {
        this(10, 10);
    }

    public TopBottomDecoration(int topOffset, int bottomOffset) {
        this(topOffset, bottomOffset, true);
    }

    public TopBottomDecoration(int topOffset, int bottomOffset, boolean isDpValue) {
        this.mTopOffset = topOffset;
        this.mBottomOffset = bottomOffset;
        this.mIsDpValue = isDpValue;
    }

    public TopBottomDecoration(int topOffset, int bottomOffset, float conerRadius, boolean isDpValue, int color, int itemBgColor) {
        this.mTopOffset = topOffset;
        this.mBottomOffset = bottomOffset;
        this.mIsDpValue = isDpValue;
        this.mRoundCornerTop = true;
        this.mRoundCornerBottom = true;
        this.mCornerRadius = isDpValue ? (float) AndroidUtilities.dp(conerRadius) : conerRadius;
        setOffsetColor(color);
        setItemBgColor(itemBgColor);
    }

    public static TopBottomDecoration getDefaultTopBottomCornerBg(int topOffset, int bottomOffset, float conerRadius) {
        return new TopBottomDecoration(topOffset, bottomOffset, conerRadius, true, Theme.getColor(Theme.key_windowBackgroundGray), Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        boolean isHorizontal = isHorizontal(parent);
        int i = this.mTopOffset;
        if (i != 0 && position == 0) {
            if (isHorizontal) {
                if (this.mIsDpValue) {
                    i = AndroidUtilities.dp((float) i);
                }
                outRect.left = i;
            } else {
                if (this.mIsDpValue) {
                    i = AndroidUtilities.dp((float) i);
                }
                outRect.top = i;
            }
        }
        if (this.mBottomOffset != 0 && parent.getAdapter() != null && position == parent.getAdapter().getItemCount() - 1) {
            if (isHorizontal) {
                outRect.right = this.mIsDpValue ? AndroidUtilities.dp((float) this.mBottomOffset) : this.mBottomOffset;
            } else {
                outRect.bottom = this.mIsDpValue ? AndroidUtilities.dp((float) this.mBottomOffset) : this.mBottomOffset;
            }
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left;
        RecyclerView.Adapter adapter;
        int i;
        RecyclerView recyclerView = parent;
        super.onDrawOver(c, parent, state);
        RecyclerView.Adapter adapter2 = parent.getAdapter();
        if (this.mDrawable == null || adapter2 == null) {
            return;
        }
        int left2 = parent.getPaddingLeft();
        int width = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        int itemCount = adapter2.getItemCount();
        boolean isHorizontal = isHorizontal(recyclerView);
        int i2 = 0;
        while (i2 < childCount) {
            View child = recyclerView.getChildAt(i2);
            if (child != null) {
                int childPosition = recyclerView.getChildAdapterPosition(child);
                if (adapter2 == null || !this.mExcludeViewTypeList.contains(Integer.valueOf(adapter2.getItemViewType(childPosition)))) {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                    if (!this.mRoundCornerTop || childPosition != this.mFirstRowPosition) {
                        adapter = adapter2;
                        left = left2;
                        if (!this.mRoundCornerBottom || (((i = this.mLastRowPosition) <= 0 || childPosition != i) && (this.mLastRowPosition > 0 || childPosition != itemCount - 1))) {
                            int i3 = this.mItemBgColor;
                            if (i3 != 0) {
                                child.setBackgroundColor(i3);
                            }
                        } else {
                            float[] fArr = this.mCornerRadiusArr;
                            if (fArr == null || fArr.length != 4) {
                                float f = this.mCornerRadius;
                                if (f != 0.0f) {
                                    if (isHorizontal) {
                                        child.setBackground(Theme.createRoundRectDrawable(0.0f, f, 0.0f, f, this.mItemBgColor));
                                    } else {
                                        child.setBackground(Theme.createRoundRectDrawable(0.0f, 0.0f, f, f, this.mItemBgColor));
                                    }
                                }
                            } else {
                                child.setBackground(Theme.createRoundRectDrawable(fArr[0], fArr[1], fArr[2], fArr[3], this.mItemBgColor));
                            }
                        }
                    } else {
                        float[] fArr2 = this.mCornerRadiusArr;
                        if (fArr2 == null || fArr2.length != 4) {
                            adapter = adapter2;
                            left = left2;
                            float f2 = this.mCornerRadius;
                            if (f2 != 0.0f) {
                                if (itemCount == 1) {
                                    child.setBackground(Theme.createRoundRectDrawable(f2, f2, f2, f2, this.mItemBgColor));
                                } else if (isHorizontal) {
                                    child.setBackground(Theme.createRoundRectDrawable(f2, 0.0f, f2, 0.0f, this.mItemBgColor));
                                } else {
                                    child.setBackground(Theme.createRoundRectDrawable(f2, f2, 0.0f, 0.0f, this.mItemBgColor));
                                }
                            }
                        } else {
                            adapter = adapter2;
                            left = left2;
                            child.setBackground(Theme.createRoundRectDrawable(fArr2[0], fArr2[1], fArr2[2], fArr2[3], this.mItemBgColor));
                        }
                    }
                } else {
                    adapter = adapter2;
                    left = left2;
                }
            } else {
                adapter = adapter2;
                left = left2;
            }
            i2++;
            recyclerView = parent;
            adapter2 = adapter;
            left2 = left;
        }
        int i4 = left2;
    }

    public TopBottomDecoration setOffsetColor(int offsetColor) {
        if (this.mColor != offsetColor) {
            this.mColor = offsetColor;
            this.mDrawable = new ColorDrawable(this.mColor);
        }
        return this;
    }

    public TopBottomDecoration setItemBgColor(int itemBgColor) {
        this.mItemBgColor = itemBgColor;
        return this;
    }

    public TopBottomDecoration setDrawable(Drawable drawable) {
        this.mDrawable = drawable;
        return this;
    }

    public TopBottomDecoration setTopOffset(int topOffset) {
        this.mTopOffset = topOffset;
        return this;
    }

    public TopBottomDecoration setBottomOffset(int bottomOffset) {
        this.mBottomOffset = bottomOffset;
        return this;
    }

    public TopBottomDecoration setIsDpValue(boolean isDpValue) {
        this.mIsDpValue = isDpValue;
        return this;
    }

    public TopBottomDecoration setRoundCornerTop(boolean roundCornerTop) {
        this.mRoundCornerTop = roundCornerTop;
        return this;
    }

    public TopBottomDecoration setRoundCornerBottom(boolean roundCornerBottom) {
        this.mRoundCornerBottom = roundCornerBottom;
        return this;
    }

    public TopBottomDecoration setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius;
        return this;
    }

    public TopBottomDecoration setCornerRadiusArr(float[] cornerRadiusArr) {
        this.mCornerRadiusArr = cornerRadiusArr;
        return this;
    }

    public TopBottomDecoration setFirstRowPosition(int firstRowPosition) {
        this.mFirstRowPosition = firstRowPosition;
        return this;
    }

    public TopBottomDecoration setLastRowPosition(int lastRowPosition) {
        this.mLastRowPosition = lastRowPosition;
        return this;
    }
}
