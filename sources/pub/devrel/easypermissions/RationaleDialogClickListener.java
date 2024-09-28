package pub.devrel.easypermissions;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.fragment.app.Fragment;
import java.util.Arrays;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.helper.PermissionHelper;

class RationaleDialogClickListener implements DialogInterface.OnClickListener {
    private EasyPermissions.PermissionCallbacks mCallbacks;
    private RationaleDialogConfig mConfig;
    private Object mHost;
    private EasyPermissions.RationaleCallbacks mRationaleCallbacks;

    RationaleDialogClickListener(RationaleDialogFragmentCompat compatDialogFragment, RationaleDialogConfig config, EasyPermissions.PermissionCallbacks callbacks, EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        Object obj;
        if (compatDialogFragment.getParentFragment() != null) {
            obj = compatDialogFragment.getParentFragment();
        } else {
            obj = compatDialogFragment.getActivity();
        }
        this.mHost = obj;
        this.mConfig = config;
        this.mCallbacks = callbacks;
        this.mRationaleCallbacks = rationaleCallbacks;
    }

    RationaleDialogClickListener(RationaleDialogFragment dialogFragment, RationaleDialogConfig config, EasyPermissions.PermissionCallbacks callbacks, EasyPermissions.RationaleCallbacks dialogCallback) {
        this.mHost = dialogFragment.getActivity();
        this.mConfig = config;
        this.mCallbacks = callbacks;
        this.mRationaleCallbacks = dialogCallback;
    }

    public void onClick(DialogInterface dialog, int which) {
        int requestCode = this.mConfig.requestCode;
        if (which == -1) {
            String[] permissions = this.mConfig.permissions;
            EasyPermissions.RationaleCallbacks rationaleCallbacks = this.mRationaleCallbacks;
            if (rationaleCallbacks != null) {
                rationaleCallbacks.onRationaleAccepted(requestCode);
            }
            Object obj = this.mHost;
            if (obj instanceof Fragment) {
                PermissionHelper.newInstance((Fragment) obj).directRequestPermissions(requestCode, permissions);
            } else if (obj instanceof Activity) {
                PermissionHelper.newInstance((Activity) obj).directRequestPermissions(requestCode, permissions);
            } else {
                throw new RuntimeException("Host must be an Activity or Fragment!");
            }
        } else {
            EasyPermissions.RationaleCallbacks rationaleCallbacks2 = this.mRationaleCallbacks;
            if (rationaleCallbacks2 != null) {
                rationaleCallbacks2.onRationaleDenied(requestCode);
            }
            notifyPermissionDenied();
        }
    }

    private void notifyPermissionDenied() {
        EasyPermissions.PermissionCallbacks permissionCallbacks = this.mCallbacks;
        if (permissionCallbacks != null) {
            permissionCallbacks.onPermissionsDenied(this.mConfig.requestCode, Arrays.asList(this.mConfig.permissions));
        }
    }
}
