package io.openinstall.sdk;

import java.nio.charset.Charset;
import java.util.Arrays;
import kotlin.UByte;

public class ab {

    public static class a {
        static final a a = new a(false, false);
        static final a b = new a(true, false);
        static final a c = new a(false, true);
        private static final int[] f;
        private static final int[] g;
        private final boolean d;
        private final boolean e;

        static {
            int[] iArr = new int[256];
            f = iArr;
            Arrays.fill(iArr, -1);
            for (int i = 0; i < b.h.length; i++) {
                f[b.h[i]] = i;
            }
            f[61] = -2;
            int[] iArr2 = new int[256];
            g = iArr2;
            Arrays.fill(iArr2, -1);
            for (int i2 = 0; i2 < b.i.length; i2++) {
                g[b.i[i2]] = i2;
            }
            g[61] = -2;
        }

        private a(boolean z, boolean z2) {
            this.d = z;
            this.e = z2;
        }

        private int a(byte[] bArr, int i, int i2) {
            int i3;
            int[] iArr = this.d ? g : f;
            int i4 = i2 - i;
            int i5 = 0;
            if (i4 == 0) {
                return 0;
            }
            if (i4 >= 2) {
                if (this.e) {
                    int i6 = 0;
                    while (true) {
                        if (i >= i2) {
                            break;
                        }
                        int i7 = i + 1;
                        byte b2 = bArr[i] & UByte.MAX_VALUE;
                        if (b2 == 61) {
                            i4 -= (i2 - i7) + 1;
                            break;
                        }
                        if (iArr[b2] == -1) {
                            i6++;
                        }
                        i = i7;
                    }
                    i4 -= i6;
                } else if (bArr[i2 - 1] == 61) {
                    i5 = bArr[i2 - 2] == 61 ? 2 : 1;
                }
                if (i5 == 0 && (i3 = i4 & 3) != 0) {
                    i5 = 4 - i3;
                }
                return (((i4 + 3) / 4) * 3) - i5;
            } else if (this.e && iArr[0] == -1) {
                return 0;
            } else {
                throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for base64 bytes");
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
            if (r11[r8] == 61) goto L_0x0030;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0030, code lost:
            if (r4 != 18) goto L_0x007f;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int a(byte[] r11, int r12, int r13, byte[] r14) {
            /*
                r10 = this;
                boolean r0 = r10.d
                if (r0 == 0) goto L_0x0007
                int[] r0 = g
                goto L_0x0009
            L_0x0007:
                int[] r0 = f
            L_0x0009:
                r1 = 18
                r2 = 0
                r3 = 0
                r4 = 18
                r5 = 0
            L_0x0010:
                r6 = 6
                r7 = 16
                if (r12 >= r13) goto L_0x007f
                int r8 = r12 + 1
                byte r12 = r11[r12]
                r12 = r12 & 255(0xff, float:3.57E-43)
                r12 = r0[r12]
                if (r12 >= 0) goto L_0x005f
                r9 = -2
                if (r12 != r9) goto L_0x003b
                if (r4 != r6) goto L_0x002f
                if (r8 == r13) goto L_0x0033
                int r12 = r8 + 1
                byte r2 = r11[r8]
                r8 = 61
                if (r2 != r8) goto L_0x0033
                goto L_0x0030
            L_0x002f:
                r12 = r8
            L_0x0030:
                if (r4 == r1) goto L_0x0033
                goto L_0x007f
            L_0x0033:
                java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
                java.lang.String r12 = "Input byte array has wrong 4-byte ending unit"
                r11.<init>(r12)
                throw r11
            L_0x003b:
                boolean r12 = r10.e
                if (r12 == 0) goto L_0x0040
                goto L_0x007d
            L_0x0040:
                java.lang.IllegalArgumentException r12 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "Illegal base64 character "
                r13.append(r14)
                int r8 = r8 + -1
                byte r11 = r11[r8]
                java.lang.String r11 = java.lang.Integer.toString(r11, r7)
                r13.append(r11)
                java.lang.String r11 = r13.toString()
                r12.<init>(r11)
                throw r12
            L_0x005f:
                int r12 = r12 << r4
                r12 = r12 | r3
                int r4 = r4 + -6
                if (r4 >= 0) goto L_0x007c
                int r3 = r5 + 1
                int r4 = r12 >> 16
                byte r4 = (byte) r4
                r14[r5] = r4
                int r4 = r3 + 1
                int r5 = r12 >> 8
                byte r5 = (byte) r5
                r14[r3] = r5
                int r5 = r4 + 1
                byte r12 = (byte) r12
                r14[r4] = r12
                r3 = 0
                r4 = 18
                goto L_0x007d
            L_0x007c:
                r3 = r12
            L_0x007d:
                r12 = r8
                goto L_0x0010
            L_0x007f:
                if (r4 != r6) goto L_0x008a
                int r1 = r5 + 1
                int r2 = r3 >> 16
                byte r2 = (byte) r2
                r14[r5] = r2
                r5 = r1
                goto L_0x009f
            L_0x008a:
                if (r4 != 0) goto L_0x009b
                int r1 = r5 + 1
                int r2 = r3 >> 16
                byte r2 = (byte) r2
                r14[r5] = r2
                int r5 = r1 + 1
                int r2 = r3 >> 8
                byte r2 = (byte) r2
                r14[r1] = r2
                goto L_0x009f
            L_0x009b:
                r14 = 12
                if (r4 == r14) goto L_0x00c8
            L_0x009f:
                if (r12 >= r13) goto L_0x00c7
                boolean r14 = r10.e
                if (r14 == 0) goto L_0x00b0
                int r14 = r12 + 1
                byte r12 = r11[r12]
                r12 = r0[r12]
                if (r12 >= 0) goto L_0x00af
                r12 = r14
                goto L_0x009f
            L_0x00af:
                r12 = r14
            L_0x00b0:
                java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r13 = new java.lang.StringBuilder
                r13.<init>()
                java.lang.String r14 = "Input byte array has incorrect ending byte at "
                r13.append(r14)
                r13.append(r12)
                java.lang.String r12 = r13.toString()
                r11.<init>(r12)
                throw r11
            L_0x00c7:
                return r5
            L_0x00c8:
                java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
                java.lang.String r12 = "Last unit does not have enough valid bits"
                r11.<init>(r12)
                throw r11
            */
            throw new UnsupportedOperationException("Method not decompiled: io.openinstall.sdk.ab.a.a(byte[], int, int, byte[]):int");
        }

        public byte[] a(String str) {
            return a(str.getBytes(Charset.defaultCharset()));
        }

        public byte[] a(byte[] bArr) {
            int a2 = a(bArr, 0, bArr.length);
            byte[] bArr2 = new byte[a2];
            int a3 = a(bArr, 0, bArr.length, bArr2);
            return a3 != a2 ? Arrays.copyOf(bArr2, a3) : bArr2;
        }
    }

    public static class b {
        static final b a = new b(false, (byte[]) null, -1, true);
        static final b b = new b(true, (byte[]) null, -1, true);
        static final b c = new b(false, j, 76, true);
        /* access modifiers changed from: private */
        public static final char[] h = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
        /* access modifiers changed from: private */
        public static final char[] i = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};
        private static final byte[] j = {13, 10};
        private final byte[] d;
        private final int e;
        private final boolean f;
        private final boolean g;

        private b(boolean z, byte[] bArr, int i2, boolean z2) {
            this.f = z;
            this.d = bArr;
            this.e = i2;
            this.g = z2;
        }

        private final int a(int i2) {
            int i3;
            if (this.g) {
                i3 = ((i2 + 2) / 3) * 4;
            } else {
                int i4 = i2 % 3;
                i3 = ((i2 / 3) * 4) + (i4 == 0 ? 0 : i4 + 1);
            }
            int i5 = this.e;
            return i5 > 0 ? i3 + (((i3 - 1) / i5) * this.d.length) : i3;
        }

        private int a(byte[] bArr, int i2, int i3, byte[] bArr2) {
            char[] cArr = this.f ? i : h;
            int i4 = ((i3 - i2) / 3) * 3;
            int i5 = i2 + i4;
            int i6 = this.e;
            if (i6 > 0 && i4 > (i6 / 4) * 3) {
                i4 = (i6 / 4) * 3;
            }
            int i7 = 0;
            while (i2 < i5) {
                int min = Math.min(i2 + i4, i5);
                int i8 = i2;
                int i9 = i7;
                while (i8 < min) {
                    int i10 = i8 + 1;
                    int i11 = i10 + 1;
                    byte b2 = ((bArr[i8] & UByte.MAX_VALUE) << 16) | ((bArr[i10] & UByte.MAX_VALUE) << 8);
                    int i12 = i11 + 1;
                    byte b3 = b2 | (bArr[i11] & UByte.MAX_VALUE);
                    int i13 = i9 + 1;
                    bArr2[i9] = (byte) cArr[(b3 >>> 18) & 63];
                    int i14 = i13 + 1;
                    bArr2[i13] = (byte) cArr[(b3 >>> 12) & 63];
                    int i15 = i14 + 1;
                    bArr2[i14] = (byte) cArr[(b3 >>> 6) & 63];
                    i9 = i15 + 1;
                    bArr2[i15] = (byte) cArr[b3 & 63];
                    i8 = i12;
                }
                int i16 = ((min - i2) / 3) * 4;
                i7 += i16;
                if (i16 == this.e && min < i3) {
                    byte[] bArr3 = this.d;
                    int length = bArr3.length;
                    int i17 = 0;
                    while (i17 < length) {
                        bArr2[i7] = bArr3[i17];
                        i17++;
                        i7++;
                    }
                }
                i2 = min;
            }
            if (i2 >= i3) {
                return i7;
            }
            int i18 = i2 + 1;
            byte b4 = bArr[i2] & UByte.MAX_VALUE;
            int i19 = i7 + 1;
            bArr2[i7] = (byte) cArr[b4 >> 2];
            if (i18 == i3) {
                int i20 = i19 + 1;
                bArr2[i19] = (byte) cArr[(b4 << 4) & 63];
                if (!this.g) {
                    return i20;
                }
                int i21 = i20 + 1;
                bArr2[i20] = 61;
                int i22 = i21 + 1;
                bArr2[i21] = 61;
                return i22;
            }
            byte b5 = bArr[i18] & UByte.MAX_VALUE;
            int i23 = i19 + 1;
            bArr2[i19] = (byte) cArr[((b4 << 4) & 63) | (b5 >> 4)];
            int i24 = i23 + 1;
            bArr2[i23] = (byte) cArr[(b5 << 2) & 63];
            if (!this.g) {
                return i24;
            }
            bArr2[i24] = 61;
            return i24 + 1;
        }

        public b a() {
            return !this.g ? this : new b(this.f, this.d, this.e, false);
        }

        public byte[] a(byte[] bArr) {
            int a2 = a(bArr.length);
            byte[] bArr2 = new byte[a2];
            int a3 = a(bArr, 0, bArr.length, bArr2);
            return a3 != a2 ? Arrays.copyOf(bArr2, a3) : bArr2;
        }

        public String b(byte[] bArr) {
            byte[] a2 = a(bArr);
            return new String(a2, 0, 0, a2.length);
        }
    }

    public static b a() {
        return b.a;
    }

    public static b b() {
        return b.b;
    }

    public static a c() {
        return a.a;
    }

    public static a d() {
        return a.b;
    }
}
