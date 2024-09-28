package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.ProtectionSchemeInformationBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SchemeInformationBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.google.android.exoplayer2.C;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.MemoryDataSourceImpl;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.cenc.CencEncryptingSampleList;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.RangeStartMap;
import com.litesuits.orm.db.assit.SQLBuilder;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.HevcConfigurationBox;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import com.mp4parser.iso23001.part7.TrackEncryptionBox;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;

public class CencEncryptingTrackImpl implements CencEncryptedTrack {
    List<CencSampleAuxiliaryDataFormat> cencSampleAuxiliaryData;
    UUID defaultKeyId;
    boolean dummyIvs;
    private final String encryptionAlgo;
    RangeStartMap<Integer, SecretKey> indexToKey;
    Map<UUID, SecretKey> keys;
    Map<GroupEntry, long[]> sampleGroups;
    List<Sample> samples;
    Track source;
    SampleDescriptionBox stsd;
    boolean subSampleEncryption;

    public CencEncryptingTrackImpl(Track source2, UUID defaultKeyId2, SecretKey key, boolean dummyIvs2) {
        this(source2, defaultKeyId2, Collections.singletonMap(defaultKeyId2, key), (Map<CencSampleEncryptionInformationGroupEntry, long[]>) null, C.CENC_TYPE_cenc, dummyIvs2);
    }

    public CencEncryptingTrackImpl(Track source2, UUID defaultKeyId2, Map<UUID, SecretKey> keys2, Map<CencSampleEncryptionInformationGroupEntry, long[]> keyRotation, String encryptionAlgo2, boolean dummyIvs2) {
        this(source2, defaultKeyId2, keys2, keyRotation, encryptionAlgo2, dummyIvs2, false);
    }

