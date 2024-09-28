package im.bclpbkiauv.ui.hui.packet;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.RecyclerListView;

public class RedpktGroupStateActivity_ViewBinding implements Unbinder {
    private RedpktGroupStateActivity target;

    public RedpktGroupStateActivity_ViewBinding(RedpktGroupStateActivity target2, View source) {
        this.target = target2;
        target2.rcyRpkHistory = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rcy_rpk_history, "field 'rcyRpkHistory'", RecyclerListView.class);
        target2.tvRpkBackDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_back_desc, "field 'tvRpkBackDesc'", TextView.class);
    }

    public void unbind() {
        RedpktGroupStateActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.rcyRpkHistory = null;
            target2.tvRpkBackDesc = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
