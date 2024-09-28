package com.ta.utdid2.b.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.ta.utdid2.a.a.g;
import com.ta.utdid2.b.a.b;
import java.io.File;
import java.util.Map;

public class c {
    private SharedPreferences.Editor a;

    /* renamed from: a  reason: collision with other field name */
    private SharedPreferences f11a;

    /* renamed from: a  reason: collision with other field name */
    private b.a f12a;

    /* renamed from: a  reason: collision with other field name */
    private b f13a;

    /* renamed from: a  reason: collision with other field name */
    private d f14a;
    private String b = "";
    private String c = "";
    private boolean f = false;
    private boolean g = false;
    private boolean h = false;
    private boolean i;
    private Context mContext;

    /* JADX WARNING: Removed duplicated region for block: B:73:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:90:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public c(android.content.Context r10, java.lang.String r11, java.lang.String r12, boolean r13, boolean r14) {
        /*
            r9 = this;
            r9.<init>()
            java.lang.String r0 = ""
            r9.b = r0
            r9.c = r0
            r0 = 0
            r9.f = r0
            r9.g = r0
            r9.h = r0
            r1 = 0
            r9.f11a = r1
            r9.f13a = r1
            r9.a = r1
            r9.f12a = r1
            r9.mContext = r1
            r9.f14a = r1
            r9.i = r0
            r9.f = r13
            r9.i = r14
            r9.b = r12
            r9.c = r11
            r9.mContext = r10
            java.lang.String r13 = "t"
            r2 = 0
            if (r10 == 0) goto L_0x003d
            android.content.SharedPreferences r4 = r10.getSharedPreferences(r12, r0)
            r9.f11a = r4
            long r4 = r4.getLong(r13, r2)
            goto L_0x003e
        L_0x003d:
            r4 = r2
        L_0x003e:
            java.lang.String r1 = android.os.Environment.getExternalStorageState()     // Catch:{ Exception -> 0x0044 }
            goto L_0x0048
        L_0x0044:
            r6 = move-exception
            r6.printStackTrace()
        L_0x0048:
            boolean r6 = com.ta.utdid2.a.a.g.a((java.lang.String) r1)
            if (r6 == 0) goto L_0x0053
            r9.h = r0
            r9.g = r0
            goto L_0x0073
        L_0x0053:
            java.lang.String r6 = "mounted"
            boolean r6 = r1.equals(r6)
            r7 = 1
            if (r6 == 0) goto L_0x0061
            r9.h = r7
            r9.g = r7
            goto L_0x0073
        L_0x0061:
            java.lang.String r6 = "mounted_ro"
            boolean r1 = r1.equals(r6)
            if (r1 == 0) goto L_0x006f
            r9.g = r7
            r9.h = r0
            goto L_0x0073
        L_0x006f:
            r9.h = r0
            r9.g = r0
        L_0x0073:
            boolean r1 = r9.g
            java.lang.String r6 = "t2"
            if (r1 != 0) goto L_0x007d
            boolean r1 = r9.h
            if (r1 == 0) goto L_0x0152
        L_0x007d:
            if (r10 == 0) goto L_0x0152
            boolean r1 = com.ta.utdid2.a.a.g.a((java.lang.String) r11)
            if (r1 != 0) goto L_0x0152
            com.ta.utdid2.b.a.d r11 = r9.a((java.lang.String) r11)
            r9.f14a = r11
            if (r11 == 0) goto L_0x0152
            com.ta.utdid2.b.a.b r11 = r11.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014f }
            r9.f13a = r11     // Catch:{ Exception -> 0x014f }
            long r7 = r11.getLong(r13, r2)     // Catch:{ Exception -> 0x014f }
            if (r14 != 0) goto L_0x00d3
            int r11 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r11 <= 0) goto L_0x00af
            android.content.SharedPreferences r10 = r9.f11a     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014d }
            r9.a((android.content.SharedPreferences) r10, (com.ta.utdid2.b.a.b) r11)     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.d r10 = r9.f14a     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.b r10 = r10.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014d }
            r9.f13a = r10     // Catch:{ Exception -> 0x014d }
            goto L_0x0149
        L_0x00af:
            if (r11 >= 0) goto L_0x00c0
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014d }
            android.content.SharedPreferences r13 = r9.f11a     // Catch:{ Exception -> 0x014d }
            r9.a((com.ta.utdid2.b.a.b) r11, (android.content.SharedPreferences) r13)     // Catch:{ Exception -> 0x014d }
            android.content.SharedPreferences r10 = r10.getSharedPreferences(r12, r0)     // Catch:{ Exception -> 0x014d }
            r9.f11a = r10     // Catch:{ Exception -> 0x014d }
            goto L_0x0149
        L_0x00c0:
            if (r11 != 0) goto L_0x0149
            android.content.SharedPreferences r10 = r9.f11a     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014d }
            r9.a((android.content.SharedPreferences) r10, (com.ta.utdid2.b.a.b) r11)     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.d r10 = r9.f14a     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.b r10 = r10.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014d }
            r9.f13a = r10     // Catch:{ Exception -> 0x014d }
            goto L_0x0149
        L_0x00d3:
            android.content.SharedPreferences r11 = r9.f11a     // Catch:{ Exception -> 0x014d }
            long r13 = r11.getLong(r6, r2)     // Catch:{ Exception -> 0x014d }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            long r7 = r11.getLong(r6, r2)     // Catch:{ Exception -> 0x014a }
            int r11 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r11 >= 0) goto L_0x00f7
            int r1 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x00f7
            android.content.SharedPreferences r10 = r9.f11a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            r9.a((android.content.SharedPreferences) r10, (com.ta.utdid2.b.a.b) r11)     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.d r10 = r9.f14a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r10 = r10.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014a }
            r9.f13a = r10     // Catch:{ Exception -> 0x014a }
            goto L_0x0148
        L_0x00f7:
            if (r11 <= 0) goto L_0x010b
            int r1 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x010b
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            android.content.SharedPreferences r1 = r9.f11a     // Catch:{ Exception -> 0x014a }
            r9.a((com.ta.utdid2.b.a.b) r11, (android.content.SharedPreferences) r1)     // Catch:{ Exception -> 0x014a }
            android.content.SharedPreferences r10 = r10.getSharedPreferences(r12, r0)     // Catch:{ Exception -> 0x014a }
            r9.f11a = r10     // Catch:{ Exception -> 0x014a }
            goto L_0x0148
        L_0x010b:
            int r1 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r1 != 0) goto L_0x0121
            int r4 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0121
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            android.content.SharedPreferences r1 = r9.f11a     // Catch:{ Exception -> 0x014a }
            r9.a((com.ta.utdid2.b.a.b) r11, (android.content.SharedPreferences) r1)     // Catch:{ Exception -> 0x014a }
            android.content.SharedPreferences r10 = r10.getSharedPreferences(r12, r0)     // Catch:{ Exception -> 0x014a }
            r9.f11a = r10     // Catch:{ Exception -> 0x014a }
            goto L_0x0148
        L_0x0121:
            int r10 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x0137
            if (r1 <= 0) goto L_0x0137
            android.content.SharedPreferences r10 = r9.f11a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            r9.a((android.content.SharedPreferences) r10, (com.ta.utdid2.b.a.b) r11)     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.d r10 = r9.f14a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r10 = r10.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014a }
            r9.f13a = r10     // Catch:{ Exception -> 0x014a }
            goto L_0x0148
        L_0x0137:
            if (r11 != 0) goto L_0x0148
            android.content.SharedPreferences r10 = r9.f11a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r11 = r9.f13a     // Catch:{ Exception -> 0x014a }
            r9.a((android.content.SharedPreferences) r10, (com.ta.utdid2.b.a.b) r11)     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.d r10 = r9.f14a     // Catch:{ Exception -> 0x014a }
            com.ta.utdid2.b.a.b r10 = r10.a((java.lang.String) r12, (int) r0)     // Catch:{ Exception -> 0x014a }
            r9.f13a = r10     // Catch:{ Exception -> 0x014a }
        L_0x0148:
            r4 = r13
        L_0x0149:
            goto L_0x0153
        L_0x014a:
            r10 = move-exception
            r4 = r13
            goto L_0x0153
        L_0x014d:
            r10 = move-exception
            goto L_0x0153
        L_0x014f:
            r10 = move-exception
            r7 = r2
            goto L_0x0153
        L_0x0152:
            r7 = r2
        L_0x0153:
            int r10 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r10 != 0) goto L_0x015f
            int r10 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x0191
            int r10 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x0191
        L_0x015f:
            long r10 = java.lang.System.currentTimeMillis()
            boolean r12 = r9.i
            if (r12 == 0) goto L_0x0171
            if (r12 == 0) goto L_0x0191
            int r12 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r12 != 0) goto L_0x0191
            int r12 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r12 != 0) goto L_0x0191
        L_0x0171:
            android.content.SharedPreferences r12 = r9.f11a
            if (r12 == 0) goto L_0x017f
            android.content.SharedPreferences$Editor r12 = r12.edit()
            r12.putLong(r6, r10)
            r12.commit()
        L_0x017f:
            com.ta.utdid2.b.a.b r12 = r9.f13a     // Catch:{ Exception -> 0x0190 }
            if (r12 == 0) goto L_0x018f
            com.ta.utdid2.b.a.b r12 = r9.f13a     // Catch:{ Exception -> 0x0190 }
            com.ta.utdid2.b.a.b$a r12 = r12.a()     // Catch:{ Exception -> 0x0190 }
            r12.a((java.lang.String) r6, (long) r10)     // Catch:{ Exception -> 0x0190 }
            r12.commit()     // Catch:{ Exception -> 0x0190 }
        L_0x018f:
            goto L_0x0191
        L_0x0190:
            r10 = move-exception
        L_0x0191:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.b.a.c.<init>(android.content.Context, java.lang.String, java.lang.String, boolean, boolean):void");
    }

    private d a(String str) {
        File a2 = a(str);
        if (a2 == null) {
            return null;
        }
        d dVar = new d(a2.getAbsolutePath());
        this.f14a = dVar;
        return dVar;
    }

    /* renamed from: a  reason: collision with other method in class */
    private File m2a(String str) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory == null) {
            return null;
        }
        File file = new File(String.format("%s%s%s", new Object[]{externalStorageDirectory.getAbsolutePath(), File.separator, str}));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private void a(SharedPreferences sharedPreferences, b bVar) {
        b.a a2;
        if (sharedPreferences != null && bVar != null && (a2 = bVar.a()) != null) {
            a2.b();
            for (Map.Entry next : sharedPreferences.getAll().entrySet()) {
                String str = (String) next.getKey();
                Object value = next.getValue();
                if (value instanceof String) {
                    a2.a(str, (String) value);
                } else if (value instanceof Integer) {
                    a2.a(str, ((Integer) value).intValue());
                } else if (value instanceof Long) {
                    a2.a(str, ((Long) value).longValue());
                } else if (value instanceof Float) {
                    a2.a(str, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    a2.a(str, ((Boolean) value).booleanValue());
                }
            }
            try {
                a2.commit();
            } catch (Exception e) {
            }
        }
    }

    private void a(b bVar, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor edit;
        if (bVar != null && sharedPreferences != null && (edit = sharedPreferences.edit()) != null) {
            edit.clear();
            for (Map.Entry next : bVar.getAll().entrySet()) {
                String str = (String) next.getKey();
                Object value = next.getValue();
                if (value instanceof String) {
                    edit.putString(str, (String) value);
                } else if (value instanceof Integer) {
                    edit.putInt(str, ((Integer) value).intValue());
                } else if (value instanceof Long) {
                    edit.putLong(str, ((Long) value).longValue());
                } else if (value instanceof Float) {
                    edit.putFloat(str, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    edit.putBoolean(str, ((Boolean) value).booleanValue());
                }
            }
            edit.commit();
        }
    }

    private boolean c() {
        b bVar = this.f13a;
        if (bVar == null) {
            return false;
        }
        boolean b2 = bVar.b();
        if (!b2) {
            commit();
        }
        return b2;
    }

    private void b() {
        b bVar;
        SharedPreferences sharedPreferences;
        if (this.a == null && (sharedPreferences = this.f11a) != null) {
            this.a = sharedPreferences.edit();
        }
        if (this.h && this.f12a == null && (bVar = this.f13a) != null) {
            this.f12a = bVar.a();
        }
        c();
    }

    public void putString(String key, String value) {
        if (!g.a(key) && !key.equals("t")) {
            b();
            SharedPreferences.Editor editor = this.a;
            if (editor != null) {
                editor.putString(key, value);
            }
            b.a aVar = this.f12a;
            if (aVar != null) {
                aVar.a(key, value);
            }
        }
    }

    public void remove(String key) {
        if (!g.a(key) && !key.equals("t")) {
            b();
            SharedPreferences.Editor editor = this.a;
            if (editor != null) {
                editor.remove(key);
            }
            b.a aVar = this.f12a;
            if (aVar != null) {
                aVar.a(key);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0042  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean commit() {
        /*
            r6 = this;
            long r0 = java.lang.System.currentTimeMillis()
            android.content.SharedPreferences$Editor r2 = r6.a
            r3 = 0
            if (r2 == 0) goto L_0x0021
            boolean r4 = r6.i
            if (r4 != 0) goto L_0x0017
            android.content.SharedPreferences r4 = r6.f11a
            if (r4 == 0) goto L_0x0017
            java.lang.String r4 = "t"
            r2.putLong(r4, r0)
        L_0x0017:
            android.content.SharedPreferences$Editor r0 = r6.a
            boolean r0 = r0.commit()
            if (r0 != 0) goto L_0x0021
            r0 = 0
            goto L_0x0022
        L_0x0021:
            r0 = 1
        L_0x0022:
            android.content.SharedPreferences r1 = r6.f11a
            if (r1 == 0) goto L_0x0032
            android.content.Context r1 = r6.mContext
            if (r1 == 0) goto L_0x0032
            java.lang.String r2 = r6.b
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)
            r6.f11a = r1
        L_0x0032:
            r1 = 0
            java.lang.String r1 = android.os.Environment.getExternalStorageState()     // Catch:{ Exception -> 0x0038 }
            goto L_0x003c
        L_0x0038:
            r2 = move-exception
            r2.printStackTrace()
        L_0x003c:
            boolean r2 = com.ta.utdid2.a.a.g.a((java.lang.String) r1)
            if (r2 != 0) goto L_0x00a8
            java.lang.String r2 = "mounted"
            boolean r4 = r1.equals(r2)
            if (r4 == 0) goto L_0x0086
            com.ta.utdid2.b.a.b r4 = r6.f13a
            if (r4 != 0) goto L_0x0076
            java.lang.String r4 = r6.c
            com.ta.utdid2.b.a.d r4 = r6.a((java.lang.String) r4)
            if (r4 == 0) goto L_0x0075
            java.lang.String r5 = r6.b
            com.ta.utdid2.b.a.b r4 = r4.a((java.lang.String) r5, (int) r3)
            r6.f13a = r4
            boolean r5 = r6.i
            if (r5 != 0) goto L_0x0068
            android.content.SharedPreferences r5 = r6.f11a
            r6.a((android.content.SharedPreferences) r5, (com.ta.utdid2.b.a.b) r4)
            goto L_0x006d
        L_0x0068:
            android.content.SharedPreferences r5 = r6.f11a
            r6.a((com.ta.utdid2.b.a.b) r4, (android.content.SharedPreferences) r5)
        L_0x006d:
            com.ta.utdid2.b.a.b r4 = r6.f13a
            com.ta.utdid2.b.a.b$a r4 = r4.a()
            r6.f12a = r4
        L_0x0075:
            goto L_0x0086
        L_0x0076:
            com.ta.utdid2.b.a.b$a r4 = r6.f12a     // Catch:{ Exception -> 0x0084 }
            if (r4 == 0) goto L_0x0083
            com.ta.utdid2.b.a.b$a r4 = r6.f12a     // Catch:{ Exception -> 0x0084 }
            boolean r4 = r4.commit()     // Catch:{ Exception -> 0x0084 }
            if (r4 != 0) goto L_0x0083
            r0 = 0
        L_0x0083:
            goto L_0x0086
        L_0x0084:
            r0 = move-exception
            r0 = 0
        L_0x0086:
            boolean r2 = r1.equals(r2)
            if (r2 != 0) goto L_0x0098
            java.lang.String r2 = "mounted_ro"
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x00a8
            com.ta.utdid2.b.a.b r1 = r6.f13a
            if (r1 == 0) goto L_0x00a8
        L_0x0098:
            com.ta.utdid2.b.a.d r1 = r6.f14a     // Catch:{ Exception -> 0x00a7 }
            if (r1 == 0) goto L_0x00a6
            com.ta.utdid2.b.a.d r1 = r6.f14a     // Catch:{ Exception -> 0x00a7 }
            java.lang.String r2 = r6.b     // Catch:{ Exception -> 0x00a7 }
            com.ta.utdid2.b.a.b r1 = r1.a((java.lang.String) r2, (int) r3)     // Catch:{ Exception -> 0x00a7 }
            r6.f13a = r1     // Catch:{ Exception -> 0x00a7 }
        L_0x00a6:
            goto L_0x00a8
        L_0x00a7:
            r1 = move-exception
        L_0x00a8:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.b.a.c.commit():boolean");
    }

    public String getString(String key) {
        c();
        SharedPreferences sharedPreferences = this.f11a;
        if (sharedPreferences != null) {
            String string = sharedPreferences.getString(key, "");
            if (!g.a(string)) {
                return string;
            }
        }
        b bVar = this.f13a;
        if (bVar != null) {
            return bVar.getString(key, "");
        }
        return "";
    }
}
