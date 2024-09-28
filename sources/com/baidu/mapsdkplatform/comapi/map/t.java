package com.baidu.mapsdkplatform.comapi.map;

import android.content.Context;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.common.EnvironmentUtilities;
import com.baidu.mapapi.common.SysOSUtil;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapsdkplatform.comjni.map.basemap.a;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class t {
    private static final String a = t.class.getSimpleName();
    /* access modifiers changed from: private */
    public static t c;
    private a b;
    /* access modifiers changed from: private */
    public y d;
    private Handler e;

    private t() {
    }

    public static t a() {
        if (c == null) {
            t tVar = new t();
            c = tVar;
            tVar.g();
        }
        return c;
    }

    private void g() {
        h();
        this.d = new y();
        u uVar = new u(this);
        this.e = uVar;
        MessageCenter.registMessage(65289, uVar);
    }

    private void h() {
        Context context = BMapManager.getContext();
        EnvironmentUtilities.initAppDirectory(context);
        a aVar = new a();
        this.b = aVar;
        aVar.a(context.hashCode());
        String moduleFileName = SysOSUtil.getModuleFileName();
        String appSDCardPath = EnvironmentUtilities.getAppSDCardPath();
        String appCachePath = EnvironmentUtilities.getAppCachePath();
        String appSecondCachePath = EnvironmentUtilities.getAppSecondCachePath();
        int mapTmpStgMax = EnvironmentUtilities.getMapTmpStgMax();
        int domTmpStgMax = EnvironmentUtilities.getDomTmpStgMax();
        int itsTmpStgMax = EnvironmentUtilities.getItsTmpStgMax();
        String str = SysOSUtil.getDensityDpi() >= 180 ? "/h/" : "/l/";
        String str2 = moduleFileName + "/cfg";
        String str3 = appSDCardPath + "/vmp";
        String str4 = str3 + str;
        String str5 = str3 + str;
        String str6 = appCachePath + "/tmp/";
        this.b.a(str2 + "/a/", str4, str6, appSecondCachePath + "/tmp/", str5, str2 + "/a/", (String) null, 0, str2 + "/idrres/", SysOSUtil.getScreenSizeX(), SysOSUtil.getScreenSizeY(), SysOSUtil.getDensityDpi(), mapTmpStgMax, domTmpStgMax, itsTmpStgMax, 0);
        this.b.d();
    }

    public ArrayList<s> a(String str) {
        JSONArray optJSONArray;
        String str2 = str;
        if (!str2.equals("")) {
            a aVar = this.b;
            if (aVar != null) {
                String a2 = aVar.a(str2);
                if (a2 == null || a2.equals("")) {
                    return null;
                }
                ArrayList<s> arrayList = new ArrayList<>();
                try {
                    JSONObject jSONObject = new JSONObject(a2);
                    if (jSONObject.length() == 0 || (optJSONArray = jSONObject.optJSONArray("dataset")) == null) {
                        return null;
                    }
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        s sVar = new s();
                        JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
                        int optInt = jSONObject2.optInt(TtmlNode.ATTR_ID);
                        if (optInt <= 2000 || optInt == 2912 || optInt == 2911 || optInt == 9000) {
                            sVar.a = optInt;
                            sVar.b = jSONObject2.optString("name");
                            sVar.c = jSONObject2.optInt("mapsize");
                            sVar.d = jSONObject2.optInt("cty");
                            if (jSONObject2.has("child")) {
                                JSONArray optJSONArray2 = jSONObject2.optJSONArray("child");
                                ArrayList arrayList2 = new ArrayList();
                                for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                                    s sVar2 = new s();
                                    JSONObject optJSONObject = optJSONArray2.optJSONObject(i2);
                                    sVar2.a = optJSONObject.optInt(TtmlNode.ATTR_ID);
                                    sVar2.b = optJSONObject.optString("name");
                                    sVar2.c = optJSONObject.optInt("mapsize");
                                    sVar2.d = optJSONObject.optInt("cty");
                                    arrayList2.add(sVar2);
                                }
                                sVar.a(arrayList2);
                            }
                            arrayList.add(sVar);
                        }
                    }
                    return arrayList;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    public void a(x xVar) {
        y yVar = this.d;
        if (yVar != null) {
            yVar.a(xVar);
        }
    }

    public boolean a(int i) {
        if (this.b == null || i < 0) {
            return false;
        }
        if (i <= 2000 || i == 2912 || i == 2911 || i == 9000) {
            return this.b.d(i);
        }
        return false;
    }

    public boolean a(boolean z, boolean z2) {
        a aVar = this.b;
        if (aVar == null) {
            return false;
        }
        return aVar.a(z, z2);
    }

    public void b() {
        MessageCenter.unregistMessage(65289, this.e);
        this.b.b(BMapManager.getContext().hashCode());
        c = null;
    }

    public void b(x xVar) {
        y yVar = this.d;
        if (yVar != null) {
            yVar.b(xVar);
        }
    }

    public boolean b(int i) {
        if (this.b == null || i < 0) {
            return false;
        }
        if (i <= 2000 || i == 2912 || i == 2911 || i == 9000) {
            return this.b.a(i, false, 0);
        }
        return false;
    }

    public ArrayList<s> c() {
        a aVar = this.b;
        if (aVar == null) {
            return null;
        }
        String m = aVar.m();
        ArrayList<s> arrayList = new ArrayList<>();
        try {
            JSONArray optJSONArray = new JSONObject(m).optJSONArray("dataset");
            for (int i = 0; i < optJSONArray.length(); i++) {
                s sVar = new s();
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                sVar.a = optJSONObject.optInt(TtmlNode.ATTR_ID);
                sVar.b = optJSONObject.optString("name");
                sVar.c = optJSONObject.optInt("mapsize");
                sVar.d = optJSONObject.optInt("cty");
                if (optJSONObject.has("child")) {
                    JSONArray optJSONArray2 = optJSONObject.optJSONArray("child");
                    ArrayList arrayList2 = new ArrayList();
                    for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                        s sVar2 = new s();
                        JSONObject optJSONObject2 = optJSONArray2.optJSONObject(i2);
                        sVar2.a = optJSONObject2.optInt(TtmlNode.ATTR_ID);
                        sVar2.b = optJSONObject2.optString("name");
                        sVar2.c = optJSONObject2.optInt("mapsize");
                        sVar2.d = optJSONObject2.optInt("cty");
                        arrayList2.add(sVar2);
                    }
                    sVar.a(arrayList2);
                }
                arrayList.add(sVar);
            }
            return arrayList;
        } catch (JSONException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public boolean c(int i) {
        a aVar = this.b;
        if (aVar == null || i < 0) {
            return false;
        }
        return aVar.b(i, false, 0);
    }

    public ArrayList<s> d() {
        a aVar = this.b;
        if (aVar == null) {
            return null;
        }
        String a2 = aVar.a("");
        ArrayList<s> arrayList = new ArrayList<>();
        try {
            JSONArray optJSONArray = new JSONObject(a2).optJSONArray("dataset");
            for (int i = 0; i < optJSONArray.length(); i++) {
                s sVar = new s();
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                int optInt = optJSONObject.optInt(TtmlNode.ATTR_ID);
                if (optInt <= 2000 || optInt == 2912 || optInt == 2911 || optInt == 9000) {
                    sVar.a = optInt;
                    sVar.b = optJSONObject.optString("name");
                    sVar.c = optJSONObject.optInt("mapsize");
                    sVar.d = optJSONObject.optInt("cty");
                    if (optJSONObject.has("child")) {
                        JSONArray optJSONArray2 = optJSONObject.optJSONArray("child");
                        ArrayList arrayList2 = new ArrayList();
                        int i2 = 0;
                        while (i2 < optJSONArray2.length()) {
                            s sVar2 = new s();
                            JSONObject optJSONObject2 = optJSONArray2.optJSONObject(i2);
                            try {
                                sVar2.a = optJSONObject2.optInt(TtmlNode.ATTR_ID);
                                sVar2.b = optJSONObject2.optString("name");
                                sVar2.c = optJSONObject2.optInt("mapsize");
                                sVar2.d = optJSONObject2.optInt("cty");
                                arrayList2.add(sVar2);
                                i2++;
                            } catch (JSONException e2) {
                                return null;
                            } catch (Exception e3) {
                                return null;
                            }
                        }
                        sVar.a(arrayList2);
                    }
                    arrayList.add(sVar);
                }
            }
            return arrayList;
        } catch (JSONException e4) {
            return null;
        } catch (Exception e5) {
            return null;
        }
    }

    public boolean d(int i) {
        a aVar = this.b;
        if (aVar == null) {
            return false;
        }
        return aVar.b(0, true, i);
    }

    public ArrayList<w> e() {
        String l;
        a aVar = this.b;
        if (!(aVar == null || (l = aVar.l()) == null || l.equals(""))) {
            ArrayList<w> arrayList = new ArrayList<>();
            try {
                JSONObject jSONObject = new JSONObject(l);
                if (jSONObject.length() == 0) {
                    return null;
                }
                JSONArray optJSONArray = jSONObject.optJSONArray("dataset");
                for (int i = 0; i < optJSONArray.length(); i++) {
                    w wVar = new w();
                    v vVar = new v();
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    vVar.a = optJSONObject.optInt(TtmlNode.ATTR_ID);
                    vVar.b = optJSONObject.optString("name");
                    vVar.c = optJSONObject.optString("pinyin");
                    vVar.h = optJSONObject.optInt("mapoldsize");
                    vVar.i = optJSONObject.optInt("ratio");
                    vVar.l = optJSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                    vVar.g = new GeoPoint((double) optJSONObject.optInt("y"), (double) optJSONObject.optInt("x"));
                    boolean z = true;
                    if (optJSONObject.optInt("up") != 1) {
                        z = false;
                    }
                    vVar.j = z;
                    vVar.e = optJSONObject.optInt("lev");
                    if (vVar.j) {
                        vVar.k = optJSONObject.optInt("mapsize");
                    } else {
                        vVar.k = 0;
                    }
                    wVar.a(vVar);
                    arrayList.add(wVar);
                }
                return arrayList;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public boolean e(int i) {
        a aVar = this.b;
        if (aVar == null || i < 0) {
            return false;
        }
        return aVar.b(i, false);
    }

    public boolean f(int i) {
        if (this.b == null || i < 0) {
            return false;
        }
        if (i <= 2000 || i == 2912 || i == 2911 || i == 9000) {
            return this.b.a(i, false);
        }
        return false;
    }

    public w g(int i) {
        String e2;
        a aVar = this.b;
        if (aVar != null && i >= 0 && (e2 = aVar.e(i)) != null && !e2.equals("")) {
            w wVar = new w();
            v vVar = new v();
            try {
                JSONObject jSONObject = new JSONObject(e2);
                if (jSONObject.length() == 0) {
                    return null;
                }
                int optInt = jSONObject.optInt(TtmlNode.ATTR_ID);
                if (optInt > 2000 && optInt != 2912 && optInt != 2911 && optInt != 9000) {
                    return null;
                }
                vVar.a = optInt;
                vVar.b = jSONObject.optString("name");
                vVar.c = jSONObject.optString("pinyin");
                vVar.d = jSONObject.optString("headchar");
                vVar.h = jSONObject.optInt("mapoldsize");
                vVar.i = jSONObject.optInt("ratio");
                vVar.l = jSONObject.optInt(NotificationCompat.CATEGORY_STATUS);
                vVar.g = new GeoPoint((double) jSONObject.optInt("y"), (double) jSONObject.optInt("x"));
                boolean z = true;
                if (jSONObject.optInt("up") != 1) {
                    z = false;
                }
                vVar.j = z;
                vVar.e = jSONObject.optInt("lev");
                if (vVar.j) {
                    vVar.k = jSONObject.optInt("mapsize");
                } else {
                    vVar.k = 0;
                }
                vVar.f = jSONObject.optInt("ver");
                wVar.a(vVar);
                return wVar;
            } catch (JSONException e3) {
                e3.printStackTrace();
            }
        }
        return null;
    }
}
