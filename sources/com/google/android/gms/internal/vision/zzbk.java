package com.google.android.gms.internal.vision;

import java.io.IOException;
import kotlin.UByte;
import kotlin.jvm.internal.ByteCompanionObject;

final class zzbk {
    static int zza(int i, byte[] bArr, int i2, int i3, zzbl zzbl) throws zzcx {
        if ((i >>> 3) != 0) {
            int i4 = i & 7;
            if (i4 == 0) {
                return zzb(bArr, i2, zzbl);
            }
            if (i4 == 1) {
                return i2 + 8;
            }
            if (i4 == 2) {
                return zza(bArr, i2, zzbl) + zzbl.zzgo;
            }
            if (i4 == 3) {
                int i5 = (i & -8) | 4;
                int i6 = 0;
                while (i2 < i3) {
                    i2 = zza(bArr, i2, zzbl);
                    i6 = zzbl.zzgo;
                    if (i6 == i5) {
                        break;
                    }
                    i2 = zza(i6, bArr, i2, i3, zzbl);
                }
                if (i2 <= i3 && i6 == i5) {
                    return i2;
                }
                throw zzcx.zzcf();
            } else if (i4 == 5) {
                return i2 + 4;
            } else {
                throw zzcx.zzcd();
            }
        } else {
            throw zzcx.zzcd();
        }
    }

    static int zza(int i, byte[] bArr, int i2, int i3, zzcw<?> zzcw, zzbl zzbl) {
        zzcs zzcs = (zzcs) zzcw;
        int zza = zza(bArr, i2, zzbl);
        while (true) {
            zzcs.zzae(zzbl.zzgo);
            if (zza >= i3) {
                break;
            }
            int zza2 = zza(bArr, zza, zzbl);
            if (i != zzbl.zzgo) {
                break;
            }
            zza = zza(bArr, zza2, zzbl);
        }
        return zza;
    }

    static int zza(int i, byte[] bArr, int i2, int i3, zzfg zzfg, zzbl zzbl) throws zzcx {
        if ((i >>> 3) != 0) {
            int i4 = i & 7;
            if (i4 == 0) {
                int zzb = zzb(bArr, i2, zzbl);
                zzfg.zzb(i, Long.valueOf(zzbl.zzgp));
                return zzb;
            } else if (i4 == 1) {
                zzfg.zzb(i, Long.valueOf(zzb(bArr, i2)));
                return i2 + 8;
            } else if (i4 == 2) {
                int zza = zza(bArr, i2, zzbl);
                int i5 = zzbl.zzgo;
                if (i5 >= 0) {
                    zzfg.zzb(i, i5 == 0 ? zzbo.zzgt : zzbo.zzb(bArr, zza, i5));
                    return zza + i5;
                }
                throw zzcx.zzcc();
            } else if (i4 == 3) {
                zzfg zzdv = zzfg.zzdv();
                int i6 = (i & -8) | 4;
                int i7 = 0;
                while (true) {
                    if (i2 >= i3) {
                        break;
                    }
                    int zza2 = zza(bArr, i2, zzbl);
                    int i8 = zzbl.zzgo;
                    i7 = i8;
                    if (i8 == i6) {
                        i2 = zza2;
                        break;
                    }
                    int zza3 = zza(i7, bArr, zza2, i3, zzdv, zzbl);
                    i7 = i8;
                    i2 = zza3;
                }
                if (i2 > i3 || i7 != i6) {
                    throw zzcx.zzcf();
                }
                zzfg.zzb(i, zzdv);
                return i2;
            } else if (i4 == 5) {
                zzfg.zzb(i, Integer.valueOf(zza(bArr, i2)));
                return i2 + 4;
            } else {
                throw zzcx.zzcd();
            }
        } else {
            throw zzcx.zzcd();
        }
    }

    static int zza(int i, byte[] bArr, int i2, zzbl zzbl) {
        int i3;
        int i4;
        int i5 = i & 127;
        int i6 = i2 + 1;
        byte b = bArr[i2];
        if (b >= 0) {
            i4 = b << 7;
        } else {
            int i7 = i5 | ((b & ByteCompanionObject.MAX_VALUE) << 7);
            int i8 = i6 + 1;
            byte b2 = bArr[i6];
            if (b2 >= 0) {
                i3 = b2 << 14;
            } else {
                i5 = i7 | ((b2 & ByteCompanionObject.MAX_VALUE) << 14);
                i6 = i8 + 1;
                byte b3 = bArr[i8];
                if (b3 >= 0) {
                    i4 = b3 << 21;
                } else {
                    i7 = i5 | ((b3 & ByteCompanionObject.MAX_VALUE) << 21);
                    i8 = i6 + 1;
                    byte b4 = bArr[i6];
                    if (b4 >= 0) {
                        i3 = b4 << 28;
                    } else {
                        int i9 = i7 | ((b4 & ByteCompanionObject.MAX_VALUE) << 28);
                        while (true) {
                            int i10 = i8 + 1;
                            if (bArr[i8] >= 0) {
                                zzbl.zzgo = i9;
                                return i10;
                            }
                            i8 = i10;
                        }
                    }
                }
            }
            zzbl.zzgo = i7 | i3;
            return i8;
        }
        zzbl.zzgo = i5 | i4;
        return i6;
    }

