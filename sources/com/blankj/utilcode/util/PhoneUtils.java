package com.blankj.utilcode.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;

public final class PhoneUtils {
    private PhoneUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isPhone() {
        return getTelephonyManager().getPhoneType() != 0;
    }

    public static String getDeviceId() {
        if (Build.VERSION.SDK_INT >= 29) {
            return "";
        }
        TelephonyManager tm = getTelephonyManager();
        String deviceId = tm.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        if (Build.VERSION.SDK_INT < 26) {
            return "";
        }
        String imei = tm.getImei();
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        String meid = tm.getMeid();
        if (TextUtils.isEmpty(meid)) {
            return "";
        }
        return meid;
    }

    public static String getSerial() {
        return Build.VERSION.SDK_INT >= 26 ? Build.getSerial() : Build.SERIAL;
    }

    public static String getIMEI() {
        return getImeiOrMeid(true);
    }

    public static String getMEID() {
        return getImeiOrMeid(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x00e3 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getImeiOrMeid(boolean r13) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            java.lang.String r1 = ""
            r2 = 29
            if (r0 < r2) goto L_0x0009
            return r1
        L_0x0009:
            android.telephony.TelephonyManager r0 = getTelephonyManager()
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 26
            r4 = 1
            r5 = 0
            if (r2 < r3) goto L_0x0031
            if (r13 == 0) goto L_0x0024
            java.lang.String r1 = r0.getImei(r5)
            java.lang.String r2 = r0.getImei(r4)
            java.lang.String r1 = getMinOne(r1, r2)
            return r1
        L_0x0024:
            java.lang.String r1 = r0.getMeid(r5)
            java.lang.String r2 = r0.getMeid(r4)
            java.lang.String r1 = getMinOne(r1, r2)
            return r1
        L_0x0031:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            r6 = 15
            r7 = 14
            if (r2 < r3) goto L_0x00cb
            if (r13 == 0) goto L_0x0040
            java.lang.String r1 = "ril.gsm.imei"
            goto L_0x0042
        L_0x0040:
            java.lang.String r1 = "ril.cdma.meid"
        L_0x0042:
            java.lang.String r1 = getSystemPropertyByReflect(r1)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            r3 = 2
            if (r2 != 0) goto L_0x0062
            java.lang.String r2 = ","
            java.lang.String[] r2 = r1.split(r2)
            int r6 = r2.length
            if (r6 != r3) goto L_0x005f
            r3 = r2[r5]
            r4 = r2[r4]
            java.lang.String r3 = getMinOne(r3, r4)
            return r3
        L_0x005f:
            r3 = r2[r5]
            return r3
        L_0x0062:
            java.lang.String r2 = r0.getDeviceId()
            java.lang.String r8 = ""
            java.lang.Class r9 = r0.getClass()     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.String r10 = "getDeviceId"
            java.lang.Class[] r11 = new java.lang.Class[r4]     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.Class r12 = java.lang.Integer.TYPE     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            r11[r5] = r12     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.reflect.Method r9 = r9.getMethod(r10, r11)     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.Object[] r10 = new java.lang.Object[r4]     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            if (r13 == 0) goto L_0x007d
            goto L_0x007e
        L_0x007d:
            r4 = 2
        L_0x007e:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            r10[r5] = r3     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.Object r3 = r9.invoke(r0, r10)     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ NoSuchMethodException -> 0x0096, IllegalAccessException -> 0x0091, InvocationTargetException -> 0x008c }
            r8 = r3
        L_0x008b:
            goto L_0x009b
        L_0x008c:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x009b
        L_0x0091:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x008b
        L_0x0096:
            r3 = move-exception
            r3.printStackTrace()
            goto L_0x008b
        L_0x009b:
            if (r13 == 0) goto L_0x00b2
            if (r2 == 0) goto L_0x00a7
            int r3 = r2.length()
            if (r3 >= r6) goto L_0x00a7
            java.lang.String r2 = ""
        L_0x00a7:
            if (r8 == 0) goto L_0x00c6
            int r3 = r8.length()
            if (r3 >= r6) goto L_0x00c6
            java.lang.String r8 = ""
            goto L_0x00c6
        L_0x00b2:
            if (r2 == 0) goto L_0x00bc
            int r3 = r2.length()
            if (r3 != r7) goto L_0x00bc
            java.lang.String r2 = ""
        L_0x00bc:
            if (r8 == 0) goto L_0x00c6
            int r3 = r8.length()
            if (r3 != r7) goto L_0x00c6
            java.lang.String r8 = ""
        L_0x00c6:
            java.lang.String r3 = getMinOne(r2, r8)
            return r3
        L_0x00cb:
            java.lang.String r2 = r0.getDeviceId()
            if (r13 == 0) goto L_0x00da
            if (r2 == 0) goto L_0x00e3
            int r3 = r2.length()
            if (r3 < r6) goto L_0x00e3
            return r2
        L_0x00da:
            if (r2 == 0) goto L_0x00e3
            int r3 = r2.length()
            if (r3 != r7) goto L_0x00e3
            return r2
        L_0x00e3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.util.PhoneUtils.getImeiOrMeid(boolean):java.lang.String");
    }

