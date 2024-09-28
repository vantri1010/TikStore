package pub.devrel.easypermissions.helper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import pub.devrel.easypermissions.RationaleDialogFragment;

class ActivityPermissionHelper extends PermissionHelper<Activity> {
    private static final String TAG = "ActPermissionHelper";

    public ActivityPermissionHelper(Activity host) {
        super(host);
    }

    public void directRequestPermissions(int requestCode, String... perms) {
        ActivityCompat.requestPermissions((Activity) getHost(), perms, requestCode);
    }

    public boolean shouldShowRequestPermissionRationale(String perm) {
        return ActivityCompat.shouldShowRequestPermissionRationale((Activity) getHost(), perm);
    }

    public Context getContext() {
        return (Context) getHost();
    }

    public void showRequestPermissionRationale(String rationale, String positiveButton, String negativeButton, int theme, int requestCode, String... perms) {
        FragmentManager fm = ((Activity) getHost()).getFragmentManager();
        if (fm.findFragmentByTag(RationaleDialogFragment.TAG) instanceof RationaleDialogFragment) {
            Log.d(TAG, "Found existing fragment, not showing rationale.");
        } else {
            RationaleDialogFragment.newInstance(positiveButton, negativeButton, rationale, theme, requestCode, perms).showAllowingStateLoss(fm, RationaleDialogFragment.TAG);
        }
    }
}
