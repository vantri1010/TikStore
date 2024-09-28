package kotlin.collections;

import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UByteArray;
import kotlin.UIntArray;
import kotlin.ULongArray;
import kotlin.UShortArray;
import kotlin.UnsignedKt;
import kotlin.jvm.internal.Intrinsics;

@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0012\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0010\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\u0014\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0019\u0010\u001a\u001a\u001a\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u0003H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001c\u0010\u001d\u001a\u001a\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\bH\u0001ø\u0001\u0000¢\u0006\u0004\b\u001e\u0010\u001f\u001a\u001a\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000bH\u0001ø\u0001\u0000¢\u0006\u0004\b \u0010!\u001a\u001a\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000eH\u0001ø\u0001\u0000¢\u0006\u0004\b\"\u0010#\u0002\u0004\n\u0002\b\u0019¨\u0006$"}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", "left", "right", "partition-4UcCI2c", "([BII)I", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "quickSort-oBK06Vg", "([III)V", "quickSort--nroSd4", "([JII)V", "quickSort-Aa5vz7o", "([SII)V", "sortArray", "sortArray-GBYM_sE", "([B)V", "sortArray--ajY-9A", "([I)V", "sortArray-QwZRm1k", "([J)V", "sortArray-rL5Bavg", "([S)V", "kotlin-stdlib"}, k = 2, mv = {1, 1, 15})
/* compiled from: UArraySorting.kt */
public final class UArraySortingKt {
    /* renamed from: partition-4UcCI2c  reason: not valid java name */
    private static final int m329partition4UcCI2c(byte[] array, int left, int right) {
        int i = left;
        int j = right;
        byte pivot = UByteArray.m76getimpl(array, (left + right) / 2);
        while (i <= j) {
            while (Intrinsics.compare((int) UByteArray.m76getimpl(array, i) & UByte.MAX_VALUE, (int) pivot & UByte.MAX_VALUE) < 0) {
                i++;
            }
            while (Intrinsics.compare((int) UByteArray.m76getimpl(array, j) & UByte.MAX_VALUE, (int) pivot & UByte.MAX_VALUE) > 0) {
                j--;
            }
            if (i <= j) {
                byte tmp = UByteArray.m76getimpl(array, i);
                UByteArray.m81setVurrAj0(array, i, UByteArray.m76getimpl(array, j));
                UByteArray.m81setVurrAj0(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-4UcCI2c  reason: not valid java name */
    private static final void m333quickSort4UcCI2c(byte[] array, int left, int right) {
        int index = m329partition4UcCI2c(array, left, right);
        if (left < index - 1) {
            m333quickSort4UcCI2c(array, left, index - 1);
        }
        if (index < right) {
            m333quickSort4UcCI2c(array, index, right);
        }
    }

    /* renamed from: partition-Aa5vz7o  reason: not valid java name */
    private static final int m330partitionAa5vz7o(short[] array, int left, int right) {
        int i = left;
        int j = right;
        short pivot = UShortArray.m309getimpl(array, (left + right) / 2);
        while (i <= j) {
            while (Intrinsics.compare((int) UShortArray.m309getimpl(array, i) & 65535, (int) pivot & 65535) < 0) {
                i++;
            }
            while (Intrinsics.compare((int) UShortArray.m309getimpl(array, j) & 65535, (int) pivot & 65535) > 0) {
                j--;
            }
            if (i <= j) {
                short tmp = UShortArray.m309getimpl(array, i);
                UShortArray.m314set01HTLdE(array, i, UShortArray.m309getimpl(array, j));
                UShortArray.m314set01HTLdE(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-Aa5vz7o  reason: not valid java name */
    private static final void m334quickSortAa5vz7o(short[] array, int left, int right) {
        int index = m330partitionAa5vz7o(array, left, right);
        if (left < index - 1) {
            m334quickSortAa5vz7o(array, left, index - 1);
        }
        if (index < right) {
            m334quickSortAa5vz7o(array, index, right);
        }
    }

    /* renamed from: partition-oBK06Vg  reason: not valid java name */
    private static final int m331partitionoBK06Vg(int[] array, int left, int right) {
        int i = left;
        int j = right;
        int pivot = UIntArray.m145getimpl(array, (left + right) / 2);
        while (i <= j) {
            while (UnsignedKt.uintCompare(UIntArray.m145getimpl(array, i), pivot) < 0) {
                i++;
            }
            while (UnsignedKt.uintCompare(UIntArray.m145getimpl(array, j), pivot) > 0) {
                j--;
            }
            if (i <= j) {
                int tmp = UIntArray.m145getimpl(array, i);
                UIntArray.m150setVXSXFK8(array, i, UIntArray.m145getimpl(array, j));
                UIntArray.m150setVXSXFK8(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-oBK06Vg  reason: not valid java name */
    private static final void m335quickSortoBK06Vg(int[] array, int left, int right) {
        int index = m331partitionoBK06Vg(array, left, right);
        if (left < index - 1) {
            m335quickSortoBK06Vg(array, left, index - 1);
        }
        if (index < right) {
            m335quickSortoBK06Vg(array, index, right);
        }
    }

    /* renamed from: partition--nroSd4  reason: not valid java name */
    private static final int m328partitionnroSd4(long[] array, int left, int right) {
        int i = left;
        int j = right;
        long pivot = ULongArray.m214getimpl(array, (left + right) / 2);
        while (i <= j) {
            while (UnsignedKt.ulongCompare(ULongArray.m214getimpl(array, i), pivot) < 0) {
                i++;
            }
            while (UnsignedKt.ulongCompare(ULongArray.m214getimpl(array, j), pivot) > 0) {
                j--;
            }
            if (i <= j) {
                long tmp = ULongArray.m214getimpl(array, i);
                ULongArray.m219setk8EXiF4(array, i, ULongArray.m214getimpl(array, j));
                ULongArray.m219setk8EXiF4(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort--nroSd4  reason: not valid java name */
    private static final void m332quickSortnroSd4(long[] array, int left, int right) {
        int index = m328partitionnroSd4(array, left, right);
        if (left < index - 1) {
            m332quickSortnroSd4(array, left, index - 1);
        }
        if (index < right) {
            m332quickSortnroSd4(array, index, right);
        }
    }

    /* renamed from: sortArray-GBYM_sE  reason: not valid java name */
    public static final void m337sortArrayGBYM_sE(byte[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        m333quickSort4UcCI2c(array, 0, UByteArray.m77getSizeimpl(array) - 1);
    }

    /* renamed from: sortArray-rL5Bavg  reason: not valid java name */
    public static final void m339sortArrayrL5Bavg(short[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        m334quickSortAa5vz7o(array, 0, UShortArray.m310getSizeimpl(array) - 1);
    }

    /* renamed from: sortArray--ajY-9A  reason: not valid java name */
    public static final void m336sortArrayajY9A(int[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        m335quickSortoBK06Vg(array, 0, UIntArray.m146getSizeimpl(array) - 1);
    }

    /* renamed from: sortArray-QwZRm1k  reason: not valid java name */
    public static final void m338sortArrayQwZRm1k(long[] array) {
        Intrinsics.checkParameterIsNotNull(array, "array");
        m332quickSortnroSd4(array, 0, ULongArray.m215getSizeimpl(array) - 1);
    }
}
