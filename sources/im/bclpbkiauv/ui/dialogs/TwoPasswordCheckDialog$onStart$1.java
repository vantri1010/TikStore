package im.bclpbkiauv.ui.dialogs;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import im.bclpbkiauv.ui.hviews.MryRoundButton;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000%\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0012\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016J*\u0010\u0006\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0016J*\u0010\f\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0016¨\u0006\u000e"}, d2 = {"im/bclpbkiauv/ui/dialogs/TwoPasswordCheckDialog$onStart$1", "Landroid/text/TextWatcher;", "afterTextChanged", "", "s", "Landroid/text/Editable;", "beforeTextChanged", "", "start", "", "count", "after", "onTextChanged", "before", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: TwoPasswordCheckDialog.kt */
public final class TwoPasswordCheckDialog$onStart$1 implements TextWatcher {
    final /* synthetic */ MryRoundButton $btnOk;
    final /* synthetic */ TwoPasswordCheckDialog this$0;

    TwoPasswordCheckDialog$onStart$1(TwoPasswordCheckDialog $outer, MryRoundButton $captured_local_variable$1) {
        this.this$0 = $outer;
        this.$btnOk = $captured_local_variable$1;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(s)) {
            this.this$0.getImgClear().setVisibility(0);
        } else {
            this.this$0.getImgClear().setVisibility(8);
        }
    }

    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s)) {
            MryRoundButton mryRoundButton = this.$btnOk;
            if (mryRoundButton == null) {
                Intrinsics.throwNpe();
            }
            mryRoundButton.setEnabled(true);
        }
    }
}
