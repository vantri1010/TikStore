package com.baidu.lbsapi.auth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class i extends Handler {
    final /* synthetic */ LBSAuthManager a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    i(LBSAuthManager lBSAuthManager, Looper looper) {
        super(looper);
        this.a = lBSAuthManager;
    }

    public void handleMessage(Message message) {
        a.a("handleMessage !!");
        LBSAuthManagerListener lBSAuthManagerListener = (LBSAuthManagerListener) LBSAuthManager.f.get(message.getData().getString("listenerKey"));
        a.a("handleMessage listener = " + lBSAuthManagerListener);
        if (lBSAuthManagerListener != null) {
            lBSAuthManagerListener.onAuthResult(message.what, message.obj.toString());
        }
    }
}
