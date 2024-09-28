package com.google.android.gms.internal.vision;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public final class zzx extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzx> CREATOR = new zzy();
    private final float zzco;
    public final String zzdd;
    public final zzag[] zzdi;
    public final zzr zzdj;
    private final zzr zzdk;
    private final zzr zzdl;
    public final String zzdm;
    private final int zzdn;
    public final boolean zzdo;
    public final int zzdp;
    public final int zzdq;

    public zzx(zzag[] zzagArr, zzr zzr, zzr zzr2, zzr zzr3, String str, float f, String str2, int i, boolean z, int i2, int i3) {
        this.zzdi = zzagArr;
        this.zzdj = zzr;
        this.zzdk = zzr2;
        this.zzdl = zzr3;
        this.zzdm = str;
        this.zzco = f;
        this.zzdd = str2;
        this.zzdn = i;
        this.zzdo = z;
        this.zzdp = i2;
        this.zzdq = i3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeTypedArray(parcel, 2, this.zzdi, i, false);
        SafeParcelWriter.writeParcelable(parcel, 3, this.zzdj, i, false);
        SafeParcelWriter.writeParcelable(parcel, 4, this.zzdk, i, false);
        SafeParcelWriter.writeParcelable(parcel, 5, this.zzdl, i, false);
        SafeParcelWriter.writeString(parcel, 6, this.zzdm, false);
        SafeParcelWriter.writeFloat(parcel, 7, this.zzco);
        SafeParcelWriter.writeString(parcel, 8, this.zzdd, false);
        SafeParcelWriter.writeInt(parcel, 9, this.zzdn);
        SafeParcelWriter.writeBoolean(parcel, 10, this.zzdo);
        SafeParcelWriter.writeInt(parcel, 11, this.zzdp);
        SafeParcelWriter.writeInt(parcel, 12, this.zzdq);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
