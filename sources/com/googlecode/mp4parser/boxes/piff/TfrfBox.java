package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.ByteCompanionObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class TfrfBox extends AbstractFullBox {
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    public List<Entry> entries = new ArrayList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TfrfBox.java", TfrfBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getFragmentCount", "com.googlecode.mp4parser.boxes.piff.TfrfBox", "", "", "", "long"), 91);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntries", "com.googlecode.mp4parser.boxes.piff.TfrfBox", "", "", "", "java.util.List"), 95);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.googlecode.mp4parser.boxes.piff.TfrfBox", "", "", "", "java.lang.String"), 100);
    }

    public TfrfBox() {
        super("uuid");
    }

    public byte[] getUserType() {
        return new byte[]{-44, ByteCompanionObject.MIN_VALUE, 126, -14, -54, 57, 70, -107, -114, 84, 38, -53, -98, 70, -89, -97};
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return (long) ((this.entries.size() * (getVersion() == 1 ? 16 : 8)) + 5);
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt8(byteBuffer, this.entries.size());
        for (Entry entry : this.entries) {
            if (getVersion() == 1) {
                IsoTypeWriter.writeUInt64(byteBuffer, entry.fragmentAbsoluteTime);
                IsoTypeWriter.writeUInt64(byteBuffer, entry.fragmentAbsoluteDuration);
            } else {
                IsoTypeWriter.writeUInt32(byteBuffer, entry.fragmentAbsoluteTime);
                IsoTypeWriter.writeUInt32(byteBuffer, entry.fragmentAbsoluteDuration);
            }
        }
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        int fragmentCount = IsoTypeReader.readUInt8(content);
        for (int i = 0; i < fragmentCount; i++) {
            Entry entry = new Entry();
            if (getVersion() == 1) {
                entry.fragmentAbsoluteTime = IsoTypeReader.readUInt64(content);
                entry.fragmentAbsoluteDuration = IsoTypeReader.readUInt64(content);
            } else {
                entry.fragmentAbsoluteTime = IsoTypeReader.readUInt32(content);
                entry.fragmentAbsoluteDuration = IsoTypeReader.readUInt32(content);
            }
            this.entries.add(entry);
        }
    }

    public long getFragmentCount() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return (long) this.entries.size();
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.entries;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TfrfBox" + "{entries=" + this.entries + '}';
    }

    public class Entry {
        long fragmentAbsoluteDuration;
        long fragmentAbsoluteTime;

        public Entry() {
        }

        public long getFragmentAbsoluteTime() {
            return this.fragmentAbsoluteTime;
        }

        public long getFragmentAbsoluteDuration() {
            return this.fragmentAbsoluteDuration;
        }

        public String toString() {
            return "Entry" + "{fragmentAbsoluteTime=" + this.fragmentAbsoluteTime + ", fragmentAbsoluteDuration=" + this.fragmentAbsoluteDuration + '}';
        }
    }
}
