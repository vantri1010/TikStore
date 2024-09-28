package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.watcher;

import android.text.SpanWatcher;
import android.text.Spannable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\b\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J(\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0016J8\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\nH\u0016J(\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0016¨\u0006\u0012"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/watcher/SpanWatcherAdapter;", "Landroid/text/SpanWatcher;", "()V", "onSpanAdded", "", "text", "Landroid/text/Spannable;", "what", "", "start", "", "end", "onSpanChanged", "ostart", "oend", "nstart", "nend", "onSpanRemoved", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: SpanWatcherAdapter.kt */
public class SpanWatcherAdapter implements SpanWatcher {
    public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(what, "what");
    }

    public void onSpanRemoved(Spannable text, Object what, int start, int end) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(what, "what");
    }

    public void onSpanAdded(Spannable text, Object what, int start, int end) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        Intrinsics.checkParameterIsNotNull(what, "what");
    }
}
