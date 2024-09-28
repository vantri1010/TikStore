package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.serenegiant.usb.UVCCamera;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import io.reactivex.annotations.SchedulerSupport;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

public class DTSTrackImpl extends AbstractTrack {
    private static final int BUFFER = 67108864;
    int bcCoreBitRate = 0;
    int bcCoreChannelMask = 0;
    int bcCoreMaxSampleRate = 0;
    int bitrate;
    int channelCount;
    int channelMask = 0;
    int codecDelayAtMaxFs = 0;
    int coreBitRate = 0;
    int coreChannelMask = 0;
    int coreFramePayloadInBytes = 0;
    int coreMaxSampleRate = 0;
    boolean coreSubStreamPresent = false;
    private int dataOffset = 0;
    private DataSource dataSource;
    DTSSpecificBox ddts = new DTSSpecificBox();
    int extAvgBitrate = 0;
    int extFramePayloadInBytes = 0;
    int extPeakBitrate = 0;
    int extSmoothBuffSize = 0;
    boolean extensionSubStreamPresent = false;
    int frameSize = 0;
    boolean isVBR = false;
    private String lang = "eng";
    int lbrCodingPresent = 0;
    int lsbTrimPercent = 0;
    int maxSampleRate = 0;
    int numExtSubStreams = 0;
    int numFramesTotal = 0;
    int numSamplesOrigAudioAtMaxFs = 0;
    SampleDescriptionBox sampleDescriptionBox;
    private long[] sampleDurations;
    int sampleSize;
    int samplerate;
    private List<Sample> samples;
    int samplesPerFrame;
    int samplesPerFrameAtMaxFs = 0;
    TrackMetaData trackMetaData = new TrackMetaData();
    String type = SchedulerSupport.NONE;

    public DTSTrackImpl(DataSource dataSource2, String lang2) throws IOException {
        super(dataSource2.toString());
        this.lang = lang2;
        this.dataSource = dataSource2;
        parse();
    }

    public DTSTrackImpl(DataSource dataSource2) throws IOException {
        super(dataSource2.toString());
        this.dataSource = dataSource2;
        parse();
    }

    public void close() throws IOException {
        this.dataSource.close();
    }

    private void parse() throws IOException {
        if (readVariables()) {
            this.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry(this.type);
            audioSampleEntry.setChannelCount(this.channelCount);
            audioSampleEntry.setSampleRate((long) this.samplerate);
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            audioSampleEntry.addBox(this.ddts);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setLanguage(this.lang);
            this.trackMetaData.setTimescale((long) this.samplerate);
            return;
        }
        throw new IOException();
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return null;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return "soun";
    }

    private void parseDtshdhdr(int size, ByteBuffer bb) {
        bb.getInt();
        bb.get();
        bb.getInt();
        bb.get();
        int bitwStreamMetadata = bb.getShort();
        bb.get();
        this.numExtSubStreams = bb.get();
        if ((bitwStreamMetadata & 1) == 1) {
            this.isVBR = true;
        }
        if ((bitwStreamMetadata & 8) == 8) {
            this.coreSubStreamPresent = true;
        }
        if ((bitwStreamMetadata & 16) == 16) {
            this.extensionSubStreamPresent = true;
            this.numExtSubStreams++;
        } else {
            this.numExtSubStreams = 0;
        }
        for (int i = 14; i < size; i++) {
            bb.get();
        }
    }

    private boolean parseCoressmd(int size, ByteBuffer bb) {
        this.coreMaxSampleRate = (bb.get() << 16) | (65535 & bb.getShort());
        this.coreBitRate = bb.getShort();
        this.coreChannelMask = bb.getShort();
        this.coreFramePayloadInBytes = bb.getInt();
        for (int i = 11; i < size; i++) {
            bb.get();
        }
        return true;
    }

