package com.baidu.location.f;

import android.util.Log;
import java.lang.ref.WeakReference;

class b implements Runnable {
    final /* synthetic */ WeakReference a;
    final /* synthetic */ a b;

    b(a aVar, WeakReference weakReference) {
        this.b = aVar;
        this.a = weakReference;
    }

    public void run() {
        a aVar = (a) this.a.get();
        if (aVar != null && aVar.h == 3) {
            Log.d("baidu_location_service", "baidu location service force stopped ...");
            aVar.d();
        }
    }
}
