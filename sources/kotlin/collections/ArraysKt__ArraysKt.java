package kotlin.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShortArray;
import kotlin.collections.unsigned.UArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a1\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\u000e\u0010\u0004\u001a\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\u0001¢\u0006\u0004\b\u0005\u0010\u0006\u001a!\u0010\u0007\u001a\u00020\b\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u0003H\u0001¢\u0006\u0004\b\t\u0010\n\u001a?\u0010\u000b\u001a\u00020\f\"\u0004\b\u0000\u0010\u0002*\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00032\n\u0010\r\u001a\u00060\u000ej\u0002`\u000f2\u0010\u0010\u0010\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00030\u0011H\u0002¢\u0006\u0004\b\u0012\u0010\u0013\u001a+\u0010\u0014\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0015\"\u0004\b\u0000\u0010\u0002*\u0012\u0012\u000e\b\u0001\u0012\n\u0012\u0006\b\u0001\u0012\u0002H\u00020\u00030\u0003¢\u0006\u0002\u0010\u0016\u001a8\u0010\u0017\u001a\u0002H\u0018\"\u0010\b\u0000\u0010\u0019*\u0006\u0012\u0002\b\u00030\u0003*\u0002H\u0018\"\u0004\b\u0001\u0010\u0018*\u0002H\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u0002H\u00180\u001bH\b¢\u0006\u0002\u0010\u001c\u001a)\u0010\u001d\u001a\u00020\u0001*\b\u0012\u0002\b\u0003\u0018\u00010\u0003H\b\u0002\u000e\n\f\b\u0000\u0012\u0002\u0018\u0001\u001a\u0004\b\u0003\u0010\u0000¢\u0006\u0002\u0010\u001e\u001aG\u0010\u001f\u001a\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0015\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00180\u00150 \"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0018*\u0016\u0012\u0012\b\u0001\u0012\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00180 0\u0003¢\u0006\u0002\u0010!¨\u0006\""}, d2 = {"contentDeepEqualsImpl", "", "T", "", "other", "contentDeepEquals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z", "contentDeepToStringImpl", "", "contentDeepToString", "([Ljava/lang/Object;)Ljava/lang/String;", "contentDeepToStringInternal", "", "result", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "processed", "", "contentDeepToStringInternal$ArraysKt__ArraysKt", "([Ljava/lang/Object;Ljava/lang/StringBuilder;Ljava/util/List;)V", "flatten", "", "([[Ljava/lang/Object;)Ljava/util/List;", "ifEmpty", "R", "C", "defaultValue", "Lkotlin/Function0;", "([Ljava/lang/Object;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "isNullOrEmpty", "([Ljava/lang/Object;)Z", "unzip", "Lkotlin/Pair;", "([Lkotlin/Pair;)Lkotlin/Pair;", "kotlin-stdlib"}, k = 5, mv = {1, 1, 15}, xi = 1, xs = "kotlin/collections/ArraysKt")
/* compiled from: Arrays.kt */
class ArraysKt__ArraysKt extends ArraysKt__ArraysJVMKt {
    public static final <T> List<T> flatten(T[][] $this$flatten) {
        Intrinsics.checkParameterIsNotNull($this$flatten, "$this$flatten");
        int sum$iv = 0;
        for (Object element$iv : (Object[]) $this$flatten) {
            sum$iv += ((Object[]) element$iv).length;
        }
        ArrayList result = new ArrayList(sum$iv);
        for (Object[] element : $this$flatten) {
            CollectionsKt.addAll(result, (T[]) element);
        }
        return result;
    }

    public static final <T, R> Pair<List<T>, List<R>> unzip(Pair<? extends T, ? extends R>[] $this$unzip) {
        Intrinsics.checkParameterIsNotNull($this$unzip, "$this$unzip");
        ArrayList listT = new ArrayList($this$unzip.length);
        ArrayList listR = new ArrayList($this$unzip.length);
        for (Pair pair : $this$unzip) {
            listT.add(pair.getFirst());
            listR.add(pair.getSecond());
        }
        return TuplesKt.to(listT, listR);
    }

    private static final boolean isNullOrEmpty(Object[] $this$isNullOrEmpty) {
        if ($this$isNullOrEmpty != null) {
            return $this$isNullOrEmpty.length == 0;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [C] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final <C extends java.lang.Object & R, R> R ifEmpty(C r2, kotlin.jvm.functions.Function0<? extends R> r3) {
        /*
            r0 = 0
            int r1 = r2.length
            if (r1 != 0) goto L_0x0006
            r1 = 1
            goto L_0x0007
        L_0x0006:
            r1 = 0
        L_0x0007:
            if (r1 == 0) goto L_0x000e
            java.lang.Object r1 = r3.invoke()
            goto L_0x000f
        L_0x000e:
            r1 = r2
        L_0x000f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.ArraysKt__ArraysKt.ifEmpty(java.lang.Object[], kotlin.jvm.functions.Function0):java.lang.Object");
    }

    public static final <T> boolean contentDeepEquals(T[] $this$contentDeepEqualsImpl, T[] other) {
        Intrinsics.checkParameterIsNotNull($this$contentDeepEqualsImpl, "$this$contentDeepEqualsImpl");
        Intrinsics.checkParameterIsNotNull(other, "other");
        if ($this$contentDeepEqualsImpl == other) {
            return true;
        }
        if ($this$contentDeepEqualsImpl.length != other.length) {
            return false;
        }
        int length = $this$contentDeepEqualsImpl.length;
        for (int i = 0; i < length; i++) {
            Object v1 = $this$contentDeepEqualsImpl[i];
            Object v2 = other[i];
            if (v1 != v2) {
                if (v1 == null || v2 == null) {
                    return false;
                }
                if (!(v1 instanceof Object[]) || !(v2 instanceof Object[])) {
                    if (!(v1 instanceof byte[]) || !(v2 instanceof byte[])) {
                        if (!(v1 instanceof short[]) || !(v2 instanceof short[])) {
                            if (!(v1 instanceof int[]) || !(v2 instanceof int[])) {
                                if (!(v1 instanceof long[]) || !(v2 instanceof long[])) {
                                    if (!(v1 instanceof float[]) || !(v2 instanceof float[])) {
                                        if (!(v1 instanceof double[]) || !(v2 instanceof double[])) {
                                            if (!(v1 instanceof char[]) || !(v2 instanceof char[])) {
                                                if (!(v1 instanceof boolean[]) || !(v2 instanceof boolean[])) {
                                                    if (!(v1 instanceof UByteArray) || !(v2 instanceof UByteArray)) {
                                                        if (!(v1 instanceof UShortArray) || !(v2 instanceof UShortArray)) {
                                                            if (!(v1 instanceof UIntArray) || !(v2 instanceof UIntArray)) {
                                                                if (!(v1 instanceof ULongArray) || !(v2 instanceof ULongArray)) {
                                                                    if (!Intrinsics.areEqual(v1, v2)) {
                                                                        return false;
                                                                    }
                                                                } else if (!UArraysKt.m427contentEqualsus8wMrg(((ULongArray) v1).m223unboximpl(), ((ULongArray) v2).m223unboximpl())) {
                                                                    return false;
                                                                }
                                                            } else if (!UArraysKt.m424contentEqualsctEhBpI(((UIntArray) v1).m154unboximpl(), ((UIntArray) v2).m154unboximpl())) {
                                                                return false;
                                                            }
                                                        } else if (!UArraysKt.m426contentEqualsmazbYpA(((UShortArray) v1).m318unboximpl(), ((UShortArray) v2).m318unboximpl())) {
                                                            return false;
                                                        }
                                                    } else if (!UArraysKt.m425contentEqualskdPth3s(((UByteArray) v1).m85unboximpl(), ((UByteArray) v2).m85unboximpl())) {
                                                        return false;
                                                    }
                                                } else if (!Arrays.equals((boolean[]) v1, (boolean[]) v2)) {
                                                    return false;
                                                }
                                            } else if (!Arrays.equals((char[]) v1, (char[]) v2)) {
                                                return false;
                                            }
                                        } else if (!Arrays.equals((double[]) v1, (double[]) v2)) {
                                            return false;
                                        }
                                    } else if (!Arrays.equals((float[]) v1, (float[]) v2)) {
                                        return false;
                                    }
                                } else if (!Arrays.equals((long[]) v1, (long[]) v2)) {
                                    return false;
                                }
                            } else if (!Arrays.equals((int[]) v1, (int[]) v2)) {
                                return false;
                            }
                        } else if (!Arrays.equals((short[]) v1, (short[]) v2)) {
                            return false;
                        }
                    } else if (!Arrays.equals((byte[]) v1, (byte[]) v2)) {
                        return false;
                    }
                } else if (!ArraysKt.contentDeepEquals((Object[]) v1, (Object[]) v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static final <T> String contentDeepToString(T[] $this$contentDeepToStringImpl) {
        Intrinsics.checkParameterIsNotNull($this$contentDeepToStringImpl, "$this$contentDeepToStringImpl");
        StringBuilder $this$buildString = new StringBuilder((RangesKt.coerceAtMost($this$contentDeepToStringImpl.length, 429496729) * 5) + 2);
        contentDeepToStringInternal$ArraysKt__ArraysKt($this$contentDeepToStringImpl, $this$buildString, new ArrayList());
        String sb = $this$buildString.toString();
        Intrinsics.checkExpressionValueIsNotNull(sb, "StringBuilder(capacity).…builderAction).toString()");
        return sb;
    }

    private static final <T> void contentDeepToStringInternal$ArraysKt__ArraysKt(T[] $this$contentDeepToStringInternal, StringBuilder result, List<Object[]> processed) {
        if (processed.contains($this$contentDeepToStringInternal)) {
            result.append("[...]");
            return;
        }
        processed.add($this$contentDeepToStringInternal);
        result.append('[');
        int length = $this$contentDeepToStringInternal.length;
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                result.append(", ");
            }
            Object element = $this$contentDeepToStringInternal[i];
            if (element == null) {
                result.append("null");
            } else if (element instanceof Object[]) {
                contentDeepToStringInternal$ArraysKt__ArraysKt((Object[]) element, result, processed);
            } else if (element instanceof byte[]) {
                String arrays = Arrays.toString((byte[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays, "java.util.Arrays.toString(this)");
                result.append(arrays);
            } else if (element instanceof short[]) {
                String arrays2 = Arrays.toString((short[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays2, "java.util.Arrays.toString(this)");
                result.append(arrays2);
            } else if (element instanceof int[]) {
                String arrays3 = Arrays.toString((int[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays3, "java.util.Arrays.toString(this)");
                result.append(arrays3);
            } else if (element instanceof long[]) {
                String arrays4 = Arrays.toString((long[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays4, "java.util.Arrays.toString(this)");
                result.append(arrays4);
            } else if (element instanceof float[]) {
                String arrays5 = Arrays.toString((float[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays5, "java.util.Arrays.toString(this)");
                result.append(arrays5);
            } else if (element instanceof double[]) {
                String arrays6 = Arrays.toString((double[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays6, "java.util.Arrays.toString(this)");
                result.append(arrays6);
            } else if (element instanceof char[]) {
                String arrays7 = Arrays.toString((char[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays7, "java.util.Arrays.toString(this)");
                result.append(arrays7);
            } else if (element instanceof boolean[]) {
                String arrays8 = Arrays.toString((boolean[]) element);
                Intrinsics.checkExpressionValueIsNotNull(arrays8, "java.util.Arrays.toString(this)");
                result.append(arrays8);
            } else if (element instanceof UByteArray) {
                result.append(UArraysKt.m433contentToStringGBYM_sE(((UByteArray) element).m85unboximpl()));
            } else if (element instanceof UShortArray) {
                result.append(UArraysKt.m435contentToStringrL5Bavg(((UShortArray) element).m318unboximpl()));
            } else if (element instanceof UIntArray) {
                result.append(UArraysKt.m432contentToStringajY9A(((UIntArray) element).m154unboximpl()));
            } else if (element instanceof ULongArray) {
                result.append(UArraysKt.m434contentToStringQwZRm1k(((ULongArray) element).m223unboximpl()));
            } else {
                result.append(element.toString());
            }
        }
        result.append(']');
        processed.remove(CollectionsKt.getLastIndex(processed));
    }
}
