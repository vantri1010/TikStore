package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeWriterVariable {
    public static void write(long v, ByteBuffer bb, int bytes) {
        if (bytes == 1) {
            IsoTypeWriter.writeUInt8(bb, (int) (255 & v));
        } else if (bytes == 2) {
            IsoTypeWriter.writeUInt16(bb, (int) (65535 & v));
        } else if (bytes == 3) {
            IsoTypeWriter.writeUInt24(bb, (int) (16777215 & v));
        } else if (bytes == 4) {
            IsoTypeWriter.writeUInt32(bb, v);
        } else if (bytes == 8) {
            IsoTypeWriter.writeUInt64(bb, v);
        } else {
            throw new RuntimeException("I don't know how to read " + bytes + " bytes");
        }
    }
}
