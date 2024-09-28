package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.baidu.location.BDLocation;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import com.mp4parser.iso14496.part15.StepwiseTemporalLayerEntry;
import com.mp4parser.iso14496.part15.SyncSampleEntry;
import com.mp4parser.iso14496.part15.TemporalLayerSampleGroup;
import com.mp4parser.iso14496.part15.TemporalSubLayerSampleGroup;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SampleGroupDescriptionBox extends AbstractFullBox {
    public static final String TYPE = "sgpd";
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ JoinPoint.StaticPart ajc$tjp_6 = null;
    private int defaultLength;
    private List<GroupEntry> groupEntries = new LinkedList();

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("SampleGroupDescriptionBox.java", SampleGroupDescriptionBox.class);
        ajc$tjp_0 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getDefaultLength", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "", "", "", "int"), 145);
        ajc$tjp_1 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setDefaultLength", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "int", "defaultLength", "", "void"), 149);
        ajc$tjp_2 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "getGroupEntries", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "", "", "", "java.util.List"), 153);
        ajc$tjp_3 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "setGroupEntries", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "java.util.List", "groupEntries", "", "void"), 157);
        ajc$tjp_4 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "equals", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "java.lang.Object", "o", "", "boolean"), (int) BDLocation.TypeServerDecryptError);
        ajc$tjp_5 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "hashCode", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "", "", "", "int"), 183);
        ajc$tjp_6 = factory.makeSJP(JoinPoint.METHOD_EXECUTION, (Signature) factory.makeMethodSig("1", "toString", "com.googlecode.mp4parser.boxes.mp4.samplegrouping.SampleGroupDescriptionBox", "", "", "", "java.lang.String"), 191);
    }

    public SampleGroupDescriptionBox() {
        super(TYPE);
        setVersion(1);
    }

    /* access modifiers changed from: protected */
    public long getContentSize() {
        long size = 8;
        if (getVersion() == 1) {
            size = 8 + 4;
        }
        long size2 = size + 4;
        for (GroupEntry groupEntry : this.groupEntries) {
            if (getVersion() == 1 && this.defaultLength == 0) {
                size2 += 4;
            }
            size2 += (long) groupEntry.size();
        }
        return size2;
    }

    /* access modifiers changed from: protected */
    public void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        byteBuffer.put(IsoFile.fourCCtoBytes(this.groupEntries.get(0).getType()));
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt32(byteBuffer, (long) this.defaultLength);
        }
        IsoTypeWriter.writeUInt32(byteBuffer, (long) this.groupEntries.size());
        for (GroupEntry entry : this.groupEntries) {
            if (getVersion() == 1 && this.defaultLength == 0) {
                IsoTypeWriter.writeUInt32(byteBuffer, (long) entry.get().limit());
            }
            byteBuffer.put(entry.get());
        }
    }

    /* access modifiers changed from: protected */
    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if (getVersion() == 1) {
            String groupingType = IsoTypeReader.read4cc(content);
            if (getVersion() == 1) {
                this.defaultLength = CastUtils.l2i(IsoTypeReader.readUInt32(content));
            }
            long entryCount = IsoTypeReader.readUInt32(content);
            while (true) {
                long entryCount2 = entryCount - 1;
                if (entryCount > 0) {
                    int length = this.defaultLength;
                    if (getVersion() == 1) {
                        if (this.defaultLength == 0) {
                            length = CastUtils.l2i(IsoTypeReader.readUInt32(content));
                        }
                        ByteBuffer parseMe = content.slice();
                        parseMe.limit(length);
                        this.groupEntries.add(parseGroupEntry(parseMe, groupingType));
                        content.position(content.position() + length);
                        entryCount = entryCount2;
                    } else {
                        throw new RuntimeException("This should be implemented");
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new RuntimeException("SampleGroupDescriptionBox are only supported in version 1");
        }
    }

    private GroupEntry parseGroupEntry(ByteBuffer content, String groupingType) {
        GroupEntry groupEntry;
        if (RollRecoveryEntry.TYPE.equals(groupingType)) {
            groupEntry = new RollRecoveryEntry();
        } else if (RateShareEntry.TYPE.equals(groupingType)) {
            groupEntry = new RateShareEntry();
        } else if (CencSampleEncryptionInformationGroupEntry.TYPE.equals(groupingType)) {
            groupEntry = new CencSampleEncryptionInformationGroupEntry();
        } else if (VisualRandomAccessEntry.TYPE.equals(groupingType)) {
            groupEntry = new VisualRandomAccessEntry();
        } else if (TemporalLevelEntry.TYPE.equals(groupingType)) {
            groupEntry = new TemporalLevelEntry();
        } else if (SyncSampleEntry.TYPE.equals(groupingType)) {
            groupEntry = new SyncSampleEntry();
        } else if (TemporalLayerSampleGroup.TYPE.equals(groupingType)) {
            groupEntry = new TemporalLayerSampleGroup();
        } else if (TemporalSubLayerSampleGroup.TYPE.equals(groupingType)) {
            groupEntry = new TemporalSubLayerSampleGroup();
        } else if (StepwiseTemporalLayerEntry.TYPE.equals(groupingType)) {
            groupEntry = new StepwiseTemporalLayerEntry();
        } else {
            groupEntry = new UnknownEntry(groupingType);
        }
        groupEntry.parse(content);
        return groupEntry;
    }

    public int getDefaultLength() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.defaultLength;
    }

    public void setDefaultLength(int defaultLength2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, Conversions.intObject(defaultLength2)));
        this.defaultLength = defaultLength2;
    }

    public List<GroupEntry> getGroupEntries() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.groupEntries;
    }

    public void setGroupEntries(List<GroupEntry> groupEntries2) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) groupEntries2));
        this.groupEntries = groupEntries2;
    }

    public boolean equals(Object o) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, (Object) this, (Object) this, o));
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SampleGroupDescriptionBox that = (SampleGroupDescriptionBox) o;
        if (this.defaultLength != that.defaultLength) {
            return false;
        }
        List<GroupEntry> list = this.groupEntries;
        if (list == null ? that.groupEntries == null : list.equals(that.groupEntries)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        int result = ((0 * 31) + this.defaultLength) * 31;
        List<GroupEntry> list = this.groupEntries;
        return result + (list != null ? list.hashCode() : 0);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        StringBuilder sb = new StringBuilder("SampleGroupDescriptionBox{groupingType='");
        sb.append(this.groupEntries.size() > 0 ? this.groupEntries.get(0).getType() : "????");
        sb.append('\'');
        sb.append(", defaultLength=");
        sb.append(this.defaultLength);
        sb.append(", groupEntries=");
        sb.append(this.groupEntries);
        sb.append('}');
        return sb.toString();
    }
}
