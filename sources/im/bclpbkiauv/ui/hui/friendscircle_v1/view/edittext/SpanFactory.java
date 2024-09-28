package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext;

import android.text.Spannable;
import android.text.SpannableString;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J'\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0012\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00010\b\"\u00020\u0001¢\u0006\u0002\u0010\t¨\u0006\n"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/SpanFactory;", "", "()V", "newSpannable", "Landroid/text/Spannable;", "source", "", "spans", "", "(Ljava/lang/CharSequence;[Ljava/lang/Object;)Landroid/text/Spannable;", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: SpanFactory.kt */
public final class SpanFactory {
    public static final SpanFactory INSTANCE = new SpanFactory();

    private SpanFactory() {
    }

    public final Spannable newSpannable(CharSequence source, Object... spans) {
        Intrinsics.checkParameterIsNotNull(source, "source");
        Intrinsics.checkParameterIsNotNull(spans, "spans");
        SpannableString valueOf = SpannableString.valueOf(source);
        SpannableString $this$apply = valueOf;
        for (Object element$iv : spans) {
            $this$apply.setSpan(element$iv, 0, $this$apply.length(), 33);
        }
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "SpannableString.valueOf(…)\n            }\n        }");
        return valueOf;
    }
}
