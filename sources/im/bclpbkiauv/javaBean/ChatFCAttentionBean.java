package im.bclpbkiauv.javaBean;

import android.text.TextUtils;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatFCAttentionBean {
    public MsgDataBean interact_msg;
    public String msg_button_text;
    public int msg_time;
    public String msg_title;
    public String msg_url;

    public static class MsgDataBean {
        public long forum_id;
        public String forum_text;
        public int forum_type;
        public int is_followed;
        public String msg_content;
        public long msg_id;
        public int msg_time;
        public int with_id;
    }

    public static class PlanBodyBean {
        public String content;
        public String xStringOne;
    }

    public static class planDetailBean {
        public int expertType;
        public int planId;
        public int threadId;
        public int userId;
    }

    public String getTime() {
        return getFriendlyTimeSpanByNow(((long) this.interact_msg.msg_time) * 1000);
    }

    public static String getFriendlyTimeSpanByNow(long millis) {
        long span = new Date().getTime() - millis;
        if (span < 0) {
            return String.format("%tc", new Object[]{Long.valueOf(millis)});
        } else if (span < DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS) {
            return "刚刚";
        } else {
            if (span < 3600000) {
                return String.format(Locale.getDefault(), "%d分钟前", new Object[]{Long.valueOf(span / DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS)});
            } else if (span < 86400000) {
                return String.format(Locale.getDefault(), "%d小时前", new Object[]{Long.valueOf(span / 3600000)});
            } else {
                int year = Calendar.getInstance().get(1);
                int i = Integer.parseInt(new SimpleDateFormat("yyyy").format(Long.valueOf(millis)));
                long wee = getWeeOfToday();
                long j = wee - 172800000;
                if (millis >= wee - 86400000) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    return "昨天  " + sdf.format(Long.valueOf(millis));
                } else if (millis > wee - 172800000 || year != i) {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Long.valueOf(millis));
                } else {
                    return new SimpleDateFormat("MM-dd HH:mm").format(Long.valueOf(millis));
                }
            }
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(11, 0);
        cal.set(13, 0);
        cal.set(12, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public String getCreate24HEndTimeFormat() {
        StringBuilder sb;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (TextUtils.isEmpty(this.msg_time + "")) {
            sb = new StringBuilder();
            sb.append(new Date().getTime());
        } else {
            sb = new StringBuilder();
            sb.append(this.msg_time);
        }
        sb.append("");
        return sdf.format(new Date(((long) Integer.parseInt(sb.toString())) * 1000));
    }
}
