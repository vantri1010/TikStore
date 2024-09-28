package com.google.android.exoplayer2.metadata.emsg;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataDecoder;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class EventMessageDecoder implements MetadataDecoder {
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        byte[] data = buffer.array();
        int size = buffer.limit();
        ParsableByteArray emsgData = new ParsableByteArray(data, size);
        long readUnsignedInt = emsgData.readUnsignedInt();
        long presentationTimeUs = Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), 1000000, readUnsignedInt);
        ByteBuffer byteBuffer = buffer;
        return new Metadata(new EventMessage((String) Assertions.checkNotNull(emsgData.readNullTerminatedString()), (String) Assertions.checkNotNull(emsgData.readNullTerminatedString()), Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), 1000, readUnsignedInt), emsgData.readUnsignedInt(), Arrays.copyOfRange(data, emsgData.getPosition(), size), presentationTimeUs));
    }
}
