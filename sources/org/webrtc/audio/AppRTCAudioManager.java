package org.webrtc.audio;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.aliyun.sophonsdk.R;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.litesuits.orm.db.assit.SQLBuilder;
import im.bclpbkiauv.messenger.voip.VoIPBaseService;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.webrtc.ali.ThreadUtils;
import org.webrtc.ali.voiceengine.WebRtcAudioManager;
import org.webrtc.audio.AppRTCBluetoothManager;
import org.webrtc.utils.AlivcLog;
import org.webrtc.utils.AppRTCUtils;
import org.webrtc.utils.DeviceConstants;

public class AppRTCAudioManager {
    private static final String SPEAKERPHONE_AUTO = "auto";
    private static final String SPEAKERPHONE_FALSE = "false";
    private static final String SPEAKERPHONE_TRUE = "true";
    private static final String TAG = "AppRTCAudioManager";
    private AudioManagerState amState;
    private WeakReference<Context> apprtcContext;
    private Set<AudioDevice> audioDevices = new HashSet();
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    /* access modifiers changed from: private */
    public AudioManager audioManager;
    /* access modifiers changed from: private */
    public AudioManagerEvents audioManagerEvents;
    private final AppRTCBluetoothManager bluetoothManager;
    private AudioDevice defaultAudioDevice;
    /* access modifiers changed from: private */
    public boolean hasWiredHeadset = false;
    private Application.ActivityLifecycleCallbacks mActivityCallbacks = new Application.ActivityLifecycleCallbacks() {
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
            if (AppRTCAudioManager.this.mAudioFocusLost) {
                boolean unused = AppRTCAudioManager.this.mAudioFocusLost = false;
                boolean unused2 = AppRTCAudioManager.this.mFromBackground = true;
                int unused3 = AppRTCAudioManager.this.requestAudioFocus();
                AppRTCAudioManager.this.updateAudioDeviceState(true);
                boolean unused4 = AppRTCAudioManager.this.mFromBackground = false;
            }
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
        }
    };
    /* access modifiers changed from: private */
    public boolean mAudioFocusLost = false;
    /* access modifiers changed from: private */
    public boolean mFromBackground = false;
    private boolean mIsBasicMusicMode = false;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            AlivcLog.i(AppRTCAudioManager.TAG, "CustomPhoneStateListener state: " + state + " incomingNumber: " + incomingNumber);
            if (AppRTCAudioManager.this.audioManagerEvents != null) {
                AppRTCAudioManager.this.audioManagerEvents.onPhoneStateChanged(state);
            }
            if (state == 0) {
                AlivcLog.i(AppRTCAudioManager.TAG, "电话挂断");
                if (AppRTCAudioManager.this.audioManager != null) {
                    AppRTCAudioManager appRTCAudioManager = AppRTCAudioManager.this;
                    appRTCAudioManager.setSpeakerphoneOn(appRTCAudioManager.mSpeakerOnWhenRinging);
                }
            } else if (state == 1) {
                AlivcLog.i(AppRTCAudioManager.TAG, "电话响铃");
                if (AppRTCAudioManager.this.audioManager != null) {
                    AppRTCAudioManager appRTCAudioManager2 = AppRTCAudioManager.this;
                    boolean unused = appRTCAudioManager2.mSpeakerOnWhenRinging = appRTCAudioManager2.audioManager.isSpeakerphoneOn();
                }
            } else if (state == 2) {
                AlivcLog.i(AppRTCAudioManager.TAG, "来电接通 或者 去电，去电接通  但是没法区分");
            }
        }
    };
    private volatile boolean mReceiverTag = false;
    /* access modifiers changed from: private */
    public boolean mSpeakerOnWhenRinging;
    private AppRTCProximitySensor proximitySensor = null;
    private int savedAudioMode = -2;
    private boolean savedIsMicrophoneMute = false;
    private boolean savedIsSpeakerPhoneOn = false;
    private volatile AudioDevice selectedAudioDevice;
    private TelephonyManager telephonyManager;
    private final String useSpeakerphone;
    private AudioDevice userSelectedAudioDevice;
    private BroadcastReceiver wiredHeadsetReceiver;

    public enum AudioDevice {
        SPEAKER_PHONE,
        WIRED_HEADSET,
        EARPIECE,
        BLUETOOTH,
        NONE
    }

    public interface AudioManagerEvents {
        void onAudioDeviceChanged(AudioDevice audioDevice, Set<AudioDevice> set);

        void onPhoneStateChanged(int i);
    }

    public enum AudioManagerState {
        UNINITIALIZED,
        PREINITIALIZED,
        RUNNING
    }

    /* access modifiers changed from: private */
    public void onProximitySensorChangedState() {
        if (!this.useSpeakerphone.equals(SPEAKERPHONE_AUTO) || this.audioDevices.size() != 2 || !this.audioDevices.contains(AudioDevice.EARPIECE) || !this.audioDevices.contains(AudioDevice.SPEAKER_PHONE)) {
            return;
        }
        if (this.proximitySensor.sensorReportsNearState()) {
            setAudioDeviceInternal(AudioDevice.EARPIECE);
        } else {
            setAudioDeviceInternal(AudioDevice.SPEAKER_PHONE);
        }
    }

    private class WiredHeadsetReceiver extends BroadcastReceiver {
        private static final int HAS_MIC = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int STATE_UNPLUGGED = 0;

        private WiredHeadsetReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean z = false;
            int state = intent.getIntExtra(RemoteConfigConstants.ResponseFieldKey.STATE, 0);
            int microphone = intent.getIntExtra("microphone", 0);
            String name = intent.getStringExtra("name");
            StringBuilder sb = new StringBuilder();
            sb.append("WiredHeadsetReceiver.onReceive");
            sb.append(AppRTCUtils.getThreadInfo());
            sb.append(": a=");
            sb.append(intent.getAction());
            sb.append(", s=");
            sb.append(state == 0 ? "unplugged" : "plugged");
            sb.append(", m=");
            sb.append(microphone == 1 ? "mic" : "no mic");
            sb.append(", n=");
            sb.append(name);
            sb.append(", sb=");
            sb.append(isInitialStickyBroadcast());
            AlivcLog.i(AppRTCAudioManager.TAG, sb.toString());
            AppRTCAudioManager appRTCAudioManager = AppRTCAudioManager.this;
            if (state == 1) {
                z = true;
            }
            boolean unused = appRTCAudioManager.hasWiredHeadset = z;
            AppRTCAudioManager.this.updateAudioDeviceState();
        }
    }

    public static AppRTCAudioManager create(Context context) {
        return new AppRTCAudioManager(context);
    }

    private AppRTCAudioManager(Context context) {
        AlivcLog.i(TAG, "ctor");
        ThreadUtils.checkIsOnMainThread();
        WeakReference<Context> weakReference = new WeakReference<>(context);
        this.apprtcContext = weakReference;
        this.audioManager = (AudioManager) ((Context) weakReference.get()).getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        this.telephonyManager = (TelephonyManager) ((Context) this.apprtcContext.get()).getSystemService("phone");
        this.bluetoothManager = AppRTCBluetoothManager.create(context, this);
        this.wiredHeadsetReceiver = new WiredHeadsetReceiver();
        this.amState = AudioManagerState.UNINITIALIZED;
        this.useSpeakerphone = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_speakerphone_key), context.getString(R.string.pref_speakerphone_default));
        AlivcLog.i(TAG, "useSpeakerphone: " + this.useSpeakerphone);
        if (this.useSpeakerphone.equals(SPEAKERPHONE_FALSE)) {
            this.defaultAudioDevice = AudioDevice.EARPIECE;
        } else {
            this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
        }
        this.proximitySensor = AppRTCProximitySensor.create(context, new Runnable() {
            public void run() {
                AppRTCAudioManager.this.onProximitySensorChangedState();
            }
        });
        AlivcLog.i(TAG, "defaultAudioDevice: " + this.defaultAudioDevice);
        AppRTCUtils.logDeviceInfo(TAG);
    }

    /* access modifiers changed from: private */
    public int requestAudioFocus() {
        if (this.audioFocusChangeListener == null) {
            this.audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    String typeOfChange;
                    if (focusChange == -3) {
                        typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    } else if (focusChange == -2) {
                        typeOfChange = "AUDIOFOCUS_LOSS_TRANSIENT";
                        boolean unused = AppRTCAudioManager.this.mAudioFocusLost = true;
                    } else if (focusChange == -1) {
                        typeOfChange = "AUDIOFOCUS_LOSS";
                        boolean unused2 = AppRTCAudioManager.this.mAudioFocusLost = true;
                    } else if (focusChange == 1) {
                        typeOfChange = "AUDIOFOCUS_GAIN";
                    } else if (focusChange == 2) {
                        typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT";
                    } else if (focusChange == 3) {
                        typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK";
                    } else if (focusChange != 4) {
                        typeOfChange = "AUDIOFOCUS_INVALID";
                    } else {
                        typeOfChange = "AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE";
                    }
                    AlivcLog.i(AppRTCAudioManager.TAG, "onAudioFocusChange: " + typeOfChange);
                }
            };
        }
        AudioManager audioManager2 = this.audioManager;
        if (audioManager2 != null) {
            return audioManager2.requestAudioFocus(this.audioFocusChangeListener, 0, 2);
        }
        return 0;
    }

    public void start(AudioManagerEvents audioManagerEvents2) {
        AlivcLog.i(TAG, TtmlNode.START);
        ThreadUtils.checkIsOnMainThread();
        if (this.amState == AudioManagerState.RUNNING) {
            AlivcLog.e(TAG, "AudioManager is already active");
            return;
        }
        AlivcLog.i(TAG, "AudioManager starts...");
        this.audioManagerEvents = audioManagerEvents2;
        this.amState = AudioManagerState.RUNNING;
        this.savedAudioMode = this.audioManager.getMode();
        this.savedIsSpeakerPhoneOn = this.audioManager.isSpeakerphoneOn();
        this.savedIsMicrophoneMute = this.audioManager.isMicrophoneMute();
        this.hasWiredHeadset = hasWiredHeadset();
        if (requestAudioFocus() == 1) {
            AlivcLog.i(TAG, "Audio focus request granted for VOICE_CALL streams");
        } else {
            AlivcLog.e(TAG, "Audio focus request failed");
        }
        this.audioManager.setMode(3);
        setMicrophoneMute(false);
        this.userSelectedAudioDevice = AudioDevice.NONE;
        this.selectedAudioDevice = AudioDevice.NONE;
        this.audioDevices.clear();
        this.bluetoothManager.start();
        updateAudioDeviceState();
        if (!this.mReceiverTag) {
            this.mReceiverTag = true;
            registerReceiver(this.wiredHeadsetReceiver, new IntentFilter(VoIPBaseService.ACTION_HEADSET_PLUG));
        }
        this.mAudioFocusLost = false;
        WeakReference<Context> weakReference = this.apprtcContext;
        if (!(weakReference == null || weakReference.get() == null || !(((Context) this.apprtcContext.get()).getApplicationContext() instanceof Application))) {
            ((Application) ((Context) this.apprtcContext.get()).getApplicationContext()).registerActivityLifecycleCallbacks(this.mActivityCallbacks);
        }
        if (DeviceConstants.shouldListenerPhoneState()) {
            this.telephonyManager.listen(this.mPhoneStateListener, 32);
        }
        AlivcLog.i(TAG, "AudioManager started");
    }

    public void stop() {
        AlivcLog.i(TAG, "stop");
        this.mAudioFocusLost = false;
        WeakReference<Context> weakReference = this.apprtcContext;
        if (!(weakReference == null || weakReference.get() == null || !(((Context) this.apprtcContext.get()).getApplicationContext() instanceof Application))) {
            ((Application) ((Context) this.apprtcContext.get()).getApplicationContext()).unregisterActivityLifecycleCallbacks(this.mActivityCallbacks);
        }
        ThreadUtils.checkIsOnMainThread();
        if (this.amState != AudioManagerState.RUNNING) {
            AlivcLog.e(TAG, "Trying to stop AudioManager in incorrect state: " + this.amState);
            return;
        }
        this.amState = AudioManagerState.UNINITIALIZED;
        if (this.mReceiverTag) {
            this.mReceiverTag = false;
            unregisterReceiver(this.wiredHeadsetReceiver);
        }
        this.bluetoothManager.stop();
        setSpeakerphoneOn(this.savedIsSpeakerPhoneOn);
        setMicrophoneMute(this.savedIsMicrophoneMute);
        this.audioManager.setMode(this.savedAudioMode);
        this.audioManager.abandonAudioFocus(this.audioFocusChangeListener);
        this.audioFocusChangeListener = null;
        AlivcLog.i(TAG, "Abandoned audio focus for VOICE_CALL streams");
        AppRTCProximitySensor appRTCProximitySensor = this.proximitySensor;
        if (appRTCProximitySensor != null) {
            appRTCProximitySensor.stop();
            this.proximitySensor = null;
        }
        this.audioManagerEvents = null;
        if (DeviceConstants.shouldListenerPhoneState()) {
            this.telephonyManager.listen(this.mPhoneStateListener, 0);
        }
        AlivcLog.i(TAG, "AudioManager stopped");
    }

    private void setAudioDeviceInternal(AudioDevice device) {
        AlivcLog.i(TAG, "setAudioDeviceInternal(device=" + device + SQLBuilder.PARENTHESES_RIGHT);
        int portType = 0;
        AppRTCUtils.assertIsTrue(this.audioDevices.contains(device));
        int i = AnonymousClass5.$SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice[device.ordinal()];
        if (i == 1) {
            setSpeakerphoneOn(true);
        } else if (i == 2) {
            setSpeakerphoneOn(false);
        } else if (i == 3) {
            setSpeakerphoneOn(false);
        } else if (i != 4) {
            AlivcLog.e(TAG, "Invalid audio device selection");
        } else {
            setSpeakerphoneOn(false);
            portType = 1;
        }
        this.selectedAudioDevice = device;
        AlivcLog.i(TAG, "setAudioDeviceInternal: CurrentPort type: " + portType);
    }

    /* renamed from: org.webrtc.audio.AppRTCAudioManager$5  reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice;

        static {
            int[] iArr = new int[AudioDevice.values().length];
            $SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice = iArr;
            try {
                iArr[AudioDevice.SPEAKER_PHONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice[AudioDevice.EARPIECE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice[AudioDevice.WIRED_HEADSET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice[AudioDevice.BLUETOOTH.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public void setDefaultAudioDevice(AudioDevice defaultDevice) {
        ThreadUtils.checkIsOnMainThread();
        int i = AnonymousClass5.$SwitchMap$org$webrtc$audio$AppRTCAudioManager$AudioDevice[defaultDevice.ordinal()];
        if (i == 1) {
            this.defaultAudioDevice = defaultDevice;
        } else if (i != 2) {
            AlivcLog.e(TAG, "Invalid default audio device selection");
        } else if (hasEarpiece()) {
            this.defaultAudioDevice = defaultDevice;
        } else {
            this.defaultAudioDevice = AudioDevice.SPEAKER_PHONE;
        }
        AlivcLog.i(TAG, "setDefaultAudioDevice(device=" + this.defaultAudioDevice + SQLBuilder.PARENTHESES_RIGHT);
        updateAudioDeviceState();
    }

    public void selectAudioDevice(AudioDevice device) {
        ThreadUtils.checkIsOnMainThread();
        if (!this.audioDevices.contains(device)) {
            AlivcLog.e(TAG, "Can not select " + device + " from available " + this.audioDevices);
        }
        this.userSelectedAudioDevice = device;
        updateAudioDeviceState();
    }

    public Set<AudioDevice> getAudioDevices() {
        ThreadUtils.checkIsOnMainThread();
        return Collections.unmodifiableSet(new HashSet(this.audioDevices));
    }

    public AudioDevice getSelectedAudioDevice() {
        return this.selectedAudioDevice;
    }

    private void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        Context context = (Context) this.apprtcContext.get();
        if (context != null) {
            context.registerReceiver(receiver, filter);
        }
    }

    private void unregisterReceiver(BroadcastReceiver receiver) {
        Context context = (Context) this.apprtcContext.get();
        if (context != null) {
            context.unregisterReceiver(receiver);
        }
    }

    public void setBasicMusicMode(boolean basicMusicMode) {
        this.mIsBasicMusicMode = basicMusicMode;
    }

    public void setBasicMusicModeVolume() {
        int protectVolume;
        if (this.mIsBasicMusicMode && this.audioManager.getStreamVolume(3) >= (protectVolume = (int) (((double) this.audioManager.getStreamMaxVolume(3)) * 0.7d))) {
            this.audioManager.setStreamVolume(3, protectVolume, 0);
        }
    }

    /* access modifiers changed from: private */
    public void setSpeakerphoneOn(boolean on) {
        this.mSpeakerOnWhenRinging = on;
        boolean wasOn = this.audioManager.isSpeakerphoneOn();
        if (this.mFromBackground || wasOn != on) {
            if (this.mIsBasicMusicMode) {
                if (on) {
                    this.audioManager.setMode(0);
                    int protectVolume = (int) (((double) this.audioManager.getStreamMaxVolume(3)) * 0.7d);
                    if (this.audioManager.getStreamVolume(3) >= protectVolume) {
                        this.audioManager.setStreamVolume(3, protectVolume, 0);
                    }
                } else {
                    this.audioManager.setMode(3);
                    this.audioManager.setStreamVolume(0, this.audioManager.getStreamMaxVolume(0), 0);
                }
            } else if (DeviceConstants.shouldSetMode()) {
                if (on) {
                    this.audioManager.setMode(WebRtcAudioManager.sMode);
                } else {
                    this.audioManager.setMode(3);
                }
            }
            this.audioManager.setSpeakerphoneOn(on);
        }
    }

    private void setMicrophoneMute(boolean on) {
        if (this.audioManager.isMicrophoneMute() != on) {
            this.audioManager.setMicrophoneMute(on);
        }
    }

    private boolean hasEarpiece() {
        return ((Context) this.apprtcContext.get()).getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        return this.audioManager.isWiredHeadsetOn();
    }

    public void updateAudioDeviceState() {
        updateAudioDeviceState(false);
    }

    public void updateAudioDeviceState(boolean audioFocusSetUpdated) {
        AudioDevice newAudioDevice;
        ThreadUtils.checkIsOnMainThread();
        AlivcLog.i(TAG, "--- updateAudioDeviceState: wired headset=" + this.hasWiredHeadset + ", BT state=" + this.bluetoothManager.getState());
        AlivcLog.i(TAG, "Device status: available=" + this.audioDevices + ", selected=" + this.selectedAudioDevice + ", user selected=" + this.userSelectedAudioDevice);
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_DISCONNECTING) {
            this.bluetoothManager.updateDevice();
        }
        Set<AudioDevice> newAudioDevices = new HashSet<>();
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE) {
            newAudioDevices.add(AudioDevice.BLUETOOTH);
        }
        if (this.hasWiredHeadset) {
            newAudioDevices.add(AudioDevice.WIRED_HEADSET);
        } else {
            newAudioDevices.add(AudioDevice.SPEAKER_PHONE);
            if (hasEarpiece()) {
                newAudioDevices.add(AudioDevice.EARPIECE);
            }
        }
        boolean needBluetoothAudioStop = true;
        boolean audioDeviceSetUpdated = !this.audioDevices.equals(newAudioDevices);
        this.audioDevices = newAudioDevices;
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_UNAVAILABLE && this.userSelectedAudioDevice == AudioDevice.BLUETOOTH) {
            this.userSelectedAudioDevice = AudioDevice.NONE;
        }
        if (this.hasWiredHeadset && this.userSelectedAudioDevice == AudioDevice.SPEAKER_PHONE) {
            this.userSelectedAudioDevice = AudioDevice.WIRED_HEADSET;
        }
        if (!this.hasWiredHeadset && this.userSelectedAudioDevice == AudioDevice.WIRED_HEADSET) {
            this.userSelectedAudioDevice = AudioDevice.SPEAKER_PHONE;
        }
        boolean needBluetoothAudioStart = this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE && (this.userSelectedAudioDevice == AudioDevice.NONE || this.userSelectedAudioDevice == AudioDevice.BLUETOOTH);
        if (!(this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING) || this.userSelectedAudioDevice == AudioDevice.NONE || this.userSelectedAudioDevice == AudioDevice.BLUETOOTH) {
            needBluetoothAudioStop = false;
        }
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.HEADSET_AVAILABLE || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTING || this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED) {
            AlivcLog.i(TAG, "Need BT audio: start=" + needBluetoothAudioStart + ", stop=" + needBluetoothAudioStop + ", BT state=" + this.bluetoothManager.getState());
        }
        if (needBluetoothAudioStop) {
            this.bluetoothManager.stopScoAudio();
            this.bluetoothManager.updateDevice();
        }
        if (needBluetoothAudioStart && !needBluetoothAudioStop && !this.bluetoothManager.startScoAudio()) {
            this.audioDevices.remove(AudioDevice.BLUETOOTH);
            audioDeviceSetUpdated = true;
        }
        AudioDevice audioDevice = this.selectedAudioDevice;
        if (this.bluetoothManager.getState() == AppRTCBluetoothManager.State.SCO_CONNECTED) {
            newAudioDevice = AudioDevice.BLUETOOTH;
        } else if (this.hasWiredHeadset) {
            newAudioDevice = AudioDevice.WIRED_HEADSET;
        } else {
            newAudioDevice = this.defaultAudioDevice;
        }
        if (newAudioDevice != this.selectedAudioDevice || audioDeviceSetUpdated || audioFocusSetUpdated) {
            setAudioDeviceInternal(newAudioDevice);
            AlivcLog.i(TAG, "New device status: available=" + this.audioDevices + ", selected=" + newAudioDevice);
            AudioManagerEvents audioManagerEvents2 = this.audioManagerEvents;
            if (audioManagerEvents2 != null) {
                audioManagerEvents2.onAudioDeviceChanged(this.selectedAudioDevice, this.audioDevices);
            }
        }
        AlivcLog.i(TAG, "--- updateAudioDeviceState done");
    }
}
