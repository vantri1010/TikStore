package im.bclpbkiauv.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.OnClick;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.MessagesStorage;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.DrawableUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.CountrySelectActivity;
import im.bclpbkiauv.ui.actionbar.ActionBar;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.actionbar.XAlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.hviews.MryTextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.slf4j.Marker;

public class ChangePhoneNumberActivity extends BaseFragment {
    private TextWatcher codeWatcher;
    private HashMap<String, String> codesMap = new HashMap<>();
    private ArrayList<String> countriesArray = new ArrayList<>();
    private HashMap<String, String> countriesMap = new HashMap<>();
    @BindView(2131296435)
    MryRoundButton mBtnSubmit;
    @BindView(2131296593)
    MryEditText mEtCode;
    @BindView(2131296599)
    MryEditText mEtPhoneNumber;
    @BindView(2131296793)
    ImageView mIvClear;
    @BindView(2131296932)
    LinearLayout mLlCodeContainer;
    @BindView(2131296949)
    LinearLayout mLlPhoneContainer;
    private CountDownTimer mTimer = new CountDownTimer(DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS, 1000) {
        public void onTick(long millisUntilFinished) {
            MryTextView mryTextView = ChangePhoneNumberActivity.this.mTvSendCode;
            mryTextView.setText((millisUntilFinished / 1000) + "s后重发");
            ChangePhoneNumberActivity.this.mTvSendCode.setTextColor(-12862209);
            ChangePhoneNumberActivity.this.mTvSendCode.setEnabled(false);
        }

        public void onFinish() {
            ChangePhoneNumberActivity.this.mTvSendCode.setText(LocaleController.getString("GetPhoneCode", R.string.GetPhoneCode));
            ChangePhoneNumberActivity.this.mTvSendCode.setTextColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
            ChangePhoneNumberActivity.this.mTvSendCode.setEnabled(true);
        }
    };
    @BindView(2131297744)
    MryTextView mTvCountryCode;
    @BindView(2131297845)
    MryTextView mTvSendCode;
    private HashMap<String, String> phoneFormatMap = new HashMap<>();
    private String phoneHash;
    private TextWatcher phoneNumberWatcher;

    public View createView(Context context) {
        this.fragmentView = LayoutInflater.from(context).inflate(R.layout.activity_change_phone_number, (ViewGroup) null);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        initActionBar();
        useButterKnife();
        initView();
        initData();
        return this.fragmentView;
    }

