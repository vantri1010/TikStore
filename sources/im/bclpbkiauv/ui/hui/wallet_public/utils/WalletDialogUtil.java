package im.bclpbkiauv.ui.hui.wallet_public.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.fragments.BaseFmts;

public class WalletDialogUtil {
    public static WalletDialog showConfirmBtnWalletDialog(Object host, CharSequence msg) {
        return showConfirmBtnWalletDialog(host, msg, true);
    }

    public static WalletDialog showConfirmBtnWalletDialog(Object host, CharSequence msg, boolean cancelable) {
        return showConfirmBtnWalletDialog(host, msg, cancelable, (DialogInterface.OnClickListener) null, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showConfirmBtnWalletDialog(Object host, CharSequence msg, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener) {
        return showConfirmBtnWalletDialog(host, msg, cancelable, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showConfirmBtnWalletDialog(Object host, CharSequence msg, boolean cancelable, DialogInterface.OnDismissListener onDismissListener) {
        return showConfirmBtnWalletDialog(host, msg, cancelable, (DialogInterface.OnClickListener) null, onDismissListener);
    }

    public static WalletDialog showConfirmBtnWalletDialog(Object host, CharSequence msg, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showSingleBtnWalletDialog(host, "", msg, LocaleController.getString("Confirm", R.string.Confirm), cancelable, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showConfirmBtnWalletDialog(Object host, String title, CharSequence msg, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showSingleBtnWalletDialog(host, title, msg, LocaleController.getString("Confirm", R.string.Confirm), cancelable, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, CharSequence msg, DialogInterface.OnClickListener onConfirmClickListener) {
        return showSingleBtnWalletDialog(host, msg, LocaleController.getString(R.string.OK), true, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, CharSequence msg, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener) {
        return showSingleBtnWalletDialog(host, msg, LocaleController.getString(R.string.OK), cancelable, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, CharSequence msg, String buttonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener) {
        return showSingleBtnWalletDialog(host, msg, buttonText, cancelable, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, CharSequence msg, String buttonText, boolean cancelable, DialogInterface.OnDismissListener onDismissListener) {
        return showSingleBtnWalletDialog(host, msg, buttonText, cancelable, (DialogInterface.OnClickListener) null, onDismissListener);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, CharSequence msg, String buttonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, "", msg, (String) null, buttonText, cancelable, (DialogInterface.OnClickListener) null, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showSingleBtnWalletDialog(Object host, String title, CharSequence msg, String buttonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, title, msg, (String) null, buttonText, cancelable, (DialogInterface.OnClickListener) null, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showRedpkgTransDialog(Object host, String title, String conent) {
        return null;
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, DialogInterface.OnClickListener onConfirmClickListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, true, (DialogInterface.OnClickListener) null, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, cancelable, (DialogInterface.OnClickListener) null, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showWalletDialog(Object host, String title, CharSequence msg, String confirmButtonText, DialogInterface.OnClickListener onConfirmClickListener) {
        return showWalletDialog(host, title, msg, (String) null, confirmButtonText, true, (DialogInterface.OnClickListener) null, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showWalletDialog(Object host, String title, CharSequence msg, String confirmButtonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener) {
        return showWalletDialog(host, title, msg, (String) null, confirmButtonText, cancelable, (DialogInterface.OnClickListener) null, onConfirmClickListener, (DialogInterface.OnDismissListener) null);
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, true, (DialogInterface.OnClickListener) null, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, boolean cancelable, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, cancelable, (DialogInterface.OnClickListener) null, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, DialogInterface.OnClickListener onCancelClickListener, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, true, onCancelClickListener, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showWalletDialog(Object host, CharSequence msg, String confirmButtonText, boolean cancelable, DialogInterface.OnClickListener onCancelClickListener, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        return showWalletDialog(host, (String) null, msg, (String) null, confirmButtonText, cancelable, onCancelClickListener, onConfirmClickListener, onDismissListener);
    }

    public static WalletDialog showWalletDialog(Object host, String title, CharSequence msg, String cancelButtonText, String confirmButtonText, DialogInterface.OnClickListener onCancelClickListener, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        WalletDialog walletDialog = showWalletDialog(host, title, msg, cancelButtonText, confirmButtonText, true, onCancelClickListener, onConfirmClickListener, onDismissListener);
        if (walletDialog != null) {
            if (walletDialog.getNegativeButton() != null) {
                walletDialog.getNegativeButton().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            }
            if (walletDialog.getPositiveButton() != null) {
                walletDialog.getPositiveButton().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            }
        }
        return walletDialog;
    }

    public static WalletDialog showWalletDialog(Object host, String title, CharSequence msg, String cancelButtonText, String confirmButtonText, boolean cancelable, DialogInterface.OnClickListener onCancelClickListener, DialogInterface.OnClickListener onConfirmClickListener, DialogInterface.OnDismissListener onDismissListener) {
        if (!(host instanceof BaseFragment) && !(host instanceof BaseFmts) && !(host instanceof Activity)) {
            return null;
        }
        Context context = null;
        if (host instanceof BaseFragment) {
            context = ((BaseFragment) host).getParentActivity();
        } else if (host instanceof BaseFmts) {
            context = ((BaseFmts) host).getParentActivity();
        }
        if (context == null) {
            return null;
        }
        WalletDialog dialog = new WalletDialog(context);
        dialog.setCancelable(false);
        if (title == null) {
            title = LocaleController.getString("AppName", R.string.AppName);
        }
        dialog.setTitle(title);
        dialog.setMessage(msg);
        if (cancelButtonText != null) {
            dialog.setNegativeButton(cancelButtonText, onCancelClickListener);
        }
        if (confirmButtonText == null) {
            confirmButtonText = LocaleController.getString("OK", R.string.OK);
        }
        dialog.setPositiveButton(confirmButtonText, onConfirmClickListener);
        if (host instanceof BaseFragment) {
            ((BaseFragment) host).showDialog(dialog, onDismissListener);
        } else if (host instanceof BaseFmts) {
            ((BaseFmts) host).showDialog(dialog, onDismissListener);
        }
        if (!cancelable) {
            dialog.setCancelable(false);
        }
        return dialog;
    }
}
