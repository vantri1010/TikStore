package im.bclpbkiauv.ui.utils.timer;

import android.os.CountDownTimer;

public class RunningFlagCountDownTimer extends CountDownTimer {
    private long currentUntilFinishedMills;
    private boolean isRunning;

    public RunningFlagCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void startInternal() {
        this.isRunning = true;
        start();
    }

    public void cancelInternal() {
        this.isRunning = false;
        try {
            cancel();
        } catch (Exception e) {
        }
    }

    public void onTick(long millisUntilFinished) {
        this.isRunning = true;
        this.currentUntilFinishedMills = millisUntilFinished;
    }

    public void onFinish() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public long getCurrentUntilFinishedMills() {
        return this.currentUntilFinishedMills;
    }
}