    private void initActionBar() {
        this.actionBar.setCastShadows(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.ChangePhoneNumber2));
        this.actionBar.setBackButtonImage(R.mipmap.ic_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangePhoneNumberActivity.this.finishFragment();
                }
            }
        });
    }

    private void initView() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        this.mLlCodeContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
        MryEditText mryEditText = this.mEtPhoneNumber;
        AnonymousClass2 r1 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                ChangePhoneNumberActivity.this.mIvClear.setVisibility(TextUtils.isEmpty(s) ? 8 : 0);
                ChangePhoneNumberActivity.this.checkBntEnable();
            }
        };
        this.phoneNumberWatcher = r1;
        mryEditText.addTextChangedListener(r1);
        MryEditText mryEditText2 = this.mEtCode;
        AnonymousClass3 r12 = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                ChangePhoneNumberActivity.this.checkBntEnable();
            }
        };
        this.codeWatcher = r12;
        mryEditText2.addTextChangedListener(r12);
        this.mBtnSubmit.setPrimaryRadiusAdjustBoundsFillStyle();
        this.mBtnSubmit.setBackgroundColor(-12862209);
    }

    private void initData() {
        String countryName;
        HashMap<String, String> languageMap = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getParentActivity().getResources().getAssets().open("countries.txt")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String[] args = line.split(";");
                this.countriesArray.add(0, args[2]);
                this.countriesMap.put(args[2], args[0]);
                this.codesMap.put(args[0], args[2]);
                if (args.length > 3) {
                    this.phoneFormatMap.put(args[0], args[3]);
                }
                languageMap.put(args[1], args[2]);
            }
            reader.close();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Collections.sort(this.countriesArray, $$Lambda$TEfSBt3hRUlBSSARfPEHsJesTtE.INSTANCE);
        String country = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (telephonyManager != null) {
                country = telephonyManager.getSimCountryIso().toUpperCase();
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        if (country != null && (countryName = languageMap.get(country)) != null && this.countriesArray.indexOf(countryName) != -1) {
            MryTextView mryTextView = this.mTvCountryCode;
            mryTextView.setText(Marker.ANY_NON_NULL_MARKER + this.countriesMap.get(countryName));
            this.mEtPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.phoneFormatMap.get(this.countriesMap.get(countryName)).replace(" ", "").length()) {
            }});
        }
    }

    private void check(boolean sendMsg) {
        boolean tag = false;
        if (!TextUtils.isEmpty(this.mTvCountryCode.getText()) && TextUtils.isEmpty(this.mTvCountryCode.getText().toString().trim().replaceAll("\\+", ""))) {
            tag = true;
        }
        if (tag) {
            ToastUtils.show((int) R.string.ReminderPleaseSelectCountry);
        } else if (sendMsg) {
            sendSms();
        } else {
            submit();
        }
    }

    /* access modifiers changed from: private */
    public void checkBntEnable() {
        boolean tag = false;
        if (!TextUtils.isEmpty(this.mTvCountryCode.getText()) && !TextUtils.isEmpty(this.mTvCountryCode.getText().toString().trim().replaceAll("\\+", ""))) {
            tag = true;
        }
        this.mBtnSubmit.setEnabled(tag && !TextUtils.isEmpty(this.mEtPhoneNumber.getText()) && !TextUtils.isEmpty(this.mEtCode.getText()));
    }

    private void sendSms() {
        String phoneNumber = this.mEtPhoneNumber.getText().toString().trim();
        String countryCode = this.mTvCountryCode.getText().toString();
        if (TextUtils.isEmpty(countryCode)) {
            ToastUtils.show((int) R.string.WrongCountry);
            this.mLlPhoneContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$sendSms$0$ChangePhoneNumberActivity();
                }
            }, 3000);
        } else if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.show((int) R.string.InvalidPhoneNumberTips);
            this.mLlPhoneContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$sendSms$1$ChangePhoneNumberActivity();
                }
            }, 3000);
        } else {
            String phone = countryCode.replace(Marker.ANY_NON_NULL_MARKER, "") + phoneNumber;
            if (getUserConfig() == null || getUserConfig().getClientPhone() == null || !getUserConfig().getClientPhone().equals(phone)) {
                TLRPC.TL_account_sendChangePhoneCode req = new TLRPC.TL_account_sendChangePhoneCode();
                req.phone_number = phone;
                req.settings = new TLRPC.TL_codeSettings();
                req.settings.allow_flashcall = false;
                req.settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                if (req.settings.allow_app_hash) {
                    preferences.edit().putString("sms_hash", BuildVars.SMS_HASH).commit();
                } else {
                    preferences.edit().remove("sms_hash").commit();
                }
                XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
                int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog, phoneNumber) {
                    private final /* synthetic */ XAlertDialog f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChangePhoneNumberActivity.this.lambda$sendSms$5$ChangePhoneNumberActivity(this.f$1, this.f$2, tLObject, tL_error);
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ChangePhoneNumberActivity.this.lambda$sendSms$6$ChangePhoneNumberActivity(this.f$1, dialogInterface);
                    }
                });
                progressDialog.show();
                return;
            }
            ToastUtils.show((int) R.string.CannotMatchTheCurrentPhoneNumber);
            this.mLlPhoneContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$sendSms$2$ChangePhoneNumberActivity();
                }
            }, 3000);
        }
    }

    public /* synthetic */ void lambda$sendSms$0$ChangePhoneNumberActivity() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$sendSms$1$ChangePhoneNumberActivity() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$sendSms$2$ChangePhoneNumberActivity() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$sendSms$5$ChangePhoneNumberActivity(XAlertDialog progressDialog, String phoneNumber, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response, phoneNumber) {
            private final /* synthetic */ XAlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ChangePhoneNumberActivity.this.lambda$null$4$ChangePhoneNumberActivity(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$4$ChangePhoneNumberActivity(XAlertDialog progressDialog, TLRPC.TL_error error, TLObject response, String phoneNumber) {
        progressDialog.dismiss();
        if (error == null) {
            this.phoneHash = ((TLRPC.TL_auth_sentCode) response).phone_code_hash;
            this.mTimer.start();
            ToastUtils.show((CharSequence) LocaleController.getString("SendSuccess", R.string.SendSuccess));
        } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
            ToastUtils.show((int) R.string.InvalidPhoneNumberTips);
            this.mLlPhoneContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$null$3$ChangePhoneNumberActivity();
                }
            }, 3000);
        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
            ToastUtils.show((int) R.string.InvalidCode);
        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
            ToastUtils.show((int) R.string.CodeExpired);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            ToastUtils.show((int) R.string.FloodWait);
        } else if (error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
            ToastUtils.show((CharSequence) LocaleController.formatString("ChangePhoneNumberOccupied", R.string.ChangePhoneNumberOccupied, phoneNumber));
        } else if (error.text.contains("IPORDE_LIMIT")) {
            ToastUtils.show((CharSequence) LocaleController.getString("IpOrDeLimit", R.string.IpOrDeLimit));
        } else if (error.text.equals("INTERNAL")) {
            ToastUtils.show((CharSequence) LocaleController.getString("InternalError", R.string.InternalError));
        } else {
            ToastUtils.show((int) R.string.ErrorOccurred);
        }
    }

    public /* synthetic */ void lambda$null$3$ChangePhoneNumberActivity() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$sendSms$6$ChangePhoneNumberActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    private void submit() {
        String phoneNumber = this.mEtPhoneNumber.getText().toString().trim();
        String countryCode = this.mTvCountryCode.getText().toString();
        TLRPC.TL_account_changePhone req = new TLRPC.TL_account_changePhone();
        req.phone_number = countryCode.replace(Marker.ANY_NON_NULL_MARKER, "") + " " + phoneNumber;
        req.phone_code = this.mEtCode.getText().toString().trim();
        req.phone_code_hash = this.phoneHash;
        XAlertDialog progressDialog = new XAlertDialog(getParentActivity(), 4);
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate(progressDialog) {
            private final /* synthetic */ XAlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangePhoneNumberActivity.this.lambda$submit$10$ChangePhoneNumberActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(reqId) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                ChangePhoneNumberActivity.this.lambda$submit$11$ChangePhoneNumberActivity(this.f$1, dialogInterface);
            }
        });
        progressDialog.show();
    }

    public /* synthetic */ void lambda$submit$10$ChangePhoneNumberActivity(XAlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(progressDialog, error, response) {
            private final /* synthetic */ XAlertDialog f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChangePhoneNumberActivity.this.lambda$null$9$ChangePhoneNumberActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$ChangePhoneNumberActivity(XAlertDialog progressDialog, TLRPC.TL_error error, TLObject response) {
        progressDialog.dismiss();
        if (error == null) {
            TLRPC.User user = (TLRPC.User) response;
            UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            UserConfig.getInstance(this.currentAccount).saveConfig(true);
            ArrayList<TLRPC.User> users = new ArrayList<>();
            users.add(user);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
            MessagesController.getInstance(this.currentAccount).putUser(user, false);
            finishFragment();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
            ToastUtils.show((int) R.string.InvalidPhoneNumberTips);
            this.mLlPhoneContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$null$7$ChangePhoneNumberActivity();
                }
            }, 3000);
        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
            ToastUtils.show((int) R.string.VerificationCodeError);
            this.mLlCodeContainer.setBackground(DrawableUtils.createLayerDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), -570319, (float) AndroidUtilities.dp(5.0f)));
            this.fragmentView.postDelayed(new Runnable() {
                public final void run() {
                    ChangePhoneNumberActivity.this.lambda$null$8$ChangePhoneNumberActivity();
                }
            }, 3000);
        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
            ToastUtils.show((int) R.string.CodeExpired);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            ToastUtils.show((int) R.string.FloodWait);
        } else {
            ToastUtils.show((CharSequence) error.text);
        }
    }

    public /* synthetic */ void lambda$null$7$ChangePhoneNumberActivity() {
        this.mLlPhoneContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$null$8$ChangePhoneNumberActivity() {
        this.mLlCodeContainer.setBackground(Theme.createRoundRectDrawable((float) AndroidUtilities.dp(5.0f), Theme.getColor(Theme.key_windowBackgroundWhite)));
    }

    public /* synthetic */ void lambda$submit$11$ChangePhoneNumberActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    @OnClick({2131297744, 2131296793, 2131297845, 2131296435})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                check(false);
                return;
            case R.id.iv_clear:
                MryEditText mryEditText = this.mEtPhoneNumber;
                if (mryEditText != null) {
                    mryEditText.setText((CharSequence) null);
                    return;
                }
                return;
            case R.id.tv_country_code:
                CountrySelectActivity fragment = new CountrySelectActivity(true);
                fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() {
                    public final void didSelectCountry(CountrySelectActivity.Country country) {
                        ChangePhoneNumberActivity.this.lambda$onViewClicked$12$ChangePhoneNumberActivity(country);
                    }
                });
                presentFragment(fragment);
                return;
            case R.id.tv_send_code:
                check(true);
                return;
            default:
                return;
        }
    }

    public /* synthetic */ void lambda$onViewClicked$12$ChangePhoneNumberActivity(CountrySelectActivity.Country country) {
        MryTextView mryTextView = this.mTvCountryCode;
        mryTextView.setText(Marker.ANY_NON_NULL_MARKER + country.code);
        this.mEtPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(country.phoneFormat.replace(" ", "").length()) {
        }});
    }

    public void onFragmentDestroy() {
        TextWatcher textWatcher;
        TextWatcher textWatcher2;
        MryEditText mryEditText = this.mEtPhoneNumber;
        if (!(mryEditText == null || (textWatcher2 = this.phoneNumberWatcher) == null)) {
            mryEditText.removeTextChangedListener(textWatcher2);
        }
        MryEditText mryEditText2 = this.mEtCode;
        if (!(mryEditText2 == null || (textWatcher = this.codeWatcher) == null)) {
            mryEditText2.removeTextChangedListener(textWatcher);
        }
        CountDownTimer countDownTimer = this.mTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mTimer = null;
        }
        super.onFragmentDestroy();
    }
}
