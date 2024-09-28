package com.google.android.gms.internal.vision;

import com.google.android.gms.internal.vision.zzcr;

final class zzem implements zzdv {
    private final int flags;
    private final String info;
    private final Object[] zznf;
    private final zzdx zzni;

    zzem(zzdx zzdx, String str, Object[] objArr) {
        this.zzni = zzdx;
        this.info = str;
        this.zznf = objArr;
        char charAt = str.charAt(0);
        if (charAt < 55296) {
            this.flags = charAt;
            return;
        }
        char c = charAt & 8191;
        int i = 13;
        int i2 = 1;
        while (true) {
            int i3 = i2 + 1;
            char charAt2 = str.charAt(i2);
            if (charAt2 >= 55296) {
                c |= (charAt2 & 8191) << i;
                i += 13;
                i2 = i3;
            } else {
                this.flags = c | (charAt2 << i);
                return;
            }
        }
    }

    public final int zzcv() {
        return (this.flags & 1) == 1 ? zzcr.zzd.zzlg : zzcr.zzd.zzlh;
    }

    public final boolean zzcw() {
        return (this.flags & 2) == 2;
    }

    public final zzdx zzcx() {
        return this.zzni;
    }

    /* access modifiers changed from: package-private */
    public final String zzde() {
        return this.info;
    }

    /* access modifiers changed from: package-private */
    public final Object[] zzdf() {
        return this.zznf;
    }
}