    private static String getMinOne(String s0, String s1) {
        boolean empty0 = TextUtils.isEmpty(s0);
        boolean empty1 = TextUtils.isEmpty(s1);
        if (empty0 && empty1) {
            return "";
        }
        if (empty0 || empty1) {
            if (!empty0) {
                return s0;
            }
            return s1;
        } else if (s0.compareTo(s1) <= 0) {
            return s0;
        } else {
            return s1;
        }
    }

    private static String getSystemPropertyByReflect(String key) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            return (String) clz.getMethod("get", new Class[]{String.class, String.class}).invoke(clz, new Object[]{key, ""});
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIMSI() {
        return getTelephonyManager().getSubscriberId();
    }

    public static int getPhoneType() {
        return getTelephonyManager().getPhoneType();
    }

    public static boolean isSimCardReady() {
        return getTelephonyManager().getSimState() == 5;
    }

    public static String getSimOperatorName() {
        return getTelephonyManager().getSimOperatorName();
    }

    public static String getSimOperatorByMnc() {
        String operator = getTelephonyManager().getSimOperator();
        if (operator == null) {
            return "";
        }
        char c = 65535;
        int hashCode = operator.hashCode();
        if (hashCode != 49679479) {
            if (hashCode != 49679502) {
                if (hashCode != 49679532) {
                    switch (hashCode) {
                        case 49679470:
                            if (operator.equals("46000")) {
                                c = 0;
                                break;
                            }
                            break;
                        case 49679471:
                            if (operator.equals("46001")) {
                                c = 4;
                                break;
                            }
                            break;
                        case 49679472:
                            if (operator.equals("46002")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 49679473:
                            if (operator.equals("46003")) {
                                c = 7;
                                break;
                            }
                            break;
                        default:
                            switch (hashCode) {
                                case 49679475:
                                    if (operator.equals("46005")) {
                                        c = 8;
                                        break;
                                    }
                                    break;
                                case 49679476:
                                    if (operator.equals("46006")) {
                                        c = 5;
                                        break;
                                    }
                                    break;
                                case 49679477:
                                    if (operator.equals("46007")) {
                                        c = 2;
                                        break;
                                    }
                                    break;
                            }
                    }
                } else if (operator.equals("46020")) {
                    c = 3;
                }
            } else if (operator.equals("46011")) {
                c = 9;
            }
        } else if (operator.equals("46009")) {
            c = 6;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
                return "中国移动";
            case 4:
            case 5:
            case 6:
                return "中国联通";
            case 7:
            case 8:
            case 9:
                return "中国电信";
            default:
                return operator;
        }
    }

    public static boolean dial(String phoneNumber) {
        Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + phoneNumber));
        if (!isIntentAvailable(intent)) {
            return false;
        }
        Utils.getApp().startActivity(intent.addFlags(C.ENCODING_PCM_MU_LAW));
        return true;
    }

    public static boolean call(String phoneNumber) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber));
        if (!isIntentAvailable(intent)) {
            return false;
        }
        Utils.getApp().startActivity(intent.addFlags(C.ENCODING_PCM_MU_LAW));
        return true;
    }

    public static boolean sendSms(String phoneNumber, String content) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + phoneNumber));
        if (!isIntentAvailable(intent)) {
            return false;
        }
        intent.putExtra("sms_body", content);
        Utils.getApp().startActivity(intent.addFlags(C.ENCODING_PCM_MU_LAW));
        return true;
    }

    private static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) Utils.getApp().getSystemService("phone");
    }

    private static boolean isIntentAvailable(Intent intent) {
        return Utils.getApp().getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
    }
}
