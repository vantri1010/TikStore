package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
import im.bclpbkiauv.tgnet.TLRPC;
import java.util.ArrayList;
import java.util.HashMap;

public class WearReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence text = remoteInput.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
            if (!TextUtils.isEmpty(text)) {
                long dialog_id = intent2.getLongExtra("dialog_id", 0);
                int max_id = intent2.getIntExtra("max_id", 0);
                int currentAccount = intent2.getIntExtra("currentAccount", 0);
                if (dialog_id == 0) {
                } else if (max_id == 0) {
                    Bundle bundle = remoteInput;
                } else {
                    int lowerId = (int) dialog_id;
                    int highId = (int) (dialog_id >> 32);
                    AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
                    if (lowerId <= 0) {
                        int i = highId;
                        if (lowerId < 0) {
                            TLRPC.Chat chat = accountInstance.getMessagesController().getChat(Integer.valueOf(-lowerId));
                            if (chat == null) {
                                DispatchQueue dispatchQueue = Utilities.globalQueue;
                                TLRPC.Chat chat2 = chat;
                                $$Lambda$WearReplyReceiver$PRvQeroa_eoSVcv1DvoMyxMvoFk r0 = r3;
                                $$Lambda$WearReplyReceiver$PRvQeroa_eoSVcv1DvoMyxMvoFk r3 = new Runnable(accountInstance, lowerId, text, dialog_id, max_id) {
                                    private final /* synthetic */ AccountInstance f$1;
                                    private final /* synthetic */ int f$2;
                                    private final /* synthetic */ CharSequence f$3;
                                    private final /* synthetic */ long f$4;
                                    private final /* synthetic */ int f$5;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                        this.f$4 = r5;
                                        this.f$5 = r7;
                                    }

                                    public final void run() {
                                        WearReplyReceiver.this.lambda$onReceive$3$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                                    }
                                };
                                dispatchQueue.postRunnable(r0);
                                return;
                            }
                        }
                    } else if (accountInstance.getMessagesController().getUser(Integer.valueOf(lowerId)) == null) {
                        Bundle bundle2 = remoteInput;
                        DispatchQueue dispatchQueue2 = Utilities.globalQueue;
                        $$Lambda$WearReplyReceiver$46cDQJXOhnvMTfgfq_yjA8vIDsc r1 = r3;
                        int i2 = highId;
                        $$Lambda$WearReplyReceiver$46cDQJXOhnvMTfgfq_yjA8vIDsc r32 = new Runnable(accountInstance, lowerId, text, dialog_id, max_id) {
                            private final /* synthetic */ AccountInstance f$1;
                            private final /* synthetic */ int f$2;
                            private final /* synthetic */ CharSequence f$3;
                            private final /* synthetic */ long f$4;
                            private final /* synthetic */ int f$5;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r7;
                            }

                            public final void run() {
                                WearReplyReceiver.this.lambda$onReceive$1$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                            }
                        };
                        dispatchQueue2.postRunnable(r1);
                        return;
                    } else {
                        int i3 = highId;
                    }
                    sendMessage(accountInstance, text, dialog_id, max_id);
                }
            }
        }
    }

    public /* synthetic */ void lambda$onReceive$1$WearReplyReceiver(AccountInstance accountInstance, int lowerId, CharSequence text, long dialog_id, int max_id) {
        int i = lowerId;
        AndroidUtilities.runOnUIThread(new Runnable(accountInstance, accountInstance.getMessagesStorage().getUserSync(lowerId), text, dialog_id, max_id) {
            private final /* synthetic */ AccountInstance f$1;
            private final /* synthetic */ TLRPC.User f$2;
            private final /* synthetic */ CharSequence f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
            }

            public final void run() {
                WearReplyReceiver.this.lambda$null$0$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$WearReplyReceiver(AccountInstance accountInstance, TLRPC.User user1, CharSequence text, long dialog_id, int max_id) {
        accountInstance.getMessagesController().putUser(user1, true);
        sendMessage(accountInstance, text, dialog_id, max_id);
    }

    public /* synthetic */ void lambda$onReceive$3$WearReplyReceiver(AccountInstance accountInstance, int lowerId, CharSequence text, long dialog_id, int max_id) {
        AndroidUtilities.runOnUIThread(new Runnable(accountInstance, accountInstance.getMessagesStorage().getChatSync(-lowerId), text, dialog_id, max_id) {
            private final /* synthetic */ AccountInstance f$1;
            private final /* synthetic */ TLRPC.Chat f$2;
            private final /* synthetic */ CharSequence f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
            }

            public final void run() {
                WearReplyReceiver.this.lambda$null$2$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$WearReplyReceiver(AccountInstance accountInstance, TLRPC.Chat chat1, CharSequence text, long dialog_id, int max_id) {
        accountInstance.getMessagesController().putChat(chat1, true);
        sendMessage(accountInstance, text, dialog_id, max_id);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence text, long dialog_id, int max_id) {
        accountInstance.getSendMessagesHelper().sendMessage(text.toString(), dialog_id, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        accountInstance.getMessagesController().markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
    }
}
