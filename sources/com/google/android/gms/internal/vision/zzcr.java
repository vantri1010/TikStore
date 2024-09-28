package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcr;
import com.google.android.gms.internal.vision.zzcr.zza;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class zzcr<MessageType extends zzcr<MessageType, BuilderType>, BuilderType extends zza<MessageType, BuilderType>> extends zzbf<MessageType, BuilderType> {
    private static Map<Object, zzcr<?, ?>> zzkt = new ConcurrentHashMap();
    protected zzfg zzkr = zzfg.zzdu();
    private int zzks = -1;

    public static abstract class zza<MessageType extends zzcr<MessageType, BuilderType>, BuilderType extends zza<MessageType, BuilderType>> extends zzbg<MessageType, BuilderType> {
        private final MessageType zzku;
        protected MessageType zzkv;
        private boolean zzkw = false;

        protected zza(MessageType messagetype) {
            this.zzku = messagetype;
            this.zzkv = (zzcr) messagetype.zza(zzd.zzlb, (Object) null, (Object) null);
        }

        private static void zza(MessageType messagetype, MessageType messagetype2) {
            zzek.zzdc().zzq(messagetype).zzc(messagetype, messagetype2);
        }

        public /* synthetic */ Object clone() throws CloneNotSupportedException {
            zza zza = (zza) ((zzcr) this.zzku).zza(zzd.zzlc, (Object) null, (Object) null);
            if (!this.zzkw) {
                MessageType messagetype = this.zzkv;
                zzek.zzdc().zzq(messagetype).zzd(messagetype);
                this.zzkw = true;
            }
            zza.zza((zzcr) this.zzkv);
            return zza;
        }

        public final boolean isInitialized() {
            return zzcr.zza(this.zzkv, false);
        }

        public final BuilderType zza(MessageType messagetype) {
            zzbx();
            zza(this.zzkv, messagetype);
            return this;
        }

        public final /* synthetic */ zzbg zzam() {
            return (zza) clone();
        }

        public final /* synthetic */ zzdx zzbw() {
            return this.zzku;
        }

        /* access modifiers changed from: protected */
        public final void zzbx() {
            if (this.zzkw) {
                MessageType messagetype = (zzcr) this.zzkv.zza(zzd.zzlb, (Object) null, (Object) null);
                zza(messagetype, this.zzkv);
                this.zzkv = messagetype;
                this.zzkw = false;
            }
        }

        public final MessageType zzby() {
            boolean z = true;
            if (!this.zzkw) {
                MessageType messagetype = this.zzkv;
                zzek.zzdc().zzq(messagetype).zzd(messagetype);
                this.zzkw = true;
            }
            MessageType messagetype2 = (zzcr) this.zzkv;
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) messagetype2.zza(zzd.zzky, (Object) null, (Object) null)).byteValue();
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzek.zzdc().zzq(messagetype2).zzp(messagetype2);
                    if (booleanValue) {
                        messagetype2.zza(zzd.zzkz, (Object) z ? messagetype2 : null, (Object) null);
                    }
                }
            }
            if (z) {
                return messagetype2;
            }
            throw new zzfe(messagetype2);
        }

        public final /* synthetic */ zzdx zzbz() {
            if (this.zzkw) {
                return this.zzkv;
            }
            MessageType messagetype = this.zzkv;
            zzek.zzdc().zzq(messagetype).zzd(messagetype);
            this.zzkw = true;
            return this.zzkv;
        }

        public final /* synthetic */ zzdx zzca() {
            boolean z = true;
            if (!this.zzkw) {
                MessageType messagetype = this.zzkv;
                zzek.zzdc().zzq(messagetype).zzd(messagetype);
                this.zzkw = true;
            }
            zzcr zzcr = (zzcr) this.zzkv;
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) zzcr.zza(zzd.zzky, (Object) null, (Object) null)).byteValue();
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzek.zzdc().zzq(zzcr).zzp(zzcr);
                    if (booleanValue) {
                        zzcr.zza(zzd.zzkz, (Object) z ? zzcr : null, (Object) null);
                    }
                }
            }
            if (z) {
                return zzcr;
            }
            throw new zzfe(zzcr);
        }
    }

    public static class zzb<T extends zzcr<T, ?>> extends zzbh<T> {
        private T zzku;

        public zzb(T t) {
            this.zzku = t;
        }
    }

    public static abstract class zzc<MessageType extends zzc<MessageType, BuilderType>, BuilderType> extends zzcr<MessageType, BuilderType> implements zzdz {
        protected zzcj<Object> zzkx = zzcj.zzbk();
    }

    /* 'enum' modifier removed */
    public static final class zzd {
        public static final int zzky = 1;
        public static final int zzkz = 2;
        public static final int zzla = 3;
        public static final int zzlb = 4;
        public static final int zzlc = 5;
        public static final int zzld = 6;
        public static final int zzle = 7;
        private static final /* synthetic */ int[] zzlf = {1, 2, 3, 4, 5, 6, 7};
        public static final int zzlg = 1;
        public static final int zzlh = 2;
        private static final /* synthetic */ int[] zzli = {1, 2};
        public static final int zzlj = 1;
        public static final int zzlk = 2;
        private static final /* synthetic */ int[] zzll = {1, 2};

        public static int[] values$50KLMJ33DTMIUPRFDTJMOP9FE1P6UT3FC9QMCBQ7CLN6ASJ1EHIM8JB5EDPM2PR59HKN8P949LIN8Q3FCHA6UIBEEPNMMP9R0() {
            return (int[]) zzlf.clone();
        }
    }

    private static <T extends zzcr<T, ?>> T zza(T t, byte[] bArr) throws zzcx {
        T t2 = (zzcr) t.zza(zzd.zzlb, (Object) null, (Object) null);
        try {
            zzek.zzdc().zzq(t2).zza(t2, bArr, 0, bArr.length, new zzbl());
            zzek.zzdc().zzq(t2).zzd(t2);
            if (t2.zzgi == 0) {
                return t2;
            }
            throw new RuntimeException();
        } catch (IOException e) {
            if (e.getCause() instanceof zzcx) {
                throw ((zzcx) e.getCause());
            }
            throw new zzcx(e.getMessage()).zzg(t2);
        } catch (IndexOutOfBoundsException e2) {
            throw zzcx.zzcb().zzg(t2);
        }
    }

    protected static Object zza(zzdx zzdx, String str, Object[] objArr) {
        return new zzem(zzdx, str, objArr);
    }

    static Object zza(Method method, Object obj, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
        } catch (InvocationTargetException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            } else if (cause instanceof Error) {
                throw ((Error) cause);
            } else {
                throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
            }
        }
    }

    protected static <T extends zzcr<?, ?>> void zza(Class<T> cls, T t) {
        zzkt.put(cls, t);
    }

    protected static final <T extends zzcr<T, ?>> boolean zza(T t, boolean z) {
        byte byteValue = ((Byte) t.zza(zzd.zzky, (Object) null, (Object) null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue == 0) {
            return false;
        }
        return zzek.zzdc().zzq(t).zzp(t);
    }

    protected static <T extends zzcr<T, ?>> T zzb(T t, byte[] bArr) throws zzcx {
        T zza2 = zza(t, bArr);
        if (zza2 != null) {
            boolean booleanValue = Boolean.TRUE.booleanValue();
            byte byteValue = ((Byte) zza2.zza(zzd.zzky, (Object) null, (Object) null)).byteValue();
            boolean z = true;
            if (byteValue != 1) {
                if (byteValue == 0) {
                    z = false;
                } else {
                    z = zzek.zzdc().zzq(zza2).zzp(zza2);
                    if (booleanValue) {
                        zza2.zza(zzd.zzkz, (Object) z ? zza2 : null, (Object) null);
                    }
                }
            }
            if (!z) {
                throw new zzcx(new zzfe(zza2).getMessage()).zzg(zza2);
            }
        }
        return zza2;
    }

    protected static <E> zzcw<E> zzbt() {
        return zzel.zzdd();
    }

    static <T extends zzcr<?, ?>> T zzc(Class<T> cls) {
        T t = (zzcr) zzkt.get(cls);
        if (t == null) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
                t = (zzcr) zzkt.get(cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class initialization cannot fail.", e);
            }
        }
        if (t != null) {
            return t;
        }
        String valueOf = String.valueOf(cls.getName());
        throw new IllegalStateException(valueOf.length() != 0 ? "Unable to get default instance for: ".concat(valueOf) : new String("Unable to get default instance for: "));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!((zzcr) zza(zzd.zzld, (Object) null, (Object) null)).getClass().isInstance(obj)) {
            return false;
        }
        return zzek.zzdc().zzq(this).equals(this, (zzcr) obj);
    }

    public int hashCode() {
        if (this.zzgi != 0) {
            return this.zzgi;
        }
        this.zzgi = zzek.zzdc().zzq(this).hashCode(this);
        return this.zzgi;
    }

    public final boolean isInitialized() {
        boolean booleanValue = Boolean.TRUE.booleanValue();
        byte byteValue = ((Byte) zza(zzd.zzky, (Object) null, (Object) null)).byteValue();
        if (byteValue == 1) {
            return true;
        }
        if (byteValue == 0) {
            return false;
        }
        boolean zzp = zzek.zzdc().zzq(this).zzp(this);
        if (booleanValue) {
            zza(zzd.zzkz, (Object) zzp ? this : null, (Object) null);
        }
        return zzp;
    }

    public String toString() {
        return zzea.zza(this, super.toString());
    }

    /* access modifiers changed from: protected */
    public abstract Object zza(int i, Object obj, Object obj2);

    /* access modifiers changed from: package-private */
    public final int zzal() {
        return this.zzks;
    }

    public final void zzb(zzca zzca) throws IOException {
        zzek.zzdc().zze(getClass()).zza(this, zzcc.zza(zzca));
    }

    public final int zzbl() {
        if (this.zzks == -1) {
            this.zzks = zzek.zzdc().zzq(this).zzn(this);
        }
        return this.zzks;
    }

    public final /* synthetic */ zzdy zzbu() {
        zza zza2 = (zza) zza(zzd.zzlc, (Object) null, (Object) null);
        zza2.zza(this);
        return zza2;
    }

    public final /* synthetic */ zzdy zzbv() {
        return (zza) zza(zzd.zzlc, (Object) null, (Object) null);
    }

    public final /* synthetic */ zzdx zzbw() {
        return (zzcr) zza(zzd.zzld, (Object) null, (Object) null);
    }

    /* access modifiers changed from: package-private */
    public final void zzh(int i) {
        this.zzks = i;
    }
}
