package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.search.MrySearchView;

public class AddContactsActivity_ViewBinding implements Unbinder {
    private AddContactsActivity target;

    public AddContactsActivity_ViewBinding(AddContactsActivity target2, View source) {
        this.target = target2;
        target2.searchView = (MrySearchView) Utils.findRequiredViewAsType(source, R.id.searchView, "field 'searchView'", MrySearchView.class);
        target2.rcvList = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rcvList, "field 'rcvList'", RecyclerListView.class);
        target2.tvSearchHeader = (TextView) Utils.findRequiredViewAsType(source, R.id.tvSearchHeader, "field 'tvSearchHeader'", TextView.class);
        target2.tvSearchNumber = (TextView) Utils.findRequiredViewAsType(source, R.id.tvSearchNumber, "field 'tvSearchNumber'", TextView.class);
        target2.llSearchLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llSearchLayout, "field 'llSearchLayout'", LinearLayout.class);
        target2.searchLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.searchLayout, "field 'searchLayout'", FrameLayout.class);
    }

    public void unbind() {
        AddContactsActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.searchView = null;
            target2.rcvList = null;
            target2.tvSearchHeader = null;
            target2.tvSearchNumber = null;
            target2.llSearchLayout = null;
            target2.searchLayout = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
