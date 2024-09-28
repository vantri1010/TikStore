package com.baidu.location.g;

import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.baidu.android.bbalbs.common.util.CommonParam;
import com.baidu.location.f;
import com.king.zxing.util.LogUtils;

public class b {
    public static String e = null;
    public static String f = null;
    public static String g = null;
    public static String h = null;
    public static int i = 0;
    private static b j = null;
    public String a = null;
    public String b = null;
    public String c = null;
    public String d = null;
    private boolean k = false;

    private b() {
        if (f.getServiceContext() != null) {
            a(f.getServiceContext());
        }
    }

    public static b a() {
        if (j == null) {
            j = new b();
        }
        return j;
    }

    public String a(boolean z) {
        return a(z, (String) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x011a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String a(boolean r4, java.lang.String r5) {
        /*
            r3 = this;
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r1 = 256(0x100, float:3.59E-43)
            r0.<init>(r1)
            java.lang.String r1 = "&sdk="
            r0.append(r1)
            r1 = 1090749727(0x4103851f, float:8.22)
            r0.append(r1)
            if (r4 == 0) goto L_0x0066
            java.lang.String r1 = com.baidu.location.g.k.g
            java.lang.String r2 = "all"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0023
            java.lang.String r1 = "&addr=allj2"
            r0.append(r1)
        L_0x0023:
            boolean r1 = com.baidu.location.g.k.i
            if (r1 != 0) goto L_0x0028
            goto L_0x002d
        L_0x0028:
            java.lang.String r1 = "&adtp=n2"
            r0.append(r1)
        L_0x002d:
            boolean r1 = com.baidu.location.g.k.h
            if (r1 != 0) goto L_0x003d
            boolean r1 = com.baidu.location.g.k.k
            if (r1 != 0) goto L_0x003d
            boolean r1 = com.baidu.location.g.k.l
            if (r1 != 0) goto L_0x003d
            boolean r1 = com.baidu.location.g.k.j
            if (r1 == 0) goto L_0x0066
        L_0x003d:
            java.lang.String r1 = "&sema="
            r0.append(r1)
            boolean r1 = com.baidu.location.g.k.h
            if (r1 == 0) goto L_0x004b
            java.lang.String r1 = "aptag|"
            r0.append(r1)
        L_0x004b:
            boolean r1 = com.baidu.location.g.k.j
            if (r1 == 0) goto L_0x0054
            java.lang.String r1 = "aptagd2|"
            r0.append(r1)
        L_0x0054:
            boolean r1 = com.baidu.location.g.k.k
            if (r1 == 0) goto L_0x005d
            java.lang.String r1 = "poiregion|"
            r0.append(r1)
        L_0x005d:
            boolean r1 = com.baidu.location.g.k.l
            if (r1 == 0) goto L_0x0066
            java.lang.String r1 = "regular"
            r0.append(r1)
        L_0x0066:
            if (r4 == 0) goto L_0x007e
            if (r5 != 0) goto L_0x006d
            java.lang.String r5 = "&coor=gcj02"
            goto L_0x0072
        L_0x006d:
            java.lang.String r1 = "&coor="
            r0.append(r1)
        L_0x0072:
            r0.append(r5)
            java.lang.String r5 = com.baidu.location.e.f.k()
            if (r5 == 0) goto L_0x007e
            r0.append(r5)
        L_0x007e:
            java.lang.String r5 = r3.c
            if (r5 != 0) goto L_0x008d
            java.lang.String r5 = "&im="
        L_0x0084:
            r0.append(r5)
            java.lang.String r5 = r3.a
            r0.append(r5)
            goto L_0x00bd
        L_0x008d:
            java.lang.String r5 = "&cu="
            r0.append(r5)
            java.lang.String r5 = r3.c
            r0.append(r5)
            java.lang.String r5 = r3.a
            if (r5 == 0) goto L_0x00bd
            java.lang.String r1 = "NULL"
            boolean r5 = r5.equals(r1)
            if (r5 != 0) goto L_0x00bd
            java.lang.String r5 = r3.c
            java.lang.StringBuffer r1 = new java.lang.StringBuffer
            java.lang.String r2 = r3.a
            r1.<init>(r2)
            java.lang.StringBuffer r1 = r1.reverse()
            java.lang.String r1 = r1.toString()
            boolean r5 = r5.contains(r1)
            if (r5 != 0) goto L_0x00bd
            java.lang.String r5 = "&Aim="
            goto L_0x0084
        L_0x00bd:
            java.lang.String r5 = r3.b
            if (r5 == 0) goto L_0x00cb
            java.lang.String r5 = "&snd="
            r0.append(r5)
            java.lang.String r5 = r3.b
            r0.append(r5)
        L_0x00cb:
            java.lang.String r5 = r3.d
            if (r5 == 0) goto L_0x00d9
            java.lang.String r5 = "&Aid="
            r0.append(r5)
            java.lang.String r5 = r3.d
            r0.append(r5)
        L_0x00d9:
            java.lang.String r5 = "&fw="
            r0.append(r5)
            float r5 = com.baidu.location.f.getFrameVersion()
            r0.append(r5)
            java.lang.String r5 = "&lt=1"
            r0.append(r5)
            java.lang.String r5 = "&mb="
            r0.append(r5)
            java.lang.String r5 = android.os.Build.MODEL
            r0.append(r5)
            if (r4 == 0) goto L_0x0104
            java.lang.String r5 = com.baidu.location.g.k.c()
            if (r5 == 0) goto L_0x0104
            java.lang.String r1 = "&laip="
            r0.append(r1)
            r0.append(r5)
        L_0x0104:
            java.lang.String r5 = "&resid="
            r0.append(r5)
            java.lang.String r5 = "12"
            r0.append(r5)
            java.lang.String r5 = "&os=A"
            r0.append(r5)
            int r5 = android.os.Build.VERSION.SDK_INT
            r0.append(r5)
            if (r4 == 0) goto L_0x0132
            java.lang.String r4 = "&sv="
            r0.append(r4)
            java.lang.String r4 = android.os.Build.VERSION.RELEASE
            if (r4 == 0) goto L_0x012f
            int r5 = r4.length()
            r1 = 6
            if (r5 <= r1) goto L_0x012f
            r5 = 0
            java.lang.String r4 = r4.substring(r5, r1)
        L_0x012f:
            r0.append(r4)
        L_0x0132:
            java.lang.String r4 = r0.toString()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.g.b.a(boolean, java.lang.String):java.lang.String");
    }

    public void a(Context context) {
        if (context != null && !this.k) {
            try {
                this.c = CommonParam.getCUID(context);
            } catch (Exception e2) {
                this.c = null;
            }
            try {
                e = context.getPackageName();
            } catch (Exception e3) {
                e = null;
            }
            k.o = "" + this.c;
            this.k = true;
        }
    }

    public void a(String str, String str2) {
        f = str;
        e = str2;
    }

    public String b() {
        String str;
        StringBuilder sb;
        if (this.c != null) {
            sb = new StringBuilder();
            sb.append("v8.22|");
            str = this.c;
        } else {
            sb = new StringBuilder();
            sb.append("v8.22|");
            str = this.a;
        }
        sb.append(str);
        sb.append(LogUtils.VERTICAL);
        sb.append(Build.MODEL);
        return sb.toString();
    }

    public String c() {
        String str;
        StringBuffer stringBuffer = new StringBuffer(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        if (this.c != null) {
            stringBuffer.append("&cu=");
            str = this.c;
        } else {
            stringBuffer.append("&im=");
            str = this.a;
        }
        stringBuffer.append(str);
        try {
            stringBuffer.append("&mb=");
            stringBuffer.append(Build.MODEL);
        } catch (Exception e2) {
        }
        stringBuffer.append("&pack=");
        try {
            stringBuffer.append(e);
        } catch (Exception e3) {
        }
        stringBuffer.append("&sdk=");
        stringBuffer.append(8.22f);
        return stringBuffer.toString();
    }
}
