package im.bclpbkiauv.ui.hui.packet.bean;

import android.text.TextUtils;
import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.utils.number.NumberUtil;

public class RedpacketBean {
    private String cancelTime;
    private String completeTime;
    private String createTime;
    private int fixedAmount;
    private String grantType;
    private String groups;
    private String groupsName;
    private String initiatorUserId;
    private int isReceive;
    private int money;
    private int number;
    private int optimumTotalFee;
    private int optimumUserId;
    private String recipientUserId;
    private String redType;
    private String remarks;
    private String serialCode;
    private String status;
    private int surplusAmount;
    private int surplusNumber;
    private String totalFee;

    public String getCompleteTime() {
        return this.completeTime;
    }

    public int getNumber() {
        return this.number;
    }

    public int getSurplusNumber() {
        return this.surplusNumber;
    }

    public int getSurplusAmount() {
        return this.surplusAmount;
    }

    public int getMoney() {
        return this.money;
    }

    public int getIsReceived() {
        return this.isReceive;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getSerialCode() {
        return this.serialCode;
    }

    public String getInitiatorUserId() {
        return this.initiatorUserId;
    }

    public int getInitiatorUserIdInt() {
        if (NumberUtil.isNumber(this.initiatorUserId)) {
            return Integer.parseInt(this.initiatorUserId);
        }
        return -1;
    }

    public String getRecipientUserId() {
        return this.recipientUserId;
    }

    public int getRecipientUserIdInt() {
        if (TextUtils.isEmpty(this.recipientUserId) || !NumberUtil.isNumber(this.recipientUserId)) {
            return -1;
        }
        return Integer.parseInt(this.recipientUserId);
    }

    public String getGroupsName() {
        return this.groupsName;
    }

    public String getTotalFee() {
        return this.totalFee;
    }

    public String getRedType() {
        return this.redType;
    }

    public String getGrantType() {
        return this.grantType;
    }

    public int getRedTypeInt() {
        if (TextUtils.isEmpty(this.redType) || !NumberUtil.isNumber(this.redType)) {
            return -1;
        }
        return Integer.parseInt(this.redType);
    }

    public int getGrantTypeInt() {
        if (TextUtils.isEmpty(this.grantType) || !NumberUtil.isNumber(this.grantType)) {
            return -1;
        }
        return Integer.parseInt(this.grantType);
    }

    public String getStatus() {
        return this.status;
    }

    public int getStatusInt() {
        if (NumberUtil.isNumber(this.status)) {
            return Integer.parseInt(this.status);
        }
        return 0;
    }

    public String getCancelTimeFormat() {
        String str = this.cancelTime;
        if (str != null && TextUtils.isDigitsOnly(str) && !"0".equals(this.cancelTime)) {
            return TimeUtils.millis2String(Long.parseLong(this.cancelTime), LocaleController.getString("formatterStandard24H", R.string.formatterStandard24H));
        }
        if (this.cancelTime == null) {
            return "";
        }
        return this.cancelTime + "";
    }

    public String getCreateTimeFormat() {
        String str = this.createTime;
        if (str != null && TextUtils.isDigitsOnly(str) && !"0".equals(this.createTime)) {
            return TimeUtils.millis2String(Long.parseLong(this.createTime), LocaleController.getString("formatterStandard24H", R.string.formatterStandard24H));
        }
        if (this.createTime == null) {
            return "";
        }
        return this.createTime + "";
    }

    public String getCompleteTimeFormat() {
        String str = this.completeTime;
        if (str != null && TextUtils.isDigitsOnly(str) && !"0".equals(this.completeTime)) {
            return TimeUtils.millis2String(Long.parseLong(this.completeTime), LocaleController.getString("formatterStandard24H", R.string.formatterMonthDayTime24H));
        }
        if (this.createTime == null) {
            return "";
        }
        return this.completeTime + "";
    }

    public String getDuration() {
        long endTime;
        long startTime;
        if (TextUtils.isEmpty(this.completeTime)) {
            endTime = 0;
        } else {
            endTime = Long.parseLong(this.completeTime);
        }
        if (TextUtils.isEmpty(this.createTime)) {
            startTime = 0;
        } else {
            startTime = Long.parseLong(this.createTime);
        }
        long duration = (endTime - startTime) / 1000;
        if (duration == 0) {
            return LocaleController.getString("HadBeenAlreadyReceived", R.string.HadBeenAlreadyReceived);
        }
        if (duration <= 60) {
            return String.format(LocaleController.getString(duration == 1 ? R.string.RobbedInSecondFromat : R.string.RobbedInSecondFromat2), new Object[]{Long.valueOf(duration)});
        } else if (duration < 3600) {
            int min = (int) (duration / 60);
            int sec = (int) (duration % 60);
            if (min == 1 && sec == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInMinSecFormat), new Object[]{Integer.valueOf(min), Integer.valueOf(sec)});
            } else if (min == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInMinSecFormat2), new Object[]{Integer.valueOf(min), Integer.valueOf(sec)});
            } else if (sec == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInMinSecFormat3), new Object[]{Integer.valueOf(min), Integer.valueOf(sec)});
            } else {
                return String.format(LocaleController.getString(R.string.RobbedInMinSecFormat4), new Object[]{Integer.valueOf(min), Integer.valueOf(sec)});
            }
        } else if (duration >= 86400) {
            return LocaleController.getString("HadBeenAlreadyReceived", R.string.HadBeenAlreadyReceived);
        } else {
            int hour = (int) (duration / 3600);
            int exmil = (int) (duration % 3600);
            int min2 = exmil / 60;
            int sec2 = exmil % 60;
            if (hour == 1) {
                if (min2 == 1 && sec2 == 1) {
                    return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
                } else if (min2 == 1) {
                    return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat2), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
                } else if (sec2 == 1) {
                    return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat3), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
                } else {
                    return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat4), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
                }
            } else if (min2 == 1 && sec2 == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat5), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
            } else if (min2 == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat6), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
            } else if (sec2 == 1) {
                return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat7), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
            } else {
                return String.format(LocaleController.getString(R.string.RobbedInHourMinSecFormat8), new Object[]{Integer.valueOf(hour), Integer.valueOf(min2), Integer.valueOf(sec2)});
            }
        }
    }
}
