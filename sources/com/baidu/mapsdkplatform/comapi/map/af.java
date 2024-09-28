package com.baidu.mapsdkplatform.comapi.map;

import android.os.Handler;

class af extends Handler {
    final /* synthetic */ ae a;

    af(ae aeVar) {
        this.a = aeVar;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0103, code lost:
        if (r12.a.h != null) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0119, code lost:
        if (r12.a.h != null) goto L_0x0105;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleMessage(android.os.Message r13) {
        /*
            r12 = this;
            super.handleMessage(r13)
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            if (r0 == 0) goto L_0x0301
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            com.baidu.mapsdkplatform.comjni.map.basemap.a r0 = r0.i
            if (r0 != 0) goto L_0x0017
            goto L_0x0301
        L_0x0017:
            java.lang.Object r0 = r13.obj
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
            com.baidu.mapsdkplatform.comapi.map.ae r2 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r2 = r2.i
            long r2 = r2.j
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x002c
            return
        L_0x002c:
            int r0 = r13.what
            r1 = 4000(0xfa0, float:5.605E-42)
            r2 = 0
            r3 = 1
            if (r0 != r1) goto L_0x00c8
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            if (r0 != 0) goto L_0x003f
            return
        L_0x003f:
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            java.util.Iterator r0 = r0.iterator()
        L_0x004b:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0301
            java.lang.Object r1 = r0.next()
            com.baidu.mapsdkplatform.comapi.map.n r1 = (com.baidu.mapsdkplatform.comapi.map.n) r1
            r4 = 0
            int r5 = r13.arg2
            if (r5 != r3) goto L_0x00c2
            int r4 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r5 = com.baidu.mapsdkplatform.comapi.map.ae.b
            int r4 = r4 * r5
            int[] r4 = new int[r4]
            int r5 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r6 = com.baidu.mapsdkplatform.comapi.map.ae.b
            int r5 = r5 * r6
            int[] r5 = new int[r5]
            com.baidu.mapsdkplatform.comapi.map.ae r6 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r6 = r6.i
            com.baidu.mapsdkplatform.comjni.map.basemap.a r6 = r6.i
            if (r6 != 0) goto L_0x0077
            return
        L_0x0077:
            com.baidu.mapsdkplatform.comapi.map.ae r6 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r6 = r6.i
            com.baidu.mapsdkplatform.comjni.map.basemap.a r6 = r6.i
            int r7 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r8 = com.baidu.mapsdkplatform.comapi.map.ae.b
            int[] r4 = r6.a((int[]) r4, (int) r7, (int) r8)
            r6 = 0
        L_0x0088:
            int r7 = com.baidu.mapsdkplatform.comapi.map.ae.b
            if (r6 >= r7) goto L_0x00b8
            r7 = 0
        L_0x008d:
            int r8 = com.baidu.mapsdkplatform.comapi.map.ae.a
            if (r7 >= r8) goto L_0x00b5
            int r8 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r8 = r8 * r6
            int r8 = r8 + r7
            r8 = r4[r8]
            int r9 = r8 >> 16
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r10 = r8 << 16
            r11 = 16711680(0xff0000, float:2.3418052E-38)
            r10 = r10 & r11
            r11 = -16711936(0xffffffffff00ff00, float:-1.7146522E38)
            r8 = r8 & r11
            r8 = r8 | r10
            r8 = r8 | r9
            int r9 = com.baidu.mapsdkplatform.comapi.map.ae.b
            int r9 = r9 - r6
            int r9 = r9 - r3
            int r10 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r9 = r9 * r10
            int r9 = r9 + r7
            r5[r9] = r8
            int r7 = r7 + 1
            goto L_0x008d
        L_0x00b5:
            int r6 = r6 + 1
            goto L_0x0088
        L_0x00b8:
            int r4 = com.baidu.mapsdkplatform.comapi.map.ae.a
            int r6 = com.baidu.mapsdkplatform.comapi.map.ae.b
            android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createBitmap(r5, r4, r6, r7)
        L_0x00c2:
            if (r1 == 0) goto L_0x004b
            r1.a((android.graphics.Bitmap) r4)
            goto L_0x004b
        L_0x00c8:
            int r0 = r13.what
            r1 = 39
            r4 = 1099956224(0x41900000, float:18.0)
            if (r0 != r1) goto L_0x020c
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            if (r0 != 0) goto L_0x00d9
            return
        L_0x00d9:
            int r0 = r13.arg1
            r1 = 100
            if (r0 != r1) goto L_0x00e9
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            r13.B()
            goto L_0x014a
        L_0x00e9:
            int r0 = r13.arg1
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L_0x00f9
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            r13.L()
            goto L_0x014a
        L_0x00f9:
            int r0 = r13.arg1
            if (r0 != r3) goto L_0x010f
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.o r13 = r13.h
            if (r13 == 0) goto L_0x014a
        L_0x0105:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.o r13 = r13.h
            r13.a()
            goto L_0x014a
        L_0x010f:
            int r0 = r13.arg1
            if (r0 != 0) goto L_0x011c
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.o r13 = r13.h
            if (r13 == 0) goto L_0x014a
            goto L_0x0105
        L_0x011c:
            int r13 = r13.arg1
            r0 = 2
            if (r13 != r0) goto L_0x014a
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x012c
            return
        L_0x012c:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x0138:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x014a
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 == 0) goto L_0x0138
            r0.c()
            goto L_0x0138
        L_0x014a:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            boolean r13 = r13.k
            if (r13 != 0) goto L_0x0199
            int r13 = com.baidu.mapsdkplatform.comapi.map.ae.b
            if (r13 <= 0) goto L_0x0199
            int r13 = com.baidu.mapsdkplatform.comapi.map.ae.a
            if (r13 <= 0) goto L_0x0199
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            com.baidu.mapapi.model.inner.GeoPoint r13 = r13.b((int) r2, (int) r2)
            if (r13 == 0) goto L_0x0199
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            r13.k = r3
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x017b
            return
        L_0x017b:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x0187:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x0199
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 == 0) goto L_0x0187
            r0.b()
            goto L_0x0187
        L_0x0199:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x01a4
            return
        L_0x01a4:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x01b0:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x01c2
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 == 0) goto L_0x01b0
            r0.a()
            goto L_0x01b0
        L_0x01c2:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            boolean r13 = r13.q()
            if (r13 == 0) goto L_0x0301
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x01d9
            return
        L_0x01d9:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x01e5:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x0301
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 != 0) goto L_0x01f4
            goto L_0x01e5
        L_0x01f4:
            com.baidu.mapsdkplatform.comapi.map.ae r1 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r1 = r1.i
            com.baidu.mapsdkplatform.comapi.map.ad r1 = r1.E()
            float r1 = r1.a
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 < 0) goto L_0x0208
            r0.a((boolean) r3)
            goto L_0x01e5
        L_0x0208:
            r0.a((boolean) r2)
            goto L_0x01e5
        L_0x020c:
            int r0 = r13.what
            r1 = 41
            if (r0 != r1) goto L_0x0286
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            if (r13 != 0) goto L_0x021b
            return
        L_0x021b:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            boolean r13 = r13.n
            if (r13 != 0) goto L_0x022f
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            boolean r13 = r13.o
            if (r13 == 0) goto L_0x0301
        L_0x022f:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x023a
            return
        L_0x023a:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x0246:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x0301
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 != 0) goto L_0x0255
            goto L_0x0246
        L_0x0255:
            com.baidu.mapsdkplatform.comapi.map.ae r1 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r1 = r1.i
            com.baidu.mapsdkplatform.comapi.map.ad r1 = r1.E()
            r0.b((com.baidu.mapsdkplatform.comapi.map.ad) r1)
            com.baidu.mapsdkplatform.comapi.map.ae r1 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r1 = r1.i
            boolean r1 = r1.q()
            if (r1 == 0) goto L_0x0246
            com.baidu.mapsdkplatform.comapi.map.ae r1 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r1 = r1.i
            com.baidu.mapsdkplatform.comapi.map.ad r1 = r1.E()
            float r1 = r1.a
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 < 0) goto L_0x0282
            r0.a((boolean) r3)
            goto L_0x0246
        L_0x0282:
            r0.a((boolean) r2)
            goto L_0x0246
        L_0x0286:
            int r0 = r13.what
            r1 = 999(0x3e7, float:1.4E-42)
            if (r0 != r1) goto L_0x02b5
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            if (r13 != 0) goto L_0x0297
            return
        L_0x0297:
            com.baidu.mapsdkplatform.comapi.map.ae r13 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r13 = r13.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r13 = r13.h
            java.util.Iterator r13 = r13.iterator()
        L_0x02a3:
            boolean r0 = r13.hasNext()
            if (r0 == 0) goto L_0x0301
            java.lang.Object r0 = r13.next()
            com.baidu.mapsdkplatform.comapi.map.n r0 = (com.baidu.mapsdkplatform.comapi.map.n) r0
            if (r0 == 0) goto L_0x02a3
            r0.e()
            goto L_0x02a3
        L_0x02b5:
            int r0 = r13.what
            r1 = 50
            if (r0 != r1) goto L_0x0301
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            if (r0 != 0) goto L_0x02c6
            return
        L_0x02c6:
            com.baidu.mapsdkplatform.comapi.map.ae r0 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r0 = r0.i
            java.util.List<com.baidu.mapsdkplatform.comapi.map.n> r0 = r0.h
            java.util.Iterator r0 = r0.iterator()
        L_0x02d2:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0301
            java.lang.Object r1 = r0.next()
            com.baidu.mapsdkplatform.comapi.map.n r1 = (com.baidu.mapsdkplatform.comapi.map.n) r1
            if (r1 != 0) goto L_0x02e1
            goto L_0x02d2
        L_0x02e1:
            int r5 = r13.arg1
            if (r5 != 0) goto L_0x02e9
        L_0x02e5:
            r1.a((boolean) r2)
            goto L_0x02d2
        L_0x02e9:
            int r5 = r13.arg1
            if (r5 != r3) goto L_0x02d2
            com.baidu.mapsdkplatform.comapi.map.ae r5 = r12.a
            com.baidu.mapsdkplatform.comapi.map.e r5 = r5.i
            com.baidu.mapsdkplatform.comapi.map.ad r5 = r5.E()
            float r5 = r5.a
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 < 0) goto L_0x02e5
            r1.a((boolean) r3)
            goto L_0x02d2
        L_0x0301:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.map.af.handleMessage(android.os.Message):void");
    }
}
