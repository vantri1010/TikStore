package im.bclpbkiauv.ui.hui.transfer.bean;

public class TransferOperation {
    private String businessKey;
    private String nonceStr;
    private String serialCode;
    private String userId;
    private String version;

    public TransferOperation(String serialCode2, String userId2, String nonceStr2, String businessKey2, String version2) {
        this.serialCode = serialCode2;
        this.userId = userId2;
        this.nonceStr = nonceStr2;
        this.businessKey = businessKey2;
        this.version = version2;
    }

    public String getSerialCode() {
        return this.serialCode;
    }

    public void setSerialCode(String serialCode2) {
        this.serialCode = serialCode2;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId2) {
        this.userId = userId2;
    }

    public String getNonceStr() {
        return this.nonceStr;
    }

    public void setNonceStr(String nonceStr2) {
        this.nonceStr = nonceStr2;
    }

    public String getBusinessKey() {
        return this.businessKey;
    }

    public void setBusinessKey(String businessKey2) {
        this.businessKey = businessKey2;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version2) {
        this.version = version2;
    }
}
