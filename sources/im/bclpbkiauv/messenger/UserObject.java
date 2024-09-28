package im.bclpbkiauv.messenger;

import android.text.TextUtils;
import im.bclpbkiauv.tgnet.TLRPC;

public class UserObject {
    public static boolean isDeleted(TLRPC.User user) {
        return user == null || (user instanceof TLRPC.TL_userDeleted_old2) || (user instanceof TLRPC.TL_userEmpty) || user.deleted;
    }

    public static boolean isContact(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    @Deprecated
    public static boolean isTempConversation(TLRPC.User user) {
        return user != null && !(user instanceof TLRPC.TL_userContact_old2) && !user.bot && !user.support && !user.verified && !user.self && !user.mutual_contact;
    }

    public static boolean isUserSelf(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userSelf_old3) || user.self);
    }

    public static String getUserName(TLRPC.User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        if (user.id == 777000) {
            user.first_name = BuildVars.SYSTEM_NAME;
            user.last_name = BuildVars.SYSTEM_NAME;
        }
        if (!TextUtils.isEmpty(user.username)) {
            return user.username;
        }
        return LocaleController.getString("UnKnown", R.string.UnKnown);
    }

    public static String getName(TLRPC.User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        if (user.id == 777000) {
            user.first_name = BuildVars.SYSTEM_NAME;
            user.last_name = BuildVars.SYSTEM_NAME;
        }
        if (!TextUtils.isEmpty(user.first_name)) {
            return user.first_name;
        }
        return LocaleController.getString("UnKnown", R.string.UnKnown);
    }

    public static String getName(TLRPC.User user, int len) {
        String name = getName(user);
        if (name.length() <= len) {
            return name;
        }
        String name2 = name.substring(0, len);
        return name2 + "...";
    }

    public static String getFirstName(TLRPC.User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(TLRPC.User user, boolean allowShort) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String name = user.first_name;
        if (TextUtils.isEmpty(name)) {
            name = user.last_name;
        }
        return !TextUtils.isEmpty(name) ? name : LocaleController.getString("HiddenName", R.string.HiddenName);
    }

    public static String getFullName(TLRPC.User user) {
        String result;
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", R.string.HiddenName);
        }
        String firstName = user.first_name;
        if (firstName != null) {
            firstName = firstName.trim();
        }
        String lastName = user.last_name;
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (!TextUtils.isEmpty(lastName)) {
            result = lastName;
        } else if (!TextUtils.isEmpty(firstName)) {
            result = firstName.trim();
        } else {
            result = LocaleController.getString("UnKnown", R.string.UnKnown);
        }
        return result.toString();
    }
}
