package com.ta.utdid2.a.a;

import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;

public class b {
    static final /* synthetic */ boolean a = (!b.class.desiredAssertionStatus());

    static abstract class a {
        public int a;

        /* renamed from: a  reason: collision with other field name */
        public byte[] f0a;

        a() {
        }
    }

    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        C0000b bVar = new C0000b(flags, new byte[((len * 3) / 4)]);
        if (!bVar.a(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        } else if (bVar.a == bVar.f0a.length) {
            return bVar.f0a;
        } else {
            byte[] bArr = new byte[bVar.a];
            System.arraycopy(bVar.f0a, 0, bArr, 0, bVar.a);
            return bArr;
        }
    }

    /* renamed from: com.ta.utdid2.a.a.b$b  reason: collision with other inner class name */
    static class C0000b extends a {
        private static final int[] a = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int[] b = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private final int[] c;
        private int state;
        private int value;

        public C0000b(int i, byte[] bArr) {
            this.f0a = bArr;
            this.c = (i & 8) == 0 ? a : b;
            this.state = 0;
            this.value = 0;
        }

        /* JADX WARNING: Removed duplicated region for block: B:72:0x00ff A[EDGE_INSN: B:72:0x00ff->B:54:0x00ff ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean a(byte[] r17, int r18, int r19, boolean r20) {
            /*
                r16 = this;
                r0 = r16
                int r1 = r0.state
                r2 = 0
                r3 = 6
                if (r1 != r3) goto L_0x0009
                return r2
            L_0x0009:
                int r4 = r19 + r18
                int r5 = r0.value
                byte[] r6 = r0.f0a
                int[] r7 = r0.c
                r8 = r5
                r9 = 0
                r5 = r1
                r1 = r18
            L_0x0019:
                r10 = 3
                r11 = 4
                r12 = 2
                r13 = 1
                if (r1 >= r4) goto L_0x00ff
                if (r5 != 0) goto L_0x0066
            L_0x0021:
                int r14 = r1 + 4
                if (r14 > r4) goto L_0x0062
                byte r8 = r17[r1]
                r8 = r8 & 255(0xff, float:3.57E-43)
                r8 = r7[r8]
                int r8 = r8 << 18
                int r15 = r1 + 1
                byte r15 = r17[r15]
                r15 = r15 & 255(0xff, float:3.57E-43)
                r15 = r7[r15]
                int r15 = r15 << 12
                r8 = r8 | r15
                int r15 = r1 + 2
                byte r15 = r17[r15]
                r15 = r15 & 255(0xff, float:3.57E-43)
                r15 = r7[r15]
                int r15 = r15 << r3
                r8 = r8 | r15
                int r15 = r1 + 3
                byte r15 = r17[r15]
                r15 = r15 & 255(0xff, float:3.57E-43)
                r15 = r7[r15]
                r8 = r8 | r15
                if (r8 < 0) goto L_0x0062
                int r1 = r9 + 2
                byte r15 = (byte) r8
                r6[r1] = r15
                int r1 = r9 + 1
                int r15 = r8 >> 8
                byte r15 = (byte) r15
                r6[r1] = r15
                int r1 = r8 >> 16
                byte r1 = (byte) r1
                r6[r9] = r1
                int r9 = r9 + 3
                r1 = r14
                goto L_0x0021
            L_0x0062:
                if (r1 < r4) goto L_0x0066
                goto L_0x00ff
            L_0x0066:
                int r14 = r1 + 1
                byte r1 = r17[r1]
                r1 = r1 & 255(0xff, float:3.57E-43)
                r1 = r7[r1]
                r15 = 5
                r2 = -1
                if (r5 == 0) goto L_0x00ee
                if (r5 == r13) goto L_0x00df
                r13 = -2
                if (r5 == r12) goto L_0x00c4
                if (r5 == r10) goto L_0x0091
                if (r5 == r11) goto L_0x0085
                if (r5 == r15) goto L_0x007f
                goto L_0x00fb
            L_0x007f:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x0085:
                if (r1 != r13) goto L_0x008b
                int r5 = r5 + 1
                goto L_0x00fb
            L_0x008b:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x0091:
                if (r1 < 0) goto L_0x00ac
                int r2 = r8 << 6
                r1 = r1 | r2
                int r2 = r9 + 2
                byte r5 = (byte) r1
                r6[r2] = r5
                int r2 = r9 + 1
                int r5 = r1 >> 8
                byte r5 = (byte) r5
                r6[r2] = r5
                int r2 = r1 >> 16
                byte r2 = (byte) r2
                r6[r9] = r2
                int r9 = r9 + 3
                r8 = r1
                r5 = 0
                goto L_0x00fb
            L_0x00ac:
                if (r1 != r13) goto L_0x00be
                int r1 = r9 + 1
                int r2 = r8 >> 2
                byte r2 = (byte) r2
                r6[r1] = r2
                int r1 = r8 >> 10
                byte r1 = (byte) r1
                r6[r9] = r1
                int r9 = r9 + 2
                r5 = 5
                goto L_0x00fb
            L_0x00be:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x00c4:
                if (r1 < 0) goto L_0x00cd
                int r2 = r8 << 6
                r1 = r1 | r2
                int r5 = r5 + 1
                r8 = r1
                goto L_0x00fb
            L_0x00cd:
                if (r1 != r13) goto L_0x00d9
                int r1 = r9 + 1
                int r2 = r8 >> 4
                byte r2 = (byte) r2
                r6[r9] = r2
                r9 = r1
                r5 = 4
                goto L_0x00fb
            L_0x00d9:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x00df:
                if (r1 < 0) goto L_0x00e8
                int r2 = r8 << 6
                r1 = r1 | r2
                int r5 = r5 + 1
                r8 = r1
                goto L_0x00fb
            L_0x00e8:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x00ee:
                if (r1 < 0) goto L_0x00f5
                int r5 = r5 + 1
                r8 = r1
                goto L_0x00fb
            L_0x00f5:
                if (r1 == r2) goto L_0x00fb
                r0.state = r3
                r1 = 0
                return r1
            L_0x00fb:
                r1 = r14
                r2 = 0
                goto L_0x0019
            L_0x00ff:
                if (r20 != 0) goto L_0x0108
                r0.state = r5
                r0.value = r8
                r0.a = r9
                return r13
            L_0x0108:
                if (r5 == r13) goto L_0x0131
                if (r5 == r12) goto L_0x0124
                if (r5 == r10) goto L_0x0115
                if (r5 == r11) goto L_0x0111
                goto L_0x012c
            L_0x0111:
                r0.state = r3
                r1 = 0
                return r1
            L_0x0115:
                int r1 = r9 + 1
                int r2 = r8 >> 10
                byte r2 = (byte) r2
                r6[r9] = r2
                int r9 = r1 + 1
                int r2 = r8 >> 2
                byte r2 = (byte) r2
                r6[r1] = r2
                goto L_0x012c
            L_0x0124:
                int r1 = r9 + 1
                int r2 = r8 >> 4
                byte r2 = (byte) r2
                r6[r9] = r2
                r9 = r1
            L_0x012c:
                r0.state = r5
                r0.a = r9
                return r13
            L_0x0131:
                r0.state = r3
                r1 = 0
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.a.a.b.C0000b.a(byte[], int, int, boolean):boolean");
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), C.ASCII_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        c cVar = new c(flags, (byte[]) null);
        int i = (len / 3) * 4;
        int i2 = 2;
        if (!cVar.f2b) {
            int i3 = len % 3;
            if (i3 == 1) {
                i += 2;
            } else if (i3 == 2) {
                i += 3;
            }
        } else if (len % 3 > 0) {
            i += 4;
        }
        if (cVar.f3c && len > 0) {
            int i4 = ((len - 1) / 57) + 1;
            if (!cVar.d) {
                i2 = 1;
            }
            i += i4 * i2;
        }
        cVar.f0a = new byte[i];
        cVar.a(input, offset, len, true);
        if (a || cVar.a == i) {
            return cVar.f0a;
        }
        throw new AssertionError();
    }

    static class c extends a {
        static final /* synthetic */ boolean a = (!b.class.desiredAssertionStatus());
        private static final byte[] b = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        private static final byte[] c = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};

