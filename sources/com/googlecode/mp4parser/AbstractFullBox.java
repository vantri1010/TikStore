package com.googlecode.mp4parser;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.FullBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public abstract class AbstractFullBox extends AbstractBox implements FullBox {
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private int flags;
    private int version;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AbstractFullBox.java", AbstractFullBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setVersion", "com.googlecode.mp4parser.AbstractFullBox", "int", "version", "", "void"), 51);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setFlags", "com.googlecode.mp4parser.AbstractFullBox", "int", "flags", "", "void"), 64);
    }

    protected AbstractFullBox(String type) {
        super(type);
    }

    protected AbstractFullBox(String type, byte[] userType) {
        super(type, userType);
    }

    @DoNotParseDetail
    public int getVersion() {
        if (!this.isParsed) {
            parseDetails();
        }
        return this.version;
    }

    public void setVersion(int version2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, (Object) this, (Object) this, Conversions.intObject(version2)));
        this.version = version2;
    }

    @DoNotParseDetail
    public int getFlags() {
        if (!this.isParsed) {
            parseDetails();
        }
        return this.flags;
    }

    public void setFlags(int flags2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(flags2)));
        this.flags = flags2;
    }

    /* access modifiers changed from: protected */
    public final long parseVersionAndFlags(ByteBuffer content) {
        this.version = IsoTypeReader.readUInt8(content);
        this.flags = IsoTypeReader.readUInt24(content);
        return 4;
    }

    /* access modifiers changed from: protected */
    public final void writeVersionAndFlags(ByteBuffer bb) {
        IsoTypeWriter.writeUInt8(bb, this.version);
        IsoTypeWriter.writeUInt24(bb, this.flags);
    }
}
