package org.webrtc.ali;

import java.util.LinkedList;
import java.util.List;

public class MediaConstraints {
    public final List<KeyValuePair> mandatory = new LinkedList();
    public final List<KeyValuePair> optional = new LinkedList();

    public static class KeyValuePair {
        private final String key;
        private final String value;

        public KeyValuePair(String key2, String value2) {
            this.key = key2;
            this.value = value2;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.key + ": " + this.value;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            KeyValuePair that = (KeyValuePair) other;
            if (!this.key.equals(that.key) || !this.value.equals(that.value)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return this.key.hashCode() + this.value.hashCode();
        }
    }

    private static String stringifyKeyValuePairList(List<KeyValuePair> list) {
        StringBuilder builder = new StringBuilder("[");
        for (KeyValuePair pair : list) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(pair.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    public String toString() {
        return "mandatory: " + stringifyKeyValuePairList(this.mandatory) + ", optional: " + stringifyKeyValuePairList(this.optional);
    }
}
