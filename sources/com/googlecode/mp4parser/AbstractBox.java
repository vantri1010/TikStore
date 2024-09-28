package com.googlecode.mp4parser;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.Logger;
import com.googlecode.mp4parser.util.Path;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public abstract class AbstractBox implements Box {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static Logger LOG = Logger.getLogger(AbstractBox.class);
    private ByteBuffer content;
    long contentStartPosition;
    DataSource dataSource;
    private ByteBuffer deadBytes = null;
    boolean isParsed;
    boolean isRead;
    long memMapSize = -1;
    long offset;
    private Container parent;
    protected String type;
    private byte[] userType;

    /* access modifiers changed from: protected */
    public abstract void _parseDetails(ByteBuffer byteBuffer);

    /* access modifiers changed from: protected */
    public abstract void getContent(ByteBuffer byteBuffer);

    /* access modifiers changed from: protected */
    public abstract long getContentSize();

    private synchronized void readContent() {
        if (!this.isRead) {
            try {
                Logger logger = LOG;
                logger.logDebug("mem mapping " + getType());
                this.content = this.dataSource.map(this.contentStartPosition, this.memMapSize);
                this.isRead = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long getOffset() {
        return this.offset;
    }

    protected AbstractBox(String type2) {
        this.type = type2;
        this.isRead = true;
        this.isParsed = true;
    }

    protected AbstractBox(String type2, byte[] userType2) {
        this.type = type2;
        this.userType = userType2;
        this.isRead = true;
        this.isParsed = true;
    }

    @DoNotParseDetail
    public void parse(DataSource dataSource2, ByteBuffer header, long contentSize, BoxParser boxParser) throws IOException {
        long position = dataSource2.position();
        this.contentStartPosition = position;
        this.offset = position - ((long) header.remaining());
        this.memMapSize = contentSize;
        this.dataSource = dataSource2;
        dataSource2.position(dataSource2.position() + contentSize);
        this.isRead = false;
        this.isParsed = false;
    }

    public void getBox(WritableByteChannel os) throws IOException {
        int i = 8;
        int i2 = 0;
        int i3 = 16;
        if (!this.isRead) {
            if (!isSmallBox()) {
                i = 16;
            }
            if ("uuid".equals(getType())) {
                i2 = 16;
            }
            ByteBuffer header = ByteBuffer.allocate(i + i2);
            getHeader(header);
            os.write((ByteBuffer) header.rewind());
            this.dataSource.transferTo(this.contentStartPosition, this.memMapSize, os);
        } else if (this.isParsed) {
            ByteBuffer bb = ByteBuffer.allocate(CastUtils.l2i(getSize()));
            getHeader(bb);
            getContent(bb);
            ByteBuffer byteBuffer = this.deadBytes;
            if (byteBuffer != null) {
                byteBuffer.rewind();
                while (this.deadBytes.remaining() > 0) {
                    bb.put(this.deadBytes);
                }
            }
            os.write((ByteBuffer) bb.rewind());
        } else {
            if (!isSmallBox()) {
                i = 16;
            }
            if (!"uuid".equals(getType())) {
                i3 = 0;
            }
            ByteBuffer header2 = ByteBuffer.allocate(i + i3);
            getHeader(header2);
            os.write((ByteBuffer) header2.rewind());
            os.write((ByteBuffer) this.content.position(0));
        }
    }

    public final synchronized void parseDetails() {
        readContent();
        Logger logger = LOG;
        logger.logDebug("parsing details of " + getType());
        if (this.content != null) {
            ByteBuffer content2 = this.content;
            this.isParsed = true;
            content2.rewind();
            _parseDetails(content2);
            if (content2.remaining() > 0) {
                this.deadBytes = content2.slice();
            }
            this.content = null;
        }
    }

    /* access modifiers changed from: protected */
    public void setDeadBytes(ByteBuffer newDeadBytes) {
        this.deadBytes = newDeadBytes;
    }

    public long getSize() {
        long size;
        int i = 0;
        if (!this.isRead) {
            size = this.memMapSize;
        } else if (this.isParsed) {
            size = getContentSize();
        } else {
            ByteBuffer byteBuffer = this.content;
            size = (long) (byteBuffer != null ? byteBuffer.limit() : 0);
        }
        long size2 = size + ((long) ((size >= 4294967288L ? 8 : 0) + 8 + ("uuid".equals(getType()) ? 16 : 0)));
        ByteBuffer byteBuffer2 = this.deadBytes;
        if (byteBuffer2 != null) {
            i = byteBuffer2.limit();
        }
        return size2 + ((long) i);
    }

    @DoNotParseDetail
    public String getType() {
        return this.type;
    }

    @DoNotParseDetail
    public byte[] getUserType() {
        return this.userType;
    }

    @DoNotParseDetail
    public Container getParent() {
        return this.parent;
    }

    @DoNotParseDetail
    public void setParent(Container parent2) {
        this.parent = parent2;
    }

    public boolean isParsed() {
        return this.isParsed;
    }

    private boolean verify(ByteBuffer content2) {
        long contentSize = getContentSize();
        ByteBuffer byteBuffer = this.deadBytes;
        ByteBuffer bb = ByteBuffer.allocate(CastUtils.l2i(contentSize + ((long) (byteBuffer != null ? byteBuffer.limit() : 0))));
        getContent(bb);
        ByteBuffer byteBuffer2 = this.deadBytes;
        if (byteBuffer2 != null) {
            byteBuffer2.rewind();
            while (this.deadBytes.remaining() > 0) {
                bb.put(this.deadBytes);
            }
        }
        content2.rewind();
        bb.rewind();
        if (content2.remaining() != bb.remaining()) {
            System.err.print(String.valueOf(getType()) + ": remaining differs " + content2.remaining() + " vs. " + bb.remaining());
            LOG.logError(String.valueOf(getType()) + ": remaining differs " + content2.remaining() + " vs. " + bb.remaining());
            return false;
        }
        int p = content2.position();
        int i = content2.limit() - 1;
        int j = bb.limit() - 1;
        while (i >= p) {
            byte v1 = content2.get(i);
            byte v2 = bb.get(j);
            if (v1 != v2) {
                LOG.logError(String.format("%s: buffers differ at %d: %2X/%2X", new Object[]{getType(), Integer.valueOf(i), Byte.valueOf(v1), Byte.valueOf(v2)}));
                byte[] b1 = new byte[content2.remaining()];
                byte[] b2 = new byte[bb.remaining()];
                content2.get(b1);
                bb.get(b2);
                System.err.println("original      : " + Hex.encodeHex(b1, 4));
                System.err.println("reconstructed : " + Hex.encodeHex(b2, 4));
                return false;
            }
            i--;
            j--;
        }
        return true;
    }

    private boolean isSmallBox() {
        int baseSize = 8;
        if ("uuid".equals(getType())) {
            baseSize = 8 + 16;
        }
        if (!this.isRead) {
            return this.memMapSize + ((long) baseSize) < 4294967296L;
        }
        if (!this.isParsed) {
            return ((long) (this.content.limit() + baseSize)) < 4294967296L;
        }
        long contentSize = getContentSize();
        ByteBuffer byteBuffer = this.deadBytes;
        return (contentSize + ((long) (byteBuffer != null ? byteBuffer.limit() : 0))) + ((long) baseSize) < 4294967296L;
    }

    private void getHeader(ByteBuffer byteBuffer) {
        if (isSmallBox()) {
            IsoTypeWriter.writeUInt32(byteBuffer, getSize());
            byteBuffer.put(IsoFile.fourCCtoBytes(getType()));
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, 1);
            byteBuffer.put(IsoFile.fourCCtoBytes(getType()));
            IsoTypeWriter.writeUInt64(byteBuffer, getSize());
        }
        if ("uuid".equals(getType())) {
            byteBuffer.put(getUserType());
        }
    }

    @DoNotParseDetail
    public String getPath() {
        return Path.createPath(this);
    }
}
