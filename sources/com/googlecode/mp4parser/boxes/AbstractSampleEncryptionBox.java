package com.googlecode.mp4parser.boxes;

import com.baidu.location.BDLocation;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public abstract class AbstractSampleEncryptionBox extends AbstractFullBox {
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    protected int algorithmId = -1;
    List<CencSampleAuxiliaryDataFormat> entries = Collections.emptyList();
    protected int ivSize = -1;
    protected byte[] kid = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("AbstractSampleEncryptionBox.java", AbstractSampleEncryptionBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getOffsetToFirstIV", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "int"), 29);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntries", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "java.util.List"), 89);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setEntries", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "java.util.List", RemoteConfigConstants.ResponseFieldKey.ENTRIES, "", "void"), 93);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "equals", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "java.lang.Object", "o", "", "boolean"), (int) BDLocation.TypeServerDecryptError);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "hashCode", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "int"), (int) PsExtractor.PRIVATE_STREAM_1);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getEntrySizes", "com.googlecode.mp4parser.boxes.AbstractSampleEncryptionBox", "", "", "", "java.util.List"), 197);
    }

    protected AbstractSampleEncryptionBox(String type) {
        super(type);
    }

    public int getOffsetToFirstIV() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return (getSize() > 4294967296L ? 16 : 8) + (isOverrideTrackEncryptionBoxParameters() ? this.kid.length + 4 : 0) + 4;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if ((getFlags() & 1) > 0) {
            this.algorithmId = IsoTypeReader.readUInt24(content);
            this.ivSize = IsoTypeReader.readUInt8(content);
            byte[] bArr = new byte[16];
            this.kid = bArr;
            content.get(bArr);
        }
        long numOfEntries = IsoTypeReader.readUInt32(content);
        ByteBuffer parseEight = content.duplicate();
        ByteBuffer parseSixteen = content.duplicate();
        List<CencSampleAuxiliaryDataFormat> parseEntries = parseEntries(parseEight, numOfEntries, 8);
        this.entries = parseEntries;
        if (parseEntries == null) {
            this.entries = parseEntries(parseSixteen, numOfEntries, 16);
            content.position((content.position() + content.remaining()) - parseSixteen.remaining());
        } else {
            content.position((content.position() + content.remaining()) - parseEight.remaining());
        }
        if (this.entries == null) {
            throw new RuntimeException("Cannot parse SampleEncryptionBox");
        }
    }

    private List<CencSampleAuxiliaryDataFormat> parseEntries(ByteBuffer content, long numOfEntries, int ivSize2) {
        List _entries = new ArrayList();
        long remainingNumOfEntries = numOfEntries;
        while (true) {
            long remainingNumOfEntries2 = remainingNumOfEntries - 1;
            if (remainingNumOfEntries <= 0) {
                return _entries;
            }
            try {
                CencSampleAuxiliaryDataFormat e = new CencSampleAuxiliaryDataFormat();
                e.iv = new byte[ivSize2];
                content.get(e.iv);
                if ((getFlags() & 2) > 0) {
                    e.pairs = new CencSampleAuxiliaryDataFormat.Pair[IsoTypeReader.readUInt16(content)];
                    for (int i = 0; i < e.pairs.length; i++) {
                        e.pairs[i] = e.createPair(IsoTypeReader.readUInt16(content), IsoTypeReader.readUInt32(content));
                    }
                }
                _entries.add(e);
                remainingNumOfEntries = remainingNumOfEntries2;
            } catch (BufferUnderflowException e2) {
                return null;
            }
        }
    }

    public List<CencSampleAuxiliaryDataFormat> getEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.entries;
    }

    public void setEntries(List<CencSampleAuxiliaryDataFormat> entries2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, (Object) this, (Object) this, (Object) entries2));
        this.entries = entries2;
    }

    @DoNotParseDetail
    public boolean isSubSampleEncryption() {
        return (getFlags() & 2) > 0;
    }

    @DoNotParseDetail
    public void setSubSampleEncryption(boolean b) {
        if (b) {
            setFlags(getFlags() | 2);
        } else {
            setFlags(getFlags() & 16777213);
        }
    }

    /* access modifiers changed from: protected */
    @DoNotParseDetail
    public boolean isOverrideTrackEncryptionBoxParameters() {
        return (getFlags() & 1) > 0;
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (isOverrideTrackEncryptionBoxParameters()) {
            IsoTypeWriter.writeUInt24(byteBuffer, this.algorithmId);
            IsoTypeWriter.writeUInt8(byteBuffer, this.ivSize);
            byteBuffer.put(this.kid);
        }
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.entries.size());
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            if (entry.getSize() > 0) {
                if (entry.iv.length == 8 || entry.iv.length == 16) {
                    byteBuffer.put(entry.iv);
                    if (isSubSampleEncryption()) {
                        IsoTypeWriter.writeUInt16(byteBuffer, entry.pairs.length);
                        for (CencSampleAuxiliaryDataFormat.Pair pair : entry.pairs) {
                            IsoTypeWriter.writeUInt16(byteBuffer, pair.clear());
                            IsoTypeWriter.writeUInt32(byteBuffer, pair.encrypted());
                        }
                    }
                } else {
                    throw new RuntimeException("IV must be either 8 or 16 bytes");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        long contentSize = 4;
        if (isOverrideTrackEncryptionBoxParameters()) {
            contentSize = 4 + 4 + ((long) this.kid.length);
        }
        long contentSize2 = contentSize + 4;
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            contentSize2 += (long) entry.getSize();
        }
        return contentSize2;
    }

    public void getBox(WritableByteChannel os) throws IOException {
        super.getBox(os);
    }

    public boolean equals(Object o) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, o));
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractSampleEncryptionBox that = (AbstractSampleEncryptionBox) o;
        if (this.algorithmId != that.algorithmId || this.ivSize != that.ivSize) {
            return false;
        }
        List<CencSampleAuxiliaryDataFormat> list = this.entries;
        if (list == null ? that.entries != null : !list.equals(that.entries)) {
            return false;
        }
        if (!Arrays.equals(this.kid, that.kid)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        int result = ((this.algorithmId * 31) + this.ivSize) * 31;
        byte[] bArr = this.kid;
        int i = 0;
        int result2 = (result + (bArr != null ? Arrays.hashCode(bArr) : 0)) * 31;
        List<CencSampleAuxiliaryDataFormat> list = this.entries;
        if (list != null) {
            i = list.hashCode();
        }
        return result2 + i;
    }

    public List<Short> getEntrySizes() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        List entrySizes = new ArrayList(this.entries.size());
        for (CencSampleAuxiliaryDataFormat entry : this.entries) {
            short size = (short) entry.iv.length;
            if (isSubSampleEncryption()) {
                size = (short) ((entry.pairs.length * 6) + ((short) (size + 2)));
            }
            entrySizes.add(Short.valueOf(size));
        }
        return entrySizes;
    }
}
