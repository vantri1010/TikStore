package com.blankj.utilcode.util;

import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class SDCardUtils {
    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static boolean isSDCardEnableByEnvironment() {
        return "mounted".equals(Environment.getExternalStorageState());
    }

    public static String getSDCardPathByEnvironment() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    public static List<SDCardInfo> getSDCardInfo() {
        List<SDCardInfo> paths = new ArrayList<>();
        StorageManager sm = (StorageManager) Utils.getApp().getSystemService("storage");
        if (sm == null) {
            return paths;
        }
        int i = 0;
        if (Build.VERSION.SDK_INT >= 24) {
            List<StorageVolume> storageVolumes = sm.getStorageVolumes();
            try {
                Method getPathMethod = StorageVolume.class.getMethod("getPath", new Class[0]);
                for (StorageVolume storageVolume : storageVolumes) {
                    boolean isRemovable = storageVolume.isRemovable();
                    paths.add(new SDCardInfo((String) getPathMethod.invoke(storageVolume, new Object[0]), storageVolume.getState(), isRemovable));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        } else {
            try {
                Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                Method getPathMethod2 = storageVolumeClazz.getMethod("getPath", new Class[0]);
                Method isRemovableMethod = storageVolumeClazz.getMethod("isRemovable", new Class[0]);
                Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
                Object result = StorageManager.class.getMethod("getVolumeList", new Class[0]).invoke(sm, new Object[0]);
                int length = Array.getLength(result);
                int i2 = 0;
                while (i2 < length) {
                    Object storageVolumeElement = Array.get(result, i2);
                    String path = (String) getPathMethod2.invoke(storageVolumeElement, new Object[i]);
                    boolean isRemovable2 = ((Boolean) isRemovableMethod.invoke(storageVolumeElement, new Object[i])).booleanValue();
                    Object[] objArr = new Object[1];
                    objArr[i] = path;
                    paths.add(new SDCardInfo(path, (String) getVolumeStateMethod.invoke(sm, objArr), isRemovable2));
                    i2++;
                    i = 0;
                }
            } catch (ClassNotFoundException e4) {
                e4.printStackTrace();
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
            } catch (NoSuchMethodException e6) {
                e6.printStackTrace();
            } catch (IllegalAccessException e7) {
                e7.printStackTrace();
            }
        }
        return paths;
    }

    public static List<String> getMountedSDCardPath() {
        List<String> path = new ArrayList<>();
        List<SDCardInfo> sdCardInfo = getSDCardInfo();
        if (sdCardInfo == null || sdCardInfo.isEmpty()) {
            return path;
        }
        for (SDCardInfo cardInfo : sdCardInfo) {
            String state = cardInfo.state;
            if (state != null && "mounted".equals(state.toLowerCase())) {
                path.add(cardInfo.path);
            }
        }
        return path;
    }

    public static class SDCardInfo {
        private boolean isRemovable;
        /* access modifiers changed from: private */
        public String path;
        /* access modifiers changed from: private */
        public String state;

        SDCardInfo(String path2, String state2, boolean isRemovable2) {
            this.path = path2;
            this.state = state2;
            this.isRemovable = isRemovable2;
        }

        public String getPath() {
            return this.path;
        }

        public String getState() {
            return this.state;
        }

        public boolean isRemovable() {
            return this.isRemovable;
        }

        public String toString() {
            return "SDCardInfo {path = " + this.path + ", state = " + this.state + ", isRemovable = " + this.isRemovable + '}';
        }
    }
}
