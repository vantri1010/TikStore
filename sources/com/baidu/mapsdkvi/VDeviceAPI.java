package com.baidu.mapsdkvi;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class VDeviceAPI {
    private static PowerManager.WakeLock a = null;
    private static BroadcastReceiver b = null;

    public static String getAppVersion() {
        try {
            return b.a().getPackageManager().getPackageInfo(b.a().getApplicationInfo().packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static long getAvailableMemory() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) b.a().getSystemService("activity")).getMemoryInfo(memoryInfo);
        return memoryInfo.availMem / 1024;
    }

    public static String getCachePath() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    public static int getCurrentNetworkType() {
        NetworkInfo networkInfo;
        try {
            networkInfo = ((ConnectivityManager) b.a().getSystemService("connectivity")).getActiveNetworkInfo();
        } catch (Exception e) {
            networkInfo = null;
        }
        if (networkInfo == null) {
            return 0;
        }
        int type = networkInfo.getType();
        if (type != 0) {
            return type != 1 ? 1 : 2;
        }
        return 3;
    }

    public static long getFreeSpace() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getPath());
        return (((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks())) / 1024;
    }

    public static String getModuleFileName() {
        return b.a().getFilesDir().getAbsolutePath();
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0024 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.baidu.mapsdkvi.c getNetworkInfo(int r3) {
        /*
            android.content.Context r0 = com.baidu.mapsdkvi.b.a()
            java.lang.String r1 = "connectivity"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.net.ConnectivityManager r0 = (android.net.ConnectivityManager) r0
            r1 = 2
            r2 = 0
            if (r3 == r1) goto L_0x0017
            r1 = 3
            if (r3 == r1) goto L_0x0015
            r3 = r2
            goto L_0x001c
        L_0x0015:
            r3 = 0
            goto L_0x0018
        L_0x0017:
            r3 = 1
        L_0x0018:
            android.net.NetworkInfo r3 = r0.getNetworkInfo(r3)
        L_0x001c:
            if (r3 == 0) goto L_0x0024
            com.baidu.mapsdkvi.c r0 = new com.baidu.mapsdkvi.c
            r0.<init>(r3)
            return r0
        L_0x0024:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.mapsdkvi.VDeviceAPI.getNetworkInfo(int):com.baidu.mapsdkvi.c");
    }

    public static String getOsVersion() {
        return "android";
    }

    public static int getScreenBrightness() {
        int i;
        ContentResolver contentResolver = b.a().getContentResolver();
        try {
            i = Settings.System.getInt(contentResolver, "screen_brightness_mode");
        } catch (Settings.SettingNotFoundException e) {
            i = 0;
        }
        if (i == 1) {
            return -1;
        }
        try {
            return Settings.System.getInt(contentResolver, "screen_brightness");
        } catch (Settings.SettingNotFoundException e2) {
            return -1;
        }
    }

    public static float getScreenDensity() {
        if (b.a() == null) {
            return 0.0f;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) b.a().getSystemService("window");
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.density;
    }

    public static int getScreenDensityDpi() {
        if (b.a() == null) {
            return 0;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) b.a().getSystemService("window");
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.densityDpi;
    }

    public static long getSdcardFreeSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks())) / 1024;
    }

    public static String getSdcardPath() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (externalStorageDirectory != null) {
            return externalStorageDirectory.getAbsolutePath();
        }
        return null;
    }

    public static long getSdcardTotalSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (((long) statFs.getBlockSize()) * ((long) statFs.getBlockCount())) / 1024;
    }

    public static float getSystemMetricsX() {
        if (b.a() == null) {
            return 0.0f;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) b.a().getSystemService("window");
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return (float) displayMetrics.widthPixels;
    }

    public static float getSystemMetricsY() {
        if (b.a() == null) {
            return 0.0f;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) b.a().getSystemService("window");
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return (float) displayMetrics.heightPixels;
    }

    public static long getTotalMemory() {
        long j = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                j = (long) Integer.valueOf(readLine.split("\\s+")[1]).intValue();
            }
            bufferedReader.close();
        } catch (IOException e) {
        }
        return j;
    }

    public static long getTotalSpace() {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getPath());
        return (((long) statFs.getBlockSize()) * ((long) statFs.getBlockCount())) / 1024;
    }

    public static boolean isWifiConnected() {
        NetworkInfo networkInfo = ((ConnectivityManager) b.a().getSystemService("connectivity")).getNetworkInfo(1);
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isConnected();
    }

    public static void makeCall(String str) {
        b.a().startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + str)));
    }

    public static native void onNetworkStateChanged();

    public static void openUrl(String str) {
        b.a().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
    }

    public static int sendMMS(String str, String str2, String str3, String str4) {
        if (!PhoneNumberUtils.isWellFormedSmsAddress(str)) {
            return 1;
        }
        try {
            String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(str4)).toString()));
            Intent intent = new Intent("android.intent.action.SEND");
            intent.putExtra("address", str);
            intent.putExtra("subject", str2);
            intent.putExtra("sms_body", str3);
            intent.putExtra("android.intent.extra.STREAM", Uri.parse("file://" + str4));
            intent.setType(mimeTypeFromExtension);
            b.a().startActivity(intent);
            return 0;
        } catch (Exception e) {
            return 2;
        }
    }

    public static void sendSMS(String str, String str2) {
        Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + str));
        intent.putExtra("sms_body", str2);
        b.a().startActivity(intent);
    }

    public static void setNetworkChangedCallback() {
        unsetNetworkChangedCallback();
        b = new a();
        b.a().registerReceiver(b, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public static void setScreenAlwaysOn(boolean z) {
        if (z) {
            if (a == null) {
                a = ((PowerManager) b.a().getSystemService("power")).newWakeLock(10, "VDeviceAPI");
            }
            a.acquire();
            return;
        }
        PowerManager.WakeLock wakeLock = a;
        if (wakeLock != null && wakeLock.isHeld()) {
            a.release();
            a = null;
        }
    }

    public static void setupSoftware(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(new File(str)), "application/vnd.android.package-archive");
        b.a().startActivity(intent);
    }

    public static void unsetNetworkChangedCallback() {
        if (b != null) {
            b.a().unregisterReceiver(b);
            b = null;
        }
    }
}
