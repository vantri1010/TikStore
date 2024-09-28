package com.baidu.platform.util;

import com.baidu.mapsdkplatform.comjni.util.AppMD5;
import java.util.LinkedHashMap;
import java.util.Map;

public class a implements ParamBuilder<a> {
    protected Map<String, String> a;

    public a a(String str, String str2) {
        if (this.a == null) {
            this.a = new LinkedHashMap();
        }
        this.a.put(str, str2);
        return this;
    }

    public String a() {
        StringBuilder sb;
        Map<String, String> map = this.a;
        if (map == null || map.isEmpty()) {
            return null;
        }
        String str = new String();
        int i = 0;
        for (String next : this.a.keySet()) {
            String encodeUrlParamsValue = AppMD5.encodeUrlParamsValue(this.a.get(next));
            if (i != 0) {
                sb = new StringBuilder();
                sb.append(str);
                str = "&";
            }
            sb.append(str);
            sb.append(next);
            sb.append("=");
            sb.append(encodeUrlParamsValue);
            str = sb.toString();
            i++;
        }
        return str;
    }
}
