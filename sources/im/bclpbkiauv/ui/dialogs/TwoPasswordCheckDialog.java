package im.bclpbkiauv.ui.dialogs;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.alibaba.fastjson.JSONObject;
import com.serenegiant.uvccamera.BuildConfig;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.utils.RegexUtils;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.actionbar.AlertDialog;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryAlphaImageView;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import im.bclpbkiauv.ui.utils.AesUtils;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u00012B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0006\u0010*\u001a\u00020+J\u001a\u0010,\u001a\u00020\u00122\b\u0010-\u001a\u0004\u0018\u00010.2\u0006\u0010/\u001a\u00020\u0012H\u0004J\b\u00100\u001a\u00020+H\u0002J\b\u00101\u001a\u00020+H\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u00020\fX.¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0012X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001a\u0010\u0017\u001a\u00020\u0018X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001d\u001a\u00020\u0018X.¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001a\"\u0004\b\u001f\u0010\u001cR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u001a\u0010\"\u001a\u00020#X.¢\u0006\u000e\n\u0000\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b(\u0010)¨\u00063"}, d2 = {"Lim/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog;", "Lim/bclpbkiauv/ui/dialogs/BaseDialog;", "activity", "Landroidx/fragment/app/FragmentActivity;", "userName", "", "onPasswordCheckListener", "Lim/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog$OnPasswordCheckListener;", "(Landroidx/fragment/app/FragmentActivity;Ljava/lang/String;Lim/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog$OnPasswordCheckListener;)V", "currentAccount", "", "editPassword", "Lim/bclpbkiauv/ui/hviews/MryEditText;", "getEditPassword", "()Lim/bclpbkiauv/ui/hviews/MryEditText;", "setEditPassword", "(Lim/bclpbkiauv/ui/hviews/MryEditText;)V", "etPwdIsHide", "", "getEtPwdIsHide", "()Z", "setEtPwdIsHide", "(Z)V", "imgClear", "Lim/bclpbkiauv/ui/hviews/MryAlphaImageView;", "getImgClear", "()Lim/bclpbkiauv/ui/hviews/MryAlphaImageView;", "setImgClear", "(Lim/bclpbkiauv/ui/hviews/MryAlphaImageView;)V", "imgShowPassword", "getImgShowPassword", "setImgShowPassword", "getOnPasswordCheckListener", "()Lim/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog$OnPasswordCheckListener;", "progressDialog", "Lim/bclpbkiauv/ui/actionbar/AlertDialog;", "getProgressDialog", "()Lim/bclpbkiauv/ui/actionbar/AlertDialog;", "setProgressDialog", "(Lim/bclpbkiauv/ui/actionbar/AlertDialog;)V", "getUserName", "()Ljava/lang/String;", "checkPassword", "", "checkPasswordRule", "et", "Landroid/widget/TextView;", "showErrorToast", "needShowProgress", "onStart", "OnPasswordCheckListener", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
public final class TwoPasswordCheckDialog extends BaseDialog {
    private final FragmentActivity activity;
    private int currentAccount = UserConfig.selectedAccount;
    public MryEditText editPassword;
    private boolean etPwdIsHide;
    public MryAlphaImageView imgClear;
    public MryAlphaImageView imgShowPassword;
    private final OnPasswordCheckListener onPasswordCheckListener;
    public AlertDialog progressDialog;
    private final String userName;

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0004"}, d2 = {"Lim/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog$OnPasswordCheckListener;", "", "onPasswordCheck", "", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
    /* compiled from: TwoPasswordCheckDialog.kt */
    public interface OnPasswordCheckListener {
        void onPasswordCheck();
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public TwoPasswordCheckDialog(FragmentActivity activity2, String userName2, OnPasswordCheckListener onPasswordCheckListener2) {
        super(activity2, R.layout.dialog_two_password);
        Intrinsics.checkParameterIsNotNull(activity2, "activity");
        Intrinsics.checkParameterIsNotNull(userName2, "userName");
        Intrinsics.checkParameterIsNotNull(onPasswordCheckListener2, "onPasswordCheckListener");
        this.activity = activity2;
        this.userName = userName2;
        this.onPasswordCheckListener = onPasswordCheckListener2;
    }

    public final String getUserName() {
        return this.userName;
    }

    public final OnPasswordCheckListener getOnPasswordCheckListener() {
        return this.onPasswordCheckListener;
    }

    public final AlertDialog getProgressDialog() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressDialog");
        }
        return alertDialog;
    }

    public final void setProgressDialog(AlertDialog alertDialog) {
        Intrinsics.checkParameterIsNotNull(alertDialog, "<set-?>");
        this.progressDialog = alertDialog;
    }

    public final MryEditText getEditPassword() {
        MryEditText mryEditText = this.editPassword;
        if (mryEditText == null) {
            Intrinsics.throwUninitializedPropertyAccessException("editPassword");
        }
        return mryEditText;
    }

    public final void setEditPassword(MryEditText mryEditText) {
        Intrinsics.checkParameterIsNotNull(mryEditText, "<set-?>");
        this.editPassword = mryEditText;
    }

    public final MryAlphaImageView getImgClear() {
        MryAlphaImageView mryAlphaImageView = this.imgClear;
        if (mryAlphaImageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imgClear");
        }
        return mryAlphaImageView;
    }

    public final void setImgClear(MryAlphaImageView mryAlphaImageView) {
        Intrinsics.checkParameterIsNotNull(mryAlphaImageView, "<set-?>");
        this.imgClear = mryAlphaImageView;
    }

    public final MryAlphaImageView getImgShowPassword() {
        MryAlphaImageView mryAlphaImageView = this.imgShowPassword;
        if (mryAlphaImageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imgShowPassword");
        }
        return mryAlphaImageView;
    }

    public final void setImgShowPassword(MryAlphaImageView mryAlphaImageView) {
        Intrinsics.checkParameterIsNotNull(mryAlphaImageView, "<set-?>");
        this.imgShowPassword = mryAlphaImageView;
    }

    public final boolean getEtPwdIsHide() {
        return this.etPwdIsHide;
    }

    public final void setEtPwdIsHide(boolean z) {
        this.etPwdIsHide = z;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        setWidthAndHeight(0.9f, 0.0f, 80);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        View findViewById = findViewById(R.id.edit_password);
        if (findViewById == null) {
            Intrinsics.throwNpe();
        }
        this.editPassword = (MryEditText) findViewById;
        View findViewById2 = findViewById(R.id.ivClearPassword1);
        if (findViewById2 == null) {
            Intrinsics.throwNpe();
        }
        this.imgClear = (MryAlphaImageView) findViewById2;
        View findViewById3 = findViewById(R.id.ivPwdShow1);
        if (findViewById3 == null) {
            Intrinsics.throwNpe();
        }
        this.imgShowPassword = (MryAlphaImageView) findViewById3;
        MryRoundButton btnOk = (MryRoundButton) findViewById(R.id.btn_ok);
        if (btnOk == null) {
            Intrinsics.throwNpe();
        }
        btnOk.setPrimaryRadiusAdjustBoundsFillStyle();
        btnOk.setEnabled(false);
        Window window = getWindow();
        if (window == null) {
            Intrinsics.throwNpe();
        }
        window.clearFlags(131080);
        Window window2 = getWindow();
        if (window2 == null) {
            Intrinsics.throwNpe();
        }
        window2.setSoftInputMode(4);
        MryEditText mryEditText = this.editPassword;
        if (mryEditText == null) {
            Intrinsics.throwUninitializedPropertyAccessException("editPassword");
        }
        mryEditText.addTextChangedListener(new TwoPasswordCheckDialog$onStart$1(this, btnOk));
        MryAlphaImageView mryAlphaImageView = this.imgShowPassword;
        if (mryAlphaImageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imgShowPassword");
        }
        mryAlphaImageView.setOnClickListener(new TwoPasswordCheckDialog$onStart$2(this));
        MryAlphaImageView mryAlphaImageView2 = this.imgClear;
        if (mryAlphaImageView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("imgClear");
        }
        mryAlphaImageView2.setOnClickListener(new TwoPasswordCheckDialog$onStart$3(this));
        btnOk.setOnClickListener(new TwoPasswordCheckDialog$onStart$4(this, btnOk));
    }

    /* access modifiers changed from: protected */
    public final boolean checkPasswordRule(TextView et, boolean showErrorToast) {
        if (et == null || et.length() == 0) {
            return false;
        }
        CharSequence $this$trim$iv$iv = et.getText().toString();
        int startIndex$iv$iv = 0;
        int endIndex$iv$iv = $this$trim$iv$iv.length() - 1;
        boolean startFound$iv$iv = false;
        while (startIndex$iv$iv <= endIndex$iv$iv) {
            char it = $this$trim$iv$iv.charAt(!startFound$iv$iv ? startIndex$iv$iv : endIndex$iv$iv) <= ' ' ? (char) 1 : 0;
            if (!startFound$iv$iv) {
                if (it == 0) {
                    startFound$iv$iv = true;
                } else {
                    startIndex$iv$iv++;
                }
            } else if (it == 0) {
                break;
            } else {
                endIndex$iv$iv--;
            }
        }
        String input = $this$trim$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString();
        if (input.length() >= 8) {
            if (input == null) {
                Intrinsics.throwNpe();
            }
            if (input.length() <= 16 && RegexUtils.hasLetterAndNumber(input, false)) {
                return true;
            }
        }
        if (showErrorToast) {
            ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdRule", R.string.LoginPwdRule));
        }
        return false;
    }

    private final void needShowProgress() {
        AlertDialog alertDialog = new AlertDialog(this.activity, 3);
        this.progressDialog = alertDialog;
        if (alertDialog == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressDialog");
        }
        alertDialog.setCanCancel(true);
        AlertDialog alertDialog2 = this.progressDialog;
        if (alertDialog2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("progressDialog");
        }
        alertDialog2.show();
    }

    public final void checkPassword() {
        MryEditText mryEditText = this.editPassword;
        if (mryEditText == null) {
            Intrinsics.throwUninitializedPropertyAccessException("editPassword");
        }
        if (TextUtils.isEmpty(String.valueOf(mryEditText.getText()))) {
            ToastUtils.show((CharSequence) LocaleController.getString("text_password_not_empty", R.string.text_password_not_empty));
            return;
        }
        TLRPCWallet.TL_paymentTrans req = new TLRPCWallet.TL_paymentTrans();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("businessKey", "password_check_login");
        Map map = jsonObject;
        MryEditText mryEditText2 = this.editPassword;
        if (mryEditText2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("editPassword");
        }
        map.put("currPwdHash", AesUtils.encryptToBase64(String.valueOf(mryEditText2.getText())));
        jsonObject.put("newPasswordHash", "");
        jsonObject.put("userName", this.userName);
        req.data.data = jsonObject.toJSONString();
        Log.e(BuildConfig.BUILD_TYPE, "request===" + jsonObject.toJSONString());
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoPasswordCheckDialog$checkPassword$1(this), 10);
    }
}
