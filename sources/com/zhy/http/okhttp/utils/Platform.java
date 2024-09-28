package com.zhy.http.okhttp.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
        L.e(PLATFORM.getClass().toString());
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException e) {
        }
        return new Platform();
    }

    public Executor defaultCallbackExecutor() {
        return Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        defaultCallbackExecutor().execute(runnable);
    }

    static class Android extends Platform {
        Android() {
        }

        public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            MainThreadExecutor() {
            }

            public void execute(Runnable r) {
                this.handler.post(r);
            }
        }
    }
}
