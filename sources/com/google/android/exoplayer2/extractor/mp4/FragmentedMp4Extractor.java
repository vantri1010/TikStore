package com.google.android.exoplayer2.extractor.mp4;

import android.util.Pair;
import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ChunkIndex;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.CencSampleEncryptionInformationGroupEntry;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FragmentedMp4Extractor implements Extractor {
    private static final Format EMSG_FORMAT = Format.createSampleFormat((String) null, MimeTypes.APPLICATION_EMSG, Long.MAX_VALUE);
    public static final ExtractorsFactory FACTORY = $$Lambda$FragmentedMp4Extractor$i0zfpH_PcF0vytkdatCL0xeWFhQ.INSTANCE;
    public static final int FLAG_ENABLE_EMSG_TRACK = 4;
    private static final int FLAG_SIDELOADED = 8;
    public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 16;
    public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
    private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = {-94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12};
    private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString(CencSampleEncryptionInformationGroupEntry.TYPE);
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_ENCRYPTION_DATA = 2;
    private static final int STATE_READING_SAMPLE_CONTINUE = 4;
    private static final int STATE_READING_SAMPLE_START = 3;
    private static final String TAG = "FragmentedMp4Extractor";
    private final TrackOutput additionalEmsgTrackOutput;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private TrackOutput[] cea608TrackOutputs;
    private final List<Format> closedCaptionFormats;
    private final ArrayDeque<Atom.ContainerAtom> containerAtoms;
    private TrackBundle currentTrackBundle;
    private long durationUs;
    private TrackOutput[] emsgTrackOutputs;
    private long endOfMdatPosition;
    private final byte[] extendedTypeScratch;
    private ExtractorOutput extractorOutput;
    private final int flags;
    private boolean haveOutputSeekMap;
    private final ParsableByteArray nalBuffer;
    private final ParsableByteArray nalPrefix;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int pendingMetadataSampleBytes;
    private final ArrayDeque<MetadataSampleInfo> pendingMetadataSampleInfos;
    private long pendingSeekTimeUs;
    private boolean processSeiNalUnitPayload;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleSize;
    private long segmentIndexEarliestPresentationTimeUs;
    private final DrmInitData sideloadedDrmInitData;
    private final Track sideloadedTrack;
    private final TimestampAdjuster timestampAdjuster;
    private final SparseArray<TrackBundle> trackBundles;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new FragmentedMp4Extractor()};
    }

    public FragmentedMp4Extractor() {
        this(0);
    }

    public FragmentedMp4Extractor(int flags2) {
        this(flags2, (TimestampAdjuster) null);
    }

    public FragmentedMp4Extractor(int flags2, TimestampAdjuster timestampAdjuster2) {
        this(flags2, timestampAdjuster2, (Track) null, (DrmInitData) null);
    }

    public FragmentedMp4Extractor(int flags2, TimestampAdjuster timestampAdjuster2, Track sideloadedTrack2, DrmInitData sideloadedDrmInitData2) {
        this(flags2, timestampAdjuster2, sideloadedTrack2, sideloadedDrmInitData2, Collections.emptyList());
    }

    public FragmentedMp4Extractor(int flags2, TimestampAdjuster timestampAdjuster2, Track sideloadedTrack2, DrmInitData sideloadedDrmInitData2, List<Format> closedCaptionFormats2) {
        this(flags2, timestampAdjuster2, sideloadedTrack2, sideloadedDrmInitData2, closedCaptionFormats2, (TrackOutput) null);
    }

    public FragmentedMp4Extractor(int flags2, TimestampAdjuster timestampAdjuster2, Track sideloadedTrack2, DrmInitData sideloadedDrmInitData2, List<Format> closedCaptionFormats2, TrackOutput additionalEmsgTrackOutput2) {
        this.flags = (sideloadedTrack2 != null ? 8 : 0) | flags2;
        this.timestampAdjuster = timestampAdjuster2;
        this.sideloadedTrack = sideloadedTrack2;
        this.sideloadedDrmInitData = sideloadedDrmInitData2;
        this.closedCaptionFormats = Collections.unmodifiableList(closedCaptionFormats2);
        this.additionalEmsgTrackOutput = additionalEmsgTrackOutput2;
        this.atomHeader = new ParsableByteArray(16);
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalPrefix = new ParsableByteArray(5);
        this.nalBuffer = new ParsableByteArray();
        this.extendedTypeScratch = new byte[16];
        this.containerAtoms = new ArrayDeque<>();
        this.pendingMetadataSampleInfos = new ArrayDeque<>();
        this.trackBundles = new SparseArray<>();
        this.durationUs = C.TIME_UNSET;
        this.pendingSeekTimeUs = C.TIME_UNSET;
        this.segmentIndexEarliestPresentationTimeUs = C.TIME_UNSET;
        enterReadingAtomHeaderState();
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return Sniffer.sniffFragmented(input);
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        Track track = this.sideloadedTrack;
        if (track != null) {
            TrackBundle bundle = new TrackBundle(output.track(0, track.type));
            bundle.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
            this.trackBundles.put(0, bundle);
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
        }
    }

    public void seek(long position, long timeUs) {
        int trackCount = this.trackBundles.size();
        for (int i = 0; i < trackCount; i++) {
            this.trackBundles.valueAt(i).reset();
        }
        this.pendingMetadataSampleInfos.clear();
        this.pendingMetadataSampleBytes = 0;
        this.pendingSeekTimeUs = timeUs;
        this.containerAtoms.clear();
        enterReadingAtomHeaderState();
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        while (true) {
            int i = this.parserState;
            if (i != 0) {
                if (i == 1) {
                    readAtomPayload(input);
                } else if (i == 2) {
                    readEncryptionData(input);
                } else if (readSample(input)) {
                    return 0;
                }
            } else if (!readAtomHeader(input)) {
                return -1;
            }
        }
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    private boolean readAtomHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (this.atomHeaderBytesRead == 0) {
            if (!input.readFully(this.atomHeader.data, 0, 8, true)) {
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        long j = this.atomSize;
        if (j == 1) {
            input.readFully(this.atomHeader.data, 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (j == 0) {
            long endPosition = input.getLength();
            if (endPosition == -1 && !this.containerAtoms.isEmpty()) {
                endPosition = this.containerAtoms.peek().endPosition;
            }
            if (endPosition != -1) {
                this.atomSize = (endPosition - input.getPosition()) + ((long) this.atomHeaderBytesRead);
            }
        }
        if (this.atomSize >= ((long) this.atomHeaderBytesRead)) {
            long atomPosition = input.getPosition() - ((long) this.atomHeaderBytesRead);
            if (this.atomType == Atom.TYPE_moof) {
                int trackCount = this.trackBundles.size();
                for (int i = 0; i < trackCount; i++) {
                    TrackFragment fragment = this.trackBundles.valueAt(i).fragment;
                    fragment.atomPosition = atomPosition;
                    fragment.auxiliaryDataPosition = atomPosition;
                    fragment.dataPosition = atomPosition;
                }
            }
            if (this.atomType == Atom.TYPE_mdat) {
                this.currentTrackBundle = null;
                this.endOfMdatPosition = this.atomSize + atomPosition;
                if (!this.haveOutputSeekMap) {
                    this.extractorOutput.seekMap(new SeekMap.Unseekable(this.durationUs, atomPosition));
                    this.haveOutputSeekMap = true;
                }
                this.parserState = 2;
                return true;
            }
            if (shouldParseContainerAtom(this.atomType)) {
                long endPosition2 = (input.getPosition() + this.atomSize) - 8;
                this.containerAtoms.push(new Atom.ContainerAtom(this.atomType, endPosition2));
                if (this.atomSize == ((long) this.atomHeaderBytesRead)) {
                    processAtomEnded(endPosition2);
                } else {
                    enterReadingAtomHeaderState();
                }
            } else if (shouldParseLeafAtom(this.atomType)) {
                if (this.atomHeaderBytesRead == 8) {
                    long j2 = this.atomSize;
                    if (j2 <= 2147483647L) {
                        this.atomData = new ParsableByteArray((int) j2);
                        System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
                        this.parserState = 1;
                    } else {
                        throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
                    }
                } else {
                    throw new ParserException("Leaf atom defines extended atom size (unsupported).");
                }
            } else if (this.atomSize <= 2147483647L) {
                this.atomData = null;
                this.parserState = 1;
            } else {
                throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
            }
            return true;
        }
        throw new ParserException("Atom size less than header length (unsupported).");
    }

    private void readAtomPayload(ExtractorInput input) throws IOException, InterruptedException {
        int atomPayloadSize = ((int) this.atomSize) - this.atomHeaderBytesRead;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            input.readFully(parsableByteArray.data, 8, atomPayloadSize);
            onLeafAtomRead(new Atom.LeafAtom(this.atomType, this.atomData), input.getPosition());
        } else {
            input.skipFully(atomPayloadSize);
        }
        processAtomEnded(input.getPosition());
    }

    private void processAtomEnded(long atomEndPosition) throws ParserException {
        while (!this.containerAtoms.isEmpty() && this.containerAtoms.peek().endPosition == atomEndPosition) {
            onContainerAtomRead(this.containerAtoms.pop());
        }
        enterReadingAtomHeaderState();
    }

    private void onLeafAtomRead(Atom.LeafAtom leaf, long inputPosition) throws ParserException {
        if (!this.containerAtoms.isEmpty()) {
            this.containerAtoms.peek().add(leaf);
        } else if (leaf.type == Atom.TYPE_sidx) {
            Pair<Long, ChunkIndex> result = parseSidx(leaf.data, inputPosition);
            this.segmentIndexEarliestPresentationTimeUs = ((Long) result.first).longValue();
            this.extractorOutput.seekMap((SeekMap) result.second);
            this.haveOutputSeekMap = true;
        } else if (leaf.type == Atom.TYPE_emsg) {
            onEmsgLeafAtomRead(leaf.data);
        }
    }

    private void onContainerAtomRead(Atom.ContainerAtom container) throws ParserException {
        if (container.type == Atom.TYPE_moov) {
            onMoovContainerAtomRead(container);
        } else if (container.type == Atom.TYPE_moof) {
            onMoofContainerAtomRead(container);
        } else if (!this.containerAtoms.isEmpty()) {
            this.containerAtoms.peek().add(container);
        }
    }

    private void onMoovContainerAtomRead(Atom.ContainerAtom moov) throws ParserException {
        DrmInitData drmInitData;
        int moovContainerChildrenSize;
        int i;
        SparseArray<Track> tracks;
        Atom.ContainerAtom containerAtom = moov;
        Assertions.checkState(this.sideloadedTrack == null, "Unexpected moov box.");
        DrmInitData drmInitData2 = this.sideloadedDrmInitData;
        if (drmInitData2 != null) {
            drmInitData = drmInitData2;
        } else {
            drmInitData = getDrmInitDataFromAtoms(containerAtom.leafChildren);
        }
        Atom.ContainerAtom mvex = containerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
        SparseArray sparseArray = new SparseArray();
        int mvexChildrenSize = mvex.leafChildren.size();
        long duration = -9223372036854775807L;
        for (int i2 = 0; i2 < mvexChildrenSize; i2++) {
            Atom.LeafAtom atom = mvex.leafChildren.get(i2);
            if (atom.type == Atom.TYPE_trex) {
                Pair<Integer, DefaultSampleValues> trexData = parseTrex(atom.data);
                sparseArray.put(((Integer) trexData.first).intValue(), trexData.second);
            } else if (atom.type == Atom.TYPE_mehd) {
                duration = parseMehd(atom.data);
            }
        }
        SparseArray<Track> tracks2 = new SparseArray<>();
        int moovContainerChildrenSize2 = containerAtom.containerChildren.size();
        int i3 = 0;
        while (i3 < moovContainerChildrenSize2) {
            Atom.ContainerAtom atom2 = containerAtom.containerChildren.get(i3);
            if (atom2.type == Atom.TYPE_trak) {
                i = i3;
                Atom.ContainerAtom containerAtom2 = atom2;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
                Track track = modifyTrack(AtomParsers.parseTrak(atom2, containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), duration, drmInitData, (this.flags & 16) != 0, false));
                if (track != null) {
                    tracks.put(track.id, track);
                }
            } else {
                i = i3;
                Atom.ContainerAtom containerAtom3 = atom2;
                moovContainerChildrenSize = moovContainerChildrenSize2;
                tracks = tracks2;
            }
            i3 = i + 1;
            tracks2 = tracks;
            moovContainerChildrenSize2 = moovContainerChildrenSize;
        }
        int i4 = i3;
        int i5 = moovContainerChildrenSize2;
        SparseArray<Track> tracks3 = tracks2;
        int trackCount = tracks3.size();
        if (this.trackBundles.size() == 0) {
            int i6 = 0;
            while (i6 < trackCount) {
                Track track2 = tracks3.valueAt(i6);
                TrackBundle trackBundle = new TrackBundle(this.extractorOutput.track(i6, track2.type));
                trackBundle.init(track2, getDefaultSampleValues(sparseArray, track2.id));
                this.trackBundles.put(track2.id, trackBundle);
                this.durationUs = Math.max(this.durationUs, track2.durationUs);
                i6++;
                Atom.ContainerAtom containerAtom4 = moov;
                mvex = mvex;
            }
            maybeInitExtraTracks();
            this.extractorOutput.endTracks();
            return;
        }
        Assertions.checkState(this.trackBundles.size() == trackCount);
        for (int i7 = 0; i7 < trackCount; i7++) {
            Track track3 = tracks3.valueAt(i7);
            this.trackBundles.get(track3.id).init(track3, getDefaultSampleValues(sparseArray, track3.id));
        }
    }

    /* access modifiers changed from: protected */
    public Track modifyTrack(Track track) {
        return track;
    }

    private DefaultSampleValues getDefaultSampleValues(SparseArray<DefaultSampleValues> defaultSampleValuesArray, int trackId) {
        if (defaultSampleValuesArray.size() == 1) {
            return defaultSampleValuesArray.valueAt(0);
        }
        return (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValuesArray.get(trackId));
    }

    private void onMoofContainerAtomRead(Atom.ContainerAtom moof) throws ParserException {
        DrmInitData drmInitData;
        parseMoof(moof, this.trackBundles, this.flags, this.extendedTypeScratch);
        if (this.sideloadedDrmInitData != null) {
            drmInitData = null;
        } else {
            drmInitData = getDrmInitDataFromAtoms(moof.leafChildren);
        }
        if (drmInitData != null) {
            int trackCount = this.trackBundles.size();
            for (int i = 0; i < trackCount; i++) {
                this.trackBundles.valueAt(i).updateDrmInitData(drmInitData);
            }
        }
        if (this.pendingSeekTimeUs != C.TIME_UNSET) {
            int trackCount2 = this.trackBundles.size();
            for (int i2 = 0; i2 < trackCount2; i2++) {
                this.trackBundles.valueAt(i2).seek(this.pendingSeekTimeUs);
            }
            this.pendingSeekTimeUs = C.TIME_UNSET;
        }
    }

    private void maybeInitExtraTracks() {
        if (this.emsgTrackOutputs == null) {
            TrackOutput[] trackOutputArr = new TrackOutput[2];
            this.emsgTrackOutputs = trackOutputArr;
            int emsgTrackOutputCount = 0;
            TrackOutput trackOutput = this.additionalEmsgTrackOutput;
            if (trackOutput != null) {
                trackOutputArr[0] = trackOutput;
                emsgTrackOutputCount = 0 + 1;
            }
            if ((this.flags & 4) != 0) {
                this.emsgTrackOutputs[emsgTrackOutputCount] = this.extractorOutput.track(this.trackBundles.size(), 4);
                emsgTrackOutputCount++;
            }
            TrackOutput[] trackOutputArr2 = (TrackOutput[]) Arrays.copyOf(this.emsgTrackOutputs, emsgTrackOutputCount);
            this.emsgTrackOutputs = trackOutputArr2;
            for (TrackOutput eventMessageTrackOutput : trackOutputArr2) {
                eventMessageTrackOutput.format(EMSG_FORMAT);
            }
        }
        if (this.cea608TrackOutputs == null) {
            this.cea608TrackOutputs = new TrackOutput[this.closedCaptionFormats.size()];
            for (int i = 0; i < this.cea608TrackOutputs.length; i++) {
                TrackOutput output = this.extractorOutput.track(this.trackBundles.size() + 1 + i, 3);
                output.format(this.closedCaptionFormats.get(i));
                this.cea608TrackOutputs[i] = output;
            }
        }
    }

    private void onEmsgLeafAtomRead(ParsableByteArray atom) {
        long sampleTimeUs;
        ParsableByteArray parsableByteArray = atom;
        TrackOutput[] trackOutputArr = this.emsgTrackOutputs;
        if (trackOutputArr != null && trackOutputArr.length != 0) {
            parsableByteArray.setPosition(12);
            int sampleSize2 = atom.bytesLeft();
            atom.readNullTerminatedString();
            atom.readNullTerminatedString();
            long presentationTimeDeltaUs = Util.scaleLargeTimestamp(atom.readUnsignedInt(), 1000000, atom.readUnsignedInt());
            for (TrackOutput emsgTrackOutput : this.emsgTrackOutputs) {
                parsableByteArray.setPosition(12);
                emsgTrackOutput.sampleData(parsableByteArray, sampleSize2);
            }
            long j = this.segmentIndexEarliestPresentationTimeUs;
            if (j != C.TIME_UNSET) {
                long sampleTimeUs2 = j + presentationTimeDeltaUs;
                TimestampAdjuster timestampAdjuster2 = this.timestampAdjuster;
                if (timestampAdjuster2 != null) {
                    sampleTimeUs = timestampAdjuster2.adjustSampleTimestamp(sampleTimeUs2);
                } else {
                    sampleTimeUs = sampleTimeUs2;
                }
                TrackOutput[] trackOutputArr2 = this.emsgTrackOutputs;
                int i = 0;
                for (int length = trackOutputArr2.length; i < length; length = length) {
                    trackOutputArr2[i].sampleMetadata(sampleTimeUs, 1, sampleSize2, 0, (TrackOutput.CryptoData) null);
                    i++;
                }
                return;
            }
            this.pendingMetadataSampleInfos.addLast(new MetadataSampleInfo(presentationTimeDeltaUs, sampleSize2));
            this.pendingMetadataSampleBytes += sampleSize2;
        }
    }

    private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray trex) {
        trex.setPosition(12);
        return Pair.create(Integer.valueOf(trex.readInt()), new DefaultSampleValues(trex.readUnsignedIntToInt() - 1, trex.readUnsignedIntToInt(), trex.readUnsignedIntToInt(), trex.readInt()));
    }

    private static long parseMehd(ParsableByteArray mehd) {
        mehd.setPosition(8);
        return Atom.parseFullAtomVersion(mehd.readInt()) == 0 ? mehd.readUnsignedInt() : mehd.readUnsignedLongToLong();
    }

    private static void parseMoof(Atom.ContainerAtom moof, SparseArray<TrackBundle> trackBundleArray, int flags2, byte[] extendedTypeScratch2) throws ParserException {
        int moofContainerChildrenSize = moof.containerChildren.size();
        for (int i = 0; i < moofContainerChildrenSize; i++) {
            Atom.ContainerAtom child = moof.containerChildren.get(i);
            if (child.type == Atom.TYPE_traf) {
                parseTraf(child, trackBundleArray, flags2, extendedTypeScratch2);
            }
        }
    }

    private static void parseTraf(Atom.ContainerAtom traf, SparseArray<TrackBundle> trackBundleArray, int flags2, byte[] extendedTypeScratch2) throws ParserException {
        String str;
        Atom.ContainerAtom containerAtom = traf;
        int i = flags2;
        Atom.LeafAtom tfhd = containerAtom.getLeafAtomOfType(Atom.TYPE_tfhd);
        TrackBundle trackBundle = parseTfhd(tfhd.data, trackBundleArray);
        if (trackBundle != null) {
            TrackFragment fragment = trackBundle.fragment;
            long decodeTime = fragment.nextFragmentDecodeTime;
            trackBundle.reset();
            if (containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null && (i & 2) == 0) {
                decodeTime = parseTfdt(containerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
            }
            parseTruns(containerAtom, trackBundle, decodeTime, i);
            TrackEncryptionBox encryptionBox = trackBundle.track.getSampleDescriptionEncryptionBox(fragment.header.sampleDescriptionIndex);
            Atom.LeafAtom saiz = containerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
            if (saiz != null) {
                parseSaiz(encryptionBox, saiz.data, fragment);
            }
            Atom.LeafAtom saio = containerAtom.getLeafAtomOfType(Atom.TYPE_saio);
            if (saio != null) {
                parseSaio(saio.data, fragment);
            }
            Atom.LeafAtom senc = containerAtom.getLeafAtomOfType(Atom.TYPE_senc);
            if (senc != null) {
                parseSenc(senc.data, fragment);
            }
            Atom.LeafAtom sbgp = containerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
            Atom.LeafAtom sgpd = containerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
            if (sbgp == null || sgpd == null) {
            } else {
                ParsableByteArray parsableByteArray = sbgp.data;
                ParsableByteArray parsableByteArray2 = sgpd.data;
                if (encryptionBox != null) {
                    Atom.LeafAtom leafAtom = tfhd;
                    str = encryptionBox.schemeType;
                } else {
                    str = null;
                }
                parseSgpd(parsableByteArray, parsableByteArray2, str, fragment);
            }
            int leafChildrenSize = containerAtom.leafChildren.size();
            int i2 = 0;
            while (i2 < leafChildrenSize) {
                Atom.LeafAtom atom = containerAtom.leafChildren.get(i2);
                int leafChildrenSize2 = leafChildrenSize;
                if (atom.type == Atom.TYPE_uuid) {
                    parseUuid(atom.data, fragment, extendedTypeScratch2);
                } else {
                    byte[] bArr = extendedTypeScratch2;
                }
                i2++;
                containerAtom = traf;
                leafChildrenSize = leafChildrenSize2;
            }
        }
    }

    private static void parseTruns(Atom.ContainerAtom traf, TrackBundle trackBundle, long decodeTime, int flags2) {
        TrackBundle trackBundle2 = trackBundle;
        List<Atom.LeafAtom> leafChildren = traf.leafChildren;
        int leafChildrenSize = leafChildren.size();
        int trunCount = 0;
        int totalSampleCount = 0;
        for (int i = 0; i < leafChildrenSize; i++) {
            Atom.LeafAtom atom = leafChildren.get(i);
            if (atom.type == Atom.TYPE_trun) {
                ParsableByteArray trunData = atom.data;
                trunData.setPosition(12);
                int trunSampleCount = trunData.readUnsignedIntToInt();
                if (trunSampleCount > 0) {
                    totalSampleCount += trunSampleCount;
                    trunCount++;
                }
            }
        }
        trackBundle2.currentTrackRunIndex = 0;
        trackBundle2.currentSampleInTrackRun = 0;
        trackBundle2.currentSampleIndex = 0;
        trackBundle2.fragment.initTables(trunCount, totalSampleCount);
        int trunStartPosition = 0;
        int trunIndex = 0;
        for (int i2 = 0; i2 < leafChildrenSize; i2++) {
            Atom.LeafAtom trun = leafChildren.get(i2);
            if (trun.type == Atom.TYPE_trun) {
                trunStartPosition = parseTrun(trackBundle, trunIndex, decodeTime, flags2, trun.data, trunStartPosition);
                trunIndex++;
            }
        }
    }

    private static void parseSaiz(TrackEncryptionBox encryptionBox, ParsableByteArray saiz, TrackFragment out) throws ParserException {
        int vectorSize = encryptionBox.perSampleIvSize;
        saiz.setPosition(8);
        boolean subsampleEncryption = true;
        if ((Atom.parseFullAtomFlags(saiz.readInt()) & 1) == 1) {
            saiz.skipBytes(8);
        }
        int defaultSampleInfoSize = saiz.readUnsignedByte();
        int sampleCount = saiz.readUnsignedIntToInt();
        if (sampleCount == out.sampleCount) {
            int totalSize = 0;
            if (defaultSampleInfoSize == 0) {
                boolean[] sampleHasSubsampleEncryptionTable = out.sampleHasSubsampleEncryptionTable;
                for (int i = 0; i < sampleCount; i++) {
                    int sampleInfoSize = saiz.readUnsignedByte();
                    totalSize += sampleInfoSize;
                    sampleHasSubsampleEncryptionTable[i] = sampleInfoSize > vectorSize;
                }
            } else {
                if (defaultSampleInfoSize <= vectorSize) {
                    subsampleEncryption = false;
                }
                totalSize = 0 + (defaultSampleInfoSize * sampleCount);
                Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
            }
            out.initEncryptionData(totalSize);
            return;
        }
        throw new ParserException("Length mismatch: " + sampleCount + ", " + out.sampleCount);
    }

    private static void parseSaio(ParsableByteArray saio, TrackFragment out) throws ParserException {
        saio.setPosition(8);
        int fullAtom = saio.readInt();
        if ((Atom.parseFullAtomFlags(fullAtom) & 1) == 1) {
            saio.skipBytes(8);
        }
        int entryCount = saio.readUnsignedIntToInt();
        if (entryCount == 1) {
            out.auxiliaryDataPosition += Atom.parseFullAtomVersion(fullAtom) == 0 ? saio.readUnsignedInt() : saio.readUnsignedLongToLong();
            return;
        }
        throw new ParserException("Unexpected saio entry count: " + entryCount);
    }

    private static TrackBundle parseTfhd(ParsableByteArray tfhd, SparseArray<TrackBundle> trackBundles2) {
        tfhd.setPosition(8);
        int atomFlags = Atom.parseFullAtomFlags(tfhd.readInt());
        TrackBundle trackBundle = getTrackBundle(trackBundles2, tfhd.readInt());
        if (trackBundle == null) {
            return null;
        }
        if ((atomFlags & 1) != 0) {
            long baseDataPosition = tfhd.readUnsignedLongToLong();
            trackBundle.fragment.dataPosition = baseDataPosition;
            trackBundle.fragment.auxiliaryDataPosition = baseDataPosition;
        }
        DefaultSampleValues defaultSampleValues = trackBundle.defaultSampleValues;
        trackBundle.fragment.header = new DefaultSampleValues((atomFlags & 2) != 0 ? tfhd.readUnsignedIntToInt() - 1 : defaultSampleValues.sampleDescriptionIndex, (atomFlags & 8) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.duration, (atomFlags & 16) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.size, (atomFlags & 32) != 0 ? tfhd.readUnsignedIntToInt() : defaultSampleValues.flags);
        return trackBundle;
    }

    private static TrackBundle getTrackBundle(SparseArray<TrackBundle> trackBundles2, int trackId) {
        if (trackBundles2.size() == 1) {
            return trackBundles2.valueAt(0);
        }
        return trackBundles2.get(trackId);
    }

    private static long parseTfdt(ParsableByteArray tfdt) {
        tfdt.setPosition(8);
        return Atom.parseFullAtomVersion(tfdt.readInt()) == 1 ? tfdt.readUnsignedLongToLong() : tfdt.readUnsignedInt();
    }

    private static int parseTrun(TrackBundle trackBundle, int index, long decodeTime, int flags2, ParsableByteArray trun, int trackRunStart) {
        int firstSampleFlags;
        long cumulativeTime;
        boolean sampleDurationsPresent;
        int i;
        boolean sampleSizesPresent;
        int i2;
        boolean firstSampleFlagsPresent;
        int i3;
        boolean sampleCompositionTimeOffsetsPresent;
        boolean sampleFlagsPresent;
        DefaultSampleValues defaultSampleValues;
        TrackBundle trackBundle2 = trackBundle;
        trun.setPosition(8);
        int fullAtom = trun.readInt();
        int atomFlags = Atom.parseFullAtomFlags(fullAtom);
        Track track = trackBundle2.track;
        TrackFragment fragment = trackBundle2.fragment;
        DefaultSampleValues defaultSampleValues2 = fragment.header;
        fragment.trunLength[index] = trun.readUnsignedIntToInt();
        fragment.trunDataPosition[index] = fragment.dataPosition;
        if ((atomFlags & 1) != 0) {
            long[] jArr = fragment.trunDataPosition;
            jArr[index] = jArr[index] + ((long) trun.readInt());
        }
        boolean firstSampleFlagsPresent2 = (atomFlags & 4) != 0;
        int firstSampleFlags2 = defaultSampleValues2.flags;
        if (firstSampleFlagsPresent2) {
            firstSampleFlags2 = trun.readUnsignedIntToInt();
        }
        boolean sampleDurationsPresent2 = (atomFlags & 256) != 0;
        boolean sampleSizesPresent2 = (atomFlags & 512) != 0;
        boolean sampleFlagsPresent2 = (atomFlags & 1024) != 0;
        boolean sampleCompositionTimeOffsetsPresent2 = (atomFlags & 2048) != 0;
        long edtsOffset = 0;
        if (track.editListDurations != null && track.editListDurations.length == 1 && track.editListDurations[0] == 0) {
            firstSampleFlags = firstSampleFlags2;
            edtsOffset = Util.scaleLargeTimestamp(track.editListMediaTimes[0], 1000, track.timescale);
        } else {
            firstSampleFlags = firstSampleFlags2;
        }
        int[] sampleSizeTable = fragment.sampleSizeTable;
        int[] sampleCompositionTimeOffsetTable = fragment.sampleCompositionTimeOffsetTable;
        long[] sampleDecodingTimeTable = fragment.sampleDecodingTimeTable;
        boolean[] sampleIsSyncFrameTable = fragment.sampleIsSyncFrameTable;
        int i4 = fullAtom;
        boolean workaroundEveryVideoFrameIsSyncFrame = track.type == 2 && (flags2 & 1) != 0;
        int trackRunEnd = trackRunStart + fragment.trunLength[index];
        boolean[] sampleIsSyncFrameTable2 = sampleIsSyncFrameTable;
        boolean workaroundEveryVideoFrameIsSyncFrame2 = workaroundEveryVideoFrameIsSyncFrame;
        long timescale = track.timescale;
        if (index > 0) {
            int i5 = atomFlags;
            Track track2 = track;
            cumulativeTime = fragment.nextFragmentDecodeTime;
        } else {
            Track track3 = track;
            cumulativeTime = decodeTime;
        }
        TrackFragment fragment2 = fragment;
        int i6 = trackRunStart;
        while (i6 < trackRunEnd) {
            if (sampleDurationsPresent2) {
                i = trun.readUnsignedIntToInt();
                sampleDurationsPresent = sampleDurationsPresent2;
            } else {
                sampleDurationsPresent = sampleDurationsPresent2;
                i = defaultSampleValues2.duration;
            }
            int sampleDuration = i;
            if (sampleSizesPresent2) {
                i2 = trun.readUnsignedIntToInt();
                sampleSizesPresent = sampleSizesPresent2;
            } else {
                sampleSizesPresent = sampleSizesPresent2;
                i2 = defaultSampleValues2.size;
            }
            int sampleSize2 = i2;
            if (i6 == 0 && firstSampleFlagsPresent2) {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                i3 = firstSampleFlags;
            } else if (sampleFlagsPresent2) {
                i3 = trun.readInt();
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
            } else {
                firstSampleFlagsPresent = firstSampleFlagsPresent2;
                i3 = defaultSampleValues2.flags;
            }
            int sampleFlags = i3;
            if (sampleCompositionTimeOffsetsPresent2) {
                defaultSampleValues = defaultSampleValues2;
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetsPresent = sampleCompositionTimeOffsetsPresent2;
                sampleCompositionTimeOffsetTable[i6] = (int) ((((long) trun.readInt()) * 1000) / timescale);
            } else {
                defaultSampleValues = defaultSampleValues2;
                sampleFlagsPresent = sampleFlagsPresent2;
                sampleCompositionTimeOffsetsPresent = sampleCompositionTimeOffsetsPresent2;
                sampleCompositionTimeOffsetTable[i6] = 0;
            }
            sampleDecodingTimeTable[i6] = Util.scaleLargeTimestamp(cumulativeTime, 1000, timescale) - edtsOffset;
            sampleSizeTable[i6] = sampleSize2;
            sampleIsSyncFrameTable2[i6] = ((sampleFlags >> 16) & 1) == 0 && (!workaroundEveryVideoFrameIsSyncFrame2 || i6 == 0);
            cumulativeTime += (long) sampleDuration;
            i6++;
            sampleDurationsPresent2 = sampleDurationsPresent;
            sampleSizesPresent2 = sampleSizesPresent;
            firstSampleFlagsPresent2 = firstSampleFlagsPresent;
            defaultSampleValues2 = defaultSampleValues;
            sampleFlagsPresent2 = sampleFlagsPresent;
            sampleCompositionTimeOffsetsPresent2 = sampleCompositionTimeOffsetsPresent;
        }
        fragment2.nextFragmentDecodeTime = cumulativeTime;
        return trackRunEnd;
    }

    private static void parseUuid(ParsableByteArray uuid, TrackFragment out, byte[] extendedTypeScratch2) throws ParserException {
        uuid.setPosition(8);
        uuid.readBytes(extendedTypeScratch2, 0, 16);
        if (Arrays.equals(extendedTypeScratch2, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE)) {
            parseSenc(uuid, 16, out);
        }
    }

    private static void parseSenc(ParsableByteArray senc, TrackFragment out) throws ParserException {
        parseSenc(senc, 0, out);
    }

    private static void parseSenc(ParsableByteArray senc, int offset, TrackFragment out) throws ParserException {
        senc.setPosition(offset + 8);
        int flags2 = Atom.parseFullAtomFlags(senc.readInt());
        if ((flags2 & 1) == 0) {
            boolean subsampleEncryption = (flags2 & 2) != 0;
            int sampleCount = senc.readUnsignedIntToInt();
            if (sampleCount == out.sampleCount) {
                Arrays.fill(out.sampleHasSubsampleEncryptionTable, 0, sampleCount, subsampleEncryption);
                out.initEncryptionData(senc.bytesLeft());
                out.fillEncryptionData(senc);
                return;
            }
            throw new ParserException("Length mismatch: " + sampleCount + ", " + out.sampleCount);
        }
        throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
    }

    private static void parseSgpd(ParsableByteArray sbgp, ParsableByteArray sgpd, String schemeType, TrackFragment out) throws ParserException {
        byte[] constantIv;
        ParsableByteArray parsableByteArray = sbgp;
        ParsableByteArray parsableByteArray2 = sgpd;
        TrackFragment trackFragment = out;
        parsableByteArray.setPosition(8);
        int sbgpFullAtom = sbgp.readInt();
        if (sbgp.readInt() == SAMPLE_GROUP_TYPE_seig) {
            if (Atom.parseFullAtomVersion(sbgpFullAtom) == 1) {
                parsableByteArray.skipBytes(4);
            }
            if (sbgp.readInt() == 1) {
                parsableByteArray2.setPosition(8);
                int sgpdFullAtom = sgpd.readInt();
                if (sgpd.readInt() == SAMPLE_GROUP_TYPE_seig) {
                    int sgpdVersion = Atom.parseFullAtomVersion(sgpdFullAtom);
                    if (sgpdVersion == 1) {
                        if (sgpd.readUnsignedInt() == 0) {
                            throw new ParserException("Variable length description in sgpd found (unsupported)");
                        }
                    } else if (sgpdVersion >= 2) {
                        parsableByteArray2.skipBytes(4);
                    }
                    if (sgpd.readUnsignedInt() == 1) {
                        parsableByteArray2.skipBytes(1);
                        int patternByte = sgpd.readUnsignedByte();
                        int cryptByteBlock = (patternByte & PsExtractor.VIDEO_STREAM_MASK) >> 4;
                        int skipByteBlock = patternByte & 15;
                        boolean isProtected = sgpd.readUnsignedByte() == 1;
                        if (isProtected) {
                            int perSampleIvSize = sgpd.readUnsignedByte();
                            byte[] keyId = new byte[16];
                            parsableByteArray2.readBytes(keyId, 0, keyId.length);
                            if (!isProtected || perSampleIvSize != 0) {
                                constantIv = null;
                            } else {
                                int constantIvSize = sgpd.readUnsignedByte();
                                byte[] constantIv2 = new byte[constantIvSize];
                                parsableByteArray2.readBytes(constantIv2, 0, constantIvSize);
                                constantIv = constantIv2;
                            }
                            trackFragment.definesEncryptionData = true;
                            byte[] bArr = keyId;
                            trackFragment.trackEncryptionBox = new TrackEncryptionBox(isProtected, schemeType, perSampleIvSize, keyId, cryptByteBlock, skipByteBlock, constantIv);
                            return;
                        }
                        return;
                    }
                    throw new ParserException("Entry count in sgpd != 1 (unsupported).");
                }
                return;
            }
            throw new ParserException("Entry count in sbgp != 1 (unsupported).");
        }
    }

    private static Pair<Long, ChunkIndex> parseSidx(ParsableByteArray atom, long inputPosition) throws ParserException {
        long earliestPresentationTime;
        long offset;
        ParsableByteArray parsableByteArray = atom;
        parsableByteArray.setPosition(8);
        int fullAtom = atom.readInt();
        int version = Atom.parseFullAtomVersion(fullAtom);
        parsableByteArray.skipBytes(4);
        long timescale = atom.readUnsignedInt();
        long offset2 = inputPosition;
        if (version == 0) {
            long earliestPresentationTime2 = atom.readUnsignedInt();
            offset = offset2 + atom.readUnsignedInt();
            earliestPresentationTime = earliestPresentationTime2;
        } else {
            long earliestPresentationTime3 = atom.readUnsignedLongToLong();
            offset = offset2 + atom.readUnsignedLongToLong();
            earliestPresentationTime = earliestPresentationTime3;
        }
        long earliestPresentationTimeUs = Util.scaleLargeTimestamp(earliestPresentationTime, 1000000, timescale);
        parsableByteArray.skipBytes(2);
        int referenceCount = atom.readUnsignedShort();
        int[] sizes = new int[referenceCount];
        long[] offsets = new long[referenceCount];
        long[] durationsUs = new long[referenceCount];
        long[] timesUs = new long[referenceCount];
        long timeUs = earliestPresentationTimeUs;
        long time = earliestPresentationTime;
        long offset3 = offset;
        int i = 0;
        while (i < referenceCount) {
            int firstInt = atom.readInt();
            if ((firstInt & Integer.MIN_VALUE) == 0) {
                long referenceDuration = atom.readUnsignedInt();
                sizes[i] = Integer.MAX_VALUE & firstInt;
                offsets[i] = offset3;
                timesUs[i] = timeUs;
                time += referenceDuration;
                long[] timesUs2 = timesUs;
                int version2 = version;
                long[] durationsUs2 = durationsUs;
                int i2 = firstInt;
                int[] sizes2 = sizes;
                timeUs = Util.scaleLargeTimestamp(time, 1000000, timescale);
                durationsUs2[i] = timeUs - timesUs2[i];
                parsableByteArray.skipBytes(4);
                offset3 += (long) sizes2[i];
                i++;
                offsets = offsets;
                durationsUs = durationsUs2;
                timesUs = timesUs2;
                sizes = sizes2;
                referenceCount = referenceCount;
                fullAtom = fullAtom;
                version = version2;
            } else {
                throw new ParserException("Unhandled indirect reference");
            }
        }
        int i3 = fullAtom;
        int i4 = version;
        return Pair.create(Long.valueOf(earliestPresentationTimeUs), new ChunkIndex(sizes, offsets, durationsUs, timesUs));
    }

    private void readEncryptionData(ExtractorInput input) throws IOException, InterruptedException {
        TrackBundle nextTrackBundle = null;
        long nextDataOffset = Long.MAX_VALUE;
        int trackBundlesSize = this.trackBundles.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackFragment trackFragment = this.trackBundles.valueAt(i).fragment;
            if (trackFragment.sampleEncryptionDataNeedsFill && trackFragment.auxiliaryDataPosition < nextDataOffset) {
                nextDataOffset = trackFragment.auxiliaryDataPosition;
                nextTrackBundle = this.trackBundles.valueAt(i);
            }
        }
        if (nextTrackBundle == null) {
            this.parserState = 3;
            return;
        }
        int bytesToSkip = (int) (nextDataOffset - input.getPosition());
        if (bytesToSkip >= 0) {
            input.skipFully(bytesToSkip);
            nextTrackBundle.fragment.fillEncryptionData(input);
            return;
        }
        throw new ParserException("Offset to encryption data was negative.");
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=boolean, code=int, for r3v2, types: [boolean] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean readSample(com.google.android.exoplayer2.extractor.ExtractorInput r20) throws java.io.IOException, java.lang.InterruptedException {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            int r2 = r0.parserState
            r3 = 0
            r4 = 3
            r5 = 4
            r6 = 1
            r7 = 0
            if (r2 != r4) goto L_0x00a2
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            if (r2 != 0) goto L_0x0050
            android.util.SparseArray<com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle> r2 = r0.trackBundles
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = getNextFragmentRun(r2)
            if (r2 != 0) goto L_0x0032
            long r3 = r0.endOfMdatPosition
            long r5 = r20.getPosition()
            long r3 = r3 - r5
            int r4 = (int) r3
            if (r4 < 0) goto L_0x002a
            r1.skipFully(r4)
            r19.enterReadingAtomHeaderState()
            return r7
        L_0x002a:
            com.google.android.exoplayer2.ParserException r3 = new com.google.android.exoplayer2.ParserException
            java.lang.String r5 = "Offset to end of mdat was negative."
            r3.<init>((java.lang.String) r5)
            throw r3
        L_0x0032:
            com.google.android.exoplayer2.extractor.mp4.TrackFragment r8 = r2.fragment
            long[] r8 = r8.trunDataPosition
            int r9 = r2.currentTrackRunIndex
            r9 = r8[r9]
            long r11 = r20.getPosition()
            long r11 = r9 - r11
            int r8 = (int) r11
            if (r8 >= 0) goto L_0x004b
            java.lang.String r11 = "FragmentedMp4Extractor"
            java.lang.String r12 = "Ignoring negative offset to sample data."
            com.google.android.exoplayer2.util.Log.w(r11, r12)
            r8 = 0
        L_0x004b:
            r1.skipFully(r8)
            r0.currentTrackBundle = r2
        L_0x0050:
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.mp4.TrackFragment r2 = r2.fragment
            int[] r2 = r2.sampleSizeTable
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r8 = r0.currentTrackBundle
            int r8 = r8.currentSampleIndex
            r2 = r2[r8]
            r0.sampleSize = r2
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            int r2 = r2.currentSampleIndex
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r8 = r0.currentTrackBundle
            int r8 = r8.firstSampleToOutputIndex
            if (r2 >= r8) goto L_0x007f
            int r2 = r0.sampleSize
            r1.skipFully(r2)
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            r2.skipSampleEncryptionData()
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            boolean r2 = r2.next()
            if (r2 != 0) goto L_0x007c
            r0.currentTrackBundle = r3
        L_0x007c:
            r0.parserState = r4
            return r6
        L_0x007f:
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.mp4.Track r2 = r2.track
            int r2 = r2.sampleTransformation
            if (r2 != r6) goto L_0x0091
            int r2 = r0.sampleSize
            r8 = 8
            int r2 = r2 - r8
            r0.sampleSize = r2
            r1.skipFully(r8)
        L_0x0091:
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            int r2 = r2.outputSampleEncryptionData()
            r0.sampleBytesWritten = r2
            int r8 = r0.sampleSize
            int r8 = r8 + r2
            r0.sampleSize = r8
            r0.parserState = r5
            r0.sampleCurrentNalBytesRemaining = r7
        L_0x00a2:
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r2 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.mp4.TrackFragment r2 = r2.fragment
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r8 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.mp4.Track r8 = r8.track
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r9 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.TrackOutput r9 = r9.output
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r10 = r0.currentTrackBundle
            int r15 = r10.currentSampleIndex
            long r10 = r2.getSamplePresentationTime(r15)
            r12 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 * r12
            com.google.android.exoplayer2.util.TimestampAdjuster r12 = r0.timestampAdjuster
            if (r12 == 0) goto L_0x00c4
            long r10 = r12.adjustSampleTimestamp(r10)
            r13 = r10
            goto L_0x00c5
        L_0x00c4:
            r13 = r10
        L_0x00c5:
            int r10 = r8.nalUnitLengthFieldLength
            if (r10 == 0) goto L_0x0186
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r0.nalPrefix
            byte[] r10 = r10.data
            r10[r7] = r7
            r10[r6] = r7
            r11 = 2
            r10[r11] = r7
            int r11 = r8.nalUnitLengthFieldLength
            int r11 = r11 + r6
            int r12 = r8.nalUnitLengthFieldLength
            int r12 = 4 - r12
        L_0x00db:
            int r4 = r0.sampleBytesWritten
            int r3 = r0.sampleSize
            if (r4 >= r3) goto L_0x0185
            int r3 = r0.sampleCurrentNalBytesRemaining
            if (r3 != 0) goto L_0x0129
            r1.readFully(r10, r12, r11)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalPrefix
            r3.setPosition(r7)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalPrefix
            int r3 = r3.readUnsignedIntToInt()
            int r3 = r3 - r6
            r0.sampleCurrentNalBytesRemaining = r3
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalStartCode
            r3.setPosition(r7)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalStartCode
            r9.sampleData(r3, r5)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalPrefix
            r9.sampleData(r3, r6)
            com.google.android.exoplayer2.extractor.TrackOutput[] r3 = r0.cea608TrackOutputs
            int r3 = r3.length
            if (r3 <= 0) goto L_0x0118
            com.google.android.exoplayer2.Format r3 = r8.format
            java.lang.String r3 = r3.sampleMimeType
            byte r4 = r10[r5]
            boolean r3 = com.google.android.exoplayer2.util.NalUnitUtil.isNalUnitSei(r3, r4)
            if (r3 == 0) goto L_0x0118
            r3 = 1
            goto L_0x0119
        L_0x0118:
            r3 = 0
        L_0x0119:
            r0.processSeiNalUnitPayload = r3
            int r3 = r0.sampleBytesWritten
            int r3 = r3 + 5
            r0.sampleBytesWritten = r3
            int r3 = r0.sampleSize
            int r3 = r3 + r12
            r0.sampleSize = r3
            r3 = 0
            r4 = 3
            goto L_0x00db
        L_0x0129:
            boolean r4 = r0.processSeiNalUnitPayload
            if (r4 == 0) goto L_0x016f
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r0.nalBuffer
            r4.reset((int) r3)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalBuffer
            byte[] r3 = r3.data
            int r4 = r0.sampleCurrentNalBytesRemaining
            r1.readFully(r3, r7, r4)
            com.google.android.exoplayer2.util.ParsableByteArray r3 = r0.nalBuffer
            int r4 = r0.sampleCurrentNalBytesRemaining
            r9.sampleData(r3, r4)
            int r3 = r0.sampleCurrentNalBytesRemaining
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r0.nalBuffer
            byte[] r4 = r4.data
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r0.nalBuffer
            int r5 = r5.limit()
            int r4 = com.google.android.exoplayer2.util.NalUnitUtil.unescapeStream(r4, r5)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r0.nalBuffer
            com.google.android.exoplayer2.Format r6 = r8.format
            java.lang.String r6 = r6.sampleMimeType
            java.lang.String r7 = "video/hevc"
            boolean r6 = r7.equals(r6)
            r5.setPosition(r6)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r0.nalBuffer
            r5.setLimit(r4)
            com.google.android.exoplayer2.util.ParsableByteArray r5 = r0.nalBuffer
            com.google.android.exoplayer2.extractor.TrackOutput[] r6 = r0.cea608TrackOutputs
            com.google.android.exoplayer2.text.cea.CeaUtil.consume(r13, r5, r6)
            goto L_0x0174
        L_0x016f:
            r4 = 0
            int r3 = r9.sampleData(r1, r3, r4)
        L_0x0174:
            int r4 = r0.sampleBytesWritten
            int r4 = r4 + r3
            r0.sampleBytesWritten = r4
            int r4 = r0.sampleCurrentNalBytesRemaining
            int r4 = r4 - r3
            r0.sampleCurrentNalBytesRemaining = r4
            r3 = 0
            r4 = 3
            r5 = 4
            r6 = 1
            r7 = 0
            goto L_0x00db
        L_0x0185:
            goto L_0x0198
        L_0x0186:
            int r3 = r0.sampleBytesWritten
            int r4 = r0.sampleSize
            if (r3 >= r4) goto L_0x0198
            int r4 = r4 - r3
            r3 = 0
            int r4 = r9.sampleData(r1, r4, r3)
            int r5 = r0.sampleBytesWritten
            int r5 = r5 + r4
            r0.sampleBytesWritten = r5
            goto L_0x0186
        L_0x0198:
            boolean[] r3 = r2.sampleIsSyncFrameTable
            boolean r3 = r3[r15]
            r4 = 0
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r5 = r0.currentTrackBundle
            com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox r5 = r5.getEncryptionBoxIfEncrypted()
            if (r5 == 0) goto L_0x01aa
            r6 = 1073741824(0x40000000, float:2.0)
            r3 = r3 | r6
            com.google.android.exoplayer2.extractor.TrackOutput$CryptoData r4 = r5.cryptoData
        L_0x01aa:
            int r6 = r0.sampleSize
            r7 = 0
            r10 = r9
            r11 = r13
            r17 = r13
            r13 = r3
            r14 = r6
            r6 = r15
            r15 = r7
            r16 = r4
            r10.sampleMetadata(r11, r13, r14, r15, r16)
            r10 = r17
            r0.outputPendingMetadataSamples(r10)
            com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor$TrackBundle r7 = r0.currentTrackBundle
            boolean r7 = r7.next()
            if (r7 != 0) goto L_0x01ca
            r7 = 0
            r0.currentTrackBundle = r7
        L_0x01ca:
            r7 = 3
            r0.parserState = r7
            r7 = 1
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor.readSample(com.google.android.exoplayer2.extractor.ExtractorInput):boolean");
    }

    private void outputPendingMetadataSamples(long sampleTimeUs) {
        while (!this.pendingMetadataSampleInfos.isEmpty()) {
            MetadataSampleInfo sampleInfo = this.pendingMetadataSampleInfos.removeFirst();
            this.pendingMetadataSampleBytes -= sampleInfo.size;
            long metadataTimeUs = sampleTimeUs + sampleInfo.presentationTimeDeltaUs;
            TimestampAdjuster timestampAdjuster2 = this.timestampAdjuster;
            if (timestampAdjuster2 != null) {
                metadataTimeUs = timestampAdjuster2.adjustSampleTimestamp(metadataTimeUs);
            }
            for (TrackOutput emsgTrackOutput : this.emsgTrackOutputs) {
                emsgTrackOutput.sampleMetadata(metadataTimeUs, 1, sampleInfo.size, this.pendingMetadataSampleBytes, (TrackOutput.CryptoData) null);
            }
        }
    }

    private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> trackBundles2) {
        TrackBundle nextTrackBundle = null;
        long nextTrackRunOffset = Long.MAX_VALUE;
        int trackBundlesSize = trackBundles2.size();
        for (int i = 0; i < trackBundlesSize; i++) {
            TrackBundle trackBundle = trackBundles2.valueAt(i);
            if (trackBundle.currentTrackRunIndex != trackBundle.fragment.trunCount) {
                long trunOffset = trackBundle.fragment.trunDataPosition[trackBundle.currentTrackRunIndex];
                if (trunOffset < nextTrackRunOffset) {
                    nextTrackBundle = trackBundle;
                    nextTrackRunOffset = trunOffset;
                }
            }
        }
        return nextTrackBundle;
    }

    private static DrmInitData getDrmInitDataFromAtoms(List<Atom.LeafAtom> leafChildren) {
        ArrayList<DrmInitData.SchemeData> schemeDatas = null;
        int leafChildrenSize = leafChildren.size();
        for (int i = 0; i < leafChildrenSize; i++) {
            Atom.LeafAtom child = leafChildren.get(i);
            if (child.type == Atom.TYPE_pssh) {
                if (schemeDatas == null) {
                    schemeDatas = new ArrayList<>();
                }
                byte[] psshData = child.data.data;
                UUID uuid = PsshAtomUtil.parseUuid(psshData);
                if (uuid == null) {
                    Log.w(TAG, "Skipped pssh atom (failed to extract uuid)");
                } else {
                    schemeDatas.add(new DrmInitData.SchemeData(uuid, MimeTypes.VIDEO_MP4, psshData));
                }
            }
        }
        if (schemeDatas == null) {
            return null;
        }
        return new DrmInitData((List<DrmInitData.SchemeData>) schemeDatas);
    }

    private static boolean shouldParseLeafAtom(int atom) {
        return atom == Atom.TYPE_hdlr || atom == Atom.TYPE_mdhd || atom == Atom.TYPE_mvhd || atom == Atom.TYPE_sidx || atom == Atom.TYPE_stsd || atom == Atom.TYPE_tfdt || atom == Atom.TYPE_tfhd || atom == Atom.TYPE_tkhd || atom == Atom.TYPE_trex || atom == Atom.TYPE_trun || atom == Atom.TYPE_pssh || atom == Atom.TYPE_saiz || atom == Atom.TYPE_saio || atom == Atom.TYPE_senc || atom == Atom.TYPE_uuid || atom == Atom.TYPE_sbgp || atom == Atom.TYPE_sgpd || atom == Atom.TYPE_elst || atom == Atom.TYPE_mehd || atom == Atom.TYPE_emsg;
    }

    private static boolean shouldParseContainerAtom(int atom) {
        return atom == Atom.TYPE_moov || atom == Atom.TYPE_trak || atom == Atom.TYPE_mdia || atom == Atom.TYPE_minf || atom == Atom.TYPE_stbl || atom == Atom.TYPE_moof || atom == Atom.TYPE_traf || atom == Atom.TYPE_mvex || atom == Atom.TYPE_edts;
    }

    private static final class MetadataSampleInfo {
        public final long presentationTimeDeltaUs;
        public final int size;

        public MetadataSampleInfo(long presentationTimeDeltaUs2, int size2) {
            this.presentationTimeDeltaUs = presentationTimeDeltaUs2;
            this.size = size2;
        }
    }

    private static final class TrackBundle {
        public int currentSampleInTrackRun;
        public int currentSampleIndex;
        public int currentTrackRunIndex;
        private final ParsableByteArray defaultInitializationVector = new ParsableByteArray();
        public DefaultSampleValues defaultSampleValues;
        private final ParsableByteArray encryptionSignalByte = new ParsableByteArray(1);
        public int firstSampleToOutputIndex;
        public final TrackFragment fragment = new TrackFragment();
        public final TrackOutput output;
        public Track track;

        public TrackBundle(TrackOutput output2) {
            this.output = output2;
        }

        public void init(Track track2, DefaultSampleValues defaultSampleValues2) {
            this.track = (Track) Assertions.checkNotNull(track2);
            this.defaultSampleValues = (DefaultSampleValues) Assertions.checkNotNull(defaultSampleValues2);
            this.output.format(track2.format);
            reset();
        }

        public void updateDrmInitData(DrmInitData drmInitData) {
            TrackEncryptionBox encryptionBox = this.track.getSampleDescriptionEncryptionBox(this.fragment.header.sampleDescriptionIndex);
            this.output.format(this.track.format.copyWithDrmInitData(drmInitData.copyWithSchemeType(encryptionBox != null ? encryptionBox.schemeType : null)));
        }

        public void reset() {
            this.fragment.reset();
            this.currentSampleIndex = 0;
            this.currentTrackRunIndex = 0;
            this.currentSampleInTrackRun = 0;
            this.firstSampleToOutputIndex = 0;
        }

        public void seek(long timeUs) {
            long timeMs = C.usToMs(timeUs);
            int searchIndex = this.currentSampleIndex;
            while (searchIndex < this.fragment.sampleCount && this.fragment.getSamplePresentationTime(searchIndex) < timeMs) {
                if (this.fragment.sampleIsSyncFrameTable[searchIndex]) {
                    this.firstSampleToOutputIndex = searchIndex;
                }
                searchIndex++;
            }
        }

        public boolean next() {
            this.currentSampleIndex++;
            int i = this.currentSampleInTrackRun + 1;
            this.currentSampleInTrackRun = i;
            int[] iArr = this.fragment.trunLength;
            int i2 = this.currentTrackRunIndex;
            if (i != iArr[i2]) {
                return true;
            }
            this.currentTrackRunIndex = i2 + 1;
            this.currentSampleInTrackRun = 0;
            return false;
        }

        public int outputSampleEncryptionData() {
            int vectorSize;
            ParsableByteArray initializationVectorData;
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox == null) {
                return 0;
            }
            if (encryptionBox.perSampleIvSize != 0) {
                initializationVectorData = this.fragment.sampleEncryptionData;
                vectorSize = encryptionBox.perSampleIvSize;
            } else {
                byte[] initVectorData = encryptionBox.defaultInitializationVector;
                this.defaultInitializationVector.reset(initVectorData, initVectorData.length);
                ParsableByteArray initializationVectorData2 = this.defaultInitializationVector;
                int length = initVectorData.length;
                initializationVectorData = initializationVectorData2;
                vectorSize = length;
            }
            boolean subsampleEncryption = this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex);
            this.encryptionSignalByte.data[0] = (byte) ((subsampleEncryption ? 128 : 0) | vectorSize);
            this.encryptionSignalByte.setPosition(0);
            this.output.sampleData(this.encryptionSignalByte, 1);
            this.output.sampleData(initializationVectorData, vectorSize);
            if (!subsampleEncryption) {
                return vectorSize + 1;
            }
            ParsableByteArray subsampleEncryptionData = this.fragment.sampleEncryptionData;
            int subsampleCount = subsampleEncryptionData.readUnsignedShort();
            subsampleEncryptionData.skipBytes(-2);
            int subsampleDataLength = (subsampleCount * 6) + 2;
            this.output.sampleData(subsampleEncryptionData, subsampleDataLength);
            return vectorSize + 1 + subsampleDataLength;
        }

        /* access modifiers changed from: private */
        public void skipSampleEncryptionData() {
            TrackEncryptionBox encryptionBox = getEncryptionBoxIfEncrypted();
            if (encryptionBox != null) {
                ParsableByteArray sampleEncryptionData = this.fragment.sampleEncryptionData;
                if (encryptionBox.perSampleIvSize != 0) {
                    sampleEncryptionData.skipBytes(encryptionBox.perSampleIvSize);
                }
                if (this.fragment.sampleHasSubsampleEncryptionTable(this.currentSampleIndex)) {
                    sampleEncryptionData.skipBytes(sampleEncryptionData.readUnsignedShort() * 6);
                }
            }
        }

        /* access modifiers changed from: private */
        public TrackEncryptionBox getEncryptionBoxIfEncrypted() {
            TrackEncryptionBox encryptionBox;
            int sampleDescriptionIndex = this.fragment.header.sampleDescriptionIndex;
            if (this.fragment.trackEncryptionBox != null) {
                encryptionBox = this.fragment.trackEncryptionBox;
            } else {
                encryptionBox = this.track.getSampleDescriptionEncryptionBox(sampleDescriptionIndex);
            }
            if (encryptionBox == null || !encryptionBox.isEncrypted) {
                return null;
            }
            return encryptionBox;
        }
    }
}
