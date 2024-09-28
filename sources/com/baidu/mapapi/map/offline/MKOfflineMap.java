package com.baidu.mapapi.map.offline;

import com.baidu.mapsdkplatform.comapi.map.k;
import com.baidu.mapsdkplatform.comapi.map.s;
import com.baidu.mapsdkplatform.comapi.map.t;
import com.baidu.mapsdkplatform.comapi.map.w;
import com.baidu.mapsdkplatform.comapi.map.x;
import java.util.ArrayList;
import java.util.Iterator;

public class MKOfflineMap {
    public static final int TYPE_DOWNLOAD_UPDATE = 0;
    public static final int TYPE_NETWORK_ERROR = 2;
    public static final int TYPE_NEW_OFFLINE = 6;
    public static final int TYPE_VER_UPDATE = 4;
    private static final String a = MKOfflineMap.class.getSimpleName();
    /* access modifiers changed from: private */
    public t b;
    /* access modifiers changed from: private */
    public MKOfflineMapListener c;

    public void destroy() {
        this.b.d(0);
        this.b.b((x) null);
        this.b.b();
        k.b();
    }

    public ArrayList<MKOLUpdateElement> getAllUpdateInfo() {
        ArrayList<w> e = this.b.e();
        if (e == null) {
            return null;
        }
        ArrayList<MKOLUpdateElement> arrayList = new ArrayList<>();
        Iterator<w> it = e.iterator();
        while (it.hasNext()) {
            arrayList.add(OfflineMapUtil.getUpdatElementFromLocalMapElement(it.next().a()));
        }
        return arrayList;
    }

    public ArrayList<MKOLSearchRecord> getHotCityList() {
        ArrayList<s> c2 = this.b.c();
        if (c2 == null) {
            return null;
        }
        ArrayList<MKOLSearchRecord> arrayList = new ArrayList<>();
        Iterator<s> it = c2.iterator();
        while (it.hasNext()) {
            arrayList.add(OfflineMapUtil.getSearchRecordFromLocalCityInfo(it.next()));
        }
        return arrayList;
    }

    public ArrayList<MKOLSearchRecord> getOfflineCityList() {
        ArrayList<s> d = this.b.d();
        if (d == null) {
            return null;
        }
        ArrayList<MKOLSearchRecord> arrayList = new ArrayList<>();
        Iterator<s> it = d.iterator();
        while (it.hasNext()) {
            arrayList.add(OfflineMapUtil.getSearchRecordFromLocalCityInfo(it.next()));
        }
        return arrayList;
    }

    public MKOLUpdateElement getUpdateInfo(int i) {
        w g = this.b.g(i);
        if (g == null) {
            return null;
        }
        return OfflineMapUtil.getUpdatElementFromLocalMapElement(g.a());
    }

    @Deprecated
    public int importOfflineData() {
        return importOfflineData(false);
    }

    @Deprecated
    public int importOfflineData(boolean z) {
        int i;
        ArrayList<w> e = this.b.e();
        int i2 = 0;
        if (e != null) {
            i2 = e.size();
            i = i2;
        } else {
            i = 0;
        }
        this.b.a(z, true);
        ArrayList<w> e2 = this.b.e();
        if (e2 != null) {
            i = e2.size();
        }
        return i - i2;
    }

    public boolean init(MKOfflineMapListener mKOfflineMapListener) {
        k.a();
        t a2 = t.a();
        this.b = a2;
        if (a2 == null) {
            return false;
        }
        a2.a((x) new a(this));
        this.c = mKOfflineMapListener;
        return true;
    }

    public boolean pause(int i) {
        return this.b.c(i);
    }

    public boolean remove(int i) {
        return this.b.e(i);
    }

    public ArrayList<MKOLSearchRecord> searchCity(String str) {
        ArrayList<s> a2 = this.b.a(str);
        if (a2 == null) {
            return null;
        }
        ArrayList<MKOLSearchRecord> arrayList = new ArrayList<>();
        Iterator<s> it = a2.iterator();
        while (it.hasNext()) {
            arrayList.add(OfflineMapUtil.getSearchRecordFromLocalCityInfo(it.next()));
        }
        return arrayList;
    }

    public boolean start(int i) {
        t tVar = this.b;
        if (tVar == null) {
            return false;
        }
        if (tVar.e() != null) {
            Iterator<w> it = this.b.e().iterator();
            while (it.hasNext()) {
                w next = it.next();
                if (next.a.a == i) {
                    if (next.a.j || next.a.l == 2 || next.a.l == 3 || next.a.l == 6) {
                        return this.b.b(i);
                    }
                    return false;
                }
            }
        }
        return this.b.a(i);
    }

    public boolean update(int i) {
        t tVar = this.b;
        if (tVar != null && tVar.e() != null) {
            Iterator<w> it = this.b.e().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                w next = it.next();
                if (next.a.a == i) {
                    if (next.a.j) {
                        return this.b.f(i);
                    }
                }
            }
        }
        return false;
    }
}
