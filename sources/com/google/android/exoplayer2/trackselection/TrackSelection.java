package com.google.android.exoplayer2.trackselection;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.chunk.MediaChunkIterator;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import java.util.List;

public interface TrackSelection {
    boolean blacklist(int i, long j);

    void disable();

    void enable();

    int evaluateQueueSize(long j, List<? extends MediaChunk> list);

    Format getFormat(int i);

    int getIndexInTrackGroup(int i);

    Format getSelectedFormat();

    int getSelectedIndex();

    int getSelectedIndexInTrackGroup();

    Object getSelectionData();

    int getSelectionReason();

    TrackGroup getTrackGroup();

    int indexOf(int i);

    int indexOf(Format format);

    int length();

    void onDiscontinuity();

    void onPlaybackSpeed(float f);

    @Deprecated
    void updateSelectedTrack(long j, long j2, long j3);

    void updateSelectedTrack(long j, long j2, long j3, List<? extends MediaChunk> list, MediaChunkIterator[] mediaChunkIteratorArr);

    public static final class Definition {
        public final TrackGroup group;
        public final int[] tracks;

        public Definition(TrackGroup group2, int... tracks2) {
            this.group = group2;
            this.tracks = tracks2;
        }
    }

    public interface Factory {
        @Deprecated
        TrackSelection createTrackSelection(TrackGroup trackGroup, BandwidthMeter bandwidthMeter, int... iArr);

        TrackSelection[] createTrackSelections(Definition[] definitionArr, BandwidthMeter bandwidthMeter);

        /* renamed from: com.google.android.exoplayer2.trackselection.TrackSelection$Factory$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            @Deprecated
            public static TrackSelection $default$createTrackSelection(Factory _this, TrackGroup group, BandwidthMeter bandwidthMeter, int... tracks) {
                throw new UnsupportedOperationException();
            }

            public static TrackSelection[] $default$createTrackSelections(Factory _this, Definition[] definitions, BandwidthMeter bandwidthMeter) {
                return TrackSelectionUtil.createTrackSelectionsForDefinitions(definitions, 
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0009: RETURN  
                      (wrap: com.google.android.exoplayer2.trackselection.TrackSelection[] : 0x0005: INVOKE  (r0v1 com.google.android.exoplayer2.trackselection.TrackSelection[]) = 
                      (r2v0 'definitions' com.google.android.exoplayer2.trackselection.TrackSelection$Definition[])
                      (wrap: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA : 0x0002: CONSTRUCTOR  (r0v0 com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA) = 
                      (r1v0 '_this' com.google.android.exoplayer2.trackselection.TrackSelection$Factory)
                      (r3v0 'bandwidthMeter' com.google.android.exoplayer2.upstream.BandwidthMeter)
                     call: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA.<init>(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.upstream.BandwidthMeter):void type: CONSTRUCTOR)
                     com.google.android.exoplayer2.trackselection.TrackSelectionUtil.createTrackSelectionsForDefinitions(com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.trackselection.TrackSelectionUtil$AdaptiveTrackSelectionFactory):com.google.android.exoplayer2.trackselection.TrackSelection[] type: STATIC)
                     in method: com.google.android.exoplayer2.trackselection.TrackSelection.Factory.-CC.$default$createTrackSelections(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.upstream.BandwidthMeter):com.google.android.exoplayer2.trackselection.TrackSelection[], dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  (r0v1 com.google.android.exoplayer2.trackselection.TrackSelection[]) = 
                      (r2v0 'definitions' com.google.android.exoplayer2.trackselection.TrackSelection$Definition[])
                      (wrap: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA : 0x0002: CONSTRUCTOR  (r0v0 com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA) = 
                      (r1v0 '_this' com.google.android.exoplayer2.trackselection.TrackSelection$Factory)
                      (r3v0 'bandwidthMeter' com.google.android.exoplayer2.upstream.BandwidthMeter)
                     call: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA.<init>(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.upstream.BandwidthMeter):void type: CONSTRUCTOR)
                     com.google.android.exoplayer2.trackselection.TrackSelectionUtil.createTrackSelectionsForDefinitions(com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.trackselection.TrackSelectionUtil$AdaptiveTrackSelectionFactory):com.google.android.exoplayer2.trackselection.TrackSelection[] type: STATIC in method: com.google.android.exoplayer2.trackselection.TrackSelection.Factory.-CC.$default$createTrackSelections(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.upstream.BandwidthMeter):com.google.android.exoplayer2.trackselection.TrackSelection[], dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:314)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA) = 
                      (r1v0 '_this' com.google.android.exoplayer2.trackselection.TrackSelection$Factory)
                      (r3v0 'bandwidthMeter' com.google.android.exoplayer2.upstream.BandwidthMeter)
                     call: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA.<init>(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.upstream.BandwidthMeter):void type: CONSTRUCTOR in method: com.google.android.exoplayer2.trackselection.TrackSelection.Factory.-CC.$default$createTrackSelections(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.upstream.BandwidthMeter):com.google.android.exoplayer2.trackselection.TrackSelection[], dex: classes2.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 63 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 69 more
                    */
                /*
                    com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA r0 = new com.google.android.exoplayer2.trackselection.-$$Lambda$TrackSelection$Factory$9mnNWe-5kFFae0E_IiLXrOzpdtA
                    r0.<init>(r1, r3)
                    com.google.android.exoplayer2.trackselection.TrackSelection[] r0 = com.google.android.exoplayer2.trackselection.TrackSelectionUtil.createTrackSelectionsForDefinitions(r2, r0)
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.trackselection.TrackSelection.Factory.CC.$default$createTrackSelections(com.google.android.exoplayer2.trackselection.TrackSelection$Factory, com.google.android.exoplayer2.trackselection.TrackSelection$Definition[], com.google.android.exoplayer2.upstream.BandwidthMeter):com.google.android.exoplayer2.trackselection.TrackSelection[]");
            }
        }
    }

    /* renamed from: com.google.android.exoplayer2.trackselection.TrackSelection$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$onDiscontinuity(TrackSelection _this) {
        }

        @Deprecated
        public static void $default$updateSelectedTrack(TrackSelection _this, long playbackPositionUs, long bufferedDurationUs, long availableDurationUs) {
            throw new UnsupportedOperationException();
        }

        public static void $default$updateSelectedTrack(TrackSelection _this, long playbackPositionUs, long bufferedDurationUs, long availableDurationUs, List list, MediaChunkIterator[] mediaChunkIterators) {
            _this.updateSelectedTrack(playbackPositionUs, bufferedDurationUs, availableDurationUs);
        }
    }
}
