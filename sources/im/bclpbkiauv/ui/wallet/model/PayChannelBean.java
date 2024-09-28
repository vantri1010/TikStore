package im.bclpbkiauv.ui.wallet.model;

import java.io.Serializable;

public class PayChannelBean implements Serializable {
    private String channelCode;
    private PayTypeListBean payType;

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String channelCode2) {
        this.channelCode = channelCode2;
    }

    public PayTypeListBean getPayType() {
        return this.payType;
    }

    public void setPayType(PayTypeListBean payType2) {
        this.payType = payType2;
    }
}
