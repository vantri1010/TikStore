package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import im.bclpbkiauv.messenger.R;

public class ForegroundService extends Service {
    public static final String ID = "0x110088";
    public static final String NAME = ForegroundService.class.getSimpleName();
    private static final int NOTIFICATION_FLAG = 17;

    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(101, createCompatibleNotification(this));
        } else {
            startForeground(101, createMainNotification(this));
        }
    }

    public Notification createCompatibleNotification(Context context) {
        ((NotificationManager) context.getSystemService("notification")).createNotificationChannel(new NotificationChannel(ID, context.getResources().getString(R.string.app_name), 3));
        Intent intentChart = new Intent(this, VisualCallActivity.class);
        intentChart.addFlags(C.ENCODING_PCM_MU_LAW);
        return new NotificationCompat.Builder(context, ID).setContentTitle("音视频通话中").setContentText("正在进行音视频通话").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(PendingIntent.getActivity(context, 1, intentChart, 134217728)).setWhen(System.currentTimeMillis()).setOngoing(true).setPriority(-2).setCategory(NotificationCompat.CATEGORY_SERVICE).build();
    }

    public Notification createMainNotification(Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, VisualCallActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentTitle("音视频通话中").setContentText("正在进行音视频通话").setSmallIcon(R.mipmap.ic_launcher).setWhen(System.currentTimeMillis()).setDefaults(2).setPriority(1);
        Notification notification = builder.build();
        notification.defaults = 1;
        notification.flags |= 16;
        notification.flags = 2 | notification.flags;
        notification.flags |= 32;
        return notification;
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
