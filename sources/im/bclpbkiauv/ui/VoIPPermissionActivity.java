package im.bclpbkiauv.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import im.bclpbkiauv.messenger.voip.VoIPService;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == 0) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            }
            finish();
            startActivity(new Intent(this, VoIPActivity.class));
        } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall();
            }
            VoIPHelper.permissionDenied(this, new Runnable() {
                public void run() {
                    VoIPPermissionActivity.this.finish();
                }
            });
        } else {
            finish();
        }
    }
}
