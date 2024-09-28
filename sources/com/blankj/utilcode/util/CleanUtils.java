package com.blankj.utilcode.util;

import android.os.Environment;
import java.io.File;

public final class CleanUtils {
    private CleanUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean cleanInternalCache() {
        return deleteFilesInDir(Utils.getApp().getCacheDir());
    }

    public static boolean cleanInternalFiles() {
        return deleteFilesInDir(Utils.getApp().getFilesDir());
    }

    public static boolean cleanInternalDbs() {
        return deleteFilesInDir(new File(Utils.getApp().getFilesDir().getParent(), "databases"));
    }

    public static boolean cleanInternalDbByName(String dbName) {
        return Utils.getApp().deleteDatabase(dbName);
    }

    public static boolean cleanInternalSp() {
        return deleteFilesInDir(new File(Utils.getApp().getFilesDir().getParent(), "shared_prefs"));
    }

    public static boolean cleanExternalCache() {
        return "mounted".equals(Environment.getExternalStorageState()) && deleteFilesInDir(Utils.getApp().getExternalCacheDir());
    }

    public static boolean cleanCustomDir(String dirPath) {
        return deleteFilesInDir(dirPath);
    }

    public static boolean cleanCustomDir(File dir) {
        return deleteFilesInDir(dir);
    }

    public static boolean deleteFilesInDir(String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    private static boolean deleteFilesInDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (!(files == null || files.length == 0)) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory() && !deleteDir(file)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean deleteDir(File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            return true;
        }
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles();
        if (!(files == null || files.length == 0)) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) {
                        return false;
                    }
                } else if (file.isDirectory() && !deleteDir(file)) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private static File getFileByPath(String filePath) {
        if (isSpace(filePath)) {
            return null;
        }
        return new File(filePath);
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
