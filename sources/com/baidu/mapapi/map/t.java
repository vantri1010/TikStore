package com.baidu.mapapi.map;

import android.view.View;
import com.baidu.mapapi.map.SwipeDismissTouchListener;

class t implements SwipeDismissTouchListener.DismissCallbacks {
    final /* synthetic */ SwipeDismissView a;

    t(SwipeDismissView swipeDismissView) {
        this.a = swipeDismissView;
    }

    public boolean canDismiss(Object obj) {
        return true;
    }

    public void onDismiss(View view, Object obj) {
        if (this.a.a != null) {
            this.a.a.onDismiss();
        }
    }

    public void onNotify() {
        if (this.a.a != null) {
            this.a.a.onNotify();
        }
    }
}
