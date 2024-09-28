package kotlin.time;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import im.bclpbkiauv.ui.hui.adapter.EditInputFilter;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.DoubleCompanionObject;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b&\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0012\b@\u0018\u0000 s2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001sB\u0014\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003ø\u0001\u0000¢\u0006\u0004\b\u0004\u0010\u0005J\u001b\u0010%\u001a\u00020\t2\u0006\u0010&\u001a\u00020\u0000H\u0002ø\u0001\u0000¢\u0006\u0004\b'\u0010(J\u001b\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\u0003H\u0002ø\u0001\u0000¢\u0006\u0004\b+\u0010,J\u001b\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\tH\u0002ø\u0001\u0000¢\u0006\u0004\b+\u0010-J\u001b\u0010)\u001a\u00020\u00032\u0006\u0010&\u001a\u00020\u0000H\u0002ø\u0001\u0000¢\u0006\u0004\b.\u0010,J\u0013\u0010/\u001a\u0002002\b\u0010&\u001a\u0004\u0018\u000101HÖ\u0003J\t\u00102\u001a\u00020\tHÖ\u0001J\r\u00103\u001a\u000200¢\u0006\u0004\b4\u00105J\r\u00106\u001a\u000200¢\u0006\u0004\b7\u00105J\r\u00108\u001a\u000200¢\u0006\u0004\b9\u00105J\r\u0010:\u001a\u000200¢\u0006\u0004\b;\u00105J\u001b\u0010<\u001a\u00020\u00002\u0006\u0010&\u001a\u00020\u0000H\u0002ø\u0001\u0000¢\u0006\u0004\b=\u0010,J\u001b\u0010>\u001a\u00020\u00002\u0006\u0010&\u001a\u00020\u0000H\u0002ø\u0001\u0000¢\u0006\u0004\b?\u0010,J\u0017\u0010@\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002¢\u0006\u0004\bA\u0010(J\u001b\u0010B\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\u0003H\u0002ø\u0001\u0000¢\u0006\u0004\bC\u0010,J\u001b\u0010B\u001a\u00020\u00002\u0006\u0010*\u001a\u00020\tH\u0002ø\u0001\u0000¢\u0006\u0004\bC\u0010-J\u0001\u0010D\u001a\u0002HE\"\u0004\b\u0000\u0010E2u\u0010F\u001aq\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(J\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(K\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(L\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(M\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(N\u0012\u0004\u0012\u0002HE0GH\b¢\u0006\u0004\bO\u0010PJx\u0010D\u001a\u0002HE\"\u0004\b\u0000\u0010E2`\u0010F\u001a\\\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(K\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(L\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(M\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(N\u0012\u0004\u0012\u0002HE0QH\b¢\u0006\u0004\bO\u0010RJc\u0010D\u001a\u0002HE\"\u0004\b\u0000\u0010E2K\u0010F\u001aG\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(L\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(M\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(N\u0012\u0004\u0012\u0002HE0SH\b¢\u0006\u0004\bO\u0010TJN\u0010D\u001a\u0002HE\"\u0004\b\u0000\u0010E26\u0010F\u001a2\u0012\u0013\u0012\u00110V¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(M\u0012\u0013\u0012\u00110\t¢\u0006\f\bH\u0012\b\bI\u0012\u0004\b\b(N\u0012\u0004\u0012\u0002HE0UH\b¢\u0006\u0004\bO\u0010WJ\u0019\u0010X\u001a\u00020\u00032\n\u0010Y\u001a\u00060Zj\u0002`[¢\u0006\u0004\b\\\u0010]J\u0019\u0010^\u001a\u00020\t2\n\u0010Y\u001a\u00060Zj\u0002`[¢\u0006\u0004\b_\u0010`J\r\u0010a\u001a\u00020b¢\u0006\u0004\bc\u0010dJ\u0019\u0010e\u001a\u00020V2\n\u0010Y\u001a\u00060Zj\u0002`[¢\u0006\u0004\bf\u0010gJ\r\u0010h\u001a\u00020V¢\u0006\u0004\bi\u0010jJ\r\u0010k\u001a\u00020V¢\u0006\u0004\bl\u0010jJ\u000f\u0010m\u001a\u00020bH\u0016¢\u0006\u0004\bn\u0010dJ#\u0010m\u001a\u00020b2\n\u0010Y\u001a\u00060Zj\u0002`[2\b\b\u0002\u0010o\u001a\u00020\t¢\u0006\u0004\bn\u0010pJ\u0013\u0010q\u001a\u00020\u0000H\u0002ø\u0001\u0000¢\u0006\u0004\br\u0010\u0005R\u0014\u0010\u0006\u001a\u00020\u00008Fø\u0001\u0000¢\u0006\u0006\u001a\u0004\b\u0007\u0010\u0005R\u001a\u0010\b\u001a\u00020\t8@X\u0004¢\u0006\f\u0012\u0004\b\n\u0010\u000b\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0005R\u0011\u0010\u0010\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0005R\u0011\u0010\u0012\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0005R\u0011\u0010\u0014\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0005R\u0011\u0010\u0016\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0005R\u0011\u0010\u0018\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0005R\u0011\u0010\u001a\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0005R\u001a\u0010\u001c\u001a\u00020\t8@X\u0004¢\u0006\f\u0012\u0004\b\u001d\u0010\u000b\u001a\u0004\b\u001e\u0010\rR\u001a\u0010\u001f\u001a\u00020\t8@X\u0004¢\u0006\f\u0012\u0004\b \u0010\u000b\u001a\u0004\b!\u0010\rR\u001a\u0010\"\u001a\u00020\t8@X\u0004¢\u0006\f\u0012\u0004\b#\u0010\u000b\u001a\u0004\b$\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000ø\u0001\u0000\u0002\u0004\n\u0002\b\u0019¨\u0006t"}, d2 = {"Lkotlin/time/Duration;", "", "value", "", "constructor-impl", "(D)D", "absoluteValue", "getAbsoluteValue-impl", "hoursComponent", "", "hoursComponent$annotations", "()V", "getHoursComponent-impl", "(D)I", "inDays", "getInDays-impl", "inHours", "getInHours-impl", "inMicroseconds", "getInMicroseconds-impl", "inMilliseconds", "getInMilliseconds-impl", "inMinutes", "getInMinutes-impl", "inNanoseconds", "getInNanoseconds-impl", "inSeconds", "getInSeconds-impl", "minutesComponent", "minutesComponent$annotations", "getMinutesComponent-impl", "nanosecondsComponent", "nanosecondsComponent$annotations", "getNanosecondsComponent-impl", "secondsComponent", "secondsComponent$annotations", "getSecondsComponent-impl", "compareTo", "other", "compareTo-LRDsOJo", "(DD)I", "div", "scale", "div-impl", "(DD)D", "(DI)D", "div-LRDsOJo", "equals", "", "", "hashCode", "isFinite", "isFinite-impl", "(D)Z", "isInfinite", "isInfinite-impl", "isNegative", "isNegative-impl", "isPositive", "isPositive-impl", "minus", "minus-LRDsOJo", "plus", "plus-LRDsOJo", "precision", "precision-impl", "times", "times-impl", "toComponents", "T", "action", "Lkotlin/Function5;", "Lkotlin/ParameterName;", "name", "days", "hours", "minutes", "seconds", "nanoseconds", "toComponents-impl", "(DLkotlin/jvm/functions/Function5;)Ljava/lang/Object;", "Lkotlin/Function4;", "(DLkotlin/jvm/functions/Function4;)Ljava/lang/Object;", "Lkotlin/Function3;", "(DLkotlin/jvm/functions/Function3;)Ljava/lang/Object;", "Lkotlin/Function2;", "", "(DLkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "toDouble", "unit", "Ljava/util/concurrent/TimeUnit;", "Lkotlin/time/DurationUnit;", "toDouble-impl", "(DLjava/util/concurrent/TimeUnit;)D", "toInt", "toInt-impl", "(DLjava/util/concurrent/TimeUnit;)I", "toIsoString", "", "toIsoString-impl", "(D)Ljava/lang/String;", "toLong", "toLong-impl", "(DLjava/util/concurrent/TimeUnit;)J", "toLongMilliseconds", "toLongMilliseconds-impl", "(D)J", "toLongNanoseconds", "toLongNanoseconds-impl", "toString", "toString-impl", "decimals", "(DLjava/util/concurrent/TimeUnit;I)Ljava/lang/String;", "unaryMinus", "unaryMinus-impl", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 1, 15})
/* compiled from: Duration.kt */
public final class Duration implements Comparable<Duration> {
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    /* access modifiers changed from: private */
    public static final double INFINITE = m934constructorimpl(DoubleCompanionObject.INSTANCE.getPOSITIVE_INFINITY());
    /* access modifiers changed from: private */
    public static final double ZERO = m934constructorimpl(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    private final double value;

    /* renamed from: box-impl  reason: not valid java name */
    public static final /* synthetic */ Duration m932boximpl(double d) {
        return new Duration(d);
    }

    /* renamed from: equals-impl  reason: not valid java name */
    public static boolean m938equalsimpl(double d, Object obj) {
        return (obj instanceof Duration) && Double.compare(d, ((Duration) obj).m977unboximpl()) == 0;
    }

    /* renamed from: equals-impl0  reason: not valid java name */
    public static final boolean m939equalsimpl0(double d, double d2) {
        throw null;
    }

    /* renamed from: hashCode-impl  reason: not valid java name */
    public static int m952hashCodeimpl(double d) {
        long doubleToLongBits = Double.doubleToLongBits(d);
        return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
    }

    public static /* synthetic */ void hoursComponent$annotations() {
    }

    public static /* synthetic */ void minutesComponent$annotations() {
    }

    public static /* synthetic */ void nanosecondsComponent$annotations() {
    }

    public static /* synthetic */ void secondsComponent$annotations() {
    }

    /* renamed from: compareTo-LRDsOJo  reason: not valid java name */
    public int m976compareToLRDsOJo(double d) {
        return m933compareToLRDsOJo(this.value, d);
    }

    public boolean equals(Object obj) {
        return m938equalsimpl(this.value, obj);
    }

    public int hashCode() {
        return m952hashCodeimpl(this.value);
    }

    public String toString() {
        return m972toStringimpl(this.value);
    }

    /* renamed from: unbox-impl  reason: not valid java name */
    public final /* synthetic */ double m977unboximpl() {
        return this.value;
    }

    private /* synthetic */ Duration(double value2) {
        this.value = value2;
    }

    /* renamed from: constructor-impl  reason: not valid java name */
    public static double m934constructorimpl(double value2) {
        return value2;
    }

    public /* bridge */ /* synthetic */ int compareTo(Object obj) {
        return m976compareToLRDsOJo(((Duration) obj).m977unboximpl());
    }

    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J&\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\n\u0010\r\u001a\u00060\u000ej\u0002`\u000f2\n\u0010\u0010\u001a\u00060\u000ej\u0002`\u000fR\u0016\u0010\u0003\u001a\u00020\u0004ø\u0001\u0000¢\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\u0005\u0010\u0006R\u0016\u0010\b\u001a\u00020\u0004ø\u0001\u0000¢\u0006\n\n\u0002\u0010\u0007\u001a\u0004\b\t\u0010\u0006\u0002\u0004\n\u0002\b\u0019¨\u0006\u0011"}, d2 = {"Lkotlin/time/Duration$Companion;", "", "()V", "INFINITE", "Lkotlin/time/Duration;", "getINFINITE", "()D", "D", "ZERO", "getZERO", "convert", "", "value", "sourceUnit", "Ljava/util/concurrent/TimeUnit;", "Lkotlin/time/DurationUnit;", "targetUnit", "kotlin-stdlib"}, k = 1, mv = {1, 1, 15})
    /* compiled from: Duration.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        public final double getZERO() {
            return Duration.ZERO;
        }

        public final double getINFINITE() {
            return Duration.INFINITE;
        }

        public final double convert(double value, TimeUnit sourceUnit, TimeUnit targetUnit) {
            Intrinsics.checkParameterIsNotNull(sourceUnit, "sourceUnit");
            Intrinsics.checkParameterIsNotNull(targetUnit, "targetUnit");
            return DurationUnitKt.convertDurationUnit(value, sourceUnit, targetUnit);
        }
    }

    /* renamed from: unaryMinus-impl  reason: not valid java name */
    public static final double m975unaryMinusimpl(double $this) {
        return m934constructorimpl(-$this);
    }

    /* renamed from: plus-LRDsOJo  reason: not valid java name */
    public static final double m958plusLRDsOJo(double $this, double other) {
        return m934constructorimpl($this + other);
    }

    /* renamed from: minus-LRDsOJo  reason: not valid java name */
    public static final double m957minusLRDsOJo(double $this, double other) {
        return m934constructorimpl($this - other);
    }

    /* renamed from: times-impl  reason: not valid java name */
    public static final double m961timesimpl(double $this, int scale) {
        return m934constructorimpl(((double) scale) * $this);
    }

    /* renamed from: times-impl  reason: not valid java name */
    public static final double m960timesimpl(double $this, double scale) {
        return m934constructorimpl($this * scale);
    }

    /* renamed from: div-impl  reason: not valid java name */
    public static final double m937divimpl(double $this, int scale) {
        return m934constructorimpl($this / ((double) scale));
    }

    /* renamed from: div-impl  reason: not valid java name */
    public static final double m936divimpl(double $this, double scale) {
        return m934constructorimpl($this / scale);
    }

    /* renamed from: div-LRDsOJo  reason: not valid java name */
    public static final double m935divLRDsOJo(double $this, double other) {
        return $this / other;
    }

    /* renamed from: isNegative-impl  reason: not valid java name */
    public static final boolean m955isNegativeimpl(double $this) {
        return $this < ((double) 0);
    }

    /* renamed from: isPositive-impl  reason: not valid java name */
    public static final boolean m956isPositiveimpl(double $this) {
        return $this > ((double) 0);
    }

    /* renamed from: isInfinite-impl  reason: not valid java name */
    public static final boolean m954isInfiniteimpl(double $this) {
        return Double.isInfinite($this);
    }

    /* renamed from: isFinite-impl  reason: not valid java name */
    public static final boolean m953isFiniteimpl(double $this) {
        return !Double.isInfinite($this) && !Double.isNaN($this);
    }

    /* renamed from: getAbsoluteValue-impl  reason: not valid java name */
    public static final double m940getAbsoluteValueimpl(double $this) {
        return m955isNegativeimpl($this) ? m975unaryMinusimpl($this) : $this;
    }

    /* renamed from: compareTo-LRDsOJo  reason: not valid java name */
    public static int m933compareToLRDsOJo(double $this, double other) {
        return Double.compare($this, other);
    }

    /* renamed from: toComponents-impl  reason: not valid java name */
    public static final <T> T m965toComponentsimpl(double $this, Function5<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        return action.invoke(Integer.valueOf((int) m942getInDaysimpl($this)), Integer.valueOf(m941getHoursComponentimpl($this)), Integer.valueOf(m949getMinutesComponentimpl($this)), Integer.valueOf(m951getSecondsComponentimpl($this)), Integer.valueOf(m950getNanosecondsComponentimpl($this)));
    }

    /* renamed from: toComponents-impl  reason: not valid java name */
    public static final <T> T m964toComponentsimpl(double $this, Function4<? super Integer, ? super Integer, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        return action.invoke(Integer.valueOf((int) m943getInHoursimpl($this)), Integer.valueOf(m949getMinutesComponentimpl($this)), Integer.valueOf(m951getSecondsComponentimpl($this)), Integer.valueOf(m950getNanosecondsComponentimpl($this)));
    }

    /* renamed from: toComponents-impl  reason: not valid java name */
    public static final <T> T m963toComponentsimpl(double $this, Function3<? super Integer, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        return action.invoke(Integer.valueOf((int) m946getInMinutesimpl($this)), Integer.valueOf(m951getSecondsComponentimpl($this)), Integer.valueOf(m950getNanosecondsComponentimpl($this)));
    }

    /* renamed from: toComponents-impl  reason: not valid java name */
    public static final <T> T m962toComponentsimpl(double $this, Function2<? super Long, ? super Integer, ? extends T> action) {
        Intrinsics.checkParameterIsNotNull(action, "action");
        return action.invoke(Long.valueOf((long) m948getInSecondsimpl($this)), Integer.valueOf(m950getNanosecondsComponentimpl($this)));
    }

    /* renamed from: getHoursComponent-impl  reason: not valid java name */
    public static final int m941getHoursComponentimpl(double $this) {
        return (int) (m943getInHoursimpl($this) % ((double) 24));
    }

    /* renamed from: getMinutesComponent-impl  reason: not valid java name */
    public static final int m949getMinutesComponentimpl(double $this) {
        return (int) (m946getInMinutesimpl($this) % ((double) 60));
    }

    /* renamed from: getSecondsComponent-impl  reason: not valid java name */
    public static final int m951getSecondsComponentimpl(double $this) {
        return (int) (m948getInSecondsimpl($this) % ((double) 60));
    }

    /* renamed from: getNanosecondsComponent-impl  reason: not valid java name */
    public static final int m950getNanosecondsComponentimpl(double $this) {
        return (int) (m947getInNanosecondsimpl($this) % 1.0E9d);
    }

    /* renamed from: toDouble-impl  reason: not valid java name */
    public static final double m966toDoubleimpl(double $this, TimeUnit unit) {
        Intrinsics.checkParameterIsNotNull(unit, "unit");
        return DurationUnitKt.convertDurationUnit($this, DurationKt.getStorageUnit(), unit);
    }

    /* renamed from: toLong-impl  reason: not valid java name */
    public static final long m969toLongimpl(double $this, TimeUnit unit) {
        Intrinsics.checkParameterIsNotNull(unit, "unit");
        return (long) m966toDoubleimpl($this, unit);
    }

    /* renamed from: toInt-impl  reason: not valid java name */
    public static final int m967toIntimpl(double $this, TimeUnit unit) {
        Intrinsics.checkParameterIsNotNull(unit, "unit");
        return (int) m966toDoubleimpl($this, unit);
    }

    /* renamed from: getInDays-impl  reason: not valid java name */
    public static final double m942getInDaysimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.DAYS);
    }

    /* renamed from: getInHours-impl  reason: not valid java name */
    public static final double m943getInHoursimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.HOURS);
    }

    /* renamed from: getInMinutes-impl  reason: not valid java name */
    public static final double m946getInMinutesimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.MINUTES);
    }

    /* renamed from: getInSeconds-impl  reason: not valid java name */
    public static final double m948getInSecondsimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.SECONDS);
    }

    /* renamed from: getInMilliseconds-impl  reason: not valid java name */
    public static final double m945getInMillisecondsimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.MILLISECONDS);
    }

    /* renamed from: getInMicroseconds-impl  reason: not valid java name */
    public static final double m944getInMicrosecondsimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.MICROSECONDS);
    }

    /* renamed from: getInNanoseconds-impl  reason: not valid java name */
    public static final double m947getInNanosecondsimpl(double $this) {
        return m966toDoubleimpl($this, TimeUnit.NANOSECONDS);
    }

    /* renamed from: toLongNanoseconds-impl  reason: not valid java name */
    public static final long m971toLongNanosecondsimpl(double $this) {
        return m969toLongimpl($this, TimeUnit.NANOSECONDS);
    }

    /* renamed from: toLongMilliseconds-impl  reason: not valid java name */
    public static final long m970toLongMillisecondsimpl(double $this) {
        return m969toLongimpl($this, TimeUnit.MILLISECONDS);
    }

    /* renamed from: toString-impl  reason: not valid java name */
    public static String m972toStringimpl(double $this) {
        TimeUnit unit;
        String str;
        if (m954isInfiniteimpl($this)) {
            return String.valueOf($this);
        }
        if ($this == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE) {
            return "0s";
        }
        double absNs = m947getInNanosecondsimpl(m940getAbsoluteValueimpl($this));
        boolean scientific = false;
        int maxDecimals = 0;
        if (absNs < 1.0E-6d) {
            unit = TimeUnit.SECONDS;
            TimeUnit timeUnit = unit;
            scientific = true;
        } else if (absNs < ((double) 1)) {
            unit = TimeUnit.NANOSECONDS;
            TimeUnit timeUnit2 = unit;
            maxDecimals = 7;
        } else if (absNs < 1000.0d) {
            unit = TimeUnit.NANOSECONDS;
        } else if (absNs < 1000000.0d) {
            unit = TimeUnit.MICROSECONDS;
        } else if (absNs < 1.0E9d) {
            unit = TimeUnit.MILLISECONDS;
        } else if (absNs < 1.0E12d) {
            unit = TimeUnit.SECONDS;
        } else if (absNs < 6.0E13d) {
            unit = TimeUnit.MINUTES;
        } else if (absNs < 3.6E15d) {
            unit = TimeUnit.HOURS;
        } else if (absNs < 8.64E20d) {
            unit = TimeUnit.DAYS;
        } else {
            unit = TimeUnit.DAYS;
            TimeUnit timeUnit3 = unit;
            scientific = true;
        }
        double value2 = m966toDoubleimpl($this, unit);
        StringBuilder sb = new StringBuilder();
        if (scientific) {
            str = FormatToDecimalsKt.formatScientific(value2);
        } else if (maxDecimals > 0) {
            str = FormatToDecimalsKt.formatUpToDecimals(value2, maxDecimals);
        } else {
            str = FormatToDecimalsKt.formatToExactDecimals(value2, m959precisionimpl($this, Math.abs(value2)));
        }
        sb.append(str);
        sb.append(DurationUnitKt.shortName(unit));
        return sb.toString();
    }

    /* renamed from: precision-impl  reason: not valid java name */
    private static final int m959precisionimpl(double $this, double value2) {
        if (value2 < ((double) 1)) {
            return 3;
        }
        if (value2 < ((double) 10)) {
            return 2;
        }
        if (value2 < ((double) 100)) {
            return 1;
        }
        return 0;
    }

    /* renamed from: toString-impl$default  reason: not valid java name */
    public static /* synthetic */ String m974toStringimpl$default(double d, TimeUnit timeUnit, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        return m973toStringimpl(d, timeUnit, i);
    }

    /* renamed from: toString-impl  reason: not valid java name */
    public static final String m973toStringimpl(double $this, TimeUnit unit, int decimals) {
        String str;
        Intrinsics.checkParameterIsNotNull(unit, "unit");
        if (!(decimals >= 0)) {
            throw new IllegalArgumentException(("decimals must be not negative, but was " + decimals).toString());
        } else if (m954isInfiniteimpl($this)) {
            return String.valueOf($this);
        } else {
            double number = m966toDoubleimpl($this, unit);
            StringBuilder sb = new StringBuilder();
            if (Math.abs(number) < 1.0E14d) {
                str = FormatToDecimalsKt.formatToExactDecimals(number, RangesKt.coerceAtMost(decimals, 12));
            } else {
                str = FormatToDecimalsKt.formatScientific(number);
            }
            sb.append(str);
            sb.append(DurationUnitKt.shortName(unit));
            return sb.toString();
        }
    }

    /* renamed from: toIsoString-impl  reason: not valid java name */
    public static final String m968toIsoStringimpl(double $this) {
        StringBuilder sb = new StringBuilder();
        StringBuilder $this$buildString = sb;
        if (m955isNegativeimpl($this)) {
            $this$buildString.append('-');
        }
        $this$buildString.append("PT");
        double $this$iv = m940getAbsoluteValueimpl($this);
        int hours = (int) m943getInHoursimpl($this$iv);
        int minutes = m949getMinutesComponentimpl($this$iv);
        int seconds = m951getSecondsComponentimpl($this$iv);
        int nanoseconds = m950getNanosecondsComponentimpl($this$iv);
        boolean hasMinutes = true;
        boolean hasHours = hours != 0;
        boolean hasSeconds = (seconds == 0 && nanoseconds == 0) ? false : true;
        if (minutes == 0 && (!hasSeconds || !hasHours)) {
            hasMinutes = false;
        }
        if (hasHours) {
            $this$buildString.append(hours);
            $this$buildString.append('H');
        }
        if (hasMinutes) {
            $this$buildString.append(minutes);
            $this$buildString.append('M');
        }
        if (hasSeconds || (!hasHours && !hasMinutes)) {
            $this$buildString.append(seconds);
            if (nanoseconds != 0) {
                $this$buildString.append('.');
                String nss = StringsKt.padStart(String.valueOf(nanoseconds), 9, '0');
                if (nanoseconds % EditInputFilter.MAX_VALUE == 0) {
                    double d = $this$iv;
                    $this$buildString.append(nss, 0, 3);
                } else {
                    if (nanoseconds % 1000 == 0) {
                        $this$buildString.append(nss, 0, 6);
                    } else {
                        $this$buildString.append(nss);
                    }
                }
            } else {
                double d2 = $this$iv;
            }
            $this$buildString.append('S');
        } else {
            double d3 = $this$iv;
        }
        String sb2 = sb.toString();
        Intrinsics.checkExpressionValueIsNotNull(sb2, "StringBuilder().apply(builderAction).toString()");
        return sb2;
    }
}
