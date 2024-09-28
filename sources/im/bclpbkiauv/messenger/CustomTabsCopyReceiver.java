package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import im.bclpbkiauv.ui.components.toast.ToastUtils;

public class CustomTabsCopyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();
        if (url != null) {
            AndroidUtilities.addToClipboard(url);
            ToastUtils.show((int) R.string.LinkCopied);
        }
    }
}
