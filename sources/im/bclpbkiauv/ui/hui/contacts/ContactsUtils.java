package im.bclpbkiauv.ui.hui.contacts;

import android.text.TextUtils;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPC;

public class ContactsUtils {
    public static String getAboutContactsErrText(TLRPC.TL_error error) {
        if (error == null || TextUtils.isEmpty(error.text)) {
            return LocaleController.getString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater);
        }
        String str = error.text;
        char c = 65535;
        switch (str.hashCode()) {
            case -435956621:
                if (str.equals("USER_HAS_BEEN_BLOCK")) {
                    c = 1;
                    break;
                }
                break;
            case 100175290:
                if (str.equals("TOO_MANY_REQUEST")) {
                    c = 3;
                    break;
                }
                break;
            case 721996124:
                if (str.equals("CONTACT_HAS_ADDED")) {
                    c = 2;
                    break;
                }
                break;
            case 1986852397:
                if (str.equals("CAN_NOT_BE_SELF")) {
                    c = 0;
                    break;
                }
                break;
        }
        if (c == 0) {
            return LocaleController.getString("CantAddYourSelf", R.string.CantAddYourSelf);
        }
        if (c == 1) {
            return LocaleController.getString("HasBeenBlocked", R.string.HasBeenBlocked);
        }
        if (c == 2) {
            return LocaleController.getString("AlreadyYourContact", R.string.AlreadyYourContact);
        }
        if (c != 3) {
            return LocaleController.getString("SystemIsBusyAndTryAgainLater", R.string.SystemIsBusyAndTryAgainLater);
        }
        return LocaleController.getString("OperationTooMany", R.string.OperationTooMany);
    }
}
