package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.ChannelApi;

public final class zzaw extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzaw> CREATOR = new zzax();
    private final int type;
    private final int zzcj;
    private final zzay zzck;
    private final int zzg;

    public zzaw(zzay zzay, int i, int i2, int i3) {
        this.zzck = zzay;
        this.type = i;
        this.zzg = i2;
        this.zzcj = i3;
    }

    public final void zza(ChannelApi.ChannelListener channelListener) {
        int i = this.type;
        if (i == 1) {
            channelListener.onChannelOpened(this.zzck);
        } else if (i == 2) {
            channelListener.onChannelClosed(this.zzck, this.zzg, this.zzcj);
        } else if (i == 3) {
            channelListener.onInputClosed(this.zzck, this.zzg, this.zzcj);
        } else if (i != 4) {
            StringBuilder sb = new StringBuilder(25);
            sb.append("Unknown type: ");
            sb.append(i);
            Log.w("ChannelEventParcelable", sb.toString());
        } else {
            channelListener.onOutputClosed(this.zzck, this.zzg, this.zzcj);
        }
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeParcelable(parcel, 2, this.zzck, i, false);
        SafeParcelWriter.writeInt(parcel, 3, this.type);
        SafeParcelWriter.writeInt(parcel, 4, this.zzg);
        SafeParcelWriter.writeInt(parcel, 5, this.zzcj);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final String toString() {
        String str;
        String str2;
        String valueOf = String.valueOf(this.zzck);
        int i = this.type;
        if (i == 1) {
            str = "CHANNEL_OPENED";
        } else if (i == 2) {
            str = "CHANNEL_CLOSED";
        } else if (i == 3) {
            str = "INPUT_CLOSED";
        } else if (i != 4) {
            str = Integer.toString(i);
        } else {
            str = "OUTPUT_CLOSED";
        }
        int i2 = this.zzg;
        if (i2 == 0) {
            str2 = "CLOSE_REASON_NORMAL";
        } else if (i2 == 1) {
            str2 = "CLOSE_REASON_DISCONNECTED";
        } else if (i2 == 2) {
            str2 = "CLOSE_REASON_REMOTE_CLOSE";
        } else if (i2 != 3) {
            str2 = Integer.toString(i2);
        } else {
            str2 = "CLOSE_REASON_LOCAL_CLOSE";
        }
        int i3 = this.zzcj;
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 81 + String.valueOf(str).length() + String.valueOf(str2).length());
        sb.append("ChannelEventParcelable[, channel=");
        sb.append(valueOf);
        sb.append(", type=");
        sb.append(str);
        sb.append(", closeReason=");
        sb.append(str2);
        sb.append(", appErrorCode=");
        sb.append(i3);
        sb.append("]");
        return sb.toString();
    }
}
