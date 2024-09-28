package com.king.zxing;

public interface CaptureLifecycle {
    void onCreate();

    void onDestroy();

    void onPause();

    void onResume();
}
