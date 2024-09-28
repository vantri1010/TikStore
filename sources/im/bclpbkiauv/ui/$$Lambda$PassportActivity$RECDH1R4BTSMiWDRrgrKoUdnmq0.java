package im.bclpbkiauv.ui;

import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.tgnet.RequestDelegate;
import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

/* renamed from: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0 INSTANCE = new $$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0();

    private /* synthetic */ $$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(
        /*  JADX ERROR: Method code generation error
            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0000: INVOKE  
              (r1v0 'tLObject' im.bclpbkiauv.tgnet.TLObject)
              (r2v0 'tL_error' im.bclpbkiauv.tgnet.TLRPC$TL_error)
             im.bclpbkiauv.ui.PassportActivity.lambda$new$1(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void type: STATIC in method: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0.run(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
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
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0005: INVOKE  
              (wrap: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50 : 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50) = (r1v0 'tLObject' im.bclpbkiauv.tgnet.TLObject) call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50.<init>(im.bclpbkiauv.tgnet.TLObject):void type: CONSTRUCTOR)
             im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0.run(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
            	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:924)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:684)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
            	... 29 more
            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: CONSTRUCTOR  (r0v0 im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50) = (r1v0 'tLObject' im.bclpbkiauv.tgnet.TLObject) call: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50.<init>(im.bclpbkiauv.tgnet.TLObject):void type: CONSTRUCTOR in method: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0.run(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void, dex: classes2.dex
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	... 33 more
            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: im.bclpbkiauv.ui.-$$Lambda$PassportActivity$rnAhDtxtuKmzT-kiBHj2eDNc_50, state: NOT_LOADED
            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
            	... 39 more
            */
        /*
            this = this;
            im.bclpbkiauv.messenger.AndroidUtilities.runOnUIThread(new im.bclpbkiauv.ui.$$Lambda$PassportActivity$rnAhDtxtuKmzTkiBHj2eDNc_50(r1))
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: im.bclpbkiauv.ui.$$Lambda$PassportActivity$RECDH1R4BTSMiWDRrgrKoUdnmq0.run(im.bclpbkiauv.tgnet.TLObject, im.bclpbkiauv.tgnet.TLRPC$TL_error):void");
    }
}
