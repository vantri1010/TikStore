package im.bclpbkiauv.ui.hui.packet;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;

public class RedpktDetailActivity_ViewBinding implements Unbinder {
    private RedpktDetailActivity target;

    public RedpktDetailActivity_ViewBinding(RedpktDetailActivity target2, View source) {
        this.target = target2;
        target2.ivRptAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.ivRptAvatar, "field 'ivRptAvatar'", BackupImageView.class);
        target2.tvRptName = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRptName, "field 'tvRptName'", TextView.class);
        target2.tvRptGreet = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRptGreet, "field 'tvRptGreet'", TextView.class);
        target2.tvRptState = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRptState, "field 'tvRptState'", TextView.class);
        target2.llUserLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llUserLayout, "field 'llUserLayout'", LinearLayout.class);
        target2.bivReceiverAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.biv_receiver_avatar, "field 'bivReceiverAvatar'", BackupImageView.class);
        target2.tvRpkName = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_name, "field 'tvRpkName'", TextView.class);
        target2.tvRpkAmount = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_amount, "field 'tvRpkAmount'", TextView.class);
        target2.tvRpkReceiveTime = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_receive_time, "field 'tvRpkReceiveTime'", TextView.class);
        target2.flRpkRecordLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.fl_rpk_record_layout, "field 'flRpkRecordLayout'", FrameLayout.class);
        target2.tvRpkBackDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_back_desc, "field 'tvRpkBackDesc'", TextView.class);
    }

    public void unbind() {
        RedpktDetailActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.ivRptAvatar = null;
            target2.tvRptName = null;
            target2.tvRptGreet = null;
            target2.tvRptState = null;
            target2.llUserLayout = null;
            target2.bivReceiverAvatar = null;
            target2.tvRpkName = null;
            target2.tvRpkAmount = null;
            target2.tvRpkReceiveTime = null;
            target2.flRpkRecordLayout = null;
            target2.tvRpkBackDesc = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
