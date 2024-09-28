package com.mp4parser.iso23001.part7;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.reflect.Factory;

public class ProtectionSystemSpecificHeaderBox extends AbstractFullBox {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static byte[] OMA2_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("A2B55680-6F43-11E0-9A3F-0002A5D5C51B"));
    public static byte[] PLAYREADY_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"));
    public static final String TYPE = "pssh";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    byte[] content;
    List<UUID> keyIds = new ArrayList();
    byte[] systemId;

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("ProtectionSystemSpecificHeaderBox.java", ProtectionSystemSpecificHeaderBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getKeyIds", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "", "", "", "java.util.List"), 43);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setKeyIds", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "java.util.List", "keyIds", "", "void"), 47);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getSystemId", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "", "", "", "[B"), 54);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setSystemId", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "[B", "systemId", "", "void"), 58);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getContent", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "", "", "", "[B"), 63);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setContent", "com.mp4parser.iso23001.part7.ProtectionSystemSpecificHeaderBox", "[B", "content", "", "void"), 67);
    }

    static {
        ajc$preClinit();
    }

    public List<UUID> getKeyIds() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.keyIds;
    }

    public void setKeyIds(List<UUID> keyIds2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) keyIds2));
        this.keyIds = keyIds2;
    }

    public byte[] getSystemId() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.systemId;
    }

    public void setSystemId(byte[] systemId2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) systemId2));
        this.systemId = systemId2;
    }

    public byte[] getContent() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.content;
    }

    public void setContent(byte[] content2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, (Object) content2));
        this.content = content2;
    }

    public ProtectionSystemSpecificHeaderBox() {
        super(TYPE);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        long l = (long) (this.content.length + 24);
        if (getVersion() > 0) {
            return l + 4 + ((long) (this.keyIds.size() * 16));
        }
        return l;
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(this.systemId, 0, 16);
        if (getVersion() > 0) {
            IsoTypeWriter.writeUInt32(byteBuffer, (long) this.keyIds.size());
            for (UUID keyId : this.keyIds) {
                byteBuffer.put(UUIDConverter.convert(keyId));
            }
        }
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.content.length);
        byteBuffer.put(this.content);
    }

    /* access modifiers changed from: protected */
    public void _parseDetails(ByteBuffer content2) {
        parseVersionAndFlags(content2);
        byte[] bArr = new byte[16];
        this.systemId = bArr;
        content2.get(bArr);
        if (getVersion() > 0) {
            int count = CastUtils.l2i(IsoTypeReader.readUInt32(content2));
            while (true) {
                int count2 = count - 1;
                if (count <= 0) {
                    break;
                }
                byte[] k = new byte[16];
                content2.get(k);
                this.keyIds.add(UUIDConverter.convert(k));
                count = count2;
            }
        }
        long readUInt32 = IsoTypeReader.readUInt32(content2);
        byte[] bArr2 = new byte[content2.remaining()];
        this.content = bArr2;
        content2.get(bArr2);
    }
}
