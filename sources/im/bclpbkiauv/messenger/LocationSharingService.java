package im.bclpbkiauv.messenger;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import im.bclpbkiauv.messenger.LocationController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.LaunchActivity;
import java.util.ArrayList;

public class LocationSharingService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private NotificationCompat.Builder builder;
    private Handler handler;
    private Runnable runnable;

    public LocationSharingService() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        $$Lambda$LocationSharingService$t0JZr_sjgY8QY__Lcpvpzu6D8uM r0 = new Runnable() {
            public final void run() {
                LocationSharingService.this.lambda$onCreate$1$LocationSharingService();
            }
        };
        this.runnable = r0;
        this.handler.postDelayed(r0, 1000);
    }

    public /* synthetic */ void lambda$onCreate$1$LocationSharingService() {
        this.handler.postDelayed(this.runnable, 1000);
        Utilities.stageQueue.postRunnable($$Lambda$LocationSharingService$EejrrBug_MDWQoL0S4PZxh3lEU.INSTANCE);
    }

    static /* synthetic */ void lambda$null$0() {
        for (int a = 0; a < 3; a++) {
            LocationController.getInstance(a).update();
        }
    }

    public IBinder onBind(Intent arg2) {
        return null;
    }

    public void onDestroy() {
        super.onDestroy();
        Handler handler2 = this.handler;
        if (handler2 != null) {
            handler2.removeCallbacks(this.runnable);
        }
        stopForeground(true);
        NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(6);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        Handler handler2;
        if (id == NotificationCenter.liveLocationsChanged && (handler2 = this.handler) != null) {
            handler2.post(new Runnable() {
                public final void run() {
                    LocationSharingService.this.lambda$didReceivedNotification$2$LocationSharingService();
                }
            });
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$LocationSharingService() {
        if (getInfos().isEmpty()) {
            stopSelf();
        } else {
            updateNotification(true);
        }
    }

    private ArrayList<LocationController.SharingLocationInfo> getInfos() {
        ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
        for (int a = 0; a < 3; a++) {
            ArrayList<LocationController.SharingLocationInfo> arrayList = LocationController.getInstance(a).sharingLocationsUI;
            if (!arrayList.isEmpty()) {
                infos.addAll(arrayList);
            }
        }
        return infos;
    }

    private void updateNotification(boolean post) {
        String param;
        if (this.builder != null) {
            ArrayList<LocationController.SharingLocationInfo> infos = getInfos();
            if (infos.size() == 1) {
                LocationController.SharingLocationInfo info = infos.get(0);
                int lower_id = (int) info.messageObject.getDialogId();
                int currentAccount = info.messageObject.currentAccount;
                if (lower_id > 0) {
                    param = UserObject.getFirstName(MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(lower_id)));
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-lower_id));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                }
            } else {
                param = LocaleController.formatPluralString("Chats", infos.size());
            }
            String str = String.format(LocaleController.getString("AttachLiveLocationIsSharing", R.string.AttachLiveLocationIsSharing), new Object[]{LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation), param});
            this.builder.setTicker(str);
            this.builder.setContentText(str);
            if (post) {
                NotificationManagerCompat.from(ApplicationLoader.applicationContext).notify(6, this.builder.build());
            }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getInfos().isEmpty()) {
            stopSelf();
        }
        if (this.builder == null) {
            Intent intent2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent2.setAction("org.tmessages.openlocations");
            intent2.addCategory("android.intent.category.LAUNCHER");
            PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent2, 0);
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext);
            this.builder = builder2;
            builder2.setWhen(System.currentTimeMillis());
            this.builder.setSmallIcon(R.drawable.live_loc);
            this.builder.setContentIntent(contentIntent);
            NotificationsController.checkOtherNotificationsChannel();
            this.builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
            this.builder.setContentTitle(LocaleController.getString("AppName", R.string.AppName));
            this.builder.addAction(0, LocaleController.getString("StopLiveLocation", R.string.StopLiveLocation), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, new Intent(ApplicationLoader.applicationContext, StopLiveLocationReceiver.class), 134217728));
        }
        updateNotification(false);
        startForeground(6, this.builder.build());
        return 2;
    }
}
