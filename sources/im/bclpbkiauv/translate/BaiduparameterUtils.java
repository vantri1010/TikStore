package im.bclpbkiauv.translate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import kotlin.text.Typography;

public class BaiduparameterUtils {
    public static Map<String, String> buildParams(String appid, String securityKey, String query, String from, String to) {
        Map<String, String> params = new HashMap<>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        params.put("sign", MD5.md5(appid + query + salt + securityKey));
        return params;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value != null) {
                if (i != 0) {
                    builder.append(Typography.amp);
                }
                builder.append(key);
                builder.append('=');
                builder.append(encode(value));
                i++;
            }
        }
        return builder.toString();
    }

    public static String encode(String input) {
        if (input == null) {
            return "";
        }
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return input;
        }
    }
}
