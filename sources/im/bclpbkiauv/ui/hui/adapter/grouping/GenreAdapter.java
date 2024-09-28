package im.bclpbkiauv.ui.hui.adapter.grouping;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.expand.ExpandableRecyclerViewAdapter;
import im.bclpbkiauv.ui.expand.models.ExpandableGroup;
import im.bclpbkiauv.ui.hui.contacts.MyGroupingActivity;
import im.bclpbkiauv.ui.hviews.slidemenu.SwipeLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenreAdapter extends ExpandableRecyclerViewAdapter<GenreViewHolder, ArtistViewHolder> {
    private MyGroupingActivity activity;
    private Map<Integer, Boolean> expandStateMap = new HashMap();

    public GenreAdapter(List<Genre> groups, MyGroupingActivity activity2) {
        super(groups);
        this.activity = activity2;
    }

    public MyGroupingActivity getActivity() {
        return this.activity;
    }

    public GenreViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_grouping_layout, parent, false);
        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        return new GenreViewHolder(view);
    }

    public ArtistViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout swipeLayout = new SwipeLayout(parent.getContext()) {
            public boolean onTouchEvent(MotionEvent event) {
                if (isExpanded()) {
                    return true;
                }
                return super.onTouchEvent(event);
            }
        };
        swipeLayout.setUpView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts_grouping_child_layout, parent, false));
        swipeLayout.setNeedDivderBetweenMainAndMenu(false);
        swipeLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        return new ArtistViewHolder(swipeLayout);
    }

    public void onBindChildViewHolder(ArtistViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        Genre genre = (Genre) group;
        holder.setUserData((Artist) genre.getItems().get(childIndex), genre, this);
    }

    public void onBindGroupViewHolder(GenreViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGenreData(group, flatPosition, getGroups());
    }

    public void storeExpandState() {
        this.expandStateMap.clear();
        if (this.expandableList.groups != null && this.expandableList.expandedGroupIndexes != null && this.expandableList.groups.size() == this.expandableList.expandedGroupIndexes.length) {
            for (int i = 0; i < this.expandableList.groups.size(); i++) {
                this.expandStateMap.put(Integer.valueOf(((Genre) this.expandableList.groups.get(i)).getGroupId()), Boolean.valueOf(this.expandableList.expandedGroupIndexes[i]));
            }
        }
    }

    public void restoreExpandState() {
        if (this.expandableList.groups != null) {
            this.expandableList.expandedGroupIndexes = new boolean[this.expandableList.groups.size()];
            for (int i = 0; i < this.expandableList.groups.size(); i++) {
                Genre genre = (Genre) this.expandableList.groups.get(i);
                this.expandableList.expandedGroupIndexes[i] = this.expandStateMap.get(Integer.valueOf(genre.getGroupId())) != null ? this.expandStateMap.get(Integer.valueOf(genre.getGroupId())).booleanValue() : false;
            }
        }
    }
}
