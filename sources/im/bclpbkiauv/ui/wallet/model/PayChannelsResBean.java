package im.bclpbkiauv.ui.wallet.model;

import java.util.ArrayList;

public class PayChannelsResBean {
    private String channelCode;
    private ArrayList<PayTypeListBean> payTypeList;

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String channelCode2) {
        this.channelCode = channelCode2;
    }

    public ArrayList<PayTypeListBean> getPayTypeList() {
        return this.payTypeList;
    }

    public void setPayTypeList(ArrayList<PayTypeListBean> payTypeList2) {
        this.payTypeList = payTypeList2;
    }
}
