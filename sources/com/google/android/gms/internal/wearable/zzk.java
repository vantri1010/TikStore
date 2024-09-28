package com.google.android.gms.internal.wearable;

import im.bclpbkiauv.tgnet.ConnectionsManager;
import java.io.IOException;
import kotlin.UByte;
import kotlin.jvm.internal.ByteCompanionObject;

public final class zzk {
    private final byte[] buffer;
    private final int zzgr;
    private final int zzgs;
    private int zzgt;
    private int zzgu;
    private int zzgv;
    private int zzgw;
    private int zzgx = Integer.MAX_VALUE;
    private int zzgy;
    private int zzgz = 64;
    private int zzha = ConnectionsManager.FileTypeFile;

    public static zzk zza(byte[] bArr, int i, int i2) {
        return new zzk(bArr, 0, i2);
    }

    public final int zzj() throws IOException {
        if (this.zzgv == this.zzgt) {
            this.zzgw = 0;
            return 0;
        }
        int zzk = zzk();
        this.zzgw = zzk;
        if (zzk != 0) {
            return zzk;
        }
        throw new zzs("Protocol message contained an invalid tag (zero).");
    }

    public final void zzc(int i) throws zzs {
        if (this.zzgw != i) {
            throw new zzs("Protocol message end-group tag did not match expected tag.");
        }
    }

    public final boolean zzd(int i) throws IOException {
        int zzj;
        int i2 = i & 7;
        if (i2 == 0) {
            zzk();
            return true;
        } else if (i2 == 1) {
            zzn();
            return true;
        } else if (i2 == 2) {
            zzh(zzk());
            return true;
        } else if (i2 == 3) {
            do {
                zzj = zzj();
                if (zzj == 0 || !zzd(zzj)) {
                    zzc(((i >>> 3) << 3) | 4);
                }
                zzj = zzj();
                break;
            } while (!zzd(zzj));
            zzc(((i >>> 3) << 3) | 4);
            return true;
        } else if (i2 == 4) {
            return false;
        } else {
            if (i2 == 5) {
                zzm();
                return true;
            }
            throw new zzs("Protocol message tag had invalid wire type.");
        }
    }

    public final String readString() throws IOException {
        int zzk = zzk();
        if (zzk < 0) {
            throw zzs.zzv();
        } else if (zzk <= this.zzgt - this.zzgv) {
            String str = new String(this.buffer, this.zzgv, zzk, zzr.UTF_8);
            this.zzgv += zzk;
            return str;
        } else {
            throw zzs.zzu();
        }
    }

    public final void zza(zzt zzt) throws IOException {
        int zzk = zzk();
        if (this.zzgy < this.zzgz) {
            int zze = zze(zzk);
            this.zzgy++;
            zzt.zza(this);
            zzc(0);
            this.zzgy--;
            zzf(zze);
            return;
        }
        throw new zzs("Protocol message had too many levels of nesting.  May be malicious.  Use CodedInputStream.setRecursionLimit() to increase the depth limit.");
    }

    public final byte[] readBytes() throws IOException {
        int zzk = zzk();
        if (zzk < 0) {
            throw zzs.zzv();
        } else if (zzk == 0) {
            return zzw.zzhy;
        } else {
            int i = this.zzgt;
            int i2 = this.zzgv;
            if (zzk <= i - i2) {
                byte[] bArr = new byte[zzk];
                System.arraycopy(this.buffer, i2, bArr, 0, zzk);
                this.zzgv += zzk;
                return bArr;
            }
            throw zzs.zzu();
        }
    }

    public final int zzk() throws IOException {
        byte zzq = zzq();
        if (zzq >= 0) {
            return zzq;
        }
        byte b = zzq & ByteCompanionObject.MAX_VALUE;
        byte zzq2 = zzq();
        if (zzq2 >= 0) {
            return b | (zzq2 << 7);
        }
        byte b2 = b | ((zzq2 & ByteCompanionObject.MAX_VALUE) << 7);
        byte zzq3 = zzq();
        if (zzq3 >= 0) {
            return b2 | (zzq3 << 14);
        }
        byte b3 = b2 | ((zzq3 & ByteCompanionObject.MAX_VALUE) << 14);
        byte zzq4 = zzq();
        if (zzq4 >= 0) {
            return b3 | (zzq4 << 21);
        }
        byte b4 = b3 | ((zzq4 & ByteCompanionObject.MAX_VALUE) << 21);
        byte zzq5 = zzq();
        byte b5 = b4 | (zzq5 << 28);
        if (zzq5 >= 0) {
            return b5;
        }
        for (int i = 0; i < 5; i++) {
            if (zzq() >= 0) {
                return b5;
            }
        }
        throw zzs.zzw();
    }

