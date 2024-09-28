package im.bclpbkiauv.messenger.voip;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.BuildVars;
import im.bclpbkiauv.messenger.ContactsController;
import im.bclpbkiauv.messenger.FileLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.messenger.ImageLoader;
import im.bclpbkiauv.messenger.LocaleController;
import im.bclpbkiauv.messenger.MessagesController;
import im.bclpbkiauv.messenger.NotificationCenter;
import im.bclpbkiauv.messenger.NotificationsController;
import im.bclpbkiauv.messenger.R;
import im.bclpbkiauv.messenger.StatsController;
import im.bclpbkiauv.messenger.UserConfig;
import im.bclpbkiauv.messenger.voip.VoIPController;
import im.bclpbkiauv.tgnet.ConnectionsManager;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;
import im.bclpbkiauv.ui.VoIPPermissionActivity;
import im.bclpbkiauv.ui.actionbar.BottomSheet;
import im.bclpbkiauv.ui.actionbar.Theme;
import im.bclpbkiauv.ui.components.AvatarDrawable;
import im.bclpbkiauv.ui.components.voip.VoIPHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class VoIPBaseService extends Service implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, VoIPController.ConnectionStateListener, NotificationCenter.NotificationCenterDelegate {
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final int AUDIO_ROUTE_BLUETOOTH = 2;
    public static final int AUDIO_ROUTE_EARPIECE = 0;
    public static final int AUDIO_ROUTE_SPEAKER = 1;
    public static final int DISCARD_REASON_DISCONNECT = 2;
    public static final int DISCARD_REASON_HANGUP = 1;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_MISSED = 3;
    protected static final int ID_INCOMING_CALL_NOTIFICATION = 202;
    protected static final int ID_ONGOING_CALL_NOTIFICATION = 201;
    protected static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final int STATE_ENDED = 11;
    public static final int STATE_ESTABLISHED = 3;
    public static final int STATE_FAILED = 4;
    public static final int STATE_RECONNECTING = 5;
    public static final int STATE_WAIT_INIT = 1;
    public static final int STATE_WAIT_INIT_ACK = 2;
    protected static final boolean USE_CONNECTION_SERVICE = isDeviceCompatibleWithConnectionServiceAPI();
    protected static VoIPBaseService sharedInstance;
    protected Runnable afterSoundRunnable = new Runnable() {
        public void run() {
            VoIPBaseService.this.soundPool.release();
            if (!VoIPBaseService.USE_CONNECTION_SERVICE) {
                if (VoIPBaseService.this.isBtHeadsetConnected) {
                    ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).stopBluetoothSco();
                }
                ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).setSpeakerphoneOn(false);
            }
        }
    };
    protected boolean audioConfigured;
    protected int audioRouteToSet = 2;
    protected boolean bluetoothScoActive = false;
    protected BluetoothAdapter btAdapter;
    protected int callDiscardReason;
    protected Runnable connectingSoundRunnable;
    protected VoIPController controller;
    protected boolean controllerStarted;
    protected PowerManager.WakeLock cpuWakelock;
    protected int currentAccount = -1;
    protected int currentState = 0;
    protected boolean didDeleteConnectionServiceContact = false;
    protected boolean haveAudioFocus;
    protected boolean isBtHeadsetConnected;
    protected boolean isHeadsetPlugged;
    protected boolean isOutgoing;
    protected boolean isProximityNear;
    protected int lastError;
    protected long lastKnownDuration = 0;
    protected NetworkInfo lastNetInfo;
    private Boolean mHasEarpiece = null;
    protected boolean micMute;
    protected boolean needPlayEndSound;
    protected boolean needSwitchToBluetoothAfterScoActivates = false;
    protected Notification ongoingCallNotification;
    protected boolean playingSound;
    protected VoIPController.Stats prevStats = new VoIPController.Stats();
    protected PowerManager.WakeLock proximityWakelock;
    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean z = true;
            if (VoIPBaseService.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                VoIPBaseService voIPBaseService = VoIPBaseService.this;
                if (intent.getIntExtra(RemoteConfigConstants.ResponseFieldKey.STATE, 0) != 1) {
                    z = false;
                }
                voIPBaseService.isHeadsetPlugged = z;
                if (VoIPBaseService.this.isHeadsetPlugged && VoIPBaseService.this.proximityWakelock != null && VoIPBaseService.this.proximityWakelock.isHeld()) {
                    VoIPBaseService.this.proximityWakelock.release();
                }
                VoIPBaseService.this.isProximityNear = false;
                VoIPBaseService.this.updateOutputGainControlState();
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                VoIPBaseService.this.updateNetworkType();
            } else if ("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("bt headset state = " + intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0));
                }
                VoIPBaseService voIPBaseService2 = VoIPBaseService.this;
                if (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0) != 2) {
                    z = false;
                }
                voIPBaseService2.updateBluetoothHeadsetState(z);
            } else if ("android.media.ACTION_SCO_AUDIO_STATE_UPDATED".equals(intent.getAction())) {
                int state = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Bluetooth SCO state updated: " + state);
                }
                if (state != 0 || !VoIPBaseService.this.isBtHeadsetConnected || (VoIPBaseService.this.btAdapter.isEnabled() && VoIPBaseService.this.btAdapter.getProfileConnectionState(1) == 2)) {
                    VoIPBaseService.this.bluetoothScoActive = state == 1;
                    if (VoIPBaseService.this.bluetoothScoActive && VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates) {
                        VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates = false;
                        AudioManager am = (AudioManager) VoIPBaseService.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                        am.setSpeakerphoneOn(false);
                        am.setBluetoothScoOn(true);
                    }
                    Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onAudioSettingsChanged();
                    }
                    return;
                }
                VoIPBaseService.this.updateBluetoothHeadsetState(false);
            } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(intent.getStringExtra(RemoteConfigConstants.ResponseFieldKey.STATE))) {
                    VoIPBaseService.this.hangUp();
                }
            }
        }
    };
    protected MediaPlayer ringtonePlayer;
    protected int signalBarCount;
    protected SoundPool soundPool;
    protected int spBusyId;
    protected int spConnectingId;
    protected int spEndId;
    protected int spFailedID;
    protected int spPlayID;
    protected int spRingbackID;
    protected boolean speakerphoneStateToSet;
    protected ArrayList<StateListener> stateListeners = new ArrayList<>();
    protected VoIPController.Stats stats = new VoIPController.Stats();
    protected CallConnection systemCallConnection;
    protected Runnable timeoutRunnable;
    protected Vibrator vibrator;
    private boolean wasEstablished;

    public interface StateListener {
        void onAudioSettingsChanged();

        void onSignalBarsCountChanged(int i);

        void onStateChanged(int i);
    }

    public abstract void acceptIncomingCall();

    public abstract void declineIncomingCall();

    public abstract void declineIncomingCall(int i, Runnable runnable);

    public abstract long getCallID();

    public abstract CallConnection getConnectionAndStartCall();

    /* access modifiers changed from: protected */
    public abstract Class<? extends Activity> getUIActivityClass();

    public abstract void hangUp();

    public abstract void hangUp(Runnable runnable);

    /* access modifiers changed from: protected */
    public abstract void showNotification();

    /* access modifiers changed from: protected */
    public abstract void startRinging();

    public abstract void startRingtoneAndVibration();

    /* access modifiers changed from: protected */
    public abstract void updateServerConfig();

    public boolean hasEarpiece() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            if (((TelephonyManager) getSystemService("phone")).getPhoneType() != 0) {
                return true;
            }
            Boolean bool = this.mHasEarpiece;
            if (bool != null) {
                return bool.booleanValue();
            }
            try {
                Method method = AudioManager.class.getMethod("getDevicesForStream", new Class[]{Integer.TYPE});
                int earpieceFlag = AudioManager.class.getField("DEVICE_OUT_EARPIECE").getInt((Object) null);
                if ((((Integer) method.invoke((AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO), new Object[]{0})).intValue() & earpieceFlag) == earpieceFlag) {
                    this.mHasEarpiece = Boolean.TRUE;
                } else {
                    this.mHasEarpiece = Boolean.FALSE;
                }
            } catch (Throwable error) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error while checking earpiece! ", error);
                }
                this.mHasEarpiece = Boolean.TRUE;
            }
            return this.mHasEarpiece.booleanValue();
        } else if ((this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 5) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public int getStatsNetworkType() {
        NetworkInfo networkInfo = this.lastNetInfo;
        if (networkInfo == null || networkInfo.getType() != 0) {
            return 1;
        }
        return this.lastNetInfo.isRoaming() ? 2 : 0;
    }

    public void registerStateListener(StateListener l) {
        this.stateListeners.add(l);
        int i = this.currentState;
        if (i != 0) {
            l.onStateChanged(i);
        }
        int i2 = this.signalBarCount;
        if (i2 != 0) {
            l.onSignalBarsCountChanged(i2);
        }
    }

    public void unregisterStateListener(StateListener l) {
        this.stateListeners.remove(l);
    }

    public void setMicMute(boolean mute) {
        this.micMute = mute;
        VoIPController voIPController = this.controller;
        if (voIPController != null) {
            voIPController.setMicMute(mute);
        }
    }

    public boolean isMicMute() {
        return this.micMute;
    }

    public void toggleSpeakerphoneOrShowRouteSheet(Activity activity) {
        CallConnection callConnection;
        int i = 2;
        if (!isBluetoothHeadsetConnected() || !hasEarpiece()) {
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
                int i2 = 5;
                if (hasEarpiece()) {
                    CallConnection callConnection2 = this.systemCallConnection;
                    if (callConnection2.getCallAudioState().getRoute() != 8) {
                        i2 = 8;
                    }
                    callConnection2.setAudioRoute(i2);
                } else {
                    CallConnection callConnection3 = this.systemCallConnection;
                    if (callConnection3.getCallAudioState().getRoute() == 2) {
                        i = 5;
                    }
                    callConnection3.setAudioRoute(i);
                }
            } else if (!this.audioConfigured || USE_CONNECTION_SERVICE) {
                this.speakerphoneStateToSet = !this.speakerphoneStateToSet;
            } else {
                AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                if (hasEarpiece()) {
                    am.setSpeakerphoneOn(!am.isSpeakerphoneOn());
                } else {
                    am.setBluetoothScoOn(!am.isBluetoothScoOn());
                }
                updateOutputGainControlState();
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
            return;
        }
        BottomSheet sheet = new BottomSheet.Builder(activity).setItems(new CharSequence[]{LocaleController.getString("VoipAudioRoutingBluetooth", R.string.VoipAudioRoutingBluetooth), LocaleController.getString("VoipAudioRoutingEarpiece", R.string.VoipAudioRoutingEarpiece), LocaleController.getString("VoipAudioRoutingSpeaker", R.string.VoipAudioRoutingSpeaker)}, new int[]{R.drawable.ic_bluetooth_white_24dp, R.drawable.ic_phone_in_talk_white_24dp, R.drawable.ic_volume_up_white_24dp}, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AudioManager am = (AudioManager) VoIPBaseService.this.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                if (VoIPBaseService.getSharedInstance() != null) {
                    if (!VoIPBaseService.USE_CONNECTION_SERVICE || VoIPBaseService.this.systemCallConnection == null) {
                        if (VoIPBaseService.this.audioConfigured && !VoIPBaseService.USE_CONNECTION_SERVICE) {
                            if (which != 0) {
                                if (which == 1) {
                                    if (VoIPBaseService.this.bluetoothScoActive) {
                                        am.stopBluetoothSco();
                                    }
                                    am.setSpeakerphoneOn(false);
                                    am.setBluetoothScoOn(false);
                                } else if (which == 2) {
                                    if (VoIPBaseService.this.bluetoothScoActive) {
                                        am.stopBluetoothSco();
                                    }
                                    am.setBluetoothScoOn(false);
                                    am.setSpeakerphoneOn(true);
                                }
                            } else if (!VoIPBaseService.this.bluetoothScoActive) {
                                VoIPBaseService.this.needSwitchToBluetoothAfterScoActivates = true;
                                try {
                                    am.startBluetoothSco();
                                } catch (Throwable th) {
                                }
                            } else {
                                am.setBluetoothScoOn(true);
                                am.setSpeakerphoneOn(false);
                            }
                            VoIPBaseService.this.updateOutputGainControlState();
                        } else if (which == 0) {
                            VoIPBaseService.this.audioRouteToSet = 2;
                        } else if (which == 1) {
                            VoIPBaseService.this.audioRouteToSet = 0;
                        } else if (which == 2) {
                            VoIPBaseService.this.audioRouteToSet = 1;
                        }
                    } else if (which == 0) {
                        VoIPBaseService.this.systemCallConnection.setAudioRoute(2);
                    } else if (which == 1) {
                        VoIPBaseService.this.systemCallConnection.setAudioRoute(5);
                    } else if (which == 2) {
                        VoIPBaseService.this.systemCallConnection.setAudioRoute(8);
                    }
                    Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onAudioSettingsChanged();
                    }
                }
            }
        }).create();
        sheet.setBackgroundColor(-13948117);
        sheet.show();
        ViewGroup container = sheet.getSheetContainer();
        for (int i3 = 0; i3 < container.getChildCount(); i3++) {
            ((BottomSheet.BottomSheetCell) container.getChildAt(i3)).setTextColor(-1);
        }
    }

    public boolean isSpeakerphoneOn() {
        CallConnection callConnection;
        if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null && callConnection.getCallAudioState() != null) {
            int route = this.systemCallConnection.getCallAudioState().getRoute();
            if (hasEarpiece()) {
                if (route == 8) {
                    return true;
                }
            } else if (route == 2) {
                return true;
            }
            return false;
        } else if (this.audioConfigured == 0 || USE_CONNECTION_SERVICE) {
            return this.speakerphoneStateToSet;
        } else {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            return hasEarpiece() ? am.isSpeakerphoneOn() : am.isBluetoothScoOn();
        }
    }

    public int getCurrentAudioRoute() {
        if (USE_CONNECTION_SERVICE) {
            CallConnection callConnection = this.systemCallConnection;
            if (!(callConnection == null || callConnection.getCallAudioState() == null)) {
                int route = this.systemCallConnection.getCallAudioState().getRoute();
                if (route != 1) {
                    if (route == 2) {
                        return 2;
                    }
                    if (route != 4) {
                        if (route == 8) {
                            return 1;
                        }
                    }
                }
                return 0;
            }
            return this.audioRouteToSet;
        } else if (!this.audioConfigured) {
            return this.audioRouteToSet;
        } else {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (am.isBluetoothScoOn()) {
                return 2;
            }
            return am.isSpeakerphoneOn() ? 1 : 0;
        }
    }

    public String getDebugString() {
        return this.controller.getDebugString();
    }

    public long getCallDuration() {
        VoIPController voIPController;
        if (!this.controllerStarted || (voIPController = this.controller) == null) {
            return this.lastKnownDuration;
        }
        long callDuration = voIPController.getCallDuration();
        this.lastKnownDuration = callDuration;
        return callDuration;
    }

    public static VoIPBaseService getSharedInstance() {
        return sharedInstance;
    }

    public void stopRinging() {
        MediaPlayer mediaPlayer = this.ringtonePlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.ringtonePlayer.release();
            this.ringtonePlayer = null;
        }
        Vibrator vibrator2 = this.vibrator;
        if (vibrator2 != null) {
            vibrator2.cancel();
            this.vibrator = null;
        }
    }

    /* access modifiers changed from: protected */
    public void showNotification(String name, TLRPC.FileLocation photo, Class<? extends Activity> activity) {
        Intent intent = new Intent(this, activity);
        intent.addFlags(805306368);
        Notification.Builder builder = new Notification.Builder(this).setContentTitle(LocaleController.getString("VoipOutgoingCall", R.string.VoipOutgoingCall)).setContentText(name).setSmallIcon(R.mipmap.notification).setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        if (Build.VERSION.SDK_INT >= 16) {
            Intent endIntent = new Intent(this, VoIPActionsReceiver.class);
            endIntent.setAction(getPackageName() + ".END_CALL");
            builder.addAction(R.drawable.ic_call_end_white_24dp, LocaleController.getString("VoipEndCall", R.string.VoipEndCall), PendingIntent.getBroadcast(this, 0, endIntent, 134217728));
            builder.setPriority(2);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            builder.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setColor(-13851168);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationsController.checkOtherNotificationsChannel();
            builder.setChannelId(NotificationsController.OTHER_NOTIFICATIONS_CHANNEL);
        }
        if (photo != null) {
            BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photo, (String) null, "50_50");
            if (img != null) {
                builder.setLargeIcon(img.getBitmap());
            } else {
                try {
                    float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = scaleFactor < 1.0f ? 1 : (int) scaleFactor;
                    Bitmap bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photo, true).toString(), options);
                    if (bitmap != null) {
                        builder.setLargeIcon(bitmap);
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        }
        Notification notification = builder.getNotification();
        this.ongoingCallNotification = notification;
        startForeground(ID_ONGOING_CALL_NOTIFICATION, notification);
    }

    /* access modifiers changed from: protected */
    public void startRingtoneAndVibration(int chatID) {
        int vibrate;
        String notificationUri;
        SharedPreferences prefs = MessagesController.getNotificationsSettings(this.currentAccount);
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (am.getRingerMode() != 0) {
            if (!USE_CONNECTION_SERVICE) {
                am.requestAudioFocus(this, 2, 1);
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.ringtonePlayer = mediaPlayer;
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    VoIPBaseService.this.ringtonePlayer.start();
                }
            });
            this.ringtonePlayer.setLooping(true);
            this.ringtonePlayer.setAudioStreamType(2);
            try {
                if (prefs.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + chatID, false)) {
                    notificationUri = prefs.getString("ringtone_path_" + chatID, RingtoneManager.getDefaultUri(1).toString());
                } else {
                    notificationUri = prefs.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString());
                }
                this.ringtonePlayer.setDataSource(this, Uri.parse(notificationUri));
                this.ringtonePlayer.prepareAsync();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                MediaPlayer mediaPlayer2 = this.ringtonePlayer;
                if (mediaPlayer2 != null) {
                    mediaPlayer2.release();
                    this.ringtonePlayer = null;
                }
            }
            if (prefs.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + chatID, false)) {
                vibrate = prefs.getInt("calls_vibrate_" + chatID, 0);
            } else {
                vibrate = prefs.getInt("vibrate_calls", 0);
            }
            if ((vibrate != 2 && vibrate != 4 && (am.getRingerMode() == 1 || am.getRingerMode() == 2)) || (vibrate == 4 && am.getRingerMode() == 1)) {
                this.vibrator = (Vibrator) getSystemService("vibrator");
                long duration = 700;
                if (vibrate == 1) {
                    duration = 700 / 2;
                } else if (vibrate == 3) {
                    duration = 700 * 2;
                }
                this.vibrator.vibrate(new long[]{0, duration, 500}, 0);
            }
        }
    }

    public void onDestroy() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STOPPING ===============");
        }
        stopForeground(true);
        stopRinging();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
        SensorManager sm = (SensorManager) getSystemService("sensor");
        if (sm.getDefaultSensor(8) != null) {
            sm.unregisterListener(this);
        }
        PowerManager.WakeLock wakeLock = this.proximityWakelock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.proximityWakelock.release();
        }
        unregisterReceiver(this.receiver);
        Runnable runnable = this.timeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.timeoutRunnable = null;
        }
        super.onDestroy();
        sharedInstance = null;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didEndedCall, new Object[0]);
            }
        });
        VoIPController voIPController = this.controller;
        if (voIPController != null && this.controllerStarted) {
            this.lastKnownDuration = voIPController.getCallDuration();
            updateStats();
            StatsController.getInstance(this.currentAccount).incrementTotalCallsTime(getStatsNetworkType(), ((int) (this.lastKnownDuration / 1000)) % 5);
            onControllerPreRelease();
            this.controller.release();
            this.controller = null;
        }
        this.cpuWakelock.release();
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (!USE_CONNECTION_SERVICE) {
            if (this.isBtHeadsetConnected && !this.playingSound) {
                am.stopBluetoothSco();
                am.setSpeakerphoneOn(false);
            }
            try {
                am.setMode(0);
            } catch (SecurityException x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error setting audio more to normal", (Throwable) x);
                }
            }
            am.abandonAudioFocus(this);
        }
        am.unregisterMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
        if (this.haveAudioFocus) {
            am.abandonAudioFocus(this);
        }
        if (!this.playingSound) {
            this.soundPool.release();
        }
        if (USE_CONNECTION_SERVICE) {
            if (!this.didDeleteConnectionServiceContact) {
                ContactsController.getInstance(this.currentAccount).deleteConnectionServiceContact();
            }
            CallConnection callConnection = this.systemCallConnection;
            if (callConnection != null && !this.playingSound) {
                callConnection.destroy();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        VoIPHelper.lastCallTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: protected */
    public void onControllerPreRelease() {
    }

    /* access modifiers changed from: protected */
    public VoIPController createController() {
        return new VoIPController();
    }

    /* access modifiers changed from: protected */
    public void initializeAccountRelatedThings() {
        updateServerConfig();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
        ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        VoIPController createController = createController();
        this.controller = createController;
        createController.setConnectionStateListener(this);
    }

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("=============== VoIPService STARTING ===============");
        }
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (Build.VERSION.SDK_INT < 17 || am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER") == null) {
            VoIPController.setNativeBufferSize(AudioTrack.getMinBufferSize(48000, 4, 2) / 2);
        } else {
            VoIPController.setNativeBufferSize(Integer.parseInt(am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER")));
        }
        try {
            boolean z = true;
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "hchat-voip");
            this.cpuWakelock = newWakeLock;
            newWakeLock.acquire();
            this.btAdapter = am.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            if (!USE_CONNECTION_SERVICE) {
                filter.addAction(ACTION_HEADSET_PLUG);
                if (this.btAdapter != null) {
                    filter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                    filter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
                }
                filter.addAction("android.intent.action.PHONE_STATE");
            }
            registerReceiver(this.receiver, filter);
            SoundPool soundPool2 = new SoundPool(1, 0, 0);
            this.soundPool = soundPool2;
            this.spConnectingId = soundPool2.load(this, R.raw.voip_connecting, 1);
            this.spRingbackID = this.soundPool.load(this, R.raw.voip_ringback, 1);
            this.spFailedID = this.soundPool.load(this, R.raw.voip_failed, 1);
            this.spEndId = this.soundPool.load(this, R.raw.voip_end, 1);
            this.spBusyId = this.soundPool.load(this, R.raw.voip_busy, 1);
            am.registerMediaButtonEventReceiver(new ComponentName(this, VoIPMediaButtonReceiver.class));
            if (!USE_CONNECTION_SERVICE && this.btAdapter != null && this.btAdapter.isEnabled()) {
                if (this.btAdapter.getProfileConnectionState(1) != 2) {
                    z = false;
                }
                updateBluetoothHeadsetState(z);
                Iterator<StateListener> it = this.stateListeners.iterator();
                while (it.hasNext()) {
                    it.next().onAudioSettingsChanged();
                }
            }
        } catch (Exception x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("error initializing voip controller", (Throwable) x);
            }
            callFailed();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchStateChanged(int state) {
        CallConnection callConnection;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("== Call " + getCallID() + " state changed to " + state + " ==");
        }
        this.currentState = state;
        if (USE_CONNECTION_SERVICE && state == 3 && (callConnection = this.systemCallConnection) != null) {
            callConnection.setActive();
        }
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onStateChanged(state);
        }
    }

    /* access modifiers changed from: protected */
    public void updateStats() {
        this.controller.getStats(this.stats);
        long wifiSentDiff = this.stats.bytesSentWifi - this.prevStats.bytesSentWifi;
        long wifiRecvdDiff = this.stats.bytesRecvdWifi - this.prevStats.bytesRecvdWifi;
        long mobileSentDiff = this.stats.bytesSentMobile - this.prevStats.bytesSentMobile;
        long mobileRecvdDiff = this.stats.bytesRecvdMobile - this.prevStats.bytesRecvdMobile;
        VoIPController.Stats tmp = this.stats;
        this.stats = this.prevStats;
        this.prevStats = tmp;
        if (wifiSentDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(1, 0, wifiSentDiff);
        }
        if (wifiRecvdDiff > 0) {
            StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(1, 0, wifiRecvdDiff);
        }
        int i = 2;
        if (mobileSentDiff > 0) {
            StatsController instance = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo = this.lastNetInfo;
            instance.incrementSentBytesCount((networkInfo == null || !networkInfo.isRoaming()) ? 0 : 2, 0, mobileSentDiff);
        }
        if (mobileRecvdDiff > 0) {
            StatsController instance2 = StatsController.getInstance(this.currentAccount);
            NetworkInfo networkInfo2 = this.lastNetInfo;
            if (networkInfo2 == null || !networkInfo2.isRoaming()) {
                i = 0;
            }
            instance2.incrementReceivedBytesCount(i, 0, mobileRecvdDiff);
        }
    }

    /* access modifiers changed from: protected */
    public void configureDeviceForCall() {
        this.needPlayEndSound = true;
        AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        if (!USE_CONNECTION_SERVICE) {
            am.setMode(3);
            am.requestAudioFocus(this, 0, 1);
            if (isBluetoothHeadsetConnected() && hasEarpiece()) {
                int i = this.audioRouteToSet;
                if (i == 0) {
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(false);
                } else if (i == 1) {
                    am.setBluetoothScoOn(false);
                    am.setSpeakerphoneOn(true);
                } else if (i == 2) {
                    if (!this.bluetoothScoActive) {
                        this.needSwitchToBluetoothAfterScoActivates = true;
                        try {
                            am.startBluetoothSco();
                        } catch (Throwable th) {
                        }
                    } else {
                        am.setBluetoothScoOn(true);
                        am.setSpeakerphoneOn(false);
                    }
                }
            } else if (isBluetoothHeadsetConnected()) {
                am.setBluetoothScoOn(this.speakerphoneStateToSet);
            } else {
                am.setSpeakerphoneOn(this.speakerphoneStateToSet);
            }
        }
        updateOutputGainControlState();
        this.audioConfigured = true;
        SensorManager sm = (SensorManager) getSystemService("sensor");
        Sensor proximity = sm.getDefaultSensor(8);
        if (proximity != null) {
            try {
                this.proximityWakelock = ((PowerManager) getSystemService("power")).newWakeLock(32, "hchat-voip-prx");
                sm.registerListener(this, proximity, 3);
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error initializing proximity sensor", (Throwable) x);
                }
            }
        }
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 8) {
            AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (!this.isHeadsetPlugged && !am.isSpeakerphoneOn()) {
                if (!isBluetoothHeadsetConnected() || !am.isBluetoothScoOn()) {
                    boolean z = false;
                    if (event.values[0] < Math.min(event.sensor.getMaximumRange(), 3.0f)) {
                        z = true;
                    }
                    boolean newIsNear = z;
                    if (newIsNear != this.isProximityNear) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("proximity " + newIsNear);
                        }
                        this.isProximityNear = newIsNear;
                        if (newIsNear) {
                            try {
                                this.proximityWakelock.acquire();
                            } catch (Exception x) {
                                FileLog.e((Throwable) x);
                            }
                        } else {
                            this.proximityWakelock.release(1);
                        }
                    }
                }
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public boolean isBluetoothHeadsetConnected() {
        CallConnection callConnection;
        if (!USE_CONNECTION_SERVICE || (callConnection = this.systemCallConnection) == null || callConnection.getCallAudioState() == null) {
            return this.isBtHeadsetConnected;
        }
        return (this.systemCallConnection.getCallAudioState().getSupportedRouteMask() & 2) != 0;
    }

    public void onAudioFocusChange(int focusChange) {
        if (focusChange == 1) {
            this.haveAudioFocus = true;
        } else {
            this.haveAudioFocus = false;
        }
    }

    /* access modifiers changed from: protected */
    public void updateBluetoothHeadsetState(boolean connected) {
        if (connected != this.isBtHeadsetConnected) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("updateBluetoothHeadsetState: " + connected);
            }
            this.isBtHeadsetConnected = connected;
            final AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
            if (!connected || isRinging() || this.currentState == 0) {
                this.bluetoothScoActive = false;
            } else if (this.bluetoothScoActive) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("SCO already active, setting audio routing");
                }
                am.setSpeakerphoneOn(false);
                am.setBluetoothScoOn(true);
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("startBluetoothSco");
                }
                this.needSwitchToBluetoothAfterScoActivates = true;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        try {
                            am.startBluetoothSco();
                        } catch (Throwable th) {
                        }
                    }
                }, 500);
            }
            Iterator<StateListener> it = this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }
    }

    public int getLastError() {
        return this.lastError;
    }

    public int getCallState() {
        return this.currentState;
    }

    /* access modifiers changed from: protected */
    public void updateNetworkType() {
        NetworkInfo info = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        this.lastNetInfo = info;
        int type = 0;
        if (info != null) {
            int type2 = info.getType();
            if (type2 == 0) {
                switch (info.getSubtype()) {
                    case 1:
                        type = 1;
                        break;
                    case 2:
                    case 7:
                        type = 2;
                        break;
                    case 3:
                    case 5:
                        type = 3;
                        break;
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 15:
                        type = 4;
                        break;
                    case 13:
                        type = 5;
                        break;
                    default:
                        type = 11;
                        break;
                }
            } else if (type2 == 1) {
                type = 6;
            } else if (type2 == 9) {
                type = 7;
            }
        }
        VoIPController voIPController = this.controller;
        if (voIPController != null) {
            voIPController.setNetworkType(type);
        }
    }

    /* access modifiers changed from: protected */
    public void callFailed() {
        VoIPController voIPController = this.controller;
        callFailed((voIPController == null || !this.controllerStarted) ? 0 : voIPController.getLastError());
    }

    /* access modifiers changed from: protected */
    public Bitmap getRoundAvatarBitmap(TLObject userOrChat) {
        AvatarDrawable placeholder;
        Bitmap bitmap = null;
        if (userOrChat instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) userOrChat;
            if (!(user.photo == null || user.photo.photo_small == null)) {
                BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(user.photo.photo_small, (String) null, "50_50");
                if (img != null) {
                    bitmap = img.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(user.photo.photo_small, true).toString(), opts);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            }
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) userOrChat;
            if (!(chat.photo == null || chat.photo.photo_small == null)) {
                BitmapDrawable img2 = ImageLoader.getInstance().getImageFromMemory(chat.photo.photo_small, (String) null, "50_50");
                if (img2 != null) {
                    bitmap = img2.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                } else {
                    try {
                        BitmapFactory.Options opts2 = new BitmapFactory.Options();
                        opts2.inMutable = true;
                        bitmap = BitmapFactory.decodeFile(FileLoader.getPathToAttach(chat.photo.photo_small, true).toString(), opts2);
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
            }
        }
        if (bitmap == null) {
            Theme.createDialogsResources(this);
            if (userOrChat instanceof TLRPC.User) {
                placeholder = new AvatarDrawable((TLRPC.User) userOrChat);
            } else {
                placeholder = new AvatarDrawable((TLRPC.Chat) userOrChat);
            }
            bitmap = Bitmap.createBitmap(AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), Bitmap.Config.ARGB_8888);
            placeholder.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            placeholder.draw(new Canvas(bitmap));
        }
        Canvas canvas = new Canvas(bitmap);
        Path circlePath = new Path();
        circlePath.addCircle((float) (bitmap.getWidth() / 2), (float) (bitmap.getHeight() / 2), (float) (bitmap.getWidth() / 2), Path.Direction.CW);
        circlePath.toggleInverseFillType();
        Paint paint = new Paint(1);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPath(circlePath, paint);
        return bitmap;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: android.text.SpannableString} */
    /* JADX WARNING: type inference failed for: r13v2, types: [android.text.SpannableString] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showIncomingNotification(java.lang.String r26, java.lang.CharSequence r27, im.bclpbkiauv.tgnet.TLObject r28, java.util.List<im.bclpbkiauv.tgnet.TLRPC.User> r29, int r30, java.lang.Class<? extends android.app.Activity> r31) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
            r2 = r27
            r3 = r28
            android.content.Intent r4 = new android.content.Intent
            r5 = r31
            r4.<init>(r0, r5)
            r6 = 805306368(0x30000000, float:4.656613E-10)
            r4.addFlags(r6)
            android.app.Notification$Builder r6 = new android.app.Notification$Builder
            r6.<init>(r0)
            r7 = 2131694700(0x7f0f146c, float:1.9018564E38)
            java.lang.String r8 = "VoipInCallBranding"
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r7)
            android.app.Notification$Builder r6 = r6.setContentTitle(r9)
            android.app.Notification$Builder r6 = r6.setContentText(r1)
            r9 = 2131558767(0x7f0d016f, float:1.874286E38)
            android.app.Notification$Builder r6 = r6.setSmallIcon(r9)
            android.app.Notification$Builder r6 = r6.setSubText(r2)
            r9 = 0
            android.app.PendingIntent r10 = android.app.PendingIntent.getActivity(r0, r9, r4, r9)
            android.app.Notification$Builder r6 = r6.setContentIntent(r10)
            java.lang.String r10 = "content://im.bclpbkiauv.messenger.call_sound_provider/start_ringing"
            android.net.Uri r10 = android.net.Uri.parse(r10)
            int r11 = android.os.Build.VERSION.SDK_INT
            r14 = 26
            if (r11 < r14) goto L_0x0139
            android.content.SharedPreferences r11 = im.bclpbkiauv.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r14 = "calls_notification_channel"
            int r15 = r11.getInt(r14, r9)
            java.lang.String r7 = "notification"
            java.lang.Object r7 = r0.getSystemService(r7)
            android.app.NotificationManager r7 = (android.app.NotificationManager) r7
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "incoming_calls"
            r12.append(r13)
            r12.append(r15)
            java.lang.String r12 = r12.toString()
            android.app.NotificationChannel r12 = r7.getNotificationChannel(r12)
            if (r12 == 0) goto L_0x007a
            java.lang.String r13 = r12.getId()
            r7.deleteNotificationChannel(r13)
        L_0x007a:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r9 = "incoming_calls2"
            r13.append(r9)
            r13.append(r15)
            java.lang.String r13 = r13.toString()
            android.app.NotificationChannel r13 = r7.getNotificationChannel(r13)
            r18 = 1
            r5 = 4
            if (r13 == 0) goto L_0x00df
            r19 = r12
            int r12 = r13.getImportance()
            if (r12 < r5) goto L_0x00b6
            android.net.Uri r12 = r13.getSound()
            boolean r12 = r10.equals(r12)
            if (r12 == 0) goto L_0x00b6
            long[] r12 = r13.getVibrationPattern()
            if (r12 != 0) goto L_0x00b6
            boolean r12 = r13.shouldVibrate()
            if (r12 == 0) goto L_0x00b3
            goto L_0x00b6
        L_0x00b3:
            r18 = 0
            goto L_0x00e1
        L_0x00b6:
            boolean r12 = im.bclpbkiauv.messenger.BuildVars.LOGS_ENABLED
            if (r12 == 0) goto L_0x00bf
            java.lang.String r12 = "User messed up the notification channel; deleting it and creating a proper one"
            im.bclpbkiauv.messenger.FileLog.d(r12)
        L_0x00bf:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r9)
            r12.append(r15)
            java.lang.String r12 = r12.toString()
            r7.deleteNotificationChannel(r12)
            int r15 = r15 + 1
            android.content.SharedPreferences$Editor r12 = r11.edit()
            android.content.SharedPreferences$Editor r12 = r12.putInt(r14, r15)
            r12.commit()
            goto L_0x00e1
        L_0x00df:
            r19 = r12
        L_0x00e1:
            if (r18 == 0) goto L_0x0122
            android.media.AudioAttributes$Builder r12 = new android.media.AudioAttributes$Builder
            r12.<init>()
            r14 = 6
            android.media.AudioAttributes$Builder r12 = r12.setUsage(r14)
            android.media.AudioAttributes r12 = r12.build()
            android.app.NotificationChannel r14 = new android.app.NotificationChannel
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            r5.append(r15)
            java.lang.String r5 = r5.toString()
            r21 = r11
            r11 = 2131691641(0x7f0f0879, float:1.901236E38)
            r22 = r13
            java.lang.String r13 = "IncomingCalls"
            java.lang.String r11 = im.bclpbkiauv.messenger.LocaleController.getString(r13, r11)
            r13 = 4
            r14.<init>(r5, r11, r13)
            r5 = r14
            r5.setSound(r10, r12)
            r11 = 0
            r5.enableVibration(r11)
            r5.enableLights(r11)
            r7.createNotificationChannel(r5)
            goto L_0x0126
        L_0x0122:
            r21 = r11
            r22 = r13
        L_0x0126:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            r5.append(r15)
            java.lang.String r5 = r5.toString()
            r6.setChannelId(r5)
            goto L_0x0144
        L_0x0139:
            int r5 = android.os.Build.VERSION.SDK_INT
            r7 = 21
            if (r5 < r7) goto L_0x0144
            r5 = 2
            r6.setSound(r10, r5)
            goto L_0x0145
        L_0x0144:
        L_0x0145:
            android.content.Intent r5 = new android.content.Intent
            java.lang.Class<im.bclpbkiauv.messenger.voip.VoIPActionsReceiver> r7 = im.bclpbkiauv.messenger.voip.VoIPActionsReceiver.class
            r5.<init>(r0, r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r9 = r25.getPackageName()
            r7.append(r9)
            java.lang.String r9 = ".DECLINE_CALL"
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r5.setAction(r7)
            long r11 = r25.getCallID()
            java.lang.String r7 = "call_id"
            r5.putExtra(r7, r11)
            r9 = 2131694690(0x7f0f1462, float:1.9018544E38)
            java.lang.String r11 = "VoipDeclineCall"
            java.lang.String r12 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r9)
            int r13 = android.os.Build.VERSION.SDK_INT
            r14 = 24
            if (r13 < r14) goto L_0x0196
            android.text.SpannableString r13 = new android.text.SpannableString
            r13.<init>(r12)
            r12 = r13
            r13 = r12
            android.text.SpannableString r13 = (android.text.SpannableString) r13
            android.text.style.ForegroundColorSpan r15 = new android.text.style.ForegroundColorSpan
            r9 = -769226(0xfffffffffff44336, float:NaN)
            r15.<init>(r9)
            int r9 = r12.length()
            r14 = 0
            r13.setSpan(r15, r14, r9, r14)
            goto L_0x0197
        L_0x0196:
            r14 = 0
        L_0x0197:
            r9 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r13 = android.app.PendingIntent.getBroadcast(r0, r14, r5, r9)
            r14 = 2131231100(0x7f08017c, float:1.8078271E38)
            r6.addAction(r14, r12, r13)
            android.content.Intent r14 = new android.content.Intent
            java.lang.Class<im.bclpbkiauv.messenger.voip.VoIPActionsReceiver> r15 = im.bclpbkiauv.messenger.voip.VoIPActionsReceiver.class
            r14.<init>(r0, r15)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r9 = r25.getPackageName()
            r15.append(r9)
            java.lang.String r9 = ".ANSWER_CALL"
            r15.append(r9)
            java.lang.String r9 = r15.toString()
            r14.setAction(r9)
            r15 = r10
            long r9 = r25.getCallID()
            r14.putExtra(r7, r9)
            r7 = 2131694682(0x7f0f145a, float:1.9018527E38)
            java.lang.String r9 = "VoipAnswerCall"
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r7)
            int r7 = android.os.Build.VERSION.SDK_INT
            r22 = r5
            r5 = 24
            if (r7 < r5) goto L_0x01fb
            android.text.SpannableString r5 = new android.text.SpannableString
            r5.<init>(r10)
            r10 = r5
            r5 = r10
            android.text.SpannableString r5 = (android.text.SpannableString) r5
            android.text.style.ForegroundColorSpan r7 = new android.text.style.ForegroundColorSpan
            r19 = r12
            r12 = -16733696(0xffffffffff00aa00, float:-1.7102387E38)
            r7.<init>(r12)
            int r12 = r10.length()
            r23 = r10
            r10 = 0
            r5.setSpan(r7, r10, r12, r10)
            r5 = r23
            goto L_0x01ff
        L_0x01fb:
            r5 = r10
            r19 = r12
            r10 = 0
        L_0x01ff:
            r7 = 268435456(0x10000000, float:2.5243549E-29)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcast(r0, r10, r14, r7)
            r12 = 2131231099(0x7f08017b, float:1.807827E38)
            r6.addAction(r12, r5, r7)
            r12 = 2
            r6.setPriority(r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r10 = 17
            if (r12 < r10) goto L_0x0219
            r10 = 0
            r6.setShowWhen(r10)
        L_0x0219:
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r10 < r12) goto L_0x0267
            r10 = -13851168(0xffffffffff2ca5e0, float:-2.2948849E38)
            r6.setColor(r10)
            r10 = 0
            long[] r12 = new long[r10]
            r6.setVibrate(r12)
            java.lang.String r12 = "call"
            r6.setCategory(r12)
            android.app.PendingIntent r12 = android.app.PendingIntent.getActivity(r0, r10, r4, r10)
            r10 = 1
            r6.setFullScreenIntent(r12, r10)
            boolean r10 = r3 instanceof im.bclpbkiauv.tgnet.TLRPC.User
            if (r10 == 0) goto L_0x0264
            r10 = r3
            im.bclpbkiauv.tgnet.TLRPC$User r10 = (im.bclpbkiauv.tgnet.TLRPC.User) r10
            java.lang.String r12 = r10.phone
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            if (r12 != 0) goto L_0x0261
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r20 = r4
            java.lang.String r4 = "tel:"
            r12.append(r4)
            java.lang.String r4 = r10.phone
            r12.append(r4)
            java.lang.String r4 = r12.toString()
            r6.addPerson(r4)
            goto L_0x0269
        L_0x0261:
            r20 = r4
            goto L_0x0269
        L_0x0264:
            r20 = r4
            goto L_0x0269
        L_0x0267:
            r20 = r4
        L_0x0269:
            android.app.Notification r4 = r6.getNotification()
            int r10 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            if (r10 < r12) goto L_0x0366
            android.widget.RemoteViews r10 = new android.widget.RemoteViews
            java.lang.String r12 = r25.getPackageName()
            boolean r17 = im.bclpbkiauv.messenger.LocaleController.isRTL
            if (r17 == 0) goto L_0x0286
            r17 = 2131492992(0x7f0c0080, float:1.8609452E38)
            r23 = r5
            r5 = 2131492992(0x7f0c0080, float:1.8609452E38)
            goto L_0x028e
        L_0x0286:
            r17 = 2131492991(0x7f0c007f, float:1.860945E38)
            r23 = r5
            r5 = 2131492991(0x7f0c007f, float:1.860945E38)
        L_0x028e:
            r10.<init>(r12, r5)
            r5 = r10
            r10 = 2131297002(0x7f0902ea, float:1.8211937E38)
            r5.setTextViewText(r10, r1)
            r10 = 1
            boolean r12 = android.text.TextUtils.isEmpty(r27)
            r1 = 2131297327(0x7f09042f, float:1.8212596E38)
            if (r12 == 0) goto L_0x02ea
            r12 = 8
            r5.setViewVisibility(r1, r12)
            r1 = 0
            int r10 = im.bclpbkiauv.messenger.UserConfig.getActivatedAccountsCount()
            r12 = 1
            if (r10 <= r12) goto L_0x02d8
            int r8 = r0.currentAccount
            im.bclpbkiauv.messenger.UserConfig r8 = im.bclpbkiauv.messenger.UserConfig.getInstance(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r8 = r8.getCurrentUser()
            java.lang.Object[] r12 = new java.lang.Object[r12]
            java.lang.String r10 = r8.first_name
            r16 = r1
            java.lang.String r1 = r8.last_name
            java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r10, r1)
            r10 = 0
            r12[r10] = r1
            java.lang.String r1 = "VoipInCallBrandingWithName"
            r10 = 2131694701(0x7f0f146d, float:1.9018566E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r1, r10, r12)
            r10 = 2131297408(0x7f090480, float:1.821276E38)
            r5.setTextViewText(r10, r1)
            goto L_0x02e7
        L_0x02d8:
            r16 = r1
            r10 = 2131297408(0x7f090480, float:1.821276E38)
            r1 = 2131694700(0x7f0f146c, float:1.9018564E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.getString(r8, r1)
            r5.setTextViewText(r10, r1)
        L_0x02e7:
            r1 = r16
            goto L_0x032e
        L_0x02ea:
            int r8 = im.bclpbkiauv.messenger.UserConfig.getActivatedAccountsCount()
            r12 = 1
            if (r8 <= r12) goto L_0x031c
            int r8 = r0.currentAccount
            im.bclpbkiauv.messenger.UserConfig r8 = im.bclpbkiauv.messenger.UserConfig.getInstance(r8)
            im.bclpbkiauv.tgnet.TLRPC$User r8 = r8.getCurrentUser()
            java.lang.Object[] r12 = new java.lang.Object[r12]
            java.lang.String r1 = r8.first_name
            r24 = r10
            java.lang.String r10 = r8.last_name
            java.lang.String r1 = im.bclpbkiauv.messenger.ContactsController.formatName(r1, r10)
            r10 = 0
            r12[r10] = r1
            java.lang.String r1 = "VoipAnsweringAsAccount"
            r10 = 2131694683(0x7f0f145b, float:1.901853E38)
            java.lang.String r1 = im.bclpbkiauv.messenger.LocaleController.formatString(r1, r10, r12)
            r10 = 2131297327(0x7f09042f, float:1.8212596E38)
            r5.setTextViewText(r10, r1)
            r10 = r24
            goto L_0x0327
        L_0x031c:
            r24 = r10
            r10 = 2131297327(0x7f09042f, float:1.8212596E38)
            r1 = 8
            r5.setViewVisibility(r10, r1)
            r10 = 0
        L_0x0327:
            r1 = 2131297408(0x7f090480, float:1.821276E38)
            r5.setTextViewText(r1, r2)
            r1 = r10
        L_0x032e:
            android.graphics.Bitmap r8 = r0.getRoundAvatarBitmap(r3)
            r10 = 2131296369(0x7f090071, float:1.8210653E38)
            r12 = 2131694682(0x7f0f145a, float:1.9018527E38)
            java.lang.String r9 = im.bclpbkiauv.messenger.LocaleController.getString(r9, r12)
            r5.setTextViewText(r10, r9)
            r9 = 2131296513(0x7f090101, float:1.8210945E38)
            r10 = 2131694690(0x7f0f1462, float:1.9018544E38)
            java.lang.String r10 = im.bclpbkiauv.messenger.LocaleController.getString(r11, r10)
            r5.setTextViewText(r9, r10)
            r9 = 2131297051(0x7f09031b, float:1.8212036E38)
            r5.setImageViewBitmap(r9, r8)
            r9 = 2131296368(0x7f090070, float:1.821065E38)
            r5.setOnClickPendingIntent(r9, r7)
            r9 = 2131296512(0x7f090100, float:1.8210943E38)
            r5.setOnClickPendingIntent(r9, r13)
            r6.setLargeIcon(r8)
            r4.bigContentView = r5
            r4.headsUpContentView = r5
            goto L_0x0368
        L_0x0366:
            r23 = r5
        L_0x0368:
            r1 = 202(0xca, float:2.83E-43)
            r0.startForeground(r1, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.messenger.voip.VoIPBaseService.showIncomingNotification(java.lang.String, java.lang.CharSequence, im.bclpbkiauv.tgnet.TLObject, java.util.List, int, java.lang.Class):void");
    }

    /* access modifiers changed from: protected */
    public void callFailed(int errorCode) {
        CallConnection callConnection;
        SoundPool soundPool2;
        try {
            throw new Exception("Call " + getCallID() + " failed with error code " + errorCode);
        } catch (Exception x) {
            FileLog.e((Throwable) x);
            this.lastError = errorCode;
            dispatchStateChanged(4);
            if (!(errorCode == -3 || (soundPool2 = this.soundPool) == null)) {
                this.playingSound = true;
                soundPool2.play(this.spFailedID, 1.0f, 1.0f, 0, 0, 1.0f);
                AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 1000);
            }
            if (USE_CONNECTION_SERVICE && (callConnection = this.systemCallConnection) != null) {
                callConnection.setDisconnected(new DisconnectCause(1));
                this.systemCallConnection.destroy();
                this.systemCallConnection = null;
            }
            stopSelf();
        }
    }

    /* access modifiers changed from: package-private */
    public void callFailedFromConnectionService() {
        if (this.isOutgoing) {
            callFailed(-5);
        } else {
            hangUp();
        }
    }

    public void onConnectionStateChanged(int newState) {
        if (newState == 4) {
            callFailed();
            return;
        }
        if (newState == 3) {
            Runnable runnable = this.connectingSoundRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.connectingSoundRunnable = null;
            }
            int i = this.spPlayID;
            if (i != 0) {
                this.soundPool.stop(i);
                this.spPlayID = 0;
            }
            if (!this.wasEstablished) {
                this.wasEstablished = true;
                if (!this.isProximityNear) {
                    Vibrator vibrator2 = (Vibrator) getSystemService("vibrator");
                    if (vibrator2.hasVibrator()) {
                        vibrator2.vibrate(100);
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (VoIPBaseService.this.controller != null) {
                            StatsController.getInstance(VoIPBaseService.this.currentAccount).incrementTotalCallsTime(VoIPBaseService.this.getStatsNetworkType(), 5);
                            AndroidUtilities.runOnUIThread(this, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                    }
                }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                if (this.isOutgoing) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(getStatsNetworkType(), 0, 1);
                } else {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(getStatsNetworkType(), 0, 1);
                }
            }
        }
        if (newState == 5) {
            int i2 = this.spPlayID;
            if (i2 != 0) {
                this.soundPool.stop(i2);
            }
            this.spPlayID = this.soundPool.play(this.spConnectingId, 1.0f, 1.0f, 0, -1, 1.0f);
        }
        dispatchStateChanged(newState);
    }

    public void onSignalBarCountChanged(int newCount) {
        this.signalBarCount = newCount;
        for (int a = 0; a < this.stateListeners.size(); a++) {
            this.stateListeners.get(a).onSignalBarsCountChanged(newCount);
        }
    }

    /* access modifiers changed from: protected */
    public void callEnded() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("Call " + getCallID() + " ended");
        }
        dispatchStateChanged(11);
        long j = 700;
        if (this.needPlayEndSound) {
            this.playingSound = true;
            this.soundPool.play(this.spEndId, 1.0f, 1.0f, 0, 0, 1.0f);
            AndroidUtilities.runOnUIThread(this.afterSoundRunnable, 700);
        }
        Runnable runnable = this.timeoutRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.timeoutRunnable = null;
        }
        if (!this.needPlayEndSound) {
            j = 0;
        }
        endConnectionServiceCall(j);
        stopSelf();
    }

    /* access modifiers changed from: protected */
    public void endConnectionServiceCall(long delay) {
        if (USE_CONNECTION_SERVICE) {
            Runnable r = new Runnable() {
                public void run() {
                    if (VoIPBaseService.this.systemCallConnection != null) {
                        int i = VoIPBaseService.this.callDiscardReason;
                        int i2 = 2;
                        if (i == 1) {
                            CallConnection callConnection = VoIPBaseService.this.systemCallConnection;
                            if (!VoIPBaseService.this.isOutgoing) {
                                i2 = 6;
                            }
                            callConnection.setDisconnected(new DisconnectCause(i2));
                        } else if (i != 2) {
                            int i3 = 4;
                            if (i == 3) {
                                CallConnection callConnection2 = VoIPBaseService.this.systemCallConnection;
                                if (!VoIPBaseService.this.isOutgoing) {
                                    i3 = 5;
                                }
                                callConnection2.setDisconnected(new DisconnectCause(i3));
                            } else if (i != 4) {
                                VoIPBaseService.this.systemCallConnection.setDisconnected(new DisconnectCause(3));
                            } else {
                                VoIPBaseService.this.systemCallConnection.setDisconnected(new DisconnectCause(7));
                            }
                        } else {
                            VoIPBaseService.this.systemCallConnection.setDisconnected(new DisconnectCause(1));
                        }
                        VoIPBaseService.this.systemCallConnection.destroy();
                        VoIPBaseService.this.systemCallConnection = null;
                    }
                }
            };
            if (delay > 0) {
                AndroidUtilities.runOnUIThread(r, delay);
            } else {
                r.run();
            }
        }
    }

    public boolean isOutgoing() {
        return this.isOutgoing;
    }

    public void handleNotificationAction(Intent intent) {
        if ((getPackageName() + ".END_CALL").equals(intent.getAction())) {
            stopForeground(true);
            hangUp();
            return;
        }
        if ((getPackageName() + ".DECLINE_CALL").equals(intent.getAction())) {
            stopForeground(true);
            declineIncomingCall(4, (Runnable) null);
            return;
        }
        if ((getPackageName() + ".ANSWER_CALL").equals(intent.getAction())) {
            acceptIncomingCallFromNotification();
        }
    }

    /* access modifiers changed from: private */
    public void acceptIncomingCallFromNotification() {
        showNotification();
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            acceptIncomingCall();
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, getUIActivityClass()).addFlags(805306368), 0).send();
            } catch (Exception x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting incall activity", (Throwable) x);
                }
            }
        } else {
            try {
                PendingIntent.getActivity(this, 0, new Intent(this, VoIPPermissionActivity.class).addFlags(C.ENCODING_PCM_MU_LAW), 0).send();
            } catch (Exception x2) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("Error starting permission activity", (Throwable) x2);
                }
            }
        }
    }

    public void updateOutputGainControlState() {
        if (this.controller != null && this.controllerStarted) {
            int i = 0;
            if (!USE_CONNECTION_SERVICE) {
                AudioManager am = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                this.controller.setAudioOutputGainControlEnabled(hasEarpiece() && !am.isSpeakerphoneOn() && !am.isBluetoothScoOn() && !this.isHeadsetPlugged);
                VoIPController voIPController = this.controller;
                if (!this.isHeadsetPlugged && (!hasEarpiece() || am.isSpeakerphoneOn() || am.isBluetoothScoOn() || this.isHeadsetPlugged)) {
                    i = 1;
                }
                voIPController.setEchoCancellationStrength(i);
                return;
            }
            boolean isEarpiece = this.systemCallConnection.getCallAudioState().getRoute() == 1;
            this.controller.setAudioOutputGainControlEnabled(isEarpiece);
            VoIPController voIPController2 = this.controller;
            if (!isEarpiece) {
                i = 1;
            }
            voIPController2.setEchoCancellationStrength(i);
        }
    }

    public int getAccount() {
        return this.currentAccount;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.appDidLogout) {
            callEnded();
        }
    }

    public static boolean isAnyKindOfCallActive() {
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getCallState() == 15) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean isFinished() {
        int i = this.currentState;
        return i == 11 || i == 4;
    }

    /* access modifiers changed from: protected */
    public boolean isRinging() {
        return false;
    }

    /* access modifiers changed from: protected */
    public PhoneAccountHandle addAccountToTelecomManager() {
        TLRPC.User self = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        ComponentName componentName = new ComponentName(this, AppConnectionService.class);
        PhoneAccountHandle handle = new PhoneAccountHandle(componentName, "" + self.id);
        ((TelecomManager) getSystemService("telecom")).registerPhoneAccount(new PhoneAccount.Builder(handle, ContactsController.formatName(self.first_name, self.last_name)).setCapabilities(2048).setIcon(Icon.createWithResource(this, R.mipmap.ic_logo)).setHighlightColor(-13851168).addSupportedUriScheme("sip").build());
        return handle;
    }

    private static boolean isDeviceCompatibleWithConnectionServiceAPI() {
        if (Build.VERSION.SDK_INT < 26) {
            return false;
        }
        if ("angler".equals(Build.PRODUCT) || "bullhead".equals(Build.PRODUCT) || "sailfish".equals(Build.PRODUCT) || "marlin".equals(Build.PRODUCT) || "walleye".equals(Build.PRODUCT) || "taimen".equals(Build.PRODUCT) || "blueline".equals(Build.PRODUCT) || "crosshatch".equals(Build.PRODUCT) || MessagesController.getGlobalMainSettings().getBoolean("dbg_force_connection_service", false)) {
            return true;
        }
        return false;
    }

    public class CallConnection extends Connection {
        public CallConnection() {
            setConnectionProperties(128);
            setAudioModeIsVoip(true);
        }

        public void onCallAudioStateChanged(CallAudioState state) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService call audio state changed: " + state);
            }
            Iterator<StateListener> it = VoIPBaseService.this.stateListeners.iterator();
            while (it.hasNext()) {
                it.next().onAudioSettingsChanged();
            }
        }

        public void onDisconnect() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onDisconnect");
            }
            setDisconnected(new DisconnectCause(2));
            destroy();
            VoIPBaseService.this.systemCallConnection = null;
            VoIPBaseService.this.hangUp();
        }

        public void onAnswer() {
            VoIPBaseService.this.acceptIncomingCallFromNotification();
        }

        public void onReject() {
            VoIPBaseService.this.needPlayEndSound = false;
            VoIPBaseService.this.declineIncomingCall(1, (Runnable) null);
        }

        public void onShowIncomingCallUi() {
            VoIPBaseService.this.startRinging();
        }

        public void onStateChanged(int state) {
            super.onStateChanged(state);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onStateChanged " + stateToString(state));
            }
            if (state == 4) {
                ContactsController.getInstance(VoIPBaseService.this.currentAccount).deleteConnectionServiceContact();
                VoIPBaseService.this.didDeleteConnectionServiceContact = true;
            }
        }

        public void onCallEvent(String event, Bundle extras) {
            super.onCallEvent(event, extras);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ConnectionService onCallEvent " + event);
            }
        }

        public void onSilence() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("onSlience");
            }
            VoIPBaseService.this.stopRinging();
        }
    }
}
