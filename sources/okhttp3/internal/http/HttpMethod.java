package okhttp3.internal.http;

import com.zhy.http.okhttp.OkHttpUtils;

public final class HttpMethod {
    public static boolean invalidatesCache(String method) {
        return method.equals("POST") || method.equals(OkHttpUtils.METHOD.PATCH) || method.equals(OkHttpUtils.METHOD.PUT) || method.equals(OkHttpUtils.METHOD.DELETE) || method.equals("MOVE");
    }

    public static boolean requiresRequestBody(String method) {
        return method.equals("POST") || method.equals(OkHttpUtils.METHOD.PUT) || method.equals(OkHttpUtils.METHOD.PATCH) || method.equals("PROPPATCH") || method.equals("REPORT");
    }

    public static boolean permitsRequestBody(String method) {
        return !method.equals("GET") && !method.equals(OkHttpUtils.METHOD.HEAD);
    }

    public static boolean redirectsWithBody(String method) {
        return method.equals("PROPFIND");
    }

    public static boolean redirectsToGet(String method) {
        return !method.equals("PROPFIND");
    }

    private HttpMethod() {
    }
}
