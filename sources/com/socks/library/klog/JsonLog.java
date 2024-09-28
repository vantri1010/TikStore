package com.socks.library.klog;

import android.util.Log;
import com.socks.library.KLog;
import com.socks.library.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonLog {
    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                message = new JSONObject(msg).toString(4);
            } else if (msg.startsWith("[")) {
                message = new JSONArray(msg).toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        Util.printLine(tag, true);
        for (String line : (headString + KLog.LINE_SEPARATOR + message).split(KLog.LINE_SEPARATOR)) {
            Log.d(tag, "║ " + line);
        }
        Util.printLine(tag, false);
    }
}
