package im.bclpbkiauv.ui.hui.packet;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hviews.MryTextView;

public class RedpktSendActivity_ViewBinding implements Unbinder {
    private RedpktSendActivity target;

    public RedpktSendActivity_ViewBinding(RedpktSendActivity target2, View source) {
        this.target = target2;
        target2.tvRpkPromet = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_rpk_promet, "field 'tvRpkPromet'", MryTextView.class);
        target2.etRedPacketAmount = (EditText) Utils.findRequiredViewAsType(source, R.id.et_red_packet_amount, "field 'etRedPacketAmount'", EditText.class);
        target2.tvRedPacketAmountHint = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_red_packet_amount_hint, "field 'tvRedPacketAmountHint'", TextView.class);
        target2.tvRedPacketUnit = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_red_packet_unit, "field 'tvRedPacketUnit'", MryTextView.class);
        target2.llAmountLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_amount_layout, "field 'llAmountLayout'", LinearLayout.class);
        target2.etRedPacketGreet = (EditText) Utils.findRequiredViewAsType(source, R.id.et_red_packet_greet, "field 'etRedPacketGreet'", EditText.class);
        target2.tvRedPacketGreetHint = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_red_packet_greet_hint, "field 'tvRedPacketGreetHint'", TextView.class);
        target2.llRemarkLayout = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.ll_remark_layout, "field 'llRemarkLayout'", LinearLayout.class);
        target2.tvAmountShowView = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_amount_show_view, "field 'tvAmountShowView'", TextView.class);
        target2.tvAmountShowUnit = (MryTextView) Utils.findRequiredViewAsType(source, R.id.tv_amount_show_unit, "field 'tvAmountShowUnit'", MryTextView.class);
        target2.flAmountShowLayout = (FrameLayout) Utils.findRequiredViewAsType(source, R.id.fl_amount_show_layout, "field 'flAmountShowLayout'", FrameLayout.class);
        target2.btnRedPacketSend = (Button) Utils.findRequiredViewAsType(source, R.id.btn_red_packet_send, "field 'btnRedPacketSend'", Button.class);
        target2.tvTimeOutDesc = (TextView) Utils.findRequiredViewAsType(source, R.id.tv_time_out_desc, "field 'tvTimeOutDesc'", TextView.class);
    }

    public void unbind() {
        RedpktSendActivity target2 = this.target;
        if (target2 != null) {
            this.target = null;
            target2.tvRpkPromet = null;
            target2.etRedPacketAmount = null;
            target2.tvRedPacketAmountHint = null;
            target2.tvRedPacketUnit = null;
            target2.llAmountLayout = null;
            target2.etRedPacketGreet = null;
            target2.tvRedPacketGreetHint = null;
            target2.llRemarkLayout = null;
            target2.tvAmountShowView = null;
            target2.tvAmountShowUnit = null;
            target2.flAmountShowLayout = null;
            target2.btnRedPacketSend = null;
            target2.tvTimeOutDesc = null;
            return;
        }
        throw new IllegalStateException("Bindings already cleared.");
    }
}
