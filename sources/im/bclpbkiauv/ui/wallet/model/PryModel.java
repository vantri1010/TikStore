package im.bclpbkiauv.ui.wallet.model;

public class PryModel {
    private String amount;
    private String rate;
    private int type;

    public PryModel(int type2, String amount2, String rate2) {
        this.type = type2;
        this.amount = amount2;
        this.rate = rate2;
    }

    public int getType() {
        return this.type;
    }

    public String getAmount() {
        return this.amount;
    }

    public String getRate() {
        return this.rate;
    }
}
