package com.scwang.smartrefresh.layout.constant;

public class SpinnerStyle {
    public static final SpinnerStyle FixedBehind = new SpinnerStyle(2, false, false);
    public static final SpinnerStyle FixedFront = new SpinnerStyle(3, true, false);
    public static final SpinnerStyle MatchLayout;
    @Deprecated
    public static final SpinnerStyle Scale = new SpinnerStyle(1, true, true);
    public static final SpinnerStyle Translate = new SpinnerStyle(0, true, false);
    public static final SpinnerStyle[] values;
    public final boolean front;
    public final int ordinal;
    public final boolean scale;

    static {
        SpinnerStyle spinnerStyle = new SpinnerStyle(4, true, false);
        MatchLayout = spinnerStyle;
        values = new SpinnerStyle[]{Translate, Scale, FixedBehind, FixedFront, spinnerStyle};
    }

    private SpinnerStyle(int ordinal2, boolean front2, boolean scale2) {
        this.ordinal = ordinal2;
        this.front = front2;
        this.scale = scale2;
    }
}
