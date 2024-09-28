package com.baidu.location.g;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public abstract class e {
    private static String a = "10.0.0.172";
    private static int b = 80;
    public static int g = a.g;
    protected static int p = 0;
    public String h = null;
    public int i = 1;
    public String j = null;
    public Map<String, Object> k = null;
    public String l = null;
    public byte[] m = null;
    public byte[] n = null;
    public String o = null;

    public abstract void a();

    public void a(ExecutorService executorService) {
        try {
            executorService.execute(new f(this));
        } catch (Throwable th) {
            a(false);
        }
    }

    public void a(ExecutorService executorService, String str) {
        try {
            executorService.execute(new i(this, str));
        } catch (Throwable th) {
            a(false);
        }
    }

    public void a(ExecutorService executorService, boolean z, String str) {
        try {
            executorService.execute(new g(this, str, z));
        } catch (Throwable th) {
            a(false);
        }
    }

    public abstract void a(boolean z);

    public void b(ExecutorService executorService) {
        a(executorService, false, "loc.map.baidu.com");
    }

    public void c(String str) {
        try {
            new h(this, str).start();
        } catch (Throwable th) {
            a(false);
        }
    }
}
