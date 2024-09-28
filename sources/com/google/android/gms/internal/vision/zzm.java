package com.google.android.gms.internal.vision;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.vision.Frame;

public final class zzm extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzm> CREATOR = new zzn();
    public int height;
    private int id;
    public int rotation;
    public int width;
    private long zzat;

    public zzm() {
    }

    public zzm(int i, int i2, int i3, long j, int i4) {
        this.width = i;
        this.height = i2;
        this.id = i3;
        this.zzat = j;
        this.rotation = i4;
    }

    public static zzm zzc(Frame frame) {
        zzm zzm = new zzm();
        zzm.width = frame.getMetadata().getWidth();
        zzm.height = frame.getMetadata().getHeight();
        zzm.rotation = frame.getMetadata().getRotation();
        zzm.id = frame.getMetadata().getId();
        zzm.zzat = frame.getMetadata().getTimestampMillis();
        return zzm;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.width);
        SafeParcelWriter.writeInt(parcel, 3, this.height);
        SafeParcelWriter.writeInt(parcel, 4, this.id);
        SafeParcelWriter.writeLong(parcel, 5, this.zzat);
        SafeParcelWriter.writeInt(parcel, 6, this.rotation);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