    public CencEncryptingTrackImpl(Track source2, UUID defaultKeyId2, Map<UUID, SecretKey> keys2, Map<CencSampleEncryptionInformationGroupEntry, long[]> keyRotation, String encryptionAlgo2, boolean dummyIvs2, boolean encryptButAllClear) {
        byte[] init;
        byte[] init2;
        int clearBytes;
        boolean z;
        Object obj;
        UUID uuid;
        UUID uuid2 = defaultKeyId2;
        Map<UUID, SecretKey> map = keys2;
        boolean z2 = dummyIvs2;
        this.keys = new HashMap();
        this.dummyIvs = false;
        this.subSampleEncryption = false;
        Object obj2 = null;
        this.stsd = null;
        this.source = source2;
        this.keys = map;
        this.defaultKeyId = uuid2;
        this.dummyIvs = z2;
        this.encryptionAlgo = encryptionAlgo2;
        this.sampleGroups = new HashMap();
        for (Map.Entry<GroupEntry, long[]> entry : source2.getSampleGroups().entrySet()) {
            UUID uuid3 = uuid2;
            Object obj3 = obj2;
            if (!(entry.getKey() instanceof CencSampleEncryptionInformationGroupEntry)) {
                this.sampleGroups.put(entry.getKey(), entry.getValue());
                z2 = dummyIvs2;
                uuid2 = uuid3;
                obj2 = obj3;
                Track track = source2;
                String str = encryptionAlgo2;
            } else {
                z2 = dummyIvs2;
                uuid2 = uuid3;
                obj2 = obj3;
                Track track2 = source2;
                String str2 = encryptionAlgo2;
            }
        }
        if (keyRotation != null) {
            for (Map.Entry<CencSampleEncryptionInformationGroupEntry, long[]> entry2 : keyRotation.entrySet()) {
                this.sampleGroups.put(entry2.getKey(), entry2.getValue());
            }
        }
        this.sampleGroups = new HashMap<GroupEntry, long[]>(this.sampleGroups) {
            public long[] put(GroupEntry key, long[] value) {
                if (!(key instanceof CencSampleEncryptionInformationGroupEntry)) {
                    return (long[]) super.put(key, value);
                }
                throw new RuntimeException("Please supply CencSampleEncryptionInformationGroupEntries in the constructor");
            }
        };
        this.samples = source2.getSamples();
        this.cencSampleAuxiliaryData = new ArrayList();
        BigInteger one = new BigInteger("1");
        byte[] init3 = new byte[8];
        if (!z2) {
            new SecureRandom().nextBytes(init3);
        }
        BigInteger ivInt = new BigInteger(1, init3);
        List<CencSampleEncryptionInformationGroupEntry> groupEntries = new ArrayList<>();
        if (keyRotation != null) {
            groupEntries.addAll(keyRotation.keySet());
        }
        this.indexToKey = new RangeStartMap<>();
        int lastSampleGroupDescriptionIndex = -1;
        int i = 0;
        while (i < source2.getSamples().size()) {
            byte[] init4 = init;
            int index = 0;
            for (int j = 0; j < groupEntries.size(); j++) {
                UUID uuid4 = defaultKeyId2;
                if (Arrays.binarySearch(getSampleGroups().get(groupEntries.get(j)), (long) i) >= 0) {
                    index = j + 1;
                }
            }
            if (lastSampleGroupDescriptionIndex != index) {
                if (index == 0) {
                    uuid = defaultKeyId2;
                    this.indexToKey.put(Integer.valueOf(i), map.get(uuid));
                    obj = null;
                } else {
                    uuid = defaultKeyId2;
                    if (groupEntries.get(index - 1).getKid() != null) {
                        SecretKey sk = map.get(groupEntries.get(index - 1).getKid());
                        if (sk != null) {
                            this.indexToKey.put(Integer.valueOf(i), sk);
                            obj = null;
                        } else {
                            throw new RuntimeException("Key " + groupEntries.get(index - 1).getKid() + " was not supplied for decryption");
                        }
                    } else {
                        obj = null;
                        this.indexToKey.put(Integer.valueOf(i), null);
                    }
                }
                lastSampleGroupDescriptionIndex = index;
            } else {
                uuid = defaultKeyId2;
                obj = null;
            }
            i++;
            boolean z3 = dummyIvs2;
            UUID uuid5 = uuid;
            Object obj4 = obj;
            init3 = init4;
            Track track3 = source2;
            String str3 = encryptionAlgo2;
        }
        List<Box> boxes = source2.getSampleDescriptionBox().getSampleEntry().getBoxes();
        int nalLengthSize = -1;
        for (Box box : boxes) {
            List<Box> boxes2 = boxes;
            byte[] init5 = init;
            if (box instanceof AvcConfigurationBox) {
                z = true;
                this.subSampleEncryption = true;
                nalLengthSize = ((AvcConfigurationBox) box).getLengthSizeMinusOne() + 1;
            } else {
                z = true;
            }
            if (box instanceof HevcConfigurationBox) {
                this.subSampleEncryption = z;
                nalLengthSize = ((HevcConfigurationBox) box).getLengthSizeMinusOne() + 1;
                Track track4 = source2;
                UUID uuid6 = defaultKeyId2;
                String str4 = encryptionAlgo2;
                boolean z4 = dummyIvs2;
                init = init5;
                boxes = boxes2;
            } else {
                Track track5 = source2;
                UUID uuid7 = defaultKeyId2;
                String str5 = encryptionAlgo2;
                boolean z5 = dummyIvs2;
                init = init5;
                boxes = boxes2;
            }
        }
        int i2 = 0;
        while (i2 < this.samples.size()) {
            Sample origSample = this.samples.get(i2);
            CencSampleAuxiliaryDataFormat e = new CencSampleAuxiliaryDataFormat();
            this.cencSampleAuxiliaryData.add(e);
            List<Box> boxes3 = boxes;
            if (this.indexToKey.get(Integer.valueOf(i2)) != null) {
                byte[] iv = ivInt.toByteArray();
                byte[] eightByteIv = new byte[8];
                init2 = init;
                System.arraycopy(iv, iv.length - 8 > 0 ? iv.length - 8 : 0, eightByteIv, 8 - iv.length < 0 ? 0 : 8 - iv.length, iv.length > 8 ? 8 : iv.length);
                e.iv = eightByteIv;
                ByteBuffer sample = (ByteBuffer) origSample.asByteBuffer().rewind();
                if (!this.subSampleEncryption) {
                    byte[] bArr = eightByteIv;
                } else if (encryptButAllClear) {
                    byte[] bArr2 = iv;
                    byte[] bArr3 = eightByteIv;
                    e.pairs = new CencSampleAuxiliaryDataFormat.Pair[]{e.createPair(sample.remaining(), 0)};
                } else {
                    byte[] bArr4 = eightByteIv;
                    List<CencSampleAuxiliaryDataFormat.Pair> pairs = new ArrayList<>(5);
                    while (sample.remaining() > 0) {
                        int nalLength = CastUtils.l2i(IsoTypeReaderVariable.read(sample, nalLengthSize));
                        int nalGrossSize = nalLength + nalLengthSize;
                        if (nalGrossSize >= 112) {
                            clearBytes = (nalGrossSize % 16) + 96;
                        } else {
                            clearBytes = nalGrossSize;
                        }
                        int i3 = nalGrossSize;
                        pairs.add(e.createPair(clearBytes, (long) (nalGrossSize - clearBytes)));
                        sample.position(sample.position() + nalLength);
                    }
                    e.pairs = (CencSampleAuxiliaryDataFormat.Pair[]) pairs.toArray(new CencSampleAuxiliaryDataFormat.Pair[pairs.size()]);
                }
                ivInt = ivInt.add(one);
            } else {
                init2 = init;
            }
            i2++;
            Track track6 = source2;
            UUID uuid8 = defaultKeyId2;
            String str6 = encryptionAlgo2;
            boolean z6 = dummyIvs2;
            init = init2;
            boxes = boxes3;
        }
        System.err.println("");
    }

