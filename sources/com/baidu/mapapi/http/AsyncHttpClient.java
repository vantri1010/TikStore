package com.baidu.mapapi.http;

import android.os.Build;
import com.baidu.mapapi.http.HttpClient;
import com.baidu.mapsdkplatform.comapi.util.PermissionCheck;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient {
    /* access modifiers changed from: private */
    public int a = 10000;
    /* access modifiers changed from: private */
    public int b = 10000;
    private ExecutorService c = Executors.newCachedThreadPool();

    private static abstract class a implements Runnable {
        private a() {
        }

        /* synthetic */ a(a aVar) {
            this();
        }

        public abstract void a();

        public void run() {
            a();
        }
    }

    static {
        if (Build.VERSION.SDK_INT <= 8) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    public void get(String str, HttpClient.ProtoResultCallback protoResultCallback) {
        if (str != null) {
            this.c.submit(new a(this, protoResultCallback, str));
            return;
        }
        throw new IllegalArgumentException("URI cannot be null");
    }

    /* access modifiers changed from: protected */
    public boolean isAuthorized() {
        int permissionCheck = PermissionCheck.permissionCheck();
        return permissionCheck == 0 || permissionCheck == 602 || permissionCheck == 601;
    }
}
