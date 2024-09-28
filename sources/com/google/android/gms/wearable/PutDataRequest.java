package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.internal.DataItemAssetParcelable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PutDataRequest extends AbstractSafeParcelable {
    public static final Parcelable.Creator<PutDataRequest> CREATOR = new zzh();
    public static final String WEAR_URI_SCHEME = "wear";
    private static final long zzt = TimeUnit.MINUTES.toMillis(30);
    private static final Random zzu = new SecureRandom();
    private byte[] data;
    private final Uri uri;
    private final Bundle zzv;
    private long zzw;

    PutDataRequest(Uri uri2, Bundle bundle, byte[] bArr, long j) {
        this.uri = uri2;
        this.zzv = bundle;
        bundle.setClassLoader(DataItemAssetParcelable.class.getClassLoader());
        this.data = bArr;
        this.zzw = j;
    }

    private PutDataRequest(Uri uri2) {
        this(uri2, new Bundle(), (byte[]) null, zzt);
    }

    public void writeToParcel(Parcel parcel, int i) {
        Asserts.checkNotNull(parcel, "dest must not be null");
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeParcelable(parcel, 2, getUri(), i, false);
        SafeParcelWriter.writeBundle(parcel, 4, this.zzv, false);
        SafeParcelWriter.writeByteArray(parcel, 5, getData(), false);
        SafeParcelWriter.writeLong(parcel, 6, this.zzw);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public static PutDataRequest createFromDataItem(DataItem dataItem) {
        Asserts.checkNotNull(dataItem, "source must not be null");
        PutDataRequest zza = zza(dataItem.getUri());
        for (Map.Entry next : dataItem.getAssets().entrySet()) {
            if (((DataItemAsset) next.getValue()).getId() == null) {
                String valueOf = String.valueOf((String) next.getKey());
                throw new IllegalStateException(valueOf.length() != 0 ? "Cannot create an asset for a put request without a digest: ".concat(valueOf) : new String("Cannot create an asset for a put request without a digest: "));
            }
            zza.putAsset((String) next.getKey(), Asset.createFromRef(((DataItemAsset) next.getValue()).getId()));
        }
        zza.setData(dataItem.getData());
        return zza;
    }

    public static PutDataRequest zza(Uri uri2) {
        Asserts.checkNotNull(uri2, "uri must not be null");
        return new PutDataRequest(uri2);
    }

    public static PutDataRequest createWithAutoAppendedId(String str) {
        Asserts.checkNotNull(str, "pathPrefix must not be null");
        StringBuilder sb = new StringBuilder(str);
        if (!str.endsWith("/")) {
            sb.append("/");
        }
        sb.append("PN");
        sb.append(zzu.nextLong());
        return new PutDataRequest(zza(sb.toString()));
    }

    public static PutDataRequest create(String str) {
        Asserts.checkNotNull(str, "path must not be null");
        return zza(zza(str));
    }

    private static Uri zza(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("An empty path was supplied.");
        } else if (!str.startsWith("/")) {
            throw new IllegalArgumentException("A path must start with a single / .");
        } else if (!str.startsWith("//")) {
            return new Uri.Builder().scheme(WEAR_URI_SCHEME).path(str).build();
        } else {
            throw new IllegalArgumentException("A path must start with a single / .");
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public byte[] getData() {
        return this.data;
    }

    public PutDataRequest setData(byte[] bArr) {
        this.data = bArr;
        return this;
    }

    public Map<String, Asset> getAssets() {
        HashMap hashMap = new HashMap();
        for (String str : this.zzv.keySet()) {
            hashMap.put(str, (Asset) this.zzv.getParcelable(str));
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public Asset getAsset(String str) {
        Asserts.checkNotNull(str, "key must not be null");
        return (Asset) this.zzv.getParcelable(str);
    }

    public boolean hasAsset(String str) {
        Asserts.checkNotNull(str, "key must not be null");
        return this.zzv.containsKey(str);
    }

    public PutDataRequest putAsset(String str, Asset asset) {
        Preconditions.checkNotNull(str);
        Preconditions.checkNotNull(asset);
        this.zzv.putParcelable(str, asset);
        return this;
    }

    public PutDataRequest removeAsset(String str) {
        Asserts.checkNotNull(str, "key must not be null");
        this.zzv.remove(str);
        return this;
    }

    public boolean isUrgent() {
        return this.zzw == 0;
    }

    public PutDataRequest setUrgent() {
        this.zzw = 0;
        return this;
    }

    public String toString() {
        return toString(Log.isLoggable(DataMap.TAG, 3));
    }

    public String toString(boolean z) {
        StringBuilder sb = new StringBuilder("PutDataRequest[");
        byte[] bArr = this.data;
        String valueOf = String.valueOf(bArr == null ? "null" : Integer.valueOf(bArr.length));
        StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 7);
        sb2.append("dataSz=");
        sb2.append(valueOf);
        sb.append(sb2.toString());
        int size = this.zzv.size();
        StringBuilder sb3 = new StringBuilder(23);
        sb3.append(", numAssets=");
        sb3.append(size);
        sb.append(sb3.toString());
        String valueOf2 = String.valueOf(this.uri);
        StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf2).length() + 6);
        sb4.append(", uri=");
        sb4.append(valueOf2);
        sb.append(sb4.toString());
        long j = this.zzw;
        StringBuilder sb5 = new StringBuilder(35);
        sb5.append(", syncDeadline=");
        sb5.append(j);
        sb.append(sb5.toString());
        if (!z) {
            sb.append("]");
            return sb.toString();
        }
        sb.append("]\n  assets: ");
        for (String str : this.zzv.keySet()) {
            String valueOf3 = String.valueOf(this.zzv.getParcelable(str));
            StringBuilder sb6 = new StringBuilder(String.valueOf(str).length() + 7 + String.valueOf(valueOf3).length());
            sb6.append("\n    ");
            sb6.append(str);
            sb6.append(": ");
            sb6.append(valueOf3);
            sb.append(sb6.toString());
        }
        sb.append("\n  ]");
        return sb.toString();
    }
}