    private boolean parseAuprhdr(int size, ByteBuffer bb) {
        bb.get();
        int bitwAupresData = bb.getShort();
        this.maxSampleRate = (bb.get() << 16) | (bb.getShort() & 65535);
        this.numFramesTotal = bb.getInt();
        this.samplesPerFrameAtMaxFs = bb.getShort();
        this.numSamplesOrigAudioAtMaxFs = (bb.get() << 32) | (bb.getInt() & 65535);
        this.channelMask = bb.getShort();
        this.codecDelayAtMaxFs = bb.getShort();
        int c = 21;
        if ((bitwAupresData & 3) == 3) {
            int a = bb.get();
            this.bcCoreMaxSampleRate = (65535 & bb.getShort()) | (a << 16);
            this.bcCoreBitRate = bb.getShort();
            this.bcCoreChannelMask = bb.getShort();
            c = 21 + 7;
        }
        if ((bitwAupresData & 4) > 0) {
            this.lsbTrimPercent = bb.get();
            c++;
        }
        if ((bitwAupresData & 8) > 0) {
            this.lbrCodingPresent = 1;
        }
        while (c < size) {
            bb.get();
            c++;
        }
        return true;
    }

    private boolean parseExtssmd(int size, ByteBuffer bb) {
        int i;
        this.extAvgBitrate = (bb.get() << 16) | (bb.getShort() & 65535);
        if (this.isVBR) {
            int a = bb.get();
            this.extPeakBitrate = (65535 & bb.getShort()) | (a << 16);
            this.extSmoothBuffSize = bb.getShort();
            i = 3 + 5;
        } else {
            this.extFramePayloadInBytes = bb.getInt();
            i = 3 + 4;
        }
        while (i < size) {
            bb.get();
            i++;
        }
        return true;
    }

