package im.bclpbkiauv.ui.wallet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AppTextView;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.TextCell;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.dialogs.WalletChannelAlert;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.load.SpinKitView;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.utils.number.MoneyUtil;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import im.bclpbkiauv.ui.wallet.WalletBankCardsActivity;
import im.bclpbkiauv.ui.wallet.WalletWithdrawActivity;
import im.bclpbkiauv.ui.wallet.adapter.PasswordKeyboardAdapter;
import im.bclpbkiauv.ui.wallet.model.AmountRulesBean;
import im.bclpbkiauv.ui.wallet.model.BankCardListResBean;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.model.PayChannelBean;
import im.bclpbkiauv.ui.wallet.model.PayChannelsResBean;
import im.bclpbkiauv.ui.wallet.model.PayTypeListBean;
import im.bclpbkiauv.ui.wallet.model.WithdrawResBean;
import im.bclpbkiauv.ui.wallet.utils.AnimationUtils;
import im.bclpbkiauv.ui.wallet.utils.CacheUtils;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletWithdrawActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public BankCardListResBean bankBean;
    private AppTextView btn;
    private AppTextView btnEmpty;
    private TextCell cardCell;
    /* access modifiers changed from: private */
    public boolean channelInited;
    private LinearLayout container;
    private LinearLayout emptyLayout;
    /* access modifiers changed from: private */
    public EditText etAmount;
    private ImageView ivEmpty;
    private SpinKitView loadView;
    /* access modifiers changed from: private */
    public boolean loadingPayChannels;
    /* access modifiers changed from: private */
    public List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    /* access modifiers changed from: private */
    public TextView[] mTvPasswords;
    /* access modifiers changed from: private */
    public int notEmptyTvCount;
    /* access modifiers changed from: private */
    public Dialog payAlert;
    /* access modifiers changed from: private */
    public ArrayList<PayChannelBean> payList = new ArrayList<>();
    /* access modifiers changed from: private */
    public PayChannelBean selectedPayType;
    private TextView tvAll;
    private TextView tvBalance;
    private TextView tvDesc;
    private TextView tvEmpty;
    /* access modifiers changed from: private */
    public AppTextView tvForgotPassword;
    /* access modifiers changed from: private */
    public TextView tvServiceCharge;
    private TextView tvServiceChargeDesc;

    static /* synthetic */ int access$1208(WalletWithdrawActivity x0) {
        int i = x0.notEmptyTvCount;
        x0.notEmptyTvCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1210(WalletWithdrawActivity x0) {
        int i = x0.notEmptyTvCount;
        x0.notEmptyTvCount = i - 1;
        return i;
    }

    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    public void setBtnEnable(boolean enable) {
        this.btn.setEnabled(enable);
        if (enable) {
            this.btn.setTextColor(ColorUtils.getColor(R.color.text_white_color));
            this.btn.setBackgroundResource(R.drawable.btn_primary_selector);
            return;
        }
        this.btn.setTextColor(ColorUtils.getColor(R.color.text_secondary_color));
        this.btn.setBackgroundResource(R.drawable.shape_rect_round_white);
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_withdraw_layout, (ViewGroup) null);
        this.emptyLayout = (LinearLayout) this.fragmentView.findViewById(R.id.emptyLayout);
        this.loadView = (SpinKitView) this.fragmentView.findViewById(R.id.loadView);
        this.ivEmpty = (ImageView) this.fragmentView.findViewById(R.id.ivEmpty);
        this.tvEmpty = (TextView) this.fragmentView.findViewById(R.id.tvEmpty);
        this.tvDesc = (TextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btnEmpty = (AppTextView) this.fragmentView.findViewById(R.id.btnEmpty);
        this.container = (LinearLayout) this.fragmentView.findViewById(R.id.container);
        this.cardCell = (TextCell) this.fragmentView.findViewById(R.id.cardCell);
        this.tvAll = (TextView) this.fragmentView.findViewById(R.id.tvAll);
        this.tvBalance = (TextView) this.fragmentView.findViewById(R.id.tvBalance);
        this.etAmount = (EditText) this.fragmentView.findViewById(R.id.etAmount);
        this.btn = (AppTextView) this.fragmentView.findViewById(R.id.btn);
        this.tvServiceCharge = (TextView) this.fragmentView.findViewById(R.id.tvServiceCharge);
        this.tvServiceChargeDesc = (TextView) this.fragmentView.findViewById(R.id.tvServiceChargeDesc);
        this.tvAll.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(4.0f), ColorUtils.getColor(R.color.btn_primary_color)));
        setBtnEnable(false);
        this.btnEmpty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WalletWithdrawActivity.this.showLoading();
                WalletWithdrawActivity.this.loadPayChannels();
            }
        });
        this.etAmount.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.etAmount.setHint(LocaleController.getString(R.string.PleaseInputWithdrawalMoneyAmount));
        this.etAmount.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                WalletWithdrawActivity.this.setBtnEnable((TextUtils.isEmpty(charSequence) || WalletWithdrawActivity.this.selectedPayType == null || WalletWithdrawActivity.this.bankBean == null) ? false : true);
                if (TextUtils.isEmpty(charSequence)) {
                    WalletWithdrawActivity.this.tvServiceCharge.setText("0.00");
                    return;
                }
                String amount = charSequence.toString().trim();
                String rate = "0";
                if (!(WalletWithdrawActivity.this.selectedPayType == null || WalletWithdrawActivity.this.selectedPayType.getPayType() == null || WalletWithdrawActivity.this.selectedPayType.getPayType().getRate() == null)) {
                    rate = WalletWithdrawActivity.this.selectedPayType.getPayType().getRate();
                    if (TextUtils.isEmpty(rate)) {
                        rate = "0";
                    }
                }
                if ("0".equals(rate)) {
                    WalletWithdrawActivity.this.tvServiceCharge.setText("0.00");
                    return;
                }
                BigDecimal multiply = new BigDecimal(amount).multiply(new BigDecimal(rate).divide(new BigDecimal("1000")));
                if (multiply.compareTo(new BigDecimal("0.10")) >= 0) {
                    WalletWithdrawActivity.this.tvServiceCharge.setText(MoneyUtil.formatToString(multiply.setScale(2, 0).toString(), 2, false));
                } else {
                    WalletWithdrawActivity.this.tvServiceCharge.setText("0.10");
                }
            }

            public void afterTextChanged(Editable editable) {
            }
        });
        this.tvAll.setText(LocaleController.getString(R.string.WithdrawAll));
        this.tvAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (WalletWithdrawActivity.this.getWalletController().getAccountInfo() != null) {
                    WalletWithdrawActivity.this.etAmount.setText(WalletWithdrawActivity.this.calcMaxValue());
                    WalletWithdrawActivity.this.etAmount.setSelection(WalletWithdrawActivity.this.etAmount.getText().length());
                }
            }
        });
        this.btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (AndroidUtilities.isKeyboardShowed(WalletWithdrawActivity.this.etAmount)) {
                    AndroidUtilities.hideKeyboard(WalletWithdrawActivity.this.etAmount);
                }
                if (WalletWithdrawActivity.this.getWalletController().getAccountInfo() != null && !WalletWithdrawActivity.this.getWalletController().getAccountInfo().hasPaypassword()) {
                    WalletDialogUtil.showWalletDialog(WalletWithdrawActivity.this, "", String.format(LocaleController.getString(R.string.PayPasswordNotSetTips), new Object[]{LocaleController.getString("Withdrawal", R.string.Withdrawal)}), LocaleController.getString("Close", R.string.Close), LocaleController.getString("redpacket_goto_set", R.string.redpacket_goto_set), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            WalletWithdrawActivity.AnonymousClass4.this.lambda$onClick$0$WalletWithdrawActivity$4(dialogInterface, i);
                        }
                    }, (DialogInterface.OnDismissListener) null);
                } else if (WalletWithdrawActivity.this.checkRules()) {
                    WalletWithdrawActivity.this.createPayAlert();
                }
            }

            public /* synthetic */ void lambda$onClick$0$WalletWithdrawActivity$4(DialogInterface dialogInterface, int i) {
                Bundle args = new Bundle();
                args.putInt("step", 0);
                args.putInt("type", 0);
                WalletWithdrawActivity.this.presentFragment(new WalletPaymentPasswordActivity(args));
            }
        });
        this.cardCell.clearColorFilter();
        this.cardCell.setBackground(Theme.getSelectorDrawable(false));
        this.cardCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                WalletWithdrawActivity walletWithdrawActivity = WalletWithdrawActivity.this;
                FragmentActivity parentActivity = WalletWithdrawActivity.this.getParentActivity();
                WalletWithdrawActivity walletWithdrawActivity2 = WalletWithdrawActivity.this;
                walletWithdrawActivity.showDialog(new WalletChannelAlert(parentActivity, walletWithdrawActivity2, walletWithdrawActivity2.payList, WalletWithdrawActivity.this.selectedPayType, 0, new WalletChannelAlert.ChannelAlertDelegate() {
                    public void onSelected(PayChannelBean bean) {
                        WalletWithdrawActivity.this.setSelectedType(bean);
                        WalletBankCardsActivity fragment = new WalletBankCardsActivity();
                        fragment.setBean(bean);
                        fragment.setBankBean(WalletWithdrawActivity.this.bankBean);
                        fragment.setDelegate(new WalletBankCardsActivity.BankCardDelegate() {
                            public void onSelected(BankCardListResBean bean) {
                                BankCardListResBean unused = WalletWithdrawActivity.this.bankBean = bean;
                                WalletWithdrawActivity.this.setSelectedType(WalletWithdrawActivity.this.selectedPayType);
                                CacheUtils cacheUtils = CacheUtils.get((Context) WalletWithdrawActivity.this.getParentActivity());
                                cacheUtils.put("selected_channel", (Serializable) WalletWithdrawActivity.this.selectedPayType);
                                cacheUtils.put("selected_bank", (Serializable) WalletWithdrawActivity.this.bankBean);
                            }
                        });
                        WalletWithdrawActivity.this.presentFragment(fragment);
                    }
                }));
            }
        });
        initActionBar();
        initAccountInfo();
        showLoading();
        loadPayChannels();
        CacheUtils cacheUtils = CacheUtils.get((Context) getParentActivity());
        this.selectedPayType = (PayChannelBean) cacheUtils.getAsObject("selected_channel");
        BankCardListResBean bankCardListResBean = (BankCardListResBean) cacheUtils.getAsObject("selected_bank");
        this.bankBean = bankCardListResBean;
        PayChannelBean payChannelBean = this.selectedPayType;
        if (payChannelBean == null || bankCardListResBean == null) {
            setSelectedType((PayChannelBean) null);
        } else {
            setSelectedType(payChannelBean);
        }
        return this.fragmentView;
    }

    public void performService(BaseFragment fragment) {
        String userString;
        int currentAccount = fragment.getCurrentAccount();
        SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        int uid = preferences.getInt("support_id", 0);
        TLRPC.User supportUser = null;
        if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 3);
            progressDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(preferences, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ XAlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    WalletWithdrawActivity.lambda$performService$2(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performService$2(SharedPreferences preferences, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ XAlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    WalletWithdrawActivity.lambda$null$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    WalletWithdrawActivity.lambda$null$1(XAlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$0(SharedPreferences preferences, TLRPC.TL_help_support res, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("support_id", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putInt("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$null$1(XAlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public String calcMaxValue() {
        double cashAmount = getWalletController().getAccountInfo().getCashAmount();
        PayChannelBean payChannelBean = this.selectedPayType;
        if (payChannelBean == null) {
            return MoneyUtil.formatToString(new BigDecimal(String.valueOf(cashAmount)).divide(new BigDecimal("100")).toString(), 2, false);
        }
        if (payChannelBean.getPayType() == null) {
            return MoneyUtil.formatToString(new BigDecimal(String.valueOf(cashAmount)).divide(new BigDecimal("100")).toString(), 2, false);
        }
        String rate = this.selectedPayType.getPayType().getRate();
        if (rate == null || TextUtils.isEmpty(rate)) {
            rate = "0";
        }
        String s = new BigDecimal(String.valueOf(cashAmount)).divide(new BigDecimal("1").add(new BigDecimal(rate).divide(new BigDecimal("1000"))), 0, 1).toString();
        if (new BigDecimal(s).multiply(new BigDecimal(rate).divide(new BigDecimal("1000"))).compareTo(new BigDecimal("0.10")) >= 0) {
            return MoneyUtil.formatToString(new BigDecimal(s).divide(new BigDecimal("100")).toString(), 2, false);
        }
        if ("0".equals(rate)) {
            return MoneyUtil.formatToString(new BigDecimal(String.valueOf(cashAmount)).divide(new BigDecimal("100")).toString(), 2, false);
        }
        return MoneyUtil.formatToString(new BigDecimal(String.valueOf(cashAmount)).divide(new BigDecimal("100")).subtract(new BigDecimal("0.10")).toString(), 2, false);
    }

    /* access modifiers changed from: private */
    public boolean checkRules() {
        String amount = this.etAmount.getText().toString().trim();
        BigDecimal bigAmount = new BigDecimal(amount).multiply(new BigDecimal("100"));
        String fee = this.tvServiceCharge.getText().toString();
        if (getWalletController().getAccountInfo() == null || new BigDecimal(amount).add(new BigDecimal(fee)).compareTo(new BigDecimal(getWalletController().getAccountInfo().getCashAmount()).divide(new BigDecimal("100"))) <= 0) {
            String minAmount = "";
            String maxAmount = "";
            PayChannelBean payChannelBean = this.selectedPayType;
            if (!(payChannelBean == null || payChannelBean.getPayType() == null || this.selectedPayType.getPayType().getAmountRules() == null)) {
                AmountRulesBean amountRules = this.selectedPayType.getPayType().getAmountRules();
                minAmount = amountRules.getMinAmount();
                maxAmount = amountRules.getMaxAmount();
            }
            if (!TextUtils.isEmpty(minAmount) && bigAmount.compareTo(new BigDecimal(minAmount).multiply(new BigDecimal("100"))) < 0) {
                ToastUtils.show((CharSequence) String.format(LocaleController.getString(R.string.WithdrawAmountNotLessThan), new Object[]{MoneyUtil.formatToString(minAmount, 2, false)}));
                return false;
            } else if (TextUtils.isEmpty(maxAmount) || bigAmount.compareTo(new BigDecimal(maxAmount).multiply(new BigDecimal("100"))) <= 0) {
                return true;
            } else {
                ToastUtils.show((CharSequence) String.format(LocaleController.getString(R.string.WithdrawAmountNotGreaterThan), new Object[]{MoneyUtil.formatToString(maxAmount, 2, false)}));
                return false;
            }
        } else {
            ToastUtils.show((CharSequence) LocaleController.getString(R.string.YourBalanceIsNotEnough));
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void showLoading() {
        this.container.setVisibility(8);
        this.btnEmpty.setVisibility(8);
        this.tvDesc.setVisibility(8);
        this.emptyLayout.setVisibility(0);
        this.loadView.setVisibility(0);
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
        this.tvEmpty.setText(LocaleController.getString(R.string.NowLoading));
        this.ivEmpty.setVisibility(8);
    }

    private void showError() {
        this.emptyLayout.setVisibility(0);
        this.container.setVisibility(8);
        this.tvDesc.setVisibility(8);
        this.loadView.setVisibility(8);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.emptyLayout);
        this.ivEmpty.setVisibility(0);
        this.ivEmpty.setImageResource(R.mipmap.ic_data_ex);
        this.btnEmpty.setVisibility(0);
        this.tvEmpty.setText(LocaleController.getString(R.string.SystemIsBusyAndTryAgainLater));
        this.tvEmpty.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
        this.tvDesc.setText(LocaleController.getString(R.string.ClickTheButtonToTryAgain));
        this.btnEmpty.setText(LocaleController.getString(R.string.Refresh));
    }

    private void showContainer() {
        this.emptyLayout.setVisibility(8);
        this.container.setVisibility(0);
        AnimationUtils.executeAlphaScaleDisplayAnimation(this.container);
    }

    /* access modifiers changed from: private */
    public void check() {
        if (!this.loadingPayChannels) {
            if (this.channelInited) {
                showContainer();
            } else {
                showError();
            }
        }
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.Withdraw));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletWithdrawActivity.this.finishFragment();
                }
            }
        });
    }

    private void initAccountInfo() {
        if (getWalletController().getAccountInfo() != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(MoneyUtil.formatToString(getWalletController().getAccountInfo().getCashAmount() / 100.0d, 2));
            this.tvBalance.setText(builder);
        }
    }

    /* access modifiers changed from: private */
    public void loadPayChannels() {
        this.loadingPayChannels = true;
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_PAY_CHANNELS);
        builder.addParam("belongType", "withdraw");
        builder.addParam("company", "Yixin");
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(builder.build(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletWithdrawActivity.this.lambda$loadPayChannels$3$WalletWithdrawActivity(tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadPayChannels$3$WalletWithdrawActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                boolean unused = WalletWithdrawActivity.this.loadingPayChannels = false;
                TLRPC.TL_error tL_error = error;
                if (tL_error != null) {
                    ExceptionUtils.handlePayChannelException(tL_error.text);
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PayChannelsResBean.class);
                    if (parse.isSuccess()) {
                        boolean unused2 = WalletWithdrawActivity.this.channelInited = true;
                        List modelList = parse.modelList;
                        if (modelList != null && !modelList.isEmpty()) {
                            WalletWithdrawActivity.this.parsePayChannel(modelList);
                        }
                    } else {
                        ExceptionUtils.handlePayChannelException(parse.message);
                    }
                }
                WalletWithdrawActivity.this.check();
            }
        });
    }

    /* access modifiers changed from: private */
    public void parsePayChannel(List<PayChannelsResBean> modelList) {
        ArrayList<PayTypeListBean> payTypeList;
        if (modelList != null && !modelList.isEmpty()) {
            for (int i = 0; i < modelList.size(); i++) {
                PayChannelsResBean payChannelsResBean = modelList.get(i);
                if (!(payChannelsResBean == null || payChannelsResBean.getPayTypeList() == null || payChannelsResBean.getPayTypeList().isEmpty() || (payTypeList = payChannelsResBean.getPayTypeList()) == null || payTypeList.isEmpty())) {
                    for (int j = 0; j < payTypeList.size(); j++) {
                        PayChannelBean bean = new PayChannelBean();
                        bean.setChannelCode(payChannelsResBean.getChannelCode());
                        bean.setPayType(payTypeList.get(j));
                        this.payList.add(bean);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void doWithdraw(String pwd) {
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_WITHDRAW_ORDER);
        builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
        builder.addParam("amount", new BigDecimal(this.etAmount.getText().toString().trim()).multiply(new BigDecimal("100")).toString());
        builder.addParam("bankId", Integer.valueOf(this.bankBean.getId()));
        builder.addParam("channelCode", this.selectedPayType.getChannelCode());
        builder.addParam("payPassword", AesUtils.encrypt(pwd));
        builder.addParam("withdrawType", this.selectedPayType.getPayType().getPayType());
        builder.addParam("requestId", StringUtils.getWithdrawStr());
        TLRPCWallet.TL_paymentTrans req = builder.build();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletWithdrawActivity.this.lambda$doWithdraw$4$WalletWithdrawActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletWithdrawActivity.this.lambda$doWithdraw$5$WalletWithdrawActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$doWithdraw$4$WalletWithdrawActivity(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                TLRPC.TL_error tL_error = error;
                if (tL_error == null) {
                    TLObject tLObject = response;
                    if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                        TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) WithdrawResBean.class);
                        if (parse.isSuccess()) {
                            if (WalletWithdrawActivity.this.payAlert != null) {
                                WalletWithdrawActivity.this.payAlert.dismiss();
                            }
                            WithdrawResBean withdrawResBean = (WithdrawResBean) parse.model;
                            WalletWithdrawActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.walletInfoNeedReload, new Object[0]);
                            WalletWithdrawActivity.this.finishFragment();
                        } else if (parse.message.contains("ACCOUNT_PASSWORD_IN_MINUTES,ERROR_TIMES,WILL_BE_FROZEN")) {
                            String[] split = parse.message.split("_");
                            String str = split[split.length - 2];
                            AndroidUtilities.runOnUIThread(new Runnable(split[split.length - 1]) {
                                private final /* synthetic */ String f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    WalletWithdrawActivity.AnonymousClass8.this.lambda$run$0$WalletWithdrawActivity$8(this.f$1);
                                }
                            });
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public final void run() {
                                    WalletWithdrawActivity.AnonymousClass8.this.lambda$run$1$WalletWithdrawActivity$8();
                                }
                            }, 2500);
                        } else if (WalletWithdrawActivity.this.handleSpecialException(parse.message)) {
                            ExceptionUtils.handlePayChannelException(parse.message);
                        }
                    }
                } else if (WalletWithdrawActivity.this.handleSpecialException(tL_error.text)) {
                    ExceptionUtils.handlePayChannelException(error.text);
                }
            }

            public /* synthetic */ void lambda$run$0$WalletWithdrawActivity$8(String time) {
                WalletWithdrawActivity.this.tvForgotPassword.setText(LocaleController.formatString("PassswordInputErrorText", R.string.PassswordInputErrorText, time));
                WalletWithdrawActivity.this.tvForgotPassword.setTextColor(ColorUtils.getColor(R.color.text_red_color));
                WalletWithdrawActivity.this.tvForgotPassword.setEnabled(false);
                for (TextView mTvPassword : WalletWithdrawActivity.this.mTvPasswords) {
                    mTvPassword.setTextColor(ColorUtils.getColor(R.color.text_red_color));
                }
            }

            public /* synthetic */ void lambda$run$1$WalletWithdrawActivity$8() {
                WalletWithdrawActivity.this.tvForgotPassword.setText(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                WalletWithdrawActivity.this.tvForgotPassword.setTextColor(ColorUtils.getColor(R.color.text_blue_color));
                WalletWithdrawActivity.this.tvForgotPassword.setEnabled(true);
                for (TextView mTvPassword : WalletWithdrawActivity.this.mTvPasswords) {
                    mTvPassword.setText((CharSequence) null);
                    mTvPassword.setTextColor(ColorUtils.getColor(R.color.text_primary_color));
                    int unused = WalletWithdrawActivity.this.notEmptyTvCount = 0;
                }
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$doWithdraw$5$WalletWithdrawActivity(int reqId, DialogInterface dialog) {
        getConnectionsManager().cancelRequest(reqId, true);
    }

    /* access modifiers changed from: private */
    public boolean handleSpecialException(String text) {
        Dialog dialog = this.payAlert;
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!"ACCOUNT_HAS_BEEN_FROZEN_CODE".equals(text)) {
            return true;
        }
        WalletDialogUtil.showConfirmBtnWalletDialog(this, LocaleController.getString(R.string.AccountHadBeenForzen), LocaleController.getString(R.string.AccountHasBeenFrozenTip), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
        return false;
    }

    /* access modifiers changed from: private */
    public void setSelectedType(PayChannelBean bean) {
        if (bean == null) {
            this.cardCell.setImageColorFilter(ColorUtils.getColor(R.color.icon_secondary_color));
            this.cardCell.setText(LocaleController.getString(R.string.SelecteWithdrawMethod), true, false);
            this.tvServiceCharge.setText("**");
            updateRate();
            return;
        }
        this.cardCell.clearColorFilter();
        this.etAmount.setText("");
        StringBuilder builder = new StringBuilder();
        this.selectedPayType = bean;
        builder.append(bean.getPayType().getName());
        if (this.bankBean != null) {
            builder.append("-");
            builder.append(this.bankBean.getReactType());
            if (!TextUtils.isEmpty(this.bankBean.getShortCardNumber())) {
                builder.append(SQLBuilder.PARENTHESES_LEFT);
                builder.append(this.bankBean.getShortCardNumber());
                builder.append(SQLBuilder.PARENTHESES_RIGHT);
            }
        }
        this.cardCell.setText(builder.toString(), true, false);
        updateRate();
    }

    private void updateRate() {
        PayChannelBean payChannelBean = this.selectedPayType;
        if (payChannelBean != null) {
            String rate = payChannelBean.getPayType().getRate();
            String min = "0.10";
            if (TextUtils.isEmpty(rate)) {
                rate = "0";
                min = "0.00";
            }
            SpanUtils.with(this.tvServiceChargeDesc).append(SQLBuilder.PARENTHESES_LEFT).append(LocaleController.getString(R.string.Rate)).append(MoneyUtil.formatToString(new BigDecimal(rate).divide(new BigDecimal("10")).toString(), 2)).append("%,").append(LocaleController.getString(R.string.MinValue)).append("₫").setTypeface(Typeface.MONOSPACE).append(min).append(SQLBuilder.PARENTHESES_RIGHT).create();
            return;
        }
        SpanUtils.with(this.tvServiceChargeDesc).append(SQLBuilder.PARENTHESES_LEFT).append(LocaleController.getString(R.string.Rate)).append("**").append("%,").append(LocaleController.getString(R.string.MinValue)).append("₫").setTypeface(Typeface.MONOSPACE).append("0.00").append(SQLBuilder.PARENTHESES_RIGHT).create();
    }

    /* access modifiers changed from: private */
    public void createPayAlert() {
        int i;
        String serviceCharge;
        BottomSheet.Builder builder = new BottomSheet.Builder((Context) getParentActivity(), 2);
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        View sheet = LayoutInflater.from(getParentActivity()).inflate(R.layout.layout_pay_alert_layout, (ViewGroup) null, false);
        builder.setCustomView(sheet);
        ImageView ivClose = (ImageView) sheet.findViewById(R.id.iv_back);
        ivClose.setBackground(Theme.createSelectorDrawable(ColorUtils.getColor(R.color.click_selector)));
        ivClose.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletWithdrawActivity.this.lambda$createPayAlert$6$WalletWithdrawActivity(view);
            }
        });
        TextView tvTitle = (TextView) sheet.findViewById(R.id.tv_title);
        TextView tvService = (TextView) sheet.findViewById(R.id.tvService);
        TextView tvRate = (TextView) sheet.findViewById(R.id.tvRate);
        this.tvForgotPassword = (AppTextView) sheet.findViewById(R.id.tv_forgot_password);
        tvTitle.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        tvTitle.setText(LocaleController.getString(R.string.PayPassword));
        ((TextView) sheet.findViewById(R.id.tvAction)).setText(LocaleController.getString(R.string.WithdrawalToBankCard));
        String amount = this.etAmount.getText().toString().trim();
        SpanUtils.with((TextView) sheet.findViewById(R.id.tvAmount)).append("₫").setTypeface(Typeface.MONOSPACE).append(MoneyUtil.formatToString(amount, 2, false)).create();
        String rate = this.selectedPayType.getPayType().getRate();
        BigDecimal bigRate = new BigDecimal(amount).multiply(new BigDecimal(rate).divide(new BigDecimal("1000")));
        if (bigRate.compareTo(new BigDecimal("0.1")) < 0) {
            serviceCharge = "0.1";
            i = 2;
        } else {
            i = 2;
            serviceCharge = bigRate.setScale(2, 0).toString();
        }
        BigDecimal bigDecimal = bigRate;
        SpanUtils.with(tvService).append(MoneyUtil.formatToString(serviceCharge, i)).append(LocaleController.getString(R.string.UnitMoneyYuan)).create();
        StringBuilder sb = new StringBuilder();
        String str = serviceCharge;
        sb.append(MoneyUtil.formatToString(new BigDecimal(rate).divide(new BigDecimal("10")).toString(), 2));
        sb.append("%");
        tvRate.setText(sb.toString());
        this.tvForgotPassword.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                WalletWithdrawActivity.this.lambda$createPayAlert$7$WalletWithdrawActivity(view);
            }
        });
        TextView[] textViewArr = new TextView[6];
        this.mTvPasswords = textViewArr;
        textViewArr[0] = (TextView) sheet.findViewById(R.id.tv_password_1);
        this.mTvPasswords[1] = (TextView) sheet.findViewById(R.id.tv_password_2);
        this.mTvPasswords[2] = (TextView) sheet.findViewById(R.id.tv_password_3);
        this.mTvPasswords[3] = (TextView) sheet.findViewById(R.id.tv_password_4);
        this.mTvPasswords[4] = (TextView) sheet.findViewById(R.id.tv_password_5);
        this.mTvPasswords[5] = (TextView) sheet.findViewById(R.id.tv_password_6);
        this.mTvPasswords[0].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        this.mTvPasswords[1].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        this.mTvPasswords[2].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        this.mTvPasswords[3].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        this.mTvPasswords[4].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        this.mTvPasswords[5].setBackgroundResource(R.drawable.shape_payment_password_gray_bg);
        RecyclerListView gvKeyboard = (RecyclerListView) sheet.findViewById(R.id.keyboardList);
        gvKeyboard.setLayoutManager(new GridLayoutManager(getParentActivity(), 3));
        gvKeyboard.setAdapter(new PasswordKeyboardAdapter(getParentActivity(), this.mNumbers));
        gvKeyboard.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position < 9 || position == 10) {
                    if (WalletWithdrawActivity.this.notEmptyTvCount != WalletWithdrawActivity.this.mTvPasswords.length) {
                        TextView[] access$1300 = WalletWithdrawActivity.this.mTvPasswords;
                        int length = access$1300.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            TextView textView = access$1300[i];
                            if (TextUtils.isEmpty(textView.getText())) {
                                textView.setText(String.valueOf(WalletWithdrawActivity.this.mNumbers.get(position)));
                                WalletWithdrawActivity.access$1208(WalletWithdrawActivity.this);
                                break;
                            }
                            i++;
                        }
                        if (WalletWithdrawActivity.this.notEmptyTvCount == WalletWithdrawActivity.this.mTvPasswords.length) {
                            StringBuilder password = new StringBuilder();
                            for (TextView textView2 : WalletWithdrawActivity.this.mTvPasswords) {
                                String text = textView2.getText().toString();
                                if (!TextUtils.isEmpty(text)) {
                                    password.append(text);
                                }
                            }
                            WalletWithdrawActivity.this.doWithdraw(password.toString());
                        }
                    }
                } else if (position == 11 && WalletWithdrawActivity.this.notEmptyTvCount != 0) {
                    for (int i2 = WalletWithdrawActivity.this.mTvPasswords.length - 1; i2 >= 0; i2--) {
                        if (!TextUtils.isEmpty(WalletWithdrawActivity.this.mTvPasswords[i2].getText())) {
                            WalletWithdrawActivity.this.mTvPasswords[i2].setText((CharSequence) null);
                            WalletWithdrawActivity.access$1210(WalletWithdrawActivity.this);
                            return;
                        }
                    }
                }
            }
        });
        Dialog showDialog = showDialog(builder.create());
        this.payAlert = showDialog;
        showDialog.setCanceledOnTouchOutside(false);
        this.payAlert.setCancelable(false);
        this.payAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                WalletWithdrawActivity.this.lambda$createPayAlert$8$WalletWithdrawActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$createPayAlert$6$WalletWithdrawActivity(View v) {
        dismissCurrentDialog();
    }

    public /* synthetic */ void lambda$createPayAlert$7$WalletWithdrawActivity(View v) {
        WalletDialogUtil.showSingleBtnWalletDialog(this, LocaleController.getString(R.string.ForgetPassword), LocaleController.getString(R.string.PleaseContactRelevantStaff), LocaleController.getString(R.string.Understood), true, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$createPayAlert$8$WalletWithdrawActivity(DialogInterface dialog1) {
        this.notEmptyTvCount = 0;
    }
}