    public final long zzl() throws IOException {
        long j = 0;
        for (int i = 0; i < 64; i += 7) {
            byte zzq = zzq();
            j |= ((long) (zzq & ByteCompanionObject.MAX_VALUE)) << i;
            if ((zzq & ByteCompanionObject.MIN_VALUE) == 0) {
                return j;
            }
        }
        throw zzs.zzw();
    }

    public final int zzm() throws IOException {
        return (zzq() & UByte.MAX_VALUE) | ((zzq() & UByte.MAX_VALUE) << 8) | ((zzq() & UByte.MAX_VALUE) << 16) | ((zzq() & UByte.MAX_VALUE) << 24);
    }

    public final long zzn() throws IOException {
        byte zzq = zzq();
        byte zzq2 = zzq();
        return ((((long) zzq2) & 255) << 8) | (((long) zzq) & 255) | ((((long) zzq()) & 255) << 16) | ((((long) zzq()) & 255) << 24) | ((((long) zzq()) & 255) << 32) | ((((long) zzq()) & 255) << 40) | ((((long) zzq()) & 255) << 48) | ((((long) zzq()) & 255) << 56);
    }

    private zzk(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzgr = i;
        int i3 = i2 + i;
        this.zzgt = i3;
        this.zzgs = i3;
        this.zzgv = i;
    }

    public final int zze(int i) throws zzs {
        if (i >= 0) {
            int i2 = i + this.zzgv;
            int i3 = this.zzgx;
            if (i2 <= i3) {
                this.zzgx = i2;
                zzo();
                return i3;
            }
            throw zzs.zzu();
        }
        throw zzs.zzv();
    }

    private final void zzo() {
        int i = this.zzgt + this.zzgu;
        this.zzgt = i;
        int i2 = this.zzgx;
        if (i > i2) {
            int i3 = i - i2;
            this.zzgu = i3;
            this.zzgt = i - i3;
            return;
        }
        this.zzgu = 0;
    }

    public final void zzf(int i) {
        this.zzgx = i;
        zzo();
    }

    public final int zzp() {
        int i = this.zzgx;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        return i - this.zzgv;
    }

    public final int getPosition() {
        return this.zzgv - this.zzgr;
    }

    public final byte[] zzb(int i, int i2) {
        if (i2 == 0) {
            return zzw.zzhy;
        }
        byte[] bArr = new byte[i2];
        System.arraycopy(this.buffer, this.zzgr + i, bArr, 0, i2);
        return bArr;
    }

    public final void zzg(int i) {
        zzc(i, this.zzgw);
    }

    /* access modifiers changed from: package-private */
    public final void zzc(int i, int i2) {
        int i3 = this.zzgv;
        int i4 = this.zzgr;
        if (i > i3 - i4) {
            StringBuilder sb = new StringBuilder(50);
            sb.append("Position ");
            sb.append(i);
            sb.append(" is beyond current ");
            sb.append(this.zzgv - this.zzgr);
            throw new IllegalArgumentException(sb.toString());
        } else if (i >= 0) {
            this.zzgv = i4 + i;
            this.zzgw = i2;
        } else {
            StringBuilder sb2 = new StringBuilder(24);
            sb2.append("Bad position ");
            sb2.append(i);
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    private final byte zzq() throws IOException {
        int i = this.zzgv;
        if (i != this.zzgt) {
            byte[] bArr = this.buffer;
            this.zzgv = i + 1;
            return bArr[i];
        }
        throw zzs.zzu();
    }

    private final void zzh(int i) throws IOException {
        if (i >= 0) {
            int i2 = this.zzgv;
            int i3 = i2 + i;
            int i4 = this.zzgx;
            if (i3 > i4) {
                zzh(i4 - i2);
                throw zzs.zzu();
            } else if (i <= this.zzgt - i2) {
                this.zzgv = i2 + i;
            } else {
                throw zzs.zzu();
            }
        } else {
            throw zzs.zzv();
        }
    }
}