        /* renamed from: b  reason: collision with other field name */
        int f1b;

        /* renamed from: b  reason: collision with other field name */
        public final boolean f2b;

        /* renamed from: c  reason: collision with other field name */
        public final boolean f3c;
        private int count;
        public final boolean d;

        /* renamed from: d  reason: collision with other field name */
        private final byte[] f4d;
        private final byte[] e;

        public c(int i, byte[] bArr) {
            this.f0a = bArr;
            boolean z = true;
            this.f2b = (i & 1) == 0;
            this.f3c = (i & 2) == 0;
            this.d = (i & 4) == 0 ? false : z;
            this.e = (i & 8) == 0 ? b : c;
            this.f4d = new byte[2];
            this.f1b = 0;
            this.count = this.f3c ? 19 : -1;
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x005d  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0092  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x0097  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00ee  */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x01e1  */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x00ec A[EDGE_INSN: B:93:0x00ec->B:30:0x00ec ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean a(byte[] r18, int r19, int r20, boolean r21) {
            /*
                r17 = this;
                r0 = r17
                byte[] r1 = r0.e
                byte[] r2 = r0.f0a
                int r3 = r0.count
                int r4 = r20 + r19
                int r5 = r0.f1b
                r6 = -1
                r7 = 0
                r8 = 2
                r9 = 1
                if (r5 == r9) goto L_0x0034
                if (r5 == r8) goto L_0x0018
                goto L_0x0053
            L_0x0018:
                int r5 = r19 + 1
                if (r5 > r4) goto L_0x0053
                byte[] r10 = r0.f4d
                byte r11 = r10[r7]
                r11 = r11 & 255(0xff, float:3.57E-43)
                int r11 = r11 << 16
                byte r10 = r10[r9]
                r10 = r10 & 255(0xff, float:3.57E-43)
                int r10 = r10 << 8
                r10 = r10 | r11
                byte r11 = r18[r19]
                r11 = r11 & 255(0xff, float:3.57E-43)
                r10 = r10 | r11
                r0.f1b = r7
                r11 = r5
                goto L_0x0056
            L_0x0034:
                int r5 = r19 + 2
                if (r5 > r4) goto L_0x0053
                byte[] r5 = r0.f4d
                byte r5 = r5[r7]
                r5 = r5 & 255(0xff, float:3.57E-43)
                int r5 = r5 << 16
                int r10 = r19 + 1
                byte r11 = r18[r19]
                r11 = r11 & 255(0xff, float:3.57E-43)
                int r11 = r11 << 8
                r5 = r5 | r11
                int r11 = r10 + 1
                byte r10 = r18[r10]
                r10 = r10 & 255(0xff, float:3.57E-43)
                r10 = r10 | r5
                r0.f1b = r7
                goto L_0x0056
            L_0x0053:
                r11 = r19
                r10 = -1
            L_0x0056:
                r12 = 4
                r13 = 13
                r14 = 10
                if (r10 == r6) goto L_0x0092
                int r6 = r10 >> 18
                r6 = r6 & 63
                byte r6 = r1[r6]
                r2[r7] = r6
                int r6 = r10 >> 12
                r6 = r6 & 63
                byte r6 = r1[r6]
                r2[r9] = r6
                int r6 = r10 >> 6
                r6 = r6 & 63
                byte r6 = r1[r6]
                r2[r8] = r6
                r6 = r10 & 63
                byte r6 = r1[r6]
                r10 = 3
                r2[r10] = r6
                int r3 = r3 + -1
                if (r3 != 0) goto L_0x0090
                boolean r3 = r0.d
                if (r3 == 0) goto L_0x0088
                r3 = 5
                r2[r12] = r13
                goto L_0x0089
            L_0x0088:
                r3 = 4
            L_0x0089:
                int r6 = r3 + 1
                r2[r3] = r14
                r3 = 19
                goto L_0x0093
            L_0x0090:
                r6 = 4
                goto L_0x0093
            L_0x0092:
                r6 = 0
            L_0x0093:
                int r10 = r11 + 3
                if (r10 > r4) goto L_0x00ec
                byte r15 = r18[r11]
                r15 = r15 & 255(0xff, float:3.57E-43)
                int r15 = r15 << 16
                int r16 = r11 + 1
                byte r5 = r18[r16]
                r5 = r5 & 255(0xff, float:3.57E-43)
                int r5 = r5 << 8
                r5 = r5 | r15
                int r11 = r11 + 2
                byte r11 = r18[r11]
                r11 = r11 & 255(0xff, float:3.57E-43)
                r5 = r5 | r11
                int r11 = r5 >> 18
                r11 = r11 & 63
                byte r11 = r1[r11]
                r2[r6] = r11
                int r11 = r6 + 1
                int r15 = r5 >> 12
                r15 = r15 & 63
                byte r15 = r1[r15]
                r2[r11] = r15
                int r11 = r6 + 2
                int r15 = r5 >> 6
                r15 = r15 & 63
                byte r15 = r1[r15]
                r2[r11] = r15
                int r11 = r6 + 3
                r5 = r5 & 63
                byte r5 = r1[r5]
                r2[r11] = r5
                int r6 = r6 + 4
                int r3 = r3 + -1
                if (r3 != 0) goto L_0x00ea
                boolean r3 = r0.d
                if (r3 == 0) goto L_0x00e1
                int r3 = r6 + 1
                r2[r6] = r13
                r6 = r3
            L_0x00e1:
                int r3 = r6 + 1
                r2[r6] = r14
                r6 = r3
                r11 = r10
                r3 = 19
                goto L_0x0093
            L_0x00ea:
                r11 = r10
                goto L_0x0093
            L_0x00ec:
                if (r21 == 0) goto L_0x01e1
                int r5 = r0.f1b
                int r10 = r11 - r5
                int r15 = r4 + -1
                r16 = 61
                if (r10 != r15) goto L_0x0141
                if (r5 <= 0) goto L_0x0101
                byte[] r5 = r0.f4d
                byte r5 = r5[r7]
                r7 = 1
                goto L_0x0107
            L_0x0101:
                int r5 = r11 + 1
                byte r8 = r18[r11]
                r11 = r5
                r5 = r8
            L_0x0107:
                r5 = r5 & 255(0xff, float:3.57E-43)
                int r5 = r5 << r12
                int r8 = r0.f1b
                int r8 = r8 - r7
                r0.f1b = r8
                int r7 = r6 + 1
                int r8 = r5 >> 6
                r8 = r8 & 63
                byte r8 = r1[r8]
                r2[r6] = r8
                int r6 = r7 + 1
                r5 = r5 & 63
                byte r1 = r1[r5]
                r2[r7] = r1
                boolean r1 = r0.f2b
                if (r1 == 0) goto L_0x012d
                int r1 = r6 + 1
                r2[r6] = r16
                int r6 = r1 + 1
                r2[r1] = r16
            L_0x012d:
                boolean r1 = r0.f3c
                if (r1 == 0) goto L_0x013f
                boolean r1 = r0.d
                if (r1 == 0) goto L_0x013a
                int r1 = r6 + 1
                r2[r6] = r13
                r6 = r1
            L_0x013a:
                int r1 = r6 + 1
                r2[r6] = r14
                r6 = r1
            L_0x013f:
                goto L_0x01c5
            L_0x0141:
                int r10 = r11 - r5
                int r12 = r4 + -2
                if (r10 != r12) goto L_0x01ad
                if (r5 <= r9) goto L_0x0150
                byte[] r5 = r0.f4d
                byte r5 = r5[r7]
                r7 = 1
                goto L_0x0156
            L_0x0150:
                int r5 = r11 + 1
                byte r10 = r18[r11]
                r11 = r5
                r5 = r10
            L_0x0156:
                r5 = r5 & 255(0xff, float:3.57E-43)
                int r5 = r5 << r14
                int r10 = r0.f1b
                if (r10 <= 0) goto L_0x0164
                byte[] r10 = r0.f4d
                int r12 = r7 + 1
                byte r7 = r10[r7]
                goto L_0x016b
            L_0x0164:
                int r10 = r11 + 1
                byte r11 = r18[r11]
                r12 = r7
                r7 = r11
                r11 = r10
            L_0x016b:
                r7 = r7 & 255(0xff, float:3.57E-43)
                int r7 = r7 << r8
                r5 = r5 | r7
                int r7 = r0.f1b
                int r7 = r7 - r12
                r0.f1b = r7
                int r7 = r6 + 1
                int r8 = r5 >> 12
                r8 = r8 & 63
                byte r8 = r1[r8]
                r2[r6] = r8
                int r6 = r7 + 1
                int r8 = r5 >> 6
                r8 = r8 & 63
                byte r8 = r1[r8]
                r2[r7] = r8
                int r7 = r6 + 1
                r5 = r5 & 63
                byte r1 = r1[r5]
                r2[r6] = r1
                boolean r1 = r0.f2b
                if (r1 == 0) goto L_0x0199
                int r1 = r7 + 1
                r2[r7] = r16
                r7 = r1
            L_0x0199:
                boolean r1 = r0.f3c
                if (r1 == 0) goto L_0x01ab
                boolean r1 = r0.d
                if (r1 == 0) goto L_0x01a6
                int r1 = r7 + 1
                r2[r7] = r13
                r7 = r1
            L_0x01a6:
                int r1 = r7 + 1
                r2[r7] = r14
                r7 = r1
            L_0x01ab:
                r6 = r7
                goto L_0x01c5
            L_0x01ad:
                boolean r1 = r0.f3c
                if (r1 == 0) goto L_0x01c5
                if (r6 <= 0) goto L_0x01c5
                r1 = 19
                if (r3 == r1) goto L_0x01c5
                boolean r1 = r0.d
                if (r1 == 0) goto L_0x01c0
                int r1 = r6 + 1
                r2[r6] = r13
                r6 = r1
            L_0x01c0:
                int r1 = r6 + 1
                r2[r6] = r14
                r6 = r1
            L_0x01c5:
                boolean r1 = a
                if (r1 != 0) goto L_0x01d4
                int r1 = r0.f1b
                if (r1 != 0) goto L_0x01ce
                goto L_0x01d4
            L_0x01ce:
                java.lang.AssertionError r1 = new java.lang.AssertionError
                r1.<init>()
                throw r1
            L_0x01d4:
                boolean r1 = a
                if (r1 != 0) goto L_0x020a
                if (r11 != r4) goto L_0x01db
                goto L_0x020a
            L_0x01db:
                java.lang.AssertionError r1 = new java.lang.AssertionError
                r1.<init>()
                throw r1
            L_0x01e1:
                int r1 = r4 + -1
                if (r11 != r1) goto L_0x01f2
                byte[] r1 = r0.f4d
                int r2 = r0.f1b
                int r4 = r2 + 1
                r0.f1b = r4
                byte r4 = r18[r11]
                r1[r2] = r4
                goto L_0x020a
            L_0x01f2:
                int r4 = r4 - r8
                if (r11 != r4) goto L_0x020a
                byte[] r1 = r0.f4d
                int r2 = r0.f1b
                int r4 = r2 + 1
                r0.f1b = r4
                byte r5 = r18[r11]
                r1[r2] = r5
                int r2 = r4 + 1
                r0.f1b = r2
                int r11 = r11 + r9
                byte r2 = r18[r11]
                r1[r4] = r2
            L_0x020a:
                r0.a = r6
                r0.count = r3
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.ta.utdid2.a.a.b.c.a(byte[], int, int, boolean):boolean");
        }
    }

    private b() {
    }
}
