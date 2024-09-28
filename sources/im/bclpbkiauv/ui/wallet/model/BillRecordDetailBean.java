package im.bclpbkiauv.ui.wallet.model;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class BillRecordDetailBean {
    private String amount;
    private String balance;
    private String bankInfo;
    private String corderId;
    private String createTime;
    private String dp;
    private String effectUserId;
    private String effectUserName;
    private String groupsName;
    private String groupsNumber;
    private Map<String, Object> infoMap;
    private String institutionCode;
    private String institutionName;
    private String orderId;
    private int orderType;
    private String originalAmount;
    private String payBankCode;
    private String payBankName;
    private String payBankNumber;
    private int payMode;
    private String recipientBankCode;
    private String recipientBankName;
    private String recipientBankNumber;
    private String refundType;
    private String remarks;
    private String serviceCharge;
    private int status;
    private String subInstitutionCode;
    private String subInstitutionName;
    private String updateTime;

    public String getSubInstitutionName() {
        return this.subInstitutionName;
    }

    public void setSubInstitutionName(String subInstitutionName2) {
        this.subInstitutionName = subInstitutionName2;
    }

    public String getSubInstitutionCode() {
        return this.subInstitutionCode;
    }

    public void setSubInstitutionCode(String subInstitutionCode2) {
        this.subInstitutionCode = subInstitutionCode2;
    }

    public String getRefundType() {
        return this.refundType;
    }

    public void setRefundType(String refundType2) {
        this.refundType = refundType2;
    }

    public int getRefundTypeInt() {
        String str = this.refundType;
        if (str != null && !TextUtils.isEmpty(str)) {
            return Integer.parseInt(this.refundType);
        }
        return 1;
    }

    public String getBankInfo() {
        return this.bankInfo;
    }

    public void setBankInfo(String bankInfo2) {
        this.bankInfo = bankInfo2;
    }

    public Map<String, Object> getInfoMap() {
        if (this.infoMap == null && !TextUtils.isEmpty(getBankInfo())) {
            try {
                this.infoMap = (Map) JSON.parseObject(getBankInfo(), LinkedHashMap.class);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
        Map<String, Object> map = this.infoMap;
        return map == null ? new HashMap() : map;
    }

    public Object getCardNumber() {
        Object va;
        Iterator<Map.Entry<String, Object>> it = getInfoMap().entrySet().iterator();
        if (!it.hasNext() || (va = it.next().getValue()) == null) {
            return "";
        }
        return va;
    }

    public String getShortCardNumber() {
        String card = getCardNumber() + "";
        if (TextUtils.isEmpty(card)) {
            return "";
        }
        if (card.length() > 4) {
            return card.substring(card.length() - 4);
        }
        return card;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount2) {
        this.amount = amount2;
    }

    public String getServiceCharge() {
        return this.serviceCharge;
    }

    public void setServiceCharge(String serviceCharge2) {
        this.serviceCharge = serviceCharge2;
    }

    public String getOriginalAmount() {
        return this.originalAmount;
    }

    public void setOriginalAmount(String originalAmount2) {
        this.originalAmount = originalAmount2;
    }

    public String getBalance() {
        return this.balance;
    }

    public void setBalance(String balance2) {
        this.balance = balance2;
    }

    public String getDp() {
        return this.dp;
    }

    public void setDp(String dp2) {
        this.dp = dp2;
    }

    public int getOrderType() {
        return this.orderType;
    }

    public void setOrderType(int orderType2) {
        this.orderType = orderType2;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public int getPayMode() {
        return this.payMode;
    }

    public void setPayMode(int payMode2) {
        this.payMode = payMode2;
    }

    public String getEffectUserId() {
        return this.effectUserId;
    }

    public void setEffectUserId(String effectUserId2) {
        this.effectUserId = effectUserId2;
    }

    public String getEffectUserName() {
        return this.effectUserName;
    }

    public void setEffectUserName(String effectUserName2) {
        this.effectUserName = effectUserName2;
    }

    public String getGroupsNumber() {
        return this.groupsNumber;
    }

    public void setGroupsNumber(String groupsNumber2) {
        this.groupsNumber = groupsNumber2;
    }

    public String getGroupsName() {
        return this.groupsName;
    }

    public void setGroupsName(String groupsName2) {
        this.groupsName = groupsName2;
    }

    public String getPayBankNumber() {
        return this.payBankNumber;
    }

    public void setPayBankNumber(String payBankNumber2) {
        this.payBankNumber = payBankNumber2;
    }

    public String getPayBankCode() {
        return this.payBankCode;
    }

    public void setPayBankCode(String payBankCode2) {
        this.payBankCode = payBankCode2;
    }

    public String getPayBankName() {
        return this.payBankName;
    }

    public void setPayBankName(String payBankName2) {
        this.payBankName = payBankName2;
    }

    public String getRecipientBankNumber() {
        return this.recipientBankNumber;
    }

    public String getSortBankNumber() {
        String str = this.recipientBankNumber;
        if (str == null) {
            return "";
        }
        if (str.length() <= 4) {
            return this.recipientBankNumber;
        }
        String str2 = this.recipientBankNumber;
        return str2.substring(str2.length() - 4);
    }

    public void setRecipientBankNumber(String recipientBankNumber2) {
        this.recipientBankNumber = recipientBankNumber2;
    }

    public String getRecipientBankCode() {
        return this.recipientBankCode;
    }

    public void setRecipientBankCode(String recipientBankCode2) {
        this.recipientBankCode = recipientBankCode2;
    }

    public String getRecipientBankName() {
        return this.recipientBankName;
    }

    public void setRecipientBankName(String recipientBankName2) {
        this.recipientBankName = recipientBankName2;
    }

    public String getInstitutionCode() {
        return this.institutionCode;
    }

    public void setInstitutionCode(String institutionCode2) {
        this.institutionCode = institutionCode2;
    }

    public String getInstitutionName() {
        return this.institutionName;
    }

    public void setInstitutionName(String institutionName2) {
        this.institutionName = institutionName2;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks2) {
        this.remarks = remarks2;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime2) {
        this.updateTime = updateTime2;
    }

    public String getCorderId() {
        return this.corderId;
    }

    public void setCorderId(String corderId2) {
        this.corderId = corderId2;
    }

    public boolean isGroupRedPacketRefund() {
        if (this.orderType == 12) {
            return !TextUtils.isEmpty(this.groupsNumber);
        }
        return false;
    }

    public boolean isRedPacketRefund() {
        return this.orderType == 12;
    }

    public boolean isPersonalRedPacketRefund() {
        if (this.orderType != 12) {
            return false;
        }
        return TextUtils.isEmpty(this.groupsNumber);
    }

    public boolean isPartialRefund() {
        if (!TextUtils.isEmpty(this.amount) && !TextUtils.isEmpty(this.originalAmount)) {
            return !this.amount.equals(this.originalAmount);
        }
        return false;
    }

    public String getTypeStr() {
        int i = this.orderType;
        if (i == 0) {
            return LocaleController.getString(R.string.redpacket_go_recharge);
        }
        if (i == 1) {
            return LocaleController.getString(R.string.Withdrawal);
        }
        if (i == 3) {
            return LocaleController.getString(R.string.WithdrawalFailureRefund);
        }
        switch (i) {
            case 5:
                return "转账-进账";
            case 6:
                return "转账-支付";
            case 7:
                return "转账-退款";
            case 8:
                return "红包-领取";
            case 9:
                return "个人-红包支付";
            case 10:
                return "群-红包支付";
            case 11:
                return "群个人-红包支付";
            case 12:
                return "红包过期退款";
            case 13:
                return "平台上账";
            default:
                switch (i) {
                    case 19:
                        return "扫码转账->进账";
                    case 20:
                        return "扫码转账->支付";
                    case 21:
                        return "UChat团队";
                    case 22:
                        return "商户交易-收款";
                    case 23:
                        return "商户交易-付款";
                    case 24:
                        return "商户交易-退款";
                    default:
                        return LocaleController.getString(R.string.UnKnown);
                }
        }
    }

    public String getTypePrefix() {
        int i = this.orderType;
        if (i == 0) {
            return LocaleController.getString(R.string.redpacket_go_recharge);
        }
        if (i == 1 || i == 3) {
            return LocaleController.getString(R.string.Withdrawal);
        }
        switch (i) {
            case 5:
            case 6:
            case 7:
                return LocaleController.getString(R.string.Transfer);
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return LocaleController.getString(R.string.RedPacket);
            case 13:
                return "平台上账";
            default:
                switch (i) {
                    case 19:
                        return "扫码转账->进账";
                    case 20:
                        return "扫码转账->支付";
                    case 21:
                        return "UChat团队";
                    case 22:
                        return "商户交易-收款";
                    case 23:
                        return "商户交易-付款";
                    case 24:
                        return "商户交易-退款";
                    default:
                        return LocaleController.getString(R.string.UnKnown);
                }
        }
    }

    public int getTypeIcon() {
        int i = this.orderType;
        if (i == 1 || i == 20 || i == 23) {
            return R.mipmap.ic_wallet_withdraw;
        }
        switch (i) {
            case 6:
            case 7:
            case 8:
                return R.mipmap.ic_wallet_transfer;
            case 9:
            case 10:
            case 11:
            case 12:
                return R.mipmap.ic_wallet_packet;
            default:
                return R.mipmap.ic_wallet_charge;
        }
    }
}
