package im.bclpbkiauv.ui.wallet.model;

public class WithdrawStatusBean {
    private String amount;
    private String channelCode;
    private String channelName;
    private String createTime;
    private String endTime;
    private String orderId;
    private String remark;
    private String serviceCharge;
    private String status;

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount2) {
        this.amount = amount2;
    }

    public String getServiceCharge() {
        return this.serviceCharge;
    }

    public void setServiceCharge(String serviceCharge2) {
        this.serviceCharge = serviceCharge2;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status2) {
        this.status = status2;
    }

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String channelCode2) {
        this.channelCode = channelCode2;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(String channelName2) {
        this.channelName = channelName2;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark2) {
        this.remark = remark2;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime2) {
        this.endTime = endTime2;
    }
}
