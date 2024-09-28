package com.baidu.platform.domain;

import com.baidu.mapapi.http.HttpClient;

public class d {
    public static c a() {
        return HttpClient.isHttpsEnable ? new b() : new a();
    }
}
