package im.bclpbkiauv.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import im.bclpbkiauv.messenger.AndroidUtilities;

/* renamed from: im.bclpbkiauv.ui.-$$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY implements TextView.OnEditorActionListener {
    public static final /* synthetic */ $$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY INSTANCE = new $$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY();

    private /* synthetic */ $$Lambda$ThemeActivity$CtIAv0mVCQoiAull5_xinkqAxkY() {
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AndroidUtilities.hideKeyboard(textView);
    }
}
