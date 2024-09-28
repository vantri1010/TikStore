package pub.devrel.easypermissions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AppSettingsDialogHolderActivity extends AppCompatActivity implements DialogInterface.OnClickListener {
    private static final int APP_SETTINGS_RC = 7534;
    private AlertDialog mDialog;
    private int mIntentFlags;

    public static Intent createShowDialogIntent(Context context, AppSettingsDialog dialog) {
        Intent intent = new Intent(context, AppSettingsDialogHolderActivity.class);
        intent.putExtra("extra_app_settings", dialog);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettingsDialog appSettingsDialog = AppSettingsDialog.fromIntent(getIntent(), this);
        this.mIntentFlags = appSettingsDialog.getIntentFlags();
        this.mDialog = appSettingsDialog.showDialog(this, this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", getPackageName(), (String) null));
            intent.addFlags(this.mIntentFlags);
            startActivityForResult(intent, APP_SETTINGS_RC);
        } else if (which == -2) {
            setResult(0);
            finish();
        } else {
            throw new IllegalStateException("Unknown button type: " + which);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }
}
