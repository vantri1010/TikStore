package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.provider.Settings;
import com.stripe.android.model.Card;

class PhoneMisc {
    private Context a = null;

    private native int get_inode(String str);

    PhoneMisc(Context context) {
        this.a = context;
    }

    static {
        System.loadLibrary("yunceng");
    }

    public String a() {
        try {
            return Settings.Secure.getString(this.a.getContentResolver(), "android_id");
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String b() {
        try {
            return Settings.Secure.getInt(this.a.getContentResolver(), "adb_enabled", 0) > 0 ? "1" : "0";
        } catch (Exception e) {
            return "0";
        }
    }

    public String c() {
        try {
            return this.a.getResources().getConfiguration().locale.getLanguage();
        } catch (Exception e) {
            return "cn";
        }
    }

    public String d() {
        try {
            int num = this.a.getPackageManager().getInstalledPackages(0).size();
            return "" + ((int) (Math.log((double) num) / Math.log(2.0d)));
        } catch (Exception e) {
            return "0";
        }
    }

    public String e() {
        return String.format("%d.%d.%d", new Object[]{Integer.valueOf(get_inode("/etc/hosts")), Integer.valueOf(get_inode("/root")), Integer.valueOf(get_inode(this.a.getPackageCodePath()))});
    }
}
