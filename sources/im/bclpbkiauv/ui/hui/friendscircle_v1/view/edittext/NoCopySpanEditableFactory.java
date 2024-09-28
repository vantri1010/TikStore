package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext;

import android.text.Editable;
import android.text.NoCopySpan;
import android.text.SpannableStringBuilder;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\u0018\u00002\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u0018\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003X\u0004¢\u0006\u0004\n\u0002\u0010\u0006¨\u0006\u000b"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/NoCopySpanEditableFactory;", "Landroid/text/Editable$Factory;", "spans", "", "Landroid/text/NoCopySpan;", "([Landroid/text/NoCopySpan;)V", "[Landroid/text/NoCopySpan;", "newEditable", "Landroid/text/Editable;", "source", "", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: NoCopySpanEditableFactory.kt */
public final class NoCopySpanEditableFactory extends Editable.Factory {
    private final NoCopySpan[] spans;

    public NoCopySpanEditableFactory(NoCopySpan... spans2) {
        Intrinsics.checkParameterIsNotNull(spans2, "spans");
        this.spans = spans2;
    }

    public Editable newEditable(CharSequence source) {
        Intrinsics.checkParameterIsNotNull(source, "source");
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(source);
        SpannableStringBuilder $this$apply = valueOf;
        for (NoCopySpan it : this.spans) {
            $this$apply.setSpan(it, 0, source.length(), 18);
        }
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "SpannableStringBuilder.v…)\n            }\n        }");
        return valueOf;
    }
}
