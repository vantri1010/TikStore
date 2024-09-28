package im.bclpbkiauv.ui.hui.transfer;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;

public class TransferSendActivity_ViewBinding implements Unbinder {
    private TransferSendActivity target;

    public TransferSendActivity_ViewBinding(TransferSendActivity target2, View source) {
        this.target = target2;
        target2.bivTransferAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.bivTransferAvatar, "field 'bivTransferAvatar'", BackupImageView.class);
        target2.tvTransferName = (TextView) Utils.findRequiredViewAsType(source, R.id.tvTransferName, "field 'tvTransferName'", TextView.class);
        target2.tvPromtText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvPromtText, "field 'tvPromtText'", TextView.class);
        target2.etTransferAmountView = (EditText) Utils.findRequiredViewAsType(source, R.id.etTransferAmountView, "field 'etTransferAmountView'", EditText.class);
        target2.tvTransferHintView = (TextView) Utils.findRequiredViewAsType(source, R.id.tvTransferHintView, "field 'tvTransferHintView'", TextView.class);
        target2.tvHongbaoUnit = (TextView) Utils.findRequiredViewAsType(source, R.id.tvHongbaoUnit, "field 'tvHongbaoUnit'", TextView.class);
        target2.etTransferText = (EditText) Utils.findRequiredViewAsType(source, R.id.etTransferText, "field 'etTransferText'", EditText.class);
        target2.btnSendTransferView = (Button) Utils.findRequiredViewAsType(source, R.id.btnSendTransferView, "field 'btnSendTransferView'", Button.class);
        target2.tvTransferHintText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvTransferHintText, "field 'tvTransferHintText'", TextView.class);
    }

    public void unbind() {
        TransferSendActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.bivTransferAvatar = null;
            target2.tvTransferName = null;
            target2.tvPromtText = null;
            target2.etTransferAmountView = null;
            target2.tvTransferHintView = null;
            target2.tvHongbaoUnit = null;
            target2.etTransferText = null;
            target2.btnSendTransferView = null;
            target2.tvTransferHintText = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
