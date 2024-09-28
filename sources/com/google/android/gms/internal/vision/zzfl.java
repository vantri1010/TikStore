package com.google.android.gms.internal.vision;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

final class zzfl {
    private static final Logger logger = Logger.getLogger(zzfl.class.getName());
    private static final Class<?> zzgm = zzbj.zzar();
    private static final boolean zzhj = zzea();
    private static final Unsafe zznd = zzdz();
    private static final boolean zzpa = zzi(Long.TYPE);
    private static final boolean zzpb = zzi(Integer.TYPE);
    private static final zzd zzpc;
    private static final boolean zzpd = zzeb();
    private static final long zzpe = ((long) zzg(byte[].class));
    private static final long zzpf;
    private static final long zzpg;
    private static final long zzph;
    private static final long zzpi;
    private static final long zzpj;
    private static final long zzpk;
    private static final long zzpl;
    private static final long zzpm;
    private static final long zzpn;
    private static final long zzpo;
    private static final long zzpp = ((long) zzg(Object[].class));
    private static final long zzpq = ((long) zzh(Object[].class));
    private static final long zzpr = zza(zzec());
    private static final long zzps;
    /* access modifiers changed from: private */
    public static final boolean zzpt = (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN);

    static final class zza extends zzd {
        zza(Unsafe unsafe) {
            super(unsafe);
        }

        public final void zza(Object obj, long j, double d) {
            zza(obj, j, Double.doubleToLongBits(d));
        }

        public final void zza(Object obj, long j, float f) {
            zza(obj, j, Float.floatToIntBits(f));
        }

        public final void zza(Object obj, long j, boolean z) {
            if (zzfl.zzpt) {
                zzfl.zzb(obj, j, z);
            } else {
                zzfl.zzc(obj, j, z);
            }
        }

        public final void zze(Object obj, long j, byte b) {
            if (zzfl.zzpt) {
                zzfl.zza(obj, j, b);
            } else {
                zzfl.zzb(obj, j, b);
            }
        }

        public final boolean zzl(Object obj, long j) {
            return zzfl.zzpt ? zzfl.zzr(obj, j) : zzfl.zzs(obj, j);
        }

        public final float zzm(Object obj, long j) {
            return Float.intBitsToFloat(zzj(obj, j));
        }

        public final double zzn(Object obj, long j) {
            return Double.longBitsToDouble(zzk(obj, j));
        }

        public final byte zzx(Object obj, long j) {
            return zzfl.zzpt ? zzfl.zzp(obj, j) : zzfl.zzq(obj, j);
        }
    }

    static final class zzb extends zzd {
        zzb(Unsafe unsafe) {
            super(unsafe);
        }

        public final void zza(Object obj, long j, double d) {
            zza(obj, j, Double.doubleToLongBits(d));
        }

        public final void zza(Object obj, long j, float f) {
            zza(obj, j, Float.floatToIntBits(f));
        }

        public final void zza(Object obj, long j, boolean z) {
            if (zzfl.zzpt) {
                zzfl.zzb(obj, j, z);
            } else {
                zzfl.zzc(obj, j, z);
            }
        }

        public final void zze(Object obj, long j, byte b) {
            if (zzfl.zzpt) {
                zzfl.zza(obj, j, b);
            } else {
                zzfl.zzb(obj, j, b);
            }
        }

        public final boolean zzl(Object obj, long j) {
            return zzfl.zzpt ? zzfl.zzr(obj, j) : zzfl.zzs(obj, j);
        }

        public final float zzm(Object obj, long j) {
            return Float.intBitsToFloat(zzj(obj, j));
        }

        public final double zzn(Object obj, long j) {
            return Double.longBitsToDouble(zzk(obj, j));
        }

        public final byte zzx(Object obj, long j) {
            return zzfl.zzpt ? zzfl.zzp(obj, j) : zzfl.zzq(obj, j);
        }
    }

    static final class zzc extends zzd {
        zzc(Unsafe unsafe) {
            super(unsafe);
        }

        public final void zza(Object obj, long j, double d) {
            this.zzpu.putDouble(obj, j, d);
        }

        public final void zza(Object obj, long j, float f) {
            this.zzpu.putFloat(obj, j, f);
        }

        public final void zza(Object obj, long j, boolean z) {
            this.zzpu.putBoolean(obj, j, z);
        }

        public final void zze(Object obj, long j, byte b) {
            this.zzpu.putByte(obj, j, b);
        }

        public final boolean zzl(Object obj, long j) {
            return this.zzpu.getBoolean(obj, j);
        }

        public final float zzm(Object obj, long j) {
            return this.zzpu.getFloat(obj, j);
        }

        public final double zzn(Object obj, long j) {
            return this.zzpu.getDouble(obj, j);
        }

        public final byte zzx(Object obj, long j) {
            return this.zzpu.getByte(obj, j);
        }
    }

