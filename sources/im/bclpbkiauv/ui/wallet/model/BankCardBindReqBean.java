package im.bclpbkiauv.ui.wallet.model;

public class BankCardBindReqBean {
    private String bank;
    private String bankCode;
    private String bankName;
    private int bankType;
    private int bindType;
    private String businessKey;
    private String code;
    private String idCard;
    private String openBank;
    private int type;
    private int userId;
    private String userName;

    public String getBusinessKey() {
        return this.businessKey;
    }

    public void setBusinessKey(String businessKey2) {
        this.businessKey = businessKey2;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId2) {
        this.userId = userId2;
    }

    public int getBindType() {
        return this.bindType;
    }

    public void setBindType(int bindType2) {
        this.bindType = bindType2;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public String getBank() {
        return this.bank;
    }

    public void setBank(String bank2) {
        this.bank = bank2;
    }

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard2) {
        this.idCard = idCard2;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName2) {
        this.userName = userName2;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code2) {
        this.code = code2;
    }

    public int getBankType() {
        return this.bankType;
    }

    public void setBankType(int bankType2) {
        this.bankType = bankType2;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName2) {
        this.bankName = bankName2;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode2) {
        this.bankCode = bankCode2;
    }

    public String getOpenBank() {
        return this.openBank;
    }

    public void setOpenBank(String openBank2) {
        this.openBank = openBank2;
    }
}
