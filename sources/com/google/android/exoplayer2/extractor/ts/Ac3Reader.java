package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.audio.Ac3Util;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public final class Ac3Reader implements ElementaryStreamReader {
    private static final int HEADER_SIZE = 128;
    private static final int STATE_FINDING_SYNC = 0;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private int bytesRead;
    private Format format;
    private final ParsableBitArray headerScratchBits;
    private final ParsableByteArray headerScratchBytes;
    private final String language;
    private boolean lastByteWas0B;
    private TrackOutput output;
    private long sampleDurationUs;
    private int sampleSize;
    private int state;
    private long timeUs;
    private String trackFormatId;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    public Ac3Reader() {
        this((String) null);
    }

    public Ac3Reader(String language2) {
        ParsableBitArray parsableBitArray = new ParsableBitArray(new byte[128]);
        this.headerScratchBits = parsableBitArray;
        this.headerScratchBytes = new ParsableByteArray(parsableBitArray.data);
        this.state = 0;
        this.language = language2;
    }

    public void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.lastByteWas0B = false;
    }

    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator generator) {
        generator.generateNewId();
        this.trackFormatId = generator.getFormatId();
        this.output = extractorOutput.track(generator.getTrackId(), 1);
    }

    public void packetStarted(long pesTimeUs, int flags) {
        this.timeUs = pesTimeUs;
    }

    public void consume(ParsableByteArray data) {
        while (data.bytesLeft() > 0) {
            int i = this.state;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        int bytesToRead = Math.min(data.bytesLeft(), this.sampleSize - this.bytesRead);
                        this.output.sampleData(data, bytesToRead);
                        int i2 = this.bytesRead + bytesToRead;
                        this.bytesRead = i2;
                        int i3 = this.sampleSize;
                        if (i2 == i3) {
                            this.output.sampleMetadata(this.timeUs, 1, i3, 0, (TrackOutput.CryptoData) null);
                            this.timeUs += this.sampleDurationUs;
                            this.state = 0;
                        }
                    }
                } else if (continueRead(data, this.headerScratchBytes.data, 128)) {
                    parseHeader();
                    this.headerScratchBytes.setPosition(0);
                    this.output.sampleData(this.headerScratchBytes, 128);
                    this.state = 2;
                }
            } else if (skipToNextSync(data)) {
                this.state = 1;
                this.headerScratchBytes.data[0] = 11;
                this.headerScratchBytes.data[1] = 119;
                this.bytesRead = 2;
            }
        }
    }

    public void packetFinished() {
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        source.readBytes(target, this.bytesRead, bytesToRead);
        int i = this.bytesRead + bytesToRead;
        this.bytesRead = i;
        return i == targetLength;
    }

    private boolean skipToNextSync(ParsableByteArray pesBuffer) {
        while (true) {
            boolean z = false;
            if (pesBuffer.bytesLeft() <= 0) {
                return false;
            }
            if (!this.lastByteWas0B) {
                if (pesBuffer.readUnsignedByte() == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            } else {
                int secondByte = pesBuffer.readUnsignedByte();
                if (secondByte == 119) {
                    this.lastByteWas0B = false;
                    return true;
                }
                if (secondByte == 11) {
                    z = true;
                }
                this.lastByteWas0B = z;
            }
        }
    }

    private void parseHeader() {
        this.headerScratchBits.setPosition(0);
        Ac3Util.SyncFrameInfo frameInfo = Ac3Util.parseAc3SyncframeInfo(this.headerScratchBits);
        if (!(this.format != null && frameInfo.channelCount == this.format.channelCount && frameInfo.sampleRate == this.format.sampleRate && frameInfo.mimeType == this.format.sampleMimeType)) {
            Format createAudioSampleFormat = Format.createAudioSampleFormat(this.trackFormatId, frameInfo.mimeType, (String) null, -1, -1, frameInfo.channelCount, frameInfo.sampleRate, (List<byte[]>) null, (DrmInitData) null, 0, this.language);
            this.format = createAudioSampleFormat;
            this.output.format(createAudioSampleFormat);
        }
        this.sampleSize = frameInfo.frameSize;
        this.sampleDurationUs = (((long) frameInfo.sampleCount) * 1000000) / ((long) this.format.sampleRate);
    }
}