    static abstract class zzd {
        Unsafe zzpu;

        zzd(Unsafe unsafe) {
            this.zzpu = unsafe;
        }

        public abstract void zza(Object obj, long j, double d);

        public abstract void zza(Object obj, long j, float f);

        public final void zza(Object obj, long j, int i) {
            this.zzpu.putInt(obj, j, i);
        }

        public final void zza(Object obj, long j, long j2) {
            this.zzpu.putLong(obj, j, j2);
        }

        public abstract void zza(Object obj, long j, boolean z);

        public abstract void zze(Object obj, long j, byte b);

        public final int zzj(Object obj, long j) {
            return this.zzpu.getInt(obj, j);
        }

        public final long zzk(Object obj, long j) {
            return this.zzpu.getLong(obj, j);
        }

        public abstract boolean zzl(Object obj, long j);

        public abstract float zzm(Object obj, long j);

        public abstract double zzn(Object obj, long j);

        public abstract byte zzx(Object obj, long j);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00fa  */
    static {
        /*
            java.lang.Class<double[]> r0 = double[].class
            java.lang.Class<float[]> r1 = float[].class
            java.lang.Class<long[]> r2 = long[].class
            java.lang.Class<int[]> r3 = int[].class
            java.lang.Class<boolean[]> r4 = boolean[].class
            java.lang.Class<com.google.android.gms.internal.vision.zzfl> r5 = com.google.android.gms.internal.vision.zzfl.class
            java.lang.String r5 = r5.getName()
            java.util.logging.Logger r5 = java.util.logging.Logger.getLogger(r5)
            logger = r5
            sun.misc.Unsafe r5 = zzdz()
            zznd = r5
            java.lang.Class r5 = com.google.android.gms.internal.vision.zzbj.zzar()
            zzgm = r5
            java.lang.Class r5 = java.lang.Long.TYPE
            boolean r5 = zzi(r5)
            zzpa = r5
            java.lang.Class r5 = java.lang.Integer.TYPE
            boolean r5 = zzi(r5)
            zzpb = r5
            sun.misc.Unsafe r5 = zznd
            r6 = 0
            if (r5 != 0) goto L_0x0039
        L_0x0037:
            r5 = r6
            goto L_0x005e
        L_0x0039:
            boolean r5 = com.google.android.gms.internal.vision.zzbj.zzaq()
            if (r5 == 0) goto L_0x0057
            boolean r5 = zzpa
            if (r5 == 0) goto L_0x004b
            com.google.android.gms.internal.vision.zzfl$zzb r5 = new com.google.android.gms.internal.vision.zzfl$zzb
            sun.misc.Unsafe r7 = zznd
            r5.<init>(r7)
            goto L_0x005e
        L_0x004b:
            boolean r5 = zzpb
            if (r5 == 0) goto L_0x0037
            com.google.android.gms.internal.vision.zzfl$zza r5 = new com.google.android.gms.internal.vision.zzfl$zza
            sun.misc.Unsafe r7 = zznd
            r5.<init>(r7)
            goto L_0x005e
        L_0x0057:
            com.google.android.gms.internal.vision.zzfl$zzc r5 = new com.google.android.gms.internal.vision.zzfl$zzc
            sun.misc.Unsafe r7 = zznd
            r5.<init>(r7)
        L_0x005e:
            zzpc = r5
            boolean r5 = zzeb()
            zzpd = r5
            boolean r5 = zzea()
            zzhj = r5
            java.lang.Class<byte[]> r5 = byte[].class
            int r5 = zzg(r5)
            long r7 = (long) r5
            zzpe = r7
            int r5 = zzg(r4)
            long r7 = (long) r5
            zzpf = r7
            int r4 = zzh(r4)
            long r4 = (long) r4
            zzpg = r4
            int r4 = zzg(r3)
            long r4 = (long) r4
            zzph = r4
            int r3 = zzh(r3)
            long r3 = (long) r3
            zzpi = r3
            int r3 = zzg(r2)
            long r3 = (long) r3
            zzpj = r3
            int r2 = zzh(r2)
            long r2 = (long) r2
            zzpk = r2
            int r2 = zzg(r1)
            long r2 = (long) r2
            zzpl = r2
            int r1 = zzh(r1)
            long r1 = (long) r1
            zzpm = r1
            int r1 = zzg(r0)
            long r1 = (long) r1
            zzpn = r1
            int r0 = zzh(r0)
            long r0 = (long) r0
            zzpo = r0
            java.lang.Class<java.lang.Object[]> r0 = java.lang.Object[].class
            int r0 = zzg(r0)
            long r0 = (long) r0
            zzpp = r0
            java.lang.Class<java.lang.Object[]> r0 = java.lang.Object[].class
            int r0 = zzh(r0)
            long r0 = (long) r0
            zzpq = r0
            java.lang.reflect.Field r0 = zzec()
            long r0 = zza(r0)
            zzpr = r0
            java.lang.Class<java.lang.String> r0 = java.lang.String.class
            java.lang.String r1 = "value"
            java.lang.reflect.Field r0 = zzb(r0, r1)
            if (r0 == 0) goto L_0x00ea
            java.lang.Class r1 = r0.getType()
            java.lang.Class<char[]> r2 = char[].class
            if (r1 != r2) goto L_0x00ea
            r6 = r0
        L_0x00ea:
            long r0 = zza(r6)
            zzps = r0
            java.nio.ByteOrder r0 = java.nio.ByteOrder.nativeOrder()
            java.nio.ByteOrder r1 = java.nio.ByteOrder.BIG_ENDIAN
            if (r0 != r1) goto L_0x00fa
            r0 = 1
            goto L_0x00fb
        L_0x00fa:
            r0 = 0
        L_0x00fb:
            zzpt = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzfl.<clinit>():void");
    }

    private zzfl() {
    }

    static byte zza(byte[] bArr, long j) {
        return zzpc.zzx(bArr, zzpe + j);
    }

    private static long zza(Field field) {
        zzd zzd2;
        if (field == null || (zzd2 = zzpc) == null) {
            return -1;
        }
        return zzd2.zzpu.objectFieldOffset(field);
    }

    /* access modifiers changed from: private */
    public static void zza(Object obj, long j, byte b) {
        long j2 = -4 & j;
        int zzj = zzj(obj, j2);
        int i = ((~((int) j)) & 3) << 3;
        zza(obj, j2, ((255 & b) << i) | (zzj & (~(255 << i))));
    }

    static void zza(Object obj, long j, double d) {
        zzpc.zza(obj, j, d);
    }

    static void zza(Object obj, long j, float f) {
        zzpc.zza(obj, j, f);
    }

    static void zza(Object obj, long j, int i) {
        zzpc.zza(obj, j, i);
    }

    static void zza(Object obj, long j, long j2) {
        zzpc.zza(obj, j, j2);
    }

    static void zza(Object obj, long j, Object obj2) {
        zzpc.zzpu.putObject(obj, j, obj2);
    }

    static void zza(Object obj, long j, boolean z) {
        zzpc.zza(obj, j, z);
    }

    static void zza(byte[] bArr, long j, byte b) {
        zzpc.zze(bArr, zzpe + j, b);
    }

    private static Field zzb(Class<?> cls, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (Throwable th) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static void zzb(Object obj, long j, byte b) {
        long j2 = -4 & j;
        int i = (((int) j) & 3) << 3;
        zza(obj, j2, ((255 & b) << i) | (zzj(obj, j2) & (~(255 << i))));
    }

    /* access modifiers changed from: private */
    public static void zzb(Object obj, long j, boolean z) {
        zza(obj, j, z ? (byte) 1 : 0);
    }

    /* access modifiers changed from: private */
    public static void zzc(Object obj, long j, boolean z) {
        zzb(obj, j, z ? (byte) 1 : 0);
    }

    static boolean zzdx() {
        return zzhj;
    }

    static boolean zzdy() {
        return zzpd;
    }

    static Unsafe zzdz() {
        try {
            return (Unsafe) AccessController.doPrivileged(new zzfm());
        } catch (Throwable th) {
            return null;
        }
    }

    private static boolean zzea() {
        Unsafe unsafe = zznd;
        if (unsafe == null) {
            return false;
        }
        try {
            Class<?> cls = unsafe.getClass();
            cls.getMethod("objectFieldOffset", new Class[]{Field.class});
            cls.getMethod("arrayBaseOffset", new Class[]{Class.class});
            cls.getMethod("arrayIndexScale", new Class[]{Class.class});
            cls.getMethod("getInt", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putInt", new Class[]{Object.class, Long.TYPE, Integer.TYPE});
            cls.getMethod("getLong", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putLong", new Class[]{Object.class, Long.TYPE, Long.TYPE});
            cls.getMethod("getObject", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putObject", new Class[]{Object.class, Long.TYPE, Object.class});
            if (zzbj.zzaq()) {
                return true;
            }
            cls.getMethod("getByte", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putByte", new Class[]{Object.class, Long.TYPE, Byte.TYPE});
            cls.getMethod("getBoolean", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putBoolean", new Class[]{Object.class, Long.TYPE, Boolean.TYPE});
            cls.getMethod("getFloat", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putFloat", new Class[]{Object.class, Long.TYPE, Float.TYPE});
            cls.getMethod("getDouble", new Class[]{Object.class, Long.TYPE});
            cls.getMethod("putDouble", new Class[]{Object.class, Long.TYPE, Double.TYPE});
            return true;
        } catch (Throwable th) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            String valueOf = String.valueOf(th);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 71);
            sb.append("platform method missing - proto runtime falling back to safer methods: ");
            sb.append(valueOf);
            logger2.logp(level, "com.google.protobuf.UnsafeUtil", "supportsUnsafeArrayOperations", sb.toString());
            return false;
        }
    }

    private static boolean zzeb() {
        Unsafe unsafe = zznd;
        if (unsafe == null) {
            return false;
        }
        try {
            Class<?> cls = unsafe.getClass();
            cls.getMethod("objectFieldOffset", new Class[]{Field.class});
            cls.getMethod("getLong", new Class[]{Object.class, Long.TYPE});
            if (zzec() == null) {
                return false;
            }
            if (zzbj.zzaq()) {
                return true;
            }
            cls.getMethod("getByte", new Class[]{Long.TYPE});
            cls.getMethod("putByte", new Class[]{Long.TYPE, Byte.TYPE});
            cls.getMethod("getInt", new Class[]{Long.TYPE});
            cls.getMethod("putInt", new Class[]{Long.TYPE, Integer.TYPE});
            cls.getMethod("getLong", new Class[]{Long.TYPE});
            cls.getMethod("putLong", new Class[]{Long.TYPE, Long.TYPE});
            cls.getMethod("copyMemory", new Class[]{Long.TYPE, Long.TYPE, Long.TYPE});
            cls.getMethod("copyMemory", new Class[]{Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE});
            return true;
        } catch (Throwable th) {
            Logger logger2 = logger;
            Level level = Level.WARNING;
            String valueOf = String.valueOf(th);
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 71);
            sb.append("platform method missing - proto runtime falling back to safer methods: ");
            sb.append(valueOf);
            logger2.logp(level, "com.google.protobuf.UnsafeUtil", "supportsUnsafeByteBufferOperations", sb.toString());
            return false;
        }
    }

    private static Field zzec() {
        Field zzb2;
        if (zzbj.zzaq() && (zzb2 = zzb(Buffer.class, "effectiveDirectAddress")) != null) {
            return zzb2;
        }
        Field zzb3 = zzb(Buffer.class, "address");
        if (zzb3 == null || zzb3.getType() != Long.TYPE) {
            return null;
        }
        return zzb3;
    }

    private static int zzg(Class<?> cls) {
        if (zzhj) {
            return zzpc.zzpu.arrayBaseOffset(cls);
        }
        return -1;
    }

    private static int zzh(Class<?> cls) {
        if (zzhj) {
            return zzpc.zzpu.arrayIndexScale(cls);
        }
        return -1;
    }

    private static boolean zzi(Class<?> cls) {
        Class<byte[]> cls2 = byte[].class;
        if (!zzbj.zzaq()) {
            return false;
        }
        try {
            Class<?> cls3 = zzgm;
            cls3.getMethod("peekLong", new Class[]{cls, Boolean.TYPE});
            cls3.getMethod("pokeLong", new Class[]{cls, Long.TYPE, Boolean.TYPE});
            cls3.getMethod("pokeInt", new Class[]{cls, Integer.TYPE, Boolean.TYPE});
            cls3.getMethod("peekInt", new Class[]{cls, Boolean.TYPE});
            cls3.getMethod("pokeByte", new Class[]{cls, Byte.TYPE});
            cls3.getMethod("peekByte", new Class[]{cls});
            cls3.getMethod("pokeByteArray", new Class[]{cls, cls2, Integer.TYPE, Integer.TYPE});
            cls3.getMethod("peekByteArray", new Class[]{cls, cls2, Integer.TYPE, Integer.TYPE});
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    static int zzj(Object obj, long j) {
        return zzpc.zzj(obj, j);
    }

    static long zzk(Object obj, long j) {
        return zzpc.zzk(obj, j);
    }

    static boolean zzl(Object obj, long j) {
        return zzpc.zzl(obj, j);
    }

    static float zzm(Object obj, long j) {
        return zzpc.zzm(obj, j);
    }

    static double zzn(Object obj, long j) {
        return zzpc.zzn(obj, j);
    }

    static Object zzo(Object obj, long j) {
        return zzpc.zzpu.getObject(obj, j);
    }

    /* access modifiers changed from: private */
    public static byte zzp(Object obj, long j) {
        return (byte) (zzj(obj, -4 & j) >>> ((int) (((~j) & 3) << 3)));
    }

    /* access modifiers changed from: private */
    public static byte zzq(Object obj, long j) {
        return (byte) (zzj(obj, -4 & j) >>> ((int) ((j & 3) << 3)));
    }

    /* access modifiers changed from: private */
    public static boolean zzr(Object obj, long j) {
        return zzp(obj, j) != 0;
    }

    /* access modifiers changed from: private */
    public static boolean zzs(Object obj, long j) {
        return zzq(obj, j) != 0;
    }
}
