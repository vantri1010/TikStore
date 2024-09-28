package com.google.android.gms.wearable.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import javax.annotation.Nullable;

public final class zzl extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzl> CREATOR = new zzm();
    private int id;
    @Nullable
    private final String packageName;
    private final String zzbf;
    @Nullable
    private final String zzbg;
    private final String zzbh;
    private final String zzbi;
    private final String zzbj;
    @Nullable
    private final String zzbk;
    private final byte zzbl;
    private final byte zzbm;
    private final byte zzbn;
    private final byte zzbo;

    public zzl(int i, String str, @Nullable String str2, String str3, String str4, String str5, @Nullable String str6, byte b, byte b2, byte b3, byte b4, @Nullable String str7) {
        this.id = i;
        this.zzbf = str;
        this.zzbg = str2;
        this.zzbh = str3;
        this.zzbi = str4;
        this.zzbj = str5;
        this.zzbk = str6;
        this.zzbl = b;
        this.zzbm = b2;
        this.zzbn = b3;
        this.zzbo = b4;
        this.packageName = str7;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeInt(parcel, 2, this.id);
        SafeParcelWriter.writeString(parcel, 3, this.zzbf, false);
        SafeParcelWriter.writeString(parcel, 4, this.zzbg, false);
        SafeParcelWriter.writeString(parcel, 5, this.zzbh, false);
        SafeParcelWriter.writeString(parcel, 6, this.zzbi, false);
        SafeParcelWriter.writeString(parcel, 7, this.zzbj, false);
        String str = this.zzbk;
        if (str == null) {
            str = this.zzbf;
        }
        SafeParcelWriter.writeString(parcel, 8, str, false);
        SafeParcelWriter.writeByte(parcel, 9, this.zzbl);
        SafeParcelWriter.writeByte(parcel, 10, this.zzbm);
        SafeParcelWriter.writeByte(parcel, 11, this.zzbn);
        SafeParcelWriter.writeByte(parcel, 12, this.zzbo);
        SafeParcelWriter.writeString(parcel, 13, this.packageName, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final String toString() {
        int i = this.id;
        String str = this.zzbf;
        String str2 = this.zzbg;
        String str3 = this.zzbh;
        String str4 = this.zzbi;
        String str5 = this.zzbj;
        String str6 = this.zzbk;
        byte b = this.zzbl;
        byte b2 = this.zzbm;
        byte b3 = this.zzbn;
        byte b4 = this.zzbo;
        String str7 = this.packageName;
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 211 + String.valueOf(str2).length() + String.valueOf(str3).length() + String.valueOf(str4).length() + String.valueOf(str5).length() + String.valueOf(str6).length() + String.valueOf(str7).length());
        sb.append("AncsNotificationParcelable{, id=");
        sb.append(i);
        sb.append(", appId='");
        sb.append(str);
        sb.append('\'');
        sb.append(", dateTime='");
        sb.append(str2);
        sb.append('\'');
        sb.append(", notificationText='");
        sb.append(str3);
        sb.append('\'');
        sb.append(", title='");
        sb.append(str4);
        sb.append('\'');
        sb.append(", subtitle='");
        sb.append(str5);
        sb.append('\'');
        sb.append(", displayName='");
        sb.append(str6);
        sb.append('\'');
        sb.append(", eventId=");
        sb.append(b);
        sb.append(", eventFlags=");
        sb.append(b2);
        sb.append(", categoryId=");
        sb.append(b3);
        sb.append(", categoryCount=");
        sb.append(b4);
        sb.append(", packageName='");
        sb.append(str7);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        zzl zzl = (zzl) obj;
        if (this.id != zzl.id || this.zzbl != zzl.zzbl || this.zzbm != zzl.zzbm || this.zzbn != zzl.zzbn || this.zzbo != zzl.zzbo || !this.zzbf.equals(zzl.zzbf)) {
            return false;
        }
        String str = this.zzbg;
        if (str == null ? zzl.zzbg != null : !str.equals(zzl.zzbg)) {
            return false;
        }
        if (!this.zzbh.equals(zzl.zzbh) || !this.zzbi.equals(zzl.zzbi) || !this.zzbj.equals(zzl.zzbj)) {
            return false;
        }
        String str2 = this.zzbk;
        if (str2 == null ? zzl.zzbk != null : !str2.equals(zzl.zzbk)) {
            return false;
        }
        String str3 = this.packageName;
        String str4 = zzl.packageName;
        if (str3 != null) {
            return str3.equals(str4);
        }
        if (str4 == null) {
            return true;
        }
        return false;
    }

    public final int hashCode() {
        int hashCode = (((this.id + 31) * 31) + this.zzbf.hashCode()) * 31;
        String str = this.zzbg;
        int i = 0;
        int hashCode2 = (((((((hashCode + (str != null ? str.hashCode() : 0)) * 31) + this.zzbh.hashCode()) * 31) + this.zzbi.hashCode()) * 31) + this.zzbj.hashCode()) * 31;
        String str2 = this.zzbk;
        int hashCode3 = (((((((((hashCode2 + (str2 != null ? str2.hashCode() : 0)) * 31) + this.zzbl) * 31) + this.zzbm) * 31) + this.zzbn) * 31) + this.zzbo) * 31;
        String str3 = this.packageName;
        if (str3 != null) {
            i = str3.hashCode();
        }
        return hashCode3 + i;
    }
}
