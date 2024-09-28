package im.bclpbkiauv.ui.utils.timer;

import android.os.CountDownTimer;
import com.blankj.utilcode.util.TimeUtils;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.king.zxing.util.LogUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import java.net.URL;
import java.net.URLConnection;

public class OrderCountDownHelper {
    private OrderCountDownCallBack callBack;
    private long mCompareTimeMills;
    private String mCompareTimeStr;
    private long mOrderCreateTimeMills;
    private String mOrderCreateTimeStr;
    private OrderCountDownTimer mTimer;
    private int termsMin;

    public interface OrderCountDownCallBack {
        void onTick(boolean z, String str);
    }

    public void start() {
        String str = this.mOrderCreateTimeStr;
        if (str != null) {
            this.mOrderCreateTimeMills = TimeUtils.string2Millis(str);
        }
        if (this.mOrderCreateTimeMills != 0) {
            String str2 = this.mCompareTimeStr;
            if (str2 != null) {
                this.mCompareTimeMills = TimeUtils.string2Millis(str2);
            }
            if (this.mCompareTimeStr == null && this.mCompareTimeMills == 0) {
                this.mCompareTimeMills = getNetTime();
            }
            long j = this.mCompareTimeMills;
            if (j != 0) {
                long cutSpan = ((long) ((this.termsMin * 60) * 1000)) - Math.abs(this.mOrderCreateTimeMills - j);
                if (cutSpan <= 0) {
                    OrderCountDownCallBack orderCountDownCallBack = this.callBack;
                    if (orderCountDownCallBack != null) {
                        orderCountDownCallBack.onTick(true, "00:00:00");
                        return;
                    }
                    return;
                }
                cancel();
                OrderCountDownTimer orderCountDownTimer = new OrderCountDownTimer(cutSpan, 1000);
                this.mTimer = orderCountDownTimer;
                orderCountDownTimer.setCallBack(this.callBack);
                this.mTimer.start();
            }
        }
    }

    public void cancel() {
        OrderCountDownTimer orderCountDownTimer = this.mTimer;
        if (orderCountDownTimer != null && orderCountDownTimer.isRunning) {
            this.mTimer.cancel();
            this.mTimer = null;
        }
    }

    public void restart() {
        this.mCompareTimeMills = getNetTime();
        start();
    }

    public void destroy() {
        cancel();
        this.callBack = null;
    }

    public OrderCountDownTimer getTimer() {
        return this.mTimer;
    }

    public boolean isRunning() {
        OrderCountDownTimer orderCountDownTimer = this.mTimer;
        return orderCountDownTimer != null && orderCountDownTimer.isRunning;
    }

    public static long getNetTime() {
        try {
            URLConnection uc = new URL("http://www.ntsc.ac.cn").openConnection();
            uc.setReadTimeout(5000);
            uc.setConnectTimeout(5000);
            uc.connect();
            return uc.getDate();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    public OrderCountDownHelper setOrderCreateTimeStr(String orderCreateTimeStr) {
        this.mOrderCreateTimeStr = orderCreateTimeStr;
        return this;
    }

    public OrderCountDownHelper setOrderCreateTimeMills(long orderCreateTimeMills) {
        this.mOrderCreateTimeMills = orderCreateTimeMills;
        return this;
    }

    public OrderCountDownHelper setTermsMin(int termsMin2) {
        this.termsMin = termsMin2;
        return this;
    }

    public OrderCountDownHelper setCompareTimeStr(String compareTimeStr) {
        this.mCompareTimeStr = compareTimeStr;
        return this;
    }

    public OrderCountDownHelper setCompareTimeMills(long compareTimeMills) {
        this.mCompareTimeMills = compareTimeMills;
        return this;
    }

    public OrderCountDownHelper setCallBack(OrderCountDownCallBack callBack2) {
        this.callBack = callBack2;
        return this;
    }

    public static class OrderCountDownTimer extends CountDownTimer {
        private OrderCountDownCallBack callBack;
        /* access modifiers changed from: private */
        public boolean isRunning;

        public OrderCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            this.isRunning = millisUntilFinished != 0;
            if (millisUntilFinished == 0) {
                OrderCountDownCallBack orderCountDownCallBack = this.callBack;
                if (orderCountDownCallBack != null) {
                    orderCountDownCallBack.onTick(true, "00:00:00");
                }
            } else if (this.callBack != null) {
                long days = millisUntilFinished / 86400000;
                long hours = millisUntilFinished / 3600000;
                long min = millisUntilFinished / DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS;
                long sec = (millisUntilFinished / 1000) % 60;
                StringBuilder builder = new StringBuilder();
                if (days > 0) {
                    builder.append(days);
                    builder.append(LocaleController.getString("TimeUnitOfDay", R.string.TimeUnitOfDay));
                }
                if (hours >= 0 && hours < 10) {
                    builder.append("0");
                }
                builder.append(hours);
                builder.append(LogUtils.COLON);
                if (min >= 0 && min < 10) {
                    builder.append("0");
                }
                builder.append(min);
                builder.append(LogUtils.COLON);
                if (sec >= 0 && sec < 10) {
                    builder.append("0");
                }
                builder.append(sec);
                this.callBack.onTick(false, builder.toString());
            }
        }

        public void onFinish() {
            this.isRunning = false;
            OrderCountDownCallBack orderCountDownCallBack = this.callBack;
            if (orderCountDownCallBack != null) {
                orderCountDownCallBack.onTick(true, "00:00:00");
            }
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public void setCallBack(OrderCountDownCallBack callBack2) {
            this.callBack = callBack2;
        }
    }
}
