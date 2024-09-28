package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.ActionBarMenuItem;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.components.TextCell;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.wallet.utils.AnimationUtils;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;

public class WalletActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TextCell bankCell;
    private AppTextView btnSet;
    private boolean hide = true;
    private ImageView ivHide;
    private ImageView ivTip;
    private TextView ivUnit;
    /* access modifiers changed from: private */
    public SpinKitView loadView;
    private ActionBarMenuItem menuItem;
    private TextCell recordCell;
    private SmartRefreshLayout refreshLayout;
    private LinearLayout tipLayout;
    private AppTextView tvCharge;
    private AppTextView tvDesc;
    private AppTextView tvTips;
    private TextView tvTotal;
    private AppTextView tvTotalTip;
    private AppTextView tvWithdraw;
    private LinearLayout walletLayout;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.walletInfoNeedReload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentPasswordDidSet);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.walletInfoNeedReload);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentPasswordDidSet);
        ConnectionsManager.getInstance(this.currentAccount).cancelRequestsForGuid(this.classGuid);
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.MyWallet));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.menuItem = this.actionBar.createMenu().addItem(1, (int) R.mipmap.ic_more);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletActivity.this.finishFragment();
                } else if (id == 1) {
                    WalletActivity.this.presentFragment(new WalletManagementActivity());
                }
            }
        });
        this.menuItem.setVisibility(8);
        this.fragmentView.postDelayed(new Runnable() {
            public void run() {
                WalletActivity.this.loadWalletInfo();
            }
        }, 1000);
    }

    private void initViews() {
        this.refreshLayout = (SmartRefreshLayout) this.fragmentView.findViewById(R.id.refreshLayout);
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.tipLayout = (LinearLayout) this.fragmentView.findViewById(R.id.tipLayout);
        this.tvTotalTip = (AppTextView) this.fragmentView.findViewById(R.id.tvTotalTip);
        this.ivTip = (ImageView) this.fragmentView.findViewById(R.id.ivTip);
        this.tvTips = (AppTextView) this.fragmentView.findViewById(R.id.tvTips);
        this.tvDesc = (AppTextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btnSet = (AppTextView) this.fragmentView.findViewById(R.id.btnSet);
        this.walletLayout = (LinearLayout) this.fragmentView.findViewById(R.id.walletLayout);
        this.ivHide = (ImageView) this.fragmentView.findViewById(R.id.ivHide);
        this.tvTotal = (TextView) this.fragmentView.findViewById(R.id.tvTotal);
        this.ivUnit = (TextView) this.fragmentView.findViewById(R.id.ivUnit);
        this.tvWithdraw = (AppTextView) this.fragmentView.findViewById(R.id.tvWithdraw);
        this.tvCharge = (AppTextView) this.fragmentView.findViewById(R.id.tvCharge);
        this.bankCell = (TextCell) this.fragmentView.findViewById(R.id.bankCell);
        this.recordCell = (TextCell) this.fragmentView.findViewById(R.id.recordCell);
        this.tipLayout.setVisibility(8);
        this.walletLayout.setVisibility(8);
        this.refreshLayout.setEnableLoadMore(false);
        this.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public void onRefresh(RefreshLayout refreshLayout) {
                WalletActivity.this.loadWalletInfo(true);
            }
        });
        SpanUtils.with(this.tvTotalTip).append(LocaleController.getString(R.string.TotalAssets)).append(SQLBuilder.PARENTHESES_LEFT).append(LocaleController.getString(R.string.UnitCNY)).append(SQLBuilder.PARENTHESES_RIGHT).create();
        this.bankCell.setData(R.mipmap.ic_bank_card, LocaleController.getString(R.string.BankCard), R.mipmap.icon_arrow_right, true);
        this.recordCell.setData(R.mipmap.ic_balance_change, LocaleController.getString(R.string.TransactionDetails2), R.mipmap.icon_arrow_right, true);
        this.bankCell.clearColorFilter();
        this.recordCell.clearColorFilter();
        this.bankCell.setTitleSize(16);
        this.recordCell.setTitleSize(16);
        this.bankCell.setTypeface(Typeface.DEFAULT_BOLD);
        this.recordCell.setTypeface(Typeface.DEFAULT_BOLD);
        this.bankCell.setBackground(Theme.getSelectorDrawable(false));
        this.recordCell.setBackground(Theme.getSelectorDrawable(false));
        this.tvWithdraw.setBackground(Theme.getSelectorDrawable(false));
        this.tvCharge.setBackground(Theme.getSelectorDrawable(false));
        showLoading();
        hideCash(true);
        this.tvWithdraw.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActivity.this.lambda$initViews$0$WalletActivity(view);
            }
        });
        this.tvCharge.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActivity.this.lambda$initViews$1$WalletActivity(view);
            }
        });
        this.bankCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActivity.this.lambda$initViews$2$WalletActivity(view);
            }
        });
        this.recordCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActivity.this.lambda$initViews$3$WalletActivity(view);
            }
        });
        this.ivHide.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletActivity.this.lambda$initViews$4$WalletActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initViews$0$WalletActivity(View view) {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString(R.string.ContactKnotter), 16, true, false, false);
        dialog.setPositiveButton(LocaleController.getString(R.string.sure), (DialogInterface.OnClickListener) null);
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$initViews$1$WalletActivity(View view) {
        WalletDialog dialog = new WalletDialog(getParentActivity());
        dialog.setMessage(LocaleController.getString(R.string.ContactKnotterPush), 16, true, false, false);
        dialog.setPositiveButton(LocaleController.getString(R.string.sure), (DialogInterface.OnClickListener) null);
        showDialog(dialog);
    }

    public /* synthetic */ void lambda$initViews$2$WalletActivity(View view) {
        presentFragment(new WalletBankCardsActivity());
    }

    public /* synthetic */ void lambda$initViews$3$WalletActivity(View view) {
        presentFragment(new WalletRecordsActivity());
    }

    public /* synthetic */ void lambda$initViews$4$WalletActivity(View view) {
        boolean z = !this.hide;
        this.hide = z;
        hideCash(z);
    }

    private void hideCash(boolean hide2) {
        if (hide2) {
            this.tvTotal.setText("******");
            this.ivUnit.setVisibility(8);
            this.ivHide.setImageResource(R.mipmap.ic_wallet_total_no_view);
            return;
        }
        this.tvTotal.setText(MoneyUtil.formatToString(getWalletController().getAccountInfo().getCashAmount() / 100.0d, 2));
        this.ivUnit.setVisibility(0);
        this.ivHide.setImageResource(R.mipmap.ic_wallet_total_view);
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        this.tipLayout.setVisibility(0);
        this.walletLayout.setVisibility(8);
        this.ivTip.setVisibility(8);
        this.loadView.setVisibility(0);
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
        this.tvTips.setText(LocaleController.getString(R.string.NowLoading));
        this.tvDesc.setVisibility(8);
        this.btnSet.setVisibility(8);
    }

    private void showCreateTip() {
        this.menuItem.setVisibility(8);
        this.walletLayout.setVisibility(8);
        this.tipLayout.setVisibility(0);
        this.loadView.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.tipLayout);
        this.ivTip.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.btnSet.setVisibility(0);
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvTips.setText("钱包未创建");
        this.tvDesc.setText("点击下方按钮创建钱包");
        this.btnSet.setText("创建钱包");
        this.btnSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WalletActivity.this.createWallet();
            }
        });
    }

    private void showLockTip() {
        this.menuItem.setVisibility(8);
        this.walletLayout.setVisibility(8);
        this.tipLayout.setVisibility(0);
        this.loadView.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.tipLayout);
        this.ivTip.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.btnSet.setVisibility(8);
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvTips.setText(LocaleController.getString(R.string.AccountHadBeenForzen));
        this.tvDesc.setText(LocaleController.getString(R.string.FreezeTips));
    }

    /* access modifiers changed from: private */
    public void loadWalletInfo() {
        loadWalletInfo(false);
    }

    /* access modifiers changed from: private */
    public void loadWalletInfo(boolean refresh) {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCWallet.TL_getPaymentAccountInfo(), new RequestDelegate(refresh) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletActivity.this.lambda$loadWalletInfo$6$WalletActivity(this.f$1, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadWalletInfo$6$WalletActivity(boolean refresh, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(refresh, error, response) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                WalletActivity.this.lambda$null$5$WalletActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$WalletActivity(boolean refresh, TLRPC.TL_error error, TLObject response) {
        if (refresh) {
            this.refreshLayout.finishRefresh();
        }
        if (error != null) {
            SpinKitView spinKitView = this.loadView;
            if (spinKitView != null) {
                spinKitView.stop();
                this.loadView.setVisibility(8);
            }
            showError();
            ExceptionUtils.handleGetAccountInfoError(error.text);
        } else if (response instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            createWallet();
        } else {
            SpinKitView spinKitView2 = this.loadView;
            if (spinKitView2 != null) {
                spinKitView2.stop();
                this.loadView.setVisibility(8);
            }
            TLApiModel<WalletAccountInfo> model = TLJsonResolve.parse(response, (Class<?>) WalletAccountInfo.class);
            if (model.isSuccess()) {
                WalletAccountInfo accountInfo = (WalletAccountInfo) model.model;
                getWalletController().setAccountInfo(accountInfo);
                WalletConfigBean.setWalletAccountInfo(accountInfo);
                WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
                if (accountInfo.isLocked()) {
                    showLockTip();
                } else {
                    showAccountInfo(refresh);
                }
            } else {
                showError();
                ExceptionUtils.handleGetAccountInfoError(model.message);
            }
        }
    }

    /* access modifiers changed from: private */
    public void createWallet() {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCWallet.TL_createAccount(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletActivity.this.lambda$createWallet$7$WalletActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$createWallet$7$WalletActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (WalletActivity.this.loadView != null) {
                    WalletActivity.this.loadView.stop();
                    WalletActivity.this.loadView.setVisibility(8);
                }
                if (error != null) {
                    WalletActivity.this.showError();
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentAccountInfo) {
                    TLApiModel model = TLJsonResolve.parse(tLObject, (Class<?>) WalletAccountInfo.class);
                    if (model.isSuccess()) {
                        WalletAccountInfo accountInfo = (WalletAccountInfo) model.model;
                        WalletConfigBean.setWalletAccountInfo(accountInfo);
                        WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
                        WalletActivity.this.getWalletController().setAccountInfo(accountInfo);
                        WalletActivity.this.showAccountInfo(false);
                        return;
                    }
                    WalletActivity.this.showError();
                    ExceptionUtils.handleCreateAccountError(model.message);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showAccountInfo(boolean refresh) {
        this.menuItem.setVisibility(0);
        this.walletLayout.setVisibility(0);
        this.tipLayout.setVisibility(8);
        if (!refresh) {
            AnimationUtils.executeAlphaScaleDisplayAnimation(this.walletLayout);
        }
        hideCash(this.hide);
    }

    private void showPasswordTip() {
        this.menuItem.setVisibility(8);
        this.walletLayout.setVisibility(8);
        this.tipLayout.setVisibility(0);
        this.loadView.setVisibility(8);
        this.ivTip.setVisibility(0);
        this.tvDesc.setVisibility(0);
        this.btnSet.setVisibility(0);
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvTips.setText(LocaleController.getString(R.string.NoPaymentPassword));
        this.tvDesc.setText(LocaleController.getString(R.string.ThisFunNeedPayPassword));
    }

    /* access modifiers changed from: private */
    public void showError() {
        this.menuItem.setVisibility(8);
        this.walletLayout.setVisibility(8);
        this.tipLayout.setVisibility(0);
        this.loadView.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.tipLayout);
        this.ivTip.setVisibility(0);
        this.ivTip.setImageResource(R.mipmap.ic_data_ex);
        this.tvDesc.setVisibility(0);
        this.btnSet.setVisibility(0);
        this.tvTips.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvTips.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvDesc.setText(LocaleController.getString(R.string.ClickTheButtonToTryAgain));
        this.btnSet.setText(LocaleController.getString(R.string.Refresh));
        this.btnSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (WalletActivity.this.getWalletController().getAccountInfo() == null) {
                    WalletActivity.this.showLoading();
                    WalletActivity.this.loadWalletInfo();
                }
            }
        });
    }

    private void parseError(int errorCode, String errorMsg) {
        WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(errorMsg));
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.walletInfoNeedReload) {
            showLoading();
            loadWalletInfo();
        } else if (id == NotificationCenter.paymentPasswordDidSet && getWalletController().getAccountInfo() != null) {
            getWalletController().getAccountInfo().setIsSetPayWord("1");
        }
    }
}
