package im.bclpbkiauv.ui.wallet.utils;

import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil;

public class ExceptionUtils {
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean handleCommonError(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = 0
            r2 = 1
            switch(r0) {
                case -1766454629: goto L_0x0046;
                case 21821413: goto L_0x003c;
                case 746324468: goto L_0x0032;
                case 1077990923: goto L_0x0028;
                case 1335086637: goto L_0x001e;
                case 1554846384: goto L_0x0014;
                case 1688152246: goto L_0x000a;
                default: goto L_0x0009;
            }
        L_0x0009:
            goto L_0x0050
        L_0x000a:
            java.lang.String r0 = "SYSTEM_ERROR_ACCOUNT_EXCEPTION_CODE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 4
            goto L_0x0051
        L_0x0014:
            java.lang.String r0 = "ACCOUNT_HAS_BEEN_FROZEN_CODE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 3
            goto L_0x0051
        L_0x001e:
            java.lang.String r0 = "ERROR_ILLEGAL_CODE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 2
            goto L_0x0051
        L_0x0028:
            java.lang.String r0 = "ERROR_USER_IS_NOT_NULL"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 1
            goto L_0x0051
        L_0x0032:
            java.lang.String r0 = "SYSTEM_ERROR_CODE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 6
            goto L_0x0051
        L_0x003c:
            java.lang.String r0 = "USER_INFONNOT_CODE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 0
            goto L_0x0051
        L_0x0046:
            java.lang.String r0 = "ERROR_PHONE_NOT_NULL"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 5
            goto L_0x0051
        L_0x0050:
            r0 = -1
        L_0x0051:
            switch(r0) {
                case 0: goto L_0x0096;
                case 1: goto L_0x0089;
                case 2: goto L_0x007c;
                case 3: goto L_0x006f;
                case 4: goto L_0x0062;
                case 5: goto L_0x0055;
                case 6: goto L_0x0055;
                default: goto L_0x0054;
            }
        L_0x0054:
            return r1
        L_0x0055:
            r0 = 2131694118(0x7f0f1226, float:1.9017384E38)
            java.lang.String r1 = "SystemIsBusyAndTryAgainLater"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        L_0x0062:
            r0 = 2131689473(0x7f0f0001, float:1.9007962E38)
            java.lang.String r1 = "AbnormalAccountInformation"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        L_0x006f:
            r0 = 2131689570(0x7f0f0062, float:1.900816E38)
            java.lang.String r1 = "AccountHadBeenForzen"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        L_0x007c:
            r0 = 2131691625(0x7f0f0869, float:1.9012327E38)
            java.lang.String r1 = "IllegalOperation"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        L_0x0089:
            r0 = 2131694570(0x7f0f13ea, float:1.90183E38)
            java.lang.String r1 = "UserNotNull"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        L_0x0096:
            r0 = 2131690772(0x7f0f0514, float:1.9010597E38)
            java.lang.String r1 = "CurrentUserNotOpenedWalletAccount"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r0)
            im.bclpbkiauv.ui.components.toast.ToastUtils.show((java.lang.CharSequence) r0)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.wallet.utils.ExceptionUtils.handleCommonError(java.lang.String):boolean");
    }

    public static void handleCreateAccountError(String error) {
        if (!handleCommonError(error)) {
            char c = 65535;
            if (error.hashCode() == 1260306429 && error.equals("ERROR_ACCOUNT_SYNCHRONIZED")) {
                c = 0;
            }
            if (c == 0) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.WalletAccountCreated));
            }
        }
    }

    public static void handleGetAccountInfoError(String error) {
        if (!handleCommonError(error)) {
            char c = 65535;
            if (error.hashCode() == 1260306429 && error.equals("ERROR_ACCOUNT_SYNCHRONIZED")) {
                c = 0;
            }
            if (c == 0) {
                ToastUtils.show((CharSequence) LocaleController.getString(R.string.WalletAccountCreated));
            }
        }
    }

    public static void handlePaymentPasswordException(String ex) {
        if (!handleCommonError(ex)) {
            char c = 65535;
            switch (ex.hashCode()) {
                case -169668534:
                    if (ex.equals("ERROR_CONFIRM_PAY_PASSWORD_NOT_NULL")) {
                        c = 1;
                        break;
                    }
                    break;
                case -151239405:
                    if (ex.equals("SMS_CODE_NULL")) {
                        c = 5;
                        break;
                    }
                    break;
                case 930821507:
                    if (ex.equals("TYPE_IS_NOT_NULL")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1420798828:
                    if (ex.equals("SAFETY_CODE_NULL")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1730358686:
                    if (ex.equals("ERROR_NEW_PASSWORD_IS_INCONSISTENT")) {
                        c = 2;
                        break;
                    }
                    break;
                case 2000358825:
                    if (ex.equals("ERROR_PAY_PASSWORD_NOT_NULL")) {
                        c = 0;
                        break;
                    }
                    break;
                case 2140948840:
                    if (ex.equals("ERROR_OLD_PASSWORD_NOT_NULL")) {
                        c = 6;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.PayPasswordNotNull));
                    return;
                case 1:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.ComfirmPayPasswordNotNull));
                    return;
                case 2:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.NewPasswordInconsistent));
                    return;
                case 3:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.TypeCannotBeEmpty));
                    return;
                case 4:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.SecurityCodeNotNull));
                    return;
                case 5:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.VertificationCodeNotNull));
                    return;
                case 6:
                    ToastUtils.show((CharSequence) LocaleController.getString(R.string.OldPasswordNotNull));
                    return;
                default:
                    ToastUtils.show((CharSequence) WalletErrorUtil.getErrorDescription(ex));
                    return;
            }
        }
    }

    public static void handlePayChannelException(String ex) {
        if (!handleCommonError(ex)) {
            ToastUtils.show((CharSequence) WalletErrorUtil.getErrorDescription(ex));
        }
    }

    public static void handleWithdrawException(String ex) {
        if (!handleCommonError(ex)) {
            ToastUtils.show((CharSequence) WalletErrorUtil.getErrorDescription(ex));
        }
    }
}
