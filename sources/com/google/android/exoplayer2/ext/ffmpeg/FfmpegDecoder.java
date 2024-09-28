package com.google.android.exoplayer2.ext.ffmpeg;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;

final class FfmpegDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FfmpegDecoderException> {
    private static final int DECODER_ERROR_INVALID_DATA = -1;
    private static final int DECODER_ERROR_OTHER = -2;
    private static final int OUTPUT_BUFFER_SIZE_16BIT = 65536;
    private static final int OUTPUT_BUFFER_SIZE_32BIT = 131072;
    private volatile int channelCount;
    private final String codecName;
    private final int encoding;
    private final byte[] extraData;
    private boolean hasOutputFormat;
    private long nativeContext;
    private final int outputBufferSize;
    private volatile int sampleRate;

    private native int ffmpegDecode(long j, ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2);

    private native int ffmpegGetChannelCount(long j);

    private native int ffmpegGetSampleRate(long j);

    private native long ffmpegInitialize(String str, byte[] bArr, boolean z, int i, int i2);

    private native void ffmpegRelease(long j);

    private native long ffmpegReset(long j, byte[] bArr);

    public FfmpegDecoder(int numInputBuffers, int numOutputBuffers, int initialInputBufferSize, Format format, boolean outputFloat) throws FfmpegDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        Assertions.checkNotNull(format.sampleMimeType);
        this.codecName = (String) Assertions.checkNotNull(FfmpegLibrary.getCodecName(format.sampleMimeType, format.pcmEncoding));
        this.extraData = getExtraData(format.sampleMimeType, format.initializationData);
        this.encoding = outputFloat ? 4 : 2;
        this.outputBufferSize = outputFloat ? 131072 : 65536;
        long ffmpegInitialize = ffmpegInitialize(this.codecName, this.extraData, outputFloat, format.sampleRate, format.channelCount);
        this.nativeContext = ffmpegInitialize;
        if (ffmpegInitialize != 0) {
            setInitialInputBufferSize(initialInputBufferSize);
            return;
        }
        throw new FfmpegDecoderException("Initialization failed.");
    }

    public String getName() {
        return "ffmpeg" + FfmpegLibrary.getVersion() + "-" + this.codecName;
    }

    /* access modifiers changed from: protected */
    public DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(2);
    }

    /* access modifiers changed from: protected */
    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    /* access modifiers changed from: protected */
    public FfmpegDecoderException createUnexpectedDecodeException(Throwable error) {
        return new FfmpegDecoderException("Unexpected decode error", error);
    }

    /* access modifiers changed from: protected */
    public FfmpegDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        if (reset) {
            long ffmpegReset = ffmpegReset(this.nativeContext, this.extraData);
            this.nativeContext = ffmpegReset;
            if (ffmpegReset == 0) {
                return new FfmpegDecoderException("Error resetting (see logcat).");
            }
        }
        ByteBuffer inputData = inputBuffer.data;
        int inputSize = inputData.limit();
        int result = ffmpegDecode(this.nativeContext, inputData, inputSize, outputBuffer.init(inputBuffer.timeUs, this.outputBufferSize), this.outputBufferSize);
        if (result == -1) {
            outputBuffer.setFlags(Integer.MIN_VALUE);
            return null;
        } else if (result == -2) {
            return new FfmpegDecoderException("Error decoding (see logcat).");
        } else {
            if (!this.hasOutputFormat) {
                this.channelCount = ffmpegGetChannelCount(this.nativeContext);
                this.sampleRate = ffmpegGetSampleRate(this.nativeContext);
                if (this.sampleRate == 0 && "alac".equals(this.codecName)) {
                    Assertions.checkNotNull(this.extraData);
                    ParsableByteArray parsableExtraData = new ParsableByteArray(this.extraData);
                    parsableExtraData.setPosition(this.extraData.length - 4);
                    this.sampleRate = parsableExtraData.readUnsignedIntToInt();
                }
                this.hasOutputFormat = true;
            }
            outputBuffer.data.position(0);
            outputBuffer.data.limit(result);
            return null;
        }
    }

    public void release() {
        super.release();
        ffmpegRelease(this.nativeContext);
        this.nativeContext = 0;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getEncoding() {
        return this.encoding;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] getExtraData(java.lang.String r8, java.util.List<byte[]> r9) {
        /*
            int r0 = r8.hashCode()
            r1 = 3
            r2 = 2
            r3 = 1
            r4 = 0
            switch(r0) {
                case -1003765268: goto L_0x002a;
                case -53558318: goto L_0x0020;
                case 1504470054: goto L_0x0016;
                case 1504891608: goto L_0x000c;
                default: goto L_0x000b;
            }
        L_0x000b:
            goto L_0x0034
        L_0x000c:
            java.lang.String r0 = "audio/opus"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000b
            r0 = 2
            goto L_0x0035
        L_0x0016:
            java.lang.String r0 = "audio/alac"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000b
            r0 = 1
            goto L_0x0035
        L_0x0020:
            java.lang.String r0 = "audio/mp4a-latm"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000b
            r0 = 0
            goto L_0x0035
        L_0x002a:
            java.lang.String r0 = "audio/vorbis"
            boolean r0 = r8.equals(r0)
            if (r0 == 0) goto L_0x000b
            r0 = 3
            goto L_0x0035
        L_0x0034:
            r0 = -1
        L_0x0035:
            if (r0 == 0) goto L_0x0084
            if (r0 == r3) goto L_0x0084
            if (r0 == r2) goto L_0x0084
            if (r0 == r1) goto L_0x003f
            r0 = 0
            return r0
        L_0x003f:
            java.lang.Object r0 = r9.get(r4)
            byte[] r0 = (byte[]) r0
            java.lang.Object r5 = r9.get(r3)
            byte[] r5 = (byte[]) r5
            int r6 = r0.length
            int r7 = r5.length
            int r6 = r6 + r7
            int r6 = r6 + 6
            byte[] r6 = new byte[r6]
            int r7 = r0.length
            int r7 = r7 >> 8
            byte r7 = (byte) r7
            r6[r4] = r7
            int r7 = r0.length
            r7 = r7 & 255(0xff, float:3.57E-43)
            byte r7 = (byte) r7
            r6[r3] = r7
            int r3 = r0.length
            java.lang.System.arraycopy(r0, r4, r6, r2, r3)
            int r3 = r0.length
            int r3 = r3 + r2
            r6[r3] = r4
            int r2 = r0.length
            int r2 = r2 + r1
            r6[r2] = r4
            int r1 = r0.length
            int r1 = r1 + 4
            int r2 = r5.length
            int r2 = r2 >> 8
            byte r2 = (byte) r2
            r6[r1] = r2
            int r1 = r0.length
            int r1 = r1 + 5
            int r2 = r5.length
            r2 = r2 & 255(0xff, float:3.57E-43)
            byte r2 = (byte) r2
            r6[r1] = r2
            int r1 = r0.length
            int r1 = r1 + 6
            int r2 = r5.length
            java.lang.System.arraycopy(r5, r4, r6, r1, r2)
            return r6
        L_0x0084:
            java.lang.Object r0 = r9.get(r4)
            byte[] r0 = (byte[]) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ext.ffmpeg.FfmpegDecoder.getExtraData(java.lang.String, java.util.List):byte[]");
    }
}
