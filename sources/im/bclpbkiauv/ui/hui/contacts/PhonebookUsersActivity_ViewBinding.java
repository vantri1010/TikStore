package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.FrameLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryEmptyTextProgressView;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;
import im.bclpbkiauv.ui.hviews.sidebar.SideBar;

public class PhonebookUsersActivity_ViewBinding implements Unbinder {
    private PhonebookUsersActivity target;

    public PhonebookUsersActivity_ViewBinding(PhonebookUsersActivity target2, View source) {
        this.target = target2;
        target2.listView = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.listview, "field 'listView'", RecyclerListView.class);
        target2.searchView = (MrySearchView) Utils.findRequiredViewAsType(source, R.id.searchView, "field 'searchView'", MrySearchView.class);
        target2.searchLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.searchLayout, "field 'searchLayout'", FrameLayout.class);
        target2.mEmptyView = (MryEmptyTextProgressView) Utils.findRequiredViewAsType(source, R.id.emptyView, "field 'mEmptyView'", MryEmptyTextProgressView.class);
        target2.mSideBar = (SideBar) Utils.findRequiredViewAsType(source, R.id.sideBar, "field 'mSideBar'", SideBar.class);
        target2.mTvChar = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_char, "field 'mTvChar'", MryTextView.class);
    }

    public void unbind() {
        PhonebookUsersActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.listView = null;
            target2.searchView = null;
            target2.searchLayout = null;
            target2.mEmptyView = null;
            target2.mSideBar = null;
            target2.mTvChar = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
