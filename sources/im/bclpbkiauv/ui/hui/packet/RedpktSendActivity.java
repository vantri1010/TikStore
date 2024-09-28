package im.bclpbkiauv.ui.hui.packet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.utils.DataTools;
import im.bclpbkiauv.messenger.utils.status.StatusBarUtils;
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
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import im.bclpbkiauv.ui.hui.contacts.AddContactsInfoActivity;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.hui.packet.bean.RepacketRequest;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletAccountInfo;
import im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletDialogUtil;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;
import im.bclpbkiauv.ui.hviews.MryTextView;
import im.bclpbkiauv.ui.utils.AesUtils;
import im.bclpbkiauv.ui.utils.number.StringUtils;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RedpktSendActivity extends BaseFragment {
    private WalletAccountInfo accountInfo;
    @BindView(2131296433)
    Button btnRedPacketSend;
    @BindView(2131296603)
    EditText etRedPacketAmount;
    @BindView(2131296604)
    EditText etRedPacketGreet;
    @BindView(2131296627)
    FrameLayout flAmountShowLayout;
    private boolean inited;
    @BindView(2131296926)
    LinearLayout llAmountLayout;
    private LinearLayout llPayPassword;
    @BindView(2131296952)
    LinearLayout llRemarkLayout;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public List<Integer> mNumbers = new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, -10, 0, -11}));
    private TextView[] mTvPasswords;
    private int notEmptyTvCount;
    private XAlertDialog progressView;
    private int reqId;
    private TLRPC.User targetUser;
    @BindView(2131297718)
    MryTextView tvAmountShowUnit;
    @BindView(2131297719)
    TextView tvAmountShowView;
    private TextView tvForgotPassword;
    @BindView(2131297825)
    TextView tvRedPacketAmountHint;
    @BindView(2131297826)
    TextView tvRedPacketGreetHint;
    @BindView(2131297828)
    MryTextView tvRedPacketUnit;
    @BindView(2131297837)
    MryTextView tvRpkPromet;
    @BindView(2131297859)
    TextView tvTimeOutDesc;
    private int userId;

    public RedpktSendActivity(Bundle args) {
        super(args);
    }

    public void setAccountInfo(WalletAccountInfo accountInfo2) {
        this.accountInfo = accountInfo2;
    }

    public boolean onFragmentCreate() {
        if (this.arguments != null) {
            this.userId = this.arguments.getInt("user_id", 0);
        }
        if (this.userId <= 0) {
            return true;
        }
        this.targetUser = getMessagesController().getUser(Integer.valueOf(this.userId));
        return true;
    }

    public void onResume() {
        super.onResume();
        getParentActivity().setRequestedOrientation(1);
    }

    public void onFragmentDestroy() {
        if (Theme.getCurrentTheme().isDark()) {
            StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), false);
        } else {
            StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), true);
        }
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
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_hongbao_send_layout, (ViewGroup) null, false);
        this.fragmentView.setBackgroundColor(-328966);
        useButterKnife();
        initActionBar();
        initView();
        StatusBarUtils.setStatusBarDarkTheme(getParentActivity(), false);
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setTitle(LocaleController.getString(R.string.redpacket_send));
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(-1, false);
        this.actionBar.setItemsBackgroundColor(-1, true);
        this.actionBar.setItemsColor(-1, false);
        this.actionBar.setItemsColor(-1, true);
        this.actionBar.setBackground(this.gameActionBarBackgroundDrawable);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktSendActivity.this.lambda$initActionBar$0$RedpktSendActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$initActionBar$0$RedpktSendActivity(View v) {
        finishFragment();
    }

    private void initView() {
        this.btnRedPacketSend.setEnabled(false);
        this.btnRedPacketSend.setText(LocaleController.getString(R.string.SendHotCoin));
        this.tvRpkPromet.setVisibility(8);
        this.tvRedPacketGreetHint.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
        this.tvTimeOutDesc.setText(LocaleController.getString(R.string.RedPacketsWillReturnWhenNotReceivedIn24Hours));
        this.tvRedPacketAmountHint.setText(LocaleController.getString(R.string.redpacket_amount));
        this.etRedPacketAmount.setFilters(new InputFilter[]{new EditInputFilter()});
        this.etRedPacketAmount.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                RedpktSendActivity.this.tvAmountShowView.setText(DataTools.format2Decimals(s.toString()));
                if (s.length() > 0) {
                    RedpktSendActivity.this.tvRedPacketAmountHint.setText("");
                    BigDecimal amount = new BigDecimal(s.toString().trim());
                    int ret = amount.compareTo(new BigDecimal("0.01"));
                    boolean isMaxAmountSingleOnce = false;
                    if (WalletConfigBean.getInstance().getRedPacketMaxMoneySingleTime() != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE && amount.compareTo(new BigDecimal(WalletConfigBean.getInstance().getRedPacketMaxMoneySingleTime()).divide(new BigDecimal(100))) > 0) {
                        isMaxAmountSingleOnce = true;
                        ret = -1;
                    }
                    RedpktSendActivity.this.updateEditLayout(ret, isMaxAmountSingleOnce);
                    if (ret >= 0) {
                        RedpktSendActivity.this.btnRedPacketSend.setEnabled(true);
                    } else {
                        RedpktSendActivity.this.btnRedPacketSend.setEnabled(false);
                    }
                } else {
                    RedpktSendActivity.this.updateEditLayout(0, false);
                    RedpktSendActivity.this.tvRedPacketAmountHint.setText(LocaleController.getString(R.string.redpacket_amount));
                    RedpktSendActivity.this.btnRedPacketSend.setEnabled(false);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.etRedPacketGreet.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    RedpktSendActivity.this.tvRedPacketGreetHint.setText("");
                } else {
                    RedpktSendActivity.this.tvRedPacketGreetHint.setText(LocaleController.getString(R.string.redpacket_greetings_tip));
                }
            }

            public void afterTextChanged(Editable s) {
            }
        });
        this.btnRedPacketSend.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktSendActivity.this.lambda$initView$1$RedpktSendActivity(view);
            }
        });
        this.tvRedPacketUnit.setText(LocaleController.getString(R.string.UnitCNY));
        this.tvAmountShowUnit.setText(LocaleController.getString(R.string.UnitCNY));
    }

    public /* synthetic */ void lambda$initView$1$RedpktSendActivity(View v) {
        createPayAlert(this.accountInfo.getCashAmount() / 100.0d);
    }

    /* access modifiers changed from: private */
    public void updateEditLayout(int ret, boolean isMaxAmountSingleOnce) {
        if (ret < 0) {
            this.tvRpkPromet.setVisibility(0);
            this.etRedPacketAmount.setTextColor(-109240);
            this.tvRedPacketUnit.setTextColor(-109240);
            if (isMaxAmountSingleOnce) {
                MryTextView mryTextView = this.tvRpkPromet;
                mryTextView.setText(LocaleController.getString(R.string.SingleRedPacketAmoutCannotExceeds) + " " + new DecimalFormat("#").format(WalletConfigBean.getInstance().getRedPacketMaxMoneySingleTime() / 100.0d) + LocaleController.getString(R.string.HotCoin));
                return;
            }
            MryTextView mryTextView2 = this.tvRpkPromet;
            mryTextView2.setText(LocaleController.getString(R.string.redpacket_single_limit_personal3) + LocaleController.getString(R.string.HotCoin));
            return;
        }
        this.tvRpkPromet.setVisibility(8);
        this.etRedPacketAmount.setTextColor(-16777216);
        this.tvRedPacketUnit.setTextColor(-16777216);
        this.tvRedPacketGreetHint.setTextColor(-3355444);
    }

    private void createPayAlert(double balance) {
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        builder.setApplyTopPadding(false);
        builder.setApplyBottomPadding(false);
        View sheet = LayoutInflater.from(getParentActivity()).inflate(R.layout.layout_hongbao_pay_pwd, (ViewGroup) null, false);
        builder.setCustomView(sheet);
        ((ImageView) sheet.findViewById(R.id.ivAlertClose)).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                RedpktSendActivity.this.lambda$createPayAlert$2$RedpktSendActivity(view);
            }
        });
        ((TextView) sheet.findViewById(R.id.tvTitle)).setText(LocaleController.getString(R.string.CgCoinRedpacket));
        TextView tvShowMoneyView = (TextView) sheet.findViewById(R.id.tvShowMoneyView);
        tvShowMoneyView.setTextColor(-109240);
        tvShowMoneyView.setText(DataTools.format2Decimals(this.etRedPacketAmount.getText().toString().trim()));
        TextView tvPayMode = (TextView) sheet.findViewById(R.id.tvPayMode);
        tvPayMode.setTextColor(-6710887);
        tvPayMode.setText(LocaleController.getString(R.string.HotCoinPay));
        SpanUtils spanTvBalance = SpanUtils.with((TextView) sheet.findViewById(R.id.tvBlance));
        spanTvBalance.setVerticalAlign(2).append(LocaleController.getString(R.string.friendscircle_publish_remain)).setForegroundColor(-6710887).append(" (").setForegroundColor(-6710887);
        spanTvBalance.append(String.format("%.2f", new Object[]{Double.valueOf(balance)}));
        ((TextView) sheet.findViewById(R.id.tvMoneyUnit)).setText(LocaleController.getString(R.string.UnitCNY));
        spanTvBalance.setForegroundColor(-16777216).append(SQLBuilder.PARENTHESES_RIGHT).setForegroundColor(-6710887).create();
        TextView textView = (TextView) sheet.findViewById(R.id.tvForgotPassword);
        this.tvForgotPassword = textView;
        textView.setText(LocaleController.getString(R.string.PasswordRecovery));
        this.tvForgotPassword.setOnClickListener($$Lambda$RedpktSendActivity$e7rE4cc_N_R5TuF7LA55tfUsw.INSTANCE);
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
        gvKeyboard.setAdapter(new MyAdapter());
        gvKeyboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                RedpktSendActivity.this.lambda$createPayAlert$4$RedpktSendActivity(adapterView, view, i, j);
            }
        });
        showDialog(builder.create()).setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                RedpktSendActivity.this.lambda$createPayAlert$5$RedpktSendActivity(dialogInterface);
            }
        });
    }

    public /* synthetic */ void lambda$createPayAlert$2$RedpktSendActivity(View v) {
        dismissCurrentDialog();
    }

    static /* synthetic */ void lambda$createPayAlert$3(View v) {
    }

    public /* synthetic */ void lambda$createPayAlert$4$RedpktSendActivity(AdapterView parent, View view, int position, long id) {
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
                    sendRedpacket(this.targetUser, String.valueOf(new BigDecimal(this.etRedPacketAmount.getText().toString().trim()).multiply(new BigDecimal("100")).intValue()), AesUtils.encrypt(password.toString().trim()));
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

    public /* synthetic */ void lambda$createPayAlert$5$RedpktSendActivity(DialogInterface dialog1) {
        this.notEmptyTvCount = 0;
    }

    private void sendRedpacket(TLRPC.User targetUser2, String total, String pwd) {
        TLRPCRedpacket.CL_messages_sendRptTransfer req = new TLRPCRedpacket.CL_messages_sendRptTransfer();
        req.flags = 3;
        req.trans = 0;
        req.type = 0;
        req.peer = getMessagesController().getInputPeer(targetUser2.id);
        RepacketRequest bean = new RepacketRequest();
        bean.setOutTradeNo("android_" + getUserConfig().clientUserId + StringUtils.getRandomString(16) + getConnectionsManager().getCurrentTime());
        bean.setNonceStr(StringUtils.getRandomString(32));
        bean.setBody(String.format("给%s发红包", new Object[]{targetUser2.first_name}));
        String str = "";
        bean.setDetail(str);
        bean.setAttach(str);
        bean.setTotalFee(total);
        bean.setTradeType("1");
        if (!TextUtils.isEmpty(this.etRedPacketGreet.getText())) {
            str = this.etRedPacketGreet.getText().toString().trim();
        }
        bean.setRemarks(str);
        bean.setInitiator(String.valueOf(getUserConfig().getClientUserId()));
        bean.setPayPassWord(pwd);
        bean.setBusinessKey(UnifyBean.BUSINESS_KEY_REDPACKET);
        bean.setRecipient(String.valueOf(targetUser2.id));
        bean.setVersion("1");
        bean.setRedType("0");
        bean.setGrantType("0");
        bean.setNumber(1);
        TLRPC.TL_dataJSON data = new TLRPC.TL_dataJSON();
        data.data = new Gson().toJson((Object) bean);
        req.redPkg = data;
        req.random_id = getSendMessagesHelper().getNextRandomId();
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        this.reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(targetUser2) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RedpktSendActivity.this.lambda$sendRedpacket$19$RedpktSendActivity(this.f$1, tLObject, tL_error);
            }
        });
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                RedpktSendActivity.this.lambda$sendRedpacket$20$RedpktSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
            this.progressView.setLoadingText(LocaleController.getString(R.string.Sending));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$sendRedpacket$19$RedpktSendActivity(TLRPC.User targetUser2, TLObject response, TLRPC.TL_error error) {
        this.progressView.dismiss();
        AndroidUtilities.runOnUIThread(new Runnable(error, targetUser2, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLRPC.User f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                RedpktSendActivity.this.lambda$null$18$RedpktSendActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$18$RedpktSendActivity(TLRPC.TL_error error, TLRPC.User targetUser2, TLObject response) {
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
                        RedpktSendActivity.this.lambda$null$6$RedpktSendActivity(this.f$1);
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        RedpktSendActivity.this.lambda$null$7$RedpktSendActivity();
                    }
                }, 2500);
            } else if ("NOT_SUFFICIENT_FUNDS".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        RedpktSendActivity.this.lambda$null$11$RedpktSendActivity();
                    }
                });
            } else if ("ACCOUNT_HAS_BEEN_FROZEN_CODE".equals(error.text) || "PAY_PASSWORD_MAX_ACCOUNT_FROZEN".equals(error.text)) {
                dismissCurrentDialog();
                WalletDialogUtil.showWalletDialog(this, "", WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), LocaleController.getString("ContactCustomerService", R.string.ContactCustomerService), (DialogInterface.OnClickListener) null, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktSendActivity.this.lambda$null$12$RedpktSendActivity(dialogInterface, i);
                    }
                }, (DialogInterface.OnDismissListener) null);
            } else if ("MUTUALCONTACTNEED".equals(error.text)) {
                AndroidUtilities.runOnUIThread(new Runnable(targetUser2) {
                    private final /* synthetic */ TLRPC.User f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RedpktSendActivity.this.lambda$null$15$RedpktSendActivity(this.f$1);
                    }
                });
            } else if (error.text.contains("EXCEED_RED_PACKET_ONCE_MAX_MONEY")) {
                dismissCurrentDialog();
                WalletDialogUtil.showSingleBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text), LocaleController.getString("Confirm", R.string.Confirm), false, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktSendActivity.this.lambda$null$16$RedpktSendActivity(dialogInterface, i);
                    }
                }, (DialogInterface.OnDismissListener) null);
            } else {
                AndroidUtilities.runOnUIThread(new Runnable(error) {
                    private final /* synthetic */ TLRPC.TL_error f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        RedpktSendActivity.this.lambda$null$17$RedpktSendActivity(this.f$1);
                    }
                });
            }
        } else if (response instanceof TLRPC.Updates) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$6$RedpktSendActivity(String time) {
        this.tvForgotPassword.setText(LocaleController.formatString("PassswordErrorText", R.string.PassswordErrorText, time));
        this.tvForgotPassword.setTextColor(-109240);
        this.tvForgotPassword.setEnabled(false);
        this.llPayPassword.setBackgroundResource(R.drawable.shape_pay_password_error_bg);
        this.llPayPassword.setDividerDrawable(getParentActivity().getResources().getDrawable(R.drawable.shape_pay_password_error_divider));
        for (TextView mTvPassword : this.mTvPasswords) {
            mTvPassword.setTextColor(-109240);
        }
    }

    public /* synthetic */ void lambda$null$7$RedpktSendActivity() {
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

    public /* synthetic */ void lambda$null$11$RedpktSendActivity() {
        dismissCurrentDialog();
        WalletDialog showWalletDialog = WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BalanceIsNotEnough", R.string.BalanceIsNotEnough), LocaleController.getString(R.string.ChangeHotCoinCount), LocaleController.getString(R.string.ChargeAmount), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                RedpktSendActivity.this.lambda$null$9$RedpktSendActivity(dialogInterface, i);
            }
        }, $$Lambda$RedpktSendActivity$MhDqdc5hYlnhLRqVQNWsIES8_A.INSTANCE, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$9$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        this.etRedPacketAmount.requestFocus();
        EditText editText = this.etRedPacketAmount;
        editText.setSelection(editText.getText().length());
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                RedpktSendActivity.this.lambda$null$8$RedpktSendActivity();
            }
        }, 300);
    }

    public /* synthetic */ void lambda$null$8$RedpktSendActivity() {
        AndroidUtilities.showKeyboard(this.etRedPacketAmount);
    }

    static /* synthetic */ void lambda$null$10(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$12$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new AboutAppActivity());
    }

    public /* synthetic */ void lambda$null$15$RedpktSendActivity(TLRPC.User targetUser2) {
        dismissCurrentDialog();
        WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("AddContactsTip", R.string.AddContactsTip), LocaleController.getString("GoVerify", R.string.GoVerify), LocaleController.getString("Confirm", R.string.Confirm), new DialogInterface.OnClickListener(targetUser2) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                RedpktSendActivity.this.lambda$null$14$RedpktSendActivity(this.f$1, dialogInterface, i);
            }
        }, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public /* synthetic */ void lambda$null$13$RedpktSendActivity(TLRPC.User targetUser2) {
        presentFragment(new AddContactsInfoActivity((Bundle) null, targetUser2));
    }

    public /* synthetic */ void lambda$null$14$RedpktSendActivity(TLRPC.User targetUser2, DialogInterface dialogInterface, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(targetUser2) {
            private final /* synthetic */ TLRPC.User f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                RedpktSendActivity.this.lambda$null$13$RedpktSendActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$16$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        this.etRedPacketAmount.setText("");
    }

    public /* synthetic */ void lambda$null$17$RedpktSendActivity(TLRPC.TL_error error) {
        dismissCurrentDialog();
        WalletDialogUtil.showConfirmBtnWalletDialog(this, WalletErrorUtil.getErrorDescription(error.text));
    }

    public /* synthetic */ void lambda$sendRedpacket$20$RedpktSendActivity(DialogInterface dialog) {
        getConnectionsManager().cancelRequest(this.reqId, true);
    }

    private void getAccountInfo() {
        this.progressView = new XAlertDialog(getParentActivity(), 5);
        this.reqId = getConnectionsManager().sendRequest(new TLRPCWallet.TL_getPaymentAccountInfo(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RedpktSendActivity.this.lambda$getAccountInfo$36$RedpktSendActivity(tLObject, tL_error);
            }
        });
        this.progressView.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public final void onCancel(DialogInterface dialogInterface) {
                RedpktSendActivity.this.lambda$getAccountInfo$37$RedpktSendActivity(dialogInterface);
            }
        });
        try {
            this.progressView.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public /* synthetic */ void lambda$getAccountInfo$36$RedpktSendActivity(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            this.progressView.dismiss();
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), error.text), true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktSendActivity.this.lambda$null$21$RedpktSendActivity(dialogInterface);
                }
            });
            return;
        }
        this.progressView.dismiss();
        if (response instanceof TLRPCWallet.TL_paymentAccountInfoNotExist) {
            WalletDialogUtil.showWalletDialog(this, "", LocaleController.formatString("AccountInfoNotCompleted", R.string.AccountInfoNotCompleted, LocaleController.getString(R.string.SendRedPackets), LocaleController.getString(R.string.SendRedPackets)), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToWalletCenter", R.string.GoToWalletCenter), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    RedpktSendActivity.this.lambda$null$22$RedpktSendActivity(dialogInterface, i);
                }
            }, $$Lambda$RedpktSendActivity$03m30msYYfXVas1pFgokxc8JPQ.INSTANCE, new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktSendActivity.this.lambda$null$25$RedpktSendActivity(dialogInterface);
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
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BasicAuthNor", R.string.BasicAuthNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToAuth", R.string.GoToAuth), (DialogInterface.OnClickListener) null, $$Lambda$RedpktSendActivity$jFVq3AReKydDzxccbw2Ngc1UvU.INSTANCE, (DialogInterface.OnDismissListener) null);
            } else if (!this.accountInfo.hasBindBank()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("BankBindNor", R.string.BankBindNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("GoToBind", R.string.GoToBind), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktSendActivity.this.lambda$null$27$RedpktSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$RedpktSendActivity$ttRsBavRDUeIPVbgjN_6uIiM7oM.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        RedpktSendActivity.this.lambda$null$30$RedpktSendActivity(dialogInterface);
                    }
                });
            } else if (!this.accountInfo.hasPaypassword()) {
                WalletDialogUtil.showWalletDialog(this, "", LocaleController.getString("PayPasswordNor", R.string.PayPasswordNor), LocaleController.getString("Close", R.string.Close), LocaleController.getString("redpacket_goto_set", R.string.redpacket_goto_set), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        RedpktSendActivity.this.lambda$null$31$RedpktSendActivity(dialogInterface, i);
                    }
                }, $$Lambda$RedpktSendActivity$byyt_mE0ht4o3mD1iXQCQ2ZtbZo.INSTANCE, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        RedpktSendActivity.this.lambda$null$34$RedpktSendActivity(dialogInterface);
                    }
                });
            } else {
                this.inited = true;
            }
        } else {
            WalletDialogUtil.showConfirmBtnWalletDialog((Object) this, (CharSequence) WalletErrorUtil.getErrorDescription(LocaleController.formatString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater, new Object[0]), model.message), true, (DialogInterface.OnDismissListener) new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    RedpktSendActivity.this.lambda$null$35$RedpktSendActivity(dialogInterface);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$21$RedpktSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$22$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$23() {
    }

    public /* synthetic */ void lambda$null$25$RedpktSendActivity(DialogInterface dialog) {
        finish();
    }

    static /* synthetic */ void lambda$null$26(DialogInterface dialogInterface, int i) {
    }

    public /* synthetic */ void lambda$null$27$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$28() {
    }

    public /* synthetic */ void lambda$null$30$RedpktSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$31$RedpktSendActivity(DialogInterface dialogInterface, int i) {
        finish();
    }

    static /* synthetic */ void lambda$null$32() {
    }

    public /* synthetic */ void lambda$null$34$RedpktSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$null$35$RedpktSendActivity(DialogInterface dialog) {
        finish();
    }

    public /* synthetic */ void lambda$getAccountInfo$37$RedpktSendActivity(DialogInterface dialog) {
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
                    RedpktSendActivity.lambda$performService$40(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performService$40(SharedPreferences preferences, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
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
                    RedpktSendActivity.lambda$null$38(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    RedpktSendActivity.lambda$null$39(XAlertDialog.this);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$38(SharedPreferences preferences, TLRPC.TL_help_support res, XAlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
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

    static /* synthetic */ void lambda$null$39(XAlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void finish() {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                RedpktSendActivity.this.finishFragment();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {
        private MyAdapter() {
        }

        public int getCount() {
            return RedpktSendActivity.this.mNumbers.size();
        }

        public Integer getItem(int position) {
            return (Integer) RedpktSendActivity.this.mNumbers.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(RedpktSendActivity.this.mContext, R.layout.item_password_number, (ViewGroup) null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView) convertView.findViewById(R.id.btn_number);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < 9 || position == 10) {
                holder.tvNumber.setText(String.valueOf(RedpktSendActivity.this.mNumbers.get(position)));
                holder.tvNumber.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (position == 9) {
                holder.tvNumber.setVisibility(4);
            } else if (position == 11) {
                holder.tvNumber.setVisibility(4);
                holder.ivDelete.setVisibility(0);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivDelete;
        TextView tvNumber;

        ViewHolder() {
        }
    }
}
