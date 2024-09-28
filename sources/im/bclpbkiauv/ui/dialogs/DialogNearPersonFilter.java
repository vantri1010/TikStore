package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import im.bclpbkiauv.messenger.R;

public class DialogNearPersonFilter extends Dialog {
    public DialogNearPersonFilter(Activity context) {
        super(context, R.style.commondialog);
        setContentView(LayoutInflater.from(getContext()).inflate(R.layout.dialog_near_person_filter, (ViewGroup) null));
        Display d = context.getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(80);
        lp.width = d.getWidth();
        window.setAttributes(lp);
        setCancelable(true);
    }

    public DialogNearPersonFilter(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DialogNearPersonFilter(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
