package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzbf;
import com.google.android.gms.internal.vision.zzbg;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class zzbf<MessageType extends zzbf<MessageType, BuilderType>, BuilderType extends zzbg<MessageType, BuilderType>> implements zzdx {
    private static boolean zzgj = false;
    protected int zzgi = 0;

    protected static <T> void zza(Iterable<T> iterable, List<? super T> list) {
        zzct.checkNotNull(iterable);
        if (iterable instanceof zzdg) {
            List<?> zzck = ((zzdg) iterable).zzck();
            zzdg zzdg = (zzdg) list;
            int size = list.size();
            for (Object next : zzck) {
                if (next == null) {
                    StringBuilder sb = new StringBuilder(37);
                    sb.append("Element at index ");
                    sb.append(zzdg.size() - size);
                    sb.append(" is null.");
                    String sb2 = sb.toString();
                    for (int size2 = zzdg.size() - 1; size2 >= size; size2--) {
                        zzdg.remove(size2);
                    }
                    throw new NullPointerException(sb2);
                } else if (next instanceof zzbo) {
                    zzdg.zzc((zzbo) next);
                } else {
                    zzdg.add((String) next);
                }
            }
        } else if (iterable instanceof zzej) {
            list.addAll((Collection) iterable);
        } else {
            if ((list instanceof ArrayList) && (iterable instanceof Collection)) {
                ((ArrayList) list).ensureCapacity(list.size() + ((Collection) iterable).size());
            }
            int size3 = list.size();
            for (T next2 : iterable) {
                if (next2 == null) {
                    StringBuilder sb3 = new StringBuilder(37);
                    sb3.append("Element at index ");
                    sb3.append(list.size() - size3);
                    sb3.append(" is null.");
                    String sb4 = sb3.toString();
                    for (int size4 = list.size() - 1; size4 >= size3; size4--) {
                        list.remove(size4);
                    }
                    throw new NullPointerException(sb4);
                }
                list.add(next2);
            }
        }
    }

    public final byte[] toByteArray() {
        try {
            byte[] bArr = new byte[zzbl()];
            zzca zzd = zzca.zzd(bArr);
            zzb(zzd);
            zzd.zzba();
            return bArr;
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 62 + String.valueOf("byte array").length());
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ");
            sb.append("byte array");
            sb.append(" threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    public final zzbo zzak() {
        try {
            zzbt zzm = zzbo.zzm(zzbl());
            zzb(zzm.zzax());
            return zzm.zzaw();
        } catch (IOException e) {
            String name = getClass().getName();
            StringBuilder sb = new StringBuilder(String.valueOf(name).length() + 62 + String.valueOf("ByteString").length());
            sb.append("Serializing ");
            sb.append(name);
            sb.append(" to a ");
            sb.append("ByteString");
            sb.append(" threw an IOException (should never happen).");
            throw new RuntimeException(sb.toString(), e);
        }
    }

    /* access modifiers changed from: package-private */
    public int zzal() {
        throw new UnsupportedOperationException();
    }

    /* access modifiers changed from: package-private */
    public void zzh(int i) {
        throw new UnsupportedOperationException();
    }
}
