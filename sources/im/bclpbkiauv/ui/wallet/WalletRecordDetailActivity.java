package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SpanUtils;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.utils.number.TimeUtils;
import im.bclpbkiauv.ui.wallet.model.BillRecordDetailBean;
import im.bclpbkiauv.ui.wallet.model.BillRecordResBillListBean;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.utils.AnimationUtils;
import java.math.BigDecimal;

public class WalletRecordDetailActivity extends BaseFragment {
    BillRecordResBillListBean bean;
    private AppTextView btnEmpty;
    private LinearLayout content;
    BillRecordDetailBean detailBean;
    private LinearLayout emptyLayout;
    private LinearLayout fullLayout;
    private ImageView ivEmpty;
    private ImageView ivTradeIcon;
    private SpinKitView loadView;
    private LinearLayout tradeDescLayout;
    private TextView tvAmount;
    private TextView tvDesc;
    private TextView tvEmpty;
    private TextView tvFullRedPacket;
    private AppTextView tvOrderId;
    private TextView tvTitle;
    private TextView tvTradeDesc;
    private TextView tvTradeId;
    private TextView tvTradeStatus;
    private TextView tvTradeTime;
    private AppTextView tvTradeTimeDesc;

    public void setBean(BillRecordResBillListBean bean2) {
        this.bean = bean2;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_record_detail_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        showLoading();
        this.fragmentView.postDelayed(new Runnable() {
            public void run() {
                WalletRecordDetailActivity.this.loadRecordDetail();
            }
        }, 1000);
        return this.fragmentView;
    }

    private void initActionBar() {
        if (this.bean != null) {
            this.actionBar.setTitle(getTitle(this.bean.getOrderType()));
        } else {
            this.actionBar.setTitle(LocaleController.getString(R.string.PayBillDetails));
        }
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletRecordDetailActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.content = (LinearLayout) this.fragmentView.findViewById(R.id.content);
        this.ivTradeIcon = (ImageView) this.fragmentView.findViewById(R.id.ivTradeIcon);
        this.tvTitle = (TextView) this.fragmentView.findViewById(R.id.tvTitle);
        this.tvAmount = (TextView) this.fragmentView.findViewById(R.id.tvAmount);
        this.tradeDescLayout = (LinearLayout) this.fragmentView.findViewById(R.id.tradeDescLayout);
        this.tvTradeDesc = (TextView) this.fragmentView.findViewById(R.id.tvTradeDesc);
        this.fullLayout = (LinearLayout) this.fragmentView.findViewById(R.id.fullLayout);
        this.tvFullRedPacket = (TextView) this.fragmentView.findViewById(R.id.tvFullRedPacket);
        this.tvTradeStatus = (TextView) this.fragmentView.findViewById(R.id.tvTradeStatus);
        this.tvTradeTime = (TextView) this.fragmentView.findViewById(R.id.tvTradeTime);
        this.tvTradeId = (TextView) this.fragmentView.findViewById(R.id.tvTradeId);
        this.tvTradeTimeDesc = (AppTextView) this.fragmentView.findViewById(R.id.tvTradeTimeDesc);
        this.emptyLayout = (LinearLayout) this.fragmentView.findViewById(R.id.emptyLayout);
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.ivEmpty = (ImageView) this.fragmentView.findViewById(R.id.ivEmpty);
        this.tvEmpty = (TextView) this.fragmentView.findViewById(R.id.tvEmpty);
        this.tvDesc = (TextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btnEmpty = (AppTextView) this.fragmentView.findViewById(R.id.btnEmpty);
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        this.content.setVisibility(8);
        this.emptyLayout.setVisibility(0);
        this.ivEmpty.setVisibility(8);
        this.tvDesc.setVisibility(8);
        this.btnEmpty.setVisibility(8);
        this.loadView.setVisibility(0);
        this.tvEmpty.setVisibility(0);
        this.tvEmpty.setText(LocaleController.getString(R.string.NowLoading));
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
    }

    private void showContent() {
        this.content.setVisibility(0);
        this.emptyLayout.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.content);
    }

