package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import com.stripe.android.model.Card;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class g {
    private String a = "";
    private Context b = null;

    g(Context context) {
        this.b = context;
        this.a = context.getPackageCodePath();
    }

    public String a() {
        return "Appname " + b() + "\nmodify time " + c() + "\n";
    }

    public String b() {
        try {
            return this.b.getPackageName().replace('#', '-');
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String c() {
        File f = new File(this.a);
        try {
            new FileInputStream(this.a);
            return new SimpleDateFormat("yy/MM/dd HH:mm").format(new Date(f.lastModified()));
        } catch (Exception e) {
            return "00/00/00 00:00";
        }
    }
}
