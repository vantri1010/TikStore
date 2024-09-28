package io.openinstall.sdk;

import android.content.SharedPreferences;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class f {
    private Future<SharedPreferences> a;
    private final String b = "";

    public f(Future<SharedPreferences> future) {
        this.a = future;
    }

    public g a() {
        try {
            return g.b(this.a.get().getString("FM_config_data", ""));
        } catch (InterruptedException | ExecutionException e) {
            return new g();
        }
    }

    public String a(String str) {
        try {
            return this.a.get().getString(str, "");
        } catch (InterruptedException | ExecutionException e) {
            return "";
        }
    }

    public void a(al alVar) {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.putString("FM_pb_data", alVar == null ? "" : alVar.d());
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    public void a(g gVar) {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.putString("FM_config_data", gVar.i());
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    public void a(String str, long j) {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.putLong(str, j);
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    public void a(String str, e eVar) {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.putInt(str, eVar.a());
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    public void a(String str, String str2) {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.putString(str, str2);
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }

    public long b(String str) {
        try {
            return this.a.get().getLong(str, 0);
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    public al b() {
        try {
            return al.c(this.a.get().getString("FM_pb_data", ""));
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    public e c(String str) {
        try {
            return e.a(this.a.get().getInt(str, e.a.a()));
        } catch (InterruptedException | ExecutionException e) {
            return e.a;
        }
    }

    public void c() {
        try {
            SharedPreferences.Editor edit = this.a.get().edit();
            edit.clear();
            edit.apply();
        } catch (InterruptedException | ExecutionException e) {
        }
    }
}
