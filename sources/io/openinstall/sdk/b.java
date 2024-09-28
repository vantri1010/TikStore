package io.openinstall.sdk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.listener.ResultCallback;
import io.openinstall.sdk.bf;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Map;

public class b {
    private static volatile b a;
    private final h b;
    private final q c;

    private b() {
        h hVar = new h();
        this.b = hVar;
        this.c = new q(hVar);
    }

    public static b a() {
        if (a == null) {
            synchronized (b.class) {
                if (a == null) {
                    a = new b();
                }
            }
        }
        return a;
    }

    private void a(Uri uri, AppWakeUpListener appWakeUpListener) {
        if (cb.a) {
            cb.a("decodeWakeUp", new Object[0]);
        }
        System.currentTimeMillis();
        new bm(this.b, uri, appWakeUpListener).b();
        System.currentTimeMillis();
    }

    public void a(Intent intent, AppWakeUpListener appWakeUpListener) {
        a(intent.getData(), appWakeUpListener);
    }

    public void a(AppWakeUpListener appWakeUpListener) {
        a((Uri) null, appWakeUpListener);
    }

    public void a(ResultCallback<File> resultCallback) {
        if (cb.a) {
            cb.a("getOriginalApk", new Object[0]);
        }
        System.currentTimeMillis();
        new bk(this.b, resultCallback).b();
        System.currentTimeMillis();
    }

    public void a(String str, long j) {
        a(str, j, (Map<String, String>) null);
    }

    public void a(String str, long j, Map<String, String> map) {
        if (cb.a) {
            cb.a("reportEffectPoint", new Object[0]);
        }
        this.c.a(str, j, map);
    }

    public void a(String str, String str2, ResultCallback<Void> resultCallback) {
        if (cb.a) {
            cb.a("reportShare", new Object[0]);
        }
        if (TextUtils.isEmpty(str)) {
            if (cb.a) {
                cb.c("shareCode 为空", new Object[0]);
            }
            resultCallback.onResult(null, bf.a.REQUEST_ERROR.a("shareCode 不能为空").c());
            return;
        }
        if (str.length() > 128 && cb.a) {
            cb.b("shareCode 长度超过128位", new Object[0]);
        }
        System.currentTimeMillis();
        o oVar = new o(str);
        oVar.a(str2);
        new bv(this.b, oVar, resultCallback).b();
        System.currentTimeMillis();
    }

    public void a(WeakReference<Activity> weakReference, long j) {
        new bo(this.b, weakReference).b();
        System.currentTimeMillis();
    }

    public void a(boolean z, int i, AppInstallListener appInstallListener) {
        if (cb.a) {
            cb.a("getInstallData", new Object[0]);
        }
        System.currentTimeMillis();
        bl blVar = new bl(this.b, z, appInstallListener);
        blVar.a(i);
        blVar.b();
        System.currentTimeMillis();
    }

    public String b() {
        return this.b.e().h();
    }

    public void c() {
        if (cb.a) {
            cb.a("reportRegister", new Object[0]);
        }
        this.c.a();
    }
}
