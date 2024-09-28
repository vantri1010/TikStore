package com.aliyun.security.yunceng.android.sdk.umid;

import android.content.Context;
import android.os.Process;
import android.telephony.TelephonyManager;
import com.stripe.android.model.Card;

class h {
    private TelephonyManager a = null;
    private boolean b = false;

    h(Context ct) {
        if (ct.checkPermission("android.permission.READ_PHONE_STATE", Process.myPid(), Process.myUid()) != -1) {
            TelephonyManager telephonyManager = (TelephonyManager) ct.getSystemService("phone");
            this.a = telephonyManager;
            if (telephonyManager != null) {
                this.b = true;
            }
        }
    }

    public String a() {
        return Card.UNKNOWN;
    }

    public String b() {
        try {
            return this.b ? this.a.getDeviceId() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String c() {
        try {
            return this.b ? this.a.getSimSerialNumber() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String d() {
        try {
            return this.b ? this.a.getSubscriberId() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String e() {
        try {
            return this.b ? this.a.getDeviceSoftwareVersion() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String f() {
        try {
            return this.b ? this.a.getNetworkCountryIso() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String g() {
        try {
            return this.b ? this.a.getNetworkOperatorName() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String h() {
        try {
            return this.b ? this.a.getNetworkOperator() : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String i() {
        try {
            if (!this.b) {
                return Card.UNKNOWN;
            }
            return "" + this.a.getNetworkType();
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }
}
