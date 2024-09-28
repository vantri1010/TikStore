package im.bclpbkiauv.ui.components;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.hviews.dialogs.XDialog;

public class XDiloagCreate {
    public static XDialog.Builder createSimpleAlert(Context context, String title, String text) {
        if (text == null) {
            return null;
        }
        XDialog.Builder builder = new XDialog.Builder(context);
        builder.setTitle(title == null ? LocaleController.getString("AppName", R.string.AppName) : title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (DialogInterface.OnClickListener) null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        Dialog dialog = createSimpleAlert(baseFragment.getParentActivity(), title, text).create();
        baseFragment.showDialog(dialog);
        return dialog;
    }
}
