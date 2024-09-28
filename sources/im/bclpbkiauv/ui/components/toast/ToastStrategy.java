package im.bclpbkiauv.ui.components.toast;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ToastStrategy extends Handler {
    private static final int DELAY_TIMEOUT = 300;
    public static final int LONG_DURATION_TIMEOUT = 3500;
    private static final int MAX_TOAST_CAPACITY = 3;
    public static final int SHORT_DURATION_TIMEOUT = 2000;
    private static final int TYPE_CANCEL = 3;
    private static final int TYPE_CONTINUE = 2;
    private static final int TYPE_SHOW = 1;
    private volatile boolean isShow;
    private volatile Queue<CharSequence> mQueue = getToastQueue();
    private Toast mToast;

    public ToastStrategy() {
        super(Looper.getMainLooper());
    }

    public void bind(Toast toast) {
        this.mToast = toast;
    }

    public void show(CharSequence text) {
        if ((this.mQueue.isEmpty() || !this.mQueue.contains(text)) && !this.mQueue.offer(text)) {
            this.mQueue.poll();
            this.mQueue.offer(text);
        }
        if (!this.isShow) {
            this.isShow = true;
            sendEmptyMessageDelayed(1, 300);
        }
    }

    public void cancel() {
        if (this.isShow) {
            this.isShow = false;
            sendEmptyMessage(3);
        }
    }

    public void handleMessage(Message msg) {
        int i = msg.what;
        if (i == 1) {
            CharSequence text = this.mQueue.peek();
            if (text != null) {
                this.mToast.setText(text);
                this.mToast.show();
                sendEmptyMessageDelayed(2, (long) (getToastDuration(text) + DELAY_TIMEOUT));
                return;
            }
            this.isShow = false;
        } else if (i == 2) {
            this.mQueue.poll();
            if (!this.mQueue.isEmpty()) {
                sendEmptyMessage(1);
            } else {
                this.isShow = false;
            }
        } else if (i == 3) {
            this.isShow = false;
            this.mQueue.clear();
            this.mToast.cancel();
        }
    }

    public Queue<CharSequence> getToastQueue() {
        return new ArrayBlockingQueue(3);
    }

    public int getToastDuration(CharSequence text) {
        if (text.length() > 20) {
            return LONG_DURATION_TIMEOUT;
        }
        return 2000;
    }
}
