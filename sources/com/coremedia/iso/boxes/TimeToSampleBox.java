package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class TimeToSampleBox extends AbstractFullBox {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final String TYPE = "stts";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    static Map<List<Entry>, SoftReference<long[]>> cache = new WeakHashMap();
    List<Entry> entries = Collections.emptyList();

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TimeToSampleBox.java", TimeToSampleBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntries", "com.coremedia.iso.boxes.TimeToSampleBox", "", "", "", "java.util.List"), 79);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setEntries", "com.coremedia.iso.boxes.TimeToSampleBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 83);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.TimeToSampleBox", "", "", "", "java.lang.String"), 87);
    }

    static {
        ajc$preClinit();
    }

    public TimeToSampleBox() {
        super(TYPE);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return (long) ((this.entries.size() * 8) + 8);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int entryCount = CastUtils.l2i(IsoTypeReader.readUInt32(content));
        this.entries = new ArrayList(entryCount);
        for (int i = 0; i < entryCount; i++) {
            this.entries.add(new Entry(IsoTypeReader.readUInt32(content), IsoTypeReader.readUInt32(content)));
        }
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.entries.size());
        for (Entry entry : this.entries) {
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getCount());
            IsoTypeWriter.writeUInt32(byteBuffer, entry.getDelta());
        }
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.entries;
    }

    public void setEntries(List<Entry> entries2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) entries2));
        this.entries = entries2;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TimeToSampleBox[entryCount=" + this.entries.size() + "]";
    }

    public static class Entry {
        long count;
        long delta;

        public Entry(long count2, long delta2) {
            this.count = count2;
            this.delta = delta2;
        }

        public long getCount() {
            return this.count;
        }

        public long getDelta() {
            return this.delta;
        }

        public void setCount(long count2) {
            this.count = count2;
        }

        public void setDelta(long delta2) {
            this.delta = delta2;
        }

        public String toString() {
            return "Entry{count=" + this.count + ", delta=" + this.delta + '}';
        }
    }

    public static synchronized long[] blowupTimeToSamples(List<Entry> entries2) {
        synchronized (TimeToSampleBox.class) {
            SoftReference softReference = cache.get(entries2);
            SoftReference cacheEntry = softReference;
            if (softReference != null) {
                long[] jArr = (long[]) cacheEntry.get();
                long[] cacheVal = jArr;
                if (jArr != null) {
                    return cacheVal;
                }
            }
            long numOfSamples = 0;
            for (Entry entry : entries2) {
                numOfSamples += entry.getCount();
            }
            long[] decodingTime = new long[((int) numOfSamples)];
            int current = 0;
            for (Entry entry2 : entries2) {
                int i = 0;
                while (((long) i) < entry2.getCount()) {
                    decodingTime[current] = entry2.getDelta();
                    i++;
                    current++;
                }
            }
            cache.put(entries2, new SoftReference(decodingTime));
            return decodingTime;
        }
    }
}
