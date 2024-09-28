package com.google.android.gms.internal.vision;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzr extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzr> CREATOR = new zzs();
    public final int height;
    public final int left;
    public final int top;
    public final int width;
    public final float zzdh;

    public zzr(int i, int i2, int i3, int i4, float f) {
        this.left = i;
        this.top = i2;
        this.width = i3;
        this.height = i4;
        this.zzdh = f;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.left);
        SafeParcelWriter.writeInt(parcel, 3, this.top);
        SafeParcelWriter.writeInt(parcel, 4, this.width);
        SafeParcelWriter.writeInt(parcel, 5, this.height);
        SafeParcelWriter.writeFloat(parcel, 6, this.zzdh);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
