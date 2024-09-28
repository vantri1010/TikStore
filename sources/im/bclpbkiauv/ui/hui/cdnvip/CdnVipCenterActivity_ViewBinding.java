package im.bclpbkiauv.ui.hui.cdnvip;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class CdnVipCenterActivity_ViewBinding implements Unbinder {
    private CdnVipCenterActivity target;
    private View view7f090096;

    public CdnVipCenterActivity_ViewBinding(final CdnVipCenterActivity target2, View source) {
        this.target = target2;
        target2.actionBarContainer = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.actionBarContainer, "field 'actionBarContainer'", FrameLayout.class);
        target2.ivAvatar = (BackupImageView) Utils.findRequiredViewAsType(source, R.id.ivAvatar, "field 'ivAvatar'", BackupImageView.class);
        target2.tvUserName = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvUserName, "field 'tvUserName'", MryTextView.class);
        target2.llBottom = Utils.findRequiredView(source, R.id.llBottom, "field 'llBottom'");
        target2.ivBgTop = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivBgTop, "field 'ivBgTop'", ImageView.class);
        target2.ivBgBottom = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivBgBottom, "field 'ivBgBottom'", ImageView.class);
        target2.tvVipTop = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvVipTop, "field 'tvVipTop'", MryTextView.class);
        target2.tvStatusOrTime = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvStatusOrTime, "field 'tvStatusOrTime'", MryTextView.class);
        target2.card = Utils.findRequiredView(source, R.id.card, "field 'card'");
        target2.tvTime = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvTime, "field 'tvTime'", MryTextView.class);
        target2.tvUnitPrice = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvUnitPrice, "field 'tvUnitPrice'", MryTextView.class);
        target2.tvTips = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvTips, "field 'tvTips'", MryTextView.class);
        View view = Utils.findRequiredView(source, R.id.btn, "field 'btn' and method 'onClick'");
        target2.btn = (MryTextView) Utils.castView(view, R.id.btn, "field 'btn'", MryTextView.class);
        this.view7f090096 = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onClick(p0);
            }
        });
        target2.tvTeQuan = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvTeQuan, "field 'tvTeQuan'", MryTextView.class);
        target2.rv = (RecyclerListView) Utils.findRequiredViewAsType(source, R.id.rv, "field 'rv'", RecyclerListView.class);
        target2.tvBottomTips = (TextView) Utils.findRequiredViewAsType(source, R.id.tvBottomTips, "field 'tvBottomTips'", TextView.class);
    }

    public void unbind() {
        CdnVipCenterActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.actionBarContainer = null;
            target2.ivAvatar = null;
            target2.tvUserName = null;
            target2.llBottom = null;
            target2.ivBgTop = null;
            target2.ivBgBottom = null;
            target2.tvVipTop = null;
            target2.tvStatusOrTime = null;
            target2.card = null;
            target2.tvTime = null;
            target2.tvUnitPrice = null;
            target2.tvTips = null;
            target2.btn = null;
            target2.tvTeQuan = null;
            target2.rv = null;
            target2.tvBottomTips = null;
            this.view7f090096.setOnClickListener((View.OnClickListener) null);
            this.view7f090096 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
