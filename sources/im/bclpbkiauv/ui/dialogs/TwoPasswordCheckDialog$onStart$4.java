package im.bclpbkiauv.ui.dialogs;

import android.text.TextUtils;
import android.view.View;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hviews.MryEditText;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, k = 3, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
final class TwoPasswordCheckDialog$onStart$4 implements View.OnClickListener {
    final /* synthetic */ MryRoundButton $btnOk;
    final /* synthetic */ TwoPasswordCheckDialog this$0;

    TwoPasswordCheckDialog$onStart$4(TwoPasswordCheckDialog twoPasswordCheckDialog, MryRoundButton mryRoundButton) {
        this.this$0 = twoPasswordCheckDialog;
        this.$btnOk = mryRoundButton;
    }

    public final void onClick(View it) {
        MryRoundButton mryRoundButton = this.$btnOk;
        if (mryRoundButton == null) {
            Intrinsics.throwNpe();
        }
        if (mryRoundButton.isEnabled()) {
            MryEditText editPassword = this.this$0.getEditPassword();
            if (editPassword == null) {
                Intrinsics.throwNpe();
            }
            if (TextUtils.isEmpty(String.valueOf(editPassword.getText()))) {
                ToastUtils.show((CharSequence) LocaleController.getString("text_password_not_empty", R.string.text_password_not_empty));
            } else {
                this.this$0.checkPassword();
            }
        }
    }
}
