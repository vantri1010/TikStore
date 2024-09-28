package com.blankj.utilcode.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Locale;

public class LanguageUtils {
    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";

    private LanguageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void applySystemLanguageInAppOnCreate() {
        if (!isAppliedSystemLanguage()) {
            applyLanguage(Resources.getSystem().getConfiguration().locale, "", true, false);
        }
    }

    public static void applyLanguageInAppOnCreate(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("Argument 'locale' of type Locale (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (!isAppliedLanguage()) {
            applyLanguage(locale, "", false, false);
        }
    }

    public static void applySystemLanguage(Class<? extends Activity> activityClz) {
        applyLanguage(Resources.getSystem().getConfiguration().locale, activityClz, true, true);
    }

    public static void applySystemLanguage(String activityClassName) {
        applyLanguage(Resources.getSystem().getConfiguration().locale, activityClassName, true, true);
    }

    public static void applyLanguage(Locale locale, Class<? extends Activity> activityClz) {
        if (locale != null) {
            applyLanguage(locale, activityClz, false, true);
            return;
        }
        throw new NullPointerException("Argument 'locale' of type Locale (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void applyLanguage(Locale locale, String activityClassName) {
        if (locale != null) {
            applyLanguage(locale, activityClassName, false, true);
            return;
        }
        throw new NullPointerException("Argument 'locale' of type Locale (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static void applyLanguage(Locale locale, Class<? extends Activity> activityClz, boolean isFollowSystem, boolean isNeedStartActivity) {
        if (locale == null) {
            throw new NullPointerException("Argument 'locale' of type Locale (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (activityClz == null) {
            applyLanguage(locale, "", isFollowSystem, isNeedStartActivity);
        } else {
            applyLanguage(locale, activityClz.getName(), isFollowSystem, isNeedStartActivity);
        }
    }

    private static void applyLanguage(Locale locale, String activityClassName, boolean isFollowSystem, boolean isNeedStartActivity) {
        if (locale != null) {
            if (isFollowSystem) {
                Utils.getSpUtils4Utils().put(KEY_LOCALE, VALUE_FOLLOW_SYSTEM);
            } else {
                String localLanguage = locale.getLanguage();
                String localCountry = locale.getCountry();
                SPUtils spUtils4Utils = Utils.getSpUtils4Utils();
                spUtils4Utils.put(KEY_LOCALE, localLanguage + "$" + localCountry);
            }
            updateLanguage(Utils.getApp(), locale);
            if (isNeedStartActivity) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(Utils.getApp(), TextUtils.isEmpty(activityClassName) ? getLauncherActivity() : activityClassName));
                intent.addFlags(335577088);
                Utils.getApp().startActivity(intent);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'locale' of type Locale (#0 out of 4, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static boolean isAppliedSystemLanguage() {
        return VALUE_FOLLOW_SYSTEM.equals(Utils.getSpUtils4Utils().getString(KEY_LOCALE));
    }

    public static boolean isAppliedLanguage() {
        return !TextUtils.isEmpty(Utils.getSpUtils4Utils().getString(KEY_LOCALE));
    }

    public static Locale getCurrentLocale() {
        return Utils.getApp().getResources().getConfiguration().locale;
    }

    static void applyLanguage(Activity activity) {
        if (activity != null) {
            String spLocale = Utils.getSpUtils4Utils().getString(KEY_LOCALE);
            if (!TextUtils.isEmpty(spLocale)) {
                if (VALUE_FOLLOW_SYSTEM.equals(spLocale)) {
                    Locale sysLocale = Resources.getSystem().getConfiguration().locale;
                    updateLanguage(Utils.getApp(), sysLocale);
                    updateLanguage(activity, sysLocale);
                    return;
                }
                String[] language_country = spLocale.split("\\$");
                if (language_country.length != 2) {
                    Log.e("LanguageUtils", "The string of " + spLocale + " is not in the correct format.");
                    return;
                }
                Locale settingLocale = new Locale(language_country[0], language_country[1]);
                updateLanguage(Utils.getApp(), settingLocale);
                updateLanguage(activity, settingLocale);
                return;
            }
            return;
        }
        throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    private static void updateLanguage(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        Locale contextLocale = config.locale;
        if (!equals(contextLocale.getLanguage(), locale.getLanguage()) || !equals(contextLocale.getCountry(), locale.getCountry())) {
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (Build.VERSION.SDK_INT >= 17) {
                config.setLocale(locale);
                context.createConfigurationContext(config);
            } else {
                config.locale = locale;
            }
            resources.updateConfiguration(config, dm);
        }
    }

    private static boolean equals(CharSequence s1, CharSequence s2) {
        if (s1 == s2) {
            return true;
        }
        if (!(s1 == null || s2 == null)) {
            int length = s1.length();
            int length2 = length;
            if (length == s2.length()) {
                if ((s1 instanceof String) && (s2 instanceof String)) {
                    return s1.equals(s2);
                }
                for (int i = 0; i < length2; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static String getLauncherActivity() {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(Utils.getApp().getPackageName());
        ResolveInfo next = Utils.getApp().getPackageManager().queryIntentActivities(intent, 0).iterator().next();
        if (next != null) {
            return next.activityInfo.name;
        }
        return "no launcher activity";
    }
}
