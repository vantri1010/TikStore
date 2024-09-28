package pub.devrel.easypermissions.helper;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;

class LowApiPermissionsHelper<T> extends PermissionHelper<T> {
    public LowApiPermissionsHelper(T host) {
        super(host);
    }

    public void directRequestPermissions(int requestCode, String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    public boolean shouldShowRequestPermissionRationale(String perm) {
        return false;
    }

    public void showRequestPermissionRationale(String rationale, String positiveButton, String negativeButton, int theme, int requestCode, String... perms) {
        throw new IllegalStateException("Should never be requesting permissions on API < 23!");
    }

    public Context getContext() {
        if (getHost() instanceof Activity) {
            return (Context) getHost();
        }
        if (getHost() instanceof Fragment) {
            return ((Fragment) getHost()).getContext();
        }
        throw new IllegalStateException("Unknown host: " + getHost());
    }
}
