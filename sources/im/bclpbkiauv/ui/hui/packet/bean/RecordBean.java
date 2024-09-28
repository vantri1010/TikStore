package im.bclpbkiauv.ui.hui.packet.bean;

import android.text.TextUtils;
import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;

public class RecordBean {
    private String completeTime;
    private String createTime;
    private String fixedAmount;
    private String grantType;
    private String groups;
    private String groupsName;
    private String initiatorUserId;
    private String isReceive;
    private String receiveTime;
    private String receiveTotalFee;
    private String recipientUserId;
    private String redType;
    private String remarks;
    private String serialCode;
    private String status;
    private String surplusAmount;
    private String surplusNumber;
    private String totalFee;

    public String getReceiveTime() {
        return this.receiveTime;
    }

    public String getReceiveTotalFee() {
        return this.receiveTotalFee;
    }

    public String getSerialCode() {
        return this.serialCode;
    }

    public int getSurplusAmount() {
        if (TextUtils.isEmpty(this.surplusAmount)) {
            return 0;
        }
        return Integer.parseInt(this.surplusAmount);
    }

    public int getTotalFee() {
        if (TextUtils.isEmpty(this.totalFee)) {
            return 0;
        }
        return Integer.parseInt(this.totalFee);
    }

    public long getFixedAmount() {
        if (TextUtils.isEmpty(this.fixedAmount)) {
            return 0;
        }
        return Long.parseLong(this.fixedAmount);
    }

    public int getSurplusNumber() {
        if (TextUtils.isEmpty(this.surplusNumber)) {
            return 0;
        }
        return Integer.parseInt(this.surplusNumber);
    }

    public int getRedType() {
        if (TextUtils.isEmpty(this.redType)) {
            return 0;
        }
        return Integer.parseInt(this.redType);
    }

    public int getGrantType() {
        if (TextUtils.isEmpty(this.grantType)) {
            return 0;
        }
        return Integer.parseInt(this.grantType);
    }

    public int getStatus() {
        if (TextUtils.isEmpty(this.status)) {
            return 0;
        }
        return Integer.parseInt(this.status);
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getInitiatorUserId() {
        return this.initiatorUserId;
    }

    public String getRecipientUserId() {
        return this.recipientUserId;
    }

    public String getCompleteTime() {
        return this.completeTime;
    }

    public String getGroupsName() {
        return this.groupsName;
    }

    public String getGroups() {
        return this.groups;
    }

    public String getCreateTime() {
        String str = this.createTime;
        if (str == null || !TextUtils.isDigitsOnly(str)) {
            return "00-00 00:00";
        }
        return TimeUtils.millis2String(Long.parseLong(this.createTime), LocaleController.getString(R.string.formatterMonthDayTime24H));
    }

    public int getIsReceive() {
        if (TextUtils.isEmpty(this.isReceive)) {
            return 0;
        }
        return Integer.parseInt(this.isReceive);
    }
}
