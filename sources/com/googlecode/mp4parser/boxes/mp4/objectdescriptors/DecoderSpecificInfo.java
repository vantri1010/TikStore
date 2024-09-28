package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Descriptor(tags = {5})
public class DecoderSpecificInfo extends BaseDescriptor {
    byte[] bytes;

    public void parseDetail(ByteBuffer bb) throws IOException {
        if (this.sizeOfInstance > 0) {
            byte[] bArr = new byte[this.sizeOfInstance];
            this.bytes = bArr;
            bb.get(bArr);
        }
    }

    public int serializedSize() {
        return this.bytes.length;
    }

    public ByteBuffer serialize() {
        return ByteBuffer.wrap(this.bytes);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DecoderSpecificInfo");
        sb.append("{bytes=");
        byte[] bArr = this.bytes;
        sb.append(bArr == null ? "null" : Hex.encodeHex(bArr));
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass() || !Arrays.equals(this.bytes, ((DecoderSpecificInfo) o).bytes)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        byte[] bArr = this.bytes;
        if (bArr != null) {
            return Arrays.hashCode(bArr);
        }
        return 0;
    }
}
