package im.bclpbkiauv.ui.hui.visualcall;

import android.text.TextUtils;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AccountInstance;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCCall;
import im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface;
import java.util.ArrayList;

public class AVideoCallInterface {

    public interface AVideoRequestCallBack {
        void onError(TLRPC.TL_error tL_error);

        void onSuccess(TLObject tLObject);
    }

    public static void StartAVideoCall(boolean blnVideo, ArrayList<TLRPC.InputPeer> userId, TLRPC.InputPeer channelId, AVideoRequestCallBack callBack) {
        TLRPCCall.TL_MeetRequestCall req = new TLRPCCall.TL_MeetRequestCall();
        if (channelId != null) {
            req.flags = 7;
        } else {
            req.flags = 3;
        }
        req.video = blnVideo;
        req.channel_id = channelId;
        req.userIdList = userId;
        req.random_id = AccountInstance.getInstance(UserConfig.selectedAccount).getSendMessagesHelper().getNextRandomId();
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tL_error, AVideoCallInterface.AVideoRequestCallBack.this) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ AVideoCallInterface.AVideoRequestCallBack f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AVideoCallInterface.lambda$null$0(TLObject.this, this.f$1, this.f$2);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$0(TLObject response, TLRPC.TL_error error, AVideoRequestCallBack callBack) {
        if (response != null) {
            TLRPCCall.TL_UpdateMeetCallWaiting res = (TLRPCCall.TL_UpdateMeetCallWaiting) ((TLRPC.Updates) response).updates.get(0);
            if (error == null) {
                String str = res.data.data;
                if (callBack != null) {
                    callBack.onSuccess(res);
                }
            } else if (callBack != null) {
                callBack.onError(error);
            }
        } else if (callBack != null) {
            callBack.onError(error);
        }
    }

    public static void AcceptAVideoCall(String strID, AVideoRequestCallBack callBack) {
        if (!TextUtils.isEmpty(strID)) {
            FileLog.d("AcceptAVideoCall" + strID);
            TLRPCCall.TL_MeetAcceptCall req = new TLRPCCall.TL_MeetAcceptCall();
            req.peer = new TLRPCCall.TL_InputMeetCall();
            req.peer.id = strID;
            AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(AVideoCallInterface.AVideoRequestCallBack.this) {
                        private final /* synthetic */ AVideoCallInterface.AVideoRequestCallBack f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            AVideoCallInterface.lambda$null$2(TLRPC.TL_error.this, this.f$1);
                        }
                    });
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$2(TLRPC.TL_error error, AVideoRequestCallBack callBack) {
        if (error != null) {
            FileLog.d("AcceptAVideoCall res111111");
            if (callBack != null) {
                callBack.onError(error);
            }
        }
    }

