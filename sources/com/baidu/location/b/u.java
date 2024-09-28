package com.baidu.location.b;

import android.os.HandlerThread;

public class u {
    private static HandlerThread a = null;

    public static synchronized HandlerThread a() {
        HandlerThread handlerThread;
        synchronized (u.class) {
            if (a == null) {
                try {
                    HandlerThread handlerThread2 = new HandlerThread("ServiceStartArguments", 10);
                    a = handlerThread2;
                    handlerThread2.start();
                } catch (Throwable th) {
                    th.printStackTrace();
                    a = null;
                }
            }
            handlerThread = a;
        }
        return handlerThread;
    }
}
