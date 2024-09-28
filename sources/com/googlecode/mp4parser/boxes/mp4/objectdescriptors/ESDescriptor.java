package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Descriptor(tags = {3})
public class ESDescriptor extends BaseDescriptor {
    private static Logger log = Logger.getLogger(ESDescriptor.class.getName());
    int URLFlag;
    int URLLength = 0;
    String URLString;
    DecoderConfigDescriptor decoderConfigDescriptor;
    int dependsOnEsId;
    int esId;
    int oCREsId;
    int oCRstreamFlag;
    List<BaseDescriptor> otherDescriptors = new ArrayList();
    int remoteODFlag;
    SLConfigDescriptor slConfigDescriptor;
    int streamDependenceFlag;
    int streamPriority;

    public void parseDetail(ByteBuffer bb) throws IOException {
        int baseSize;
        int baseSize2;
        this.esId = IsoTypeReader.readUInt16(bb);
        int data = IsoTypeReader.readUInt8(bb);
        int i = data >>> 7;
        this.streamDependenceFlag = i;
        this.URLFlag = (data >>> 6) & 1;
        this.oCRstreamFlag = (data >>> 5) & 1;
        this.streamPriority = data & 31;
        if (i == 1) {
            this.dependsOnEsId = IsoTypeReader.readUInt16(bb);
        }
        if (this.URLFlag == 1) {
            int readUInt8 = IsoTypeReader.readUInt8(bb);
            this.URLLength = readUInt8;
            this.URLString = IsoTypeReader.readString(bb, readUInt8);
        }
        if (this.oCRstreamFlag == 1) {
            this.oCREsId = IsoTypeReader.readUInt16(bb);
        }
        int i2 = 0;
        int sizeBytes = getSizeBytes() + 1 + 2 + 1 + (this.streamDependenceFlag == 1 ? 2 : 0) + (this.URLFlag == 1 ? this.URLLength + 1 : 0);
        if (this.oCRstreamFlag == 1) {
            i2 = 2;
        }
        int baseSize3 = sizeBytes + i2;
        int begin = bb.position();
        if (getSize() > baseSize3 + 2) {
            BaseDescriptor descriptor = ObjectDescriptorFactory.createFrom(-1, bb);
            long read = (long) (bb.position() - begin);
            Logger logger = log;
            StringBuilder sb = new StringBuilder();
            sb.append(descriptor);
            sb.append(" - ESDescriptor1 read: ");
            sb.append(read);
            sb.append(", size: ");
            sb.append(descriptor != null ? Integer.valueOf(descriptor.getSize()) : null);
            logger.finer(sb.toString());
            if (descriptor != null) {
                int size = descriptor.getSize();
                bb.position(begin + size);
                baseSize3 += size;
            } else {
                baseSize3 = (int) (((long) baseSize3) + read);
            }
            if (descriptor instanceof DecoderConfigDescriptor) {
                this.decoderConfigDescriptor = (DecoderConfigDescriptor) descriptor;
            }
        }
        int begin2 = bb.position();
        if (getSize() > baseSize3 + 2) {
            BaseDescriptor descriptor2 = ObjectDescriptorFactory.createFrom(-1, bb);
            long read2 = (long) (bb.position() - begin2);
            Logger logger2 = log;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(descriptor2);
            sb2.append(" - ESDescriptor2 read: ");
            sb2.append(read2);
            sb2.append(", size: ");
            sb2.append(descriptor2 != null ? Integer.valueOf(descriptor2.getSize()) : null);
            logger2.finer(sb2.toString());
            if (descriptor2 != null) {
                int size2 = descriptor2.getSize();
                bb.position(begin2 + size2);
                baseSize2 = baseSize3 + size2;
            } else {
                baseSize2 = (int) (((long) baseSize3) + read2);
            }
            if (descriptor2 instanceof SLConfigDescriptor) {
                this.slConfigDescriptor = (SLConfigDescriptor) descriptor2;
            }
        } else {
            log.warning("SLConfigDescriptor is missing!");
        }
        while (getSize() - baseSize3 > 2) {
            int begin3 = bb.position();
            BaseDescriptor descriptor3 = ObjectDescriptorFactory.createFrom(-1, bb);
            long read3 = (long) (bb.position() - begin3);
            Logger logger3 = log;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(descriptor3);
            sb3.append(" - ESDescriptor3 read: ");
            sb3.append(read3);
            sb3.append(", size: ");
            sb3.append(descriptor3 != null ? Integer.valueOf(descriptor3.getSize()) : null);
            logger3.finer(sb3.toString());
            if (descriptor3 != null) {
                int size3 = descriptor3.getSize();
                bb.position(begin3 + size3);
                baseSize = baseSize3 + size3;
            } else {
                baseSize = (int) (((long) baseSize3) + read3);
            }
            this.otherDescriptors.add(descriptor3);
        }
    }

