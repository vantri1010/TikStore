package com.serenegiant.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class BaseHandler extends Handler {
    private static final String TAG = "BaseHandler";

    public static final BaseHandler createHandler() {
        return createHandler("HandlerThreadHandler");
    }

    public static final BaseHandler createHandler(String name) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new BaseHandler(thread.getLooper());
    }

    public static final BaseHandler createHandler(Handler.Callback callback) {
        return createHandler("HandlerThreadHandler", callback);
    }

    public static final BaseHandler createHandler(String name, Handler.Callback callback) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new BaseHandler(thread.getLooper(), callback);
    }

    private BaseHandler(Looper looper) {
        super(looper);
    }

    private BaseHandler(Looper looper, Handler.Callback callback) {
        super(looper, callback);
    }
}
