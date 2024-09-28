package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.watcher;

import android.text.Spannable;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.DirtySpan;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003¢\u0006\u0002\u0010\u0006J8\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\rH\u0016R\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/watcher/DirtySpanWatcher;", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/watcher/SpanWatcherAdapter;", "removePredicate", "Lkotlin/Function1;", "", "", "(Lkotlin/jvm/functions/Function1;)V", "onSpanChanged", "", "text", "Landroid/text/Spannable;", "what", "ostart", "", "oend", "nstart", "nend", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: DirtySpanWatcher.kt */
public final class DirtySpanWatcher extends SpanWatcherAdapter {
    private final Function1<Object, Boolean> removePredicate;

    public DirtySpanWatcher(Function1<Object, Boolean> removePredicate2) {
        Intrinsics.checkParameterIsNotNull(removePredicate2, "removePredicate");
        this.removePredicate = removePredicate2;
    }

    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        Spannable spannable = text;
        Object obj = what;
        Intrinsics.checkParameterIsNotNull(spannable, "text");
        Intrinsics.checkParameterIsNotNull(obj, "what");
        if (!(obj instanceof DirtySpan) || !((DirtySpan) obj).isDirty(spannable)) {
            return;
        }
        Object[] $this$filter$iv = spannable.getSpans(text.getSpanStart(what), text.getSpanEnd(what), Object.class);
        Intrinsics.checkExpressionValueIsNotNull($this$filter$iv, "text.getSpans(spanStart, spanEnd, Any::class.java)");
        Collection destination$iv$iv = new ArrayList();
        Object[] $this$filterTo$iv$iv = $this$filter$iv;
        int length = $this$filterTo$iv$iv.length;
        int i = 0;
        while (i < length) {
            Object element$iv$iv = $this$filterTo$iv$iv[i];
            Object it = element$iv$iv;
            Function1<Object, Boolean> function1 = this.removePredicate;
            Intrinsics.checkExpressionValueIsNotNull(it, "it");
            if (function1.invoke(it).booleanValue()) {
                destination$iv$iv.add(element$iv$iv);
            }
            i++;
            Object obj2 = what;
        }
        for (Object element$iv : (List) destination$iv$iv) {
            spannable.removeSpan(element$iv);
        }
    }
}