    private boolean readVariables() throws IOException {
        int testHeader2;
        int extCore;
        ByteBuffer bb;
        int fd;
        int coreLayout;
        int extLbr;
        int extPresent;
        int streamContruction;
        int i;
        int streamContruction2;
        int extXch;
        int extXbr;
        int testHeader22;
        ByteBuffer bb2 = this.dataSource.map(0, 25000);
        int testHeader1 = bb2.getInt();
        int testHeader23 = bb2.getInt();
        if (testHeader1 == 1146377032 && testHeader23 == 1145586770) {
            int extCore2 = testHeader1;
            int testHeader24 = testHeader23;
            while (true) {
                if (!(extCore == 1398035021 && testHeader2 == 1145132097) && bb.remaining() > 100) {
                    ByteBuffer bb3 = bb;
                    int testHeader25 = testHeader2;
                    int size = (int) bb3.getLong();
                    int testHeader12 = extCore;
                    if (testHeader12 == 1146377032) {
                        testHeader22 = testHeader25;
                        if (testHeader22 == 1145586770) {
                            parseDtshdhdr(size, bb3);
                            int testHeader13 = bb3.getInt();
                            testHeader24 = bb3.getInt();
                            extCore2 = testHeader13;
                            bb2 = bb3;
                        }
                    } else {
                        testHeader22 = testHeader25;
                    }
                    if (testHeader12 == 1129271877 && testHeader22 == 1397968196) {
                        if (!parseCoressmd(size, bb3)) {
                            return false;
                        }
                        int testHeader132 = bb3.getInt();
                        testHeader24 = bb3.getInt();
                        extCore2 = testHeader132;
                        bb2 = bb3;
                    } else if (testHeader12 == 1096110162 && testHeader22 == 759710802) {
                        if (!parseAuprhdr(size, bb3)) {
                            return false;
                        }
                        int testHeader1322 = bb3.getInt();
                        testHeader24 = bb3.getInt();
                        extCore2 = testHeader1322;
                        bb2 = bb3;
                    } else if (testHeader12 != 1163416659 || testHeader22 != 1398754628) {
                        for (int i2 = 0; i2 < size; i2++) {
                            bb3.get();
                        }
                        int testHeader13222 = bb3.getInt();
                        testHeader24 = bb3.getInt();
                        extCore2 = testHeader13222;
                        bb2 = bb3;
                    } else if (!parseExtssmd(size, bb3)) {
                        return false;
                    } else {
                        int testHeader132222 = bb3.getInt();
                        testHeader24 = bb3.getInt();
                        extCore2 = testHeader132222;
                        bb2 = bb3;
                    }
                }
            }
            long dataSize = bb.getLong();
            this.dataOffset = bb.position();
            int extPresent2 = 65535;
            boolean done = false;
            int extLbr2 = false;
            int testHeader14 = 0;
            ByteBuffer bb4 = null;
            int extXll = -1;
            int offset = false;
            int extXbr2 = -1;
            int corePresent = false;
            int extAudioId = 0;
            int extAudioId2 = false;
            int extAudio = 0;
            int extAudio2 = false;
            while (!done) {
                int extXbr3 = offset;
                int extX96k = extAudioId2;
                int extXch2 = extAudio2;
                int extXXch = corePresent;
                int extPresent3 = extPresent2;
                int testHeader26 = testHeader2;
                int amode = extXll;
                int extXll2 = testHeader14;
                int testHeader15 = extCore;
                ByteBuffer extCore3 = bb4;
                ByteBuffer bb5 = bb;
                int extLbr3 = extLbr2;
                int corePresent2 = extXbr2;
                int offset2 = bb5.position();
                int sync = bb5.getInt();
                if (sync == 2147385345) {
                    int corePresent3 = corePresent2;
                    if (corePresent3 == 1) {
                        done = true;
                        extXbr2 = corePresent3;
                        extLbr2 = extLbr3;
                        bb = bb5;
                        extPresent2 = extPresent3;
                        testHeader2 = testHeader26;
                        extAudioId2 = extX96k;
                        extAudio2 = extXch2;
                        corePresent = extXXch;
                        offset = extXbr3;
                        bb4 = extCore3;
                        extCore = testHeader15;
                        testHeader14 = extXll2;
                        extXll = amode;
                    } else {
                        extXbr2 = 1;
                        ByteBuffer bb6 = bb5;
                        BitReaderBuffer brb = new BitReaderBuffer(bb6);
                        int ftype = brb.readBits(1);
                        long dataSize2 = dataSize;
                        int shrt = brb.readBits(5);
                        int cpf = brb.readBits(1);
                        if (ftype != 1 || shrt != 31) {
                            BitReaderBuffer bitReaderBuffer = brb;
                            int i3 = ftype;
                            int i4 = shrt;
                            return false;
                        } else if (cpf != 0) {
                            BitReaderBuffer bitReaderBuffer2 = brb;
                            int i5 = ftype;
                            int i6 = shrt;
                            return false;
                        } else {
                            this.samplesPerFrame = (brb.readBits(7) + 1) * 32;
                            int fsize = brb.readBits(14);
                            this.frameSize += fsize + 1;
                            int amode2 = brb.readBits(6);
                            int i7 = ftype;
                            int sfreq = brb.readBits(4);
                            this.samplerate = getSampleRate(sfreq);
                            int i8 = sfreq;
                            int sfreq2 = brb.readBits(5);
                            this.bitrate = getBitRate(sfreq2);
                            if (brb.readBits(1) != 0) {
                                return false;
                            }
                            brb.readBits(1);
                            brb.readBits(1);
                            brb.readBits(1);
                            brb.readBits(1);
                            extAudioId = brb.readBits(3);
                            extAudio = brb.readBits(1);
                            brb.readBits(1);
                            brb.readBits(2);
                            brb.readBits(1);
                            if (cpf == 1) {
                                brb.readBits(16);
                            }
                            brb.readBits(1);
                            int i9 = sfreq2;
                            int vernum = brb.readBits(4);
                            brb.readBits(2);
                            int i10 = shrt;
                            int shrt2 = brb.readBits(3);
                            if (shrt2 == 0 || shrt2 == 1) {
                                this.sampleSize = 16;
                            } else if (shrt2 == 2 || shrt2 == 3) {
                                this.sampleSize = 20;
                            } else if (shrt2 != 5 && shrt2 != 6) {
                                return false;
                            } else {
                                this.sampleSize = 24;
                            }
                            brb.readBits(1);
                            brb.readBits(1);
                            if (vernum == 6) {
                                int readBits = brb.readBits(4);
                            } else if (vernum != 7) {
                                brb.readBits(4);
                            } else {
                                int readBits2 = brb.readBits(4);
                            }
                            BitReaderBuffer bitReaderBuffer3 = brb;
                            bb6.position(offset2 + fsize + 1);
                            extLbr2 = extLbr3;
                            bb4 = extCore3;
                            extCore = testHeader15;
                            dataSize = dataSize2;
                            testHeader2 = testHeader26;
                            extAudioId2 = extX96k;
                            extAudio2 = extXch2;
                            corePresent = extXXch;
                            offset = extXbr3;
                            bb = bb6;
                            testHeader14 = extXll2;
                            extXll = amode2;
                            extPresent2 = extPresent3;
                        }
                    }
                } else {
                    long dataSize3 = dataSize;
                    int corePresent4 = corePresent2;
                    ByteBuffer bb7 = bb5;
                    if (sync == 1683496997) {
                        if (corePresent4 == -1) {
                            this.samplesPerFrame = this.samplesPerFrameAtMaxFs;
                            extXbr2 = 0;
                        } else {
                            extXbr2 = corePresent4;
                        }
                        BitReaderBuffer brb2 = new BitReaderBuffer(bb7);
                        brb2.readBits(8);
                        brb2.readBits(2);
                        int nuBits4Header = 12;
                        int nuBits4ExSSFsize = 20;
                        if (brb2.readBits(1) == 0) {
                            nuBits4Header = 8;
                            nuBits4ExSSFsize = 16;
                        }
                        int i11 = sync;
                        int nuExtSSFsize = brb2.readBits(nuBits4ExSSFsize) + 1;
                        bb7.position(offset2 + brb2.readBits(nuBits4Header) + 1);
                        int extSync = bb7.getInt();
                        if (extSync == 1515870810) {
                            BitReaderBuffer bitReaderBuffer4 = brb2;
                            if (extXch2 == 1) {
                                done = true;
                            }
                            extXch = 1;
                            extLbr2 = extLbr3;
                            extAudioId2 = extX96k;
                            extXbr = extXbr3;
                        } else {
                            int extXch3 = extXch2;
                            if (extSync == 1191201283) {
                                extXch = extXch3;
                                if (extXXch == 1) {
                                    done = true;
                                }
                                extXXch = 1;
                                extLbr2 = extLbr3;
                                extAudioId2 = extX96k;
                                extXbr = extXbr3;
                            } else {
                                extXch = extXch3;
                                int extXXch2 = extXXch;
                                if (extSync == 496366178) {
                                    extXXch = extXXch2;
                                    if (extX96k == 1) {
                                        done = true;
                                    }
                                    extAudioId2 = 1;
                                    extLbr2 = extLbr3;
                                    extXbr = extXbr3;
                                } else {
                                    extXXch = extXXch2;
                                    int extX96k2 = extX96k;
                                    if (extSync == 1700671838) {
                                        int extX96k3 = extX96k2;
                                        if (extXbr3 == 1) {
                                            done = true;
                                        }
                                        extXbr = 1;
                                        extLbr2 = extLbr3;
                                        extAudioId2 = extX96k3;
                                    } else {
                                        int extX96k4 = extX96k2;
                                        extXbr = extXbr3;
                                        if (extSync == 176167201) {
                                            if (extLbr3 == 1) {
                                                done = true;
                                            }
                                            extLbr2 = 1;
                                            extAudioId2 = extX96k4;
                                        } else if (extSync == 1101174087) {
                                            if (extXll2 == 1) {
                                                done = true;
                                            }
                                            extXll2 = 1;
                                            extLbr2 = extLbr3;
                                            extAudioId2 = extX96k4;
                                        } else if (extSync == 45126241) {
                                            if (extCore3 == 1) {
                                                done = true;
                                            }
                                            extCore3 = 1;
                                            extLbr2 = extLbr3;
                                            extAudioId2 = extX96k4;
                                        } else {
                                            extLbr2 = extLbr3;
                                            extAudioId2 = extX96k4;
                                        }
                                    }
                                }
                            }
                        }
                        if (!done) {
                            this.frameSize += nuExtSSFsize;
                        }
                        bb7.position(offset2 + nuExtSSFsize);
                        offset = extXbr;
                        bb = bb7;
                        extPresent2 = 1;
                        dataSize = dataSize3;
                        testHeader2 = testHeader26;
                        extAudio2 = extXch;
                        corePresent = extXXch;
                        bb4 = extCore3;
                        extCore = testHeader15;
                        testHeader14 = extXll2;
                        extXll = amode;
                    } else {
                        throw new IOException("No DTS_SYNCWORD_* found at " + bb7.position());
                    }
                }
            }
            int i12 = this.samplesPerFrame;
            int extPresent4 = extAudio2;
            int i13 = testHeader2;
            if (i12 == 512) {
                fd = 0;
            } else if (i12 == 1024) {
                fd = 1;
            } else if (i12 != 2048) {
                fd = i12 != 4096 ? -1 : 3;
            } else {
                fd = 2;
            }
            if (fd == -1) {
                return false;
            }
            if (!(extXll == 0 || extXll == 2)) {
                switch (extXll) {
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        break;
                    default:
                        coreLayout = 31;
                        break;
                }
            }
            coreLayout = extXll;
            if (extXbr2 == 0) {
                int i14 = extXll;
                int amode3 = testHeader14;
                int extXll3 = extCore;
                if (amode3 != 1) {
                    ByteBuffer extCore4 = bb4;
                    ByteBuffer byteBuffer = bb;
                    int extLbr4 = extLbr2;
                    extLbr = extXbr2;
                    if (extLbr4 == 1) {
                        streamContruction2 = 18;
                        this.type = AudioSampleEntry.TYPE13;
                    } else {
                        if (extCore4 == 1) {
                            this.type = AudioSampleEntry.TYPE12;
                            if (corePresent == 0 && amode3 == 0) {
                                streamContruction2 = 19;
                            } else if (corePresent == 1 && amode3 == 0) {
                                streamContruction2 = 20;
                            } else if (corePresent == 0 && amode3 == 1) {
                                streamContruction2 = 21;
                            }
                        }
                        streamContruction2 = 0;
                    }
                } else if (bb4 == null) {
                    this.type = AudioSampleEntry.TYPE11;
                    streamContruction2 = 17;
                    ByteBuffer byteBuffer2 = bb;
                    int i15 = extLbr2;
                    extLbr = extXbr2;
                } else {
                    this.type = AudioSampleEntry.TYPE12;
                    streamContruction2 = 21;
                    ByteBuffer byteBuffer3 = bb;
                    int i16 = extLbr2;
                    extLbr = extXbr2;
                }
                this.samplerate = this.maxSampleRate;
                this.sampleSize = 24;
                streamContruction = streamContruction2;
                extPresent = extPresent2;
            } else {
                int amode4 = extXll;
                int amode5 = testHeader14;
                int extXll4 = extCore;
                ByteBuffer extCore5 = bb4;
                ByteBuffer byteBuffer4 = bb;
                int extLbr5 = extLbr2;
                extLbr = extXbr2;
                if (extPresent2 >= 1) {
                    extPresent = extPresent2;
                    this.type = AudioSampleEntry.TYPE12;
                    if (extAudio == 0) {
                        if (extCore5 == null && corePresent == 1 && extAudioId2 == 0 && offset == 0 && amode5 == 0 && extLbr5 == 0) {
                            streamContruction = 5;
                        } else if (extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 1 && amode5 == 0 && extLbr5 == 0) {
                            streamContruction = 6;
                        } else if (extCore5 == null && corePresent == 1 && extAudioId2 == 0 && offset == 1 && amode5 == 0 && extLbr5 == 0) {
                            streamContruction = 9;
                        } else if (extCore5 == null && corePresent == 0 && extAudioId2 == 1 && offset == 0 && amode5 == 0 && extLbr5 == 0) {
                            streamContruction = 10;
                        } else if (extCore5 == null && corePresent == 1 && extAudioId2 == 1 && offset == 0 && amode5 == 0 && extLbr5 == 0) {
                            streamContruction = 13;
                        } else if (extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 0 && amode5 == 1 && extLbr5 == 0) {
                            streamContruction = 14;
                        }
                    } else if (extAudioId == 0 && extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 1 && amode5 == 0 && extLbr5 == 0) {
                        streamContruction = 7;
                    } else if (extAudioId == 6 && extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 1 && amode5 == 0 && extLbr5 == 0) {
                        streamContruction = 8;
                    } else if (extAudioId == 0 && extCore5 == null && corePresent == 0 && extAudioId2 == 1 && offset == 0 && amode5 == 0 && extLbr5 == 0) {
                        streamContruction = 11;
                    } else if (extAudioId == 6 && extCore5 == null && corePresent == 0 && extAudioId2 == 1 && offset == 0 && amode5 == 0 && extLbr5 == 0) {
                        streamContruction = 12;
                    } else if (extAudioId == 0 && extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 0 && amode5 == 1 && extLbr5 == 0) {
                        streamContruction = 15;
                    } else if (extAudioId == 2 && extCore5 == null && corePresent == 0 && extAudioId2 == 0 && offset == 0 && amode5 == 1 && extLbr5 == 0) {
                        streamContruction = 16;
                    }
                    streamContruction = 0;
                } else if (extAudio <= 0) {
                    extPresent = extPresent2;
                    this.type = "dtsc";
                    streamContruction = 1;
                } else if (extAudioId != 0) {
                    extPresent = extPresent2;
                    if (extAudioId == 2) {
                        this.type = "dtsc";
                        streamContruction = 4;
                    } else if (extAudioId != 6) {
                        this.type = AudioSampleEntry.TYPE12;
                        streamContruction = 0;
                    } else {
                        this.type = AudioSampleEntry.TYPE12;
                        streamContruction = 3;
                    }
                } else {
                    extPresent = extPresent2;
                    this.type = "dtsc";
                    streamContruction = 2;
                }
            }
            int extXXch3 = corePresent;
            this.ddts.setDTSSamplingFrequency((long) this.maxSampleRate);
            if (this.isVBR) {
                this.ddts.setMaxBitRate((long) ((this.coreBitRate + this.extPeakBitrate) * 1000));
            } else {
                this.ddts.setMaxBitRate((long) ((this.coreBitRate + this.extAvgBitrate) * 1000));
            }
            this.ddts.setAvgBitRate((long) ((this.coreBitRate + this.extAvgBitrate) * 1000));
            this.ddts.setPcmSampleDepth(this.sampleSize);
            this.ddts.setFrameDuration(fd);
            this.ddts.setStreamConstruction(streamContruction);
            int i17 = this.coreChannelMask;
            if ((i17 & 8) > 0 || (i17 & 4096) > 0) {
                this.ddts.setCoreLFEPresent(1);
            } else {
                this.ddts.setCoreLFEPresent(0);
            }
            this.ddts.setCoreLayout(coreLayout);
            this.ddts.setCoreSize(this.coreFramePayloadInBytes);
            this.ddts.setStereoDownmix(0);
            this.ddts.setRepresentationType(4);
            this.ddts.setChannelLayout(this.channelMask);
            if (this.coreMaxSampleRate <= 0 || this.extAvgBitrate <= 0) {
                i = 0;
                this.ddts.setMultiAssetFlag(0);
            } else {
                this.ddts.setMultiAssetFlag(1);
                i = 0;
            }
            this.ddts.setLBRDurationMod(this.lbrCodingPresent);
            this.ddts.setReservedBoxPresent(i);
            this.channelCount = i;
            int bit = 0;
            while (bit < 16) {
                int extXbr4 = offset;
                int extX96k5 = extAudioId2;
                int fd2 = fd;
                int coreLayout2 = coreLayout;
                int extXXch4 = extXXch3;
                int extXch4 = extPresent4;
                int extPresent5 = extPresent;
                if (((this.channelMask >> bit) & 1) == 1) {
                    if (bit == 0 || bit == 12 || bit == 14 || bit == 3 || bit == 4 || bit == 7 || bit == 8) {
                        this.channelCount++;
                    } else {
                        this.channelCount += 2;
                    }
                }
                bit++;
                coreLayout = coreLayout2;
                fd = fd2;
                extPresent = extPresent5;
                extAudioId2 = extX96k5;
                extPresent4 = extXch4;
                extXXch3 = extXXch4;
                offset = extXbr4;
            }
            int i18 = coreLayout;
            int i19 = extAudioId2;
            int i20 = fd;
            int i21 = extXXch3;
            int i22 = extPresent4;
            int i23 = offset;
            int i24 = extPresent;
            List<Sample> generateSamples = generateSamples(this.dataSource, this.dataOffset, dataSize, extLbr);
            this.samples = generateSamples;
            long[] jArr = new long[generateSamples.size()];
            this.sampleDurations = jArr;
            Arrays.fill(jArr, (long) this.samplesPerFrame);
            return true;
        }
        throw new IOException("data does not start with 'DTSHDHDR' as required for a DTS-HD file");
    }

