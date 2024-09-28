package im.bclpbkiauv.ui.wallet.model;

import java.io.Serializable;

public class AmountRulesBean implements Serializable {
    private String amount;
    private String channelCode;
    private String id;
    private String maxAmount;
    private String minAmount;
    private String payType;
    private int self;

    public String getMinAmount() {
        return this.minAmount;
    }

    public void setMinAmount(String minAmount2) {
        this.minAmount = minAmount2;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount2) {
        this.amount = amount2;
    }

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType2) {
        this.payType = payType2;
    }

    public int getSelf() {
        return this.self;
    }

    public void setSelf(int self2) {
        this.self = self2;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id2) {
        this.id = id2;
    }

    public String getMaxAmount() {
        return this.maxAmount;
    }

    public void setMaxAmount(String maxAmount2) {
        this.maxAmount = maxAmount2;
    }

    public String getChannelCode() {
        return this.channelCode;
    }

    public void setChannelCode(String channelCode2) {
        this.channelCode = channelCode2;
    }
}
