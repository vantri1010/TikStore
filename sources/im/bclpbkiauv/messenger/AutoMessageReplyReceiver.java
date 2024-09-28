package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.RemoteInput;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.HashMap;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        CharSequence text;
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null && (text = remoteInput.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY)) != null && text.length() != 0) {
            long dialog_id = intent2.getLongExtra("dialog_id", 0);
            int max_id = intent2.getIntExtra("max_id", 0);
            int currentAccount = intent2.getIntExtra("currentAccount", 0);
            if (dialog_id != 0 && max_id != 0) {
                SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialog_id, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
            }
        }
    }
}
