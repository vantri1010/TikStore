package com.baidu.mapapi.map;

import android.graphics.Point;
import com.baidu.mapapi.map.p.a;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class p<T extends a> {
    private final h a;
    private final int b;
    private List<T> c;
    private List<p<T>> d;

    static abstract class a {
        a() {
        }

        /* access modifiers changed from: package-private */
        public abstract Point a();
    }

    private p(double d2, double d3, double d4, double d5, int i) {
        this(new h(d2, d3, d4, d5), i);
    }

    public p(h hVar) {
        this(hVar, 0);
    }

    private p(h hVar, int i) {
        this.d = null;
        this.a = hVar;
        this.b = i;
    }

    private void a() {
        ArrayList arrayList = new ArrayList(4);
        this.d = arrayList;
        arrayList.add(new p(this.a.a, this.a.e, this.a.b, this.a.f, this.b + 1));
        this.d.add(new p(this.a.e, this.a.c, this.a.b, this.a.f, this.b + 1));
        this.d.add(new p(this.a.a, this.a.e, this.a.f, this.a.d, this.b + 1));
        this.d.add(new p(this.a.e, this.a.c, this.a.f, this.a.d, this.b + 1));
        List<T> list = this.c;
        this.c = null;
        for (T t : list) {
            a((double) t.a().x, (double) t.a().y, t);
        }
    }

    private void a(double d2, double d3, T t) {
        int i;
        List<p<T>> list;
        if (this.d != null) {
            int i2 = (d3 > this.a.f ? 1 : (d3 == this.a.f ? 0 : -1));
            double d4 = this.a.e;
            if (i2 < 0) {
                int i3 = (d2 > d4 ? 1 : (d2 == d4 ? 0 : -1));
                list = this.d;
                i = i3 < 0 ? 0 : 1;
            } else {
                int i4 = (d2 > d4 ? 1 : (d2 == d4 ? 0 : -1));
                list = this.d;
                i = i4 < 0 ? 2 : 3;
            }
            list.get(i).a(d2, d3, t);
            return;
        }
        if (this.c == null) {
            this.c = new ArrayList();
        }
        this.c.add(t);
        if (this.c.size() > 40 && this.b < 40) {
            a();
        }
    }

    private void a(h hVar, Collection<T> collection) {
        if (this.a.a(hVar)) {
            List<p<T>> list = this.d;
            if (list != null) {
                for (p<T> a2 : list) {
                    a2.a(hVar, collection);
                }
            } else if (this.c == null) {
            } else {
                if (hVar.b(this.a)) {
                    collection.addAll(this.c);
                    return;
                }
                for (T t : this.c) {
                    if (hVar.a(t.a())) {
                        collection.add(t);
                    }
                }
            }
        }
    }

    public Collection<T> a(h hVar) {
        ArrayList arrayList = new ArrayList();
        a(hVar, arrayList);
        return arrayList;
    }

    public void a(T t) {
        Point a2 = t.a();
        if (this.a.a((double) a2.x, (double) a2.y)) {
            a((double) a2.x, (double) a2.y, t);
        }
    }
}
