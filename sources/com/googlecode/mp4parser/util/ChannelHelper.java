package com.googlecode.mp4parser.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChannelHelper {
    private static ByteBuffer empty = ByteBuffer.allocate(0).asReadOnlyBuffer();

    public static void readFully(ReadableByteChannel channel, ByteBuffer buf) throws IOException {
        readFully(channel, buf, buf.remaining());
    }

    /* JADX WARNING: Removed duplicated region for block: B:6:0x0011 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0012  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int readFully(java.nio.channels.ReadableByteChannel r4, java.nio.ByteBuffer r5, int r6) throws java.io.IOException {
        /*
            r0 = 0
        L_0x0002:
            int r1 = r4.read(r5)
            r2 = r1
            r3 = -1
            if (r3 != r1) goto L_0x000b
            goto L_0x000f
        L_0x000b:
            int r0 = r0 + r2
            if (r0 != r6) goto L_0x001a
        L_0x000f:
            if (r2 == r3) goto L_0x0012
            return r0
        L_0x0012:
            java.io.EOFException r1 = new java.io.EOFException
            java.lang.String r3 = "End of file. No more boxes."
            r1.<init>(r3)
            throw r1
        L_0x001a:
            goto L_0x0002
        */
        throw new UnsupportedOperationException("Method not decompiled: com.googlecode.mp4parser.util.ChannelHelper.readFully(java.nio.channels.ReadableByteChannel, java.nio.ByteBuffer, int):int");
    }
}
