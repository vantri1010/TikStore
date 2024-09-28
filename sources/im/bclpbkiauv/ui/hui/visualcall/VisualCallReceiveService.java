package im.bclpbkiauv.ui.hui.visualcall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import com.socks.library.KLog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.tgnet.TLRPCCall;
import im.bclpbkiauv.ui.LaunchActivity;
import im.bclpbkiauv.ui.hui.friendscircle_v1.helper.DatabaseInstance;
import java.util.ArrayList;
import java.util.Random;

public class VisualCallReceiveService extends Service implements NotificationCenter.NotificationCenterDelegate {
    private String ID = "0x110066";
    private Handler handler = new Handler();
    private long mlLastReqTime;
    private Runnable runnable = $$Lambda$1le26hykZKxtZWMdpMizrfnHPMU.INSTANCE;
    private String strId;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intent2 = intent;
        RingUtils.stopMediaPlayerRing();
        if (DatabaseInstance.queryVisualCallById(intent2.getStringExtra(TtmlNode.ATTR_ID)) != null) {
            return 2;
        }
        KLog.d("----------收到音视频请求 type = " + intent2.getStringExtra(TtmlNode.ATTR_ID) + " " + (System.currentTimeMillis() - this.mlLastReqTime));
        this.handler.removeCallbacks(this.runnable);
        if (System.currentTimeMillis() - this.mlLastReqTime > AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            this.mlLastReqTime = System.currentTimeMillis();
            if (ApplicationLoader.mbytAVideoCallBusy == 0) {
                if (!AndroidUtilities.isAppOnForeground(this)) {
                    if (DatabaseInstance.getVisualCallCount() < 1) {
                        RingUtils.playRingBySoundPool(this);
                        this.strId = intent2.getStringExtra(TtmlNode.ATTR_ID);
                        VisualCallRequestParaBean paraBean = new VisualCallRequestParaBean();
                        paraBean.setStrId(intent2.getStringExtra(TtmlNode.ATTR_ID));
                        paraBean.setVideo(intent2.getBooleanExtra(MimeTypes.BASE_TYPE_VIDEO, false));
                        paraBean.setAdmin_id(intent2.getIntExtra("admin_id", 0));
                        paraBean.setApp_id(intent2.getStringExtra("app_id"));
                        paraBean.setToken(intent2.getStringExtra("token"));
                        String strGslb = "";
                        ArrayList<String> arrayList = intent2.getStringArrayListExtra("gslb");
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (strGslb.equals("")) {
                                strGslb = arrayList.get(i);
                            } else {
                                strGslb = strGslb + "," + arrayList.get(i);
                            }
                        }
                        paraBean.setGslb(strGslb);
                        paraBean.setJson(intent2.getStringExtra("json"));
                        DatabaseInstance.saveVisualCallPara(paraBean);
                        this.handler.postDelayed(this.runnable, 35000);
                    } else {
                        AVideoCallInterface.IsBusyingNow(intent2.getStringExtra(TtmlNode.ATTR_ID));
                    }
                } else if (DatabaseInstance.getVisualCallCount() < 1) {
                    boolean blnVideo = intent2.getBooleanExtra(MimeTypes.BASE_TYPE_VIDEO, false);
                    Intent actIntent = new Intent(this, VisualCallReceiveActivity.class);
                    actIntent.putExtra(MimeTypes.BASE_TYPE_VIDEO, blnVideo);
                    actIntent.putExtra(TtmlNode.ATTR_ID, intent2.getStringExtra(TtmlNode.ATTR_ID));
                    actIntent.putExtra("admin_id", intent2.getIntExtra("admin_id", 0));
                    actIntent.putExtra("app_id", intent2.getStringExtra("app_id"));
                    actIntent.putExtra("token", intent2.getStringExtra("token"));
                    actIntent.putStringArrayListExtra("gslb", intent2.getStringArrayListExtra("gslb"));
                    actIntent.putExtra("json", intent2.getStringExtra("json"));
                    actIntent.putExtra("from", 0);
                    actIntent.addFlags(C.ENCODING_PCM_MU_LAW);
                    startActivity(actIntent);
                    new Handler().postDelayed(new Runnable(intent2) {
                        private final /* synthetic */ Intent f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void run() {
                            VisualCallReceiveService.lambda$onStartCommand$0(this.f$0);
                        }
                    }, 3000);
                } else {
                    AVideoCallInterface.IsBusyingNow(intent2.getStringExtra(TtmlNode.ATTR_ID));
                }
                VisualCallRequestBean bean1 = new VisualCallRequestBean();
                bean1.setStrId(intent2.getStringExtra(TtmlNode.ATTR_ID));
                bean1.setTimestamp(System.currentTimeMillis());
                DatabaseInstance.saveVisualCallRequest(bean1);
                return 2;
            }
            AVideoCallInterface.IsBusyingNow(intent2.getStringExtra(TtmlNode.ATTR_ID));
            return 2;
        }
        AVideoCallInterface.IsBusyingNow(intent2.getStringExtra(TtmlNode.ATTR_ID));
        return 2;
    }

    static /* synthetic */ void lambda$onStartCommand$0(Intent intent) {
        if (ApplicationLoader.mbytAVideoCallBusy == 0) {
            AVideoCallInterface.IsBusyingNow(intent.getStringExtra(TtmlNode.ATTR_ID));
        }
    }

    private void WaitForCallReceiveActivity(Intent intent) {
        new Thread(new Runnable(intent) {
            private final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VisualCallReceiveService.this.lambda$WaitForCallReceiveActivity$2$VisualCallReceiveService(this.f$1);
            }
        }).start();
    }

    public /* synthetic */ void lambda$WaitForCallReceiveActivity$2$VisualCallReceiveService(Intent intent) {
        int iCount = 0;
        while (ApplicationLoader.mbytAVideoCallBusy == 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iCount++;
            if (iCount <= 8) {
                if (ApplicationLoader.mbytAVideoCallBusy == 1) {
                    break;
                }
            } else {
                break;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(intent) {
            private final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                VisualCallReceiveService.this.lambda$null$1$VisualCallReceiveService(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$VisualCallReceiveService(Intent intent) {
        RingUtils.playRingBySoundPool(this);
        if (ApplicationLoader.mbytAVideoCallBusy == 0) {
            startActivity(intent);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(101, createCompatibleNotification(this));
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reecivedAVideoDiscarded);
    }

    public Notification createCompatibleNotification(Context context) {
        this.ID = "0x" + (new Random().nextInt(DefaultOggSeeker.MATCH_BYTE_RANGE) + 100);
        NotificationChannel chan = new NotificationChannel("to-do-it", context.getResources().getString(R.string.app_name), 3);
        chan.enableLights(false);
        chan.enableVibration(false);
        chan.setVibrationPattern(new long[]{0});
        chan.setSound((Uri) null, (AudioAttributes) null);
        NotificationManager service = (NotificationManager) context.getSystemService("notification");
        if (service != null) {
            service.createNotificationChannel(chan);
        }
        return new NotificationCompat.Builder(context, this.ID).setContentTitle(LocaleController.getString("visual_call_doing", R.string.visual_call_doing)).setContentText(LocaleController.getString("visual_call_doing_now", R.string.visual_call_doing_now)).setSmallIcon(R.mipmap.notification).setContentIntent(PendingIntent.getActivity(context, 1, new Intent(this, LaunchActivity.class), 134217728)).setOngoing(false).setWhen(System.currentTimeMillis()).setSound((Uri) null).setChannelId("to-do-it").setPriority(-2).build();
    }

    public Notification createMainNotification(Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, VisualCallReceiveActivity.class), 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentIntent(pendingIntent).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)).setContentTitle(LocaleController.getString("visual_call_doing", R.string.visual_call_doing)).setContentText(LocaleController.getString("visual_call_doing_now", R.string.visual_call_doing_now)).setSmallIcon(R.mipmap.notification).setWhen(System.currentTimeMillis()).setDefaults(2).setPriority(1);
        Notification notification = builder.build();
        notification.defaults = 1;
        notification.flags |= 16;
        notification.flags = 2 | notification.flags;
        notification.flags |= 32;
        return notification;
    }

    public void onDestroy() {
        stopForeground(true);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reecivedAVideoDiscarded);
        super.onDestroy();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPCCall.TL_UpdateMeetCallDiscarded discarded;
        if (!AndroidUtilities.isAppOnForeground(this) && id == NotificationCenter.reecivedAVideoDiscarded && (discarded = args[0]) != null && discarded.id.equals(this.strId)) {
            Toast.makeText(this, LocaleController.getString("visual_call_other_side_cancel", R.string.visual_call_other_side_cancel), 1).show();
            DatabaseInstance.deleteVisualCallRequest();
            RingUtils.stopSoundPoolRing();
        }
    }
}
