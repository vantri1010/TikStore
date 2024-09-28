package com.preview.util.notch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
public @interface CutOutMode {
    public static final int DEFAULT = 0;
    public static final int NEVER = 2;
    public static final int SHORT_EDGES = 1;
}
