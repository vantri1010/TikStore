package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public abstract class AbstractBoxParser implements BoxParser {
    private static Logger LOG = Logger.getLogger(AbstractBoxParser.class.getName());
    ThreadLocal<ByteBuffer> header = new ThreadLocal<ByteBuffer>() {
        /* access modifiers changed from: protected */
        public ByteBuffer initialValue() {
            return ByteBuffer.allocate(32);
        }
    };

    public abstract Box createBox(String str, byte[] bArr, String str2);

    public Box parseBox(DataSource byteChannel, Container parent) throws IOException {
        long contentSize;
        long contentSize2;
        byte[] usertype;
        DataSource dataSource = byteChannel;
        Container container = parent;
        long startPos = byteChannel.position();
        this.header.get().rewind().limit(8);
        while (true) {
            int read = dataSource.read(this.header.get());
            int b = read;
            if (read == 8) {
                this.header.get().rewind();
                long size = IsoTypeReader.readUInt32(this.header.get());
                if (size >= 8 || size <= 1) {
                    String type = IsoTypeReader.read4cc(this.header.get());
                    if (size == 1) {
                        this.header.get().limit(16);
                        dataSource.read(this.header.get());
                        this.header.get().position(8);
                        long size2 = IsoTypeReader.readUInt64(this.header.get());
                        contentSize = size2 - 16;
                        long j = size2;
                    } else if (size == 0) {
                        contentSize = byteChannel.size() - byteChannel.position();
                        long j2 = contentSize + 8;
                    } else {
                        contentSize = size - 8;
                        long j3 = size;
                    }
                    if ("uuid".equals(type)) {
                        this.header.get().limit(this.header.get().limit() + 16);
                        dataSource.read(this.header.get());
                        byte[] usertype2 = new byte[16];
                        for (int i = this.header.get().position() - 16; i < this.header.get().position(); i++) {
                            usertype2[i - (this.header.get().position() - 16)] = this.header.get().get(i);
                        }
                        usertype = usertype2;
                        contentSize2 = contentSize - 16;
                    } else {
                        usertype = null;
                        contentSize2 = contentSize;
                    }
                    Box box = createBox(type, usertype, container instanceof Box ? ((Box) container).getType() : "");
                    box.setParent(container);
                    this.header.get().rewind();
                    box.parse(byteChannel, this.header.get(), contentSize2, this);
                    return box;
                }
                LOG.severe("Plausibility check failed: size < 8 (size = " + size + "). Stop parsing!");
                return null;
            } else if (b >= 0) {
                int i2 = b;
            } else {
                dataSource.position(startPos);
                throw new EOFException();
            }
        }
    }
}
