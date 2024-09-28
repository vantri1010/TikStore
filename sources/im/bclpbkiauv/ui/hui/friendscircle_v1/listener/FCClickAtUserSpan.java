package im.bclpbkiauv.ui.hui.friendscircle_v1.listener;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;
import com.bjz.comm.net.bean.FCEntitysResponse;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.ui.FcPageOthersActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;

public class FCClickAtUserSpan extends ClickAtUserSpan {
    private int guid;
    private FCEntitysResponse mFcEntitysResponse;
    private SpanAtUserCallBack spanClickCallBack;

    public FCClickAtUserSpan(int guid2, FCEntitysResponse FCEntitysResponse, int color, SpanAtUserCallBack spanClickCallBack2) {
        super(FCEntitysResponse, color, spanClickCallBack2);
        this.spanClickCallBack = spanClickCallBack2;
        this.guid = guid2;
        this.mFcEntitysResponse = FCEntitysResponse;
    }

    public void onClick(View view) {
        FCEntitysResponse fCEntitysResponse;
        super.onClick(view);
        if (view instanceof TextView) {
            ((TextView) view).setHighlightColor(0);
        }
        if (this.spanClickCallBack != null && (fCEntitysResponse = this.mFcEntitysResponse) != null && fCEntitysResponse.getUserID() != 0 && this.mFcEntitysResponse.getAccessHash() != 0) {
            this.spanClickCallBack.onPresentFragment(new FcPageOthersActivity(this.mFcEntitysResponse.getUserID(), this.mFcEntitysResponse.getAccessHash()));
        }
    }

    private void getUserInfo() {
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
            if (connectionsManager != null && messagesController != null && this.guid != 0) {
                TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
                TLRPC.TL_inputUser user = new TLRPC.TL_inputUser();
                user.user_id = this.mFcEntitysResponse.getUserID();
                user.access_hash = this.mFcEntitysResponse.getAccessHash();
                req.id = user;
                connectionsManager.bindRequestToGuid(connectionsManager.sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FCClickAtUserSpan.this.lambda$getUserInfo$1$FCClickAtUserSpan(tLObject, tL_error);
                    }
                }), this.guid);
            }
        }
    }

    public /* synthetic */ void lambda$getUserInfo$1$FCClickAtUserSpan(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FCClickAtUserSpan.this.lambda$null$0$FCClickAtUserSpan(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$FCClickAtUserSpan(TLObject response) {
        TLRPC.UserFull userFull = (TLRPC.UserFull) response;
        MessagesController.getInstance(UserConfig.selectedAccount).putUser(userFull.user, false);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", userFull.user.id);
        SpanAtUserCallBack spanAtUserCallBack = this.spanClickCallBack;
        if (spanAtUserCallBack != null) {
            spanAtUserCallBack.onPresentFragment(new NewProfileActivity(bundle));
        }
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
    }
}
