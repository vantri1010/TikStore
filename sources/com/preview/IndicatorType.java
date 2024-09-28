package com.preview;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface IndicatorType {
    public static final int DOT = 0;
    public static final int NONE = -1;
    public static final int TEXT = 1;
}
