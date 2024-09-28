package im.bclpbkiauv.messenger.voip;

import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import org.json.JSONException;
import org.json.JSONObject;

public class VoIPServerConfig {
    private static JSONObject config = new JSONObject();

    private static native void nativeSetConfig(String str);

    public static void setConfig(String json) {
        try {
            config = new JSONObject(json);
            nativeSetConfig(json);
        } catch (JSONException x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error parsing VoIP config", (Throwable) x);
            }
        }
    }

    public static int getInt(String key, int fallback) {
        return config.optInt(key, fallback);
    }

    public static double getDouble(String key, double fallback) {
        return config.optDouble(key, fallback);
    }

    public static String getString(String key, String fallback) {
        return config.optString(key, fallback);
    }

    public static boolean getBoolean(String key, boolean fallback) {
        return config.optBoolean(key, fallback);
    }
}
