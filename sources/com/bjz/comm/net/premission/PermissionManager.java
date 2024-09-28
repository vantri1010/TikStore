package com.bjz.comm.net.premission;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.bjz.comm.net.BuildVars;
import com.bjz.comm.net.premission.observer.PermissionObserver;
import com.google.android.exoplayer2.C;
import java.lang.ref.WeakReference;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionManager {
    private static final String TAG = "PermissionLog";
    private static volatile PermissionManager install;
    private Context context;
    private int flag;
    private WeakReference<PermissionObserver> permissionObs;

    public static PermissionManager getInstance(Context context2) {
        if (install == null) {
            synchronized (PermissionManager.class) {
                install = new PermissionManager(context2.getApplicationContext());
            }
        }
        return install;
    }

    public PermissionManager(Context context2) {
        this.context = context2;
    }

    public void requestPermission(PermissionObserver permissionObs2, int flag2, String... permissions) {
        if (BuildVars.DEBUG_VERSION) {
            Log.d(TAG, getClass().getSimpleName() + " ===> flag = " + flag2);
        }
        WeakReference<PermissionObserver> weakReference = this.permissionObs;
        if (!(weakReference == null || weakReference.get() == null)) {
            this.permissionObs.clear();
        }
        this.permissionObs = new WeakReference<>(permissionObs2);
        this.flag = flag2;
        if (EasyPermissions.hasPermissions(this.context, permissions)) {
            requestPermissionSuccess();
            return;
        }
        Intent intent = new Intent(this.context, PermissionActivity.class);
        intent.setFlags(C.ENCODING_PCM_MU_LAW);
        intent.putExtra("permissions", permissions);
        this.context.startActivity(intent);
    }

    public void requestPermissionSuccess() {
        WeakReference<PermissionObserver> weakReference = this.permissionObs;
        if (weakReference != null && weakReference.get() != null) {
            ((PermissionObserver) this.permissionObs.get()).onRequestPermissionSuccess(this.flag);
        }
    }

    public void requestPermissionFail() {
        WeakReference<PermissionObserver> weakReference = this.permissionObs;
        if (weakReference != null && weakReference.get() != null) {
            ((PermissionObserver) this.permissionObs.get()).onRequestPermissionFail(this.flag);
        }
    }
}
