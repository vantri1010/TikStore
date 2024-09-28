package im.bclpbkiauv.ui.expand;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import im.bclpbkiauv.ui.expand.models.ExpandableListPosition;
import im.bclpbkiauv.ui.expand.viewholders.ChildViewHolder;
import im.bclpbkiauv.ui.expand.viewholders.GroupViewHolder;
import java.util.List;

public abstract class MultiTypeExpandableRecyclerViewAdapter<GVH extends GroupViewHolder, CVH extends ChildViewHolder> extends ExpandableRecyclerViewAdapter<GVH, CVH> {
    public MultiTypeExpandableRecyclerViewAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isGroup(viewType)) {
            GVH gvh = onCreateGroupViewHolder(parent, viewType);
            gvh.setOnGroupClickListener(this);
            return gvh;
        } else if (isChild(viewType)) {
            return onCreateChildViewHolder(parent, viewType);
        } else {
            throw new IllegalArgumentException("viewType is not valid");
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ExpandableListPosition listPos = this.expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = this.expandableList.getExpandableGroup(listPos);
        if (isGroup(getItemViewType(position))) {
            onBindGroupViewHolder((GroupViewHolder) holder, position, group);
            if (isGroupExpanded(group)) {
                ((GroupViewHolder) holder).expand();
            } else {
                ((GroupViewHolder) holder).collapse();
            }
        } else if (isChild(getItemViewType(position))) {
            onBindChildViewHolder((ChildViewHolder) holder, position, group, listPos.childPos);
        }
    }

    public int getItemViewType(int position) {
        ExpandableListPosition listPosition = this.expandableList.getUnflattenedPosition(position);
        ExpandableGroup group = this.expandableList.getExpandableGroup(listPosition);
        int viewType = listPosition.type;
        if (viewType == 1) {
            return getChildViewType(position, group, listPosition.childPos);
        }
        if (viewType != 2) {
            return viewType;
        }
        return getGroupViewType(position, group);
    }

    public int getChildViewType(int position, ExpandableGroup group, int childIndex) {
        return super.getItemViewType(position);
    }

    public int getGroupViewType(int position, ExpandableGroup group) {
        return super.getItemViewType(position);
    }

    public boolean isGroup(int viewType) {
        return viewType == 2;
    }

    public boolean isChild(int viewType) {
        return viewType == 1;
    }
}
