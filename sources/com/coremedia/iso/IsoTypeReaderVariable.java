package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeReaderVariable {
    public static long read(ByteBuffer bb, int bytes) {
        if (bytes == 1) {
            return (long) IsoTypeReader.readUInt8(bb);
        }
        if (bytes == 2) {
            return (long) IsoTypeReader.readUInt16(bb);
        }
        if (bytes == 3) {
            return (long) IsoTypeReader.readUInt24(bb);
        }
        if (bytes == 4) {
            return IsoTypeReader.readUInt32(bb);
        }
        if (bytes == 8) {
            return IsoTypeReader.readUInt64(bb);
        }
        throw new RuntimeException("I don't know how to read " + bytes + " bytes");
    }
}
