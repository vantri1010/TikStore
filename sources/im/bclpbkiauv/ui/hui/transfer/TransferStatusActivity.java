package im.bclpbkiauv.ui.hui.transfer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.blankj.utilcode.util.SpanUtils;
import com.google.gson.Gson;
import im.bclpbkiauv.javaBean.hongbao.UnifyBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.ParamsUtil;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.transfer.bean.TransferOperation;
import im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.util.Iterator;

public class TransferStatusActivity extends BaseFragment {
    @BindView(2131296425)
    MryRoundButton btnTransferStateButton;
    private boolean complete;
    private boolean isSender;
    @BindView(2131296779)
    ImageView ivTransferStateImg;
    @BindView(2131296921)
    LinearLayout llTransferAboutLayout;
    private int mTokenReq;
    private TLRPC.Message message;
    private TLRPC.User targetUser;
    private String total;
    private TransferResponse transResponse;
    @BindView(2131297435)
    TextView tvActionTime;
    @BindView(2131297530)
    TextView tvHongbaoType;
    @BindView(2131297580)
    TextView tvRefuseTransfer;
    @BindView(2131297581)
    TextView tvRemarks;
    @BindView(2131297688)
    MryTextView tvTransferAmount;
    @BindView(2131297692)
    TextView tvTransferStateText;
    @BindView(2131297693)
    TextView tvTransferTime;
    @BindView(2131297707)
    TextView tvWallet;

    public void setMessage(TLRPC.Message message2) {
        this.message = message2;
    }

    public void setTargetUser(TLRPC.User user) {
        this.targetUser = user;
    }

    public void setSender(boolean sender) {
        this.isSender = sender;
    }

    public void setTransResponse(TransferResponse transResponse2) {
        setTransResponse(transResponse2, false);
    }

