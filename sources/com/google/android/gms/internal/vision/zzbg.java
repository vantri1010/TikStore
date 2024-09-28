package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzbf;
import com.google.android.gms.internal.vision.zzbg;

public abstract class zzbg<MessageType extends zzbf<MessageType, BuilderType>, BuilderType extends zzbg<MessageType, BuilderType>> implements zzdy {
    /* access modifiers changed from: protected */
    public abstract BuilderType zza(MessageType messagetype);

    public final /* synthetic */ zzdy zza(zzdx zzdx) {
        if (zzbw().getClass().isInstance(zzdx)) {
            return zza((zzbf) zzdx);
        }
        throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
    }

    /* renamed from: zzam */
    public abstract BuilderType clone();
}
