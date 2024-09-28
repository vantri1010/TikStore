package im.bclpbkiauv.ui.expand;

import im.bclpbkiauv.ui.expand.listeners.ExpandCollapseListener;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import im.bclpbkiauv.ui.expand.models.ExpandableList;
import im.bclpbkiauv.ui.expand.models.ExpandableListPosition;

public class ExpandCollapseController {
    private ExpandableList expandableList;
    private ExpandCollapseListener listener;

    public ExpandCollapseController(ExpandableList expandableList2, ExpandCollapseListener listener2) {
        this.expandableList = expandableList2;
        this.listener = listener2;
    }

    private void collapseGroup(ExpandableListPosition listPosition) {
        this.expandableList.expandedGroupIndexes[listPosition.groupPos] = false;
        ExpandCollapseListener expandCollapseListener = this.listener;
        if (expandCollapseListener != null) {
            expandCollapseListener.onGroupCollapsed(this.expandableList.getFlattenedGroupIndex(listPosition) + 1, ((ExpandableGroup) this.expandableList.groups.get(listPosition.groupPos)).getItemCount());
        }
    }

    private void expandGroup(ExpandableListPosition listPosition) {
        this.expandableList.expandedGroupIndexes[listPosition.groupPos] = true;
        ExpandCollapseListener expandCollapseListener = this.listener;
        if (expandCollapseListener != null) {
            expandCollapseListener.onGroupExpanded(this.expandableList.getFlattenedGroupIndex(listPosition) + 1, ((ExpandableGroup) this.expandableList.groups.get(listPosition.groupPos)).getItemCount());
        }
    }

    public boolean isGroupExpanded(ExpandableGroup group) {
        return this.expandableList.expandedGroupIndexes[this.expandableList.groups.indexOf(group)];
    }

    public boolean isGroupExpanded(int flatPos) {
        return this.expandableList.expandedGroupIndexes[this.expandableList.getUnflattenedPosition(flatPos).groupPos];
    }

    public boolean toggleGroup(int flatPos) {
        ExpandableListPosition listPos = this.expandableList.getUnflattenedPosition(flatPos);
        boolean expanded = this.expandableList.expandedGroupIndexes[listPos.groupPos];
        if (expanded) {
            collapseGroup(listPos);
        } else {
            expandGroup(listPos);
        }
        return expanded;
    }

    public boolean toggleGroup(ExpandableGroup group) {
        ExpandableList expandableList2 = this.expandableList;
        ExpandableListPosition listPos = expandableList2.getUnflattenedPosition(expandableList2.getFlattenedGroupIndex(group));
        boolean expanded = this.expandableList.expandedGroupIndexes[listPos.groupPos];
        if (expanded) {
            collapseGroup(listPos);
        } else {
            expandGroup(listPos);
        }
        return expanded;
    }
}
