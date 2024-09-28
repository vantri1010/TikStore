package im.bclpbkiauv.ui.hui.friendscircle_v1.ui;

import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class WhoCanWatchActivity_ViewBinding implements Unbinder {
    private WhoCanWatchActivity target;

    public WhoCanWatchActivity_ViewBinding(WhoCanWatchActivity target2, View source) {
        this.target = target2;
        target2.rv = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rv, "field 'rv'", RecyclerListView.class);
    }

    public void unbind() {
        WhoCanWatchActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.rv = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
