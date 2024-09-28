package im.bclpbkiauv.ui.hui.transfer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import com.blankj.utilcode.util.SpanUtils;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.javaBean.hongbao.UnifyBean;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLocation;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserObject;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.SerializedData;
import im.bclpbkiauv.tgnet.TLApiModel;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.ChatActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.BackupImageView;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import im.bclpbkiauv.ui.hui.adapter.KeyboardAdapter;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.hui.transfer.bean.TransferRequest;
import im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.utils.number.NumberUtil;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TransferSendActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public WalletAccountInfo accountInfo;
    @BindView(2131296390)
    BackupImageView bivTransferAvatar;
    @BindView(2131296422)
    Button btnSendTransferView;
    @BindView(2131296591)
    EditText etTransferAmountView;
    @BindView(2131296592)
    EditText etTransferText;
    private LinearLayout llPayPassword;
    private Context mContext;
    private List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    private TextView[] mTvPasswords;
    private int notEmptyTvCount;
    private XAlertDialog progressView;
    private int reqId = -1;
    private TLRPC.User targetUser;
    private TextView tvForgotPassword;
    @BindView(2131297531)
    TextView tvHongbaoUnit;
    @BindView(2131297573)
    TextView tvPromtText;
    @BindView(2131297689)
    TextView tvTransferHintText;
    @BindView(2131297690)
    TextView tvTransferHintView;
    @BindView(2131297691)
    TextView tvTransferName;

    public TransferSendActivity(Bundle args) {
        super(args);
    }

    public void setAccountInfo(WalletAccountInfo accountInfo2) {
        this.accountInfo = accountInfo2;
    }

    public boolean onFragmentCreate() {
        if (this.arguments == null) {
            return true;
        }
        this.targetUser = getMessagesController().getUser(Integer.valueOf(this.arguments.getInt("user_id", 0)));
        return true;
    }

    public void onResume() {
        super.onResume();
        getParentActivity().setRequestedOrientation(1);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getParentActivity().setRequestedOrientation(2);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen && this.accountInfo == null) {
            getAccountInfo();
        }
    }

    public View createView(Context context) {
        this.mContext = context;
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_transfer_send_layout, (ViewGroup) null, false);
        useButterKnife();
        initActionBar();
        initView();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString("Transfer", R.string.Transfer));
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackgroundColor(-1);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TransferSendActivity.this.finishFragment();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateEditLayout(int result, boolean isMaxAmountSingleOnce) {
        if (result < 0) {
            this.tvPromtText.setVisibility(0);
            this.etTransferAmountView.setTextColor(-109240);
            this.tvHongbaoUnit.setTextColor(-109240);
            if (isMaxAmountSingleOnce) {
                TextView textView = this.tvPromtText;
                textView.setText(LocaleController.getString(R.string.SingleTransferAmoutCannotExceeds) + " " + new DecimalFormat("#").format(WalletConfigBean.getInstance().getTransferMaxMoneySingleTime() / 100.0d) + LocaleController.getString(R.string.HotCoin));
                return;
            }
            TextView textView2 = this.tvPromtText;
            textView2.setText(LocaleController.getString(R.string.TransferAmountMin001) + LocaleController.getString(R.string.HotCoin));
            return;
        }
        this.tvPromtText.setVisibility(8);
        this.etTransferAmountView.setTextColor(-16777216);
        this.tvHongbaoUnit.setTextColor(-16777216);
    }

    private void initView() {
        this.tvHongbaoUnit.setText(LocaleController.getString(R.string.UnitCNY));
        if (this.targetUser != null) {
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(16.0f));
            avatarDrawable.setInfo(this.targetUser);
            this.bivTransferAvatar.setRoundRadius(AndroidUtilities.dp(7.0f));
            this.bivTransferAvatar.getImageReceiver().setCurrentAccount(this.currentAccount);
            this.bivTransferAvatar.setImage(ImageLocation.getForUser(this.targetUser, false), "50_50", (Drawable) avatarDrawable, (Object) this.targetUser);
            this.tvTransferName.setText(UserObject.getName(this.targetUser));
        }
        this.btnSendTransferView.setEnabled(false);
        this.btnSendTransferView.setText(LocaleController.getString(R.string.ConfirmedTransfer));
        this.tvTransferHintText.setText(LocaleController.getString(R.string.TransferMessageTip));
        this.etTransferAmountView.setFilters(new InputFilter[]{new EditInputFilter()});
        this.etTransferAmountView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    BigDecimal amount = new BigDecimal(s.toString().trim());
                    int ret = amount.compareTo(new BigDecimal("0.01"));
                    boolean isMaxAmountSingleOnce = false;
                    if (WalletConfigBean.getInstance().getTransferMaxMoneySingleTime() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && amount.compareTo(new BigDecimal(WalletConfigBean.getInstance().getTransferMaxMoneySingleTime()).divide(new BigDecimal(100))) > 0) {
                        isMaxAmountSingleOnce = true;
                        ret = -1;
                    }
                    TransferSendActivity.this.updateEditLayout(ret, isMaxAmountSingleOnce);
                    if (ret >= 0) {
                        TransferSendActivity.this.btnSendTransferView.setEnabled(true);
                    } else {
                        TransferSendActivity.this.btnSendTransferView.setEnabled(false);
                    }
                    TransferSendActivity.this.tvTransferHintView.setVisibility(8);
                    return;
                }
                TransferSendActivity.this.updateEditLayout(0, false);
                TransferSendActivity.this.btnSendTransferView.setEnabled(false);
                TransferSendActivity.this.tvTransferHintView.setVisibility(0);
                TextView textView = TransferSendActivity.this.tvTransferHintView;
                textView.setText(LocaleController.getString("Remain", R.string.friendscircle_publish_remain) + " " + NumberUtil.replacesSientificE(TransferSendActivity.this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
            }

            public void afterTextChanged(Editable s) {
            }
        });
        if (this.accountInfo != null) {
            this.tvTransferHintView.setVisibility(0);
            TextView textView = this.tvTransferHintView;
            textView.setText(LocaleController.getString("Remain", R.string.friendscircle_publish_remain) + " " + NumberUtil.replacesSientificE(this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
        }
        this.etTransferText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        this.etTransferText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    TransferSendActivity.this.tvTransferHintText.setVisibility(8);
                } else {
                    TransferSendActivity.this.tvTransferHintText.setVisibility(0);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.btnSendTransferView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TransferSendActivity.this.lambda$initView$0$TransferSendActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$TransferSendActivity(View v) {
        if (this.accountInfo != null) {
            createPayAlert();
        }
    }

    private void createPayAlert() {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        View sheet = LayoutInflater.from(getParentActivity()).inflate(R.layout.layout_hongbao_pay_pwd, (ViewGroup) null, false);
        builder.setCustomView(sheet);
        ((ImageView) sheet.findViewById(R.id.ivAlertClose)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TransferSendActivity.this.lambda$createPayAlert$1$TransferSendActivity(view);
            }
        });
        TextView tvTitle = (TextView) sheet.findViewById(R.id.tvTitle);
        TLRPC.User user = this.targetUser;
        if (user != null) {
            tvTitle.setText(LocaleController.formatString("TransferToSomeone", R.string.TransferToSomeone, UserObject.getName(user)));
        } else {
            tvTitle.setText(LocaleController.getString("Transfer", R.string.Transfer));
        }
        TextView tvShowMoneyView = (TextView) sheet.findViewById(R.id.tvShowMoneyView);
        tvShowMoneyView.setTextColor(-109240);
        tvShowMoneyView.setText(DataTools.format2Decimals(this.etTransferAmountView.getText().toString().trim()));
        TextView tvPayMode = (TextView) sheet.findViewById(R.id.tvPayMode);
        tvPayMode.setTextColor(-6710887);
        tvPayMode.setText(LocaleController.getString(R.string.HotCoinPay));
        SpanUtils spanTvBalance = SpanUtils.with((TextView) sheet.findViewById(R.id.tvBlance));
        spanTvBalance.setVerticalAlign(2).append(LocaleController.getString(R.string.friendscircle_publish_remain)).setForegroundColor(-6710887).append(" (").setForegroundColor(-6710887);
        spanTvBalance.append(NumberUtil.replacesSientificE(this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
        ((TextView) sheet.findViewById(R.id.tvMoneyUnit)).setText(LocaleController.getString(R.string.UnitCNY));
        spanTvBalance.setForegroundColor(-16777216).append(SQLBuilder.PARENTHESES_RIGHT).setForegroundColor(-6710887).create();
        TextView textView = (TextView) sheet.findViewById(R.id.tvForgotPassword);
        this.tvForgotPassword = textView;
        textView.setText(LocaleController.getString(R.string.PasswordRecovery));
        this.tvForgotPassword.setOnClickListener($$Lambda$TransferSendActivity$rG8_OqEUXPjpI7MYBFtkBEVs63g.INSTANCE);
        this.llPayPassword = (LinearLayout) sheet.findViewById(R.id.ll_pay_password);
        TextView[] textViewArr = new TextView[6];
        this.mTvPasswords = textViewArr;
        textViewArr[0] = (TextView) sheet.findViewById(R.id.tv_password_1);
        this.mTvPasswords[1] = (TextView) sheet.findViewById(R.id.tv_password_2);
        this.mTvPasswords[2] = (TextView) sheet.findViewById(R.id.tv_password_3);
        this.mTvPasswords[3] = (TextView) sheet.findViewById(R.id.tv_password_4);
        this.mTvPasswords[4] = (TextView) sheet.findViewById(R.id.tv_password_5);
        this.mTvPasswords[5] = (TextView) sheet.findViewById(R.id.tv_password_6);
        GridView gvKeyboard = (GridView) sheet.findViewById(R.id.gvKeyboard);
        gvKeyboard.setAdapter(new KeyboardAdapter(this.mNumbers, getParentActivity()));
        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                TransferSendActivity.this.lambda$createPayAlert$3$TransferSendActivity(adapterView, view, i, j);
            }
        });
        showDialog(builder.create()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                TransferSendActivity.this.lambda$createPayAlert$4$TransferSendActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$createPayAlert$1$TransferSendActivity(View v) {
        dismissCurrentDialog();
    }

    static /* synthetic */ void lambda$createPayAlert$2(View v) {
    }

    public /* synthetic */ void lambda$createPayAlert$3$TransferSendActivity(AdapterView parent, View view, int position, long id) {
        if (position < 9 || position == 10) {
            int i = this.notEmptyTvCount;
            TextView[] textViewArr = this.mTvPasswords;
            if (i != textViewArr.length) {
                int length = textViewArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    TextView textView = textViewArr[i2];
                    if (TextUtils.isEmpty(textView.getText())) {
                        textView.setText(String.valueOf(this.mNumbers.get(position)));
                        this.notEmptyTvCount++;
                        break;
                    }
                    i2++;
                }
                if (this.notEmptyTvCount == this.mTvPasswords.length) {
                    StringBuilder password = new StringBuilder();
                    for (TextView textView2 : this.mTvPasswords) {
                        String text = textView2.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            password.append(text);
                        }
                    }
                    sendTransfer(UserObject.getName(this.targetUser), String.valueOf(new BigDecimal(this.etTransferAmountView.getText().toString().trim()).multiply(new BigDecimal("100")).intValue()), AesUtils.encrypt(password.toString().trim()));
                }
            }
        } else if (position == 11) {
            for (int i3 = this.mTvPasswords.length - 1; i3 >= 0; i3--) {
                if (!TextUtils.isEmpty(this.mTvPasswords[i3].getText())) {
                    this.mTvPasswords[i3].setText((CharSequence) null);
                    this.notEmptyTvCount--;
                    return;
                }
            }
        }
    }

    public /* synthetic */ void lambda$createPayAlert$4$TransferSendActivity(DialogInterface dialog1) {
        this.notEmptyTvCount = 0;
    }

    private void sendTransfer(String dest, String total, String pwd) {
        TLRPCRedpacket.CL_messages_sendRptTransfer req = new TLRPCRedpacket.CL_messages_sendRptTransfer();
        req.flags = 2;
        req.trans = 1;
        req.peer = getMessagesController().getInputPeer(this.targetUser.id);
        TransferRequest bean = new TransferRequest();
        bean.setOutTradeNo(StringUtils.getTradeNo(getUserConfig().clientUserId, getConnectionsManager().getCurrentTime()));
        bean.setNonceStr(StringUtils.getNonceStr(getConnectionsManager().getCurrentTime()));
        bean.setBody(LocaleController.formatString("TransferCreateToSomeone", R.string.TransferCreateToSomeone, dest));
        String str = "";
        bean.setDetail(str);
        bean.setAttach(str);
        bean.setTotalFee(total);
        bean.setTradeType("0");
        if (!TextUtils.isEmpty(this.etTransferText.getText())) {
            str = this.etTransferText.getText().toString().trim();
        }
        bean.setRemarks(str);
        bean.setInitiator(String.valueOf(getUserConfig().getClientUserId()));
        bean.setPayPassWord(pwd);
        bean.setBusinessKey(UnifyBean.BUSINESS_KEY_TRANSFER);
        bean.setVersion("1");
        bean.setRecipient(String.valueOf(this.targetUser.id));
        TLRPC.TL_dataJSON data = new TLRPC.TL_dataJSON();
        data.data = new Gson().toJson((Object) bean);
        req.redPkg = data;
        req.random_id = getSendMessagesHelper().getNextRandomId();
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        ConnectionsManager connectionsManager = getConnectionsManager();
        int sendRequest = getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TransferSendActivity.this.lambda$sendTransfer$20$TransferSendActivity(tLObject, tL_error);
            }
        });
        this.reqId = sendRequest;
        connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                TransferSendActivity.this.lambda$sendTransfer$21$TransferSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
            this.progressView.setLoadingText(LocaleController.getString(R.string.Sending));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$sendTransfer$20$TransferSendActivity(TLObject response, TLRPC.TL_error error) {
        this.progressView.dismiss();
        if (error != null) {
            if (error.text == null) {
                error.text = "";
            }
            error.text = error.text.replace("\n", "");
            if (error.text.contains("ACCOUNT_PASSWORD_IN_MINUTES,ERROR_TIMES,WILL_BE_FROZEN")) {
                String[] split = error.text.split("_");
                String str = split[split.length - 2];
                AndroidUtilities.runOnUIThread(new Runnable(split[split.length - 1]) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TransferSendActivity.this.lambda$null$5$TransferSendActivity(this.f$1);
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TransferSendActivity.this.lambda$null$6$TransferSendActivity();
                    }
                }, 2500);
            } else if ("NOT_SUFFICIENT_FUNDS".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TransferSendActivity.this.lambda$null$10$TransferSendActivity();
                    }
                });
            } else if ("ACCOUNT_HAS_BEEN_FROZEN_CODE".equals(error.text) || "PAY_PASSWORD_MAX_ACCOUNT_FROZEN".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TransferSendActivity.this.lambda$null$12$TransferSendActivity(this.f$1);
                    }
                });
            } else if ("MUTUALCONTACTNEED".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TransferSendActivity.this.lambda$null$15$TransferSendActivity();
                    }
                });
            } else if (error.text.contains("EXCEED_TRANSFER_ONCE_MAX_MONEY")) {
                dismissCurrentDialog();
                WalletDialogUtil.showSingleBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), true, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferSendActivity.this.lambda$null$17$TransferSendActivity(dialogInterface, i);
                    }
                }, (DialogInterface.OnDismissListener) null);
            } else {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TransferSendActivity.this.lambda$null$18$TransferSendActivity(this.f$1);
                    }
                });
            }
        } else if (response instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            getMessagesController().processUpdates(updates, false);
            if (updates.updates != null) {
                Iterator<TLRPC.Update> it = updates.updates.iterator();
                while (it.hasNext()) {
                    TLRPC.Update u = it.next();
                    if (u instanceof TLRPCRedpacket.CL_updateRpkTransfer) {
                        TLApiModel<TransferResponse> parseResponse = TLJsonResolve.parse((TLObject) ((TLRPCRedpacket.CL_updateRpkTransfer) u).data, (Class<?>) TransferResponse.class);
                        if (!parseResponse.isSuccess()) {
                            WalletErrorUtil.parseErrorToast(LocaleController.getString("TransferFailure", R.string.TransferFailure), parseResponse.message);
                            return;
                        } else {
                            AndroidUtilities.runOnUIThread(new Runnable(parseResponse) {
                                private final /* synthetic */ TLApiModel f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    TransferSendActivity.this.lambda$null$19$TransferSendActivity(this.f$1);
                                }
                            });
                            return;
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$5$TransferSendActivity(String time) {
        this.tvForgotPassword.setText(LocaleController.formatString("PassswordErrorText", R.string.PassswordErrorText, time));
        this.tvForgotPassword.setTextColor(-2737326);
        this.tvForgotPassword.setEnabled(false);
        this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_error_bg);
        this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_error_divider));
        for (TextView mTvPassword : this.mTvPasswords) {
            mTvPassword.setTextColor(-2737326);
        }
    }

    public /* synthetic */ void lambda$null$6$TransferSendActivity() {
        this.tvForgotPassword.setText(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
        this.tvForgotPassword.setTextColor(-14250753);
        this.tvForgotPassword.setEnabled(true);
        this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_bg);
        this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_divider));
        for (TextView mTvPassword : this.mTvPasswords) {
            mTvPassword.setText((CharSequence) null);
            mTvPassword.setTextColor(-16777216);
            this.notEmptyTvCount = 0;
        }
    }

    public /* synthetic */ void lambda$null$10$TransferSendActivity() {
        dismissCurrentDialog();
        WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BalanceIsNotEnough", R.string.BalanceIsNotEnough), LocaleController.getString(R.string.ChangeHotCoinCount), LocaleController.getString(R.string.ChargeAmount), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TransferSendActivity.this.lambda$null$8$TransferSendActivity(dialogInterface, i);
            }
        }, $$Lambda$TransferSendActivity$xbVy4z7MCZLiVTpjrD8S7JO6dlI.INSTANCE, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$8$TransferSendActivity(DialogInterface dialogInterface, int i) {
        this.etTransferAmountView.requestFocus();
        EditText editText = this.etTransferAmountView;
        editText.setSelection(editText.getText().length());
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferSendActivity.this.lambda$null$7$TransferSendActivity();
            }
        }, 300);
    }

    public /* synthetic */ void lambda$null$7$TransferSendActivity() {
        AndroidUtilities.showKeyboard(this.etTransferAmountView);
    }

    static /* synthetic */ void lambda$null$9(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$12$TransferSendActivity(TLRPC.TL_error error) {
        dismissCurrentDialog();
        WalletDialogUtil.showWalletDialog(this, "", WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), LocaleController.getString("ContactCustomerService", R.string.ContactCustomerService), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TransferSendActivity.this.lambda$null$11$TransferSendActivity(dialogInterface, i);
            }
        }, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$11$TransferSendActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new AboutAppActivity());
    }

    public /* synthetic */ void lambda$null$15$TransferSendActivity() {
        dismissCurrentDialog();
        WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("AddContactsTip", R.string.AddContactsTip), LocaleController.getString("GoVerify", R.string.GoVerify), LocaleController.getString("Confirm", R.string.Confirm), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                TransferSendActivity.this.lambda$null$14$TransferSendActivity(dialogInterface, i);
            }
        }, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$13$TransferSendActivity() {
        presentFragment(new AddContactsInfoActivity((Bundle) null, this.targetUser));
    }

    public /* synthetic */ void lambda$null$14$TransferSendActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferSendActivity.this.lambda$null$13$TransferSendActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$17$TransferSendActivity(DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferSendActivity.this.lambda$null$16$TransferSendActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$16$TransferSendActivity() {
        this.etTransferAmountView.setText("");
    }

    public /* synthetic */ void lambda$null$18$TransferSendActivity(TLRPC.TL_error error) {
        dismissCurrentDialog();
        WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text));
    }

    public /* synthetic */ void lambda$null$19$TransferSendActivity(TLApiModel parseResponse) {
        dismissCurrentDialog();
        TransferStatusActivity transferStatusActivity = new TransferStatusActivity();
        transferStatusActivity.setTargetUser(this.targetUser);
        transferStatusActivity.setTransResponse((TransferResponse) parseResponse.model, true);
        transferStatusActivity.setSender(true);
        presentFragment(transferStatusActivity, true);
    }

    public /* synthetic */ void lambda$sendTransfer$21$TransferSendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.reqId, true);
    }

    private void getAccountInfo() {
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        TLRPCWallet.TL_getPaymentAccountInfo req = new TLRPCWallet.TL_getPaymentAccountInfo();
        ConnectionsManager connectionsManager = getConnectionsManager();
        int sendRequest = getConnectionsManager().sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TransferSendActivity.this.lambda$getAccountInfo$38$TransferSendActivity(tLObject, tL_error);
            }
        });
        this.reqId = sendRequest;
        connectionsManager.bindRequestToGuid(sendRequest, this.classGuid);
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                TransferSendActivity.this.lambda$getAccountInfo$39$TransferSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$getAccountInfo$38$TransferSendActivity(TLObject response, TLRPC.TL_error error) {
        String str;
        if (error != null) {
            this.progressView.dismiss();
            if (BuildVars.RELEASE_VERSION) {
                str = LocaleController.getString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater);
            } else {
                str = WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), error.text);
            }
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) str, true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    TransferSendActivity.this.lambda$null$22$TransferSendActivity(dialogInterface);
                }
            });
            return;
        }
        this.progressView.dismiss();
        if (response instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("AccountInfoNotCompleted", R.string.AccountInfoNotCompleted, "转账", "转账"), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToWalletCenter", R.string.GoToWalletCenter), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TransferSendActivity.this.lambda$null$23$TransferSendActivity(dialogInterface, i);
                }
            }, $$Lambda$TransferSendActivity$VCfz6DtCe5Zqaz_SO4ES5wNwkF8.INSTANCE, new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    TransferSendActivity.this.lambda$null$26$TransferSendActivity(dialogInterface);
                }
            });
            return;
        }
        TLApiModel<WalletAccountInfo> model = TLJsonResolve.parse(response, (Class<?>) WalletAccountInfo.class);
        if (model.isSuccess()) {
            WalletAccountInfo walletAccountInfo = (WalletAccountInfo) model.model;
            this.accountInfo = walletAccountInfo;
            WalletConfigBean.setWalletAccountInfo(walletAccountInfo);
            WalletConfigBean.setConfigValue(((WalletAccountInfo) model.model).getRiskList());
            if (!this.accountInfo.hasNormalAuth()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BasicAuthNor", R.string.BasicAuthNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToAuth", R.string.GoToAuth), (DialogInterface.OnClickListener) null, $$Lambda$TransferSendActivity$obaUV41J1Xm78M7OhOEQq_HzDcc.INSTANCE, (DialogInterface.OnDismissListener) null);
            } else if (!this.accountInfo.hasBindBank()) {
                WalletDialogUtil.showWalletDialog(this, (String) null, LocaleController.getString("BankBindNor", R.string.BankBindNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferSendActivity.this.lambda$null$28$TransferSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$TransferSendActivity$GFtCgAGvAOIApAQ1TCH0RU5scGM.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        TransferSendActivity.this.lambda$null$31$TransferSendActivity(dialogInterface);
                    }
                });
            } else if (!this.accountInfo.hasPaypassword()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("PayPasswordNor", R.string.PayPasswordNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("redpacket_goto_set", R.string.redpacket_goto_set), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TransferSendActivity.this.lambda$null$32$TransferSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$TransferSendActivity$7oY2SYMt7jfswKMI9Yn6OnCmVI.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        TransferSendActivity.this.lambda$null$35$TransferSendActivity(dialogInterface);
                    }
                });
            } else {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        TransferSendActivity.this.lambda$null$36$TransferSendActivity();
                    }
                });
            }
        } else {
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), model.message), true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    TransferSendActivity.this.lambda$null$37$TransferSendActivity(dialogInterface);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$22$TransferSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$23$TransferSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$24() {
    }

    public /* synthetic */ void lambda$null$26$TransferSendActivity(DialogInterface dialog) {
        finish();
    }

    static /* synthetic */ void lambda$null$27(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$28$TransferSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$29() {
    }

    public /* synthetic */ void lambda$null$31$TransferSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$32$TransferSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$33() {
    }

    public /* synthetic */ void lambda$null$35$TransferSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$36$TransferSendActivity() {
        this.tvTransferHintView.setVisibility(0);
        TextView textView = this.tvTransferHintView;
        textView.setText(LocaleController.getString(R.string.friendscircle_publish_remain) + NumberUtil.replacesSientificE(this.accountInfo.getCashAmount() / 100.0d, "#0.00"));
        this.etTransferAmountView.setEnabled(true);
    }

    public /* synthetic */ void lambda$null$37$TransferSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$getAccountInfo$39$TransferSendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.reqId, true);
        finish();
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
            XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
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
                    TransferSendActivity.lambda$performService$42(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performService$42(SharedPreferences preferences, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
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
                    TransferSendActivity.lambda$null$40(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TransferSendActivity.lambda$null$41(XAlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$40(SharedPreferences preferences, TLRPC.TL_help_support res, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
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

    static /* synthetic */ void lambda$null$41(XAlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void finish() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                TransferSendActivity.this.finishFragment();
            }
        });
    }
}
