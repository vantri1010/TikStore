package com.coremedia.iso.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class TrickPlayBox extends AbstractFullBox {
    public static final String TYPE = "trik";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private List<Entry> entries = new ArrayList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("TrickPlayBox.java", TrickPlayBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setEntries", "com.coremedia.iso.boxes.dece.TrickPlayBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 32);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntries", "com.coremedia.iso.boxes.dece.TrickPlayBox", "", "", "", "java.util.List"), 36);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.dece.TrickPlayBox", "", "", "", "java.lang.String"), 103);
    }

    public TrickPlayBox() {
        super(TYPE);
    }

    public void setEntries(List<Entry> entries2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, (Object) this, (Object) this, (Object) entries2));
        this.entries = entries2;
    }

    public List<Entry> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.entries;
    }

    public static class Entry {
        /* access modifiers changed from: private */
        public int value;

        public Entry() {
        }

        public Entry(int value2) {
            this.value = value2;
        }

        public int getPicType() {
            return (this.value >> 6) & 3;
        }

        public void setPicType(int picType) {
            int i = this.value & 31;
            this.value = i;
            this.value = i | ((picType & 3) << 6);
        }

        public int getDependencyLevel() {
            return this.value & 63;
        }

        public void setDependencyLevel(int dependencyLevel) {
            this.value = (dependencyLevel & 63) | this.value;
        }

        public String toString() {
            return "Entry" + "{picType=" + getPicType() + ",dependencyLevel=" + getDependencyLevel() + '}';
        }
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return (long) (this.entries.size() + 4);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        while (content.remaining() > 0) {
            this.entries.add(new Entry(IsoTypeReader.readUInt8(content)));
        }
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        for (Entry entry : this.entries) {
            IsoTypeWriter.writeUInt8(byteBuffer, entry.value);
        }
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return "TrickPlayBox" + "{entries=" + this.entries + '}';
    }
}
