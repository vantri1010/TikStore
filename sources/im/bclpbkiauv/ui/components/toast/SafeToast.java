package im.bclpbkiauv.ui.components.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;
import java.lang.reflect.Field;

public final class SafeToast extends BaseToast {
    public SafeToast(Context context) {
        super(context);
        try {
            Field mTNField = Toast.class.getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(this);
            Field mHandlerField = mTNField.getType().getDeclaredField("mHandler");
            mHandlerField.setAccessible(true);
            mHandlerField.set(mTN, new SafeHandler((Handler) mHandlerField.get(mTN)));
        } catch (Exception e) {
        }
    }

    private static final class SafeHandler extends Handler {
        private Handler mHandler;

        private SafeHandler(Handler handler) {
            this.mHandler = handler;
        }

        public void handleMessage(Message msg) {
            try {
                this.mHandler.handleMessage(msg);
            } catch (WindowManager.BadTokenException e) {
            }
        }
    }
}
