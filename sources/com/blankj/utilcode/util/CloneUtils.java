package com.blankj.utilcode.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public final class CloneUtils {
    private CloneUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static <T> T deepClone(T data, Type type) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson((Object) data), type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
