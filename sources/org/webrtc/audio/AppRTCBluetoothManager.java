package org.webrtc.audio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.List;
import java.util.Set;
import org.webrtc.ali.ThreadUtils;
import org.webrtc.utils.AppRTCUtils;

public class AppRTCBluetoothManager {
    private static final int BLUETOOTH_SCO_TIMEOUT_MS = 4000;
    private static final int MAX_SCO_CONNECTION_ATTEMPTS = 2;
    private static final String TAG = "AppRTCBluetoothManager";
    private final AppRTCAudioManager apprtcAudioManager;
    private final Context apprtcContext;
    private final AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    /* access modifiers changed from: private */
    public BluetoothDevice bluetoothDevice;
    /* access modifiers changed from: private */
    public BluetoothHeadset bluetoothHeadset;
    private final BroadcastReceiver bluetoothHeadsetReceiver;
    private final BluetoothProfile.ServiceListener bluetoothServiceListener;
    /* access modifiers changed from: private */
    public State bluetoothState;
    private final Runnable bluetoothTimeoutRunnable = new Runnable() {
        public void run() {
            AppRTCBluetoothManager.this.bluetoothTimeout();
        }
    };
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    int scoConnectionAttempts;

    public enum State {
        UNINITIALIZED,
        ERROR,
        HEADSET_UNAVAILABLE,
        HEADSET_AVAILABLE,
        SCO_DISCONNECTING,
        SCO_CONNECTING,
        SCO_CONNECTED
    }

