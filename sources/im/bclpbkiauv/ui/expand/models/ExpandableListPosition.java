package im.bclpbkiauv.ui.expand.models;

import android.widget.ExpandableListView;
import java.util.ArrayList;

public class ExpandableListPosition {
    public static final int CHILD = 1;
    public static final int GROUP = 2;
    private static final int MAX_POOL_SIZE = 5;
    private static ArrayList<ExpandableListPosition> sPool = new ArrayList<>(5);
    public int childPos;
    int flatListPos;
    public int groupPos;
    public int type;

    private void resetState() {
        this.groupPos = 0;
        this.childPos = 0;
        this.flatListPos = 0;
        this.type = 0;
    }

    private ExpandableListPosition() {
    }

    public long getPackedPosition() {
        if (this.type == 1) {
            return ExpandableListView.getPackedPositionForChild(this.groupPos, this.childPos);
        }
        return ExpandableListView.getPackedPositionForGroup(this.groupPos);
    }

    static ExpandableListPosition obtainGroupPosition(int groupPosition) {
        return obtain(2, groupPosition, 0, 0);
    }

    static ExpandableListPosition obtainChildPosition(int groupPosition, int childPosition) {
        return obtain(1, groupPosition, childPosition, 0);
    }

    static ExpandableListPosition obtainPosition(long packedPosition) {
        if (packedPosition == 4294967295L) {
            return null;
        }
        ExpandableListPosition elp = getRecycledOrCreate();
        elp.groupPos = ExpandableListView.getPackedPositionGroup(packedPosition);
        if (ExpandableListView.getPackedPositionType(packedPosition) == 1) {
            elp.type = 1;
            elp.childPos = ExpandableListView.getPackedPositionChild(packedPosition);
        } else {
            elp.type = 2;
        }
        return elp;
    }

    public static ExpandableListPosition obtain(int type2, int groupPos2, int childPos2, int flatListPos2) {
        ExpandableListPosition elp = getRecycledOrCreate();
        elp.type = type2;
        elp.groupPos = groupPos2;
        elp.childPos = childPos2;
        elp.flatListPos = flatListPos2;
        return elp;
    }

    private static ExpandableListPosition getRecycledOrCreate() {
        synchronized (sPool) {
            if (sPool.size() > 0) {
                ExpandableListPosition elp = sPool.remove(0);
                elp.resetState();
                return elp;
            }
            ExpandableListPosition expandableListPosition = new ExpandableListPosition();
            return expandableListPosition;
        }
    }

    public void recycle() {
        synchronized (sPool) {
            if (sPool.size() < 5) {
                sPool.add(this);
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpandableListPosition that = (ExpandableListPosition) o;
        if (this.groupPos == that.groupPos && this.childPos == that.childPos && this.flatListPos == that.flatListPos && this.type == that.type) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((((this.groupPos * 31) + this.childPos) * 31) + this.flatListPos) * 31) + this.type;
    }

    public String toString() {
        return "ExpandableListPosition{groupPos=" + this.groupPos + ", childPos=" + this.childPos + ", flatListPos=" + this.flatListPos + ", type=" + this.type + '}';
    }
}
