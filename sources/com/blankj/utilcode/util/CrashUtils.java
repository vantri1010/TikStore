package com.blankj.utilcode.util;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public final class CrashUtils {
    /* access modifiers changed from: private */
    public static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler();
    private static final String FILE_SEP = System.getProperty("file.separator");
    /* access modifiers changed from: private */
    public static final Format FORMAT = new SimpleDateFormat("MM-dd_HH-mm-ss");
    private static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            if (e != null) {
                String time = CrashUtils.FORMAT.format(new Date(System.currentTimeMillis()));
                StringBuilder sb = new StringBuilder();
                sb.append("************* Log Head ****************\nTime Of Crash      : " + time + "\nDevice Manufacturer: " + Build.MANUFACTURER + "\nDevice Model       : " + Build.MODEL + "\nAndroid Version    : " + Build.VERSION.RELEASE + "\nAndroid SDK        : " + Build.VERSION.SDK_INT + "\nApp VersionName    : " + CrashUtils.versionName + "\nApp VersionCode    : " + CrashUtils.versionCode + "\n************* Log Head ****************\n\n");
                sb.append(ThrowableUtils.getFullStackTrace(e));
                String crashInfo = sb.toString();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(CrashUtils.dir == null ? CrashUtils.defaultDir : CrashUtils.dir);
                sb2.append(time);
                sb2.append(".txt");
                String fullPath = sb2.toString();
                if (CrashUtils.createOrExistsFile(fullPath)) {
                    CrashUtils.input2File(crashInfo, fullPath);
                } else {
                    Log.e("CrashUtils", "create " + fullPath + " failed!");
                }
                if (CrashUtils.sOnCrashListener != null) {
                    CrashUtils.sOnCrashListener.onCrash(crashInfo, e);
                }
                if (CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                    CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, e);
                }
            } else if (CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                CrashUtils.DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, (Throwable) null);
            } else {
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
        }
    };
    /* access modifiers changed from: private */
    public static String defaultDir;
    /* access modifiers changed from: private */
    public static String dir;
    /* access modifiers changed from: private */
    public static OnCrashListener sOnCrashListener;
    /* access modifiers changed from: private */
    public static int versionCode;
    /* access modifiers changed from: private */
    public static String versionName;

    public interface OnCrashListener {
        void onCrash(String str, Throwable th);
    }

    static {
        try {
            PackageInfo pi = Utils.getApp().getPackageManager().getPackageInfo(Utils.getApp().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private CrashUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init() {
        init("");
    }

    public static void init(File crashDir) {
        if (crashDir != null) {
            init(crashDir.getAbsolutePath(), (OnCrashListener) null);
            return;
        }
        throw new NullPointerException("Argument 'crashDir' of type File (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void init(String crashDirPath) {
        init(crashDirPath, (OnCrashListener) null);
    }

    public static void init(OnCrashListener onCrashListener) {
        init("", onCrashListener);
    }

    public static void init(File crashDir, OnCrashListener onCrashListener) {
        if (crashDir != null) {
            init(crashDir.getAbsolutePath(), onCrashListener);
            return;
        }
        throw new NullPointerException("Argument 'crashDir' of type File (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
    }

    public static void init(String crashDirPath, OnCrashListener onCrashListener) {
        String str;
        if (isSpace(crashDirPath)) {
            dir = null;
        } else {
            if (crashDirPath.endsWith(FILE_SEP)) {
                str = crashDirPath;
            } else {
                str = crashDirPath + FILE_SEP;
            }
            dir = str;
        }
        if (!"mounted".equals(Environment.getExternalStorageState()) || Utils.getApp().getExternalCacheDir() == null) {
            defaultDir = Utils.getApp().getCacheDir() + FILE_SEP + "crash" + FILE_SEP;
        } else {
            defaultDir = Utils.getApp().getExternalCacheDir() + FILE_SEP + "crash" + FILE_SEP;
        }
        sOnCrashListener = onCrashListener;
        Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
    }

    /* access modifiers changed from: private */
    public static void input2File(final String input, final String filePath) {
        try {
            if (Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                public Boolean call() {
                    BufferedWriter bw = null;
                    try {
                        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
                        bw2.write(input);
                        try {
                            bw2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        if (bw != null) {
                            try {
                                bw.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        return false;
                    } catch (Throwable th) {
                        if (bw != null) {
                            try {
                                bw.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                        }
                        throw th;
                    }
                }
            }).get().booleanValue()) {
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e2) {
            e2.printStackTrace();
        }
        Log.e("CrashUtils", "write crash info to " + filePath + " failed!");
    }

    /* access modifiers changed from: private */
    public static boolean createOrExistsFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (!file.exists() ? file.mkdirs() : file.isDirectory());
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
