package com.baidu.mapsdkvi;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

public class VMsg {
    private static final String a = VMsg.class.getSimpleName();
    private static Handler b;
    private static HandlerThread c;
    private static VMsg d = new VMsg();

    static class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            VMsg.OnUserCommand1(message.what, message.arg1, message.arg2, message.obj == null ? 0 : ((Long) message.obj).longValue());
        }
    }

    public static native void InitClass(Object obj);

    /* access modifiers changed from: private */
    public static native void OnUserCommand1(int i, int i2, int i3, long j);

    public static void destroy() {
        c.quit();
        c = null;
        b.removeCallbacksAndMessages((Object) null);
        b = null;
    }

    public static VMsg getInstance() {
        return d;
    }

    public static void init() {
        HandlerThread handlerThread = new HandlerThread("VIMsgThread");
        c = handlerThread;
        handlerThread.start();
        b = new a(c.getLooper());
    }

    private static void postMessage(int i, int i2, int i3, long j) {
        Handler handler = b;
        if (handler != null) {
            Message.obtain(handler, i, i2, i3, j == 0 ? null : Long.valueOf(j)).sendToTarget();
        }
    }
}
