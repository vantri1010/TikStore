package im.bclpbkiauv.ui;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import com.alibaba.fastjson.JSONObject;
import com.gyf.barlibrary.OSUtils;
import com.serenegiant.uvccamera.BuildConfig;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.utils.AesUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TwoStepVerificationActivityNew extends AppCompatActivity implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public MryRoundButton btnOk;
    private ConstraintLayout clAgain;
    private ConstraintLayout clFirst;
    protected int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public EditText editPasswordAgain;
    /* access modifiers changed from: private */
    public EditText editPasswordFirst;
    private boolean etPwdIsHideAgain;
    private boolean etPwdIsHideFirst;
    /* access modifiers changed from: private */
    public MryAlphaImageView imgClearAgain;
    /* access modifiers changed from: private */
    public MryAlphaImageView imgClearFirst;
    private MryAlphaImageView imgShowPasswordAgain;
    private MryAlphaImageView imgShowPasswordFirst;
    boolean passwordEntered;
    private AlertDialog progressDialog;
    private int state = 1;
    private Toolbar toolBar;
    private TextView twoTitle;
    private TextView txtDeletePassword;
    private TextView txtNoticeHadPassword;
    private TextView txtNoticeNoPassword;
    private TextView txtPasswordTitle;
    private TextView txtUpdatePassword;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_two_password);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        setSpecialBarDarkMode();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.toolBar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle((CharSequence) "");
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        this.toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$0$TwoStepVerificationActivityNew(view);
            }
        });
        this.clFirst = (ConstraintLayout) findViewById(R.id.cl_first);
        this.clAgain = (ConstraintLayout) findViewById(R.id.cl_again);
        this.imgClearFirst = (MryAlphaImageView) findViewById(R.id.ivClearPassword1);
        this.imgClearAgain = (MryAlphaImageView) findViewById(R.id.ivClearPassword2);
        this.imgShowPasswordFirst = (MryAlphaImageView) findViewById(R.id.ivPwdShow1);
        this.imgShowPasswordAgain = (MryAlphaImageView) findViewById(R.id.ivPwdShow2);
        this.twoTitle = (TextView) findViewById(R.id.txt_tow_title);
        this.txtPasswordTitle = (TextView) findViewById(R.id.txt_password_title);
        this.txtNoticeNoPassword = (TextView) findViewById(R.id.txt_notice_no_password);
        this.txtNoticeHadPassword = (TextView) findViewById(R.id.txt_notice_had_password);
        this.txtDeletePassword = (TextView) findViewById(R.id.txt_delete_password);
        this.txtUpdatePassword = (TextView) findViewById(R.id.txt_update_password);
        this.editPasswordFirst = (EditText) findViewById(R.id.edit_password_first);
        this.editPasswordAgain = (EditText) findViewById(R.id.edit_password_again);
        MryRoundButton mryRoundButton = (MryRoundButton) findViewById(R.id.btn_ok);
        this.btnOk = mryRoundButton;
        mryRoundButton.setEnabled(false);
        this.btnOk.setPrimaryRadiusAdjustBoundsFillStyle();
        this.imgClearFirst.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$1$TwoStepVerificationActivityNew(view);
            }
        });
        this.imgClearAgain.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$2$TwoStepVerificationActivityNew(view);
            }
        });
        this.imgShowPasswordFirst.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$3$TwoStepVerificationActivityNew(view);
            }
        });
        this.imgShowPasswordAgain.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$4$TwoStepVerificationActivityNew(view);
            }
        });
        this.twoTitle.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$5$TwoStepVerificationActivityNew(view);
            }
        });
        this.btnOk.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$6$TwoStepVerificationActivityNew(view);
            }
        });
        this.txtUpdatePassword.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$7$TwoStepVerificationActivityNew(view);
            }
        });
        this.txtDeletePassword.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivityNew.this.lambda$onCreate$8$TwoStepVerificationActivityNew(view);
            }
        });
        this.twoTitle.setText(LocaleController.getString("SetAdditionalPassword", R.string.SetAdditionalPassword));
        this.txtPasswordTitle.setText(LocaleController.getString("two_password_notice", R.string.two_password_notice));
        this.txtNoticeNoPassword.setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
        this.txtNoticeHadPassword.setText(LocaleController.getString("EnabledPasswordText", R.string.EnabledPasswordText));
        this.txtDeletePassword.setText(LocaleController.getString("TurnPasswordOff", R.string.TurnPasswordOff));
        this.txtUpdatePassword.setText(LocaleController.getString("ChangePassword", R.string.ChangePassword));
        this.editPasswordFirst.setHint(LocaleController.getString("text_type_passwod", R.string.text_type_passwod));
        this.editPasswordAgain.setHint(LocaleController.getString("text_type_password_again", R.string.text_type_password_again));
        this.btnOk.setText(LocaleController.getString("OK", R.string.OK));
        this.editPasswordFirst.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    TwoStepVerificationActivityNew.this.imgClearFirst.setVisibility(0);
                } else {
                    TwoStepVerificationActivityNew.this.imgClearFirst.setVisibility(8);
                }
            }

            public void afterTextChanged(Editable s) {
                if (TwoStepVerificationActivityNew.this.editPasswordAgain.getVisibility() == 0) {
                    if (TextUtils.isEmpty(s) || TextUtils.isEmpty(TwoStepVerificationActivityNew.this.editPasswordAgain.getText().toString().trim())) {
                        TwoStepVerificationActivityNew.this.btnOk.setEnabled(false);
                    } else {
                        TwoStepVerificationActivityNew.this.btnOk.setEnabled(true);
                    }
                } else if (!TextUtils.isEmpty(s)) {
                    TwoStepVerificationActivityNew.this.btnOk.setEnabled(true);
                } else {
                    TwoStepVerificationActivityNew.this.btnOk.setEnabled(false);
                }
            }
        });
        this.editPasswordAgain.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    TwoStepVerificationActivityNew.this.imgClearAgain.setVisibility(0);
                } else {
                    TwoStepVerificationActivityNew.this.imgClearAgain.setVisibility(8);
                }
            }

            public void afterTextChanged(Editable s) {
                if (TwoStepVerificationActivityNew.this.editPasswordFirst.getVisibility() == 0) {
                    if (TextUtils.isEmpty(s) || TextUtils.isEmpty(TwoStepVerificationActivityNew.this.editPasswordFirst.getText().toString().trim())) {
                        TwoStepVerificationActivityNew.this.btnOk.setEnabled(false);
                    } else {
                        TwoStepVerificationActivityNew.this.btnOk.setEnabled(true);
                    }
                } else if (!TextUtils.isEmpty(s)) {
                    TwoStepVerificationActivityNew.this.btnOk.setEnabled(true);
                } else {
                    TwoStepVerificationActivityNew.this.btnOk.setEnabled(false);
                }
            }
        });
        needShowProgress();
        getHasPassword();
    }

    public /* synthetic */ void lambda$onCreate$0$TwoStepVerificationActivityNew(View view) {
        finish();
    }

    public /* synthetic */ void lambda$onCreate$1$TwoStepVerificationActivityNew(View v) {
        this.editPasswordFirst.getText().clear();
    }

    public /* synthetic */ void lambda$onCreate$2$TwoStepVerificationActivityNew(View v) {
        this.editPasswordAgain.getText().clear();
    }

    public /* synthetic */ void lambda$onCreate$3$TwoStepVerificationActivityNew(View v) {
        boolean z = !this.etPwdIsHideFirst;
        this.etPwdIsHideFirst = z;
        if (z) {
            this.imgShowPasswordFirst.setImageResource(R.mipmap.eye_close);
            this.editPasswordFirst.setTransformationMethod(PasswordTransformationMethod.getInstance());
            return;
        }
        this.imgShowPasswordFirst.setImageResource(R.mipmap.eye_open);
        this.editPasswordFirst.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public /* synthetic */ void lambda$onCreate$4$TwoStepVerificationActivityNew(View v) {
        boolean z = !this.etPwdIsHideAgain;
        this.etPwdIsHideAgain = z;
        if (z) {
            this.imgShowPasswordAgain.setImageResource(R.mipmap.eye_close);
            this.editPasswordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
            return;
        }
        this.imgShowPasswordAgain.setImageResource(R.mipmap.eye_open);
        this.editPasswordAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    public /* synthetic */ void lambda$onCreate$5$TwoStepVerificationActivityNew(View view) {
        this.twoTitle.setVisibility(8);
        this.txtNoticeNoPassword.setVisibility(8);
        this.txtPasswordTitle.setVisibility(0);
        this.editPasswordFirst.setVisibility(0);
        this.editPasswordAgain.setVisibility(0);
        this.btnOk.setVisibility(0);
        this.clFirst.setVisibility(0);
        this.clAgain.setVisibility(0);
        this.state = 1;
    }

    public /* synthetic */ void lambda$onCreate$6$TwoStepVerificationActivityNew(View view) {
        int i = this.state;
        if (i == 1) {
            setPassword();
        } else if (i == 2) {
            updatePassword();
        } else if (i == 3) {
            deletePassword();
        } else if (i == 4) {
            checkPassword();
        }
    }

    public /* synthetic */ void lambda$onCreate$7$TwoStepVerificationActivityNew(View view) {
        this.twoTitle.setVisibility(8);
        this.txtUpdatePassword.setVisibility(8);
        this.txtDeletePassword.setVisibility(8);
        this.txtNoticeHadPassword.setVisibility(8);
        this.txtPasswordTitle.setVisibility(0);
        this.editPasswordFirst.setVisibility(0);
        this.editPasswordAgain.setVisibility(0);
        this.btnOk.setVisibility(0);
        this.state = 2;
        this.clFirst.setVisibility(0);
        this.clAgain.setVisibility(0);
        this.editPasswordFirst.getText().clear();
        this.editPasswordAgain.getText().clear();
        this.editPasswordFirst.setHint(LocaleController.getString("text_type_old_password", R.string.text_type_old_password));
        this.editPasswordAgain.setHint(LocaleController.getString("text_type_new_password", R.string.text_type_new_password));
    }

    public /* synthetic */ void lambda$onCreate$8$TwoStepVerificationActivityNew(View view) {
        this.twoTitle.setVisibility(8);
        this.txtUpdatePassword.setVisibility(8);
        this.txtDeletePassword.setVisibility(8);
        this.txtNoticeHadPassword.setVisibility(8);
        this.txtPasswordTitle.setVisibility(0);
        this.editPasswordFirst.setVisibility(0);
        this.editPasswordAgain.setVisibility(8);
        this.btnOk.setVisibility(0);
        this.state = 3;
        this.editPasswordFirst.getText().clear();
        this.editPasswordAgain.getText().clear();
        this.editPasswordFirst.setHint(LocaleController.getString("SetAdditionalPassword", R.string.text_type_passwod));
        this.clFirst.setVisibility(0);
        this.clAgain.setVisibility(8);
    }

    private void needShowProgress() {
        AlertDialog alertDialog = new AlertDialog(this, 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(true);
        this.progressDialog.show();
    }

    public void hideView() {
        this.twoTitle.setVisibility(8);
        this.txtUpdatePassword.setVisibility(8);
        this.txtDeletePassword.setVisibility(8);
        this.txtNoticeHadPassword.setVisibility(8);
        this.txtPasswordTitle.setVisibility(8);
        this.editPasswordFirst.setVisibility(8);
        this.editPasswordAgain.setVisibility(8);
        this.editPasswordAgain.getText().clear();
        this.editPasswordFirst.getText().clear();
        this.clFirst.setVisibility(8);
        this.clAgain.setVisibility(8);
        this.btnOk.setVisibility(8);
    }

    public void getHasPassword() {
        hideView();
        TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessKey", (Object) "password_info");
        jsonObject.put("currPwdHash", (Object) "");
        jsonObject.put("newPasswordHash", (Object) "");
        req.data.data = jsonObject.toJSONString();
        Log.e(BuildConfig.BUILD_TYPE, "request===" + jsonObject.toJSONString());
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivityNew.this.lambda$getHasPassword$10$TwoStepVerificationActivityNew(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$getHasPassword$10$TwoStepVerificationActivityNew(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivityNew.this.lambda$null$9$TwoStepVerificationActivityNew(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$9$TwoStepVerificationActivityNew(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            Log.e(BuildConfig.BUILD_TYPE, "password_info response===" + JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data));
            this.progressDialog.dismiss();
            if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                JSONObject resp = (JSONObject) JSONObject.parse(((JSONObject) JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data))).getString("data"));
                if (resp.getInteger("code").intValue() == 402) {
                    this.state = 1;
                    this.twoTitle.setVisibility(0);
                    this.txtNoticeNoPassword.setVisibility(0);
                } else if (resp.getInteger("code").intValue() == 0) {
                    this.state = 4;
                    this.txtNoticeHadPassword.setVisibility(0);
                    this.txtPasswordTitle.setVisibility(0);
                    this.editPasswordFirst.setVisibility(0);
                    this.btnOk.setVisibility(0);
                    this.clFirst.setVisibility(0);
                }
            }
        } else {
            Log.e(BuildConfig.BUILD_TYPE, "password_info error===" + JSONObject.toJSONString(error));
            this.progressDialog.dismiss();
            if (error.text.contains("INVALID_PASSWORD")) {
                ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
            } else if (error.text.contains("INTERNAL_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
            }
        }
    }

    public void setPassword() {
        if (TextUtils.isEmpty(this.editPasswordAgain.getText().toString()) || TextUtils.isEmpty(this.editPasswordFirst.getText().toString())) {
            ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.text_password_not_empty));
        } else if (!this.editPasswordAgain.getText().toString().trim().equals(this.editPasswordFirst.getText().toString().trim())) {
            ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.text_password_not_same));
        } else {
            TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("businessKey", (Object) "password_setting");
            jsonObject.put("currPwdHash", (Object) "");
            jsonObject.put("newPasswordHash", (Object) AesUtils.encryptToBase64(this.editPasswordAgain.getText().toString().trim()));
            req.data.data = jsonObject.toJSONString();
            Log.e(BuildConfig.BUILD_TYPE, "request===" + jsonObject.toJSONString());
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TwoStepVerificationActivityNew.this.lambda$setPassword$12$TwoStepVerificationActivityNew(tLObject, tL_error);
                }
            }, 10);
        }
    }

    public /* synthetic */ void lambda$setPassword$12$TwoStepVerificationActivityNew(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivityNew.this.lambda$null$11$TwoStepVerificationActivityNew(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$11$TwoStepVerificationActivityNew(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            Log.e(BuildConfig.BUILD_TYPE, "password_setting response===" + JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data));
            this.progressDialog.dismiss();
            if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                JSONObject resp = (JSONObject) JSONObject.parse(((JSONObject) JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data))).getString("data"));
                if (resp.getInteger("code").intValue() == 403) {
                    ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
                } else if (resp.getInteger("code").intValue() == 404) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
                } else if (resp.getInteger("code").intValue() == 0) {
                    getHasPassword();
                }
            }
        } else {
            this.progressDialog.dismiss();
            if (error.text.contains("INVALID_PASSWORD")) {
                ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
            } else if (error.text.contains("INTERNAL_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
            }
        }
    }

    public void updatePassword() {
        if (TextUtils.isEmpty(this.editPasswordAgain.getText().toString()) || TextUtils.isEmpty(this.editPasswordFirst.getText().toString())) {
            ToastUtils.show((CharSequence) LocaleController.getString(R.string.text_password_not_empty));
            return;
        }
        TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessKey", (Object) "password_update");
        jsonObject.put("currPwdHash", (Object) AesUtils.encryptToBase64(this.editPasswordFirst.getText().toString().trim()));
        jsonObject.put("newPasswordHash", (Object) AesUtils.encryptToBase64(this.editPasswordAgain.getText().toString().trim()));
        req.data.data = jsonObject.toJSONString();
        Log.e(BuildConfig.BUILD_TYPE, "request===" + jsonObject.toJSONString());
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivityNew.this.lambda$updatePassword$14$TwoStepVerificationActivityNew(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$updatePassword$14$TwoStepVerificationActivityNew(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivityNew.this.lambda$null$13$TwoStepVerificationActivityNew(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$TwoStepVerificationActivityNew(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            Log.e(BuildConfig.BUILD_TYPE, "password_update response===" + JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data));
            this.progressDialog.dismiss();
            if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                JSONObject resp = (JSONObject) JSONObject.parse(((JSONObject) JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data))).getString("data"));
                if (resp.getInteger("code").intValue() == 403) {
                    ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.LoginPwdError));
                } else if (resp.getInteger("code").intValue() == 404) {
                    ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.text_system_error));
                } else if (resp.getInteger("code").intValue() == 0) {
                    ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.text_update_success));
                    this.twoTitle.setVisibility(8);
                    this.txtUpdatePassword.setVisibility(0);
                    this.txtDeletePassword.setVisibility(0);
                    this.txtNoticeHadPassword.setVisibility(0);
                    this.txtPasswordTitle.setVisibility(8);
                    this.editPasswordFirst.setVisibility(8);
                    this.editPasswordAgain.setVisibility(8);
                    this.btnOk.setVisibility(8);
                    this.clFirst.setVisibility(8);
                    this.clAgain.setVisibility(8);
                }
            }
        } else {
            this.progressDialog.dismiss();
            if (error.text.contains("INVALID_PASSWORD")) {
                ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
            } else if (error.text.contains("INTERNAL_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
            }
        }
    }

    public void deletePassword() {
        if (TextUtils.isEmpty(this.editPasswordFirst.getText().toString())) {
            ToastUtils.show((CharSequence) LocaleController.getString("SetAdditionalPassword", R.string.text_password_not_empty));
            return;
        }
        TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessKey", (Object) "password_delete");
        jsonObject.put("currPwdHash", (Object) AesUtils.encryptToBase64(this.editPasswordFirst.getText().toString().trim()));
        jsonObject.put("newPasswordHash", (Object) "");
        req.data.data = jsonObject.toJSONString();
        Log.e(BuildConfig.BUILD_TYPE, "request===" + jsonObject.toJSONString());
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivityNew.this.lambda$deletePassword$16$TwoStepVerificationActivityNew(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$deletePassword$16$TwoStepVerificationActivityNew(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivityNew.this.lambda$null$15$TwoStepVerificationActivityNew(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$15$TwoStepVerificationActivityNew(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            Log.e(BuildConfig.BUILD_TYPE, "password_delete response===" + JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data));
            this.progressDialog.dismiss();
            if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                JSONObject resp = (JSONObject) JSONObject.parse(((JSONObject) JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data))).getString("data"));
                if (resp.getInteger("code").intValue() == 403) {
                    ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
                } else if (resp.getInteger("code").intValue() == 404) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
                } else if (resp.getInteger("code").intValue() == 0) {
                    ToastUtils.show((CharSequence) LocaleController.getString("DeleteSuccess", R.string.DeleteSuccess));
                    getHasPassword();
                }
            }
        } else {
            this.progressDialog.dismiss();
            if (error.text.contains("INVALID_PASSWORD")) {
                ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
            } else if (error.text.contains("INTERNAL_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
            }
        }
    }

    public void checkPassword() {
        TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessKey", (Object) "password_check");
        jsonObject.put("currPwdHash", (Object) AesUtils.encryptToBase64(this.editPasswordFirst.getText().toString().trim()));
        jsonObject.put("newPasswordHash", (Object) "");
        req.data.data = jsonObject.toJSONString();
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TwoStepVerificationActivityNew.this.lambda$checkPassword$18$TwoStepVerificationActivityNew(tLObject, tL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$checkPassword$18$TwoStepVerificationActivityNew(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable(error, response) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivityNew.this.lambda$null$17$TwoStepVerificationActivityNew(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$TwoStepVerificationActivityNew(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            Log.e(BuildConfig.BUILD_TYPE, "password_check response===" + JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data));
            this.progressDialog.dismiss();
            if (response instanceof TLRPCWallet.TL_paymentTransResult) {
                JSONObject resp = (JSONObject) JSONObject.parse(((JSONObject) JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) response).data))).getString("data"));
                if (resp.getInteger("code").intValue() == 403) {
                    ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
                } else if (resp.getInteger("code").intValue() == 404) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
                } else if (resp.getInteger("code").intValue() == 408) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_password_error_limit", R.string.text_password_error_limit));
                } else if (resp.getInteger("code").intValue() == 0) {
                    hideView();
                    this.txtNoticeHadPassword.setVisibility(0);
                    this.txtUpdatePassword.setVisibility(0);
                    this.txtDeletePassword.setVisibility(0);
                }
            }
        } else {
            Log.e(BuildConfig.BUILD_TYPE, "password_check error===" + JSONObject.toJSONString(error));
            this.progressDialog.dismiss();
            if (error.text.contains("INVALID_PASSWORD")) {
                ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
            } else if (error.text.contains("INTERNAL_ERROR")) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
            } else if (error.text.contains("MANY_PASSWORD_ERROR")) {
                LocaleController.getString("text_password_error_limit", R.string.text_password_error_limit);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            for (Object arg : args) {
                Log.e(BuildConfig.BUILD_TYPE, "response===" + JSONObject.toJSONString(arg));
            }
        }
    }

    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<?> cls = activity.getWindow().getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            int darkModeFlag = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE").getInt(layoutParams);
            Method extraFlagField = cls.getMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
            Window window = activity.getWindow();
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(darkmode ? darkModeFlag : 0);
            objArr[1] = Integer.valueOf(darkModeFlag);
            extraFlagField.invoke(window, objArr);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        int value;
        if (activity == null) {
            return false;
        }
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt((Object) null);
            int value2 = meizuFlags.getInt(lp);
            if (dark) {
                value = value2 | bit;
            } else {
                value = value2 & (~bit);
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setSpecialBarDarkMode() {
        if (OSUtils.isMIUI6Later()) {
            setMiuiStatusBarDarkMode(this, false);
        }
        if (OSUtils.isFlymeOS4Later()) {
            setMeizuStatusBarDarkIcon(this, false);
        }
    }
}
