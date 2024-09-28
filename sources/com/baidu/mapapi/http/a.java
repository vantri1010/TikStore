package com.baidu.mapapi.http;

import com.baidu.mapapi.http.AsyncHttpClient;
import com.baidu.mapapi.http.HttpClient;

class a extends AsyncHttpClient.a {
    final /* synthetic */ HttpClient.ProtoResultCallback a;
    final /* synthetic */ String b;
    final /* synthetic */ AsyncHttpClient c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(AsyncHttpClient asyncHttpClient, HttpClient.ProtoResultCallback protoResultCallback, String str) {
        super((a) null);
        this.c = asyncHttpClient;
        this.a = protoResultCallback;
        this.b = str;
    }

    public void a() {
        HttpClient httpClient = new HttpClient("GET", this.a);
        httpClient.setMaxTimeOut(this.c.a);
        httpClient.setReadTimeOut(this.c.b);
        httpClient.request(this.b);
    }
}
