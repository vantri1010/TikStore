package im.bclpbkiauv.ui.hui.visualcall;

import android.widget.TextView;
import im.bclpbkiauv.ui.hui.visualcall.DynamicPoint;
import java.util.Timer;
import java.util.TimerTask;

public class DynamicPoint {
    /* access modifiers changed from: private */
    public int iCount = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    static /* synthetic */ int access$008(DynamicPoint x0) {
        int i = x0.iCount;
        x0.iCount = i + 1;
        return i;
    }

    public void animForWaitting(final String strText, final TextView textView) {
        this.iCount = 0;
        TimerTask timerTask2 = this.timerTask;
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
        AnonymousClass1 r2 = new TimerTask() {
            public void run() {
                ThreadUtils.runOnUiThread(new Runnable(textView, strText) {
                    private final /* synthetic */ TextView f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        DynamicPoint.AnonymousClass1.this.lambda$run$0$DynamicPoint$1(this.f$1, this.f$2);
                    }
                });
            }

            public /* synthetic */ void lambda$run$0$DynamicPoint$1(TextView textView, String strText) {
                if (DynamicPoint.this.iCount % 4 == 0) {
                    textView.setText(strText);
                } else if (DynamicPoint.this.iCount % 4 == 1) {
                    textView.setText(strText + ".");
                } else if (DynamicPoint.this.iCount % 4 == 2) {
                    textView.setText(strText + "..");
                } else if (DynamicPoint.this.iCount % 4 == 3) {
                    textView.setText(strText + "...");
                }
                DynamicPoint.access$008(DynamicPoint.this);
            }
        };
        this.timerTask = r2;
        this.timer.schedule(r2, 0, 1000);
    }

    public void release() {
        this.timer.cancel();
        this.timer.purge();
    }

    public void cancel() {
        TimerTask timerTask2 = this.timerTask;
        if (timerTask2 != null) {
            timerTask2.cancel();
        }
    }
}
