package com.blankj.utilcode.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GsonUtils {
    private static final Gson GSON = createGson(true);
    private static final Gson GSON_NO_NULLS = createGson(false);

    private GsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static Gson getGson() {
        return getGson(true);
    }

    public static Gson getGson(boolean serializeNulls) {
        return serializeNulls ? GSON_NO_NULLS : GSON;
    }

    public static String toJson(Object object) {
        return toJson(object, true);
    }

    public static String toJson(Object object, boolean includeNulls) {
        return (includeNulls ? GSON : GSON_NO_NULLS).toJson(object);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return toJson(src, typeOfSrc, true);
    }

    public static String toJson(Object src, Type typeOfSrc, boolean includeNulls) {
        return (includeNulls ? GSON : GSON_NO_NULLS).toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(Reader reader, Class<T> type) {
        return GSON.fromJson(reader, type);
    }

    public static <T> T fromJson(Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }

    public static Type getListType(Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static Type getSetType(Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    public static Type getMapType(Type keyType, Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    public static Type getArrayType(Type type) {
        return TypeToken.getArray(type).getType();
    }

    public static Type getType(Type rawType, Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    private static Gson createGson(boolean serializeNulls) {
        GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) {
            builder.serializeNulls();
        }
        return builder.create();
    }
}
