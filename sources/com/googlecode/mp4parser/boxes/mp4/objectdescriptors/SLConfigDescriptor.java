package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags = {6})
public class SLConfigDescriptor extends BaseDescriptor {
    int predefined;

    public int getPredefined() {
        return this.predefined;
    }

    public void setPredefined(int predefined2) {
        this.predefined = predefined2;
    }

    public void parseDetail(ByteBuffer bb) throws IOException {
        this.predefined = IsoTypeReader.readUInt8(bb);
    }

    public int serializedSize() {
        return 3;
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.allocate(3);
        IsoTypeWriter.writeUInt8(out, 6);
        IsoTypeWriter.writeUInt8(out, 1);
        IsoTypeWriter.writeUInt8(out, this.predefined);
        return out;
    }

    public String toString() {
        return "SLConfigDescriptor" + "{predefined=" + this.predefined + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass() && this.predefined == ((SLConfigDescriptor) o).predefined) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.predefined;
    }
}
