package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class DataEntryUrlBox extends AbstractFullBox {
    public static final String TYPE = "url ";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("DataEntryUrlBox.java", DataEntryUrlBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.DataEntryUrlBox", "", "", "", "java.lang.String"), 51);
    }

    public DataEntryUrlBox() {
        super(TYPE);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return 4;
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return "DataEntryUrlBox[]";
    }
}
