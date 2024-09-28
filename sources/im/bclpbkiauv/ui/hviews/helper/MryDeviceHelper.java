package im.bclpbkiauv.ui.hviews.helper;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import im.bclpbkiauv.messenger.FileLog;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MryDeviceHelper {
    private static final String BRAND = Build.BRAND.toLowerCase();
    private static final String ESSENTIAL = "essential";
    private static final String FLYME = "flyme";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String[] MEIZUBOARD = {"m9", "M9", "mx", "MX"};
    private static final String TAG = "QMUIDeviceHelper";
    private static final String ZTEC2016 = "zte c2016";
    private static final String ZUKZ1 = "zuk z1";
    private static String sFlymeVersionName;
    private static boolean sIsTabletChecked = false;
    private static boolean sIsTabletValue = false;
    private static String sMiuiVersionName;

    static {
        Properties properties = new Properties();
        if (Build.VERSION.SDK_INT < 26) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Exception e) {
                FileLog.e("QMUIDeviceHelper =====> " + e + ". read file error");
            } catch (Throwable th) {
                MryLangHelper.close(fileInputStream);
                throw th;
            }
            MryLangHelper.close(fileInputStream);
        }
        try {
            Method getMethod = Class.forName("android.os.SystemProperties").getDeclaredMethod("get", new Class[]{String.class});
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
        } catch (Exception e2) {
            FileLog.e("QMUIDeviceHelper =====> " + e2 + ". read SystemProperties error");
        }
    }

    private static boolean _isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    public static boolean isTablet(Context context) {
        if (sIsTabletChecked) {
            return sIsTabletValue;
        }
        boolean _isTablet = _isTablet(context);
        sIsTabletValue = _isTablet;
        sIsTabletChecked = true;
        return _isTablet;
    }

    public static boolean isFlyme() {
        return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
    }

    public static boolean isMIUI() {
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeLowerThan8() {
        String versionString;
        boolean isLower = false;
        String str = sFlymeVersionName;
        if (str != null && !str.equals("")) {
            Matcher matcher = Pattern.compile("(\\d+\\.){2}\\d").matcher(sFlymeVersionName);
            if (matcher.find() && (versionString = matcher.group()) != null && !versionString.equals("")) {
                String[] version = versionString.split("\\.");
                if (version.length >= 1 && Integer.parseInt(version[0]) < 8) {
                    isLower = true;
                }
            }
        }
        if (!isMeizu() || !isLower) {
            return false;
        }
        return true;
    }

    public static boolean isFlymeVersionHigher5_2_4() {
        String versionString;
        boolean isHigher = true;
        String str = sFlymeVersionName;
        if (str != null && !str.equals("")) {
            Matcher matcher = Pattern.compile("(\\d+\\.){2}\\d").matcher(sFlymeVersionName);
            if (matcher.find() && (versionString = matcher.group()) != null && !versionString.equals("")) {
                String[] version = versionString.split("\\.");
                if (version.length == 3) {
                    int majorVersion = Integer.parseInt(version[0]);
                    if (majorVersion < 5) {
                        isHigher = false;
                    } else if (majorVersion == 5) {
                        int minorVersion = Integer.parseInt(version[1]);
                        if (minorVersion < 2) {
                            isHigher = false;
                        } else if (minorVersion == 2 && Integer.parseInt(version[2]) < 4) {
                            isHigher = false;
                        }
                    }
                }
            }
        }
        if (!isMeizu() || !isHigher) {
            return false;
        }
        return true;
    }

    public static boolean isMeizu() {
        return isPhone(MEIZUBOARD) || isFlyme();
    }

    public static boolean isXiaomi() {
        return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
    }

    public static boolean isVivo() {
        return BRAND.contains("vivo") || BRAND.contains("bbk");
    }

    public static boolean isOppo() {
        return BRAND.contains("oppo");
    }

    public static boolean isHuawei() {
        return BRAND.contains("huawei") || BRAND.contains("honor");
    }

    public static boolean isEssentialPhone() {
        return BRAND.contains(ESSENTIAL);
    }

    public static boolean isZUKZ1() {
        String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZUKZ1);
    }

    public static boolean isZTKC2016() {
        String board = Build.MODEL;
        return board != null && board.toLowerCase().contains(ZTEC2016);
    }

    private static boolean isPhone(String[] boards) {
        String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        for (String board1 : boards) {
            if (board.equals(board1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFloatWindowOpAllowed(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return checkOp(context, 24);
        }
        try {
            return (context.getApplicationInfo().flags & 134217728) == 134217728;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService("appops");
            try {
                if (((Integer) manager.getClass().getDeclaredMethod("checkOp", new Class[]{Integer.TYPE, Integer.TYPE, String.class}).invoke(manager, new Object[]{Integer.valueOf(op), Integer.valueOf(Binder.getCallingUid()), context.getPackageName()})).intValue() == 0) {
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke((Object) null, new Object[]{key});
            } catch (Exception e) {
            }
        }
        if (name != null) {
            return name.toLowerCase();
        }
        return name;
    }
}
