package com.baidu.mapsdkplatform.comapi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.storage.StorageManager;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class g {
    private static volatile g a = null;
    private boolean b = false;
    private boolean c = true;
    private final List<f> d = new ArrayList();
    private f e = null;
    private String f;

    private g() {
    }

    public static g a() {
        if (a == null) {
            synchronized (g.class) {
                if (a == null) {
                    a = new g();
                }
            }
        }
        return a;
    }

    private boolean a(String str) {
        boolean z = false;
        try {
            File file = new File(str + "/test.0");
            if (file.exists()) {
                file.delete();
            }
            z = file.createNewFile();
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return z;
    }

    private void c(Context context) {
        String str;
        boolean z;
        Object[] objArr;
        Context context2 = context;
        try {
            StorageManager storageManager = (StorageManager) context2.getSystemService("storage");
            Method method = storageManager.getClass().getMethod("getVolumeList", new Class[0]);
            int i = 1;
            Method method2 = storageManager.getClass().getMethod("getVolumeState", new Class[]{String.class});
            Class<?> cls = Class.forName("android.os.storage.StorageVolume");
            Method method3 = cls.getMethod("isRemovable", new Class[0]);
            Method method4 = cls.getMethod("getPath", new Class[0]);
            Object[] objArr2 = (Object[]) method.invoke(storageManager, new Object[0]);
            if (objArr2 != null) {
                int length = objArr2.length;
                int i2 = 0;
                while (true) {
                    str = "外置存储卡";
                    if (i2 >= length) {
                        break;
                    }
                    Object obj = objArr2[i2];
                    String str2 = (String) method4.invoke(obj, new Object[0]);
                    if (str2 == null || str2.length() <= 0) {
                        objArr = objArr2;
                    } else {
                        objArr = objArr2;
                        Object[] objArr3 = new Object[i];
                        objArr3[0] = str2;
                        if ("mounted".equals(method2.invoke(storageManager, objArr3))) {
                            boolean z2 = !((Boolean) method3.invoke(obj, new Object[0])).booleanValue();
                            if (Build.VERSION.SDK_INT <= 19 && a(str2)) {
                                List<f> list = this.d;
                                boolean z3 = !z2;
                                if (z2) {
                                    str = "内置存储卡";
                                }
                                list.add(new f(str2, z3, str, context2));
                            } else if (Build.VERSION.SDK_INT >= 19) {
                                if (new File(str2 + File.separator + "BaiduMapSDKNew").exists() && str2.equals(context2.getSharedPreferences("map_pref", 0).getString("PREFFERED_SD_CARD", ""))) {
                                    this.f = str2 + File.separator + "BaiduMapSDKNew";
                                }
                            }
                        }
                    }
                    i2++;
                    objArr2 = objArr;
                    i = 1;
                }
                if (Build.VERSION.SDK_INT >= 19) {
                    File[] externalFilesDirs = context2.getExternalFilesDirs((String) null);
                    ArrayList arrayList = new ArrayList();
                    arrayList.addAll(this.d);
                    int i3 = 0;
                    while (true) {
                        if (i3 >= externalFilesDirs.length) {
                            break;
                        } else if (externalFilesDirs[i3] == null) {
                            break;
                        } else {
                            String absolutePath = externalFilesDirs[i3].getAbsolutePath();
                            Iterator<f> it = this.d.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (absolutePath.startsWith(it.next().a())) {
                                        z = true;
                                        break;
                                    }
                                } else {
                                    z = false;
                                    break;
                                }
                            }
                            String str3 = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).packageName;
                            if (str3 != null && !z && absolutePath.indexOf(str3) != -1) {
                                arrayList.add(new f(absolutePath, true, str, context2));
                            }
                            i3++;
                        }
                    }
                    this.d.clear();
                    this.d.addAll(arrayList);
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:85:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void d(android.content.Context r13) {
        /*
            r12 = this;
            java.lang.String r0 = ":"
            java.lang.String r1 = "Auto"
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r4 = 0
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r6 = "/proc/mounts"
            r5.<init>(r6)     // Catch:{ Exception -> 0x00f5 }
            boolean r6 = r5.exists()     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r7 = " "
            r8 = 32
            r9 = 9
            if (r6 == 0) goto L_0x0059
            java.util.Scanner r6 = new java.util.Scanner     // Catch:{ Exception -> 0x00f5 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x00f5 }
        L_0x0027:
            boolean r5 = r6.hasNext()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r5 == 0) goto L_0x004d
            java.lang.String r5 = r6.nextLine()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            java.lang.String r10 = "/dev/block/vold/"
            boolean r10 = r5.startsWith(r10)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r10 == 0) goto L_0x0027
            java.lang.String r5 = r5.replace(r9, r8)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            java.lang.String[] r5 = r5.split(r7)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r5 == 0) goto L_0x0027
            int r10 = r5.length     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r10 <= 0) goto L_0x0027
            r10 = 1
            r5 = r5[r10]     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            r2.add(r5)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            goto L_0x0027
        L_0x004d:
            r6.close()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            goto L_0x0059
        L_0x0051:
            r13 = move-exception
            r4 = r6
            goto L_0x00ff
        L_0x0055:
            r13 = move-exception
            r4 = r6
            goto L_0x00f6
        L_0x0059:
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r6 = "/system/etc/vold.fstab"
            r5.<init>(r6)     // Catch:{ Exception -> 0x00f5 }
            boolean r6 = r5.exists()     // Catch:{ Exception -> 0x00f5 }
            r10 = 0
            if (r6 == 0) goto L_0x00a3
            java.util.Scanner r6 = new java.util.Scanner     // Catch:{ Exception -> 0x00f5 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x00f5 }
        L_0x006c:
            boolean r5 = r6.hasNext()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r5 == 0) goto L_0x00a0
            java.lang.String r5 = r6.nextLine()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            java.lang.String r11 = "dev_mount"
            boolean r11 = r5.startsWith(r11)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r11 == 0) goto L_0x006c
            java.lang.String r5 = r5.replace(r9, r8)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            java.lang.String[] r5 = r5.split(r7)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r5 == 0) goto L_0x006c
            int r11 = r5.length     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r11 <= 0) goto L_0x006c
            r11 = 2
            r5 = r5[r11]     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            boolean r11 = r5.contains(r0)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            if (r11 == 0) goto L_0x009c
            int r11 = r5.indexOf(r0)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            java.lang.String r5 = r5.substring(r10, r11)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
        L_0x009c:
            r3.add(r5)     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
            goto L_0x006c
        L_0x00a0:
            r6.close()     // Catch:{ Exception -> 0x0055, all -> 0x0051 }
        L_0x00a3:
            java.io.File r0 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x00f5 }
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r5 = r12.d     // Catch:{ Exception -> 0x00f5 }
            com.baidu.mapsdkplatform.comapi.util.f r6 = new com.baidu.mapsdkplatform.comapi.util.f     // Catch:{ Exception -> 0x00f5 }
            r6.<init>(r0, r10, r1, r13)     // Catch:{ Exception -> 0x00f5 }
            r5.add(r6)     // Catch:{ Exception -> 0x00f5 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x00f5 }
        L_0x00b9:
            boolean r5 = r2.hasNext()     // Catch:{ Exception -> 0x00f5 }
            if (r5 == 0) goto L_0x00fe
            java.lang.Object r5 = r2.next()     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x00f5 }
            boolean r6 = r3.contains(r5)     // Catch:{ Exception -> 0x00f5 }
            if (r6 == 0) goto L_0x00b9
            boolean r6 = r5.equals(r0)     // Catch:{ Exception -> 0x00f5 }
            if (r6 != 0) goto L_0x00b9
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x00f5 }
            r6.<init>(r5)     // Catch:{ Exception -> 0x00f5 }
            boolean r7 = r6.exists()     // Catch:{ Exception -> 0x00f5 }
            if (r7 == 0) goto L_0x00b9
            boolean r7 = r6.isDirectory()     // Catch:{ Exception -> 0x00f5 }
            if (r7 == 0) goto L_0x00b9
            boolean r6 = r6.canWrite()     // Catch:{ Exception -> 0x00f5 }
            if (r6 == 0) goto L_0x00b9
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r6 = r12.d     // Catch:{ Exception -> 0x00f5 }
            com.baidu.mapsdkplatform.comapi.util.f r7 = new com.baidu.mapsdkplatform.comapi.util.f     // Catch:{ Exception -> 0x00f5 }
            r7.<init>(r5, r10, r1, r13)     // Catch:{ Exception -> 0x00f5 }
            r6.add(r7)     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00b9
        L_0x00f3:
            r13 = move-exception
            goto L_0x00ff
        L_0x00f5:
            r13 = move-exception
        L_0x00f6:
            r13.printStackTrace()     // Catch:{ all -> 0x00f3 }
            if (r4 == 0) goto L_0x00fe
            r4.close()
        L_0x00fe:
            return
        L_0x00ff:
            if (r4 == 0) goto L_0x0104
            r4.close()
        L_0x0104:
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.util.g.d(android.content.Context):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x009e A[Catch:{ Exception -> 0x00a9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(android.content.Context r9) {
        /*
            r8 = this;
            boolean r0 = r8.b
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            r0 = 1
            r8.b = r0
            r1 = 0
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0033 }
            r3 = 29
            if (r2 < r3) goto L_0x0025
            r8.c = r1     // Catch:{ Exception -> 0x0033 }
            com.baidu.mapsdkplatform.comapi.util.f r2 = new com.baidu.mapsdkplatform.comapi.util.f     // Catch:{ Exception -> 0x0033 }
            r2.<init>(r9)     // Catch:{ Exception -> 0x0033 }
            r8.e = r2     // Catch:{ Exception -> 0x0033 }
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r2 = r8.d     // Catch:{ Exception -> 0x0033 }
            r2.clear()     // Catch:{ Exception -> 0x0033 }
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r2 = r8.d     // Catch:{ Exception -> 0x0033 }
            com.baidu.mapsdkplatform.comapi.util.f r3 = r8.e     // Catch:{ Exception -> 0x0033 }
            r2.add(r3)     // Catch:{ Exception -> 0x0033 }
            return
        L_0x0025:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0033 }
            r3 = 14
            if (r2 < r3) goto L_0x002f
            r8.c(r9)     // Catch:{ Exception -> 0x0033 }
            goto L_0x0037
        L_0x002f:
            r8.d(r9)     // Catch:{ Exception -> 0x0033 }
            goto L_0x0037
        L_0x0033:
            r2 = move-exception
            r2.printStackTrace()
        L_0x0037:
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r2 = r8.d     // Catch:{ Exception -> 0x00a9 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x00a9 }
            if (r2 <= 0) goto L_0x00ad
            r2 = 0
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r3 = r8.d     // Catch:{ Exception -> 0x00a9 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x00a9 }
            r4 = 0
        L_0x0047:
            boolean r5 = r3.hasNext()     // Catch:{ Exception -> 0x00a9 }
            if (r5 == 0) goto L_0x0066
            java.lang.Object r5 = r3.next()     // Catch:{ Exception -> 0x00a9 }
            com.baidu.mapsdkplatform.comapi.util.f r5 = (com.baidu.mapsdkplatform.comapi.util.f) r5     // Catch:{ Exception -> 0x00a9 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r7 = r5.b()     // Catch:{ Exception -> 0x00a9 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x00a9 }
            boolean r6 = r6.exists()     // Catch:{ Exception -> 0x00a9 }
            if (r6 == 0) goto L_0x0047
            int r4 = r4 + 1
            r2 = r5
            goto L_0x0047
        L_0x0066:
            if (r4 != 0) goto L_0x008b
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.b(r9)     // Catch:{ Exception -> 0x00a9 }
            r8.e = r0     // Catch:{ Exception -> 0x00a9 }
            if (r0 != 0) goto L_0x009a
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r0 = r8.d     // Catch:{ Exception -> 0x00a9 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x00a9 }
        L_0x0076:
            boolean r2 = r0.hasNext()     // Catch:{ Exception -> 0x00a9 }
            if (r2 == 0) goto L_0x009a
            java.lang.Object r2 = r0.next()     // Catch:{ Exception -> 0x00a9 }
            com.baidu.mapsdkplatform.comapi.util.f r2 = (com.baidu.mapsdkplatform.comapi.util.f) r2     // Catch:{ Exception -> 0x00a9 }
            boolean r3 = r8.a(r9, r2)     // Catch:{ Exception -> 0x00a9 }
            if (r3 == 0) goto L_0x0076
        L_0x0088:
            r8.e = r2     // Catch:{ Exception -> 0x00a9 }
            goto L_0x009a
        L_0x008b:
            if (r4 != r0) goto L_0x0094
            boolean r0 = r8.a(r9, r2)     // Catch:{ Exception -> 0x00a9 }
            if (r0 == 0) goto L_0x009a
            goto L_0x0088
        L_0x0094:
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.b(r9)     // Catch:{ Exception -> 0x00a9 }
            r8.e = r0     // Catch:{ Exception -> 0x00a9 }
        L_0x009a:
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x00a9 }
            if (r0 != 0) goto L_0x00ad
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r0 = r8.d     // Catch:{ Exception -> 0x00a9 }
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x00a9 }
            com.baidu.mapsdkplatform.comapi.util.f r0 = (com.baidu.mapsdkplatform.comapi.util.f) r0     // Catch:{ Exception -> 0x00a9 }
            r8.e = r0     // Catch:{ Exception -> 0x00a9 }
            goto L_0x00ad
        L_0x00a9:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00ad:
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x010c }
            if (r0 == 0) goto L_0x00f6
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x010c }
            java.lang.String r0 = r0.a()     // Catch:{ Exception -> 0x010c }
            boolean r0 = r8.a((java.lang.String) r0)     // Catch:{ Exception -> 0x010c }
            if (r0 == 0) goto L_0x00f6
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x010c }
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x010c }
            java.lang.String r0 = r0.b()     // Catch:{ Exception -> 0x010c }
            r9.<init>(r0)     // Catch:{ Exception -> 0x010c }
            boolean r0 = r9.exists()     // Catch:{ Exception -> 0x010c }
            if (r0 != 0) goto L_0x00d1
            r9.mkdirs()     // Catch:{ Exception -> 0x010c }
        L_0x00d1:
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x010c }
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x010c }
            java.lang.String r0 = r0.c()     // Catch:{ Exception -> 0x010c }
            r9.<init>(r0)     // Catch:{ Exception -> 0x010c }
            boolean r0 = r9.exists()     // Catch:{ Exception -> 0x010c }
            if (r0 != 0) goto L_0x00e5
            r9.mkdirs()     // Catch:{ Exception -> 0x010c }
        L_0x00e5:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x010c }
            java.lang.String r1 = ".nomedia"
            r0.<init>(r9, r1)     // Catch:{ Exception -> 0x010c }
            boolean r9 = r0.exists()     // Catch:{ Exception -> 0x010c }
            if (r9 != 0) goto L_0x0110
            r0.createNewFile()     // Catch:{ Exception -> 0x010c }
            goto L_0x0110
        L_0x00f6:
            r8.c = r1     // Catch:{ Exception -> 0x010c }
            com.baidu.mapsdkplatform.comapi.util.f r0 = new com.baidu.mapsdkplatform.comapi.util.f     // Catch:{ Exception -> 0x010c }
            r0.<init>(r9)     // Catch:{ Exception -> 0x010c }
            r8.e = r0     // Catch:{ Exception -> 0x010c }
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r9 = r8.d     // Catch:{ Exception -> 0x010c }
            r9.clear()     // Catch:{ Exception -> 0x010c }
            java.util.List<com.baidu.mapsdkplatform.comapi.util.f> r9 = r8.d     // Catch:{ Exception -> 0x010c }
            com.baidu.mapsdkplatform.comapi.util.f r0 = r8.e     // Catch:{ Exception -> 0x010c }
            r9.add(r0)     // Catch:{ Exception -> 0x010c }
            goto L_0x0110
        L_0x010c:
            r9 = move-exception
            r9.printStackTrace()
        L_0x0110:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkplatform.comapi.util.g.a(android.content.Context):void");
    }

    public boolean a(Context context, f fVar) {
        String a2 = fVar.a();
        if (!a(a2)) {
            return false;
        }
        SharedPreferences.Editor edit = context.getSharedPreferences("map_pref", 0).edit();
        edit.putString("PREFFERED_SD_CARD", a2);
        return edit.commit();
    }

    public f b() {
        return this.e;
    }

    public f b(Context context) {
        String string = context.getSharedPreferences("map_pref", 0).getString("PREFFERED_SD_CARD", "");
        if (string == null || string.length() <= 0) {
            return null;
        }
        for (f next : this.d) {
            if (next.a().equals(string)) {
                return next;
            }
        }
        return null;
    }
}
