package com.bjz.comm.net.premission;

public interface PermissionUtils {
    public static final String BODY_SENSORS = "android.permission.BODY_SENSORS";
    public static final String CALENDAR = "android.permission.READ_CALENDAR";
    public static final String CAMERA = "android.permission.CAMERA";
    public static final String[] CAMERA_AND_FINE_LOCATION = {"android.permission.CAMERA", "android.permission.ACCESS_FINE_LOCATION"};
    public static final String EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String LINKMAIN = "android.permission.READ_CONTACTS";
    public static final String PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String RECORD_AUDIO = "android.permission.RECORD_AUDIO";
    public static final String SMS = "android.permission.READ_SMS";
}
