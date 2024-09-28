package im.bclpbkiauv.ui.wallet.model;

public class PayPasswordReqBean {
    private String businessKey;
    private String code;
    private String confirmPayPassWord;
    private String payPassWord;
    private String safetyCode;
    private int type;
    private int userId;

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

    public String getPayPassWord() {
        return this.payPassWord;
    }

    public void setPayPassWord(String payPassWord2) {
        this.payPassWord = payPassWord2;
    }

    public String getConfirmPayPassWord() {
        return this.confirmPayPassWord;
    }

    public void setConfirmPayPassWord(String confirmPayPassWord2) {
        this.confirmPayPassWord = confirmPayPassWord2;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        this.type = type2;
    }

    public String getSafetyCode() {
        return this.safetyCode;
    }

    public void setSafetyCode(String safetyCode2) {
        this.safetyCode = safetyCode2;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code2) {
        this.code = code2;
    }
}
