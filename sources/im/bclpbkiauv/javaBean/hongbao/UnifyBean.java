package im.bclpbkiauv.javaBean.hongbao;

public class UnifyBean {
    public static final String API_VERSION = "0.0.1";
    public static final String BUSINESS_KEY_REDPACKET = "unified_order_red";
    public static final String BUSINESS_KEY_REDPACKET_CHECK = "check_red";
    public static final String BUSINESS_KEY_REDPACKET_DETAIL = "red_details";
    public static final String BUSINESS_KEY_REDPACKET_RECEIVE = "receive_red_v2";
    public static final String BUSINESS_KEY_REDPACKET_RECORDS = "red_records";
    public static final String BUSINESS_KEY_TRANSFER = "unified_order_carry_over";
    public static final String BUSINESS_KEY_TRANSFER_CHECK = "carry_over_details";
    public static final String BUSINESS_KEY_TRANSFER_RECEIVE = "rob_carry_over";
    public static final String BUSINESS_KEY_TRANSFER_REFUSE = "cancel_carry_over";
    public static final String REDPACKET_TRANSFER_API_VERSION = "1";
    public static final String REDPACKET_TRANSFER_API_VERSION_TEMP = "2.0.1";
    public static final String REDPKG_GROUP_PERSON_TYPE = "2";
    public static final String REDPKG_GROUP_TYPE = "1";
    public static final String REDPKG_PERSON_TYPE = "0";
    public static final String REDPKG_SEND_FIXAMOUNT_TYPE = "0";
    public static final String REDPKG_SEND_RANDOM_TYPE = "1";
    public static final String TRADE_REDPKG_TYPE = "1";
    public static final String TRADE_TRANSF_TYPE = "0";
    private String attach;
    private String body;
    private String businessKey;
    private String detail;
    private String initiator;
    private String nonceStr;
    private String outTradeNo;
    private String payPassWord;
    private String recipient;
    private String remarks;
    private String totalFee;
    private String tradeType;
    private String version;

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String recipient2) {
        this.recipient = recipient2;
    }

    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail2) {
        this.detail = detail2;
    }

    public String getAttach() {
        return this.attach;
    }

    public void setAttach(String attach2) {
        this.attach = attach2;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks2) {
        this.remarks = remarks2;
    }

    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo2) {
        this.outTradeNo = outTradeNo2;
    }

    public String getNonceStr() {
        return this.nonceStr;
    }

    public void setNonceStr(String nonceStr2) {
        this.nonceStr = nonceStr2;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body2) {
        this.body = body2;
    }

    public String getTotalFee() {
        return this.totalFee;
    }

    public void setTotalFee(String totalFee2) {
        this.totalFee = totalFee2;
    }

    public String getTradeType() {
        return this.tradeType;
    }

    public void setTradeType(String tradeType2) {
        this.tradeType = tradeType2;
    }

    public String getInitiator() {
        return this.initiator;
    }

    public void setInitiator(String initiator2) {
        this.initiator = initiator2;
    }

    public String getPayPassWord() {
        return this.payPassWord;
    }

    public void setPayPassWord(String payPassWord2) {
        this.payPassWord = payPassWord2;
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
