package im.bclpbkiauv.ui.decoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.decoration.ClickInfo;
import im.bclpbkiauv.ui.decoration.listener.OnGroupClickListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class BaseDecoration extends RecyclerView.ItemDecoration {
    private SparseIntArray firstInGroupCash = new SparseIntArray(100);
    /* access modifiers changed from: private */
    public GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        public boolean onDown(MotionEvent e) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return BaseDecoration.this.onTouchEvent(e);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };
    int mDivideColor = Color.parseColor("#CCCCCC");
    int mDivideHeight = 0;
    Paint mDividePaint;
    private boolean mDownInHeader;
    int mGroupBackground = Color.parseColor("#48BDFF");
    int mGroupHeight = 120;
    int mHeaderCount;
    protected OnGroupClickListener mOnGroupClickListener;
    private int offset = 0;
    protected HashMap<Integer, ClickInfo> stickyHeaderPosArray = new HashMap<>();

    /* access modifiers changed from: package-private */
    public abstract String getGroupName(int i);

    public BaseDecoration() {
        Paint paint = new Paint();
        this.mDividePaint = paint;
        paint.setColor(this.mDivideColor);
    }

    /* access modifiers changed from: protected */
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.mOnGroupClickListener = listener;
    }

    /* access modifiers changed from: protected */
    public int getRealPosition(int position) {
        return position - this.mHeaderCount;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int realPosition = getRealPosition(parent.getChildAdapterPosition(view));
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (!isHeader(realPosition) && !isOffset(realPosition)) {
                if (isFirstLineInGroup(realPosition, spanCount)) {
                    outRect.top = this.mGroupHeight;
                } else {
                    outRect.top = this.mDivideHeight;
                }
            }
        } else if (!isHeader(realPosition) && !isOffset(realPosition)) {
            if (isFirstInGroup(realPosition)) {
                outRect.top = this.mGroupHeight;
            } else {
                outRect.top = this.mDivideHeight;
            }
        }
    }

    public void setOffset(int count) {
        this.offset = count;
    }

    /* access modifiers changed from: protected */
    public boolean isFirstInGroup(int realPosition) {
        String preGroupId;
        if (realPosition < 0) {
            return false;
        }
        if (realPosition == 0) {
            return true;
        }
        if (realPosition <= 0) {
            preGroupId = null;
        } else {
            preGroupId = getGroupName(realPosition - 1);
        }
        String curGroupId = getGroupName(realPosition);
        if (curGroupId == null) {
            return false;
        }
        return !TextUtils.equals(preGroupId, curGroupId);
    }

    /* access modifiers changed from: protected */
    public boolean isFirstInRecyclerView(int realPosition, int index) {
        return realPosition >= 0 && index == 0;
    }

    /* access modifiers changed from: protected */
    public boolean isHeader(int realPosition) {
        return realPosition < 0;
    }

    /* access modifiers changed from: protected */
    public boolean isOffset(int realPosition) {
        return realPosition < this.offset;
    }

    /* access modifiers changed from: protected */
    public boolean isFirstLineInGroup(int realPosition, int spanCount) {
        if (realPosition < 0) {
            return false;
        }
        if (realPosition != 0 && realPosition - getFirstInGroupWithCash(realPosition) >= spanCount) {
            return false;
        }
        return true;
    }

    public void resetSpan(RecyclerView recyclerView, GridLayoutManager gridLayoutManager) {
        if (recyclerView == null) {
            throw new NullPointerException("recyclerView not allow null");
        } else if (gridLayoutManager != null) {
            final int spanCount = gridLayoutManager.getSpanCount();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int position) {
                    String nextGroupId;
                    int realPosition = BaseDecoration.this.getRealPosition(position);
                    if (realPosition < 0) {
                        return spanCount;
                    }
                    String curGroupId = BaseDecoration.this.getGroupName(realPosition);
                    try {
                        nextGroupId = BaseDecoration.this.getGroupName(realPosition + 1);
                    } catch (Exception e) {
                        nextGroupId = curGroupId;
                    }
                    if (TextUtils.equals(curGroupId, nextGroupId)) {
                        return 1;
                    }
                    int posFirstInGroup = BaseDecoration.this.getFirstInGroupWithCash(realPosition);
                    int i = spanCount;
                    return i - ((realPosition - posFirstInGroup) % i);
                }
            });
        } else {
            throw new NullPointerException("gridLayoutManager not allow null");
        }
    }

    public void onEventDown(MotionEvent event) {
        boolean z = false;
        if (event == null) {
            this.mDownInHeader = false;
            return;
        }
        if (event.getY() > 0.0f && event.getY() < ((float) this.mGroupHeight)) {
            z = true;
        }
        this.mDownInHeader = z;
    }

    public boolean onEventUp(MotionEvent event) {
        if (this.mDownInHeader) {
            float y = event.getY();
            if (y > 0.0f && y < ((float) this.mGroupHeight)) {
                return onTouchEvent(event);
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public int getFirstInGroupWithCash(int realPosition) {
        return getFirstInGroup(realPosition);
    }

    private int getFirstInGroup(int realPosition) {
        if (realPosition <= 0) {
            return 0;
        }
        if (isFirstInGroup(realPosition)) {
            return realPosition;
        }
        return getFirstInGroup(realPosition - 1);
    }

    /* access modifiers changed from: protected */
    public boolean isLastLineInGroup(RecyclerView recyclerView, int realPosition) {
        String nextGroupName;
        if (realPosition < 0) {
            return true;
        }
        String curGroupName = getGroupName(realPosition);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int findCount = 1;
        if (manager instanceof GridLayoutManager) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            findCount = spanCount - ((realPosition - getFirstInGroupWithCash(realPosition)) % spanCount);
        }
        try {
            nextGroupName = getGroupName(realPosition + findCount);
        } catch (Exception e) {
            nextGroupName = curGroupName;
        }
        if (nextGroupName == null) {
            return true;
        }
        return true ^ TextUtils.equals(curGroupName, nextGroupName);
    }

    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (this.gestureDetector == null) {
            this.gestureDetector = new GestureDetector(parent.getContext(), this.gestureListener);
            parent.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return BaseDecoration.this.gestureDetector.onTouchEvent(event);
                }
            });
        }
        this.stickyHeaderPosArray.clear();
    }

    private void onGroupClick(int realPosition, int viewId) {
        OnGroupClickListener onGroupClickListener = this.mOnGroupClickListener;
        if (onGroupClickListener != null) {
            onGroupClickListener.onClick(realPosition, viewId);
        }
    }

    /* access modifiers changed from: private */
    public boolean onTouchEvent(MotionEvent e) {
        for (Map.Entry<Integer, ClickInfo> entry : this.stickyHeaderPosArray.entrySet()) {
            ClickInfo value = this.stickyHeaderPosArray.get(entry.getKey());
            float y = e.getY();
            float x = e.getX();
            if (((float) (value.mBottom - this.mGroupHeight)) <= y && y <= ((float) value.mBottom)) {
                if (value.mDetailInfoList == null || value.mDetailInfoList.size() == 0) {
                    onGroupClick(entry.getKey().intValue(), value.mGroupId);
                    return true;
                }
                boolean isChildViewClicked = false;
                Iterator<ClickInfo.DetailInfo> it = value.mDetailInfoList.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    ClickInfo.DetailInfo detailInfo = it.next();
                    if (((float) detailInfo.top) <= y && y <= ((float) detailInfo.bottom) && ((float) detailInfo.left) <= x && ((float) detailInfo.right) >= x) {
                        onGroupClick(entry.getKey().intValue(), detailInfo.id);
                        isChildViewClicked = true;
                        break;
                    }
                }
                if (isChildViewClicked) {
                    return true;
                }
                onGroupClick(entry.getKey().intValue(), value.mGroupId);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void drawDivide(Canvas c, RecyclerView parent, View childView, int realPosition, int left, int right) {
        if (this.mDivideHeight != 0 && !isHeader(realPosition)) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (!(manager instanceof GridLayoutManager)) {
                float bottom = (float) childView.getTop();
                if (bottom >= ((float) this.mGroupHeight)) {
                    c.drawRect((float) left, bottom - ((float) this.mDivideHeight), (float) right, bottom, this.mDividePaint);
                }
            } else if (!isFirstLineInGroup(realPosition, ((GridLayoutManager) manager).getSpanCount())) {
                float bottom2 = (float) (childView.getTop() + parent.getPaddingTop());
                if (bottom2 >= ((float) this.mGroupHeight)) {
                    c.drawRect((float) left, bottom2 - ((float) this.mDivideHeight), (float) right, bottom2, this.mDividePaint);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void log(String content) {
    }
}
