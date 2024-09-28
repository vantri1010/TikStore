package com.ta.utdid2.a.a;

public class h {
    public static String get(String key, String defaultValue) {
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            return (String) cls.getMethod("get", new Class[]{String.class, String.class}).invoke(cls, new Object[]{key, defaultValue});
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
