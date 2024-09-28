package org.webrtc.ali;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.ByteBuffer;

public class AliHardwareAudioDecoder {
    private static int DECODE_READ_BUFFER_SIZE = 98304;
    private static int HTTP_REQUEST_TIMEOUT = 6000;
    private static int MAX_DECODER_RETRY_COUNT = 64;
    private static String TAG = "MediaCodecAudioDecoder";
    private int channels_;
    private Context context_ = null;
    private boolean decodeIsReady_;
    private ByteBuffer decodeReadBuffer_;
    private MediaExtractor extractor_;
    private long fileLength_;
    MediaCodec.BufferInfo info;
    private ByteBuffer[] inputBuffers_;
    private boolean inputStreamEnd_;
    private boolean isMeizu_;
    private boolean isVivo_;
    private MediaCodec mediaCodec_;
    private long nativeAudioCodec_;
    private ByteBuffer[] outputBuffers_;
    private boolean outputStreamEnd_;
    private int retryCount_;
    private int sampleRate_;
    private MediaFormat trackFormat_;

    private native void nativeCacheDirectBufferAddress(ByteBuffer byteBuffer, long j);

    private native void nativeCodecInit(int i, int i2, long j, long j2);

    private native void nativeDataBufferIsReady(int i, int i2, int i3, long j);

    public AliHardwareAudioDecoder(Context context) {
        boolean z = false;
        this.inputStreamEnd_ = false;
        this.outputStreamEnd_ = false;
        this.decodeReadBuffer_ = ByteBuffer.allocateDirect(DECODE_READ_BUFFER_SIZE);
        this.mediaCodec_ = null;
        this.extractor_ = null;
        this.trackFormat_ = null;
        this.decodeIsReady_ = false;
        this.sampleRate_ = 48000;
        this.channels_ = 2;
        this.retryCount_ = 0;
        this.nativeAudioCodec_ = 0;
        this.isMeizu_ = false;
        this.isVivo_ = false;
        this.info = new MediaCodec.BufferInfo();
        this.context_ = context;
        this.isMeizu_ = Build.BRAND.toLowerCase().contains("meizu") || Build.MANUFACTURER.toLowerCase().contains("meizu");
        this.isVivo_ = (Build.BRAND.toLowerCase().contains("vivo") || Build.MANUFACTURER.toLowerCase().contains("vivo")) ? true : z;
    }

    public int initDecoder(String filename) {
        String str = filename;
        try {
            this.retryCount_ = 0;
            this.decodeReadBuffer_.clear();
            String str2 = TAG;
            Logging.d(str2, "open Audio File name:" + str);
            boolean isHttpFile = filename.toLowerCase().startsWith("http");
            String assets_prefix = "/assets/";
            boolean isAssetsFile = str.startsWith(assets_prefix);
            MediaExtractor mediaExtractor = new MediaExtractor();
            this.extractor_ = mediaExtractor;
            if (isAssetsFile) {
                if (this.context_ == null) {
                    return -1;
                }
                try {
                    AssetFileDescriptor assetfd = this.context_.getAssets().openFd(str.substring(assets_prefix.length()));
                    this.extractor_.setDataSource(assetfd.getFileDescriptor(), assetfd.getStartOffset(), assetfd.getLength());
                } catch (IOException e) {
                    String str3 = TAG;
                    Logging.e(str3, "Failed to open assert File:" + str);
                    return -2;
                }
            } else if (isHttpFile) {
                try {
                    HttpURLConnection.setFollowRedirects(true);
                    HttpURLConnection con = (HttpURLConnection) new URL(str).openConnection();
                    con.setConnectTimeout(HTTP_REQUEST_TIMEOUT);
                    con.setReadTimeout(HTTP_REQUEST_TIMEOUT);
                    con.connect();
                    if (con.getResponseCode() != 200) {
                        return -3;
                    }
                    this.extractor_.setDataSource(str);
                } catch (SocketTimeoutException e2) {
                    String str4 = TAG;
                    Logging.e(str4, "Failed to Connect URL:" + str + " timeout!");
                    return -4;
                } catch (IOException ex) {
                    String str5 = TAG;
                    Logging.e(str5, "Failed to Connect URL:" + str + " exception!");
                    ex.printStackTrace();
                    return -5;
                }
            } else {
                mediaExtractor.setDataSource(str);
            }
            int nTrackCount = this.extractor_.getTrackCount();
            for (int i = 0; i < nTrackCount; i++) {
                this.extractor_.unselectTrack(i);
            }
            int i2 = 0;
            while (true) {
                if (i2 >= nTrackCount) {
                    break;
                }
                MediaFormat trackFormat = this.extractor_.getTrackFormat(i2);
                this.trackFormat_ = trackFormat;
                String mimeType = trackFormat.getString("mime");
                if (mimeType.contains("audio/")) {
                    this.extractor_.selectTrack(i2);
                    MediaCodec createDecoderByType = MediaCodec.createDecoderByType(mimeType);
                    this.mediaCodec_ = createDecoderByType;
                    createDecoderByType.configure(this.trackFormat_, (Surface) null, (MediaCrypto) null, 0);
                    break;
                }
                i2++;
            }
            if (this.mediaCodec_ == null) {
                return -6;
            }
            this.mediaCodec_.start();
            this.sampleRate_ = this.trackFormat_.getInteger("sample-rate");
            this.channels_ = this.trackFormat_.getInteger("channel-count");
            long j = this.trackFormat_.getLong("durationUs");
            this.fileLength_ = j;
            nativeCodecInit(this.sampleRate_, this.channels_, j, this.nativeAudioCodec_);
            nativeCacheDirectBufferAddress(this.decodeReadBuffer_, this.nativeAudioCodec_);
            String str6 = TAG;
            Logging.d(str6, "open Audio File name:" + str + " succ!");
            return 0;
        } catch (Exception ex2) {
            String str7 = TAG;
            Logging.e(str7, "Failed to open file:" + str);
            ex2.printStackTrace();
            return -7;
        }
    }