    public int serializedSize() {
        int out = 5;
        if (this.streamDependenceFlag > 0) {
            out = 5 + 2;
        }
        if (this.URLFlag > 0) {
            out += this.URLLength + 1;
        }
        if (this.oCRstreamFlag > 0) {
            out += 2;
        }
        return out + this.decoderConfigDescriptor.serializedSize() + this.slConfigDescriptor.serializedSize();
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.allocate(serializedSize());
        IsoTypeWriter.writeUInt8(out, 3);
        IsoTypeWriter.writeUInt8(out, serializedSize() - 2);
        IsoTypeWriter.writeUInt16(out, this.esId);
        IsoTypeWriter.writeUInt8(out, (this.streamDependenceFlag << 7) | (this.URLFlag << 6) | (this.oCRstreamFlag << 5) | (this.streamPriority & 31));
        if (this.streamDependenceFlag > 0) {
            IsoTypeWriter.writeUInt16(out, this.dependsOnEsId);
        }
        if (this.URLFlag > 0) {
            IsoTypeWriter.writeUInt8(out, this.URLLength);
            IsoTypeWriter.writeUtf8String(out, this.URLString);
        }
        if (this.oCRstreamFlag > 0) {
            IsoTypeWriter.writeUInt16(out, this.oCREsId);
        }
        ByteBuffer dec = this.decoderConfigDescriptor.serialize();
        ByteBuffer sl = this.slConfigDescriptor.serialize();
        out.put(dec.array());
        out.put(sl.array());
        return out;
    }

    public DecoderConfigDescriptor getDecoderConfigDescriptor() {
        return this.decoderConfigDescriptor;
    }

    public SLConfigDescriptor getSlConfigDescriptor() {
        return this.slConfigDescriptor;
    }

    public void setDecoderConfigDescriptor(DecoderConfigDescriptor decoderConfigDescriptor2) {
        this.decoderConfigDescriptor = decoderConfigDescriptor2;
    }

    public void setSlConfigDescriptor(SLConfigDescriptor slConfigDescriptor2) {
        this.slConfigDescriptor = slConfigDescriptor2;
    }

    public List<BaseDescriptor> getOtherDescriptors() {
        return this.otherDescriptors;
    }

    public int getoCREsId() {
        return this.oCREsId;
    }

    public void setoCREsId(int oCREsId2) {
        this.oCREsId = oCREsId2;
    }

    public int getEsId() {
        return this.esId;
    }

    public void setEsId(int esId2) {
        this.esId = esId2;
    }

    public int getStreamDependenceFlag() {
        return this.streamDependenceFlag;
    }

    public void setStreamDependenceFlag(int streamDependenceFlag2) {
        this.streamDependenceFlag = streamDependenceFlag2;
    }

    public int getURLFlag() {
        return this.URLFlag;
    }

    public void setURLFlag(int URLFlag2) {
        this.URLFlag = URLFlag2;
    }

    public int getoCRstreamFlag() {
        return this.oCRstreamFlag;
    }

    public void setoCRstreamFlag(int oCRstreamFlag2) {
        this.oCRstreamFlag = oCRstreamFlag2;
    }

