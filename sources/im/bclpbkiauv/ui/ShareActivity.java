package im.bclpbkiauv.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.MessageObject;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.Utilities;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(R.style.Theme_TMessages_Transparent);
        super.onCreate(savedInstanceState);
        setContentView(new View(this), new ViewGroup.LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            finish();
            return;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String url = data.toString();
        String hash = data.getQueryParameter("hash");
        if (!"hchat".equals(scheme) || !url.toLowerCase().startsWith("hchat://share_game_score") || TextUtils.isEmpty(hash)) {
            finish();
            return;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        String message = sharedPreferences.getString(hash + "_m", (String) null);
        if (TextUtils.isEmpty(message)) {
            finish();
            return;
        }
        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(message));
        TLRPC.Message mess = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
        mess.readAttachPath(serializedData, 0);
        serializedData.cleanup();
        if (mess == null) {
            finish();
            return;
        }
        String link = sharedPreferences.getString(hash + "_link", (String) null);
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, mess, false);
        messageObject.messageOwner.with_my_score = true;
        MessageObject messageObject2 = messageObject;
        TLRPC.Message message2 = mess;
        try {
            ShareAlert createShareAlert = ShareAlert.createShareAlert(this, messageObject, (String) null, false, link, false);
            this.visibleDialog = createShareAlert;
            createShareAlert.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    ShareActivity.this.lambda$onCreate$0$ShareActivity(dialogInterface);
                }
            });
            this.visibleDialog.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            finish();
        }
    }

    public /* synthetic */ void lambda$onCreate$0$ShareActivity(DialogInterface dialog) {
        if (!isFinishing()) {
            finish();
        }
        this.visibleDialog = null;
    }

    public void onPause() {
        super.onPause();
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
