package com.blankj.utilcode.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.blankj.utilcode.util.Utils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NotificationUtils {
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Importance {
    }

    public static boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(Utils.getApp()).areNotificationsEnabled();
    }

    public static void notify(int id, Utils.Func1<Void, NotificationCompat.Builder> func1) {
        notify((String) null, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, func1);
    }

    public static void notify(String tag, int id, Utils.Func1<Void, NotificationCompat.Builder> func1) {
        notify(tag, id, ChannelConfig.DEFAULT_CHANNEL_CONFIG, func1);
    }

    public static void notify(int id, ChannelConfig channelConfig, Utils.Func1<Void, NotificationCompat.Builder> func1) {
        notify((String) null, id, channelConfig, func1);
    }

    public static void notify(String tag, int id, ChannelConfig channelConfig, Utils.Func1<Void, NotificationCompat.Builder> func1) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) Utils.getApp().getSystemService("notification")).createNotificationChannel(channelConfig.getNotificationChannel());
        }
        NotificationManagerCompat nmc = NotificationManagerCompat.from(Utils.getApp());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(Utils.getApp());
        if (Build.VERSION.SDK_INT >= 26) {
            builder.setChannelId(channelConfig.mNotificationChannel.getId());
        }
        func1.call(builder);
        nmc.notify(tag, id, builder.build());
    }

    public static void cancel(String tag, int id) {
        NotificationManagerCompat.from(Utils.getApp()).cancel(tag, id);
    }

    public static void cancel(int id) {
        NotificationManagerCompat.from(Utils.getApp()).cancel(id);
    }

    public static void cancelAll() {
        NotificationManagerCompat.from(Utils.getApp()).cancelAll();
    }

    public static void setNotificationBarVisibility(boolean isVisible) {
        String methodName;
        if (isVisible) {
            methodName = Build.VERSION.SDK_INT <= 16 ? "expand" : "expandNotificationsPanel";
        } else {
            methodName = Build.VERSION.SDK_INT <= 16 ? "collapse" : "collapsePanels";
        }
        invokePanels(methodName);
    }

    private static void invokePanels(String methodName) {
        try {
            Class.forName("android.app.StatusBarManager").getMethod(methodName, new Class[0]).invoke(Utils.getApp().getSystemService("statusbar"), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ChannelConfig {
        public static final ChannelConfig DEFAULT_CHANNEL_CONFIG = new ChannelConfig(Utils.getApp().getPackageName(), Utils.getApp().getPackageName(), 3);
        /* access modifiers changed from: private */
        public NotificationChannel mNotificationChannel;

        public ChannelConfig(String id, CharSequence name, int importance) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel = new NotificationChannel(id, name, importance);
            }
        }

        public NotificationChannel getNotificationChannel() {
            return this.mNotificationChannel;
        }

        public ChannelConfig setBypassDnd(boolean bypassDnd) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setBypassDnd(bypassDnd);
            }
            return this;
        }

        public ChannelConfig setDescription(String description) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setDescription(description);
            }
            return this;
        }

        public ChannelConfig setGroup(String groupId) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setGroup(groupId);
            }
            return this;
        }

        public ChannelConfig setImportance(int importance) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setImportance(importance);
            }
            return this;
        }

        public ChannelConfig setLightColor(int argb) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setLightColor(argb);
            }
            return this;
        }

        public ChannelConfig setLockscreenVisibility(int lockscreenVisibility) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setLockscreenVisibility(lockscreenVisibility);
            }
            return this;
        }

        public ChannelConfig setName(CharSequence name) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setName(name);
            }
            return this;
        }

        public ChannelConfig setShowBadge(boolean showBadge) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setShowBadge(showBadge);
            }
            return this;
        }

        public ChannelConfig setSound(Uri sound, AudioAttributes audioAttributes) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setSound(sound, audioAttributes);
            }
            return this;
        }

        public ChannelConfig setVibrationPattern(long[] vibrationPattern) {
            if (Build.VERSION.SDK_INT >= 26) {
                this.mNotificationChannel.setVibrationPattern(vibrationPattern);
            }
            return this;
        }
    }
}
