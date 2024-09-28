package com.google.firebase.remoteconfig.internal;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

/* compiled from: com.google.firebase:firebase-config@@19.1.0 */
public class FirebaseRemoteConfigValueImpl implements FirebaseRemoteConfigValue {
    private static final String ILLEGAL_ARGUMENT_STRING_FORMAT = "[Value: %s] cannot be converted to a %s.";
    private final int source;
    private final String value;

    FirebaseRemoteConfigValueImpl(String value2, int source2) {
        this.value = value2;
        this.source = source2;
    }

    public long asLong() {
        if (this.source == 0) {
            return 0;
        }
        String valueAsString = asTrimmedString();
        try {
            return Long.valueOf(valueAsString).longValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_STRING_FORMAT, new Object[]{valueAsString, "long"}), e);
        }
    }

    public double asDouble() {
        if (this.source == 0) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        }
        String valueAsString = asTrimmedString();
        try {
            return Double.valueOf(valueAsString).doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_STRING_FORMAT, new Object[]{valueAsString, "double"}), e);
        }
    }

    public String asString() {
        if (this.source == 0) {
            return "";
        }
        throwIfNullValue();
        return this.value;
    }

    public byte[] asByteArray() {
        if (this.source == 0) {
            return FirebaseRemoteConfig.DEFAULT_VALUE_FOR_BYTE_ARRAY;
        }
        return this.value.getBytes(ConfigGetParameterHandler.FRC_BYTE_ARRAY_ENCODING);
    }

    public boolean asBoolean() throws IllegalArgumentException {
        if (this.source == 0) {
            return false;
        }
        String valueAsString = asTrimmedString();
        if (ConfigGetParameterHandler.TRUE_REGEX.matcher(valueAsString).matches()) {
            return true;
        }
        if (ConfigGetParameterHandler.FALSE_REGEX.matcher(valueAsString).matches()) {
            return false;
        }
        throw new IllegalArgumentException(String.format(ILLEGAL_ARGUMENT_STRING_FORMAT, new Object[]{valueAsString, "boolean"}));
    }

    public int getSource() {
        return this.source;
    }

    private void throwIfNullValue() {
        if (this.value == null) {
            throw new IllegalArgumentException("Value is null, and cannot be converted to the desired type.");
        }
    }

    private String asTrimmedString() {
        return asString().trim();
    }
}