    private List<Sample> generateSamples(DataSource dataSource2, int dataOffset2, long dataSize, int corePresent) throws IOException {
        LookAhead la = new LookAhead(dataSource2, (long) dataOffset2, dataSize, corePresent);
        List<Sample> mySamples = new ArrayList<>();
        while (true) {
            ByteBuffer findNextStart = la.findNextStart();
            ByteBuffer sample = findNextStart;
            if (findNextStart == null) {
                System.err.println("all samples found");
                return mySamples;
            }
            final ByteBuffer finalSample = sample;
            mySamples.add(new Sample() {
                public void writeTo(WritableByteChannel channel) throws IOException {
                    channel.write((ByteBuffer) finalSample.rewind());
                }

                public long getSize() {
                    return (long) finalSample.rewind().remaining();
                }

                public ByteBuffer asByteBuffer() {
                    return finalSample;
                }
            });
        }
    }

    private int getBitRate(int rate) throws IOException {
        switch (rate) {
            case 0:
                return 32;
            case 1:
                return 56;
            case 2:
                return 64;
            case 3:
                return 96;
            case 4:
                return 112;
            case 5:
                return 128;
            case 6:
                return PsExtractor.AUDIO_STREAM;
            case 7:
                return 224;
            case 8:
                return 256;
            case 9:
                return 320;
            case 10:
                return 384;
            case 11:
                return 448;
            case 12:
                return 512;
            case 13:
                return 576;
            case 14:
                return UVCCamera.DEFAULT_PREVIEW_WIDTH;
            case 15:
                return 768;
            case 16:
                return 960;
            case 17:
                return 1024;
            case 18:
                return 1152;
            case 19:
                return 1280;
            case 20:
                return 1344;
            case 21:
                return 1408;
            case 22:
                return 1411;
            case 23:
                return 1472;
            case 24:
                return 1536;
            case 25:
                return -1;
            default:
                throw new IOException("Unknown bitrate value");
        }
    }

