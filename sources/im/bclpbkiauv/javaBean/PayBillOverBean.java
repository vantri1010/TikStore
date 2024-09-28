package im.bclpbkiauv.javaBean;

import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayBillOverBean {
    public String buy_avg;
    public String coin_code;
    public String deal_amount;
    public String deal_fees;
    public String deal_from;
    public String deal_message;
    public String deal_num;
    public int deal_status;
    public String deal_time;
    public String deal_txId;
    public String deal_type;
    public String deal_with;
    public String end_time;
    public String gain_amount;
    public String mandate_num;
    public String order_id;
    public String pass_time;
    public String payment_time;
    public String sell_avg;

    public String getCreate24HDealTimeFormat() {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (TextUtils.isEmpty(this.deal_time)) {
            str = new Date().getTime() + "";
        } else {
            str = this.deal_time;
        }
        return sdf.format(new Date(((long) Integer.parseInt(str)) * 1000));
    }

    public String getCreate24HEndTimeFormat() {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (TextUtils.isEmpty(this.end_time)) {
            str = new Date().getTime() + "";
        } else {
            str = this.end_time;
        }
        return sdf.format(new Date(((long) Integer.parseInt(str)) * 1000));
    }

    public String getCreate12HTimeFormat() {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("a h:mm");
        if (TextUtils.isEmpty(this.deal_time)) {
            str = new Date().getTime() + "";
        } else {
            str = this.deal_time;
        }
        return sdf.format(new Date(((long) Integer.parseInt(str)) * 1000));
    }

    public String getCreate12HEndTimeFormat() {
        String str;
        SimpleDateFormat sdf = new SimpleDateFormat("a h:mm");
        if (TextUtils.isEmpty(this.end_time)) {
            str = new Date().getTime() + "";
        } else {
            str = this.end_time;
        }
        return sdf.format(new Date(((long) Integer.parseInt(str)) * 1000));
    }
}
