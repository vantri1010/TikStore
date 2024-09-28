package com.googlecode.mp4parser.authoring.tracks.h265;

import com.baidu.mapapi.UIMsg;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AbstractH26XTrack;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.ByteBufferByteChannel;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcDecoderConfigurationRecord;
import com.serenegiant.usb.UVCCamera;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;

public class H265TrackImpl extends AbstractH26XTrack implements NalUnitTypes {
    ArrayList<ByteBuffer> pps = new ArrayList<>();
    ArrayList<Sample> samples = new ArrayList<>();
    ArrayList<ByteBuffer> sps = new ArrayList<>();
    SampleDescriptionBox stsd;
    ArrayList<ByteBuffer> vps = new ArrayList<>();

    public H265TrackImpl(DataSource dataSource) throws IOException {
        super(dataSource);
        ArrayList<ByteBuffer> nals = new ArrayList<>();
        AbstractH26XTrack.LookAhead la = new AbstractH26XTrack.LookAhead(dataSource);
        boolean[] vclNalUnitSeenInAU = new boolean[1];
        boolean[] isIdr = {true};
        while (true) {
            ByteBuffer findNextNal = findNextNal(la);
            ByteBuffer nal = findNextNal;
            if (findNextNal == null) {
                this.stsd = createSampleDescriptionBox();
                this.decodingTimes = new long[this.samples.size()];
                getTrackMetaData().setTimescale(25);
                Arrays.fill(this.decodingTimes, 1);
                return;
            }
            NalUnitHeader unitHeader = getNalUnitHeader(nal);
            if (vclNalUnitSeenInAU[0]) {
                if (!isVcl(unitHeader)) {
                    switch (unitHeader.nalUnitType) {
                        case 32:
                        case 33:
                        case 34:
                        case 35:
                        case 36:
                        case 37:
                        case 39:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                            wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                            break;
                    }
                } else if ((nal.get(2) & ByteCompanionObject.MIN_VALUE) != 0) {
                    wrapUp(nals, vclNalUnitSeenInAU, isIdr);
                }
            }
            int i = unitHeader.nalUnitType;
            if (i != 39) {
                switch (i) {
                    case 32:
                        nal.position(2);
                        this.vps.add(nal.slice());
                        System.err.println("Stored VPS");
                        break;
                    case 33:
                        nal.position(2);
                        this.sps.add(nal.slice());
                        nal.position(1);
                        new SequenceParameterSetRbsp(Channels.newInputStream(new ByteBufferByteChannel(nal.slice())));
                        System.err.println("Stored SPS");
                        break;
                    case 34:
                        nal.position(2);
                        this.pps.add(nal.slice());
                        System.err.println("Stored PPS");
                        break;
                }
            } else {
                new SEIMessage(new BitReaderBuffer(nal.slice()));
            }
            switch (unitHeader.nalUnitType) {
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                    break;
                default:
                    PrintStream printStream = System.err;
                    printStream.println("Adding " + unitHeader.nalUnitType);
                    nals.add(nal);
                    break;
            }
            if (isVcl(unitHeader)) {
                int i2 = unitHeader.nalUnitType;
                if (i2 == 19 || i2 == 20) {
                    isIdr[0] = isIdr[0] & true;
                } else {
                    isIdr[0] = false;
                }
            }
            vclNalUnitSeenInAU[0] = vclNalUnitSeenInAU[0] | isVcl(unitHeader);
        }
    }

