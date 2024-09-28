package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.List;

public final class zzdi extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzdi> CREATOR = new zzdj();
    public final int statusCode;
    public final List<zzah> zzdp;

    public zzdi(int i, List<zzah> list) {
        this.statusCode = i;
        this.zzdp = list;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.statusCode);
        SafeParcelWriter.writeTypedList(parcel, 3, this.zzdp, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
