package com.google.android.exoplayer2.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import com.google.android.exoplayer2.scheduler.RequirementsWatcher;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

public final class RequirementsWatcher {
    private static final String TAG = "RequirementsWatcher";
    private final Context context;
    /* access modifiers changed from: private */
    public Handler handler;
    private final Listener listener;
    /* access modifiers changed from: private */
    public CapabilityValidatedCallback networkCallback;
    private int notMetRequirements;
    private DeviceStatusChangeReceiver receiver;
    private final Requirements requirements;

    public interface Listener {
        void requirementsMet(RequirementsWatcher requirementsWatcher);

        void requirementsNotMet(RequirementsWatcher requirementsWatcher, int i);
    }

    public RequirementsWatcher(Context context2, Listener listener2, Requirements requirements2) {
        this.requirements = requirements2;
        this.listener = listener2;
        this.context = context2.getApplicationContext();
        logd(this + " created");
    }

    public int start() {
        Assertions.checkNotNull(Looper.myLooper());
        this.handler = new Handler();
        this.notMetRequirements = this.requirements.getNotMetRequirements(this.context);
        IntentFilter filter = new IntentFilter();
        if (this.requirements.getRequiredNetworkType() != 0) {
            if (Util.SDK_INT >= 23) {
                registerNetworkCallbackV23();
            } else {
                filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            }
        }
        if (this.requirements.isChargingRequired()) {
            filter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
            filter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        }
        if (this.requirements.isIdleRequired()) {
            if (Util.SDK_INT >= 23) {
                filter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
            } else {
                filter.addAction("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.SCREEN_OFF");
            }
        }
        DeviceStatusChangeReceiver deviceStatusChangeReceiver = new DeviceStatusChangeReceiver();
        this.receiver = deviceStatusChangeReceiver;
        this.context.registerReceiver(deviceStatusChangeReceiver, filter, (String) null, this.handler);
        logd(this + " started");
        return this.notMetRequirements;
    }

    public void stop() {
        this.context.unregisterReceiver(this.receiver);
        this.receiver = null;
        if (this.networkCallback != null) {
            unregisterNetworkCallback();
        }
        logd(this + " stopped");
    }

    public Requirements getRequirements() {
        return this.requirements;
    }

    public String toString() {
        return super.toString();
    }

    private void registerNetworkCallbackV23() {
        NetworkRequest request = new NetworkRequest.Builder().addCapability(16).build();
        CapabilityValidatedCallback capabilityValidatedCallback = new CapabilityValidatedCallback();
        this.networkCallback = capabilityValidatedCallback;
        ((ConnectivityManager) this.context.getSystemService("connectivity")).registerNetworkCallback(request, capabilityValidatedCallback);
    }

    private void unregisterNetworkCallback() {
        if (Util.SDK_INT >= 21) {
            ((ConnectivityManager) this.context.getSystemService("connectivity")).unregisterNetworkCallback(this.networkCallback);
            this.networkCallback = null;
        }
    }

    /* access modifiers changed from: private */
    public void checkRequirements() {
        int notMetRequirements2 = this.requirements.getNotMetRequirements(this.context);
        if (this.notMetRequirements == notMetRequirements2) {
            logd("notMetRequirements hasn't changed: " + notMetRequirements2);
            return;
        }
        this.notMetRequirements = notMetRequirements2;
        if (notMetRequirements2 == 0) {
            logd("start job");
            this.listener.requirementsMet(this);
            return;
        }
        logd("stop job");
        this.listener.requirementsNotMet(this, notMetRequirements2);
    }

    /* access modifiers changed from: private */
    public static void logd(String message) {
    }

    private class DeviceStatusChangeReceiver extends BroadcastReceiver {
        private DeviceStatusChangeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                RequirementsWatcher.logd(RequirementsWatcher.this + " received " + intent.getAction());
                RequirementsWatcher.this.checkRequirements();
            }
        }
    }

    private final class CapabilityValidatedCallback extends ConnectivityManager.NetworkCallback {
        private CapabilityValidatedCallback() {
        }

        public void onAvailable(Network network) {
            onNetworkCallback();
        }

        public void onLost(Network network) {
            onNetworkCallback();
        }

        private void onNetworkCallback() {
            RequirementsWatcher.this.handler.post(new Runnable() {
                public final void run() {
                    RequirementsWatcher.CapabilityValidatedCallback.this.lambda$onNetworkCallback$0$RequirementsWatcher$CapabilityValidatedCallback();
                }
            });
        }

        public /* synthetic */ void lambda$onNetworkCallback$0$RequirementsWatcher$CapabilityValidatedCallback() {
            if (RequirementsWatcher.this.networkCallback != null) {
                RequirementsWatcher.logd(RequirementsWatcher.this + " NetworkCallback");
                RequirementsWatcher.this.checkRequirements();
            }
        }
    }
}
