package im.bclpbkiauv.ui.decoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.decoration.ClickInfo;
import im.bclpbkiauv.ui.decoration.cache.CacheUtil;
import im.bclpbkiauv.ui.decoration.listener.GroupXListener;
import im.bclpbkiauv.ui.decoration.listener.OnGroupClickListener;
import im.bclpbkiauv.ui.decoration.util.ViewUtil;
import java.util.ArrayList;
import java.util.List;

public class StickyXDecoration extends BaseDecoration {
    private CacheUtil<Bitmap> mBitmapCache;
    private GroupXListener mGroupListener;
    /* access modifiers changed from: private */
    public Paint mGroutPaint;
    private CacheUtil<View> mHeadViewCache;

    private StickyXDecoration(GroupXListener groupListener) {
        this.mBitmapCache = new CacheUtil<>();
        this.mHeadViewCache = new CacheUtil<>();
        this.mGroupListener = groupListener;
        this.mGroutPaint = new Paint();
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount;
        int childCount2;
        int bottom;
        RecyclerView recyclerView = parent;
        super.onDrawOver(c, parent, state);
        int itemCount = state.getItemCount();
        int childCount3 = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int i = 0;
        while (i < childCount3) {
            View childView = recyclerView.getChildAt(i);
            int position = recyclerView.getChildAdapterPosition(childView);
            int realPosition = getRealPosition(position);
            if (isFirstInGroup(realPosition)) {
                childCount = childCount3;
                childCount2 = realPosition;
            } else if (isFirstInRecyclerView(realPosition, i)) {
                childCount = childCount3;
                childCount2 = realPosition;
            } else {
                childCount = childCount3;
                int childCount4 = realPosition;
                drawDivide(c, parent, childView, realPosition, left, right);
                i++;
                childCount3 = childCount;
            }
            int realPosition2 = childView.getBottom();
            int bottom2 = Math.max(this.mGroupHeight, childView.getTop() + parent.getPaddingTop());
            if (position + 1 >= itemCount || !isLastLineInGroup(recyclerView, childCount2) || realPosition2 >= bottom2) {
                bottom = bottom2;
            } else {
                bottom = realPosition2;
            }
            drawDecoration(c, childCount2, left, right, bottom);
            i++;
            childCount3 = childCount;
        }
    }

    private void drawDecoration(Canvas c, int realPosition, int left, int right, int bottom) {
        View groupView;
        Bitmap bitmap;
        c.drawRect((float) left, (float) (bottom - this.mGroupHeight), (float) right, (float) bottom, this.mGroutPaint);
        int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
        if (this.mHeadViewCache.get(firstPositionInGroup) == null) {
            groupView = getGroupView(firstPositionInGroup);
            if (groupView != null) {
                measureAndLayoutView(groupView, left, right);
                this.mHeadViewCache.put(firstPositionInGroup, groupView);
            } else {
                return;
            }
        } else {
            groupView = this.mHeadViewCache.get(firstPositionInGroup);
        }
        if (this.mBitmapCache.get(firstPositionInGroup) != null) {
            bitmap = this.mBitmapCache.get(firstPositionInGroup);
        } else {
            bitmap = Bitmap.createBitmap(groupView.getDrawingCache());
            this.mBitmapCache.put(firstPositionInGroup, bitmap);
        }
        c.drawBitmap(bitmap, (float) left, (float) (bottom - this.mGroupHeight), (Paint) null);
        if (this.mOnGroupClickListener != null) {
            setClickInfo(groupView, left, bottom, realPosition);
        }
    }

    private void measureAndLayoutView(View groupView, int left, int right) {
        groupView.setDrawingCacheEnabled(true);
        groupView.setLayoutParams(new ViewGroup.LayoutParams(right, this.mGroupHeight));
        groupView.measure(View.MeasureSpec.makeMeasureSpec(right, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mGroupHeight, 1073741824));
        groupView.layout(left, 0 - this.mGroupHeight, right, 0);
    }

