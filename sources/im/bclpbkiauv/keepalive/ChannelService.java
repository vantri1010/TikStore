package im.bclpbkiauv.keepalive;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.ui.LaunchActivity;

public class ChannelService extends Service {
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startNotification() {
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel notificationChannel = new NotificationChannel(DaemonService.KEEP_CHANNEL_ID, DaemonService.KEEP_CHANNEL_NAME, 4);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(-16711936);
                notificationChannel.setShowBadge(true);
                notificationChannel.setLockscreenVisibility(1);
                ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannel);
            }
            Intent openLaunchIntent = new Intent(getApplicationContext(), LaunchActivity.class);
            openLaunchIntent.addCategory("android.intent.category.LAUNCHER");
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, openLaunchIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DaemonService.KEEP_CHANNEL_ID);
            builder.setPriority(2).setSmallIcon(R.mipmap.notification).setContentTitle(LocaleController.getString(R.string.AppName)).setContentText(LocaleController.getString(R.string.Notifications)).setOngoing(true).setAutoCancel(false).setShowWhen(true).setWhen(System.currentTimeMillis()).setContentIntent(contentIntent);
            Notification notification = builder.build();
            notification.flags |= 64;
            startForeground(DaemonService.KEEP_SERVICE_ID, notification);
        } catch (Throwable e) {
            FileLog.e("DaemonService startNotification error:" + e.toString());
        }
    }
}
