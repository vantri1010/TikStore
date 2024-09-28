package com.googlecode.mp4parser.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class BaseLocationBox extends AbstractFullBox {
    public static final String TYPE = "bloc";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
    String baseLocation = "";
    String purchaseLocation = "";

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("BaseLocationBox.java", BaseLocationBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getBaseLocation", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "", "", "", "java.lang.String"), 44);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setBaseLocation", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "java.lang.String", "baseLocation", "", "void"), 48);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getPurchaseLocation", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "", "", "", "java.lang.String"), 52);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setPurchaseLocation", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "java.lang.String", "purchaseLocation", "", "void"), 56);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "equals", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "java.lang.Object", "o", "", "boolean"), 86);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "hashCode", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "", "", "", "int"), 100);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.googlecode.mp4parser.boxes.dece.BaseLocationBox", "", "", "", "java.lang.String"), 107);
    }

    public BaseLocationBox() {
        super(TYPE);
    }

    public BaseLocationBox(String baseLocation2, String purchaseLocation2) {
        super(TYPE);
        this.baseLocation = baseLocation2;
        this.purchaseLocation = purchaseLocation2;
    }

    public String getBaseLocation() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.baseLocation;
    }

    public void setBaseLocation(String baseLocation2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) baseLocation2));
        this.baseLocation = baseLocation2;
    }

    public String getPurchaseLocation() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.purchaseLocation;
    }

    public void setPurchaseLocation(String purchaseLocation2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) purchaseLocation2));
        this.purchaseLocation = purchaseLocation2;
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return 1028;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        String readString = IsoTypeReader.readString(content);
        this.baseLocation = readString;
        content.get(new byte[((256 - Utf8.utf8StringLengthInBytes(readString)) - 1)]);
        String readString2 = IsoTypeReader.readString(content);
        this.purchaseLocation = readString2;
        content.get(new byte[((256 - Utf8.utf8StringLengthInBytes(readString2)) - 1)]);
        content.get(new byte[512]);
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(Utf8.convert(this.baseLocation));
        byteBuffer.put(new byte[(256 - Utf8.utf8StringLengthInBytes(this.baseLocation))]);
        byteBuffer.put(Utf8.convert(this.purchaseLocation));
        byteBuffer.put(new byte[(256 - Utf8.utf8StringLengthInBytes(this.purchaseLocation))]);
        byteBuffer.put(new byte[512]);
    }

    public boolean equals(Object o) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, o));
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseLocationBox that = (BaseLocationBox) o;
        String str = this.baseLocation;
        if (str == null ? that.baseLocation != null : !str.equals(that.baseLocation)) {
            return false;
        }
        String str2 = this.purchaseLocation;
        if (str2 == null ? that.purchaseLocation == null : str2.equals(that.purchaseLocation)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        String str = this.baseLocation;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.purchaseLocation;
        if (str2 != null) {
            i = str2.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return "BaseLocationBox{baseLocation='" + this.baseLocation + '\'' + ", purchaseLocation='" + this.purchaseLocation + '\'' + '}';
    }
}
