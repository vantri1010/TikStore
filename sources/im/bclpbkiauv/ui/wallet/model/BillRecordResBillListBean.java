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

public class BillRecordResBillListBean {
    private int amount;
    private int balance;
    private String bankInfo;
    private String createTime;
    private String dp;
    private String effectUserId;
    private String effectUserName;
    private String groupsName;
    private String groupsNumber;
    private int id;
    private Map<String, Object> infoMap;
    private String institutionCode;
    private String institutionName;
    private String orderId;
    private int orderType;
    private int payMode;
    private String recipientBankNumber;
    private String refundAmount;
    private String refundType;
    private int serviceCharge;
    private int status;
    private String subInstitutionCode;
    private String subInstitutionName;
    private String updateTime;

    public String getBankInfo() {
        return this.bankInfo;
    }

    public void setBankInfo(String bankInfo2) {
        this.bankInfo = bankInfo2;
    }

    public String getRefundType() {
        return this.refundType;
    }

    public void setRefundType(String refundType2) {
        this.refundType = refundType2;
    }

    public String getRefundAmount() {
        return this.refundAmount;
    }

    public void setRefundAmount(String refundAmount2) {
        this.refundAmount = refundAmount2;
    }

    public String getGroupsNumber() {
        return this.groupsNumber;
    }

    public void setGroupsNumber(String groupsNumber2) {
        this.groupsNumber = groupsNumber2;
    }

    public int getOrderType() {
        return this.orderType;
    }

    public void setOrderType(int orderType2) {
        this.orderType = orderType2;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount2) {
        this.amount = amount2;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId2) {
        this.orderId = orderId2;
    }

    public int getPayMode() {
        return this.payMode;
    }

    public void setPayMode(int payMode2) {
        this.payMode = payMode2;
    }

    public String getGroupsName() {
        return this.groupsName;
    }

    public void setGroupsName(String groupsName2) {
        this.groupsName = groupsName2;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime2) {
        this.updateTime = updateTime2;
    }

    public String getDp() {
        return this.dp;
    }

    public void setDp(String dp2) {
        this.dp = dp2;
    }

    public int getServiceCharge() {
        return this.serviceCharge;
    }

    public void setServiceCharge(int serviceCharge2) {
        this.serviceCharge = serviceCharge2;
    }

    public String getEffectUserName() {
        return this.effectUserName;
    }

    public void setEffectUserName(String effectUserName2) {
        this.effectUserName = effectUserName2;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(int balance2) {
        this.balance = balance2;
    }

    public String getInstitutionCode() {
        return this.institutionCode;
    }

    public void setInstitutionCode(String institutionCode2) {
        this.institutionCode = institutionCode2;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime2) {
        this.createTime = createTime2;
    }

    public String getInstitutionName() {
        return this.institutionName;
    }

    public void setInstitutionName(String institutionName2) {
        this.institutionName = institutionName2;
    }

    public String getEffectUserId() {
        return this.effectUserId;
    }

    public void setEffectUserId(String effectUserId2) {
        this.effectUserId = effectUserId2;
    }

    public String getRecipientBankNumber() {
        return this.recipientBankNumber;
    }

    public void setRecipientBankNumber(String recipientBankNumber2) {
        this.recipientBankNumber = recipientBankNumber2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
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
                return LocaleController.getString(R.string.TransferIncoming);
            case 6:
                return LocaleController.getString(R.string.TransferPay);
            case 7:
                return LocaleController.getString(R.string.TransferRefund2);
            case 8:
                return LocaleController.getString(R.string.RedPacketReceive);
            case 9:
                return LocaleController.getString(R.string.PersonalRedPacketPayment);
            case 10:
                return LocaleController.getString(R.string.GrouplRedPacketPayment);
            case 11:
                return LocaleController.getString(R.string.GroupOfIndividualsRedPacketPayment);
            case 12:
                return LocaleController.getString(R.string.RedPacketExpiredRefund);
            case 13:
                return LocaleController.getString(R.string.PlatformAccount);
            default:
                switch (i) {
                    case 19:
                        return LocaleController.getString(R.string.ScanCodeTransferCredit);
                    case 20:
                        return LocaleController.getString(R.string.ScanCodeTransferPayment);
                    case 21:
                        return LocaleController.getString(R.string.BackstageAccount);
                    case 22:
                        return LocaleController.getString(R.string.MerchantTransactionCollection);
                    case 23:
                        return LocaleController.getString(R.string.MerchantTransactionPayment);
                    case 24:
                        return LocaleController.getString(R.string.MerchantTransactionRefund);
                    case 25:
                        return LocaleController.getString(R.string.BackOfficeAccount);
                    default:
                        return LocaleController.getString(R.string.UnKnown);
                }
        }
    }

    public int getTypeIcon() {
        int i = this.orderType;
        if (i == 0) {
            int i2 = this.status;
            if (i2 == 0 || i2 == 1) {
                return R.mipmap.ic_top_up_success;
            }
            return R.mipmap.ic_top_up_failed;
        } else if (i == 1) {
            return R.mipmap.ic_trade_withdrawal;
        } else {
            if (i == 3) {
                return R.mipmap.ic_transfer_refund;
            }
            if (i == 21) {
                return R.mipmap.ic_back_top_up;
            }
            switch (i) {
                case 5:
                case 6:
                case 7:
                    return R.mipmap.ic_bill_detail_trasfer;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                    return R.mipmap.ic_bill_detail_packet;
                case 13:
                    return R.mipmap.ic_back_top_up;
                default:
                    switch (i) {
                        case 25:
                            return R.mipmap.ic_back_top_up;
                        case 26:
                        case 27:
                            return R.mipmap.ic_order_live;
                        default:
                            return R.mipmap.transfer_success_icon;
                    }
            }
        }
    }
}
