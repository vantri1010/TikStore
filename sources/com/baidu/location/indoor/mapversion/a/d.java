package com.baidu.location.indoor.mapversion.a;

import android.text.TextUtils;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;

public class d {
    private HashMap<String, c> a;
    private HashSet<a> b;
    /* access modifiers changed from: private */
    public File c;
    /* access modifiers changed from: private */
    public boolean d;
    /* access modifiers changed from: private */
    public String e;
    /* access modifiers changed from: private */
    public String f;

    public interface a {
        void a(boolean z);
    }

    private void a(boolean z) {
        Iterator<a> it = this.b.iterator();
        while (it.hasNext()) {
            it.next().a(z);
        }
        this.d = false;
    }

    private boolean b(String str) {
        if (str != null) {
            try {
                if (!str.equalsIgnoreCase("")) {
                    JSONArray optJSONArray = new JSONObject(str).optJSONArray("item");
                    this.a = new HashMap<>();
                    for (int i = 0; i < optJSONArray.length(); i++) {
                        c cVar = new c(optJSONArray.getJSONObject(i));
                        if (cVar.b() != null) {
                            if (cVar.c() != null) {
                                this.a.put(c.a(cVar.c()), cVar);
                            }
                        }
                    }
                    return true;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    private void c() {
        new Thread(new e(this)).start();
    }

    /* access modifiers changed from: private */
    public boolean d() {
        try {
            File file = new File(this.c, this.e);
            long currentTimeMillis = System.currentTimeMillis();
            file.lastModified();
            if (file.exists()) {
                if (currentTimeMillis - file.lastModified() <= 604800000) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    this.f = bufferedReader.readLine();
                    while (true) {
                        String readLine = bufferedReader.readLine();
                        if (readLine != null) {
                            sb.append(readLine);
                            sb.append("\n");
                        } else {
                            bufferedReader.close();
                            a(b(new String(Base64.decode(sb.toString(), 0))));
                            this.d = false;
                            return true;
                        }
                    }
                }
            }
            this.d = false;
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        } finally {
            this.d = false;
        }
    }

    public c a(String str, String str2) {
        String str3;
        if (this.a == null || (str3 = this.e) == null || !str.equalsIgnoreCase(str3)) {
            return null;
        }
        return this.a.get(c.a(str2));
    }

    public void a(String str) {
        if (!this.d) {
            this.d = true;
            if (TextUtils.isEmpty(str)) {
                a(false);
                return;
            }
            String str2 = this.e;
            if (str2 == null || !str.equalsIgnoreCase(str2) || !a()) {
                this.e = str;
                this.f = null;
                if (!d()) {
                    c();
                    return;
                }
                return;
            }
            a(true);
        }
    }

    public boolean a() {
        HashMap<String, c> hashMap = this.a;
        return hashMap != null && hashMap.size() > 0;
    }

    public String b() {
        if (!a()) {
            return "A001";
        }
        Object[] array = this.a.keySet().toArray();
        return (array.length <= 0 || this.a.get(array[0].toString()) == null) ? "A001" : this.a.get(array[0].toString()).c();
    }
}
