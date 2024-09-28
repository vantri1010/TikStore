package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.aliyun.security.yunceng.android.sdk.YunCeng;

public class d {
    private Context a = null;
    private final int b = 0;
    private final int c = 1;
    private final int d = 2;
    private final int e = 3;
    private final int f = 4;
    private int g = 0;

    public d(Context context) {
        this.a = context;
    }

    private void f() {
        YunCeng.reportInfo(21, "virtual", "" + a(), 0);
    }

    private Bitmap a(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private String a(ApplicationInfo app) {
        return app.loadLabel(this.a.getPackageManager()).toString();
    }

    private Bitmap b(ApplicationInfo app) {
        return a(app.loadIcon(this.a.getPackageManager()));
    }

    private boolean a(PackageInfo other) {
        if (other.sharedUserId == null) {
            return true;
        }
        try {
            if (this.a.getPackageManager().getPackageInfo(this.a.getPackageName(), 0).sharedUserId == null) {
                return true;
            }
            return false;
        } catch (Exception e2) {
        }
    }

    public int a() {
        return this.g;
    }

    public boolean b() {
        try {
            if (c()) {
                f();
            }
            if (d()) {
                f();
            }
            if (!e() && this.g == 0) {
                return false;
            }
            return true;
        } catch (Exception e2) {
            return false;
        }
    }

    public boolean c() {
        boolean ret = this.a.checkPermission("com.google.xxxxxx", Process.myPid(), Process.myUid()) == 0;
        if (ret) {
            this.g = 1;
        }
        return ret;
    }

    public boolean d() {
        int system_app_num = 0;
        boolean ret = false;
        try {
            for (PackageInfo pack : this.a.getPackageManager().getInstalledPackages(0)) {
                if ((1 & pack.applicationInfo.flags) != 0) {
                    system_app_num++;
                }
            }
            if (system_app_num < 30) {
                ret = true;
            }
            if (ret) {
                this.g = 2;
            }
            return ret;
        } catch (Exception e2) {
            return false;
        }
    }

    public boolean e() {
        ApplicationInfo self_app = this.a.getApplicationInfo();
        String self_label = a(self_app);
        Bitmap self_bitmap = b(self_app);
        try {
            for (PackageInfo pack : this.a.getPackageManager().getInstalledPackages(0)) {
                ApplicationInfo app = pack.applicationInfo;
                if ((app.flags & 1) == 0) {
                    String app_label = a(app);
                    Bitmap app_bitmap = b(app);
                    if (self_app.uid != app.uid || self_label != app_label || !self_bitmap.sameAs(app_bitmap)) {
                        if (self_app.uid != app.uid || !a(pack)) {
                            if (self_label != app_label) {
                                if (self_bitmap.sameAs(app_bitmap)) {
                                }
                            }
                            this.g = 4;
                            f();
                            return true;
                        }
                        this.g = 3;
                        f();
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e2) {
            return false;
        }
    }
}
