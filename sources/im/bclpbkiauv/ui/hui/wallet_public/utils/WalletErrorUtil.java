package im.bclpbkiauv.ui.hui.wallet_public.utils;

import android.content.DialogInterface;
import android.text.Html;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.actionbar.BaseFragment;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.toast.ToastUtils;
import im.bclpbkiauv.ui.constants.Constants;
import im.bclpbkiauv.ui.dialogs.WalletDialog;
import im.bclpbkiauv.ui.hui.mine.AboutAppActivity;
import im.bclpbkiauv.ui.utils.number.NumberUtil;

public class WalletErrorUtil implements Constants {
    public static void parseErrorToast(int errorMsgKey) {
        parseErrorToast(LocaleController.getString(errorMsgKey + "", errorMsgKey));
    }

    public static void parseErrorToast(String errorMsg) {
        ToastUtils.show((CharSequence) getErrorDescription(errorMsg));
    }

    public static void parseErrorToast(int prefixOfErrorMsgKey, String errorMsg) {
        parseErrorToast(LocaleController.getString(prefixOfErrorMsgKey + "", prefixOfErrorMsgKey), errorMsg);
    }

    public static void parseErrorToast(String prefixOfErrorMsg, String errorMsg) {
        ToastUtils.show((CharSequence) getErrorDescription(prefixOfErrorMsg, errorMsg));
    }

    public static WalletDialog parseErrorDialog(Object host, int errorMsgKey) {
        return parseErrorDialog(host, LocaleController.getString(errorMsgKey + "", errorMsgKey));
    }

    public static WalletDialog parseErrorDialog(Object host, int errorMsgKey, boolean cancelable) {
        return parseErrorDialog(host, LocaleController.getString(errorMsgKey + "", errorMsgKey), cancelable);
    }

    public static WalletDialog parseErrorDialog(Object host, String errorMsg) {
        return WalletDialogUtil.showConfirmBtnWalletDialog(host, getErrorDescription(errorMsg));
    }

    public static WalletDialog parseErrorDialog(Object host, String errorMsg, boolean cancelable) {
        return WalletDialogUtil.showConfirmBtnWalletDialog(host, getErrorDescription(errorMsg), cancelable);
    }

    public static WalletDialog parseErrorDialog(Object host, String errMsg, boolean cancel, String btnText, DialogInterface.OnClickListener listener) {
        return WalletDialogUtil.showSingleBtnWalletDialog(host, (CharSequence) getErrorDescription(errMsg), btnText, cancel, listener);
    }

    public static WalletDialog parseErrorDialog(Object host, int prefixOfErrorMsgKey, int errorMsgKey) {
        return parseErrorDialog(host, prefixOfErrorMsgKey, LocaleController.getString(errorMsgKey + "", errorMsgKey));
    }

    public static WalletDialog parseErrorDialog(Object host, int prefixOfErrorMsgKey, int errorMsgKey, boolean cancelable) {
        return parseErrorDialog(host, prefixOfErrorMsgKey, LocaleController.getString(errorMsgKey + "", errorMsgKey), cancelable);
    }

    public static WalletDialog parseErrorDialog(Object host, int prefixOfErrorMsgKey, String errorMsg) {
        return parseErrorDialog(host, LocaleController.getString(prefixOfErrorMsgKey + "", prefixOfErrorMsgKey), errorMsg);
    }

    public static WalletDialog parseErrorDialog(Object host, int prefixOfErrorMsgKey, String errorMsg, boolean cancelable) {
        return parseErrorDialog(host, LocaleController.getString(prefixOfErrorMsgKey + "", prefixOfErrorMsgKey), errorMsg, cancelable);
    }

    public static WalletDialog parseErrorDialog(Object host, String prefixOfErrorMsg, String errorMsg) {
        return WalletDialogUtil.showConfirmBtnWalletDialog(host, getErrorDescription(prefixOfErrorMsg, errorMsg), true);
    }

    public static WalletDialog parseErrorDialog(Object host, String prefixOfErrorMsg, String errorMsg, boolean cancelable) {
        return WalletDialogUtil.showConfirmBtnWalletDialog(host, getErrorDescription(prefixOfErrorMsg, errorMsg), cancelable);
    }

    public static String getErrorDescription(int errorMsgKey) {
        return getErrorDescription(LocaleController.getString(errorMsgKey + "", errorMsgKey));
    }

    public static String getErrorDescription(String errorMsg) {
        return getErrorDescription((String) null, errorMsg);
    }

    public static String getErrorDescription(int prefixOfErrorMsgKey, int errorMsgKey) {
        return getErrorDescription(prefixOfErrorMsgKey, LocaleController.getString(errorMsgKey + "", errorMsgKey));
    }

    public static String getErrorDescription(int prefixOfErrorMsgKey, String errorMsg) {
        return getErrorDescription(LocaleController.getString(prefixOfErrorMsgKey + "", prefixOfErrorMsgKey), errorMsg);
    }

