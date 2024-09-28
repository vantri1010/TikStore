package com.scwang.smartrefresh.layout.constant;

public class DimensionStatus {
    public static final DimensionStatus CodeExact = new DimensionStatus(9, true);
    public static final DimensionStatus CodeExactUnNotify = new DimensionStatus(8, false);
    public static final DimensionStatus DeadLock;
    public static final DimensionStatus DeadLockUnNotify = new DimensionStatus(10, false);
    public static final DimensionStatus Default = new DimensionStatus(1, true);
    public static final DimensionStatus DefaultUnNotify = new DimensionStatus(0, false);
    public static final DimensionStatus XmlExact = new DimensionStatus(5, true);
    public static final DimensionStatus XmlExactUnNotify = new DimensionStatus(4, false);
    public static final DimensionStatus XmlLayout = new DimensionStatus(7, true);
    public static final DimensionStatus XmlLayoutUnNotify = new DimensionStatus(6, false);
    public static final DimensionStatus XmlWrap = new DimensionStatus(3, true);
    public static final DimensionStatus XmlWrapUnNotify = new DimensionStatus(2, false);
    public static final DimensionStatus[] values;
    public final boolean notified;
    public final int ordinal;

    static {
        DimensionStatus dimensionStatus = new DimensionStatus(10, true);
        DeadLock = dimensionStatus;
        values = new DimensionStatus[]{DefaultUnNotify, Default, XmlWrapUnNotify, XmlWrap, XmlExactUnNotify, XmlExact, XmlLayoutUnNotify, XmlLayout, CodeExactUnNotify, CodeExact, DeadLockUnNotify, dimensionStatus};
    }

    private DimensionStatus(int ordinal2, boolean notified2) {
        this.ordinal = ordinal2;
        this.notified = notified2;
    }

    public DimensionStatus unNotify() {
        if (!this.notified) {
            return this;
        }
        DimensionStatus prev = values[this.ordinal - 1];
        if (!prev.notified) {
            return prev;
        }
        return DefaultUnNotify;
    }

    public DimensionStatus notified() {
        if (!this.notified) {
            return values[this.ordinal + 1];
        }
        return this;
    }

    public boolean canReplaceWith(DimensionStatus status) {
        return this.ordinal < status.ordinal || ((!this.notified || CodeExact == this) && this.ordinal == status.ordinal);
    }
}
