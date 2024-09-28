package com.preview.util.notch;

import android.os.Build;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class OSUtils {
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_QIKU = "QIKU";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    private static final String TAG = OSUtils.class.getSimpleName();
    private static String sName;
    private static String sVersion;

    public static boolean isEmui() {
        return check("EMUI");
    }

    public static boolean isMiui() {
        return check("MIUI");
    }

    public static boolean isVivo() {
        return check("VIVO");
    }

    public static boolean isOppo() {
        return check("OPPO");
    }

    public static boolean isFlyme() {
        return check("FLYME");
    }

    public static boolean is360() {
        return check("QIKU") || check("360");
    }

    public static boolean isSmartisan() {
        return check("SMARTISAN");
    }

    public static String getName() {
        if (sName == null) {
            check("");
        }
        return sName;
    }

    public static String getVersion() {
        if (sVersion == null) {
            check("");
        }
        return sVersion;
    }

    public static boolean check(String rom) {
        String str = sName;
        if (str != null) {
            return str.equals(rom);
        }
        String prop = getProp(KEY_VERSION_MIUI);
        sVersion = prop;
        if (!TextUtils.isEmpty(prop)) {
            sName = "MIUI";
        } else {
            String prop2 = getProp(KEY_VERSION_EMUI);
            sVersion = prop2;
            if (!TextUtils.isEmpty(prop2)) {
                sName = "EMUI";
            } else {
                String prop3 = getProp(KEY_VERSION_OPPO);
                sVersion = prop3;
                if (!TextUtils.isEmpty(prop3)) {
                    sName = "OPPO";
                } else {
                    String prop4 = getProp(KEY_VERSION_VIVO);
                    sVersion = prop4;
                    if (!TextUtils.isEmpty(prop4)) {
                        sName = "VIVO";
                    } else {
                        String prop5 = getProp(KEY_VERSION_SMARTISAN);
                        sVersion = prop5;
                        if (!TextUtils.isEmpty(prop5)) {
                            sName = "SMARTISAN";
                        } else {
                            String str2 = Build.DISPLAY;
                            sVersion = str2;
                            if (str2.toUpperCase().contains("FLYME")) {
                                sName = "FLYME";
                            } else {
                                sVersion = "unknown";
                                sName = Build.MANUFACTURER.toUpperCase();
                            }
                        }
                    }
                }
            }
        }
        return sName.equals(rom);
    }

    public static String getProp(String name) {
        BufferedReader input = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            input = new BufferedReader(new InputStreamReader(runtime.exec("getprop " + name).getInputStream()), 1024);
            String line = input.readLine();
            input.close();
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return line;
        } catch (IOException e2) {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }
}
