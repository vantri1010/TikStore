package im.bclpbkiauv.ui.hui.decoration;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import im.bclpbkiauv.ui.hui.decoration.BaseItemDecoration;
import java.util.ArrayList;
import java.util.List;

public class BaseItemDecoration<I extends BaseItemDecoration> extends RecyclerView.ItemDecoration {
    protected List<Integer> mExcludeViewTypeList = new ArrayList();

    public int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        }
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 1;
    }

    public boolean isFirstRow(int position, int columnCount) {
        return position < columnCount;
    }

    public boolean isLastRow(int position, int columnCount, int childCount) {
        if (columnCount != 1) {
            int lastRawItemCount = childCount % columnCount;
            int rawCount = ((childCount - lastRawItemCount) / columnCount) + (lastRawItemCount > 0 ? 1 : 0);
            int rawPositionJudge = (position + 1) % columnCount;
            if (rawPositionJudge == 0) {
                if (rawCount == (position + 1) / columnCount) {
                    return true;
                }
                return false;
            } else if (rawCount == (((position + 1) - rawPositionJudge) / columnCount) + 1) {
                return true;
            } else {
                return false;
            }
        } else if (position + 1 == childCount) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFirstColumn(int position, int columnCount) {
        if (columnCount == 1 || position % columnCount == 0) {
            return true;
        }
        return false;
    }

    public boolean isLastColumn(int position, int columnCount) {
        if (columnCount == 1 || (position + 1) % columnCount == 0) {
            return true;
        }
        return false;
    }

    public boolean isHorizontal(RecyclerView parent) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        boolean isHorizontal = true;
        if (manager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) manager).getOrientation() != 0) {
                isHorizontal = false;
            }
            return isHorizontal;
        } else if (!(manager instanceof StaggeredGridLayoutManager)) {
            return false;
        } else {
            if (((StaggeredGridLayoutManager) manager).getOrientation() != 0) {
                isHorizontal = false;
            }
            return isHorizontal;
        }
    }

    public I setExcludeViewTypeList(int... excludeViewType) {
        for (int i : excludeViewType) {
            this.mExcludeViewTypeList.add(Integer.valueOf(i));
        }
        return this;
    }

    public I setExcludeViewTypeList(List<Integer> excludeViewTypeList) {
        this.mExcludeViewTypeList = excludeViewTypeList;
        return this;
    }

    public List<Integer> getExcludeViewTypeList() {
        return this.mExcludeViewTypeList;
    }
}
