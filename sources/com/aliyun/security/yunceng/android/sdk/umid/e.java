package com.aliyun.security.yunceng.android.sdk.umid;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Process;
import android.provider.Settings;
import com.stripe.android.model.Card;

class e {
    private boolean a = false;
    private BluetoothAdapter b = null;
    private Context c = null;

    e(Context context) {
        this.c = context;
        if (context.checkPermission("android.permission.BLUETOOTH", Process.myPid(), Process.myUid()) != -1) {
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.b = defaultAdapter;
            if (defaultAdapter != null) {
                this.a = true;
            }
        }
    }

    public String a() {
        try {
            return this.a ? this.b.getName().replace('#', '-') : Card.UNKNOWN;
        } catch (Exception e) {
            return Card.UNKNOWN;
        }
    }

    public String b() {
        try {
            return Settings.Secure.getString(this.c.getContentResolver(), "bluetooth_address");
        } catch (Exception e) {
            return "00";
        }
    }
}
