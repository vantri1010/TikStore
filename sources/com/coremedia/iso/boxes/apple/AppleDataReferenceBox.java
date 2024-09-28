package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class AppleDataReferenceBox extends AbstractFullBox {
    public static final String TYPE = "rdrf";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private String dataReference;
    private int dataReferenceSize;
    private String dataReferenceType;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AppleDataReferenceBox.java", AppleDataReferenceBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getDataReferenceSize", "com.coremedia.iso.boxes.apple.AppleDataReferenceBox", "", "", "", "long"), 63);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getDataReferenceType", "com.coremedia.iso.boxes.apple.AppleDataReferenceBox", "", "", "", "java.lang.String"), 67);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getDataReference", "com.coremedia.iso.boxes.apple.AppleDataReferenceBox", "", "", "", "java.lang.String"), 71);
    }

    public AppleDataReferenceBox() {
        super(TYPE);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return (long) (this.dataReferenceSize + 12);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.dataReferenceType = IsoTypeReader.read4cc(content);
        int l2i = CastUtils.l2i(IsoTypeReader.readUInt32(content));
        this.dataReferenceSize = l2i;
        this.dataReference = IsoTypeReader.readString(content, l2i);
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(IsoFile.fourCCtoBytes(this.dataReferenceType));
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.dataReferenceSize);
        byteBuffer.put(Utf8.convert(this.dataReference));
    }

    public long getDataReferenceSize() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return (long) this.dataReferenceSize;
    }

    public String getDataReferenceType() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.dataReferenceType;
    }

    public String getDataReference() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.dataReference;
    }
}
