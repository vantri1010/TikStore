package im.bclpbkiauv.messenger;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.MimeTypes;
import com.zhy.http.okhttp.OkHttpUtils;
import im.bclpbkiauv.messenger.support.SparseLongArray;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLJsonResolve;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.tgnet.TLRPCRedpacket;
import im.bclpbkiauv.ui.PopupNotificationActivity;
import im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse;
import im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
    public static final int SETTING_MUTE_2_DAYS = 2;
    public static final int SETTING_MUTE_8_HOURS = 1;
    public static final int SETTING_MUTE_FOREVER = 3;
    public static final int SETTING_MUTE_HOUR = 0;
    public static final int SETTING_MUTE_UNMUTE = 4;
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_PRIVATE = 1;
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO));
    public static long globalSecretChatId = -4294967296L;
    /* access modifiers changed from: private */
    public static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList<>();
    private LongSparseArray<MessageObject> fcmRandomMessagesDict = new LongSparseArray<>();
    private boolean inChatSoundEnabled;
    private int lastBadgeCount = -1;
    private int lastButtonId = 5000;
    private int lastOnlineFromOtherDevice = 0;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray<>();
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId = (this.currentAccount + 1);
    private boolean notifyCheck = false;
    private long opened_dialog_id = 0;
    private int personal_count = 0;
    public ArrayList<MessageObject> popupMessages = new ArrayList<>();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList<>();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray<>();
    private ArrayList<MessageObject> pushMessages = new ArrayList<>();
    private LongSparseArray<MessageObject> pushMessagesDict = new LongSparseArray<>();
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private LongSparseArray<Point> smartNotificationsDialogs = new LongSparseArray<>();
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count = 0;
    private LongSparseArray<Integer> wearNotificationsIds = new LongSparseArray<>();

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
    }

    public static NotificationsController getInstance(int num) {
        NotificationsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    NotificationsController[] notificationsControllerArr = Instance;
                    NotificationsController notificationsController = new NotificationsController(num);
                    localInstance = notificationsController;
                    notificationsControllerArr[num] = notificationsController;
                }
            }
        }
        return localInstance;
    }

    public NotificationManagerCompat getNotificationManager() {
        return notificationManager;
    }

    public NotificationsController(int instance) {
        super(instance);
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        sb.append(this.currentAccount == 0 ? "" : Integer.valueOf(this.currentAccount));
        this.notificationGroup = sb.toString();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = preferences.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = preferences.getBoolean("badgeNumber", true);
        this.showBadgeMuted = preferences.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = preferences.getBoolean("badgeNumberMessages", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "hchat:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e((Throwable) e3);
        }
        this.notificationDelayRunnable = new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$new$0$NotificationsController();
            }
        };
    }

    public /* synthetic */ void lambda$new$0$NotificationsController() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            SharedPreferences preferences = null;
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                OTHER_NOTIFICATIONS_CHANNEL = preferences.getString("OtherKey", "Other3");
            }
            NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            if (notificationChannel != null && notificationChannel.getImportance() == 0) {
                systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
                OTHER_NOTIFICATIONS_CHANNEL = null;
                notificationChannel = null;
            }
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                if (preferences == null) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                }
                OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
                preferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
            }
            if (notificationChannel == null) {
                NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Other", 2);
                notificationChannel2.enableLights(false);
                notificationChannel2.enableVibration(false);
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setContentType(4);
                builder.setUsage(5);
                notificationChannel2.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, builder.build());
                try {
                    systemNotificationManager.createNotificationChannel(notificationChannel2);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$cleanup$1$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$1$NotificationsController() {
        this.opened_dialog_id = 0;
        this.total_unread_count = 0;
        this.personal_count = 0;
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.fcmRandomMessagesDict.clear();
        this.pushDialogs.clear();
        this.wearNotificationsIds.clear();
        this.lastWearNotifiedMessageId.clear();
        this.delayedPushMessages.clear();
        this.notifyCheck = false;
        this.lastBadgeCount = 0;
        try {
            if (this.notificationDelayWakelock.isHeld()) {
                this.notificationDelayWakelock.release();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        dismissNotification();
        setBadge(getTotalAllUnreadCount());
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        editor.clear();
        editor.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                for (int a = 0; a < count; a++) {
                    String id = list.get(a).getId();
                    if (id.startsWith(keyStart)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    public /* synthetic */ void lambda$setOpenedDialogId$2$NotificationsController(long dialog_id) {
        this.opened_dialog_id = dialog_id;
    }

    public void setOpenedDialogId(long dialog_id) {
        notificationsQueue.postRunnable(new Runnable(dialog_id) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2$NotificationsController(this.f$1);
            }
        });
    }

    public void setLastOnlineFromOtherDevice(int time) {
        notificationsQueue.postRunnable(new Runnable(time) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$3$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$3$NotificationsController(int time) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + time);
        }
        this.lastOnlineFromOtherDevice = time;
    }

    public void removeNotificationsForDialog(long did) {
        processReadMessages((SparseLongArray) null, did, 0, Integer.MAX_VALUE, false);
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray<>();
        dialogsToUpdate.put(did, 0);
        processDialogsUpdateRead(dialogsToUpdate);
    }

    public boolean hasMessagesToReply() {
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && ((int) dialog_id) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$5$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$forceShowPopupForReply$5$NotificationsController() {
        ArrayList<MessageObject> popupArray = new ArrayList<>();
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && ((int) dialog_id) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                popupArray.add(0, messageObject);
            }
        }
        if (popupArray.isEmpty() == 0 && !AndroidUtilities.needShowPasscode(false)) {
            AndroidUtilities.runOnUIThread(new Runnable(popupArray) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$4$NotificationsController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$4$NotificationsController(ArrayList popupArray) {
        this.popupReplyMessages = popupArray;
        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        popupIntent.putExtra("force", true);
        popupIntent.putExtra("currentAccount", this.currentAccount);
        popupIntent.setFlags(268763140);
        ApplicationLoader.applicationContext.startActivity(popupIntent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> deletedMessages) {
        notificationsQueue.postRunnable(new Runnable(deletedMessages, new ArrayList<>(0)) {
            private final /* synthetic */ SparseArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(SparseArray deletedMessages, ArrayList popupArrayRemove) {
        SparseArray sparseArray = deletedMessages;
        ArrayList arrayList = popupArrayRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        int a = 0;
        while (a < deletedMessages.size()) {
            int key = sparseArray.keyAt(a);
            long dialog_id = (long) (-key);
            ArrayList<Integer> mids = (ArrayList) sparseArray.get(key);
            Integer currentCount = this.pushDialogs.get(dialog_id);
            if (currentCount == null) {
                currentCount = 0;
            }
            Integer newCount = currentCount;
            int b = 0;
            while (b < mids.size()) {
                int old_unread_count2 = old_unread_count;
                SharedPreferences preferences2 = preferences;
                long mid = (((long) key) << 32) | ((long) mids.get(b).intValue());
                MessageObject messageObject = this.pushMessagesDict.get(mid);
                if (messageObject != null) {
                    this.pushMessagesDict.remove(mid);
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    arrayList.add(messageObject);
                    newCount = Integer.valueOf(newCount.intValue() - 1);
                }
                b++;
                old_unread_count = old_unread_count2;
                preferences = preferences2;
            }
            int old_unread_count3 = old_unread_count;
            SharedPreferences preferences3 = preferences;
            if (newCount.intValue() <= 0) {
                newCount = 0;
                this.smartNotificationsDialogs.remove(dialog_id);
            }
            if (!newCount.equals(currentCount)) {
                int intValue = this.total_unread_count - currentCount.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id);
                this.pushDialogsOverrideMention.remove(dialog_id);
            }
            a++;
            old_unread_count = old_unread_count3;
            preferences = preferences3;
        }
        int old_unread_count4 = old_unread_count;
        SharedPreferences sharedPreferences = preferences;
        if (popupArrayRemove.isEmpty() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$6$NotificationsController(this.f$1);
                }
            });
        }
        if (old_unread_count4 != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$7$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
    }

    public /* synthetic */ void lambda$null$7$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void removeDeletedHisoryFromNotifications(SparseIntArray deletedMessages) {
        notificationsQueue.postRunnable(new Runnable(deletedMessages, new ArrayList<>(0)) {
            private final /* synthetic */ SparseIntArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(SparseIntArray deletedMessages, ArrayList popupArrayRemove) {
        long dialog_id;
        long dialog_id2;
        int i;
        SparseIntArray sparseIntArray = deletedMessages;
        ArrayList arrayList = popupArrayRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        for (int a = 0; a < deletedMessages.size(); a++) {
            int key = sparseIntArray.keyAt(a);
            long dialog_id3 = (long) (-key);
            int id = sparseIntArray.get(key);
            Integer currentCount = this.pushDialogs.get(dialog_id3);
            if (currentCount == null) {
                currentCount = 0;
            }
            Integer newCount = currentCount;
            int c = 0;
            while (c < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(c);
                if (messageObject.getDialogId() != dialog_id3 || messageObject.getId() > id) {
                    dialog_id2 = dialog_id3;
                    i = 1;
                } else {
                    dialog_id2 = dialog_id3;
                    this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    c--;
                    if (isPersonalMessage(messageObject)) {
                        i = 1;
                        this.personal_count--;
                    } else {
                        i = 1;
                    }
                    arrayList.add(messageObject);
                    newCount = Integer.valueOf(newCount.intValue() - i);
                }
                c += i;
                dialog_id3 = dialog_id2;
            }
            long dialog_id4 = dialog_id3;
            if (newCount.intValue() <= 0) {
                newCount = 0;
                dialog_id = dialog_id4;
                this.smartNotificationsDialogs.remove(dialog_id);
            } else {
                dialog_id = dialog_id4;
            }
            if (!newCount.equals(currentCount)) {
                int intValue = this.total_unread_count - currentCount.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id);
                this.pushDialogsOverrideMention.remove(dialog_id);
            }
        }
        boolean z = true;
        if (popupArrayRemove.isEmpty() != 0) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$9$NotificationsController(this.f$1);
                }
            });
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$10$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$9$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
    }

    public /* synthetic */ void lambda$null$10$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processReadMessages(SparseLongArray inbox, long dialog_id, int max_date, int max_id, boolean isPopup) {
        notificationsQueue.postRunnable(new Runnable(inbox, new ArrayList<>(0), dialog_id, max_id, max_date, isPopup) {
            private final /* synthetic */ SparseLongArray f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                NotificationsController.this.lambda$processReadMessages$13$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$processReadMessages$13$NotificationsController(SparseLongArray inbox, ArrayList popupArrayRemove, long dialog_id, int max_id, int max_date, boolean isPopup) {
        SparseLongArray sparseLongArray = inbox;
        ArrayList arrayList = popupArrayRemove;
        int i = max_id;
        int i2 = max_date;
        if (sparseLongArray != null) {
            for (int b = 0; b < inbox.size(); b++) {
                int key = sparseLongArray.keyAt(b);
                long messageId = sparseLongArray.get(key);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == ((long) key) && messageObject.getId() <= ((int) messageId)) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        arrayList.add(messageObject);
                        long mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        this.pushMessagesDict.remove(mid);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(a);
                        a--;
                    }
                    a++;
                }
            }
        }
        if (!(dialog_id == 0 || (i == 0 && i2 == 0))) {
            int a2 = 0;
            while (a2 < this.pushMessages.size()) {
                MessageObject messageObject2 = this.pushMessages.get(a2);
                if (messageObject2.getDialogId() == dialog_id) {
                    boolean remove = false;
                    if (i2 != 0) {
                        if (messageObject2.messageOwner.date <= i2) {
                            remove = true;
                        }
                    } else if (!isPopup) {
                        if (messageObject2.getId() <= i || i < 0) {
                            remove = true;
                        }
                    } else if (messageObject2.getId() == i || i < 0) {
                        remove = true;
                    }
                    if (remove) {
                        if (isPersonalMessage(messageObject2)) {
                            this.personal_count--;
                        }
                        this.pushMessages.remove(a2);
                        this.delayedPushMessages.remove(messageObject2);
                        arrayList.add(messageObject2);
                        long mid2 = (long) messageObject2.getId();
                        if (messageObject2.messageOwner.to_id.channel_id != 0) {
                            mid2 |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                        }
                        this.pushMessagesDict.remove(mid2);
                        a2--;
                    }
                }
                a2++;
            }
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$12$NotificationsController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$12$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    private int addToPopupMessages(ArrayList<MessageObject> popupArrayAdd, MessageObject messageObject, int lower_id, long dialog_id, boolean isChannel, SharedPreferences preferences) {
        int popup = 0;
        if (lower_id != 0) {
            if (preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + dialog_id, false)) {
                popup = preferences.getInt("popup_" + dialog_id, 0);
            } else {
                popup = 0;
            }
            if (popup == 0) {
                if (isChannel) {
                    popup = preferences.getInt("popupChannel", 0);
                } else {
                    popup = preferences.getInt(((int) dialog_id) < 0 ? "popupGroup" : "popupAll", 0);
                }
            } else if (popup == 1) {
                popup = 3;
            } else if (popup == 2) {
                popup = 0;
            }
        }
        if (!(popup == 0 || messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
            popup = 0;
        }
        if (popup != 0) {
            popupArrayAdd.add(0, messageObject);
        }
        return popup;
    }

    public void processNewMessages(ArrayList<MessageObject> messageObjects, boolean isLast, boolean isFcm, CountDownLatch countDownLatch) {
        if (!messageObjects.isEmpty()) {
            notificationsQueue.postRunnable(new Runnable(messageObjects, new ArrayList<>(0), isFcm, isLast, countDownLatch) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ CountDownLatch f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$16$NotificationsController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public /* synthetic */ void lambda$processNewMessages$16$NotificationsController(ArrayList messageObjects, ArrayList popupArrayAdd, boolean isFcm, boolean isLast, CountDownLatch countDownLatch) {
        boolean canAddValue;
        Integer override;
        LongSparseArray<Boolean> settingsCache;
        boolean allowPinned;
        int a;
        boolean isChannel;
        long mid;
        long random_id;
        int lower_id;
        boolean value;
        boolean added;
        boolean edited;
        long original_dialog_id;
        int i;
        boolean value2;
        MessageObject messageObject;
        ArrayList arrayList = messageObjects;
        LongSparseArray<Boolean> settingsCache2 = new LongSparseArray<>();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        boolean allowPinned2 = preferences.getBoolean("PinnedMessages", true);
        boolean added2 = false;
        boolean edited2 = false;
        int popup = 0;
        boolean hasScheduled = false;
        int a2 = 0;
        while (a2 < messageObjects.size()) {
            MessageObject messageObject2 = (MessageObject) arrayList.get(a2);
            if (messageObject2.messageOwner != null && messageObject2.messageOwner.silent) {
                if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp) {
                    a = a2;
                    allowPinned = allowPinned2;
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) {
                    a = a2;
                    allowPinned = allowPinned2;
                }
                settingsCache = settingsCache2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            }
            a = a2;
            long mid2 = (long) messageObject2.getId();
            long random_id2 = messageObject2.isFcmMessage() ? messageObject2.messageOwner.random_id : 0;
            allowPinned = allowPinned2;
            long dialog_id = messageObject2.getDialogId();
            int lower_id2 = (int) dialog_id;
            if (messageObject2.messageOwner.to_id.channel_id != 0) {
                mid = mid2 | (((long) messageObject2.messageOwner.to_id.channel_id) << 32);
                isChannel = true;
            } else {
                mid = mid2;
                isChannel = false;
            }
            MessageObject oldMessageObject = this.pushMessagesDict.get(mid);
            if (oldMessageObject == null) {
                random_id = random_id2;
                int lower_id3 = lower_id2;
                if (messageObject2.messageOwner.random_id != 0) {
                    lower_id = lower_id3;
                    oldMessageObject = this.fcmRandomMessagesDict.get(messageObject2.messageOwner.random_id);
                    if (oldMessageObject != null) {
                        this.fcmRandomMessagesDict.remove(messageObject2.messageOwner.random_id);
                    }
                } else {
                    lower_id = lower_id3;
                }
            } else {
                lower_id = lower_id2;
                random_id = random_id2;
            }
            if (oldMessageObject == null) {
                MessageObject messageObject3 = messageObject2;
                long mid3 = mid;
                int lower_id4 = lower_id;
                long random_id3 = random_id;
                MessageObject messageObject4 = oldMessageObject;
                if (!edited2) {
                    if (isFcm) {
                        getMessagesStorage().putPushMessage(messageObject3);
                    }
                    long original_dialog_id2 = dialog_id;
                    if (dialog_id != this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                        if (messageObject3.messageOwner.mentioned) {
                            if (allowPinned || !(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                                dialog_id = (long) messageObject3.messageOwner.from_id;
                            }
                        }
                        if (isPersonalMessage(messageObject3)) {
                            this.personal_count++;
                        }
                        boolean z = lower_id4 < 0;
                        int index = settingsCache2.indexOfKey(dialog_id);
                        if (index >= 0) {
                            value = settingsCache2.valueAt(index).booleanValue();
                        } else {
                            int notifyOverride = getNotifyOverride(preferences, dialog_id);
                            if (notifyOverride == -1) {
                                value2 = isGlobalNotificationsEnabled(dialog_id);
                            } else {
                                value2 = notifyOverride != 2;
                            }
                            settingsCache2.put(dialog_id, Boolean.valueOf(value2));
                            value = value2;
                        }
                        if (value) {
                            if (!isFcm) {
                                int i2 = index;
                                added = true;
                                edited = edited2;
                                original_dialog_id = original_dialog_id2;
                                settingsCache = settingsCache2;
                                i = 0;
                                popup = addToPopupMessages(popupArrayAdd, messageObject3, lower_id4, dialog_id, isChannel, preferences);
                            } else {
                                settingsCache = settingsCache2;
                                added = true;
                                edited = edited2;
                                i = 0;
                                original_dialog_id = original_dialog_id2;
                            }
                            if (!hasScheduled) {
                                hasScheduled = messageObject3.messageOwner.from_scheduled;
                            }
                            this.delayedPushMessages.add(messageObject3);
                            this.pushMessages.add(i, messageObject3);
                            long mid4 = mid3;
                            if (mid4 != 0) {
                                this.pushMessagesDict.put(mid4, messageObject3);
                                long j = random_id3;
                            } else {
                                long random_id4 = random_id3;
                                if (random_id4 != 0) {
                                    this.fcmRandomMessagesDict.put(random_id4, messageObject3);
                                }
                            }
                            if (original_dialog_id != dialog_id) {
                                Integer current = this.pushDialogsOverrideMention.get(original_dialog_id);
                                this.pushDialogsOverrideMention.put(original_dialog_id, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                            }
                            edited2 = edited;
                            added2 = added;
                        } else {
                            int i3 = index;
                            settingsCache = settingsCache2;
                            long j2 = random_id3;
                            long j3 = original_dialog_id2;
                            long j4 = mid3;
                            edited2 = edited2;
                            added2 = true;
                        }
                        a2 = a + 1;
                        arrayList = messageObjects;
                        allowPinned2 = allowPinned;
                        settingsCache2 = settingsCache;
                    } else if (!isFcm) {
                        playInChatSound();
                    }
                }
                settingsCache = settingsCache2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            } else if (oldMessageObject.isFcmMessage()) {
                this.pushMessagesDict.put(mid, messageObject2);
                int idxOld = this.pushMessages.indexOf(oldMessageObject);
                if (idxOld >= 0) {
                    this.pushMessages.set(idxOld, messageObject2);
                    long j5 = random_id;
                    int i4 = idxOld;
                    MessageObject messageObject5 = oldMessageObject;
                    long j6 = mid;
                    messageObject = messageObject2;
                    popup = addToPopupMessages(popupArrayAdd, messageObject2, lower_id, dialog_id, isChannel, preferences);
                } else {
                    messageObject = messageObject2;
                    long j7 = mid;
                    int i5 = lower_id;
                    long j8 = random_id;
                    MessageObject messageObject6 = oldMessageObject;
                }
                if (isFcm) {
                    boolean z2 = messageObject.localEdit;
                    edited2 = z2;
                    if (z2) {
                        getMessagesStorage().putPushMessage(messageObject);
                    }
                }
                settingsCache = settingsCache2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            } else {
                long j9 = mid;
                int i6 = lower_id;
                long j10 = random_id;
                MessageObject messageObject7 = oldMessageObject;
                settingsCache = settingsCache2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache2 = settingsCache;
            }
        }
        int i7 = a2;
        LongSparseArray<Boolean> longSparseArray = settingsCache2;
        boolean z3 = allowPinned2;
        boolean edited3 = edited2;
        if (added2) {
            this.notifyCheck = isLast;
        } else {
            boolean z4 = isLast;
        }
        if (popupArrayAdd.isEmpty() || AndroidUtilities.needShowPasscode(false)) {
            ArrayList arrayList2 = popupArrayAdd;
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(popupArrayAdd, popup) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$14$NotificationsController(this.f$1, this.f$2);
                }
            });
        }
        if (!isFcm && !hasScheduled) {
            ArrayList arrayList3 = messageObjects;
        } else if (edited3) {
            this.delayedPushMessages.clear();
            showOrUpdateNotification(this.notifyCheck);
            ArrayList arrayList4 = messageObjects;
        } else if (added2) {
            long dialog_id2 = ((MessageObject) messageObjects.get(0)).getDialogId();
            int old_unread_count = this.total_unread_count;
            int notifyOverride2 = getNotifyOverride(preferences, dialog_id2);
            if (notifyOverride2 == -1) {
                canAddValue = isGlobalNotificationsEnabled(dialog_id2);
            } else {
                canAddValue = notifyOverride2 != 2;
            }
            Integer currentCount = this.pushDialogs.get(dialog_id2);
            Integer newCount = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
            if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialog_id2)) != null && override.intValue() != 0) {
                canAddValue = true;
                newCount = override;
            }
            if (canAddValue) {
                if (currentCount != null) {
                    this.total_unread_count -= currentCount.intValue();
                }
                this.total_unread_count += newCount.intValue();
                this.pushDialogs.put(dialog_id2, newCount);
            }
            if (old_unread_count != this.total_unread_count) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
                AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        NotificationsController.this.lambda$null$15$NotificationsController(this.f$1);
                    }
                });
            }
            this.notifyCheck = false;
            if (this.showBadgeNumber) {
                setBadge(getTotalAllUnreadCount());
            }
        } else {
            ArrayList arrayList5 = messageObjects;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public /* synthetic */ void lambda$null$14$NotificationsController(ArrayList popupArrayAdd, int popupFinal) {
        this.popupMessages.addAll(0, popupArrayAdd);
        if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || SharedConfig.isWaitingForPasscodeEnter)) {
            return;
        }
        if (popupFinal == 3 || ((popupFinal == 1 && ApplicationLoader.isScreenOn) || (popupFinal == 2 && !ApplicationLoader.isScreenOn))) {
            Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
            popupIntent.setFlags(268763140);
            try {
                ApplicationLoader.applicationContext.startActivity(popupIntent);
            } catch (Throwable th) {
            }
        }
    }

    public /* synthetic */ void lambda$null$15$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> dialogsToUpdate) {
        notificationsQueue.postRunnable(new Runnable(dialogsToUpdate, new ArrayList<>()) {
            private final /* synthetic */ LongSparseArray f$1;
            private final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$19$NotificationsController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$processDialogsUpdateRead$19$NotificationsController(LongSparseArray dialogsToUpdate, ArrayList popupArrayToRemove) {
        boolean z;
        long dialog_id;
        Integer currentCount;
        Integer override;
        LongSparseArray longSparseArray = dialogsToUpdate;
        ArrayList arrayList = popupArrayToRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        int b = 0;
        while (true) {
            boolean canAddValue = false;
            z = true;
            if (b >= dialogsToUpdate.size()) {
                break;
            }
            long dialog_id2 = longSparseArray.keyAt(b);
            int notifyOverride = getNotifyOverride(preferences, dialog_id2);
            if (notifyOverride == -1) {
                canAddValue = isGlobalNotificationsEnabled(dialog_id2);
            } else if (notifyOverride != 2) {
                canAddValue = true;
            }
            Integer currentCount2 = this.pushDialogs.get(dialog_id2);
            Integer newCount = (Integer) longSparseArray.get(dialog_id2);
            if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialog_id2)) != null && override.intValue() != 0) {
                canAddValue = true;
                newCount = override;
            }
            if (newCount.intValue() == 0) {
                this.smartNotificationsDialogs.remove(dialog_id2);
            }
            if (newCount.intValue() < 0) {
                if (currentCount2 == null) {
                    b++;
                } else {
                    newCount = Integer.valueOf(currentCount2.intValue() + newCount.intValue());
                }
            }
            if ((canAddValue || newCount.intValue() == 0) && currentCount2 != null) {
                this.total_unread_count -= currentCount2.intValue();
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id2);
                this.pushDialogsOverrideMention.remove(dialog_id2);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (messageObject.messageOwner.from_scheduled || messageObject.getDialogId() != dialog_id2) {
                        dialog_id = dialog_id2;
                        currentCount = currentCount2;
                    } else {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count -= z ? 1 : 0;
                        }
                        this.pushMessages.remove(a);
                        a--;
                        this.delayedPushMessages.remove(messageObject);
                        dialog_id = dialog_id2;
                        long mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            currentCount = currentCount2;
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        } else {
                            currentCount = currentCount2;
                        }
                        this.pushMessagesDict.remove(mid);
                        arrayList.add(messageObject);
                    }
                    z = true;
                    a++;
                    currentCount2 = currentCount;
                    dialog_id2 = dialog_id;
                }
                Integer num = currentCount2;
            } else {
                long dialog_id3 = dialog_id2;
                Integer num2 = currentCount2;
                if (canAddValue) {
                    this.total_unread_count += newCount.intValue();
                    this.pushDialogs.put(dialog_id3, newCount);
                }
            }
            b++;
        }
        if (popupArrayToRemove.isEmpty() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$17$NotificationsController(this.f$1);
                }
            });
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    NotificationsController.this.lambda$null$18$NotificationsController(this.f$1);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$17$NotificationsController(ArrayList popupArrayToRemove) {
        int size = popupArrayToRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayToRemove.get(a));
        }
    }

    public /* synthetic */ void lambda$null$18$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> dialogs, ArrayList<TLRPC.Message> messages, ArrayList<MessageObject> push, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, ArrayList<TLRPC.EncryptedChat> encryptedChats) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        notificationsQueue.postRunnable(new Runnable(messages, dialogs, push) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ LongSparseArray f$2;
            private final /* synthetic */ ArrayList f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$21$NotificationsController(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$processLoadedUnreadMessages$21$NotificationsController(ArrayList messages, LongSparseArray dialogs, ArrayList push) {
        int a;
        int notifyOverride;
        long original_dialog_id;
        boolean value;
        int notifyOverride2;
        boolean value2;
        int a2;
        long dialog_id;
        boolean value3;
        ArrayList arrayList = messages;
        LongSparseArray longSparseArray = dialogs;
        ArrayList arrayList2 = push;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z = false;
        this.total_unread_count = 0;
        this.personal_count = 0;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        LongSparseArray<Boolean> settingsCache = new LongSparseArray<>();
        char c = ' ';
        int i = 1;
        if (arrayList != null) {
            int a3 = 0;
            while (a3 < messages.size()) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(a3);
                if (message != null && message.silent) {
                    if (message.action instanceof TLRPC.TL_messageActionContactSignUp) {
                        a2 = a3;
                    } else if (message.action instanceof TLRPC.TL_messageActionUserJoined) {
                        a2 = a3;
                    }
                    a3 = a2 + 1;
                    arrayList = messages;
                    ArrayList arrayList3 = push;
                    z = false;
                    c = ' ';
                    i = 1;
                }
                long mid = (long) message.id;
                if (message.to_id.channel_id != 0) {
                    mid |= ((long) message.to_id.channel_id) << c;
                }
                if (this.pushMessagesDict.indexOfKey(mid) >= 0) {
                    a2 = a3;
                } else {
                    MessageObject messageObject = new MessageObject(this.currentAccount, message, z);
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count += i;
                    }
                    long dialog_id2 = messageObject.getDialogId();
                    long original_dialog_id2 = dialog_id2;
                    if (messageObject.messageOwner.mentioned) {
                        a2 = a3;
                        dialog_id = (long) messageObject.messageOwner.from_id;
                    } else {
                        a2 = a3;
                        dialog_id = dialog_id2;
                    }
                    int index = settingsCache.indexOfKey(dialog_id);
                    if (index >= 0) {
                        value3 = settingsCache.valueAt(index).booleanValue();
                    } else {
                        int notifyOverride3 = getNotifyOverride(preferences, dialog_id);
                        if (notifyOverride3 == -1) {
                            value3 = isGlobalNotificationsEnabled(dialog_id);
                        } else {
                            value3 = notifyOverride3 != 2;
                        }
                        settingsCache.put(dialog_id, Boolean.valueOf(value3));
                    }
                    if (!value3) {
                    } else if (dialog_id != this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                        this.pushMessagesDict.put(mid, messageObject);
                        this.pushMessages.add(0, messageObject);
                        long original_dialog_id3 = original_dialog_id2;
                        if (original_dialog_id3 != dialog_id) {
                            Integer current = this.pushDialogsOverrideMention.get(original_dialog_id3);
                            this.pushDialogsOverrideMention.put(original_dialog_id3, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                        }
                    }
                }
                a3 = a2 + 1;
                arrayList = messages;
                ArrayList arrayList32 = push;
                z = false;
                c = ' ';
                i = 1;
            }
            int i2 = a3;
        }
        for (int a4 = 0; a4 < dialogs.size(); a4++) {
            long dialog_id3 = longSparseArray.keyAt(a4);
            int index2 = settingsCache.indexOfKey(dialog_id3);
            if (index2 >= 0) {
                notifyOverride2 = settingsCache.valueAt(index2).booleanValue();
            } else {
                int notifyOverride4 = getNotifyOverride(preferences, dialog_id3);
                if (notifyOverride4 == -1) {
                    value2 = isGlobalNotificationsEnabled(dialog_id3);
                } else {
                    value2 = notifyOverride4 != 2;
                }
                settingsCache.put(dialog_id3, Boolean.valueOf(value2));
                notifyOverride2 = value2;
            }
            if (notifyOverride2 != 0) {
                int count = ((Integer) longSparseArray.valueAt(a4)).intValue();
                this.pushDialogs.put(dialog_id3, Integer.valueOf(count));
                this.total_unread_count += count;
            }
        }
        ArrayList arrayList4 = push;
        if (arrayList4 != null) {
            int a5 = 0;
            while (a5 < push.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList4.get(a5);
                long mid2 = (long) messageObject2.getId();
                if (messageObject2.messageOwner.to_id.channel_id != 0) {
                    mid2 |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                }
                if (this.pushMessagesDict.indexOfKey(mid2) >= 0) {
                    a = a5;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personal_count++;
                    }
                    long dialog_id4 = messageObject2.getDialogId();
                    long original_dialog_id4 = dialog_id4;
                    long random_id = messageObject2.messageOwner.random_id;
                    if (messageObject2.messageOwner.mentioned) {
                        dialog_id4 = (long) messageObject2.messageOwner.from_id;
                    }
                    int index3 = settingsCache.indexOfKey(dialog_id4);
                    if (index3 >= 0) {
                        notifyOverride = settingsCache.valueAt(index3).booleanValue();
                    } else {
                        int notifyOverride5 = getNotifyOverride(preferences, dialog_id4);
                        if (notifyOverride5 == -1) {
                            value = isGlobalNotificationsEnabled(dialog_id4);
                        } else {
                            value = notifyOverride5 != 2;
                        }
                        settingsCache.put(dialog_id4, Boolean.valueOf(value));
                        notifyOverride = value;
                    }
                    if (notifyOverride == 0) {
                        a = a5;
                        MessageObject messageObject3 = messageObject2;
                    } else if (dialog_id4 != this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                        if (mid2 != 0) {
                            this.pushMessagesDict.put(mid2, messageObject2);
                        } else if (random_id != 0) {
                            this.fcmRandomMessagesDict.put(random_id, messageObject2);
                        }
                        this.pushMessages.add(0, messageObject2);
                        if (original_dialog_id4 != dialog_id4) {
                            a = a5;
                            original_dialog_id = original_dialog_id4;
                            Integer current2 = this.pushDialogsOverrideMention.get(original_dialog_id);
                            MessageObject messageObject4 = messageObject2;
                            Integer num = current2;
                            this.pushDialogsOverrideMention.put(original_dialog_id, Integer.valueOf(current2 == null ? 1 : current2.intValue() + 1));
                        } else {
                            a = a5;
                            MessageObject messageObject5 = messageObject2;
                            original_dialog_id = original_dialog_id4;
                        }
                        Integer currentCount = this.pushDialogs.get(dialog_id4);
                        Integer newCount = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
                        if (currentCount != null) {
                            long j = original_dialog_id;
                            this.total_unread_count -= currentCount.intValue();
                        }
                        this.total_unread_count += newCount.intValue();
                        this.pushDialogs.put(dialog_id4, newCount);
                    } else {
                        a = a5;
                    }
                }
                a5 = a + 1;
                LongSparseArray longSparseArray2 = dialogs;
                arrayList4 = push;
            }
            int i3 = a5;
        }
        AndroidUtilities.runOnUIThread(new Runnable(this.pushDialogs.size()) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$null$20$NotificationsController(this.f$1);
            }
        });
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    public /* synthetic */ void lambda$null$20$NotificationsController(int pushDialogsCount) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    private int getTotalAllUnreadCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                NotificationsController controller = getInstance(a);
                if (controller.showBadgeNumber) {
                    if (controller.showBadgeMessages) {
                        if (controller.showBadgeMuted) {
                            try {
                                int N = MessagesController.getInstance(a).allDialogs.size();
                                for (int i = 0; i < N; i++) {
                                    TLRPC.Dialog dialog = MessagesController.getInstance(a).allDialogs.get(i);
                                    if (dialog.unread_count != 0) {
                                        count += dialog.unread_count;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else {
                            count += controller.total_unread_count;
                        }
                    } else if (controller.showBadgeMuted) {
                        try {
                            int N2 = MessagesController.getInstance(a).allDialogs.size();
                            for (int i2 = 0; i2 < N2; i2++) {
                                if (MessagesController.getInstance(a).allDialogs.get(i2).unread_count != 0) {
                                    count++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else {
                        count += controller.pushDialogs.size();
                    }
                }
            }
        }
        return count;
    }

    public /* synthetic */ void lambda$updateBadge$22$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$updateBadge$22$NotificationsController();
            }
        });
    }

    private void setBadge(int count) {
        if (this.lastBadgeCount != count) {
            this.lastBadgeCount = count;
            NotificationBadge.applyCount(count);
        }
    }

    private String getShortStringForMessage(MessageObject messageObject, String[] userName, boolean[] preview) {
        int i;
        char c;
        int i2;
        char c2;
        int i3;
        char c3;
        char c4;
        char c5;
        MessageObject messageObject2 = messageObject;
        if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
        }
        long dialog_id = messageObject2.messageOwner.dialog_id;
        int chat_id = messageObject2.messageOwner.to_id.chat_id != 0 ? messageObject2.messageOwner.to_id.chat_id : messageObject2.messageOwner.to_id.channel_id;
        int from_id = messageObject2.messageOwner.to_id.user_id;
        if (preview != null) {
            preview[0] = true;
        }
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        boolean dialogPreviewEnabled = preferences.getBoolean("content_preview_" + dialog_id, true);
        if (messageObject.isFcmMessage()) {
            if (chat_id == 0 && from_id != 0) {
                if (Build.VERSION.SDK_INT > 27) {
                    userName[0] = messageObject2.localName;
                }
                if (!dialogPreviewEnabled || !preferences.getBoolean("EnablePreviewAll", true)) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    return LocaleController.getString("Message", R.string.Message);
                }
            } else if (chat_id != 0) {
                if (messageObject2.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) {
                    userName[0] = messageObject2.localUserName;
                } else if (Build.VERSION.SDK_INT > 27) {
                    userName[0] = messageObject2.localName;
                }
                if (!dialogPreviewEnabled || ((!messageObject2.localChannel && !preferences.getBoolean("EnablePreviewGroup", true)) || (messageObject2.localChannel && !preferences.getBoolean("EnablePreviewChannel", true)))) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    if (messageObject.isMegagroup() || messageObject2.messageOwner.to_id.channel_id == 0) {
                        return LocaleController.formatString("NotificationMessageGroupNoText", R.string.NotificationMessageGroupNoText, messageObject2.localUserName, messageObject2.localName);
                    }
                    return LocaleController.formatString("ChannelMessageNoText", R.string.ChannelMessageNoText, messageObject2.localName);
                }
            }
            return messageObject2.messageOwner.message;
        }
        if (from_id == 0) {
            if (messageObject.isFromUser() || messageObject.getId() < 0) {
                from_id = messageObject2.messageOwner.from_id;
            } else {
                from_id = -chat_id;
            }
        } else if (from_id == getUserConfig().getClientUserId()) {
            from_id = messageObject2.messageOwner.from_id;
        }
        if (dialog_id == 0) {
            if (chat_id != 0) {
                dialog_id = (long) (-chat_id);
            } else if (from_id != 0) {
                dialog_id = (long) from_id;
            }
        }
        String name = null;
        if (from_id > 0) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf(from_id));
            if (user != null) {
                name = UserObject.getName(user);
                if (chat_id != 0) {
                    userName[0] = name;
                } else if (Build.VERSION.SDK_INT > 27) {
                    userName[0] = name;
                } else {
                    userName[0] = null;
                }
            }
        } else {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-from_id));
            if (chat != null) {
                name = chat.title;
                userName[0] = name;
            }
        }
        if (name == null) {
            return null;
        }
        TLRPC.Chat chat2 = null;
        if (chat_id != 0) {
            chat2 = getMessagesController().getChat(Integer.valueOf(chat_id));
            if (chat2 == null) {
                return null;
            }
            if (ChatObject.isChannel(chat2) && !chat2.megagroup && Build.VERSION.SDK_INT <= 27) {
                userName[0] = null;
            }
        }
        if (((int) dialog_id) == 0) {
            userName[0] = null;
            return LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
        }
        boolean isChannel = ChatObject.isChannel(chat2) && !chat2.megagroup;
        if (!dialogPreviewEnabled || ((chat_id != 0 || from_id == 0 || !preferences.getBoolean("EnablePreviewAll", true)) && (chat_id == 0 || ((isChannel || !preferences.getBoolean("EnablePreviewGroup", true)) && (!isChannel || !preferences.getBoolean("EnablePreviewChannel", true)))))) {
            boolean z = isChannel;
            long j = dialog_id;
            if (preview != null) {
                preview[0] = false;
            }
            return LocaleController.getString("Message", R.string.Message);
        }
        int i4 = chat_id;
        boolean z2 = isChannel;
        long j2 = dialog_id;
        if (messageObject2.messageOwner instanceof TLRPC.TL_messageService) {
            userName[0] = null;
            if ((messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) || (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp)) {
                return LocaleController.formatString("NotificationContactJoined", R.string.NotificationContactJoined, name);
            } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto) {
                return LocaleController.formatString("NotificationContactNewPhoto", R.string.NotificationContactNewPhoto, name);
            } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation) {
                return LocaleController.formatString("NotificationUnrecognizedDevice", R.string.NotificationUnrecognizedDevice, getUserConfig().getCurrentUser().first_name, LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(((long) messageObject2.messageOwner.date) * 1000), LocaleController.getInstance().formatterDay.format(((long) messageObject2.messageOwner.date) * 1000)), messageObject2.messageOwner.action.title, messageObject2.messageOwner.action.address);
            } else if ((messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionGameScore) || (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent)) {
                return messageObject2.messageText.toString();
            } else {
                if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    TLRPC.PhoneCallDiscardReason reason = messageObject2.messageOwner.action.reason;
                    if (!messageObject.isOut() && (reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
                        return LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                    }
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser) {
                    int singleUserId = messageObject2.messageOwner.action.user_id;
                    if (singleUserId == 0 && messageObject2.messageOwner.action.users.size() == 1) {
                        singleUserId = messageObject2.messageOwner.action.users.get(0).intValue();
                    }
                    if (singleUserId == 0) {
                        StringBuilder names = new StringBuilder();
                        for (int a = 0; a < messageObject2.messageOwner.action.users.size(); a++) {
                            TLRPC.User user2 = getMessagesController().getUser(messageObject2.messageOwner.action.users.get(a));
                            if (user2 != null) {
                                String name2 = UserObject.getName(user2);
                                if (names.length() != 0) {
                                    names.append(", ");
                                }
                                names.append(name2);
                            }
                        }
                        return LocaleController.formatString("NotificationGroupAddMember", R.string.NotificationGroupAddMember, name, chat2.title, names.toString());
                    } else if (messageObject2.messageOwner.to_id.channel_id != 0 && !chat2.megagroup) {
                        return LocaleController.formatString("ChannelAddedByNotification", R.string.ChannelAddedByNotification, name, chat2.title);
                    } else if (singleUserId == getUserConfig().getClientUserId()) {
                        return LocaleController.formatString("NotificationInvitedToGroup", R.string.NotificationInvitedToGroup, name, chat2.title);
                    } else {
                        TLRPC.User u2 = getMessagesController().getUser(Integer.valueOf(singleUserId));
                        if (u2 == null) {
                            return null;
                        }
                        if (from_id != u2.id) {
                            return LocaleController.formatString("NotificationGroupAddMember", R.string.NotificationGroupAddMember, name, chat2.title, UserObject.getName(u2));
                        } else if (chat2.megagroup) {
                            return LocaleController.formatString("NotificationGroupAddSelfMega", R.string.NotificationGroupAddSelfMega, name, chat2.title);
                        } else {
                            return LocaleController.formatString("NotificationGroupAddSelf", R.string.NotificationGroupAddSelf, name, chat2.title);
                        }
                    }
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink) {
                    return LocaleController.formatString("NotificationInvitedToGroupByLink", R.string.NotificationInvitedToGroupByLink, name, chat2.title);
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle) {
                    return LocaleController.formatString("NotificationEditedGroupName", R.string.NotificationEditedGroupName, name, messageObject2.messageOwner.action.title);
                } else if ((messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto) || (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto)) {
                    if (messageObject2.messageOwner.to_id.channel_id == 0 || chat2.megagroup) {
                        return LocaleController.formatString("NotificationEditedGroupPhoto", R.string.NotificationEditedGroupPhoto, name, chat2.title);
                    }
                    return LocaleController.formatString("ChannelPhotoEditNotification", R.string.ChannelPhotoEditNotification, chat2.title);
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser) {
                    if (messageObject2.messageOwner.action.user_id == getUserConfig().getClientUserId()) {
                        return LocaleController.formatString("NotificationGroupKickYou", R.string.NotificationGroupKickYou, name, chat2.title);
                    } else if (messageObject2.messageOwner.action.user_id == from_id) {
                        return LocaleController.formatString("NotificationGroupLeftMember", R.string.NotificationGroupLeftMember, name, chat2.title);
                    } else {
                        TLRPC.User u22 = getMessagesController().getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id));
                        if (u22 == null) {
                            return null;
                        }
                        return LocaleController.formatString("NotificationGroupKickMember", R.string.NotificationGroupKickMember, name, chat2.title, UserObject.getName(u22));
                    }
                } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate) {
                    return messageObject2.messageText.toString();
                } else {
                    if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate) {
                        return messageObject2.messageText.toString();
                    }
                    if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo) {
                        return LocaleController.formatString("ActionMigrateFromGroupNotify", R.string.ActionMigrateFromGroupNotify, chat2.title);
                    } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom) {
                        return LocaleController.formatString("ActionMigrateFromGroupNotify", R.string.ActionMigrateFromGroupNotify, messageObject2.messageOwner.action.title);
                    } else if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken) {
                        return messageObject2.messageText.toString();
                    } else {
                        if (messageObject2.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage) {
                            if (chat2 == null) {
                                i = 1;
                            } else if (ChatObject.isChannel(chat2) && !chat2.megagroup) {
                                i = 1;
                            } else if (messageObject2.replyMessageObject == null) {
                                return LocaleController.formatString("NotificationActionPinnedNoText", R.string.NotificationActionPinnedNoText, name, chat2.title);
                            } else {
                                MessageObject object = messageObject2.replyMessageObject;
                                if (object.isMusic()) {
                                    return LocaleController.formatString("NotificationActionPinnedMusic", R.string.NotificationActionPinnedMusic, name, chat2.title);
                                } else if (object.isVideo()) {
                                    if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedVideo", R.string.NotificationActionPinnedVideo, name, chat2.title);
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, "📹 " + object.messageOwner.message, chat2.title);
                                } else if (object.isGif()) {
                                    if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedGif", R.string.NotificationActionPinnedGif, name, chat2.title);
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, "🎬 " + object.messageOwner.message, chat2.title);
                                } else if (object.isVoice()) {
                                    return LocaleController.formatString("NotificationActionPinnedVoice", R.string.NotificationActionPinnedVoice, name, chat2.title);
                                } else if (object.isRoundVideo()) {
                                    return LocaleController.formatString("NotificationActionPinnedRound", R.string.NotificationActionPinnedRound, name, chat2.title);
                                } else if (object.isSticker() || object.isAnimatedSticker()) {
                                    String emoji = object.getStickerEmoji();
                                    if (emoji != null) {
                                        return LocaleController.formatString("NotificationActionPinnedStickerEmoji", R.string.NotificationActionPinnedStickerEmoji, name, chat2.title, emoji);
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedSticker", R.string.NotificationActionPinnedSticker, name, chat2.title);
                                } else if (!(object.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                                    if (object.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) {
                                        c4 = 0;
                                        c3 = 1;
                                        i3 = 2;
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) {
                                        c4 = 0;
                                        c3 = 1;
                                        i3 = 2;
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                                        return LocaleController.formatString("NotificationActionPinnedGeoLive", R.string.NotificationActionPinnedGeoLive, name, chat2.title);
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                                        TLRPC.TL_messageMediaContact mediaContact = (TLRPC.TL_messageMediaContact) object.messageOwner.media;
                                        return LocaleController.formatString("NotificationActionPinnedContact2", R.string.NotificationActionPinnedContact2, name, chat2.title, ContactsController.formatName(mediaContact.first_name, mediaContact.last_name));
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                                        return LocaleController.formatString("NotificationActionPinnedPoll2", R.string.NotificationActionPinnedPoll2, name, chat2.title, ((TLRPC.TL_messageMediaPoll) object.messageOwner.media).poll.question);
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                                        if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedPhoto", R.string.NotificationActionPinnedPhoto, name, chat2.title);
                                        }
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, "🖼 " + object.messageOwner.message, chat2.title);
                                    } else if (object.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                                        return LocaleController.formatString("NotificationActionPinnedGame", R.string.NotificationActionPinnedGame, name, chat2.title);
                                    } else if (object.messageText == null || object.messageText.length() <= 0) {
                                        return LocaleController.formatString("NotificationActionPinnedNoText", R.string.NotificationActionPinnedNoText, name, chat2.title);
                                    } else {
                                        CharSequence message = object.messageText;
                                        if (message.length() > 20) {
                                            StringBuilder sb = new StringBuilder();
                                            c5 = 0;
                                            sb.append(message.subSequence(0, 20));
                                            sb.append("...");
                                            message = sb.toString();
                                        } else {
                                            c5 = 0;
                                        }
                                        Object[] objArr = new Object[3];
                                        objArr[c5] = name;
                                        objArr[1] = message;
                                        objArr[2] = chat2.title;
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, objArr);
                                    }
                                    Object[] objArr2 = new Object[i3];
                                    objArr2[c4] = name;
                                    objArr2[c3] = chat2.title;
                                    return LocaleController.formatString("NotificationActionPinnedGeo", R.string.NotificationActionPinnedGeo, objArr2);
                                } else if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    return LocaleController.formatString("NotificationActionPinnedFile", R.string.NotificationActionPinnedFile, name, chat2.title);
                                } else {
                                    return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, "📎 " + object.messageOwner.message, chat2.title);
                                }
                            }
                            if (messageObject2.replyMessageObject == null) {
                                Object[] objArr3 = new Object[i];
                                objArr3[0] = chat2.title;
                                return LocaleController.formatString("NotificationActionPinnedNoTextChannel", R.string.NotificationActionPinnedNoTextChannel, objArr3);
                            }
                            MessageObject object2 = messageObject2.replyMessageObject;
                            if (object2.isMusic()) {
                                return LocaleController.formatString("NotificationActionPinnedMusicChannel", R.string.NotificationActionPinnedMusicChannel, chat2.title);
                            } else if (object2.isVideo()) {
                                if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object2.messageOwner.message)) {
                                    return LocaleController.formatString("NotificationActionPinnedVideoChannel", R.string.NotificationActionPinnedVideoChannel, chat2.title);
                                }
                                return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat2.title, "📹 " + object2.messageOwner.message);
                            } else if (object2.isGif()) {
                                if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object2.messageOwner.message)) {
                                    return LocaleController.formatString("NotificationActionPinnedGifChannel", R.string.NotificationActionPinnedGifChannel, chat2.title);
                                }
                                return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat2.title, "🎬 " + object2.messageOwner.message);
                            } else if (object2.isVoice()) {
                                return LocaleController.formatString("NotificationActionPinnedVoiceChannel", R.string.NotificationActionPinnedVoiceChannel, chat2.title);
                            } else if (object2.isRoundVideo()) {
                                return LocaleController.formatString("NotificationActionPinnedRoundChannel", R.string.NotificationActionPinnedRoundChannel, chat2.title);
                            } else if (object2.isSticker() || object2.isAnimatedSticker()) {
                                String emoji2 = object2.getStickerEmoji();
                                if (emoji2 != null) {
                                    return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", R.string.NotificationActionPinnedStickerEmojiChannel, chat2.title, emoji2);
                                }
                                return LocaleController.formatString("NotificationActionPinnedStickerChannel", R.string.NotificationActionPinnedStickerChannel, chat2.title);
                            } else if (!(object2.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                                if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) {
                                    i2 = 1;
                                    c = 0;
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaVenue) {
                                    i2 = 1;
                                    c = 0;
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                                    return LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", R.string.NotificationActionPinnedGeoLiveChannel, chat2.title);
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                                    TLRPC.TL_messageMediaContact mediaContact2 = (TLRPC.TL_messageMediaContact) object2.messageOwner.media;
                                    return LocaleController.formatString("NotificationActionPinnedContactChannel2", R.string.NotificationActionPinnedContactChannel2, chat2.title, ContactsController.formatName(mediaContact2.first_name, mediaContact2.last_name));
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                                    return LocaleController.formatString("NotificationActionPinnedPollChannel2", R.string.NotificationActionPinnedPollChannel2, chat2.title, ((TLRPC.TL_messageMediaPoll) object2.messageOwner.media).poll.question);
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
                                    if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object2.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedPhotoChannel", R.string.NotificationActionPinnedPhotoChannel, chat2.title);
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat2.title, "🖼 " + object2.messageOwner.message);
                                } else if (object2.messageOwner.media instanceof TLRPC.TL_messageMediaGame) {
                                    return LocaleController.formatString("NotificationActionPinnedGameChannel", R.string.NotificationActionPinnedGameChannel, chat2.title);
                                } else if (object2.messageText == null || object2.messageText.length() <= 0) {
                                    return LocaleController.formatString("NotificationActionPinnedNoTextChannel", R.string.NotificationActionPinnedNoTextChannel, chat2.title);
                                } else {
                                    CharSequence message2 = object2.messageText;
                                    if (message2.length() > 20) {
                                        StringBuilder sb2 = new StringBuilder();
                                        c2 = 0;
                                        sb2.append(message2.subSequence(0, 20));
                                        sb2.append("...");
                                        message2 = sb2.toString();
                                    } else {
                                        c2 = 0;
                                    }
                                    Object[] objArr4 = new Object[2];
                                    objArr4[c2] = chat2.title;
                                    objArr4[1] = message2;
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, objArr4);
                                }
                                Object[] objArr5 = new Object[i2];
                                objArr5[c] = chat2.title;
                                return LocaleController.formatString("NotificationActionPinnedGeoChannel", R.string.NotificationActionPinnedGeoChannel, objArr5);
                            } else if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(object2.messageOwner.message)) {
                                return LocaleController.formatString("NotificationActionPinnedFileChannel", R.string.NotificationActionPinnedFileChannel, chat2.title);
                            } else {
                                return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat2.title, "📎 " + object2.messageOwner.message);
                            }
                        } else if (messageObject2.messageOwner.action instanceof TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) {
                            TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer action = (TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) messageObject2.messageOwner.action;
                            if (action.trans == 0) {
                                TLRPC.User receiver = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(action.receiver.user_id));
                                TLRPC.User sender = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(action.sender.user_id));
                                RedpacketResponse bean = (RedpacketResponse) TLJsonResolve.parse((TLObject) action.data, (Class<?>) RedpacketResponse.class).model;
                                StringBuilder builder = new StringBuilder();
                                if (bean != null) {
                                    if (messageObject.isOut()) {
                                        if (getUserConfig().clientUserId == action.sender.user_id) {
                                            builder.append(LocaleController.getString(R.string.YouReceivedYourPacket));
                                        } else {
                                            builder.append(String.format(LocaleController.getString(R.string.YouReceivePacketFrom), new Object[]{UserObject.getName(sender)}));
                                        }
                                    } else if (getUserConfig().clientUserId == action.sender.user_id) {
                                        builder.append(UserObject.getName(receiver));
                                        builder.append(" ");
                                        builder.append(LocaleController.getString(R.string.ReceivedYourPacket));
                                    } else {
                                        builder.append(String.format(LocaleController.getString(R.string.WhoReceivePacketFrom), new Object[]{UserObject.getName(receiver), UserObject.getName(sender)}));
                                    }
                                }
                                return builder.toString();
                            }
                        }
                    }
                }
            }
        } else if (messageObject.isMediaEmpty()) {
            if (!TextUtils.isEmpty(messageObject2.messageText)) {
                return messageObject2.messageText.toString();
            }
            if (!TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                return messageObject2.messageOwner.message;
            }
            return LocaleController.getString("Message", R.string.Message);
        } else if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) {
            if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                return "🖼 " + messageObject2.messageOwner.message;
            } else if (messageObject2.messageOwner.media.ttl_seconds != 0) {
                return LocaleController.getString("AttachDestructingPhoto", R.string.AttachDestructingPhoto);
            } else {
                return LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
            }
        } else if (messageObject2.messageOwner.media instanceof TLRPCRedpacket.CL_messagesRpkTransferMedia) {
            TLRPCRedpacket.CL_messagesRpkTransferMedia media = (TLRPCRedpacket.CL_messagesRpkTransferMedia) messageObject2.messageOwner.media;
            if (media.trans == 0) {
                RedpacketResponse bean2 = null;
                if (media.data != null) {
                    bean2 = TLJsonResolve.parse((TLObject) media.data, (Class<?>) RedpacketResponse.class).model;
                }
                if (bean2 == null) {
                    return LocaleController.getString(R.string.ReceivePacketMessage);
                }
                return String.format(LocaleController.getString(R.string.ReceiveRedPackFrom), new Object[]{UserObject.getName(getMessagesController().getUser(Integer.valueOf(bean2.getRed().getInitiatorUserIdInt())))});
            } else if (media.trans == 1 || media.trans == 2) {
                TransferResponse bean3 = null;
                if (media.data != null) {
                    bean3 = TLJsonResolve.parse((TLObject) media.data, (Class<?>) TransferResponse.class).model;
                }
                if (bean3 == null) {
                    return LocaleController.getString(R.string.TransferMessages);
                }
                TransferResponse.Status state = bean3.getState();
                if (!messageObject.isOutOwner()) {
                    int sender_id = bean3.getInitiatorUserIdInt();
                    int receiver_id = Integer.parseInt(bean3.getRecipientUserId());
                    TLRPC.User sender2 = getMessagesController().getUser(Integer.valueOf(sender_id));
                    TLRPC.User receiver2 = getMessagesController().getUser(Integer.valueOf(receiver_id));
                    if (state == TransferResponse.Status.WAITING) {
                        return String.format(LocaleController.getString(R.string.TransferReceivedFromSomebody), new Object[]{UserObject.getName(sender2)});
                    } else if (state == TransferResponse.Status.RECEIVED) {
                        if (sender_id != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                            return LocaleController.getString(R.string.YouHaveConfirmedReceipt);
                        }
                        return String.format(LocaleController.getString(R.string.TransferReceivedBySomebody), new Object[]{UserObject.getName(receiver2)});
                    } else if (state == TransferResponse.Status.REFUSED) {
                        if (sender_id != UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                            return LocaleController.getString(R.string.YouHaveReturned);
                        }
                        return String.format(LocaleController.getString(R.string.TransferReturnedBySomebody), new Object[]{UserObject.getName(receiver2)});
                    } else if (state == TransferResponse.Status.TIMEOUT) {
                        return String.format(LocaleController.getString(R.string.TransferSendToSomebodyExpired), new Object[]{UserObject.getName(receiver2)});
                    }
                } else if (state == TransferResponse.Status.WAITING) {
                    return LocaleController.getString(R.string.TransferWaitingOtherCollect);
                } else {
                    if (state == TransferResponse.Status.RECEIVED) {
                        if (bean3.getInitiatorUserIdInt() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                            return LocaleController.getString(R.string.TransferOtherHasCollected);
                        }
                        return LocaleController.getString(R.string.YouHaveConfirmedReceipt);
                    } else if (state == TransferResponse.Status.REFUSED) {
                        if (bean3.getInitiatorUserIdInt() == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId) {
                            return LocaleController.getString(R.string.TransferHasBeenReturned);
                        }
                        return LocaleController.getString(R.string.YouHaveReturned);
                    } else if (state == TransferResponse.Status.TIMEOUT) {
                        return LocaleController.getString(R.string.TransferHasReturned);
                    }
                }
            }
        } else if (messageObject.isVideo()) {
            if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                return "📹 " + messageObject2.messageOwner.message;
            } else if (messageObject2.messageOwner.media.ttl_seconds != 0) {
                return LocaleController.getString("AttachDestructingVideo", R.string.AttachDestructingVideo);
            } else {
                return LocaleController.getString("AttachVideo", R.string.AttachVideo);
            }
        } else if (messageObject.isGame()) {
            return LocaleController.getString("AttachGame", R.string.AttachGame);
        } else {
            if (messageObject.isVoice()) {
                return LocaleController.getString("AttachAudio", R.string.AttachAudio);
            }
            if (messageObject.isRoundVideo()) {
                return LocaleController.getString("AttachRound", R.string.AttachRound);
            }
            if (messageObject.isMusic()) {
                return LocaleController.getString("AttachMusic", R.string.AttachMusic);
            }
            if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaContact) {
                return LocaleController.getString("AttachContact", R.string.AttachContact);
            }
            if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaPoll) {
                return LocaleController.getString("Poll", R.string.Poll);
            }
            if ((messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGeo) || (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) {
                return LocaleController.getString("AttachLocation", R.string.AttachLocation);
            }
            if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive) {
                return LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
            }
            if (messageObject2.messageOwner.media instanceof TLRPC.TL_messageMediaDocument) {
                if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                    String emoji3 = messageObject.getStickerEmoji();
                    if (emoji3 == null) {
                        return LocaleController.getString("AttachSticker", R.string.AttachSticker);
                    }
                    return emoji3 + " " + LocaleController.getString("AttachSticker", R.string.AttachSticker);
                } else if (messageObject.isGif()) {
                    if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                        return LocaleController.getString("AttachGif", R.string.AttachGif);
                    }
                    return "🎬 " + messageObject2.messageOwner.message;
                } else if (Build.VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                    return LocaleController.getString("AttachDocument", R.string.AttachDocument);
                } else {
                    return "📎 " + messageObject2.messageOwner.message;
                }
            }
        }
        return null;
    }

    /* JADX WARNING: type inference failed for: r4v174 */
    /* JADX WARNING: type inference failed for: r4v175 */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x08b1, code lost:
        if (r8.getBoolean("EnablePreviewGroup", true) == false) goto L_0x08b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x08bf, code lost:
        if (r8.getBoolean("EnablePreviewChannel", true) != false) goto L_0x08c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x08c5, code lost:
        if ((r1.messageOwner instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService) == false) goto L_0x127e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x08cd, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatAddUser) == false) goto L_0x09ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x08cf, code lost:
        r2 = r1.messageOwner.action.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x08d5, code lost:
        if (r2 != 0) goto L_0x08f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x08e2, code lost:
        if (r1.messageOwner.action.users.size() != 1) goto L_0x08f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x08e4, code lost:
        r2 = r1.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x08f5, code lost:
        if (r2 == 0) goto L_0x0992;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x08fd, code lost:
        if (r1.messageOwner.to_id.channel_id == 0) goto L_0x091a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0901, code lost:
        if (r12.megagroup != false) goto L_0x091a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x091a, code lost:
        if (r2 != r10) goto L_0x0933;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0933, code lost:
        r4 = getMessagesController().getUser(java.lang.Integer.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x093f, code lost:
        if (r4 != null) goto L_0x0943;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0941, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0945, code lost:
        if (r6 != r4.id) goto L_0x0975;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0949, code lost:
        if (r12.megagroup == false) goto L_0x0960;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x094b, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", im.bclpbkiauv.messenger.R.string.NotificationGroupAddSelfMega, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x0960, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupAddSelf", im.bclpbkiauv.messenger.R.string.NotificationGroupAddSelf, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x0975, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupAddMember", im.bclpbkiauv.messenger.R.string.NotificationGroupAddMember, r14, r12.title, im.bclpbkiauv.messenger.UserObject.getName(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0992, code lost:
        r4 = new java.lang.StringBuilder();
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x09a2, code lost:
        if (r5 >= r1.messageOwner.action.users.size()) goto L_0x09cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x09a4, code lost:
        r7 = getMessagesController().getUser(r1.messageOwner.action.users.get(r5));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x09b8, code lost:
        if (r7 == null) goto L_0x09cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x09ba, code lost:
        r11 = im.bclpbkiauv.messenger.UserObject.getName(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x09c2, code lost:
        if (r4.length() == 0) goto L_0x09c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x09c4, code lost:
        r4.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x09c9, code lost:
        r4.append(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x09cc, code lost:
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x09f3, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatJoinedByLink) == false) goto L_0x0a0b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0a11, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditTitle) == false) goto L_0x0a2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0a33, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditPhoto) != false) goto L_0x1249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0a3b, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeletePhoto) == false) goto L_0x0a3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0a45, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeleteUser) == false) goto L_0x0ab6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x0a4d, code lost:
        if (r1.messageOwner.action.user_id != r10) goto L_0x0a65;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x0a6b, code lost:
        if (r1.messageOwner.action.user_id != r6) goto L_0x0a83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0a83, code lost:
        r2 = getMessagesController().getUser(java.lang.Integer.valueOf(r1.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0a95, code lost:
        if (r2 != null) goto L_0x0a99;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x0a97, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0abc, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatCreate) == false) goto L_0x0ac6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0acc, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelCreate) == false) goto L_0x0ad6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0adc, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatMigrateTo) == false) goto L_0x0af1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0af7, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelMigrateFrom) == false) goto L_0x0b10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0b16, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionScreenshotTaken) == false) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0b26, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPinMessage) == false) goto L_0x116e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0b28, code lost:
        if (r12 == null) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0b2e, code lost:
        if (im.bclpbkiauv.messenger.ChatObject.isChannel(r12) == false) goto L_0x0b34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0b32, code lost:
        if (r12.megagroup == false) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0b36, code lost:
        if (r1.replyMessageObject != null) goto L_0x0b4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0b4e, code lost:
        r4 = r1.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0b54, code lost:
        if (r4.isMusic() == false) goto L_0x0b6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0b73, code lost:
        if (r4.isVideo() == false) goto L_0x0bc7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0b79, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bb0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0b83, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0bb0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0bcb, code lost:
        if (r4.isGif() == false) goto L_0x0c1f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x0bd1, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0bdb, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0c08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0c23, code lost:
        if (r4.isVoice() == false) goto L_0x0c3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0c40, code lost:
        if (r4.isRoundVideo() == false) goto L_0x0c59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0c5d, code lost:
        if (r4.isSticker() != false) goto L_0x0e3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0c63, code lost:
        if (r4.isAnimatedSticker() == false) goto L_0x0c67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x0c6d, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument) == false) goto L_0x0cc1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0c73, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0caa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0c7d, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0caa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0cc7, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo) != false) goto L_0x0e26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0ccf, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue) == false) goto L_0x0cd6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0cd1, code lost:
        r7 = 0;
        r11 = 2;
        r13 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0cdc, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive) == false) goto L_0x0cf5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0cfb, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) == false) goto L_0x0d25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0cfd, code lost:
        r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r1.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x0d2b, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) == false) goto L_0x0d51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x0d57, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto) == false) goto L_0x0dab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0d5d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0d94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0d67, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0d94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0db1, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame) == false) goto L_0x0dca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0dcc, code lost:
        if (r4.messageText == null) goto L_0x0e10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0dd4, code lost:
        if (r4.messageText.length() <= 0) goto L_0x0e10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0dd6, code lost:
        r2 = r4.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0dde, code lost:
        if (r2.length() <= 20) goto L_0x0df9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0de0, code lost:
        r5 = new java.lang.StringBuilder();
        r11 = 0;
        r5.append(r2.subSequence(0, 20));
        r5.append("...");
        r2 = r5.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0df9, code lost:
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0dfa, code lost:
        r5 = new java.lang.Object[3];
        r5[r11] = r14;
        r5[1] = r2;
        r5[2] = r12.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0e26, code lost:
        r7 = 0;
        r11 = 2;
        r13 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0e29, code lost:
        r5 = new java.lang.Object[r11];
        r5[r7] = r14;
        r5[r13] = r12.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0e3c, code lost:
        r2 = r4.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0e40, code lost:
        if (r2 == null) goto L_0x0e5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0e42, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedStickerEmoji, r14, r12.title, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0e5a, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedSticker", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedSticker, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0e73, code lost:
        if (r1.replyMessageObject != null) goto L_0x0e88;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0e88, code lost:
        r4 = r1.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0e8e, code lost:
        if (r4.isMusic() == false) goto L_0x0ea4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0eaa, code lost:
        if (r4.isVideo() == false) goto L_0x0ef8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0eb0, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ee4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0eba, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0ee4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0efc, code lost:
        if (r4.isGif() == false) goto L_0x0f4b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0f02, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0f36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0f0c, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0f36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0f52, code lost:
        if (r4.isVoice() == false) goto L_0x0f66;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0f6a, code lost:
        if (r4.isRoundVideo() == false) goto L_0x0f7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0f82, code lost:
        if (r4.isSticker() != false) goto L_0x113f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0f88, code lost:
        if (r4.isAnimatedSticker() == false) goto L_0x0f8c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0f92, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument) == false) goto L_0x0fe0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0f98, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0fcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0fa2, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x0fcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0fe6, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo) != false) goto L_0x112c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0fee, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue) == false) goto L_0x0ff4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0ff0, code lost:
        r7 = 1;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0ffa, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive) == false) goto L_0x1010;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x1016, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) == false) goto L_0x103d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x1018, code lost:
        r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r1.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x1043, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) == false) goto L_0x1066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x106c, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto) == false) goto L_0x10ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x1072, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x10a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x107c, code lost:
        if (android.text.TextUtils.isEmpty(r4.messageOwner.message) != false) goto L_0x10a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x10c0, code lost:
        if ((r4.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame) == false) goto L_0x10d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x10d8, code lost:
        if (r4.messageText == null) goto L_0x1119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x10e0, code lost:
        if (r4.messageText.length() <= 0) goto L_0x1119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x10e2, code lost:
        r2 = r4.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x10ea, code lost:
        if (r2.length() <= 20) goto L_0x1105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x10ec, code lost:
        r5 = new java.lang.StringBuilder();
        r11 = 0;
        r5.append(r2.subSequence(0, 20));
        r5.append("...");
        r2 = r5.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x1105, code lost:
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x1106, code lost:
        r5 = new java.lang.Object[2];
        r5[r11] = r12.title;
        r5[1] = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x112c, code lost:
        r7 = 1;
        r11 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x112e, code lost:
        r5 = new java.lang.Object[r7];
        r5[r11] = r12.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x113f, code lost:
        r2 = r4.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x1143, code lost:
        if (r2 == null) goto L_0x115a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x1145, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r12.title, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x115a, code lost:
        r5 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedStickerChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x1174, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionGameScore) == false) goto L_0x117e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x1184, code lost:
        if ((r1.messageOwner.action instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) == false) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x1186, code lost:
        r2 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) r1.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x118e, code lost:
        if (r2.trans != 0) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x1190, code lost:
        r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0.currentAccount).getUser(java.lang.Integer.valueOf(r2.receiver.user_id));
        r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r0.currentAccount).getUser(java.lang.Integer.valueOf(r2.sender.user_id));
        r11 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r2.data, (java.lang.Class<?>) im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class).model;
        r13 = new java.lang.StringBuilder();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x11c5, code lost:
        if (r11 == null) goto L_0x1242;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x11cb, code lost:
        if (r24.isOut() == false) goto L_0x11fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x11d7, code lost:
        if (getUserConfig().clientUserId != r2.sender.user_id) goto L_0x11e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x11d9, code lost:
        r13.append(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouReceivedYourPacket));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x11e4, code lost:
        r13.append(java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouReceivePacketFrom), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r5)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x1208, code lost:
        if (getUserConfig().clientUserId != r2.sender.user_id) goto L_0x1221;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x120a, code lost:
        r13.append(im.bclpbkiauv.messenger.UserObject.getName(r4));
        r13.append(" ");
        r13.append(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.ReceivedYourPacket));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x1221, code lost:
        r13.append(java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.WhoReceivePacketFrom), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r4), im.bclpbkiauv.messenger.UserObject.getName(r5)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x1246, code lost:
        return r13.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x124f, code lost:
        if (r1.messageOwner.to_id.channel_id == 0) goto L_0x1268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x1253, code lost:
        if (r12.megagroup != false) goto L_0x1268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x1282, code lost:
        if (im.bclpbkiauv.messenger.ChatObject.isChannel(r12) == false) goto L_0x1514;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x1286, code lost:
        if (r12.megagroup != false) goto L_0x1514;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x128c, code lost:
        if (r24.isMediaEmpty() == false) goto L_0x12c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x128e, code lost:
        if (r25 != false) goto L_0x12b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x1294, code lost:
        if (r1.messageOwner.message == null) goto L_0x12b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x129e, code lost:
        if (r1.messageOwner.message.length() == 0) goto L_0x12b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x12a0, code lost:
        r13 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageText", im.bclpbkiauv.messenger.R.string.NotificationMessageText, r14, r1.messageOwner.message);
        r26[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x12cf, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto) == false) goto L_0x131b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x12d1, code lost:
        if (r25 != false) goto L_0x130a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x12d7, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x130a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x12e1, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x130a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x12e3, code lost:
        r13 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageText", im.bclpbkiauv.messenger.R.string.NotificationMessageText, r14, "🖼 " + r1.messageOwner.message);
        r26[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x131f, code lost:
        if (r24.isVideo() == false) goto L_0x136b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x1321, code lost:
        if (r25 != false) goto L_0x135a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x1327, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x135a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x1331, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x135a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x1333, code lost:
        r13 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageText", im.bclpbkiauv.messenger.R.string.NotificationMessageText, r14, "📹 " + r1.messageOwner.message);
        r26[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x1371, code lost:
        if (r24.isVoice() == false) goto L_0x1382;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x1386, code lost:
        if (r24.isRoundVideo() == false) goto L_0x1397;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x139b, code lost:
        if (r24.isMusic() == false) goto L_0x13ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x13b2, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) == false) goto L_0x13d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x13b4, code lost:
        r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r1.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x13dc, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) == false) goto L_0x13fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x1402, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo) != false) goto L_0x1503;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x140a, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue) == false) goto L_0x140e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x1414, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive) == false) goto L_0x1427;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x142d, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument) == false) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x1433, code lost:
        if (r24.isSticker() != false) goto L_0x14d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x1439, code lost:
        if (r24.isAnimatedSticker() == false) goto L_0x143d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x1441, code lost:
        if (r24.isGif() == false) goto L_0x148d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x1443, code lost:
        if (r25 != false) goto L_0x147c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x1449, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x147c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x1453, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x147c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x1455, code lost:
        r13 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageText", im.bclpbkiauv.messenger.R.string.NotificationMessageText, r14, "🎬 " + r1.messageOwner.message);
        r26[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x148d, code lost:
        if (r25 != false) goto L_0x14c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x1493, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x14c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x149d, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x14c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x149f, code lost:
        r13 = im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageText", im.bclpbkiauv.messenger.R.string.NotificationMessageText, r14, "📎 " + r1.messageOwner.message);
        r26[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x14d7, code lost:
        r0 = r24.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x14db, code lost:
        if (r0 == null) goto L_0x14f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x151d, code lost:
        if (r24.isMediaEmpty() == false) goto L_0x155f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x151f, code lost:
        if (r25 != false) goto L_0x1549;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x1525, code lost:
        if (r1.messageOwner.message == null) goto L_0x1549;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x152f, code lost:
        if (r1.messageOwner.message.length() == 0) goto L_0x1549;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x1565, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto) == false) goto L_0x15b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x1567, code lost:
        if (r25 != false) goto L_0x15a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x156d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x15a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x1577, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x15a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x15ba, code lost:
        if (r24.isVideo() == false) goto L_0x160b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x15bc, code lost:
        if (r25 != false) goto L_0x15f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x15c2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x15f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x15cc, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x15f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x160f, code lost:
        if (r24.isVoice() == false) goto L_0x1627;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x162b, code lost:
        if (r24.isRoundVideo() == false) goto L_0x1643;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x1647, code lost:
        if (r24.isMusic() == false) goto L_0x165f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x1665, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) == false) goto L_0x168e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x1667, code lost:
        r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r1.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x1694, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) == false) goto L_0x16b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x16bf, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame) == false) goto L_0x16e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x16e8, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo) != false) goto L_0x19a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x16f0, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue) == false) goto L_0x16f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x16fa, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive) == false) goto L_0x1712;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x1718, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument) == false) goto L_0x1802;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x171e, code lost:
        if (r24.isSticker() != false) goto L_0x17cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x1724, code lost:
        if (r24.isAnimatedSticker() == false) goto L_0x1728;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x172c, code lost:
        if (r24.isGif() == false) goto L_0x177d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x172e, code lost:
        if (r25 != false) goto L_0x1767;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x1734, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x1767;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x173e, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x1767;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x177d, code lost:
        if (r25 != false) goto L_0x17b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x1783, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x17b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x178d, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x17b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x17cc, code lost:
        r0 = r24.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x17d0, code lost:
        if (r0 == null) goto L_0x17eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x1808, code lost:
        if ((r1.messageOwner.media instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia) == false) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x180a, code lost:
        r0 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia) r1.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x1812, code lost:
        if (r0.trans != 0) goto L_0x185b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x1814, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x1817, code lost:
        if (r0.data == null) goto L_0x1826;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x1819, code lost:
        r2 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r0.data, (java.lang.Class<?>) im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class).model;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x1826, code lost:
        if (r2 == null) goto L_0x1853;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1852, code lost:
        return java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.ReceiveGroupRedPacketFromOtherView), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(getMessagesController().getUser(java.lang.Integer.valueOf(r2.getRed().getInitiatorUserIdInt())))});
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x185a, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.ReceiveGroupRedPacket);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x185e, code lost:
        if (r0.trans == 1) goto L_0x1865;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x1863, code lost:
        if (r0.trans != 2) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x1865, code lost:
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x1868, code lost:
        if (r0.data == null) goto L_0x1877;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x186a, code lost:
        r2 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r0.data, (java.lang.Class<?>) im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.class).model;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1877, code lost:
        if (r2 == null) goto L_0x1997;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x1879, code lost:
        r4 = r2.getState();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x1881, code lost:
        if (r24.isOutOwner() == false) goto L_0x18e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1885, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING) goto L_0x188f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x188e, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferWaitingOtherCollect);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x1891, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED) goto L_0x18ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x189f, code lost:
        if (r2.getInitiatorUserIdInt() != im.bclpbkiauv.messenger.UserConfig.getInstance(im.bclpbkiauv.messenger.UserConfig.selectedAccount).clientUserId) goto L_0x18a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x18a8, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferOtherHasCollected);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x18ad, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouHaveConfirmedReceipt);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x18b0, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED) goto L_0x18d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x18be, code lost:
        if (r2.getInitiatorUserIdInt() != im.bclpbkiauv.messenger.UserConfig.getInstance(im.bclpbkiauv.messenger.UserConfig.selectedAccount).clientUserId) goto L_0x18c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x18c7, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferHasBeenReturned);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x18cf, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouHaveReturned);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x18d2, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT) goto L_0x18dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x18db, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferHasReturned);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x18dc, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x18e0, code lost:
        r5 = r2.getInitiatorUserIdInt();
        r7 = java.lang.Integer.parseInt(r2.getRecipientUserId());
        r11 = getMessagesController().getUser(java.lang.Integer.valueOf(r5));
        r13 = getMessagesController().getUser(java.lang.Integer.valueOf(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1906, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING) goto L_0x1921;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x1908, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1920, code lost:
        return java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferReceivedFromSomebody), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r11)});
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x1921, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1925, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED) goto L_0x194d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x192f, code lost:
        if (r5 != im.bclpbkiauv.messenger.UserConfig.getInstance(im.bclpbkiauv.messenger.UserConfig.selectedAccount).clientUserId) goto L_0x1948;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1947, code lost:
        return java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferReceivedBySomebody), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r13)});
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x194c, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouHaveConfirmedReceipt);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x194f, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED) goto L_0x197a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1959, code lost:
        if (r5 != im.bclpbkiauv.messenger.UserConfig.getInstance(im.bclpbkiauv.messenger.UserConfig.selectedAccount).clientUserId) goto L_0x1972;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x1971, code lost:
        return java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferReturnedBySomebody), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r13)});
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1979, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.YouHaveReturned);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x197c, code lost:
        if (r4 != im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT) goto L_0x19ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1994, code lost:
        return java.lang.String.format(im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferSendToSomebodyExpired), new java.lang.Object[]{im.bclpbkiauv.messenger.UserObject.getName(r13)});
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x1997, code lost:
        r17 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x19a0, code lost:
        return im.bclpbkiauv.messenger.LocaleController.getString(im.bclpbkiauv.messenger.R.string.TransferMessages);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", im.bclpbkiauv.messenger.R.string.NotificationInvitedToGroupByLink, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationEditedGroupName", im.bclpbkiauv.messenger.R.string.NotificationEditedGroupName, r14, r1.messageOwner.action.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupKickYou", im.bclpbkiauv.messenger.R.string.NotificationGroupKickYou, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupLeftMember", im.bclpbkiauv.messenger.R.string.NotificationGroupLeftMember, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupKickMember", im.bclpbkiauv.messenger.R.string.NotificationGroupKickMember, r14, r12.title, im.bclpbkiauv.messenger.UserObject.getName(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:?, code lost:
        return r1.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:?, code lost:
        return r1.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", im.bclpbkiauv.messenger.R.string.ActionMigrateFromGroupNotify, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", im.bclpbkiauv.messenger.R.string.ActionMigrateFromGroupNotify, r1.messageOwner.action.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:?, code lost:
        return r1.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedNoText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedNoText, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedNoTextChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:?, code lost:
        return r1.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelPhotoEditNotification", im.bclpbkiauv.messenger.R.string.ChannelPhotoEditNotification, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", im.bclpbkiauv.messenger.R.string.NotificationEditedGroupPhoto, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageNoText", im.bclpbkiauv.messenger.R.string.ChannelMessageNoText, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessagePhoto", im.bclpbkiauv.messenger.R.string.ChannelMessagePhoto, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageVideo", im.bclpbkiauv.messenger.R.string.ChannelMessageVideo, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageAudio", im.bclpbkiauv.messenger.R.string.ChannelMessageAudio, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageRound", im.bclpbkiauv.messenger.R.string.ChannelMessageRound, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageMusic", im.bclpbkiauv.messenger.R.string.ChannelMessageMusic, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageContact2", im.bclpbkiauv.messenger.R.string.ChannelMessageContact2, r14, im.bclpbkiauv.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessagePoll2", im.bclpbkiauv.messenger.R.string.ChannelMessagePoll2, r14, ((im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r1.messageOwner.media).poll.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageLiveLocation", im.bclpbkiauv.messenger.R.string.ChannelMessageLiveLocation, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageGIF", im.bclpbkiauv.messenger.R.string.ChannelMessageGIF, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageDocument", im.bclpbkiauv.messenger.R.string.ChannelMessageDocument, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageMap", im.bclpbkiauv.messenger.R.string.ChannelMessageMap, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:906:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupText, r14, r12.title, r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupNoText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupNoText, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupText, r14, r12.title, "🖼 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupPhoto, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupText, r14, r12.title, "📹 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString(" ", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupVideo, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupAudio", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupAudio, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupRound", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupRound, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupMusic", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupMusic, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupContact2", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupContact2, r14, r12.title, im.bclpbkiauv.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupPoll2, r14, r12.title, ((im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r1.messageOwner.media).poll.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupGame", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupGame, r14, r12.title, r1.messageOwner.media.game.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupLiveLocation, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupText, r14, r12.title, "🎬 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupGif", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupGif, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupText", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupText, r14, r12.title, "📎 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupDocument", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupDocument, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupMap", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupMap, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationGroupAddMember", im.bclpbkiauv.messenger.R.string.NotificationGroupAddMember, r14, r12.title, r4.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelAddedByNotification", im.bclpbkiauv.messenger.R.string.ChannelAddedByNotification, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationInvitedToGroup", im.bclpbkiauv.messenger.R.string.NotificationInvitedToGroup, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:?, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:?, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedMusic", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedMusic, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedText, r14, "📹 " + r4.messageOwner.message, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedVideo", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedVideo, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedText, r14, "🎬 " + r4.messageOwner.message, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGif", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGif, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedVoice", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedVoice, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedRound", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedRound, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedText, r14, "📎 " + r4.messageOwner.message, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedFile", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedFile, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGeoLive, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedContact2", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedContact2, r14, r12.title, im.bclpbkiauv.messenger.ContactsController.formatName(r2.first_name, r2.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedPoll2, r14, r12.title, ((im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r4.messageOwner.media).poll.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:943:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedText, r14, "🖼 " + r4.messageOwner.message, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedPhoto, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGame", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGame, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedText, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedNoText", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedNoText, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGeo", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGeo, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:?, code lost:
        return r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedMusicChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedTextChannel, r12.title, "📹 " + r4.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedVideoChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedTextChannel, r12.title, "🎬 " + r4.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGifChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedVoiceChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedRoundChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedTextChannel, r12.title, "📎 " + r4.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedFileChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedContactChannel2, r12.title, im.bclpbkiauv.messenger.ContactsController.formatName(r2.first_name, r2.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedPollChannel2, r12.title, ((im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r4.messageOwner.media).poll.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedTextChannel, r12.title, "🖼 " + r4.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedPhotoChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:964:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGameChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:965:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedTextChannel, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:966:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedNoTextChannel, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:967:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", im.bclpbkiauv.messenger.R.string.NotificationActionPinnedGeoChannel, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:968:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageSticker", im.bclpbkiauv.messenger.R.string.ChannelMessageSticker, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:969:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", im.bclpbkiauv.messenger.R.string.ChannelMessageStickerEmoji, r14, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:970:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupSticker", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupSticker, r14, r12.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:971:?, code lost:
        return im.bclpbkiauv.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", im.bclpbkiauv.messenger.R.string.NotificationMessageGroupStickerEmoji, r14, r12.title, r0);
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=boolean, code=?, for r4v173, types: [boolean] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x08a6  */
    /* JADX WARNING: Removed duplicated region for block: B:828:0x19b6  */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x19ba  */
    /* JADX WARNING: Removed duplicated region for block: B:835:0x19c7  */
    /* JADX WARNING: Removed duplicated region for block: B:836:0x19d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(im.bclpbkiauv.messenger.MessageObject r24, boolean r25, boolean[] r26, boolean[] r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = 0
            boolean r3 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r2)
            if (r3 != 0) goto L_0x19f2
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r3 == 0) goto L_0x0011
            goto L_0x19f2
        L_0x0011:
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            long r3 = r3.dialog_id
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.chat_id
            if (r5 == 0) goto L_0x0024
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.chat_id
            goto L_0x002a
        L_0x0024:
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.channel_id
        L_0x002a:
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.user_id
            r7 = 1
            if (r27 == 0) goto L_0x0035
            r27[r2] = r7
        L_0x0035:
            im.bclpbkiauv.messenger.AccountInstance r8 = r23.getAccountInstance()
            android.content.SharedPreferences r8 = r8.getNotificationsSettings()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "content_preview_"
            r9.append(r10)
            r9.append(r3)
            java.lang.String r9 = r9.toString()
            boolean r9 = r8.getBoolean(r9, r7)
            boolean r10 = r24.isFcmMessage()
            r11 = 2131692379(0x7f0f0b5b, float:1.9013856E38)
            java.lang.String r12 = "NotificationMessageNoText"
            r13 = 2
            if (r10 == 0) goto L_0x00d4
            if (r5 != 0) goto L_0x007b
            if (r6 == 0) goto L_0x007b
            if (r9 == 0) goto L_0x006c
            java.lang.String r10 = "EnablePreviewAll"
            boolean r10 = r8.getBoolean(r10, r7)
            if (r10 != 0) goto L_0x00cd
        L_0x006c:
            if (r27 == 0) goto L_0x0070
            r27[r2] = r2
        L_0x0070:
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r10 = r1.localName
            r7[r2] = r10
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r12, r11, r7)
            return r2
        L_0x007b:
            if (r5 == 0) goto L_0x00cd
            if (r9 == 0) goto L_0x0097
            boolean r10 = r1.localChannel
            if (r10 != 0) goto L_0x008b
            java.lang.String r10 = "EnablePreviewGroup"
            boolean r10 = r8.getBoolean(r10, r7)
            if (r10 == 0) goto L_0x0097
        L_0x008b:
            boolean r10 = r1.localChannel
            if (r10 == 0) goto L_0x00cd
            java.lang.String r10 = "EnablePreviewChannel"
            boolean r10 = r8.getBoolean(r10, r7)
            if (r10 != 0) goto L_0x00cd
        L_0x0097:
            if (r27 == 0) goto L_0x009b
            r27[r2] = r2
        L_0x009b:
            boolean r10 = r24.isMegagroup()
            if (r10 != 0) goto L_0x00b9
            im.bclpbkiauv.tgnet.TLRPC$Message r10 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r10 = r10.to_id
            int r10 = r10.channel_id
            if (r10 == 0) goto L_0x00b9
            r10 = 2131690450(0x7f0f03d2, float:1.9009944E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r11 = r1.localName
            r7[r2] = r11
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r10, r7)
            return r2
        L_0x00b9:
            r10 = 2131692366(0x7f0f0b4e, float:1.901383E38)
            java.lang.Object[] r11 = new java.lang.Object[r13]
            java.lang.String r12 = r1.localUserName
            r11[r2] = r12
            java.lang.String r2 = r1.localName
            r11[r7] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r10, r11)
            return r2
        L_0x00cd:
            r26[r2] = r7
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r2 = (java.lang.String) r2
            return r2
        L_0x00d4:
            im.bclpbkiauv.messenger.UserConfig r10 = r23.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r6 != 0) goto L_0x00f2
            boolean r14 = r24.isFromUser()
            if (r14 != 0) goto L_0x00ed
            int r14 = r24.getId()
            if (r14 >= 0) goto L_0x00eb
            goto L_0x00ed
        L_0x00eb:
            int r6 = -r5
            goto L_0x00f8
        L_0x00ed:
            im.bclpbkiauv.tgnet.TLRPC$Message r14 = r1.messageOwner
            int r6 = r14.from_id
            goto L_0x00f8
        L_0x00f2:
            if (r6 != r10) goto L_0x00f8
            im.bclpbkiauv.tgnet.TLRPC$Message r14 = r1.messageOwner
            int r6 = r14.from_id
        L_0x00f8:
            r14 = 0
            int r16 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x0106
            if (r5 == 0) goto L_0x0103
            int r14 = -r5
            long r3 = (long) r14
            goto L_0x0106
        L_0x0103:
            if (r6 == 0) goto L_0x0106
            long r3 = (long) r6
        L_0x0106:
            r14 = 0
            if (r6 <= 0) goto L_0x013f
            im.bclpbkiauv.tgnet.TLRPC$Message r15 = r1.messageOwner
            boolean r15 = r15.from_scheduled
            if (r15 == 0) goto L_0x012a
            r16 = r12
            long r11 = (long) r10
            int r17 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r17 != 0) goto L_0x0120
            r11 = 2131692013(0x7f0f09ed, float:1.9013114E38)
            java.lang.String r12 = "MessageScheduledReminderNotification"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            goto L_0x0152
        L_0x0120:
            r11 = 2131692388(0x7f0f0b64, float:1.9013875E38)
            java.lang.String r12 = "NotificationMessageScheduledName"
            java.lang.String r14 = im.bclpbkiauv.messenger.LocaleController.getString(r12, r11)
            goto L_0x0152
        L_0x012a:
            r16 = r12
            im.bclpbkiauv.messenger.MessagesController r11 = r23.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 == 0) goto L_0x013e
            java.lang.String r14 = im.bclpbkiauv.messenger.UserObject.getName(r11)
        L_0x013e:
            goto L_0x0152
        L_0x013f:
            r16 = r12
            im.bclpbkiauv.messenger.MessagesController r11 = r23.getMessagesController()
            int r12 = -r6
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            im.bclpbkiauv.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            if (r11 == 0) goto L_0x0152
            java.lang.String r14 = r11.title
        L_0x0152:
            r11 = 0
            if (r14 != 0) goto L_0x0156
            return r11
        L_0x0156:
            r12 = 0
            if (r5 == 0) goto L_0x0168
            im.bclpbkiauv.messenger.MessagesController r15 = r23.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$Chat r12 = r15.getChat(r13)
            if (r12 != 0) goto L_0x0168
            return r11
        L_0x0168:
            r13 = 0
            int r15 = (int) r3
            if (r15 != 0) goto L_0x017b
            r2 = 2131694857(0x7f0f1509, float:1.9018882E38)
            java.lang.String r7 = "YouHaveNewMessage"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.getString(r7, r2)
            r20 = r3
            r16 = r5
            goto L_0x19f1
        L_0x017b:
            r19 = 2131694856(0x7f0f1508, float:1.901888E38)
            java.lang.String r11 = "🎬 "
            java.lang.String r15 = "📎 "
            java.lang.String r2 = "📹 "
            java.lang.String r7 = "🖼 "
            r20 = r3
            java.lang.String r4 = "NotificationMessageText"
            if (r5 != 0) goto L_0x0890
            if (r6 == 0) goto L_0x0890
            if (r9 == 0) goto L_0x0874
            java.lang.String r3 = "EnablePreviewAll"
            r22 = r13
            r13 = 1
            boolean r3 = r8.getBoolean(r3, r13)
            if (r3 == 0) goto L_0x086f
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService
            if (r3 == 0) goto L_0x0380
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionUserJoined
            if (r2 != 0) goto L_0x036a
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x01b8
            r13 = r12
            goto L_0x036b
        L_0x01b8:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01d3
            r2 = 2131692330(0x7f0f0b2a, float:1.9013757E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r14
            java.lang.String r4 = "NotificationContactNewPhoto"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            goto L_0x19f1
        L_0x01d3:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x0247
            r3 = 2
            java.lang.Object[] r4 = new java.lang.Object[r3]
            im.bclpbkiauv.messenger.LocaleController r3 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r3 = r3.formatterYear
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            int r7 = r7.date
            r13 = r3
            long r2 = (long) r7
            r15 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 * r15
            r7 = r13
            java.lang.String r2 = r7.format((long) r2)
            r3 = 0
            r4[r3] = r2
            im.bclpbkiauv.messenger.LocaleController r2 = im.bclpbkiauv.messenger.LocaleController.getInstance()
            im.bclpbkiauv.messenger.time.FastDateFormat r2 = r2.formatterDay
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            int r3 = r3.date
            r13 = r12
            long r11 = (long) r3
            long r11 = r11 * r15
            java.lang.String r2 = r2.format((long) r11)
            r3 = 1
            r4[r3] = r2
            java.lang.String r2 = "formatDateAtTime"
            r7 = 2131695131(0x7f0f161b, float:1.9019438E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r7, r4)
            r4 = 2131692394(0x7f0f0b6a, float:1.9013887E38)
            r7 = 4
            java.lang.Object[] r7 = new java.lang.Object[r7]
            im.bclpbkiauv.messenger.UserConfig r11 = r23.getUserConfig()
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getCurrentUser()
            java.lang.String r11 = r11.first_name
            r12 = 0
            r7[r12] = r11
            r7[r3] = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            java.lang.String r3 = r3.title
            r11 = 2
            r7[r11] = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r3 = r3.action
            java.lang.String r3 = r3.address
            r11 = 3
            r7[r11] = r3
            java.lang.String r3 = "NotificationUnrecognizedDevice"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r4, r7)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0247:
            r13 = r12
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionGameScore
            if (r2 != 0) goto L_0x035e
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x025a
            goto L_0x035e
        L_0x025a:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r2 == 0) goto L_0x0284
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            im.bclpbkiauv.tgnet.TLRPC$PhoneCallDiscardReason r2 = r2.reason
            boolean r3 = r24.isOut()
            if (r3 != 0) goto L_0x027d
            boolean r3 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed
            if (r3 == 0) goto L_0x027d
            r3 = 2131690289(0x7f0f0331, float:1.9009617E38)
            java.lang.String r4 = "CallMessageIncomingMissed"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r4, r3)
            r22 = r3
        L_0x027d:
            r16 = r5
            r12 = r13
            r13 = r22
            goto L_0x19f1
        L_0x0284:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer
            if (r2 == 0) goto L_0x0359
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesActionReceivedRpkTransfer r2 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) r2
            int r3 = r2.trans
            if (r3 != 0) goto L_0x0354
            int r3 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r3 = im.bclpbkiauv.messenger.MessagesController.getInstance(r3)
            im.bclpbkiauv.tgnet.TLRPC$Peer r4 = r2.receiver
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r3.getUser(r4)
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            im.bclpbkiauv.tgnet.TLRPC$Peer r7 = r2.sender
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r7)
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r7 = r2.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r11 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r7 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r7, (java.lang.Class<?>) r11)
            T r11 = r7.model
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r11 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r11
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            if (r11 == 0) goto L_0x034d
            boolean r15 = r24.isOut()
            if (r15 == 0) goto L_0x0306
            im.bclpbkiauv.messenger.UserConfig r15 = r23.getUserConfig()
            int r15 = r15.clientUserId
            r16 = r7
            im.bclpbkiauv.tgnet.TLRPC$Peer r7 = r2.sender
            int r7 = r7.user_id
            if (r15 != r7) goto L_0x02ec
            r7 = 2131694868(0x7f0f1514, float:1.9018905E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            r12.append(r7)
            goto L_0x034f
        L_0x02ec:
            r7 = 2131694865(0x7f0f1511, float:1.9018899E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r4)
            r18 = 0
            r15[r18] = r17
            java.lang.String r7 = java.lang.String.format(r7, r15)
            r12.append(r7)
            goto L_0x034f
        L_0x0306:
            r16 = r7
            im.bclpbkiauv.messenger.UserConfig r7 = r23.getUserConfig()
            int r7 = r7.clientUserId
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r2.sender
            int r15 = r15.user_id
            if (r7 != r15) goto L_0x032b
            java.lang.String r7 = im.bclpbkiauv.messenger.UserObject.getName(r3)
            r12.append(r7)
            java.lang.String r7 = " "
            r12.append(r7)
            r7 = 2131693297(0x7f0f0ef1, float:1.9015718E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            r12.append(r7)
            goto L_0x034f
        L_0x032b:
            r7 = 2131694781(0x7f0f14bd, float:1.9018728E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            r15 = 2
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r3)
            r18 = 0
            r15[r18] = r17
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r4)
            r18 = 1
            r15[r18] = r17
            java.lang.String r7 = java.lang.String.format(r7, r15)
            r12.append(r7)
            goto L_0x034f
        L_0x034d:
            r16 = r7
        L_0x034f:
            java.lang.String r7 = r12.toString()
            return r7
        L_0x0354:
            r16 = r5
            r12 = r13
            goto L_0x19ef
        L_0x0359:
            r16 = r5
            r12 = r13
            goto L_0x19ef
        L_0x035e:
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r2 = r2.toString()
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x036a:
            r13 = r12
        L_0x036b:
            r2 = 2131692329(0x7f0f0b29, float:1.9013755E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r14
            java.lang.String r4 = "NotificationContactJoined"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0380:
            r13 = r12
            boolean r3 = r24.isMediaEmpty()
            if (r3 == 0) goto L_0x03d9
            if (r25 != 0) goto L_0x03c4
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x03af
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r14
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r11 = 1
            r2[r11] = r7
            r7 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r7, r2)
            r26[r3] = r11
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x03af:
            r3 = 0
            r11 = 1
            java.lang.Object[] r2 = new java.lang.Object[r11]
            r2[r3] = r14
            r7 = r16
            r4 = 2131692379(0x7f0f0b5b, float:1.9013856E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r4, r2)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x03c4:
            r7 = r16
            r3 = 0
            r4 = 2131692379(0x7f0f0b5b, float:1.9013856E38)
            r11 = 1
            java.lang.Object[] r2 = new java.lang.Object[r11]
            r2[r3] = r14
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r4, r2)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x03d9:
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0450
            if (r25 != 0) goto L_0x041e
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x041e
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x041e
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r14
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r11.append(r7)
            java.lang.String r7 = r11.toString()
            r11 = 1
            r2[r11] = r7
            r7 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r7, r2)
            r26[r3] = r11
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x041e:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x043b
            r2 = 2131692384(0x7f0f0b60, float:1.9013867E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r14
            java.lang.String r4 = "NotificationMessageSDPhoto"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x043b:
            r3 = 1
            r4 = 0
            r2 = 2131692380(0x7f0f0b5c, float:1.9013858E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r14
            java.lang.String r4 = "NotificationMessagePhoto"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0450:
            boolean r3 = r24.isVideo()
            if (r3 == 0) goto L_0x04c5
            if (r25 != 0) goto L_0x0493
            int r3 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r3 < r7) goto L_0x0493
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r1.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0493
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r7 = 0
            r3[r7] = r14
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r11.append(r2)
            java.lang.String r2 = r11.toString()
            r11 = 1
            r3[r11] = r2
            r2 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r26[r7] = r11
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0493:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x04b0
            r2 = 2131692385(0x7f0f0b61, float:1.9013869E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r7 = 0
            r3[r7] = r14
            java.lang.String r4 = "NotificationMessageSDVideo"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x04b0:
            r3 = 1
            r7 = 0
            r2 = 2131692392(0x7f0f0b68, float:1.9013883E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r14
            java.lang.String r4 = "NotificationMessageVideo"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x04c5:
            r7 = 0
            boolean r2 = r24.isGame()
            if (r2 == 0) goto L_0x04eb
            r2 = 2131692352(0x7f0f0b40, float:1.9013802E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r14
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r4.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r7 = 1
            r3[r7] = r4
            java.lang.String r4 = "NotificationMessageGame"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x04eb:
            r7 = 1
            boolean r2 = r24.isVoice()
            if (r2 == 0) goto L_0x0506
            r2 = 2131692346(0x7f0f0b3a, float:1.901379E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r12 = 0
            r3[r12] = r14
            java.lang.String r4 = "NotificationMessageAudio"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0506:
            r12 = 0
            boolean r2 = r24.isRoundVideo()
            if (r2 == 0) goto L_0x0520
            r2 = 2131692383(0x7f0f0b5f, float:1.9013865E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r12] = r14
            java.lang.String r4 = "NotificationMessageRound"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0520:
            boolean r2 = r24.isMusic()
            if (r2 == 0) goto L_0x0539
            r2 = 2131692378(0x7f0f0b5a, float:1.9013854E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r12] = r14
            java.lang.String r4 = "NotificationMessageMusic"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0539:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r2 == 0) goto L_0x0567
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaContact r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r2
            r3 = 2131692348(0x7f0f0b3c, float:1.9013794E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r7 = 0
            r4[r7] = r14
            java.lang.String r7 = r2.first_name
            java.lang.String r11 = r2.last_name
            java.lang.String r7 = im.bclpbkiauv.messenger.ContactsController.formatName(r7, r11)
            r11 = 1
            r4[r11] = r7
            java.lang.String r7 = "NotificationMessageContact2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r3, r4)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0567:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r2 == 0) goto L_0x0591
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r2
            r3 = 2131692382(0x7f0f0b5e, float:1.9013863E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r7 = 0
            r4[r7] = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r7 = r2.poll
            java.lang.String r7 = r7.question
            r11 = 1
            r4[r11] = r7
            java.lang.String r7 = "NotificationMessagePoll2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r3, r4)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0591:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r2 != 0) goto L_0x085a
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r2 == 0) goto L_0x05a3
            goto L_0x085a
        L_0x05a3:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x05c0
            r2 = 2131692376(0x7f0f0b58, float:1.901385E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r14
            java.lang.String r4 = "NotificationMessageLiveLocation"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x05c0:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r2 == 0) goto L_0x06ae
            boolean r2 = r24.isSticker()
            if (r2 != 0) goto L_0x0680
            boolean r2 = r24.isAnimatedSticker()
            if (r2 == 0) goto L_0x05d6
            goto L_0x0680
        L_0x05d6:
            boolean r2 = r24.isGif()
            if (r2 == 0) goto L_0x062e
            if (r25 != 0) goto L_0x0619
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0619
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0619
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r14
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r1.messageOwner
            java.lang.String r11 = r11.message
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            r11 = 1
            r2[r11] = r7
            r7 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r7, r2)
            r26[r3] = r11
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0619:
            r3 = 0
            r11 = 1
            r2 = 2131692354(0x7f0f0b42, float:1.9013806E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r4[r3] = r14
            java.lang.String r3 = "NotificationMessageGif"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r2, r4)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x062e:
            if (r25 != 0) goto L_0x066b
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x066b
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x066b
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r14
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r15)
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r1.messageOwner
            java.lang.String r11 = r11.message
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            r11 = 1
            r2[r11] = r7
            r7 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r7, r2)
            r26[r3] = r11
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x066b:
            r3 = 0
            r11 = 1
            r2 = 2131692349(0x7f0f0b3d, float:1.9013796E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r4[r3] = r14
            java.lang.String r3 = "NotificationMessageDocument"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r3, r2, r4)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0680:
            java.lang.String r2 = r24.getStickerEmoji()
            if (r2 == 0) goto L_0x0699
            r3 = 2131692390(0x7f0f0b66, float:1.9013879E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r7 = 0
            r4[r7] = r14
            r11 = 1
            r4[r11] = r2
            java.lang.String r7 = "NotificationMessageStickerEmoji"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r3, r4)
            goto L_0x06a8
        L_0x0699:
            r7 = 0
            r11 = 1
            r3 = 2131692389(0x7f0f0b65, float:1.9013877E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r4[r7] = r14
            java.lang.String r7 = "NotificationMessageSticker"
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r3, r4)
        L_0x06a8:
            r16 = r5
            r12 = r13
            r13 = r3
            goto L_0x19f1
        L_0x06ae:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia
            if (r2 == 0) goto L_0x0855
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesRpkTransferMedia r2 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia) r2
            int r3 = r2.trans
            if (r3 != 0) goto L_0x0707
            r3 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r2.data
            if (r4 == 0) goto L_0x06d2
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r2.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r7 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r4 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r4, (java.lang.Class<?>) r7)
            T r7 = r4.model
            r3 = r7
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r3 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r3
        L_0x06d2:
            if (r3 == 0) goto L_0x06ff
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r4 = r3.getRed()
            int r7 = r4.getInitiatorUserIdInt()
            im.bclpbkiauv.messenger.MessagesController r11 = r23.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getUser(r12)
            r12 = 2131693290(0x7f0f0eea, float:1.9015704E38)
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r12)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r16 = im.bclpbkiauv.messenger.UserObject.getName(r11)
            r17 = 0
            r15[r17] = r16
            java.lang.String r12 = java.lang.String.format(r12, r15)
            return r12
        L_0x06ff:
            r4 = 2131693289(0x7f0f0ee9, float:1.9015702E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            return r4
        L_0x0707:
            r15 = 1
            int r3 = r2.trans
            if (r3 == r15) goto L_0x0711
            int r3 = r2.trans
            r4 = 2
            if (r3 != r4) goto L_0x0846
        L_0x0711:
            r3 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r2.data
            if (r4 == 0) goto L_0x0723
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r2.data
            java.lang.Class<im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse> r7 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r4 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r4, (java.lang.Class<?>) r7)
            T r7 = r4.model
            r3 = r7
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse r3 = (im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse) r3
        L_0x0723:
            if (r3 == 0) goto L_0x084b
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r4 = r3.getState()
            boolean r7 = r24.isOutOwner()
            if (r7 == 0) goto L_0x078e
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r7 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r4 != r7) goto L_0x073b
            r7 = 2131694417(0x7f0f1351, float:1.901799E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            return r7
        L_0x073b:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r7 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r4 != r7) goto L_0x075a
            int r7 = r3.getInitiatorUserIdInt()
            int r11 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r11 = im.bclpbkiauv.messenger.UserConfig.getInstance(r11)
            int r11 = r11.clientUserId
            if (r7 != r11) goto L_0x0755
            r11 = 2131694364(0x7f0f131c, float:1.9017882E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11)
            return r11
        L_0x0755:
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r19)
            return r11
        L_0x075a:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r7 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r4 != r7) goto L_0x077c
            int r7 = r3.getInitiatorUserIdInt()
            int r11 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r11 = im.bclpbkiauv.messenger.UserConfig.getInstance(r11)
            int r11 = r11.clientUserId
            if (r7 != r11) goto L_0x0774
            r11 = 2131694356(0x7f0f1314, float:1.9017866E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11)
            return r11
        L_0x0774:
            r11 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11)
            return r11
        L_0x077c:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r7 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r4 != r7) goto L_0x0788
            r7 = 2131694357(0x7f0f1315, float:1.9017868E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            return r7
        L_0x0788:
            r16 = r2
            r17 = r3
            goto L_0x0845
        L_0x078e:
            int r7 = r3.getInitiatorUserIdInt()
            java.lang.String r11 = r3.getRecipientUserId()
            int r11 = java.lang.Integer.parseInt(r11)
            im.bclpbkiauv.messenger.MessagesController r12 = r23.getMessagesController()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r12 = r12.getUser(r15)
            im.bclpbkiauv.messenger.MessagesController r15 = r23.getMessagesController()
            r16 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r11)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r15.getUser(r2)
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r15 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r4 != r15) goto L_0x07d1
            r15 = 2131694384(0x7f0f1330, float:1.9017923E38)
            java.lang.String r15 = im.bclpbkiauv.messenger.LocaleController.getString(r15)
            r17 = r3
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r12)
            r19 = 0
            r3[r19] = r18
            java.lang.String r3 = java.lang.String.format(r15, r3)
            return r3
        L_0x07d1:
            r17 = r3
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r4 != r3) goto L_0x07fd
            int r3 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r3 = im.bclpbkiauv.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            if (r7 != r3) goto L_0x07f8
            r3 = 2131694383(0x7f0f132f, float:1.9017921E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            r19 = 0
            r15[r19] = r18
            java.lang.String r3 = java.lang.String.format(r3, r15)
            return r3
        L_0x07f8:
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r19)
            return r3
        L_0x07fd:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r4 != r3) goto L_0x082a
            int r3 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r3 = im.bclpbkiauv.messenger.UserConfig.getInstance(r3)
            int r3 = r3.clientUserId
            if (r7 != r3) goto L_0x0822
            r3 = 2131694399(0x7f0f133f, float:1.9017953E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            r19 = 0
            r15[r19] = r18
            java.lang.String r3 = java.lang.String.format(r3, r15)
            return r3
        L_0x0822:
            r3 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            return r3
        L_0x082a:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r3 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r4 != r3) goto L_0x0845
            r3 = 2131694401(0x7f0f1341, float:1.9017958E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            r19 = 0
            r15[r19] = r18
            java.lang.String r3 = java.lang.String.format(r3, r15)
            return r3
        L_0x0845:
        L_0x0846:
            r16 = r5
            r12 = r13
            goto L_0x19ef
        L_0x084b:
            r16 = r2
            r2 = 2131694363(0x7f0f131b, float:1.901788E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2)
            return r2
        L_0x0855:
            r16 = r5
            r12 = r13
            goto L_0x19ef
        L_0x085a:
            r2 = 2131692377(0x7f0f0b59, float:1.9013852E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r14
            java.lang.String r4 = "NotificationMessageMap"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r3)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x086f:
            r13 = r12
            r7 = r16
            r4 = 0
            goto L_0x087a
        L_0x0874:
            r22 = r13
            r7 = r16
            r4 = 0
            r13 = r12
        L_0x087a:
            if (r27 == 0) goto L_0x087e
            r27[r4] = r4
        L_0x087e:
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r14
            r3 = 2131692379(0x7f0f0b5b, float:1.9013856E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r3, r2)
            r16 = r5
            r12 = r13
            r13 = r2
            goto L_0x19f1
        L_0x0890:
            r22 = r13
            r13 = r12
            if (r5 == 0) goto L_0x19ec
            boolean r3 = im.bclpbkiauv.messenger.ChatObject.isChannel(r13)
            if (r3 == 0) goto L_0x08a2
            r12 = r13
            boolean r3 = r12.megagroup
            if (r3 != 0) goto L_0x08a3
            r3 = 1
            goto L_0x08a4
        L_0x08a2:
            r12 = r13
        L_0x08a3:
            r3 = 0
        L_0x08a4:
            if (r9 == 0) goto L_0x19b6
            if (r3 != 0) goto L_0x08b4
            java.lang.String r13 = "EnablePreviewGroup"
            r16 = r5
            r5 = 1
            boolean r13 = r8.getBoolean(r13, r5)
            if (r13 != 0) goto L_0x08c1
            goto L_0x08b6
        L_0x08b4:
            r16 = r5
        L_0x08b6:
            if (r3 == 0) goto L_0x19b8
            java.lang.String r5 = "EnablePreviewChannel"
            r13 = 1
            boolean r5 = r8.getBoolean(r5, r13)
            if (r5 == 0) goto L_0x19b8
        L_0x08c1:
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            boolean r5 = r5 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageService
            if (r5 == 0) goto L_0x127e
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x09ed
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != 0) goto L_0x08f5
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            int r4 = r4.size()
            r5 = 1
            if (r4 != r5) goto L_0x08f5
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            r5 = 0
            java.lang.Object r4 = r4.get(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r2 = r4.intValue()
        L_0x08f5:
            if (r2 == 0) goto L_0x0992
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r4 = r4.to_id
            int r4 = r4.channel_id
            if (r4 == 0) goto L_0x091a
            boolean r4 = r12.megagroup
            if (r4 != 0) goto L_0x091a
            r4 = 2131690398(0x7f0f039e, float:1.9009838E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "ChannelAddedByNotification"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r4, r5)
            r13 = r4
            goto L_0x09eb
        L_0x091a:
            if (r2 != r10) goto L_0x0933
            r4 = 2131692343(0x7f0f0b37, float:1.9013783E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationInvitedToGroup"
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r4, r5)
            r13 = r4
            goto L_0x09eb
        L_0x0933:
            im.bclpbkiauv.messenger.MessagesController r4 = r23.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r2)
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 != 0) goto L_0x0943
            r5 = 0
            return r5
        L_0x0943:
            int r5 = r4.id
            if (r6 != r5) goto L_0x0975
            boolean r5 = r12.megagroup
            if (r5 == 0) goto L_0x0960
            r5 = 2131692335(0x7f0f0b2f, float:1.9013767E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r11 = 0
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r13 = 1
            r7[r13] = r11
            java.lang.String r11 = "NotificationGroupAddSelfMega"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            goto L_0x0990
        L_0x0960:
            r7 = 2
            r11 = 0
            r13 = 1
            r5 = 2131692334(0x7f0f0b2e, float:1.9013765E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r7[r13] = r11
            java.lang.String r11 = "NotificationGroupAddSelf"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            goto L_0x0990
        L_0x0975:
            r11 = 0
            r13 = 1
            r5 = 2131692333(0x7f0f0b2d, float:1.9013763E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r7[r13] = r11
            java.lang.String r11 = im.bclpbkiauv.messenger.UserObject.getName(r4)
            r13 = 2
            r7[r13] = r11
            java.lang.String r11 = "NotificationGroupAddMember"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
        L_0x0990:
            r13 = r5
            goto L_0x09eb
        L_0x0992:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 0
        L_0x0998:
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r7 = r7.action
            java.util.ArrayList<java.lang.Integer> r7 = r7.users
            int r7 = r7.size()
            if (r5 >= r7) goto L_0x09cf
            im.bclpbkiauv.messenger.MessagesController r7 = r23.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r11 = r11.action
            java.util.ArrayList<java.lang.Integer> r11 = r11.users
            java.lang.Object r11 = r11.get(r5)
            java.lang.Integer r11 = (java.lang.Integer) r11
            im.bclpbkiauv.tgnet.TLRPC$User r7 = r7.getUser(r11)
            if (r7 == 0) goto L_0x09cc
            java.lang.String r11 = im.bclpbkiauv.messenger.UserObject.getName(r7)
            int r13 = r4.length()
            if (r13 == 0) goto L_0x09c9
            java.lang.String r13 = ", "
            r4.append(r13)
        L_0x09c9:
            r4.append(r11)
        L_0x09cc:
            int r5 = r5 + 1
            goto L_0x0998
        L_0x09cf:
            r5 = 2131692333(0x7f0f0b2d, float:1.9013763E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r11 = 0
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r13 = 1
            r7[r13] = r11
            java.lang.String r11 = r4.toString()
            r13 = 2
            r7[r13] = r11
            java.lang.String r11 = "NotificationGroupAddMember"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            r13 = r5
        L_0x09eb:
            goto L_0x19f1
        L_0x09ed:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r4 == 0) goto L_0x0a0b
            r2 = 2131692344(0x7f0f0b38, float:1.9013785E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "NotificationInvitedToGroupByLink"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0a0b:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r4 == 0) goto L_0x0a2d
            r2 = 2131692331(0x7f0f0b2b, float:1.901376E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r5 = r5.action
            java.lang.String r5 = r5.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "NotificationEditedGroupName"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0a2d:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r4 != 0) goto L_0x1249
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r4 == 0) goto L_0x0a3f
            goto L_0x1249
        L_0x0a3f:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x0ab6
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != r10) goto L_0x0a65
            r2 = 2131692341(0x7f0f0b35, float:1.901378E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "NotificationGroupKickYou"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0a65:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.user_id
            if (r2 != r6) goto L_0x0a83
            r2 = 2131692342(0x7f0f0b36, float:1.9013781E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "NotificationGroupLeftMember"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0a83:
            im.bclpbkiauv.messenger.MessagesController r2 = r23.getMessagesController()
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r2.getUser(r4)
            if (r2 != 0) goto L_0x0a99
            r4 = 0
            return r4
        L_0x0a99:
            r4 = 2131692340(0x7f0f0b34, float:1.9013777E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = im.bclpbkiauv.messenger.UserObject.getName(r2)
            r11 = 2
            r5[r11] = r7
            java.lang.String r7 = "NotificationGroupKickMember"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r4, r5)
            goto L_0x19f1
        L_0x0ab6:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatCreate
            if (r4 == 0) goto L_0x0ac6
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r13 = r2.toString()
            goto L_0x19f1
        L_0x0ac6:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0ad6
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r13 = r2.toString()
            goto L_0x19f1
        L_0x0ad6:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x0af1
            r2 = 2131689619(0x7f0f0093, float:1.9008258E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = r12.title
            r7 = 0
            r4[r7] = r5
            java.lang.String r5 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0af1:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x0b10
            r2 = 2131689619(0x7f0f0093, float:1.9008258E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r5 = r5.action
            java.lang.String r5 = r5.title
            r7 = 0
            r4[r7] = r5
            java.lang.String r5 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0b10:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x0b20
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r13 = r2.toString()
            goto L_0x19f1
        L_0x0b20:
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionPinMessage
            if (r4 == 0) goto L_0x116e
            if (r12 == 0) goto L_0x0e71
            boolean r4 = im.bclpbkiauv.messenger.ChatObject.isChannel(r12)
            if (r4 == 0) goto L_0x0b34
            boolean r4 = r12.megagroup
            if (r4 == 0) goto L_0x0e71
        L_0x0b34:
            im.bclpbkiauv.messenger.MessageObject r4 = r1.replyMessageObject
            if (r4 != 0) goto L_0x0b4e
            r2 = 2131692308(0x7f0f0b14, float:1.9013712E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "NotificationActionPinnedNoText"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0b4e:
            im.bclpbkiauv.messenger.MessageObject r4 = r1.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0b6d
            r2 = 2131692306(0x7f0f0b12, float:1.9013708E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedMusic"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0b6d:
            boolean r5 = r4.isVideo()
            java.lang.String r13 = "NotificationActionPinnedText"
            if (r5 == 0) goto L_0x0bc7
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r5 < r7) goto L_0x0bb0
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0bb0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            r7 = 1
            r5[r7] = r2
            java.lang.String r7 = r12.title
            r11 = 2
            r5[r11] = r7
            r7 = 2131692322(0x7f0f0b22, float:1.901374E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0bb0:
            r11 = 2
            r2 = 2131692324(0x7f0f0b24, float:1.9013745E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedVideo"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0bc7:
            boolean r2 = r4.isGif()
            if (r2 == 0) goto L_0x0c1f
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x0c08
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0c08
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            r7 = 1
            r5[r7] = r2
            java.lang.String r7 = r12.title
            r11 = 2
            r5[r11] = r7
            r7 = 2131692322(0x7f0f0b22, float:1.901374E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0c08:
            r11 = 2
            r2 = 2131692302(0x7f0f0b0e, float:1.90137E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGif"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0c1f:
            boolean r2 = r4.isVoice()
            if (r2 == 0) goto L_0x0c3c
            r2 = 2131692326(0x7f0f0b26, float:1.9013749E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedVoice"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0c3c:
            boolean r2 = r4.isRoundVideo()
            if (r2 == 0) goto L_0x0c59
            r2 = 2131692316(0x7f0f0b1c, float:1.9013729E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedRound"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0c59:
            boolean r2 = r4.isSticker()
            if (r2 != 0) goto L_0x0e3c
            boolean r2 = r4.isAnimatedSticker()
            if (r2 == 0) goto L_0x0c67
            goto L_0x0e3c
        L_0x0c67:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r2 == 0) goto L_0x0cc1
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x0caa
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0caa
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            r7 = 1
            r5[r7] = r2
            java.lang.String r7 = r12.title
            r11 = 2
            r5[r11] = r7
            r7 = 2131692322(0x7f0f0b22, float:1.901374E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0caa:
            r11 = 2
            r2 = 2131692292(0x7f0f0b04, float:1.901368E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedFile"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0cc1:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r2 != 0) goto L_0x0e26
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r2 == 0) goto L_0x0cd6
            r7 = 0
            r11 = 2
            r13 = 1
            goto L_0x0e29
        L_0x0cd6:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0cf5
            r2 = 2131692300(0x7f0f0b0c, float:1.9013696E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLive"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0cf5:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r2 == 0) goto L_0x0d25
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaContact r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r2
            r5 = 2131692289(0x7f0f0b01, float:1.9013674E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r11 = 0
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r13 = 1
            r7[r13] = r11
            java.lang.String r11 = r2.first_name
            java.lang.String r13 = r2.last_name
            java.lang.String r11 = im.bclpbkiauv.messenger.ContactsController.formatName(r11, r13)
            r13 = 2
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedContact2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            r13 = r2
            goto L_0x0e6f
        L_0x0d25:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r2 == 0) goto L_0x0d51
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r2
            r5 = 2131692313(0x7f0f0b19, float:1.9013723E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r11 = 0
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r13 = 1
            r7[r13] = r11
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r11 = r2.poll
            java.lang.String r11 = r11.question
            r13 = 2
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedPoll2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            r13 = r2
            goto L_0x0e6f
        L_0x0d51:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0dab
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x0d94
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d94
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            r7 = 1
            r5[r7] = r2
            java.lang.String r7 = r12.title
            r11 = 2
            r5[r11] = r7
            r7 = 2131692322(0x7f0f0b22, float:1.901374E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0d94:
            r11 = 2
            r2 = 2131692310(0x7f0f0b16, float:1.9013716E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedPhoto"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0dab:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r2 == 0) goto L_0x0dca
            r2 = 2131692294(0x7f0f0b06, float:1.9013684E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGame"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0dca:
            java.lang.CharSequence r2 = r4.messageText
            if (r2 == 0) goto L_0x0e10
            java.lang.CharSequence r2 = r4.messageText
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x0e10
            java.lang.CharSequence r2 = r4.messageText
            int r5 = r2.length()
            r7 = 20
            if (r5 <= r7) goto L_0x0df9
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r7 = 20
            r11 = 0
            java.lang.CharSequence r7 = r2.subSequence(r11, r7)
            r5.append(r7)
            java.lang.String r7 = "..."
            r5.append(r7)
            java.lang.String r2 = r5.toString()
            goto L_0x0dfa
        L_0x0df9:
            r11 = 0
        L_0x0dfa:
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r11] = r14
            r7 = 1
            r5[r7] = r2
            java.lang.String r7 = r12.title
            r11 = 2
            r5[r11] = r7
            r7 = 2131692322(0x7f0f0b22, float:1.901374E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0e10:
            r11 = 2
            r2 = 2131692308(0x7f0f0b14, float:1.9013712E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r7 = 0
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r13 = 1
            r5[r13] = r7
            java.lang.String r7 = "NotificationActionPinnedNoText"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0e26:
            r7 = 0
            r11 = 2
            r13 = 1
        L_0x0e29:
            r2 = 2131692298(0x7f0f0b0a, float:1.9013692E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            r5[r7] = r14
            java.lang.String r7 = r12.title
            r5[r13] = r7
            java.lang.String r7 = "NotificationActionPinnedGeo"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x0e6f
        L_0x0e3c:
            java.lang.String r2 = r4.getStickerEmoji()
            if (r2 == 0) goto L_0x0e5a
            r5 = 2131692320(0x7f0f0b20, float:1.9013737E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r11 = 0
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r13 = 1
            r7[r13] = r11
            r15 = 2
            r7[r15] = r2
            java.lang.String r11 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            goto L_0x0e6e
        L_0x0e5a:
            r11 = 0
            r13 = 1
            r15 = 2
            r5 = 2131692318(0x7f0f0b1e, float:1.9013733E38)
            java.lang.Object[] r7 = new java.lang.Object[r15]
            r7[r11] = r14
            java.lang.String r11 = r12.title
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedSticker"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
        L_0x0e6e:
            r13 = r5
        L_0x0e6f:
            goto L_0x19f1
        L_0x0e71:
            im.bclpbkiauv.messenger.MessageObject r4 = r1.replyMessageObject
            if (r4 != 0) goto L_0x0e88
            r2 = 2131692309(0x7f0f0b15, float:1.9013714E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r5 = r12.title
            r7 = 0
            r4[r7] = r5
            java.lang.String r5 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x0e88:
            im.bclpbkiauv.messenger.MessageObject r4 = r1.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0ea4
            r2 = 2131692307(0x7f0f0b13, float:1.901371E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedMusicChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0ea4:
            boolean r5 = r4.isVideo()
            java.lang.String r13 = "NotificationActionPinnedTextChannel"
            if (r5 == 0) goto L_0x0ef8
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r5 < r7) goto L_0x0ee4
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0ee4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            r7 = 1
            r5[r7] = r2
            r7 = 2131692323(0x7f0f0b23, float:1.9013743E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x116c
        L_0x0ee4:
            r7 = 1
            r2 = 2131692325(0x7f0f0b25, float:1.9013747E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedVideoChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0ef8:
            boolean r2 = r4.isGif()
            if (r2 == 0) goto L_0x0f4b
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x0f36
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0f36
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            r11 = 1
            r5[r11] = r2
            r7 = 2131692323(0x7f0f0b23, float:1.9013743E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x116c
        L_0x0f36:
            r11 = 1
            r2 = 2131692303(0x7f0f0b0f, float:1.9013702E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            java.lang.String r7 = r12.title
            r19 = 0
            r5[r19] = r7
            java.lang.String r7 = "NotificationActionPinnedGifChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0f4b:
            r11 = 1
            r19 = 0
            boolean r2 = r4.isVoice()
            if (r2 == 0) goto L_0x0f66
            r2 = 2131692327(0x7f0f0b27, float:1.901375E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            java.lang.String r7 = r12.title
            r5[r19] = r7
            java.lang.String r7 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0f66:
            boolean r2 = r4.isRoundVideo()
            if (r2 == 0) goto L_0x0f7e
            r2 = 2131692317(0x7f0f0b1d, float:1.901373E38)
            java.lang.Object[] r5 = new java.lang.Object[r11]
            java.lang.String r7 = r12.title
            r5[r19] = r7
            java.lang.String r7 = "NotificationActionPinnedRoundChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0f7e:
            boolean r2 = r4.isSticker()
            if (r2 != 0) goto L_0x113f
            boolean r2 = r4.isAnimatedSticker()
            if (r2 == 0) goto L_0x0f8c
            goto L_0x113f
        L_0x0f8c:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r2 == 0) goto L_0x0fe0
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x0fcc
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0fcc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            r7 = 1
            r5[r7] = r2
            r7 = 2131692323(0x7f0f0b23, float:1.9013743E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x116c
        L_0x0fcc:
            r7 = 1
            r2 = 2131692293(0x7f0f0b05, float:1.9013682E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedFileChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x0fe0:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r2 != 0) goto L_0x112c
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r2 == 0) goto L_0x0ff4
            r7 = 1
            r11 = 0
            goto L_0x112e
        L_0x0ff4:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x1010
            r2 = 2131692301(0x7f0f0b0d, float:1.9013698E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x1010:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r2 == 0) goto L_0x103d
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaContact r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r2
            r5 = 2131692291(0x7f0f0b03, float:1.9013678E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r11 = r12.title
            r13 = 0
            r7[r13] = r11
            java.lang.String r11 = r2.first_name
            java.lang.String r13 = r2.last_name
            java.lang.String r11 = im.bclpbkiauv.messenger.ContactsController.formatName(r11, r13)
            r13 = 1
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedContactChannel2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            r13 = r2
            goto L_0x116c
        L_0x103d:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r2 == 0) goto L_0x1066
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r2 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r2
            r5 = 2131692315(0x7f0f0b1b, float:1.9013727E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r11 = r12.title
            r13 = 0
            r7[r13] = r11
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r11 = r2.poll
            java.lang.String r11 = r11.question
            r13 = 1
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedPollChannel2"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            r13 = r2
            goto L_0x116c
        L_0x1066:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r2 == 0) goto L_0x10ba
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r2 < r5) goto L_0x10a6
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x10a6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            r7 = 1
            r5[r7] = r2
            r7 = 2131692323(0x7f0f0b23, float:1.9013743E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x116c
        L_0x10a6:
            r7 = 1
            r2 = 2131692311(0x7f0f0b17, float:1.9013719E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x10ba:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r2 == 0) goto L_0x10d6
            r2 = 2131692295(0x7f0f0b07, float:1.9013686E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGameChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x10d6:
            java.lang.CharSequence r2 = r4.messageText
            if (r2 == 0) goto L_0x1119
            java.lang.CharSequence r2 = r4.messageText
            int r2 = r2.length()
            if (r2 <= 0) goto L_0x1119
            java.lang.CharSequence r2 = r4.messageText
            int r5 = r2.length()
            r7 = 20
            if (r5 <= r7) goto L_0x1105
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r7 = 20
            r11 = 0
            java.lang.CharSequence r7 = r2.subSequence(r11, r7)
            r5.append(r7)
            java.lang.String r7 = "..."
            r5.append(r7)
            java.lang.String r2 = r5.toString()
            goto L_0x1106
        L_0x1105:
            r11 = 0
        L_0x1106:
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r7 = r12.title
            r5[r11] = r7
            r7 = 1
            r5[r7] = r2
            r7 = 2131692323(0x7f0f0b23, float:1.9013743E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r13, r7, r5)
            r13 = r2
            goto L_0x116c
        L_0x1119:
            r7 = 1
            r2 = 2131692309(0x7f0f0b15, float:1.9013714E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r7 = r12.title
            r11 = 0
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x112c:
            r7 = 1
            r11 = 0
        L_0x112e:
            r2 = 2131692299(0x7f0f0b0b, float:1.9013694E38)
            java.lang.Object[] r5 = new java.lang.Object[r7]
            java.lang.String r7 = r12.title
            r5[r11] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoChannel"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r7, r2, r5)
            r13 = r2
            goto L_0x116c
        L_0x113f:
            java.lang.String r2 = r4.getStickerEmoji()
            if (r2 == 0) goto L_0x115a
            r5 = 2131692321(0x7f0f0b21, float:1.9013739E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r11 = r12.title
            r13 = 0
            r7[r13] = r11
            r11 = 1
            r7[r11] = r2
            java.lang.String r11 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
            goto L_0x116b
        L_0x115a:
            r11 = 1
            r13 = 0
            r5 = 2131692319(0x7f0f0b1f, float:1.9013735E38)
            java.lang.Object[] r7 = new java.lang.Object[r11]
            java.lang.String r11 = r12.title
            r7[r13] = r11
            java.lang.String r11 = "NotificationActionPinnedStickerChannel"
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.formatString(r11, r5, r7)
        L_0x116b:
            r13 = r5
        L_0x116c:
            goto L_0x19f1
        L_0x116e:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageActionGameScore
            if (r2 == 0) goto L_0x117e
            java.lang.CharSequence r2 = r1.messageText
            java.lang.String r13 = r2.toString()
            goto L_0x19f1
        L_0x117e:
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer
            if (r2 == 0) goto L_0x19ef
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageAction r2 = r2.action
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesActionReceivedRpkTransfer r2 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesActionReceivedRpkTransfer) r2
            int r4 = r2.trans
            if (r4 != 0) goto L_0x1247
            int r4 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r4 = im.bclpbkiauv.messenger.MessagesController.getInstance(r4)
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r2.receiver
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r4 = r4.getUser(r5)
            int r5 = r0.currentAccount
            im.bclpbkiauv.messenger.MessagesController r5 = im.bclpbkiauv.messenger.MessagesController.getInstance(r5)
            im.bclpbkiauv.tgnet.TLRPC$Peer r7 = r2.sender
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r7)
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r7 = r2.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r11 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r7 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r7, (java.lang.Class<?>) r11)
            T r11 = r7.model
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r11 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r11
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            if (r11 == 0) goto L_0x1242
            boolean r15 = r24.isOut()
            if (r15 == 0) goto L_0x11fe
            im.bclpbkiauv.messenger.UserConfig r15 = r23.getUserConfig()
            int r15 = r15.clientUserId
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r2.sender
            int r0 = r0.user_id
            if (r15 != r0) goto L_0x11e4
            r0 = 2131694868(0x7f0f1514, float:1.9018905E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r13.append(r0)
            goto L_0x1242
        L_0x11e4:
            r0 = 2131694865(0x7f0f1511, float:1.9018899E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r5)
            r18 = 0
            r15[r18] = r17
            java.lang.String r0 = java.lang.String.format(r0, r15)
            r13.append(r0)
            goto L_0x1242
        L_0x11fe:
            im.bclpbkiauv.messenger.UserConfig r0 = r23.getUserConfig()
            int r0 = r0.clientUserId
            im.bclpbkiauv.tgnet.TLRPC$Peer r15 = r2.sender
            int r15 = r15.user_id
            if (r0 != r15) goto L_0x1221
            java.lang.String r0 = im.bclpbkiauv.messenger.UserObject.getName(r4)
            r13.append(r0)
            java.lang.String r0 = " "
            r13.append(r0)
            r0 = 2131693297(0x7f0f0ef1, float:1.9015718E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r13.append(r0)
            goto L_0x1242
        L_0x1221:
            r0 = 2131694781(0x7f0f14bd, float:1.9018728E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r15 = 2
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r4)
            r18 = 0
            r15[r18] = r17
            java.lang.String r17 = im.bclpbkiauv.messenger.UserObject.getName(r5)
            r18 = 1
            r15[r18] = r17
            java.lang.String r0 = java.lang.String.format(r0, r15)
            r13.append(r0)
        L_0x1242:
            java.lang.String r0 = r13.toString()
            return r0
        L_0x1247:
            goto L_0x19ef
        L_0x1249:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x1268
            boolean r0 = r12.megagroup
            if (r0 != 0) goto L_0x1268
            r0 = 2131690466(0x7f0f03e2, float:1.9009976E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r12.title
            r5 = 0
            r2[r5] = r4
            java.lang.String r4 = "ChannelPhotoEditNotification"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1268:
            r5 = 0
            r0 = 2131692332(0x7f0f0b2c, float:1.9013761E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationEditedGroupPhoto"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x127e:
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r12)
            if (r0 == 0) goto L_0x1514
            boolean r0 = r12.megagroup
            if (r0 != 0) goto L_0x1514
            boolean r0 = r24.isMediaEmpty()
            if (r0 == 0) goto L_0x12c9
            if (r25 != 0) goto L_0x12b8
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x12b8
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            int r0 = r0.length()
            if (r0 == 0) goto L_0x12b8
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r1.messageOwner
            java.lang.String r5 = r5.message
            r7 = 1
            r0[r7] = r5
            r5 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r5, r0)
            r26[r2] = r7
            goto L_0x19f1
        L_0x12b8:
            r2 = 0
            r7 = 1
            r0 = 2131690450(0x7f0f03d2, float:1.9009944E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r2] = r14
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r4)
            goto L_0x19f1
        L_0x12c9:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x131b
            if (r25 != 0) goto L_0x130a
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x130a
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x130a
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r7 = 1
            r0[r7] = r5
            r5 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r5, r0)
            r26[r2] = r7
            goto L_0x19f1
        L_0x130a:
            r2 = 0
            r7 = 1
            r0 = 2131690451(0x7f0f03d3, float:1.9009946E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r2] = r14
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r4)
            goto L_0x19f1
        L_0x131b:
            boolean r0 = r24.isVideo()
            if (r0 == 0) goto L_0x136b
            if (r25 != 0) goto L_0x135a
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 < r5) goto L_0x135a
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x135a
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r5 = 0
            r0[r5] = r14
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            r7 = 1
            r0[r7] = r2
            r2 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r2, r0)
            r26[r5] = r7
            goto L_0x19f1
        L_0x135a:
            r5 = 0
            r7 = 1
            r0 = 2131690457(0x7f0f03d9, float:1.9009958E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r5] = r14
            java.lang.String r4 = "ChannelMessageVideo"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x136b:
            r5 = 0
            r7 = 1
            boolean r0 = r24.isVoice()
            if (r0 == 0) goto L_0x1382
            r0 = 2131690441(0x7f0f03c9, float:1.9009926E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r5] = r14
            java.lang.String r4 = "ChannelMessageAudio"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1382:
            boolean r0 = r24.isRoundVideo()
            if (r0 == 0) goto L_0x1397
            r0 = 2131690454(0x7f0f03d6, float:1.9009952E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r5] = r14
            java.lang.String r4 = "ChannelMessageRound"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1397:
            boolean r0 = r24.isMusic()
            if (r0 == 0) goto L_0x13ac
            r0 = 2131690449(0x7f0f03d1, float:1.9009942E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r5] = r14
            java.lang.String r4 = "ChannelMessageMusic"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x13ac:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r0 == 0) goto L_0x13d6
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaContact r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r0
            r2 = 2131690443(0x7f0f03cb, float:1.900993E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r0.first_name
            java.lang.String r7 = r0.last_name
            java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r7)
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "ChannelMessageContact2"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x13d6:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x13fc
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r0
            r2 = 2131690453(0x7f0f03d5, float:1.900995E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r5 = r0.poll
            java.lang.String r5 = r5.question
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = "ChannelMessagePoll2"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x13fc:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r0 != 0) goto L_0x1503
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r0 == 0) goto L_0x140e
            goto L_0x1503
        L_0x140e:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x1427
            r0 = 2131690447(0x7f0f03cf, float:1.9009938E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = "ChannelMessageLiveLocation"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1427:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x19ef
            boolean r0 = r24.isSticker()
            if (r0 != 0) goto L_0x14d7
            boolean r0 = r24.isAnimatedSticker()
            if (r0 == 0) goto L_0x143d
            goto L_0x14d7
        L_0x143d:
            boolean r0 = r24.isGif()
            if (r0 == 0) goto L_0x148d
            if (r25 != 0) goto L_0x147c
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x147c
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x147c
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r7 = 1
            r0[r7] = r5
            r5 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r5, r0)
            r26[r2] = r7
            goto L_0x19f1
        L_0x147c:
            r2 = 0
            r7 = 1
            r0 = 2131690446(0x7f0f03ce, float:1.9009936E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r2] = r14
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r4)
            goto L_0x19f1
        L_0x148d:
            if (r25 != 0) goto L_0x14c6
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x14c6
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x14c6
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r15)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r7 = 1
            r0[r7] = r5
            r5 = 2131692391(0x7f0f0b67, float:1.901388E38)
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r5, r0)
            r26[r2] = r7
            goto L_0x19f1
        L_0x14c6:
            r2 = 0
            r7 = 1
            r0 = 2131690444(0x7f0f03cc, float:1.9009932E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r2] = r14
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r2, r0, r4)
            goto L_0x19f1
        L_0x14d7:
            java.lang.String r0 = r24.getStickerEmoji()
            if (r0 == 0) goto L_0x14f1
            r2 = 2131690456(0x7f0f03d8, float:1.9009956E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            r7 = 1
            r4[r7] = r0
            java.lang.String r5 = "ChannelMessageStickerEmoji"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            r13 = r2
            goto L_0x1501
        L_0x14f1:
            r5 = 0
            r7 = 1
            r2 = 2131690455(0x7f0f03d7, float:1.9009954E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]
            r4[r5] = r14
            java.lang.String r5 = "ChannelMessageSticker"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            r13 = r2
        L_0x1501:
            goto L_0x19f1
        L_0x1503:
            r0 = 2131690448(0x7f0f03d0, float:1.900994E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = "ChannelMessageMap"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1514:
            boolean r0 = r24.isMediaEmpty()
            r4 = 2131692373(0x7f0f0b55, float:1.9013844E38)
            java.lang.String r5 = "NotificationMessageGroupText"
            if (r0 == 0) goto L_0x155f
            if (r25 != 0) goto L_0x1549
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x1549
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            int r0 = r0.length()
            if (r0 == 0) goto L_0x1549
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.String r2 = r12.title
            r7 = 1
            r0[r7] = r2
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r7 = 2
            r0[r7] = r2
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r4, r0)
            goto L_0x19f1
        L_0x1549:
            r7 = 2
            r0 = 2131692366(0x7f0f0b4e, float:1.901383E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupNoText"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x155f:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x15b6
            if (r25 != 0) goto L_0x15a0
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x15a0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x15a0
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.String r2 = r12.title
            r11 = 1
            r0[r11] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r7 = 2
            r0[r7] = r2
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r4, r0)
            goto L_0x19f1
        L_0x15a0:
            r7 = 2
            r0 = 2131692367(0x7f0f0b4f, float:1.9013832E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupPhoto"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x15b6:
            boolean r0 = r24.isVideo()
            if (r0 == 0) goto L_0x160b
            if (r25 != 0) goto L_0x15f5
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r0 < r7) goto L_0x15f5
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x15f5
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r7 = 0
            r0[r7] = r14
            java.lang.String r7 = r12.title
            r11 = 1
            r0[r11] = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r7.append(r2)
            java.lang.String r2 = r7.toString()
            r7 = 2
            r0[r7] = r2
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r4, r0)
            goto L_0x19f1
        L_0x15f5:
            r7 = 2
            r0 = 2131692374(0x7f0f0b56, float:1.9013846E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = " "
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x160b:
            boolean r0 = r24.isVoice()
            if (r0 == 0) goto L_0x1627
            r0 = 2131692355(0x7f0f0b43, float:1.9013808E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupAudio"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1627:
            boolean r0 = r24.isRoundVideo()
            if (r0 == 0) goto L_0x1643
            r0 = 2131692370(0x7f0f0b52, float:1.9013838E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupRound"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1643:
            boolean r0 = r24.isMusic()
            if (r0 == 0) goto L_0x165f
            r0 = 2131692365(0x7f0f0b4d, float:1.9013828E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupMusic"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x165f:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact
            if (r0 == 0) goto L_0x168e
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaContact r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaContact) r0
            r2 = 2131692357(0x7f0f0b45, float:1.9013812E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            java.lang.String r5 = r0.first_name
            java.lang.String r7 = r0.last_name
            java.lang.String r5 = im.bclpbkiauv.messenger.ContactsController.formatName(r5, r7)
            r7 = 2
            r4[r7] = r5
            java.lang.String r5 = "NotificationMessageGroupContact2"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x168e:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x16b9
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPC$TL_messageMediaPoll r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaPoll) r0
            r2 = 2131692369(0x7f0f0b51, float:1.9013836E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            im.bclpbkiauv.tgnet.TLRPC$TL_poll r5 = r0.poll
            java.lang.String r5 = r5.question
            r7 = 2
            r4[r7] = r5
            java.lang.String r5 = "NotificationMessageGroupPoll2"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            goto L_0x19f1
        L_0x16b9:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x16e2
            r0 = 2131692359(0x7f0f0b47, float:1.9013816E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r4 = r4.media
            im.bclpbkiauv.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r5 = 2
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupGame"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x16e2:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeo
            if (r0 != 0) goto L_0x19a1
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaVenue
            if (r0 == 0) goto L_0x16f4
            goto L_0x19a1
        L_0x16f4:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x1712
            r0 = 2131692363(0x7f0f0b4b, float:1.9013824E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupLiveLocation"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x1712:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x1802
            boolean r0 = r24.isSticker()
            if (r0 != 0) goto L_0x17cc
            boolean r0 = r24.isAnimatedSticker()
            if (r0 == 0) goto L_0x1728
            goto L_0x17cc
        L_0x1728:
            boolean r0 = r24.isGif()
            if (r0 == 0) goto L_0x177d
            if (r25 != 0) goto L_0x1767
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x1767
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x1767
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.String r2 = r12.title
            r7 = 1
            r0[r7] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r7 = 2
            r0[r7] = r2
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r4, r0)
            goto L_0x19f1
        L_0x1767:
            r7 = 2
            r0 = 2131692361(0x7f0f0b49, float:1.901382E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupGif"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x177d:
            if (r25 != 0) goto L_0x17b6
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x17b6
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x17b6
            r0 = 3
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r2 = 0
            r0[r2] = r14
            java.lang.String r2 = r12.title
            r7 = 1
            r0[r7] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r1.messageOwner
            java.lang.String r7 = r7.message
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r7 = 2
            r0[r7] = r2
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r4, r0)
            goto L_0x19f1
        L_0x17b6:
            r7 = 2
            r0 = 2131692358(0x7f0f0b46, float:1.9013814E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupDocument"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x17cc:
            java.lang.String r0 = r24.getStickerEmoji()
            if (r0 == 0) goto L_0x17eb
            r2 = 2131692372(0x7f0f0b54, float:1.9013842E38)
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r7 = 1
            r4[r7] = r5
            r11 = 2
            r4[r11] = r0
            java.lang.String r5 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            r13 = r2
            goto L_0x1800
        L_0x17eb:
            r5 = 0
            r7 = 1
            r11 = 2
            r2 = 2131692371(0x7f0f0b53, float:1.901384E38)
            java.lang.Object[] r4 = new java.lang.Object[r11]
            r4[r5] = r14
            java.lang.String r5 = r12.title
            r4[r7] = r5
            java.lang.String r5 = "NotificationMessageGroupSticker"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r5, r2, r4)
            r13 = r2
        L_0x1800:
            goto L_0x19f1
        L_0x1802:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia
            if (r0 == 0) goto L_0x19ef
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r1.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$MessageMedia r0 = r0.media
            im.bclpbkiauv.tgnet.TLRPCRedpacket$CL_messagesRpkTransferMedia r0 = (im.bclpbkiauv.tgnet.TLRPCRedpacket.CL_messagesRpkTransferMedia) r0
            int r2 = r0.trans
            if (r2 != 0) goto L_0x185b
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r0.data
            if (r4 == 0) goto L_0x1826
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r0.data
            java.lang.Class<im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse> r5 = im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r4 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r4, (java.lang.Class<?>) r5)
            T r5 = r4.model
            r2 = r5
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse r2 = (im.bclpbkiauv.ui.hui.packet.bean.RedpacketResponse) r2
        L_0x1826:
            if (r2 == 0) goto L_0x1853
            im.bclpbkiauv.ui.hui.packet.bean.RedpacketBean r4 = r2.getRed()
            int r5 = r4.getInitiatorUserIdInt()
            im.bclpbkiauv.messenger.MessagesController r7 = r23.getMessagesController()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r7 = r7.getUser(r11)
            r11 = 2131693288(0x7f0f0ee8, float:1.90157E38)
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r11)
            r13 = 1
            java.lang.Object[] r13 = new java.lang.Object[r13]
            java.lang.String r15 = im.bclpbkiauv.messenger.UserObject.getName(r7)
            r17 = 0
            r13[r17] = r15
            java.lang.String r11 = java.lang.String.format(r11, r13)
            return r11
        L_0x1853:
            r4 = 2131693287(0x7f0f0ee7, float:1.9015698E38)
            java.lang.String r4 = im.bclpbkiauv.messenger.LocaleController.getString(r4)
            return r4
        L_0x185b:
            r13 = 1
            int r2 = r0.trans
            if (r2 == r13) goto L_0x1865
            int r2 = r0.trans
            r4 = 2
            if (r2 != r4) goto L_0x1996
        L_0x1865:
            r2 = 0
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r0.data
            if (r4 == 0) goto L_0x1877
            im.bclpbkiauv.tgnet.TLRPC$TL_dataJSON r4 = r0.data
            java.lang.Class<im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse> r5 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.class
            im.bclpbkiauv.tgnet.TLApiModel r4 = im.bclpbkiauv.tgnet.TLJsonResolve.parse((im.bclpbkiauv.tgnet.TLObject) r4, (java.lang.Class<?>) r5)
            T r5 = r4.model
            r2 = r5
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse r2 = (im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse) r2
        L_0x1877:
            if (r2 == 0) goto L_0x1997
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r4 = r2.getState()
            boolean r5 = r24.isOutOwner()
            if (r5 == 0) goto L_0x18e0
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r5 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r4 != r5) goto L_0x188f
            r5 = 2131694417(0x7f0f1351, float:1.901799E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            return r5
        L_0x188f:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r5 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r4 != r5) goto L_0x18ae
            int r5 = r2.getInitiatorUserIdInt()
            int r7 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r7 = im.bclpbkiauv.messenger.UserConfig.getInstance(r7)
            int r7 = r7.clientUserId
            if (r5 != r7) goto L_0x18a9
            r7 = 2131694364(0x7f0f131c, float:1.9017882E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            return r7
        L_0x18a9:
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r19)
            return r7
        L_0x18ae:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r5 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r4 != r5) goto L_0x18d0
            int r5 = r2.getInitiatorUserIdInt()
            int r7 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r7 = im.bclpbkiauv.messenger.UserConfig.getInstance(r7)
            int r7 = r7.clientUserId
            if (r5 != r7) goto L_0x18c8
            r7 = 2131694356(0x7f0f1314, float:1.9017866E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            return r7
        L_0x18c8:
            r7 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.getString(r7)
            return r7
        L_0x18d0:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r5 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r4 != r5) goto L_0x18dc
            r5 = 2131694357(0x7f0f1315, float:1.9017868E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r5)
            return r5
        L_0x18dc:
            r17 = r0
            goto L_0x1995
        L_0x18e0:
            int r5 = r2.getInitiatorUserIdInt()
            java.lang.String r7 = r2.getRecipientUserId()
            int r7 = java.lang.Integer.parseInt(r7)
            im.bclpbkiauv.messenger.MessagesController r11 = r23.getMessagesController()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r5)
            im.bclpbkiauv.tgnet.TLRPC$User r11 = r11.getUser(r13)
            im.bclpbkiauv.messenger.MessagesController r13 = r23.getMessagesController()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r7)
            im.bclpbkiauv.tgnet.TLRPC$User r13 = r13.getUser(r15)
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r15 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.WAITING
            if (r4 != r15) goto L_0x1921
            r15 = 2131694384(0x7f0f1330, float:1.9017923E38)
            java.lang.String r15 = im.bclpbkiauv.messenger.LocaleController.getString(r15)
            r17 = r0
            r0 = 1
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r11)
            r19 = 0
            r0[r19] = r18
            java.lang.String r0 = java.lang.String.format(r15, r0)
            return r0
        L_0x1921:
            r17 = r0
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r0 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.RECEIVED
            if (r4 != r0) goto L_0x194d
            int r0 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            if (r5 != r0) goto L_0x1948
            r0 = 2131694383(0x7f0f132f, float:1.9017921E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r13)
            r19 = 0
            r15[r19] = r18
            java.lang.String r0 = java.lang.String.format(r0, r15)
            return r0
        L_0x1948:
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r19)
            return r0
        L_0x194d:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r0 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.REFUSED
            if (r4 != r0) goto L_0x197a
            int r0 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            im.bclpbkiauv.messenger.UserConfig r0 = im.bclpbkiauv.messenger.UserConfig.getInstance(r0)
            int r0 = r0.clientUserId
            if (r5 != r0) goto L_0x1972
            r0 = 2131694399(0x7f0f133f, float:1.9017953E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r13)
            r19 = 0
            r15[r19] = r18
            java.lang.String r0 = java.lang.String.format(r0, r15)
            return r0
        L_0x1972:
            r0 = 2131694860(0x7f0f150c, float:1.9018889E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            return r0
        L_0x197a:
            im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse$Status r0 = im.bclpbkiauv.ui.hui.transfer.bean.TransferResponse.Status.TIMEOUT
            if (r4 != r0) goto L_0x1995
            r0 = 2131694401(0x7f0f1341, float:1.9017958E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            r15 = 1
            java.lang.Object[] r15 = new java.lang.Object[r15]
            java.lang.String r18 = im.bclpbkiauv.messenger.UserObject.getName(r13)
            r19 = 0
            r15[r19] = r18
            java.lang.String r0 = java.lang.String.format(r0, r15)
            return r0
        L_0x1995:
        L_0x1996:
            goto L_0x19ef
        L_0x1997:
            r17 = r0
            r0 = 2131694363(0x7f0f131b, float:1.901788E38)
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0)
            return r0
        L_0x19a1:
            r0 = 2131692364(0x7f0f0b4c, float:1.9013826E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupMap"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x19b6:
            r16 = r5
        L_0x19b8:
            if (r27 == 0) goto L_0x19bd
            r0 = 0
            r27[r0] = r0
        L_0x19bd:
            boolean r0 = im.bclpbkiauv.messenger.ChatObject.isChannel(r12)
            if (r0 == 0) goto L_0x19d7
            boolean r0 = r12.megagroup
            if (r0 != 0) goto L_0x19d7
            r0 = 2131690450(0x7f0f03d2, float:1.9009944E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r14
            java.lang.String r4 = "ChannelMessageNoText"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x19d7:
            r4 = 0
            r0 = 2131692366(0x7f0f0b4e, float:1.901383E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r14
            java.lang.String r4 = r12.title
            r5 = 1
            r2[r5] = r4
            java.lang.String r4 = "NotificationMessageGroupNoText"
            java.lang.String r13 = im.bclpbkiauv.messenger.LocaleController.formatString(r4, r0, r2)
            goto L_0x19f1
        L_0x19ec:
            r16 = r5
            r12 = r13
        L_0x19ef:
            r13 = r22
        L_0x19f1:
            return r13
        L_0x19f2:
            r0 = 2131694857(0x7f0f1509, float:1.9018882E38)
            java.lang.String r2 = "YouHaveNewMessage"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.NotificationsController.getStringForMessage(im.bclpbkiauv.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int minutes = getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60);
            if (minutes <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(pintent);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) (minutes * 60 * 1000)), pintent);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.to_id != null && messageObject.messageOwner.to_id.chat_id == 0 && messageObject.messageOwner.to_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, -1);
        if (notifyOverride != 3) {
            return notifyOverride;
        }
        if (preferences.getInt("notifyuntil_" + dialog_id, 0) >= getConnectionsManager().getCurrentTime()) {
            return 2;
        }
        return notifyOverride;
    }

    public /* synthetic */ void lambda$showNotifications$23$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$showNotifications$23$NotificationsController();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$24$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$hideNotifications$24$NotificationsController() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
            notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    private void dismissNotification() {
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
                notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread($$Lambda$NotificationsController$_UbpcRooJOBBsGMhGLMKFel2yUM.INSTANCE);
            if (WearDataLayerListenerService.isWatchConnected()) {
                try {
                    JSONObject o = new JSONObject();
                    o.put(TtmlNode.ATTR_ID, getUserConfig().getClientUserId());
                    o.put("cancel_all", true);
                    WearDataLayerListenerService.sendMessageToWatch("/notify", o.toString().getBytes(), "remote_notifications");
                } catch (JSONException e) {
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.opened_dialog_id) != 2) {
                    notificationsQueue.postRunnable(new Runnable() {
                        public final void run() {
                            NotificationsController.this.lambda$playInChatSound$27$NotificationsController();
                        }
                    });
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public /* synthetic */ void lambda$playInChatSound$27$NotificationsController() {
        if (Math.abs(System.currentTimeMillis() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$GJSop0Py6GPWgXLt6aXArKSoaWs.INSTANCE);
                }
                if (this.soundIn == 0 && !this.soundInLoaded) {
                    this.soundInLoaded = true;
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_in, 1);
                }
                if (this.soundIn != 0) {
                    try {
                        this.soundPool.play(this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    static /* synthetic */ void lambda$null$26(SoundPool soundPool2, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool2.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + onlineReason);
            }
            this.notificationDelayWakelock.acquire(OkHttpUtils.DEFAULT_MILLISECONDS);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, (long) (onlineReason ? 3000 : 1000));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* access modifiers changed from: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$repeatNotificationMaybe$28$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$repeatNotificationMaybe$28$NotificationsController() {
        int hour = Calendar.getInstance().get(11);
        if (hour < 11 || hour > 22) {
            scheduleNotificationRepeat();
            return;
        }
        notificationManager.cancel(this.notificationId);
        showOrUpdateNotification(true);
    }

    private boolean isEmptyVibration(long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            return false;
        }
        for (long j : pattern) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    public void deleteNotificationChannel(long dialogId) {
        notificationsQueue.postRunnable(new Runnable(dialogId) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$29$NotificationsController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$deleteNotificationChannel$29$NotificationsController(long dialogId) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
                String key = "im.bclpbkiauv.key" + dialogId;
                String channelId = preferences.getString(key, (String) null);
                if (channelId != null) {
                    preferences.edit().remove(key).remove(key + "_s").commit();
                    systemNotificationManager.deleteNotificationChannel(channelId);
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void deleteAllNotificationChannels() {
        notificationsQueue.postRunnable(new Runnable() {
            public final void run() {
                NotificationsController.this.lambda$deleteAllNotificationChannels$30$NotificationsController();
            }
        });
    }

    public /* synthetic */ void lambda$deleteAllNotificationChannels$30$NotificationsController() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
                Map<String, ?> values = preferences.getAll();
                SharedPreferences.Editor editor = preferences.edit();
                for (Map.Entry<String, ?> entry : values.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith("im.bclpbkiauv.key")) {
                        if (!key.endsWith("_s")) {
                            systemNotificationManager.deleteNotificationChannel((String) entry.getValue());
                        }
                        editor.remove(key);
                    }
                }
                editor.commit();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private String validateChannelId(long dialogId, String name, long[] vibrationPattern, int ledColor, Uri sound, int importance, long[] configVibrationPattern, Uri configSound, int configImportance) {
        long[] jArr = vibrationPattern;
        int i = ledColor;
        Uri uri = sound;
        int i2 = importance;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        String key = "im.bclpbkiauv.key" + dialogId;
        String channelId = preferences.getString(key, (String) null);
        String settings = preferences.getString(key + "_s", (String) null);
        StringBuilder newSettings = new StringBuilder();
        int a = 0;
        while (a < jArr.length) {
            newSettings.append(jArr[a]);
            a++;
            long j = dialogId;
        }
        newSettings.append(i);
        if (uri != null) {
            newSettings.append(sound.toString());
        }
        newSettings.append(i2);
        String newSettingsHash = Utilities.MD5(newSettings.toString());
        if (channelId != null && !settings.equals(newSettingsHash)) {
            if (0 != 0) {
                preferences.edit().putString(key, channelId).putString(key + "_s", newSettingsHash).commit();
            } else {
                systemNotificationManager.deleteNotificationChannel(channelId);
                channelId = null;
            }
        }
        if (channelId == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.currentAccount);
            sb.append("channel");
            String str = channelId;
            sb.append(dialogId);
            sb.append("_");
            sb.append(Utilities.random.nextLong());
            channelId = sb.toString();
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, i2);
            if (i != 0) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(i);
            }
            if (!isEmptyVibration(jArr)) {
                notificationChannel.enableVibration(true);
                if (jArr != null && jArr.length > 0) {
                    notificationChannel.setVibrationPattern(jArr);
                }
            } else {
                notificationChannel.enableVibration(false);
            }
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            builder.setContentType(4);
            builder.setUsage(5);
            if (uri != null) {
                notificationChannel.setSound(uri, builder.build());
            } else {
                notificationChannel.setSound((Uri) null, builder.build());
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            preferences.edit().putString(key, channelId).putString(key + "_s", newSettingsHash).commit();
        } else {
            String str2 = name;
            String str3 = channelId;
        }
        return channelId;
    }

    /* JADX WARNING: Removed duplicated region for block: B:190:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x044b A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0458 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0491 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x052c A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0564 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x057d A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0582 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x058b A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x05a6 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x05c2 A[SYNTHETIC, Splitter:B:307:0x05c2] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x05f6 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0602 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0618 A[SYNTHETIC, Splitter:B:322:0x0618] */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0632 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x06b9 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x06f4 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0791 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x088d A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x08ec  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x08f2  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x094b A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0957 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x095d A[ADDED_TO_REGION, Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x09b0 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0a59 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0a61 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x0a6b A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0a72 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0b0e A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x0c08 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0c42 A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0c5b A[Catch:{ Exception -> 0x0ce0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:550:0x0c79  */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x0cb7 A[Catch:{ Exception -> 0x0cde }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r77) {
        /*
            r76 = this;
            r12 = r76
            r13 = r77
            java.lang.String r1 = "color_"
            java.lang.String r2 = "currentAccount"
            im.bclpbkiauv.messenger.UserConfig r3 = r76.getUserConfig()
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x0ceb
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r3 = r12.pushMessages
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0ceb
            boolean r3 = im.bclpbkiauv.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r3 != 0) goto L_0x0027
            int r3 = r12.currentAccount
            int r4 = im.bclpbkiauv.messenger.UserConfig.selectedAccount
            if (r3 == r4) goto L_0x0027
            r14 = r13
            goto L_0x0cec
        L_0x0027:
            im.bclpbkiauv.tgnet.ConnectionsManager r3 = r76.getConnectionsManager()     // Catch:{ Exception -> 0x0ce4 }
            r3.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0ce4 }
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r3 = r12.pushMessages     // Catch:{ Exception -> 0x0ce4 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0ce4 }
            im.bclpbkiauv.messenger.MessageObject r3 = (im.bclpbkiauv.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0ce4 }
            r14 = r3
            im.bclpbkiauv.messenger.AccountInstance r3 = r76.getAccountInstance()     // Catch:{ Exception -> 0x0ce4 }
            android.content.SharedPreferences r3 = r3.getNotificationsSettings()     // Catch:{ Exception -> 0x0ce4 }
            r15 = r3
            java.lang.String r3 = "dismissDate"
            int r3 = r15.getInt(r3, r4)     // Catch:{ Exception -> 0x0ce4 }
            r11 = r3
            im.bclpbkiauv.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0ce4 }
            int r3 = r3.date     // Catch:{ Exception -> 0x0ce4 }
            if (r3 > r11) goto L_0x0057
            r76.dismissNotification()     // Catch:{ Exception -> 0x0052 }
            return
        L_0x0052:
            r0 = move-exception
            r1 = r0
            r14 = r13
            goto L_0x0ce7
        L_0x0057:
            long r5 = r14.getDialogId()     // Catch:{ Exception -> 0x0ce4 }
            r9 = r5
            r3 = 0
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x0ce4 }
            boolean r7 = r7.mentioned     // Catch:{ Exception -> 0x0ce4 }
            if (r7 == 0) goto L_0x006b
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x0052 }
            int r7 = r7.from_id     // Catch:{ Exception -> 0x0052 }
            long r5 = (long) r7
            r7 = r5
            goto L_0x006c
        L_0x006b:
            r7 = r5
        L_0x006c:
            int r5 = r14.getId()     // Catch:{ Exception -> 0x0ce4 }
            r16 = r5
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0ce4 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id     // Catch:{ Exception -> 0x0ce4 }
            int r5 = r5.chat_id     // Catch:{ Exception -> 0x0ce4 }
            if (r5 == 0) goto L_0x0081
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0052 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id     // Catch:{ Exception -> 0x0052 }
            int r5 = r5.chat_id     // Catch:{ Exception -> 0x0052 }
            goto L_0x0087
        L_0x0081:
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0ce4 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id     // Catch:{ Exception -> 0x0ce4 }
            int r5 = r5.channel_id     // Catch:{ Exception -> 0x0ce4 }
        L_0x0087:
            r6 = r5
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0ce4 }
            im.bclpbkiauv.tgnet.TLRPC$Peer r5 = r5.to_id     // Catch:{ Exception -> 0x0ce4 }
            int r5 = r5.user_id     // Catch:{ Exception -> 0x0ce4 }
            if (r5 != 0) goto L_0x0096
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0052 }
            int r4 = r4.from_id     // Catch:{ Exception -> 0x0052 }
            r5 = r4
            goto L_0x00a5
        L_0x0096:
            im.bclpbkiauv.messenger.UserConfig r4 = r76.getUserConfig()     // Catch:{ Exception -> 0x0ce4 }
            int r4 = r4.getClientUserId()     // Catch:{ Exception -> 0x0ce4 }
            if (r5 != r4) goto L_0x00a5
            im.bclpbkiauv.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0052 }
            int r4 = r4.from_id     // Catch:{ Exception -> 0x0052 }
            r5 = r4
        L_0x00a5:
            im.bclpbkiauv.messenger.MessagesController r4 = r76.getMessagesController()     // Catch:{ Exception -> 0x0ce4 }
            r18 = r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0ce4 }
            im.bclpbkiauv.tgnet.TLRPC$User r3 = r4.getUser(r3)     // Catch:{ Exception -> 0x0ce4 }
            r4 = r3
            r3 = 0
            r19 = r3
            if (r6 == 0) goto L_0x00d8
            im.bclpbkiauv.messenger.MessagesController r3 = r76.getMessagesController()     // Catch:{ Exception -> 0x0052 }
            r21 = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x0052 }
            im.bclpbkiauv.tgnet.TLRPC$Chat r3 = r3.getChat(r11)     // Catch:{ Exception -> 0x0052 }
            boolean r11 = im.bclpbkiauv.messenger.ChatObject.isChannel(r3)     // Catch:{ Exception -> 0x0052 }
            if (r11 == 0) goto L_0x00d3
            boolean r11 = r3.megagroup     // Catch:{ Exception -> 0x0052 }
            if (r11 != 0) goto L_0x00d3
            r11 = 1
            goto L_0x00d4
        L_0x00d3:
            r11 = 0
        L_0x00d4:
            r18 = r11
            r11 = r3
            goto L_0x00dc
        L_0x00d8:
            r21 = r11
            r11 = r19
        L_0x00dc:
            r3 = 0
            r19 = 0
            r22 = 0
            r23 = -16776961(0xffffffffff0000ff, float:-1.7014636E38)
            r24 = 0
            int r25 = r12.getNotifyOverride(r15, r7)     // Catch:{ Exception -> 0x0ce4 }
            r26 = r25
            r25 = r3
            r3 = -1
            r27 = r14
            r14 = r26
            if (r14 != r3) goto L_0x00fc
            boolean r26 = r12.isGlobalNotificationsEnabled((long) r9)     // Catch:{ Exception -> 0x0052 }
            r29 = r26
            goto L_0x0104
        L_0x00fc:
            r3 = 2
            if (r14 == r3) goto L_0x0101
            r3 = 1
            goto L_0x0102
        L_0x0101:
            r3 = 0
        L_0x0102:
            r29 = r3
        L_0x0104:
            if (r13 == 0) goto L_0x0108
            if (r29 != 0) goto L_0x010a
        L_0x0108:
            r19 = 1
        L_0x010a:
            java.lang.String r3 = "custom_"
            r30 = 1000(0x3e8, double:4.94E-321)
            if (r19 != 0) goto L_0x01d3
            int r32 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r32 != 0) goto L_0x01d3
            if (r11 == 0) goto L_0x01d3
            r32 = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r7.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r7.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            r7.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0ce0 }
            r8 = 0
            boolean r7 = r15.getBoolean(r7, r8)     // Catch:{ Exception -> 0x0ce0 }
            if (r7 == 0) goto L_0x0163
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0052 }
            r7.<init>()     // Catch:{ Exception -> 0x0052 }
            java.lang.String r8 = "smart_max_count_"
            r7.append(r8)     // Catch:{ Exception -> 0x0052 }
            r7.append(r9)     // Catch:{ Exception -> 0x0052 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0052 }
            r8 = 2
            int r7 = r15.getInt(r7, r8)     // Catch:{ Exception -> 0x0052 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0052 }
            r8.<init>()     // Catch:{ Exception -> 0x0052 }
            r34 = r7
            java.lang.String r7 = "smart_delay_"
            r8.append(r7)     // Catch:{ Exception -> 0x0052 }
            r8.append(r9)     // Catch:{ Exception -> 0x0052 }
            java.lang.String r7 = r8.toString()     // Catch:{ Exception -> 0x0052 }
            r8 = 180(0xb4, float:2.52E-43)
            int r7 = r15.getInt(r7, r8)     // Catch:{ Exception -> 0x0052 }
            r8 = r7
            r7 = r34
            goto L_0x0166
        L_0x0163:
            r7 = 2
            r8 = 180(0xb4, float:2.52E-43)
        L_0x0166:
            if (r7 == 0) goto L_0x01c8
            r34 = r14
            android.util.LongSparseArray<android.graphics.Point> r14 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0ce0 }
            java.lang.Object r14 = r14.get(r9)     // Catch:{ Exception -> 0x0ce0 }
            android.graphics.Point r14 = (android.graphics.Point) r14     // Catch:{ Exception -> 0x0ce0 }
            if (r14 != 0) goto L_0x018c
            android.graphics.Point r13 = new android.graphics.Point     // Catch:{ Exception -> 0x0ce0 }
            long r35 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0ce0 }
            r37 = r1
            r38 = r2
            long r1 = r35 / r30
            int r2 = (int) r1     // Catch:{ Exception -> 0x0ce0 }
            r1 = 1
            r13.<init>(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r1 = r13
            android.util.LongSparseArray<android.graphics.Point> r2 = r12.smartNotificationsDialogs     // Catch:{ Exception -> 0x0ce0 }
            r2.put(r9, r1)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x01db
        L_0x018c:
            r37 = r1
            r38 = r2
            int r1 = r14.y     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r1 + r8
            r13 = r1
            long r1 = (long) r2     // Catch:{ Exception -> 0x0ce0 }
            long r35 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0ce0 }
            long r35 = r35 / r30
            int r39 = (r1 > r35 ? 1 : (r1 == r35 ? 0 : -1))
            if (r39 >= 0) goto L_0x01ac
            long r1 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0ce0 }
            long r1 = r1 / r30
            int r2 = (int) r1     // Catch:{ Exception -> 0x0ce0 }
            r1 = 1
            r14.set(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x01db
        L_0x01ac:
            int r1 = r14.x     // Catch:{ Exception -> 0x0ce0 }
            if (r1 >= r7) goto L_0x01c1
            int r2 = r1 + 1
            long r35 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0ce0 }
            r39 = r7
            r40 = r8
            long r7 = r35 / r30
            int r8 = (int) r7     // Catch:{ Exception -> 0x0ce0 }
            r14.set(r2, r8)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x01db
        L_0x01c1:
            r39 = r7
            r40 = r8
            r19 = 1
            goto L_0x01db
        L_0x01c8:
            r37 = r1
            r38 = r2
            r39 = r7
            r40 = r8
            r34 = r14
            goto L_0x01db
        L_0x01d3:
            r37 = r1
            r38 = r2
            r32 = r7
            r34 = r14
        L_0x01db:
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r1 = r1.getPath()     // Catch:{ Exception -> 0x0ce0 }
            r13 = r1
            java.lang.String r1 = "EnableInAppSounds"
            r2 = 1
            boolean r1 = r15.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r14 = r1
            java.lang.String r1 = "EnableInAppVibrate"
            boolean r1 = r15.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r35 = r1
            java.lang.String r1 = "EnableInAppPreview"
            boolean r1 = r15.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r36 = r1
            r39 = 1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r1.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r1.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            r1.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0ce0 }
            r2 = 0
            boolean r1 = r15.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r40 = r1
            r2 = 0
            r3 = 3
            if (r1 == 0) goto L_0x025a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r1.<init>()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = "vibrate_"
            r1.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            r1.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0ce0 }
            r7 = 0
            int r1 = r15.getInt(r1, r7)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r7.<init>()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r8 = "priority_"
            r7.append(r8)     // Catch:{ Exception -> 0x0ce0 }
            r7.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0ce0 }
            int r7 = r15.getInt(r7, r3)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r8.<init>()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = "sound_path_"
            r8.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            r8.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r15.getString(r3, r2)     // Catch:{ Exception -> 0x0ce0 }
            r8 = r1
            goto L_0x025e
        L_0x025a:
            r1 = 0
            r7 = 3
            r3 = 0
            r8 = r1
        L_0x025e:
            r1 = 0
            if (r6 == 0) goto L_0x02d0
            if (r18 == 0) goto L_0x029a
            if (r3 == 0) goto L_0x026d
            boolean r43 = r3.equals(r13)     // Catch:{ Exception -> 0x0ce0 }
            if (r43 == 0) goto L_0x026d
            r3 = 0
            goto L_0x0276
        L_0x026d:
            if (r3 != 0) goto L_0x0276
            java.lang.String r2 = "ChannelSoundPath"
            java.lang.String r2 = r15.getString(r2, r13)     // Catch:{ Exception -> 0x0ce0 }
            r3 = r2
        L_0x0276:
            java.lang.String r2 = "vibrate_channel"
            r44 = r1
            r1 = 0
            int r2 = r15.getInt(r2, r1)     // Catch:{ Exception -> 0x0ce0 }
            r22 = r2
            java.lang.String r1 = "priority_channel"
            r2 = 1
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r24 = r1
            java.lang.String r1 = "ChannelLed"
            r2 = -16776961(0xffffffffff0000ff, float:-1.7014636E38)
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r23 = r1
            r1 = r22
            goto L_0x030a
        L_0x029a:
            r44 = r1
            if (r3 == 0) goto L_0x02a6
            boolean r1 = r3.equals(r13)     // Catch:{ Exception -> 0x0ce0 }
            if (r1 == 0) goto L_0x02a6
            r3 = 0
            goto L_0x02af
        L_0x02a6:
            if (r3 != 0) goto L_0x02af
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r15.getString(r1, r13)     // Catch:{ Exception -> 0x0ce0 }
            r3 = r1
        L_0x02af:
            java.lang.String r1 = "vibrate_group"
            r2 = 0
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r22 = r1
            java.lang.String r1 = "priority_group"
            r2 = 1
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r24 = r1
            java.lang.String r1 = "GroupLed"
            r2 = -16776961(0xffffffffff0000ff, float:-1.7014636E38)
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r23 = r1
            r1 = r22
            goto L_0x030a
        L_0x02d0:
            r44 = r1
            if (r5 == 0) goto L_0x0308
            if (r3 == 0) goto L_0x02de
            boolean r1 = r3.equals(r13)     // Catch:{ Exception -> 0x0ce0 }
            if (r1 == 0) goto L_0x02de
            r3 = 0
            goto L_0x02e7
        L_0x02de:
            if (r3 != 0) goto L_0x02e7
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r15.getString(r1, r13)     // Catch:{ Exception -> 0x0ce0 }
            r3 = r1
        L_0x02e7:
            java.lang.String r1 = "vibrate_messages"
            r2 = 0
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r22 = r1
            java.lang.String r1 = "priority_messages"
            r2 = 1
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r24 = r1
            java.lang.String r1 = "MessagesLed"
            r2 = -16776961(0xffffffffff0000ff, float:-1.7014636E38)
            int r1 = r15.getInt(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            r23 = r1
            r1 = r22
            goto L_0x030a
        L_0x0308:
            r1 = r22
        L_0x030a:
            if (r40 == 0) goto L_0x033c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r22 = r3
            r3 = r37
            r2.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            boolean r2 = r15.contains(r2)     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x033e
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r9)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            r3 = 0
            int r2 = r15.getInt(r2, r3)     // Catch:{ Exception -> 0x0ce0 }
            r23 = r2
            goto L_0x033e
        L_0x033c:
            r22 = r3
        L_0x033e:
            r2 = 3
            if (r7 == r2) goto L_0x0346
            r24 = r7
            r2 = r24
            goto L_0x0348
        L_0x0346:
            r2 = r24
        L_0x0348:
            r3 = 4
            if (r1 != r3) goto L_0x034f
            r24 = 1
            r1 = 0
            goto L_0x0351
        L_0x034f:
            r24 = r44
        L_0x0351:
            r3 = 2
            if (r1 != r3) goto L_0x035a
            r3 = 1
            if (r8 == r3) goto L_0x0364
            r3 = 3
            if (r8 == r3) goto L_0x0364
        L_0x035a:
            r3 = 2
            if (r1 == r3) goto L_0x035f
            if (r8 == r3) goto L_0x0364
        L_0x035f:
            if (r8 == 0) goto L_0x0365
            r3 = 4
            if (r8 == r3) goto L_0x0365
        L_0x0364:
            r1 = r8
        L_0x0365:
            boolean r3 = im.bclpbkiauv.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0ce0 }
            if (r3 != 0) goto L_0x038a
            if (r14 != 0) goto L_0x036d
            r3 = 0
            goto L_0x036f
        L_0x036d:
            r3 = r22
        L_0x036f:
            if (r35 != 0) goto L_0x0372
            r1 = 2
        L_0x0372:
            if (r39 != 0) goto L_0x037a
            r2 = 0
            r45 = r3
            r3 = r1
            r1 = 2
            goto L_0x038e
        L_0x037a:
            r22 = r1
            r1 = 2
            if (r2 != r1) goto L_0x0385
            r2 = 1
            r45 = r3
            r3 = r22
            goto L_0x038e
        L_0x0385:
            r45 = r3
            r3 = r22
            goto L_0x038e
        L_0x038a:
            r3 = r1
            r1 = 2
            r45 = r22
        L_0x038e:
            if (r24 == 0) goto L_0x03ae
            if (r3 == r1) goto L_0x03ae
            android.media.AudioManager r1 = audioManager     // Catch:{ Exception -> 0x03a6 }
            int r1 = r1.getRingerMode()     // Catch:{ Exception -> 0x03a6 }
            if (r1 == 0) goto L_0x03a1
            r22 = r3
            r3 = 1
            if (r1 == r3) goto L_0x03a3
            r3 = 2
            goto L_0x03a5
        L_0x03a1:
            r22 = r3
        L_0x03a3:
            r3 = r22
        L_0x03a5:
            goto L_0x03b2
        L_0x03a6:
            r0 = move-exception
            r22 = r3
            r1 = r0
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x03b0
        L_0x03ae:
            r22 = r3
        L_0x03b0:
            r3 = r22
        L_0x03b2:
            r1 = 0
            r22 = 0
            r43 = 0
            r44 = r1
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r46 = r7
            java.lang.String r7 = "NoSound"
            r47 = 100
            r49 = r8
            r8 = 26
            r51 = 0
            if (r1 < r8) goto L_0x0449
            r1 = 2
            if (r3 != r1) goto L_0x03d7
            long[] r8 = new long[r1]     // Catch:{ Exception -> 0x0ce0 }
            r1 = 0
            r8[r1] = r51     // Catch:{ Exception -> 0x0ce0 }
            r1 = 1
            r8[r1] = r51     // Catch:{ Exception -> 0x0ce0 }
            r22 = r8
            goto L_0x0406
        L_0x03d7:
            r1 = 1
            if (r3 != r1) goto L_0x03ec
            r8 = 4
            long[] r1 = new long[r8]     // Catch:{ Exception -> 0x0ce0 }
            r8 = 0
            r1[r8] = r51     // Catch:{ Exception -> 0x0ce0 }
            r8 = 1
            r1[r8] = r47     // Catch:{ Exception -> 0x0ce0 }
            r8 = 2
            r1[r8] = r51     // Catch:{ Exception -> 0x0ce0 }
            r8 = 3
            r1[r8] = r47     // Catch:{ Exception -> 0x0ce0 }
            r22 = r1
            goto L_0x0406
        L_0x03ec:
            if (r3 == 0) goto L_0x0401
            r1 = 4
            if (r3 != r1) goto L_0x03f2
            goto L_0x0401
        L_0x03f2:
            r1 = 3
            if (r3 != r1) goto L_0x0406
            r1 = 2
            long[] r8 = new long[r1]     // Catch:{ Exception -> 0x0ce0 }
            r1 = 0
            r8[r1] = r51     // Catch:{ Exception -> 0x0ce0 }
            r1 = 1
            r8[r1] = r30     // Catch:{ Exception -> 0x0ce0 }
            r22 = r8
            goto L_0x0406
        L_0x0401:
            r1 = 0
            long[] r8 = new long[r1]     // Catch:{ Exception -> 0x0ce0 }
            r22 = r8
        L_0x0406:
            r1 = r45
            if (r1 == 0) goto L_0x041e
            boolean r8 = r1.equals(r7)     // Catch:{ Exception -> 0x0ce0 }
            if (r8 != 0) goto L_0x041e
            boolean r8 = r1.equals(r13)     // Catch:{ Exception -> 0x0ce0 }
            if (r8 == 0) goto L_0x0419
            android.net.Uri r8 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0420
        L_0x0419:
            android.net.Uri r8 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0420
        L_0x041e:
            r8 = r44
        L_0x0420:
            if (r2 != 0) goto L_0x0429
            r43 = 3
            r45 = r1
            r44 = r8
            goto L_0x0449
        L_0x0429:
            r45 = r1
            r1 = 1
            if (r2 == r1) goto L_0x0445
            r1 = 2
            if (r2 != r1) goto L_0x0432
            goto L_0x0445
        L_0x0432:
            r1 = 4
            if (r2 != r1) goto L_0x043a
            r43 = 1
            r44 = r8
            goto L_0x0449
        L_0x043a:
            r1 = 5
            if (r2 != r1) goto L_0x0442
            r43 = 2
            r44 = r8
            goto L_0x0449
        L_0x0442:
            r44 = r8
            goto L_0x0449
        L_0x0445:
            r43 = 4
            r44 = r8
        L_0x0449:
            if (r19 == 0) goto L_0x0458
            r3 = 0
            r2 = 0
            r23 = 0
            r45 = 0
            r8 = r2
            r2 = r3
            r3 = r23
            r1 = r45
            goto L_0x045e
        L_0x0458:
            r8 = r2
            r2 = r3
            r3 = r23
            r1 = r45
        L_0x045e:
            r23 = r14
            android.content.Intent r14 = new android.content.Intent     // Catch:{ Exception -> 0x0ce0 }
            r45 = r15
            android.content.Context r15 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r54 = r2
            java.lang.Class<im.bclpbkiauv.ui.LaunchActivity> r2 = im.bclpbkiauv.ui.LaunchActivity.class
            r14.<init>(r15, r2)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r15 = "com.tmessages.openchat"
            r2.append(r15)     // Catch:{ Exception -> 0x0ce0 }
            r15 = r7
            r55 = r8
            double r7 = java.lang.Math.random()     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            r7 = 2147483647(0x7fffffff, float:NaN)
            r2.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            r14.setAction(r2)     // Catch:{ Exception -> 0x0ce0 }
            int r2 = (int) r9     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x052c
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            r8 = 1
            if (r2 != r8) goto L_0x04aa
            if (r6 == 0) goto L_0x04a2
            java.lang.String r2 = "chatId"
            r14.putExtra(r2, r6)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x04aa
        L_0x04a2:
            if (r5 == 0) goto L_0x04aa
            java.lang.String r2 = "userId"
            r14.putExtra(r2, r5)     // Catch:{ Exception -> 0x0ce0 }
        L_0x04aa:
            r2 = 0
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r2)     // Catch:{ Exception -> 0x0ce0 }
            if (r8 != 0) goto L_0x0526
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x04b7
            goto L_0x0526
        L_0x04b7:
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            r8 = 1
            if (r2 != r8) goto L_0x0522
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r8 = 28
            if (r2 >= r8) goto L_0x0522
            if (r11 == 0) goto L_0x04f3
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r2 = r11.photo     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x04ee
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r2 = r11.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x04ee
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r2 = r11.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            long r7 = r2.volume_id     // Catch:{ Exception -> 0x0ce0 }
            int r2 = (r7 > r51 ? 1 : (r7 == r51 ? 0 : -1))
            if (r2 == 0) goto L_0x04ee
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r2 = r11.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.local_id     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x04ee
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r2 = r11.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            r7 = r2
            r57 = r3
            r8 = r4
            goto L_0x054e
        L_0x04ee:
            r57 = r3
            r8 = r4
            goto L_0x054c
        L_0x04f3:
            if (r4 == 0) goto L_0x051e
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r2 = r4.photo     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x051a
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r2 = r4.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x051a
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r2 = r4.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            long r7 = r2.volume_id     // Catch:{ Exception -> 0x0ce0 }
            int r2 = (r7 > r51 ? 1 : (r7 == r51 ? 0 : -1))
            if (r2 == 0) goto L_0x051a
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r2 = r4.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.local_id     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x051a
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r2 = r4.photo     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0ce0 }
            r7 = r2
            r57 = r3
            r8 = r4
            goto L_0x054e
        L_0x051a:
            r57 = r3
            r8 = r4
            goto L_0x054c
        L_0x051e:
            r57 = r3
            r8 = r4
            goto L_0x054c
        L_0x0522:
            r57 = r3
            r8 = r4
            goto L_0x054c
        L_0x0526:
            r2 = 0
            r7 = r2
            r57 = r3
            r8 = r4
            goto L_0x054e
        L_0x052c:
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            r7 = 1
            if (r2 != r7) goto L_0x0549
            long r7 = globalSecretChatId     // Catch:{ Exception -> 0x0ce0 }
            int r2 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0549
            java.lang.String r2 = "encId"
            r57 = r3
            r8 = r4
            r7 = 32
            long r3 = r9 >> r7
            int r4 = (int) r3     // Catch:{ Exception -> 0x0ce0 }
            r14.putExtra(r2, r4)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x054c
        L_0x0549:
            r57 = r3
            r8 = r4
        L_0x054c:
            r7 = r25
        L_0x054e:
            int r2 = r12.currentAccount     // Catch:{ Exception -> 0x0ce0 }
            r3 = r38
            r14.putExtra(r3, r2)     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r4 = 1073741824(0x40000000, float:2.0)
            r25 = r5
            r5 = 0
            android.app.PendingIntent r2 = android.app.PendingIntent.getActivity(r2, r5, r14, r4)     // Catch:{ Exception -> 0x0ce0 }
            r5 = r2
            r2 = 1
            if (r6 == 0) goto L_0x0566
            if (r11 == 0) goto L_0x0568
        L_0x0566:
            if (r8 != 0) goto L_0x0577
        L_0x0568:
            boolean r4 = r27.isFcmMessage()     // Catch:{ Exception -> 0x0ce0 }
            if (r4 == 0) goto L_0x0577
            r4 = r27
            r27 = r2
            java.lang.String r2 = r4.localName     // Catch:{ Exception -> 0x0ce0 }
            r38 = r2
            goto L_0x0588
        L_0x0577:
            r4 = r27
            r27 = r2
            if (r11 == 0) goto L_0x0582
            java.lang.String r2 = r11.title     // Catch:{ Exception -> 0x0ce0 }
            r38 = r2
            goto L_0x0588
        L_0x0582:
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getName(r8)     // Catch:{ Exception -> 0x0ce0 }
            r38 = r2
        L_0x0588:
            int r2 = (int) r9     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x05a6
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            r58 = r6
            r6 = 1
            if (r2 > r6) goto L_0x05a8
            r2 = 0
            boolean r6 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r2)     // Catch:{ Exception -> 0x0ce0 }
            if (r6 != 0) goto L_0x05a8
            boolean r2 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x05a2
            goto L_0x05a8
        L_0x05a2:
            r2 = r38
            r6 = r2
            goto L_0x05b5
        L_0x05a6:
            r58 = r6
        L_0x05a8:
            java.lang.String r2 = "AppName"
            r6 = 2131689824(0x7f0f0160, float:1.9008674E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r2, r6)     // Catch:{ Exception -> 0x0ce0 }
            r6 = 0
            r27 = r6
            r6 = r2
        L_0x05b5:
            int r2 = im.bclpbkiauv.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0ce0 }
            r59 = r14
            java.lang.String r14 = ""
            r60 = r15
            r15 = 1
            if (r2 <= r15) goto L_0x05f6
            android.util.LongSparseArray<java.lang.Integer> r2 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            if (r2 != r15) goto L_0x05d7
            im.bclpbkiauv.messenger.UserConfig r2 = r76.getUserConfig()     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$User r2 = r2.getCurrentUser()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = im.bclpbkiauv.messenger.UserObject.getFirstName(r2)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x05f7
        L_0x05d7:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.messenger.UserConfig r15 = r76.getUserConfig()     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$User r15 = r15.getCurrentUser()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r15 = im.bclpbkiauv.messenger.UserObject.getFirstName(r15)     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r15)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r15 = "・"
            r2.append(r15)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x05f7
        L_0x05f6:
            r2 = r14
        L_0x05f7:
            android.util.LongSparseArray<java.lang.Integer> r15 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r15 = r15.size()     // Catch:{ Exception -> 0x0ce0 }
            r61 = r9
            r9 = 1
            if (r15 != r9) goto L_0x060d
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r10 = 23
            if (r9 >= r10) goto L_0x0609
            goto L_0x060d
        L_0x0609:
            r65 = r13
            r13 = r2
            goto L_0x066a
        L_0x060d:
            android.util.LongSparseArray<java.lang.Integer> r9 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r10 = "NewMessages"
            r15 = 1
            if (r9 != r15) goto L_0x0632
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r9.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r9.append(r2)     // Catch:{ Exception -> 0x0ce0 }
            int r15 = r12.total_unread_count     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r10, r15)     // Catch:{ Exception -> 0x0ce0 }
            r9.append(r10)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0ce0 }
            r2 = r9
            r65 = r13
            r13 = r2
            goto L_0x066a
        L_0x0632:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r9.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r9.append(r2)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r15 = "NotificationMessagesPeopleDisplayOrder"
            r63 = r2
            r65 = r13
            r2 = 2
            java.lang.Object[] r13 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r12.total_unread_count     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r10, r2)     // Catch:{ Exception -> 0x0ce0 }
            r10 = 0
            r13[r10] = r2     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r10 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatPluralString(r2, r10)     // Catch:{ Exception -> 0x0ce0 }
            r10 = 1
            r13[r10] = r2     // Catch:{ Exception -> 0x0ce0 }
            r2 = 2131692393(0x7f0f0b69, float:1.9013885E38)
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.formatString(r15, r2, r13)     // Catch:{ Exception -> 0x0ce0 }
            r9.append(r2)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r9.toString()     // Catch:{ Exception -> 0x0ce0 }
            r13 = r2
        L_0x066a:
            androidx.core.app.NotificationCompat$Builder r2 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r9 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>(r9)     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$Builder r2 = r2.setContentTitle(r6)     // Catch:{ Exception -> 0x0ce0 }
            r9 = 2131558767(0x7f0d016f, float:1.874286E38)
            androidx.core.app.NotificationCompat$Builder r2 = r2.setSmallIcon(r9)     // Catch:{ Exception -> 0x0ce0 }
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r2 = r2.setAutoCancel(r9)     // Catch:{ Exception -> 0x0ce0 }
            int r9 = r12.total_unread_count     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$Builder r2 = r2.setNumber(r9)     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$Builder r2 = r2.setContentIntent(r5)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r9 = r12.notificationGroup     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$Builder r2 = r2.setGroup(r9)     // Catch:{ Exception -> 0x0ce0 }
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r2 = r2.setGroupSummary(r9)     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$Builder r2 = r2.setShowWhen(r9)     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$Message r9 = r4.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            int r9 = r9.date     // Catch:{ Exception -> 0x0ce0 }
            long r9 = (long) r9     // Catch:{ Exception -> 0x0ce0 }
            long r9 = r9 * r30
            androidx.core.app.NotificationCompat$Builder r2 = r2.setWhen(r9)     // Catch:{ Exception -> 0x0ce0 }
            r9 = -15618822(0xffffffffff11acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r2 = r2.setColor(r9)     // Catch:{ Exception -> 0x0ce0 }
            r15 = r2
            r2 = 0
            r9 = 0
            r10 = 0
            r63 = r2
            java.lang.String r2 = "msg"
            r15.setCategory(r2)     // Catch:{ Exception -> 0x0ce0 }
            if (r11 != 0) goto L_0x06e1
            if (r8 == 0) goto L_0x06e1
            java.lang.String r2 = r8.phone     // Catch:{ Exception -> 0x0ce0 }
            if (r2 == 0) goto L_0x06e1
            java.lang.String r2 = r8.phone     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0ce0 }
            if (r2 <= 0) goto L_0x06e1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r64 = r5
            java.lang.String r5 = "tel:+"
            r2.append(r5)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r5 = r8.phone     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r5)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            r15.addPerson(r2)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x06e3
        L_0x06e1:
            r64 = r5
        L_0x06e3:
            r2 = 2
            r5 = 0
            r66 = 0
            r67 = r2
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r2 = r12.pushMessages     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0ce0 }
            r68 = r5
            r5 = 1
            if (r2 != r5) goto L_0x0791
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r2 = r12.pushMessages     // Catch:{ Exception -> 0x0ce0 }
            r5 = 0
            java.lang.Object r2 = r2.get(r5)     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.messenger.MessageObject r2 = (im.bclpbkiauv.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0ce0 }
            r69 = r8
            r5 = 1
            boolean[] r8 = new boolean[r5]     // Catch:{ Exception -> 0x0ce0 }
            r5 = r8
            r70 = r9
            r8 = 0
            r9 = 0
            java.lang.String r71 = r12.getStringForMessage(r2, r9, r5, r8)     // Catch:{ Exception -> 0x0ce0 }
            r8 = r71
            r9 = r71
            r68 = r8
            im.bclpbkiauv.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            boolean r8 = r8.silent     // Catch:{ Exception -> 0x0ce0 }
            if (r9 != 0) goto L_0x0718
            return
        L_0x0718:
            if (r27 == 0) goto L_0x076e
            if (r11 == 0) goto L_0x0737
            r71 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r67 = r8
            java.lang.String r8 = " @ "
            r2.append(r8)     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r9.replace(r2, r14)     // Catch:{ Exception -> 0x0ce0 }
            r9 = r2
            goto L_0x0772
        L_0x0737:
            r71 = r2
            r67 = r8
            r2 = 0
            boolean r8 = r5[r2]     // Catch:{ Exception -> 0x0ce0 }
            if (r8 == 0) goto L_0x0757
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r8 = ": "
            r2.append(r8)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r9.replace(r2, r14)     // Catch:{ Exception -> 0x0ce0 }
            r9 = r2
            goto L_0x0772
        L_0x0757:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r2.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r8 = " "
            r2.append(r8)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r2 = r9.replace(r2, r14)     // Catch:{ Exception -> 0x0ce0 }
            r9 = r2
            goto L_0x0772
        L_0x076e:
            r71 = r2
            r67 = r8
        L_0x0772:
            r15.setContentText(r9)     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$BigTextStyle r2 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$BigTextStyle r2 = r2.bigText(r9)     // Catch:{ Exception -> 0x0ce0 }
            r15.setStyle(r2)     // Catch:{ Exception -> 0x0ce0 }
            r72 = r1
            r74 = r3
            r73 = r7
            r8 = r68
            r75 = r67
            r67 = r10
            r10 = r75
            goto L_0x0865
        L_0x0791:
            r69 = r8
            r70 = r9
            r15.setContentText(r13)     // Catch:{ Exception -> 0x0ce0 }
            androidx.core.app.NotificationCompat$InboxStyle r2 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0ce0 }
            r2.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r2.setBigContentTitle(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r5 = r12.pushMessages     // Catch:{ Exception -> 0x0ce0 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0ce0 }
            r8 = 10
            int r5 = java.lang.Math.min(r8, r5)     // Catch:{ Exception -> 0x0ce0 }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0ce0 }
            r8 = r9
            r9 = 0
            r75 = r10
            r10 = r9
            r9 = r67
            r67 = r75
        L_0x07b8:
            if (r10 >= r5) goto L_0x0854
            r71 = r5
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r5 = r12.pushMessages     // Catch:{ Exception -> 0x0ce0 }
            java.lang.Object r5 = r5.get(r10)     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5     // Catch:{ Exception -> 0x0ce0 }
            r72 = r1
            r73 = r7
            r1 = 0
            r7 = 0
            java.lang.String r74 = r12.getStringForMessage(r5, r7, r8, r1)     // Catch:{ Exception -> 0x0ce0 }
            r1 = r74
            if (r1 == 0) goto L_0x0846
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r5.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0ce0 }
            r74 = r3
            r3 = r21
            if (r7 > r3) goto L_0x07e0
            r21 = r3
            goto L_0x0848
        L_0x07e0:
            r7 = 2
            if (r9 != r7) goto L_0x07ea
            r68 = r1
            im.bclpbkiauv.tgnet.TLRPC$Message r7 = r5.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            boolean r7 = r7.silent     // Catch:{ Exception -> 0x0ce0 }
            r9 = r7
        L_0x07ea:
            android.util.LongSparseArray<java.lang.Integer> r7 = r12.pushDialogs     // Catch:{ Exception -> 0x0ce0 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0ce0 }
            r21 = r3
            r3 = 1
            if (r7 != r3) goto L_0x0842
            if (r27 == 0) goto L_0x0842
            if (r11 == 0) goto L_0x0810
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r3.<init>()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = " @ "
            r3.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            r3.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r1.replace(r3, r14)     // Catch:{ Exception -> 0x0ce0 }
            r1 = r3
            goto L_0x0842
        L_0x0810:
            r3 = 0
            boolean r7 = r8[r3]     // Catch:{ Exception -> 0x0ce0 }
            if (r7 == 0) goto L_0x082c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r3.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r3.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = ": "
            r3.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r1.replace(r3, r14)     // Catch:{ Exception -> 0x0ce0 }
            r1 = r3
            goto L_0x0842
        L_0x082c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r3.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r3.append(r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r7 = " "
            r3.append(r7)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r1.replace(r3, r14)     // Catch:{ Exception -> 0x0ce0 }
            r1 = r3
        L_0x0842:
            r2.addLine(r1)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0848
        L_0x0846:
            r74 = r3
        L_0x0848:
            int r10 = r10 + 1
            r5 = r71
            r1 = r72
            r7 = r73
            r3 = r74
            goto L_0x07b8
        L_0x0854:
            r72 = r1
            r74 = r3
            r71 = r5
            r73 = r7
            r2.setSummaryText(r13)     // Catch:{ Exception -> 0x0ce0 }
            r15.setStyle(r2)     // Catch:{ Exception -> 0x0ce0 }
            r10 = r9
            r8 = r68
        L_0x0865:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            java.lang.Class<im.bclpbkiauv.messenger.NotificationDismissReceiver> r3 = im.bclpbkiauv.messenger.NotificationDismissReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0ce0 }
            r9 = r1
            java.lang.String r1 = "messageDate"
            im.bclpbkiauv.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            int r2 = r2.date     // Catch:{ Exception -> 0x0ce0 }
            r9.putExtra(r1, r2)     // Catch:{ Exception -> 0x0ce0 }
            int r1 = r12.currentAccount     // Catch:{ Exception -> 0x0ce0 }
            r2 = r74
            r9.putExtra(r2, r1)     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r1 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 1
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r5, r9, r3)     // Catch:{ Exception -> 0x0ce0 }
            r15.setDeleteIntent(r1)     // Catch:{ Exception -> 0x0ce0 }
            if (r73 == 0) goto L_0x08ec
            im.bclpbkiauv.messenger.ImageLoader r1 = im.bclpbkiauv.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r5 = "50_50"
            r7 = r73
            r3 = 0
            android.graphics.drawable.BitmapDrawable r1 = r1.getImageFromMemory(r7, r3, r5)     // Catch:{ Exception -> 0x0ce0 }
            if (r1 == 0) goto L_0x08a4
            android.graphics.Bitmap r3 = r1.getBitmap()     // Catch:{ Exception -> 0x0ce0 }
            r15.setLargeIcon(r3)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x08ee
        L_0x08a4:
            r3 = 1
            java.io.File r5 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r7, r3)     // Catch:{ all -> 0x08e8 }
            r3 = r5
            boolean r5 = r3.exists()     // Catch:{ all -> 0x08e8 }
            if (r5 == 0) goto L_0x08e5
            r68 = 1112014848(0x42480000, float:50.0)
            int r5 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r68)     // Catch:{ all -> 0x08e8 }
            float r5 = (float) r5     // Catch:{ all -> 0x08e8 }
            r68 = 1126170624(0x43200000, float:160.0)
            float r5 = r68 / r5
            android.graphics.BitmapFactory$Options r68 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x08e8 }
            r68.<init>()     // Catch:{ all -> 0x08e8 }
            r71 = r68
            r68 = 1065353216(0x3f800000, float:1.0)
            int r68 = (r5 > r68 ? 1 : (r5 == r68 ? 0 : -1))
            if (r68 >= 0) goto L_0x08cc
            r68 = r1
            r1 = 1
            goto L_0x08cf
        L_0x08cc:
            r68 = r1
            int r1 = (int) r5
        L_0x08cf:
            r73 = r5
            r5 = r71
            r5.inSampleSize = r1     // Catch:{ all -> 0x08e3 }
            java.lang.String r1 = r3.getAbsolutePath()     // Catch:{ all -> 0x08e3 }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1, r5)     // Catch:{ all -> 0x08e3 }
            if (r1 == 0) goto L_0x08e7
            r15.setLargeIcon(r1)     // Catch:{ all -> 0x08e3 }
            goto L_0x08e7
        L_0x08e3:
            r0 = move-exception
            goto L_0x08ee
        L_0x08e5:
            r68 = r1
        L_0x08e7:
            goto L_0x08ee
        L_0x08e8:
            r0 = move-exception
            r68 = r1
            goto L_0x08ee
        L_0x08ec:
            r7 = r73
        L_0x08ee:
            r5 = r77
            if (r5 == 0) goto L_0x094b
            r1 = 1
            if (r10 != r1) goto L_0x08f8
            r3 = r55
            goto L_0x094d
        L_0x08f8:
            if (r55 != 0) goto L_0x0912
            r1 = 0
            r15.setPriority(r1)     // Catch:{ Exception -> 0x090d }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x090d }
            r3 = 26
            if (r1 < r3) goto L_0x090a
            r1 = 3
            r70 = r1
            r3 = r55
            goto L_0x095a
        L_0x090a:
            r3 = r55
            goto L_0x095a
        L_0x090d:
            r0 = move-exception
            r1 = r0
            r14 = r5
            goto L_0x0ce7
        L_0x0912:
            r3 = r55
            r1 = 1
            if (r3 == r1) goto L_0x093d
            r1 = 2
            if (r3 != r1) goto L_0x091b
            goto L_0x093d
        L_0x091b:
            r1 = 4
            if (r3 != r1) goto L_0x092c
            r1 = -2
            r15.setPriority(r1)     // Catch:{ Exception -> 0x0ce0 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 26
            if (r1 < r5) goto L_0x095a
            r1 = 1
            r70 = r1
            goto L_0x095a
        L_0x092c:
            r1 = 5
            if (r3 != r1) goto L_0x095a
            r1 = -1
            r15.setPriority(r1)     // Catch:{ Exception -> 0x0ce0 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 26
            if (r1 < r5) goto L_0x095a
            r1 = 2
            r70 = r1
            goto L_0x095a
        L_0x093d:
            r1 = 1
            r15.setPriority(r1)     // Catch:{ Exception -> 0x0ce0 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 26
            if (r1 < r5) goto L_0x095a
            r1 = 4
            r70 = r1
            goto L_0x095a
        L_0x094b:
            r3 = r55
        L_0x094d:
            r1 = -1
            r15.setPriority(r1)     // Catch:{ Exception -> 0x0ce0 }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 26
            if (r1 < r5) goto L_0x095a
            r1 = 2
            r70 = r1
        L_0x095a:
            r1 = 1
            if (r10 == r1) goto L_0x0adb
            if (r19 != 0) goto L_0x0adb
            boolean r1 = im.bclpbkiauv.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0ce0 }
            if (r1 != 0) goto L_0x096b
            if (r36 == 0) goto L_0x0966
            goto L_0x096b
        L_0x0966:
            r55 = r3
            r26 = r6
            goto L_0x09a6
        L_0x096b:
            if (r8 == 0) goto L_0x099f
            int r1 = r8.length()     // Catch:{ Exception -> 0x0ce0 }
            r5 = 100
            if (r1 <= r5) goto L_0x099f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ce0 }
            r1.<init>()     // Catch:{ Exception -> 0x0ce0 }
            r5 = 100
            r55 = r3
            r3 = 0
            java.lang.String r5 = r8.substring(r3, r5)     // Catch:{ Exception -> 0x0ce0 }
            r26 = r6
            r3 = 10
            r6 = 32
            java.lang.String r3 = r5.replace(r3, r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = r3.trim()     // Catch:{ Exception -> 0x0ce0 }
            r1.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r3 = "..."
            r1.append(r3)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0ce0 }
            r8 = r1
            goto L_0x09a3
        L_0x099f:
            r55 = r3
            r26 = r6
        L_0x09a3:
            r15.setTicker(r8)     // Catch:{ Exception -> 0x0ce0 }
        L_0x09a6:
            im.bclpbkiauv.messenger.MediaController r1 = im.bclpbkiauv.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0ce0 }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0ce0 }
            if (r1 != 0) goto L_0x0a59
            if (r72 == 0) goto L_0x0a54
            r3 = r60
            r1 = r72
            boolean r3 = r1.equals(r3)     // Catch:{ Exception -> 0x0ce0 }
            if (r3 != 0) goto L_0x0a51
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 26
            if (r3 < r5) goto L_0x09dc
            r6 = r65
            boolean r3 = r1.equals(r6)     // Catch:{ Exception -> 0x0ce0 }
            if (r3 == 0) goto L_0x09d2
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0ce0 }
            r65 = r6
            r73 = r7
            goto L_0x0a5f
        L_0x09d2:
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0ce0 }
            r65 = r6
            r73 = r7
            goto L_0x0a5f
        L_0x09dc:
            r6 = r65
            boolean r3 = r1.equals(r6)     // Catch:{ Exception -> 0x0ce0 }
            if (r3 == 0) goto L_0x09f0
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0ce0 }
            r5 = 5
            r15.setSound(r3, r5)     // Catch:{ Exception -> 0x0ce0 }
            r65 = r6
            r73 = r7
            goto L_0x0a5d
        L_0x09f0:
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r5 = 24
            if (r3 < r5) goto L_0x0a44
            java.lang.String r3 = "file://"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x0ce0 }
            if (r3 == 0) goto L_0x0a44
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0ce0 }
            boolean r3 = im.bclpbkiauv.messenger.AndroidUtilities.isInternalUri(r3)     // Catch:{ Exception -> 0x0ce0 }
            if (r3 != 0) goto L_0x0a44
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a34 }
            java.lang.String r5 = "im.bclpbkiauv.messenger.provider"
            r65 = r6
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0a2f }
            r73 = r7
            java.lang.String r7 = "file://"
            java.lang.String r7 = r1.replace(r7, r14)     // Catch:{ Exception -> 0x0a2c }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0a2c }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r5, r6)     // Catch:{ Exception -> 0x0a2c }
            android.content.Context r5 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a2c }
            java.lang.String r6 = "com.android.systemui"
            r7 = 1
            r5.grantUriPermission(r6, r3, r7)     // Catch:{ Exception -> 0x0a2c }
            r5 = 5
            r15.setSound(r3, r5)     // Catch:{ Exception -> 0x0a2c }
            goto L_0x0a5d
        L_0x0a2c:
            r0 = move-exception
            r3 = r0
            goto L_0x0a3a
        L_0x0a2f:
            r0 = move-exception
            r73 = r7
            r3 = r0
            goto L_0x0a3a
        L_0x0a34:
            r0 = move-exception
            r65 = r6
            r73 = r7
            r3 = r0
        L_0x0a3a:
            android.net.Uri r5 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0ce0 }
            r6 = 5
            r15.setSound(r5, r6)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0a5d
        L_0x0a44:
            r65 = r6
            r73 = r7
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0ce0 }
            r5 = 5
            r15.setSound(r3, r5)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0a5d
        L_0x0a51:
            r73 = r7
            goto L_0x0a5d
        L_0x0a54:
            r73 = r7
            r1 = r72
            goto L_0x0a5d
        L_0x0a59:
            r73 = r7
            r1 = r72
        L_0x0a5d:
            r3 = r67
        L_0x0a5f:
            if (r57 == 0) goto L_0x0a6b
            r5 = 1000(0x3e8, float:1.401E-42)
            r6 = 1000(0x3e8, float:1.401E-42)
            r7 = r57
            r15.setLights(r7, r5, r6)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0a6d
        L_0x0a6b:
            r7 = r57
        L_0x0a6d:
            r5 = r54
            r6 = 2
            if (r5 == r6) goto L_0x0ac8
            im.bclpbkiauv.messenger.MediaController r6 = im.bclpbkiauv.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0ce0 }
            boolean r6 = r6.isRecordingAudio()     // Catch:{ Exception -> 0x0ce0 }
            if (r6 == 0) goto L_0x0a7d
            goto L_0x0ac8
        L_0x0a7d:
            r6 = 1
            if (r5 != r6) goto L_0x0a9a
            r14 = 4
            long[] r14 = new long[r14]     // Catch:{ Exception -> 0x0ce0 }
            r17 = 0
            r14[r17] = r51     // Catch:{ Exception -> 0x0ce0 }
            r14[r6] = r47     // Catch:{ Exception -> 0x0ce0 }
            r6 = 2
            r14[r6] = r51     // Catch:{ Exception -> 0x0ce0 }
            r6 = 3
            r14[r6] = r47     // Catch:{ Exception -> 0x0ce0 }
            r6 = r14
            r15.setVibrate(r14)     // Catch:{ Exception -> 0x0ce0 }
            r67 = r3
            r63 = r6
            r14 = r8
            goto L_0x0af7
        L_0x0a9a:
            if (r5 == 0) goto L_0x0aba
            r6 = 4
            if (r5 != r6) goto L_0x0aa0
            goto L_0x0aba
        L_0x0aa0:
            r6 = 3
            if (r5 != r6) goto L_0x0ab6
            r6 = 2
            long[] r14 = new long[r6]     // Catch:{ Exception -> 0x0ce0 }
            r6 = 0
            r14[r6] = r51     // Catch:{ Exception -> 0x0ce0 }
            r6 = 1
            r14[r6] = r30     // Catch:{ Exception -> 0x0ce0 }
            r6 = r14
            r15.setVibrate(r14)     // Catch:{ Exception -> 0x0ce0 }
            r67 = r3
            r63 = r6
            r14 = r8
            goto L_0x0af7
        L_0x0ab6:
            r67 = r3
            r14 = r8
            goto L_0x0af7
        L_0x0aba:
            r6 = 2
            r15.setDefaults(r6)     // Catch:{ Exception -> 0x0ce0 }
            r6 = 0
            long[] r14 = new long[r6]     // Catch:{ Exception -> 0x0ce0 }
            r6 = r14
            r67 = r3
            r63 = r6
            r14 = r8
            goto L_0x0af7
        L_0x0ac8:
            r6 = 2
            long[] r14 = new long[r6]     // Catch:{ Exception -> 0x0ce0 }
            r6 = 0
            r14[r6] = r51     // Catch:{ Exception -> 0x0ce0 }
            r6 = 1
            r14[r6] = r51     // Catch:{ Exception -> 0x0ce0 }
            r6 = r14
            r15.setVibrate(r14)     // Catch:{ Exception -> 0x0ce0 }
            r67 = r3
            r63 = r6
            r14 = r8
            goto L_0x0af7
        L_0x0adb:
            r55 = r3
            r26 = r6
            r73 = r7
            r5 = r54
            r7 = r57
            r1 = r72
            r3 = 2
            long[] r6 = new long[r3]     // Catch:{ Exception -> 0x0ce0 }
            r3 = 0
            r6[r3] = r51     // Catch:{ Exception -> 0x0ce0 }
            r3 = 1
            r6[r3] = r51     // Catch:{ Exception -> 0x0ce0 }
            r3 = r6
            r15.setVibrate(r6)     // Catch:{ Exception -> 0x0ce0 }
            r63 = r3
            r14 = r8
        L_0x0af7:
            r3 = 0
            r6 = 0
            boolean r8 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r6)     // Catch:{ Exception -> 0x0ce0 }
            if (r8 != 0) goto L_0x0c08
            boolean r6 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0ce0 }
            if (r6 != 0) goto L_0x0c08
            long r30 = r4.getDialogId()     // Catch:{ Exception -> 0x0ce0 }
            r47 = 777000(0xbdb28, double:3.83889E-318)
            int r6 = (r30 > r47 ? 1 : (r30 == r47 ? 0 : -1))
            if (r6 != 0) goto L_0x0c08
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup     // Catch:{ Exception -> 0x0ce0 }
            if (r6 == 0) goto L_0x0bf9
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup     // Catch:{ Exception -> 0x0ce0 }
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow> r6 = r6.rows     // Catch:{ Exception -> 0x0ce0 }
            r8 = 0
            int r20 = r6.size()     // Catch:{ Exception -> 0x0ce0 }
            r30 = r20
        L_0x0b21:
            r72 = r1
            r1 = r30
            if (r8 >= r1) goto L_0x0be6
            java.lang.Object r20 = r6.get(r8)     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow r20 = (im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonRow) r20     // Catch:{ Exception -> 0x0ce0 }
            r30 = r20
            r20 = 0
            r31 = r1
            r1 = r30
            r30 = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons     // Catch:{ Exception -> 0x0ce0 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0ce0 }
            r54 = r5
            r5 = r20
        L_0x0b41:
            if (r5 >= r3) goto L_0x0bc3
            r20 = r3
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons     // Catch:{ Exception -> 0x0ce0 }
            java.lang.Object r3 = r3.get(r5)     // Catch:{ Exception -> 0x0ce0 }
            im.bclpbkiauv.tgnet.TLRPC$KeyboardButton r3 = (im.bclpbkiauv.tgnet.TLRPC.KeyboardButton) r3     // Catch:{ Exception -> 0x0ce0 }
            r37 = r1
            boolean r1 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0ce0 }
            if (r1 == 0) goto L_0x0ba0
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0ce0 }
            r41 = r6
            android.content.Context r6 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r57 = r7
            java.lang.Class<im.bclpbkiauv.messenger.NotificationCallbackReceiver> r7 = im.bclpbkiauv.messenger.NotificationCallbackReceiver.class
            r1.<init>(r6, r7)     // Catch:{ Exception -> 0x0ce0 }
            int r6 = r12.currentAccount     // Catch:{ Exception -> 0x0ce0 }
            r1.putExtra(r2, r6)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r6 = "did"
            r48 = r9
            r47 = r10
            r9 = r61
            r1.putExtra(r6, r9)     // Catch:{ Exception -> 0x0ce0 }
            byte[] r6 = r3.data     // Catch:{ Exception -> 0x0ce0 }
            if (r6 == 0) goto L_0x0b7b
            java.lang.String r6 = "data"
            byte[] r7 = r3.data     // Catch:{ Exception -> 0x0ce0 }
            r1.putExtra(r6, r7)     // Catch:{ Exception -> 0x0ce0 }
        L_0x0b7b:
            java.lang.String r6 = "mid"
            int r7 = r4.getId()     // Catch:{ Exception -> 0x0ce0 }
            r1.putExtra(r6, r7)     // Catch:{ Exception -> 0x0ce0 }
            java.lang.String r6 = r3.text     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r7 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r50 = r3
            int r3 = r12.lastButtonId     // Catch:{ Exception -> 0x0ce0 }
            r51 = r4
            int r4 = r3 + 1
            r12.lastButtonId = r4     // Catch:{ Exception -> 0x0ce0 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r7, r3, r1, r4)     // Catch:{ Exception -> 0x0ce0 }
            r4 = 0
            r15.addAction(r4, r6, r3)     // Catch:{ Exception -> 0x0ce0 }
            r3 = 1
            r30 = r3
            goto L_0x0baf
        L_0x0ba0:
            r50 = r3
            r51 = r4
            r41 = r6
            r57 = r7
            r48 = r9
            r47 = r10
            r9 = r61
            r4 = 0
        L_0x0baf:
            int r5 = r5 + 1
            r61 = r9
            r3 = r20
            r1 = r37
            r6 = r41
            r10 = r47
            r9 = r48
            r4 = r51
            r7 = r57
            goto L_0x0b41
        L_0x0bc3:
            r37 = r1
            r20 = r3
            r51 = r4
            r41 = r6
            r57 = r7
            r48 = r9
            r47 = r10
            r9 = r61
            r4 = 0
            int r8 = r8 + 1
            r3 = r30
            r30 = r31
            r10 = r47
            r9 = r48
            r4 = r51
            r5 = r54
            r1 = r72
            goto L_0x0b21
        L_0x0be6:
            r31 = r1
            r30 = r3
            r51 = r4
            r54 = r5
            r41 = r6
            r57 = r7
            r48 = r9
            r47 = r10
            r9 = r61
            goto L_0x0c18
        L_0x0bf9:
            r72 = r1
            r51 = r4
            r54 = r5
            r57 = r7
            r48 = r9
            r47 = r10
            r9 = r61
            goto L_0x0c16
        L_0x0c08:
            r72 = r1
            r51 = r4
            r54 = r5
            r57 = r7
            r48 = r9
            r47 = r10
            r9 = r61
        L_0x0c16:
            r30 = r3
        L_0x0c18:
            if (r30 != 0) goto L_0x0c73
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r3 = 24
            if (r1 >= r3) goto L_0x0c73
            java.lang.String r1 = im.bclpbkiauv.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0ce0 }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0ce0 }
            if (r1 != 0) goto L_0x0c73
            boolean r1 = r76.hasMessagesToReply()     // Catch:{ Exception -> 0x0ce0 }
            if (r1 == 0) goto L_0x0c73
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r3 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            java.lang.Class<im.bclpbkiauv.messenger.PopupReplyReceiver> r4 = im.bclpbkiauv.messenger.PopupReplyReceiver.class
            r1.<init>(r3, r4)     // Catch:{ Exception -> 0x0ce0 }
            int r3 = r12.currentAccount     // Catch:{ Exception -> 0x0ce0 }
            r1.putExtra(r2, r3)     // Catch:{ Exception -> 0x0ce0 }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r3 = 19
            if (r2 > r3) goto L_0x0c5b
            r2 = 2131231077(0x7f080165, float:1.8078225E38)
            java.lang.String r3 = "Reply"
            r4 = 2131693448(0x7f0f0f88, float:1.9016025E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0ce0 }
            r15.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0ce0 }
            goto L_0x0c73
        L_0x0c5b:
            r2 = 2131231076(0x7f080164, float:1.8078223E38)
            java.lang.String r3 = "Reply"
            r4 = 2131693448(0x7f0f0f88, float:1.9016025E38)
            java.lang.String r3 = im.bclpbkiauv.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0ce0 }
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ce0 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0ce0 }
            r15.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0ce0 }
        L_0x0c73:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0ce0 }
            r2 = 26
            if (r1 < r2) goto L_0x0cb7
            r17 = r72
            r1 = r76
            r20 = r21
            r21 = r54
            r8 = r55
            r28 = r57
            r2 = r9
            r31 = r51
            r37 = r69
            r4 = r38
            r7 = r77
            r41 = r64
            r5 = r63
            r50 = r26
            r26 = r58
            r42 = r65
            r6 = r28
            r51 = r14
            r52 = r73
            r14 = r7
            r7 = r67
            r53 = r8
            r8 = r70
            r54 = r9
            r9 = r22
            r10 = r44
            r56 = r11
            r11 = r43
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0cde }
            r15.setChannelId(r1)     // Catch:{ Exception -> 0x0cde }
            goto L_0x0cd7
        L_0x0cb7:
            r56 = r11
            r20 = r21
            r50 = r26
            r31 = r51
            r21 = r54
            r53 = r55
            r28 = r57
            r26 = r58
            r41 = r64
            r42 = r65
            r37 = r69
            r17 = r72
            r52 = r73
            r54 = r9
            r51 = r14
            r14 = r77
        L_0x0cd7:
            r12.showExtraNotifications(r15, r14, r13)     // Catch:{ Exception -> 0x0cde }
            r76.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0cde }
            goto L_0x0cea
        L_0x0cde:
            r0 = move-exception
            goto L_0x0ce6
        L_0x0ce0:
            r0 = move-exception
            r14 = r77
            goto L_0x0ce6
        L_0x0ce4:
            r0 = move-exception
            r14 = r13
        L_0x0ce6:
            r1 = r0
        L_0x0ce7:
            im.bclpbkiauv.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0cea:
            return
        L_0x0ceb:
            r14 = r13
        L_0x0cec:
            r76.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:164:0x04ab  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x04b2  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x04c9  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x04df  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x052b  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0537  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x054f  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0926  */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x093a  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0959  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x095f  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x09bb  */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x09f4  */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0a18  */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0a3f  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x0b27  */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x0b3c  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x0b42  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x0b46  */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0b4b  */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:374:0x0b54  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0c61  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0c8b  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0d97  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r72, boolean r73, java.lang.String r74) {
        /*
            r71 = this;
            r1 = r71
            android.app.Notification r2 = r72.build()
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r0 >= r3) goto L_0x001d
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r3 = r1.notificationId
            r0.notify(r3, r2)
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "show summary notification by SDK check"
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x001c:
            return
        L_0x001d:
            im.bclpbkiauv.messenger.AccountInstance r0 = r71.getAccountInstance()
            android.content.SharedPreferences r3 = r0.getNotificationsSettings()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r4 = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r5 = r0
            r0 = 0
        L_0x0032:
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r6 = r1.pushMessages
            int r6 = r6.size()
            r7 = 0
            if (r0 >= r6) goto L_0x0081
            java.util.ArrayList<im.bclpbkiauv.messenger.MessageObject> r6 = r1.pushMessages
            java.lang.Object r6 = r6.get(r0)
            im.bclpbkiauv.messenger.MessageObject r6 = (im.bclpbkiauv.messenger.MessageObject) r6
            long r8 = r6.getDialogId()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "dismissDate"
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r3.getInt(r10, r7)
            im.bclpbkiauv.tgnet.TLRPC$Message r11 = r6.messageOwner
            int r11 = r11.date
            if (r11 > r10) goto L_0x0063
            goto L_0x007e
        L_0x0063:
            java.lang.Object r11 = r5.get(r8)
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            if (r11 != 0) goto L_0x007b
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r11 = r12
            r5.put(r8, r11)
            java.lang.Long r12 = java.lang.Long.valueOf(r8)
            r4.add(r7, r12)
        L_0x007b:
            r11.add(r6)
        L_0x007e:
            int r0 = r0 + 1
            goto L_0x0032
        L_0x0081:
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            android.util.LongSparseArray r6 = r0.clone()
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            r0 = 0
            boolean r9 = im.bclpbkiauv.messenger.WearDataLayerListenerService.isWatchConnected()
            if (r9 == 0) goto L_0x00a0
            org.json.JSONArray r9 = new org.json.JSONArray
            r9.<init>()
            r0 = r9
            goto L_0x00a1
        L_0x00a0:
            r9 = r0
        L_0x00a1:
            int r0 = android.os.Build.VERSION.SDK_INT
            r10 = 27
            r11 = 1
            if (r0 <= r10) goto L_0x00b5
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 <= r10) goto L_0x00b3
            int r0 = r4.size()
            if (r0 <= r11) goto L_0x00b3
            goto L_0x00b5
        L_0x00b3:
            r0 = 0
            goto L_0x00b6
        L_0x00b5:
            r0 = 1
        L_0x00b6:
            r10 = r0
            r12 = 26
            if (r10 == 0) goto L_0x00c2
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r12) goto L_0x00c2
            checkOtherNotificationsChannel()
        L_0x00c2:
            im.bclpbkiauv.messenger.UserConfig r0 = r71.getUserConfig()
            int r13 = r0.getClientUserId()
            r0 = 0
            int r14 = r4.size()
            r15 = r0
        L_0x00d0:
            java.lang.String r12 = "id"
            if (r15 >= r14) goto L_0x0dc3
            java.lang.Object r0 = r4.get(r15)
            java.lang.Long r0 = (java.lang.Long) r0
            r17 = r12
            long r11 = r0.longValue()
            java.lang.Object r0 = r5.get(r11)
            r18 = r3
            r3 = r0
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            java.lang.Object r0 = r3.get(r7)
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            int r7 = r0.getId()
            r20 = r4
            int r4 = (int) r11
            r21 = r5
            r5 = 32
            r22 = r14
            r23 = r15
            long r14 = r11 >> r5
            int r15 = (int) r14
            java.lang.Object r0 = r6.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0117
            if (r4 == 0) goto L_0x0111
            java.lang.Integer r0 = java.lang.Integer.valueOf(r4)
            r14 = r0
            goto L_0x011b
        L_0x0111:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r15)
            r14 = r0
            goto L_0x011b
        L_0x0117:
            r6.remove(r11)
            r14 = r0
        L_0x011b:
            r0 = 0
            if (r9 == 0) goto L_0x0125
            org.json.JSONObject r24 = new org.json.JSONObject
            r24.<init>()
            r0 = r24
        L_0x0125:
            r5 = 0
            java.lang.Object r25 = r3.get(r5)
            r5 = r25
            im.bclpbkiauv.messenger.MessageObject r5 = (im.bclpbkiauv.messenger.MessageObject) r5
            r25 = r0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r5.messageOwner
            r26 = r6
            int r6 = r0.date
            r0 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            android.util.LongSparseArray r33 = new android.util.LongSparseArray
            r33.<init>()
            r34 = r33
            r35 = 0
            if (r4 == 0) goto L_0x02b8
            r33 = r0
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r4 == r0) goto L_0x0157
            r0 = 1
            goto L_0x0158
        L_0x0157:
            r0 = 0
        L_0x0158:
            if (r4 <= 0) goto L_0x0200
            r37 = r0
            im.bclpbkiauv.messenger.MessagesController r0 = r71.getMessagesController()
            r38 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            im.bclpbkiauv.tgnet.TLRPC$User r0 = r0.getUser(r9)
            if (r0 != 0) goto L_0x01ac
            boolean r9 = r5.isFcmMessage()
            if (r9 == 0) goto L_0x0178
            java.lang.String r9 = r5.localName
            r39 = r8
            goto L_0x01dd
        L_0x0178:
            boolean r9 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r9 == 0) goto L_0x019e
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r39 = r8
            java.lang.String r8 = "not found user to show dialog notification "
            r9.append(r8)
            r9.append(r4)
            java.lang.String r8 = r9.toString()
            im.bclpbkiauv.messenger.FileLog.w(r8)
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x019e:
            r39 = r8
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x01ac:
            r39 = r8
            java.lang.String r9 = im.bclpbkiauv.messenger.UserObject.getName(r0)
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            if (r8 == 0) goto L_0x01d9
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            if (r8 == 0) goto L_0x01d9
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            r27 = r9
            long r8 = r8.volume_id
            int r40 = (r8 > r35 ? 1 : (r8 == r35 ? 0 : -1))
            if (r40 == 0) goto L_0x01db
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            int r8 = r8.local_id
            if (r8 == 0) goto L_0x01db
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            r30 = r8
            r9 = r27
            goto L_0x01dd
        L_0x01d9:
            r27 = r9
        L_0x01db:
            r9 = r27
        L_0x01dd:
            if (r4 != r13) goto L_0x01f4
            r8 = 2131692013(0x7f0f09ed, float:1.9013114E38)
            r27 = r0
            java.lang.String r0 = "MessageScheduledReminderNotification"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r8)
            r40 = r5
            r5 = r25
            r8 = r27
            r0 = r37
            goto L_0x0355
        L_0x01f4:
            r27 = r0
            r40 = r5
            r5 = r25
            r8 = r27
            r0 = r37
            goto L_0x0355
        L_0x0200:
            r37 = r0
            r39 = r8
            r38 = r9
            im.bclpbkiauv.messenger.MessagesController r0 = r71.getMessagesController()
            int r8 = -r4
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            im.bclpbkiauv.tgnet.TLRPC$Chat r0 = r0.getChat(r8)
            if (r0 != 0) goto L_0x0261
            boolean r8 = r5.isFcmMessage()
            if (r8 == 0) goto L_0x0231
            boolean r29 = r5.isMegagroup()
            java.lang.String r9 = r5.localName
            boolean r8 = r5.localChannel
            r33 = r0
            r40 = r5
            r28 = r8
            r5 = r25
            r8 = r27
            r0 = r37
            goto L_0x0355
        L_0x0231:
            boolean r8 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x0255
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "not found chat to show dialog notification "
            r8.append(r9)
            r8.append(r4)
            java.lang.String r8 = r8.toString()
            im.bclpbkiauv.messenger.FileLog.w(r8)
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x0255:
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x0261:
            boolean r8 = r0.megagroup
            boolean r9 = im.bclpbkiauv.messenger.ChatObject.isChannel(r0)
            if (r9 == 0) goto L_0x026f
            boolean r9 = r0.megagroup
            if (r9 != 0) goto L_0x026f
            r9 = 1
            goto L_0x0270
        L_0x026f:
            r9 = 0
        L_0x0270:
            r28 = r9
            java.lang.String r9 = r0.title
            r40 = r5
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            if (r5 == 0) goto L_0x02a8
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            if (r5 == 0) goto L_0x02a8
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            r29 = r8
            r33 = r9
            long r8 = r5.volume_id
            int r5 = (r8 > r35 ? 1 : (r8 == r35 ? 0 : -1))
            if (r5 == 0) goto L_0x02ac
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            int r5 = r5.local_id
            if (r5 == 0) goto L_0x02ac
            im.bclpbkiauv.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            r30 = r5
            r5 = r25
            r8 = r27
            r9 = r33
            r33 = r0
            r0 = r37
            goto L_0x0355
        L_0x02a8:
            r29 = r8
            r33 = r9
        L_0x02ac:
            r5 = r25
            r8 = r27
            r9 = r33
            r33 = r0
            r0 = r37
            goto L_0x0355
        L_0x02b8:
            r33 = r0
            r40 = r5
            r39 = r8
            r38 = r9
            r0 = 0
            long r8 = globalSecretChatId
            int r5 = (r11 > r8 ? 1 : (r11 == r8 ? 0 : -1))
            if (r5 == 0) goto L_0x0347
            im.bclpbkiauv.messenger.MessagesController r5 = r71.getMessagesController()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r15)
            im.bclpbkiauv.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r8)
            if (r5 != 0) goto L_0x0305
            boolean r8 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x02f9
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "not found secret chat to show dialog notification "
            r8.append(r9)
            r8.append(r15)
            java.lang.String r8 = r8.toString()
            im.bclpbkiauv.messenger.FileLog.w(r8)
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x02f9:
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x0305:
            im.bclpbkiauv.messenger.MessagesController r8 = r71.getMessagesController()
            int r9 = r5.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            im.bclpbkiauv.tgnet.TLRPC$User r27 = r8.getUser(r9)
            if (r27 != 0) goto L_0x0347
            boolean r8 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x033b
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "not found secret chat user to show dialog notification "
            r8.append(r9)
            int r9 = r5.user_id
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            im.bclpbkiauv.messenger.FileLog.w(r8)
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x033b:
            r25 = r2
            r30 = r10
            r66 = r13
            r6 = r38
            r5 = r39
            goto L_0x0da9
        L_0x0347:
            r5 = 2131693750(0x7f0f10b6, float:1.9016637E38)
            java.lang.String r8 = "SecretChatName"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r5)
            r30 = 0
            r5 = 0
            r8 = r27
        L_0x0355:
            r19 = 0
            boolean r25 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r19)
            if (r25 != 0) goto L_0x036b
            boolean r25 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r25 == 0) goto L_0x0362
            goto L_0x036b
        L_0x0362:
            r25 = r2
            r27 = r8
            r2 = r9
            r8 = r30
            r9 = r0
            goto L_0x0383
        L_0x036b:
            r25 = r0
            r0 = 2131689824(0x7f0f0160, float:1.9008674E38)
            r27 = r9
            java.lang.String r9 = "AppName"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r0)
            r30 = 0
            r0 = 0
            r25 = r2
            r27 = r8
            r2 = r9
            r8 = r30
            r9 = r0
        L_0x0383:
            r30 = r10
            if (r8 == 0) goto L_0x03e7
            r10 = 1
            java.io.File r32 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r8, r10)
            int r0 = android.os.Build.VERSION.SDK_INT
            r10 = 28
            if (r0 >= r10) goto L_0x03e2
            im.bclpbkiauv.messenger.ImageLoader r0 = im.bclpbkiauv.messenger.ImageLoader.getInstance()
            r10 = 0
            r41 = r6
            java.lang.String r6 = "50_50"
            android.graphics.drawable.BitmapDrawable r6 = r0.getImageFromMemory(r8, r10, r6)
            if (r6 == 0) goto L_0x03a8
            android.graphics.Bitmap r31 = r6.getBitmap()
            r6 = r31
            goto L_0x03eb
        L_0x03a8:
            boolean r0 = r32.exists()     // Catch:{ all -> 0x03de }
            if (r0 == 0) goto L_0x03d9
            r0 = 1126170624(0x43200000, float:160.0)
            r10 = 1112014848(0x42480000, float:50.0)
            int r10 = im.bclpbkiauv.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x03de }
            float r10 = (float) r10     // Catch:{ all -> 0x03de }
            float r0 = r0 / r10
            android.graphics.BitmapFactory$Options r10 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03de }
            r10.<init>()     // Catch:{ all -> 0x03de }
            r42 = 1065353216(0x3f800000, float:1.0)
            int r42 = (r0 > r42 ? 1 : (r0 == r42 ? 0 : -1))
            if (r42 >= 0) goto L_0x03c7
            r42 = r6
            r6 = 1
            goto L_0x03ca
        L_0x03c7:
            r42 = r6
            int r6 = (int) r0
        L_0x03ca:
            r10.inSampleSize = r6     // Catch:{ all -> 0x03d7 }
            java.lang.String r6 = r32.getAbsolutePath()     // Catch:{ all -> 0x03d7 }
            android.graphics.Bitmap r6 = android.graphics.BitmapFactory.decodeFile(r6, r10)     // Catch:{ all -> 0x03d7 }
            r31 = r6
            goto L_0x03db
        L_0x03d7:
            r0 = move-exception
            goto L_0x03e4
        L_0x03d9:
            r42 = r6
        L_0x03db:
            r6 = r31
            goto L_0x03eb
        L_0x03de:
            r0 = move-exception
            r42 = r6
            goto L_0x03e4
        L_0x03e2:
            r41 = r6
        L_0x03e4:
            r6 = r31
            goto L_0x03eb
        L_0x03e7:
            r41 = r6
            r6 = r31
        L_0x03eb:
            r0 = 0
            java.lang.String r10 = "dialog_id"
            r31 = r8
            java.lang.String r8 = "max_id"
            r42 = r6
            java.lang.String r6 = "currentAccount"
            if (r28 == 0) goto L_0x0407
            if (r29 == 0) goto L_0x03fb
            goto L_0x0407
        L_0x03fb:
            r43 = r0
            r49 = r7
            r44 = r9
            r46 = r14
            r45 = r15
            goto L_0x049f
        L_0x0407:
            if (r9 == 0) goto L_0x0495
            boolean r43 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r43 != 0) goto L_0x0495
            if (r13 == r4) goto L_0x0495
            r43 = r0
            android.content.Intent r0 = new android.content.Intent
            r44 = r9
            android.content.Context r9 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r45 = r15
            java.lang.Class<im.bclpbkiauv.messenger.WearReplyReceiver> r15 = im.bclpbkiauv.messenger.WearReplyReceiver.class
            r0.<init>(r9, r15)
            r0.putExtra(r10, r11)
            r0.putExtra(r8, r7)
            int r9 = r1.currentAccount
            r0.putExtra(r6, r9)
            android.content.Context r9 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            int r15 = r14.intValue()
            r46 = r14
            r14 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r9 = android.app.PendingIntent.getBroadcast(r9, r15, r0, r14)
            androidx.core.app.RemoteInput$Builder r14 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r15 = "extra_voice_reply"
            r14.<init>(r15)
            r15 = 2131693448(0x7f0f0f88, float:1.9016025E38)
            r47 = r0
            java.lang.String r0 = "Reply"
            java.lang.String r0 = im.bclpbkiauv.messenger.LocaleController.getString(r0, r15)
            androidx.core.app.RemoteInput$Builder r0 = r14.setLabel(r0)
            androidx.core.app.RemoteInput r0 = r0.build()
            if (r4 >= 0) goto L_0x0465
            r15 = 1
            java.lang.Object[] r14 = new java.lang.Object[r15]
            r15 = 0
            r14[r15] = r2
            java.lang.String r15 = "ReplyToGroup"
            r49 = r7
            r7 = 2131693449(0x7f0f0f89, float:1.9016027E38)
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r15, r7, r14)
            goto L_0x0476
        L_0x0465:
            r49 = r7
            r7 = 2131693450(0x7f0f0f8a, float:1.9016029E38)
            r14 = 1
            java.lang.Object[] r15 = new java.lang.Object[r14]
            r14 = 0
            r15[r14] = r2
            java.lang.String r14 = "ReplyToUser"
            java.lang.String r7 = im.bclpbkiauv.messenger.LocaleController.formatString(r14, r7, r15)
        L_0x0476:
            androidx.core.app.NotificationCompat$Action$Builder r14 = new androidx.core.app.NotificationCompat$Action$Builder
            r15 = 2131231154(0x7f0801b2, float:1.807838E38)
            r14.<init>((int) r15, (java.lang.CharSequence) r7, (android.app.PendingIntent) r9)
            r15 = 1
            androidx.core.app.NotificationCompat$Action$Builder r14 = r14.setAllowGeneratedReplies(r15)
            androidx.core.app.NotificationCompat$Action$Builder r14 = r14.setSemanticAction(r15)
            androidx.core.app.NotificationCompat$Action$Builder r14 = r14.addRemoteInput(r0)
            r15 = 0
            androidx.core.app.NotificationCompat$Action$Builder r14 = r14.setShowsUserInterface(r15)
            androidx.core.app.NotificationCompat$Action r14 = r14.build()
            goto L_0x04a1
        L_0x0495:
            r43 = r0
            r49 = r7
            r44 = r9
            r46 = r14
            r45 = r15
        L_0x049f:
            r14 = r43
        L_0x04a1:
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.pushDialogs
            java.lang.Object r0 = r0.get(r11)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x04b2
            r7 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r7)
            r7 = r0
            goto L_0x04b3
        L_0x04b2:
            r7 = r0
        L_0x04b3:
            int r0 = r7.intValue()
            int r9 = r3.size()
            int r9 = java.lang.Math.max(r0, r9)
            r15 = 1
            if (r9 <= r15) goto L_0x04df
            int r0 = android.os.Build.VERSION.SDK_INT
            r15 = 28
            if (r0 < r15) goto L_0x04c9
            goto L_0x04df
        L_0x04c9:
            r15 = 2
            java.lang.Object[] r0 = new java.lang.Object[r15]
            r15 = 0
            r0[r15] = r2
            java.lang.Integer r15 = java.lang.Integer.valueOf(r9)
            r16 = 1
            r0[r16] = r15
            java.lang.String r15 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r15, r0)
            r15 = r0
            goto L_0x04e1
        L_0x04df:
            r0 = r2
            r15 = r0
        L_0x04e1:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r47 = r7
            java.lang.String r7 = ""
            r0.<init>((java.lang.CharSequence) r7)
            r48 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r50 = r9
            r9 = 28
            if (r0 < r9) goto L_0x04fc
            if (r4 >= 0) goto L_0x04f9
            if (r28 != 0) goto L_0x04f9
            goto L_0x04fc
        L_0x04f9:
            r9 = r48
            goto L_0x0501
        L_0x04fc:
            r9 = r48
            r9.setConversationTitle(r15)
        L_0x0501:
            int r0 = android.os.Build.VERSION.SDK_INT
            r48 = r15
            r15 = 28
            if (r0 < r15) goto L_0x0510
            if (r28 != 0) goto L_0x050e
            if (r4 >= 0) goto L_0x050e
            goto L_0x0510
        L_0x050e:
            r0 = 0
            goto L_0x0511
        L_0x0510:
            r0 = 1
        L_0x0511:
            r9.setGroupConversation(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r15 = r0
            r51 = r8
            r52 = r10
            r8 = 1
            java.lang.String[] r10 = new java.lang.String[r8]
            r53 = r14
            boolean[] r14 = new boolean[r8]
            r0 = 0
            r8 = 0
            r54 = 0
            if (r5 == 0) goto L_0x0537
            org.json.JSONArray r55 = new org.json.JSONArray
            r55.<init>()
            r54 = r55
            r55 = r8
            r8 = r54
            goto L_0x053b
        L_0x0537:
            r55 = r8
            r8 = r54
        L_0x053b:
            int r54 = r3.size()
            r16 = 1
            int r54 = r54 + -1
            r56 = r5
            r5 = r54
            r57 = r55
            r54 = r0
        L_0x054b:
            r58 = 1000(0x3e8, double:4.94E-321)
            if (r5 < 0) goto L_0x08e5
            java.lang.Object r0 = r3.get(r5)
            r55 = r3
            r3 = r0
            im.bclpbkiauv.messenger.MessageObject r3 = (im.bclpbkiauv.messenger.MessageObject) r3
            java.lang.String r0 = r1.getShortStringForMessage(r3, r10, r14)
            r61 = r5
            r60 = r6
            long r5 = (long) r13
            r62 = r8
            java.lang.String r8 = "NotificationMessageScheduledName"
            int r64 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r64 != 0) goto L_0x056d
            r5 = 0
            r10[r5] = r2
            goto L_0x057f
        L_0x056d:
            r5 = 0
            if (r4 >= 0) goto L_0x057f
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
            boolean r6 = r6.from_scheduled
            if (r6 == 0) goto L_0x057f
            r6 = 2131692388(0x7f0f0b64, float:1.9013875E38)
            java.lang.String r19 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r6)
            r10[r5] = r19
        L_0x057f:
            if (r0 != 0) goto L_0x05ca
            boolean r5 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r5 == 0) goto L_0x05ba
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "message text is null for "
            r5.append(r6)
            int r6 = r3.getId()
            r5.append(r6)
            java.lang.String r6 = " did = "
            r5.append(r6)
            r6 = r9
            long r8 = r3.getDialogId()
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            im.bclpbkiauv.messenger.FileLog.w(r5)
            r64 = r2
            r9 = r6
            r8 = r7
            r65 = r10
            r66 = r13
            r63 = r14
            r67 = r15
            r5 = r62
            goto L_0x08d0
        L_0x05ba:
            r6 = r9
            r64 = r2
            r8 = r7
            r65 = r10
            r66 = r13
            r63 = r14
            r67 = r15
            r5 = r62
            goto L_0x08d0
        L_0x05ca:
            r6 = r9
            int r5 = r15.length()
            if (r5 <= 0) goto L_0x05d6
            java.lang.String r5 = "\n\n"
            r15.append(r5)
        L_0x05d6:
            r9 = r6
            long r5 = (long) r13
            r64 = r2
            java.lang.String r2 = "%1$s: %2$s"
            int r65 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r65 == 0) goto L_0x0601
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r3.messageOwner
            boolean r5 = r5.from_scheduled
            if (r5 == 0) goto L_0x0601
            if (r4 <= 0) goto L_0x0601
            r5 = 2
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r5 = 2131692388(0x7f0f0b64, float:1.9013875E38)
            java.lang.String r5 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r5)
            r8 = 0
            r6[r8] = r5
            r5 = 1
            r6[r5] = r0
            java.lang.String r0 = java.lang.String.format(r2, r6)
            r15.append(r0)
            r2 = r0
            goto L_0x061c
        L_0x0601:
            r5 = 0
            r6 = r10[r5]
            if (r6 == 0) goto L_0x0618
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r6 = r10[r5]
            r8[r5] = r6
            r5 = 1
            r8[r5] = r0
            java.lang.String r2 = java.lang.String.format(r2, r8)
            r15.append(r2)
            goto L_0x061b
        L_0x0618:
            r15.append(r0)
        L_0x061b:
            r2 = r0
        L_0x061c:
            if (r4 <= 0) goto L_0x0620
            long r5 = (long) r4
            goto L_0x062e
        L_0x0620:
            if (r28 == 0) goto L_0x0625
            int r0 = -r4
            long r5 = (long) r0
            goto L_0x062e
        L_0x0625:
            if (r4 >= 0) goto L_0x062d
            int r0 = r3.getFromId()
            long r5 = (long) r0
            goto L_0x062e
        L_0x062d:
            r5 = r11
        L_0x062e:
            r8 = r34
            java.lang.Object r0 = r8.get(r5)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            if (r0 != 0) goto L_0x06e7
            r34 = r0
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r19 = 0
            r63 = r10[r19]
            if (r63 != 0) goto L_0x0649
            r65 = r10
            r10 = r7
            goto L_0x064f
        L_0x0649:
            r63 = r10[r19]
            r65 = r10
            r10 = r63
        L_0x064f:
            androidx.core.app.Person$Builder r0 = r0.setName(r10)
            boolean r10 = r14[r19]
            if (r10 == 0) goto L_0x06db
            if (r4 == 0) goto L_0x06db
            int r10 = android.os.Build.VERSION.SDK_INT
            r63 = r14
            r14 = 28
            if (r10 < r14) goto L_0x06d8
            r10 = 0
            if (r4 > 0) goto L_0x06ce
            if (r28 == 0) goto L_0x066c
            r66 = r10
            r67 = r15
            goto L_0x06d2
        L_0x066c:
            if (r4 >= 0) goto L_0x06c7
            int r14 = r3.getFromId()
            r66 = r10
            im.bclpbkiauv.messenger.MessagesController r10 = r71.getMessagesController()
            r67 = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r10.getUser(r15)
            if (r10 != 0) goto L_0x069c
            im.bclpbkiauv.messenger.MessagesStorage r15 = r71.getMessagesStorage()
            im.bclpbkiauv.tgnet.TLRPC$User r10 = r15.getUserSync(r14)
            if (r10 == 0) goto L_0x0699
            im.bclpbkiauv.messenger.MessagesController r15 = r71.getMessagesController()
            r68 = r14
            r14 = 1
            r15.putUser(r10, r14)
            goto L_0x069e
        L_0x0699:
            r68 = r14
            goto L_0x069e
        L_0x069c:
            r68 = r14
        L_0x069e:
            if (r10 == 0) goto L_0x06cb
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r14 = r10.photo
            if (r14 == 0) goto L_0x06cb
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r14 = r10.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            if (r14 == 0) goto L_0x06cb
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r14 = r10.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            long r14 = r14.volume_id
            int r69 = (r14 > r35 ? 1 : (r14 == r35 ? 0 : -1))
            if (r69 == 0) goto L_0x06cb
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r14 = r10.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            int r14 = r14.local_id
            if (r14 == 0) goto L_0x06cb
            im.bclpbkiauv.tgnet.TLRPC$UserProfilePhoto r14 = r10.photo
            im.bclpbkiauv.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            r15 = 1
            java.io.File r14 = im.bclpbkiauv.messenger.FileLoader.getPathToAttach(r14, r15)
            r10 = r14
            goto L_0x06d4
        L_0x06c7:
            r66 = r10
            r67 = r15
        L_0x06cb:
            r10 = r66
            goto L_0x06d4
        L_0x06ce:
            r66 = r10
            r67 = r15
        L_0x06d2:
            r10 = r32
        L_0x06d4:
            r1.loadRoundAvatar(r10, r0)
            goto L_0x06df
        L_0x06d8:
            r67 = r15
            goto L_0x06df
        L_0x06db:
            r63 = r14
            r67 = r15
        L_0x06df:
            androidx.core.app.Person r10 = r0.build()
            r8.put(r5, r10)
            goto L_0x06f1
        L_0x06e7:
            r34 = r0
            r65 = r10
            r63 = r14
            r67 = r15
            r10 = r34
        L_0x06f1:
            if (r4 == 0) goto L_0x0852
            int r0 = android.os.Build.VERSION.SDK_INT
            java.lang.String r14 = "im.bclpbkiauv.messenger.provider"
            r15 = 28
            if (r0 < r15) goto L_0x0803
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.String r15 = "activity"
            java.lang.Object r0 = r0.getSystemService(r15)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0803
            boolean r0 = r3.isSecretMedia()
            if (r0 != 0) goto L_0x07f1
            int r0 = r3.type
            r15 = 1
            if (r0 == r15) goto L_0x0726
            boolean r0 = r3.isSticker()
            if (r0 == 0) goto L_0x071d
            goto L_0x0726
        L_0x071d:
            r68 = r5
            r34 = r8
            r66 = r13
            r8 = r7
            goto L_0x07f8
        L_0x0726:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.io.File r0 = im.bclpbkiauv.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r15 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            r68 = r5
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r3.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            long r5 = r5 * r58
            r15.<init>((java.lang.CharSequence) r2, (long) r5, (androidx.core.app.Person) r10)
            r5 = r15
            boolean r6 = r3.isSticker()
            if (r6 == 0) goto L_0x0744
            java.lang.String r6 = "image/webp"
            goto L_0x0746
        L_0x0744:
            java.lang.String r6 = "image/jpeg"
        L_0x0746:
            boolean r15 = r0.exists()
            if (r15 == 0) goto L_0x0757
            android.content.Context r15 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            android.net.Uri r15 = androidx.core.content.FileProvider.getUriForFile(r15, r14, r0)
            r34 = r8
            r66 = r13
            goto L_0x07af
        L_0x0757:
            im.bclpbkiauv.messenger.FileLoader r15 = r71.getFileLoader()
            r34 = r8
            java.lang.String r8 = r0.getName()
            boolean r8 = r15.isLoadingFile(r8)
            if (r8 == 0) goto L_0x07ac
            android.net.Uri$Builder r8 = new android.net.Uri$Builder
            r8.<init>()
            java.lang.String r15 = "content"
            android.net.Uri$Builder r8 = r8.scheme(r15)
            java.lang.String r15 = "im.bclpbkiauv.messenger.notification_image_provider"
            android.net.Uri$Builder r8 = r8.authority(r15)
            java.lang.String r15 = "msg_media_raw"
            android.net.Uri$Builder r8 = r8.appendPath(r15)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r66 = r13
            int r13 = r1.currentAccount
            r15.append(r13)
            r15.append(r7)
            java.lang.String r13 = r15.toString()
            android.net.Uri$Builder r8 = r8.appendPath(r13)
            java.lang.String r13 = r0.getName()
            android.net.Uri$Builder r8 = r8.appendPath(r13)
            java.lang.String r13 = r0.getAbsolutePath()
            java.lang.String r15 = "final_path"
            android.net.Uri$Builder r8 = r8.appendQueryParameter(r15, r13)
            android.net.Uri r15 = r8.build()
            goto L_0x07af
        L_0x07ac:
            r66 = r13
            r15 = 0
        L_0x07af:
            if (r15 == 0) goto L_0x07e2
            r5.setData(r6, r15)
            r9.addMessage(r5)
            android.content.Context r8 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.String r13 = "com.android.systemui"
            r70 = r5
            r5 = 1
            r8.grantUriPermission(r13, r15, r5)
            im.bclpbkiauv.messenger.-$$Lambda$NotificationsController$7uto4QVZA9L-UIMEpT7ZDx4Mljw r5 = new im.bclpbkiauv.messenger.-$$Lambda$NotificationsController$7uto4QVZA9L-UIMEpT7ZDx4Mljw
            r5.<init>(r15)
            r13 = r6
            r8 = r7
            r6 = 20000(0x4e20, double:9.8813E-320)
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(r5, r6)
            java.lang.CharSequence r5 = r3.caption
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x07f0
            java.lang.CharSequence r5 = r3.caption
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner
            int r6 = r6.date
            long r6 = (long) r6
            long r6 = r6 * r58
            r9.addMessage((java.lang.CharSequence) r5, (long) r6, (androidx.core.app.Person) r10)
            goto L_0x07f0
        L_0x07e2:
            r70 = r5
            r13 = r6
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r5 = r3.messageOwner
            int r5 = r5.date
            long r5 = (long) r5
            long r5 = r5 * r58
            r9.addMessage((java.lang.CharSequence) r2, (long) r5, (androidx.core.app.Person) r10)
        L_0x07f0:
            goto L_0x0814
        L_0x07f1:
            r68 = r5
            r34 = r8
            r66 = r13
            r8 = r7
        L_0x07f8:
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            long r5 = r5 * r58
            r9.addMessage((java.lang.CharSequence) r2, (long) r5, (androidx.core.app.Person) r10)
            goto L_0x0814
        L_0x0803:
            r68 = r5
            r34 = r8
            r66 = r13
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            long r5 = r5 * r58
            r9.addMessage((java.lang.CharSequence) r2, (long) r5, (androidx.core.app.Person) r10)
        L_0x0814:
            boolean r0 = r3.isVoice()
            if (r0 == 0) goto L_0x0863
            java.util.List r5 = r9.getMessages()
            boolean r0 = r5.isEmpty()
            if (r0 != 0) goto L_0x0851
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.io.File r6 = im.bclpbkiauv.messenger.FileLoader.getPathToMessage(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 24
            if (r0 < r7) goto L_0x083a
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0837 }
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r14, r6)     // Catch:{ Exception -> 0x0837 }
            goto L_0x0839
        L_0x0837:
            r0 = move-exception
            r0 = 0
        L_0x0839:
            goto L_0x083e
        L_0x083a:
            android.net.Uri r0 = android.net.Uri.fromFile(r6)
        L_0x083e:
            if (r0 == 0) goto L_0x0851
            int r7 = r5.size()
            r13 = 1
            int r7 = r7 - r13
            java.lang.Object r7 = r5.get(r7)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r7 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r7
            java.lang.String r13 = "audio/ogg"
            r7.setData(r13, r0)
        L_0x0851:
            goto L_0x0863
        L_0x0852:
            r68 = r5
            r34 = r8
            r66 = r13
            r8 = r7
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r5 = (long) r0
            long r5 = r5 * r58
            r9.addMessage((java.lang.CharSequence) r2, (long) r5, (androidx.core.app.Person) r10)
        L_0x0863:
            if (r62 == 0) goto L_0x08b3
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08af }
            r0.<init>()     // Catch:{ JSONException -> 0x08af }
            java.lang.String r5 = "text"
            r0.put(r5, r2)     // Catch:{ JSONException -> 0x08af }
            java.lang.String r5 = "date"
            im.bclpbkiauv.tgnet.TLRPC$Message r6 = r3.messageOwner     // Catch:{ JSONException -> 0x08af }
            int r6 = r6.date     // Catch:{ JSONException -> 0x08af }
            r0.put(r5, r6)     // Catch:{ JSONException -> 0x08af }
            boolean r5 = r3.isFromUser()     // Catch:{ JSONException -> 0x08af }
            if (r5 == 0) goto L_0x08a6
            if (r4 >= 0) goto L_0x08a6
            im.bclpbkiauv.messenger.MessagesController r5 = r71.getMessagesController()     // Catch:{ JSONException -> 0x08a2 }
            int r6 = r3.getFromId()     // Catch:{ JSONException -> 0x08a2 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ JSONException -> 0x08a2 }
            im.bclpbkiauv.tgnet.TLRPC$User r5 = r5.getUser(r6)     // Catch:{ JSONException -> 0x08a2 }
            if (r5 == 0) goto L_0x08a6
            java.lang.String r6 = "fname"
            java.lang.String r7 = r5.first_name     // Catch:{ JSONException -> 0x08a2 }
            r0.put(r6, r7)     // Catch:{ JSONException -> 0x08a2 }
            java.lang.String r6 = "lname"
            java.lang.String r7 = r5.last_name     // Catch:{ JSONException -> 0x08a2 }
            r0.put(r6, r7)     // Catch:{ JSONException -> 0x08a2 }
            goto L_0x08a6
        L_0x08a2:
            r0 = move-exception
            r5 = r62
            goto L_0x08b5
        L_0x08a6:
            r5 = r62
            r5.put(r0)     // Catch:{ JSONException -> 0x08ad }
            goto L_0x08b5
        L_0x08ad:
            r0 = move-exception
            goto L_0x08b5
        L_0x08af:
            r0 = move-exception
            r5 = r62
            goto L_0x08b5
        L_0x08b3:
            r5 = r62
        L_0x08b5:
            r6 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x08d0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x08d0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r3.messageOwner
            im.bclpbkiauv.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r6 = r3.getId()
            r54 = r0
            r57 = r6
        L_0x08d0:
            int r0 = r61 + -1
            r7 = r8
            r3 = r55
            r6 = r60
            r14 = r63
            r2 = r64
            r10 = r65
            r13 = r66
            r15 = r67
            r8 = r5
            r5 = r0
            goto L_0x054b
        L_0x08e5:
            r64 = r2
            r55 = r3
            r61 = r5
            r60 = r6
            r5 = r8
            r65 = r10
            r66 = r13
            r63 = r14
            r67 = r15
            r8 = r7
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r2 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.ui.LaunchActivity> r3 = im.bclpbkiauv.ui.LaunchActivity.class
            r0.<init>(r2, r3)
            r2 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "com.tmessages.openchat"
            r0.append(r3)
            double r6 = java.lang.Math.random()
            r0.append(r6)
            r3 = 2147483647(0x7fffffff, float:NaN)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            r2.setAction(r0)
            java.lang.String r0 = "android.intent.category.LAUNCHER"
            r2.addCategory(r0)
            if (r4 == 0) goto L_0x093a
            if (r4 <= 0) goto L_0x0931
            java.lang.String r0 = "userId"
            r2.putExtra(r0, r4)
            r3 = r45
            goto L_0x0941
        L_0x0931:
            int r0 = -r4
            java.lang.String r3 = "chatId"
            r2.putExtra(r3, r0)
            r3 = r45
            goto L_0x0941
        L_0x093a:
            java.lang.String r0 = "encId"
            r3 = r45
            r2.putExtra(r0, r3)
        L_0x0941:
            int r0 = r1.currentAccount
            r6 = r60
            r2.putExtra(r6, r0)
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r7 = 1073741824(0x40000000, float:2.0)
            r10 = 0
            android.app.PendingIntent r7 = android.app.PendingIntent.getActivity(r0, r10, r2, r7)
            androidx.core.app.NotificationCompat$WearableExtender r0 = new androidx.core.app.NotificationCompat$WearableExtender
            r0.<init>()
            r10 = r0
            if (r53 == 0) goto L_0x095f
            r14 = r53
            r10.addAction(r14)
            goto L_0x0961
        L_0x095f:
            r14 = r53
        L_0x0961:
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r13 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.AutoMessageHeardReceiver> r15 = im.bclpbkiauv.messenger.AutoMessageHeardReceiver.class
            r0.<init>(r13, r15)
            r13 = r0
            r15 = 32
            r13.addFlags(r15)
            java.lang.String r0 = "im.bclpbkiauv.messenger.ACTION_MESSAGE_HEARD"
            r13.setAction(r0)
            r15 = r52
            r13.putExtra(r15, r11)
            r24 = r2
            r15 = r49
            r2 = r51
            r13.putExtra(r2, r15)
            int r0 = r1.currentAccount
            r13.putExtra(r6, r0)
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r62 = r5
            int r5 = r46.intValue()
            r2 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r5 = android.app.PendingIntent.getBroadcast(r0, r5, r13, r2)
            androidx.core.app.NotificationCompat$Action$Builder r0 = new androidx.core.app.NotificationCompat$Action$Builder
            r2 = 2131691908(0x7f0f0984, float:1.9012901E38)
            r36 = r13
            java.lang.String r13 = "MarkAsRead"
            java.lang.String r2 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r2)
            r13 = 2131231268(0x7f080224, float:1.8078612E38)
            r0.<init>((int) r13, (java.lang.CharSequence) r2, (android.app.PendingIntent) r5)
            r2 = 2
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setSemanticAction(r2)
            r2 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r2)
            androidx.core.app.NotificationCompat$Action r2 = r0.build()
            java.lang.String r0 = "_"
            if (r4 == 0) goto L_0x09f4
            if (r4 <= 0) goto L_0x09d8
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r35 = r5
            java.lang.String r5 = "tguser"
            r13.append(r5)
            r13.append(r4)
            r13.append(r0)
            r13.append(r15)
            java.lang.String r5 = r13.toString()
            goto L_0x0a16
        L_0x09d8:
            r35 = r5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = "tgchat"
            r5.append(r13)
            int r13 = -r4
            r5.append(r13)
            r5.append(r0)
            r5.append(r15)
            java.lang.String r5 = r5.toString()
            goto L_0x0a16
        L_0x09f4:
            r35 = r5
            long r52 = globalSecretChatId
            int r5 = (r11 > r52 ? 1 : (r11 == r52 ? 0 : -1))
            if (r5 == 0) goto L_0x0a15
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r13 = "tgenc"
            r5.append(r13)
            r5.append(r3)
            r5.append(r0)
            r5.append(r15)
            java.lang.String r5 = r5.toString()
            goto L_0x0a16
        L_0x0a15:
            r5 = 0
        L_0x0a16:
            if (r5 == 0) goto L_0x0a3f
            r10.setDismissalId(r5)
            androidx.core.app.NotificationCompat$WearableExtender r13 = new androidx.core.app.NotificationCompat$WearableExtender
            r13.<init>()
            r45 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r37 = r0
            java.lang.String r0 = "summary_"
            r3.append(r0)
            r3.append(r5)
            java.lang.String r0 = r3.toString()
            r13.setDismissalId(r0)
            r3 = r72
            r3.extend(r13)
            goto L_0x0a45
        L_0x0a3f:
            r37 = r0
            r45 = r3
            r3 = r72
        L_0x0a45:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r13 = "tgaccount"
            r0.append(r13)
            r13 = r66
            r0.append(r13)
            java.lang.String r0 = r0.toString()
            r10.setBridgeTag(r0)
            r43 = r5
            r3 = r55
            r5 = 0
            java.lang.Object r0 = r3.get(r5)
            im.bclpbkiauv.messenger.MessageObject r0 = (im.bclpbkiauv.messenger.MessageObject) r0
            im.bclpbkiauv.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.date
            r49 = r4
            long r4 = (long) r0
            long r4 = r4 * r58
            androidx.core.app.NotificationCompat$Builder r0 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r13 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r0.<init>(r13)
            r13 = r64
            androidx.core.app.NotificationCompat$Builder r0 = r0.setContentTitle(r13)
            r52 = r15
            r15 = 2131558767(0x7f0d016f, float:1.874286E38)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSmallIcon(r15)
            java.lang.String r15 = r67.toString()
            androidx.core.app.NotificationCompat$Builder r0 = r0.setContentText(r15)
            r15 = 1
            androidx.core.app.NotificationCompat$Builder r0 = r0.setAutoCancel(r15)
            int r15 = r3.size()
            androidx.core.app.NotificationCompat$Builder r0 = r0.setNumber(r15)
            r15 = -15618822(0xffffffffff11acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setColor(r15)
            r15 = 0
            androidx.core.app.NotificationCompat$Builder r0 = r0.setGroupSummary(r15)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setWhen(r4)
            r15 = 1
            androidx.core.app.NotificationCompat$Builder r0 = r0.setShowWhen(r15)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r3 = "sdid_"
            r15.append(r3)
            r15.append(r11)
            java.lang.String r3 = r15.toString()
            androidx.core.app.NotificationCompat$Builder r0 = r0.setShortcutId(r3)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setStyle(r9)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setContentIntent(r7)
            androidx.core.app.NotificationCompat$Builder r0 = r0.extend(r10)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r8)
            r58 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r15 = r7
            long r7 = r58 - r4
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSortKey(r3)
            java.lang.String r3 = "msg"
            androidx.core.app.NotificationCompat$Builder r3 = r0.setCategory(r3)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r7 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            java.lang.Class<im.bclpbkiauv.messenger.NotificationDismissReceiver> r8 = im.bclpbkiauv.messenger.NotificationDismissReceiver.class
            r0.<init>(r7, r8)
            r7 = r0
            java.lang.String r0 = "messageDate"
            r8 = r41
            r7.putExtra(r0, r8)
            java.lang.String r0 = "dialogId"
            r7.putExtra(r0, r11)
            int r0 = r1.currentAccount
            r7.putExtra(r6, r0)
            android.content.Context r0 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r58 = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 1
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r0, r5, r7, r4)
            r3.setDeleteIntent(r0)
            if (r30 == 0) goto L_0x0b25
            java.lang.String r0 = r1.notificationGroup
            r3.setGroup(r0)
            r3.setGroupAlertBehavior(r5)
        L_0x0b25:
            if (r14 == 0) goto L_0x0b2a
            r3.addAction(r14)
        L_0x0b2a:
            r3.addAction(r2)
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.pushDialogs
            int r0 = r0.size()
            r4 = 1
            if (r0 != r4) goto L_0x0b42
            boolean r0 = android.text.TextUtils.isEmpty(r74)
            if (r0 != 0) goto L_0x0b42
            r4 = r74
            r3.setSubText(r4)
            goto L_0x0b44
        L_0x0b42:
            r4 = r74
        L_0x0b44:
            if (r49 != 0) goto L_0x0b4b
            r5 = 1
            r3.setLocalOnly(r5)
            goto L_0x0b4c
        L_0x0b4b:
            r5 = 1
        L_0x0b4c:
            if (r42 == 0) goto L_0x0b54
            r5 = r42
            r3.setLargeIcon(r5)
            goto L_0x0b56
        L_0x0b54:
            r5 = r42
        L_0x0b56:
            r19 = 0
            boolean r0 = im.bclpbkiauv.messenger.AndroidUtilities.needShowPasscode(r19)
            if (r0 != 0) goto L_0x0c24
            boolean r0 = im.bclpbkiauv.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0c24
            if (r54 == 0) goto L_0x0c24
            r0 = 0
            r41 = r0
            int r0 = r54.size()
            r42 = r2
            r2 = r41
        L_0x0b6f:
            if (r2 >= r0) goto L_0x0c18
            r4 = r54
            java.lang.Object r41 = r4.get(r2)
            r53 = r0
            r0 = r41
            im.bclpbkiauv.tgnet.TLRPC$TL_keyboardButtonRow r0 = (im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonRow) r0
            r41 = 0
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r4 = r0.buttons
            int r4 = r4.size()
            r60 = r5
            r5 = r41
        L_0x0b89:
            if (r5 >= r4) goto L_0x0bfb
            r41 = r4
            java.util.ArrayList<im.bclpbkiauv.tgnet.TLRPC$KeyboardButton> r4 = r0.buttons
            java.lang.Object r4 = r4.get(r5)
            im.bclpbkiauv.tgnet.TLRPC$KeyboardButton r4 = (im.bclpbkiauv.tgnet.TLRPC.KeyboardButton) r4
            r61 = r0
            boolean r0 = r4 instanceof im.bclpbkiauv.tgnet.TLRPC.TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0bdf
            android.content.Intent r0 = new android.content.Intent
            r64 = r7
            android.content.Context r7 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r68 = r9
            java.lang.Class<im.bclpbkiauv.messenger.NotificationCallbackReceiver> r9 = im.bclpbkiauv.messenger.NotificationCallbackReceiver.class
            r0.<init>(r7, r9)
            int r7 = r1.currentAccount
            r0.putExtra(r6, r7)
            java.lang.String r7 = "did"
            r0.putExtra(r7, r11)
            byte[] r7 = r4.data
            if (r7 == 0) goto L_0x0bbd
            byte[] r7 = r4.data
            java.lang.String r9 = "data"
            r0.putExtra(r9, r7)
        L_0x0bbd:
            java.lang.String r7 = "mid"
            r9 = r57
            r0.putExtra(r7, r9)
            java.lang.String r7 = r4.text
            r57 = r4
            android.content.Context r4 = im.bclpbkiauv.messenger.ApplicationLoader.applicationContext
            r69 = r6
            int r6 = r1.lastButtonId
            r70 = r9
            int r9 = r6 + 1
            r1.lastButtonId = r9
            r9 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r6, r0, r9)
            r6 = 0
            r3.addAction(r6, r7, r4)
            goto L_0x0bec
        L_0x0bdf:
            r69 = r6
            r64 = r7
            r68 = r9
            r70 = r57
            r6 = 0
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r57 = r4
        L_0x0bec:
            int r5 = r5 + 1
            r4 = r41
            r0 = r61
            r7 = r64
            r9 = r68
            r6 = r69
            r57 = r70
            goto L_0x0b89
        L_0x0bfb:
            r61 = r0
            r41 = r4
            r69 = r6
            r64 = r7
            r68 = r9
            r70 = r57
            r6 = 0
            r9 = 134217728(0x8000000, float:3.85186E-34)
            int r2 = r2 + 1
            r4 = r74
            r0 = r53
            r5 = r60
            r9 = r68
            r6 = r69
            goto L_0x0b6f
        L_0x0c18:
            r53 = r0
            r60 = r5
            r64 = r7
            r68 = r9
            r70 = r57
            r6 = 0
            goto L_0x0c2f
        L_0x0c24:
            r42 = r2
            r60 = r5
            r64 = r7
            r68 = r9
            r70 = r57
            r6 = 0
        L_0x0c2f:
            if (r33 != 0) goto L_0x0c59
            if (r27 == 0) goto L_0x0c59
            r2 = r27
            java.lang.String r0 = r2.phone
            if (r0 == 0) goto L_0x0c5b
            java.lang.String r0 = r2.phone
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0c5b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "tel:+"
            r0.append(r4)
            java.lang.String r4 = r2.phone
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            r3.addPerson(r0)
            goto L_0x0c5b
        L_0x0c59:
            r2 = r27
        L_0x0c5b:
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 26
            if (r0 < r4) goto L_0x0c70
            if (r30 == 0) goto L_0x0c69
            java.lang.String r0 = OTHER_NOTIFICATIONS_CHANNEL
            r3.setChannelId(r0)
            goto L_0x0c70
        L_0x0c69:
            java.lang.String r0 = r25.getChannelId()
            r3.setChannelId(r0)
        L_0x0c70:
            im.bclpbkiauv.messenger.NotificationsController$1NotificationHolder r0 = new im.bclpbkiauv.messenger.NotificationsController$1NotificationHolder
            int r5 = r46.intValue()
            android.app.Notification r7 = r3.build()
            r0.<init>(r5, r7)
            r5 = r39
            r5.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r1.wearNotificationsIds
            r7 = r46
            r0.put(r11, r7)
            if (r49 == 0) goto L_0x0d97
            if (r56 == 0) goto L_0x0d84
            java.lang.String r0 = "reply"
            r9 = r44
            r4 = r56
            r4.put(r0, r9)     // Catch:{ JSONException -> 0x0d60 }
            java.lang.String r0 = "name"
            r4.put(r0, r13)     // Catch:{ JSONException -> 0x0d60 }
            r27 = r2
            r2 = r51
            r6 = r52
            r4.put(r2, r6)     // Catch:{ JSONException -> 0x0d50 }
            java.lang.String r0 = "max_date"
            r4.put(r0, r8)     // Catch:{ JSONException -> 0x0d50 }
            int r0 = java.lang.Math.abs(r49)     // Catch:{ JSONException -> 0x0d50 }
            r2 = r17
            r4.put(r2, r0)     // Catch:{ JSONException -> 0x0d50 }
            if (r31 == 0) goto L_0x0d07
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0cf6 }
            r2.<init>()     // Catch:{ JSONException -> 0x0cf6 }
            r17 = r3
            r52 = r6
            r3 = r31
            int r6 = r3.dc_id     // Catch:{ JSONException -> 0x0ceb }
            r2.append(r6)     // Catch:{ JSONException -> 0x0ceb }
            r6 = r37
            r2.append(r6)     // Catch:{ JSONException -> 0x0ceb }
            r46 = r7
            r41 = r8
            long r7 = r3.volume_id     // Catch:{ JSONException -> 0x0ce4 }
            r2.append(r7)     // Catch:{ JSONException -> 0x0ce4 }
            r2.append(r6)     // Catch:{ JSONException -> 0x0ce4 }
            long r6 = r3.secret     // Catch:{ JSONException -> 0x0ce4 }
            r2.append(r6)     // Catch:{ JSONException -> 0x0ce4 }
            java.lang.String r2 = r2.toString()     // Catch:{ JSONException -> 0x0ce4 }
            r4.put(r0, r2)     // Catch:{ JSONException -> 0x0ce4 }
            goto L_0x0d11
        L_0x0ce4:
            r0 = move-exception
            r6 = r38
            r2 = r62
            goto L_0x0da9
        L_0x0ceb:
            r0 = move-exception
            r46 = r7
            r41 = r8
            r6 = r38
            r2 = r62
            goto L_0x0da9
        L_0x0cf6:
            r0 = move-exception
            r17 = r3
            r52 = r6
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r2 = r62
            goto L_0x0da9
        L_0x0d07:
            r17 = r3
            r52 = r6
            r46 = r7
            r41 = r8
            r3 = r31
        L_0x0d11:
            if (r62 == 0) goto L_0x0d27
            java.lang.String r0 = "msgs"
            r2 = r62
            r4.put(r0, r2)     // Catch:{ JSONException -> 0x0d1b }
            goto L_0x0d29
        L_0x0d1b:
            r0 = move-exception
            r6 = r38
            goto L_0x0da9
        L_0x0d20:
            r0 = move-exception
            r2 = r62
            r6 = r38
            goto L_0x0da9
        L_0x0d27:
            r2 = r62
        L_0x0d29:
            java.lang.String r0 = "type"
            if (r49 <= 0) goto L_0x0d35
            java.lang.String r6 = "user"
            r4.put(r0, r6)     // Catch:{ JSONException -> 0x0d1b }
            goto L_0x0d47
        L_0x0d35:
            if (r49 >= 0) goto L_0x0d47
            if (r28 != 0) goto L_0x0d42
            if (r29 == 0) goto L_0x0d3c
            goto L_0x0d42
        L_0x0d3c:
            java.lang.String r6 = "group"
            r4.put(r0, r6)     // Catch:{ JSONException -> 0x0d1b }
            goto L_0x0d47
        L_0x0d42:
            java.lang.String r6 = "channel"
            r4.put(r0, r6)     // Catch:{ JSONException -> 0x0d1b }
        L_0x0d47:
            r6 = r38
            r6.put(r4)     // Catch:{ JSONException -> 0x0d4d }
            goto L_0x0d96
        L_0x0d4d:
            r0 = move-exception
            goto L_0x0da9
        L_0x0d50:
            r0 = move-exception
            r17 = r3
            r52 = r6
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r2 = r62
            goto L_0x0da9
        L_0x0d60:
            r0 = move-exception
            r27 = r2
            r17 = r3
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r2 = r62
            goto L_0x0da9
        L_0x0d70:
            r0 = move-exception
            r27 = r2
            r17 = r3
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r9 = r44
            r4 = r56
            r2 = r62
            goto L_0x0da9
        L_0x0d84:
            r27 = r2
            r17 = r3
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r9 = r44
            r4 = r56
            r2 = r62
        L_0x0d96:
            goto L_0x0da9
        L_0x0d97:
            r27 = r2
            r17 = r3
            r46 = r7
            r41 = r8
            r3 = r31
            r6 = r38
            r9 = r44
            r4 = r56
            r2 = r62
        L_0x0da9:
            int r15 = r23 + 1
            r8 = r5
            r9 = r6
            r3 = r18
            r4 = r20
            r5 = r21
            r14 = r22
            r2 = r25
            r6 = r26
            r10 = r30
            r13 = r66
            r7 = 0
            r11 = 1
            r12 = 26
            goto L_0x00d0
        L_0x0dc3:
            r25 = r2
            r18 = r3
            r20 = r4
            r21 = r5
            r26 = r6
            r5 = r8
            r6 = r9
            r30 = r10
            r2 = r12
            r66 = r13
            r22 = r14
            r23 = r15
            if (r30 == 0) goto L_0x0dfe
            boolean r0 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0df4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "show summary with id "
            r0.append(r3)
            int r3 = r1.notificationId
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            im.bclpbkiauv.messenger.FileLog.d(r0)
        L_0x0df4:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r3 = r1.notificationId
            r4 = r25
            r0.notify(r3, r4)
            goto L_0x0e07
        L_0x0dfe:
            r4 = r25
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r3 = r1.notificationId
            r0.cancel(r3)
        L_0x0e07:
            r0 = 0
            int r3 = r5.size()
        L_0x0e0c:
            if (r0 >= r3) goto L_0x0e1a
            java.lang.Object r7 = r5.get(r0)
            im.bclpbkiauv.messenger.NotificationsController$1NotificationHolder r7 = (im.bclpbkiauv.messenger.NotificationsController.AnonymousClass1NotificationHolder) r7
            r7.call()
            int r0 = r0 + 1
            goto L_0x0e0c
        L_0x0e1a:
            r0 = 0
        L_0x0e1b:
            int r3 = r26.size()
            if (r0 >= r3) goto L_0x0e4f
            r3 = r26
            java.lang.Object r7 = r3.valueAt(r0)
            java.lang.Integer r7 = (java.lang.Integer) r7
            boolean r8 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x0e41
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "cancel notification id "
            r8.append(r9)
            r8.append(r7)
            java.lang.String r8 = r8.toString()
            im.bclpbkiauv.messenger.FileLog.w(r8)
        L_0x0e41:
            androidx.core.app.NotificationManagerCompat r8 = notificationManager
            int r9 = r7.intValue()
            r8.cancel(r9)
            int r0 = r0 + 1
            r26 = r3
            goto L_0x0e1b
        L_0x0e4f:
            r3 = r26
            if (r6 == 0) goto L_0x0e78
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0e74 }
            r0.<init>()     // Catch:{ Exception -> 0x0e74 }
            r7 = r66
            r0.put(r2, r7)     // Catch:{ Exception -> 0x0e72 }
            java.lang.String r2 = "n"
            r0.put(r2, r6)     // Catch:{ Exception -> 0x0e72 }
            java.lang.String r2 = "/notify"
            java.lang.String r8 = r0.toString()     // Catch:{ Exception -> 0x0e72 }
            byte[] r8 = r8.getBytes()     // Catch:{ Exception -> 0x0e72 }
            java.lang.String r9 = "remote_notifications"
            im.bclpbkiauv.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r8, r9)     // Catch:{ Exception -> 0x0e72 }
            goto L_0x0e7a
        L_0x0e72:
            r0 = move-exception
            goto L_0x0e7a
        L_0x0e74:
            r0 = move-exception
            r7 = r66
            goto L_0x0e7a
        L_0x0e78:
            r7 = r66
        L_0x0e7a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, boolean, java.lang.String):void");
    }

    private void loadRoundAvatar(File avatar, Person.Builder personBuilder) {
        if (avatar != null) {
            try {
                personBuilder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(avatar), $$Lambda$NotificationsController$NRS_jKTJfCynorueqQBNZAybGMk.INSTANCE)));
            } catch (Throwable th) {
            }
        }
    }

    static /* synthetic */ int lambda$null$32(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        path.addRoundRect(0.0f, 0.0f, (float) width, (float) canvas.getHeight(), (float) (width / 2), (float) (width / 2), Path.Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawPath(path, paint);
        return -3;
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            notificationsQueue.postRunnable(new Runnable() {
                public final void run() {
                    NotificationsController.this.lambda$playOutChatSound$35$NotificationsController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$playOutChatSound$35$NotificationsController() {
        try {
            if (Math.abs(System.currentTimeMillis() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = System.currentTimeMillis();
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener($$Lambda$NotificationsController$Sf8ZZ5YMNGc51Lpoz8GuYP48_8.INSTANCE);
                }
                if (this.soundOut == 0 && !this.soundOutLoaded) {
                    this.soundOutLoaded = true;
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_out, 1);
                }
                if (this.soundOut != 0) {
                    try {
                        this.soundPool.play(this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }

    static /* synthetic */ void lambda$null$34(SoundPool soundPool2, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool2.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void setDialogNotificationsSettings(long dialog_id, int setting) {
        long flags;
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        TLRPC.Dialog dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(dialog_id);
        if (setting == 4) {
            if (isGlobalNotificationsEnabled(dialog_id)) {
                editor.remove("notify2_" + dialog_id);
            } else {
                editor.putInt("notify2_" + dialog_id, 0);
            }
            getMessagesStorage().setDialogFlags(dialog_id, 0);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        } else {
            int untilTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (setting == 0) {
                untilTime += 3600;
            } else if (setting == 1) {
                untilTime += 28800;
            } else if (setting == 2) {
                untilTime += 172800;
            } else if (setting == 3) {
                untilTime = Integer.MAX_VALUE;
            }
            if (setting == 3) {
                editor.putInt("notify2_" + dialog_id, 2);
                flags = 1;
            } else {
                editor.putInt("notify2_" + dialog_id, 3);
                editor.putInt("notifyuntil_" + dialog_id, untilTime);
                flags = (((long) untilTime) << 32) | 1;
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(dialog_id);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(dialog_id, flags);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings.mute_until = untilTime;
            }
        }
        editor.commit();
        updateServerNotificationsSettings(dialog_id);
    }

    public void updateServerNotificationsSettings(long dialog_id) {
        updateServerNotificationsSettings(dialog_id, true);
    }

    public void updateServerNotificationsSettings(long dialog_id, boolean post) {
        int i = 0;
        if (post) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        if (((int) dialog_id) != 0) {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
            req.settings = new TLRPC.TL_inputPeerNotifySettings();
            req.settings.flags |= 1;
            req.settings.show_previews = preferences.getBoolean("content_preview_" + dialog_id, true);
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings = req.settings;
            tL_inputPeerNotifySettings.flags = tL_inputPeerNotifySettings.flags | 2;
            req.settings.silent = preferences.getBoolean("silent_" + dialog_id, false);
            int mute_type = preferences.getInt("notify2_" + dialog_id, -1);
            if (mute_type != -1) {
                req.settings.flags |= 4;
                if (mute_type == 3) {
                    req.settings.mute_until = preferences.getInt("notifyuntil_" + dialog_id, 0);
                } else {
                    TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = req.settings;
                    if (mute_type == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tL_inputPeerNotifySettings2.mute_until = i;
                }
            }
            req.peer = new TLRPC.TL_inputNotifyPeer();
            ((TLRPC.TL_inputNotifyPeer) req.peer).peer = getMessagesController().getInputPeer((int) dialog_id);
            getConnectionsManager().sendRequest(req, $$Lambda$NotificationsController$QRf7HjeSZia2sMwAGb0LzGqN4hk.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$36(TLObject response, TLRPC.TL_error error) {
    }

    public void updateServerNotificationsSettings(int type) {
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
        req.settings = new TLRPC.TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        if (type == 0) {
            req.peer = new TLRPC.TL_inputNotifyChats();
            req.settings.mute_until = preferences.getInt("EnableGroup2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
        } else if (type == 1) {
            req.peer = new TLRPC.TL_inputNotifyUsers();
            req.settings.mute_until = preferences.getInt("EnableAll2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
        } else {
            req.peer = new TLRPC.TL_inputNotifyBroadcasts();
            req.settings.mute_until = preferences.getInt("EnableChannel2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewChannel", true);
        }
        getConnectionsManager().sendRequest(req, $$Lambda$NotificationsController$_kSaHgN_CV7tHrK9h9_He26KJ9g.INSTANCE);
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$37(TLObject response, TLRPC.TL_error error) {
    }

    public boolean isGlobalNotificationsEnabled(long did) {
        int type;
        int lower_id = (int) did;
        if (lower_id < 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Integer.valueOf(-lower_id));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                type = 0;
            } else {
                type = 2;
            }
        } else {
            type = 1;
        }
        return isGlobalNotificationsEnabled(type);
    }

    public boolean isGlobalNotificationsEnabled(int type) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(type), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int type, int time) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(type), time).commit();
        updateServerNotificationsSettings(type);
    }

    public String getGlobalNotificationsKey(int type) {
        if (type == 0) {
            return "EnableGroup2";
        }
        if (type == 1) {
            return "EnableAll2";
        }
        return "EnableChannel2";
    }
}
