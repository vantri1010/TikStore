package com.blankj.utilcode.constant;

import com.bjz.comm.net.premission.PermissionUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PermissionConstants {
    public static final String CALENDAR = "android.permission-group.CALENDAR";
    public static final String CAMERA = "android.permission-group.CAMERA";
    public static final String CONTACTS = "android.permission-group.CONTACTS";
    private static final String[] GROUP_CALENDAR = {PermissionUtils.CALENDAR, "android.permission.WRITE_CALENDAR"};
    private static final String[] GROUP_CAMERA = {"android.permission.CAMERA"};
    private static final String[] GROUP_CONTACTS = {PermissionUtils.LINKMAIN, "android.permission.WRITE_CONTACTS", im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_GET_ACCOUNTS};
    private static final String[] GROUP_LOCATION = {"android.permission.ACCESS_FINE_LOCATION", im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_ACCESS_COARSE_LOCATION};
    private static final String[] GROUP_MICROPHONE = {"android.permission.RECORD_AUDIO"};
    private static final String[] GROUP_PHONE = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PHONE_NUMBERS", im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_CALL_PHONE, "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.ANSWER_PHONE_CALLS"};
    private static final String[] GROUP_PHONE_BELOW_O = {"android.permission.READ_PHONE_STATE", "android.permission.READ_PHONE_NUMBERS", im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_CALL_PHONE, "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP", "android.permission.PROCESS_OUTGOING_CALLS"};
    private static final String[] GROUP_SENSORS = {PermissionUtils.BODY_SENSORS};
    private static final String[] GROUP_SMS = {"android.permission.SEND_SMS", "android.permission.RECEIVE_SMS", PermissionUtils.SMS, "android.permission.RECEIVE_WAP_PUSH", "android.permission.RECEIVE_MMS"};
    private static final String[] GROUP_STORAGE = {im.bclpbkiauv.ui.hui.visualcall.PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE, "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final String LOCATION = "android.permission-group.LOCATION";
    public static final String MICROPHONE = "android.permission-group.MICROPHONE";
    public static final String PHONE = "android.permission-group.PHONE";
    public static final String SENSORS = "android.permission-group.SENSORS";
    public static final String SMS = "android.permission-group.SMS";
    public static final String STORAGE = "android.permission-group.STORAGE";

    @Retention(RetentionPolicy.SOURCE)
    public @interface Permission {
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String[] getPermissions(java.lang.String r3) {
        /*
            int r0 = r3.hashCode()
            r1 = 0
            r2 = 1
            switch(r0) {
                case -1639857183: goto L_0x005b;
                case -1410061184: goto L_0x0051;
                case -1250730292: goto L_0x0047;
                case -1140935117: goto L_0x003d;
                case 421761675: goto L_0x0033;
                case 828638019: goto L_0x0029;
                case 852078861: goto L_0x001e;
                case 1581272376: goto L_0x0014;
                case 1795181803: goto L_0x000a;
                default: goto L_0x0009;
            }
        L_0x0009:
            goto L_0x0065
        L_0x000a:
            java.lang.String r0 = "android.permission-group.SMS"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 7
            goto L_0x0066
        L_0x0014:
            java.lang.String r0 = "android.permission-group.MICROPHONE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 4
            goto L_0x0066
        L_0x001e:
            java.lang.String r0 = "android.permission-group.STORAGE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 8
            goto L_0x0066
        L_0x0029:
            java.lang.String r0 = "android.permission-group.LOCATION"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 3
            goto L_0x0066
        L_0x0033:
            java.lang.String r0 = "android.permission-group.SENSORS"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 6
            goto L_0x0066
        L_0x003d:
            java.lang.String r0 = "android.permission-group.CAMERA"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 1
            goto L_0x0066
        L_0x0047:
            java.lang.String r0 = "android.permission-group.CALENDAR"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 0
            goto L_0x0066
        L_0x0051:
            java.lang.String r0 = "android.permission-group.PHONE"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 5
            goto L_0x0066
        L_0x005b:
            java.lang.String r0 = "android.permission-group.CONTACTS"
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x0009
            r0 = 2
            goto L_0x0066
        L_0x0065:
            r0 = -1
        L_0x0066:
            switch(r0) {
                case 0: goto L_0x008f;
                case 1: goto L_0x008c;
                case 2: goto L_0x0089;
                case 3: goto L_0x0086;
                case 4: goto L_0x0083;
                case 5: goto L_0x0077;
                case 6: goto L_0x0074;
                case 7: goto L_0x0071;
                case 8: goto L_0x006e;
                default: goto L_0x0069;
            }
        L_0x0069:
            java.lang.String[] r0 = new java.lang.String[r2]
            r0[r1] = r3
            return r0
        L_0x006e:
            java.lang.String[] r0 = GROUP_STORAGE
            return r0
        L_0x0071:
            java.lang.String[] r0 = GROUP_SMS
            return r0
        L_0x0074:
            java.lang.String[] r0 = GROUP_SENSORS
            return r0
        L_0x0077:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x0080
            java.lang.String[] r0 = GROUP_PHONE_BELOW_O
            return r0
        L_0x0080:
            java.lang.String[] r0 = GROUP_PHONE
            return r0
        L_0x0083:
            java.lang.String[] r0 = GROUP_MICROPHONE
            return r0
        L_0x0086:
            java.lang.String[] r0 = GROUP_LOCATION
            return r0
        L_0x0089:
            java.lang.String[] r0 = GROUP_CONTACTS
            return r0
        L_0x008c:
            java.lang.String[] r0 = GROUP_CAMERA
            return r0
        L_0x008f:
            java.lang.String[] r0 = GROUP_CALENDAR
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.blankj.utilcode.constant.PermissionConstants.getPermissions(java.lang.String):java.lang.String[]");
    }
}
