package io.openinstall.sdk;

import android.app.Activity;
import java.lang.ref.WeakReference;

class aj extends a {
    final /* synthetic */ ai a;

    aj(ai aiVar) {
        this.a = aiVar;
    }

    public void onActivityResumed(Activity activity) {
        activity.getWindow().getDecorView().postDelayed(this.a.h, 300);
        WeakReference unused = this.a.e = new WeakReference(activity);
        ai aiVar = this.a;
        aiVar.a((WeakReference<Activity>) aiVar.e);
    }
}
