package com.mp4parser.iso14496.part15;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.authoring.tracks.CleanInputStream;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import com.googlecode.mp4parser.h264.model.PictureParameterSet;
import com.googlecode.mp4parser.h264.model.SeqParameterSet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AvcDecoderConfigurationRecord {
    public int avcLevelIndication;
    public int avcProfileIndication;
    public int bitDepthChromaMinus8 = 0;
    public int bitDepthChromaMinus8PaddingBits = 31;
    public int bitDepthLumaMinus8 = 0;
    public int bitDepthLumaMinus8PaddingBits = 31;
    public int chromaFormat = 1;
    public int chromaFormatPaddingBits = 31;
    public int configurationVersion;
    public boolean hasExts = true;
    public int lengthSizeMinusOne;
    public int lengthSizeMinusOnePaddingBits = 63;
    public int numberOfSequenceParameterSetsPaddingBits = 7;
    public List<byte[]> pictureParameterSets = new ArrayList();
    public int profileCompatibility;
    public List<byte[]> sequenceParameterSetExts = new ArrayList();
    public List<byte[]> sequenceParameterSets = new ArrayList();

    public AvcDecoderConfigurationRecord() {
    }

    public AvcDecoderConfigurationRecord(ByteBuffer content) {
        int i;
        this.configurationVersion = IsoTypeReader.readUInt8(content);
        this.avcProfileIndication = IsoTypeReader.readUInt8(content);
        this.profileCompatibility = IsoTypeReader.readUInt8(content);
        this.avcLevelIndication = IsoTypeReader.readUInt8(content);
        BitReaderBuffer brb = new BitReaderBuffer(content);
        this.lengthSizeMinusOnePaddingBits = brb.readBits(6);
        this.lengthSizeMinusOne = brb.readBits(2);
        this.numberOfSequenceParameterSetsPaddingBits = brb.readBits(3);
        int numberOfSeuqenceParameterSets = brb.readBits(5);
        for (int i2 = 0; i2 < numberOfSeuqenceParameterSets; i2++) {
            byte[] sequenceParameterSetNALUnit = new byte[IsoTypeReader.readUInt16(content)];
            content.get(sequenceParameterSetNALUnit);
            this.sequenceParameterSets.add(sequenceParameterSetNALUnit);
        }
        long numberOfPictureParameterSets = (long) IsoTypeReader.readUInt8(content);
        for (int i3 = 0; ((long) i3) < numberOfPictureParameterSets; i3++) {
            byte[] pictureParameterSetNALUnit = new byte[IsoTypeReader.readUInt16(content)];
            content.get(pictureParameterSetNALUnit);
            this.pictureParameterSets.add(pictureParameterSetNALUnit);
        }
        if (content.remaining() < 4) {
            this.hasExts = false;
        }
        if (!this.hasExts || !((i = this.avcProfileIndication) == 100 || i == 110 || i == 122 || i == 144)) {
            this.chromaFormat = -1;
            this.bitDepthLumaMinus8 = -1;
            this.bitDepthChromaMinus8 = -1;
            return;
        }
        BitReaderBuffer brb2 = new BitReaderBuffer(content);
        this.chromaFormatPaddingBits = brb2.readBits(6);
        this.chromaFormat = brb2.readBits(2);
        this.bitDepthLumaMinus8PaddingBits = brb2.readBits(5);
        this.bitDepthLumaMinus8 = brb2.readBits(3);
        this.bitDepthChromaMinus8PaddingBits = brb2.readBits(5);
        this.bitDepthChromaMinus8 = brb2.readBits(3);
        long numOfSequenceParameterSetExt = (long) IsoTypeReader.readUInt8(content);
        for (int i4 = 0; ((long) i4) < numOfSequenceParameterSetExt; i4++) {
            byte[] sequenceParameterSetExtNALUnit = new byte[IsoTypeReader.readUInt16(content)];
            content.get(sequenceParameterSetExtNALUnit);
            this.sequenceParameterSetExts.add(sequenceParameterSetExtNALUnit);
        }
        BitReaderBuffer bitReaderBuffer = brb2;
    }

    public void getContent(ByteBuffer byteBuffer) {
        IsoTypeWriter.writeUInt8(byteBuffer, this.configurationVersion);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcProfileIndication);
        IsoTypeWriter.writeUInt8(byteBuffer, this.profileCompatibility);
        IsoTypeWriter.writeUInt8(byteBuffer, this.avcLevelIndication);
        BitWriterBuffer bwb = new BitWriterBuffer(byteBuffer);
        bwb.writeBits(this.lengthSizeMinusOnePaddingBits, 6);
        bwb.writeBits(this.lengthSizeMinusOne, 2);
        bwb.writeBits(this.numberOfSequenceParameterSetsPaddingBits, 3);
        bwb.writeBits(this.pictureParameterSets.size(), 5);
        for (byte[] sequenceParameterSetNALUnit : this.sequenceParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, sequenceParameterSetNALUnit.length);
            byteBuffer.put(sequenceParameterSetNALUnit);
        }
        IsoTypeWriter.writeUInt8(byteBuffer, this.pictureParameterSets.size());
        for (byte[] pictureParameterSetNALUnit : this.pictureParameterSets) {
            IsoTypeWriter.writeUInt16(byteBuffer, pictureParameterSetNALUnit.length);
            byteBuffer.put(pictureParameterSetNALUnit);
        }
        if (this.hasExts) {
            int i = this.avcProfileIndication;
            if (i == 100 || i == 110 || i == 122 || i == 144) {
                BitWriterBuffer bwb2 = new BitWriterBuffer(byteBuffer);
                bwb2.writeBits(this.chromaFormatPaddingBits, 6);
                bwb2.writeBits(this.chromaFormat, 2);
                bwb2.writeBits(this.bitDepthLumaMinus8PaddingBits, 5);
                bwb2.writeBits(this.bitDepthLumaMinus8, 3);
                bwb2.writeBits(this.bitDepthChromaMinus8PaddingBits, 5);
                bwb2.writeBits(this.bitDepthChromaMinus8, 3);
                for (byte[] sequenceParameterSetExtNALUnit : this.sequenceParameterSetExts) {
                    IsoTypeWriter.writeUInt16(byteBuffer, sequenceParameterSetExtNALUnit.length);
                    byteBuffer.put(sequenceParameterSetExtNALUnit);
                }
                BitWriterBuffer bitWriterBuffer = bwb2;
            }
        }
    }

    public long getContentSize() {
        long size;
        int i;
        long size2 = 5 + 1;
        for (byte[] sequenceParameterSetNALUnit : this.sequenceParameterSets) {
            size2 = size2 + 2 + ((long) sequenceParameterSetNALUnit.length);
        }
        long size3 = size2 + 1;
        for (byte[] pictureParameterSetNALUnit : this.pictureParameterSets) {
            size3 = size + 2 + ((long) pictureParameterSetNALUnit.length);
        }
        if (this.hasExts && ((i = this.avcProfileIndication) == 100 || i == 110 || i == 122 || i == 144)) {
            size += 4;
            for (byte[] sequenceParameterSetExtNALUnit : this.sequenceParameterSetExts) {
                size = size + 2 + ((long) sequenceParameterSetExtNALUnit.length);
            }
        }
        return size;
    }

    public String[] getPPS() {
        ArrayList<String> l = new ArrayList<>();
        for (byte[] pictureParameterSet : this.pictureParameterSets) {
            try {
                l.add(PictureParameterSet.read((InputStream) new ByteArrayInputStream(pictureParameterSet, 1, pictureParameterSet.length - 1)).toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return (String[]) l.toArray(new String[l.size()]);
    }

    public String[] getSPS() {
        ArrayList<String> l = new ArrayList<>();
        for (byte[] sequenceParameterSet : this.sequenceParameterSets) {
            String detail = "not parsable";
            try {
                detail = SeqParameterSet.read(new CleanInputStream(new ByteArrayInputStream(sequenceParameterSet, 1, sequenceParameterSet.length - 1))).toString();
            } catch (IOException e) {
            }
            l.add(detail);
        }
        return (String[]) l.toArray(new String[l.size()]);
    }

    public List<String> getSequenceParameterSetsAsStrings() {
        List<String> result = new ArrayList<>(this.sequenceParameterSets.size());
        for (byte[] parameterSet : this.sequenceParameterSets) {
            result.add(Hex.encodeHex(parameterSet));
        }
        return result;
    }

    public List<String> getSequenceParameterSetExtsAsStrings() {
        List<String> result = new ArrayList<>(this.sequenceParameterSetExts.size());
        for (byte[] parameterSet : this.sequenceParameterSetExts) {
            result.add(Hex.encodeHex(parameterSet));
        }
        return result;
    }

    public List<String> getPictureParameterSetsAsStrings() {
        List<String> result = new ArrayList<>(this.pictureParameterSets.size());
        for (byte[] parameterSet : this.pictureParameterSets) {
            result.add(Hex.encodeHex(parameterSet));
        }
        return result;
    }

    public String toString() {
        return "AvcDecoderConfigurationRecord{configurationVersion=" + this.configurationVersion + ", avcProfileIndication=" + this.avcProfileIndication + ", profileCompatibility=" + this.profileCompatibility + ", avcLevelIndication=" + this.avcLevelIndication + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", hasExts=" + this.hasExts + ", chromaFormat=" + this.chromaFormat + ", bitDepthLumaMinus8=" + this.bitDepthLumaMinus8 + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", lengthSizeMinusOnePaddingBits=" + this.lengthSizeMinusOnePaddingBits + ", numberOfSequenceParameterSetsPaddingBits=" + this.numberOfSequenceParameterSetsPaddingBits + ", chromaFormatPaddingBits=" + this.chromaFormatPaddingBits + ", bitDepthLumaMinus8PaddingBits=" + this.bitDepthLumaMinus8PaddingBits + ", bitDepthChromaMinus8PaddingBits=" + this.bitDepthChromaMinus8PaddingBits + '}';
    }
}
