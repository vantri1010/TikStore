package com.google.android.exoplayer2.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Requirements {
    public static final int DEVICE_CHARGING = 32;
    public static final int DEVICE_IDLE = 16;
    public static final int NETWORK_TYPE_ANY = 1;
    private static final int NETWORK_TYPE_MASK = 15;
    public static final int NETWORK_TYPE_METERED = 8;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NOT_ROAMING = 4;
    private static final String[] NETWORK_TYPE_STRINGS = null;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    private static final String TAG = "Requirements";
    private final int requirements;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface NetworkType {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequirementFlags {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Requirements(int r3, boolean r4, boolean r5) {
        /*
            r2 = this;
            r0 = 0
            if (r4 == 0) goto L_0x0006
            r1 = 32
            goto L_0x0007
        L_0x0006:
            r1 = 0
        L_0x0007:
            r1 = r1 | r3
            if (r5 == 0) goto L_0x000c
            r0 = 16
        L_0x000c:
            r0 = r0 | r1
            r2.<init>(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.scheduler.Requirements.<init>(int, boolean, boolean):void");
    }

    public Requirements(int requirements2) {
        this.requirements = requirements2;
        int networkType = getRequiredNetworkType();
        Assertions.checkState(((networkType + -1) & networkType) == 0);
    }

    public int getRequiredNetworkType() {
        return this.requirements & 15;
    }

    public boolean isChargingRequired() {
        return (this.requirements & 32) != 0;
    }

    public boolean isIdleRequired() {
        return (this.requirements & 16) != 0;
    }

    public boolean checkRequirements(Context context) {
        return getNotMetRequirements(context) == 0;
    }

    public int getNotMetRequirements(Context context) {
        int i = 0;
        int requiredNetworkType = (!checkNetworkRequirements(context) ? getRequiredNetworkType() : 0) | (!checkChargingRequirement(context) ? 32 : 0);
        if (!checkIdleRequirement(context)) {
            i = 16;
        }
        return requiredNetworkType | i;
    }

    public int getRequirements() {
        return this.requirements;
    }

    private boolean checkNetworkRequirements(Context context) {
        int networkRequirement = getRequiredNetworkType();
        if (networkRequirement == 0) {
            return true;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            logd("No network info or no connection.");
            return false;
        } else if (!checkInternetConnectivity(connectivityManager)) {
            return false;
        } else {
            if (networkRequirement == 1) {
                return true;
            }
            if (networkRequirement == 4) {
                boolean roaming = networkInfo.isRoaming();
                logd("Roaming: " + roaming);
                return !roaming;
            }
            boolean roaming2 = isActiveNetworkMetered(connectivityManager, networkInfo);
            logd("Metered network: " + roaming2);
            if (networkRequirement == 2) {
                return !roaming2;
            }
            if (networkRequirement == 8) {
                return roaming2;
            }
            throw new IllegalStateException();
        }
    }

    private boolean checkChargingRequirement(Context context) {
        if (!isChargingRequired()) {
            return true;
        }
        Intent batteryStatus = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatus == null) {
            return false;
        }
        int status = batteryStatus.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
        if (status == 2 || status == 5) {
            return true;
        }
        return false;
    }

    private boolean checkIdleRequirement(Context context) {
        if (!isIdleRequired()) {
            return true;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        if (Util.SDK_INT >= 23) {
            return powerManager.isDeviceIdleMode();
        }
        if (Util.SDK_INT >= 20) {
            if (!powerManager.isInteractive()) {
                return true;
            }
        } else if (!powerManager.isScreenOn()) {
            return true;
        }
        return false;
    }

    private static boolean checkInternetConnectivity(ConnectivityManager connectivityManager) {
        if (Util.SDK_INT < 23) {
            return true;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            logd("No active network.");
            return false;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        boolean validated = networkCapabilities == null || !networkCapabilities.hasCapability(16);
        logd("Network capability validated: " + validated);
        if (!validated) {
            return true;
        }
        return false;
    }

    private static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager, NetworkInfo networkInfo) {
        if (Util.SDK_INT >= 16) {
            return connectivityManager.isActiveNetworkMetered();
        }
        int type = networkInfo.getType();
        return (type == 1 || type == 7 || type == 9) ? false : true;
    }

    private static void logd(String message) {
    }

    public String toString() {
        return super.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass() && this.requirements == ((Requirements) o).requirements) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.requirements;
    }
}
