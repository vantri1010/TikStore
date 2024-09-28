package im.bclpbkiauv.ui.hui.mine;

import android.view.KeyEvent;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;

/* renamed from: im.bclpbkiauv.ui.hui.mine.-$$Lambda$MryThemeActivity$vAtJX85PycTuRPic_gpTwtSZpUg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MryThemeActivity$vAtJX85PycTuRPic_gpTwtSZpUg implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$MryThemeActivity$vAtJX85PycTuRPic_gpTwtSZpUg INSTANCE = new $$Lambda$MryThemeActivity$vAtJX85PycTuRPic_gpTwtSZpUg();

    private /* synthetic */ $$Lambda$MryThemeActivity$vAtJX85PycTuRPic_gpTwtSZpUg() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
