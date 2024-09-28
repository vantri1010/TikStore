package com.bjz.comm.net.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.bjz.comm.net.SPConstant;

public class AppPreferenceUtil {
    private static Context mContext;
    private static SharedPreferences mSharedPreferences;

    public static void initSharedPreferences(Context context) {
        mContext = context;
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(SPConstant.SP_SYSTEM_CONFIG, 0);
        }
    }

    public static String getStringWithId(int id) {
        return mContext.getString(id);
    }

    public static void putInt(String tag, int vaule) {
        mSharedPreferences.edit().putInt(tag, vaule).apply();
    }

    public static int getInt(String tag, int defVaule) {
        return mSharedPreferences.getInt(tag, defVaule);
    }

    public static void putString(String tag, String vaule) {
        mSharedPreferences.edit().putString(tag, vaule).apply();
    }

    public static String getString(String tag, String defVaule) {
        return mSharedPreferences.getString(tag, defVaule);
    }

    public static void putBoolean(String tag, boolean b) {
        mSharedPreferences.edit().putBoolean(tag, b).apply();
    }

    public static boolean getBoolean(String tag, boolean defVaule) {
        return mSharedPreferences.getBoolean(tag, defVaule);
    }
}