    private int getSampleRate(int sfreq) throws IOException {
        switch (sfreq) {
            case 1:
                return 8000;
            case 2:
                return AudioEditConstant.ExportSampleRate;
            case 3:
                return 32000;
            case 6:
                return 11025;
            case 7:
                return 22050;
            case 8:
                return 44100;
            case 11:
                return 12000;
            case 12:
                return 24000;
            case 13:
                return 48000;
            default:
                throw new IOException("Unknown Sample Rate");
        }
    }

    class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos;
        private final int corePresent;
        long dataEnd;
        DataSource dataSource;
        int inBufferPos = 0;
        long start;

        LookAhead(DataSource dataSource2, long bufferStartPos2, long dataSize, int corePresent2) throws IOException {
            this.dataSource = dataSource2;
            this.bufferStartPos = bufferStartPos2;
            this.dataEnd = dataSize + bufferStartPos2;
            this.corePresent = corePresent2;
            fillBuffer();
        }

        public ByteBuffer findNextStart() throws IOException {
            while (true) {
                try {
                    if (this.corePresent == 1) {
                        if (nextFourEquals0x7FFE8001()) {
                            break;
                        }
                        discardByte();
                    } else if (nextFourEquals0x64582025()) {
                        break;
                    } else {
                        discardByte();
                    }
                } catch (EOFException e) {
                    return null;
                }
            }
            discardNext4AndMarkStart();
            while (true) {
                if (this.corePresent == 1) {
                    if (nextFourEquals0x7FFE8001orEof()) {
                        break;
                    }
                    discardQWord();
                } else if (nextFourEquals0x64582025orEof()) {
                    break;
                } else {
                    discardQWord();
                }
            }
            return getSample();
        }

