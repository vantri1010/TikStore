package com.baidu.lbsapi.auth;

import java.util.Hashtable;

class j implements Runnable {
    final /* synthetic */ int a;
    final /* synthetic */ boolean b;
    final /* synthetic */ String c;
    final /* synthetic */ String d;
    final /* synthetic */ Hashtable e;
    final /* synthetic */ LBSAuthManager f;

    j(LBSAuthManager lBSAuthManager, int i, boolean z, String str, String str2, Hashtable hashtable) {
        this.f = lBSAuthManager;
        this.a = i;
        this.b = z;
        this.c = str;
        this.d = str2;
        this.e = hashtable;
    }

    public void run() {
        a.a("status = " + this.a + "; forced = " + this.b + "checkAK = " + this.f.b(this.c));
        int i = this.a;
        if (i == 601 || this.b || i == -1 || this.f.b(this.c)) {
            a.a("authenticate sendAuthRequest");
            String[] b2 = b.b(LBSAuthManager.a);
            if (b2 == null || b2.length <= 1) {
                this.f.a(this.b, this.d, this.e, this.c);
                return;
            }
            a.a("authStrings.length:" + b2.length);
            a.a("more sha1 auth");
            this.f.a(this.b, this.d, (Hashtable<String, String>) this.e, b2, this.c);
            return;
        }
        if (602 == this.a) {
            a.a("authenticate wait ");
            if (LBSAuthManager.d != null) {
                LBSAuthManager.d.b();
            }
        } else {
            a.a("authenticate else");
        }
        this.f.a((String) null, this.c);
    }
}
