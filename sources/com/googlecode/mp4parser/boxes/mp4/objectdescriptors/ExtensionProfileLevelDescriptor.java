package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags = {19})
public class ExtensionProfileLevelDescriptor extends BaseDescriptor {
    byte[] bytes;

    public void parseDetail(ByteBuffer bb) throws IOException {
        if (getSize() > 0) {
            byte[] bArr = new byte[getSize()];
            this.bytes = bArr;
            bb.get(bArr);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtensionDescriptor");
        sb.append("{bytes=");
        byte[] bArr = this.bytes;
        sb.append(bArr == null ? "null" : Hex.encodeHex(bArr));
        sb.append('}');
        return sb.toString();
    }
}
