package im.bclpbkiauv.ui.dialogs;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import im.bclpbkiauv.messenger.R;
import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, k = 3, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
final class TwoPasswordCheckDialog$onStart$2 implements View.OnClickListener {
    final /* synthetic */ TwoPasswordCheckDialog this$0;

    TwoPasswordCheckDialog$onStart$2(TwoPasswordCheckDialog twoPasswordCheckDialog) {
        this.this$0 = twoPasswordCheckDialog;
    }

    public final void onClick(View it) {
        TwoPasswordCheckDialog twoPasswordCheckDialog = this.this$0;
        twoPasswordCheckDialog.setEtPwdIsHide(!twoPasswordCheckDialog.getEtPwdIsHide());
        if (this.this$0.getEtPwdIsHide()) {
            this.this$0.getImgShowPassword().setImageResource(R.mipmap.eye_close);
            this.this$0.getEditPassword().setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            this.this$0.getImgShowPassword().setImageResource(R.mipmap.eye_open);
            this.this$0.getEditPassword().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        this.this$0.getEditPassword().setSelectionEnd();
    }
}
