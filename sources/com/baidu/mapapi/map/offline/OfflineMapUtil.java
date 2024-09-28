package com.baidu.mapapi.map.offline;

import com.baidu.mapapi.model.CoordUtil;
import com.baidu.mapsdkplatform.comapi.map.s;
import com.baidu.mapsdkplatform.comapi.map.v;
import java.util.ArrayList;
import java.util.Iterator;

public class OfflineMapUtil {
    public static MKOLSearchRecord getSearchRecordFromLocalCityInfo(s sVar) {
        if (sVar == null) {
            return null;
        }
        MKOLSearchRecord mKOLSearchRecord = new MKOLSearchRecord();
        mKOLSearchRecord.cityID = sVar.a;
        mKOLSearchRecord.cityName = sVar.b;
        mKOLSearchRecord.cityType = sVar.d;
        long j = 0;
        if (sVar.a() != null) {
            ArrayList<MKOLSearchRecord> arrayList = new ArrayList<>();
            Iterator<s> it = sVar.a().iterator();
            while (it.hasNext()) {
                s next = it.next();
                arrayList.add(getSearchRecordFromLocalCityInfo(next));
                j += (long) next.c;
                mKOLSearchRecord.childCities = arrayList;
            }
        }
        if (mKOLSearchRecord.cityType != 1) {
            j = (long) sVar.c;
        }
        mKOLSearchRecord.dataSize = j;
        return mKOLSearchRecord;
    }

    public static MKOLUpdateElement getUpdatElementFromLocalMapElement(v vVar) {
        if (vVar == null) {
            return null;
        }
        MKOLUpdateElement mKOLUpdateElement = new MKOLUpdateElement();
        mKOLUpdateElement.cityID = vVar.a;
        mKOLUpdateElement.cityName = vVar.b;
        if (vVar.g != null) {
            mKOLUpdateElement.geoPt = CoordUtil.mc2ll(vVar.g);
        }
        mKOLUpdateElement.level = vVar.e;
        mKOLUpdateElement.ratio = vVar.i;
        mKOLUpdateElement.serversize = vVar.h;
        mKOLUpdateElement.size = vVar.i == 100 ? vVar.h : (vVar.h / 100) * vVar.i;
        mKOLUpdateElement.status = vVar.l;
        mKOLUpdateElement.update = vVar.j;
        return mKOLUpdateElement;
    }
}
