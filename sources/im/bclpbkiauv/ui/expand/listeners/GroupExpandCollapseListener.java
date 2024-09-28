package im.bclpbkiauv.ui.expand.listeners;

import im.bclpbkiauv.ui.expand.models.ExpandableGroup;

public interface GroupExpandCollapseListener {
    void onGroupCollapsed(ExpandableGroup expandableGroup);

    void onGroupExpanded(ExpandableGroup expandableGroup);
}
