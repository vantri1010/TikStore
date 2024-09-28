package im.bclpbkiauv.ui;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.SpanUtils;
import com.google.android.gms.common.internal.ImagesContract;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.WebViewAppCompatActivity;

public class LaunchAgDialogActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public boolean startPressed;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(1280);
            window.setStatusBarColor(0);
        }
        try {
            getWindow().clearFlags(8192);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        showPrivacyPermissionDialog();
    }

    private void showPrivacyPermissionDialog() {
        if (!MessagesController.getGlobalMainSettings().getBoolean("isFSPrivacy", true)) {
            toLaunchPage();
            return;
        }
        WalletDialog dialog = new WalletDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        SpanUtils span = new SpanUtils();
        span.append(LocaleController.getString(R.string.PrivacyAgreement1)).append(LocaleController.getString(R.string.UserAgreementOnly)).setClickSpan(new ClickableSpan() {
            public void onClick(View widget) {
                Intent intent = new Intent(LaunchAgDialogActivity.this, WebViewAppCompatActivity.class);
                intent.putExtra(ImagesContract.URL, Constants.URL_USER_AGREEMENT);
                LaunchAgDialogActivity.this.startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            }
        }).append("、").append(LocaleController.getString(R.string.PrivacyAgreement)).setClickSpan(new ClickableSpan() {
            public void onClick(View widget) {
                Intent intent = new Intent(LaunchAgDialogActivity.this, WebViewAppCompatActivity.class);
                intent.putExtra(ImagesContract.URL, Constants.URL_PRIVACY_POLICY);
                LaunchAgDialogActivity.this.startActivity(intent);
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            }
        }).append(LocaleController.getString(R.string.PrivacyAgreement2));
        dialog.setMessage(span.create(), false, false);
        dialog.setTitle(LocaleController.getString(R.string.PrivacyAgreement));
        dialog.setNegativeButton(LocaleController.getString(R.string.Disagree), Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LaunchAgDialogActivity.this.finish();
            }
        });
        dialog.setPositiveButton(LocaleController.getString(R.string.Agree), Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (!LaunchAgDialogActivity.this.startPressed) {
                    LaunchAgDialogActivity.this.setNotFirstLaunch();
                    boolean unused = LaunchAgDialogActivity.this.startPressed = true;
                    LaunchAgDialogActivity.this.toLaunchPage();
                }
            }
        });
        dialog.show();
    }

    /* access modifiers changed from: private */
    public void toLaunchPage() {
        Intent intent2 = new Intent(this, LaunchActivity.class);
        intent2.putExtra("fromIntro", true);
        startActivity(intent2);
        finish();
    }

    /* access modifiers changed from: private */
    public void setNotFirstLaunch() {
        SharedPreferences sp = MessagesController.getGlobalMainSettings();
        if (sp.getBoolean("isFSPrivacy", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFSPrivacy", false);
            editor.commit();
        }
    }

    public void killAppProcess() {
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService("activity")).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid != Process.myPid()) {
                Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
