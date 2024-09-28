package im.bclpbkiauv.ui.utils.translate;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.view.Surface;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.messenger.ApplicationLoader;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.utils.translate.callback.DecodeOperateInterface;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import im.bclpbkiauv.ui.utils.translate.ssrc.SSRC;
import im.bclpbkiauv.ui.utils.translate.utils.AudioBitUtils;
import im.bclpbkiauv.ui.utils.translate.utils.AudioEncodeUtil;
import im.bclpbkiauv.ui.utils.translate.utils.AudioFileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import kotlin.UByte;

public class DecodeEngine {
    private static DecodeEngine instance;
    private String TAG = DecodeEngine.class.getSimpleName();

    private DecodeEngine() {
    }

    public static DecodeEngine getInstance() {
        if (instance == null) {
            synchronized (DecodeEngine.class) {
                if (instance == null) {
                    instance = new DecodeEngine();
                }
            }
        }
        return instance;
    }

    public boolean convertMusicFileToWaveFile(String musicFileUrl, String decodeFileUrl, DecodeOperateInterface decodeOperateInterface) {
        boolean success = decodeMusicFile(musicFileUrl, decodeFileUrl, 1, -1, -1, decodeOperateInterface);
        if (decodeOperateInterface != null) {
            if (success) {
                decodeOperateInterface.decodeSuccess();
            } else {
                decodeOperateInterface.decodeFail();
            }
        }
        return success;
    }

    public boolean convertMusicFileToWaveFile(String musicFileUrl, String decodeFileUrl, double startSecond, double endSecond, DecodeOperateInterface decodeOperateInterface) {
        boolean success = decodeMusicFile(musicFileUrl, decodeFileUrl, 1, ((long) startSecond) * 1000000, ((long) endSecond) * 1000000, decodeOperateInterface);
        if (decodeOperateInterface != null) {
            if (success) {
                decodeOperateInterface.decodeSuccess();
            } else {
                decodeOperateInterface.decodeFail();
            }
        }
        return success;
    }

    public boolean convertMusicFileToAccFile(String musicFileUrl, String decodeFileUrl, DecodeOperateInterface decodeOperateInterface) {
        boolean success = decodeMusicFile(musicFileUrl, decodeFileUrl, 2, -1, -1, decodeOperateInterface);
        if (decodeOperateInterface != null) {
            if (success) {
                decodeOperateInterface.decodeSuccess();
            } else {
                decodeOperateInterface.decodeFail();
            }
        }
        return success;
    }

    public boolean convertMusicFileToPcmFile(String musicFileUrl, String decodeFileUrl, DecodeOperateInterface decodeOperateInterface) {
        boolean success = decodeMusicFile(musicFileUrl, decodeFileUrl, 0, -1, -1, decodeOperateInterface);
        if (decodeOperateInterface != null) {
            if (success) {
                decodeOperateInterface.decodeSuccess();
            } else {
                decodeOperateInterface.decodeFail();
            }
        }
        return success;
    }

    public boolean convertMusicFileToPcmFile(String musicFileUrl, String decodeFileUrl, int startSecond, int endSecond, DecodeOperateInterface decodeOperateInterface) {
        boolean success = decodeMusicFile(musicFileUrl, decodeFileUrl, 0, ((long) startSecond) * 1000000, ((long) endSecond) * 1000000, decodeOperateInterface);
        if (decodeOperateInterface != null) {
            if (success) {
                decodeOperateInterface.decodeSuccess();
            } else {
                decodeOperateInterface.decodeFail();
            }
        }
        return success;
    }

