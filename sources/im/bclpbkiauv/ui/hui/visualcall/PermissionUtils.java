package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.hui.visualcall.PermissionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionUtils {
    public static final int CODE_ACCESS_COARSE_LOCATION = 6;
    public static final int CODE_ACCESS_FINE_LOCATION = 5;
    public static final int CODE_CALL_PHONE = 3;
    public static final int CODE_CAMERA = 4;
    public static final int CODE_GET_ACCOUNTS = 1;
    public static final int CODE_MULTI_PERMISSION = 100;
    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_READ_PHONE_STATE = 2;
    public static final int CODE_RECORD_AUDIO = 0;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    public static final String PERMISSION_ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String PERMISSION_CALL_PHONE = "android.permission.CALL_PHONE";
    public static final String PERMISSION_CAMERA = "android.permission.CAMERA";
    public static final String PERMISSION_GET_ACCOUNTS = "android.permission.GET_ACCOUNTS";
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String PERMISSION_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String PERMISSION_RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final int REQUEST_CODE_SETTING = 100;
    private static final String TAG = PermissionUtils.class.getSimpleName();
    private static final String[] requestPermissions = {"android.permission.RECORD_AUDIO", PERMISSION_GET_ACCOUNTS, "android.permission.READ_PHONE_STATE", PERMISSION_CALL_PHONE, "android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION", PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_READ_EXTERNAL_STORAGE, "android.permission.WRITE_EXTERNAL_STORAGE"};

    public interface PermissionGrant {
        void onPermissionCancel();

        void onPermissionGranted(int i);
    }

    public static void requestPermission(Activity activity, int requestCode, PermissionGrant permissionGrant) {
        if (activity != null) {
            String str = TAG;
            Log.i(str, "requestPermission requestCode:" + requestCode);
            if (requestCode >= 0) {
                String[] strArr = requestPermissions;
                if (requestCode < strArr.length) {
                    String requestPermission = strArr[requestCode];
                    try {
                        if (ActivityCompat.checkSelfPermission(activity, requestPermission) != 0) {
                            Log.i(TAG, "ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED");
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                                Log.i(TAG, "requestPermission shouldShowRequestPermissionRationale");
                                shouldShowRationale(activity, requestCode, requestPermission, permissionGrant);
                                return;
                            }
                            Log.d(TAG, "requestCameraPermission else");
                            ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
                            return;
                        }
                        Log.d(TAG, "ActivityCompat.checkSelfPermission ==== PackageManager.PERMISSION_GRANTED");
                        Toast.makeText(activity, "opened:" + requestPermissions[requestCode], 0).show();
                        permissionGrant.onPermissionGranted(requestCode);
                        return;
                    } catch (RuntimeException e) {
                        Toast.makeText(activity, "please open this permission", 0).show();
                        String str2 = TAG;
                        Log.e(str2, "RuntimeException:" + e.getMessage());
                        return;
                    }
                }
            }
            String requestPermission2 = TAG;
            Log.w(requestPermission2, "requestPermission illegal requestCode:" + requestCode);
        }
    }

    private static void requestMultiResult(Activity activity, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {
        if (activity != null) {
            String str = TAG;
            Log.d(str, "onRequestPermissionsResult permissions length:" + permissions.length);
            Map<String, Integer> perms = new HashMap<>();
            ArrayList<String> notGranted = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String str2 = TAG;
                Log.d(str2, "permissions: [i]:" + i + ", permissions[i]" + permissions[i] + ",grantResults[i]:" + grantResults[i]);
                perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                if (grantResults[i] != 0) {
                    notGranted.add(permissions[i]);
                }
            }
            if (notGranted.size() == 0) {
                permissionGrant.onPermissionGranted(100);
            } else {
                openSettingActivity(activity, LocaleController.getString("visual_call_permission_tip", R.string.visual_call_permission_tip), (String[]) null, permissionGrant);
            }
        }
    }

    public static void requestMultiPermissions(Activity activity, String[] permissions, PermissionGrant grant) {
        List<String> permissionsList = getNoGrantedPermission(activity, permissions, false);
        List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, permissions, true);
        if (permissionsList != null && shouldRationalePermissionsList != null) {
            String str = TAG;
            Log.d(str, "requestMultiPermissions permissionsList:" + permissionsList.size() + ",shouldRationalePermissionsList:" + shouldRationalePermissionsList.size());
            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions(activity, (String[]) permissionsList.toArray(new String[permissionsList.size()]), 100);
                Log.d(TAG, "showMessageOKCancel requestPermissions");
            } else if (shouldRationalePermissionsList.size() > 0) {
                showMessageOKCancel(activity, LocaleController.getString("visual_call_permission_tip", R.string.visual_call_permission_tip), (String[]) null, new DialogInterface.OnClickListener(activity, shouldRationalePermissionsList) {
                    private final /* synthetic */ Activity f$0;
                    private final /* synthetic */ List f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.lambda$requestMultiPermissions$0(this.f$0, this.f$1, dialogInterface, i);
                    }
                }, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.PermissionGrant.this.onPermissionCancel();
                    }
                });
            } else {
                grant.onPermissionGranted(100);
            }
        }
    }

    static /* synthetic */ void lambda$requestMultiPermissions$0(Activity activity, List shouldRationalePermissionsList, DialogInterface dialog, int which) {
        ActivityCompat.requestPermissions(activity, (String[]) shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]), 100);
        Log.d(TAG, "showMessageOKCancel requestPermissions");
    }

    private static void shouldShowRationale(Activity activity, int requestCode, String requestPermission, PermissionGrant permissionGrant) {
        showMessageOKCancel(activity, "Rationale: need to open under permission by yourself", new String[]{requestPermission}, new DialogInterface.OnClickListener(activity, requestPermission, requestCode) {
            private final /* synthetic */ Activity f$0;
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PermissionUtils.lambda$shouldShowRationale$2(this.f$0, this.f$1, this.f$2, dialogInterface, i);
            }
        }, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PermissionUtils.PermissionGrant.this.onPermissionCancel();
            }
        });
    }

    static /* synthetic */ void lambda$shouldShowRationale$2(Activity activity, String requestPermission, int requestCode, DialogInterface dialog, int which) {
        ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
        String str = TAG;
        Log.d(str, "showMessageOKCancel requestPermissions:" + requestPermission);
    }

    private static void showMessageOKCancel(Activity context, String message, String[] permissions, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(context).setTitle((CharSequence) message).setItems((CharSequence[]) permissions, (DialogInterface.OnClickListener) null).setPositiveButton((CharSequence) LocaleController.getString("OK", R.string.OK), okListener).setNegativeButton((CharSequence) LocaleController.getString("Cancel", R.string.Cancel), cancelListener).setCancelable(false).create().show();
    }

    public static void requestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {
        if (activity != null) {
            String str = TAG;
            Log.d(str, "requestPermissionsResult requestCode:" + requestCode);
            if (requestCode == 100) {
                requestMultiResult(activity, permissions, grantResults, permissionGrant);
            } else if (requestCode < 0 || requestCode >= requestPermissions.length) {
                String str2 = TAG;
                Log.w(str2, "requestPermissionsResult illegal requestCode:" + requestCode);
                Toast.makeText(activity, "illegal requestCode:" + requestCode, 0).show();
            } else {
                String str3 = TAG;
                Log.i(str3, "onRequestPermissionsResult requestCode:" + requestCode + ",permissions:" + permissions.toString() + ",grantResults:" + grantResults.toString() + ",length:" + grantResults.length);
                if (grantResults.length == 1 && grantResults[0] == 0) {
                    Log.i(TAG, "onRequestPermissionsResult PERMISSION_GRANTED");
                    permissionGrant.onPermissionGranted(requestCode);
                    return;
                }
                Log.i(TAG, "onRequestPermissionsResult PERMISSION NOT GRANTED");
                openSettingActivity(activity, "those permission need granted!", permissions, permissionGrant);
            }
        }
    }

    private static void openSettingActivity(Activity activity, String message, String[] permissions, PermissionGrant permissionGrant) {
        showMessageOKCancel(activity, message, permissions, new DialogInterface.OnClickListener(activity) {
            private final /* synthetic */ Activity f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                PermissionUtils.lambda$openSettingActivity$4(this.f$0, dialogInterface, i);
            }
        }, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                PermissionUtils.PermissionGrant.this.onPermissionCancel();
            }
        });
    }

    static /* synthetic */ void lambda$openSettingActivity$4(Activity activity, DialogInterface dialog, int which) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        String str = TAG;
        Log.d(str, "getPackageName(): " + activity.getPackageName());
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
        activity.startActivityForResult(intent, 100);
    }

    public static ArrayList<String> getNoGrantedPermission(Activity activity, String[] permissions, boolean isShouldRationale) {
        ArrayList<String> noGrantedPermission = new ArrayList<>();
        int i = 0;
        while (i < permissions.length) {
            String requestPermission = permissions[i];
            try {
                if (ActivityCompat.checkSelfPermission(activity, requestPermission) != 0) {
                    String str = TAG;
                    Log.i(str, "getNoGrantedPermission ActivityCompat.checkSelfPermission != PackageManager.PERMISSION_GRANTED:" + requestPermission);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                        Log.d(TAG, "shouldShowRequestPermissionRationale if");
                        if (isShouldRationale) {
                            noGrantedPermission.add(requestPermission);
                        }
                    } else {
                        if (!isShouldRationale) {
                            noGrantedPermission.add(requestPermission);
                        }
                        Log.d(TAG, "shouldShowRequestPermissionRationale else");
                    }
                }
                i++;
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", 0).show();
                String str2 = TAG;
                Log.e(str2, "RuntimeException:" + e.getMessage());
                return null;
            }
        }
        return noGrantedPermission;
    }
}
