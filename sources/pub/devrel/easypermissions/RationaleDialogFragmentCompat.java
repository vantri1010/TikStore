package pub.devrel.easypermissions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;
import pub.devrel.easypermissions.EasyPermissions;

public class RationaleDialogFragmentCompat extends AppCompatDialogFragment {
    public static final String TAG = "RationaleDialogFragmentCompat";
    private EasyPermissions.PermissionCallbacks mPermissionCallbacks;
    private EasyPermissions.RationaleCallbacks mRationaleCallbacks;

    public static RationaleDialogFragmentCompat newInstance(String rationaleMsg, String positiveButton, String negativeButton, int theme, int requestCode, String[] permissions) {
        RationaleDialogFragmentCompat dialogFragment = new RationaleDialogFragmentCompat();
        dialogFragment.setArguments(new RationaleDialogConfig(positiveButton, negativeButton, rationaleMsg, theme, requestCode, permissions).toBundle());
        return dialogFragment;
    }

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        if (!manager.isStateSaved()) {
            show(manager, tag);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof EasyPermissions.PermissionCallbacks) {
                this.mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) getParentFragment();
            }
            if (getParentFragment() instanceof EasyPermissions.RationaleCallbacks) {
                this.mRationaleCallbacks = (EasyPermissions.RationaleCallbacks) getParentFragment();
            }
        }
        if (context instanceof EasyPermissions.PermissionCallbacks) {
            this.mPermissionCallbacks = (EasyPermissions.PermissionCallbacks) context;
        }
        if (context instanceof EasyPermissions.RationaleCallbacks) {
            this.mRationaleCallbacks = (EasyPermissions.RationaleCallbacks) context;
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mPermissionCallbacks = null;
        this.mRationaleCallbacks = null;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        RationaleDialogConfig config = new RationaleDialogConfig(getArguments());
        return config.createSupportDialog(getContext(), new RationaleDialogClickListener(this, config, this.mPermissionCallbacks, this.mRationaleCallbacks));
    }
}
