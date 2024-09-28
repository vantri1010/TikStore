package com.google.android.gms.internal.vision;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class zzdl extends zzbi<Long> implements zzcw<Long>, zzej, RandomAccess {
    private static final zzdl zzmr;
    private int size;
    private long[] zzms;

    static {
        zzdl zzdl = new zzdl();
        zzmr = zzdl;
        zzdl.zzao();
    }

    zzdl() {
        this(new long[10], 0);
    }

    private zzdl(long[] jArr, int i) {
        this.zzms = jArr;
        this.size = i;
    }

    private final void zzi(int i) {
        if (i < 0 || i >= this.size) {
            throw new IndexOutOfBoundsException(zzj(i));
        }
    }

    private final String zzj(int i) {
        int i2 = this.size;
        StringBuilder sb = new StringBuilder(35);
        sb.append("Index:");
        sb.append(i);
        sb.append(", Size:");
        sb.append(i2);
        return sb.toString();
    }

    private final void zzk(int i, long j) {
        int i2;
        zzap();
        if (i < 0 || i > (i2 = this.size)) {
            throw new IndexOutOfBoundsException(zzj(i));
        }
        long[] jArr = this.zzms;
        if (i2 < jArr.length) {
            System.arraycopy(jArr, i, jArr, i + 1, i2 - i);
        } else {
            long[] jArr2 = new long[(((i2 * 3) / 2) + 1)];
            System.arraycopy(jArr, 0, jArr2, 0, i);
            System.arraycopy(this.zzms, i, jArr2, i + 1, this.size - i);
            this.zzms = jArr2;
        }
        this.zzms[i] = j;
        this.size++;
        this.modCount++;
    }

    public final /* synthetic */ void add(int i, Object obj) {
        zzk(i, ((Long) obj).longValue());
    }

    public final boolean addAll(Collection<? extends Long> collection) {
        zzap();
        zzct.checkNotNull(collection);
        if (!(collection instanceof zzdl)) {
            return super.addAll(collection);
        }
        zzdl zzdl = (zzdl) collection;
        int i = zzdl.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        if (Integer.MAX_VALUE - i2 >= i) {
            int i3 = i2 + i;
            long[] jArr = this.zzms;
            if (i3 > jArr.length) {
                this.zzms = Arrays.copyOf(jArr, i3);
            }
            System.arraycopy(zzdl.zzms, 0, this.zzms, this.size, zzdl.size);
            this.size = i3;
            this.modCount++;
            return true;
        }
        throw new OutOfMemoryError();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzdl)) {
            return super.equals(obj);
        }
        zzdl zzdl = (zzdl) obj;
        if (this.size != zzdl.size) {
            return false;
        }
        long[] jArr = zzdl.zzms;
        for (int i = 0; i < this.size; i++) {
            if (this.zzms[i] != jArr[i]) {
                return false;
            }
        }
        return true;
    }

    public final /* synthetic */ Object get(int i) {
        return Long.valueOf(getLong(i));
    }

    public final long getLong(int i) {
        zzi(i);
        return this.zzms[i];
    }

    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzct.zzk(this.zzms[i2]);
        }
        return i;
    }

    public final /* synthetic */ Object remove(int i) {
        zzap();
        zzi(i);
        long[] jArr = this.zzms;
        long j = jArr[i];
        int i2 = this.size;
        if (i < i2 - 1) {
            System.arraycopy(jArr, i + 1, jArr, i, i2 - i);
        }
        this.size--;
        this.modCount++;
        return Long.valueOf(j);
    }

    public final boolean remove(Object obj) {
        zzap();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Long.valueOf(this.zzms[i]))) {
                long[] jArr = this.zzms;
                System.arraycopy(jArr, i + 1, jArr, i, this.size - i);
                this.size--;
                this.modCount++;
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public final void removeRange(int i, int i2) {
        zzap();
        if (i2 >= i) {
            long[] jArr = this.zzms;
            System.arraycopy(jArr, i2, jArr, i, this.size - i2);
            this.size -= i2 - i;
            this.modCount++;
            return;
        }
        throw new IndexOutOfBoundsException("toIndex < fromIndex");
    }

    public final /* synthetic */ Object set(int i, Object obj) {
        long longValue = ((Long) obj).longValue();
        zzap();
        zzi(i);
        long[] jArr = this.zzms;
        long j = jArr[i];
        jArr[i] = longValue;
        return Long.valueOf(j);
    }

    public final int size() {
        return this.size;
    }

    public final /* synthetic */ zzcw zzk(int i) {
        if (i >= this.size) {
            return new zzdl(Arrays.copyOf(this.zzms, i), this.size);
        }
        throw new IllegalArgumentException();
    }

    public final void zzl(long j) {
        zzk(this.size, j);
    }
}