    private SampleDescriptionBox createSampleDescriptionBox() {
        this.stsd = new SampleDescriptionBox();
        VisualSampleEntry visualSampleEntry = new VisualSampleEntry(VisualSampleEntry.TYPE6);
        visualSampleEntry.setDataReferenceIndex(1);
        visualSampleEntry.setDepth(24);
        visualSampleEntry.setFrameCount(1);
        visualSampleEntry.setHorizresolution(72.0d);
        visualSampleEntry.setVertresolution(72.0d);
        visualSampleEntry.setWidth(UVCCamera.DEFAULT_PREVIEW_WIDTH);
        visualSampleEntry.setHeight(UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        visualSampleEntry.setCompressorname("HEVC Coding");
        HevcConfigurationBox hevcConfigurationBox = new HevcConfigurationBox();
        HevcDecoderConfigurationRecord.Array spsArray = new HevcDecoderConfigurationRecord.Array();
        spsArray.array_completeness = true;
        spsArray.nal_unit_type = 33;
        spsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it = this.sps.iterator();
        while (it.hasNext()) {
            spsArray.nalUnits.add(toArray(it.next()));
        }
        HevcDecoderConfigurationRecord.Array ppsArray = new HevcDecoderConfigurationRecord.Array();
        ppsArray.array_completeness = true;
        ppsArray.nal_unit_type = 34;
        ppsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it2 = this.pps.iterator();
        while (it2.hasNext()) {
            ppsArray.nalUnits.add(toArray(it2.next()));
        }
        HevcDecoderConfigurationRecord.Array vpsArray = new HevcDecoderConfigurationRecord.Array();
        vpsArray.array_completeness = true;
        vpsArray.nal_unit_type = 34;
        vpsArray.nalUnits = new ArrayList();
        Iterator<ByteBuffer> it3 = this.vps.iterator();
        while (it3.hasNext()) {
            vpsArray.nalUnits.add(toArray(it3.next()));
        }
        hevcConfigurationBox.getArrays().addAll(Arrays.asList(new HevcDecoderConfigurationRecord.Array[]{spsArray, vpsArray, ppsArray}));
        visualSampleEntry.addBox(hevcConfigurationBox);
        this.stsd.addBox(visualSampleEntry);
        return this.stsd;
    }

    public void wrapUp(List<ByteBuffer> nals, boolean[] vclNalUnitSeenInAU, boolean[] isIdr) {
        this.samples.add(createSampleObject(nals));
        PrintStream printStream = System.err;
        printStream.print("Create AU from " + nals.size() + " NALs");
        if (isIdr[0]) {
            System.err.println("  IDR");
        } else {
            System.err.println();
        }
        vclNalUnitSeenInAU[0] = false;
        isIdr[0] = true;
        nals.clear();
    }

    public SampleDescriptionBox getSampleDescriptionBox() {
        return null;
    }

    public String getHandler() {
        return "vide";
    }

    public List<Sample> getSamples() {
        return this.samples;
    }

    /* access modifiers changed from: package-private */
    public boolean isVcl(NalUnitHeader nalUnitHeader) {
        return nalUnitHeader.nalUnitType >= 0 && nalUnitHeader.nalUnitType <= 31;
    }

    public NalUnitHeader getNalUnitHeader(ByteBuffer nal) {
        nal.position(0);
        int nal_unit_header = IsoTypeReader.readUInt16(nal);
        NalUnitHeader nalUnitHeader = new NalUnitHeader();
        nalUnitHeader.forbiddenZeroFlag = (32768 & nal_unit_header) >> 15;
        nalUnitHeader.nalUnitType = (nal_unit_header & 32256) >> 9;
        nalUnitHeader.nuhLayerId = (nal_unit_header & UIMsg.d_ResultType.LOC_INFO_UPLOAD) >> 3;
        nalUnitHeader.nuhTemporalIdPlusOne = nal_unit_header & 7;
        return nalUnitHeader;
    }

    public static void main(String[] args) throws IOException {
        Track track = new H265TrackImpl(new FileDataSourceImpl("c:\\content\\test-UHD-HEVC_01_FMV_Med_track1.hvc"));
        Movie movie = new Movie();
        movie.addTrack(track);
        new DefaultMp4Builder().build(movie).writeContainer(new FileOutputStream("output.mp4").getChannel());
    }
}