    static int zza(byte[] bArr, int i) {
        return ((bArr[i + 3] & UByte.MAX_VALUE) << 24) | (bArr[i] & UByte.MAX_VALUE) | ((bArr[i + 1] & UByte.MAX_VALUE) << 8) | ((bArr[i + 2] & UByte.MAX_VALUE) << 16);
    }

    static int zza(byte[] bArr, int i, zzbl zzbl) {
        int i2 = i + 1;
        byte b = bArr[i];
        if (b < 0) {
            return zza((int) b, bArr, i2, zzbl);
        }
        zzbl.zzgo = b;
        return i2;
    }

    static int zza(byte[] bArr, int i, zzcw<?> zzcw, zzbl zzbl) throws IOException {
        zzcs zzcs = (zzcs) zzcw;
        int zza = zza(bArr, i, zzbl);
        int i2 = zzbl.zzgo + zza;
        while (zza < i2) {
            zza = zza(bArr, zza, zzbl);
            zzcs.zzae(zzbl.zzgo);
        }
        if (zza == i2) {
            return zza;
        }
        throw zzcx.zzcb();
    }

    static int zzb(byte[] bArr, int i, zzbl zzbl) {
        int i2 = i + 1;
        long j = (long) bArr[i];
        if (j >= 0) {
            zzbl.zzgp = j;
            return i2;
        }
        int i3 = i2 + 1;
        byte b = bArr[i2];
        long j2 = (j & 127) | (((long) (b & ByteCompanionObject.MAX_VALUE)) << 7);
        int i4 = 7;
        while (b < 0) {
            int i5 = i3 + 1;
            byte b2 = bArr[i3];
            i4 += 7;
            j2 |= ((long) (b2 & ByteCompanionObject.MAX_VALUE)) << i4;
            int i6 = i5;
            b = b2;
            i3 = i6;
        }
        zzbl.zzgp = j2;
        return i3;
    }

    static long zzb(byte[] bArr, int i) {
        return ((((long) bArr[i + 7]) & 255) << 56) | (((long) bArr[i]) & 255) | ((((long) bArr[i + 1]) & 255) << 8) | ((((long) bArr[i + 2]) & 255) << 16) | ((((long) bArr[i + 3]) & 255) << 24) | ((((long) bArr[i + 4]) & 255) << 32) | ((((long) bArr[i + 5]) & 255) << 40) | ((((long) bArr[i + 6]) & 255) << 48);
    }

    static double zzc(byte[] bArr, int i) {
        return Double.longBitsToDouble(zzb(bArr, i));
    }

    static int zzc(byte[] bArr, int i, zzbl zzbl) throws zzcx {
        int zza = zza(bArr, i, zzbl);
        int i2 = zzbl.zzgo;
        if (i2 < 0) {
            throw zzcx.zzcc();
        } else if (i2 == 0) {
            zzbl.zzgq = "";
            return zza;
        } else {
            zzbl.zzgq = new String(bArr, zza, i2, zzct.UTF_8);
            return zza + i2;
        }
    }

    static float zzd(byte[] bArr, int i) {
        return Float.intBitsToFloat(zza(bArr, i));
    }

    static int zzd(byte[] bArr, int i, zzbl zzbl) throws zzcx {
        int zza = zza(bArr, i, zzbl);
        int i2 = zzbl.zzgo;
        if (i2 < 0) {
            throw zzcx.zzcc();
        } else if (i2 == 0) {
            zzbl.zzgq = "";
            return zza;
        } else {
            int i3 = zza + i2;
            if (zzfn.zze(bArr, zza, i3)) {
                zzbl.zzgq = new String(bArr, zza, i2, zzct.UTF_8);
                return i3;
            }
            throw zzcx.zzcg();
        }
    }

    static int zze(byte[] bArr, int i, zzbl zzbl) throws zzcx {
        int zza = zza(bArr, i, zzbl);
        int i2 = zzbl.zzgo;
        if (i2 < 0) {
            throw zzcx.zzcc();
        } else if (i2 == 0) {
            zzbl.zzgq = zzbo.zzgt;
            return zza;
        } else {
            zzbl.zzgq = zzbo.zzb(bArr, zza, i2);
            return zza + i2;
        }
    }
}
