package io.openinstall.sdk;

import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import org.json.JSONException;

public class bl extends bx {
    private final boolean a;
    private final AppInstallListener b;
    private int c;

    public bl(h hVar, boolean z, AppInstallListener appInstallListener) {
        super(hVar);
        this.a = z;
        this.b = appInstallListener;
    }

    public void a(int i) {
        this.c = i;
    }

    /* access modifiers changed from: protected */
    public void a(bf bfVar) {
        super.a(bfVar);
        if (bfVar.c() == null) {
            if (cb.a) {
                cb.a("decodeInstall success : %s", bfVar.b());
            }
            try {
                AppData b2 = b(bfVar.b());
                if (this.b != null) {
                    this.b.onInstallFinish(b2, (Error) null);
                }
            } catch (JSONException e) {
                if (cb.a) {
                    cb.c("decodeInstall error : %s", e.toString());
                }
                AppInstallListener appInstallListener = this.b;
                if (appInstallListener != null) {
                    appInstallListener.onInstallFinish((AppData) null, (Error) null);
                }
            }
        } else {
            if (cb.a) {
                cb.c("decodeInstall fail : %s", bfVar.c());
            }
            AppInstallListener appInstallListener2 = this.b;
            if (appInstallListener2 != null) {
                appInstallListener2.onInstallFinish((AppData) null, bfVar.c());
            }
        }
    }

    /* access modifiers changed from: protected */
    public int n() {
        int i = this.c;
        if (i > 0) {
            return i;
        }
        return 10;
    }

    /* access modifiers changed from: protected */
    public String o() {
        return "install";
    }

    /* access modifiers changed from: protected */
    public void p() {
        if (this.a) {
            m().b(o());
        } else {
            m().a(o());
        }
    }

    /* access modifiers changed from: protected */
    public bf q() {
        return bf.a(h().a("FM_init_data"));
    }
}
