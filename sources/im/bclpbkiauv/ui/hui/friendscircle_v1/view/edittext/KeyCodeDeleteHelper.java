package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext;

import android.text.Selection;
import android.text.Spannable;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.DataBindingSpan;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/KeyCodeDeleteHelper;", "", "()V", "onDelDown", "", "text", "Landroid/text/Spannable;", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: KeyCodeDeleteHelper.kt */
public final class KeyCodeDeleteHelper {
    public static final KeyCodeDeleteHelper INSTANCE = new KeyCodeDeleteHelper();

    private KeyCodeDeleteHelper() {
    }

    public final boolean onDelDown(Spannable text) {
        DataBindingSpan element$iv;
        Intrinsics.checkParameterIsNotNull(text, "text");
        int selectionStart = Selection.getSelectionStart(text);
        int selectionEnd = Selection.getSelectionEnd(text);
        Object[] $this$firstOrNull$iv = text.getSpans(selectionStart, selectionEnd, DataBindingSpan.class);
        Intrinsics.checkExpressionValueIsNotNull($this$firstOrNull$iv, "text.getSpans(selectionS…aBindingSpan::class.java)");
        int length = $this$firstOrNull$iv.length;
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= length) {
                element$iv = null;
                break;
            }
            element$iv = $this$firstOrNull$iv[i];
            if (text.getSpanEnd((DataBindingSpan) element$iv) == selectionStart) {
                break;
            }
            i++;
        }
        DataBindingSpan dataBindingSpan = element$iv;
        if (dataBindingSpan == null) {
            return false;
        }
        DataBindingSpan $this$run = dataBindingSpan;
        if (selectionStart == selectionEnd) {
            z = true;
        }
        boolean z2 = z;
        Selection.setSelection(text, text.getSpanStart($this$run), text.getSpanEnd($this$run));
        return z;
    }
}
