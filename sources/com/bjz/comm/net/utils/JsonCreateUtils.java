package com.bjz.comm.net.utils;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class JsonCreateUtils {
    public static MapForJsonObject build() {
        return new MapForJsonObject();
    }

    public static class MapForJsonObject {
        Gson gson = new Gson();
        Map<String, Object> map = new HashMap();

        public MapForJsonObject addParam(String strKey, Object obj) {
            this.map.put(strKey, obj);
            return this;
        }

        public RequestBody getHttpBody() {
            return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), this.gson.toJson((Object) this.map));
        }

        public String getJsonString() {
            return this.gson.toJson((Object) this.map);
        }
    }
}
