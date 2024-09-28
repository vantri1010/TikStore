package com.google.android.gms.vision;

import android.util.SparseArray;

public final class zzc {
    private static final Object lock = new Object();
    private static int zzau = 0;
    private SparseArray<Integer> zzav = new SparseArray<>();
    private SparseArray<Integer> zzaw = new SparseArray<>();

    public final int zzb(int i) {
        synchronized (lock) {
            Integer num = this.zzav.get(i);
            if (num != null) {
                int intValue = num.intValue();
                return intValue;
            }
            int i2 = zzau;
            zzau++;
            this.zzav.append(i, Integer.valueOf(i2));
            this.zzaw.append(i2, Integer.valueOf(i));
            return i2;
        }
    }

    public final int zzc(int i) {
        int intValue;
        synchronized (lock) {
            intValue = this.zzaw.get(i).intValue();
        }
        return intValue;
    }
}