    private class BluetoothServiceListener implements BluetoothProfile.ServiceListener {
        private BluetoothServiceListener() {
        }

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == 1 && AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                Log.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceConnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
                BluetoothHeadset unused = AppRTCBluetoothManager.this.bluetoothHeadset = (BluetoothHeadset) proxy;
                AppRTCBluetoothManager.this.updateAudioDeviceState();
                Log.d(AppRTCBluetoothManager.TAG, "onServiceConnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == 1 && AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                Log.d(AppRTCBluetoothManager.TAG, "BluetoothServiceListener.onServiceDisconnected: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
                AppRTCBluetoothManager.this.stopScoAudio();
                BluetoothHeadset unused = AppRTCBluetoothManager.this.bluetoothHeadset = null;
                BluetoothDevice unused2 = AppRTCBluetoothManager.this.bluetoothDevice = null;
                State unused3 = AppRTCBluetoothManager.this.bluetoothState = State.HEADSET_UNAVAILABLE;
                AppRTCBluetoothManager.this.updateAudioDeviceState();
                Log.d(AppRTCBluetoothManager.TAG, "onServiceDisconnected done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }
    }

    private class BluetoothHeadsetBroadcastReceiver extends BroadcastReceiver {
        private BluetoothHeadsetBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (AppRTCBluetoothManager.this.bluetoothState != State.UNINITIALIZED) {
                String action = intent.getAction();
                if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
                    int state = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
                    Log.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_CONNECTION_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(state) + ", sb=" + isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                    if (state == 2) {
                        AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                        AppRTCBluetoothManager.this.mHandler.postDelayed(new Runnable() {
                            public void run() {
                                AppRTCBluetoothManager.this.updateAudioDeviceState();
                            }
                        }, 1000);
                    } else if (!(state == 1 || state == 3 || state != 0)) {
                        AppRTCBluetoothManager.this.stopScoAudio();
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    }
                } else if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                    int state2 = intent.getIntExtra("android.bluetooth.profile.extra.STATE", 10);
                    Log.d(AppRTCBluetoothManager.TAG, "BluetoothHeadsetBroadcastReceiver.onReceive: a=ACTION_AUDIO_STATE_CHANGED, s=" + AppRTCBluetoothManager.this.stateToString(state2) + ", sb=" + isInitialStickyBroadcast() + ", BT state: " + AppRTCBluetoothManager.this.bluetoothState);
                    if (state2 == 12) {
                        AppRTCBluetoothManager.this.cancelTimer();
                        if (AppRTCBluetoothManager.this.bluetoothState == State.SCO_CONNECTING) {
                            Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connected");
                            State unused = AppRTCBluetoothManager.this.bluetoothState = State.SCO_CONNECTED;
                            AppRTCBluetoothManager.this.scoConnectionAttempts = 0;
                            AppRTCBluetoothManager.this.updateAudioDeviceState();
                        } else {
                            Log.w(AppRTCBluetoothManager.TAG, "Unexpected state BluetoothHeadset.STATE_AUDIO_CONNECTED");
                        }
                    } else if (state2 == 11) {
                        Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now connecting...");
                    } else if (state2 == 10) {
                        Log.d(AppRTCBluetoothManager.TAG, "+++ Bluetooth audio SCO is now disconnected");
                        if (isInitialStickyBroadcast()) {
                            Log.d(AppRTCBluetoothManager.TAG, "Ignore STATE_AUDIO_DISCONNECTED initial sticky broadcast.");
                            return;
                        }
                        AppRTCBluetoothManager.this.updateAudioDeviceState();
                    }
                }
                Log.d(AppRTCBluetoothManager.TAG, "onReceive done: BT state=" + AppRTCBluetoothManager.this.bluetoothState);
            }
        }
    }

    static AppRTCBluetoothManager create(Context context, AppRTCAudioManager audioManager2) {
        Log.d(TAG, "create" + AppRTCUtils.getThreadInfo());
        return new AppRTCBluetoothManager(context, audioManager2);
    }

    protected AppRTCBluetoothManager(Context context, AppRTCAudioManager audioManager2) {
        Log.d(TAG, "ctor");
        ThreadUtils.checkIsOnMainThread();
        this.apprtcContext = context;
        this.apprtcAudioManager = audioManager2;
        this.audioManager = getAudioManager(context);
        this.bluetoothState = State.UNINITIALIZED;
        this.bluetoothServiceListener = new BluetoothServiceListener();
        this.bluetoothHeadsetReceiver = new BluetoothHeadsetBroadcastReceiver();
    }

    public State getState() {
        ThreadUtils.checkIsOnMainThread();
        return this.bluetoothState;
    }

    public void start() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, TtmlNode.START);
        if (!hasPermission(this.apprtcContext, "android.permission.BLUETOOTH")) {
            Log.w(TAG, "Process (pid=" + Process.myPid() + ") lacks BLUETOOTH permission");
        } else if (this.bluetoothState != State.UNINITIALIZED) {
            Log.w(TAG, "Invalid BT state");
        } else {
            this.bluetoothHeadset = null;
            this.bluetoothDevice = null;
            this.scoConnectionAttempts = 0;
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            this.bluetoothAdapter = defaultAdapter;
            if (defaultAdapter == null) {
                Log.w(TAG, "Device does not support Bluetooth");
            } else if (!this.audioManager.isBluetoothScoAvailableOffCall()) {
                Log.e(TAG, "Bluetooth SCO audio is not available off call");
            } else {
                logBluetoothAdapterInfo(this.bluetoothAdapter);
                if (!getBluetoothProfileProxy(this.apprtcContext, this.bluetoothServiceListener, 1)) {
                    Log.e(TAG, "BluetoothAdapter.getProfileProxy(HEADSET) failed");
                    return;
                }
                IntentFilter bluetoothHeadsetFilter = new IntentFilter();
                bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
                bluetoothHeadsetFilter.addAction("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
                registerReceiver(this.bluetoothHeadsetReceiver, bluetoothHeadsetFilter);
                Log.d(TAG, "HEADSET profile state: " + stateToString(this.bluetoothAdapter.getProfileConnectionState(1)));
                Log.d(TAG, "Bluetooth proxy for headset profile has started");
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
                Log.d(TAG, "start done: BT state=" + this.bluetoothState);
            }
        }
    }

    public void stop() {
        ThreadUtils.checkIsOnMainThread();
        unregisterReceiver(this.bluetoothHeadsetReceiver);
        Log.d(TAG, "stop: BT state=" + this.bluetoothState);
        if (this.bluetoothAdapter != null) {
            stopScoAudio();
            if (this.bluetoothState != State.UNINITIALIZED) {
                cancelTimer();
                BluetoothHeadset bluetoothHeadset2 = this.bluetoothHeadset;
                if (bluetoothHeadset2 != null) {
                    this.bluetoothAdapter.closeProfileProxy(1, bluetoothHeadset2);
                    this.bluetoothHeadset = null;
                }
                this.bluetoothAdapter = null;
                this.bluetoothDevice = null;
                this.bluetoothState = State.UNINITIALIZED;
            }
        }
        Log.d(TAG, "stop done: BT state=" + this.bluetoothState);
    }

    public boolean startScoAudio() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "startSco: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + isScoOn());
        if (this.scoConnectionAttempts >= 2) {
            Log.e(TAG, "BT SCO connection fails - no more attempts");
            return false;
        } else if (this.bluetoothState != State.HEADSET_AVAILABLE) {
            Log.e(TAG, "BT SCO connection fails - no headset available");
            return false;
        } else {
            Log.d(TAG, "Starting Bluetooth SCO and waits for ACTION_AUDIO_STATE_CHANGED...");
            this.bluetoothState = State.SCO_CONNECTING;
            this.audioManager.startBluetoothSco();
            this.audioManager.setBluetoothScoOn(true);
            this.scoConnectionAttempts++;
            startTimer();
            Log.d(TAG, "startScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
            return true;
        }
    }

    public void stopScoAudio() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "stopScoAudio: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
        if (this.bluetoothState == State.SCO_CONNECTING || this.bluetoothState == State.SCO_CONNECTED) {
            cancelTimer();
            this.audioManager.stopBluetoothSco();
            this.audioManager.setBluetoothScoOn(false);
            this.bluetoothState = State.SCO_DISCONNECTING;
            Log.d(TAG, "stopScoAudio done: BT state=" + this.bluetoothState + ", SCO is on: " + isScoOn());
        }
    }

    public void updateDevice() {
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
            Log.d(TAG, "updateDevice");
            List<BluetoothDevice> devices = this.bluetoothHeadset.getConnectedDevices();
            if (devices.isEmpty()) {
                this.bluetoothDevice = null;
                this.bluetoothState = State.HEADSET_UNAVAILABLE;
                Log.d(TAG, "No connected bluetooth headset");
            } else {
                this.bluetoothDevice = devices.get(0);
                this.bluetoothState = State.HEADSET_AVAILABLE;
                Log.d(TAG, "Connected bluetooth headset: name=" + this.bluetoothDevice.getName() + ", state=" + stateToString(this.bluetoothHeadset.getConnectionState(this.bluetoothDevice)) + ", SCO audio=" + this.bluetoothHeadset.isAudioConnected(this.bluetoothDevice));
            }
            Log.d(TAG, "updateDevice done: BT state=" + this.bluetoothState);
        }
    }

    /* access modifiers changed from: protected */
    public AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
    }

    /* access modifiers changed from: protected */
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        try {
            this.apprtcContext.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void unregisterReceiver(BroadcastReceiver receiver) {
        try {
            this.apprtcContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public boolean getBluetoothProfileProxy(Context context, BluetoothProfile.ServiceListener listener, int profile) {
        return this.bluetoothAdapter.getProfileProxy(context, listener, profile);
    }

    /* access modifiers changed from: protected */
    public boolean hasPermission(Context context, String permission) {
        return this.apprtcContext.checkPermission(permission, Process.myPid(), Process.myUid()) == 0;
    }

    /* access modifiers changed from: protected */
    public void logBluetoothAdapterInfo(BluetoothAdapter localAdapter) {
        Log.d(TAG, "BluetoothAdapter: enabled=" + localAdapter.isEnabled() + ", state=" + stateToString(localAdapter.getState()) + ", name=" + localAdapter.getName() + ", address=" + localAdapter.getAddress());
        Set<BluetoothDevice> pairedDevices = localAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            Log.d(TAG, "paired devices:");
            for (BluetoothDevice device : pairedDevices) {
                Log.d(TAG, " name=" + device.getName() + ", address=" + device.getAddress());
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAudioDeviceState() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "updateAudioDeviceState");
        this.apprtcAudioManager.updateAudioDeviceState();
    }

    private void startTimer() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "startTimer");
        ThreadUtils.postOnUiThread(this.bluetoothTimeoutRunnable, 4000);
    }

    /* access modifiers changed from: private */
    public void cancelTimer() {
        ThreadUtils.checkIsOnMainThread();
        Log.d(TAG, "cancelTimer");
        ThreadUtils.runOnUiThread(this.bluetoothTimeoutRunnable);
    }

    /* access modifiers changed from: private */
    public void bluetoothTimeout() {
        ThreadUtils.checkIsOnMainThread();
        if (this.bluetoothState != State.UNINITIALIZED && this.bluetoothHeadset != null) {
            Log.d(TAG, "bluetoothTimeout: BT state=" + this.bluetoothState + ", attempts: " + this.scoConnectionAttempts + ", SCO is on: " + isScoOn());
            if (this.bluetoothState == State.SCO_CONNECTING) {
                boolean scoConnected = false;
                List<BluetoothDevice> devices = this.bluetoothHeadset.getConnectedDevices();
                if (devices.size() > 0) {
                    BluetoothDevice bluetoothDevice2 = devices.get(0);
                    this.bluetoothDevice = bluetoothDevice2;
                    if (this.bluetoothHeadset.isAudioConnected(bluetoothDevice2)) {
                        Log.d(TAG, "SCO connected with " + this.bluetoothDevice.getName());
                        scoConnected = true;
                    } else {
                        Log.d(TAG, "SCO is not connected with " + this.bluetoothDevice.getName());
                    }
                }
                if (scoConnected) {
                    this.bluetoothState = State.SCO_CONNECTED;
                    this.scoConnectionAttempts = 0;
                } else {
                    Log.w(TAG, "BT failed to connect after timeout");
                    stopScoAudio();
                }
                updateAudioDeviceState();
                Log.d(TAG, "bluetoothTimeout done: BT state=" + this.bluetoothState);
            }
        }
    }

    private boolean isScoOn() {
        return this.audioManager.isBluetoothScoOn();
    }

    /* access modifiers changed from: private */
    public String stateToString(int state) {
        if (state == 0) {
            return "DISCONNECTED";
        }
        if (state == 1) {
            return "CONNECTING";
        }
        if (state == 2) {
            return "CONNECTED";
        }
        if (state == 3) {
            return "DISCONNECTING";
        }
        switch (state) {
            case 10:
                return "OFF";
            case 11:
                return "TURNING_ON";
            case 12:
                return "ON";
            case 13:
                return "TURNING_OFF";
            default:
                return "INVALID";
        }
    }
}
