package im.bclpbkiauv.ui.hui.contacts;

import im.bclpbkiauv.ui.hui.adapter.grouping.Genre;
import java.util.Comparator;

/* renamed from: im.bclpbkiauv.ui.hui.contacts.-$$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFH-RMUiRRgsw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFHRMUiRRgsw implements Comparator {
    public static final /* synthetic */ $$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFHRMUiRRgsw INSTANCE = new $$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFHRMUiRRgsw();

    private /* synthetic */ $$Lambda$MyGroupingActivity$qsS9uAJ8GqUBR4xFHRMUiRRgsw() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((Genre) obj).getOrderId(), ((Genre) obj2).getOrderId());
    }
}