    /* access modifiers changed from: private */
    /* renamed from: showError */
    public void lambda$null$2$WalletRecordDetailActivity() {
        this.content.setVisibility(8);
        this.emptyLayout.setVisibility(0);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.emptyLayout);
        this.ivEmpty.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.btnEmpty.setVisibility(0);
        this.loadView.setVisibility(8);
        this.tvEmpty.setVisibility(0);
        this.tvEmpty.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvDesc.setText(LocaleController.getString(R.string.ClickTheButtonToTryAgain));
        this.btnEmpty.setText(LocaleController.getString(R.string.Refresh));
        this.btnEmpty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WalletRecordDetailActivity.this.showLoading();
                WalletRecordDetailActivity.this.loadRecordDetail();
            }
        });
    }

    private void initDatas() {
        if (this.detailBean != null) {
            setHeaderInfo();
        }
    }

    private void setHeaderInfo() {
        this.ivTradeIcon.setImageResource(getTradeIcon(this.detailBean.getOrderType(), this.detailBean.getStatus()));
        ctrlTradeDesc();
        SpanUtils.with(this.tvAmount).append(this.detailBean.getDp()).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(this.detailBean.getAmount()).divide(new BigDecimal("100")).toString(), 2)).create();
        ctrlViews();
    }

    private void ctrlTradeDesc() {
        int orderType = this.detailBean.getOrderType();
        String target = "";
        if (!TextUtils.isEmpty(this.bean.getGroupsNumber())) {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(Integer.parseInt(this.bean.getGroupsNumber())));
            if (chat != null) {
                target = chat.title;
            }
            if (TextUtils.isEmpty(target) && !TextUtils.isEmpty(this.detailBean.getGroupsName())) {
                target = this.detailBean.getGroupsName();
            }
        }
        if (TextUtils.isEmpty(target) && !TextUtils.isEmpty(this.detailBean.getEffectUserId())) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(Integer.parseInt(this.detailBean.getEffectUserId())));
            if (user != null) {
                target = user.first_name;
            }
            if (TextUtils.isEmpty(target) && !TextUtils.isEmpty(this.detailBean.getEffectUserName())) {
                target = this.detailBean.getEffectUserName();
            }
        }
        this.tvTitle.setText(getTitleDesc(orderType, target));
    }

    private void ctrlViews() {
        int orderType = this.detailBean.getOrderType();
        initFullLayout();
        initStatus();
        this.tvTradeId.setText(this.detailBean.getOrderId());
        this.tvTradeTimeDesc.setText(getTradeTimeStr(orderType));
        this.tvTradeTime.setText(getTradeTime());
        initTradeDesc();
    }

    private void initFullLayout() {
        if (!this.detailBean.isGroupRedPacketRefund()) {
            this.fullLayout.setVisibility(8);
        } else if (this.detailBean.isPartialRefund()) {
            this.fullLayout.setVisibility(0);
            SpanUtils.with(this.tvFullRedPacket).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(this.detailBean.getOriginalAmount()).divide(new BigDecimal("100")).toString(), 2)).create();
        } else {
            this.fullLayout.setVisibility(8);
        }
    }

    private void initTradeDesc() {
        if (this.detailBean.getOrderType() == 7) {
            this.tradeDescLayout.setVisibility(0);
            if (this.detailBean.getRefundTypeInt() == 0) {
                this.tvTradeDesc.setText(LocaleController.getString(R.string.ManualRefund));
            } else {
                this.tvTradeDesc.setText(LocaleController.getString(R.string.OverTimeRefund));
            }
        } else {
            this.tradeDescLayout.setVisibility(8);
        }
    }

    private String getTradeTime() {
        BillRecordDetailBean billRecordDetailBean = this.detailBean;
        if (billRecordDetailBean == null) {
            return "";
        }
        int orderType = billRecordDetailBean.getOrderType();
        if (orderType == 5 || orderType == 12 || orderType == 7 || orderType == 8) {
            String updateTime = this.detailBean.getUpdateTime();
            if (!TextUtils.isEmpty(updateTime)) {
                return TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", updateTime, "HH:mm:ss dd/MM/yy");
            }
            return updateTime;
        }
        String createTime = this.detailBean.getCreateTime();
        if (!TextUtils.isEmpty(createTime)) {
            return TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", createTime, "HH:mm:ss dd/MM/yy");
        }
        return createTime;
    }

    private String getTradeTimeStr(int type) {
        if (type != 0) {
            if (type != 21) {
                switch (type) {
                    case 5:
                        break;
                    case 6:
                        return LocaleController.getString(R.string.TransferTime);
                    case 7:
                        return LocaleController.getString(R.string.RefundTime);
                    case 8:
                        return LocaleController.getString(R.string.PayBillArrivalTime);
                    case 9:
                    case 10:
                    case 11:
                        return LocaleController.getString(R.string.PaidTime);
                    case 12:
                        return LocaleController.getString(R.string.PayBillArrivalTime);
                    case 13:
                        break;
                    default:
                        switch (type) {
                            case 25:
                                return LocaleController.getString(R.string.DebitTime);
                            case 26:
                            case 27:
                                return LocaleController.getString(R.string.PayBillArrivalTime);
                            default:
                                return LocaleController.getString(R.string.UnKnown);
                        }
                }
            }
            return LocaleController.getString(R.string.PayBillArrivalTime);
        }
        return LocaleController.getString(R.string.PayBillArrivalTime);
    }

    private void initStatus() {
        if (this.detailBean == null) {
            this.tvTradeStatus.setText(LocaleController.getString(R.string.UnKnown));
            return;
        }
        SpanUtils span = SpanUtils.with(this.tvTradeStatus);
        int type = this.detailBean.getOrderType();
        if (type == 5 || type == 8 || type == 13 || type == 21) {
            span.append(LocaleController.getString(R.string.ArrivaledAccount));
        } else if (type == 0 || type == 6 || type == 9 || type == 10 || type == 11 || type == 25 || type == 26 || type == 27) {
            span.append(LocaleController.getString(R.string.Success));
        } else if (type == 7) {
            this.tvTradeStatus.setTextColor(ColorUtils.getColor(R.color.text_red_color));
            span.append(LocaleController.getString(R.string.Refunded));
            String amount = this.detailBean.getAmount();
            if (TextUtils.isEmpty(amount)) {
                amount = "0";
            }
            span.append(" ").append("₫").setTypeface(Typeface.MONOSPACE);
            span.append(MoneyUtil.formatToString(new BigDecimal(amount).divide(new BigDecimal("100")).toString(), 2));
        } else if (type == 12) {
            this.tvTradeStatus.setTextColor(ColorUtils.getColor(R.color.text_red_color));
            if (this.detailBean.isPersonalRedPacketRefund()) {
                span.append(LocaleController.getString(R.string.Refunded));
            } else if (this.detailBean.isPartialRefund()) {
                span.append(LocaleController.getString(R.string.PartiallyRefunded));
            } else {
                span.append(LocaleController.getString(R.string.FullyRefunded));
            }
            String amount2 = this.detailBean.getAmount();
            if (TextUtils.isEmpty(amount2)) {
                amount2 = "0";
            }
            span.append(" ").append("₫").setTypeface(Typeface.MONOSPACE);
            span.append(MoneyUtil.formatToString(new BigDecimal(amount2).divide(new BigDecimal("100")).toString(), 2));
        } else {
            span.append(LocaleController.getString(R.string.UnKnown));
        }
        span.create();
    }

    private int getTradeIcon(int type, int status) {
        if (type != 0) {
            if (type != 1) {
                if (type == 21) {
                    return R.mipmap.ic_back_top_up;
                }
                switch (type) {
                    case 5:
                    case 6:
                    case 7:
                        return R.mipmap.ic_bill_detail_trasfer;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        return R.mipmap.ic_bill_detail_packet;
                    case 13:
                        return R.mipmap.ic_back_top_up;
                    default:
                        switch (type) {
                            case 25:
                                return R.mipmap.ic_back_top_up;
                            case 26:
                            case 27:
                                return R.mipmap.ic_order_live;
                            default:
                                return R.mipmap.transfer_success_icon;
                        }
                }
            } else if (status == 0 || status == 1) {
                return R.mipmap.transfer_success_icon;
            } else {
                return R.mipmap.ic_transfer_failed;
            }
        } else if (status == 0 || status == 1) {
            return R.mipmap.ic_top_up_success;
        } else {
            return R.mipmap.ic_top_up_failed;
        }
    }

    private String getTitle(int type) {
        if (type == 0) {
            return LocaleController.getString(R.string.TopUp);
        }
        if (type != 21) {
            switch (type) {
                case 5:
                case 6:
                case 7:
                    return LocaleController.getString(R.string.Transfer);
                case 8:
                case 9:
                case 10:
                case 11:
                    return LocaleController.getString(R.string.RedPacket);
                case 12:
                    return LocaleController.getString(R.string.RefundRedPacketsWithOut_);
                case 13:
                    break;
                default:
                    switch (type) {
                        case 25:
                            return LocaleController.getString(R.string.BackOfficeAccount);
                        case 26:
                        case 27:
                            return LocaleController.getString(R.string.LiveReward);
                        default:
                            return LocaleController.getString(R.string.PayBillDetails);
                    }
            }
        }
        return LocaleController.getString(R.string.BackstageAccount);
    }

    private String getTitleDesc(int type, String target) {
        if (type == 0) {
            return LocaleController.getString(R.string.TopUpToWallet);
        }
        if (type != 21) {
            switch (type) {
                case 5:
                    return String.format(LocaleController.getString(R.string.TransferFromSomebody), new Object[]{target});
                case 6:
                case 7:
                    return String.format(LocaleController.getString(R.string.TransferToSombody), new Object[]{target});
                case 8:
                    return String.format(LocaleController.getString(R.string.RedPacketFromSomebody), new Object[]{target});
                case 9:
                case 10:
                case 11:
                    return String.format(LocaleController.getString(R.string.RedPacketToSomebody), new Object[]{target});
                case 12:
                    return String.format(LocaleController.getString(R.string.RedPacketRefundFromSomebody), new Object[]{target});
                case 13:
                    break;
                default:
                    switch (type) {
                        case 25:
                            return LocaleController.getString(R.string.BackOfficeAccount);
                        case 26:
                            return String.format(LocaleController.getString(R.string.LiveRewardToFormat), new Object[]{target});
                        case 27:
                            return String.format(LocaleController.getString(R.string.LiveRewardFromFormat), new Object[]{target});
                        default:
                            return LocaleController.getString(R.string.UnKnown);
                    }
            }
        }
        return LocaleController.getString(R.string.BackstageAccount);
    }

    private String getTradeDesc(int type, String target) {
        if (type != 21) {
            switch (type) {
                case 5:
                    return String.format(LocaleController.getString(R.string.SombodyTransferToYou), new Object[]{target});
                case 6:
                case 7:
                    return String.format(LocaleController.getString(R.string.YouTransferToSomebody), new Object[]{target});
                case 8:
                    return String.format(LocaleController.getString(R.string.RedPacketFromSomebody), new Object[]{target});
                case 9:
                case 10:
                case 11:
                    return String.format(LocaleController.getString(R.string.RedPacketToSomebody), new Object[]{target});
                case 12:
                    return String.format(LocaleController.getString(R.string.RedPacketToSomebodyReturned), new Object[]{target});
                case 13:
                    break;
                default:
                    return LocaleController.getString(R.string.UnKnown);
            }
        }
        return LocaleController.getString(R.string.BackgroundTopUp);
    }

    /* access modifiers changed from: private */
    public void loadRecordDetail() {
        if (this.bean != null) {
            TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
            builder.setBusinessKey(Constants.KEY_ORDER_DETAIL);
            builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
            builder.addParam("orderId", this.bean.getOrderId());
            builder.addParam("orderType", Integer.valueOf(this.bean.getOrderType()));
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WalletRecordDetailActivity.this.lambda$loadRecordDetail$3$WalletRecordDetailActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadRecordDetail$3$WalletRecordDetailActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordDetailActivity.this.lambda$null$0$WalletRecordDetailActivity();
                }
            });
        } else if (response instanceof TLRPCWallet.TL_paymentTransResult) {
            TLApiModel<BillRecordDetailBean> parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) response).data, BillRecordDetailBean.class);
            if (parse.isSuccess()) {
                this.detailBean = (BillRecordDetailBean) parse.model;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletRecordDetailActivity.this.lambda$null$1$WalletRecordDetailActivity();
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordDetailActivity.this.lambda$null$2$WalletRecordDetailActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$WalletRecordDetailActivity() {
        showContent();
        initDatas();
    }
}
