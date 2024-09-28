package com.baidu.mapapi.map;

import android.os.Bundle;
import com.baidu.mapapi.map.Overlay;

class a implements Overlay.a {
    final /* synthetic */ BaiduMap a;

    a(BaiduMap baiduMap) {
        this.a = baiduMap;
    }

    public void a(Overlay overlay) {
        if (overlay != null && this.a.k.contains(overlay)) {
            Bundle a2 = overlay.a();
            if (this.a.i != null) {
                this.a.i.d(a2);
            }
            this.a.k.remove(overlay);
        }
        if (overlay != null && this.a.m.contains(overlay)) {
            this.a.m.remove(overlay);
        }
        if (overlay != null && this.a.l.contains(overlay)) {
            Marker marker = (Marker) overlay;
            if (marker.p != null) {
                this.a.l.remove(marker);
                if (this.a.l.size() == 0 && this.a.i != null) {
                    this.a.i.b(false);
                }
            }
        }
    }

    public void b(Overlay overlay) {
        if (overlay != null && this.a.k.contains(overlay)) {
            boolean z = false;
            if (overlay instanceof Marker) {
                Marker marker = (Marker) overlay;
                if (marker.b != null) {
                    if (marker.p != null && marker.p.size() > 1) {
                        Bundle bundle = new Bundle();
                        if (this.a.i != null) {
                            marker.remove();
                            marker.p.clear();
                            this.a.i.b(overlay.a(bundle));
                            this.a.k.add(overlay);
                            z = true;
                        }
                    }
                } else if (!(marker.p == null || marker.p.size() == 0)) {
                    if (this.a.l.contains(marker)) {
                        this.a.l.remove(marker);
                    }
                    this.a.l.add(marker);
                    if (this.a.i != null) {
                        this.a.i.b(true);
                    }
                }
            }
            if (this.a.i != null && !z) {
                this.a.i.c(overlay.a(new Bundle()));
            }
        }
        if (this.a.m.contains(overlay)) {
            this.a.m.remove(overlay);
        }
        if (overlay instanceof Marker) {
            this.a.m.add((Marker) overlay);
        }
    }

    public boolean c(Overlay overlay) {
        return this.a.k != null && !this.a.k.contains(overlay);
    }
}
