package io.openinstall.sdk;

import android.app.Application;
import android.os.HandlerThread;
import android.text.TextUtils;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class q {
    /* access modifiers changed from: private */
    public final LinkedBlockingQueue<Object> a = new LinkedBlockingQueue<>(1);
    /* access modifiers changed from: private */
    public volatile boolean b = true;
    private final Thread c;
    private final Application d = ((Application) c.a().b());
    private Application.ActivityLifecycleCallbacks e;
    /* access modifiers changed from: private */
    public final t f;

    public q(h hVar) {
        HandlerThread handlerThread = new HandlerThread("EventsHandler");
        handlerThread.start();
        this.f = new t(this.d, handlerThread.getLooper(), hVar);
        Thread thread = new Thread(new r(this));
        this.c = thread;
        thread.setName("el");
        b();
    }

    private boolean a(String str) {
        return !TextUtils.isEmpty(str) && str.indexOf(44) < 0 && str.indexOf(59) < 0;
    }

    private void b() {
        this.b = true;
        this.c.start();
        c();
    }

    private void c() {
        s sVar = new s(this);
        this.e = sVar;
        this.d.registerActivityLifecycleCallbacks(sVar);
    }

    public void a() {
        l a2 = l.a();
        a2.a(true);
        this.f.a(a2);
    }

    public void a(long j) {
        if (j >= 1) {
            l a2 = l.a(j);
            a2.a(true);
            this.f.a(a2);
        }
    }

    public void a(String str, long j, Map<String, String> map) {
        if (!a(str)) {
            if (cb.a) {
                cb.b("传入的效果点名称包含不支持的字符 %s", str);
            }
        } else if (map == null || map.size() <= 10) {
            this.f.a(l.a(str, j, map));
        } else if (cb.a) {
            cb.c("传入的效果点附加参数超过限制", new Object[0]);
        }
    }
}
