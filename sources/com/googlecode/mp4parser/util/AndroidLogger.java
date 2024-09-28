package com.googlecode.mp4parser.util;

import android.util.Log;
import com.king.zxing.util.LogUtils;

public class AndroidLogger extends Logger {
    private static final String TAG = "isoparser";
    String name;

    public AndroidLogger(String name2) {
        this.name = name2;
    }

    public void logDebug(String message) {
        Log.d(TAG, String.valueOf(this.name) + LogUtils.COLON + message);
    }

    public void logWarn(String message) {
        Log.w(TAG, String.valueOf(this.name) + LogUtils.COLON + message);
    }

    public void logError(String message) {
        Log.e(TAG, String.valueOf(this.name) + LogUtils.COLON + message);
    }
}
