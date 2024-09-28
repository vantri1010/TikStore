package im.bclpbkiauv.ui.hui.transfer.bean;

import android.text.TextUtils;
import androidx.exifinterface.media.ExifInterface;
import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.utils.number.NumberUtil;

public class TransferResponse {
    private String cancelTime;
    private String collectTime;
    private String createTime;
    private String initiatorUserId;
    private String recipientUserId;
    private String remarks;
    private String serialCode;
    private String status;
    private String totalFee;

    public enum Status {
        NONE,
        WAITING,
        RECEIVED,
        CANCEL,
        REFUSED,
        TIMEOUT
    }

    public String getSerialCode() {
        return this.serialCode;
    }

    public String getTotalFee() {
        return this.totalFee;
    }

    public String getStatus() {
        return this.status;
    }

    public String getInitiatorUserId() {
        return this.initiatorUserId;
    }

    public String getRecipientUserId() {
        return this.recipientUserId;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getCollectTime() {
        return this.collectTime;
    }

    public String getCancelTime() {
        return this.cancelTime;
    }

    public int getInitiatorUserIdInt() {
        if (NumberUtil.isNumber(this.initiatorUserId)) {
            return Integer.parseInt(this.initiatorUserId);
        }
        return -1;
    }

    public String getCollectTimeFormat() {
        String str = this.collectTime;
        if (str != null && TextUtils.isDigitsOnly(str) && !"0".equals(this.collectTime)) {
            return TimeUtils.millis2String(Long.parseLong(this.collectTime), LocaleController.getString("formatterStandard24H", R.string.formatterStandard24H));
        }
        return this.collectTime + "";
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

    public Status getState() {
        if (TextUtils.isEmpty(this.status)) {
            return Status.NONE;
        }
        String str = this.status;
        char c = 65535;
        switch (str.hashCode()) {
            case 49:
                if (str.equals("1")) {
                    c = 0;
                    break;
                }
                break;
            case 50:
                if (str.equals("2")) {
                    c = 1;
                    break;
                }
                break;
            case 51:
                if (str.equals(ExifInterface.GPS_MEASUREMENT_3D)) {
                    c = 2;
                    break;
                }
                break;
            case 52:
                if (str.equals("4")) {
                    c = 3;
                    break;
                }
                break;
            case 53:
                if (str.equals("5")) {
                    c = 4;
                    break;
                }
                break;
        }
        if (c == 0) {
            return Status.WAITING;
        }
        if (c == 1) {
            return Status.RECEIVED;
        }
        if (c == 2) {
            return Status.CANCEL;
        }
        if (c == 3) {
            return Status.REFUSED;
        }
        if (c != 4) {
            return Status.NONE;
        }
        return Status.TIMEOUT;
    }
}
