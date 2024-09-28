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
import com.litesuits.orm.db.assit.SQLBuilder;
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

public class WalletRecordWithdrawDetailActivity extends BaseFragment {
    BillRecordResBillListBean bean;
    private AppTextView btnEmpty;
    private LinearLayout content;
    BillRecordDetailBean detailBean;
    private LinearLayout emptyLayout;
    private ImageView ivEmpty;
    private ImageView ivEnd;
    private ImageView ivStart;
    private ImageView ivTradeIcon;
    private SpinKitView loadView;
    private LinearLayout reasonLayout;
    private TextView tvAmount;
    private TextView tvDesc;
    private TextView tvEmpty;
    private TextView tvEnd;
    private TextView tvEndTime;
    private TextView tvRechargeReason;
    private TextView tvStartTime;
    private TextView tvTradeAmount;
    private TextView tvTradeChannel;
    private TextView tvTradeEndTime;
    private TextView tvTradeEndTitle;
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
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_record_withdraw_detail_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        showLoading();
        this.fragmentView.postDelayed(new Runnable() {
            public void run() {
                WalletRecordWithdrawDetailActivity.this.loadRecordDetail();
            }
        }, 1000);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.Withdraw));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletRecordWithdrawDetailActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.ivTradeIcon = (ImageView) this.fragmentView.findViewById(R.id.ivTradeIcon);
        this.tvTradeTitle = (TextView) this.fragmentView.findViewById(R.id.tvTradeTitle);
        this.tvAmount = (TextView) this.fragmentView.findViewById(R.id.tvAmount);
        this.ivStart = (ImageView) this.fragmentView.findViewById(R.id.ivStart);
        this.ivEnd = (ImageView) this.fragmentView.findViewById(R.id.ivEnd);
        this.tvStartTime = (TextView) this.fragmentView.findViewById(R.id.tvStartTime);
        this.tvEnd = (TextView) this.fragmentView.findViewById(R.id.tvEnd);
        this.tvEndTime = (TextView) this.fragmentView.findViewById(R.id.tvEndTime);
        this.tvTradeStatus = (TextView) this.fragmentView.findViewById(R.id.tvTradeStatus);
        this.tvTradeAmount = (TextView) this.fragmentView.findViewById(R.id.tvTradeAmount);
        this.tvTradeServiceCharge = (TextView) this.fragmentView.findViewById(R.id.tvTradeServiceCharge);
        this.tvTradeStartTime = (TextView) this.fragmentView.findViewById(R.id.tvTradeStartTime);
        this.tvTradeEndTitle = (TextView) this.fragmentView.findViewById(R.id.tvTradeEndTitle);
        this.tvTradeEndTime = (TextView) this.fragmentView.findViewById(R.id.tvTradeEndTime);
        this.tvTradeChannel = (TextView) this.fragmentView.findViewById(R.id.tvTradeChannel);
        this.tvTradeId = (TextView) this.fragmentView.findViewById(R.id.tvTradeId);
        this.reasonLayout = (LinearLayout) this.fragmentView.findViewById(R.id.reasonLayout);
        this.tvRechargeReason = (TextView) this.fragmentView.findViewById(R.id.tvRechargeReason);
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
    public void lambda$null$2$WalletRecordWithdrawDetailActivity() {
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
                WalletRecordWithdrawDetailActivity.this.showLoading();
                WalletRecordWithdrawDetailActivity.this.loadRecordDetail();
            }
        });
    }

    private void setHeaderInfo() {
        String withdrawAmount;
        BillRecordDetailBean billRecordDetailBean = this.detailBean;
        if (billRecordDetailBean != null) {
            this.ivTradeIcon.setImageResource(getTradeIcon(billRecordDetailBean.getOrderType(), this.detailBean.getStatus()));
            ctrlTradeTitle();
            if (TextUtils.isEmpty(this.detailBean.getServiceCharge())) {
                withdrawAmount = new BigDecimal(this.detailBean.getAmount()).divide(new BigDecimal("100")).toString();
            } else {
                withdrawAmount = new BigDecimal(this.detailBean.getAmount()).add(new BigDecimal(this.detailBean.getServiceCharge())).divide(new BigDecimal("100")).toString();
            }
            SpanUtils.with(this.tvAmount).append(this.detailBean.getDp()).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(withdrawAmount, 2)).create();
            ctrlSteps();
            ctrlDetails();
        }
    }

    private void ctrlTradeTitle() {
        int status = this.detailBean.getStatus();
        String title = "";
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(this.detailBean.getSubInstitutionName())) {
            builder.append(this.detailBean.getSubInstitutionName());
        }
        if (!TextUtils.isEmpty(builder)) {
            title = String.format(LocaleController.getString(R.string.WithdrawalToFormat), new Object[]{builder.toString()});
        } else if (status == 2) {
            title = LocaleController.getString(R.string.WithdrawalFailure);
        } else if (status == 1) {
            title = LocaleController.getString(R.string.WithdrawalSuccess);
        } else if (status == 0) {
            title = LocaleController.getString(R.string.Processing);
        }
        this.tvTradeTitle.setText(title);
    }

    private void ctrlSteps() {
        String createTime = this.detailBean.getCreateTime();
        if (!TextUtils.isEmpty(createTime)) {
            createTime = TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", createTime, "HH:mm:ss dd/MM/yy");
        }
        this.tvStartTime.setText(createTime);
        String updateTime = this.detailBean.getUpdateTime();
        if (!TextUtils.isEmpty(updateTime)) {
            updateTime = TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", updateTime, "HH:mm:ss dd/MM/yy");
        }
        this.tvEndTime.setText(updateTime);
        int status = this.detailBean.getStatus();
        if (status == 2) {
            this.ivStart.setImageResource(R.drawable.shape_withdraw_gray_circle);
            this.tvEnd.setText(LocaleController.getString(R.string.WithdrawalFailure));
            this.tvEnd.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
            this.ivEnd.setImageResource(R.mipmap.ic_withdraw_step_failed);
        } else if (status == 0) {
            this.ivStart.setImageResource(R.drawable.shape_withdraw_blue_circle);
            this.tvEnd.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
            this.tvEnd.setText(LocaleController.getString(R.string.WithdrawalToAccount));
            this.ivEnd.setImageResource(R.mipmap.ic_withdraw_step_processing);
        } else {
            this.ivStart.setImageResource(R.drawable.shape_withdraw_gray_circle);
            this.tvEnd.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
            this.tvEnd.setText(LocaleController.getString(R.string.WithdrawalToAccount));
            this.ivEnd.setImageResource(R.mipmap.ic_withdraw_step_success);
        }
    }

    private void ctrlDetails() {
        String chan;
        int status = this.detailBean.getStatus();
        if (status == 2) {
            this.tvTradeStatus.setText(LocaleController.getString(R.string.Failed));
            this.tvTradeEndTitle.setText(LocaleController.getString(R.string.FailureTime));
            this.tvRechargeReason.setText(LocaleController.getString(R.string.WithdrawalFailure));
            this.reasonLayout.setVisibility(8);
        } else if (status == 0) {
            this.tvTradeStatus.setText(LocaleController.getString(R.string.Processing));
        } else {
            this.tvTradeStatus.setText(LocaleController.getString(R.string.Success));
            this.tvTradeEndTitle.setText(LocaleController.getString(R.string.PayBillArrivalTime));
            this.reasonLayout.setVisibility(8);
        }
        SpanUtils.with(this.tvTradeAmount).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(this.detailBean.getAmount()).divide(new BigDecimal("100")).toString(), 2)).create();
        SpanUtils.with(this.tvTradeServiceCharge).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(new BigDecimal(this.detailBean.getServiceCharge()).divide(new BigDecimal("100")).toString(), 2)).create();
        String createTime = this.detailBean.getCreateTime();
        if (!TextUtils.isEmpty(createTime)) {
            createTime = TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", createTime, "HH:mm:ss dd/MM/yy");
        }
        this.tvTradeStartTime.setText(createTime);
        String updateTime = this.detailBean.getUpdateTime();
        if (!TextUtils.isEmpty(updateTime)) {
            updateTime = TimeUtils.getTimeLocalString("yyyy-MM-dd HH:mm:ss", updateTime, "HH:mm:ss dd/MM/yy");
        }
        this.tvTradeEndTime.setText(updateTime);
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(this.detailBean.getInstitutionName())) {
            builder.append(this.detailBean.getInstitutionName());
        }
        String card = this.detailBean.getShortCardNumber();
        if (!TextUtils.isEmpty(card)) {
            builder.append(SQLBuilder.PARENTHESES_LEFT);
            builder.append(card);
            builder.append(SQLBuilder.PARENTHESES_RIGHT);
        }
        if (TextUtils.isEmpty(builder)) {
            chan = LocaleController.getString(R.string.UnKnown);
        } else {
            chan = builder.toString();
        }
        this.tvTradeChannel.setText(chan);
        this.tvTradeId.setText(this.detailBean.getOrderId());
    }

    private int getTradeIcon(int type, int status) {
        if (type != 0) {
            if (type != 1) {
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
                    default:
                        return R.mipmap.transfer_success_icon;
                }
            } else if (status == 0) {
                return R.mipmap.transfer_waiting_icon;
            } else {
                if (status == 1) {
                    return R.mipmap.transfer_success_icon;
                }
                return R.mipmap.ic_withdraw_failed;
            }
        } else if (status == 0 || status == 1) {
            return R.mipmap.ic_top_up_success;
        } else {
            return R.mipmap.ic_top_up_failed;
        }
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
                    WalletRecordWithdrawDetailActivity.this.lambda$loadRecordDetail$3$WalletRecordWithdrawDetailActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadRecordDetail$3$WalletRecordWithdrawDetailActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordWithdrawDetailActivity.this.lambda$null$0$WalletRecordWithdrawDetailActivity();
                }
            });
        } else if (response instanceof TLRPCWallet.TL_paymentTransResult) {
            TLApiModel<BillRecordDetailBean> parse = TLJsonResolve.parse3(((TLRPCWallet.TL_paymentTransResult) response).data, BillRecordDetailBean.class);
            if (parse.isSuccess()) {
                this.detailBean = (BillRecordDetailBean) parse.model;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        WalletRecordWithdrawDetailActivity.this.lambda$null$1$WalletRecordWithdrawDetailActivity();
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletRecordWithdrawDetailActivity.this.lambda$null$2$WalletRecordWithdrawDetailActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$WalletRecordWithdrawDetailActivity() {
        showContent();
        setHeaderInfo();
    }
}
