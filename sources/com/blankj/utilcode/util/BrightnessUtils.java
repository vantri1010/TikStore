package com.blankj.utilcode.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public final class BrightnessUtils {
    private BrightnessUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isAutoBrightnessEnabled() {
        try {
            if (Settings.System.getInt(Utils.getApp().getContentResolver(), "screen_brightness_mode") == 1) {
                return true;
            }
            return false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean setAutoBrightnessEnabled(boolean enabled) {
        return Settings.System.putInt(Utils.getApp().getContentResolver(), "screen_brightness_mode", enabled);
    }

    public static int getBrightness() {
        try {
            return Settings.System.getInt(Utils.getApp().getContentResolver(), "screen_brightness");
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean setBrightness(int brightness) {
        ContentResolver resolver = Utils.getApp().getContentResolver();
        boolean b = Settings.System.putInt(resolver, "screen_brightness", brightness);
        resolver.notifyChange(Settings.System.getUriFor("screen_brightness"), (ContentObserver) null);
        return b;
    }

    public static void setWindowBrightness(Window window, int brightness) {
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = ((float) brightness) / 255.0f;
            window.setAttributes(lp);
            return;
        }
        throw new NullPointerException("Argument 'window' of type Window (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static int getWindowBrightness(Window window) {
        float brightness = window.getAttributes().screenBrightness;
        if (brightness < 0.0f) {
            return getBrightness();
        }
        return (int) (255.0f * brightness);
    }
}
