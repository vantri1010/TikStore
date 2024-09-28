package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class PesReader implements TsPayloadReader {
    private static final int HEADER_SIZE = 9;
    private static final int MAX_HEADER_EXTENSION_SIZE = 10;
    private static final int PES_SCRATCH_SIZE = 10;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_BODY = 3;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_HEADER_EXTENSION = 2;
    private static final String TAG = "PesReader";
    private int bytesRead;
    private boolean dataAlignmentIndicator;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private int payloadSize;
    private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[10]);
    private boolean ptsFlag;
    private final ElementaryStreamReader reader;
    private boolean seenFirstDts;
    private int state = 0;
    private long timeUs;
    private TimestampAdjuster timestampAdjuster;

    public PesReader(ElementaryStreamReader reader2) {
        this.reader = reader2;
    }

    public void init(TimestampAdjuster timestampAdjuster2, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator idGenerator) {
        this.timestampAdjuster = timestampAdjuster2;
        this.reader.createTracks(extractorOutput, idGenerator);
    }

    public final void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.seenFirstDts = false;
        this.reader.seek();
    }

    public final void consume(ParsableByteArray data, int flags) throws ParserException {
        if ((flags & 1) != 0) {
            int i = this.state;
            if (!(i == 0 || i == 1)) {
                if (i == 2) {
                    Log.w(TAG, "Unexpected start indicator reading extended header");
                } else if (i == 3) {
                    if (this.payloadSize != -1) {
                        Log.w(TAG, "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
                    }
                    this.reader.packetFinished();
                } else {
                    throw new IllegalStateException();
                }
            }
            setState(1);
        }
        while (data.bytesLeft() > 0) {
            int i2 = this.state;
            if (i2 != 0) {
                int padding = 0;
                if (i2 != 1) {
                    if (i2 == 2) {
                        if (continueRead(data, this.pesScratch.data, Math.min(10, this.extendedHeaderLength)) && continueRead(data, (byte[]) null, this.extendedHeaderLength)) {
                            parseHeaderExtension();
                            if (this.dataAlignmentIndicator) {
                                padding = 4;
                            }
                            flags |= padding;
                            this.reader.packetStarted(this.timeUs, flags);
                            setState(3);
                        }
                    } else if (i2 == 3) {
                        int readLength = data.bytesLeft();
                        int i3 = this.payloadSize;
                        if (i3 != -1) {
                            padding = readLength - i3;
                        }
                        if (padding > 0) {
                            readLength -= padding;
                            data.setLimit(data.getPosition() + readLength);
                        }
                        this.reader.consume(data);
                        int i4 = this.payloadSize;
                        if (i4 != -1) {
                            int i5 = i4 - readLength;
                            this.payloadSize = i5;
                            if (i5 == 0) {
                                this.reader.packetFinished();
                                setState(1);
                            }
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                } else if (continueRead(data, this.pesScratch.data, 9)) {
                    if (parseHeader()) {
                        padding = 2;
                    }
                    setState(padding);
                }
            } else {
                data.skipBytes(data.bytesLeft());
            }
        }
    }

    private void setState(int state2) {
        this.state = state2;
        this.bytesRead = 0;
    }

    private boolean continueRead(ParsableByteArray source, byte[] target, int targetLength) {
        int bytesToRead = Math.min(source.bytesLeft(), targetLength - this.bytesRead);
        if (bytesToRead <= 0) {
            return true;
        }
        if (target == null) {
            source.skipBytes(bytesToRead);
        } else {
            source.readBytes(target, this.bytesRead, bytesToRead);
        }
        int i = this.bytesRead + bytesToRead;
        this.bytesRead = i;
        if (i == targetLength) {
            return true;
        }
        return false;
    }

    private boolean parseHeader() {
        this.pesScratch.setPosition(0);
        int startCodePrefix = this.pesScratch.readBits(24);
        if (startCodePrefix != 1) {
            Log.w(TAG, "Unexpected start code prefix: " + startCodePrefix);
            this.payloadSize = -1;
            return false;
        }
        this.pesScratch.skipBits(8);
        int packetLength = this.pesScratch.readBits(16);
        this.pesScratch.skipBits(5);
        this.dataAlignmentIndicator = this.pesScratch.readBit();
        this.pesScratch.skipBits(2);
        this.ptsFlag = this.pesScratch.readBit();
        this.dtsFlag = this.pesScratch.readBit();
        this.pesScratch.skipBits(6);
        int readBits = this.pesScratch.readBits(8);
        this.extendedHeaderLength = readBits;
        if (packetLength == 0) {
            this.payloadSize = -1;
        } else {
            this.payloadSize = ((packetLength + 6) - 9) - readBits;
        }
        return true;
    }

    private void parseHeaderExtension() {
        this.pesScratch.setPosition(0);
        this.timeUs = C.TIME_UNSET;
        if (this.ptsFlag) {
            this.pesScratch.skipBits(4);
            this.pesScratch.skipBits(1);
            this.pesScratch.skipBits(1);
            long pts = (((long) this.pesScratch.readBits(3)) << 30) | ((long) (this.pesScratch.readBits(15) << 15)) | ((long) this.pesScratch.readBits(15));
            this.pesScratch.skipBits(1);
            if (!this.seenFirstDts && this.dtsFlag) {
                this.pesScratch.skipBits(4);
                this.pesScratch.skipBits(1);
                this.pesScratch.skipBits(1);
                this.pesScratch.skipBits(1);
                this.timestampAdjuster.adjustTsTimestamp((((long) this.pesScratch.readBits(3)) << 30) | ((long) (this.pesScratch.readBits(15) << 15)) | ((long) this.pesScratch.readBits(15)));
                this.seenFirstDts = true;
            }
            this.timeUs = this.timestampAdjuster.adjustTsTimestamp(pts);
        }
    }
}
