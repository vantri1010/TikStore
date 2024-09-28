package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method;

import android.text.Spannable;
import android.widget.EditText;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.AtUserSpan;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016R\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0001X\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007¨\u0006\u0010"}, d2 = {"Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/MethodContext;", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/Method;", "()V", "method", "getMethod", "()Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/Method;", "setMethod", "(Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/method/Method;)V", "init", "", "editText", "Landroid/widget/EditText;", "newSpannable", "Landroid/text/Spannable;", "atUserSpan", "Lim/bclpbkiauv/ui/hui/friendscircle_v1/view/edittext/span/AtUserSpan;", "HMessagesPrj_prodRelease"}, k = 1, mv = {1, 1, 16})
/* compiled from: MethodContext.kt */
public final class MethodContext implements Method {
    private Method method;

    public final Method getMethod() {
        return this.method;
    }

    public final void setMethod(Method method2) {
        this.method = method2;
    }

    public void init(EditText editText) {
        Intrinsics.checkParameterIsNotNull(editText, "editText");
        Method method2 = this.method;
        if (method2 != null) {
            method2.init(editText);
        }
    }

    public Spannable newSpannable(AtUserSpan atUserSpan) {
        Spannable newSpannable;
        Intrinsics.checkParameterIsNotNull(atUserSpan, "atUserSpan");
        Method method2 = this.method;
        if (method2 != null && (newSpannable = method2.newSpannable(atUserSpan)) != null) {
            return newSpannable;
        }
        throw new NullPointerException("method: null");
    }
}
