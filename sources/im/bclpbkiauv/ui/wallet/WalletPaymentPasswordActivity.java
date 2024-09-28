package im.bclpbkiauv.ui.wallet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.blankj.utilcode.util.ColorUtils;
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
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.components.RecyclerListView;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.wallet.model.Constants;
import im.bclpbkiauv.ui.wallet.model.PayPasswordReqBean;
import im.bclpbkiauv.ui.wallet.model.PaymentPasswordResBean;
import im.bclpbkiauv.ui.wallet.utils.ExceptionUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalletPaymentPasswordActivity extends BaseFragment {
    public static final int TYPE_MODIFY_PASSWORD = 1;
    public static final int TYPE_RESET_PASSWORD = 2;
    public static final int TYPE_SET_PASSWORD = 0;
    private TextView btn;
    private RecyclerListView keyboardList;
    /* access modifiers changed from: private */
    public List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    /* access modifiers changed from: private */
    public TextView[] mTvPasswords;
    /* access modifiers changed from: private */
    public int notEmptyTvCount;
    /* access modifiers changed from: private */
    public String passwordOld;
    /* access modifiers changed from: private */
    public String passwordOne;
    /* access modifiers changed from: private */
    public String passwordTwo;
    /* access modifiers changed from: private */
    public int step;
    /* access modifiers changed from: private */
    public TextView tvDesc;
    private TextView tvTips;
    /* access modifiers changed from: private */
    public int type;

    static /* synthetic */ int access$608(WalletPaymentPasswordActivity x0) {
        int i = x0.notEmptyTvCount;
        x0.notEmptyTvCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$610(WalletPaymentPasswordActivity x0) {
        int i = x0.notEmptyTvCount;
        x0.notEmptyTvCount = i - 1;
        return i;
    }

    public WalletPaymentPasswordActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        if (this.arguments != null) {
            this.type = this.arguments.getInt("type", 0);
            this.step = this.arguments.getInt("step", 0);
            this.passwordOne = this.arguments.getString("password", "");
            this.passwordOld = this.arguments.getString("password_old", "");
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_wallet_payment_password_layout, (ViewGroup) null);
        initActionBar();
        initViews();
        return this.fragmentView;
    }

    private void initActionBar() {
        String title;
        int i = this.type;
        if (i == 0) {
            title = LocaleController.getString(R.string.SetPayPassword);
        } else if (i == 1) {
            title = LocaleController.getString(R.string.ModifyPayPassword);
        } else if (i == 2) {
            title = LocaleController.getString(R.string.ResetPaymentPassword);
        } else {
            title = LocaleController.getString(R.string.SetPayPassword);
        }
        this.actionBar.setTitle(title);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setCastShadows(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WalletPaymentPasswordActivity.this.finishFragment();
                }
            }
        });
    }

    private void initViews() {
        this.keyboardList = (RecyclerListView) this.fragmentView.findViewById(R.id.keyboardList);
        this.tvTips = (TextView) this.fragmentView.findViewById(R.id.tvTips);
        this.tvDesc = (TextView) this.fragmentView.findViewById(R.id.tvDesc);
        this.btn = (TextView) this.fragmentView.findViewById(R.id.btn);
        TextView[] textViewArr = new TextView[6];
        this.mTvPasswords = textViewArr;
        textViewArr[0] = (TextView) this.fragmentView.findViewById(R.id.tv_password_1);
        this.mTvPasswords[1] = (TextView) this.fragmentView.findViewById(R.id.tv_password_2);
        this.mTvPasswords[2] = (TextView) this.fragmentView.findViewById(R.id.tv_password_3);
        this.mTvPasswords[3] = (TextView) this.fragmentView.findViewById(R.id.tv_password_4);
        this.mTvPasswords[4] = (TextView) this.fragmentView.findViewById(R.id.tv_password_5);
        this.mTvPasswords[5] = (TextView) this.fragmentView.findViewById(R.id.tv_password_6);
        initTips();
        initKeyboard();
    }

    private void initTips() {
        int i = this.type;
        if (i == 0) {
            if (this.step == 0) {
                this.tvTips.setText(LocaleController.getString(R.string.PayPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.PleaseSetYourPaymentPassword));
                this.btn.setText(LocaleController.getString(R.string.Next));
            } else {
                this.tvTips.setText(LocaleController.getString(R.string.ConfirmPaymentPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.EmptyConfirmPayPasswordTips));
                this.btn.setText(LocaleController.getString(R.string.Done));
            }
        } else if (i == 1) {
            int i2 = this.step;
            if (i2 == 0) {
                this.tvTips.setText(LocaleController.getString(R.string.PleaseInputPayPasswordToVerfiyIdentity));
                this.tvDesc.setText(LocaleController.getString(R.string.EmptyOldPayPasswordTips));
                this.tvDesc.setVisibility(8);
                this.btn.setText(LocaleController.getString(R.string.Next));
            } else if (i2 == 1) {
                this.tvTips.setText(LocaleController.getString(R.string.NewPayPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.EmptyNewPayPasswordTips));
                this.btn.setText(LocaleController.getString(R.string.Next));
            } else {
                this.tvTips.setText(LocaleController.getString(R.string.ConfirmNewPayPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.EmptyConfirmNewPayPasswordTips));
                this.btn.setText(LocaleController.getString(R.string.Done));
            }
        } else if (i == 2) {
            if (this.step == 0) {
                this.tvTips.setText(LocaleController.getString(R.string.PayPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.PleaseSetYourPaymentPassword));
                this.btn.setText(LocaleController.getString(R.string.Next));
            } else {
                this.tvTips.setText(LocaleController.getString(R.string.ConfirmPaymentPassword));
                this.tvDesc.setText(LocaleController.getString(R.string.SetPayPasswordTipsAgain));
                this.btn.setText(LocaleController.getString(R.string.Done));
            }
        }
        this.btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuilder password = new StringBuilder();
                for (TextView textView : WalletPaymentPasswordActivity.this.mTvPasswords) {
                    String text = textView.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        password.append(text);
                    }
                }
                if (password.toString().trim().length() != 6) {
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.PaymentPasswordNeed6Digits));
                } else if (WalletPaymentPasswordActivity.this.type == 0) {
                    if (WalletPaymentPasswordActivity.this.step == 0) {
                        Bundle args = new Bundle();
                        args.putInt("type", WalletPaymentPasswordActivity.this.type);
                        args.putInt("step", 1);
                        args.putString("password", password.toString().trim());
                        WalletPaymentPasswordActivity.this.presentFragment(new WalletPaymentPasswordActivity(args), true);
                    } else if (TextUtils.isEmpty(WalletPaymentPasswordActivity.this.passwordOne) || TextUtils.isEmpty(password)) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.SystemErrorTryLater));
                        WalletPaymentPasswordActivity.this.finishFragment();
                    } else if (!WalletPaymentPasswordActivity.this.passwordOne.equals(password.toString())) {
                        WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_red_color));
                        WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.PasswordErrorTryAgain));
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
                                WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.EmptyConfirmPayPasswordTips));
                            }
                        }, 1000);
                        WalletPaymentPasswordActivity.this.clearText();
                        int unused = WalletPaymentPasswordActivity.this.notEmptyTvCount = 0;
                    } else {
                        String unused2 = WalletPaymentPasswordActivity.this.passwordTwo = password.toString();
                        WalletPaymentPasswordActivity.this.setPassword();
                    }
                } else if (WalletPaymentPasswordActivity.this.type == 1) {
                    if (WalletPaymentPasswordActivity.this.step == 0) {
                        WalletPaymentPasswordActivity.this.checkOldPassword(password.toString().trim());
                    } else if (WalletPaymentPasswordActivity.this.step == 1) {
                        Bundle args2 = new Bundle();
                        args2.putInt("type", WalletPaymentPasswordActivity.this.type);
                        args2.putInt("step", 2);
                        args2.putString("password_old", WalletPaymentPasswordActivity.this.passwordOld);
                        args2.putString("password", password.toString().trim());
                        WalletPaymentPasswordActivity.this.presentFragment(new WalletPaymentPasswordActivity(args2), true);
                    } else if (TextUtils.isEmpty(WalletPaymentPasswordActivity.this.passwordOne) || TextUtils.isEmpty(WalletPaymentPasswordActivity.this.passwordOld) || TextUtils.isEmpty(password)) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.SystemErrorTryLater));
                        WalletPaymentPasswordActivity.this.finishFragment();
                    } else if (!WalletPaymentPasswordActivity.this.passwordOne.equals(password.toString())) {
                        WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_red_color));
                        WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.PasswordErrorTryAgain));
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
                                WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.EmptyConfirmNewPayPasswordTips));
                            }
                        }, 1000);
                        WalletPaymentPasswordActivity.this.clearText();
                        int unused3 = WalletPaymentPasswordActivity.this.notEmptyTvCount = 0;
                    } else {
                        String unused4 = WalletPaymentPasswordActivity.this.passwordTwo = password.toString();
                        WalletPaymentPasswordActivity.this.modifyPassword();
                    }
                } else if (WalletPaymentPasswordActivity.this.type != 2) {
                } else {
                    if (WalletPaymentPasswordActivity.this.step == 0) {
                        Bundle args3 = new Bundle();
                        args3.putInt("type", WalletPaymentPasswordActivity.this.type);
                        args3.putInt("step", 1);
                        args3.putString("password", password.toString().trim());
                        WalletPaymentPasswordActivity.this.presentFragment(new WalletPaymentPasswordActivity(args3), true);
                    } else if (TextUtils.isEmpty(WalletPaymentPasswordActivity.this.passwordOne) || TextUtils.isEmpty(password)) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.SystemErrorTryLater));
                        WalletPaymentPasswordActivity.this.finishFragment();
                    } else if (!WalletPaymentPasswordActivity.this.passwordOne.equals(password.toString())) {
                        WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_red_color));
                        WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.PasswordErrorTryAgain));
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                WalletPaymentPasswordActivity.this.tvDesc.setTextColor(ColorUtils.getColor(R.color.text_descriptive_color));
                                WalletPaymentPasswordActivity.this.tvDesc.setText(LocaleController.getString(R.string.SetPayPasswordTipsAgain));
                            }
                        }, 1000);
                        int unused5 = WalletPaymentPasswordActivity.this.notEmptyTvCount = 0;
                        WalletPaymentPasswordActivity.this.clearText();
                    } else {
                        String unused6 = WalletPaymentPasswordActivity.this.passwordTwo = password.toString();
                        WalletPaymentPasswordActivity.this.resetPassword();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void checkOldPassword(String psd) {
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_PASSWORD_CHECK);
        builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
        builder.addParam("payPassword", AesUtils.encrypt(psd));
        TLRPCWallet.TL_paymentTrans req = builder.build();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, psd) {
            private final /* synthetic */ AlertDialog f$1;
            private final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletPaymentPasswordActivity.this.lambda$checkOldPassword$0$WalletPaymentPasswordActivity(this.f$1, this.f$2, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletPaymentPasswordActivity.this.lambda$checkOldPassword$1$WalletPaymentPasswordActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(progressDialog);
    }

    public /* synthetic */ void lambda$checkOldPassword$0$WalletPaymentPasswordActivity(AlertDialog progressDialog, String psd, TLObject response, TLRPC.TL_error error) {
        final AlertDialog alertDialog = progressDialog;
        final TLRPC.TL_error tL_error = error;
        final TLObject tLObject = response;
        final String str = psd;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                alertDialog.dismiss();
                if (tL_error != null) {
                    WalletPaymentPasswordActivity.this.clearText();
                    int unused = WalletPaymentPasswordActivity.this.notEmptyTvCount = 0;
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.PaymentPasswordChangeFailed));
                    return;
                }
                TLObject tLObject = tLObject;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PaymentPasswordResBean.class);
                    if (parse.isSuccess()) {
                        Bundle args = new Bundle();
                        args.putInt("type", WalletPaymentPasswordActivity.this.type);
                        args.putInt("step", 1);
                        args.putString("password_old", str);
                        WalletPaymentPasswordActivity.this.presentFragment(new WalletPaymentPasswordActivity(args), true);
                        return;
                    }
                    WalletPaymentPasswordActivity.this.clearText();
                    int unused2 = WalletPaymentPasswordActivity.this.notEmptyTvCount = 0;
                    ExceptionUtils.handlePaymentPasswordException(parse.message);
                }
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$checkOldPassword$1$WalletPaymentPasswordActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    /* access modifiers changed from: private */
    public void clearText() {
        for (TextView textView : this.mTvPasswords) {
            textView.setText("");
        }
    }

    private void initKeyboard() {
        GridLayoutManager layoutManager = new GridLayoutManager(getParentActivity(), 3);
        ListAdapter adapter = new ListAdapter(getParentActivity());
        this.keyboardList.setLayoutManager(layoutManager);
        this.keyboardList.setAdapter(adapter);
        this.keyboardList.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position < 9 || position == 10) {
                    if (WalletPaymentPasswordActivity.this.notEmptyTvCount != WalletPaymentPasswordActivity.this.mTvPasswords.length) {
                        for (TextView textView : WalletPaymentPasswordActivity.this.mTvPasswords) {
                            if (TextUtils.isEmpty(textView.getText())) {
                                textView.setText(String.valueOf(WalletPaymentPasswordActivity.this.mNumbers.get(position)));
                                WalletPaymentPasswordActivity.access$608(WalletPaymentPasswordActivity.this);
                                return;
                            }
                        }
                    }
                } else if (position == 11) {
                    for (int i = WalletPaymentPasswordActivity.this.mTvPasswords.length - 1; i >= 0; i--) {
                        if (!TextUtils.isEmpty(WalletPaymentPasswordActivity.this.mTvPasswords[i].getText())) {
                            WalletPaymentPasswordActivity.this.mTvPasswords[i].setText((CharSequence) null);
                            WalletPaymentPasswordActivity.access$610(WalletPaymentPasswordActivity.this);
                            return;
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void setPassword() {
        PayPasswordReqBean bean = new PayPasswordReqBean();
        bean.setBusinessKey(Constants.KEY_PASSWORD_SET);
        bean.setUserId(getUserConfig().clientUserId);
        bean.setPayPassWord(AesUtils.encrypt(this.passwordOne.trim()));
        bean.setConfirmPayPassWord(AesUtils.encrypt(this.passwordTwo.trim()));
        bean.setType(0);
        bean.setSafetyCode("1");
        bean.setCode("");
        TLRPCWallet.TL_paymentTrans<PayPasswordReqBean> req = new TLRPCWallet.TL_paymentTrans<>();
        req.requestModel = bean;
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletPaymentPasswordActivity.this.lambda$setPassword$2$WalletPaymentPasswordActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletPaymentPasswordActivity.this.lambda$setPassword$3$WalletPaymentPasswordActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(progressDialog);
    }

    public /* synthetic */ void lambda$setPassword$2$WalletPaymentPasswordActivity(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (error != null) {
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.PaymentPasswordSetupFailed));
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PaymentPasswordResBean.class);
                    if (parse.isSuccess()) {
                        WalletPaymentPasswordActivity.this.finishFragment();
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.PayPasswordSetSuccess));
                        WalletPaymentPasswordActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.paymentPasswordDidSet, new Object[0]);
                        return;
                    }
                    ExceptionUtils.handlePaymentPasswordException(parse.message);
                }
            }
        });
    }

    public /* synthetic */ void lambda$setPassword$3$WalletPaymentPasswordActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    /* access modifiers changed from: private */
    public void modifyPassword() {
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_PASSWORD_MODIFY);
        builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
        builder.addParam("payPassword", AesUtils.encrypt(this.passwordOne.trim()));
        builder.addParam("confirmPayPassWord", AesUtils.encrypt(this.passwordTwo.trim()));
        builder.addParam("oldPayPassWord", AesUtils.encrypt(this.passwordOld.trim()));
        builder.addParam("code", "");
        TLRPCWallet.TL_paymentTrans req = builder.build();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletPaymentPasswordActivity.this.lambda$modifyPassword$4$WalletPaymentPasswordActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletPaymentPasswordActivity.this.lambda$modifyPassword$5$WalletPaymentPasswordActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(progressDialog);
    }

    public /* synthetic */ void lambda$modifyPassword$4$WalletPaymentPasswordActivity(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (error != null) {
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.PaymentPasswordChangeFailed));
                    WalletPaymentPasswordActivity.this.finishFragment();
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PaymentPasswordResBean.class);
                    if (parse.isSuccess()) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.PwdResetSuccessful));
                        WalletPaymentPasswordActivity.this.finishFragment();
                        return;
                    }
                    ExceptionUtils.handlePaymentPasswordException(parse.message);
                }
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$modifyPassword$5$WalletPaymentPasswordActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    /* access modifiers changed from: private */
    public void resetPassword() {
        TLRPCWallet.Builder builder = new TLRPCWallet.Builder();
        builder.setBusinessKey(Constants.KEY_PASSWORD_RESET);
        builder.addParam("userId", Integer.valueOf(getUserConfig().clientUserId));
        builder.addParam("payPassword", AesUtils.encrypt(this.passwordOne.trim()));
        builder.addParam("confirmPayPassWord", AesUtils.encrypt(this.passwordTwo.trim()));
        builder.addParam("safetyCode", "1");
        builder.addParam("code", "");
        TLRPCWallet.TL_paymentTrans req = builder.build();
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                WalletPaymentPasswordActivity.this.lambda$resetPassword$6$WalletPaymentPasswordActivity(this.f$1, tLObject, tL_error);
            }
        })) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                WalletPaymentPasswordActivity.this.lambda$resetPassword$7$WalletPaymentPasswordActivity(this.f$1, dialogInterface);
            }
        });
        showDialog(progressDialog);
    }

    public /* synthetic */ void lambda$resetPassword$6$WalletPaymentPasswordActivity(final AlertDialog progressDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                if (error != null) {
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.ResetPaymentPasswordFailedTryLater));
                    return;
                }
                TLObject tLObject = response;
                if (tLObject instanceof TLRPCWallet.TL_paymentTransResult) {
                    TLApiModel parse = TLJsonResolve.parse((TLObject) ((TLRPCWallet.TL_paymentTransResult) tLObject).data, (Class<?>) PaymentPasswordResBean.class);
                    if (parse.isSuccess()) {
                        ToastUtils.show((CharSequence) LocaleController.getString(R.string.PayPasswordResetSuccess));
                        WalletPaymentPasswordActivity.this.finishFragment();
                        return;
                    }
                    ExceptionUtils.handlePaymentPasswordException(parse.message);
                }
            }
        }, 1000);
    }

    public /* synthetic */ void lambda$resetPassword$7$WalletPaymentPasswordActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            return WalletPaymentPasswordActivity.this.mNumbers.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = LayoutInflater.from(this.mContext).inflate(R.layout.item_payment_password_number, parent, false);
            } else if (viewType == 1) {
                view = new View(this.mContext);
            } else if (viewType != 2) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                TextView tvNumber = (TextView) holder.itemView.findViewById(R.id.btn_number);
                ImageView ivDelete = (ImageView) holder.itemView.findViewById(R.id.iv_delete);
                tvNumber.setText(String.valueOf(WalletPaymentPasswordActivity.this.mNumbers.get(position)));
                if (position == 11) {
                    tvNumber.setVisibility(8);
                    ivDelete.setVisibility(0);
                    return;
                }
                ivDelete.setVisibility(8);
                tvNumber.setVisibility(0);
            }
        }

        public int getItemViewType(int i) {
            if (i == 9) {
                return 1;
            }
            return 0;
        }
    }
}
