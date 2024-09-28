package im.bclpbkiauv.ui.wallet.model;

public class BankCardInfo {
    private String accountName;
    private String accountNo;
    private String bankCode;
    private String bankName;
    private String branchName;
    private String city;
    private String currency;
    private String idCard;
    private String province;

    public String getIdCard() {
        return this.idCard;
    }

    public void setIdCard(String idCard2) {
        this.idCard = idCard2;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public void setAccountName(String accountName2) {
        this.accountName = accountName2;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public void setAccountNo(String accountNo2) {
        this.accountNo = accountNo2;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode2) {
        this.bankCode = bankCode2;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName2) {
        this.bankName = bankName2;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName2) {
        this.branchName = branchName2;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province2) {
        this.province = province2;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city2) {
        this.city = city2;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency2) {
        this.currency = currency2;
    }
}
