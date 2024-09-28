package org.webrtc.ali;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.io.File;
import java.nio.ByteBuffer;

public class AliHardwareAudioEncoder {
    private final String MIME_NAME_AAC = MimeTypes.AUDIO_AAC;
    private final String TAG = "AliHardwareAudioEncoder";
    private ByteBuffer aacEncodedBuffer_ = ByteBuffer.allocateDirect(2048);
    private MediaCodec aacEncoder_ = null;
    private MediaFormat aacFormat_ = null;
    private ByteBuffer[] aacInputBuffers_;
    private ByteBuffer[] aacOutputBuffers_;
    private MediaCodec.BufferInfo bufferInfo_ = new MediaCodec.BufferInfo();
    private int channels_ = 1;
    private Context context_;
    private final int[] kAACProfiles = {2, 5, 39, 29};
    private int[] kSampleRateIndexArray = {96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, AudioEditConstant.ExportSampleRate, 12000, 11025, 8000};
    private long nativeAudioCodec_ = 0;
    private int profile_ = 0;
    private byte[] sampleRateIndexArray = new byte[16];
    private byte sampleRateIndex_ = -1;

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer, long j);

    private native void nativeCodecInit(long j);

    private native void nativeDataBufferIsReady(int i, long j);

    public AliHardwareAudioEncoder(Context context) {
        this.context_ = context;
    }

    public void noExistCreateFile(File mediaFile) {
        try {
            if (!mediaFile.exists()) {
                mediaFile.createNewFile();
                return;
            }
            mediaFile.delete();
            mediaFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildADTSHeader(byte[] buffer, int bufsize) {
        byte chanCfg = (byte) this.channels_;
        buffer[0] = -1;
        buffer[1] = -7;
        buffer[2] = (byte) (((((byte) this.kAACProfiles[this.profile_]) - 1) << 6) + (this.sampleRateIndex_ << 2) + (chanCfg >> 2));
        buffer[3] = (byte) (((chanCfg & 3) << 6) + (bufsize >> 11));
        buffer[4] = (byte) ((bufsize & 2047) >> 3);
        buffer[5] = (byte) (((bufsize & 7) << 5) + 31);
        buffer[6] = -4;
    }

    public int createAACEncoder(int sampleRate, int channels, int bitRate, int profile) {
        Log.i("AliHardwareAudioEncoder", "aac encoder param sampleRate:" + sampleRate + " bitrate:" + bitRate + " profile = " + profile);
        if (profile < 0 || profile > 2 || channels < 0 || channels > 2) {
            return -1;
        }
        this.sampleRateIndex_ = -1;
        this.channels_ = channels;
        int useSampleRate = sampleRate;
        if (profile == 1) {
            useSampleRate = sampleRate / 2;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.kSampleRateIndexArray;
            if (i >= iArr.length) {
                break;
            } else if (useSampleRate == iArr[i]) {
                this.sampleRateIndex_ = (byte) i;
                break;
            } else {
                i++;
            }
        }
        if (this.sampleRateIndex_ < 0) {
            return -2;
        }
        this.profile_ = profile;
        try {
            MediaFormat createAudioFormat = MediaFormat.createAudioFormat(MimeTypes.AUDIO_AAC, sampleRate, channels);
            this.aacFormat_ = createAudioFormat;
            createAudioFormat.setInteger("aac-profile", this.kAACProfiles[this.profile_]);
            this.aacFormat_.setInteger("sample-rate", sampleRate);
            this.aacFormat_.setInteger("channel-count", this.channels_);
            this.aacFormat_.setInteger("bitrate", bitRate);
            MediaCodec createEncoderByType = MediaCodec.createEncoderByType(MimeTypes.AUDIO_AAC);
            this.aacEncoder_ = createEncoderByType;
            createEncoderByType.configure(this.aacFormat_, (Surface) null, (MediaCrypto) null, 1);
            if (this.aacEncoder_ == null) {
                return -3;
            }
            this.aacEncoder_.start();
            nativeCodecInit(this.nativeAudioCodec_);
            nativeCacheDirectBufferAddress(this.aacEncodedBuffer_, this.nativeAudioCodec_);
            return 0;
        } catch (Exception e) {
            Log.e("AliHardwareAudioEncoder", "Failed to create aac encoder msg:" + e.getMessage());
            return -4;
        }
    }

    public void releaseAACEncoder() {
        try {
            if (this.aacEncoder_ != null) {
                this.aacEncoder_.stop();
                this.aacEncoder_.release();
                this.aacEncoder_ = null;
            }
        } catch (Exception e) {
            Log.e("AliHardwareAudioEncoder", "Failed to release aac encoder msg:" + e.getMessage());
        }
    }

    public int encodeAACData(byte[] data) {
        ByteBuffer outputBuffer;
        int outSize;
        ByteBuffer inputBuffer;
        MediaCodec mediaCodec = this.aacEncoder_;
        if (mediaCodec == null) {
            return -1;
        }
        try {
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            if (inputBufferIndex != -1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    inputBuffer = this.aacEncoder_.getInputBuffer(inputBufferIndex);
                } else {
                    ByteBuffer[] inputBuffers = this.aacEncoder_.getInputBuffers();
                    this.aacInputBuffers_ = inputBuffers;
                    inputBuffer = inputBuffers[inputBufferIndex];
                }
                inputBuffer.clear();
                inputBuffer.put(data);
                this.aacEncoder_.queueInputBuffer(inputBufferIndex, 0, data.length, 0, 0);
            }
            int outputBufferIndex = this.aacEncoder_.dequeueOutputBuffer(this.bufferInfo_, 0);
            if (outputBufferIndex < 0) {
                return 0;
            }
            if (Build.VERSION.SDK_INT >= 21) {
                outputBuffer = this.aacEncoder_.getOutputBuffer(outputBufferIndex);
            } else {
                ByteBuffer[] outputBuffers = this.aacEncoder_.getOutputBuffers();
                this.aacOutputBuffers_ = outputBuffers;
                outputBuffer = outputBuffers[outputBufferIndex];
            }
            if ((this.bufferInfo_.flags & 2) == 2) {
                outSize = 0;
            } else {
                outSize = this.bufferInfo_.size;
            }
            outputBuffer.position(this.bufferInfo_.offset);
            outputBuffer.limit(this.bufferInfo_.offset + outSize);
            this.aacEncodedBuffer_.position(0);
            this.aacEncodedBuffer_.put(outputBuffer);
            this.aacEncoder_.releaseOutputBuffer(outputBufferIndex, false);
            return outSize;
        } catch (Exception e) {
            Log.e("AliHardwareAudioEncoder", "Failed to encoder audio data msg:" + e.getMessage());
            return -2;
        }
    }

    public ByteBuffer getAacEncodedBuffer() {
        return this.aacEncodedBuffer_;
    }
}
