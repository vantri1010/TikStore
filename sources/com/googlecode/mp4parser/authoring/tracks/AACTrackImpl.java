package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import im.bclpbkiauv.ui.utils.translate.common.AudioEditConstant;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AACTrackImpl extends AbstractTrack {
    static Map<Integer, String> audioObjectTypes;
    public static Map<Integer, Integer> samplingFrequencyIndexMap;
    long avgBitRate;
    int bufferSizeDB;
    /* access modifiers changed from: private */
    public DataSource dataSource;
    long[] decTimes;
    AdtsHeader firstHeader;
    private String lang;
    long maxBitRate;
    SampleDescriptionBox sampleDescriptionBox;
    private List<Sample> samples;
    TrackMetaData trackMetaData;

    static {
        HashMap hashMap = new HashMap();
        audioObjectTypes = hashMap;
        hashMap.put(1, "AAC Main");
        audioObjectTypes.put(2, "AAC LC (Low Complexity)");
        audioObjectTypes.put(3, "AAC SSR (Scalable Sample Rate)");
        audioObjectTypes.put(4, "AAC LTP (Long Term Prediction)");
        audioObjectTypes.put(5, "SBR (Spectral Band Replication)");
        audioObjectTypes.put(6, "AAC Scalable");
        audioObjectTypes.put(7, "TwinVQ");
        audioObjectTypes.put(8, "CELP (Code Excited Linear Prediction)");
        audioObjectTypes.put(9, "HXVC (Harmonic Vector eXcitation Coding)");
        audioObjectTypes.put(10, "Reserved");
        audioObjectTypes.put(11, "Reserved");
        audioObjectTypes.put(12, "TTSI (Text-To-Speech Interface)");
        audioObjectTypes.put(13, "Main Synthesis");
        audioObjectTypes.put(14, "Wavetable Synthesis");
        audioObjectTypes.put(15, "General MIDI");
        audioObjectTypes.put(16, "Algorithmic Synthesis and Audio Effects");
        audioObjectTypes.put(17, "ER (Error Resilient) AAC LC");
        audioObjectTypes.put(18, "Reserved");
        audioObjectTypes.put(19, "ER AAC LTP");
        audioObjectTypes.put(20, "ER AAC Scalable");
        audioObjectTypes.put(21, "ER TwinVQ");
        audioObjectTypes.put(22, "ER BSAC (Bit-Sliced Arithmetic Coding)");
        audioObjectTypes.put(23, "ER AAC LD (Low Delay)");
        audioObjectTypes.put(24, "ER CELP");
        audioObjectTypes.put(25, "ER HVXC");
        audioObjectTypes.put(26, "ER HILN (Harmonic and Individual Lines plus Noise)");
        audioObjectTypes.put(27, "ER Parametric");
        audioObjectTypes.put(28, "SSC (SinuSoidal Coding)");
        audioObjectTypes.put(29, "PS (Parametric Stereo)");
        audioObjectTypes.put(30, "MPEG Surround");
        audioObjectTypes.put(31, "(Escape value)");
        audioObjectTypes.put(32, "Layer-1");
        audioObjectTypes.put(33, "Layer-2");
        audioObjectTypes.put(34, "Layer-3");
        audioObjectTypes.put(35, "DST (Direct Stream Transfer)");
        audioObjectTypes.put(36, "ALS (Audio Lossless)");
        audioObjectTypes.put(37, "SLS (Scalable LosslesS)");
        audioObjectTypes.put(38, "SLS non-core");
        audioObjectTypes.put(39, "ER AAC ELD (Enhanced Low Delay)");
        audioObjectTypes.put(40, "SMR (Symbolic Music Representation) Simple");
        audioObjectTypes.put(41, "SMR Main");
        audioObjectTypes.put(42, "USAC (Unified Speech and Audio Coding) (no SBR)");
        audioObjectTypes.put(43, "SAOC (Spatial Audio Object Coding)");
        audioObjectTypes.put(44, "LD MPEG Surround");
        audioObjectTypes.put(45, "USAC");
        HashMap hashMap2 = new HashMap();
        samplingFrequencyIndexMap = hashMap2;
        hashMap2.put(96000, 0);
        samplingFrequencyIndexMap.put(88200, 1);
        samplingFrequencyIndexMap.put(64000, 2);
        samplingFrequencyIndexMap.put(48000, 3);
        samplingFrequencyIndexMap.put(44100, 4);
        samplingFrequencyIndexMap.put(32000, 5);
        samplingFrequencyIndexMap.put(24000, 6);
        samplingFrequencyIndexMap.put(22050, 7);
        samplingFrequencyIndexMap.put(Integer.valueOf(AudioEditConstant.ExportSampleRate), 8);
        samplingFrequencyIndexMap.put(12000, 9);
        samplingFrequencyIndexMap.put(11025, 10);
        samplingFrequencyIndexMap.put(8000, 11);
        samplingFrequencyIndexMap.put(0, 96000);
        samplingFrequencyIndexMap.put(1, 88200);
        samplingFrequencyIndexMap.put(2, 64000);
        samplingFrequencyIndexMap.put(3, 48000);
        samplingFrequencyIndexMap.put(4, 44100);
        samplingFrequencyIndexMap.put(5, 32000);
        samplingFrequencyIndexMap.put(6, 24000);
        samplingFrequencyIndexMap.put(7, 22050);
        samplingFrequencyIndexMap.put(8, Integer.valueOf(AudioEditConstant.ExportSampleRate));
        samplingFrequencyIndexMap.put(9, 12000);
        samplingFrequencyIndexMap.put(10, 11025);
        samplingFrequencyIndexMap.put(11, 8000);
    }

    public void close() throws IOException {
        this.dataSource.close();
    }

    public AACTrackImpl(DataSource dataSource2) throws IOException {
        this(dataSource2, "eng");
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AACTrackImpl(DataSource dataSource2, String lang2) throws IOException {
        super(dataSource2.toString());
        String str = lang2;
        this.trackMetaData = new TrackMetaData();
        this.lang = "eng";
        this.lang = str;
        this.dataSource = dataSource2;
        this.samples = new ArrayList();
        AdtsHeader readSamples = readSamples(dataSource2);
        this.firstHeader = readSamples;
        double packetsPerSecond = ((double) readSamples.sampleRate) / 1024.0d;
        double duration = ((double) this.samples.size()) / packetsPerSecond;
        long dataSize = 0;
        LinkedList<Integer> queue = new LinkedList<>();
        for (Sample sample : this.samples) {
            double duration2 = duration;
            int size = (int) sample.getSize();
            dataSize += (long) size;
            queue.add(Integer.valueOf(size));
            while (((double) queue.size()) > packetsPerSecond) {
                queue.pop();
            }
            if (queue.size() == ((int) packetsPerSecond)) {
                int currSize = 0;
                Iterator it = queue.iterator();
                while (it.hasNext()) {
                    currSize += ((Integer) it.next()).intValue();
                }
                double currBitrate = ((((double) currSize) * 8.0d) / ((double) queue.size())) * packetsPerSecond;
                if (currBitrate > ((double) this.maxBitRate)) {
                    this.maxBitRate = (long) ((int) currBitrate);
                }
            }
            DataSource dataSource3 = dataSource2;
            duration = duration2;
        }
        this.avgBitRate = (long) ((int) (((double) (8 * dataSize)) / duration));
        this.bufferSizeDB = 1536;
        this.sampleDescriptionBox = new SampleDescriptionBox();
        AudioSampleEntry audioSampleEntry = new AudioSampleEntry(AudioSampleEntry.TYPE3);
        if (this.firstHeader.channelconfig == 7) {
            audioSampleEntry.setChannelCount(8);
        } else {
            audioSampleEntry.setChannelCount(this.firstHeader.channelconfig);
        }
        audioSampleEntry.setSampleRate((long) this.firstHeader.sampleRate);
        audioSampleEntry.setDataReferenceIndex(1);
        audioSampleEntry.setSampleSize(16);
        ESDescriptorBox esds = new ESDescriptorBox();
        ESDescriptor descriptor = new ESDescriptor();
        descriptor.setEsId(0);
        SLConfigDescriptor slConfigDescriptor = new SLConfigDescriptor();
        slConfigDescriptor.setPredefined(2);
        descriptor.setSlConfigDescriptor(slConfigDescriptor);
        DecoderConfigDescriptor decoderConfigDescriptor = new DecoderConfigDescriptor();
        decoderConfigDescriptor.setObjectTypeIndication(64);
        decoderConfigDescriptor.setStreamType(5);
        decoderConfigDescriptor.setBufferSizeDB(this.bufferSizeDB);
        double d = duration;
        decoderConfigDescriptor.setMaxBitRate(this.maxBitRate);
        decoderConfigDescriptor.setAvgBitRate(this.avgBitRate);
        AudioSpecificConfig audioSpecificConfig = new AudioSpecificConfig();
        audioSpecificConfig.setAudioObjectType(2);
        audioSpecificConfig.setSamplingFrequencyIndex(this.firstHeader.sampleFrequencyIndex);
        audioSpecificConfig.setChannelConfiguration(this.firstHeader.channelconfig);
        decoderConfigDescriptor.setAudioSpecificInfo(audioSpecificConfig);
        descriptor.setDecoderConfigDescriptor(decoderConfigDescriptor);
        ByteBuffer data = descriptor.serialize();
        esds.setEsDescriptor(descriptor);
        esds.setData(data);
        audioSampleEntry.addBox(esds);
        this.sampleDescriptionBox.addBox(audioSampleEntry);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setLanguage(str);
        this.trackMetaData.setVolume(1.0f);
        AudioSpecificConfig audioSpecificConfig2 = audioSpecificConfig;
        ByteBuffer byteBuffer = data;
        this.trackMetaData.setTimescale((long) this.firstHeader.sampleRate);
        long[] jArr = new long[this.samples.size()];
        this.decTimes = jArr;
        Arrays.fill(jArr, 1024);
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.sampleDescriptionBox;
    }

    public long[] getSampleDurations() {
        return this.decTimes;
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

    public List<Sample> getSamples() {
        return this.samples;
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return null;
    }

    class AdtsHeader {
        int bufferFullness;
        int channelconfig;
        int copyrightStart;
        int copyrightedStream;
        int frameLength;
        int home;
        int layer;
        int mpegVersion;
        int numAacFramesPerAdtsFrame;
        int original;
        int profile;
        int protectionAbsent;
        int sampleFrequencyIndex;
        int sampleRate;

        AdtsHeader() {
        }

        /* access modifiers changed from: package-private */
        public int getSize() {
            return (this.protectionAbsent == 0 ? 2 : 0) + 7;
        }
    }

    private AdtsHeader readADTSHeader(DataSource channel) throws IOException {
        AdtsHeader hdr = new AdtsHeader();
        ByteBuffer bb = ByteBuffer.allocate(7);
        while (bb.position() < 7) {
            if (channel.read(bb) == -1) {
                return null;
            }
        }
        BitReaderBuffer brb = new BitReaderBuffer((ByteBuffer) bb.rewind());
        if (brb.readBits(12) == 4095) {
            hdr.mpegVersion = brb.readBits(1);
            hdr.layer = brb.readBits(2);
            hdr.protectionAbsent = brb.readBits(1);
            hdr.profile = brb.readBits(2) + 1;
            hdr.sampleFrequencyIndex = brb.readBits(4);
            hdr.sampleRate = samplingFrequencyIndexMap.get(Integer.valueOf(hdr.sampleFrequencyIndex)).intValue();
            brb.readBits(1);
            hdr.channelconfig = brb.readBits(3);
            hdr.original = brb.readBits(1);
            hdr.home = brb.readBits(1);
            hdr.copyrightedStream = brb.readBits(1);
            hdr.copyrightStart = brb.readBits(1);
            hdr.frameLength = brb.readBits(13);
            hdr.bufferFullness = brb.readBits(11);
            hdr.numAacFramesPerAdtsFrame = brb.readBits(2) + 1;
            if (hdr.numAacFramesPerAdtsFrame == 1) {
                if (hdr.protectionAbsent == 0) {
                    channel.read(ByteBuffer.allocate(2));
                }
                return hdr;
            }
            throw new IOException("This muxer can only work with 1 AAC frame per ADTS frame");
        }
        throw new IOException("Expected Start Word 0xfff");
    }

    private AdtsHeader readSamples(DataSource channel) throws IOException {
        AdtsHeader first = null;
        while (true) {
            AdtsHeader readADTSHeader = readADTSHeader(channel);
            AdtsHeader hdr = readADTSHeader;
            if (readADTSHeader == null) {
                return first;
            }
            if (first == null) {
                first = hdr;
            }
            long currentPosition = channel.position();
            final long j = currentPosition;
            final long size = (long) (hdr.frameLength - hdr.getSize());
            this.samples.add(new Sample() {
                public void writeTo(WritableByteChannel channel) throws IOException {
                    AACTrackImpl.this.dataSource.transferTo(j, size, channel);
                }

                public long getSize() {
                    return size;
                }

                public ByteBuffer asByteBuffer() {
                    try {
                        return AACTrackImpl.this.dataSource.map(j, size);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            channel.position((channel.position() + ((long) hdr.frameLength)) - ((long) hdr.getSize()));
        }
    }

    public String toString() {
        return "AACTrackImpl{sampleRate=" + this.firstHeader.sampleRate + ", channelconfig=" + this.firstHeader.channelconfig + '}';
    }
}
