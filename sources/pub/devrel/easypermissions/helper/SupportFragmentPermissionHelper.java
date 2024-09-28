package pub.devrel.easypermissions.helper;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

class SupportFragmentPermissionHelper extends BaseSupportPermissionsHelper<Fragment> {
    public SupportFragmentPermissionHelper(Fragment host) {
        super(host);
    }

    public FragmentManager getSupportFragmentManager() {
        return ((Fragment) getHost()).getChildFragmentManager();
    }

    public void directRequestPermissions(int requestCode, String... perms) {
        ((Fragment) getHost()).requestPermissions(perms, requestCode);
    }

    public boolean shouldShowRequestPermissionRationale(String perm) {
        return ((Fragment) getHost()).shouldShowRequestPermissionRationale(perm);
    }

    public Context getContext() {
        return ((Fragment) getHost()).getActivity();
    }
}