    public UUID getDefaultKeyId() {
        return this.defaultKeyId;
    }

    public boolean hasSubSampleEncryption() {
        return this.subSampleEncryption;
    }

    public List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries() {
        return this.cencSampleAuxiliaryData;
    }

    public synchronized SampleDescriptionBox getSampleDescriptionBox() {
        if (this.stsd == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                this.source.getSampleDescriptionBox().getBox(Channels.newChannel(baos));
                int i = 0;
                this.stsd = (SampleDescriptionBox) new IsoFile((DataSource) new MemoryDataSourceImpl(baos.toByteArray())).getBoxes().get(0);
                OriginalFormatBox originalFormatBox = new OriginalFormatBox();
                originalFormatBox.setDataFormat(this.stsd.getSampleEntry().getType());
                if (this.stsd.getSampleEntry() instanceof AudioSampleEntry) {
                    ((AudioSampleEntry) this.stsd.getSampleEntry()).setType(AudioSampleEntry.TYPE_ENCRYPTED);
                } else if (this.stsd.getSampleEntry() instanceof VisualSampleEntry) {
                    ((VisualSampleEntry) this.stsd.getSampleEntry()).setType(VisualSampleEntry.TYPE_ENCRYPTED);
                } else {
                    throw new RuntimeException("I don't know how to cenc " + this.stsd.getSampleEntry().getType());
                }
                ProtectionSchemeInformationBox sinf = new ProtectionSchemeInformationBox();
                sinf.addBox(originalFormatBox);
                SchemeTypeBox schm = new SchemeTypeBox();
                schm.setSchemeType(this.encryptionAlgo);
                schm.setSchemeVersion(65536);
                sinf.addBox(schm);
                SchemeInformationBox schi = new SchemeInformationBox();
                TrackEncryptionBox trackEncryptionBox = new TrackEncryptionBox();
                trackEncryptionBox.setDefaultIvSize(this.defaultKeyId == null ? 0 : 8);
                if (this.defaultKeyId != null) {
                    i = 1;
                }
                trackEncryptionBox.setDefaultAlgorithmId(i);
                trackEncryptionBox.setDefault_KID(this.defaultKeyId == null ? new UUID(0, 0) : this.defaultKeyId);
                schi.addBox(trackEncryptionBox);
                sinf.addBox(schi);
                this.stsd.getSampleEntry().addBox(sinf);
            } catch (IOException e) {
                throw new RuntimeException("Dumping stsd to memory failed");
            }
        }
        return this.stsd;
    }

    public long[] getSampleDurations() {
        return this.source.getSampleDurations();
    }

    public long getDuration() {
        return this.source.getDuration();
    }

    public List<CompositionTimeToSample.Entry> getCompositionTimeEntries() {
        return this.source.getCompositionTimeEntries();
    }

    public long[] getSyncSamples() {
        return this.source.getSyncSamples();
    }

    public List<SampleDependencyTypeBox.Entry> getSampleDependencies() {
        return this.source.getSampleDependencies();
    }

    public TrackMetaData getTrackMetaData() {
        return this.source.getTrackMetaData();
    }

    public String getHandler() {
        return this.source.getHandler();
    }

    public List<Sample> getSamples() {
        return new CencEncryptingSampleList(this.indexToKey, this.source.getSamples(), this.cencSampleAuxiliaryData, this.encryptionAlgo);
    }

    public SubSampleInformationBox getSubsampleInformationBox() {
        return this.source.getSubsampleInformationBox();
    }

    public void close() throws IOException {
        this.source.close();
    }

    public String getName() {
        return "enc(" + this.source.getName() + SQLBuilder.PARENTHESES_RIGHT;
    }

    public List<Edit> getEdits() {
        return this.source.getEdits();
    }

    public Map<GroupEntry, long[]> getSampleGroups() {
        return this.sampleGroups;
    }
}
