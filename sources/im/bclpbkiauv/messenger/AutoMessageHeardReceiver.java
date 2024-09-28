package im.bclpbkiauv.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import im.bclpbkiauv.tgnet.TLRPC;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        long dialog_id = intent2.getLongExtra("dialog_id", 0);
        int max_id = intent2.getIntExtra("max_id", 0);
        int currentAccount = intent2.getIntExtra("currentAccount", 0);
        if (dialog_id != 0 && max_id != 0) {
            int lowerId = (int) dialog_id;
            int i = (int) (dialog_id >> 32);
            AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
            if (lowerId > 0) {
                if (accountInstance.getMessagesController().getUser(Integer.valueOf(lowerId)) == null) {
                    Utilities.globalQueue.postRunnable(new Runnable(lowerId, currentAccount, dialog_id, max_id) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ long f$3;
                        private final /* synthetic */ int f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r6;
                        }

                        public final void run() {
                            AndroidUtilities.runOnUIThread(new Runnable(AccountInstance.this.getMessagesStorage().getUserSync(this.f$1), this.f$2, this.f$3, this.f$4) {
                                private final /* synthetic */ TLRPC.User f$1;
                                private final /* synthetic */ int f$2;
                                private final /* synthetic */ long f$3;
                                private final /* synthetic */ int f$4;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                    this.f$4 = r6;
                                }

                                public final void run() {
                                    AutoMessageHeardReceiver.lambda$null$0(AccountInstance.this, this.f$1, this.f$2, this.f$3, this.f$4);
                                }
                            });
                        }
                    });
                    return;
                }
            } else if (lowerId < 0 && accountInstance.getMessagesController().getChat(Integer.valueOf(-lowerId)) == null) {
                Utilities.globalQueue.postRunnable(new Runnable(lowerId, currentAccount, dialog_id, max_id) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ long f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(AccountInstance.this.getMessagesStorage().getChatSync(-this.f$1), this.f$2, this.f$3, this.f$4) {
                            private final /* synthetic */ TLRPC.Chat f$1;
                            private final /* synthetic */ int f$2;
                            private final /* synthetic */ long f$3;
                            private final /* synthetic */ int f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r6;
                            }

                            public final void run() {
                                AutoMessageHeardReceiver.lambda$null$2(AccountInstance.this, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
                return;
            }
            MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
        }
    }

    static /* synthetic */ void lambda$null$0(AccountInstance accountInstance, TLRPC.User user1, int currentAccount, long dialog_id, int max_id) {
        TLRPC.User user = user1;
        accountInstance.getMessagesController().putUser(user1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
    }

    static /* synthetic */ void lambda$null$2(AccountInstance accountInstance, TLRPC.Chat chat1, int currentAccount, long dialog_id, int max_id) {
        TLRPC.Chat chat = chat1;
        accountInstance.getMessagesController().putChat(chat1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true, 0);
    }
}