    public int getStreamPriority() {
        return this.streamPriority;
    }

    public void setStreamPriority(int streamPriority2) {
        this.streamPriority = streamPriority2;
    }

    public int getURLLength() {
        return this.URLLength;
    }

    public void setURLLength(int URLLength2) {
        this.URLLength = URLLength2;
    }

    public String getURLString() {
        return this.URLString;
    }

    public void setURLString(String URLString2) {
        this.URLString = URLString2;
    }

    public int getRemoteODFlag() {
        return this.remoteODFlag;
    }

    public void setRemoteODFlag(int remoteODFlag2) {
        this.remoteODFlag = remoteODFlag2;
    }

    public int getDependsOnEsId() {
        return this.dependsOnEsId;
    }

    public void setDependsOnEsId(int dependsOnEsId2) {
        this.dependsOnEsId = dependsOnEsId2;
    }

    public String toString() {
        return "ESDescriptor" + "{esId=" + this.esId + ", streamDependenceFlag=" + this.streamDependenceFlag + ", URLFlag=" + this.URLFlag + ", oCRstreamFlag=" + this.oCRstreamFlag + ", streamPriority=" + this.streamPriority + ", URLLength=" + this.URLLength + ", URLString='" + this.URLString + '\'' + ", remoteODFlag=" + this.remoteODFlag + ", dependsOnEsId=" + this.dependsOnEsId + ", oCREsId=" + this.oCREsId + ", decoderConfigDescriptor=" + this.decoderConfigDescriptor + ", slConfigDescriptor=" + this.slConfigDescriptor + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ESDescriptor that = (ESDescriptor) o;
        if (this.URLFlag != that.URLFlag || this.URLLength != that.URLLength || this.dependsOnEsId != that.dependsOnEsId || this.esId != that.esId || this.oCREsId != that.oCREsId || this.oCRstreamFlag != that.oCRstreamFlag || this.remoteODFlag != that.remoteODFlag || this.streamDependenceFlag != that.streamDependenceFlag || this.streamPriority != that.streamPriority) {
            return false;
        }
        String str = this.URLString;
        if (str == null ? that.URLString != null : !str.equals(that.URLString)) {
            return false;
        }
        DecoderConfigDescriptor decoderConfigDescriptor2 = this.decoderConfigDescriptor;
        if (decoderConfigDescriptor2 == null ? that.decoderConfigDescriptor != null : !decoderConfigDescriptor2.equals(that.decoderConfigDescriptor)) {
            return false;
        }
        List<BaseDescriptor> list = this.otherDescriptors;
        if (list == null ? that.otherDescriptors != null : !list.equals(that.otherDescriptors)) {
            return false;
        }
        SLConfigDescriptor sLConfigDescriptor = this.slConfigDescriptor;
        if (sLConfigDescriptor == null ? that.slConfigDescriptor == null : sLConfigDescriptor.equals(that.slConfigDescriptor)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = ((((((((((this.esId * 31) + this.streamDependenceFlag) * 31) + this.URLFlag) * 31) + this.oCRstreamFlag) * 31) + this.streamPriority) * 31) + this.URLLength) * 31;
        String str = this.URLString;
        int i = 0;
        int result2 = (((((((result + (str != null ? str.hashCode() : 0)) * 31) + this.remoteODFlag) * 31) + this.dependsOnEsId) * 31) + this.oCREsId) * 31;
        DecoderConfigDescriptor decoderConfigDescriptor2 = this.decoderConfigDescriptor;
        int result3 = (result2 + (decoderConfigDescriptor2 != null ? decoderConfigDescriptor2.hashCode() : 0)) * 31;
        SLConfigDescriptor sLConfigDescriptor = this.slConfigDescriptor;
        int result4 = (result3 + (sLConfigDescriptor != null ? sLConfigDescriptor.hashCode() : 0)) * 31;
        List<BaseDescriptor> list = this.otherDescriptors;
        if (list != null) {
            i = list.hashCode();
        }
        return result4 + i;
    }
}