    private void setClickInfo(View groupView, int parentLeft, int parentBottom, int realPosition) {
        int i = parentBottom;
        int parentTop = i - this.mGroupHeight;
        List<ClickInfo.DetailInfo> list = new ArrayList<>();
        for (View view : ViewUtil.getChildViewWithId(groupView)) {
            int bottom = view.getBottom() + parentTop;
            int parentTop2 = parentTop;
            ClickInfo.DetailInfo detailInfo = r8;
            ClickInfo.DetailInfo detailInfo2 = new ClickInfo.DetailInfo(view.getId(), view.getLeft() + parentLeft, view.getRight() + parentLeft, view.getTop() + parentTop, bottom);
            list.add(detailInfo);
            parentTop = parentTop2;
        }
        ClickInfo clickInfo = new ClickInfo(i, list);
        clickInfo.mGroupId = groupView.getId();
        this.stickyHeaderPosArray.put(Integer.valueOf(realPosition), clickInfo);
    }

    /* access modifiers changed from: package-private */
    public String getGroupName(int realPosition) {
        GroupXListener groupXListener = this.mGroupListener;
        if (groupXListener != null) {
            return groupXListener.getGroupName(realPosition);
        }
        return null;
    }

    private View getGroupView(int realPosition) {
        GroupXListener groupXListener = this.mGroupListener;
        if (groupXListener != null) {
            return groupXListener.getGroupView(realPosition);
        }
        return null;
    }

    public void setCacheEnable(boolean b) {
        this.mHeadViewCache.isCacheable(b);
    }

    public void clearCache() {
        this.mHeadViewCache.clean();
        this.mBitmapCache.clean();
    }

    public void notifyRedraw(RecyclerView recyclerView, View viewGroup, int realPosition) {
        viewGroup.setDrawingCacheEnabled(false);
        int firstPositionInGroup = getFirstInGroupWithCash(realPosition);
        this.mBitmapCache.remove(firstPositionInGroup);
        this.mHeadViewCache.remove(firstPositionInGroup);
        measureAndLayoutView(viewGroup, recyclerView.getPaddingLeft(), recyclerView.getWidth() - recyclerView.getPaddingRight());
        this.mHeadViewCache.put(firstPositionInGroup, viewGroup);
        recyclerView.invalidate();
    }

    public static class Builder {
        StickyXDecoration mDecoration;

        private Builder(GroupXListener listener) {
            this.mDecoration = new StickyXDecoration(listener);
        }

        public static Builder init(GroupXListener listener) {
            return new Builder(listener);
        }

        public Builder setGroupHeight(int groutHeight) {
            this.mDecoration.mGroupHeight = groutHeight;
            return this;
        }

        public Builder setGroupBackground(int background) {
            this.mDecoration.mGroupBackground = background;
            this.mDecoration.mGroutPaint.setColor(this.mDecoration.mGroupBackground);
            return this;
        }

        public Builder setDivideHeight(int height) {
            this.mDecoration.mDivideHeight = height;
            return this;
        }

        public Builder setDivideColor(int color) {
            this.mDecoration.mDivideColor = color;
            this.mDecoration.mDividePaint.setColor(this.mDecoration.mDivideColor);
            return this;
        }

        public Builder setOnClickListener(OnGroupClickListener listener) {
            this.mDecoration.setOnGroupClickListener(listener);
            return this;
        }

        public Builder resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
            this.mDecoration.resetSpan(recyclerView, gridLayoutManager);
            return this;
        }

        public Builder setCacheEnable(boolean b) {
            this.mDecoration.setCacheEnable(b);
            return this;
        }

        public Builder setHeaderCount(int headerCount) {
            if (headerCount >= 0) {
                this.mDecoration.mHeaderCount = headerCount;
            }
            return this;
        }

        public StickyXDecoration build() {
            return this.mDecoration;
        }
    }
}