    public int getAudioChannels() {
        return this.channels_;
    }

    public int getAudioSampleRate() {
        return this.sampleRate_;
    }

    public long getFileLength() {
        return this.fileLength_;
    }

    public void rewindDecoder() {
        try {
            this.extractor_.seekTo(0, 1);
            this.mediaCodec_.flush();
        } catch (Exception e) {
            Logging.e(TAG, "Failed to rewind file!");
        }
        this.inputStreamEnd_ = false;
        this.outputStreamEnd_ = false;
        this.decodeIsReady_ = false;
    }

    public void releaseDecoder() {
        try {
            if (this.mediaCodec_ != null) {
                this.mediaCodec_.stop();
                this.mediaCodec_.release();
                this.mediaCodec_ = null;
            }
            if (this.extractor_ != null) {
                this.extractor_.release();
                this.extractor_ = null;
            }
        } catch (Exception e) {
            Logging.e(TAG, "Failed to releaseDecoder file!");
            e.printStackTrace();
        }
        Logging.d(TAG, "releaseDecoder!");
        this.outputStreamEnd_ = false;
        this.inputStreamEnd_ = false;
    }

    private void dequeueInputBuffer() {
        ByteBuffer inBuf;
        int sampleSize;
        int index = this.mediaCodec_.dequeueInputBuffer(0);
        if (index >= 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                inBuf = this.mediaCodec_.getInputBuffer(index);
            } else {
                ByteBuffer[] inputBuffers = this.mediaCodec_.getInputBuffers();
                this.inputBuffers_ = inputBuffers;
                inBuf = inputBuffers[index];
            }
            long sampleTime = this.extractor_.getSampleTime();
            int flags = this.extractor_.getSampleFlags();
            int sampleSize2 = this.extractor_.readSampleData(inBuf, 0);
            if (sampleSize2 <= 0) {
                this.inputStreamEnd_ = true;
                flags |= 4;
                sampleSize = 0;
            } else {
                sampleSize = sampleSize2;
            }
            this.mediaCodec_.queueInputBuffer(index, 0, sampleSize, sampleTime, flags);
            this.extractor_.advance();
        }
    }

    private void dequeueOutputBuffer() {
        int index = this.mediaCodec_.dequeueOutputBuffer(this.info, 0);
        this.decodeIsReady_ = false;
        if (index != -3 && index != -2) {
            if (index == -1) {
                int i = this.retryCount_ + 1;
                this.retryCount_ = i;
                if (i < MAX_DECODER_RETRY_COUNT) {
                    return;
                }
                if (this.isMeizu_ || this.isVivo_) {
                    String str = TAG;
                    Logging.e(str, "Failed to dequeueBuffer trycount=" + this.retryCount_ + " presentationTime=" + this.info.presentationTimeUs + " total=" + this.fileLength_);
                    this.retryCount_ = 0;
                    this.outputStreamEnd_ = true;
                }
            } else if (index >= 0) {
                this.retryCount_ = 0;
                if ((this.info.flags & 4) != 0) {
                    this.outputStreamEnd_ = true;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    ByteBuffer outbuf = this.mediaCodec_.getOutputBuffer(index);
                    copyToReadBuffer(outbuf, outbuf.limit());
                } else {
                    ByteBuffer[] outputBuffers = this.mediaCodec_.getOutputBuffers();
                    this.outputBuffers_ = outputBuffers;
                    copyToReadBuffer(outputBuffers[index], this.info.size);
                }
                this.mediaCodec_.releaseOutputBuffer(index, false);
                this.decodeIsReady_ = true;
            }
        }
    }

    public boolean decodeData() {
        try {
            if (!this.inputStreamEnd_) {
                dequeueInputBuffer();
            }
            if (!this.outputStreamEnd_) {
                dequeueOutputBuffer();
            }
            return this.outputStreamEnd_;
        } catch (Exception ex) {
            Logging.e(TAG, "Failed to decode data!");
            ex.printStackTrace();
            return false;
        }
    }

    private boolean checkAudioForamtChange() {
        if (Build.VERSION.SDK_INT < 16) {
            return false;
        }
        boolean change = false;
        try {
            MediaFormat newformat = this.mediaCodec_.getOutputFormat();
            int new_channels = newformat.getInteger("channel-count");
            int new_rate = newformat.getInteger("sample-rate");
            if (!(this.sampleRate_ == new_rate && this.channels_ == new_channels)) {
                change = true;
            }
            this.sampleRate_ = new_rate;
            this.channels_ = new_channels;
            return change;
        } catch (Exception e) {
            Logging.e(TAG, "Failed to get new audio format!");
            return false;
        }
    }

    private void copyToReadBuffer(ByteBuffer inputBuffer, int length) {
        int readBytes;
        int pos = 0;
        boolean bResetPos = false;
        try {
            checkAudioForamtChange();
            while (pos < length) {
                int readBytes2 = length - pos;
                if (readBytes2 > DECODE_READ_BUFFER_SIZE) {
                    bResetPos = true;
                    readBytes = DECODE_READ_BUFFER_SIZE;
                } else {
                    readBytes = readBytes2;
                }
                if (bResetPos) {
                    inputBuffer.position(pos);
                    inputBuffer.limit(pos + readBytes);
                }
                this.decodeReadBuffer_.position(0);
                this.decodeReadBuffer_.put(inputBuffer);
                nativeDataBufferIsReady(readBytes, this.sampleRate_, this.channels_, this.nativeAudioCodec_);
                pos += readBytes;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isAACSupported() {
        boolean supported = true;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                MediaCodecInfo[] codecInfos = new MediaCodecList(1).getCodecInfos();
                int length = codecInfos.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    MediaCodecInfo codecInfo = codecInfos[i];
                    if (!codecInfo.isEncoder() && codecInfo.getName().toLowerCase().contains("nvidia")) {
                        supported = false;
                        break;
                    }
                    i++;
                }
                return supported;
            }
            int numCodecs = MediaCodecList.getCodecCount();
            for (int i2 = 0; i2 < numCodecs; i2++) {
                MediaCodecInfo codecInfo2 = MediaCodecList.getCodecInfoAt(i2);
                if (!codecInfo2.isEncoder() && codecInfo2.getName().toLowerCase().contains("nvidia")) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            Logging.e(TAG, "Failed to get aac decoder");
            ex.printStackTrace();
            return false;
        }
    }

    public long getFilePosition() {
        return this.extractor_.getSampleTime();
    }

    public void setFilePosition(long pos) {
        this.extractor_.seekTo(pos, 2);
    }

    public boolean decodeIsReady() {
        return this.decodeIsReady_;
    }
}
