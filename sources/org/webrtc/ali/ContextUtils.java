package org.webrtc.ali;

import android.content.Context;

public class ContextUtils {
    private static final String TAG = "ContextUtils";
    private static Context applicationContext;

    public static void initialize(Context applicationContext2) {
        if (applicationContext != null) {
            Logging.e(TAG, "Calling ContextUtils.initialize multiple times, this will crash in the future!");
        }
        if (applicationContext2 != null) {
            applicationContext = applicationContext2;
            return;
        }
        throw new RuntimeException("Application context cannot be null for ContextUtils.initialize.");
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }
}
