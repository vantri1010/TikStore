package androidx.recyclerview.widget;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.Map;

public class LinearScrollOffsetLayoutManager extends LinearLayoutManager {
    private Map<Integer, Integer> heightMap = new HashMap();

    public LinearScrollOffsetLayoutManager(Context context) {
        super(context);
    }

    public LinearScrollOffsetLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            this.heightMap.put(Integer.valueOf(i), Integer.valueOf(getChildAt(i).getHeight()));
        }
    }

    public int computeVerticalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        return computeScrollOffset(state);
    }

    private int computeScrollOffset(RecyclerView.State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        int firstVisiablePosition = findFirstVisibleItemPosition();
        int offsetY = -((int) findViewByPosition(firstVisiablePosition).getY());
        for (int i = 0; i < firstVisiablePosition; i++) {
            offsetY += this.heightMap.get(Integer.valueOf(i)) == null ? 0 : this.heightMap.get(Integer.valueOf(i)).intValue();
        }
        return offsetY;
    }
}
