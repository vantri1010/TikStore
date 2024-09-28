package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.TextCell;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;

public class WalletManagementActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private TextCell passwordCell;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.paymentPasswordDidSet);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.paymentPasswordDidSet);
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_actions_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        if (getWalletController().getAccountInfo() == null) {
            loadWalletInfo();
        }
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.WalletManagement));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletManagementActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.passwordCell = (TextCell) this.fragmentView.findViewById(R.id.passwordCell);
        setPasswordCell();
        this.passwordCell.setBackground(Theme.getSelectorDrawable(false));
        this.passwordCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (WalletManagementActivity.this.getWalletController().getAccountInfo() != null) {
                    if (WalletManagementActivity.this.getWalletController().getAccountInfo().hasPaypassword()) {
                        Bundle args = new Bundle();
                        args.putInt("step", 0);
                        args.putInt("type", 1);
                        WalletManagementActivity.this.presentFragment(new WalletPaymentPasswordActivity(args));
                        return;
                    }
                    Bundle args2 = new Bundle();
                    args2.putInt("step", 0);
                    args2.putInt("type", 0);
                    WalletManagementActivity.this.presentFragment(new WalletPaymentPasswordActivity(args2));
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void setPasswordCell() {
        if (getWalletController().getAccountInfo() == null) {
            return;
        }
        if (getWalletController().getAccountInfo().hasPaypassword()) {
            this.passwordCell.setText((int) R.mipmap.profile_shareout, LocaleController.getString(R.string.ModifyPayPassword), true, false);
        } else {
            this.passwordCell.setText((int) R.mipmap.profile_shareout, LocaleController.getString(R.string.SetPayPassword), true, false);
        }
    }

    private void loadWalletInfo() {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCWallet.TL_getPaymentAccountInfo(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletManagementActivity.this.lambda$loadWalletInfo$1$WalletManagementActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadWalletInfo$1$WalletManagementActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                WalletManagementActivity.this.lambda$null$0$WalletManagementActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$WalletManagementActivity(TLRPC.TL_error error, TLObject response) {
        if (error != null) {
            ExceptionUtils.handleGetAccountInfoError(error.text);
        } else if (response instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            createWallet();
        } else {
            TLApiModel<WalletAccountInfo> model = TLJsonResolve.parse(response, (Class<?>) WalletAccountInfo.class);
            if (model.isSuccess()) {
                WalletAccountInfo accountInfo = (WalletAccountInfo) model.model;
                getWalletController().setAccountInfo(accountInfo);
                WalletConfigBean.setWalletAccountInfo(accountInfo);
                WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
                setPasswordCell();
                return;
            }
            ExceptionUtils.handleGetAccountInfoError(model.message);
        }
    }

    private void createWallet() {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(new TLRPCWallet.TL_createAccount(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletManagementActivity.this.lambda$createWallet$2$WalletManagementActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$createWallet$2$WalletManagementActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (error != null) {
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
                        WalletManagementActivity.this.getWalletController().setAccountInfo(accountInfo);
                        WalletManagementActivity.this.setPasswordCell();
                        return;
                    }
                    ExceptionUtils.handleCreateAccountError(model.message);
                }
            }
        });
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.paymentPasswordDidSet && getWalletController().getAccountInfo() != null) {
            getWalletController().getAccountInfo().setIsSetPayWord("1");
            setPasswordCell();
        }
    }
}
