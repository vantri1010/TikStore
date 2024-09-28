package io.openinstall.sdk;

import android.content.Context;
import com.fm.openinstall.listener.ResultCallback;
import io.openinstall.sdk.bf;
import java.io.File;
import java.io.IOException;

public class bk extends bc {
    private final ResultCallback<File> a;

    public bk(h hVar, ResultCallback<File> resultCallback) {
        super(hVar);
        this.a = resultCallback;
    }

    /* access modifiers changed from: protected */
    public void a(bf bfVar) {
        super.a(bfVar);
        ResultCallback<File> resultCallback = this.a;
        if (resultCallback != null) {
            resultCallback.onResult(new File(bfVar.b()), bfVar.c());
        }
    }

    /* renamed from: n */
    public bf call() {
        Context b = c.a().b();
        String str = b.getApplicationInfo().sourceDir;
        String str2 = b.getFilesDir() + File.separator + b.getPackageName() + ".apk";
        try {
            ap.a((byte[]) null, new File(str), new File(str2));
            return bf.a(str2);
        } catch (IOException e) {
            if (cb.a) {
                e.printStackTrace();
            }
            return bf.a.REQUEST_FAIL.a(e.getMessage());
        }
    }
}
