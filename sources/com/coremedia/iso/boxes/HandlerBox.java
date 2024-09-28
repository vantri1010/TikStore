package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class HandlerBox extends AbstractFullBox {
    public static final String TYPE = "hdlr";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    public static final Map<String, String> readableTypes;
    private long a;
    private long b;
    private long c;
    private String handlerType;
    private String name = null;
    private long shouldBeZeroButAppleWritesHereSomeValue;
    private boolean zeroTerm = true;

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("HandlerBox.java", HandlerBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getHandlerType", "com.coremedia.iso.boxes.HandlerBox", "", "", "", "java.lang.String"), 78);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setName", "com.coremedia.iso.boxes.HandlerBox", "java.lang.String", "name", "", "void"), 87);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setHandlerType", "com.coremedia.iso.boxes.HandlerBox", "java.lang.String", "handlerType", "", "void"), 91);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getName", "com.coremedia.iso.boxes.HandlerBox", "", "", "", "java.lang.String"), 95);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getHumanReadableTrackType", "com.coremedia.iso.boxes.HandlerBox", "", "", "", "java.lang.String"), 99);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.HandlerBox", "", "", "", "java.lang.String"), 149);
    }

    static {
        ajc$preClinit();
        HashMap hm = new HashMap();
        hm.put("odsm", "ObjectDescriptorStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("crsm", "ClockReferenceStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("sdsm", "SceneDescriptionStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("m7sm", "MPEG7Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("ocsm", "ObjectContentInfoStream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("ipsm", "IPMP Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("mjsm", "MPEG-J Stream - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        hm.put("mdir", "Apple Meta Data iTunes Reader");
        hm.put("mp7b", "MPEG-7 binary XML");
        hm.put("mp7t", "MPEG-7 XML");
        hm.put("vide", "Video Track");
        hm.put("soun", "Sound Track");
        hm.put(TrackReferenceTypeBox.TYPE1, "Hint Track");
        hm.put("appl", "Apple specific");
        hm.put(MetaBox.TYPE, "Timed Metadata track - defined in ISO/IEC JTC1/SC29/WG11 - CODING OF MOVING PICTURES AND AUDIO");
        readableTypes = Collections.unmodifiableMap(hm);
    }

    public HandlerBox() {
        super(TYPE);
    }

    public String getHandlerType() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.handlerType;
    }

    public void setName(String name2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) name2));
        this.name = name2;
    }

    public void setHandlerType(String handlerType2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) handlerType2));
        this.handlerType = handlerType2;
    }

    public String getName() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
        return this.name;
    }

    public String getHumanReadableTrackType() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return readableTypes.get(this.handlerType) != null ? readableTypes.get(this.handlerType) : "Unknown Handler Type";
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        if (this.zeroTerm) {
            return (long) (Utf8.utf8StringLengthInBytes(this.name) + 25);
        }
        return (long) (Utf8.utf8StringLengthInBytes(this.name) + 24);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.shouldBeZeroButAppleWritesHereSomeValue = IsoTypeReader.readUInt32(content);
        this.handlerType = IsoTypeReader.read4cc(content);
        this.a = IsoTypeReader.readUInt32(content);
        this.b = IsoTypeReader.readUInt32(content);
        this.c = IsoTypeReader.readUInt32(content);
        if (content.remaining() > 0) {
            String readString = IsoTypeReader.readString(content, content.remaining());
            this.name = readString;
            if (readString.endsWith("\u0000")) {
                String str = this.name;
                this.name = str.substring(0, str.length() - 1);
                this.zeroTerm = true;
                return;
            }
            this.zeroTerm = false;
            return;
        }
        this.zeroTerm = false;
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt32(byteBuffer, this.shouldBeZeroButAppleWritesHereSomeValue);
        byteBuffer.put(IsoFile.fourCCtoBytes(this.handlerType));
        IsoTypeWriter.writeUInt32(byteBuffer, this.a);
        IsoTypeWriter.writeUInt32(byteBuffer, this.b);
        IsoTypeWriter.writeUInt32(byteBuffer, this.c);
        String str = this.name;
        if (str != null) {
            byteBuffer.put(Utf8.convert(str));
        }
        if (this.zeroTerm) {
            byteBuffer.put((byte) 0);
        }
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        return "HandlerBox[handlerType=" + getHandlerType() + ";name=" + getName() + "]";
    }
}
