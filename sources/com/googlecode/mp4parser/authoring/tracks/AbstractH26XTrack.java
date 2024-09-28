package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractH26XTrack extends AbstractTrack {
    static int BUFFER = 67107840;
    protected List<CompositionTimeToSample.Entry> ctts = new ArrayList();
    private DataSource dataSource;
    protected long[] decodingTimes;
    protected List<SampleDependencyTypeBox.Entry> sdtp = new ArrayList();
    protected List<Integer> stss = new ArrayList();
    TrackMetaData trackMetaData = new TrackMetaData();

    public TrackMetaData getTrackMetaData() {
        return this.trackMetaData;
    }

    public AbstractH26XTrack(DataSource dataSource2) {
        super(dataSource2.toString());
        this.dataSource = dataSource2;
    }

    public static class LookAhead {
        ByteBuffer buffer;
        long bufferStartPos = 0;
        DataSource dataSource;
        int inBufferPos = 0;
        long start;

        public void fillBuffer() throws IOException {
            DataSource dataSource2 = this.dataSource;
            this.buffer = dataSource2.map(this.bufferStartPos, Math.min(dataSource2.size() - this.bufferStartPos, (long) AbstractH26XTrack.BUFFER));
        }

        public LookAhead(DataSource dataSource2) throws IOException {
            this.dataSource = dataSource2;
            fillBuffer();
        }

        public boolean nextThreeEquals001() throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 3) {
                if (this.buffer.get(i) == 0 && this.buffer.get(this.inBufferPos + 1) == 0 && this.buffer.get(this.inBufferPos + 2) == 1) {
                    return true;
                }
                return false;
            } else if (this.bufferStartPos + ((long) i) + 3 < this.dataSource.size()) {
                return false;
            } else {
                throw new EOFException();
            }
        }

        public boolean nextThreeEquals000or001orEof() throws IOException {
            int limit = this.buffer.limit();
            int i = this.inBufferPos;
            if (limit - i >= 3) {
                return this.buffer.get(i) == 0 && this.buffer.get(this.inBufferPos + 1) == 0 && (this.buffer.get(this.inBufferPos + 2) == 0 || this.buffer.get(this.inBufferPos + 2) == 1);
            }
            if (this.bufferStartPos + ((long) i) + 3 > this.dataSource.size()) {
                return this.bufferStartPos + ((long) this.inBufferPos) == this.dataSource.size();
            }
            this.bufferStartPos = this.start;
            this.inBufferPos = 0;
            fillBuffer();
            return nextThreeEquals000or001orEof();
        }

        public void discardByte() {
            this.inBufferPos++;
        }

        public void discardNext3AndMarkStart() {
            int i = this.inBufferPos + 3;
            this.inBufferPos = i;
            this.start = this.bufferStartPos + ((long) i);
        }

        public ByteBuffer getNal() {
            long j = this.start;
            long j2 = this.bufferStartPos;
            if (j >= j2) {
                this.buffer.position((int) (j - j2));
                ByteBuffer slice = this.buffer.slice();
                slice.limit((int) (((long) this.inBufferPos) - (this.start - this.bufferStartPos)));
                return slice;
            }
            throw new RuntimeException("damn! NAL exceeds buffer");
        }
    }

    /* access modifiers changed from: protected */
    public ByteBuffer findNextNal(LookAhead la) throws IOException {
        while (!la.nextThreeEquals001()) {
            try {
                la.discardByte();
            } catch (EOFException e) {
                return null;
            }
        }
        la.discardNext3AndMarkStart();
        while (!la.nextThreeEquals000or001orEof()) {
            la.discardByte();
        }
        return la.getNal();
    }

    /* access modifiers changed from: protected */
    public Sample createSampleObject(List<? extends ByteBuffer> nals) {
        byte[] sizeInfo = new byte[(nals.size() * 4)];
        ByteBuffer sizeBuf = ByteBuffer.wrap(sizeInfo);
        for (ByteBuffer b : nals) {
            sizeBuf.putInt(b.remaining());
        }
        ByteBuffer[] data = new ByteBuffer[(nals.size() * 2)];
        for (int i = 0; i < nals.size(); i++) {
            data[i * 2] = ByteBuffer.wrap(sizeInfo, i * 4, 4);
            data[(i * 2) + 1] = (ByteBuffer) nals.get(i);
        }
        return new SampleImpl(data);
    }

    public long[] getSampleDurations() {
        return this.decodingTimes;
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.ctts;
    }

    public long[] getSyncSamples() {
        long[] returns = new long[this.stss.size()];
        for (int i = 0; i < this.stss.size(); i++) {
            returns[i] = (long) this.stss.get(i).intValue();
        }
        return returns;
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.sdtp;
    }

    static InputStream cleanBuffer(InputStream is) {
        return new CleanInputStream(is);
    }

    protected static byte[] toArray(ByteBuffer buf) {
        ByteBuffer buf2 = buf.duplicate();
        byte[] b = new byte[buf2.remaining()];
        buf2.get(b, 0, b.length);
        return b;
    }

    public void close() throws IOException {
        this.dataSource.close();
    }
}
