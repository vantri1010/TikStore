package com.blankj.utilcode.util;

import android.util.Log;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ApiUtils {
    private static final String TAG = "ApiUtils";
    private Map<Class, BaseApi> mApiMap;
    private Map<Class, Class> mInjectApiImplMap;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    public @interface Api {
        boolean isMock() default false;
    }

    public static abstract class BaseApi {
    }

    private ApiUtils() {
        this.mApiMap = new ConcurrentHashMap();
        this.mInjectApiImplMap = new HashMap();
        init();
    }

    private void init() {
    }

    private void registerImpl(Class implClass) {
        this.mInjectApiImplMap.put(implClass.getSuperclass(), implClass);
    }

    public static <T extends BaseApi> T getApi(Class<T> apiClass) {
        if (apiClass != null) {
            return (BaseApi) getInstance().getApiInner(apiClass);
        }
        throw new NullPointerException("Argument 'apiClass' of type Class<T> (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static String toString_() {
        return getInstance().toString();
    }

    public String toString() {
        return "ApiUtils: " + this.mInjectApiImplMap;
    }

    private static ApiUtils getInstance() {
        return LazyHolder.INSTANCE;
    }

    private <Result> Result getApiInner(Class apiClass) {
        BaseApi api = this.mApiMap.get(apiClass);
        if (api == null) {
            synchronized (this) {
                api = (BaseApi) this.mApiMap.get(apiClass);
                if (api == null) {
                    Class implClass = this.mInjectApiImplMap.get(apiClass);
                    if (implClass != null) {
                        try {
                            api = (BaseApi) implClass.newInstance();
                            this.mApiMap.put(apiClass, api);
                        } catch (Exception e) {
                            Log.e(TAG, "The <" + implClass + "> has no parameterless constructor.");
                            return null;
                        }
                    } else {
                        Log.e(TAG, "The <" + apiClass + "> doesn't implement.");
                        return null;
                    }
                }
            }
        }
        return api;
    }

    private static class LazyHolder {
        /* access modifiers changed from: private */
        public static final ApiUtils INSTANCE = new ApiUtils();

        private LazyHolder() {
        }
    }
}
