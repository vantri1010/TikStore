package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags = {20})
public class ProfileLevelIndicationDescriptor extends BaseDescriptor {
    int profileLevelIndicationIndex;

    public void parseDetail(ByteBuffer bb) throws IOException {
        this.profileLevelIndicationIndex = IsoTypeReader.readUInt8(bb);
    }

    public String toString() {
        return "ProfileLevelIndicationDescriptor" + "{profileLevelIndicationIndex=" + Integer.toHexString(this.profileLevelIndicationIndex) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && getClass() == o.getClass() && this.profileLevelIndicationIndex == ((ProfileLevelIndicationDescriptor) o).profileLevelIndicationIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.profileLevelIndicationIndex;
    }
}
