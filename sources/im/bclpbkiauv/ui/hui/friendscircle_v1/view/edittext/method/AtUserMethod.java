package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method;

import android.text.Spannable;
import android.widget.EditText;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.NoCopySpanEditableFactory;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.SpanFactory;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.AtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.DataBindingSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.watcher.SelectionSpanWatcher;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016¨\u0006\u000b"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/AtUserMethod;", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/Method;", "()V", "init", "", "editText", "Landroid/widget/EditText;", "newSpannable", "Landroid/text/Spannable;", "atUserSpan", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/span/AtUserSpan;", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: AtUserMethod.kt */
public final class AtUserMethod implements Method {
    public static final AtUserMethod INSTANCE = new AtUserMethod();

    private AtUserMethod() {
    }

    public void init(EditText editText) {
        Intrinsics.checkParameterIsNotNull(editText, "editText");
        editText.setText((CharSequence) null);
        editText.setEditableFactory(new NoCopySpanEditableFactory(new SelectionSpanWatcher(Reflection.getOrCreateKotlinClass(DataBindingSpan.class))));
        editText.setOnKeyListener(AtUserMethod$init$1.INSTANCE);
    }

    public Spannable newSpannable(AtUserSpan atUserSpan) {
        Intrinsics.checkParameterIsNotNull(atUserSpan, "atUserSpan");
        return SpanFactory.INSTANCE.newSpannable(atUserSpan.getSpannedName(), atUserSpan);
    }
}
