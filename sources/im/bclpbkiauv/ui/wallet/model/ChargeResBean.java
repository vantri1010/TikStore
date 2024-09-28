package im.bclpbkiauv.ui.wallet.model;

public class ChargeResBean {
    private String action;
    private String amount;
    private String orderId;
    private String orderStatus;
    private String requestType;
    private String statusDesc;

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount2) {
        this.amount = amount2;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    public void setOrderStatus(String orderStatus2) {
        this.orderStatus = orderStatus2;
    }

    public String getStatusDesc() {
        return this.statusDesc;
    }

    public void setStatusDesc(String statusDesc2) {
        this.statusDesc = statusDesc2;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(String requestType2) {
        this.requestType = requestType2;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action2) {
        this.action = action2;
    }
}
