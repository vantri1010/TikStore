package im.bclpbkiauv.ui.wallet.model;

public class WithdrawResBean {
    private long cashAmount;
    private String createTime;
    private long freezeOthers;
    private long frozenCash;
    private long id;
    private long otherAmount;
    private long type;
    private int userId;

    public long getId() {
        return this.id;
    }

    public void setId(long id2) {
        this.id = id2;
    }

    public long getCashAmount() {
        return this.cashAmount;
    }

    public void setCashAmount(long cashAmount2) {
        this.cashAmount = cashAmount2;
    }

    public long getOtherAmount() {
        return this.otherAmount;
    }

    public void setOtherAmount(long otherAmount2) {
        this.otherAmount = otherAmount2;
    }

    public long getFrozenCash() {
        return this.frozenCash;
    }

    public void setFrozenCash(long frozenCash2) {
        this.frozenCash = frozenCash2;
    }

    public long getFreezeOthers() {
        return this.freezeOthers;
    }

    public void setFreezeOthers(long freezeOthers2) {
        this.freezeOthers = freezeOthers2;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long type2) {
        this.type = type2;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }
}