    private boolean decodeMusicFile(String musicFileUrl, String decodeFileUrl, int convertType, long startMicroseconds, long endMicroseconds, DecodeOperateInterface decodeOperateInterface) {
        int bitNumber;
        String str = musicFileUrl;
        String str2 = decodeFileUrl;
        int i = convertType;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(str);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                mediaExtractor.setDataSource(new FileInputStream(str).getFD());
            } catch (Exception e2) {
                e2.printStackTrace();
                FileLog.e("设置解码音频文件路径错误");
            }
        }
        if (mediaExtractor.getTrackCount() <= 0) {
            FileLog.e("解码器出错");
            return false;
        }
        MediaFormat mediaFormat = mediaExtractor.getTrackFormat(0);
        int sampleRate = mediaFormat.containsKey("sample-rate") ? mediaFormat.getInteger("sample-rate") : 44100;
        int channelCount = mediaFormat.containsKey("channel-count") ? mediaFormat.getInteger("channel-count") : 1;
        long duration = mediaFormat.containsKey("durationUs") ? mediaFormat.getLong("durationUs") : 0;
        String mime = mediaFormat.containsKey("mime") ? mediaFormat.getString("mime") : "";
        int pcmEncoding = mediaFormat.containsKey("pcm-encoding") ? mediaFormat.getInteger("pcm-encoding") : 2;
        if (pcmEncoding == 3) {
            bitNumber = 8;
        } else if (pcmEncoding != 4) {
            bitNumber = 16;
        } else {
            bitNumber = 32;
        }
        FileLog.e("歌曲信息Track info: mime:" + mime + " 采样率sampleRate:" + sampleRate + " channels:" + channelCount + " duration:" + duration);
        if (TextUtils.isEmpty(mime)) {
            int i2 = pcmEncoding;
            MediaFormat mediaFormat2 = mediaFormat;
            MediaExtractor mediaExtractor2 = mediaExtractor;
            long j = startMicroseconds;
        } else if (!mime.startsWith("audio/")) {
            int i3 = pcmEncoding;
            MediaFormat mediaFormat3 = mediaFormat;
            MediaExtractor mediaExtractor3 = mediaExtractor;
            long j2 = startMicroseconds;
        } else {
            if (mime.equals("audio/ffmpeg")) {
                mime = MimeTypes.AUDIO_MPEG;
                mediaFormat.setString("mime", mime);
            }
            if (duration <= 0) {
                FileLog.e("音频文件duration为" + duration);
                return false;
            }
            long startMicroseconds2 = Math.max(startMicroseconds, 0);
            long endMicroseconds2 = Math.min(endMicroseconds < 0 ? duration : endMicroseconds, duration);
            if (startMicroseconds2 >= endMicroseconds2) {
                return false;
            }
            try {
                MediaCodec mediaCodec = MediaCodec.createDecoderByType(mime);
                try {
                    mediaCodec.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 0);
                    String decodeFileUrl2 = str2.substring(0, str2.lastIndexOf("."));
                    String pcmFilePath = decodeFileUrl2 + ".pcm";
                    MediaCodec mediaCodec2 = mediaCodec;
                    int i4 = pcmEncoding;
                    MediaFormat mediaFormat4 = mediaFormat;
                    MediaExtractor mediaExtractor4 = mediaExtractor;
                    getDecodeData(mediaExtractor, mediaCodec, pcmFilePath, sampleRate, channelCount, startMicroseconds2, endMicroseconds2, decodeOperateInterface);
                    if (i == 1) {
                        convertPcmFileToWaveFile(pcmFilePath, decodeFileUrl2 + ".wav", sampleRate, channelCount, bitNumber);
                        new File(pcmFilePath).delete();
                    } else if (i == 2) {
                        convertPcmFileToAccFile(pcmFilePath, decodeFileUrl2 + DefaultHlsExtractorFactory.AAC_FILE_EXTENSION, sampleRate, channelCount, bitNumber);
                        new File(pcmFilePath).delete();
                    }
                    return true;
                } catch (Exception e3) {
                    int i5 = pcmEncoding;
                    MediaFormat mediaFormat5 = mediaFormat;
                    MediaExtractor mediaExtractor5 = mediaExtractor;
                    MediaCodec mediaCodec3 = mediaCodec;
                    FileLog.e("解码器configure出错");
                    return false;
                }
            } catch (Exception e4) {
                int i6 = pcmEncoding;
                MediaFormat mediaFormat6 = mediaFormat;
                MediaExtractor mediaExtractor6 = mediaExtractor;
                FileLog.e("解码器configure出错");
                return false;
            }
        }
        FileLog.e("解码文件不是音频文件mime:" + mime);
        return false;
    }

    private void convertPcmFileToWaveFile(String pcmFilePath, String convertFilePath, int sampleRate, int channels, int bitNumber) {
        AudioEncodeUtil.convertPcm2Wav(pcmFilePath, convertFilePath, sampleRate, channels, bitNumber);
    }

    private void convertPcmFileToAccFile(String pcmFilePath, String convertFilePath, int sampleRate, int channels, int bitNumber) {
        if (AudioFileUtils.checkFileExist(convertFilePath)) {
            AudioFileUtils.deleteFile(new File(convertFilePath));
        }
        AudioFileUtils.confirmFolderExist(new File(convertFilePath).getParent());
        AudioEncodeUtil.convertPcm2Acc(pcmFilePath, convertFilePath, sampleRate, channels, bitNumber);
    }

    private void getDecodeData(MediaExtractor mediaExtractor, MediaCodec mediaCodec, String decodeFileUrl, int sampleRate, int channelCount, long startMicroseconds, long endMicroseconds, DecodeOperateInterface decodeOperateInterface) {
        BufferedOutputStream bufferedOutputStream;
        int sampleRate2;
        long decodeNoticeTime;
        MediaCodec.BufferInfo bufferInfo;
        BufferedOutputStream bufferedOutputStream2;
        long j;
        int sampleRate3;
        int channelCount2;
        long presentationTimeUs;
        int sampleDataSize;
        int sampleDataSize2;
        DecodeEngine decodeEngine = this;
        MediaExtractor mediaExtractor2 = mediaExtractor;
        MediaCodec mediaCodec2 = mediaCodec;
        DecodeOperateInterface decodeOperateInterface2 = decodeOperateInterface;
        long decodeNoticeTime2 = System.currentTimeMillis();
        MediaFormat outputFormat = mediaCodec.getOutputFormat();
        long decodeNoticeTime3 = decodeNoticeTime2;
        int integer = outputFormat.containsKey("bit-width") ? outputFormat.getInteger("bit-width") : 0;
        mediaCodec.start();
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        mediaExtractor2.selectTrack(0);
        MediaCodec.BufferInfo bufferInfo2 = new MediaCodec.BufferInfo();
        BufferedOutputStream bufferedOutputStream3 = AudioFileUtils.getBufferedOutputStreamFromFile(decodeFileUrl);
        int i = integer / 8;
        long presentationTimeUs2 = 0;
        MediaFormat mediaFormat = outputFormat;
        ByteBuffer[] outputBuffers2 = outputBuffers;
        long presentationTimeUs3 = decodeNoticeTime3;
        int sampleRate4 = sampleRate;
        int channelCount3 = channelCount;
        boolean decodeOutputEnd = false;
        int decodeInputEnd = 0;
        while (true) {
            if (decodeOutputEnd) {
                bufferedOutputStream = bufferedOutputStream3;
                long j2 = presentationTimeUs3;
                sampleRate2 = sampleRate4;
                MediaCodec.BufferInfo bufferInfo3 = bufferInfo2;
                break;
            }
            long decodeTime = System.currentTimeMillis();
            bufferedOutputStream = bufferedOutputStream3;
            long decodeNoticeTime4 = presentationTimeUs3;
            if (decodeTime - presentationTimeUs3 > 1000) {
                int decodeProgress = (int) (((presentationTimeUs2 - startMicroseconds) * 100) / endMicroseconds);
                if (decodeProgress > 0) {
                    decodeEngine.notifyProgress(decodeOperateInterface2, decodeProgress);
                }
                decodeNoticeTime = decodeTime;
            } else {
                decodeNoticeTime = decodeNoticeTime4;
            }
            try {
                int inputBufferIndex = mediaCodec2.dequeueInputBuffer(100);
                if (inputBufferIndex >= 0) {
                    try {
                        ByteBuffer sourceBuffer = inputBuffers[inputBufferIndex];
                        int sampleDataSize3 = mediaExtractor2.readSampleData(sourceBuffer, 0);
                        if (sampleDataSize3 < 0) {
                            presentationTimeUs = presentationTimeUs2;
                            sampleDataSize = 0;
                            sampleDataSize2 = 1;
                        } else {
                            presentationTimeUs = mediaExtractor.getSampleTime();
                            sampleDataSize = sampleDataSize3;
                            sampleDataSize2 = decodeInputEnd;
                        }
                        j = 100;
                        bufferedOutputStream2 = bufferedOutputStream;
                        ByteBuffer byteBuffer = sourceBuffer;
                        sampleRate2 = sampleRate4;
                    } catch (Exception e) {
                        e = e;
                        int i2 = sampleRate4;
                        bufferInfo = bufferInfo2;
                        FileLog.e("getDecodeData异常" + e);
                        decodeEngine = this;
                        bufferInfo2 = bufferInfo;
                        bufferedOutputStream3 = bufferedOutputStream;
                        presentationTimeUs3 = decodeNoticeTime;
                    }
                    try {
                        mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleDataSize, presentationTimeUs, sampleDataSize2 != 0 ? 4 : 0);
                        if (sampleDataSize2 == 0) {
                            mediaExtractor.advance();
                        }
                        decodeInputEnd = sampleDataSize2;
                        presentationTimeUs2 = presentationTimeUs;
                    } catch (Exception e2) {
                        e = e2;
                        decodeInputEnd = sampleDataSize2;
                        bufferInfo = bufferInfo2;
                        presentationTimeUs2 = presentationTimeUs;
                        bufferedOutputStream = bufferedOutputStream2;
                        sampleRate4 = sampleRate2;
                        FileLog.e("getDecodeData异常" + e);
                        decodeEngine = this;
                        bufferInfo2 = bufferInfo;
                        bufferedOutputStream3 = bufferedOutputStream;
                        presentationTimeUs3 = decodeNoticeTime;
                    }
                } else {
                    j = 100;
                    bufferedOutputStream2 = bufferedOutputStream;
                    sampleRate2 = sampleRate4;
                }
                bufferInfo = bufferInfo2;
                try {
                    int outputBufferIndex = mediaCodec2.dequeueOutputBuffer(bufferInfo, j);
                    if (outputBufferIndex < 0) {
                        if (outputBufferIndex == -3) {
                            ByteBuffer[] outputBuffers3 = mediaCodec.getOutputBuffers();
                            try {
                                FileLog.e("MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED [AudioDecoder]output buffers have changed.");
                                outputBuffers2 = outputBuffers3;
                                sampleRate4 = sampleRate2;
                            } catch (Exception e3) {
                                e = e3;
                                outputBuffers2 = outputBuffers3;
                                bufferedOutputStream = bufferedOutputStream2;
                                sampleRate4 = sampleRate2;
                                FileLog.e("getDecodeData异常" + e);
                                decodeEngine = this;
                                bufferInfo2 = bufferInfo;
                                bufferedOutputStream3 = bufferedOutputStream;
                                presentationTimeUs3 = decodeNoticeTime;
                            }
                        } else if (outputBufferIndex != -2) {
                            sampleRate4 = sampleRate2;
                        } else {
                            try {
                                MediaFormat outputFormat2 = mediaCodec.getOutputFormat();
                                try {
                                    sampleRate3 = outputFormat2.containsKey("sample-rate") ? outputFormat2.getInteger("sample-rate") : sampleRate2;
                                    try {
                                        channelCount2 = outputFormat2.containsKey("channel-count") ? outputFormat2.getInteger("channel-count") : channelCount3;
                                    } catch (Exception e4) {
                                        e = e4;
                                        MediaFormat mediaFormat2 = outputFormat2;
                                        sampleRate4 = sampleRate3;
                                        bufferedOutputStream = bufferedOutputStream2;
                                        FileLog.e("getDecodeData异常" + e);
                                        decodeEngine = this;
                                        bufferInfo2 = bufferInfo;
                                        bufferedOutputStream3 = bufferedOutputStream;
                                        presentationTimeUs3 = decodeNoticeTime;
                                    }
                                } catch (Exception e5) {
                                    e = e5;
                                    MediaFormat mediaFormat3 = outputFormat2;
                                    bufferedOutputStream = bufferedOutputStream2;
                                    sampleRate4 = sampleRate2;
                                    FileLog.e("getDecodeData异常" + e);
                                    decodeEngine = this;
                                    bufferInfo2 = bufferInfo;
                                    bufferedOutputStream3 = bufferedOutputStream;
                                    presentationTimeUs3 = decodeNoticeTime;
                                }
                                try {
                                    int byteNumber = (outputFormat2.containsKey("bitrate") ? outputFormat2.getInteger("bitrate") : 0) / 8;
                                    try {
                                        FileLog.e("MediaCodec.INFO_OUTPUT_FORMAT_CHANGED [AudioDecoder]output format has changed to " + mediaCodec.getOutputFormat());
                                        MediaFormat mediaFormat4 = outputFormat2;
                                        sampleRate4 = sampleRate3;
                                        channelCount3 = channelCount2;
                                        int i3 = byteNumber;
                                    } catch (Exception e6) {
                                        e = e6;
                                        MediaFormat mediaFormat5 = outputFormat2;
                                        sampleRate4 = sampleRate3;
                                        channelCount3 = channelCount2;
                                        int i4 = byteNumber;
                                        bufferedOutputStream = bufferedOutputStream2;
                                        FileLog.e("getDecodeData异常" + e);
                                        decodeEngine = this;
                                        bufferInfo2 = bufferInfo;
                                        bufferedOutputStream3 = bufferedOutputStream;
                                        presentationTimeUs3 = decodeNoticeTime;
                                    }
                                } catch (Exception e7) {
                                    e = e7;
                                    MediaFormat mediaFormat6 = outputFormat2;
                                    sampleRate4 = sampleRate3;
                                    channelCount3 = channelCount2;
                                    bufferedOutputStream = bufferedOutputStream2;
                                    FileLog.e("getDecodeData异常" + e);
                                    decodeEngine = this;
                                    bufferInfo2 = bufferInfo;
                                    bufferedOutputStream3 = bufferedOutputStream;
                                    presentationTimeUs3 = decodeNoticeTime;
                                }
                            } catch (Exception e8) {
                                e = e8;
                                bufferedOutputStream = bufferedOutputStream2;
                                sampleRate4 = sampleRate2;
                                FileLog.e("getDecodeData异常" + e);
                                decodeEngine = this;
                                bufferInfo2 = bufferInfo;
                                bufferedOutputStream3 = bufferedOutputStream;
                                presentationTimeUs3 = decodeNoticeTime;
                            }
                        }
                        decodeEngine = this;
                        bufferInfo2 = bufferInfo;
                        presentationTimeUs3 = decodeNoticeTime;
                        bufferedOutputStream3 = bufferedOutputStream2;
                    } else {
                        ByteBuffer targetBuffer = outputBuffers2[outputBufferIndex];
                        byte[] sourceByteArray = new byte[bufferInfo.size];
                        targetBuffer.get(sourceByteArray);
                        targetBuffer.clear();
                        mediaCodec2.releaseOutputBuffer(outputBufferIndex, false);
                        if ((bufferInfo.flags & 4) != 0) {
                            decodeOutputEnd = true;
                        }
                        if (sourceByteArray.length > 0) {
                            bufferedOutputStream = bufferedOutputStream2;
                            if (bufferedOutputStream != null) {
                                if (presentationTimeUs2 >= startMicroseconds) {
                                    try {
                                        bufferedOutputStream.write(sourceByteArray);
                                    } catch (Exception e9) {
                                        try {
                                            FileLog.e("输出解压音频数据异常" + e9);
                                        } catch (Exception e10) {
                                            e = e10;
                                            sampleRate4 = sampleRate2;
                                        }
                                    }
                                }
                                decodeEngine = this;
                                bufferInfo2 = bufferInfo;
                                bufferedOutputStream3 = bufferedOutputStream;
                                presentationTimeUs3 = decodeNoticeTime;
                                sampleRate4 = sampleRate2;
                            }
                        } else {
                            bufferedOutputStream = bufferedOutputStream2;
                        }
                        if (presentationTimeUs2 > endMicroseconds) {
                            long j3 = decodeNoticeTime;
                            break;
                        }
                        decodeEngine = this;
                        bufferInfo2 = bufferInfo;
                        bufferedOutputStream3 = bufferedOutputStream;
                        presentationTimeUs3 = decodeNoticeTime;
                        sampleRate4 = sampleRate2;
                    }
                } catch (Exception e11) {
                    e = e11;
                    bufferedOutputStream = bufferedOutputStream2;
                    sampleRate4 = sampleRate2;
                    FileLog.e("getDecodeData异常" + e);
                    decodeEngine = this;
                    bufferInfo2 = bufferInfo;
                    bufferedOutputStream3 = bufferedOutputStream;
                    presentationTimeUs3 = decodeNoticeTime;
                }
            } catch (Exception e12) {
                e = e12;
                int i5 = sampleRate4;
                bufferInfo = bufferInfo2;
                FileLog.e("getDecodeData异常" + e);
                decodeEngine = this;
                bufferInfo2 = bufferInfo;
                bufferedOutputStream3 = bufferedOutputStream;
                presentationTimeUs3 = decodeNoticeTime;
            }
        }
        if (bufferedOutputStream != null) {
            try {
                bufferedOutputStream.close();
            } catch (IOException e13) {
                FileLog.e("关闭bufferedOutputStream异常" + e13);
            }
        }
        int sampleRate5 = sampleRate2;
        if (sampleRate5 != 16000) {
            Resample(sampleRate5, decodeFileUrl);
        } else {
            String str = decodeFileUrl;
        }
        notifyProgress(decodeOperateInterface2, 100);
        if (mediaCodec2 != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }
        if (mediaExtractor2 != null) {
            mediaExtractor.release();
        }
    }

    private static void Resample(int sampleRate, String decodeFileUrl) {
        String str = decodeFileUrl;
        String newDecodeFileUrl = str.substring(0, str.lastIndexOf(".")) + "_new.pcm";
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(str));
            FileOutputStream fileOutputStream = new FileOutputStream(new File(newDecodeFileUrl));
            new SSRC(fileInputStream, fileOutputStream, sampleRate, AudioEditConstant.ExportSampleRate, 2, 2, 1, Integer.MAX_VALUE, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, 0, true);
            fileInputStream.close();
            fileOutputStream.close();
            AudioFileUtils.renameFile(newDecodeFileUrl, str);
        } catch (IOException e) {
            FileLog.e("关闭bufferedOutputStream异常" + e);
        }
    }

    public static byte[] convertByteNumber(int sourceByteNumber, int outputByteNumber, byte[] sourceByteArray) {
        if (sourceByteNumber == outputByteNumber) {
            return sourceByteArray;
        }
        int sourceByteArrayLength = sourceByteArray.length;
        if (sourceByteNumber == 0 || sourceByteNumber == 1) {
            if (outputByteNumber == 2) {
                byte[] byteArray = new byte[(sourceByteArrayLength * 2)];
                for (int index = 0; index < sourceByteArrayLength; index++) {
                    byte[] resultByte = AudioBitUtils.GetBytes((short) (sourceByteArray[index] * UByte.MIN_VALUE), AudioEditConstant.isBigEnding);
                    byteArray[index * 2] = resultByte[0];
                    byteArray[(index * 2) + 1] = resultByte[1];
                }
                return byteArray;
            }
        } else if (sourceByteNumber == 2 && outputByteNumber == 1) {
            int outputByteArrayLength = sourceByteArrayLength / 2;
            byte[] byteArray2 = new byte[outputByteArrayLength];
            for (int index2 = 0; index2 < outputByteArrayLength; index2++) {
                byteArray2[index2] = (byte) (AudioBitUtils.GetShort(sourceByteArray[index2 * 2], sourceByteArray[(index2 * 2) + 1], AudioEditConstant.isBigEnding) / 256);
            }
            return byteArray2;
        }
        return sourceByteArray;
    }

    public static byte[] convertChannelNumber(int sourceChannelCount, int outputChannelCount, int byteNumber, byte[] sourceByteArray) {
        if (sourceChannelCount == outputChannelCount) {
            return sourceByteArray;
        }
        if (byteNumber != 1 && byteNumber != 2) {
            return sourceByteArray;
        }
        int sourceByteArrayLength = sourceByteArray.length;
        if (sourceChannelCount != 1) {
            if (sourceChannelCount == 2 && outputChannelCount == 1) {
                int outputByteArrayLength = sourceByteArrayLength / 2;
                byte[] byteArray = new byte[outputByteArrayLength];
                if (byteNumber == 1) {
                    for (int index = 0; index < outputByteArrayLength; index += 2) {
                        byteArray[index] = (byte) (((short) (((short) sourceByteArray[index * 2]) + ((short) sourceByteArray[(index * 2) + 1]))) >> 1);
                    }
                } else if (byteNumber == 2) {
                    for (int index2 = 0; index2 < outputByteArrayLength; index2 += 2) {
                        byte[] resultByte = AudioBitUtils.AverageShortByteArray(sourceByteArray[index2 * 2], sourceByteArray[(index2 * 2) + 1], sourceByteArray[(index2 * 2) + 2], sourceByteArray[(index2 * 2) + 3], AudioEditConstant.isBigEnding);
                        byteArray[index2] = resultByte[0];
                        byteArray[index2 + 1] = resultByte[1];
                    }
                }
                return byteArray;
            }
        } else if (outputChannelCount == 2) {
            byte[] byteArray2 = new byte[(sourceByteArrayLength * 2)];
            if (byteNumber == 1) {
                for (int index3 = 0; index3 < sourceByteArrayLength; index3++) {
                    byte firstByte = sourceByteArray[index3];
                    byteArray2[index3 * 2] = firstByte;
                    byteArray2[(index3 * 2) + 1] = firstByte;
                }
            } else if (byteNumber == 2) {
                for (int index4 = 0; index4 < sourceByteArrayLength; index4 += 2) {
                    byte firstByte2 = sourceByteArray[index4];
                    byte secondByte = sourceByteArray[index4 + 1];
                    byteArray2[index4 * 2] = firstByte2;
                    byteArray2[(index4 * 2) + 1] = secondByte;
                    byteArray2[(index4 * 2) + 2] = firstByte2;
                    byteArray2[(index4 * 2) + 3] = secondByte;
                }
            }
            return byteArray2;
        }
        return sourceByteArray;
    }

    private void notifyProgress(final DecodeOperateInterface decodeOperateInterface, final int progress) {
        ApplicationLoader.applicationHandler.post(new Runnable() {
            public void run() {
                DecodeOperateInterface decodeOperateInterface = decodeOperateInterface;
                if (decodeOperateInterface != null) {
                    decodeOperateInterface.updateDecodeProgress(progress);
                }
            }
        });
    }
}
