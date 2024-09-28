package com.blankj.utilcode.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {
    private static final byte TYPE_BOOLEAN = 0;
    private static final byte TYPE_DOUBLE = 3;
    private static final byte TYPE_INT = 1;
    private static final byte TYPE_JSON_ARRAY = 6;
    private static final byte TYPE_JSON_OBJECT = 5;
    private static final byte TYPE_LONG = 2;
    private static final byte TYPE_STRING = 4;

    private JsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        return getBoolean(jsonObject, key, false);
    }

    public static boolean getBoolean(JSONObject jsonObject, String key, boolean defaultValue) {
        return ((Boolean) getValueByType(jsonObject, key, Boolean.valueOf(defaultValue), (byte) 0)).booleanValue();
    }

    public static boolean getBoolean(String json, String key) {
        return getBoolean(json, key, false);
    }

    public static boolean getBoolean(String json, String key, boolean defaultValue) {
        return ((Boolean) getValueByType(json, key, Boolean.valueOf(defaultValue), (byte) 0)).booleanValue();
    }

    public static int getInt(JSONObject jsonObject, String key) {
        return getInt(jsonObject, key, -1);
    }

    public static int getInt(JSONObject jsonObject, String key, int defaultValue) {
        return ((Integer) getValueByType(jsonObject, key, Integer.valueOf(defaultValue), (byte) TYPE_INT)).intValue();
    }

    public static int getInt(String json, String key) {
        return getInt(json, key, -1);
    }

    public static int getInt(String json, String key, int defaultValue) {
        return ((Integer) getValueByType(json, key, Integer.valueOf(defaultValue), (byte) TYPE_INT)).intValue();
    }

    public static long getLong(JSONObject jsonObject, String key) {
        return getLong(jsonObject, key, -1);
    }

    public static long getLong(JSONObject jsonObject, String key, long defaultValue) {
        return ((Long) getValueByType(jsonObject, key, Long.valueOf(defaultValue), (byte) TYPE_LONG)).longValue();
    }

    public static long getLong(String json, String key) {
        return getLong(json, key, -1);
    }

    public static long getLong(String json, String key, long defaultValue) {
        return ((Long) getValueByType(json, key, Long.valueOf(defaultValue), (byte) TYPE_LONG)).longValue();
    }

    public static double getDouble(JSONObject jsonObject, String key) {
        return getDouble(jsonObject, key, -1.0d);
    }

    public static double getDouble(JSONObject jsonObject, String key, double defaultValue) {
        return ((Double) getValueByType(jsonObject, key, Double.valueOf(defaultValue), (byte) TYPE_DOUBLE)).doubleValue();
    }

    public static double getDouble(String json, String key) {
        return getDouble(json, key, -1.0d);
    }

    public static double getDouble(String json, String key, double defaultValue) {
        return ((Double) getValueByType(json, key, Double.valueOf(defaultValue), (byte) TYPE_DOUBLE)).doubleValue();
    }

    public static String getString(JSONObject jsonObject, String key) {
        return getString(jsonObject, key, "");
    }

    public static String getString(JSONObject jsonObject, String key, String defaultValue) {
        return (String) getValueByType(jsonObject, key, defaultValue, (byte) TYPE_STRING);
    }

    public static String getString(String json, String key) {
        return getString(json, key, "");
    }

    public static String getString(String json, String key, String defaultValue) {
        return (String) getValueByType(json, key, defaultValue, (byte) TYPE_STRING);
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String key, JSONObject defaultValue) {
        return (JSONObject) getValueByType(jsonObject, key, defaultValue, (byte) TYPE_JSON_OBJECT);
    }

    public static JSONObject getJSONObject(String json, String key, JSONObject defaultValue) {
        return (JSONObject) getValueByType(json, key, defaultValue, (byte) TYPE_JSON_OBJECT);
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String key, JSONArray defaultValue) {
        return (JSONArray) getValueByType(jsonObject, key, defaultValue, (byte) TYPE_JSON_ARRAY);
    }

    public static JSONArray getJSONArray(String json, String key, JSONArray defaultValue) {
        return (JSONArray) getValueByType(json, key, defaultValue, (byte) TYPE_JSON_ARRAY);
    }

    private static <T> T getValueByType(JSONObject jsonObject, String key, T defaultValue, byte type) {
        if (jsonObject == null || key == null || key.length() == 0) {
            return defaultValue;
        }
        if (type == 0) {
            try {
                return Boolean.valueOf(jsonObject.getBoolean(key));
            } catch (JSONException e) {
                Log.e("JsonUtils", "getValueByType: ", e);
                return defaultValue;
            }
        } else if (type == 1) {
            return Integer.valueOf(jsonObject.getInt(key));
        } else {
            if (type == 2) {
                return Long.valueOf(jsonObject.getLong(key));
            }
            if (type == 3) {
                return Double.valueOf(jsonObject.getDouble(key));
            }
            if (type == 4) {
                return jsonObject.getString(key);
            }
            if (type == 5) {
                return jsonObject.getJSONObject(key);
            }
            if (type == 6) {
                return jsonObject.getJSONArray(key);
            }
            return defaultValue;
        }
    }

    private static <T> T getValueByType(String json, String key, T defaultValue, byte type) {
        if (json == null || json.length() == 0 || key == null || key.length() == 0) {
            return defaultValue;
        }
        try {
            return getValueByType(new JSONObject(json), key, defaultValue, type);
        } catch (JSONException e) {
            Log.e("JsonUtils", "getValueByType: ", e);
            return defaultValue;
        }
    }

    public static String formatJson(String json) {
        return formatJson(json, 4);
    }

    public static String formatJson(String json, int indentSpaces) {
        try {
            int len = json.length();
            for (int i = 0; i < len; i++) {
                char c = json.charAt(i);
                if (c == '{') {
                    return new JSONObject(json).toString(indentSpaces);
                }
                if (c == '[') {
                    return new JSONArray(json).toString(indentSpaces);
                }
                if (!Character.isWhitespace(c)) {
                    return json;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
