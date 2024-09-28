package pub.devrel.easypermissions.helper;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

class AppCompatActivityPermissionsHelper extends BaseSupportPermissionsHelper<AppCompatActivity> {
    public AppCompatActivityPermissionsHelper(AppCompatActivity host) {
        super(host);
    }

    public FragmentManager getSupportFragmentManager() {
        return ((AppCompatActivity) getHost()).getSupportFragmentManager();
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
}
