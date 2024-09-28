package im.bclpbkiauv.ui.hui.packet;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryImageView;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class BillDetailsActivity_ViewBinding implements Unbinder {
    private BillDetailsActivity target;
    private View view7f0901e5;
    private View view7f0901fd;
    private View view7f0901fe;
    private View view7f0901ff;
    private View view7f090200;

    public BillDetailsActivity_ViewBinding(final BillDetailsActivity target2, View source) {
        this.target = target2;
        target2.ivIcon = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivIcon, "field 'ivIcon'", ImageView.class);
        target2.tvAmount = (TextView) Utils.findRequiredViewAsType(source, R.id.tvAmount, "field 'tvAmount'", TextView.class);
        target2.llIconView = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llIconView, "field 'llIconView'", LinearLayout.class);
        target2.ivIcon2 = (ImageView) Utils.findRequiredViewAsType(source, R.id.ivIcon2, "field 'ivIcon2'", ImageView.class);
        target2.tvAmount2 = (TextView) Utils.findRequiredViewAsType(source, R.id.tvAmount2, "field 'tvAmount2'", TextView.class);
        target2.llContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llContainer, "field 'llContainer'", LinearLayout.class);
        target2.tvRowName1 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName1, "field 'tvRowName1'", MryTextView.class);
        target2.tvRowAddress1 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress1, "field 'tvRowAddress1'", MryTextView.class);
        View view = Utils.findRequiredView(source, R.id.ivRowCopy1, "field 'ivRowCopy1' and method 'onViewClicked'");
        target2.ivRowCopy1 = (MryImageView) Utils.castView(view, R.id.ivRowCopy1, "field 'ivRowCopy1'", MryImageView.class);
        this.view7f0901fe = view;
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.tvRowName2 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName2, "field 'tvRowName2'", MryTextView.class);
        target2.tvRowAddress2 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress2, "field 'tvRowAddress2'", MryTextView.class);
        View view2 = Utils.findRequiredView(source, R.id.ivRowCopy2, "field 'ivRowCopy2' and method 'onViewClicked'");
        target2.ivRowCopy2 = (MryImageView) Utils.castView(view2, R.id.ivRowCopy2, "field 'ivRowCopy2'", MryImageView.class);
        this.view7f0901ff = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.tvRowName3 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName3, "field 'tvRowName3'", MryTextView.class);
        target2.tvRowAddress3 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress3, "field 'tvRowAddress3'", MryTextView.class);
        View view3 = Utils.findRequiredView(source, R.id.ivRowCopy3, "field 'ivRowCopy3' and method 'onViewClicked'");
        target2.ivRowCopy3 = (MryImageView) Utils.castView(view3, R.id.ivRowCopy3, "field 'ivRowCopy3'", MryImageView.class);
        this.view7f090200 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.tvRowName4 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName4, "field 'tvRowName4'", MryTextView.class);
        target2.tvRowAddress4 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress4, "field 'tvRowAddress4'", MryTextView.class);
        target2.tvRowName5 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName5, "field 'tvRowName5'", MryTextView.class);
        target2.tvRowAddress5 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress5, "field 'tvRowAddress5'", MryTextView.class);
        target2.rlRow6 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlRow6, "field 'rlRow6'", RelativeLayout.class);
        target2.tvRowName6 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName6, "field 'tvRowName6'", MryTextView.class);
        target2.tvRowAddress6 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress6, "field 'tvRowAddress6'", MryTextView.class);
        target2.tvRowName7 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName7, "field 'tvRowName7'", MryTextView.class);
        target2.tvRowAddress7 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress7, "field 'tvRowAddress7'", MryTextView.class);
        target2.rlRow8 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlRow8, "field 'rlRow8'", RelativeLayout.class);
        target2.tvRowName8 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowName8, "field 'tvRowName8'", MryTextView.class);
        target2.tvRowAddress8 = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRowAddress8, "field 'tvRowAddress8'", MryTextView.class);
        target2.llCurrencyContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llCurrencyContainer, "field 'llCurrencyContainer'", LinearLayout.class);
        target2.llCurrencyInfo1 = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llCurrencyInfo1, "field 'llCurrencyInfo1'", LinearLayout.class);
        target2.rlCurrencyRow1 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlCurrencyRow1, "field 'rlCurrencyRow1'", RelativeLayout.class);
        target2.rlCurrencyRow2 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlCurrencyRow2, "field 'rlCurrencyRow2'", RelativeLayout.class);
        target2.rlCurrencyRow4 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlCurrencyRow4, "field 'rlCurrencyRow4'", RelativeLayout.class);
        target2.rlCurrencyRow5 = (RelativeLayout) Utils.findRequiredViewAsType(source, R.id.rlCurrencyRow5, "field 'rlCurrencyRow5'", RelativeLayout.class);
        target2.tvRow1Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow1Name, "field 'tvRow1Name'", TextView.class);
        target2.tvRow1Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow1Info, "field 'tvRow1Info'", MryTextView.class);
        target2.tvRow2Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow2Name, "field 'tvRow2Name'", TextView.class);
        target2.tvRow2Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow2Info, "field 'tvRow2Info'", MryTextView.class);
        target2.tvRow3Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow3Name, "field 'tvRow3Name'", TextView.class);
        target2.tvRow3Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow3Info, "field 'tvRow3Info'", MryTextView.class);
        target2.tvRow4Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow4Name, "field 'tvRow4Name'", TextView.class);
        target2.tvRow4Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow4Info, "field 'tvRow4Info'", MryTextView.class);
        target2.tvRow5Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow5Name, "field 'tvRow5Name'", TextView.class);
        target2.tvRow5Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow5Info, "field 'tvRow5Info'", MryTextView.class);
        target2.tvRow6Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow6Name, "field 'tvRow6Name'", TextView.class);
        target2.tvRow6Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow6Info, "field 'tvRow6Info'", MryTextView.class);
        View view4 = Utils.findRequiredView(source, R.id.ivRow6Copy, "field 'ivRow6Copy' and method 'onViewClicked'");
        target2.ivRow6Copy = (MryImageView) Utils.castView(view4, R.id.ivRow6Copy, "field 'ivRow6Copy'", MryImageView.class);
        this.view7f0901fd = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
        target2.tvRow7Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow7Name, "field 'tvRow7Name'", TextView.class);
        target2.tvRow7Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow7Info, "field 'tvRow7Info'", MryTextView.class);
        target2.tvRow8Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow8Name, "field 'tvRow8Name'", TextView.class);
        target2.tvRow8Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow8Info, "field 'tvRow8Info'", MryTextView.class);
        target2.tvRow9Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow9Name, "field 'tvRow9Name'", TextView.class);
        target2.tvRow9Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow9Info, "field 'tvRow9Info'", MryTextView.class);
        target2.tvRow10Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow10Name, "field 'tvRow10Name'", TextView.class);
        target2.tvRow10Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow10Info, "field 'tvRow10Info'", MryTextView.class);
        target2.tvRow11Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvRow11Name, "field 'tvRow11Name'", TextView.class);
        target2.tvRow11Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvRow11Info, "field 'tvRow11Info'", MryTextView.class);
        target2.llFiatContainer = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llFiatContainer, "field 'llFiatContainer'", LinearLayout.class);
        target2.llFiatInfo1 = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.llFiatInfo1, "field 'llFiatInfo1'", LinearLayout.class);
        target2.tvFiatRow1Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow1Name, "field 'tvFiatRow1Name'", TextView.class);
        target2.tvFiatRow1Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow1Info, "field 'tvFiatRow1Info'", MryTextView.class);
        target2.tvFiatRow2Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow2Name, "field 'tvFiatRow2Name'", TextView.class);
        target2.tvFiatRow2Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow2Info, "field 'tvFiatRow2Info'", MryTextView.class);
        target2.tvFiatRow3Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow3Name, "field 'tvFiatRow3Name'", TextView.class);
        target2.tvFiatRow3Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow3Info, "field 'tvFiatRow3Info'", MryTextView.class);
        target2.tvFiatRow4Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow4Name, "field 'tvFiatRow4Name'", TextView.class);
        target2.tvFiatRow4Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow4Info, "field 'tvFiatRow4Info'", MryTextView.class);
        target2.tvFiatRow5Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow5Name, "field 'tvFiatRow5Name'", TextView.class);
        target2.tvFiatRow5Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow5Info, "field 'tvFiatRow5Info'", MryTextView.class);
        target2.tvFiatRow6Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow6Name, "field 'tvFiatRow6Name'", TextView.class);
        target2.tvFiatRow6Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow6Info, "field 'tvFiatRow6Info'", MryTextView.class);
        target2.tvFiatRow7Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow7Name, "field 'tvFiatRow7Name'", TextView.class);
        target2.tvFiatRow7Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow7Info, "field 'tvFiatRow7Info'", MryTextView.class);
        target2.tvFiatRow8Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow8Name, "field 'tvFiatRow8Name'", TextView.class);
        target2.tvFiatRow8Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow8Info, "field 'tvFiatRow8Info'", MryTextView.class);
        target2.tvFiatRow9Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow9Name, "field 'tvFiatRow9Name'", TextView.class);
        target2.tvFiatRow9Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow9Info, "field 'tvFiatRow9Info'", MryTextView.class);
        target2.tvFiatRow10Name = (TextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow10Name, "field 'tvFiatRow10Name'", TextView.class);
        target2.tvFiatRow10Info = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tvFiatRow10Info, "field 'tvFiatRow10Info'", MryTextView.class);
        View view5 = Utils.findRequiredView(source, R.id.ivFiat5Copy, "method 'onViewClicked'");
        this.view7f0901e5 = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target2.onViewClicked(p0);
            }
        });
    }

    public void unbind() {
        BillDetailsActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.ivIcon = null;
            target2.tvAmount = null;
            target2.llIconView = null;
            target2.ivIcon2 = null;
            target2.tvAmount2 = null;
            target2.llContainer = null;
            target2.tvRowName1 = null;
            target2.tvRowAddress1 = null;
            target2.ivRowCopy1 = null;
            target2.tvRowName2 = null;
            target2.tvRowAddress2 = null;
            target2.ivRowCopy2 = null;
            target2.tvRowName3 = null;
            target2.tvRowAddress3 = null;
            target2.ivRowCopy3 = null;
            target2.tvRowName4 = null;
            target2.tvRowAddress4 = null;
            target2.tvRowName5 = null;
            target2.tvRowAddress5 = null;
            target2.rlRow6 = null;
            target2.tvRowName6 = null;
            target2.tvRowAddress6 = null;
            target2.tvRowName7 = null;
            target2.tvRowAddress7 = null;
            target2.rlRow8 = null;
            target2.tvRowName8 = null;
            target2.tvRowAddress8 = null;
            target2.llCurrencyContainer = null;
            target2.llCurrencyInfo1 = null;
            target2.rlCurrencyRow1 = null;
            target2.rlCurrencyRow2 = null;
            target2.rlCurrencyRow4 = null;
            target2.rlCurrencyRow5 = null;
            target2.tvRow1Name = null;
            target2.tvRow1Info = null;
            target2.tvRow2Name = null;
            target2.tvRow2Info = null;
            target2.tvRow3Name = null;
            target2.tvRow3Info = null;
            target2.tvRow4Name = null;
            target2.tvRow4Info = null;
            target2.tvRow5Name = null;
            target2.tvRow5Info = null;
            target2.tvRow6Name = null;
            target2.tvRow6Info = null;
            target2.ivRow6Copy = null;
            target2.tvRow7Name = null;
            target2.tvRow7Info = null;
            target2.tvRow8Name = null;
            target2.tvRow8Info = null;
            target2.tvRow9Name = null;
            target2.tvRow9Info = null;
            target2.tvRow10Name = null;
            target2.tvRow10Info = null;
            target2.tvRow11Name = null;
            target2.tvRow11Info = null;
            target2.llFiatContainer = null;
            target2.llFiatInfo1 = null;
            target2.tvFiatRow1Name = null;
            target2.tvFiatRow1Info = null;
            target2.tvFiatRow2Name = null;
            target2.tvFiatRow2Info = null;
            target2.tvFiatRow3Name = null;
            target2.tvFiatRow3Info = null;
            target2.tvFiatRow4Name = null;
            target2.tvFiatRow4Info = null;
            target2.tvFiatRow5Name = null;
            target2.tvFiatRow5Info = null;
            target2.tvFiatRow6Name = null;
            target2.tvFiatRow6Info = null;
            target2.tvFiatRow7Name = null;
            target2.tvFiatRow7Info = null;
            target2.tvFiatRow8Name = null;
            target2.tvFiatRow8Info = null;
            target2.tvFiatRow9Name = null;
            target2.tvFiatRow9Info = null;
            target2.tvFiatRow10Name = null;
            target2.tvFiatRow10Info = null;
            this.view7f0901fe.setOnClickListener((View.OnClickListener) null);
            this.view7f0901fe = null;
            this.view7f0901ff.setOnClickListener((View.OnClickListener) null);
            this.view7f0901ff = null;
            this.view7f090200.setOnClickListener((View.OnClickListener) null);
            this.view7f090200 = null;
            this.view7f0901fd.setOnClickListener((View.OnClickListener) null);
            this.view7f0901fd = null;
            this.view7f0901e5.setOnClickListener((View.OnClickListener) null);
            this.view7f0901e5 = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
