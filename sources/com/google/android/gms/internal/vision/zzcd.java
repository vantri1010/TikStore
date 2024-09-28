package com.google.android.gms.internal.vision;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class zzcd extends zzbi<Double> implements zzcw<Double>, zzej, RandomAccess {
    private static final zzcd zzhl;
    private int size;
    private double[] zzhm;

    static {
        zzcd zzcd = new zzcd();
        zzhl = zzcd;
        zzcd.zzao();
    }

    zzcd() {
        this(new double[10], 0);
    }

    private zzcd(double[] dArr, int i) {
        this.zzhm = dArr;
        this.size = i;
    }

    private final void zzc(int i, double d) {
        int i2;
        zzap();
        if (i < 0 || i > (i2 = this.size)) {
            throw new IndexOutOfBoundsException(zzj(i));
        }
        double[] dArr = this.zzhm;
        if (i2 < dArr.length) {
            System.arraycopy(dArr, i, dArr, i + 1, i2 - i);
        } else {
            double[] dArr2 = new double[(((i2 * 3) / 2) + 1)];
            System.arraycopy(dArr, 0, dArr2, 0, i);
            System.arraycopy(this.zzhm, i, dArr2, i + 1, this.size - i);
            this.zzhm = dArr2;
        }
        this.zzhm[i] = d;
        this.size++;
        this.modCount++;
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

    public final /* synthetic */ void add(int i, Object obj) {
        zzc(i, ((Double) obj).doubleValue());
    }

    public final boolean addAll(Collection<? extends Double> collection) {
        zzap();
        zzct.checkNotNull(collection);
        if (!(collection instanceof zzcd)) {
            return super.addAll(collection);
        }
        zzcd zzcd = (zzcd) collection;
        int i = zzcd.size;
        if (i == 0) {
            return false;
        }
        int i2 = this.size;
        if (Integer.MAX_VALUE - i2 >= i) {
            int i3 = i2 + i;
            double[] dArr = this.zzhm;
            if (i3 > dArr.length) {
                this.zzhm = Arrays.copyOf(dArr, i3);
            }
            System.arraycopy(zzcd.zzhm, 0, this.zzhm, this.size, zzcd.size);
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
        if (!(obj instanceof zzcd)) {
            return super.equals(obj);
        }
        zzcd zzcd = (zzcd) obj;
        if (this.size != zzcd.size) {
            return false;
        }
        double[] dArr = zzcd.zzhm;
        for (int i = 0; i < this.size; i++) {
            if (this.zzhm[i] != dArr[i]) {
                return false;
            }
        }
        return true;
    }

    public final /* synthetic */ Object get(int i) {
        zzi(i);
        return Double.valueOf(this.zzhm[i]);
    }

    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzct.zzk(Double.doubleToLongBits(this.zzhm[i2]));
        }
        return i;
    }

    public final /* synthetic */ Object remove(int i) {
        zzap();
        zzi(i);
        double[] dArr = this.zzhm;
        double d = dArr[i];
        int i2 = this.size;
        if (i < i2 - 1) {
            System.arraycopy(dArr, i + 1, dArr, i, i2 - i);
        }
        this.size--;
        this.modCount++;
        return Double.valueOf(d);
    }

    public final boolean remove(Object obj) {
        zzap();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Double.valueOf(this.zzhm[i]))) {
                double[] dArr = this.zzhm;
                System.arraycopy(dArr, i + 1, dArr, i, this.size - i);
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
            double[] dArr = this.zzhm;
            System.arraycopy(dArr, i2, dArr, i, this.size - i2);
            this.size -= i2 - i;
            this.modCount++;
            return;
        }
        throw new IndexOutOfBoundsException("toIndex < fromIndex");
    }

    public final /* synthetic */ Object set(int i, Object obj) {
        double doubleValue = ((Double) obj).doubleValue();
        zzap();
        zzi(i);
        double[] dArr = this.zzhm;
        double d = dArr[i];
        dArr[i] = doubleValue;
        return Double.valueOf(d);
    }

    public final int size() {
        return this.size;
    }

    public final void zzc(double d) {
        zzc(this.size, d);
    }

    public final /* synthetic */ zzcw zzk(int i) {
        if (i >= this.size) {
            return new zzcd(Arrays.copyOf(this.zzhm, i), this.size);
        }
        throw new IllegalArgumentException();
    }
}
