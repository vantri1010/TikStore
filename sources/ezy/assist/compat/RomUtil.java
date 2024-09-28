package ezy.assist.compat;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtil {
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level";
    private static final String KEY_FLYME_FLYME = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_PUBLISHED = "ro.flyme.published";
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_MIUI_HANDY_MODE_SF = "ro.miui.has_handy_mode_sf";
    private static final String KEY_MIUI_REAL_BLUR = "ro.miui.has_real_blur";
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_FLYME = "ro.build.display.id";
    private static final String KEY_VERSION_GIONEE = "ro.gn.sv.version";
    private static final String KEY_VERSION_LENOVO = "ro.lenovo.lvp.version";
    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
    private static final String KEY_VIVO_OS_NAME = "ro.vivo.os.name";
    private static final String KEY_VIVO_OS_VERSION = "ro.vivo.os.version";
    private static final String KEY_VIVO_ROM_VERSION = "ro.vivo.rom.version";
    public static final String ROM_EMUI = "EMUI";
    public static final String ROM_FLYME = "FLYME";
    public static final String ROM_LENOVO = "LENOVO";
    public static final String ROM_MIUI = "MIUI";
    public static final String ROM_OPPO = "OPPO";
    public static final String ROM_QIKU = "QIKU";
    public static final String ROM_SAMSUNG = "SAMSUNG";
    public static final String ROM_SMARTISAN = "SMARTISAN";
    public static final String ROM_VIVO = "VIVO";
    private static final String TAG = "RomUtil";
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

    public static boolean isQiku() {
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
                    String prop4 = getProp("ro.vivo.os.version");
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
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read prop " + name, ex);
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
    }
}
