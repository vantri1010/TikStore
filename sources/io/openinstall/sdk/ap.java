package io.openinstall.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ap {
    private static int a(FileChannel fileChannel, long j, ByteBuffer byteBuffer) throws IOException {
        int read;
        int i = 0;
        while (byteBuffer.hasRemaining() && (read = fileChannel.read(byteBuffer, j)) != -1) {
            j += (long) read;
            i += read;
        }
        return i;
    }

    private static int a(FileChannel fileChannel, long j, byte[] bArr, int i, int i2) throws IOException {
        ByteBuffer wrap = ByteBuffer.wrap(bArr, i, i2);
        int i3 = 0;
        while (i3 < i2) {
            int read = fileChannel.read(wrap, ((long) i3) + j);
            if (read == -1) {
                break;
            }
            i3 += read;
        }
        return i3;
    }

    public static ao a(FileChannel fileChannel) throws IOException {
        at b = b(fileChannel);
        if (b == null) {
            return null;
        }
        if (b.f < 32) {
            return new ao(b);
        }
        byte[] bArr = new byte[24];
        a(fileChannel, b.f - ((long) 24), bArr, 0, 24);
        long c = aq.c(bArr, 0, ByteOrder.LITTLE_ENDIAN);
        long c2 = aq.c(bArr, 8, ByteOrder.LITTLE_ENDIAN);
        long c3 = aq.c(bArr, 16, ByteOrder.LITTLE_ENDIAN);
        if (c2 != 2334950737559900225L || c3 != 3617552046287187010L) {
            return new ao(b);
        }
        int i = (int) (8 + c);
        long j = (long) i;
        long j2 = b.f - j;
        if (i < 32 || j2 < 0) {
            return new ao(b);
        }
        if (j > 20971520) {
            return new ao(b);
        }
        ByteBuffer allocate = ByteBuffer.allocate(i - 24);
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        if (a(fileChannel, j2, allocate) != allocate.capacity() || ((ByteBuffer) allocate.flip()).getLong() != c) {
            return new ao(b);
        }
        as asVar = new as(j2);
        while (allocate.remaining() >= 12) {
            long j3 = allocate.getLong();
            int i2 = allocate.getInt();
            int i3 = (int) (j3 - 4);
            if (i3 < 0 || i3 > allocate.remaining()) {
                break;
            }
            byte[] bArr2 = new byte[i3];
            allocate.get(bArr2, 0, i3);
            asVar.a(i2, bArr2);
        }
        return new ao(asVar, b);
    }

    private static void a(FileChannel fileChannel, FileChannel fileChannel2, long j, long j2) throws IOException {
        while (j2 > 0) {
            long transferTo = fileChannel.transferTo(j, j2, fileChannel2);
            j += transferTo;
            j2 -= transferTo;
        }
    }

    public static void a(byte[] bArr, File file, File file2) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        try {
            FileChannel channel = fileInputStream.getChannel();
            FileChannel channel2 = fileOutputStream.getChannel();
            ao a = a(channel);
            channel.position(0);
            if (a == null) {
                a(channel, channel2, 0, channel.size());
                return;
            }
            a.a(bArr);
            as b = a.b();
            at a2 = a.a();
            if (b != null) {
                a(channel, channel2, 0, b.b());
                for (ByteBuffer byteBuffer : b.e()) {
                    while (byteBuffer.hasRemaining()) {
                        channel2.write(byteBuffer);
                    }
                }
                a(channel, channel2, a2.f, a2.h - a2.f);
            } else {
                a(channel, channel2, 0, a2.h);
            }
            ByteBuffer a3 = a2.a(b != null ? b.a() : a2.f);
            while (a3.hasRemaining()) {
                channel2.write(a3);
            }
            fileInputStream.close();
            fileOutputStream.close();
        } finally {
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    private static at b(FileChannel fileChannel) throws IOException {
        int i;
        long j;
        byte[] bArr = new byte[128];
        long size = fileChannel.size();
        at atVar = null;
        long j2 = 22;
        if (size < 22) {
            return null;
        }
        long j3 = 0;
        long j4 = (long) 106;
        long max = Math.max(0, (size > 65557 ? size - 65557 : 0) - j4);
        long j5 = size - ((long) 128);
        while (j5 >= max) {
            int i2 = (j5 > j3 ? 1 : (j5 == j3 ? 0 : -1));
            if (i2 < 0) {
                i = (int) (-j5);
                Arrays.fill(bArr, 0, i, (byte) 0);
            } else {
                i = 0;
            }
            long j6 = j5;
            long j7 = j4;
            a(fileChannel, i2 < 0 ? 0 : j5, bArr, i, 128 - i);
            int i3 = 106;
            while (i3 >= 0) {
                if (bArr[i3 + 0] == 80 && bArr[i3 + 1] == 75 && bArr[i3 + 2] == 5 && bArr[i3 + 3] == 6) {
                    int b = aq.b(bArr, i3 + 20, ByteOrder.LITTLE_ENDIAN) & 65535;
                    long j8 = j6 + ((long) i3);
                    if (j8 + j2 + ((long) b) == size) {
                        at atVar2 = new at();
                        atVar2.h = j8;
                        atVar2.a = aq.b(bArr, i3 + 4, ByteOrder.LITTLE_ENDIAN) & 65535;
                        atVar2.b = aq.b(bArr, i3 + 6, ByteOrder.LITTLE_ENDIAN) & 65535;
                        atVar2.c = aq.b(bArr, i3 + 8, ByteOrder.LITTLE_ENDIAN) & 65535;
                        atVar2.d = 65535 & aq.b(bArr, i3 + 10, ByteOrder.LITTLE_ENDIAN);
                        atVar2.e = ((long) aq.a(bArr, i3 + 12, ByteOrder.LITTLE_ENDIAN)) & 4294967295L;
                        atVar2.f = ((long) aq.a(bArr, i3 + 16, ByteOrder.LITTLE_ENDIAN)) & 4294967295L;
                        if (b > 0) {
                            atVar2.g = new byte[b];
                            a(fileChannel, atVar2.h + 22, atVar2.g, 0, b);
                        }
                        return atVar2;
                    }
                    j = 22;
                } else {
                    j = j2;
                }
                i3--;
                j2 = j;
            }
            j5 = j6 - j7;
            j2 = j2;
            j4 = j7;
            atVar = null;
            j3 = 0;
        }
        return atVar;
    }
}