        private void fillBuffer() throws IOException {
            System.err.println("Fill Buffer");
            DataSource dataSource2 = this.dataSource;
            long j = this.bufferStartPos;
            this.buffer = dataSource2.map(j, Math.min(this.dataEnd - j, 67108864));
        }

        private boolean nextFourEquals0x64582025() throws IOException {
            return nextFourEquals((byte) 100, (byte) 88, (byte) 32, (byte) 37);
        }

        private boolean nextFourEquals0x7FFE8001() throws IOException {
            return nextFourEquals(ByteCompanionObject.MAX_VALUE, (byte) -2, ByteCompanionObject.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEquals(byte a, byte b, byte c, byte d) throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 4) {
                if (this.buffer.get(i) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d) {
                    return true;
                }
                return false;
            } else if (this.bufferStartPos + ((long) i) + 4 < this.dataSource.size()) {
                return false;
            } else {
                throw new EOFException();
            }
        }

        private boolean nextFourEquals0x64582025orEof() throws IOException {
            return nextFourEqualsOrEof((byte) 100, (byte) 88, (byte) 32, (byte) 37);
        }

        private boolean nextFourEquals0x7FFE8001orEof() throws IOException {
            return nextFourEqualsOrEof(ByteCompanionObject.MAX_VALUE, (byte) -2, ByteCompanionObject.MIN_VALUE, (byte) 1);
        }

