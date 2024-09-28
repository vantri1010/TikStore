package im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.method;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.core.app.NotificationCompat;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.KeyCodeDeleteHelper;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u000e\u0010\u0007\u001a\n \u0004*\u0004\u0018\u00010\b0\bH\n¢\u0006\u0002\b\t"}, d2 = {"<anonymous>", "", "v", "Landroid/view/View;", "kotlin.jvm.PlatformType", "keyCode", "", "event", "Landroid/view/KeyEvent;", "onKey"}, k = 3, mv = {1, 1, 16})
/* compiled from: AtUserMethod.kt */
final class AtUserMethod$init$1 implements View.OnKeyListener {
    public static final AtUserMethod$init$1 INSTANCE = new AtUserMethod$init$1();

    AtUserMethod$init$1() {
    }

    public final boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode != 67) {
            return false;
        }
        Intrinsics.checkExpressionValueIsNotNull(event, NotificationCompat.CATEGORY_EVENT);
        if (event.getAction() != 0) {
            return false;
        }
        KeyCodeDeleteHelper keyCodeDeleteHelper = KeyCodeDeleteHelper.INSTANCE;
        if (v != null) {
            Editable text = ((EditText) v).getText();
            Intrinsics.checkExpressionValueIsNotNull(text, "(v as EditText).text");
            return keyCodeDeleteHelper.onDelDown(text);
        }
        throw new TypeCastException("null cannot be cast to non-null type android.widget.EditText");
    }
}
