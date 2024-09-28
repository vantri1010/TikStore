package com.google.android.gms.wearable.internal;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemAsset;
import java.util.HashMap;
import java.util.Map;

public final class zzdd extends AbstractSafeParcelable implements DataItem {
    public static final Parcelable.Creator<zzdd> CREATOR = new zzde();
    private byte[] data;
    private final Uri uri;
    private final Map<String, DataItemAsset> zzdo;

    zzdd(Uri uri2, Bundle bundle, byte[] bArr) {
        this.uri = uri2;
        HashMap hashMap = new HashMap();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (String str : bundle.keySet()) {
            hashMap.put(str, (DataItemAssetParcelable) bundle.getParcelable(str));
        }
        this.zzdo = hashMap;
        this.data = bArr;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeParcelable(parcel, 2, getUri(), i, false);
        Bundle bundle = new Bundle();
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        for (Map.Entry next : this.zzdo.entrySet()) {
            bundle.putParcelable((String) next.getKey(), new DataItemAssetParcelable((DataItemAsset) next.getValue()));
        }
        SafeParcelWriter.writeBundle(parcel, 4, bundle, false);
        SafeParcelWriter.writeByteArray(parcel, 5, getData(), false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public final boolean isDataValid() {
        return true;
    }

    public final Uri getUri() {
        return this.uri;
    }

    public final byte[] getData() {
        return this.data;
    }

    public final Map<String, DataItemAsset> getAssets() {
        return this.zzdo;
    }

    public final String toString() {
        boolean isLoggable = Log.isLoggable("DataItem", 3);
        StringBuilder sb = new StringBuilder("DataItemParcelable[");
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        byte[] bArr = this.data;
        String valueOf = String.valueOf(bArr == null ? "null" : Integer.valueOf(bArr.length));
        StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 8);
        sb2.append(",dataSz=");
        sb2.append(valueOf);
        sb.append(sb2.toString());
        int size = this.zzdo.size();
        StringBuilder sb3 = new StringBuilder(23);
        sb3.append(", numAssets=");
        sb3.append(size);
        sb.append(sb3.toString());
        String valueOf2 = String.valueOf(this.uri);
        StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf2).length() + 6);
        sb4.append(", uri=");
        sb4.append(valueOf2);
        sb.append(sb4.toString());
        if (!isLoggable) {
            sb.append("]");
            return sb.toString();
        }
        sb.append("]\n  assets: ");
        for (String next : this.zzdo.keySet()) {
            String valueOf3 = String.valueOf(this.zzdo.get(next));
            StringBuilder sb5 = new StringBuilder(String.valueOf(next).length() + 7 + String.valueOf(valueOf3).length());
            sb5.append("\n    ");
            sb5.append(next);
            sb5.append(": ");
            sb5.append(valueOf3);
            sb.append(sb5.toString());
        }
        sb.append("\n  ]");
        return sb.toString();
    }

    public final /* synthetic */ DataItem setData(byte[] bArr) {
        this.data = bArr;
        return this;
    }

    public final /* bridge */ /* synthetic */ Object freeze() {
        return this;
    }
}
