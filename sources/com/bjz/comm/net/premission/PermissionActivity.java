package com.bjz.comm.net.premission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.R;
import java.util.ArrayList;
import java.util.List;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "PermissionLog";
    private List<String> addTexts = new ArrayList();
    @SuppressLint({"HandlerLeak"})
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 11) {
                PermissionActivity.this.onBackPressed();
            }
        }
    };
    private boolean isFirst = true;
    private int permissionSize = 0;
    private String[] permissions;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.permissions = savedInstanceState.getStringArray("permissions");
        }
        initPermission(getIntent());
    }

    private void initPermission(Intent intent) {
        if (this.permissions == null) {
            this.permissions = intent.getStringArrayExtra("permissions");
        }
        EasyPermissions.requestPermissions((Activity) this, getCurPermission(), 10, this.permissions);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (BuildVars.DEBUG_VERSION) {
            Log.e(TAG, getClass().getSimpleName() + " ===> onResume");
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initPermission(intent);
    }

    @SuppressLint({"StringFormatInvalid"})
    private String getCurPermission() {
        this.addTexts.clear();
        this.permissionSize = 0;
        StringBuilder builder = new StringBuilder();
        for (String value : this.permissions) {
            String per = getPermission(value);
            if (!this.addTexts.contains(per)) {
                this.addTexts.add(per);
                builder.append(per);
                builder.append("-");
                this.permissionSize++;
            }
        }
        builder.delete(builder.length() - 1, builder.length());
        return String.format(getString(R.string.need_permission), new Object[]{builder.toString()});
    }

    @SuppressLint({"StringFormatInvalid"})
    private String getListPermission(List<String> perms) {
        this.addTexts.clear();
        StringBuilder builder = new StringBuilder();
        for (String value : perms) {
            String per = getPermission(value);
            if (!this.addTexts.contains(per)) {
                this.addTexts.add(per);
                builder.append(per);
                builder.append("-");
            }
        }
        builder.delete(builder.length() - 1, builder.length());
        return String.format(getString(R.string.need_permission), new Object[]{builder.toString()});
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getPermission(java.lang.String r3) {
        /*
            r2 = this;
            java.lang.String r0 = ""
            int r1 = r3.hashCode()
            switch(r1) {
                case -2062386608: goto L_0x010f;
                case -1928411001: goto L_0x0104;
                case -1921431796: goto L_0x00fa;
                case -1888586689: goto L_0x00ef;
                case -1479758289: goto L_0x00e4;
                case -1238066820: goto L_0x00d9;
                case -895679497: goto L_0x00ce;
                case -895673731: goto L_0x00c3;
                case -406040016: goto L_0x00b8;
                case -63024214: goto L_0x00ac;
                case -5573545: goto L_0x00a1;
                case 52602690: goto L_0x0095;
                case 112197485: goto L_0x008a;
                case 214526995: goto L_0x007f;
                case 463403621: goto L_0x0073;
                case 603653886: goto L_0x0067;
                case 610633091: goto L_0x005c;
                case 784519842: goto L_0x0051;
                case 952819282: goto L_0x0045;
                case 1271781903: goto L_0x003a;
                case 1365911975: goto L_0x002e;
                case 1831139720: goto L_0x0022;
                case 1977429404: goto L_0x0017;
                case 2133799037: goto L_0x000b;
                default: goto L_0x0009;
            }
        L_0x0009:
            goto L_0x011a
        L_0x000b:
            java.lang.String r1 = "com.android.voicemail.permission.ADD_VOICEMAIL"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 9
            goto L_0x011b
        L_0x0017:
            java.lang.String r1 = "android.permission.READ_CONTACTS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 2
            goto L_0x011b
        L_0x0022:
            java.lang.String r1 = "android.permission.RECORD_AUDIO"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 18
            goto L_0x011b
        L_0x002e:
            java.lang.String r1 = "android.permission.WRITE_EXTERNAL_STORAGE"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 17
            goto L_0x011b
        L_0x003a:
            java.lang.String r1 = "android.permission.GET_ACCOUNTS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 1
            goto L_0x011b
        L_0x0045:
            java.lang.String r1 = "android.permission.PROCESS_OUTGOING_CALLS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 8
            goto L_0x011b
        L_0x0051:
            java.lang.String r1 = "android.permission.USE_SIP"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 7
            goto L_0x011b
        L_0x005c:
            java.lang.String r1 = "android.permission.WRITE_CALL_LOG"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 6
            goto L_0x011b
        L_0x0067:
            java.lang.String r1 = "android.permission.WRITE_CALENDAR"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 11
            goto L_0x011b
        L_0x0073:
            java.lang.String r1 = "android.permission.CAMERA"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 12
            goto L_0x011b
        L_0x007f:
            java.lang.String r1 = "android.permission.WRITE_CONTACTS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 0
            goto L_0x011b
        L_0x008a:
            java.lang.String r1 = "android.permission.CALL_PHONE"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 5
            goto L_0x011b
        L_0x0095:
            java.lang.String r1 = "android.permission.SEND_SMS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 23
            goto L_0x011b
        L_0x00a1:
            java.lang.String r1 = "android.permission.READ_PHONE_STATE"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 4
            goto L_0x011b
        L_0x00ac:
            java.lang.String r1 = "android.permission.ACCESS_COARSE_LOCATION"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 15
            goto L_0x011b
        L_0x00b8:
            java.lang.String r1 = "android.permission.READ_EXTERNAL_STORAGE"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 16
            goto L_0x011b
        L_0x00c3:
            java.lang.String r1 = "android.permission.RECEIVE_SMS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 22
            goto L_0x011b
        L_0x00ce:
            java.lang.String r1 = "android.permission.RECEIVE_MMS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 21
            goto L_0x011b
        L_0x00d9:
            java.lang.String r1 = "android.permission.BODY_SENSORS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 13
            goto L_0x011b
        L_0x00e4:
            java.lang.String r1 = "android.permission.RECEIVE_WAP_PUSH"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 20
            goto L_0x011b
        L_0x00ef:
            java.lang.String r1 = "android.permission.ACCESS_FINE_LOCATION"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 14
            goto L_0x011b
        L_0x00fa:
            java.lang.String r1 = "android.permission.READ_CALL_LOG"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 3
            goto L_0x011b
        L_0x0104:
            java.lang.String r1 = "android.permission.READ_CALENDAR"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 10
            goto L_0x011b
        L_0x010f:
            java.lang.String r1 = "android.permission.READ_SMS"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0009
            r1 = 19
            goto L_0x011b
        L_0x011a:
            r1 = -1
        L_0x011b:
            switch(r1) {
                case 0: goto L_0x0157;
                case 1: goto L_0x0157;
                case 2: goto L_0x0157;
                case 3: goto L_0x0150;
                case 4: goto L_0x0150;
                case 5: goto L_0x0150;
                case 6: goto L_0x0150;
                case 7: goto L_0x0150;
                case 8: goto L_0x0150;
                case 9: goto L_0x0150;
                case 10: goto L_0x0149;
                case 11: goto L_0x0149;
                case 12: goto L_0x0142;
                case 13: goto L_0x013b;
                case 14: goto L_0x0134;
                case 15: goto L_0x0134;
                case 16: goto L_0x012d;
                case 17: goto L_0x012d;
                case 18: goto L_0x0126;
                case 19: goto L_0x011f;
                case 20: goto L_0x011f;
                case 21: goto L_0x011f;
                case 22: goto L_0x011f;
                case 23: goto L_0x011f;
                default: goto L_0x011e;
            }
        L_0x011e:
            goto L_0x015e
        L_0x011f:
            int r1 = com.bjz.comm.net.R.string.get_phone_info
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0126:
            int r1 = com.bjz.comm.net.R.string.record
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x012d:
            int r1 = com.bjz.comm.net.R.string.storage
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0134:
            int r1 = com.bjz.comm.net.R.string.location
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x013b:
            int r1 = com.bjz.comm.net.R.string.sensors
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0142:
            int r1 = com.bjz.comm.net.R.string.camera
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0149:
            int r1 = com.bjz.comm.net.R.string.calendar
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0150:
            int r1 = com.bjz.comm.net.R.string.call_phone
            java.lang.String r0 = r2.getString(r1)
            goto L_0x015e
        L_0x0157:
            int r1 = com.bjz.comm.net.R.string.linkman
            java.lang.String r0 = r2.getString(r1)
        L_0x015e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bjz.comm.net.premission.PermissionActivity.getPermission(java.lang.String):java.lang.String");
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("permissions", this.permissions);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions2, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions2, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions2, grantResults, this);
    }

    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(TAG, getClass().getSimpleName() + " ===> onPermissionsGranted-成功" + perms.toString());
        }
        if (this.permissions.length == perms.size()) {
            PermissionManager.getInstance(this).requestPermissionSuccess();
            onBackPressed();
            return;
        }
        this.handler.sendEmptyMessageDelayed(11, 500);
    }

    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (BuildVars.DEBUG_VERSION) {
            Log.e(TAG, getClass().getSimpleName() + " ===> onPermissionsDenied-失败" + perms.toString());
        }
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, perms) && this.isFirst) {
            this.isFirst = false;
            new AppSettingsDialog.Builder((Activity) this).setRationale(String.format(getString(R.string.permission_message), new Object[]{getListPermission(perms)})).setPositiveButton(getString(R.string.insure)).setNegativeButton(getString(R.string.quit)).build().show();
        }
        PermissionManager.getInstance(this).requestPermissionFail();
        this.handler.removeCallbacksAndMessages((Object) null);
        onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.DEBUG_VERSION) {
            Log.e(TAG, getClass().getSimpleName() + " ===> onDestroy");
        }
    }
}