    public void setTransResponse(TransferResponse transResponse2, boolean complete2) {
        this.transResponse = transResponse2;
        this.complete = complete2;
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_transfer_status_layout, (ViewGroup) null, false);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        useButterKnife();
        initActionBar();
        initView();
        initState();
        updateViews();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TransferStatusActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.tvTransferAmount.setBold();
        this.tvRefuseTransfer.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.tvTransferTime.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.tvActionTime.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        this.btnTransferStateButton.setPrimaryRoundFillStyle((float) AndroidUtilities.dp(25.0f));
        this.btnTransferStateButton.setBackgroundColor(-12862209);
        this.btnTransferStateButton.setTextSize(2, 15.0f);
    }

    private void initState() {
        TLRPC.Message message2;
        if (this.transResponse == null && (message2 = this.message) != null && (message2.media instanceof TLRPCRedpacket.CL_messagesRpkTransferMedia)) {
            this.transResponse = (TransferResponse) TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_messagesRpkTransferMedia) this.message.media).data, (Class<?>) TransferResponse.class).model;
        }
        TransferResponse transferResponse = this.transResponse;
        if (transferResponse != null && this.targetUser != null) {
            this.isSender = transferResponse.getInitiatorUserIdInt() == getUserConfig().getClientUserId();
            if (NumberUtil.isNumber(this.transResponse.getTotalFee())) {
                this.total = DataTools.format2Decimals(new BigDecimal(this.transResponse.getTotalFee()).divide(new BigDecimal("100")).toString());
            }
            if (!this.isSender && NumberUtil.isNumber(this.transResponse.getInitiatorUserId())) {
                this.targetUser = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(this.transResponse.getInitiatorUserId())));
            } else if (NumberUtil.isNumber(this.transResponse.getRecipientUserId())) {
                this.targetUser = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(this.transResponse.getRecipientUserId())));
            }
            this.tvTransferAmount.setText(this.total);
            this.tvHongbaoType.setText(LocaleController.getString(R.string.UnitCNY));
        }
    }

    /* access modifiers changed from: private */
    public void updateViews() {
        String text;
        String text2;
        TransferResponse transferResponse = this.transResponse;
        if (transferResponse != null) {
            TransferResponse.Status state = transferResponse.getState();
            if (!TextUtils.isEmpty(this.transResponse.getRemarks())) {
                this.tvRemarks.setText(this.transResponse.getRemarks());
                this.tvRemarks.setVisibility(0);
            } else {
                this.tvRemarks.setVisibility(8);
            }
            if (state == TransferResponse.Status.NONE || this.complete) {
                this.ivTransferStateImg.setImageResource(R.mipmap.transfer_success_icon);
                this.btnTransferStateButton.setVisibility(0);
                this.btnTransferStateButton.setText(LocaleController.getString("Done", R.string.Done));
                this.tvTransferStateText.setText(LocaleController.formatString("PayCompleate", R.string.PayCompleate, UserObject.getName(this.targetUser, 10)));
                this.tvRefuseTransfer.setVisibility(8);
                this.tvRefuseTransfer.setText(LocaleController.getString("RefundWhenNotReceiveMoneyInOneDay", R.string.RefundWhenNotReceiveMoneyInOneDay));
                this.tvActionTime.setVisibility(8);
            } else if (state == TransferResponse.Status.WAITING) {
                this.ivTransferStateImg.setImageResource(R.mipmap.transfer_waiting_icon);
                this.tvActionTime.setVisibility(8);
                if (this.isSender) {
                    text2 = LocaleController.formatString("WaitingToReceiveMoney", R.string.WaitingToReceiveMoney, UserObject.getName(this.targetUser, 10));
                    this.btnTransferStateButton.setVisibility(8);
                    this.tvRefuseTransfer.setText(LocaleController.getString("RefundWhenNotReceiveMoneyInOneDay", R.string.RefundWhenNotReceiveMoneyInOneDay));
                    this.tvRefuseTransfer.setVisibility(0);
                    this.tvTransferTime.setText(LocaleController.formatString("TransferTimeWithColon", R.string.TransferTimeWithColon, this.transResponse.getCreateTimeFormat()));
                } else {
                    text2 = LocaleController.getString("WaitReceiveTransferMonney", R.string.WaitReceiveTransferMonney);
                    this.btnTransferStateButton.setVisibility(0);
                    SpanUtils.with(this.tvRefuseTransfer).append(LocaleController.getString("RefundWhenNotConfirmInOneDay", R.string.RefundWhenNotConfirmInOneDay)).setForegroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3)).append(LocaleController.getString("TransferRefundNow", R.string.TransferRefundNow)).setClickSpan(new ClickableSpan() {
                        public void onClick(View widget) {
                            TransferStatusActivity.this.showRefuseDialog();
                        }

                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(Color.parseColor("#ff268CFF"));
                        }
                    }).setForegroundColor(Color.parseColor("#ff268CFF")).create();
                    this.tvTransferTime.setText(LocaleController.formatString("TransferTimeWithColon", R.string.TransferTimeWithColon, this.transResponse.getCreateTimeFormat()));
                }
                this.tvRefuseTransfer.setVisibility(0);
                this.tvTransferStateText.setText(text2);
                this.btnTransferStateButton.setText(LocaleController.getString("ConfirmReceiptMoney", R.string.ConfirmReceiptMoney));
            } else if (state == TransferResponse.Status.RECEIVED) {
                this.btnTransferStateButton.setVisibility(8);
                this.tvRefuseTransfer.setVisibility(8);
                this.ivTransferStateImg.setImageResource(R.mipmap.transfer_success_icon);
                this.tvTransferStateText.setText(LocaleController.getString("ReceivableMoney", R.string.ReceivableMoney));
                if (!this.isSender) {
                    this.tvWallet.setVisibility(0);
                    this.tvWallet.setText(Html.fromHtml(LocaleController.getString(R.string.ViewWalletBalance)));
                }
                this.tvTransferTime.setText(LocaleController.formatString("TransferTimeWithColon", R.string.TransferTimeWithColon, this.transResponse.getCreateTimeFormat()));
                this.tvTransferTime.setVisibility(0);
                if (this.transResponse.getCollectTimeFormat() != null) {
                    this.tvActionTime.setVisibility(0);
                    this.tvActionTime.setText(LocaleController.formatString("TransferReceivedTimeWithColon", R.string.TransferReceivedTimeWithColon, this.transResponse.getCollectTimeFormat()));
                    return;
                }
                this.tvActionTime.setVisibility(8);
            } else if (state == TransferResponse.Status.REFUSED || state == TransferResponse.Status.TIMEOUT) {
                this.btnTransferStateButton.setVisibility(8);
                this.tvRefuseTransfer.setVisibility(8);
                this.ivTransferStateImg.setImageResource(R.mipmap.transfer_back_icon);
                if (this.isSender) {
                    text = LocaleController.formatString("TransferRefundByOtherSide", R.string.TransferRefundByOtherSide, UserObject.getName(this.targetUser, 10));
                } else if (state == TransferResponse.Status.REFUSED) {
                    text = LocaleController.getString(R.string.YouHaveRefused);
                } else {
                    text = LocaleController.getString("TransferRefunded", R.string.TransferRefunded);
                }
                this.tvTransferStateText.setText(text);
                if (this.isSender) {
                    this.tvWallet.setVisibility(0);
                    SpanUtils.with(this.tvWallet).append(LocaleController.getString("TransferRefundedWithComm", R.string.TransferRefundedWithComm)).setForegroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText)).append(LocaleController.getString("Wallet", R.string.Wallet)).setClickSpan(new ClickableSpan() {
                        public void onClick(View widget) {
                        }

                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(Color.parseColor("#fffe5548"));
                        }
                    }).setForegroundColor(Color.parseColor("#fffe5548")).create();
                } else {
                    this.tvWallet.setVisibility(8);
                }
                this.tvTransferTime.setText(LocaleController.formatString("TransferTimeWithColon", R.string.TransferTimeWithColon, this.transResponse.getCreateTimeFormat()));
                this.tvTransferTime.setVisibility(0);
                if (this.transResponse.getCancelTimeFormat() != null) {
                    this.tvActionTime.setVisibility(0);
                    this.tvActionTime.setText(LocaleController.formatString("TransferRefundedTimeWithColon", R.string.TransferRefundedTimeWithColon, this.transResponse.getCancelTimeFormat()));
                } else {
                    this.tvActionTime.setVisibility(8);
                }
                this.btnTransferStateButton.setVisibility(8);
            }
        }
    }

    private void receiveTransfer() {
        if (this.message != null && this.transResponse != null) {
            this.btnTransferStateButton.setEnabled(false);
            TLRPCRedpacket.CL_messages_rpkTransferReceive transferReceive = new TLRPCRedpacket.CL_messages_rpkTransferReceive();
            transferReceive.trans = 1;
            transferReceive.peer = getMessagesController().getInputPeer(this.transResponse.getInitiatorUserIdInt());
            transferReceive.id = this.message.id;
            transferReceive.flags = 2;
            TransferOperation transferOperation = new TransferOperation(this.transResponse.getSerialCode(), this.transResponse.getRecipientUserId(), StringUtils.getNonceStr(getConnectionsManager().getCurrentTime()), UnifyBean.BUSINESS_KEY_TRANSFER_RECEIVE, "0.0.1");
            TLRPC.TL_dataJSON dataJSON = new TLRPC.TL_dataJSON();
            dataJSON.data = new Gson().toJson((Object) transferOperation);
            transferReceive.data = dataJSON;
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
            ConnectionsManager connectionsManager = getConnectionsManager();
            int sendRequest = getConnectionsManager().sendRequest(transferReceive, new RequestDelegate(progressDialog) {
                private final /* synthetic */ XAlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TransferStatusActivity.this.lambda$receiveTransfer$8$TransferStatusActivity(this.f$1, tLObject, tL_error);
                }
            });
            this.mTokenReq = sendRequest;
            connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    TransferStatusActivity.this.lambda$receiveTransfer$9$TransferStatusActivity(dialogInterface);
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$receiveTransfer$8$TransferStatusActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        TLObject tLObject = response;
        TLRPC.TL_error tL_error = error;
        progressDialog.dismiss();
        int i = R.string.OtherDeviceSureTransfer;
        if (tL_error != null) {
            if ("REPEATED_REQUESTS".equals(tL_error.text)) {
                WalletDialogUtil.showSingleBtnWalletDialog((Object) this, (CharSequence) LocaleController.getString("OtherDeviceDoSame", R.string.OtherDeviceSureTransfer), LocaleController.getString("Back", R.string.Back), false, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferStatusActivity.this.lambda$null$0$TransferStatusActivity(dialogInterface, i);
                    }
                });
            } else {
                WalletErrorUtil.parseErrorDialog(this, tL_error.text, false, LocaleController.getString("Back", R.string.Back), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferStatusActivity.this.lambda$null$1$TransferStatusActivity(dialogInterface, i);
                    }
                });
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TransferStatusActivity.this.lambda$null$2$TransferStatusActivity();
                }
            });
        } else if (tLObject instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            Iterator<TLRPC.Update> it = updates.updates.iterator();
            while (it.hasNext()) {
                TLRPC.Update u = it.next();
                if (u instanceof TLRPCRedpacket.CL_updateRpkTransfer) {
                    TLApiModel<TransferResponse> parseResponse = TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_updateRpkTransfer) u).data, (Class<?>) TransferResponse.class);
                    if (parseResponse.isSuccess()) {
                        this.transResponse = (TransferResponse) parseResponse.model;
                        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                TransferStatusActivity.this.updateViews();
                            }
                        });
                        return;
                    } else if ("EXCLUSIVE_PLEASE_BIND_FIRST_BANKINFO".equals(parseResponse.message)) {
                        WalletDialogUtil.showWalletDialog(this, (String) null, LocaleController.getString(R.string.BankBindNorRecv), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), (DialogInterface.OnClickListener) null, $$Lambda$TransferStatusActivity$IiFCxcoXoWbaKk1LyPQ5vmLgrM.INSTANCE, (DialogInterface.OnDismissListener) null);
                        return;
                    } else if ("THE_TRANSFER_IS_NOT_AVAILABLE".equals(parseResponse.message) || "THE_TRANSFER_HAS_BEEN_CANCELLED".equals(parseResponse.message)) {
                        WalletErrorUtil.parseErrorDialog(this, parseResponse.message, false, LocaleController.getString("Back", R.string.Back), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                TransferStatusActivity.this.lambda$null$4$TransferStatusActivity(dialogInterface, i);
                            }
                        });
                        return;
                    } else if ("TRANSFER_COMPLETED_ERROR_CODE".equals(parseResponse.message)) {
                        this.transResponse = (TransferResponse) parseResponse.model;
                        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                TransferStatusActivity.this.updateViews();
                            }
                        });
                        return;
                    } else if ("REPEATED_REQUESTS".equals(parseResponse.message)) {
                        WalletDialogUtil.showSingleBtnWalletDialog((Object) this, (CharSequence) LocaleController.getString("OtherDeviceDoSame", i), LocaleController.getString("Back", R.string.Back), false, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                TransferStatusActivity.this.lambda$null$5$TransferStatusActivity(dialogInterface, i);
                            }
                        });
                    } else {
                        WalletErrorUtil.parseErrorDialog(this, parseResponse.message, false, LocaleController.getString("Back", R.string.Back), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                TransferStatusActivity.this.lambda$null$6$TransferStatusActivity(dialogInterface, i);
                            }
                        });
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                TransferStatusActivity.this.lambda$null$7$TransferStatusActivity();
                            }
                        });
                        return;
                    }
                } else if (u instanceof TLRPC.TL_updateEditMessage) {
                    TLRPC.TL_updateEditMessage update = (TLRPC.TL_updateEditMessage) u;
                    if (update.message.media instanceof TLRPCRedpacket.CL_messagesRpkTransferMedia) {
                        TLRPCRedpacket.CL_messagesRpkTransferMedia media = (TLRPCRedpacket.CL_messagesRpkTransferMedia) update.message.media;
                        TLApiModel<TransferResponse> parseResponse2 = TLJsonResolve.parse((TLObject) media.data, (Class<?>) TransferResponse.class);
                        if ("THE_TRANSFER_IS_NOT_AVAILABLE".equals(parseResponse2.message)) {
                            parseResponse2.code = "0";
                            parseResponse2.message = "SUCCESS_CODE";
                            media.data.data = ParamsUtil.toJson((Object) parseResponse2);
                            ((TLRPC.TL_updateEditMessage) u).message.media = media;
                            this.transResponse = (TransferResponse) parseResponse2.model;
                            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    TransferStatusActivity.this.updateViews();
                                }
                            });
                        }
                    }
                }
                i = R.string.OtherDeviceSureTransfer;
            }
        }
    }

    public /* synthetic */ void lambda$null$0$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$1$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$TransferStatusActivity() {
        this.btnTransferStateButton.setEnabled(true);
    }

    static /* synthetic */ void lambda$null$3(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$4$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$5$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$6$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$7$TransferStatusActivity() {
        this.btnTransferStateButton.setEnabled(true);
    }

    public /* synthetic */ void lambda$receiveTransfer$9$TransferStatusActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.mTokenReq, true);
        this.btnTransferStateButton.setEnabled(true);
    }

    private void refuseTransfer() {
        if (this.message != null && this.transResponse != null) {
            this.tvRefuseTransfer.setEnabled(false);
            TLRPCRedpacket.CL_messages_rpkTransferRefuse transferReceive = new TLRPCRedpacket.CL_messages_rpkTransferRefuse();
            transferReceive.trans = 1;
            transferReceive.peer = getMessagesController().getInputPeer(this.targetUser.id);
            transferReceive.id = this.message.id;
            transferReceive.flags = 2;
            TransferOperation transferOperation = new TransferOperation(this.transResponse.getSerialCode(), this.transResponse.getRecipientUserId(), StringUtils.getNonceStr(getConnectionsManager().getCurrentTime()), UnifyBean.BUSINESS_KEY_TRANSFER_REFUSE, "0.0.1");
            TLRPC.TL_dataJSON dataJSON = new TLRPC.TL_dataJSON();
            dataJSON.data = new Gson().toJson((Object) transferOperation);
            transferReceive.data = dataJSON;
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
            ConnectionsManager connectionsManager = getConnectionsManager();
            int sendRequest = getConnectionsManager().sendRequest(transferReceive, new RequestDelegate(progressDialog) {
                private final /* synthetic */ XAlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TransferStatusActivity.this.lambda$refuseTransfer$14$TransferStatusActivity(this.f$1, tLObject, tL_error);
                }
            });
            this.mTokenReq = sendRequest;
            connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    TransferStatusActivity.this.lambda$refuseTransfer$15$TransferStatusActivity(dialogInterface);
                }
            });
            try {
                progressDialog.show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$refuseTransfer$14$TransferStatusActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        progressDialog.dismiss();
        String str = "";
        if (error != null) {
            if ("TRANSFER_NON_CANCELLING_ERROR_CODE".equals(error.text)) {
                ToastUtils.show((CharSequence) LocaleController.getString("HaveRecievedCantRefuse", R.string.HaveRecievedCantRefuse));
            } else if ("TRANSFER_CANCELLING_ERROR_CODE".equals(error.text)) {
                ToastUtils.show((CharSequence) LocaleController.getString("HaveRefusedCantRefuse", R.string.HaveRefusedCantRefuse));
            } else if ("REPEATED_REQUESTS".equals(error.text)) {
                WalletDialogUtil.showSingleBtnWalletDialog((Object) this, (CharSequence) LocaleController.getString(R.string.OtherDeviceDoRefuse), LocaleController.getString("Back", R.string.Back), false, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferStatusActivity.this.lambda$null$10$TransferStatusActivity(dialogInterface, i);
                    }
                });
            } else {
                Object[] objArr = new Object[1];
                if (error.text != null) {
                    str = error.text;
                }
                objArr[0] = str;
                ToastUtils.showFormat(R.string.TransferRefundMoneyFailed, objArr);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TransferStatusActivity.this.lambda$null$11$TransferStatusActivity();
                }
            });
        } else if (response instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            Iterator<TLRPC.Update> it = updates.updates.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC.Update u = it.next();
                if (u instanceof TLRPCRedpacket.CL_updateRpkTransfer) {
                    TLApiModel<TransferResponse> parseResponse = TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_updateRpkTransfer) u).data, (Class<?>) TransferResponse.class);
                    if (!parseResponse.isSuccess()) {
                        if ("REPEATED_REQUESTS".equals(parseResponse.message)) {
                            WalletDialogUtil.showSingleBtnWalletDialog((Object) this, (CharSequence) "您的其它设备正在进行退款!", LocaleController.getString("Back", R.string.Back), false, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    TransferStatusActivity.this.lambda$null$12$TransferStatusActivity(dialogInterface, i);
                                }
                            });
                        } else {
                            Object[] objArr2 = new Object[1];
                            if (parseResponse.message != null) {
                                str = parseResponse.message;
                            }
                            objArr2[0] = str;
                            ToastUtils.showFormat(R.string.TransferRefundMoneyFailed, objArr2);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                TransferStatusActivity.this.lambda$null$13$TransferStatusActivity();
                            }
                        });
                        return;
                    }
                    this.transResponse = (TransferResponse) parseResponse.model;
                }
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TransferStatusActivity.this.updateViews();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$11$TransferStatusActivity() {
        this.tvRefuseTransfer.setEnabled(true);
    }

    public /* synthetic */ void lambda$null$12$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferStatusActivity.this.finishFragment();
            }
        });
    }

    public /* synthetic */ void lambda$null$13$TransferStatusActivity() {
        this.tvRefuseTransfer.setEnabled(true);
    }

    public /* synthetic */ void lambda$refuseTransfer$15$TransferStatusActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.mTokenReq, true);
        this.tvRefuseTransfer.setEnabled(true);
    }

    /* access modifiers changed from: private */
    public void showRefuseDialog() {
        if (this.transResponse != null) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(this.transResponse.getInitiatorUserIdInt()));
            String refuseText = "";
            if (user != null) {
                refuseText = LocaleController.formatString("TransferWhetherRefundTo", R.string.TransferWhetherRefundTo, UserObject.getName(user, 10));
            }
            WalletDialogUtil.showWalletDialog(this, "", refuseText, LocaleController.getString("Cancel", R.string.Cancel), LocaleController.getString("RefundTo", R.string.RefundTo), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TransferStatusActivity.this.lambda$showRefuseDialog$16$TransferStatusActivity(dialogInterface, i);
                }
            }, (DialogInterface.OnDismissListener) null);
        }
    }

    public /* synthetic */ void lambda$showRefuseDialog$16$TransferStatusActivity(DialogInterface dialogInterface, int i) {
        refuseTransfer();
    }

    @OnClick({2131297707, 2131296425})
    public void onClick(View view) {
        if (view.getId() == R.id.btnTransferStateButton) {
            if (this.complete) {
                finishFragment();
            } else {
                receiveTransfer();
            }
        }
    }
}
