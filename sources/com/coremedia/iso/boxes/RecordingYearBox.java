package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class RecordingYearBox extends AbstractFullBox {
    public static final String TYPE = "yrrc";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    int recordingYear;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("RecordingYearBox.java", RecordingYearBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getRecordingYear", "com.coremedia.iso.boxes.RecordingYearBox", "", "", "", "int"), 42);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setRecordingYear", "com.coremedia.iso.boxes.RecordingYearBox", "int", "recordingYear", "", "void"), 46);
    }

    public RecordingYearBox() {
        super(TYPE);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        return 6;
    }

    public int getRecordingYear() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.recordingYear;
    }

    public void setRecordingYear(int recordingYear2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(recordingYear2)));
        this.recordingYear = recordingYear2;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.recordingYear = IsoTypeReader.readUInt16(content);
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeUInt16(byteBuffer, this.recordingYear);
    }
}
