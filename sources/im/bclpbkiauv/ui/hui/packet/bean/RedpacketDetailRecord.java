package im.bclpbkiauv.ui.hui.packet.bean;

import android.text.TextUtils;
import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.utils.number.NumberUtil;

public class RedpacketDetailRecord {
    private String createTime;
    private String isOptimum;
    private String totalFee;
    private String userId;

    public RedpacketDetailRecord(String createTime2, String totalFee2, String userId2, String isOptimum2) {
        this.createTime = createTime2;
        this.totalFee = totalFee2;
        this.userId = userId2;
        this.isOptimum = isOptimum2;
    }

    public String getIsOptimum() {
        return this.isOptimum;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getTotalFee() {
        return this.totalFee;
    }

    public String getUserId() {
        return this.userId;
    }

    public int getUserIdInt() {
        String str = this.userId;
        if (str == null || !NumberUtil.isNumber(str)) {
            return 0;
        }
        return Integer.parseInt(this.userId);
    }

    public String getCreateTimeFormat() {
        String str = this.createTime;
        if (str != null && TextUtils.isDigitsOnly(str) && !"0".equals(this.createTime)) {
            return TimeUtils.millis2String(Long.parseLong(this.createTime), LocaleController.getString("formatterMonthDayTime24H", R.string.formatterMonthDayTime24H));
        }
        return this.createTime + "";
    }

    public long getCreatTimeLong() {
        String str = this.createTime;
        if (str == null || !TextUtils.isDigitsOnly(str)) {
            return 0;
        }
        return Long.parseLong(this.createTime);
    }
}
