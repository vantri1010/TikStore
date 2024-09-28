package im.bclpbkiauv.javaBean.cdnVip;

import com.blankj.utilcode.util.TimeUtils;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import java.util.ArrayList;
import java.util.List;

public class CdnVipDetailsListBean {
    private List<Item> infoList;

    public List<Item> getInfoList() {
        List<Item> list = this.infoList;
        return list == null ? new ArrayList() : list;
    }

    public static class Item {
        public int BgnTime;
        public int EndTime;
        public int PayAmt;
        public long PayId;

        public boolean cdnVipIsAvailable() {
            return this.EndTime > ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        }

        public String getBgnTimeFormat() {
            return TimeUtils.millis2String(((long) this.BgnTime) * 1000, "YYYY.MM.dd");
        }

        public String getEndTimeFormat() {
            return TimeUtils.millis2String(((long) this.EndTime) * 1000, "YYYY.MM.dd");
        }

        public String getMoney() {
            return "₫" + MoneyUtil.formatToString(Integer.toString(this.PayAmt / 100), 0);
        }
    }
}
