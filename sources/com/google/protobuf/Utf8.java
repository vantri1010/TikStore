package com.google.protobuf;

import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.serenegiant.usb.UVCCamera;
import java.nio.ByteBuffer;

final class Utf8 {
    private static final long ASCII_MASK_LONG = -9187201950435737472L;
    public static final int COMPLETE = 0;
    public static final int MALFORMED = -1;
    static final int MAX_BYTES_PER_CHAR = 3;
    private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;
    private static final Processor processor = (UnsafeProcessor.isAvailable() ? new UnsafeProcessor() : new SafeProcessor());

    public static boolean isValidUtf8(byte[] bytes) {
        return processor.isValidUtf8(bytes, 0, bytes.length);
    }

    public static boolean isValidUtf8(byte[] bytes, int index, int limit) {
        return processor.isValidUtf8(bytes, index, limit);
    }

    public static int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
        return processor.partialIsValidUtf8(state, bytes, index, limit);
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1) {
        if (byte1 > -12) {
            return -1;
        }
        return byte1;
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1, int byte2) {
        if (byte1 > -12 || byte2 > -65) {
            return -1;
        }
        return (byte2 << 8) ^ byte1;
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(int byte1, int byte2, int byte3) {
        if (byte1 > -12 || byte2 > -65 || byte3 > -65) {
            return -1;
        }
        return ((byte2 << 8) ^ byte1) ^ (byte3 << 16);
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(byte[] bytes, int index, int limit) {
        byte byte1 = bytes[index - 1];
        int i = limit - index;
        if (i == 0) {
            return incompleteStateFor(byte1);
        }
        if (i == 1) {
            return incompleteStateFor(byte1, bytes[index]);
        }
        if (i == 2) {
            return incompleteStateFor((int) byte1, (int) bytes[index], (int) bytes[index + 1]);
        }
        throw new AssertionError();
    }

    /* access modifiers changed from: private */
    public static int incompleteStateFor(ByteBuffer buffer, int byte1, int index, int remaining) {
        if (remaining == 0) {
            return incompleteStateFor(byte1);
        }
        if (remaining == 1) {
            return incompleteStateFor(byte1, buffer.get(index));
        }
        if (remaining == 2) {
            return incompleteStateFor(byte1, (int) buffer.get(index), (int) buffer.get(index + 1));
        }
        throw new AssertionError();
    }

    static class UnpairedSurrogateException extends IllegalArgumentException {
        UnpairedSurrogateException(int index, int length) {
            super("Unpaired surrogate at index " + index + " of " + length);
        }
    }

    static int encodedLength(CharSequence sequence) {
        int utf16Length = sequence.length();
        int utf8Length = utf16Length;
        int i = 0;
        while (i < utf16Length && sequence.charAt(i) < 128) {
            i++;
        }
        while (true) {
            if (i < utf16Length) {
                char c = sequence.charAt(i);
                if (c >= 2048) {
                    utf8Length += encodedLengthGeneral(sequence, i);
                    break;
                }
                utf8Length += (127 - c) >>> 31;
                i++;
            } else {
                break;
            }
        }
        if (utf8Length >= utf16Length) {
            return utf8Length;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) utf8Length) + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence sequence, int start) {
        int utf16Length = sequence.length();
        int utf8Length = 0;
        int i = start;
        while (i < utf16Length) {
            char c = sequence.charAt(i);
            if (c < 2048) {
                utf8Length += (127 - c) >>> 31;
            } else {
                utf8Length += 2;
                if (55296 <= c && c <= 57343) {
                    if (Character.codePointAt(sequence, i) >= 65536) {
                        i++;
                    } else {
                        throw new UnpairedSurrogateException(i, utf16Length);
                    }
                }
            }
            i++;
        }
        return utf8Length;
    }

    static int encode(CharSequence in, byte[] out, int offset, int length) {
        return processor.encodeUtf8(in, out, offset, length);
    }

    static boolean isValidUtf8(ByteBuffer buffer) {
        return processor.isValidUtf8(buffer, buffer.position(), buffer.remaining());
    }

    static int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
        return processor.partialIsValidUtf8(state, buffer, index, limit);
    }

    static void encodeUtf8(CharSequence in, ByteBuffer out) {
        processor.encodeUtf8(in, out);
    }

    /* access modifiers changed from: private */
    public static int estimateConsecutiveAscii(ByteBuffer buffer, int index, int limit) {
        int i = index;
        int lim = limit - 7;
        while (i < lim && (buffer.getLong(i) & ASCII_MASK_LONG) == 0) {
            i += 8;
        }
        return i - index;
    }

    static abstract class Processor {
        /* access modifiers changed from: package-private */
        public abstract int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2);

        /* access modifiers changed from: package-private */
        public abstract void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer);

        /* access modifiers changed from: package-private */
        public abstract int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3);

        /* access modifiers changed from: package-private */
        public abstract int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3);

        Processor() {
        }

        /* access modifiers changed from: package-private */
        public final boolean isValidUtf8(byte[] bytes, int index, int limit) {
            return partialIsValidUtf8(0, bytes, index, limit) == 0;
        }

        /* access modifiers changed from: package-private */
        public final boolean isValidUtf8(ByteBuffer buffer, int index, int limit) {
            return partialIsValidUtf8(0, buffer, index, limit) == 0;
        }

        /* access modifiers changed from: package-private */
        public final int partialIsValidUtf8(int state, ByteBuffer buffer, int index, int limit) {
            if (buffer.hasArray()) {
                int offset = buffer.arrayOffset();
                return partialIsValidUtf8(state, buffer.array(), offset + index, offset + limit);
            } else if (buffer.isDirect() != 0) {
                return partialIsValidUtf8Direct(state, buffer, index, limit);
            } else {
                return partialIsValidUtf8Default(state, buffer, index, limit);
            }
        }

        /* access modifiers changed from: package-private */
        public final int partialIsValidUtf8Default(int state, ByteBuffer buffer, int index, int limit) {
            if (state != 0) {
                if (index >= limit) {
                    return state;
                }
                byte byte1 = (byte) state;
                if (byte1 < -32) {
                    if (byte1 >= -62) {
                        int index2 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i = index2;
                        } else {
                            index = index2;
                        }
                    }
                    return -1;
                } else if (byte1 < -16) {
                    byte byte2 = (byte) (~(state >> 8));
                    if (byte2 == 0) {
                        int index3 = index + 1;
                        byte2 = buffer.get(index);
                        if (index3 >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte2);
                        }
                        index = index3;
                    }
                    if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                        int index4 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i2 = index4;
                        } else {
                            index = index4;
                        }
                    }
                    return -1;
                } else {
                    byte byte22 = (byte) (~(state >> 8));
                    byte byte3 = 0;
                    if (byte22 == 0) {
                        int index5 = index + 1;
                        byte22 = buffer.get(index);
                        if (index5 >= limit) {
                            return Utf8.incompleteStateFor(byte1, byte22);
                        }
                        index = index5;
                    } else {
                        byte3 = (byte) (state >> 16);
                    }
                    if (byte3 == 0) {
                        int index6 = index + 1;
                        byte3 = buffer.get(index);
                        if (index6 >= limit) {
                            return Utf8.incompleteStateFor((int) byte1, (int) byte22, (int) byte3);
                        }
                        index = index6;
                    }
                    if (byte22 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0 && byte3 <= -65) {
                        int index7 = index + 1;
                        if (buffer.get(index) > -65) {
                            int i3 = index7;
                        } else {
                            index = index7;
                        }
                    }
                    return -1;
                }
            }
            return partialIsValidUtf8(buffer, index, limit);
        }

        private static int partialIsValidUtf8(ByteBuffer buffer, int index, int limit) {
            int index2 = index + Utf8.estimateConsecutiveAscii(buffer, index, limit);
            while (index2 < limit) {
                int index3 = index2 + 1;
                int index4 = buffer.get(index2);
                int byte1 = index4;
                if (index4 >= 0) {
                    index2 = index3;
                } else if (byte1 < -32) {
                    if (index3 >= limit) {
                        return byte1;
                    }
                    if (byte1 < -62 || buffer.get(index3) > -65) {
                        return -1;
                    }
                    index2 = index3 + 1;
                } else if (byte1 < -16) {
                    if (index3 >= limit - 1) {
                        return Utf8.incompleteStateFor(buffer, byte1, index3, limit - index3);
                    }
                    int index5 = index3 + 1;
                    byte byte2 = buffer.get(index3);
                    if (byte2 > -65 || ((byte1 == -32 && byte2 < -96) || ((byte1 == -19 && byte2 >= -96) || buffer.get(index5) > -65))) {
                        return -1;
                    }
                    index2 = index5 + 1;
                } else if (index3 >= limit - 2) {
                    return Utf8.incompleteStateFor(buffer, byte1, index3, limit - index3);
                } else {
                    int index6 = index3 + 1;
                    int byte22 = buffer.get(index3);
                    if (byte22 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0) {
                        int index7 = index6 + 1;
                        if (buffer.get(index6) <= -65) {
                            index2 = index7 + 1;
                            if (buffer.get(index7) > -65) {
                            }
                        }
                    }
                    return -1;
                }
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public final void encodeUtf8(CharSequence in, ByteBuffer out) {
            if (out.hasArray()) {
                int offset = out.arrayOffset();
                out.position(Utf8.encode(in, out.array(), out.position() + offset, out.remaining()) - offset);
            } else if (out.isDirect()) {
                encodeUtf8Direct(in, out);
            } else {
                encodeUtf8Default(in, out);
            }
        }

        /* access modifiers changed from: package-private */
        public final void encodeUtf8Default(CharSequence in, ByteBuffer out) {
            int outIx;
            int inLength = in.length();
            int outIx2 = out.position();
            int inIx = 0;
            while (inIx < inLength) {
                try {
                    char charAt = in.charAt(inIx);
                    char c = charAt;
                    if (charAt >= 128) {
                        break;
                    }
                    out.put(outIx2 + inIx, (byte) c);
                    inIx++;
                } catch (IndexOutOfBoundsException e) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                }
            }
            if (inIx == inLength) {
                out.position(outIx2 + inIx);
                return;
            }
            outIx2 += inIx;
            while (inIx < inLength) {
                char c2 = in.charAt(inIx);
                if (c2 < 128) {
                    out.put(outIx2, (byte) c2);
                } else if (c2 < 2048) {
                    outIx = outIx2 + 1;
                    try {
                        out.put(outIx2, (byte) ((c2 >>> 6) | PsExtractor.AUDIO_STREAM));
                        out.put(outIx, (byte) ((c2 & '?') | 128));
                        outIx2 = outIx;
                    } catch (IndexOutOfBoundsException e2) {
                        outIx2 = outIx;
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                    }
                } else if (c2 < 55296 || 57343 < c2) {
                    outIx = outIx2 + 1;
                    out.put(outIx2, (byte) ((c2 >>> 12) | 224));
                    outIx2 = outIx + 1;
                    out.put(outIx, (byte) (((c2 >>> 6) & 63) | 128));
                    out.put(outIx2, (byte) ((c2 & '?') | 128));
                } else {
                    if (inIx + 1 != inLength) {
                        inIx++;
                        char charAt2 = in.charAt(inIx);
                        char low = charAt2;
                        if (Character.isSurrogatePair(c2, charAt2)) {
                            int codePoint = Character.toCodePoint(c2, low);
                            int outIx3 = outIx2 + 1;
                            try {
                                out.put(outIx2, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                outIx2 = outIx3 + 1;
                                out.put(outIx3, (byte) (((codePoint >>> 12) & 63) | 128));
                                outIx3 = outIx2 + 1;
                                out.put(outIx2, (byte) (((codePoint >>> 6) & 63) | 128));
                                out.put(outIx3, (byte) ((codePoint & 63) | 128));
                                outIx2 = outIx3;
                            } catch (IndexOutOfBoundsException e3) {
                                outIx2 = outIx3;
                                throw new ArrayIndexOutOfBoundsException("Failed writing " + in.charAt(inIx) + " at index " + (out.position() + Math.max(inIx, (outIx2 - out.position()) + 1)));
                            }
                        }
                    }
                    throw new UnpairedSurrogateException(inIx, inLength);
                }
                inIx++;
                outIx2++;
            }
            out.position(outIx2);
        }
    }

    static final class SafeProcessor extends Processor {
        SafeProcessor() {
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r1v5, types: [int, byte] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v10, types: [int, byte] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=int, for r4v4, types: [int, byte] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int partialIsValidUtf8(int r8, byte[] r9, int r10, int r11) {
            /*
                r7 = this;
                if (r8 == 0) goto L_0x0089
                if (r10 < r11) goto L_0x0005
                return r8
            L_0x0005:
                byte r0 = (byte) r8
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L_0x001d
                r1 = -62
                if (r0 < r1) goto L_0x001c
                int r1 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0019
                r10 = r1
                goto L_0x001c
            L_0x0019:
                r10 = r1
                goto L_0x0089
            L_0x001c:
                return r2
            L_0x001d:
                r4 = -16
                if (r0 >= r4) goto L_0x004c
                int r4 = r8 >> 8
                int r4 = ~r4
                byte r4 = (byte) r4
                if (r4 != 0) goto L_0x0033
                int r5 = r10 + 1
                byte r4 = r9[r10]
                if (r5 < r11) goto L_0x0032
                int r10 = com.google.protobuf.Utf8.incompleteStateFor(r0, r4)
                return r10
            L_0x0032:
                r10 = r5
            L_0x0033:
                if (r4 > r3) goto L_0x004b
                r5 = -96
                if (r0 != r1) goto L_0x003b
                if (r4 < r5) goto L_0x004b
            L_0x003b:
                r1 = -19
                if (r0 != r1) goto L_0x0041
                if (r4 >= r5) goto L_0x004b
            L_0x0041:
                int r1 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0049
                r10 = r1
                goto L_0x004b
            L_0x0049:
                r10 = r1
                goto L_0x0089
            L_0x004b:
                return r2
            L_0x004c:
                int r1 = r8 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L_0x0060
                int r5 = r10 + 1
                byte r1 = r9[r10]
                if (r5 < r11) goto L_0x005e
                int r10 = com.google.protobuf.Utf8.incompleteStateFor(r0, r1)
                return r10
            L_0x005e:
                r10 = r5
                goto L_0x0063
            L_0x0060:
                int r5 = r8 >> 16
                byte r4 = (byte) r5
            L_0x0063:
                if (r4 != 0) goto L_0x0071
                int r5 = r10 + 1
                byte r4 = r9[r10]
                if (r5 < r11) goto L_0x0070
                int r10 = com.google.protobuf.Utf8.incompleteStateFor((int) r0, (int) r1, (int) r4)
                return r10
            L_0x0070:
                r10 = r5
            L_0x0071:
                if (r1 > r3) goto L_0x0088
                int r5 = r0 << 28
                int r6 = r1 + 112
                int r5 = r5 + r6
                int r5 = r5 >> 30
                if (r5 != 0) goto L_0x0088
                if (r4 > r3) goto L_0x0088
                int r5 = r10 + 1
                byte r10 = r9[r10]
                if (r10 <= r3) goto L_0x0086
                r10 = r5
                goto L_0x0088
            L_0x0086:
                r10 = r5
                goto L_0x0089
            L_0x0088:
                return r2
            L_0x0089:
                int r0 = partialIsValidUtf8(r9, r10, r11)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.SafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            return partialIsValidUtf8Default(state, buffer, index, limit);
        }

        /* access modifiers changed from: package-private */
        public int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
            int utf16Length = in.length();
            int j = offset;
            int i = 0;
            int limit = offset + length;
            while (i < utf16Length && i + j < limit) {
                char charAt = in.charAt(i);
                char c = charAt;
                if (charAt >= 128) {
                    break;
                }
                out[j + i] = (byte) c;
                i++;
            }
            if (i == utf16Length) {
                return j + utf16Length;
            }
            int j2 = j + i;
            while (i < utf16Length) {
                char c2 = in.charAt(i);
                if (c2 < 128 && j2 < limit) {
                    out[j2] = (byte) c2;
                    j2++;
                } else if (c2 < 2048 && j2 <= limit - 2) {
                    int j3 = j2 + 1;
                    out[j2] = (byte) ((c2 >>> 6) | 960);
                    j2 = j3 + 1;
                    out[j3] = (byte) ((c2 & '?') | 128);
                } else if ((c2 < 55296 || 57343 < c2) && j2 <= limit - 3) {
                    int j4 = j2 + 1;
                    out[j2] = (byte) ((c2 >>> 12) | UVCCamera.DEFAULT_PREVIEW_HEIGHT);
                    int j5 = j4 + 1;
                    out[j4] = (byte) (((c2 >>> 6) & 63) | 128);
                    out[j5] = (byte) ((c2 & '?') | 128);
                    j2 = j5 + 1;
                } else if (j2 <= limit - 4) {
                    if (i + 1 != in.length()) {
                        i++;
                        char charAt2 = in.charAt(i);
                        char low = charAt2;
                        if (Character.isSurrogatePair(c2, charAt2)) {
                            int codePoint = Character.toCodePoint(c2, low);
                            int j6 = j2 + 1;
                            out[j2] = (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK);
                            int j7 = j6 + 1;
                            out[j6] = (byte) (((codePoint >>> 12) & 63) | 128);
                            int j8 = j7 + 1;
                            out[j7] = (byte) (((codePoint >>> 6) & 63) | 128);
                            j2 = j8 + 1;
                            out[j8] = (byte) ((codePoint & 63) | 128);
                        }
                    }
                    throw new UnpairedSurrogateException(i - 1, utf16Length);
                } else if (55296 > c2 || c2 > 57343 || (i + 1 != in.length() && Character.isSurrogatePair(c2, in.charAt(i + 1)))) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + c2 + " at index " + j2);
                } else {
                    throw new UnpairedSurrogateException(i, utf16Length);
                }
                i++;
            }
            return j2;
        }

        /* access modifiers changed from: package-private */
        public void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
            encodeUtf8Default(in, out);
        }

        private static int partialIsValidUtf8(byte[] bytes, int index, int limit) {
            while (index < limit && bytes[index] >= 0) {
                index++;
            }
            if (index >= limit) {
                return 0;
            }
            return partialIsValidUtf8NonAscii(bytes, index, limit);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bytes, int index, int limit) {
            while (index < limit) {
                int index2 = index + 1;
                byte index3 = bytes[index];
                int byte1 = index3;
                if (index3 >= 0) {
                    index = index2;
                } else if (byte1 < -32) {
                    if (index2 >= limit) {
                        return byte1;
                    }
                    if (byte1 >= -62) {
                        index = index2 + 1;
                        if (bytes[index2] > -65) {
                            int i = index;
                        }
                    }
                    return -1;
                } else if (byte1 < -16) {
                    if (index2 >= limit - 1) {
                        return Utf8.incompleteStateFor(bytes, index2, limit);
                    }
                    int index4 = index2 + 1;
                    byte index5 = bytes[index2];
                    int byte2 = index5;
                    if (index5 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                        index = index4 + 1;
                        if (bytes[index4] > -65) {
                            int i2 = index;
                        }
                    }
                    return -1;
                } else if (index2 >= limit - 2) {
                    return Utf8.incompleteStateFor(bytes, index2, limit);
                } else {
                    int index6 = index2 + 1;
                    byte index7 = bytes[index2];
                    int byte22 = index7;
                    if (index7 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0) {
                        int index8 = index6 + 1;
                        if (bytes[index6] <= -65) {
                            index = index8 + 1;
                            if (bytes[index8] > -65) {
                            }
                        }
                    }
                    return -1;
                }
            }
            return 0;
        }
    }

    static final class UnsafeProcessor extends Processor {
        UnsafeProcessor() {
        }

        static boolean isAvailable() {
            return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8(int state, byte[] bytes, int index, int limit) {
            int i = state;
            byte[] bArr = bytes;
            int i2 = index;
            int i3 = limit;
            if ((i2 | i3 | (bArr.length - i3)) >= 0) {
                long offset = UnsafeUtil.getArrayBaseOffset() + ((long) i2);
                long offsetLimit = UnsafeUtil.getArrayBaseOffset() + ((long) i3);
                if (i != 0) {
                    if (offset >= offsetLimit) {
                        return i;
                    }
                    int byte1 = (byte) i;
                    if (byte1 < -32) {
                        if (byte1 >= -62) {
                            long offset2 = 1 + offset;
                            if (UnsafeUtil.getByte(bArr, offset) > -65) {
                                long j = offset2;
                            } else {
                                offset = offset2;
                            }
                        }
                        return -1;
                    } else if (byte1 < -16) {
                        int byte2 = (byte) (~(i >> 8));
                        if (byte2 == 0) {
                            long offset3 = offset + 1;
                            byte2 = UnsafeUtil.getByte(bArr, offset);
                            if (offset3 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte2);
                            }
                            offset = offset3;
                        }
                        if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                            long offset4 = 1 + offset;
                            if (UnsafeUtil.getByte(bArr, offset) > -65) {
                                long j2 = offset4;
                            } else {
                                offset = offset4;
                            }
                        }
                        return -1;
                    } else {
                        int byte22 = (byte) (~(i >> 8));
                        int byte3 = 0;
                        if (byte22 == 0) {
                            long offset5 = offset + 1;
                            byte22 = UnsafeUtil.getByte(bArr, offset);
                            if (offset5 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22);
                            }
                            offset = offset5;
                        } else {
                            byte3 = (byte) (i >> 16);
                        }
                        if (byte3 == 0) {
                            long offset6 = offset + 1;
                            byte3 = UnsafeUtil.getByte(bArr, offset);
                            if (offset6 >= offsetLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22, byte3);
                            }
                            offset = offset6;
                        }
                        if (byte22 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0 && byte3 <= -65) {
                            long offset7 = 1 + offset;
                            if (UnsafeUtil.getByte(bArr, offset) > -65) {
                                long j3 = offset7;
                            } else {
                                offset = offset7;
                            }
                        }
                        return -1;
                    }
                }
                return partialIsValidUtf8(bArr, offset, (int) (offsetLimit - offset));
            }
            throw new ArrayIndexOutOfBoundsException(String.format("Array length=%d, index=%d, limit=%d", new Object[]{Integer.valueOf(bArr.length), Integer.valueOf(index), Integer.valueOf(limit)}));
        }

        /* access modifiers changed from: package-private */
        public int partialIsValidUtf8Direct(int state, ByteBuffer buffer, int index, int limit) {
            int i = state;
            int i2 = index;
            if ((i2 | limit | (buffer.limit() - limit)) >= 0) {
                long address = UnsafeUtil.addressOffset(buffer) + ((long) i2);
                long addressLimit = ((long) (limit - i2)) + address;
                if (i != 0) {
                    if (address >= addressLimit) {
                        return i;
                    }
                    int byte1 = (byte) i;
                    if (byte1 < -32) {
                        if (byte1 >= -62) {
                            long address2 = 1 + address;
                            if (UnsafeUtil.getByte(address) > -65) {
                                long j = address2;
                            } else {
                                address = address2;
                            }
                        }
                        return -1;
                    } else if (byte1 < -16) {
                        int byte2 = (byte) (~(i >> 8));
                        if (byte2 == 0) {
                            long address3 = address + 1;
                            byte2 = UnsafeUtil.getByte(address);
                            if (address3 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte2);
                            }
                            address = address3;
                        }
                        if (byte2 <= -65 && ((byte1 != -32 || byte2 >= -96) && (byte1 != -19 || byte2 < -96))) {
                            long address4 = 1 + address;
                            if (UnsafeUtil.getByte(address) > -65) {
                                long j2 = address4;
                            } else {
                                address = address4;
                            }
                        }
                        return -1;
                    } else {
                        int byte22 = (byte) (~(i >> 8));
                        int byte3 = 0;
                        if (byte22 == 0) {
                            long address5 = address + 1;
                            byte22 = UnsafeUtil.getByte(address);
                            if (address5 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22);
                            }
                            address = address5;
                        } else {
                            byte3 = (byte) (i >> 16);
                        }
                        if (byte3 == 0) {
                            long address6 = address + 1;
                            byte3 = UnsafeUtil.getByte(address);
                            if (address6 >= addressLimit) {
                                return Utf8.incompleteStateFor(byte1, byte22, byte3);
                            }
                            address = address6;
                        }
                        if (byte22 <= -65 && (((byte1 << 28) + (byte22 + 112)) >> 30) == 0 && byte3 <= -65) {
                            long address7 = 1 + address;
                            if (UnsafeUtil.getByte(address) > -65) {
                                long j3 = address7;
                            } else {
                                address = address7;
                            }
                        }
                        return -1;
                    }
                }
                return partialIsValidUtf8(address, (int) (addressLimit - address));
            }
            throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", new Object[]{Integer.valueOf(buffer.limit()), Integer.valueOf(index), Integer.valueOf(limit)}));
        }

        /* access modifiers changed from: package-private */
        public int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
            long outIx;
            char c;
            long j;
            long outIx2;
            CharSequence charSequence = in;
            byte[] bArr = out;
            int i = offset;
            int i2 = length;
            long outIx3 = UnsafeUtil.getArrayBaseOffset() + ((long) i);
            long outLimit = ((long) i2) + outIx3;
            int inLimit = in.length();
            if (inLimit > i2 || bArr.length - i2 < i) {
                long j2 = outLimit;
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(inLimit - 1) + " at index " + (offset + length));
            }
            int inIx = 0;
            while (true) {
                c = 128;
                j = 1;
                if (inIx >= inLimit) {
                    break;
                }
                char charAt = charSequence.charAt(inIx);
                char c2 = charAt;
                if (charAt >= 128) {
                    break;
                }
                UnsafeUtil.putByte(bArr, outIx, (byte) c2);
                inIx++;
                outIx3 = outIx + 1;
            }
            if (inIx == inLimit) {
                return (int) (outIx - UnsafeUtil.getArrayBaseOffset());
            }
            while (inIx < inLimit) {
                char c3 = charSequence.charAt(inIx);
                if (c3 < c && outIx < outLimit) {
                    UnsafeUtil.putByte(bArr, outIx, (byte) c3);
                    outIx += j;
                    c = 128;
                    outIx2 = outLimit;
                } else if (c3 < 2048 && outIx <= outLimit - 2) {
                    long outIx4 = outIx + j;
                    UnsafeUtil.putByte(bArr, outIx, (byte) ((c3 >>> 6) | 960));
                    outIx = outIx4 + j;
                    UnsafeUtil.putByte(bArr, outIx4, (byte) ((c3 & '?') | 128));
                    outIx2 = outLimit;
                    c = 128;
                } else if ((c3 < 55296 || 57343 < c3) && outIx <= outLimit - 3) {
                    long outIx5 = outIx + 1;
                    UnsafeUtil.putByte(bArr, outIx, (byte) ((c3 >>> 12) | UVCCamera.DEFAULT_PREVIEW_HEIGHT));
                    long outIx6 = outIx5 + 1;
                    UnsafeUtil.putByte(bArr, outIx5, (byte) (((c3 >>> 6) & 63) | 128));
                    UnsafeUtil.putByte(bArr, outIx6, (byte) ((c3 & '?') | 128));
                    outIx2 = outLimit;
                    outIx = outIx6 + 1;
                    c = 128;
                } else if (outIx <= outLimit - 4) {
                    if (inIx + 1 != inLimit) {
                        inIx++;
                        char charAt2 = charSequence.charAt(inIx);
                        char low = charAt2;
                        if (Character.isSurrogatePair(c3, charAt2)) {
                            int codePoint = Character.toCodePoint(c3, low);
                            outIx2 = outLimit;
                            long outLimit2 = outIx + 1;
                            UnsafeUtil.putByte(bArr, outIx, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                            long outIx7 = outLimit2 + 1;
                            c = 128;
                            UnsafeUtil.putByte(bArr, outLimit2, (byte) (((codePoint >>> 12) & 63) | 128));
                            long outIx8 = outIx7 + 1;
                            UnsafeUtil.putByte(bArr, outIx7, (byte) (((codePoint >>> 6) & 63) | 128));
                            outIx = outIx8 + 1;
                            UnsafeUtil.putByte(bArr, outIx8, (byte) ((codePoint & 63) | 128));
                        }
                    }
                    throw new UnpairedSurrogateException(inIx - 1, inLimit);
                } else {
                    if (55296 > c3 || c3 > 57343 || (inIx + 1 != inLimit && Character.isSurrogatePair(c3, charSequence.charAt(inIx + 1)))) {
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + c3 + " at index " + outIx);
                    }
                    throw new UnpairedSurrogateException(inIx, inLimit);
                }
                inIx++;
                int i3 = offset;
                int i4 = length;
                outLimit = outIx2;
                j = 1;
            }
            return (int) (outIx - UnsafeUtil.getArrayBaseOffset());
        }

        /* access modifiers changed from: package-private */
        public void encodeUtf8Direct(CharSequence in, ByteBuffer out) {
            long outIx;
            char c;
            long j;
            long j2;
            long outIx2;
            String str;
            CharSequence charSequence = in;
            ByteBuffer byteBuffer = out;
            long address = UnsafeUtil.addressOffset(out);
            long outIx3 = ((long) out.position()) + address;
            long outLimit = ((long) out.limit()) + address;
            int inLimit = in.length();
            String str2 = " at index ";
            if (((long) inLimit) <= outLimit - outIx3) {
                int inIx = 0;
                while (true) {
                    c = 128;
                    j = 1;
                    if (inIx >= inLimit) {
                        break;
                    }
                    char charAt = charSequence.charAt(inIx);
                    char c2 = charAt;
                    if (charAt >= 128) {
                        break;
                    }
                    UnsafeUtil.putByte(outIx, (byte) c2);
                    inIx++;
                    outIx3 = outIx + 1;
                }
                if (inIx == inLimit) {
                    byteBuffer.position((int) (outIx - address));
                    return;
                }
                while (inIx < inLimit) {
                    char c3 = charSequence.charAt(inIx);
                    if (c3 < c && outIx < outLimit) {
                        long outIx4 = outIx + j;
                        UnsafeUtil.putByte(outIx, (byte) c3);
                        j2 = j;
                        str = str2;
                        outIx = outIx4;
                        outIx2 = address;
                    } else if (c3 >= 2048 || outIx > outLimit - 2) {
                        outIx2 = address;
                        if ((c3 < 55296 || 57343 < c3) && outIx <= outLimit - 3) {
                            long outIx5 = outIx + j;
                            UnsafeUtil.putByte(outIx, (byte) ((c3 >>> 12) | UVCCamera.DEFAULT_PREVIEW_HEIGHT));
                            long outIx6 = outIx5 + j;
                            UnsafeUtil.putByte(outIx5, (byte) (((c3 >>> 6) & 63) | 128));
                            UnsafeUtil.putByte(outIx6, (byte) ((c3 & '?') | 128));
                            outIx = outIx6 + j;
                            j2 = j;
                            str = str2;
                        } else if (outIx <= outLimit - 4) {
                            if (inIx + 1 != inLimit) {
                                inIx++;
                                char charAt2 = charSequence.charAt(inIx);
                                char low = charAt2;
                                if (Character.isSurrogatePair(c3, charAt2)) {
                                    int codePoint = Character.toCodePoint(c3, low);
                                    char c4 = low;
                                    long outIx7 = outIx + j;
                                    UnsafeUtil.putByte(outIx, (byte) ((codePoint >>> 18) | PsExtractor.VIDEO_STREAM_MASK));
                                    long outIx8 = outIx7 + 1;
                                    UnsafeUtil.putByte(outIx7, (byte) (((codePoint >>> 12) & 63) | 128));
                                    str = str2;
                                    long outIx9 = outIx8 + 1;
                                    UnsafeUtil.putByte(outIx8, (byte) (((codePoint >>> 6) & 63) | 128));
                                    j2 = 1;
                                    outIx = outIx9 + 1;
                                    UnsafeUtil.putByte(outIx9, (byte) ((codePoint & 63) | 128));
                                } else {
                                    char c5 = low;
                                }
                            }
                            throw new UnpairedSurrogateException(inIx - 1, inLimit);
                        } else {
                            String str3 = str2;
                            if (55296 > c3 || c3 > 57343 || (inIx + 1 != inLimit && Character.isSurrogatePair(c3, charSequence.charAt(inIx + 1)))) {
                                throw new ArrayIndexOutOfBoundsException("Failed writing " + c3 + str3 + outIx);
                            }
                            throw new UnpairedSurrogateException(inIx, inLimit);
                        }
                    } else {
                        outIx2 = address;
                        long outIx10 = outIx + j;
                        UnsafeUtil.putByte(outIx, (byte) ((c3 >>> 6) | 960));
                        UnsafeUtil.putByte(outIx10, (byte) ((c3 & '?') | 128));
                        outIx = outIx10 + j;
                        j2 = j;
                        str = str2;
                    }
                    inIx++;
                    ByteBuffer byteBuffer2 = out;
                    str2 = str;
                    address = outIx2;
                    j = j2;
                    c = 128;
                }
                out.position((int) (outIx - address));
                return;
            }
            throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(inLimit - 1) + str2 + out.limit());
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bytes, long offset, int maxChars) {
            int remaining = maxChars;
            if (remaining < 16) {
                return 0;
            }
            int unaligned = ((int) offset) & 7;
            int j = unaligned;
            while (j > 0) {
                long offset2 = 1 + offset;
                if (UnsafeUtil.getByte(bytes, offset) < 0) {
                    return unaligned - j;
                }
                j--;
                offset = offset2;
            }
            int remaining2 = remaining - unaligned;
            while (remaining2 >= 8 && (UnsafeUtil.getLong(bytes, offset) & Utf8.ASCII_MASK_LONG) == 0) {
                offset += 8;
                remaining2 -= 8;
            }
            return maxChars - remaining2;
        }

        private static int unsafeEstimateConsecutiveAscii(long address, int maxChars) {
            int remaining = maxChars;
            if (remaining < 16) {
                return 0;
            }
            int unaligned = ((int) address) & 7;
            int j = unaligned;
            while (j > 0) {
                long address2 = 1 + address;
                if (UnsafeUtil.getByte(address) < 0) {
                    return unaligned - j;
                }
                j--;
                address = address2;
            }
            int remaining2 = remaining - unaligned;
            while (remaining2 >= 8 && (UnsafeUtil.getLong(address) & Utf8.ASCII_MASK_LONG) == 0) {
                address += 8;
                remaining2 -= 8;
            }
            return maxChars - remaining2;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003b, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x006a, code lost:
            return -1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int partialIsValidUtf8(byte[] r11, long r12, int r14) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r11, r12, r14)
                int r14 = r14 - r0
                long r1 = (long) r0
                long r12 = r12 + r1
            L_0x0007:
                r1 = 0
            L_0x0008:
                r2 = 1
                if (r14 <= 0) goto L_0x001a
                long r4 = r12 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r12)
                r1 = r12
                if (r12 < 0) goto L_0x0019
                int r14 = r14 + -1
                r12 = r4
                goto L_0x0008
            L_0x0019:
                r12 = r4
            L_0x001a:
                if (r14 != 0) goto L_0x001e
                r2 = 0
                return r2
            L_0x001e:
                int r14 = r14 + -1
                r4 = -32
                r5 = -65
                r6 = -1
                if (r1 >= r4) goto L_0x003c
                if (r14 != 0) goto L_0x002a
                return r1
            L_0x002a:
                int r14 = r14 + -1
                r4 = -62
                if (r1 < r4) goto L_0x003b
                long r2 = r2 + r12
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r12)
                if (r12 <= r5) goto L_0x0039
                r12 = r2
                goto L_0x003b
            L_0x0039:
                r12 = r2
                goto L_0x0099
            L_0x003b:
                return r6
            L_0x003c:
                r7 = -16
                if (r1 >= r7) goto L_0x006b
                r7 = 2
                if (r14 >= r7) goto L_0x0048
                int r2 = unsafeIncompleteStateFor(r11, r1, r12, r14)
                return r2
            L_0x0048:
                int r14 = r14 + -2
                long r7 = r12 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r12)
                r13 = r12
                if (r12 > r5) goto L_0x006a
                r12 = -96
                if (r1 != r4) goto L_0x0059
                if (r13 < r12) goto L_0x006a
            L_0x0059:
                r4 = -19
                if (r1 != r4) goto L_0x005f
                if (r13 >= r12) goto L_0x006a
            L_0x005f:
                long r2 = r2 + r7
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r7)
                if (r12 <= r5) goto L_0x0068
                r7 = r2
                goto L_0x006a
            L_0x0068:
                r12 = r2
                goto L_0x0099
            L_0x006a:
                return r6
            L_0x006b:
                r4 = 3
                if (r14 >= r4) goto L_0x0073
                int r2 = unsafeIncompleteStateFor(r11, r1, r12, r14)
                return r2
            L_0x0073:
                int r14 = r14 + -3
                long r7 = r12 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r12)
                r13 = r12
                if (r12 > r5) goto L_0x009c
                int r12 = r1 << 28
                int r4 = r13 + 112
                int r12 = r12 + r4
                int r12 = r12 >> 30
                if (r12 != 0) goto L_0x009c
                long r9 = r7 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r7)
                if (r12 > r5) goto L_0x009b
                long r7 = r9 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r11, r9)
                if (r12 <= r5) goto L_0x0098
                goto L_0x009c
            L_0x0098:
                r12 = r7
            L_0x0099:
                goto L_0x0007
            L_0x009b:
                r7 = r9
            L_0x009c:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(byte[], long, int):int");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:20:0x003b, code lost:
            return -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0069, code lost:
            return -1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int partialIsValidUtf8(long r11, int r13) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r11, r13)
                long r1 = (long) r0
                long r11 = r11 + r1
                int r13 = r13 - r0
            L_0x0007:
                r1 = 0
            L_0x0008:
                r2 = 1
                if (r13 <= 0) goto L_0x001a
                long r4 = r11 + r2
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r11)
                r1 = r11
                if (r11 < 0) goto L_0x0019
                int r13 = r13 + -1
                r11 = r4
                goto L_0x0008
            L_0x0019:
                r11 = r4
            L_0x001a:
                if (r13 != 0) goto L_0x001e
                r2 = 0
                return r2
            L_0x001e:
                int r13 = r13 + -1
                r4 = -32
                r5 = -65
                r6 = -1
                if (r1 >= r4) goto L_0x003c
                if (r13 != 0) goto L_0x002a
                return r1
            L_0x002a:
                int r13 = r13 + -1
                r4 = -62
                if (r1 < r4) goto L_0x003b
                long r2 = r2 + r11
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r11)
                if (r11 <= r5) goto L_0x0039
                r11 = r2
                goto L_0x003b
            L_0x0039:
                r11 = r2
                goto L_0x0097
            L_0x003b:
                return r6
            L_0x003c:
                r7 = -16
                if (r1 >= r7) goto L_0x006a
                r7 = 2
                if (r13 >= r7) goto L_0x0048
                int r2 = unsafeIncompleteStateFor(r11, r1, r13)
                return r2
            L_0x0048:
                int r13 = r13 + -2
                long r7 = r11 + r2
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r11)
                if (r11 > r5) goto L_0x0069
                r12 = -96
                if (r1 != r4) goto L_0x0058
                if (r11 < r12) goto L_0x0069
            L_0x0058:
                r4 = -19
                if (r1 != r4) goto L_0x005e
                if (r11 >= r12) goto L_0x0069
            L_0x005e:
                long r2 = r2 + r7
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r7)
                if (r12 <= r5) goto L_0x0067
                r7 = r2
                goto L_0x0069
            L_0x0067:
                r11 = r2
                goto L_0x0097
            L_0x0069:
                return r6
            L_0x006a:
                r4 = 3
                if (r13 >= r4) goto L_0x0072
                int r2 = unsafeIncompleteStateFor(r11, r1, r13)
                return r2
            L_0x0072:
                int r13 = r13 + -3
                long r7 = r11 + r2
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r11)
                if (r11 > r5) goto L_0x009a
                int r12 = r1 << 28
                int r4 = r11 + 112
                int r12 = r12 + r4
                int r12 = r12 >> 30
                if (r12 != 0) goto L_0x009a
                long r9 = r7 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r7)
                if (r12 > r5) goto L_0x0099
                long r7 = r9 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r9)
                if (r12 <= r5) goto L_0x0096
                goto L_0x009a
            L_0x0096:
                r11 = r7
            L_0x0097:
                goto L_0x0007
            L_0x0099:
                r7 = r9
            L_0x009a:
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(long, int):int");
        }

        private static int unsafeIncompleteStateFor(byte[] bytes, int byte1, long offset, int remaining) {
            if (remaining == 0) {
                return Utf8.incompleteStateFor(byte1);
            }
            if (remaining == 1) {
                return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(bytes, offset));
            }
            if (remaining == 2) {
                return Utf8.incompleteStateFor(byte1, (int) UnsafeUtil.getByte(bytes, offset), (int) UnsafeUtil.getByte(bytes, 1 + offset));
            }
            throw new AssertionError();
        }

        private static int unsafeIncompleteStateFor(long address, int byte1, int remaining) {
            if (remaining == 0) {
                return Utf8.incompleteStateFor(byte1);
            }
            if (remaining == 1) {
                return Utf8.incompleteStateFor(byte1, UnsafeUtil.getByte(address));
            }
            if (remaining == 2) {
                return Utf8.incompleteStateFor(byte1, (int) UnsafeUtil.getByte(address), (int) UnsafeUtil.getByte(1 + address));
            }
            throw new AssertionError();
        }
    }

    private Utf8() {
    }
}
