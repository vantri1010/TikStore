package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.watcher;

import android.text.Selection;
import android.text.Spannable;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClass;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0003B\u0013\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005¢\u0006\u0002\u0010\u0006J8\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/watcher/SelectionSpanWatcher;", "T", "", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/watcher/SpanWatcherAdapter;", "kClass", "Lkotlin/reflect/KClass;", "(Lkotlin/reflect/KClass;)V", "selEnd", "", "selStart", "onSpanChanged", "", "text", "Landroid/text/Spannable;", "what", "ostart", "oend", "nstart", "nend", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: SelectionSpanWatcher.kt */
public final class SelectionSpanWatcher<T> extends SpanWatcherAdapter {
    private final KClass<T> kClass;
    private int selEnd;
    private int selStart;

    public SelectionSpanWatcher(KClass<T> kClass2) {
        Intrinsics.checkParameterIsNotNull(kClass2, "kClass");
        this.kClass = kClass2;
    }

    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(what, "what");
        if (what == Selection.SELECTION_END && this.selEnd != nstart) {
            this.selEnd = nstart;
            Object[] spans = text.getSpans(nstart, nend, JvmClassMappingKt.getJavaClass(this.kClass));
            Intrinsics.checkExpressionValueIsNotNull(spans, "text.getSpans(nstart, nend, kClass.java)");
            Object $this$run = ArraysKt.firstOrNull((T[]) spans);
            if ($this$run != null) {
                int spanStart = text.getSpanStart($this$run);
                int spanEnd = text.getSpanEnd($this$run);
                Selection.setSelection(text, Selection.getSelectionStart(text), Math.abs(this.selEnd - spanEnd) > Math.abs(this.selEnd - spanStart) ? spanStart : spanEnd);
            }
        }
        if (what == Selection.SELECTION_START && this.selStart != nstart) {
            this.selStart = nstart;
            Object[] spans2 = text.getSpans(nstart, nend, JvmClassMappingKt.getJavaClass(this.kClass));
            Intrinsics.checkExpressionValueIsNotNull(spans2, "text.getSpans(nstart, nend, kClass.java)");
            Object $this$run2 = ArraysKt.firstOrNull((T[]) spans2);
            if ($this$run2 != null) {
                int spanStart2 = text.getSpanStart($this$run2);
                int spanEnd2 = text.getSpanEnd($this$run2);
                Selection.setSelection(text, Math.abs(this.selStart - spanEnd2) > Math.abs(this.selStart - spanStart2) ? spanStart2 : spanEnd2, Selection.getSelectionEnd(text));
            }
        }
    }
}
