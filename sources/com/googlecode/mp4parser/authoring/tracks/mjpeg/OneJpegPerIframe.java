package com.googlecode.mp4parser.authoring.tracks.mjpeg;

import com.coremedia.iso.Hex;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ObjectDescriptorFactory;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class OneJpegPerIframe extends AbstractTrack {
    File[] jpegs;
    long[] sampleDurations;
    SampleDescriptionBox stsd;
    long[] syncSamples;
    TrackMetaData trackMetaData = new TrackMetaData();

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public OneJpegPerIframe(String name, File[] jpegs2, Track alignTo) throws IOException {
        super(name);
        long duration;
        long[] sampleDurationsToiAlignTo;
        BufferedImage a;
        int i;
        long duration2;
        File[] fileArr = jpegs2;
        this.jpegs = fileArr;
        if (alignTo.getSyncSamples().length == fileArr.length) {
            int i2 = 0;
            BufferedImage a2 = ImageIO.read(fileArr[0]);
            this.trackMetaData.setWidth((double) a2.getWidth());
            this.trackMetaData.setHeight((double) a2.getHeight());
            this.trackMetaData.setTimescale(alignTo.getTrackMetaData().getTimescale());
            long[] sampleDurationsToiAlignTo2 = alignTo.getSampleDurations();
            long[] syncSamples2 = alignTo.getSyncSamples();
            int currentSyncSample = 1;
            long duration3 = 0;
            this.sampleDurations = new long[syncSamples2.length];
            int i3 = 1;
            while (i3 < sampleDurationsToiAlignTo.length) {
                BufferedImage a3 = a;
                long[] sampleDurationsToiAlignTo3 = sampleDurationsToiAlignTo;
                long duration4 = duration;
                if (currentSyncSample >= syncSamples2.length || ((long) i3) != syncSamples2[currentSyncSample]) {
                    duration2 = duration4;
                } else {
                    this.sampleDurations[currentSyncSample - 1] = duration4;
                    duration2 = 0;
                    currentSyncSample++;
                }
                duration3 = duration2 + sampleDurationsToiAlignTo3[i3];
                i3++;
                fileArr = jpegs2;
                sampleDurationsToiAlignTo2 = sampleDurationsToiAlignTo3;
                a2 = a3;
                i2 = 0;
            }
            long[] jArr = this.sampleDurations;
            jArr[jArr.length - 1] = duration;
            this.stsd = new SampleDescriptionBox();
            VisualSampleEntry mp4v = new VisualSampleEntry(VisualSampleEntry.TYPE1);
            this.stsd.addBox(mp4v);
            ESDescriptorBox esds = new ESDescriptorBox();
            esds.setData(ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102")));
            esds.setEsDescriptor((ESDescriptor) ObjectDescriptorFactory.createFrom(-1, ByteBuffer.wrap(Hex.decodeHex("038080801B000100048080800D6C11000000000A1CB4000A1CB4068080800102"))));
            mp4v.addBox(esds);
            this.syncSamples = new long[fileArr.length];
            int i4 = 0;
            while (true) {
                long[] jArr2 = this.syncSamples;
                if (i4 >= jArr2.length) {
                    break;
                }
                long[] jArr3 = sampleDurationsToiAlignTo;
                long j = duration;
                VisualSampleEntry visualSampleEntry = mp4v;
                ESDescriptorBox eSDescriptorBox = esds;
                jArr2[i4] = (long) (i4 + 1);
                i4++;
                File[] fileArr2 = jpegs2;
                i2 = 0;
            }
            double earliestTrackPresentationTime = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
            boolean acceptDwell = true;
            boolean acceptEdit = true;
            for (Edit edit : alignTo.getEdits()) {
                BufferedImage a4 = a;
                long[] sampleDurationsToiAlignTo4 = sampleDurationsToiAlignTo;
                long duration5 = duration;
                VisualSampleEntry mp4v2 = mp4v;
                ESDescriptorBox esds2 = esds;
                if (edit.getMediaTime() == -1 && !acceptDwell) {
                    throw new RuntimeException("Cannot accept edit list for processing (1)");
                } else if (edit.getMediaTime() >= 0 && !acceptEdit) {
                    throw new RuntimeException("Cannot accept edit list for processing (2)");
                } else if (edit.getMediaTime() == -1) {
                    earliestTrackPresentationTime += edit.getSegmentDuration();
                    File[] fileArr3 = jpegs2;
                    sampleDurationsToiAlignTo = sampleDurationsToiAlignTo4;
                    mp4v = mp4v2;
                    a = a4;
                    esds = esds2;
                    duration = duration5;
                    i = 0;
                } else {
                    earliestTrackPresentationTime -= ((double) edit.getMediaTime()) / ((double) edit.getTimeScale());
                    acceptEdit = false;
                    acceptDwell = false;
                    File[] fileArr4 = jpegs2;
                    sampleDurationsToiAlignTo = sampleDurationsToiAlignTo4;
                    mp4v = mp4v2;
                    a = a4;
                    esds = esds2;
                    duration = duration5;
                    i = 0;
                }
            }
            if (alignTo.getCompositionTimeEntries() == null || alignTo.getCompositionTimeEntries().size() <= 0) {
                VisualSampleEntry visualSampleEntry2 = mp4v;
                ESDescriptorBox eSDescriptorBox2 = esds;
            } else {
                long currentTime = 0;
                BufferedImage bufferedImage = a;
                VisualSampleEntry visualSampleEntry3 = mp4v;
                int[] ptss = Arrays.copyOfRange(CompositionTimeToSample.blowupCompositionTimes(alignTo.getCompositionTimeEntries()), i, 50);
                int j2 = 0;
                while (j2 < ptss.length) {
                    ESDescriptorBox esds3 = esds;
                    int[] ptss2 = ptss;
                    ptss2[j2] = (int) (((long) ptss[j2]) + currentTime);
                    currentTime += alignTo.getSampleDurations()[j2];
                    j2++;
                    File[] fileArr5 = jpegs2;
                    ptss = ptss2;
                    esds = esds3;
                }
                Arrays.sort(ptss);
                ESDescriptorBox eSDescriptorBox3 = esds;
                earliestTrackPresentationTime += ((double) ptss[0]) / ((double) alignTo.getTrackMetaData().getTimescale());
            }
            if (earliestTrackPresentationTime < FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                long[] jArr4 = sampleDurationsToiAlignTo;
                long j3 = duration;
                getEdits().add(new Edit((long) ((-earliestTrackPresentationTime) * ((double) getTrackMetaData().getTimescale())), getTrackMetaData().getTimescale(), 1.0d, ((double) getDuration()) / ((double) getTrackMetaData().getTimescale())));
                return;
            }
            long j4 = duration;
            if (earliestTrackPresentationTime > FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
                getEdits().add(new Edit(-1, getTrackMetaData().getTimescale(), 1.0d, earliestTrackPresentationTime));
                getEdits().add(new Edit(0, getTrackMetaData().getTimescale(), 1.0d, ((double) getDuration()) / ((double) getTrackMetaData().getTimescale())));
                return;
            }
            return;
        }
        throw new RuntimeException("Number of sync samples doesn't match the number of stills (" + alignTo.getSyncSamples().length + " vs. " + jpegs2.length + SQLBuilder.PARENTHESES_RIGHT);
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return this.stsd;
    }

    public long[] getSampleDurations() {
        return this.sampleDurations;
    }

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public String getHandler() {
        return "vide";
    }

    public long[] getSyncSamples() {
        return this.syncSamples;
    }

    public List<Sample> getSamples() {
        return new AbstractList<Sample>() {
            public int size() {
                return OneJpegPerIframe.this.jpegs.length;
            }

            public Sample get(final int index) {
                return new Sample() {
                    ByteBuffer sample = null;

                    public void writeTo(WritableByteChannel channel) throws IOException {
                        RandomAccessFile raf = new RandomAccessFile(OneJpegPerIframe.this.jpegs[index], "r");
                        raf.getChannel().transferTo(0, raf.length(), channel);
                        raf.close();
                    }

                    public long getSize() {
                        return OneJpegPerIframe.this.jpegs[index].length();
                    }

                    public ByteBuffer asByteBuffer() {
                        if (this.sample == null) {
                            try {
                                RandomAccessFile raf = new RandomAccessFile(OneJpegPerIframe.this.jpegs[index], "r");
                                this.sample = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return this.sample;
                    }
                };
            }
        };
    }

    public void close() throws IOException {
    }
}
