package im.bclpbkiauv.ui.dialogs;

import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.serenegiant.uvccamera.BuildConfig;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCWallet;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\n¢\u0006\u0002\b\u0006"}, d2 = {"<anonymous>", "", "response", "Lim/bclpbkiauv/tgnet/TLObject;", "error", "Lim/bclpbkiauv/tgnet/TLRPC$TL_error;", "run"}, k = 3, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
final class TwoPasswordCheckDialog$checkPassword$1 implements RequestDelegate {
    final /* synthetic */ TwoPasswordCheckDialog this$0;

    TwoPasswordCheckDialog$checkPassword$1(TwoPasswordCheckDialog twoPasswordCheckDialog) {
        this.this$0 = twoPasswordCheckDialog;
    }

    public final void run(final TLObject response, final TLRPC.TL_error error) {
        Intrinsics.checkParameterIsNotNull(response, "response");
        AndroidUtilities.runOnUIThread(new Runnable(this) {
            final /* synthetic */ TwoPasswordCheckDialog$checkPassword$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void run() {
                if (error == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("password_check_login response===");
                    TLObject tLObject = response;
                    if (tLObject != null) {
                        sb.append(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) tLObject).data));
                        Log.e(BuildConfig.BUILD_TYPE, sb.toString());
                        this.this$0.this$0.getProgressDialog().dismiss();
                        TLObject tLObject2 = response;
                        if (tLObject2 instanceof TLRPCWallet.TL_paymentTransResult) {
                            Object parse = JSONObject.parse(JSONObject.toJSONString(((TLRPCWallet.TL_paymentTransResult) tLObject2).data));
                            if (parse != null) {
                                Object parse2 = JSONObject.parse(((JSONObject) parse).getString("data"));
                                if (parse2 != null) {
                                    JSONObject resp = (JSONObject) parse2;
                                    Integer integer = resp.getInteger("code");
                                    if (integer != null && integer.intValue() == 403) {
                                        ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
                                        return;
                                    }
                                    Integer integer2 = resp.getInteger("code");
                                    if (integer2 != null && integer2.intValue() == 404) {
                                        ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
                                        return;
                                    }
                                    Integer integer3 = resp.getInteger("code");
                                    if (integer3 != null && integer3.intValue() == 408) {
                                        ToastUtils.show((CharSequence) LocaleController.getString("text_password_error_limit", R.string.text_password_error_limit));
                                        return;
                                    }
                                    Integer integer4 = resp.getInteger("code");
                                    if (integer4 != null && integer4.intValue() == 0) {
                                        this.this$0.this$0.dismiss();
                                        this.this$0.this$0.getOnPasswordCheckListener().onPasswordCheck();
                                        return;
                                    }
                                    return;
                                }
                                throw new TypeCastException("null cannot be cast to non-null type com.alibaba.fastjson.JSONObject");
                            }
                            throw new TypeCastException("null cannot be cast to non-null type com.alibaba.fastjson.JSONObject");
                        }
                        return;
                    }
                    throw new TypeCastException("null cannot be cast to non-null type im.bclpbkiauv.tgnet.TLRPCWallet.TL_paymentTransResult");
                }
                Log.e(BuildConfig.BUILD_TYPE, "password_check_login error===" + JSONObject.toJSONString(error));
                this.this$0.this$0.getProgressDialog().dismiss();
                String str = error.text;
                Intrinsics.checkExpressionValueIsNotNull(str, "error.text");
                if (StringsKt.contains$default((CharSequence) str, (CharSequence) "INVALID_PASSWORD", false, 2, (Object) null)) {
                    ToastUtils.show((CharSequence) LocaleController.getString("LoginPwdError", R.string.LoginPwdError));
                    return;
                }
                String str2 = error.text;
                Intrinsics.checkExpressionValueIsNotNull(str2, "error.text");
                if (StringsKt.contains$default((CharSequence) str2, (CharSequence) "INTERNAL_ERROR", false, 2, (Object) null)) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_system_error", R.string.text_system_error));
                    return;
                }
                String str3 = error.text;
                Intrinsics.checkExpressionValueIsNotNull(str3, "error.text");
                if (StringsKt.contains$default((CharSequence) str3, (CharSequence) "MANY_PASSWORD_ERROR", false, 2, (Object) null)) {
                    ToastUtils.show((CharSequence) LocaleController.getString("text_password_error_limit", R.string.text_password_error_limit));
                }
            }
        });
    }
}
