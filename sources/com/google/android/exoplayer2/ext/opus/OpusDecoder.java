package com.google.android.exoplayer2.ext.opus;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.decoder.CryptoInfo;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.decoder.SimpleDecoder;
import com.google.android.exoplayer2.decoder.SimpleOutputBuffer;
import com.google.android.exoplayer2.drm.DecryptionException;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import kotlin.UByte;

final class OpusDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, OpusDecoderException> {
    private static final int DECODE_ERROR = -1;
    private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
    private static final int DRM_ERROR = -2;
    private static final int NO_ERROR = 0;
    private static final int SAMPLE_RATE = 48000;
    private final int channelCount;
    private final ExoMediaCrypto exoMediaCrypto;
    private final int headerSeekPreRollSamples;
    private final int headerSkipSamples;
    private final long nativeDecoderContext;
    private int skipSamples;

    private native void opusClose(long j);

    private native int opusDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer);

    private native int opusGetErrorCode(long j);

    private native String opusGetErrorMessage(long j);

    private native long opusInit(int i, int i2, int i3, int i4, int i5, byte[] bArr);

    private native void opusReset(long j);

    private native int opusSecureDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer, int i2, ExoMediaCrypto exoMediaCrypto2, int i3, byte[] bArr, byte[] bArr2, int i4, int[] iArr, int[] iArr2);

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OpusDecoder(int numInputBuffers, int numOutputBuffers, int initialInputBufferSize, List<byte[]> initializationData, ExoMediaCrypto exoMediaCrypto2) throws OpusDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        int numStreams;
        int numCoupled;
        List<byte[]> list = initializationData;
        ExoMediaCrypto exoMediaCrypto3 = exoMediaCrypto2;
        this.exoMediaCrypto = exoMediaCrypto3;
        if (exoMediaCrypto3 == null || OpusLibrary.opusIsSecureDecodeSupported()) {
            byte[] headerBytes = list.get(0);
            if (headerBytes.length >= 19) {
                byte b = headerBytes[9] & UByte.MAX_VALUE;
                this.channelCount = b;
                if (b <= 8) {
                    int preskip = readLittleEndian16(headerBytes, 10);
                    int gain = readLittleEndian16(headerBytes, 16);
                    byte[] streamMap = new byte[8];
                    if (headerBytes[18] == 0) {
                        int i = this.channelCount;
                        if (i <= 2) {
                            int numCoupled2 = i == 2 ? 1 : 0;
                            streamMap[0] = 0;
                            streamMap[1] = 1;
                            numCoupled = numCoupled2;
                            numStreams = 1;
                        } else {
                            throw new OpusDecoderException("Invalid Header, missing stream map.");
                        }
                    } else {
                        int length = headerBytes.length;
                        int i2 = this.channelCount;
                        if (length >= i2 + 21) {
                            int numStreams2 = headerBytes[19] & 255;
                            System.arraycopy(headerBytes, 21, streamMap, 0, i2);
                            numCoupled = headerBytes[20] & 255;
                            numStreams = numStreams2;
                        } else {
                            int i3 = initialInputBufferSize;
                            throw new OpusDecoderException("Header size is too small.");
                        }
                    }
                    if (initializationData.size() != 3) {
                        this.headerSkipSamples = preskip;
                        this.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
                    } else if (list.get(1).length == 8 && list.get(2).length == 8) {
                        long codecDelayNs = ByteBuffer.wrap(list.get(1)).order(ByteOrder.nativeOrder()).getLong();
                        long seekPreRollNs = ByteBuffer.wrap(list.get(2)).order(ByteOrder.nativeOrder()).getLong();
                        this.headerSkipSamples = nsToSamples(codecDelayNs);
                        this.headerSeekPreRollSamples = nsToSamples(seekPreRollNs);
                    } else {
                        throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
                    }
                    long opusInit = opusInit(SAMPLE_RATE, this.channelCount, numStreams, numCoupled, gain, streamMap);
                    this.nativeDecoderContext = opusInit;
                    if (opusInit != 0) {
                        setInitialInputBufferSize(initialInputBufferSize);
                    } else {
                        int i4 = initialInputBufferSize;
                        throw new OpusDecoderException("Failed to initialize decoder");
                    }
                } else {
                    int i5 = initialInputBufferSize;
                    throw new OpusDecoderException("Invalid channel count: " + this.channelCount);
                }
            } else {
                int i6 = initialInputBufferSize;
                throw new OpusDecoderException("Header size is too small.");
            }
        } else {
            throw new OpusDecoderException("Opus decoder does not support secure decode.");
        }
    }

    public String getName() {
        return "libopus" + OpusLibrary.getVersion();
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
    public OpusDecoderException createUnexpectedDecodeException(Throwable error) {
        return new OpusDecoderException("Unexpected decode error", error);
    }

    /* access modifiers changed from: protected */
    public OpusDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        OpusDecoder opusDecoder;
        int result;
        DecoderInputBuffer decoderInputBuffer = inputBuffer;
        SimpleOutputBuffer simpleOutputBuffer = outputBuffer;
        if (reset) {
            opusReset(this.nativeDecoderContext);
            this.skipSamples = decoderInputBuffer.timeUs == 0 ? this.headerSkipSamples : this.headerSeekPreRollSamples;
        }
        ByteBuffer inputData = decoderInputBuffer.data;
        CryptoInfo cryptoInfo = decoderInputBuffer.cryptoInfo;
        if (inputBuffer.isEncrypted()) {
            long j = this.nativeDecoderContext;
            long j2 = decoderInputBuffer.timeUs;
            int limit = inputData.limit();
            ExoMediaCrypto exoMediaCrypto2 = this.exoMediaCrypto;
            int i = cryptoInfo.mode;
            byte[] bArr = cryptoInfo.key;
            byte[] bArr2 = cryptoInfo.iv;
            int i2 = cryptoInfo.numSubSamples;
            CryptoInfo cryptoInfo2 = cryptoInfo;
            ByteBuffer byteBuffer = inputData;
            result = opusSecureDecode(j, j2, inputData, limit, outputBuffer, SAMPLE_RATE, exoMediaCrypto2, i, bArr, bArr2, i2, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
            opusDecoder = this;
            DecoderInputBuffer decoderInputBuffer2 = inputBuffer;
        } else {
            ByteBuffer inputData2 = inputData;
            opusDecoder = this;
            result = opusDecode(opusDecoder.nativeDecoderContext, inputBuffer.timeUs, inputData2, inputData2.limit(), outputBuffer);
        }
        if (result >= 0) {
            SimpleOutputBuffer simpleOutputBuffer2 = outputBuffer;
            ByteBuffer outputData = simpleOutputBuffer2.data;
            outputData.position(0);
            outputData.limit(result);
            int i3 = opusDecoder.skipSamples;
            if (i3 <= 0) {
                return null;
            }
            int bytesPerSample = opusDecoder.channelCount * 2;
            int skipBytes = i3 * bytesPerSample;
            if (result <= skipBytes) {
                opusDecoder.skipSamples = i3 - (result / bytesPerSample);
                simpleOutputBuffer2.addFlag(Integer.MIN_VALUE);
                outputData.position(result);
                return null;
            }
            opusDecoder.skipSamples = 0;
            outputData.position(skipBytes);
            return null;
        } else if (result == -2) {
            String message = "Drm error: " + opusDecoder.opusGetErrorMessage(opusDecoder.nativeDecoderContext);
            return new OpusDecoderException(message, new DecryptionException(opusDecoder.opusGetErrorCode(opusDecoder.nativeDecoderContext), message));
        } else {
            return new OpusDecoderException("Decode error: " + opusDecoder.opusGetErrorMessage((long) result));
        }
    }

    public void release() {
        super.release();
        opusClose(this.nativeDecoderContext);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    private static int nsToSamples(long ns) {
        return (int) ((48000 * ns) / C.NANOS_PER_SECOND);
    }

    private static int readLittleEndian16(byte[] input, int offset) {
        return (input[offset] & 255) | ((input[offset + 1] & UByte.MAX_VALUE) << 8);
    }
}
