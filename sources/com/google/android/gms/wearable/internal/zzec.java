package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzec extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzec> CREATOR = new zzed();
    public final int statusCode;
    public final zzdd zzdy;

    public zzec(int i, zzdd zzdd) {
        this.statusCode = i;
        this.zzdy = zzdd;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.statusCode);
        SafeParcelWriter.writeParcelable(parcel, 3, this.zzdy, i, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
