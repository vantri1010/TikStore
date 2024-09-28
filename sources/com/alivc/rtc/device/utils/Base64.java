package com.alivc.rtc.device.utils;

import com.google.android.exoplayer2.C;
import java.io.UnsupportedEncodingException;
import kotlin.UByte;

public class Base64 {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int CRLF = 4;
    public static final int DEFAULT = 0;
    public static final int NO_CLOSE = 16;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;

    public static byte[] decode(String str, int flags) {
        return decode(str.getBytes(), flags);
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(input, 0, input.length, flags);
    }

    public static byte[] decode(byte[] input, int offset, int len, int flags) {
        Decoder decoder = new Decoder(flags, new byte[((len * 3) / 4)]);
        if (!decoder.process(input, offset, len, true)) {
            throw new IllegalArgumentException("bad base-64");
        } else if (decoder.op == decoder.output.length) {
            return decoder.output;
        } else {
            byte[] temp = new byte[decoder.op];
            System.arraycopy(decoder.output, 0, temp, 0, decoder.op);
            return temp;
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), C.ASCII_NAME);
        } catch (UnsupportedEncodingException var3) {
            throw new AssertionError(var3);
        }
    }

    public static String encodeToString(byte[] input, int offset, int len, int flags) {
        try {
            return new String(encode(input, offset, len, flags), C.ASCII_NAME);
        } catch (UnsupportedEncodingException var5) {
            throw new AssertionError(var5);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, (byte[]) null);
        int output_len = (len / 3) * 4;
        int i = 2;
        if (!encoder.do_padding) {
            int i2 = len % 3;
            if (i2 == 1) {
                output_len += 2;
            } else if (i2 == 2) {
                output_len += 3;
            }
        } else if (len % 3 > 0) {
            output_len += 4;
        }
        if (encoder.do_newline && len > 0) {
            int i3 = ((len - 1) / 57) + 1;
            if (!encoder.do_cr) {
                i = 1;
            }
            output_len += i3 * i;
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        return encoder.output;
    }

    private Base64() {
    }

    static class Encoder extends Coder {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private static final byte[] ENCODE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
        private static final byte[] ENCODE_WEBSAFE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
        public static final int LINE_GROUPS = 19;
        private final byte[] alphabet;
        private int count;
        public final boolean do_cr;
        public final boolean do_newline;
        public final boolean do_padding;
        private final byte[] tail;
        int tailLen;

        static {
            Class<Base64> cls = Base64.class;
        }

        public Encoder(int flags, byte[] output) {
            this.output = output;
            boolean z = true;
            this.do_padding = (flags & 1) == 0;
            this.do_newline = (flags & 2) == 0;
            this.do_cr = (flags & 4) == 0 ? false : z;
            this.alphabet = (flags & 8) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = this.do_newline ? 19 : -1;
        }

        public int maxOutputSize(int len) {
            return ((len * 8) / 5) + 10;
        }

        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r13v7, types: [byte] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r8v27, types: [byte] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r8v42, types: [byte] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(byte[] r19, int r20, int r21, boolean r22) {
            /*
                r18 = this;
                r0 = r18
                byte[] r1 = r0.alphabet
                byte[] r2 = r0.output
                r3 = 0
                int r4 = r0.count
                r5 = r20
                int r6 = r21 + r20
                r7 = -1
                int r8 = r0.tailLen
                r9 = 0
                r10 = 2
                r11 = 1
                if (r8 == r11) goto L_0x0036
                if (r8 == r10) goto L_0x0018
                goto L_0x0056
            L_0x0018:
                int r8 = r20 + 1
                if (r8 > r6) goto L_0x0056
                byte[] r8 = r0.tail
                byte r12 = r8[r9]
                r12 = r12 & 255(0xff, float:3.57E-43)
                int r12 = r12 << 16
                byte r8 = r8[r11]
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r8 = r8 << 8
                r8 = r8 | r12
                int r5 = r20 + 1
                byte r12 = r19[r20]
                r12 = r12 & 255(0xff, float:3.57E-43)
                r7 = r8 | r12
                r0.tailLen = r9
                goto L_0x0056
            L_0x0036:
                int r8 = r20 + 2
                if (r8 > r6) goto L_0x0056
                byte[] r8 = r0.tail
                byte r8 = r8[r9]
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r8 = r8 << 16
                int r5 = r20 + 1
                byte r12 = r19[r20]
                r12 = r12 & 255(0xff, float:3.57E-43)
                int r12 = r12 << 8
                r12 = r12 | r8
                int r13 = r5 + 1
                byte r5 = r19[r5]
                r5 = r5 & 255(0xff, float:3.57E-43)
                r7 = r12 | r5
                r0.tailLen = r9
                r5 = r13
            L_0x0056:
                r8 = -1
                r9 = 13
                r12 = 10
                if (r7 == r8) goto L_0x0097
                int r8 = r3 + 1
                int r13 = r7 >> 18
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r3] = r13
                int r3 = r8 + 1
                int r13 = r7 >> 12
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                int r8 = r3 + 1
                int r13 = r7 >> 6
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r3] = r13
                int r3 = r8 + 1
                r13 = r7 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                int r4 = r4 + -1
                if (r4 != 0) goto L_0x0097
                boolean r8 = r0.do_cr
                if (r8 == 0) goto L_0x0090
                int r8 = r3 + 1
                r2[r3] = r9
                r3 = r8
            L_0x0090:
                int r8 = r3 + 1
                r2[r3] = r12
                r4 = 19
                r3 = r8
            L_0x0097:
                int r8 = r5 + 3
                if (r8 > r6) goto L_0x00ef
                byte r8 = r19[r5]
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r8 = r8 << 16
                int r13 = r5 + 1
                byte r13 = r19[r13]
                r13 = r13 & 255(0xff, float:3.57E-43)
                int r13 = r13 << 8
                r8 = r8 | r13
                int r13 = r5 + 2
                byte r13 = r19[r13]
                r13 = r13 & 255(0xff, float:3.57E-43)
                r7 = r8 | r13
                int r8 = r7 >> 18
                r8 = r8 & 63
                byte r8 = r1[r8]
                r2[r3] = r8
                int r8 = r3 + 1
                int r13 = r7 >> 12
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                int r8 = r3 + 2
                int r13 = r7 >> 6
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                int r8 = r3 + 3
                r13 = r7 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                int r5 = r5 + 3
                int r3 = r3 + 4
                int r4 = r4 + -1
                if (r4 != 0) goto L_0x0097
                boolean r8 = r0.do_cr
                if (r8 == 0) goto L_0x00e7
                int r8 = r3 + 1
                r2[r3] = r9
                r3 = r8
            L_0x00e7:
                int r8 = r3 + 1
                r2[r3] = r12
                r4 = 19
                r3 = r8
                goto L_0x0097
            L_0x00ef:
                if (r22 == 0) goto L_0x01d9
                int r8 = r0.tailLen
                int r13 = r5 - r8
                int r14 = r6 + -1
                r15 = 61
                if (r13 != r14) goto L_0x014a
                r10 = 0
                if (r8 <= 0) goto L_0x0106
                byte[] r8 = r0.tail
                int r13 = r10 + 1
                byte r8 = r8[r10]
                r10 = r13
                goto L_0x010f
            L_0x0106:
                int r8 = r5 + 1
                byte r5 = r19[r5]
                r17 = r8
                r8 = r5
                r5 = r17
            L_0x010f:
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r7 = r8 << 4
                int r8 = r0.tailLen
                int r8 = r8 - r10
                r0.tailLen = r8
                int r8 = r3 + 1
                int r13 = r7 >> 6
                r13 = r13 & 63
                byte r13 = r1[r13]
                r2[r3] = r13
                int r3 = r8 + 1
                r13 = r7 & 63
                byte r13 = r1[r13]
                r2[r8] = r13
                boolean r8 = r0.do_padding
                if (r8 == 0) goto L_0x0136
                int r8 = r3 + 1
                r2[r3] = r15
                int r3 = r8 + 1
                r2[r8] = r15
            L_0x0136:
                boolean r8 = r0.do_newline
                if (r8 == 0) goto L_0x01d7
                boolean r8 = r0.do_cr
                if (r8 == 0) goto L_0x0143
                int r8 = r3 + 1
                r2[r3] = r9
                r3 = r8
            L_0x0143:
                int r8 = r3 + 1
                r2[r3] = r12
                r3 = r8
                goto L_0x01d7
            L_0x014a:
                int r13 = r5 - r8
                int r14 = r6 + -2
                if (r13 != r14) goto L_0x01bf
                r13 = 0
                if (r8 <= r11) goto L_0x015b
                byte[] r8 = r0.tail
                int r14 = r13 + 1
                byte r8 = r8[r13]
                r13 = r14
                goto L_0x0164
            L_0x015b:
                int r8 = r5 + 1
                byte r5 = r19[r5]
                r17 = r8
                r8 = r5
                r5 = r17
            L_0x0164:
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r8 = r8 << r12
                int r14 = r0.tailLen
                if (r14 <= 0) goto L_0x0172
                byte[] r14 = r0.tail
                int r16 = r13 + 1
                byte r13 = r14[r13]
                goto L_0x017a
            L_0x0172:
                int r14 = r5 + 1
                byte r5 = r19[r5]
                r16 = r13
                r13 = r5
                r5 = r14
            L_0x017a:
                r13 = r13 & 255(0xff, float:3.57E-43)
                int r10 = r13 << 2
                r7 = r8 | r10
                int r8 = r0.tailLen
                int r8 = r8 - r16
                r0.tailLen = r8
                int r8 = r3 + 1
                int r10 = r7 >> 12
                r10 = r10 & 63
                byte r10 = r1[r10]
                r2[r3] = r10
                int r3 = r8 + 1
                int r10 = r7 >> 6
                r10 = r10 & 63
                byte r10 = r1[r10]
                r2[r8] = r10
                int r8 = r3 + 1
                r10 = r7 & 63
                byte r10 = r1[r10]
                r2[r3] = r10
                boolean r3 = r0.do_padding
                if (r3 == 0) goto L_0x01ab
                int r3 = r8 + 1
                r2[r8] = r15
                r8 = r3
            L_0x01ab:
                boolean r3 = r0.do_newline
                if (r3 == 0) goto L_0x01bd
                boolean r3 = r0.do_cr
                if (r3 == 0) goto L_0x01b8
                int r3 = r8 + 1
                r2[r8] = r9
                r8 = r3
            L_0x01b8:
                int r3 = r8 + 1
                r2[r8] = r12
                goto L_0x01d7
            L_0x01bd:
                r3 = r8
                goto L_0x01d7
            L_0x01bf:
                boolean r8 = r0.do_newline
                if (r8 == 0) goto L_0x01d7
                if (r3 <= 0) goto L_0x01d7
                r8 = 19
                if (r4 == r8) goto L_0x01d7
                boolean r8 = r0.do_cr
                if (r8 == 0) goto L_0x01d2
                int r8 = r3 + 1
                r2[r3] = r9
                r3 = r8
            L_0x01d2:
                int r8 = r3 + 1
                r2[r3] = r12
                r3 = r8
            L_0x01d7:
                goto L_0x0204
            L_0x01d9:
                int r8 = r6 + -1
                if (r5 != r8) goto L_0x01ea
                byte[] r8 = r0.tail
                int r9 = r0.tailLen
                int r10 = r9 + 1
                r0.tailLen = r10
                byte r10 = r19[r5]
                r8[r9] = r10
                goto L_0x0204
            L_0x01ea:
                int r8 = r6 + -2
                if (r5 != r8) goto L_0x0204
                byte[] r8 = r0.tail
                int r9 = r0.tailLen
                int r10 = r9 + 1
                r0.tailLen = r10
                byte r12 = r19[r5]
                r8[r9] = r12
                int r9 = r10 + 1
                r0.tailLen = r9
                int r9 = r5 + 1
                byte r9 = r19[r9]
                r8[r10] = r9
            L_0x0204:
                r0.op = r3
                r0.count = r4
                return r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.alivc.rtc.device.utils.Base64.Encoder.process(byte[], int, int, boolean):boolean");
        }
    }

    static class Decoder extends Coder {
        private static final int[] DECODE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int[] DECODE_WEBSAFE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        private static final int EQUALS = -2;
        private static final int SKIP = -1;
        private final int[] alphabet;
        private int state;
        private int value;

        public Decoder(int flags, byte[] output) {
            this.output = output;
            this.alphabet = (flags & 8) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = 0;
            this.value = 0;
        }

        public int maxOutputSize(int len) {
            return ((len * 3) / 4) + 10;
        }

        public boolean process(byte[] input, int offset, int len, boolean finish) {
            if (this.state == 6) {
                return false;
            }
            int p = offset;
            int len2 = len + offset;
            int state2 = this.state;
            int value2 = this.value;
            int op = 0;
            byte[] output = this.output;
            int[] alphabet2 = this.alphabet;
            while (p < len2) {
                if (state2 == 0) {
                    while (p + 4 <= len2) {
                        int i = (alphabet2[input[p] & UByte.MAX_VALUE] << 18) | (alphabet2[input[p + 1] & UByte.MAX_VALUE] << 12) | (alphabet2[input[p + 2] & UByte.MAX_VALUE] << 6) | alphabet2[input[p + 3] & UByte.MAX_VALUE];
                        value2 = i;
                        if (i < 0) {
                            break;
                        }
                        output[op + 2] = (byte) value2;
                        output[op + 1] = (byte) (value2 >> 8);
                        output[op] = (byte) (value2 >> 16);
                        op += 3;
                        p += 4;
                    }
                    if (p >= len2) {
                        break;
                    }
                }
                int p2 = p + 1;
                int d = alphabet2[input[p] & UByte.MAX_VALUE];
                if (state2 != 0) {
                    if (state2 != 1) {
                        if (state2 != 2) {
                            if (state2 != 3) {
                                if (state2 != 4) {
                                    if (state2 == 5 && d != -1) {
                                        this.state = 6;
                                        return false;
                                    }
                                } else if (d == -2) {
                                    state2++;
                                } else if (d != -1) {
                                    this.state = 6;
                                    return false;
                                }
                            } else if (d >= 0) {
                                value2 = (value2 << 6) | d;
                                output[op + 2] = (byte) value2;
                                output[op + 1] = (byte) (value2 >> 8);
                                output[op] = (byte) (value2 >> 16);
                                op += 3;
                                state2 = 0;
                            } else if (d == -2) {
                                output[op + 1] = (byte) (value2 >> 2);
                                output[op] = (byte) (value2 >> 10);
                                op += 2;
                                state2 = 5;
                            } else if (d != -1) {
                                this.state = 6;
                                return false;
                            }
                        } else if (d >= 0) {
                            value2 = (value2 << 6) | d;
                            state2++;
                        } else if (d == -2) {
                            output[op] = (byte) (value2 >> 4);
                            state2 = 4;
                            op++;
                        } else if (d != -1) {
                            this.state = 6;
                            return false;
                        }
                    } else if (d >= 0) {
                        value2 = (value2 << 6) | d;
                        state2++;
                    } else if (d != -1) {
                        this.state = 6;
                        return false;
                    }
                } else if (d >= 0) {
                    value2 = d;
                    state2++;
                } else if (d != -1) {
                    this.state = 6;
                    return false;
                }
                p = p2;
            }
            if (!finish) {
                this.state = state2;
                this.value = value2;
                this.op = op;
                return true;
            } else if (state2 != 1) {
                if (state2 == 2) {
                    output[op] = (byte) (value2 >> 4);
                    op++;
                } else if (state2 == 3) {
                    int op2 = op + 1;
                    output[op] = (byte) (value2 >> 10);
                    op = op2 + 1;
                    output[op2] = (byte) (value2 >> 2);
                } else if (state2 == 4) {
                    this.state = 6;
                    return false;
                }
                this.state = state2;
                this.op = op;
                return true;
            } else {
                this.state = 6;
                return false;
            }
        }
    }

    static abstract class Coder {
        public int op;
        public byte[] output;

        public abstract int maxOutputSize(int i);

        public abstract boolean process(byte[] bArr, int i, int i2, boolean z);

        Coder() {
        }
    }
}
