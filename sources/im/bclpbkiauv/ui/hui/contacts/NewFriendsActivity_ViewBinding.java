package im.bclpbkiauv.ui.hui.contacts;

import android.view.View;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RadialProgressView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class NewFriendsActivity_ViewBinding implements Unbinder {
    private NewFriendsActivity target;

    public NewFriendsActivity_ViewBinding(NewFriendsActivity target2, View source) {
        this.target = target2;
        target2.listview = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.listview, "field 'listview'", RecyclerListView.class);
        target2.progressBar = (RadialProgressView) Utils.findRequiredViewAsType(source, R.id.progressBar, "field 'progressBar'", RadialProgressView.class);
        target2.emptyLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.emptyLayout, "field 'emptyLayout'", LinearLayout.class);
        target2.tvEmptyText = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvEmptyText, "field 'tvEmptyText'", MryTextView.class);
    }

    public void unbind() {
        NewFriendsActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.listview = null;
            target2.progressBar = null;
            target2.emptyLayout = null;
            target2.tvEmptyText = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
