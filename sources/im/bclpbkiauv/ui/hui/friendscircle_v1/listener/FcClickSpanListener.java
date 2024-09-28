package im.bclpbkiauv.ui.hui.friendscircle_v1.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.expandViewModel.LinkType;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.browser.Browser;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.hui.chats.NewProfileActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.expandTextView.ExpandableTextView;

public class FcClickSpanListener implements ExpandableTextView.OnLinkClickListener {
    private FcItemActionClickListener listener;
    private final Context mContext;
    private final int mGuid;

    public FcClickSpanListener(Context context, int guid, FcItemActionClickListener listener2) {
        this.mContext = context;
        this.mGuid = guid;
        this.listener = listener2;
    }

    /* renamed from: im.bclpbkiauv.ui.hui.friendscircle_v1.listener.FcClickSpanListener$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$bjz$comm$net$expandViewModel$LinkType;

        static {
            int[] iArr = new int[LinkType.values().length];
            $SwitchMap$com$bjz$comm$net$expandViewModel$LinkType = iArr;
            try {
                iArr[LinkType.LINK_TYPE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$bjz$comm$net$expandViewModel$LinkType[LinkType.MENTION_TYPE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$bjz$comm$net$expandViewModel$LinkType[LinkType.SELF.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public void onLinkClickListener(LinkType type, String content, String selfContent, FCEntitysResponse entityPosition) {
        int i = AnonymousClass1.$SwitchMap$com$bjz$comm$net$expandViewModel$LinkType[type.ordinal()];
        if (i != 1) {
            if (i == 2 && entityPosition != null) {
                getUserInfo(entityPosition);
            }
        } else if (!TextUtils.isEmpty(content) && this.mContext != null) {
            if ((content.contains("tel:") && TextUtils.isDigitsOnly(content.replace("tel:", ""))) || TextUtils.isDigitsOnly(content)) {
                this.mContext.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(content)));
            } else if (Browser.isInternalUrl(content, (boolean[]) null)) {
                Browser.openUrl(this.mContext, content, true);
            } else {
                String realUrl = content;
                if (!realUrl.contains("://") && (!realUrl.startsWith("http://") || !realUrl.startsWith("https://"))) {
                    realUrl = "http://" + realUrl;
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(realUrl));
                this.mContext.startActivity(intent);
            }
        }
    }

    private void getUserInfo(FCEntitysResponse fcEntitysResponse) {
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
            if (connectionsManager != null && messagesController != null && this.mGuid != 0) {
                TLRPC.TL_users_getFullUser req = new TLRPC.TL_users_getFullUser();
                TLRPC.TL_inputUser user = new TLRPC.TL_inputUser();
                user.user_id = fcEntitysResponse.getUserID();
                user.access_hash = fcEntitysResponse.getAccessHash();
                req.id = user;
                connectionsManager.bindRequestToGuid(connectionsManager.sendRequest(req, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FcClickSpanListener.this.lambda$getUserInfo$1$FcClickSpanListener(tLObject, tL_error);
                    }
                }), this.mGuid);
            }
        }
    }

    public /* synthetic */ void lambda$getUserInfo$1$FcClickSpanListener(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(response) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FcClickSpanListener.this.lambda$null$0$FcClickSpanListener(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$FcClickSpanListener(TLObject response) {
        TLRPC.UserFull userFull = (TLRPC.UserFull) response;
        MessagesController.getInstance(UserConfig.selectedAccount).putUser(userFull.user, false);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", userFull.user.id);
        FcItemActionClickListener fcItemActionClickListener = this.listener;
        if (fcItemActionClickListener != null) {
            fcItemActionClickListener.onPresentFragment(new NewProfileActivity(bundle));
        }
    }
}