    public static void DiscardAVideoCall(String strID, int iDur, boolean blnVideo) {
        TLRPCCall.TL_MeetDiscardCall req = new TLRPCCall.TL_MeetDiscardCall();
        req.peer = new TLRPCCall.TL_InputMeetCall();
        req.peer.id = strID;
        req.duration = iDur;
        req.flags = 0;
        req.video = blnVideo;
        req.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
        KLog.d("aaaa 开始发挂断消息");
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, $$Lambda$AVideoCallInterface$6xW_M1CGxWydxfiB3onSUDvYd8.INSTANCE);
    }

    static /* synthetic */ void lambda$DiscardAVideoCall$4(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            KLog.d("aaaa 发挂断消息异常");
        }
    }

    public static void ConfirmCall(String strID, long lFinger, AVideoRequestCallBack callBack) {
        TLRPCCall.TL_MeetConfirmCall req = new TLRPCCall.TL_MeetConfirmCall();
        req.peer = new TLRPCCall.TL_InputMeetCall();
        req.peer.id = strID;
        req.key_fingerprint = lFinger;
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tL_error, AVideoCallInterface.AVideoRequestCallBack.this) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ AVideoCallInterface.AVideoRequestCallBack f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AVideoCallInterface.lambda$null$5(TLObject.this, this.f$1, this.f$2);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$5(TLObject response, TLRPC.TL_error error, AVideoRequestCallBack callBack) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            if (error != null) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            } else if (callBack != null) {
                callBack.onSuccess(updates.updates.get(0));
            }
        } else if (callBack != null) {
            callBack.onError(error);
        }
    }

    public static void IsBusyingNow(String strID) {
        KLog.d("----------收到音视频2888");
        TLRPCCall.TL_MeetReceivedCall req = new TLRPCCall.TL_MeetReceivedCall();
        req.peer = new TLRPCCall.TL_InputMeetCall();
        req.peer.id = strID;
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, $$Lambda$AVideoCallInterface$aBcGnKDogE648pWQUWqLpnljTA.INSTANCE);
    }

    static /* synthetic */ void lambda$null$7(TLRPC.TL_error error) {
        if (error != null) {
            FileLog.d("IsBusyingNow res111111");
        }
    }

    public static void sendJumpPacket(String strID, AVideoRequestCallBack callBack) {
        KLog.d("+++++++ sendJumpPacket = " + strID);
        TLRPCCall.TL_MeetKeepCallV1 req = new TLRPCCall.TL_MeetKeepCallV1();
        req.peer = new TLRPCCall.TL_InputMeetCall();
        req.peer.id = strID;
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tLObject, AVideoCallInterface.AVideoRequestCallBack.this) {
                    private final /* synthetic */ TLObject f$1;
                    private final /* synthetic */ AVideoCallInterface.AVideoRequestCallBack f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AVideoCallInterface.lambda$null$9(TLRPC.TL_error.this, this.f$1, this.f$2);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$9(TLRPC.TL_error error, TLObject response, AVideoRequestCallBack callBack) {
        if (error != null) {
            KLog.d("------keepcall error = " + error.text);
        } else if (response != null) {
            TLRPCCall.TL_MeetModel meetModel = (TLRPCCall.TL_MeetModel) response;
            if (callBack != null) {
                callBack.onSuccess(meetModel);
            }
        }
    }

    public static void GetCallHistory(TLRPC.InputPeer uid, boolean blnVideo, int offset_id, int offset_date, int add_offset, int limit, int max_id, int min_id, int hash, AVideoRequestCallBack callBack) {
        TLRPCCall.TL_MeetGetCallHistory req = new TLRPCCall.TL_MeetGetCallHistory();
        req.peer = uid;
        req.flags = 1;
        req.video = blnVideo;
        req.offset_id = offset_id;
        req.offset_date = offset_date;
        req.add_offset = add_offset;
        req.limit = limit;
        req.max_id = max_id;
        req.min_id = min_id;
        req.hash = hash;
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tL_error, AVideoCallInterface.AVideoRequestCallBack.this) {
                    private final /* synthetic */ TLRPC.TL_error f$1;
                    private final /* synthetic */ AVideoCallInterface.AVideoRequestCallBack f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AVideoCallInterface.lambda$null$11(TLObject.this, this.f$1, this.f$2);
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$11(TLObject response, TLRPC.TL_error error, AVideoRequestCallBack callBack) {
        TLRPCCall.TL_UpdateMeetCallHistory res = (TLRPCCall.TL_UpdateMeetCallHistory) response;
        if (error == null) {
            String str = res.data.data;
            if (callBack != null) {
                callBack.onSuccess(response);
            }
        } else if (callBack != null) {
            callBack.onError(error);
        }
    }

    public static void ChangeToVoiceCall(String strID, boolean blnVideo) {
        TLRPCCall.TL_MeetChangeCall req = new TLRPCCall.TL_MeetChangeCall();
        req.peer = new TLRPCCall.TL_InputMeetCall();
        req.peer.id = strID;
        req.flags = 0;
        req.video = blnVideo;
        AccountInstance.getInstance(UserConfig.selectedAccount).getConnectionsManager().sendRequest(req, $$Lambda$AVideoCallInterface$xBOGwveEb7f7xyxLVBhZI61_cL8.INSTANCE);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void lambda$ChangeToVoiceCall$13(im.bclpbkiauv.tgnet.TLObject r0, im.bclpbkiauv.tgnet.TLRPC.TL_error r1) {
        /*
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.visualcall.AVideoCallInterface.lambda$ChangeToVoiceCall$13(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
    }
}
