package im.bclpbkiauv.ui.hui.transfer;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class TransferStatusActivity_ViewBinding implements Unbinder {
    private TransferStatusActivity target;
    private View view7f0900a9;
    private View view7f0905ab;

    public TransferStatusActivity_ViewBinding(final TransferStatusActivity target2, View source) {
        this.target = target2;
        target2.ivTransferStateImg = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivTransferStateImg, "field 'ivTransferStateImg'", ImageView.class);
        target2.tvTransferStateText = (TextView) Utils.findRequiredViewAsType(source, R.id.tvTransferStateText, "field 'tvTransferStateText'", TextView.class);
        target2.tvTransferAmount = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvTransferAmount, "field 'tvTransferAmount'", MryTextView.class);
        target2.tvHongbaoType = (TextView) Utils.findRequiredViewAsType(source, R.id.tvHongbaoType, "field 'tvHongbaoType'", TextView.class);
        View view = Utils.findRequiredView(source, R.id.btnTransferStateButton, "field 'btnTransferStateButton' and method 'onClick'");
        target2.btnTransferStateButton = (MryRoundButton) Utils.castView(view, R.id.btnTransferStateButton, "field 'btnTransferStateButton'", MryRoundButton.class);
        this.view7f0900a9 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        View view2 = Utils.findRequiredView(source, R.id.tvWallet, "field 'tvWallet' and method 'onClick'");
        target2.tvWallet = (TextView) Utils.castView(view2, R.id.tvWallet, "field 'tvWallet'", TextView.class);
        this.view7f0905ab = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.tvRefuseTransfer = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRefuseTransfer, "field 'tvRefuseTransfer'", TextView.class);
        target2.llTransferAboutLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llTransferAboutLayout, "field 'llTransferAboutLayout'", LinearLayout.class);
        target2.tvTransferTime = (TextView) Utils.findRequiredViewAsType(source, R.id.tvTransferTime, "field 'tvTransferTime'", TextView.class);
        target2.tvActionTime = (TextView) Utils.findRequiredViewAsType(source, R.id.tvActionTime, "field 'tvActionTime'", TextView.class);
        target2.tvRemarks = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRemarks, "field 'tvRemarks'", TextView.class);
    }

    public void unbind() {
        TransferStatusActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.ivTransferStateImg = null;
            target2.tvTransferStateText = null;
            target2.tvTransferAmount = null;
            target2.tvHongbaoType = null;
            target2.btnTransferStateButton = null;
            target2.tvWallet = null;
            target2.tvRefuseTransfer = null;
            target2.llTransferAboutLayout = null;
            target2.tvTransferTime = null;
            target2.tvActionTime = null;
            target2.tvRemarks = null;
            this.view7f0900a9.setOnClickListener((View.OnClickListener) null);
            this.view7f0900a9 = null;
            this.view7f0905ab.setOnClickListener((View.OnClickListener) null);
            this.view7f0905ab = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
