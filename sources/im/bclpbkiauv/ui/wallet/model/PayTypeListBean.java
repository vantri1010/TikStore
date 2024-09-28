package im.bclpbkiauv.ui.wallet.model;

import java.io.Serializable;

public class PayTypeListBean implements Serializable {
    private AmountRulesBean amountRules;
    private String belongType;
    private String name;
    private String payType;
    private String rate;
    private int supportId;
    private int templateId;

    public String getBelongType() {
        return this.belongType;
    }

    public void setBelongType(String belongType2) {
        this.belongType = belongType2;
    }

    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType2) {
        this.payType = payType2;
    }

    public AmountRulesBean getAmountRules() {
        return this.amountRules;
    }

    public void setAmountRules(AmountRulesBean amountRules2) {
        this.amountRules = amountRules2;
    }

    public String getRate() {
        return this.rate;
    }

    public void setRate(String rate2) {
        this.rate = rate2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public int getSupportId() {
        return this.supportId;
    }

    public void setSupportId(int supportId2) {
        this.supportId = supportId2;
    }

    public int getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(int templateId2) {
        this.templateId = templateId2;
    }
}
