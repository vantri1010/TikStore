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

public class WalletRecordWithdrawReturnDetailActivity extends BaseFragment {
    BillRecordResBillListBean bean;
    private AppTextView btnEmpty;
    private LinearLayout content;
    BillRecordDetailBean detailBean;
    private LinearLayout emptyLayout;
    private ImageView ivEmpty;
    private ImageView ivTradeIcon;
    private SpinKitView loadView;
    private TextView tvAmount;
    private TextView tvDesc;
    private TextView tvEmpty;
    private TextView tvTradeId;
    private TextView tvTradeServiceCharge;
    private TextView tvTradeStartTime;
    private TextView tvTradeStatus;
    private TextView tvTradeTitle;

    public void setBean(BillRecordResBillListBean bean2) {
        this.bean = bean2;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_record_withdraw_return_detail_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        showLoading();
        this.fragmentView.postDelayed(new Runnable() {
            public void run() {
                WalletRecordWithdrawReturnDetailActivity.this.loadRecordDetail();
            }
        }, 1000);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.RefundStr));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletRecordWithdrawReturnDetailActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.ivTradeIcon = (ImageView) this.fragmentView.findViewById(R.id.ivTradeIcon);
        this.tvTradeTitle = (TextView) this.fragmentView.findViewById(R.id.tvTradeTitle);
        this.tvAmount = (TextView) this.fragmentView.findViewById(R.id.tvAmount);
        this.tvTradeStatus = (TextView) this.fragmentView.findViewById(R.id.tvTradeStatus);
        this.tvTradeServiceCharge = (TextView) this.fragmentView.findViewById(R.id.tvTradeServiceCharge);
        this.tvTradeStartTime = (TextView) this.fragmentView.findViewById(R.id.tvTradeStartTime);
        this.tvTradeId = (TextView) this.fragmentView.findViewById(R.id.tvTradeId);
        this.content = (LinearLayout) this.fragmentView.findViewById(R.id.content);
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
    public void lambda$null$2$WalletRecordWithdrawReturnDetailActivity() {
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
                WalletRecordWithdrawReturnDetailActivity.this.showLoading();
                WalletRecordWithdrawReturnDetailActivity.this.loadRecordDetail();
            }
        });
    }

    private void setHeaderInfo() {
        BillRecordDetailBean billRecordDetailBean = this.detailBean;
        if (billRecordDetailBean != null) {
            this.ivTradeIcon.setImageResource(getTradeIcon(billRecordDetailBean.getOrderType()));
            this.tvTradeTitle.setText(LocaleController.getString(R.string.WithdrawalFailedRefund));
            ctrlDetails();
        }
    }

    private void ctrlDetails() {
        String returnStr = new BigDecimal(this.detailBean.getAmount()).add(new BigDecimal(this.detailBean.getServiceCharge())).divide(new BigDecimal("100")).toString();
        SpanUtils.with(this.tvAmount).append(this.detailBean.getDp()).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(returnStr, 2)).create();
        SpanUtils.with(this.tvTradeStatus).append(LocaleController.getString(R.string.Refunded)).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(returnStr, 2)).create();
        SpanUtils.with(this.tvTradeServiceCharge).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(this.detailBean.getServiceCharge()).divide(new BigDecimal("100")).toString(), 2)).create();
        String updateTime = this.detailBean.getUpdateTime();
        if (!TextUtils.isEmpty(updateTime)) {
            updateTime = TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", updateTime, "HH:mm:ss dd/MM/yy");
        }
        this.tvTradeStartTime.setText(updateTime);
        this.tvTradeId.setText(this.detailBean.getOrderId());
    }

    private int getTradeIcon(int type) {
        if (type != 3) {
            return R.mipmap.transfer_success_icon;
        }
        return R.mipmap.ic_transfer_refund;
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
                    WalletRecordWithdrawReturnDetailActivity.this.lambda$loadRecordDetail$3$WalletRecordWithdrawReturnDetailActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadRecordDetail$3$WalletRecordWithdrawReturnDetailActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordWithdrawReturnDetailActivity.this.lambda$null$0$WalletRecordWithdrawReturnDetailActivity();
                }
            });
        } else if (response instanceof TLRPCWallet.TL_paymentTransResult) {
            TLApiModel<BillRecordDetailBean> parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) response).data, BillRecordDetailBean.class);
            if (parse.isSuccess()) {
                this.detailBean = (BillRecordDetailBean) parse.model;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletRecordWithdrawReturnDetailActivity.this.lambda$null$1$WalletRecordWithdrawReturnDetailActivity();
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordWithdrawReturnDetailActivity.this.lambda$null$2$WalletRecordWithdrawReturnDetailActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$WalletRecordWithdrawReturnDetailActivity() {
        showContent();
        setHeaderInfo();
    }
}
