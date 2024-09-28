package im.bclpbkiauv.ui.hui.packet;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;

public class RedpktDetailReceiverActivity_ViewBinding implements Unbinder {
    private RedpktDetailReceiverActivity target;

    public RedpktDetailReceiverActivity_ViewBinding(RedpktDetailReceiverActivity target2, View source) {
        this.target = target2;
        target2.bivRpkAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.biv_rpk_avatar, "field 'bivRpkAvatar'", BackupImageView.class);
        target2.tvRpkName = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_name, "field 'tvRpkName'", TextView.class);
        target2.tvRpkGreet = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_greet, "field 'tvRpkGreet'", TextView.class);
        target2.tvRpkAmount = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_amount, "field 'tvRpkAmount'", TextView.class);
        target2.tvRpkDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_desc, "field 'tvRpkDesc'", TextView.class);
        target2.llUserLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llUserLayout, "field 'llUserLayout'", LinearLayout.class);
    }

    public void unbind() {
        RedpktDetailReceiverActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.bivRpkAvatar = null;
            target2.tvRpkName = null;
            target2.tvRpkGreet = null;
            target2.tvRpkAmount = null;
            target2.tvRpkDesc = null;
            target2.llUserLayout = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