        private boolean nextFourEqualsOrEof(byte a, byte b, byte c, byte d) throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 4) {
                if ((this.bufferStartPos + ((long) i)) % 1048576 == 0) {
                    PrintStream printStream = System.err;
                    StringBuilder sb = new StringBuilder();
                    sb.append(((this.bufferStartPos + ((long) this.inBufferPos)) / 1024) / 1024);
                    printStream.println(sb.toString());
                }
                return this.buffer.get(this.inBufferPos) == a && this.buffer.get(this.inBufferPos + 1) == b && this.buffer.get(this.inBufferPos + 2) == c && this.buffer.get(this.inBufferPos + 3) == d;
            }
            long j = this.bufferStartPos;
            long j2 = this.dataEnd;
            if (((long) i) + j + 4 > j2) {
                return j + ((long) i) == j2;
            }
            this.bufferStartPos = this.start;
            this.inBufferPos = 0;
            fillBuffer();
            return nextFourEquals0x7FFE8001();
        }

        private void discardByte() {
            this.inBufferPos++;
        }

        private void discardQWord() {
            this.inBufferPos += 4;
        }

        private void discardNext4AndMarkStart() {
            long j = this.bufferStartPos;
            int i = this.inBufferPos;
            this.start = j + ((long) i);
            this.inBufferPos = i + 4;
        }

        private ByteBuffer getSample() {
            long j = this.start;
            long j2 = this.bufferStartPos;
            if (j >= j2) {
                this.buffer.position((int) (j - j2));
                ByteBuffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }
}