    public static boolean parsePayPasswordErrorDialog(BaseFragment fragment, String errorMsg) {
        String confirmText;
        String content;
        if (fragment == null || fragment.getParentActivity() == null) {
            return false;
        }
        boolean tag = false;
        WalletDialog dialog = new WalletDialog(fragment.getParentActivity());
        if (errorMsg != null) {
            if (errorMsg.contains("FROZEN")) {
                tag = true;
                String[] numbers = NumberUtil.getNumbersFromStr(errorMsg);
                int count = 0;
                if (numbers.length == 2) {
                    count = Integer.valueOf(numbers[1]).intValue();
                }
                if (count > 0) {
                    content = LocaleController.getString(R.string.ErrorPayPasswordAndTryAgain) + "<br/><br/>" + String.format(LocaleController.getString(R.string.YouCanEnterTimesWithColor), new Object[]{Integer.valueOf(count)});
                    confirmText = LocaleController.getString(R.string.ForgetPassword);
                } else {
                    content = LocaleController.getString(R.string.TheAccountHasBeenFrozenWith24H);
                    confirmText = LocaleController.getString(R.string.ContactCustomerService);
                }
                dialog.setMessage(Html.fromHtml(content), true, true);
                dialog.setPositiveButton(confirmText, new DialogInterface.OnClickListener(count, fragment) {
                    private final /* synthetic */ int f$0;
                    private final /* synthetic */ BaseFragment f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        WalletErrorUtil.lambda$parsePayPasswordErrorDialog$0(this.f$0, this.f$1, dialogInterface, i);
                    }
                });
            } else if ("DAY_BAND_BANK_NUMBER_OVER_LIMIT".equals(errorMsg)) {
                tag = true;
                dialog.setMessage(LocaleController.getString(R.string.NumberOfAddBankCardTodayHadExceededLimitTips));
                dialog.setPositiveButton(LocaleController.getString(R.string.OK), (DialogInterface.OnClickListener) null);
            } else {
                dialog.setMessage(getErrorDescription(errorMsg), true, true);
            }
        }
        if (tag) {
            dialog.getNegativeButton().setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
            dialog.setNegativeButton(LocaleController.getString(R.string.Retry), (DialogInterface.OnClickListener) null);
            fragment.showDialog(dialog);
        }
        return tag;
    }

    static /* synthetic */ void lambda$parsePayPasswordErrorDialog$0(int finalCount, BaseFragment fragment, DialogInterface dialogInterface, int i) {
        if (finalCount <= 0) {
            fragment.presentFragment(new AboutAppActivity());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v131, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v132, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v134, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v137, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v138, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v139, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v140, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v141, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v142, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v143, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v144, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v146, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v147, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v149, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v150, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v151, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v152, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v153, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v154, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v155, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v156, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v157, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v158, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v159, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v160, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v161, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v162, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v163, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v164, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v165, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v166, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v167, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v168, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v169, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v170, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v171, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v172, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v173, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v174, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v175, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v176, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v177, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v178, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v179, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v180, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v181, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v182, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v184, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v185, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v186, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v187, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v188, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v189, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v190, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v191, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v192, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v193, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v194, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v195, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v196, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v197, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v198, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v199, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v201, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v202, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v203, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v204, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v205, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v206, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v207, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v208, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v209, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v210, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v211, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v212, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v215, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v216, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v218, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v219, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v220, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v221, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v222, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v223, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v224, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v225, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v226, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v227, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v228, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v229, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v230, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v231, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v232, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v233, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v234, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v235, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v236, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v237, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v238, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v239, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v240, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v241, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v242, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v243, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v244, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v245, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v246, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v247, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v248, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v249, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v250, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v251, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v252, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v253, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v254, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v255, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v256, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v257, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v258, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v259, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v260, resolved type: char} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getErrorDescription(java.lang.String r16, java.lang.String r17) {
        /*
            r0 = r17
            java.lang.String r1 = "ServerErrorResponseIsEmpty"
            r2 = 2131693857(0x7f0f1121, float:1.9016854E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r1, r2)
            if (r0 != 0) goto L_0x000e
            return r1
        L_0x000e:
            java.lang.String r2 = "ACCOUNT_PASSWORD_IN_MINUTES,ERROR_TIMES,WILL_BE_FROZEN_"
            boolean r2 = r0.contains(r2)
            r3 = 2131691146(0x7f0f068a, float:1.9011356E38)
            java.lang.String r4 = "ErrorPayPasswordTimesYouAccountWillBeForzen"
            r5 = 2
            r6 = 0
            r7 = 1
            if (r2 == 0) goto L_0x0035
            java.lang.String[] r2 = im.bclpbkiauv.ui.utils.number.NumberUtil.getNumbersFromStr(r17)
            int r8 = r2.length
            if (r8 != r5) goto L_0x0033
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r8 = r2[r6]
            r5[r6] = r8
            r6 = r2[r7]
            r5[r7] = r6
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r5)
        L_0x0033:
            goto L_0x0c18
        L_0x0035:
            java.lang.String r2 = "ERROR_NO_CANCELLATION_BALANCE_GREATER_THAN_"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0055
            java.lang.String[] r2 = im.bclpbkiauv.ui.utils.number.NumberUtil.getNumbersFromStr(r17)
            int r3 = r2.length
            if (r3 != r7) goto L_0x0053
            r3 = 2131690309(0x7f0f0345, float:1.9009658E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r5 = r2[r6]
            r4[r6] = r5
            java.lang.String r5 = "CancelAccountFailWithCashIsNotEmpty"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r3, r4)
        L_0x0053:
            goto L_0x0c18
        L_0x0055:
            r2 = -1
            int r8 = r17.hashCode()
            switch(r8) {
                case -2144635420: goto L_0x065b;
                case -2092522851: goto L_0x0650;
                case -2091299496: goto L_0x0645;
                case -2079937259: goto L_0x063a;
                case -2025769420: goto L_0x062f;
                case -1972351521: goto L_0x0624;
                case -1968712000: goto L_0x0619;
                case -1964016177: goto L_0x060e;
                case -1961651411: goto L_0x0603;
                case -1951241795: goto L_0x05f8;
                case -1936208949: goto L_0x05ec;
                case -1923611970: goto L_0x05e0;
                case -1896901538: goto L_0x05d4;
                case -1844978982: goto L_0x05c8;
                case -1773444383: goto L_0x05bc;
                case -1761476232: goto L_0x05b0;
                case -1675822067: goto L_0x05a4;
                case -1605877014: goto L_0x0598;
                case -1577993965: goto L_0x058c;
                case -1575231852: goto L_0x0580;
                case -1549115538: goto L_0x0574;
                case -1469612725: goto L_0x0568;
                case -1454959391: goto L_0x055c;
                case -1449090263: goto L_0x0550;
                case -1435642988: goto L_0x0544;
                case -1349891338: goto L_0x0538;
                case -1294203302: goto L_0x052c;
                case -1272363335: goto L_0x0520;
                case -1258389000: goto L_0x0514;
                case -1246306989: goto L_0x0508;
                case -1232921218: goto L_0x04fc;
                case -1190339722: goto L_0x04f0;
                case -1177453267: goto L_0x04e4;
                case -1171826550: goto L_0x04d8;
                case -1148959888: goto L_0x04cc;
                case -1145264038: goto L_0x04c1;
                case -1123435528: goto L_0x04b5;
                case -1108080737: goto L_0x04a9;
                case -945961441: goto L_0x049d;
                case -898271457: goto L_0x0491;
                case -862586151: goto L_0x0485;
                case -815654167: goto L_0x047a;
                case -803269227: goto L_0x046e;
                case -745834116: goto L_0x0462;
                case -739718390: goto L_0x0456;
                case -734874358: goto L_0x044a;
                case -580958708: goto L_0x043e;
                case -577434663: goto L_0x0432;
                case -539680220: goto L_0x0426;
                case -488853947: goto L_0x041a;
                case -449813985: goto L_0x040e;
                case -434497339: goto L_0x0402;
                case -430617811: goto L_0x03f6;
                case -424816978: goto L_0x03ea;
                case -410570710: goto L_0x03de;
                case -409306558: goto L_0x03d2;
                case -329865043: goto L_0x03c6;
                case -256279664: goto L_0x03ba;
                case -251524880: goto L_0x03ae;
                case -232446680: goto L_0x03a2;
                case -224509840: goto L_0x0396;
                case -175935118: goto L_0x038a;
                case -169668534: goto L_0x037e;
                case -130358483: goto L_0x0372;
                case -130024776: goto L_0x0366;
                case -88118345: goto L_0x035a;
                case -58040641: goto L_0x034e;
                case 21821413: goto L_0x0343;
                case 41860780: goto L_0x0337;
                case 89931806: goto L_0x032b;
                case 100175290: goto L_0x031f;
                case 127292299: goto L_0x0313;
                case 155378505: goto L_0x0307;
                case 197222107: goto L_0x02fb;
                case 199708815: goto L_0x02ef;
                case 235405616: goto L_0x02e3;
                case 292411456: goto L_0x02d7;
                case 343356237: goto L_0x02cb;
                case 371395673: goto L_0x02bf;
                case 505899914: goto L_0x02b3;
                case 598536268: goto L_0x02a7;
                case 661399960: goto L_0x029b;
                case 677566228: goto L_0x028f;
                case 694574117: goto L_0x0283;
                case 731241168: goto L_0x0277;
                case 746324468: goto L_0x026b;
                case 833588359: goto L_0x025f;
                case 837752632: goto L_0x0253;
                case 840426438: goto L_0x0247;
                case 866398254: goto L_0x023b;
                case 898444516: goto L_0x022f;
                case 904822764: goto L_0x0223;
                case 930821507: goto L_0x0217;
                case 938682073: goto L_0x020b;
                case 956012942: goto L_0x01ff;
                case 960863229: goto L_0x01f3;
                case 1050804984: goto L_0x01e7;
                case 1077990923: goto L_0x01db;
                case 1117754157: goto L_0x01cf;
                case 1126260135: goto L_0x01c3;
                case 1178337426: goto L_0x01b8;
                case 1185352029: goto L_0x01ac;
                case 1222472957: goto L_0x01a0;
                case 1234557633: goto L_0x0194;
                case 1260306429: goto L_0x0188;
                case 1335086637: goto L_0x017d;
                case 1407588716: goto L_0x0172;
                case 1488864863: goto L_0x0167;
                case 1491676330: goto L_0x015b;
                case 1518236069: goto L_0x014f;
                case 1539334037: goto L_0x0143;
                case 1554846384: goto L_0x0137;
                case 1556727811: goto L_0x012b;
                case 1598219258: goto L_0x011f;
                case 1630302841: goto L_0x0113;
                case 1668633190: goto L_0x0107;
                case 1688152246: goto L_0x00fb;
                case 1713725742: goto L_0x00ef;
                case 1730358686: goto L_0x00e3;
                case 1782154686: goto L_0x00d7;
                case 1783865422: goto L_0x00cb;
                case 1853331822: goto L_0x00bf;
                case 1854890676: goto L_0x00b3;
                case 1898328170: goto L_0x00a7;
                case 1936143911: goto L_0x009b;
                case 1940543338: goto L_0x008f;
                case 2000358825: goto L_0x0083;
                case 2101432071: goto L_0x0077;
                case 2129881790: goto L_0x006b;
                case 2140948840: goto L_0x005f;
                default: goto L_0x005d;
            }
        L_0x005d:
            goto L_0x0664
        L_0x005f:
            java.lang.String r8 = "ERROR_OLD_PASSWORD_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 94
            goto L_0x0664
        L_0x006b:
            java.lang.String r8 = "SYSTEM_SMS_BUSY_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 30
            goto L_0x0664
        L_0x0077:
            java.lang.String r8 = "ERROR_TRANS_AMOUNT_MISMATCH"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 70
            goto L_0x0664
        L_0x0083:
            java.lang.String r8 = "ERROR_PAY_PASSWORD_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 44
            goto L_0x0664
        L_0x008f:
            java.lang.String r8 = "AUTHENTICATION_CANCELLATION"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 112(0x70, float:1.57E-43)
            goto L_0x0664
        L_0x009b:
            java.lang.String r8 = "INCORRECT_PHONE_NUMBER_FORMATG_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 35
            goto L_0x0664
        L_0x00a7:
            java.lang.String r8 = "SYSTEM_ERROR_PAY_PASSWORD_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 41
            goto L_0x0664
        L_0x00b3:
            java.lang.String r8 = "ERROR_BILL_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 53
            goto L_0x0664
        L_0x00bf:
            java.lang.String r8 = "ERROR_RANDOM_CHARACTER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 74
            goto L_0x0664
        L_0x00cb:
            java.lang.String r8 = "ERROR_TEL_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 50
            goto L_0x0664
        L_0x00d7:
            java.lang.String r8 = "EXCLUSIVE_RED_NOT_COLLECTED_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 19
            goto L_0x0664
        L_0x00e3:
            java.lang.String r8 = "ERROR_NEW_PASSWORD_IS_INCONSISTENT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 46
            goto L_0x0664
        L_0x00ef:
            java.lang.String r8 = "THE_TRANSFER_IS_NOT_AVAILABLE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 26
            goto L_0x0664
        L_0x00fb:
            java.lang.String r8 = "SYSTEM_ERROR_ACCOUNT_EXCEPTION_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 27
            goto L_0x0664
        L_0x0107:
            java.lang.String r8 = "ERROR_TRADE_TYPE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 78
            goto L_0x0664
        L_0x0113:
            java.lang.String r8 = "ERROR_ORDER_HAS_BEEN_CONFIRMED"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 95
            goto L_0x0664
        L_0x011f:
            java.lang.String r8 = "BANKINFO_DOES_NOT_EXIST"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 105(0x69, float:1.47E-43)
            goto L_0x0664
        L_0x012b:
            java.lang.String r8 = "INSUFFICIENT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 128(0x80, float:1.794E-43)
            goto L_0x0664
        L_0x0137:
            java.lang.String r8 = "ACCOUNT_HAS_BEEN_FROZEN_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 36
            goto L_0x0664
        L_0x0143:
            java.lang.String r8 = "ERROR_GROUPS_NUMBER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 88
            goto L_0x0664
        L_0x014f:
            java.lang.String r8 = "ERROR_RED_NUMBER_ILLEGAL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 54
            goto L_0x0664
        L_0x015b:
            java.lang.String r8 = "SUCCESS_RED_STATUS_NUMBER_FULL_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 15
            goto L_0x0664
        L_0x0167:
            java.lang.String r8 = "ORDER_OK_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 5
            goto L_0x0664
        L_0x0172:
            java.lang.String r8 = "ORDER_CANCELLED_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 6
            goto L_0x0664
        L_0x017d:
            java.lang.String r8 = "ERROR_ILLEGAL_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 1
            goto L_0x0664
        L_0x0188:
            java.lang.String r8 = "ERROR_ACCOUNT_SYNCHRONIZED"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 81
            goto L_0x0664
        L_0x0194:
            java.lang.String r8 = "SYSTEM_SMS_NOT_SUPPORTED_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 110(0x6e, float:1.54E-43)
            goto L_0x0664
        L_0x01a0:
            java.lang.String r8 = "ERROR_PRODUCT_DESCRIPTION_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 75
            goto L_0x0664
        L_0x01ac:
            java.lang.String r8 = "ERROR_NOT_SUPPORTED_TEMPORARILY_FUNCTION"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 99
            goto L_0x0664
        L_0x01b8:
            java.lang.String r8 = "ORDER_NOT_EXIST_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 7
            goto L_0x0664
        L_0x01c3:
            java.lang.String r8 = "ERROR_RED_TO_EXIST_SUBMIT_REPEATEDLY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 84
            goto L_0x0664
        L_0x01cf:
            java.lang.String r8 = "ERROR_TADDITIONAL_DATA_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 71
            goto L_0x0664
        L_0x01db:
            java.lang.String r8 = "ERROR_USER_IS_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 43
            goto L_0x0664
        L_0x01e7:
            java.lang.String r8 = "ERROR_BANK_INFO_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 93
            goto L_0x0664
        L_0x01f3:
            java.lang.String r8 = "NAME_NOT_STANDARD"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 123(0x7b, float:1.72E-43)
            goto L_0x0664
        L_0x01ff:
            java.lang.String r8 = "ERROR_RED_NUMBER"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 61
            goto L_0x0664
        L_0x020b:
            java.lang.String r8 = "CARRYOVER_THE_TRANSFER_HAS_BEEN_RECEIVED"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 120(0x78, float:1.68E-43)
            goto L_0x0664
        L_0x0217:
            java.lang.String r8 = "TYPE_IS_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 109(0x6d, float:1.53E-43)
            goto L_0x0664
        L_0x0223:
            java.lang.String r8 = "SUCCESS_RED_STATUS_RECEIVE_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 13
            goto L_0x0664
        L_0x022f:
            java.lang.String r8 = "ACCOUNT_DISACCORD_AUTHENTICATION_INFORMATION_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 103(0x67, float:1.44E-43)
            goto L_0x0664
        L_0x023b:
            java.lang.String r8 = "SYSTEM_UNSUPPORTED_FILE_ERROR_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 33
            goto L_0x0664
        L_0x0247:
            java.lang.String r8 = "CARRYOVER_INFO_DOES_NOT_EXIST"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 106(0x6a, float:1.49E-43)
            goto L_0x0664
        L_0x0253:
            java.lang.String r8 = "ERROR_VERIFICATION_CODE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 47
            goto L_0x0664
        L_0x025f:
            java.lang.String r8 = "EXCLUSIVE_PLEASE_BIND_FIRST_BANKINFO"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 104(0x68, float:1.46E-43)
            goto L_0x0664
        L_0x026b:
            java.lang.String r8 = "SYSTEM_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 39
            goto L_0x0664
        L_0x0277:
            java.lang.String r8 = "ERROR_TRANSFER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 51
            goto L_0x0664
        L_0x0283:
            java.lang.String r8 = "EXCEED_RED_PACKET_ONCE_MAX_MONEY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 126(0x7e, float:1.77E-43)
            goto L_0x0664
        L_0x028f:
            java.lang.String r8 = "ERROR_ACC_NUMBER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 91
            goto L_0x0664
        L_0x029b:
            java.lang.String r8 = "ERROR_FIXED_AMOUNT_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 62
            goto L_0x0664
        L_0x02a7:
            java.lang.String r8 = "TRANSFER_COMPLETED_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 21
            goto L_0x0664
        L_0x02b3:
            java.lang.String r8 = "BANK_INFO_EXISTS"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 114(0x72, float:1.6E-43)
            goto L_0x0664
        L_0x02bf:
            java.lang.String r8 = "ERROR_ID_CAD_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 49
            goto L_0x0664
        L_0x02cb:
            java.lang.String r8 = "ACCOUNT_PAY_PASSWORD_ERROR"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 40
            goto L_0x0664
        L_0x02d7:
            java.lang.String r8 = "ERROR_RANDOM_TIME_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 52
            goto L_0x0664
        L_0x02e3:
            java.lang.String r8 = "ACCOUNT_UNCERTIFIED_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 38
            goto L_0x0664
        L_0x02ef:
            java.lang.String r8 = "ERROR_OPENBANK_OR_BANKCODE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 87
            goto L_0x0664
        L_0x02fb:
            java.lang.String r8 = "ID_NUMBER_NOT_STANDARD"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 121(0x79, float:1.7E-43)
            goto L_0x0664
        L_0x0307:
            java.lang.String r8 = "ERROR_INCONSISTENT_AMOUNT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 63
            goto L_0x0664
        L_0x0313:
            java.lang.String r8 = "ERROR_GROUPS_RED_NUMBER"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 56
            goto L_0x0664
        L_0x031f:
            java.lang.String r8 = "TOO_MANY_REQUEST"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 100
            goto L_0x0664
        L_0x032b:
            java.lang.String r8 = "ERROR_GRANT_TYPE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 59
            goto L_0x0664
        L_0x0337:
            java.lang.String r8 = "ERROR_AMOUNT_BELOW_MINIMUM_LIMIT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 64
            goto L_0x0664
        L_0x0343:
            java.lang.String r8 = "USER_INFONNOT_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 4
            goto L_0x0664
        L_0x034e:
            java.lang.String r8 = "TRANSFER_CANCELLING_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 24
            goto L_0x0664
        L_0x035a:
            java.lang.String r8 = "ERROR_NOT_SUPPORTED_PAY_SET"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 90
            goto L_0x0664
        L_0x0366:
            java.lang.String r8 = "ERROR_MERCHANT_ORDER_NUMBER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 66
            goto L_0x0664
        L_0x0372:
            java.lang.String r8 = "ERROR_ORDER_DOES_NOT_EXIST"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 86
            goto L_0x0664
        L_0x037e:
            java.lang.String r8 = "ERROR_CONFIRM_PAY_PASSWORD_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 45
            goto L_0x0664
        L_0x038a:
            java.lang.String r8 = "AUTHENTICATE_INFO_NOT_MATCH"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 124(0x7c, float:1.74E-43)
            goto L_0x0664
        L_0x0396:
            java.lang.String r8 = "ERROR_RECEIVER_OR_SELLER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 57
            goto L_0x0664
        L_0x03a2:
            java.lang.String r8 = "SYSTEM_REPEAT_SMS_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 29
            goto L_0x0664
        L_0x03ae:
            java.lang.String r8 = "EXCEED_TRANSFER_ONCE_MAX_MONEY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 127(0x7f, float:1.78E-43)
            goto L_0x0664
        L_0x03ba:
            java.lang.String r8 = "SUCCESS_RED_STATUS_COMPLETE_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 14
            goto L_0x0664
        L_0x03c6:
            java.lang.String r8 = "TRANSFER_NON_CANCELLING_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 22
            goto L_0x0664
        L_0x03d2:
            java.lang.String r8 = "ERROR_TRANSACTION_AMOUNT_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 69
            goto L_0x0664
        L_0x03de:
            java.lang.String r8 = "SYSTEM_ERROR_BANK_NO_FROZEN_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 42
            goto L_0x0664
        L_0x03ea:
            java.lang.String r8 = "ERROR_NOTALLOW_HAIR_LUCK_RED"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 60
            goto L_0x0664
        L_0x03f6:
            java.lang.String r8 = "SYSTEM_ERROR_BANK_NO_BADING_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 34
            goto L_0x0664
        L_0x0402:
            java.lang.String r8 = "ERROR_IMG_TYPE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 80
            goto L_0x0664
        L_0x040e:
            java.lang.String r8 = "SYSTEM_VERIFICATION_ERROR_SMS_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 32
            goto L_0x0664
        L_0x041a:
            java.lang.String r8 = "ERROR_TOTAL_AMOUNT_OF_CASH_SOLD_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 73
            goto L_0x0664
        L_0x0426:
            java.lang.String r8 = "BANK_NOT_WITHDRAWAL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 115(0x73, float:1.61E-43)
            goto L_0x0664
        L_0x0432:
            java.lang.String r8 = "NOT_SUFFICIENT_FUNDS"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 117(0x75, float:1.64E-43)
            goto L_0x0664
        L_0x043e:
            java.lang.String r8 = "SYSTEM_VERIFICATION_CODE_EXPIRED_SMS_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 31
            goto L_0x0664
        L_0x044a:
            java.lang.String r8 = "ERROR_USER_NAME_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 48
            goto L_0x0664
        L_0x0456:
            java.lang.String r8 = "ERROR_ORDER_TO_EXIST_SUBMIT_REPEATEDLY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 85
            goto L_0x0664
        L_0x0462:
            java.lang.String r8 = "BANK_NUMBER_NOT_STANDARD"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 122(0x7a, float:1.71E-43)
            goto L_0x0664
        L_0x046e:
            java.lang.String r8 = "ErrorSendMessageTooFreq"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 101(0x65, float:1.42E-43)
            goto L_0x0664
        L_0x047a:
            java.lang.String r8 = "REPEATED_REQUESTS"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 2
            goto L_0x0664
        L_0x0485:
            java.lang.String r8 = "ERROR_TOTALFEE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 76
            goto L_0x0664
        L_0x0491:
            java.lang.String r8 = "ERROR_SYSTEM_CODE_ALREADY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 82
            goto L_0x0664
        L_0x049d:
            java.lang.String r8 = "ERROR_RED_INFO_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 20
            goto L_0x0664
        L_0x04a9:
            java.lang.String r8 = "RED_MSG_NOT_EXIST"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 118(0x76, float:1.65E-43)
            goto L_0x0664
        L_0x04b5:
            java.lang.String r8 = "ORDER_NOT_CANCELLED_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 8
            goto L_0x0664
        L_0x04c1:
            java.lang.String r8 = "PARAMETER_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 3
            goto L_0x0664
        L_0x04cc:
            java.lang.String r8 = "ERROR_USER_NOT_MATCH_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 9
            goto L_0x0664
        L_0x04d8:
            java.lang.String r8 = "SUCCESS_USERINFO_INFORMATION_GROUP_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 23
            goto L_0x0664
        L_0x04e4:
            java.lang.String r8 = "ERROR_UNIT_PRICE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 67
            goto L_0x0664
        L_0x04f0:
            java.lang.String r8 = "ALREADY_VIP"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 129(0x81, float:1.81E-43)
            goto L_0x0664
        L_0x04fc:
            java.lang.String r8 = "INCONSISTENT_VALIDATION_INFORMATION_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 113(0x71, float:1.58E-43)
            goto L_0x0664
        L_0x0508:
            java.lang.String r8 = "EFFECT_USER_INFONNOT_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 10
            goto L_0x0664
        L_0x0514:
            java.lang.String r8 = "THE_BANK_INFORMATION_HAS_BEEN_BOUND"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 107(0x6b, float:1.5E-43)
            goto L_0x0664
        L_0x0520:
            java.lang.String r8 = "SUCCESS_RED_NOT_COLLECTED_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 18
            goto L_0x0664
        L_0x052c:
            java.lang.String r8 = "PAY_PASSWORD_MAX_ACCOUNT_FROZEN"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 37
            goto L_0x0664
        L_0x0538:
            java.lang.String r8 = "ERROR_REMARKS_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 65
            goto L_0x0664
        L_0x0544:
            java.lang.String r8 = "ERROR_AMOUNT_DOES_NOT_MATCH_CONFIRMED_AMOUNT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 96
            goto L_0x0664
        L_0x0550:
            java.lang.String r8 = "THE_TRANSFER_HAS_BEEN_CANCELLED"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 119(0x77, float:1.67E-43)
            goto L_0x0664
        L_0x055c:
            java.lang.String r8 = "RED_UNAVAILABLE_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 11
            goto L_0x0664
        L_0x0568:
            java.lang.String r8 = "SYSTEM_ERROR_NOT_SET_PAYWORD_COCE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 28
            goto L_0x0664
        L_0x0574:
            java.lang.String r8 = "ERROR_RECEIVER_USER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 55
            goto L_0x0664
        L_0x0580:
            java.lang.String r8 = "ERROR_INITIATOR_OR_BUYER_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 77
            goto L_0x0664
        L_0x058c:
            java.lang.String r8 = "ERROR_RED_TYPE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 58
            goto L_0x0664
        L_0x0598:
            java.lang.String r8 = "DAY_BAND_BANK_NUMBER_OVER_LIMIT"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 125(0x7d, float:1.75E-43)
            goto L_0x0664
        L_0x05a4:
            java.lang.String r8 = "NO_SMS_VERIFICATION_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 111(0x6f, float:1.56E-43)
            goto L_0x0664
        L_0x05b0:
            java.lang.String r8 = "TRANSFER_ACCOUNTS_UNAVAILABLE_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 12
            goto L_0x0664
        L_0x05bc:
            java.lang.String r8 = "ERROR_DIRECTION_OF_BUSINESS_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 72
            goto L_0x0664
        L_0x05c8:
            java.lang.String r8 = "NO_USABLE_CHANNEL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 116(0x74, float:1.63E-43)
            goto L_0x0664
        L_0x05d4:
            java.lang.String r8 = "AUTHENTICATE_INFO_ERR"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 102(0x66, float:1.43E-43)
            goto L_0x0664
        L_0x05e0:
            java.lang.String r8 = "ERROR_PAY_SET_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 89
            goto L_0x0664
        L_0x05ec:
            java.lang.String r8 = "TRANSFER_REPEAT_PAY_ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 25
            goto L_0x0664
        L_0x05f8:
            java.lang.String r8 = "ERROR_SERVICE_CHARGE_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 68
            goto L_0x0664
        L_0x0603:
            java.lang.String r8 = "SUCCESS_RED_STATUS_EXISTENCE_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 17
            goto L_0x0664
        L_0x060e:
            java.lang.String r8 = "ERROR_TRADELOG_ID_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 98
            goto L_0x0664
        L_0x0619:
            java.lang.String r8 = "ERROR_CASH_AMOUNT_MISMATCH"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 97
            goto L_0x0664
        L_0x0624:
            java.lang.String r8 = "ERROR_TRANSFER_TO_EXIST_SUBMIT_REPEATEDLY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 83
            goto L_0x0664
        L_0x062f:
            java.lang.String r8 = "ERROR_NOT_SUPPORTED_PAY"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 79
            goto L_0x0664
        L_0x063a:
            java.lang.String r8 = "ERROR_ADDRESS_NOT_NULL"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 92
            goto L_0x0664
        L_0x0645:
            java.lang.String r8 = "SUCCESS_RED_STATUS_INVALID_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 16
            goto L_0x0664
        L_0x0650:
            java.lang.String r8 = "CURRENCY_TYPE_IS_EMPTY_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 108(0x6c, float:1.51E-43)
            goto L_0x0664
        L_0x065b:
            java.lang.String r8 = "ERROR_CODE"
            boolean r8 = r0.equals(r8)
            if (r8 == 0) goto L_0x005d
            r2 = 0
        L_0x0664:
            r8 = 2131690088(0x7f0f0268, float:1.900921E38)
            java.lang.String r9 = "BalanceIsNotEnough"
            r10 = 2131694280(0x7f0f12c8, float:1.9017712E38)
            java.lang.String r11 = "TooManyRequest"
            r12 = 2131690140(0x7f0f029c, float:1.9009315E38)
            java.lang.String r13 = "BillInfoNotNull"
            r14 = 2131694118(0x7f0f1226, float:1.9017384E38)
            java.lang.String r15 = "TheAccountHasBeenFrozenWith24H"
            switch(r2) {
                case 0: goto L_0x0c0e;
                case 1: goto L_0x0c04;
                case 2: goto L_0x0bfa;
                case 3: goto L_0x0bf0;
                case 4: goto L_0x0be6;
                case 5: goto L_0x0bdc;
                case 6: goto L_0x0bd2;
                case 7: goto L_0x0bc8;
                case 8: goto L_0x0bbe;
                case 9: goto L_0x0bb4;
                case 10: goto L_0x0baa;
                case 11: goto L_0x0b9f;
                case 12: goto L_0x0b9f;
                case 13: goto L_0x0b94;
                case 14: goto L_0x0b89;
                case 15: goto L_0x0b89;
                case 16: goto L_0x0b7e;
                case 17: goto L_0x0b73;
                case 18: goto L_0x0b68;
                case 19: goto L_0x0b5d;
                case 20: goto L_0x0b52;
                case 21: goto L_0x0b47;
                case 22: goto L_0x0b3c;
                case 23: goto L_0x0b31;
                case 24: goto L_0x0b26;
                case 25: goto L_0x0b1b;
                case 26: goto L_0x0b10;
                case 27: goto L_0x0b05;
                case 28: goto L_0x0afa;
                case 29: goto L_0x0aef;
                case 30: goto L_0x0ae4;
                case 31: goto L_0x0ad9;
                case 32: goto L_0x0ace;
                case 33: goto L_0x0ac3;
                case 34: goto L_0x0ab8;
                case 35: goto L_0x0aad;
                case 36: goto L_0x0aa4;
                case 37: goto L_0x0a9b;
                case 38: goto L_0x0a90;
                case 39: goto L_0x0a88;
                case 40: goto L_0x0a64;
                case 41: goto L_0x0a3b;
                case 42: goto L_0x0a20;
                case 43: goto L_0x0a15;
                case 44: goto L_0x0a0a;
                case 45: goto L_0x09ff;
                case 46: goto L_0x09f4;
                case 47: goto L_0x09e9;
                case 48: goto L_0x09de;
                case 49: goto L_0x09d3;
                case 50: goto L_0x09c8;
                case 51: goto L_0x09bd;
                case 52: goto L_0x09b2;
                case 53: goto L_0x09ac;
                case 54: goto L_0x09a1;
                case 55: goto L_0x0996;
                case 56: goto L_0x098b;
                case 57: goto L_0x0980;
                case 58: goto L_0x0975;
                case 59: goto L_0x096a;
                case 60: goto L_0x095f;
                case 61: goto L_0x0954;
                case 62: goto L_0x0949;
                case 63: goto L_0x093e;
                case 64: goto L_0x0933;
                case 65: goto L_0x0928;
                case 66: goto L_0x091d;
                case 67: goto L_0x0912;
                case 68: goto L_0x0907;
                case 69: goto L_0x08fc;
                case 70: goto L_0x08f1;
                case 71: goto L_0x08e6;
                case 72: goto L_0x08db;
                case 73: goto L_0x08d0;
                case 74: goto L_0x08c5;
                case 75: goto L_0x08ba;
                case 76: goto L_0x08af;
                case 77: goto L_0x08a4;
                case 78: goto L_0x0899;
                case 79: goto L_0x088e;
                case 80: goto L_0x0883;
                case 81: goto L_0x0878;
                case 82: goto L_0x086d;
                case 83: goto L_0x0862;
                case 84: goto L_0x0857;
                case 85: goto L_0x084c;
                case 86: goto L_0x0841;
                case 87: goto L_0x0836;
                case 88: goto L_0x082b;
                case 89: goto L_0x0820;
                case 90: goto L_0x0815;
                case 91: goto L_0x080a;
                case 92: goto L_0x07ff;
                case 93: goto L_0x07f4;
                case 94: goto L_0x07e9;
                case 95: goto L_0x07de;
                case 96: goto L_0x07d3;
                case 97: goto L_0x07c8;
                case 98: goto L_0x07c2;
                case 99: goto L_0x07b7;
                case 100: goto L_0x07b1;
                case 101: goto L_0x07ab;
                case 102: goto L_0x07a0;
                case 103: goto L_0x0795;
                case 104: goto L_0x078a;
                case 105: goto L_0x077f;
                case 106: goto L_0x0774;
                case 107: goto L_0x0769;
                case 108: goto L_0x075e;
                case 109: goto L_0x0753;
                case 110: goto L_0x0748;
                case 111: goto L_0x073d;
                case 112: goto L_0x0732;
                case 113: goto L_0x0727;
                case 114: goto L_0x071c;
                case 115: goto L_0x0711;
                case 116: goto L_0x0706;
                case 117: goto L_0x0700;
                case 118: goto L_0x06f5;
                case 119: goto L_0x06ea;
                case 120: goto L_0x06df;
                case 121: goto L_0x06d4;
                case 122: goto L_0x06c9;
                case 123: goto L_0x06be;
                case 124: goto L_0x06b3;
                case 125: goto L_0x06a8;
                case 126: goto L_0x069d;
                case 127: goto L_0x0692;
                case 128: goto L_0x068c;
                case 129: goto L_0x0681;
                default: goto L_0x067b;
            }
        L_0x067b:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r14)
            goto L_0x0c18
        L_0x0681:
            r2 = 2131689758(0x7f0f011e, float:1.900854E38)
            java.lang.String r3 = "AlreadyCdnVIP"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x068c:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            goto L_0x0c18
        L_0x0692:
            r2 = 2131693983(0x7f0f119f, float:1.901711E38)
            java.lang.String r3 = "SingleTransferAmoutExceedsUpperLimit"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x069d:
            r2 = 2131693980(0x7f0f119c, float:1.9017104E38)
            java.lang.String r3 = "SingleRedPacketAmoutExceedsUpperLimit"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06a8:
            r2 = 2131692458(0x7f0f0baa, float:1.9014017E38)
            java.lang.String r3 = "NumberOfAddBankCardTodayHadExceededLimitTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06b3:
            r2 = 2131690917(0x7f0f05a5, float:1.9010891E38)
            java.lang.String r3 = "DiffBankCardAndIdCardTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06be:
            r2 = 2131692124(0x7f0f0a5c, float:1.901334E38)
            java.lang.String r3 = "NameErrorTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06c9:
            r2 = 2131690111(0x7f0f027f, float:1.9009256E38)
            java.lang.String r3 = "BankCardNumberErrorTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06d4:
            r2 = 2131691623(0x7f0f0867, float:1.9012323E38)
            java.lang.String r3 = "IdCardNumberErrorTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06df:
            r2 = 2131689760(0x7f0f0120, float:1.9008544E38)
            java.lang.String r3 = "AlreadyCollectedCantRepeat"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06ea:
            r2 = 2131694353(0x7f0f1311, float:1.901786E38)
            java.lang.String r3 = "TransferHadBeenCanceledCannotBeReceived"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x06f5:
            r2 = 2131693378(0x7f0f0f42, float:1.9015883E38)
            java.lang.String r3 = "RedpacketHadBeenWithdrawCannotBeClaimed"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0700:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r8)
            goto L_0x0c18
        L_0x0706:
            r2 = 2131692233(0x7f0f0ac9, float:1.901356E38)
            java.lang.String r3 = "NoPaymentChannelsAvailable"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0711:
            r2 = 2131690331(0x7f0f035b, float:1.9009703E38)
            java.lang.String r3 = "CannotSupportThisBank"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x071c:
            r2 = 2131690107(0x7f0f027b, float:1.9009248E38)
            java.lang.String r3 = "BankCardInfoIsExists"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0727:
            r2 = 2131691156(0x7f0f0694, float:1.9011376E38)
            java.lang.String r3 = "ErrorVerifyPhoneCodeAndTryAgain"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0732:
            r2 = 2131693266(0x7f0f0ed2, float:1.9015655E38)
            java.lang.String r3 = "RealNameAuthCancelSuccessTips"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x073d:
            r2 = 2131693121(0x7f0f0e41, float:1.9015361E38)
            java.lang.String r3 = "PleaseVerifyYourPhoneCode"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0748:
            r2 = 2131692284(0x7f0f0afc, float:1.9013664E38)
            java.lang.String r3 = "NotSupportThisSMSType"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0753:
            r2 = 2131694435(0x7f0f1363, float:1.9018026E38)
            java.lang.String r3 = "TypeCannotBeEmpty"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x075e:
            r2 = 2131690635(0x7f0f048b, float:1.901032E38)
            java.lang.String r3 = "CoinTypeCannotBeEmpty"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0769:
            r2 = 2131694166(0x7f0f1256, float:1.901748E38)
            java.lang.String r3 = "TheBankCardHadBeenBound"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0774:
            r2 = 2131694359(0x7f0f1317, float:1.9017872E38)
            java.lang.String r3 = "TransferInfoNotExists"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x077f:
            r2 = 2131690108(0x7f0f027c, float:1.900925E38)
            java.lang.String r3 = "BankCardInfoNotExists"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x078a:
            r2 = 2131693076(0x7f0f0e14, float:1.901527E38)
            java.lang.String r3 = "PleaseBindBankCardFirst"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0795:
            r2 = 2131689973(0x7f0f01f5, float:1.9008976E38)
            java.lang.String r3 = "AuthInfoInconsistent"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07a0:
            r2 = 2131689975(0x7f0f01f7, float:1.900898E38)
            java.lang.String r3 = "AuthInfoMismatched"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07ab:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            goto L_0x0c18
        L_0x07b1:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            goto L_0x0c18
        L_0x07b7:
            r2 = 2131692280(0x7f0f0af8, float:1.9013656E38)
            java.lang.String r3 = "NotSupport"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07c2:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            goto L_0x0c18
        L_0x07c8:
            r2 = 2131690344(0x7f0f0368, float:1.9009729E38)
            java.lang.String r3 = "CashAmountMismatch"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07d3:
            r2 = 2131689777(0x7f0f0131, float:1.9008579E38)
            java.lang.String r3 = "AmountNotMatchComfireAmount"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07de:
            r2 = 2131692526(0x7f0f0bee, float:1.9014155E38)
            java.lang.String r3 = "OrderHasBeenComfirmed"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07e9:
            r2 = 2131692470(0x7f0f0bb6, float:1.901404E38)
            java.lang.String r3 = "OldPasswordNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07f4:
            r2 = 2131690118(0x7f0f0286, float:1.900927E38)
            java.lang.String r3 = "BankInfoNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x07ff:
            r2 = 2131689719(0x7f0f00f7, float:1.9008461E38)
            java.lang.String r3 = "AddressNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x080a:
            r2 = 2131689577(0x7f0f0069, float:1.9008173E38)
            java.lang.String r3 = "AccountNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0815:
            r2 = 2131692283(0x7f0f0afb, float:1.9013662E38)
            java.lang.String r3 = "NotSupportPayChannel"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0820:
            r2 = 2131692891(0x7f0f0d5b, float:1.9014895E38)
            java.lang.String r3 = "PayChannelNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x082b:
            r2 = 2131691549(0x7f0f081d, float:1.9012173E38)
            java.lang.String r3 = "GroupsNumberNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0836:
            r2 = 2131692504(0x7f0f0bd8, float:1.901411E38)
            java.lang.String r3 = "OpenbankOrBankcodeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0841:
            r2 = 2131692523(0x7f0f0beb, float:1.9014149E38)
            java.lang.String r3 = "OrderDoesNotExist"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x084c:
            r2 = 2131692524(0x7f0f0bec, float:1.901415E38)
            java.lang.String r3 = "OrderExistSubmitRepeatedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0857:
            r2 = 2131693561(0x7f0f0ff9, float:1.9016254E38)
            java.lang.String r3 = "RpkToExistSubmitRepeatedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0862:
            r2 = 2131694407(0x7f0f1347, float:1.901797E38)
            java.lang.String r3 = "TransferToExistSubmitRepeatedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x086d:
            r2 = 2131694116(0x7f0f1224, float:1.901738E38)
            java.lang.String r3 = "SystemCodeAlready"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0878:
            r2 = 2131689580(0x7f0f006c, float:1.900818E38)
            java.lang.String r3 = "AccountSynchronized"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0883:
            r2 = 2131691630(0x7f0f086e, float:1.9012337E38)
            java.lang.String r3 = "ImgTypeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x088e:
            r2 = 2131692282(0x7f0f0afa, float:1.901366E38)
            java.lang.String r3 = "NotSupportPay"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0899:
            r2 = 2131694312(0x7f0f12e8, float:1.9017777E38)
            java.lang.String r3 = "TradeTypeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08a4:
            r2 = 2131691655(0x7f0f0887, float:1.9012388E38)
            java.lang.String r3 = "InitiatorOrBuyerNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08af:
            r2 = 2131694305(0x7f0f12e1, float:1.9017763E38)
            java.lang.String r3 = "TotalfeeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08ba:
            r2 = 2131693192(0x7f0f0e88, float:1.9015505E38)
            java.lang.String r3 = "ProductDescNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08c5:
            r2 = 2131693245(0x7f0f0ebd, float:1.9015613E38)
            java.lang.String r3 = "RandomCharacterNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08d0:
            r2 = 2131694299(0x7f0f12db, float:1.901775E38)
            java.lang.String r3 = "TotalAmountOfCashSoldNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08db:
            r2 = 2131690923(0x7f0f05ab, float:1.9010903E38)
            java.lang.String r3 = "DirectionOfBusinessNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08e6:
            r2 = 2131694124(0x7f0f122c, float:1.9017396E38)
            java.lang.String r3 = "TadditionalDataNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08f1:
            r2 = 2131694317(0x7f0f12ed, float:1.9017787E38)
            java.lang.String r3 = "TransAmountMismatch"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x08fc:
            r2 = 2131694323(0x7f0f12f3, float:1.90178E38)
            java.lang.String r3 = "TransactionAmountNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0907:
            r2 = 2131693861(0x7f0f1125, float:1.9016862E38)
            java.lang.String r3 = "ServiceChargeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0912:
            r2 = 2131694482(0x7f0f1392, float:1.9018122E38)
            java.lang.String r3 = "UnitPriceNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x091d:
            r2 = 2131691987(0x7f0f09d3, float:1.9013061E38)
            java.lang.String r3 = "MerchantOrderNumberNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0928:
            r2 = 2131693417(0x7f0f0f69, float:1.9015962E38)
            java.lang.String r3 = "RemarksNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0933:
            r2 = 2131689776(0x7f0f0130, float:1.9008577E38)
            java.lang.String r3 = "AmountBelowMinLimit"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x093e:
            r2 = 2131691643(0x7f0f087b, float:1.9012364E38)
            java.lang.String r3 = "InconsistentAmount"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0949:
            r2 = 2131691321(0x7f0f0739, float:1.901171E38)
            java.lang.String r3 = "FixedAmountNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0954:
            r2 = 2131693559(0x7f0f0ff7, float:1.901625E38)
            java.lang.String r3 = "RpkInfoError"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x095f:
            r2 = 2131692272(0x7f0f0af0, float:1.901364E38)
            java.lang.String r3 = "NotAllowHairLuckRpk"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x096a:
            r2 = 2131691498(0x7f0f07ea, float:1.901207E38)
            java.lang.String r3 = "GrantTypeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0975:
            r2 = 2131693562(0x7f0f0ffa, float:1.9016256E38)
            java.lang.String r3 = "RpkTypeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0980:
            r2 = 2131693299(0x7f0f0ef3, float:1.9015722E38)
            java.lang.String r3 = "ReceiverOrSellerNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x098b:
            r2 = 2131691523(0x7f0f0803, float:1.901212E38)
            java.lang.String r3 = "GroupPersonRpkNumberError"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0996:
            r2 = 2131693298(0x7f0f0ef2, float:1.901572E38)
            java.lang.String r3 = "ReceiverInfoNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09a1:
            r2 = 2131693560(0x7f0f0ff8, float:1.9016252E38)
            java.lang.String r3 = "RpkNumberIllegal"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09ac:
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r12)
            goto L_0x0c18
        L_0x09b2:
            r2 = 2131693246(0x7f0f0ebe, float:1.9015615E38)
            java.lang.String r3 = "RandomTimeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09bd:
            r2 = 2131694360(0x7f0f1318, float:1.9017874E38)
            java.lang.String r3 = "TransferInfoNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09c8:
            r2 = 2131694134(0x7f0f1236, float:1.9017416E38)
            java.lang.String r3 = "TelNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09d3:
            r2 = 2131691622(0x7f0f0866, float:1.9012321E38)
            java.lang.String r3 = "IdCardNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09de:
            r2 = 2131694567(0x7f0f13e7, float:1.9018294E38)
            java.lang.String r3 = "UserNameNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09e9:
            r2 = 2131694644(0x7f0f1434, float:1.901845E38)
            java.lang.String r3 = "VertificationCodeNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09f4:
            r2 = 2131692165(0x7f0f0a85, float:1.9013422E38)
            java.lang.String r3 = "NewPasswordInconsistent"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x09ff:
            r2 = 2131690653(0x7f0f049d, float:1.9010356E38)
            java.lang.String r3 = "ComfirmPayPasswordNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0a0a:
            r2 = 2131692899(0x7f0f0d63, float:1.9014911E38)
            java.lang.String r3 = "PayPasswordNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0a15:
            r2 = 2131694570(0x7f0f13ea, float:1.90183E38)
            java.lang.String r3 = "UserNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0a20:
            r2 = 2131694873(0x7f0f1519, float:1.9018915E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean r4 = im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean.getInstance()
            int r4 = r4.getForzenTime()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r6] = r4
            java.lang.String r4 = "YourAccountHasBeenForzenTime"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            goto L_0x0c18
        L_0x0a3b:
            r2 = 2131691147(0x7f0f068b, float:1.9011358E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean r4 = im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean.getInstance()
            int r4 = r4.getTradePayPasswordInputWrongTimes()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r6] = r4
            im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean r4 = im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean.getInstance()
            int r4 = r4.getTradeLimitTime()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r7] = r4
            java.lang.String r4 = "ErrorPayPasswordTimesYouAccountWillLimitTrade"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            goto L_0x0c18
        L_0x0a64:
            java.lang.Object[] r2 = new java.lang.Object[r5]
            im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean r5 = im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean.getInstance()
            int r5 = r5.getForzenTime()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r6] = r5
            im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean r5 = im.bclpbkiauv.ui.hui.wallet_public.bean.WalletConfigBean.getInstance()
            int r5 = r5.getForzenPayPasswordInputWrongTimes()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r2[r7] = r5
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r3, r2)
            goto L_0x0c18
        L_0x0a88:
            java.lang.String r2 = "SystemIsBusyAndTryAgainLater"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r14)
            goto L_0x0c18
        L_0x0a90:
            r2 = 2131694165(0x7f0f1255, float:1.9017479E38)
            java.lang.String r3 = "TheAccountNotAuthedOrNotPassedPleaseAuthFirst"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0a9b:
            r2 = 2131694164(0x7f0f1254, float:1.9017477E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r2)
            goto L_0x0c18
        L_0x0aa4:
            r2 = 2131694164(0x7f0f1254, float:1.9017477E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r15, r2)
            goto L_0x0c18
        L_0x0aad:
            r2 = 2131691646(0x7f0f087e, float:1.901237E38)
            java.lang.String r3 = "IncorrectPhoneNumberFormat"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0ab8:
            r2 = 2131692285(0x7f0f0afd, float:1.9013666E38)
            java.lang.String r3 = "NotSupportThisTypeBankCard"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0ac3:
            r2 = 2131692286(0x7f0f0afe, float:1.9013668E38)
            java.lang.String r3 = "NotSupportThisTypeFileToUpload"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0ace:
            r2 = 2131694635(0x7f0f142b, float:1.9018432E38)
            java.lang.String r3 = "VerificationcodeError"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0ad9:
            r2 = 2131694636(0x7f0f142c, float:1.9018434E38)
            java.lang.String r3 = "VerificationcodeExpired"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0ae4:
            r2 = 2131693619(0x7f0f1033, float:1.9016371E38)
            java.lang.String r3 = "SMSSystemIsBusy"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0aef:
            r2 = 2131690330(0x7f0f035a, float:1.90097E38)
            java.lang.String r3 = "CannotSendSMSRepeatedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0afa:
            r2 = 2131693115(0x7f0f0e3b, float:1.901535E38)
            java.lang.String r3 = "PleaseSetPayPasswordFirst"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b05:
            r2 = 2131689473(0x7f0f0001, float:1.9007962E38)
            java.lang.String r3 = "AbnormalAccountInformation"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b10:
            r2 = 2131694338(0x7f0f1302, float:1.901783E38)
            java.lang.String r3 = "TransferCannotBeReceived"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b1b:
            r2 = 2131692534(0x7f0f0bf6, float:1.901417E38)
            java.lang.String r3 = "OrderYouPaidCannotPayRepetedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b26:
            r2 = 2131694334(0x7f0f12fe, float:1.9017822E38)
            java.lang.String r3 = "TransferCancelledCannotCancelledRepetedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b31:
            r2 = 2131694842(0x7f0f14fa, float:1.9018852E38)
            java.lang.String r3 = "YouAreNotMemberOfThisGroupYouCannotReceived"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b3c:
            r2 = 2131694340(0x7f0f1304, float:1.9017834E38)
            java.lang.String r3 = "TransferCompletedCannotCancelled"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b47:
            r2 = 2131694342(0x7f0f1306, float:1.9017838E38)
            java.lang.String r3 = "TransferCompletedCannotReceivedRepetedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b52:
            r2 = 2131693380(0x7f0f0f44, float:1.9015887E38)
            java.lang.String r3 = "RedpacketInfoMustNotNull"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b5d:
            r2 = 2131693382(0x7f0f0f46, float:1.901589E38)
            java.lang.String r3 = "RedpacketIsExclusivedIsNotForYou"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b68:
            r2 = 2131693384(0x7f0f0f48, float:1.9015895E38)
            java.lang.String r3 = "RedpacketIsNotForYou"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b73:
            r2 = 2131693385(0x7f0f0f49, float:1.9015897E38)
            java.lang.String r3 = "RedpacketNotExist"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b7e:
            r2 = 2131693379(0x7f0f0f43, float:1.9015885E38)
            java.lang.String r3 = "RedpacketHadExpired"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b89:
            r2 = 2131693377(0x7f0f0f41, float:1.901588E38)
            java.lang.String r3 = "RedpacketHadBeenCollected"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b94:
            r2 = 2131693387(0x7f0f0f4b, float:1.90159E38)
            java.lang.String r3 = "RedpacketYouHadReceievedCanNotReceivedRepetedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0b9f:
            r2 = 2131693394(0x7f0f0f52, float:1.9015915E38)
            java.lang.String r3 = "RefundIsNotSupportedForThisType"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0baa:
            r2 = 2131693321(0x7f0f0f09, float:1.9015767E38)
            java.lang.String r3 = "RecipientInformationDoesNotExist"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bb4:
            r2 = 2131689573(0x7f0f0065, float:1.9008165E38)
            java.lang.String r3 = "AccountInformationMismatch"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bbe:
            r2 = 2131692518(0x7f0f0be6, float:1.9014138E38)
            java.lang.String r3 = "OrderCanNotBeCancelled"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bc8:
            r2 = 2131692531(0x7f0f0bf3, float:1.9014165E38)
            java.lang.String r3 = "OrderNotExist"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bd2:
            r2 = 2131692528(0x7f0f0bf0, float:1.9014159E38)
            java.lang.String r3 = "OrderHasCanceledCanNotCancelRepetedly"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bdc:
            r2 = 2131692529(0x7f0f0bf1, float:1.901416E38)
            java.lang.String r3 = "OrderHasCompletedCanNotCancel"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0be6:
            r2 = 2131690772(0x7f0f0514, float:1.9010597E38)
            java.lang.String r3 = "CurrentUserNotOpenedWalletAccount"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bf0:
            r2 = 2131692591(0x7f0f0c2f, float:1.9014286E38)
            java.lang.String r3 = "ParameterError"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0bfa:
            r2 = 2131692540(0x7f0f0bfc, float:1.9014183E38)
            java.lang.String r3 = "OtherDeviceDoTheSame"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0c04:
            r2 = 2131691625(0x7f0f0869, float:1.9012327E38)
            java.lang.String r3 = "IllegalOperation"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
            goto L_0x0c18
        L_0x0c0e:
            r2 = 2131692505(0x7f0f0bd9, float:1.9014112E38)
            java.lang.String r3 = "OperationFailed"
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r2)
        L_0x0c18:
            boolean r2 = android.text.TextUtils.isEmpty(r16)
            if (r2 != 0) goto L_0x0c35
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = r16
            r2.append(r3)
            java.lang.String r4 = "  "
            r2.append(r4)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            goto L_0x0c37
        L_0x0c35:
            r3 = r16
        L_0x0c37:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil.getErrorDescription(java.lang.String, java.lang.String):java.lang.String");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getWeChatPayErrorDescription(java.lang.String r2) {
        /*
            int r0 = r2.hashCode()
            switch(r0) {
                case 49500725: goto L_0x0350;
                case 49500726: goto L_0x0346;
                case 49500727: goto L_0x033c;
                case 49500728: goto L_0x0332;
                default: goto L_0x0007;
            }
        L_0x0007:
            switch(r0) {
                case 49500731: goto L_0x0328;
                case 49500732: goto L_0x031e;
                case 49500733: goto L_0x0314;
                default: goto L_0x000a;
            }
        L_0x000a:
            switch(r0) {
                case 49500755: goto L_0x030a;
                case 49500756: goto L_0x02ff;
                case 49500757: goto L_0x02f4;
                case 49500758: goto L_0x02e8;
                case 49500759: goto L_0x02dc;
                case 49500760: goto L_0x02d0;
                case 49500761: goto L_0x02c4;
                case 49500762: goto L_0x02b8;
                case 49500763: goto L_0x02ac;
                case 49500764: goto L_0x02a0;
                case 49500786: goto L_0x0294;
                case 49500795: goto L_0x0288;
                case 49500817: goto L_0x027c;
                case 49500826: goto L_0x0270;
                case 49500856: goto L_0x0264;
                case 49500916: goto L_0x0258;
                case 49530546: goto L_0x024c;
                case 49649680: goto L_0x0240;
                case 49649681: goto L_0x0234;
                case 49649682: goto L_0x0228;
                case 49649683: goto L_0x021c;
                case 49649684: goto L_0x0210;
                case 49649685: goto L_0x0204;
                case 49649686: goto L_0x01f8;
                case 49649687: goto L_0x01ec;
                case 49649688: goto L_0x01e0;
                case 49649710: goto L_0x01d4;
                case 49649711: goto L_0x01c8;
                case 49649712: goto L_0x01bc;
                case 49649716: goto L_0x01b0;
                case 49649717: goto L_0x01a4;
                case 49649718: goto L_0x0198;
                case 50424246: goto L_0x018c;
                default: goto L_0x000d;
            }
        L_0x000d:
            switch(r0) {
                case 49500789: goto L_0x0180;
                case 49500790: goto L_0x0174;
                case 49500791: goto L_0x0168;
                case 49500792: goto L_0x015c;
                case 49500793: goto L_0x0150;
                default: goto L_0x0010;
            }
        L_0x0010:
            switch(r0) {
                case 49500823: goto L_0x0144;
                case 49500824: goto L_0x0138;
                default: goto L_0x0013;
            }
        L_0x0013:
            switch(r0) {
                case 49500883: goto L_0x012c;
                case 49500884: goto L_0x0120;
                default: goto L_0x0016;
            }
        L_0x0016:
            switch(r0) {
                case 49530516: goto L_0x0114;
                case 49530517: goto L_0x0108;
                case 49530518: goto L_0x00fc;
                case 49530519: goto L_0x00f0;
                case 49530520: goto L_0x00e4;
                case 49530521: goto L_0x00d8;
                case 49530522: goto L_0x00cc;
                case 49530523: goto L_0x00c0;
                case 49530524: goto L_0x00b4;
                default: goto L_0x0019;
            }
        L_0x0019:
            switch(r0) {
                case 49560307: goto L_0x00a8;
                case 49560308: goto L_0x009c;
                case 49560309: goto L_0x0090;
                default: goto L_0x001c;
            }
        L_0x001c:
            switch(r0) {
                case 49590098: goto L_0x0084;
                case 49590099: goto L_0x0078;
                case 49590100: goto L_0x006c;
                case 49590101: goto L_0x0060;
                default: goto L_0x001f;
            }
        L_0x001f:
            switch(r0) {
                case 49619889: goto L_0x0054;
                case 49619890: goto L_0x0048;
                case 49619891: goto L_0x003c;
                case 49619892: goto L_0x0030;
                case 49619893: goto L_0x0024;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x035a
        L_0x0024:
            java.lang.String r0 = "44005"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 53
            goto L_0x035b
        L_0x0030:
            java.lang.String r0 = "44004"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 52
            goto L_0x035b
        L_0x003c:
            java.lang.String r0 = "44003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 51
            goto L_0x035b
        L_0x0048:
            java.lang.String r0 = "44002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 50
            goto L_0x035b
        L_0x0054:
            java.lang.String r0 = "44001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 49
            goto L_0x035b
        L_0x0060:
            java.lang.String r0 = "43004"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 48
            goto L_0x035b
        L_0x006c:
            java.lang.String r0 = "43003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 47
            goto L_0x035b
        L_0x0078:
            java.lang.String r0 = "43002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 46
            goto L_0x035b
        L_0x0084:
            java.lang.String r0 = "43001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 45
            goto L_0x035b
        L_0x0090:
            java.lang.String r0 = "42003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 44
            goto L_0x035b
        L_0x009c:
            java.lang.String r0 = "42002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 43
            goto L_0x035b
        L_0x00a8:
            java.lang.String r0 = "42001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 42
            goto L_0x035b
        L_0x00b4:
            java.lang.String r0 = "41009"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 40
            goto L_0x035b
        L_0x00c0:
            java.lang.String r0 = "41008"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 39
            goto L_0x035b
        L_0x00cc:
            java.lang.String r0 = "41007"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 38
            goto L_0x035b
        L_0x00d8:
            java.lang.String r0 = "41006"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 37
            goto L_0x035b
        L_0x00e4:
            java.lang.String r0 = "41005"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 36
            goto L_0x035b
        L_0x00f0:
            java.lang.String r0 = "41004"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 35
            goto L_0x035b
        L_0x00fc:
            java.lang.String r0 = "41003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 34
            goto L_0x035b
        L_0x0108:
            java.lang.String r0 = "41002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 33
            goto L_0x035b
        L_0x0114:
            java.lang.String r0 = "41001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 32
            goto L_0x035b
        L_0x0120:
            java.lang.String r0 = "40055"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 30
            goto L_0x035b
        L_0x012c:
            java.lang.String r0 = "40054"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 29
            goto L_0x035b
        L_0x0138:
            java.lang.String r0 = "40037"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 26
            goto L_0x035b
        L_0x0144:
            java.lang.String r0 = "40036"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 25
            goto L_0x035b
        L_0x0150:
            java.lang.String r0 = "40027"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 22
            goto L_0x035b
        L_0x015c:
            java.lang.String r0 = "40026"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 21
            goto L_0x035b
        L_0x0168:
            java.lang.String r0 = "40025"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 20
            goto L_0x035b
        L_0x0174:
            java.lang.String r0 = "40024"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 19
            goto L_0x035b
        L_0x0180:
            java.lang.String r0 = "40023"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 18
            goto L_0x035b
        L_0x018c:
            java.lang.String r0 = "50001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 69
            goto L_0x035b
        L_0x0198:
            java.lang.String r0 = "45018"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 68
            goto L_0x035b
        L_0x01a4:
            java.lang.String r0 = "45017"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 67
            goto L_0x035b
        L_0x01b0:
            java.lang.String r0 = "45016"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 66
            goto L_0x035b
        L_0x01bc:
            java.lang.String r0 = "45012"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 65
            goto L_0x035b
        L_0x01c8:
            java.lang.String r0 = "45011"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 64
            goto L_0x035b
        L_0x01d4:
            java.lang.String r0 = "45010"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 63
            goto L_0x035b
        L_0x01e0:
            java.lang.String r0 = "45009"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 62
            goto L_0x035b
        L_0x01ec:
            java.lang.String r0 = "45008"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 61
            goto L_0x035b
        L_0x01f8:
            java.lang.String r0 = "45007"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 60
            goto L_0x035b
        L_0x0204:
            java.lang.String r0 = "45006"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 59
            goto L_0x035b
        L_0x0210:
            java.lang.String r0 = "45005"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 58
            goto L_0x035b
        L_0x021c:
            java.lang.String r0 = "45004"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 57
            goto L_0x035b
        L_0x0228:
            java.lang.String r0 = "45003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 56
            goto L_0x035b
        L_0x0234:
            java.lang.String r0 = "45002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 55
            goto L_0x035b
        L_0x0240:
            java.lang.String r0 = "45001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 54
            goto L_0x035b
        L_0x024c:
            java.lang.String r0 = "41010"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 41
            goto L_0x035b
        L_0x0258:
            java.lang.String r0 = "40066"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 31
            goto L_0x035b
        L_0x0264:
            java.lang.String r0 = "40048"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 28
            goto L_0x035b
        L_0x0270:
            java.lang.String r0 = "40039"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 27
            goto L_0x035b
        L_0x027c:
            java.lang.String r0 = "40030"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 24
            goto L_0x035b
        L_0x0288:
            java.lang.String r0 = "40029"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 23
            goto L_0x035b
        L_0x0294:
            java.lang.String r0 = "40020"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 17
            goto L_0x035b
        L_0x02a0:
            java.lang.String r0 = "40019"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 16
            goto L_0x035b
        L_0x02ac:
            java.lang.String r0 = "40018"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 15
            goto L_0x035b
        L_0x02b8:
            java.lang.String r0 = "40017"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 14
            goto L_0x035b
        L_0x02c4:
            java.lang.String r0 = "40016"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 13
            goto L_0x035b
        L_0x02d0:
            java.lang.String r0 = "40015"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 12
            goto L_0x035b
        L_0x02dc:
            java.lang.String r0 = "40014"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 11
            goto L_0x035b
        L_0x02e8:
            java.lang.String r0 = "40013"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 10
            goto L_0x035b
        L_0x02f4:
            java.lang.String r0 = "40012"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 9
            goto L_0x035b
        L_0x02ff:
            java.lang.String r0 = "40011"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 8
            goto L_0x035b
        L_0x030a:
            java.lang.String r0 = "40010"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 7
            goto L_0x035b
        L_0x0314:
            java.lang.String r0 = "40009"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 6
            goto L_0x035b
        L_0x031e:
            java.lang.String r0 = "40008"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 5
            goto L_0x035b
        L_0x0328:
            java.lang.String r0 = "40007"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 4
            goto L_0x035b
        L_0x0332:
            java.lang.String r0 = "40004"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 3
            goto L_0x035b
        L_0x033c:
            java.lang.String r0 = "40003"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 2
            goto L_0x035b
        L_0x0346:
            java.lang.String r0 = "40002"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 1
            goto L_0x035b
        L_0x0350:
            java.lang.String r0 = "40001"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0022
            r0 = 0
            goto L_0x035b
        L_0x035a:
            r0 = -1
        L_0x035b:
            java.lang.String r1 = "不合法的 url 长度"
            switch(r0) {
                case 0: goto L_0x042e;
                case 1: goto L_0x042b;
                case 2: goto L_0x0428;
                case 3: goto L_0x0425;
                case 4: goto L_0x0422;
                case 5: goto L_0x041f;
                case 6: goto L_0x041c;
                case 7: goto L_0x0419;
                case 8: goto L_0x0416;
                case 9: goto L_0x0413;
                case 10: goto L_0x0410;
                case 11: goto L_0x040d;
                case 12: goto L_0x040a;
                case 13: goto L_0x0407;
                case 14: goto L_0x0404;
                case 15: goto L_0x0401;
                case 16: goto L_0x03fe;
                case 17: goto L_0x03fd;
                case 18: goto L_0x03fa;
                case 19: goto L_0x03f7;
                case 20: goto L_0x03f4;
                case 21: goto L_0x03f1;
                case 22: goto L_0x03ee;
                case 23: goto L_0x03eb;
                case 24: goto L_0x03e8;
                case 25: goto L_0x03e5;
                case 26: goto L_0x03e2;
                case 27: goto L_0x03e1;
                case 28: goto L_0x03de;
                case 29: goto L_0x03db;
                case 30: goto L_0x03d8;
                case 31: goto L_0x03d5;
                case 32: goto L_0x03d2;
                case 33: goto L_0x03cf;
                case 34: goto L_0x03cc;
                case 35: goto L_0x03c9;
                case 36: goto L_0x03c6;
                case 37: goto L_0x03c3;
                case 38: goto L_0x03c0;
                case 39: goto L_0x03bd;
                case 40: goto L_0x03ba;
                case 41: goto L_0x03b7;
                case 42: goto L_0x03b4;
                case 43: goto L_0x03b1;
                case 44: goto L_0x03ae;
                case 45: goto L_0x03ab;
                case 46: goto L_0x03a8;
                case 47: goto L_0x03a5;
                case 48: goto L_0x03a2;
                case 49: goto L_0x039f;
                case 50: goto L_0x039c;
                case 51: goto L_0x0399;
                case 52: goto L_0x0396;
                case 53: goto L_0x0393;
                case 54: goto L_0x0390;
                case 55: goto L_0x038d;
                case 56: goto L_0x038a;
                case 57: goto L_0x0387;
                case 58: goto L_0x0384;
                case 59: goto L_0x0381;
                case 60: goto L_0x037e;
                case 61: goto L_0x037b;
                case 62: goto L_0x0378;
                case 63: goto L_0x0375;
                case 64: goto L_0x0372;
                case 65: goto L_0x036f;
                case 66: goto L_0x036c;
                case 67: goto L_0x0369;
                case 68: goto L_0x0366;
                case 69: goto L_0x0363;
                default: goto L_0x0360;
            }
        L_0x0360:
            java.lang.String r0 = ""
            return r0
        L_0x0363:
            java.lang.String r0 = "接口未授权"
            return r0
        L_0x0366:
            java.lang.String r0 = "组数量过多"
            return r0
        L_0x0369:
            java.lang.String r0 = "修改组名过长"
            return r0
        L_0x036c:
            java.lang.String r0 = "不能修改默认组"
            return r0
        L_0x036f:
            java.lang.String r0 = "模板大小超过限制"
            return r0
        L_0x0372:
            java.lang.String r0 = "频率限制"
            return r0
        L_0x0375:
            java.lang.String r0 = "建立菜单被限制"
            return r0
        L_0x0378:
            java.lang.String r0 = "接口调动频率超过限制"
            return r0
        L_0x037b:
            java.lang.String r0 = "article 参数超过限制"
            return r0
        L_0x037e:
            java.lang.String r0 = "播放时间超过限制（语音为 60s 最大）"
            return r0
        L_0x0381:
            java.lang.String r0 = "picurl 参数超过限制"
            return r0
        L_0x0384:
            java.lang.String r0 = "url 参数长度超过限制"
            return r0
        L_0x0387:
            java.lang.String r0 = "description 参数超过限制"
            return r0
        L_0x038a:
            java.lang.String r0 = "title 参数超过限制"
            return r0
        L_0x038d:
            java.lang.String r0 = "content 参数超过限制"
            return r0
        L_0x0390:
            java.lang.String r0 = "二进制文件超过限制"
            return r0
        L_0x0393:
            java.lang.String r0 = "空白的列表"
            return r0
        L_0x0396:
            java.lang.String r0 = "空白的内容"
            return r0
        L_0x0399:
            java.lang.String r0 = "空白的 news 数据"
            return r0
        L_0x039c:
            java.lang.String r0 = "空白的 POST 数据"
            return r0
        L_0x039f:
            java.lang.String r0 = "空白的二进制数据"
            return r0
        L_0x03a2:
            java.lang.String r0 = "需要订阅关系"
            return r0
        L_0x03a5:
            java.lang.String r0 = "需要使用 HTTPS"
            return r0
        L_0x03a8:
            java.lang.String r0 = "需要使用 POST 方法请求"
            return r0
        L_0x03ab:
            java.lang.String r0 = "需要使用 GET 方法请求"
            return r0
        L_0x03ae:
            java.lang.String r0 = "code 超时"
            return r0
        L_0x03b1:
            java.lang.String r0 = "refresh_token 超时"
            return r0
        L_0x03b4:
            java.lang.String r0 = "access_token 超时"
            return r0
        L_0x03b7:
            java.lang.String r0 = "缺失 url 参数"
            return r0
        L_0x03ba:
            java.lang.String r0 = "缺失 openid 参数"
            return r0
        L_0x03bd:
            java.lang.String r0 = "缺失 code 参数"
            return r0
        L_0x03c0:
            java.lang.String r0 = "缺失子菜单数据"
            return r0
        L_0x03c3:
            java.lang.String r0 = "缺失 media_id 参数"
            return r0
        L_0x03c6:
            java.lang.String r0 = "缺失二进制媒体文件"
            return r0
        L_0x03c9:
            java.lang.String r0 = "缺失 secret 参数"
            return r0
        L_0x03cc:
            java.lang.String r0 = "缺失 refresh_token 参数"
            return r0
        L_0x03cf:
            java.lang.String r0 = "缺失 appid 参数"
            return r0
        L_0x03d2:
            java.lang.String r0 = "缺失 access_token 参数"
            return r0
        L_0x03d5:
            java.lang.String r0 = "不合法的 url"
            return r0
        L_0x03d8:
            java.lang.String r0 = "不合法的菜单按钮 url 域名"
            return r0
        L_0x03db:
            java.lang.String r0 = "不合法的子菜单按钮 url 域名"
            return r0
        L_0x03de:
            java.lang.String r0 = "不合法的 url 域名"
            return r0
        L_0x03e1:
            return r1
        L_0x03e2:
            java.lang.String r0 = "不合法的 template_id"
            return r0
        L_0x03e5:
            java.lang.String r0 = "不合法的 template_id 长度"
            return r0
        L_0x03e8:
            java.lang.String r0 = "不合法的 refresh_token"
            return r0
        L_0x03eb:
            java.lang.String r0 = "不合法或已过期的 code"
            return r0
        L_0x03ee:
            java.lang.String r0 = "不合法的子菜单按钮 url 长度"
            return r0
        L_0x03f1:
            java.lang.String r0 = "不合法的子菜单按钮 KEY 长度"
            return r0
        L_0x03f4:
            java.lang.String r0 = "不合法的子菜单按钮名称长度"
            return r0
        L_0x03f7:
            java.lang.String r0 = "不合法的子菜单类型"
            return r0
        L_0x03fa:
            java.lang.String r0 = "不合法的子菜单按钮个数"
            return r0
        L_0x03fd:
            return r1
        L_0x03fe:
            java.lang.String r0 = "不合法的按钮 KEY 长度"
            return r0
        L_0x0401:
            java.lang.String r0 = "不合法的按钮名称长度"
            return r0
        L_0x0404:
            java.lang.String r0 = "不合法的按钮类型"
            return r0
        L_0x0407:
            java.lang.String r0 = "不合法的菜单按钮个数"
            return r0
        L_0x040a:
            java.lang.String r0 = "不合法的菜单类型"
            return r0
        L_0x040d:
            java.lang.String r0 = "不合法的 access_token"
            return r0
        L_0x0410:
            java.lang.String r0 = "不合法的 AppID"
            return r0
        L_0x0413:
            java.lang.String r0 = "不合法的缩略图大小"
            return r0
        L_0x0416:
            java.lang.String r0 = "不合法的视频大小"
            return r0
        L_0x0419:
            java.lang.String r0 = "不合法的语音大小"
            return r0
        L_0x041c:
            java.lang.String r0 = "不合法的图片大小"
            return r0
        L_0x041f:
            java.lang.String r0 = "不合法的 message_type"
            return r0
        L_0x0422:
            java.lang.String r0 = "不合法的 media_id"
            return r0
        L_0x0425:
            java.lang.String r0 = "不合法的媒体文件类型"
            return r0
        L_0x0428:
            java.lang.String r0 = "不合法的 OpenID"
            return r0
        L_0x042b:
            java.lang.String r0 = "不合法的 grant_type"
            return r0
        L_0x042e:
            java.lang.String r0 = "不合法的调用凭证"
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.hui.wallet_public.utils.WalletErrorUtil.getWeChatPayErrorDescription(java.lang.String):java.lang.String");
    }
}
