package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequenceScope;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H@¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "T", "Lkotlin/sequences/SequenceScope;", "", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 15})
@DebugMetadata(c = "kotlin.collections.SlidingWindowKt$windowedIterator$1", f = "SlidingWindow.kt", i = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4}, l = {34, 40, 49, 55, 58}, m = "invokeSuspend", n = {"$this$iterator", "bufferInitialCapacity", "gap", "buffer", "skip", "e", "$this$iterator", "bufferInitialCapacity", "gap", "buffer", "skip", "$this$iterator", "bufferInitialCapacity", "gap", "buffer", "e", "$this$iterator", "bufferInitialCapacity", "gap", "buffer", "$this$iterator", "bufferInitialCapacity", "gap", "buffer"}, s = {"L$0", "I$0", "I$1", "L$1", "I$2", "L$2", "L$0", "I$0", "I$1", "L$1", "I$2", "L$0", "I$0", "I$1", "L$1", "L$2", "L$0", "I$0", "I$1", "L$1", "L$0", "I$0", "I$1", "L$1"})
/* compiled from: SlidingWindow.kt */
final class SlidingWindowKt$windowedIterator$1 extends RestrictedSuspendLambda implements Function2<SequenceScope<? super List<? extends T>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Iterator $iterator;
    final /* synthetic */ boolean $partialWindows;
    final /* synthetic */ boolean $reuseBuffer;
    final /* synthetic */ int $size;
    final /* synthetic */ int $step;
    int I$0;
    int I$1;
    int I$2;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    int label;
    private SequenceScope p$;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SlidingWindowKt$windowedIterator$1(int i, int i2, Iterator it, boolean z, boolean z2, Continuation continuation) {
        super(2, continuation);
        this.$size = i;
        this.$step = i2;
        this.$iterator = it;
        this.$reuseBuffer = z;
        this.$partialWindows = z2;
    }

    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        SlidingWindowKt$windowedIterator$1 slidingWindowKt$windowedIterator$1 = new SlidingWindowKt$windowedIterator$1(this.$size, this.$step, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
        SequenceScope sequenceScope = (SequenceScope) obj;
        slidingWindowKt$windowedIterator$1.p$ = (SequenceScope) obj;
        return slidingWindowKt$windowedIterator$1;
    }

    public final Object invoke(Object obj, Object obj2) {
        return ((SlidingWindowKt$windowedIterator$1) create(obj, (Continuation) obj2)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: kotlin.sequences.SequenceScope} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: kotlin.collections.RingBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: kotlin.sequences.SequenceScope} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v15, resolved type: kotlin.collections.RingBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: kotlin.sequences.SequenceScope} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: kotlin.sequences.SequenceScope} */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01bf A[SYNTHETIC] */
    public final java.lang.Object invokeSuspend(java.lang.Object r18) {
        /*
            r17 = this;
            r0 = r17
            java.lang.Object r1 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r2 = r0.label
            r3 = 5
            r4 = 4
            r5 = 3
            r6 = 2
            r7 = 1
            if (r2 == 0) goto L_0x00c6
            r8 = 0
            r9 = 0
            if (r2 == r7) goto L_0x009f
            if (r2 == r6) goto L_0x007f
            if (r2 == r5) goto L_0x005d
            if (r2 == r4) goto L_0x003e
            if (r2 != r3) goto L_0x0036
            r1 = r9
            r2 = r8
            r3 = r8
            r4 = r9
            java.lang.Object r5 = r0.L$1
            r4 = r5
            kotlin.collections.RingBuffer r4 = (kotlin.collections.RingBuffer) r4
            int r2 = r0.I$1
            int r3 = r0.I$0
            java.lang.Object r5 = r0.L$0
            r1 = r5
            kotlin.sequences.SequenceScope r1 = (kotlin.sequences.SequenceScope) r1
            kotlin.ResultKt.throwOnFailure(r18)
            r11 = r0
            r6 = r1
            r1 = r18
            goto L_0x0213
        L_0x0036:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r1
        L_0x003e:
            r2 = r9
            r5 = r8
            r6 = r8
            r8 = r9
            java.lang.Object r9 = r0.L$1
            r8 = r9
            kotlin.collections.RingBuffer r8 = (kotlin.collections.RingBuffer) r8
            int r5 = r0.I$1
            int r6 = r0.I$0
            java.lang.Object r9 = r0.L$0
            r2 = r9
            kotlin.sequences.SequenceScope r2 = (kotlin.sequences.SequenceScope) r2
            kotlin.ResultKt.throwOnFailure(r18)
            r11 = r0
            r10 = r5
            r9 = r8
            r8 = r6
            r6 = r2
            r2 = r1
            r1 = r18
            goto L_0x01ef
        L_0x005d:
            r2 = r9
            r6 = r8
            r10 = r8
            java.lang.Object r11 = r0.L$3
            java.util.Iterator r11 = (java.util.Iterator) r11
            java.lang.Object r6 = r0.L$2
            java.lang.Object r12 = r0.L$1
            r9 = r12
            kotlin.collections.RingBuffer r9 = (kotlin.collections.RingBuffer) r9
            int r10 = r0.I$1
            int r8 = r0.I$0
            java.lang.Object r12 = r0.L$0
            r2 = r12
            kotlin.sequences.SequenceScope r2 = (kotlin.sequences.SequenceScope) r2
            kotlin.ResultKt.throwOnFailure(r18)
            r12 = r0
            r13 = r6
            r6 = r2
            r2 = r1
            r1 = r18
            goto L_0x01b9
        L_0x007f:
            r1 = r9
            r2 = r8
            r3 = r8
            r4 = r8
            r5 = r9
            int r2 = r0.I$2
            java.lang.Object r6 = r0.L$1
            r5 = r6
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            int r3 = r0.I$1
            int r4 = r0.I$0
            java.lang.Object r6 = r0.L$0
            r1 = r6
            kotlin.sequences.SequenceScope r1 = (kotlin.sequences.SequenceScope) r1
            kotlin.ResultKt.throwOnFailure(r18)
            r11 = r0
            r8 = r4
            r4 = r2
            r2 = r1
            r1 = r18
            goto L_0x0161
        L_0x009f:
            r2 = r9
            r3 = r8
            r4 = r8
            r5 = r8
            r8 = r9
            java.lang.Object r10 = r0.L$3
            java.util.Iterator r10 = (java.util.Iterator) r10
            java.lang.Object r8 = r0.L$2
            int r3 = r0.I$2
            java.lang.Object r11 = r0.L$1
            r9 = r11
            java.util.ArrayList r9 = (java.util.ArrayList) r9
            int r4 = r0.I$1
            int r5 = r0.I$0
            java.lang.Object r11 = r0.L$0
            r2 = r11
            kotlin.sequences.SequenceScope r2 = (kotlin.sequences.SequenceScope) r2
            kotlin.ResultKt.throwOnFailure(r18)
            r12 = r0
            r11 = r10
            r10 = r9
            r9 = r4
            r4 = r3
            r3 = r1
            r1 = r18
            goto L_0x0121
        L_0x00c6:
            kotlin.ResultKt.throwOnFailure(r18)
            kotlin.sequences.SequenceScope r2 = r0.p$
            int r8 = r0.$size
            r9 = 1024(0x400, float:1.435E-42)
            int r8 = kotlin.ranges.RangesKt.coerceAtMost((int) r8, (int) r9)
            int r9 = r0.$step
            int r10 = r0.$size
            int r9 = r9 - r10
            if (r9 < 0) goto L_0x0163
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r8)
            r4 = 0
            java.util.Iterator r5 = r0.$iterator
            r11 = r0
            r10 = r5
            r5 = r3
            r3 = r1
            r1 = r18
        L_0x00e8:
            boolean r12 = r10.hasNext()
            if (r12 == 0) goto L_0x0137
            java.lang.Object r12 = r10.next()
            if (r4 <= 0) goto L_0x00f7
            int r4 = r4 + -1
            goto L_0x0136
        L_0x00f7:
            r5.add(r12)
            int r13 = r5.size()
            int r14 = r11.$size
            if (r13 != r14) goto L_0x0136
            r11.L$0 = r2
            r11.I$0 = r8
            r11.I$1 = r9
            r11.L$1 = r5
            r11.I$2 = r4
            r11.L$2 = r12
            r11.L$3 = r10
            r11.label = r7
            java.lang.Object r13 = r2.yield(r5, r11)
            if (r13 != r3) goto L_0x0119
            return r3
        L_0x0119:
            r16 = r10
            r10 = r5
            r5 = r8
            r8 = r12
            r12 = r11
            r11 = r16
        L_0x0121:
            boolean r13 = r12.$reuseBuffer
            if (r13 == 0) goto L_0x0129
            r10.clear()
            goto L_0x0131
        L_0x0129:
            java.util.ArrayList r13 = new java.util.ArrayList
            int r14 = r12.$size
            r13.<init>(r14)
            r10 = r13
        L_0x0131:
            r4 = r9
            r8 = r5
            r5 = r10
            r10 = r11
            r11 = r12
        L_0x0136:
            goto L_0x00e8
        L_0x0137:
            r10 = r5
            java.util.Collection r10 = (java.util.Collection) r10
            boolean r10 = r10.isEmpty()
            r7 = r7 ^ r10
            if (r7 == 0) goto L_0x0219
            boolean r7 = r11.$partialWindows
            if (r7 != 0) goto L_0x014d
            int r7 = r5.size()
            int r10 = r11.$size
            if (r7 != r10) goto L_0x0219
        L_0x014d:
            r11.L$0 = r2
            r11.I$0 = r8
            r11.I$1 = r9
            r11.L$1 = r5
            r11.I$2 = r4
            r11.label = r6
            java.lang.Object r6 = r2.yield(r5, r11)
            if (r6 != r3) goto L_0x0160
            return r3
        L_0x0160:
            r3 = r9
        L_0x0161:
            goto L_0x0219
        L_0x0163:
            kotlin.collections.RingBuffer r6 = new kotlin.collections.RingBuffer
            r6.<init>(r8)
            java.util.Iterator r10 = r0.$iterator
            r12 = r0
            r11 = r10
            r10 = r9
            r9 = r6
            r6 = r2
            r2 = r1
            r1 = r18
        L_0x0172:
            boolean r13 = r11.hasNext()
            if (r13 == 0) goto L_0x01bf
            java.lang.Object r13 = r11.next()
            r9.add(r13)
            boolean r14 = r9.isFull()
            if (r14 == 0) goto L_0x01be
            int r14 = r9.size()
            int r15 = r12.$size
            if (r14 >= r15) goto L_0x0192
            kotlin.collections.RingBuffer r9 = r9.expanded(r15)
            goto L_0x01be
        L_0x0192:
            boolean r14 = r12.$reuseBuffer
            if (r14 == 0) goto L_0x019a
            r14 = r9
            java.util.List r14 = (java.util.List) r14
            goto L_0x01a4
        L_0x019a:
            java.util.ArrayList r14 = new java.util.ArrayList
            r15 = r9
            java.util.Collection r15 = (java.util.Collection) r15
            r14.<init>(r15)
            java.util.List r14 = (java.util.List) r14
        L_0x01a4:
            r12.L$0 = r6
            r12.I$0 = r8
            r12.I$1 = r10
            r12.L$1 = r9
            r12.L$2 = r13
            r12.L$3 = r11
            r12.label = r5
            java.lang.Object r14 = r6.yield(r14, r12)
            if (r14 != r2) goto L_0x01b9
            return r2
        L_0x01b9:
            int r14 = r12.$step
            r9.removeFirst(r14)
        L_0x01be:
            goto L_0x0172
        L_0x01bf:
            boolean r5 = r12.$partialWindows
            if (r5 == 0) goto L_0x0217
            r11 = r12
        L_0x01c4:
            int r5 = r9.size()
            int r12 = r11.$step
            if (r5 <= r12) goto L_0x01f5
            boolean r5 = r11.$reuseBuffer
            if (r5 == 0) goto L_0x01d4
            r5 = r9
            java.util.List r5 = (java.util.List) r5
            goto L_0x01de
        L_0x01d4:
            java.util.ArrayList r5 = new java.util.ArrayList
            r12 = r9
            java.util.Collection r12 = (java.util.Collection) r12
            r5.<init>(r12)
            java.util.List r5 = (java.util.List) r5
        L_0x01de:
            r11.L$0 = r6
            r11.I$0 = r8
            r11.I$1 = r10
            r11.L$1 = r9
            r11.label = r4
            java.lang.Object r5 = r6.yield(r5, r11)
            if (r5 != r2) goto L_0x01ef
            return r2
        L_0x01ef:
            int r5 = r11.$step
            r9.removeFirst(r5)
            goto L_0x01c4
        L_0x01f5:
            r4 = r9
            java.util.Collection r4 = (java.util.Collection) r4
            boolean r4 = r4.isEmpty()
            r4 = r4 ^ r7
            if (r4 == 0) goto L_0x0215
            r11.L$0 = r6
            r11.I$0 = r8
            r11.I$1 = r10
            r11.L$1 = r9
            r11.label = r3
            java.lang.Object r3 = r6.yield(r9, r11)
            if (r3 != r2) goto L_0x0210
            return r2
        L_0x0210:
            r3 = r8
            r4 = r9
            r2 = r10
        L_0x0213:
            r2 = r6
            goto L_0x0219
        L_0x0215:
            r2 = r6
            goto L_0x0219
        L_0x0217:
            r2 = r6
            r11 = r12
        L_0x0219:
            kotlin.Unit r3 = kotlin.Unit.INSTANCE
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt$windowedIterator$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
