package io.openinstall.sdk;

import android.net.Uri;
import android.util.Pair;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import io.openinstall.sdk.bf;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;

public class bm extends bx {
    private final h a;
    private final Uri b;
    private final AppWakeUpListener c;
    private boolean d = false;

    public bm(h hVar, Uri uri, AppWakeUpListener appWakeUpListener) {
        super(hVar);
        this.a = hVar;
        this.b = uri;
        this.c = appWakeUpListener;
    }

    private void a(Uri uri) {
        new bw(this.a, uri).b();
    }

    private bf s() {
        List<String> pathSegments = this.b.getPathSegments();
        if (pathSegments == null || pathSegments.size() <= 0) {
            return bf.a.INVALID_URI.a();
        }
        if (pathSegments.get(0).equalsIgnoreCase("c")) {
            this.d = true;
            return bf.a(pathSegments.size() > 1 ? bz.b(pathSegments.get(1)) : "");
        } else if (!pathSegments.get(0).equalsIgnoreCase("h")) {
            return bf.a.INVALID_URI.a();
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("waU", this.b.toString());
            bb b2 = l().b(hashMap);
            if (!(b2 instanceof ay)) {
                b2 = l().b(hashMap);
            }
            a(b2);
            return bf.a(b2);
        }
    }

    private bf t() {
        HashMap hashMap = new HashMap();
        LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(1);
        e().execute(new bn(this, linkedBlockingQueue));
        try {
            Pair pair = (Pair) linkedBlockingQueue.poll(3, TimeUnit.SECONDS);
            hashMap.put(pair.first, pair.second);
            m().a(o());
        } catch (InterruptedException e) {
        }
        bb b2 = l().b(hashMap);
        if (!(b2 instanceof ay)) {
            b2 = l().b(hashMap);
        }
        a(b2);
        return bf.a(b2);
    }

    /* access modifiers changed from: protected */
    public void a(bf bfVar) {
        super.a(bfVar);
        if (bfVar.c() == null) {
            String b2 = bfVar.b();
            if (cb.a) {
                cb.a("decodeWakeUp success : %s", b2);
            }
            try {
                AppData a2 = this.d ? a(b2) : b(b2);
                if (this.c != null) {
                    this.c.onWakeUpFinish(a2, (Error) null);
                }
                if (!a2.isEmpty()) {
                    a(this.b);
                }
            } catch (JSONException e) {
                if (cb.a) {
                    cb.c("decodeWakeUp error : %s", e.toString());
                }
                AppWakeUpListener appWakeUpListener = this.c;
                if (appWakeUpListener != null) {
                    appWakeUpListener.onWakeUpFinish((AppData) null, (Error) null);
                }
            }
        } else {
            if (cb.a) {
                cb.c("decodeWakeUp fail : %s", bfVar.c());
            }
            AppWakeUpListener appWakeUpListener2 = this.c;
            if (appWakeUpListener2 != null) {
                appWakeUpListener2.onWakeUpFinish((AppData) null, bfVar.c());
            }
        }
    }

    /* access modifiers changed from: protected */
    public int n() {
        return 6;
    }

    /* access modifiers changed from: protected */
    public String o() {
        return "wakeup";
    }

    /* access modifiers changed from: protected */
    public bf q() {
        return this.b == null ? t() : s();
    }
}
