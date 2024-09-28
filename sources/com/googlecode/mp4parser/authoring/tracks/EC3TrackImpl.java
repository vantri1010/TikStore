package com.googlecode.mp4parser.authoring.tracks;

import androidx.recyclerview.widget.ItemTouchHelper;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.CastUtils;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EC3TrackImpl extends AbstractTrack {
    private static final long MAX_FRAMES_PER_MMAP = 20;
    private List<BitStreamInfo> bitStreamInfos = new LinkedList();
    private int bitrate;
    /* access modifiers changed from: private */
    public final DataSource dataSource;
    private long[] decodingTimes;
    /* access modifiers changed from: private */
    public int frameSize;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    TrackMetaData trackMetaData = new TrackMetaData();

    public EC3TrackImpl(DataSource dataSource2) throws IOException {
        super(dataSource2.toString());
        this.dataSource = dataSource2;
        boolean done = false;
        while (!done) {
            BitStreamInfo bsi = readVariables();
            if (bsi != null) {
                for (BitStreamInfo entry : this.bitStreamInfos) {
                    if (bsi.strmtyp != 1 && entry.substreamid == bsi.substreamid) {
                        done = true;
                    }
                }
                if (!done) {
                    this.bitStreamInfos.add(bsi);
                }
            } else {
                throw new IOException();
            }
        }
        if (this.bitStreamInfos.size() != 0) {
            int samplerate = this.bitStreamInfos.get(0).samplerate;
            this.sampleDescriptionBox = new SampleDescriptionBox();
            AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE9);
            audioSampleEntry.setChannelCount(2);
            audioSampleEntry.setSampleRate((long) samplerate);
            audioSampleEntry.setDataReferenceIndex(1);
            audioSampleEntry.setSampleSize(16);
            EC3SpecificBox ec3 = new EC3SpecificBox();
            int[] deps = new int[this.bitStreamInfos.size()];
            int[] chan_locs = new int[this.bitStreamInfos.size()];
            for (BitStreamInfo bsi2 : this.bitStreamInfos) {
                if (bsi2.strmtyp == 1) {
                    int i = bsi2.substreamid;
                    deps[i] = deps[i] + 1;
                    chan_locs[bsi2.substreamid] = ((bsi2.chanmap >> 6) & 256) | ((bsi2.chanmap >> 5) & 255);
                }
            }
            for (BitStreamInfo bsi3 : this.bitStreamInfos) {
                if (bsi3.strmtyp != 1) {
                    EC3SpecificBox.Entry e = new EC3SpecificBox.Entry();
                    e.fscod = bsi3.fscod;
                    e.bsid = bsi3.bsid;
                    e.bsmod = bsi3.bsmod;
                    e.acmod = bsi3.acmod;
                    e.lfeon = bsi3.lfeon;
                    e.reserved = 0;
                    e.num_dep_sub = deps[bsi3.substreamid];
                    e.chan_loc = chan_locs[bsi3.substreamid];
                    e.reserved2 = 0;
                    ec3.addEntry(e);
                }
                this.bitrate += bsi3.bitrate;
                this.frameSize += bsi3.frameSize;
            }
            ec3.setDataRate(this.bitrate / 1000);
            audioSampleEntry.addBox(ec3);
            this.sampleDescriptionBox.addBox(audioSampleEntry);
            this.trackMetaData.setCreationTime(new Date());
            this.trackMetaData.setModificationTime(new Date());
            this.trackMetaData.setTimescale((long) samplerate);
            this.trackMetaData.setVolume(1.0f);
            dataSource2.position(0);
            List<Sample> readSamples = readSamples();
            this.samples = readSamples;
            long[] jArr = new long[readSamples.size()];
            this.decodingTimes = jArr;
            Arrays.fill(jArr, 1536);
            return;
        }
        throw new IOException();
    }

    public void close() throws IOException {
        this.dataSource.close();
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return null;
    }

    public long[] getSyncSamples() {
        return null;
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
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

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    private BitStreamInfo readVariables() throws IOException {
        int numblkscod;
        int i;
        int i2;
        int i3;
        long startPosition = this.dataSource.position();
        ByteBuffer bb = ByteBuffer.allocate(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.dataSource.read(bb);
        bb.rewind();
        BitReaderBuffer brb = new BitReaderBuffer(bb);
        if (brb.readBits(16) != 2935) {
            return null;
        }
        BitStreamInfo entry = new BitStreamInfo();
        entry.strmtyp = brb.readBits(2);
        entry.substreamid = brb.readBits(3);
        entry.frameSize = (brb.readBits(11) + 1) * 2;
        entry.fscod = brb.readBits(2);
        int fscod2 = -1;
        if (entry.fscod == 3) {
            fscod2 = brb.readBits(2);
            numblkscod = 3;
        } else {
            numblkscod = brb.readBits(2);
        }
        int numberOfBlocksPerSyncFrame = 0;
        if (numblkscod == 0) {
            numberOfBlocksPerSyncFrame = 1;
        } else if (numblkscod == 1) {
            numberOfBlocksPerSyncFrame = 2;
        } else if (numblkscod == 2) {
            numberOfBlocksPerSyncFrame = 3;
        } else if (numblkscod == 3) {
            numberOfBlocksPerSyncFrame = 6;
        }
        entry.frameSize *= 6 / numberOfBlocksPerSyncFrame;
        entry.acmod = brb.readBits(3);
        entry.lfeon = brb.readBits(1);
        entry.bsid = brb.readBits(5);
        brb.readBits(5);
        if (1 == brb.readBits(1)) {
            brb.readBits(8);
        }
        if (entry.acmod == 0) {
            brb.readBits(5);
            if (1 == brb.readBits(1)) {
                brb.readBits(8);
            }
        }
        if (1 == entry.strmtyp && 1 == brb.readBits(1)) {
            entry.chanmap = brb.readBits(16);
        }
        if (1 == brb.readBits(1)) {
            if (entry.acmod > 2) {
                brb.readBits(2);
            }
            if (1 == (entry.acmod & 1) && entry.acmod > 2) {
                brb.readBits(3);
                brb.readBits(3);
            }
            if ((entry.acmod & 4) > 0) {
                brb.readBits(3);
                brb.readBits(3);
            }
            if (1 == entry.lfeon && 1 == brb.readBits(1)) {
                brb.readBits(5);
            }
            if (entry.strmtyp == 0) {
                if (1 == brb.readBits(1)) {
                    i2 = 6;
                    brb.readBits(6);
                } else {
                    i2 = 6;
                }
                if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                    brb.readBits(i2);
                }
                if (1 == brb.readBits(1)) {
                    brb.readBits(i2);
                }
                int mixdef = brb.readBits(2);
                if (1 == mixdef) {
                    brb.readBits(5);
                } else if (2 == mixdef) {
                    brb.readBits(12);
                } else if (3 == mixdef) {
                    int mixdeflen = brb.readBits(5);
                    if (1 == brb.readBits(1)) {
                        brb.readBits(5);
                        if (1 == brb.readBits(1)) {
                            i3 = 4;
                            brb.readBits(4);
                        } else {
                            i3 = 4;
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            brb.readBits(i3);
                        }
                        if (1 == brb.readBits(1)) {
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i3);
                            }
                            if (1 == brb.readBits(1)) {
                                brb.readBits(i3);
                            }
                        }
                    }
                    if (1 == brb.readBits(1)) {
                        brb.readBits(5);
                        if (1 == brb.readBits(1)) {
                            brb.readBits(7);
                            if (1 == brb.readBits(1)) {
                                brb.readBits(8);
                            }
                        }
                    }
                    for (int i4 = 0; i4 < mixdeflen + 2; i4++) {
                        brb.readBits(8);
                    }
                    brb.byteSync();
                }
                if (entry.acmod < 2) {
                    if (1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (entry.acmod == 0 && 1 == brb.readBits(1)) {
                        brb.readBits(14);
                    }
                    if (1 == brb.readBits(1)) {
                        if (numblkscod == 0) {
                            brb.readBits(5);
                        } else {
                            for (int i5 = 0; i5 < numberOfBlocksPerSyncFrame; i5++) {
                                if (1 == brb.readBits(1)) {
                                    brb.readBits(5);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (1 == brb.readBits(1)) {
            i = 3;
            entry.bsmod = brb.readBits(3);
        } else {
            i = 3;
        }
        int i6 = entry.fscod;
        if (i6 == 0) {
            entry.samplerate = 48000;
        } else if (i6 == 1) {
            entry.samplerate = 44100;
        } else if (i6 == 2) {
            entry.samplerate = 32000;
        } else if (i6 == i) {
            if (fscod2 == 0) {
                entry.samplerate = 24000;
            } else if (fscod2 == 1) {
                entry.samplerate = 22050;
            } else if (fscod2 == 2) {
                entry.samplerate = AudioEditConstant.ExportSampleRate;
            } else if (fscod2 == i) {
                entry.samplerate = 0;
            }
        }
        if (entry.samplerate == 0) {
            return null;
        }
        ByteBuffer byteBuffer = bb;
        BitReaderBuffer bitReaderBuffer = brb;
        entry.bitrate = (int) ((((double) entry.samplerate) / 1536.0d) * ((double) entry.frameSize) * 8.0d);
        this.dataSource.position(((long) entry.frameSize) + startPosition);
        return entry;
    }

    private List<Sample> readSamples() throws IOException {
        int framesLeft = CastUtils.l2i((this.dataSource.size() - this.dataSource.position()) / ((long) this.frameSize));
        List<Sample> mySamples = new ArrayList<>(framesLeft);
        for (int i = 0; i < framesLeft; i++) {
            final int start = this.frameSize * i;
            mySamples.add(new Sample() {
                public void writeTo(WritableByteChannel channel) throws IOException {
                    EC3TrackImpl.this.dataSource.transferTo((long) start, (long) EC3TrackImpl.this.frameSize, channel);
                }

                public long getSize() {
                    return (long) EC3TrackImpl.this.frameSize;
                }

                public ByteBuffer asByteBuffer() {
                    try {
                        return EC3TrackImpl.this.dataSource.map((long) start, (long) EC3TrackImpl.this.frameSize);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        return mySamples;
    }

    public static class BitStreamInfo extends EC3SpecificBox.Entry {
        public int bitrate;
        public int chanmap;
        public int frameSize;
        public int samplerate;
        public int strmtyp;
        public int substreamid;

        public String toString() {
            return "BitStreamInfo{frameSize=" + this.frameSize + ", substreamid=" + this.substreamid + ", bitrate=" + this.bitrate + ", samplerate=" + this.samplerate + ", strmtyp=" + this.strmtyp + ", chanmap=" + this.chanmap + '}';
        }
    }

    public String toString() {
        return "EC3TrackImpl{bitrate=" + this.bitrate + ", bitStreamInfos=" + this.bitStreamInfos + '}';
    }
}
