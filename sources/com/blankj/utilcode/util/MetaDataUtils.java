package com.blankj.utilcode.util;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public final class MetaDataUtils {
    private MetaDataUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getMetaDataInApp(String key) {
        if (key != null) {
            try {
                return String.valueOf(Utils.getApp().getPackageManager().getApplicationInfo(Utils.getApp().getPackageName(), 128).metaData.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            throw new NullPointerException("Argument 'key' of type String (#0 out of 1, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInActivity(Activity activity, String key) {
        if (activity == null) {
            throw new NullPointerException("Argument 'activity' of type Activity (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            return getMetaDataInActivity((Class<? extends Activity>) activity.getClass(), key);
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInActivity(Class<? extends Activity> clz, String key) {
        if (clz == null) {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Activity> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            try {
                return String.valueOf(Utils.getApp().getPackageManager().getActivityInfo(new ComponentName(Utils.getApp(), clz), 128).metaData.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInService(Service service, String key) {
        if (service == null) {
            throw new NullPointerException("Argument 'service' of type Service (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            return getMetaDataInService((Class<? extends Service>) service.getClass(), key);
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInService(Class<? extends Service> clz, String key) {
        if (clz == null) {
            throw new NullPointerException("Argument 'clz' of type Class<? extends Service> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            try {
                return String.valueOf(Utils.getApp().getPackageManager().getServiceInfo(new ComponentName(Utils.getApp(), clz), 128).metaData.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInReceiver(BroadcastReceiver receiver, String key) {
        if (receiver == null) {
            throw new NullPointerException("Argument 'receiver' of type BroadcastReceiver (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            return getMetaDataInReceiver(receiver, key);
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }

    public static String getMetaDataInReceiver(Class<? extends BroadcastReceiver> clz, String key) {
        if (clz == null) {
            throw new NullPointerException("Argument 'clz' of type Class<? extends BroadcastReceiver> (#0 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        } else if (key != null) {
            try {
                return String.valueOf(Utils.getApp().getPackageManager().getReceiverInfo(new ComponentName(Utils.getApp(), clz), 128).metaData.get(key));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            throw new NullPointerException("Argument 'key' of type String (#1 out of 2, zero-based) is marked by @android.support.annotation.NonNull but got null for it");
        }
    }
}
