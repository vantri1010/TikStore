package com.king.zxing.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.king.zxing.Preferences;

public enum FrontLightMode {
    ON,
    AUTO,
    OFF;

    private static FrontLightMode parse(String modeString) {
        return modeString == null ? AUTO : valueOf(modeString);
    }

    public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
        return parse(sharedPrefs.getString(Preferences.KEY_FRONT_LIGHT_MODE, AUTO.toString()));
    }

    public static void put(Context context, FrontLightMode mode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Preferences.KEY_FRONT_LIGHT_MODE, mode.toString()).commit();
    }
}
