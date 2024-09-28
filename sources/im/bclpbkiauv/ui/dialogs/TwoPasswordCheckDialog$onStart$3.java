package im.bclpbkiauv.ui.dialogs;

import android.text.Editable;
import android.view.View;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, k = 3, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
final class TwoPasswordCheckDialog$onStart$3 implements View.OnClickListener {
    final /* synthetic */ TwoPasswordCheckDialog this$0;

    TwoPasswordCheckDialog$onStart$3(TwoPasswordCheckDialog twoPasswordCheckDialog) {
        this.this$0 = twoPasswordCheckDialog;
    }

    public final void onClick(View it) {
        Editable text = this.this$0.getEditPassword().getText();
        if (text == null) {
            Intrinsics.throwNpe();
        }
        text.clear();
    }
}
