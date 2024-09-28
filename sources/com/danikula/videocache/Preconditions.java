package com.danikula.videocache;

public final class Preconditions {
    public static <T> T checkNotNull(T reference) {
        if (reference != null) {
            return reference;
        }
        throw null;
    }

    public static void checkAllNotNull(Object... references) {
        int length = references.length;
        int i = 0;
        while (i < length) {
            if (references[i] != null) {
                i++;
            } else {
                throw null;
            }
        }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference != null) {
            return reference;
        }
        throw new NullPointerException(errorMessage);
    }

    static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
