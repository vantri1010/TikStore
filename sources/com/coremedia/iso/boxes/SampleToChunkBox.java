package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class SampleToChunkBox extends AbstractFullBox {
    public static final String TYPE = "stsc";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    List<Entry> entries = Collections.emptyList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("SampleToChunkBox.java", SampleToChunkBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntries", "com.coremedia.iso.boxes.SampleToChunkBox", "", "", "", "java.util.List"), 47);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setEntries", "com.coremedia.iso.boxes.SampleToChunkBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 51);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.SampleToChunkBox", "", "", "", "java.lang.String"), 84);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "blowup", "com.coremedia.iso.boxes.SampleToChunkBox", "int", "chunkCount", "", "[J"), 95);
    }

    public SampleToChunkBox() {
        super(TYPE);
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.entries;
    }

    public void setEntries(List<Entry> entries2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) entries2));
        this.entries = entries2;
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return (long) ((this.entries.size() * 12) + 8);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int entryCount = CastUtils.l2i(IsoTypeReader.readUInt32(content));
        this.entries = new ArrayList(entryCount);
        for (int i = 0; i < entryCount; i++) {
            this.entries.add(new Entry(IsoTypeReader.readUInt32(content), IsoTypeReader.readUInt32(content), IsoTypeReader.readUInt32(content)));
        }
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.entries.size());
        for (Entry entry : this.entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getFirstChunk());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getSamplesPerChunk());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getSampleDescriptionIndex());
        }
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "SampleToChunkBox[entryCount=" + this.entries.size() + "]";
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: com.coremedia.iso.boxes.SampleToChunkBox$Entry} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long[] blowup(int r11) {
        /*
            r10 = this;
            org.aspectj.lang.JoinPoint$StaticPart r0 = ajc$tjp_3
            java.lang.Object r1 = org.aspectj.runtime.internal.Conversions.intObject(r11)
            org.aspectj.lang.JoinPoint r0 = org.aspectj.runtime.reflect.Factory.makeJP((org.aspectj.lang.JoinPoint.StaticPart) r0, (java.lang.Object) r10, (java.lang.Object) r10, (java.lang.Object) r1)
            com.googlecode.mp4parser.RequiresParseDetailAspect r1 = com.googlecode.mp4parser.RequiresParseDetailAspect.aspectOf()
            r1.before(r0)
            long[] r0 = new long[r11]
            java.util.LinkedList r1 = new java.util.LinkedList
            java.util.List<com.coremedia.iso.boxes.SampleToChunkBox$Entry> r2 = r10.entries
            r1.<init>(r2)
            java.util.Collections.reverse(r1)
            java.util.Iterator r2 = r1.iterator()
            java.lang.Object r3 = r2.next()
            com.coremedia.iso.boxes.SampleToChunkBox$Entry r3 = (com.coremedia.iso.boxes.SampleToChunkBox.Entry) r3
            int r4 = r0.length
        L_0x0029:
            r5 = 1
            if (r4 > r5) goto L_0x0034
            r4 = 0
            long r5 = r3.getSamplesPerChunk()
            r0[r4] = r5
            return r0
        L_0x0034:
            int r5 = r4 + -1
            long r6 = r3.getSamplesPerChunk()
            r0[r5] = r6
            long r5 = (long) r4
            long r7 = r3.getFirstChunk()
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x004c
            java.lang.Object r5 = r2.next()
            r3 = r5
            com.coremedia.iso.boxes.SampleToChunkBox$Entry r3 = (com.coremedia.iso.boxes.SampleToChunkBox.Entry) r3
        L_0x004c:
            int r4 = r4 + -1
            goto L_0x0029
        */
        throw new UnsupportedOperationException("Method not decompiled: com.coremedia.iso.boxes.SampleToChunkBox.blowup(int):long[]");
    }

    public static class Entry {
        long firstChunk;
        long sampleDescriptionIndex;
        long samplesPerChunk;

        public Entry(long firstChunk2, long samplesPerChunk2, long sampleDescriptionIndex2) {
            this.firstChunk = firstChunk2;
            this.samplesPerChunk = samplesPerChunk2;
            this.sampleDescriptionIndex = sampleDescriptionIndex2;
        }

        public long getFirstChunk() {
            return this.firstChunk;
        }

        public void setFirstChunk(long firstChunk2) {
            this.firstChunk = firstChunk2;
        }

        public long getSamplesPerChunk() {
            return this.samplesPerChunk;
        }

        public void setSamplesPerChunk(long samplesPerChunk2) {
            this.samplesPerChunk = samplesPerChunk2;
        }

        public long getSampleDescriptionIndex() {
            return this.sampleDescriptionIndex;
        }

        public void setSampleDescriptionIndex(long sampleDescriptionIndex2) {
            this.sampleDescriptionIndex = sampleDescriptionIndex2;
        }

        public String toString() {
            return "Entry{firstChunk=" + this.firstChunk + ", samplesPerChunk=" + this.samplesPerChunk + ", sampleDescriptionIndex=" + this.sampleDescriptionIndex + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Entry entry = (Entry) o;
            if (this.firstChunk == entry.firstChunk && this.sampleDescriptionIndex == entry.sampleDescriptionIndex && this.samplesPerChunk == entry.samplesPerChunk) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            long j = this.firstChunk;
            long j2 = this.samplesPerChunk;
            long j3 = this.sampleDescriptionIndex;
            return (((((int) (j ^ (j >>> 32))) * 31) + ((int) (j2 ^ (j2 >>> 32)))) * 31) + ((int) (j3 ^ (j3 >>> 32)));
        }
    }
}
